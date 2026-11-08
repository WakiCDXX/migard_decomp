package com.xiaomi.push.service.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RemoteViews;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.channel.commonutils.android.SystemUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.reflect.JavaCalls;
import com.xiaomi.push.service.MIPushNotificationHelper;
import com.xiaomi.push.service.NotificationUtils;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class ColorfulBuilder extends CustomNotificationBuilder {
    private static final int BACKGROUND_IMAGE_HEIGHT = 192;
    private static final int BACKGROUND_IMAGE_MAX_HEIGHT = 207;
    private static final int BACKGROUND_IMAGE_MIN_HEIGHT = 177;
    private static final int BACKGROUND_IMAGE_WIDTH = 984;
    private static final String COLORFUL_NOTIFICATION_BACKGROUND_NAME = "bg";
    private static final String COLORFUL_NOTIFICATION_BUTTON_BG_NAME = "buttonBg";
    private static final String COLORFUL_NOTIFICATION_BUTTON_CONTAINER_NAME = "buttonContainer";
    private static final String COLORFUL_NOTIFICATION_BUTTON_NAME = "button";
    private static final String COLORFUL_NOTIFICATION_CONTAINER_NAME = "container";
    private static final String COLORFUL_NOTIFICATION_CONTENT_NAME = "content";
    private static final String COLORFUL_NOTIFICATION_ICON_NAME = "icon";
    private static final String COLORFUL_NOTIFICATION_LAYOUT_COPY_NAME = "notification_colorful_copy";
    private static final String COLORFUL_NOTIFICATION_LAYOUT_NAME = "notification_colorful";
    private static final String COLORFUL_NOTIFICATION_TITLE_NAME = "title";
    private Bitmap mBackgroundBitmap;
    private int mBackgroundColor;
    private int mButtonBgColor;
    private PendingIntent mButtonClickPendingIntent;
    private CharSequence mButtonText;
    private int mImageTextColor;

    public ColorfulBuilder(Context context, int i2, String str) {
        super(context, i2, str);
        this.mBackgroundColor = 16777216;
        this.mButtonBgColor = 16777216;
        this.mImageTextColor = 16777216;
    }

    private void adjustRemoteViewPaddingAndTextColor(RemoteViews remoteViews, int i2, int i3, int i4, boolean z2) {
        int iDp2px = dp2px(6.0f);
        remoteViews.setViewPadding(i2, iDp2px, 0, iDp2px, 0);
        if (z2) {
            remoteViews.setTextColor(i3, -1);
            remoteViews.setTextColor(i4, -1);
        } else {
            remoteViews.setTextColor(i3, -16777216);
            remoteViews.setTextColor(i4, -16777216);
        }
    }

    private Drawable createRoundCornerBg(int i2, int i3, int i4, float f2) {
        ShapeDrawable shapeDrawable = new ShapeDrawable();
        shapeDrawable.setShape(new RoundRectShape(new float[]{f2, f2, f2, f2, f2, f2, f2, f2}, null, null));
        shapeDrawable.getPaint().setColor(i2);
        shapeDrawable.getPaint().setStyle(Paint.Style.FILL);
        shapeDrawable.setIntrinsicWidth(i3);
        shapeDrawable.setIntrinsicHeight(i4);
        return shapeDrawable;
    }

    public ColorfulBuilder addAction(CharSequence charSequence, PendingIntent pendingIntent) {
        if (isSupportCustom()) {
            super.addAction(0, charSequence, pendingIntent);
            this.mButtonText = charSequence;
            this.mButtonClickPendingIntent = pendingIntent;
        }
        return this;
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
        int resourceIdentifier = getResourceIdentifier(resources, "icon", "id", packageName);
        if (this.mIconBitmap == null) {
            showDefaultAppIcon(resourceIdentifier);
        } else {
            getRemoteViews().setImageViewBitmap(resourceIdentifier, this.mIconBitmap);
        }
        int resourceIdentifier2 = getResourceIdentifier(resources, "title", "id", packageName);
        int resourceIdentifier3 = getResourceIdentifier(resources, "content", "id", packageName);
        getRemoteViews().setTextViewText(resourceIdentifier2, this.mTitle);
        getRemoteViews().setTextViewText(resourceIdentifier3, this.mContent);
        if (!TextUtils.isEmpty(this.mButtonText)) {
            int resourceIdentifier4 = getResourceIdentifier(resources, COLORFUL_NOTIFICATION_BUTTON_CONTAINER_NAME, "id", packageName);
            int resourceIdentifier5 = getResourceIdentifier(resources, COLORFUL_NOTIFICATION_BUTTON_NAME, "id", packageName);
            int resourceIdentifier6 = getResourceIdentifier(resources, COLORFUL_NOTIFICATION_BUTTON_BG_NAME, "id", packageName);
            getRemoteViews().setViewVisibility(resourceIdentifier4, 0);
            getRemoteViews().setTextViewText(resourceIdentifier5, this.mButtonText);
            getRemoteViews().setOnClickPendingIntent(resourceIdentifier4, this.mButtonClickPendingIntent);
            if (this.mButtonBgColor != 16777216) {
                int iDp2px = dp2px(70.0f);
                int iDp2px2 = dp2px(29.0f);
                getRemoteViews().setImageViewBitmap(resourceIdentifier6, MIPushNotificationHelper.drawableToBitmap(createRoundCornerBg(this.mButtonBgColor, iDp2px, iDp2px2, iDp2px2 / 2.0f)));
                getRemoteViews().setTextColor(resourceIdentifier5, isDarkColor(this.mButtonBgColor) ? -1 : -16777216);
            }
        }
        int resourceIdentifier7 = getResourceIdentifier(resources, COLORFUL_NOTIFICATION_BACKGROUND_NAME, "id", packageName);
        int resourceIdentifier8 = getResourceIdentifier(resources, COLORFUL_NOTIFICATION_CONTAINER_NAME, "id", packageName);
        if (this.mBackgroundColor != 16777216) {
            if (MIUIUtils.getMiuiVersionCode(getContext()) >= 10) {
                getRemoteViews().setImageViewBitmap(resourceIdentifier7, MIPushNotificationHelper.drawableToBitmap(createRoundCornerBg(this.mBackgroundColor, BACKGROUND_IMAGE_WIDTH, BACKGROUND_IMAGE_HEIGHT, 30.0f)));
            } else {
                getRemoteViews().setImageViewBitmap(resourceIdentifier7, MIPushNotificationHelper.drawableToBitmap(createRoundCornerBg(this.mBackgroundColor, BACKGROUND_IMAGE_WIDTH, BACKGROUND_IMAGE_HEIGHT, 0.0f)));
            }
            adjustRemoteViewPaddingAndTextColor(getRemoteViews(), resourceIdentifier8, resourceIdentifier2, resourceIdentifier3, isDarkColor(this.mBackgroundColor));
        } else if (this.mBackgroundBitmap != null) {
            if (MIUIUtils.getMiuiVersionCode(getContext()) >= 10) {
                getRemoteViews().setImageViewBitmap(resourceIdentifier7, bitmapWithRound(this.mBackgroundBitmap, 30.0f));
            } else {
                getRemoteViews().setImageViewBitmap(resourceIdentifier7, this.mBackgroundBitmap);
            }
            Map<String, String> map = this.mPushExtra;
            if (map != null && this.mImageTextColor == 16777216) {
                setImageTextColor(map.get(MIPushNotificationHelper.NOTIFICATION_IMAGE_TEXT_COLOR));
            }
            int i2 = this.mImageTextColor;
            adjustRemoteViewPaddingAndTextColor(getRemoteViews(), resourceIdentifier8, resourceIdentifier2, resourceIdentifier3, i2 == 16777216 || !isDarkColor(i2));
        } else {
            getRemoteViews().setViewVisibility(resourceIdentifier, 8);
            getRemoteViews().setViewVisibility(resourceIdentifier7, 8);
            try {
                Class[] clsArr = new Class[0];
                JavaCalls.callMethod(this, "setStyle", SystemUtils.loadClass(getContext(), "android.app.Notification$DecoratedCustomViewStyle").getConstructor(null).newInstance(null));
            } catch (Exception unused) {
                MyLog.w("load class DecoratedCustomViewStyle failed");
            }
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean(NotificationUtils.EXTRA_CUSTOM_HEIGHT, true);
        addExtras(bundle);
        setCustomContentView(getRemoteViews());
    }

    @Override // com.xiaomi.push.service.notification.CustomNotificationBuilder
    protected boolean checkSupportCustom() {
        if (!MIUIUtils.isMIUI()) {
            return false;
        }
        Resources resources = getContext().getResources();
        String packageName = getContext().getPackageName();
        return (getResourceIdentifier(resources, "icon", "id", packageName) == 0 || getResourceIdentifier(resources, "title", "id", packageName) == 0 || getResourceIdentifier(resources, "content", "id", packageName) == 0) ? false : true;
    }

    @Override // com.xiaomi.push.service.notification.CustomNotificationBuilder
    protected String getCopyLayout() {
        return COLORFUL_NOTIFICATION_LAYOUT_COPY_NAME;
    }

    @Override // com.xiaomi.push.service.notification.CustomNotificationBuilder
    protected String getOriginalLayout() {
        return COLORFUL_NOTIFICATION_LAYOUT_NAME;
    }

    public ColorfulBuilder setActionBackground(int i2) {
        if (isSupportCustom()) {
            this.mButtonBgColor = i2;
        }
        return this;
    }

    public ColorfulBuilder setBackground(int i2) {
        if (isSupportCustom()) {
            this.mBackgroundColor = i2;
        }
        return this;
    }

    public ColorfulBuilder setImageTextColor(String str) {
        if (isSupportCustom() && !TextUtils.isEmpty(str)) {
            try {
                this.mImageTextColor = Color.parseColor(str);
            } catch (Exception unused) {
                MyLog.w("parse colorful notification image text color error");
            }
        }
        return this;
    }

    public ColorfulBuilder setActionBackground(String str) {
        if (isSupportCustom() && !TextUtils.isEmpty(str)) {
            try {
                this.mButtonBgColor = Color.parseColor(str);
            } catch (Exception unused) {
                MyLog.w("parse colorful notification button bg color error");
            }
        }
        return this;
    }

    public ColorfulBuilder setBackground(String str) {
        if (isSupportCustom() && !TextUtils.isEmpty(str)) {
            try {
                this.mBackgroundColor = Color.parseColor(str);
            } catch (Exception unused) {
                MyLog.w("parse colorful notification bg color error");
            }
        }
        return this;
    }

    public ColorfulBuilder setBackground(Bitmap bitmap) {
        if (isSupportCustom() && bitmap != null) {
            if (bitmap.getWidth() == BACKGROUND_IMAGE_WIDTH && bitmap.getHeight() >= BACKGROUND_IMAGE_MIN_HEIGHT && bitmap.getHeight() <= BACKGROUND_IMAGE_MAX_HEIGHT) {
                this.mBackgroundBitmap = bitmap;
            } else {
                MyLog.w("colorful notification bg image resolution error, must [984*177, 984*207]");
            }
        }
        return this;
    }
}
