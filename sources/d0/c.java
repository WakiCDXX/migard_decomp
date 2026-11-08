package d0;

import android.text.TextUtils;
import com.ot.pubsub.util.k;
import com.xiaomi.smack.packet.CommonPacketExtension;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class c {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static volatile int f1760a = 1;

    private static String a(List<v.a> list) {
        if (list == null) {
            return "";
        }
        try {
            JSONObject jSONObject = new JSONObject();
            JSONArray jSONArray = new JSONArray();
            for (v.a aVar : list) {
                JSONObject jSONObject2 = new JSONObject();
                Map<String, String> mapE = aVar.e();
                if (mapE != null) {
                    JSONObject jSONObject3 = new JSONObject();
                    for (Map.Entry<String, String> entry : mapE.entrySet()) {
                        if (!TextUtils.isEmpty(entry.getKey())) {
                            jSONObject3.put(entry.getKey(), entry.getValue());
                        }
                    }
                    jSONObject2.put(CommonPacketExtension.ATTRIBUTE_NAME, jSONObject3);
                }
                jSONObject2.put("data", aVar.d());
                jSONArray.put(jSONObject2);
            }
            jSONObject.put("messages", jSONArray);
            return jSONObject.toString();
        } catch (Exception unused) {
            return "";
        }
    }

    public static void b(a aVar) {
        try {
            for (Map.Entry<String, List<v.a>> entry : j(aVar.f1754a).entrySet()) {
                String key = entry.getKey();
                List<v.a> value = entry.getValue();
                if (value != null && value.size() != 0) {
                    for (Map.Entry<String, List<v.a>> entry2 : l(value).entrySet()) {
                        String key2 = entry2.getKey();
                        List<v.a> value2 = entry2.getValue();
                        if (value2 != null && value2.size() != 0) {
                            value2.removeAll(f(key, key2, value2));
                            i(key, key2, value2);
                        }
                    }
                }
            }
        } catch (Throwable unused) {
        }
    }

    private static void c(String str, String str2, List<v.a> list) throws Throwable {
        if (list == null || list.size() <= 0) {
            return;
        }
        d.b bVarK = k(str, str2, g(list));
        k.c("UploaderDispense", "uploadGzipEncryptData success:" + bVarK.f1769a);
        if (bVarK.f1769a) {
            k.c("UploaderDispense", "uploadGzipEncryptData deleted:" + y.f.e().a(list));
            return;
        }
        if (bVarK.f1770b) {
            k.c("UploaderDispense", "uploadGzipEncryptData deleted:" + y.f.e().j(list));
        }
    }

    private static byte[] d(String str) throws Throwable {
        GZIPOutputStream gZIPOutputStream;
        ByteArrayOutputStream byteArrayOutputStream;
        ByteArrayOutputStream byteArrayOutputStream2 = null;
        byte[] byteArray = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream(str.getBytes("UTF-8").length);
            try {
                gZIPOutputStream = new GZIPOutputStream(byteArrayOutputStream);
                try {
                    try {
                        gZIPOutputStream.write(str.getBytes("UTF-8"));
                        gZIPOutputStream.finish();
                        byteArray = byteArrayOutputStream.toByteArray();
                    } catch (Exception e2) {
                        e = e2;
                        k.h("UploaderDispense", " zipData failed! " + e.toString());
                    }
                } catch (Throwable th) {
                    th = th;
                    byteArrayOutputStream2 = byteArrayOutputStream;
                    com.ot.pubsub.util.i.f(byteArrayOutputStream2);
                    com.ot.pubsub.util.i.f(gZIPOutputStream);
                    throw th;
                }
            } catch (Exception e3) {
                e = e3;
                gZIPOutputStream = null;
            } catch (Throwable th2) {
                th = th2;
                gZIPOutputStream = null;
                byteArrayOutputStream2 = byteArrayOutputStream;
                com.ot.pubsub.util.i.f(byteArrayOutputStream2);
                com.ot.pubsub.util.i.f(gZIPOutputStream);
                throw th;
            }
        } catch (Exception e4) {
            e = e4;
            byteArrayOutputStream = null;
            gZIPOutputStream = null;
        } catch (Throwable th3) {
            th = th3;
            gZIPOutputStream = null;
            com.ot.pubsub.util.i.f(byteArrayOutputStream2);
            com.ot.pubsub.util.i.f(gZIPOutputStream);
            throw th;
        }
        com.ot.pubsub.util.i.f(byteArrayOutputStream);
        com.ot.pubsub.util.i.f(gZIPOutputStream);
        return byteArray;
    }

    private static byte[] e(byte[] bArr, String str) {
        if (bArr != null) {
            return u.a.e(bArr, u.c.c(str));
        }
        k.h("UploaderDispense", "content is null");
        return null;
    }

    private static List<v.a> f(String str, String str2, List<v.a> list) throws Throwable {
        ArrayList arrayList = new ArrayList();
        for (v.a aVar : list) {
            if (aVar != null && aVar.g() == f1760a) {
                arrayList.add(aVar);
            }
        }
        if (arrayList.size() > 0) {
            d.b bVarK = k(str, str2, g(arrayList));
            k.c("UploaderDispense", "uploadGzipEncryptData success:" + bVarK.f1769a);
            if (bVarK.f1769a) {
                k.c("UploaderDispense", "uploadGzipEncryptData deleted:" + y.c.d().a(arrayList));
            } else if (bVarK.f1770b) {
                k.c("UploaderDispense", "uploadGzipEncryptData deleted:" + y.c.d().i(arrayList));
            }
        }
        return arrayList;
    }

    private static List<v.a> g(List<v.a> list) throws Throwable {
        if (list == null || list.size() == 0) {
            k.c("UploaderDispense", "messages is empty, return null");
            return null;
        }
        HashMap map = new HashMap();
        ArrayList arrayList = new ArrayList();
        String str = u.f.a().b()[1];
        String str2 = u.f.a().b()[0];
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return null;
        }
        try {
            for (v.a aVar : list) {
                Map<String, String> mapE = aVar.e();
                if (mapE != null) {
                    if (map.get(mapE) == null) {
                        map.put(mapE, new JSONArray());
                    }
                    ((JSONArray) map.get(mapE)).put(aVar.d());
                }
            }
            for (Map.Entry entry : map.entrySet()) {
                Map map2 = (Map) entry.getKey();
                map2.put("pub_gzipencrypt", "true");
                map2.put("pub_sid", str);
                JSONArray jSONArray = (JSONArray) entry.getValue();
                k.c("UploaderDispense", "zip before : " + jSONArray.toString().length());
                byte[] bArrD = d(jSONArray.toString());
                k.c("UploaderDispense", "zip after : " + bArrD.length);
                arrayList.add(new v.a(-1L, null, null, u.c.e(e(bArrD, str2)), map2, 1, -1L));
            }
        } catch (Exception e2) {
            k.c("UploaderDispense", "sortByAttribute error: " + e2.getMessage());
        }
        return arrayList;
    }

    public static void h(a aVar) {
        try {
            for (Map.Entry<String, List<v.a>> entry : j(aVar.f1754a).entrySet()) {
                String key = entry.getKey();
                List<v.a> value = entry.getValue();
                if (value != null && value.size() != 0) {
                    for (Map.Entry<String, List<v.a>> entry2 : l(value).entrySet()) {
                        String key2 = entry2.getKey();
                        List<v.a> value2 = entry2.getValue();
                        if (value2 != null && value2.size() != 0) {
                            c(key, key2, value2);
                        }
                    }
                }
            }
        } catch (Throwable unused) {
        }
    }

    private static List<v.a> i(String str, String str2, List<v.a> list) throws Throwable {
        if (list != null && list.size() > 0) {
            d.b bVarK = k(str, str2, list);
            k.c("UploaderDispense", "uploadData success:" + bVarK.f1769a);
            if (bVarK.f1769a) {
                k.c("UploaderDispense", "upload deleted:" + y.c.d().a(list));
            } else if (bVarK.f1770b) {
                k.c("UploaderDispense", "upload deleted:" + y.c.d().i(list));
            }
        }
        return list;
    }

    private static Map<String, List<v.a>> j(List<v.a> list) {
        HashMap map = new HashMap();
        for (int i2 = 0; i2 < list.size(); i2++) {
            v.a aVar = list.get(i2);
            String strB = aVar.b();
            if (map.get(strB) == null) {
                map.put(strB, new ArrayList());
            }
            ((List) map.get(strB)).add(aVar);
        }
        return map;
    }

    private static d.b k(String str, String str2, List<v.a> list) throws Throwable {
        try {
            if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2) && list != null && list.size() != 0) {
                String strB = c0.b.a().b(str);
                if (TextUtils.isEmpty(strB)) {
                    return new d.b(false, false);
                }
                a0.a aVarA = b0.a.a(String.format("%s/v1/projects/%s/topics/%s:publish", "https://pubsub.googleapis.com", str, str2), a(list), strB);
                if (aVarA != null) {
                    int i2 = aVarA.f5a;
                    if (i2 == 401) {
                        c0.b.a().f(str);
                    } else if (i2 == 404) {
                        if ("NOT_FOUND".equals(aVarA.f8d) && !TextUtils.isEmpty(aVarA.f7c) && (aVarA.f7c.contains(str) || aVarA.f7c.contains(str2))) {
                            return new d.b(false, true);
                        }
                    } else if (i2 == 200) {
                        return new d.b(true, false);
                    }
                }
                return new d.b(false, false);
            }
            return new d.b(false, false);
        } catch (Exception e2) {
            k.i("UploaderDispense", "Exception while uploading ", e2);
        }
    }

    private static Map<String, List<v.a>> l(List<v.a> list) {
        HashMap map = new HashMap();
        for (int i2 = 0; i2 < list.size(); i2++) {
            v.a aVar = list.get(i2);
            String strC = aVar.c();
            if (map.get(strC) == null) {
                map.put(strC, new ArrayList());
            }
            ((List) map.get(strC)).add(aVar);
        }
        return map;
    }
}
