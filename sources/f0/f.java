package f0;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes.dex */
class f implements ServiceConnection {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ p f1828a;

    f(p pVar) {
        this.f1828a = pVar;
    }

    @Override // android.content.ServiceConnection
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        try {
            this.f1828a.f1850a.offer(iBinder, 1L, TimeUnit.SECONDS);
        } catch (Exception e2) {
            com.ot.pubsub.util.k.c("SamsungDeviceIDHelper", e2.getMessage());
        }
    }

    @Override // android.content.ServiceConnection
    public void onServiceDisconnected(ComponentName componentName) {
    }
}
