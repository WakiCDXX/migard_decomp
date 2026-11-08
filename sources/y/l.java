package y;

import android.text.TextUtils;
import com.ot.pubsub.util.t;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class l {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static ConcurrentHashMap<String, String> f2444a = new ConcurrentHashMap<>();

    private static class a {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private static final l f2445a = new l();
    }

    private String b(boolean z2, String str) {
        if (!z2) {
            return "tracking.miui.com";
        }
        String str2 = f2444a.get(str);
        return TextUtils.isEmpty(str2) ? "tracking.intl.miui.com" : str2;
    }

    public static l c() {
        return a.f2445a;
    }

    private String j() {
        return "https://";
    }

    private String k() {
        boolean zN = com.ot.pubsub.util.m.n();
        String strO = com.ot.pubsub.util.m.o();
        if (!zN) {
            return "sdkconfig.ad.intl.xiaomi.com";
        }
        if (TextUtils.equals(strO, "IN")) {
            return "sdkconfig.ad.india.xiaomi.com";
        }
        return TextUtils.equals(strO, "RU") ? "sdkconfig.ad.rus.xiaomi.com" : "sdkconfig.ad.intl.xiaomi.com";
    }

    private String l() {
        return b(com.ot.pubsub.util.m.n(), com.ot.pubsub.util.m.o());
    }

    public String a(String str, String str2, String str3) {
        return str + str2 + str3;
    }

    public synchronized void d(JSONObject jSONObject) {
        try {
            com.ot.pubsub.util.k.c("RegionDomainManager", "updateHostMap:" + jSONObject.toString());
            try {
                Iterator<String> itKeys = jSONObject.keys();
                while (itKeys.hasNext()) {
                    String next = itKeys.next();
                    String strOptString = jSONObject.optString(next);
                    if (!TextUtils.isEmpty(next) && !TextUtils.isEmpty(strOptString)) {
                        f2444a.put(next, strOptString);
                    }
                }
                t.v(new JSONObject(f2444a).toString());
            } catch (Exception e2) {
                com.ot.pubsub.util.k.c("RegionDomainManager", "updateHostMap: " + e2.toString());
            }
            com.ot.pubsub.util.k.c("RegionDomainManager", "merge config:" + new JSONObject(f2444a).toString());
        } catch (Throwable th) {
            throw th;
        }
    }

    public String e() {
        return a(j(), k(), "/api/v1/token");
    }

    public String f() {
        return a(j(), l(), "/track/key_get");
    }

    public String g() {
        return a(j(), k(), "/api/v4/detail/config_common");
    }

    public String h() {
        return a(j(), k(), "/api/v4/detail/config_p");
    }

    private l() {
        i();
    }

    private void i() {
    }
}
