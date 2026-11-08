package com.miui.powerkeeper.cloudcontrol;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import android.util.Log;
import com.google.android.collect.Maps;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.feedbackcontrol.ThermalPerfLimitsUpdate;
import com.miui.powerkeeper.perfengine.PeCloudUpdate;
import com.miui.powerkeeper.perfshielder.PerfshielderCloudUpdate;
import com.miui.powerkeeper.powerchecker.PowerCheckerCloudUpdate;
import com.miui.powerkeeper.provider.CloudAppConfigure;
import com.miui.powerkeeper.provider.GlobalFeatureConfigure;
import com.miui.powerkeeper.provider.GlobalFeatureConfigureHelper;
import com.miui.powerkeeper.provider.SimpleSettingKeys;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.utils.Device;
import com.miui.powerkeeper.utils.Utils;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import miui.os.Build;

/* JADX INFO: loaded from: classes.dex */
public class LocalUpdateUtils {
    private static final String APP_BG_DATA_DELAY_COUNT = "set_delay_count";
    private static final String APP_BG_DATA_DELAY_TIME = "app_delay";
    private static final String APP_BG_DATA_DISABLE_LONG_TIME = "set_data_disable_long";
    private static final String APP_BG_DATA_DISABLE_SHORT_TIME = "set_data_disable_short";
    private static final String APP_BG_DATA_MAX_INACTIVE_COUNT = "set_inactive_count";
    private static final String APP_BG_DATA_MIN_DATA_KB = "set_data_kb";
    private static final String APP_BG_LOCATION_DELAY_TIME = "set_location_delay";
    private static final String APP_BG_LOCATION_DISABLE_SHORT_TIME = "set_location_disable_short";
    private static final String APP_POWER_STATE_ID = "power_state_id";
    public static final String BLE_SCAN_BLOCK_PARAM = "ble_scan_param";
    public static final String BLE_SCAN_BLOCK_STATUS = "ble_scan_block";
    public static final String DOZE_WHITELIST_APPS = "doze_whitelist_apps";
    public static final String FEATURE_ALARM_ALIGN = "alarm_align";
    public static final String FEATURE_AUROGON_ENABLE = "aurogon_enable";
    public static final String FEATURE_BG_KILL_DELAY_HOT = "kill_delay_hot";
    public static final String FEATURE_BG_LOCATION_DELAY_HOT = "location_delay_hot";
    public static final String FEATURE_BKGRND_IDLE_DELAY = "i_delay";
    public static final String FEATURE_BKGRND_IDLE_STATUS = "app_bgidle";
    public static final String FEATURE_BRIGHT_FROZEN = "bright_millet";
    private static final String FEATURE_CLUSTER_POLICY = "cluster_policy";
    public static final String FEATURE_CLUSTER_STATUS = "cluster";
    private static final String FEATURE_CONNECTION = "set_data_connection";
    public static final String FEATURE_DPFS = "dpfs_feature";
    public static final String FEATURE_FROZEN_NEW_STATUS = "frozenNew";
    public static final String FEATURE_FROZEN_NEW_WHITELIST = "frozenNew_whitelist";
    public static final String FEATURE_FROZEN_STATUS = "frozen";
    public static final String FEATURE_HOT_FEEDBACK = "hot_feedback";
    public static final String FEATURE_KILL_CONFIGS = "kill_configs";
    private static final String FEATURE_KILL_DELAY = "kill_delay";
    private static final String FEATURE_KILL_POLICY = "kill_policy";
    public static final String FEATURE_LAUNCH_RESTRICT = "launch_restrict";
    private static final String FEATURE_LOCATION = "set_location";
    public static final String FEATURE_NETWORK_FEEDBACK = "network_feedback";
    public static final String FEATURE_NETWORK_MIN_INTERVAL = "network_min_interval";
    public static final String FEATURE_SCREENOFF_MILLET_MODE = "screenoff_millet_mode";
    private static final String FEATURE_SENSOR_DELAY = "sensor_delay";
    private static final String FEATURE_SENSOR_DISABLE_TYPE = "sensor_disable_type";
    public static final String FEATURE_SENSOR_STATUS = "set_sensor";
    public static final String FEATURE_STANDBY_CHAIN_DELAY = "standby_chain_delay";
    public static final String FEATURE_STATUS = "hide_mode";
    public static final String LEVEL_ULTIMATE_SPECIAL_APPS = "level_ultimate_special_apps";
    public static final String MIUI_STANDBY_STATUS = "miui_standby";
    public static final String NO_CORE_SYSTEM_APPS = "no_core_system_apps";
    public static final String SPEED_MODE_ENABLE = "speed_mode_enable";
    public static final String TAG = "LocalUpdateUtils";
    public static final String WAKELOCK = "wakelock";
    public static final HashMap<String, String> sAppMap;
    private static final HashMap<String, String> sFeaturesMap;

