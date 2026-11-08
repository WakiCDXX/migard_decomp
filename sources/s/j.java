package s;

import android.content.Context;
import android.text.TextUtils;
import com.ot.pubsub.PubSubTrack;
import com.ot.pubsub.util.m;
import com.ot.pubsub.util.q;
import com.ot.pubsub.util.s;
import com.xiaomi.onetrack.util.z;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class j implements b {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private r.b f2335a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private q f2336b;

    public j(Context context, r.b bVar, q qVar) {
        this.f2335a = bVar;
        this.f2336b = qVar;
    }

    private void c(JSONObject jSONObject, Set<String> set) {
        if (jSONObject == null || set == null || set.size() == 0) {
            com.ot.pubsub.util.k.c("PubSubTrackLocalImp", "jsonObject is null or bannedParams is empty");
            return;
        }
        com.ot.pubsub.util.k.c("PubSubTrackLocalImp", "jsonObject: " + jSONObject.toString() + ", bannedParams: " + set.toString());
        try {
            Iterator<String> itKeys = jSONObject.keys();
            while (itKeys.hasNext()) {
                if (set.contains(itKeys.next())) {
                    itKeys.remove();
                }
            }
        } catch (Exception e2) {
            com.ot.pubsub.util.k.h("PubSubTrackLocalImp", "filterParams error：" + e2.toString());
        }
    }

    private boolean d() {
        String strO = m.o();
        if (!TextUtils.isEmpty(strO) && TextUtils.equals(strO.toUpperCase(), "RU")) {
            com.ot.pubsub.util.k.c("PubSubTrackLocalImp", "region is RU,Not allowed pubsub");
            return false;
        }
        if (TextUtils.isEmpty(strO) || !TextUtils.equals(strO.toUpperCase(), "CN")) {
            return true;
        }
        com.ot.pubsub.util.k.c("PubSubTrackLocalImp", "region is CN,Not allowed pubsub");
        return false;
    }

    private boolean e(String str) {
        try {
            return t.g.e().h(str, com.xiaomi.onetrack.b.a.f1155a);
        } catch (Exception e2) {
            com.ot.pubsub.util.k.h("PubSubTrackLocalImp", "isDisableTrackForApp error: " + e2.toString());
            return false;
        }
    }

    private void g(String str, String str2, String str3, String str4, Map<String, String> map) {
        String strC = "";
        if (e(this.f2335a.b())) {
            com.ot.pubsub.util.k.c("PubSubTrackLocalImp", "This app disabled tracking data, skip it.");
            return;
        }
        try {
            JSONObject jSONObject = new JSONObject(str4);
            JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject(com.xiaomi.onetrack.api.e.f1071b);
            JSONObject jSONObjectOptJSONObject2 = jSONObject.optJSONObject(com.xiaomi.onetrack.api.e.f1070a);
            String strC2 = t.g.e().c(this.f2335a.b(), str3, com.xiaomi.onetrack.b.a.f1160f, "");
            String strOptString = jSONObjectOptJSONObject2 == null ? "" : jSONObjectOptJSONObject2.optString(com.xiaomi.onetrack.api.d.ac);
            com.ot.pubsub.util.k.c("PubSubTrackLocalImp", "tip: " + strOptString + ", needIds: " + strC2);
            if (h(strOptString, strC2)) {
                if (i(this.f2335a.b(), str3)) {
                    com.ot.pubsub.util.k.c("PubSubTrackLocalImp", " This event disabled tracking data , skip it.");
                    return;
                } else {
                    if (j(this.f2335a.b(), str3)) {
                        com.ot.pubsub.util.k.c("PubSubTrackLocalImp", " This event should not upload by sampling , skip it.");
                        return;
                    }
                    strC = t.g.e().c(this.f2335a.b(), str3, com.xiaomi.onetrack.b.a.f1161g, "");
                }
            }
            String strN = t.g.e().n(this.f2335a.b());
            com.ot.pubsub.util.k.c("PubSubTrackLocalImp", "bannedParamsForApp: " + strN + ", bannedParamsForEvent: " + strC);
            Set<String> setB = s.b(strN, strC, ",");
            c(jSONObjectOptJSONObject, setB);
            c(jSONObjectOptJSONObject2, setB);
            x.b.a(str, str2, this.f2335a.b(), com.ot.pubsub.util.b.e(), str3, jSONObject.toString(), map);
        } catch (JSONException e2) {
            com.ot.pubsub.util.k.h("PubSubTrackLocalImp", "checkCloudControl error：" + e2.toString());
        }
    }

    private boolean h(String str, String str2) {
        List<String> listA;
        if (TextUtils.isEmpty(str2)) {
            return true;
        }
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        try {
            String[] strArrSplit = str.split(z.f1744a);
            return strArrSplit != null && strArrSplit.length >= 5 && (listA = s.a(str2, ",")) != null && listA.contains(strArrSplit[4]);
        } catch (Exception e2) {
            com.ot.pubsub.util.k.h("PubSubTrackLocalImp", "isMatchId error：" + e2.toString());
        }
    }

    private boolean i(String str, String str2) {
        try {
            return t.g.e().i(str, str2, com.xiaomi.onetrack.b.a.f1155a, false);
        } catch (Exception e2) {
            com.ot.pubsub.util.k.h("PubSubTrackLocalImp", "isDisableTrackForEvent error: " + e2.toString());
            return false;
        }
    }

    private boolean j(String str, String str2) {
        long jK = t.g.e().k(str, str2);
        long jAbs = Math.abs(e0.a.a().b(com.ot.pubsub.util.b.c()).hashCode()) % 100;
        boolean z2 = jK > jAbs;
        com.ot.pubsub.util.k.c("PubSubTrackLocalImp", "shouldUploadBySampling " + str2 + ",  shouldUpload=" + z2 + ", sample=" + jK + ", val=" + jAbs);
        return !z2;
    }

    @Override // s.b
    public void a(String str, String str2, String str3, Map<String, String> map, boolean z2) {
        if (!y.i.d()) {
            com.ot.pubsub.util.k.c("PubSubTrackLocalImp", "network is unaccessable, cta");
            y.i.a(str, str2, "", str3, map, z2);
            return;
        }
        y.i.b(this);
        if (com.ot.pubsub.util.k.f724a) {
            com.ot.pubsub.util.k.c("PubSubTrackLocalImp", "track pb data:" + str3);
        }
        y.c.d().e(str, str2, str3, map, z2);
    }

    @Override // s.b
    public void b(String str, String str2, String str3, String str4, Map<String, String> map) {
        q qVar = this.f2336b;
        if (qVar != null && !qVar.b(str3)) {
            com.ot.pubsub.util.k.c("PubSubTrackLocalImp", "The privacy policy is not permitted, and the event is not basic or recommend event or custom dau event, skip it.");
            return;
        }
        if (f(str3, str4)) {
            if (!y.i.d()) {
                com.ot.pubsub.util.k.c("PubSubTrackLocalImp", "track network is unaccessable, cta");
                y.i.a(str, str2, str3, str4, map, true);
                return;
            }
            y.i.b(this);
            if (com.ot.pubsub.util.k.f724a) {
                com.ot.pubsub.util.k.c("PubSubTrackLocalImp", "track pb ot data:" + str4);
            }
            t.d.a();
            t.a.d(this.f2335a.b());
            g(str, str2, str3, str4, map);
        }
    }

    public boolean f(String str, String str2) {
        if (PubSubTrack.c()) {
            com.ot.pubsub.util.k.c("PubSubTrackLocalImp", "Tracking data is disabled or onetrack use system net traffic only, skip it.");
            return false;
        }
        if (!d()) {
            return false;
        }
        if ((str != null && str.equals("onetrack_bug_report")) || str2 == null || str2.length() * 2 <= 102400) {
            return true;
        }
        com.ot.pubsub.util.k.c("PubSubTrackLocalImp", "Event size exceed limitation!");
        return false;
    }

    @Override // s.b
    public void a(int i2) {
        if (i2 == 2) {
            w.a.b(new k(this));
        }
    }
}
