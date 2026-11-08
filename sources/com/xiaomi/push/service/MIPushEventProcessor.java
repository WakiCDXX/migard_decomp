package com.xiaomi.push.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.AppInfoUtils;
import com.xiaomi.channel.commonutils.android.DeviceInfo;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channelcollection.RegisterAppChannelInfoCollectionJob;
import com.xiaomi.push.clientreport.PerfMessageHelper;
import com.xiaomi.push.service.clientReport.PushClientReportManager;
import com.xiaomi.push.service.clientReport.ReportConstants;
import com.xiaomi.slim.Blob;
import com.xiaomi.smack.XMPPException;
import com.xiaomi.smack.packet.CommonPacketExtension;
import com.xiaomi.smack.packet.Message;
import com.xiaomi.smack.packet.Packet;
import com.xiaomi.smack.util.TrafficUtils;
import com.xiaomi.xmpush.thrift.ActionType;
import com.xiaomi.xmpush.thrift.NotificationType;
import com.xiaomi.xmpush.thrift.PushMetaInfo;
import com.xiaomi.xmpush.thrift.XmPushActionAckMessage;
import com.xiaomi.xmpush.thrift.XmPushActionAckNotification;
import com.xiaomi.xmpush.thrift.XmPushActionContainer;
import com.xiaomi.xmpush.thrift.XmPushActionNotification;
import com.xiaomi.xmpush.thrift.XmPushActionRegistrationResult;
import com.xiaomi.xmpush.thrift.XmPushThriftSerializeUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;

/* JADX INFO: loaded from: classes.dex */
public class MIPushEventProcessor {
    public static boolean broadcastMessageArrived(Context context, String str, byte[] bArr) {
        if (!AppInfoUtils.isAppRunning(context, str)) {
            return false;
        }
        Intent intent = new Intent(PushConstants.MIPUSH_ACTION_MESSAGE_ARRIVED);
        intent.putExtra(PushConstants.MIPUSH_EXTRA_PAYLOAD, bArr);
        intent.setPackage(str);
        try {
            if (context.getPackageManager().queryBroadcastReceivers(intent, 0).isEmpty()) {
                return false;
            }
            MyLog.w("broadcast message arrived.");
            context.sendBroadcast(intent, MIPushHelper.getReceiverPermission(str));
            return true;
        } catch (Exception e2) {
            MyLog.w("meet error when broadcast message arrived. " + e2);
            return false;
        }
    }

    public static XmPushActionContainer buildContainer(byte[] bArr) {
        XmPushActionContainer xmPushActionContainer = new XmPushActionContainer();
        try {
            XmPushThriftSerializeUtils.convertByteArrayToThriftObject(xmPushActionContainer, bArr);
            return xmPushActionContainer;
        } catch (Throwable th) {
            MyLog.e(th);
            return null;
        }
    }

    public static Intent buildIntent(byte[] bArr, long j2) {
        XmPushActionContainer xmPushActionContainerBuildContainer = buildContainer(bArr);
        if (xmPushActionContainerBuildContainer == null) {
            return null;
        }
        Intent intent = new Intent(PushConstants.MIPUSH_ACTION_NEW_MESSAGE);
        intent.putExtra(PushConstants.MIPUSH_EXTRA_PAYLOAD, bArr);
        intent.putExtra(PushConstants.MESSAGE_RECEIVE_TIME, Long.toString(j2));
        intent.setPackage(xmPushActionContainerBuildContainer.packageName);
        return intent;
    }

    public static XmPushActionContainer constructAckMessage(Context context, XmPushActionContainer xmPushActionContainer) {
        return constructAckMessage(context, xmPushActionContainer, null);
    }

    private static boolean isIntentAvailable(Context context, Intent intent) {
        try {
            List<ResolveInfo> listQueryBroadcastReceivers = context.getPackageManager().queryBroadcastReceivers(intent, 32);
            return (listQueryBroadcastReceivers == null || listQueryBroadcastReceivers.isEmpty()) ? false : true;
        } catch (Exception unused) {
            return true;
        }
    }

    private static boolean isMIUIOldAdsSDKMessage(XmPushActionContainer xmPushActionContainer) {
        if (xmPushActionContainer.getMetaInfo() == null || xmPushActionContainer.getMetaInfo().getExtra() == null) {
            return false;
        }
        return "1".equals(xmPushActionContainer.getMetaInfo().getExtra().get("obslete_ads_message"));
    }

    private static boolean isMIUIPushMessage(XmPushActionContainer xmPushActionContainer) {
        return PushConstants.PUSH_SERVICE_PACKAGE_NAME.equals(xmPushActionContainer.packageName) && xmPushActionContainer.getMetaInfo() != null && xmPushActionContainer.getMetaInfo().getExtra() != null && xmPushActionContainer.getMetaInfo().getExtra().containsKey(MIPushNotificationHelper.MIUI_PACKAGE_NAME);
    }

    private static boolean isMIUIPushSupported(Context context, String str) {
        Intent intent = new Intent("com.xiaomi.mipush.miui.CLICK_MESSAGE");
        intent.setPackage(str);
        Intent intent2 = new Intent("com.xiaomi.mipush.miui.RECEIVE_MESSAGE");
        intent2.setPackage(str);
        PackageManager packageManager = context.getPackageManager();
        try {
            return (packageManager.queryBroadcastReceivers(intent2, 32).isEmpty() && packageManager.queryIntentServices(intent, 32).isEmpty()) ? false : true;
        } catch (Exception e2) {
            MyLog.e(e2);
            return false;
        }
    }

