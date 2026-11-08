package com.xiaomi.tinyData;

import android.content.Context;
import android.content.SharedPreferences;
import com.xiaomi.channel.commonutils.android.DataCryptUtils;
import com.xiaomi.channel.commonutils.file.IOUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.misc.ByteUtils;
import com.xiaomi.channel.commonutils.misc.ScheduledJobManager;
import com.xiaomi.push.service.TinyDataStorage;
import com.xiaomi.push.service.clientReport.ReportConstants;
import com.xiaomi.xmpush.thrift.ClientUploadDataItem;
import com.xiaomi.xmpush.thrift.XmPushThriftSerializeUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class TinyDataCacheReader {
    private static final int TINY_DATA_MAX_UPLOAD_ITEM_COUNT = 8;
    private static final String TINY_DATA_READ_TEMP_FILE_DIR = "/tdReadTemp";
    private static boolean mTinyDataJobIsRunning = false;

    static class TinyDataReadJob implements Runnable {
        private Context mContext;
        private TinyDataUploader mUploader;

        public TinyDataReadJob(Context context, TinyDataUploader tinyDataUploader) {
            this.mUploader = tinyDataUploader;
            this.mContext = context;
        }

        @Override // java.lang.Runnable
        public void run() throws Throwable {
            TinyDataCacheReader.extractTinyData(this.mContext, this.mUploader);
        }
    }

    public static void addTinyDataCacheReadJob(Context context, TinyDataUploader tinyDataUploader) {
        ScheduledJobManager.getInstance(context).addOneShootJob(new TinyDataReadJob(context, tinyDataUploader));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:37:0x00ba  */
    /* JADX WARN: Code duplicated, block: B:39:0x00be  */
    public static void extractTinyData(Context context, TinyDataUploader tinyDataUploader) throws Throwable {
        RandomAccessFile randomAccessFile;
        File file;
        if (mTinyDataJobIsRunning) {
            MyLog.w("TinyData extractTinyData is running");
            return;
        }
        mTinyDataJobIsRunning = true;
        File file2 = new File(context.getFilesDir(), TinyDataStorage.TINY_DATA_CACHE_FILE_NAME);
        if (!file2.exists()) {
            MyLog.w("TinyData no ready file to get data.");
            return;
        }
        verifyFileDir(context);
        byte[] tinyDataKeyWithDefault = TinyDataStorage.getTinyDataKeyWithDefault(context);
        FileLock fileLockLock = null;
        try {
            File file3 = new File(context.getFilesDir(), TinyDataStorage.TINY_DATA_CACHE_FILE_LOCK);
            IOUtils.createFileQuietly(file3);
            randomAccessFile = new RandomAccessFile(file3, "rw");
            try {
                try {
                    fileLockLock = randomAccessFile.getChannel().lock();
                    file2.renameTo(new File(context.getFilesDir() + TINY_DATA_READ_TEMP_FILE_DIR + "/" + TinyDataStorage.TINY_DATA_CACHE_FILE_NAME));
                    if (fileLockLock != null && fileLockLock.isValid()) {
                        try {
                            fileLockLock.release();
                        } catch (IOException e2) {
                            e = e2;
                            MyLog.e(e);
                        }
                    }
                } catch (Exception e3) {
                    e = e3;
                    MyLog.e(e);
                    if (fileLockLock != null && fileLockLock.isValid()) {
                        try {
                            fileLockLock.release();
                        } catch (IOException e4) {
                            e = e4;
                            MyLog.e(e);
                        }
                    }
                    IOUtils.closeQuietly(randomAccessFile);
                    file = new File(context.getFilesDir() + TINY_DATA_READ_TEMP_FILE_DIR + "/" + TinyDataStorage.TINY_DATA_CACHE_FILE_NAME);
                    if (!file.exists()) {
                        MyLog.w("TinyData no ready file to get data.");
                        return;
                    }
                    readTinyDataFromFile(context, tinyDataUploader, file, tinyDataKeyWithDefault);
                    TinyDataCacheProcessor.setIsTinyDataExtracting(false);
                    updateTinyDataUploadTimeStamp(context);
                    mTinyDataJobIsRunning = false;
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
        file = new File(context.getFilesDir() + TINY_DATA_READ_TEMP_FILE_DIR + "/" + TinyDataStorage.TINY_DATA_CACHE_FILE_NAME);
        if (!file.exists()) {
            MyLog.w("TinyData no ready file to get data.");
            return;
        }
        readTinyDataFromFile(context, tinyDataUploader, file, tinyDataKeyWithDefault);
        TinyDataCacheProcessor.setIsTinyDataExtracting(false);
        updateTinyDataUploadTimeStamp(context);
        mTinyDataJobIsRunning = false;
    }

    private static void readTinyDataFromFile(Context context, TinyDataUploader tinyDataUploader, File file, byte[] bArr) throws Throwable {
        ArrayList arrayList = new ArrayList();
        byte[] bArr2 = new byte[4];
        BufferedInputStream bufferedInputStream = null;
        try {
            try {
                BufferedInputStream bufferedInputStream2 = new BufferedInputStream(new FileInputStream(file));
                bufferedInputStream = null;
                loop0: while (true) {
                    int i2 = 0;
                    int length = 0;
                    while (true) {
                        try {
                            int i3 = bufferedInputStream2.read(bArr2);
                            if (i3 == -1) {
                                break loop0;
                            }
                            if (i3 != 4) {
                                MyLog.e("TinyData read from cache file failed cause lengthBuffer error. size:" + i3);
                                break loop0;
                            }
                            int i4 = ByteUtils.toInt(bArr2);
                            if (i4 < 1 || i4 > 30720) {
                                MyLog.e("TinyData read from cache file failed cause lengthBuffer < 1 || too big. length:" + i4);
                                break loop0;
                            }
                            byte[] bArr3 = new byte[i4];
                            int i5 = bufferedInputStream2.read(bArr3);
                            if (i5 != i4) {
                                MyLog.e("TinyData read from cache file failed cause buffer size not equal length. size:" + i5 + "__length:" + i4);
                                break loop0;
                            }
                            byte[] bArrMipushDecrypt = DataCryptUtils.mipushDecrypt(bArr, bArr3);
                            if (bArrMipushDecrypt == null || bArrMipushDecrypt.length == 0) {
                                MyLog.e("TinyData read from cache file failed cause decrypt fail");
                            } else {
                                ClientUploadDataItem clientUploadDataItem = new ClientUploadDataItem();
                                XmPushThriftSerializeUtils.convertByteArrayToThriftObject(clientUploadDataItem, bArrMipushDecrypt);
                                clientUploadDataItem.putToExtra(ReportConstants.UPLOAD_DATA_ITEM_EXTRA_KEY_SIZE, String.valueOf(bArrMipushDecrypt.length));
                                arrayList.add(clientUploadDataItem);
                                i2++;
                                length += bArrMipushDecrypt.length;
                                if (i2 >= 8 || length >= 30720) {
                                    TinyDataCacheUploader.uploadTinyData(context, tinyDataUploader, arrayList);
                                    arrayList.clear();
                                }
                            }
                        } catch (Exception e2) {
                            e = e2;
                            bufferedInputStream = bufferedInputStream2;
                            MyLog.e(e);
                            IOUtils.closeQuietly(bufferedInputStream);
                        } catch (Throwable th) {
                            th = th;
                            bufferedInputStream = bufferedInputStream2;
                            IOUtils.closeQuietly(bufferedInputStream);
                            throw th;
                        }
                    }
                }
                TinyDataCacheUploader.uploadTinyData(context, tinyDataUploader, arrayList);
                if (file != null && file.exists() && !file.delete()) {
                    MyLog.w("TinyData delete reading temp file failed");
                }
                IOUtils.closeQuietly(bufferedInputStream2);
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e3) {
            e = e3;
        }
    }

    private static void updateTinyDataUploadTimeStamp(Context context) {
        SharedPreferences.Editor editorEdit = context.getSharedPreferences("mipush_extra", 4).edit();
        editorEdit.putLong(TinyDataCacheProcessor.LAST_TINY_DATA_UPLOAD_TIMESTAMP, System.currentTimeMillis() / 1000);
        editorEdit.commit();
    }

    private static void verifyFileDir(Context context) {
        File file = new File(context.getFilesDir() + TINY_DATA_READ_TEMP_FILE_DIR);
        if (file.exists()) {
            return;
        }
        file.mkdirs();
    }
}
