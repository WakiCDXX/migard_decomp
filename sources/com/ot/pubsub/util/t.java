package com.ot.pubsub.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/* JADX INFO: loaded from: classes.dex */
public class t {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static SharedPreferences f770a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static SharedPreferences.Editor f771b;

    private static long a(String str, long j2) {
        z();
        return f770a.getLong(str, j2);
    }

    public static String b(Context context) {
        return c("custom_id", "");
    }

    private static String c(String str, String str2) {
        z();
        return f770a.getString(str, str2);
    }

    public static void d(long j2) {
        i("pref_instance_id_last_use_time", j2);
    }

    public static void e(String str) {
        j("loc_token", str);
    }

    public static String f() {
        return c("loc_token", "");
    }

    public static void g(long j2) {
        i("last_secret_key_time", j2);
    }

    public static void h(String str) {
        j("loc_config", str);
    }

    private static void i(String str, long j2) {
        z();
        f771b.putLong(str, j2).apply();
    }

    private static void j(String str, String str2) {
        z();
        f771b.putString(str, str2).apply();
    }

    private static boolean k(String str, boolean z2) {
        z();
        return f770a.getBoolean(str, z2);
    }

    public static String l() {
        return c("loc_config", "");
    }

    public static void m(long j2) {
        i("first_launch_time", j2);
    }

    public static void n(String str) {
        j("pub_sub_secret_key_data", str);
    }

    public static String o() {
        return c("pub_sub_secret_key_data", "");
    }

    public static void p(long j2) {
        i("next_update_common_conf_time", j2);
    }

    public static boolean q(String str) {
        if (TextUtils.isEmpty(str)) {
            return true;
        }
        return k("pref_custom_privacy_policy_" + str, true);
    }

    public static void r(String str) {
        j("pref_instance_id", str);
        d(v.a());
    }

    public static String s() {
        return c("pref_instance_id", "");
    }

    public static void t(String str) {
        j("common_cloud_data", str);
    }

    public static long u() {
        return a("first_launch_time", 0L);
    }

    public static void v(String str) {
        j("region_rul", str);
    }

    public static String w() {
        return c("common_cloud_data", "");
    }

    public static void x(String str) {
        j("common_config_hash", str);
    }

    public static long y() {
        return a("next_update_common_conf_time", 0L);
    }

    private static void z() {
        if (f771b != null) {
            return;
        }
        synchronized (t.class) {
            try {
                if (f771b == null) {
                    SharedPreferences sharedPreferences = b.a().getSharedPreferences("one_track_pub_sub", 0);
                    f770a = sharedPreferences;
                    f771b = sharedPreferences.edit();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
