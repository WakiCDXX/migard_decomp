package c0;

import android.text.TextUtils;
import com.ot.pubsub.util.k;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public int f50a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public String f51b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public String f52c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public String f53d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public int f54e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public long f55f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public String f56g;

    public static a a(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        a aVar = new a();
        try {
            JSONObject jSONObject = new JSONObject(str);
            aVar.f50a = jSONObject.optInt("code");
            String strOptString = jSONObject.optString(com.xiaomi.onetrack.g.a.f1423c);
            aVar.f51b = strOptString;
            if (aVar.f50a == 0) {
                JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject("data");
                if (jSONObjectOptJSONObject != null) {
                    String strOptString2 = jSONObjectOptJSONObject.optString("accessToken");
                    if (!TextUtils.isEmpty(strOptString2)) {
                        aVar.f53d = u.a.j(strOptString2, u.a.f2373b);
                        aVar.f54e = jSONObjectOptJSONObject.optInt("expires");
                        long jOptLong = jSONObjectOptJSONObject.optLong("local_time", 0L);
                        if (jOptLong == 0) {
                            long jCurrentTimeMillis = System.currentTimeMillis();
                            aVar.f55f = jCurrentTimeMillis;
                            jSONObjectOptJSONObject.put("local_time", jCurrentTimeMillis);
                        } else {
                            aVar.f55f = jOptLong;
                        }
                    }
                }
                aVar.f56g = jSONObject.toString();
            } else if (!TextUtils.isEmpty(strOptString)) {
                k.h("TokenBean", "token exception response :" + aVar.f51b);
            }
        } catch (Exception unused) {
        }
        return aVar;
    }

    public String toString() {
        return "TokenBean{code=" + this.f50a + ", msg='" + this.f51b + "', data='" + this.f52c + "', access_token='" + this.f53d + "', expires=" + this.f54e + ", local_time=" + this.f55f + ", response='" + this.f56g + "'}";
    }
}
