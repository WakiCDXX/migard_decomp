package com.xiaomi.push.mpcd.job;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.file.FileUtils;
import com.xiaomi.channel.commonutils.file.IOUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.misc.ByteUtils;
import com.xiaomi.channel.commonutils.misc.ScheduledJobManager;
import com.xiaomi.channel.commonutils.string.XMStringUtils;
import com.xiaomi.push.mpcd.CDActionProvider;
import com.xiaomi.push.mpcd.CDActionProviderHolder;
import com.xiaomi.push.mpcd.CDataHelper;
import com.xiaomi.push.mpcd.Constants;
import com.xiaomi.push.service.OnlineConfig;
import com.xiaomi.xmpush.thrift.ClientCollectionType;
import com.xiaomi.xmpush.thrift.ConfigKey;
import com.xiaomi.xmpush.thrift.DataCollectionItem;
import com.xiaomi.xmpush.thrift.XmPushThriftSerializeUtils;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

/* JADX INFO: loaded from: classes.dex */
public abstract class CollectionJob extends ScheduledJobManager.Job {
    public static final String KEY_JOB_RESULT_PREFIX = "dc_job_result_";
    public static final String KEY_JOB_RESULT_TIME_PREFIX = "dc_job_result_time_";
    public static final String RESULT_SAME_PREFIX = "same_";
    protected Context context;
    protected int period;

    public CollectionJob(Context context, int i2) {
        this.period = i2;
        this.context = context;
    }

    private String getJobResultKey() {
        return KEY_JOB_RESULT_PREFIX + getJobId();
    }

    private String getJobResultTimeKey() {
        return "dc_job_result_time_" + getJobId();
    }

    public static void writeItemToFile(Context context, DataCollectionItem dataCollectionItem) {
        CDActionProvider cDActionProvider = CDActionProviderHolder.getInstance().getCDActionProvider();
        String regSecret = cDActionProvider == null ? "" : cDActionProvider.getRegSecret();
        if (TextUtils.isEmpty(regSecret) || TextUtils.isEmpty(dataCollectionItem.getContent())) {
            return;
        }
        writeItemToFile(context, dataCollectionItem, regSecret);
    }

    protected boolean checkDataCollectionJobMutual() {
        return CDataHelper.checkDataCollectionJobMutual(this.context, String.valueOf(getJobId()), this.period);
    }

    protected boolean checkPermission() {
        return true;
    }

    protected boolean checkRepeatedData() {
        return false;
    }

    public abstract String collectInfo();

    public abstract ClientCollectionType getCollectionType();

    @Override // java.lang.Runnable
    public void run() {
        String strCollectInfo = collectInfo();
        if (TextUtils.isEmpty(strCollectInfo)) {
            return;
        }
        if (checkDataCollectionJobMutual()) {
            MyLog.w("DC run job mutual: " + getJobId());
            return;
        }
        CDActionProvider cDActionProvider = CDActionProviderHolder.getInstance().getCDActionProvider();
        String regSecret = cDActionProvider == null ? "" : cDActionProvider.getRegSecret();
        if (!TextUtils.isEmpty(regSecret) && checkPermission()) {
            if (checkRepeatedData()) {
                SharedPreferences sharedPreferences = this.context.getSharedPreferences("mipush_extra", 0);
                if (XMStringUtils.getMd5Digest(strCollectInfo).equals(sharedPreferences.getString(getJobResultKey(), null))) {
                    long j2 = sharedPreferences.getLong(getJobResultTimeKey(), 0L);
                    int intValue = OnlineConfig.getInstance(this.context).getIntValue(ConfigKey.DCJobUploadRepeatedInterval.getValue(), 604800);
                    if ((System.currentTimeMillis() - j2) / 1000 < this.period) {
                        return;
                    }
                    if ((System.currentTimeMillis() - j2) / 1000 < intValue) {
                        strCollectInfo = RESULT_SAME_PREFIX + j2;
                    }
                }
            }
            DataCollectionItem dataCollectionItem = new DataCollectionItem();
            dataCollectionItem.setContent(strCollectInfo);
            dataCollectionItem.setCollectedAt(System.currentTimeMillis());
            dataCollectionItem.setCollectionType(getCollectionType());
            writeItemToFile(this.context, dataCollectionItem, regSecret);
        }
    }

