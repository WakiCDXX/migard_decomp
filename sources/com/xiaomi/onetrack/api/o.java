package com.xiaomi.onetrack.api;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.SystemClock;
import com.xiaomi.onetrack.util.DeviceUtil;

/* JADX INFO: loaded from: classes.dex */
class o implements Application.ActivityLifecycleCallbacks {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ j f1126a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private int f1127b = 0;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private int f1128c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private long f1129d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private boolean f1130e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private boolean f1131f;

    o(j jVar) {
        this.f1126a = jVar;
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityPaused(Activity activity) {
        this.f1126a.a(activity.getClass().getName(), this.f1128c == System.identityHashCode(activity) ? SystemClock.elapsedRealtime() - this.f1129d : 0L);
        if (com.xiaomi.onetrack.util.p.f1665a) {
            com.xiaomi.onetrack.util.p.a("OneTrackImp", "onActivityPaused:" + activity.getLocalClassName());
        }
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityResumed(Activity activity) {
        this.f1126a.d(this.f1131f);
        this.f1128c = System.identityHashCode(activity);
        this.f1129d = SystemClock.elapsedRealtime();
        this.f1126a.a(activity.getClass().getName(), this.f1130e);
        if (com.xiaomi.onetrack.util.p.f1665a) {
            com.xiaomi.onetrack.util.p.a("OneTrackImp", "onActivityResumed:" + activity.getLocalClassName() + " isAppStart:" + this.f1130e);
        }
        this.f1130e = false;
        this.f1126a.d();
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityStarted(Activity activity) {
        if (this.f1127b == 0) {
            this.f1126a.f1109b.a(1);
            this.f1130e = true;
            this.f1131f = false;
            DeviceUtil.a();
        } else {
            this.f1130e = false;
        }
        this.f1127b++;
        com.xiaomi.onetrack.util.p.a("OneTrackImp", "onActivityStarted: " + activity.getLocalClassName());
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityStopped(Activity activity) {
        int i2 = this.f1127b - 1;
        this.f1127b = i2;
        if (i2 == 0) {
            this.f1126a.f1109b.a(2);
            this.f1126a.j();
            this.f1131f = true;
            this.f1130e = false;
        } else {
            this.f1131f = false;
        }
        this.f1126a.d(this.f1131f);
        com.xiaomi.onetrack.util.p.a("OneTrackImp", "onActivityStopped: " + activity.getLocalClassName());
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityDestroyed(Activity activity) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityCreated(Activity activity, Bundle bundle) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }
}
