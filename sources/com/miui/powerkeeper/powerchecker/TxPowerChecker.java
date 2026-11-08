package com.miui.powerkeeper.powerchecker;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.ServiceManager;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.telephony.ITelephony;
import com.miui.powerkeeper.cloudcontrol.CloudFunctionConfig;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.utils.Constant;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class TxPowerChecker extends Handler {
    private static final int DATA_NETWORK_TYPE_LTE_CA = 19;
    private static final int EVENT_QUERY_TX_POWER = 0;
    private static final int EVENT_QUERY_TX_POWER_DONE = 1;
    private static final int EVENT_UPDATE_CLOUD = 2;
    private static final long INTERVAL = 600000;
    private static final int MAX_TRY = 10;
    private static final int ServiceState_RIL_RADIO_TECHNOLOGY_TD_SCDMA = 17;
    private static final int TYPE_CALL = 0;
    private static final int TYPE_DATA = 1;
    private static final int TelephonyManager_NETWORK_TYPE_GSM = 16;
    private static final int TelephonyManager_NETWORK_TYPE_TD_SCDMA = 17;
    private static final int VOICE_NETWORK_TYPE_LTE_CA = 19;
    private static Class<?> clsITelephony;
    private static Method sInvokeOemRilRequestRaw;
    private Context mContext;
    private boolean mEnabled;
    private TelephonyManager mTelephonyManager;
    private Thread mTxPowerQueryThread;
    private static final String TAG = "TxPower";
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);
    private final Object mLock = new Object();
    private int mGetTxPowerBlocked = 0;
    private Record mTxRecord = new Record();
    private long mLastQueryTxTime = 0;
    private CloudFunctionConfig.ICloudFunctionListener mFunctionListener = new CloudFunctionConfig.ICloudFunctionListener() { // from class: com.miui.powerkeeper.powerchecker.TxPowerChecker.1
        @Override // com.miui.powerkeeper.cloudcontrol.CloudFunctionConfig.ICloudFunctionListener
        public void onCloudFunctionUpdated() {
            TxPowerChecker txPowerChecker = TxPowerChecker.this;
            txPowerChecker.sendMessage(txPowerChecker.obtainMessage(2));
        }
    };
    MyPhoneStateListener mPhoneStateListener = new MyPhoneStateListener();

    private class MyPhoneStateListener extends PhoneStateListener {
        private int mDataActivity;
        private int mDataState;
        private int mNetworkType;
        private int mVoiceTechnology;

        @Override // android.telephony.PhoneStateListener
        public void onCallStateChanged(int i2, String str) {
            if ((i2 == 2 || i2 == 1) && this.mVoiceTechnology != 0) {
                TxPowerChecker.this.scheduleCheckTxPower(0);
            }
        }

        @Override // android.telephony.PhoneStateListener
        public void onCellLocationChanged(CellLocation cellLocation) {
            try {
                if (cellLocation instanceof GsmCellLocation) {
                    GsmCellLocation gsmCellLocation = (GsmCellLocation) cellLocation;
                    TxPowerChecker.this.mTxRecord.setLac(gsmCellLocation.getLac());
                    TxPowerChecker.this.mTxRecord.setCid(gsmCellLocation.getCid());
                }
            } catch (Exception e2) {
                Log.e(TxPowerChecker.TAG, "Exception in onCellLocationChanged", e2);
            }
        }

        @Override // android.telephony.PhoneStateListener
        public void onDataActivity(int i2) {
            if (TxPowerChecker.DEBUG) {
                Log.d(TxPowerChecker.TAG, "onDataActivity: direction=" + i2);
            }
            this.mDataActivity = i2;
            if (this.mDataState != 2 || (i2 & 3) == 0 || this.mNetworkType == 0) {
                return;
            }
            TxPowerChecker.this.scheduleCheckTxPower(1);
        }

        @Override // android.telephony.PhoneStateListener
        public void onDataConnectionStateChanged(int i2, int i3) {
            if (TxPowerChecker.DEBUG) {
                Log.d(TxPowerChecker.TAG, "onDataConnectionStateChanged: state = " + i2 + " type = " + i3);
            }
            if (this.mDataState == i2 && i3 == this.mNetworkType) {
                return;
            }
            this.mDataState = i2;
            this.mNetworkType = i3;
            TxPowerChecker.this.mTxRecord.setDataTech(this.mNetworkType);
            if (TxPowerChecker.DEBUG) {
                Log.d(TxPowerChecker.TAG, "mNetworkType: " + this.mNetworkType);
            }
        }

        @Override // android.telephony.PhoneStateListener
        public void onServiceStateChanged(ServiceState serviceState) {
            if (serviceState.getVoiceRegState() != 0) {
                this.mVoiceTechnology = 0;
                return;
            }
            this.mVoiceTechnology = serviceState.getRilVoiceRadioTechnology();
            if (TxPowerChecker.DEBUG) {
                Log.d(TxPowerChecker.TAG, "service state changed: voice tech:" + this.mVoiceTechnology);
            }
            TxPowerChecker.this.mTxRecord.setVoiceTech(this.mVoiceTechnology);
            TxPowerChecker.this.mTxRecord.plmn = serviceState.getOperatorNumeric();
        }

        @Override // android.telephony.PhoneStateListener
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            if (TxPowerChecker.this.mTelephonyManager.getPhoneType() == 1) {
                TxPowerChecker.this.mTxRecord.setSignal(signalStrength.getDbm());
            }
        }

        private MyPhoneStateListener() {
            this.mDataState = -1;
            this.mDataActivity = -1;
            this.mNetworkType = 0;
            this.mVoiceTechnology = 0;
        }
    }

    private class Record {
        private int cid;
        private int dataTech;
        private int lac;
        private String plmn;
        private int signal;
        private int txpower;
        private int type;
        private int voiceTech;

        public Record() {
            reset();
        }

        private void addJsonObject(JSONObject jSONObject, String str, Object obj) {
            if (jSONObject == null || obj == null) {
                return;
            }
            try {
                if ((obj instanceof String) && ((String) obj).isEmpty()) {
                    return;
                }
                jSONObject.put(str, obj);
            } catch (JSONException unused) {
            }
        }

        public void reset() {
            this.signal = -1;
            this.cid = -1;
            this.lac = -1;
            this.txpower = -1;
            this.dataTech = -1;
            this.voiceTech = -1;
            this.type = -1;
            this.plmn = null;
        }

        public void setCid(int i2) {
            this.cid = i2;
        }

        public void setDataTech(int i2) {
            this.dataTech = i2;
        }

        public void setLac(int i2) {
            this.lac = i2;
        }

        public void setPlmn(String str) {
            this.plmn = str;
        }

        public void setSignal(int i2) {
            this.signal = i2;
        }

        public void setTxpower(int i2) {
            this.txpower = i2;
        }

        public void setType(int i2) {
            this.type = i2;
        }

        public void setVoiceTech(int i2) {
            this.voiceTech = i2;
        }

        public JSONObject toJson() {
            JSONObject jSONObject = new JSONObject();
            addJsonObject(jSONObject, "type", Integer.valueOf(this.type));
            addJsonObject(jSONObject, "voice", Integer.valueOf(this.voiceTech));
            addJsonObject(jSONObject, "data", Integer.valueOf(this.dataTech));
            addJsonObject(jSONObject, "tx", Integer.valueOf(this.txpower));
            addJsonObject(jSONObject, "lac", Integer.valueOf(this.lac));
            addJsonObject(jSONObject, "cid", Integer.valueOf(this.cid));
            addJsonObject(jSONObject, "signal", Integer.valueOf(this.signal));
            addJsonObject(jSONObject, "pl", this.plmn);
            addJsonObject(jSONObject, "time", Long.valueOf(System.currentTimeMillis()));
            return jSONObject;
        }
    }

    static {
        try {
            Class<?> cls = Class.forName("com.android.internal.telephony.ITelephony");
            clsITelephony = cls;
            sInvokeOemRilRequestRaw = cls.getMethod("invokeOemRilRequestRaw", byte[].class, byte[].class);
        } catch (ClassNotFoundException unused) {
            Log.v(TAG, "Can't find the class ITelephony");
        } catch (NoSuchMethodException unused2) {
            Log.v(TAG, "Can't find the method invokeOemRilRequestRaw");
        }
    }

    public TxPowerChecker(Context context) {
        this.mContext = context;
        this.mTelephonyManager = (TelephonyManager) context.getSystemService("phone");
        boolean z2 = SimpleSettings.Misc.getBoolean(this.mContext, Constant.FUNC_GET_TXPOWER, false);
        this.mEnabled = z2;
        if (z2) {
            registerPhoneStateListener();
        }
        CloudFunctionConfig.getInstance().registerCloudFunctionConfig(this.mFunctionListener);
    }

    private void disable() {
        unRegisterPhoneStateListener();
        removeMessages(0);
        removeMessages(1);
        Thread thread = this.mTxPowerQueryThread;
        if (thread != null) {
            thread.interrupt();
            this.mTxPowerQueryThread = null;
        }
    }

    /* JADX WARN: Code duplicated, block: B:12:0x0021  */
    /* JADX WARN: Code duplicated, block: B:13:0x0023  */
    /* JADX WARN: Code duplicated, block: B:20:0x0038  */
    /* JADX WARN: Code duplicated, block: B:7:0x0010  */
    private int getRadioInterfaceforNas(int i2, int i3) {
        int i4 = 4;
        if (i3 == 1) {
            switch (i2) {
                case 1:
                case 2:
                case 16:
                    break;
                case 3:
                    i4 = 5;
                    break;
                case 4:
                case 7:
                    i4 = 1;
                    break;
                case 5:
                case 6:
                case 12:
                    i4 = 2;
                    break;
                case 8:
                case 9:
                case 10:
                case 14:
                case 15:
                    i4 = !isCmcc(this.mTxRecord.plmn) ? 5 : 9;
                    break;
                case 11:
                case 18:
                default:
                    i4 = -1;
                    break;
                case 13:
                case 19:
                    i4 = 8;
                    break;
                case 17:
                    i4 = 9;
                    break;
            }
        } else if (i3 == 0) {
            switch (i2) {
                case 1:
                case 2:
                case 16:
                    break;
                case 3:
                    i4 = 5;
                    break;
                case 4:
                case 5:
                case 18:
                default:
                    i4 = -1;
                    break;
                case 6:
                    i4 = 1;
                    break;
                case 7:
                case 8:
                case 12:
                    i4 = 2;
                    break;
                case 9:
                case 10:
                case 11:
                case 13:
                case 15:
                    i4 = !isCmcc(this.mTxRecord.plmn) ? 5 : 9;
                    break;
                case 14:
                case 19:
                    i4 = 8;
                    break;
                case 17:
                    i4 = 9;
                    break;
            }
        } else {
            i4 = -1;
        }
        Log.d(TAG, "in getRadioInterfaceForNas: tech = " + i2 + ", ret = " + i4);
        return i4;
    }

    private final void getTxPower(final int i2) {
        if (DEBUG) {
            Log.d(TAG, "getTxPower for tech: " + i2);
        }
        Thread thread = this.mTxPowerQueryThread;
        if (thread == null || !thread.isAlive()) {
            synchronized (this.mLock) {
                this.mTxPowerQueryThread = new Thread(TAG) { // from class: com.miui.powerkeeper.powerchecker.TxPowerChecker.2
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() {
                        TxPowerChecker txPowerChecker = TxPowerChecker.this;
                        txPowerChecker.queryTxPower(i2, txPowerChecker);
                        Log.d(TxPowerChecker.TAG, "queryTxPower over");
                    }
                };
                Log.d(TAG, "new thread to query tx power " + this.mTxPowerQueryThread.getName());
                this.mTxPowerQueryThread.start();
            }
            return;
        }
        Thread thread2 = this.mTxPowerQueryThread;
        if (thread2 == null || !thread2.isAlive()) {
            return;
        }
        int i3 = this.mGetTxPowerBlocked + 1;
        this.mGetTxPowerBlocked = i3;
        if (i3 > 10) {
            disable();
        }
    }

    private boolean isCmcc(String str) {
        int i2;
        if (str == null || TextUtils.isEmpty(str) || str.length() < 5) {
            return false;
        }
        try {
            i2 = Integer.parseInt(str.substring(str.length() - (str.length() > 5 ? 3 : 2)));
        } catch (Exception unused) {
            i2 = -1;
        }
        return i2 == 0 || 2 == i2 || 7 == i2;
    }

    private void onCloudUpdated() {
        boolean z2 = SimpleSettings.Misc.getBoolean(this.mContext, Constant.FUNC_GET_TXPOWER, false);
        Log.d(TAG, "get tx power enabled: " + z2);
        if (this.mEnabled == z2) {
            Log.d(TAG, "get tx power enabled not change");
            return;
        }
        this.mEnabled = z2;
        if (z2) {
            registerPhoneStateListener();
            return;
        }
        unRegisterPhoneStateListener();
        removeMessages(0);
        removeMessages(1);
    }

    private int parseResult(byte[] bArr) {
        return ((bArr[3] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL) << 24) | ((bArr[2] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL) << 16) | ((bArr[1] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL) << 8) | (bArr[0] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void queryTxPower(int i2, Handler handler) {
        byte[] bArr = new byte[20];
        byte[] bArr2 = new byte[20];
        ByteBuffer byteBufferWrap = ByteBuffer.wrap(bArr);
        byteBufferWrap.order(ByteOrder.nativeOrder());
        byteBufferWrap.put("QOEMHOOK".getBytes());
        byteBufferWrap.putInt(524342);
        byteBufferWrap.putInt(4);
        byteBufferWrap.putInt(i2);
        Log.d(TAG, "queryTxPower req = " + Arrays.toString(bArr));
        try {
            if (sInvokeOemRilRequestRaw != null) {
                int iIntValue = ((Integer) sInvokeOemRilRequestRaw.invoke(ITelephony.Stub.asInterface(ServiceManager.getService("phone")), bArr, bArr2)).intValue();
                Log.d(TAG, "get tx power done: " + Arrays.toString(bArr2));
                if (iIntValue >= 4) {
                    Message messageObtain = Message.obtain();
                    messageObtain.what = 1;
                    messageObtain.arg1 = parseResult(bArr2);
                    Log.d(TAG, "parse tx power: " + messageObtain.arg1);
                    handler.sendMessage(messageObtain);
                }
            } else {
                Log.d(TAG, "no method to get tx power");
            }
        } catch (IllegalAccessException unused) {
            Log.e(TAG, "can't access");
        } catch (Exception e2) {
            e2.printStackTrace();
            Log.e(TAG, "get tx power failed");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scheduleCheckTxPower(int i2) {
        if (i2 == 1) {
            long jCurrentTimeMillis = System.currentTimeMillis();
            if (jCurrentTimeMillis - this.mLastQueryTxTime < 600000) {
                return;
            } else {
                this.mLastQueryTxTime = jCurrentTimeMillis;
            }
        }
        Message messageObtain = Message.obtain();
        messageObtain.what = 0;
        messageObtain.arg1 = i2;
        this.mTxRecord.setType(i2);
        if (i2 == 1) {
            sendMessage(messageObtain);
        } else if (i2 == 0) {
            sendMessageDelayed(messageObtain, 3000L);
        }
    }

    public void destroy() {
        disable();
        CloudFunctionConfig.getInstance().unRegisterCloudFunctionConfig(this.mFunctionListener);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("dump TxPowerChecker:");
        printWriter.println("\tfunc enabled = " + this.mEnabled);
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i2 = message.what;
        if (i2 == 0) {
            int i3 = message.arg1;
            if (i3 == 0) {
                getTxPower(getRadioInterfaceforNas(this.mTxRecord.voiceTech, 0));
                return;
            } else {
                if (i3 == 1) {
                    getTxPower(getRadioInterfaceforNas(this.mTxRecord.dataTech, 1));
                    return;
                }
                return;
            }
        }
        if (i2 != 1) {
            if (i2 != 2) {
                return;
            }
            onCloudUpdated();
        } else {
            int i4 = message.arg1;
            if (i4 != 0) {
                this.mTxRecord.setTxpower(i4);
            }
        }
    }

    void registerPhoneStateListener() {
        Log.d(TAG, "register phone state");
        this.mTelephonyManager.listen(this.mPhoneStateListener, 497);
    }

    void unRegisterPhoneStateListener() {
        Log.d(TAG, "un-register phone state");
        this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
    }
}
