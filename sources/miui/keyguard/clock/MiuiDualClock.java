package miui.keyguard.clock;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import miui.date.Calendar;
import miui.date.DateUtils;
import miui.provider.Weather;
import q.b;
import q.c;
import q.e;
import q.h;

/* JADX INFO: loaded from: classes.dex */
public class MiuiDualClock extends RelativeLayout implements KeyguardClockController.IClockView {
    private static final String TAG = "MiuiDualClock";
    private static final String WEATHER_URI = "content://weather/realtimeLocalWeatherData/4/1";
    private boolean m24HourFormat;
    private boolean mAttached;
    private boolean mAutoTimeZone;
    ContentObserver mAutoTimeZoneObserver;
    private Calendar mCalendar;
    private Context mContext;
    private String mCountry;
    private boolean mDateTooLong;
    private String mLanguage;
    private TextView mLocalCity;
    private OnLocalCityChangeListener mLocalCityChangeListener;
    private TextView mLocalDate;
    private TextView mLocalTime;
    private String mLocalTimeZone;
    private Calendar mResidentCalendar;
    private TextView mResidentCity;
    private TextView mResidentDate;
    private LinearLayout mResidentLayout;
    private TextView mResidentTime;
    private String mResidentTimeZone;
    protected float mScaleRatio;

    public interface OnLocalCityChangeListener {
        void onLocalCityChanged(String str);
    }

    public MiuiDualClock(Context context) {
        this(context, null);
    }

    private String getNamebyZone(String str) {
        try {
            Class<?> cls = Class.forName("android.icu.text.TimeZoneNames");
            Method declaredMethod = cls.getDeclaredMethod("getInstance", Locale.class);
            declaredMethod.setAccessible(true);
            Object objInvoke = declaredMethod.invoke(cls, Locale.getDefault());
            if (str == null || !str.equals("Asia/Shanghai")) {
                Method declaredMethod2 = cls.getDeclaredMethod("getExemplarLocationName", String.class);
                declaredMethod2.setAccessible(true);
                return (String) declaredMethod2.invoke(objInvoke, str);
            }
            Class<?> cls2 = Class.forName("android.icu.text.TimeZoneNames$NameType");
            Object obj = new Object();
            for (Object obj2 : cls2.getEnumConstants()) {
                if (obj2.toString().equalsIgnoreCase("LONG_STANDARD")) {
                    obj = obj2;
                }
            }
            Method declaredMethod3 = cls.getDeclaredMethod("getDisplayName", String.class, cls2, Long.TYPE);
            declaredMethod3.setAccessible(true);
            return (String) declaredMethod3.invoke(objInvoke, "Asia/Shanghai", obj, Long.valueOf(new Date().getTime()));
        } catch (Exception e2) {
            e2.printStackTrace();
            return this.mContext.getString(h.c1);
        }
    }

