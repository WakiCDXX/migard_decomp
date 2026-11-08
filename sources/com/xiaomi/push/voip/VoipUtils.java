package com.xiaomi.push.voip;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.widget.RemoteViews;
import com.xiaomi.channel.commonutils.android.AppInfoUtils;

/* JADX INFO: loaded from: classes.dex */
class VoipUtils {
    VoipUtils() {
    }

    static void fillRemoteViewsIcons(Context context, String str, RemoteViews remoteViews, int i2) {
        try {
            remoteViews.setImageViewIcon(i2, Icon.createWithResource(str, AppInfoUtils.getAppIconId(context, str)));
        } catch (Exception unused) {
            Drawable appIconDrawable = AppInfoUtils.getAppIconDrawable(context, str);
            if (appIconDrawable instanceof BitmapDrawable) {
                remoteViews.setImageViewBitmap(i2, ((BitmapDrawable) appIconDrawable).getBitmap());
            } else {
                VoipLog.w("fill icon falied");
            }
        }
    }
}
