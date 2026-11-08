package com.xiaomi.onetrack.util.oaid.helpers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.xiaomi.onetrack.util.p;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes.dex */
public class k {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static final String f1657c = "SamsungDeviceIDHelper";

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final LinkedBlockingQueue<IBinder> f1658a = new LinkedBlockingQueue<>(1);

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    ServiceConnection f1659b = new ServiceConnection() { // from class: com.xiaomi.onetrack.util.oaid.helpers.SamsungDeviceIDHelper$1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            try {
                this.f1609a.f1658a.offer(iBinder, 1L, TimeUnit.SECONDS);
            } catch (Exception e2) {
                p.a("SamsungDeviceIDHelper", e2.getMessage());
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    public String a(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.samsung.android.deviceidservice", "com.samsung.android.deviceidservice.DeviceIdService");
        String strA = "";
        if (context.bindService(intent, this.f1659b, 1)) {
            try {
                try {
                    try {
                        IBinder iBinderPoll = this.f1658a.poll(1L, TimeUnit.SECONDS);
                        if (iBinderPoll == null) {
                            try {
                                context.unbindService(this.f1659b);
                            } catch (Exception e2) {
                                p.a(f1657c, e2.getMessage());
                            }
                            return "";
                        }
                        strA = new com.xiaomi.onetrack.util.oaid.a.f.a(iBinderPoll).a();
                        context.unbindService(this.f1659b);
                    } catch (Exception e3) {
                        p.a(f1657c, e3.getMessage());
                        context.unbindService(this.f1659b);
                    }
                } catch (Exception e4) {
                    p.a(f1657c, e4.getMessage());
                }
            } catch (Throwable th) {
                try {
                    context.unbindService(this.f1659b);
                } catch (Exception e5) {
                    p.a(f1657c, e5.getMessage());
                }
                throw th;
            }
        }
        return strA;
    }
}
