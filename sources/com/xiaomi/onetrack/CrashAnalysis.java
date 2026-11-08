package com.xiaomi.onetrack;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.xiaomi.channelcollection.RegisterAppChannelInfoCollectionJob;
import com.xiaomi.mipush.sdk.AssemblePushUtils;
import com.xiaomi.onetrack.api.j;
import com.xiaomi.onetrack.d.d;
import com.xiaomi.onetrack.f.a;
import com.xiaomi.onetrack.util.aa;
import com.xiaomi.onetrack.util.ac;
import com.xiaomi.onetrack.util.b;
import com.xiaomi.onetrack.util.i;
import com.xiaomi.onetrack.util.k;
import com.xiaomi.onetrack.util.p;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/* JADX INFO: loaded from: classes.dex */
public class CrashAnalysis {
    public static final String ANR_CRASH = "anr";
    public static final String JAVA_CRASH = "java";
    public static final String NATIVE_CRASH = "native";

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final String f805a = "CrashAnalysis";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static final String f806b = "com.xiaomi.digest.DigestUtil";

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static final String f807c = "calcuateJavaDigest";

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static final String f808d = "/sdcard/tombstone";

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static final String f809e = "backtrace feature id:\n\t";

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static final String f810f = "error reason:\n\t";

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private static final String f811g = "Crash time: '";

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private static final String f812h = ".xcrash";

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private static final int f813i = 604800000;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private static final int f814j = 102400;

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private static final int f815k = 10;

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    private static final int f816l = 20;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    private static final String f817m = "@[0-9a-fA-F]{1,10}";

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    private static final String f818n = "\\$[0-9a-fA-F]{1,10}@[0-9a-fA-F]{1,10}";

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    private static final String f819o = "0x[0-9a-fA-F]{1,10}";

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    private static final String f820p = "\\d+[B,KB,MB]*";

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    private static final String f821q = "((java:)|(length=)|(index=)|(Index:)|(Size:))\\d+";

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    private static final int f822r = 20;

    /* JADX INFO: renamed from: s, reason: collision with root package name */
    private static final boolean f823s = false;

    /* JADX INFO: renamed from: t, reason: collision with root package name */
    private static final AtomicBoolean f824t = new AtomicBoolean(false);

    /* JADX INFO: renamed from: u, reason: collision with root package name */
    private final FileProcessor[] f825u;

    /* JADX INFO: renamed from: v, reason: collision with root package name */
    private final j f826v;

    public static String calculateJavaDigest(String str) {
        String[] strArrSplit = str.replaceAll("\\t", "").split("\\n");
        StringBuilder sb = new StringBuilder();
        int iMin = Math.min(strArrSplit.length, 20);
        for (int i2 = 0; i2 < iMin; i2++) {
            strArrSplit[i2] = strArrSplit[i2].replaceAll(f821q, "$1XX").replaceAll("\\$[0-9a-fA-F]{1,10}@[0-9a-fA-F]{1,10}|@[0-9a-fA-F]{1,10}|0x[0-9a-fA-F]{1,10}", "XX").replaceAll(f820p, "");
        }
        for (int i3 = 0; i3 < iMin && (!strArrSplit[i3].contains("...") || !strArrSplit[i3].contains("more")); i3++) {
            sb.append(strArrSplit[i3]);
            sb.append('\n');
        }
        return d.h(sb.toString());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String d(String str, String str2) {
        int i2;
        int iIndexOf;
        int iIndexOf2;
        String strSubstring = "";
        if (!TextUtils.isEmpty(str)) {
            try {
                if (str2.equals("anr")) {
                    int iIndexOf3 = str.indexOf(" tid=1 ");
                    if (iIndexOf3 != -1 && (iIndexOf2 = str.indexOf("\n\n", iIndexOf3)) != -1) {
                        strSubstring = calculateJavaDigest(str.substring(iIndexOf3, iIndexOf2));
                    }
                } else {
                    int iIndexOf4 = str.indexOf(f809e);
                    if (iIndexOf4 != -1 && (iIndexOf = str.indexOf("\n\n", (i2 = iIndexOf4 + 23))) != -1) {
                        strSubstring = str.substring(i2, iIndexOf);
                    }
                }
            } catch (Exception e2) {
                p.b(f805a, "calculateFeatureId error: " + e2.toString());
            }
        }
        return strSubstring;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void e() throws Throwable {
        for (FileProcessor fileProcessor : this.f825u) {
            fileProcessor.a();
        }
    }

    public static boolean isSupport() {
        try {
            Class.forName("xcrash.XCrash");
            return true;
        } catch (Throwable unused) {
            return false;
        }
    }

    public static void start(final Context context, final j jVar) {
        if (f824t.compareAndSet(false, true)) {
            i.a(new Runnable() { // from class: com.xiaomi.onetrack.CrashAnalysis.1
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        CrashAnalysis crashAnalysis = new CrashAnalysis(context, jVar);
                        if (crashAnalysis.d()) {
                            crashAnalysis.e();
                        } else {
                            p.a(CrashAnalysis.f805a, "no crash file found");
                        }
                    } catch (Throwable th) {
                        p.b(CrashAnalysis.f805a, "processCrash error: " + th.toString());
                    }
                }
            });
        } else {
            p.b(f805a, "run method has been invoked more than once");
        }
    }

