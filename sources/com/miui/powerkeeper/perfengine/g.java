package com.miui.powerkeeper.perfengine;

import android.util.Log;
import com.miui.powerkeeper.utils.PowerLog;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/* JADX INFO: compiled from: QcomBoost.java */
/* JADX INFO: loaded from: classes.dex */
public class g {

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    public static int f531k = 60000 * 60;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private Object f532a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private Object f533b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private Class<?> f534c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private Method f535d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private Method f536e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private Method f537f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private Method f538g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private boolean f539h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private volatile boolean f540i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private int f541j;

    /* JADX INFO: compiled from: QcomBoost.java */
    private static class a {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private static final g f542a = new g();
    }

    public static g c() {
        return a.f542a;
    }

    private void g() {
        try {
            Class<?> cls = Class.forName("android.util.BoostFramework");
            this.f534c = cls;
            Class[] clsArr = new Class[0];
            Constructor<?> constructor = cls.getConstructor(null);
            if (constructor != null) {
                this.f532a = constructor.newInstance(null);
                this.f533b = constructor.newInstance(null);
            }
            Class<?> cls2 = Integer.TYPE;
            this.f536e = this.f534c.getMethod("perfLockAcquire", cls2, int[].class);
            Class[] clsArr2 = new Class[0];
            this.f535d = this.f534c.getMethod("perfLockRelease", null);
            this.f537f = this.f534c.getMethod("perfHint", cls2, String.class, cls2, cls2);
            this.f538g = this.f534c.getMethod("perfLockReleaseHandler", cls2);
            if (PowerLog.V) {
                PowerLog.v("PeGameController_QcomBoost", "preload BoostFramework succeed.");
            }
            this.f539h = true;
        } catch (Exception unused) {
            Log.e("PeGameController_QcomBoost", "preload class android.util.BoostFramework failed");
        }
    }

    public synchronized void a(boolean z2, int i2) {
        try {
            if (PowerLog.V) {
                PowerLog.v("PeGameController_QcomBoost", "QcomBoost boostAnimation isBoost:" + z2);
            }
            if (z2) {
                if (PowerLog.V) {
                    PowerLog.v("PeGameController_QcomBoost", "QcomBoost boostAnimation boost");
                }
                this.f541j = d(4227, i2, -1);
            } else if (this.f541j > 0 && this.f538g != null && this.f533b != null) {
                if (PowerLog.V) {
                    PowerLog.v("PeGameController_QcomBoost", "QcomBoost boostAnimation release");
                }
                try {
                    this.f538g.invoke(this.f533b, Integer.valueOf(this.f541j));
                    this.f541j = -1;
                } catch (Exception e2) {
                    Log.e("PeGameController_QcomBoost", "boostAnimation e:" + e2.getMessage());
                }
            }
            if (PowerLog.V) {
                PowerLog.v("PeGameController_QcomBoost", "QcomBoost boostAnimation End, mPerfHintHandler:" + this.f541j);
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    public synchronized int b(int i2) {
        int iD;
        try {
            if (PowerLog.V) {
                PowerLog.v("PeGameController_QcomBoost", "QcomBoost boostAnimationRetHandler timeoutMs:" + i2);
            }
            iD = d(4227, i2, -1);
            if (PowerLog.V) {
                PowerLog.v("PeGameController_QcomBoost", "QcomBoost boostAnimationRetHandler End, handler:" + iD);
            }
        } catch (Throwable th) {
            throw th;
        }
        return iD;
    }

    public synchronized int d(int i2, int i3, int i4) {
        int iIntValue;
        Object obj;
        try {
            if (PowerLog.V) {
                PowerLog.v("PeGameController_QcomBoost", "QcomBoost perfHint id:" + i2 + ", dura:" + i3 + ", tpid:" + i4);
            }
            iIntValue = -1;
            try {
                Method method = this.f537f;
                if (method != null && (obj = this.f533b) != null) {
                    iIntValue = ((Integer) method.invoke(obj, Integer.valueOf(i2), "PowerKeeper_QcomBoost", Integer.valueOf(i3), Integer.valueOf(i4))).intValue();
                }
            } catch (IllegalAccessException e2) {
                Log.e("PeGameController_QcomBoost", "perfHint e:" + e2.getMessage());
            } catch (Exception e3) {
                Log.e("PeGameController_QcomBoost", "perfHint e:" + e3.getMessage());
            }
        } catch (Throwable th) {
            throw th;
        }
        return iIntValue;
    }

    public synchronized void e(int i2, int[] iArr) {
        if (!this.f539h) {
            Log.i("PeGameController_QcomBoost", "perfLockAcquire !mIsPreloadDone return");
            return;
        }
        if (!this.f540i) {
            Log.i("PeGameController_QcomBoost", "perfLockAcquire !mIsReleased return");
            return;
        }
        try {
            if (this.f536e != null) {
                if (PowerLog.V) {
                    PowerLog.v("PeGameController_QcomBoost", "perfLockAcquire");
                }
                this.f536e.invoke(this.f532a, Integer.valueOf(i2), iArr);
                this.f540i = false;
            }
        } catch (IllegalAccessException e2) {
            Log.e("PeGameController_QcomBoost", "perfLockAcquire e:" + e2.getMessage());
        } catch (Exception e3) {
            Log.e("PeGameController_QcomBoost", "perfLockAcquire e:" + e3.getMessage());
        }
    }

    public synchronized void f() {
        if (!this.f539h) {
            Log.i("PeGameController_QcomBoost", "perfLockRelease !mIsPreloadDone return");
            return;
        }
        if (this.f540i) {
            Log.i("PeGameController_QcomBoost", "perfLockRelease mIsReleased return");
            return;
        }
        try {
            if (this.f535d != null) {
                if (PowerLog.V) {
                    PowerLog.v("PeGameController_QcomBoost", "perfLockRelease");
                }
                this.f535d.invoke(this.f532a, null);
                this.f540i = true;
            }
        } catch (IllegalAccessException e2) {
            Log.e("PeGameController_QcomBoost", "perfLockRelease e:" + e2.getMessage());
        } catch (Exception e3) {
            Log.e("PeGameController_QcomBoost", "perfLockRelease e:" + e3.getMessage());
        }
    }

    public synchronized void h(int i2) {
        try {
            if (PowerLog.V) {
                PowerLog.v("PeGameController_QcomBoost", "QcomBoost releaseAnimationRetHandler handler:" + i2);
            }
            if (i2 > 0 && this.f538g != null && this.f533b != null) {
                if (PowerLog.V) {
                    PowerLog.v("PeGameController_QcomBoost", "QcomBoost releaseAnimationRetHandler release");
                }
                try {
                    this.f538g.invoke(this.f533b, Integer.valueOf(i2));
                } catch (Exception e2) {
                    Log.e("PeGameController_QcomBoost", "releaseAnimationRetHandler e:" + e2.getMessage());
                }
            }
            if (PowerLog.V) {
                PowerLog.v("PeGameController_QcomBoost", "QcomBoost releaseAnimationRetHandler End");
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    private g() {
        this.f532a = null;
        this.f533b = null;
        this.f534c = null;
        this.f535d = null;
        this.f536e = null;
        this.f537f = null;
        this.f538g = null;
        this.f539h = false;
        this.f540i = true;
        this.f541j = -1;
        this.f540i = true;
        g();
    }
}
