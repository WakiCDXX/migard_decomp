package f0;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes.dex */
class c implements ServiceConnection {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ j f1825a;

    c(j jVar) {
        this.f1825a = jVar;
    }

    @Override // android.content.ServiceConnection
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        try {
            this.f1825a.f1834a.offer(iBinder, 1L, TimeUnit.SECONDS);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    @Override // android.content.ServiceConnection
    public void onServiceDisconnected(ComponentName componentName) {
    }
}
