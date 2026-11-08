package miui.keyguard.clock;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import q.b;
import q.c;
import q.e;
import q.h;

/* JADX INFO: loaded from: classes.dex */
public class MiuiVerticalClock extends MiuiBaseClock {
    private TextView mTimeText;

    public MiuiVerticalClock(Context context) {
        this(context, null);
    }

    @Override // miui.keyguard.clock.MiuiBaseClock, miui.keyguard.clock.KeyguardClockController.IClockView
    public float getTopMargin() {
        return this.mContext.getResources().getDimensionPixelSize(c.f2202g);
    }

    @Override // miui.keyguard.clock.MiuiBaseClock, android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mTimeText = (TextView) findViewById(e.f2231g);
        updateTime();
    }

    @Override // miui.keyguard.clock.MiuiBaseClock, miui.keyguard.clock.KeyguardClockController.IClockView
    public void setTextColorDark(boolean z2) {
        super.setTextColorDark(z2);
        int color = z2 ? getResources().getColor(b.f2193b) : -1;
        this.mTimeText.setTextColor(color);
        setInfoDarkMode(color);
    }

    @Override // miui.keyguard.clock.MiuiBaseClock, miui.keyguard.clock.KeyguardClockController.IClockView
    public void updateTime() {
        super.updateTime();
        this.mTimeText.setText(this.mCalendar.format(this.mContext.getString(this.m24HourFormat ? h.l1 : h.k1)));
    }

    @Override // miui.keyguard.clock.MiuiBaseClock
    protected void updateViewsLayoutParams() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
        layoutParams.topMargin = this.mHasTopMargin ? (int) (this.mScaleRatio * this.mResources.getDimensionPixelSize(c.f2202g)) : 0;
        setLayoutParams(layoutParams);
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.mCurrentDate.getLayoutParams();
        layoutParams2.topMargin = (int) (this.mScaleRatio * this.mResources.getDimensionPixelSize(c.f2221z));
        this.mCurrentDate.setLayoutParams(layoutParams2);
        LinearLayout.LayoutParams layoutParams3 = (LinearLayout.LayoutParams) this.mLunarCalendarInfo.getLayoutParams();
        layoutParams3.topMargin = (int) (this.mScaleRatio * this.mResources.getDimensionPixelSize(c.f2206k));
        this.mLunarCalendarInfo.setLayoutParams(layoutParams3);
        LinearLayout.LayoutParams layoutParams4 = (LinearLayout.LayoutParams) this.mOwnerInfo.getLayoutParams();
        layoutParams4.topMargin = (int) (this.mScaleRatio * this.mResources.getDimensionPixelSize(c.f2207l));
        this.mOwnerInfo.setLayoutParams(layoutParams4);
    }

    @Override // miui.keyguard.clock.MiuiBaseClock
    protected void updateViewsTextSize() {
        super.updateViewsTextSize();
        this.mTimeText.setTextSize(0, (int) (this.mScaleRatio * this.mContext.getResources().getDimensionPixelSize(c.f2203h)));
    }

    public MiuiVerticalClock(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }
}
