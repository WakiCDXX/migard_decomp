package miui.keyguard.clock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import java.util.TimeZone;
import miui.os.Build;
import q.f;

/* JADX INFO: loaded from: classes.dex */
public class KeyguardClockController {
    private static final String AUTO_DUAL_CLOCK = "auto_dual_clock";
    public static final int HIDE_LUNAR_CALENDAR = 0;
    public static final int KEYGUARD_CLOCK_CENTER_HORIZONTAL = 1;
    public static final int KEYGUARD_CLOCK_CENTER_VERTICAL = 3;
    public static final int KEYGUARD_CLOCK_DEFAULT = 0;
    public static final int KEYGUARD_CLOCK_DUAL = 101;
    public static final int KEYGUARD_CLOCK_LEFT_TOP = 2;
    public static final int KEYGUARD_CLOCK_LEFT_TOP_LARGE = 4;
    private static final String KEY_SETTINGS_SYSTEM_SHOW_LUNAR_CALENDAR = "show_lunar_calendar";
    private static final String RESIDENT_TIMEZONE = "resident_timezone";
    public static final String SELECTED_KEYGUARD_CLOCK_POSITION = "selected_keyguard_clock_position";
    public static final int SHOW_LUNAR_CALENDAR = 1;
    public static final int UNDEFINED = -1;
    private IClockView mClockView;
    private ViewGroup mContainer;
    private Context mContext;
    private boolean mDualClockOpen;
    private String mResidentTimezone;
    private int mSelectedClockPosition;
    private boolean mShowDualClock;
    private String mCurrentTimezone = TimeZone.getDefault().getID();
    private boolean mAutoDualClock = true;
    private boolean mHasTopMargin = true;
    private String mOwnerString = null;
    private int mClockStyle = 0;
    private int mLastClockPosition = 0;
    private int mShowLunarCalendar = -1;
    private float mScaleRatio = 1.0f;
    private boolean mTextDark = false;
    private final Handler mHandler = new Handler();
    private boolean mAutoUpdateTime = true;
    private Runnable mUpdateTimeRunnable = new Runnable() { // from class: miui.keyguard.clock.KeyguardClockController.1
        @Override // java.lang.Runnable
        public void run() {
            if (KeyguardClockController.this.mClockView != null) {
                KeyguardClockController.this.mClockView.updateTime();
            }
        }
    };
    private final BroadcastReceiver mTimezoneChangeReceiver = new BroadcastReceiver() { // from class: miui.keyguard.clock.KeyguardClockController.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            KeyguardClockController.this.mHandler.post(new Runnable() { // from class: miui.keyguard.clock.KeyguardClockController.2.1
                @Override // java.lang.Runnable
                public void run() {
                    KeyguardClockController.this.mCurrentTimezone = TimeZone.getDefault().getID();
                    KeyguardClockController.this.updateDualClock();
                }
            });
        }
    };
    private final BroadcastReceiver mUpdateTimeReceiver = new BroadcastReceiver() { // from class: miui.keyguard.clock.KeyguardClockController.3
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            KeyguardClockController.this.mHandler.post(KeyguardClockController.this.mUpdateTimeRunnable);
        }
    };
    ContentObserver mDualClockOpenObserver = new ContentObserver(new Handler()) { // from class: miui.keyguard.clock.KeyguardClockController.4
        @Override // android.database.ContentObserver
        public void onChange(boolean z2) {
            super.onChange(z2);
            KeyguardClockController keyguardClockController = KeyguardClockController.this;
            keyguardClockController.mDualClockOpen = Settings.System.getInt(keyguardClockController.mContext.getContentResolver(), KeyguardClockController.AUTO_DUAL_CLOCK, 0) != 0;
            KeyguardClockController.this.updateDualClock();
        }
    };
    ContentObserver mResidentTimezoneObserver = new ContentObserver(new Handler()) { // from class: miui.keyguard.clock.KeyguardClockController.5
        @Override // android.database.ContentObserver
        public void onChange(boolean z2) {
            super.onChange(z2);
            KeyguardClockController keyguardClockController = KeyguardClockController.this;
            keyguardClockController.mResidentTimezone = Settings.System.getString(keyguardClockController.mContext.getContentResolver(), KeyguardClockController.RESIDENT_TIMEZONE);
            KeyguardClockController.this.updateDualClock();
        }
    };

    public interface IClockView {
        int getClockHeight();

        float getClockVisibleHeight();

        float getTopMargin();

        void setClockAlpha(float f2);

        void setOwnerInfo(String str);

        void setScaleRatio(float f2);

        void setShowLunarCalendar(boolean z2);

        void setTextColorDark(boolean z2);

        void updateResidentTimeZone(String str);

        void updateTime();

        void updateTimeZone(String str);

        void updateViewTopMargin(boolean z2);
    }

    public KeyguardClockController(Context context, ViewGroup viewGroup) {
        this.mDualClockOpen = false;
        this.mShowDualClock = false;
        this.mSelectedClockPosition = 0;
        this.mContext = context;
        this.mContainer = viewGroup;
        this.mSelectedClockPosition = Settings.System.getInt(context.getContentResolver(), SELECTED_KEYGUARD_CLOCK_POSITION, 0);
        this.mDualClockOpen = Settings.System.getInt(context.getContentResolver(), AUTO_DUAL_CLOCK, 0) != 0;
        String string = Settings.System.getString(context.getContentResolver(), RESIDENT_TIMEZONE);
        this.mResidentTimezone = string;
        this.mShowDualClock = (!this.mDualClockOpen || string == null || string.equals(this.mCurrentTimezone)) ? false : true;
        addClockView();
        updateKeyguardClock();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v15 */
    /* JADX WARN: Type inference failed for: r0v16 */
    /* JADX WARN: Type inference failed for: r0v17 */
    /* JADX WARN: Type inference failed for: r0v18 */
    /* JADX WARN: Type inference failed for: r0v19 */
    /* JADX WARN: Type inference failed for: r0v20 */
    /* JADX WARN: Type inference failed for: r0v3, types: [android.view.View] */
    /* JADX WARN: Type inference failed for: r1v2, types: [android.view.ViewGroup] */
    private void addClockView() {
        ?? Inflate;
        LayoutInflater layoutInflaterFrom = LayoutInflater.from(this.mContext);
        int clockPosition = getClockPosition();
        if (clockPosition == 0 || clockPosition == 1) {
            Inflate = layoutInflaterFrom.inflate(f.f2247a, this.mContainer, false);
        } else if (clockPosition == 2) {
            Inflate = layoutInflaterFrom.inflate(f.f2249c, this.mContainer, false);
        } else if (clockPosition == 3) {
            Inflate = layoutInflaterFrom.inflate(f.f2251e, this.mContainer, false);
        } else if (clockPosition != 4) {
            Inflate = clockPosition != 101 ? 0 : layoutInflaterFrom.inflate(f.f2248b, this.mContainer, false);
        } else {
            Inflate = layoutInflaterFrom.inflate(f.f2250d, this.mContainer, false);
        }
        this.mContainer.addView(Inflate);
        IClockView iClockView = (IClockView) Inflate;
        this.mClockView = iClockView;
        if (iClockView != null) {
            iClockView.updateResidentTimeZone(this.mResidentTimezone);
            this.mClockView.updateTimeZone(this.mCurrentTimezone);
            this.mClockView.setShowLunarCalendar(getShowLunarCalendar());
            this.mClockView.setScaleRatio(this.mScaleRatio);
            this.mClockView.setTextColorDark(this.mTextDark);
            this.mClockView.updateViewTopMargin(this.mHasTopMargin);
            this.mClockView.setOwnerInfo(this.mOwnerString);
        }
    }

    private int getClockPosition() {
        if (this.mShowDualClock) {
            return 101;
        }
        int i2 = this.mClockStyle;
        if (i2 != 0) {
            return i2;
        }
        int i3 = this.mSelectedClockPosition;
        return i3 != 0 ? i3 : getDefaultClockPosition();
    }

    private boolean getShowLunarCalendar() {
        int i2 = this.mShowLunarCalendar;
        if (i2 != -1) {
            return i2 == 1;
        }
        return Settings.System.getInt(this.mContext.getContentResolver(), KEY_SETTINGS_SYSTEM_SHOW_LUNAR_CALENDAR, 0) == 1;
    }

    private void registerDualClockObserver() {
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(AUTO_DUAL_CLOCK), false, this.mDualClockOpenObserver);
        this.mDualClockOpenObserver.onChange(false);
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(RESIDENT_TIMEZONE), false, this.mResidentTimezoneObserver);
        this.mResidentTimezoneObserver.onChange(false);
    }

    private void unregisterDualClockObserver() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mDualClockOpenObserver);
        this.mContext.getContentResolver().unregisterContentObserver(this.mResidentTimezoneObserver);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDualClock() {
        String str;
        this.mShowDualClock = this.mAutoDualClock && this.mDualClockOpen && (str = this.mResidentTimezone) != null && !str.equals(this.mCurrentTimezone);
        updateKeyguardClock();
    }

    public int getClockHeight() {
        return this.mClockView.getClockHeight();
    }

    public float getClockVisibleHeight() {
        return this.mClockView.getClockVisibleHeight();
    }

    public int getDefaultClockPosition() {
        String str = Build.DEVICE;
        return ("davinci".equals(str) || "davinciin".equals(str) || "raphael".equals(str) || "raphaelin".equals(str) || "chiron".equals(str) || "polaris".equals(str)) ? 3 : 0;
    }

    public float getTopMargin() {
        return this.mClockView.getTopMargin();
    }

    public void onAddToWindow() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
        this.mContext.registerReceiver(this.mTimezoneChangeReceiver, intentFilter);
        if (this.mAutoUpdateTime) {
            IntentFilter intentFilter2 = new IntentFilter();
            intentFilter2.addAction("android.intent.action.TIME_TICK");
            intentFilter2.addAction("android.intent.action.TIME_SET");
            this.mContext.registerReceiver(this.mUpdateTimeReceiver, intentFilter2);
        }
        registerDualClockObserver();
    }

    public void onRemoveFromWindow() {
        this.mContext.unregisterReceiver(this.mTimezoneChangeReceiver);
        if (this.mAutoUpdateTime) {
            this.mContext.unregisterReceiver(this.mUpdateTimeReceiver);
        }
        unregisterDualClockObserver();
    }

    public void setAutoDualClock(boolean z2) {
        this.mAutoDualClock = z2;
        updateDualClock();
    }

    public void setAutoUpdateTime(boolean z2) {
        this.mAutoUpdateTime = z2;
    }

    public void setClockAlpha(float f2) {
        this.mClockView.setClockAlpha(f2);
    }

    public void setClockStyle(int i2) {
        if (this.mClockStyle != i2) {
            this.mClockStyle = i2;
            updateKeyguardClock();
        }
    }

    public void setHasTopMargin(boolean z2) {
        if (this.mHasTopMargin != z2) {
            this.mHasTopMargin = z2;
            IClockView iClockView = this.mClockView;
            if (iClockView != null) {
                iClockView.updateViewTopMargin(z2);
            }
        }
    }

    public void setOwnerInfo(String str) {
        if (this.mOwnerString != str) {
            this.mOwnerString = str;
            IClockView iClockView = this.mClockView;
            if (iClockView != null) {
                iClockView.setOwnerInfo(str);
            }
        }
    }

    public void setScaleRatio(float f2) {
        if (this.mScaleRatio != f2) {
            this.mScaleRatio = f2;
            IClockView iClockView = this.mClockView;
            if (iClockView != null) {
                iClockView.setScaleRatio(f2);
            }
        }
    }

    public void setShowLunarCalendar(int i2) {
        if (this.mShowLunarCalendar != i2) {
            this.mShowLunarCalendar = i2;
            IClockView iClockView = this.mClockView;
            if (iClockView != null) {
                iClockView.setShowLunarCalendar(getShowLunarCalendar());
            }
        }
    }

    public void setTextColorDark(boolean z2) {
        if (this.mTextDark != z2) {
            this.mTextDark = z2;
            IClockView iClockView = this.mClockView;
            if (iClockView != null) {
                iClockView.setTextColorDark(z2);
            }
        }
    }

    public void updateKeyguardClock() {
        if (getClockPosition() != this.mLastClockPosition) {
            this.mLastClockPosition = getClockPosition();
            this.mContainer.removeAllViews();
            addClockView();
        }
    }

    public void updateTime() {
        IClockView iClockView = this.mClockView;
        if (iClockView != null) {
            iClockView.updateTime();
        }
    }
}
