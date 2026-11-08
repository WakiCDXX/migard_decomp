package com.miui.powerkeeper.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.LocalLog;
import android.util.Log;
import android.util.Slog;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.thermalcollector.collector.ScenarioCollectorUtils;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import miui.util.FeatureParser;

/* JADX INFO: loaded from: classes.dex */
public class IrisServiceUtils {
    private static final boolean DEBUG = Log.isLoggable(IrisServiceUtils.class.getSimpleName(), 3);
    private static int INDEX_BASE = 0;
    private static final int INDEX_ENHANCE = 2;
    private static final int INDEX_PERFORMANCE = 1;
    private static final int INDEX_POWER_SAVE = 3;
    public static final int MODE_BY_PASS = 1;
    public static final int MODE_DISABLE_IRIS_PQ = 4;
    public static final int MODE_ENABLE_IRIS_PQ = 3;
    public static final int MODE_PT = 2;
    public static final int MODE_RESET_MEMC = 5;
    public static final int MODE_RESET_SDR2HDR = 6;
    private static final ArrayList<String> POWER_MODE_LIST;
    private static final String TAG = "IrisServiceUtils";
    private static final String UI_SERVICE_NAME = "uiservice";
    static final Set<String> chipSet;
    private static volatile IrisServiceUtils sInstance;
    private final String PATH_THERMAL_BOARD_TEMP;
    private final String PATH_THERMAL_TYPE;
    private final String PATH_THERMAL_X7_TEMP;
    private int TEMPERATURE_BOARD_CLEAR_THRESHOLD;
    private int TEMPERATURE_BOARD_UPPER_THRESHOLD;
    private int TEMPERATURE_X7_CLEAR_THRESHOLD;
    private int TEMPERATURE_X7_UPPER_THRESHOLD;
    private int X7_INDEX;
    private final String X7_THERMAL_LIMIT_SWITCH;
    private final String X7_THERMAL_TYPE;
    private final String X7_VIDEO_MODE_SWITCH;
    private Object iIrisConfigService;
    private final Deque<Float> mBoardTempQueue;
    private Context mContext;
    private int mCurrentExtremeState;
    private Handler mHandler;
    private boolean mIsDisabledByBoardTemp;
    private boolean mIsDisabledByIrisTemp;
    private boolean mIsNeedBoardThermalRecovery;
    private boolean mIsNeedx7ThermalRecovery;
    private LocalLog mLocalLog;
    private Object mLock;
    private final Deque<Float> mNtcTempQueue;
    private int mPrevExtremeState;
    private ContentResolver mResolver;
    private SettingsObserver mSettingsObserver;
    private final int UISERVICE_TRANSACTION_NOTIFY_GESTURE = 2;
    private final int UISERVICE_TRANSACTION_NOTIFY_THERMAL = 3;
    private final int MSG_SET_IRIS_SWTICH = 1;
    private final int MSG_UPDATE_TEMP_VAL = 2;
    private final int MSG_UPDATE_X7_THERMAL_LIMIT_SWITCH = 3;
    private final int MSG_UPDATE_ENABLE_BOARD_TEMP_SWITCH = 4;
    private final int CODE_BYPASS_MODE = 56;
    private final int CODE_PQ_MODE = 519;
    private final int CODE_MEMC = 258;
    private final int CODE_SDR2HDR = 267;
    private int mTempWindowSize = 5;

    private final class MyHandler extends Handler {
        public MyHandler(Looper looper) {
            super(looper);
        }

