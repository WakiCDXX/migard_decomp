package com.xiaomi.push.mpcd.job;

import android.content.Context;
import com.xiaomi.channel.commonutils.android.DeviceInfo;
import com.xiaomi.xmpush.thrift.ClientCollectionType;

/* JADX INFO: loaded from: classes.dex */
public class StorageCollectionJob extends CollectionJob {
    public StorageCollectionJob(Context context, int i2) {
        super(context, i2);
    }

    @Override // com.xiaomi.push.mpcd.job.CollectionJob
    public String collectInfo() {
        return "ram:" + DeviceInfo.getRamSize() + ",rom:" + DeviceInfo.getRomSize() + "|ramOriginal:" + DeviceInfo.getRamSizeOriginal() + ",romOriginal:" + DeviceInfo.getRomSizeOriginal();
    }

    @Override // com.xiaomi.push.mpcd.job.CollectionJob
    public ClientCollectionType getCollectionType() {
        return ClientCollectionType.Storage;
    }

    @Override // com.xiaomi.channel.commonutils.misc.ScheduledJobManager.Job
    public String getJobId() {
        return "23";
    }
}
