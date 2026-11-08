package com.xiaomi.onetrack.c;

import android.os.Handler;
import android.os.HandlerThread;

/* JADX INFO: loaded from: classes.dex */
public class b {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final String f1239a = "DbExecutor";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static String f1240b = "onetrack_db";

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static Handler f1241c;

    private static void a() {
        if (f1241c == null) {
            synchronized (b.class) {
                try {
                    if (f1241c == null) {
                        HandlerThread handlerThread = new HandlerThread(f1240b);
                        handlerThread.start();
                        f1241c = new Handler(handlerThread.getLooper());
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    public static void a(Runnable runnable) {
        a();
        f1241c.post(runnable);
    }
}
