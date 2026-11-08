package com.xiaomi.push.service;

import android.text.TextUtils;
import com.google.protobuf.micro.InvalidProtocolBufferMicroException;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.network.Fallback;
import com.xiaomi.network.HostManager;
import com.xiaomi.push.log.LogUploader;
import com.xiaomi.push.protobuf.ChannelMessage;
import com.xiaomi.push.thrift.ChannelStatsType;
import com.xiaomi.report.ConnReportHelper;
import com.xiaomi.slim.Blob;
import com.xiaomi.smack.ConnectionConfiguration;
import com.xiaomi.smack.packet.CommonPacketExtension;
import com.xiaomi.smack.packet.IQ;
import com.xiaomi.smack.packet.Message;
import com.xiaomi.smack.packet.Packet;
import com.xiaomi.smack.util.TrafficUtils;
import java.util.Date;

/* JADX INFO: loaded from: classes.dex */
public class PacketSync {
    private XMPushService mService;

    public interface PacketReceiveHandler {
        boolean Handle(Blob blob);

        boolean Handle(Packet packet);
    }

    PacketSync(XMPushService xMPushService) {
        this.mService = xMPushService;
    }

    private void dispatchNetFlow(Packet packet) {
        PushClientsManager.ClientLoginInfo clientLoginInfoByChidAndUserId;
        String to = packet.getTo();
        String channelId = packet.getChannelId();
        if (TextUtils.isEmpty(to) || TextUtils.isEmpty(channelId) || (clientLoginInfoByChidAndUserId = PushClientsManager.getInstance().getClientLoginInfoByChidAndUserId(channelId, to)) == null) {
            return;
        }
        TrafficUtils.distributionTraffic(this.mService, clientLoginInfoByChidAndUserId.pkgName, TrafficUtils.getTrafficFlow(packet.toXML()), true, true, System.currentTimeMillis());
    }

    private void processRedirectMessage(CommonPacketExtension commonPacketExtension) {
        String text = commonPacketExtension.getText();
        if (TextUtils.isEmpty(text)) {
            return;
        }
        String[] strArrSplit = text.split(";");
        Fallback fallbacksByHost = HostManager.getInstance().getFallbacksByHost(ConnectionConfiguration.getXmppServerHost(), false);
        if (fallbacksByHost == null || strArrSplit.length <= 0) {
            return;
        }
        fallbacksByHost.addPreferredHost(strArrSplit);
        this.mService.disconnect(20, null);
        this.mService.scheduleConnect(true);
    }

