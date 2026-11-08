package com.xiaomi.onetrack.b;

/* JADX INFO: loaded from: classes.dex */
class j implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ String f1210a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ h f1211b;

    j(h hVar, String str) {
        this.f1211b = hVar;
        this.f1210a = str;
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            this.f1211b.f1204b.getWritableDatabase().delete(g.f1193b, "app_id=?", new String[]{this.f1210a});
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }
}
