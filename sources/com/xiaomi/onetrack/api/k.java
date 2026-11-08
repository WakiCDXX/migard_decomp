package com.xiaomi.onetrack.api;

import android.text.TextUtils;

/* JADX INFO: loaded from: classes.dex */
class k implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ j f1117a;

    k(j jVar) {
        this.f1117a = jVar;
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            com.xiaomi.onetrack.c.a.a();
            a.a().a(this.f1117a.f1109b);
            if (com.xiaomi.onetrack.util.aa.B() == 0) {
                com.xiaomi.onetrack.util.aa.n(System.currentTimeMillis());
            }
            if (!TextUtils.isEmpty(this.f1117a.f1112f.getInstanceId())) {
                com.xiaomi.onetrack.util.o.a().a(this.f1117a.f1112f.getInstanceId());
            }
            this.f1117a.k();
            com.xiaomi.onetrack.util.d.a();
            com.xiaomi.onetrack.c.i.c(false);
        } catch (Throwable th) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "init WorkerExecutor execute throwable:" + th.getMessage());
        }
    }
}
