package com.xiaomi.onetrack.util;

import com.xiaomi.onetrack.Configuration;
import com.xiaomi.onetrack.OneTrack;

/* JADX INFO: loaded from: classes.dex */
public class v {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final String f1705a = "custom_open";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static final String f1706b = "custom_close";

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static final String f1707c = "exprience_open";

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static final String f1708d = "exprience_close";

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static final String f1709e = "PrivacyManager";

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private static final long f1710k = 900000;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private OneTrack.IEventHook f1711f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private Configuration f1712g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private boolean f1713h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private boolean f1714i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private long f1715j = 0;

    public v(Configuration configuration) {
        this.f1712g = configuration;
        this.f1713h = aa.k(r.a(configuration));
    }

    private boolean b(String str) {
        return "onetrack_dau".equals(str) || com.xiaomi.onetrack.api.d.f1050g.equals(str);
    }

    private boolean c(String str) {
        OneTrack.IEventHook iEventHook = this.f1711f;
        return iEventHook != null && iEventHook.isRecommendEvent(str);
    }

    private boolean d(String str) {
        OneTrack.IEventHook iEventHook = this.f1711f;
        return iEventHook != null && iEventHook.isCustomDauEvent(str);
    }

    public boolean a(String str) {
        boolean zB;
        if (this.f1712g.isUseCustomPrivacyPolicy()) {
            StringBuilder sb = new StringBuilder();
            sb.append("use custom privacy policy, the policy is ");
            sb.append(this.f1713h ? "open" : "close");
            p.a(f1709e, sb.toString());
            zB = this.f1713h;
        } else {
            zB = b();
            StringBuilder sb2 = new StringBuilder();
            sb2.append("use system experience plan, the policy is ");
            sb2.append(zB ? "open" : "close");
            p.a(f1709e, sb2.toString());
        }
        if (zB) {
            return zB;
        }
        boolean zB2 = b(str);
        boolean zC = c(str);
        boolean zD = d(str);
        StringBuilder sb3 = new StringBuilder();
        sb3.append("This event ");
        sb3.append(str);
        sb3.append(zB2 ? " is " : " is not ");
        sb3.append("basic event and ");
        sb3.append(zC ? "is" : "is not");
        sb3.append(" recommend event and ");
        sb3.append(zD ? "is" : "is not");
        sb3.append(" custom dau event");
        p.a(f1709e, sb3.toString());
        return zB2 || zC || zD;
    }

    private boolean b() {
        if (Math.abs(System.currentTimeMillis() - this.f1715j) > f1710k) {
            this.f1715j = System.currentTimeMillis();
            this.f1714i = q.a(com.xiaomi.onetrack.f.a.b());
        }
        return this.f1714i;
    }

    public void a(OneTrack.IEventHook iEventHook) {
        this.f1711f = iEventHook;
    }

    public void a(boolean z2) {
        this.f1713h = z2;
    }

    public String a() {
        if (this.f1712g.isUseCustomPrivacyPolicy()) {
            if (this.f1713h) {
                return f1705a;
            }
            return f1706b;
        }
        if (b()) {
            return f1707c;
        }
        return f1708d;
    }
}
