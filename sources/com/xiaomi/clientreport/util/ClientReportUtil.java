package com.xiaomi.clientreport.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.file.IOUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.string.Base64Coder;
import com.xiaomi.channel.commonutils.string.XMStringUtils;
import com.xiaomi.clientreport.data.ClientReportConstants;
import com.xiaomi.clientreport.manager.ClientReportLogicManager;
import com.xiaomi.mipush.sdk.Constants;
import com.xiaomi.push.service.PushConstants;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.Arrays;
import java.util.List;
import miui.yellowpage.YellowPageStatistic;

/* JADX INFO: loaded from: classes.dex */
public class ClientReportUtil {
    public static String getEventKeyWithDefault(Context context) {
        String stringValue = SPManager.getInstance(context).getStringValue(ClientReportConstants.SP_FILE_STATUS, ClientReportConstants.SP_KEY_KEY, "");
        if (!TextUtils.isEmpty(stringValue)) {
            return stringValue;
        }
        String strGenerateRandomString = XMStringUtils.generateRandomString(20);
        SPManager.getInstance(context).setStringnValue(ClientReportConstants.SP_FILE_STATUS, ClientReportConstants.SP_KEY_KEY, strGenerateRandomString);
        return strGenerateRandomString;
    }

    public static String getOs() {
        return Build.VERSION.RELEASE + Constants.ACCEPT_TIME_SEPARATOR_SERVER + Build.VERSION.INCREMENTAL;
    }

    public static File[] getReadFileName(Context context, String str) {
        return new File(context.getFilesDir(), str).listFiles(new FilenameFilter() { // from class: com.xiaomi.clientreport.util.ClientReportUtil.2
            @Override // java.io.FilenameFilter
            public boolean accept(File file, String str2) {
                return (TextUtils.isEmpty(str2) || str2.toLowerCase().endsWith(".lock")) ? false : true;
            }
        });
    }

    public static boolean isFileCanBeUse(Context context, String str) {
        File file = new File(str);
        long maxFileLength = ClientReportLogicManager.getInstance(context).getConfig().getMaxFileLength();
        if (!file.exists()) {
            IOUtils.createFileQuietly(file);
            return true;
        }
        try {
            if (file.length() <= maxFileLength) {
                return true;
            }
        } catch (Exception e2) {
            MyLog.e(e2);
        }
        return false;
    }

