package com.miui.powerkeeper.powerchecker;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.utils.PackageUtil;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import miui.process.ProcessManager;
import miui.util.ReflectionUtils;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class PowerCheckerPolicy {
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    public static final int MIN_RULE_NOTIFY_REASON = 0;
    public static final int RULE_AUTO_KILL = 2;
    public static final int RULE_IGNORE = 0;
    public static final int RULE_IGNORE_REASON_APP_ACTIVE = 202;
    public static final int RULE_IGNORE_REASON_APP_COUNT_EXCEED = 104;
    public static final int RULE_IGNORE_REASON_APP_LOOSE_IGNORED = 110;
    public static final int RULE_IGNORE_REASON_APP_PROCESS_IGNORED = 109;
    public static final int RULE_IGNORE_REASON_APP_TIME_EXCEED = 105;
    public static final int RULE_IGNORE_REASON_APP_WAKELOCK_IGNORED = 205;
    public static final int RULE_IGNORE_REASON_AUDIO_ACTIVE = 203;
    public static final int RULE_IGNORE_REASON_BASE_1 = 100;
    public static final int RULE_IGNORE_REASON_BASE_2 = 200;
    public static final int RULE_IGNORE_REASON_CURRENT_IME = 103;
    public static final int RULE_IGNORE_REASON_DOWNLOAD_ACTIVE = 201;
    public static final int RULE_IGNORE_REASON_NOTIFY_COUNT_EXCEED = 108;
    public static final int RULE_IGNORE_REASON_PACKAGE_LOCKED = 102;
    public static final int RULE_IGNORE_REASON_PACKAGE_STOPPED = 101;
    public static final int RULE_IGNORE_REASON_TOTAL_COUNT_EXCEED = 106;
    public static final int RULE_IGNORE_REASON_TOTAL_TIME_EXCEED = 107;
    public static final int RULE_IGNORE_REASON_UNREASONABLE_RESULT = 204;
    public static final int RULE_NOTIFY = 1;
    public static final int RULE_NOTIFY_REASON_CURRENT_IME = 2;
    public static final int RULE_NOTIFY_REASON_PACKAGE_LOCKED = 1;
    public static final int RULE_NOTIFY_REASON_PROCESS_NOTIFY = 3;
    private static final int RULE_OVER_TRIGGERLEVEL = 2;
    public static final int RULE_RECORD = 4;
    public static final int RULE_USER_KILL = 3;
    private static final String TAG = "PowerChecker.Policy";
    private static Method sGetActivePlaybackConfigurations;
    private static Method sGetClientUid;
    private static Method sIsActive;
    private static Method sToLogFriendlyString;
    private AppCommonPolicyMaker mAppCommonPolicyMaker;
    private AppWakelockPolicyMaker mAppWakelockPolicyMaker;
    private PowerCheckerBatteryStats mBatteryStats;
    private Context mContext;
    private PowerCheckerProcessNotifier mNotifier;
    private PowerCheckerCloudPolicy mPowerCheckerCloudPolicy;
    private PowerCheckerProcessStats mProcessStats;
    private SystemModuleCpuPolicyMaker mSystemModuleCpuPolicyMaker;
    private final String LAUNCHER_PKG = SystemProperties.get("ro.miui.product.home", "com.miui.home");
    ArrayList<Integer> mBgNetworkActiveUids = new ArrayList<>();

    public static class PolicyResult {
        int mActionRule;
        int mActiveType;
        boolean mIgnoreOverLimit;

        public PolicyResult(int i2) {
            this(i2, 0, false);
        }

        public String toString() {
            return "PolicyResult:rule=" + this.mActionRule + ", type=" + this.mActiveType + ", ignore=" + this.mIgnoreOverLimit;
        }

        public PolicyResult(int i2, boolean z2) {
            this(i2, 0, z2);
        }

        public PolicyResult(int i2, int i3, boolean z2) {
            this.mActionRule = i2;
            this.mActiveType = i3;
            this.mIgnoreOverLimit = z2;
        }
    }

    static {
        sGetActivePlaybackConfigurations = null;
        sGetClientUid = null;
        sIsActive = null;
        sToLogFriendlyString = null;
        try {
            sGetActivePlaybackConfigurations = ReflectionUtils.findMethodExact(ReflectionUtils.findClass("android.media.AudioManager", (ClassLoader) null), "getActivePlaybackConfigurations", new Class[0]);
            Class clsTryFindClass = ReflectionUtils.tryFindClass("android.media.AudioPlaybackConfiguration", (ClassLoader) null);
            sGetClientUid = ReflectionUtils.tryFindMethodBestMatch(clsTryFindClass, "getClientUid", new Class[0]);
            sIsActive = ReflectionUtils.tryFindMethodBestMatch(clsTryFindClass, "isActive", new Class[0]);
            sToLogFriendlyString = ReflectionUtils.tryFindMethodBestMatch(clsTryFindClass, "toLogFriendlyString", new Class[]{clsTryFindClass});
        } catch (Exception unused) {
            sGetActivePlaybackConfigurations = null;
            sGetClientUid = null;
            sIsActive = null;
            sToLogFriendlyString = null;
        }
    }

    public PowerCheckerPolicy(Context context, PowerCheckerProcessStats powerCheckerProcessStats, PowerCheckerProcessNotifier powerCheckerProcessNotifier, PowerCheckerBatteryStats powerCheckerBatteryStats) {
        this.mContext = context;
        this.mPowerCheckerCloudPolicy = new PowerCheckerCloudPolicy(context);
        this.mProcessStats = powerCheckerProcessStats;
        this.mNotifier = powerCheckerProcessNotifier;
        this.mBatteryStats = powerCheckerBatteryStats;
        this.mAppWakelockPolicyMaker = new AppWakelockPolicyMaker(this.mPowerCheckerCloudPolicy, this.mNotifier);
        this.mAppCommonPolicyMaker = new AppCommonPolicyMaker(this.mContext, this.mPowerCheckerCloudPolicy, this.mNotifier);
        this.mSystemModuleCpuPolicyMaker = new SystemModuleCpuPolicyMaker(this.mPowerCheckerCloudPolicy);
    }

    private boolean IsAudioOnSenceActiveScreenOff(PowerCheckerNotifier.CommonPowerExceedInfo commonPowerExceedInfo) {
        long j2 = commonPowerExceedInfo.screenOffTotalAudioOnTimeMs;
        if (j2 > 0) {
            long batteryRealTimeSinceCurrent = this.mNotifier.getBatteryRealTimeSinceCurrent() / 1000;
            long j3 = (10 * batteryRealTimeSinceCurrent) / 100;
            double d2 = (j3 <= 0 || j2 <= j3) ? 0.0d : (j2 * 1.0d) / j3;
            if (DEBUG) {
                Log.d(TAG, "AudioOn ScreenOff, audioOnTimeMs = " + j2 + ", audioOnRatio = " + d2 + ", whichBatteryRealtimeMs = " + batteryRealTimeSinceCurrent);
            }
            if (d2 >= 10 - this.mPowerCheckerCloudPolicy.getAudioOnThreshold()) {
                return true;
            }
        }
        return false;
    }

    private boolean IsDownloadSenceActiveScreenOff(PowerCheckerNotifier.CommonPowerExceedInfo commonPowerExceedInfo) {
        long j2 = commonPowerExceedInfo.screenOffTotalBytes;
        if (j2 <= 0) {
            return false;
        }
        long batteryRealTimeSinceCurrent = this.mNotifier.getBatteryRealTimeSinceCurrent() / 1000;
        double d2 = batteryRealTimeSinceCurrent > 0 ? (j2 * 1000.0d) / batteryRealTimeSinceCurrent : 0.0d;
        if (DEBUG) {
            Log.d(TAG, "Download ScreenOff, Bytes = " + j2 + ", netSpeed = " + d2 + ", whichBatteryRealtimeMs = " + batteryRealTimeSinceCurrent);
        }
        return d2 > ((double) this.mPowerCheckerCloudPolicy.getNetworkSpeedThreshold());
    }

    private boolean isAudioActive(int i2) {
        if (sGetActivePlaybackConfigurations != null && sGetClientUid != null && sIsActive != null && sToLogFriendlyString != null) {
            try {
                for (Object obj : (List) sGetActivePlaybackConfigurations.invoke(new AudioManager(this.mContext), null)) {
                    String str = (String) sToLogFriendlyString.invoke(null, obj);
                    if (DEBUG) {
                        Log.d(TAG, "isAudioActive, config:" + str);
                    }
                    int iIntValue = ((Integer) sGetClientUid.invoke(obj, null)).intValue();
                    boolean zBooleanValue = ((Boolean) sIsActive.invoke(obj, null)).booleanValue();
                    if (i2 == iIntValue && zBooleanValue) {
                        return true;
                    }
                }
            } catch (Exception unused) {
                Log.w(TAG, "isAudioActive, FAILED!");
            }
        }
        return false;
    }

    private boolean isIgnoreCountOverLimit(PowerCheckerProcessStats.StatsInfo statsInfo, int i2) {
        return statsInfo.getIgnoreCount(i2) >= this.mPowerCheckerCloudPolicy.getMaxIgnoreCount();
    }

    private boolean isKwlResultUnreasonable(int i2, int i3, int i4) {
        int defaultLimitRatio;
        if (i2 == 0) {
            return (i4 == 1 || i4 == 3) && (defaultLimitRatio = getDefaultLimitRatio(0)) > 0 && i3 > 100 / defaultLimitRatio;
        }
        return false;
    }

    private boolean isNotifyCountOverLimit(int i2, PowerCheckerProcessStats.StatsInfo statsInfo) {
        return statsInfo.getNotifyCount() >= this.mPowerCheckerCloudPolicy.getMaxNotifyCount();
    }

    private boolean isPackageStopped(String str) {
        try {
            ApplicationInfo applicationInfo = this.mContext.getPackageManager().getApplicationInfo(str, 0);
            return (applicationInfo == null || (applicationInfo.flags & LogType.LOG_TYPE_SAMPLING_NETWORK_MODE) == 0) ? false : true;
        } catch (Exception unused) {
            Log.d(TAG, "Exception happens when get whether package is stopped, packageName: " + str);
            return false;
        }
    }

    private boolean isProcessCountOverLimit(int i2, PowerCheckerProcessStats.StatsInfo statsInfo) {
        return statsInfo.getProcessCount() >= this.mPowerCheckerCloudPolicy.getMaxKillCount();
    }

    private boolean isRecordCountOverLimit(int i2, PowerCheckerProcessStats.StatsInfo statsInfo) {
        return statsInfo.getRecordCount() >= this.mPowerCheckerCloudPolicy.getMaxKillCount();
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        this.mPowerCheckerCloudPolicy.dump(fileDescriptor, printWriter, strArr);
    }

    public List<String> getActivePackages() {
        String[] packagesForUid;
        ArrayList arrayList = new ArrayList();
        List<Integer> activeUids = PowerKeeperManager.getInstance().getActiveController().getActiveUids(31);
        Log.d(TAG, "Get active uids from active controller: " + activeUids);
        if (sGetActivePlaybackConfigurations != null && sGetClientUid != null && sIsActive != null && sToLogFriendlyString != null) {
            try {
                for (Object obj : (List) sGetActivePlaybackConfigurations.invoke(new AudioManager(this.mContext), null)) {
                    String str = (String) sToLogFriendlyString.invoke(null, obj);
                    if (DEBUG) {
                        Log.d(TAG, "ActivePlaybackConfiguration:" + str);
                    }
                    Integer num = (Integer) sGetClientUid.invoke(obj, null);
                    int iIntValue = num.intValue();
                    if (((Boolean) sIsActive.invoke(obj, null)).booleanValue() && !activeUids.contains(num)) {
                        Log.d(TAG, "add active uid from AudioManager:" + iIntValue);
                        activeUids.add(num);
                    }
                }
            } catch (Exception unused) {
                Log.w(TAG, "isAudioActive, FAILED!");
            }
        }
        for (Integer num2 : this.mBgNetworkActiveUids) {
            if (!activeUids.contains(num2)) {
                if (DEBUG) {
                    Log.d(TAG, "add active uid from background network active apps:" + num2);
                }
                activeUids.add(num2);
            }
        }
        PackageManager packageManager = this.mContext.getPackageManager();
        for (Integer num3 : activeUids) {
            if (UserHandle.isApp(num3.intValue()) && (packagesForUid = packageManager.getPackagesForUid(num3.intValue())) != null && packagesForUid.length > 0) {
                for (String str2 : packagesForUid) {
                    if (!arrayList.contains(str2)) {
                        arrayList.add(str2);
                    }
                }
            }
        }
        Log.d(TAG, "Active apps: " + arrayList);
        return arrayList;
    }

    public void getAppRule(PowerCheckerController.PowerCheckerAppInfo powerCheckerAppInfo) {
        char c2;
        String str;
        int rule;
        int uid = powerCheckerAppInfo.getUid();
        String pkg = powerCheckerAppInfo.getPkg();
        PowerCheckerProcessStats.StatsInfo appStatsInfo = this.mProcessStats.getAppStatsInfo(uid, pkg);
        this.mProcessStats.tryReset();
        boolean zIsPackageStopped = isPackageStopped(pkg);
        boolean zIsAudioActive = isAudioActive(uid);
        boolean zIsDisguiseChecked = PowerKeeperManager.getInstance().getActiveController().isDisguiseChecked(uid);
        int appActiveType = PowerKeeperManager.getInstance().getActiveController().getAppActiveType(uid);
        boolean z2 = 2 == (appActiveType & 2);
        boolean zIsLockedApplication = ProcessManager.isLockedApplication(pkg, UserHandle.getUserId(uid));
        boolean z3 = uid == PowerKeeperManager.getInstance().getCurrentIME(UserHandle.getUserId(uid));
        for (PowerCheckerController.PowerCheckerAppExceedInfo powerCheckerAppExceedInfo : powerCheckerAppInfo.getAppExceedInfoList()) {
            PowerCheckerNotifier.AppPowerExceedInfo appPowerExceedInfo = powerCheckerAppExceedInfo.getAppPowerExceedInfo();
            int i2 = appPowerExceedInfo.type;
            if (zIsAudioActive) {
                Slog.w(TAG, "checked isDisguise = " + zIsDisguiseChecked + ", uid = " + uid + ", pkg = " + pkg);
            }
            if (zIsPackageStopped) {
                Log.d(TAG, "ignore for pkg stopped, uid = " + uid + ", pkg = " + pkg);
                powerCheckerAppExceedInfo.setPolicyResult(new PolicyResult(101, appActiveType, isIgnoreCountOverLimit(appStatsInfo, 101)));
                zIsPackageStopped = zIsPackageStopped;
                zIsAudioActive = zIsAudioActive;
            } else {
                if (zIsAudioActive && !zIsDisguiseChecked) {
                    Slog.w(TAG, "ignore for audio active from AudioManager, uid = " + uid + ", pkg = " + pkg);
                    powerCheckerAppExceedInfo.setPolicyResult(new PolicyResult(202, 1, isIgnoreCountOverLimit(appStatsInfo, 202)));
                    zIsPackageStopped = zIsPackageStopped;
                    zIsAudioActive = zIsAudioActive;
                    c2 = 2;
                } else if (z2 || (appActiveType > 0 && !zIsDisguiseChecked)) {
                    zIsPackageStopped = zIsPackageStopped;
                    zIsAudioActive = zIsAudioActive;
                    c2 = 2;
                    Slog.w(TAG, "ignore for app active, uid = " + uid + ", pkg = " + pkg + ", activeType = " + appActiveType);
                    powerCheckerAppExceedInfo.setPolicyResult(new PolicyResult(202, appActiveType, isIgnoreCountOverLimit(appStatsInfo, 202)));
                } else {
                    if (i2 == 1) {
                        str = ", uid = ";
                        rule = this.mAppWakelockPolicyMaker.getRule(uid, pkg, appPowerExceedInfo, appStatsInfo, zIsDisguiseChecked);
                    } else {
                        str = ", uid = ";
                        rule = this.mAppCommonPolicyMaker.getRule(uid, pkg, appPowerExceedInfo, appStatsInfo, zIsDisguiseChecked);
                    }
                    if (rule <= 0 || rule >= 100) {
                        c2 = 2;
                        powerCheckerAppExceedInfo.setPolicyResult(new PolicyResult(rule, appActiveType, isIgnoreCountOverLimit(appStatsInfo, rule)));
                    } else if (zIsLockedApplication) {
                        Log.d(TAG, "ignore for app locked, uid = " + uid + ", pkg = " + pkg);
                        powerCheckerAppExceedInfo.setPolicyResult(new PolicyResult(102, appActiveType, isIgnoreCountOverLimit(appStatsInfo, 102)));
                    } else if (z3) {
                        Log.d(r1, "ignore for current ime, uid = " + uid + r2 + pkg);
                        powerCheckerAppExceedInfo.setPolicyResult(new PolicyResult(103, appActiveType, isIgnoreCountOverLimit(appStatsInfo, 103)));
                    } else {
                        c2 = 2;
                        if (rule == 2 && isProcessCountOverLimit(i2, appStatsInfo)) {
                            Log.d(r1, "ignore for app count exceed, type = " + i2 + str + uid);
                            powerCheckerAppExceedInfo.setPolicyResult(new PolicyResult(104, appActiveType, isIgnoreCountOverLimit(appStatsInfo, 104)));
                        } else {
                            String str2 = str;
                            if (rule == 1 && isNotifyCountOverLimit(i2, appStatsInfo)) {
                                Log.d(r1, "ignore for notify count exceed, type = " + i2 + str2 + uid);
                                powerCheckerAppExceedInfo.setPolicyResult(new PolicyResult(108, appActiveType, isIgnoreCountOverLimit(appStatsInfo, 108)));
                            } else if (rule == 4 && isRecordCountOverLimit(i2, appStatsInfo)) {
                                Log.d(r1, "ignore for record count exceed, type = " + i2 + str2 + uid);
                                powerCheckerAppExceedInfo.setPolicyResult(new PolicyResult(104, appActiveType, isIgnoreCountOverLimit(appStatsInfo, 104)));
                            } else {
                                powerCheckerAppExceedInfo.setPolicyResult(new PolicyResult(rule, appActiveType, isIgnoreCountOverLimit(appStatsInfo, rule)));
                            }
                        }
                    }
                }
                zIsPackageStopped = zIsPackageStopped;
                zIsAudioActive = zIsAudioActive;
            }
            c2 = 2;
            zIsPackageStopped = zIsPackageStopped;
            zIsAudioActive = zIsAudioActive;
        }
    }

    public boolean getCustomerPower() {
        return this.mPowerCheckerCloudPolicy.getCustomerPower();
    }

    public int getDefaultLimitRatio(int i2) {
        return this.mPowerCheckerCloudPolicy.getDefaultLimitRatio(i2);
    }

    public int getKernelWakelockInterval() {
        return this.mPowerCheckerCloudPolicy.getKernelWakelockInterval();
    }

    public int getKernelWakeupInterval() {
        return this.mPowerCheckerCloudPolicy.getKernelWakeupInterval();
    }

    public PolicyResult getKwlRule(PowerCheckerNotifier.KWLPowerExceedInfo kWLPowerExceedInfo) {
        String str;
        boolean z2;
        int i2 = kWLPowerExceedInfo.priority;
        PowerCheckerUtils.TimerEntry timerEntry = kWLPowerExceedInfo.ktimer;
        if (timerEntry != null) {
            str = timerEntry.mName;
            z2 = false;
        } else {
            str = "kernel_wakelocks";
            z2 = true;
        }
        PowerCheckerProcessStats.StatsInfo kwlStatsInfo = this.mProcessStats.getKwlStatsInfo(str);
        this.mProcessStats.tryReset();
        int actionRule = this.mPowerCheckerCloudPolicy.getActionRule(0);
        if (actionRule == 0) {
            return new PolicyResult(0, isIgnoreCountOverLimit(kwlStatsInfo, 0));
        }
        if (!z2 && isKwlResultUnreasonable(0, i2, kWLPowerExceedInfo.kType)) {
            Log.d(TAG, "kwl result is unreasonable, type = 0, level = " + i2);
            return new PolicyResult(204, isIgnoreCountOverLimit(kwlStatsInfo, 204));
        }
        if (IsDownloadSenceActiveScreenOff(kWLPowerExceedInfo)) {
            Log.d(TAG, "ignore for download scene active, type = 0");
            return new PolicyResult(201, isIgnoreCountOverLimit(kwlStatsInfo, 201));
        }
        if (IsAudioOnSenceActiveScreenOff(kWLPowerExceedInfo)) {
            Log.d(TAG, "ignore for audio scene active, type = 0");
            return new PolicyResult(203, isIgnoreCountOverLimit(kwlStatsInfo, 203));
        }
        if (!isRecordCountOverLimit(0, kwlStatsInfo) && !isProcessCountOverLimit(0, kwlStatsInfo)) {
            return new PolicyResult(actionRule);
        }
        Log.d(TAG, "ignore for kwl count exceed, type = 0, name = " + str + ", info = " + kwlStatsInfo);
        return new PolicyResult(104, isIgnoreCountOverLimit(kwlStatsInfo, 104));
    }

    public int getMinCheckTimeInterval() {
        return this.mPowerCheckerCloudPolicy.getMinCheckTimeInterval();
    }

    public PolicyResult getSubSystemRule(int i2, PowerCheckerNotifier.SubSystemPowerExceedInfo subSystemPowerExceedInfo) {
        PowerCheckerProcessStats.StatsInfo subSystemStatsInfo = this.mProcessStats.getSubSystemStatsInfo();
        this.mProcessStats.tryReset();
        int actionRule = this.mPowerCheckerCloudPolicy.getActionRule(i2);
        if (actionRule == 0) {
            return new PolicyResult(0, isIgnoreCountOverLimit(subSystemStatsInfo, 0));
        }
        if (IsDownloadSenceActiveScreenOff(subSystemPowerExceedInfo)) {
            Log.d(TAG, "ignore for download scene active, type = " + i2);
            return new PolicyResult(201, isIgnoreCountOverLimit(subSystemStatsInfo, 201));
        }
        if (IsAudioOnSenceActiveScreenOff(subSystemPowerExceedInfo)) {
            Log.d(TAG, "ignore for audio scene active, type = " + i2);
            return new PolicyResult(203, isIgnoreCountOverLimit(subSystemStatsInfo, 203));
        }
        if (!isRecordCountOverLimit(i2, subSystemStatsInfo) && !isProcessCountOverLimit(i2, subSystemStatsInfo)) {
            return new PolicyResult(actionRule);
        }
        Log.d(TAG, "ignore for sub system count exceed, type = " + i2 + ", info = " + subSystemStatsInfo);
        return new PolicyResult(104, isIgnoreCountOverLimit(subSystemStatsInfo, 104));
    }

    public int getSystemAnomalyAction() {
        return this.mPowerCheckerCloudPolicy.getSystemAnomalyAction();
    }

    public void getSystemRule(PowerCheckerController.PowerCheckerAppInfo powerCheckerAppInfo) {
        int uid = powerCheckerAppInfo.getUid();
        String pkg = powerCheckerAppInfo.getPkg();
        PowerCheckerProcessStats.StatsInfo systemStatsInfo = this.mProcessStats.getSystemStatsInfo(uid, pkg);
        this.mProcessStats.tryReset();
        for (PowerCheckerController.PowerCheckerAppExceedInfo powerCheckerAppExceedInfo : powerCheckerAppInfo.getAppExceedInfoList()) {
            PowerCheckerNotifier.AppPowerExceedInfo appPowerExceedInfo = powerCheckerAppExceedInfo.getAppPowerExceedInfo();
            int i2 = appPowerExceedInfo.type;
            if (i2 == 4) {
                int rule = this.mSystemModuleCpuPolicyMaker.getRule(uid, pkg, appPowerExceedInfo, systemStatsInfo, false);
                if (rule <= 0 || rule >= 100) {
                    powerCheckerAppExceedInfo.setPolicyResult(new PolicyResult(rule, isIgnoreCountOverLimit(systemStatsInfo, rule)));
                } else if (isRecordCountOverLimit(i2, systemStatsInfo) || isProcessCountOverLimit(i2, systemStatsInfo)) {
                    Log.d(TAG, "ignore for system count exceed, type = " + i2 + ", uid = " + uid);
                    powerCheckerAppExceedInfo.setPolicyResult(new PolicyResult(104, isIgnoreCountOverLimit(systemStatsInfo, 104)));
                } else {
                    powerCheckerAppExceedInfo.setPolicyResult(new PolicyResult(rule, isIgnoreCountOverLimit(systemStatsInfo, rule)));
                }
            } else {
                powerCheckerAppExceedInfo.setPolicyResult(new PolicyResult(0, isIgnoreCountOverLimit(systemStatsInfo, 0)));
            }
        }
    }

    public boolean isEnable() {
        return this.mPowerCheckerCloudPolicy.isEnable();
    }

    public boolean isFlagChecked(int i2, int i3) {
        return i2 >= this.mPowerCheckerCloudPolicy.getTriggerLevel(i3) + 2;
    }

    public boolean isInBackgroundMainly(long j2, long j3) {
        if (j2 > 0 && j3 >= 0) {
            if (DEBUG) {
                Log.d(TAG, "isInBackgroundMainly, whichBatteryFgRealtimeMs = " + j3 + ", whichBatteryRealtimeMs = " + j2);
            }
            if (j2 > j3 && j2 - j3 >= (j2 * ((long) this.mPowerCheckerCloudPolicy.getInBackgroundPercent())) / 10) {
                return true;
            }
        }
        return false;
    }

    public boolean isNetworkActive(double d2) {
        return d2 > ((double) this.mPowerCheckerCloudPolicy.getNetworkSpeedThreshold());
    }

    public boolean isPwlTimeCountChecked(long j2, int i2, long j3) {
        return this.mAppWakelockPolicyMaker.isTimeCountChecked(j2, i2, j3);
    }

    public boolean isStandbyMainly(long j2, long j3) {
        if (j2 > 0 && j3 > 0) {
            if (DEBUG) {
                Log.d(TAG, "isStandbyMainly, whichBatteryScreenOffRealtimeMs = " + j3 + ", whichBatteryRealtimeMs = " + j2);
            }
            if (j2 > j3 && j3 >= (j2 * ((long) this.mPowerCheckerCloudPolicy.getScreenOffPercent())) / 10) {
                return true;
            }
        }
        return false;
    }

    public boolean preFiltered(int i2, int i3, int i4, String str, String str2) {
        String packageNameByProcessName;
        if (!this.mPowerCheckerCloudPolicy.isEnable()) {
            return true;
        }
        if (UserHandle.isApp(i3)) {
            if (PowerKeeperManager.getInstance().isUidForeground(i3)) {
                if (DEBUG) {
                    Slog.w(TAG, "skip for proc (as visible):" + i3);
                }
                return true;
            }
            String packageNameByProcessName2 = PackageUtil.getPackageNameByProcessName(str);
            if (packageNameByProcessName2 != null && packageNameByProcessName2.equals(this.LAUNCHER_PKG)) {
                Log.d(TAG, "  skip for proc (as special):" + str);
                return true;
            }
        }
        if (this.mPowerCheckerCloudPolicy.getActionRule(i2) == 0) {
            return true;
        }
        if (i2 == 1) {
            if (DEBUG) {
                Log.d(TAG, "partial wakelock have special rule for trigger level");
            }
            return false;
        }
        if (i4 < this.mPowerCheckerCloudPolicy.getTriggerLevel(i2)) {
            return true;
        }
        if (i2 != 4 || (packageNameByProcessName = PackageUtil.getPackageNameByProcessName(str)) == null || !packageNameByProcessName.equals(str2)) {
            return false;
        }
        Log.d(TAG, "  skip for proc (as foreground):" + str);
        return true;
    }

    /* JADX WARN: Code duplicated, block: B:28:0x00d7  */
    public void updateBackgroundNetworkActiveUids() {
        this.mBgNetworkActiveUids.clear();
        SparseArray<PowerCheckerBatteryStats.Uid> uidStats = this.mBatteryStats.getUidStats();
        int size = uidStats.size();
        int networkSpeedThreshold = this.mPowerCheckerCloudPolicy.getNetworkSpeedThreshold();
        long dummyRealTimeSinceCurrent = this.mNotifier.getDummyRealTimeSinceCurrent() / 1000;
        int i2 = 0;
        while (i2 < size) {
            int iKeyAt = uidStats.keyAt(i2);
            if (UserHandle.isApp(iKeyAt)) {
                long backgroundNetworkActivityBytes = uidStats.valueAt(i2).getBackgroundNetworkActivityBytes(1);
                if (backgroundNetworkActivityBytes > 0) {
                    long foregroundActivityTime = this.mBatteryStats.getForegroundActivityTime(iKeyAt, 1);
                    long j2 = dummyRealTimeSinceCurrent > foregroundActivityTime ? dummyRealTimeSinceCurrent - foregroundActivityTime : 0L;
                    double d2 = j2 > 0 ? (backgroundNetworkActivityBytes * 1000.0d) / j2 : 0.0d;
                    boolean z2 = DEBUG;
                    if (z2) {
                        Log.d(TAG, "Download AppBackground, uid = " + iKeyAt + ", Bytes = " + backgroundNetworkActivityBytes + ", netSpeed = " + d2 + ", whichBatteryRealtimeMs = " + dummyRealTimeSinceCurrent + ", fgActivityTimeMs = " + foregroundActivityTime);
                    }
                    if (d2 > networkSpeedThreshold && !this.mBgNetworkActiveUids.contains(Integer.valueOf(iKeyAt))) {
                        if (z2) {
                            Log.d(TAG, "add background network active uid :" + iKeyAt);
                        }
                        this.mBgNetworkActiveUids.add(Integer.valueOf(iKeyAt));
                    }
                } else {
                    uidStats = uidStats;
                    size = size;
                }
            } else {
                uidStats = uidStats;
                size = size;
            }
            i2++;
            size = size;
            uidStats = uidStats;
        }
        Log.d(TAG, "Active uids: " + this.mBgNetworkActiveUids);
    }

    public void updatePolicy(int i2, JSONObject jSONObject) {
        this.mPowerCheckerCloudPolicy.updatePolicy(this.mContext, i2, jSONObject);
    }

    public void updatePolicy() {
        this.mPowerCheckerCloudPolicy.updatePolicy(this.mContext);
    }
}
