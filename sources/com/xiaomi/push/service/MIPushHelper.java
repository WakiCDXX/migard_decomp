package com.xiaomi.push.service;

import android.content.Context;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.msa.MsaIdManager;
import com.xiaomi.push.clientreport.PerfMessageHelper;
import com.xiaomi.slim.Blob;
import com.xiaomi.smack.Connection;
import com.xiaomi.smack.XMPPException;
import com.xiaomi.smack.packet.Packet;
import com.xiaomi.xmpush.thrift.ActionType;
import com.xiaomi.xmpush.thrift.NotificationType;
import com.xiaomi.xmpush.thrift.PushMetaInfo;
import com.xiaomi.xmpush.thrift.Target;
import com.xiaomi.xmpush.thrift.XmPushActionContainer;
import com.xiaomi.xmpush.thrift.XmPushActionNotification;
import com.xiaomi.xmpush.thrift.XmPushThriftSerializeUtils;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;

/* JADX INFO: loaded from: classes.dex */
final class MIPushHelper {
    static final int MIPUSH_CHANNEL_ID = 5;
    private static final String SYNC_GROUP_MSAID = "MSAID";
    private static final String SYNC_KEY_MSAID = "msaid";

    MIPushHelper() {
    }

    static Blob constructBlob(MIPushAccount mIPushAccount, Context context, XmPushActionContainer xmPushActionContainer) {
        try {
            Blob blob = new Blob();
            blob.setChannelId(5);
            blob.setFrom(mIPushAccount.account);
            blob.setPackageName(getSourcePkgName(xmPushActionContainer));
            blob.setCmd(Blob.CMD_SECMSG, "message");
            String str = mIPushAccount.account;
            xmPushActionContainer.target.userId = str.substring(0, str.indexOf("@"));
            xmPushActionContainer.target.resource = str.substring(str.indexOf("/") + 1);
            blob.setPayload(XmPushThriftSerializeUtils.convertThriftObjectToBytes(xmPushActionContainer), mIPushAccount.security);
            blob.setPayloadType((short) 1);
            MyLog.w("try send mi push message. packagename:" + xmPushActionContainer.packageName + " action:" + xmPushActionContainer.action);
            return blob;
        } catch (NullPointerException e2) {
            MyLog.e(e2);
            return null;
        }
    }

    static <T extends TBase<T, ?>> XmPushActionContainer constructResponseContainer(String str, String str2, T t2, ActionType actionType) {
        return generateContainer(str, str2, t2, actionType, false);
    }

    static XmPushActionContainer contructAppAbsentMessage(String str, String str2) {
        XmPushActionNotification xmPushActionNotification = new XmPushActionNotification();
        xmPushActionNotification.setAppId(str2);
        xmPushActionNotification.setType("package uninstalled");
        xmPushActionNotification.setId(Packet.nextID());
        xmPushActionNotification.setRequireAck(false);
        return generateRequestContainer(str, str2, xmPushActionNotification, ActionType.Notification);
    }

    static XmPushActionContainer contructAppDataCleared(String str, String str2) {
        XmPushActionNotification xmPushActionNotification = new XmPushActionNotification();
        xmPushActionNotification.setAppId(str2);
        xmPushActionNotification.setType(NotificationType.AppDataCleared.value);
        xmPushActionNotification.setId(PacketHelper.generatePacketID());
        xmPushActionNotification.setRequireAck(false);
        return generateRequestContainer(str, str2, xmPushActionNotification, ActionType.Notification);
    }

    private static <T extends TBase<T, ?>> XmPushActionContainer generateContainer(String str, String str2, T t2, ActionType actionType, boolean z2) {
        byte[] bArrConvertThriftObjectToBytes = XmPushThriftSerializeUtils.convertThriftObjectToBytes(t2);
        XmPushActionContainer xmPushActionContainer = new XmPushActionContainer();
        Target target = new Target();
        target.channelId = 5L;
        target.userId = "fakeid";
        xmPushActionContainer.setTarget(target);
        xmPushActionContainer.setPushAction(ByteBuffer.wrap(bArrConvertThriftObjectToBytes));
        xmPushActionContainer.setAction(actionType);
        xmPushActionContainer.setIsRequest(z2);
        xmPushActionContainer.setPackageName(str);
        xmPushActionContainer.setEncryptAction(false);
        xmPushActionContainer.setAppid(str2);
        return xmPushActionContainer;
    }

    static <T extends TBase<T, ?>> XmPushActionContainer generateRequestContainer(String str, String str2, T t2, ActionType actionType) {
        return generateContainer(str, str2, t2, actionType, true);
    }

    static String getReceiverPermission(String str) {
        return str + ".permission.MIPUSH_RECEIVE";
    }

    private static String getSourcePkgName(XmPushActionContainer xmPushActionContainer) {
        Map<String, String> map;
        PushMetaInfo pushMetaInfo = xmPushActionContainer.metaInfo;
        if (pushMetaInfo != null && (map = pushMetaInfo.internal) != null) {
            String str = map.get(PushConstants.EXTRA_TRAFFIC_SOURCE_PKG);
            if (!TextUtils.isEmpty(str)) {
                return str;
            }
        }
        return xmPushActionContainer.packageName;
    }

