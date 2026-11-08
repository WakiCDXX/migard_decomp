package com.xiaomi.push.service;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.xiaomi.channel.commonutils.android.AppInfoUtils;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.channel.commonutils.android.ReceiverHelper;
import com.xiaomi.push.clientreport.PowerStatsHelper;
import com.xiaomi.push.crash.CrashManager;
import com.xiaomi.push.extension.AppInfoAgentImpl;
import com.xiaomi.push.extension.LogAgentImpl;
import com.xiaomi.push.headsup.HeadsUpManager;
import com.xiaomi.push.headsup.HeadsUpNotification;
import com.xiaomi.push.log.LogAgent;
import com.xiaomi.push.notificationlisten.XMSFNotificationListener;
import com.xiaomi.push.pulldown.PullDownManager;
import com.xiaomi.push.scene.ScenePushManager;
import com.xiaomi.push.service.notification.BuilderCompat;
import com.xiaomi.push.service.notification.StatusBarNotificationWrapper;
import com.xiaomi.push.sweetnotification.SweetCardBuilder;
import com.xiaomi.push.sweetnotification.SweetNotificationHelper;
import com.xiaomi.push.tts.TtsManager;
import com.xiaomi.push.voip.VoipManager;
import com.xiaomi.xmpush.thrift.NotificationType;
import com.xiaomi.xmpush.thrift.XmPushActionContainer;
import com.xiaomi.xmpush.thrift.XmPushActionNotification;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class ExtensionPluginsInit {
    public static void onApplicationCreate(final Context context) {
        if (MIUIUtils.isXMSF(context)) {
            CrashManager.listenCrash(context);
            final HeadsUpManager.BuilderFactory builderFactory = new HeadsUpManager.BuilderFactory() { // from class: com.xiaomi.push.service.ExtensionPluginsInit.1
                @Override // com.xiaomi.push.headsup.HeadsUpManager.BuilderFactory
                public HeadsUpNotification.Builder createCustomBuilder(int i2, int i3) {
                    if (i2 == 0) {
                        return ScenePushManager.createHeadsUpBuilder(context, i3);
                    }
                    if (i2 != 1) {
                        return null;
                    }
                    return VoipManager.createHeadsUpBuilder(context, i3);
                }
            };
            MIPushNotificationHelper.setNotificationAgent(new NotificationAgent() { // from class: com.xiaomi.push.service.ExtensionPluginsInit.2
                @Override // com.xiaomi.push.service.NotificationAgent
                public void clearHeadsUpNotification(String str) {
                    HeadsUpManager.getInstance(context).clearHeadsUpNotification(str);
                }

                @Override // com.xiaomi.push.service.NotificationAgent
                public boolean handleNotify(Map<String, String> map, int i2, Notification notification) {
                    return HeadsUpManager.getInstance(context).handleNotify(map, i2, notification, builderFactory);
                }

                @Override // com.xiaomi.push.service.NotificationAgent
                void onBeforeNotify(XmPushActionContainer xmPushActionContainer, Map<String, String> map, int i2, Notification notification) {
                    SweetNotificationHelper.onBeforeNotify(context, map, i2, notification);
                    ScenePushManager.onBeforeNotify(context, map);
                    VoipManager.onBeforeNotify(context, map, i2, notification);
                    TtsManager.onBeforeNotify(context, xmPushActionContainer, i2, notification);
                }

                @Override // com.xiaomi.push.service.NotificationAgent
                public BuilderCompat onCustomNotificationBuilder(Context context2, int i2, String str, Map<String, String> map) {
                    if (map == null) {
                        return null;
                    }
                    String str2 = map.get(MIPushNotificationHelper.NOTIFICATION_STYLE_TYPE);
                    if ("5".equals(str2)) {
                        return new SweetCardBuilder(context2, i2, str);
                    }
                    if ("6".equals(str2)) {
                        return VoipManager.createNotificationBuilder(context2, i2, str, map);
                    }
                    return null;
                }

                @Override // com.xiaomi.push.service.NotificationAgent
                public boolean shouldBlockNotification(Context context2, int i2, String str, Map<String, String> map) {
                    return SweetNotificationHelper.shouldDropSweetNotification(context2, i2, str, map) || VoipManager.shouldDropVoipNotification(context2, str, map);
                }
            });
            ChannelPushReportAgent.setReceiveAgent(new ChannelPushReportAgent.ReceiveAgent() { // from class: com.xiaomi.push.service.ExtensionPluginsInit.3
                @Override // com.xiaomi.push.service.ChannelPushReportAgent.ReceiveAgent
                public boolean receive(XmPushActionNotification xmPushActionNotification) {
                    if (NotificationType.ScenePushRuleUpdate.value.equals(xmPushActionNotification.type)) {
                        ScenePushManager.handleServerUpdate(context, xmPushActionNotification);
                    } else {
                        if (!NotificationType.ScenePushRuleDelete.value.equals(xmPushActionNotification.type)) {
                            return false;
                        }
                        ScenePushManager.handleServerDelete(context, xmPushActionNotification);
                    }
                    return true;
                }
            });
            MIPushEventProcessorAgent.setPullDownListener(new MIPushEventProcessorAgent.PullDownListener() { // from class: com.xiaomi.push.service.ExtensionPluginsInit.4
                @Override // com.xiaomi.push.service.MIPushEventProcessorAgent.PullDownListener
                public void clearMessage(XmPushActionContainer xmPushActionContainer) {
                    PullDownManager.clearMessage(context, xmPushActionContainer);
                }

                @Override // com.xiaomi.push.service.MIPushEventProcessorAgent.PullDownListener
                public boolean handleReceiveMessage(XmPushActionContainer xmPushActionContainer) {
                    return PullDownManager.handleReceiveMessage(context, xmPushActionContainer);
                }

                @Override // com.xiaomi.push.service.MIPushEventProcessorAgent.PullDownListener
                public void clearMessage(String str) {
                    PullDownManager.cancelMessage(context, str);
                }
            });
            PingPongCallbackAgent.setOnPingPongListener(context, new PingPongCallbackAgent.OnPingPongListener() { // from class: com.xiaomi.push.service.ExtensionPluginsInit.5
                @Override // com.xiaomi.push.service.PingPongCallbackAgent.OnPingPongListener
                public void onPong() {
                    CrashManager.getInstance(context).onPong();
                }
            });
            MIPushEventProcessorAgent.setEventProcessorListener(new MIPushEventProcessorAgent.EventProcessorListener() { // from class: com.xiaomi.push.service.ExtensionPluginsInit.6
                @Override // com.xiaomi.push.service.MIPushEventProcessorAgent.EventProcessorListener
                public Map<String, String> fillParamsInACK(Context context2, XmPushActionContainer xmPushActionContainer) {
                    return TtsManager.putParamsToACK(context2, xmPushActionContainer);
                }

                @Override // com.xiaomi.push.service.MIPushEventProcessorAgent.EventProcessorListener
                public void handleMessage(Context context2, XmPushActionContainer xmPushActionContainer) {
                    TtsManager.handleMessage(context2, xmPushActionContainer);
                }

                @Override // com.xiaomi.push.service.MIPushEventProcessorAgent.EventProcessorListener
                public void setAppNotificationPermission(Context context2, XmPushActionContainer xmPushActionContainer, XmPushActionNotification xmPushActionNotification) {
                    AppPermissionOperator.setAppNotificationPermission(context2, xmPushActionContainer, xmPushActionNotification);
                }

                @Override // com.xiaomi.push.service.MIPushEventProcessorAgent.EventProcessorListener
                public boolean shouldHandleMessage(Context context2, XmPushActionContainer xmPushActionContainer, boolean z2) {
                    return TtsManager.shouldHandleMessage(xmPushActionContainer, z2);
                }
            });
            AppInfoUtils.setAppInfoAgent(new AppInfoAgentImpl());
            LogAgent.setLogAgent(new LogAgentImpl());
            PullDownManager.start(context);
            ScenePushManager.start(context);
            VoipManager.init(context);
            registerCommonReceiver(context);
            XMSFNotificationListener.getInstance(context).addCallback(new XMSFNotificationListener.Callback() { // from class: com.xiaomi.push.service.ExtensionPluginsInit.7
                @Override // com.xiaomi.push.notificationlisten.XMSFNotificationListener.Callback
                public void onNotificationPosted(Context context2, StatusBarNotificationWrapper statusBarNotificationWrapper, int i2) {
                    if (i2 == 0) {
                        ExtensionPluginsInit.onNotificationPosted(context2, statusBarNotificationWrapper);
                        TtsManager.onNotificationPosted(context2, statusBarNotificationWrapper);
                    }
                    UploadNotificationDatas.onNotificationPosted(context2, statusBarNotificationWrapper, i2);
                }

                @Override // com.xiaomi.push.notificationlisten.XMSFNotificationListener.Callback
                public void onNotificationRemoved(Context context2, StatusBarNotificationWrapper statusBarNotificationWrapper, int i2, int i3) {
                    if (i3 != 0) {
                        UploadNotificationDatas.onNotificationRemoved(context2, statusBarNotificationWrapper, i2);
                    } else {
                        ExtensionPluginsInit.onNotificationRemoved(context2, statusBarNotificationWrapper, i2);
                        TtsManager.onNotificationRemoved(context2, statusBarNotificationWrapper, i2);
                    }
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void onNotificationPosted(Context context, StatusBarNotificationWrapper statusBarNotificationWrapper) {
        SweetNotificationHelper.onNotificationPosted(context, statusBarNotificationWrapper);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void onNotificationRemoved(Context context, StatusBarNotificationWrapper statusBarNotificationWrapper, int i2) {
        if (i2 != 3) {
            MIPushNotificationHelper.onNotificationRemoved(context, statusBarNotificationWrapper);
        }
        SweetNotificationHelper.onNotificationRemoved(context, statusBarNotificationWrapper, i2);
        if (VoipManager.onNotificationRemoved(context, statusBarNotificationWrapper, i2)) {
            return;
        }
        UploadNotificationDatas.onNotificationRemoved(context, statusBarNotificationWrapper, i2);
    }

    private static void registerCommonReceiver(Context context) {
        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.SCREEN_OFF");
            intentFilter.addAction("android.intent.action.SCREEN_ON");
            intentFilter.addAction("android.intent.action.USER_PRESENT");
            ReceiverHelper.registerReceiver(context.getApplicationContext(), new BroadcastReceiver() { // from class: com.xiaomi.push.service.ExtensionPluginsInit.8
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context2, Intent intent) {
                    HeadsUpManager.getInstance(context2).onReceive(intent);
                    ScenePushManager.onReceive(context2, intent);
                    SweetNotificationHelper.onScreeanOn(context2, intent);
                    PullDownManager.onReceive(context2, intent);
                    PowerStatsHelper.handleScreenChanged(context2, intent);
                }
            }, intentFilter, 2);
        } catch (Exception unused) {
        }
    }
}
