package r;

import android.text.TextUtils;

/* JADX INFO: loaded from: classes.dex */
public class b {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private String f2296a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private String f2297b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private String f2298c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private String f2299d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private boolean f2300e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private String f2301f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private String f2302g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private boolean f2303h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private boolean f2304i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private boolean f2305j;

    public static class a {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private String f2306a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        private String f2307b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        private String f2308c;

        /* JADX INFO: renamed from: d, reason: collision with root package name */
        private String f2309d;

        /* JADX INFO: renamed from: e, reason: collision with root package name */
        private boolean f2310e;

        /* JADX INFO: renamed from: f, reason: collision with root package name */
        private String f2311f;

        /* JADX INFO: renamed from: i, reason: collision with root package name */
        private String f2314i;

        /* JADX INFO: renamed from: g, reason: collision with root package name */
        private boolean f2312g = false;

        /* JADX INFO: renamed from: h, reason: collision with root package name */
        private boolean f2313h = false;

        /* JADX INFO: renamed from: j, reason: collision with root package name */
        private boolean f2315j = false;

        public b c() {
            return new b(this);
        }

        public a l(String str) {
            this.f2306a = str;
            return this;
        }

        public a m(String str) {
            this.f2307b = str;
            return this;
        }

        public a n(boolean z2) {
            this.f2310e = z2;
            return this;
        }

        public a o(boolean z2) {
            this.f2313h = z2;
            return this;
        }

        public a p(boolean z2) {
            this.f2312g = z2;
            return this;
        }

        public a q(String str) {
            this.f2309d = str;
            return this;
        }

        public a r(String str) {
            this.f2308c = str;
            return this;
        }

        public a s(String str) {
            this.f2311f = str;
            return this;
        }
    }

    private String a(String str) {
        StringBuilder sb = new StringBuilder();
        if (TextUtils.isEmpty(str) || str.length() <= 4) {
            sb.append(str);
        } else {
            for (int i2 = 0; i2 < str.length(); i2++) {
                if (i2 == 0 || i2 == 1 || i2 == str.length() - 2 || i2 == str.length() - 1) {
                    sb.append(str.charAt(i2));
                } else {
                    sb.append("*");
                }
            }
        }
        return sb.toString();
    }

    public String b() {
        return this.f2296a;
    }

    public String c() {
        return this.f2299d;
    }

    public String d() {
        return this.f2302g;
    }

    public String e() {
        return this.f2298c;
    }

    public String f() {
        return this.f2297b;
    }

    public String g() {
        return this.f2301f;
    }

    public boolean h() {
        return this.f2300e;
    }

    public boolean i() {
        return this.f2305j;
    }

    public boolean j() {
        return this.f2304i;
    }

    public boolean k() {
        return this.f2303h;
    }

    public String toString() {
        try {
            return "Configuration{appId='" + a(this.f2296a) + "', channel='" + this.f2299d + "'mProjectId='" + a(this.f2297b) + "', mPrivateKeyId='" + a(this.f2298c) + "', mInternational=" + this.f2300e + ", mNeedGzipAndEncrypt=" + this.f2305j + ", mRegion='" + this.f2301f + "', overrideMiuiRegionSetting=" + this.f2304i + ", instanceId=" + a(this.f2302g) + '}';
        } catch (Exception unused) {
            return "";
        }
    }

    private b(a aVar) {
        this.f2303h = false;
        this.f2304i = false;
        this.f2305j = false;
        this.f2296a = aVar.f2306a;
        this.f2299d = aVar.f2307b;
        this.f2297b = aVar.f2308c;
        this.f2298c = aVar.f2309d;
        this.f2300e = aVar.f2310e;
        this.f2301f = aVar.f2311f;
        this.f2304i = aVar.f2312g;
        this.f2305j = aVar.f2313h;
        this.f2302g = aVar.f2314i;
        this.f2303h = aVar.f2315j;
    }
}
