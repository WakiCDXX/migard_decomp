package com.xiaomi.onetrack.util;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;
import com.xiaomi.mipush.sdk.stat.db.MessageInfoContract;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import miui.date.DateUtils;

/* JADX INFO: loaded from: classes.dex */
public class DeviceUtil {
    private static String A = null;
    private static String B = null;
    private static String C = null;
    private static String D = null;
    private static String E = null;
    private static Boolean F = null;
    private static String G = null;
    private static volatile boolean H = false;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    static final int f1447a = 15;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    static final int f1448b = 6;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static final String f1449c = "DeviceUtil";

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static final String f1450d = "";

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static final String f1451e = "ro.build.description";

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static final int f1452f = 15;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private static final int f1453g = 14;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private static Method f1454h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private static Method f1455i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private static Method f1456j;

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private static Object f1457k;

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    private static Method f1458l;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    private static Method f1459m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    private static volatile String f1460n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    private static volatile String f1461o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    private static String f1462p;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    private static String f1463q;

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    private static String f1464r;

    /* JADX INFO: renamed from: s, reason: collision with root package name */
    private static String f1465s;

    /* JADX INFO: renamed from: t, reason: collision with root package name */
    private static String f1466t;

    /* JADX INFO: renamed from: u, reason: collision with root package name */
    private static String f1467u;

    /* JADX INFO: renamed from: v, reason: collision with root package name */
    private static String f1468v;

    /* JADX INFO: renamed from: w, reason: collision with root package name */
    private static String f1469w;

    /* JADX INFO: renamed from: x, reason: collision with root package name */
    private static String f1470x;

    /* JADX INFO: renamed from: y, reason: collision with root package name */
    private static String f1471y;

    /* JADX INFO: renamed from: z, reason: collision with root package name */
    private static String f1472z;

    private static class GAIDClient {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private static final String f1473a = "GAIDClient";

        private GAIDClient() {
        }

        static String a(Context context) {
            if (!c(context)) {
                p.a(f1473a, "Google play service is not available");
                return "";
            }
            AdvertisingConnection advertisingConnection = new AdvertisingConnection(null);
            try {
                try {
                    Intent intent = new Intent("com.google.android.gms.ads.identifier.service.START");
                    intent.setPackage("com.google.android.gms");
                    if (context.bindService(intent, advertisingConnection, 1)) {
                        return new a(advertisingConnection.a()).a();
                    }
                } catch (Exception e2) {
                    p.a(f1473a, "Query Google ADID failed ", e2);
                }
                return "";
            } finally {
                context.unbindService(advertisingConnection);
            }
        }

