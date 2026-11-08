package com.xiaomi.push.service.notification;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RemoteViews;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.push.service.MIPushNotificationHelper;
import com.xiaomi.push.service.NotificationUtils;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class BannerBuilder extends CustomNotificationBuilder {
    private static final int BANNER_IMAGE_MAX_HEIGHT = 1678;
    private static final int BANNER_IMAGE_MIN_HEIGHT = 184;
    private static final int BANNER_IMAGE_WIDTH = 984;
    private static final String BANNER_NOTIFICATION_BACKGROUND_NAME = "bg";
    private static final String BANNER_NOTIFICATION_ICON_NAME = "icon";
    private static final String BANNER_NOTIFICATION_LAYOUT_NAME = "notification_banner";
    private static final String BANNER_NOTIFICATION_TITLE_NAME = "title";
    private Bitmap mBannerBackgroundBitmap;
    private Bitmap mBannerIconBitmap;
    private int mImageTextColor;

    public BannerBuilder(Context context, String str) {
        super(context, str);
        this.mImageTextColor = 16777216;
    }

    @Override // com.xiaomi.push.service.notification.CustomNotificationBuilder, com.xiaomi.push.service.notification.BuilderCompat
    public void applyCustomizations() {
        if (!isSupportCustom() || this.mBannerBackgroundBitmap == null) {
            buildStandardNotification();
            return;
        }
        super.applyCustomizations();
        Resources resources = getContext().getResources();
        String packageName = getContext().getPackageName();
        int resourceIdentifier = getResourceIdentifier(resources, BANNER_NOTIFICATION_BACKGROUND_NAME, "id", packageName);
        if (MIUIUtils.getMiuiVersionCode(getContext()) >= 10) {
            getRemoteViews().setImageViewBitmap(resourceIdentifier, bitmapWithRound(this.mBannerBackgroundBitmap, 30.0f));
        } else {
            getRemoteViews().setImageViewBitmap(resourceIdentifier, this.mBannerBackgroundBitmap);
        }
        int resourceIdentifier2 = getResourceIdentifier(resources, "icon", "id", packageName);
        if (this.mBannerIconBitmap != null) {
            getRemoteViews().setImageViewBitmap(resourceIdentifier2, this.mBannerIconBitmap);
        } else {
            showDefaultAppIcon(resourceIdentifier2);
        }
        int resourceIdentifier3 = getResourceIdentifier(resources, "title", "id", packageName);
        getRemoteViews().setTextViewText(resourceIdentifier3, this.mTitle);
        Map<String, String> map = this.mPushExtra;
        if (map != null && this.mImageTextColor == 16777216) {
            setImageTextColor(map.get(MIPushNotificationHelper.NOTIFICATION_IMAGE_TEXT_COLOR));
        }
        RemoteViews remoteViews = getRemoteViews();
        int i2 = this.mImageTextColor;
        remoteViews.setTextColor(resourceIdentifier3, (i2 == 16777216 || !isDarkColor(i2)) ? -1 : -16777216);
        setCustomContentView(getRemoteViews());
        Bundle bundle = new Bundle();
        bundle.putBoolean(NotificationUtils.EXTRA_CUSTOM_HEIGHT, true);
        addExtras(bundle);
    }

    @Override // com.xiaomi.push.service.notification.CustomNotificationBuilder
    protected boolean checkSupportCustom() {
        if (!MIUIUtils.isMIUI()) {
            return false;
        }
        Resources resources = getContext().getResources();
        String packageName = getContext().getPackageName();
        return (getResourceIdentifier(getContext().getResources(), BANNER_NOTIFICATION_BACKGROUND_NAME, "id", getContext().getPackageName()) == 0 || getResourceIdentifier(resources, "icon", "id", packageName) == 0 || getResourceIdentifier(resources, "title", "id", packageName) == 0 || MIUIUtils.getMiuiVersionCode(getContext()) < 9) ? false : true;
    }

    @Override // com.xiaomi.push.service.notification.CustomNotificationBuilder
    protected String getCopyLayout() {
        return null;
    }

    @Override // com.xiaomi.push.service.notification.CustomNotificationBuilder
    protected String getOriginalLayout() {
        return BANNER_NOTIFICATION_LAYOUT_NAME;
    }

    public BannerBuilder setBanner(Bitmap bitmap) {
        if (isSupportCustom() && bitmap != null) {
            if (bitmap.getWidth() != BANNER_IMAGE_WIDTH || BANNER_IMAGE_MIN_HEIGHT > bitmap.getHeight() || bitmap.getHeight() > BANNER_IMAGE_MAX_HEIGHT) {
                MyLog.w("colorful notification banner image resolution error, must belong to [984*184, 984*1678]");
            } else {
                this.mBannerBackgroundBitmap = bitmap;
            }
        }
        return this;
    }

    public BannerBuilder setIcon(Bitmap bitmap) {
        if (isSupportCustom() && bitmap != null) {
            this.mBannerIconBitmap = bitmap;
        }
        return this;
    }

    public BannerBuilder setImageTextColor(String str) {
        if (isSupportCustom() && !TextUtils.isEmpty(str)) {
            try {
                this.mImageTextColor = Color.parseColor(str);
            } catch (Exception unused) {
                MyLog.w("parse banner notification image text color error");
            }
        }
        return this;
    }

    @Override // com.xiaomi.push.service.notification.CustomNotificationBuilder, android.app.Notification.Builder
    public CustomNotificationBuilder setLargeIcon(Bitmap bitmap) {
        return this;
    }
}
