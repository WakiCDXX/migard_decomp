package com.xiaomi.report;

import com.xiaomi.push.service.PushClientsManager;
import com.xiaomi.push.service.XMPushService;
import com.xiaomi.push.thrift.ChannelStatsType;
import com.xiaomi.push.thrift.StatsEvent;
import com.xiaomi.smack.Connection;

/* JADX INFO: loaded from: classes.dex */
class BindTracker implements PushClientsManager.ClientLoginInfo.ClientStatusListener {
    private PushClientsManager.ClientLoginInfo client;
    private Connection connection;
    private XMPushService pushService;
    private int reason;
    private boolean tracked = false;
    private PushClientsManager.ClientStatus status = PushClientsManager.ClientStatus.binding;

    /* JADX INFO: renamed from: com.xiaomi.report.BindTracker$2, reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$xiaomi$push$service$PushClientsManager$ClientStatus;

        static {
            int[] iArr = new int[PushClientsManager.ClientStatus.values().length];
            $SwitchMap$com$xiaomi$push$service$PushClientsManager$ClientStatus = iArr;
            try {
                iArr[PushClientsManager.ClientStatus.unbind.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$xiaomi$push$service$PushClientsManager$ClientStatus[PushClientsManager.ClientStatus.binding.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$xiaomi$push$service$PushClientsManager$ClientStatus[PushClientsManager.ClientStatus.binded.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    BindTracker(XMPushService xMPushService, PushClientsManager.ClientLoginInfo clientLoginInfo) {
        this.pushService = xMPushService;
        this.client = clientLoginInfo;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void done() {
        untrack();
        if (this.tracked && this.reason != 11) {
            StatsEvent statsEventCreateStatsEvent = ConnReportHandler.getInstance().createStatsEvent();
            int i2 = AnonymousClass2.$SwitchMap$com$xiaomi$push$service$PushClientsManager$ClientStatus[this.status.ordinal()];
            if (i2 == 1) {
                int i3 = this.reason;
                if (i3 == 17) {
                    statsEventCreateStatsEvent.type = ChannelStatsType.BIND_TCP_READ_TIMEOUT.getValue();
                } else if (i3 == 21) {
                    statsEventCreateStatsEvent.type = ChannelStatsType.BIND_TIMEOUT.getValue();
                } else {
                    try {
                        ConnReportAnalyser.TypeWraper typeWraperFromBind = ConnReportAnalyser.fromBind(ConnReportHandler.getContext().getCaughtException());
                        statsEventCreateStatsEvent.type = typeWraperFromBind.type.getValue();
                        statsEventCreateStatsEvent.setAnnotation(typeWraperFromBind.annotation);
                    } catch (NullPointerException unused) {
                        statsEventCreateStatsEvent = null;
                    }
                }
            } else if (i2 == 3) {
                statsEventCreateStatsEvent.type = ChannelStatsType.BIND_SUCCESS.getValue();
            }
            if (statsEventCreateStatsEvent != null) {
                statsEventCreateStatsEvent.setHost(this.connection.getHost());
                statsEventCreateStatsEvent.setUser(this.client.userId);
                statsEventCreateStatsEvent.value = 1;
                try {
                    statsEventCreateStatsEvent.setChid((byte) Integer.parseInt(this.client.chid));
                } catch (NumberFormatException unused2) {
                }
                ConnReportHandler.getInstance().add(statsEventCreateStatsEvent);
            }
        }
    }

    private void untrack() {
        this.client.removeClientStatusListener(this);
    }

    @Override // com.xiaomi.push.service.PushClientsManager.ClientLoginInfo.ClientStatusListener
    public void onChange(PushClientsManager.ClientStatus clientStatus, PushClientsManager.ClientStatus clientStatus2, int i2) {
        if (!this.tracked && clientStatus == PushClientsManager.ClientStatus.binding) {
            this.status = clientStatus2;
            this.reason = i2;
            this.tracked = true;
        }
        this.pushService.executeJob(new XMPushService.Job(4) { // from class: com.xiaomi.report.BindTracker.1
            @Override // com.xiaomi.push.service.XMPushService.Job
            public String getDesc() {
                return "Handling bind stats";
            }

            @Override // com.xiaomi.push.service.XMPushService.Job
            public void process() {
                BindTracker.this.done();
            }
        });
    }

    void track() {
        this.client.addClientStatusListener(this);
        this.connection = this.pushService.getCurrentConnection();
    }
}
