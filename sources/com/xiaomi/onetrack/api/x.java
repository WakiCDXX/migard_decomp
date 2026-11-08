package com.xiaomi.onetrack.api;

/* JADX INFO: loaded from: classes.dex */
class x implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ String f1150a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ j f1151b;

    x(j jVar, String str) {
        this.f1151b = jVar;
        this.f1150a = str;
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            com.xiaomi.onetrack.util.o.a().a(this.f1150a);
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "setInstanceId error: " + e2.toString());
        }
    }
}
