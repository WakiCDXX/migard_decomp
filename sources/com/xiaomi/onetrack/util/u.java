package com.xiaomi.onetrack.util;

import android.content.Context;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import java.util.HashSet;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class u {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final String f1702a = "PermissionUtil";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static final String f1703b = "android.permission.READ_PRIVILEGED_PHONE_STATE";

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static Set<String> f1704c;

    static {
        try {
            HashSet hashSet = new HashSet();
            f1704c = hashSet;
            hashSet.add("android");
            f1704c.add("com.miui.analytics");
            f1704c.add("com.miui.cit");
            f1704c.add("com.xiaomi.finddevice");
            f1704c.add("com.miui.securitycenter");
            f1704c.add("com.android.settings");
            f1704c.add("com.android.vending");
            f1704c.add("com.google.android.gms");
            f1704c.add("com.xiaomi.factory.mmi");
            f1704c.add("com.miui.qr");
            f1704c.add("com.android.contacts");
            f1704c.add("com.qualcomm.qti.autoregistration");
            f1704c.add("com.miui.tsmclient");
            f1704c.add("com.miui.sekeytool");
            f1704c.add("com.android.updater");
            if (!"cn_chinamobile".equals(ab.a("ro.miui.cust_variant")) && !"cn_chinatelecom".equals(ab.a("ro.miui.cust_variant"))) {
                return;
            }
            f1704c.add("com.mobiletools.systemhelper");
            f1704c.add("com.miui.dmregservice");
        } catch (Exception e2) {
            Log.e(f1702a, "static initializer: " + e2.toString());
        }
    }

    public static boolean a(Context context) {
        if (a()) {
            return a(com.xiaomi.onetrack.f.a.e()) && a(context, f1703b);
        }
        return a(context, f1703b);
    }

    public static boolean b(Context context) {
        return a(context, f1703b);
    }

    private static boolean a() {
        try {
            return q.a() && !q.h() && "1".equals(ab.a("ro.miui.restrict_imei"));
        } catch (Exception e2) {
            p.b(f1702a, "isRestrictIMEI " + e2.toString());
            return false;
        }
    }

    private static boolean a(String str) {
        Set<String> set;
        return (TextUtils.isEmpty(str) || (set = f1704c) == null || !set.contains(str)) ? false : true;
    }

    private static boolean a(Context context, String str) {
        return context.checkPermission(str, Process.myPid(), Process.myUid()) == 0;
    }
}
