package f0;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes.dex */
class a implements ServiceConnection {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ h f1823a;

    a(h hVar) {
        this.f1823a = hVar;
    }

    @Override // android.content.ServiceConnection
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        try {
            this.f1823a.f1830a.offer(iBinder, 1L, TimeUnit.SECONDS);
        } catch (Exception e2) {
            com.ot.pubsub.util.k.c("ASUSDeviceIDHelper", e2.getMessage());
        }
    }

    @Override // android.content.ServiceConnection
    public void onServiceDisconnected(ComponentName componentName) {
    }
}
