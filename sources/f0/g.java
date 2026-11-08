package f0;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes.dex */
class g implements ServiceConnection {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ r f1829a;

    g(r rVar) {
        this.f1829a = rVar;
    }

    @Override // android.content.ServiceConnection
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        try {
            this.f1829a.f1853b.offer(iBinder, 1L, TimeUnit.SECONDS);
        } catch (Exception e2) {
            com.ot.pubsub.util.k.c("ZTEDeviceIDHelper", e2.getMessage());
        }
    }

    @Override // android.content.ServiceConnection
    public void onServiceDisconnected(ComponentName componentName) {
    }
}
