package d0;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/* JADX INFO: loaded from: classes.dex */
class g extends BroadcastReceiver {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ d f1774a;

    g(d dVar) {
        this.f1774a = dVar;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (!this.f1774a.f1765c) {
            w.a.b(new h(this));
        }
        this.f1774a.f1765c = false;
    }
}