    private static void writeItemToFile(Context context, DataCollectionItem dataCollectionItem, String str) {
        BufferedOutputStream bufferedOutputStream;
        RandomAccessFile randomAccessFile;
        byte[] bArrEncryptData = CDataHelper.encryptData(str, XmPushThriftSerializeUtils.convertThriftObjectToBytes(dataCollectionItem));
        if (bArrEncryptData == null || bArrEncryptData.length == 0) {
            return;
        }
        synchronized (Constants.cDataLock4Thread) {
            FileLock fileLock = null;
            BufferedOutputStream bufferedOutputStream2 = null;
            fileLock = null;
            fileLock = null;
            fileLock = null;
            try {
                try {
                    File file = new File(context.getFilesDir(), Constants.COLLECTED_DATA_LOCK);
                    IOUtils.createFileQuietly(file);
                    randomAccessFile = new RandomAccessFile(file, "rw");
                    try {
                        FileLock fileLockLock = randomAccessFile.getChannel().lock();
                        try {
                            File file2 = new File(context.getFilesDir(), Constants.COLLECTED_DATA_FILENAME);
                            if (FileUtils.canWriteInternalFileDir(file2)) {
                                bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file2, true));
                                try {
                                    bufferedOutputStream.write(ByteUtils.parseInt(bArrEncryptData.length));
                                    bufferedOutputStream.write(bArrEncryptData);
                                    bufferedOutputStream.flush();
                                    file2.setLastModified(0L);
                                    bufferedOutputStream2 = bufferedOutputStream;
                                } catch (IOException e2) {
                                    e = e2;
                                    fileLock = fileLockLock;
                                    try {
                                        e.printStackTrace();
                                        if (fileLock != null && fileLock.isValid()) {
                                            try {
                                                fileLock.release();
                                            } catch (IOException unused) {
                                            }
                                        }
                                        IOUtils.closeQuietly(bufferedOutputStream);
                                    } catch (Throwable th) {
                                        th = th;
                                        if (fileLock != null && fileLock.isValid()) {
                                            try {
                                                fileLock.release();
                                            } catch (IOException unused2) {
                                            }
                                        }
                                        IOUtils.closeQuietly(bufferedOutputStream);
                                        IOUtils.closeQuietly(randomAccessFile);
                                        throw th;
                                    }
                                } catch (Throwable th2) {
                                    th = th2;
                                    fileLock = fileLockLock;
                                    if (fileLock != null) {
                                        fileLock.release();
                                    }
                                    IOUtils.closeQuietly(bufferedOutputStream);
                                    IOUtils.closeQuietly(randomAccessFile);
                                    throw th;
                                }
                            }
                            if (fileLockLock != null && fileLockLock.isValid()) {
                                try {
                                    fileLockLock.release();
                                } catch (IOException unused3) {
                                }
                            }
                            IOUtils.closeQuietly(bufferedOutputStream2);
                        } catch (IOException e3) {
                            e = e3;
                            bufferedOutputStream = null;
                        } catch (Throwable th3) {
                            th = th3;
                            bufferedOutputStream = null;
                        }
                    } catch (IOException e4) {
                        e = e4;
                        bufferedOutputStream = null;
                    } catch (Throwable th4) {
                        th = th4;
                        bufferedOutputStream = null;
                    }
                } catch (Throwable th5) {
                    throw th5;
                }
            } catch (IOException e5) {
                e = e5;
                bufferedOutputStream = null;
                randomAccessFile = null;
            } catch (Throwable th6) {
                th = th6;
                bufferedOutputStream = null;
                randomAccessFile = null;
            }
            IOUtils.closeQuietly(randomAccessFile);
        }
    }
}
