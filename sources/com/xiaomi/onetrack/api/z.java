package com.xiaomi.onetrack.api;

/* JADX INFO: loaded from: classes.dex */
class z implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ j f1154a;

    z(j jVar) {
        this.f1154a = jVar;
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            this.f1154a.f1109b.a(d.f1052i, e.a(this.f1154a.f1112f, this.f1154a.f1114h, this.f1154a.f1115i, this.f1154a.f1116j));
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "cta event error: " + e2.toString());
        }
    }
}
