package com.xiaomi.push.headsup;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;
import com.xiaomi.push.extension.XmsfR;

/* JADX INFO: loaded from: classes.dex */
class DefaultCardView extends AbsCardView {
    public DefaultCardView(Context context) {
        super(context);
    }

    @Override // com.xiaomi.push.headsup.AbsCardView
    public void fillView() {
        fillTitle((TextView) findViewByIdName("title"));
        fillContent((TextView) findViewByIdName("content"));
        fillAppName((TextView) findViewByIdName("app_name"));
        fillTime((TextView) findViewByIdName("time"));
        fillSmallIcon((ImageView) findViewByIdName(XmsfR.id.SMALL_ICON));
        fillLargeIcon((ImageView) findViewByIdName("large_icon"), false);
    }

    @Override // com.xiaomi.push.headsup.AbsCardView
    public String layoutName() {
        return XmsfR.layout.HEADSUP_DEFAULT;
    }
}
