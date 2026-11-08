package com.xiaomi.onetrack.util;

import android.content.Context;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes.dex */
public class n {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final String f1573a = "IdentifierManager";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static Object f1574b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static Class<?> f1575c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static Method f1576d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static Method f1577e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static Method f1578f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private static Method f1579g;

    static {
        try {
            Class<?> cls = Class.forName("com.android.id.impl.IdProviderImpl");
            f1575c = cls;
            f1574b = cls.newInstance();
            f1576d = f1575c.getMethod("getUDID", Context.class);
            f1577e = f1575c.getMethod("getOAID", Context.class);
            f1578f = f1575c.getMethod("getVAID", Context.class);
            f1579g = f1575c.getMethod("getAAID", Context.class);
        } catch (Exception e2) {
            p.a(f1573a, "reflect exception!", e2);
        }
    }

    public static boolean a() {
        return (f1575c == null || f1574b == null) ? false : true;
    }

    public static String b(Context context) {
        return a(context, f1577e);
    }

    public static String c(Context context) {
        return a(context, f1578f);
    }

    public static String d(Context context) {
        return a(context, f1579g);
    }

    public static String a(Context context) {
        return a(context, f1576d);
    }

    private static String a(Context context, Method method) {
        Object obj = f1574b;
        if (obj != null && method != null) {
            try {
                Object objInvoke = method.invoke(obj, context);
                if (objInvoke != null) {
                    return (String) objInvoke;
                }
                return "";
            } catch (Exception e2) {
                p.a(f1573a, "invoke exception!", e2);
                return "";
            }
        }
        return "";
    }
}
