package miui.keyguard.clock;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.TextView;
import q.h;

/* JADX INFO: loaded from: classes.dex */
class MiuiClockAccessibilityDelegate extends View.AccessibilityDelegate {
    private final String mFancyColon;

    public MiuiClockAccessibilityDelegate(Context context) {
        this.mFancyColon = context.getString(h.d1);
    }

    private CharSequence replaceFancyColon(CharSequence charSequence) {
        return charSequence.toString().replace(this.mFancyColon, ":");
    }

    @Override // android.view.View.AccessibilityDelegate
    public void onInitializeAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(view, accessibilityEvent);
        CharSequence contentDescription = accessibilityEvent.getContentDescription();
        if (TextUtils.isEmpty(contentDescription)) {
            return;
        }
        accessibilityEvent.setContentDescription(replaceFancyColon(contentDescription));
    }

    @Override // android.view.View.AccessibilityDelegate
    public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
        if (!TextUtils.isEmpty(accessibilityNodeInfo.getText())) {
            accessibilityNodeInfo.setText(replaceFancyColon(accessibilityNodeInfo.getText()));
        }
        if (TextUtils.isEmpty(accessibilityNodeInfo.getContentDescription())) {
            return;
        }
        accessibilityNodeInfo.setContentDescription(replaceFancyColon(accessibilityNodeInfo.getContentDescription()));
    }

    @Override // android.view.View.AccessibilityDelegate
    public void onPopulateAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
        CharSequence text = view instanceof TextView ? ((TextView) view).getText() : accessibilityEvent.getContentDescription();
        if (TextUtils.isEmpty(text)) {
            return;
        }
        accessibilityEvent.getText().add(replaceFancyColon(text));
    }
}
