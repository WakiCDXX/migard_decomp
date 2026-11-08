package v;

import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private long f2385a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private String f2386b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private String f2387c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private String f2388d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private Map<String, String> f2389e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private long f2390f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private int f2391g;

    public a(long j2, String str, String str2, String str3, Map<String, String> map, int i2, long j3) {
        this.f2385a = j2;
        this.f2386b = str;
        this.f2387c = str2;
        this.f2388d = str3;
        this.f2389e = map;
        this.f2391g = i2;
        this.f2390f = j3;
    }

    public long a() {
        return this.f2385a;
    }

    public String b() {
        return this.f2386b;
    }

    public String c() {
        return this.f2387c;
    }

    public String d() {
        return this.f2388d;
    }

    public Map<String, String> e() {
        return this.f2389e;
    }

    public long f() {
        return this.f2390f;
    }

    public int g() {
        return this.f2391g;
    }

    public String toString() {
        return "PSMessage{mId=" + this.f2385a + ", mProjectID='" + this.f2386b + "', mTopic='" + this.f2387c + "', mData='" + this.f2388d + "', mAttributes=" + this.f2389e + ", mGzipAndEncrypt=" + this.f2391g + ", mTimestamp=" + this.f2390f + '}';
    }
}
