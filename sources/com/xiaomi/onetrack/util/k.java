package com.xiaomi.onetrack.util;

import android.text.TextUtils;
import android.util.LruCache;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.utils.ShellUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/* JADX INFO: loaded from: classes.dex */
public class k {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final String f1565a = "FileUtil";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static final String f1566b = "onetrack";

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static final String f1567c = "tombstone";

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static LruCache<String, a> f1568d = new l(LogType.LOG_TYPE_SAMPLING_SIGNAL_STRENGTH);

    /* JADX INFO: Access modifiers changed from: private */
    static class a {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        String f1569a;

        private a() {
        }

        /* synthetic */ a(l lVar) {
            this();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v2, types: [com.xiaomi.onetrack.util.l] */
    /* JADX WARN: Type inference failed for: r0v3 */
    /* JADX WARN: Type inference failed for: r0v4, types: [java.io.Closeable] */
    /* JADX WARN: Type inference failed for: r0v5, types: [java.io.Closeable] */
    /* JADX WARN: Type inference failed for: r0v6 */
    /* JADX WARN: Type inference failed for: r0v7 */
    /* JADX WARN: Type inference failed for: r0v8 */
    /* JADX WARN: Type inference failed for: r0v9 */
    public static void a(String str, String str2) throws Throwable {
        ?? r0;
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return;
        }
        ?? r1 = 0;
        r1 = 0;
        try {
            try {
                a aVar = new a(r1);
                aVar.f1569a = str2;
                f1568d.put(str, aVar);
                String strB = b();
                File file = new File(strB);
                if (!file.exists()) {
                    file.mkdirs();
                }
                File file2 = new File(strB, str);
                if (!file2.exists()) {
                    file2.createNewFile();
                }
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file2), 1024);
                try {
                    bufferedWriter.write(str2);
                    bufferedWriter.flush();
                    m.a(bufferedWriter);
                } catch (Exception e2) {
                    r1 = bufferedWriter;
                    e = e2;
                    p.c(f1565a, "put error:" + e.toString());
                    m.a((Closeable) r1);
                } catch (Throwable th) {
                    r0 = bufferedWriter;
                    th = th;
                    m.a((Closeable) r0);
                    throw th;
                }
            } catch (Exception e3) {
                e = e3;
            }
        } catch (Throwable th2) {
            th = th2;
            r0 = r1;
        }
    }

    private static String b() {
        return c("onetrack");
    }

    private static String c(String str) {
        String str2 = com.xiaomi.onetrack.f.a.a().getFilesDir().getAbsolutePath() + File.separator + str;
        File file = new File(str2);
        if (!file.exists()) {
            try {
                file.mkdirs();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return str2;
    }

    public static void b(String str) {
        try {
            if (TextUtils.isEmpty(str)) {
                return;
            }
            f1568d.remove(str);
            File file = new File(b(), str);
            if (file.exists() && file.isFile()) {
                file.delete();
            }
        } catch (Exception e2) {
            p.c(f1565a, "clear error:" + e2.toString());
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v11 */
    /* JADX WARN: Type inference failed for: r0v12 */
    /* JADX WARN: Type inference failed for: r0v4, types: [com.xiaomi.onetrack.util.l] */
    /* JADX WARN: Type inference failed for: r0v5 */
    /* JADX WARN: Type inference failed for: r0v6, types: [java.io.Closeable] */
    /* JADX WARN: Type inference failed for: r0v7, types: [java.io.Closeable] */
    /* JADX WARN: Type inference failed for: r0v8 */
    /* JADX WARN: Type inference failed for: r0v9 */
    public static String a(String str) throws Throwable {
        ?? r0;
        BufferedReader bufferedReader;
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        a aVar = f1568d.get(str);
        if (aVar != null) {
            return aVar.f1569a;
        }
        ?? r1 = 0;
        r1 = 0;
        try {
            try {
                File file = new File(b(), str);
                StringBuilder sb = new StringBuilder();
                if (file.exists()) {
                    bufferedReader = new BufferedReader(new FileReader(file));
                    while (true) {
                        try {
                            String line = bufferedReader.readLine();
                            if (line == null) {
                                break;
                            }
                            sb.append(line);
                        } catch (Exception e2) {
                            e = e2;
                            r1 = bufferedReader;
                            p.c(f1565a, "get error:" + e.toString());
                            m.a((Closeable) r1);
                            return "";
                        } catch (Throwable th) {
                            th = th;
                            r0 = bufferedReader;
                            m.a((Closeable) r0);
                            throw th;
                        }
                    }
                } else {
                    bufferedReader = null;
                }
                String string = sb.toString();
                a aVar2 = new a(r1);
                aVar2.f1569a = string;
                f1568d.put(str, aVar2);
                m.a(bufferedReader);
                return string;
            } catch (Throwable th2) {
                th = th2;
                r0 = r1;
            }
        } catch (Exception e3) {
            e = e3;
        }
    }

    public static String a() {
        return c(f1567c);
    }

    /* JADX WARN: Not initialized variable reg: 2, insn: 0x0031: MOVE (r0 I:??[OBJECT, ARRAY]) = (r2 I:??[OBJECT, ARRAY]), block:B:12:0x0031 */
    public static String a(String str, int i2) throws Throwable {
        BufferedReader bufferedReader;
        Closeable closeable;
        Closeable closeable2 = null;
        try {
            try {
                File file = new File(str);
                StringBuilder sb = new StringBuilder();
                if (file.exists()) {
                    bufferedReader = new BufferedReader(new FileReader(file));
                    do {
                        try {
                            String line = bufferedReader.readLine();
                            if (line == null) {
                                break;
                            }
                            sb.append(line);
                            sb.append(ShellUtils.COMMAND_LINE_END);
                        } catch (Exception e2) {
                            e = e2;
                            p.c(f1565a, "get error:" + e.toString());
                            m.a(bufferedReader);
                            return null;
                        }
                    } while (sb.length() <= i2);
                } else {
                    bufferedReader = null;
                }
                if (sb.length() > i2) {
                    String strSubstring = sb.substring(0, i2 - 1);
                    m.a(bufferedReader);
                    return strSubstring;
                }
                String string = sb.toString();
                m.a(bufferedReader);
                return string;
            } catch (Throwable th) {
                th = th;
                closeable2 = closeable;
                m.a(closeable2);
                throw th;
            }
        } catch (Exception e3) {
            e = e3;
            bufferedReader = null;
        } catch (Throwable th2) {
            th = th2;
            m.a(closeable2);
            throw th;
        }
    }

    public static void a(File file) {
        try {
            if (file.exists() && file.isFile()) {
                file.delete();
            }
        } catch (Exception e2) {
            p.c(f1565a, "failed to remove file: " + file.getName() + "," + e2.toString());
        }
    }
}
