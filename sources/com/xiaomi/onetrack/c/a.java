package com.xiaomi.onetrack.c;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final String f1236a = "BroadcastExecutor";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static String f1237b = "onetrack_broadcast";

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static Handler f1238c;

    public static void a() {
        if (f1238c == null) {
            synchronized (a.class) {
                try {
                    if (f1238c == null) {
                        Log.d(f1236a, "initIfNeeded : " + Thread.currentThread().getId());
                        HandlerThread handlerThread = new HandlerThread(f1237b);
                        handlerThread.start();
                        f1238c = new Handler(handlerThread.getLooper());
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    public static void a(Runnable runnable) {
        Log.d(f1236a, "BroadcastExecutor : " + Thread.currentThread().getId());
        a();
        f1238c.post(runnable);
    }
}
