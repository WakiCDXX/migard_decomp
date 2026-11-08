package com.miui.powerkeeper.statemachine;

import android.annotation.NonNull;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.ReflectUtil;
import android.net.IMiuiNetworkManager;
import android.net.Uri;
import android.net.wifi.SoftApInfo;
import android.net.wifi.WifiClient;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.telephony.ServiceState;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.LocalLog;
import android.util.Log;
import android.util.Slog;
import com.miui.powerkeeper.PowerKeeperApplication;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.active.ActiveController;
import com.miui.powerkeeper.appcontrol.HideModeStateMachineConfig;
import com.miui.powerkeeper.cloudcontrol.CloudFunctionConfig;
import com.miui.powerkeeper.cloudcontrol.CloudUpdateHideMode;
import com.miui.powerkeeper.controller.AlarmController;
import com.miui.powerkeeper.customerpower.CustomerPowerManager;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.control.MessageType;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.powerchecker.BatteryStatsHelper;
import com.miui.powerkeeper.powerchecker.NightStandbyRecord;
import com.miui.powerkeeper.powerchecker.PowerCheckerCloudConfigure;
import com.miui.powerkeeper.provider.GlobalFeatureConfigure;
import com.miui.powerkeeper.provider.GlobalFeatureConfigureHelper;
import com.miui.powerkeeper.provider.SimpleSettingKeys;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.siming.dasi.MIY;
import com.miui.powerkeeper.utils.CommonAdapter;
import com.miui.powerkeeper.utils.Constant;
import com.miui.powerkeeper.utils.InternationalAdapter;
import com.miui.powerkeeper.utils.NetUtils;
import com.miui.powerkeeper.utils.PackageUtil;
import com.miui.powerkeeper.utils.PowerLog;
import com.miui.powerkeeper.utils.Utils;
import com.miui.powerkeeper.utils.WifiApAdapter;
import com.miui.whetstone.AlarmPolicy;
import com.miui.whetstone.WhetstoneActivityManager;
import com.xiaomi.mipush.sdk.Constants;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import miui.os.Build;
import miui.process.ProcessConfig;
import miui.process.ProcessManager;
import miui.process.RunningProcessInfo;
import miui.telephony.TelephonyManagerEx;
import miui.util.HardwareInfo;
import miui.yellowpage.YellowPageContract;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class PhoneSleepModeController extends SleepModeControllerNew {
    private static final String ACTION_BLUETOOTH_OPP_INBOUND_END = "com.android.bluetooth.opp.BLUETOOTH_OPP_INBOUND_END";
    private static final String ACTION_BLUETOOTH_OPP_INBOUND_START = "com.android.bluetooth.opp.BLUETOOTH_OPP_INBOUND_START";
    private static final String ACTION_BLUETOOTH_OPP_OUTBOUND_END = "com.android.bluetooth.opp.BLUETOOTH_OPP_OUTBOUND_END";
    private static final String ACTION_BLUETOOTH_OPP_OUTBOUND_START = "com.android.bluetooth.opp.BLUETOOTH_OPP_OUTBOUND_START";
    private static final String ACTION_CHECK_STATIONARY = "com.miui.powerkeeper.check_stationary";
    private static final String ACTION_ENTER_DAY = "com.miui.powerkeeper.enter_day";
    private static final String ACTION_ENTER_NIGHT = "com.miui.powerkeeper.enter_night";
    private static final String ACTION_SCREEN_OFF_TIMEOUT = "com.miui.powerkeeper.screen_off_timeout";
    private static final int[] CHECK_SCREEN_OFF_DURATION;
    private static final int CHECK_STATIONARY_DURATION;
    private static final boolean DBG_COMPRESS_TIME;
    private static final boolean DBG_SLEEP;
    private static final int DEFAULT_SLEEP_BEGIN_TIME = 23;
    private static final int DEFAULT_SLEEP_END_TIME = 7;
    private static final int DEFAULT_SLEEP_LIGHT_LUX_THRESHOLD = 5;
    private static final int DEFAULT_SLEEP_MAX_TIMES;
    private static final int DEFAULT_STATIONARY_TIME_THRESHOLD;
    public static final String FUCSWITCH = "fucSwitch";
    public static final String FUNC_SLEEP_REBOOT = "sleep_reboot";
    private static final int GPS_TIME_THRESHOLD = 5;
    private static String KEY_RESOURCE_STATE = null;
    private static String KEY_SLEEP_REBOOT_TIME = null;
    private static final int MSG_DESK_CLOCK_REQUEST_QUIT = 10;
    private static final int MSG_ENTER_DAY = 7;
    private static final int MSG_ENTER_NIGHT = 6;
    private static final int MSG_SCREEN_OFF_TIMEOUT = 8;
    private static final int MSG_SLEEP_CHECK = 9;
    private static final int MSG_SLEEP_CLOSE_GPS = 11;
    private static final int MSG_SLEEP_CLOSE_WIFIAP = 12;
    private static final int RANDOM_DURATION;
    private static final String SETTINGS_SLEEP_MODE_SWITCH_ATTR = "sec_pc_config_scenario_policies_open";
    private static final String SLEEP_MODE_OFF_BROADCAST = "com.miui.powerkeeper.SLEEP_MODE_OFF_REASON";
    public static final String SLEEP_MODE_PARAMS = "sleep_mode_cloud_params";
    public static final String SLEEP_MODE_PARAMS2 = "sleep_mode_cloud_params2";
    private static final String TAG = "power.sleep";
    public static final String USER_CLOSE_SLEEP_MODE_BY_BUTTON = "userCloseSleepModeByUIButton";
    private static int mNumConnectedDevices;
    private static String[] mRestrictPkgs;
    private static volatile PhoneSleepModeController sInstance;
    private boolean isReceiverEnterDay;
    private boolean isStopSatellite;
    private List<PowerKeeperInterface.a0> listeners;
    private BluetoothStartReceiver mBluetoothStartReceiver;
    private boolean mBugAppClearSwitch;
    private CheckSleepPower mCheckSleepPower;
    PendingIntent mCheckStationaryPi;
    private CheckStationaryReceiver mCheckStationaryReceiver;
    private ClockReceiver mClockReceiver;
    private Context mContext;
    private boolean mForceSleep;
    private String mGpsPkg;
    private int mGpsStartTime;
    private SleepHandler mHandler;
    private HotsSpotSoftApCallback mHotsSpotSoftApCallback;
    private Sensor mLightSensor;
    private LocalLog mLocalLog;
    private String mNetworkAllowApps;
    private RequestWakeReceiver mRequestWakeReceiver;
    PendingIntent mScreeenOffPi;
    private SensorListener mSensorEventListener;
    private TelephonyCallback mServiceStateListener;
    private SleepState mSleepState;
    private SleepSwitchObserver mSleepSwitchObserver;
    private Sensor mStepDetector;
    private WifiManager mWifiManager;
    private boolean oppState = false;
    private final int REASON_PRESS_POWER_KEY = 1;
    private final int REASON_CHARGING = 2;
    private final int REASON_SCREEN_ON = 4;
    private final int REASON_USER_PRESENT = 8;
    private final int REASON_ENTER_DAY = 16;
    private final int REASON_DESK_CLOCK_REQUEST = 32;
    private final int REASON_USER_SWITCH_OFF = 64;
    private final int REASON_CLOUD_SWITCH_OFF = 128;
    private final int REASON_LIGHT_LUX = 256;
    private final int REASON_BACKUP_RESTORE = 512;
    private final int REASON_STEP_NOT_STATIONARY = 1024;
    private final int REASON_GPS_NOT_STATIONARY = 2048;
    private final int REASON_WIFI_HOTSPOT_CONNECTED = 4096;
    private final int REASON_USER_SWITCH_ON = LogType.LOG_TYPE_SAMPLING_INTERRUPT;
    private final int REASON_CLOUD_SWITCH_ON = LogType.LOG_TYPE_SAMPLING_WIFI_DATA;
    private final int REASON_DISPOSE = LogType.LOG_TYPE_SAMPLING_MOBILE_DATA;
    private final int REASON_SCREEN_OFF = LogType.LOG_TYPE_SAMPLING_CLOCK;
    private final int REASON_ENTER_NIGHT = LogType.LOG_TYPE_SAMPLING_SIGNAL_STRENGTH;
    private final int REASON_UNCHARGING = LogType.LOG_TYPE_SAMPLING_NETWORK_MODE;
    private final int REASON_SCREEN_OFF_TIME_OUT = LogType.LOG_TYPE_SAMPLING_TRANSMIT_POWER;
    private final int REASON_CHECK_STATIONARY_TIME_OUT = 8388608;
    private final int REASON_POWERKEEPER_RESTART = 16777216;
    private final int REASON_EXCEED_MAX_TIMES = LogType.LOG_TYPE_SAMPLING_CPUFREQ;
    private final int REJECT_REASON_USER_CLOSE = 1;
    private final int REJECT_REASON_CLOUD_CLOSE = 2;
    private final int REJECT_REASON_FOREIGN_NETWORK = 4;
    private final int REJECT_REASON_ONE_FOREIGN_SIM = 8;
    private final int REJECT_REASON_TWO_FOREIGN_SIM = 16;
    private final int STATE_DEFAULT = 0;
    private final int STATE_IN_NIGHT = 1;
    private final int STATE_MONITORING = 2;
    private final int STATE_ASLEEP = 3;
    private final int STATE_AWAKE = 4;
    private final int WAKELOCK_DEFAULT_TIMEOUT = 200;
    public boolean sSleepModeForEarthquakeWarnFlag = true;
    public boolean mControlAlarmEw = true;

    /* JADX INFO: renamed from: com.miui.powerkeeper.statemachine.PhoneSleepModeController$2, reason: invalid class name */
    class AnonymousClass2 implements Runnable {
        final /* synthetic */ boolean val$previous;

        AnonymousClass2(boolean z2) {
            this.val$previous = z2;
        }

        @Override // java.lang.Runnable
        public void run() {
            boolean z2 = Settings.Global.getInt(PhoneSleepModeController.this.mContext.getContentResolver(), "wifi_scan_always_enabled", 0) == 1;
            PhoneSleepModeController.this.logWithsLocal("run setWlanScanEnable:" + this.val$previous + " now=" + z2);
            if (this.val$previous != z2) {
                Settings.Global.putInt(PhoneSleepModeController.this.mContext.getContentResolver(), "wifi_scan_always_enabled", this.val$previous ? 1 : 0);
            }
        }
    }

    private class BluetoothStartReceiver extends BroadcastReceiver {
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.bluetooth.adapter.action.STATE_CHANGED".equals(intent.getAction())) {
                int i2 = Calendar.getInstance().get(11);
                boolean zIsEnabled = ((BluetoothManager) PhoneSleepModeController.this.mContext.getSystemService("bluetooth")).getAdapter().isEnabled();
                if (PhoneSleepModeController.DBG_SLEEP) {
                    Log.d(PhoneSleepModeController.TAG, "bluetooth state is BluetoothAdapter.STATE_ON = " + zIsEnabled);
                }
                int i3 = Settings.Global.getInt(PhoneSleepModeController.this.mContext.getContentResolver(), "enable_bluetooth_restricte", 0);
                if ((i2 >= 23 || i2 < 5) && zIsEnabled && i3 == 1) {
                    PhoneSleepModeController phoneSleepModeController = PhoneSleepModeController.this;
                    phoneSleepModeController.sendRestrictStateChanged(phoneSleepModeController.mContext, 12, 10, true);
                    if (PhoneSleepModeController.DBG_SLEEP) {
                        Log.d(PhoneSleepModeController.TAG, "the bt state is halfOn and hour of day = " + i2);
                    }
                    PhoneSleepModeController.this.unregisterBluetoothStartReceiver();
                }
            }
        }

        private BluetoothStartReceiver() {
        }
    }

    private class BugAppsClearSwitchCloudObserver implements b.a.d {
        @Override // b.a.d
        public void onChanged(JSONObject jSONObject) {
            PhoneSleepModeController.this.mBugAppClearSwitch = false;
            if (jSONObject != null) {
                PhoneSleepModeController.this.mBugAppClearSwitch = jSONObject.optBoolean("bugAppsClearSwitch", false);
                PhoneSleepModeController.this.sSleepModeForEarthquakeWarnFlag = jSONObject.optBoolean("sleepModeforEW", true);
                PhoneSleepModeController.this.mControlAlarmEw = jSONObject.optBoolean("controlalarmEw", true);
                PhoneSleepModeController phoneSleepModeController = PhoneSleepModeController.this;
                phoneSleepModeController.mNetworkAllowApps = jSONObject.optString("networkAllowApps", SimpleSettings.Misc.getString(phoneSleepModeController.mContext, SimpleSettingKeys.KEY_SLEEP_MODE_NETWORK_WHITE_APPS, InternationalAdapter.sNetworkAllowApps));
                Log.d(PhoneSleepModeController.TAG, "Bug Apps Clear Switch CloudObserver changed : " + PhoneSleepModeController.this.mBugAppClearSwitch + ", sSleepModeForEarthquakeWarnFlag is " + PhoneSleepModeController.this.sSleepModeForEarthquakeWarnFlag + ", mNetworkAllowApps is " + PhoneSleepModeController.this.mNetworkAllowApps);
                int intForUser = Settings.Secure.getIntForUser(PhoneSleepModeController.this.mContext.getContentResolverForUser(UserHandle.OWNER), "key_open_earthquake_warning", 0, 0);
                PhoneSleepModeController.this.logWithsLocal("earthquakeWarningFlag is " + intForUser + ", sSleepModeForEarthquakeWarnFlag is " + PhoneSleepModeController.this.sSleepModeForEarthquakeWarnFlag + "network apps is " + PhoneSleepModeController.this.mNetworkAllowApps);
                if (!PhoneSleepModeController.this.sSleepModeForEarthquakeWarnFlag && intForUser == 1) {
                    SleepModeControllerNew.setSleepModeSwitch(false, 4);
                } else if (SleepModeControllerNew.sUserCloseSleepModeByUIButton) {
                    SleepModeControllerNew.setSleepModeSwitch(false, 4);
                } else {
                    SleepModeControllerNew.setSleepModeSwitch(true, 4);
                }
            }
        }

        private BugAppsClearSwitchCloudObserver() {
        }
    }

    private class CheckSleepPower extends BroadcastReceiver {
        private final String ACTION_SLEEP_CHECK_POWER;
        private final int DEFAULT_DELAY;
        private PendingIntent mCheckPowerPi;

        public CheckSleepPower() {
            this.DEFAULT_DELAY = PhoneSleepModeController.DBG_COMPRESS_TIME ? PowerCheckerCloudConfigure.DEFAULT_WAKEUP_ALARM_INTERVAL : CustomerPowerManager.SYSTEM_BAD_SIGNAL_TIME;
            this.ACTION_SLEEP_CHECK_POWER = "com.miui.powerkeeper.sleep_check_power";
            if (PhoneSleepModeController.DBG_SLEEP) {
                Slog.d(PhoneSleepModeController.TAG, "CheckSleepPower construct");
            }
            PhoneSleepModeController.this.mContext.registerReceiver(this, new IntentFilter("com.miui.powerkeeper.sleep_check_power"), null, PhoneSleepModeController.this.mHandler, 2);
        }

        public boolean cancelCheckPowerAlarm() {
            if (this.mCheckPowerPi == null) {
                return false;
            }
            if (PhoneSleepModeController.DBG_SLEEP) {
                Slog.d(PhoneSleepModeController.TAG, "cancelCheckPowerAlarm reason=");
            }
            ((AlarmManager) PhoneSleepModeController.this.mContext.getSystemService("alarm")).cancel(this.mCheckPowerPi);
            this.mCheckPowerPi = null;
            return true;
        }

        public void dispose() {
            if (PhoneSleepModeController.DBG_SLEEP) {
                Slog.d(PhoneSleepModeController.TAG, "CheckSleepPower dispose");
            }
            PhoneSleepModeController.this.mContext.unregisterReceiver(this);
            cancelCheckPowerAlarm();
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("com.miui.powerkeeper.sleep_check_power".equals(intent == null ? null : intent.getAction())) {
                if (PhoneSleepModeController.this.mSleepState.state[PhoneSleepModeController.this.mSleepState.index] != 3) {
                    PhoneSleepModeController.this.logWithsLocal("CheckSleepPower abandon");
                    return;
                }
                int i2 = Utils.toInt(CloudFunctionConfig.getFunctionParameters(PhoneSleepModeController.this.mContext, "sleep_reboot", ""), -1);
                if (!PhoneSleepModeController.DBG_COMPRESS_TIME && i2 <= 0) {
                    PhoneSleepModeController.this.logWithsLocal("CheckSleepPower invalid threshold");
                    return;
                }
                int curBatteryLevel = Utils.getCurBatteryLevel(PhoneSleepModeController.this.mContext);
                if (PhoneSleepModeController.this.mSleepState.sleepBatteryLevel - curBatteryLevel >= i2) {
                    SimpleSettings.Misc.putString(PhoneSleepModeController.this.mContext, PhoneSleepModeController.KEY_SLEEP_REBOOT_TIME, Utils.getCurrentFormatTime());
                    ((PowerManager) PhoneSleepModeController.this.mContext.getSystemService(NightStandbyRecord.KEY_POWER)).reboot("quiescent");
                    return;
                }
                PhoneSleepModeController.this.logWithsLocal("CheckSleepPower ignore threshold=" + i2 + " currentLevel=" + curBatteryLevel + " sleepLevel=" + PhoneSleepModeController.this.mSleepState.sleepBatteryLevel);
            }
        }

        public void resetCheckPowerAlarm() {
            AlarmManager alarmManager = (AlarmManager) PhoneSleepModeController.this.mContext.getSystemService("alarm");
            PendingIntent pendingIntent = this.mCheckPowerPi;
            if (pendingIntent == null) {
                this.mCheckPowerPi = PendingIntent.getBroadcast(PhoneSleepModeController.this.mContext, 0, new Intent("com.miui.powerkeeper.sleep_check_power"), 67108864);
                PhoneSleepModeController.this.logWithsLocal("resetCheckPowerAlarm");
            } else {
                alarmManager.cancel(pendingIntent);
                PhoneSleepModeController.this.logWithsLocal("resetCheckPowerAlarm with cancel");
            }
            alarmManager.set(3, SystemClock.elapsedRealtime() + ((long) this.DEFAULT_DELAY), this.mCheckPowerPi);
        }
    }

    private class CheckStationaryReceiver extends BroadcastReceiver {
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent == null ? null : intent.getAction();
            if (PhoneSleepModeController.ACTION_SCREEN_OFF_TIMEOUT.equals(action)) {
                PhoneSleepModeController.this.logWithsLocal("receive ACTION_SCREEN_OFF_TIMEOUT");
                PhoneSleepModeController.this.mHandler.sendEmptyMessage(8);
            } else if (PhoneSleepModeController.ACTION_CHECK_STATIONARY.equals(action)) {
                PhoneSleepModeController.this.logWithsLocal("receive ACTION_CHECK_STATIONARY");
                PhoneSleepModeController.this.mHandler.sendEmptyMessage(9);
            }
        }

        private CheckStationaryReceiver() {
        }
    }

    private class ClockReceiver extends BroadcastReceiver {
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent == null ? null : intent.getAction();
            if (PhoneSleepModeController.ACTION_ENTER_NIGHT.equals(action)) {
                PhoneSleepModeController.this.mHandler.sendMessageAtFrontOfQueue(PhoneSleepModeController.this.mHandler.obtainMessage(6, LogType.LOG_TYPE_SAMPLING_SIGNAL_STRENGTH, -1));
                return;
            }
            if (PhoneSleepModeController.ACTION_ENTER_DAY.equals(action)) {
                PhoneSleepModeController.this.logWithsLocal("receive ACTION_ENTER_DAY");
                PhoneSleepModeController.this.mHandler.sendMessageAtFrontOfQueue(PhoneSleepModeController.this.mHandler.obtainMessage(7));
            } else if (PhoneSleepModeController.ACTION_BLUETOOTH_OPP_INBOUND_START.equals(action) || PhoneSleepModeController.ACTION_BLUETOOTH_OPP_OUTBOUND_START.equals(action)) {
                PhoneSleepModeController.this.oppState = true;
            } else if (PhoneSleepModeController.ACTION_BLUETOOTH_OPP_INBOUND_END.equals(action) || PhoneSleepModeController.ACTION_BLUETOOTH_OPP_OUTBOUND_END.equals(action)) {
                PhoneSleepModeController.this.oppState = false;
            }
        }

        private ClockReceiver() {
        }
    }

    private class DumpTheReasonOfSleepModeOff extends BroadcastReceiver {
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (PhoneSleepModeController.SLEEP_MODE_OFF_BROADCAST.equals(intent.getAction())) {
                String stringExtra = intent.getStringExtra("offReason");
                PhoneSleepModeController.this.logWithsLocal("user start/stop sleepModeReason  -> " + stringExtra);
            }
        }

        private DumpTheReasonOfSleepModeOff() {
        }
    }

    private static class HotsSpotSoftApCallback implements WifiManager.SoftApCallback {
        private HotsSpotSoftApCallback() {
        }

        public void onConnectedClientsChanged(@NonNull SoftApInfo softApInfo, @NonNull List<WifiClient> list) {
            PhoneSleepModeController.mNumConnectedDevices = list.size();
        }
    }

    private class RequestWakeReceiver extends BroadcastReceiver {
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent != null && "com.miui.powerkeeper_request_wake".equals(intent.getAction())) {
                int intExtra = intent.getIntExtra("reason", 0);
                PhoneSleepModeController.this.logWithsLocal("receive ACTION_REQUEST_WAKE reason=" + intExtra);
                if (intExtra == 1) {
                    PhoneSleepModeController.this.mHandler.sendEmptyMessage(10);
                }
            }
        }

        private RequestWakeReceiver() {
        }
    }

    class SatelliteServiceStateListener extends TelephonyCallback implements TelephonyCallback.ServiceStateListener {
        SatelliteServiceStateListener() {
        }

        @Override // android.telephony.TelephonyCallback.ServiceStateListener
        public void onServiceStateChanged(@NonNull ServiceState serviceState) {
            try {
                TelephonyManagerEx telephonyManagerEx = TelephonyManagerEx.getDefault();
                TelephonyManager telephonyManager = (TelephonyManager) PhoneSleepModeController.this.mContext.getSystemService("phone");
                PhoneSleepModeController.this.logWithsLocal("satellite service state change -> " + serviceState.getState() + "  satellite isSatelliteEnabled  " + telephonyManagerEx.isSatelliteEnabled());
                if (1 == serviceState.getState() && telephonyManagerEx.isSatelliteEnabled()) {
                    telephonyManagerEx.setSatelliteEnableByUser(false, 1008);
                    PhoneSleepModeController.this.logWithsLocal("sleep mode stop the satellite communication，the satelliteEnabled is : " + telephonyManagerEx.isSatelliteEnabled());
                    if (telephonyManager == null || PhoneSleepModeController.this.mServiceStateListener == null) {
                        return;
                    }
                    telephonyManager.unregisterTelephonyCallback(PhoneSleepModeController.this.mServiceStateListener);
                    PhoneSleepModeController.this.mServiceStateListener = null;
                    PhoneSleepModeController.this.logWithsLocal("sleep mode unregisterTelephonyCallback!!!");
                }
            } catch (Exception e2) {
                throw new RuntimeException(e2);
            }
        }
    }

    private class SensorListener implements SensorEventListener {
        @Override // android.hardware.SensorEventListener
        public void onSensorChanged(SensorEvent sensorEvent) {
            int type = sensorEvent.sensor.getType();
            if (type == 18) {
                if (sensorEvent.values[0] == 1.0d) {
                    PhoneSleepModeController.this.mSleepState.stepStopTime = (int) (sensorEvent.timestamp / 1000000000);
                    if (PhoneSleepModeController.DBG_SLEEP) {
                        Slog.d(PhoneSleepModeController.TAG, "onSensorChanged");
                        return;
                    }
                    return;
                }
                return;
            }
            if (type == 5) {
                int i2 = PhoneSleepModeController.this.mSleepState.lightLux;
                PhoneSleepModeController.this.mSleepState.lightLux = (int) sensorEvent.values[0];
                if ((i2 <= PhoneSleepModeController.this.mSleepState.LIGHT_LUX_THRESHOLD || PhoneSleepModeController.this.mSleepState.lightLux > PhoneSleepModeController.this.mSleepState.LIGHT_LUX_THRESHOLD) && (i2 > PhoneSleepModeController.this.mSleepState.LIGHT_LUX_THRESHOLD || PhoneSleepModeController.this.mSleepState.lightLux <= PhoneSleepModeController.this.mSleepState.LIGHT_LUX_THRESHOLD)) {
                    if (PhoneSleepModeController.DBG_SLEEP) {
                        Slog.d(PhoneSleepModeController.TAG, "onSensorChanged TYPE_LIGHT ignore for shaking value0=" + sensorEvent.values[0]);
                        return;
                    }
                    return;
                }
                if (PhoneSleepModeController.DBG_SLEEP) {
                    Slog.d(PhoneSleepModeController.TAG, "onSensorChanged TYPE_LIGHT value0=" + sensorEvent.values[0]);
                }
                PhoneSleepModeController.this.mSleepState.lightTime = (int) (sensorEvent.timestamp / 1000000000);
            }
        }

        private SensorListener() {
        }

        @Override // android.hardware.SensorEventListener
        public void onAccuracyChanged(Sensor sensor, int i2) {
        }
    }

    private class SleepHandler extends Handler {
        public SleepHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i2;
            int i3 = message.what;
            int i4 = 2;
            if (i3 == -8) {
                PhoneSleepModeController.this.mSleepState.isCharging = ((Boolean) ((AsyncResult) message.obj).result).booleanValue();
                if (PhoneSleepModeController.this.mSleepState.isCharging) {
                    PhoneSleepModeController.this.mSleepState.chargingTime = (int) (SystemClock.elapsedRealtime() / 1000);
                    if (PhoneSleepModeController.this.mSleepState.state[PhoneSleepModeController.this.mSleepState.index] == 2 || PhoneSleepModeController.this.mSleepState.state[PhoneSleepModeController.this.mSleepState.index] == 3) {
                        PhoneSleepModeController.this.tryToQuitSleep(2);
                        return;
                    }
                    return;
                }
                if (PhoneSleepModeController.this.mSleepState.chargingTime != 0) {
                    PhoneSleepModeController.this.mSleepState.chargedTotalDuration += ((int) (SystemClock.elapsedRealtime() / 1000)) - PhoneSleepModeController.this.mSleepState.chargingTime;
                    PhoneSleepModeController.this.mSleepState.chargingTime = 0;
                }
                if (PhoneSleepModeController.this.mSleepState.state[PhoneSleepModeController.this.mSleepState.index] == 2) {
                    PhoneSleepModeController.this.resetScreenOffAlarm(LogType.LOG_TYPE_SAMPLING_NETWORK_MODE, PhoneSleepModeController.CHECK_SCREEN_OFF_DURATION[PhoneSleepModeController.this.getIndex()]);
                    return;
                }
                return;
            }
            if (i3 == -7) {
                PhoneSleepModeController.this.mSleepState.isScreenOn = true;
                PhoneSleepModeController.this.tryToQuitSleep(8);
                return;
            }
            if (i3 == -6) {
                if (PhoneSleepModeController.this.mSleepState.state[PhoneSleepModeController.this.mSleepState.index] == 3) {
                    PhoneSleepModeController.this.tryToQuitSleep(1);
                    return;
                } else {
                    if (PhoneSleepModeController.DBG_SLEEP) {
                        Slog.d(PhoneSleepModeController.TAG, "Ignore power key event for not in sleep");
                        return;
                    }
                    return;
                }
            }
            if (i3 == -5) {
                PhoneSleepModeController.this.mSleepState.isScreenOn = false;
                if (PhoneSleepModeController.this.mSleepState.isCharging) {
                    return;
                }
                PhoneSleepModeController.this.resetScreenOffAlarm(LogType.LOG_TYPE_SAMPLING_CLOCK, PhoneSleepModeController.CHECK_SCREEN_OFF_DURATION[PhoneSleepModeController.this.getIndex()]);
                return;
            }
            if (i3 == -4) {
                TelephonyManager telephonyManager = (TelephonyManager) PhoneSleepModeController.this.mContext.getSystemService("phone");
                if (telephonyManager != null && PhoneSleepModeController.this.mServiceStateListener != null) {
                    telephonyManager.unregisterTelephonyCallback(PhoneSleepModeController.this.mServiceStateListener);
                    PhoneSleepModeController.this.mServiceStateListener = null;
                    PhoneSleepModeController.this.logWithsLocal("unregisterTelephonyCallback for screen on !!!");
                }
                if (PhoneSleepModeController.this.mWifiManager != null && PhoneSleepModeController.this.mHotsSpotSoftApCallback != null) {
                    PhoneSleepModeController.this.mWifiManager.unregisterSoftApCallback(PhoneSleepModeController.this.mHotsSpotSoftApCallback);
                    PhoneSleepModeController.this.mWifiManager = null;
                    PhoneSleepModeController.this.mHotsSpotSoftApCallback = null;
                }
                PhoneSleepModeController.this.mSleepState.isScreenOn = true;
                PhoneSleepModeController.this.tryToQuitSleep(4);
                return;
            }
            switch (i3) {
                case 6:
                    PhoneSleepModeController.this.isStopSatellite = false;
                    PhoneSleepModeController.this.isReceiverEnterDay = false;
                    if (PhoneSleepModeController.DBG_SLEEP) {
                        Log.d(PhoneSleepModeController.TAG, "MSG_ENTER_NIGHT -> isReceiverEnterDay : " + PhoneSleepModeController.this.isReceiverEnterDay);
                    }
                    PhoneSleepModeController.this.mSleepState.resetThreshold();
                    if (Build.IS_INTERNATIONAL_BUILD) {
                        if (!SystemProperties.get("ro.product.mod_device", "").contains("_in_")) {
                            PhoneSleepModeController.this.logWithsLocal("quit for region by intertional devices");
                            return;
                        } else if (!PhoneSleepModeController.this.isCheckRegion("IN")) {
                            PhoneSleepModeController.this.logWithsLocal("quit for region");
                            return;
                        }
                    }
                    if (!PhoneSleepModeController.this.hasTimeToSleep()) {
                        PhoneSleepModeController.this.startNightClockTimer();
                        return;
                    }
                    PhoneSleepModeController.this.mSleepState.state[PhoneSleepModeController.this.mSleepState.index] = 1;
                    PhoneSleepModeController.this.mSleepState.nightTime = (int) (SystemClock.elapsedRealtime() / 1000);
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(13, 0);
                    calendar.set(12, 0);
                    calendar.set(11, 0);
                    PhoneSleepModeController.this.mSleepState.nightTimeOffset = (int) ((System.currentTimeMillis() - calendar.getTimeInMillis()) / 1000);
                    PhoneSleepModeController.this.mSleepState.nightPowerConsumed = Utils.getScreenOffPowerConsumed();
                    PhoneSleepModeController.this.checkChineseUser();
                    PhoneSleepModeController.this.startListenChargingState();
                    PhoneSleepModeController.this.registerSleepSwitchObserver();
                    PhoneSleepModeController.this.startListenDeviceStateIfNeeded(message.arg1);
                    PhoneSleepModeController.this.startDayClockTimer();
                    if (SimpleSettings.Misc.getBoolean(PhoneSleepModeController.this.mContext, "sleep_reboot", false)) {
                        PhoneSleepModeController.this.mCheckSleepPower = PhoneSleepModeController.this.new CheckSleepPower();
                        return;
                    }
                    return;
                case 7:
                    synchronized (PhoneSleepModeController.class) {
                        try {
                            TelephonyManager telephonyManager2 = (TelephonyManager) PhoneSleepModeController.this.mContext.getSystemService("phone");
                            if (telephonyManager2 != null && PhoneSleepModeController.this.mServiceStateListener != null) {
                                telephonyManager2.unregisterTelephonyCallback(PhoneSleepModeController.this.mServiceStateListener);
                                PhoneSleepModeController.this.mServiceStateListener = null;
                                PhoneSleepModeController.this.logWithsLocal("unregisterTelephonyCallback for enter day !!!");
                            }
                            PhoneSleepModeController.this.isStopSatellite = false;
                            PhoneSleepModeController.this.isReceiverEnterDay = true;
                            if (PhoneSleepModeController.DBG_SLEEP) {
                                Log.d(PhoneSleepModeController.TAG, "MSG_ENTER_DAY -> isReceiverEnterDay : " + PhoneSleepModeController.this.isReceiverEnterDay);
                            }
                            PhoneSleepModeController.this.mSleepState.dayTime = (int) (SystemClock.elapsedRealtime() / 1000);
                            if (PhoneSleepModeController.this.mSleepState.chargingTime != 0) {
                                PhoneSleepModeController.this.mSleepState.chargedTotalDuration += PhoneSleepModeController.this.mSleepState.dayTime - PhoneSleepModeController.this.mSleepState.chargingTime;
                                PhoneSleepModeController.this.mSleepState.chargingTime = 0;
                            }
                            PhoneSleepModeController.this.mSleepState.dayPowerConsumed = Utils.getScreenOffPowerConsumed();
                            i2 = PhoneSleepModeController.this.mSleepState.state[PhoneSleepModeController.this.mSleepState.index] == 3 ? 1 : 0;
                            if (PhoneSleepModeController.this.mSleepState.state[PhoneSleepModeController.this.mSleepState.index] == 3) {
                                PhoneSleepModeController.this.mSleepState.state[PhoneSleepModeController.this.mSleepState.index] = 4;
                                if (PhoneSleepModeController.this.mSleepState.sleepBatteryLevel > 0) {
                                    PhoneSleepModeController.this.mSleepState.sleepTotalConsumeBattery += PhoneSleepModeController.this.mSleepState.sleepBatteryLevel - Utils.getCurBatteryLevel(PhoneSleepModeController.this.mContext);
                                }
                                PhoneSleepModeController.this.mSleepState.wakeTime[PhoneSleepModeController.this.mSleepState.index] = PhoneSleepModeController.this.mSleepState.dayTime;
                            }
                            PhoneSleepModeController.this.logWithsLocal("ACTION_ENTER_DAY mSleepState=" + PhoneSleepModeController.this.mSleepState);
                            if (PhoneSleepModeController.this.mSleepState.state[PhoneSleepModeController.this.mSleepState.index] != 0) {
                                PhoneSleepModeController phoneSleepModeController = PhoneSleepModeController.this;
                                phoneSleepModeController.uploadSleepStatistic(phoneSleepModeController.mSleepState);
                            }
                            if (i2 != 0) {
                                PhoneSleepModeController phoneSleepModeController2 = PhoneSleepModeController.this;
                                phoneSleepModeController2.restoreSleepConfig(phoneSleepModeController2.mSleepState);
                            }
                            PhoneSleepModeController phoneSleepModeController3 = PhoneSleepModeController.this;
                            phoneSleepModeController3.writeToDb(phoneSleepModeController3.mSleepState);
                            PhoneSleepModeController.this.unregisterAll(16);
                            if (PhoneSleepModeController.this.mCheckSleepPower != null) {
                                PhoneSleepModeController.this.mCheckSleepPower.dispose();
                                PhoneSleepModeController.this.mCheckSleepPower = null;
                            }
                            PhoneSleepModeController.this.startNightClockTimer();
                        } catch (Throwable th) {
                            throw th;
                        }
                        break;
                    }
                    return;
                case 8:
                    PhoneSleepModeController phoneSleepModeController4 = PhoneSleepModeController.this;
                    phoneSleepModeController4.mWifiManager = (WifiManager) phoneSleepModeController4.mContext.getSystemService("wifi");
                    PhoneSleepModeController.this.mHotsSpotSoftApCallback = new HotsSpotSoftApCallback();
                    PhoneSleepModeController.this.mWifiManager.registerSoftApCallback(new HandlerExecutor(new Handler(Looper.myLooper())), PhoneSleepModeController.this.mHotsSpotSoftApCallback);
                    i4 = PhoneSleepModeController.this.mSleepState.isCharging ? 2 : PhoneSleepModeController.this.mSleepState.isScreenOn ? 4 : 0;
                    PhoneSleepModeController.this.logWithsLocal("onReceive ACTION_SCREEN_OFF_TIMEOUT reason=" + PhoneSleepModeController.this.reasonToString(i4));
                    if (i4 == 0) {
                        PhoneSleepModeController.this.registerStepDetectorSensor();
                        WhetstoneActivityManager.sleepModeRegisterGpsCallback();
                        PhoneSleepModeController.this.resetCheckStationaryAlarm(LogType.LOG_TYPE_SAMPLING_TRANSMIT_POWER);
                        return;
                    } else {
                        PhoneSleepModeController.this.mSleepState.failedReason[PhoneSleepModeController.this.mSleepState.index] = i4;
                        PhoneSleepModeController phoneSleepModeController5 = PhoneSleepModeController.this;
                        phoneSleepModeController5.notifySleepState(1, phoneSleepModeController5.reasonToString(i4));
                        return;
                    }
                case 9:
                    synchronized (PhoneSleepModeController.class) {
                        try {
                            if (!PhoneSleepModeController.this.isStopSatellite) {
                                PhoneSleepModeController.this.stopSatellite();
                                PhoneSleepModeController.this.isStopSatellite = true;
                                if (PhoneSleepModeController.DBG_SLEEP) {
                                    Log.d(PhoneSleepModeController.TAG, "stop has benn executed so isStopSatellite set true");
                                }
                            }
                            Log.d("SleepModeController", "MSG_SLEEP_CHECK in");
                            if (PhoneSleepModeController.this.mSleepState.state[PhoneSleepModeController.this.mSleepState.index] == 3) {
                                PhoneSleepModeController.this.logWithsLocal("ERROR already in asleep");
                                Log.d("SleepModeController", "STATE_ASLEEP in");
                                return;
                            }
                            int iElapsedRealtime = (int) (SystemClock.elapsedRealtime() / 1000);
                            boolean z2 = iElapsedRealtime - PhoneSleepModeController.this.mSleepState.stepStopTime > PhoneSleepModeController.this.mSleepState.STATIONARY_TIME_THRESHOLD;
                            PhoneSleepModeController.this.mGpsPkg = WhetstoneActivityManager.getGpsPkg();
                            PhoneSleepModeController.this.updateSleepStateGpsStopTime(WhetstoneActivityManager.getSleepStateGpsStopTime());
                            PhoneSleepModeController.this.logWithsLocal("gps is running mGpsPkg =" + PhoneSleepModeController.this.mGpsPkg + " gpsStopTime=" + PhoneSleepModeController.this.mSleepState.gpsStopTime + " interval=" + (iElapsedRealtime - PhoneSleepModeController.this.mSleepState.gpsStopTime));
                            boolean z3 = PhoneSleepModeController.this.mGpsPkg == null && iElapsedRealtime - PhoneSleepModeController.this.mSleepState.gpsStopTime > PhoneSleepModeController.this.mSleepState.STATIONARY_TIME_THRESHOLD;
                            int i5 = iElapsedRealtime - PhoneSleepModeController.this.mSleepState.lightTime;
                            boolean z4 = PhoneSleepModeController.this.mSleepState.lightLux <= PhoneSleepModeController.this.mSleepState.LIGHT_LUX_THRESHOLD && i5 > PhoneSleepModeController.this.mSleepState.STATIONARY_TIME_THRESHOLD;
                            PhoneSleepModeController.this.logWithsLocal("currentLight =" + PhoneSleepModeController.this.mSleepState.lightLux + " lightCurrentStateDuration=" + i5);
                            if (PhoneSleepModeController.DBG_SLEEP) {
                                Log.d(PhoneSleepModeController.TAG, "mNumConnectedDevices -> " + PhoneSleepModeController.mNumConnectedDevices);
                            }
                            boolean z5 = PhoneSleepModeController.mNumConnectedDevices > 0;
                            if (!PhoneSleepModeController.this.mSleepState.isCharging) {
                                i2 = z2 ? 0 : 1024;
                                if (!z3) {
                                    i2 |= 2048;
                                }
                                if (z5) {
                                    i2 |= 4096;
                                }
                                i4 = PhoneSleepModeController.this.mSleepState.isScreenOn ? i2 | 4 : i2;
                                if (!z4) {
                                    i4 |= 256;
                                }
                            }
                            Log.d(PhoneSleepModeController.TAG, "sleep check reason=" + PhoneSleepModeController.this.reasonToString(i4));
                            if (i4 == 0 || PhoneSleepModeController.this.mForceSleep) {
                                if (i4 != 0) {
                                    Log.d(PhoneSleepModeController.TAG, "Try to sleep for FORCE");
                                }
                                if (PhoneSleepModeController.this.isReceiverEnterDay) {
                                    if (PhoneSleepModeController.DBG_SLEEP) {
                                        Log.d(PhoneSleepModeController.TAG, "it already has enter Day so that it need not applySleepConfig()");
                                    }
                                    return;
                                }
                                if (PhoneSleepModeController.this.isBackupAndRestore()) {
                                    PhoneSleepModeController.this.mSleepState.failedReason[PhoneSleepModeController.this.mSleepState.index] = 512;
                                    PhoneSleepModeController.this.logWithsLocal("failedReason=" + PhoneSleepModeController.this.reasonToString(512));
                                    PhoneSleepModeController.this.unregisterAll(512);
                                    PhoneSleepModeController phoneSleepModeController6 = PhoneSleepModeController.this;
                                    phoneSleepModeController6.notifySleepState(1, phoneSleepModeController6.reasonToString(512));
                                    return;
                                }
                                PhoneSleepModeController.this.mSleepState.sleepTime[PhoneSleepModeController.this.mSleepState.index] = (int) (SystemClock.elapsedRealtime() / 1000);
                                PhoneSleepModeController.this.mSleepState.state[PhoneSleepModeController.this.mSleepState.index] = 3;
                                PhoneSleepModeController.this.mSleepState.sleepBatteryLevel = Utils.getCurBatteryLevel(PhoneSleepModeController.this.mContext);
                                PhoneSleepModeController phoneSleepModeController7 = PhoneSleepModeController.this;
                                phoneSleepModeController7.applySleepConfig(phoneSleepModeController7.mSleepState);
                                if (PhoneSleepModeController.this.mWifiManager != null && PhoneSleepModeController.this.mHotsSpotSoftApCallback != null) {
                                    PhoneSleepModeController.this.mWifiManager.unregisterSoftApCallback(PhoneSleepModeController.this.mHotsSpotSoftApCallback);
                                    PhoneSleepModeController.this.mWifiManager = null;
                                    PhoneSleepModeController.this.mHotsSpotSoftApCallback = null;
                                }
                                PhoneSleepModeController.this.cancelScreenOffAlarm(8388608);
                                PhoneSleepModeController.this.cancelCheckStationaryAlarm(8388608);
                                PhoneSleepModeController.this.unregisterStepDetectorSensor();
                                WhetstoneActivityManager.sleepModeUnregisterGpsCallback();
                                if (PhoneSleepModeController.this.mCheckSleepPower != null) {
                                    PhoneSleepModeController.this.mCheckSleepPower.resetCheckPowerAlarm();
                                }
                            } else {
                                PhoneSleepModeController.this.logWithsLocal("failedReason=" + PhoneSleepModeController.this.reasonToString(i4) + ((i4 | 256) != 0 ? " light=" + PhoneSleepModeController.this.mSleepState.lightLux + " lightCurrentStateDuration=" + i5 : ""));
                                PhoneSleepModeController.this.mSleepState.failedReason[PhoneSleepModeController.this.mSleepState.index] = i4;
                                PhoneSleepModeController.this.resetCheckStationaryAlarm(i4);
                                PhoneSleepModeController phoneSleepModeController8 = PhoneSleepModeController.this;
                                phoneSleepModeController8.notifySleepState(1, phoneSleepModeController8.reasonToString(i4));
                            }
                            return;
                        } catch (Throwable th2) {
                            throw th2;
                        }
                    }
                case 10:
                    PhoneSleepModeController.this.tryToQuitSleep(32);
                    return;
                case 11:
                    PowerManager.WakeLock wakeLockNewWakeLock = ((PowerManager) PhoneSleepModeController.this.mContext.getSystemService(NightStandbyRecord.KEY_POWER)).newWakeLock(1, "phoneSleepMode:sleep_gps");
                    wakeLockNewWakeLock.acquire(200L);
                    if (PhoneSleepModeController.this.closeGps()) {
                        sendEmptyMessageDelayed(12, 2000L);
                    } else {
                        PhoneSleepModeController.this.closeWifiAp();
                    }
                    if (wakeLockNewWakeLock.isHeld()) {
                        wakeLockNewWakeLock.release();
                        return;
                    }
                    return;
                case 12:
                    PowerManager.WakeLock wakeLockNewWakeLock2 = ((PowerManager) PhoneSleepModeController.this.mContext.getSystemService(NightStandbyRecord.KEY_POWER)).newWakeLock(1, "phoneSleepMode:sleep_wifiap");
                    wakeLockNewWakeLock2.acquire(200L);
                    PhoneSleepModeController.this.closeWifiAp();
                    if (wakeLockNewWakeLock2.isHeld()) {
                        wakeLockNewWakeLock2.release();
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }

    class SleepModeCloudParamsObserver implements b.a.d {
        SleepModeCloudParamsObserver() {
        }

        @Override // b.a.d
        public void onChanged(JSONObject jSONObject) {
            if (jSONObject != null) {
                PhoneSleepModeController.this.putCloudDataToDB(jSONObject);
            }
        }
    }

    private class SleepModeSwitchObserver extends ContentObserver {
        public SleepModeSwitchObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z2, Uri uri) {
            String lastPathSegment = uri.getLastPathSegment();
            ContentResolver contentResolverForUser = PhoneSleepModeController.this.mContext.getContentResolverForUser(UserHandle.SYSTEM);
            if (PhoneSleepModeController.SETTINGS_SLEEP_MODE_SWITCH_ATTR.equals(lastPathSegment)) {
                int intForUser = Settings.Secure.getIntForUser(contentResolverForUser, PhoneSleepModeController.SETTINGS_SLEEP_MODE_SWITCH_ATTR, 1, 0);
                if (intForUser == 1) {
                    SleepModeControllerNew.setSleepModeSwitch(true, 1);
                } else if (intForUser == 0) {
                    SleepModeControllerNew.setSleepModeSwitch(false, 1);
                } else {
                    Log.e(PhoneSleepModeController.TAG, "error, parameter type don't match");
                }
            }
        }
    }

    private class SleepState {
        int BEGIN_TIME;
        int END_TIME;
        final int INDEX_BLUETOOTH;
        final int INDEX_DATA;
        final int INDEX_FINGER;
        final int INDEX_GPS;
        final int INDEX_LIGHT;
        final int INDEX_NETWORK;
        final int INDEX_NOTIFICATION;
        final int INDEX_PICKUP_WAKE;
        final int INDEX_WIFI;
        final int INDEX_WIFI_AP;
        final int INDEX_WIFI_SCAN;
        int LIGHT_LUX_THRESHOLD;
        int MAX_SLEEP_TIMES;
        int STATIONARY_TIME_THRESHOLD;
        int chargedTotalDuration;
        int chargingTime;
        int dayPowerConsumed;
        int dayTime;
        int[] failedReason;
        int foreignNet;
        int foreignSim;
        int gpsStopTime;
        int index;
        boolean isCharging;
        boolean isCloudOff;
        boolean isScreenOn;
        boolean isUserOff;
        int lightLux;
        int lightTime;
        int monitorTime;
        int nightPowerConsumed;
        int nightTime;
        int nightTimeOffset;
        String oldFeatureStatus;
        int previous;
        int previousNotification;
        int[] quitReason;
        int restore;
        int restoreWarning;
        int sleepBatteryLevel;
        int[] sleepTime;
        int sleepTotalConsumeBattery;
        int[] state;
        int stepStopTime;
        int[] wakeTime;

        /* JADX INFO: Access modifiers changed from: private */
        public String nightToString() {
            StringBuilder sb = new StringBuilder(256);
            sb.append('{');
            sb.append("index=");
            sb.append(this.index);
            sb.append(" state=");
            sb.append(PhoneSleepModeController.this.stateToString(this.state[this.index]));
            sb.append(" isCharging=");
            sb.append(this.isCharging);
            sb.append(" isUserOff=");
            sb.append(this.isUserOff);
            sb.append(" isCloudOff=");
            sb.append(this.isCloudOff);
            sb.append(" foreignSim=");
            sb.append(this.foreignSim);
            sb.append(" foreignNet=");
            sb.append(this.foreignNet);
            sb.append(" nightPowerConsumed=");
            sb.append(this.nightPowerConsumed);
            sb.append('}');
            return sb.toString();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public SleepState reset() {
            this.previous = 0;
            this.restore = 0;
            this.previousNotification = 0;
            this.restoreWarning = 0;
            this.isUserOff = false;
            this.isCloudOff = false;
            this.foreignSim = 0;
            this.foreignNet = 0;
            this.nightTimeOffset = 0;
            this.nightTime = 0;
            this.monitorTime = 0;
            this.dayTime = 0;
            this.stepStopTime = 0;
            this.gpsStopTime = 0;
            this.lightLux = -1;
            this.lightTime = 0;
            this.isScreenOn = false;
            this.isCharging = false;
            this.chargingTime = 0;
            this.chargedTotalDuration = 0;
            this.nightPowerConsumed = -1;
            this.dayPowerConsumed = -1;
            this.sleepBatteryLevel = 0;
            this.sleepTotalConsumeBattery = 0;
            this.index = 0;
            for (int i2 = 0; i2 < this.MAX_SLEEP_TIMES; i2++) {
                this.state[i2] = 0;
                this.sleepTime[i2] = 0;
                this.wakeTime[i2] = 0;
                this.failedReason[i2] = 16;
                this.quitReason[i2] = 16;
            }
            return this;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void resetThreshold() {
            String separatedStringAt;
            String separatedStringAt2;
            String separatedStringAt3;
            String separatedStringAt4;
            int i2;
            int i3 = PhoneSleepModeController.DEFAULT_SLEEP_MAX_TIMES;
            this.STATIONARY_TIME_THRESHOLD = PhoneSleepModeController.DEFAULT_STATIONARY_TIME_THRESHOLD;
            this.LIGHT_LUX_THRESHOLD = 5;
            this.BEGIN_TIME = 23;
            this.END_TIME = 7;
            String str = null;
            String functionParameters2 = CloudFunctionConfig.getFunctionParameters2(PhoneSleepModeController.this.mContext, Constant.FUNC_SLEEP_MODE_CLOUD, null);
            if (functionParameters2 == null || functionParameters2.isEmpty()) {
                separatedStringAt = null;
                separatedStringAt2 = null;
                separatedStringAt3 = null;
                separatedStringAt4 = null;
            } else {
                String separatedStringAt5 = Utils.getSeparatedStringAt(functionParameters2, ',', 0);
                separatedStringAt2 = Utils.getSeparatedStringAt(functionParameters2, ',', 1);
                separatedStringAt3 = Utils.getSeparatedStringAt(functionParameters2, ',', 2);
                separatedStringAt4 = Utils.getSeparatedStringAt(functionParameters2, ',', 3);
                separatedStringAt = Utils.getSeparatedStringAt(functionParameters2, ',', 4);
                str = separatedStringAt5;
            }
            if (str != null && !str.isEmpty()) {
                try {
                    i3 = Integer.parseInt(str, 10);
                } catch (NumberFormatException e2) {
                    if (PhoneSleepModeController.DBG_SLEEP) {
                        Slog.d(PhoneSleepModeController.TAG, "resetThreshold1 " + e2);
                    }
                }
            }
            if (separatedStringAt2 != null && !separatedStringAt2.isEmpty()) {
                try {
                    this.STATIONARY_TIME_THRESHOLD = Integer.parseInt(separatedStringAt2, 10);
                } catch (NumberFormatException e3) {
                    if (PhoneSleepModeController.DBG_SLEEP) {
                        Slog.d(PhoneSleepModeController.TAG, "resetThreshold2 " + e3);
                    }
                }
            }
            if (separatedStringAt3 != null && !separatedStringAt3.isEmpty()) {
                try {
                    this.LIGHT_LUX_THRESHOLD = Integer.parseInt(separatedStringAt3, 10);
                } catch (NumberFormatException e4) {
                    if (PhoneSleepModeController.DBG_SLEEP) {
                        Slog.d(PhoneSleepModeController.TAG, "resetThreshold3 " + e4);
                    }
                }
            }
            if (separatedStringAt4 != null && !separatedStringAt4.isEmpty() && separatedStringAt != null && !separatedStringAt.isEmpty()) {
                try {
                    this.BEGIN_TIME = Integer.parseInt(separatedStringAt4, 10);
                    this.END_TIME = Integer.parseInt(separatedStringAt, 10);
                } catch (NumberFormatException e5) {
                    if (PhoneSleepModeController.DBG_SLEEP) {
                        Slog.d(PhoneSleepModeController.TAG, "resetThreshold4 " + e5);
                    }
                }
            }
            if (PhoneSleepModeController.DBG_COMPRESS_TIME || i3 <= 0 || i3 > 10) {
                i3 = PhoneSleepModeController.DEFAULT_SLEEP_MAX_TIMES;
            }
            if (PhoneSleepModeController.DBG_COMPRESS_TIME || (i2 = this.STATIONARY_TIME_THRESHOLD) <= 0 || i2 > 3600) {
                this.STATIONARY_TIME_THRESHOLD = PhoneSleepModeController.DEFAULT_STATIONARY_TIME_THRESHOLD;
            }
            int i4 = this.LIGHT_LUX_THRESHOLD;
            if (i4 <= 0 || i4 > 400) {
                this.LIGHT_LUX_THRESHOLD = 5;
            }
            int i5 = this.BEGIN_TIME;
            if (i5 < 21 && i5 >= 9) {
                this.BEGIN_TIME = 23;
            }
            if (this.END_TIME > 9) {
                this.END_TIME = 7;
            }
            if (PhoneSleepModeController.DBG_SLEEP) {
                Slog.d(PhoneSleepModeController.TAG, "resetThreshold times=" + this.MAX_SLEEP_TIMES + " stationary=" + this.STATIONARY_TIME_THRESHOLD + " light=" + this.LIGHT_LUX_THRESHOLD + " begin=" + this.BEGIN_TIME + " end=" + this.END_TIME + " newMaxSleepTimes=" + i3);
            }
            if (this.MAX_SLEEP_TIMES != i3) {
                this.MAX_SLEEP_TIMES = i3;
                this.state = new int[i3];
                this.sleepTime = new int[i3];
                this.wakeTime = new int[i3];
                this.failedReason = new int[i3];
                this.quitReason = new int[i3];
            }
            reset();
        }

        int getNightScreenOffConsumeBattery() {
            int i2;
            int i3 = this.dayPowerConsumed;
            if (i3 < 0 || (i2 = this.nightPowerConsumed) < 0) {
                return -1;
            }
            return i3 - i2;
        }

        boolean getPreviousEnable(int i2) {
            return (this.previous & i2) == i2;
        }

        int getSleepTime(int i2) {
            int i3;
            int i4 = this.wakeTime[i2];
            if (i4 <= 0 || (i3 = this.sleepTime[i2]) <= 0) {
                return -1;
            }
            return i4 - i3;
        }

        boolean needRestore(int i2) {
            return (this.restore & i2) == i2;
        }

        void setPreviousEnable(int i2, boolean z2) {
            if (z2) {
                this.previous |= i2;
                this.restore = i2 | this.restore;
            } else {
                int i3 = this.previous;
                int i4 = ~i2;
                this.previous = i3 & i4;
                this.restore = i4 & this.restore;
            }
        }

        void setRestore(int i2, boolean z2) {
            if (z2) {
                this.restore = i2 | this.restore;
            } else {
                this.restore = (~i2) & this.restore;
            }
        }

        void setRestoreWarning(int i2) {
            this.restoreWarning = i2 | this.restoreWarning;
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r1v24 */
        /* JADX WARN: Type inference failed for: r1v25 */
        /* JADX WARN: Type inference failed for: r1v26 */
        /* JADX WARN: Type inference failed for: r1v27 */
        /* JADX WARN: Type inference failed for: r1v28 */
        /* JADX WARN: Type inference failed for: r1v5 */
        /* JADX WARN: Type inference failed for: r1v7 */
        JSONObject toJson() {
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("version", 2);
                String string = "";
                int sleepTime = 0;
                int i2 = 0;
                for (int i3 = 0; i3 < this.MAX_SLEEP_TIMES; i3++) {
                    if (i3 > 0) {
                        string = Integer.toString(i3);
                    }
                    jSONObject.put(YellowPageContract.Permission.STATE + string, this.state[i3]);
                    jSONObject.put("failedReason" + string, this.failedReason[i3]);
                    jSONObject.put("quitReason" + string, this.quitReason[i3]);
                    int i4 = this.sleepTime[i3];
                    int i5 = this.nightTime;
                    if (i4 <= i5 || i5 <= 0) {
                        jSONObject.put("sleepClock" + string, -1);
                    } else {
                        int i6 = (i4 - i5) + this.nightTimeOffset;
                        if (i6 >= 86400) {
                            i6 -= 86400;
                        }
                        jSONObject.put("sleepClock" + string, i6 / 60);
                    }
                    int i7 = this.wakeTime[i3];
                    int i8 = this.nightTime;
                    if (i7 <= i8 || i8 <= 0) {
                        jSONObject.put("wakeClock" + string, -1);
                    } else {
                        int i9 = (i7 - i8) + this.nightTimeOffset;
                        if (i9 >= 86400) {
                            i9 -= 86400;
                        }
                        jSONObject.put("wakeClock" + string, i9 / 60);
                    }
                    if (this.state[i3] == 4) {
                        i2++;
                        sleepTime += getSleepTime(i3);
                    }
                }
                boolean z2 = this.isUserOff;
                ?? r1 = z2;
                if (this.isCloudOff) {
                    r1 = (z2 ? 1 : 0) | 2;
                }
                int i10 = this.foreignSim;
                ?? r2 = r1;
                if (i10 >= 2) {
                    r2 = (r1 == true ? 1 : 0) | 16;
                } else if (i10 == 1) {
                    r2 = (r1 == true ? 1 : 0) | 8;
                }
                int i11 = r2;
                if (this.foreignNet >= 1) {
                    i11 = (r2 == true ? 1 : 0) | 4;
                }
                jSONObject.put("index", this.index).put("sleepTotalDuration", sleepTime / 60).put("sleepCount", i2).put("sleepTotalConsumeBattery", this.sleepTotalConsumeBattery).put("nightScreenOffConsumeBattery", getNightScreenOffConsumeBattery()).put("previousNotification", this.previousNotification).put("previous", this.previous).put("restoreWarning", this.restoreWarning).put("rejectReason", i11).put("nightTimeOffset", this.nightTimeOffset / 60).put("nightTime", this.nightTime / 60).put("monitorTime", this.monitorTime / 60).put("dayTime", this.dayTime / 60).put("lightLux", this.lightLux).put("chargedTotalDuration", this.chargedTotalDuration / 60);
            } catch (JSONException e2) {
                Slog.w(PhoneSleepModeController.TAG, "toJson exception", e2);
            }
            return jSONObject;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(2048);
            sb.append('{');
            sb.append("isUserOff=");
            sb.append(this.isUserOff);
            sb.append(" foreignSim=");
            sb.append(this.foreignSim);
            sb.append(" net=");
            sb.append(this.foreignNet);
            if (PhoneSleepModeController.this.mSleepState.nightTime <= 0) {
                sb.append(" nightDuration={-1, -1, 0}");
            } else {
                int iElapsedRealtime = PhoneSleepModeController.this.mSleepState.dayTime <= 0 ? (int) (SystemClock.elapsedRealtime() / 1000) : PhoneSleepModeController.this.mSleepState.dayTime;
                int i2 = 0;
                for (int i3 = 0; i3 < this.MAX_SLEEP_TIMES; i3++) {
                    if (this.state[i3] == 4) {
                        i2 += this.wakeTime[i3] - this.sleepTime[i3];
                    }
                }
                int i4 = this.chargedTotalDuration;
                int i5 = this.chargingTime;
                if (i5 > 0) {
                    i4 += iElapsedRealtime - i5;
                }
                DecimalFormat decimalFormat = new DecimalFormat("0.0");
                sb.append(" nightDuration={");
                sb.append(decimalFormat.format(((double) (iElapsedRealtime - PhoneSleepModeController.this.mSleepState.nightTime)) / 3600.0d));
                sb.append('h');
                sb.append(", ");
                sb.append(decimalFormat.format(((double) i2) / 3600.0d));
                sb.append('h');
                sb.append(", ");
                sb.append(decimalFormat.format(((double) i4) / 3600.0d));
                sb.append("h}");
            }
            if (PhoneSleepModeController.this.mSleepState.nightPowerConsumed < 0) {
                sb.append(" battery={-1");
            } else {
                sb.append(" battery={");
                sb.append(PhoneSleepModeController.this.mSleepState.dayPowerConsumed - PhoneSleepModeController.this.mSleepState.nightPowerConsumed);
            }
            sb.append(", ");
            sb.append(PhoneSleepModeController.this.mSleepState.sleepTotalConsumeBattery);
            sb.append(", ");
            sb.append(this.nightPowerConsumed);
            sb.append(", ");
            sb.append(this.dayPowerConsumed);
            sb.append('}');
            sb.append(" cloud={");
            sb.append(this.isCloudOff);
            sb.append(", ");
            sb.append(this.MAX_SLEEP_TIMES);
            sb.append(", ");
            sb.append(this.STATIONARY_TIME_THRESHOLD);
            sb.append(", ");
            sb.append(this.LIGHT_LUX_THRESHOLD);
            sb.append(", ");
            sb.append(this.BEGIN_TIME);
            sb.append(", ");
            sb.append(this.END_TIME);
            sb.append('}');
            sb.append(" applyRestore={0x");
            sb.append(Integer.toHexString(this.previous));
            sb.append(", ");
            sb.append(this.previousNotification);
            sb.append(", 0x");
            sb.append(Integer.toHexString(this.restore));
            sb.append(", 0x");
            sb.append(Integer.toHexString(this.restoreWarning));
            sb.append('}');
            sb.append(" timestamp={");
            sb.append(this.nightTimeOffset);
            sb.append(", ");
            sb.append(this.nightTime);
            sb.append(", ");
            sb.append(this.monitorTime);
            sb.append(", ");
            sb.append(this.dayTime);
            sb.append(", ");
            sb.append(this.stepStopTime);
            sb.append(", ");
            sb.append(this.gpsStopTime);
            sb.append(", ");
            sb.append(this.lightTime);
            sb.append('}');
            sb.append(" light=");
            sb.append(this.lightLux);
            for (int i6 = 0; i6 < this.failedReason.length; i6++) {
                sb.append(" state");
                sb.append(i6);
                sb.append("={");
                sb.append(this.state[i6]);
                sb.append(", ");
                sb.append(this.sleepTime[i6]);
                sb.append(", ");
                sb.append(this.wakeTime[i6]);
                sb.append(", 0x");
                sb.append(Integer.toHexString(this.failedReason[i6]));
                sb.append(", 0x");
                sb.append(Integer.toHexString(this.quitReason[i6]));
                sb.append('}');
            }
            sb.append('}');
            return sb.toString();
        }

        private SleepState() {
            this.INDEX_DATA = 1;
            this.INDEX_WIFI = 2;
            this.INDEX_WIFI_AP = 4;
            this.INDEX_GPS = 8;
            this.INDEX_NOTIFICATION = 16;
            this.INDEX_FINGER = 32;
            this.INDEX_PICKUP_WAKE = 128;
            this.INDEX_WIFI_SCAN = 256;
            this.INDEX_LIGHT = 512;
            this.INDEX_BLUETOOTH = 1024;
            this.INDEX_NETWORK = 2048;
            this.previous = 0;
            this.restore = 0;
            this.oldFeatureStatus = "enhance";
            this.nightPowerConsumed = -1;
            this.dayPowerConsumed = -1;
            this.sleepBatteryLevel = 0;
            this.sleepTotalConsumeBattery = 0;
            resetThreshold();
        }
    }

    private class SleepSwitchObserver extends ContentObserver {
        public SleepSwitchObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z2, Uri uri) {
            String lastPathSegment = uri.getLastPathSegment();
            if (Constant.FUNC_SLEEP_MODE_USER.equals(lastPathSegment)) {
                if (PhoneSleepModeController.this.mSleepState.isUserOff != Utils.isSleepModeEnabled(PhoneSleepModeController.this.mContext)) {
                    return;
                }
                PhoneSleepModeController.this.mSleepState.isUserOff = !PhoneSleepModeController.this.mSleepState.isUserOff;
                if (PhoneSleepModeController.DBG_SLEEP) {
                    Slog.d(PhoneSleepModeController.TAG, "onChange isUserOff=" + PhoneSleepModeController.this.mSleepState.isUserOff);
                }
                if (PhoneSleepModeController.this.mSleepState.isUserOff) {
                    PhoneSleepModeController.this.tryToQuitSleep(64);
                    return;
                } else {
                    if (PhoneSleepModeController.this.hasTimeToSleep()) {
                        PhoneSleepModeController.this.startListenDeviceStateIfNeeded(LogType.LOG_TYPE_SAMPLING_INTERRUPT);
                        return;
                    }
                    return;
                }
            }
            if (Constant.FUNC_SLEEP_MODE_CLOUD.equals(lastPathSegment) && PhoneSleepModeController.this.mSleepState.isCloudOff == PhoneSleepModeController.this.isSleepModeCloudEnabled()) {
                PhoneSleepModeController.this.mSleepState.isCloudOff = !PhoneSleepModeController.this.mSleepState.isCloudOff;
                if (PhoneSleepModeController.DBG_SLEEP) {
                    Slog.d(PhoneSleepModeController.TAG, "onChange isCloudOff=" + PhoneSleepModeController.this.mSleepState.isCloudOff);
                }
                if (PhoneSleepModeController.this.mSleepState.isCloudOff) {
                    PhoneSleepModeController.this.tryToQuitSleep(128);
                } else if (PhoneSleepModeController.this.hasTimeToSleep()) {
                    PhoneSleepModeController.this.startListenDeviceStateIfNeeded(LogType.LOG_TYPE_SAMPLING_WIFI_DATA);
                }
            }
        }
    }

    static {
        boolean zIsLoggable = Log.isLoggable("power.sleep.time", 3);
        DBG_COMPRESS_TIME = zIsLoggable;
        DBG_SLEEP = zIsLoggable || Log.isLoggable(TAG, 3);
        RANDOM_DURATION = (zIsLoggable ? 1 : 30) * 60000;
        CHECK_SCREEN_OFF_DURATION = zIsLoggable ? new int[]{60000, 60000, 60000, 60000} : new int[]{1800000, Constants.MAX_VALID_TIME_FOR_REGISTRATION_REQUEST, 600000, PowerCheckerCloudConfigure.DEFAULT_WAKEUP_ALARM_INTERVAL};
        CHECK_STATIONARY_DURATION = (zIsLoggable ? 1 : 10) * 60000;
        DEFAULT_SLEEP_MAX_TIMES = zIsLoggable ? 50 : 2;
        DEFAULT_STATIONARY_TIME_THRESHOLD = (zIsLoggable ? 1 : 9) * 60;
        KEY_RESOURCE_STATE = "key_sleep_state";
        KEY_SLEEP_REBOOT_TIME = "sleep_reboot_time";
        mRestrictPkgs = InternationalAdapter.mRestrictPkgs;
        mNumConnectedDevices = 0;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private PhoneSleepModeController(Context context) {
        this.mLocalLog = new LocalLog(DBG_SLEEP ? 128 : 64);
        Object[] objArr = 0;
        Object[] objArr2 = 0;
        this.mGpsPkg = null;
        this.mGpsStartTime = 0;
        this.isReceiverEnterDay = false;
        this.isStopSatellite = false;
        this.mForceSleep = false;
        this.listeners = new ArrayList();
        this.mNetworkAllowApps = InternationalAdapter.sNetworkAllowApps;
        this.mContext = context;
        getSleepModeCloudFromNewCloud();
        initCloudConfig();
        b.a.p().t(b.b.L, new SleepModeCloudParamsObserver());
        this.mSleepState = new SleepState();
        this.mHandler = new SleepHandler(PowerStateMachineService.getThread().getLooper());
        this.mClockReceiver = new ClockReceiver();
        IntentFilter intentFilter = new IntentFilter(ACTION_ENTER_NIGHT);
        intentFilter.addAction(ACTION_ENTER_DAY);
        intentFilter.addAction(ACTION_BLUETOOTH_OPP_INBOUND_START);
        intentFilter.addAction(ACTION_BLUETOOTH_OPP_INBOUND_END);
        intentFilter.addAction(ACTION_BLUETOOTH_OPP_OUTBOUND_START);
        intentFilter.addAction(ACTION_BLUETOOTH_OPP_OUTBOUND_END);
        this.mContext.registerReceiver(this.mClockReceiver, intentFilter, 2);
        readFromDb(this.mSleepState);
        SleepState sleepState = this.mSleepState;
        if (sleepState.state[0] == 3) {
            restoreSleepConfig(sleepState);
            writeToDb(this.mSleepState.reset());
        }
        this.mContext.registerReceiver(new DumpTheReasonOfSleepModeOff(), new IntentFilter(SLEEP_MODE_OFF_BROADCAST), 2);
        this.mHandler.sendMessage(this.mHandler.obtainMessage(6, 16777216, -1));
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor(SETTINGS_SLEEP_MODE_SWITCH_ATTR), false, new SleepModeSwitchObserver(this.mHandler));
        b.a.p().t(b.b.K, new BugAppsClearSwitchCloudObserver());
        this.mBugAppClearSwitch = getCloudSwitchOfBugAppsClear();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void applySleepConfig(SleepState sleepState) {
        WifiInfo connectionInfo;
        PowerManager.WakeLock wakeLockNewWakeLock = ((PowerManager) this.mContext.getSystemService(NightStandbyRecord.KEY_POWER)).newWakeLock(1, "phoneSleepMode:sleep_enter");
        boolean z2 = SystemProperties.getBoolean("persist.sys.spc.powerkill.newpolicy.enable", false);
        wakeLockNewWakeLock.acquire(200L);
        StringBuilder sb = new StringBuilder(256);
        sb.append("applySleepConfig index=" + sleepState.index);
        Slog.i(TAG, "milletEnable is " + z2);
        if (!z2 || getNightKillAppEnable()) {
            clearApp();
        }
        Slog.i(TAG, "mBugAppClearSwitch is " + this.mBugAppClearSwitch);
        if (this.mBugAppClearSwitch) {
            clearHaveBugApps();
        }
        int intForUser = Settings.Secure.getIntForUser(this.mContext.getContentResolverForUser(UserHandle.OWNER), "key_open_earthquake_warning", 0, 0);
        Slog.i(TAG, "earthquakeWarningFlag is " + intForUser);
        if (intForUser != 1) {
            TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
            sleepState.setPreviousEnable(1, CommonAdapter.isDataEnabled(telephonyManager));
            writeToDb(sleepState);
            if (sleepState.getPreviousEnable(1)) {
                sb.append(" setDataEnabled false");
                CommonAdapter.setDataEnabled(telephonyManager, false);
            }
            WifiManager wifiManager = (WifiManager) this.mContext.getSystemService("wifi");
            if (NetUtils.isWifiConnected(this.mContext) && (connectionInfo = wifiManager.getConnectionInfo()) != null && !connectionInfo.getSSID().equals(CommonAdapter.UNKNOWN_SSID)) {
                SimpleSettings.Misc.putString(this.mContext, "wifi_ssid_when_sleep", connectionInfo.getSSID());
            }
            int i2 = 2;
            sleepState.setPreviousEnable(2, wifiManager.isWifiEnabled());
            writeToDb(sleepState);
            if (sleepState.getPreviousEnable(2)) {
                sb.append(" setWifiEnabled false");
                wifiManager.setWifiEnabled(false);
            }
            this.mHandler.sendEmptyMessageDelayed(11, 2000L);
            int i3 = Settings.System.getInt(this.mContext.getContentResolver(), "wakeup_for_keyguard_notification", -1);
            if (i3 != -1) {
                i2 = i3;
            } else if (!isSupportAodAnimateDevice()) {
                i2 = 1;
            }
            sleepState.previousNotification = i2;
            sleepState.setRestore(16, i2 > 0);
            writeToDb(sleepState);
            if (i2 > 0) {
                sb.append(" setWakeUpForKeyguardNotification from " + i2 + " to 0");
                Settings.System.putInt(this.mContext.getContentResolver(), "wakeup_for_keyguard_notification", 0);
            }
        }
        if (SystemProperties.getBoolean("ro.hardware.fp.fod", false)) {
            sleepState.setPreviousEnable(32, isFodAodShowEnable());
            writeToDb(sleepState);
            if (sleepState.getPreviousEnable(32)) {
                sb.append(" setFodAodShowEnable false");
                setFodAodShowEnable(false);
            }
        }
        sleepState.setPreviousEnable(128, isPickupWakeupEnable());
        writeToDb(sleepState);
        if (sleepState.getPreviousEnable(128)) {
            sb.append(" setPickupWakeupEnable false");
            setPickupWakeupEnable(false);
        }
        sleepState.setPreviousEnable(512, Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "light_turn_on", 0, -2) == 1);
        writeToDb(sleepState);
        if (sleepState.getPreviousEnable(512)) {
            sb.append(" setLightEnabled false");
            Settings.Secure.putInt(this.mContext.getContentResolver(), "light_turn_on", 0);
        }
        BluetoothAdapter adapter = ((BluetoothManager) this.mContext.getSystemService("bluetooth")).getAdapter();
        boolean z3 = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "bluetooth_on", 0, -2) == 1;
        boolean zIsBlueToothConnected = isBlueToothConnected(this.mContext);
        sleepState.setPreviousEnable(1024, z3);
        writeToDb(sleepState);
        if (sleepState.getPreviousEnable(1024) && !zIsBlueToothConnected) {
            sb.append("  setBluetoothEnabled false");
            Settings.Secure.putInt(this.mContext.getContentResolver(), "bluetooth_on", 0);
            adapter.disable();
        }
        if (intForUser == 1) {
            sb.append("  setNetworkEnabled false");
            handleNetworkForEarthquakeWarning(sleepState);
        }
        if (intForUser != 1 || this.mControlAlarmEw) {
            restrictAlarm(true);
        }
        logWithsLocal(sb.toString());
        registerRequestWakeReceiver();
        broadcastSleepState(1);
        notifySleepState(1, null);
        setSleepModeStatus(this.mContext, true);
        if (wakeLockNewWakeLock.isHeld()) {
            wakeLockNewWakeLock.release();
        }
    }

    private void broadcastSleepState(int i2) {
        Intent intent = new Intent("com.miui.powerkeeper_sleep_changed");
        intent.putExtra(YellowPageContract.Permission.STATE, i2);
        this.mContext.sendBroadcast(intent);
        for (PowerKeeperInterface.a0 a0Var : this.listeners) {
            boolean z2 = true;
            if (i2 != 1) {
                z2 = false;
            }
            a0Var.onSleepMode(z2);
        }
        Slog.d(TAG, "sleepstate broadcast:" + intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean cancelCheckStationaryAlarm(int i2) {
        if (this.mCheckStationaryPi == null) {
            return false;
        }
        if (DBG_SLEEP) {
            Slog.d(TAG, "cancelCheckStationaryAlarm reason=" + reasonToString(i2));
        }
        ((AlarmManager) this.mContext.getSystemService("alarm")).cancel(this.mCheckStationaryPi);
        this.mCheckStationaryPi = null;
        return true;
    }

    private void cancelClockTimer() {
        logWithsLocal("cancelClockTimer");
        AlarmManager alarmManager = (AlarmManager) this.mContext.getSystemService("alarm");
        Intent intent = new Intent(ACTION_ENTER_NIGHT);
        Intent intent2 = new Intent(ACTION_ENTER_DAY);
        alarmManager.cancel(PendingIntent.getBroadcast(this.mContext, 0, intent, 67108864));
        alarmManager.cancel(PendingIntent.getBroadcast(this.mContext, 0, intent2, 67108864));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean cancelScreenOffAlarm(int i2) {
        if (this.mScreeenOffPi == null) {
            return false;
        }
        if (DBG_SLEEP) {
            Slog.d(TAG, "cancelScreenOffAlarm reason=" + reasonToString(i2));
        }
        ((AlarmManager) this.mContext.getSystemService("alarm")).cancel(this.mScreeenOffPi);
        this.mScreeenOffPi = null;
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkChineseUser() {
        if (Build.IS_INTERNATIONAL_BUILD) {
            return;
        }
        TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
        int phoneCount = telephonyManager.getPhoneCount();
        for (int i2 = 0; i2 < phoneCount; i2++) {
            String simOperatorNumericForPhone = telephonyManager.getSimOperatorNumericForPhone(i2);
            if (!simOperatorNumericForPhone.isEmpty()) {
                if (!simOperatorNumericForPhone.startsWith("460")) {
                    this.mSleepState.foreignSim++;
                }
                String networkOperatorForPhone = telephonyManager.getNetworkOperatorForPhone(i2);
                if (!networkOperatorForPhone.isEmpty() && !networkOperatorForPhone.startsWith("460")) {
                    this.mSleepState.foreignNet++;
                }
            }
        }
    }

    private void clearApp() {
        ProcessConfig processConfig = new ProcessConfig(15);
        List<String> activePkgList = PowerKeeperManager.getInstance().getActiveController().getActivePkgList(this.mContext, 3);
        if (activePkgList != null) {
            processConfig.setWhiteList(activePkgList);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("AutoLockOffClean in sleep ");
        sb.append(activePkgList == null ? "" : activePkgList.toString());
        PowerLog.recordKey(sb.toString());
        if (DBG_SLEEP) {
            Slog.d(TAG, "clearApp whiteList=" + activePkgList);
        }
        try {
            ProcessManager.kill(processConfig);
        } catch (Exception e2) {
            Slog.e(TAG, "clearApp", e2);
        }
        PowerStateMachine.clearUnactiveApps(this.mContext);
    }

    private void clearHaveBugApps() {
        String[] cloudAppsListOfBugAppsClear = getCloudAppsListOfBugAppsClear();
        if (cloudAppsListOfBugAppsClear == null) {
            return;
        }
        for (String str : cloudAppsListOfBugAppsClear) {
            boolean zIsLockedApplication = ProcessManager.isLockedApplication(str, 0);
            Log.d(TAG, " is unLocked  ：" + zIsLockedApplication);
            if (!zIsLockedApplication) {
                try {
                    List runningProcessInfoByPackageName = ProcessManager.getRunningProcessInfoByPackageName(str);
                    if (runningProcessInfoByPackageName == null || runningProcessInfoByPackageName.isEmpty()) {
                        Log.d(TAG, str + " ->  infoList is null");
                    } else {
                        int i2 = ((RunningProcessInfo) runningProcessInfoByPackageName.get(0)).mUid;
                        ActiveController activeController = PowerKeeperManager.getInstance().getActiveController();
                        if ((activeController.getAppActiveType(i2) & 3) != 0) {
                            ProcessManager.kill(new ProcessConfig(13, str, i2));
                            logWithsLocal("bug Apps Clear kill  " + str);
                        } else {
                            StringBuilder sb = new StringBuilder();
                            sb.append("the kill app info ");
                            sb.append(str);
                            sb.append("  isKilled : ");
                            sb.append((activeController.getAppActiveType(i2) & 3) != 0);
                            Log.d(TAG, sb.toString());
                        }
                    }
                } catch (RemoteException e2) {
                    Log.e(TAG, e2.toString());
                } catch (Exception e3) {
                    Log.e(TAG, e3.toString());
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean closeGps() {
        SleepState sleepState = this.mSleepState;
        int i2 = sleepState.index;
        int[] iArr = sleepState.state;
        if (i2 >= iArr.length || iArr[i2] != 3) {
            logWithsLocal("ignore setGpsEnabled");
            return false;
        }
        Objects.requireNonNull(sleepState);
        sleepState.setPreviousEnable(8, CommonAdapter.isGpsEnabled(this.mContext));
        writeToDb(this.mSleepState);
        SleepState sleepState2 = this.mSleepState;
        Objects.requireNonNull(sleepState2);
        if (!sleepState2.getPreviousEnable(8)) {
            return false;
        }
        logWithsLocal("setGpsEnabled false");
        CommonAdapter.setGpsEnabled(this.mContext, false);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean closeWifiAp() {
        SleepState sleepState = this.mSleepState;
        int i2 = sleepState.index;
        int[] iArr = sleepState.state;
        if (i2 >= iArr.length || iArr[i2] != 3) {
            logWithsLocal("ignore setWifiApEnabled");
            return false;
        }
        boolean zIsWifiApEnabled = ((WifiManager) this.mContext.getSystemService("wifi")).isWifiApEnabled();
        SleepState sleepState2 = this.mSleepState;
        Objects.requireNonNull(sleepState2);
        sleepState2.setPreviousEnable(4, zIsWifiApEnabled);
        writeToDb(this.mSleepState);
        SleepState sleepState3 = this.mSleepState;
        Objects.requireNonNull(sleepState3);
        if (!sleepState3.getPreviousEnable(4)) {
            return false;
        }
        logWithsLocal("setWifiApEnabled false");
        WifiApAdapter.setWifiApEnabled(this.mContext, false);
        return true;
    }

    private String[] getCloudAppsListOfBugAppsClear() {
        b.a aVarP = b.a.p();
        int i2 = b.b.K;
        JSONObject jSONObjectL = aVarP.l(i2);
        String strOptString = (!aVarP.s(i2) || jSONObjectL == null) ? null : jSONObjectL.optString("bugApps", "");
        if (strOptString == null || "".equals(strOptString)) {
            return null;
        }
        return strOptString.split(";");
    }

    private String[] getCloudAppsNetWhiteListOfEarthquakeWarn() {
        Log.i(TAG, "get sleep mode network allow apps is " + this.mNetworkAllowApps);
        return this.mNetworkAllowApps.split(",");
    }

    private boolean getCloudSwitchOfBugAppsClear() {
        b.a aVarP = b.a.p();
        int i2 = b.b.K;
        JSONObject jSONObjectL = aVarP.l(i2);
        this.sSleepModeForEarthquakeWarnFlag = getSleepModeForEarthquakeWarnFlag();
        if (!aVarP.s(i2) || jSONObjectL == null) {
            return false;
        }
        boolean zOptBoolean = jSONObjectL.optBoolean("bugAppsClearSwitch", false);
        Log.d(TAG, "the cloud data of bug app Clear is " + zOptBoolean + ", sSleepModeForEarthquakeWarnFlag is " + this.sSleepModeForEarthquakeWarnFlag);
        return zOptBoolean;
    }

    private static String getHideMode(Context context) {
        return GlobalFeatureConfigureHelper.getUserConfigure(context);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getIndex() {
        int i2 = Calendar.getInstance().get(11);
        if (i2 == 1 || i2 == 5) {
            return 1;
        }
        if (i2 == 2) {
            return 2;
        }
        return (i2 == 3 || i2 == 4) ? 3 : 0;
    }

    private boolean getNightKillAppEnable() {
        b.a aVarP = b.a.p();
        int i2 = b.b.I;
        JSONObject jSONObjectL = aVarP.l(i2);
        if (!b.a.p().s(i2)) {
            return false;
        }
        boolean zOptBoolean = jSONObjectL.optBoolean("night_switch", false);
        Log.d(TAG, "Sleep Mode nightCleanEnable is " + zOptBoolean);
        return zOptBoolean;
    }

    private void getSleepModeCloudFromNewCloud() {
        JSONObject jSONObjectL;
        b.a aVarP = b.a.p();
        int i2 = b.b.L;
        if (!aVarP.s(i2) || (jSONObjectL = b.a.p().l(i2)) == null) {
            return;
        }
        putCloudDataToDB(jSONObjectL);
    }

    private void handleNetworkForEarthquakeWarning(SleepState sleepState) {
        Objects.requireNonNull(sleepState);
        sleepState.setPreviousEnable(2048, true);
        whitelistCloudAppsForEarthquakeWarning();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean hasTimeToSleep() {
        int i2 = Calendar.getInstance().get(11);
        SleepState sleepState = this.mSleepState;
        int i3 = sleepState.BEGIN_TIME;
        boolean z2 = true;
        if (i3 <= 12 ? i2 < i3 || i2 >= sleepState.END_TIME - 1 : i2 < i3 && i2 >= sleepState.END_TIME - 1) {
            z2 = false;
        }
        if (DBG_SLEEP) {
            Slog.d(TAG, "hasTimeToSleep curHour=" + i2 + " ret=" + z2);
        }
        return z2;
    }

    public static PhoneSleepModeController init(Context context) {
        if (!Utils.isSleepModeViewVisable(true)) {
            if (DBG_SLEEP) {
                Slog.d(TAG, "init return for not in regison china");
            }
            return null;
        }
        long totalPhysicalMemory = HardwareInfo.getTotalPhysicalMemory() / 1073741824;
        if (totalPhysicalMemory < 3) {
            if (DBG_SLEEP) {
                Slog.d(TAG, "init return for sizeGb = " + totalPhysicalMemory);
            }
            return null;
        }
        if (sInstance == null) {
            synchronized (PhoneSleepModeController.class) {
                try {
                    if (sInstance == null) {
                        sInstance = new PhoneSleepModeController(context);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return sInstance;
    }

    private void initCloudConfig() {
        JSONObject jSONObjectL;
        b.a aVarP = b.a.p();
        this.mNetworkAllowApps = SimpleSettings.Misc.getString(this.mContext, SimpleSettingKeys.KEY_SLEEP_MODE_NETWORK_WHITE_APPS, InternationalAdapter.sNetworkAllowApps);
        int i2 = b.b.K;
        if (!aVarP.s(i2) || (jSONObjectL = aVarP.l(i2)) == null) {
            return;
        }
        this.mNetworkAllowApps = jSONObjectL.optString("networkAllowApps", SimpleSettings.Misc.getString(this.mContext, SimpleSettingKeys.KEY_SLEEP_MODE_NETWORK_WHITE_APPS, InternationalAdapter.sNetworkAllowApps));
        this.mControlAlarmEw = jSONObjectL.optBoolean("controlalarmEw", true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isBackupAndRestore() {
        List<ActivityManager.RunningServiceInfo> runningServices = ((ActivityManager) this.mContext.getSystemService("activity")).getRunningServices(2000);
        StringBuilder sb = DBG_SLEEP ? new StringBuilder(2048) : null;
        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {
            if (sb != null) {
                sb.append(',');
                sb.append(runningServiceInfo.service.getClassName());
            }
            if (runningServiceInfo.service.getClassName().equals("com.miui.backup.service.TransFileService") && runningServiceInfo.service.getPackageName().equals("com.miui.backup")) {
                if (sb == null) {
                    return true;
                }
                Slog.d(TAG, "isBackupAndRestore found " + ((Object) sb));
                return true;
            }
        }
        if (sb == null) {
            return false;
        }
        Slog.d(TAG, "isBackupAndRestore " + ((Object) sb));
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isCheckRegion(String str) {
        return SystemProperties.get("ro.miui.region", "IN").equalsIgnoreCase(str);
    }

    private boolean isFodAodShowEnable() {
        boolean z2 = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "gxzw_icon_aod_show_enable", 1, 0) == 1;
        if (DBG_SLEEP) {
            Slog.d(TAG, "isFodAodShowEnable ret=" + z2);
        }
        return z2;
    }

    private boolean isPickupWakeupEnable() {
        boolean booleanForUser = MiuiSettings.System.getBooleanForUser(this.mContext.getContentResolver(), "pick_up_gesture_wakeup_mode", false, UserHandle.myUserId());
        if (DBG_SLEEP) {
            Slog.d(TAG, "isPickupWakeupEnable ret=" + booleanForUser);
        }
        return booleanForUser;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isSleepModeCloudEnabled() {
        return SimpleSettings.Misc.getBooleanForUser(this.mContext, Constant.FUNC_SLEEP_MODE_CLOUD, true, 0);
    }

    private boolean isSupportAodAnimateDevice() {
        return "perseus".equals(android.os.Build.DEVICE);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifySleepState(int i2, String str) {
        Log.i(TAG, "notifySleepState state=" + i2 + ", reason=" + str);
        EventsAggregator.getInstance(this.mContext).notifySleepStateListeners(i2 == 1, str);
    }

    private static void onSleepModeWhitelistChange(int i2, boolean z2) {
        try {
            if (((Boolean) IMiuiNetworkManager.class.getMethod("onSleepModeWhitelistChange", Integer.TYPE, Boolean.TYPE).invoke(IMiuiNetworkManager.Stub.asInterface((IBinder) ReflectUtil.callObjectMethod(ReflectUtil.callStaticObjectMethod(Class.forName("android.os.INetworkManagementService$Stub"), "asInterface", new Class[]{IBinder.class}, new Object[]{(IBinder) ReflectUtil.callStaticObjectMethod(Class.forName("android.os.ServiceManager"), IBinder.class, "getService", new Class[]{String.class}, new Object[]{(String) ReflectUtil.getStaticObjectField(Class.forName("android.content.Context"), "NETWORKMANAGEMENT_SERVICE", String.class)})}), "getMiuiNetworkManager", (Class[]) null, new Object[0])), Integer.valueOf(i2), Boolean.valueOf(z2))).booleanValue()) {
                Log.i(TAG, "onSleepModeWhitelistChange: appId is " + i2 + ", added is " + z2);
            }
        } catch (Exception e2) {
            Log.i(TAG, e2.toString());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void putCloudDataToDB(JSONObject jSONObject) {
        boolean zOptBoolean = jSONObject.optBoolean("fucSwitch", true);
        SimpleSettings.Misc.putBoolean(this.mContext, Constant.FUNC_SLEEP_MODE_CLOUD, zOptBoolean);
        String strOptString = jSONObject.optString("param");
        if (TextUtils.isEmpty(strOptString)) {
            SimpleSettings.Misc.delete(this.mContext, SLEEP_MODE_PARAMS);
        } else {
            SimpleSettings.Misc.putString(this.mContext, SLEEP_MODE_PARAMS, strOptString);
        }
        String strOptString2 = jSONObject.optString(CloudUpdateHideMode.PARAM2);
        if (TextUtils.isEmpty(strOptString2)) {
            SimpleSettings.Misc.delete(this.mContext, SLEEP_MODE_PARAMS2);
        } else {
            SimpleSettings.Misc.putString(this.mContext, SLEEP_MODE_PARAMS2, strOptString2);
        }
        if (DBG_SLEEP) {
            Log.d(TAG, "cloud Data => " + zOptBoolean + " - " + strOptString + " - " + strOptString2);
        }
    }

    private SleepState readFromDb(SleepState sleepState) {
        try {
            String string = SimpleSettings.Misc.getString(this.mContext, KEY_RESOURCE_STATE, "");
            if (!string.isEmpty()) {
                JSONObject jSONObject = new JSONObject(string);
                sleepState.state[0] = jSONObject.optInt(YellowPageContract.Permission.STATE, 0);
                sleepState.previous = jSONObject.optInt("previous", 0);
                sleepState.restore = jSONObject.optInt("restore", 0);
                sleepState.oldFeatureStatus = jSONObject.optString("oldFeatureStatus", "enhance");
                sleepState.previousNotification = jSONObject.optInt("previousNotification", 0);
            }
        } catch (JSONException e2) {
            Slog.w(TAG, "readFromDb exception", e2);
        }
        logWithsLocal("readFromDb ss: state=" + sleepState.state[0] + " previous=0x" + Integer.toHexString(sleepState.previous) + " restore=0x" + Integer.toHexString(sleepState.restore) + " noti=" + sleepState.previousNotification + " rebootTime=" + SimpleSettings.Misc.getString(this.mContext, KEY_SLEEP_REBOOT_TIME, "") + " oldFeatureStatus=" + sleepState.oldFeatureStatus);
        return sleepState;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String reasonToString(int i2) {
        String str = (i2 & 1) != 0 ? "press_power" : null;
        if ((i2 & 2) != 0) {
            if (str == null) {
                str = MIY.BUNDLE_PARAM_CHARGING;
            } else {
                str = str + ",charging";
            }
        }
        if ((i2 & 4) != 0) {
            if (str == null) {
                str = "screen_on";
            } else {
                str = str + ",screen_on";
            }
        }
        if ((i2 & 8) != 0) {
            if (str == null) {
                str = "user_present";
            } else {
                str = str + ",user_present";
            }
        }
        if ((i2 & 16) != 0) {
            if (str == null) {
                str = "enter_day";
            } else {
                str = str + ",enter_day";
            }
        }
        if ((16777216 & i2) != 0) {
            if (str == null) {
                str = "powerkeeper_restart";
            } else {
                str = str + ",powerkeeper_restart";
            }
        }
        if ((i2 & 64) != 0) {
            if (str == null) {
                str = "user_off";
            } else {
                str = str + ",user_off";
            }
        }
        if ((i2 & 128) != 0) {
            if (str == null) {
                str = "cloud_off";
            } else {
                str = str + ",cloud_off";
            }
        }
        if ((262144 & i2) != 0) {
            if (str == null) {
                str = "dispose";
            } else {
                str = str + ",dispose";
            }
        }
        if ((i2 & 1024) != 0) {
            if (str == null) {
                str = "step";
            } else {
                str = str + ",step";
            }
        }
        if ((i2 & 2048) != 0) {
            if (str == null) {
                str = "gps";
            } else {
                str = str + ",gps";
            }
        }
        if ((i2 & 4096) != 0) {
            if (str == null) {
                str = "wifi_hotspot";
            } else {
                str = str + ",wifi_hotspot";
            }
        }
        if ((524288 & i2) != 0) {
            if (str == null) {
                str = MessageType.SCREEN_OFF;
            } else {
                str = str + ",screen_off";
            }
        }
        if ((1048576 & i2) != 0) {
            if (str == null) {
                str = "enter_night";
            } else {
                str = str + ",enter_night";
            }
        }
        if ((2097152 & i2) != 0) {
            if (str == null) {
                str = "uncharging";
            } else {
                str = str + ",uncharging";
            }
        }
        if ((65536 & i2) != 0) {
            if (str == null) {
                str = "user_on";
            } else {
                str = str + ",user_on";
            }
        }
        if ((131072 & i2) != 0) {
            if (str == null) {
                str = "cloud_on";
            } else {
                str = str + ",cloud_on";
            }
        }
        if ((i2 & 256) != 0) {
            if (str == null) {
                str = "light";
            } else {
                str = str + ",light";
            }
        }
        if ((4194304 & i2) != 0) {
            if (str == null) {
                str = "screen_off_timeout";
            } else {
                str = str + ",screen_off_timeout";
            }
        }
        if ((8388608 & i2) != 0) {
            if (str == null) {
                str = "check_stationary_timeout";
            } else {
                str = str + ",check_stationary_timeout";
            }
        }
        if ((i2 & 32) != 0) {
            if (str == null) {
                str = "desk_clock";
            } else {
                str = str + ",desk_clock";
            }
        }
        if ((33554432 & i2) != 0) {
            if (str == null) {
                str = "max_times";
            } else {
                str = str + ",max_times";
            }
        }
        if ((i2 & 512) != 0) {
            if (str == null) {
                str = "backup_restore";
            } else {
                str = str + ",backup_restore";
            }
        }
        return str == null ? "default" : str;
    }

    private void registerBluetoothStartReceiver() {
        if (this.mBluetoothStartReceiver != null) {
            return;
        }
        if (DBG_SLEEP) {
            Slog.d(TAG, "registerBluetoothStartReceiver");
        }
        this.mBluetoothStartReceiver = new BluetoothStartReceiver();
        this.mContext.registerReceiver(this.mBluetoothStartReceiver, new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED"), 2);
    }

    private void registerCheckStationaryReceiver() {
        if (this.mCheckStationaryReceiver != null) {
            return;
        }
        if (DBG_SLEEP) {
            Slog.d(TAG, "registerCheckStationaryReceiver");
        }
        this.mCheckStationaryReceiver = new CheckStationaryReceiver();
        IntentFilter intentFilter = new IntentFilter(ACTION_SCREEN_OFF_TIMEOUT);
        intentFilter.addAction(ACTION_CHECK_STATIONARY);
        this.mContext.registerReceiver(this.mCheckStationaryReceiver, intentFilter, 2);
    }

    private void registerRequestWakeReceiver() {
        if (this.mRequestWakeReceiver != null) {
            return;
        }
        if (DBG_SLEEP) {
            Slog.d(TAG, "registerRequestWakeReceiver");
        }
        this.mRequestWakeReceiver = new RequestWakeReceiver();
        this.mContext.registerReceiver(this.mRequestWakeReceiver, new IntentFilter("com.miui.powerkeeper_request_wake"), "com.miui.powerkeeper.permission.REQUEST_WAKE", null, 2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerSleepSwitchObserver() {
        if (this.mSleepSwitchObserver != null) {
            return;
        }
        if (DBG_SLEEP) {
            Slog.d(TAG, "registerSleepSwitchObserver");
        }
        ContentResolver contentResolverForUser = this.mContext.getContentResolverForUser(UserHandle.SYSTEM);
        this.mSleepSwitchObserver = new SleepSwitchObserver(this.mHandler);
        this.mSleepState.isUserOff = !Utils.isSleepModeEnabled(this.mContext);
        this.mSleepState.isCloudOff = !isSleepModeCloudEnabled();
        contentResolverForUser.registerContentObserver(SimpleSettings.Misc.getUriFor(Constant.FUNC_SLEEP_MODE_USER), false, this.mSleepSwitchObserver);
        contentResolverForUser.registerContentObserver(SimpleSettings.Misc.getUriFor(Constant.FUNC_SLEEP_MODE_CLOUD), false, this.mSleepSwitchObserver);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerStepDetectorSensor() {
        SensorManager sensorManager = (SensorManager) this.mContext.getSystemService(HideModeStateMachineConfig.STRATEGY_SENSOR);
        if (this.mStepDetector == null) {
            this.mStepDetector = sensorManager.getDefaultSensor(18, false);
        }
        if (this.mLightSensor == null) {
            this.mLightSensor = sensorManager.getDefaultSensor(5, false);
        }
        if ((this.mStepDetector != null || this.mLightSensor != null) && this.mSensorEventListener == null) {
            this.mSensorEventListener = new SensorListener();
        }
        if (this.mStepDetector != null) {
            if (DBG_SLEEP) {
                Slog.d(TAG, "registerStepDetectorSensor mStepDetector=" + this.mStepDetector);
            }
            sensorManager.registerListener(this.mSensorEventListener, this.mStepDetector, 3);
        }
        if (this.mLightSensor != null) {
            if (DBG_SLEEP) {
                Slog.d(TAG, "registerStepDetectorSensor mLightSensor=" + this.mLightSensor);
            }
            SleepState sleepState = this.mSleepState;
            sleepState.lightLux = -1;
            sleepState.lightTime = 0;
            sensorManager.registerListener(this.mSensorEventListener, this.mLightSensor, 3);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetCheckStationaryAlarm(int i2) {
        AlarmManager alarmManager = (AlarmManager) this.mContext.getSystemService("alarm");
        PendingIntent pendingIntent = this.mCheckStationaryPi;
        if (pendingIntent == null) {
            this.mCheckStationaryPi = PendingIntent.getBroadcast(this.mContext, 0, new Intent(ACTION_CHECK_STATIONARY), 67108864);
            if (DBG_SLEEP) {
                Slog.d(TAG, "resetCheckStationaryAlarm reason=" + reasonToString(i2));
            }
        } else {
            alarmManager.cancel(pendingIntent);
            if (DBG_SLEEP) {
                Slog.d(TAG, "resetCheckStationaryAlarm with cancel reason=" + reasonToString(i2));
            }
        }
        alarmManager.set(3, SystemClock.elapsedRealtime() + ((long) CHECK_STATIONARY_DURATION), this.mCheckStationaryPi);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetScreenOffAlarm(int i2, long j2) {
        AlarmManager alarmManager = (AlarmManager) this.mContext.getSystemService("alarm");
        PendingIntent pendingIntent = this.mScreeenOffPi;
        if (pendingIntent == null) {
            this.mScreeenOffPi = PendingIntent.getBroadcast(this.mContext, 0, new Intent(ACTION_SCREEN_OFF_TIMEOUT), 67108864);
            logWithsLocal("resetScreenOffAlarm delay=" + j2 + "ms reason=" + reasonToString(i2));
        } else {
            alarmManager.cancel(pendingIntent);
            logWithsLocal("resetScreenOffAlarm delay=" + j2 + "ms with cancel reason=" + reasonToString(i2));
        }
        alarmManager.set(3, SystemClock.elapsedRealtime() + j2, this.mScreeenOffPi);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void restoreSleepConfig(SleepState sleepState) {
        long j2;
        StringBuilder sb = new StringBuilder(256);
        sb.append("restoreSleepConfig index=" + sleepState.index + " reason=" + reasonToString(sleepState.quitReason[sleepState.index]));
        int intForUser = Settings.Secure.getIntForUser(this.mContext.getContentResolverForUser(UserHandle.OWNER), "key_open_earthquake_warning", 0, 0);
        final WifiManager wifiManager = (WifiManager) this.mContext.getSystemService("wifi");
        if (intForUser == 1 || !sleepState.needRestore(2)) {
            j2 = 0;
        } else {
            sleepState.setRestore(2, false);
            boolean previousEnable = sleepState.getPreviousEnable(2);
            if (previousEnable != wifiManager.isWifiEnabled()) {
                sb.append(" setWifiEnabled:" + previousEnable);
                wifiManager.setWifiEnabled(previousEnable);
                if (previousEnable) {
                    j2 = 300;
                }
            } else {
                sb.append(" setWifiEnabled:warning");
                sleepState.setRestoreWarning(2);
            }
            j2 = 0;
        }
        if (intForUser != 1 && sleepState.needRestore(4)) {
            sleepState.setRestore(4, false);
            boolean previousEnable2 = sleepState.getPreviousEnable(4);
            if (previousEnable2 != wifiManager.isWifiApEnabled()) {
                sb.append(" setWifiApEnabled:" + previousEnable2);
                WifiApAdapter.setWifiApEnabled(this.mContext, previousEnable2);
            } else {
                sleepState.setRestoreWarning(4);
                sb.append(" setWifiApEnabled:warning");
            }
        }
        final TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
        if (intForUser != 1 && sleepState.needRestore(1)) {
            sleepState.setRestore(1, false);
            final boolean previousEnable3 = sleepState.getPreviousEnable(1);
            WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            final int ipAddress = connectionInfo == null ? 0 : connectionInfo.getIpAddress();
            if (previousEnable3 == CommonAdapter.isDataEnabled(telephonyManager)) {
                sb.append(" setDataEnabled:warning");
                sleepState.setRestoreWarning(1);
            } else if (j2 > 0) {
                Runnable runnable = new Runnable() { // from class: com.miui.powerkeeper.statemachine.PhoneSleepModeController.1
                    @Override // java.lang.Runnable
                    public void run() {
                        PhoneSleepModeController.this.logWithsLocal("run setDataEnabled:" + previousEnable3);
                        long jCurrentTimeMillis = System.currentTimeMillis();
                        while (System.currentTimeMillis() - jCurrentTimeMillis <= 700 && (!wifiManager.isWifiEnabled() || ipAddress == 0)) {
                            try {
                                Thread.sleep(100L);
                            } catch (InterruptedException unused) {
                                Thread.currentThread().interrupt();
                            }
                        }
                        CommonAdapter.setDataEnabled(telephonyManager, previousEnable3);
                    }
                };
                sb.append(" setDataEnabled delayMs=" + j2);
                this.mHandler.postDelayed(runnable, j2);
            } else {
                sb.append(" setDataEnabled:" + previousEnable3);
                CommonAdapter.setDataEnabled(telephonyManager, previousEnable3);
            }
        }
        if (intForUser != 1 && sleepState.needRestore(8)) {
            sleepState.setRestore(8, false);
            boolean previousEnable4 = sleepState.getPreviousEnable(8);
            if (previousEnable4 != CommonAdapter.isGpsEnabled(this.mContext)) {
                sb.append(" setGpsEnabled:" + previousEnable4);
                CommonAdapter.setGpsEnabled(this.mContext, previousEnable4);
            } else {
                sb.append(" setGpsEnabled:warning");
                sleepState.setRestoreWarning(8);
            }
        }
        if (intForUser != 1 && sleepState.needRestore(16)) {
            sleepState.setRestore(16, false);
            if (sleepState.previousNotification != Settings.System.getInt(this.mContext.getContentResolver(), "wakeup_for_keyguard_notification", 0)) {
                sb.append(" setWakeUpForKeyguardNotification from 0 to " + sleepState.previousNotification);
                Settings.System.putInt(this.mContext.getContentResolver(), "wakeup_for_keyguard_notification", sleepState.previousNotification);
            } else {
                sb.append(" setWakeUpForKeyguardNotification:warning");
                sleepState.setRestoreWarning(16);
            }
        }
        if (sleepState.needRestore(32)) {
            sleepState.setRestore(32, false);
            boolean previousEnable5 = sleepState.getPreviousEnable(32);
            if (previousEnable5 != isFodAodShowEnable()) {
                sb.append(" setFodAodShowEnable:" + previousEnable5);
                setFodAodShowEnable(previousEnable5);
            } else {
                sb.append(" setFodAodShowEnable:warning");
                sleepState.setRestoreWarning(32);
            }
        }
        if (sleepState.needRestore(128)) {
            sleepState.setRestore(128, false);
            boolean previousEnable6 = sleepState.getPreviousEnable(128);
            if (previousEnable6 != isPickupWakeupEnable()) {
                sb.append(" setPickupWakeupEnable:" + previousEnable6);
                setPickupWakeupEnable(previousEnable6);
            } else {
                sb.append(" setPickupWakeupEnable:warning");
                sleepState.setRestoreWarning(128);
            }
        }
        if (sleepState.needRestore(512)) {
            sleepState.setRestore(512, false);
            boolean previousEnable7 = sleepState.getPreviousEnable(512);
            if (previousEnable7 != (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "light_turn_on", 0, -2) == 1)) {
                sb.append(" setLightEnabled :" + previousEnable7);
                Settings.Secure.putInt(this.mContext.getContentResolver(), "light_turn_on", 1);
            } else {
                sb.append("setLightEnabled:warning");
                sleepState.setRestoreWarning(512);
            }
        }
        if (sleepState.needRestore(1024)) {
            sleepState.setRestore(1024, false);
            boolean previousEnable8 = sleepState.getPreviousEnable(1024);
            if (previousEnable8 != (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "bluetooth_on", 0, -2) == 1)) {
                sb.append(" setBluetoothEnabled :" + previousEnable8);
                Settings.Secure.putInt(this.mContext.getContentResolver(), "bluetooth_on", 1);
                ((BluetoothManager) this.mContext.getSystemService("bluetooth")).getAdapter().enable();
                if (DBG_SLEEP) {
                    Log.d(TAG, "sleepmode quit bluetoothAdapter start and send bluetooth broadcast");
                }
                registerBluetoothStartReceiver();
            } else {
                sb.append("setBluetoothEnabled:warning");
                sleepState.setRestoreWarning(1024);
            }
        }
        sb.append("setTrafficControllerForSleepMode earthquakeWarningFlag is " + intForUser + ", INDEX_NETWORK needRestore is " + sleepState.needRestore(2048));
        if (intForUser == 1 && sleepState.needRestore(2048)) {
            sleepState.setRestore(2048, false);
            sb.append("Restore network for exit sleep mode.");
        }
        if (intForUser != 1 || this.mControlAlarmEw) {
            restrictAlarm(false);
        }
        logWithsLocal(sb.toString());
        broadcastSleepState(2);
        notifySleepState(2, reasonToString(sleepState.quitReason[sleepState.index]));
        setSleepModeStatus(this.mContext, false);
        unregisterRequestWakeReceiver();
    }

    private void restrictAlarm(boolean z2) {
        logWithsLocal("restrictAlarm is " + z2);
        Map<String, Integer> packageUidsMap = PowerKeeperManager.getInstance().getPackageUidsMap(this.mContext.getUserId());
        int i2 = 0;
        String[] strArr = new String[0];
        AlarmController alarmController = PowerKeeperManager.getInstance().getAlarmController();
        ArrayList arrayList = new ArrayList();
        while (true) {
            String[] strArr2 = mRestrictPkgs;
            if (i2 >= strArr2.length) {
                alarmController.setAppsAlarmState(arrayList);
                return;
            }
            Integer num = packageUidsMap.get(strArr2[i2]);
            if (num != null) {
                arrayList.add(z2 ? new AlarmPolicy(num.intValue(), strArr) : new AlarmPolicy(num.intValue(), (String[]) null));
            }
            i2++;
        }
    }

    public static void sendBroadcastOfSleepModeOff(String str) {
        Intent intent = new Intent(SLEEP_MODE_OFF_BROADCAST);
        intent.putExtra("offReason", str);
        PowerKeeperApplication.b().sendBroadcast(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendRestrictStateChanged(Context context, int i2, int i3, boolean z2) {
        Log.i(TAG, "sendRestrictStateChanged Change: " + i2 + " ---> " + i3);
        Intent intent = new Intent("android.xiaomi.bluetooth.WRITE_RESTRICT_STATE_CHANGED");
        intent.setPackage("com.android.bluetooth");
        intent.putExtra("android.bluetooth.adapter.extra.PREVIOUS_STATE", i2);
        intent.putExtra("android.bluetooth.adapter.extra.STATE", i3);
        intent.putExtra("android.xiaomi.bluetooth.BLUETOOTH_RESTRICT_STATE_WRITE", z2);
        intent.addFlags(67108864);
        intent.setFlags(BatteryStatsHelper.BatteryHistoryItemV23.STATE2_WIFI_ON_FLAG);
        context.sendBroadcastAsUser(intent, UserHandle.ALL);
    }

    private void setFodAodShowEnable(boolean z2) {
        Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "gxzw_icon_aod_show_enable", z2 ? 1 : 0, 0);
    }

    private static void setHideMode(Context context, String str) {
        Bundle bundle = new Bundle();
        bundle.putString(GlobalFeatureConfigure.USER_CONFIGURE_STATUS, str);
        GlobalFeatureConfigureHelper.updateGlobalConfigure(context, bundle);
    }

    private void setPickupWakeupEnable(boolean z2) {
        MiuiSettings.System.putBooleanForUser(this.mContext.getContentResolver(), "pick_up_gesture_wakeup_mode", z2, UserHandle.myUserId());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startDayClockTimer() {
        Calendar calendar = Calendar.getInstance();
        long timeInMillis = calendar.getTimeInMillis();
        calendar.set(11, this.mSleepState.END_TIME);
        calendar.set(14, 0);
        calendar.set(13, 0);
        calendar.set(12, 0);
        Intent intent = new Intent(ACTION_ENTER_DAY);
        long timeInMillis2 = (calendar.getTimeInMillis() - timeInMillis) + ((long) new Random().nextInt(RANDOM_DURATION));
        if (timeInMillis2 < 0) {
            timeInMillis2 += 86400000;
        }
        if (timeInMillis2 < 0) {
            logWithsLocal("startDayClockTimer error negative interval=" + timeInMillis2);
            return;
        }
        logWithsLocal("startDayClockTimer");
        intent.setPackage(this.mContext.getPackageName());
        ((AlarmManager) this.mContext.getSystemService("alarm")).setExactAndAllowWhileIdle(0, System.currentTimeMillis() + timeInMillis2, PendingIntent.getBroadcast(this.mContext, 0, intent, 67108864));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startListenChargingState() {
        PowerKeeperManager powerKeeperManager = PowerKeeperManager.getInstance();
        this.mSleepState.isCharging = powerKeeperManager.isCharging();
        SleepState sleepState = this.mSleepState;
        if (sleepState.isCharging) {
            sleepState.chargingTime = sleepState.nightTime;
        }
        if (DBG_SLEEP) {
            Slog.d(TAG, "startListenChargingState isCharging=" + this.mSleepState.isCharging);
        }
        powerKeeperManager.getEventsAggregator().registerForCharging(this.mHandler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean startListenDeviceStateIfNeeded(int i2) {
        logWithsLocal("startListenDeviceStateIfNeeded reason=" + reasonToString(i2) + this.mSleepState.nightToString());
        SleepState sleepState = this.mSleepState;
        int[] iArr = sleepState.state;
        int i3 = sleepState.index;
        if (iArr[i3] != 1 || sleepState.isUserOff || sleepState.isCloudOff || sleepState.foreignSim > 0 || sleepState.foreignNet > 0) {
            return false;
        }
        iArr[i3] = 2;
        sleepState.monitorTime = (int) (SystemClock.elapsedRealtime() / 1000);
        PowerKeeperManager powerKeeperManager = PowerKeeperManager.getInstance();
        EventsAggregator eventsAggregator = powerKeeperManager.getEventsAggregator();
        eventsAggregator.registerForPowerKeyPressed(this.mHandler, null);
        this.mSleepState.isScreenOn = powerKeeperManager.isScreenOn();
        eventsAggregator.registerForScreenOn(this.mHandler, null);
        eventsAggregator.registerForScreenOff(this.mHandler, null);
        eventsAggregator.registerForUserPresent(this.mHandler, null);
        registerCheckStationaryReceiver();
        SleepState sleepState2 = this.mSleepState;
        if (sleepState2.isCharging) {
            sleepState2.failedReason[sleepState2.index] = 2;
        } else if (sleepState2.isScreenOn) {
            sleepState2.failedReason[sleepState2.index] = 4;
        } else {
            long lastScreenOffTime = powerKeeperManager.getLastScreenOffTime();
            long jElapsedRealtime = lastScreenOffTime != 0 ? SystemClock.elapsedRealtime() - lastScreenOffTime : 0L;
            int i4 = CHECK_SCREEN_OFF_DURATION[0];
            if (jElapsedRealtime >= i4) {
                registerStepDetectorSensor();
                WhetstoneActivityManager.sleepModeRegisterGpsCallback();
                resetCheckStationaryAlarm(i2);
            } else {
                resetScreenOffAlarm(i2, ((long) i4) - jElapsedRealtime);
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startNightClockTimer() {
        Calendar calendar = Calendar.getInstance();
        long timeInMillis = calendar.getTimeInMillis();
        int i2 = this.mSleepState.BEGIN_TIME;
        if (i2 == 0) {
            calendar.set(11, 24);
        } else {
            calendar.set(11, i2);
        }
        calendar.set(14, 0);
        calendar.set(13, 0);
        calendar.set(12, 0);
        Intent intent = new Intent(ACTION_ENTER_NIGHT);
        long timeInMillis2 = calendar.getTimeInMillis() - timeInMillis;
        if (timeInMillis2 < 0) {
            timeInMillis2 += 86400000;
        }
        if (timeInMillis2 < 0) {
            logWithsLocal("startNightClockTimer error negative interval=" + timeInMillis2);
            return;
        }
        logWithsLocal("startNightClockTimer");
        intent.setPackage(this.mContext.getPackageName());
        ((AlarmManager) this.mContext.getSystemService("alarm")).setExactAndAllowWhileIdle(1, System.currentTimeMillis() + timeInMillis2, PendingIntent.getBroadcast(this.mContext, 0, intent, 67108864));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String stateToString(int i2) {
        if (i2 == 1) {
            return "in_night";
        }
        if (i2 == 2) {
            return "monitoring";
        }
        if (i2 != 3) {
            return i2 != 4 ? "default" : "awake";
        }
        return "asleep";
    }

    private void stopListenChargingState() {
        if (DBG_SLEEP) {
            Slog.d(TAG, "stopListenChargingState");
        }
        PowerKeeperManager.getInstance().getEventsAggregator().unregisterForCharging(this.mHandler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopSatellite() {
        try {
            TelephonyManagerEx telephonyManagerEx = TelephonyManagerEx.getDefault();
            TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
            if (telephonyManagerEx == null) {
                Log.d(TAG, "telephonyManagerEx is null");
                return;
            }
            if (!telephonyManagerEx.isSupportSatelliteByDevice()) {
                logWithsLocal("device don't support Satellite");
                return;
            }
            if (!telephonyManagerEx.isSupportSatelliteByCarrier()) {
                logWithsLocal("sim don't support Satellite");
                return;
            }
            if (!telephonyManagerEx.isSatelliteEnabled()) {
                logWithsLocal("user don't open satellite!!!");
                return;
            }
            this.mServiceStateListener = new SatelliteServiceStateListener();
            if (telephonyManager != null) {
                logWithsLocal("registerTelephonyCallback!!!");
                telephonyManager.registerTelephonyCallback(this.mContext.getMainExecutor(), this.mServiceStateListener);
            }
        } catch (Exception e2) {
            throw new RuntimeException(e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:21:0x0088  */
    public void tryToQuitSleep(int i2) {
        SleepState sleepState = this.mSleepState;
        int[] iArr = sleepState.state;
        int i3 = sleepState.index;
        int i4 = iArr[i3];
        boolean z2 = true;
        if (i4 == 2) {
            boolean zCancelScreenOffAlarm = cancelScreenOffAlarm(i2);
            if (cancelCheckStationaryAlarm(i2)) {
                zCancelScreenOffAlarm = true;
            }
            unregisterStepDetectorSensor();
            WhetstoneActivityManager.sleepModeUnregisterGpsCallback();
            SleepState sleepState2 = this.mSleepState;
            if (sleepState2.isCharging) {
                sleepState2.failedReason[sleepState2.index] = 2;
            } else {
                sleepState2.failedReason[sleepState2.index] = i2;
            }
            if (i2 == 64 || i2 == 128) {
                unregisterCheckStationaryReceiver();
                stopListenDeviceState(i2);
                zCancelScreenOffAlarm = true;
            }
            if (i2 == 32) {
                int i5 = Calendar.getInstance().get(11);
                if (DBG_SLEEP) {
                    Log.d(TAG, "monitoring currentHour " + i5 + "  tryToQuitSleep: " + reasonToString(i2));
                }
                if (i5 < 5 || i5 >= 23) {
                    z2 = zCancelScreenOffAlarm;
                } else {
                    unregisterCheckStationaryReceiver();
                    stopListenDeviceState(i2);
                    unregisterSleepSwitchObserver();
                }
            } else {
                z2 = zCancelScreenOffAlarm;
            }
            if (z2) {
                StringBuilder sb = new StringBuilder();
                sb.append("tryToQuitSleep index=");
                sb.append(this.mSleepState.index);
                sb.append(" state=");
                SleepState sleepState3 = this.mSleepState;
                sb.append(stateToString(sleepState3.state[sleepState3.index]));
                sb.append(" reason=");
                SleepState sleepState4 = this.mSleepState;
                sb.append(reasonToString(i2 | sleepState4.failedReason[sleepState4.index]));
                logWithsLocal(sb.toString());
                return;
            }
            return;
        }
        if (i4 != 3) {
            if (i2 == 64 || i2 == 128 || i2 == 32) {
                unregisterAll(i2);
                return;
            }
            return;
        }
        iArr[i3] = 4;
        int i6 = sleepState.sleepBatteryLevel;
        if (i6 > 0) {
            sleepState.sleepTotalConsumeBattery += i6 - Utils.getCurBatteryLevel(this.mContext);
            this.mSleepState.sleepBatteryLevel = 0;
        }
        SleepState sleepState5 = this.mSleepState;
        int[] iArr2 = sleepState5.quitReason;
        int i7 = sleepState5.index;
        iArr2[i7] = i2;
        sleepState5.wakeTime[i7] = (int) (SystemClock.elapsedRealtime() / 1000);
        restoreSleepConfig(this.mSleepState);
        writeToDb(this.mSleepState);
        CheckSleepPower checkSleepPower = this.mCheckSleepPower;
        if (checkSleepPower != null) {
            checkSleepPower.cancelCheckPowerAlarm();
        }
        SleepState sleepState6 = this.mSleepState;
        int i8 = sleepState6.index;
        if (i8 + 1 >= sleepState6.MAX_SLEEP_TIMES) {
            unregisterAll(LogType.LOG_TYPE_SAMPLING_CPUFREQ);
            return;
        }
        if (i2 != 32) {
            int i9 = i8 + 1;
            sleepState6.index = i9;
            if (sleepState6.isUserOff || sleepState6.isCloudOff || sleepState6.foreignSim > 0 || sleepState6.foreignNet > 0) {
                sleepState6.state[i9] = 1;
                return;
            } else {
                sleepState6.state[i9] = 2;
                return;
            }
        }
        sleepState6.index = i8 + 1;
        int i10 = Calendar.getInstance().get(11);
        if (DBG_SLEEP) {
            Log.d(TAG, "sleeping currentHour " + i10 + "  tryToQuitSleep: " + reasonToString(i2));
        }
        if (i10 < 5 || i10 >= 23) {
            return;
        }
        unregisterAll(32);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unregisterAll(int i2) {
        cancelScreenOffAlarm(i2);
        cancelCheckStationaryAlarm(i2);
        unregisterStepDetectorSensor();
        WhetstoneActivityManager.sleepModeUnregisterGpsCallback();
        if (i2 == 64 || i2 == 128 || i2 == 32 || i2 == 33554432 || i2 == 512 || i2 == 16 || i2 == 262144) {
            unregisterCheckStationaryReceiver();
            stopListenDeviceState(i2);
        }
        if (i2 == 32 || i2 == 33554432 || i2 == 512 || i2 == 16 || i2 == 262144) {
            unregisterSleepSwitchObserver();
        }
        if (i2 == 16 || i2 == 262144) {
            stopListenChargingState();
        }
        if (i2 == 262144) {
            cancelClockTimer();
            ClockReceiver clockReceiver = this.mClockReceiver;
            if (clockReceiver != null) {
                this.mContext.unregisterReceiver(clockReceiver);
                this.mClockReceiver = null;
            }
            unregisterRequestWakeReceiver();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unregisterBluetoothStartReceiver() {
        if (this.mBluetoothStartReceiver == null) {
            return;
        }
        if (DBG_SLEEP) {
            Slog.d(TAG, "unregisterBluetoothStartReceiver");
        }
        this.mContext.unregisterReceiver(this.mBluetoothStartReceiver);
        this.mBluetoothStartReceiver = null;
    }

    private void unregisterCheckStationaryReceiver() {
        if (this.mCheckStationaryReceiver == null) {
            return;
        }
        if (DBG_SLEEP) {
            Slog.d(TAG, "unregisterCheckStationaryReceiver");
        }
        this.mContext.unregisterReceiver(this.mCheckStationaryReceiver);
        this.mCheckStationaryReceiver = null;
    }

    private void unregisterRequestWakeReceiver() {
        if (this.mRequestWakeReceiver == null) {
            return;
        }
        if (DBG_SLEEP) {
            Slog.d(TAG, "unregisterRequestWakeReceiver");
        }
        this.mContext.unregisterReceiver(this.mRequestWakeReceiver);
        this.mRequestWakeReceiver = null;
    }

    private void unregisterSleepSwitchObserver() {
        if (this.mSleepSwitchObserver == null) {
            return;
        }
        if (DBG_SLEEP) {
            Slog.d(TAG, "unregisterSleepSwitchObserver");
        }
        this.mContext.getContentResolverForUser(UserHandle.SYSTEM).unregisterContentObserver(this.mSleepSwitchObserver);
        this.mSleepSwitchObserver = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unregisterStepDetectorSensor() {
        if (this.mSensorEventListener == null) {
            return;
        }
        SensorManager sensorManager = (SensorManager) this.mContext.getSystemService(HideModeStateMachineConfig.STRATEGY_SENSOR);
        if (this.mStepDetector != null) {
            if (DBG_SLEEP) {
                Slog.d(TAG, "unregisterStepDetectorSensor mStepDetector");
            }
            sensorManager.unregisterListener(this.mSensorEventListener, this.mStepDetector);
            this.mStepDetector = null;
        }
        if (this.mLightSensor != null) {
            if (DBG_SLEEP) {
                Slog.d(TAG, "unregisterStepDetectorSensor mLightSensor");
            }
            sensorManager.unregisterListener(this.mSensorEventListener, this.mLightSensor);
            this.mLightSensor = null;
        }
        this.mSensorEventListener = null;
    }

    private void whitelistCloudAppsForEarthquakeWarning() {
        for (String str : getCloudAppsNetWhiteListOfEarthquakeWarn()) {
            int uidByPackageName = PackageUtil.getUidByPackageName(this.mContext, str);
            Log.i(TAG, "whitelisting network for package " + str + " with UID " + uidByPackageName);
            if (uidByPackageName >= 0) {
                onSleepModeWhitelistChange(uidByPackageName, true);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void writeToDb(SleepState sleepState) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(YellowPageContract.Permission.STATE, sleepState.state[sleepState.index]).put("oldFeatureStatus", sleepState.oldFeatureStatus).put("previous", sleepState.previous).put("restore", sleepState.restore).put("previousNotification", sleepState.previousNotification);
        } catch (JSONException e2) {
            Slog.w(TAG, "writeToDb exception", e2);
        }
        SimpleSettings.Misc.putString(this.mContext, KEY_RESOURCE_STATE, jSONObject.toString());
    }

    @Override // com.miui.powerkeeper.statemachine.SleepModeControllerNew
    public void dispose() {
        unregisterAll(LogType.LOG_TYPE_SAMPLING_MOBILE_DATA);
    }

    @Override // com.miui.powerkeeper.statemachine.SleepModeControllerNew
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (strArr == null || strArr.length <= 0) {
            printWriter.println("dump of PhoneSleepModeController:");
            this.mLocalLog.dump(fileDescriptor, printWriter, strArr);
            printWriter.println("SleepState=" + this.mSleepState);
            printWriter.println(KEY_SLEEP_REBOOT_TIME + '=' + SimpleSettings.Misc.getString(this.mContext, KEY_SLEEP_REBOOT_TIME, ""));
            StringBuilder sb = new StringBuilder(256);
            if (this.mCheckStationaryReceiver != null) {
                sb.append(" mCheckStationaryReceiver");
            }
            if (this.mRequestWakeReceiver != null) {
                sb.append(" mRequestWakeReceiver");
            }
            if (this.mSleepSwitchObserver != null) {
                sb.append(" mSleepSwitchObserver");
            }
            if (!WhetstoneActivityManager.isEmptyOfGpsCallback()) {
                sb.append(" mGpsCallback");
            }
            if (this.mSensorEventListener != null) {
                sb.append(" mSensorEventListener");
            }
            if (this.mStepDetector != null) {
                sb.append(" mStepDetector");
            }
            if (this.mLightSensor != null) {
                sb.append(" mLightSensor");
            }
            if (sb.length() > 0) {
                printWriter.println("Not empty pointers:" + ((Object) sb));
                return;
            }
            return;
        }
        byte b2 = 0;
        String str = strArr[0];
        switch (str.hashCode()) {
            case 1492:
                b2 = !str.equals("-a") ? (byte) -1 : (byte) 5;
                break;
            case 46476:
                b2 = !str.equals("-ed") ? (byte) -1 : (byte) 1;
                break;
            case 46486:
                if (!str.equals("-en")) {
                    b2 = -1;
                }
                break;
            case 46522:
                b2 = !str.equals("-fs") ? (byte) -1 : (byte) 4;
                break;
            case 46909:
                b2 = !str.equals("-sc") ? (byte) -1 : (byte) 3;
                break;
            case 46926:
                b2 = !str.equals("-st") ? (byte) -1 : (byte) 2;
                break;
            default:
                b2 = -1;
                break;
        }
        if (b2 == 0) {
            SleepHandler sleepHandler = this.mHandler;
            sleepHandler.sendMessageAtFrontOfQueue(sleepHandler.obtainMessage(6, LogType.LOG_TYPE_SAMPLING_SIGNAL_STRENGTH, -1));
            printWriter.println("Send message MSG_ENTER_NIGHT");
            return;
        }
        if (b2 == 1) {
            SleepHandler sleepHandler2 = this.mHandler;
            sleepHandler2.sendMessageAtFrontOfQueue(sleepHandler2.obtainMessage(7));
            printWriter.println("Send message MSG_ENTER_DAY");
            return;
        }
        if (b2 == 2) {
            this.mHandler.sendEmptyMessage(8);
            printWriter.println("Send message MSG_SCREEN_OFF_TIMEOUT");
            return;
        }
        if (b2 == 3) {
            this.mHandler.sendEmptyMessage(9);
            printWriter.println("Send message MSG_SLEEP_CHECK");
            return;
        }
        if (b2 == 4) {
            this.mForceSleep = !this.mForceSleep;
            printWriter.println("Force sleep status changed. mForceSleep=" + this.mForceSleep);
            return;
        }
        if (b2 != 5) {
            return;
        }
        printWriter.println("SleepState=" + this.mSleepState);
    }

    @Override // com.miui.powerkeeper.statemachine.SleepModeControllerNew
    public boolean getSleepModeForEarthquakeWarnFlag() {
        b.a aVarP = b.a.p();
        int i2 = b.b.K;
        JSONObject jSONObjectL = aVarP.l(i2);
        if (!aVarP.s(i2) || jSONObjectL == null) {
            return true;
        }
        boolean zOptBoolean = jSONObjectL.optBoolean("sleepModeforEW", true);
        Log.d(TAG, "getSleepModeForEarthquakeWarnFlag functionSwitch is " + zOptBoolean);
        return zOptBoolean;
    }

    public boolean isBlueToothConnected(Context context) {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothManager bluetoothManager = (BluetoothManager) this.mContext.getSystemService("bluetooth");
        int iMax = Math.max(bluetoothManager.getDevicesMatchingConnectionStates(8, new int[]{2, 1}).size(), bluetoothManager.getDevicesMatchingConnectionStates(7, new int[]{2, 1}).size());
        if (defaultAdapter != null && defaultAdapter.isEnabled()) {
            Iterator<BluetoothDevice> it = defaultAdapter.getBondedDevices().iterator();
            boolean zIsConnected = false;
            while (it.hasNext() && !(zIsConnected = it.next().isConnected())) {
            }
            Slog.d(TAG, "the common bluetooth device state is:" + zIsConnected);
            if (zIsConnected || iMax > 0) {
                Slog.d(TAG, "low energy bluetooth devices list size is " + iMax);
                return true;
            }
        }
        if (!this.oppState) {
            return false;
        }
        Slog.d(TAG, "the OPP transpormission state is:" + this.oppState);
        return true;
    }

    @Override // com.miui.powerkeeper.statemachine.SleepModeControllerNew
    public boolean isInSleep() {
        PhoneSleepModeController phoneSleepModeController = sInstance;
        if (phoneSleepModeController == null) {
            return false;
        }
        SleepState sleepState = phoneSleepModeController.mSleepState;
        return sleepState.state[sleepState.index] == 3;
    }

    @Override // com.miui.powerkeeper.statemachine.SleepModeControllerNew
    public void logWithsLocal(String str) {
        Slog.i(TAG, str);
        this.mLocalLog.log(str);
    }

    @Override // com.miui.powerkeeper.statemachine.SleepModeControllerNew
    public void registerSleepListener(PowerKeeperInterface.a0 a0Var) {
        if (a0Var != null) {
            this.listeners.add(a0Var);
        }
    }

    public void setSleepModeStatus(Context context, boolean z2) {
        String hideMode = getHideMode(context);
        if (TextUtils.isEmpty(hideMode)) {
            Log.e(TAG, "hide mode status null");
            return;
        }
        if (z2) {
            setHideMode(context, GlobalFeatureConfigure.USER_CONFIGURE_LEVEL_SLEEP);
            SleepState sleepState = this.mSleepState;
            sleepState.oldFeatureStatus = hideMode;
            writeToDb(sleepState);
            return;
        }
        SleepState sleepState2 = this.mSleepState;
        String str = sleepState2.oldFeatureStatus;
        sleepState2.oldFeatureStatus = GlobalFeatureConfigure.USER_CONFIGURE_LEVEL_SLEEP;
        writeToDb(sleepState2);
        setHideMode(context, str);
    }

    public void stopListenDeviceState(int i2) {
        if (DBG_SLEEP) {
            Slog.d(TAG, "stopListenDeviceState stopReason=" + reasonToString(i2));
        }
        EventsAggregator eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
        eventsAggregator.unregisterForPowerKeyPressed(this.mHandler);
        eventsAggregator.unregisterForScreenOn(this.mHandler);
        eventsAggregator.unregisterForScreenOff(this.mHandler);
        eventsAggregator.unregisterForUserPresent(this.mHandler);
    }

    @Override // com.miui.powerkeeper.statemachine.SleepModeControllerNew
    public void unregisterSleepListener(PowerKeeperInterface.a0 a0Var) {
        if (a0Var != null) {
            this.listeners.remove(a0Var);
        }
    }

    public void updateSleepStateGpsStopTime(int i2) {
        Slog.d(TAG, " updateSleepStateGpsStopTime = " + i2);
        this.mSleepState.gpsStopTime = i2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void uploadSleepStatistic(SleepState sleepState) {
    }
}
