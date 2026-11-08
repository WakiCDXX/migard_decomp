package com.xiaomi.onetrack.c;

/* JADX INFO: loaded from: classes.dex */
class s implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ p f1317a;

    s(p pVar) {
        this.f1317a = pVar;
    }

    @Override // java.lang.Runnable
    public void run() {
        if (this.f1317a.f1313i.get()) {
            com.xiaomi.onetrack.b.e.b();
        }
        this.f1317a.f1313i.set(true);
    }
}
