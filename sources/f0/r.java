package f0;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes.dex */
public class r {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    String f1852a = "com.mdid.msa";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final LinkedBlockingQueue<IBinder> f1853b = new LinkedBlockingQueue<>(1);

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    ServiceConnection f1854c = new g(this);

    private void b(String str, Context context) {
        Intent intent = new Intent();
        intent.setClassName(this.f1852a, "com.mdid.msa.service.MsaKlService");
        intent.setAction("com.bun.msa.action.start.service");
        intent.putExtra("com.bun.msa.param.pkgname", str);
        try {
            intent.putExtra("com.bun.msa.param.runinset", true);
            context.startService(intent);
        } catch (Exception e2) {
            com.ot.pubsub.util.k.c("ZTEDeviceIDHelper", e2.getMessage());
        }
    }

    /* JADX INFO: Removed unreachable split cross block B:38:0x0091 */
    /* JADX WARN: Multi-variable type inference failed */
    public String a(Context context) {
        try {
            context.getPackageManager().getPackageInfo(this.f1852a, 0);
        } catch (Exception e2) {
            com.ot.pubsub.util.k.c("ZTEDeviceIDHelper", e2.getMessage());
        }
        String packageName = context.getPackageName();
        b(packageName, context);
        Intent intent = new Intent();
        intent.setClassName("com.mdid.msa", "com.mdid.msa.service.MsaIdService");
        intent.setAction("com.bun.msa.action.bindto.service");
        intent.putExtra("com.bun.msa.param.pkgname", packageName);
        boolean zBindService = context.bindService(intent, this.f1854c, 1);
        String strB = "";
        try {
            try {
                if (zBindService) {
                    try {
                        IBinder iBinderPoll = this.f1853b.poll(1L, TimeUnit.SECONDS);
                        if (iBinderPoll == null) {
                            try {
                                context.unbindService(this.f1854c);
                            } catch (Exception e3) {
                                com.ot.pubsub.util.k.c("ZTEDeviceIDHelper", e3.getMessage());
                            }
                            return "";
                        }
                        strB = new com.ot.pubsub.util.oaid.a.g.a.C0008a(iBinderPoll).b();
                        ServiceConnection serviceConnection = this.f1854c;
                        context.unbindService(serviceConnection);
                        this = serviceConnection;
                    } catch (Exception e4) {
                        com.ot.pubsub.util.k.c("ZTEDeviceIDHelper", e4.getMessage());
                        ServiceConnection serviceConnection2 = this.f1854c;
                        context.unbindService(serviceConnection2);
                        this = serviceConnection2;
                    }
                }
            } catch (Throwable th) {
                try {
                    context.unbindService(this.f1854c);
                } catch (Exception e5) {
                    com.ot.pubsub.util.k.c("ZTEDeviceIDHelper", e5.getMessage());
                }
                throw th;
            }
        } catch (Exception e6) {
            String message = e6.getMessage();
            com.ot.pubsub.util.k.c("ZTEDeviceIDHelper", message);
            this = message;
        }
        return strB;
    }
}
