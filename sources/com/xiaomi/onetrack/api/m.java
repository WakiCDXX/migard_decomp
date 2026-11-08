package com.xiaomi.onetrack.api;

import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
class m implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ String f1120a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ Number f1121b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    final /* synthetic */ j f1122c;

    m(j jVar, String str, Number number) {
        this.f1122c = jVar;
        this.f1120a = str;
        this.f1121b = number;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        try {
            this.f1122c.f1109b.a(d.f1047d, e.b(new JSONObject().put(this.f1120a, this.f1121b), this.f1122c.f1112f, this.f1122c.f1114h, this.f1122c.f(d.f1047d), this.f1122c.f1115i, this.f1122c.f1116j));
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "userProfileIncrement single error:" + e2.toString());
        }
    }
}
