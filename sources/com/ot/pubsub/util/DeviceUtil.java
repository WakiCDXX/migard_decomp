package com.ot.pubsub.util;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.xiaomi.mipush.sdk.stat.db.MessageInfoContract;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import miui.date.DateUtils;

/* JADX INFO: loaded from: classes.dex */
public class DeviceUtil {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static Method f692a = null;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static Method f693b = null;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static Method f694c = null;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static Object f695d = null;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static Method f696e = null;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static Method f697f = null;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private static volatile String f698g = null;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private static volatile String f699h = null;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private static String f700i = null;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private static String f701j = null;

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private static volatile boolean f702k = false;

    private static class GAIDClient {
        static String a(Context context) {
            if (!c(context)) {
                k.c("GAIDClient", "Google play service is not available");
                return "";
            }
            b bVar = new b(null);
            try {
                try {
                    Intent intent = new Intent("com.google.android.gms.ads.identifier.service.START");
                    intent.setPackage("com.google.android.gms");
                    if (context.bindService(intent, bVar, 1)) {
                        return new a(bVar.a()).a();
                    }
                } catch (Exception e2) {
                    k.e("GAIDClient", "Query Google ADID failed ", e2);
                }
                return "";
            } finally {
                context.unbindService(bVar);
            }
        }

        static boolean b(Context context) {
            if (!c(context)) {
                k.c("GAIDClient", "Google play service is not available");
                return false;
            }
            b bVar = new b(null);
            try {
                try {
                    Intent intent = new Intent("com.google.android.gms.ads.identifier.service.START");
                    intent.setPackage("com.google.android.gms");
                    if (context.bindService(intent, bVar, 1)) {
                        return new a(bVar.a()).a(true);
                    }
                } catch (Exception e2) {
                    k.e("GAIDClient", "Query Google isLimitAdTrackingEnabled failed ", e2);
                }
                return false;
            } finally {
                context.unbindService(bVar);
            }
        }

        private static boolean c(Context context) {
            try {
                context.getPackageManager().getPackageInfo("com.android.vending", DateUtils.FORMAT_ABBREV_TIME);
                return true;
            } catch (PackageManager.NameNotFoundException unused) {
                return false;
            }
        }

        private static final class b implements ServiceConnection {

            /* JADX INFO: renamed from: a, reason: collision with root package name */
            private boolean f704a;

            /* JADX INFO: renamed from: b, reason: collision with root package name */
            private IBinder f705b;

            private b() {
                this.f704a = false;
            }

            public IBinder a() throws InterruptedException {
                IBinder iBinder = this.f705b;
                if (iBinder != null) {
                    return iBinder;
                }
                if (iBinder == null && !this.f704a) {
                    synchronized (this) {
                        try {
                            wait(MessageInfoContract.TIMEOUT);
                            if (this.f705b == null) {
                                throw new InterruptedException("Not connect or connect timeout to google play service");
                            }
                        } catch (Throwable th) {
                            throw th;
                        }
                    }
                }
                return this.f705b;
            }

            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                synchronized (this) {
                    this.f705b = iBinder;
                    notifyAll();
                }
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName componentName) {
                this.f704a = true;
                this.f705b = null;
            }

            /* synthetic */ b(d dVar) {
                this();
            }
        }

        private static final class a implements IInterface {

            /* JADX INFO: renamed from: a, reason: collision with root package name */
            private IBinder f703a;

            public a(IBinder iBinder) {
                this.f703a = iBinder;
            }

            public String a() throws RemoteException {
                if (this.f703a == null) {
                    return "";
                }
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                    this.f703a.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readString();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.f703a;
            }

            public boolean a(boolean z2) throws RemoteException {
                if (this.f703a == null) {
                    return false;
                }
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                    parcelObtain.writeInt(z2 ? 1 : 0);
                    this.f703a.transact(2, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }
    }

