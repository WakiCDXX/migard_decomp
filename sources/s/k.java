package s;

/* JADX INFO: loaded from: classes.dex */
class k implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ j f2337a;

    k(j jVar) {
        this.f2337a = jVar;
    }

    @Override // java.lang.Runnable
    public void run() {
        if (y.i.d()) {
            d0.d.a().d(true);
            d0.d.a().b(0, true);
            d0.d.a().b(1, true);
        }
    }
}
