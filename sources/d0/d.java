package d0;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import com.ot.pubsub.util.k;
import com.ot.pubsub.util.l;
import com.ot.pubsub.util.m;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class d {

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static volatile d f1761f = null;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private static int f1762g = 900000;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private d0.b f1763a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private a f1764b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private volatile boolean f1765c = true;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private BroadcastReceiver f1766d = new e(this);

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private BroadcastReceiver f1767e = new g(this);

    /* JADX INFO: Access modifiers changed from: private */
    class a extends Handler {
        /* synthetic */ a(d dVar, Looper looper, e eVar) {
            this(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 123) {
                com.ot.pubsub.util.e.a(new j(this));
            }
        }

        private a(Looper looper) {
            super(looper);
        }
    }

    static class b {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        public boolean f1769a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        public boolean f1770b;

        b(boolean z2, boolean z3) {
            this.f1769a = z2;
            this.f1770b = z3;
        }
    }

    private d() {
        r();
        u();
        o();
        this.f1764b = new a(this, Looper.getMainLooper(), null);
    }

    public static d a() {
        if (f1761f == null) {
            g();
        }
        return f1761f;
    }

    public static void g() {
        if (f1761f == null) {
            synchronized (d.class) {
                try {
                    if (f1761f == null) {
                        f1761f = new d();
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    private void o() {
        HandlerThread handlerThread = new HandlerThread("mi_ad_pubsub_uploader_worker");
        handlerThread.start();
        this.f1763a = new d0.b(handlerThread.getLooper());
    }

    private boolean p() {
        if (l.a() && !m.i("UploaderEngine") && !y.c.d().n()) {
            return true;
        }
        k.c("UploaderEngine", "not match the upload status，即将返回");
        return false;
    }

    private boolean q() {
        if (l.a() && !m.i("UploaderEngine") && !y.f.e().n()) {
            return true;
        }
        k.c("UploaderEngine", "hb data not match the upload status，即将返回");
        return false;
    }

    private void r() {
        try {
            Context contextC = com.ot.pubsub.util.b.c();
            if (contextC == null) {
                return;
            }
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.SCREEN_ON");
            intentFilter.addAction("android.intent.action.SCREEN_OFF");
            contextC.registerReceiver(this.f1766d, intentFilter);
        } catch (Exception e2) {
            k.j("UploaderEngine", "registerScreenReceiver: %s", e2.toString());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void s() {
        a aVar = this.f1764b;
        if (aVar != null) {
            aVar.sendEmptyMessageDelayed(123, f1762g);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void t() {
        a aVar = this.f1764b;
        if (aVar != null) {
            aVar.removeCallbacksAndMessages(123);
        }
    }

    private void u() {
        try {
            Context contextC = com.ot.pubsub.util.b.c();
            if (contextC == null) {
                return;
            }
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            contextC.registerReceiver(this.f1767e, intentFilter);
        } catch (Exception e2) {
            k.c("UploaderEngine", "registerNetReceiver: " + e2);
        }
    }

    public synchronized void b(int i2, boolean z2) {
        try {
            d0.b bVar = this.f1763a;
            if (bVar != null) {
                bVar.c(i2, z2);
            } else {
                k.h("UploaderEngine", "*** impossible, upload timer should not be null");
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    public synchronized void d(boolean z2) {
        try {
            d0.b bVar = this.f1763a;
            if (bVar != null) {
                bVar.d(z2);
            } else {
                k.h("UploaderEngine", "*** impossible, upload timer should not be null");
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    public boolean e(int i2) throws Throwable {
        ArrayList<v.a> arrayList;
        try {
            int i3 = 0;
            if (!q()) {
                return false;
            }
            int i4 = 100;
            while (true) {
                if (k.f724a) {
                    i4 = 1000;
                }
                if (i3 >= i4) {
                    return true;
                }
                d0.a aVarB = y.f.e().b(i2);
                if (aVarB == null || (arrayList = aVarB.f1754a) == null || arrayList.size() == 0) {
                    k.c("UploaderEngine", "满足条件的记录为空，即将返回");
                } else {
                    c.h(aVarB);
                    if (aVarB.f1757d) {
                        k.c("UploaderEngine", "No more records ");
                    } else {
                        i3++;
                    }
                }
                return true;
            }
        } catch (Exception unused) {
        }
    }

    public synchronized void j() {
        try {
            d0.b bVar = this.f1763a;
            if (bVar != null) {
                bVar.a();
            } else {
                k.h("UploaderEngine", "*** impossible, isInitCheckUpload timer should not be null");
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    public boolean k() throws Throwable {
        ArrayList<v.a> arrayList;
        try {
            int i2 = 0;
            if (!p()) {
                return false;
            }
            int i3 = 100;
            while (true) {
                if (k.f724a) {
                    i3 = 1000;
                }
                if (i2 >= i3) {
                    return true;
                }
                d0.a aVarL = y.c.d().l();
                if (aVarL == null || (arrayList = aVarL.f1754a) == null || arrayList.size() == 0) {
                    k.c("UploaderEngine", "满足条件的记录为空，即将返回");
                } else {
                    c.b(aVarL);
                    if (aVarL.f1757d) {
                        k.c("UploaderEngine", "No more records ");
                    } else {
                        i2++;
                    }
                }
                return true;
            }
        } catch (Exception unused) {
        }
    }

    public void m() {
        y.a.b(new i(this));
    }
}
