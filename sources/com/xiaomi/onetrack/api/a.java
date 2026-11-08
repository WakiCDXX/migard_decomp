package com.xiaomi.onetrack.api;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.xiaomi.mipush.sdk.MiPushClient;
import java.util.concurrent.CopyOnWriteArrayList;

/* JADX INFO: loaded from: classes.dex */
public class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final String f942a = "BroadcastManager";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static String f943b = "onetrack_broadcast_manager";

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static volatile a f944c = null;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static final int f945e = 100;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static final int f946f = 101;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private static volatile boolean f947g = false;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private Handler f948d;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private CopyOnWriteArrayList<g> f949h = new CopyOnWriteArrayList<>();

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private BroadcastReceiver f950i = new b(this);

    /* JADX INFO: renamed from: com.xiaomi.onetrack.api.a$a, reason: collision with other inner class name */
    private class HandlerC0010a extends Handler {
        /* synthetic */ HandlerC0010a(a aVar, Looper looper, b bVar) {
            this(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message == null) {
                return;
            }
            int i2 = message.what;
            if (i2 == 100 || i2 == 101) {
                try {
                    a.this.a(i2);
                } catch (Exception e2) {
                    com.xiaomi.onetrack.util.p.a(a.f942a, "screenReceiver exception: ", e2);
                }
            }
        }

        private HandlerC0010a(Looper looper) {
            super(looper);
        }
    }

    private a() {
        try {
            HandlerThread handlerThread = new HandlerThread(f943b);
            handlerThread.start();
            this.f948d = new HandlerC0010a(this, handlerThread.getLooper(), null);
        } catch (Throwable unused) {
        }
    }

    public static void b() {
        if (f944c == null) {
            synchronized (a.class) {
                try {
                    if (f944c == null) {
                        f944c = new a();
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    private void d() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        com.xiaomi.onetrack.f.a.b().registerReceiver(this.f950i, intentFilter);
        Log.d("ot_receiver", MiPushClient.COMMAND_REGISTER);
    }

    public void c() {
        if (f947g) {
            return;
        }
        f947g = true;
        try {
            d();
        } catch (Throwable unused) {
            f947g = false;
        }
    }

    public static a a() {
        if (f944c == null) {
            b();
        }
        return f944c;
    }

    public void a(g gVar) {
        if (this.f949h.contains(gVar)) {
            return;
        }
        this.f949h.add(gVar);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(int i2) {
        for (g gVar : this.f949h) {
            if (i2 == 100) {
                gVar.a(true);
            } else if (i2 == 101) {
                gVar.a(false);
            }
        }
    }
}
