package d0;

import android.os.SystemClock;
import com.ot.pubsub.util.k;

/* JADX INFO: loaded from: classes.dex */
class j implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ d.a f1777a;

    j(d.a aVar) {
        this.f1777a = aVar;
    }

    @Override // java.lang.Runnable
    public void run() {
        d.a().d(false);
        k.c("UploaderEngine", "Scheduled try track events cost: " + (SystemClock.elapsedRealtime() - SystemClock.elapsedRealtime()));
        if (y.c.d().n()) {
            return;
        }
        d.this.f1764b.sendEmptyMessageDelayed(123, d.f1762g);
    }
}