    static {
        HashMap<String, String> map = new HashMap<>();
        sFeaturesMap = map;
        map.put("hide_mode", GlobalFeatureConfigure.FEATURE_STATUS);
        map.put("miui_standby", "miui_standby");
        map.put("no_core_system_apps", GlobalFeatureConfigure.NO_CORE_SYSTEM_APPS);
        map.put(DOZE_WHITELIST_APPS, GlobalFeatureConfigure.DOZE_WHITELIST_APPS);
        map.put(LEVEL_ULTIMATE_SPECIAL_APPS, GlobalFeatureConfigure.LEVEL_ULTIMATE_SPECIAL_APPS);
        map.put(BLE_SCAN_BLOCK_STATUS, GlobalFeatureConfigure.BLE_SCAN_BLOCK_STATUS);
        map.put(BLE_SCAN_BLOCK_PARAM, GlobalFeatureConfigure.BLE_SCAN_BLOCK_PARAM);
        map.put("frozen", GlobalFeatureConfigure.FROZEN_STATUS);
        map.put("frozenNew", GlobalFeatureConfigure.FROZEN_NEW_STATUS);
        map.put("frozenNew_whitelist", GlobalFeatureConfigure.FROZEN_NEW_WHITELIST);
        map.put("alarm_align", GlobalFeatureConfigure.ALARM_ALIGN);
        map.put("launch_restrict", GlobalFeatureConfigure.LAUNCH_RESTRICT);
        map.put("aurogon_enable", "aurogon_enable");
        map.put("bright_millet", GlobalFeatureConfigure.BRIGHT_FROZEN);
        map.put("screenoff_millet_mode", "screenoff_millet_mode");
        map.put(FEATURE_SENSOR_STATUS, GlobalFeatureConfigure.SENSORS_STATUS);
        map.put(FEATURE_BKGRND_IDLE_STATUS, GlobalFeatureConfigure.BG_IDLE_STATUS);
        map.put("cluster", GlobalFeatureConfigure.CLUSTER_STATUS);
        map.put(FEATURE_LOCATION, "bgLocation");
        map.put(FEATURE_CONNECTION, "bgData");
        map.put(APP_BG_DATA_DELAY_TIME, "bgDataDelayTime");
        map.put(APP_BG_DATA_DELAY_COUNT, "bgDataDelayCount");
        map.put(APP_BG_DATA_MIN_DATA_KB, "bgDataMinDataKb");
        map.put(APP_BG_DATA_MAX_INACTIVE_COUNT, "bgDataMaxInactiveCount");
        map.put(APP_BG_LOCATION_DELAY_TIME, "bgLocationDelayTime");
        map.put(APP_BG_DATA_DISABLE_SHORT_TIME, GlobalFeatureConfigure.BG_DATA_DISABLE_SHORT_TIME);
        map.put(APP_BG_DATA_DISABLE_LONG_TIME, GlobalFeatureConfigure.BG_DATA_DISABLE_LONG_TIME);
        map.put(APP_BG_LOCATION_DISABLE_SHORT_TIME, GlobalFeatureConfigure.BG_LOCATION_DISABLE_SHORT_TIME);
        map.put(FEATURE_KILL_DELAY, "k_delay");
        map.put(FEATURE_KILL_CONFIGS, GlobalFeatureConfigure.KILL_CONFIGS);
        map.put(FEATURE_KILL_POLICY, "k_policy");
        map.put(FEATURE_CLUSTER_POLICY, "c_policy");
        map.put(FEATURE_SENSOR_DELAY, "s_delay");
        map.put(FEATURE_SENSOR_DISABLE_TYPE, "s_disable_type");
        map.put("i_delay", "i_delay");
        map.put(FEATURE_BG_LOCATION_DELAY_HOT, "l_delay_hot");
        map.put(FEATURE_BG_KILL_DELAY_HOT, "k_delay_hot");
        map.put(FEATURE_HOT_FEEDBACK, GlobalFeatureConfigure.HOT_FEEDBACK_FEATURE);
        map.put(FEATURE_NETWORK_FEEDBACK, GlobalFeatureConfigure.NETWORK_FEEDBACK_FEATURE);
        map.put(FEATURE_STANDBY_CHAIN_DELAY, GlobalFeatureConfigure.STANDBY_CHAIN_DELAY);
        HashMap<String, String> mapNewHashMap = Maps.newHashMap();
        sAppMap = mapNewHashMap;
        mapNewHashMap.put(FEATURE_LOCATION, "bgLocation");
        mapNewHashMap.put(FEATURE_CONNECTION, "bgData");
        mapNewHashMap.put(APP_BG_DATA_DELAY_TIME, "bgDataDelayTime");
        mapNewHashMap.put(APP_BG_DATA_DELAY_COUNT, "bgDataDelayCount");
        mapNewHashMap.put(APP_BG_DATA_MIN_DATA_KB, "bgDataMinDataKb");
        mapNewHashMap.put(APP_BG_DATA_MAX_INACTIVE_COUNT, "bgDataMaxInactiveCount");
        mapNewHashMap.put(APP_BG_LOCATION_DELAY_TIME, "bgLocationDelayTime");
        mapNewHashMap.put(FEATURE_KILL_DELAY, "k_delay");
        mapNewHashMap.put(FEATURE_KILL_POLICY, "k_policy");
        mapNewHashMap.put(FEATURE_CLUSTER_POLICY, "c_policy");
        mapNewHashMap.put(FEATURE_SENSOR_DELAY, "s_delay");
        mapNewHashMap.put(FEATURE_SENSOR_DISABLE_TYPE, "s_disable_type");
        mapNewHashMap.put(FEATURE_BG_LOCATION_DELAY_HOT, "l_delay_hot");
        mapNewHashMap.put(FEATURE_BG_KILL_DELAY_HOT, "k_delay_hot");
        mapNewHashMap.put("i_delay", "i_delay");
        mapNewHashMap.put("power_state_id", "power_state_id");
    }

