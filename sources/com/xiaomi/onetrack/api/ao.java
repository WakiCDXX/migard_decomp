package com.xiaomi.onetrack.api;

import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class ao {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final String f1011a = "name";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final String f1012b = "gender";

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final String f1013c = "birthday";

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final String f1014d = "phone";

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final String f1015e = "job";

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public static final String f1016f = "hobby";

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public static final String f1017g = "region";

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public static final String f1018h = "province";

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final String f1019i = "city";

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private String f1020j;

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private String f1021k;

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    private String f1022l;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    private String f1023m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    private String f1024n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    private String f1025o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    private String f1026p;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    private String f1027q;

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    private String f1028r;

    public static final class a {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private String f1029a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        private String f1030b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        private String f1031c;

        /* JADX INFO: renamed from: d, reason: collision with root package name */
        private String f1032d;

        /* JADX INFO: renamed from: e, reason: collision with root package name */
        private String f1033e;

        /* JADX INFO: renamed from: f, reason: collision with root package name */
        private String f1034f;

        /* JADX INFO: renamed from: g, reason: collision with root package name */
        private String f1035g;

        /* JADX INFO: renamed from: h, reason: collision with root package name */
        private String f1036h;

        /* JADX INFO: renamed from: i, reason: collision with root package name */
        private String f1037i;

        public a a(String str) {
            this.f1029a = str;
            return this;
        }

        public a b(String str) {
            this.f1030b = str;
            return this;
        }

        public a c(String str) {
            this.f1031c = str;
            return this;
        }

        public a d(String str) {
            this.f1032d = str;
            return this;
        }

        public a e(String str) {
            this.f1033e = str;
            return this;
        }

        public a f(String str) {
            this.f1034f = str;
            return this;
        }

        public a g(String str) {
            this.f1035g = str;
            return this;
        }

        public a h(String str) {
            this.f1036h = str;
            return this;
        }

        public a i(String str) {
            this.f1037i = str;
            return this;
        }

        public ao a() {
            ao aoVar = new ao();
            aoVar.f1025o = this.f1034f;
            aoVar.f1024n = this.f1033e;
            aoVar.f1028r = this.f1037i;
            aoVar.f1023m = this.f1032d;
            aoVar.f1027q = this.f1036h;
            aoVar.f1022l = this.f1031c;
            aoVar.f1020j = this.f1029a;
            aoVar.f1026p = this.f1035g;
            aoVar.f1021k = this.f1030b;
            return aoVar;
        }
    }

    public JSONObject j() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("name", this.f1020j);
            jSONObject.put("gender", this.f1021k);
            jSONObject.put("birthday", this.f1022l);
            jSONObject.put("phone", this.f1023m);
            jSONObject.put("job", this.f1024n);
            jSONObject.put("hobby", this.f1025o);
            jSONObject.put("region", this.f1026p);
            jSONObject.put("province", this.f1027q);
            jSONObject.put("city", this.f1028r);
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
        return jSONObject;
    }

    public String toString() {
        return j().toString();
    }

    private ao() {
    }

    public String a() {
        return this.f1020j;
    }

    public String b() {
        return this.f1021k;
    }

    public String c() {
        return this.f1022l;
    }

    public String d() {
        return this.f1023m;
    }

    public String e() {
        return this.f1024n;
    }

    public String f() {
        return this.f1025o;
    }

    public String g() {
        return this.f1026p;
    }

    public String h() {
        return this.f1027q;
    }

    public String i() {
        return this.f1028r;
    }
}
