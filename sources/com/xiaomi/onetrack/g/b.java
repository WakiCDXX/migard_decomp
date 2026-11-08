package com.xiaomi.onetrack.g;

import android.text.TextUtils;
import com.xiaomi.onetrack.d.d;
import com.xiaomi.onetrack.d.f;
import com.xiaomi.onetrack.util.m;
import com.xiaomi.onetrack.util.p;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class b {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final int f1426a = 10000;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final int f1427b = 15000;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final String f1428c = "OT_SID";

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final String f1429d = "OT_ts";

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final String f1430e = "OT_net";

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public static final String f1431f = "OT_sender";

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public static final String f1432g = "OT_protocol";

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private static String f1433h = "HttpUtil";

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private static final String f1434i = "GET";

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private static final String f1435j = "POST";

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private static final String f1436k = "&";

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    private static final String f1437l = "=";

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    private static final String f1438m = "UTF-8";

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    private static final String f1439n = "miui_sdkconfig_jafej!@#)(*e@!#";

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    private static final int f1440o = 3;

    private b() {
    }

    public static String a(String str, byte[] bArr) throws IOException {
        HttpURLConnection httpURLConnection;
        OutputStream outputStream;
        InputStream inputStream;
        p.a(f1433h, "doPost url=" + str + ", len=" + bArr.length);
        InputStream inputStream2 = null;
        try {
            httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
            try {
                httpURLConnection.setConnectTimeout(10000);
                httpURLConnection.setReadTimeout(15000);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod(f1435j);
                httpURLConnection.setRequestProperty("Content-Type", "application/octet-stream");
                String str2 = f.a().b()[1];
                httpURLConnection.setRequestProperty(f1428c, str2);
                httpURLConnection.setRequestProperty(f1429d, Long.toString(System.currentTimeMillis()));
                httpURLConnection.setRequestProperty(f1430e, c.a(com.xiaomi.onetrack.f.a.b()).toString());
                httpURLConnection.setRequestProperty(f1431f, com.xiaomi.onetrack.f.a.e());
                httpURLConnection.setRequestProperty(f1432g, "3.0");
                p.a(f1433h, "sid:" + str2);
                outputStream = httpURLConnection.getOutputStream();
                try {
                    outputStream.write(bArr, 0, bArr.length);
                    outputStream.flush();
                    int responseCode = httpURLConnection.getResponseCode();
                    inputStream = httpURLConnection.getInputStream();
                    try {
                        try {
                            byte[] bArrB = m.b(inputStream);
                            p.a(f1433h, String.format("HttpUtils POST 上传成功 url: %s, code: %s", str, Integer.valueOf(responseCode)));
                            String str3 = new String(bArrB, f1438m);
                            m.a(inputStream);
                            m.a(outputStream);
                            m.a(httpURLConnection);
                            return str3;
                        } catch (IOException e2) {
                            e = e2;
                            p.b(f1433h, String.format("HttpUtils POST 上传失败, url: %s, error: %s", str, e.getMessage()));
                            m.a(inputStream);
                            m.a(outputStream);
                            m.a(httpURLConnection);
                            return null;
                        }
                    } catch (Throwable th) {
                        th = th;
                        inputStream2 = inputStream;
                        m.a(inputStream2);
                        m.a(outputStream);
                        m.a(httpURLConnection);
                        throw th;
                    }
                } catch (IOException e3) {
                    e = e3;
                    inputStream = null;
                } catch (Throwable th2) {
                    th = th2;
                    m.a(inputStream2);
                    m.a(outputStream);
                    m.a(httpURLConnection);
                    throw th;
                }
            } catch (IOException e4) {
                e = e4;
                outputStream = null;
                inputStream = outputStream;
                p.b(f1433h, String.format("HttpUtils POST 上传失败, url: %s, error: %s", str, e.getMessage()));
                m.a(inputStream);
                m.a(outputStream);
                m.a(httpURLConnection);
                return null;
            } catch (Throwable th3) {
                th = th3;
                outputStream = null;
            }
        } catch (IOException e5) {
            e = e5;
            httpURLConnection = null;
            outputStream = null;
        } catch (Throwable th4) {
            th = th4;
            httpURLConnection = null;
            outputStream = null;
        }
    }

    public static String b(String str, Map<String, String> map) throws IOException {
        return b(str, map, true);
    }

    public static String b(String str, Map<String, String> map, boolean z2) throws IOException {
        return a(f1435j, str, map, z2);
    }

    public static boolean b(String str) throws Throwable {
        if (TextUtils.isEmpty(str)) {
            p.a(f1433h, "doGetAdMonitor dbUrl is null");
            return true;
        }
        HttpURLConnection httpURLConnection = null;
        int i2 = 0;
        int i3 = 0;
        while (i2 < 3) {
            try {
                try {
                    try {
                        if (i3 / 100 == 3) {
                            i2++;
                            str = httpURLConnection.getHeaderField("Location");
                            p.a(f1433h, "redirect url is:" + str);
                        }
                        HttpURLConnection httpURLConnection2 = (HttpURLConnection) new URL(str).openConnection();
                        try {
                            httpURLConnection2.setInstanceFollowRedirects(false);
                            httpURLConnection2.setRequestMethod(f1434i);
                            httpURLConnection2.setConnectTimeout(10000);
                            httpURLConnection2.setReadTimeout(15000);
                            int responseCode = httpURLConnection2.getResponseCode();
                            p.a(f1433h, "AdMonitor get 请求url:" + str + "_ResponseCode：" + responseCode);
                            if (responseCode / 100 != 5 && responseCode / 100 != 3) {
                                try {
                                    m.a(httpURLConnection2);
                                } catch (Exception unused) {
                                }
                                return true;
                            }
                            if (responseCode / 100 != 3) {
                                m.a(httpURLConnection2);
                                return false;
                            }
                            i3 = responseCode;
                            httpURLConnection = httpURLConnection2;
                        } catch (ProtocolException e2) {
                            e = e2;
                            httpURLConnection = httpURLConnection2;
                            if (TextUtils.isEmpty(e.getMessage()) && e.getMessage().contains("200 OK")) {
                                p.a(f1433h, "response code is 200, bug status line is invalid.");
                                try {
                                    m.a(httpURLConnection);
                                } catch (Exception unused2) {
                                }
                                return true;
                            }
                            m.a(httpURLConnection);
                        } catch (Exception e3) {
                            e = e3;
                            httpURLConnection = httpURLConnection2;
                            p.b(f1433h, "HttpUtils doGetAdMonitor 上传异常:" + e.getMessage());
                            m.a(httpURLConnection);
                        } catch (Throwable th) {
                            th = th;
                            httpURLConnection = httpURLConnection2;
                            try {
                                m.a(httpURLConnection);
                            } catch (Exception unused3) {
                            }
                            throw th;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                    }
                } catch (ProtocolException e4) {
                    e = e4;
                } catch (Exception e5) {
                    e = e5;
                }
            } catch (Exception unused4) {
            }
        }
        p.a(f1433h, "redirectCount >= 3, return true");
        try {
            m.a(httpURLConnection);
        } catch (Exception unused5) {
        }
        return true;
    }

    public static String a(String str) throws IOException {
        return a(str, null, false);
    }

    public static String a(String str, Map<String, String> map) throws IOException {
        return a(str, map, true);
    }

    public static String a(String str, Map<String, String> map, boolean z2) throws IOException {
        return a(f1434i, str, map, z2);
    }

    private static String a(String str, String str2, Map<String, String> map, boolean z2) throws Throwable {
        String strA;
        OutputStream outputStream;
        HttpURLConnection httpURLConnection;
        InputStream inputStream;
        String str3;
        InputStream inputStream2 = null;
        if (map == null) {
            strA = null;
        } else {
            try {
                strA = a(map, z2);
            } catch (Exception e2) {
                e = e2;
                outputStream = null;
                httpURLConnection = null;
                inputStream = null;
                p.b(f1433h, "HttpUtils POST 上传异常", e);
                m.a(inputStream);
                m.a(outputStream);
                m.a(httpURLConnection);
                return null;
            } catch (Throwable th) {
                th = th;
                outputStream = null;
                httpURLConnection = null;
                m.a(inputStream2);
                m.a(outputStream);
                m.a(httpURLConnection);
                throw th;
            }
        }
        if (!f1434i.equals(str) || strA == null) {
            str3 = str2;
        } else {
            str3 = str2 + "? " + strA;
        }
        httpURLConnection = (HttpURLConnection) new URL(str3).openConnection();
        try {
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(15000);
            try {
                try {
                    if (f1434i.equals(str)) {
                        httpURLConnection.setRequestMethod(f1434i);
                    } else {
                        if (f1435j.equals(str) && strA != null) {
                            httpURLConnection.setRequestMethod(f1435j);
                            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                            httpURLConnection.setDoOutput(true);
                            byte[] bytes = strA.getBytes(f1438m);
                            outputStream = httpURLConnection.getOutputStream();
                            try {
                                outputStream.write(bytes, 0, bytes.length);
                                outputStream.flush();
                            } catch (Exception e3) {
                                e = e3;
                                inputStream = null;
                                p.b(f1433h, "HttpUtils POST 上传异常", e);
                                m.a(inputStream);
                                m.a(outputStream);
                                m.a(httpURLConnection);
                                return null;
                            } catch (Throwable th2) {
                                th = th2;
                                m.a(inputStream2);
                                m.a(outputStream);
                                m.a(httpURLConnection);
                                throw th;
                            }
                        }
                        int responseCode = httpURLConnection.getResponseCode();
                        inputStream = httpURLConnection.getInputStream();
                        byte[] bArrB = m.b(inputStream);
                        p.a(f1433h, String.format("HttpUtils POST 上传成功 url: %s, code: %s", str2, Integer.valueOf(responseCode)));
                        String str4 = new String(bArrB, f1438m);
                        m.a(inputStream);
                        m.a(outputStream);
                        m.a(httpURLConnection);
                        return str4;
                    }
                    byte[] bArrB2 = m.b(inputStream);
                    p.a(f1433h, String.format("HttpUtils POST 上传成功 url: %s, code: %s", str2, Integer.valueOf(responseCode)));
                    String str5 = new String(bArrB2, f1438m);
                    m.a(inputStream);
                    m.a(outputStream);
                    m.a(httpURLConnection);
                    return str5;
                } catch (Exception e4) {
                    e = e4;
                    p.b(f1433h, "HttpUtils POST 上传异常", e);
                    m.a(inputStream);
                    m.a(outputStream);
                    m.a(httpURLConnection);
                    return null;
                }
            } catch (Throwable th3) {
                th = th3;
                inputStream2 = inputStream;
                m.a(inputStream2);
                m.a(outputStream);
                m.a(httpURLConnection);
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

    private static String a(Map<String, String> map, boolean z2) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            try {
                if (!TextUtils.isEmpty(entry.getKey())) {
                    if (sb.length() > 0) {
                        sb.append(f1436k);
                    }
                    sb.append(URLEncoder.encode(entry.getKey(), f1438m));
                    sb.append(f1437l);
                    sb.append(URLEncoder.encode(entry.getValue() == null ? "null" : entry.getValue(), f1438m));
                }
            } catch (UnsupportedEncodingException unused) {
                p.b(f1433h, "format params failed");
            }
        }
        if (z2) {
            String strA = a(map);
            if (sb.length() > 0) {
                sb.append(f1436k);
            }
            sb.append(URLEncoder.encode("sign", f1438m));
            sb.append(f1437l);
            sb.append(URLEncoder.encode(strA, f1438m));
        }
        return sb.toString();
    }

    public static String a(Map<String, String> map) {
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
        sb.append(f1439n);
        return d.c(sb.toString());
    }
}
