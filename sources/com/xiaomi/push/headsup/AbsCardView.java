package com.xiaomi.push.headsup;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.drawable.Icon;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.xiaomi.push.service.NotificationUtils;
import java.util.Date;

/* JADX INFO: loaded from: classes.dex */
@TargetApi(28)
public abstract class AbsCardView extends FrameLayout {
    private HeadsUpNotification mData;

    public AbsCardView(Context context) {
        super(context);
        int identifier = getIdentifier(layoutName(), "layout");
        if (identifier != 0) {
            FrameLayout.inflate(context, identifier, this);
            return;
        }
        HeadsUpLog.w("inflate layout fail: " + layoutName());
    }

    private void clickPendingIntent(PendingIntent pendingIntent) {
        try {
            HeadsUpLog.d("click card:" + this.mData);
            ViewContainer.reportViewState((View) getParent(), 2);
            if (pendingIntent != null) {
                pendingIntent.send();
            }
        } catch (Exception e2) {
            HeadsUpLog.w("click card error:" + e2);
        }
    }

    private PendingIntent getStyleButtonPendingIntent(int i2) {
        Notification.Action action;
        Notification.Action[] actionArrResolveActions = NotificationUtils.resolveActions(this.mData.f1747n);
        if (actionArrResolveActions == null || actionArrResolveActions.length <= i2 || (action = actionArrResolveActions[i2]) == null) {
            return null;
        }
        HeadsUpLog.d("click button " + i2 + " " + ((Object) action.title));
        return action.actionIntent;
    }

    protected final void bindData(HeadsUpNotification headsUpNotification) {
        this.mData = headsUpNotification;
        fillView();
    }

    protected final void clickCard() {
        clickPendingIntent(this.mData.f1747n.contentIntent);
    }

    protected final void clickStyleButton(int i2) {
        HeadsUpLog.d("click style button:" + i2);
        getData().clickStyleButtonIndex = i2;
        clickPendingIntent(getStyleButtonPendingIntent(i2));
    }

    public final void fillAppName(TextView textView) {
        if (textView != null) {
            textView.setText(getContext().getApplicationInfo().loadLabel(getContext().getPackageManager()));
        }
    }

    public final void fillContent(TextView textView) {
        if (textView != null) {
            textView.setText(HeadsUpUtils.resolveContent(this.mData.f1747n));
        }
    }

    public final void fillLargeIcon(ImageView imageView, boolean z2) {
        if (imageView != null) {
            Icon iconResolveLargeIcon = NotificationUtils.resolveLargeIcon(getContext(), this.mData.f1747n, z2);
            imageView.setImageIcon(iconResolveLargeIcon);
            if (iconResolveLargeIcon == null) {
                if (z2) {
                    imageView.setVisibility(4);
                } else {
                    imageView.setVisibility(8);
                }
            }
        }
    }

    public final void fillSmallIcon(ImageView imageView) {
        if (imageView != null) {
            imageView.setImageIcon(NotificationUtils.resolveSmallIcon(getContext(), this.mData.f1747n, true));
        }
    }

    public final void fillTime(TextView textView) {
        if (textView != null) {
            textView.setText(DateFormat.format("HH:mm", new Date(this.mData.f1747n.when)));
        }
    }

    public final void fillTitle(TextView textView) {
        if (textView != null) {
            textView.setText(HeadsUpUtils.resolveTitle(this.mData.f1747n));
        }
    }

    public abstract void fillView();

    protected final View findViewByIdName(String str) {
        int identifier = getIdentifier(str, "id");
        if (identifier != 0) {
            return findViewById(identifier);
        }
        return null;
    }

    protected final HeadsUpNotification getData() {
        return this.mData;
    }

    protected final int getIdentifier(String str, String str2) {
        try {
            return getResources().getIdentifier(str, str2, getContext().getPackageName());
        } catch (Exception unused) {
            return 0;
        }
    }

    public abstract String layoutName();
}
