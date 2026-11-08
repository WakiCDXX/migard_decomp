package com.xiaomi.onetrack.c;

import android.os.HandlerThread;
import android.text.TextUtils;
import com.xiaomi.onetrack.util.x;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.GZIPOutputStream;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class t {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final String f1318a = "config";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final String f1319b = "appId";

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final String f1320c = "version";

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static final String f1321d = "UploaderEngine";

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static final String f1322e = "code";

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static final String f1323f = "UTF-8";

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private p f1324g;

    private static class a {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private static final t f1325a = new t();

        private a() {
        }
    }

    public static t a() {
        return a.f1325a;
    }

    private void b() {
        HandlerThread handlerThread = new HandlerThread("onetrack_uploader_worker");
        handlerThread.start();
        this.f1324g = new p(handlerThread.getLooper());
    }

    private t() {
        b();
    }

    public synchronized void a(int i2, boolean z2) {
        try {
            p pVar = this.f1324g;
            if (pVar != null) {
                pVar.a(i2, z2);
            } else {
                com.xiaomi.onetrack.util.p.b(f1321d, "*** impossible, upload timer should not be null");
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    private boolean b(String str) {
        boolean z2 = false;
        try {
            JSONObject jSONObject = new JSONObject(str);
            int iOptInt = jSONObject.optInt("code");
            if (iOptInt == 0) {
                com.xiaomi.onetrack.util.p.a(f1321d, "成功发送数据到服务端");
                com.xiaomi.onetrack.b.a.a(jSONObject);
                z2 = true;
            } else if (iOptInt == -3) {
                com.xiaomi.onetrack.util.p.b(f1321d, "signature expired, will update");
                com.xiaomi.onetrack.d.f.a().c();
            } else {
                com.xiaomi.onetrack.util.p.b(f1321d, "Error: status code=" + iOptInt);
            }
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b(f1321d, "parseUploadingResult exception ", e2);
        }
        return z2;
    }

    public boolean a(int i2) {
        h hVarA;
        com.xiaomi.onetrack.util.p.a(f1321d, "即将读取数据库并上传数据");
        do {
            hVarA = c.a().a(i2);
            if (hVarA == null) {
                com.xiaomi.onetrack.util.p.a(f1321d, "满足条件的记录为空，即将返回, priority=" + i2);
                return true;
            }
            ArrayList<Long> arrayList = hVarA.f1270c;
            boolean zA = a(hVarA.f1268a);
            com.xiaomi.onetrack.util.p.a(f1321d, "upload success:" + zA);
            if (!zA) {
                return false;
            }
            if (c.a().a(arrayList) == 0) {
                com.xiaomi.onetrack.util.p.b(f1321d, "delete DB failed!", new Throwable());
            }
            return true;
        } while (!hVarA.f1271d);
        com.xiaomi.onetrack.util.p.a(f1321d, "No more records for prio=" + i2);
        return true;
    }

    private boolean a(JSONArray jSONArray) {
        try {
            String strB = x.a().b();
            String string = jSONArray.toString();
            com.xiaomi.onetrack.util.p.a(f1321d, " payload:" + string);
            byte[] bArrA = a(a(string));
            com.xiaomi.onetrack.util.p.a(f1321d, "before zip and encrypt, len=" + string.length() + ", after=" + bArrA.length);
            String strA = com.xiaomi.onetrack.g.b.a(strB, bArrA);
            StringBuilder sb = new StringBuilder();
            sb.append("sendDataToServer response: ");
            sb.append(strA);
            com.xiaomi.onetrack.util.p.a(f1321d, sb.toString());
            if (TextUtils.isEmpty(strA)) {
                return false;
            }
            return b(strA);
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b(f1321d, "Exception while uploading ", e2);
            return false;
        }
    }

    private static byte[] a(String str) throws Throwable {
        GZIPOutputStream gZIPOutputStream;
        ByteArrayOutputStream byteArrayOutputStream;
        ByteArrayOutputStream byteArrayOutputStream2 = null;
        byte[] byteArray = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream(str.getBytes(f1323f).length);
            try {
                gZIPOutputStream = new GZIPOutputStream(byteArrayOutputStream);
                try {
                    try {
                        gZIPOutputStream.write(str.getBytes(f1323f));
                        gZIPOutputStream.finish();
                        byteArray = byteArrayOutputStream.toByteArray();
                    } catch (Exception e2) {
                        e = e2;
                        com.xiaomi.onetrack.util.p.b(f1321d, " zipData failed! " + e.toString());
                    }
                } catch (Throwable th) {
                    th = th;
                    byteArrayOutputStream2 = byteArrayOutputStream;
                    com.xiaomi.onetrack.util.m.a((OutputStream) byteArrayOutputStream2);
                    com.xiaomi.onetrack.util.m.a((OutputStream) gZIPOutputStream);
                    throw th;
                }
            } catch (Exception e3) {
                e = e3;
                gZIPOutputStream = null;
            } catch (Throwable th2) {
                th = th2;
                gZIPOutputStream = null;
                byteArrayOutputStream2 = byteArrayOutputStream;
                com.xiaomi.onetrack.util.m.a((OutputStream) byteArrayOutputStream2);
                com.xiaomi.onetrack.util.m.a((OutputStream) gZIPOutputStream);
                throw th;
            }
        } catch (Exception e4) {
            e = e4;
            byteArrayOutputStream = null;
            gZIPOutputStream = null;
        } catch (Throwable th3) {
            th = th3;
            gZIPOutputStream = null;
            com.xiaomi.onetrack.util.m.a((OutputStream) byteArrayOutputStream2);
            com.xiaomi.onetrack.util.m.a((OutputStream) gZIPOutputStream);
            throw th;
        }
        com.xiaomi.onetrack.util.m.a((OutputStream) byteArrayOutputStream);
        com.xiaomi.onetrack.util.m.a((OutputStream) gZIPOutputStream);
        return byteArray;
    }

    private byte[] a(byte[] bArr) {
        if (bArr == null) {
            com.xiaomi.onetrack.util.p.b(f1321d, "content is null");
            return null;
        }
        return com.xiaomi.onetrack.d.a.a(bArr, com.xiaomi.onetrack.d.c.a(com.xiaomi.onetrack.d.f.a().b()[0]));
    }
}
