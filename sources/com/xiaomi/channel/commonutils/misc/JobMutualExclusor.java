package com.xiaomi.channel.commonutils.misc;

import android.content.Context;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.file.FileUtils;
import com.xiaomi.channel.commonutils.file.IOUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class JobMutualExclusor {
    public static final float CHECK_FRACTION = 0.9f;
    private static final String INFO_SEPARATOR = ",";
    private static final String LAST_COLLECT_FILE_PATH = "lcfp";
    private static final String LAST_COLLECT_FILE_PATH_LOCK = "lcfp.lock";
    private static final String VIRTUAL_DEVICE_DIR = "/.vdevdir/";

    public static boolean checkPeriodAndRecordWithFileLock(Context context, String str, long j2) throws Throwable {
        RandomAccessFile randomAccessFile;
        FileLock fileLockLock = null;
        try {
            try {
                File file = new File(context.getFilesDir(), VIRTUAL_DEVICE_DIR);
                if (!FileUtils.canWriteInternalFileDir(file)) {
                    IOUtils.closeQuietly(null);
                    return true;
                }
                File file2 = new File(file, LAST_COLLECT_FILE_PATH_LOCK);
                IOUtils.createFileQuietly(file2);
                randomAccessFile = new RandomAccessFile(file2, "rw");
                try {
                    fileLockLock = randomAccessFile.getChannel().lock();
                    boolean zCheckPeriodAndRecordWorking = checkPeriodAndRecordWorking(context, str, j2);
                    if (fileLockLock != null && fileLockLock.isValid()) {
                        try {
                            fileLockLock.release();
                        } catch (IOException unused) {
                        }
                    }
                    IOUtils.closeQuietly(randomAccessFile);
                    return zCheckPeriodAndRecordWorking;
                } catch (IOException e2) {
                    e = e2;
                }
            } catch (Throwable th) {
                th = th;
                if (0 != 0 && fileLockLock.isValid()) {
                    try {
                        fileLockLock.release();
                    } catch (IOException unused2) {
                    }
                }
                IOUtils.closeQuietly(null);
                throw th;
            }
        } catch (IOException e3) {
            e = e3;
            randomAccessFile = null;
        } catch (Throwable th2) {
            th = th2;
            if (0 != 0) {
                fileLockLock.release();
            }
            IOUtils.closeQuietly(null);
            throw th;
        }
        e.printStackTrace();
        if (fileLockLock != null && fileLockLock.isValid()) {
            try {
                fileLockLock.release();
            } catch (IOException unused3) {
            }
        }
        IOUtils.closeQuietly(randomAccessFile);
        return true;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r10v0 */
    /* JADX WARN: Type inference failed for: r10v1, types: [java.io.Closeable] */
    /* JADX WARN: Type inference failed for: r10v2 */
    /* JADX WARN: Type inference failed for: r9v0, types: [boolean] */
    private static boolean checkPeriodAndRecordWorking(Context context, String str, long j2) throws Throwable {
        ?? r10;
        BufferedReader bufferedReader;
        BufferedWriter bufferedWriter;
        File file = new File(context.getFilesDir(), VIRTUAL_DEVICE_DIR);
        if (!FileUtils.canWriteInternalFileDir(file)) {
            return true;
        }
        File file2 = new File(file, LAST_COLLECT_FILE_PATH);
        ArrayList arrayList = new ArrayList();
        long jCurrentTimeMillis = System.currentTimeMillis();
        String str2 = str + ":" + context.getPackageName() + "," + jCurrentTimeMillis;
        ?? Exists = file2.exists();
        try {
            if (Exists != 0) {
                try {
                    bufferedReader = new BufferedReader(new FileReader(file2));
                    while (true) {
                        try {
                            String line = bufferedReader.readLine();
                            if (line == null) {
                                break;
                            }
                            String[] strArrSplit = line.split(":");
                            if (strArrSplit.length == 2) {
                                if (TextUtils.equals(strArrSplit[0], String.valueOf(str))) {
                                    String[] strArrSplit2 = strArrSplit[1].split(",");
                                    if (strArrSplit2.length == 2) {
                                        long j3 = Long.parseLong(strArrSplit2[1]);
                                        if (!TextUtils.equals(strArrSplit2[0], context.getPackageName()) && Math.abs(jCurrentTimeMillis - j3) < 1000 * j2 * 0.9f) {
                                            IOUtils.closeQuietly(bufferedReader);
                                            return false;
                                        }
                                    }
                                } else {
                                    arrayList.add(line);
                                }
                            }
                        } catch (Exception unused) {
                            arrayList.clear();
                        }
                    }
                } catch (Exception unused2) {
                    bufferedReader = null;
                } catch (Throwable th) {
                    th = th;
                    r10 = 0;
                    IOUtils.closeQuietly(r10);
                    throw th;
                }
                IOUtils.closeQuietly(bufferedReader);
            } else if (!IOUtils.createFileQuietly(file2)) {
                return true;
            }
            arrayList.add(str2);
            try {
                BufferedWriter bufferedWriter2 = new BufferedWriter(new FileWriter(file2));
                try {
                    Iterator it = arrayList.iterator();
                    while (it.hasNext()) {
                        bufferedWriter2.write((String) it.next());
                        bufferedWriter2.newLine();
                        bufferedWriter2.flush();
                    }
                    IOUtils.closeQuietly(bufferedWriter2);
                } catch (IOException e2) {
                    e = e2;
                    bufferedWriter = bufferedWriter2;
                    try {
                        MyLog.e(e.toString());
                        IOUtils.closeQuietly(bufferedWriter);
                    } catch (Throwable th2) {
                        th = th2;
                        IOUtils.closeQuietly(bufferedWriter);
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    bufferedWriter = bufferedWriter2;
                    IOUtils.closeQuietly(bufferedWriter);
                    throw th;
                }
            } catch (IOException e3) {
                e = e3;
                bufferedWriter = null;
            } catch (Throwable th4) {
                th = th4;
                bufferedWriter = null;
            }
            return true;
        } catch (Throwable th5) {
            th = th5;
            r10 = Exists;
        }
    }
}