    private static void notifyMessageArrived(Context context, XmPushActionContainer xmPushActionContainer, byte[] bArr) {
        if (MIPushNotificationHelper.isBusinessMessage(xmPushActionContainer)) {
            return;
        }
        String targetPackage = MIPushNotificationHelper.getTargetPackage(xmPushActionContainer);
        if (TextUtils.isEmpty(targetPackage) || broadcastMessageArrived(context, targetPackage, bArr)) {
            return;
        }
        PushClientReportManager.getInstance(context).reportEvent4ERROR(targetPackage, MIPushNotificationHelper.getInterfaceId(xmPushActionContainer), xmPushActionContainer.getMetaInfo().getId(), "1");
    }

    public static void postNotifyPushMessage(Context context, XmPushActionContainer xmPushActionContainer, byte[] bArr) {
        try {
            MIPushNotificationHelper.NotifyPushMessageInfo notifyPushMessageInfoNotifyPushMessage = MIPushNotificationHelper.notifyPushMessage(context, xmPushActionContainer, bArr);
            if (notifyPushMessageInfoNotifyPushMessage.traffic > 0 && !TextUtils.isEmpty(notifyPushMessageInfoNotifyPushMessage.targetPkgName)) {
                TrafficUtils.distributionTraffic(context, notifyPushMessageInfoNotifyPushMessage.targetPkgName, notifyPushMessageInfoNotifyPushMessage.traffic, true, false, System.currentTimeMillis());
            }
            if (!MIUIUtils.isXMSF(context) || !MIPushEventProcessorAgent.shouldHandleMessage(context, xmPushActionContainer, notifyPushMessageInfoNotifyPushMessage.isNotifyFromStatusBar)) {
                notifyMessageArrived(context, xmPushActionContainer, bArr);
            } else {
                MIPushEventProcessorAgent.handleMessage(context, xmPushActionContainer);
                MyLog.w("consume this broadcast by tts");
            }
        } catch (Exception e2) {
            MyLog.w("notify push msg error " + e2);
            e2.printStackTrace();
        }
    }

