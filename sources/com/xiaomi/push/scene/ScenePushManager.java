package com.xiaomi.push.scene;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.channel.commonutils.android.ToolUtils;
import com.xiaomi.push.extension.QueuedWork;
import com.xiaomi.push.headsup.HeadsUpConstants;
import com.xiaomi.push.headsup.HeadsUpManager;
import com.xiaomi.push.headsup.HeadsUpNotification;
import com.xiaomi.push.headsup.HeadsUpNotificationListener;
import com.xiaomi.push.service.ChannelPushReportAgent;
import com.xiaomi.push.service.NotificationManagerHelper;
import com.xiaomi.push.service.NotificationUtils;
import com.xiaomi.push.service.PacketHelper;
import com.xiaomi.xmpush.thrift.NotificationType;
import com.xiaomi.xmpush.thrift.XmPushActionNotification;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class ScenePushManager {
    private static Context sAppContext;
    private static volatile ScenePushManager sInstance;
    private SceneDataCacher mDataCacher;
    private final ForegroundScene mForegroundScene;
    private final NoneScene mNoneScene;

    private ScenePushManager(Context context) {
        sAppContext = context.getApplicationContext();
        this.mDataCacher = new SceneDataCacher(context);
        AbsScene.OnTriggerSceneCallback onTriggerSceneCallback = new AbsScene.OnTriggerSceneCallback() { // from class: com.xiaomi.push.scene.ScenePushManager.1
            @Override // com.xiaomi.push.scene.AbsScene.OnTriggerSceneCallback
            public void onTrigger(final int i2, final String str) {
                if (SceneLog.DEBUG.booleanValue()) {
                    SceneLog.i("onTrigger:" + i2 + " triggerConstraint:" + str);
                }
                QueuedWork.post(new Runnable() { // from class: com.xiaomi.push.scene.ScenePushManager.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        ScenePushManager.this.triggerSceneInQueued(i2, str);
                    }
                });
            }
        };
        this.mNoneScene = new NoneScene(0, onTriggerSceneCallback);
        this.mForegroundScene = new ForegroundScene(1, onTriggerSceneCallback);
        HeadsUpManager.getInstance(context).registerListener(0, new HeadsUpNotificationListener() { // from class: com.xiaomi.push.scene.ScenePushManager.2
            @Override // com.xiaomi.push.headsup.HeadsUpNotificationListener
            public HeadsUpConstants.ActionAfterRemoved onNotificationRemoved(HeadsUpNotification headsUpNotification, int i2) {
                if (i2 != 2 && i2 != 4) {
                    return HeadsUpConstants.ActionAfterRemoved.DidRemoved;
                }
                ScenePushManager.this.recoverToStatusBarInQueued(headsUpNotification);
                return HeadsUpConstants.ActionAfterRemoved.RecoverToStatusBar;
            }

            @Override // com.xiaomi.push.headsup.HeadsUpNotificationListener
            public void onNotificationPosted(HeadsUpNotification headsUpNotification) {
            }
        });
    }

    public static HeadsUpNotification.Builder createHeadsUpBuilder(Context context, int i2) {
        return new HeadsUpNotification.Builder(context).setSuppressEffects(true).setKeyguardLockedMode(3);
    }

    static Context getContext() {
        if (sAppContext == null) {
            SceneLog.w("Warn: called by static method, context is null!");
        }
        return sAppContext;
    }

    private AbsScene getScene(int i2) {
        return i2 != 1 ? this.mNoneScene : this.mForegroundScene;
    }

    public static void handleServerDelete(final Context context, XmPushActionNotification xmPushActionNotification) {
        Map<String, String> map;
        if (!MIUIUtils.isXMSF(context) || xmPushActionNotification == null || (map = xmPushActionNotification.extra) == null) {
            return;
        }
        final String str = map.get("ruleIds");
        if (TextUtils.isEmpty(str)) {
            SceneLog.w("receive server request delete, but ruleIds is empty.");
            return;
        }
        if (SceneLog.DEBUG.booleanValue()) {
            SceneLog.i("delete ruleIds:" + str);
        }
        QueuedWork.post(new Runnable() { // from class: com.xiaomi.push.scene.ScenePushManager.7
            @Override // java.lang.Runnable
            public void run() {
                ScenePushManager.singleton(context).mDataCacher.removeAll(ToolUtils.asList(str, ","));
            }
        });
    }

    public static void handleServerUpdate(final Context context, XmPushActionNotification xmPushActionNotification) {
        Map<String, String> map;
        if (!MIUIUtils.isXMSF(context) || xmPushActionNotification == null || (map = xmPushActionNotification.extra) == null) {
            return;
        }
        final String str = map.get("rules");
        if (TextUtils.isEmpty(str)) {
            SceneLog.w("receive server request update, but json is empty.");
            return;
        }
        final boolean z2 = Boolean.parseBoolean(xmPushActionNotification.extra.get("issueAll"));
        if (SceneLog.DEBUG.booleanValue()) {
            SceneLog.i(z2 + " update json:" + str);
        }
        QueuedWork.post(new Runnable() { // from class: com.xiaomi.push.scene.ScenePushManager.6
            @Override // java.lang.Runnable
            public void run() {
                ScenePushManager scenePushManagerSingleton = ScenePushManager.singleton(context);
                scenePushManagerSingleton.mDataCacher.putJson(str, z2);
                scenePushManagerSingleton.tryListenAllSceneInQueued();
            }
        });
    }

    private boolean isNoneScene(int i2) {
        return getScene(i2) == this.mNoneScene;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void listenOrUnlistenAllSceneInQueued(boolean z2) {
        List<SceneDataCacher.Rule> all = this.mDataCacher.getAll();
        if (ToolUtils.isEmptyList(all)) {
            return;
        }
        for (SceneDataCacher.Rule rule : all) {
            if (SceneLog.DEBUG.booleanValue()) {
                SceneLog.i("listenOrUnlisten all " + z2 + " " + rule.getSceneType());
            }
            AbsScene scene = getScene(rule.getSceneType());
            if (z2) {
                scene.listen();
            } else {
                scene.unlisten();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadCacheInQueued() {
        this.mDataCacher.load();
        tryListenAllSceneInQueued();
    }

    public static void onBeforeNotify(final Context context, Map<String, String> map) {
        if (!MIUIUtils.isXMSF(context) || map == null) {
            return;
        }
        final String str = map.get(ScenePushConstants.EXTRA_SCENE_RULE_ID);
        if (SceneLog.DEBUG.booleanValue()) {
            SceneLog.i("handle notify ruleId is:" + str);
        }
        if (TextUtils.isEmpty(str)) {
            return;
        }
        QueuedWork.post(new Runnable() { // from class: com.xiaomi.push.scene.ScenePushManager.5
            @Override // java.lang.Runnable
            public void run() {
                ScenePushManager.singleton(context).mDataCacher.remove(str);
            }
        });
    }

    public static void onForegroundStateChanged(Context context, int i2, int i3, boolean z2) {
        if (MIUIUtils.isXMSF(context)) {
            singleton(context).mForegroundScene.judgeCondition(i2, i3, z2);
        }
    }

    public static void onReceive(final Context context, Intent intent) {
        if (!MIUIUtils.isXMSF(context) || intent == null) {
            return;
        }
        final String action = intent.getAction();
        if (SceneLog.DEBUG.booleanValue()) {
            SceneLog.i("receive action:" + action);
        }
        QueuedWork.post(new Runnable() { // from class: com.xiaomi.push.scene.ScenePushManager.8
            @Override // java.lang.Runnable
            public void run() {
                if ("android.intent.action.SCREEN_OFF".equals(action)) {
                    ScenePushManager.singleton(context).listenOrUnlistenAllSceneInQueued(false);
                } else if ("android.intent.action.USER_PRESENT".equals(action)) {
                    ScenePushManager.singleton(context).listenOrUnlistenAllSceneInQueued(true);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void recoverToStatusBarInQueued(final HeadsUpNotification headsUpNotification) {
        QueuedWork.post(new Runnable() { // from class: com.xiaomi.push.scene.ScenePushManager.4
            @Override // java.lang.Runnable
            @TargetApi(26)
            public void run() {
                NotificationManagerHelper notificationManagerHelperFrom = NotificationManagerHelper.from(ScenePushManager.getContext(), headsUpNotification.pkgName);
                Notification.Builder builderRecoverBuilder = Notification.Builder.recoverBuilder(ScenePushManager.getContext(), headsUpNotification.f1747n);
                NotificationUtils.suppressNotificationEffects(builderRecoverBuilder, false);
                notificationManagerHelperFrom.notify(headsUpNotification.id, builderRecoverBuilder.build());
                SceneLog.i("recover " + headsUpNotification + " to statusbar");
            }
        });
    }

    private boolean reportTriggerRules(List<SceneDataCacher.Rule> list) {
        StringBuilder sb = new StringBuilder();
        Iterator<SceneDataCacher.Rule> it = list.iterator();
        while (it.hasNext()) {
            sb.append(it.next().getRuleId());
            sb.append(",");
        }
        XmPushActionNotification xmPushActionNotification = new XmPushActionNotification();
        xmPushActionNotification.setType(NotificationType.ScenePushRuleTrigger.value);
        xmPushActionNotification.setId(PacketHelper.generatePacketID());
        xmPushActionNotification.setRequireAck(false);
        HashMap map = new HashMap();
        xmPushActionNotification.extra = map;
        map.put("ruleIds", sb.toString());
        xmPushActionNotification.extra.put("screenRotate", String.valueOf(SceneUtils.getScreenRotate(getContext())));
        xmPushActionNotification.extra.put("audioMode", String.valueOf(SceneUtils.getAudioMode(getContext())));
        if (SceneLog.DEBUG.booleanValue()) {
            SceneLog.i("report trigger ruleIds:" + xmPushActionNotification);
        }
        ChannelPushReportAgent.report(xmPushActionNotification);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static ScenePushManager singleton(Context context) {
        if (sInstance == null) {
            synchronized (ScenePushManager.class) {
                try {
                    if (sInstance == null) {
                        sInstance = new ScenePushManager(context);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return sInstance;
    }

    public static void start(final Context context) {
        SceneLog.i("scene push start.");
        QueuedWork.post(new Runnable() { // from class: com.xiaomi.push.scene.ScenePushManager.3
            @Override // java.lang.Runnable
            public void run() {
                ScenePushManager.singleton(context).loadCacheInQueued();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void triggerSceneInQueued(int i2, String str) {
        List<SceneDataCacher.Rule> listFind = this.mDataCacher.find(i2);
        if (ToolUtils.isEmptyList(listFind)) {
            getScene(i2).unlisten();
            SceneLog.i("triggerSceneInQueued unlisten " + i2);
            return;
        }
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (SceneDataCacher.Rule rule : listFind) {
            if (rule.canTrigger(str)) {
                arrayList2.add(rule);
                SceneLog.i("do trigger..." + rule);
            } else if (rule.isExpired()) {
                arrayList.add(rule.key());
                SceneLog.i("do expired..." + rule);
            } else {
                SceneLog.i("do ignored..." + rule);
            }
        }
        if (!ToolUtils.isEmptyList(arrayList)) {
            this.mDataCacher.removeAll(arrayList);
        }
        if (ToolUtils.isEmptyList(arrayList2) || !reportTriggerRules(arrayList2)) {
            return;
        }
        this.mDataCacher.recordTrigger(arrayList2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void tryListenAllSceneInQueued() {
        if (!ToolUtils.isKeyguardLocked(getContext())) {
            listenOrUnlistenAllSceneInQueued(true);
        } else if (SceneLog.DEBUG.booleanValue()) {
            SceneLog.i("try listen all scene forbidden, because keyguard locked.");
        }
    }
}
