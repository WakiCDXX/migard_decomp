package com.xiaomi.onetrack.util;

import android.text.TextUtils;
import com.xiaomi.onetrack.Configuration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class r {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final String f1698a = "onetrack_";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final String f1699b = "ot_";

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static final String f1700c = "ParamUtil";

    public interface a {
        boolean a(Object obj);
    }

    public static JSONObject a(Map<String, Object> map, boolean z2) {
        return a(map, new s(z2));
    }

    private static boolean b(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean d(Object obj) {
        return obj instanceof Number;
    }

    public static JSONObject a(Map<String, Object> map) {
        return a(map, new t());
    }

    private static boolean c(String str) {
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

    private static JSONObject a(Map<String, Object> map, a aVar) {
        JSONObject jSONObject = new JSONObject();
        if (map == null) {
            return jSONObject;
        }
        try {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (!aVar.a(value)) {
                    if (p.f1665a) {
                        a(f1700c, entry.getKey());
                    }
                } else if (b(value)) {
                    jSONObject.put(key, value);
                } else if (value instanceof List) {
                    jSONObject.put(key, a((List) value));
                }
            }
        } catch (Exception e2) {
            p.b(f1700c, "checkParam error:" + e2.toString());
        }
        return jSONObject;
    }

    public static boolean b(Object obj) {
        return (obj instanceof Boolean) || (obj instanceof Number) || (obj instanceof String);
    }

    public static JSONArray a(List list) throws JSONException {
        if (list == null) {
            return null;
        }
        JSONArray jSONArray = new JSONArray();
        for (Object obj : list) {
            if (b(obj)) {
                jSONArray.put(obj);
            } else if (obj instanceof Map) {
                JSONObject jSONObject = new JSONObject();
                boolean z2 = false;
                for (Map.Entry entry : ((Map) obj).entrySet()) {
                    Object key = entry.getKey();
                    Object value = entry.getValue();
                    if ((key instanceof String) && b(value)) {
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

    public static void a(String str, String str2) {
        p.b(str, "key is " + str2 + ", the param value is invalid，please change the parameter type to string ,numeric, boolean！");
    }

    public static JSONObject a(JSONObject jSONObject, JSONObject jSONObject2) {
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
            p.b(f1700c, "merge error：" + e2.toString());
            return jSONObject;
        }
    }

    public static boolean a(Object obj) {
        return b(obj) || (obj instanceof List);
    }

    public static boolean a(String str) {
        if (!c(str)) {
            return false;
        }
        String lowerCase = str.toLowerCase();
        return (lowerCase.startsWith(f1698a) || lowerCase.startsWith(f1699b)) ? false : true;
    }

    public static String a(Configuration configuration) {
        StringBuilder sb = new StringBuilder();
        String appId = configuration.getAppId();
        String pluginId = configuration.getPluginId();
        if (!TextUtils.isEmpty(appId)) {
            sb.append(appId);
        }
        if (!TextUtils.isEmpty(pluginId)) {
            sb.append(pluginId);
        }
        return com.xiaomi.onetrack.d.d.h(sb.toString());
    }
}
