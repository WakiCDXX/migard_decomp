package f0;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes.dex */
class d implements ServiceConnection {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ n f1826a;

    d(n nVar) {
        this.f1826a = nVar;
    }

    @Override // android.content.ServiceConnection
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        try {
            this.f1826a.f1844c.offer(iBinder, 1L, TimeUnit.SECONDS);
        } catch (Exception e2) {
            com.ot.pubsub.util.k.c("OnePlusDeviceIDHelper", e2.getMessage());
        }
    }

    @Override // android.content.ServiceConnection
    public void onServiceDisconnected(ComponentName componentName) {
        this.f1826a.f1843b = null;
    }
}
