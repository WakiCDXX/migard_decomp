package com.xiaomi.onetrack.b;

import android.os.Message;
import android.text.TextUtils;
import com.xiaomi.onetrack.c.t;
import com.xiaomi.onetrack.util.p;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
final class c implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ JSONObject f1182a;

    c(JSONObject jSONObject) {
        this.f1182a = jSONObject;
    }

    @Override // java.lang.Runnable
    public void run() {
        p.a("AppConfigUpdater", "checkAppConfigVersion start");
        JSONArray jSONArrayOptJSONArray = this.f1182a.optJSONArray(t.f1318a);
        if (jSONArrayOptJSONArray != null) {
            for (int i2 = 0; i2 < jSONArrayOptJSONArray.length(); i2++) {
                JSONObject jSONObjectOptJSONObject = jSONArrayOptJSONArray.optJSONObject(i2);
                String strOptString = jSONObjectOptJSONObject == null ? "" : jSONObjectOptJSONObject.optString("appId");
                p.a("AppConfigUpdater", "appId: " + strOptString);
                if (!TextUtils.isEmpty(strOptString)) {
                    int iOptInt = jSONObjectOptJSONObject == null ? 0 : jSONObjectOptJSONObject.optInt("version");
                    int iE = h.a().e(strOptString);
                    boolean zE = a.e(strOptString);
                    boolean z2 = a.A.containsKey(strOptString) && ((Boolean) a.A.get(strOptString)).booleanValue();
                    p.a("AppConfigUpdater", "local version: " + iE + ", server version: " + iOptInt + ", canUpdate: " + zE + ", isScheduling: " + z2);
                    if (iOptInt > 0 && iOptInt > iE && zE && !z2) {
                        Message messageObtain = Message.obtain();
                        messageObtain.what = 0;
                        messageObtain.obj = strOptString;
                        long jRandom = (long) (Math.random() * 1800000.0d);
                        p.a("AppConfigUpdater", "the message will be handled after " + jRandom + " ms");
                        a.f1179y.sendMessageDelayed(messageObtain, jRandom);
                        a.A.put(strOptString, Boolean.TRUE);
                    }
                }
            }
        }
    }
}
