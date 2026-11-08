package com.xiaomi.clientreport.processor;

import android.content.Context;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.file.IOUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.clientreport.data.BaseClientReport;
import com.xiaomi.clientreport.data.ClientReportConstants;
import com.xiaomi.clientreport.data.PerfClientReport;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class PerfKVFileHelper {
    private static PerfClientReport buildPerfClientReport(PerfClientReport perfClientReport, String str) {
        long[] valueStr;
        if (perfClientReport == null || (valueStr = parseValueStr(str)) == null) {
            return null;
        }
        perfClientReport.perfCounts = valueStr[0];
        perfClientReport.perfLatencies = valueStr[1];
        return perfClientReport;
    }

    /* JADX WARN: Code duplicated, block: B:111:? A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:70:0x00e1  */
    /* JADX WARN: Code duplicated, block: B:78:0x00f7  */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r4v0 */
    /* JADX WARN: Type inference failed for: r4v1 */
    /* JADX WARN: Type inference failed for: r4v10, types: [java.io.BufferedReader, java.io.Closeable] */
    /* JADX WARN: Type inference failed for: r4v11 */
    /* JADX WARN: Type inference failed for: r4v2, types: [java.io.Closeable] */
    /* JADX WARN: Type inference failed for: r4v3, types: [java.io.Closeable] */
    /* JADX WARN: Type inference failed for: r4v4 */
    /* JADX WARN: Type inference failed for: r4v5 */
    /* JADX WARN: Type inference failed for: r4v6 */
    /* JADX WARN: Type inference failed for: r4v7 */
    /* JADX WARN: Type inference failed for: r4v8 */
    /* JADX WARN: Type inference failed for: r4v9 */
    public static List<String> extractToDatas(Context context, String str) throws Throwable {
        File file;
        RandomAccessFile randomAccessFile;
        ?? bufferedReader;
        PerfClientReport perfClientReportBuildPerfClientReport;
        ArrayList arrayList = new ArrayList();
        if (TextUtils.isEmpty(str) || !new File(str).exists()) {
            return arrayList;
        }
        FileLock fileLock = null;
        try {
            file = new File(str + ".lock");
            try {
                IOUtils.createFileQuietly(file);
                randomAccessFile = new RandomAccessFile(file, "rw");
                try {
                    FileLock fileLockLock = randomAccessFile.getChannel().lock();
                    try {
                        bufferedReader = new BufferedReader(new FileReader(str));
                        while (true) {
                            try {
                                String line = bufferedReader.readLine();
                                if (line == null) {
                                    break;
                                }
                                String[] strArrSplit = line.split(ClientReportConstants.SEPARATOR);
                                if (strArrSplit.length >= 2 && !TextUtils.isEmpty(strArrSplit[0]) && !TextUtils.isEmpty(strArrSplit[1]) && (perfClientReportBuildPerfClientReport = buildPerfClientReport(spiltKeyForModel(strArrSplit[0]), strArrSplit[1])) != null) {
                                    arrayList.add(perfClientReportBuildPerfClientReport.toJsonString());
                                }
                            } catch (Exception e2) {
                                e = e2;
                                fileLock = fileLockLock;
                                bufferedReader = bufferedReader;
                                try {
                                    MyLog.e(e);
                                    if (fileLock != null && fileLock.isValid()) {
                                        try {
                                            fileLock.release();
                                        } catch (IOException e3) {
                                            MyLog.e(e3);
                                        }
                                    }
                                    IOUtils.closeQuietly(randomAccessFile);
                                    IOUtils.closeQuietly(bufferedReader);
                                    if (file != null) {
                                    }
                                    return arrayList;
                                } catch (Throwable th) {
                                    th = th;
                                    if (fileLock != null && fileLock.isValid()) {
                                        try {
                                            fileLock.release();
                                        } catch (IOException e4) {
                                            MyLog.e(e4);
                                        }
                                    }
                                    IOUtils.closeQuietly(randomAccessFile);
                                    IOUtils.closeQuietly(bufferedReader);
                                    if (file == null) {
                                        throw th;
                                    }
                                    file.delete();
                                    throw th;
                                }
                            } catch (Throwable th2) {
                                th = th2;
                                fileLock = fileLockLock;
                                if (fileLock != null) {
                                    fileLock.release();
                                }
                                IOUtils.closeQuietly(randomAccessFile);
                                IOUtils.closeQuietly(bufferedReader);
                                if (file == null) {
                                    throw th;
                                }
                                file.delete();
                                throw th;
                            }
                        }
                        if (fileLockLock != null && fileLockLock.isValid()) {
                            try {
                                fileLockLock.release();
                            } catch (IOException e5) {
                                MyLog.e(e5);
                            }
                        }
                        IOUtils.closeQuietly(randomAccessFile);
                        IOUtils.closeQuietly(bufferedReader);
                    } catch (Exception e6) {
                        e = e6;
                        bufferedReader = 0;
                    } catch (Throwable th3) {
                        th = th3;
                        bufferedReader = 0;
                    }
                } catch (Exception e7) {
                    e = e7;
                    bufferedReader = 0;
                } catch (Throwable th4) {
                    th = th4;
                    bufferedReader = 0;
                }
            } catch (Exception e8) {
                e = e8;
                randomAccessFile = null;
                bufferedReader = randomAccessFile;
                MyLog.e(e);
                if (fileLock != null) {
                    fileLock.release();
                }
                IOUtils.closeQuietly(randomAccessFile);
                IOUtils.closeQuietly(bufferedReader);
                if (file != null) {
                    file.delete();
                }
                return arrayList;
            } catch (Throwable th5) {
                th = th5;
                randomAccessFile = null;
                bufferedReader = randomAccessFile;
                if (fileLock != null) {
                    fileLock.release();
                }
                IOUtils.closeQuietly(randomAccessFile);
                IOUtils.closeQuietly(bufferedReader);
                if (file == null) {
                    throw th;
                }
                file.delete();
                throw th;
            }
        } catch (Exception e9) {
            e = e9;
            file = null;
            randomAccessFile = null;
        } catch (Throwable th6) {
            th = th6;
            file = null;
            randomAccessFile = null;
        }
        file.delete();
        return arrayList;
    }

    public static String generateKey(PerfClientReport perfClientReport) {
        return perfClientReport.production + "#" + perfClientReport.clientInterfaceId + "#" + perfClientReport.reportType + "#" + perfClientReport.code;
    }

    protected static long[] parseValueStr(String str) {
        long[] jArr = new long[2];
        try {
            String[] strArrSplit = str.split("#");
            if (strArrSplit.length >= 2) {
                jArr[0] = Long.parseLong(strArrSplit[0].trim());
                jArr[1] = Long.parseLong(strArrSplit[1].trim());
            }
            return jArr;
        } catch (Exception e2) {
            MyLog.e(e2);
            return null;
        }
    }

    public static void put(String str, BaseClientReport[] baseClientReportArr) {
        RandomAccessFile randomAccessFile;
        boolean zIsValid;
        if (baseClientReportArr == null || baseClientReportArr.length <= 0 || TextUtils.isEmpty(str)) {
            return;
        }
        FileLock fileLockLock = null;
        try {
            File file = new File(str + ".lock");
            IOUtils.createFileQuietly(file);
            randomAccessFile = new RandomAccessFile(file, "rw");
            try {
                fileLockLock = randomAccessFile.getChannel().lock();
                HashMap<String, String> fromFile = readFromFile(str);
                for (BaseClientReport baseClientReport : baseClientReportArr) {
                    if (baseClientReport != null) {
                        String strGenerateKey = generateKey((PerfClientReport) baseClientReport);
                        long j2 = ((PerfClientReport) baseClientReport).perfCounts;
                        long j3 = ((PerfClientReport) baseClientReport).perfLatencies;
                        if (!TextUtils.isEmpty(strGenerateKey) && j2 > 0 && j3 >= 0) {
                            putInMemeory(fromFile, strGenerateKey, j2, j3);
                        }
                    }
                }
                writeToFile(str, fromFile);
                if (fileLockLock != null && fileLockLock.isValid()) {
                    try {
                        fileLockLock.release();
                    } catch (IOException e2) {
                        e = e2;
                        MyLog.e(e);
                    }
                }
            } catch (Throwable unused) {
                try {
                    MyLog.v("failed to write perf to file ");
                    if (fileLockLock != null) {
                        if (zIsValid) {
                            try {
                            } catch (IOException e3) {
                                e = e3;
                                MyLog.e(e);
                            }
                        }
                    }
                } finally {
                    if (fileLockLock != null && fileLockLock.isValid()) {
                        try {
                            fileLockLock.release();
                        } catch (IOException e4) {
                            MyLog.e(e4);
                        }
                    }
                    IOUtils.closeQuietly(randomAccessFile);
                }
            }
        } catch (Throwable unused2) {
            randomAccessFile = null;
        }
    }

    /* JADX WARN: Code duplicated, block: B:13:0x0050  */
    /* JADX WARN: Instruction removed from duplicated block: B:13:0x0050, please report this as an issue */
    private static void putInMemeory(HashMap<String, String> map, String str, long j2, long j3) {
        String str2;
        String str3 = map.get(str);
        if (TextUtils.isEmpty(str3)) {
            map.put(str, j2 + "#" + j3);
            return;
        }
        long[] valueStr = parseValueStr(str3);
        if (valueStr != null) {
            long j4 = valueStr[0];
            if (j4 > 0) {
                long j5 = valueStr[1];
                if (j5 < 0) {
                    str2 = j2 + "#" + j3;
                } else {
                    str2 = (j2 + j4) + "#" + (j3 + j5);
                }
            } else {
                str2 = j2 + "#" + j3;
            }
        } else {
            str2 = j2 + "#" + j3;
        }
        map.put(str, str2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v12, types: [int] */
    /* JADX WARN: Type inference failed for: r1v13 */
    /* JADX WARN: Type inference failed for: r1v14 */
    /* JADX WARN: Type inference failed for: r1v15, types: [java.lang.Object] */
    /* JADX WARN: Type inference failed for: r1v17 */
    /* JADX WARN: Type inference failed for: r1v18 */
    /* JADX WARN: Type inference failed for: r1v19 */
    /* JADX WARN: Type inference failed for: r1v20 */
    /* JADX WARN: Type inference failed for: r1v3 */
    /* JADX WARN: Type inference failed for: r1v4 */
    /* JADX WARN: Type inference failed for: r1v5, types: [java.io.Closeable] */
    /* JADX WARN: Type inference failed for: r1v7 */
    /* JADX WARN: Type inference failed for: r1v8 */
    /* JADX WARN: Type inference failed for: r1v9 */
    /* JADX WARN: Type inference failed for: r3v2, types: [java.lang.CharSequence] */
    /* JADX WARN: Type inference failed for: r4v0, types: [java.lang.CharSequence] */
    /* JADX WARN: Type inference failed for: r5v8, types: [java.lang.String[]] */
    /* JADX WARN: Type inference failed for: r5v9, types: [java.lang.Object] */
    private static HashMap<String, String> readFromFile(String str) throws Throwable {
        HashMap map = new HashMap();
        if (TextUtils.isEmpty(str) || !new File(str).exists()) {
            return map;
        }
        ?? r1 = 0;
        ?? length = 0;
        BufferedReader bufferedReader = null;
        try {
            try {
                BufferedReader bufferedReader2 = new BufferedReader(new FileReader(str));
                while (true) {
                    try {
                        String line = bufferedReader2.readLine();
                        if (line == null) {
                            break;
                        }
                        ?? Split = line.split(ClientReportConstants.SEPARATOR);
                        length = Split.length;
                        if (length >= 2) {
                            length = 0;
                            length = 0;
                            if (!TextUtils.isEmpty(Split[0]) && !TextUtils.isEmpty(Split[1])) {
                                length = Split[0];
                                map.put(length, Split[1]);
                            }
                        }
                    } catch (Exception e2) {
                        e = e2;
                        bufferedReader = bufferedReader2;
                        MyLog.e(e);
                        IOUtils.closeQuietly(bufferedReader);
                        r1 = bufferedReader;
                    } catch (Throwable th) {
                        th = th;
                        r1 = bufferedReader2;
                        IOUtils.closeQuietly(r1);
                        throw th;
                    }
                }
                IOUtils.closeQuietly(bufferedReader2);
                r1 = length;
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e3) {
            e = e3;
        }
        return map;
    }

    private static String[] spiltKey(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        return str.split("#");
    }

    private static PerfClientReport spiltKeyForModel(String str) {
        PerfClientReport blankInstance = null;
        try {
            String[] strArrSpiltKey = spiltKey(str);
            if (strArrSpiltKey == null || strArrSpiltKey.length < 4 || TextUtils.isEmpty(strArrSpiltKey[0]) || TextUtils.isEmpty(strArrSpiltKey[1]) || TextUtils.isEmpty(strArrSpiltKey[2]) || TextUtils.isEmpty(strArrSpiltKey[3])) {
                return null;
            }
            blankInstance = PerfClientReport.getBlankInstance();
            blankInstance.production = Integer.parseInt(strArrSpiltKey[0]);
            blankInstance.clientInterfaceId = strArrSpiltKey[1];
            blankInstance.reportType = Integer.parseInt(strArrSpiltKey[2]);
            blankInstance.code = Integer.parseInt(strArrSpiltKey[3]);
            return blankInstance;
        } catch (Exception unused) {
            MyLog.v("parse per key error");
            return blankInstance;
        }
    }

    private static void writeToFile(String str, HashMap<String, String> map) throws Throwable {
        BufferedWriter bufferedWriter;
        Throwable th;
        Exception e2;
        if (TextUtils.isEmpty(str) || map == null || map.size() == 0) {
            return;
        }
        File file = new File(str);
        if (file.exists()) {
            file.delete();
        }
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(file));
            try {
                try {
                    for (String str2 : map.keySet()) {
                        bufferedWriter.write(str2 + ClientReportConstants.SEPARATOR + map.get(str2));
                        bufferedWriter.newLine();
                    }
                } catch (Exception e3) {
                    e2 = e3;
                    MyLog.e(e2);
                }
            } catch (Throwable th2) {
                th = th2;
                IOUtils.closeQuietly(bufferedWriter);
                throw th;
            }
        } catch (Exception e4) {
            bufferedWriter = null;
            e2 = e4;
        } catch (Throwable th3) {
            bufferedWriter = null;
            th = th3;
            IOUtils.closeQuietly(bufferedWriter);
            throw th;
        }
        IOUtils.closeQuietly(bufferedWriter);
    }
}
