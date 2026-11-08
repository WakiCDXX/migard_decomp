package com.xiaomi.push.service;

import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.slim.Blob;
import com.xiaomi.smack.XMPPException;

/* JADX INFO: loaded from: classes.dex */
class BatchSendMessageJob extends XMPushService.Job {
    private Blob[] mBlobs;
    private XMPushService pushService;

    public BatchSendMessageJob(XMPushService xMPushService, Blob[] blobArr) {
        super(4);
        this.pushService = xMPushService;
        this.mBlobs = blobArr;
    }

    @Override // com.xiaomi.push.service.XMPushService.Job
    public String getDesc() {
        return "batch send message.";
    }

    @Override // com.xiaomi.push.service.XMPushService.Job
    public void process() {
        try {
            Blob[] blobArr = this.mBlobs;
            if (blobArr != null) {
                this.pushService.batchSendPacket(blobArr);
            }
        } catch (XMPPException e2) {
            MyLog.e(e2);
            this.pushService.disconnect(10, e2);
        }
    }
}
