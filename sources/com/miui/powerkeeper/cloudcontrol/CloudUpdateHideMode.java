package com.miui.powerkeeper.cloudcontrol;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.util.IndentingPrintWriter;
import com.google.android.collect.Lists;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.controller.AudioDisguiseController;
import com.miui.powerkeeper.provider.CloudAppConfigure;
import com.miui.powerkeeper.provider.SimpleSettingKeys;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.provider.UserConfigureHelper;
import com.miui.powerkeeper.statemachine.DisplayFrameSetting;
import com.miui.powerkeeper.statemachine.PowerStateMachine;
import com.miui.powerkeeper.utils.Constant;
import com.miui.powerkeeper.utils.Device;
import com.miui.powerkeeper.utils.Utils;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class CloudUpdateHideMode {
    private static final boolean DBG_CLOUD = Log.isLoggable("power.cloud", 3);
    public static final String FUCSWITCH = "fucSwitch";
    static final String LAST_SYNC_CLOUD_INFO = "last_sync_cloud_info";
    public static final String PARAM = "param";
    public static final String PARAM2 = "param2";
    public static final String TAG = "CloudUpdateHideMode";
    static final String TEMP_FILE = "temp.config";
    public static final String VIP = "vip";

    static boolean addOptGlobalFeature(Context context, List<ContentValues> list, JSONObject jSONObject, String str) {
        String strOptString = jSONObject.optString(str);
        String str2 = TAG;
        Log.d(str2, "key is " + str);
        Log.d(str2, "featureValue is " + strOptString);
        if (TextUtils.isEmpty(strOptString)) {
            return false;
        }
        ContentValues globalCloudFeatureContentValues = LocalUpdateUtils.getGlobalCloudFeatureContentValues(context, str, strOptString);
        if (globalCloudFeatureContentValues == null) {
            return true;
        }
        list.add(globalCloudFeatureContentValues);
        return true;
    }

    public static void dump(Context context, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        String lastUpdateTime = LocalUpdateUtils.getLastUpdateTime(context);
        String string = SimpleSettings.Misc.getString(context, Constants.APPLIST_UPDATE_TIME, null);
        if (lastUpdateTime == null) {
            indentingPrintWriter.println("None Cloud Update");
        } else {
            indentingPrintWriter.println("CloudUpdateTime is " + lastUpdateTime);
        }
        if (string == null) {
            indentingPrintWriter.println("None appCloud Update");
        } else {
            indentingPrintWriter.println("AppCloudUpdateTime is " + string);
        }
        String string2 = SimpleSettings.Misc.getString(context, Constants.CLOUD_POWER_UPDATE_UPID, null);
        if (string2 == null) {
            string2 = SimpleSettings.Misc.getString(context, Constants.LAST_DATA_MD5, null);
        }
        String string3 = SimpleSettings.Misc.getString(context, Constants.CLOUD_POWER_APPLIST_UPID, null);
        if (string3 == null) {
            string3 = SimpleSettings.Misc.getString(context, Constants.LAST_APPLIST_DATA_MD5, null);
        }
        String string4 = SimpleSettings.Misc.getString(context, LAST_SYNC_CLOUD_INFO, null);
        StringBuilder sb = new StringBuilder(256);
        sb.append("PowerCloud EffectiveInfo:{update=");
        sb.append(lastUpdateTime);
        sb.append(",updateHash=");
        sb.append(string2);
        sb.append(",applist=");
        sb.append(string);
        sb.append(",applistHash=");
        sb.append(string3);
        sb.append(",lastSyncInfo:{");
        sb.append(string4);
        sb.append('}');
        indentingPrintWriter.println(sb.toString());
        dumpUserTable(context, fileDescriptor, printWriter, strArr);
    }

    public static void dumpUserTable(Context context, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        indentingPrintWriter.println("dump  userTable");
        List<UserConfigureHelper> listCreateFromTable = UserConfigureHelper.createFromTable(context);
        for (int i2 = 0; i2 < listCreateFromTable.size(); i2++) {
            StringBuffer stringBuffer = new StringBuffer(64);
            stringBuffer.append(listCreateFromTable.get(i2).getId() + "|");
            stringBuffer.append(listCreateFromTable.get(i2).getUserId() + "|");
            stringBuffer.append(listCreateFromTable.get(i2).getPackageName() + "|");
            stringBuffer.append(listCreateFromTable.get(i2).getLastConfigured() + "|");
            stringBuffer.append(listCreateFromTable.get(i2).getBgControl() + "|");
            stringBuffer.append(listCreateFromTable.get(i2).getBgDelayMin());
            indentingPrintWriter.println(stringBuffer);
        }
        indentingPrintWriter.println("end  dump  userTable  end");
    }

    private static String getParamNameInDB(String str, String str2) {
        if (str == null || str2 == null) {
            return null;
        }
        if ("param".equals(str2)) {
            return str + "_params";
        }
        if (!PARAM2.equals(str2)) {
            return null;
        }
        return str + "_params2";
    }

    public static Boolean isApplistListNull(Context context) {
        boolean z2 = true;
        Cursor cursorQuery = null;
        try {
            try {
                cursorQuery = context.getContentResolverForUser(UserHandle.OWNER).query(CloudAppConfigure.CONTENT_URI, null, null, null, null);
                if (cursorQuery != null && cursorQuery.moveToFirst()) {
                    z2 = false;
                }
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
            } catch (Exception e2) {
                Log.e(TAG, "applistException", e2);
                if (cursorQuery != null) {
                }
            }
            Log.i(TAG, "applist if null --->" + z2);
            return Boolean.valueOf(z2);
        } catch (Throwable th) {
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            throw th;
        }
    }

    private static void logLong(String str) {
        int length = str.length();
        int i2 = 0;
        while (i2 < length) {
            int i3 = i2 + 4000;
            if (i3 >= length) {
                Log.i(TAG, str.substring(i2, length));
            } else {
                Log.i(TAG, str.substring(i2, i3));
            }
            i2 = i3;
        }
    }

    public static boolean parseApplistResult(Context context, String str, boolean z2) {
        ContentValues cloudAppContentValues;
        try {
            JSONArray jSONArrayOptJSONArray = new JSONObject(str).optJSONArray("app_list");
            ArrayList arrayList = new ArrayList();
            if (jSONArrayOptJSONArray.length() == 0) {
                return false;
            }
            boolean zUseNewCloudControl = LocalUpdateUtils.useNewCloudControl();
            int i2 = 0;
            while (i2 < jSONArrayOptJSONArray.length()) {
                PowerKeeperCloudControlApp fromJsonForNewCC = (!z2 && zUseNewCloudControl) ? PowerKeeperCloudControlApp.parseFromJsonForNewCC(jSONArrayOptJSONArray.optJSONObject(i2)) : PowerKeeperCloudControlApp.parseFromJson(jSONArrayOptJSONArray.optJSONObject(i2));
                if (fromJsonForNewCC != null && (cloudAppContentValues = LocalUpdateUtils.getCloudAppContentValues(context, fromJsonForNewCC)) != null) {
                    arrayList.add(cloudAppContentValues);
                }
                i2++;
            }
            LocalUpdateUtils.setCloudAppRule(context, arrayList);
            arrayList.clear();
            return true;
        } catch (JSONException unused) {
            Log.d(TAG, "parse json content error");
            return false;
        } catch (Exception e2) {
            Log.d(TAG, "parse cloud config error" + e2);
            return false;
        }
    }

    private static void parseFunction(Context context, JSONObject jSONObject, String str, String... strArr) {
        String strOptString = jSONObject.optString(str);
        if (Constant.DEBUG) {
            Log.d(TAG, str + ": " + strOptString);
        }
        if (TextUtils.isEmpty(strOptString)) {
            return;
        }
        try {
            JSONObject jSONObject2 = new JSONObject(strOptString);
            SimpleSettings.Misc.putBoolean(context, str, jSONObject2.optBoolean("fucSwitch"));
            for (String str2 : strArr) {
                String paramNameInDB = getParamNameInDB(str, str2);
                String strOptString2 = jSONObject2.optString(str2);
                if (TextUtils.isEmpty(strOptString2)) {
                    SimpleSettings.Misc.delete(context, paramNameInDB);
                } else {
                    SimpleSettings.Misc.putString(context, paramNameInDB, strOptString2);
                }
            }
        } catch (Exception e2) {
            Log.e(TAG, "parseFunction error: " + e2);
        }
    }

    public static boolean parseLocalConfig(Context context, String str, String[] strArr) {
        String str2;
        String str3;
        boolean z2;
        String str4 = "parseResult";
        try {
            try {
                try {
                    try {
                        JSONObject jSONObject = new JSONObject(str);
                        int i2 = 0;
                        while (strArr != 0) {
                            try {
                                if (i2 >= strArr.length) {
                                    break;
                                }
                                String str5 = strArr[i2];
                                if (str5 != null && !str5.isEmpty()) {
                                    if (DBG_CLOUD) {
                                        Log.d(TAG, "parseLocalConfig overlayStr=" + str5);
                                    }
                                    JSONObject jSONObject2 = new JSONObject(str5);
                                    Iterator<String> itKeys = jSONObject2.keys();
                                    while (itKeys.hasNext()) {
                                        String next = itKeys.next();
                                        Object obj = jSONObject2.get(next);
                                        if (DBG_CLOUD) {
                                            String str6 = TAG;
                                            StringBuilder sb = new StringBuilder();
                                            str3 = str4;
                                            try {
                                                try {
                                                    sb.append("parseLocalConfig overlay key=");
                                                    sb.append(next);
                                                    sb.append(" value=");
                                                    sb.append(obj);
                                                    Log.d(str6, sb.toString());
                                                } catch (Exception e2) {
                                                    e = e2;
                                                }
                                            } catch (JSONException e3) {
                                                e = e3;
                                                str2 = str3;
                                                z2 = false;
                                                Log.e(TAG, str2, e);
                                                CloudControlManager.getInstance().usersUpdateCloud();
                                                return z2;
                                            }
                                        } else {
                                            str3 = str4;
                                        }
                                        jSONObject.put(next, obj);
                                        jSONObject2 = jSONObject2;
                                        itKeys = itKeys;
                                        str4 = str3;
                                    }
                                }
                                i2++;
                                str4 = str4;
                            } catch (JSONException e4) {
                                e = e4;
                                str3 = str4;
                            }
                            e = e2;
                            z2 = false;
                            Log.e(TAG, str3, e);
                            CloudControlManager.getInstance().usersUpdateCloud();
                            return z2;
                        }
                        str3 = str4;
                        try {
                            parseFunction(context, jSONObject, SimpleSettingKeys.FUNC_THERMAL_VIDEOCHAT_CONFIG_ENABLE, "param", PARAM2);
                            z2 = false;
                            try {
                                boolean zOptBoolean = jSONObject.optBoolean(SimpleSettingKeys.KEY_THERMAL_ENHANCE_MODE_ENABLE, false);
                                Boolean boolValueOf = Boolean.valueOf(zOptBoolean);
                                SimpleSettings.Misc.putBoolean(context, SimpleSettingKeys.KEY_THERMAL_ENHANCE_MODE_ENABLE, zOptBoolean);
                                Log.i(TAG, "enhanceModeEnable is " + boolValueOf);
                                SimpleSettings.Misc.putString(context, "thermal_group", jSONObject.optString("thermal_group", ""));
                                SimpleSettings.Misc.putString(context, "thermal_camera_group_activities", jSONObject.optString("thermal_camera_group_activities", ""));
                                SimpleSettings.Misc.putString(context, SimpleSettingKeys.CLOUD_THERMAL_COMMON, jSONObject.optString(SimpleSettingKeys.CLOUD_THERMAL_COMMON, ""));
                                SimpleSettings.Misc.putString(context, "thermal_highfps_group_activities", jSONObject.optString("thermal_highfps_group_activities", ""));
                                SimpleSettings.Misc.putString(context, "thermal_collector_version", jSONObject.optString("thermal_collector_version", ""));
                                SimpleSettings.Misc.putString(context, "thermal_collector_compound_scenarios", jSONObject.optString("thermal_collector_compound_scenarios", ""));
                                CloudControlManager.getInstance().usersUpdateCloud();
                                return true;
                            } catch (JSONException e5) {
                                e = e5;
                                str2 = str3;
                                Log.e(TAG, str2, e);
                                CloudControlManager.getInstance().usersUpdateCloud();
                                return z2;
                            } catch (Exception e6) {
                                e = e6;
                            }
                        } catch (JSONException e7) {
                            e = e7;
                            z2 = false;
                            str2 = str3;
                            Log.e(TAG, str2, e);
                            CloudControlManager.getInstance().usersUpdateCloud();
                            return z2;
                        }
                    } catch (JSONException e8) {
                        e = e8;
                        str3 = "parseResult";
                    }
                } catch (Exception e9) {
                    e = e9;
                    str3 = str4;
                }
            } catch (JSONException e10) {
                e = e10;
                str2 = str4;
            }
        } catch (Throwable th) {
            CloudControlManager.getInstance().usersUpdateCloud();
            throw th;
        }
    }

    static boolean parseRemoteResult(Context context, String str) {
        return parseResult(context, str, null);
    }

    public static boolean parseResult(Context context, String str, String[] strArr) {
        boolean z2;
        JSONException jSONException;
        Exception exc;
        String str2;
        AudioDisguiseController audioDisguiseController;
        ContentValues cloudFeatureContentValues;
        String str3;
        String str4 = "log_system_config";
        String str5 = SimpleSettingKeys.KEY_TP_ACTIVE_TIME;
        String str6 = SimpleSettingKeys.KEY_TP_ACTIVE_DEVICES;
        String str7 = "power_group";
        try {
            try {
                try {
                    JSONObject jSONObject = new JSONObject(str);
                    int i2 = 0;
                    while (true) {
                        if (strArr == 0) {
                            str2 = str4;
                            break;
                        }
                        str2 = str4;
                        try {
                            if (i2 >= strArr.length) {
                                break;
                            }
                            String str8 = strArr[i2];
                            if (str8 == null || str8.isEmpty()) {
                                str3 = str7;
                                str5 = str5;
                                str6 = str6;
                            } else {
                                if (DBG_CLOUD) {
                                    Log.d(TAG, "parseResult overlayStr=" + str8);
                                }
                                JSONObject jSONObject2 = new JSONObject(str8);
                                Iterator<String> itKeys = jSONObject2.keys();
                                while (itKeys.hasNext()) {
                                    String next = itKeys.next();
                                    Object obj = jSONObject2.get(next);
                                    if (DBG_CLOUD) {
                                        Log.d(TAG, "parseResult overlay key=" + next + " value=" + obj);
                                    }
                                    jSONObject.put(next, obj);
                                    jSONObject2 = jSONObject2;
                                    itKeys = itKeys;
                                    str7 = str7;
                                }
                                str3 = str7;
                            }
                            i2++;
                            str4 = str2;
                            str5 = str5;
                            str6 = str6;
                            str7 = str3;
                        } catch (JSONException e2) {
                            jSONException = e2;
                            z2 = false;
                            Log.e(TAG, "parseResult", jSONException);
                            return z2;
                        } catch (Exception e3) {
                            exc = e3;
                            z2 = false;
                            Log.e(TAG, "parseResult", exc);
                            return z2;
                        }
                    }
                    String str9 = str5;
                    String str10 = str6;
                    JSONArray jSONArray = jSONObject.getJSONArray(PowerKeeperCloudControlFeature.FEATURE_LIST);
                    Lists.newArrayList().clear();
                    ArrayList arrayListNewArrayList = Lists.newArrayList();
                    for (int i3 = 0; i3 < jSONArray.length(); i3++) {
                        PowerKeeperCloudControlFeature fromJson = PowerKeeperCloudControlFeature.parseFromJson(jSONArray.optJSONObject(i3));
                        if (fromJson != null && (cloudFeatureContentValues = LocalUpdateUtils.getCloudFeatureContentValues(context, fromJson)) != null) {
                            arrayListNewArrayList.add(cloudFeatureContentValues);
                        }
                    }
                    addOptGlobalFeature(context, arrayListNewArrayList, jSONObject, "hide_mode");
                    addOptGlobalFeature(context, arrayListNewArrayList, jSONObject, "miui_standby");
                    addOptGlobalFeature(context, arrayListNewArrayList, jSONObject, "no_core_system_apps");
                    if (!b.a.p().s(b.b.Q)) {
                        if (Build.IS_DEBUGGABLE) {
                            Log.d(TAG, "old cloud");
                        }
                        addOptGlobalFeature(context, arrayListNewArrayList, jSONObject, LocalUpdateUtils.DOZE_WHITELIST_APPS);
                    }
                    addOptGlobalFeature(context, arrayListNewArrayList, jSONObject, LocalUpdateUtils.LEVEL_ULTIMATE_SPECIAL_APPS);
                    addOptGlobalFeature(context, arrayListNewArrayList, jSONObject, LocalUpdateUtils.BLE_SCAN_BLOCK_STATUS);
                    addOptGlobalFeature(context, arrayListNewArrayList, jSONObject, LocalUpdateUtils.BLE_SCAN_BLOCK_PARAM);
                    addOptGlobalFeature(context, arrayListNewArrayList, jSONObject, "frozen");
                    addOptGlobalFeature(context, arrayListNewArrayList, jSONObject, "cluster");
                    addOptGlobalFeature(context, arrayListNewArrayList, jSONObject, "frozenNew");
                    addOptGlobalFeature(context, arrayListNewArrayList, jSONObject, "frozenNew_whitelist");
                    addOptGlobalFeature(context, arrayListNewArrayList, jSONObject, "bright_millet");
                    addOptGlobalFeature(context, arrayListNewArrayList, jSONObject, "screenoff_millet_mode");
                    addOptGlobalFeature(context, arrayListNewArrayList, jSONObject, LocalUpdateUtils.FEATURE_HOT_FEEDBACK);
                    addOptGlobalFeature(context, arrayListNewArrayList, jSONObject, LocalUpdateUtils.FEATURE_NETWORK_FEEDBACK);
                    addOptGlobalFeature(context, arrayListNewArrayList, jSONObject, LocalUpdateUtils.FEATURE_BG_LOCATION_DELAY_HOT);
                    addOptGlobalFeature(context, arrayListNewArrayList, jSONObject, LocalUpdateUtils.FEATURE_BG_KILL_DELAY_HOT);
                    addOptGlobalFeature(context, arrayListNewArrayList, jSONObject, "alarm_align");
                    addOptGlobalFeature(context, arrayListNewArrayList, jSONObject, "launch_restrict");
                    addOptGlobalFeature(context, arrayListNewArrayList, jSONObject, "aurogon_enable");
                    addOptGlobalFeature(context, arrayListNewArrayList, jSONObject, LocalUpdateUtils.FEATURE_BKGRND_IDLE_STATUS);
                    addOptGlobalFeature(context, arrayListNewArrayList, jSONObject, "i_delay");
                    addOptGlobalFeature(context, arrayListNewArrayList, jSONObject, LocalUpdateUtils.FEATURE_STANDBY_CHAIN_DELAY);
                    addOptGlobalFeature(context, arrayListNewArrayList, jSONObject, LocalUpdateUtils.FEATURE_KILL_CONFIGS);
                    LocalUpdateUtils.setCloudFeatureRule(context, arrayListNewArrayList);
                    String strOptString = jSONObject.optString(LocalUpdateUtils.FEATURE_NETWORK_MIN_INTERVAL);
                    if (strOptString != null) {
                        Log.i(TAG, "feaStandbyChainDelayValue:" + strOptString);
                        if (!SimpleSettings.Misc.getString(context, SimpleSettingKeys.KEY_NETWORK_MIN_INTERVAL, "").equals(strOptString)) {
                            SimpleSettings.Misc.putString(context, SimpleSettingKeys.KEY_NETWORK_MIN_INTERVAL, strOptString);
                        }
                    }
                    String strOptString2 = jSONObject.optString(LocalUpdateUtils.WAKELOCK);
                    if (strOptString2 != null) {
                        Log.i(TAG, "wakelockJson:" + strOptString2);
                        if (!SimpleSettings.Misc.getString(context, SimpleSettingKeys.KEY_WAKELOCK_CLOUD_CONTENT, "").equals(strOptString2)) {
                            SimpleSettings.Misc.putString(context, SimpleSettingKeys.KEY_WAKELOCK_CLOUD_CONTENT, strOptString2);
                        }
                    }
                    boolean zOptBoolean = jSONObject.optBoolean(LocalUpdateUtils.SPEED_MODE_ENABLE);
                    String str11 = TAG;
                    Log.i(str11, "speedMode:" + zOptBoolean);
                    if (zOptBoolean) {
                        Settings.Secure.putInt(context.getContentResolver(), LocalUpdateUtils.SPEED_MODE_ENABLE, 1);
                    } else {
                        try {
                            Settings.Secure.putInt(context.getContentResolver(), LocalUpdateUtils.SPEED_MODE_ENABLE, 0);
                        } catch (JSONException e4) {
                            e = e4;
                            z2 = false;
                            jSONException = e;
                            Log.e(TAG, "parseResult", jSONException);
                            return z2;
                        } catch (Exception e5) {
                            e = e5;
                            z2 = false;
                            exc = e;
                            Log.e(TAG, "parseResult", exc);
                            return z2;
                        }
                    }
                    String strOptString3 = jSONObject.optString(SimpleSettingKeys.KEY_EVENT_NOTIFY_CONTROL, "");
                    if (!strOptString3.isEmpty()) {
                        boolean zOptBoolean2 = new JSONObject(strOptString3).optBoolean(SimpleSettingKeys.KEY_EVENT_NOTIFY_CONTROL_AUDIO, false);
                        SimpleSettings.Misc.putBoolean(context, SimpleSettingKeys.KEY_EVENT_NOTIFY_CONTROL_AUDIO_SWITCH, zOptBoolean2);
                        Log.i(str11, "KEY_EVENT_NOTIFY_CONTROL_AUDIO_SWITCH=" + zOptBoolean2);
                    }
                    String strOptString4 = jSONObject.optString(SimpleSettingKeys.KEY_SLEEP_MODE_NETWORK_WHITE_APPS, "");
                    Log.i(str11, "sleepModeData is " + strOptString4);
                    if (!strOptString4.isEmpty()) {
                        SimpleSettings.Misc.putString(context, SimpleSettingKeys.KEY_SLEEP_MODE_NETWORK_WHITE_APPS, strOptString4);
                        Log.i(str11, "sleep mode white list is " + strOptString4);
                    }
                    parseFunction(context, jSONObject, Constant.FUNC_GMS_CONTROL, "param");
                    boolean z3 = false;
                    try {
                        parseFunction(context, jSONObject, Constant.FUNC_FAKE_GPS, new String[0]);
                        parseFunction(context, jSONObject, Constant.FUNC_GET_TXPOWER, new String[0]);
                        parseFunction(context, jSONObject, "screen_off_disable_sync", "param");
                        parseFunction(context, jSONObject, Constant.FUNC_SCREEN_OFF_CLEAN_APP, "param", PARAM2);
                        parseFunction(context, jSONObject, Constant.FUNC_SCREEN_OFF_FORCE_IDLE, "param", PARAM2);
                        parseFunction(context, jSONObject, Constant.FUNC_NETWORK_TRAFFIC_CHECK, "param", PARAM2);
                        parseFunction(context, jSONObject, Constant.FUNC_SHUTDOWN_CHECK, "param", PARAM2);
                        parseFunction(context, jSONObject, Constant.FUNC_MONITOR_NIGHT_BATTERY_USAGE_CHECK, "param", PARAM2);
                        parseFunction(context, jSONObject, Constant.FUNC_AUTO_CLOSE_WIFI_AP, "param");
                        parseFunction(context, jSONObject, Constant.FUNC_DEEP_SLEEP_CHECK, "param");
                        parseFunction(context, jSONObject, Constant.FUNC_NIHGT_SUBSYSAWAKE_CHECK, "param", PARAM2);
                        parseFunction(context, jSONObject, Constant.FUNC_NON_UI_MODE, new String[0]);
                        parseFunction(context, jSONObject, Constant.FUNC_SLEEP_MODE_CLOUD, "param");
                        parseFunction(context, jSONObject, Constant.FUNC_SLEEP_MODE_CLOUD, "param", PARAM2);
                        if (!b.a.p().s(b.b.N)) {
                            if (DBG_CLOUD) {
                                Log.d(str11, "is old cloud");
                            }
                            parseFunction(context, jSONObject, Constant.FUNC_DEEP_SLEEP_MODE_CLOUD, "param");
                        }
                        parseFunction(context, jSONObject, Constant.FUNC_NIGHT_CLEAN_PROCESS, "param", PARAM2);
                        if (!b.a.p().s(b.b.T)) {
                            if (DBG_CLOUD) {
                                Log.d(str11, "old alarm control cloud");
                            }
                            parseFunction(context, jSONObject, Constant.FUNC_ALARM_CONTROL, "param");
                        }
                        parseFunction(context, jSONObject, "sleep_reboot", "param");
                        z3 = false;
                        parseFunction(context, jSONObject, Constant.FUNC_ENABLE_TXPOWER_CHANGED, new String[0]);
                        parseFunction(context, jSONObject, Constant.FUNC_THERMAL_IECTEST_CONFIG_ENABLE, "param", PARAM2);
                        parseFunction(context, jSONObject, Constant.FUNC_THERMAL_SPTM_CONFIG_ENABLE, "param");
                        parseFunction(context, jSONObject, Constant.FUNC_THERMAL_SCENARIO_CONFIG_ENABLE, "param");
                        CloudFunctionConfig.getInstance().notifyAllListeners();
                        String str12 = str7;
                        String strOptString5 = jSONObject.optString(str12, "");
                        String string = SimpleSettings.Misc.getString(context, str12, null);
                        if (strOptString5 != null && !strOptString5.isEmpty() && !strOptString5.equals(string)) {
                            SimpleSettings.Misc.putString(context, str12, strOptString5);
                        }
                        SimpleSettings.Misc.putString(context, str10, jSONObject.optString(str10));
                        SimpleSettings.Misc.putString(context, str9, jSONObject.optString(str9));
                        String str13 = str2;
                        SimpleSettings.Misc.putString(context, str13, jSONObject.optString(str13));
                        if (jSONObject.has(SimpleSettingKeys.KEY_UPLOAD_LOG)) {
                            SimpleSettings.Misc.putBoolean(context, SimpleSettingKeys.KEY_UPLOAD_LOG, jSONObject.optBoolean(SimpleSettingKeys.KEY_UPLOAD_LOG, !miui.os.Build.IS_INTERNATIONAL_BUILD));
                        }
                        String strOptString6 = jSONObject.optString(SimpleSettingKeys.KEY_AUDIO_ACTIVE_CONTROL);
                        SimpleSettings.Misc.putString(context, SimpleSettingKeys.KEY_AUDIO_ACTIVE_CONTROL, strOptString6);
                        if (strOptString6 != null && !strOptString6.isEmpty() && PowerKeeperManager.getInstance() != null && (audioDisguiseController = PowerKeeperManager.getInstance().getAudioDisguiseController()) != null) {
                            audioDisguiseController.dispatchCloudFunctionUpdated();
                        }
                        SimpleSettings.Misc.putString(context, SimpleSettingKeys.KEY_ALARM_ALIGN_LIST, jSONObject.optString(SimpleSettingKeys.KEY_ALARM_ALIGN_LIST));
                        SimpleSettings.Misc.putString(context, SimpleSettingKeys.KEY_PROCESS_CLUSTER_GROUP, jSONObject.optString(SimpleSettingKeys.KEY_PROCESS_CLUSTER_GROUP));
                        SimpleSettings.Misc.putString(context, SimpleSettingKeys.KEY_RESTRICT_SERVICE_GROUP, jSONObject.optString(SimpleSettingKeys.KEY_RESTRICT_SERVICE_GROUP));
                        try {
                            boolean zOptBoolean3 = jSONObject.optBoolean(SimpleSettingKeys.KEY_OPEN_NIGHT_CLEAN_ENABLE, false);
                            Boolean boolValueOf = Boolean.valueOf(zOptBoolean3);
                            SimpleSettings.Misc.putBoolean(context, SimpleSettingKeys.KEY_OPEN_NIGHT_CLEAN_ENABLE, zOptBoolean3);
                            Log.i(str11, "nightCleanEnable is " + boolValueOf);
                            String strOptString7 = jSONObject.optString(SimpleSettingKeys.CLOUD_THERMAL_COMMON);
                            if (!strOptString7.isEmpty()) {
                                Log.i(str11, "superModeEnable is " + Boolean.valueOf(new JSONObject(strOptString7).optBoolean(SimpleSettingKeys.KEY_ENABLE_SP_ON_VIRTUAL_SENSOR_COMPENSATION, false)));
                            }
                            z2 = false;
                            try {
                                DisplayFrameSetting.onCloudFunctionUpdated(context, jSONObject, null, 0);
                                PowerStateMachine.onCloudUpdate(context, jSONObject);
                                e.e.u(jSONObject);
                                return true;
                            } catch (JSONException e6) {
                                e = e6;
                                jSONException = e;
                                Log.e(TAG, "parseResult", jSONException);
                                return z2;
                            } catch (Exception e7) {
                                e = e7;
                                exc = e;
                                Log.e(TAG, "parseResult", exc);
                                return z2;
                            }
                        } catch (JSONException e8) {
                            e = e8;
                            z2 = false;
                        } catch (Exception e9) {
                            e = e9;
                            z2 = false;
                        }
                    } catch (JSONException e10) {
                        e = e10;
                        z2 = z3;
                    } catch (Exception e11) {
                        e = e11;
                        z2 = z3;
                    }
                } catch (JSONException e12) {
                    e = e12;
                    z2 = false;
                }
            } catch (JSONException e13) {
                z2 = false;
                jSONException = e13;
            }
        } catch (Exception e14) {
            e = e14;
            z2 = false;
        }
    }

    public static boolean parseResultForGlobalFeature(Context context, String str, String str2) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            Lists.newArrayList().clear();
            ArrayList arrayListNewArrayList = Lists.newArrayList();
            addOptGlobalFeature(context, arrayListNewArrayList, jSONObject, str2);
            LocalUpdateUtils.setCloudFeatureRule(context, arrayListNewArrayList);
            CloudFunctionConfig.getInstance().notifyAllListeners();
            return true;
        } catch (JSONException e2) {
            Log.e(TAG, "parseResultForGlobalFeature", e2);
            return false;
        } catch (Exception e3) {
            Log.e(TAG, "parseResultForGlobalFeature", e3);
            return false;
        }
    }

    public static void setTestFunc(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null) {
            return;
        }
        String string = extras.getString(Constants.TEST_UPDATE_MD5_ADDR);
        if (string != null) {
            Log.i(TAG, "Update md5Addr");
            LocalUpdateUtils.putURL(context, Constants.KEY_MD5_URL, string);
        }
        String string2 = extras.getString(Constants.TEST_UPDATE_CONTENT_ADDR);
        if (string2 != null) {
            Log.i(TAG, "Update contentAddr");
            LocalUpdateUtils.putURL(context, Constants.KEY_CONTENT_URL, string2);
        }
        String string3 = extras.getString(Constants.TEST_APP_LIST_UPDATE_MD5_ADDR);
        if (string3 != null) {
            Log.i(TAG, "Update applistcontentAddr " + string3);
            LocalUpdateUtils.putURL(context, Constants.KEY_APP_LIST_MD5_URL, string3);
        }
        String string4 = extras.getString(Constants.TEST_APP_LIST_UPDATE_CONTENT_ADDR);
        if (string4 != null) {
            Log.i(TAG, "Update applistcontentAddr " + string4);
            LocalUpdateUtils.putURL(context, Constants.KEY_APP_LIST_CONTENT_URL, string4);
        }
    }

    public static String startUpdate(Context context, boolean z2) throws Throwable {
        String str = TAG;
        Log.i(str, "startUpdateFeature--->");
        int[] iArr = {0};
        String[] strArr = {""};
        String strHttpGet = HttpUtils.httpGet(iArr, strArr, LocalUpdateUtils.getURL(context, Constants.KEY_MD5_URL, Constants.CLOUD_NEW_SERVER) + Constants.CLOUD_NEW_SERVER_UUID + LocalUpdateUtils.getUUID(context) + Constants.CLOUD_NEW_SERVER_UPID + LocalUpdateUtils.getUPID(context, Constants.CLOUD_POWER_UPDATE_UPID, z2) + Constants.CLOUD_NEW_SERVER_REGION + "power_update" + Constants.CLOUD_NEW_SERVER_BUCKET + "power_update" + Constants.CLOUD_NEW_SERVER_TYPE + Device.getInstance().getType(context), Constants.CLOUD_POWER_NEW_SID);
        if (DBG_CLOUD) {
            logLong("startUpdate result=" + strHttpGet);
        }
        if (strHttpGet == null || strHttpGet.isEmpty()) {
            Log.d(str, "startUpdate result is empty");
            return "Error: response=" + iArr[0] + " reason=" + strArr[0] + ", result empty";
        }
        try {
            JSONObject jSONObject = new JSONObject(strHttpGet);
            int i2 = jSONObject.getInt(Constants.CLOUD_NEW_SERVER_KEY_CODE);
            switch (i2) {
                case 200:
                    Log.d(str, "hit profile");
                    break;
                case 201:
                    Log.d(str, "none profile match the type");
                    break;
                case 202:
                    Log.d(str, "gray test, not hit");
                    break;
                case 203:
                    Log.d(str, "no update profile");
                    break;
                default:
                    Log.d(str, "unknown");
                    break;
            }
            if (i2 != 200) {
                Log.i(str, "no new clould configuration, returnCode = " + i2);
                return "Error: serverCode=" + i2;
            }
            if (!parseRemoteResult(context, jSONObject.getString("profile"))) {
                return null;
            }
            String string = jSONObject.getString(Constants.CLOUD_NEW_SERVER_KEY_UPID);
            String str2 = "hash=" + string;
            LocalUpdateUtils.storeUPID(context, Constants.CLOUD_POWER_UPDATE_UPID, string);
            LocalUpdateUtils.saveLastUpdateTime(context);
            return str2;
        } catch (Exception e2) {
            Log.e(TAG, "startUpdate", e2);
            return "Error: " + e2;
        }
    }

    public static String startUpdateAppList(Context context, boolean z2) throws Throwable {
        if (b.a.p().s(b.b.D)) {
            Log.i(TAG, "AppList has new cloud configured.");
            return "Warning:appList has new cloud configured";
        }
        int[] iArr = {0};
        String[] strArr = {""};
        String strHttpGet = HttpUtils.httpGet(iArr, strArr, LocalUpdateUtils.getURL(context, Constants.KEY_APP_LIST_MD5_URL, Constants.CLOUD_NEW_SERVER) + Constants.CLOUD_NEW_SERVER_UUID + LocalUpdateUtils.getUUID(context) + Constants.CLOUD_NEW_SERVER_UPID + LocalUpdateUtils.getUPID(context, Constants.CLOUD_POWER_APPLIST_UPID, z2) + Constants.CLOUD_NEW_SERVER_REGION + "power_update" + Constants.CLOUD_NEW_SERVER_BUCKET + Constants.BUCKET_NAME_POWER_APPLIST + Constants.CLOUD_NEW_SERVER_TYPE + Device.getInstance().getType(context), Constants.CLOUD_POWER_NEW_SID);
        if (DBG_CLOUD) {
            logLong("startUpdateAppList result=" + strHttpGet);
        }
        if (strHttpGet == null || strHttpGet.isEmpty()) {
            Log.d(TAG, "applist get no data");
            return "Error: response=" + iArr[0] + " reason=" + strArr[0] + ", result empty";
        }
        try {
            JSONObject jSONObject = new JSONObject(strHttpGet);
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
                return "Error: serverCode=" + i2;
            }
            if (!parseApplistResult(context, jSONObject.getString("profile"), false)) {
                return null;
            }
            String string = jSONObject.getString(Constants.CLOUD_NEW_SERVER_KEY_UPID);
            String str = "hash=" + string;
            LocalUpdateUtils.storeUPID(context, Constants.CLOUD_POWER_APPLIST_UPID, string);
            SimpleSettings.Misc.putString(context, Constants.APPLIST_UPDATE_TIME, Utils.getCurrentFormatTime());
            return str;
        } catch (Exception e2) {
            Log.d(TAG, "startUpdateAppList", e2);
            return "Error: " + e2;
        }
    }

    public static boolean parseApplistResult(Context context, JSONObject jSONObject) {
        ContentValues cloudAppContentValues;
        try {
            JSONArray jSONArrayOptJSONArray = jSONObject.optJSONArray("app_list_controller");
            ArrayList arrayList = new ArrayList();
            if (jSONArrayOptJSONArray.length() == 0) {
                return false;
            }
            for (int i2 = 0; i2 < jSONArrayOptJSONArray.length(); i2++) {
                PowerKeeperCloudControlApp fromJsonForNewCC = PowerKeeperCloudControlApp.parseFromJsonForNewCC(jSONArrayOptJSONArray.optJSONObject(i2));
                if (fromJsonForNewCC != null && (cloudAppContentValues = LocalUpdateUtils.getCloudAppContentValues(context, fromJsonForNewCC)) != null) {
                    arrayList.add(cloudAppContentValues);
                }
            }
            LocalUpdateUtils.setCloudAppRule(context, arrayList);
            arrayList.clear();
            return true;
        } catch (Exception e2) {
            Log.d(TAG, "parse cloud config error" + e2);
            return false;
        }
    }
}