    private class FileProcessor {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final List<File> f830a = new ArrayList();

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        final String f831b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        final String f832c;

        FileProcessor(String str) {
            this.f832c = str;
            this.f831b = str + CrashAnalysis.f812h;
        }

        boolean a(File file) {
            if (!file.getName().contains(this.f831b)) {
                return false;
            }
            this.f830a.add(file);
            return true;
        }

        private String a(String str) {
            if (TextUtils.isEmpty(str)) {
                return null;
            }
            String[] strArrSplit = str.split("__");
            if (strArrSplit.length != 2) {
                return null;
            }
            String[] strArrSplit2 = strArrSplit[0].split(RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT);
            if (strArrSplit2.length == 3) {
                return strArrSplit2[2];
            }
            return null;
        }

        void a() throws Throwable {
            for (int i2 = 0; i2 < this.f830a.size(); i2++) {
                String absolutePath = this.f830a.get(i2).getAbsoluteFile().getAbsolutePath();
                String strA = a(absolutePath);
                String strA2 = k.a(absolutePath, CrashAnalysis.f814j);
                if (!TextUtils.isEmpty(strA2) && CrashAnalysis.this.f826v != null) {
                    String strD = CrashAnalysis.d(strA2, this.f832c);
                    String strC = CrashAnalysis.c(strA2, this.f832c);
                    long jB = CrashAnalysis.b(strA2);
                    p.a(CrashAnalysis.f805a, "fileName: " + absolutePath);
                    p.a(CrashAnalysis.f805a, "feature id: " + strD);
                    p.a(CrashAnalysis.f805a, "error: " + strC);
                    p.a(CrashAnalysis.f805a, "crashTimeStamp: " + jB);
                    CrashAnalysis.this.f826v.a(strA2, strC, this.f832c, strA, strD, jB);
                    k.a(new File(absolutePath));
                    p.a(CrashAnalysis.f805a, "remove reported crash file");
                }
            }
        }
    }

