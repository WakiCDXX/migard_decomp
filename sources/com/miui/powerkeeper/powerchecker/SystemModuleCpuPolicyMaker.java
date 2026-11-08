package com.miui.powerkeeper.powerchecker;

import android.os.Build;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class SystemModuleCpuPolicyMaker extends PowerCheckerPolicyMaker {
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    private static final String TAG = "SMCpuPolicyMaker";
    private PowerCheckerCloudPolicy mCloudPolicy;

    public SystemModuleCpuPolicyMaker(PowerCheckerCloudPolicy powerCheckerCloudPolicy) {
        this.mCloudPolicy = powerCheckerCloudPolicy;
    }

    @Override // com.miui.powerkeeper.powerchecker.PowerCheckerPolicyMaker
    public int getRule(int i2, String str, PowerCheckerNotifier.PowerExceedInfo powerExceedInfo, PowerCheckerProcessStats.StatsInfo statsInfo, boolean z2) {
        int policyCount;
        int policyAction;
        int policyLevel;
        PowerCheckerNotifier.AppPowerExceedInfo appPowerExceedInfo = (PowerCheckerNotifier.AppPowerExceedInfo) powerExceedInfo;
        int i3 = appPowerExceedInfo.type;
        int i4 = appPowerExceedInfo.priority;
        PowerCheckerCloudPolicy.AppPolicy appPolicy = this.mCloudPolicy.getAppPolicy(i3, str);
        if (appPolicy != null) {
            policyLevel = appPolicy.getPolicyLevel();
            policyCount = appPolicy.getPolicyCount();
            policyAction = appPolicy.getPolicyAction();
        } else {
            int triggerLevel = this.mCloudPolicy.getTriggerLevel(i3);
            policyCount = this.mCloudPolicy.getPolicyCount(i3);
            policyAction = 4;
            policyLevel = triggerLevel;
        }
        if (policyCount <= 1) {
            if (policyAction != 0) {
                return policyAction;
            }
            Log.d(TAG, "ignore for system module ignored, type = " + i3 + ", uid = " + i2 + ", pkg = " + str + ", level = " + i4);
            return 0;
        }
        if (statsInfo.isHeavyPolicyLevel(i3, i4, policyLevel, policyCount)) {
            Log.d(TAG, "system is heavy , type = " + i3 + ", uid = " + i2 + ", statsInfo = " + statsInfo);
            return policyAction;
        }
        Log.d(TAG, "ignore for system is not heavy, type = " + i3 + ", uid = " + i2 + ", statsInfo = " + statsInfo);
        return 0;
    }
}
