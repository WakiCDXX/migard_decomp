package y;

import android.text.TextUtils;
import com.xiaomi.smack.packet.CommonPacketExtension;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
final class k implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ s.b f2443a;

    k(s.b bVar) {
        this.f2443a = bVar;
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            List<JSONObject> listE = i.e();
            com.ot.pubsub.util.k.c("NetworkAccessManager", "jsonData :" + listE);
            if (listE != null && listE.size() > 0) {
                for (JSONObject jSONObject : listE) {
                    String strOptString = jSONObject.optString("projectid");
                    String strOptString2 = jSONObject.optString("topic");
                    String strOptString3 = jSONObject.optString("eventName");
                    String strOptString4 = jSONObject.optString("data");
                    Map<String, String> mapC = com.ot.pubsub.util.c.c(jSONObject.optString(CommonPacketExtension.ATTRIBUTE_NAME));
                    com.ot.pubsub.util.k.c("NetworkAccessManager", "attributes values : " + mapC.toString());
                    boolean zOptBoolean = jSONObject.optBoolean("needGzipAndEncrypt", false);
                    if (TextUtils.isEmpty(strOptString3)) {
                        this.f2443a.a(strOptString, strOptString2, strOptString4, mapC, zOptBoolean);
                    } else {
                        this.f2443a.b(strOptString, strOptString2, strOptString3, strOptString4, mapC);
                    }
                }
            }
            i.g(true);
        } catch (Exception e2) {
            com.ot.pubsub.util.k.h("NetworkAccessManager", "cta event error: " + e2.toString());
        }
        boolean unused = i.f2435d = false;
    }
}