    private void updateDateLines() {
        boolean z2 = ((float) (((int) this.mLocalDate.getPaint().measureText(this.mLocalDate.getText().toString())) + ((int) this.mResidentDate.getPaint().measureText(this.mResidentDate.getText().toString())))) > getResources().getDimension(c.f2211p) * 2.0f;
        if (z2 != this.mDateTooLong) {
            this.mDateTooLong = z2;
            updateTime();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateLocalCity() {
        if (this.mAutoTimeZone) {
            new AsyncTask<Void, Void, String>() { // from class: miui.keyguard.clock.MiuiDualClock.2
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public String doInBackground(Void... voidArr) {
                    String string = "";
                    Cursor cursorQuery = null;
                    try {
                        cursorQuery = MiuiDualClock.this.mContext.getContentResolver().query(Uri.parse(MiuiDualClock.WEATHER_URI), null, null, null, null);
                        if (cursorQuery != null) {
                            while (cursorQuery.moveToNext()) {
                                string = cursorQuery.getString(cursorQuery.getColumnIndex(Weather.WeatherBaseColumns.CITY_NAME));
                            }
                        }
                        Log.i(MiuiDualClock.TAG, "update local city name, city=" + string);
                    } catch (Exception e2) {
                        Log.e(MiuiDualClock.TAG, "get city exception", e2);
                    } finally {
                        if (cursorQuery != null) {
                            cursorQuery.close();
                        }
                    }
                    return string;
                }

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public void onPostExecute(String str) {
                    if (TextUtils.isEmpty(str)) {
                        str = MiuiDualClock.this.mContext.getString(h.b1);
                    }
                    MiuiDualClock.this.mLocalCity.setText(str);
                    if (MiuiDualClock.this.mLocalCityChangeListener != null) {
                        MiuiDualClock.this.mLocalCityChangeListener.onLocalCityChanged(str);
                    }
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
            return;
        }
        String namebyZone = getNamebyZone(this.mLocalTimeZone);
        this.mLocalCity.setText(namebyZone);
        OnLocalCityChangeListener onLocalCityChangeListener = this.mLocalCityChangeListener;
        if (onLocalCityChangeListener != null) {
            onLocalCityChangeListener.onLocalCityChanged(namebyZone);
        }
    }

    private void updateResidentCityName() {
        this.mResidentCity.setText(getNamebyZone(this.mResidentTimeZone));
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public int getClockHeight() {
        return getHeight();
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public float getClockVisibleHeight() {
        return getHeight();
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public float getTopMargin() {
        return this.mContext.getResources().getDimensionPixelSize(c.f2210o);
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mAttached) {
            return;
        }
        this.mAttached = true;
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("auto_time_zone"), false, this.mAutoTimeZoneObserver);
        this.mAutoTimeZoneObserver.onChange(false);
        updateViewsLayoutParams();
        updateViewsTextSize();
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        String language = configuration.locale.getLanguage();
        String country = configuration.locale.getCountry();
        if ((language == null || language.equals(this.mLanguage)) && (country == null || country.equals(this.mCountry))) {
            return;
        }
        updateResidentCityName();
        updateLocalCity();
        this.mDateTooLong = false;
        updateTime();
        updateDateLines();
        this.mLanguage = language;
        this.mCountry = country;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mAttached) {
            this.mAttached = false;
            this.mContext.getContentResolver().unregisterContentObserver(this.mAutoTimeZoneObserver);
        }
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mLocalCity = (TextView) findViewById(e.f2234j);
        TextView textView = (TextView) findViewById(e.f2236l);
        this.mLocalTime = textView;
        textView.setAccessibilityDelegate(new MiuiClockAccessibilityDelegate(this.mContext));
        this.mLocalDate = (TextView) findViewById(e.f2235k);
        this.mResidentCity = (TextView) findViewById(e.f2238n);
        TextView textView2 = (TextView) findViewById(e.f2240p);
        this.mResidentTime = textView2;
        textView2.setAccessibilityDelegate(new MiuiClockAccessibilityDelegate(this.mContext));
        this.mResidentDate = (TextView) findViewById(e.f2239o);
        this.mResidentLayout = (LinearLayout) findViewById(e.f2241q);
        this.mLanguage = this.mContext.getResources().getConfiguration().locale.getLanguage();
        TimeZone timeZone = TimeZone.getDefault();
        this.mLocalTimeZone = timeZone.getID();
        updateLocalCity();
        this.mCalendar = new Calendar();
        if (TextUtils.isEmpty(this.mResidentTimeZone)) {
            this.mResidentTimeZone = timeZone.getID();
        }
        updateResidentCityName();
        this.mResidentCalendar = new Calendar(TimeZone.getTimeZone(this.mResidentTimeZone));
        updateHourFormat();
        updateTime();
        updateDateLines();
    }

    public void setIs24HourFormat(boolean z2) {
        this.m24HourFormat = z2;
    }

    public void setOnLocalCityChangeListener(OnLocalCityChangeListener onLocalCityChangeListener) {
        this.mLocalCityChangeListener = onLocalCityChangeListener;
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public void setScaleRatio(float f2) {
        this.mScaleRatio = f2;
        updateViewsTextSize();
        updateViewsLayoutParams();
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public void setTextColorDark(boolean z2) {
        int color = z2 ? getContext().getResources().getColor(b.f2193b) : -1;
        this.mLocalCity.setTextColor(color);
        this.mLocalTime.setTextColor(color);
        this.mLocalDate.setTextColor(color);
        this.mResidentCity.setTextColor(color);
        this.mResidentTime.setTextColor(color);
        this.mResidentDate.setTextColor(color);
    }

    public void updateHourFormat() {
        this.m24HourFormat = DateFormat.is24HourFormat(this.mContext);
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public void updateResidentTimeZone(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        this.mResidentTimeZone = str;
        Log.i(TAG, "update resident timeZone:" + this.mResidentTimeZone);
        this.mResidentCalendar = new Calendar(TimeZone.getTimeZone(this.mResidentTimeZone));
        updateTime();
        updateResidentCityName();
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public void updateTime() {
        updateTime(this.mCalendar, this.mLocalTime, this.mLocalDate);
        updateTime(this.mResidentCalendar, this.mResidentTime, this.mResidentDate);
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public void updateTimeZone(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        this.mLocalTimeZone = str;
        Log.i(TAG, "update local timeZone:" + this.mLocalTimeZone);
        this.mCalendar = new Calendar(TimeZone.getTimeZone(this.mLocalTimeZone));
        updateTime();
        updateLocalCity();
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public void updateViewTopMargin(boolean z2) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
        if (z2) {
            layoutParams.topMargin = (int) (this.mScaleRatio * this.mContext.getResources().getDimensionPixelSize(c.f2210o));
        } else {
            layoutParams.topMargin = 0;
        }
        setLayoutParams(layoutParams);
    }

    protected void updateViewsLayoutParams() {
        Resources resources = this.mContext.getResources();
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
        layoutParams.topMargin = (int) (this.mScaleRatio * resources.getDimensionPixelSize(c.f2210o));
        setLayoutParams(layoutParams);
        int dimensionPixelSize = (int) (this.mScaleRatio * resources.getDimensionPixelSize(c.f2212q));
        int dimensionPixelSize2 = (int) (this.mScaleRatio * resources.getDimensionPixelSize(c.f2209n));
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.mLocalTime.getLayoutParams();
        layoutParams2.topMargin = dimensionPixelSize;
        this.mLocalTime.setLayoutParams(layoutParams2);
        LinearLayout.LayoutParams layoutParams3 = (LinearLayout.LayoutParams) this.mLocalDate.getLayoutParams();
        layoutParams3.topMargin = dimensionPixelSize2;
        this.mLocalDate.setLayoutParams(layoutParams3);
        LinearLayout.LayoutParams layoutParams4 = (LinearLayout.LayoutParams) this.mResidentTime.getLayoutParams();
        layoutParams4.topMargin = dimensionPixelSize;
        this.mResidentTime.setLayoutParams(layoutParams4);
        LinearLayout.LayoutParams layoutParams5 = (LinearLayout.LayoutParams) this.mResidentDate.getLayoutParams();
        layoutParams5.topMargin = dimensionPixelSize2;
        this.mResidentDate.setLayoutParams(layoutParams5);
        RelativeLayout.LayoutParams layoutParams6 = (RelativeLayout.LayoutParams) this.mResidentLayout.getLayoutParams();
        layoutParams6.setMarginStart((int) (this.mScaleRatio * resources.getDimensionPixelSize(c.f2220y)));
        this.mResidentLayout.setLayoutParams(layoutParams6);
    }

    protected void updateViewsTextSize() {
        Resources resources = this.mContext.getResources();
        float dimensionPixelSize = (int) (this.mScaleRatio * resources.getDimensionPixelSize(c.f2208m));
        this.mLocalCity.setTextSize(0, dimensionPixelSize);
        this.mResidentCity.setTextSize(0, dimensionPixelSize);
        this.mLocalDate.setTextSize(0, dimensionPixelSize);
        this.mResidentDate.setTextSize(0, dimensionPixelSize);
        float dimensionPixelSize2 = (int) (this.mScaleRatio * resources.getDimensionPixelSize(c.f2213r));
        this.mLocalTime.setTextSize(0, dimensionPixelSize2);
        this.mResidentTime.setTextSize(0, dimensionPixelSize2);
    }

    public MiuiDualClock(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mLanguage = "";
        this.mCountry = Locale.getDefault().getCountry();
        this.mAutoTimeZone = true;
        this.mDateTooLong = false;
        this.mScaleRatio = 1.0f;
        this.mAutoTimeZoneObserver = new ContentObserver(new Handler()) { // from class: miui.keyguard.clock.MiuiDualClock.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z2) {
                super.onChange(z2);
                MiuiDualClock miuiDualClock = MiuiDualClock.this;
                miuiDualClock.mAutoTimeZone = Settings.Global.getInt(miuiDualClock.mContext.getContentResolver(), "auto_time_zone", 0) > 0;
                MiuiDualClock.this.updateLocalCity();
            }
        };
        this.mContext = context;
        this.mAutoTimeZone = Settings.Global.getInt(context.getContentResolver(), "auto_time_zone", 0) > 0;
    }

    public void updateTime(Calendar calendar, TextView textView, TextView textView2) {
        int i2;
        calendar.setTimeInMillis(System.currentTimeMillis());
        textView.setText(DateUtils.formatDateTime(System.currentTimeMillis(), (this.m24HourFormat ? 32 : 16) | 76, calendar.getTimeZone()));
        if (this.mDateTooLong) {
            i2 = this.m24HourFormat ? h.g1 : h.h1;
        } else {
            i2 = this.m24HourFormat ? h.e1 : h.f1;
        }
        textView2.setText(calendar.format(this.mContext.getString(i2)));
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public void setClockAlpha(float f2) {
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public void setOwnerInfo(String str) {
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public void setShowLunarCalendar(boolean z2) {
    }
}
