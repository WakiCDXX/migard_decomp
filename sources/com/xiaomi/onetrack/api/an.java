package com.xiaomi.onetrack.api;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import com.miui.analytics.ITrack;
import com.xiaomi.onetrack.Configuration;
import com.xiaomi.push.extension.PluginConstants;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/* JADX INFO: loaded from: classes.dex */
public class an {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final String f996a = "com.miui.analytics";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final String f997b = "com.miui.analytics.onetrack.OneTrackService";

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static final String f998c = "ServiceConnectManager";

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private static final int f999h = 1;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private static final int f1000i = 2;

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private static volatile an f1001k = null;

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    private static String f1002l = "onetrack_service_connect";

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private volatile ITrack f1003d;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private b f1007j;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private AtomicBoolean f1004e = new AtomicBoolean(false);

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private AtomicBoolean f1005f = new AtomicBoolean(false);

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    private ServiceConnection f1008m = new ServiceConnection() { // from class: com.xiaomi.onetrack.api.ServiceConnectionManager$1
        @Override // android.content.ServiceConnection
        public void onBindingDied(ComponentName componentName) {
            try {
                try {
                    this.f941a.f1006g.unbindService(this.f941a.f1008m);
                } catch (Exception e2) {
                    com.xiaomi.onetrack.util.p.a("ServiceConnectManager", "onBindingDied: " + e2.toString());
                }
                this.f941a.c();
            } catch (Throwable th) {
                com.xiaomi.onetrack.util.p.a("ServiceConnectManager", "onBindingDied throwable:" + th.getMessage());
            }
        }

        @Override // android.content.ServiceConnection
        public void onNullBinding(ComponentName componentName) {
            try {
                this.f941a.c();
            } catch (Throwable th) {
                com.xiaomi.onetrack.util.p.a("ServiceConnectManager", "onNullBinding throwable:" + th.getMessage());
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            try {
                this.f941a.f1003d = ITrack.Stub.asInterface(iBinder);
                this.f941a.f1005f.set(true);
                this.f941a.f1004e.set(false);
                this.f941a.g();
                StringBuilder sb = new StringBuilder();
                sb.append("onServiceConnected  mConnecting ");
                sb.append(this.f941a.f1004e);
                sb.append(" mBindResult:");
                sb.append(this.f941a.f1005f);
                sb.append(" mIOneTrackService ");
                sb.append(this.f941a.f1003d == null ? 0 : 1);
                sb.append(" pid:");
                sb.append(Process.myPid());
                sb.append(" tid:");
                sb.append(Process.myTid());
                com.xiaomi.onetrack.util.p.a("ServiceConnectManager", sb.toString());
            } catch (Throwable th) {
                this.f941a.c();
                com.xiaomi.onetrack.util.p.a("ServiceConnectManager", "onServiceConnected throwable:" + th.getMessage());
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            try {
                this.f941a.c();
                StringBuilder sb = new StringBuilder();
                sb.append("onServiceDisconnected:  mConnecting ");
                sb.append(this.f941a.f1004e);
                sb.append(" mIOneTrackService ");
                sb.append(this.f941a.f1003d == null ? 0 : 1);
                com.xiaomi.onetrack.util.p.a("ServiceConnectManager", sb.toString());
            } catch (Throwable th) {
                com.xiaomi.onetrack.util.p.a("ServiceConnectManager", "onServiceDisconnected throwable:" + th.getMessage());
            }
        }
    };

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    private CopyOnWriteArrayList<a> f1009n = new CopyOnWriteArrayList<>();

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private Context f1006g = com.xiaomi.onetrack.f.a.b();

    public interface a {
        void a();
    }

    private class b extends Handler {
        public b(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            super.handleMessage(message);
            int i2 = message.what;
            if (i2 == 1) {
                an.this.f();
            } else if (i2 == 2) {
                an.this.d();
            }
        }
    }

    private an() {
        HandlerThread handlerThread = new HandlerThread(f1002l);
        handlerThread.start();
        b bVar = new b(handlerThread.getLooper());
        this.f1007j = bVar;
        bVar.sendEmptyMessage(2);
    }

    public static void b() {
        if (f1001k == null) {
            synchronized (an.class) {
                try {
                    if (f1001k == null) {
                        f1001k = new an();
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void c() {
        this.f1003d = null;
        this.f1005f.set(false);
        this.f1004e.set(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void d() {
        if (!this.f1004e.get() && !this.f1005f.get() && this.f1003d == null) {
            e();
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("ensureService mConnecting: ");
        sb.append(this.f1004e.get());
        sb.append(" mIsBindSuccess:");
        sb.append(this.f1005f.get());
        sb.append(" mAnalytics: ");
        sb.append(this.f1003d == null ? 0 : 1);
        com.xiaomi.onetrack.util.p.a(f998c, sb.toString());
    }

    private void e() {
        this.f1004e.set(true);
        try {
            Intent intent = new Intent();
            intent.setClassName("com.miui.analytics", f997b);
            boolean zBindService = this.f1006g.bindService(intent, this.f1008m, 1);
            if (zBindService) {
                this.f1005f.set(true);
            } else {
                this.f1005f.set(false);
                try {
                    this.f1006g.unbindService(this.f1008m);
                } catch (Throwable th) {
                    Log.d(f998c, "unbindService e1: " + th.getMessage());
                }
            }
            com.xiaomi.onetrack.util.p.a(f998c, "bindService:  mConnecting: " + this.f1004e + " bindResult:" + zBindService);
        } catch (Throwable th2) {
            try {
                this.f1005f.set(false);
                this.f1004e.set(false);
                this.f1006g.unbindService(this.f1008m);
            } catch (Throwable th3) {
                Log.d(f998c, "bindService e1: " + th3.getMessage());
            }
            com.xiaomi.onetrack.util.p.b(f998c, "bindService e: " + th2.getMessage());
        }
        this.f1004e.set(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void f() {
        try {
            if (this.f1005f.get()) {
                this.f1006g.unbindService(this.f1008m);
            }
        } catch (Throwable th) {
            com.xiaomi.onetrack.util.p.a(f998c, "unBindService Throwable: " + th.getMessage());
        }
        c();
        com.xiaomi.onetrack.util.p.a(f998c, "unBindService  mIsBindSuccess:" + this.f1005f.get());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void g() {
        Iterator<a> it = this.f1009n.iterator();
        while (it.hasNext()) {
            it.next().a();
        }
    }

    public static an a() {
        if (f1001k == null) {
            b();
        }
        return f1001k;
    }

    public boolean a(String str, String str2, Configuration configuration, boolean z2) {
        if (this.f1004e.get()) {
            return false;
        }
        if (this.f1003d != null) {
            try {
                String appId = configuration.getAppId();
                if (z2) {
                    appId = configuration.getAdEventAppId();
                }
                this.f1003d.trackEvent(appId, com.xiaomi.onetrack.f.a.e(), str, str2);
                return true;
            } catch (Throwable th) {
                f();
                com.xiaomi.onetrack.util.p.a(f998c, "track throwable: " + th.getMessage());
                return false;
            }
        }
        this.f1007j.sendEmptyMessage(2);
        return false;
    }

    public void a(String str, String str2, String str3) {
        try {
            com.xiaomi.onetrack.util.p.b(f998c, "trackCacheData: appId:" + str + "_eventName:_data:" + str3);
            this.f1003d.trackEvent(str, com.xiaomi.onetrack.f.a.e(), str2, str3);
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b(f998c, "trackCacheData error:" + e2.toString());
        }
    }

    public void a(a aVar) {
        if (this.f1009n.contains(aVar)) {
            return;
        }
        this.f1009n.add(aVar);
    }

    public void a(int i2) {
        if (i2 == 2) {
            this.f1007j.sendEmptyMessageDelayed(1, PluginConstants.TTS_REMOVE_BEAN_TIME);
        } else if (this.f1007j.hasMessages(1)) {
            this.f1007j.removeMessages(1);
        }
    }
}
