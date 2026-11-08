package com.miui.powerkeeper.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import com.android.internal.os.BackgroundThread;
import com.google.android.collect.Lists;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.event.EventsAggregator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import miui.provider.Weather;

/* JADX INFO: loaded from: classes.dex */
public class BatteryStateObserver {
    private static final String ACTION_SYS_LOWPOWER_CONTROL = "sys.action.lowbattery.control";
    private static final String DATA_SYS_LOWPOWER_CONTROL = "cloud_LowPower_Control";
    private static final String TAG = "BatteryStateObserver";
    int mBatteryHealth;
    int mBatteryLevel;
    int mBatteryPlugged;
    int mBatteryScale;
    int mBatteryStatus;
    int mBatteryTemperature;
    private Context mContext;
    private EventsAggregator mEventsAggregator;
    private boolean mIsSupportLowperControl;
    private int mLowPowercontrolCode;
    private List<PowerKeeperInterface.d> mBatteryChangedCallbacks = Lists.newArrayList();
    private List<String> mSupportDeviceList = new ArrayList(Arrays.asList("ruyi"));
    private BroadcastReceiver mBatteryStatusReceiver = new BroadcastReceiver() { // from class: com.miui.powerkeeper.utils.BatteryStateObserver.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.BATTERY_CHANGED")) {
                int intExtra = intent.getIntExtra("status", 1);
                int intExtra2 = intent.getIntExtra("health", 1);
                int intExtra3 = intent.getIntExtra("level", -1);
                int intExtra4 = intent.getIntExtra(Weather.WeatherBaseColumns.TEMPERATURE, 0) / 10;
                int intExtra5 = intent.getIntExtra("plugged", -1);
                int intExtra6 = intent.getIntExtra("scale", -1);
                boolean zIsCharging = BatteryStateObserver.this.isCharging();
                BatteryStateObserver batteryStateObserver = BatteryStateObserver.this;
                if (intExtra != batteryStateObserver.mBatteryStatus || intExtra2 != batteryStateObserver.mBatteryHealth || intExtra3 != batteryStateObserver.mBatteryLevel || intExtra4 != batteryStateObserver.mBatteryTemperature || intExtra5 != batteryStateObserver.mBatteryPlugged) {
                    batteryStateObserver.mBatteryStatus = intExtra;
                    batteryStateObserver.mBatteryHealth = intExtra2;
                    if (batteryStateObserver.mBatteryLevel != intExtra3) {
                        batteryStateObserver.mEventsAggregator.notifyBatteryLevelListeners(intExtra3, BatteryStateObserver.this.mBatteryLevel, null);
                        BatteryStateObserver batteryStateObserver2 = BatteryStateObserver.this;
                        batteryStateObserver2.mBatteryLevel = intExtra3;
                        batteryStateObserver2.sendLowPowerControlBroadcast();
                    }
                    BatteryStateObserver batteryStateObserver3 = BatteryStateObserver.this;
                    batteryStateObserver3.mBatteryTemperature = intExtra4;
                    batteryStateObserver3.mBatteryPlugged = intExtra5;
                    batteryStateObserver3.mBatteryScale = intExtra6;
                    if (zIsCharging != batteryStateObserver3.isCharging()) {
                        BatteryStateObserver.this.mEventsAggregator.notifyForCharging(!zIsCharging);
                    }
                }
                BatteryStateObserver.this.notifyBatteryChangedListeners();
            }
        }
    };

    public BatteryStateObserver(Context context, EventsAggregator eventsAggregator) {
        this.mIsSupportLowperControl = false;
        this.mLowPowercontrolCode = 0;
        this.mBatteryStatus = 1;
        this.mBatteryHealth = 1;
        this.mBatteryLevel = -1;
        this.mBatteryTemperature = 0;
        this.mBatteryPlugged = -1;
        this.mBatteryScale = -1;
        this.mContext = context;
        this.mEventsAggregator = eventsAggregator;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
        Intent intentRegisterReceiver = this.mContext.registerReceiver(this.mBatteryStatusReceiver, intentFilter, 4);
        this.mBatteryStatus = intentRegisterReceiver.getIntExtra("status", 1);
        this.mBatteryHealth = intentRegisterReceiver.getIntExtra("health", 1);
        this.mBatteryLevel = intentRegisterReceiver.getIntExtra("level", -1);
        this.mBatteryTemperature = intentRegisterReceiver.getIntExtra(Weather.WeatherBaseColumns.TEMPERATURE, 0) / 10;
        this.mBatteryPlugged = intentRegisterReceiver.getIntExtra("plugged", -1);
        this.mBatteryScale = intentRegisterReceiver.getIntExtra("scale", -1);
        boolean zContains = this.mSupportDeviceList.contains(Build.DEVICE);
        this.mIsSupportLowperControl = zContains;
        if (zContains) {
            registerLowpowercontrolObserver();
            this.mLowPowercontrolCode = 1;
            updateLowPowerControl();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyBatteryChangedListeners() {
        synchronized (this.mBatteryChangedCallbacks) {
            try {
                for (final PowerKeeperInterface.d dVar : this.mBatteryChangedCallbacks) {
                    if (dVar != null) {
                        Handler handler = dVar.f251a;
                        if (handler == null) {
                            handler = BackgroundThread.getHandler();
                        }
                        handler.post(new Runnable() { // from class: com.miui.powerkeeper.utils.BatteryStateObserver.3
                            @Override // java.lang.Runnable
                            public void run() {
                                PowerKeeperInterface.m mVar = dVar.f252b;
                                if (mVar != null) {
                                    BatteryStateObserver batteryStateObserver = BatteryStateObserver.this;
                                    mVar.onBatteryChanged(batteryStateObserver.mBatteryStatus, batteryStateObserver.mBatteryHealth, batteryStateObserver.mBatteryLevel, batteryStateObserver.mBatteryTemperature, batteryStateObserver.mBatteryPlugged, batteryStateObserver.mBatteryScale);
                                }
                            }
                        });
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private void registerLowpowercontrolObserver() {
        Log.d(TAG, "register lowpowercontrol Observer");
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(DATA_SYS_LOWPOWER_CONTROL), false, new ContentObserver(BackgroundThread.getHandler()) { // from class: com.miui.powerkeeper.utils.BatteryStateObserver.2
            @Override // android.database.ContentObserver
            public void onChange(boolean z2, Uri uri) {
                Log.d(BatteryStateObserver.TAG, "lowpowercontrol changed: " + z2 + " database uri : " + uri);
                if (uri == null || !uri.equals(Settings.System.getUriFor(BatteryStateObserver.DATA_SYS_LOWPOWER_CONTROL))) {
                    return;
                }
                BatteryStateObserver.this.updateLowPowerControl();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendLowPowerControlBroadcast() {
        int i2;
        if (this.mIsSupportLowperControl) {
            Intent intent = new Intent(ACTION_SYS_LOWPOWER_CONTROL);
            int i3 = this.mBatteryLevel;
            int i4 = (i3 <= 0 || (i2 = this.mBatteryScale) <= 0) ? 0 : (int) ((i3 * 100.0f) / i2);
            Log.d(TAG, "lowpowercontrol Broadcast BatteryLevel: " + i4 + " mLowPowercontrolCode=" + this.mLowPowercontrolCode);
            intent.putExtra("batterylevel", i4);
            int i5 = this.mLowPowercontrolCode;
            if (i5 == 0) {
                intent.putExtra("audioControl", 0);
                intent.putExtra("falshClose", 0);
                intent.putExtra("modemControl", 0);
            } else if (2 == i5) {
                intent.putExtra("audioControl", 0);
                intent.putExtra("falshClose", 1);
                intent.putExtra("modemControl", 1);
            } else if (3 == i5) {
                intent.putExtra("audioControl", 1);
                intent.putExtra("falshClose", 0);
                intent.putExtra("modemControl", 1);
            } else if (1 == i5) {
                intent.putExtra("audioControl", 1);
                intent.putExtra("falshClose", 1);
                intent.putExtra("modemControl", 1);
            }
            Context context = this.mContext;
            if (context != null) {
                context.sendStickyBroadcast(intent);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateLowPowerControl() {
        String stringForUser = Settings.System.getStringForUser(this.mContext.getContentResolver(), DATA_SYS_LOWPOWER_CONTROL, -2);
        if (stringForUser == null || stringForUser.equals("1")) {
            this.mLowPowercontrolCode = 1;
        } else if (stringForUser.equals("0")) {
            this.mLowPowercontrolCode = 0;
        } else if (stringForUser.equals("2")) {
            this.mLowPowercontrolCode = 2;
        } else if (stringForUser.equals("3")) {
            this.mLowPowercontrolCode = 3;
        }
        Log.d(TAG, "update from DB lowpowercontrol:" + stringForUser + " mLowPowercontrolCode=" + this.mLowPowercontrolCode);
        sendLowPowerControlBroadcast();
    }

    public int getBatteryCurLevel() {
        return this.mBatteryLevel;
    }

    public int getBatteryCurTemp() {
        return this.mBatteryTemperature;
    }

    public boolean isCharging() {
        int i2 = this.mBatteryStatus;
        if (i2 == 2) {
            return true;
        }
        return i2 == 5 && (this.mBatteryPlugged & 15) != 0;
    }

    public void registerBatteryChangedListener(PowerKeeperInterface.d dVar) {
        synchronized (this.mBatteryChangedCallbacks) {
            try {
                if (!this.mBatteryChangedCallbacks.contains(dVar)) {
                    this.mBatteryChangedCallbacks.add(dVar);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void unregisterBatteryChangedListener(PowerKeeperInterface.d dVar) {
        synchronized (this.mBatteryChangedCallbacks) {
            try {
                if (this.mBatteryChangedCallbacks.contains(dVar)) {
                    this.mBatteryChangedCallbacks.remove(dVar);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
