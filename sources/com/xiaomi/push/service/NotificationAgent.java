package com.xiaomi.push.service;

import android.app.Notification;
import android.content.Context;
import com.xiaomi.push.service.notification.BuilderCompat;
import com.xiaomi.xmpush.thrift.XmPushActionContainer;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public abstract class NotificationAgent {
    abstract void clearHeadsUpNotification(String str);

    abstract boolean handleNotify(Map<String, String> map, int i2, Notification notification);

    abstract void onBeforeNotify(XmPushActionContainer xmPushActionContainer, Map<String, String> map, int i2, Notification notification);

    abstract BuilderCompat onCustomNotificationBuilder(Context context, int i2, String str, Map<String, String> map);

    abstract boolean shouldBlockNotification(Context context, int i2, String str, Map<String, String> map);
}
