package com.xiaomi.onetrack.c;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class i {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final long f1272a = 0;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final long f1273b = 1;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final long f1274c = 2;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final String f1275d = "eventName";

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final String f1276e = "data";

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static final String f1277f = "NetworkAccessManager";

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private static final String f1278g = "networkAccess";

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private static String f1279h = "onetrack_netaccess_%s";

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private static SimpleDateFormat f1280i = new SimpleDateFormat("yyyyMMdd");

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private static boolean f1281j = false;

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private static volatile boolean f1282k = true;

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    private static volatile boolean f1283l = false;

    public static boolean a() {
        return f1281j;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static synchronized void c(String str, String str2) {
        FileWriter fileWriter;
        File file = new File(e(), String.format(f1279h, f1280i.format(new Date())));
        BufferedWriter bufferedWriter = null;
        try {
            if (!file.exists()) {
                if (file.getParentFile().exists()) {
                    file.createNewFile();
                } else {
                    new File(file.getParentFile().getAbsolutePath()).mkdirs();
                    file.createNewFile();
                }
            }
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("eventName", str);
            jSONObject.put("data", str2);
            byte[] bArrA = c.a(jSONObject.toString());
            fileWriter = new FileWriter(file, true);
            try {
                try {
                    BufferedWriter bufferedWriter2 = new BufferedWriter(fileWriter);
                    try {
                        bufferedWriter2.write(com.xiaomi.onetrack.d.c.a(bArrA));
                        bufferedWriter2.newLine();
                        f1282k = true;
                        com.xiaomi.onetrack.util.m.a(bufferedWriter2);
                    } catch (Exception e2) {
                        e = e2;
                        bufferedWriter = bufferedWriter2;
                        com.xiaomi.onetrack.util.p.b(f1277f, "cta doSaveData error: " + e.toString());
                        e.printStackTrace();
                        com.xiaomi.onetrack.util.m.a(bufferedWriter);
                    } catch (Throwable th) {
                        th = th;
                        bufferedWriter = bufferedWriter2;
                        com.xiaomi.onetrack.util.m.a(bufferedWriter);
                        com.xiaomi.onetrack.util.m.a(fileWriter);
                        throw th;
                    }
                } catch (Exception e3) {
                    e = e3;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e4) {
            e = e4;
            fileWriter = null;
        } catch (Throwable th3) {
            th = th3;
            fileWriter = null;
        }
        com.xiaomi.onetrack.util.m.a(fileWriter);
    }

    private static String e() {
        return com.xiaomi.onetrack.f.a.a().getFilesDir().getAbsolutePath() + File.separator + f1278g;
    }

    public static void a(boolean z2) {
        f1281j = z2;
    }

    public static boolean b() {
        return !new File(com.xiaomi.onetrack.f.a.a().getFilesDir(), ".ot_net_disallowed").exists();
    }

    public static boolean d() {
        return !f1283l && f1282k;
    }

    public static void a(String str, String str2) {
        com.xiaomi.onetrack.util.i.a(new j(str, str2));
    }

    public static synchronized void a(com.xiaomi.onetrack.api.g gVar) {
        if (d()) {
            if (gVar != null && b()) {
                f1283l = true;
                com.xiaomi.onetrack.util.i.a(new k(gVar));
            }
        }
    }

    public static void b(boolean z2) {
        File file = new File(com.xiaomi.onetrack.f.a.a().getFilesDir(), ".ot_net_allowed");
        File file2 = new File(com.xiaomi.onetrack.f.a.a().getFilesDir(), ".ot_net_disallowed");
        try {
            if (z2) {
                file.createNewFile();
                if (file2.exists()) {
                    file2.delete();
                }
            } else {
                file2.createNewFile();
                if (file.exists()) {
                    file.delete();
                }
            }
        } catch (IOException e2) {
            com.xiaomi.onetrack.util.p.b(f1277f, "setNetworkAccessStateEnabled: " + z2 + "failed ", e2);
        }
    }

    public static synchronized void c(boolean z2) {
        try {
            try {
                File file = new File(e());
                if (file.exists() && file.isDirectory()) {
                    String str = String.format(f1279h, f1280i.format(new Date()));
                    File[] fileArrListFiles = file.listFiles();
                    for (int i2 = 0; i2 < fileArrListFiles.length; i2++) {
                        if (fileArrListFiles[i2].isFile() && (z2 || !fileArrListFiles[i2].getName().equalsIgnoreCase(str))) {
                            fileArrListFiles[i2].delete();
                        }
                    }
                    if (file.listFiles().length == 0) {
                        f1282k = false;
                    }
                    return;
                }
                f1282k = false;
            } catch (Exception e2) {
                com.xiaomi.onetrack.util.p.b(f1277f, "cta removeObsoleteEvent error: " + e2.toString());
                e2.printStackTrace();
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r3v1 */
    /* JADX WARN: Type inference failed for: r3v13 */
    /* JADX WARN: Type inference failed for: r3v2 */
    /* JADX WARN: Type inference failed for: r3v3, types: [java.io.Closeable] */
    public static synchronized List<JSONObject> c() {
        FileReader fileReader;
        Exception e2;
        BufferedReader bufferedReader;
        File file = new File(e(), String.format(f1279h, f1280i.format(new Date())));
        ?? r3 = 0;
        r3 = 0;
        if (!file.exists()) {
            return null;
        }
        try {
            List<JSONObject> arrayList = new ArrayList<>();
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
                            com.xiaomi.onetrack.util.p.b(f1277f, "cta getCacheData error: " + e2.toString());
                            e2.printStackTrace();
                            com.xiaomi.onetrack.util.m.a(bufferedReader);
                        }
                    }
                    com.xiaomi.onetrack.util.m.a(bufferedReader);
                } catch (Exception e4) {
                    e2 = e4;
                    bufferedReader = null;
                } catch (Throwable th) {
                    th = th;
                    com.xiaomi.onetrack.util.m.a((Closeable) r3);
                    com.xiaomi.onetrack.util.m.a(fileReader);
                    throw th;
                }
            } catch (Exception e5) {
                fileReader = null;
                e2 = e5;
                bufferedReader = null;
            } catch (Throwable th2) {
                th = th2;
                fileReader = null;
            }
            com.xiaomi.onetrack.util.m.a(fileReader);
            if (arrayList.size() > 200) {
                arrayList = arrayList.subList(arrayList.size() - 200, arrayList.size());
            }
            if (arrayList.size() > 0) {
                f1282k = true;
            }
            return arrayList;
        } catch (Throwable th3) {
            th = th3;
            r3 = file;
        }
    }
}
