package com.xiaomi.onetrack.api;

import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
class ah implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ Object f973a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ String f974b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    final /* synthetic */ j f975c;

    ah(j jVar, Object obj, String str) {
        this.f975c = jVar;
        this.f973a = obj;
        this.f974b = str;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        try {
            if (!com.xiaomi.onetrack.util.r.b(this.f973a)) {
                com.xiaomi.onetrack.util.r.a("OneTrackImp", this.f974b);
                return;
            }
            this.f975c.f1109b.a(d.f1046c, e.a(new JSONObject().put(this.f974b, this.f973a), this.f975c.f1112f, this.f975c.f1114h, this.f975c.f(d.f1046c), this.f975c.f1115i, this.f975c.f1116j));
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "setUserProfile single error:" + e2.toString());
        }
    }
}
