package com.miui.powerkeeper;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import com.android.internal.os.BackgroundThread;
import com.google.android.collect.Lists;
import com.miui.powerkeeper.appcontrol.HideModeStateMachineConfig;
import com.miui.powerkeeper.event.EventsAggregator;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class AppRuleChecker implements PowerKeeperInterface.l {
    protected static final boolean B = Log.isLoggable("power.debug", 3);
    private PowerKeeperInterface.o A;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    protected String f117a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private Context f118b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private PowerKeeperManager f119c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private Handler f120d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private l f121e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private AppActiveChecker f122f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private volatile boolean f123g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private PowerKeeperInterface.d0 f124h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private PowerKeeperInterface.f0 f125i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private PowerKeeperInterface.a f126j;

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private PowerKeeperInterface.b f127k;

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    private PowerKeeperInterface.g0 f128l;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    private PowerKeeperInterface.e f129m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    private final Object f130n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    private boolean f131o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    private boolean f132p;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    private boolean f133q;

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    private boolean f134r;

    /* JADX INFO: renamed from: s, reason: collision with root package name */
    private boolean f135s;

    /* JADX INFO: renamed from: t, reason: collision with root package name */
    private SparseArray<j> f136t;

    /* JADX INFO: renamed from: u, reason: collision with root package name */
    private List<PowerKeeperInterface.c> f137u;

    /* JADX INFO: renamed from: v, reason: collision with root package name */
    private PowerKeeperInterface.z f138v;

    /* JADX INFO: renamed from: w, reason: collision with root package name */
    private PowerKeeperInterface.x f139w;

    /* JADX INFO: renamed from: x, reason: collision with root package name */
    private PowerKeeperInterface.i f140x;

    /* JADX INFO: renamed from: y, reason: collision with root package name */
    private PowerKeeperInterface.j f141y;

    /* JADX INFO: renamed from: z, reason: collision with root package name */
    private PowerKeeperInterface.b0 f142z;

    class a implements Runnable {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final /* synthetic */ int f143a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        final /* synthetic */ Bundle f144b;

        a(int i2, Bundle bundle) {
            this.f143a = i2;
            this.f144b = bundle;
        }

        @Override // java.lang.Runnable
        public void run() {
            AppRuleChecker.this.J(this.f143a, this.f144b);
        }
    }

    class b implements PowerKeeperInterface.z {
        b() {
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.z
        public void onRegistered(boolean z2) {
            synchronized (AppRuleChecker.this.f130n) {
                try {
                    if (AppRuleChecker.this.f131o) {
                        if (AppRuleChecker.this.f123g != AppRuleChecker.this.f119c.isScreenOn()) {
                            AppRuleChecker.this.A();
                        }
                    } else {
                        if (AppRuleChecker.B) {
                            Log.v(AppRuleChecker.this.f117a, "still disabled");
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.z
        public void onScreenChanged(boolean z2) {
            synchronized (AppRuleChecker.this.f130n) {
                try {
                    if (AppRuleChecker.this.f131o) {
                        if (AppRuleChecker.this.f123g != AppRuleChecker.this.f119c.isScreenOn()) {
                            AppRuleChecker.this.A();
                        }
                    } else {
                        if (AppRuleChecker.B) {
                            Log.v(AppRuleChecker.this.f117a, "still disabled");
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    class d implements PowerKeeperInterface.i {
        d() {
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.i
        public void onAlarmHappen(long j2) {
            synchronized (AppRuleChecker.this.f130n) {
                try {
                    if (!AppRuleChecker.this.f131o) {
                        if (AppRuleChecker.B) {
                            Log.v(AppRuleChecker.this.f117a, "still disabled");
                        }
                        return;
                    }
                    if (AppRuleChecker.B) {
                        Log.d(AppRuleChecker.this.f117a, "onAlarmHappen, elapsedRealtime = " + j2 + "ms");
                    }
                    AppRuleChecker.this.f121e.a(j2);
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    class e implements PowerKeeperInterface.j {
        e() {
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.j
        public void onAppActiveChange(int i2, boolean z2) {
            synchronized (AppRuleChecker.this.f130n) {
                try {
                    if (AppRuleChecker.this.f131o) {
                        AppRuleChecker.this.H(i2, z2);
                    } else {
                        if (AppRuleChecker.B) {
                            Log.v(AppRuleChecker.this.f117a, "still disabled");
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    class f implements PowerKeeperInterface.b0 {
        f() {
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
        public void onUserEnableStatusChange(int i2, boolean z2) {
            synchronized (AppRuleChecker.this.f130n) {
                try {
                    if (AppRuleChecker.this.f131o) {
                        if (!z2) {
                            AppRuleChecker.this.C(i2);
                        }
                    } else {
                        if (AppRuleChecker.B) {
                            Log.v(AppRuleChecker.this.f117a, "still disabled");
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
        public void onUserForegroundStatusChange(int i2, boolean z2) {
            synchronized (AppRuleChecker.this.f130n) {
            }
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
        public void onUserRunningStatusChange(int i2, boolean z2) {
            synchronized (AppRuleChecker.this.f130n) {
                try {
                    if (AppRuleChecker.this.f131o) {
                        if (!z2) {
                            AppRuleChecker.this.C(i2);
                        }
                    } else {
                        if (AppRuleChecker.B) {
                            Log.v(AppRuleChecker.this.f117a, "still disabled");
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
        public void onXSpaceStatusChange(int i2, boolean z2) {
            synchronized (AppRuleChecker.this.f130n) {
                try {
                    if (AppRuleChecker.this.f131o) {
                        if (!z2) {
                            AppRuleChecker.this.C(i2);
                        }
                    } else {
                        if (AppRuleChecker.B) {
                            Log.v(AppRuleChecker.this.f117a, "still disabled");
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    class g implements PowerKeeperInterface.o {
        g() {
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.o
        public void batteryBecomeHeat() {
            synchronized (AppRuleChecker.this.f130n) {
                try {
                    if (AppRuleChecker.this.f131o) {
                        AppRuleChecker.this.z(true);
                    } else {
                        if (AppRuleChecker.B) {
                            Log.v(AppRuleChecker.this.f117a, "still disabled");
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.o
        public void batteryCoolDown() {
            synchronized (AppRuleChecker.this.f130n) {
                try {
                    if (AppRuleChecker.this.f131o) {
                        AppRuleChecker.this.z(false);
                    } else {
                        if (AppRuleChecker.B) {
                            Log.v(AppRuleChecker.this.f117a, "still disabled");
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    class h implements Runnable {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final /* synthetic */ PowerKeeperInterface.c f152a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        final /* synthetic */ int f153b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        final /* synthetic */ int f154c;

        h(PowerKeeperInterface.c cVar, int i2, int i3) {
            this.f152a = cVar;
            this.f153b = i2;
            this.f154c = i3;
        }

        @Override // java.lang.Runnable
        public void run() {
            PowerKeeperInterface.k kVar = this.f152a.f250b;
            if (kVar != null) {
                kVar.onAppRuleChange(this.f153b, this.f154c);
            }
        }
    }

    class j {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        int f159a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        int f160b = 2;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        int f161c;

        /* JADX INFO: renamed from: d, reason: collision with root package name */
        i f162d;

        /* JADX INFO: renamed from: e, reason: collision with root package name */
        i f163e;

        /* JADX INFO: renamed from: f, reason: collision with root package name */
        i f164f;

        public j(int i2) {
            this.f159a = i2;
            if (AppRuleChecker.this.f135s) {
                this.f161c = 1;
            } else {
                this.f161c = 0;
            }
            b();
        }

        j a() {
            j jVar = AppRuleChecker.this.new j(this.f159a);
            jVar.f162d = this.f162d.a();
            return jVar;
        }

        void b() {
            i iVar = AppRuleChecker.this.new i();
            this.f163e = iVar;
            this.f164f = null;
            this.f162d = iVar;
        }

        boolean c(j jVar) {
            return this.f162d.c(jVar.f162d);
        }

        void d() {
            this.f160b = 1;
        }

        void e() {
            if (this.f164f == null || !AppRuleChecker.this.f132p) {
                this.f162d = this.f163e;
            } else {
                this.f162d = this.f164f;
            }
        }

        void f(Bundle bundle) {
            long j2;
            int i2 = bundle.getInt("POLICY", 1);
            long j3 = -1;
            if (i2 == 2) {
                j2 = bundle.containsKey(HideModeStateMachineConfig.KEY_DELAY_MINUTE) ? bundle.getInt(HideModeStateMachineConfig.KEY_DELAY_MINUTE) * 60000 : 0L;
                if (j2 < 0) {
                    j2 = -1;
                }
            } else {
                j2 = 0;
            }
            this.f163e.d(i2, j2);
            if (bundle.containsKey(HideModeStateMachineConfig.KEY_HOT_POLICY)) {
                int i3 = bundle.getInt(HideModeStateMachineConfig.KEY_HOT_POLICY, 1);
                if (i3 == 2) {
                    long j4 = bundle.containsKey(HideModeStateMachineConfig.KEY_HOT_DELAY_MINUTE) ? bundle.getInt(HideModeStateMachineConfig.KEY_HOT_DELAY_MINUTE) * 60000 : 0L;
                    if (j4 >= 0) {
                        j3 = j4;
                    }
                } else {
                    j3 = 0;
                }
                i iVar = this.f164f;
                if (iVar == null) {
                    this.f164f = AppRuleChecker.this.new i(i3, j3);
                } else {
                    iVar.d(i3, j3);
                }
            } else {
                this.f164f = null;
            }
            if (this.f164f == null || !AppRuleChecker.this.f132p) {
                this.f162d = this.f163e;
            } else {
                this.f162d = this.f164f;
            }
        }

        void g() {
            this.f160b = 2;
        }

        void h() {
            this.f160b = 0;
        }
    }

    private class k implements Handler.Callback {
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            int i2 = message.what;
            if (i2 == -23 || i2 == -11 || i2 == -10) {
                AppRuleChecker.this.r(message.arg1);
            }
            return AppRuleChecker.this.x(message);
        }

        private k() {
        }
    }

    class l {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private SparseLongArray f167a = new SparseLongArray();

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        private Handler f168b;

        l(Handler handler) {
            this.f168b = handler;
        }

        void a(long j2) {
            for (int size = this.f167a.size() - 1; size >= 0; size--) {
                int iKeyAt = this.f167a.keyAt(size);
                long jValueAt = this.f167a.valueAt(size);
                if (jValueAt <= j2) {
                    if (AppRuleChecker.B) {
                        Log.d(AppRuleChecker.this.f117a, "onAlarm: uid = " + iKeyAt + " elapsedRealtime = " + jValueAt);
                    }
                    j jVarQ = AppRuleChecker.this.q(iKeyAt);
                    this.f168b.removeMessages(1, jVarQ);
                    this.f168b.sendMessage(Message.obtain(this.f168b, 1, jVarQ));
                    this.f167a.delete(iKeyAt);
                }
            }
        }

        void b() {
            c();
            this.f168b = null;
            this.f167a = null;
        }

        void c() {
            this.f168b.removeCallbacksAndMessages(null);
            this.f167a.clear();
        }

        void d(int i2) {
            j jVarQ = AppRuleChecker.this.q(i2);
            if (this.f168b.hasMessages(1, jVarQ) && AppRuleChecker.B) {
                Log.d(AppRuleChecker.this.f117a, "removeMessages: hasMessage, uid = " + i2);
            }
            this.f168b.removeMessages(1, jVarQ);
            this.f167a.delete(i2);
        }

        void e(int i2, long j2) {
            j jVarQ = AppRuleChecker.this.q(i2);
            if (j2 <= 0) {
                this.f168b.sendMessage(Message.obtain(this.f168b, 1, jVarQ));
                return;
            }
            this.f168b.sendMessageDelayed(Message.obtain(this.f168b, 1, jVarQ), j2);
            long jElapsedRealtime = SystemClock.elapsedRealtime() + j2;
            this.f167a.put(i2, jElapsedRealtime);
            if (AppRuleChecker.B) {
                Log.d(AppRuleChecker.this.f117a, "sendMessageDelayed: uid = " + i2 + " delayMillis = " + j2 + " elapsedRealtime = " + jElapsedRealtime);
            }
        }
    }

    public AppRuleChecker(Context context, PowerKeeperManager powerKeeperManager) {
        this.f117a = AppRuleChecker.class.getSimpleName();
        this.f130n = new Object();
        this.f131o = false;
        this.f132p = false;
        this.f133q = false;
        this.f134r = false;
        this.f135s = false;
        this.f136t = new SparseArray<>();
        this.f137u = Lists.newArrayList();
        this.f138v = new b();
        this.f139w = new c();
        this.f140x = new d();
        this.f141y = new e();
        this.f142z = new f();
        this.A = new g();
        this.f118b = context;
        this.f119c = powerKeeperManager;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void A() {
        this.f123g = this.f119c.isScreenOn();
        if (B) {
            Log.d(this.f117a, "updateForScreenChangedLocked, mScreenOn = " + this.f123g);
        }
        int size = this.f136t.size();
        for (int i2 = 0; i2 < size; i2++) {
            F(this.f136t.keyAt(i2));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void B(int i2) {
        if (B) {
            Log.v(this.f117a, "updateForUidRemovedLocked, uid=" + i2);
        }
        this.f121e.d(i2);
        this.f136t.remove(i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void C(int i2) {
        if (B) {
            Log.v(this.f117a, "updateForUserRemovedLocked, userId=" + i2);
        }
        SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
        int size = this.f136t.size();
        for (int i3 = 0; i3 < size; i3++) {
            if (UserHandle.getUserId(this.f136t.keyAt(i3)) == i2) {
                sparseBooleanArray.put(this.f136t.keyAt(i3), true);
            }
        }
        for (int i4 = 0; i4 < sparseBooleanArray.size(); i4++) {
            B(sparseBooleanArray.keyAt(i4));
        }
    }

    private void D(j jVar) {
        int i2 = jVar.f159a;
        int iY = y(jVar);
        if (iY != jVar.f161c) {
            if (B) {
                Log.d(this.f117a, "updateRuleForUidLocked, uid = " + i2 + " rule = " + iY);
            }
            jVar.f161c = iY;
            w(i2, iY);
        }
    }

    private void E(int i2, boolean z2) {
        if (v(i2)) {
            if (B) {
                Log.d(this.f117a, "updateStateForInteractiveUidChangedLocked: uid = " + i2 + " foreground = " + z2);
            }
            j jVarQ = q(i2);
            if (!this.f123g && !t(i2)) {
                int i3 = jVarQ.f160b;
                if (i3 == 0) {
                    this.f121e.d(i2);
                    i iVar = jVarQ.f162d;
                    int i4 = iVar.f156a;
                    if (i4 == 2) {
                        if (iVar.f157b > 0) {
                            jVarQ.d();
                            this.f121e.e(i2, jVarQ.f162d.f157b);
                        } else {
                            jVarQ.g();
                        }
                    } else if (i4 == 3 && s(i2)) {
                        jVarQ.d();
                    } else {
                        jVarQ.g();
                    }
                } else if (i3 == 1) {
                    i iVar2 = jVarQ.f162d;
                    int i5 = iVar2.f156a;
                    if (i5 == 2) {
                        if (iVar2.f157b < 0) {
                            this.f121e.d(i2);
                            jVarQ.g();
                        }
                    } else if (i5 != 3 || !s(i2)) {
                        jVarQ.g();
                    }
                }
            } else if (z2) {
                this.f121e.d(i2);
                jVarQ.h();
            } else {
                int i6 = jVarQ.f160b;
                if (i6 == 0) {
                    this.f121e.d(i2);
                    i iVar3 = jVarQ.f162d;
                    int i7 = iVar3.f156a;
                    if (i7 == 2) {
                        long j2 = iVar3.f157b;
                        if (j2 > 0) {
                            jVarQ.d();
                            this.f121e.e(i2, jVarQ.f162d.f157b);
                        } else if (j2 == 0) {
                            jVarQ.g();
                        } else {
                            jVarQ.d();
                        }
                    } else if (i7 == 3 && s(i2)) {
                        jVarQ.d();
                    } else {
                        jVarQ.g();
                    }
                } else if (i6 == 1 && jVarQ.f162d.f156a == 3 && !s(i2)) {
                    jVarQ.g();
                }
            }
            D(jVarQ);
        }
    }

    private void F(int i2) {
        if (v(i2)) {
            j jVarQ = q(i2);
            if (!this.f123g && !t(i2)) {
                int i3 = jVarQ.f160b;
                if (i3 == 0) {
                    this.f121e.d(i2);
                    i iVar = jVarQ.f162d;
                    int i4 = iVar.f156a;
                    if (i4 == 2) {
                        if (iVar.f157b > 0) {
                            jVarQ.d();
                            this.f121e.e(i2, jVarQ.f162d.f157b);
                        } else {
                            jVarQ.g();
                        }
                    } else if (i4 == 3 && s(i2)) {
                        jVarQ.d();
                    } else {
                        jVarQ.g();
                    }
                } else if (i3 == 1) {
                    i iVar2 = jVarQ.f162d;
                    int i5 = iVar2.f156a;
                    if (i5 == 2) {
                        if (iVar2.f157b < 0) {
                            this.f121e.d(i2);
                            jVarQ.g();
                        }
                    } else if (i5 != 3 || !s(i2)) {
                        jVarQ.g();
                    }
                }
            } else if (u(i2)) {
                this.f121e.d(i2);
                jVarQ.h();
            }
            D(jVarQ);
        }
    }

    private void G(int i2) {
        j jVarQ = q(i2);
        this.f121e.d(i2);
        if (jVarQ.f162d.f156a == 2 && jVarQ.f160b == 1) {
            if (B) {
                Log.d(this.f117a, "updateStateForTimeOutLocked, uid: " + i2);
            }
            jVarQ.g();
            D(jVarQ);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void H(int i2, boolean z2) {
        if (v(i2)) {
            j jVarQ = q(i2);
            if (jVarQ.f162d.f156a != 3 || jVarQ.f160b == 0) {
                return;
            }
            if (B) {
                Log.d(this.f117a, "updateStateForUidActiveChangedLocked, uid: " + i2 + " active: " + z2);
            }
            if (z2) {
                jVarQ.d();
            } else {
                jVarQ.g();
            }
            D(jVarQ);
        }
    }

    private void I(int i2) {
        if (v(i2)) {
            if (B) {
                Log.d(this.f117a, "updateStateForUidResetPolicyLocked, uid = " + i2);
            }
            boolean zU = u(i2);
            j jVarQ = q(i2);
            this.f121e.d(i2);
            if (!this.f123g && !t(i2)) {
                int i3 = jVarQ.f160b;
                if (i3 == 0 || i3 == 1 || (i3 == 2 && jVarQ.f161c == 0)) {
                    i iVar = jVarQ.f162d;
                    int i4 = iVar.f156a;
                    if (i4 == 2) {
                        if (iVar.f157b > 0) {
                            jVarQ.d();
                            this.f121e.e(i2, jVarQ.f162d.f157b);
                        } else {
                            jVarQ.g();
                        }
                    } else if (i4 == 3 && s(i2)) {
                        jVarQ.d();
                    } else {
                        jVarQ.g();
                    }
                } else if (i3 == 2) {
                    jVarQ.g();
                }
            } else if (zU) {
                jVarQ.h();
            } else {
                int i5 = jVarQ.f160b;
                if (i5 == 0 || i5 == 1 || (i5 == 2 && jVarQ.f161c == 0)) {
                    i iVar2 = jVarQ.f162d;
                    int i6 = iVar2.f156a;
                    if (i6 == 2) {
                        long j2 = iVar2.f157b;
                        if (j2 > 0) {
                            jVarQ.d();
                            this.f121e.e(i2, jVarQ.f162d.f157b);
                        } else if (j2 == 0) {
                            jVarQ.g();
                        } else {
                            jVarQ.d();
                        }
                    } else if (i6 == 3 && s(i2)) {
                        jVarQ.d();
                    } else {
                        jVarQ.g();
                    }
                } else if (i5 == 2) {
                    jVarQ.g();
                }
            }
            D(jVarQ);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void J(int i2, Bundle bundle) {
        boolean z2 = B;
        if (z2) {
            Log.d(this.f117a, "updateUidPolicyUnchecked, uid: " + i2 + " bundle: " + bundle);
        }
        synchronized (this.f130n) {
            try {
                if (!this.f131o) {
                    if (z2) {
                        Log.v(this.f117a, "updateUidPolicyUnchecked, still disabled");
                    }
                    return;
                }
                j jVarQ = q(i2);
                j jVarA = jVarQ.a();
                jVarQ.f(bundle);
                if (!jVarQ.c(jVarA)) {
                    I(i2);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public j q(int i2) {
        if (this.f136t.get(i2) == null) {
            this.f136t.put(i2, new j(i2));
        }
        return this.f136t.get(i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void r(int i2) {
        if (v(i2)) {
            synchronized (this.f130n) {
                E(i2, u(i2));
            }
        }
    }

    private boolean s(int i2) {
        return this.f122f.y(i2);
    }

    private boolean t(int i2) {
        if (this.f134r) {
            return this.f119c.getProcessObserver().isUidForegroundService(i2);
        }
        return false;
    }

    private boolean u(int i2) {
        boolean zIsUidForeground = !this.f133q ? this.f119c.isUidForeground(i2) : this.f119c.getProcessObserver().isUidForegroundPreTask(i2);
        return (zIsUidForeground || !this.f134r) ? zIsUidForeground : this.f119c.getProcessObserver().isUidForegroundService(i2);
    }

    private boolean v(int i2) {
        return UserHandle.isApp(i2);
    }

    private void w(int i2, int i3) {
        synchronized (this.f137u) {
            try {
                for (PowerKeeperInterface.c cVar : this.f137u) {
                    Handler handler = cVar.f249a;
                    if (handler == null) {
                        handler = BackgroundThread.getHandler();
                    }
                    handler.post(new h(cVar, i2, i3));
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean x(Message message) {
        if (message.what != 1) {
            return false;
        }
        int i2 = ((j) message.obj).f159a;
        synchronized (this.f130n) {
            try {
                if (this.f131o) {
                    G(i2);
                    return true;
                }
                if (B) {
                    Log.v(this.f117a, "still disabled");
                }
                return true;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void z(boolean z2) {
        this.f132p = z2;
        if (B) {
            Log.d(this.f117a, "updateForBatteryHeatChangedLocked, mHighTemp = " + this.f132p);
        }
        int size = this.f136t.size();
        for (int i2 = 0; i2 < size; i2++) {
            j jVarValueAt = this.f136t.valueAt(i2);
            if (jVarValueAt.f164f != null) {
                j jVarA = jVarValueAt.a();
                jVarValueAt.e();
                if (!jVarValueAt.c(jVarA)) {
                    I(this.f136t.keyAt(i2));
                }
            }
        }
    }

    @Override // com.miui.powerkeeper.PowerKeeperInterface.l
    public void disable() {
        synchronized (this.f130n) {
            try {
                if (!this.f131o) {
                    if (B) {
                        Log.v(this.f117a, "still disabled");
                    }
                    return;
                }
                Log.i(this.f117a, "disable");
                this.f131o = false;
                this.f136t.clear();
                EventsAggregator eventsAggregator = this.f119c.getEventsAggregator();
                FeedBackManager.getInstance(this.f118b).unregisterFeedBackListener(this.f129m);
                this.f119c.getUserCheckManager().K(this.f128l);
                this.f122f.L(this.f127k);
                this.f119c.unregisterAlarmListener(this.f126j);
                if (this.f133q) {
                    eventsAggregator.unregisterForegroundPreTaskChangeListener(this.f120d);
                } else {
                    eventsAggregator.unregisterForegroundActivityChangeListener(this.f120d);
                }
                if (this.f134r) {
                    eventsAggregator.unregisterForegroundServiceChangeListener(this.f120d);
                }
                this.f119c.unregisterScreenChangedListener(this.f125i);
                this.f119c.unregisterPackageChangedListener(this.f124h);
                this.f124h.a();
                this.f124h = null;
                this.f125i.a();
                this.f125i = null;
                this.f126j.a();
                this.f126j = null;
                this.f127k.a();
                this.f127k = null;
                this.f128l.a();
                this.f128l = null;
                this.f129m.a();
                this.f129m = null;
                this.f122f = null;
                this.f121e.b();
                this.f121e = null;
                this.f120d.removeCallbacksAndMessages(null);
                this.f120d = null;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // com.miui.powerkeeper.PowerKeeperInterface.l
    public void enable() {
        synchronized (this.f130n) {
            try {
                if (this.f131o) {
                    if (B) {
                        Log.v(this.f117a, "already enabled");
                    }
                    return;
                }
                Log.i(this.f117a, "enable");
                this.f131o = true;
                this.f136t.clear();
                this.f122f = this.f119c.getAppActiveChecker();
                Handler handler = new Handler(PowerKeeperManager.getThread().getLooper(), new k());
                this.f120d = handler;
                this.f121e = new l(handler);
                A();
                z(FeedBackManager.getInstance(this.f118b).isBatteryHeat());
                this.f124h = new PowerKeeperInterface.d0(this.f120d, this.f139w);
                this.f125i = new PowerKeeperInterface.f0(this.f120d, this.f138v);
                EventsAggregator eventsAggregator = this.f119c.getEventsAggregator();
                if (this.f133q) {
                    eventsAggregator.registerForegroundPreTaskChangeListener(this.f120d);
                } else {
                    eventsAggregator.registerForegroundActivityChangeListener(this.f120d);
                }
                if (this.f134r) {
                    eventsAggregator.registerForegroundServiceChangeListener(this.f120d);
                }
                this.f126j = new PowerKeeperInterface.a(this.f120d, this.f140x);
                this.f127k = new PowerKeeperInterface.b(this.f120d, this.f141y);
                this.f128l = new PowerKeeperInterface.g0(this.f120d, this.f142z);
                this.f129m = new PowerKeeperInterface.e(this.f120d, this.A, null, null, null);
                this.f119c.registerPackageChangedListener(this.f124h);
                this.f119c.registerScreenChangedListener(this.f125i);
                this.f119c.registerAlarmListener(this.f126j);
                this.f122f.I(this.f127k);
                this.f119c.getUserCheckManager().J(this.f128l);
                FeedBackManager.getInstance(this.f118b).registerFeedBackListener(this.f129m);
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // com.miui.powerkeeper.PowerKeeperInterface.l
    public int getAppRule(int i2) {
        int i3;
        synchronized (this.f130n) {
            i3 = q(i2).f161c;
        }
        return i3;
    }

    @Override // com.miui.powerkeeper.PowerKeeperInterface.l
    public SparseIntArray getAppsRule(int[] iArr) {
        SparseIntArray sparseIntArray;
        synchronized (this.f130n) {
            try {
                sparseIntArray = new SparseIntArray(iArr.length);
                for (int i2 : iArr) {
                    sparseIntArray.put(i2, q(i2).f161c);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return sparseIntArray;
    }

    @Override // com.miui.powerkeeper.PowerKeeperInterface.l
    public void registerAppRuleChangeListener(PowerKeeperInterface.c cVar) {
        synchronized (this.f137u) {
            try {
                if (!this.f137u.contains(cVar)) {
                    this.f137u.add(cVar);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // com.miui.powerkeeper.PowerKeeperInterface.l
    public void setUidPolicy(int i2, Bundle bundle) {
        if (!UserHandle.isApp(i2)) {
            if (B) {
                Log.e(this.f117a, "cannot apply policy to UID " + i2);
                return;
            }
            return;
        }
        synchronized (this.f130n) {
            try {
                if (this.f131o) {
                    this.f120d.post(new a(i2, bundle));
                } else {
                    if (B) {
                        Log.v(this.f117a, "still disabled");
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // com.miui.powerkeeper.PowerKeeperInterface.l
    public void unregisterAppRuleChangeListener(PowerKeeperInterface.c cVar) {
        synchronized (this.f137u) {
            try {
                if (this.f137u.contains(cVar)) {
                    this.f137u.remove(cVar);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    protected int y(j jVar) {
        int i2 = jVar.f162d.f156a;
        if (i2 == 0) {
            return 0;
        }
        return (i2 == 1 || jVar.f160b == 2) ? 1 : 0;
    }

    class i {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        int f156a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        long f157b;

        i() {
            b();
        }

        i a() {
            i iVar = AppRuleChecker.this.new i();
            iVar.f156a = this.f156a;
            iVar.f157b = this.f157b;
            return iVar;
        }

        void b() {
            if (AppRuleChecker.this.f135s) {
                this.f156a = 1;
            } else {
                this.f156a = 0;
            }
            this.f157b = 0L;
        }

        boolean c(i iVar) {
            if (this == iVar) {
                return true;
            }
            return this.f156a == iVar.f156a && this.f157b == iVar.f157b;
        }

        void d(int i2, long j2) {
            this.f156a = i2;
            this.f157b = j2;
        }

        i(int i2, long j2) {
            this.f156a = i2;
            this.f157b = j2;
        }
    }

    public AppRuleChecker(Context context, PowerKeeperManager powerKeeperManager, String str) {
        this(context, powerKeeperManager);
        this.f117a = str;
    }

    public AppRuleChecker(Context context, PowerKeeperManager powerKeeperManager, String str, Bundle bundle) {
        this(context, powerKeeperManager, str);
        this.f133q = bundle.getBoolean(HideModeStateMachineConfig.KEY_REGARD_PRETASK, false);
        this.f134r = bundle.getBoolean(HideModeStateMachineConfig.KEY_REGARD_FOREGROUND_SERVICE, false);
        this.f135s = bundle.getBoolean("SET_WHITE_LIST", false);
    }

    class c implements PowerKeeperInterface.x {
        c() {
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.x
        public void onUidRemoved(int i2, int i3) {
            synchronized (AppRuleChecker.this.f130n) {
                try {
                    if (AppRuleChecker.this.f131o) {
                        AppRuleChecker.this.B(i3);
                    } else {
                        if (AppRuleChecker.B) {
                            Log.v(AppRuleChecker.this.f117a, "still disabled");
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
}
