package com.miui.powerkeeper.powerchecker;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import com.miui.powerkeeper.utils.FileUtil;
import com.miui.powerkeeper.utils.Utils;
import com.xiaomi.channelcollection.RegisterAppChannelInfoCollectionJob;
import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import miui.os.Build;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class PowerCheckerCloudPolicy {
    private static final String TAG = "PowerChecker.ClPolicy";
    private Context mContext;
    private int mMinCheckTimeInterval = 600000;
    private int mMaxKillCount = 5;
    private int mMaxNotifyCount = 3;
    private int mMaxIgnoreCount = 1;
    private int mNetworkSpeedThreshold = PowerCheckerCloudConfigure.DEFAULT_NETWORK_SPEED_THRESHOLD;
    private int mAudioOnThreshold = 5;
    private int mScreenOffPercent = 5;
    private int mKernelWakelockInterval = 5000;
    private int mKernelWakeupInterval = PowerCheckerCloudConfigure.DEFAULT_KERNEL_WAKEUP_INTERVAL;
    private int mPartialWLTimeBase = 4000;
    private int mPartialWLCountBase = 10;
    private int mSystemAnomalyAction = 0;
    private int mInBackgroundPercent = 5;
    private boolean mEnabled = false;
    private boolean mCustomerPower = true;
    private ArrayMap<String, AppPolicy> mAppPolicies = new ArrayMap<>();
    private GlobalPolicy[] mPolicy = new GlobalPolicy[8];

    public class AppPolicy {
        private int mAction;
        private int mCount;
        private int mLevel;

        public AppPolicy(int i2, int i3, int i4) {
            this.mLevel = i2;
            this.mCount = i3;
            this.mAction = i4;
        }

        public int getPolicyAction() {
            return this.mAction;
        }

        public int getPolicyCount() {
            return this.mCount;
        }

        public int getPolicyLevel() {
            return this.mLevel;
        }

        public String toString() {
            return "AppPolicy: mLevel=" + this.mLevel + ", mCount=" + this.mCount + ", mAction=" + this.mAction;
        }
    }

    public class GlobalPolicy {
        private int mDefaultLimitRatio;
        private int mType;
        private int mTriggerLevel = 5;
        private int mActionRule = 0;

        public GlobalPolicy(int i2) {
            this.mType = i2;
            if (i2 == 5) {
                this.mDefaultLimitRatio = PowerCheckerCloudConfigure.DEFAULT_DEFAULT_WAKEUP_ALARM_LIMIT_RATIO;
            } else {
                this.mDefaultLimitRatio = 10;
            }
        }

        public int getActionRule() {
            return this.mActionRule;
        }

        public int getDefaultLimitRatio() {
            return this.mDefaultLimitRatio;
        }

        public int getTriggerLevel() {
            return this.mTriggerLevel;
        }

        public void setActionRule(int i2) {
            this.mActionRule = i2;
        }

        public void setDefaultLimitRatio(int i2) {
            this.mDefaultLimitRatio = i2;
        }

        public void setTriggerLevel(int i2) {
            this.mTriggerLevel = i2;
        }

        public String toString() {
            return "Policy[" + PowerCheckerManager.POWER_CHECKER_TYPE_NAMES[this.mType] + "]: triggerLevel=" + this.mTriggerLevel + ", actionRule=" + this.mActionRule + ", defaultLimitRatio=" + this.mDefaultLimitRatio;
        }
    }

    public PowerCheckerCloudPolicy(Context context) {
        this.mContext = context;
        for (int i2 = 0; i2 < 8; i2++) {
            this.mPolicy[i2] = new GlobalPolicy(i2);
        }
    }

    private JSONObject getNewCloud(b.a aVar, int i2) {
        if (aVar.s(i2)) {
            return aVar.l(i2);
        }
        return null;
    }

    private String getOldCloud(Context context) {
        String str = context.getFilesDir().getAbsolutePath() + "/" + PowerCheckerCloudConfigure.PROFILE_NAME;
        if (new File(str).exists()) {
            Log.d(TAG, "old-cloud load cloud config file");
            return FileUtil.readFromFile(str);
        }
        Log.d(TAG, "old-cloud cloud config file is not exist");
        if (Build.IS_INTERNATIONAL_BUILD) {
            Log.d(TAG, "old-cloud is disable by default");
            return null;
        }
        Log.d(TAG, "old-cloud load local config file");
        return Utils.getJsonFromAssert(context, PowerCheckerCloudConfigure.PROFILE_NAME);
    }

    private String getValidCloud(Context context, b.a aVar, int i2) {
        JSONObject newCloud = getNewCloud(aVar, i2);
        String oldCloud = getOldCloud(context);
        if (newCloud == null) {
            Log.d(TAG, "cloud use old-cloud");
            return oldCloud;
        }
        Log.d(TAG, "cloud use new-cloud");
        return newCloud.toString();
    }

    private void updateAbnormalConsumeConfig(String str) {
        Bundle abnormalConsumeConfig = PowerCheckerCloudConfigure.getAbnormalConsumeConfig(str);
        if (abnormalConsumeConfig == null) {
            this.mEnabled = false;
            return;
        }
        this.mEnabled = true;
        setMinCheckTimeInterval(abnormalConsumeConfig.getInt(PowerCheckerCloudConfigure.MIN_CHECK_INTERVAL));
        setMaxKillCount(abnormalConsumeConfig.getInt(PowerCheckerCloudConfigure.MAX_KILL_COUNT));
        setMaxNotifyCount(abnormalConsumeConfig.getInt(PowerCheckerCloudConfigure.MAX_NOTIFY_COUNT));
        setMaxIgnoreCount(abnormalConsumeConfig.getInt(PowerCheckerCloudConfigure.MAX_IGNORE_COUNT));
        setNetworkSpeedThreshold(abnormalConsumeConfig.getInt(PowerCheckerCloudConfigure.NETWORK_SPEED_THRESHOLD));
        setAudioOnThreshold(abnormalConsumeConfig.getInt(PowerCheckerCloudConfigure.AUDIO_ON_THRESHOLD));
        setScreenOffPercent(abnormalConsumeConfig.getInt(PowerCheckerCloudConfigure.SCREEN_OFF_PERCENT));
        setKernelWakelockInterval(abnormalConsumeConfig.getInt(PowerCheckerCloudConfigure.KERNEL_WAKELOCK_INTERVAL));
        setKernelWakeupInterval(abnormalConsumeConfig.getInt(PowerCheckerCloudConfigure.KERNEL_WAKEUP_INTERVAL));
        setPartialWLTimeBase(abnormalConsumeConfig.getInt(PowerCheckerCloudConfigure.PARTIAL_WAKELOCK_TIME_BASE));
        setPartialWLCountBase(abnormalConsumeConfig.getInt(PowerCheckerCloudConfigure.PARTIAL_WAKELOCK_COUNT_BASE));
        setSystemAnomalyAction(abnormalConsumeConfig.getInt(PowerCheckerCloudConfigure.SYSTEM_ANOMALY_ACTION));
        int[] intArray = abnormalConsumeConfig.getIntArray(PowerCheckerCloudConfigure.TRIGGER_LEVEL);
        if (intArray != null) {
            for (int i2 = 0; i2 < 8 && i2 < intArray.length; i2++) {
                this.mPolicy[i2].setTriggerLevel(intArray[i2]);
            }
        }
        int[] intArray2 = abnormalConsumeConfig.getIntArray(PowerCheckerCloudConfigure.ACTION_RULE);
        if (intArray2 != null) {
            for (int i3 = 0; i3 < 8 && i3 < intArray2.length; i3++) {
                this.mPolicy[i3].setActionRule(intArray2[i3]);
            }
        }
        int[] intArray3 = abnormalConsumeConfig.getIntArray(PowerCheckerCloudConfigure.DEFAULT_LIMIT_RATIO);
        if (intArray3 != null) {
            for (int i4 = 0; i4 < 8 && i4 < intArray3.length; i4++) {
                this.mPolicy[i4].setDefaultLimitRatio(intArray3[i4]);
            }
        }
    }

    private void updateAppPolicies(Context context, String str) {
        this.mAppPolicies.clear();
        String appPoliciesConfig = PowerCheckerCloudConfigure.getAppPoliciesConfig(str, PowerCheckerCloudConfigure.getDetectControl(getValidCloud(context, b.a.p(), b.b.f43y)));
        if (appPoliciesConfig == null) {
            return;
        }
        try {
            JSONArray jSONArray = new JSONArray(appPoliciesConfig);
            for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                JSONObject jSONObjectOptJSONObject = jSONArray.optJSONObject(i2);
                if (jSONObjectOptJSONObject != null) {
                    String strOptString = jSONObjectOptJSONObject.optString("app_name");
                    JSONArray jSONArrayOptJSONArray = jSONObjectOptJSONObject.optJSONArray(PowerCheckerCloudConfigure.POLICY_LIST);
                    if (!TextUtils.isEmpty(strOptString) && jSONArrayOptJSONArray != null) {
                        for (int i3 = 0; i3 < jSONArrayOptJSONArray.length(); i3++) {
                            JSONObject jSONObjectOptJSONObject2 = jSONArrayOptJSONArray.optJSONObject(i3);
                            if (jSONObjectOptJSONObject2 != null) {
                                String strOptString2 = jSONObjectOptJSONObject2.optString("type");
                                JSONObject jSONObjectOptJSONObject3 = jSONObjectOptJSONObject2.optJSONObject(PowerCheckerCloudConfigure.POLICY_ITEM);
                                if (!TextUtils.isEmpty(strOptString2) && jSONObjectOptJSONObject3 != null) {
                                    int i4 = Integer.parseInt(strOptString2);
                                    int iOptInt = jSONObjectOptJSONObject3.optInt("level", getTriggerLevel(i4));
                                    int iOptInt2 = jSONObjectOptJSONObject3.optInt("count", getPolicyCount(i4));
                                    int iOptInt3 = jSONObjectOptJSONObject3.optInt("action", getActionRule(i4));
                                    this.mAppPolicies.put(strOptString2 + RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT + strOptString, new AppPolicy(iOptInt, iOptInt2, iOptInt3));
                                }
                            }
                        }
                    }
                }
            }
        } catch (JSONException e2) {
            Log.e(TAG, "updateAppPolicies, bad config file" + e2);
        }
    }

    private void updateCustomerPower(String str) {
        this.mCustomerPower = PowerCheckerCloudConfigure.getPowerCustomer(str);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("PowerCheckerCloudPolicy dump info");
        printWriter.println("  mEnabled =" + this.mEnabled);
        printWriter.println("  mMinCheckTimeInterval =" + this.mMinCheckTimeInterval);
        printWriter.println("  mMaxKillCount =" + this.mMaxKillCount);
        printWriter.println("  mMaxNotifyCount =" + this.mMaxNotifyCount);
        printWriter.println("  mMaxIgnoreCount =" + this.mMaxIgnoreCount);
        printWriter.println("  mNetworkSpeedThreshold =" + this.mNetworkSpeedThreshold);
        printWriter.println("  mAudioOnThreshold =" + this.mAudioOnThreshold);
        printWriter.println("  mScreenOffPercent =" + this.mScreenOffPercent);
        printWriter.println("  mKernelWakelockInterval =" + this.mKernelWakelockInterval);
        printWriter.println("  mKernelWakeupInterval =" + this.mKernelWakeupInterval);
        printWriter.println("  mPartialWLTimeBase =" + this.mPartialWLTimeBase);
        printWriter.println("  mPartialWLCountBase =" + this.mPartialWLCountBase);
        printWriter.println("  mSystemAnomalyAction =" + this.mSystemAnomalyAction);
        for (int i2 = 0; i2 < 8; i2++) {
            printWriter.println("  " + this.mPolicy[i2]);
        }
        printWriter.println("  app policies:");
        for (int i3 = 0; i3 < this.mAppPolicies.size(); i3++) {
            printWriter.println("    " + this.mAppPolicies.keyAt(i3) + ", " + this.mAppPolicies.valueAt(i3));
        }
        printWriter.println();
    }

    public int getActionRule(int i2) {
        return this.mPolicy[i2].getActionRule();
    }

    public AppPolicy getAppPolicy(int i2, String str) {
        String str2 = i2 + RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT + str;
        if (this.mAppPolicies.containsKey(str2)) {
            return this.mAppPolicies.get(str2);
        }
        return null;
    }

    public int getAudioOnThreshold() {
        return this.mAudioOnThreshold;
    }

    public boolean getCustomerPower() {
        return this.mCustomerPower;
    }

    public int getDefaultLimitRatio(int i2) {
        return this.mPolicy[i2].getDefaultLimitRatio();
    }

    public int getInBackgroundPercent() {
        return this.mInBackgroundPercent;
    }

    public int getKernelWakelockInterval() {
        return this.mKernelWakelockInterval;
    }

    public int getKernelWakeupInterval() {
        return this.mKernelWakeupInterval;
    }

    public int getMaxIgnoreCount() {
        return this.mMaxIgnoreCount;
    }

    public int getMaxKillCount() {
        return this.mMaxKillCount;
    }

    public int getMaxNotifyCount() {
        return this.mMaxNotifyCount;
    }

    public int getMinCheckTimeInterval() {
        return this.mMinCheckTimeInterval;
    }

    public int getNetworkSpeedThreshold() {
        return this.mNetworkSpeedThreshold;
    }

    public int getPartialWLCountBase() {
        return this.mPartialWLCountBase;
    }

    public int getPartialWLTimeBase() {
        return this.mPartialWLTimeBase;
    }

    public int getPolicyCount(int i2) {
        return 1;
    }

    public int getScreenOffPercent() {
        return this.mScreenOffPercent;
    }

    public int getSystemAnomalyAction() {
        return this.mSystemAnomalyAction;
    }

    public int getTriggerLevel(int i2) {
        return this.mPolicy[i2].getTriggerLevel();
    }

    public boolean isEnable() {
        return this.mEnabled;
    }

    public void setAudioOnThreshold(int i2) {
        this.mAudioOnThreshold = i2;
    }

    public void setKernelWakelockInterval(int i2) {
        this.mKernelWakelockInterval = i2;
    }

    public void setKernelWakeupInterval(int i2) {
        this.mKernelWakeupInterval = i2;
    }

    public void setMaxIgnoreCount(int i2) {
        this.mMaxIgnoreCount = i2;
    }

    public void setMaxKillCount(int i2) {
        this.mMaxKillCount = i2;
    }

    public void setMaxNotifyCount(int i2) {
        this.mMaxNotifyCount = i2;
    }

    public void setMinCheckTimeInterval(int i2) {
        this.mMinCheckTimeInterval = i2;
    }

    public void setNetworkSpeedThreshold(int i2) {
        this.mNetworkSpeedThreshold = i2;
    }

    public void setPartialWLCountBase(int i2) {
        this.mPartialWLCountBase = i2;
    }

    public void setPartialWLTimeBase(int i2) {
        this.mPartialWLTimeBase = i2;
    }

    public void setScreenOffPercent(int i2) {
        this.mScreenOffPercent = i2;
    }

    public void setSystemAnomalyAction(int i2) {
        this.mSystemAnomalyAction = i2;
    }

    public void updatePolicy(Context context) {
        Log.d(TAG, "update cdp-cal-pcc cloud");
        b.a aVarP = b.a.p();
        String validCloud = getValidCloud(context, aVarP, b.b.f43y);
        String validCloud2 = getValidCloud(context, aVarP, b.b.f42x);
        String validCloud3 = getValidCloud(context, aVarP, b.b.f41w);
        if (!TextUtils.isEmpty(validCloud)) {
            updateAbnormalConsumeConfig(validCloud);
        }
        if (!TextUtils.isEmpty(validCloud2)) {
            updateAppPolicies(context, validCloud2);
        }
        if (TextUtils.isEmpty(validCloud3)) {
            return;
        }
        updateCustomerPower(validCloud3);
    }

    public void updatePolicy(Context context, int i2, JSONObject jSONObject) {
        String string;
        String string2;
        String string3;
        Log.d(TAG, "update cdp-cal-pcc cloud");
        if (i2 == b.b.f41w) {
            if (jSONObject == null) {
                Log.d(TAG, "pcc use old-cloud");
                string3 = getOldCloud(context);
            } else {
                Log.d(TAG, "pcc use new-cloud");
                string3 = jSONObject.toString();
            }
            if (TextUtils.isEmpty(string3)) {
                return;
            }
            updateCustomerPower(string3);
            return;
        }
        if (i2 == b.b.f43y) {
            if (jSONObject == null) {
                Log.d(TAG, "cdp use old-cloud");
                string2 = getOldCloud(context);
            } else {
                Log.d(TAG, "cdp use new-cloud");
                string2 = jSONObject.toString();
            }
            if (TextUtils.isEmpty(string2)) {
                return;
            }
            updateAbnormalConsumeConfig(string2);
            return;
        }
        if (i2 == b.b.f42x) {
            if (jSONObject == null) {
                Log.d(TAG, "cal use old-cloud");
                string = getOldCloud(context);
            } else {
                Log.d(TAG, "cal use new-cloud");
                string = jSONObject.toString();
            }
            if (TextUtils.isEmpty(string)) {
                return;
            }
            updateAppPolicies(context, string);
            return;
        }
        Log.d(TAG, "Cloud paraType is not exist");
    }
}
