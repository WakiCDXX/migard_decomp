package com.xiaomi.onetrack.util.oaid.helpers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.xiaomi.mipush.sdk.AssemblePushUtils;
import com.xiaomi.onetrack.util.p;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes.dex */
public class d {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static final String f1634c = "HWDeviceIDHelper";

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final LinkedBlockingQueue<IBinder> f1635a = new LinkedBlockingQueue<>(1);

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    ServiceConnection f1636b = new ServiceConnection() { // from class: com.xiaomi.onetrack.util.oaid.helpers.HWDeviceIDHelper$1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            try {
                this.f1605a.f1635a.offer(iBinder, 1L, TimeUnit.SECONDS);
            } catch (Exception e2) {
                p.a("HWDeviceIDHelper", e2.getMessage());
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    public String a(Context context) {
        Intent intent = new Intent("com.uodis.opendevice.OPENIDS_SERVICE");
        intent.setPackage(AssemblePushUtils.HMS_PUSH_PACKAGE_NAME);
        String strA = "";
        if (context.bindService(intent, this.f1636b, 1)) {
            try {
                try {
                    try {
                        IBinder iBinderPoll = this.f1635a.poll(1L, TimeUnit.SECONDS);
                        if (iBinderPoll == null) {
                            try {
                                context.unbindService(this.f1636b);
                            } catch (Exception e2) {
                                p.a(f1634c, e2.getMessage());
                            }
                            return "";
                        }
                        strA = new com.xiaomi.onetrack.util.oaid.a.b.a(iBinderPoll).a();
                        context.unbindService(this.f1636b);
                    } catch (Exception e3) {
                        p.a(f1634c, e3.getMessage());
                        context.unbindService(this.f1636b);
                    }
                } catch (Exception e4) {
                    p.a(f1634c, e4.getMessage());
                }
            } catch (Throwable th) {
                try {
                    context.unbindService(this.f1636b);
                } catch (Exception e5) {
                    p.a(f1634c, e5.getMessage());
                }
                throw th;
            }
        }
        return strA;
    }
}
