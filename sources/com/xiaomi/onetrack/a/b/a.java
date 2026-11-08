package com.xiaomi.onetrack.a.b;

import android.text.TextUtils;
import com.xiaomi.onetrack.util.p;

/* JADX INFO: loaded from: classes.dex */
public class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final int f917a = 0;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final int f918b = 1;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final int f919c = 2;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static final String f920d = "AdMonitor";

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private int f921e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private String f922f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private long f923g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private String f924h = "";

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private String f925i = "";

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private String f926j = "";

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private int f927k = 0;

    public String a() {
        return this.f926j;
    }

    public int b() {
        return this.f921e;
    }

    public String c() {
        return this.f922f;
    }

    public long d() {
        return this.f923g;
    }

    public String e() {
        return this.f924h;
    }

    public String f() {
        return this.f925i;
    }

    public int g() {
        return this.f927k;
    }

    public boolean h() {
        try {
            return (TextUtils.isEmpty(this.f922f) || TextUtils.isEmpty(this.f924h) || TextUtils.isEmpty(this.f925i)) ? false : true;
        } catch (Exception e2) {
            p.a(f920d, "check AdMonitor isValid error:" + e2.getMessage());
            return false;
        }
    }

    public void a(String str) {
        this.f926j = str;
    }

    public void b(String str) {
        this.f922f = str;
    }

    public void c(String str) {
        this.f924h = str;
    }

    public void d(String str) {
        this.f925i = str;
    }

    public void a(int i2) {
        this.f921e = i2;
    }

    public void b(int i2) {
        this.f927k = i2;
    }

    public void a(long j2) {
        this.f923g = j2;
    }
}
