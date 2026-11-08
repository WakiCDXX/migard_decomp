package com.xiaomi.onetrack.api;

import java.util.Map;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
class n implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ boolean f1123a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ Map f1124b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    final /* synthetic */ j f1125c;

    n(j jVar, boolean z2, Map map) {
        this.f1125c = jVar;
        this.f1123a = z2;
        this.f1124b = map;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        try {
            if (!this.f1123a) {
                JSONObject jSONObjectA = com.xiaomi.onetrack.util.r.a((Map<String, Object>) this.f1124b, false);
                if (!this.f1125c.f1116j) {
                    String strU = com.xiaomi.onetrack.util.aa.u();
                    String strW = com.xiaomi.onetrack.util.aa.w();
                    jSONObjectA.put("uid", strU);
                    jSONObjectA.put("uid_type", strW);
                }
                this.f1125c.f1109b.a("ot_logout", e.d(jSONObjectA, this.f1125c.f1112f, this.f1125c.f1114h, this.f1125c.f("ot_logout"), this.f1125c.f1115i, this.f1125c.f1116j));
            }
            com.xiaomi.onetrack.util.aa.v();
            com.xiaomi.onetrack.util.aa.x();
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "logout error:" + e2.toString());
        }
    }
}
