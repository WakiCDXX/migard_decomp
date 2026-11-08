package z;

import com.ot.pubsub.util.k;
import org.json.JSONObject;
import t.g;

/* JADX INFO: loaded from: classes.dex */
public class b extends a {
    public b(String str, String str2, String str3, String str4) {
        try {
            d(str);
            j(str3);
            h(str2);
            g(System.currentTimeMillis());
            e(new JSONObject(str4));
            c(g.e().a(str, str3, "level", 1));
        } catch (Exception e2) {
            k.c("CustomMessage", "CustomMessage error:" + e2.toString());
        }
    }
}
