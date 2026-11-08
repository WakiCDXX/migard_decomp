package com.xiaomi.smack;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import com.miui.powerkeeper.utils.ShellUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.network.Network;
import com.xiaomi.mipush.sdk.Constants;
import com.xiaomi.network.Fallback;
import com.xiaomi.network.Host;
import com.xiaomi.network.HostManager;
import com.xiaomi.push.service.HeartbeatHolder;
import com.xiaomi.push.service.XMPushService;
import com.xiaomi.push.thrift.ChannelStatsType;
import com.xiaomi.report.ConnReportHelper;
import com.xiaomi.slim.Blob;
import com.xiaomi.smack.packet.Packet;
import com.xiaomi.smack.util.TaskExecutor;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public abstract class SocketConnection extends Connection {
    private static final int CONNECTION_TIMEOUT = 8000;
    private static final int MAX_SHORT_CONN_COUNT = 2;
    private static final int SHORT_CONNECTION_THRESHOLD = 300000;
    private static final long STAT_CONNECT_FAIL_DURATION = 480000;
    private static final String TAG = "SocketConnection";
    private String connectedHost;
    String connectionID;
    private int curShortConnCount;
    protected Exception failedException;
    private long lastConnectFail;
    protected volatile long lastConnectedTime;
    protected volatile long lastPingReceived;
    protected volatile long lastPingSent;
    protected XMPushService pushService;
    protected Socket socket;

    public SocketConnection(XMPushService xMPushService, ConnectionConfiguration connectionConfiguration) {
        super(xMPushService, connectionConfiguration);
        this.failedException = null;
        this.connectionID = null;
        this.lastPingSent = 0L;
        this.lastPingReceived = 0L;
        this.lastConnectedTime = 0L;
        this.lastConnectFail = 0L;
        this.pushService = xMPushService;
    }

    /* JADX WARN: Code duplicated, block: B:120:0x01ef A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:121:0x01ef A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:123:0x0286 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:49:0x01b2  */
    /* JADX WARN: Code duplicated, block: B:50:0x01b4  */
    /* JADX WARN: Code duplicated, block: B:53:0x01cf  */
    /* JADX WARN: Code duplicated, block: B:58:0x01f4  */
    /* JADX WARN: Code duplicated, block: B:66:0x0244  */
    /* JADX WARN: Code duplicated, block: B:67:0x0246  */
    /* JADX WARN: Code duplicated, block: B:70:0x025f  */
    /* JADX WARN: Code duplicated, block: B:71:0x0277  */
    /* JADX WARN: Code duplicated, block: B:79:0x02cc  */
    /* JADX WARN: Code duplicated, block: B:80:0x02ce  */
    /* JADX WARN: Code duplicated, block: B:83:0x02e7  */
    /* JADX WARN: Code duplicated, block: B:84:0x02fc  */
    /* JADX WARN: Code duplicated, block: B:87:0x0309  */
    /* JADX WARN: Code duplicated, block: B:88:0x030c  */
    /* JADX WARN: Code duplicated, block: B:92:0x0326  */
    /* JADX WARN: Code duplicated, block: B:94:0x032c  */
    /* JADX WARN: Code duplicated, block: B:96:0x033a  */
    /* JADX WARN: Code duplicated, block: B:99:0x035e  */
    /* JADX WARN: Instruction removed from duplicated block: B:50:0x01b4, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:67:0x0246, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:80:0x02ce, please report this as an issue */
    private void connectDirectly(String str, int i2) throws Throwable {
        long j2;
        int i3;
        String str2;
        boolean z2;
        int iElapsedRealtime;
        int i4;
        String str3;
        String str4;
        String str5;
        boolean z3;
        this.failedException = null;
        ArrayList<String> arrayList = new ArrayList<>();
        Integer numPs = MyLog.ps("get bucket for host : " + str);
        numPs.intValue();
        Fallback fallback = getFallback(str);
        MyLog.pe(numPs);
        if (fallback != null) {
            arrayList = fallback.getHosts(true);
        }
        Fallback reservedFallback = HostManager.getInstance().getReservedFallback(str);
        if (reservedFallback != null) {
            for (String str6 : reservedFallback.getHosts(true)) {
                if (arrayList.indexOf(str6) == -1) {
                    arrayList.add(str6);
                }
            }
        }
        if (arrayList.isEmpty()) {
            arrayList.add(str);
        }
        long j3 = 0;
        this.lastConnectedTime = 0L;
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        String activeConnPoint = Network.getActiveConnPoint(this.pushService);
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = arrayList.iterator();
        String str7 = "";
        int i5 = 0;
        while (true) {
            if (it.hasNext()) {
                String next = it.next();
                long jCurrentTimeMillis = System.currentTimeMillis();
                this.connTimes++;
                i4 = i5 + 1;
                try {
                    try {
                        try {
                            MyLog.w("begin to connect to " + next);
                            this.socket = createSocket();
                            this.socket.connect(Host.from(next, i2), CONNECTION_TIMEOUT);
                            MyLog.w("tcp connected");
                            try {
                                this.socket.setTcpNoDelay(true);
                                this.connectedHost = next;
                                initConnection();
                                long jCurrentTimeMillis2 = System.currentTimeMillis() - jCurrentTimeMillis;
                                this.connectTime = jCurrentTimeMillis2;
                                this.connectionPoint = activeConnPoint;
                                if (fallback != null) {
                                    it = it;
                                    sb = sb;
                                    activeConnPoint = activeConnPoint;
                                    z3 = true;
                                    j2 = 0;
                                    fallback = fallback;
                                    try {
                                        fallback.succeedHost(next, jCurrentTimeMillis2, 0L);
                                    } catch (Exception e2) {
                                        e = e2;
                                        str3 = activeConnPoint;
                                        try {
                                            this.failedException = e;
                                            MyLog.e("SMACK: Could not connect to:" + next);
                                            sb.append("SMACK: Could not connect to ");
                                            sb.append(next);
                                            sb.append(" port:");
                                            sb.append(i2);
                                            sb.append(" err:");
                                            sb.append(this.failedException.getClass().getSimpleName());
                                            sb.append(ShellUtils.COMMAND_LINE_END);
                                            if (TextUtils.isEmpty(str7)) {
                                                str2 = next;
                                            } else {
                                                str2 = str7 + "|" + next;
                                            }
                                            ConnReportHelper.connectFail(next, this.failedException);
                                            if (fallback != null) {
                                                fallback.failedHost(next, System.currentTimeMillis() - jCurrentTimeMillis, 0L, this.failedException);
                                            }
                                            if (TextUtils.equals(str3, Network.getActiveConnPoint(this.pushService))) {
                                                str7 = str2;
                                                sb = sb;
                                                activeConnPoint = str3;
                                                i5 = i4;
                                                it = it;
                                                j3 = j2;
                                                fallback = fallback;
                                            } else {
                                                i3 = i4;
                                                z2 = false;
                                            }
                                        } catch (Throwable th) {
                                            th = th;
                                            MyLog.e("SMACK: Could not connect to:" + next);
                                            sb.append("SMACK: Could not connect to ");
                                            sb.append(next);
                                            sb.append(" port:");
                                            sb.append(i2);
                                            sb.append(" err:");
                                            sb.append(this.failedException.getClass().getSimpleName());
                                            sb.append(ShellUtils.COMMAND_LINE_END);
                                            if (TextUtils.isEmpty(str7)) {
                                                str5 = next;
                                            } else {
                                                str5 = str7 + "|" + next;
                                            }
                                            ConnReportHelper.connectFail(next, this.failedException);
                                            if (fallback != null) {
                                                fallback.failedHost(next, System.currentTimeMillis() - jCurrentTimeMillis, 0L, this.failedException);
                                            }
                                            if (!TextUtils.equals(str3, Network.getActiveConnPoint(this.pushService))) {
                                                throw th;
                                            }
                                            str2 = str5;
                                            i3 = i4;
                                            z2 = false;
                                            HostManager.getInstance().persist();
                                            iElapsedRealtime = (int) (SystemClock.elapsedRealtime() - jElapsedRealtime);
                                            if (!z2) {
                                                ConnReportHelper.stats(0, ChannelStatsType.BATCH_TCP_CONN_SUCCESS.getValue(), iElapsedRealtime, str2, i3);
                                                return;
                                            }
                                            if (this.lastConnectFail != j2) {
                                                this.lastConnectFail = SystemClock.elapsedRealtime();
                                                ConnReportHelper.stats(0, ChannelStatsType.BATCH_TCP_CONN_FAIL.getValue(), iElapsedRealtime, str2, Network.isConnectivity(this.pushService.getApplicationContext()) ? 1 : 0);
                                            } else {
                                                this.lastConnectFail = SystemClock.elapsedRealtime();
                                                ConnReportHelper.stats(0, ChannelStatsType.BATCH_TCP_CONN_FAIL.getValue(), iElapsedRealtime, str2, Network.isConnectivity(this.pushService.getApplicationContext()) ? 1 : 0);
                                            }
                                            throw new XMPPException(sb.toString());
                                        }
                                    } catch (Throwable th2) {
                                        th = th2;
                                        try {
                                            this.failedException = new Exception("abnormal exception", th);
                                            MyLog.e(th);
                                            MyLog.e("SMACK: Could not connect to:" + next);
                                            sb.append("SMACK: Could not connect to ");
                                            sb.append(next);
                                            sb.append(" port:");
                                            sb.append(i2);
                                            sb.append(" err:");
                                            sb.append(this.failedException.getClass().getSimpleName());
                                            sb.append(ShellUtils.COMMAND_LINE_END);
                                            if (TextUtils.isEmpty(str7)) {
                                                str2 = next;
                                            } else {
                                                str2 = str7 + "|" + next;
                                            }
                                            ConnReportHelper.connectFail(next, this.failedException);
                                            if (fallback != null) {
                                                fallback.failedHost(next, System.currentTimeMillis() - jCurrentTimeMillis, 0L, this.failedException);
                                            }
                                            str4 = activeConnPoint;
                                            if (TextUtils.equals(str4, Network.getActiveConnPoint(this.pushService))) {
                                                str3 = str4;
                                                str7 = str2;
                                                sb = sb;
                                                activeConnPoint = str3;
                                                i5 = i4;
                                                it = it;
                                                j3 = j2;
                                                fallback = fallback;
                                            } else {
                                                i3 = i4;
                                                z2 = false;
                                            }
                                        } catch (Throwable th3) {
                                            th = th3;
                                            str7 = str7;
                                            str3 = activeConnPoint;
                                            MyLog.e("SMACK: Could not connect to:" + next);
                                            sb.append("SMACK: Could not connect to ");
                                            sb.append(next);
                                            sb.append(" port:");
                                            sb.append(i2);
                                            sb.append(" err:");
                                            sb.append(this.failedException.getClass().getSimpleName());
                                            sb.append(ShellUtils.COMMAND_LINE_END);
                                            if (TextUtils.isEmpty(str7)) {
                                                str5 = next;
                                            } else {
                                                str5 = str7 + "|" + next;
                                            }
                                            ConnReportHelper.connectFail(next, this.failedException);
                                            if (fallback != null) {
                                                fallback.failedHost(next, System.currentTimeMillis() - jCurrentTimeMillis, 0L, this.failedException);
                                            }
                                            if (!TextUtils.equals(str3, Network.getActiveConnPoint(this.pushService))) {
                                                throw th;
                                            }
                                            str2 = str5;
                                            i3 = i4;
                                            z2 = false;
                                            HostManager.getInstance().persist();
                                            iElapsedRealtime = (int) (SystemClock.elapsedRealtime() - jElapsedRealtime);
                                            if (!z2) {
                                                ConnReportHelper.stats(0, ChannelStatsType.BATCH_TCP_CONN_SUCCESS.getValue(), iElapsedRealtime, str2, i3);
                                                return;
                                            }
                                            if (this.lastConnectFail != j2) {
                                                this.lastConnectFail = SystemClock.elapsedRealtime();
                                                ConnReportHelper.stats(0, ChannelStatsType.BATCH_TCP_CONN_FAIL.getValue(), iElapsedRealtime, str2, Network.isConnectivity(this.pushService.getApplicationContext()) ? 1 : 0);
                                            } else {
                                                this.lastConnectFail = SystemClock.elapsedRealtime();
                                                ConnReportHelper.stats(0, ChannelStatsType.BATCH_TCP_CONN_FAIL.getValue(), iElapsedRealtime, str2, Network.isConnectivity(this.pushService.getApplicationContext()) ? 1 : 0);
                                            }
                                            throw new XMPPException(sb.toString());
                                        }
                                    }
                                } else {
                                    it = it;
                                    sb = sb;
                                    activeConnPoint = activeConnPoint;
                                    fallback = fallback;
                                    z3 = true;
                                    j2 = 0;
                                }
                                this.lastConnectedTime = SystemClock.elapsedRealtime();
                                MyLog.w("connected to " + next + " in " + this.connectTime);
                                str2 = str7;
                                z2 = z3;
                                i3 = i4;
                            } catch (Exception e3) {
                                e = e3;
                                j2 = 0;
                                str3 = activeConnPoint;
                                this.failedException = e;
                                MyLog.e("SMACK: Could not connect to:" + next);
                                sb.append("SMACK: Could not connect to ");
                                sb.append(next);
                                sb.append(" port:");
                                sb.append(i2);
                                sb.append(" err:");
                                sb.append(this.failedException.getClass().getSimpleName());
                                sb.append(ShellUtils.COMMAND_LINE_END);
                                if (TextUtils.isEmpty(str7)) {
                                    str2 = next;
                                } else {
                                    str2 = str7 + "|" + next;
                                }
                                ConnReportHelper.connectFail(next, this.failedException);
                                if (fallback != null) {
                                    fallback.failedHost(next, System.currentTimeMillis() - jCurrentTimeMillis, 0L, this.failedException);
                                }
                                if (TextUtils.equals(str3, Network.getActiveConnPoint(this.pushService))) {
                                    i3 = i4;
                                    z2 = false;
                                    HostManager.getInstance().persist();
                                    iElapsedRealtime = (int) (SystemClock.elapsedRealtime() - jElapsedRealtime);
                                    if (!z2) {
                                        ConnReportHelper.stats(0, ChannelStatsType.BATCH_TCP_CONN_SUCCESS.getValue(), iElapsedRealtime, str2, i3);
                                        return;
                                    }
                                    if (this.lastConnectFail != j2) {
                                        this.lastConnectFail = SystemClock.elapsedRealtime();
                                        ConnReportHelper.stats(0, ChannelStatsType.BATCH_TCP_CONN_FAIL.getValue(), iElapsedRealtime, str2, Network.isConnectivity(this.pushService.getApplicationContext()) ? 1 : 0);
                                    } else {
                                        this.lastConnectFail = SystemClock.elapsedRealtime();
                                        ConnReportHelper.stats(0, ChannelStatsType.BATCH_TCP_CONN_FAIL.getValue(), iElapsedRealtime, str2, Network.isConnectivity(this.pushService.getApplicationContext()) ? 1 : 0);
                                    }
                                    throw new XMPPException(sb.toString());
                                }
                                str7 = str2;
                                sb = sb;
                                activeConnPoint = str3;
                                i5 = i4;
                                it = it;
                                j3 = j2;
                                fallback = fallback;
                            } catch (Throwable th4) {
                                th = th4;
                                j2 = 0;
                                this.failedException = new Exception("abnormal exception", th);
                                MyLog.e(th);
                                MyLog.e("SMACK: Could not connect to:" + next);
                                sb.append("SMACK: Could not connect to ");
                                sb.append(next);
                                sb.append(" port:");
                                sb.append(i2);
                                sb.append(" err:");
                                sb.append(this.failedException.getClass().getSimpleName());
                                sb.append(ShellUtils.COMMAND_LINE_END);
                                if (TextUtils.isEmpty(str7)) {
                                    str2 = next;
                                } else {
                                    str2 = str7 + "|" + next;
                                }
                                ConnReportHelper.connectFail(next, this.failedException);
                                if (fallback != null) {
                                    fallback.failedHost(next, System.currentTimeMillis() - jCurrentTimeMillis, 0L, this.failedException);
                                }
                                str4 = activeConnPoint;
                                if (TextUtils.equals(str4, Network.getActiveConnPoint(this.pushService))) {
                                    i3 = i4;
                                    z2 = false;
                                    HostManager.getInstance().persist();
                                    iElapsedRealtime = (int) (SystemClock.elapsedRealtime() - jElapsedRealtime);
                                    if (!z2) {
                                        ConnReportHelper.stats(0, ChannelStatsType.BATCH_TCP_CONN_SUCCESS.getValue(), iElapsedRealtime, str2, i3);
                                        return;
                                    }
                                    if (this.lastConnectFail != j2) {
                                        this.lastConnectFail = SystemClock.elapsedRealtime();
                                        ConnReportHelper.stats(0, ChannelStatsType.BATCH_TCP_CONN_FAIL.getValue(), iElapsedRealtime, str2, Network.isConnectivity(this.pushService.getApplicationContext()) ? 1 : 0);
                                    } else {
                                        this.lastConnectFail = SystemClock.elapsedRealtime();
                                        ConnReportHelper.stats(0, ChannelStatsType.BATCH_TCP_CONN_FAIL.getValue(), iElapsedRealtime, str2, Network.isConnectivity(this.pushService.getApplicationContext()) ? 1 : 0);
                                    }
                                    throw new XMPPException(sb.toString());
                                }
                                str3 = str4;
                                str7 = str2;
                                sb = sb;
                                activeConnPoint = str3;
                                i5 = i4;
                                it = it;
                                j3 = j2;
                                fallback = fallback;
                            }
                        } catch (Exception e4) {
                            e = e4;
                        }
                    } catch (Exception e5) {
                        e = e5;
                        it = it;
                        sb = sb;
                        fallback = fallback;
                        j2 = 0;
                        str3 = activeConnPoint;
                    }
                } catch (Throwable th5) {
                    th = th5;
                }
                HostManager.getInstance().persist();
                iElapsedRealtime = (int) (SystemClock.elapsedRealtime() - jElapsedRealtime);
                if (!z2) {
                    ConnReportHelper.stats(0, ChannelStatsType.BATCH_TCP_CONN_SUCCESS.getValue(), iElapsedRealtime, str2, i3);
                    return;
                }
                if (this.lastConnectFail != j2 || SystemClock.elapsedRealtime() - this.lastConnectFail > STAT_CONNECT_FAIL_DURATION) {
                    this.lastConnectFail = SystemClock.elapsedRealtime();
                    ConnReportHelper.stats(0, ChannelStatsType.BATCH_TCP_CONN_FAIL.getValue(), iElapsedRealtime, str2, Network.isConnectivity(this.pushService.getApplicationContext()) ? 1 : 0);
                }
                throw new XMPPException(sb.toString());
            }
            sb = sb;
            j2 = j3;
            i3 = i5;
            str2 = str7;
            z2 = false;
            HostManager.getInstance().persist();
            iElapsedRealtime = (int) (SystemClock.elapsedRealtime() - jElapsedRealtime);
            if (!z2) {
                ConnReportHelper.stats(0, ChannelStatsType.BATCH_TCP_CONN_SUCCESS.getValue(), iElapsedRealtime, str2, i3);
                return;
            }
            if (this.lastConnectFail != j2) {
                this.lastConnectFail = SystemClock.elapsedRealtime();
                ConnReportHelper.stats(0, ChannelStatsType.BATCH_TCP_CONN_FAIL.getValue(), iElapsedRealtime, str2, Network.isConnectivity(this.pushService.getApplicationContext()) ? 1 : 0);
            } else {
                this.lastConnectFail = SystemClock.elapsedRealtime();
                ConnReportHelper.stats(0, ChannelStatsType.BATCH_TCP_CONN_FAIL.getValue(), iElapsedRealtime, str2, Network.isConnectivity(this.pushService.getApplicationContext()) ? 1 : 0);
            }
            throw new XMPPException(sb.toString());
            str7 = str2;
            sb = sb;
            activeConnPoint = str3;
            i5 = i4;
            it = it;
            j3 = j2;
            fallback = fallback;
        }
    }

    private void connectUsingConfiguration(ConnectionConfiguration connectionConfiguration) throws Throwable {
        connectDirectly(connectionConfiguration.getHost(), connectionConfiguration.getPort());
    }

    @Override // com.xiaomi.smack.Connection
    public void batchSend(Blob[] blobArr) throws XMPPException {
        throw new XMPPException("Don't support send Blob");
    }

    @Override // com.xiaomi.smack.Connection
    public void batchSendPacket(Packet[] packetArr) throws XMPPException {
        for (Packet packet : packetArr) {
            sendPacket(packet);
        }
    }

    @Override // com.xiaomi.smack.Connection
    public synchronized void connect() throws XMPPException {
        try {
            if (!isConnected() && !isConnecting()) {
                setConnectionStatus(0, 0, null);
                connectUsingConfiguration(this.config);
                return;
            }
            MyLog.w("WARNING: current xmpp has connected");
        } catch (IOException e2) {
            throw new XMPPException(e2);
        }
    }

    public Socket createSocket() {
        return new Socket();
    }

    @Override // com.xiaomi.smack.Connection
    public void disconnect(int i2, Exception exc) {
        shutdown(i2, exc);
        if ((exc != null || i2 == 18) && this.lastConnectedTime != 0) {
            sinkdownHost(exc);
        }
    }

    public String getChallenge() {
        return this.challenge;
    }

    public Context getContext() {
        return this.pushService;
    }

    Fallback getFallback(final String str) {
        Fallback fallbacksByHost = HostManager.getInstance().getFallbacksByHost(str, false);
        if (!fallbacksByHost.isEffective()) {
            TaskExecutor.execute(new Runnable() { // from class: com.xiaomi.smack.SocketConnection.3
                @Override // java.lang.Runnable
                public void run() {
                    HostManager.getInstance().getFallbacksByHost(str, true);
                }
            });
        }
        return fallbacksByHost;
    }

    @Override // com.xiaomi.smack.Connection
    public String getHost() {
        return this.connectedHost;
    }

    protected synchronized void initConnection() throws XMPPException, IOException {
    }

    public void notifyConnectionError(final int i2, final Exception exc) {
        Network.refreshNetworkInfo();
        this.pushService.executeJob(new XMPushService.Job(2) { // from class: com.xiaomi.smack.SocketConnection.2
            @Override // com.xiaomi.push.service.XMPushService.Job
            public String getDesc() {
                return "shutdown the connection. " + i2 + ", " + exc;
            }

            @Override // com.xiaomi.push.service.XMPushService.Job
            public void process() {
                SocketConnection.this.pushService.disconnect(i2, exc);
            }
        });
    }

    @Override // com.xiaomi.smack.Connection
    public void ping(boolean z2) throws XMPPException {
        final long jElapsedRealtime = SystemClock.elapsedRealtime();
        final long jCurrentTimeMillis = System.currentTimeMillis();
        sendPing(z2);
        HeartbeatHolder.getInstance(this.pushService).onPing();
        if (z2) {
            return;
        }
        this.pushService.executeJobDelayed(new XMPushService.Job(13) { // from class: com.xiaomi.smack.SocketConnection.1
            @Override // com.xiaomi.push.service.XMPushService.Job
            public String getDesc() {
                return "check the ping-pong." + jCurrentTimeMillis;
            }

            @Override // com.xiaomi.push.service.XMPushService.Job
            public void process() {
                Thread.yield();
                if (!SocketConnection.this.isConnected() || SocketConnection.this.isReadAlive(jElapsedRealtime)) {
                    return;
                }
                HeartbeatHolder.getInstance(SocketConnection.this.pushService).onPingTimeout();
                SocketConnection.this.pushService.disconnect(22, null);
            }
        }, 10000L);
    }

    protected abstract void sendPing(boolean z2) throws XMPPException;

    protected synchronized void shutdown(int i2, Exception exc) {
        if (getConnectionStatus() == 2) {
            return;
        }
        setConnectionStatus(2, i2, exc);
        this.challenge = "";
        try {
            this.socket.close();
        } catch (Throwable unused) {
        }
        this.lastPingSent = 0L;
        this.lastPingReceived = 0L;
    }

    protected void sinkdownHost(Exception exc) {
        if (SystemClock.elapsedRealtime() - this.lastConnectedTime >= Constants.ASSEMBLE_PUSH_NETWORK_INTERVAL) {
            this.curShortConnCount = 0;
            return;
        }
        if (Network.hasNetwork(this.pushService)) {
            int i2 = this.curShortConnCount + 1;
            this.curShortConnCount = i2;
            if (i2 >= 2) {
                String host = getHost();
                MyLog.w("max short conn time reached, sink down current host:" + host);
                sinkdownHost(host, 0L, exc);
                this.curShortConnCount = 0;
            }
        }
    }

    public void updateLastReceived() {
        this.lastPingReceived = SystemClock.elapsedRealtime();
    }

    public void updateLastSent() {
        this.lastPingSent = SystemClock.elapsedRealtime();
    }

    protected void sinkdownHost(String str, long j2, Exception exc) {
        Fallback fallbacksByHost = HostManager.getInstance().getFallbacksByHost(ConnectionConfiguration.getXmppServerHost(), false);
        if (fallbacksByHost != null) {
            fallbacksByHost.failedHost(str, j2, 0L, exc);
            HostManager.getInstance().persist();
        }
    }
}
