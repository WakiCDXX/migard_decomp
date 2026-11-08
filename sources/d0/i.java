package d0;

import com.ot.pubsub.util.k;

/* JADX INFO: loaded from: classes.dex */
class i implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ d f1776a;

    i(d dVar) {
        this.f1776a = dVar;
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            t.d.c();
        } catch (Exception e2) {
            k.c("UploaderEngine", "handleNetChange: " + e2);
        }
    }
}
