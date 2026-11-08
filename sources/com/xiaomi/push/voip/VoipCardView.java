package com.xiaomi.push.voip;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.xiaomi.channel.commonutils.android.AppInfoUtils;
import com.xiaomi.channel.commonutils.android.ToolUtils;
import com.xiaomi.push.extension.XmsfR;
import com.xiaomi.push.headsup.AbsCardView;

/* JADX INFO: loaded from: classes.dex */
@TargetApi(28)
class VoipCardView extends AbsCardView {
    public VoipCardView(Context context) {
        super(context);
    }

    @Override // com.xiaomi.push.headsup.AbsCardView
    public void fillView() {
        fillTitle((TextView) findViewByIdName("title"));
        TextView textView = (TextView) findViewByIdName("content");
        if (textView != null) {
            fillContent(textView);
            textView.append(AppInfoUtils.getAppLabel(getContext(), getData().pkgName));
            Drawable appIconDrawable = AppInfoUtils.getAppIconDrawable(getContext(), getData().pkgName);
            if (appIconDrawable != null) {
                int iDp2px = ToolUtils.dp2px(getContext(), 14);
                appIconDrawable.setBounds(0, 0, iDp2px, iDp2px);
            }
            textView.setCompoundDrawables(null, null, appIconDrawable, null);
        }
        fillLargeIcon((ImageView) findViewByIdName("large_icon"), true);
        View viewFindViewByIdName = findViewByIdName(XmsfR.id.END_CALL_IV);
        if (viewFindViewByIdName != null) {
            viewFindViewByIdName.setOnClickListener(new View.OnClickListener() { // from class: com.xiaomi.push.voip.VoipCardView.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    VoipCardView.this.clickStyleButton(0);
                }
            });
        }
        ImageView imageView = (ImageView) findViewByIdName(XmsfR.id.ANSWER_CALL_IV);
        if (imageView != null) {
            int identifier = getIdentifier(VoipManager.getVoipDrawableByType(VoipManager.getVoipType(getData().f1747n.extras), true), "drawable");
            if (identifier == 0) {
                VoipLog.w("answer drawalbe is null");
            } else {
                imageView.setImageResource(identifier);
                imageView.setOnClickListener(new View.OnClickListener() { // from class: com.xiaomi.push.voip.VoipCardView.2
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        VoipCardView.this.clickStyleButton(1);
                    }
                });
            }
        }
    }

    @Override // com.xiaomi.push.headsup.AbsCardView
    public String layoutName() {
        return XmsfR.layout.HEADSUP_VOIP;
    }
}
