package com.xiaomi.onetrack.api;

/* JADX INFO: loaded from: classes.dex */
class ab implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ boolean f953a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ j f954b;

    ab(j jVar, boolean z2) {
        this.f954b = jVar;
        this.f953a = z2;
    }

    @Override // java.lang.Runnable
    public void run() {
        this.f954b.f1115i.a(this.f953a);
        com.xiaomi.onetrack.util.aa.a(com.xiaomi.onetrack.util.r.a(this.f954b.f1112f), this.f953a);
    }
}
