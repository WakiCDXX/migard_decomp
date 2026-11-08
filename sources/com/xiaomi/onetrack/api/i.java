package com.xiaomi.onetrack.api;

/* JADX INFO: loaded from: classes.dex */
class i implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ Thread f1104a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ Throwable f1105b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    final /* synthetic */ h f1106c;

    i(h hVar, Thread thread, Throwable th) {
        this.f1106c = hVar;
        this.f1104a = thread;
        this.f1105b = th;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        this.f1106c.a(this.f1104a, this.f1105b);
    }
}
