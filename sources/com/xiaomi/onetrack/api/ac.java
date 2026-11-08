package com.xiaomi.onetrack.api;

import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
class ac implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ String f955a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ Map f956b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    final /* synthetic */ j f957c;

    ac(j jVar, String str, Map map) {
        this.f957c = jVar;
        this.f955a = str;
        this.f956b = map;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        try {
            if (this.f957c.d(this.f955a)) {
                return;
            }
            this.f957c.f1109b.a(this.f955a, e.a(this.f955a, com.xiaomi.onetrack.util.r.a((Map<String, Object>) this.f956b, true), this.f957c.f1112f, this.f957c.f1114h, this.f957c.f(this.f955a), this.f957c.f1115i, this.f957c.f1116j));
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "track map error: " + e2.toString());
        }
    }
}
