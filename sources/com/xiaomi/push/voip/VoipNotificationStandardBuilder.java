package com.xiaomi.push.voip;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import com.xiaomi.push.service.notification.BuilderCompat;

/* JADX INFO: loaded from: classes.dex */
class VoipNotificationStandardBuilder extends BuilderCompat {
    public VoipNotificationStandardBuilder(Context context) {
        super(context);
    }

    @Override // android.app.Notification.Builder
    public Notification.Builder addAction(int i2, CharSequence charSequence, PendingIntent pendingIntent) {
        return this;
    }

    @Override // android.app.Notification.Builder
    public Notification.Builder addAction(Notification.Action action) {
        return this;
    }
}
