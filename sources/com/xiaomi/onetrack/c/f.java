package com.xiaomi.onetrack.c;

/* JADX INFO: loaded from: classes.dex */
class f implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ com.xiaomi.onetrack.f.b f1265a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ c f1266b;

    f(c cVar, com.xiaomi.onetrack.f.b bVar) {
        this.f1266b = cVar;
        this.f1265a = bVar;
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            this.f1266b.b(this.f1265a);
            com.xiaomi.onetrack.a.a.a().a(this.f1265a);
            com.xiaomi.onetrack.util.p.a("EventManager", "addEvent: " + this.f1265a.e() + "data:" + this.f1265a.g().toString());
            t.a().a(this.f1265a.f(), false);
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b("EventManager", "EventManager.addEvent exception: ", e2);
        }
    }
}
