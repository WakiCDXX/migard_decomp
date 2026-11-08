package com.xiaomi.push.mpcd.job;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.file.IOUtils;
import com.xiaomi.channel.commonutils.misc.ByteUtils;
import com.xiaomi.channel.commonutils.misc.CollectionUtils;
import com.xiaomi.channel.commonutils.misc.ScheduledJobManager;
import com.xiaomi.channel.commonutils.network.Network;
import com.xiaomi.channel.commonutils.string.XMStringUtils;
import com.xiaomi.push.mpcd.CDActionProvider;
import com.xiaomi.push.mpcd.CDActionProviderHolder;
import com.xiaomi.push.mpcd.CDataHelper;
import com.xiaomi.push.mpcd.Constants;
import com.xiaomi.push.service.DefaultConfig;
import com.xiaomi.push.service.OnlineConfig;
import com.xiaomi.xmpush.thrift.ActionType;
import com.xiaomi.xmpush.thrift.ClientCollectionType;
import com.xiaomi.xmpush.thrift.ConfigKey;
import com.xiaomi.xmpush.thrift.DataCollectionItem;
import com.xiaomi.xmpush.thrift.NotificationType;
import com.xiaomi.xmpush.thrift.XmPushActionCollectData;
import com.xiaomi.xmpush.thrift.XmPushActionNotification;
import com.xiaomi.xmpush.thrift.XmPushThriftSerializeUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class UploadJob extends ScheduledJobManager.Job {
    private static final int DEFAULT_LAST_UPDATE_DATA_TIMESTAMP = -1;
    public static final String LAST_UPLADTE_DATA_TIMESTAMP = "last_upload_data_timestamp";
    private Context context;
    private OnlineConfig mOnlineConfig;
    private SharedPreferences mSharedPreference;

    public UploadJob(Context context) {
        this.context = context;
        this.mSharedPreference = context.getSharedPreferences("mipush_extra", 0);
        this.mOnlineConfig = OnlineConfig.getInstance(context);
    }

    private boolean ignoreUploadDataAtCurrentNetwork() {
        if (Network.isWIFIConnected(this.context)) {
            return false;
        }
        if ((Network.is4GConnected(this.context) || Network.is5GConnected(this.context)) && !verify4GorFasterUploadData()) {
            return true;
        }
        return (Network.is3GConnected(this.context) && !verify3GUploadData()) || Network.is2GConnected(this.context);
    }

    private List<DataCollectionItem> readFromFile(File file) {
        RandomAccessFile randomAccessFile;
        FileInputStream fileInputStream;
        CDActionProvider cDActionProvider = CDActionProviderHolder.getInstance().getCDActionProvider();
        String regSecret = cDActionProvider == null ? "" : cDActionProvider.getRegSecret();
        FileLock fileLock = null;
        if (TextUtils.isEmpty(regSecret)) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        byte[] bArr = new byte[4];
        synchronized (Constants.cDataLock4Thread) {
            try {
                try {
                    File file2 = new File(this.context.getFilesDir(), Constants.COLLECTED_DATA_LOCK);
                    IOUtils.createFileQuietly(file2);
                    randomAccessFile = new RandomAccessFile(file2, "rw");
                    try {
                        FileLock fileLockLock = randomAccessFile.getChannel().lock();
                        try {
                            fileInputStream = new FileInputStream(file);
                            while (fileInputStream.read(bArr) == 4) {
                                try {
                                    int i2 = ByteUtils.toInt(bArr);
                                    byte[] bArr2 = new byte[i2];
                                    if (fileInputStream.read(bArr2) != i2) {
                                        break;
                                    }
                                    byte[] bArrDecryptData = CDataHelper.decryptData(regSecret, bArr2);
                                    if (bArrDecryptData != null && bArrDecryptData.length != 0) {
                                        DataCollectionItem dataCollectionItem = new DataCollectionItem();
                                        XmPushThriftSerializeUtils.convertByteArrayToThriftObject(dataCollectionItem, bArrDecryptData);
                                        arrayList.add(dataCollectionItem);
                                        recordLastUploadFullData(dataCollectionItem);
                                    }
                                } catch (Exception unused) {
                                    fileLock = fileLockLock;
                                    if (fileLock != null && fileLock.isValid()) {
                                        try {
                                            fileLock.release();
                                        } catch (IOException unused2) {
                                        }
                                    }
                                    IOUtils.closeQuietly(fileInputStream);
                                } catch (Throwable th) {
                                    th = th;
                                    fileLock = fileLockLock;
                                    if (fileLock != null && fileLock.isValid()) {
                                        try {
                                            fileLock.release();
                                        } catch (IOException unused3) {
                                        }
                                    }
                                    IOUtils.closeQuietly(fileInputStream);
                                    IOUtils.closeQuietly(randomAccessFile);
                                    throw th;
                                }
                            }
                            if (fileLockLock != null && fileLockLock.isValid()) {
                                try {
                                    fileLockLock.release();
                                } catch (IOException unused4) {
                                }
                            }
                            IOUtils.closeQuietly(fileInputStream);
                        } catch (Exception unused5) {
                            fileInputStream = null;
                        } catch (Throwable th2) {
                            th = th2;
                            fileInputStream = null;
                        }
                    } catch (Exception unused6) {
                        fileInputStream = null;
                    } catch (Throwable th3) {
                        th = th3;
                        fileInputStream = null;
                    }
                } catch (Throwable th4) {
                    throw th4;
                }
            } catch (Exception unused7) {
                randomAccessFile = null;
                fileInputStream = null;
            } catch (Throwable th5) {
                th = th5;
                randomAccessFile = null;
                fileInputStream = null;
            }
            IOUtils.closeQuietly(randomAccessFile);
        }
        return arrayList;
    }

    private void recordLastUploadFullData(DataCollectionItem dataCollectionItem) {
        if (dataCollectionItem.collectionType != ClientCollectionType.AppInstallList || dataCollectionItem.content.startsWith(CollectionJob.RESULT_SAME_PREFIX)) {
            return;
        }
        SharedPreferences.Editor editorEdit = this.mSharedPreference.edit();
        editorEdit.putLong("dc_job_result_time_4", dataCollectionItem.collectedAt);
        editorEdit.putString("dc_job_result_4", XMStringUtils.getMd5Digest(dataCollectionItem.content));
        editorEdit.commit();
    }

    private void updateUpdateTimeStamp() {
        SharedPreferences.Editor editorEdit = this.mSharedPreference.edit();
        editorEdit.putLong(LAST_UPLADTE_DATA_TIMESTAMP, System.currentTimeMillis() / 1000);
        editorEdit.commit();
    }

    private boolean verify3GUploadData() {
        if (!this.mOnlineConfig.getBooleanValue(ConfigKey.Upload3GSwitch.getValue(), true)) {
            return false;
        }
        return Math.abs((System.currentTimeMillis() / 1000) - this.mSharedPreference.getLong(LAST_UPLADTE_DATA_TIMESTAMP, -1L)) > ((long) Math.max(86400, this.mOnlineConfig.getIntValue(ConfigKey.Upload3GFrequency.getValue(), DefaultConfig.DEFAULT_3G_UPLOAD_PERIOD)));
    }

    private boolean verify4GorFasterUploadData() {
        if (!this.mOnlineConfig.getBooleanValue(ConfigKey.Upload4GSwitch.getValue(), true)) {
            return false;
        }
        return Math.abs((System.currentTimeMillis() / 1000) - this.mSharedPreference.getLong(LAST_UPLADTE_DATA_TIMESTAMP, -1L)) > ((long) Math.max(86400, this.mOnlineConfig.getIntValue(ConfigKey.Upload4GFrequency.getValue(), DefaultConfig.DEFAULT_4G_UPLOAD_PERIOD)));
    }

    @Override // com.xiaomi.channel.commonutils.misc.ScheduledJobManager.Job
    public String getJobId() {
        return "1";
    }

    @Override // java.lang.Runnable
    public void run() {
        File file = new File(this.context.getFilesDir(), Constants.COLLECTED_DATA_FILENAME);
        if (!Network.isConnected(this.context)) {
            if (file.length() > 1863680) {
                file.delete();
                return;
            }
            return;
        }
        if (!ignoreUploadDataAtCurrentNetwork() && file.exists()) {
            List<DataCollectionItem> fromFile = readFromFile(file);
            if (!CollectionUtils.isEmpty(fromFile)) {
                int size = fromFile.size();
                if (size > 4000) {
                    fromFile = fromFile.subList(size - 4000, size);
                }
                XmPushActionCollectData xmPushActionCollectData = new XmPushActionCollectData();
                xmPushActionCollectData.setDataCollectionItems(fromFile);
                byte[] bArrGZip = IOUtils.gZip(XmPushThriftSerializeUtils.convertThriftObjectToBytes(xmPushActionCollectData));
                XmPushActionNotification xmPushActionNotification = new XmPushActionNotification("-1", false);
                xmPushActionNotification.setType(NotificationType.DataCollection.value);
                xmPushActionNotification.setBinaryExtra(bArrGZip);
                CDActionProvider cDActionProvider = CDActionProviderHolder.getInstance().getCDActionProvider();
                if (cDActionProvider != null) {
                    cDActionProvider.uploadNotification(xmPushActionNotification, ActionType.Notification, null);
                }
                updateUpdateTimeStamp();
            }
            file.delete();
        }
    }
}
