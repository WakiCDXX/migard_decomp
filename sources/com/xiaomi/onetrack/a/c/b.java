package com.xiaomi.onetrack.a.c;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import com.xiaomi.onetrack.b.n;
import com.xiaomi.onetrack.util.p;

/* JADX INFO: loaded from: classes.dex */
public class b {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final String f934a = "AdMonitorUploadTimer";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static final int f935b = 10;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static volatile b f936c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private a f937d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private BroadcastReceiver f938e = new c(this);

    private b() {
        HandlerThread handlerThread = new HandlerThread("onetrack_ad_monitor_uploader");
        handlerThread.start();
        this.f937d = new a(handlerThread.getLooper());
        a(com.xiaomi.onetrack.f.a.a());
    }

    public void b() {
        this.f937d.a(0);
    }

    public static b a() {
        if (f936c == null) {
            synchronized (b.class) {
                try {
                    if (f936c == null) {
                        f936c = new b();
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return f936c;
    }

    /* JADX INFO: Access modifiers changed from: private */
    static final class a extends Handler {
        public a(Looper looper) {
            super(looper);
        }

        public void a(int i2) {
            if (hasMessages(i2)) {
                p.a(b.f934a, "has message\u3000prio=" + i2);
                return;
            }
            long jA = n.a(i2);
            p.a(b.f934a, "will check prio=" + i2 + ", delay=" + jA);
            a(i2, jA);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            super.handleMessage(message);
            p.a(b.f934a, "AdMonitorUploadTimer.handleMessage, msg.what=" + message.what);
            if (message.what != 10) {
                d.a();
                return;
            }
            boolean zA = com.xiaomi.onetrack.g.c.a();
            p.a(b.f934a, "AdMonitorUploadTimer netReceiver, 网络是否可用=" + zA);
            if (zA) {
                a(0, 1000L);
            }
        }

        public void a() {
            removeCallbacksAndMessages(null);
        }

        private void a(int i2, long j2) {
            removeMessages(i2);
            p.a(b.f934a, "will post msg, prio=" + i2 + ", delay=" + j2);
            sendEmptyMessageDelayed(i2, j2);
        }
    }

    private void a(Context context) {
        if (context == null) {
            return;
        }
        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            context.registerReceiver(this.f938e, intentFilter);
        } catch (Exception e2) {
            p.a(f934a, "registerNetReceiver: " + e2);
        }
    }
}
