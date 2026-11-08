package com.xiaomi.push.sweetnotification;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.AppInfoUtils;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.push.service.NotificationUtils;
import com.xiaomi.push.service.notification.BuilderCompat;
import com.xiaomi.push.service.notification.CustomNotificationBuilder;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class SweetCardBuilder extends CustomNotificationBuilder {
    private String mAlertTitle;
    private Bitmap mBackgroundBitmap;
    private SweetHightlightTitleBuilder mFloatRemoveViewsBuilder;
    private String mInfoLeft;
    private String mInfoRight;

    private static class Tools {
        static final float ALERT_SIZE = 20.0f;
        private static final String ALERT_TITLE_ID = "alert_title";
        private static final String APP_NAME_ID = "display_name";
        private static final String BACKGROUND_ID = "notification_background";
        static final float CORNER_WIDTH = 17.45f;
        private static final String DEFAULT_MAP_ID = "sweet_default_map";
        private static final String ICON_ID = "display_icon";
        static final float IMAGE_DEFAULT_HEIGHT = 98.33f;
        static final float IMAGE_FIX_HIGHT = 295.0f;
        static final float IMAGE_FIX_WIDTH = 1008.0f;
        static final float IMAGE_MAX_WIDTH = 367.0f;
        private static final String INFO_LEFT_ID = "info_left";
        private static final String INFO_RIGHT_ID = "info_right";
        static final float INFO_SIZE = 20.0f;
        private static final String LAYOUT_NAME = "notification_sweet_card";
        static final float MARGIN = 12.75f;

        private Tools() {
        }

        protected static Bitmap bitmapWithRound(Context context, Bitmap bitmap) {
            if (context == null || bitmap == null) {
                return bitmap;
            }
            if (bitmap.getWidth() != IMAGE_FIX_WIDTH || bitmap.getHeight() != IMAGE_FIX_HIGHT) {
                return null;
            }
            float f2 = context.getResources().getDisplayMetrics().density;
            Rect rectFillInBitmap = fillInBitmap(bitmap, Math.min(context.getResources().getDisplayMetrics().widthPixels - ((MARGIN * f2) * 2.0f), f2 * IMAGE_MAX_WIDTH), getImageHeight(context));
            int mapRadius = getMapRadius(context);
            Bitmap bitmapCreateBitmap = Bitmap.createBitmap(rectFillInBitmap.width(), rectFillInBitmap.height(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmapCreateBitmap);
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(-65536);
            paint.setAntiAlias(true);
            float f3 = mapRadius;
            canvas.drawRoundRect(new RectF(new Rect(0, 0, rectFillInBitmap.width(), rectFillInBitmap.height())), f3, f3, paint);
            canvas.drawRect(0.0f, rectFillInBitmap.height() - mapRadius, rectFillInBitmap.width(), rectFillInBitmap.height(), paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rectFillInBitmap, new Rect(0, 0, rectFillInBitmap.width(), rectFillInBitmap.height()), paint);
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
            return bitmapCreateBitmap;
        }

        private static Rect fillInBitmap(Bitmap bitmap, float f2, float f3) {
            if (bitmap == null || bitmap.getWidth() == 0 || bitmap.getHeight() == 0) {
                return new Rect(0, 0, (int) f2, (int) f3);
            }
            float fMin = Math.min(bitmap.getWidth() / f2, bitmap.getHeight() / f3);
            int iRound = Math.round(f2 * fMin);
            int iRound2 = Math.round(f3 * fMin);
            int width = (bitmap.getWidth() - iRound) / 2;
            int height = (bitmap.getHeight() - iRound2) / 2;
            return new Rect(width, height, iRound + width, iRound2 + height);
        }

        private static float getImageHeight(Context context) {
            try {
                Resources resources = context.getResources();
                return resources.getDimension(resources.getIdentifier("sweet_map_height", "dimen", context.getPackageName()));
            } catch (Throwable unused) {
                return context.getResources().getDisplayMetrics().density * IMAGE_DEFAULT_HEIGHT;
            }
        }

        private static int getMapRadius(Context context) {
            try {
                Context contextCreatePackageContext = context.createPackageContext("com.android.systemui", 0);
                if (contextCreatePackageContext != null) {
                    Resources resources = contextCreatePackageContext.getResources();
                    return Math.round(resources.getDimension(resources.getIdentifier("notification_item_bg_radius", "dimen", contextCreatePackageContext.getPackageName())));
                }
            } catch (Throwable th) {
                MyLog.e("[Sweet] get map radius from systemui failed:" + th);
            }
            return Math.round(context.getResources().getDisplayMetrics().density * CORNER_WIDTH);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static float getNormalTextSize(Context context) {
            try {
                Resources resources = context.getResources();
                return resources.getDimension(resources.getIdentifier("sweet_normal_text_size", "dimen", context.getPackageName()));
            } catch (Throwable unused) {
                return context.getResources().getDisplayMetrics().density * 20.0f;
            }
        }
    }

    public SweetCardBuilder(Context context, int i2, String str) {
        super(context, i2, str);
    }

    @Override // com.xiaomi.push.service.notification.CustomNotificationBuilder, com.xiaomi.push.service.notification.BuilderCompat
    public void applyCustomizations() {
        if (!isSupportCustom()) {
            buildStandardNotification();
            return;
        }
        super.applyCustomizations();
        SweetHightlightTitleBuilder sweetHightlightTitleBuilder = this.mFloatRemoveViewsBuilder;
        if (sweetHightlightTitleBuilder != null && sweetHightlightTitleBuilder.canAddForHeadup()) {
            try {
                this.mFloatRemoveViewsBuilder.applyCustomizations();
                if (this.mFloatRemoveViewsBuilder.getRemoteViews() != null) {
                    setCustomHeadsUpContentView(this.mFloatRemoveViewsBuilder.getRemoteViews());
                }
            } catch (Exception unused) {
            }
        }
        Resources resources = getContext().getResources();
        String packageName = getContext().getPackageName();
        int resourceIdentifier = getResourceIdentifier(resources, "notification_background", "id", packageName);
        int resourceIdentifier2 = getResourceIdentifier(resources, "display_icon", "id", packageName);
        int resourceIdentifier3 = getResourceIdentifier(resources, "display_name", "id", packageName);
        int resourceIdentifier4 = getResourceIdentifier(resources, "alert_title", "id", packageName);
        int resourceIdentifier5 = getResourceIdentifier(resources, "info_left", "id", packageName);
        int resourceIdentifier6 = getResourceIdentifier(resources, "info_right", "id", packageName);
        if (this.mBackgroundBitmap != null) {
            getRemoteViews().setImageViewBitmap(resourceIdentifier, this.mBackgroundBitmap);
        } else if (!SweetNotificationHelper.isSweetNotificationInSystemUI(getContext(), getNotifyId(), getTargetPackageName())) {
            try {
                getRemoteViews().setImageViewBitmap(resourceIdentifier, BitmapFactory.decodeResource(resources, resources.getIdentifier("sweet_default_map", "drawable", packageName)));
            } catch (Exception unused2) {
            }
        }
        if (this.mIconBitmap == null) {
            showDefaultAppIcon(resourceIdentifier2);
        } else {
            getRemoteViews().setImageViewBitmap(resourceIdentifier2, this.mIconBitmap);
        }
        String appLabel = AppInfoUtils.getAppLabel(getContext(), getTargetPackageName());
        if (!TextUtils.isEmpty(appLabel)) {
            getRemoteViews().setTextViewText(resourceIdentifier3, appLabel);
        }
        CharSequence charSequenceStringFromHtml = SweetNotificationHelper.stringFromHtml(getContext(), this.mAlertTitle);
        float normalTextSize = Tools.getNormalTextSize(getContext());
        if (charSequenceStringFromHtml != null) {
            getRemoteViews().setTextViewTextSize(resourceIdentifier4, 0, normalTextSize);
            getRemoteViews().setTextViewText(resourceIdentifier4, charSequenceStringFromHtml);
        } else {
            getRemoteViews().setTextViewText(resourceIdentifier4, "");
            getRemoteViews().setTextViewTextSize(resourceIdentifier4, 0, 0.0f);
        }
        CharSequence charSequenceStringFromHtml2 = SweetNotificationHelper.stringFromHtml(getContext(), this.mInfoLeft);
        if (charSequenceStringFromHtml2 != null) {
            getRemoteViews().setTextViewTextSize(resourceIdentifier5, 0, normalTextSize);
            getRemoteViews().setTextViewText(resourceIdentifier5, charSequenceStringFromHtml2);
        } else {
            getRemoteViews().setTextViewText(resourceIdentifier5, "");
            getRemoteViews().setTextViewTextSize(resourceIdentifier5, 0, 0.0f);
        }
        CharSequence charSequenceStringFromHtml3 = SweetNotificationHelper.stringFromHtml(getContext(), this.mInfoRight);
        if (charSequenceStringFromHtml3 != null) {
            getRemoteViews().setTextViewTextSize(resourceIdentifier6, 0, normalTextSize);
            getRemoteViews().setTextViewText(resourceIdentifier6, charSequenceStringFromHtml3);
        } else {
            getRemoteViews().setTextViewText(resourceIdentifier6, "");
            getRemoteViews().setTextViewTextSize(resourceIdentifier6, 0, 0.0f);
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean(NotificationUtils.EXTRA_CUSTOM_HEIGHT, true);
        SweetNotificationHelper.setSweetParams(getContext(), getNotifyId(), getTargetPackageName(), bundle, this.mPushExtra, this);
        bundle.putBoolean(NotificationUtils.EXTRA_HIDE_BORDER, true);
        addExtras(bundle);
        setCustomContentView(getRemoteViews());
    }

    @Override // com.xiaomi.push.service.notification.CustomNotificationBuilder
    protected boolean checkSupportCustom() {
        if (!MIUIUtils.isXMSF(getContext())) {
            return false;
        }
        Resources resources = getContext().getResources();
        String packageName = getContext().getPackageName();
        return (getResourceIdentifier(resources, "notification_background", "id", packageName) == 0 || getResourceIdentifier(resources, "display_icon", "id", packageName) == 0 || getResourceIdentifier(resources, "display_name", "id", packageName) == 0 || getResourceIdentifier(resources, "alert_title", "id", packageName) == 0 || getResourceIdentifier(resources, "info_left", "id", packageName) == 0 || getResourceIdentifier(resources, "info_right", "id", packageName) == 0) ? false : true;
    }

    @Override // com.xiaomi.push.service.notification.CustomNotificationBuilder
    protected String getCopyLayout() {
        return null;
    }

    @Override // com.xiaomi.push.service.notification.CustomNotificationBuilder
    protected String getOriginalLayout() {
        return "notification_sweet_card";
    }

    @Override // com.xiaomi.push.service.notification.CustomNotificationBuilder, com.xiaomi.push.service.notification.BuilderCompat
    public BuilderCompat setPushExtra(Map<String, String> map) {
        if (map != null && !map.isEmpty() && isSupportCustom() && this.mPushExtra == null) {
            this.mAlertTitle = map.get("notify_style_5_alert");
            this.mInfoLeft = map.get("notify_style_5_left");
            this.mInfoRight = map.get("notify_style_5_right");
            try {
                this.mBackgroundBitmap = Tools.bitmapWithRound(getContext(), ImageDownloadHelper.getOnlinePictureResource(getContext(), map.get("notify_style_5_bg_pic"), false));
            } catch (Exception unused) {
            }
            this.mFloatRemoveViewsBuilder = SweetNotificationHelper.addFloatStyle(getContext(), getNotifyId(), getTargetPackageName(), map);
        }
        return super.setPushExtra(map);
    }
}
