package com.ot.pubsub.util;

/* JADX INFO: loaded from: classes.dex */
public class q {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private r.b f765a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private boolean f766b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private boolean f767c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private long f768d = 0;

    public q(r.b bVar) {
        this.f765a = bVar;
        this.f766b = t.q(n.a(bVar));
    }

    private boolean c() {
        if (Math.abs(System.currentTimeMillis() - this.f768d) > 900000) {
            this.f768d = System.currentTimeMillis();
            this.f767c = m.g(b.c());
        }
        return this.f767c;
    }

    private boolean d(String str) {
        return "onetrack_dau".equals(str) || com.xiaomi.onetrack.api.d.f1050g.equals(str);
    }

    private boolean e(String str) {
        return false;
    }

    private boolean f(String str) {
        return false;
    }

    public String a() {
        if (this.f765a.k()) {
            return this.f766b ? "custom_open" : "custom_close";
        }
        return c() ? "exprience_open" : "exprience_close";
    }

    public boolean b(String str) {
        boolean zC;
        if (this.f765a.k()) {
            StringBuilder sb = new StringBuilder();
            sb.append("use custom privacy policy, the policy is ");
            sb.append(this.f766b ? "open" : "close");
            k.c("PrivacyManager", sb.toString());
            zC = this.f766b;
        } else {
            zC = c();
            StringBuilder sb2 = new StringBuilder();
            sb2.append("use system experience plan, the policy is ");
            sb2.append(zC ? "open" : "close");
            k.c("PrivacyManager", sb2.toString());
        }
        if (zC) {
            return zC;
        }
        boolean zD = d(str);
        boolean zE = e(str);
        boolean zF = f(str);
        StringBuilder sb3 = new StringBuilder();
        sb3.append("This event ");
        sb3.append(str);
        sb3.append(zD ? " is " : " is not ");
        sb3.append("basic event and ");
        sb3.append(zE ? "is" : "is not");
        sb3.append(" recommend event and ");
        sb3.append(zF ? "is" : "is not");
        sb3.append(" custom dau event");
        k.c("PrivacyManager", sb3.toString());
        return zD || zE || zF;
    }
}
