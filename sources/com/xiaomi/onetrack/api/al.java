package com.xiaomi.onetrack.api;

import android.os.Process;
import android.text.TextUtils;
import com.xiaomi.onetrack.Configuration;
import com.xiaomi.onetrack.OneTrack;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class al implements an.a, g {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final String f988a = "OneTrackSystemImp";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static final int f989b = 102400;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static final int f990c = 2;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private final ConcurrentHashMap<String, String> f991d = new ConcurrentHashMap<>();

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private Configuration f992e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private an f993f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private com.xiaomi.onetrack.util.v f994g;

    public al(Configuration configuration, com.xiaomi.onetrack.util.v vVar) {
        this.f992e = configuration;
        this.f994g = vVar;
        an anVarA = an.a();
        this.f993f = anVarA;
        anVarA.a(this);
    }

    private boolean b(String str, String str2) {
        if (OneTrack.isDisable()) {
            return false;
        }
        if ((str != null && str.equals("onetrack_bug_report")) || str2 == null || str2.length() * 2 <= f989b) {
            return true;
        }
        com.xiaomi.onetrack.util.p.a(f988a, "Event size exceed limitation!");
        return false;
    }

    @Override // com.xiaomi.onetrack.api.g
    public void a(String str, String str2) {
        boolean zA = a(str2);
        com.xiaomi.onetrack.util.v vVar = this.f994g;
        if (vVar != null && !vVar.a(str) && !zA) {
            com.xiaomi.onetrack.util.p.a(f988a, "The privacy policy is not permitted, and the event is not basic or recommend event or custom dau event, skip it.");
            return;
        }
        if (b(str, str2)) {
            if (com.xiaomi.onetrack.c.i.b()) {
                com.xiaomi.onetrack.c.i.a(this);
            } else if (!d.f1052i.equalsIgnoreCase(str)) {
                com.xiaomi.onetrack.c.i.a(str, str2);
                return;
            }
            if (com.xiaomi.onetrack.util.p.f1665a) {
                com.xiaomi.onetrack.util.p.a(f988a, "track name:" + str + " data :" + str2 + " tid" + Process.myTid());
            }
            if (this.f993f.a(str, str2, this.f992e, zA)) {
                return;
            }
            String appId = this.f992e.getAppId();
            if (zA) {
                appId = this.f992e.getAdEventAppId();
            }
            com.xiaomi.onetrack.c.l.a().a(appId, str, str2);
            if (com.xiaomi.onetrack.util.p.f1665a) {
                com.xiaomi.onetrack.util.p.a(f988a, "track mIOneTrackService is null! SystemImpCacheManager cache data:" + str2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void b() {
        try {
            com.xiaomi.onetrack.c.l.a().c(this.f992e.getAppId());
            if (TextUtils.isEmpty(this.f992e.getAdEventAppId())) {
                return;
            }
            com.xiaomi.onetrack.c.l.a().c(this.f992e.getAdEventAppId());
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.a(f988a, "trackCachedEvents: " + e2.toString());
        }
    }

    @Override // com.xiaomi.onetrack.api.g
    public void a(int i2) {
        this.f993f.a(i2);
    }

    @Override // com.xiaomi.onetrack.api.g
    public void a(boolean z2) {
        com.xiaomi.onetrack.c.i.a(this);
    }

    @Override // com.xiaomi.onetrack.api.an.a
    public void a() {
        com.xiaomi.onetrack.util.i.a(new am(this));
    }

    private boolean a(String str) {
        try {
            return new JSONObject(str).optJSONObject(e.f1071b).optBoolean(com.xiaomi.onetrack.f.b.C0012b.F, false);
        } catch (Throwable unused) {
            com.xiaomi.onetrack.util.p.a(f988a, "");
            return false;
        }
    }
}
