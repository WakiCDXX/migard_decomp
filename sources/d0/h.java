package d0;

import com.ot.pubsub.util.k;
import com.ot.pubsub.util.l;

/* JADX INFO: loaded from: classes.dex */
class h implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ g f1775a;

    h(g gVar) {
        this.f1775a = gVar;
    }

    @Override // java.lang.Runnable
    public void run() {
        this.f1775a.f1774a.m();
        try {
            boolean zA = l.a();
            k.c("UploaderEngine", "UploadTimer netReceiver, 网络是否可用=" + zA);
            if (zA) {
                d.a().d(false);
                d.a().b(0, false);
                d.a().b(1, false);
                d.a().b(2, false);
                this.f1775a.f1774a.s();
            } else {
                this.f1775a.f1774a.t();
            }
        } catch (Exception e2) {
            k.c("UploaderEngine", "netReceiver: " + e2);
        }
    }
}