    public void handleBlob(Blob blob) throws InvalidProtocolBufferMicroException {
        String cmd = blob.getCmd();
        if (blob.getChannelId() == 0) {
            if (Blob.CMD_PING.equals(cmd)) {
                byte[] payload = blob.getPayload();
                if (payload != null && payload.length > 0) {
                    ChannelMessage.XMMsgPing from = ChannelMessage.XMMsgPing.parseFrom(payload);
                    if (from.hasPsc()) {
                        ServiceConfig.getInstance().handle(from.getPsc());
                    }
                }
                if (!PushConstants.PUSH_SERVICE_PACKAGE_NAME.equals(this.mService.getPackageName())) {
                    this.mService.sendPongIfNeed();
                }
                if ("1".equals(blob.getPacketID())) {
                    MyLog.w("received a server ping");
                } else {
                    ConnReportHelper.pingEnded();
                }
                this.mService.onPong();
                return;
            }
            if (!Blob.CMD_SYNC.equals(cmd)) {
                if (Blob.CMD_NOTIFY.equals(blob.getCmd())) {
                    ChannelMessage.XMMsgNotify from2 = ChannelMessage.XMMsgNotify.parseFrom(blob.getPayload());
                    MyLog.w("notify by server err = " + from2.getErrCode() + " desc = " + from2.getErrStr());
                    return;
                }
                return;
            }
            if (Blob.SUBCMD_CONF.equals(blob.getSubcmd())) {
                ServiceConfig.getInstance().handle(ChannelMessage.PushServiceConfigMsg.parseFrom(blob.getPayload()));
                return;
            }
            if (TextUtils.equals("U", blob.getSubcmd())) {
                ChannelMessage.XMMsgU from3 = ChannelMessage.XMMsgU.parseFrom(blob.getPayload());
                LogUploader.getInstance(this.mService).upload(from3.getUrl(), from3.getToken(), new Date(from3.getStart()), new Date(from3.getEnd()), from3.getMaxlen() * 1024, from3.getForce());
                Blob blob2 = new Blob();
                blob2.setChannelId(0);
                blob2.setCmd(blob.getCmd(), "UCA");
                blob2.setPacketID(blob.getPacketID());
                XMPushService xMPushService = this.mService;
                xMPushService.executeJob(new SendMessageJob(xMPushService, blob2));
                return;
            }
            if (TextUtils.equals("P", blob.getSubcmd())) {
                ChannelMessage.XMMsgP from4 = ChannelMessage.XMMsgP.parseFrom(blob.getPayload());
                Blob blob3 = new Blob();
                blob3.setChannelId(0);
                blob3.setCmd(blob.getCmd(), "PCA");
                blob3.setPacketID(blob.getPacketID());
                ChannelMessage.XMMsgP xMMsgP = new ChannelMessage.XMMsgP();
                if (from4.hasCookie()) {
                    xMMsgP.setCookie(from4.getCookie());
                }
                blob3.setPayload(xMMsgP.toByteArray(), null);
                XMPushService xMPushService2 = this.mService;
                xMPushService2.executeJob(new SendMessageJob(xMPushService2, blob3));
                MyLog.w("ACK msgP: id = " + blob.getPacketID());
                return;
            }
            return;
        }
        String string = Integer.toString(blob.getChannelId());
        if (Blob.CMD_SECMSG.equals(blob.getCmd())) {
            if (!blob.hasErr()) {
                this.mService.getClientEventDispatcher().notifyPacketArrival(this.mService, string, blob);
                return;
            }
            MyLog.w("Recv SECMSG errCode = " + blob.getErrCode() + " errStr = " + blob.getErrStr());
            return;
        }
        if (!Blob.CMD_BIND.equals(cmd)) {
            if (Blob.CMD_KICK.equals(cmd)) {
                ChannelMessage.XMMsgKick from5 = ChannelMessage.XMMsgKick.parseFrom(blob.getPayload());
                String fullUserName = blob.getFullUserName();
                String type = from5.getType();
                String reason = from5.getReason();
                MyLog.w("kicked by server, chid=" + string + " res= " + PushClientsManager.ClientLoginInfo.getResource(fullUserName) + " type=" + type + " reason=" + reason);
                if (!"wait".equals(type)) {
                    this.mService.closeChannel(string, fullUserName, 3, reason, type);
                    PushClientsManager.getInstance().deactivateClient(string, fullUserName);
                    return;
                }
                PushClientsManager.ClientLoginInfo clientLoginInfoByChidAndUserId = PushClientsManager.getInstance().getClientLoginInfoByChidAndUserId(string, fullUserName);
                if (clientLoginInfoByChidAndUserId != null) {
                    this.mService.scheduleRebindChannel(clientLoginInfoByChidAndUserId);
                    clientLoginInfoByChidAndUserId.setStatus(PushClientsManager.ClientStatus.unbind, 3, 0, reason, type);
                    return;
                }
                return;
            }
            return;
        }
        ChannelMessage.XMMsgBindResp from6 = ChannelMessage.XMMsgBindResp.parseFrom(blob.getPayload());
        String fullUserName2 = blob.getFullUserName();
        PushClientsManager.ClientLoginInfo clientLoginInfoByChidAndUserId2 = PushClientsManager.getInstance().getClientLoginInfoByChidAndUserId(string, fullUserName2);
        if (clientLoginInfoByChidAndUserId2 == null) {
            return;
        }
        if (from6.getResult()) {
            MyLog.w("SMACK: channel bind succeeded, chid=" + blob.getChannelId());
            clientLoginInfoByChidAndUserId2.setStatus(PushClientsManager.ClientStatus.binded, 1, 0, null, null);
            return;
        }
        String errorType = from6.getErrorType();
        if ("auth".equals(errorType)) {
            if ("invalid-sig".equals(from6.getErrorReason())) {
                MyLog.w("SMACK: bind error invalid-sig token = " + clientLoginInfoByChidAndUserId2.token + " sec = " + clientLoginInfoByChidAndUserId2.security);
                ConnReportHelper.stats(0, ChannelStatsType.BIND_INVALID_SIG.getValue(), 1, null, 0);
            }
            clientLoginInfoByChidAndUserId2.setStatus(PushClientsManager.ClientStatus.unbind, 1, 5, from6.getErrorReason(), errorType);
            PushClientsManager.getInstance().deactivateClient(string, fullUserName2);
        } else if ("cancel".equals(errorType)) {
            clientLoginInfoByChidAndUserId2.setStatus(PushClientsManager.ClientStatus.unbind, 1, 7, from6.getErrorReason(), errorType);
            PushClientsManager.getInstance().deactivateClient(string, fullUserName2);
        } else if ("wait".equals(errorType)) {
            this.mService.scheduleRebindChannel(clientLoginInfoByChidAndUserId2);
            clientLoginInfoByChidAndUserId2.setStatus(PushClientsManager.ClientStatus.unbind, 1, 7, from6.getErrorReason(), errorType);
        }
        MyLog.w("SMACK: channel bind failed, chid=" + string + " reason=" + from6.getErrorReason());
    }

