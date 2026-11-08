package com.miui.powerkeeper;

import android.content.Context;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.LocalLog;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseLongArray;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.IndentingPrintWriter;
import com.google.android.collect.Lists;
import com.miui.powerkeeper.controller.ActiveStateController;
import com.miui.powerkeeper.provider.AppActiveConfigure;
import com.miui.powerkeeper.ui.AppStateInfo;
import com.miui.powerkeeper.utils.Constant;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import miui.process.ProcessManager;

/* JADX INFO: loaded from: classes.dex */
public class AppActiveChecker {

    /* JADX INFO: renamed from: y, reason: collision with root package name */
    private static final String f61y = "AppActiveChecker";

    /* JADX INFO: renamed from: z, reason: collision with root package name */
    private static final boolean f62z = Log.isLoggable("power.debug", 3);

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private Context f63a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private PowerKeeperManager f64b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private Handler f65c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private l f66d;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private volatile boolean f68f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private PowerKeeperInterface.d0 f69g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private PowerKeeperInterface.f0 f70h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private PowerKeeperInterface.f f71i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private PowerKeeperInterface.e0 f72j;

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private PowerKeeperInterface.a f73k;

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    private PowerKeeperInterface.g0 f74l;

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    private PowerKeeperInterface.j f80r;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    private final Object f75m = new Object();

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    private boolean f76n = false;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    private SparseArray<k> f77o = new SparseArray<>();

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    private SparseBooleanArray f78p = new SparseBooleanArray();

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    private List<PowerKeeperInterface.b> f79q = Lists.newArrayList();

    /* JADX INFO: renamed from: s, reason: collision with root package name */
    private PowerKeeperInterface.r f81s = new e();

    /* JADX INFO: renamed from: t, reason: collision with root package name */
    private PowerKeeperInterface.z f82t = new f();

    /* JADX INFO: renamed from: u, reason: collision with root package name */
    private PowerKeeperInterface.x f83u = new g();

    /* JADX INFO: renamed from: v, reason: collision with root package name */
    private PowerKeeperInterface.i f84v = new h();

    /* JADX INFO: renamed from: w, reason: collision with root package name */
    private PowerKeeperInterface.y f85w = new i();

    /* JADX INFO: renamed from: x, reason: collision with root package name */
    private PowerKeeperInterface.b0 f86x = new j();

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private LocalLog f67e = new LocalLog(160);

    class a implements Runnable {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final /* synthetic */ PowerKeeperInterface.b f87a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        final /* synthetic */ int f88b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        final /* synthetic */ boolean f89c;

        a(PowerKeeperInterface.b bVar, int i2, boolean z2) {
            this.f87a = bVar;
            this.f88b = i2;
            this.f89c = z2;
        }

        @Override // java.lang.Runnable
        public void run() {
            PowerKeeperInterface.j jVar = this.f87a.f248b;
            if (jVar != null) {
                jVar.onAppActiveChange(this.f88b, this.f89c);
            }
        }
    }

