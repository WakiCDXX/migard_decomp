package f0;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes.dex */
public class p {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final LinkedBlockingQueue<IBinder> f1850a = new LinkedBlockingQueue<>(1);

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    ServiceConnection f1851b = new f(this);

    public String a(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.samsung.android.deviceidservice", "com.samsung.android.deviceidservice.DeviceIdService");
        String strA = "";
        if (context.bindService(intent, this.f1851b, 1)) {
            try {
                try {
                    try {
                        IBinder iBinderPoll = this.f1850a.poll(1L, TimeUnit.SECONDS);
                        if (iBinderPoll == null) {
                            try {
                                context.unbindService(this.f1851b);
                            } catch (Exception e2) {
                                com.ot.pubsub.util.k.c("SamsungDeviceIDHelper", e2.getMessage());
                            }
                            return "";
                        }
                        strA = new com.ot.pubsub.util.oaid.a.f.a(iBinderPoll).a();
                        context.unbindService(this.f1851b);
                    } catch (Exception e3) {
                        com.ot.pubsub.util.k.c("SamsungDeviceIDHelper", e3.getMessage());
                        context.unbindService(this.f1851b);
                    }
                } catch (Exception e4) {
                    com.ot.pubsub.util.k.c("SamsungDeviceIDHelper", e4.getMessage());
                }
            } catch (Throwable th) {
                try {
                    context.unbindService(this.f1851b);
                } catch (Exception e5) {
                    com.ot.pubsub.util.k.c("SamsungDeviceIDHelper", e5.getMessage());
                }
                throw th;
            }
        }
        return strA;
    }
}
