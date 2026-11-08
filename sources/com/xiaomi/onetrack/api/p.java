package com.xiaomi.onetrack.api;

/* JADX INFO: loaded from: classes.dex */
class p implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ j f1132a;

    p(j jVar) {
        this.f1132a = jVar;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        try {
            if (com.xiaomi.onetrack.util.ac.d(com.xiaomi.onetrack.util.aa.t())) {
                return;
            }
            com.xiaomi.onetrack.util.aa.m(System.currentTimeMillis());
            this.f1132a.f1109b.a("onetrack_dau", e.a(this.f1132a.f1112f, this.f1132a.f1114h, this.f1132a.f("onetrack_dau"), this.f1132a.f1115i, this.f1132a.f1116j));
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "trackDau error  e:" + e2.toString());
        }
    }
}
