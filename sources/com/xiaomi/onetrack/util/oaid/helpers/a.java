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
public class a {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static final String f1611c = "ASUSDeviceIDHelper";

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final LinkedBlockingQueue<IBinder> f1612a = new LinkedBlockingQueue<>(1);

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    ServiceConnection f1613b = new ServiceConnection() { // from class: com.xiaomi.onetrack.util.oaid.helpers.ASUSDeviceIDHelper$1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            try {
                this.f1604a.f1612a.offer(iBinder, 1L, TimeUnit.SECONDS);
            } catch (Exception e2) {
                p.a("ASUSDeviceIDHelper", e2.getMessage());
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    public String a(Context context) {
        Intent intent = new Intent();
        intent.setAction("com.asus.msa.action.ACCESS_DID");
        intent.setComponent(new ComponentName("com.asus.msa.SupplementaryDID", "com.asus.msa.SupplementaryDID.SupplementaryDIDService"));
        String strA = "";
        try {
            if (context.bindService(intent, this.f1613b, 1)) {
                try {
                    try {
                        IBinder iBinderPoll = this.f1612a.poll(1L, TimeUnit.SECONDS);
                        if (iBinderPoll == null) {
                            try {
                                context.unbindService(this.f1613b);
                            } catch (Exception e2) {
                                p.a(f1611c, e2.getMessage());
                            }
                            return "";
                        }
                        strA = new com.xiaomi.onetrack.util.oaid.a.a.C0013a(iBinderPoll).a();
                        context.unbindService(this.f1613b);
                    } catch (Exception e3) {
                        p.a(f1611c, e3.getMessage());
                        context.unbindService(this.f1613b);
                    }
                } catch (Throwable th) {
                    try {
                        context.unbindService(this.f1613b);
                    } catch (Exception e4) {
                        p.a(f1611c, e4.getMessage());
                    }
                    throw th;
                }
            }
        } catch (Exception e5) {
            p.a(f1611c, e5.getMessage());
        }
        return strA;
    }
}
