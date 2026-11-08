package com.miui.powerkeeper.perfengine;

import android.util.Log;
import com.miui.powerkeeper.utils.PowerLog;
import dalvik.system.PathClassLoader;
import java.lang.reflect.Method;

/* JADX INFO: compiled from: XRBoost.java */
/* JADX INFO: loaded from: classes.dex */
public class i {

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public static int f543g = 60000 * 60;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private Object f544a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private Class<?> f545b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private Method f546c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private Method f547d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private boolean f548e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private volatile boolean f549f;

    /* JADX INFO: compiled from: XRBoost.java */
    private static class a {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private static final i f550a = new i();
    }

    public static i a() {
        return a.f550a;
    }

    private void d() {
        try {
            Class<?> clsLoadClass = new PathClassLoader("/system_ext/framework/PerfFlingerClient.jar", ClassLoader.getSystemClassLoader()).loadClass("com.xring.perf.PerfFlingerClient");
            this.f545b = clsLoadClass;
            if (clsLoadClass != null) {
                this.f544a = clsLoadClass.newInstance();
            }
            Class<?> cls = Integer.TYPE;
            this.f546c = this.f545b.getMethod("perfLockAcquire", cls, cls, int[].class);
            this.f547d = this.f545b.getMethod("perfRelease", cls);
            if (PowerLog.V) {
                PowerLog.v("PeGameController_XRBoost", "preloadXRBoostFrameworkNew succeed.");
            }
            this.f548e = true;
        } catch (Exception e2) {
            Log.e("PeGameController_XRBoost", "preloadBoostFrameworkNew class PerfFlinger failed, e:" + e2.getMessage());
        }
    }

    public synchronized int b(int i2, int i3, int[] iArr) {
        int i4 = 0;
        if (!this.f548e) {
            Log.i("PeGameController_XRBoost", "perfLockAcquire !mIsPreloadDone return");
            return 0;
        }
        if (!this.f549f) {
            Log.i("PeGameController_XRBoost", "perfLockAcquire !mIsReleased return");
            return 0;
        }
        try {
            if (this.f546c != null) {
                if (PowerLog.V) {
                    PowerLog.v("PeGameController_XRBoost", "perfLockAcquire");
                }
                Integer num = (Integer) this.f546c.invoke(this.f544a, Integer.valueOf(i2), Integer.valueOf(i3), iArr);
                int iIntValue = num.intValue();
                try {
                    if (iIntValue != -1) {
                        if (PowerLog.V) {
                            PowerLog.v("PeGameController_XRBoost", "perfLockAcquire ret >=0, set mIsReleased = false, ret=", num);
                        }
                        this.f549f = false;
                    } else if (PowerLog.V) {
                        PowerLog.v("PeGameController_XRBoost", "perfLockAcquire ret <0, not set mIsReleased = false, ret=", num);
                    }
                    i4 = iIntValue;
                } catch (IllegalAccessException e2) {
                    e = e2;
                    i4 = iIntValue;
                    Log.e("PeGameController_XRBoost", "perfLockAcquire e:" + e.getMessage());
                } catch (Exception e3) {
                    e = e3;
                    i4 = iIntValue;
                    Log.e("PeGameController_XRBoost", "perfLockAcquire e:" + e.getMessage());
                }
            }
        } catch (IllegalAccessException e4) {
            e = e4;
        } catch (Exception e5) {
            e = e5;
        }
        return i4;
    }

    public synchronized boolean c(int i2) {
        try {
            boolean zBooleanValue = false;
            if (!this.f548e) {
                Log.i("PeGameController_XRBoost", "perfLockRelease !mIsPreloadDone return");
                return false;
            }
            if (this.f549f) {
                Log.i("PeGameController_XRBoost", "perfLockRelease mIsReleased return");
                return false;
            }
            try {
                if (this.f547d != null) {
                    if (PowerLog.V) {
                        PowerLog.v("PeGameController_XRBoost", "perfLockRelease");
                    }
                    zBooleanValue = ((Boolean) this.f547d.invoke(this.f544a, Integer.valueOf(i2))).booleanValue();
                    if (zBooleanValue) {
                        this.f549f = true;
                        if (PowerLog.V) {
                            PowerLog.v("PeGameController_XRBoost", "perfLockRelease ret = true, set mIsReleased = true");
                        }
                    } else if (PowerLog.V) {
                        PowerLog.v("PeGameController_XRBoost", "perfLockRelease ret = false, not set mIsReleased = true");
                    }
                }
            } catch (IllegalAccessException e2) {
                Log.e("PeGameController_XRBoost", "perfLockRelease e:" + e2.getMessage());
            } catch (Exception e3) {
                Log.e("PeGameController_XRBoost", "perfLockRelease e:" + e3.getMessage());
            }
            return zBooleanValue;
        } catch (Throwable th) {
            throw th;
        }
    }

    private i() {
        this.f544a = null;
        this.f545b = null;
        this.f546c = null;
        this.f547d = null;
        this.f548e = false;
        this.f549f = true;
        this.f549f = true;
        d();
    }
}