        private void updateBoardTempState() {
            float boardTemp = IrisServiceUtils.this.readBoardTemp();
            IrisServiceUtils irisServiceUtils = IrisServiceUtils.this;
            float aveTemp = irisServiceUtils.getAveTemp(boardTemp, irisServiceUtils.mBoardTempQueue);
            if (IrisServiceUtils.DEBUG) {
                Log.i(IrisServiceUtils.TAG, "updateBoardTempState board average temp value=" + aveTemp);
            }
            if (aveTemp < IrisServiceUtils.this.TEMPERATURE_BOARD_UPPER_THRESHOLD) {
                if (aveTemp > IrisServiceUtils.this.TEMPERATURE_BOARD_CLEAR_THRESHOLD || !IrisServiceUtils.this.mIsDisabledByBoardTemp) {
                    return;
                }
                IrisServiceUtils.this.setBoardThermalLimitSwitch(false);
                IrisServiceUtils.this.mLocalLog.log("updateBoardTempState x7 enable boardAveTemp=" + aveTemp);
                if (IrisServiceUtils.this.mIsNeedBoardThermalRecovery) {
                    Settings.Global.putInt(IrisServiceUtils.this.mContext.getContentResolver(), ScenarioCollectorUtils.VB_SYSTEM_PROPERTIES_FRC_X7, 1);
                    return;
                }
                return;
            }
            if (IrisServiceUtils.this.mIsDisabledByBoardTemp) {
                if (IrisServiceUtils.DEBUG) {
                    Log.i(IrisServiceUtils.TAG, "updateBoardTempState board thermal limit already reached");
                    return;
                }
                return;
            }
            IrisServiceUtils.this.mLocalLog.log("updateBoardTempState x7 disable boardAveTemp=" + aveTemp);
            IrisServiceUtils irisServiceUtils2 = IrisServiceUtils.this;
            irisServiceUtils2.mIsNeedBoardThermalRecovery = Settings.Global.getInt(irisServiceUtils2.mContext.getContentResolver(), ScenarioCollectorUtils.VB_SYSTEM_PROPERTIES_FRC_X7, 0) == 1;
            if (IrisServiceUtils.this.mIsNeedBoardThermalRecovery) {
                Settings.Global.putInt(IrisServiceUtils.this.mContext.getContentResolver(), ScenarioCollectorUtils.VB_SYSTEM_PROPERTIES_FRC_X7, 0);
            }
            sendMessageDelayed(obtainMessage(4, Boolean.TRUE), 500L);
        }

        private void updateNtcTempState() {
            IrisServiceUtils irisServiceUtils = IrisServiceUtils.this;
            float ntcTemp = irisServiceUtils.readNtcTemp(irisServiceUtils.X7_INDEX);
            IrisServiceUtils irisServiceUtils2 = IrisServiceUtils.this;
            float aveTemp = irisServiceUtils2.getAveTemp(ntcTemp, irisServiceUtils2.mNtcTempQueue);
            if (IrisServiceUtils.DEBUG) {
                Log.i(IrisServiceUtils.TAG, "updateNtcTempState x7 average temp value=" + aveTemp);
            }
            if (aveTemp < IrisServiceUtils.this.TEMPERATURE_X7_UPPER_THRESHOLD) {
                if (aveTemp > IrisServiceUtils.this.TEMPERATURE_X7_CLEAR_THRESHOLD || !IrisServiceUtils.this.mIsDisabledByIrisTemp) {
                    return;
                }
                IrisServiceUtils.this.setThermalLimitSwitch_X7(false);
                IrisServiceUtils.this.mLocalLog.log("updateNtcTempState x7 enable ntcAveTemp=" + aveTemp);
                if (IrisServiceUtils.this.mIsNeedx7ThermalRecovery) {
                    Settings.Global.putInt(IrisServiceUtils.this.mContext.getContentResolver(), ScenarioCollectorUtils.VB_SYSTEM_PROPERTIES_FRC_X7, 1);
                    return;
                }
                return;
            }
            if (IrisServiceUtils.this.mIsDisabledByIrisTemp) {
                if (IrisServiceUtils.DEBUG) {
                    Log.i(IrisServiceUtils.TAG, "updateNtcTempState x7 thermal list already reached");
                    return;
                }
                return;
            }
            IrisServiceUtils.this.mLocalLog.log("updateNtcTempState x7 disable ntcAveTemp=" + aveTemp);
            IrisServiceUtils irisServiceUtils3 = IrisServiceUtils.this;
            irisServiceUtils3.mIsNeedx7ThermalRecovery = Settings.Global.getInt(irisServiceUtils3.mContext.getContentResolver(), ScenarioCollectorUtils.VB_SYSTEM_PROPERTIES_FRC_X7, 0) == 1;
            if (IrisServiceUtils.this.mIsNeedx7ThermalRecovery) {
                Settings.Global.putInt(IrisServiceUtils.this.mContext.getContentResolver(), ScenarioCollectorUtils.VB_SYSTEM_PROPERTIES_FRC_X7, 0);
            }
            sendMessageDelayed(obtainMessage(3, Boolean.TRUE), 500L);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i2 = message.what;
            if (i2 != 1) {
                if (i2 == 2) {
                    updateNtcTempState();
                    updateBoardTempState();
                    IrisServiceUtils.this.scheduleTempUpdate();
                    return;
                } else if (i2 == 3) {
                    IrisServiceUtils.this.setThermalLimitSwitch_X7(((Boolean) message.obj).booleanValue());
                    return;
                } else {
                    if (i2 != 4) {
                        return;
                    }
                    IrisServiceUtils.this.setBoardThermalLimitSwitch(((Boolean) message.obj).booleanValue());
                    return;
                }
            }
            int iIntValue = ((Integer) message.obj).intValue();
            IrisServiceUtils.this.mLocalLog.log("MSG_SET_IRIS_SWTICH set state=" + iIntValue + " mCurrentState=" + IrisServiceUtils.this.mCurrentExtremeState);
            if (iIntValue != IrisServiceUtils.this.mCurrentExtremeState) {
                IrisServiceUtils irisServiceUtils = IrisServiceUtils.this;
                irisServiceUtils.mPrevExtremeState = irisServiceUtils.mCurrentExtremeState;
                Settings.Global.putInt(IrisServiceUtils.this.mContext.getContentResolver(), ScenarioCollectorUtils.VB_SYSTEM_PROPERTIES_FRC_X7, iIntValue);
            }
        }
    }

