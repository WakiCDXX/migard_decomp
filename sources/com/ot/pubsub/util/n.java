package com.ot.pubsub.util;

import android.text.TextUtils;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class n {

    public interface a {
        boolean a(Object obj);
    }

    public static String a(r.b bVar) {
        StringBuilder sb = new StringBuilder();
        String strB = bVar.b();
        if (!TextUtils.isEmpty(strB)) {
            sb.append(strB);
        }
        return u.d.j(sb.toString());
    }

    public static JSONArray b(List list) throws JSONException {
        if (list == null) {
            return null;
        }
        JSONArray jSONArray = new JSONArray();
        for (Object obj : list) {
            if (i(obj)) {
                jSONArray.put(obj);
            } else if (obj instanceof Map) {
                JSONObject jSONObject = new JSONObject();
                boolean z2 = false;
                for (Map.Entry entry : ((Map) obj).entrySet()) {
                    Object key = entry.getKey();
                    Object value = entry.getValue();
                    if ((key instanceof String) && i(value)) {
                        jSONObject.put((String) key, value);
                        z2 = true;
                    }
                }
                if (z2) {
                    jSONArray.put(jSONObject);
                }
            }
        }
        return jSONArray;
    }

    private static JSONObject c(Map<String, Object> map, a aVar) {
        JSONObject jSONObject = new JSONObject();
        if (map == null) {
            return jSONObject;
        }
        try {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (aVar.a(value)) {
                    if (i(value)) {
                        jSONObject.put(key, value);
                    } else if (value instanceof List) {
                        jSONObject.put(key, b((List) value));
                    }
                } else if (k.f724a) {
                    f("ParamUtil", entry.getKey());
                }
            }
        } catch (Exception e2) {
            k.h("ParamUtil", "checkParam error:" + e2.toString());
        }
        return jSONObject;
    }

    public static JSONObject d(Map<String, Object> map, boolean z2) {
        return c(map, new o(z2));
    }

    public static JSONObject e(JSONObject jSONObject, JSONObject jSONObject2) {
        if (jSONObject == null && jSONObject2 == null) {
            return new JSONObject();
        }
        if (jSONObject == null && jSONObject2 != null) {
            return jSONObject2;
        }
        if (jSONObject != null && jSONObject2 == null) {
            return jSONObject;
        }
        try {
            Iterator<String> itKeys = jSONObject.keys();
            while (itKeys.hasNext()) {
                String next = itKeys.next();
                jSONObject2.put(next, jSONObject.opt(next));
            }
            return jSONObject2;
        } catch (Exception e2) {
            k.h("ParamUtil", "merge error：" + e2.toString());
            return jSONObject;
        }
    }

    public static void f(String str, String str2) {
        k.h(str, "key is " + str2 + ", the param value is invalid，please change the parameter type to string ,numeric, boolean！");
    }

    public static boolean g(Object obj) {
        return i(obj) || (obj instanceof List);
    }

    public static boolean h(String str) {
        if (!j(str)) {
            return false;
        }
        String lowerCase = str.toLowerCase();
        return (lowerCase.startsWith(com.xiaomi.onetrack.util.r.f1698a) || lowerCase.startsWith(com.xiaomi.onetrack.util.r.f1699b)) ? false : true;
    }

    public static boolean i(Object obj) {
        return (obj instanceof Boolean) || (obj instanceof Number) || (obj instanceof String);
    }

    private static boolean j(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        char[] charArray = str.toCharArray();
        for (int i2 = 0; i2 < charArray.length; i2++) {
            char c2 = charArray[i2];
            if (i2 == 0 && Character.isDigit(c2)) {
                return false;
            }
            if (c2 != '_' && !Character.isDigit(c2) && ((c2 < 'a' || c2 > 'z') && (c2 < 'A' || c2 > 'Z'))) {
                return false;
            }
        }
        return true;
    }
}
