package com.xiaomi.onetrack.api;

/* JADX INFO: loaded from: classes.dex */
class am implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ al f995a;

    am(al alVar) {
        this.f995a = alVar;
    }

    @Override // java.lang.Runnable
    public void run() {
        if (com.xiaomi.onetrack.c.i.b()) {
            this.f995a.b();
        }
    }
}