    public void onBlobReceive(Blob blob) {
        if (5 != blob.getChannelId()) {
            dispatchNetFlow(blob);
        }
        try {
            handleBlob(blob);
        } catch (Exception e2) {
            MyLog.e("handle Blob chid = " + blob.getChannelId() + " cmd = " + blob.getCmd() + " packetid = " + blob.getPacketID() + " failure ", e2);
        }
    }

    public void onPacketReceive(Packet packet) {
        if (!"5".equals(packet.getChannelId())) {
            dispatchNetFlow(packet);
        }
        String channelId = packet.getChannelId();
        if (TextUtils.isEmpty(channelId)) {
            channelId = "1";
            packet.setChannelId("1");
        }
        if (channelId.equals("0")) {
            MyLog.w("Received wrong packet with chid = 0 : " + packet.toXML());
        }
        if (packet instanceof IQ) {
            CommonPacketExtension extension = packet.getExtension("kick");
            if (extension != null) {
                String to = packet.getTo();
                String attributeValue = extension.getAttributeValue("type");
                String attributeValue2 = extension.getAttributeValue("reason");
                MyLog.w("kicked by server, chid=" + channelId + " res=" + PushClientsManager.ClientLoginInfo.getResource(to) + " type=" + attributeValue + " reason=" + attributeValue2);
                if (!"wait".equals(attributeValue)) {
                    this.mService.closeChannel(channelId, to, 3, attributeValue2, attributeValue);
                    PushClientsManager.getInstance().deactivateClient(channelId, to);
                    return;
                }
                PushClientsManager.ClientLoginInfo clientLoginInfoByChidAndUserId = PushClientsManager.getInstance().getClientLoginInfoByChidAndUserId(channelId, to);
                if (clientLoginInfoByChidAndUserId != null) {
                    this.mService.scheduleRebindChannel(clientLoginInfoByChidAndUserId);
                    clientLoginInfoByChidAndUserId.setStatus(PushClientsManager.ClientStatus.unbind, 3, 0, attributeValue2, attributeValue);
                    return;
                }
                return;
            }
        } else if (packet instanceof Message) {
            Message message = (Message) packet;
            if ("redir".equals(message.getType())) {
                CommonPacketExtension extension2 = message.getExtension("hosts");
                if (extension2 != null) {
                    processRedirectMessage(extension2);
                    return;
                }
                return;
            }
        }
        this.mService.getClientEventDispatcher().notifyPacketArrival(this.mService, channelId, packet);
    }

    private void dispatchNetFlow(Blob blob) {
        PushClientsManager.ClientLoginInfo clientLoginInfoByChidAndUserId;
        String fullUserName = blob.getFullUserName();
        String string = Integer.toString(blob.getChannelId());
        if (TextUtils.isEmpty(fullUserName) || TextUtils.isEmpty(string) || (clientLoginInfoByChidAndUserId = PushClientsManager.getInstance().getClientLoginInfoByChidAndUserId(string, fullUserName)) == null) {
            return;
        }
        TrafficUtils.distributionTraffic(this.mService, clientLoginInfoByChidAndUserId.pkgName, blob.getSerializedSize(), true, true, System.currentTimeMillis());
    }
}
