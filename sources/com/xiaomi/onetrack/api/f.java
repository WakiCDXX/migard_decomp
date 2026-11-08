package com.xiaomi.onetrack.api;

import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class f {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final String f1073a = "H5DataModel";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private String f1074b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private long f1075c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private String f1076d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private String f1077e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private String f1078f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private String f1079g;

    public static class a {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        public static String f1080a = "event";

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        public static String f1081b = "session_id";

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        public static String f1082c = "instance_id";

        /* JADX INFO: renamed from: d, reason: collision with root package name */
        public static String f1083d = "platform";

        /* JADX INFO: renamed from: e, reason: collision with root package name */
        public static String f1084e = "e_ts";

        /* JADX INFO: renamed from: f, reason: collision with root package name */
        public static String f1085f = "tz";

        /* JADX INFO: renamed from: g, reason: collision with root package name */
        public static String f1086g = "sdk_ver";

        /* JADX INFO: renamed from: h, reason: collision with root package name */
        public static String f1087h = "app_id";

        /* JADX INFO: renamed from: i, reason: collision with root package name */
        public static String f1088i = "channel";

        /* JADX INFO: renamed from: j, reason: collision with root package name */
        public static String f1089j = "uid";

        /* JADX INFO: renamed from: k, reason: collision with root package name */
        public static String f1090k = "uid_type";
    }

    public f(JSONObject jSONObject) {
        this.f1074b = a(jSONObject, a.f1080a);
        try {
            this.f1075c = Long.parseLong(a(jSONObject, a.f1084e));
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b(f1073a, "e_ts parse error: " + e2.getMessage());
        }
        this.f1076d = a(jSONObject, a.f1087h);
        this.f1077e = a(jSONObject, a.f1088i);
        this.f1078f = a(jSONObject, a.f1089j);
        this.f1079g = a(jSONObject, a.f1090k);
    }

    private String a(JSONObject jSONObject, String str) {
        Object objOpt = jSONObject.opt(str);
        return objOpt == null ? "" : String.valueOf(objOpt);
    }

    public long b() {
        return this.f1075c;
    }

    public String c() {
        return this.f1076d;
    }

    public String d() {
        return this.f1077e;
    }

    public String e() {
        return this.f1078f;
    }

    public String f() {
        return this.f1079g;
    }

    public String toString() {
        return "H5DataModel{eventName='" + this.f1074b + "', e_ts=" + this.f1075c + ", appId='" + this.f1076d + "', channel='" + this.f1077e + "', uid='" + this.f1078f + "', uidType='" + this.f1079g + "'}";
    }

    public String a() {
        return this.f1074b;
    }
}