    class b implements Handler.Callback {
        b() {
        }

        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            return AppActiveChecker.this.H(message);
        }
    }

    class c implements Runnable {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final /* synthetic */ int f92a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        final /* synthetic */ Bundle f93b;

        c(int i2, Bundle bundle) {
            this.f92a = i2;
            this.f93b = bundle;
        }

        @Override // java.lang.Runnable
        public void run() {
            AppActiveChecker.this.K(this.f92a, this.f93b);
        }
    }

    class d implements PowerKeeperInterface.j {
        d() {
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.j
        public void onAppActiveChange(int i2, boolean z2) {
            AppActiveChecker.this.f65c.sendMessage(AppActiveChecker.this.f65c.obtainMessage(2, i2, z2 ? 1 : 0));
        }
    }

    class e implements PowerKeeperInterface.r {
        e() {
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.r
        public void onForegroundActivitiesChanged(int i2, boolean z2) {
            synchronized (AppActiveChecker.this.f75m) {
                try {
                    if (AppActiveChecker.this.f76n) {
                        AppActiveChecker.this.O(i2, z2);
                    } else {
                        if (AppActiveChecker.f62z) {
                            Log.v(AppActiveChecker.f61y, "still disabled");
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    class f implements PowerKeeperInterface.z {
        f() {
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.z
        public void onRegistered(boolean z2) {
            synchronized (AppActiveChecker.this.f75m) {
                try {
                    if (AppActiveChecker.this.f76n) {
                        if (AppActiveChecker.this.f68f != AppActiveChecker.this.f64b.isScreenOn()) {
                            AppActiveChecker.this.S();
                        }
                    } else {
                        if (AppActiveChecker.f62z) {
                            Log.v(AppActiveChecker.f61y, "still disabled");
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.z
        public void onScreenChanged(boolean z2) {
            synchronized (AppActiveChecker.this.f75m) {
                try {
                    if (AppActiveChecker.this.f76n) {
                        if (AppActiveChecker.this.f68f != AppActiveChecker.this.f64b.isScreenOn()) {
                            AppActiveChecker.this.S();
                        }
                    } else {
                        if (AppActiveChecker.f62z) {
                            Log.v(AppActiveChecker.f61y, "still disabled");
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    class h implements PowerKeeperInterface.i {
        h() {
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.i
        public void onAlarmHappen(long j2) {
            synchronized (AppActiveChecker.this.f75m) {
                try {
                    if (!AppActiveChecker.this.f76n) {
                        if (AppActiveChecker.f62z) {
                            Log.v(AppActiveChecker.f61y, "still disabled");
                        }
                        return;
                    }
                    AppActiveChecker.this.F("onAlarmHappen, elapsedRealtime = " + j2 + "ms");
                    AppActiveChecker.this.f66d.a(j2);
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    class i implements PowerKeeperInterface.y {
        i() {
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.y
        public void onProcessDiedListener(int i2, int i3) {
            synchronized (AppActiveChecker.this.f75m) {
                try {
                    if (!AppActiveChecker.this.f76n) {
                        if (AppActiveChecker.f62z) {
                            Log.v(AppActiveChecker.f61y, "still disabled");
                        }
                        return;
                    }
                    try {
                        List runningProcessInfoByUid = ProcessManager.getRunningProcessInfoByUid(i2);
                        if (runningProcessInfoByUid == null || runningProcessInfoByUid.isEmpty()) {
                            AppActiveChecker.this.N(i2);
                            AppActiveChecker.this.G(i2, 1);
                        } else {
                            AppActiveChecker.this.F("onProcessDiedListener ignore for other process alive uid=" + i2);
                        }
                    } catch (RemoteException e2) {
                        Log.w(AppActiveChecker.f61y, "onProcessDiedListener", e2);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    class k {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        int f102a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        int f103b = 2;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        int f104c = 1;

        /* JADX INFO: renamed from: d, reason: collision with root package name */
        long f105d;

        /* JADX INFO: renamed from: e, reason: collision with root package name */
        int f106e;

        /* JADX INFO: renamed from: f, reason: collision with root package name */
        int f107f;

        /* JADX INFO: renamed from: g, reason: collision with root package name */
        long f108g;

        /* JADX INFO: renamed from: h, reason: collision with root package name */
        long f109h;

        /* JADX INFO: renamed from: i, reason: collision with root package name */
        int f110i;

        /* JADX INFO: renamed from: j, reason: collision with root package name */
        int f111j;

        /* JADX INFO: renamed from: k, reason: collision with root package name */
        int f112k;

        k(int i2) {
            this.f102a = i2;
            b();
        }

        k a() {
            k kVar = AppActiveChecker.this.new k(this.f102a);
            kVar.f112k = this.f112k;
            kVar.f105d = this.f105d;
            kVar.f107f = this.f107f;
            kVar.f109h = this.f109h;
            kVar.f111j = this.f111j;
            return kVar;
        }

        void b() {
            this.f112k = 1;
            this.f105d = 0L;
            this.f106e = 0;
            this.f107f = -1;
            this.f108g = 0L;
            this.f109h = -1L;
            this.f110i = 0;
            this.f111j = -1;
        }

        boolean c(k kVar) {
            return this.f112k == kVar.f112k && this.f105d == kVar.f105d && this.f107f == kVar.f107f && this.f109h == kVar.f109h && this.f111j == kVar.f111j;
        }

        void d() {
            this.f103b = 1;
            this.f106e = 0;
            this.f108g = AppActiveChecker.this.B(this.f102a);
            this.f110i = 0;
        }

        void e(Bundle bundle) {
            int i2 = bundle.getInt("POLICY", 1);
            this.f112k = i2;
            this.f105d = 0L;
            this.f107f = -1;
            this.f109h = -1L;
            this.f111j = -1;
            if (i2 == 0) {
                if (bundle.containsKey(ActiveStateController.KEY_DELAY_PERIOD_MINUTE)) {
                    this.f105d = bundle.getInt(ActiveStateController.KEY_DELAY_PERIOD_MINUTE) * 60000;
                }
                long j2 = this.f105d;
                if (j2 <= 0) {
                    if (j2 < 0) {
                        this.f105d = -1L;
                    }
                } else {
                    this.f107f = bundle.getInt("MAX_PERIOD_COUNT", this.f107f);
                    if (bundle.containsKey("MIN_DATA_KBYTES")) {
                        this.f109h = bundle.getInt("MIN_DATA_KBYTES") * 1024;
                    }
                    this.f111j = bundle.getInt("MAX_INACTIVE_COUNT", this.f111j);
                }
            }
        }

        void f() {
            this.f103b = 2;
            this.f106e = 0;
            this.f108g = 0L;
            this.f110i = 0;
        }

        void g() {
            this.f103b = 0;
            this.f106e = 0;
            this.f108g = 0L;
            this.f110i = 0;
        }
    }

    class l {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private SparseLongArray f114a = new SparseLongArray();

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        private Handler f115b;

        l(Handler handler) {
            this.f115b = handler;
        }

        void a(long j2) {
            for (int size = this.f114a.size() - 1; size >= 0; size--) {
                int iKeyAt = this.f114a.keyAt(size);
                long jValueAt = this.f114a.valueAt(size);
                if (jValueAt <= j2) {
                    AppActiveChecker.this.F("onAlarm: uid = " + iKeyAt + " elapsedRealtime = " + jValueAt);
                    k kVarA = AppActiveChecker.this.A(iKeyAt);
                    this.f115b.removeMessages(1, kVarA);
                    this.f115b.sendMessage(Message.obtain(this.f115b, 1, kVarA));
                    this.f114a.delete(iKeyAt);
                }
            }
        }

        void b() {
            c();
            this.f115b = null;
            this.f114a = null;
        }

        void c() {
            this.f115b.removeCallbacksAndMessages(null);
            this.f114a.clear();
        }

        void d(int i2) {
            k kVarA = AppActiveChecker.this.A(i2);
            if (this.f115b.hasMessages(1, kVarA)) {
                AppActiveChecker.this.F("removeMessages: hasMessage, uid = " + i2);
            }
            this.f115b.removeMessages(1, kVarA);
            this.f114a.delete(i2);
        }

        void e(int i2, long j2) {
            k kVarA = AppActiveChecker.this.A(i2);
            if (j2 <= 0) {
                this.f115b.sendMessage(Message.obtain(this.f115b, 1, kVarA));
                return;
            }
            this.f115b.sendMessageDelayed(Message.obtain(this.f115b, 1, kVarA), j2);
            long jElapsedRealtime = SystemClock.elapsedRealtime() + j2;
            this.f114a.put(i2, jElapsedRealtime);
            AppActiveChecker.this.F("sendMessageDelayed: uid = " + i2 + " delayMillis = " + j2 + " elapsedRealtime = " + jElapsedRealtime);
        }
    }

    public AppActiveChecker(Context context, PowerKeeperManager powerKeeperManager) {
        this.f63a = context;
        this.f64b = powerKeeperManager;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public k A(int i2) {
        if (this.f77o.get(i2) == null) {
            this.f77o.put(i2, new k(i2));
        }
        return this.f77o.get(i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public long B(int i2) {
        return TrafficStats.getUidRxBytes(i2) + TrafficStats.getUidTxBytes(i2);
    }

    private boolean C(int i2) {
        return this.f64b.isUidForeground(i2);
    }

    private boolean D(int i2) {
        return this.f78p.get(i2, false);
    }

    private boolean E(int i2) {
        return UserHandle.isApp(i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void F(String str) {
        if (f62z) {
            Log.d(f61y, str);
        }
        this.f67e.log(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void G(int i2, int i3) {
        boolean z2 = i3 == 0;
        synchronized (this.f79q) {
            try {
                for (PowerKeeperInterface.b bVar : this.f79q) {
                    Handler handler = bVar.f247a;
                    if (handler == null) {
                        handler = BackgroundThread.getHandler();
                    }
                    handler.post(new a(bVar, i2, z2));
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean H(Message message) {
        int i2 = message.what;
        if (i2 == 1) {
            int i3 = ((k) message.obj).f102a;
            synchronized (this.f75m) {
                try {
                    if (!this.f76n) {
                        if (f62z) {
                            Log.v(f61y, "still disabled");
                        }
                        return true;
                    }
                    synchronized (this.f75m) {
                        try {
                            if (this.f76n) {
                                Q(i3);
                                return true;
                            }
                            if (f62z) {
                                Log.v(f61y, "still disabled");
                            }
                            return true;
                        } catch (Throwable th) {
                            throw th;
                        }
                    }
                } catch (Throwable th2) {
                    throw th2;
                }
            }
        }
        if (i2 != 2) {
            return false;
        }
        boolean z2 = message.arg2 == 1;
        String str = f61y;
        StringBuilder sb = new StringBuilder();
        sb.append("uid:");
        sb.append(message.arg1);
        sb.append(z2 ? " change to active" : "change to inactive");
        Log.i(str, sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("uid:");
        sb2.append(message.arg1);
        sb2.append(z2 ? " change to active" : "change to inactive");
        F(sb2.toString());
        synchronized (this.f75m) {
            try {
                if (!this.f76n) {
                    if (Constant.DBG_ACTIVE) {
                        Log.v(str, "still disabled");
                    }
                    return true;
                }
                if (z2 != this.f78p.get(message.arg1, false)) {
                    this.f78p.put(message.arg1, z2);
                    V(message.arg1, z2);
                }
                return true;
            } catch (Throwable th3) {
                throw th3;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void K(int i2, Bundle bundle) {
        if (bundle.getInt("POLICY", 0) == 0) {
            F("setUidPolicyUnchecked, uid: " + i2 + " bundle: " + bundle);
        }
        synchronized (this.f75m) {
            try {
                if (!this.f76n) {
                    if (f62z) {
                        Log.v(f61y, "setUidPolicyUnchecked, still disabled");
                    }
                    return;
                }
                k kVarA = A(i2);
                k kVarA2 = kVarA.a();
                kVarA.e(bundle);
                if (!kVarA.c(kVarA2)) {
                    R(i2);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private void M(k kVar) {
        int i2 = kVar.f102a;
        int i3 = (kVar.f103b != 2 || D(i2)) ? 0 : 1;
        if (i3 != kVar.f104c) {
            F("updateAppActiveStateForUidLocked, uid = " + i2 + " appActiveState = " + i3);
            kVar.f104c = i3;
            G(i2, i3);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void N(int i2) {
        if (this.f77o.get(i2) != null) {
            this.f77o.get(i2).f104c = 1;
        } else {
            this.f77o.put(i2, new k(i2));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void O(int i2, boolean z2) {
        if (E(i2)) {
            if (f62z) {
                Log.d(f61y, "updateDownloadStateForFgUidChangedLocked, mScreenOn = " + this.f68f + " uid = " + i2 + " foreground = " + z2);
            }
            k kVarA = A(i2);
            if (!this.f68f) {
                int i3 = kVarA.f112k;
                if (i3 == 0) {
                    int i4 = kVarA.f103b;
                    if (i4 == 0) {
                        this.f66d.d(i2);
                        if (kVarA.f105d > 0) {
                            kVarA.d();
                            this.f66d.e(i2, kVarA.f105d);
                        } else {
                            kVarA.f();
                        }
                    } else if (i4 == 1 && kVarA.f105d < 0) {
                        this.f66d.d(i2);
                        kVarA.f();
                    }
                } else if (i3 == 1) {
                    this.f66d.d(i2);
                    kVarA.f();
                }
            } else if (z2) {
                this.f66d.d(i2);
                kVarA.g();
            } else {
                int i5 = kVarA.f112k;
                if (i5 == 0) {
                    if (kVarA.f103b == 0) {
                        this.f66d.d(i2);
                        long j2 = kVarA.f105d;
                        if (j2 > 0) {
                            kVarA.d();
                            this.f66d.e(i2, kVarA.f105d);
                        } else if (j2 == 0) {
                            kVarA.f();
                        } else {
                            kVarA.d();
                        }
                    }
                } else if (i5 == 1) {
                    this.f66d.d(i2);
                    kVarA.f();
                }
            }
            M(kVarA);
        }
    }

    private void P(int i2) {
        if (E(i2)) {
            k kVarA = A(i2);
            if (!this.f68f) {
                int i3 = kVarA.f112k;
                if (i3 == 0) {
                    int i4 = kVarA.f103b;
                    if (i4 == 0) {
                        this.f66d.d(i2);
                        if (kVarA.f105d > 0) {
                            kVarA.d();
                            this.f66d.e(i2, kVarA.f105d);
                        } else {
                            kVarA.f();
                        }
                    } else if (i4 == 1 && kVarA.f105d < 0) {
                        this.f66d.d(i2);
                        kVarA.f();
                    }
                } else if (i3 == 1) {
                    this.f66d.d(i2);
                    kVarA.f();
                }
            } else if (C(i2)) {
                this.f66d.d(i2);
                kVarA.g();
            }
            M(kVarA);
        }
    }

    private void Q(int i2) {
        k kVarA = A(i2);
        this.f66d.d(i2);
        if (kVarA.f112k == 0 && kVarA.f103b == 1) {
            boolean z2 = f62z;
            if (z2) {
                Log.d(f61y, "updateDownloadStateForTimeOutLocked, uid = " + i2);
            }
            int i3 = kVarA.f106e + 1;
            kVarA.f106e = i3;
            if (i3 >= kVarA.f107f) {
                kVarA.f();
            } else if (kVarA.f109h > 0) {
                long jB = B(i2);
                long j2 = jB - kVarA.f108g;
                if (z2) {
                    Log.d(f61y, "updateDownloadStateForTimeOutLocked, uid: " + i2 + " KB: " + (j2 / 1024));
                }
                if (j2 <= kVarA.f109h) {
                    int i4 = kVarA.f110i + 1;
                    kVarA.f110i = i4;
                    if (i4 >= kVarA.f111j) {
                        F("updateDownloadStateForTimeOutLocked, uid: " + i2 + " KB: " + (j2 / 1024) + ", networkStateInActive is true");
                        kVarA.f();
                    } else {
                        F("updateDownloadStateForTimeOutLocked, uid: " + i2 + " KB: " + (j2 / 1024) + " currentInActiveCount: " + kVarA.f110i);
                        kVarA.f108g = jB;
                    }
                } else {
                    kVarA.f108g = jB;
                    kVarA.f110i = 0;
                }
                this.f66d.e(i2, kVarA.f105d);
            } else {
                this.f66d.e(i2, kVarA.f105d);
            }
            M(kVarA);
        }
    }

    private void R(int i2) {
        if (E(i2)) {
            F("updateDownloadStateForUidResetPolicyLocked, uid = " + i2);
            boolean zC = C(i2);
            k kVarA = A(i2);
            this.f66d.d(i2);
            if (!this.f68f) {
                int i3 = kVarA.f112k;
                if (i3 == 0) {
                    int i4 = kVarA.f103b;
                    if (i4 == 0 || i4 == 1 || (i4 == 2 && kVarA.f104c == 0)) {
                        if (kVarA.f105d > 0) {
                            kVarA.d();
                            this.f66d.e(i2, kVarA.f105d);
                        } else {
                            kVarA.f();
                        }
                    } else if (i4 == 2) {
                        kVarA.f();
                    }
                } else if (i3 == 1) {
                    kVarA.f();
                }
            } else if (zC) {
                kVarA.g();
            } else {
                int i5 = kVarA.f112k;
                if (i5 == 0) {
                    int i6 = kVarA.f103b;
                    if (i6 == 0 || i6 == 1 || (i6 == 2 && kVarA.f104c == 0)) {
                        long j2 = kVarA.f105d;
                        if (j2 > 0) {
                            kVarA.d();
                            this.f66d.e(i2, kVarA.f105d);
                        } else if (j2 == 0) {
                            kVarA.f();
                        } else {
                            kVarA.d();
                        }
                    } else if (i6 == 2) {
                        kVarA.f();
                    }
                } else if (i5 == 1) {
                    kVarA.f();
                }
            }
            M(kVarA);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void S() {
        this.f68f = this.f64b.isScreenOn();
        F("updateForScreenChangedLocked, mScreenOn = " + this.f68f);
        int size = this.f77o.size();
        for (int i2 = 0; i2 < size; i2++) {
            P(this.f77o.keyAt(i2));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void T(int i2) {
        F("updateForUidRemovedLocked, uid=" + i2);
        this.f66d.d(i2);
        this.f77o.remove(i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void U(int i2) {
        F("updateForUserRemovedLocked, userId=" + i2);
        SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
        int size = this.f77o.size();
        for (int i3 = 0; i3 < size; i3++) {
            if (UserHandle.getUserId(this.f77o.keyAt(i3)) == i2) {
                sparseBooleanArray.put(this.f77o.keyAt(i3), true);
            }
        }
        for (int i4 = 0; i4 < sparseBooleanArray.size(); i4++) {
            T(sparseBooleanArray.keyAt(i4));
        }
    }

    private void V(int i2, boolean z2) {
        k kVarA = A(i2);
        if (z2) {
            M(kVarA);
        } else {
            kVarA.g();
            R(i2);
        }
    }

    private void u() {
        if (this.f80r != null) {
            this.f64b.getActiveController().unregisterActiveListener(this.f80r, 11);
            this.f80r = null;
        }
        this.f78p.clear();
    }

    private void x() {
        this.f78p.clear();
        this.f64b.getActiveController().loadActiveUid(this.f78p, 11);
        this.f80r = new d();
        this.f64b.getActiveController().registerActiveListener(this.f80r, 11);
    }

    public void I(PowerKeeperInterface.b bVar) {
        synchronized (this.f79q) {
            try {
                if (!this.f79q.contains(bVar)) {
                    this.f79q.add(bVar);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void J(int i2, Bundle bundle) {
        if (!UserHandle.isApp(i2)) {
            if (f62z) {
                Log.e(f61y, "cannot apply policy to UID " + i2);
                return;
            }
            return;
        }
        synchronized (this.f75m) {
            try {
                if (this.f76n) {
                    this.f65c.post(new c(i2, bundle));
                } else {
                    if (f62z) {
                        Log.v(f61y, "still disabled");
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void L(PowerKeeperInterface.b bVar) {
        synchronized (this.f79q) {
            try {
                if (this.f79q.contains(bVar)) {
                    this.f79q.remove(bVar);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void t() {
        synchronized (this.f75m) {
            try {
                if (!this.f76n) {
                    if (f62z) {
                        Log.v(f61y, "still disabled");
                    }
                    return;
                }
                Log.i(f61y, "disable");
                this.f76n = false;
                this.f77o.clear();
                u();
                this.f64b.getUserCheckManager().K(this.f74l);
                this.f64b.unregisterAlarmListener(this.f73k);
                this.f64b.unregisterForegroundActivitiesChangedListener(this.f71i);
                this.f64b.unregisterScreenChangedListener(this.f70h);
                this.f64b.unregisterPackageChangedListener(this.f69g);
                this.f64b.unregisterProcessDiedListener(this.f72j);
                this.f69g.a();
                this.f69g = null;
                this.f70h.a();
                this.f70h = null;
                this.f71i.a();
                this.f71i = null;
                this.f73k.a();
                this.f73k = null;
                this.f74l.a();
                this.f74l = null;
                this.f66d.b();
                this.f66d = null;
                this.f65c.removeCallbacksAndMessages(null);
                this.f65c = null;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void v(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        synchronized (this.f75m) {
            try {
                indentingPrintWriter.println("#######dump AppActiveChecker#######");
                int size = this.f77o.size();
                for (int i2 = 0; i2 < size; i2++) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("uid: ");
                    sb.append(this.f77o.keyAt(i2));
                    sb.append(" state: ");
                    sb.append(this.f77o.valueAt(i2).f104c == 0 ? "ACTIVE" : "INACTIVE");
                    indentingPrintWriter.println(sb.toString());
                }
                this.f67e.dump(fileDescriptor, indentingPrintWriter, strArr);
                indentingPrintWriter.println("end#######dump AppActiveChecker#######end");
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void w() {
        synchronized (this.f75m) {
            try {
                if (this.f76n) {
                    if (f62z) {
                        Log.v(f61y, "already enabled");
                    }
                    return;
                }
                Log.i(f61y, "enable");
                this.f76n = true;
                this.f77o.clear();
                Handler handler = new Handler(PowerKeeperManager.getThread().getLooper(), new b());
                this.f65c = handler;
                this.f66d = new l(handler);
                S();
                this.f69g = new PowerKeeperInterface.d0(this.f65c, this.f83u);
                this.f70h = new PowerKeeperInterface.f0(this.f65c, this.f82t);
                this.f71i = new PowerKeeperInterface.f(this.f65c, this.f81s);
                this.f72j = new PowerKeeperInterface.e0(this.f65c, this.f85w);
                this.f73k = new PowerKeeperInterface.a(this.f65c, this.f84v);
                this.f74l = new PowerKeeperInterface.g0(this.f65c, this.f86x);
                this.f64b.registerPackageChangedListener(this.f69g);
                this.f64b.registerProcessDiedListener(this.f72j);
                this.f64b.registerScreenChangedListener(this.f70h);
                this.f64b.registerForegroundActivitiesChangedListener(this.f71i);
                this.f64b.registerAlarmListener(this.f73k);
                this.f64b.getUserCheckManager().J(this.f74l);
                x();
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public boolean y(int i2) {
        synchronized (this.f75m) {
            try {
                return A(i2).f104c == 0;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public List<AppStateInfo> z() {
        ArrayList arrayList = new ArrayList();
        int size = this.f77o.size();
        for (int i2 = 0; i2 < size; i2++) {
            AppStateInfo appStateInfo = new AppStateInfo();
            appStateInfo.uid = this.f77o.keyAt(i2);
            appStateInfo.activeState = this.f77o.valueAt(i2).f104c == 0 ? AppActiveConfigure.Columns.ACTIVE : "inactive";
            arrayList.add(appStateInfo);
        }
        return arrayList;
    }

    class g implements PowerKeeperInterface.x {
        g() {
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.x
        public void onUidRemoved(int i2, int i3) {
            synchronized (AppActiveChecker.this.f75m) {
                try {
                    if (AppActiveChecker.this.f76n) {
                        AppActiveChecker.this.T(i3);
                    } else {
                        if (AppActiveChecker.f62z) {
                            Log.v(AppActiveChecker.f61y, "still disabled");
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.x
        public void onRegistered() {
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.x
        public void onUserReadyPackageAdded(int i2, Map<String, Integer> map) {
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.x
        public void onPackageAdded(int i2, String str, int i3) {
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.x
        public void onPackageRemoved(int i2, String str, int i3) {
        }
    }

    class j implements PowerKeeperInterface.b0 {
        j() {
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
        public void onUserEnableStatusChange(int i2, boolean z2) {
            synchronized (AppActiveChecker.this.f75m) {
                try {
                    if (AppActiveChecker.this.f76n) {
                        if (!z2) {
                            AppActiveChecker.this.U(i2);
                        }
                    } else {
                        if (AppActiveChecker.f62z) {
                            Log.v(AppActiveChecker.f61y, "still disabled");
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
        public void onUserRunningStatusChange(int i2, boolean z2) {
            synchronized (AppActiveChecker.this.f75m) {
                try {
                    if (AppActiveChecker.this.f76n) {
                        if (!z2) {
                            AppActiveChecker.this.U(i2);
                        }
                    } else {
                        if (AppActiveChecker.f62z) {
                            Log.v(AppActiveChecker.f61y, "still disabled");
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
        public void onXSpaceStatusChange(int i2, boolean z2) {
            synchronized (AppActiveChecker.this.f75m) {
                try {
                    if (AppActiveChecker.this.f76n) {
                        if (!z2) {
                            AppActiveChecker.this.U(i2);
                        }
                    } else {
                        if (AppActiveChecker.f62z) {
                            Log.v(AppActiveChecker.f61y, "still disabled");
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
        public void onUserForegroundStatusChange(int i2, boolean z2) {
        }
    }
}
