package com.xiaomi.channel.commonutils.misc;

import android.os.Looper;
import com.xiaomi.channel.commonutils.logger.MyLog;

/* JADX INFO: loaded from: classes.dex */
public class ThreadUtils {
    private ThreadUtils() {
    }

    public static void checkNotUIThread() {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            throw new RuntimeException("can't do this on ui thread");
        }
    }

    public static void checkUIThread() {
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            throw new RuntimeException("must do this on ui thread");
        }
    }

    public static void checkNotUIThread(boolean z2) {
        if (Looper.getMainLooper().getThread() == Thread.currentThread() && z2) {
            throw new RuntimeException("can't do this on ui thread when debug_switch:" + z2);
        }
        if (Looper.getMainLooper().getThread() != Thread.currentThread() || z2) {
            return;
        }
        MyLog.w("can't do this on ui thread when debug_switch:" + z2);
    }
}