    static {
        try {
            f692a = Class.forName("android.os.SystemProperties").getMethod("get", String.class);
        } catch (Throwable th) {
            k.h("DeviceUtil", "sGetProp init failed ex: " + th.getMessage());
        }
        try {
            Class<?> cls = Class.forName("miui.telephony.TelephonyManagerEx");
            Class[] clsArr = new Class[0];
            f695d = cls.getMethod("getDefault", null).invoke(null, null);
            Class[] clsArr2 = new Class[0];
            f693b = cls.getMethod("getImeiList", null);
            Class[] clsArr3 = new Class[0];
            f694c = cls.getMethod("getMeidList", null);
            f697f = cls.getMethod("getSubscriberIdForSlot", Integer.TYPE);
        } catch (Throwable th2) {
            k.h("DeviceUtil", "TelephonyManagerEx init failed ex: " + th2.getMessage());
        }
        try {
            f696e = Class.forName("android.telephony.TelephonyManager").getMethod("getImei", Integer.TYPE);
        } catch (Throwable th3) {
            k.h("DeviceUtil", "sGetImeiForSlot init failed ex: " + th3.getMessage());
        }
    }

    private static List<String> a(Context context) {
        if (f696e == null) {
            return null;
        }
        try {
            ArrayList arrayList = new ArrayList();
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
            String str = (String) f696e.invoke(telephonyManager, 0);
            if (g(str)) {
                arrayList.add(str);
            }
            if (k()) {
                String str2 = (String) f696e.invoke(telephonyManager, 1);
                if (g(str2)) {
                    arrayList.add(str2);
                }
            }
            return arrayList;
        } catch (Exception e2) {
            k.c("DeviceUtil", "getImeiListAboveLollipop failed ex: " + e2.getMessage());
            return null;
        }
    }

    public static String b(Context context) {
        if (!TextUtils.isEmpty(f698g)) {
            return f698g;
        }
        n(context);
        return !TextUtils.isEmpty(f698g) ? f698g : "";
    }

    private static boolean c(List<String> list) {
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            if (!g(it.next())) {
                return true;
            }
        }
        return false;
    }

    public static String d(Context context) {
        if (!TextUtils.isEmpty(f701j)) {
            return f701j;
        }
        String strB = b(context);
        if (TextUtils.isEmpty(strB)) {
            return "";
        }
        String strH = u.d.h(strB);
        f701j = strH;
        return strH;
    }

    private static String e(String str) {
        try {
            Method method = f692a;
            if (method != null) {
                return String.valueOf(method.invoke(null, str));
            }
        } catch (Exception e2) {
            k.c("DeviceUtil", "getProp failed ex: " + e2.getMessage());
        }
        return null;
    }

    public static String f() {
        return Build.MODEL;
    }

    private static boolean g(String str) {
        return (str == null || str.length() != 15 || str.matches("^0*$")) ? false : true;
    }

    public static String h() {
        return e("ro.product.marketname");
    }

    public static String i() {
        return Build.MANUFACTURER;
    }

    private static List<String> j() {
        if (f693b != null && !l()) {
            try {
                List<String> list = (List) f693b.invoke(f695d, null);
                if (list != null && list.size() > 0 && !c(list)) {
                    return list;
                }
            } catch (Exception e2) {
                k.c("DeviceUtil", "getImeiListFromMiui failed ex: " + e2.getMessage());
            }
        }
        return null;
    }

    private static boolean k() {
        if ("dsds".equals(e("persist.radio.multisim.config"))) {
            return true;
        }
        String str = Build.DEVICE;
        return "lcsh92_wet_jb9".equals(str) || "lcsh92_wet_tdd".equals(str) || "HM2013022".equals(str) || "HM2013023".equals(str) || "armani".equals(str) || "HM2014011".equals(str) || "HM2014012".equals(str);
    }

    private static boolean l() {
        return false;
    }

    public static String m(Context context) {
        if (!TextUtils.isEmpty(f700i)) {
            return f700i;
        }
        if (GAIDClient.b(context)) {
            return "";
        }
        String strA = GAIDClient.a(context);
        if (TextUtils.isEmpty(strA)) {
            return "";
        }
        f700i = strA;
        return strA;
    }

    @SuppressLint({"MissingPermission"})
    public static List<String> n(Context context) {
        List<String> listA = null;
        if (p.b(context)) {
            if (f702k) {
                return null;
            }
            List<String> listJ = j();
            listA = (listJ == null || listJ.isEmpty()) ? a(context) : listJ;
            f702k = true;
        }
        if (listA != null && !listA.isEmpty()) {
            Collections.sort(listA);
            f698g = listA.get(0);
            if (listA.size() >= 2) {
                f699h = listA.get(1);
            }
        }
        return listA;
    }
}
