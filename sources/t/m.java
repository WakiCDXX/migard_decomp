package t;

import android.text.TextUtils;
import com.ot.pubsub.util.t;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
class m implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ r.b f2369a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ String f2370b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    final /* synthetic */ l f2371c;

    m(l lVar, r.b bVar, String str) {
        this.f2371c = lVar;
        this.f2369a = bVar;
        this.f2370b = str;
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            String strL = t.l();
            if (!TextUtils.isEmpty(strL)) {
                String str = u.a.f2373b;
                String strJ = u.a.j(strL, str);
                if (!TextUtils.isEmpty(strJ)) {
                    JSONObject jSONObject = new JSONObject(strJ);
                    jSONObject.put(this.f2370b, this.f2371c.e(this.f2369a));
                    t.h(u.a.b(jSONObject.toString(), str));
                    return;
                }
            }
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put(this.f2370b, this.f2371c.e(this.f2369a));
            t.h(u.a.b(jSONObject2.toString(), u.a.f2373b));
        } catch (Throwable th) {
            com.ot.pubsub.util.k.c("ConfigEntityManager", "putProjectIdConfigBySP Throwable:" + th.getMessage());
        }
    }
}
