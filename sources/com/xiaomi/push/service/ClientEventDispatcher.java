package com.xiaomi.push.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.slim.Blob;
import com.xiaomi.smack.packet.IQ;
import com.xiaomi.smack.packet.Packet;
import com.xiaomi.smack.packet.Presence;
import java.util.Collection;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class ClientEventDispatcher {
    private MIPushEventProcessor mPushEventProcessor = new MIPushEventProcessor();

    public static String getReceiverPermission(PushClientsManager.ClientLoginInfo clientLoginInfo) {
        if ("9".equals(clientLoginInfo.chid)) {
            return clientLoginInfo.pkgName + ".permission.MIMC_RECEIVE";
        }
        return clientLoginInfo.pkgName + ".permission.MIPUSH_RECEIVE";
    }

    private static void sendBroadcast(Context context, Intent intent, PushClientsManager.ClientLoginInfo clientLoginInfo) {
        if (PushConstants.PUSH_SERVICE_PACKAGE_NAME.equals(context.getPackageName())) {
            context.sendBroadcast(intent);
        } else {
            context.sendBroadcast(intent, getReceiverPermission(clientLoginInfo));
        }
    }

    PushClientsManager.ClientLoginInfo getClientLoginInfo(Packet packet) {
        Collection<PushClientsManager.ClientLoginInfo> allClientLoginInfoByChid = PushClientsManager.getInstance().getAllClientLoginInfoByChid(packet.getChannelId());
        if (allClientLoginInfoByChid.isEmpty()) {
            return null;
        }
        Iterator<PushClientsManager.ClientLoginInfo> it = allClientLoginInfoByChid.iterator();
        if (allClientLoginInfoByChid.size() == 1) {
            return it.next();
        }
        String from = packet.getFrom();
        String to = packet.getTo();
        while (it.hasNext()) {
            PushClientsManager.ClientLoginInfo next = it.next();
            if (TextUtils.equals(from, next.userId) || TextUtils.equals(to, next.userId)) {
                return next;
            }
        }
        return null;
    }

    @SuppressLint({"DefaultLocale"})
    public void notifyChannelClosed(Context context, PushClientsManager.ClientLoginInfo clientLoginInfo, int i2) {
        if ("5".equalsIgnoreCase(clientLoginInfo.chid)) {
            return;
        }
        Intent intent = new Intent();
        intent.setAction(PushConstants.ACTION_CHANNEL_CLOSED);
        intent.setPackage(clientLoginInfo.pkgName);
        intent.putExtra(PushConstants.EXTRA_CHANNEL_ID, clientLoginInfo.chid);
        intent.putExtra(PushConstants.EXTRA_REASON, i2);
        intent.putExtra(PushConstants.EXTRA_USER_ID, clientLoginInfo.userId);
        intent.putExtra(PushConstants.EXTRA_SESSION, clientLoginInfo.session);
        if (clientLoginInfo.peer == null || !"9".equals(clientLoginInfo.chid)) {
            MyLog.w(String.format("[Bcst] notify channel closed. %s,%s,%d", clientLoginInfo.chid, clientLoginInfo.pkgName, Integer.valueOf(i2)));
            sendBroadcast(context, intent, clientLoginInfo);
            return;
        }
        try {
            clientLoginInfo.peer.send(Message.obtain(null, 17, intent));
        } catch (RemoteException unused) {
            clientLoginInfo.peer = null;
            StringBuilder sb = new StringBuilder();
            sb.append("peer may died: ");
            String str = clientLoginInfo.userId;
            sb.append(str.substring(str.lastIndexOf(64)));
            MyLog.w(sb.toString());
        }
    }

    @SuppressLint({"DefaultLocale"})
    public void notifyChannelOpenResult(Context context, PushClientsManager.ClientLoginInfo clientLoginInfo, boolean z2, int i2, String str) {
        if ("5".equalsIgnoreCase(clientLoginInfo.chid)) {
            this.mPushEventProcessor.processChannelOpenResult(context, clientLoginInfo, z2, i2, str);
            return;
        }
        Intent intent = new Intent();
        intent.setAction(PushConstants.ACTION_CHANNEL_OPENED);
        intent.setPackage(clientLoginInfo.pkgName);
        intent.putExtra(PushConstants.EXTRA_SUCCEEDED, z2);
        if (!z2) {
            intent.putExtra(PushConstants.EXTRA_REASON, i2);
        }
        if (!TextUtils.isEmpty(str)) {
            intent.putExtra(PushConstants.EXTRA_REASON_MSG, str);
        }
        intent.putExtra(PushConstants.EXTRA_CHID, clientLoginInfo.chid);
        intent.putExtra(PushConstants.EXTRA_USER_ID, clientLoginInfo.userId);
        intent.putExtra(PushConstants.EXTRA_SESSION, clientLoginInfo.session);
        MyLog.w(String.format("[Bcst] notify channel open result. %s,%s,%b,%d", clientLoginInfo.chid, clientLoginInfo.pkgName, Boolean.valueOf(z2), Integer.valueOf(i2)));
        sendBroadcast(context, intent, clientLoginInfo);
    }

    public void notifyKickedByServer(Context context, PushClientsManager.ClientLoginInfo clientLoginInfo, String str, String str2) {
        if (clientLoginInfo == null) {
            MyLog.e("error while notify kick by server!");
            return;
        }
        if ("5".equalsIgnoreCase(clientLoginInfo.chid)) {
            MyLog.e("mipush kicked by server");
            return;
        }
        Intent intent = new Intent();
        intent.setAction(PushConstants.ACTION_KICKED_BY_SERVER);
        intent.setPackage(clientLoginInfo.pkgName);
        intent.putExtra(PushConstants.EXTRA_KICK_TYPE, str);
        intent.putExtra(PushConstants.EXTRA_KICK_REASON, str2);
        intent.putExtra(PushConstants.EXTRA_CHID, clientLoginInfo.chid);
        intent.putExtra(PushConstants.EXTRA_USER_ID, clientLoginInfo.userId);
        intent.putExtra(PushConstants.EXTRA_SESSION, clientLoginInfo.session);
        MyLog.w(String.format("[Bcst] notify packet(blob) arrival. %s,%s,%s", clientLoginInfo.chid, clientLoginInfo.pkgName, str2));
        sendBroadcast(context, intent, clientLoginInfo);
    }

    public void notifyPacketArrival(XMPushService xMPushService, String str, Packet packet) {
        String str2;
        PushClientsManager.ClientLoginInfo clientLoginInfo = getClientLoginInfo(packet);
        if (clientLoginInfo == null) {
            MyLog.e("error while notify channel closed! channel " + str + " not registered");
            return;
        }
        if ("5".equalsIgnoreCase(str)) {
            this.mPushEventProcessor.processNewPacket(xMPushService, packet, clientLoginInfo);
            return;
        }
        String str3 = clientLoginInfo.pkgName;
        if (packet instanceof com.xiaomi.smack.packet.Message) {
            str2 = PushConstants.ACTION_RECEIVE_NEW_MESSAGE;
        } else if (packet instanceof IQ) {
            str2 = PushConstants.ACTION_RECEIVE_NEW_IQ;
        } else {
            if (!(packet instanceof Presence)) {
                MyLog.e("unknown packet type, drop it");
                return;
            }
            str2 = PushConstants.ACTION_RECEIVE_NEW_PRESENCE;
        }
        Intent intent = new Intent();
        intent.setAction(str2);
        intent.setPackage(str3);
        intent.putExtra(PushConstants.EXTRA_CHID, str);
        intent.putExtra(PushConstants.EXTRA_PACKET, packet.toBundle());
        intent.putExtra(PushConstants.EXTRA_SESSION, clientLoginInfo.session);
        intent.putExtra(PushConstants.EXTRA_SECURITY, clientLoginInfo.security);
        MyLog.w(String.format("[Bcst] notify packet arrival. %s,%s,%s", clientLoginInfo.chid, clientLoginInfo.pkgName, packet.getPacketID()));
        if ("3".equalsIgnoreCase(str)) {
            intent.putExtra(PushConstants.EXTRA_RECEIVE_TIME, packet.receiveTime);
            intent.putExtra(PushConstants.EXTRA_BROADCAST_TIME, System.currentTimeMillis());
        }
        sendBroadcast(xMPushService, intent, clientLoginInfo);
    }

    @SuppressLint({"WrongConstant"})
    public void notifyServiceStarted(Context context) {
        if (PushConstants.PUSH_SERVICE_PACKAGE_NAME.equals(context.getPackageName())) {
            Intent intent = new Intent();
            intent.setAction(PushConstants.ACTION_SERVICE_STARTED);
            if (MIUIUtils.isXMS()) {
                intent.addFlags(16777216);
            }
            MyLog.w("[Bcst] send ***.push.service_started broadcast to inform push service has started.");
            intent.setPackage(PushConstants.MMS_PACKAGE_NAME);
            context.sendBroadcast(intent);
        }
    }

    PushClientsManager.ClientLoginInfo getClientLoginInfo(Blob blob) {
        Collection<PushClientsManager.ClientLoginInfo> allClientLoginInfoByChid = PushClientsManager.getInstance().getAllClientLoginInfoByChid(Integer.toString(blob.getChannelId()));
        if (allClientLoginInfoByChid.isEmpty()) {
            return null;
        }
        Iterator<PushClientsManager.ClientLoginInfo> it = allClientLoginInfoByChid.iterator();
        if (allClientLoginInfoByChid.size() == 1) {
            return it.next();
        }
        String fullUserName = blob.getFullUserName();
        while (it.hasNext()) {
            PushClientsManager.ClientLoginInfo next = it.next();
            if (TextUtils.equals(fullUserName, next.userId)) {
                return next;
            }
        }
        return null;
    }

    public void notifyPacketArrival(XMPushService xMPushService, String str, Blob blob) {
        PushClientsManager.ClientLoginInfo clientLoginInfo = getClientLoginInfo(blob);
        if (clientLoginInfo == null) {
            MyLog.e("error while notify channel closed! channel " + str + " not registered");
            return;
        }
        if ("5".equalsIgnoreCase(str)) {
            this.mPushEventProcessor.processNewPacket(xMPushService, blob, clientLoginInfo);
            return;
        }
        String str2 = clientLoginInfo.pkgName;
        Intent intent = new Intent();
        intent.setAction(PushConstants.ACTION_RECEIVE_NEW_MESSAGE);
        intent.setPackage(str2);
        intent.putExtra(PushConstants.EXTRA_RCV_TIMESTAMP, SystemClock.elapsedRealtime());
        intent.putExtra(PushConstants.EXTRA_CHID, str);
        intent.putExtra(PushConstants.EXTRA_RAW_PACKET, blob.getDecryptedPayload(clientLoginInfo.security));
        intent.putExtra(PushConstants.EXTRA_SESSION, clientLoginInfo.session);
        intent.putExtra(PushConstants.EXTRA_SECURITY, clientLoginInfo.security);
        if (ChannelConstants.isIOTDCMStatTime(blob)) {
            intent.putExtra(PushConstants.EXTRA_DOWNWARD_PACKET_ID, blob.getPacketID());
        }
        if (clientLoginInfo.peer != null) {
            try {
                clientLoginInfo.peer.send(Message.obtain(null, 17, intent));
                MyLog.w("message was sent by messenger for chid=" + str);
                return;
            } catch (RemoteException unused) {
                clientLoginInfo.peer = null;
                StringBuilder sb = new StringBuilder();
                sb.append("peer may died: ");
                String str3 = clientLoginInfo.userId;
                sb.append(str3.substring(str3.lastIndexOf(64)));
                MyLog.w(sb.toString());
            }
        }
        if (PushConstants.PUSH_SERVICE_PACKAGE_NAME.equals(str2)) {
            return;
        }
        MyLog.w(String.format("[Bcst] notify packet(blob) arrival. %s,%s,%s", clientLoginInfo.chid, clientLoginInfo.pkgName, blob.getPacketID()));
        if (ChannelConstants.isIOTDCMStatTime(blob)) {
            RecordTimeManager.get().recordMsgHandleTime(blob.getPacketID(), SystemClock.elapsedRealtime());
        }
        sendBroadcast(xMPushService, intent, clientLoginInfo);
    }
}
