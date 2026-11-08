package com.miui.powerkeeper.feedbackcontrol;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import com.miui.powerkeeper.cloudcontrol.Constants;
import com.miui.powerkeeper.cloudcontrol.LocalUpdateUtils;
import com.miui.powerkeeper.provider.SimpleSettingKeys;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.utils.Device;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class FeedbackControlServiceUpdate {
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    private static final String JSON_KEY_CATCH_LOG_CONTROL = "catch_bugReport_control";
    private static final String JSON_KEY_TEMP_BATTERY_LEVEL_THERMAL = "temp_threshold_battery_level";
    private static final String JSON_KEY_TEMP_THRESHOLD = "temp_threshold";
    private static final String JSON_KEY_TEMP_THRESHOLD_BL_THERMAL = "temp_threshold_bl_thermal";
    private static final String JSON_KEY_TEMP_THRESHOLD_XO_THERMAL = "temp_threshold_xo_thermal";
    private static final String JSON_KEY_TEMP_THRESHOLD_XO_THERMAL_CHECK = "temp_threshold_xo_thermal_check";
    private static final String JSON_KEY_THERMAL_ENABLE = "thermal_enable";
    private static String TAG = "PowerKeeper.Feedback";

    private static boolean parseJsonCfgAndSave(Context context, String str, boolean z2) {
        if (str == null) {
            if (DEBUG) {
                Log.d(TAG, "parseJsonCfgAndSave, json is null");
            }
            return false;
        }
        try {
            String string = z2 ? new JSONObject(str).getString("common_config") : new JSONObject(str).getString("data");
            if (TextUtils.isEmpty(string)) {
                return false;
            }
            JSONObject jSONObject = new JSONObject(string);
            SimpleSettings.Misc.putBoolean(context, "enable", jSONObject.optBoolean("enable"));
            int iOptInt = jSONObject.optInt(SimpleSettingKeys.KEY_XO_TEMP_THRESHOLD, -1);
            if (iOptInt != -1) {
                SimpleSettings.Misc.putInt(context, SimpleSettingKeys.KEY_XO_TEMP_THRESHOLD, iOptInt);
            }
            int iOptInt2 = jSONObject.optInt(SimpleSettingKeys.KEY_XO_TEMP_THRESHOLDCLR, -1);
            if (iOptInt2 != -1) {
                SimpleSettings.Misc.putInt(context, SimpleSettingKeys.KEY_XO_TEMP_THRESHOLDCLR, iOptInt2);
            }
            int iOptInt3 = jSONObject.optInt(SimpleSettingKeys.KEY_APP_BG_TIME_THRESHOLD, -1);
            if (iOptInt3 != -1) {
                SimpleSettings.Misc.putInt(context, SimpleSettingKeys.KEY_APP_BG_TIME_THRESHOLD, iOptInt3);
            }
            int iOptInt4 = jSONObject.optInt(SimpleSettingKeys.KEY_APP_BG_MAX_CHECK_TIME, -1);
            if (iOptInt4 != -1) {
                SimpleSettings.Misc.putInt(context, SimpleSettingKeys.KEY_APP_BG_MAX_CHECK_TIME, iOptInt4);
            }
            int iOptInt5 = jSONObject.optInt(SimpleSettingKeys.KEY_APP_STATUS_COUNT_THRESHOLD, -1);
            if (iOptInt5 != -1) {
                SimpleSettings.Misc.putInt(context, SimpleSettingKeys.KEY_APP_STATUS_COUNT_THRESHOLD, iOptInt5);
            }
            int iOptInt6 = jSONObject.optInt(SimpleSettingKeys.KEY_APP_ACTIVE_COUNT_THRESHOLD, -1);
            if (iOptInt6 != -1) {
                SimpleSettings.Misc.putInt(context, SimpleSettingKeys.KEY_APP_ACTIVE_COUNT_THRESHOLD, iOptInt6);
            }
            int iOptInt7 = jSONObject.optInt(SimpleSettingKeys.KEY_DOWNLOAD_SPEED_THRESHOLD, -1);
            if (iOptInt7 != -1) {
                SimpleSettings.Misc.putInt(context, SimpleSettingKeys.KEY_DOWNLOAD_SPEED_THRESHOLD, iOptInt7);
            }
            int iOptInt8 = jSONObject.optInt(SimpleSettingKeys.KEY_CPU_LOAD_THRESHOLD, -1);
            if (iOptInt8 != -1) {
                SimpleSettings.Misc.putInt(context, SimpleSettingKeys.KEY_CPU_LOAD_THRESHOLD, iOptInt8);
            }
            int iOptInt9 = jSONObject.optInt(SimpleSettingKeys.KEY_MIN_POLLING_TIME, -1);
            if (iOptInt9 != -1) {
                SimpleSettings.Misc.putInt(context, SimpleSettingKeys.KEY_MIN_POLLING_TIME, iOptInt9);
            }
            int iOptInt10 = jSONObject.optInt(SimpleSettingKeys.KEY_MAX_POLLING_TIME, -1);
            if (iOptInt10 != -1) {
                SimpleSettings.Misc.putInt(context, SimpleSettingKeys.KEY_MAX_POLLING_TIME, iOptInt10);
            }
            SimpleSettings.Misc.putBoolean(context, SimpleSettingKeys.KEY_DEBUG_ENABLE, jSONObject.optBoolean(SimpleSettingKeys.KEY_DEBUG_ENABLE));
            int iOptInt11 = jSONObject.optInt(JSON_KEY_TEMP_THRESHOLD, -1);
            if (iOptInt11 != -1) {
                SimpleSettings.Misc.putInt(context, SimpleSettingKeys.KEY_TEMP_THRESHOLD, iOptInt11);
            }
            int iOptInt12 = jSONObject.optInt(JSON_KEY_TEMP_THRESHOLD_XO_THERMAL, -1);
            if (iOptInt12 != -1) {
                SimpleSettings.Misc.putInt(context, SimpleSettingKeys.KEY_TEMP_THRESHOLD_XO_THERMAL, iOptInt12);
            }
            int iOptInt13 = jSONObject.optInt(JSON_KEY_TEMP_THRESHOLD_XO_THERMAL_CHECK, -1);
            if (iOptInt13 != -1) {
                SimpleSettings.Misc.putInt(context, SimpleSettingKeys.KEY_TEMP_THRESHOLD_XO_THERMAL_CHECK, iOptInt13);
            }
            int iOptInt14 = jSONObject.optInt(JSON_KEY_TEMP_BATTERY_LEVEL_THERMAL, -1);
            if (iOptInt14 != -1) {
                SimpleSettings.Misc.putInt(context, SimpleSettingKeys.KEY_TEMP_THRESHOLD_BATTERY_LEVEL, iOptInt14);
            }
            SimpleSettings.Misc.putBoolean(context, SimpleSettingKeys.KEY_THERMAL_ENABLE, jSONObject.optBoolean(JSON_KEY_THERMAL_ENABLE, false));
            SimpleSettings.Misc.putBoolean(context, SimpleSettingKeys.KEY_CATCH_BUG_REPORT_HOT, jSONObject.optBoolean(JSON_KEY_CATCH_LOG_CONTROL, false));
            int iOptInt15 = jSONObject.optInt(JSON_KEY_TEMP_THRESHOLD_BL_THERMAL, -1);
            if (iOptInt15 != -1) {
                SimpleSettings.Misc.putInt(context, SimpleSettingKeys.KEY_TEMP_THRESHOLD_BL_THERMAL, iOptInt15);
            }
            SimpleSettings.Misc.putBoolean(context, SimpleSettingKeys.KEY_CATCH_LOG_UNRELEASED_ENABLE, jSONObject.optBoolean(SimpleSettingKeys.KEY_CATCH_LOG_UNRELEASED_ENABLE, false));
            SimpleSettings.Misc.putBoolean(context, SimpleSettingKeys.KEY_CATCH_LOG_RELEASED_ENABLE, jSONObject.optBoolean(SimpleSettingKeys.KEY_CATCH_LOG_RELEASED_ENABLE, false));
            SimpleSettings.Misc.putString(context, SimpleSettingKeys.KEY_CATCH_LOG_UNRELEASED_THRESHHOLD, jSONObject.optString(SimpleSettingKeys.KEY_CATCH_LOG_UNRELEASED_THRESHHOLD, ""));
            SimpleSettings.Misc.putString(context, SimpleSettingKeys.KEY_CATCH_LOG_RELEASED_THRESHHOLD, jSONObject.optString(SimpleSettingKeys.KEY_CATCH_LOG_RELEASED_THRESHHOLD, ""));
            long j2 = ThermalInfoCollector.DEFAULT_CATCH_LOG_MIN_INTERVAL;
            SimpleSettings.Misc.putLong(context, SimpleSettingKeys.KEY_CATCH_LOG_UNRELEASED_MIN_INTERVAL, jSONObject.optLong(SimpleSettingKeys.KEY_CATCH_LOG_UNRELEASED_MIN_INTERVAL, j2));
            SimpleSettings.Misc.putLong(context, SimpleSettingKeys.KEY_CATCH_LOG_RELEASED_MIN_INTERVAL, jSONObject.optLong(SimpleSettingKeys.KEY_CATCH_LOG_RELEASED_MIN_INTERVAL, j2));
            SimpleSettings.Misc.putLong(context, SimpleSettingKeys.KEY_COLLECT_APPS_INFO_UNRELEASED_MIN_CHANGED_INTERVAL, jSONObject.optLong(SimpleSettingKeys.KEY_COLLECT_APPS_INFO_UNRELEASED_MIN_CHANGED_INTERVAL, 1800000L));
            SimpleSettings.Misc.putLong(context, SimpleSettingKeys.KEY_COLLECT_APPS_INFO_RELEASED_MIN_CHANGED_INTERVAL, jSONObject.optLong(SimpleSettingKeys.KEY_COLLECT_APPS_INFO_RELEASED_MIN_CHANGED_INTERVAL, 1800000L));
            SimpleSettings.Misc.putLong(context, SimpleSettingKeys.KEY_COLLECT_APPS_INFO_UNRELEASED_MIN_INTERVAL, jSONObject.optLong(SimpleSettingKeys.KEY_COLLECT_APPS_INFO_UNRELEASED_MIN_INTERVAL, 1800000L));
            SimpleSettings.Misc.putLong(context, SimpleSettingKeys.KEY_COLLECT_APPS_INFO_RELEASED_MIN_INTERVAL, jSONObject.optLong(SimpleSettingKeys.KEY_COLLECT_APPS_INFO_RELEASED_MIN_INTERVAL, 1800000L));
            int i2 = ThermalInfoCollector.DEFAULT_COLLECT_TOTAL_INFO_BATTERY_LEVEL;
            SimpleSettings.Misc.putLong(context, SimpleSettingKeys.KEY_COLLECT_TOTAL_INFO_UNRELEASED_BATTERY_LEVEL, jSONObject.optLong(SimpleSettingKeys.KEY_COLLECT_TOTAL_INFO_UNRELEASED_BATTERY_LEVEL, i2));
            SimpleSettings.Misc.putLong(context, SimpleSettingKeys.KEY_COLLECT_TOTAL_INFO_RELEASED_BATTERY_LEVEL, jSONObject.optLong(SimpleSettingKeys.KEY_COLLECT_TOTAL_INFO_RELEASED_BATTERY_LEVEL, i2));
            SimpleSettings.Misc.putInt(context, "allowed_kill_battery_temp_threshhold", jSONObject.optInt("allowed_kill_battery_temp_threshhold", 48));
            SimpleSettings.Misc.putInt(context, SimpleSettingKeys.KEY_ALLOWED_KILL_SOME_BACK_PRO_BATTERY_TEMP_THRESHOLD, jSONObject.optInt(SimpleSettingKeys.KEY_ALLOWED_KILL_SOME_BACK_PRO_BATTERY_TEMP_THRESHOLD, 48));
            SimpleSettings.Misc.putInt(context, SimpleSettingKeys.KEY_ALLOWED_KILL_ALL_BACK_PRO_BATTERY_TEMP_THRESHOLD, jSONObject.optInt(SimpleSettingKeys.KEY_ALLOWED_KILL_ALL_BACK_PRO_BATTERY_TEMP_THRESHOLD, 50));
            SimpleSettings.Misc.putInt(context, SimpleSettingKeys.KEY_ALLOWED_SHOW_DIALOG_TEMP_THRESHHOLD, jSONObject.optInt(SimpleSettingKeys.KEY_ALLOWED_SHOW_DIALOG_TEMP_THRESHHOLD, 48));
            SimpleSettings.Misc.putInt(context, SimpleSettingKeys.KEY_ALLOWED_SHOW_BENCHMARK_DIALOG_TEMP_THRESHHOLD, jSONObject.optInt(SimpleSettingKeys.KEY_ALLOWED_SHOW_BENCHMARK_DIALOG_TEMP_THRESHHOLD, 48));
            SimpleSettings.Misc.putBoolean(context, SimpleSettingKeys.KEY_EXIT_FOREGROUND_APP_ENABLE, jSONObject.optBoolean(SimpleSettingKeys.KEY_EXIT_FOREGROUND_APP_ENABLE, true));
            SimpleSettings.Misc.putBoolean(context, SimpleSettingKeys.KEY_KILL_SOME_BACK_PRO_ENABLE, jSONObject.optBoolean(SimpleSettingKeys.KEY_KILL_SOME_BACK_PRO_ENABLE, true));
            SimpleSettings.Misc.putBoolean(context, SimpleSettingKeys.KEY_KILL_ALL_BACK_PRO_ENABLE, jSONObject.optBoolean(SimpleSettingKeys.KEY_KILL_ALL_BACK_PRO_ENABLE, true));
            SimpleSettings.Misc.putString(context, SimpleSettingKeys.KEY_NOT_ALLOWED_KILL_APPS, jSONObject.optString(SimpleSettingKeys.KEY_NOT_ALLOWED_KILL_APPS, ""));
            SimpleSettings.Misc.putBoolean(context, SimpleSettingKeys.KEY_EXIT_BENCHMARK_ENABLE, jSONObject.optBoolean(SimpleSettingKeys.KEY_EXIT_BENCHMARK_ENABLE, true));
            return true;
        } catch (JSONException e2) {
            Log.e(TAG, "parseJsonCfgAndSave, error" + e2);
            return false;
        }
    }

    public static void startCloudUpdate(Context context, boolean z2) {
        if (!LocalUpdateUtils.useNewCloudControl()) {
            StringBuilder sb = new StringBuilder();
            String str = Constants.CLOUD_THERMAL_CONTENT_URL;
            sb.append(str);
            sb.append(Device.getInstance().getType(context));
            sb.append("/md5");
            String strHttpGet = com.miui.powerkeeper.cloudcontrol.HttpUtils.httpGet(sb.toString(), Constants.CLOUD_THERMAL_SID);
            if (strHttpGet == null || strHttpGet.isEmpty()) {
                Log.d(TAG, "get no data for md5 : " + str);
                return;
            }
            try {
                String string = new JSONObject(strHttpGet).getString("data");
                if (SimpleSettings.Misc.getString(context, SimpleSettingKeys.KEY_THERMAL_LAST_DATA_MD5, "").equalsIgnoreCase(string) && !z2) {
                    Log.i(TAG, "No need to get remote data: " + string);
                    return;
                }
                String str2 = str + Device.getInstance().getType(context);
                String strHttpGet2 = com.miui.powerkeeper.cloudcontrol.HttpUtils.httpGet(str2, Constants.CLOUD_THERMAL_SID);
                if (strHttpGet2 != null && !strHttpGet2.isEmpty()) {
                    Log.i(TAG, "Receive remote data succeed = " + strHttpGet2);
                    if (parseJsonCfgAndSave(context, strHttpGet2, false)) {
                        SimpleSettings.Misc.putString(context, SimpleSettingKeys.KEY_THERMAL_LAST_DATA_MD5, string);
                        return;
                    }
                    return;
                }
                Log.d(TAG, "get no data for content : " + str2);
                return;
            } catch (JSONException e2) {
                Log.e(TAG, "startCloudUpdate", e2);
                return;
            } catch (Exception e3) {
                Log.e(TAG, "startCloudUpdate", e3);
                return;
            }
        }
        String strHttpGet3 = com.miui.powerkeeper.cloudcontrol.HttpUtils.httpGet(Constants.CLOUD_NEW_SERVER + Constants.CLOUD_NEW_SERVER_UUID + LocalUpdateUtils.getUUID(context) + Constants.CLOUD_NEW_SERVER_UPID + LocalUpdateUtils.getUPID(context, Constants.CLOUD_THERMAL_COMMON_UPID, z2) + Constants.CLOUD_NEW_SERVER_REGION + Constants.REGION_NAME_THERMAL + Constants.CLOUD_NEW_SERVER_BUCKET + Constants.BUCKET_NAME_THERMAL_COMMON + Constants.CLOUD_NEW_SERVER_TYPE + Device.getInstance().getType(context), Constants.CLOUD_THERMAL_NEW_SID);
        if (strHttpGet3 == null || strHttpGet3.isEmpty()) {
            Log.d(TAG, "startUpdate result is empty");
            return;
        }
        if (DEBUG) {
            Log.d(TAG, "startCloudUpdate result=" + strHttpGet3);
        }
        try {
            JSONObject jSONObject = new JSONObject(strHttpGet3);
            int i2 = jSONObject.getInt(Constants.CLOUD_NEW_SERVER_KEY_CODE);
            switch (i2) {
                case 200:
                    Log.d(TAG, "hit profile");
                    break;
                case 201:
                    Log.d(TAG, "none profile match the type");
                    break;
                case 202:
                    Log.d(TAG, "gray test, not hit");
                    break;
                case 203:
                    Log.d(TAG, "no update profile");
                    break;
                default:
                    Log.d(TAG, "unknown");
                    break;
            }
            if (i2 != 200) {
                Log.i(TAG, "no new clould configuration, returnCode = " + i2);
                return;
            }
            if (parseJsonCfgAndSave(context, jSONObject.getString("profile"), true)) {
                String string2 = jSONObject.getString(Constants.CLOUD_NEW_SERVER_KEY_UPID);
                LocalUpdateUtils.storeUPID(context, Constants.CLOUD_THERMAL_COMMON_UPID, string2);
                SimpleSettings.Misc.putString(context, SimpleSettingKeys.KEY_THERMAL_LAST_DATA_MD5, string2);
            }
        } catch (Exception e4) {
            Log.e(TAG, "startUpdate", e4);
        }
    }
}
