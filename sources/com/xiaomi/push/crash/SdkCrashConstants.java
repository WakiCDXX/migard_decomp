package com.xiaomi.push.crash;

import com.xiaomi.clientreport.BuildConfig;

/* JADX INFO: loaded from: classes.dex */
class SdkCrashConstants {
    public static final String[] CRASH_FILTER = {"com.xiaomi.channel.commonutils", BuildConfig.APPLICATION_ID, "com.xiaomi.mipush.sdk", "com.xiaomi.push"};
    public static final String DEAD_EXCEPTION = "android.os.DeadSystemException";
    public static final String LF = "#n";
    public static final int MAX_CACHE_COUNT = 20;
    public static String PA_ANDROID_VERSION = "an_ver";
    public static String PA_APP_VERSION_NAME = "ap_vn";
    public static String PA_COUNT = "cnt";
    public static String PA_CREATE_TIME = "time";
    public static String PA_EXCEPTION_TYPE = "etype";
    public static String PA_MODIFIED_TIME = "mod_time";
    public static String PA_PRIORITY = "pri";
    public static String PA_ROM_NAME = "ro_name";
    public static String PA_SDK_VERSION = "sdk_ver";
    public static String PA_SECRET_PARAMS = "se_pa";
    public static String PA_STACK = "stack";
    public static String PA_TYPE = "type";
    public static final int REPORT_TRIGGER_INTERVAL = 180000;
    public static final String REPORT_URL_HOST_CHINA = "https://api.xmpush.xiaomi.com";
    public static final String REPORT_URL_PATH = "/v1/trace/report/sdk";
    public static final int TYPE_ANR = 1;
    public static final int TYPE_CRASH = 0;

    SdkCrashConstants() {
    }
}
