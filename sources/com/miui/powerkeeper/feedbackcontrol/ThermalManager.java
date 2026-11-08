package com.miui.powerkeeper.feedbackcontrol;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.TrafficStats;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.LocalLog;
import android.util.Log;
import android.util.Slog;
import com.android.internal.util.IndentingPrintWriter;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.perfengine.PerfEngineManager;
import com.miui.powerkeeper.powerchecker.NightStandbyRecord;
import com.miui.powerkeeper.provider.CloudAppConfigure;
import com.miui.powerkeeper.provider.SimpleSettingKeys;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.provider.ThermalInfoHelper;
import com.miui.powerkeeper.statemachine.DisplayFrameSetting;
import com.miui.powerkeeper.statemachine.SleepModeControllerNew;
import com.miui.powerkeeper.statemachine.ThermalIECHandler;
import com.miui.powerkeeper.thermalcollector.CollectorManager;
import com.miui.powerkeeper.thermalcollector.collector.ScenarioCollectorUtils;
import com.miui.powerkeeper.thermalcollector.collector.ThermalPlusCollector;
import com.miui.powerkeeper.ui.BgService;
import com.miui.powerkeeper.utils.CommonAdapter;
import com.miui.powerkeeper.utils.FileUtil;
import com.miui.powerkeeper.utils.NetUtils;
import com.miui.powerkeeper.utils.PackageUtil;
import com.miui.powerkeeper.utils.PowerLog;
import com.miui.powerkeeper.utils.ShellUtils;
import com.miui.powerkeeper.utils.Utils;
import com.miui.powerkeeper.utils.WifiApAdapter;
import com.xiaomi.channel.commonutils.android.AppInfoUtils;
import com.xiaomi.push.service.MIPushAccount;
import e.e;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import miui.hardware.display.DisplayFeatureManager;
import miui.process.ForegroundInfo;
import miui.process.ProcessConfig;
import miui.process.ProcessManager;
import miui.process.RunningProcessInfo;
import miui.security.SecurityManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class ThermalManager {
    private static final String APP_ACESS_LOCK_TOP_ACTIVITY = "com.miui.applicationlock.ConfirmAccessControl";
    private static final int APP_ACTIVE_COUNT_THRESHOLD = 2;
    private static final int APP_IN_BG_MAX_CHECK_TIME = 600000;
    private static final int APP_IN_BG_TIME_THRESHOLD = 300000;
    private static final int APP_STATUS_CONUT_THRESHOLD = 4;
    private static final int CPU_LOAD_THRESHOLD = 10;
    public static final int DEFAULT_ALLOWED_KILL_ALL_BACK_PRO_BATTERY_TEMP_THRESHOLD = 50;
    public static final int DEFAULT_ALLOWED_KILL_BATTERY_TEMP_THRESHHOLD = 48;
    public static final int DEFAULT_ALLOWED_KILL_SOME_BACK_PRO_BATTERY_TEMP_THRESHOLD = 48;
    public static final int DEFAULT_ALLOWED_SHOW_BENCHMARK_DIALOG_TEMP_THRESHHOLD = 45;
    public static final int DEFAULT_ALLOWED_SHOW_DIALOG_TEMP_THRESHHOLD = 48;
    private static final int DEFAULT_BATTERY_LEVEL_THRESHOLD = 3;
    public static final int DEFAULT_BATTERY_THRESHOLD = 47;
    public static final int DEFAULT_BL_TEMP_THRESHOLD = 60;
    private static final boolean DEFAULT_CATCH_BUG_REPORT_FLG = false;
    private static final boolean DEFAULT_DEBUG = false;
    public static final boolean DEFAULT_EXIT_FOREGROUND_APP_ENABLE = true;
    private static final int DEFAULT_IEC_MONITOR_KILL_ALL_BACK_PRO_BATTERY_TEMP = 44;
    private static final int DEFAULT_IEC_MONITOR_KILL_BATTERY_TEMP = 44;
    private static final int DEFAULT_IEC_MONITOR_KILL_SOME_BACK_PRO_BATTERY_TEMP = 44;
    public static final boolean DEFAULT_KILL_ALL_BACK_PRO_ENABLE = true;
    public static final boolean DEFAULT_KILL_SOME_BACK_PRO_ENABLE = true;
    private static final int DEFAULT_MAX_POLLING_COUNT = 10;
    private static final int DEFAULT_MAX_POLLING_TIME = 120000;
    private static final int DEFAULT_MAX_THERMAL_POLLING_TIME = 1800000;
    private static final int DEFAULT_MAX_UPDATE_AMBIENT_TIME = 3600000;
    private static final int DEFAULT_MIN_POLLING_TIME = 60000;
    private static final String DEFAULT_PERF_LIMITS = "no_string";
    private static final String DEFAULT_TEMP_PATH = "/temp";
    public static final boolean DEFAULT_THERMAL_ENABLE = false;
    public static final boolean DEFAULT_THERMAL_ENABLE_STATUS = false;
    private static final long DEFAULT_TIME_CATCH_BUG_REPORT_WHEN_SCREENOFF = 1800000;
    private static final String DEFAULT_TPL_NAME = "default";
    private static final int DEFAULT_UPDATE_POLLING_TIME = 600000;
    private static final int DEFAULT_XO_TEMP_CHECK_THRESHOLD = 25;
    public static final int DEFAULT_XO_TEMP_DIV = 1;
    public static final int DEFAULT_XO_TEMP_DIV_THOUSANDS = 1000;
    public static final int DEFAULT_XO_TEMP_THRESHOLD = 52;
    private static final int DOWNLOAD_SPEED_THRESHOLD = 20;
    private static final int LOCAL_LOG_SIZE = 512;
    private static final String PERF_LIMITS_ENABLE = "enable";
    private static final String PERF_LIMITS_LOW_CAP_LEVEL = "low_cap_level";
    private static final String PERF_LIMITS_MIDDLE = "middle";
    private static final String PERF_LIMITS_NORMAL = "normal";
    private static final String PKN_COM = "com";
    private static final String PKN_IQIYI = "com.qiyi.video";
    private static final String PKN_POINT = ".";
    private static final String PKN_QIYI = "qiyi";
    private static final String PKN_VIDEO = "video";
    private static final int PROVISION_APP_USERID = 999;
    private static final int SCREEN_DPI_MODE = 24;
    private static final long SHOW_KILL_PKG_DELAYED_TIME = 1000;
    private static final String TAG = "PowerKeeper.Thermal";
    private static final int TEMP_BACKPROGRESS_SIZE = 10000;
    private static final int TEMP_BENCHMARK_SHOW_DIAG_AND_KILL_FG = 1;
    private static final int TEMP_BENCHMARK_SIZE = 10;
    private static final int TEMP_CCC_NEW_SIZE = 100000;
    private static final int TEMP_DATA = 4;
    private static final int TEMP_DISPLAY_CTRL_30HZ = 4;
    private static final int TEMP_DISPLAY_CTRL_48HZ = 3;
    private static final int TEMP_DISPLAY_CTRL_50HZ = 2;
    private static final int TEMP_DISPLAY_CTRL_60HZ = 1;
    private static final int TEMP_DISPLAY_CTRL_90HZ = 5;
    private static final int TEMP_DP_SIZE = 10000000;
    private static final int TEMP_GPS = 3;
    private static final int TEMP_GPS_DATA = 7;
    private static final int TEMP_KILL_ALL_BACKPROS = 2;
    private static final int TEMP_KILL_PROC = 3;
    private static final int TEMP_KILL_SOME_BACKPROS = 1;
    private static final int TEMP_NEW_DIALOG_SIZE = 100000000;
    private static final int TEMP_SHOW_DIAG = 2;
    private static final int TEMP_SHOW_KILL = 4;
    private static final int TEMP_SHOW_STRICTER_KILL = 5;
    private static final int TEMP_SK_SIZE = 100000;
    private static final int TEMP_STATE_CCC = 1;
    private static final int TEMP_STATE_DEFAULT = 0;
    private static final String TEMP_STATE_FILE = "/sys/class/thermal/thermal_message/temp_state";
    private static final int TEMP_STATE_FLASH = 4;
    private static final int TEMP_STATE_FLASH_WIFIAP = 5;
    private static final int TEMP_STATE_FLASH_WIFIAP_GPS = 6;
    private static final int TEMP_WGD_SIZE = 1000000;
    private static final int TEMP_WIFIAP = 2;
    private static final int TEMP_WIFIAP_DATA = 6;
    private static final int TEMP_WIFIAP_GPS = 5;
    private static final int TEMP_WIFIAP_GPS_DATA = 1;
    private static final int THERMAL = 253;
    private static final int THERMAL_CONTROL_LEVEL_HIGH = 2;
    private static final int THERMAL_CONTROL_LEVEL_MIDDLE = 1;
    private static final int THERMAL_CONTROL_LEVEL_NORMAL = 0;
    private static final String TORCH_REAL_LEVEL_PATH = "/sys/class/thermal/thermal_message/torch_real_level";
    private static final int XO_TEMP_THRESHOLD = 45;
    private static final int XO_TEMP_THRESHOLDCLR = 43;
    private static ThermalManager sInstance;
    ProcessConfig killAllProcessConfig;
    ProcessConfig killSomeProcessConfig;
    private PowerKeeperInterface.d mBatteryChangedCallback;
    private int mBatteryLevel;
    private long mBatteryTemperature;
    private Context mContext;
    private int mCurrentAmbientTemp;
    private long mDisChargeTime;
    private DisplayFeatureManager mDisplayFeatureManager;
    private Handler mHandler;
    private String mHardwareId;
    private HighTempRange mHighTempRange;
    private int mPollingTime;
    private int mPrevUploadTorchLevel;
    private PowerKeeperInterface.e0 mProcessDiedCallback;
    private PowerKeeperInterface.f mProcessForegroundCallback;
    private boolean mReceiverRegisterred;
    private long mScreenOffTime;
    private int mSubBatteryLevel;
    private ContentObserver mThermalCloudObserver;
    private ThermalInfoCollector mThermalInfoCollector;
    private ContentObserver mThermalPerfLimitsObserver;
    private MyThermalStatus mThermalStatus;
    private long mUpdateAmbientTime;
    private PowerKeeperInterface.f tplForegroundCallback;
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    private static boolean mFeatureEnabled = false;
    private static int mXoTempDiv = 1;
    private static boolean mBoardTempSupport = true;
    private static boolean mOtherBoardTempSupport = true;
    private static boolean mAmbientTempSupport = true;
    private static int mXoThermalTempCheckThreshold = 25;
    private static int mBatteryLevelThreshold = 3;
    private static boolean mThermalCollectorEnable = false;
    private boolean mIsCatchBugReport = false;
    private boolean mThermalMonitorEnabled = false;
    private boolean mAppStateMonitorEnabled = false;
    private final Object mStatusLock = new Object();
    private final Object mTempStateLock = new Object();
    private int mTemperatureThreshold = 47;
    private int mXoThermalTemperatureThreshold = 52;
    private int mBlThermalTemperatureThreshold = 60;
    private boolean mEnable = false;
    private int mXoTempThreshold = 45;
    private int mXoTempThresholdclr = XO_TEMP_THRESHOLDCLR;
    private int mAppInBackgroundTimeTh = 300000;
    private int mMaxCheckTime = 600000;
    private int mAppStatusCountTh = 4;
    private int mAppActiveCountTh = 2;
    private int mDownloadSpeedTh = 20;
    private int mCpuLoadTh = 10;
    private int mMinPollingTime = 60000;
    private int mMaxPollingTime = DEFAULT_MAX_POLLING_TIME;
    private boolean mDebug = false;
    private boolean mThermalMonitorReady = false;
    private boolean mThresholdTrigged = false;
    private boolean mBlThresholdTrigged = false;
    private final IntentFilter mIntentFilter = new IntentFilter("android.intent.action.BATTERY_CHANGED");
    private TempStateListener mTempStateListener = null;
    private int mTempStatePollingTime = 1000;
    private int mTempStatePrev = -1;
    private int mBatteryAllowKillSettingsPrev = -1;
    private int mTempStateSettingsPrev = -1;
    private int mTempStateNow = 0;
    private boolean mShowHighTempDialog = false;
    private boolean mShowKillPkgToast = false;
    private int mPerfLimitsTemp = 0;
    private int mPerfLimitsLevelSave = -1;
    private boolean mPerfLimitsEnabled = false;
    private int lowCapLevel = 0;
    private int lowCapLevelTh = 0;
    private int fpsLast = 0;
    private boolean mSoftbankTimerToast = false;
    private boolean mThermalEnable = false;
    private boolean mChargeFlag = false;
    List<ProcessStatusMsg> mProcessStatusMsg = new ArrayList();
    List<ThermalPerfLimits> mThermalPerfLimits = new ArrayList();
    private ThermalPerfLimits mTempThermalPerfLimits = null;
    private LocalLog mHistoryLog = new LocalLog(512);
    private int mLastPlug = -1;
    private boolean mStricterKillEnable = false;
    private int mAllowedKillBatteryTempThreshold = 48;
    private int mAllowedKillSomeBackProBatteryTempThreshold = 48;
    private int mAllowedKillAllBackProBatteryTempThreshold = 50;
    private int mAllowedShowDialogBatteryTempThreshhold = 48;
    private int mAllowedShowBenchmarkDialogBatteryTempThreshhold = 45;
    private boolean mExitForegroundAppEnable = true;
    private boolean mKillSomeBackProEnable = true;
    private boolean mKillAllBackProEnable = true;
    private String mNotAllowedKillApps = "com.google.android.dialer,com.google.android.contacts,com.android.contacts,com.miui.home,com.android.server.telecom,com.android.contacts,com.android.incallui,com.mi.android.globallauncher,com.android.mms,com.android.server.telecom,com.miui.huanji,com.hema.spottransfer,com.tencent.transfer,com.qqwj.clonedata,com.android.settings,com.mi.stabilitytest,QMESA_32,QMESA_64,com.android.lowpowerstatestest,com.miui.video,com.clov4r.android.nil,qseecom_sample_client,flashval,com.google.android.apps.messaging";
    private String mNotAllowedKillAppsForNewDialog = "com.google.android.dialer,com.google.android.contacts,com.android.contacts,com.miui.home,com.android.server.telecom,com.android.contacts,com.android.incallui,com.mi.android.globallauncher,com.android.mms,com.android.server.telecom,com.miui.huanji,com.hema.spottransfer,com.tencent.transfer,com.qqwj.clonedata,com.android.settings,com.mi.stabilitytest,QMESA_32,QMESA_64,com.android.lowpowerstatestest,com.miui.video,com.clov4r.android.nil,qseecom_sample_client,flashval,com.google.android.apps.messaging,com.miui.securitycenter";
    private boolean mExitFGBenchmarkEnable = true;
    private String mCurrentFGPkg = "";
    private boolean isShowKillSomeBackPros = false;
    private boolean isShowKillAllBackPros = false;
    private boolean mIsHightTempCloseData = false;
    private boolean mIsHightTempCloseGPS = false;
    private boolean mIsHightTempCloseWifiAp = false;
    private int mIECKillBatteryTemp = 44;
    private int mIECKillSomeBackProBatteryTemp = 44;
    private int mIECKillAllBackProBatteryTemp = 44;
    private int mIECState = 99;
    private TorchRealLevelListener mTorchRealLevelListener = null;
    private final String KEY_TORCH_BRIGHTNESS_LEVEL = "torch_level";
    private final String KEY_SETING_TEMP_STATE = "thermal_temp_state_value";
    private final String KEY_BATTERY_TEMP_ALLOW_KILL = "allowed_kill_battery_temp_threshhold";
    private String mCurrentActivityName = "";
    private PowerKeeperInterface.r mTPLForegroundReceiver = new PowerKeeperInterface.r() { // from class: com.miui.powerkeeper.feedbackcontrol.ThermalManager.4
        @Override // com.miui.powerkeeper.PowerKeeperInterface.r
        public void onForegroundActivitiesChanged(int i2, boolean z2) {
            if (z2) {
                ThermalManager.this.ThermalPerfLimitsArrayScan(PackageUtil.getPackageNameByUid(ThermalManager.this.mContext, i2));
                ThermalManager thermalManager = ThermalManager.this;
                thermalManager.ThermalPerfLimitsArraySend(thermalManager.mContext);
            }
        }
    };
    private PowerKeeperInterface.m mBatteryStatusReceiver = new PowerKeeperInterface.m() { // from class: com.miui.powerkeeper.feedbackcontrol.ThermalManager.11
        @Override // com.miui.powerkeeper.PowerKeeperInterface.m
        public void onBatteryChanged(int i2, int i3, int i4, int i5, int i6, int i7) throws Throwable {
            if (ThermalManager.this.mThermalStatus == null) {
                ThermalManager thermalManager = ThermalManager.this;
                thermalManager.mThermalStatus = thermalManager.new MyThermalStatus(i2, i3, i4, i5);
                return;
            }
            int xoThermalTemp = ThermalTempInfoHelper.getXoThermalTemp();
            if (xoThermalTemp >= ThermalManager.this.mXoTempThreshold) {
                ThermalManager thermalManager2 = ThermalManager.this;
                thermalManager2.enableThermalMonitor(thermalManager2.mEnable);
            } else if (xoThermalTemp <= ThermalManager.this.mXoTempThresholdclr) {
                ThermalManager.this.enableThermalMonitor(false);
            }
            if (ThermalManager.this.mPerfLimitsEnabled) {
                ThermalManager.this.mPerfLimitsTemp = xoThermalTemp;
                ThermalManager.this.lowCapLevel = i4;
                ThermalManager thermalManager3 = ThermalManager.this;
                thermalManager3.ThermalPerfLimitsArraySend(thermalManager3.mContext);
            }
            ThermalManager.this.mBatteryTemperature = i5;
            Log.d(ThermalManager.TAG, "mBatteryTemperature: " + ThermalManager.this.mBatteryTemperature);
            ThermalManager.this.mThermalStatus.update(i2, i3, i4, i5);
            if (ThermalManager.this.mThermalInfoCollector == null || !ThermalManager.mThermalCollectorEnable || ThermalManager.this.mLastPlug == i6) {
                return;
            }
            Log.i(ThermalManager.TAG, "batteryStatusReceiver plug info:" + i6);
            ThermalManager.this.mThermalInfoCollector.onPlugedStatusChanged(i6);
            ThermalManager.this.mLastPlug = i6;
        }
    };
    private PowerKeeperInterface.r mProcessForegroundReceiver = new PowerKeeperInterface.r() { // from class: com.miui.powerkeeper.feedbackcontrol.ThermalManager.12
        @Override // com.miui.powerkeeper.PowerKeeperInterface.r
        public void onForegroundActivitiesChanged(int i2, boolean z2) {
            synchronized (ThermalManager.this.mProcessStatusMsg) {
                try {
                    PackageManager packageManager = ThermalManager.this.mContext.getPackageManager();
                    String packageNameByUid = PackageUtil.getPackageNameByUid(ThermalManager.this.mContext, i2);
                    try {
                        if (PackageUtil.isThirdPartApp(packageManager.getPackageInfo(packageNameByUid, 0).applicationInfo)) {
                            for (int i3 = 0; i3 < ThermalManager.this.mProcessStatusMsg.size(); i3++) {
                                ProcessStatusMsg processStatusMsg = ThermalManager.this.mProcessStatusMsg.get(i3);
                                if (processStatusMsg.uId == i2) {
                                    processStatusMsg.setTimeWhenProcessChanged(z2);
                                    processStatusMsg.pollCount = 0;
                                    processStatusMsg.activeCount = 0;
                                    processStatusMsg.lastTime = processStatusMsg.timeChangeBackground;
                                    processStatusMsg.totalBytes = ThermalManager.this.getTotalBytesByUid(i2);
                                    processStatusMsg.speedTrans = 0L;
                                    int size = processStatusMsg.procList.size();
                                    for (int i4 = 0; i4 < size; i4++) {
                                        ProcessStatus processStatus = processStatusMsg.procList.get(i4);
                                        processStatus.cpuTime = Utils.getCpuTime();
                                        processStatus.pidCpuTime = Utils.getProcessCpuTime(processStatus.pid);
                                    }
                                    return;
                                }
                            }
                            List<Integer> pidByPackageName = PackageUtil.getPidByPackageName(packageNameByUid);
                            if (pidByPackageName.size() == 0) {
                                return;
                            }
                            ProcessStatusMsg processStatusMsg2 = ThermalManager.this.new ProcessStatusMsg(i2, packageNameByUid, pidByPackageName);
                            processStatusMsg2.setTimeWhenProcessChanged(z2);
                            processStatusMsg2.lastTime = processStatusMsg2.timeChangeBackground;
                            ThermalManager.this.mProcessStatusMsg.add(processStatusMsg2);
                        }
                    } catch (PackageManager.NameNotFoundException unused) {
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    };
    private PowerKeeperInterface.y mProcessDiedReceiver = new PowerKeeperInterface.y() { // from class: com.miui.powerkeeper.feedbackcontrol.ThermalManager.13
        @Override // com.miui.powerkeeper.PowerKeeperInterface.y
        public void onProcessDiedListener(int i2, int i3) {
            synchronized (ThermalManager.this.mProcessStatusMsg) {
                try {
                    int size = ThermalManager.this.mProcessStatusMsg.size();
                    for (int i4 = 0; i4 < size; i4++) {
                        if (ThermalManager.this.mProcessStatusMsg.get(i4).uId == i2) {
                            ThermalManager.this.mProcessStatusMsg.remove(i4);
                            return;
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    };
    private BroadcastReceiver mThermalReceiver = new BroadcastReceiver() { // from class: com.miui.powerkeeper.feedbackcontrol.ThermalManager.15
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (Utils.isCharging(ThermalManager.this.mContext) || !ThermalManager.this.mThermalEnable) {
                return;
            }
            String action = intent.getAction();
            if (ThermalManager.DEBUG) {
                Log.d(ThermalManager.TAG, "receive action = " + action);
            }
            if ("android.intent.action.SCREEN_OFF".equals(action)) {
                ThermalManager.this.mScreenOffTime = SystemClock.elapsedRealtime();
            } else {
                if (!"android.intent.action.SCREEN_ON".equals(action) || 0 == ThermalManager.this.mScreenOffTime || SystemClock.elapsedRealtime() - ThermalManager.this.mScreenOffTime < ThermalInfoCollector.DEFAULT_COLLECT_UNRE_APPS_INFO_MIN_INTERVAL) {
                    return;
                }
                if (SystemClock.elapsedRealtime() - ThermalManager.this.mUpdateAmbientTime >= 3600000 || ThermalManager.this.mUpdateAmbientTime == 0) {
                    ThermalManager.this.mHandler.postDelayed(new Runnable() { // from class: com.miui.powerkeeper.feedbackcontrol.ThermalManager.15.1
                        @Override // java.lang.Runnable
                        public void run() {
                            ThermalManager.this.mCurrentAmbientTemp = ThermalTempInfoHelper.getAmbientTemp();
                            ThermalManager.this.mUpdateAmbientTime = SystemClock.elapsedRealtime();
                            if (ThermalManager.DEBUG) {
                                Log.d(ThermalManager.TAG, "get current ambient temp " + ThermalManager.this.mCurrentAmbientTemp);
                            }
                        }
                    }, 10000L);
                }
            }
        }
    };

    class MyThermalStatus {
        int batteryHealth;
        int batteryLevel;
        int batteryStatus;
        int batteryTemperature;
        int blThermalTemperature;
        boolean isBlThermalValid;
        boolean isXoTermalValid;
        int xoTermalTemperature;

        public MyThermalStatus(int i2, int i3, int i4, int i5) throws Throwable {
            this.batteryStatus = i2;
            this.batteryHealth = i3;
            this.batteryLevel = i4;
            this.batteryTemperature = i5;
            int xoThermalTemp = ThermalTempInfoHelper.getXoThermalTemp();
            this.xoTermalTemperature = xoThermalTemp;
            this.isXoTermalValid = ThermalTempInfoHelper.isValidXoThermalValue(xoThermalTemp);
            this.blThermalTemperature = ThermalTempInfoHelper.getBacklightThermalTemp();
            this.isBlThermalValid = ThermalTempInfoHelper.isValidBacklightThermalValue(this.xoTermalTemperature);
        }

        public void update(int i2, int i3, int i4, int i5) throws Throwable {
            this.batteryStatus = i2;
            this.batteryHealth = i3;
            this.batteryLevel = i4;
            int xoThermalTemp = ThermalTempInfoHelper.getXoThermalTemp();
            this.isXoTermalValid = ThermalTempInfoHelper.isValidXoThermalValue(xoThermalTemp);
            int backlightThermalTemp = ThermalTempInfoHelper.getBacklightThermalTemp();
            this.isBlThermalValid = ThermalTempInfoHelper.isValidBacklightThermalValue(backlightThermalTemp);
            if (i5 != this.batteryTemperature || xoThermalTemp != this.xoTermalTemperature) {
                this.batteryTemperature = i5;
                this.xoTermalTemperature = xoThermalTemp;
                ThermalManager.this.checkThermalStateChange(this);
            }
            if (backlightThermalTemp != this.blThermalTemperature) {
                this.blThermalTemperature = backlightThermalTemp;
                ThermalManager.this.checkBlThermalStateChange(this);
            }
            if (this.isXoTermalValid && ThermalManager.mThermalCollectorEnable && ThermalManager.this.mThermalInfoCollector != null) {
                ThermalManager.this.mThermalInfoCollector.checkNeedCollectThermalInfo(this.xoTermalTemperature, this.batteryTemperature, this.batteryLevel);
            }
        }
    }

    public static final class ProcessInfo {
        private static final int MIN_STR_ARRAY_LENGTH = 3;
        private static final int NAMA_INDEX = 2;
        private static final int PERCENT_INDEX = 0;
        private String mLineInfo;
        public String mPName;
        public int mPercent;

        public ProcessInfo(String str) {
            String[] strArrSplit = str.split("%|:|/");
            if (3 > strArrSplit.length || strArrSplit[0] == null || strArrSplit[2] == null) {
                this.mPName = "Invalid";
                this.mPercent = -1;
            }
            this.mPName = strArrSplit[2];
            try {
                this.mPercent = Float.valueOf(Float.parseFloat(strArrSplit[0].trim()) * 10.0f).intValue();
            } catch (NumberFormatException e2) {
                Log.e(ThermalManager.TAG, "ProcessInfo " + e2);
                this.mPercent = -1;
            }
            this.mLineInfo = str;
        }

        public static boolean isValidProInfoLine(String str) {
            if (str != null) {
                String[] strArrSplit = str.split("%|:|/");
                if (3 <= strArrSplit.length && strArrSplit[0] != null && strArrSplit[2] != null) {
                    return true;
                }
            }
            return false;
        }

        public String lineToString() {
            return "line" + this.mLineInfo;
        }

        public String toString() {
            return "ProcessInfo{pName='" + this.mPName + "', percent=" + this.mPercent + " \n\t\t line" + this.mLineInfo + '}';
        }
    }

    class ProcessStatus {
        long cpuTime;
        int pid;
        long pidCpuTime;

        public ProcessStatus(int i2, long j2, long j3) {
            this.pid = i2;
            this.cpuTime = j2;
            this.pidCpuTime = j3;
        }
    }

    class ProcessStatusMsg {
        long lastTime;
        String packageName;
        long totalBytes;
        int uId;
        long timeChangeBackground = 0;
        int pollCount = 0;
        int activeCount = 0;
        long speedTrans = 0;
        List<ProcessStatus> procList = new ArrayList();

        public ProcessStatusMsg(int i2, String str, List<Integer> list) {
            this.uId = i2;
            this.packageName = str;
            this.totalBytes = ThermalManager.this.getTotalBytesByUid(i2);
            int size = list.size();
            for (int i3 = 0; i3 < size; i3++) {
                int iIntValue = list.get(i3).intValue();
                this.procList.add(ThermalManager.this.new ProcessStatus(iIntValue, Utils.getCpuTime(), Utils.getProcessCpuTime(iIntValue)));
            }
        }

        public void setTimeWhenProcessChanged(boolean z2) {
            if (z2) {
                this.timeChangeBackground = 0L;
            } else {
                this.timeChangeBackground = System.currentTimeMillis();
            }
        }
    }

    private class TempStateListener extends FileObserver {
        public TempStateListener(String str) throws Throwable {
            super(str);
            ThermalManager.this.updateTempState(str);
        }

        @Override // android.os.FileObserver
        public void onEvent(int i2, String str) throws Throwable {
            if (i2 != 2) {
                return;
            }
            ThermalManager.this.updateTempState(str);
        }
    }

    static class ThermalPerfLimits {
        int midLevel;
        int norLevel;
        String pkgName;

        public ThermalPerfLimits(String str, int i2, int i3) {
            this.pkgName = str;
            this.norLevel = i2;
            this.midLevel = i3;
        }
    }

    public static class ThermalTempInfoHelper {
        private static final String BASE_PATH = "sys/class/thermal/thermal_zone";
        private static final int MAX_QUERY_LIMIT = -10;
        private static final String TAG = "ThermalTempInfoHelper";
        private static final String THERMAL_ZONE_TEMP_FILE = "/sys/class/thermal/thermal_zone%d/temp";
        private static int amb_thermal_index = -1;
        private static int backlight_thermal_index = -1;
        private static int tzNums = 0;
        private static int xo_thermal_index = -1;

        private ThermalTempInfoHelper() {
        }

        public static int getAmbientTemp() {
            String fileString = FileUtil.getFileString("sys/class/thermal/thermal_message/ambient_sensor_temp");
            if (Utils.isNumber(fileString)) {
                return Integer.parseInt(fileString);
            }
            return -1;
        }

        private static int getAmbientThermalIndex() throws Throwable {
            String str;
            StringBuilder sb;
            int i2 = amb_thermal_index;
            if (-1 < i2 || -10 > i2) {
                return i2;
            }
            BufferedReader bufferedReader = null;
            int i3 = 0;
            while (i3 < tzNums) {
                try {
                    try {
                        File file = new File(BASE_PATH + i3 + "/type");
                        if (!file.exists()) {
                            if (bufferedReader != null) {
                                try {
                                    bufferedReader.close();
                                } catch (IOException e2) {
                                    Log.e(TAG, "getAmbientThermalIndex" + e2);
                                }
                            }
                            return 0;
                        }
                        BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                        try {
                            String line = bufferedReader2.readLine();
                            if (ThermalManager.DEBUG) {
                                Log.d(TAG, "ambient thermal sensor" + i3 + " type is " + line);
                            }
                            if (isAmbientThermal(line)) {
                                amb_thermal_index = i3;
                                bufferedReader = bufferedReader2;
                                break;
                            }
                            bufferedReader2.close();
                            i3++;
                            bufferedReader = bufferedReader2;
                        } catch (FileNotFoundException e3) {
                            e = e3;
                            bufferedReader = bufferedReader2;
                            Log.e(TAG, "FileNotFoundException" + e);
                            if (bufferedReader != null) {
                                try {
                                    bufferedReader.close();
                                } catch (IOException e4) {
                                    e = e4;
                                    str = TAG;
                                    sb = new StringBuilder();
                                    sb.append("getAmbientThermalIndex");
                                    sb.append(e);
                                    Log.e(str, sb.toString());
                                }
                            }
                        } catch (IOException e5) {
                            e = e5;
                            bufferedReader = bufferedReader2;
                            Log.e(TAG, "IOException" + e);
                            if (bufferedReader != null) {
                                try {
                                    bufferedReader.close();
                                } catch (IOException e6) {
                                    e = e6;
                                    str = TAG;
                                    sb = new StringBuilder();
                                    sb.append("getAmbientThermalIndex");
                                    sb.append(e);
                                    Log.e(str, sb.toString());
                                }
                            }
                        } catch (Throwable th) {
                            th = th;
                            bufferedReader = bufferedReader2;
                            if (bufferedReader != null) {
                                try {
                                    bufferedReader.close();
                                } catch (IOException e7) {
                                    Log.e(TAG, "getAmbientThermalIndex" + e7);
                                }
                            }
                            throw th;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                    }
                } catch (FileNotFoundException e8) {
                    e = e8;
                } catch (IOException e9) {
                    e = e9;
                }
            }
            int i4 = amb_thermal_index;
            if (i4 < 0) {
                amb_thermal_index = i4 - 1;
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e10) {
                    e = e10;
                    str = TAG;
                    sb = new StringBuilder();
                    sb.append("getAmbientThermalIndex");
                    sb.append(e);
                    Log.e(str, sb.toString());
                }
            }
            return amb_thermal_index;
        }

        public static int getAmbientThermalTemp() throws Throwable {
            File file = new File("sys/class/thermal/thermal_message/ambient_sensor_temp");
            if (!file.exists()) {
                ThermalManager.mAmbientTempSupport = false;
                return 0;
            }
            int ambientThermal = readAmbientThermal(file);
            if (!ThermalManager.DEBUG) {
                return ambientThermal;
            }
            Log.d(TAG, "ambient thermal sensor board temp is " + ambientThermal);
            return ambientThermal;
        }

        private static int getBacklightThermalIndex() throws Throwable {
            String str;
            StringBuilder sb;
            int i2 = backlight_thermal_index;
            if (-1 < i2 || -10 > i2) {
                return i2;
            }
            BufferedReader bufferedReader = null;
            boolean z2 = false;
            for (int i3 = 0; i3 < tzNums; i3++) {
                try {
                    try {
                        File file = new File(BASE_PATH + i3 + "/type");
                        if (file.exists()) {
                            BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                            try {
                                String line = bufferedReader2.readLine();
                                if (ThermalManager.DEBUG) {
                                    Log.d(TAG, "thermal sensor" + i3 + " type is " + line);
                                }
                                if (isBacklightThermal(line)) {
                                    z2 = true;
                                }
                                bufferedReader2.close();
                                bufferedReader = bufferedReader2;
                            } catch (FileNotFoundException e2) {
                                e = e2;
                                bufferedReader = bufferedReader2;
                                Log.e(TAG, "getBacklightThermalIndex" + e);
                                if (bufferedReader != null) {
                                    try {
                                        bufferedReader.close();
                                    } catch (IOException e3) {
                                        e = e3;
                                        str = TAG;
                                        sb = new StringBuilder();
                                        sb.append("getBacklightThermalIndex");
                                        sb.append(e);
                                        Log.e(str, sb.toString());
                                    }
                                }
                            } catch (IOException e4) {
                                e = e4;
                                bufferedReader = bufferedReader2;
                                Log.e(TAG, "getBacklightThermalIndex" + e);
                                if (bufferedReader != null) {
                                    try {
                                        bufferedReader.close();
                                    } catch (IOException e5) {
                                        e = e5;
                                        str = TAG;
                                        sb = new StringBuilder();
                                        sb.append("getBacklightThermalIndex");
                                        sb.append(e);
                                        Log.e(str, sb.toString());
                                    }
                                }
                            } catch (Throwable th) {
                                th = th;
                                bufferedReader = bufferedReader2;
                                if (bufferedReader != null) {
                                    try {
                                        bufferedReader.close();
                                    } catch (IOException e6) {
                                        Log.e(TAG, "getBacklightThermalIndex" + e6);
                                    }
                                }
                                throw th;
                            }
                        }
                        if (z2 && z2) {
                            backlight_thermal_index = i3;
                            break;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                    }
                } catch (FileNotFoundException e7) {
                    e = e7;
                } catch (IOException e8) {
                    e = e8;
                }
            }
            int i4 = backlight_thermal_index;
            if (i4 < 0) {
                backlight_thermal_index = i4 - 1;
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e9) {
                    e = e9;
                    str = TAG;
                    sb = new StringBuilder();
                    sb.append("getBacklightThermalIndex");
                    sb.append(e);
                    Log.e(str, sb.toString());
                }
            }
            return backlight_thermal_index;
        }

        /* JADX WARN: Code duplicated, block: B:56:0x00fe A[EXC_TOP_SPLITTER, SYNTHETIC] */
        public static int getBacklightThermalTemp() throws Throwable {
            Throwable th;
            BufferedReader bufferedReader;
            IOException e2;
            FileNotFoundException e3;
            String str;
            StringBuilder sb;
            int i2 = 0;
            if (getBacklightThermalIndex() < 0) {
                return 0;
            }
            BufferedReader bufferedReader2 = null;
            try {
                try {
                    File file = new File(BASE_PATH + getBacklightThermalIndex() + ThermalManager.DEFAULT_TEMP_PATH);
                    if (file.exists()) {
                        StringBuffer stringBuffer = new StringBuffer();
                        bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                        try {
                            for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                                stringBuffer.append(line);
                            }
                            i2 = Integer.parseInt(stringBuffer.toString());
                            if (ThermalManager.DEBUG) {
                                Log.d(TAG, "thermal sensor" + getBacklightThermalIndex() + " temp is " + i2);
                            }
                            bufferedReader2 = bufferedReader;
                        } catch (FileNotFoundException e4) {
                            e3 = e4;
                            Log.e(TAG, "getBacklightThermalTemp" + e3);
                            if (bufferedReader != null) {
                                try {
                                    bufferedReader.close();
                                } catch (IOException e5) {
                                    e = e5;
                                    str = TAG;
                                    sb = new StringBuilder();
                                    sb.append("getBacklightThermalTemp");
                                    sb.append(e);
                                    Log.e(str, sb.toString());
                                }
                            }
                        } catch (IOException e6) {
                            e2 = e6;
                            Log.e(TAG, "getBacklightThermalTemp" + e2);
                            if (bufferedReader != null) {
                                try {
                                    bufferedReader.close();
                                } catch (IOException e7) {
                                    e = e7;
                                    str = TAG;
                                    sb = new StringBuilder();
                                    sb.append("getBacklightThermalTemp");
                                    sb.append(e);
                                    Log.e(str, sb.toString());
                                }
                            }
                        }
                    }
                    if (bufferedReader2 != null) {
                        try {
                            bufferedReader2.close();
                        } catch (IOException e8) {
                            e = e8;
                            str = TAG;
                            sb = new StringBuilder();
                            sb.append("getBacklightThermalTemp");
                            sb.append(e);
                            Log.e(str, sb.toString());
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                    if (0 != 0) {
                        try {
                            bufferedReader2.close();
                        } catch (IOException e9) {
                            Log.e(TAG, "getBacklightThermalTemp" + e9);
                        }
                    }
                    throw th;
                }
            } catch (FileNotFoundException e10) {
                bufferedReader = null;
                e3 = e10;
            } catch (IOException e11) {
                bufferedReader = null;
                e2 = e11;
            } catch (Throwable th3) {
                th = th3;
                if (0 != 0) {
                    bufferedReader2.close();
                }
                throw th;
            }
            return i2;
        }

        public static String getBoardFile() {
            if (ThermalManager.mOtherBoardTempSupport) {
                if (new File("sys/class/thermal/thermal_message/board_sensor_other_temp").exists()) {
                    return "board_sensor_other_temp";
                }
                ThermalManager.mOtherBoardTempSupport = false;
            }
            if (!ThermalManager.mBoardTempSupport) {
                return "xo_temp";
            }
            if (new File("sys/class/thermal/thermal_message/board_sensor_temp").exists()) {
                return "board_sensor_temp";
            }
            ThermalManager.mOtherBoardTempSupport = false;
            return "xo_temp";
        }

        /* JADX WARN: Code duplicated, block: B:78:0x010b A[EXC_TOP_SPLITTER, SYNTHETIC] */
        public static int getBoardThermalTemp() throws Throwable {
            Throwable th;
            BufferedReader bufferedReader;
            IOException e2;
            FileNotFoundException e3;
            String str;
            StringBuilder sb;
            BufferedReader bufferedReader2 = null;
            int i2 = 0;
            try {
                try {
                    File file = new File("sys/class/thermal/thermal_message/board_sensor_temp");
                    if (file.exists()) {
                        StringBuffer stringBuffer = new StringBuffer();
                        bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                        try {
                            for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                                stringBuffer.append(line);
                            }
                            try {
                                i2 = Integer.parseInt(stringBuffer.toString());
                                if (ThermalManager.DEBUG) {
                                    Log.d(TAG, "thermal sensor board temp is " + i2);
                                }
                                bufferedReader2 = bufferedReader;
                            } catch (NumberFormatException unused) {
                                ThermalManager.mBoardTempSupport = false;
                                try {
                                    bufferedReader.close();
                                } catch (IOException e4) {
                                    Log.e(TAG, "getBoardThermalTemp" + e4);
                                }
                                return 0;
                            }
                        } catch (FileNotFoundException e5) {
                            e3 = e5;
                            Log.e(TAG, "getBoardThermalTemp" + e3);
                            if (bufferedReader != null) {
                                try {
                                    bufferedReader.close();
                                } catch (IOException e6) {
                                    e = e6;
                                    str = TAG;
                                    sb = new StringBuilder();
                                    sb.append("getBoardThermalTemp");
                                    sb.append(e);
                                    Log.e(str, sb.toString());
                                }
                            }
                        } catch (IOException e7) {
                            e2 = e7;
                            Log.e(TAG, "getBoardThermalTemp" + e2);
                            if (bufferedReader != null) {
                                try {
                                    bufferedReader.close();
                                } catch (IOException e8) {
                                    e = e8;
                                    str = TAG;
                                    sb = new StringBuilder();
                                    sb.append("getBoardThermalTemp");
                                    sb.append(e);
                                    Log.e(str, sb.toString());
                                }
                            }
                        }
                    } else {
                        ThermalManager.mBoardTempSupport = false;
                    }
                    if (bufferedReader2 != null) {
                        try {
                            bufferedReader2.close();
                        } catch (IOException e9) {
                            e = e9;
                            str = TAG;
                            sb = new StringBuilder();
                            sb.append("getBoardThermalTemp");
                            sb.append(e);
                            Log.e(str, sb.toString());
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                    if (0 != 0) {
                        try {
                            bufferedReader2.close();
                        } catch (IOException e10) {
                            Log.e(TAG, "getBoardThermalTemp" + e10);
                        }
                    }
                    throw th;
                }
            } catch (FileNotFoundException e11) {
                bufferedReader = null;
                e3 = e11;
            } catch (IOException e12) {
                bufferedReader = null;
                e2 = e12;
            } catch (Throwable th3) {
                th = th3;
                if (0 != 0) {
                    bufferedReader2.close();
                }
                throw th;
            }
            if (ThermalManager.mXoTempDiv == 1 && (i2 > 1000 || i2 < -1000)) {
                ThermalManager.mXoTempDiv = 1000;
            }
            return i2 / ThermalManager.mXoTempDiv;
        }

        public static String getFlashTemp() {
            return FileUtil.getFileString(String.format(Locale.CHINA, "/sys/class/thermal/thermal_zone%d/temp", Integer.valueOf(ThermalPlusCollector.getNTCId("flash_therm"))));
        }

        public static int getOtherBoardTemp() throws Throwable {
            BufferedReader bufferedReader;
            Object e2;
            BufferedReader bufferedReader2 = null;
            int i2 = 0;
            try {
                try {
                    try {
                        File file = new File("sys/class/thermal/thermal_message/board_sensor_other_temp");
                        if (file.exists()) {
                            StringBuilder sb = new StringBuilder();
                            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                            try {
                                for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                                    sb.append(line);
                                }
                                i2 = Integer.parseInt(sb.toString());
                                bufferedReader2 = bufferedReader;
                            } catch (IOException | NumberFormatException e3) {
                                e2 = e3;
                                Log.e(TAG, "get other board thermal temp" + e2);
                                ThermalManager.mOtherBoardTempSupport = false;
                                if (bufferedReader != null) {
                                    try {
                                        bufferedReader.close();
                                    } catch (Exception e4) {
                                        Log.e(TAG, "other board thermal temp close" + e4);
                                    }
                                }
                                return 0;
                            }
                        } else {
                            ThermalManager.mOtherBoardTempSupport = false;
                        }
                        if (bufferedReader2 != null) {
                            try {
                                bufferedReader2.close();
                            } catch (Exception e5) {
                                Log.e(TAG, "other board thermal temp close" + e5);
                            }
                        }
                        if (ThermalManager.mXoTempDiv == 1 && (i2 > 1000 || i2 < -1000)) {
                            ThermalManager.mXoTempDiv = 1000;
                        }
                        return i2 / ThermalManager.mXoTempDiv;
                    } catch (Throwable th) {
                        th = th;
                        if (0 != 0) {
                            try {
                                bufferedReader2.close();
                            } catch (Exception e6) {
                                Log.e(TAG, "other board thermal temp close" + e6);
                            }
                        }
                        throw th;
                    }
                } catch (IOException | NumberFormatException e7) {
                    bufferedReader = null;
                    e2 = e7;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void getThermalZoneNums() {
            for (int i2 = 0; i2 < 100; i2++) {
                File file = new File(BASE_PATH + i2);
                if (!file.exists() && !file.isDirectory()) {
                    tzNums = i2;
                    return;
                }
            }
        }

        public static int getVirtualBoardSensorTemp() {
            String fileString = FileUtil.getFileString("/sys/class/thermal/thermal_message/board_sensor_temp");
            if (Utils.isNumber(fileString)) {
                return Integer.parseInt(fileString);
            }
            return -1;
        }

        private static int getXoThermalIndex() throws Throwable {
            String str;
            StringBuilder sb;
            int i2 = xo_thermal_index;
            if (-1 < i2 || -10 > i2) {
                return i2;
            }
            BufferedReader bufferedReader = null;
            boolean z2 = false;
            for (int i3 = 0; i3 < tzNums; i3++) {
                try {
                    try {
                        File file = new File(BASE_PATH + i3 + "/type");
                        if (file.exists()) {
                            BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                            try {
                                String line = bufferedReader2.readLine();
                                if (ThermalManager.DEBUG) {
                                    Log.d(TAG, "thermal sensor" + i3 + " type is " + line);
                                }
                                if (isXoThermal(line)) {
                                    z2 = true;
                                }
                                bufferedReader2.close();
                                bufferedReader = bufferedReader2;
                            } catch (FileNotFoundException e2) {
                                e = e2;
                                bufferedReader = bufferedReader2;
                                Log.e(TAG, "getXoThermalIndex" + e);
                                if (bufferedReader != null) {
                                    try {
                                        bufferedReader.close();
                                    } catch (IOException e3) {
                                        e = e3;
                                        str = TAG;
                                        sb = new StringBuilder();
                                        sb.append("getXoThermalIndex");
                                        sb.append(e);
                                        Log.e(str, sb.toString());
                                    }
                                }
                            } catch (IOException e4) {
                                e = e4;
                                bufferedReader = bufferedReader2;
                                Log.e(TAG, "getXoThermalIndex" + e);
                                if (bufferedReader != null) {
                                    try {
                                        bufferedReader.close();
                                    } catch (IOException e5) {
                                        e = e5;
                                        str = TAG;
                                        sb = new StringBuilder();
                                        sb.append("getXoThermalIndex");
                                        sb.append(e);
                                        Log.e(str, sb.toString());
                                    }
                                }
                            } catch (Throwable th) {
                                th = th;
                                bufferedReader = bufferedReader2;
                                if (bufferedReader != null) {
                                    try {
                                        bufferedReader.close();
                                    } catch (IOException e6) {
                                        Log.e(TAG, "getXoThermalIndex" + e6);
                                    }
                                }
                                throw th;
                            }
                        }
                        if (z2 && z2) {
                            xo_thermal_index = i3;
                            break;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                    }
                } catch (FileNotFoundException e7) {
                    e = e7;
                } catch (IOException e8) {
                    e = e8;
                }
            }
            int i4 = xo_thermal_index;
            if (i4 < 0) {
                xo_thermal_index = i4 - 1;
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e9) {
                    e = e9;
                    str = TAG;
                    sb = new StringBuilder();
                    sb.append("getXoThermalIndex");
                    sb.append(e);
                    Log.e(str, sb.toString());
                }
            }
            return xo_thermal_index;
        }

        /* JADX WARN: Code duplicated, block: B:76:0x012b A[EXC_TOP_SPLITTER, SYNTHETIC] */
        public static int getXoThermalTemp() throws Throwable {
            Throwable th;
            BufferedReader bufferedReader;
            IOException e2;
            FileNotFoundException e3;
            String str;
            StringBuilder sb;
            if (ThermalManager.mOtherBoardTempSupport) {
                return getOtherBoardTemp();
            }
            if (ThermalManager.mBoardTempSupport) {
                return getBoardThermalTemp();
            }
            int i2 = 0;
            if (getXoThermalIndex() < 0) {
                return 0;
            }
            BufferedReader bufferedReader2 = null;
            try {
                try {
                    File file = new File(BASE_PATH + getXoThermalIndex() + ThermalManager.DEFAULT_TEMP_PATH);
                    if (file.exists()) {
                        StringBuffer stringBuffer = new StringBuffer();
                        bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                        try {
                            for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                                stringBuffer.append(line);
                            }
                            i2 = Integer.parseInt(stringBuffer.toString());
                            if (ThermalManager.DEBUG) {
                                Log.d(TAG, "thermal sensor" + getXoThermalIndex() + " temp is " + i2);
                            }
                            bufferedReader2 = bufferedReader;
                        } catch (FileNotFoundException e4) {
                            e3 = e4;
                            Log.e(TAG, "getXoThermalTemp" + e3);
                            if (bufferedReader != null) {
                                try {
                                    bufferedReader.close();
                                } catch (IOException e5) {
                                    e = e5;
                                    str = TAG;
                                    sb = new StringBuilder();
                                    sb.append("getXoThermalTemp");
                                    sb.append(e);
                                    Log.e(str, sb.toString());
                                }
                            }
                        } catch (IOException e6) {
                            e2 = e6;
                            Log.e(TAG, "getXoThermalTemp" + e2);
                            if (bufferedReader != null) {
                                try {
                                    bufferedReader.close();
                                } catch (IOException e7) {
                                    e = e7;
                                    str = TAG;
                                    sb = new StringBuilder();
                                    sb.append("getXoThermalTemp");
                                    sb.append(e);
                                    Log.e(str, sb.toString());
                                }
                            }
                        }
                    }
                    if (bufferedReader2 != null) {
                        try {
                            bufferedReader2.close();
                        } catch (IOException e8) {
                            e = e8;
                            str = TAG;
                            sb = new StringBuilder();
                            sb.append("getXoThermalTemp");
                            sb.append(e);
                            Log.e(str, sb.toString());
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                    if (0 != 0) {
                        try {
                            bufferedReader2.close();
                        } catch (IOException e9) {
                            Log.e(TAG, "getXoThermalTemp" + e9);
                        }
                    }
                    throw th;
                }
            } catch (FileNotFoundException e10) {
                bufferedReader = null;
                e3 = e10;
            } catch (IOException e11) {
                bufferedReader = null;
                e2 = e11;
            } catch (Throwable th3) {
                th = th3;
                if (0 != 0) {
                    bufferedReader2.close();
                }
                throw th;
            }
            if (ThermalManager.mXoTempDiv == 1 && (i2 > 1000 || i2 < -1000)) {
                ThermalManager.mXoTempDiv = 1000;
            }
            return i2 / ThermalManager.mXoTempDiv;
        }

        private static boolean isAmbientThermal(String str) {
            if (str == null || !str.contains("quiet_therm")) {
                return false;
            }
            if (!ThermalManager.DEBUG) {
                return true;
            }
            Log.d(TAG, "isAmbientThermal TRUE");
            return true;
        }

        private static boolean isBacklightThermal(String str) {
            return str != null && str.contains("backlight_therm");
        }

        public static boolean isValidBacklightThermalValue(int i2) {
            return i2 != 0;
        }

        public static boolean isValidXoThermalValue(int i2) {
            return i2 != 0;
        }

        private static boolean isXoThermal(String str) {
            return str != null && str.contains("xo_therm");
        }

        public static int readAmbientThermal(File file) throws Throwable {
            String str;
            StringBuilder sb;
            int i2 = 0;
            BufferedReader bufferedReader = null;
            try {
                try {
                    StringBuilder sb2 = new StringBuilder();
                    BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                    try {
                        for (String line = bufferedReader2.readLine(); line != null; line = bufferedReader2.readLine()) {
                            sb2.append(line);
                        }
                        i2 = Integer.parseInt(sb2.toString());
                        try {
                            bufferedReader2.close();
                        } catch (Exception e2) {
                            e = e2;
                            str = TAG;
                            sb = new StringBuilder();
                            sb.append("getAmbientThermalTemp IOException");
                            sb.append(e);
                            Log.e(str, sb.toString());
                        }
                    } catch (FileNotFoundException e3) {
                        e = e3;
                        bufferedReader = bufferedReader2;
                        Log.e(TAG, "getAmbientThermalTemp FileNotFoundException" + e);
                        ThermalManager.mAmbientTempSupport = false;
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (Exception e4) {
                                e = e4;
                                str = TAG;
                                sb = new StringBuilder();
                                sb.append("getAmbientThermalTemp IOException");
                                sb.append(e);
                                Log.e(str, sb.toString());
                            }
                        }
                    } catch (Exception e5) {
                        e = e5;
                        bufferedReader = bufferedReader2;
                        Log.e(TAG, "getAmbientThermalTemp IOException" + e);
                        ThermalManager.mAmbientTempSupport = false;
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (Exception e6) {
                                e = e6;
                                str = TAG;
                                sb = new StringBuilder();
                                sb.append("getAmbientThermalTemp IOException");
                                sb.append(e);
                                Log.e(str, sb.toString());
                            }
                        }
                    } catch (Throwable th) {
                        th = th;
                        bufferedReader = bufferedReader2;
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (Exception e7) {
                                Log.e(TAG, "getAmbientThermalTemp IOException" + e7);
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (FileNotFoundException e8) {
                e = e8;
            } catch (Exception e9) {
                e = e9;
            }
            return i2;
        }

        public static int readQuietThermal(File file) throws Throwable {
            String str;
            StringBuilder sb;
            BufferedReader bufferedReader = null;
            try {
                try {
                    StringBuilder sb2 = new StringBuilder();
                    BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                    try {
                        for (String line = bufferedReader2.readLine(); line != null; line = bufferedReader2.readLine()) {
                            sb2.append(line);
                        }
                        int i2 = Integer.parseInt(sb2.toString());
                        try {
                            bufferedReader2.close();
                            return i2;
                        } catch (Exception e2) {
                            Log.e(TAG, "get Ambient Thermal Temp" + e2);
                            return i2;
                        }
                    } catch (FileNotFoundException e3) {
                        e = e3;
                        bufferedReader = bufferedReader2;
                        Log.e(TAG, "Ambient Thermal FileNotFoundException" + e);
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (Exception e4) {
                                e = e4;
                                str = TAG;
                                sb = new StringBuilder();
                                sb.append("get Ambient Thermal Temp");
                                sb.append(e);
                                Log.e(str, sb.toString());
                                return 0;
                            }
                        }
                        return 0;
                    } catch (Exception e5) {
                        e = e5;
                        bufferedReader = bufferedReader2;
                        Log.e(TAG, "Ambient Thermal IOException" + e);
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (Exception e6) {
                                e = e6;
                                str = TAG;
                                sb = new StringBuilder();
                                sb.append("get Ambient Thermal Temp");
                                sb.append(e);
                                Log.e(str, sb.toString());
                                return 0;
                            }
                        }
                        return 0;
                    } catch (Throwable th) {
                        th = th;
                        bufferedReader = bufferedReader2;
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (Exception e7) {
                                Log.e(TAG, "get Ambient Thermal Temp" + e7);
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (FileNotFoundException e8) {
                e = e8;
            } catch (Exception e9) {
                e = e9;
            }
        }
    }

    private class TorchRealLevelListener extends FileObserver {
        public TorchRealLevelListener(String str) {
            super(str);
        }

        @Override // android.os.FileObserver
        public void onEvent(int i2, String str) {
            int torchRealLevel;
            if (i2 == 2 && ThermalManager.this.mPrevUploadTorchLevel != (torchRealLevel = ThermalManager.this.getTorchRealLevel())) {
                ThermalManager.this.mPrevUploadTorchLevel = torchRealLevel;
                ThermalManager.this.uploadTorchBrightnessRecord(torchRealLevel);
            }
        }
    }

    private ThermalManager(Context context, Handler handler) {
        this.killSomeProcessConfig = null;
        this.killAllProcessConfig = null;
        this.mPrevUploadTorchLevel = -1;
        this.mThermalCloudObserver = new ContentObserver(this.mHandler) { // from class: com.miui.powerkeeper.feedbackcontrol.ThermalManager.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z2) {
                super.onChange(z2);
                ThermalManager.this.updateThermalConfig();
            }
        };
        this.mThermalPerfLimitsObserver = new ContentObserver(this.mHandler) { // from class: com.miui.powerkeeper.feedbackcontrol.ThermalManager.2
            @Override // android.database.ContentObserver
            public void onChange(boolean z2) {
                super.onChange(z2);
                ThermalManager thermalManager = ThermalManager.this;
                thermalManager.loadPerfLimitsConfig(thermalManager.mContext);
            }
        };
        this.mContext = context;
        this.mPrevUploadTorchLevel = getTorchRealLevel();
        setIECState(ThermalIECHandler.getIECState());
        this.mHandler = handler;
        ThermalTempInfoHelper.getThermalZoneNums();
        this.mThermalInfoCollector = ThermalInfoCollector.get(this.mContext);
        this.mContext.getContentResolver().registerContentObserver(SimpleSettings.Misc.getUriFor(SimpleSettingKeys.KEY_THERMAL_LAST_DATA_MD5), false, this.mThermalCloudObserver);
        this.mContext.getContentResolver().registerContentObserver(SimpleSettings.Misc.getUriFor(SimpleSettingKeys.KEY_THERMAL_PERF_LIMITS), false, this.mThermalPerfLimitsObserver);
        this.mHandler.post(new Runnable() { // from class: com.miui.powerkeeper.feedbackcontrol.ThermalManager.3
            @Override // java.lang.Runnable
            public void run() {
                if (DisplayFrameSetting.isFeatureOn()) {
                    ThermalManager.this.mDisplayFeatureManager = DisplayFeatureManager.getInstance();
                }
                ThermalManager.this.updateThermalConfig();
                ThermalManager thermalManager = ThermalManager.this;
                thermalManager.mHighTempRange = thermalManager.new HighTempRange();
                ThermalManager thermalManager2 = ThermalManager.this;
                thermalManager2.loadPerfLimitsConfig(thermalManager2.mContext);
                ThermalManager.this.mTempStateListener = ThermalManager.this.new TempStateListener(ThermalManager.TEMP_STATE_FILE);
                ThermalManager.this.mTempStateListener.startWatching();
                ThermalManager.this.mTorchRealLevelListener = ThermalManager.this.new TorchRealLevelListener(ThermalManager.TORCH_REAL_LEVEL_PATH);
                ThermalManager.this.mTorchRealLevelListener.startWatching();
            }
        });
        if (PowerKeeperManager.getInstance() != null && PowerKeeperManager.getInstance().getEventsAggregator() != null) {
            PowerKeeperManager.getInstance().getEventsAggregator().registerForForegroundInfo(this.mHandler);
            PowerKeeperManager.getInstance().getEventsAggregator().registerIECStateBatteryChanged(this.mHandler);
            PowerKeeperManager.getInstance().getEventsAggregator().registerIECStateChanged(this.mHandler);
        }
        for (Field field : ProcessConfig.class.getDeclaredFields()) {
            if (field.getName() == "POLICY_AUTO_THERMAL_KILL_ALL_LEVEL_1") {
                try {
                    this.killSomeProcessConfig = new ProcessConfig(((Integer) field.get(ProcessConfig.class.getClass())).intValue());
                } catch (IllegalAccessException e2) {
                    e2.printStackTrace();
                }
            }
            if (field.getName() == "POLICY_AUTO_THERMAL_KILL_ALL_LEVEL_2") {
                try {
                    this.killAllProcessConfig = new ProcessConfig(((Integer) field.get(ProcessConfig.class.getClass())).intValue());
                } catch (IllegalAccessException e3) {
                    e3.printStackTrace();
                }
            }
        }
    }

    public static boolean ThermalCollectorEnabled() {
        return mThermalCollectorEnable && mFeatureEnabled;
    }

    private void ThermalPerfLimitsArrayAdd(ThermalPerfLimits thermalPerfLimits) {
        synchronized (this.mThermalPerfLimits) {
            this.mThermalPerfLimits.add(thermalPerfLimits);
        }
    }

    private void ThermalPerfLimitsArrayClear() {
        synchronized (this.mThermalPerfLimits) {
            this.mTempThermalPerfLimits = null;
            this.mThermalPerfLimits.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ThermalPerfLimitsArrayScan(String str) {
        synchronized (this.mThermalPerfLimits) {
            try {
                int size = this.mThermalPerfLimits.size();
                for (int i2 = 0; i2 < size; i2++) {
                    ThermalPerfLimits thermalPerfLimits = this.mThermalPerfLimits.get(i2);
                    if (!thermalPerfLimits.pkgName.equals("default")) {
                        if (thermalPerfLimits.pkgName.equals(str)) {
                            this.mTempThermalPerfLimits = thermalPerfLimits;
                            break;
                        }
                    } else {
                        this.mTempThermalPerfLimits = thermalPerfLimits;
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ThermalPerfLimitsArraySend(Context context) {
        synchronized (this.mThermalPerfLimits) {
            try {
                ThermalPerfLimits thermalPerfLimits = this.mTempThermalPerfLimits;
                if (thermalPerfLimits != null) {
                    int i2 = 2;
                    if (this.lowCapLevel > this.lowCapLevelTh) {
                        int i3 = this.mPerfLimitsTemp;
                        if (i3 < thermalPerfLimits.norLevel) {
                            i2 = 0;
                        } else if (i3 < thermalPerfLimits.midLevel) {
                            i2 = 1;
                        }
                    }
                    if (i2 == this.mPerfLimitsLevelSave) {
                        return;
                    }
                    this.mPerfLimitsLevelSave = i2;
                    Intent intent = new Intent("action_thermal_control_change");
                    intent.putExtra("cur_level", i2);
                    context.sendStickyBroadcast(intent);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void catchPowerLogsWhenHighTemp() {
        if (this.mIsCatchBugReport && getPlugType() == 0) {
            Intent intent = new Intent("upload_one_bugreport");
            intent.setComponent(new ComponentName("com.miui.klo.bugreport", "com.miui.klo.bugreport.service.FeedbackBackgroundService"));
            Bundle bundle = new Bundle();
            bundle.putInt("bugreportTypeID", 105);
            bundle.putString("bugreportVariant", "hottempwhenscreenoff");
            intent.putExtras(bundle);
            this.mContext.startServiceAsUser(intent, UserHandle.CURRENT);
            return;
        }
        if (DEBUG) {
            Log.d(TAG, " mIsCatchBugReport = " + this.mIsCatchBugReport + " PlugType " + getPlugType());
        }
    }

    private void cccSendIntent() {
        Intent intent = new Intent("action_temp_state_change");
        int i2 = this.mTempStateNow;
        if (i2 >= 100000) {
            intent.putExtra("temp_state", i2 % AppInfoUtils.PATTERN);
        } else if (i2 == 1 || i2 == 4 || i2 == 5 || i2 == 6) {
            intent.putExtra("temp_state", 1);
        } else {
            intent.putExtra("temp_state", i2);
        }
        this.mContext.sendStickyBroadcast(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkBatteryAndThermalChange(MyThermalStatus myThermalStatus) {
        int i2 = myThermalStatus.xoTermalTemperature;
        boolean z2 = myThermalStatus.isXoTermalValid;
        if (DEBUG) {
            Log.d(TAG, " xo_thermal " + i2 + " valid " + z2);
        }
        synchronized (this.mStatusLock) {
            try {
                if (this.mChargeFlag) {
                    if (z2 && i2 > mXoThermalTempCheckThreshold) {
                        this.mHighTempRange.addTempEvent(myThermalStatus, 8);
                    }
                } else if (z2 && i2 > mXoThermalTempCheckThreshold && isDischargeUploadThermal()) {
                    this.mHighTempRange.addTempEvent(myThermalStatus, 8);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:15:0x004e  */
    /* JADX WARN: Code duplicated, block: B:21:0x0071 A[Catch: all -> 0x004c, TryCatch #0 {all -> 0x004c, blocks: (B:8:0x002b, B:10:0x002f, B:12:0x0033, B:24:0x007f, B:16:0x0050, B:18:0x0054, B:20:0x0058, B:21:0x0071, B:23:0x0075), top: B:28:0x002b }] */
    /* JADX WARN: Code duplicated, block: B:23:0x0075 A[Catch: all -> 0x004c, TryCatch #0 {all -> 0x004c, blocks: (B:8:0x002b, B:10:0x002f, B:12:0x0033, B:24:0x007f, B:16:0x0050, B:18:0x0054, B:20:0x0058, B:21:0x0071, B:23:0x0075), top: B:28:0x002b }] */
    public void checkBlThermalStateChange(final MyThermalStatus myThermalStatus) {
        int i2 = myThermalStatus.blThermalTemperature;
        boolean z2 = myThermalStatus.isBlThermalValid;
        if (DEBUG) {
            Log.d(TAG, "bl_thermal " + i2 + " valid " + z2);
        }
        synchronized (this.mStatusLock) {
            if (z2) {
                try {
                    if (i2 >= this.mBlThermalTemperatureThreshold && !this.mBlThresholdTrigged) {
                        this.mBlThresholdTrigged = true;
                        this.mHandler.obtainMessage(9).sendToTarget();
                        this.mHandler.post(new Runnable() { // from class: com.miui.powerkeeper.feedbackcontrol.ThermalManager.8
                            @Override // java.lang.Runnable
                            public void run() throws Throwable {
                                ThermalManager.this.mHighTempRange.addTempEvent(myThermalStatus, 5);
                            }
                        });
                    } else if (!z2 && i2 < this.mBlThermalTemperatureThreshold && this.mBlThresholdTrigged) {
                        this.mBlThresholdTrigged = false;
                        this.mHandler.obtainMessage(10).sendToTarget();
                        this.mHandler.post(new Runnable() { // from class: com.miui.powerkeeper.feedbackcontrol.ThermalManager.9
                            @Override // java.lang.Runnable
                            public void run() throws Throwable {
                                ThermalManager.this.mHighTempRange.addTempEvent(myThermalStatus, 6);
                            }
                        });
                    } else if (this.mBlThresholdTrigged) {
                        this.mHandler.post(new Runnable() { // from class: com.miui.powerkeeper.feedbackcontrol.ThermalManager.10
                            @Override // java.lang.Runnable
                            public void run() throws Throwable {
                                ThermalManager.this.mHighTempRange.addTempEvent(myThermalStatus, 7);
                            }
                        });
                    }
                } catch (Throwable th) {
                    throw th;
                }
            } else if (!z2) {
                if (this.mBlThresholdTrigged) {
                    this.mHandler.post(new Runnable() { // from class: com.miui.powerkeeper.feedbackcontrol.ThermalManager.10
                        @Override // java.lang.Runnable
                        public void run() throws Throwable {
                            ThermalManager.this.mHighTempRange.addTempEvent(myThermalStatus, 7);
                        }
                    });
                }
            } else if (this.mBlThresholdTrigged) {
                this.mHandler.post(new Runnable() { // from class: com.miui.powerkeeper.feedbackcontrol.ThermalManager.10
                    @Override // java.lang.Runnable
                    public void run() throws Throwable {
                        ThermalManager.this.mHighTempRange.addTempEvent(myThermalStatus, 7);
                    }
                });
            }
        }
    }

    private void checkChargeThermalUpload() {
        if (!this.mThermalEnable) {
            if (this.mChargeFlag) {
                removeChargeMessages();
                this.mChargeFlag = false;
            }
            unRegisterScreenChanged();
            this.mScreenOffTime = 0L;
            return;
        }
        registerScreenChanged();
        if (!Utils.isCharging(this.mContext)) {
            if (this.mChargeFlag) {
                removeChargeMessages();
                this.mChargeFlag = false;
                this.mDisChargeTime = 0L;
                return;
            }
            return;
        }
        if (this.mChargeFlag) {
            return;
        }
        if (DEBUG) {
            Log.d(TAG, "battery charge flag = " + this.mChargeFlag);
        }
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(12), 1800000L);
        this.mChargeFlag = true;
        this.mSubBatteryLevel = 0;
        this.mScreenOffTime = 0L;
    }

    private void checkDischargeThermalUpload(int i2, MyThermalStatus myThermalStatus) {
        try {
            if (DEBUG) {
                Log.d(TAG, "current battery level " + i2);
            }
            if (!this.mThermalEnable || Utils.isCharging(this.mContext)) {
                return;
            }
            if (this.mBatteryLevel - i2 >= 1) {
                checkBatteryDischarge(myThermalStatus);
            }
            this.mBatteryLevel = i2;
        } catch (Exception e2) {
            Log.e(TAG, "check thermal failed: ", e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:18:0x005f A[Catch: all -> 0x0040, TryCatch #0 {all -> 0x0040, blocks: (B:7:0x0033, B:10:0x003b, B:18:0x005f, B:22:0x0066, B:24:0x006b, B:26:0x006f, B:30:0x0096, B:27:0x0088, B:29:0x008c, B:15:0x0042, B:17:0x0046), top: B:34:0x0033 }] */
    /* JADX WARN: Code duplicated, block: B:27:0x0088 A[Catch: all -> 0x0040, TryCatch #0 {all -> 0x0040, blocks: (B:7:0x0033, B:10:0x003b, B:18:0x005f, B:22:0x0066, B:24:0x006b, B:26:0x006f, B:30:0x0096, B:27:0x0088, B:29:0x008c, B:15:0x0042, B:17:0x0046), top: B:34:0x0033 }] */
    /* JADX WARN: Code duplicated, block: B:29:0x008c A[Catch: all -> 0x0040, TryCatch #0 {all -> 0x0040, blocks: (B:7:0x0033, B:10:0x003b, B:18:0x005f, B:22:0x0066, B:24:0x006b, B:26:0x006f, B:30:0x0096, B:27:0x0088, B:29:0x008c, B:15:0x0042, B:17:0x0046), top: B:34:0x0033 }] */
    public void checkThermalStateChange(final MyThermalStatus myThermalStatus) {
        final int i2 = myThermalStatus.batteryTemperature;
        final int i3 = myThermalStatus.xoTermalTemperature;
        final boolean z2 = myThermalStatus.isXoTermalValid;
        if (DEBUG) {
            Log.d(TAG, "current battery temperature:" + i2 + " xo_thermal " + i3 + " valid " + z2);
        }
        synchronized (this.mStatusLock) {
            try {
                int i4 = this.mTemperatureThreshold;
                if (i2 > i4 || (z2 && i3 > this.mXoThermalTemperatureThreshold)) {
                    if (!this.mThresholdTrigged) {
                        this.mThresholdTrigged = true;
                        this.mHandler.obtainMessage(0).sendToTarget();
                        this.mHandler.post(new Runnable() { // from class: com.miui.powerkeeper.feedbackcontrol.ThermalManager.5
                            @Override // java.lang.Runnable
                            public void run() throws Throwable {
                                if (i2 > ThermalManager.this.mTemperatureThreshold) {
                                    ThermalManager.this.mHighTempRange.addTempEvent(myThermalStatus, 0);
                                } else {
                                    if (!z2 || i3 <= ThermalManager.this.mXoThermalTemperatureThreshold) {
                                        return;
                                    }
                                    ThermalManager.this.mHighTempRange.addTempEvent(myThermalStatus, 1);
                                }
                            }
                        });
                    } else if (i2 >= i4 - 1 && ((!z2 || (z2 && i3 < this.mXoThermalTemperatureThreshold - 1)) && this.mThresholdTrigged)) {
                        this.mThresholdTrigged = false;
                        this.mHandler.obtainMessage(1).sendToTarget();
                        this.mHandler.post(new Runnable() { // from class: com.miui.powerkeeper.feedbackcontrol.ThermalManager.6
                            @Override // java.lang.Runnable
                            public void run() throws Throwable {
                                if (i2 < ThermalManager.this.mTemperatureThreshold - 1) {
                                    ThermalManager.this.mHighTempRange.addTempEvent(myThermalStatus, 2);
                                } else {
                                    if (!z2 || i3 >= ThermalManager.this.mXoThermalTemperatureThreshold - 1) {
                                        return;
                                    }
                                    ThermalManager.this.mHighTempRange.addTempEvent(myThermalStatus, 3);
                                }
                            }
                        });
                    } else if (this.mThresholdTrigged) {
                        this.mHandler.post(new Runnable() { // from class: com.miui.powerkeeper.feedbackcontrol.ThermalManager.7
                            @Override // java.lang.Runnable
                            public void run() throws Throwable {
                                ThermalManager.this.mHighTempRange.addTempEvent(myThermalStatus, 4);
                            }
                        });
                    }
                } else if (i2 >= i4 - 1) {
                    if (this.mThresholdTrigged) {
                        this.mHandler.post(new Runnable() { // from class: com.miui.powerkeeper.feedbackcontrol.ThermalManager.7
                            @Override // java.lang.Runnable
                            public void run() throws Throwable {
                                ThermalManager.this.mHighTempRange.addTempEvent(myThermalStatus, 4);
                            }
                        });
                    }
                } else if (this.mThresholdTrigged) {
                    this.mHandler.post(new Runnable() { // from class: com.miui.powerkeeper.feedbackcontrol.ThermalManager.7
                        @Override // java.lang.Runnable
                        public void run() throws Throwable {
                            ThermalManager.this.mHighTempRange.addTempEvent(myThermalStatus, 4);
                        }
                    });
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private void enableAppStateMonitor(boolean z2) {
        if (z2) {
            if (this.mAppStateMonitorEnabled) {
                return;
            }
            this.mAppStateMonitorEnabled = true;
            this.mProcessForegroundCallback = new PowerKeeperInterface.f(this.mHandler, this.mProcessForegroundReceiver);
            PowerKeeperManager.getInstance().registerForegroundActivitiesChangedListener(this.mProcessForegroundCallback);
            this.mProcessDiedCallback = new PowerKeeperInterface.e0(this.mHandler, this.mProcessDiedReceiver);
            PowerKeeperManager.getInstance().registerProcessDiedListener(this.mProcessDiedCallback);
            return;
        }
        if (this.mAppStateMonitorEnabled) {
            this.mAppStateMonitorEnabled = false;
            PowerKeeperManager.getInstance().unregisterForegroundActivitiesChangedListener(this.mProcessForegroundCallback);
            this.mProcessForegroundCallback.a();
            this.mProcessForegroundCallback = null;
            PowerKeeperManager.getInstance().unregisterProcessDiedListener(this.mProcessDiedCallback);
            this.mProcessDiedCallback.a();
            this.mProcessDiedCallback = null;
            synchronized (this.mProcessStatusMsg) {
                this.mProcessStatusMsg.clear();
            }
        }
    }

    private void enablePerfLimitsMonitor(boolean z2) {
        if (z2) {
            if (this.mPerfLimitsEnabled) {
                return;
            }
            this.mPerfLimitsEnabled = true;
            this.tplForegroundCallback = new PowerKeeperInterface.f(this.mHandler, this.mTPLForegroundReceiver);
            PowerKeeperManager.getInstance().registerForegroundActivitiesChangedListener(this.tplForegroundCallback);
            return;
        }
        if (this.mPerfLimitsEnabled) {
            this.mPerfLimitsEnabled = false;
            PowerKeeperManager.getInstance().unregisterForegroundActivitiesChangedListener(this.tplForegroundCallback);
            this.tplForegroundCallback = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enableThermalMonitor(boolean z2) {
        if (!z2) {
            if (this.mThermalMonitorEnabled) {
                this.mThermalMonitorEnabled = false;
                this.mHandler.removeMessages(8);
                return;
            }
            return;
        }
        if (this.mThermalMonitorEnabled) {
            return;
        }
        this.mThermalMonitorEnabled = true;
        synchronized (this.mProcessStatusMsg) {
            try {
                int size = this.mProcessStatusMsg.size();
                for (int i2 = 0; i2 < size; i2++) {
                    ProcessStatusMsg processStatusMsg = this.mProcessStatusMsg.get(i2);
                    if (processStatusMsg.timeChangeBackground != 0) {
                        processStatusMsg.setTimeWhenProcessChanged(false);
                        processStatusMsg.pollCount = 0;
                        processStatusMsg.activeCount = 0;
                        processStatusMsg.lastTime = processStatusMsg.timeChangeBackground;
                        processStatusMsg.totalBytes = getTotalBytesByUid(processStatusMsg.uId);
                        processStatusMsg.speedTrans = 0L;
                        int size2 = processStatusMsg.procList.size();
                        for (int i3 = 0; i3 < size2; i3++) {
                            ProcessStatus processStatus = processStatusMsg.procList.get(i3);
                            processStatus.cpuTime = Utils.getCpuTime();
                            processStatus.pidCpuTime = Utils.getProcessCpuTime(processStatus.pid);
                        }
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        this.mPollingTime = this.mMinPollingTime;
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(8), this.mPollingTime);
    }

    public static int getBatteryCurrentNow(Context context) {
        return ((BatteryManager) context.getSystemService("batterymanager")).getIntProperty(2) / 1000;
    }

    public static int getBatteryLevelThreshold() {
        return mBatteryLevelThreshold;
    }

    private int getKillAllProsBatTemp() {
        return isIECBatteryAllowed() ? this.mIECKillAllBackProBatteryTemp : this.mAllowedKillAllBackProBatteryTempThreshold;
    }

    private int getKillSomeProsBatTemp() {
        return isIECBatteryAllowed() ? this.mIECKillSomeBackProBatteryTemp : this.mAllowedKillSomeBackProBatteryTempThreshold;
    }

    private int getKilledBatteryTemp() {
        return isIECBatteryAllowed() ? this.mIECKillBatteryTemp : this.mAllowedKillBatteryTempThreshold;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getPlugType() {
        Intent intentRegisterReceiver = this.mContext.registerReceiver(null, this.mIntentFilter, 2);
        if (intentRegisterReceiver != null) {
            return intentRegisterReceiver.getIntExtra("plugged", 0);
        }
        if (DEBUG) {
            Log.d(TAG, "getBatteryPluggedType null");
        }
        return 0;
    }

    private long getProcessCpuLoad(ProcessStatusMsg processStatusMsg) {
        ProcessStatusMsg processStatusMsg2 = processStatusMsg;
        int size = processStatusMsg2.procList.size();
        int i2 = 0;
        long j2 = -1;
        while (i2 < size) {
            ProcessStatus processStatus = processStatusMsg2.procList.get(i2);
            long cpuTime = Utils.getCpuTime();
            long processCpuTime = Utils.getProcessCpuTime(processStatus.pid);
            long j3 = processStatus.cpuTime;
            long j4 = cpuTime - j3;
            int i3 = size;
            long j5 = processStatus.pidCpuTime;
            long j6 = processCpuTime - j5;
            if (j3 == -1 || j5 == -1) {
                processStatus.cpuTime = cpuTime;
                processStatus.pidCpuTime = processCpuTime;
            } else if (cpuTime == -1 || processCpuTime == -1 || j4 == 0) {
                Log.e(TAG, "ThermalLog: get cpu load failed");
            } else {
                if (j2 == -1) {
                    j2 = 0;
                }
                j2 += (j6 * 100) / j4;
                processStatus.cpuTime = cpuTime;
                processStatus.pidCpuTime = processCpuTime;
            }
            i2++;
            processStatusMsg2 = processStatusMsg;
            size = i3;
        }
        if (this.mDebug) {
            Log.d(TAG, "ThermalLog: cpu load for process = " + processStatusMsg.packageName + " " + j2);
        }
        return j2;
    }

    private void getThermalParams() {
        this.mEnable = SimpleSettings.Misc.getBoolean(this.mContext, "enable", false);
        this.mXoTempThreshold = SimpleSettings.Misc.getInt(this.mContext, SimpleSettingKeys.KEY_XO_TEMP_THRESHOLD, 45);
        this.mXoTempThresholdclr = SimpleSettings.Misc.getInt(this.mContext, SimpleSettingKeys.KEY_XO_TEMP_THRESHOLDCLR, XO_TEMP_THRESHOLDCLR);
        this.mAppInBackgroundTimeTh = SimpleSettings.Misc.getInt(this.mContext, SimpleSettingKeys.KEY_APP_BG_TIME_THRESHOLD, 300000);
        this.mMaxCheckTime = SimpleSettings.Misc.getInt(this.mContext, SimpleSettingKeys.KEY_APP_BG_MAX_CHECK_TIME, 600000);
        this.mAppStatusCountTh = SimpleSettings.Misc.getInt(this.mContext, SimpleSettingKeys.KEY_APP_STATUS_COUNT_THRESHOLD, 4);
        this.mAppActiveCountTh = SimpleSettings.Misc.getInt(this.mContext, SimpleSettingKeys.KEY_APP_ACTIVE_COUNT_THRESHOLD, 2);
        this.mDownloadSpeedTh = SimpleSettings.Misc.getInt(this.mContext, SimpleSettingKeys.KEY_DOWNLOAD_SPEED_THRESHOLD, 20);
        this.mCpuLoadTh = SimpleSettings.Misc.getInt(this.mContext, SimpleSettingKeys.KEY_CPU_LOAD_THRESHOLD, 10);
        int i2 = SimpleSettings.Misc.getInt(this.mContext, SimpleSettingKeys.KEY_MIN_POLLING_TIME, 60000);
        this.mMinPollingTime = i2;
        if (i2 < 60000) {
            this.mMinPollingTime = 60000;
        }
        int i3 = SimpleSettings.Misc.getInt(this.mContext, SimpleSettingKeys.KEY_MAX_POLLING_TIME, DEFAULT_MAX_POLLING_TIME);
        this.mMaxPollingTime = i3;
        if (i3 < DEFAULT_MAX_POLLING_TIME) {
            this.mMaxPollingTime = DEFAULT_MAX_POLLING_TIME;
        }
        int i4 = this.mMaxPollingTime;
        int i5 = this.mMinPollingTime;
        if (i4 < i5) {
            this.mMaxPollingTime = i5;
        }
        this.mPollingTime = i5;
        this.mDebug = SimpleSettings.Misc.getBoolean(this.mContext, SimpleSettingKeys.KEY_DEBUG_ENABLE, false);
        this.mAllowedKillBatteryTempThreshold = SimpleSettings.Misc.getInt(this.mContext, "allowed_kill_battery_temp_threshhold", 48);
        this.mAllowedKillSomeBackProBatteryTempThreshold = SimpleSettings.Misc.getInt(this.mContext, SimpleSettingKeys.KEY_ALLOWED_KILL_SOME_BACK_PRO_BATTERY_TEMP_THRESHOLD, 48);
        this.mAllowedKillAllBackProBatteryTempThreshold = SimpleSettings.Misc.getInt(this.mContext, SimpleSettingKeys.KEY_ALLOWED_KILL_ALL_BACK_PRO_BATTERY_TEMP_THRESHOLD, 50);
        this.mAllowedShowDialogBatteryTempThreshhold = SimpleSettings.Misc.getInt(this.mContext, SimpleSettingKeys.KEY_ALLOWED_SHOW_DIALOG_TEMP_THRESHHOLD, 48);
        this.mAllowedShowBenchmarkDialogBatteryTempThreshhold = SimpleSettings.Misc.getInt(this.mContext, SimpleSettingKeys.KEY_ALLOWED_SHOW_BENCHMARK_DIALOG_TEMP_THRESHHOLD, 45);
        this.mExitForegroundAppEnable = SimpleSettings.Misc.getBoolean(this.mContext, SimpleSettingKeys.KEY_EXIT_FOREGROUND_APP_ENABLE, true);
        this.mKillSomeBackProEnable = SimpleSettings.Misc.getBoolean(this.mContext, SimpleSettingKeys.KEY_KILL_SOME_BACK_PRO_ENABLE, true);
        this.mKillAllBackProEnable = SimpleSettings.Misc.getBoolean(this.mContext, SimpleSettingKeys.KEY_KILL_ALL_BACK_PRO_ENABLE, true);
        String string = SimpleSettings.Misc.getString(this.mContext, SimpleSettingKeys.KEY_NOT_ALLOWED_KILL_APPS, "");
        if (string != null && string.length() > 1) {
            this.mNotAllowedKillApps = string;
        }
        String string2 = SimpleSettings.Misc.getString(this.mContext, SimpleSettingKeys.KEY_NOT_ALLOWED_KILL_APPS_FOR_NEW_DIALOG, "");
        if (string2 != null && string2.length() > 1) {
            this.mNotAllowedKillAppsForNewDialog = string2;
        }
        this.mExitFGBenchmarkEnable = SimpleSettings.Misc.getBoolean(this.mContext, SimpleSettingKeys.KEY_EXIT_BENCHMARK_ENABLE, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:42:0x008e A[EXC_TOP_SPLITTER, SYNTHETIC] */
    public int getTorchRealLevel() throws Throwable {
        Throwable th;
        BufferedReader bufferedReader;
        Exception e2;
        BufferedReader bufferedReader2 = null;
        try {
            try {
                File file = new File(TORCH_REAL_LEVEL_PATH);
                if (!file.exists()) {
                    return -1;
                }
                StringBuilder sb = new StringBuilder();
                bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                try {
                    for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                        sb.append(line);
                    }
                    int i2 = Integer.parseInt(sb.toString());
                    try {
                        bufferedReader.close();
                    } catch (IOException e3) {
                        Log.e(TAG, "getTorchRealLevel " + e3);
                    }
                    return i2;
                } catch (Exception e4) {
                    e2 = e4;
                }
            } catch (Throwable th2) {
                th = th2;
                if (0 != 0) {
                    try {
                        bufferedReader2.close();
                    } catch (IOException e5) {
                        Log.e(TAG, "getTorchRealLevel " + e5);
                    }
                }
                throw th;
            }
        } catch (Exception e6) {
            bufferedReader = null;
            e2 = e6;
        } catch (Throwable th3) {
            th = th3;
            if (0 != 0) {
                bufferedReader2.close();
            }
            throw th;
        }
        Log.e(TAG, "getTorchRealLevel " + e2);
        if (bufferedReader == null) {
            return -1;
        }
        try {
            bufferedReader.close();
            return -1;
        } catch (IOException e7) {
            Log.e(TAG, "getTorchRealLevel " + e7);
            return -1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public long getTotalBytesByUid(int i2) {
        return TrafficStats.getUidRxBytes(i2) + TrafficStats.getUidTxBytes(i2);
    }

    private void getTransmissionSpeed(ProcessStatusMsg processStatusMsg) {
        long jCurrentTimeMillis = System.currentTimeMillis();
        long j2 = jCurrentTimeMillis - processStatusMsg.lastTime;
        long totalBytesByUid = getTotalBytesByUid(processStatusMsg.uId);
        long j3 = totalBytesByUid - processStatusMsg.totalBytes;
        processStatusMsg.totalBytes = totalBytesByUid;
        processStatusMsg.lastTime = jCurrentTimeMillis;
        if (j2 > 0 && j3 >= 0) {
            processStatusMsg.speedTrans = (1000 * j3) / j2;
        }
        if (this.mDebug) {
            Log.d(TAG, "ThermalLog: timeInterval = " + j2 + " bytesInterval = " + j3 + " speedTrans = " + processStatusMsg.speedTrans);
        }
    }

    public static int getXoTempThreshold() {
        return mXoThermalTempCheckThreshold;
    }

    public static synchronized ThermalManager init(Context context, Handler handler, boolean z2) {
        try {
            if (sInstance == null) {
                sInstance = new ThermalManager(context, handler);
            }
            sInstance.enableMonitor(z2);
        } catch (Throwable th) {
            throw th;
        }
        return sInstance;
    }

    private boolean isAllowedKillFGBenchmark(String str) {
        if (e.o(4) && PerfEngineManager.d().g() != null) {
            return PerfEngineManager.d().g().f(str);
        }
        PowerLog.i(TAG, "isAllowedKillFGBenchmark " + str + ", not allow");
        return false;
    }

    private boolean isAllowedKillPkg(String str) {
        String str2;
        try {
            if (TextUtils.isEmpty(this.mNotAllowedKillApps)) {
                return true;
            }
            String[] strArrSplit = this.mNotAllowedKillApps.split(",");
            SecurityManager securityManager = (SecurityManager) this.mContext.getSystemService(MIPushAccount.PREF_KEY_SECURITY);
            for (String str3 : strArrSplit) {
                if ((securityManager.getApplicationAccessControlEnabled(str3) && !securityManager.checkAccessControlPass(str3) && (str2 = this.mCurrentActivityName) != null && str2.equals(APP_ACESS_LOCK_TOP_ACTIVITY)) || str3.equals(str)) {
                    return false;
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return true;
    }

    private boolean isAllowedKillPkgForNewDialog(String str) {
        String str2;
        try {
            if (TextUtils.isEmpty(this.mNotAllowedKillAppsForNewDialog)) {
                return true;
            }
            String[] strArrSplit = this.mNotAllowedKillAppsForNewDialog.split(",");
            SecurityManager securityManager = (SecurityManager) this.mContext.getSystemService(MIPushAccount.PREF_KEY_SECURITY);
            for (String str3 : strArrSplit) {
                if ((securityManager.getApplicationAccessControlEnabled(str3) && !securityManager.checkAccessControlPass(str3) && (str2 = this.mCurrentActivityName) != null && str2.equals(APP_ACESS_LOCK_TOP_ACTIVITY)) || str3.equals(str)) {
                    return false;
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return true;
    }

    public static Boolean isAppInWhiteList(Context context, String str) {
        boolean z2 = false;
        Cursor cursorQuery = null;
        try {
            try {
                cursorQuery = context.getContentResolverForUser(UserHandle.OWNER).query(CloudAppConfigure.CONTENT_URI, null, "pkgName = ?", new String[]{str}, null);
                if (cursorQuery != null && cursorQuery.moveToFirst()) {
                    int columnIndex = cursorQuery.getColumnIndex("k_delay_hot");
                    if ((!cursorQuery.isNull(columnIndex) ? cursorQuery.getInt(columnIndex) : 0) < 0) {
                        z2 = true;
                    }
                }
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
            } catch (Exception e2) {
                Log.e(TAG, "applistException", e2);
                if (cursorQuery != null) {
                }
            }
            if (z2) {
                Slog.i(TAG, "ThermalLog: package: " + str + " in white list");
            }
            return Boolean.valueOf(z2);
        } catch (Throwable th) {
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            throw th;
        }
    }

    private boolean isDischargeUploadThermal() {
        long jElapsedRealtime = SystemClock.elapsedRealtime() - this.mDisChargeTime;
        if (DEBUG) {
            Log.d(TAG, "durationTime = " + jElapsedRealtime + ", disChargeTime = " + this.mDisChargeTime);
        }
        if (0 == this.mDisChargeTime) {
            this.mDisChargeTime = SystemClock.elapsedRealtime();
            return false;
        }
        if (jElapsedRealtime < ThermalInfoCollector.DEFAULT_COLLECT_UNRE_APPS_INFO_MIN_INTERVAL) {
            return false;
        }
        this.mDisChargeTime = SystemClock.elapsedRealtime();
        return true;
    }

    private boolean isIECBatteryAllowed() {
        int i2 = this.mIECState;
        return i2 == 1 || i2 == 98;
    }

    private static byte[] readInputStream(FileInputStream fileInputStream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[512];
        while (true) {
            try {
                try {
                    try {
                        int i2 = fileInputStream.read(bArr, 0, 512);
                        if (i2 <= 0) {
                            break;
                        }
                        byteArrayOutputStream.write(bArr, 0, i2);
                    } catch (Exception e2) {
                        Log.e(TAG, "readInputStream " + e2);
                        try {
                            byteArrayOutputStream.close();
                            return null;
                        } catch (IOException e3) {
                            Log.e(TAG, "readInputStream " + e3);
                            return null;
                        }
                    }
                } catch (Throwable th) {
                    byteArrayOutputStream.close();
                    throw th;
                }
                byteArrayOutputStream.close();
            } catch (IOException e4) {
                Log.e(TAG, "readInputStream " + e4);
            }
            throw th;
        }
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        try {
            byteArrayOutputStream.close();
        } catch (IOException e5) {
            Log.e(TAG, "readInputStream " + e5);
        }
        return byteArray;
    }

    private void registerScreenChanged() {
        if (this.mReceiverRegisterred) {
            return;
        }
        IntentFilter intentFilter = new IntentFilter("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        this.mContext.registerReceiver(this.mThermalReceiver, intentFilter, 2);
        this.mReceiverRegisterred = true;
    }

    private void sendSoftbankTimerToast() {
        if (this.mSoftbankTimerToast) {
            return;
        }
        Intent intent = new Intent(this.mContext, (Class<?>) BgService.class);
        intent.setAction("temp_state");
        intent.putExtra("temp_state_data", this.mTempStateNow);
        this.mContext.startService(intent);
        Log.i(TAG, "send softbank toast value:" + this.mTempStateNow);
        this.mSoftbankTimerToast = true;
    }

    private void showKillPkgToastDelayed() {
        try {
            if (this.mHandler.hasMessages(14)) {
                this.mHandler.removeMessages(14);
            }
            this.mHandler.sendEmptyMessageDelayed(14, 1000L);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void stricterKillProcessForPkg(String str) {
        try {
            ActivityManager activityManager = (ActivityManager) this.mContext.getSystemService("activity");
            if (activityManager == null) {
                return;
            }
            activityManager.forceStopPackage(str);
            ForegroundInfo foregroundInfo = PowerKeeperManager.getInstance().getEventsAggregator().getForegroundInfo();
            String str2 = foregroundInfo != null ? foregroundInfo.mForegroundPackageName : null;
            if (str2 == null || !str2.equals(str)) {
                return;
            }
            activityManager.forceStopPackageAsUser(str, 999);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void unRegisterScreenChanged() {
        if (this.mReceiverRegisterred) {
            this.mContext.unregisterReceiver(this.mThermalReceiver);
            this.mReceiverRegisterred = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r7v1 */
    /* JADX WARN: Type inference failed for: r7v10 */
    /* JADX WARN: Type inference failed for: r7v2 */
    /* JADX WARN: Type inference failed for: r7v28 */
    /* JADX WARN: Type inference failed for: r7v29 */
    /* JADX WARN: Type inference failed for: r7v3, types: [java.io.FileInputStream] */
    /* JADX WARN: Type inference failed for: r7v30 */
    /* JADX WARN: Type inference failed for: r7v9 */
    public void updateTempState(String str) throws Throwable {
        String str2;
        StringBuilder sb;
        String str3;
        boolean zEqualsIgnoreCase;
        ?? r7 = 0;
        FileInputStream fileInputStream = null;
        try {
            try {
                FileInputStream fileInputStream2 = new FileInputStream(new File(TEMP_STATE_FILE));
                try {
                    int i2 = Integer.parseInt(new String(readInputStream(fileInputStream2)).trim());
                    this.mTempStateNow = i2;
                    if (this.mTempStatePrev != i2) {
                        this.mTempStatePrev = i2;
                        if (CollectorManager.getInstance().isPopUpWindowCollector()) {
                            ScenarioCollectorUtils.getInstance().setPopupWindowState(this.mTempStateNow);
                            int virtualBoardSensorTemp = ThermalTempInfoHelper.getVirtualBoardSensorTemp();
                            if (virtualBoardSensorTemp != -1) {
                                PowerKeeperManager.getInstance().getEventsAggregator().notifyForBoardTemperature(virtualBoardSensorTemp);
                            }
                        }
                        synchronized (this.mTempStateLock) {
                            try {
                                int i3 = this.mTempStateNow;
                                int i4 = i3 / AppInfoUtils.PATTERN;
                                if (i4 == 0) {
                                    this.mShowKillPkgToast = false;
                                    this.mStricterKillEnable = false;
                                    this.isShowKillSomeBackPros = false;
                                    this.isShowKillAllBackPros = false;
                                    if (i3 == 0) {
                                        setTempStateSettings(i3);
                                        this.mHandler.removeMessages(11);
                                        this.mShowHighTempDialog = false;
                                        this.mSoftbankTimerToast = false;
                                        if (this.mIsHightTempCloseData) {
                                            NetUtils.setMobileDataEnabled(this.mContext, true, TAG);
                                            this.mIsHightTempCloseData = false;
                                            Slog.d(TAG, "temp_state default open mobile");
                                        }
                                        if (this.mIsHightTempCloseGPS) {
                                            CommonAdapter.setGpsEnabled(this.mContext, true);
                                            this.mIsHightTempCloseGPS = false;
                                            Slog.d(TAG, "temp_state default open gps");
                                        }
                                        this.mIsHightTempCloseWifiAp = false;
                                        displayControl(0);
                                    } else if (i3 == 1 || i3 == 5 || i3 == 6) {
                                        this.mHandler.removeMessages(11);
                                        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(11), this.mTempStatePollingTime);
                                    } else {
                                        this.mHandler.removeMessages(11);
                                        this.mShowHighTempDialog = false;
                                        this.mSoftbankTimerToast = false;
                                        displayControl(0);
                                    }
                                } else if (i4 == 1) {
                                    this.mHandler.removeMessages(11);
                                    this.mShowHighTempDialog = false;
                                    this.mStricterKillEnable = false;
                                    this.mShowKillPkgToast = false;
                                    this.isShowKillSomeBackPros = false;
                                    this.isShowKillAllBackPros = false;
                                    displayControl(0);
                                } else {
                                    this.mHandler.removeMessages(11);
                                    this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(11), this.mTempStatePollingTime);
                                }
                            } catch (Throwable th) {
                                throw th;
                            }
                        }
                    }
                    String str4 = Build.DEVICE;
                    boolean zEqualsIgnoreCase2 = str4.equalsIgnoreCase("iris");
                    String str5 = str4;
                    if (zEqualsIgnoreCase2 || str4.equalsIgnoreCase("XIG02") || str4.equalsIgnoreCase("A101XM") || (zEqualsIgnoreCase = str4.equalsIgnoreCase("lilac"))) {
                        str5 = str4;
                        str5 = str4;
                        str3 = zEqualsIgnoreCase;
                        str5 = zEqualsIgnoreCase;
                        str5 = str4;
                        str5 = str4;
                        str5 = str4;
                        sendSoftbankTimerToast();
                        str3 = str5;
                    }
                    str5 = str4;
                    str5 = str4;
                    str3 = zEqualsIgnoreCase;
                    str5 = zEqualsIgnoreCase;
                    cccSendIntent();
                    try {
                        fileInputStream2.close();
                        r7 = str3;
                    } catch (IOException e2) {
                        e = e2;
                        str2 = TAG;
                        sb = new StringBuilder();
                        sb.append("can not get temp state ");
                        sb.append(e);
                        Log.e(str2, sb.toString());
                    }
                } catch (Exception e3) {
                    e = e3;
                    fileInputStream = fileInputStream2;
                    Log.e("", "read temp state failed", e);
                    r7 = fileInputStream;
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                            r7 = fileInputStream;
                        } catch (IOException e4) {
                            e = e4;
                            str2 = TAG;
                            sb = new StringBuilder();
                            sb.append("can not get temp state ");
                            sb.append(e);
                            Log.e(str2, sb.toString());
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                    r7 = fileInputStream2;
                    if (r7 != 0) {
                        try {
                            r7.close();
                        } catch (IOException e5) {
                            Log.e(TAG, "can not get temp state " + e5);
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
            }
        } catch (Exception e6) {
            e = e6;
        }
    }

    private void updateThermalParamsForKillProcess() {
        getThermalParams();
        enableAppStateMonitor(this.mEnable);
        this.mThermalMonitorReady = true;
        Slog.d(TAG, "ThermalLog: " + this.mEnable + " " + this.mXoTempThreshold + " " + this.mXoTempThresholdclr + " " + this.mAppInBackgroundTimeTh + " " + this.mMaxCheckTime + " " + this.mAppStatusCountTh + " " + this.mAppActiveCountTh + " " + this.mDownloadSpeedTh + " " + this.mCpuLoadTh + " " + this.mMinPollingTime + " " + this.mMaxPollingTime + " " + this.mDebug);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void uploadTorchBrightnessRecord(int i2) {
        ArrayMap arrayMap = new ArrayMap();
        arrayMap.put("torch_level", Integer.valueOf(i2));
        if (DEBUG) {
            Log.d(TAG, "upload torch_brightness info " + arrayMap.toString());
        }
        ThermalInfoHelper.insertThermalInfo(this.mContext, arrayMap.toString(), ThermalInfoCollector.PHONE_TORCH_BRIGHTNESS_LEVEL_TYPE);
    }

    public void checkAndKillAbnormalProcesses() {
        int i2;
        int i3;
        if (this.mThermalMonitorReady) {
            synchronized (this.mProcessStatusMsg) {
                try {
                    long jCurrentTimeMillis = System.currentTimeMillis();
                    int size = this.mProcessStatusMsg.size();
                    i2 = 0;
                    for (int i4 = 0; i4 < size; i4++) {
                        ProcessStatusMsg processStatusMsg = this.mProcessStatusMsg.get(i4);
                        if (processStatusMsg.timeChangeBackground > 0) {
                            int i5 = processStatusMsg.pollCount;
                            if (i5 < 10) {
                                processStatusMsg.pollCount = i5 + 1;
                            }
                            if (PowerKeeperManager.getInstance().getActiveController().isAppActive(processStatusMsg.uId) && (i3 = processStatusMsg.activeCount) < 10) {
                                processStatusMsg.activeCount = i3 + 1;
                            }
                            if (this.mDebug) {
                                Slog.d(TAG, "ThermalLog: packagename = " + processStatusMsg.packageName + " activeCount = " + processStatusMsg.activeCount + " pollConut = " + processStatusMsg.pollCount);
                            }
                            if (jCurrentTimeMillis - processStatusMsg.timeChangeBackground < this.mMaxCheckTime) {
                                i2++;
                            }
                            getTransmissionSpeed(processStatusMsg);
                            if (getProcessCpuLoad(processStatusMsg) >= this.mCpuLoadTh && !isAppInWhiteList(this.mContext, processStatusMsg.packageName).booleanValue() && jCurrentTimeMillis - processStatusMsg.timeChangeBackground > this.mAppInBackgroundTimeTh && processStatusMsg.activeCount < this.mAppActiveCountTh && processStatusMsg.pollCount > this.mAppStatusCountTh && processStatusMsg.speedTrans < this.mDownloadSpeedTh && !ProcessManager.isLockedApplication(processStatusMsg.packageName, processStatusMsg.uId)) {
                                Slog.d(TAG, "ThermalLog: Kill " + processStatusMsg.packageName);
                                this.mHistoryLog.log("ThermalLog: Kill " + processStatusMsg.packageName);
                                ProcessManager.kill(new ProcessConfig(12, processStatusMsg.packageName, processStatusMsg.uId));
                            }
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
            if (i2 == 0 && this.mPollingTime == this.mMinPollingTime) {
                this.mPollingTime = this.mMaxPollingTime;
                Slog.i(TAG, "ThermalLog: modify polling time to " + this.mPollingTime + "(ms)");
            } else if (i2 > 0 && this.mPollingTime == this.mMaxPollingTime) {
                this.mPollingTime = this.mMinPollingTime;
                Slog.i(TAG, "ThermalLog: modify polling time to " + this.mPollingTime + "(ms)");
            }
            if (this.mThermalMonitorEnabled) {
                this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(8), this.mPollingTime);
            }
        }
    }

    public void checkBatteryCharge() {
        if (DEBUG) {
            Log.d(TAG, "checkBatteryCharge = " + this.mThermalStatus);
        }
        checkBatteryAndThermalChange(this.mThermalStatus);
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(12), 1800000L);
    }

    public void checkBatteryDischarge(final MyThermalStatus myThermalStatus) {
        this.mSubBatteryLevel++;
        if (DEBUG) {
            Log.d(TAG, "SubBatteryLevel = " + this.mSubBatteryLevel + ", myThermalStatus = " + myThermalStatus);
        }
        if (this.mSubBatteryLevel == mBatteryLevelThreshold) {
            this.mHandler.post(new Runnable() { // from class: com.miui.powerkeeper.feedbackcontrol.ThermalManager.14
                @Override // java.lang.Runnable
                public void run() {
                    ThermalManager.this.checkBatteryAndThermalChange(myThermalStatus);
                }
            });
            this.mSubBatteryLevel = 0;
        }
    }

    public void controlSystemRes() {
        synchronized (this.mTempStateLock) {
            try {
                int i2 = this.mTempStateNow;
                if (i2 < 100000) {
                    controlSystemResOld();
                } else if (i2 > 100000 && i2 < TEMP_NEW_DIALOG_SIZE) {
                    controlSystemResNew();
                } else if (i2 >= TEMP_NEW_DIALOG_SIZE) {
                    controlSystemResNewDialog();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void controlSystemResNew() {
        ForegroundInfo foregroundInfo;
        int uidByPackageName;
        int wifiApState;
        int bacKProgressCtrlCode = getBacKProgressCtrlCode();
        int sKCtrlCode = getSKCtrlCode();
        int wGDCtrlCode = getWGDCtrlCode();
        int displayCtrlCode = getDisplayCtrlCode();
        int benchmarkCode = getBenchmarkCode();
        PowerLog.v(TAG, "controlSystemResNew2, benchmarkCode:" + benchmarkCode + ",mBatteryTemperature :" + this.mBatteryTemperature + ",board temp:" + ThermalTempInfoHelper.getBoardThermalTemp());
        int i2 = 1;
        if (this.mKillSomeBackProEnable && bacKProgressCtrlCode == 1 && this.mBatteryTemperature >= getKillSomeProsBatTemp() && this.killSomeProcessConfig != null) {
            Log.d(TAG, "mAllowedKillSomeBackProBatteryTempThreshold is " + getKillSomeProsBatTemp());
            showKillSomeBackProToast();
            ProcessManager.kill(this.killSomeProcessConfig);
        }
        if (this.mKillAllBackProEnable && bacKProgressCtrlCode == 2 && this.mBatteryTemperature >= getKillAllProsBatTemp() && this.killAllProcessConfig != null) {
            Log.d(TAG, "mAllowedKillAllBackProBatteryTempThreshold is " + getKillAllProsBatTemp());
            showKillAllBackProToast();
            ProcessManager.kill(this.killAllProcessConfig);
        }
        int i3 = 0;
        if (sKCtrlCode == 2 || sKCtrlCode == 4) {
            showHighTempDialog();
        } else {
            this.mShowHighTempDialog = false;
            this.mSoftbankTimerToast = false;
        }
        if ((sKCtrlCode == 3 || sKCtrlCode == 4) && (foregroundInfo = ProcessManager.getForegroundInfo()) != null && foregroundInfo.mForegroundPackageName != null) {
            String[] strArr = {PKN_IQIYI, "com.miui.video", "com.tencent.qqlive"};
            int i4 = 0;
            for (int i5 = 3; i4 < i5; i5 = 3) {
                String str = strArr[i4];
                if (!str.equals(foregroundInfo.mForegroundPackageName)) {
                    try {
                        List runningProcessInfoByPackageName = ProcessManager.getRunningProcessInfoByPackageName(str);
                        uidByPackageName = (runningProcessInfoByPackageName == null || runningProcessInfoByPackageName.isEmpty()) ? -1 : ((RunningProcessInfo) runningProcessInfoByPackageName.get(i3)).mUid;
                    } catch (RemoteException e2) {
                        this.mHistoryLog.log("RemoteException " + e2);
                        uidByPackageName = PackageUtil.getUidByPackageName(this.mContext, str);
                    }
                    if (uidByPackageName > 0) {
                        this.mHistoryLog.log("Kill " + str + " uid=" + uidByPackageName);
                        ProcessManager.kill(new ProcessConfig(12, str, uidByPackageName));
                    }
                }
                i4++;
                i2 = 1;
                i3 = 0;
            }
        }
        if (benchmarkCode == i2) {
            if (!this.mExitFGBenchmarkEnable) {
                PowerLog.v(TAG, "benchmarkCode:" + benchmarkCode + ",mBatteryTemperature :" + this.mBatteryTemperature + ",board temp:" + ThermalTempInfoHelper.getBoardThermalTemp() + ", !mExitFGBenchmarkEnable, return");
                return;
            }
            try {
                String str2 = this.mCurrentFGPkg;
                boolean zIsAllowedKillFGBenchmark = isAllowedKillFGBenchmark(str2);
                if (TextUtils.isEmpty(this.mCurrentFGPkg) || !zIsAllowedKillFGBenchmark) {
                    PowerLog.i(TAG, "ExitFGBenchmarkEnable fail, benchmarkCode:" + benchmarkCode + ",mBatteryTemperature :" + this.mBatteryTemperature + ",board temp:" + ThermalTempInfoHelper.getBoardThermalTemp() + ", isAllowToKillFG:" + zIsAllowedKillFGBenchmark);
                } else {
                    if (this.mBatteryTemperature < this.mAllowedShowBenchmarkDialogBatteryTempThreshhold) {
                        PowerLog.i(TAG, "mBatteryTemperature is " + this.mBatteryTemperature + " less then BatteryTempThreshhold: " + this.mAllowedShowBenchmarkDialogBatteryTempThreshhold + ", not showHighTempDialog return");
                        return;
                    }
                    PowerLog.i(TAG, "mBatteryTemperature is " + this.mBatteryTemperature + " over than BatteryTempThreshhold: " + this.mAllowedShowBenchmarkDialogBatteryTempThreshhold + ", showHighTempDialog");
                    showHighTempBenchmarkDialog(this.mCurrentFGPkg);
                    stricterKillProcessForPkg(str2);
                    PowerLog.v(TAG, "ExitFGBenchmarkEnable succeed, benchmarkCode:" + benchmarkCode + ",mBatteryTemperature :" + this.mBatteryTemperature + ",board temp:" + ThermalTempInfoHelper.getBoardThermalTemp());
                }
            } catch (Exception e3) {
                e3.printStackTrace();
            }
        }
        if (sKCtrlCode == 5) {
            this.mStricterKillEnable = true;
            if (!this.mExitForegroundAppEnable) {
                StringBuilder sb = new StringBuilder();
                sb.append("skCode:");
                sb.append(sKCtrlCode);
                sb.append(",mBatteryTemperature :");
                sb.append(this.mBatteryTemperature);
                sb.append(",board temp:");
                sb.append(ThermalTempInfoHelper.getBoardThermalTemp());
                sb.append(", !mExitForegroundAppEnable:");
                sb.append(!this.mExitForegroundAppEnable);
                Log.i(TAG, sb.toString());
                return;
            }
            try {
                String str3 = ProcessManager.getForegroundInfo().mForegroundPackageName;
                if (this.mBatteryTemperature < getKilledBatteryTemp() || str3 == null || !isAllowedKillPkg(str3)) {
                    Log.i(TAG, "skCode2:" + sKCtrlCode + ",mBatteryTemperature :" + this.mBatteryTemperature + ",board temp:" + ThermalTempInfoHelper.getBoardThermalTemp());
                } else {
                    stricterKillProcessForPkg(str3);
                    showKillPkgToastDelayed();
                    Log.i(TAG, "skCode:" + sKCtrlCode + ",mBatteryTemperature :" + this.mBatteryTemperature + ",board temp:" + ThermalTempInfoHelper.getBoardThermalTemp());
                }
            } catch (Exception e4) {
                e4.printStackTrace();
            }
        } else {
            this.mStricterKillEnable = false;
            this.mShowKillPkgToast = false;
        }
        if ((wGDCtrlCode == 1 || wGDCtrlCode == 4 || wGDCtrlCode == 6 || wGDCtrlCode == 7) && NetUtils.getMobileDataEnabled(this.mContext, TAG)) {
            NetUtils.setMobileDataEnabled(this.mContext, false, TAG);
            Slog.d(TAG, "disable mobile");
        }
        if ((wGDCtrlCode == 1 || wGDCtrlCode == 3 || wGDCtrlCode == 5 || wGDCtrlCode == 7) && CommonAdapter.isGpsEnabled(this.mContext)) {
            CommonAdapter.setGpsEnabled(this.mContext, false);
            Slog.d(TAG, "disable gps");
        }
        if ((wGDCtrlCode == 1 || wGDCtrlCode == 2 || wGDCtrlCode == 5 || wGDCtrlCode == 6) && ((wifiApState = ((WifiManager) this.mContext.getSystemService("wifi")).getWifiApState()) == 13 || wifiApState == 12)) {
            WifiApAdapter.setWifiApEnabled(this.mContext, false);
            Slog.d(TAG, "disable wifi ap");
        }
        if (displayCtrlCode == 5) {
            displayControl(90);
        } else if (displayCtrlCode == 1) {
            displayControl(60);
        } else if (displayCtrlCode == 2) {
            displayControl(50);
        } else if (displayCtrlCode == 3) {
            displayControl(48);
        } else if (displayCtrlCode == 4) {
            displayControl(30);
        }
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(11), this.mTempStatePollingTime);
    }

    public void controlSystemResNewDialog() {
        ForegroundInfo foregroundInfo;
        int uidByPackageName;
        int wifiApState;
        int i2;
        int bacKProgressCtrlCode = getBacKProgressCtrlCode();
        int sKCtrlCode = getSKCtrlCode();
        int wGDCtrlCode = getWGDCtrlCode();
        int displayCtrlCode = getDisplayCtrlCode();
        int benchmarkCode = getBenchmarkCode();
        PowerLog.v(TAG, "controlSystemResNewdialog2.0, benchmarkCode:" + benchmarkCode + ",mBatteryTemperature :" + this.mBatteryTemperature + ",board temp:" + ThermalTempInfoHelper.getBoardThermalTemp());
        if (this.mBatteryTemperature >= getKilledBatteryTemp()) {
            setBatteryAllowKillSettings(1);
        } else {
            setBatteryAllowKillSettings(0);
        }
        setTempStateSettings(this.mTempStateNow);
        if (this.mKillSomeBackProEnable && bacKProgressCtrlCode == 1 && this.mBatteryTemperature >= getKillSomeProsBatTemp() && this.killSomeProcessConfig != null) {
            Log.d(TAG, "mAllowedKillBatteryTempThreshhold is " + getKillSomeProsBatTemp());
            ProcessManager.kill(this.killSomeProcessConfig);
        }
        if (this.mKillAllBackProEnable && bacKProgressCtrlCode == 2 && this.mBatteryTemperature >= getKillAllProsBatTemp() && this.killAllProcessConfig != null) {
            Log.d(TAG, "mAllowedKillBatteryTempThreshhold is " + getKillAllProsBatTemp());
            ProcessManager.kill(this.killAllProcessConfig);
        }
        if ((sKCtrlCode == 3 || sKCtrlCode == 4) && (foregroundInfo = ProcessManager.getForegroundInfo()) != null && foregroundInfo.mForegroundPackageName != null) {
            String[] strArr = {PKN_IQIYI, "com.miui.video", "com.tencent.qqlive"};
            int i3 = 0;
            for (int i4 = 3; i3 < i4; i4 = 3) {
                String str = strArr[i3];
                if (!str.equals(foregroundInfo.mForegroundPackageName)) {
                    try {
                        List runningProcessInfoByPackageName = ProcessManager.getRunningProcessInfoByPackageName(str);
                        uidByPackageName = (runningProcessInfoByPackageName == null || runningProcessInfoByPackageName.isEmpty()) ? -1 : ((RunningProcessInfo) runningProcessInfoByPackageName.get(0)).mUid;
                    } catch (RemoteException e2) {
                        this.mHistoryLog.log("RemoteException " + e2);
                        uidByPackageName = PackageUtil.getUidByPackageName(this.mContext, str);
                    }
                    if (uidByPackageName > 0) {
                        this.mHistoryLog.log("Kill " + str + " uid=" + uidByPackageName);
                        ProcessManager.kill(new ProcessConfig(12, str, uidByPackageName));
                    }
                }
                i3++;
            }
        }
        if (benchmarkCode == 1) {
            if (!this.mExitFGBenchmarkEnable) {
                PowerLog.v(TAG, "benchmarkCode:" + benchmarkCode + ",mBatteryTemperature :" + this.mBatteryTemperature + ",board temp:" + ThermalTempInfoHelper.getBoardThermalTemp() + ", !mExitFGBenchmarkEnable, return");
                return;
            }
            try {
                String str2 = this.mCurrentFGPkg;
                boolean zIsAllowedKillFGBenchmark = isAllowedKillFGBenchmark(str2);
                if (TextUtils.isEmpty(this.mCurrentFGPkg) || !zIsAllowedKillFGBenchmark) {
                    PowerLog.i(TAG, "ExitFGBenchmarkEnable fail, benchmarkCode:" + benchmarkCode + ",mBatteryTemperature :" + this.mBatteryTemperature + ",board temp:" + ThermalTempInfoHelper.getBoardThermalTemp() + ", isAllowToKillFG:" + zIsAllowedKillFGBenchmark);
                } else {
                    if (this.mBatteryTemperature < this.mAllowedShowBenchmarkDialogBatteryTempThreshhold) {
                        PowerLog.i(TAG, "mBatteryTemperature is " + this.mBatteryTemperature + " less then BatteryTempThreshhold: " + this.mAllowedShowBenchmarkDialogBatteryTempThreshhold + ", not showHighTempDialog return");
                        return;
                    }
                    PowerLog.i(TAG, "mBatteryTemperature is " + this.mBatteryTemperature + " over than BatteryTempThreshhold: " + this.mAllowedShowBenchmarkDialogBatteryTempThreshhold + ", showHighTempDialog");
                    stricterKillProcessForPkg(str2);
                    PowerLog.v(TAG, "ExitFGBenchmarkEnable succeed, benchmarkCode:" + benchmarkCode + ",mBatteryTemperature :" + this.mBatteryTemperature + ",board temp:" + ThermalTempInfoHelper.getBoardThermalTemp());
                }
            } catch (Exception e3) {
                e3.printStackTrace();
            }
        }
        if (sKCtrlCode == 5) {
            if (!this.mExitForegroundAppEnable) {
                StringBuilder sb = new StringBuilder();
                sb.append("skCode:");
                sb.append(sKCtrlCode);
                sb.append(",mBatteryTemperature :");
                sb.append(this.mBatteryTemperature);
                sb.append(",board temp:");
                sb.append(ThermalTempInfoHelper.getBoardThermalTemp());
                sb.append(", !mExitForegroundAppEnable:");
                sb.append(!this.mExitForegroundAppEnable);
                Log.i(TAG, sb.toString());
                return;
            }
            try {
                String str3 = ProcessManager.getForegroundInfo().mForegroundPackageName;
                if (this.mBatteryTemperature >= getKilledBatteryTemp()) {
                    if (str3 != null && isAllowedKillPkgForNewDialog(str3)) {
                        stricterKillProcessForPkg(str3);
                    }
                    Log.i(TAG, "skCode:" + sKCtrlCode + ",mBatteryTemperature :" + this.mBatteryTemperature + ",board temp:" + ThermalTempInfoHelper.getBoardThermalTemp());
                } else {
                    Log.i(TAG, "skCode2:" + sKCtrlCode + ",mBatteryTemperature :" + this.mBatteryTemperature + ",board temp:" + ThermalTempInfoHelper.getBoardThermalTemp());
                }
            } catch (Exception e4) {
                e4.printStackTrace();
            }
        }
        boolean z2 = true;
        if (wGDCtrlCode == 1 || wGDCtrlCode == 4 || wGDCtrlCode == 6 || wGDCtrlCode == 7) {
            if (NetUtils.getMobileDataEnabled(this.mContext, TAG)) {
                NetUtils.setMobileDataEnabled(this.mContext, false, TAG);
                z2 = true;
                this.mIsHightTempCloseData = true;
                Slog.d(TAG, "disable mobile");
            } else {
                z2 = true;
            }
        } else if (this.mIsHightTempCloseData) {
            NetUtils.setMobileDataEnabled(this.mContext, true, TAG);
            this.mIsHightTempCloseData = false;
            Slog.d(TAG, "open mobile");
            z2 = true;
        }
        if (wGDCtrlCode == z2 || wGDCtrlCode == 3 || wGDCtrlCode == 5 || wGDCtrlCode == 7) {
            if (CommonAdapter.isGpsEnabled(this.mContext)) {
                CommonAdapter.setGpsEnabled(this.mContext, false);
                z2 = true;
                this.mIsHightTempCloseGPS = true;
                Slog.d(TAG, "disable gps");
            } else {
                z2 = true;
            }
        } else if (this.mIsHightTempCloseGPS) {
            CommonAdapter.setGpsEnabled(this.mContext, z2);
            this.mIsHightTempCloseGPS = false;
            Slog.d(TAG, "open gps");
            z2 = true;
        }
        if ((wGDCtrlCode == z2 || wGDCtrlCode == 2 || wGDCtrlCode == 5 || wGDCtrlCode == 6) && ((wifiApState = ((WifiManager) this.mContext.getSystemService("wifi")).getWifiApState()) == 13 || wifiApState == 12)) {
            WifiApAdapter.setWifiApEnabled(this.mContext, false);
            i2 = 1;
            this.mIsHightTempCloseWifiAp = true;
            Slog.d(TAG, "disable wifi ap");
        } else {
            i2 = 1;
        }
        if (displayCtrlCode == 5) {
            displayControl(90);
        } else if (displayCtrlCode == i2) {
            displayControl(60);
        } else if (displayCtrlCode == 2) {
            displayControl(50);
        } else if (displayCtrlCode == 3) {
            displayControl(48);
        } else if (displayCtrlCode == 4) {
            displayControl(30);
        }
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(11), this.mTempStatePollingTime);
    }

    public void controlSystemResOld() {
        int wifiApState;
        int uidByPackageName;
        if (this.mTempStateNow > 0) {
            showHighTempDialog();
            ForegroundInfo foregroundInfo = ProcessManager.getForegroundInfo();
            if (foregroundInfo != null && foregroundInfo.mForegroundPackageName != null) {
                String[] strArr = {PKN_IQIYI, "com.miui.video", "com.tencent.qqlive"};
                for (int i2 = 0; i2 < 3; i2++) {
                    String str = strArr[i2];
                    if (!str.equals(foregroundInfo.mForegroundPackageName)) {
                        try {
                            List runningProcessInfoByPackageName = ProcessManager.getRunningProcessInfoByPackageName(str);
                            uidByPackageName = (runningProcessInfoByPackageName == null || runningProcessInfoByPackageName.isEmpty()) ? -1 : ((RunningProcessInfo) runningProcessInfoByPackageName.get(0)).mUid;
                        } catch (RemoteException e2) {
                            this.mHistoryLog.log("RemoteException " + e2);
                            uidByPackageName = PackageUtil.getUidByPackageName(this.mContext, str);
                        }
                        if (uidByPackageName > 0) {
                            this.mHistoryLog.log("Kill " + str + " uid=" + uidByPackageName);
                            ProcessManager.kill(new ProcessConfig(12, str, uidByPackageName));
                        }
                    }
                }
            }
            if (this.mTempStateNow == 1 && NetUtils.getMobileDataEnabled(this.mContext, TAG)) {
                NetUtils.setMobileDataEnabled(this.mContext, false, TAG);
                Slog.d(TAG, "disable mobile");
            }
            int i3 = this.mTempStateNow;
            if ((i3 == 1 || i3 == 6) && CommonAdapter.isGpsEnabled(this.mContext)) {
                CommonAdapter.setGpsEnabled(this.mContext, false);
                Slog.d(TAG, "disable gps");
            }
            int i4 = this.mTempStateNow;
            if ((i4 == 1 || i4 == 5 || i4 == 6) && ((wifiApState = ((WifiManager) this.mContext.getSystemService("wifi")).getWifiApState()) == 13 || wifiApState == 12)) {
                WifiApAdapter.setWifiApEnabled(this.mContext, false);
                Slog.d(TAG, "disable wifi ap");
            }
            this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(11), this.mTempStatePollingTime);
        }
    }

    public void displayControl(int i2) {
        DisplayFrameSetting dFSInstance;
        if (i2 == this.fpsLast || this.mDisplayFeatureManager == null || (dFSInstance = DisplayFrameSetting.getDFSInstance()) == null) {
            return;
        }
        dFSInstance.setScreenEffect(i2, THERMAL);
        this.fpsLast = i2;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        indentingPrintWriter.println("#######dump##ThermalManager#######");
        indentingPrintWriter.println("ThermalManager kill processes history:");
        this.mHistoryLog.dump(fileDescriptor, indentingPrintWriter, strArr);
        indentingPrintWriter.println("ThermalManager thermal threshold:");
        StringBuilder sb = new StringBuilder();
        sb.append("\tBatteryMonitor status:");
        sb.append(mFeatureEnabled ? SleepModeControllerNew.SLEEP_MODE_ON : "off");
        printWriter.println(sb.toString());
        printWriter.println("\tbattery temp Threshold:" + this.mTemperatureThreshold + " xo_thermal Threshold " + this.mXoThermalTemperatureThreshold + " mIsCatchBugReport " + this.mIsCatchBugReport);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("\tCurrent battery temp: ");
        MyThermalStatus myThermalStatus = this.mThermalStatus;
        sb2.append(myThermalStatus != null ? Integer.valueOf(myThermalStatus.batteryTemperature) : "unknown");
        printWriter.println(sb2.toString());
        printWriter.println("\tCurrent xo_thermal temp: " + ThermalTempInfoHelper.getXoThermalTemp());
        printWriter.println("\tCurrent mXoThermalTempCheckThreshold:" + mXoThermalTempCheckThreshold + ",mBatteryLevelThreshold:" + mBatteryLevelThreshold + ",mThermalCollectorEnable:" + mThermalCollectorEnable);
        StringBuilder sb3 = new StringBuilder();
        sb3.append("\tCurrent AmbientTemp :");
        sb3.append(ThermalTempInfoHelper.getAmbientTemp());
        printWriter.println(sb3.toString());
        HighTempRange highTempRange = this.mHighTempRange;
        if (highTempRange != null) {
            highTempRange.dump(printWriter);
        }
        printWriter.println("\nHigh temp benchmark:" + this.mExitFGBenchmarkEnable + ShellUtils.COMMAND_LINE_END);
        printWriter.println("\nAllowed Show Benchmark DialogBatteryTempThreshhold:" + this.mAllowedShowBenchmarkDialogBatteryTempThreshhold + ShellUtils.COMMAND_LINE_END);
        indentingPrintWriter.println("end#######dump##ThermalManager#######end");
    }

    public void enableMonitor(boolean z2) {
        ThermalInfoCollector thermalInfoCollector;
        ThermalInfoCollector thermalInfoCollector2;
        if (z2) {
            if (mFeatureEnabled) {
                return;
            }
            mFeatureEnabled = true;
            this.mBatteryChangedCallback = new PowerKeeperInterface.d(this.mHandler, this.mBatteryStatusReceiver);
            PowerKeeperManager.getInstance().registerBatteryChangedListener(this.mBatteryChangedCallback);
            Log.d(TAG, "register battery status listener.");
            if (!mThermalCollectorEnable || (thermalInfoCollector2 = this.mThermalInfoCollector) == null) {
                return;
            }
            thermalInfoCollector2.startCollectInfo();
            return;
        }
        if (mFeatureEnabled) {
            mFeatureEnabled = false;
            PowerKeeperManager.getInstance().unregisterBatteryChangedListener(this.mBatteryChangedCallback);
            PowerKeeperInterface.d dVar = this.mBatteryChangedCallback;
            if (dVar != null) {
                dVar.a();
                this.mBatteryChangedCallback = null;
            }
            Log.d(TAG, "unregister battery status listener.");
            if (!mThermalCollectorEnable || (thermalInfoCollector = this.mThermalInfoCollector) == null) {
                return;
            }
            thermalInfoCollector.stopCollectInfo();
        }
    }

    public int getBacKProgressCtrlCode() {
        return (this.mTempStateNow / 10000) % 10;
    }

    public int getBenchmarkCode() {
        return (this.mTempStateNow / 10) % 10;
    }

    public int getDisplayCtrlCode() {
        return (this.mTempStateNow / TEMP_DP_SIZE) % 10;
    }

    public int getSKCtrlCode() {
        return (this.mTempStateNow / AppInfoUtils.PATTERN) % 10;
    }

    public Bundle getThermalThresholds() {
        Bundle bundle;
        synchronized (this.mStatusLock) {
            bundle = new Bundle();
            bundle.putInt(SimpleSettingKeys.KEY_TEMP_THRESHOLD, this.mTemperatureThreshold);
            bundle.putInt(SimpleSettingKeys.KEY_TEMP_THRESHOLD_XO_THERMAL, this.mXoThermalTemperatureThreshold);
            bundle.putInt(SimpleSettingKeys.KEY_TEMP_THRESHOLD_XO_THERMAL, this.mBlThermalTemperatureThreshold);
        }
        return bundle;
    }

    public List<ProcessInfo> getTopProcessInfoByCpuRate(int i2) throws Throwable {
        List<String> allProcessInfoFromCpuInfo = Utils.getAllProcessInfoFromCpuInfo();
        ArrayList arrayList = new ArrayList();
        arrayList.clear();
        for (String str : allProcessInfoFromCpuInfo) {
            if (str != null && ProcessInfo.isValidProInfoLine(str)) {
                arrayList.add(new ProcessInfo(str));
            }
        }
        return arrayList.size() <= i2 ? arrayList : arrayList.subList(0, i2);
    }

    public int getWGDCtrlCode() {
        return (this.mTempStateNow / 1000000) % 10;
    }

    public void loadPerfLimitsConfig(Context context) {
        String str = DEFAULT_PERF_LIMITS;
        boolean z2 = false;
        try {
            String string = SimpleSettings.Misc.getString(this.mContext, SimpleSettingKeys.KEY_THERMAL_PERF_LIMITS, DEFAULT_PERF_LIMITS);
            if (string.equals(DEFAULT_PERF_LIMITS)) {
                Log.i(TAG, "PerfLog: no perf limits config be found");
                return;
            }
            Log.i(TAG, "PerfLog: " + string);
            ThermalPerfLimits thermalPerfLimits = this.mTempThermalPerfLimits;
            if (thermalPerfLimits != null) {
                str = thermalPerfLimits.pkgName;
            }
            ThermalPerfLimitsArrayClear();
            JSONObject jSONObject = new JSONObject(string);
            Iterator<String> itKeys = jSONObject.keys();
            boolean zOptBoolean = false;
            while (itKeys.hasNext()) {
                try {
                    String next = itKeys.next();
                    if (next.equals("enable")) {
                        zOptBoolean = jSONObject.optBoolean("enable", false);
                        Log.i(TAG, "PerfLog: enable " + zOptBoolean);
                    } else if (next.equals(PERF_LIMITS_LOW_CAP_LEVEL)) {
                        this.lowCapLevelTh = jSONObject.optInt(PERF_LIMITS_LOW_CAP_LEVEL, 0);
                        Log.i(TAG, "PerfLog: low cap level " + this.lowCapLevelTh);
                    } else {
                        JSONObject jSONObject2 = jSONObject.getJSONObject(next);
                        int iOptInt = jSONObject2.optInt("normal", -1);
                        int iOptInt2 = jSONObject2.optInt(PERF_LIMITS_MIDDLE, -1);
                        if (iOptInt != -1 && iOptInt2 != -1) {
                            ThermalPerfLimitsArrayAdd(new ThermalPerfLimits(next, iOptInt, iOptInt2));
                        }
                    }
                } catch (JSONException e2) {
                    e = e2;
                    z2 = zOptBoolean;
                    Log.e(TAG, "startUpdate", e);
                    zOptBoolean = z2;
                } catch (Exception e3) {
                    e = e3;
                    z2 = zOptBoolean;
                    Log.e(TAG, "startUpdate", e);
                    zOptBoolean = z2;
                }
            }
            if (this.mTempThermalPerfLimits == null) {
                ThermalPerfLimitsArrayScan(str);
            }
            enablePerfLimitsMonitor(zOptBoolean);
        } catch (JSONException e4) {
            e = e4;
        } catch (Exception e5) {
            e = e5;
        }
    }

    public void onForegroundPkgChanged(ForegroundInfo foregroundInfo) {
        try {
            String str = foregroundInfo.mForegroundPackageName;
            if (!TextUtils.isEmpty(str)) {
                this.mCurrentFGPkg = str;
            }
            if (this.mExitForegroundAppEnable && this.mBatteryTemperature >= getKilledBatteryTemp() && this.mStricterKillEnable && isAllowedKillPkg(str)) {
                stricterKillProcessForPkg(str);
                showHighTempToast();
                Log.i(TAG, "mBatteryTemperature :" + this.mBatteryTemperature + ",board temp:" + ThermalTempInfoHelper.getBoardThermalTemp());
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public void removeChargeMessages() {
        if (DEBUG) {
            Log.d(TAG, "removeChargeMessages");
        }
        this.mHandler.removeMessages(12);
    }

    public void setBatteryAllowKillSettings(int i2) {
        if (this.mBatteryAllowKillSettingsPrev != i2) {
            Log.d(TAG, "mBatteryAllowKillSettingsPrev != value mBatteryPrev = " + this.mBatteryAllowKillSettingsPrev + " value = " + i2);
            this.mBatteryAllowKillSettingsPrev = i2;
            Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "allowed_kill_battery_temp_threshhold", i2, 0);
        }
    }

    public void setIECBatteryThresholds(int i2, int i3, int i4) {
        this.mIECKillBatteryTemp = i2;
        this.mIECKillSomeBackProBatteryTemp = i3;
        this.mIECKillAllBackProBatteryTemp = i4;
    }

    public void setIECState(int i2) {
        this.mIECState = i2;
        Log.d(TAG, "setIECState: " + this.mIECState);
    }

    public void setTempStateSettings(int i2) {
        if (this.mTempStateSettingsPrev != i2) {
            Log.d(TAG, "mTempStateSettingsPrev != value mTempSatePre = " + this.mTempStateSettingsPrev + " value = " + i2);
            this.mTempStateSettingsPrev = i2;
            Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "thermal_temp_state_value", i2, 0);
        }
    }

    public void showHighTempBenchmarkDialog(String str) {
        String str2;
        PowerLog.i(TAG, "showHighTempBenchmarkDialog pkg:" + str);
        PackageManager packageManager = this.mContext.getPackageManager();
        String str3 = "";
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(str, 128);
            str2 = (String) (applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo) : "");
            try {
                PowerLog.v(TAG, "showHighTempBenchmarkDialog2 appInfo.name:" + str2);
            } catch (PackageManager.NameNotFoundException e2) {
                str3 = str2;
                e = e2;
                e.printStackTrace();
                str2 = str3;
            }
        } catch (PackageManager.NameNotFoundException e3) {
            e = e3;
        }
        cccUploadRecord();
        Intent intent = new Intent(this.mContext, (Class<?>) BgService.class);
        intent.setAction("show_high_temp_benchmark_dialog");
        intent.putExtra("app_name", str2);
        this.mContext.startService(intent);
    }

    public void showHighTempDialog() {
        Slog.d(TAG, "showHighTempDialog");
        if (this.mShowHighTempDialog) {
            return;
        }
        cccUploadRecord();
        Slog.d(TAG, "mBatteryTemperature is : " + this.mBatteryTemperature + "mAllowedShowDialogBatteryTempThreshhold:" + this.mAllowedShowDialogBatteryTempThreshhold);
        if (this.mBatteryTemperature >= this.mAllowedShowDialogBatteryTempThreshhold) {
            Intent intent = new Intent(this.mContext, (Class<?>) BgService.class);
            intent.setAction("show_high_temp_dialog");
            this.mContext.startService(intent);
            this.mShowHighTempDialog = true;
            return;
        }
        Slog.d(TAG, "mBatteryTemperature is less then: " + this.mAllowedShowDialogBatteryTempThreshhold + " and not showHighTempDialog");
    }

    public void showHighTempToast() {
        Intent intent = new Intent(this.mContext, (Class<?>) BgService.class);
        intent.setAction("show_high_temp_toast");
        this.mContext.startService(intent);
    }

    public void showKillAllBackProToast() {
        if (this.isShowKillSomeBackPros) {
            return;
        }
        Intent intent = new Intent(this.mContext, (Class<?>) BgService.class);
        intent.setAction("show_kill_all_back_pro_toast");
        this.mContext.startService(intent);
        this.isShowKillSomeBackPros = true;
    }

    public void showKillPkgToast() {
        if (this.mShowKillPkgToast) {
            return;
        }
        Intent intent = new Intent(this.mContext, (Class<?>) BgService.class);
        intent.setAction("show_kill_pkg_toast");
        this.mContext.startService(intent);
        this.mShowKillPkgToast = true;
    }

    public void showKillSomeBackProToast() {
        if (this.isShowKillAllBackPros) {
            return;
        }
        Intent intent = new Intent(this.mContext, (Class<?>) BgService.class);
        intent.setAction("show_kill_some_back_pro_toast");
        this.mContext.startService(intent);
        this.isShowKillAllBackPros = true;
    }

    public void updateThermalConfig() {
        updateThermalParamsForKillProcess();
        int i2 = SimpleSettings.Misc.getInt(this.mContext, SimpleSettingKeys.KEY_TEMP_THRESHOLD, 47);
        int i3 = SimpleSettings.Misc.getInt(this.mContext, SimpleSettingKeys.KEY_TEMP_THRESHOLD_XO_THERMAL, 52);
        int i4 = SimpleSettings.Misc.getInt(this.mContext, SimpleSettingKeys.KEY_TEMP_THRESHOLD_BL_THERMAL, 60);
        int i5 = SimpleSettings.Misc.getInt(this.mContext, SimpleSettingKeys.KEY_TEMP_THRESHOLD_XO_THERMAL_CHECK, 25);
        int i6 = SimpleSettings.Misc.getInt(this.mContext, SimpleSettingKeys.KEY_TEMP_THRESHOLD_BATTERY_LEVEL, 3);
        boolean z2 = SimpleSettings.Misc.getBoolean(this.mContext, SimpleSettingKeys.KEY_THERMAL_ENABLE, false);
        boolean z3 = SimpleSettings.Misc.getBoolean(this.mContext, SimpleSettingKeys.KEY_CATCH_LOG_UNRELEASED_ENABLE, false);
        boolean z4 = SimpleSettings.Misc.getBoolean(this.mContext, SimpleSettingKeys.KEY_CATCH_LOG_RELEASED_ENABLE, false);
        String string = SimpleSettings.Misc.getString(this.mContext, SimpleSettingKeys.KEY_CATCH_LOG_UNRELEASED_THRESHHOLD, "");
        String string2 = SimpleSettings.Misc.getString(this.mContext, SimpleSettingKeys.KEY_CATCH_LOG_RELEASED_THRESHHOLD, "");
        Context context = this.mContext;
        long j2 = ThermalInfoCollector.DEFAULT_CATCH_LOG_MIN_INTERVAL;
        long j3 = SimpleSettings.Misc.getLong(context, SimpleSettingKeys.KEY_CATCH_LOG_UNRELEASED_MIN_INTERVAL, j2);
        long j4 = SimpleSettings.Misc.getLong(this.mContext, SimpleSettingKeys.KEY_CATCH_LOG_RELEASED_MIN_INTERVAL, j2);
        long j5 = SimpleSettings.Misc.getLong(this.mContext, SimpleSettingKeys.KEY_COLLECT_APPS_INFO_UNRELEASED_MIN_CHANGED_INTERVAL, 1800000L);
        long j6 = SimpleSettings.Misc.getLong(this.mContext, SimpleSettingKeys.KEY_COLLECT_APPS_INFO_RELEASED_MIN_CHANGED_INTERVAL, 1800000L);
        long j7 = SimpleSettings.Misc.getLong(this.mContext, SimpleSettingKeys.KEY_COLLECT_APPS_INFO_UNRELEASED_MIN_INTERVAL, 1800000L);
        long j8 = SimpleSettings.Misc.getLong(this.mContext, SimpleSettingKeys.KEY_COLLECT_APPS_INFO_RELEASED_MIN_INTERVAL, 1800000L);
        Context context2 = this.mContext;
        int i7 = ThermalInfoCollector.DEFAULT_COLLECT_TOTAL_INFO_BATTERY_LEVEL;
        int i8 = SimpleSettings.Misc.getInt(context2, SimpleSettingKeys.KEY_COLLECT_TOTAL_INFO_UNRELEASED_BATTERY_LEVEL, i7);
        int i9 = SimpleSettings.Misc.getInt(this.mContext, SimpleSettingKeys.KEY_COLLECT_TOTAL_INFO_RELEASED_BATTERY_LEVEL, i7);
        if (i2 == 0) {
            i2 = 47;
        }
        if (i3 == 0) {
            i3 = 52;
        }
        if (i4 == 0) {
            i4 = 60;
        }
        if (i5 == 0) {
            i5 = 25;
        }
        int i10 = i6 == 0 ? 3 : i6;
        if (ThermalInfoUtils.isUnReleased()) {
            this.mThermalInfoCollector.onThermalConfigChanged(z3, string, j3, j5, j7, i8);
        } else {
            this.mThermalInfoCollector.onThermalConfigChanged(z4, string2, j4, j6, j8, i9);
        }
        updateThermalConfig(i2, i3, i4, i5, i10, z2);
    }

    public void updateThermalConfig(int i2, int i3, int i4, int i5, int i6, boolean z2) {
        synchronized (this.mStatusLock) {
            try {
                this.mTemperatureThreshold = i2;
                this.mXoThermalTemperatureThreshold = i3;
                this.mBlThermalTemperatureThreshold = i4;
                this.mIsCatchBugReport = SimpleSettings.Misc.getBoolean(this.mContext, SimpleSettingKeys.KEY_CATCH_BUG_REPORT_HOT, false);
                MyThermalStatus myThermalStatus = this.mThermalStatus;
                if (myThermalStatus != null) {
                    checkThermalStateChange(myThermalStatus);
                    checkBlThermalStateChange(this.mThermalStatus);
                }
                mXoThermalTempCheckThreshold = i5;
                mBatteryLevelThreshold = i6;
                mThermalCollectorEnable = z2;
                this.mSubBatteryLevel = 0;
                if (DEBUG) {
                    Log.d(TAG, "batteryThreshold = " + i2 + ", xoThreshold = " + i3 + ", blThreshold = " + i4 + ", xoThresholdCheck = " + i5 + ", batteryLevelThreshold = " + i6 + ", thermalEnable = " + z2);
                }
                ThermalInfoCollector thermalInfoCollector = this.mThermalInfoCollector;
                if (thermalInfoCollector != null && mFeatureEnabled) {
                    if (mThermalCollectorEnable) {
                        thermalInfoCollector.startCollectInfo();
                    } else {
                        thermalInfoCollector.stopCollectInfo();
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    final class HighTempRange {
        public static final int SCENE_SCREEN_OFF_30_MINUTS = 1;
        public static final int SCENE_SCREEN_OFF_OTHERS = 2;
        public static final int SCENE_SCREEN_ON = 0;
        private static final int TOP_MAX_V = 5;
        public static final int TYPE_BECOME_BATTERY_AND_THERMAL = 8;
        public static final int TYPE_BECOME_COLD_BY_BACKLIGHT = 6;
        public static final int TYPE_BECOME_COLD_BY_BATTERY = 2;
        public static final int TYPE_BECOME_COLD_BY_THERMAL = 3;
        public static final int TYPE_BECOME_HEAT_BY_BACKLIGHT = 5;
        public static final int TYPE_BECOME_HEAT_BY_BATTERY = 0;
        public static final int TYPE_BECOME_HEAT_BY_THERMAL = 1;
        public static final int TYPE_BL_TEMP_IN_HIGH = 7;
        public static final int TYPE_TEMP_IN_HIGH = 4;
        private final ProcessInfo[] mBTopProcessInfo = new ProcessInfo[5];
        private final ProcessInfo[] mETopProcessInfo = new ProcessInfo[5];
        private final String EVENT_NAME = "highTemp";
        private final String KEY_B_TIME = NightStandbyRecord.KEY_BLUETOOTH;
        private final String KEY_B_TYPE = "tr";
        private final String KEY_TIME_LONG = "tl";
        private final String KEY_MAX_TEMP = "max";
        private final String KEY_MAX_THERMAL_TEMP = "xo_max";
        private final String KEY_AMBIENT_THERMAL_TEMP = "ambient";
        private final String KEY_HARDWARE_ID = "hardwareId";
        private final String KEY_B_TOP_P = "ps";
        private final String KEY_E_TOP_P = "pe";
        private final String KEY_INDEX = "i";
        private final String KEY_PKG = "pkg";
        private final String KEY_CPURATE = LogType.LOG_TYPE_CPU_NAME;
        private final String KEY_B_F_PKG = "bfpkg";
        private final String KEY_E_F_PKG = "efpkg";
        private final String KEY_B_SCENE = "bScene";
        private final String KEY_B_CURRENT = "bCurrent";
        private final String KEY_B_VOLTAGE = "bVoltage";
        private final String KEY_B_PLUG_TYPE = "bPlugType";
        private final String BL_EVENT_NAME = "blHighTemp";
        private final String KEY_BL_STIME = "bl_start_time";
        private final String KEY_BL_ETIME = "bl_end_time";
        private final String KEY_BL_INTERVAL_TIME = "bl_interval_time";
        private final String KEY_BL_TEMP = "max_bl_temp";
        private final String[] mForegroundPkgs = new String[2];
        private int maxTemp = 0;
        private int maxThermalTemp = 0;
        private long mStartTime = 0;
        private boolean mStartTempIsBattery = true;
        private int mStartPlugType = -1;
        private int mStartCurrentNow = -1;
        private int mStartVoltageNow = -1;
        private int mStartScenario = -1;
        private long mEndTime = 0;
        private boolean mEndTempIsBattery = true;
        private boolean mIsHistory = false;
        private long mBlStartTime = 0;
        private long mBlEndTime = 0;
        private int maxBlTemp = 0;

        public HighTempRange() {
        }

        private void diagnosisTopProcess() throws Throwable {
            List<ProcessInfo> topProcessInfoByCpuRate = ThermalManager.this.getTopProcessInfoByCpuRate(5);
            ProcessInfo[] processInfoArr = this.mIsHistory ? this.mETopProcessInfo : this.mBTopProcessInfo;
            ForegroundInfo foregroundInfo = ProcessManager.getForegroundInfo();
            this.mForegroundPkgs[this.mIsHistory ? 1 : 0] = foregroundInfo == null ? "null" : foregroundInfo.mForegroundPackageName;
            for (int i2 = 0; i2 < topProcessInfoByCpuRate.size(); i2++) {
                processInfoArr[i2] = topProcessInfoByCpuRate.get(i2);
            }
        }

        private int getCurScenario() {
            if (PowerKeeperManager.getInstance().isScreenOn()) {
                return 0;
            }
            long lastScreenOffTime = PowerKeeperManager.getInstance().getLastScreenOffTime();
            return (0 == lastScreenOffTime || (SystemClock.elapsedRealtime() - lastScreenOffTime) - 1800000 < 0) ? 2 : 1;
        }

        private int getHighTempLength() {
            long jCurrentTimeMillis;
            long j2;
            if (this.mIsHistory) {
                jCurrentTimeMillis = System.currentTimeMillis();
                j2 = this.mStartTime;
            } else {
                jCurrentTimeMillis = this.mEndTime;
                j2 = this.mStartTime;
            }
            return new Long(jCurrentTimeMillis - j2).intValue();
        }

        private void reset() {
            this.maxTemp = 0;
            this.maxThermalTemp = 0;
            this.mStartTime = 0L;
            this.mStartTempIsBattery = true;
            this.mStartPlugType = -1;
            this.mStartCurrentNow = -1;
            this.mStartVoltageNow = -1;
            this.mStartScenario = -1;
            this.mEndTime = 0L;
            this.mEndTempIsBattery = true;
            this.mIsHistory = false;
            String[] strArr = this.mForegroundPkgs;
            strArr[0] = null;
            strArr[1] = null;
            for (int i2 = 0; i2 < 5; i2++) {
                this.mBTopProcessInfo[i2] = null;
                this.mETopProcessInfo[i2] = null;
            }
        }

        private void updateMaxBlTemp(MyThermalStatus myThermalStatus) {
            if (myThermalStatus != null && myThermalStatus.isBlThermalValid) {
                int i2 = this.maxBlTemp;
                int i3 = myThermalStatus.blThermalTemperature;
                if (i2 < i3) {
                    this.maxBlTemp = i3;
                }
            }
        }

        private void updateMaxTemp(MyThermalStatus myThermalStatus) {
            if (myThermalStatus == null) {
                return;
            }
            int i2 = this.maxTemp;
            int i3 = myThermalStatus.batteryTemperature;
            if (i2 < i3) {
                this.maxTemp = i3;
            }
            if (myThermalStatus.isXoTermalValid) {
                int i4 = this.maxThermalTemp;
                int i5 = myThermalStatus.xoTermalTemperature;
                if (i4 < i5) {
                    this.maxThermalTemp = i5;
                }
            }
        }

        private void uploadRecord() {
            if (Utils.isUserExperienceAndPrivacyAllowed(ThermalManager.this.mContext)) {
                JSONArray jSONArray = new JSONArray();
                JSONArray jSONArray2 = new JSONArray();
                for (int i2 = 0; i2 < 5; i2++) {
                    try {
                        if (this.mBTopProcessInfo[i2] != null) {
                            JSONObject jSONObject = new JSONObject();
                            jSONObject.put("i", i2);
                            jSONObject.put("pkg", this.mBTopProcessInfo[i2].mPName);
                            jSONObject.put(LogType.LOG_TYPE_CPU_NAME, this.mBTopProcessInfo[i2].mPercent);
                            jSONArray.put(jSONObject);
                        }
                    } catch (JSONException e2) {
                        Log.e(ThermalManager.TAG, "HighTempRange uploadRecord", e2);
                        return;
                    }
                }
                for (int i3 = 0; i3 < 5; i3++) {
                    if (this.mETopProcessInfo[i3] != null) {
                        JSONObject jSONObject2 = new JSONObject();
                        jSONObject2.put("i", i3);
                        jSONObject2.put("pkg", this.mETopProcessInfo[i3].mPName);
                        jSONObject2.put(LogType.LOG_TYPE_CPU_NAME, this.mETopProcessInfo[i3].mPercent);
                        jSONArray2.put(jSONObject2);
                    }
                }
                ArrayMap arrayMap = new ArrayMap();
                arrayMap.put(NightStandbyRecord.KEY_BLUETOOTH, Long.valueOf(this.mStartTime));
                arrayMap.put("tr", String.valueOf(this.mStartTempIsBattery));
                arrayMap.put("tl", Integer.valueOf(getHighTempLength()));
                arrayMap.put("max", Integer.valueOf(this.maxTemp));
                arrayMap.put("xo_max", Integer.valueOf(this.maxThermalTemp));
                arrayMap.put("bScene", Integer.valueOf(this.mStartScenario));
                arrayMap.put("bPlugType", Integer.valueOf(this.mStartPlugType));
                arrayMap.put("bCurrent", Integer.valueOf(this.mStartCurrentNow));
                arrayMap.put("bVoltage", Integer.valueOf(this.mStartVoltageNow));
                arrayMap.put("ps", jSONArray.toString());
                arrayMap.put("pe", jSONArray2.toString());
                arrayMap.put("bfpkg", this.mForegroundPkgs[0]);
                arrayMap.put("efpkg", this.mForegroundPkgs[1]);
                if (ThermalManager.DEBUG) {
                    Log.d(ThermalManager.TAG, "upload info " + arrayMap.toString());
                }
                ThermalInfoHelper.insertThermalInfo(ThermalManager.this.mContext, arrayMap.toString(), ThermalInfoCollector.PHONE_HIGHTEMP_TYPE);
            }
        }

        public void addTempEvent(MyThermalStatus myThermalStatus, int i2) throws Throwable {
            switch (i2) {
                case 0:
                case 1:
                    updateMaxTemp(myThermalStatus);
                    this.mStartTime = System.currentTimeMillis();
                    this.mStartTempIsBattery = i2 == 0;
                    this.mStartScenario = getCurScenario();
                    this.mStartPlugType = ThermalManager.this.getPlugType();
                    this.mStartCurrentNow = ThermalManager.getBatteryCurrentNow(ThermalManager.this.mContext);
                    this.mStartVoltageNow = Utils.getBatteryVoltage(ThermalManager.this.mContext);
                    this.mIsHistory = false;
                    diagnosisTopProcess();
                    if (1 == this.mStartScenario && ThermalManager.this.mIsCatchBugReport) {
                        if (ThermalManager.DEBUG) {
                            Log.d(ThermalManager.TAG, "get High temp and screen off 30 minutes, catch bug report ");
                        }
                        ThermalManager.this.catchPowerLogsWhenHighTemp();
                        break;
                    }
                    break;
                case 2:
                case 3:
                    updateMaxTemp(myThermalStatus);
                    this.mIsHistory = true;
                    this.mEndTime = System.currentTimeMillis();
                    this.mEndTempIsBattery = 2 == i2;
                    diagnosisTopProcess();
                    uploadRecord();
                    reset();
                    break;
                case 4:
                    updateMaxTemp(myThermalStatus);
                    break;
                case 5:
                    updateMaxBlTemp(myThermalStatus);
                    this.mBlStartTime = System.currentTimeMillis();
                    break;
                case 6:
                    updateMaxBlTemp(myThermalStatus);
                    this.mBlEndTime = System.currentTimeMillis();
                    uploadBlRecord();
                    this.maxBlTemp = 0;
                    break;
                case 7:
                    updateMaxBlTemp(myThermalStatus);
                    break;
                case 8:
                    updateMaxTemp(myThermalStatus);
                    this.mStartTime = System.currentTimeMillis();
                    this.mStartPlugType = ThermalManager.this.getPlugType();
                    this.mStartCurrentNow = ThermalManager.getBatteryCurrentNow(ThermalManager.this.mContext);
                    this.mStartVoltageNow = Utils.getBatteryVoltage(ThermalManager.this.mContext);
                    ThermalManager.this.mHardwareId = SystemProperties.get("ro.boot.hwversion", "");
                    diagnosisTopProcess();
                    uploadThermalRecord();
                    reset();
                    break;
            }
        }

        public void dump(PrintWriter printWriter) {
            printWriter.println("\t hot temp start at " + this.mStartTime + ", end time " + this.mEndTime + ", time long is " + getHighTempLength() + ", max temp is " + this.maxTemp);
            StringBuilder sb = new StringBuilder();
            sb.append("\t hot temp start type ");
            sb.append(this.mStartTempIsBattery);
            sb.append(", end type ");
            sb.append(this.mEndTempIsBattery);
            sb.append(", max thermal temp is ");
            sb.append(this.maxThermalTemp);
            printWriter.println(sb.toString());
            printWriter.println("\t mIsHistory = " + this.mIsHistory + " Scenario " + getCurScenario() + " plugType " + ThermalManager.this.getPlugType());
            StringBuilder sb2 = new StringBuilder();
            sb2.append("\t from ");
            sb2.append(new Date(this.mStartTime));
            sb2.append(" to ");
            sb2.append(new Date(this.mEndTime));
            printWriter.println(sb2.toString());
            StringBuilder sb3 = new StringBuilder();
            sb3.append("\t b foreground pkg is ");
            int i2 = 0;
            sb3.append(this.mForegroundPkgs[0]);
            sb3.append("\n\t e foreground pkg is ");
            sb3.append(this.mForegroundPkgs[1]);
            printWriter.println(sb3.toString());
            int i3 = 0;
            while (true) {
                ProcessInfo[] processInfoArr = this.mBTopProcessInfo;
                if (i3 >= processInfoArr.length) {
                    break;
                }
                if (processInfoArr[i3] != null) {
                    printWriter.println("\t b top " + i3 + " " + this.mBTopProcessInfo[i3].toString());
                }
                i3++;
            }
            while (true) {
                ProcessInfo[] processInfoArr2 = this.mETopProcessInfo;
                if (i2 >= processInfoArr2.length) {
                    return;
                }
                if (processInfoArr2[i2] != null) {
                    printWriter.println("\t e top " + i2 + " " + this.mETopProcessInfo[i2].toString());
                }
                i2++;
            }
        }

        public JSONObject toJson() {
            JSONObject jSONObject = new JSONObject();
            try {
                JSONArray jSONArray = new JSONArray();
                for (int i2 = 0; i2 < 5; i2++) {
                    if (this.mBTopProcessInfo[i2] != null) {
                        JSONObject jSONObject2 = new JSONObject();
                        jSONObject2.put("i", i2);
                        jSONObject2.put("pkg", this.mBTopProcessInfo[i2].mPName);
                        jSONObject2.put(LogType.LOG_TYPE_CPU_NAME, this.mBTopProcessInfo[i2].mPercent);
                        jSONArray.put(jSONObject2);
                    }
                }
                jSONObject.put(NightStandbyRecord.KEY_BLUETOOTH, this.mStartTime);
                jSONObject.put("xo_max", this.maxThermalTemp);
                jSONObject.put("max", this.maxTemp);
                jSONObject.put("bPlugType", this.mStartPlugType);
                jSONObject.put("bCurrent", this.mStartCurrentNow);
                jSONObject.put("bVoltage", this.mStartVoltageNow);
                if (!jSONArray.toString().equals("")) {
                    jSONObject.put("ps", jSONArray.toString());
                }
                jSONObject.put("bfpkg", this.mForegroundPkgs[0]);
                jSONObject.put("ambient", ThermalManager.this.mCurrentAmbientTemp);
                jSONObject.put("hardwareId", ThermalManager.this.mHardwareId);
                if (ThermalManager.DEBUG) {
                    Log.d(ThermalManager.TAG, "toJson()---->" + jSONObject.toString());
                }
            } catch (JSONException e2) {
                Log.e(ThermalManager.TAG, "toJson", e2);
            }
            return jSONObject;
        }

        private void uploadBlRecord() {
        }

        private void uploadThermalRecord() {
        }
    }

    private void cccUploadRecord() {
    }
}
