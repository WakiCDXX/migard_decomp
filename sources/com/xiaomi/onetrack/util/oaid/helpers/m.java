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
public class m {

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static final String f1661d = "ZTEDeviceIDHelper";

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    String f1662a = "com.mdid.msa";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final LinkedBlockingQueue<IBinder> f1663b = new LinkedBlockingQueue<>(1);

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    ServiceConnection f1664c = new ServiceConnection() { // from class: com.xiaomi.onetrack.util.oaid.helpers.ZTEDeviceIDHelper$1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            try {
                this.f1610a.f1663b.offer(iBinder, 1L, TimeUnit.SECONDS);
            } catch (Exception e2) {
                p.a("ZTEDeviceIDHelper", e2.getMessage());
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    private void a(String str, Context context) {
        Intent intent = new Intent();
        intent.setClassName(this.f1662a, "com.mdid.msa.service.MsaKlService");
        intent.setAction("com.bun.msa.action.start.service");
        intent.putExtra("com.bun.msa.param.pkgname", str);
        try {
            intent.putExtra("com.bun.msa.param.runinset", true);
            context.startService(intent);
        } catch (Exception e2) {
            p.a(f1661d, e2.getMessage());
        }
    }

    /* JADX INFO: Removed unreachable split cross block B:38:0x0091 */
    /* JADX WARN: Multi-variable type inference failed */
    public String a(Context context) {
        try {
            context.getPackageManager().getPackageInfo(this.f1662a, 0);
        } catch (Exception e2) {
            p.a(f1661d, e2.getMessage());
        }
        String packageName = context.getPackageName();
        a(packageName, context);
        Intent intent = new Intent();
        intent.setClassName("com.mdid.msa", "com.mdid.msa.service.MsaIdService");
        intent.setAction("com.bun.msa.action.bindto.service");
        intent.putExtra("com.bun.msa.param.pkgname", packageName);
        boolean zBindService = context.bindService(intent, this.f1664c, 1);
        String strB = "";
        try {
            try {
                if (zBindService) {
                    try {
                        IBinder iBinderPoll = this.f1663b.poll(1L, TimeUnit.SECONDS);
                        if (iBinderPoll == null) {
                            try {
                                context.unbindService(this.f1664c);
                            } catch (Exception e3) {
                                p.a(f1661d, e3.getMessage());
                            }
                            return "";
                        }
                        strB = new com.xiaomi.onetrack.util.oaid.a.g.a.C0017a(iBinderPoll).b();
                        ServiceConnection serviceConnection = this.f1664c;
                        context.unbindService(serviceConnection);
                        this = serviceConnection;
                    } catch (Exception e4) {
                        p.a(f1661d, e4.getMessage());
                        ServiceConnection serviceConnection2 = this.f1664c;
                        context.unbindService(serviceConnection2);
                        this = serviceConnection2;
                    }
                }
            } catch (Throwable th) {
                try {
                    context.unbindService(this.f1664c);
                } catch (Exception e5) {
                    p.a(f1661d, e5.getMessage());
                }
                throw th;
            }
        } catch (Exception e6) {
            String message = e6.getMessage();
            p.a(f1661d, message);
            this = message;
        }
        return strB;
    }
}
