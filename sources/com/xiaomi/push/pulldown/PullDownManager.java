package com.xiaomi.push.pulldown;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.channel.commonutils.android.ToolUtils;
import com.xiaomi.push.extension.PluginConstants;
import com.xiaomi.push.extension.QueuedWork;
import com.xiaomi.push.service.ChannelPushReportAgent;
import com.xiaomi.push.service.MIPushEventProcessor;
import com.xiaomi.push.service.MIPushNotificationHelper;
import com.xiaomi.push.service.NotificationUtils;
import com.xiaomi.xmpush.thrift.NotificationType;
import com.xiaomi.xmpush.thrift.XmPushActionContainer;
import com.xiaomi.xmpush.thrift.XmPushActionNotification;
import com.xiaomi.xmpush.thrift.XmPushThriftSerializeUtils;
import java.util.HashMap;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class PullDownManager implements PullDownTask.OnTaskExpired, PullDownScene.OnTriggerPullDownCallback {
    private static int MAX_SIZE = 5;
    private static final long dataCheckTime = 300000;
    private static Context sAppContext = null;
    private static volatile PullDownManager sInstance = null;
    private static volatile long sSceenOnTime = -1;
    private PullDownCacheQueue mPullDownCacheQueue;
    private PullDownScene mPullDownScene;

    private PullDownManager(Context context) {
        sAppContext = context;
        this.mPullDownCacheQueue = new PullDownCacheQueue(context);
        PullDownTask.setsOnTaskExpired(this);
        this.mPullDownScene = new PullDownScene(context, this);
    }

    public static void cancelMessage(final Context context, final String str) {
        PullDownLog.w("PullDownManager cancelMessage enter");
        QueuedWork.post(new Runnable() { // from class: com.xiaomi.push.pulldown.PullDownManager.4
            @Override // java.lang.Runnable
            public void run() throws Throwable {
                PullDownManager pullDownManagerSingleton = PullDownManager.singleton(context);
                List<XmPushActionContainer> all = pullDownManagerSingleton.mPullDownCacheQueue.getAll();
                if (ToolUtils.isEmptyList(all)) {
                    return;
                }
                for (int i2 = 0; i2 < all.size(); i2++) {
                    if (!TextUtils.isEmpty(MIPushNotificationHelper.getTargetPackage(all.get(i2))) && str.equals(MIPushNotificationHelper.getTargetPackage(all.get(i2)))) {
                        pullDownManagerSingleton.mPullDownCacheQueue.remove(all.get(i2));
                    }
                }
            }
        });
    }

    public static void clearMessage(final Context context, final XmPushActionContainer xmPushActionContainer) {
        PullDownLog.w("PullDownManager clearMessage enter");
        QueuedWork.post(new Runnable() { // from class: com.xiaomi.push.pulldown.PullDownManager.3
            @Override // java.lang.Runnable
            public void run() {
                PullDownManager.singleton(context).mPullDownCacheQueue.remove(xmPushActionContainer);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void constructAndNotifyMessage(XmPushActionContainer xmPushActionContainer, String str) {
        if (xmPushActionContainer == null || xmPushActionContainer.getMetaInfo() == null) {
            PullDownLog.w("null container");
            return;
        }
        xmPushActionContainer.getMetaInfo().putToExtra(PluginConstants.PULL_DOWN_POP_TYPE, str);
        PullDownLog.w("notifyMessage reason : " + str);
        MIPushEventProcessor.postNotifyPushMessage(sAppContext, xmPushActionContainer, XmPushThriftSerializeUtils.convertThriftObjectToBytes(xmPushActionContainer));
    }

    public static boolean handleReceiveMessage(final Context context, final XmPushActionContainer xmPushActionContainer) {
        if (!MIUIUtils.isXMSF(context) || !isPullDownMessage(xmPushActionContainer)) {
            return false;
        }
        boolean zIsSystemSupportMonitorPullDown = PullDownSupport.isSystemSupportMonitorPullDown(context);
        PullDownLog.w("cache pull down message");
        if (zIsSystemSupportMonitorPullDown) {
            QueuedWork.post(new Runnable() { // from class: com.xiaomi.push.pulldown.PullDownManager.2
                @Override // java.lang.Runnable
                public void run() throws Throwable {
                    PullDownManager pullDownManagerSingleton = PullDownManager.singleton(context);
                    if (!pullDownManagerSingleton.mPullDownCacheQueue.add(xmPushActionContainer, System.currentTimeMillis())) {
                        PullDownManager.constructAndNotifyMessage(xmPushActionContainer, "3");
                        return;
                    }
                    if (pullDownManagerSingleton.mPullDownCacheQueue.getSize() > PullDownManager.MAX_SIZE) {
                        XmPushActionContainer xmPushActionContainerPeek = pullDownManagerSingleton.mPullDownCacheQueue.peek();
                        if (xmPushActionContainerPeek == null || xmPushActionContainerPeek.getMetaInfo() == null) {
                            PullDownLog.w("PullDownManager handleReceiveMessage container is null");
                        } else {
                            PullDownManager.constructAndNotifyMessage(xmPushActionContainerPeek, "1");
                            pullDownManagerSingleton.mPullDownCacheQueue.remove(xmPushActionContainerPeek);
                            pullDownManagerSingleton.reportPopNotification("1", xmPushActionContainerPeek.getMetaInfo().getId(), xmPushActionContainerPeek.getAppid(), xmPushActionContainerPeek.getPackageName());
                        }
                    }
                    pullDownManagerSingleton.tryListenPullDownSceneInQueued();
                }
            });
            return true;
        }
        xmPushActionContainer.getMetaInfo().putToExtra(PluginConstants.PULL_DOWN_SUPPORT, "false");
        constructAndNotifyMessage(xmPushActionContainer, "4");
        return true;
    }

    private static boolean isPullDownMessage(XmPushActionContainer xmPushActionContainer) {
        if (xmPushActionContainer == null || xmPushActionContainer.getMetaInfo() == null || xmPushActionContainer.getMetaInfo().getExtra() == null) {
            return false;
        }
        return MIPushNotificationHelper.MSG_BUSI_TYPE_PULLDOWN.equals(NotificationUtils.getMsgBusinessType(xmPushActionContainer.getMetaInfo().getExtra()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void listenOrUnlistenPullDownScene(boolean z2) {
        if (this.mPullDownCacheQueue.getSize() <= 0 || !z2) {
            this.mPullDownScene.unlisten();
        } else {
            this.mPullDownScene.listen();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadCacheInQueued() throws Throwable {
        PullDownLog.w(" pdm loadCacheInQueued");
        this.mPullDownCacheQueue.load();
        tryListenPullDownSceneInQueued();
    }

    public static void onReceive(final Context context, Intent intent) {
        if (!MIUIUtils.isXMSF(context) || intent == null) {
            return;
        }
        final String action = intent.getAction();
        QueuedWork.post(new Runnable() { // from class: com.xiaomi.push.pulldown.PullDownManager.5
            @Override // java.lang.Runnable
            public void run() {
                if ("android.intent.action.SCREEN_OFF".equals(action)) {
                    PullDownManager.singleton(context).listenOrUnlistenPullDownScene(false);
                    return;
                }
                if ("android.intent.action.USER_PRESENT".equals(action)) {
                    PullDownManager.singleton(context).listenOrUnlistenPullDownScene(true);
                    if (System.currentTimeMillis() - PullDownManager.sSceenOnTime > 300000) {
                        PullDownTask.onTaskCheckExpired();
                        long unused = PullDownManager.sSceenOnTime = System.currentTimeMillis();
                        PullDownLog.w("PullDownManager start check data expired");
                    }
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reportPopNotification(String str, String str2, String str3, String str4) {
        if (TextUtils.isEmpty(str3) || TextUtils.isEmpty(str4)) {
            PullDownLog.w("msgId: " + str2 + " appId or pkgName is null ");
        }
        XmPushActionNotification xmPushActionNotification = new XmPushActionNotification();
        xmPushActionNotification.setType(NotificationType.PullDownPop.value);
        xmPushActionNotification.setId(str2);
        xmPushActionNotification.setAppId(str3);
        xmPushActionNotification.setPackageName(str4);
        xmPushActionNotification.setRequireAck(false);
        HashMap map = new HashMap();
        xmPushActionNotification.extra = map;
        map.put(PluginConstants.PULL_DOWN_POP_TYPE, str);
        ChannelPushReportAgent.report(xmPushActionNotification);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static PullDownManager singleton(Context context) {
        if (sInstance == null) {
            synchronized (PullDownManager.class) {
                try {
                    if (sInstance == null) {
                        sInstance = new PullDownManager(context);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return sInstance;
    }

    public static void start(final Context context) {
        PullDownLog.w(" pdm start");
        QueuedWork.post(new Runnable() { // from class: com.xiaomi.push.pulldown.PullDownManager.1
            @Override // java.lang.Runnable
            public void run() throws Throwable {
                PullDownManager.singleton(context).loadCacheInQueued();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void triggerPullDownScene() throws Throwable {
        XmPushActionContainer xmPushActionContainerPeek = this.mPullDownCacheQueue.peek();
        if (xmPushActionContainerPeek == null || xmPushActionContainerPeek.getMetaInfo() == null) {
            PullDownLog.w("PullDownManager triggerPullDownScene container is null");
            return;
        }
        constructAndNotifyMessage(xmPushActionContainerPeek, "0");
        this.mPullDownCacheQueue.remove(xmPushActionContainerPeek);
        reportPopNotification("0", xmPushActionContainerPeek.getMetaInfo().getId(), xmPushActionContainerPeek.getAppid(), xmPushActionContainerPeek.getPackageName());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void tryListenPullDownSceneInQueued() {
        if (!ToolUtils.isKeyguardLocked(sAppContext)) {
            listenOrUnlistenPullDownScene(true);
        } else if (PullDownLog.DEBUG.booleanValue()) {
            PullDownLog.i("start monitor forbidden, because keyguard locked.");
        }
    }

    @Override // com.xiaomi.push.pulldown.PullDownTask.OnTaskExpired
    public void onTaskExpired(XmPushActionContainer xmPushActionContainer) {
        if (xmPushActionContainer == null || xmPushActionContainer.getMetaInfo() == null) {
            PullDownLog.w("PullDownManager onTaskExpired container is null");
            return;
        }
        constructAndNotifyMessage(xmPushActionContainer, "2");
        this.mPullDownCacheQueue.remove(xmPushActionContainer);
        reportPopNotification("2", xmPushActionContainer.getMetaInfo().getId(), xmPushActionContainer.getAppid(), xmPushActionContainer.getPackageName());
    }

    @Override // com.xiaomi.push.pulldown.PullDownScene.OnTriggerPullDownCallback
    public void onTrigger() {
        QueuedWork.post(new Runnable() { // from class: com.xiaomi.push.pulldown.PullDownManager.6
            @Override // java.lang.Runnable
            public void run() throws Throwable {
                PullDownManager.this.triggerPullDownScene();
            }
        });
    }
}
