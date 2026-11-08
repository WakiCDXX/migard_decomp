package s;

import android.text.TextUtils;
import com.ot.pubsub.util.t;

/* JADX INFO: loaded from: classes.dex */
class d implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ c f2322a;

    d(c cVar) {
        this.f2322a = cVar;
    }

    @Override // java.lang.Runnable
    public void run() {
        com.ot.pubsub.util.k.b();
        w.a.a();
        y.a.a();
        if (t.u() == 0) {
            t.m(System.currentTimeMillis());
        }
        if (!TextUtils.isEmpty(this.f2322a.f2319c.d())) {
            y.b.a().b(this.f2322a.f2319c.d());
        }
        d0.d.a().j();
        y.i.g(false);
    }
}
