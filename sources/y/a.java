package y;

import android.os.Handler;
import android.os.HandlerThread;

/* JADX INFO: loaded from: classes.dex */
public class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static String f2401a = "ot_pubsub_db";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static Handler f2402b;

    public static void a() {
        if (f2402b == null) {
            synchronized (a.class) {
                try {
                    if (f2402b == null) {
                        HandlerThread handlerThread = new HandlerThread(f2401a);
                        handlerThread.start();
                        f2402b = new Handler(handlerThread.getLooper());
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    public static void b(Runnable runnable) {
        a();
        f2402b.post(runnable);
    }
}