    /* JADX WARN: Code duplicated, block: B:150:0x0443  */
    public static void postProcessMIPushMessage(XMPushService xMPushService, String str, byte[] bArr, Intent intent) {
        XmPushActionContainer xmPushActionContainerBuildContainer = buildContainer(bArr);
        PushMetaInfo metaInfo = xmPushActionContainerBuildContainer.getMetaInfo();
        TBase responseMessageBodyFromContainer = null;
        if (bArr != null) {
            PerfMessageHelper.collectPerfData(xmPushActionContainerBuildContainer.getPackageName(), xMPushService.getApplicationContext(), null, xmPushActionContainerBuildContainer.getAction(), bArr.length);
        }
        if (isMIUIOldAdsSDKMessage(xmPushActionContainerBuildContainer) && isMIUIPushSupported(xMPushService, str)) {
            if (MIPushNotificationHelper.isNPBMessage(xmPushActionContainerBuildContainer)) {
                PushClientReportManager.getInstance(xMPushService.getApplicationContext()).reportEvent4NeedDrop(xmPushActionContainerBuildContainer.getPackageName(), MIPushNotificationHelper.getInterfaceId(xmPushActionContainerBuildContainer), metaInfo.getId(), "5");
            }
            sendMIUIOldAdsAckMessage(xMPushService, xmPushActionContainerBuildContainer);
            return;
        }
        if (isMIUIPushMessage(xmPushActionContainerBuildContainer) && !isMIUIPushSupported(xMPushService, str) && !predefinedNotification(xmPushActionContainerBuildContainer)) {
            if (MIPushNotificationHelper.isNPBMessage(xmPushActionContainerBuildContainer)) {
                PushClientReportManager.getInstance(xMPushService.getApplicationContext()).reportEvent4NeedDrop(xmPushActionContainerBuildContainer.getPackageName(), MIPushNotificationHelper.getInterfaceId(xmPushActionContainerBuildContainer), metaInfo.getId(), "6");
            }
            sendMIUINewAdsAckMessage(xMPushService, xmPushActionContainerBuildContainer);
            return;
        }
        if ((!MIPushNotificationHelper.isBusinessMessage(xmPushActionContainerBuildContainer) || !AppInfoUtils.isPkgInstalled(xMPushService, xmPushActionContainerBuildContainer.packageName)) && !isIntentAvailable(xMPushService, intent)) {
            if (!AppInfoUtils.isPkgInstalled(xMPushService, xmPushActionContainerBuildContainer.packageName)) {
                if (MIPushNotificationHelper.isNPBMessage(xmPushActionContainerBuildContainer)) {
                    PushClientReportManager.getInstance(xMPushService.getApplicationContext()).reportEvent4ERROR(xmPushActionContainerBuildContainer.getPackageName(), MIPushNotificationHelper.getInterfaceId(xmPushActionContainerBuildContainer), metaInfo.getId(), "2");
                }
                sendAppNotInstallNotification(xMPushService, xmPushActionContainerBuildContainer);
                return;
            } else {
                MyLog.w("receive a mipush message, we can see the app, but we can't see the receiver.");
                if (MIPushNotificationHelper.isNPBMessage(xmPushActionContainerBuildContainer)) {
                    PushClientReportManager.getInstance(xMPushService.getApplicationContext()).reportEvent4ERROR(xmPushActionContainerBuildContainer.getPackageName(), MIPushNotificationHelper.getInterfaceId(xmPushActionContainerBuildContainer), metaInfo.getId(), "3");
                    return;
                }
                return;
            }
        }
        boolean z2 = false;
        if (ActionType.Registration == xmPushActionContainerBuildContainer.getAction()) {
            String packageName = xmPushActionContainerBuildContainer.getPackageName();
            SharedPreferences.Editor editorEdit = xMPushService.getSharedPreferences(PushServiceConstants.PREF_KEY_REGISTERED_PKGS, 0).edit();
            editorEdit.putString(packageName, xmPushActionContainerBuildContainer.appid);
            editorEdit.commit();
            XmPushActionRegistrationResult registrationResponse = FCMChannelHelper.getRegistrationResponse(xmPushActionContainerBuildContainer);
            if (registrationResponse.getErrorCode() != 0 || TextUtils.isEmpty(registrationResponse.getRegSecret())) {
                MyLog.e("read regSecret failed");
            } else {
                FCMChannelHelper.saveAppRegistrationSecret(xMPushService, packageName, registrationResponse.getRegSecret());
            }
            MIPushAppInfo.getInstance(xMPushService).removeDisablePushPkg(packageName);
            MIPushAppInfo.getInstance(xMPushService).removeDisablePushPkgCache(packageName);
            PushClientReportManager.getInstance(xMPushService.getApplicationContext()).reportEvent(packageName, ReportConstants.REGISTER_EVENT_CHAIN_INTERFACE_ID, metaInfo.getId(), ReportConstants.REGISTER_TYPE_RECEIVE, null);
            if (!TextUtils.isEmpty(metaInfo.getId())) {
                intent.putExtra("messageId", metaInfo.getId());
                intent.putExtra(ReportConstants.EVENT_MESSAGE_TYPE, ReportConstants.REGISTER_TYPE);
            }
        }
        if (MIPushNotificationHelper.isNormalNotificationMessage(xmPushActionContainerBuildContainer)) {
            PushClientReportManager.getInstance(xMPushService.getApplicationContext()).reportEvent(xmPushActionContainerBuildContainer.getPackageName(), MIPushNotificationHelper.getInterfaceId(xmPushActionContainerBuildContainer), metaInfo.getId(), 1001, System.currentTimeMillis(), null);
            if (!TextUtils.isEmpty(metaInfo.getId())) {
                intent.putExtra("messageId", metaInfo.getId());
                intent.putExtra(ReportConstants.EVENT_MESSAGE_TYPE, 1000);
            }
        }
        if (MIPushNotificationHelper.isPassThoughMessage(xmPushActionContainerBuildContainer)) {
            PushClientReportManager.getInstance(xMPushService.getApplicationContext()).reportEvent(xmPushActionContainerBuildContainer.getPackageName(), MIPushNotificationHelper.getInterfaceId(xmPushActionContainerBuildContainer), metaInfo.getId(), 2001, System.currentTimeMillis(), null);
            if (!TextUtils.isEmpty(metaInfo.getId())) {
                intent.putExtra("messageId", metaInfo.getId());
                intent.putExtra(ReportConstants.EVENT_MESSAGE_TYPE, 2000);
            }
        }
        if (MIPushNotificationHelper.isBusinessMessage(xmPushActionContainerBuildContainer)) {
            PushClientReportManager.getInstance(xMPushService.getApplicationContext()).reportEvent(xmPushActionContainerBuildContainer.getPackageName(), MIPushNotificationHelper.getInterfaceId(xmPushActionContainerBuildContainer), metaInfo.getId(), 3001, System.currentTimeMillis(), null);
            if (!TextUtils.isEmpty(metaInfo.getId())) {
                intent.putExtra("messageId", metaInfo.getId());
                intent.putExtra(ReportConstants.EVENT_MESSAGE_TYPE, ReportConstants.AWAKE_TYPE);
            }
        }
        if (metaInfo != null && !TextUtils.isEmpty(metaInfo.getTitle()) && !TextUtils.isEmpty(metaInfo.getDescription()) && metaInfo.passThrough != 1 && !MIPushNotificationHelper.isLimitNotifyForeground(xMPushService, xmPushActionContainerBuildContainer.packageName, MIPushNotificationHelper.isNotifyForeground(metaInfo.getExtra()))) {
            Map<String, String> map = metaInfo.extra;
            String id = map != null ? map.get("jobkey") : null;
            if (TextUtils.isEmpty(id)) {
                id = metaInfo.getId();
            }
            if (MiPushMessageDuplicate.isDuplicateMessage(xMPushService, xmPushActionContainerBuildContainer.packageName, id)) {
                PushClientReportManager.getInstance(xMPushService.getApplicationContext()).reportEvent4DUPMD(xmPushActionContainerBuildContainer.getPackageName(), MIPushNotificationHelper.getInterfaceId(xmPushActionContainerBuildContainer), metaInfo.getId(), "1:" + id);
                MyLog.w("drop a duplicate message, key=" + id);
            } else if (MIUIUtils.isXMSF(xMPushService) && MIPushEventProcessorAgent.handleReceiveMessage(xmPushActionContainerBuildContainer)) {
                MyLog.w("receive pull down message");
            } else {
                postNotifyPushMessage(xMPushService, xmPushActionContainerBuildContainer, bArr);
            }
            sendAckMessage(xMPushService, xmPushActionContainerBuildContainer);
        } else if (PushConstants.PUSH_SERVICE_PACKAGE_NAME.contains(xmPushActionContainerBuildContainer.packageName) && !xmPushActionContainerBuildContainer.isEncryptAction() && metaInfo != null && metaInfo.getExtra() != null && metaInfo.getExtra().containsKey("ab")) {
            sendAckMessage(xMPushService, xmPushActionContainerBuildContainer);
            MyLog.v("receive abtest message. ack it." + metaInfo.getId());
        } else if (shouldSendBroadcast(xMPushService, str, xmPushActionContainerBuildContainer, metaInfo)) {
            if (metaInfo != null && !TextUtils.isEmpty(metaInfo.getId())) {
                if (MIPushNotificationHelper.isPassThoughMessage(xmPushActionContainerBuildContainer)) {
                    PushClientReportManager.getInstance(xMPushService.getApplicationContext()).reportEvent(xmPushActionContainerBuildContainer.getPackageName(), MIPushNotificationHelper.getInterfaceId(xmPushActionContainerBuildContainer), metaInfo.getId(), 2002, null);
                } else if (MIPushNotificationHelper.isBusinessMessage(xmPushActionContainerBuildContainer)) {
                    PushClientReportManager.getInstance(xMPushService.getApplicationContext()).reportEvent4NeedDrop(xmPushActionContainerBuildContainer.getPackageName(), MIPushNotificationHelper.getInterfaceId(xmPushActionContainerBuildContainer), metaInfo.getId(), "7");
                } else if (MIPushNotificationHelper.isNormalNotificationMessage(xmPushActionContainerBuildContainer)) {
                    PushClientReportManager.getInstance(xMPushService.getApplicationContext()).reportEvent4NeedDrop(xmPushActionContainerBuildContainer.getPackageName(), MIPushNotificationHelper.getInterfaceId(xmPushActionContainerBuildContainer), metaInfo.getId(), "8");
                } else if (MIPushNotificationHelper.isRegisterMessage(xmPushActionContainerBuildContainer)) {
                    PushClientReportManager.getInstance(xMPushService.getApplicationContext()).reportEvent(xmPushActionContainerBuildContainer.getPackageName(), ReportConstants.REGISTER_EVENT_CHAIN_INTERFACE_ID, metaInfo.getId(), ReportConstants.REGISTER_TYPE_SEND_BROADCAST, null);
                }
            }
            if (ActionType.Notification == xmPushActionContainerBuildContainer.action) {
                try {
                    responseMessageBodyFromContainer = UnEncryptedPushContainerHelper.getResponseMessageBodyFromContainer(xMPushService, xmPushActionContainerBuildContainer);
                    if (responseMessageBodyFromContainer == null) {
                        MyLog.e("receiving an un-recognized notification message. " + xmPushActionContainerBuildContainer.action);
                    } else {
                        z2 = true;
                    }
                } catch (TException e2) {
                    MyLog.e("receive a message which action string is not valid. " + e2);
                }
                if (z2 && (responseMessageBodyFromContainer instanceof XmPushActionNotification)) {
                    XmPushActionNotification xmPushActionNotification = (XmPushActionNotification) responseMessageBodyFromContainer;
                    if (NotificationType.CancelPushMessage.value.equals(xmPushActionNotification.type) && xmPushActionNotification.getExtra() != null) {
                        String str2 = xmPushActionNotification.getExtra().get(PushConstants.PUSH_NOTIFY_ID);
                        int i2 = -2;
                        if (!TextUtils.isEmpty(str2)) {
                            try {
                                i2 = Integer.parseInt(str2);
                            } catch (NumberFormatException e3) {
                                MyLog.w("parse notifyId from STRING to INT failed: " + e3);
                            }
                        }
                        if (i2 >= -1) {
                            MyLog.w("try to retract a message by notifyId=" + i2);
                            MIPushNotificationHelper.clearNotification(xMPushService, xmPushActionContainerBuildContainer.packageName, i2);
                        } else {
                            String str3 = xmPushActionNotification.getExtra().get(PushConstants.PUSH_TITLE);
                            String str4 = xmPushActionNotification.getExtra().get(PushConstants.PUSH_DESCRIPTION);
                            MyLog.w("try to retract a message by title&description.");
                            MIPushNotificationHelper.clearNotification(xMPushService, xmPushActionContainerBuildContainer.packageName, str3, str4);
                        }
                        if (metaInfo != null && metaInfo.getExtra() != null && MIUIUtils.isXMSF(xMPushService) && MIPushNotificationHelper.MSG_BUSI_TYPE_PULLDOWN.equals(NotificationUtils.getMsgBusinessType(metaInfo.getExtra()))) {
                            MIPushEventProcessorAgent.clearMessage(xmPushActionContainerBuildContainer);
                        }
                        sendClearPushMessageAck(xMPushService, xmPushActionContainerBuildContainer, xmPushActionNotification);
                    } else if (!NotificationType.SettingAppNotificationPermission.value.equals(xmPushActionNotification.getType())) {
                        MyLog.w("broadcast passthrough message.");
                        xMPushService.sendBroadcast(intent, MIPushHelper.getReceiverPermission(xmPushActionContainerBuildContainer.packageName));
                    } else if (MIUIUtils.isXMSF(xMPushService)) {
                        MIPushEventProcessorAgent.setAppNotificationPermission(xMPushService, xmPushActionContainerBuildContainer, xmPushActionNotification);
                    }
                } else {
                    MyLog.w("broadcast passthrough message.");
                    xMPushService.sendBroadcast(intent, MIPushHelper.getReceiverPermission(xmPushActionContainerBuildContainer.packageName));
                }
            } else {
                MyLog.w("broadcast passthrough message.");
                xMPushService.sendBroadcast(intent, MIPushHelper.getReceiverPermission(xmPushActionContainerBuildContainer.packageName));
            }
        } else {
            PushClientReportManager.getInstance(xMPushService.getApplicationContext()).reportEvent4NeedDrop(xmPushActionContainerBuildContainer.getPackageName(), MIPushNotificationHelper.getInterfaceId(xmPushActionContainerBuildContainer), metaInfo.getId(), "9");
        }
        if (xmPushActionContainerBuildContainer.getAction() != ActionType.UnRegistration || PushConstants.PUSH_SERVICE_PACKAGE_NAME.equals(xMPushService.getPackageName())) {
            return;
        }
        xMPushService.stopSelf();
    }

