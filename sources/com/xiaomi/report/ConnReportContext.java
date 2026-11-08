package com.xiaomi.report;

import android.net.TrafficStats;
import android.os.Process;
import android.os.SystemClock;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.network.Network;
import com.xiaomi.mipush.sdk.stat.db.MessageInfoContract;
import com.xiaomi.push.service.XMPushService;
import com.xiaomi.push.thrift.ChannelStatsType;
import com.xiaomi.push.thrift.StatsEvent;
import com.xiaomi.smack.Connection;
import com.xiaomi.smack.ConnectionListener;
import com.xiaomi.smack.SmackConfiguration;

/* JADX INFO: loaded from: classes.dex */
public class ConnReportContext implements ConnectionListener {
    private static final int NETWORKDURATION_MAX_THRESHOLD = 5400000;
    private static final int NETWORKDURATION_MIN_THRESHOLD = 30000;
    Connection connection;
    private Exception exception;
    private long mRxBytes;
    private long mTxBytes;
    XMPushService pushService;
    private int reason;
    private long networkConnectedTime = 0;
    private long accumulatedNetworkDuration = 0;
    private long channelConnectedTime = 0;
    private long accumulatedChannelDuration = 0;
    private String connectionPoint = "";

    ConnReportContext(XMPushService xMPushService) {
        this.mTxBytes = 0L;
        this.mRxBytes = 0L;
        this.pushService = xMPushService;
        resetChannelStats();
        int iMyUid = Process.myUid();
        try {
            this.mRxBytes = TrafficStats.getUidRxBytes(iMyUid);
            this.mTxBytes = TrafficStats.getUidTxBytes(iMyUid);
        } catch (Exception e2) {
            MyLog.w("Failed to obtain traffic data during initialization: " + e2);
            this.mRxBytes = -1L;
            this.mTxBytes = -1L;
        }
    }

    private void resetChannelStats() {
        this.accumulatedNetworkDuration = 0L;
        this.accumulatedChannelDuration = 0L;
        this.networkConnectedTime = 0L;
        this.channelConnectedTime = 0L;
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        if (Network.hasNetwork(this.pushService)) {
            this.networkConnectedTime = jElapsedRealtime;
        }
        if (this.pushService.isConnected()) {
            this.channelConnectedTime = jElapsedRealtime;
        }
    }

    private synchronized void statsChannelDuration() {
        MyLog.v("stat connpt = " + this.connectionPoint + " netDuration = " + this.accumulatedNetworkDuration + " ChannelDuration = " + this.accumulatedChannelDuration + " channelConnectedTime = " + this.channelConnectedTime);
        StatsEvent statsEvent = new StatsEvent();
        statsEvent.chid = (byte) 0;
        statsEvent.setType(ChannelStatsType.CHANNEL_ONLINE_RATE.getValue());
        statsEvent.setConnpt(this.connectionPoint);
        statsEvent.setTime((int) (System.currentTimeMillis() / 1000));
        statsEvent.setValue((int) (this.accumulatedNetworkDuration / 1000));
        statsEvent.setSubvalue((int) (this.accumulatedChannelDuration / 1000));
        ConnReportHandler.getInstance().add(statsEvent);
        resetChannelStats();
    }

    @Override // com.xiaomi.smack.ConnectionListener
    public void connectionClosed(Connection connection, int i2, Exception exc) {
        long uidRxBytes;
        long uidTxBytes;
        if (this.reason == 0 && this.exception == null) {
            this.reason = i2;
            this.exception = exc;
            ConnReportHelper.connectionDown(connection.getHost(), exc);
        }
        if (i2 == 22 && this.channelConnectedTime != 0) {
            long lastPingRecv = connection.getLastPingRecv() - this.channelConnectedTime;
            if (lastPingRecv < 0) {
                lastPingRecv = 0;
            }
            this.accumulatedChannelDuration += lastPingRecv + ((long) (SmackConfiguration.getPingInteval() / 2));
            this.channelConnectedTime = 0L;
        }
        statsChannelIfNeed();
        int iMyUid = Process.myUid();
        try {
            uidRxBytes = TrafficStats.getUidRxBytes(iMyUid);
            uidTxBytes = TrafficStats.getUidTxBytes(iMyUid);
        } catch (Exception e2) {
            MyLog.w("Failed to obtain traffic data: " + e2);
            uidRxBytes = -1;
            uidTxBytes = -1L;
        }
        MyLog.v("Stats rx=" + (uidRxBytes - this.mRxBytes) + ", tx=" + (uidTxBytes - this.mTxBytes));
        this.mRxBytes = uidRxBytes;
        this.mTxBytes = uidTxBytes;
    }

    @Override // com.xiaomi.smack.ConnectionListener
    public void connectionStarted(Connection connection) {
        this.reason = 0;
        this.exception = null;
        this.connection = connection;
        this.connectionPoint = Network.getActiveConnPoint(this.pushService);
        ConnReportHelper.trackStart(0, ChannelStatsType.CONN_SUCCESS.getValue());
    }

    Exception getCaughtException() {
        return this.exception;
    }

    @Override // com.xiaomi.smack.ConnectionListener
    public void reconnectionFailed(Connection connection, Exception exc) {
        ConnReportHelper.stats(0, ChannelStatsType.CHANNEL_CON_FAIL.getValue(), 1, connection.getHost(), Network.isConnectivity(this.pushService) ? 1 : 0);
        statsChannelIfNeed();
    }

    @Override // com.xiaomi.smack.ConnectionListener
    public void reconnectionSuccessful(Connection connection) {
        statsChannelIfNeed();
        this.channelConnectedTime = SystemClock.elapsedRealtime();
        ConnReportHelper.trackEnd(0, ChannelStatsType.CONN_SUCCESS.getValue(), connection.getHost(), connection.getConnTryTimes());
    }

    public synchronized void statsChannelIfNeed() {
        try {
            XMPushService xMPushService = this.pushService;
            if (xMPushService == null) {
                return;
            }
            String activeConnPoint = Network.getActiveConnPoint(xMPushService);
            boolean zIsConnectivity = Network.isConnectivity(this.pushService);
            long jElapsedRealtime = SystemClock.elapsedRealtime();
            long j2 = this.networkConnectedTime;
            if (j2 > 0) {
                this.accumulatedNetworkDuration += jElapsedRealtime - j2;
                this.networkConnectedTime = 0L;
            }
            long j3 = this.channelConnectedTime;
            if (j3 != 0) {
                this.accumulatedChannelDuration += jElapsedRealtime - j3;
                this.channelConnectedTime = 0L;
            }
            if (zIsConnectivity) {
                if ((!TextUtils.equals(this.connectionPoint, activeConnPoint) && this.accumulatedNetworkDuration > MessageInfoContract.TIMEOUT) || this.accumulatedNetworkDuration > 5400000) {
                    statsChannelDuration();
                }
                this.connectionPoint = activeConnPoint;
                if (this.networkConnectedTime == 0) {
                    this.networkConnectedTime = jElapsedRealtime;
                }
                if (this.pushService.isConnected()) {
                    this.channelConnectedTime = jElapsedRealtime;
                }
            }
        } catch (Throwable th) {
            throw th;
        }
    }
}
