package com.miui.powerkeeper.appcontrol;

import android.os.Bundle;
import com.google.android.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class HideModeStateMachineConfig {
    public static final String KEY_DELAY_MINUTE = "DELAY_MINUTE";
    public static final String KEY_HOT_DELAY_MINUTE = "HOT_DELAY_MINUTE";
    public static final String KEY_HOT_POLICY = "HOT_POLICY";
    public static final String KEY_POLICY = "POLICY";
    public static final String KEY_REGARD_BACKGROUND = "REGARD_BACKGROUND";
    public static final String KEY_REGARD_FOREGROUND_SERVICE = "REGARD_FOREGROUND_SERVICE";
    public static final String KEY_REGARD_PRETASK = "REGARD_PRETASK";
    public static final int POLICY_CONTROL_DISABLE_ALWAYS = 1;
    public static final int POLICY_CONTROL_ENABLE_ALWAYS = 0;
    public static final int POLICY_CONTROL_WITH_APP_ACTIVE = 3;
    public static final int POLICY_CONTROL_WITH_DELAY = 2;
    public static final int RULE_ALLOW = 0;
    public static final int RULE_RESTRICT = 1;
    static final String STATE_ACTIVE = "active";
    static final String STATE_INACTIVE_FINAL = "inactive_final";
    public static final String STATE_INACTIVE_LEVEL_1ST = "inactive_level_1st";
    public static final String STATE_INACTIVE_LEVEL_2ND = "inactive_level_2nd";
    public static final String STATE_INACTIVE_LEVEL_3RD = "inactive_level_3rd";
    public static final String STATE_INACTIVE_LEVEL_4TH = "inactive_level_4th";
    public static final String STRATEGY_APPIDLE = "appIdle";
    public static final String STRATEGY_BKIDLE = "bkIdle";
    public static final String STRATEGY_FROZEN = "frozen";
    public static final String STRATEGY_LOCATION = "location";
    public static final String STRATEGY_SENSOR = "sensor";
    private static volatile HideModeStateMachineConfig mConfig;
    private List<String> mStateList = Lists.newArrayList();
    private Map<String, String> mFeatureStateMap = newHashMap();
    private Map<String, FeatureConfig> mFeatureConfigMap = newHashMap();

    class FeatureConfig {
        boolean mEnabled;
        String mFeatureName;
        Bundle mFeatureOpt;

        FeatureConfig(String str, Bundle bundle) {
            this.mFeatureName = str;
            this.mFeatureOpt = bundle;
        }
    }

    private HideModeStateMachineConfig() {
        addState(null, STATE_INACTIVE_LEVEL_1ST);
        addState(STATE_INACTIVE_LEVEL_1ST, STATE_INACTIVE_LEVEL_2ND);
        addState(STATE_INACTIVE_LEVEL_2ND, STATE_INACTIVE_LEVEL_3RD);
        addState(STATE_INACTIVE_LEVEL_3RD, STATE_INACTIVE_LEVEL_4TH);
    }

    public static HideModeStateMachineConfig getInstance() {
        if (mConfig == null) {
            synchronized (HideModeStateMachineConfig.class) {
                try {
                    if (mConfig == null) {
                        mConfig = new HideModeStateMachineConfig();
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return mConfig;
    }

    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<>();
    }

    public void addFeature(String str, String str2, Bundle bundle) {
        if (this.mStateList.contains(str)) {
            FeatureConfig featureConfig = new FeatureConfig(str2, bundle != null ? (Bundle) bundle.clone() : null);
            this.mFeatureStateMap.put(str2, str);
            this.mFeatureConfigMap.put(str2, featureConfig);
        }
    }

    public void addState(String str, String str2) {
        if (str == null) {
            this.mStateList.add(0, str2);
            return;
        }
        int iIndexOf = this.mStateList.indexOf(str);
        if (iIndexOf >= 0) {
            this.mStateList.add(iIndexOf + 1, str2);
        }
    }

    public FeatureConfig getFeatureConfig(String str) {
        return this.mFeatureConfigMap.get(str);
    }

    public Map<String, FeatureConfig> getFeatureConfigMap() {
        return this.mFeatureConfigMap;
    }

    public Map<String, String> getFeatureStateMap() {
        return this.mFeatureStateMap;
    }

    public List<String> getStateList() {
        return this.mStateList;
    }
}
