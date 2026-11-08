package com.xiaomi.onetrack.util;

import android.content.Context;
import android.text.TextUtils;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class x {
    private static final String A = "/api/v4/detail/config_common";

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final String f1717a = "RegionDomainManager";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static final String f1718b = "CN";

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static final String f1719c = "INTL";

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static final String f1720d = "IN";

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static final String f1721e = "RU";

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static final String f1722f = "http://";

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private static final String f1723g = "https://";

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private static String f1724h = "";

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private static String f1725i = "";

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private static String f1726j = "";

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private static String f1727k = "";

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    private static String f1728l = "";

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    private static String f1729m = "";

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    private static String f1730n = "";

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    private static String f1731o = "";

    /* JADX INFO: renamed from: x, reason: collision with root package name */
    private static final String f1740x = "/track/v4";

    /* JADX INFO: renamed from: y, reason: collision with root package name */
    private static final String f1741y = "/track/key_get";

    /* JADX INFO: renamed from: z, reason: collision with root package name */
    private static final String f1742z = "/api/v4/detail/config";
    private Context B;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    private static final byte[] f1732p = {116, 114, 97, 99, 107, 105, 110, 103, 46, 109, 105, 117, 105, 46, 99, 111, 109};

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    private static final byte[] f1733q = {116, 114, 97, 99, 107, 105, 110, 103, 46, 105, 110, 116, 108, 46, 109, 105, 117, 105, 46, 99, 111, 109};

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    private static final byte[] f1734r = {116, 114, 97, 99, 107, 105, 110, 103, 46, 114, 117, 115, 46, 109, 105, 117, 105, 46, 99, 111, 109};

    /* JADX INFO: renamed from: s, reason: collision with root package name */
    private static final byte[] f1735s = {116, 114, 97, 99, 107, 105, 110, 103, 46, 105, 110, 100, 105, 97, 46, 109, 105, 117, 105, 46, 99, 111, 109};

    /* JADX INFO: renamed from: t, reason: collision with root package name */
    private static final byte[] f1736t = {115, 100, 107, 99, 111, 110, 102, 105, 103, 46, 97, 100, 46, 120, 105, 97, 111, 109, 105, 46, 99, 111, 109};

    /* JADX INFO: renamed from: u, reason: collision with root package name */
    private static final byte[] f1737u = {115, 100, 107, 99, 111, 110, 102, 105, 103, 46, 97, 100, 46, 105, 110, 116, 108, 46, 120, 105, 97, 111, 109, 105, 46, 99, 111, 109};

    /* JADX INFO: renamed from: v, reason: collision with root package name */
    private static final byte[] f1738v = {115, 100, 107, 99, 111, 110, 102, 105, 103, 46, 97, 100, 46, 105, 110, 100, 105, 97, 46, 120, 105, 97, 111, 109, 105, 46, 99, 111, 109};

    /* JADX INFO: renamed from: w, reason: collision with root package name */
    private static final byte[] f1739w = {115, 100, 107, 99, 111, 110, 102, 105, 103, 46, 97, 100, 46, 114, 117, 115, 46, 120, 105, 97, 111, 109, 105, 46, 99, 111, 109};
    private static ConcurrentHashMap<String, String> C = new ConcurrentHashMap<>();

    private static class a {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private static final x f1743a = new x();

        private a() {
        }
    }

    private String a(byte[] bArr) {
        String str = "";
        try {
            String str2 = new String(bArr, "UTF-8");
            try {
                p.a(f1717a, "transmitToString host:" + str2);
                return str2;
            } catch (Exception e2) {
                e = e2;
                str = str2;
                p.b(f1717a, e.getMessage());
                return str;
            }
        } catch (Exception e3) {
            e = e3;
        }
    }

    private void f() {
        f1724h = a(f1732p);
        f1725i = a(f1733q);
        f1726j = a(f1734r);
        f1727k = a(f1735s);
        f1728l = a(f1736t);
        f1729m = a(f1737u);
        f1730n = a(f1738v);
        f1731o = a(f1739w);
    }

    private void g() {
        try {
            String strH = aa.h();
            if (TextUtils.isEmpty(strH)) {
                return;
            }
            a(new JSONObject(strH));
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
    }

    private String h() {
        return f1723g;
    }

    private String i() {
        return a(q.h(), q.i());
    }

    private String j() {
        boolean zH = q.h();
        String strI = q.i();
        if (!zH) {
            return f1728l;
        }
        if (TextUtils.equals(strI, f1720d)) {
            return f1730n;
        }
        return TextUtils.equals(strI, f1721e) ? f1731o : f1729m;
    }

    public String b() {
        try {
            if (TextUtils.isEmpty(aa.l())) {
                com.xiaomi.onetrack.b.e.b();
            }
        } catch (Exception e2) {
            p.a(f1717a, "getTrackingUrl: " + e2.toString());
        }
        return a(h(), i(), f1740x);
    }

    public String c() {
        return a(h(), j(), f1742z);
    }

    public String d() {
        return a(h(), j(), A);
    }

    public String e() {
        return a(h(), i(), f1741y);
    }

    private x() {
        f();
        C.put(f1720d, f1727k);
        C.put(f1721e, f1726j);
        g();
    }

    public static x a() {
        return a.f1743a;
    }

    public synchronized void a(JSONObject jSONObject) {
        try {
            p.a(f1717a, "updateHostMap:" + jSONObject.toString());
            try {
                Iterator<String> itKeys = jSONObject.keys();
                while (itKeys.hasNext()) {
                    String next = itKeys.next();
                    String strOptString = jSONObject.optString(next);
                    if (!TextUtils.isEmpty(next) && !TextUtils.isEmpty(strOptString)) {
                        C.put(next, strOptString);
                    }
                }
                aa.b(new JSONObject(C).toString());
            } catch (Exception e2) {
                p.a(f1717a, "updateHostMap: " + e2.toString());
            }
            p.a(f1717a, "merge config:" + new JSONObject(C).toString());
        } catch (Throwable th) {
            throw th;
        }
    }

    public String a(String str, String str2, String str3) {
        return str + str2 + str3;
    }

    private String a(boolean z2, String str) {
        if (!z2) {
            return f1724h;
        }
        String str2 = C.get(str);
        return TextUtils.isEmpty(str2) ? f1725i : str2;
    }
}
