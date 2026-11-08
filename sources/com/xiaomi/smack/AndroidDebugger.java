package com.xiaomi.smack;

import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.push.mpcd.Constants;
import com.xiaomi.push.protobuf.ChannelMessage;
import com.xiaomi.push.service.ChannelConstants;
import com.xiaomi.push.service.PacketHelper;
import com.xiaomi.push.service.PushClientsManager;
import com.xiaomi.slim.Blob;
import com.xiaomi.smack.debugger.SmackDebugger;
import com.xiaomi.smack.filter.PacketFilter;
import com.xiaomi.smack.packet.Packet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class AndroidDebugger implements SmackDebugger {
    public static boolean printInterpreted = false;
    private Connection connection;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("hh:mm:ss aaa");
    private Listener readListener = null;
    private Listener writeListener = null;
    private ConnectionListener connListener = null;
    private final String TAG = "[Slim] ";

    class Listener implements PacketListener, PacketFilter {
        private boolean isReadListener;
        String rcvOrSent;

        Listener(boolean z2) {
            this.isReadListener = z2;
            this.rcvOrSent = z2 ? " RCV " : " Sent ";
        }

        @Override // com.xiaomi.smack.filter.PacketFilter
        public boolean accept(Packet packet) {
            return true;
        }

        @Override // com.xiaomi.smack.PacketListener
        public void process(Blob blob) {
            if (AndroidDebugger.printInterpreted) {
                MyLog.v("[Slim] " + AndroidDebugger.this.dateFormatter.format(new Date()) + this.rcvOrSent + blob.toString());
            } else {
                MyLog.v("[Slim] " + AndroidDebugger.this.dateFormatter.format(new Date()) + this.rcvOrSent + " Blob [" + blob.getCmd() + "," + blob.getChannelId() + "," + PacketHelper.filterRegistrationPacketIdForLog(blob.getPacketID()) + "]");
            }
            if (blob == null || blob.getChannelId() != 99999) {
                return;
            }
            String cmd = blob.getCmd();
            Blob blob2 = null;
            if (!this.isReadListener) {
                if (Blob.CMD_BIND.equals(cmd)) {
                    MyLog.w("build binded result for loopback.");
                    ChannelMessage.XMMsgBindResp xMMsgBindResp = new ChannelMessage.XMMsgBindResp();
                    xMMsgBindResp.setResult(true);
                    xMMsgBindResp.setErrorDesc("login success.");
                    xMMsgBindResp.setErrorReason("success");
                    xMMsgBindResp.setErrorType("success");
                    Blob blob3 = new Blob();
                    blob3.setPayload(xMMsgBindResp.toByteArray(), null);
                    blob3.setPayloadType((short) 2);
                    blob3.setChannelId(ChannelConstants.CHID_DEBUG);
                    blob3.setCmd(Blob.CMD_BIND, null);
                    blob3.setPacketID(blob.getPacketID());
                    blob3.setPackageName(null);
                    blob3.setFrom(blob.getFullUserName());
                    blob2 = blob3;
                } else if (!Blob.CMD_UNBIND.equals(cmd) && Blob.CMD_SECMSG.equals(cmd)) {
                    Blob blob4 = new Blob();
                    blob4.setChannelId(ChannelConstants.CHID_DEBUG);
                    blob4.setCmd(Blob.CMD_SECMSG, null);
                    blob4.setFrom(blob.getFullUserName());
                    blob4.setPacketID(blob.getPacketID());
                    blob4.setPayloadType(blob.getPayloadType());
                    blob4.setPackageName(blob.getPackageName());
                    blob4.setPayload(blob.getDecryptedPayload(PushClientsManager.getInstance().getClientLoginInfoByChidAndUserId(String.valueOf(ChannelConstants.CHID_DEBUG), blob.getFullUserName()).security), null);
                    blob2 = blob4;
                }
            }
            if (blob2 != null) {
                for (Map.Entry<PacketListener, Connection.ListenerWrapper> entry : AndroidDebugger.this.connection.getPacketListeners().entrySet()) {
                    if (AndroidDebugger.this.readListener != entry.getKey()) {
                        entry.getValue().notifyListener(blob2);
                    }
                }
            }
        }

        @Override // com.xiaomi.smack.PacketListener
        public void processPacket(Packet packet) {
            if (AndroidDebugger.printInterpreted) {
                MyLog.v("[Slim] " + AndroidDebugger.this.dateFormatter.format(new Date()) + this.rcvOrSent + " PKT " + packet.toXML());
                return;
            }
            MyLog.v("[Slim] " + AndroidDebugger.this.dateFormatter.format(new Date()) + this.rcvOrSent + " PKT [" + packet.getChannelId() + "," + packet.getPacketID() + "]");
        }
    }

    public AndroidDebugger(Connection connection) {
        this.connection = connection;
        createDebug();
    }

    private void createDebug() {
        this.readListener = new Listener(true);
        this.writeListener = new Listener(false);
        Connection connection = this.connection;
        Listener listener = this.readListener;
        connection.addPacketListener(listener, listener);
        Connection connection2 = this.connection;
        Listener listener2 = this.writeListener;
        connection2.addPacketSendingListener(listener2, listener2);
        this.connListener = new ConnectionListener() { // from class: com.xiaomi.smack.AndroidDebugger.1
            @Override // com.xiaomi.smack.ConnectionListener
            public void connectionClosed(Connection connection3, int i2, Exception exc) {
                MyLog.v("[Slim] " + AndroidDebugger.this.dateFormatter.format(new Date()) + " Connection closed (" + AndroidDebugger.this.connection.hashCode() + Constants.SEPARATOR_RIGHT_PARENTESIS);
            }

            @Override // com.xiaomi.smack.ConnectionListener
            public void connectionStarted(Connection connection3) {
                MyLog.v("[Slim] " + AndroidDebugger.this.dateFormatter.format(new Date()) + " Connection started (" + AndroidDebugger.this.connection.hashCode() + Constants.SEPARATOR_RIGHT_PARENTESIS);
            }

            @Override // com.xiaomi.smack.ConnectionListener
            public void reconnectionFailed(Connection connection3, Exception exc) {
                MyLog.v("[Slim] " + AndroidDebugger.this.dateFormatter.format(new Date()) + " Reconnection failed due to an exception (" + AndroidDebugger.this.connection.hashCode() + Constants.SEPARATOR_RIGHT_PARENTESIS);
                exc.printStackTrace();
            }

            @Override // com.xiaomi.smack.ConnectionListener
            public void reconnectionSuccessful(Connection connection3) {
                MyLog.v("[Slim] " + AndroidDebugger.this.dateFormatter.format(new Date()) + " Connection reconnected (" + AndroidDebugger.this.connection.hashCode() + Constants.SEPARATOR_RIGHT_PARENTESIS);
            }
        };
    }

    @Override // com.xiaomi.smack.debugger.SmackDebugger
    public PacketListener getReaderListener() {
        return this.readListener;
    }

    @Override // com.xiaomi.smack.debugger.SmackDebugger
    public PacketListener getWriterListener() {
        return this.writeListener;
    }
}
