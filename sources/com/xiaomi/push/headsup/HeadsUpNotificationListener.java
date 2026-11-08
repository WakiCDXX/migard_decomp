package com.xiaomi.push.headsup;

/* JADX INFO: loaded from: classes.dex */
public abstract class HeadsUpNotificationListener {
    public static final int REASON_APP_CANCEL = 6;
    public static final int REASON_CLICK = 1;
    public static final int REASON_KEYGUARD_LOCKED = 5;
    public static final int REASON_REPLACE = 2;
    public static final int REASON_SWIPE = 3;
    public static final int REASON_TIMEOUT = 4;
    public static final int REASON_UPDATE = 7;

    public abstract void onNotificationPosted(HeadsUpNotification headsUpNotification);

    public abstract HeadsUpConstants.ActionAfterRemoved onNotificationRemoved(HeadsUpNotification headsUpNotification, int i2);

    public void onSnoozedNotificationReplaced(HeadsUpNotification headsUpNotification, HeadsUpNotification headsUpNotification2) {
    }
}
