package f0;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes.dex */
public class j {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final LinkedBlockingQueue<IBinder> f1834a = new LinkedBlockingQueue<>(1);

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    ServiceConnection f1835b = new c(this);

    public String a(Context context) {
        context.getPackageName();
        Intent intent = new Intent();
        intent.setClassName("com.zui.deviceidservice", "com.zui.deviceidservice.DeviceidService");
        String strA = "";
        if (context.bindService(intent, this.f1835b, 1)) {
            try {
                try {
                    IBinder iBinderPoll = this.f1834a.poll(1L, TimeUnit.SECONDS);
                    if (iBinderPoll == null) {
                        return "";
                    }
                    strA = new com.ot.pubsub.util.oaid.a.c.a.C0005a(iBinderPoll).a();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            } finally {
                try {
                    context.unbindService(this.f1835b);
                } catch (Exception unused) {
                }
            }
        }
        return strA;
    }
}
