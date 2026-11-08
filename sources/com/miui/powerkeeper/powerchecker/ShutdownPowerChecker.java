package com.miui.powerkeeper.powerchecker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.Log;
import com.android.internal.app.IBatteryStats;
import com.miui.powerkeeper.cloudcontrol.CloudFunctionConfig;
import com.miui.powerkeeper.provider.SimpleSettingKeys;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.utils.Constant;
import com.miui.powerkeeper.utils.FileUtil;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class ShutdownPowerChecker extends Handler {
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    private static final int DEFAULT_DIS_START_LEVEL_THRESHOLD = 90;
    private static final int DEFAULT_LEAST_CHARGING_TIME = 5;
    private static final int DEFAULT_SCREEN_ON_RATIO_THRESHOLD = 20;
    private static final int DEFAULT_TIME_ON_BATTERY_THRESHOLD = 3;
    public static final int EVENT_BOOT_COMPLETED = 2;
    public static final int EVENT_POWER_DISCONNECTED = 3;
    public static final int EVENT_SHUT_DOWN = 0;
    public static final int EVENT_UPDATE_CLOUD = 1;
    private static final int INVALID_BATTERY_LEVEL = -1;
    private static final int SHUTDOWN_BATTERY_LEVEL = 2;
    private static final int STATE_ABNORMAL_BOOTUP = 3;
    private static final int STATE_CHARGE_SCREEN_OFF = 7;
    private static final int STATE_CHARGE_SCREEN_ON = 6;
    private static final int STATE_DISCHARGE_SCREEN_OFF = 5;
    private static final int STATE_DISCHARGE_SCREEN_ON = 4;
    private static final int STATE_INIT = 0;
    private static final int STATE_NORMAL_BOOTUP = 2;
    private static final int STATE_SHUTDOWN = 1;
    private static final String TAG = "ShutdownPowerChecker";
    private Context mContext;
    private int mDefaultDisStartLevelThreshold;
    private int mDefaultScreenOnRatioThreshold;
    private int mDefaultTimeOnBatteryThreshold;
    int mDischargeAmountScreenOff3;
    int mDischargeAmountScreenOn3;
    long mDischargePlugTime3;
    long mDischargeTimeScreenOff3;
    long mDischargeTimeScreenOn3;
    int mDischargeUnplugLevel3;
    private boolean mEnabled;
    private boolean mFastCheck;
    private CloudFunctionConfig.ICloudFunctionListener mFunctionListener;
    int mRebootCount3;
    private BroadcastReceiver mReceiver;
    int mShortChargeCount3;

    private class Record {
        private long mBatteryRealtime;
        private long mBatteryRealtimeSinceCharge;
        private long mBatteryScreenOffRealtime;
        private long mBatteryScreenOffRealtime3;
        private long mBatteryScreenOffRealtimeSinceCharge;
        private long mBatteryScreenOnRealtime3;
        private int mDischargeAmountScreenOff;
        private int mDischargeAmountScreenOff3;
        private int mDischargeAmountScreenOffSinceCharge;
        private int mDischargeAmountScreenOn;
        private int mDischargeAmountScreenOn3;
        private int mDischargeAmountScreenOnSinceCharge;
        private int mDischargeCurrentLevel;
        private int mDischargeStartLevel;
        private int mDischargeUnplugLevel3;
        private boolean mReboot;
        private int mRebootCount3;
        private long mRecordTime = System.currentTimeMillis();
        private int mShortChargeCount3;

        public Record(int i2, int i3, int i4, int i5, long j2, long j3, int i6, int i7, long j4, long j5, boolean z2, int i8, int i9, int i10, long j6, long j7, int i11, int i12) {
            this.mDischargeCurrentLevel = i2;
            this.mDischargeStartLevel = i3;
            this.mDischargeAmountScreenOn = i4;
            this.mDischargeAmountScreenOff = i5;
            this.mBatteryRealtime = j2;
            this.mBatteryScreenOffRealtime = j3;
            this.mDischargeAmountScreenOnSinceCharge = i6;
            this.mDischargeAmountScreenOffSinceCharge = i7;
            this.mBatteryRealtimeSinceCharge = j4;
            this.mBatteryScreenOffRealtimeSinceCharge = j5;
            this.mReboot = z2;
            this.mDischargeUnplugLevel3 = i8;
            this.mDischargeAmountScreenOn3 = i9;
            this.mDischargeAmountScreenOff3 = i10;
            this.mBatteryScreenOnRealtime3 = j6;
            this.mBatteryScreenOffRealtime3 = j7;
            this.mRebootCount3 = i11;
            this.mShortChargeCount3 = i12;
        }

        private void addJsonObject(JSONObject jSONObject, String str, Object obj) {
            if (jSONObject == null || obj == null) {
                return;
            }
            try {
                if ((obj instanceof String) && ((String) obj).isEmpty()) {
                    return;
                }
                jSONObject.put(str, obj);
            } catch (JSONException unused) {
            }
        }

        public JSONObject toJson() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            JSONObject jSONObject = new JSONObject();
            addJsonObject(jSONObject, "dis_cur_level", Integer.valueOf(this.mDischargeCurrentLevel));
            addJsonObject(jSONObject, "dis_start_level", Integer.valueOf(this.mDischargeStartLevel));
            addJsonObject(jSONObject, "dis_amount_screen_on", Integer.valueOf(this.mDischargeAmountScreenOn));
            addJsonObject(jSONObject, "dis_amount_screen_off", Integer.valueOf(this.mDischargeAmountScreenOff));
            addJsonObject(jSONObject, PowerCheckerProcessNotifier.KEY_TIME_ON_BATTERY, Long.valueOf(this.mBatteryRealtime));
            addJsonObject(jSONObject, "time_on_battery_screen_off", Long.valueOf(this.mBatteryScreenOffRealtime));
            addJsonObject(jSONObject, "dis_amount_screen_on_c", Integer.valueOf(this.mDischargeAmountScreenOnSinceCharge));
            addJsonObject(jSONObject, "dis_amount_screen_off_c", Integer.valueOf(this.mDischargeAmountScreenOffSinceCharge));
            addJsonObject(jSONObject, "time_on_battery_c", Long.valueOf(this.mBatteryRealtimeSinceCharge));
            addJsonObject(jSONObject, "time_on_battery_screen_off_c", Long.valueOf(this.mBatteryScreenOffRealtimeSinceCharge));
            addJsonObject(jSONObject, "record_time", simpleDateFormat.format(new Date(this.mRecordTime)));
            addJsonObject(jSONObject, "reboot", Boolean.valueOf(this.mReboot));
            addJsonObject(jSONObject, "dis_unplug_level_3", Integer.valueOf(this.mDischargeUnplugLevel3));
            addJsonObject(jSONObject, "dis_amount_screen_on_3", Integer.valueOf(this.mDischargeAmountScreenOn3));
            addJsonObject(jSONObject, "dis_amount_screen_off_3", Integer.valueOf(this.mDischargeAmountScreenOff3));
            addJsonObject(jSONObject, "time_on_battery_screen_on_3", Long.valueOf(this.mBatteryScreenOnRealtime3));
            addJsonObject(jSONObject, "time_on_battery_screen_off_3", Long.valueOf(this.mBatteryScreenOffRealtime3));
            addJsonObject(jSONObject, "reboot_count_3", Integer.valueOf(this.mRebootCount3));
            addJsonObject(jSONObject, "short_charge_count_3", Integer.valueOf(this.mShortChargeCount3));
            return jSONObject;
        }
    }

    public ShutdownPowerChecker(Context context, Looper looper) {
        super(looper);
        this.mDefaultDisStartLevelThreshold = DEFAULT_DIS_START_LEVEL_THRESHOLD;
        this.mDefaultTimeOnBatteryThreshold = 3;
        this.mDefaultScreenOnRatioThreshold = 20;
        this.mFastCheck = false;
        this.mReceiver = new BroadcastReceiver() { // from class: com.miui.powerkeeper.powerchecker.ShutdownPowerChecker.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                if (ShutdownPowerChecker.DEBUG) {
                    Log.d(ShutdownPowerChecker.TAG, "receive: " + action);
                }
                if ("android.intent.action.ACTION_SHUTDOWN".equals(action)) {
                    ShutdownPowerChecker.this.sendEmptyMessage(0);
                } else if ("android.intent.action.ACTION_POWER_DISCONNECTED".equals(action)) {
                    ShutdownPowerChecker.this.sendEmptyMessage(3);
                }
            }
        };
        this.mFunctionListener = new CloudFunctionConfig.ICloudFunctionListener() { // from class: com.miui.powerkeeper.powerchecker.ShutdownPowerChecker.2
            @Override // com.miui.powerkeeper.cloudcontrol.CloudFunctionConfig.ICloudFunctionListener
            public void onCloudFunctionUpdated() {
                ShutdownPowerChecker shutdownPowerChecker = ShutdownPowerChecker.this;
                shutdownPowerChecker.sendMessage(shutdownPowerChecker.obtainMessage(1));
            }
        };
        this.mDischargeUnplugLevel3 = 0;
        this.mDischargeAmountScreenOn3 = 0;
        this.mDischargeAmountScreenOff3 = 0;
        this.mDischargeTimeScreenOn3 = 0L;
        this.mDischargeTimeScreenOff3 = 0L;
        this.mRebootCount3 = 0;
        this.mShortChargeCount3 = 0;
        this.mContext = context;
        boolean z2 = SimpleSettings.Misc.getBoolean(context, Constant.FUNC_SHUTDOWN_CHECK, false);
        this.mEnabled = z2;
        if (z2) {
            registerBroadcast();
            loadShutdownAbnormalThreshold();
        }
        CloudFunctionConfig.getInstance().registerCloudFunctionConfig(this.mFunctionListener);
    }

    private void catchPowerIssueLogs() {
        if (DEBUG) {
            Log.d(TAG, "record shutdown power abnormal");
        }
        Intent intent = new Intent("com.miui.klo.COMMON_LOG");
        intent.setPackage("com.miui.klo.bugreport");
        intent.putExtra("name", TAG);
        intent.putExtra("logcatCmd", "batterystats");
        intent.putExtra("type", 64);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.CURRENT);
    }

    private int getBatteryLevel() {
        Intent intentRegisterReceiver = this.mContext.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"), 4);
        int intExtra = -1;
        if (intentRegisterReceiver != null) {
            intExtra = intentRegisterReceiver.getIntExtra("level", -1);
            if (DEBUG) {
                Log.d(TAG, "level = " + intExtra);
            }
        }
        return intExtra;
    }

    private boolean isCharging() {
        Intent intentRegisterReceiver = this.mContext.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"), 4);
        if (intentRegisterReceiver == null) {
            return false;
        }
        int intExtra = intentRegisterReceiver.getIntExtra("status", -1);
        if (intExtra == 2) {
            return true;
        }
        return intExtra == 5 && (intentRegisterReceiver.getIntExtra("plugged", -1) & 15) != 0;
    }

    private void loadShutdownAbnormalThreshold() {
        try {
            this.mDefaultDisStartLevelThreshold = Integer.parseInt(CloudFunctionConfig.getFunctionParameterAtIndex(this.mContext, Constant.FUNC_SHUTDOWN_CHECK, 0, "90"));
            this.mDefaultTimeOnBatteryThreshold = Integer.parseInt(CloudFunctionConfig.getFunctionParameterAtIndex(this.mContext, Constant.FUNC_SHUTDOWN_CHECK, 1, "3"));
            this.mDefaultScreenOnRatioThreshold = Integer.parseInt(CloudFunctionConfig.getFunctionParameterAtIndex(this.mContext, Constant.FUNC_SHUTDOWN_CHECK, 2, "20"));
        } catch (Exception e2) {
            Log.e(TAG, "get shut down parameters failed: ", e2);
        }
        Log.d(TAG, "shutdown power params, mDefaultDisStartLevelThreshold: " + this.mDefaultDisStartLevelThreshold + " mDefaultTimeOnBatteryThreshold: " + this.mDefaultTimeOnBatteryThreshold + " mDefaultScreenOnRatioThreshold: " + this.mDefaultScreenOnRatioThreshold);
    }

    private void logd(String str) {
        if (DEBUG) {
            Log.d(TAG, str);
        }
    }

    private void onBootCompleted() throws Throwable {
        if (this.mEnabled) {
            boolean z2 = SimpleSettings.Misc.getBoolean(this.mContext, SimpleSettingKeys.KEY_SHUTDOWN_ANALYTICS, false);
            if (DEBUG) {
                Log.d(TAG, "onBootCompleted, analytics : " + z2);
            }
            if (z2) {
                String fromFile = FileUtil.readFromFile(this.mContext.getFilesDir().getAbsolutePath() + "/" + SimpleSettingKeys.SHUTDOWN_FILE_NAME);
                if (fromFile != null) {
                    SimpleSettings.Misc.putBoolean(this.mContext, SimpleSettingKeys.KEY_SHUTDOWN_ANALYTICS, false);
                    try {
                        JSONObject jSONObject = new JSONObject(fromFile);
                        int i2 = jSONObject.getInt("dis_start_level");
                        long j2 = jSONObject.getLong(PowerCheckerProcessNotifier.KEY_TIME_ON_BATTERY);
                        long j3 = jSONObject.getLong("time_on_battery_screen_off");
                        double d2 = (j2 <= 0 || j2 <= j3) ? 0.0d : ((j2 - j3) * 100.0d) / j2;
                        Log.d(TAG, "onBootCompleted, dischargeStartLevel : " + i2 + " batteryRealtime : " + j2 + " batteryScreenOffRealtime : " + j3 + " batteryScreenOnRealtimeRatio : " + d2);
                        if (i2 >= this.mDefaultDisStartLevelThreshold && j2 <= this.mDefaultTimeOnBatteryThreshold * 3600000 && d2 <= this.mDefaultScreenOnRatioThreshold) {
                            catchPowerIssueLogs();
                        }
                    } catch (JSONException e2) {
                        Log.e(TAG, "read shut down power file failed, e:" + e2);
                    }
                }
            }
            if (isCharging()) {
                return;
            }
            onPowerDisconnected(true);
        }
    }

    private void onCloudUpdated() {
        boolean z2 = SimpleSettings.Misc.getBoolean(this.mContext, Constant.FUNC_SHUTDOWN_CHECK, false);
        Log.d(TAG, "get shutdown power enabled: " + z2);
        if (z2) {
            loadShutdownAbnormalThreshold();
        }
        if (this.mEnabled == z2) {
            Log.d(TAG, "get shutdown power enabled not change");
            return;
        }
        this.mEnabled = z2;
        if (z2) {
            registerBroadcast();
            return;
        }
        unRegisterBroadcast();
        removeMessages(0);
        removeMessages(2);
        removeMessages(3);
    }

    private void onPowerDisconnected(boolean z2) {
        IBatteryStats batteryStats;
        if (this.mEnabled && (batteryStats = BatteryStatsHelper.getBatteryStats()) != null) {
            try {
                long jElapsedRealtime = SystemClock.elapsedRealtime() * 1000;
                int dischargeCurrentLevel = batteryStats.getDischargeCurrentLevel();
                long jComputeBatteryScreenOffRealtime = batteryStats.computeBatteryScreenOffRealtime(jElapsedRealtime, 0);
                if (DEBUG) {
                    Log.d(TAG, "dischargeCurrentLevel = " + dischargeCurrentLevel + " batteryScreenOffRealtimeSinceCharge = " + jComputeBatteryScreenOffRealtime + " reboot = " + z2);
                }
                if (jComputeBatteryScreenOffRealtime >= 0) {
                    SimpleSettings.Misc.putLong(this.mContext, SimpleSettingKeys.KEY_LAST_BATTERY_SCREENOFF_REALTIME_C, jComputeBatteryScreenOffRealtime);
                    SimpleSettings.Misc.putBoolean(this.mContext, SimpleSettingKeys.KEY_REBOOT_POWER_DISCONNECTED, z2);
                }
            } catch (RemoteException e2) {
                Log.e(TAG, e2.getMessage());
            }
        }
    }

    private void simpleParseBatteryStatsDischargedLocked(IBatteryStats iBatteryStats) {
        this.mDischargeUnplugLevel3 = 0;
        this.mDischargeAmountScreenOn3 = 0;
        this.mDischargeAmountScreenOff3 = 0;
        this.mDischargeTimeScreenOn3 = 0L;
        this.mDischargeTimeScreenOff3 = 0L;
        this.mRebootCount3 = 0;
        this.mShortChargeCount3 = 0;
        try {
            int i2 = Integer.parseInt(CloudFunctionConfig.getFunctionParameters2(this.mContext, Constant.FUNC_SHUTDOWN_CHECK, String.valueOf(5)));
            if (DEBUG) {
                Log.d(TAG, "least charging time threshold: " + i2);
            }
        } catch (Exception e2) {
            Log.e(TAG, "get shut down threshold failed: ", e2);
        }
    }

    private int stateMap(boolean z2, boolean z3) {
        if (z2) {
            return z3 ? 6 : 7;
        }
        return z3 ? 4 : 5;
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) throws Throwable {
        int i2 = message.what;
        if (i2 == 0) {
            shutdown();
            return;
        }
        if (i2 == 1) {
            onCloudUpdated();
        } else if (i2 == 2) {
            onBootCompleted();
        } else {
            if (i2 != 3) {
                return;
            }
            onPowerDisconnected(false);
        }
    }

    void registerBroadcast() {
        if (DEBUG) {
            Log.d(TAG, "register shut down broadcast");
        }
        IntentFilter intentFilter = new IntentFilter("android.intent.action.ACTION_SHUTDOWN");
        intentFilter.addAction("android.intent.action.ACTION_POWER_DISCONNECTED");
        this.mContext.registerReceiver(this.mReceiver, intentFilter, 4);
    }

    public void setFastCheck(boolean z2) {
        this.mFastCheck = z2;
    }

    void shutdown() {
        IBatteryStats batteryStats;
        String str;
        long j2;
        long j3;
        if (!this.mEnabled) {
            return;
        }
        int batteryLevel = getBatteryLevel();
        if ((!this.mFastCheck && (batteryLevel == -1 || batteryLevel > 2)) || (batteryStats = BatteryStatsHelper.getBatteryStats()) == null) {
            return;
        }
        try {
            long jElapsedRealtime = SystemClock.elapsedRealtime() * 1000;
            int dischargeCurrentLevel = batteryStats.getDischargeCurrentLevel();
            int dischargeStartLevel = batteryStats.getDischargeStartLevel();
            int dischargeAmountScreenOn = batteryStats.getDischargeAmountScreenOn();
            int dischargeAmountScreenOff = batteryStats.getDischargeAmountScreenOff();
            long jComputeBatteryRealtime = batteryStats.computeBatteryRealtime(jElapsedRealtime, 0);
            int dischargeAmountScreenOnSinceCharge = batteryStats.getDischargeAmountScreenOnSinceCharge();
            int dischargeAmountScreenOffSinceCharge = batteryStats.getDischargeAmountScreenOffSinceCharge();
            long jComputeBatteryRealtime2 = batteryStats.computeBatteryRealtime(jElapsedRealtime, 0);
            long jComputeBatteryScreenOffRealtime = batteryStats.computeBatteryScreenOffRealtime(jElapsedRealtime, 0);
            long j4 = SimpleSettings.Misc.getLong(this.mContext, SimpleSettingKeys.KEY_LAST_BATTERY_SCREENOFF_REALTIME_C, 0L);
            boolean z2 = SimpleSettings.Misc.getBoolean(this.mContext, SimpleSettingKeys.KEY_REBOOT_POWER_DISCONNECTED, false);
            long j5 = jComputeBatteryScreenOffRealtime > j4 ? jComputeBatteryScreenOffRealtime - j4 : 0L;
            boolean z3 = DEBUG;
            if (z3) {
                Log.d(TAG, "dischargeCurrentLevel = " + dischargeCurrentLevel + " dischargeStartLevel = " + dischargeStartLevel + " reboot = " + z2);
            }
            if (z3) {
                Log.d(TAG, " dischargeAmountScreenOn = " + dischargeAmountScreenOn + " dischargeAmountScreenOff = " + dischargeAmountScreenOff + " batteryRealtime = " + jComputeBatteryRealtime + " batteryScreenOffRealtime = " + j5);
            }
            if (z3) {
                StringBuilder sb = new StringBuilder();
                sb.append("dischargeAmountScreenOnSinceCharge = ");
                sb.append(dischargeAmountScreenOnSinceCharge);
                sb.append(" dischargeAmountScreenOffSinceCharge = ");
                sb.append(dischargeAmountScreenOffSinceCharge);
                sb.append(" batteryRealtimeSinceCharge = ");
                j2 = jComputeBatteryRealtime2;
                sb.append(j2);
                sb.append(" batteryScreenOffRealtimeSinceCharge = ");
                j3 = jComputeBatteryScreenOffRealtime;
                sb.append(j3);
                Log.d(TAG, sb.toString());
            } else {
                j2 = jComputeBatteryRealtime2;
                j3 = jComputeBatteryScreenOffRealtime;
            }
            simpleParseBatteryStatsDischargedLocked(batteryStats);
            long j6 = jComputeBatteryRealtime / 1000;
            long j7 = j5 / 1000;
            long j8 = j2 / 1000;
            long j9 = j3 / 1000;
            int i2 = this.mDischargeUnplugLevel3;
            int i3 = this.mDischargeAmountScreenOn3;
            int i4 = this.mDischargeAmountScreenOff3;
            long j10 = this.mDischargeTimeScreenOn3;
            long j11 = this.mDischargeTimeScreenOff3;
            try {
                int i5 = this.mRebootCount3;
                int i6 = this.mShortChargeCount3;
                str = TAG;
                try {
                    if (FileUtil.writeToFile(this.mContext.getFilesDir().getAbsolutePath() + "/" + SimpleSettingKeys.SHUTDOWN_FILE_NAME, new Record(dischargeCurrentLevel, dischargeStartLevel, dischargeAmountScreenOn, dischargeAmountScreenOff, j6, j7, dischargeAmountScreenOnSinceCharge, dischargeAmountScreenOffSinceCharge, j8, j9, z2, i2, i3, i4, j10, j11, i5, i6).toJson().toString())) {
                        SimpleSettings.Misc.putBoolean(this.mContext, SimpleSettingKeys.KEY_SHUTDOWN_ANALYTICS, true);
                    }
                } catch (RemoteException e2) {
                    e = e2;
                    Log.e(str, e.getMessage());
                }
            } catch (RemoteException e3) {
                e = e3;
                str = TAG;
            }
        } catch (RemoteException e4) {
            e = e4;
            str = TAG;
        }
    }

    void unRegisterBroadcast() {
        if (DEBUG) {
            Log.d(TAG, "un-register shut down broadcast");
        }
        this.mContext.unregisterReceiver(this.mReceiver);
    }
}
