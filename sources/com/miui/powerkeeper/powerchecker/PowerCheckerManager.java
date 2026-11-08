package com.miui.powerkeeper.powerchecker;

/* JADX INFO: loaded from: classes.dex */
public class PowerCheckerManager {
    public static final int MAX_POWER_CHECKER_TYPE = 8;
    public static final String NOTIFICATION_PARAM_PACKAGE_ARRAY = "package_name_array";
    public static final String NOTIFICATION_PARAM_RESULT_ARRAY = "result_code_array";
    public static final String NOTIFICATION_PARAM_TYPE_ARRAY = "type_array";
    public static final String NOTIFICATION_PARAM_UID_ARRAY = "uid_array";
    public static final int NOTIFICATION_RESULT_TYPE_IGNORED = 0;
    public static final int NOTIFICATION_RESULT_TYPE_KILLED = 1;
    public static final int NOTIFICATION_RESULT_TYPE_PENDING = -1;
    public static final int NOTIFICATION_RESULT_TYPE_UNINSTALLED = 2;
    public static final String[] POWER_CHECKER_TYPE_NAMES = {"kernel_wakelock", "partial_wakelock", "wifi_scan", "sensor_gps", "cpu_time", PowerCheckerProcessNotifier.KEY_WAKEUP_ALARM, "mobile_radio", "sub_system"};
    public static final int TYPE_BACKGROUND_CPU = 4;
    public static final int TYPE_BACKGROUND_GPS = 3;
    public static final int TYPE_MOBILE_RADIO = 6;
    public static final int TYPE_PARTIAL_WAKELOCK = 1;
    public static final int TYPE_PLATFORM_WAKELOCK = 0;
    public static final int TYPE_SUB_SYSTEM = 7;
    public static final int TYPE_WAKEUP_ALARM = 5;
    public static final int TYPE_WIFI_SCAN = 2;

    public static boolean isValidType(int i2) {
        return i2 >= 0 && i2 < 8;
    }
}
