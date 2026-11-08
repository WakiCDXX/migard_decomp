package com.xiaomi.onetrack.api;

import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
class ae implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ String f962a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ j f963b;

    ae(j jVar, String str) {
        this.f963b = jVar;
        this.f962a = str;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        try {
            JSONArray jSONArray = new JSONArray(this.f962a);
            for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                JSONObject jSONObjectOptJSONObject = jSONArray.optJSONObject(i2);
                if (jSONObjectOptJSONObject != null && jSONObjectOptJSONObject.has(e.f1071b) && jSONObjectOptJSONObject.has(e.f1070a)) {
                    f fVar = new f(jSONObjectOptJSONObject.optJSONObject(e.f1071b));
                    if (!this.f963b.e(fVar.a())) {
                        this.f963b.f1109b.a(fVar.a(), e.a(fVar, jSONObjectOptJSONObject.optJSONObject(e.f1070a), this.f963b.f1112f, this.f963b.f1114h, this.f963b.f(fVar.a()), this.f963b.f1115i, this.f963b.f1116j));
                    }
                } else {
                    com.xiaomi.onetrack.util.p.a("OneTrackImp", "h5 json is empty or has no \"H\" or \"B\" ");
                }
            }
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "trackEventFromH5 error: " + e2.toString());
        }
    }
}
