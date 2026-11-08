package com.xiaomi.push.extension;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import com.xiaomi.channel.commonutils.android.SafeHandler;

/* JADX INFO: loaded from: classes.dex */
public class QueuedWork {
    private Handler mDefaultHandler;

    private static class InstanceHolder {
        static QueuedWork sInstance = new QueuedWork();

        private InstanceHolder() {
        }
    }

    private static Handler getDefaultHandler() {
        return InstanceHolder.sInstance.mDefaultHandler;
    }

    public static Handler newHandler(Handler.Callback callback) {
        return new SafeHandler(getDefaultHandler().getLooper(), callback);
    }

    public static void post(Runnable runnable) {
        if (Looper.myLooper() == getDefaultHandler().getLooper()) {
            runnable.run();
        } else {
            getDefaultHandler().post(runnable);
        }
    }

    public static void postDelayed(Runnable runnable, long j2) {
        getDefaultHandler().postDelayed(runnable, j2);
    }

    private QueuedWork() {
        HandlerThread handlerThread = new HandlerThread("QueuedWork");
        handlerThread.start();
        this.mDefaultHandler = new SafeHandler(handlerThread.getLooper());
    }
}
