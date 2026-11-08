package y;

import android.text.TextUtils;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
class g implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ Map f2426a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ String f2427b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    final /* synthetic */ String f2428c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    final /* synthetic */ z.a f2429d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    final /* synthetic */ f f2430e;

    g(f fVar, Map map, String str, String str2, z.a aVar) {
        this.f2430e = fVar;
        this.f2426a = map;
        this.f2427b = str;
        this.f2428c = str2;
        this.f2429d = aVar;
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            if (this.f2426a != null) {
                String strO = com.ot.pubsub.util.m.o();
                if (this.f2426a.get("region") == null) {
                    this.f2426a.put("region", strO);
                }
                if (this.f2426a.get("area") == null && !TextUtils.isEmpty(strO) && com.ot.pubsub.util.a.f706a.contains(strO.toUpperCase())) {
                    this.f2426a.put("area", "EU");
                }
            }
            this.f2430e.l(this.f2427b, this.f2428c, this.f2429d, this.f2426a);
            d0.d.a().b(this.f2429d.l(), false);
        } catch (Exception e2) {
            com.ot.pubsub.util.k.i("MessageOTManager", "EventManager.addTrackMessage exception: ", e2);
        }
    }
}
