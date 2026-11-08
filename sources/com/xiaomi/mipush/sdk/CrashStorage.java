package com.xiaomi.mipush.sdk;

import android.content.Context;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.file.IOUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class CrashStorage {
    public static final String CRASH_FILE_PATH = "/crash";
    private static final Object mCrashFileLock = new Object();
    private static volatile CrashStorage sInstance;
    private Context mContext;

    private CrashStorage(Context context) {
        this.mContext = context;
    }

    private File getCrashFileBySummary(String str) {
        File file = new File(this.mContext.getFilesDir() + CRASH_FILE_PATH);
        if (!file.exists()) {
            file.mkdirs();
            return null;
        }
        File[] fileArrListFiles = file.listFiles();
        for (int i2 = 0; i2 < fileArrListFiles.length; i2++) {
            if (fileArrListFiles[i2].isFile() && fileArrListFiles[i2].getName().startsWith(str)) {
                return fileArrListFiles[i2];
            }
        }
        return null;
    }

    public static CrashStorage getInstance(Context context) {
        if (sInstance == null) {
            synchronized (CrashStorage.class) {
                try {
                    if (sInstance == null) {
                        sInstance = new CrashStorage(context);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return sInstance;
    }

    public ArrayList<File> getAllCrashFile() {
        ArrayList<File> arrayList = new ArrayList<>();
        File file = new File(this.mContext.getFilesDir() + CRASH_FILE_PATH);
        if (!file.exists()) {
            file.mkdirs();
            return arrayList;
        }
        File[] fileArrListFiles = file.listFiles();
        for (int i2 = 0; i2 < fileArrListFiles.length; i2++) {
            String[] strArrSplit = fileArrListFiles[i2].getName().split(":");
            if (strArrSplit.length >= 2 && Integer.parseInt(strArrSplit[1]) >= 1 && fileArrListFiles[i2].isFile()) {
                arrayList.add(fileArrListFiles[i2]);
            }
        }
        return arrayList;
    }

    public String getCrashSummary(File file) {
        if (file == null) {
            return "";
        }
        String[] strArrSplit = file.getName().split(":");
        return strArrSplit.length != 2 ? "" : strArrSplit[0];
    }

    public void writeCrash2File(String str, String str2) {
        if (TextUtils.isEmpty(str2) || TextUtils.isEmpty(str)) {
            return;
        }
        synchronized (mCrashFileLock) {
            try {
                File crashFileBySummary = getCrashFileBySummary(str2);
                if (crashFileBySummary != null) {
                    String[] strArrSplit = crashFileBySummary.getName().split(":");
                    if (strArrSplit.length < 2) {
                        return;
                    }
                    crashFileBySummary.renameTo(new File(this.mContext.getFilesDir() + CRASH_FILE_PATH + "/" + str2 + ":" + String.valueOf(Integer.parseInt(strArrSplit[1]) + 1)));
                } else {
                    FileOutputStream fileOutputStream = null;
                    try {
                        try {
                            FileOutputStream fileOutputStream2 = new FileOutputStream(new File(this.mContext.getFilesDir() + CRASH_FILE_PATH + "/" + str2 + ":1"));
                            try {
                                fileOutputStream2.write(str.getBytes());
                                fileOutputStream2.flush();
                                IOUtils.closeQuietly(fileOutputStream2);
                            } catch (Exception e2) {
                                e = e2;
                                fileOutputStream = fileOutputStream2;
                                MyLog.e(e);
                                IOUtils.closeQuietly(fileOutputStream);
                            } catch (Throwable th) {
                                th = th;
                                fileOutputStream = fileOutputStream2;
                                IOUtils.closeQuietly(fileOutputStream);
                                throw th;
                            }
                        } catch (Exception e3) {
                            e = e3;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                    }
                }
            } catch (Throwable th3) {
                throw th3;
            }
        }
    }
}
