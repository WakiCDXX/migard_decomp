package com.xiaomi.onetrack.api;

/* JADX INFO: loaded from: classes.dex */
class af implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ String f964a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ String f965b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    final /* synthetic */ String f966c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    final /* synthetic */ String f967d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    final /* synthetic */ String f968e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    final /* synthetic */ long f969f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    final /* synthetic */ j f970g;

    af(j jVar, String str, String str2, String str3, String str4, String str5, long j2) {
        this.f970g = jVar;
        this.f964a = str;
        this.f965b = str2;
        this.f966c = str3;
        this.f967d = str4;
        this.f968e = str5;
        this.f969f = j2;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        try {
            this.f970g.f1109b.a("onetrack_bug_report", e.a(this.f964a, this.f965b, this.f966c, this.f967d, this.f968e, this.f969f, this.f970g.f1112f, this.f970g.f1114h, this.f970g.f("onetrack_bug_report"), this.f970g.f1115i, this.f970g.f1116j));
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "trackException error: " + e2.toString());
        }
    }
}
