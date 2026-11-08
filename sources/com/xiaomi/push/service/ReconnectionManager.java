package com.xiaomi.push.service;

import com.miui.powerkeeper.powerchecker.PowerCheckerCloudConfigure;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.push.service.XMPushService.ConnectJob;
import com.xiaomi.report.ConnReportHandler;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
class ReconnectionManager {
    private static int MAX_RETRY_INTERVAL = 300000;
    private static final int SHORT_LIVE_CONN_THRESHOLD = 310000;
    private static final int SLOW_RETRY_THRESHOLD = 4;
    private static final String TAG = "ReconnectionManager";
    private XMPushService mPushService;
    private int attempts = 0;
    private int shortLiveConnCount = 0;
    private int curDelay = 500;
    private long lastConnectTime = 0;

    public ReconnectionManager(XMPushService xMPushService) {
        this.mPushService = xMPushService;
    }

    private int timeDelay() {
        double d2;
        if (this.attempts > 8) {
            return PowerCheckerCloudConfigure.DEFAULT_WAKEUP_ALARM_INTERVAL;
        }
        double dRandom = (Math.random() * 2.0d) + 1.0d;
        int i2 = this.attempts;
        if (i2 > 4) {
            d2 = 60000.0d;
        } else {
            if (i2 <= 1) {
                if (this.lastConnectTime == 0) {
                    return 0;
                }
                if (System.currentTimeMillis() - this.lastConnectTime >= 310000) {
                    this.curDelay = 1000;
                    this.shortLiveConnCount = 0;
                    return 0;
                }
                int i3 = this.curDelay;
                int i4 = MAX_RETRY_INTERVAL;
                if (i3 >= i4) {
                    return i3;
                }
                int i5 = this.shortLiveConnCount + 1;
                this.shortLiveConnCount = i5;
                if (i5 >= 4) {
                    return i4;
                }
                this.curDelay = (int) (((double) i3) * 1.5d);
                return i3;
            }
            d2 = 10000.0d;
        }
        return (int) (dRandom * d2);
    }

    public void onConnectSucceeded() {
        this.lastConnectTime = System.currentTimeMillis();
        this.mPushService.removeJobs(1);
        this.attempts = 0;
    }

    public void tryReconnect(boolean z2) {
        if (!this.mPushService.shouldReconnect()) {
            MyLog.v("should not reconnect as no client or network.");
            return;
        }
        if (z2) {
            if (!this.mPushService.hasJob(1)) {
                this.attempts++;
            }
            this.mPushService.removeJobs(1);
            MyLog.w(TAG, "-->tryReconnect(): exec ConnectJob");
            XMPushService xMPushService = this.mPushService;
            Objects.requireNonNull(xMPushService);
            xMPushService.executeJob(xMPushService.new ConnectJob());
            return;
        }
        if (this.mPushService.hasJob(1)) {
            return;
        }
        int iTimeDelay = timeDelay();
        this.attempts++;
        MyLog.w("schedule reconnect in " + iTimeDelay + "ms");
        XMPushService xMPushService2 = this.mPushService;
        Objects.requireNonNull(xMPushService2);
        xMPushService2.executeJobDelayed(xMPushService2.new ConnectJob(), (long) iTimeDelay);
        if (this.attempts == 2 && ConnReportHandler.getInstance().isAllowStats()) {
            NetworkCheckup.dumpNativeNetInfo();
        }
        if (this.attempts == 3) {
            NetworkCheckup.connectivityTest();
        }
    }
}
