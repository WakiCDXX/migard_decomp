package com.xiaomi.onetrack.f;

/* JADX INFO: loaded from: classes.dex */
final class e implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ String f1408a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ String f1409b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    final /* synthetic */ String f1410c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    final /* synthetic */ String f1411d;

    e(String str, String str2, String str3, String str4) {
        this.f1408a = str;
        this.f1409b = str2;
        this.f1410c = str3;
        this.f1411d = str4;
    }

    @Override // java.lang.Runnable
    public void run() {
        com.xiaomi.onetrack.c.c.a().a(com.xiaomi.onetrack.e.b.a(this.f1408a, this.f1409b, this.f1410c, this.f1411d));
    }
}
