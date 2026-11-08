package t;

import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
class h implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ ArrayList f2350a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ g f2351b;

    h(g gVar, ArrayList arrayList) {
        this.f2351b = gVar;
        this.f2350a = arrayList;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        if (com.ot.pubsub.util.k.f724a) {
            com.ot.pubsub.util.k.c("ConfigDbManager", "update: " + this.f2350a);
        }
        this.f2351b.m(this.f2350a);
    }
}
