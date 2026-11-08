package com.xiaomi.onetrack.f;

import android.content.Context;

/* JADX INFO: loaded from: classes.dex */
class h implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ Context f1419a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ g f1420b;

    h(g gVar, Context context) {
        this.f1420b = gVar;
        this.f1419a = context;
    }

    @Override // java.lang.Runnable
    public void run() {
        g.c(this.f1419a);
        com.xiaomi.onetrack.c.c.a(this.f1419a);
    }
}
