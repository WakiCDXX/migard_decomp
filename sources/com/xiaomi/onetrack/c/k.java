package com.xiaomi.onetrack.c;

import java.util.List;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
final class k implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ com.xiaomi.onetrack.api.g f1286a;

    k(com.xiaomi.onetrack.api.g gVar) {
        this.f1286a = gVar;
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            List<JSONObject> listC = i.c();
            if (listC != null && listC.size() > 0) {
                for (JSONObject jSONObject : listC) {
                    this.f1286a.a(jSONObject.optString("eventName"), jSONObject.optString("data"));
                }
            }
            i.c(true);
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b("NetworkAccessManager", "cta event error: " + e2.toString());
        }
        boolean unused = i.f1283l = false;
    }
}
