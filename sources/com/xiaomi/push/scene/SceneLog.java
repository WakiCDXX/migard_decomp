package com.xiaomi.push.scene;

import android.util.Log;
import com.xiaomi.channel.commonutils.logger.MyLog;

/* JADX INFO: loaded from: classes.dex */
class SceneLog {
    private static final String TAG = "ScenePush";
    public static final Boolean DEBUG = Boolean.valueOf(Log.isLoggable(TAG, 3));

    private SceneLog() {
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
