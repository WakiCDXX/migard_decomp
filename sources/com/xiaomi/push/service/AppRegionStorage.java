package com.xiaomi.push.service;

import android.content.Context;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.file.IOUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

/* JADX INFO: loaded from: classes.dex */
public class AppRegionStorage {
    private static volatile AppRegionStorage sInstance;
    private Context mContext;
    private volatile String mCountryCode;
    private volatile String mRegion;
    private final Object mRegionLock = new Object();
    private final Object mCountryCodeLock = new Object();
    private final String REGION_CACHE_FILE_NAME = "mipush_region";
    private final String COUNTRY_CODE_CACHE_FILE_NAME = "mipush_country_code";
    private final String REGION_CACHE_FILE_LOCK = "mipush_region.lock";
    private final String COUNTRY_CODE_CACHE_FILE_LOCK = "mipush_country_code.lock";

    public AppRegionStorage(Context context) {
        this.mContext = context;
    }

    public static AppRegionStorage getInstance(Context context) {
        if (sInstance == null) {
            synchronized (AppRegionStorage.class) {
                try {
                    if (sInstance == null) {
                        sInstance = new AppRegionStorage(context);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return sInstance;
    }

    private String readFromFileWithLock(Context context, String str, String str2, Object obj) {
        RandomAccessFile randomAccessFile;
        FileLock fileLockLock;
        File file = new File(context.getFilesDir(), str);
        FileLock fileLock = null;
        if (!file.exists()) {
            MyLog.w("No ready file to get data from " + str);
            return null;
        }
        synchronized (obj) {
            try {
                File file2 = new File(context.getFilesDir(), str2);
                IOUtils.createFileQuietly(file2);
                randomAccessFile = new RandomAccessFile(file2, "rw");
                try {
                    fileLockLock = randomAccessFile.getChannel().lock();
                    try {
                        try {
                            String strFileToStr = IOUtils.fileToStr(file);
                            if (fileLockLock != null && fileLockLock.isValid()) {
                                try {
                                    fileLockLock.release();
                                } catch (IOException e2) {
                                    MyLog.e(e2);
                                }
                            }
                            IOUtils.closeQuietly(randomAccessFile);
                            return strFileToStr;
                        } catch (Exception e3) {
                            e = e3;
                            MyLog.e(e);
                            if (fileLockLock != null && fileLockLock.isValid()) {
                                try {
                                    fileLockLock.release();
                                } catch (IOException e4) {
                                    MyLog.e(e4);
                                }
                            }
                            IOUtils.closeQuietly(randomAccessFile);
                            return null;
                        }
                    } catch (Throwable th) {
                        th = th;
                        fileLock = fileLockLock;
                        if (fileLock != null && fileLock.isValid()) {
                            try {
                                fileLock.release();
                            } catch (IOException e5) {
                                MyLog.e(e5);
                            }
                        }
                        IOUtils.closeQuietly(randomAccessFile);
                        throw th;
                    }
                } catch (Exception e6) {
                    e = e6;
                    fileLockLock = null;
                } catch (Throwable th2) {
                    th = th2;
                    if (fileLock != null) {
                        fileLock.release();
                    }
                    IOUtils.closeQuietly(randomAccessFile);
                    throw th;
                }
            } catch (Exception e7) {
                e = e7;
                randomAccessFile = null;
                fileLockLock = null;
            } catch (Throwable th3) {
                th = th3;
                randomAccessFile = null;
            }
        }
    }

    private void write2FileWithLock(Context context, String str, String str2, String str3, Object obj) {
        RandomAccessFile randomAccessFile;
        synchronized (obj) {
            FileLock fileLockLock = null;
            try {
                File file = new File(context.getFilesDir(), str3);
                IOUtils.createFileQuietly(file);
                randomAccessFile = new RandomAccessFile(file, "rw");
                try {
                    try {
                        fileLockLock = randomAccessFile.getChannel().lock();
                        IOUtils.strToFile(new File(context.getFilesDir(), str2), str);
                        if (fileLockLock != null && fileLockLock.isValid()) {
                            try {
                                fileLockLock.release();
                            } catch (IOException e2) {
                                MyLog.e(e2);
                            }
                        }
                    } catch (Exception e3) {
                        e = e3;
                        MyLog.e(e);
                        if (fileLockLock != null && fileLockLock.isValid()) {
                            try {
                                fileLockLock.release();
                            } catch (IOException e4) {
                                MyLog.e(e4);
                            }
                        }
                    }
                } catch (Throwable th) {
                    th = th;
                    if (fileLockLock != null && fileLockLock.isValid()) {
                        try {
                            fileLockLock.release();
                        } catch (IOException e5) {
                            MyLog.e(e5);
                        }
                    }
                    IOUtils.closeQuietly(randomAccessFile);
                    throw th;
                }
            } catch (Exception e6) {
                e = e6;
                randomAccessFile = null;
            } catch (Throwable th2) {
                th = th2;
                randomAccessFile = null;
                if (fileLockLock != null) {
                    fileLockLock.release();
                }
                IOUtils.closeQuietly(randomAccessFile);
                throw th;
            }
            IOUtils.closeQuietly(randomAccessFile);
        }
    }

    public String getCountryCode() {
        if (TextUtils.isEmpty(this.mCountryCode)) {
            this.mCountryCode = readFromFileWithLock(this.mContext, "mipush_country_code", "mipush_country_code.lock", this.mCountryCodeLock);
        }
        return this.mCountryCode;
    }

    public String getRegion() {
        if (TextUtils.isEmpty(this.mRegion)) {
            this.mRegion = readFromFileWithLock(this.mContext, "mipush_region", "mipush_region.lock", this.mRegionLock);
        }
        return this.mRegion;
    }

    public void setCountryCode(String str, boolean z2) {
        if (!TextUtils.equals(str, this.mCountryCode)) {
            this.mCountryCode = str;
        }
        if (z2) {
            write2FileWithLock(this.mContext, str, "mipush_country_code", "mipush_region.lock", this.mRegionLock);
        }
    }

    public void setRegion(String str, boolean z2) {
        if (!TextUtils.equals(str, this.mRegion)) {
            this.mRegion = str;
        }
        if (z2) {
            write2FileWithLock(this.mContext, str, "mipush_region", "mipush_region.lock", this.mRegionLock);
        }
    }
}
