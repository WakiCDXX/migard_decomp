package com.xiaomi.onetrack.c;

import com.xiaomi.onetrack.api.an;
import java.util.List;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
class n implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ String f1299a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ l f1300b;

    n(l lVar, String str) {
        this.f1300b = lVar;
        this.f1299a = str;
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            List<JSONObject> listB = l.b(this.f1299a);
            if (listB != null && listB.size() > 0) {
                for (JSONObject jSONObject : listB) {
                    an.a().a(this.f1299a, jSONObject.optString("eventName"), jSONObject.optString("data"));
                }
            }
            this.f1300b.a(this.f1299a);
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b("SystemImpCacheManager", "trackSystemImpCache event error: " + e2.toString());
        }
    }
}
