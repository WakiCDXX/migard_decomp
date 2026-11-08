package com.xiaomi.push.sweetnotification;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.AppInfoUtils;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.push.service.NotificationUtils;
import com.xiaomi.push.service.notification.BuilderCompat;
import com.xiaomi.push.service.notification.CustomNotificationBuilder;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class SweetHightlightTitleBuilder extends CustomNotificationBuilder {
    private String mDescription;
    private String mTitle;

    private static class Tools {
        private static final String APP_NAME_ID = "display_name";
        private static final String DESCRIPTION_ID = "notification_description";
        private static final float DESC_SIZE = 11.33f;
        private static final String ICON_ID = "display_icon";
        private static final String LAYOUT_NAME = "notification_sweet_float";
        private static final String TITLE_ID = "notification_title";
        private static final float TITLE_SIZE = 13.33f;

        private Tools() {
        }
    }

    public SweetHightlightTitleBuilder(Context context, int i2, String str) {
        super(context, i2, str);
    }

    @Override // com.xiaomi.push.service.notification.CustomNotificationBuilder, com.xiaomi.push.service.notification.BuilderCompat
    public void applyCustomizations() {
        if (!isSupportCustom()) {
            buildStandardNotification();
            return;
        }
        super.applyCustomizations();
        Resources resources = getContext().getResources();
        String packageName = getContext().getPackageName();
        int resourceIdentifier = getResourceIdentifier(resources, "display_icon", "id", packageName);
        int resourceIdentifier2 = getResourceIdentifier(resources, "display_name", "id", packageName);
        int resourceIdentifier3 = getResourceIdentifier(resources, "notification_title", "id", packageName);
        int resourceIdentifier4 = getResourceIdentifier(resources, "notification_description", "id", packageName);
        if (this.mIconBitmap == null) {
            showDefaultAppIcon(resourceIdentifier);
        } else {
            getRemoteViews().setImageViewBitmap(resourceIdentifier, this.mIconBitmap);
        }
        String appLabel = AppInfoUtils.getAppLabel(getContext(), getTargetPackageName());
        if (!TextUtils.isEmpty(appLabel)) {
            getRemoteViews().setTextViewText(resourceIdentifier2, appLabel);
        }
        CharSequence charSequenceStringFromHtml = SweetNotificationHelper.stringFromHtml(getContext(), this.mTitle);
        if (charSequenceStringFromHtml != null) {
            getRemoteViews().setTextViewTextSize(resourceIdentifier3, 1, 13.33f);
            getRemoteViews().setTextViewText(resourceIdentifier3, charSequenceStringFromHtml);
        } else {
            getRemoteViews().setTextViewText(resourceIdentifier3, "");
            getRemoteViews().setTextViewTextSize(resourceIdentifier3, 0, 0.0f);
        }
        CharSequence charSequenceStringFromHtml2 = SweetNotificationHelper.stringFromHtml(getContext(), this.mDescription);
        if (charSequenceStringFromHtml2 != null) {
            getRemoteViews().setTextViewTextSize(resourceIdentifier4, 1, 11.33f);
            getRemoteViews().setTextViewText(resourceIdentifier4, charSequenceStringFromHtml2);
        } else {
            getRemoteViews().setTextViewText(resourceIdentifier4, "");
            getRemoteViews().setTextViewTextSize(resourceIdentifier4, 0, 0.0f);
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean(NotificationUtils.EXTRA_CUSTOM_HEIGHT, true);
        addExtras(bundle);
        setCustomContentView(getRemoteViews());
    }

    protected boolean canAddForHeadup() {
        return isSupportCustom() && !TextUtils.isEmpty(this.mTitle);
    }

    @Override // com.xiaomi.push.service.notification.CustomNotificationBuilder
    protected boolean checkSupportCustom() {
        if (!MIUIUtils.isXMSF(getContext())) {
            return false;
        }
        Resources resources = getContext().getResources();
        String packageName = getContext().getPackageName();
        return (getResourceIdentifier(resources, "display_icon", "id", packageName) == 0 || getResourceIdentifier(resources, "display_name", "id", packageName) == 0 || getResourceIdentifier(resources, "notification_title", "id", packageName) == 0 || getResourceIdentifier(resources, "notification_description", "id", packageName) == 0) ? false : true;
    }

    @Override // com.xiaomi.push.service.notification.CustomNotificationBuilder
    protected String getCopyLayout() {
        return null;
    }

    @Override // com.xiaomi.push.service.notification.CustomNotificationBuilder
    protected String getOriginalLayout() {
        return "notification_sweet_float";
    }

    @Override // com.xiaomi.push.service.notification.CustomNotificationBuilder, com.xiaomi.push.service.notification.BuilderCompat
    public BuilderCompat setPushExtra(Map<String, String> map) {
        if (map != null && !map.isEmpty() && isSupportCustom() && this.mPushExtra == null) {
            this.mTitle = map.get("notify_rich_title");
            this.mDescription = map.get("notify_rich_desc");
        }
        return super.setPushExtra(map);
    }
}
