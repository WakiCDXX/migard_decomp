package com.miui.powerkeeper.powerchecker;

import android.content.Context;
import android.os.Build;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class AppCommonPolicyMaker extends PowerCheckerPolicyMaker {
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    private static final String TAG = "AppCommonPolicyMaker";
    private PowerCheckerCloudPolicy mCloudPolicy;
    private Context mContext;
    private PowerCheckerProcessNotifier mNotifier;

    public AppCommonPolicyMaker(Context context, PowerCheckerCloudPolicy powerCheckerCloudPolicy, PowerCheckerProcessNotifier powerCheckerProcessNotifier) {
        this.mCloudPolicy = powerCheckerCloudPolicy;
        this.mNotifier = powerCheckerProcessNotifier;
        this.mContext = context;
    }

    /* JADX WARN: Code duplicated, block: B:15:0x0039  */
    private boolean IsAudioOnSenceActiveAppBackground(PowerCheckerNotifier.AppPowerExceedInfo appPowerExceedInfo) {
        double d2;
        long j2 = appPowerExceedInfo.info.screenOffAudioOnTimeMs;
        if (j2 <= 0) {
            return false;
        }
        long batteryRealTimeInc = this.mNotifier.getBatteryRealTimeInc() / 1000;
        long j3 = appPowerExceedInfo.info.fgActivityTimeMs;
        long j4 = batteryRealTimeInc > j3 ? batteryRealTimeInc - j3 : 0L;
        if (j4 > 0) {
            long j5 = (j4 * 10) / 100;
            if (j5 <= 0 || j2 <= j5) {
                d2 = 0.0d;
            } else {
                d2 = (j2 * 1.0d) / j5;
            }
        } else {
            d2 = 0.0d;
        }
        if (DEBUG) {
            Log.d(TAG, "AudioOn AppBackground, uid = " + appPowerExceedInfo.uid + ", audioOnTimeMs = " + j2 + ", audioOnRatio = " + d2 + ", whichBatteryRealtimeMs = " + batteryRealTimeInc + ", fgActivityTimeMs = " + j3);
        }
        return d2 >= ((double) this.mCloudPolicy.getAudioOnThreshold());
    }

    private boolean IsDownloadSenceActiveAppBackground(PowerCheckerNotifier.AppPowerExceedInfo appPowerExceedInfo) {
        long j2 = appPowerExceedInfo.info.backgroundBytes;
        if (j2 <= 0) {
            return false;
        }
        long dummyRealTimeSinceCurrent = this.mNotifier.getDummyRealTimeSinceCurrent() / 1000;
        long j3 = appPowerExceedInfo.info.fgActivityTimeMs;
        long j4 = dummyRealTimeSinceCurrent > j3 ? dummyRealTimeSinceCurrent - j3 : 0L;
        double d2 = (j2 <= 0 || j4 <= 0) ? 0.0d : (j2 * 1000.0d) / j4;
        if (DEBUG) {
            Log.d(TAG, "Download AppBackground, uid = " + appPowerExceedInfo.uid + ", Bytes = " + j2 + ", netSpeed = " + d2 + ", whichBatteryRealtimeMs = " + dummyRealTimeSinceCurrent + ", fgActivityTimeMs = " + j3);
        }
        return d2 > ((double) this.mCloudPolicy.getNetworkSpeedThreshold());
    }

    @Override // com.miui.powerkeeper.powerchecker.PowerCheckerPolicyMaker
    public int getRule(int i2, String str, PowerCheckerNotifier.PowerExceedInfo powerExceedInfo, PowerCheckerProcessStats.StatsInfo statsInfo, boolean z2) {
        int policyCount;
        int actionRule;
        int i3;
        String str2;
        PowerCheckerNotifier.AppPowerExceedInfo appPowerExceedInfo = (PowerCheckerNotifier.AppPowerExceedInfo) powerExceedInfo;
        int i4 = appPowerExceedInfo.type;
        int i5 = appPowerExceedInfo.priority;
        PowerCheckerCloudPolicy.AppPolicy appPolicy = this.mCloudPolicy.getAppPolicy(i4, i4 == 4 ? PowerCheckerCloudConfigure.getPackageNameByUid(this.mContext, i2) : str);
        if (appPolicy != null) {
            int policyLevel = appPolicy.getPolicyLevel();
            int policyCount2 = appPolicy.getPolicyCount();
            actionRule = appPolicy.getPolicyAction();
            policyCount = policyCount2;
            i3 = policyLevel;
        } else {
            int triggerLevel = this.mCloudPolicy.getTriggerLevel(i4);
            policyCount = this.mCloudPolicy.getPolicyCount(i4);
            actionRule = this.mCloudPolicy.getActionRule(i4);
            i3 = triggerLevel;
        }
        if (IsDownloadSenceActiveAppBackground(appPowerExceedInfo)) {
            Log.d(TAG, "ignore for download scene active, type = " + i4 + ", uid = " + i2 + ", pkg = " + str + ", level = " + i5);
            return 201;
        }
        if (IsAudioOnSenceActiveAppBackground(appPowerExceedInfo) && !z2) {
            Log.d(TAG, "ignore for audio scene active, type = " + i4 + ", uid = " + i2 + ", pkg = " + str + ", level = " + i5);
            return 203;
        }
        if (policyCount > 1) {
            str2 = TAG;
            if (!statsInfo.isLoosePolicyLevel(i4, i5, i3, policyCount, 110)) {
                Log.d(str2, "ignore for app loose ignored, type = " + i4 + ", uid = " + i2 + ", statsInfo = " + statsInfo);
                return 110;
            }
            Log.d(str2, "app get loose , type = " + i4 + ", uid = " + i2 + ", statsInfo = " + statsInfo);
        } else {
            str2 = TAG;
        }
        if (actionRule != 0) {
            return actionRule;
        }
        Log.d(str2, "ignore for app process ignored, type = " + i4 + ", uid = " + i2 + ", pkg = " + str + ", level = " + i5);
        return 109;
    }
}
