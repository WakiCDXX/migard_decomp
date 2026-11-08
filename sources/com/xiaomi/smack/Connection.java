package com.xiaomi.smack;

import android.os.SystemClock;
import android.util.Pair;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.network.Network;
import com.xiaomi.channel.commonutils.string.MD5;
import com.xiaomi.push.service.PushClientsManager;
import com.xiaomi.push.service.PushConstants;
import com.xiaomi.push.service.XMPushService;
import com.xiaomi.slim.Blob;
import com.xiaomi.smack.debugger.SmackDebugger;
import com.xiaomi.smack.filter.PacketFilter;
import com.xiaomi.smack.packet.Packet;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import miui.yellowpage.YellowPageContract;

/* JADX INFO: loaded from: classes.dex */
public abstract class Connection {
    public static final boolean DEBUG_CHANNEL_LOOKBACK = false;
    public static boolean DEBUG_ENABLED = false;
    private static final long EFFECTIVE_STATUS = 1800000;
    public static final int ERR_BOSH = 499;
    public static final int ERR_TCP_BROKEN_PIPE = 110;
    public static final int ERR_TCP_CONNREFUSED = 103;
    public static final int ERR_TCP_CONNRESET = 109;
    public static final int ERR_TCP_INVALARG = 106;
    public static final int ERR_TCP_NETUNREACH = 102;
    public static final int ERR_TCP_NOACCESS = 101;
    public static final int ERR_TCP_NOROUTETOHOST = 104;
    public static final int ERR_TCP_OTHER = 199;
    public static final int ERR_TCP_READ_TIMEOUT = 108;
    public static final int ERR_TCP_TIMEOUT = 105;
    public static final int ERR_TCP_UKNOWNHOST = 107;
    public static final int ERR_UNKNOWN = 0;
    public static final int ERR_XMPP = 399;
    private static final int MAX_STATUS_CNT = 6;
    public static final long PING_TIMEOUT = 10000;
    private static final AtomicInteger connectionCounter = new AtomicInteger(0);
    protected ConnectionConfiguration config;
    protected XMPushService mPushService;
    protected int connTimes = 0;
    protected long connectTime = -1;
    protected volatile long lastPingSent = 0;
    protected volatile long lastPingReceived = 0;
    private LinkedList<Pair<Integer, Long>> mCachedStatus = new LinkedList<>();
    private final Collection<ConnectionListener> connectionListeners = new CopyOnWriteArrayList();
    protected final Map<PacketListener, ListenerWrapper> recvListeners = new ConcurrentHashMap();
    protected final Map<PacketListener, ListenerWrapper> sendListeners = new ConcurrentHashMap();
    protected SmackDebugger debugger = null;
    protected String challenge = "";
    protected String connectionPoint = "";
    private int connectStatus = 2;
    protected final int connectionCounterValue = connectionCounter.getAndIncrement();
    private long readAlive = 0;
    protected long writeAlive = 0;

    static {
        DEBUG_ENABLED = false;
        try {
            DEBUG_ENABLED = Boolean.getBoolean("smack.debugEnabled");
        } catch (Exception unused) {
        }
        SmackConfiguration.getVersion();
    }

    protected Connection(XMPushService xMPushService, ConnectionConfiguration connectionConfiguration) {
        this.config = connectionConfiguration;
        this.mPushService = xMPushService;
        initDebugger();
    }