    static void prepareClientLoginInfo(final XMPushService xMPushService, PushClientsManager.ClientLoginInfo clientLoginInfo) {
        clientLoginInfo.watch(null);
        clientLoginInfo.addClientStatusListener(new PushClientsManager.ClientLoginInfo.ClientStatusListener() { // from class: com.xiaomi.push.service.MIPushHelper.2
            @Override // com.xiaomi.push.service.PushClientsManager.ClientLoginInfo.ClientStatusListener
            public void onChange(PushClientsManager.ClientStatus clientStatus, PushClientsManager.ClientStatus clientStatus2, int i2) {
                if (clientStatus2 == PushClientsManager.ClientStatus.binded) {
                    MIPushClientManager.processPendingRegistrationRequest(xMPushService, true);
                    MIPushClientManager.processPendingMessages(xMPushService);
                } else if (clientStatus2 == PushClientsManager.ClientStatus.unbind) {
                    MyLog.w("onChange unbind");
                    MIPushClientManager.notifyRegisterError(xMPushService, 70000001, " the push is not connected.");
                }
            }
        });
    }

    static void prepareMIPushAccount(XMPushService xMPushService) {
        MIPushAccount mIPushAccount = MIPushAccountUtils.getMIPushAccount(xMPushService.getApplicationContext());
        if (mIPushAccount != null) {
            PushClientsManager.ClientLoginInfo clientLoginInfo = MIPushAccountUtils.getMIPushAccount(xMPushService.getApplicationContext()).toClientLoginInfo(xMPushService);
            MyLog.w("prepare account. " + clientLoginInfo.pkgName);
            prepareClientLoginInfo(xMPushService, clientLoginInfo);
            PushClientsManager.getInstance().addActiveClient(clientLoginInfo);
            syncMsaid(xMPushService, mIPushAccount, 172800);
        }
    }

    static void sendPacket(XMPushService xMPushService, String str, byte[] bArr) throws XMPPException {
        PerfMessageHelper.collectUpStream(str, xMPushService.getApplicationContext(), bArr);
        Connection currentConnection = xMPushService.getCurrentConnection();
        if (currentConnection == null) {
            throw new XMPPException("try send msg while connection is null.");
        }
        if (!currentConnection.isBinaryConnection()) {
            throw new XMPPException("Don't support XMPP connection.");
        }
        Blob blobConstructBlob = constructBlob(xMPushService, bArr);
        if (blobConstructBlob != null) {
            currentConnection.send(blobConstructBlob);
        } else {
            MIPushClientManager.notifyError(xMPushService, str, bArr, 70000003, "not a valid message");
        }
    }

    private static void syncMsaid(final XMPushService xMPushService, final MIPushAccount mIPushAccount, int i2) {
        Sync.getInstance(xMPushService).schedSync(new Sync.SyncJob(SYNC_GROUP_MSAID, i2) { // from class: com.xiaomi.push.service.MIPushHelper.1
            @Override // com.xiaomi.push.service.Sync.SyncJob
            void sync(Sync sync) {
                MsaIdManager msaIdManager = MsaIdManager.getInstance(xMPushService);
                String string = sync.getString(MIPushHelper.SYNC_GROUP_MSAID, MIPushHelper.SYNC_KEY_MSAID);
                String oaid = msaIdManager.getOAID();
                if (TextUtils.isEmpty(oaid) || TextUtils.equals(string, oaid)) {
                    return;
                }
                sync.put(MIPushHelper.SYNC_GROUP_MSAID, MIPushHelper.SYNC_KEY_MSAID, oaid);
                XmPushActionNotification xmPushActionNotification = new XmPushActionNotification();
                xmPushActionNotification.setAppId(mIPushAccount.appId);
                xmPushActionNotification.setType(NotificationType.ClientInfoUpdate.value);
                xmPushActionNotification.setId(PacketHelper.generatePacketID());
                xmPushActionNotification.setExtra(new HashMap());
                msaIdManager.fillData(xmPushActionNotification.getExtra());
                byte[] bArrConvertThriftObjectToBytes = XmPushThriftSerializeUtils.convertThriftObjectToBytes(MIPushHelper.generateRequestContainer(xMPushService.getPackageName(), mIPushAccount.appId, xmPushActionNotification, ActionType.Notification));
                XMPushService xMPushService2 = xMPushService;
                xMPushService2.sendMessage(xMPushService2.getPackageName(), bArrConvertThriftObjectToBytes, true);
            }
        });
    }

    static void sendPacket(XMPushService xMPushService, XmPushActionContainer xmPushActionContainer) throws XMPPException {
        PerfMessageHelper.collectUpStream(xmPushActionContainer.getPackageName(), xMPushService.getApplicationContext(), xmPushActionContainer, -1);
        Connection currentConnection = xMPushService.getCurrentConnection();
        if (currentConnection != null) {
            if (currentConnection.isBinaryConnection()) {
                Blob blobConstructBlob = constructBlob(MIPushAccountUtils.getMIPushAccount(xMPushService), xMPushService, xmPushActionContainer);
                if (blobConstructBlob != null) {
                    currentConnection.send(blobConstructBlob);
                    return;
                }
                return;
            }
            throw new XMPPException("Don't support XMPP connection.");
        }
        throw new XMPPException("try send msg while connection is null.");
    }

    static Blob constructBlob(XMPushService xMPushService, byte[] bArr) {
        XmPushActionContainer xmPushActionContainer = new XmPushActionContainer();
        try {
            XmPushThriftSerializeUtils.convertByteArrayToThriftObject(xmPushActionContainer, bArr);
            return constructBlob(MIPushAccountUtils.getMIPushAccount(xMPushService), xMPushService, xmPushActionContainer);
        } catch (TException e2) {
            MyLog.e(e2);
            return null;
        }
    }
}
