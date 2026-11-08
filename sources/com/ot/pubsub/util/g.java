package com.ot.pubsub.util;

import android.text.TextUtils;
import android.util.LruCache;
import com.miui.powerkeeper.logsystem.config.LogType;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/* JADX INFO: loaded from: classes.dex */
public class g {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static LruCache<String, a> f716a = new h(LogType.LOG_TYPE_SAMPLING_SIGNAL_STRENGTH);

    /* JADX INFO: Access modifiers changed from: private */
    static class a {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        String f717a;

        private a() {
        }

        /* synthetic */ a(h hVar) {
            this();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v11 */
    /* JADX WARN: Type inference failed for: r0v12 */
    /* JADX WARN: Type inference failed for: r0v4, types: [com.ot.pubsub.util.h] */
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
        a aVar = f716a.get(str);
        if (aVar != null) {
            return aVar.f717a;
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
                            k.k("FileUtil", "get error:" + e.toString());
                            i.d(r1);
                            return "";
                        } catch (Throwable th) {
                            th = th;
                            r0 = bufferedReader;
                            i.d(r0);
                            throw th;
                        }
                    }
                } else {
                    bufferedReader = null;
                }
                String string = sb.toString();
                a aVar2 = new a(r1);
                aVar2.f717a = string;
                f716a.put(str, aVar2);
                i.d(bufferedReader);
                return string;
            } catch (Throwable th2) {
                th = th2;
                r0 = r1;
            }
        } catch (Exception e3) {
            e = e3;
        }
    }

    private static String b() {
        return c(com.xiaomi.onetrack.c.c.a.f1252a);
    }

    private static String c(String str) {
        String str2 = b.a().getFilesDir().getAbsolutePath() + File.separator + str;
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
}
