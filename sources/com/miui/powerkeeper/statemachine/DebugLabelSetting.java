package com.miui.powerkeeper.statemachine;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.LocalLog;
import android.util.Log;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.utils.DebugUtils;
import com.miui.powerkeeper.utils.FileUtil;
import com.miui.powerkeeper.utils.PowerKeeperPackageManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import miui.yellowpage.YellowPageContract;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class DebugLabelSetting extends Handler {
    private static final String CLOUD_GPIO_REGULATOR_DEBUG = "gpio_regulator_debug";
    private static final String CLOUD_LABEL_CONTENT = "label_content";
    private static final String CLOUD_LABEL_SECRET_CODE_CTRL = "label_content_secret_code";
    private static final String CLOUD_RAMDUMP_DEBUG = "ramdump_debug";
    private static final String MI_TEST_PKG = "com.jtom.userprobation";
    private static final String TAG = "DebugLabelSetting ";
    private static DebugLabelSetting sInstance;
    private final int CLOUD_DIRECT_CTRL_MODE;
    private final int CLOUD_DIRECT_VALUE_MAX_LENGTH;
    private final boolean DBG_DEBUG_CONTROL;
    private final int DEFAULT_CTRL_MODE;
    private final int GPIO_A_REGULATOR_CODE_LOW_INDEX;
    private final int MSG_LABEL_CONTENT_UPDATE;
    private final int MSG_PKG_EXISTENT_CHANGED;
    private final String RAM_DUMP_PATH;
    private final int RAM_DUMP_SECRET_CODE_INDEX;
    private final int RESET_CTRL_MODE;
    private final int SECRET_CODE_CTRL_MODE;
    private final int SECRET_CODE_VALUE_MAX_LENGTH;
    private b.a mCloudObserver;
    private Context mContext;
    private boolean mCurSleepModeState;
    private int mCurrentIndex;
    private boolean mGpioRegDebug;
    private boolean mIsDebugRamdumpShow;
    private boolean mIsSupportGRDevice;
    private boolean mIsSupportRamdumpDevice;
    private ArrayMap<String, String> mLabelContent;
    private LocalLog mLocalLog;
    private final Object mLock;
    private boolean mMiTestPkgInstalled;
    private boolean mRamDumpDebug;
    private ArrayMap<String, Boolean> mSecretCodeHostEnableList;
    private static ArrayMap<String, String> mLabelSecretCodeCtrl = new ArrayMap<>();
    private static String[] mRamDumpSupportDevice = {"haotian", "dada"};
    private static String[] mGpioRegSupportDevice = {"haotian", "dada"};

    private DebugLabelSetting(Context context, Looper looper) {
        super(looper);
        this.DBG_DEBUG_CONTROL = Log.isLoggable("power.labelsetting", 3);
        this.RESET_CTRL_MODE = 0;
        this.DEFAULT_CTRL_MODE = 1;
        this.CLOUD_DIRECT_CTRL_MODE = 2;
        this.SECRET_CODE_CTRL_MODE = 3;
        this.CLOUD_DIRECT_VALUE_MAX_LENGTH = 24;
        this.SECRET_CODE_VALUE_MAX_LENGTH = 7;
        this.RAM_DUMP_SECRET_CODE_INDEX = 1;
        this.GPIO_A_REGULATOR_CODE_LOW_INDEX = 3;
        this.RAM_DUMP_PATH = "/sys/class/power_debug/debug_suspend";
        this.MSG_LABEL_CONTENT_UPDATE = 0;
        this.MSG_PKG_EXISTENT_CHANGED = 2;
        this.mLocalLog = new LocalLog(32);
        this.mLabelContent = new ArrayMap<>();
        this.mSecretCodeHostEnableList = new ArrayMap<>();
        this.mIsDebugRamdumpShow = false;
        this.mCurSleepModeState = false;
        this.mMiTestPkgInstalled = false;
        this.mIsSupportRamdumpDevice = false;
        this.mIsSupportGRDevice = false;
        this.mGpioRegDebug = true;
        this.mRamDumpDebug = true;
        this.mCurrentIndex = -1;
        this.mLock = new Object();
        this.mContext = context;
        this.mCloudObserver = b.a.p();
        registerCloudObserver();
        this.mIsSupportRamdumpDevice = isSupportDevice(mRamDumpSupportDevice);
        boolean zIsSupportDevice = isSupportDevice(mGpioRegSupportDevice);
        this.mIsSupportGRDevice = zIsSupportDevice;
        if (zIsSupportDevice) {
            this.mCurrentIndex = -1;
            setLabelValue(0, 0, "/sys/class/power_debug/debug_suspend");
            EventsAggregator.getInstance(this.mContext).registerSleepStateListener(this);
            EventsAggregator.getInstance(this.mContext).registerForPkgExistentState(this, 2);
        }
        this.mMiTestPkgInstalled = isMiTestAppInstalled();
        sendMessage(obtainMessage(0, -1, -1, null));
    }

    public static DebugLabelSetting getInstance() {
        return sInstance;
    }

    public static synchronized DebugLabelSetting init(Context context, Looper looper) {
        try {
            if (!isFeatureOn()) {
                return null;
            }
            if (sInstance == null) {
                sInstance = new DebugLabelSetting(context, looper);
            }
            return sInstance;
        } catch (Throwable th) {
            throw th;
        }
    }

    public static boolean isDebugSecretCodeEnable(String str) {
        DebugLabelSetting debugLabelSetting = sInstance;
        return debugLabelSetting != null && debugLabelSetting.mSecretCodeHostEnableList != null && debugLabelSetting.mMiTestPkgInstalled && debugLabelSetting.isSupportDevice(mRamDumpSupportDevice) && sInstance.mRamDumpDebug;
    }

    public static boolean isFeatureOn() {
        return !Build.IS_DEBUGGABLE || SystemProperties.getBoolean("debug.power.labelsetting.enable", true);
    }

    private boolean isMiTestAppInstalled() {
        ArraySet<String> installedThirdPartApps = PowerKeeperPackageManager.getInstalledThirdPartApps();
        return installedThirdPartApps != null && installedThirdPartApps.contains(MI_TEST_PKG);
    }

    private boolean isSupportDevice(String[] strArr) {
        if (strArr != null && !miui.os.Build.IS_INTERNATIONAL_BUILD) {
            for (String str : strArr) {
                if (Build.DEVICE.equals(str)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void notifyCloudConfigChange(int i2) {
        if (3 == i2) {
            writeLocalLog("gpio cloud update, mGpioRegDebug = " + this.mGpioRegDebug);
            updateNodeState();
        }
    }

    private void notifySleepModeStateChange(boolean z2, String str) {
        writeLocalLog("sleepMode change, state = " + z2 + ", reason = " + str + ", curSleepModeState = " + this.mCurSleepModeState);
        boolean z3 = this.mCurSleepModeState;
        if (z2 && str == null) {
            this.mCurSleepModeState = true;
        } else if (!z2) {
            this.mCurSleepModeState = false;
        }
        if (this.mCurSleepModeState != z3) {
            updateNodeState();
        }
    }

    private void onPkgExistentChanged(EventsAggregator.EventResult eventResult) {
        if (eventResult.mFlag == 0 && MI_TEST_PKG.equals((String) eventResult.mObj)) {
            writeLocalLog("remove mi test pkg");
            this.mMiTestPkgInstalled = false;
            updateNodeState();
        } else if (eventResult.mFlag == 1 && MI_TEST_PKG.equals((String) eventResult.mObj)) {
            this.mMiTestPkgInstalled = true;
        }
    }

    private ArrayMap<String, String> parseConfigStr(String str, int i2) {
        ArrayMap<String, String> arrayMap = new ArrayMap<>();
        if (str == null || str.isEmpty()) {
            if (this.DBG_DEBUG_CONTROL) {
                Log.d(TAG, "parseConfigStr is empty");
            }
            return arrayMap;
        }
        String[] strArrSplit = str.split(",");
        if (strArrSplit != null && strArrSplit.length != 0) {
            for (String str2 : strArrSplit) {
                try {
                    String[] strArrSplit2 = str2.split(":");
                    if (strArrSplit2 != null && strArrSplit2.length == 2 && !TextUtils.isEmpty(strArrSplit2[0]) && !TextUtils.isEmpty(strArrSplit2[1]) && strArrSplit2[1].length() <= i2) {
                        String str3 = strArrSplit2[0];
                        String str4 = strArrSplit2[1];
                        arrayMap.put(str3, str4);
                        if (i2 == 7) {
                            int i3 = Integer.parseInt(str4, 2);
                            if ("/sys/class/power_debug/debug_suspend".equals(str3) && (i3 & 1) == 1) {
                                this.mSecretCodeHostEnableList.put(DebugUtils.DEBUG_SUSPEND_SECRET_CODE, Boolean.TRUE);
                            }
                        } else {
                            continue;
                        }
                    }
                } catch (Exception e2) {
                    Log.e(TAG, "parseConfigStr error string to int error " + e2);
                }
            }
        }
        return arrayMap;
    }

    /* JADX WARN: Code duplicated, block: B:27:0x008f  */
    /* JADX WARN: Code duplicated, block: B:31:0x009f  */
    /* JADX WARN: Code duplicated, block: B:33:0x00a4  */
    private void parseNodeConfigFromCloud(int i2, String str) {
        String str2;
        ArrayMap<String, String> configStr;
        this.mLabelContent.clear();
        mLabelSecretCodeCtrl.clear();
        this.mSecretCodeHostEnableList.clear();
        String str3 = null;
        if (str == null) {
            b.a aVar = this.mCloudObserver;
            int i3 = b.b.R;
            JSONObject jSONObjectL = aVar.l(i3);
            if (this.mCloudObserver.s(i3) && jSONObjectL != null) {
                String strOptString = jSONObjectL.optString(CLOUD_LABEL_CONTENT, null);
                String strOptString2 = jSONObjectL.optString(CLOUD_LABEL_SECRET_CODE_CTRL, null);
                this.mGpioRegDebug = jSONObjectL.optBoolean(CLOUD_GPIO_REGULATOR_DEBUG, true);
                this.mRamDumpDebug = jSONObjectL.optBoolean(CLOUD_RAMDUMP_DEBUG, true);
                str2 = strOptString2;
                str3 = strOptString;
            }
            if (str3 != null && !str3.isEmpty()) {
                configStr = parseConfigStr(str3, 24);
                this.mLabelContent = configStr;
                if (configStr != null) {
                    configStr.size();
                }
            }
            if (str2 != null && !str2.isEmpty()) {
                mLabelSecretCodeCtrl = parseConfigStr(str2, 7);
            }
            notifyCloudConfigChange(3);
        }
        try {
            if (i2 == 1) {
                String[] strArrSplit = str.split("=");
                if (strArrSplit != null && strArrSplit.length == 2) {
                    if (CLOUD_LABEL_CONTENT.equals(strArrSplit[0])) {
                        str3 = strArrSplit[1];
                        str2 = null;
                    } else if (CLOUD_LABEL_SECRET_CODE_CTRL.equals(strArrSplit[0])) {
                        str2 = strArrSplit[1];
                    }
                }
                return;
            }
            JSONObject jSONObject = new JSONObject(str);
            String strOptString3 = jSONObject.optString(CLOUD_LABEL_CONTENT, null);
            String strOptString4 = jSONObject.optString(CLOUD_LABEL_SECRET_CODE_CTRL, null);
            this.mGpioRegDebug = jSONObject.optBoolean(CLOUD_GPIO_REGULATOR_DEBUG, true);
            this.mRamDumpDebug = jSONObject.optBoolean(CLOUD_RAMDUMP_DEBUG, true);
            str3 = strOptString3;
            str2 = strOptString4;
            if (str3 != null) {
                configStr = parseConfigStr(str3, 24);
                this.mLabelContent = configStr;
                if (configStr != null) {
                    configStr.size();
                }
            }
            if (str2 != null) {
                mLabelSecretCodeCtrl = parseConfigStr(str2, 7);
            }
            notifyCloudConfigChange(3);
        } catch (JSONException e2) {
            Log.e(TAG, "parse json config failed" + e2);
            return;
        }
        str2 = null;
        if (str3 != null) {
            configStr = parseConfigStr(str3, 24);
            this.mLabelContent = configStr;
            if (configStr != null) {
                configStr.size();
            }
        }
        if (str2 != null) {
            mLabelSecretCodeCtrl = parseConfigStr(str2, 7);
        }
        notifyCloudConfigChange(3);
    }

    private void quickDebug() {
        String str;
        if (!Build.IS_DEBUGGABLE || (str = SystemProperties.get("debug.power.labelsetting", "")) == null || str.isEmpty()) {
            return;
        }
        writeLocalLog("quickDebugConfig: " + str);
        sendMessage(obtainMessage(0, 1, -1, str));
    }

    private void registerCloudObserver() {
        this.mCloudObserver.t(b.b.R, new b.a.d() { // from class: com.miui.powerkeeper.statemachine.DebugLabelSetting.1
            @Override // b.a.d
            public void onChanged(JSONObject jSONObject) {
                ExtremeScenarioAwareHandler.notifyCloudUpdate();
                PowerScheduleController.notifyCloudUpdate();
            }
        });
    }

    private void setLabelValue(int i2, int i3, String str) {
        synchronized (this.mLock) {
            try {
                writeLocalLog("setLabelValue  ctrlMode = " + i2 + ", secretCodePath = " + str + ", secretCodeIndex = " + i3);
                try {
                    if (i2 != 3 || i3 == 0) {
                        if (i2 == 2) {
                            ArrayMap<String, String> arrayMap = this.mLabelContent;
                            if (arrayMap != null && arrayMap.size() != 0) {
                                for (int i4 = 0; i4 < this.mLabelContent.size(); i4++) {
                                    String strKeyAt = this.mLabelContent.keyAt(i4);
                                    String strValueAt = this.mLabelContent.valueAt(i4);
                                    if (!TextUtils.isEmpty(strKeyAt) && !TextUtils.isEmpty(strValueAt)) {
                                        writeLocalLog("write path = " + strKeyAt + ",  value = " + strValueAt);
                                        if (!FileUtil.writeToFile(strKeyAt, strValueAt)) {
                                            Log.e(TAG, "write " + strKeyAt + " error");
                                        }
                                    }
                                }
                            }
                            writeLocalLog("mLabelContent is not valid");
                            return;
                        }
                        if (i2 == 1 || i2 == 0) {
                            int i5 = this.mCurrentIndex;
                            int i6 = (i5 == -1 || i2 == 0) ? i3 : i5 | i3;
                            if (i5 == i6) {
                                writeLocalLog("ignore for same index");
                                writeLocalLog("mCurrentIndex = " + i6 + "secretCodeIndex = " + i3);
                                return;
                            }
                            String str2 = "0x" + Integer.toString(i6, 16);
                            writeLocalLog("write path = " + str + ",  value = " + str2 + ", mCurrentIndex = " + i6 + ", secretCodeIndex = " + i3);
                            if (FileUtil.writeToFile(str, str2)) {
                                this.mCurrentIndex = i6;
                            } else {
                                Log.e(TAG, "write " + str + " error");
                            }
                        }
                    } else if (i3 == 1 && "/sys/class/power_debug/debug_suspend".equals(str)) {
                        int i7 = i3 << 24;
                        writeLocalLog("test setLabelValue val(int) = " + i7);
                        String str3 = "0x" + Integer.toString(i7, 16);
                        writeLocalLog("test setLabelValue value(string) = " + str3);
                        if (!FileUtil.writeToFile(str, str3)) {
                            Log.e(TAG, "write " + str + " error");
                        }
                    }
                } catch (Exception unused) {
                    Log.e(TAG, "setLabelValue  error, ctrlMode = " + i2 + "secretCodePath = " + i3);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private void updateNodeState() {
        if (!this.mGpioRegDebug || !this.mCurSleepModeState || !this.mMiTestPkgInstalled) {
            setLabelValue(0, 0, "/sys/class/power_debug/debug_suspend");
        } else if (isSupportDevice(mGpioRegSupportDevice)) {
            setLabelValue(1, 3, "/sys/class/power_debug/debug_suspend");
        }
    }

    private void updateValueBySecretCode(int i2, String str) {
        writeLocalLog("receiveSecretCode: " + str + ", status: " + i2);
        if (str == null || str.isEmpty() || !DebugUtils.DEBUG_SUSPEND_SECRET_CODE.equals(str) || i2 != 1) {
            return;
        }
        setLabelValue(3, 1, "/sys/class/power_debug/debug_suspend");
    }

    private void writeLocalLog(String str) {
        if (this.DBG_DEBUG_CONTROL) {
            Log.i(TAG, str);
        }
        this.mLocalLog.log(TAG + str);
    }

    void destroy() {
        Log.w(TAG, "destroy");
        if (isSupportDevice(mGpioRegSupportDevice)) {
            EventsAggregator.getInstance(this.mContext).unregisterSleepStateListener(this);
            EventsAggregator.getInstance(this.mContext).unregisterForPkgExistentState(this);
        }
    }

    void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        String str;
        this.mLocalLog.dump(fileDescriptor, printWriter, strArr);
        printWriter.println("DebugLabelSetting labelconfig mMiTestPkgInstalled = " + this.mMiTestPkgInstalled);
        printWriter.println("DebugLabelSetting labelconfig mCurSleepModeState = " + this.mCurSleepModeState);
        printWriter.println("DebugLabelSetting labelconfig mGpioRegDebug(cloud) = " + this.mGpioRegDebug);
        printWriter.println("DebugLabelSetting labelconfig mRamDumpDebug(cloud) = " + this.mRamDumpDebug);
        printWriter.println("DebugLabelSetting labelconfig mIsSupportGRDevice = " + this.mIsSupportGRDevice);
        printWriter.println("DebugLabelSetting labelconfig mIsSupportRamdumpDevice = " + this.mIsSupportRamdumpDevice);
        printWriter.println("DebugLabelSetting labelconfig mLabelContent = " + this.mLabelContent);
        printWriter.println("DebugLabelSetting labelconfig mLabelSecretCodeCtrl = " + mLabelSecretCodeCtrl);
        printWriter.println("DebugLabelSetting labelconfig mSecretCodeHostEnableList = " + this.mSecretCodeHostEnableList);
        printWriter.println("DebugLabelSetting labelconfig mCurrentIndex = " + this.mCurrentIndex);
        if (!Build.IS_DEBUGGABLE || strArr == null || strArr.length < 1 || (str = strArr[0]) == null || !str.equals("-labelsetting_quick_debug")) {
            return;
        }
        quickDebug();
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i2 = message.what;
        if (i2 == -19) {
            Bundle bundle = (Bundle) message.obj;
            if (bundle != null) {
                notifySleepModeStateChange(bundle.getBoolean(YellowPageContract.Permission.STATE, false), bundle.getString("reason"));
                return;
            }
            return;
        }
        if (i2 == 0) {
            writeLocalLog("MSG_LABEL_CONTENT_UPDATE");
            parseNodeConfigFromCloud(message.arg1, (String) message.obj);
        } else {
            if (i2 != 2) {
                return;
            }
            onPkgExistentChanged((EventsAggregator.EventResult) message.obj);
        }
    }
}