    private void addStatus(int i2) {
        synchronized (this.mCachedStatus) {
            try {
                if (i2 == 1) {
                    this.mCachedStatus.clear();
                } else {
                    this.mCachedStatus.add(new Pair<>(Integer.valueOf(i2), Long.valueOf(System.currentTimeMillis())));
                    if (this.mCachedStatus.size() > 6) {
                        this.mCachedStatus.remove(0);
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private String getDesc(int i2) {
        if (i2 == 1) {
            return YellowPageContract.MipubPhoneEvent.EXTRA_DATA_CONNECTED;
        }
        if (i2 == 0) {
            return "connecting";
        }
        return i2 == 2 ? "disconnected" : "unknown";
    }

    public void addConnectionListener(ConnectionListener connectionListener) {
        if (connectionListener == null || this.connectionListeners.contains(connectionListener)) {
            return;
        }
        this.connectionListeners.add(connectionListener);
    }

    public void addPacketListener(PacketListener packetListener, PacketFilter packetFilter) {
        if (packetListener == null) {
            throw new NullPointerException("Packet listener is null.");
        }
        this.recvListeners.put(packetListener, new ListenerWrapper(packetListener, packetFilter));
    }

    public void addPacketSendingListener(PacketListener packetListener, PacketFilter packetFilter) {
        if (packetListener == null) {
            throw new NullPointerException("Packet listener is null.");
        }
        this.sendListeners.put(packetListener, new ListenerWrapper(packetListener, packetFilter));
    }

    public abstract void batchSend(Blob[] blobArr) throws XMPPException;

    public abstract void batchSendPacket(Packet[] packetArr) throws XMPPException;

    public abstract void bind(PushClientsManager.ClientLoginInfo clientLoginInfo) throws XMPPException;

    public void clearCachedStatus() {
        synchronized (this.mCachedStatus) {
            this.mCachedStatus.clear();
        }
    }

    public abstract void connect() throws XMPPException;

    public void disconnect() {
        disconnect(0, null);
    }

    public abstract void disconnect(int i2, Exception exc);

    protected void firePacketSendingListeners(Packet packet) {
        Iterator<ListenerWrapper> it = this.sendListeners.values().iterator();
        while (it.hasNext()) {
            it.next().notifyListener(packet);
        }
    }

    public ConnectionConfiguration getConfiguration() {
        return this.config;
    }

    public int getConnTryTimes() {
        return this.connTimes;
    }

    public long getConnectTime() {
        return this.connectTime;
    }

    public Collection<ConnectionListener> getConnectionListeners() {
        return this.connectionListeners;
    }

    public String getConnectionPoint() {
        return this.config.getConnectionPoint();
    }

    public int getConnectionStatus() {
        return this.connectStatus;
    }

    public String getCurrentNetwork() {
        return this.connectionPoint;
    }

    public String getHost() {
        return this.config.getHost();
    }

    public long getLastPingRecv() {
        return this.lastPingReceived;
    }

    protected Map<PacketListener, ListenerWrapper> getPacketListeners() {
        return this.recvListeners;
    }

    protected Map<PacketListener, ListenerWrapper> getPacketSendingListeners() {
        return this.sendListeners;
    }

    public int getPort() {
        return this.config.getPort();
    }

    public String getServiceName() {
        return this.config.getServiceName();
    }

    protected void initDebugger() {
        String property;
        if (this.config.isDebuggerEnabled() && this.debugger == null) {
            Class<?> cls = null;
            try {
                property = System.getProperty("smack.debuggerClass");
            } catch (Throwable unused) {
                property = null;
            }
            if (property != null) {
                try {
                    cls = Class.forName(property);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            if (cls == null) {
                this.debugger = new AndroidDebugger(this);
                return;
            }
            try {
                this.debugger = (SmackDebugger) cls.getConstructor(Connection.class, Writer.class, Reader.class).newInstance(this);
            } catch (Exception e3) {
                throw new IllegalArgumentException("Can't initialize the configured debugger!", e3);
            }
        }
    }

    public boolean isAlwaysFailed() {
        boolean z2;
        synchronized (this.mCachedStatus) {
            try {
                ArrayList arrayList = new ArrayList();
                for (Pair<Integer, Long> pair : this.mCachedStatus) {
                    if (System.currentTimeMillis() - ((Long) pair.second).longValue() < 1800000) {
                        arrayList.add(pair);
                    }
                }
                this.mCachedStatus.clear();
                this.mCachedStatus.addAll(arrayList);
                z2 = this.mCachedStatus.size() >= 6;
            } catch (Throwable th) {
                throw th;
            }
        }
        return z2;
    }

    public boolean isBinaryConnection() {
        return false;
    }

    public boolean isConnected() {
        return this.connectStatus == 1;
    }

    public boolean isConnecting() {
        return this.connectStatus == 0;
    }

    public synchronized boolean isReadAlive(long j2) {
        return this.readAlive >= j2;
    }

    protected boolean isReconnectionAllowed() {
        return this.config.isReconnectionAllowed();
    }

    public abstract void ping(boolean z2) throws XMPPException;

    public void removeConnectionListener(ConnectionListener connectionListener) {
        this.connectionListeners.remove(connectionListener);
    }

    public void removePacketListener(PacketListener packetListener) {
        this.recvListeners.remove(packetListener);
    }

    public void removePacketSendingListener(PacketListener packetListener) {
        this.sendListeners.remove(packetListener);
    }

    public void resetConnTryTimes() {
        this.connTimes = 0;
    }

    public void resetConnectTime() {
        this.connectTime = -1L;
    }

    public abstract void send(Blob blob) throws XMPPException;

    public abstract void sendPacket(Packet packet) throws XMPPException;

    public synchronized void setChallenge(String str) {
        try {
            if (this.connectStatus == 0) {
                MyLog.w("setChallenge hash = " + MD5.MD5_32(str).substring(0, 8));
                this.challenge = str;
                setConnectionStatus(1, 0, null);
            } else {
                MyLog.w("ignore setChallenge because connection was disconnected");
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    public void setConnectionStatus(int i2, int i3, Exception exc) {
        int i4 = this.connectStatus;
        if (i2 != i4) {
            MyLog.w(String.format("update the connection status. %1$s -> %2$s : %3$s ", getDesc(i4), getDesc(i2), PushConstants.getErrorDesc(i3)));
        }
        if (Network.hasNetwork(this.mPushService)) {
            addStatus(i2);
        }
        if (i2 == 1) {
            this.mPushService.removeJobs(10);
            if (this.connectStatus != 0) {
                MyLog.w("try set connected while not connecting.");
            }
            this.connectStatus = i2;
            Iterator<ConnectionListener> it = this.connectionListeners.iterator();
            while (it.hasNext()) {
                it.next().reconnectionSuccessful(this);
            }
            return;
        }
        if (i2 == 0) {
            if (this.connectStatus != 2) {
                MyLog.w("try set connecting while not disconnected.");
            }
            this.connectStatus = i2;
            Iterator<ConnectionListener> it2 = this.connectionListeners.iterator();
            while (it2.hasNext()) {
                it2.next().connectionStarted(this);
            }
            return;
        }
        if (i2 == 2) {
            this.mPushService.removeJobs(10);
            int i5 = this.connectStatus;
            if (i5 == 0) {
                Iterator<ConnectionListener> it3 = this.connectionListeners.iterator();
                while (it3.hasNext()) {
                    it3.next().reconnectionFailed(this, exc == null ? new CancellationException("disconnect while connecting") : exc);
                }
            } else if (i5 == 1) {
                Iterator<ConnectionListener> it4 = this.connectionListeners.iterator();
                while (it4.hasNext()) {
                    it4.next().connectionClosed(this, i3, exc);
                }
            }
            this.connectStatus = i2;
        }
    }

    public synchronized void setReadAlive() {
        this.readAlive = SystemClock.elapsedRealtime();
    }

    public abstract void unbind(String str, String str2) throws XMPPException;

    public static class ListenerWrapper {
        private PacketFilter packetFilter;
        private PacketListener packetListener;

        public ListenerWrapper(PacketListener packetListener, PacketFilter packetFilter) {
            this.packetListener = packetListener;
            this.packetFilter = packetFilter;
        }

        public void notifyListener(Packet packet) {
            PacketFilter packetFilter = this.packetFilter;
            if (packetFilter == null || packetFilter.accept(packet)) {
                this.packetListener.processPacket(packet);
            }
        }

        public void notifyListener(Blob blob) {
            this.packetListener.process(blob);
        }
    }
}
