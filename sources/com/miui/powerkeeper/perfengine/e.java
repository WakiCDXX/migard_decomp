package com.miui.powerkeeper.perfengine;

import com.miui.powerkeeper.utils.PowerLog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: compiled from: NewAdvanceSchedApps.java */
/* JADX INFO: loaded from: classes.dex */
public class e extends JSONObject {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public boolean f516a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public boolean f517b = false;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public boolean f518c = false;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public boolean f519d = false;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public ArrayList<HashMap<String, ArrayList<d>>> f520e = new ArrayList<>();

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public HashMap<String, ArrayList<d>> f521f = new HashMap<>();

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public HashMap<String, ArrayList<d>> f522g = new HashMap<>();

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public HashMap<String, ArrayList<d>> f523h = new HashMap<>();

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public HashMap<String, ArrayList<String>> f524i = new HashMap<>();

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    public HashMap<String, String> f525j = new HashMap<>();

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    public HashMap<String, String> f526k = new HashMap<>();

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    public HashMap<String, String> f527l = new HashMap<>();

    public e(boolean z2) {
        this.f516a = false;
        this.f516a = z2;
    }

    private boolean b(String str, String str2) {
        Matcher matcher = Pattern.compile(str).matcher(str2);
        boolean z2 = false;
        while (matcher.find()) {
            if (PowerLog.V) {
                PowerLog.v("PeGameController_NewAdvanceSchedApps", "isRegexFound true, find [" + str2 + "] by [" + str + "]");
            }
            z2 = true;
        }
        return z2;
    }

    private void d() {
        for (HashMap<String, ArrayList<d>> map : this.f520e) {
            for (String str : map.keySet()) {
                if (PowerLog.V) {
                    PowerLog.v("PeGameController_NewAdvanceSchedApps", "updateAllAdPerfNode keyVer:" + str);
                }
                for (d dVar : map.get(str)) {
                    if (PowerLog.V) {
                        PowerLog.v("PeGameController_NewAdvanceSchedApps", "updateAllAdPerfNode node:" + ((Object) dVar.b()));
                    }
                    String str2 = dVar.f508f + "";
                    if (this.f524i.containsKey(str2)) {
                        dVar.f512j = this.f524i.get(str2);
                    }
                    String str3 = dVar.f509g + "";
                    if (this.f525j.containsKey(str3)) {
                        dVar.f513k = this.f525j.get(str3);
                    }
                    String str4 = dVar.f510h + "";
                    if (this.f526k.containsKey(str4)) {
                        dVar.f514l = this.f526k.get(str4);
                    }
                    String str5 = dVar.f511i + "";
                    if (this.f527l.containsKey(str5)) {
                        dVar.f515m = this.f527l.get(str5);
                    }
                }
            }
        }
    }

    public d a(String str, String str2, String str3, String str4) {
        if (PowerLog.V) {
            PowerLog.v("PeGameController_NewAdvanceSchedApps", "getAdvPerfNode instlAttVer:" + str + ", instlAndBenVer:" + str2 + ", instlGfBenVer:" + str3 + ", key:" + str4);
        }
        for (HashMap<String, ArrayList<d>> map : this.f520e) {
            for (String str5 : map.keySet()) {
                if (b(str5, str) || b(str5, str2) || b(str5, str3)) {
                    ArrayList<d> arrayList = map.get(str5);
                    if (arrayList != null) {
                        for (d dVar : arrayList) {
                            if (dVar.c(str4)) {
                                if (PowerLog.V) {
                                    PowerLog.v("PeGameController_NewAdvanceSchedApps", "getAdvPerfNode ret key:[" + str4 + "]");
                                }
                                return dVar;
                            }
                        }
                    } else {
                        continue;
                    }
                }
            }
        }
        return null;
    }

