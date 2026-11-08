package com.ot.pubsub.util;

import android.content.Context;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes.dex */
public class j {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static Object f718a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static Class<?> f719b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static Method f720c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static Method f721d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static Method f722e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static Method f723f;

    static {
        try {
            Class<?> cls = Class.forName("com.android.id.impl.IdProviderImpl");
            f719b = cls;
            f718a = cls.newInstance();
            f720c = f719b.getMethod("getUDID", Context.class);
            f721d = f719b.getMethod("getOAID", Context.class);
            f722e = f719b.getMethod("getVAID", Context.class);
            f723f = f719b.getMethod("getAAID", Context.class);
        } catch (Exception e2) {
            k.e("IdentifierManager", "reflect exception!", e2);
        }
    }

    private static String a(Context context, Method method) {
        Object obj = f718a;
        if (obj == null || method == null) {
            return "";
        }
        try {
            Object objInvoke = method.invoke(obj, context);
            return objInvoke != null ? (String) objInvoke : "";
        } catch (Exception e2) {
            k.e("IdentifierManager", "invoke exception!", e2);
            return "";
        }
    }

    public static String b(Context context) {
        return a(context, f721d);
    }
}