    private final class SettingsObserver extends ContentObserver {
        public SettingsObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z2, Uri uri) {
            synchronized (IrisServiceUtils.this.mLock) {
                IrisServiceUtils.this.handleSettingsChangedLocked(uri);
            }
        }
    }

    static {
        ArrayList<String> arrayList = new ArrayList<>();
        POWER_MODE_LIST = arrayList;
        arrayList.add(INDEX_BASE, "POWER_MODE_BASE");
        arrayList.add(1, "POWER_PERFORMANCE_MODE_OPEN");
        arrayList.add(2, "POWER_BALANCED_MODE_OPEN");
        arrayList.add(3, "POWER_SAVE_MODE_OPEN");
        chipSet = new HashSet(Arrays.asList("nt72335"));
    }

    private IrisServiceUtils(Context context) {
        int integer = FeatureParser.getInteger("x7_thermal_threshold", 70) * 1000;
        this.TEMPERATURE_X7_UPPER_THRESHOLD = integer;
        this.TEMPERATURE_X7_CLEAR_THRESHOLD = integer - 3000;
        int integer2 = FeatureParser.getInteger("x7_board_thermal_threshold", 42) * 1000;
        this.TEMPERATURE_BOARD_UPPER_THRESHOLD = integer2;
        this.TEMPERATURE_BOARD_CLEAR_THRESHOLD = integer2 - 2000;
        this.X7_INDEX = -1;
        this.X7_THERMAL_TYPE = "X7_therm";
        this.PATH_THERMAL_TYPE = "sys/class/thermal/thermal_zone%d/type";
        this.PATH_THERMAL_X7_TEMP = "sys/class/thermal/thermal_zone%d/temp";
        this.PATH_THERMAL_BOARD_TEMP = "sys/class/thermal/thermal_message/board_sensor_temp";
        this.X7_THERMAL_LIMIT_SWITCH = "x7_thermal_limit_switch";
        this.X7_VIDEO_MODE_SWITCH = ScenarioCollectorUtils.VB_SYSTEM_PROPERTIES_FRC_X7;
        this.mLocalLog = new LocalLog(128);
        this.mNtcTempQueue = new ArrayDeque();
        this.mBoardTempQueue = new ArrayDeque();
        this.mLock = new Object();
        this.mContext = context;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float getAveTemp(float f2, Deque<Float> deque) {
        deque.add(Float.valueOf(f2));
        Iterator<Float> it = deque.iterator();
        float fFloatValue = 0.0f;
        while (it.hasNext()) {
            fFloatValue += it.next().floatValue();
        }
        float size = fFloatValue / deque.size();
        if (deque.size() + 1 > this.mTempWindowSize) {
            deque.removeFirst();
        }
        return size;
    }

    public static IrisServiceUtils getInstance(Context context) {
        if (sInstance == null) {
            synchronized (IrisServiceUtils.class) {
                try {
                    if (sInstance == null) {
                        sInstance = new IrisServiceUtils(context);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return sInstance;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSettingsChangedLocked(Uri uri) {
        boolean z2;
        ArrayList<String> arrayList = POWER_MODE_LIST;
        int iIndexOf = arrayList.indexOf(uri.getLastPathSegment());
        if (iIndexOf == 1) {
            z2 = Settings.System.getIntForUser(this.mResolver, arrayList.get(1), 0, -2) != 0;
            if (DEBUG) {
                Log.i(TAG, "POWER_PERFORMANCE_MODE_OPEN=" + z2);
            }
            onPowerModeChanged(z2 ? 1 : 2);
            return;
        }
        if (iIndexOf != 3) {
            return;
        }
        z2 = Settings.System.getIntForUser(this.mResolver, arrayList.get(3), 0, -2) != 0;
        if (DEBUG) {
            Log.i(TAG, "POWER_SAVE_MODE=" + z2);
        }
        onPowerModeChanged(z2 ? 3 : 2);
    }

    private void initNtc() {
        int i2 = 0;
        while (true) {
            String fileString = FileUtil.getFileString(String.format(Locale.CHINA, "sys/class/thermal/thermal_zone%d/type", Integer.valueOf(i2)));
            if (fileString == null) {
                return;
            }
            if ("X7_therm".equals(fileString)) {
                this.X7_INDEX = i2;
                return;
            }
            i2++;
        }
    }

    public static boolean isDualDPUSupported() {
        return SystemProperties.getBoolean("ro.vendor.display.iris_x7.support", false) || chipSet.contains(SystemProperties.get("ro.vendor.display.dualDPU.support", ""));
    }

    public static boolean isDualDPUSupportedNew() {
        return chipSet.contains(SystemProperties.get("ro.vendor.display.dualDPU.support", ""));
    }

    private void notifyUIServiceThermalStatus(boolean z2) {
        if (DEBUG) {
            Log.i(TAG, "notifyUIServiceThermalStatus =" + z2);
        }
        IBinder service = ServiceManager.getService(UI_SERVICE_NAME);
        if (service == null) {
            Log.e(TAG, "null binder");
            return;
        }
        Parcel parcelObtain = Parcel.obtain();
        parcelObtain.writeInterfaceToken("com.android.server.ui.IUiService");
        parcelObtain.writeInt(z2 ? 1 : 0);
        try {
            try {
                service.transact(3, parcelObtain, null, 1);
            } catch (RemoteException e2) {
                Slog.e(TAG, "uiService abnormal=", e2);
            }
        } finally {
            parcelObtain.recycle();
        }
    }

    private void onPowerModeChanged(int i2) {
        if (this.mHandler.hasMessages(1)) {
            this.mHandler.removeMessages(1);
        }
        int i3 = Settings.Global.getInt(this.mContext.getContentResolver(), ScenarioCollectorUtils.VB_SYSTEM_PROPERTIES_FRC_X7, 0);
        this.mCurrentExtremeState = i3;
        if (i2 != 1) {
            if (i2 == 2) {
                Handler handler = this.mHandler;
                handler.sendMessageDelayed(handler.obtainMessage(1, Integer.valueOf(this.mPrevExtremeState)), 200L);
                return;
            } else if (i2 != 3) {
                return;
            }
        }
        if (i3 == 1) {
            Handler handler2 = this.mHandler;
            handler2.sendMessage(handler2.obtainMessage(1, 0));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float readBoardTemp() {
        try {
            return Float.parseFloat(FileUtil.getFileString("sys/class/thermal/thermal_message/board_sensor_temp"));
        } catch (NumberFormatException e2) {
            Log.e(TAG, "readBoardTemp failed: " + e2);
            return -1.0f;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float readNtcTemp(int i2) {
        if (i2 == -1) {
            Slog.e(TAG, "readNtcTemp x7 failed: ntc not found");
            return -1.0f;
        }
        try {
            return Float.parseFloat(FileUtil.getFileString(String.format(Locale.CHINA, "sys/class/thermal/thermal_zone%d/temp", Integer.valueOf(i2))));
        } catch (NumberFormatException e2) {
            Log.e(TAG, "readNtcTemp x7 failed: " + e2);
            return -1.0f;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scheduleTempUpdate() {
        this.mHandler.removeMessages(2);
        this.mHandler.sendEmptyMessageDelayed(2, 10000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setBoardThermalLimitSwitch(boolean z2) {
        if (DEBUG) {
            Log.i(TAG, "setBoardThermalLimitSwitch set board temp flag=" + z2);
        }
        this.mIsDisabledByBoardTemp = z2;
        if (isDualDPUSupportedNew()) {
            notifyUIServiceThermalStatus(z2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setThermalLimitSwitch_X7(boolean z2) {
        if (DEBUG) {
            Log.i(TAG, "setThermalLimitSwitch_X7 set x7 temp flag=" + z2);
        }
        this.mIsDisabledByIrisTemp = z2;
        Settings.Global.putInt(this.mContext.getContentResolver(), "x7_thermal_limit_switch", z2 ? 1 : 0);
        if (isDualDPUSupportedNew()) {
            notifyUIServiceThermalStatus(z2);
        }
    }

    private void systemReady() {
        initNtc();
        scheduleTempUpdate();
        if (SystemProperties.getBoolean("ro.vendor.display.iris_x7.support", false)) {
            this.iIrisConfigService = this.mContext.getSystemService("iris_config");
            this.mResolver = this.mContext.getContentResolverForUser(UserHandle.SYSTEM);
            this.mSettingsObserver = new SettingsObserver(this.mHandler);
            handleSettingsChangedLocked();
            ContentResolver contentResolver = this.mResolver;
            ArrayList<String> arrayList = POWER_MODE_LIST;
            contentResolver.registerContentObserver(Settings.System.getUriFor(arrayList.get(1)), false, this.mSettingsObserver, -1);
            this.mResolver.registerContentObserver(Settings.System.getUriFor(arrayList.get(3)), false, this.mSettingsObserver, -1);
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("dump IrisServiceUtils");
        this.mLocalLog.dump(fileDescriptor, printWriter, strArr);
        printWriter.println("TEMPERATURE_X7_UPPER_THRESHOLD=" + this.TEMPERATURE_X7_UPPER_THRESHOLD);
        printWriter.println("TEMPERATURE_X7_CLEAR_THRESHOLD=" + this.TEMPERATURE_X7_CLEAR_THRESHOLD);
        printWriter.println("TEMPERATURE_BOARD_UPPER_THRESHOLD=" + this.TEMPERATURE_BOARD_UPPER_THRESHOLD);
        printWriter.println("TEMPERATURE_BOARD_CLEAR_THRESHOLD=" + this.TEMPERATURE_BOARD_CLEAR_THRESHOLD);
        printWriter.println("end dump IrisServiceUtils");
    }

    public boolean getThermalLimitSwitch_X7() {
        return this.mIsDisabledByIrisTemp || this.mIsDisabledByBoardTemp;
    }

    public int irisConfigureSet(int i2) {
        switch (i2) {
            case 1:
                return irisConfigureSet(56, new int[]{1});
            case 2:
                return irisConfigureSet(56, new int[]{0});
            case 3:
                return irisConfigureSet(519, new int[]{2});
            case 4:
                return irisConfigureSet(519, new int[]{1});
            case 5:
                return irisConfigureSet(258, new int[]{0});
            case 6:
                return irisConfigureSet(267, new int[]{3, 0});
            default:
                return -1;
        }
    }

    public void notifyUIServiceGestureChange(String str) {
        if (DEBUG) {
            Log.i(TAG, "notifyUIServiceGestureChange =" + str);
        }
        IBinder service = ServiceManager.getService(UI_SERVICE_NAME);
        if (service == null) {
            Log.e(TAG, "null binder");
            return;
        }
        Parcel parcelObtain = Parcel.obtain();
        parcelObtain.writeInterfaceToken("com.android.server.ui.IUiService");
        parcelObtain.writeString(str);
        try {
            service.transact(2, parcelObtain, null, 1);
        } catch (RemoteException e2) {
            Slog.e(TAG, "uiService abnormal=", e2);
        }
    }

    public void onCreate() {
        Log.i(TAG, "onCreate");
        this.mHandler = new MyHandler(PowerKeeperManager.getThread().getLooper());
        systemReady();
        Settings.Global.putInt(this.mContext.getContentResolver(), "x7_thermal_limit_switch", 0);
    }

    public int irisConfigureSet(int i2, int[] iArr) {
        int iIntValue = -1;
        if (this.iIrisConfigService == null) {
            Slog.e(TAG, "can't get Iris service");
            return -1;
        }
        if (i2 >= 0 && iArr != null) {
            for (int i3 = 0; i3 < iArr.length; i3++) {
                if (DEBUG) {
                    Slog.i(TAG, String.format("Iris Set %3d = (%d %d)", Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(iArr[i3])));
                }
            }
            try {
                iIntValue = ((Integer) this.iIrisConfigService.getClass().getDeclaredMethod("irisConfigureSet", Integer.TYPE, int[].class).invoke(this.iIrisConfigService, Integer.valueOf(i2), iArr)).intValue();
            } catch (Exception e2) {
                Slog.e(TAG, "Access irisService failed", e2);
            }
            if (DEBUG) {
                Slog.i(TAG, "Iris ret value=" + iIntValue);
            }
            return iIntValue;
        }
        Slog.e(TAG, "input parameters are wrong");
        return -1;
    }

    private void handleSettingsChangedLocked() {
        this.mCurrentExtremeState = Settings.Global.getInt(this.mContext.getContentResolver(), ScenarioCollectorUtils.VB_SYSTEM_PROPERTIES_FRC_X7, 0);
        int i2 = 0;
        while (true) {
            ArrayList<String> arrayList = POWER_MODE_LIST;
            if (i2 >= arrayList.size()) {
                return;
            }
            if (Settings.System.getIntForUser(this.mResolver, arrayList.get(i2), 0, -2) != 0 && i2 != 2) {
                onPowerModeChanged(i2);
                return;
            }
            i2++;
        }
    }
}
