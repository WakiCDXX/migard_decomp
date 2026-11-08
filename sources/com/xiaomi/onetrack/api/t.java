package com.xiaomi.onetrack.api;

import android.text.TextUtils;
import java.util.Map;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
class t implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ Map f1141a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ j f1142b;

    t(j jVar, Map map) {
        this.f1142b = jVar;
        this.f1141a = map;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        try {
            JSONObject jSONObjectA = com.xiaomi.onetrack.util.r.a((Map<String, Object>) this.f1141a, true);
            String strA = com.xiaomi.onetrack.util.k.a(com.xiaomi.onetrack.util.r.a(this.f1142b.f1112f));
            com.xiaomi.onetrack.util.k.a(com.xiaomi.onetrack.util.r.a(this.f1142b.f1112f), com.xiaomi.onetrack.util.r.a(jSONObjectA, !TextUtils.isEmpty(strA) ? new JSONObject(strA) : null).toString());
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", " " + e2.toString());
        }
    }
}
