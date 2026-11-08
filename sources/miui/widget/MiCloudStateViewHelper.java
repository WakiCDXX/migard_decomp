package miui.widget;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import q.b;
import q.c;

/* JADX INFO: loaded from: classes.dex */
public class MiCloudStateViewHelper {
    public static int getDisablePaddingBottom(Context context) {
        return context.getResources().getDimensionPixelSize(c.f2196a);
    }

    public static int getEnablePaddingBottom(Context context) {
        return context.getResources().getDimensionPixelSize(c.f2197b);
    }

    public static int getEnablePaddingTop(Context context) {
        return context.getResources().getDimensionPixelSize(c.f2198c);
    }

    public static int getTextViewsMargin(Context context) {
        return context.getResources().getDimensionPixelSize(c.f2199d);
    }

    public static void setLayoutMinHeight(Context context, View view, int i2) {
        view.setMinimumHeight(i2 + getEnablePaddingTop(context) + getEnablePaddingBottom(context));
    }

    public static void setUnsyncedTextColor(Context context, TextView textView) {
        textView.setTextColor(context.getResources().getColor(b.f2192a));
    }
}
