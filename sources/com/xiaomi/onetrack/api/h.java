package com.xiaomi.onetrack.api;

import android.os.Process;
import android.util.Log;
import com.miui.powerkeeper.utils.ShellUtils;
import com.xiaomi.onetrack.CrashAnalysis;
import java.io.File;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes.dex */
public class h implements Thread.UncaughtExceptionHandler {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final String f1091a = "OneTrackExceptionHandler";

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static final String f1092c = "tombstone";

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static final String f1093d = ".java.xcrash";

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static final String f1094e = "backtrace feature id:\n\t";

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static final String f1095f = "error reason:\n\t";

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private static final long f1096h = 2;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private Thread.UncaughtExceptionHandler f1097b;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private final Date f1098g = new Date();

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private int f1099i = 50;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private int f1100j = 50;

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private int f1101k = 200;

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    private boolean f1102l = true;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    private boolean f1103m = true;

    @Override // java.lang.Thread.UncaughtExceptionHandler
    public void uncaughtException(Thread thread, Throwable th) {
        Log.d(com.xiaomi.onetrack.util.p.a(f1091a), "crash happened->stacktrace: " + th.getStackTrace());
        FutureTask futureTask = new FutureTask(new i(this, thread, th), null);
        com.xiaomi.onetrack.util.i.a(futureTask);
        try {
            futureTask.get(2L, TimeUnit.SECONDS);
        } catch (Exception e2) {
            Log.e(com.xiaomi.onetrack.util.p.a(f1091a), "handleException error :" + e2.getMessage());
        }
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = this.f1097b;
        if (uncaughtExceptionHandler != null) {
            uncaughtExceptionHandler.uncaughtException(thread, th);
        }
    }

