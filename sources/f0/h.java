package f0;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes.dex */
public class h {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final LinkedBlockingQueue<IBinder> f1830a = new LinkedBlockingQueue<>(1);

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    ServiceConnection f1831b = new a(this);

    public String a(Context context) {
        Intent intent = new Intent();
        intent.setAction("com.asus.msa.action.ACCESS_DID");
        intent.setComponent(new ComponentName("com.asus.msa.SupplementaryDID", "com.asus.msa.SupplementaryDID.SupplementaryDIDService"));
        String strA = "";
        try {
            if (context.bindService(intent, this.f1831b, 1)) {
                try {
                    try {
                        IBinder iBinderPoll = this.f1830a.poll(1L, TimeUnit.SECONDS);
                        if (iBinderPoll == null) {
                            try {
                                context.unbindService(this.f1831b);
                            } catch (Exception e2) {
                                com.ot.pubsub.util.k.c("ASUSDeviceIDHelper", e2.getMessage());
                            }
                            return "";
                        }
                        strA = new com.ot.pubsub.util.oaid.a.a.C0004a(iBinderPoll).a();
                        context.unbindService(this.f1831b);
                    } catch (Exception e3) {
                        com.ot.pubsub.util.k.c("ASUSDeviceIDHelper", e3.getMessage());
                        context.unbindService(this.f1831b);
                    }
                } catch (Throwable th) {
                    try {
                        context.unbindService(this.f1831b);
                    } catch (Exception e4) {
                        com.ot.pubsub.util.k.c("ASUSDeviceIDHelper", e4.getMessage());
                    }
                    throw th;
                }
            }
        } catch (Exception e5) {
            com.ot.pubsub.util.k.c("ASUSDeviceIDHelper", e5.getMessage());
        }
        return strA;
    }
}
