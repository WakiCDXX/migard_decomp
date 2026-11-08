package com.xiaomi.onetrack.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/* JADX INFO: loaded from: classes.dex */
public class aa {
    private static final String A = "onetrack_user_id";
    private static final String B = "onetrack_user_type";
    private static final String C = "custom_id";
    private static final String D = "page_end";
    private static final String E = "last_app_version";
    private static final String F = "first_launch_time";
    private static final String G = "pref_custom_privacy_policy_";

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final String f1493a = "onetrack";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static final String f1494b = "one_track_pref";

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static SharedPreferences f1495c = null;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static SharedPreferences.Editor f1496d = null;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static final String f1497e = "last_upload_active_time";

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static final String f1498f = "last_upload_usage_time";

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private static final String f1499g = "last_collect_crash_time";

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private static final String f1500h = "report_crash_ticket";

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private static final String f1501i = "last_secret_key_time";

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private static final String f1502j = "last_common_cloud_time";

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private static final String f1503k = "next_update_common_conf_time";

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    private static final String f1504l = "common_cloud_data";

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    private static final String f1505m = "common_config_hash";

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    private static final String f1506n = "s_t";

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    private static final String f1507o = "l_t";

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    private static final String f1508p = "e_t";

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    private static final String f1509q = "secret_key_data";

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    private static final String f1510r = "region_rul";

    /* JADX INFO: renamed from: s, reason: collision with root package name */
    private static final String f1511s = "pref_instance_id";

    /* JADX INFO: renamed from: t, reason: collision with root package name */
    private static final String f1512t = "pref_instance_id_last_use_time";

    /* JADX INFO: renamed from: u, reason: collision with root package name */
    private static final String f1513u = "last_usage_resume_component";

    /* JADX INFO: renamed from: v, reason: collision with root package name */
    private static final String f1514v = "last_usage_resume_time";

    /* JADX INFO: renamed from: w, reason: collision with root package name */
    private static final String f1515w = "network_access_enabled";

    /* JADX INFO: renamed from: x, reason: collision with root package name */
    private static final String f1516x = "anonymous_enabled";

    /* JADX INFO: renamed from: y, reason: collision with root package name */
    private static final String f1517y = "onetrack_first_open";

    /* JADX INFO: renamed from: z, reason: collision with root package name */
    private static final String f1518z = "dau_last_time";

    public static String A() {
        return a(E, "");
    }

    public static long B() {
        return a(F, 0L);
    }

    private static void C() {
        if (f1496d != null) {
            return;
        }
        synchronized (aa.class) {
            try {
                if (f1496d == null) {
                    SharedPreferences sharedPreferences = com.xiaomi.onetrack.f.a.a().getSharedPreferences(f1494b, 0);
                    f1495c = sharedPreferences;
                    f1496d = sharedPreferences.edit();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private static String a(String str, String str2) {
        C();
        return f1495c.getString(str, str2);
    }

    private static void b(String str, String str2) {
        C();
        f1496d.putString(str, str2).apply();
    }

    private static void c(String str, boolean z2) {
        C();
        f1496d.putBoolean(str, z2).apply();
    }

    public static void d(long j2) {
        b(f1500h, j2);
    }

    public static void e(long j2) {
        b(f1498f, j2);
    }

    public static void f(long j2) {
        b(f1506n, j2);
    }

    public static void g(long j2) {
        b(f1507o, j2);
    }

    public static void h(long j2) {
        b(f1508p, j2);
    }

    public static void i(long j2) {
        b(f1501i, j2);
    }

    public static void j(long j2) {
        b(f1503k, j2);
    }

    public static String k() {
        return a(f1505m, "");
    }

    private static void l(String str) {
        C();
        f1496d.remove(str).apply();
    }

    public static String m() {
        return a(f1511s, "");
    }

    public static long n() {
        return a(f1512t, 0L);
    }

    public static String o() {
        return a(f1513u, "");
    }

    public static long p() {
        return a(f1514v, 0L);
    }

    public static boolean q() {
        return b(f1515w, true);
    }

    public static boolean r() {
        return b(f1516x, false);
    }

    public static boolean s() {
        return b(f1517y, true);
    }

    public static long t() {
        return a(f1518z, 0L);
    }

    public static String u() {
        return a(A, "");
    }

    public static void v() {
        l(A);
    }

    public static String w() {
        return a(B, "");
    }

    public static void x() {
        l(B);
    }

    public static long y() {
        return a(f1515w, 0L);
    }

    public static String z() {
        return a(D, "");
    }

    public static long d() {
        return a(f1506n, 0L);
    }

    public static long e() {
        return a(f1507o, 0L);
    }

    public static long f() {
        return a(f1508p, 0L);
    }

    public static String g() {
        return a(f1509q, "");
    }

    public static String h() {
        return a(f1510r, "");
    }

    public static long i() {
        return a(f1501i, 0L);
    }

    public static long j() {
        return a(f1503k, 0L);
    }

    public static void k(long j2) {
        b(f1512t, j2);
    }

    public static void m(long j2) {
        b(f1518z, j2);
    }

    public static void n(long j2) {
        b(F, j2);
    }

    private static long a(String str, long j2) {
        C();
        return f1495c.getLong(str, j2);
    }

    private static void b(String str, long j2) {
        C();
        f1496d.putLong(str, j2).apply();
    }

    public static void c(long j2) {
        b(f1499g, j2);
    }

    public static void d(String str) {
        b(f1504l, str);
    }

    public static void e(String str) {
        b(f1511s, str);
        k(ac.a());
    }

    public static void f(String str) {
        b(f1513u, str);
    }

    public static void g(String str) {
        b(A, str);
    }

    public static void h(String str) {
        b(B, str);
    }

    public static void i(String str) {
        b(D, str);
    }

    public static void j(String str) {
        b(E, str);
    }

    public static boolean k(String str) {
        if (TextUtils.isEmpty(str)) {
            return true;
        }
        return b(G + str, true);
    }

    public static String l() {
        return a(f1504l, "");
    }

    public static long c() {
        return a(f1500h, 0L);
    }

    public static void l(long j2) {
        b(f1514v, j2);
    }

    private static void a(String str, float f2) {
        C();
        f1496d.putFloat(str, f2).apply();
    }

    private static boolean b(String str, boolean z2) {
        C();
        return f1495c.getBoolean(str, z2);
    }

    public static void c(String str) {
        b(f1505m, str);
    }

    public static void c(boolean z2) {
        c(f1517y, z2);
    }

    public static long a() {
        return a(f1497e, 0L);
    }

    private static float b(String str, float f2) {
        C();
        return f1495c.getFloat(str, f2);
    }

    public static void a(long j2) {
        b(f1497e, j2);
    }

    public static void a(String str) {
        b(f1509q, str);
    }

    public static long b(long j2) {
        return a(f1498f, j2);
    }

    public static void a(boolean z2) {
        c(f1515w, z2);
    }

    public static long b() {
        return a(f1499g, 0L);
    }

    public static void a(Context context, String str) {
        b(C, str);
    }

    public static void b(String str) {
        b(f1510r, str);
    }

    public static String a(Context context) {
        return a(C, "");
    }

    public static void b(boolean z2) {
        c(f1516x, z2);
    }

    public static void a(String str, boolean z2) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        c(G + str, z2);
    }
}
