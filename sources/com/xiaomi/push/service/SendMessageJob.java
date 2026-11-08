package com.xiaomi.push.service;

import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.slim.Blob;
import com.xiaomi.smack.XMPPException;

/* JADX INFO: loaded from: classes.dex */
public class SendMessageJob extends XMPushService.Job {
    private Blob mBlob;
    private XMPushService pushService;

    public SendMessageJob(XMPushService xMPushService, Blob blob) {
        super(4);
        this.pushService = xMPushService;
        this.mBlob = blob;
    }

    @Override // com.xiaomi.push.service.XMPushService.Job
    public String getDesc() {
        return "send a message.";
    }

    @Override // com.xiaomi.push.service.XMPushService.Job
    public void process() {
        try {
            Blob blob = this.mBlob;
            if (blob != null) {
                if (ChannelConstants.isIOTDCMStatTime(blob)) {
                    this.mBlob.setDelay(System.currentTimeMillis() - this.mBlob.getCreateTime());
                }
                this.pushService.sendPacket(this.mBlob);
            }
        } catch (XMPPException e2) {
            MyLog.e(e2);
            this.pushService.disconnect(10, e2);
        }
    }
}
