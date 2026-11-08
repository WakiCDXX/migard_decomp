package com.xiaomi.onetrack.util;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.os.Process;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.xiaomi.onetrack.OneTrack;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.TimeZone;
import miui.provider.ExtraSettings;

/* JADX INFO: loaded from: classes.dex */
public class q {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final int f1679a = 29;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final int f1680b = 25;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final int f1681c = 24;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final int f1682d = 23;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final int f1683e = 22;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public static final int f1684f = 21;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public static final int f1685g = 19;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public static final int f1686h = 17;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final int f1687i = 28;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private static final String f1688j = "OsUtil";

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private static Class f1689k = null;

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    private static Method f1690l = null;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    private static Boolean f1691m = null;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    private static final String f1692n = "";

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    private static Method f1693o = null;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    private static boolean f1694p = false;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    private static String f1695q = null;

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    private static boolean f1696r = false;

    /* JADX INFO: renamed from: s, reason: collision with root package name */
    private static int f1697s;

    static {
        try {
            f1693o = Class.forName("android.os.SystemProperties").getMethod("get", String.class);
        } catch (Throwable th) {
            p.b(f1688j, "sGetProp init failed ex: " + th.getMessage());
        }
        try {
            f1689k = Class.forName("miui.os.Build");
        } catch (Throwable th2) {
            p.b(f1688j, "sMiuiBuild init failed ex: " + th2.getMessage());
        }
        try {
            Method declaredMethod = Class.forName("android.provider.MiuiSettings$Secure").getDeclaredMethod("isUserExperienceProgramEnable", ContentResolver.class);
            f1690l = declaredMethod;
            declaredMethod.setAccessible(true);
        } catch (Throwable th3) {
            p.b(f1688j, "sMiuiUEPMethod init failed ex: " + th3.getMessage());
        }
    }

    public static boolean a() {
        Boolean bool = f1691m;
        if (bool != null) {
            return bool.booleanValue();
        }
        if (TextUtils.isEmpty(b("ro.miui.ui.version.code"))) {
            f1691m = Boolean.FALSE;
        } else {
            f1691m = Boolean.TRUE;
        }
        return f1691m.booleanValue();
    }

    private static String b(String str) {
        try {
            Method method = f1693o;
            if (method != null) {
                return String.valueOf(method.invoke(null, str));
            }
        } catch (Exception e2) {
            p.b(f1688j, "getProp failed ex: " + e2.getMessage());
        }
        return null;
    }

    public static String c() {
        Class cls = f1689k;
        if (cls == null) {
            return "";
        }
        try {
            if (((Boolean) cls.getField("IS_ALPHA_BUILD").get(null)).booleanValue()) {
                return "A";
            }
            if (((Boolean) f1689k.getField("IS_STABLE_VERSION").get(null)).booleanValue()) {
                return "S";
            }
            boolean zContains = Build.VERSION.INCREMENTAL.contains(".DEV");
            boolean zBooleanValue = ((Boolean) f1689k.getField("IS_DEVELOPMENT_VERSION").get(null)).booleanValue();
            if (!zBooleanValue || zContains) {
                return (zBooleanValue && zContains) ? "X" : "";
            }
            return "D";
        } catch (Exception e2) {
            Log.e(f1688j, "getRomBuildCode failed: " + e2.toString());
            return "";
        }
    }

    public static String d() {
        return Build.VERSION.INCREMENTAL;
    }

    public static String e() {
        return Build.VERSION.RELEASE;
    }

    public static int f() {
        Integer num;
        Throwable th;
        try {
            Method declaredMethod = Class.forName("android.os.UserHandle").getDeclaredMethod("getUserId", Integer.TYPE);
            declaredMethod.setAccessible(true);
            int iMyUid = Process.myUid();
            num = (Integer) declaredMethod.invoke(null, Integer.valueOf(iMyUid));
            try {
                p.a(f1688j, String.format("getUserId, uid:%d, userId:%d", Integer.valueOf(iMyUid), num));
            } catch (Throwable th2) {
                th = th2;
                Log.e(p.a(f1688j), "getUserId exception: " + th.getMessage());
            }
        } catch (Throwable th3) {
            num = null;
            th = th3;
        }
        if (num == null) {
            num = 0;
        }
        return num.intValue();
    }

    @TargetApi(17)
    public static boolean g() {
        try {
            boolean z2 = Settings.Global.getInt(com.xiaomi.onetrack.f.a.b().getContentResolver(), "device_provisioned", 0) != 0;
            if (!z2) {
                p.c(f1688j, "Provisioned: " + z2);
            }
            return z2;
        } catch (Exception e2) {
            p.b(f1688j, "isDeviceProvisioned exception", e2);
            return true;
        }
    }

