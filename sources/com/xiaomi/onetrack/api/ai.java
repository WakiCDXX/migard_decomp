package com.xiaomi.onetrack.api;

import com.xiaomi.onetrack.OneTrack;
import java.util.Map;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
class ai implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ String f976a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ OneTrack.UserIdType f977b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    final /* synthetic */ boolean f978c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    final /* synthetic */ Map f979d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    final /* synthetic */ j f980e;

    ai(j jVar, String str, OneTrack.UserIdType userIdType, boolean z2, Map map) {
        this.f980e = jVar;
        this.f976a = str;
        this.f977b = userIdType;
        this.f978c = z2;
        this.f979d = map;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        try {
            com.xiaomi.onetrack.util.aa.g(this.f976a);
            com.xiaomi.onetrack.util.aa.h(this.f977b.getUserIdType());
            if (this.f978c) {
                return;
            }
            JSONObject jSONObjectA = com.xiaomi.onetrack.util.r.a((Map<String, Object>) this.f979d, false);
            if (!this.f980e.f1116j) {
                jSONObjectA.put("uid", this.f976a);
                jSONObjectA.put("uid_type", this.f977b.getUserIdType());
            }
            this.f980e.f1109b.a("ot_login", e.c(jSONObjectA, this.f980e.f1112f, this.f980e.f1114h, this.f980e.f("ot_login"), this.f980e.f1115i, this.f980e.f1116j));
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "login error:" + e2.toString());
        }
    }
}
