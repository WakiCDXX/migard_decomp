package com.xiaomi.onetrack.api;

/* JADX INFO: loaded from: classes.dex */
class r implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ String f1136a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ long f1137b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    final /* synthetic */ j f1138c;

    r(j jVar, String str, long j2) {
        this.f1138c = jVar;
        this.f1136a = str;
        this.f1137b = j2;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        try {
            if (!this.f1138c.f1112f.isAutoTrackActivityAction()) {
                com.xiaomi.onetrack.util.p.a("OneTrackImp", "config.autoTrackActivityAction is false, ignore onetrack_pa pause event");
            } else {
                com.xiaomi.onetrack.util.aa.i(e.a(this.f1136a, d.f1050g, this.f1137b, this.f1138c.f1112f, this.f1138c.f1114h, this.f1138c.f(d.f1050g), this.f1138c.f1115i, this.f1138c.f1116j));
            }
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "savePageEndData error:" + e2.toString());
        }
    }
}
