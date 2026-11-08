package b0;

import android.text.TextUtils;
import com.ot.pubsub.util.i;
import com.ot.pubsub.util.k;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import u.d;

/* JADX INFO: loaded from: classes.dex */
public class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static String f45a = "HttpUtil";

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v1 */
    /* JADX WARN: Type inference failed for: r0v2, types: [java.io.OutputStream] */
    /* JADX WARN: Type inference failed for: r0v3 */
    /* JADX WARN: Type inference failed for: r0v6 */
    /* JADX WARN: Type inference failed for: r9v1 */
    /* JADX WARN: Type inference failed for: r9v13 */
    /* JADX WARN: Type inference failed for: r9v14 */
    /* JADX WARN: Type inference failed for: r9v4, types: [java.io.OutputStream] */
    /* JADX WARN: Type inference failed for: r9v6 */
    /* JADX WARN: Type inference failed for: r9v7 */
    public static a0.a a(String str, String str2, String str3) throws Throwable {
        HttpURLConnection httpURLConnection;
        ?? r9;
        InputStream inputStream;
        ?? r0;
        InputStream inputStream2 = null;
        try {
            httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
            try {
                httpURLConnection.setConnectTimeout(60000);
                httpURLConnection.setReadTimeout(60000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("accept", "*/*");
                httpURLConnection.setRequestProperty("connection", "Keep-Alive");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestProperty("Authorization", str3);
                httpURLConnection.setDoOutput(true);
                byte[] bytes = str2.getBytes("UTF-8");
                OutputStream outputStream = httpURLConnection.getOutputStream();
                try {
                    outputStream.write(bytes, 0, bytes.length);
                    outputStream.flush();
                    int responseCode = httpURLConnection.getResponseCode();
                    a0.a aVar = new a0.a();
                    aVar.f5a = responseCode;
                    InputStream errorStream = responseCode != 200 ? httpURLConnection.getErrorStream() : httpURLConnection.getInputStream();
                    try {
                        aVar.a(new String(i.h(errorStream), "UTF-8"));
                        if (aVar.f5a == 200) {
                            k.c(f45a, "POST 成功 publishResponse:" + aVar.toString());
                        } else {
                            k.h(f45a, "POST 失败 publishResponse:" + aVar.toString());
                        }
                        i.e(errorStream);
                        i.f(outputStream);
                        i.g(httpURLConnection);
                        return aVar;
                    } catch (Exception e2) {
                        inputStream = errorStream;
                        e = e2;
                        r0 = outputStream;
                        try {
                            k.j(f45a, "HttpUtils POST 上传异常", e.getMessage());
                            i.e(inputStream);
                            i.f(r0);
                            i.g(httpURLConnection);
                            return null;
                        } catch (Throwable th) {
                            th = th;
                            inputStream2 = inputStream;
                            r9 = r0;
                            i.e(inputStream2);
                            i.f(r9);
                            i.g(httpURLConnection);
                            throw th;
                        }
                    } catch (Throwable th2) {
                        inputStream2 = errorStream;
                        th = th2;
                        r9 = outputStream;
                        i.e(inputStream2);
                        i.f(r9);
                        i.g(httpURLConnection);
                        throw th;
                    }
                } catch (Exception e3) {
                    e = e3;
                    r0 = outputStream;
                    inputStream = null;
                } catch (Throwable th3) {
                    th = th3;
                    r9 = outputStream;
                }
            } catch (Exception e4) {
                e = e4;
                inputStream = null;
                r0 = inputStream;
                k.j(f45a, "HttpUtils POST 上传异常", e.getMessage());
                i.e(inputStream);
                i.f(r0);
                i.g(httpURLConnection);
                return null;
            } catch (Throwable th4) {
                th = th4;
                r9 = 0;
            }
        } catch (Exception e5) {
            e = e5;
            httpURLConnection = null;
            inputStream = null;
        } catch (Throwable th5) {
            th = th5;
            httpURLConnection = null;
            r9 = 0;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v0 */
    /* JADX WARN: Type inference failed for: r2v1 */
    /* JADX WARN: Type inference failed for: r2v2, types: [java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r2v3 */
    /* JADX WARN: Type inference failed for: r2v4 */
    /* JADX WARN: Type inference failed for: r7v0, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r7v1, types: [java.net.HttpURLConnection] */
    /* JADX WARN: Type inference failed for: r7v2 */
    /* JADX WARN: Type inference failed for: r7v3 */
    /* JADX WARN: Type inference failed for: r7v4, types: [java.net.HttpURLConnection] */
    /* JADX WARN: Type inference failed for: r7v6, types: [java.net.HttpURLConnection] */
    /* JADX WARN: Type inference failed for: r8v0, types: [java.util.Map, java.util.Map<java.lang.String, java.lang.String>] */
    /* JADX WARN: Type inference failed for: r8v1, types: [java.io.OutputStream] */
    /* JADX WARN: Type inference failed for: r8v3 */
    /* JADX WARN: Type inference failed for: r8v4 */
    /* JADX WARN: Type inference failed for: r8v5, types: [java.io.OutputStream] */
    /* JADX WARN: Type inference failed for: r8v7 */
    /* JADX WARN: Type inference failed for: r8v8 */
    /* JADX WARN: Type inference failed for: r8v9, types: [java.io.OutputStream] */
    public static String b(String str, String str2, Map<String, String> map) throws Throwable {
        String strF;
        InputStream inputStream;
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        ?? r2 = 0;
        r2 = 0;
        r2 = 0;
        try {
            if (map == 0) {
                strF = null;
            } else {
                try {
                    strF = f(map, true);
                } catch (Exception e2) {
                    e = e2;
                    inputStream = null;
                    str2 = 0;
                    map = 0;
                    k.i(f45a, "HttpUtils POST 上传异常", e);
                    i.e(inputStream);
                    i.f(map);
                    i.g(str2);
                    return null;
                } catch (Throwable th) {
                    th = th;
                    str2 = 0;
                    map = 0;
                    i.e(r2);
                    i.f(map);
                    i.g(str2);
                    throw th;
                }
            }
            str2 = (HttpURLConnection) new URL(str2).openConnection();
            try {
                str2.setConnectTimeout(30000);
                str2.setReadTimeout(30000);
                str2.setRequestMethod("POST");
                str2.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                str2.setRequestProperty("Authorization", "Bearer " + str);
                str2.setDoOutput(true);
                byte[] bytes = strF.getBytes("UTF-8");
                map = str2.getOutputStream();
                try {
                    map.write(bytes, 0, bytes.length);
                    map.flush();
                    int responseCode = str2.getResponseCode();
                    k.c(f45a, "postGetToken responseData statusCode:" + responseCode);
                    inputStream = str2.getInputStream();
                    try {
                        String str3 = new String(i.h(inputStream), "UTF-8");
                        i.e(inputStream);
                        i.f(map);
                        i.g(str2);
                        return str3;
                    } catch (Exception e3) {
                        e = e3;
                        k.i(f45a, "HttpUtils POST 上传异常", e);
                        i.e(inputStream);
                        i.f(map);
                        i.g(str2);
                        return null;
                    }
                } catch (Exception e4) {
                    e = e4;
                    inputStream = null;
                } catch (Throwable th2) {
                    th = th2;
                    i.e(r2);
                    i.f(map);
                    i.g(str2);
                    throw th;
                }
            } catch (Exception e5) {
                e = e5;
                inputStream = null;
                map = 0;
            } catch (Throwable th3) {
                th = th3;
                map = 0;
            }
        } catch (Throwable th4) {
            th = th4;
            r2 = str;
        }
    }

    private static String c(String str, String str2, Map<String, String> map, boolean z2) throws Throwable {
        String strF;
        OutputStream outputStream;
        HttpURLConnection httpURLConnection;
        InputStream inputStream;
        String str3;
        InputStream inputStream2 = null;
        if (map == null) {
            strF = null;
        } else {
            try {
                strF = f(map, z2);
            } catch (Exception e2) {
                e = e2;
                outputStream = null;
                httpURLConnection = null;
                inputStream = null;
                k.i(f45a, "HttpUtils POST 上传异常", e);
                i.e(inputStream);
                i.f(outputStream);
                i.g(httpURLConnection);
                return null;
            } catch (Throwable th) {
                th = th;
                outputStream = null;
                httpURLConnection = null;
                i.e(inputStream2);
                i.f(outputStream);
                i.g(httpURLConnection);
                throw th;
            }
        }
        if (!"GET".equals(str) || strF == null) {
            str3 = str2;
        } else {
            str3 = str2 + "? " + strF;
        }
        httpURLConnection = (HttpURLConnection) new URL(str3).openConnection();
        try {
            httpURLConnection.setConnectTimeout(30000);
            httpURLConnection.setReadTimeout(30000);
            try {
                try {
                    if (!"GET".equals(str)) {
                        if ("POST".equals(str) && strF != null) {
                            httpURLConnection.setRequestMethod("POST");
                            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                            httpURLConnection.setDoOutput(true);
                            byte[] bytes = strF.getBytes("UTF-8");
                            outputStream = httpURLConnection.getOutputStream();
                            try {
                                outputStream.write(bytes, 0, bytes.length);
                                outputStream.flush();
                            } catch (Exception e3) {
                                e = e3;
                                inputStream = null;
                                k.i(f45a, "HttpUtils POST 上传异常", e);
                                i.e(inputStream);
                                i.f(outputStream);
                                i.g(httpURLConnection);
                                return null;
                            } catch (Throwable th2) {
                                th = th2;
                                i.e(inputStream2);
                                i.f(outputStream);
                                i.g(httpURLConnection);
                                throw th;
                            }
                        }
                        int responseCode = httpURLConnection.getResponseCode();
                        inputStream = httpURLConnection.getInputStream();
                        byte[] bArrH = i.h(inputStream);
                        k.c(f45a, String.format("HttpUtils POST 上传成功 url: %s, code: %s", str2, Integer.valueOf(responseCode)));
                        String str4 = new String(bArrH, "UTF-8");
                        i.e(inputStream);
                        i.f(outputStream);
                        i.g(httpURLConnection);
                        return str4;
                    }
                    httpURLConnection.setRequestMethod("GET");
                    byte[] bArrH2 = i.h(inputStream);
                    k.c(f45a, String.format("HttpUtils POST 上传成功 url: %s, code: %s", str2, Integer.valueOf(responseCode)));
                    String str5 = new String(bArrH2, "UTF-8");
                    i.e(inputStream);
                    i.f(outputStream);
                    i.g(httpURLConnection);
                    return str5;
                } catch (Exception e4) {
                    e = e4;
                    k.i(f45a, "HttpUtils POST 上传异常", e);
                    i.e(inputStream);
                    i.f(outputStream);
                    i.g(httpURLConnection);
                    return null;
                }
            } catch (Throwable th3) {
                th = th3;
                inputStream2 = inputStream;
                i.e(inputStream2);
                i.f(outputStream);
                i.g(httpURLConnection);
                throw th;
            }
            outputStream = null;
            int responseCode2 = httpURLConnection.getResponseCode();
            inputStream = httpURLConnection.getInputStream();
        } catch (Exception e5) {
            e = e5;
            outputStream = null;
            inputStream = null;
        } catch (Throwable th4) {
            th = th4;
            outputStream = null;
        }
    }

    public static String d(String str, Map<String, String> map, boolean z2) throws IOException {
        return c("POST", str, map, z2);
    }

    public static String e(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        if (map != null) {
            ArrayList<String> arrayList = new ArrayList(map.keySet());
            Collections.sort(arrayList);
            for (String str : arrayList) {
                if (!TextUtils.isEmpty(str)) {
                    sb.append(str);
                    sb.append(map.get(str));
                }
            }
        }
        sb.append("miui_sdkconfig_jafej!@#)(*e@!#");
        return d.h(sb.toString());
    }

    private static String f(Map<String, String> map, boolean z2) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            try {
                if (!TextUtils.isEmpty(entry.getKey())) {
                    if (sb.length() > 0) {
                        sb.append("&");
                    }
                    sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                    sb.append("=");
                    sb.append(URLEncoder.encode(entry.getValue() == null ? "null" : entry.getValue(), "UTF-8"));
                }
            } catch (UnsupportedEncodingException unused) {
                k.h(f45a, "format params failed");
            }
        }
        if (z2) {
            String strE = e(map);
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(URLEncoder.encode("sign", "UTF-8"));
            sb.append("=");
            sb.append(URLEncoder.encode(strE, "UTF-8"));
        }
        return sb.toString();
    }
}