    private static boolean predefinedNotification(XmPushActionContainer xmPushActionContainer) {
        Map<String, String> extra = xmPushActionContainer.getMetaInfo().getExtra();
        return extra != null && extra.containsKey(PushConstants.EXTRA_PARAM_NOTIFY_EFFECT);
    }

    private static void processMIPushMessage(XMPushService xMPushService, byte[] bArr, long j2) {
        processMIPushMessage(xMPushService, bArr, j2, null);
    }

    private static void sendAckMessage(final XMPushService xMPushService, final XmPushActionContainer xmPushActionContainer) {
        xMPushService.executeJob(new XMPushService.Job(4) { // from class: com.xiaomi.push.service.MIPushEventProcessor.2
            @Override // com.xiaomi.push.service.XMPushService.Job
            public String getDesc() {
                return "send ack message for message.";
            }

            @Override // com.xiaomi.push.service.XMPushService.Job
            public void process() {
                Map<String, String> mapFillParamsInACK;
                try {
                    if (MIUIUtils.isXMSF(xMPushService)) {
                        try {
                            mapFillParamsInACK = MIPushEventProcessorAgent.fillParamsInACK(xMPushService, xmPushActionContainer);
                        } catch (Throwable unused) {
                            mapFillParamsInACK = null;
                        }
                    } else {
                        mapFillParamsInACK = null;
                    }
                    MIPushHelper.sendPacket(xMPushService, MIPushEventProcessor.constructAckMessage(xMPushService, xmPushActionContainer, mapFillParamsInACK));
                } catch (XMPPException e2) {
                    MyLog.e(e2);
                    xMPushService.disconnect(10, e2);
                }
            }
        });
    }

