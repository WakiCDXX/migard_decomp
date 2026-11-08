package com.xiaomi.onetrack.c;

/* JADX INFO: loaded from: classes.dex */
class r implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ q f1316a;

    r(q qVar) {
        this.f1316a = qVar;
    }

    @Override // java.lang.Runnable
    public void run() {
        this.f1316a.f1315a.a();
        try {
            boolean zA = com.xiaomi.onetrack.g.c.a();
            com.xiaomi.onetrack.util.p.a("UploadTimer", "UploadTimer netReceiver, 网络是否可用=" + zA);
            if (zA) {
                int[] iArr = {0, 1, 2};
                for (int i2 = 0; i2 < 3; i2++) {
                    int i3 = iArr[i2];
                    int iA = com.xiaomi.onetrack.b.n.a(i3);
                    if (!this.f1316a.f1315a.hasMessages(i3)) {
                        this.f1316a.f1315a.sendEmptyMessageDelayed(i3, iA);
                    }
                }
            }
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.a("UploadTimer", "netReceiver: " + e2);
        }
    }
}
