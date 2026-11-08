package com.xiaomi.push.voip;

import android.app.Notification;
import android.content.Context;
import android.os.Bundle;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.channel.commonutils.android.ToolUtils;
import com.xiaomi.push.extension.XmsfR;
import com.xiaomi.push.service.NotificationUtils;
import com.xiaomi.push.service.notification.BuilderCompat;
import com.xiaomi.push.service.notification.CustomNotificationBuilder;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
class VoipNotificationSmallBuilder extends CustomNotificationBuilder {
    private int mVoipType;

    public VoipNotificationSmallBuilder(Context context, Notification notification) {
        super(context, notification);
        this.mVoipType = VoipManager.getVoipType(notification.extras);
    }

    @Override // com.xiaomi.push.service.notification.CustomNotificationBuilder, com.xiaomi.push.service.notification.BuilderCompat
    protected void applyCustomizations() {
        if (!isSupportCustom()) {
            buildStandardNotification();
            return;
        }
        super.applyCustomizations();
        int viewResId = getViewResId("title");
        int viewResId2 = getViewResId("content");
        int viewResId3 = getViewResId("large_icon");
        int viewResId4 = getViewResId(XmsfR.id.ANSWER_CALL_IV);
        getRemoteViews().setTextViewText(viewResId, this.mTitle);
        getRemoteViews().setTextViewText(viewResId2, this.mContent);
        if (this.mIconBitmap != null) {
            getRemoteViews().setImageViewBitmap(viewResId3, isRecoveredBuilder() ? this.mIconBitmap : bitmapWithRound(this.mIconBitmap, ToolUtils.dp2px(getContext(), 12)));
        } else {
            VoipUtils.fillRemoteViewsIcons(getContext(), getTargetPackageName(), getRemoteViews(), viewResId3);
        }
        String voipDrawableByType = VoipManager.getVoipDrawableByType(this.mVoipType, true);
        if (voipDrawableByType != null) {
            getRemoteViews().setImageViewResource(viewResId4, getDrawableResId(voipDrawableByType));
        }
        setCustomContentView(getRemoteViews());
    }

    @Override // com.xiaomi.push.service.notification.BuilderCompat, android.app.Notification.Builder
    public Notification build() {
        Notification notificationBuild = super.build();
        Bundle bundle = notificationBuild.extras;
        if (bundle != null) {
            Map<String, String> map = this.mPushExtra;
            if (map != null) {
                VoipManager.fillVoipExtras(map, bundle);
            }
            bundle.putBoolean(NotificationUtils.EXTRA_CUSTOM_HEIGHT, true);
            bundle.putBoolean(NotificationUtils.EXTRA_HIDE_BORDER, false);
            bundle.putBoolean(NotificationUtils.EXTRA_ENABLE_KEYGUARD, false);
            bundle.putBoolean(NotificationUtils.EXTRA_ENABLE_FLOAT, false);
        }
        return notificationBuild;
    }

    @Override // com.xiaomi.push.service.notification.CustomNotificationBuilder
    protected boolean checkSupportCustom() {
        return MIUIUtils.isXMSF(getContext());
    }

    @Override // com.xiaomi.push.service.notification.CustomNotificationBuilder
    protected void fillCustomStyleButton(int i2, Notification.Action action) {
        int viewResId;
        super.fillCustomStyleButton(i2, action);
        if (i2 == 0) {
            viewResId = getViewResId(XmsfR.id.END_CALL_IV);
        } else {
            if (i2 != 1) {
                VoipLog.w("button not support:" + i2);
                return;
            }
            viewResId = getViewResId(XmsfR.id.ANSWER_CALL_IV);
        }
        getRemoteViews().setOnClickPendingIntent(viewResId, action.actionIntent);
    }

    @Override // com.xiaomi.push.service.notification.CustomNotificationBuilder
    protected String getCopyLayout() {
        return null;
    }

    @Override // com.xiaomi.push.service.notification.CustomNotificationBuilder
    protected String getOriginalLayout() {
        return XmsfR.layout.NOTIFICATION_VOIP_SMALL;
    }

    @Override // com.xiaomi.push.service.notification.CustomNotificationBuilder, com.xiaomi.push.service.notification.BuilderCompat
    public BuilderCompat setPushExtra(Map<String, String> map) {
        super.setPushExtra(map);
        this.mVoipType = VoipManager.getVoipType(map);
        return this;
    }

    public VoipNotificationSmallBuilder(Context context, int i2, String str) {
        super(context, i2, str);
    }
}