    public void a() {
        Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (defaultUncaughtExceptionHandler instanceof h) {
            return;
        }
        this.f1097b = defaultUncaughtExceptionHandler;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:30:0x00c6 A[Catch: all -> 0x00ce, Exception -> 0x00d3, TryCatch #11 {Exception -> 0x00d3, all -> 0x00ce, blocks: (B:28:0x0086, B:30:0x00c6, B:35:0x00d6, B:37:0x00da, B:39:0x00de, B:42:0x00f1, B:44:0x00f5, B:45:0x0100, B:47:0x0104, B:48:0x010f, B:41:0x00e2), top: B:85:0x0086 }] */
    /* JADX WARN: Code duplicated, block: B:41:0x00e2 A[Catch: all -> 0x00ce, Exception -> 0x00d3, TryCatch #11 {Exception -> 0x00d3, all -> 0x00ce, blocks: (B:28:0x0086, B:30:0x00c6, B:35:0x00d6, B:37:0x00da, B:39:0x00de, B:42:0x00f1, B:44:0x00f5, B:45:0x0100, B:47:0x0104, B:48:0x010f, B:41:0x00e2), top: B:85:0x0086 }] */
    /* JADX WARN: Code duplicated, block: B:44:0x00f5 A[Catch: all -> 0x00ce, Exception -> 0x00d3, TryCatch #11 {Exception -> 0x00d3, all -> 0x00ce, blocks: (B:28:0x0086, B:30:0x00c6, B:35:0x00d6, B:37:0x00da, B:39:0x00de, B:42:0x00f1, B:44:0x00f5, B:45:0x0100, B:47:0x0104, B:48:0x010f, B:41:0x00e2), top: B:85:0x0086 }] */
    /* JADX WARN: Code duplicated, block: B:47:0x0104 A[Catch: all -> 0x00ce, Exception -> 0x00d3, TryCatch #11 {Exception -> 0x00d3, all -> 0x00ce, blocks: (B:28:0x0086, B:30:0x00c6, B:35:0x00d6, B:37:0x00da, B:39:0x00de, B:42:0x00f1, B:44:0x00f5, B:45:0x0100, B:47:0x0104, B:48:0x010f, B:41:0x00e2), top: B:85:0x0086 }] */
    /* JADX WARN: Code duplicated, block: B:67:0x007f A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:89:? A[RETURN, SYNTHETIC] */
    public void a(Thread thread, Throwable th) throws Throwable {
        File file;
        String string;
        String strA;
        Throwable th2;
        RandomAccessFile randomAccessFile;
        int i2;
        Date date = new Date();
        RandomAccessFile randomAccessFile2 = null;
        try {
            file = new File(String.format(Locale.US, "%s/%s_%020d_%s__%s%s", com.xiaomi.onetrack.util.k.a(), f1092c, Long.valueOf(this.f1098g.getTime() * 1000), com.xiaomi.onetrack.util.b.a(com.xiaomi.onetrack.f.a.b()), com.xiaomi.onetrack.f.a.e(), f1093d));
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
            } catch (Exception e2) {
                e = e2;
                com.xiaomi.onetrack.util.p.b(f1091a, "JavaCrashHandler createLogFile failed", e);
            }
        } catch (Exception e3) {
            e = e3;
            file = null;
        }
        try {
            StringWriter stringWriter = new StringWriter();
            try {
                th.printStackTrace(new PrintWriter(stringWriter));
                string = stringWriter.toString();
                try {
                    strA = a(date, thread, string);
                } catch (Exception e4) {
                    e = e4;
                    com.xiaomi.onetrack.util.p.b(f1091a, "JavaCrashHandler getEmergency failed", e);
                    strA = null;
                }
            } catch (Exception e5) {
                e = e5;
                string = null;
                com.xiaomi.onetrack.util.p.b(f1091a, "JavaCrashHandler getEmergency failed", e);
                strA = null;
                if (file == null) {
                }
                try {
                    try {
                        randomAccessFile = new RandomAccessFile(file, "rws");
                        try {
                            randomAccessFile.write((f1094e + CrashAnalysis.calculateJavaDigest(string) + "\n\n").getBytes("UTF-8"));
                            randomAccessFile.write((f1095f + th.toString() + "\n\n").getBytes("UTF-8"));
                            if (strA != null) {
                                randomAccessFile.write(strA.getBytes("UTF-8"));
                            }
                            i2 = this.f1101k;
                            if (i2 <= 0) {
                                randomAccessFile.write(com.xiaomi.onetrack.util.b.a(i2, this.f1099i, this.f1100j).getBytes("UTF-8"));
                            } else {
                                randomAccessFile.write(com.xiaomi.onetrack.util.b.a(i2, this.f1099i, this.f1100j).getBytes("UTF-8"));
                            }
                            if (this.f1102l) {
                                randomAccessFile.write(com.xiaomi.onetrack.util.b.f().getBytes("UTF-8"));
                            }
                            if (this.f1103m) {
                                randomAccessFile.write(com.xiaomi.onetrack.util.b.e().getBytes("UTF-8"));
                            }
                            randomAccessFile.write(com.xiaomi.onetrack.util.b.d().getBytes("UTF-8"));
                            randomAccessFile.write("foreground:\nyes\n\n".getBytes("UTF-8"));
                            randomAccessFile.close();
                        } catch (Exception e6) {
                            e = e6;
                            randomAccessFile2 = randomAccessFile;
                            com.xiaomi.onetrack.util.p.b(f1091a, "JavaCrashHandler write log file failed", e);
                            if (randomAccessFile2 == null) {
                                return;
                            } else {
                                randomAccessFile2.close();
                            }
                        } catch (Throwable th3) {
                            th2 = th3;
                            randomAccessFile2 = randomAccessFile;
                            if (randomAccessFile2 != null) {
                                try {
                                    randomAccessFile2.close();
                                    throw th2;
                                } catch (Exception e7) {
                                    com.xiaomi.onetrack.util.p.b(f1091a, "JavaCrashHandler close RandomAccessFile failed", e7);
                                    throw th2;
                                }
                            }
                            throw th2;
                        }
                    } catch (Exception e8) {
                        e = e8;
                    }
                } catch (Throwable th4) {
                    th2 = th4;
                }
            }
        } catch (Exception e9) {
            e = e9;
        }
        try {
            if (file == null) {
                randomAccessFile = new RandomAccessFile(file, "rws");
                randomAccessFile.write((f1094e + CrashAnalysis.calculateJavaDigest(string) + "\n\n").getBytes("UTF-8"));
                randomAccessFile.write((f1095f + th.toString() + "\n\n").getBytes("UTF-8"));
                if (strA != null) {
                    randomAccessFile.write(strA.getBytes("UTF-8"));
                }
                i2 = this.f1101k;
                if (i2 <= 0 || this.f1099i > 0 || this.f1100j > 0) {
                    randomAccessFile.write(com.xiaomi.onetrack.util.b.a(i2, this.f1099i, this.f1100j).getBytes("UTF-8"));
                }
                if (this.f1102l) {
                    randomAccessFile.write(com.xiaomi.onetrack.util.b.f().getBytes("UTF-8"));
                }
                if (this.f1103m) {
                    randomAccessFile.write(com.xiaomi.onetrack.util.b.e().getBytes("UTF-8"));
                }
                randomAccessFile.write(com.xiaomi.onetrack.util.b.d().getBytes("UTF-8"));
                randomAccessFile.write("foreground:\nyes\n\n".getBytes("UTF-8"));
                randomAccessFile.close();
            }
        } catch (Exception e10) {
            com.xiaomi.onetrack.util.p.b(f1091a, "JavaCrashHandler close RandomAccessFile failed", e10);
        }
    }

    private String a(Date date, Thread thread, String str) {
        return com.xiaomi.onetrack.util.b.a(this.f1098g, date, "java", com.xiaomi.onetrack.f.a.e(), com.xiaomi.onetrack.util.b.a(com.xiaomi.onetrack.f.a.b())) + "pid: " + Process.myPid() + ", tid: " + Process.myTid() + ", name: " + thread.getName() + "  >>> " + com.xiaomi.onetrack.f.a.e() + " <<<\n\njava stacktrace:\n" + str + ShellUtils.COMMAND_LINE_END;
    }
}
