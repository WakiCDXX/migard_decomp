package com.xiaomi.push.voip;

import android.app.Notification;
import android.content.Context;
import android.os.Bundle;
import com.xiaomi.channel.commonutils.android.AppInfoUtils;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.channel.commonutils.android.ToolUtils;
import com.xiaomi.push.extension.XmsfR;
import com.xiaomi.push.service.NotificationUtils;
import com.xiaomi.push.service.notification.BuilderCompat;
import com.xiaomi.push.service.notification.CustomNotificationBuilder;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
class VoipNotificationBigBuilder extends CustomNotificationBuilder {
    private int mVoipType;

    public VoipNotificationBigBuilder(Context context, int i2, String str) {
        super(context, i2, str);
    }

    @Override // com.xiaomi.push.service.notification.CustomNotificationBuilder, com.xiaomi.push.service.notification.BuilderCompat
    protected void applyCustomizations() {
        if (!isSupportCustom()) {
            VoipLog.w("fail: change to standard notification");
            buildStandardNotification();
            return;
        }
        super.applyCustomizations();
        int viewResId = getViewResId("title");
        int viewResId2 = getViewResId("content");
        int viewResId3 = getViewResId("app_name");
        int viewResId4 = getViewResId(XmsfR.id.APP_ICON);
        int viewResId5 = getViewResId("large_icon");
        int viewResId6 = getViewResId(XmsfR.id.ANSWER_CALL_IV);
        getRemoteViews().setTextViewText(viewResId, this.mTitle);
        getRemoteViews().setTextViewText(viewResId2, this.mContent);
        getRemoteViews().setTextViewText(viewResId3, AppInfoUtils.getAppLabel(getContext(), getTargetPackageName()));
        VoipUtils.fillRemoteViewsIcons(getContext(), getTargetPackageName(), getRemoteViews(), viewResId4);
        if (this.mIconBitmap != null) {
            getRemoteViews().setImageViewBitmap(viewResId5, isRecoveredBuilder() ? this.mIconBitmap : bitmapWithRound(this.mIconBitmap, ToolUtils.dp2px(getContext(), 12)));
        } else {
            VoipUtils.fillRemoteViewsIcons(getContext(), getTargetPackageName(), getRemoteViews(), viewResId5);
        }
        String voipDrawableByType = VoipManager.getVoipDrawableByType(this.mVoipType, false);
        if (voipDrawableByType != null) {
            getRemoteViews().setImageViewResource(viewResId6, getDrawableResId(voipDrawableByType));
        }
        Bundle bundle = new Bundle();
        VoipManager.fillVoipExtras(this.mPushExtra, bundle);
        bundle.putBoolean(NotificationUtils.EXTRA_CUSTOM_HEIGHT, true);
        bundle.putBoolean(NotificationUtils.EXTRA_HIDE_BORDER, false);
        bundle.putBoolean(NotificationUtils.EXTRA_ENABLE_KEYGUARD, true);
        bundle.putBoolean(NotificationUtils.EXTRA_ENABLE_FLOAT, false);
        addExtras(bundle);
        setCustomContentView(getRemoteViews());
    }

    @Override // com.xiaomi.push.service.notification.CustomNotificationBuilder
    protected boolean checkSupportCustom() {
        return MIUIUtils.isXMSF(getContext());
    }

    @Override // com.xiaomi.push.service.notification.CustomNotificationBuilder
    protected void fillCustomStyleButton(int i2, Notification.Action action) {
        int viewResId;
        int viewResId2;
        super.fillCustomStyleButton(i2, action);
        if (i2 == 0) {
            viewResId = getViewResId(XmsfR.id.END_CALL_TV);
            viewResId2 = getViewResId(XmsfR.id.END_CALL_LL);
        } else {
            if (i2 != 1) {
                VoipLog.w("button not support:" + i2);
                return;
            }
            viewResId = getViewResId(XmsfR.id.ANSWER_CALL_TV);
            viewResId2 = getViewResId(XmsfR.id.ANSWER_CALL_LL);
            int i3 = this.mVoipType;
            if (i3 == 1) {
                getRemoteViews().setTextColor(viewResId, XmsfR.color.VOIP_VOICE_ANSWER_COLOR);
            } else if (i3 == 2) {
                getRemoteViews().setTextColor(viewResId, XmsfR.color.VOIP_VIDEO_ANSWER_COLOR);
            }
        }
        getRemoteViews().setTextViewText(viewResId, action.title);
        getRemoteViews().setOnClickPendingIntent(viewResId2, action.actionIntent);
    }

    @Override // com.xiaomi.push.service.notification.CustomNotificationBuilder
    protected String getCopyLayout() {
        return null;
    }

    @Override // com.xiaomi.push.service.notification.CustomNotificationBuilder
    protected String getOriginalLayout() {
        return MIUIUtils.getVisualMiuiVersionCode(getContext()) >= 12 ? XmsfR.layout.NOTIFICATION_VOIP_BIG_MIUI12 : XmsfR.layout.NOTIFICATION_VOIP_BIG_MIUI11;
    }

    @Override // com.xiaomi.push.service.notification.CustomNotificationBuilder, com.xiaomi.push.service.notification.BuilderCompat
    public BuilderCompat setPushExtra(Map<String, String> map) {
        super.setPushExtra(map);
        this.mVoipType = VoipManager.getVoipType(map);
        return this;
    }
}
