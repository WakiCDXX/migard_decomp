package f0;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.xiaomi.mipush.sdk.AssemblePushUtils;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes.dex */
public class i {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final LinkedBlockingQueue<IBinder> f1832a = new LinkedBlockingQueue<>(1);

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    ServiceConnection f1833b = new b(this);

    public String a(Context context) {
        Intent intent = new Intent("com.uodis.opendevice.OPENIDS_SERVICE");
        intent.setPackage(AssemblePushUtils.HMS_PUSH_PACKAGE_NAME);
        String strA = "";
        if (context.bindService(intent, this.f1833b, 1)) {
            try {
                try {
                    try {
                        IBinder iBinderPoll = this.f1832a.poll(1L, TimeUnit.SECONDS);
                        if (iBinderPoll == null) {
                            try {
                                context.unbindService(this.f1833b);
                            } catch (Exception e2) {
                                com.ot.pubsub.util.k.c("HWDeviceIDHelper", e2.getMessage());
                            }
                            return "";
                        }
                        strA = new com.ot.pubsub.util.oaid.a.b.a(iBinderPoll).a();
                        context.unbindService(this.f1833b);
                    } catch (Exception e3) {
                        com.ot.pubsub.util.k.c("HWDeviceIDHelper", e3.getMessage());
                        context.unbindService(this.f1833b);
                    }
                } catch (Exception e4) {
                    com.ot.pubsub.util.k.c("HWDeviceIDHelper", e4.getMessage());
                }
            } catch (Throwable th) {
                try {
                    context.unbindService(this.f1833b);
                } catch (Exception e5) {
                    com.ot.pubsub.util.k.c("HWDeviceIDHelper", e5.getMessage());
                }
                throw th;
            }
        }
        return strA;
    }
}