    private static void sendAppAbsentAck(final XMPushService xMPushService, final XmPushActionContainer xmPushActionContainer, final String str) {
        xMPushService.executeJob(new XMPushService.Job(4) { // from class: com.xiaomi.push.service.MIPushEventProcessor.5
            @Override // com.xiaomi.push.service.XMPushService.Job
            public String getDesc() {
                return "send app absent ack message for message.";
            }

            @Override // com.xiaomi.push.service.XMPushService.Job
            public void process() {
                try {
                    XmPushActionContainer xmPushActionContainerConstructAckMessage = MIPushEventProcessor.constructAckMessage(xMPushService, xmPushActionContainer);
                    xmPushActionContainerConstructAckMessage.getMetaInfo().putToExtra("absent_target_package", str);
                    MIPushHelper.sendPacket(xMPushService, xmPushActionContainerConstructAckMessage);
                } catch (XMPPException e2) {
                    MyLog.e(e2);
                    xMPushService.disconnect(10, e2);
                }
            }
        });
    }

    private static void sendAppNotInstallNotification(final XMPushService xMPushService, final XmPushActionContainer xmPushActionContainer) {
        xMPushService.executeJob(new XMPushService.Job(4) { // from class: com.xiaomi.push.service.MIPushEventProcessor.1
            @Override // com.xiaomi.push.service.XMPushService.Job
            public String getDesc() {
                return "send app absent message.";
            }

            @Override // com.xiaomi.push.service.XMPushService.Job
            public void process() {
                try {
                    MIPushHelper.sendPacket(xMPushService, MIPushHelper.contructAppAbsentMessage(xmPushActionContainer.getPackageName(), xmPushActionContainer.getAppid()));
                } catch (XMPPException e2) {
                    MyLog.e(e2);
                    xMPushService.disconnect(10, e2);
                }
            }
        });
    }

    private static void sendClearPushMessageAck(final XMPushService xMPushService, final XmPushActionContainer xmPushActionContainer, final XmPushActionNotification xmPushActionNotification) {
        xMPushService.executeJob(new XMPushService.Job(4) { // from class: com.xiaomi.push.service.MIPushEventProcessor.7
            @Override // com.xiaomi.push.service.XMPushService.Job
            public String getDesc() {
                return "send ack message for clear push message.";
            }

            @Override // com.xiaomi.push.service.XMPushService.Job
            public void process() {
                try {
                    XmPushActionAckNotification xmPushActionAckNotification = new XmPushActionAckNotification();
                    xmPushActionAckNotification.setType(NotificationType.CancelPushMessageACK.value);
                    xmPushActionAckNotification.setId(xmPushActionNotification.getId());
                    xmPushActionAckNotification.setTarget(xmPushActionNotification.getTarget());
                    xmPushActionAckNotification.setAppId(xmPushActionNotification.getAppId());
                    xmPushActionAckNotification.setPackageName(xmPushActionNotification.getPackageName());
                    xmPushActionAckNotification.setErrorCode(0L);
                    xmPushActionAckNotification.setReason("success clear push message.");
                    MIPushHelper.sendPacket(xMPushService, MIPushHelper.constructResponseContainer(xmPushActionContainer.getPackageName(), xmPushActionContainer.getAppid(), xmPushActionAckNotification, ActionType.Notification));
                } catch (XMPPException e2) {
                    MyLog.e("clear push message. " + e2);
                    xMPushService.disconnect(10, e2);
                }
            }
        });
    }

