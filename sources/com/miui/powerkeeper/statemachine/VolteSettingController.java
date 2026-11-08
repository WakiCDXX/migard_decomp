package com.miui.powerkeeper.statemachine;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import com.miui.powerkeeper.TimeSpan;
import com.miui.powerkeeper.powerchecker.NightStandbyRecord;
import com.miui.powerkeeper.provider.SimpleSettingKeys;
import com.miui.powerkeeper.provider.SimpleSettings;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* JADX INFO: loaded from: classes.dex */
public class VolteSettingController implements TimeSpan.a {
    private static final String ACTION_SCREEN_OFF_TIMEOUT = "action.powerkeeper.volte.screenoff_timeout";
    public static final String FUNC_VOLTE = "volte";
    private static final long TIMEOUT = 1800000;
    private AlarmManager mAlarmManager;
    private Context mContext;
    private boolean mHasChanged;
    private boolean mInNight;
    private boolean mIsScreenOn;
    private boolean mLastSetting;
    private PowerManager mPowerManager;
    private boolean mReceiverRegisterred;
    private boolean mVolteEnabled;
    private static final String TAG = "PowerKeeper.Volte";
    public static final boolean D = Log.isLoggable(TAG, 3);
    private BroadcastReceiver mVoLteReceiver = new BroadcastReceiver() { // from class: com.miui.powerkeeper.statemachine.VolteSettingController.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(VolteSettingController.TAG, "receive " + action);
            if ("android.intent.action.SCREEN_ON".equals(action)) {
                VolteSettingController.this.mIsScreenOn = true;
                VolteSettingController.this.changeSettingIfNecessary(false);
                VolteSettingController.this.cancelScreenOffTimeout();
            } else {
                if ("android.intent.action.SCREEN_OFF".equals(action)) {
                    VolteSettingController.this.mIsScreenOn = false;
                    if (VolteSettingController.this.mInNight) {
                        VolteSettingController.this.setScreenOffTimeout();
                        return;
                    }
                    return;
                }
                if (VolteSettingController.ACTION_SCREEN_OFF_TIMEOUT.equals(action) && VolteSettingController.this.mInNight && !VolteSettingController.this.mIsScreenOn) {
                    VolteSettingController.this.changeSettingIfNecessary(false);
                }
            }
        }
    };
    private boolean mAlarmSet = false;

    public VolteSettingController(Context context) {
        this.mVolteEnabled = false;
        this.mContext = context;
        boolean zIsVolteEnabledByPlatform = isVolteEnabledByPlatform(context);
        this.mVolteEnabled = zIsVolteEnabledByPlatform;
        if (zIsVolteEnabledByPlatform) {
            Log.d(TAG, "volte is supported");
            this.mInNight = false;
            this.mIsScreenOn = true;
            this.mReceiverRegisterred = false;
            this.mHasChanged = false;
            this.mLastSetting = true;
            this.mPowerManager = (PowerManager) context.getSystemService(NightStandbyRecord.KEY_POWER);
            this.mAlarmManager = (AlarmManager) context.getSystemService("alarm");
            changeSettingIfNecessary(true);
            removeUserVolteSetting(context);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelScreenOffTimeout() {
        if (this.mAlarmSet) {
            if (D) {
                Log.d(TAG, "cancelScreenOffTimeout");
            }
            this.mAlarmManager.cancel(PendingIntent.getBroadcast(this.mContext, 0, new Intent(ACTION_SCREEN_OFF_TIMEOUT), 67108864));
            this.mAlarmSet = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changeSettingIfNecessary(boolean z2) {
        if (z2 && hasUserVolteSettting()) {
            Log.d(TAG, "restore user volte setting");
            setEnhanced4gLteModeSetting(this.mContext, getUserVolteSettting(this.mContext));
            return;
        }
        if (!this.mInNight || this.mIsScreenOn) {
            Log.d(TAG, "night or screen changed");
            if (this.mHasChanged) {
                this.mLastSetting = !this.mLastSetting;
                Log.d(TAG, "change volte setting");
                setEnhanced4gLteModeSetting(this.mContext, this.mLastSetting);
                this.mHasChanged = false;
                removeUserVolteSetting(this.mContext);
                return;
            }
            return;
        }
        Log.d(TAG, "night and screen off");
        if (isEnhanced4gLteModeSettingEnabledByUser(this.mContext)) {
            Log.d(TAG, "change volte setting");
            setEnhanced4gLteModeSetting(this.mContext, false);
            this.mHasChanged = true;
            this.mLastSetting = false;
            saveUserVolteSetting(this.mContext, true);
        }
    }

    private boolean getUserVolteSettting(Context context) {
        return SimpleSettings.Misc.getBoolean(context, SimpleSettingKeys.KEY_USER_VOLTE_SETTING, true);
    }

    private boolean hasUserVolteSettting() {
        return SimpleSettings.Misc.contains(this.mContext, SimpleSettingKeys.KEY_USER_VOLTE_SETTING);
    }

    public static boolean isEnhanced4gLteModeSettingEnabledByUser(Context context) {
        try {
            boolean zBooleanValue = ((Boolean) Class.forName("com.android.ims.ImsManager").getMethod("isEnhanced4gLteModeSettingEnabledByUser", Context.class).invoke(null, context)).booleanValue();
            if (D) {
                Log.d(TAG, "is user set support volte: " + zBooleanValue);
            }
            return zBooleanValue;
        } catch (Exception unused) {
            return false;
        }
    }

    public static boolean isVolteEnabledByPlatform(Context context) {
        try {
            return ((Boolean) Class.forName("com.android.ims.ImsManager").getMethod("isVolteEnabledByPlatform", Context.class).invoke(null, context)).booleanValue();
        } catch (Exception unused) {
            return false;
        }
    }

    private void registerScreenChanged() {
        if (this.mReceiverRegisterred) {
            return;
        }
        IntentFilter intentFilter = new IntentFilter("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        intentFilter.addAction(ACTION_SCREEN_OFF_TIMEOUT);
        this.mContext.registerReceiver(this.mVoLteReceiver, intentFilter, 2);
        this.mReceiverRegisterred = true;
    }

    private void removeUserVolteSetting(Context context) {
        SimpleSettings.Misc.delete(this.mContext, SimpleSettingKeys.KEY_USER_VOLTE_SETTING);
    }

    private void saveUserVolteSetting(Context context, boolean z2) {
        SimpleSettings.Misc.getBoolean(context, SimpleSettingKeys.KEY_USER_VOLTE_SETTING, z2);
    }

    private void setEnhanced4gLteModeSetting(Context context, boolean z2) {
        try {
            Class.forName("com.android.ims.ImsManager").getMethod("setEnhanced4gLteModeSetting", Context.class, Boolean.TYPE).invoke(null, context, Boolean.valueOf(z2));
        } catch (Exception unused) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setScreenOffTimeout() {
        if (SimpleSettings.Misc.getBoolean(this.mContext, FUNC_VOLTE, false)) {
            if (D) {
                Log.d(TAG, "setScreenOffTimeout");
            }
            this.mAlarmManager.setExact(3, SystemClock.elapsedRealtime() + 1800000, PendingIntent.getBroadcast(this.mContext, 0, new Intent(ACTION_SCREEN_OFF_TIMEOUT), 67108864));
            this.mAlarmSet = true;
        }
    }

    private void unRegisterScreenChanged() {
        if (this.mReceiverRegisterred) {
            this.mContext.unregisterReceiver(this.mVoLteReceiver);
            this.mReceiverRegisterred = false;
        }
    }

    public void destroy() {
        reset();
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("dump VolteSettingController:");
        printWriter.print("\tfunc enabled = " + SimpleSettings.Misc.getBoolean(this.mContext, FUNC_VOLTE, false));
        printWriter.print(", platform lte support = " + this.mVolteEnabled);
        printWriter.println(", in night = " + this.mInNight);
    }

    @Override // com.miui.powerkeeper.TimeSpan.a
    public void inTimeSpan() {
        if (SimpleSettings.Misc.getBoolean(this.mContext, FUNC_VOLTE, false)) {
            Log.d(TAG, "enter night");
            this.mInNight = true;
            boolean zIsScreenOn = this.mPowerManager.isScreenOn();
            this.mIsScreenOn = zIsScreenOn;
            if (zIsScreenOn) {
                changeSettingIfNecessary(false);
            } else if (this.mInNight) {
                setScreenOffTimeout();
            }
            registerScreenChanged();
        }
    }

    @Override // com.miui.powerkeeper.TimeSpan.a
    public void outTimeSpan() {
        Log.d(TAG, "exit night");
        this.mInNight = false;
        changeSettingIfNecessary(false);
        unRegisterScreenChanged();
        cancelScreenOffTimeout();
    }

    public void reset() {
        this.mInNight = false;
        this.mIsScreenOn = true;
        unRegisterScreenChanged();
        if (this.mHasChanged) {
            this.mLastSetting = true ^ this.mLastSetting;
            Log.d(TAG, "reset volte setting");
            setEnhanced4gLteModeSetting(this.mContext, this.mLastSetting);
            this.mHasChanged = false;
        }
    }
}