    public static boolean h() {
        return (!a() || f1696r) ? f1694p : l();
    }

    public static String i() {
        if (!a() || f1696r) {
            return !TextUtils.isEmpty(f1695q) ? f1695q : k();
        }
        return k();
    }

    public static String j() {
        return k();
    }

    private static String k() {
        try {
            String strA = ab.a("ro.miui.region", "");
            if (TextUtils.isEmpty(strA)) {
                strA = ab.a("ro.product.locale.region", "");
            }
            if (TextUtils.isEmpty(strA)) {
                Class[] clsArr = new Class[0];
                Object objInvoke = Class.forName("android.os.LocaleList").getMethod("getDefault", null).invoke(null, null);
                Class[] clsArr2 = new Class[0];
                Object objInvoke2 = objInvoke.getClass().getMethod("size", null).invoke(objInvoke, null);
                if ((objInvoke2 instanceof Integer) && ((Integer) objInvoke2).intValue() > 0) {
                    Object objInvoke3 = objInvoke.getClass().getMethod("get", Integer.TYPE).invoke(objInvoke, 0);
                    Class[] clsArr3 = new Class[0];
                    Object objInvoke4 = objInvoke3.getClass().getMethod("getCountry", null).invoke(objInvoke3, null);
                    if (objInvoke4 instanceof String) {
                        strA = (String) objInvoke4;
                    }
                }
            }
            if (TextUtils.isEmpty(strA)) {
                strA = Locale.getDefault().getCountry();
            }
            if (!TextUtils.isEmpty(strA)) {
                return strA.trim();
            }
        } catch (Throwable th) {
            p.b(f1688j, "getRegion Exception: " + th.getMessage());
        }
        return "";
    }

    private static boolean l() {
        Class cls = f1689k;
        if (cls != null) {
            try {
                return ((Boolean) cls.getField("IS_INTERNATIONAL_BUILD").get(null)).booleanValue();
            } catch (Exception unused) {
            }
        }
        String strK = k();
        if (TextUtils.isEmpty(strK)) {
            return false;
        }
        return !TextUtils.equals("CN", strK.toUpperCase());
    }

    public static String b() {
        return a(TimeZone.getDefault().getRawOffset());
    }

    public static String a(int i2) {
        char c2;
        try {
            int i3 = i2 / 60000;
            if (i3 < 0) {
                i3 = -i3;
                c2 = '-';
            } else {
                c2 = '+';
            }
            StringBuilder sb = new StringBuilder(9);
            sb.append("GMT");
            sb.append(c2);
            a(sb, i3 / 60);
            sb.append(':');
            a(sb, i3 % 60);
            return sb.toString();
        } catch (Exception unused) {
            return "";
        }
    }

    private static void a(StringBuilder sb, int i2) {
        String string = Integer.toString(i2);
        for (int i3 = 0; i3 < 2 - string.length(); i3++) {
            sb.append('0');
        }
        sb.append(string);
    }

    public static boolean a(Context context) {
        if (f1690l == null) {
            try {
                if (a()) {
                    int i2 = Settings.Secure.getInt(context.getContentResolver(), ExtraSettings.Secure.UPLOAD_LOG, -1);
                    p.a(f1688j, "isUserExperiencePlanEnabled upload_log_value: " + i2);
                    if (i2 != 1 && i2 == 0) {
                        return false;
                    }
                }
                return true;
            } catch (Throwable th) {
                p.a(f1688j, "Settings failed: " + th.toString());
            }
        }
        try {
            return ((Boolean) f1690l.invoke(null, context.getContentResolver())).booleanValue();
        } catch (Throwable th2) {
            Log.d(f1688j, "isUserExperiencePlanEnabled failed: " + th2.getMessage());
            return true;
        }
    }

    public static boolean a(String str) {
        if (!OneTrack.isDisable() && !OneTrack.isUseSystemNetTrafficOnly()) {
            if (!g()) {
                p.c(str, "should not access network or location, not provisioned");
                return true;
            }
            if (com.xiaomi.onetrack.c.i.b()) {
                return false;
            }
            p.c(str, "should not access network or location, cta");
            return true;
        }
        p.c(str, "should not access network or location, cta");
        return true;
    }

    public static void a(boolean z2, String str, OneTrack.Mode mode) {
        int i2;
        if (mode == OneTrack.Mode.APP) {
            i2 = 3;
        } else if (mode == OneTrack.Mode.PLUGIN) {
            i2 = 2;
        } else {
            i2 = mode == OneTrack.Mode.SDK ? 1 : 0;
        }
        if (f1697s <= i2) {
            f1694p = z2;
            f1695q = str;
            f1697s = i2;
        }
    }

    public static void a(boolean z2) {
        f1696r = z2;
    }
}
