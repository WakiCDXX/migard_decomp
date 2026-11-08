package com.xiaomi.push.pulldown;

import android.util.Log;
import com.xiaomi.channel.commonutils.logger.MyLog;

/* JADX INFO: loaded from: classes.dex */
class PullDownLog {
    private static final String TAG = "PullDownLog";
    public static final Boolean DEBUG = Boolean.valueOf(Log.isLoggable(TAG, 3));

    private PullDownLog() {
    }

    public static void d(String str) {
        if (DEBUG.booleanValue()) {
            i(str);
        }
    }

    public static void e(String str) {
        MyLog.e(wrapMessage(str));
    }

    public static void i(String str) {
        MyLog.i(wrapMessage(str));
    }

    public static void w(String str) {
        MyLog.w(wrapMessage(str));
    }

    private static String wrapMessage(String str) {
        return MyLog.wrapTagMsg(TAG, str);
    }
}