        static boolean b(Context context) {
            if (!c(context)) {
                p.a(f1473a, "Google play service is not available");
                return false;
            }
            AdvertisingConnection advertisingConnection = new AdvertisingConnection(null);
            try {
                try {
                    Intent intent = new Intent("com.google.android.gms.ads.identifier.service.START");
                    intent.setPackage("com.google.android.gms");
                    if (context.bindService(intent, advertisingConnection, 1)) {
                        return new a(advertisingConnection.a()).a(true);
                    }
                } catch (Exception e2) {
                    p.a(f1473a, "Query Google isLimitAdTrackingEnabled failed ", e2);
                }
                return false;
            } finally {
                context.unbindService(advertisingConnection);
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

        private static final class AdvertisingConnection implements ServiceConnection {

            /* JADX INFO: renamed from: a, reason: collision with root package name */
            private static final int f1474a = 30000;

            /* JADX INFO: renamed from: b, reason: collision with root package name */
            private boolean f1475b;

            /* JADX INFO: renamed from: c, reason: collision with root package name */
            private IBinder f1476c;

            private AdvertisingConnection() {
                this.f1475b = false;
            }

            public IBinder a() throws InterruptedException {
                IBinder iBinder = this.f1476c;
                if (iBinder != null) {
                    return iBinder;
                }
                if (iBinder == null && !this.f1475b) {
                    synchronized (this) {
                        try {
                            wait(MessageInfoContract.TIMEOUT);
                            if (this.f1476c == null) {
                                throw new InterruptedException("Not connect or connect timeout to google play service");
                            }
                        } catch (Throwable th) {
                            throw th;
                        }
                    }
                }
                return this.f1476c;
            }

            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                synchronized (this) {
                    this.f1476c = iBinder;
                    notifyAll();
                }
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName componentName) {
                this.f1475b = true;
                this.f1476c = null;
            }

            /* synthetic */ AdvertisingConnection(h hVar) {
                this();
            }
        }

        private static final class a implements IInterface {

            /* JADX INFO: renamed from: a, reason: collision with root package name */
            private IBinder f1477a;

            public a(IBinder iBinder) {
                this.f1477a = iBinder;
            }

            public String a() throws RemoteException {
                if (this.f1477a == null) {
                    return "";
                }
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                    this.f1477a.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readString();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.f1477a;
            }

            public boolean a(boolean z2) throws RemoteException {
                if (this.f1477a == null) {
                    return false;
                }
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                    parcelObtain.writeInt(z2 ? 1 : 0);
                    this.f1477a.transact(2, parcelObtain, parcelObtain2, 0);
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
            f1454h = Class.forName("android.os.SystemProperties").getMethod("get", String.class);
        } catch (Throwable th) {
            p.b(f1449c, "sGetProp init failed ex: " + th.getMessage());
        }
        try {
            Class<?> cls = Class.forName("miui.telephony.TelephonyManagerEx");
            Class[] clsArr = new Class[0];
            f1457k = cls.getMethod("getDefault", null).invoke(null, null);
            Class[] clsArr2 = new Class[0];
            f1455i = cls.getMethod("getImeiList", null);
            Class[] clsArr3 = new Class[0];
            f1456j = cls.getMethod("getMeidList", null);
            f1459m = cls.getMethod("getSubscriberIdForSlot", Integer.TYPE);
        } catch (Throwable th2) {
            p.b(f1449c, "TelephonyManagerEx init failed ex: " + th2.getMessage());
        }
        try {
            f1458l = Class.forName("android.telephony.TelephonyManager").getMethod("getImei", Integer.TYPE);
        } catch (Throwable th3) {
            p.b(f1449c, "sGetImeiForSlot init failed ex: " + th3.getMessage());
        }
    }

    private static String A(Context context) {
        if (!TextUtils.isEmpty(f1462p)) {
            return f1462p;
        }
        D(context);
        return !TextUtils.isEmpty(f1462p) ? f1462p : "";
    }

    private static List<String> B(Context context) {
        if (f1458l == null) {
            return null;
        }
        try {
            ArrayList arrayList = new ArrayList();
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
            String str = (String) f1458l.invoke(telephonyManager, 0);
            if (c(str)) {
                arrayList.add(str);
            }
            if (n()) {
                String str2 = (String) f1458l.invoke(telephonyManager, 1);
                if (c(str2)) {
                    arrayList.add(str2);
                }
            }
            return arrayList;
        } catch (Exception e2) {
            p.a(f1449c, "getImeiListAboveLollipop failed ex: " + e2.getMessage());
            return null;
        }
    }

    @SuppressLint({"MissingPermission"})
    private static List<String> C(Context context) {
        try {
            ArrayList arrayList = new ArrayList();
            Class<?> cls = Class.forName("android.telephony.TelephonyManager");
            if (!n()) {
                Class[] clsArr = new Class[0];
                String deviceId = ((TelephonyManager) cls.getMethod("getDefault", null).invoke(null, null)).getDeviceId();
                if (c(deviceId)) {
                    arrayList.add(deviceId);
                }
                return arrayList;
            }
            Class<?> cls2 = Integer.TYPE;
            String deviceId2 = ((TelephonyManager) cls.getMethod("getDefault", cls2).invoke(null, 0)).getDeviceId();
            String deviceId3 = ((TelephonyManager) cls.getMethod("getDefault", cls2).invoke(null, 1)).getDeviceId();
            if (c(deviceId2)) {
                arrayList.add(deviceId2);
            }
            if (c(deviceId3)) {
                arrayList.add(deviceId3);
            }
            return arrayList;
        } catch (Throwable th) {
            p.a(f1449c, "getImeiListBelowLollipop failed ex: " + th.getMessage());
            return null;
        }
    }

    @SuppressLint({"MissingPermission"})
    private static List<String> D(Context context) {
        if (u.a(context)) {
            Method method = f1456j;
            if (method != null) {
                try {
                    List<String> list = (List) method.invoke(f1457k, null);
                    if (list != null && list.size() > 0 && !b(list)) {
                        Collections.sort(list);
                        f1462p = list.get(0);
                        return list;
                    }
                } catch (Exception e2) {
                    p.a(f1449c, "queryMeidList failed ex: " + e2.getMessage());
                }
            }
            try {
                Class<?> cls = Class.forName("android.telephony.TelephonyManager");
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
                Class[] clsArr = new Class[0];
                Method method2 = cls.getMethod("getMeid", null);
                String str = method2 != null ? (String) method2.invoke(telephonyManager, null) : null;
                if (d(str)) {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(str);
                    f1462p = str;
                    return arrayList;
                }
            } catch (Throwable th) {
                p.a(f1449c, "queryMeidList->getMeid failed ex: " + th.getMessage());
            }
        }
        return null;
    }

    private static boolean E(Context context) {
        return (context.getResources().getConfiguration().screenLayout & 15) >= 3;
    }

    private static boolean F(Context context) {
        return a.a(context) || (context.getResources().getConfiguration().uiMode & 15) == 4;
    }

    public static long a(long j2) {
        float f2 = j2;
        if (f2 > 900.0d) {
            f2 /= 1000.0f;
        }
        if (f2 > 900.0d) {
            f2 /= 1000.0f;
        }
        if (f2 > 900.0d) {
            f2 /= 1000.0f;
        }
        return (long) f2;
    }

    public static String b(Context context) {
        if (!TextUtils.isEmpty(f1467u)) {
            return f1467u;
        }
        String strA = a(context);
        if (TextUtils.isEmpty(strA)) {
            return "";
        }
        String strC = com.xiaomi.onetrack.d.d.c(strA);
        f1467u = strC;
        return strC;
    }

    private static long c(long j2) {
        long j3 = 1;
        long j4 = 1;
        while (true) {
            long j5 = j3 * j4;
            if (j5 >= j2) {
                return j5;
            }
            j3 <<= 1;
            if (j3 > 512) {
                j4 *= 1000;
                j3 = 1;
            }
        }
    }

    public static String d(Context context) {
        if (!TextUtils.isEmpty(A)) {
            return A;
        }
        String strA = a(context);
        if (TextUtils.isEmpty(strA)) {
            return "";
        }
        String strD = com.xiaomi.onetrack.d.d.d(strA);
        A = strD;
        return strD;
    }

    public static String e(Context context) {
        if (!TextUtils.isEmpty(f1461o)) {
            return f1461o;
        }
        r(context);
        return !TextUtils.isEmpty(f1461o) ? f1461o : "";
    }

    public static String f(Context context) {
        if (!TextUtils.isEmpty(f1468v)) {
            return f1468v;
        }
        String strE = e(context);
        if (TextUtils.isEmpty(strE)) {
            return "";
        }
        String strC = com.xiaomi.onetrack.d.d.c(strE);
        f1468v = strC;
        return strC;
    }

    public static String g(Context context) {
        if (!TextUtils.isEmpty(f1472z)) {
            return f1472z;
        }
        String strE = e(context);
        if (TextUtils.isEmpty(strE)) {
            return "";
        }
        String strE2 = com.xiaomi.onetrack.d.d.e(strE);
        f1472z = strE2;
        return strE2;
    }

    public static String h(Context context) {
        if (!TextUtils.isEmpty(B)) {
            return B;
        }
        String strE = e(context);
        if (TextUtils.isEmpty(strE)) {
            return "";
        }
        String strD = com.xiaomi.onetrack.d.d.d(strE);
        B = strD;
        return strD;
    }

    public static String i(Context context) {
        if (!TextUtils.isEmpty(f1469w)) {
            return f1469w;
        }
        String strA = A(context);
        if (TextUtils.isEmpty(strA)) {
            return "";
        }
        String strC = com.xiaomi.onetrack.d.d.c(strA);
        f1469w = strC;
        return strC;
    }

    public static String j(Context context) {
        if (!TextUtils.isEmpty(C)) {
            return C;
        }
        String strA = A(context);
        if (TextUtils.isEmpty(strA)) {
            return "";
        }
        String strE = com.xiaomi.onetrack.d.d.e(strA);
        C = strE;
        return strE;
    }

    public static String k(Context context) throws Throwable {
        if (!TextUtils.isEmpty(f1463q)) {
            return f1463q;
        }
        String strB = a.b(context);
        if (TextUtils.isEmpty(strB)) {
            return "";
        }
        f1463q = strB;
        return strB;
    }

    public static String l(Context context) {
        if (!TextUtils.isEmpty(f1464r)) {
            return f1464r;
        }
        String strU = u(context);
        if (TextUtils.isEmpty(strU)) {
            return "";
        }
        f1464r = strU;
        return strU;
    }

    public static String m(Context context) {
        if (!TextUtils.isEmpty(f1470x)) {
            return f1470x;
        }
        String strL = l(context);
        if (TextUtils.isEmpty(strL)) {
            return "";
        }
        String strC = com.xiaomi.onetrack.d.d.c(strL);
        f1470x = strC;
        return strC;
    }

    public static String n(Context context) {
        if (!TextUtils.isEmpty(E)) {
            return E;
        }
        String strL = l(context);
        if (TextUtils.isEmpty(strL)) {
            return "";
        }
        String strE = com.xiaomi.onetrack.d.d.e(strL);
        E = strE;
        return strE;
    }

    private static boolean o() {
        return false;
    }

    public static String p(Context context) {
        if (!TextUtils.isEmpty(f1466t)) {
            return f1466t;
        }
        if (GAIDClient.b(context)) {
            return "";
        }
        String strA = GAIDClient.a(context);
        if (TextUtils.isEmpty(strA)) {
            return "";
        }
        f1466t = strA;
        return strA;
    }

    public static List<String> q(Context context) {
        List<String> listR = r(context);
        ArrayList arrayList = new ArrayList();
        if (listR != null && !listR.isEmpty()) {
            for (int i2 = 0; i2 < listR.size(); i2++) {
                if (!TextUtils.isEmpty(listR.get(i2))) {
                    arrayList.add(i2, com.xiaomi.onetrack.d.d.c(listR.get(i2)));
                }
            }
        }
        return arrayList;
    }

    @SuppressLint({"MissingPermission"})
    public static List<String> r(Context context) {
        List<String> listB = null;
        if (u.a(context)) {
            if (H) {
                return null;
            }
            List<String> listM = m();
            listB = (listM == null || listM.isEmpty()) ? B(context) : listM;
            H = true;
        }
        if (listB != null && !listB.isEmpty()) {
            Collections.sort(listB);
            f1460n = listB.get(0);
            if (listB.size() >= 2) {
                f1461o = listB.get(1);
            }
        }
        return listB;
    }

    public static String s(Context context) {
        try {
            List<String> listD = D(context);
            if (listD == null) {
                return "";
            }
            Collections.sort(listD, new h());
            for (int i2 = 0; i2 < listD.size(); i2++) {
                listD.set(i2, com.xiaomi.onetrack.d.d.h(listD.get(i2)));
            }
            return listD.toString();
        } catch (Throwable th) {
            p.b(f1449c, "getMeidListMd5 e", th);
            return "";
        }
    }

    @SuppressLint({"MissingPermission"})
    public static List<String> t(Context context) {
        if (!u.b(context)) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        try {
            if (!n()) {
                String subscriberId = ((TelephonyManager) context.getSystemService("phone")).getSubscriberId();
                if (e(subscriberId)) {
                    arrayList.add(subscriberId);
                }
                return arrayList;
            }
            Class<?> cls = Class.forName("android.telephony.TelephonyManager");
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
            SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService("telephony_subscription_service");
            Class<?> cls2 = Class.forName("android.telephony.SubscriptionManager");
            String str = b(cls, cls2, telephonyManager, subscriptionManager)[0];
            String str2 = b(cls, cls2, telephonyManager, subscriptionManager)[1];
            if (!e(str)) {
                str = "";
            }
            arrayList.add(str);
            if (!e(str2)) {
                str2 = "";
            }
            arrayList.add(str2);
            return arrayList;
        } catch (SecurityException unused) {
            p.a(f1449c, "getImsiList failed with on permission");
            return null;
        } catch (Throwable th) {
            p.b(f1449c, "getImsiList failed: " + th.getMessage());
            return null;
        }
    }

    @SuppressLint({"MissingPermission"})
    public static String u(Context context) {
        String str = null;
        if (u.b(context)) {
            try {
                Class[] clsArr = new Class[0];
                Method method = Class.forName("android.os.Build").getMethod("getSerial", null);
                if (method != null) {
                    str = (String) method.invoke(null, null);
                }
            } catch (Throwable th) {
                p.a(f1449c, "querySerial failed ex: " + th.getMessage());
            }
        }
        if (TextUtils.isEmpty(str) || "unknown".equals(str)) {
            return "";
        }
        f1464r = str;
        return str;
    }

    public static String v(Context context) {
        try {
            List<String> listT = t(context);
            if (listT == null) {
                return "";
            }
            for (int i2 = 0; i2 < listT.size(); i2++) {
                listT.set(i2, com.xiaomi.onetrack.d.d.h(listT.get(i2)));
            }
            return listT.toString();
        } catch (Throwable th) {
            p.b(p.a(f1449c), "getImeiListMd5 failed!", th);
            return "";
        }
    }

    public static String w(Context context) {
        if (!TextUtils.isEmpty(G)) {
            return G;
        }
        if (p()) {
            G = "Pad";
            return "Pad";
        }
        if (!F(context)) {
            return "Phone";
        }
        G = "Tv";
        return "Tv";
    }

    public static boolean x(Context context) {
        if (F == null) {
            F = Boolean.valueOf(a.a(context));
        }
        return F.booleanValue();
    }

    public static String y(Context context) {
        return com.xiaomi.onetrack.util.oaid.a.a().a(context);
    }

    public static String z(Context context) {
        Display defaultDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getRealSize(point);
        return String.format("%d*%d", Integer.valueOf(point.y), Integer.valueOf(point.x));
    }

    public static String c(Context context) {
        if (!TextUtils.isEmpty(f1471y)) {
            return f1471y;
        }
        String strA = a(context);
        if (TextUtils.isEmpty(strA)) {
            return "";
        }
        String strE = com.xiaomi.onetrack.d.d.e(strA);
        f1471y = strE;
        return strE;
    }

    public static String o(Context context) {
        if (!TextUtils.isEmpty(f1465s)) {
            return f1465s;
        }
        try {
            String type = context.getContentResolver().getType(Uri.parse("content://com.miui.analytics.server.AnalyticsProvider/aaid"));
            if (!TextUtils.isEmpty(type)) {
                f1465s = type;
                return type;
            }
            Object objInvoke = Class.forName("android.provider.MiuiSettings$Ad").getDeclaredMethod("getAaid", ContentResolver.class).invoke(null, context.getContentResolver());
            if (!(objInvoke instanceof String) || TextUtils.isEmpty((String) objInvoke)) {
                return "";
            }
            String str = (String) objInvoke;
            f1465s = str;
            return str;
        } catch (Throwable th) {
            p.a(f1449c, "getAaid failed ex: " + th.getMessage());
            return "";
        }
    }

    private static class a {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private static final String f1478a = "box";

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        private static final String f1479b = "tvbox";

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        private static final String f1480c = "projector";

        /* JADX INFO: renamed from: d, reason: collision with root package name */
        private static final String f1481d = "tv";

        /* JADX INFO: renamed from: e, reason: collision with root package name */
        private static final String f1482e = "mi_device_mac";

        /* JADX INFO: renamed from: f, reason: collision with root package name */
        private static Signature[] f1483f;

        /* JADX INFO: renamed from: g, reason: collision with root package name */
        private static final Signature f1484g = new Signature("3082033b30820223a003020102020900a07a328482f70d2a300d06092a864886f70d01010505003035310b30090603550406130255533113301106035504080c0a43616c69666f726e69613111300f06035504070c084d6f756e7461696e301e170d3133303430313033303831325a170d3430303831373033303831325a3035310b30090603550406130255533113301106035504080c0a43616c69666f726e69613111300f06035504070c084d6f756e7461696e30820120300d06092a864886f70d01010105000382010d00308201080282010100ac678c9234a0226edbeb75a43e8e18f632d8c8a094c087fffbbb0b5e4429d845e36bffbe2d7098e320855258aa777368c18c538f968063d5d61663dc946ab03acbb31d00a27d452e12e6d42865e27d6d0ad2d8b12cf3b3096a7ec66a21db2a6a697857fd4d29fb4cdf294b3371d7601f2e3f190c0164efa543897026c719b334808e4f612fe3a3da589115fc30f9ca89862feefdf31a9164ecb295dcf7767e673be2192dda64f88189fd6e6ebd62e572c7997c2385a0ea9292ec799dee8f87596fc73aa123fb6f577d09ac0c123179c3bdbc978c2fe6194eb9fa4ab3658bfe8b44040bb13fe7809409e622189379fbc63966ab36521793547b01673ecb5f15cf020103a350304e301d0603551d0e0416041447203684e562385ada79108c4c94c5055037592f301f0603551d2304183016801447203684e562385ada79108c4c94c5055037592f300c0603551d13040530030101ff300d06092a864886f70d010105050003820101008d530fe05c6fe694c7559ddb5dd2c556528dd3cad4f7580f439f9a90a4681d37ce246b9a6681bdd5a5437f0b8bba903e39bac309fc0e9ee5553681612e723e9ec4f6abab6b643b33013f09246a9b5db7703b96f838fb27b00612f5fcd431bea32f68350ae51a4a1d012c520c401db7cccc15a7b19c4310b0c3bfc625ce5744744d0b9eeb02b0a4e7d51ed59849ce580b9f7c3062c84b9a0b13cc211e1c916c289820266a610801e3316c915649804571b147beadbf88d3b517ee04121d40630853f2f2a506bb788620de9648faeacff568e5033a666316bc2046526674ed3de25ceefdc4ad3628f1a230fd41bf9ca9f6a078173850dba555768fe1c191483ad9");

        private a() {
        }

        static boolean a(Context context) {
            if (f1483f == null) {
                f1483f = new Signature[]{c(context)};
            }
            Signature signature = f1483f[0];
            return signature != null && signature.equals(f1484g);
        }

        public static String b(Context context) throws Throwable {
            String strA;
            try {
                String string = Settings.Global.getString(context.getContentResolver(), f1482e);
                if (!TextUtils.isEmpty(string)) {
                    return string;
                }
            } catch (Exception unused) {
            }
            try {
                String str = Build.PRODUCT;
                String strB = DeviceUtil.b("ro.product.model");
                boolean z2 = true;
                if ((!TextUtils.equals(f1481d, a()) || "batman".equals(str) || "conan".equals(str)) && !"augustrush".equals(str) && !"casablanca".equals(str)) {
                    z2 = false;
                }
                if (TextUtils.equals("me2", str)) {
                    strA = DeviceUtil.b("persist.service.bdroid.bdaddr");
                } else if ((TextUtils.equals("transformers", str) && TextUtils.equals("MiBOX4C", strB)) || TextUtils.equals("dolphin-zorro", str)) {
                    strA = a("/sys/class/net/wlan0/address");
                } else {
                    strA = z2 ? a("/sys/class/net/eth0/address") : a("ro.boot.btmac");
                }
                return !TextUtils.isEmpty(strA) ? strA.trim() : "";
            } catch (Exception e2) {
                p.a(DeviceUtil.f1449c, "getMiTvMac exception", e2);
                return "";
            }
        }

        private static Signature c(Context context) {
            try {
                return a(context.getPackageManager().getPackageInfo("android", 64));
            } catch (Exception unused) {
                return null;
            }
        }

        private static Signature a(PackageInfo packageInfo) {
            Signature[] signatureArr;
            if (packageInfo == null || (signatureArr = packageInfo.signatures) == null || signatureArr.length <= 0) {
                return null;
            }
            return signatureArr[0];
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r1v0 */
        /* JADX WARN: Type inference failed for: r1v1 */
        /* JADX WARN: Type inference failed for: r1v12 */
        /* JADX WARN: Type inference failed for: r1v13 */
        /* JADX WARN: Type inference failed for: r1v2, types: [java.io.Closeable] */
        /* JADX WARN: Type inference failed for: r1v4 */
        /* JADX WARN: Type inference failed for: r1v5 */
        private static String a(String str) throws Throwable {
            String string = "";
            ?? r1 = 0;
            StringBuilder sb = null;
            BufferedReader bufferedReader = null;
            try {
                try {
                    BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(new FileInputStream(str)), 512);
                    try {
                        String line = bufferedReader2.readLine();
                        if (line != null) {
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("");
                            sb2.append(line);
                            string = sb2.toString();
                            sb = sb2;
                        }
                        m.a(bufferedReader2);
                        r1 = sb;
                    } catch (Exception e2) {
                        e = e2;
                        bufferedReader = bufferedReader2;
                        p.b(DeviceUtil.f1449c, "catEntry exception", e);
                        m.a(bufferedReader);
                        r1 = bufferedReader;
                    } catch (Throwable th) {
                        th = th;
                        r1 = bufferedReader2;
                        m.a((Closeable) r1);
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (Exception e3) {
                e = e3;
            }
            return string;
        }

        private static String a() {
            try {
                Class<?> cls = Class.forName("mitv.common.ConfigurationManager");
                Class[] clsArr = new Class[0];
                Class[] clsArr2 = new Class[0];
                int i2 = Integer.parseInt(String.valueOf(cls.getMethod("getProductCategory", null).invoke(cls.getMethod("getInstance", null).invoke(cls, null), null)));
                Class<?> cls2 = Class.forName("mitv.tv.TvContext");
                if (i2 == Integer.parseInt(String.valueOf(a(cls2, "PRODUCT_CATEGORY_MITV")))) {
                    return f1481d;
                }
                if (i2 == Integer.parseInt(String.valueOf(a(cls2, "PRODUCT_CATEGORY_MIBOX")))) {
                    return f1478a;
                }
                if (i2 == Integer.parseInt(String.valueOf(a(cls2, "PRODUCT_CATEGORY_MITVBOX")))) {
                    return f1479b;
                }
                if (i2 != Integer.parseInt(String.valueOf(a(cls2, "PRODUCT_CATEGORY_MIPROJECTOR")))) {
                    return "";
                }
                return f1480c;
            } catch (Throwable th) {
                p.a(DeviceUtil.f1449c, "getMiTvProductCategory exception: " + th.getMessage());
                return "";
            }
        }

        private static <T> T a(Class<?> cls, String str) {
            try {
                Field declaredField = cls.getDeclaredField(str);
                declaredField.setAccessible(true);
                return (T) declaredField.get(null);
            } catch (Exception e2) {
                p.b(DeviceUtil.f1449c, "getStaticVariableValue exception", e2);
                return null;
            }
        }
    }

    public static String a(Context context) {
        if (!TextUtils.isEmpty(f1460n)) {
            return f1460n;
        }
        r(context);
        if (!TextUtils.isEmpty(f1460n)) {
            return f1460n;
        }
        return "";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String b(String str) {
        try {
            Method method = f1454h;
            if (method != null) {
                return String.valueOf(method.invoke(null, str));
            }
        } catch (Exception e2) {
            p.a(f1449c, "getProp failed ex: " + e2.getMessage());
        }
        return null;
    }

    private static boolean d(String str) {
        return (str == null || str.length() != 14 || str.matches("^0*$")) ? false : true;
    }

    private static boolean e(String str) {
        return str != null && str.length() >= 6 && str.length() <= 15 && !str.matches("^0*$");
    }

    public static String f() {
        try {
            String strB = b("ro.product.model.real");
            return TextUtils.isEmpty(strB) ? c() : strB;
        } catch (Exception e2) {
            p.b(f1449c, "getModelReal Exception: ", e2);
            return "";
        }
    }

    public static String g() {
        try {
            String strA = ab.a("ro.product.mod_device", "");
            return TextUtils.isEmpty(strA) ? Build.DEVICE : strA;
        } catch (Exception e2) {
            p.b(f1449c, "getModDevice Exception: ", e2);
            return "";
        }
    }

    public static String h() {
        return ab.a(f1451e, "");
    }

    public static String i() {
        try {
            return ab.a("ro.product.manufacturer", "");
        } catch (Exception e2) {
            p.b(p.a(f1449c), "getProductManufacturer e", e2);
            return "";
        }
    }

    public static long j() {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        return (statFs.getBlockSizeLong() * statFs.getBlockCountLong()) / 1073741824;
    }

    public static String k() {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        return String.format("%dGB", Long.valueOf(b(statFs.getBlockSizeLong() * statFs.getBlockCountLong())));
    }

    public static long l() {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        return a(statFs.getBlockSizeLong() * statFs.getAvailableBlocksLong());
    }

    private static List<String> m() {
        if (f1455i != null && !o()) {
            try {
                List<String> list = (List) f1455i.invoke(f1457k, null);
                if (list != null && list.size() > 0 && !a(list)) {
                    return list;
                }
            } catch (Exception e2) {
                p.a(f1449c, "getImeiListFromMiui failed ex: " + e2.getMessage());
            }
        }
        return null;
    }

    private static boolean n() {
        if ("dsds".equals(b("persist.radio.multisim.config"))) {
            return true;
        }
        String str = Build.DEVICE;
        return "lcsh92_wet_jb9".equals(str) || "lcsh92_wet_tdd".equals(str) || "HM2013022".equals(str) || "HM2013023".equals(str) || "armani".equals(str) || "HM2014011".equals(str) || "HM2014012".equals(str);
    }

    private static boolean p() {
        try {
            return ((Boolean) Class.forName("miui.os.Build").getField("IS_TABLET").get(null)).booleanValue();
        } catch (Throwable th) {
            p.b(f1449c, "get miui.os.Build Exception: " + th.getMessage());
            try {
                return ((Boolean) Class.forName("miui.util.FeatureParser").getMethod("getBoolean", String.class, Boolean.TYPE).invoke(null, "is_pad", Boolean.FALSE)).booleanValue();
            } catch (Throwable th2) {
                p.b(f1449c, "get miui.util.FeatureParser Exception: " + th2.getMessage());
                return false;
            }
        }
    }

    private static boolean c(String str) {
        return (str == null || str.length() != 15 || str.matches("^0*$")) ? false : true;
    }

    public static String d() {
        return b("ro.product.marketname");
    }

    public static String e() {
        return Build.MANUFACTURER;
    }

    public static void a() {
        f1466t = null;
    }

    public static String c() {
        return Build.MODEL;
    }

    private static boolean a(List<String> list) {
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            if (!c(it.next())) {
                return true;
            }
        }
        return false;
    }

    private static boolean b(List<String> list) {
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            if (!d(it.next())) {
                return true;
            }
        }
        return false;
    }

    private static String[] a(Class<?> cls, Class<?> cls2, TelephonyManager telephonyManager, SubscriptionManager subscriptionManager) {
        Method method;
        Object obj;
        String[] strArr = new String[2];
        try {
            Class<?> cls3 = Integer.TYPE;
            String str = (String) cls.getMethod("getSubscriberId", cls3).invoke(telephonyManager, Integer.valueOf(((int[]) cls2.getMethod("getSubId", cls3).invoke(subscriptionManager, 0))[0]));
            strArr[0] = str;
            if (e(str) || (method = f1459m) == null || (obj = f1457k) == null) {
                strArr[1] = (String) cls.getMethod("getSubscriberId", cls3).invoke(telephonyManager, Integer.valueOf(((int[]) cls2.getMethod("getSubId", cls3).invoke(subscriptionManager, 1))[0]));
            } else {
                strArr[0] = (String) method.invoke(obj, 0);
                strArr[1] = (String) f1459m.invoke(f1457k, 1);
            }
        } catch (Exception e2) {
            p.a(f1449c, "getImsiFromLToP: " + e2);
        }
        return strArr;
    }

    private static String[] b(Class<?> cls, Class<?> cls2, TelephonyManager telephonyManager, SubscriptionManager subscriptionManager) {
        String[] strArr = new String[2];
        try {
            Class<?> cls3 = Integer.TYPE;
            int[] iArr = (int[]) cls2.getMethod("getSubscriptionIds", cls3).invoke(subscriptionManager, 0);
            if (iArr != null) {
                strArr[0] = (String) cls.getMethod("getSubscriberId", cls3).invoke(telephonyManager, Integer.valueOf(iArr[0]));
            }
        } catch (Exception e2) {
            p.b(f1449c, "get imsi1 above Android Q exception:" + e2);
        }
        try {
            Class<?> cls4 = Integer.TYPE;
            int[] iArr2 = (int[]) cls2.getMethod("getSubscriptionIds", cls4).invoke(subscriptionManager, 1);
            if (iArr2 != null) {
                strArr[1] = (String) cls.getMethod("getSubscriberId", cls4).invoke(telephonyManager, Integer.valueOf(iArr2[0]));
            }
        } catch (Exception e3) {
            p.b(f1449c, "get imsi2 above Android Q exception:" + e3);
        }
        return strArr;
    }

    public static String b() {
        return b("ro.product.name");
    }

    private static long b(long j2) {
        long jPow;
        if (j2 >= 8000000000L) {
            jPow = (long) Math.pow(2.0d, Math.ceil(Math.log(j2 / 1000000000) / Math.log(2.0d)));
        } else {
            jPow = (j2 / 1000000000) + 1;
        }
        long jC = jPow * 1000000000;
        if (jC < j2) {
            jC = c(j2);
        }
        return jC / 1000000000;
    }
}