    public static boolean isSupportXMSFUpload(Context context) {
        try {
            return context.getApplicationContext().getPackageManager().getPackageInfo(PushConstants.PUSH_SERVICE_PACKAGE_NAME, 0).versionCode >= 108;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    /* JADX WARN: Code duplicated, block: B:109:0x0130 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:87:0x012d  */
    public static void moveFiles(Context context, String str, String str2) throws Throwable {
        File file;
        RandomAccessFile randomAccessFile;
        Exception e2;
        if (context == null || str == null || str2 == null) {
            return;
        }
        File file2 = new File(context.getFilesDir(), str2);
        if (!file2.exists()) {
            file2.mkdirs();
        }
        File file3 = new File(context.getFilesDir(), str);
        if (!file3.exists()) {
            file3.mkdirs();
            return;
        }
        File[] fileArrListFiles = file3.listFiles(new FilenameFilter() { // from class: com.xiaomi.clientreport.util.ClientReportUtil.1
            @Override // java.io.FilenameFilter
            public boolean accept(File file4, String str3) {
                return (TextUtils.isEmpty(str3) || str3.toLowerCase().endsWith(".lock")) ? false : true;
            }
        });
        if (fileArrListFiles == null || fileArrListFiles.length <= 0) {
            return;
        }
        long jCurrentTimeMillis = System.currentTimeMillis();
        FileLock fileLockLock = null;
        File file4 = null;
        RandomAccessFile randomAccessFile2 = null;
        for (File file5 : fileArrListFiles) {
            if (file5 != null) {
                try {
                    if (TextUtils.isEmpty(file5.getAbsolutePath())) {
                        if (fileLockLock != null && fileLockLock.isValid()) {
                            try {
                                fileLockLock.release();
                            } catch (IOException e3) {
                                MyLog.e(e3);
                            }
                        }
                        IOUtils.closeQuietly(randomAccessFile2);
                        if (file4 != null) {
                            file4.delete();
                        }
                    } else {
                        file = new File(file5.getAbsolutePath() + ".lock");
                        try {
                            IOUtils.createFileQuietly(file);
                            randomAccessFile = new RandomAccessFile(file, "rw");
                            try {
                                try {
                                    fileLockLock = randomAccessFile.getChannel().lock();
                                    File file6 = new File(file2.getAbsolutePath() + File.separator + file5.getName() + jCurrentTimeMillis);
                                    try {
                                        IOUtils.copyFile(file5, file6);
                                    } catch (IOException e4) {
                                        e4.printStackTrace();
                                        file5.delete();
                                        file6.delete();
                                    }
                                    file5.delete();
                                    if (fileLockLock != null && fileLockLock.isValid()) {
                                        try {
                                            fileLockLock.release();
                                        } catch (IOException e5) {
                                            MyLog.e(e5);
                                        }
                                    }
                                    IOUtils.closeQuietly(randomAccessFile);
                                } catch (Throwable th) {
                                    th = th;
                                    randomAccessFile2 = randomAccessFile;
                                    file4 = file;
                                    if (fileLockLock != null && fileLockLock.isValid()) {
                                        try {
                                            fileLockLock.release();
                                        } catch (IOException e6) {
                                            MyLog.e(e6);
                                        }
                                    }
                                    IOUtils.closeQuietly(randomAccessFile2);
                                    if (file4 == null) {
                                        throw th;
                                    }
                                    file4.delete();
                                    throw th;
                                }
                            } catch (Exception e7) {
                                e2 = e7;
                                MyLog.e(e2);
                                if (fileLockLock != null) {
                                    try {
                                        fileLockLock.release();
                                    } catch (IOException e8) {
                                        MyLog.e(e8);
                                    }
                                }
                                IOUtils.closeQuietly(randomAccessFile);
                                if (file != null) {
                                }
                                randomAccessFile2 = randomAccessFile;
                                file4 = file;
                            }
                        } catch (Exception e9) {
                            randomAccessFile = randomAccessFile2;
                            e2 = e9;
                        } catch (Throwable th2) {
                            th = th2;
                        }
                        file.delete();
                        randomAccessFile2 = randomAccessFile;
                        file4 = file;
                    }
                } catch (Exception e10) {
                    file = file4;
                    randomAccessFile = randomAccessFile2;
                    e2 = e10;
                } catch (Throwable th3) {
                    th = th3;
                }
                MyLog.e(e2);
                if (fileLockLock != null && fileLockLock.isValid()) {
                    fileLockLock.release();
                }
                IOUtils.closeQuietly(randomAccessFile);
                if (file != null) {
                    file.delete();
                }
                randomAccessFile2 = randomAccessFile;
                file4 = file;
            } else {
                if (fileLockLock != null) {
                    fileLockLock.release();
                }
                IOUtils.closeQuietly(randomAccessFile2);
                if (file4 != null) {
                    file4.delete();
                }
            }
        }
    }

    @TargetApi(9)
    public static byte[] parseKey(String str) {
        byte[] bArrCopyOf = Arrays.copyOf(Base64Coder.decode(str), 16);
        bArrCopyOf[0] = 68;
        bArrCopyOf[15] = 84;
        return bArrCopyOf;
    }

    public static void sendData(Context context, String str) {
        Intent intent = new Intent(ClientReportConstants.XMSF_UPLOAD);
        intent.putExtra("pkgname", context.getPackageName());
        intent.putExtra(YellowPageStatistic.Display.CATEGORY, "category_client_report_data");
        intent.putExtra("name", ClientReportConstants.NAME);
        intent.putExtra("data", str);
        context.sendBroadcast(intent, ClientReportConstants.XMSF_UPLOAD_PERMISSION);
    }

    public static void sendFile(Context context, List<String> list) {
        if (list == null || list.size() <= 0 || !isSupportXMSFUpload(context)) {
            return;
        }
        for (String str : list) {
            if (!TextUtils.isEmpty(str)) {
                sendData(context, str);
            }
        }
    }
}
