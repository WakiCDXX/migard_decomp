package com.xiaomi.onetrack.api;

import android.content.Context;
import android.text.TextUtils;
import com.xiaomi.onetrack.Configuration;
import com.xiaomi.onetrack.OneTrack;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class aj implements g {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final String f981a = "OneTrackLocalImp";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static final int f982b = 102400;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static final int f983c = 2;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private Configuration f984d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private com.xiaomi.onetrack.util.v f985e;

    public aj(Context context, Configuration configuration, com.xiaomi.onetrack.util.v vVar) {
        com.xiaomi.onetrack.f.g.a(context);
        this.f984d = configuration;
        this.f985e = vVar;
    }

    private boolean c(String str, String str2) {
        List<String> listA;
        if (TextUtils.isEmpty(str2)) {
            return true;
        }
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        try {
            String[] strArrSplit = str.split(com.xiaomi.onetrack.util.z.f1744a);
            return strArrSplit != null && strArrSplit.length >= 5 && (listA = com.xiaomi.onetrack.util.z.a(str2, ",")) != null && listA.contains(strArrSplit[4]);
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b(f981a, "isMatchId error：" + e2.toString());
        }
    }

    private boolean d(String str, String str2) {
        try {
            return com.xiaomi.onetrack.b.h.a().a(str, str2, com.xiaomi.onetrack.b.a.f1155a, false);
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b(f981a, "isDisableTrackForEvent error: " + e2.toString());
            return false;
        }
    }

    private boolean e(String str, String str2) {
        long jB = com.xiaomi.onetrack.b.h.a().b(str, str2);
        long jAbs = Math.abs(com.xiaomi.onetrack.util.oaid.a.a().a(com.xiaomi.onetrack.f.a.b()).hashCode()) % 100;
        boolean z2 = jB > jAbs;
        com.xiaomi.onetrack.util.p.a(f981a, "shouldUploadBySampling " + str2 + ",  shouldUpload=" + z2 + ", sample=" + jB + ", val=" + jAbs);
        return !z2;
    }

    @Override // com.xiaomi.onetrack.api.g
    public void a(String str, String str2) {
        JSONObject jSONObject;
        JSONObject jSONObjectOptJSONObject;
        JSONObject jSONObjectOptJSONObject2 = null;
        try {
            jSONObject = new JSONObject(str2);
            try {
                jSONObjectOptJSONObject = jSONObject.optJSONObject(e.f1071b);
                try {
                    jSONObjectOptJSONObject2 = jSONObject.optJSONObject(e.f1070a);
                } catch (JSONException e2) {
                    e = e2;
                    com.xiaomi.onetrack.util.p.a(f981a, " data JSONException e:" + e.getMessage());
                }
            } catch (JSONException e3) {
                e = e3;
                jSONObjectOptJSONObject = null;
            }
        } catch (JSONException e4) {
            e = e4;
            jSONObject = null;
            jSONObjectOptJSONObject = null;
        }
        JSONObject jSONObject2 = jSONObjectOptJSONObject2;
        JSONObject jSONObject3 = jSONObject;
        JSONObject jSONObject4 = jSONObjectOptJSONObject;
        boolean zA = a(jSONObject4);
        com.xiaomi.onetrack.util.v vVar = this.f985e;
        if (vVar != null && !vVar.a(str) && !zA) {
            com.xiaomi.onetrack.util.p.a(f981a, "The privacy policy is not permitted, and the event is not basic or recommend event or custom dau event, skip it.");
            return;
        }
        if (b(str, str2)) {
            if (!com.xiaomi.onetrack.c.i.b()) {
                com.xiaomi.onetrack.c.i.a(str, str2);
                return;
            }
            com.xiaomi.onetrack.c.i.a(this);
            if (com.xiaomi.onetrack.util.p.f1665a && !str.equalsIgnoreCase("onetrack_bug_report")) {
                com.xiaomi.onetrack.util.p.a(f981a, "track data:" + str2);
            }
            if (zA) {
                com.xiaomi.onetrack.b.a.a(this.f984d.getAdEventAppId());
            }
            com.xiaomi.onetrack.b.a.a(this.f984d.getAppId());
            a(str, jSONObject3, jSONObject4, jSONObject2, zA);
        }
    }

    public boolean b(String str, String str2) {
        if (OneTrack.isDisable() || OneTrack.isUseSystemNetTrafficOnly()) {
            com.xiaomi.onetrack.util.p.a(f981a, "Tracking data is disabled or onetrack use system net traffic only, skip it.");
            return false;
        }
        if ((str != null && str.equals("onetrack_bug_report")) || str2 == null || str2.length() * 2 <= f982b) {
            return true;
        }
        com.xiaomi.onetrack.util.p.a(f981a, "Event size exceed limitation!");
        return false;
    }

    @Override // com.xiaomi.onetrack.api.g
    public void a(int i2) {
        com.xiaomi.onetrack.util.i.a(new ak(this, i2));
    }

    @Override // com.xiaomi.onetrack.api.g
    public void a(boolean z2) {
        com.xiaomi.onetrack.c.i.a(this);
    }

    private void a(String str, JSONObject jSONObject, JSONObject jSONObject2, JSONObject jSONObject3, boolean z2) {
        String strOptString;
        String strA = "";
        String appId = this.f984d.getAppId();
        if (z2) {
            appId = this.f984d.getAdEventAppId();
        }
        if (!a(appId)) {
            try {
                String strA2 = com.xiaomi.onetrack.b.h.a().a(appId, str, com.xiaomi.onetrack.b.a.f1160f, "");
                if (jSONObject3 == null) {
                    strOptString = "";
                } else {
                    strOptString = jSONObject3.optString(d.ac);
                }
                com.xiaomi.onetrack.util.p.a(f981a, "tip: " + strOptString + ", needIds: " + strA2);
                if (c(strOptString, strA2)) {
                    if (d(appId, str)) {
                        com.xiaomi.onetrack.util.p.a(f981a, " This event disabled tracking data , skip it.");
                        return;
                    } else if (!e(appId, str)) {
                        strA = com.xiaomi.onetrack.b.h.a().a(appId, str, com.xiaomi.onetrack.b.a.f1161g, "");
                    } else {
                        com.xiaomi.onetrack.util.p.a(f981a, " This event should not upload by sampling , skip it.");
                        return;
                    }
                }
                String strC = com.xiaomi.onetrack.b.h.a().c(appId);
                com.xiaomi.onetrack.util.p.a(f981a, "bannedParamsForApp: " + strC + ", bannedParamsForEvent: " + strA);
                Set<String> setA = com.xiaomi.onetrack.util.z.a(strC, strA, ",");
                a(jSONObject2, setA);
                a(jSONObject3, setA);
                if (z2) {
                    com.xiaomi.onetrack.f.d.b(appId, com.xiaomi.onetrack.f.a.e(), str, jSONObject.toString());
                    return;
                } else {
                    com.xiaomi.onetrack.f.d.a(appId, com.xiaomi.onetrack.f.a.e(), str, jSONObject.toString());
                    return;
                }
            } catch (Throwable th) {
                com.xiaomi.onetrack.util.p.b(f981a, "checkCloudControl error：" + th.toString());
                return;
            }
        }
        com.xiaomi.onetrack.util.p.a(f981a, "This app disabled tracking data, skip it.");
    }

    private void a(JSONObject jSONObject, Set<String> set) {
        if (jSONObject == null || set == null || set.size() == 0) {
            com.xiaomi.onetrack.util.p.a(f981a, "jsonObject is null or bannedParams is empty");
            return;
        }
        com.xiaomi.onetrack.util.p.a(f981a, "jsonObject: " + jSONObject.toString() + ", bannedParams: " + set.toString());
        try {
            Iterator<String> itKeys = jSONObject.keys();
            while (itKeys.hasNext()) {
                if (set.contains(itKeys.next())) {
                    itKeys.remove();
                }
            }
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b(f981a, "filterParams error：" + e2.toString());
        }
    }

    private boolean a(String str) {
        try {
            return com.xiaomi.onetrack.b.h.a().a(str, com.xiaomi.onetrack.b.a.f1155a);
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b(f981a, "isDisableTrackForApp error: " + e2.toString());
            return false;
        }
    }

    private boolean a(JSONObject jSONObject) {
        try {
            return jSONObject.optBoolean(com.xiaomi.onetrack.f.b.C0012b.F, false);
        } catch (Throwable unused) {
            com.xiaomi.onetrack.util.p.a(f981a, "");
            return false;
        }
    }
}
