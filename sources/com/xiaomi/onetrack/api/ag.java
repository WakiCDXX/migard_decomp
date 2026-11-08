package com.xiaomi.onetrack.api;

import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
class ag implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ Map f971a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ j f972b;

    ag(j jVar, Map map) {
        this.f972b = jVar;
        this.f971a = map;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        try {
            this.f972b.f1109b.a(d.f1046c, e.a(com.xiaomi.onetrack.util.r.a((Map<String, Object>) this.f971a, false), this.f972b.f1112f, this.f972b.f1114h, this.f972b.f(d.f1046c), this.f972b.f1115i, this.f972b.f1116j));
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "setUserProfile map error:" + e2.toString());
        }
    }
}
