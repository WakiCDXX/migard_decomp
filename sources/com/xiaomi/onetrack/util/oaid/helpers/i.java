package com.xiaomi.onetrack.util.oaid.helpers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.Signature;
import android.os.IBinder;
import com.miui.powerkeeper.powerchecker.BatteryStatsHelper;
import com.xiaomi.onetrack.util.p;
import java.security.MessageDigest;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import miui.util.HashUtils;

/* JADX INFO: loaded from: classes.dex */
public class i {

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static final String f1647d = "OnePlusDeviceIDHelper";

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    com.xiaomi.onetrack.util.oaid.a.d f1648a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final LinkedBlockingQueue<IBinder> f1649b = new LinkedBlockingQueue<>(1);

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    ServiceConnection f1650c = new ServiceConnection() { // from class: com.xiaomi.onetrack.util.oaid.helpers.OnePlusDeviceIDHelper$1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            try {
                this.f1607a.f1649b.offer(iBinder, 1L, TimeUnit.SECONDS);
            } catch (Exception e2) {
                p.a("OnePlusDeviceIDHelper", e2.getMessage());
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            this.f1607a.f1648a = null;
        }
    };

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private String f1651e;

    private boolean b(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo("com.heytap.openid", 0);
            return packageInfo != null && ((long) packageInfo.versionCode) >= 1;
        } catch (Exception e2) {
            p.a(f1647d, e2.getMessage());
            return true;
        }
    }

    public String a(Context context) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.heytap.openid", "com.heytap.openid.IdentifyService"));
        intent.setAction("action.com.heytap.openid.OPEN_ID_SERVICE");
        String strA = "";
        try {
            if (context.bindService(intent, this.f1650c, 1)) {
                try {
                    try {
                        IBinder iBinderPoll = this.f1649b.poll(1L, TimeUnit.SECONDS);
                        if (iBinderPoll == null) {
                            try {
                                context.unbindService(this.f1650c);
                            } catch (Exception e2) {
                                p.a(f1647d, e2.getMessage());
                            }
                            return "";
                        }
                        com.xiaomi.onetrack.util.oaid.a.d dVarA = com.xiaomi.onetrack.util.oaid.a.d.a.a(iBinderPoll);
                        this.f1648a = dVarA;
                        if (dVarA != null) {
                            strA = a("OUID", context);
                        }
                        context.unbindService(this.f1650c);
                    } catch (Exception e3) {
                        p.a(f1647d, e3.getMessage());
                        context.unbindService(this.f1650c);
                    }
                } catch (Throwable th) {
                    try {
                        context.unbindService(this.f1650c);
                    } catch (Exception e4) {
                        p.a(f1647d, e4.getMessage());
                    }
                    throw th;
                }
            }
        } catch (Exception e5) {
            p.a(f1647d, e5.getMessage());
        }
        return strA;
    }

    private String a(String str, Context context) {
        Signature[] signatureArr;
        String packageName = context.getPackageName();
        if (this.f1651e == null) {
            String string = null;
            try {
                signatureArr = context.getPackageManager().getPackageInfo(packageName, 64).signatures;
            } catch (Exception e2) {
                p.a(f1647d, e2.getMessage());
                signatureArr = null;
            }
            if (signatureArr != null && signatureArr.length > 0) {
                byte[] byteArray = signatureArr[0].toByteArray();
                try {
                    MessageDigest messageDigest = MessageDigest.getInstance(HashUtils.SHA1);
                    if (messageDigest != null) {
                        byte[] bArrDigest = messageDigest.digest(byteArray);
                        StringBuilder sb = new StringBuilder();
                        for (byte b2 : bArrDigest) {
                            sb.append(Integer.toHexString((b2 & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL) | 256).substring(1, 3));
                        }
                        string = sb.toString();
                    }
                } catch (Exception e3) {
                    p.a(f1647d, e3.getMessage());
                }
            }
            this.f1651e = string;
        }
        return ((com.xiaomi.onetrack.util.oaid.a.d.a.C0015a) this.f1648a).a(packageName, this.f1651e, str);
    }
}