    public static boolean getCloudAllowState(Context context) {
        boolean z2 = SimpleSettings.Misc.getBoolean(context, Constants.IS_ALLOW_AUTO_CLOUD_SYNC, true);
        Log.d(TAG, "get currentCloud state: " + z2);
        return z2;
    }

    public static ContentValues getCloudAppContentValues(Context context, PowerKeeperCloudControlApp powerKeeperCloudControlApp) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("pkgName", powerKeeperCloudControlApp.appName);
        for (String str : powerKeeperCloudControlApp.action.keySet()) {
            HashMap<String, String> map = sAppMap;
            if (map.get(str) != null) {
                contentValues.put(map.get(str), String.valueOf(powerKeeperCloudControlApp.action.get(str)));
            }
        }
        return contentValues;
    }

    public static ContentValues getCloudFeatureContentValues(Context context, PowerKeeperCloudControlFeature powerKeeperCloudControlFeature) {
        String str = sFeaturesMap.get(powerKeeperCloudControlFeature.featureName);
        if (str == null) {
            return null;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(str, powerKeeperCloudControlFeature.value);
        return contentValues;
    }

    private static String getCloudServer(Context context) {
        StringBuilder sb = new StringBuilder(32);
        if (useNewCloudControl()) {
            sb.append("new");
        }
        String string = SimpleSettings.Misc.getString(context, SimpleSettingKeys.CLOUD_CURRENT_ENVIRONMENT, "");
        if (string.isEmpty()) {
            if (Build.IS_INTERNATIONAL_BUILD) {
                sb.append("International");
            } else {
                sb.append("China");
            }
        } else if (Constants.CLOUD_NEW_SERVER_CHINA.equals(string)) {
            sb.append(" force China");
        } else if (Constants.CLOUD_NEW_SERVER_STAGING.equals(string)) {
            sb.append(" force Staging");
        } else if (Constants.CLOUD_NEW_SERVER_INTERNATIONAL.equals(string)) {
            sb.append(" force International");
        }
        return sb.toString();
    }

    public static String getCloudVersion(Context context) {
        String string = SimpleSettings.Misc.getString(context, Constants.HIDEMODE_UPDATE_TIME, null);
        String string2 = SimpleSettings.Misc.getString(context, Constants.CLOUD_POWER_UPDATE_UPID, null);
        String string3 = SimpleSettings.Misc.getString(context, Constants.CLOUD_POWER_APPLIST_UPID, null);
        StringBuilder sb = new StringBuilder(256);
        sb.append("CloudVersion: LatestUpdate=");
        sb.append(string);
        sb.append(",hash=");
        sb.append(string2);
        sb.append(",applistHash=");
        sb.append(string3);
        sb.append(",model=" + Device.getModel());
        return sb.toString();
    }

    public static ContentValues getGlobalCloudFeatureContentValues(Context context, String str, String str2) {
        String str3 = sFeaturesMap.get(str);
        if (str3 == null) {
            return null;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(str3, str2);
        return contentValues;
    }

    public static String getLastUpdateTime(Context context) {
        return SimpleSettings.Misc.getString(context, Constants.HIDEMODE_UPDATE_TIME, null);
    }

    public static int getRandom(int i2) {
        return new Random().nextInt(i2);
    }

    public static String getUPID(Context context, String str, boolean z2) {
        return z2 ? "" : SimpleSettings.Misc.getString(context, str, "");
    }

    public static String getURL(Context context, String str, String str2) {
        return SimpleSettings.Misc.getString(context, str, str2);
    }

    public static String getUUID(Context context) {
        String string = SimpleSettings.Misc.getString(context, Constants.KEY_LOCAL_UUID, "");
        if (string.isEmpty()) {
            string = MiuiSettings.System.getString(context.getContentResolverForUser(UserHandle.OWNER), Constants.KEY_MQS_UUID);
            if (string == null || string.isEmpty()) {
                return "";
            }
            SimpleSettings.Misc.putString(context, Constants.KEY_LOCAL_UUID, string);
        }
        return string;
    }

    public static boolean hasSuccessfulCloudSync(Context context) {
        return !SimpleSettings.Misc.getString(context, Constants.HIDEMODE_UPDATE_TIME, "").isEmpty();
    }

    public static boolean isLocalDataOld(Context context, String str) {
        return !SimpleSettings.Misc.getString(context, Constants.LAST_DATA_MD5, "").equalsIgnoreCase(str);
    }

    public static boolean isOtaUpgrade(Context context) {
        String version = Device.getVersion();
        String string = SimpleSettings.Misc.getString(context, Constants.KEY_BUILD_VERSION, "");
        return (string.equals("") || string.equals(version)) ? false : true;
    }

    public static void putURL(Context context, String str, String str2) {
        SimpleSettings.Misc.putString(context, str, str2);
    }

    public static void saveLastDataMD5(Context context, String str) {
        SimpleSettings.Misc.putString(context, Constants.LAST_DATA_MD5, str);
    }

    public static void saveLastUpdateTime(Context context) {
        SimpleSettings.Misc.putString(context, Constants.HIDEMODE_UPDATE_TIME, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())));
    }

    public static boolean setCloudAppRule(Context context, List<ContentValues> list) {
        ContentValues[] contentValuesArr = (ContentValues[]) list.toArray(new ContentValues[list.size()]);
        Uri uri = CloudAppConfigure.CONTENT_URI;
        Bundle bundle = new Bundle();
        bundle.setClassLoader(Thread.currentThread().getContextClassLoader());
        bundle.putParcelableArray(CloudAppConfigure.Method.METHOD_OVERRIDE, contentValuesArr);
        context.getContentResolverForUser(UserHandle.OWNER).call(uri, CloudAppConfigure.Method.METHOD_OVERRIDE, (String) null, bundle);
        return true;
    }

    public static boolean setCloudFeatureRule(Context context, PowerKeeperCloudControlFeature powerKeeperCloudControlFeature) {
        String str = sFeaturesMap.get(powerKeeperCloudControlFeature.featureName);
        if (str == null) {
            return false;
        }
        Bundle bundle = new Bundle();
        bundle.putString(str, powerKeeperCloudControlFeature.value);
        GlobalFeatureConfigureHelper.updateGlobalConfigure(context, bundle);
        Log.d(TAG, "setCloudFeatureRule: " + bundle.toString());
        return true;
    }

    public static void setLastBuildVersion(Context context) {
        SimpleSettings.Misc.putString(context, Constants.KEY_BUILD_VERSION, Device.getVersion());
    }

    /* JADX WARN: Code duplicated, block: B:13:0x0032  */
    /* JADX WARN: Code duplicated, block: B:16:0x003c  */
    /* JADX WARN: Code duplicated, block: B:6:0x001e  */
    private static void setServerConfigurations(Context context) {
        boolean z2;
        boolean z3;
        String string = SimpleSettings.Misc.getString(context, SimpleSettingKeys.CLOUD_CURRENT_ENVIRONMENT, "");
        boolean z4 = true;
        boolean z5 = false;
        if (!string.isEmpty()) {
            if (string.equalsIgnoreCase(Constants.CLOUD_NEW_SERVER_CHINA)) {
                z2 = true;
                z3 = false;
            } else if (string.equalsIgnoreCase(Constants.CLOUD_NEW_SERVER_STAGING)) {
                z3 = true;
                z2 = false;
                z4 = false;
            } else if (string.equalsIgnoreCase(Constants.CLOUD_NEW_SERVER_INTERNATIONAL)) {
                z2 = false;
                z3 = false;
                z5 = true;
            } else if (string.equalsIgnoreCase(Constants.CLOUD_NEW_SERVER_RUSSION)) {
                z2 = false;
                z3 = false;
            } else {
                z2 = false;
                z3 = false;
            }
            z4 = z3;
        } else if (SystemProperties.get("ro.miui.region", "unknown").equals("RU")) {
            z2 = false;
            z3 = false;
        } else {
            if (Build.IS_INTERNATIONAL_BUILD) {
                z2 = false;
                z3 = false;
                z5 = true;
            } else {
                z2 = true;
                z3 = false;
            }
            z4 = z3;
        }
        if (z4) {
            Constants.CLOUD_NEW_SERVER = Constants.CLOUD_NEW_SERVER_RUSSION;
            Constants.CLOUD_POWER_NEW_SID = "wdFSzmfNh";
            Constants.CLOUD_PERF_ENGINE_SID = "kxIu9S9Wi";
            Constants.CLOUD_THERMAL_NEW_SID = "J6G4oVvvp";
            Constants.CLOUD_PERF_SHIELDER_SID = Constants.CLOUD_PERF_SHIELDER_SID_RUSSION;
            return;
        }
        if (z5) {
            Constants.CLOUD_NEW_SERVER = Constants.CLOUD_NEW_SERVER_INTERNATIONAL;
            Constants.CLOUD_POWER_NEW_SID = "wdFSzmfNh";
            Constants.CLOUD_PERF_ENGINE_SID = "kxIu9S9Wi";
            Constants.CLOUD_THERMAL_NEW_SID = "J6G4oVvvp";
            Constants.CLOUD_PERF_SHIELDER_SID = Constants.CLOUD_PERF_SHIELDER_SID_INTERNATIONAL;
            return;
        }
        if (z2) {
            Constants.CLOUD_NEW_SERVER = Constants.CLOUD_NEW_SERVER_CHINA;
            Constants.CLOUD_POWER_NEW_SID = Constants.CLOUD_POWER_NEW_SID_CHINA;
            Constants.CLOUD_PERF_ENGINE_SID = Constants.CLOUD_PERF_ENGINE_SID_CHINA;
            Constants.CLOUD_THERMAL_NEW_SID = Constants.CLOUD_THERMAL_NEW_SID_CHINA;
            Constants.CLOUD_PERF_SHIELDER_SID = Constants.CLOUD_PERF_SHIELDER_SID_CHINA;
            return;
        }
        if (z3) {
            Constants.CLOUD_NEW_SERVER = Constants.CLOUD_NEW_SERVER_STAGING;
            Constants.CLOUD_POWER_NEW_SID = Constants.CLOUD_POWER_NEW_SID_STAGING;
            Constants.CLOUD_PERF_ENGINE_SID = Constants.CLOUD_PERF_ENGINE_SID_STAGING;
            Constants.CLOUD_THERMAL_NEW_SID = Constants.CLOUD_THERMAL_NEW_SID_STAGING;
            Constants.CLOUD_PERF_SHIELDER_SID = Constants.CLOUD_PERF_SHIELDER_SID_STAGING;
        }
    }

    public static void startCloudSyncData(Context context, boolean z2) {
        setServerConfigurations(context);
        String currentFormatTime = Utils.getCurrentFormatTime();
        String cloudServer = getCloudServer(context);
        String strStartUpdate = CloudUpdateHideMode.startUpdate(context, z2);
        String strStartUpdateAppList = CloudUpdateHideMode.startUpdateAppList(context, z2);
        StringBuilder sb = new StringBuilder(256);
        sb.append(currentFormatTime);
        sb.append(',');
        sb.append(cloudServer);
        sb.append(",update:{");
        sb.append(strStartUpdate);
        sb.append("},applist:{");
        sb.append(strStartUpdateAppList);
        sb.append('}');
        SimpleSettings.Misc.putString(context, "last_sync_cloud_info", sb.toString());
        PowerCheckerCloudUpdate.startCloudUpdate(context, z2);
        ThermalPerfLimitsUpdate.updatePerfLimits(context, z2);
        PeCloudUpdate.f(context, z2);
        EventsAggregator.getInstance(context).notifyCloudUpdateListeners();
        PerfshielderCloudUpdate.startCloudUpdate(context, z2);
    }

    public static void storeUPID(Context context, String str, String str2) {
        if (str2 == null || str2.isEmpty()) {
            return;
        }
        SimpleSettings.Misc.putString(context, str, str2);
    }

    public static boolean useNewCloudControl() {
        return true;
    }

    public static void waitAndDumpUpdateCloud(PrintWriter printWriter, final Context context, Handler handler) {
        if (handler == null) {
            printWriter.println("updateCloud ignore");
            return;
        }
        final int[] iArr = new int[0];
        synchronized (iArr) {
            handler.postAtFrontOfQueue(new Runnable() { // from class: com.miui.powerkeeper.cloudcontrol.LocalUpdateUtils.1
                @Override // java.lang.Runnable
                public void run() {
                    synchronized (iArr) {
                        LocalUpdateUtils.startCloudSyncData(context, true);
                        iArr.notify();
                    }
                }
            });
            try {
                iArr.wait(4000L);
            } catch (Exception e2) {
                printWriter.println("updateCloud error: " + e2);
            }
        }
        String string = SimpleSettings.Misc.getString(context, "last_sync_cloud_info", null);
        if (string == null || !string.contains(com.xiaomi.onetrack.g.a.f1425e)) {
            printWriter.println("updateCloud failed. " + getCloudVersion(context));
            return;
        }
        printWriter.println("updateCloud success. " + getCloudVersion(context));
    }

    public static boolean setCloudFeatureRule(Context context, List<ContentValues> list) {
        Bundle bundle = new Bundle();
        for (ContentValues contentValues : list) {
            for (String str : contentValues.keySet()) {
                bundle.putString(str, contentValues.getAsString(str));
            }
        }
        GlobalFeatureConfigureHelper.updateGlobalConfigure(context, bundle);
        Log.d(TAG, "setCloudFeatureRule: " + bundle.toString());
        return true;
    }

    /* JADX WARN: Code duplicated, block: B:31:0x00a2  */
    public static boolean setCloudAppRule(Context context, PowerKeeperCloudControlApp powerKeeperCloudControlApp) throws Throwable {
        boolean z2;
        String str = "pkgName = \"" + powerKeeperCloudControlApp.appName + "\"";
        String[] strArr = {"pkgName"};
        Uri uri = CloudAppConfigure.CONTENT_URI;
        ContentValues contentValues = new ContentValues();
        contentValues.put("pkgName", powerKeeperCloudControlApp.appName);
        for (String str2 : powerKeeperCloudControlApp.action.keySet()) {
            HashMap<String, String> map = sAppMap;
            if (map.get(str2) != null) {
                contentValues.put(map.get(str2), String.valueOf(powerKeeperCloudControlApp.action.get(str2)));
            }
        }
        boolean z3 = false;
        Cursor cursor = null;
        try {
            try {
                UserHandle userHandle = UserHandle.OWNER;
                Cursor cursorQuery = context.getContentResolverForUser(userHandle).query(uri, strArr, str, null, null);
                if (cursorQuery != null) {
                    try {
                        z2 = cursorQuery.getCount() >= 1;
                    } catch (Exception e2) {
                        e = e2;
                        cursor = cursorQuery;
                        Log.e(TAG, "setCloudAppRule", e);
                        if (cursor != null) {
                            cursor.close();
                        }
                    } catch (Throwable th) {
                        th = th;
                        cursor = cursorQuery;
                        if (cursor != null) {
                            cursor.close();
                        }
                        throw th;
                    }
                }
                if (!powerKeeperCloudControlApp.added) {
                    if (z2) {
                        context.getContentResolverForUser(userHandle).delete(uri, str, null);
                    } else if (cursorQuery != null) {
                        cursorQuery.close();
                    }
                    Log.d(TAG, "setCloudAppRule: " + z3);
                    return z3;
                }
                if (z2) {
                    context.getContentResolverForUser(userHandle).update(uri, contentValues, str, null);
                } else {
                    context.getContentResolverForUser(userHandle).insert(uri, contentValues);
                }
                z3 = true;
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e3) {
            e = e3;
        }
        Log.d(TAG, "setCloudAppRule: " + z3);
        return z3;
    }
}