    private static void sendErrorAck(final XMPushService xMPushService, final XmPushActionContainer xmPushActionContainer, final String str, final String str2) {
        xMPushService.executeJob(new XMPushService.Job(4) { // from class: com.xiaomi.push.service.MIPushEventProcessor.6
            @Override // com.xiaomi.push.service.XMPushService.Job
            public String getDesc() {
                return "send wrong message ack for message.";
            }

            @Override // com.xiaomi.push.service.XMPushService.Job
            public void process() {
                try {
                    XmPushActionContainer xmPushActionContainerConstructAckMessage = MIPushEventProcessor.constructAckMessage(xMPushService, xmPushActionContainer);
                    xmPushActionContainerConstructAckMessage.metaInfo.putToExtra(Message.MSG_TYPE_ERROR, str);
                    xmPushActionContainerConstructAckMessage.metaInfo.putToExtra("reason", str2);
                    MIPushHelper.sendPacket(xMPushService, xmPushActionContainerConstructAckMessage);
                } catch (XMPPException e2) {
                    MyLog.e(e2);
                    xMPushService.disconnect(10, e2);
                }
            }
        });
    }

    private static void sendMIUINewAdsAckMessage(final XMPushService xMPushService, final XmPushActionContainer xmPushActionContainer) {
        xMPushService.executeJob(new XMPushService.Job(4) { // from class: com.xiaomi.push.service.MIPushEventProcessor.4
            @Override // com.xiaomi.push.service.XMPushService.Job
            public String getDesc() {
                return "send ack message for unrecognized new miui message.";
            }

            @Override // com.xiaomi.push.service.XMPushService.Job
            public void process() {
                try {
                    XmPushActionContainer xmPushActionContainerConstructAckMessage = MIPushEventProcessor.constructAckMessage(xMPushService, xmPushActionContainer);
                    xmPushActionContainerConstructAckMessage.getMetaInfo().putToExtra("miui_message_unrecognized", "1");
                    MIPushHelper.sendPacket(xMPushService, xmPushActionContainerConstructAckMessage);
                } catch (XMPPException e2) {
                    MyLog.e(e2);
                    xMPushService.disconnect(10, e2);
                }
            }
        });
    }

    private static void sendMIUIOldAdsAckMessage(final XMPushService xMPushService, final XmPushActionContainer xmPushActionContainer) {
        xMPushService.executeJob(new XMPushService.Job(4) { // from class: com.xiaomi.push.service.MIPushEventProcessor.3
            @Override // com.xiaomi.push.service.XMPushService.Job
            public String getDesc() {
                return "send ack message for obsleted message.";
            }

            @Override // com.xiaomi.push.service.XMPushService.Job
            public void process() {
                try {
                    XmPushActionContainer xmPushActionContainerConstructAckMessage = MIPushEventProcessor.constructAckMessage(xMPushService, xmPushActionContainer);
                    xmPushActionContainerConstructAckMessage.getMetaInfo().putToExtra("message_obsleted", "1");
                    MIPushHelper.sendPacket(xMPushService, xmPushActionContainerConstructAckMessage);
                } catch (XMPPException e2) {
                    MyLog.e(e2);
                    xMPushService.disconnect(10, e2);
                }
            }
        });
    }

    private static boolean shouldSendBroadcast(XMPushService xMPushService, String str, XmPushActionContainer xmPushActionContainer, PushMetaInfo pushMetaInfo) {
        boolean z2 = true;
        if (pushMetaInfo != null && pushMetaInfo.getExtra() != null && pushMetaInfo.getExtra().containsKey(PushConstants.EXTRA_PARAM_CHECK_ALIVE) && pushMetaInfo.getExtra().containsKey(PushConstants.EXTRA_PARAM_AWAKE)) {
            XmPushActionNotification xmPushActionNotification = new XmPushActionNotification();
            xmPushActionNotification.setAppId(xmPushActionContainer.getAppid());
            xmPushActionNotification.setPackageName(str);
            xmPushActionNotification.setType(NotificationType.AwakeSystemApp.value);
            xmPushActionNotification.setId(pushMetaInfo.getId());
            xmPushActionNotification.extra = new HashMap();
            boolean zIsAppRunning = AppInfoUtils.isAppRunning(xMPushService.getApplicationContext(), str);
            xmPushActionNotification.extra.put(PushConstants.EXTRA_PARAM_APP_RUNNING, Boolean.toString(zIsAppRunning));
            if (!zIsAppRunning) {
                boolean z3 = Boolean.parseBoolean(pushMetaInfo.getExtra().get(PushConstants.EXTRA_PARAM_AWAKE));
                xmPushActionNotification.extra.put(PushConstants.EXTRA_PARAM_AWAKED, Boolean.toString(z3));
                if (!z3) {
                    z2 = false;
                }
            }
            try {
                MIPushHelper.sendPacket(xMPushService, MIPushHelper.generateRequestContainer(xmPushActionContainer.getPackageName(), xmPushActionContainer.getAppid(), xmPushActionNotification, ActionType.Notification));
            } catch (XMPPException e2) {
                MyLog.e(e2);
            }
        }
        return z2;
    }

