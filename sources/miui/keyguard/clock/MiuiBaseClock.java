package miui.keyguard.clock;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Locale;
import java.util.TimeZone;
import miui.date.Calendar;
import q.b;
import q.c;
import q.e;
import q.h;

/* JADX INFO: loaded from: classes.dex */
public class MiuiBaseClock extends LinearLayout implements KeyguardClockController.IClockView {
    protected boolean m24HourFormat;
    protected Calendar mCalendar;
    private int mCalendarDayOfWeek;
    protected Context mContext;
    protected TextView mCurrentDate;
    protected int mDensityDpi;
    protected float mFontScale;
    protected boolean mFontScaleChanged;
    protected boolean mHasTopMargin;
    protected String mLanguage;
    protected TextView mLunarCalendarInfo;
    protected TextView mOwnerInfo;
    protected Resources mResources;
    protected float mScaleRatio;
    private boolean mShowLunarCalendar;
    protected boolean mTextDark;
    protected int mUserId;

    public MiuiBaseClock(Context context) {
        this(context, null);
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public int getClockHeight() {
        if (getHeight() > 0) {
            return getHeight();
        }
        return 0;
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public float getClockVisibleHeight() {
        if (getHeight() > 0) {
            return getHeight();
        }
        return 0.0f;
    }

    public View getLunarCalendarView() {
        return this.mLunarCalendarInfo;
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public float getTopMargin() {
        return 0.0f;
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateViewsLayoutParams();
        updateViewsTextSize();
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        float f2 = configuration.fontScale;
        if (this.mFontScale != f2) {
            this.mFontScaleChanged = true;
            updateViewsTextSize();
            this.mFontScale = f2;
        }
        int i2 = configuration.densityDpi;
        if (this.mDensityDpi != i2) {
            this.mFontScaleChanged = true;
            updateViewsTextSize();
            updateViewsLayoutParams();
            this.mDensityDpi = i2;
        }
        String language = configuration.locale.getLanguage();
        if (TextUtils.isEmpty(language) || language.equals(this.mLanguage)) {
            return;
        }
        this.mLanguage = language;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mCurrentDate = (TextView) findViewById(e.f2229e);
        this.mLunarCalendarInfo = (TextView) findViewById(e.f2244t);
        this.mOwnerInfo = (TextView) findViewById(e.f2245u);
        this.mCalendar = new Calendar();
        updateLunarCalendarInfo();
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public void setClockAlpha(float f2) {
        setAlpha(f2);
    }

    protected void setInfoDarkMode(int i2) {
        this.mCurrentDate.setTextColor(i2);
        this.mLunarCalendarInfo.setTextColor(i2);
    }

    public void setIs24HourFormat(boolean z2) {
        this.m24HourFormat = z2;
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public void setOwnerInfo(String str) {
        if (TextUtils.isEmpty(str)) {
            this.mOwnerInfo.setVisibility(8);
        } else {
            this.mOwnerInfo.setVisibility(0);
            this.mOwnerInfo.setText(str);
        }
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public void setScaleRatio(float f2) {
        this.mScaleRatio = f2;
        updateViewsTextSize();
        try {
            updateViewsLayoutParams();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public void setShowLunarCalendar(boolean z2) {
        this.mShowLunarCalendar = z2;
        updateLunarCalendarInfo();
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public void setTextColorDark(boolean z2) {
        this.mOwnerInfo.setTextColor(z2 ? getContext().getResources().getColor(b.f2194c) : getContext().getResources().getColor(b.f2195d));
    }

    public void updateHourFormat() {
        this.m24HourFormat = DateFormat.is24HourFormat(this.mContext);
    }

    public void updateLunarCalendarInfo() {
        if (!Locale.CHINESE.getLanguage().equals(Locale.getDefault().getLanguage()) || !this.mShowLunarCalendar) {
            this.mLunarCalendarInfo.setVisibility(8);
            return;
        }
        Calendar calendar = new Calendar();
        this.mLunarCalendarInfo.setVisibility(0);
        this.mLunarCalendarInfo.setText(calendar.format("YY年 N月e"));
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public void updateTime() {
        this.mCalendar.setTimeInMillis(System.currentTimeMillis());
        this.mCurrentDate.setText(this.mCalendar.format(this.mContext.getString(this.m24HourFormat ? h.e1 : h.f1)));
        int i2 = this.mCalendar.get(14);
        if (i2 != this.mCalendarDayOfWeek) {
            updateLunarCalendarInfo();
            this.mCalendarDayOfWeek = i2;
        }
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public void updateTimeZone(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        this.mCalendar = new Calendar(TimeZone.getTimeZone(str));
        updateTime();
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public void updateViewTopMargin(boolean z2) {
        this.mHasTopMargin = z2;
        try {
            updateViewsLayoutParams();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    protected void updateViewsTextSize() {
        Resources resources = this.mContext.getResources();
        float dimensionPixelSize = (int) (this.mScaleRatio * resources.getDimensionPixelSize(c.f2205j));
        this.mCurrentDate.setTextSize(0, dimensionPixelSize);
        this.mLunarCalendarInfo.setTextSize(0, dimensionPixelSize);
        this.mOwnerInfo.setTextSize(0, (int) (this.mScaleRatio * resources.getDimensionPixelSize(c.f2205j)));
    }

    public MiuiBaseClock(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mResources = null;
        this.mTextDark = false;
        this.mHasTopMargin = true;
        this.mScaleRatio = 1.0f;
        this.mContext = context;
        this.mResources = context.getResources();
        updateHourFormat();
    }

    protected void updateViewsLayoutParams() {
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public void updateResidentTimeZone(String str) {
    }
}
