package com.xiaomi.push.headsup;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.content.Context;
import android.content.Intent;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.SparseArray;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.channel.commonutils.android.ToolUtils;
import com.xiaomi.push.extension.QueuedWork;
import com.xiaomi.push.service.MIPushNotificationHelper;
import com.xiaomi.push.service.NotificationManagerHelper;
import com.xiaomi.push.service.NotificationUtils;
import com.xiaomi.push.service.PushServiceConstants;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
@TargetApi(28)
public class HeadsUpManager {
    private static volatile HeadsUpManager sInstance;
    private volatile HeadsUpNotification mLastSnoozedCache;
    private SparseArray<HeadsUpNotificationListener> mListeners;
    private NotificationPlayer mPlayer;
    private ViewDisplayController mViewDisplay;

    public interface BuilderFactory {
        HeadsUpNotification.Builder createCustomBuilder(int i2, int i3);
    }

    private HeadsUpManager() {
    }

    private void cancel(int i2) {
        HeadsUpLog.d("cancel notification " + i2);
        this.mViewDisplay.dismiss(i2);
        HeadsUpNotification headsUpNotification = this.mLastSnoozedCache;
        if (headsUpNotification == null || headsUpNotification.id != i2) {
            return;
        }
        this.mLastSnoozedCache = null;
        HeadsUpLog.d("cancel cache notification " + i2);
    }

