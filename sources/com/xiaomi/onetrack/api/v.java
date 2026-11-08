package com.xiaomi.onetrack.api;

import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
class v implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ String f1144a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ Map f1145b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    final /* synthetic */ String f1146c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    final /* synthetic */ j f1147d;

    v(j jVar, String str, Map map, String str2) {
        this.f1147d = jVar;
        this.f1144a = str;
        this.f1145b = map;
        this.f1146c = str2;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        try {
            if (this.f1147d.d(this.f1144a)) {
                return;
            }
            this.f1147d.f1109b.a(this.f1144a, e.a(this.f1146c, this.f1144a, com.xiaomi.onetrack.util.r.a((Map<String, Object>) this.f1145b, true), this.f1147d.f1112f, this.f1147d.f1114h, this.f1147d.f(this.f1144a), this.f1147d.f1115i, this.f1147d.f1116j));
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "track json error:" + e2.toString());
        }
    }
}
