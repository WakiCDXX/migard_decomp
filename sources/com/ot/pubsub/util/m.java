package com.ot.pubsub.util;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.os.Process;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes.dex */
public class m {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static Class f730a = null;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static Method f731b = null;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static Boolean f732c = null;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static boolean f733d = false;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static String f734e = null;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static boolean f735f = false;

    static {
        try {
            f730a = Class.forName("miui.os.Build");
        } catch (Throwable th) {
            k.h("OsUtil", "sMiuiBuild init failed ex: " + th.getMessage());
        }
        try {
            Method declaredMethod = Class.forName("android.provider.MiuiSettings$Secure").getDeclaredMethod("isUserExperienceProgramEnable", ContentResolver.class);
            f731b = declaredMethod;
            declaredMethod.setAccessible(true);
        } catch (Throwable th2) {
            k.h("OsUtil", "sMiuiUEPMethod init failed ex: " + th2.getMessage());
        }
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
            b(sb, i3 / 60);
            sb.append(':');
            b(sb, i3 % 60);
            return sb.toString();
        } catch (Exception unused) {
            return "";
        }
    }

    private static void b(StringBuilder sb, int i2) {
        String string = Integer.toString(i2);
        for (int i3 = 0; i3 < 2 - string.length(); i3++) {
            sb.append('0');
        }
        sb.append(string);
    }

    public static void c(boolean z2) {
        f735f = z2;
    }

    public static void d(boolean z2, String str) {
        f733d = z2;
        f734e = str;
    }

    public static boolean e() {
        Boolean bool = f732c;
        if (bool != null) {
            return bool.booleanValue();
        }
        if (TextUtils.isEmpty(u.a("ro.miui.ui.version.code"))) {
            f732c = Boolean.FALSE;
        } else {
            f732c = Boolean.TRUE;
        }
        return f732c.booleanValue();
    }

    public static String f() {
        return a(TimeZone.getDefault().getRawOffset());
    }

    public static boolean g(Context context) {
        Method method = f731b;
        if (method == null) {
            return true;
        }
        try {
            return ((Boolean) method.invoke(null, context.getContentResolver())).booleanValue();
        } catch (Exception e2) {
            Log.e("OsUtil", "isUserExperiencePlanEnabled failed: " + e2.toString());
            return true;
        }
    }

    public static String h() {
        Class cls = f730a;
        if (cls == null) {
            return "";
        }
        try {
            if (((Boolean) cls.getField("IS_ALPHA_BUILD").get(null)).booleanValue()) {
                return "A";
            }
            if (((Boolean) f730a.getField("IS_DEVELOPMENT_VERSION").get(null)).booleanValue()) {
                return "D";
            }
            return ((Boolean) f730a.getField("IS_STABLE_VERSION").get(null)).booleanValue() ? "S" : "";
        } catch (Exception e2) {
            Log.e("OsUtil", "getRomBuildCode failed: " + e2.toString());
            return "";
        }
    }

    public static boolean i(String str) {
        if (m()) {
            return false;
        }
        k.k(str, "should not access network or location, not provisioned");
        return true;
    }

    public static String j() {
        return Build.VERSION.INCREMENTAL;
    }

    public static String k() {
        return Build.VERSION.RELEASE;
    }

    public static int l() {
        Integer num;
        Throwable th;
        try {
            Method declaredMethod = Class.forName("android.os.UserHandle").getDeclaredMethod("getUserId", Integer.TYPE);
            declaredMethod.setAccessible(true);
            int iMyUid = Process.myUid();
            num = (Integer) declaredMethod.invoke(null, Integer.valueOf(iMyUid));
            try {
                k.c("OsUtil", String.format("getUserId, uid:%d, userId:%d", Integer.valueOf(iMyUid), num));
            } catch (Throwable th2) {
                th = th2;
                Log.e(k.a("OsUtil"), "getUserId exception: " + th.getMessage());
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
    public static boolean m() {
        try {
            return Settings.Global.getInt(b.c().getContentResolver(), "device_provisioned", 0) != 0;
        } catch (Exception e2) {
            k.i("OsUtil", "isDeviceProvisioned exception：", e2);
            return true;
        }
    }

    public static boolean n() {
        return (!e() || f735f) ? f733d : q();
    }

    public static String o() {
        if (!e() || f735f) {
            return !TextUtils.isEmpty(f734e) ? f734e : p();
        }
        return p();
    }

    private static String p() {
        try {
            String strB = u.b("ro.miui.region", "");
            if (TextUtils.isEmpty(strB)) {
                strB = u.b("ro.product.locale.region", "");
            }
            if (TextUtils.isEmpty(strB)) {
                Class[] clsArr = new Class[0];
                Object objInvoke = Class.forName("android.os.LocaleList").getMethod("getDefault", null).invoke(null, null);
                Class[] clsArr2 = new Class[0];
                Object objInvoke2 = objInvoke.getClass().getMethod("size", null).invoke(objInvoke, null);
                if ((objInvoke2 instanceof Integer) && ((Integer) objInvoke2).intValue() > 0) {
                    Object objInvoke3 = objInvoke.getClass().getMethod("get", Integer.TYPE).invoke(objInvoke, 0);
                    Class[] clsArr3 = new Class[0];
                    Object objInvoke4 = objInvoke3.getClass().getMethod("getCountry", null).invoke(objInvoke3, null);
                    if (objInvoke4 instanceof String) {
                        strB = (String) objInvoke4;
                    }
                }
            }
            if (TextUtils.isEmpty(strB)) {
                strB = Locale.getDefault().getCountry();
            }
            if (!TextUtils.isEmpty(strB)) {
                return strB.trim();
            }
        } catch (Throwable th) {
            k.h("OsUtil", "getRegion Exception: " + th.getMessage());
        }
        return "";
    }

    private static boolean q() {
        Class cls = f730a;
        if (cls != null) {
            try {
                return ((Boolean) cls.getField("IS_INTERNATIONAL_BUILD").get(null)).booleanValue();
            } catch (Exception unused) {
            }
        }
        String strP = p();
        if (TextUtils.isEmpty(strP)) {
            return false;
        }
        return !TextUtils.equals("CN", strP.toUpperCase());
    }
}
