package com.xiaomi.push.service;

import android.content.Context;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.DataCryptUtils;
import com.xiaomi.channel.commonutils.android.SharedPreferenceManager;
import com.xiaomi.channel.commonutils.file.IOUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.misc.ByteUtils;
import com.xiaomi.channel.commonutils.misc.ScheduledJobManager;
import com.xiaomi.channel.commonutils.string.Base64Coder;
import com.xiaomi.channel.commonutils.string.XMStringUtils;
import com.xiaomi.xmpush.thrift.ClientUploadDataItem;
import com.xiaomi.xmpush.thrift.XmPushThriftSerializeUtils;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public class TinyDataStorage {
    public static final int TINY_DATA_BYTE_MAX_SIZE = 30720;
    private static final String TINY_DATA_CACHE_DEBUG_FILE_NAME = "tiny_data_debug.txt";
    public static final String TINY_DATA_CACHE_FILE_LOCK = "tiny_data.lock";
    public static final String TINY_DATA_CACHE_FILE_NAME = "tiny_data.data";
    public static final Object mTinyDataLock4Thread = new Object();

    public static void cacheTinyData(final Context context, final ClientUploadDataItem clientUploadDataItem) {
        if (TinyDataHelper.shouldUpload(clientUploadDataItem.getPkgName())) {
            ScheduledJobManager.getInstance(context).addOneShootJob(new Runnable() { // from class: com.xiaomi.push.service.TinyDataStorage.1
                @Override // java.lang.Runnable
                public void run() {
                    RandomAccessFile randomAccessFile;
                    synchronized (TinyDataStorage.mTinyDataLock4Thread) {
                        FileLock fileLockLock = null;
                        try {
                            try {
                                File file = new File(context.getFilesDir(), TinyDataStorage.TINY_DATA_CACHE_FILE_LOCK);
                                IOUtils.createFileQuietly(file);
                                randomAccessFile = new RandomAccessFile(file, "rw");
                                try {
                                    fileLockLock = randomAccessFile.getChannel().lock();
                                    TinyDataStorage.writeTinyData2File(context, clientUploadDataItem);
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
            });
        }
    }

    public static byte[] getTinyDataKeyWithDefault(Context context) {
        String stringValue = SharedPreferenceManager.getInstance(context).getStringValue(PushConstants.SP_NAME_MIPUSH, PushConstants.SP_KEY_TINY_DATA_KEY, "");
        if (TextUtils.isEmpty(stringValue)) {
            stringValue = XMStringUtils.generateRandomString(20);
            SharedPreferenceManager.getInstance(context).setStringnValue(PushConstants.SP_NAME_MIPUSH, PushConstants.SP_KEY_TINY_DATA_KEY, stringValue);
        }
        return parseKey(stringValue);
    }

    private static byte[] parseKey(String str) {
        byte[] bArrCopyOf = Arrays.copyOf(Base64Coder.decode(str), 16);
        bArrCopyOf[0] = 68;
        bArrCopyOf[15] = 84;
        return bArrCopyOf;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v0, types: [byte[]] */
    /* JADX WARN: Type inference failed for: r0v1 */
    /* JADX WARN: Type inference failed for: r0v12 */
    /* JADX WARN: Type inference failed for: r0v19 */
    /* JADX WARN: Type inference failed for: r0v20 */
    /* JADX WARN: Type inference failed for: r0v4 */
    /* JADX WARN: Type inference failed for: r0v5, types: [java.io.Closeable] */
    /* JADX WARN: Type inference failed for: r0v7, types: [java.io.Closeable] */
    public static void writeTinyData2File(Context context, ClientUploadDataItem clientUploadDataItem) throws Throwable {
        BufferedOutputStream bufferedOutputStream;
        BufferedOutputStream bufferedOutputStream2;
        ?? tinyDataKeyWithDefault = getTinyDataKeyWithDefault(context);
        try {
            try {
                byte[] bArrMipushEncrypt = DataCryptUtils.mipushEncrypt(tinyDataKeyWithDefault, XmPushThriftSerializeUtils.convertThriftObjectToBytes(clientUploadDataItem));
                if (bArrMipushEncrypt == null || bArrMipushEncrypt.length < 1) {
                    MyLog.w("TinyData write to cache file failed case encryption fail item:" + clientUploadDataItem.getId() + "  ts:" + System.currentTimeMillis());
                } else {
                    if (bArrMipushEncrypt.length <= 30720) {
                        BufferedOutputStream bufferedOutputStream3 = new BufferedOutputStream(new FileOutputStream(new File(context.getFilesDir(), TINY_DATA_CACHE_FILE_NAME), true));
                        try {
                            bufferedOutputStream3.write(ByteUtils.parseInt(bArrMipushEncrypt.length));
                            bufferedOutputStream3.write(bArrMipushEncrypt);
                            bufferedOutputStream3.flush();
                            IOUtils.closeQuietly(null);
                            IOUtils.closeQuietly(bufferedOutputStream3);
                            return;
                        } catch (IOException e2) {
                            bufferedOutputStream2 = bufferedOutputStream3;
                            e = e2;
                            MyLog.e("TinyData write to cache file failed cause io exception item:" + clientUploadDataItem.getId(), e);
                            tinyDataKeyWithDefault = bufferedOutputStream2;
                            IOUtils.closeQuietly(null);
                            IOUtils.closeQuietly(tinyDataKeyWithDefault);
                            return;
                        } catch (Exception e3) {
                            bufferedOutputStream = bufferedOutputStream3;
                            e = e3;
                            MyLog.e("TinyData write to cache file  failed item:" + clientUploadDataItem.getId(), e);
                            tinyDataKeyWithDefault = bufferedOutputStream;
                            IOUtils.closeQuietly(null);
                            IOUtils.closeQuietly(tinyDataKeyWithDefault);
                            return;
                        } catch (Throwable th) {
                            tinyDataKeyWithDefault = bufferedOutputStream3;
                            th = th;
                            IOUtils.closeQuietly(null);
                            IOUtils.closeQuietly(tinyDataKeyWithDefault);
                            throw th;
                        }
                    }
                    MyLog.w("TinyData write to cache file failed case too much data content item:" + clientUploadDataItem.getId() + "  ts:" + System.currentTimeMillis());
                }
                IOUtils.closeQuietly(null);
                IOUtils.closeQuietly(null);
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (IOException e4) {
            e = e4;
            bufferedOutputStream2 = null;
        } catch (Exception e5) {
            e = e5;
            bufferedOutputStream = null;
        } catch (Throwable th3) {
            th = th3;
            tinyDataKeyWithDefault = 0;
        }
    }
}
