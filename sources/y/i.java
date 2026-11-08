package y;

import com.xiaomi.smack.packet.CommonPacketExtension;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class i {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static String f2432a = "ot_pubsub_netaccess_%s";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static SimpleDateFormat f2433b = new SimpleDateFormat("yyyyMMdd");

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static volatile boolean f2434c = true;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static volatile boolean f2435d = false;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static boolean f2436e = true;

    public static void a(String str, String str2, String str3, String str4, Map<String, String> map, boolean z2) {
        com.ot.pubsub.util.e.a(new j(str, str2, str3, str4, map, z2));
    }

    public static synchronized void b(s.b bVar) {
        if (!i()) {
            com.ot.pubsub.util.k.c("NetworkAccessManager", "analytics is publishing or no cache can be found，skip it!");
        } else if (!d()) {
            com.ot.pubsub.util.k.c("NetworkAccessManager", "network is unaccessable, cta");
        } else {
            f2435d = true;
            com.ot.pubsub.util.e.a(new k(bVar));
        }
    }

    public static void c(boolean z2) {
        f2436e = z2;
    }

    public static boolean d() {
        return f2436e;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r3v1 */
    /* JADX WARN: Type inference failed for: r3v13 */
    /* JADX WARN: Type inference failed for: r3v2 */
    /* JADX WARN: Type inference failed for: r3v3, types: [java.io.Closeable] */
    public static synchronized List<JSONObject> e() {
        FileReader fileReader;
        Exception e2;
        BufferedReader bufferedReader;
        File file = new File(k(), String.format(f2432a, f2433b.format(new Date())));
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
                            arrayList.add(new JSONObject(c.b(u.c.c(line))));
                        } catch (Exception e3) {
                            e2 = e3;
                            com.ot.pubsub.util.k.h("NetworkAccessManager", "cta getCacheData error: " + e2.toString());
                            e2.printStackTrace();
                            com.ot.pubsub.util.i.d(bufferedReader);
                        }
                    }
                    com.ot.pubsub.util.i.d(bufferedReader);
                } catch (Exception e4) {
                    e2 = e4;
                    bufferedReader = null;
                } catch (Throwable th) {
                    th = th;
                    com.ot.pubsub.util.i.d(r3);
                    com.ot.pubsub.util.i.d(fileReader);
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
            com.ot.pubsub.util.i.d(fileReader);
            if (arrayList.size() > 100) {
                arrayList = arrayList.subList(arrayList.size() - 100, arrayList.size());
            }
            if (arrayList.size() > 0) {
                f2434c = true;
            }
            return arrayList;
        } catch (Throwable th3) {
            th = th3;
            r3 = file;
        }
    }

    public static synchronized void g(boolean z2) {
        try {
            try {
                File file = new File(k());
                if (file.exists() && file.isDirectory()) {
                    String str = String.format(f2432a, f2433b.format(new Date()));
                    File[] fileArrListFiles = file.listFiles();
                    for (int i2 = 0; i2 < fileArrListFiles.length; i2++) {
                        if (fileArrListFiles[i2].isFile() && (z2 || !fileArrListFiles[i2].getName().equalsIgnoreCase(str))) {
                            fileArrListFiles[i2].delete();
                        }
                    }
                    if (file.listFiles().length == 0) {
                        f2434c = false;
                    }
                    return;
                }
                f2434c = false;
            } catch (Exception e2) {
                com.ot.pubsub.util.k.h("NetworkAccessManager", "cta removeObsoleteEvent error: " + e2.toString());
                e2.printStackTrace();
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r6v0, types: [java.lang.Object, java.lang.String] */
    /* JADX WARN: Type inference failed for: r6v1 */
    /* JADX WARN: Type inference failed for: r6v10 */
    /* JADX WARN: Type inference failed for: r6v2 */
    /* JADX WARN: Type inference failed for: r6v3 */
    /* JADX WARN: Type inference failed for: r6v4, types: [java.io.Closeable] */
    /* JADX WARN: Type inference failed for: r6v5 */
    /* JADX WARN: Type inference failed for: r6v6, types: [java.io.Closeable] */
    /* JADX WARN: Type inference failed for: r6v8, types: [java.io.FileWriter, java.io.Writer] */
    /* JADX WARN: Type inference failed for: r6v9 */
    public static synchronized void h(String str, String str2, String str3, String str4, Map<String, String> map, boolean z2) {
        File file = new File(k(), String.format(f2432a, f2433b.format(new Date())));
        BufferedWriter bufferedWriter = null;
        try {
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
                jSONObject.put("projectid", str);
                jSONObject.put("topic", (Object) str2);
                jSONObject.put("eventName", str3);
                jSONObject.put("data", str4);
                jSONObject.put(CommonPacketExtension.ATTRIBUTE_NAME, com.ot.pubsub.util.c.b(map));
                jSONObject.put("needGzipAndEncrypt", z2);
                byte[] bArrH = c.h(jSONObject.toString());
                str2 = new FileWriter(file, true);
                try {
                    BufferedWriter bufferedWriter2 = new BufferedWriter(str2);
                    try {
                        bufferedWriter2.write(u.c.a(bArrH));
                        bufferedWriter2.newLine();
                        f2434c = true;
                        com.ot.pubsub.util.i.d(bufferedWriter2);
                        str2 = str2;
                    } catch (Exception e2) {
                        e = e2;
                        bufferedWriter = bufferedWriter2;
                        com.ot.pubsub.util.k.h("NetworkAccessManager", "cta doSaveData error: " + e.toString());
                        e.printStackTrace();
                        com.ot.pubsub.util.i.d(bufferedWriter);
                        str2 = str2;
                    } catch (Throwable th) {
                        th = th;
                        bufferedWriter = bufferedWriter2;
                        com.ot.pubsub.util.i.d(bufferedWriter);
                        com.ot.pubsub.util.i.d(str2);
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
            str2 = 0;
        } catch (Throwable th3) {
            th = th3;
            str2 = 0;
        }
        com.ot.pubsub.util.i.d(str2);
    }

    public static boolean i() {
        return !f2435d && f2434c;
    }

    private static String k() {
        return com.ot.pubsub.util.b.a().getFilesDir().getAbsolutePath() + File.separator + "ot_pubsub";
    }
}
