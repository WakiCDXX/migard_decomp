package com.xiaomi.push.service;

import android.app.Notification;
import android.content.Context;
import android.os.SystemClock;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.push.extension.PluginConstants;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/* JADX INFO: loaded from: classes.dex */
public class ButonClickRemoveNotificationHelper {
    private static final int MAX_DURATION_MILLIS = 5000;
    private static final int MAX_SIZE = 10;
    private static List<ButtonClickedCacheItem> sButtonClickedNotificationKeys = new CopyOnWriteArrayList();

    private static class ButtonClickedCacheItem {
        public final Notification.Action[] actions;
        public final int buttonPlace;
        public final long cacheTime;
        public final String notificationKey;

        ButtonClickedCacheItem(String str, long j2, int i2, Notification.Action[] actionArr) {
            this.notificationKey = str;
            this.cacheTime = j2;
            this.buttonPlace = i2;
            this.actions = actionArr;
        }
    }

    private static void addCacheItemToTail(ButtonClickedCacheItem buttonClickedCacheItem) {
        sButtonClickedNotificationKeys.add(buttonClickedCacheItem);
        kickOverCache();
    }

    protected static void appRemovedNotification(Context context, StatusBarNotification statusBarNotification, int i2) {
        if (!MIUIUtils.isXMSF(context) || i2 <= 0 || statusBarNotification == null) {
            return;
        }
        addCacheItemToTail(new ButtonClickedCacheItem(statusBarNotification.getKey(), SystemClock.elapsedRealtime(), i2, NotificationUtils.resolveActions(statusBarNotification.getNotification())));
    }

    private static void kickOverCache() {
        for (int size = sButtonClickedNotificationKeys.size() - 1; size >= 0; size--) {
            ButtonClickedCacheItem buttonClickedCacheItem = sButtonClickedNotificationKeys.get(size);
            if (SystemClock.elapsedRealtime() - buttonClickedCacheItem.cacheTime > PluginConstants.TTS_REMOVE_BEAN_TIME) {
                sButtonClickedNotificationKeys.remove(buttonClickedCacheItem);
            }
        }
        if (sButtonClickedNotificationKeys.size() > 10) {
            sButtonClickedNotificationKeys.remove(0);
        }
    }

    public static int redefineNotificationRemovedReasonAndFillActions(StatusBarNotification statusBarNotification, int i2) {
        ButtonClickedCacheItem next;
        if (statusBarNotification != null && i2 == 8) {
            Iterator<ButtonClickedCacheItem> it = sButtonClickedNotificationKeys.iterator();
            while (true) {
                if (!it.hasNext()) {
                    next = null;
                    break;
                }
                next = it.next();
                if (TextUtils.equals(next.notificationKey, statusBarNotification.getKey())) {
                    sButtonClickedNotificationKeys.remove(next);
                    break;
                }
            }
            if (next != null) {
                int i3 = next.buttonPlace;
                if (i3 == 1) {
                    i2 = PushConstants.NOTIFICATION_REMOVED_REASON_CLICK_BUTTON_PLACE_1;
                } else if (i3 == 2) {
                    i2 = PushConstants.NOTIFICATION_REMOVED_REASON_CLICK_BUTTON_PLACE_2;
                } else if (i3 == 3) {
                    i2 = PushConstants.NOTIFICATION_REMOVED_REASON_CLICK_BUTTON_PLACE_3;
                } else if (i3 == 4) {
                    i2 = PushConstants.NOTIFICATION_REMOVED_REASON_CLICK_BUTTON_PLACE_COLORFUL;
                }
                if (statusBarNotification.getNotification().actions == null) {
                    statusBarNotification.getNotification().actions = next.actions;
                }
            }
        }
        kickOverCache();
        return i2;
    }
}