    public void processChannelOpenResult(Context context, PushClientsManager.ClientLoginInfo clientLoginInfo, boolean z2, int i2, String str) {
        MIPushAccount mIPushAccount;
        if (z2 || (mIPushAccount = MIPushAccountUtils.getMIPushAccount(context)) == null || !"token-expired".equals(str)) {
            return;
        }
        MIPushAccountUtils.register(context, mIPushAccount.packageName, mIPushAccount.appId, mIPushAccount.appToken);
    }

    public void processNewPacket(XMPushService xMPushService, Packet packet, PushClientsManager.ClientLoginInfo clientLoginInfo) {
        if (!(packet instanceof Message)) {
            MyLog.w("not a mipush message");
            return;
        }
        Message message = (Message) packet;
        CommonPacketExtension extension = message.getExtension(RegisterAppChannelInfoCollectionJob.JSON_KEY_SWITCH);
        if (extension != null) {
            try {
                processMIPushMessage(xMPushService, RC4Cryption.decrypt(RC4Cryption.generateKeyForRC4(clientLoginInfo.security, message.getPacketID()), extension.getText()), TrafficUtils.getTrafficFlow(packet.toXML()));
            } catch (IllegalArgumentException e2) {
                MyLog.e(e2);
            }
        }
    }

    public static XmPushActionContainer constructAckMessage(Context context, XmPushActionContainer xmPushActionContainer, Map<String, String> map) {
        XmPushActionAckMessage xmPushActionAckMessage = new XmPushActionAckMessage();
        xmPushActionAckMessage.setAppId(xmPushActionContainer.getAppid());
        PushMetaInfo metaInfo = xmPushActionContainer.getMetaInfo();
        if (metaInfo != null) {
            xmPushActionAckMessage.setId(metaInfo.getId());
            xmPushActionAckMessage.setMessageTs(metaInfo.getMessageTs());
            if (!TextUtils.isEmpty(metaInfo.getTopic())) {
                xmPushActionAckMessage.setTopic(metaInfo.getTopic());
            }
        }
        xmPushActionAckMessage.setDeviceStatus(XmPushThriftSerializeUtils.getDeviceStatus(context, xmPushActionContainer));
        XmPushActionContainer xmPushActionContainerGenerateRequestContainer = MIPushHelper.generateRequestContainer(xmPushActionContainer.getPackageName(), xmPushActionContainer.getAppid(), xmPushActionAckMessage, ActionType.AckMessage);
        PushMetaInfo metaInfo2 = xmPushActionContainer.getMetaInfo();
        if (metaInfo2 != null) {
            metaInfo2 = RedundantUploadInfoHelper.cleanRedundantInfoForAck(metaInfo2.m25deepCopy());
            Map<String, String> extra = metaInfo2.getExtra();
            String str = extra != null ? extra.get(MIPushNotificationHelper.NOTIFICATION_CHANNEL_ID) : null;
            metaInfo2.putToExtra(PushConstants.MESSAGE_ACK_TIME, Long.toString(System.currentTimeMillis()));
            metaInfo2.putToExtra("cs", String.valueOf(ChannelPermissions.getChannelInfo(context, xmPushActionContainer.packageName, str)));
        }
        if (map != null) {
            try {
                if (map.size() > 0) {
                    for (String str2 : map.keySet()) {
                        metaInfo2.putToExtra(str2, map.get(str2));
                    }
                }
            } catch (Throwable unused) {
            }
        }
        xmPushActionContainerGenerateRequestContainer.setMetaInfo(metaInfo2);
        return xmPushActionContainerGenerateRequestContainer;
    }