    private void fillHeadsUpExtras(Map<String, String> map, Notification notification) {
        HeadsUpUtils.cpMapToNotification(map, notification, HeadsUpConstants.EXTRA_IS_HEADSUP_MODE);
        HeadsUpUtils.cpMapToNotification(map, notification, HeadsUpConstants.EXTRA_HEADSUP_STYLE);
        HeadsUpUtils.cpMapToNotification(map, notification, HeadsUpConstants.EXTRA_HEADSUP_TIMEOUT);
        HeadsUpUtils.cpMapToNotification(map, notification, HeadsUpConstants.EXTRA_HEADSUP_TITLE);
        HeadsUpUtils.cpMapToNotification(map, notification, HeadsUpConstants.EXTRA_HEADSUP_DESC);
        HeadsUpUtils.cpMapToNotification(map, notification, "mipush_custom_extra");
        HeadsUpUtils.cpMapToNotification(map, notification, HeadsUpConstants.EXTRA_HEADSUP_RESCHEDULE_DELAY_SECONDS);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Context getContext() {
        return this.mViewDisplay.getContext();
    }

    public static HeadsUpManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (HeadsUpManager.class) {
                try {
                    if (sInstance == null) {
                        if (isOsSupport()) {
                            sInstance = new HeadsUpManager(context.getApplicationContext());
                        } else {
                            sInstance = new HeadsUpManager();
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return sInstance;
    }

    private static boolean isOsSupport() {
        return true;
    }

    private boolean isSupport() {
        if (!MIUIUtils.isXMSF(getContext())) {
            HeadsUpLog.w("not support, because other app");
            return false;
        }
        if (!isOsSupport()) {
            HeadsUpLog.w("not support, because platform too old");
            return false;
        }
        if (!HeadsUpUtils.isDndModeAndDisableFloat(getContext())) {
            return true;
        }
        HeadsUpLog.w("not support, because is dnd mode disable float");
        return false;
    }

    private void notify(HeadsUpNotification headsUpNotification) {
        if (headsUpNotification != null && headsUpNotification.style >= 0) {
            this.mViewDisplay.show(headsUpNotification);
            return;
        }
        HeadsUpLog.w("notify fail, because params invalid: " + headsUpNotification);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void tryScheduleNotificationInQueued(HeadsUpNotification headsUpNotification) {
        NotificationManagerHelper notificationManagerHelperFrom = NotificationManagerHelper.from(getContext(), headsUpNotification.pkgName);
        List<StatusBarNotification> activeNotifications = notificationManagerHelperFrom.getActiveNotifications();
        if (activeNotifications == null || activeNotifications.size() <= 0) {
            return;
        }
        for (StatusBarNotification statusBarNotification : activeNotifications) {
            if (statusBarNotification.getNotification() != null && statusBarNotification.getId() == headsUpNotification.id && Boolean.parseBoolean(statusBarNotification.getNotification().extras.getString(HeadsUpConstants.EXTRA_IS_HEADSUP_MODE))) {
                notificationManagerHelperFrom.cancel(statusBarNotification.getId());
                HeadsUpLog.d("cancel and reschedule:" + statusBarNotification);
                HeadsUpNotification.Builder builderRecoverBuilder = HeadsUpNotification.Builder.recoverBuilder(getContext(), headsUpNotification);
                builderRecoverBuilder.setSuppressEffects(true);
                notify(builderRecoverBuilder.build());
                return;
            }
        }
    }

    public boolean canHeadsUp(Map<String, String> map, String str) {
        if (!isOsSupport()) {
            HeadsUpLog.w("canup not support");
            return false;
        }
        if (map == null || TextUtils.isEmpty(str)) {
            return false;
        }
        boolean z2 = Boolean.parseBoolean(map.get(HeadsUpConstants.EXTRA_IS_HEADSUP_MODE));
        boolean zCheckComponentSupportStyle = Boolean.parseBoolean(map.get(MIPushNotificationHelper.NOTIFICATION_USE_CLICKED_ACTIVITY)) ? HeadsUpUtils.checkComponentSupportStyle(getContext(), MIPushNotificationHelper.getClickedActivity(str), HeadsUpUtils.resolveHeadsUpStyle(map)) : false;
        int visualMiuiVersionCode = MIUIUtils.getVisualMiuiVersionCode(getContext());
        int i2 = ToolUtils.parseInt(map.get(HeadsUpConstants.EXTRA_HEADSUP_MIN_SUPPORT_MIUI), 12);
        HeadsUpLog.d("isHeadsUpMode:" + z2 + " isManifest:" + zCheckComponentSupportStyle + " visualMiuiVesion:" + visualMiuiVersionCode + " minSupportMiui:" + i2);
        return z2 && zCheckComponentSupportStyle && visualMiuiVersionCode >= i2 && isSupport();
    }

    public void clearHeadsUpNotification(String str) {
        if (isSupport()) {
            HeadsUpNotification currentCardInfo = this.mViewDisplay.getCurrentCardInfo();
            if (currentCardInfo == null || TextUtils.isEmpty(str) || !str.equals(currentCardInfo.pkgName)) {
                HeadsUpLog.d("not support to clear notification");
            } else {
                cancel(currentCardInfo.id);
            }
        }
    }

    public boolean handleNotify(Map<String, String> map, int i2, Notification notification, BuilderFactory builderFactory) {
        NotificationChannel notificationChannel;
        if (!isOsSupport()) {
            HeadsUpLog.w("notify not support");
            return false;
        }
        this.mPlayer.stop(getContext(), PushServiceConstants.EXTENSION_ELEMENT_NOTIFY);
        String targetPackage = NotificationUtils.getTargetPackage(notification);
        if (canHeadsUp(map, targetPackage)) {
            HeadsUpNotification.Builder builderCreateCustomBuilder = builderFactory != null ? builderFactory.createCustomBuilder(HeadsUpUtils.resolveHeadsUpStyle(map), HeadsUpUtils.resolveHeadsUpUiType(map)) : null;
            if (builderCreateCustomBuilder != null) {
                fillHeadsUpExtras(map, notification);
                builderCreateCustomBuilder.setId(i2);
                builderCreateCustomBuilder.setNotification(notification);
                HeadsUpNotification headsUpNotificationBuild = builderCreateCustomBuilder.build();
                if (ToolUtils.isKeyguardLocked(getContext())) {
                    int i3 = headsUpNotificationBuild.keyguardLockedMode;
                    if (i3 == 1) {
                        HeadsUpLog.w("discard this message in keyguard locked mode");
                        return true;
                    }
                    if (i3 == 2) {
                        HeadsUpLog.w("cache this message to wait keyguard unlocked");
                        HeadsUpNotification headsUpNotification = this.mLastSnoozedCache;
                        if (headsUpNotification != null) {
                            HeadsUpNotificationListener headsUpNotificationListener = this.mListeners.get(headsUpNotification.style);
                            if (headsUpNotificationListener != null) {
                                headsUpNotificationListener.onSnoozedNotificationReplaced(headsUpNotification, headsUpNotificationBuild);
                            }
                            HeadsUpNotificationListener headsUpNotificationListener2 = this.mListeners.get(-1);
                            if (headsUpNotificationListener2 != null && headsUpNotificationListener2 != headsUpNotificationListener) {
                                headsUpNotificationListener2.onSnoozedNotificationReplaced(headsUpNotification, headsUpNotificationBuild);
                            }
                        }
                        this.mLastSnoozedCache = headsUpNotificationBuild;
                        return false;
                    }
                    if (i3 == 3) {
                        HeadsUpLog.w("notify to statusbar because keyguard locked " + i2);
                        return false;
                    }
                }
                if (headsUpNotificationBuild.discardWhenChannelClosed && (notificationChannel = NotificationManagerHelper.from(getContext(), targetPackage).getNotificationChannel(headsUpNotificationBuild.f1747n.getChannelId())) != null && notificationChannel.getImportance() == 0) {
                    HeadsUpLog.w("discard this message, because channel closed");
                    return true;
                }
                notify(headsUpNotificationBuild);
                return true;
            }
        }
        HeadsUpLog.w("not notify by headsup:" + i2);
        cancel(i2);
        return false;
    }

    public void onReceive(Intent intent) {
        if (!isOsSupport()) {
            HeadsUpLog.w("receive not support");
            return;
        }
        if (intent == null) {
            return;
        }
        String action = intent.getAction();
        HeadsUpLog.d("receiver action:" + action);
        if ("android.intent.action.SCREEN_OFF".equals(action)) {
            this.mViewDisplay.keyguardLocked();
            return;
        }
        if ("android.intent.action.USER_PRESENT".equals(action)) {
            final HeadsUpNotification headsUpNotification = this.mLastSnoozedCache;
            this.mLastSnoozedCache = null;
            HeadsUpLog.d("reschedule hun:" + headsUpNotification);
            if (headsUpNotification == null) {
                return;
            }
            String str = (String) NotificationUtils.resolveNotification(headsUpNotification.f1747n, HeadsUpConstants.EXTRA_HEADSUP_RESCHEDULE_DELAY_SECONDS);
            long j2 = (long) (ToolUtils.parseFloat(str, 1.0f) * 1000.0f);
            HeadsUpLog.d("reschedule sec:" + str + " " + j2);
            QueuedWork.postDelayed(new Runnable() { // from class: com.xiaomi.push.headsup.HeadsUpManager.2
                @Override // java.lang.Runnable
                public void run() {
                    HeadsUpManager.this.tryScheduleNotificationInQueued(headsUpNotification);
                }
            }, j2);
        }
    }

    public void registerListener(int i2, HeadsUpNotificationListener headsUpNotificationListener) {
        if (!isOsSupport()) {
            HeadsUpLog.w("register not support");
        } else if (headsUpNotificationListener != null) {
            synchronized (HeadsUpNotificationListener.class) {
                this.mListeners.put(i2, headsUpNotificationListener);
            }
        }
    }

    public void unregisterListener(int i2) {
        if (!isOsSupport()) {
            HeadsUpLog.w("unregister not support");
        } else {
            synchronized (HeadsUpNotificationListener.class) {
                this.mListeners.remove(i2);
            }
        }
    }

    private HeadsUpManager(Context context) {
        this.mListeners = new SparseArray<>();
        ViewDisplayController viewDisplayController = new ViewDisplayController(context);
        this.mViewDisplay = viewDisplayController;
        viewDisplayController.setOnVisibilityChangedListener(new ViewDisplayController.OnVisibilityChangedListener() { // from class: com.xiaomi.push.headsup.HeadsUpManager.1
            @Override // com.xiaomi.push.headsup.ViewDisplayController.OnVisibilityChangedListener
            public void onDismiss(int i2, HeadsUpNotification headsUpNotification) {
                HeadsUpManager.this.mPlayer.stop(HeadsUpManager.this.getContext(), "dismiss");
                HeadsUpReport.reportCancel(headsUpNotification, i2);
                if (headsUpNotification != null) {
                    HeadsUpNotificationListener headsUpNotificationListener = (HeadsUpNotificationListener) HeadsUpManager.this.mListeners.get(headsUpNotification.style);
                    if (headsUpNotificationListener != null) {
                        headsUpNotification.actionAfterRemoved = headsUpNotificationListener.onNotificationRemoved(headsUpNotification, i2);
                    }
                    HeadsUpNotificationListener headsUpNotificationListener2 = (HeadsUpNotificationListener) HeadsUpManager.this.mListeners.get(-1);
                    if (headsUpNotificationListener2 == null || headsUpNotificationListener2 == headsUpNotificationListener) {
                        return;
                    }
                    headsUpNotificationListener2.onNotificationRemoved(headsUpNotification, i2);
                }
            }

            @Override // com.xiaomi.push.headsup.ViewDisplayController.OnVisibilityChangedListener
            public void onShow(HeadsUpNotification headsUpNotification) {
                HeadsUpManager.this.mPlayer.play(HeadsUpManager.this.getContext(), headsUpNotification);
                HeadsUpReport.reportShow(headsUpNotification);
                if (headsUpNotification != null) {
                    HeadsUpNotificationListener headsUpNotificationListener = (HeadsUpNotificationListener) HeadsUpManager.this.mListeners.get(headsUpNotification.style);
                    if (headsUpNotificationListener != null) {
                        headsUpNotificationListener.onNotificationPosted(headsUpNotification);
                    }
                    HeadsUpNotificationListener headsUpNotificationListener2 = (HeadsUpNotificationListener) HeadsUpManager.this.mListeners.get(-1);
                    if (headsUpNotificationListener2 == null || headsUpNotificationListener2 == headsUpNotificationListener) {
                        return;
                    }
                    headsUpNotificationListener2.onNotificationPosted(headsUpNotification);
                }
            }
        });
        this.mPlayer = new NotificationPlayer();
    }
}
