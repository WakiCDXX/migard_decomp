package com.xiaomi.push.service.notification;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RemoteViews;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.reflect.JavaCalls;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
@SuppressLint({"NewApi"})
public class BuilderCompat extends Notification.Builder {
    private Context mContext;

    public BuilderCompat(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override // android.app.Notification.Builder
    public Notification build() {
        applyCustomizations();
        return super.build();
    }

    protected Context getContext() {
        return this.mContext;
    }

    public final int getDrawableResId(String str) {
        return getResourceIdentifier(getContext().getResources(), str, "drawable", getContext().getPackageName());
    }

    public int getResourceIdentifier(Resources resources, String str, String str2, String str3) {
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        return resources.getIdentifier(str, str2, str3);
    }

    public final int getViewResId(String str) {
        return getResourceIdentifier(getContext().getResources(), str, "id", getContext().getPackageName());
    }

    public BuilderCompat setColorStr(String str) {
        if (!TextUtils.isEmpty(str)) {
            try {
                JavaCalls.callMethod(this, "setColor", Integer.valueOf(Color.parseColor(str)));
            } catch (Exception e2) {
                MyLog.e("fail to set color. " + e2);
            }
        }
        return this;
    }

    @Override // android.app.Notification.Builder
    public BuilderCompat addExtras(Bundle bundle) {
        super.addExtras(bundle);
        return this;
    }

    @Override // android.app.Notification.Builder
    public BuilderCompat setCustomContentView(RemoteViews remoteViews) {
        super.setCustomContentView(remoteViews);
        return this;
    }

    protected void applyCustomizations() {
    }

    public BuilderCompat setPushExtra(Map<String, String> map) {
        return this;
    }
}
