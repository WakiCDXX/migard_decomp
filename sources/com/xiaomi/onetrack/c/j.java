package com.xiaomi.onetrack.c;

/* JADX INFO: loaded from: classes.dex */
final class j implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ String f1284a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ String f1285b;

    j(String str, String str2) {
        this.f1284a = str;
        this.f1285b = str2;
    }

    @Override // java.lang.Runnable
    public void run() {
        com.xiaomi.onetrack.api.a.a().c();
        i.c(this.f1284a, this.f1285b);
    }
}
