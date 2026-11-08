package com.xiaomi.push.service.notification;

import android.app.Notification;

/* JADX INFO: loaded from: classes.dex */
public class StatusBarNotificationWrapper {
    private int mId;
    private Notification mNotification;
    private String mOpPkg;
    private String mPackageName;

    public StatusBarNotificationWrapper(int i2, Notification notification, String str, String str2) {
        this.mId = i2;
        this.mNotification = notification;
        this.mPackageName = str;
        this.mOpPkg = str2;
    }

    public int getId() {
        return this.mId;
    }

    public Notification getNotification() {
        return this.mNotification;
    }

    public String getOpPkg() {
        return this.mOpPkg;
    }

    public String getPackageName() {
        return this.mPackageName;
    }
}
