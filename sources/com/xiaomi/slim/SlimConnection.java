package com.xiaomi.slim;

import android.os.SystemClock;
import android.text.TextUtils;
import com.google.protobuf.micro.ByteStringMicro;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.push.mpcd.Constants;
import com.xiaomi.push.protobuf.ChannelMessage;
import com.xiaomi.push.service.ChannelConstants;
import com.xiaomi.push.service.PushClientsManager;
import com.xiaomi.push.service.RC4Cryption;
import com.xiaomi.push.service.SendMessageJob;
import com.xiaomi.push.service.ServiceConfig;
import com.xiaomi.push.service.XMPushService;
import com.xiaomi.report.ConnReportHelper;
import com.xiaomi.smack.Connection;
import com.xiaomi.smack.ConnectionConfiguration;
import com.xiaomi.smack.SocketConnection;
import com.xiaomi.smack.XMPPException;
import com.xiaomi.smack.packet.Packet;
import com.xiaomi.smack.util.TrafficUtils;
import java.io.IOException;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class SlimConnection extends SocketConnection {
    private byte[] mKey;
    private BlobReader mReader;
    private Thread mReaderThread;
    private BlobWriter mWriter;

    public SlimConnection(XMPushService xMPushService, ConnectionConfiguration connectionConfiguration) {
        super(xMPushService, connectionConfiguration);
    }

    private Blob getPing(boolean z2) {
        Ping ping = new Ping();
        if (z2) {
            ping.setPacketID("1");
        }
        byte[] bArrRetriveStatsAsByte = ConnReportHelper.retriveStatsAsByte();
        if (bArrRetriveStatsAsByte != null) {
            ChannelMessage.XMMsgPing xMMsgPing = new ChannelMessage.XMMsgPing();
            xMMsgPing.setStats(ByteStringMicro.copyFrom(bArrRetriveStatsAsByte));
            ping.setPayload(xMMsgPing.toByteArray(), null);
        }
        return ping;
    }

    private void initReaderAndWriter() throws XMPPException {
        try {
            this.mReader = new BlobReader(this.socket.getInputStream(), this);
            this.mWriter = new BlobWriter(this.socket.getOutputStream(), this);
            Thread thread = new Thread("Blob Reader (" + this.connectionCounterValue + Constants.SEPARATOR_RIGHT_PARENTESIS) { // from class: com.xiaomi.slim.SlimConnection.1
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    try {
                        SlimConnection.this.mReader.start();
                    } catch (Exception e2) {
                        SlimConnection.this.notifyConnectionError(9, e2);
                    }
                }
            };
            this.mReaderThread = thread;
            thread.start();
        } catch (Exception e2) {
            throw new XMPPException("Error to init reader and writer", e2);
        }
    }

    @Override // com.xiaomi.smack.SocketConnection, com.xiaomi.smack.Connection
    public void batchSend(Blob[] blobArr) throws XMPPException {
        for (Blob blob : blobArr) {
            send(blob);
        }
    }

    @Override // com.xiaomi.smack.SocketConnection, com.xiaomi.smack.Connection
    @Deprecated
    public void batchSendPacket(Packet[] packetArr) throws XMPPException {
        for (Packet packet : packetArr) {
            sendPacket(packet);
        }
    }

    @Override // com.xiaomi.smack.Connection
    public synchronized void bind(PushClientsManager.ClientLoginInfo clientLoginInfo) throws XMPPException {
        Binder.bind(clientLoginInfo, getChallenge(), this);
    }

    synchronized byte[] getKey() {
        try {
            if (this.mKey == null && !TextUtils.isEmpty(this.challenge)) {
                String deviceUUID = ServiceConfig.getDeviceUUID();
                StringBuilder sb = new StringBuilder();
                String str = this.challenge;
                sb.append(str.substring(str.length() / 2));
                sb.append(deviceUUID.substring(deviceUUID.length() / 2));
                this.mKey = RC4Cryption.encrypt(this.challenge.getBytes(), sb.toString().getBytes());
            }
        } catch (Throwable th) {
            throw th;
        }
        return this.mKey;
    }

    @Override // com.xiaomi.smack.SocketConnection
    protected synchronized void initConnection() throws XMPPException, IOException {
        initReaderAndWriter();
        this.mWriter.openStream();
    }

    @Override // com.xiaomi.smack.Connection
    public boolean isBinaryConnection() {
        return true;
    }

    void notifyDataArrived(Packet packet) {
        if (packet == null) {
            return;
        }
        Iterator<Connection.ListenerWrapper> it = this.recvListeners.values().iterator();
        while (it.hasNext()) {
            it.next().notifyListener(packet);
        }
    }

    @Override // com.xiaomi.smack.Connection
    public void send(Blob blob) throws XMPPException {
        BlobWriter blobWriter = this.mWriter;
        if (blobWriter == null) {
            throw new XMPPException("the writer is null.");
        }
        try {
            int iWrite = blobWriter.write(blob);
            this.writeAlive = SystemClock.elapsedRealtime();
            String packageName = blob.getPackageName();
            if (!TextUtils.isEmpty(packageName)) {
                TrafficUtils.distributionTraffic(this.mPushService, packageName, iWrite, false, true, System.currentTimeMillis());
            }
            Iterator<Connection.ListenerWrapper> it = this.sendListeners.values().iterator();
            while (it.hasNext()) {
                it.next().notifyListener(blob);
            }
        } catch (Exception e2) {
            throw new XMPPException(e2);
        }
    }

    @Override // com.xiaomi.smack.Connection
    @Deprecated
    public void sendPacket(Packet packet) throws XMPPException {
        send(Blob.from(packet, null));
    }

    @Override // com.xiaomi.smack.SocketConnection
    protected void sendPing(boolean z2) throws XMPPException {
        if (this.mWriter == null) {
            throw new XMPPException("The BlobWriter is null.");
        }
        Blob ping = getPing(z2);
        MyLog.w("[Slim] SND ping id=" + ping.getPacketID());
        send(ping);
        updateLastSent();
    }

    @Override // com.xiaomi.smack.SocketConnection
    protected synchronized void shutdown(int i2, Exception exc) {
        try {
            BlobReader blobReader = this.mReader;
            if (blobReader != null) {
                blobReader.shutdown();
                this.mReader = null;
            }
            BlobWriter blobWriter = this.mWriter;
            if (blobWriter != null) {
                try {
                    blobWriter.shutdown();
                } catch (Exception e2) {
                    MyLog.e("SlimConnection shutdown cause exception: " + e2);
                }
                this.mWriter = null;
            }
            this.mKey = null;
            super.shutdown(i2, exc);
        } catch (Throwable th) {
            throw th;
        }
    }

    @Override // com.xiaomi.smack.Connection
    public synchronized void unbind(String str, String str2) throws XMPPException {
        Binder.unbind(str, str2, this);
    }

    void notifyDataArrived(Blob blob) {
        if (blob == null) {
            return;
        }
        if (ChannelConstants.isIOTDCMStatTime(blob)) {
            Blob blob2 = new Blob();
            blob2.setChannelId(blob.getChannelId());
            blob2.setCmd(Blob.CMD_SYNC, "ACK_RTT");
            blob2.setPacketID(blob.getPacketID());
            blob2.setTimestamp(blob.getTimestamp());
            blob2.setUUID(blob.getUUID());
            XMPushService xMPushService = this.mPushService;
            xMPushService.executeJob(new SendMessageJob(xMPushService, blob2));
        }
        if (blob.hasErr()) {
            MyLog.w("[Slim] RCV blob chid=" + blob.getChannelId() + "; id=" + blob.getPacketID() + "; errCode=" + blob.getErrCode() + "; err=" + blob.getErrStr());
        }
        if (blob.getChannelId() == 0) {
            if (Blob.CMD_PING.equals(blob.getCmd())) {
                MyLog.w("[Slim] RCV ping id=" + blob.getPacketID());
                updateLastReceived();
            } else if (Blob.CMD_CLOSE.equals(blob.getCmd())) {
                notifyConnectionError(13, null);
            }
        }
        Iterator<Connection.ListenerWrapper> it = this.recvListeners.values().iterator();
        while (it.hasNext()) {
            it.next().notifyListener(blob);
        }
    }
}
