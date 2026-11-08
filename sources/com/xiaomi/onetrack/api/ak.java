package com.xiaomi.onetrack.api;

/* JADX INFO: loaded from: classes.dex */
class ak implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ int f986a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ aj f987b;

    ak(aj ajVar, int i2) {
        this.f987b = ajVar;
        this.f986a = i2;
    }

    @Override // java.lang.Runnable
    public void run() {
        if (com.xiaomi.onetrack.c.i.b() && this.f986a == 2) {
            com.xiaomi.onetrack.c.t.a().a(0, true);
            com.xiaomi.onetrack.c.t.a().a(1, true);
            com.xiaomi.onetrack.a.c.b.a().b();
        }
    }
}
