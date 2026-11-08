package com.xiaomi.onetrack.c;

/* JADX INFO: loaded from: classes.dex */
class m implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ String f1295a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ String f1296b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    final /* synthetic */ String f1297c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    final /* synthetic */ l f1298d;

    m(l lVar, String str, String str2, String str3) {
        this.f1298d = lVar;
        this.f1295a = str;
        this.f1296b = str2;
        this.f1297c = str3;
    }

    @Override // java.lang.Runnable
    public void run() {
        this.f1298d.b(this.f1295a, this.f1296b, this.f1297c);
    }
}
