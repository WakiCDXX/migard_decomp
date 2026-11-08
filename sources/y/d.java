package y;

import android.text.TextUtils;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
class d implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ Map f2415a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ String f2416b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    final /* synthetic */ String f2417c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    final /* synthetic */ String f2418d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    final /* synthetic */ boolean f2419e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    final /* synthetic */ c f2420f;

    d(c cVar, Map map, String str, String str2, String str3, boolean z2) {
        this.f2420f = cVar;
        this.f2415a = map;
        this.f2416b = str;
        this.f2417c = str2;
        this.f2418d = str3;
        this.f2419e = z2;
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            if (this.f2415a != null) {
                String strO = com.ot.pubsub.util.m.o();
                if (this.f2415a.get("region") == null) {
                    this.f2415a.put("region", strO);
                }
                if (this.f2415a.get("area") == null && !TextUtils.isEmpty(strO) && com.ot.pubsub.util.a.f706a.contains(strO.toUpperCase())) {
                    this.f2415a.put("area", "EU");
                }
            }
            this.f2420f.k(this.f2416b, this.f2417c, this.f2418d, this.f2415a, this.f2419e);
            d0.d.a().d(false);
        } catch (Exception e2) {
            com.ot.pubsub.util.k.i("MessageManager", "EventManager.addTrackMessage exception: ", e2);
        }
    }
}
