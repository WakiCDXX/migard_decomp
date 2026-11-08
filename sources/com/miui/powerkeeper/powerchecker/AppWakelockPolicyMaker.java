package com.miui.powerkeeper.powerchecker;

import android.os.Build;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class AppWakelockPolicyMaker extends PowerCheckerPolicyMaker {
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    private static final String TAG = "AppWakelockPolicyMaker";
    private PowerCheckerCloudPolicy mCloudPolicy;
    private PowerCheckerProcessNotifier mNotifier;

    public AppWakelockPolicyMaker(PowerCheckerCloudPolicy powerCheckerCloudPolicy, PowerCheckerProcessNotifier powerCheckerProcessNotifier) {
        this.mCloudPolicy = powerCheckerCloudPolicy;
        this.mNotifier = powerCheckerProcessNotifier;
    }

    private boolean IsAudioOnSenceActiveScreenOff(PowerCheckerNotifier.AppPowerExceedInfo appPowerExceedInfo) {
        long j2;
        PowerCheckerNotifier.BaseInfo baseInfo = appPowerExceedInfo.info;
        long j3 = baseInfo.screenOffAudioOnTimeMs;
        if (j3 <= 0) {
            return false;
        }
        long j4 = baseInfo.totalPartialSinceCharged - appPowerExceedInfo.lastInfo.totalPartialSinceCharged;
        long batteryRealTimeInc = this.mNotifier.getBatteryRealTimeInc() / 1000;
        long j5 = (10 * batteryRealTimeInc) / 100;
        double d2 = (j5 <= 0 || j3 <= j5) ? 0.0d : (j3 * 1.0d) / j5;
        boolean z2 = DEBUG;
        if (z2) {
            StringBuilder sb = new StringBuilder();
            sb.append("AudioOn ScreenOff, uid = ");
            sb.append(appPowerExceedInfo.uid);
            sb.append(", audioOnTimeMs = ");
            sb.append(j3);
            sb.append(", audioOnRatio = ");
            sb.append(d2);
            sb.append(", whichBatteryRealtimeMs = ");
            sb.append(batteryRealTimeInc);
            sb.append(", totalPartialSinceChargedInc = ");
            j2 = j4;
            sb.append(j2);
            Log.d(TAG, sb.toString());
        } else {
            j2 = j4;
        }
        if (d2 < 10 - this.mCloudPolicy.getAudioOnThreshold() && j2 > j3) {
            long j6 = j2 - j3;
            if (j5 > 0 && j6 > j5) {
                double d3 = (j6 * 1.0d) / j5;
                if (z2) {
                    Log.d(TAG, "AudioOn ScreenOff, excludeAudioPWLPriority = " + d3);
                }
                if (d3 >= this.mCloudPolicy.getTriggerLevel(1)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean IsDownloadSenceActiveScreenOff(PowerCheckerNotifier.AppPowerExceedInfo appPowerExceedInfo) {
        long j2 = appPowerExceedInfo.info.screenOffBytes;
        if (j2 <= 0) {
            return false;
        }
        long batteryRealTimeSinceCurrent = this.mNotifier.getBatteryRealTimeSinceCurrent() / 1000;
        double d2 = batteryRealTimeSinceCurrent > 0 ? (j2 * 1000.0d) / batteryRealTimeSinceCurrent : 0.0d;
        if (DEBUG) {
            Log.d(TAG, "Download ScreenOff, uid = " + appPowerExceedInfo.uid + ", Bytes = " + j2 + ", netSpeed = " + d2 + ", whichBatteryRealtimeMs = " + batteryRealTimeSinceCurrent);
        }
        return d2 > ((double) this.mCloudPolicy.getNetworkSpeedThreshold());
    }

    private boolean isResultUnreasonable(int i2) {
        int defaultLimitRatio = this.mCloudPolicy.getDefaultLimitRatio(1);
        return defaultLimitRatio > 0 && i2 > 100 / defaultLimitRatio;
    }

    @Override // com.miui.powerkeeper.powerchecker.PowerCheckerPolicyMaker
    public int getRule(int i2, String str, PowerCheckerNotifier.PowerExceedInfo powerExceedInfo, PowerCheckerProcessStats.StatsInfo statsInfo, boolean z2) {
        int triggerLevel;
        int policyCount;
        int actionRule;
        PowerCheckerNotifier.AppPowerExceedInfo appPowerExceedInfo = (PowerCheckerNotifier.AppPowerExceedInfo) powerExceedInfo;
        int i3 = appPowerExceedInfo.type;
        int i4 = appPowerExceedInfo.priority;
        PowerCheckerCloudPolicy.AppPolicy appPolicy = this.mCloudPolicy.getAppPolicy(i3, str);
        if (appPolicy != null) {
            int policyLevel = appPolicy.getPolicyLevel();
            int policyCount2 = appPolicy.getPolicyCount();
            actionRule = appPolicy.getPolicyAction();
            triggerLevel = policyLevel;
            policyCount = policyCount2;
        } else {
            triggerLevel = this.mCloudPolicy.getTriggerLevel(i3);
            policyCount = this.mCloudPolicy.getPolicyCount(i3);
            actionRule = this.mCloudPolicy.getActionRule(i3);
        }
        if (isResultUnreasonable(i4)) {
            Log.d(TAG, "app result is unreasonable, type = " + i3 + ", uid = " + i2 + ", pkg = " + str + ", level = " + i4);
            return 204;
        }
        if (IsDownloadSenceActiveScreenOff(appPowerExceedInfo)) {
            Log.d(TAG, "ignore for download scene active, type = " + i3 + ", uid = " + i2 + ", pkg = " + str + ", level = " + i4);
            return 201;
        }
        if (IsAudioOnSenceActiveScreenOff(appPowerExceedInfo) && !z2) {
            Log.d(TAG, "ignore for audio scene active, type = " + i3 + ", uid = " + i2 + ", pkg = " + str + ", level = " + i4);
            return 203;
        }
        if (!statsInfo.isContinuousAbnormal(i3, i4, triggerLevel, policyCount, 205)) {
            Log.d(TAG, "ignore for app wakelock ignored, type = " + i3 + ", uid = " + i2 + ", pkg = " + str + ", level = " + i4);
            return 205;
        }
        Log.d(TAG, "partial wakelock, type = " + i3 + ", uid = " + i2 + ", pkg = " + str + ", level = " + i4);
        if (actionRule != 0) {
            return actionRule;
        }
        Log.d(TAG, "ignore for app process ignored, type = " + i3 + ", uid = " + i2 + ", pkg = " + str + ", level = " + i4);
        return 109;
    }

    public boolean isTimeCountChecked(long j2, int i2, long j3) {
        if (j2 <= 0 || i2 <= 0 || j3 <= 0) {
            return false;
        }
        int partialWLTimeBase = this.mCloudPolicy.getPartialWLTimeBase();
        int partialWLCountBase = this.mCloudPolicy.getPartialWLCountBase();
        long j4 = ((long) (3600000 * i2)) / j2;
        long j5 = j3 / ((long) i2);
        return (j4 >= ((long) partialWLCountBase) && j5 >= ((long) (partialWLTimeBase * 4))) || (j4 >= ((long) (partialWLCountBase * 2)) && j5 >= ((long) (partialWLTimeBase * 2))) || (j4 >= ((long) (partialWLCountBase * 4)) && j5 >= ((long) partialWLTimeBase));
    }
}