    public void c(JSONObject jSONObject) {
        String strOptString;
        String strOptString2;
        String strOptString3;
        if (jSONObject != null) {
            try {
                this.f516a = jSONObject.optBoolean("enable", false);
                this.f520e.clear();
                JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject("at");
                if (jSONObjectOptJSONObject != null) {
                    this.f517b = true;
                    this.f521f.clear();
                    Iterator<String> itKeys = jSONObjectOptJSONObject.keys();
                    while (itKeys.hasNext()) {
                        String next = itKeys.next();
                        JSONObject jSONObject2 = jSONObjectOptJSONObject.getJSONObject(next);
                        ArrayList<d> arrayList = new ArrayList<>();
                        Iterator<String> itKeys2 = jSONObject2.keys();
                        while (itKeys2.hasNext()) {
                            String next2 = itKeys2.next();
                            arrayList.add(new d(next2, jSONObject2.getJSONObject(next2)));
                        }
                        this.f521f.put(next, arrayList);
                    }
                    if (!this.f521f.isEmpty()) {
                        this.f520e.add(this.f521f);
                    }
                } else {
                    this.f517b = false;
                }
                JSONObject jSONObjectOptJSONObject2 = jSONObject.optJSONObject("andBen");
                if (jSONObjectOptJSONObject2 != null) {
                    this.f518c = true;
                    this.f522g.clear();
                    Iterator<String> itKeys3 = jSONObjectOptJSONObject2.keys();
                    while (itKeys3.hasNext()) {
                        String next3 = itKeys3.next();
                        JSONObject jSONObject3 = jSONObjectOptJSONObject2.getJSONObject(next3);
                        ArrayList<d> arrayList2 = new ArrayList<>();
                        Iterator<String> itKeys4 = jSONObject3.keys();
                        while (itKeys4.hasNext()) {
                            String next4 = itKeys4.next();
                            arrayList2.add(new d(next4, jSONObject3.getJSONObject(next4)));
                        }
                        this.f522g.put(next3, arrayList2);
                    }
                    if (!this.f522g.isEmpty()) {
                        this.f520e.add(this.f522g);
                    }
                } else {
                    this.f518c = false;
                }
                JSONObject jSONObjectOptJSONObject3 = jSONObject.optJSONObject("gfBen");
                if (jSONObjectOptJSONObject3 != null) {
                    this.f519d = true;
                    this.f523h.clear();
                    Iterator<String> itKeys5 = jSONObjectOptJSONObject3.keys();
                    while (itKeys5.hasNext()) {
                        String next5 = itKeys5.next();
                        JSONObject jSONObject4 = jSONObjectOptJSONObject3.getJSONObject(next5);
                        ArrayList<d> arrayList3 = new ArrayList<>();
                        Iterator<String> itKeys6 = jSONObject4.keys();
                        while (itKeys6.hasNext()) {
                            String next6 = itKeys6.next();
                            arrayList3.add(new d(next6, jSONObject4.getJSONObject(next6)));
                        }
                        this.f523h.put(next5, arrayList3);
                    }
                    if (!this.f523h.isEmpty()) {
                        this.f520e.add(this.f523h);
                    }
                } else {
                    this.f519d = false;
                }
                JSONObject jSONObjectOptJSONObject4 = jSONObject.optJSONObject("sched_cmds");
                if (jSONObjectOptJSONObject4 != null) {
                    this.f524i.clear();
                    Iterator<String> itKeys7 = jSONObjectOptJSONObject4.keys();
                    while (itKeys7.hasNext()) {
                        String next7 = itKeys7.next();
                        ArrayList arrayList4 = new ArrayList();
                        ArrayList<String> arrayList5 = new ArrayList<>();
                        JSONArray jSONArrayOptJSONArray = jSONObjectOptJSONObject4.optJSONArray(next7);
                        if (jSONArrayOptJSONArray != null) {
                            for (int i2 = 0; i2 < jSONArrayOptJSONArray.length(); i2++) {
                                String strOptString4 = jSONArrayOptJSONArray.optString(i2, "");
                                arrayList4.add(new a(strOptString4));
                                arrayList5.add(strOptString4);
                            }
                        }
                        this.f524i.put(next7, arrayList5);
                    }
                }
                JSONObject jSONObjectOptJSONObject5 = jSONObject.optJSONObject("q_perflock");
                if (jSONObjectOptJSONObject5 != null) {
                    this.f525j.clear();
                    Iterator<String> itKeys8 = jSONObjectOptJSONObject5.keys();
                    while (itKeys8.hasNext()) {
                        String next8 = itKeys8.next();
                        JSONArray jSONArrayOptJSONArray2 = jSONObjectOptJSONObject5.optJSONArray(next8);
                        if (jSONArrayOptJSONArray2 != null) {
                            strOptString3 = "";
                            for (int i3 = 0; i3 < jSONArrayOptJSONArray2.length(); i3++) {
                                strOptString3 = jSONArrayOptJSONArray2.optString(i3, "");
                            }
                        } else {
                            strOptString3 = "";
                        }
                        this.f525j.put(next8, strOptString3);
                    }
                }
                JSONObject jSONObjectOptJSONObject6 = jSONObject.optJSONObject("m_perflock");
                if (jSONObjectOptJSONObject6 != null) {
                    this.f526k.clear();
                    Iterator<String> itKeys9 = jSONObjectOptJSONObject6.keys();
                    while (itKeys9.hasNext()) {
                        String next9 = itKeys9.next();
                        JSONArray jSONArrayOptJSONArray3 = jSONObjectOptJSONObject6.optJSONArray(next9);
                        if (jSONArrayOptJSONArray3 != null) {
                            strOptString2 = "";
                            for (int i4 = 0; i4 < jSONArrayOptJSONArray3.length(); i4++) {
                                strOptString2 = jSONArrayOptJSONArray3.optString(i4, "");
                            }
                        } else {
                            strOptString2 = "";
                        }
                        this.f526k.put(next9, strOptString2);
                    }
                }
                JSONObject jSONObjectOptJSONObject7 = jSONObject.optJSONObject("xr_perflock");
                if (jSONObjectOptJSONObject7 != null) {
                    this.f527l.clear();
                    Iterator<String> itKeys10 = jSONObjectOptJSONObject7.keys();
                    while (itKeys10.hasNext()) {
                        String next10 = itKeys10.next();
                        JSONArray jSONArrayOptJSONArray4 = jSONObjectOptJSONObject7.optJSONArray(next10);
                        if (jSONArrayOptJSONArray4 != null) {
                            strOptString = "";
                            for (int i5 = 0; i5 < jSONArrayOptJSONArray4.length(); i5++) {
                                strOptString = jSONArrayOptJSONArray4.optString(i5, "");
                            }
                        } else {
                            strOptString = "";
                        }
                        this.f527l.put(next10, strOptString);
                    }
                }
                d();
            } catch (Exception e2) {
                if (PowerLog.V) {
                    PowerLog.v("PeGameController_NewAdvanceSchedApps", "update fail Exception ex:" + e2.getMessage());
                }
            }
        }
    }
}
