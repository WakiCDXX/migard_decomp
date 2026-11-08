package com.xiaomi.push.voip;

import android.util.Log;
import com.xiaomi.channel.commonutils.logger.MyLog;

/* JADX INFO: loaded from: classes.dex */
class VoipLog {
    private static final String TAG = "VoipTag";
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);

    VoipLog() {
    }

    public static void d(String str) {
        if (DEBUG) {
            MyLog.i(wrapperMsg(str));
        }
    }

    public static void e(String str, Throwable th) {
        MyLog.e(wrapperMsg(str), th);
    }

    public static void w(String str) {
        MyLog.w(wrapperMsg(str));
    }

    private static String wrapperMsg(String str) {
        return MyLog.wrapTagMsg(TAG, str);
    }
}
