package com.xiaomi.onetrack.c;

import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class l {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final String f1287a = "eventName";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final String f1288b = "data";

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static final String f1289c = "SystemImpCacheManager";

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static final String f1290d = "systemimp_cache";

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static String f1291e = "systemimp_cache_%s";

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static volatile l f1292f = null;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private static String f1293g = "system_imp_cache_manager";

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private Handler f1294h;

    private l() {
        try {
            HandlerThread handlerThread = new HandlerThread(f1293g);
            handlerThread.start();
            this.f1294h = new Handler(handlerThread.getLooper());
        } catch (Throwable th) {
            com.xiaomi.onetrack.util.p.b(f1289c, "SystemImpCacheManager init Throwable: " + th.getMessage());
        }
    }

    public static void b() {
        if (f1292f == null) {
            synchronized (l.class) {
                try {
                    if (f1292f == null) {
                        f1292f = new l();
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    private static String c() {
        return com.xiaomi.onetrack.f.a.a().getFilesDir().getAbsolutePath() + File.separator + f1290d;
    }

    public static l a() {
        if (f1292f == null) {
            b();
        }
        return f1292f;
    }

    public synchronized void c(String str) {
        if (this.f1294h != null && !TextUtils.isEmpty(str)) {
            this.f1294h.post(new n(this, str));
        }
    }

    public void a(String str, String str2, String str3) {
        if (this.f1294h == null || TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return;
        }
        this.f1294h.post(new m(this, str, str2, str3));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void b(String str, String str2, String str3) {
        BufferedWriter bufferedWriter;
        FileWriter fileWriter = null;
        try {
            File file = new File(c(), String.format(f1291e, str));
            if (!file.exists()) {
                if (file.getParentFile().exists()) {
                    file.createNewFile();
                } else {
                    new File(file.getParentFile().getAbsolutePath()).mkdirs();
                    file.createNewFile();
                }
            }
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("eventName", str2);
            jSONObject.put("data", str3);
            byte[] bArrA = c.a(jSONObject.toString());
            FileWriter fileWriter2 = new FileWriter(file, true);
            try {
                bufferedWriter = new BufferedWriter(fileWriter2);
                try {
                    bufferedWriter.write(com.xiaomi.onetrack.d.c.a(bArrA));
                    bufferedWriter.newLine();
                    com.xiaomi.onetrack.util.m.a(bufferedWriter);
                    com.xiaomi.onetrack.util.m.a(fileWriter2);
                } catch (Throwable th) {
                    th = th;
                    fileWriter = fileWriter2;
                    try {
                        com.xiaomi.onetrack.util.p.b(f1289c, "systemimp doSaveData error: " + th.getMessage());
                        com.xiaomi.onetrack.util.m.a(bufferedWriter);
                        com.xiaomi.onetrack.util.m.a(fileWriter);
                    } catch (Throwable th2) {
                        com.xiaomi.onetrack.util.m.a(bufferedWriter);
                        com.xiaomi.onetrack.util.m.a(fileWriter);
                        throw th2;
                    }
                }
            } catch (Throwable th3) {
                th = th3;
                bufferedWriter = null;
            }
        } catch (Throwable th4) {
            th = th4;
            bufferedWriter = null;
        }
    }

    public synchronized void a(String str) {
        try {
            File file = new File(c());
            if (file.exists() && file.isDirectory()) {
                String str2 = String.format(f1291e, str);
                File[] fileArrListFiles = file.listFiles();
                for (int i2 = 0; i2 < fileArrListFiles.length; i2++) {
                    if (fileArrListFiles[i2].isFile() && fileArrListFiles[i2].getName().equalsIgnoreCase(str2)) {
                        fileArrListFiles[i2].delete();
                        break;
                    }
                }
            }
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b(f1289c, "systemimp removeObsoleteEvent error: " + e2.toString());
        }
    }

    /* JADX WARN: Not initialized variable reg: 2, insn: 0x004f: MOVE (r1 I:??[OBJECT, ARRAY]) = (r2 I:??[OBJECT, ARRAY]), block:B:20:0x004f */
    public static synchronized List<JSONObject> b(String str) {
        Closeable closeable;
        BufferedReader bufferedReader;
        FileReader fileReader;
        Exception e2;
        ArrayList arrayList;
        Closeable closeable2 = null;
        try {
            try {
                try {
                    try {
                        File file = new File(c(), String.format(f1291e, str));
                        if (!file.exists()) {
                            com.xiaomi.onetrack.util.m.a((Closeable) null);
                            com.xiaomi.onetrack.util.m.a((Closeable) null);
                            return null;
                        }
                        arrayList = new ArrayList();
                        try {
                            fileReader = new FileReader(file);
                            try {
                                bufferedReader = new BufferedReader(fileReader);
                                while (true) {
                                    try {
                                        String line = bufferedReader.readLine();
                                        if (line == null) {
                                            break;
                                        }
                                        arrayList.add(new JSONObject(c.a(com.xiaomi.onetrack.d.c.a(line))));
                                    } catch (Exception e3) {
                                        e2 = e3;
                                        com.xiaomi.onetrack.util.p.b(f1289c, "cta getCacheData error: " + e2.toString());
                                        e2.printStackTrace();
                                        com.xiaomi.onetrack.util.m.a(bufferedReader);
                                    }
                                }
                                com.xiaomi.onetrack.util.m.a(bufferedReader);
                            } catch (Exception e4) {
                                bufferedReader = null;
                                e2 = e4;
                            } catch (Throwable th) {
                                th = th;
                            }
                        } catch (Exception e5) {
                            fileReader = null;
                            e2 = e5;
                            bufferedReader = null;
                        }
                        com.xiaomi.onetrack.util.m.a(fileReader);
                        return arrayList;
                    } catch (Throwable th2) {
                        th = th2;
                        fileReader = null;
                    }
                } catch (Exception e6) {
                    bufferedReader = null;
                    fileReader = null;
                    e2 = e6;
                    arrayList = null;
                }
            } catch (Throwable th3) {
                th = th3;
                closeable2 = closeable;
            }
            com.xiaomi.onetrack.util.m.a(closeable2);
            com.xiaomi.onetrack.util.m.a(fileReader);
            throw th;
        } catch (Throwable th4) {
            throw th4;
        }
    }
}
