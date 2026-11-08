package com.xiaomi.onetrack.api;

import java.util.Map;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
class l implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ Map f1118a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ j f1119b;

    l(j jVar, Map map) {
        this.f1119b = jVar;
        this.f1118a = map;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        try {
            this.f1119b.f1109b.a(d.f1047d, e.b(new JSONObject(this.f1118a), this.f1119b.f1112f, this.f1119b.f1114h, this.f1119b.f(d.f1047d), this.f1119b.f1115i, this.f1119b.f1116j));
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "userProfileIncrement map error:" + e2.toString());
        }
    }
}
