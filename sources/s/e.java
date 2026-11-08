package s;

import com.ot.pubsub.util.n;
import java.util.Map;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
class e implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ String f2323a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ Map f2324b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    final /* synthetic */ String f2325c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    final /* synthetic */ String f2326d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    final /* synthetic */ Map f2327e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    final /* synthetic */ c f2328f;

    e(c cVar, String str, Map map, String str2, String str3, Map map2) {
        this.f2328f = cVar;
        this.f2323a = str;
        this.f2324b = map;
        this.f2325c = str2;
        this.f2326d = str3;
        this.f2327e = map2;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        try {
            if (this.f2328f.h(this.f2323a)) {
                return;
            }
            JSONObject jSONObjectD = n.d(this.f2324b, true);
            JSONObject jSONObjectJ = this.f2328f.j(this.f2323a);
            String str = this.f2323a;
            r.b bVar = this.f2328f.f2319c;
            c.i(this.f2328f);
            this.f2328f.f2317a.b(this.f2325c, this.f2326d, this.f2323a, a.a(str, jSONObjectD, bVar, null, jSONObjectJ, this.f2328f.f2320d), this.f2327e);
        } catch (Exception e2) {
            com.ot.pubsub.util.k.h("PubSubTrackImp", "track map error: " + e2.toString());
        }
    }
}
