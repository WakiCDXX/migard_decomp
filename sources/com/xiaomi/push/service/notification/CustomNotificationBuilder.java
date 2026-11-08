package com.xiaomi.push.service.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.widget.RemoteViews;
import com.xiaomi.channel.commonutils.android.AppInfoUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.reflect.JavaCalls;
import com.xiaomi.push.service.MIPushNotificationHelper;
import com.xiaomi.push.service.NotificationManagerHelper;
import com.xiaomi.push.service.NotificationUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public abstract class CustomNotificationBuilder extends BuilderCompat {
    private static final String CUSTOM_NOTIFICATION_EXTRA_COPY_FLAG_BOOLEAN = "mipush.customCopyLayout";
    protected static final int INVALID_COLOR = 16777216;
    private ArrayList<Notification.Action> mActions;
    private int mButtonIndex;
    protected CharSequence mContent;
    protected Bitmap mIconBitmap;
    private boolean mIsRecovered;
    private boolean mIsSupportCustom;
    private int mNotifyId;
    protected Map<String, String> mPushExtra;
    private RemoteViews mRemoteViews;
    private String mTargetPackageName;
    protected CharSequence mTitle;
    private boolean mUseCopyLayout;

    public CustomNotificationBuilder(Context context, String str) {
        this(context, 0, str);
    }

    private boolean builderSetTitle() {
        Map<String, String> map = this.mPushExtra;
        return map != null && Boolean.parseBoolean(map.get(MIPushNotificationHelper.NOTIFICATION_CUSTOM_BUILDER_SET_TITLE));
    }

    private void createRemoteViews() {
        int resourceIdentifier = getResourceIdentifier(getContext().getResources(), getLayoutName(), "layout", getContext().getPackageName());
        if (resourceIdentifier == 0) {
            MyLog.w("create RemoteViews failed, no such layout resource was found");
        } else {
            this.mRemoteViews = new RemoteViews(getContext().getPackageName(), resourceIdentifier);
            this.mIsSupportCustom = checkSupportCustom();
        }
    }

    private Bitmap getAppIconBitmap() {
        return MIPushNotificationHelper.drawableToBitmap(AppInfoUtils.getAppIconDrawable(getContext(), this.mTargetPackageName));
    }

    private String getLayoutName() {
        boolean zUseCopyLayout = useCopyLayout();
        this.mUseCopyLayout = zUseCopyLayout;
        return zUseCopyLayout ? getCopyLayout() : getOriginalLayout();
    }

    private boolean reapplyDisallowed() {
        return (TextUtils.isEmpty(getCopyLayout()) || TextUtils.isEmpty(this.mTargetPackageName)) ? false : true;
    }

    private void setContentTitleAndText() {
        super.setContentTitle(this.mTitle);
        super.setContentText(this.mContent);
    }

    private boolean shouldUseCopyLayout() {
        List<StatusBarNotification> activeNotifications = NotificationManagerHelper.from(getContext(), this.mTargetPackageName).getActiveNotifications();
        if (activeNotifications != null && !activeNotifications.isEmpty()) {
            for (StatusBarNotification statusBarNotification : activeNotifications) {
                if (statusBarNotification.getId() == this.mNotifyId) {
                    Notification notification = statusBarNotification.getNotification();
                    if (notification == null) {
                        return false;
                    }
                    return !notification.extras.getBoolean(CUSTOM_NOTIFICATION_EXTRA_COPY_FLAG_BOOLEAN, true);
                }
            }
        }
        return false;
    }

    private boolean useCopyLayout() {
        return reapplyDisallowed() && shouldUseCopyLayout();
    }

    @Override // com.xiaomi.push.service.notification.BuilderCompat
    protected void applyCustomizations() {
        super.applyCustomizations();
        Bundle bundle = new Bundle();
        if (reapplyDisallowed()) {
            bundle.putBoolean(CUSTOM_NOTIFICATION_EXTRA_COPY_FLAG_BOOLEAN, this.mUseCopyLayout);
        } else {
            bundle.putBoolean(CUSTOM_NOTIFICATION_EXTRA_COPY_FLAG_BOOLEAN, false);
        }
        bundle.putBoolean(NotificationUtils.EXTRA_CUSTOM_HEIGHT, false);
        bundle.putBoolean(CustomNotificationConstants.EXTRA_CUSTOM_NOTIFICATION, true);
        bundle.putInt(CustomNotificationConstants.EXTRA_CUSTOM_LARGE_ICON_ID, getViewResId("large_icon"));
        if (this.mActions.size() > 0) {
            Notification.Action[] actionArr = new Notification.Action[this.mActions.size()];
            this.mActions.toArray(actionArr);
            bundle.putParcelableArray(CustomNotificationConstants.EXTRA_CUSTOM_ACTIONS, actionArr);
        }
        if (builderSetTitle() || !NotificationUtils.isUserAggregate(getContext().getContentResolver())) {
            setContentTitleAndText();
        } else {
            bundle.putCharSequence(CustomNotificationConstants.EXTRA_CUSTOM_TITLE, this.mTitle);
            bundle.putCharSequence(CustomNotificationConstants.EXTRA_CUSTOM_CONTENT, this.mContent);
        }
        addExtras(bundle);
    }

    protected Bitmap bitmapWithRound(Bitmap bitmap, float f2) {
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapCreateBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        canvas.drawRoundRect(new RectF(rect), f2, f2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return bitmapCreateBitmap;
    }

    protected final void buildStandardNotification() {
        super.setContentTitle(this.mTitle);
        super.setContentText(this.mContent);
        Bitmap bitmap = this.mIconBitmap;
        if (bitmap != null) {
            super.setLargeIcon(bitmap);
        }
    }

    protected abstract boolean checkSupportCustom();

    protected int dp2px(float f2) {
        return (int) ((f2 * getContext().getResources().getDisplayMetrics().density) + 0.5f);
    }

    protected abstract String getCopyLayout();

    public int getNotifyId() {
        return this.mNotifyId;
    }

    protected abstract String getOriginalLayout();

    public final RemoteViews getRemoteViews() {
        return this.mRemoteViews;
    }

    protected final String getTargetPackageName() {
        return this.mTargetPackageName;
    }

    protected final boolean isDarkColor(int i2) {
        return ((((double) Color.red(i2)) * 0.299d) + (((double) Color.green(i2)) * 0.587d)) + (((double) Color.blue(i2)) * 0.114d) < 192.0d;
    }

    protected boolean isRecoveredBuilder() {
        return this.mIsRecovered;
    }

    protected final boolean isSupportCustom() {
        return this.mIsSupportCustom;
    }

    @Override // com.xiaomi.push.service.notification.BuilderCompat
    public BuilderCompat setPushExtra(Map<String, String> map) {
        this.mPushExtra = map;
        return this;
    }

    protected void showDefaultAppIcon(int i2) {
        Bitmap appIconBitmap = getAppIconBitmap();
        if (appIconBitmap != null) {
            getRemoteViews().setImageViewBitmap(i2, appIconBitmap);
            return;
        }
        int appIconId = AppInfoUtils.getAppIconId(getContext(), this.mTargetPackageName);
        if (appIconId != 0) {
            getRemoteViews().setImageViewResource(i2, appIconId);
        }
    }

    public CustomNotificationBuilder(Context context, int i2, String str) {
        super(context);
        this.mActions = new ArrayList<>();
        this.mButtonIndex = 0;
        this.mTargetPackageName = str;
        this.mNotifyId = i2;
        createRemoteViews();
    }

    @Override // android.app.Notification.Builder
    public CustomNotificationBuilder setContentText(CharSequence charSequence) {
        this.mContent = charSequence;
        return this;
    }

    @Override // android.app.Notification.Builder
    public CustomNotificationBuilder setContentTitle(CharSequence charSequence) {
        this.mTitle = charSequence;
        return this;
    }

    @Override // android.app.Notification.Builder
    public CustomNotificationBuilder setLargeIcon(Bitmap bitmap) {
        this.mIconBitmap = bitmap;
        return this;
    }

    @Override // android.app.Notification.Builder
    public CustomNotificationBuilder addAction(int i2, CharSequence charSequence, PendingIntent pendingIntent) {
        addAction(new Notification.Action(i2, charSequence, pendingIntent));
        return this;
    }

    @Override // android.app.Notification.Builder
    public CustomNotificationBuilder addAction(Notification.Action action) {
        if (action != null) {
            this.mActions.add(action);
        }
        int i2 = this.mButtonIndex;
        this.mButtonIndex = i2 + 1;
        fillCustomStyleButton(i2, action);
        return this;
    }

    public CustomNotificationBuilder(Context context, Notification notification) {
        this(context, NotificationUtils.getTargetPackage(notification));
        this.mIsRecovered = true;
        JavaCalls.setField(this, "mN", notification);
        setContentTitle((CharSequence) NotificationUtils.resolveTitle(notification));
        setContentText((CharSequence) NotificationUtils.resolveContent(notification));
        setLargeIcon(NotificationUtils.resolveLargeIconBitmap(context, notification, false));
        Notification.Action[] actionArrResolveActions = NotificationUtils.resolveActions(notification);
        if (actionArrResolveActions == null || actionArrResolveActions.length <= 0) {
            return;
        }
        for (Notification.Action action : actionArrResolveActions) {
            addAction(action);
        }
    }

    protected void fillCustomStyleButton(int i2, Notification.Action action) {
    }
}
