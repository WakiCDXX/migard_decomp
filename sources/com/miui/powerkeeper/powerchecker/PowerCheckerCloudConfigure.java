package com.miui.powerkeeper.powerchecker;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class PowerCheckerCloudConfigure {
    public static final String ACTION_RULE = "action_rule";
    public static final int AID_ADB = 1011;
    public static final int AID_AUDIO = 1005;
    public static final int AID_BLUETOOTH = 1002;
    public static final int AID_CAMERA = 1006;
    public static final int AID_CLAT = 1029;
    public static final int AID_COMPASS = 1008;
    public static final int AID_DHCP = 1014;
    public static final int AID_DRM = 1019;
    public static final int AID_DRMRPC = 1026;
    public static final int AID_GPS = 1021;
    public static final int AID_GRAPHICS = 1003;
    public static final int AID_INPUT = 1004;
    public static final int AID_INSTALL = 1012;
    public static final int AID_KEYSTORE = 1017;
    public static final int AID_LOG = 1007;
    public static final int AID_LOGD = 1036;
    public static final int AID_LOOP_RADIO = 1030;
    public static final int AID_MDNSR = 1020;
    public static final int AID_MEDIA = 1013;
    public static final int AID_MEDIA_DRM = 1031;
    public static final int AID_MEDIA_RW = 1023;
    public static final int AID_MOUNT = 1009;
    public static final int AID_MTP = 1024;
    public static final int AID_NFC = 1027;
    public static final int AID_PACKAGE_INFO = 1032;
    public static final int AID_RADIO = 1001;
    public static final int AID_ROOT = 0;
    public static final int AID_SDCARD_ALL = 1035;
    public static final int AID_SDCARD_AV = 1034;
    public static final int AID_SDCARD_PICS = 1033;
    public static final int AID_SDCARD_R = 1028;
    public static final int AID_SDCARD_RW = 1015;
    public static final int AID_SHARED_RELRO = 1037;
    public static final int AID_SYSTEM = 1000;
    public static final int AID_UNUSED1 = 1022;
    public static final int AID_UNUSED2 = 1025;
    public static final int AID_USB = 1018;
    public static final int AID_VPN = 1016;
    public static final int AID_WIFI = 1010;
    public static final String APP_LIST = "app_list";
    public static final String APP_NAME = "app_name";
    public static final String AUDIO_ON_THRESHOLD = "audio_on_threshold";
    public static final String CONTROL = "control";
    public static final String CUSTOMER_POWER = "power_customer";
    public static final String CUSTOMER_POWER_CONTROL = "power_customer_control";
    public static final int DEFAULT_AUDIO_ON_THRESHOLD = 5;
    public static final int DEFAULT_DEFAULT_LIMIT_RATIO = 10;
    public static final int DEFAULT_DEFAULT_WAKEUP_ALARM_LIMIT_RATIO = 150000;
    public static final int DEFAULT_IN_BACKGROUND_PERCENT = 5;
    public static final int DEFAULT_KERNEL_WAKELOCK_INTERVAL = 5000;
    public static final int DEFAULT_KERNEL_WAKEUP_INTERVAL = 50000;
    public static final String DEFAULT_LIMIT_RATIO = "default_limit_ratio";
    public static final int DEFAULT_MAX_IGNORE_COUNT = 1;
    public static final int DEFAULT_MAX_KILL_COUNT = 5;
    public static final int DEFAULT_MAX_NOTIFY_COUNT = 3;
    public static final int DEFAULT_MIN_CHECK_TIME_INTERVAL = 600000;
    public static final int DEFAULT_NETWORK_SPEED_THRESHOLD = 10240;
    public static final int DEFAULT_PARTIAL_WAKELOCK_COUNT_BASE = 10;
    public static final int DEFAULT_PARTIAL_WAKELOCK_TIME_BASE = 4000;
    public static final int DEFAULT_POLICY_COUNT = 1;
    public static final int DEFAULT_POWER_CHECK_CYCLE_INTERVAL = 600000;
    public static final int DEFAULT_SCREEN_OFF_PERCENT = 5;
    public static final int DEFAULT_SYSTEM_ANOMALY_ACTION = 0;
    public static final int DEFAULT_TIME_LIMIT_RATIO_AUDIO_SENCE = 10;
    public static final int DEFAULT_TRIGGER_LEVEL = 5;
    public static final int DEFAULT_WAKEUP_ALARM_INTERVAL = 300000;
    public static final String DETECT_CONTROL = "detect_control";
    public static final String DETECT_PARAM = "detect_param";
    public static final String KERNEL_WAKELOCK_INTERVAL = "kernel_wakelock_interval";
    public static final String KERNEL_WAKEUP_INTERVAL = "kernel_wakeup_interval";
    public static final String MAX_IGNORE_COUNT = "max_ignore_count";
    public static final String MAX_KILL_COUNT = "max_kill_count";
    public static final String MAX_NOTIFY_COUNT = "max_notify_count";
    public static final String MIN_CHECK_INTERVAL = "min_check_interval";
    public static final String NETWORK_SPEED_THRESHOLD = "network_speed_threshold";
    public static final String PARTIAL_WAKELOCK_COUNT_BASE = "partial_wakelock_count_base";
    public static final String PARTIAL_WAKELOCK_TIME_BASE = "partial_wakelock_time_base";
    public static final String POLICY_ACTION = "action";
    public static final String POLICY_COUNT = "count";
    public static final String POLICY_ITEM = "policy";
    public static final String POLICY_LEVEL = "level";
    public static final String POLICY_LIST = "policy_list";
    public static final String POLICY_TYPE = "type";
    public static final String PROFILE_NAME = "abnormal_consume.config";
    public static final String SCREEN_OFF_PERCENT = "screen_off_percent";
    public static final String SYSTEM_ANOMALY_ACTION = "system_anomaly_action";
    private static final String TAG = "PowerChecker.ClConfig";
    public static final String TRIGGER_LEVEL = "trigger_level";
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    public static final String[] ANDROID_IDS_INFO = {"system", "radio", "bluetooth", "graphics", "input", "audio", "camera", "log", "compass", "mount", "wifi", "adb", "install", "media", "dhcp", "sdcard_rw", "vpn", "keystore", "usb", "drm", "mdnsr", "gps", null, "media_rw", "mtp", null, "drmrpc", "nfc", "sdcard_r", "clat", "loop_radio", "mediadrm", "package_info", "sdcard_pics", "sdcard_av", "sdcard_all", "logd", "shared_relro", "dbus", "tlsdate", "mediaextractor", "audioserver", "metrics_collector", "metricsd", "webservd", "debuggerd", "mediacodec", "cameraserver", "firewalld", "trunksd", "nvram", com.xiaomi.onetrack.api.d.P, "dns_tether", "webview_zygote", "vehicle_network", "media_audio", "media_vidio", "media_image", "tombstoned", "media_obb", "ese", "ota_update"};

    /* JADX WARN: Not initialized variable reg: 20, insn: 0x008d: MOVE (r1 I:??[OBJECT, ARRAY]) = (r20 I:??[OBJECT, ARRAY]) (LINE:142), block:B:26:0x008d */
    public static Bundle getAbnormalConsumeConfig(String str) {
        String str2;
        String str3 = TAG;
        if (str == null) {
            if (DEBUG) {
                Log.d(TAG, "getAbnormalConsumeConfig, cfg file is null");
            }
            return null;
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            String strOptString = jSONObject.optString(DETECT_CONTROL);
            try {
                if (TextUtils.isEmpty(strOptString)) {
                    if (DEBUG) {
                        Log.d(TAG, "getAbnormalConsumeConfig, no cloud configuration");
                    }
                    return null;
                }
                try {
                    if (!new JSONObject(strOptString).optBoolean(CONTROL)) {
                        if (DEBUG) {
                            Log.d(TAG, "getAbnormalConsumeConfig, control is disabled");
                        }
                        return null;
                    }
                    String strOptString2 = jSONObject.optString(DETECT_PARAM);
                    if (!TextUtils.isEmpty(strOptString2)) {
                        JSONObject jSONObject2 = new JSONObject(strOptString2);
                        Bundle bundle = new Bundle();
                        JSONArray jSONArrayOptJSONArray = jSONObject2.optJSONArray(ACTION_RULE);
                        if (jSONArrayOptJSONArray != null) {
                            int length = jSONArrayOptJSONArray.length();
                            int[] iArr = new int[length];
                            for (int i2 = 0; i2 < length; i2++) {
                                iArr[i2] = jSONArrayOptJSONArray.optInt(i2);
                            }
                            bundle.putIntArray(ACTION_RULE, iArr);
                        }
                        JSONArray jSONArrayOptJSONArray2 = jSONObject2.optJSONArray(TRIGGER_LEVEL);
                        if (jSONArrayOptJSONArray2 != null) {
                            int length2 = jSONArrayOptJSONArray2.length();
                            int[] iArr2 = new int[length2];
                            for (int i3 = 0; i3 < length2; i3++) {
                                iArr2[i3] = jSONArrayOptJSONArray2.optInt(i3);
                            }
                            bundle.putIntArray(TRIGGER_LEVEL, iArr2);
                        }
                        JSONArray jSONArrayOptJSONArray3 = jSONObject2.optJSONArray(DEFAULT_LIMIT_RATIO);
                        if (jSONArrayOptJSONArray3 != null) {
                            int length3 = jSONArrayOptJSONArray3.length();
                            int[] iArr3 = new int[length3];
                            for (int i4 = 0; i4 < length3; i4++) {
                                iArr3[i4] = jSONArrayOptJSONArray3.optInt(i4);
                            }
                            bundle.putIntArray(DEFAULT_LIMIT_RATIO, iArr3);
                        }
                        int iOptInt = jSONObject2.optInt(MIN_CHECK_INTERVAL, 600000);
                        int iOptInt2 = jSONObject2.optInt(MAX_KILL_COUNT, 5);
                        int iOptInt3 = jSONObject2.optInt(MAX_NOTIFY_COUNT, 3);
                        int iOptInt4 = jSONObject2.optInt(MAX_IGNORE_COUNT, 1);
                        int iOptInt5 = jSONObject2.optInt(NETWORK_SPEED_THRESHOLD, DEFAULT_NETWORK_SPEED_THRESHOLD);
                        int iOptInt6 = jSONObject2.optInt(AUDIO_ON_THRESHOLD, 5);
                        int iOptInt7 = jSONObject2.optInt(SCREEN_OFF_PERCENT, 5);
                        int iOptInt8 = jSONObject2.optInt(KERNEL_WAKELOCK_INTERVAL, 5000);
                        int iOptInt9 = jSONObject2.optInt(KERNEL_WAKEUP_INTERVAL, DEFAULT_KERNEL_WAKEUP_INTERVAL);
                        int iOptInt10 = jSONObject2.optInt(PARTIAL_WAKELOCK_TIME_BASE, 4000);
                        int iOptInt11 = jSONObject2.optInt(PARTIAL_WAKELOCK_COUNT_BASE, 10);
                        int iOptInt12 = jSONObject2.optInt(SYSTEM_ANOMALY_ACTION, 0);
                        bundle.putInt(MIN_CHECK_INTERVAL, iOptInt);
                        bundle.putInt(MAX_KILL_COUNT, iOptInt2);
                        bundle.putInt(MAX_NOTIFY_COUNT, iOptInt3);
                        bundle.putInt(MAX_IGNORE_COUNT, iOptInt4);
                        bundle.putInt(NETWORK_SPEED_THRESHOLD, iOptInt5);
                        bundle.putInt(AUDIO_ON_THRESHOLD, iOptInt6);
                        bundle.putInt(SCREEN_OFF_PERCENT, iOptInt7);
                        bundle.putInt(KERNEL_WAKELOCK_INTERVAL, iOptInt8);
                        bundle.putInt(KERNEL_WAKEUP_INTERVAL, iOptInt9);
                        bundle.putInt(PARTIAL_WAKELOCK_TIME_BASE, iOptInt10);
                        bundle.putInt(PARTIAL_WAKELOCK_COUNT_BASE, iOptInt11);
                        bundle.putInt(SYSTEM_ANOMALY_ACTION, iOptInt12);
                        return bundle;
                    }
                } catch (JSONException e2) {
                    e = e2;
                    Log.e(str3, "getAbnormalConsumeConfig, bad config file, e:" + e);
                }
                return null;
            } catch (JSONException e3) {
                e = e3;
                str3 = str2;
            }
        } catch (JSONException e4) {
            e = e4;
        }
    }

    public static String getAppPoliciesConfig(String str, boolean z2) {
        if (str == null) {
            if (DEBUG) {
                Log.d(TAG, "getAppPoliciesConfig, cfg file is null");
            }
            return null;
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            if (!z2) {
                if (DEBUG) {
                    Log.d(TAG, "getAppPoliciesConfig, control is disabled");
                }
                return null;
            }
            String strOptString = jSONObject.optString("app_list");
            if (TextUtils.isEmpty(strOptString)) {
                return null;
            }
            return strOptString;
        } catch (JSONException e2) {
            Log.e(TAG, "getAppPoliciesConfig, bad config file" + e2);
            return null;
        }
    }

    public static boolean getDetectControl(String str) {
        if (str == null) {
            if (DEBUG) {
                Log.d(TAG, "getDetectControl, detectControl file is null");
            }
            return false;
        }
        try {
            String strOptString = new JSONObject(str).optString(DETECT_CONTROL);
            if (!TextUtils.isEmpty(strOptString)) {
                return new JSONObject(strOptString).optBoolean(CONTROL);
            }
            if (DEBUG) {
                Log.d(TAG, "getDetectControl, no cloud configuration");
            }
            return false;
        } catch (JSONException e2) {
            Log.e(TAG, "getDetectControl dump exception " + e2);
            return false;
        }
    }

    public static String getPackageNameByUid(Context context, int i2) {
        if (i2 == 0) {
            return "root";
        }
        if (i2 >= 10000) {
            String[] packagesForUid = context.getPackageManager().getPackagesForUid(i2);
            if (packagesForUid == null || packagesForUid.length <= 0) {
                return null;
            }
            return packagesForUid[0];
        }
        int i3 = i2 - 1000;
        if (i3 < 0) {
            return null;
        }
        String[] strArr = ANDROID_IDS_INFO;
        if (i3 < strArr.length) {
            return strArr[i3];
        }
        return null;
    }

    public static boolean getPowerCustomer(String str) {
        if (str == null) {
            if (DEBUG) {
                Log.d(TAG, "customer power dump is no cloud configuration");
            }
            return false;
        }
        try {
            String strOptString = new JSONObject(str).optString(CUSTOMER_POWER);
            if (TextUtils.isEmpty(strOptString)) {
                if (DEBUG) {
                    Log.d(TAG, "customer power dump is no cloud switch");
                }
                return false;
            }
            if (new JSONObject(strOptString).optBoolean(CUSTOMER_POWER_CONTROL)) {
                if (!DEBUG) {
                    return true;
                }
                Log.d(TAG, "customer power dump is enable");
                return true;
            }
            return false;
        } catch (JSONException e2) {
            Log.e(TAG, "get customer power dump exception " + e2);
        }
    }
}
