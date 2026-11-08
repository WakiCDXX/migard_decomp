package s;

import com.ot.pubsub.PubSubTrack;
import com.ot.pubsub.util.n;
import com.ot.pubsub.util.q;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class a {
    public static String a(String str, JSONObject jSONObject, r.b bVar, PubSubTrack.a aVar, JSONObject jSONObject2, q qVar) throws JSONException {
        JSONObject jSONObject3 = new JSONObject();
        jSONObject3.put(com.xiaomi.onetrack.api.e.f1071b, z.a.a(str, bVar, aVar, qVar));
        jSONObject3.put(com.xiaomi.onetrack.api.e.f1070a, n.e(jSONObject, jSONObject2));
        return jSONObject3.toString();
    }
}
