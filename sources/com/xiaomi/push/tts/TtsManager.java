package com.xiaomi.push.tts;

import android.app.Notification;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.AppInfoUtils;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.channel.commonutils.android.ToolUtils;
import com.xiaomi.push.extension.LogPools;
import com.xiaomi.push.extension.PluginConstants;
import com.xiaomi.push.service.MIPushNotificationHelper;
import com.xiaomi.push.service.NotificationUtils;
import com.xiaomi.push.service.notification.StatusBarNotificationWrapper;
import com.xiaomi.xmpush.thrift.XmPushActionContainer;
import com.xiaomi.xmpush.thrift.XmPushThriftSerializeUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

/* JADX INFO: loaded from: classes.dex */
public class TtsManager {
    private static volatile TtsManager sInstance;
    private static Timer sTimer;
    private ConcurrentHashMap<String, TtsSpeechBean> mPendingMap = new ConcurrentHashMap<>();
    private TtsSpeaker mSpeak;

    private TtsManager(Context context) {
        this.mSpeak = new TtsSpeaker(context);
        sTimer = new Timer();
    }

    private static boolean checkParamsValid(Context context, XmPushActionContainer xmPushActionContainer) {
        if (!MIUIUtils.isXMSF(context)) {
            LogPools.TtsLog.w("not from xmsf");
            return false;
        }
        if (xmPushActionContainer == null || xmPushActionContainer.getMetaInfo() == null || xmPushActionContainer.getMetaInfo().getExtra() == null) {
            LogPools.TtsLog.w("normal params are null");
            return false;
        }
        if (!TtsUtils.isChinaRegion(context)) {
            LogPools.TtsLog.w("not support global devices");
            return false;
        }
        Map<String, String> extra = xmPushActionContainer.getMetaInfo().getExtra();
        if (!TtsUtils.isTTSMessage(extra)) {
            return false;
        }
        String str = extra.get(PluginConstants.TTS_ENG);
        String str2 = extra.get(PluginConstants.TTS_ENG_VER);
        String str3 = extra.get(PluginConstants.TTS_VOLUME);
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2) || TextUtils.isEmpty(extra.get(PluginConstants.TTS_CONTENT)) || TextUtils.isEmpty(str3)) {
            LogPools.TtsLog.w("tts parameters are null");
            return false;
        }
        if (!TtsUtils.isEngineVerSupport(context, str, str2)) {
            LogPools.TtsLog.w("tts engine version not support");
            return false;
        }
        if (ToolUtils.parseInt(str3, 0) == 0) {
            LogPools.TtsLog.w("expected vol is 0");
            return false;
        }
        LogPools.TtsLog.w("tts params check ok.");
        return true;
    }

    public static void handleMessage(Context context, XmPushActionContainer xmPushActionContainer) {
        if (isSpeakIndependent(xmPushActionContainer)) {
            speak(context, xmPushActionContainer);
        }
    }

    private static boolean isSpeakIndependent(XmPushActionContainer xmPushActionContainer) {
        if (xmPushActionContainer == null || xmPushActionContainer.getMetaInfo() == null || xmPushActionContainer.getMetaInfo().getExtra() == null) {
            return false;
        }
        return Boolean.parseBoolean((String) NotificationUtils.getValue(xmPushActionContainer.getMetaInfo().getExtra(), PluginConstants.TTS_INDEPENDENT, "false"));
    }

    private static boolean isTtsMessage(XmPushActionContainer xmPushActionContainer) {
        if (xmPushActionContainer == null || xmPushActionContainer.getMetaInfo() == null || xmPushActionContainer.getMetaInfo().getExtra() == null) {
            return false;
        }
        return TtsUtils.isTTSMessage(xmPushActionContainer.getMetaInfo().getExtra());
    }

    public static void onBeforeNotify(final Context context, XmPushActionContainer xmPushActionContainer, int i2, Notification notification) {
        if (!checkParamsValid(context, xmPushActionContainer)) {
            if (context == null || !isTtsMessage(xmPushActionContainer) || MIPushNotificationHelper.isBusinessMessage(xmPushActionContainer)) {
                return;
            }
            TtsSpeaker.sendMessageBroadcast(context, MIPushNotificationHelper.getTargetPackage(xmPushActionContainer), XmPushThriftSerializeUtils.convertThriftObjectToBytes(xmPushActionContainer));
            return;
        }
        if (isSpeakIndependent(xmPushActionContainer)) {
            return;
        }
        final String strGenerateKey = TtsUtils.generateKey(MIPushNotificationHelper.getTargetPackage(xmPushActionContainer), i2);
        LogPools.TtsLog.w("msg wait notify");
        TtsSpeechBean ttsSpeechBean = singleton(context).mPendingMap.get(strGenerateKey);
        if (ttsSpeechBean != null && ttsSpeechBean.timeoutTask != null) {
            ttsSpeechBean.cancelTask();
        }
        TtsSpeechBean bean = parseBean(xmPushActionContainer);
        bean.key = strGenerateKey;
        bean.report.ttsAckTime = System.currentTimeMillis();
        bean.timeoutTask = new TtsTask() { // from class: com.xiaomi.push.tts.TtsManager.1
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                TtsSpeechBean ttsSpeechBean2 = (TtsSpeechBean) TtsManager.singleton(context).mPendingMap.remove(strGenerateKey);
                if (ttsSpeechBean2 != null) {
                    TtsManager.singleton(context).mSpeak.broadcastAndRemove(ttsSpeechBean2, PluginConstants.TTS_ERROR_CODE_NOTIFICATION_NOT_POSTED);
                }
            }
        };
        singleton(context).mPendingMap.put(strGenerateKey, bean);
        sTimer.schedule(bean.timeoutTask, PluginConstants.TTS_REMOVE_BEAN_TIME);
    }

    public static void onNotificationPosted(Context context, StatusBarNotificationWrapper statusBarNotificationWrapper) {
        if (MIUIUtils.isXMSF(context) && NotificationUtils.isNotificationFromXmsf(context, statusBarNotificationWrapper) && TtsUtils.isTTSMessage(statusBarNotificationWrapper.getNotification())) {
            TtsSpeechBean ttsSpeechBeanRemove = singleton(context).mPendingMap.remove(TtsUtils.generateKey(NotificationUtils.getTargetPackage(statusBarNotificationWrapper.getNotification()), statusBarNotificationWrapper.getId()));
            if (ttsSpeechBeanRemove != null) {
                LogPools.TtsLog.w("msg posted " + statusBarNotificationWrapper.getId());
                ttsSpeechBeanRemove.cancelTask();
                singleton(context).mSpeak.enqueue(ttsSpeechBeanRemove);
            }
        }
    }

    public static void onNotificationRemoved(Context context, StatusBarNotificationWrapper statusBarNotificationWrapper, int i2) {
        if (MIUIUtils.isXMSF(context) && NotificationUtils.isNotificationFromXmsf(context, statusBarNotificationWrapper) && TtsUtils.isTTSMessage(statusBarNotificationWrapper.getNotification()) && i2 != 10) {
            singleton(context).mSpeak.removeFromQueue(TtsUtils.generateKey(NotificationUtils.getTargetPackage(statusBarNotificationWrapper.getNotification()), statusBarNotificationWrapper.getId()));
        }
    }

    private static TtsSpeechBean parseBean(XmPushActionContainer xmPushActionContainer) {
        TtsSpeechBean ttsSpeechBean = new TtsSpeechBean();
        ttsSpeechBean.vol = xmPushActionContainer.getMetaInfo().getExtra().get(PluginConstants.TTS_VOLUME);
        ttsSpeechBean.content = xmPushActionContainer.getMetaInfo().getExtra().get(PluginConstants.TTS_CONTENT);
        ttsSpeechBean.engine = xmPushActionContainer.getMetaInfo().getExtra().get(PluginConstants.TTS_ENG);
        String str = xmPushActionContainer.getMetaInfo().getExtra().get(PluginConstants.TTS_PARAM_KEY);
        String str2 = xmPushActionContainer.getMetaInfo().getExtra().get(PluginConstants.TTS_PARAM_VALUE);
        Bundle bundle = new Bundle();
        if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2)) {
            bundle.putString(str, str2);
        }
        ttsSpeechBean.bundle = bundle;
        ttsSpeechBean.wakeLock = Boolean.parseBoolean(xmPushActionContainer.getMetaInfo().getExtra().get(PluginConstants.TTS_PARTIAL_WAKE_LOCK));
        ttsSpeechBean.serverExtra = xmPushActionContainer.getMetaInfo().getExtra();
        ttsSpeechBean.report.msgId = xmPushActionContainer.getMetaInfo().getId();
        ttsSpeechBean.report.appId = xmPushActionContainer.getAppid();
        ttsSpeechBean.report.disableReport = Boolean.parseBoolean(xmPushActionContainer.getMetaInfo().getExtra().get(PluginConstants.TTS_KEY_DISABLE_REPORT_FLAG));
        ttsSpeechBean.pkgName = MIPushNotificationHelper.getTargetPackage(xmPushActionContainer);
        ttsSpeechBean.isBusinessMessage = MIPushNotificationHelper.isBusinessMessage(xmPushActionContainer);
        ttsSpeechBean.decryptedContent = XmPushThriftSerializeUtils.convertThriftObjectToBytes(xmPushActionContainer);
        return ttsSpeechBean;
    }

    public static Map<String, String> putParamsToACK(Context context, XmPushActionContainer xmPushActionContainer) {
        if (isTtsMessage(xmPushActionContainer)) {
            String str = (String) NotificationUtils.getValue(xmPushActionContainer.getMetaInfo().getExtra(), PluginConstants.TTS_ENG, null);
            if (!TextUtils.isEmpty(str)) {
                HashMap map = new HashMap();
                map.put("evid", String.valueOf(AppInfoUtils.getVersionCode(context, str)));
                return map;
            }
        }
        return null;
    }

    public static boolean shouldHandleMessage(XmPushActionContainer xmPushActionContainer, boolean z2) {
        if (!isTtsMessage(xmPushActionContainer)) {
            return false;
        }
        if (isSpeakIndependent(xmPushActionContainer)) {
            return true;
        }
        return z2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static TtsManager singleton(Context context) {
        if (sInstance == null) {
            synchronized (TtsManager.class) {
                try {
                    if (sInstance == null) {
                        sInstance = new TtsManager(context);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return sInstance;
    }

    private static void speak(Context context, XmPushActionContainer xmPushActionContainer) {
        if (checkParamsValid(context, xmPushActionContainer)) {
            LogPools.TtsLog.w("msg speak independent");
            String targetPackage = MIPushNotificationHelper.getTargetPackage(xmPushActionContainer);
            String strGenerateKey = TtsUtils.generateKey(targetPackage, ToolUtils.generateRealID(targetPackage, xmPushActionContainer.metaInfo.notifyId));
            TtsSpeechBean bean = parseBean(xmPushActionContainer);
            bean.key = strGenerateKey;
            bean.independent = true;
            bean.report.ttsAckTime = System.currentTimeMillis();
            singleton(context).mSpeak.enqueue(bean);
        }
    }
}
