package miui.keyguard.clock;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import miui.date.DateUtils;
import q.b;
import q.c;
import q.e;
import q.h;

/* JADX INFO: loaded from: classes.dex */
public class MiuiLeftTopLargeClock extends MiuiBaseClock {
    private TextView mCurrentDateLarge;
    private FrameLayout mDateContainer;
    private TextView mTimeText;

    public MiuiLeftTopLargeClock(Context context) {
        this(context, null);
    }

    @Override // miui.keyguard.clock.MiuiBaseClock, miui.keyguard.clock.KeyguardClockController.IClockView
    public float getTopMargin() {
        return this.mContext.getResources().getDimensionPixelSize(c.f2216u);
    }

    @Override // miui.keyguard.clock.MiuiBaseClock, android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override // miui.keyguard.clock.MiuiBaseClock, android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mTimeText = (TextView) findViewById(e.f2231g);
        this.mDateContainer = (FrameLayout) findViewById(e.f2232h);
        this.mCurrentDateLarge = (TextView) findViewById(e.f2230f);
        updateTime();
    }

    @Override // miui.keyguard.clock.MiuiBaseClock, miui.keyguard.clock.KeyguardClockController.IClockView
    public void setTextColorDark(boolean z2) {
        int i2 = z2 ? -16777216 : -1;
        this.mTimeText.setTextColor(i2);
        this.mCurrentDateLarge.setTextColor(i2);
        setInfoDarkMode(i2);
        this.mOwnerInfo.setTextColor(z2 ? getContext().getResources().getColor(b.f2194c) : -1);
    }

    @Override // miui.keyguard.clock.MiuiBaseClock, miui.keyguard.clock.KeyguardClockController.IClockView
    public void updateTime() {
        super.updateTime();
        this.mTimeText.setText(DateUtils.formatDateTime(System.currentTimeMillis(), (this.m24HourFormat ? 32 : 16) | 76));
        this.mCurrentDateLarge.setText(this.mCalendar.format(this.mContext.getString(this.m24HourFormat ? h.i1 : h.j1)).toUpperCase());
    }

    @Override // miui.keyguard.clock.MiuiBaseClock
    protected void updateViewsLayoutParams() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
        layoutParams.topMargin = this.mHasTopMargin ? (int) (this.mScaleRatio * this.mResources.getDimensionPixelSize(c.f2216u)) : 0;
        layoutParams.setMarginStart((int) (this.mScaleRatio * this.mResources.getDimensionPixelSize(c.f2215t)));
        setLayoutParams(layoutParams);
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.mDateContainer.getLayoutParams();
        layoutParams2.topMargin = (int) (this.mScaleRatio * this.mResources.getDimensionPixelSize(c.f2218w));
        this.mDateContainer.setLayoutParams(layoutParams2);
        LinearLayout.LayoutParams layoutParams3 = (LinearLayout.LayoutParams) this.mLunarCalendarInfo.getLayoutParams();
        layoutParams3.topMargin = (int) (this.mScaleRatio * this.mResources.getDimensionPixelSize(c.f2206k));
        layoutParams3.setMarginStart((int) (this.mScaleRatio * this.mResources.getDimensionPixelSize(c.f2201f)));
        this.mLunarCalendarInfo.setLayoutParams(layoutParams3);
        LinearLayout.LayoutParams layoutParams4 = (LinearLayout.LayoutParams) this.mOwnerInfo.getLayoutParams();
        layoutParams4.topMargin = (int) (this.mScaleRatio * this.mResources.getDimensionPixelSize(c.f2207l));
        layoutParams4.setMarginStart((int) (this.mScaleRatio * this.mResources.getDimensionPixelSize(c.f2201f)));
        this.mOwnerInfo.setLayoutParams(layoutParams4);
    }

    @Override // miui.keyguard.clock.MiuiBaseClock
    protected void updateViewsTextSize() {
        super.updateViewsTextSize();
        Resources resources = this.mContext.getResources();
        this.mTimeText.setTextSize(0, (int) (this.mScaleRatio * resources.getDimensionPixelSize(c.f2217v)));
        this.mCurrentDateLarge.setTextSize(0, (int) (this.mScaleRatio * resources.getDimensionPixelSize(c.f2219x)));
    }

    public MiuiLeftTopLargeClock(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }
}
