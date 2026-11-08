package com.ot.pubsub.util;

import android.content.Context;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import java.util.HashSet;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class p {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static Set<String> f764a;

    static {
        try {
            HashSet hashSet = new HashSet();
            f764a = hashSet;
            hashSet.add("android");
            f764a.add("com.miui.analytics");
            f764a.add("com.miui.cit");
            f764a.add("com.xiaomi.finddevice");
            f764a.add("com.miui.securitycenter");
            f764a.add("com.android.settings");
            f764a.add("com.android.vending");
            f764a.add("com.google.android.gms");
            f764a.add("com.xiaomi.factory.mmi");
            f764a.add("com.miui.qr");
            f764a.add("com.android.contacts");
            f764a.add("com.qualcomm.qti.autoregistration");
            f764a.add("com.miui.tsmclient");
            f764a.add("com.miui.sekeytool");
            f764a.add("com.android.updater");
            if (!"cn_chinamobile".equals(u.a("ro.miui.cust_variant")) && !"cn_chinatelecom".equals(u.a("ro.miui.cust_variant"))) {
                return;
            }
            f764a.add("com.mobiletools.systemhelper");
            f764a.add("com.miui.dmregservice");
        } catch (Exception e2) {
            Log.e("PermissionUtil", "static initializer: " + e2.toString());
        }
    }

    private static boolean a() {
        try {
            return m.e() && !m.n() && "1".equals(u.a("ro.miui.restrict_imei"));
        } catch (Exception e2) {
            k.h("PermissionUtil", "isRestrictIMEI " + e2.toString());
            return false;
        }
    }

    public static boolean b(Context context) {
        if (a()) {
            return c(b.e()) && d(context, "android.permission.READ_PRIVILEGED_PHONE_STATE");
        }
        return d(context, "android.permission.READ_PRIVILEGED_PHONE_STATE");
    }

    private static boolean c(String str) {
        Set<String> set;
        return (TextUtils.isEmpty(str) || (set = f764a) == null || !set.contains(str)) ? false : true;
    }

    public static boolean d(Context context, String str) {
        return context.checkPermission(str, Process.myPid(), Process.myUid()) == 0;
    }
}
