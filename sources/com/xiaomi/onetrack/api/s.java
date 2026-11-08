package com.xiaomi.onetrack.api;

import android.text.TextUtils;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
class s implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ boolean f1139a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ j f1140b;

    s(j jVar, boolean z2) {
        this.f1140b = jVar;
        this.f1139a = z2;
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            String strZ = com.xiaomi.onetrack.util.aa.z();
            if (TextUtils.isEmpty(strZ)) {
                return;
            }
            JSONObject jSONObject = new JSONObject(strZ);
            this.f1140b.f1109b.a(d.f1050g, jSONObject.put(e.f1070a, jSONObject.optJSONObject(e.f1070a).put(d.f1064u, this.f1139a)).toString());
            if (com.xiaomi.onetrack.util.p.f1665a) {
                com.xiaomi.onetrack.util.p.a("OneTrackImp", "trackPageEndAuto");
            }
            com.xiaomi.onetrack.util.aa.i("");
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "trackPageEndAuto error:" + e2.toString());
        }
    }
}
