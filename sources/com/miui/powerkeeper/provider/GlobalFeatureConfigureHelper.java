package com.miui.powerkeeper.provider;

import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import com.google.android.collect.Lists;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class GlobalFeatureConfigureHelper {
    private static final String TAG = "GlobalFeatureConfigureHelper";

    public static void deleteGlobalConfigure(Context context, Bundle bundle) {
        deleteGlobalConfigure(context, context.getUserId(), bundle);
    }

    public static String getCloudAlarmAlign(Context context) {
        Bundle bundleQueryGlobalConfigure = queryGlobalConfigure(context);
        return bundleQueryGlobalConfigure.containsKey(GlobalFeatureConfigure.ALARM_ALIGN) ? bundleQueryGlobalConfigure.getString(GlobalFeatureConfigure.ALARM_ALIGN) : "";
    }

    public static boolean getCloudAppIdleConfigure(Context context) {
        return getCloudAppIdleConfigure(queryGlobalConfigure(context));
    }

    public static String getCloudAurogonEnable(Context context) {
        Bundle bundleQueryGlobalConfigure = queryGlobalConfigure(context);
        return bundleQueryGlobalConfigure.containsKey("aurogon_enable") ? bundleQueryGlobalConfigure.getString("aurogon_enable") : "";
    }

    public static boolean getCloudBGIdleConfigure(Context context) {
        Bundle bundleQueryGlobalConfigure = queryGlobalConfigure(context);
        if (bundleQueryGlobalConfigure.containsKey(GlobalFeatureConfigure.BG_IDLE_STATUS)) {
            return bundleQueryGlobalConfigure.getString(GlobalFeatureConfigure.BG_IDLE_STATUS).equals("true");
        }
        return false;
    }

    public static int getCloudBgIdleDelay(Context context) {
        Bundle bundleQueryGlobalConfigure = queryGlobalConfigure(context);
        if (bundleQueryGlobalConfigure.containsKey("i_delay")) {
            try {
                return Integer.parseInt(bundleQueryGlobalConfigure.getString("i_delay"));
            } catch (Exception e2) {
                Log.e(TAG, "getCloudBgIdleDelay", e2);
            }
        }
        return -2;
    }

    public static String getCloudBgKillConfig(Context context) {
        Bundle bundleQueryGlobalConfigure = queryGlobalConfigure(context);
        if (bundleQueryGlobalConfigure.containsKey(GlobalFeatureConfigure.KILL_CONFIGS)) {
            return bundleQueryGlobalConfigure.getString(GlobalFeatureConfigure.KILL_CONFIGS);
        }
        return null;
    }

    public static int getCloudBgKillDelay(Context context) {
        return getCloudBgKillDelay(queryGlobalConfigure(context));
    }

    public static boolean getCloudBgLocationConfigure(Context context) {
        return getCloudBgLocationConfigure(queryGlobalConfigure(context));
    }

    public static int getCloudBgSDelay(Context context) {
        return getCloudBgSDelay(queryGlobalConfigure(context));
    }

    public static boolean getCloudBleScanBlockConfigure(Context context) {
        return getCloudBleScanBlockConfigure(queryGlobalConfigure(context));
    }

    public static String getCloudBleScanBlockParam(Context context) {
        return getCloudBleScanBlockParam(queryGlobalConfigure(context));
    }

    public static boolean getCloudClusterConfigure(Bundle bundle) {
        if (bundle.containsKey(GlobalFeatureConfigure.CLUSTER_STATUS)) {
            return bundle.getString(GlobalFeatureConfigure.CLUSTER_STATUS).equals("true");
        }
        return false;
    }

    public static boolean getCloudFeatureConfigure(Context context) {
        return getCloudFeatureConfigure(queryGlobalConfigure(context));
    }

    public static boolean getCloudFrozenConfigure(Context context) {
        return getCloudFrozenConfigure(queryGlobalConfigure(context));
    }

    public static String getCloudFrozenNewBright(Context context) {
        Bundle bundleQueryGlobalConfigure = queryGlobalConfigure(context);
        return bundleQueryGlobalConfigure.containsKey(GlobalFeatureConfigure.BRIGHT_FROZEN) ? bundleQueryGlobalConfigure.getString(GlobalFeatureConfigure.BRIGHT_FROZEN) : "";
    }

    public static boolean getCloudFrozenNewConfigure(Context context) {
        Bundle bundleQueryGlobalConfigure = queryGlobalConfigure(context);
        if (bundleQueryGlobalConfigure.containsKey(GlobalFeatureConfigure.FROZEN_NEW_STATUS)) {
            return bundleQueryGlobalConfigure.getString(GlobalFeatureConfigure.FROZEN_NEW_STATUS).equals("true");
        }
        return false;
    }

    public static String getCloudFrozenNewWhiteList(Context context) {
        Bundle bundleQueryGlobalConfigure = queryGlobalConfigure(context);
        return bundleQueryGlobalConfigure.containsKey(GlobalFeatureConfigure.FROZEN_NEW_WHITELIST) ? bundleQueryGlobalConfigure.getString(GlobalFeatureConfigure.FROZEN_NEW_WHITELIST) : "";
    }

    public static String getCloudLaunchRestrict(Context context) {
        Bundle bundleQueryGlobalConfigure = queryGlobalConfigure(context);
        return bundleQueryGlobalConfigure.containsKey(GlobalFeatureConfigure.LAUNCH_RESTRICT) ? bundleQueryGlobalConfigure.getString(GlobalFeatureConfigure.LAUNCH_RESTRICT) : "";
    }

    public static boolean getCloudNetRechableConfigure(Context context) {
        return getCloudNetRechableConfigure(queryGlobalConfigure(context));
    }

    public static String getCloudScreenOffMilletMode(Context context) {
        Bundle bundleQueryGlobalConfigure = queryGlobalConfigure(context);
        return bundleQueryGlobalConfigure.containsKey("screenoff_millet_mode") ? bundleQueryGlobalConfigure.getString("screenoff_millet_mode") : "";
    }

    public static boolean getCloudSensorConfigure(Context context) {
        return getCloudSensorConfigure(queryGlobalConfigure(context));
    }

    public static int getCloudStandbyChainDelay(Context context) {
        return getCloudStandbyChainDelay(queryGlobalConfigure(context));
    }

    public static boolean getConfigureStatusBoolean(Context context) {
        Bundle bundleQueryGlobalConfigure = queryGlobalConfigure(context);
        if (bundleQueryGlobalConfigure.containsKey(GlobalFeatureConfigure.FEATURE_STATUS) && bundleQueryGlobalConfigure.getString(GlobalFeatureConfigure.FEATURE_STATUS).equals("true")) {
            return true;
        }
        return bundleQueryGlobalConfigure.containsKey(GlobalFeatureConfigure.FROZEN_STATUS) && bundleQueryGlobalConfigure.getString(GlobalFeatureConfigure.FROZEN_STATUS).equals("true");
    }

    public static List<String> getDozeWhiteListApps(Context context) {
        return getDozeWhiteListApps(queryGlobalConfigure(context));
    }

    public static List<String> getSystemNonCoreApps(Bundle bundle) {
        String string = bundle.containsKey(GlobalFeatureConfigure.NO_CORE_SYSTEM_APPS) ? bundle.getString(GlobalFeatureConfigure.NO_CORE_SYSTEM_APPS) : null;
        ArrayList arrayListNewArrayList = Lists.newArrayList();
        if (string != null && !"".equals(string)) {
            for (String str : string.split(":")) {
                arrayListNewArrayList.add(str);
            }
        }
        return arrayListNewArrayList;
    }

    public static List<String> getUltimateSpecialApps(Bundle bundle) {
        String string = bundle.containsKey(GlobalFeatureConfigure.LEVEL_ULTIMATE_SPECIAL_APPS) ? bundle.getString(GlobalFeatureConfigure.LEVEL_ULTIMATE_SPECIAL_APPS) : null;
        ArrayList arrayListNewArrayList = Lists.newArrayList();
        if (string != null && !"".equals(string)) {
            for (String str : string.split(":")) {
                arrayListNewArrayList.add(str);
            }
        }
        return arrayListNewArrayList;
    }

    public static String getUserConfigure(Bundle bundle) {
        return bundle.containsKey(GlobalFeatureConfigure.USER_CONFIGURE_STATUS) ? bundle.getString(GlobalFeatureConfigure.USER_CONFIGURE_STATUS) : "disable";
    }

    public static void insertGlobalConfigure(Context context, Bundle bundle) {
        insertGlobalConfigure(context, context.getUserId(), bundle);
    }

    public static Bundle queryGlobalConfigure(Context context) {
        return queryGlobalConfigure(context, context.getUserId());
    }

    public static void setUserConfigure(Context context, String str) {
        setUserConfigure(context, context.getUserId(), str);
    }

    public static void updateGlobalConfigure(Context context, Bundle bundle) {
        updateGlobalConfigure(context, context.getUserId(), bundle);
    }

    public static void deleteGlobalConfigure(Context context, int i2, Bundle bundle) {
        bundle.putInt("userId", i2);
        context.getContentResolverForUser(UserHandle.OWNER).call(GlobalFeatureConfigure.CONTENT_URI, GlobalFeatureConfigure.Method.METHOD_DELETE, (String) null, bundle);
    }

    public static boolean getCloudAppIdleConfigure(Bundle bundle) {
        if (bundle.containsKey("miui_standby")) {
            return bundle.getString("miui_standby").equals("true");
        }
        return false;
    }

    public static int getCloudBgKillDelay(Bundle bundle) {
        if (bundle.containsKey("k_delay")) {
            try {
                return Integer.parseInt(bundle.getString("k_delay"));
            } catch (Exception e2) {
                Log.e(TAG, "getCloudBgKillDelay", e2);
            }
        }
        return -2;
    }

    public static boolean getCloudBgLocationConfigure(Bundle bundle) {
        if (bundle.containsKey("bgLocation")) {
            return bundle.getString("bgLocation").equals("true");
        }
        return false;
    }

    public static int getCloudBgSDelay(Bundle bundle) {
        if (bundle.containsKey("s_delay")) {
            try {
                return Integer.parseInt(bundle.getString("s_delay"));
            } catch (Exception e2) {
                Log.e(TAG, "getCloudBgSDelay", e2);
            }
        }
        return -2;
    }

    public static boolean getCloudBleScanBlockConfigure(Bundle bundle) {
        if (bundle.containsKey(GlobalFeatureConfigure.BLE_SCAN_BLOCK_STATUS)) {
            return bundle.getString(GlobalFeatureConfigure.BLE_SCAN_BLOCK_STATUS).equals("true");
        }
        return false;
    }

    public static String getCloudBleScanBlockParam(Bundle bundle) {
        if (bundle.containsKey(GlobalFeatureConfigure.BLE_SCAN_BLOCK_PARAM)) {
            return bundle.getString(GlobalFeatureConfigure.BLE_SCAN_BLOCK_PARAM);
        }
        return null;
    }

    public static boolean getCloudFeatureConfigure(Bundle bundle) {
        if (bundle.containsKey(GlobalFeatureConfigure.FEATURE_STATUS)) {
            return bundle.getString(GlobalFeatureConfigure.FEATURE_STATUS).equals("true");
        }
        return false;
    }

    public static boolean getCloudFrozenConfigure(Bundle bundle) {
        if (bundle.containsKey(GlobalFeatureConfigure.FROZEN_STATUS)) {
            return bundle.getString(GlobalFeatureConfigure.FROZEN_STATUS).equals("true");
        }
        return false;
    }

    public static boolean getCloudNetRechableConfigure(Bundle bundle) {
        if (bundle.containsKey(GlobalFeatureConfigure.NETWORK_FEEDBACK_FEATURE)) {
            return bundle.getString(GlobalFeatureConfigure.NETWORK_FEEDBACK_FEATURE).equals("true");
        }
        return false;
    }

    public static boolean getCloudSensorConfigure(Bundle bundle) {
        if (bundle.containsKey(GlobalFeatureConfigure.SENSORS_STATUS)) {
            return bundle.getString(GlobalFeatureConfigure.SENSORS_STATUS).equals("true");
        }
        return false;
    }

    public static int getCloudStandbyChainDelay(Bundle bundle) {
        if (bundle.containsKey(GlobalFeatureConfigure.STANDBY_CHAIN_DELAY)) {
            try {
                return Integer.parseInt(bundle.getString(GlobalFeatureConfigure.STANDBY_CHAIN_DELAY));
            } catch (Exception e2) {
                Log.e(TAG, "getCloudStandbyChainDelay", e2);
            }
        }
        return -2;
    }

    public static List<String> getDozeWhiteListApps(Bundle bundle) {
        String string = bundle.containsKey(GlobalFeatureConfigure.DOZE_WHITELIST_APPS) ? bundle.getString(GlobalFeatureConfigure.DOZE_WHITELIST_APPS) : null;
        ArrayList arrayListNewArrayList = Lists.newArrayList();
        if (string != null && !"".equals(string)) {
            for (String str : string.split(":")) {
                arrayListNewArrayList.add(str);
            }
        }
        return arrayListNewArrayList;
    }

    public static void insertGlobalConfigure(Context context, int i2, Bundle bundle) {
        bundle.putInt("userId", i2);
        context.getContentResolverForUser(UserHandle.OWNER).call(GlobalFeatureConfigure.CONTENT_URI, GlobalFeatureConfigure.Method.METHOD_INSERT, (String) null, bundle);
    }

    public static Bundle queryGlobalConfigure(Context context, int i2) {
        Bundle bundle = new Bundle();
        bundle.putInt("userId", i2);
        Bundle bundleCall = context.getContentResolverForUser(UserHandle.OWNER).call(GlobalFeatureConfigure.CONTENT_URI, GlobalFeatureConfigure.Method.METHOD_QUERY, (String) null, bundle);
        return bundleCall != null ? bundleCall : new Bundle();
    }

    public static void setUserConfigure(Context context, int i2, String str) {
        Bundle bundle = new Bundle();
        bundle.putString(GlobalFeatureConfigure.USER_CONFIGURE_STATUS, str);
        updateGlobalConfigure(context, i2, bundle);
    }

    public static void updateGlobalConfigure(Context context, int i2, Bundle bundle) {
        bundle.putInt("userId", i2);
        context.getContentResolverForUser(UserHandle.OWNER).call(GlobalFeatureConfigure.CONTENT_URI, GlobalFeatureConfigure.Method.METHOD_UPDATE, (String) null, bundle);
    }

    public static String getUserConfigure(Context context) {
        return getUserConfigure(context, context.getUserId());
    }

    public static String getUserConfigure(Context context, int i2) {
        return getUserConfigure(queryGlobalConfigure(context, i2));
    }

    public static List<String> getSystemNonCoreApps(Context context) {
        return getSystemNonCoreApps(queryGlobalConfigure(context));
    }

    public static List<String> getUltimateSpecialApps(Context context) {
        return getUltimateSpecialApps(queryGlobalConfigure(context));
    }
}
