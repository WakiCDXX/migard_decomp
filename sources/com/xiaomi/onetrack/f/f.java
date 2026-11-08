package com.xiaomi.onetrack.f;

/* JADX INFO: loaded from: classes.dex */
final class f implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ String f1412a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ String f1413b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    final /* synthetic */ String f1414c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    final /* synthetic */ String f1415d;

    f(String str, String str2, String str3, String str4) {
        this.f1412a = str;
        this.f1413b = str2;
        this.f1414c = str3;
        this.f1415d = str4;
    }

    @Override // java.lang.Runnable
    public void run() {
        com.xiaomi.onetrack.c.c.a().a(com.xiaomi.onetrack.e.b.b(this.f1412a, this.f1413b, this.f1414c, this.f1415d));
    }
}
