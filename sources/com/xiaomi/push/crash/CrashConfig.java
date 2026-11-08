package com.xiaomi.push.crash;

/* JADX INFO: loaded from: classes.dex */
class CrashConfig {
    public static final int DB_MAX_BATCH_COUNT = 100;
    public static final int DB_MAX_TOTAL_COUNT = 500;
    public static final String DEAD_EXCEPTION = "android.os.DeadSystemException";
    public static final String LF = "#n";
    public static final int REPORT_LIMIT = 12;
    public static final int REPORT_TRIGGER_INTERVAL = 60000;
    public static final String REPORT_URL_HOST_CHINA = "https://api.xmpush.xiaomi.com";
    public static final String REPORT_URL_PATH = "/v1/trace/report";

    CrashConfig() {
    }
}
