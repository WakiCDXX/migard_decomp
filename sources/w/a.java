package w;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static String f2392a = "pub_broadcast";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static Handler f2393b;

    public static void a() {
        if (f2393b == null) {
            synchronized (a.class) {
                try {
                    if (f2393b == null) {
                        Log.d("BroadcastExecutor", "initIfNeeded : " + Thread.currentThread().getId());
                        HandlerThread handlerThread = new HandlerThread(f2392a);
                        handlerThread.start();
                        f2393b = new Handler(handlerThread.getLooper());
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    public static void b(Runnable runnable) {
        Log.d("BroadcastExecutor", "BroadcastExecutor : " + Thread.currentThread().getId());
        a();
        f2393b.post(runnable);
    }
}
