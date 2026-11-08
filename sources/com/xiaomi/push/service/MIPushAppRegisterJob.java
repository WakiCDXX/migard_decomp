package com.xiaomi.push.service;

import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.push.service.XMPushService.BindJob;
import com.xiaomi.smack.XMPPException;
import java.util.Collection;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class MIPushAppRegisterJob extends XMPushService.Job {
    private String appId;
    private String appToken;
    private String packageName;
    private byte[] payload;
    private XMPushService pushService;

    public MIPushAppRegisterJob(XMPushService xMPushService, String str, String str2, String str3, byte[] bArr) {
        super(9);
        this.pushService = xMPushService;
        this.packageName = str;
        this.payload = bArr;
        this.appId = str2;
        this.appToken = str3;
    }

    @Override // com.xiaomi.push.service.XMPushService.Job
    public String getDesc() {
        return "register app";
    }

    @Override // com.xiaomi.push.service.XMPushService.Job
    public void process() {
        PushClientsManager.ClientLoginInfo next;
        MIPushAccount mIPushAccount = MIPushAccountUtils.getMIPushAccount(this.pushService);
        if (mIPushAccount == null) {
            try {
                mIPushAccount = MIPushAccountUtils.register(this.pushService, this.packageName, this.appId, this.appToken);
            } catch (Exception e2) {
                MyLog.e("fail to register push account. " + e2);
            }
        }
        if (mIPushAccount == null) {
            MyLog.e("no account for registration.");
            MIPushClientManager.notifyRegisterError(this.pushService, 70000002, "no account.");
            return;
        }
        MyLog.w("do registration now.");
        Collection<PushClientsManager.ClientLoginInfo> allClientLoginInfoByChid = PushClientsManager.getInstance().getAllClientLoginInfoByChid("5");
        if (allClientLoginInfoByChid.isEmpty()) {
            next = mIPushAccount.toClientLoginInfo(this.pushService);
            MIPushHelper.prepareClientLoginInfo(this.pushService, next);
            PushClientsManager.getInstance().addActiveClient(next);
        } else {
            next = allClientLoginInfoByChid.iterator().next();
        }
        if (!this.pushService.isConnected()) {
            MIPushClientManager.pendingRegisterApp(this.packageName, this.payload);
            this.pushService.scheduleConnect(true);
            return;
        }
        try {
            PushClientsManager.ClientStatus clientStatus = next.status;
            if (clientStatus == PushClientsManager.ClientStatus.binded) {
                MIPushHelper.sendPacket(this.pushService, this.packageName, this.payload);
            } else if (clientStatus == PushClientsManager.ClientStatus.unbind) {
                MIPushClientManager.pendingRegisterApp(this.packageName, this.payload);
                XMPushService xMPushService = this.pushService;
                Objects.requireNonNull(xMPushService);
                xMPushService.executeJob(xMPushService.new BindJob(next));
            }
        } catch (XMPPException e3) {
            MyLog.e("meet error, disconnect connection. " + e3);
            this.pushService.disconnect(10, e3);
        }
    }
}
