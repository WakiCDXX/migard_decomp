package com.xiaomi.onetrack.api;

import android.text.TextUtils;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
class ad implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ String f958a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ Map f959b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    final /* synthetic */ List f960c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    final /* synthetic */ j f961d;

    ad(j jVar, String str, Map map, List list) {
        this.f961d = jVar;
        this.f958a = str;
        this.f959b = map;
        this.f960c = list;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        try {
            if (TextUtils.isEmpty(this.f961d.f1112f.getAdEventAppId())) {
                com.xiaomi.onetrack.util.p.a("OneTrackImp", "adEventAppId is null,Please configure,event name:" + this.f958a);
                return;
            }
            if (this.f961d.d(this.f958a)) {
                return;
            }
            this.f961d.f1109b.a(this.f958a, e.a(this.f958a, com.xiaomi.onetrack.util.r.a((Map<String, Object>) this.f959b, true), this.f961d.f1112f, this.f961d.f1114h, this.f961d.f(this.f958a), this.f961d.f1115i, com.xiaomi.onetrack.util.r.a(this.f960c), this.f961d.f1116j));
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "track map error: " + e2.toString());
        }
    }
}
