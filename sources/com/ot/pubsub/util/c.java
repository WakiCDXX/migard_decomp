package com.ot.pubsub.util;

import android.text.TextUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class c {
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r7v0, types: [byte[]] */
    /* JADX WARN: Type inference failed for: r7v2 */
    /* JADX WARN: Type inference failed for: r7v4, types: [java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r7v7 */
    public static Object a(byte[] bArr) throws Throwable {
        ByteArrayInputStream byteArrayInputStream;
        Throwable th;
        ObjectInputStream objectInputStream;
        try {
            try {
                byteArrayInputStream = new ByteArrayInputStream(bArr);
                try {
                    objectInputStream = new ObjectInputStream(byteArrayInputStream);
                    try {
                        Object object = objectInputStream.readObject();
                        i.e(byteArrayInputStream);
                        i.e(objectInputStream);
                        return object;
                    } catch (Exception e2) {
                        e = e2;
                        k.h("DataConvertUtil", "deserialize error:" + e.getMessage());
                        i.e(byteArrayInputStream);
                        i.e(objectInputStream);
                        return null;
                    }
                } catch (Exception e3) {
                    e = e3;
                    objectInputStream = null;
                } catch (Throwable th2) {
                    th = th2;
                    bArr = 0;
                    i.e(byteArrayInputStream);
                    i.e(bArr);
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
            }
        } catch (Exception e4) {
            e = e4;
            objectInputStream = null;
            byteArrayInputStream = null;
        } catch (Throwable th4) {
            byteArrayInputStream = null;
            th = th4;
            bArr = 0;
        }
    }

    public static String b(Map<String, String> map) {
        if (map != null && !map.isEmpty()) {
            try {
                return new JSONObject(map).toString();
            } catch (Exception e2) {
                k.h("DataConvertUtil", "map to json error:" + e2.getMessage());
            }
        }
        return "";
    }

    public static Map<String, String> c(String str) {
        if (TextUtils.isEmpty(str)) {
            return new HashMap();
        }
        HashMap map = new HashMap();
        try {
            JSONObject jSONObject = new JSONObject(str);
            Iterator<String> itKeys = jSONObject.keys();
            while (itKeys.hasNext()) {
                String next = itKeys.next();
                map.put(next, jSONObject.optString(next));
            }
        } catch (Exception e2) {
            k.h("DataConvertUtil", "json to map error:" + e2.getMessage());
        }
        return map;
    }

    public static byte[] d(Object obj) throws Throwable {
        ByteArrayOutputStream byteArrayOutputStream;
        ObjectOutputStream objectOutputStream;
        ByteArrayOutputStream byteArrayOutputStream2 = null;
        try {
            try {
                byteArrayOutputStream = new ByteArrayOutputStream();
                try {
                    objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                    try {
                        objectOutputStream.writeObject(obj);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        i.f(byteArrayOutputStream);
                        i.f(objectOutputStream);
                        return byteArray;
                    } catch (IOException e2) {
                        e = e2;
                        k.h("DataConvertUtil", "serialize error:" + e.getMessage());
                        i.f(byteArrayOutputStream);
                        i.f(objectOutputStream);
                        return null;
                    }
                } catch (IOException e3) {
                    e = e3;
                    objectOutputStream = null;
                } catch (Throwable th) {
                    th = th;
                    byteArrayOutputStream2 = byteArrayOutputStream;
                    i.f(byteArrayOutputStream2);
                    i.f(0);
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (IOException e4) {
            e = e4;
            byteArrayOutputStream = null;
            objectOutputStream = null;
        } catch (Throwable th3) {
            th = th3;
            i.f(byteArrayOutputStream2);
            i.f(0);
            throw th;
        }
    }
}