    private CrashAnalysis(Context context, j jVar) {
        try {
            Class[] clsArr = new Class[0];
            Object objNewInstance = Class.forName("xcrash.XCrash$InitParameters").getConstructor(null).newInstance(null);
            Boolean bool = Boolean.FALSE;
            a(objNewInstance, "setNativeDumpAllThreads", bool);
            a(objNewInstance, "setLogDir", a());
            a(objNewInstance, "setNativeDumpMap", bool);
            a(objNewInstance, "setNativeDumpFds", bool);
            a(objNewInstance, "setJavaDumpAllThreads", bool);
            a(objNewInstance, "setAnrRethrow", bool);
            Class.forName("xcrash.XCrash").getDeclaredMethod(AssemblePushUtils.REASON_SYNC_TOKEN_INIT, Context.class, objNewInstance.getClass()).invoke(null, context.getApplicationContext(), objNewInstance);
            p.a(f805a, "XCrash init success");
        } catch (Throwable th) {
            p.a(f805a, "XCrash init failed: " + th.toString());
        }
        this.f826v = jVar;
        this.f825u = new FileProcessor[]{new FileProcessor("java"), new FileProcessor("anr"), new FileProcessor(NATIVE_CRASH)};
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String c(String str, String str2) {
        int i2;
        int iIndexOf;
        int iIndexOf2;
        int iIndexOf3;
        String strSubstring = "uncategoried";
        if (!TextUtils.isEmpty(str)) {
            try {
                if (str2.equals("anr")) {
                    int iIndexOf4 = str.indexOf(" tid=1 ");
                    if (iIndexOf4 != -1 && (iIndexOf2 = str.indexOf("\n  at ", iIndexOf4)) != -1 && (iIndexOf3 = str.indexOf(10, iIndexOf2 + 6)) != -1) {
                        strSubstring = str.substring(iIndexOf2 + 2, iIndexOf3);
                    }
                } else {
                    int iIndexOf5 = str.indexOf(f810f);
                    if (iIndexOf5 != -1 && (iIndexOf = str.indexOf("\n\n", (i2 = iIndexOf5 + 15))) != -1) {
                        strSubstring = str.substring(i2, iIndexOf);
                    }
                }
            } catch (Exception e2) {
                p.b(f805a, "getErrorReasonString error: " + e2.toString());
            }
        }
        return strSubstring;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static long b(String str) {
        int i2;
        int iIndexOf;
        if (TextUtils.isEmpty(str)) {
            return 0L;
        }
        try {
            int iIndexOf2 = str.indexOf(f811g);
            if (iIndexOf2 == -1 || (iIndexOf = str.indexOf("'\n", (i2 = iIndexOf2 + 13))) == -1) {
                return 0L;
            }
            return b.a(str.substring(i2, iIndexOf));
        } catch (Exception e2) {
            p.b(f805a, "getCrashTimeStamp error: " + e2.toString());
            return 0L;
        }
    }

    static void a(Context context) {
        try {
            a.a(context.getApplicationContext());
            Class.forName("xcrash.XCrash").getDeclaredMethod("initHooker", Context.class, String.class).invoke(null, context.getApplicationContext(), a());
            Log.d(f805a, "registerHook succeeded");
        } catch (Throwable th) {
            Log.d(f805a, "registerHook failed: " + th.toString());
        }
    }

    private long b() {
        long jC = aa.c();
        if (jC == 0) {
            p.a(f805a, "no ticket data found, return max count");
            return 10L;
        }
        long jB = ac.b();
        if (jC / 100 != jB) {
            p.a(f805a, "no today's ticket, return max count");
            return 10L;
        }
        long j2 = jC - (jB * 100);
        p.a(f805a, "today's remain ticket is " + j2);
        return j2;
    }

    private void a(Object obj, String str, Object obj2) throws Exception {
        obj.getClass().getDeclaredMethod(str, obj2.getClass() == Boolean.class ? Boolean.TYPE : obj2.getClass()).invoke(obj, obj2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean d() {
        boolean z2;
        Iterator<File> it;
        List<File> listC = c();
        long jB = b();
        if (listC == null || listC.size() <= 0) {
            z2 = false;
        } else {
            long jCurrentTimeMillis = System.currentTimeMillis();
            long jB2 = aa.b();
            long j2 = 604800000;
            if (jB2 > jCurrentTimeMillis) {
                jB2 = jCurrentTimeMillis - 604800000;
            }
            Iterator<File> it2 = listC.iterator();
            long j3 = 0;
            long j4 = 0;
            boolean z3 = false;
            while (it2.hasNext()) {
                File next = it2.next();
                long jLastModified = next.lastModified();
                if (jLastModified < jCurrentTimeMillis - j2 || jLastModified > jCurrentTimeMillis) {
                    it = it2;
                    p.a(f805a, "remove obsolete crash files: " + next.getName());
                    k.a(next);
                } else {
                    if (jLastModified <= jB2) {
                        p.a(f805a, "found already reported crash file, ignore");
                    } else if (jB > j3) {
                        FileProcessor[] fileProcessorArr = this.f825u;
                        int length = fileProcessorArr.length;
                        int i2 = 0;
                        while (i2 < length) {
                            Iterator<File> it3 = it2;
                            if (fileProcessorArr[i2].a(next)) {
                                p.a(f805a, "find crash file:" + next.getName());
                                jB--;
                                z3 = true;
                                if (j4 < jLastModified) {
                                    j4 = jLastModified;
                                }
                            }
                            i2++;
                            it2 = it3;
                        }
                    }
                    it = it2;
                }
                it2 = it;
                j2 = 604800000;
                j3 = 0;
            }
            if (j4 > j3) {
                aa.c(j4);
            }
            z2 = z3;
        }
        if (z2) {
            a(jB);
        }
        return z2;
    }

    private List<File> c() {
        File[] fileArrListFiles = new File(a()).listFiles();
        if (fileArrListFiles == null) {
            p.a(f805a, "this path does not denote a directory, or if an I/O error occurs.");
            return null;
        }
        List<File> listAsList = Arrays.asList(fileArrListFiles);
        Collections.sort(listAsList, new Comparator<File>() { // from class: com.xiaomi.onetrack.CrashAnalysis.2
            @Override // java.util.Comparator
            public int compare(File file, File file2) {
                return (int) (file.lastModified() - file2.lastModified());
            }
        });
        int size = listAsList.size();
        if (size <= 20) {
            return listAsList;
        }
        int i2 = size - 20;
        for (int i3 = 0; i3 < i2; i3++) {
            k.a(listAsList.get(i3));
        }
        return listAsList.subList(i2, size);
    }

    private static String a() {
        return k.a();
    }

    private void a(long j2) {
        aa.d((ac.b() * 100) + j2);
    }
}
