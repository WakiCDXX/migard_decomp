package com.xiaomi.push.extension;

import android.util.Log;
import com.xiaomi.channel.commonutils.logger.MyLog;

/* JADX INFO: loaded from: classes.dex */
public final class LogPools {
    public static final LogModule CrashLog;
    public static final LogModule HeadsUpLog;
    public static final LogModule PullDownLog;
    public static final LogModule SceneLog;
    public static final LogModule TtsLog;
    public static final LogModule VoipLog;

    public static final class LogModule {
        public final boolean DEBUG;
        private final String TAG;

        private String wrapperMsg(String str) {
            return MyLog.wrapTagMsg(this.TAG, str);
        }

        public void d(String str) {
            if (this.DEBUG) {
                i(str);
            }
        }

        public void e(String str) {
            MyLog.e(wrapperMsg(str));
        }

        public void i(String str) {
            MyLog.i(wrapperMsg(str));
        }

        public void w(String str) {
            MyLog.w(wrapperMsg(str));
        }

        private LogModule(String str) {
            this.TAG = str;
            this.DEBUG = Log.isLoggable(str, 3);
        }

        public void e(String str, Throwable th) {
            MyLog.e(wrapperMsg(str), th);
        }
    }

    static {
        HeadsUpLog = new LogModule("HeadsUpTag");
        SceneLog = new LogModule("ScenePush");
        VoipLog = new LogModule("VoipTag");
        PullDownLog = new LogModule("PullDownLog");
        CrashLog = new LogModule("CrTag");
        TtsLog = new LogModule("TtsLog");
    }
}
