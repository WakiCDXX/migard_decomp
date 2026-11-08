package f0;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.Signature;
import android.os.IBinder;
import com.miui.powerkeeper.powerchecker.BatteryStatsHelper;
import java.security.MessageDigest;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import miui.util.HashUtils;

/* JADX INFO: loaded from: classes.dex */
public class n {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private String f1842a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    com.ot.pubsub.util.oaid.a.d f1843b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final LinkedBlockingQueue<IBinder> f1844c = new LinkedBlockingQueue<>(1);

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    ServiceConnection f1845d = new d(this);

    private String b(String str, Context context) {
        Signature[] signatureArr;
        String packageName = context.getPackageName();
        if (this.f1842a == null) {
            String string = null;
            try {
                signatureArr = context.getPackageManager().getPackageInfo(packageName, 64).signatures;
            } catch (Exception e2) {
                com.ot.pubsub.util.k.c("OnePlusDeviceIDHelper", e2.getMessage());
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
                    com.ot.pubsub.util.k.c("OnePlusDeviceIDHelper", e3.getMessage());
                }
            }
            this.f1842a = string;
        }
        return ((com.ot.pubsub.util.oaid.a.d.a.C0006a) this.f1843b).a(packageName, this.f1842a, str);
    }

    public String a(Context context) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.heytap.openid", "com.heytap.openid.IdentifyService"));
        intent.setAction("action.com.heytap.openid.OPEN_ID_SERVICE");
        String strB = "";
        try {
            if (context.bindService(intent, this.f1845d, 1)) {
                try {
                    try {
                        IBinder iBinderPoll = this.f1844c.poll(1L, TimeUnit.SECONDS);
                        if (iBinderPoll == null) {
                            try {
                                context.unbindService(this.f1845d);
                            } catch (Exception e2) {
                                com.ot.pubsub.util.k.c("OnePlusDeviceIDHelper", e2.getMessage());
                            }
                            return "";
                        }
                        com.ot.pubsub.util.oaid.a.d dVarA = com.ot.pubsub.util.oaid.a.d.a.a(iBinderPoll);
                        this.f1843b = dVarA;
                        if (dVarA != null) {
                            strB = b("OUID", context);
                        }
                        context.unbindService(this.f1845d);
                    } catch (Exception e3) {
                        com.ot.pubsub.util.k.c("OnePlusDeviceIDHelper", e3.getMessage());
                        context.unbindService(this.f1845d);
                    }
                } catch (Throwable th) {
                    try {
                        context.unbindService(this.f1845d);
                    } catch (Exception e4) {
                        com.ot.pubsub.util.k.c("OnePlusDeviceIDHelper", e4.getMessage());
                    }
                    throw th;
                }
            }
        } catch (Exception e5) {
            com.ot.pubsub.util.k.c("OnePlusDeviceIDHelper", e5.getMessage());
        }
        return strB;
    }
}