    /* JADX WARN: Code duplicated, block: B:30:0x006a  */
    private static void processMIPushMessage(XMPushService xMPushService, byte[] bArr, long j2, Map<String, String> map) {
        byte[] bArr2;
        Map<String, String> extra;
        Map<String, String> extra2;
        byte[] bArrConvertThriftObjectToBytes;
        XmPushActionContainer xmPushActionContainerBuildContainer = buildContainer(bArr);
        if (xmPushActionContainerBuildContainer == null) {
            return;
        }
        if (TextUtils.isEmpty(xmPushActionContainerBuildContainer.packageName)) {
            MyLog.w("receive a mipush message without package name");
            return;
        }
        PushMetaInfo metaInfo = xmPushActionContainerBuildContainer.getMetaInfo();
        if (metaInfo == null || map == null || map.isEmpty() || (extra2 = metaInfo.getExtra()) == null || extra2.isEmpty()) {
            bArr2 = bArr;
        } else {
            boolean z2 = false;
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (extra2.containsKey(entry.getKey())) {
                    extra2.put(entry.getKey(), entry.getValue());
                    z2 = true;
                }
            }
            if (!z2 || (bArrConvertThriftObjectToBytes = XmPushThriftSerializeUtils.convertThriftObjectToBytes(xmPushActionContainerBuildContainer)) == null || bArrConvertThriftObjectToBytes.length <= 0) {
                bArr2 = bArr;
            } else {
                bArr2 = bArrConvertThriftObjectToBytes;
            }
        }
        long jCurrentTimeMillis = System.currentTimeMillis();
        Intent intentBuildIntent = buildIntent(bArr2, jCurrentTimeMillis);
        String targetPackage = MIPushNotificationHelper.getTargetPackage(xmPushActionContainerBuildContainer);
        TrafficUtils.distributionTraffic(xMPushService, targetPackage, j2, true, true, System.currentTimeMillis());
        if (metaInfo != null && metaInfo.getId() != null) {
            MyLog.persist(String.format("receive a message. appid=%1$s, msgid= %2$s, action=%3$s", xmPushActionContainerBuildContainer.getAppid(), PacketHelper.filterRegistrationPacketIdForLog(metaInfo.getId()), xmPushActionContainerBuildContainer.getAction()));
        }
        if (metaInfo != null) {
            metaInfo.putToExtra(PushConstants.MESSAGE_RECEIVE_TIME, Long.toString(jCurrentTimeMillis));
        }
        ActionType actionType = ActionType.SendMessage;
        String id = "";
        if (actionType == xmPushActionContainerBuildContainer.getAction() && MIPushAppInfo.getInstance(xMPushService).isUnRegistered(xmPushActionContainerBuildContainer.packageName) && !MIPushNotificationHelper.isBusinessMessage(xmPushActionContainerBuildContainer)) {
            if (metaInfo != null) {
                id = metaInfo.getId();
                if (MIPushNotificationHelper.isNPBMessage(xmPushActionContainerBuildContainer)) {
                    PushClientReportManager.getInstance(xMPushService.getApplicationContext()).reportEvent4NeedDrop(xmPushActionContainerBuildContainer.getPackageName(), MIPushNotificationHelper.getInterfaceId(xmPushActionContainerBuildContainer), id, "1");
                }
            }
            MyLog.w("Drop a message for unregistered, msgid=" + id);
            sendAppAbsentAck(xMPushService, xmPushActionContainerBuildContainer, xmPushActionContainerBuildContainer.packageName);
            return;
        }
        if (actionType == xmPushActionContainerBuildContainer.getAction() && MIPushAppInfo.getInstance(xMPushService).isPushDisabled4User(xmPushActionContainerBuildContainer.packageName) && !MIPushNotificationHelper.isBusinessMessage(xmPushActionContainerBuildContainer)) {
            if (metaInfo != null) {
                id = metaInfo.getId();
                if (MIPushNotificationHelper.isNPBMessage(xmPushActionContainerBuildContainer)) {
                    PushClientReportManager.getInstance(xMPushService.getApplicationContext()).reportEvent4NeedDrop(xmPushActionContainerBuildContainer.getPackageName(), MIPushNotificationHelper.getInterfaceId(xmPushActionContainerBuildContainer), id, "2");
                }
            }
            MyLog.w("Drop a message for push closed, msgid=" + id);
            sendAppAbsentAck(xMPushService, xmPushActionContainerBuildContainer, xmPushActionContainerBuildContainer.packageName);
            return;
        }
        if (actionType == xmPushActionContainerBuildContainer.getAction() && !TextUtils.equals(xMPushService.getPackageName(), PushConstants.PUSH_SERVICE_PACKAGE_NAME) && !TextUtils.equals(xMPushService.getPackageName(), xmPushActionContainerBuildContainer.packageName)) {
            MyLog.w("Receive a message with wrong package name, expect " + xMPushService.getPackageName() + ", received " + xmPushActionContainerBuildContainer.packageName);
            sendErrorAck(xMPushService, xmPushActionContainerBuildContainer, "unmatched_package", "package should be " + xMPushService.getPackageName() + ", but got " + xmPushActionContainerBuildContainer.packageName);
            if (metaInfo == null || !MIPushNotificationHelper.isNPBMessage(xmPushActionContainerBuildContainer)) {
                return;
            }
            PushClientReportManager.getInstance(xMPushService.getApplicationContext()).reportEvent4NeedDrop(xmPushActionContainerBuildContainer.getPackageName(), MIPushNotificationHelper.getInterfaceId(xmPushActionContainerBuildContainer), metaInfo.getId(), "3");
            return;
        }
        if (actionType != xmPushActionContainerBuildContainer.getAction() || DeviceInfo.getSpaceId() != 999 || !DeviceInfo.isDualAppUninstalled(xMPushService, targetPackage)) {
            if (metaInfo == null || (extra = metaInfo.getExtra()) == null || !extra.containsKey("hide") || !"true".equalsIgnoreCase(extra.get("hide"))) {
                postProcessMIPushMessage(xMPushService, targetPackage, bArr2, intentBuildIntent);
                return;
            } else {
                sendAckMessage(xMPushService, xmPushActionContainerBuildContainer);
                return;
            }
        }
        MyLog.w("Receive the uninstalled dual app message");
        try {
            MIPushHelper.sendPacket(xMPushService, MIPushHelper.contructAppAbsentMessage(targetPackage, xmPushActionContainerBuildContainer.getAppid()));
            MyLog.w("uninstall " + targetPackage + " msg sent");
        } catch (XMPPException e2) {
            MyLog.e("Fail to send Message: " + e2.getMessage());
            xMPushService.disconnect(10, e2);
        }
        MIPushNotificationHelper.clearNotification(xMPushService, targetPackage);
    }

    public void processNewPacket(XMPushService xMPushService, Blob blob, PushClientsManager.ClientLoginInfo clientLoginInfo) {
        HashMap map;
        try {
            byte[] decryptedPayload = blob.getDecryptedPayload(clientLoginInfo.security);
            if (ChannelConstants.isMIPUSHStatTime(blob)) {
                map = new HashMap();
                map.put(PushConstants.MESSAGE_ACK_FE_TIME, String.valueOf(blob.getTimestamp()));
                map.put(PushConstants.MESSAGE_ACK_RECEIVE_TIME, String.valueOf(blob.getCreateTime()));
            } else {
                map = null;
            }
            processMIPushMessage(xMPushService, decryptedPayload, blob.getSerializedSize(), map);
        } catch (IllegalArgumentException e2) {
            MyLog.e(e2);
        }
    }
}
