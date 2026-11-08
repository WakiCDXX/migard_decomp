package com.xiaomi.onetrack.a.a;

import android.os.Handler;
import android.os.HandlerThread;

/* JADX INFO: loaded from: classes.dex */
public class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final String f912a = "AdMonitorDbExecutor";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static String f913b = "onetrack_ad_monitor_db";

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static Handler f914c;

    private static void a() {
        if (f914c == null) {
            synchronized (a.class) {
                try {
                    if (f914c == null) {
                        HandlerThread handlerThread = new HandlerThread(f913b);
                        handlerThread.start();
                        f914c = new Handler(handlerThread.getLooper());
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    public static void a(Runnable runnable) {
        a();
        f914c.post(runnable);
    }
}
