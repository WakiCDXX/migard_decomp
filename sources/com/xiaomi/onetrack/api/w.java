package com.xiaomi.onetrack.api;

import android.text.TextUtils;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
class w implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ String f1148a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ j f1149b;

    w(j jVar, String str) {
        this.f1149b = jVar;
        this.f1148a = str;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        try {
            String strA = com.xiaomi.onetrack.util.k.a(com.xiaomi.onetrack.util.r.a(this.f1149b.f1112f));
            if (TextUtils.isEmpty(strA)) {
                return;
            }
            JSONObject jSONObject = new JSONObject(strA);
            jSONObject.remove(this.f1148a);
            com.xiaomi.onetrack.util.k.a(com.xiaomi.onetrack.util.r.a(this.f1149b.f1112f), jSONObject.toString());
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "removeCommonProperty error:" + e2.toString());
        }
    }
}
