package com.xiaomi.onetrack.api;

/* JADX INFO: loaded from: classes.dex */
class q implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ String f1133a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ boolean f1134b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    final /* synthetic */ j f1135c;

    q(j jVar, String str, boolean z2) {
        this.f1135c = jVar;
        this.f1133a = str;
        this.f1134b = z2;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        try {
            if (!this.f1135c.f1112f.isAutoTrackActivityAction()) {
                com.xiaomi.onetrack.util.p.a("OneTrackImp", "config.autoTrackActivityAction is false, ignore onetrack_pa resume event");
                return;
            }
            this.f1135c.f1109b.a(d.f1050g, e.a(this.f1133a, d.f1050g, this.f1135c.f1112f, this.f1135c.f1114h, this.f1135c.f(d.f1050g), this.f1134b, this.f1135c.f1115i, this.f1135c.f1116j));
            if (com.xiaomi.onetrack.util.p.f1665a) {
                com.xiaomi.onetrack.util.p.a("OneTrackImp", "trackPageStartAuto");
            }
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "auto trackPageStartAuto error: " + e2.toString());
        }
    }
}
