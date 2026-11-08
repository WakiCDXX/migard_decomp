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
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.SoftApInfo;
import android.net.wifi.WifiClient;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UEventObserver;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.LocalLog;
import android.util.Log;
import android.util.Slog;
import com.miui.powerkeeper.PowerKeeperApplication;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.appcontrol.HideModeStateMachineConfig;
import com.miui.powerkeeper.batterylife.systeminfo.BSHelper;
import com.miui.powerkeeper.cloudcontrol.CloudFunctionConfig;
import com.miui.powerkeeper.controller.AlarmController;
import com.miui.powerkeeper.customerpower.CustomerPowerManager;
import com.miui.powerkeeper.dfs.DfsUtils;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.control.MessageType;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.powerchecker.BatteryStatsHelper;
import com.miui.powerkeeper.powerchecker.NightStandbyRecord;
import com.miui.powerkeeper.powerchecker.PowerCheckerCloudConfigure;
import com.miui.powerkeeper.provider.GlobalFeatureConfigure;
import com.miui.powerkeeper.provider.GlobalFeatureConfigureHelper;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.siming.dasi.MIY;
import com.miui.powerkeeper.tracker.TrackerManager;
import com.miui.powerkeeper.utils.CommonAdapter;
import com.miui.powerkeeper.utils.Constant;
import com.miui.powerkeeper.utils.InternationalAdapter;
import com.miui.powerkeeper.utils.NetUtils;
import com.miui.powerkeeper.utils.PowerLog;
import com.miui.powerkeeper.utils.Utils;
import com.miui.powerkeeper.utils.WifiApAdapter;
import com.miui.whetstone.AlarmPolicy;
import com.miui.whetstone.WhetstoneActivityManager;
import com.xiaomi.channel.commonutils.misc.DateTimeHelper;
import com.xiaomi.mipush.sdk.Constants;
import com.xiaomi.push.service.clientReport.ReportConstants;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import miui.date.DateUtils;
import miui.os.Build;
import miui.process.ProcessConfig;
import miui.process.ProcessManager;
import miui.util.HardwareInfo;
import miui.yellowpage.YellowPageContract;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class PadSleepModeController extends SleepModeControllerNew {
    private static final String ACTION_AMD_SENSOR_TIMEOUT_DEEP = "com.miui.powerkeeper.amd_stop_timeout.pad";
    private static final String ACTION_BLUETOOTH_OPP_INBOUND_END = "com.android.bluetooth.opp.BLUETOOTH_OPP_INBOUND_END";
    private static final String ACTION_BLUETOOTH_OPP_INBOUND_START = "com.android.bluetooth.opp.BLUETOOTH_OPP_INBOUND_START";
    private static final String ACTION_BLUETOOTH_OPP_OUTBOUND_END = "com.android.bluetooth.opp.BLUETOOTH_OPP_OUTBOUND_END";
    private static final String ACTION_BLUETOOTH_OPP_OUTBOUND_START = "com.android.bluetooth.opp.BLUETOOTH_OPP_OUTBOUND_START";
    private static final String ACTION_CHECK_STATIONARY = "com.miui.powerkeeper.check_stationary";
    private static final String ACTION_CHECK_STATIONARY_DEEP = "com.miui.powerkeeper.check_stationary.pad";
    private static final String ACTION_DEAD_SLEEP_CONTROL = "com.miui.powerkeeper.deadsleep.pad";
    private static final String ACTION_ENTER_DAY = "com.miui.powerkeeper.enter_day";
    private static final String ACTION_ENTER_NIGHT = "com.miui.powerkeeper.enter_night";
    private static final String ACTION_SCREEN_OFF_TIMEOUT = "com.miui.powerkeeper.screen_off_timeout";
    private static final int[] CHECK_SCREEN_OFF_DURATION;
    private static final int CHECK_SCREEN_OFF_DURATION_FOR_DEEP_SLEEP;
    private static final int CHECK_STATIONARY_DURATION;
    private static final int CHECK_STATIONARY_DURATION_FOR_DEEP_SLEEP;
    private static final boolean DBG_COMPRESS_TIME;
    private static final boolean DBG_SLEEP;
    private static final String DEEP_SLEEP_MODE_PARAMS = "deep_sleep_mode_cloud_params";
    private static final int DEFAULT_SLEEP_BEGIN_TIME = 23;
    private static final int DEFAULT_SLEEP_END_TIME = 7;
    private static final int DEFAULT_SLEEP_LIGHT_LUX_THRESHOLD = 5;
    private static final int DEFAULT_SLEEP_MAX_TIMES;
    private static final int DEFAULT_STATIONARY_TIME_THRESHOLD;
    private static final String EVENT_RECORD_SHIPMODE_DATA = "record_shipmode_data";
    private static final String EVENT_RECORD_SLEEP_DATA = "record_deep_sleep_data";
    private static final String FUCSWITCH = "fucSwitch";
    public static final String FUNC_SLEEP_REBOOT = "sleep_reboot";
    private static final int GPS_TIME_THRESHOLD = 5;
    private static final int HALL_CLOSE = 0;
    private static final int HALL_FAR = 1;
    private static final String KEYBOARD = "keyboard";
    private static String KEY_RESOURCE_STATE = null;
    private static String KEY_SLEEP_REBOOT_TIME = null;
    private static final int MSG_AMD_SENSOR_CHANGE = 15;
    private static final int MSG_AMD_SENSOR_TIMEOUT_DEEP = 13;
    private static final int MSG_DESK_CLOCK_REQUEST_QUIT = 10;
    private static final int MSG_ENTER_DAY = 7;
    private static final int MSG_ENTER_NIGHT = 6;
    private static final int MSG_PEN_PICK_UP = 16;
    private static final int MSG_SCREEN_OFF_TIMEOUT = 8;
    private static final int MSG_SLEEP_CHECK = 9;
    private static final int MSG_SLEEP_CLOSE_GPS = 11;
    private static final int MSG_SLEEP_CLOSE_WIFIAP = 12;
    private static final int MSG_SLEEP_DEEP_CHECK = 14;
    private static final String PEN = "pen";
    private static final String POWER_SUPPLY_PEN_HALL3_EVENT = "POWER_SUPPLY_PEN_HALL3";
    private static final String POWER_SUPPLY_PEN_HALL4_EVENT = "POWER_SUPPLY_PEN_HALL4";
    private static final int RANDOM_DURATION;
    private static final String SETTINGS_SLEEP_MODE_SWITCH_ATTR = "sec_pc_config_scenario_policies_open";
    private static final String SLEEP_MODE_OFF_BROADCAST = "com.miui.powerkeeper.SLEEP_MODE_OFF_REASON";
    private static final String TAG = "power.sleep.pad";
    public static final String USER_CLOSE_SLEEP_MODE_BY_BUTTON = "userCloseSleepModeByUIButton";
    private static int mNumConnectedDevices;
    private static String[] mRestrictPkgs;
    private static PadSleepModeController sInstance;
    private List<PowerKeeperInterface.a0> listeners;
    private Sensor mAmdSensor;
    private AmdSensorListener mAmdSensorListener;
    PendingIntent mAmdSensorStopPiDeepSleep;
    private long mAmdStopTime;
    private BluetoothStartReceiver mBluetoothStartReceiver;
    private CheckDeepStationaryReceiver mCheckDeepStationaryReceiver;
    private CheckSleepPower mCheckSleepPower;
    PendingIntent mCheckStationaryPi;
    PendingIntent mCheckStationaryPiDeepSleep;
    private CheckStationaryReceiver mCheckStationaryReceiver;
    private ClockReceiver mClockReceiver;
    private Context mContext;
    private DeepSleepData mDeepSleepData;
    private List<String> mDeepSleepDevice;
    private List<String> mEnhanceSleepDevice;
    private String mGpsPkg;
    private int mGpsStartTime;
    private SleepHandler mHandler;
    private HotsSpotSoftApCallback mHotsSpotSoftApCallback;
    private boolean mIsEnhanceSleep;
    private boolean mIsSupportDeadSleep;
    private Sensor mLightSensor;
    private LightSensorListener mLightSensorListener;
    private LocalLog mLocalLog;
    private boolean mNetCheck;
    private int mPenHallStatus;
    private final UEventObserver mPenPoseObserver;
    private RequestWakeReceiver mRequestWakeReceiver;
    PendingIntent mScreeenOffPi;
    private Set<DeepSleepData> mSetData;
    private SleepState mSleepState;
    private SleepSwitchObserver mSleepSwitchObserver;
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
    private final int REASON_AMD_NOT_STATIONARY = 1024;
    private final int REASON_GPS_NOT_STATIONARY = 2048;
    private final int REASON_WIFI_HOTSPOT_CONNECTED = 4096;
    private final int REASON_AMD_SENSOR_TIME_OUT = DateUtils.FORMAT_ABBREV_WEEKDAY;
    private final int REASON_AMD_SENSOR_CHANGE = DateUtils.FORMAT_ABBREV_TIME;
    private final int REASON_PEN_PICK_UP = 32768;
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
    private final int REASON_ENTER_DEEP_SLEEP = 67108864;
    private final int REASON_EXIT_DEEP_SLEEP = 134217728;
    private final int REASON_NOT_SLEEP_STATE = BatteryStatsHelper.BatteryHistoryItemV23.STATE2_WIFI_ON_FLAG;
    private final int REASON_DEEP_CLOUD_SWITCH_OFF = 536870912;
    private final int REASON_DEEP_CLOUD_SWITCH_ON = LogType.LOG_TYPE_F_SAMPLING_CURRENT;
    private final int REASON_AUDIO_ACTIVE_BG = Integer.MIN_VALUE;
    private final int REJECT_REASON_USER_CLOSE = 1;
    private final int REJECT_REASON_CLOUD_CLOSE = 2;
    private final int REJECT_REASON_FOREIGN_NETWORK = 4;
    private final int REJECT_REASON_ONE_FOREIGN_SIM = 8;
    private final int REJECT_REASON_TWO_FOREIGN_SIM = 16;
    private final int REJECT_REASON_DEEP_SLEEP_CLOUD_CLOSE = 32;
    private final int STATE_DEFAULT = 0;
    private final int STATE_IN_NIGHT = 1;
    private final int STATE_MONITORING = 2;
    private final int STATE_ASLEEP = 3;
    private final int STATE_AWAKE = 4;
    private final int STATE_DEEP_SLEEP = 5;
    private final int WAKELOCK_DEFAULT_TIMEOUT = 200;
    private final int PENLOCK_DEFAULT_TIMEOUT = ReportConstants.AWAKE_TYPE;
    private final int SENSOR_TYPE_AMD = 33171211;

    /* JADX INFO: renamed from: com.miui.powerkeeper.statemachine.PadSleepModeController$3, reason: invalid class name */
    class AnonymousClass3 implements Runnable {
        final /* synthetic */ boolean val$previous;

        AnonymousClass3(boolean z2) {
            this.val$previous = z2;
        }

        @Override // java.lang.Runnable
        public void run() {
            boolean z2 = Settings.Global.getInt(PadSleepModeController.this.mContext.getContentResolver(), "wifi_scan_always_enabled", 0) == 1;
            PadSleepModeController.this.logWithsLocal("run setWlanScanEnable:" + this.val$previous + " now=" + z2);
            if (this.val$previous != z2) {
                Settings.Global.putInt(PadSleepModeController.this.mContext.getContentResolver(), "wifi_scan_always_enabled", this.val$previous ? 1 : 0);
            }
        }
    }

    private class AmdSensorListener implements SensorEventListener {
        @Override // android.hardware.SensorEventListener
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == 33171211 && sensorEvent.values[0] == 1.0f) {
                PadSleepModeController.this.mAmdStopTime = (int) (sensorEvent.timestamp / DfsUtils.SECOND_TO_MICROS);
                PadSleepModeController.this.mSleepState.amdStopTime = (int) (sensorEvent.timestamp / 1000000000);
                PadSleepModeController.this.mHandler.sendMessage(PadSleepModeController.this.mHandler.obtainMessage(15, DateUtils.FORMAT_ABBREV_TIME, -1));
            }
        }

        private AmdSensorListener() {
        }

        @Override // android.hardware.SensorEventListener
        public void onAccuracyChanged(Sensor sensor, int i2) {
        }
    }

    private class BluetoothStartReceiver extends BroadcastReceiver {
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.bluetooth.adapter.action.STATE_CHANGED".equals(intent.getAction())) {
                int i2 = Calendar.getInstance().get(11);
                boolean zIsEnabled = ((BluetoothManager) PadSleepModeController.this.mContext.getSystemService("bluetooth")).getAdapter().isEnabled();
                if (PadSleepModeController.DBG_SLEEP) {
                    Log.d(PadSleepModeController.TAG, "bluetooth state is BluetoothAdapter.STATE_ON = " + zIsEnabled);
                }
                int i3 = Settings.Global.getInt(PadSleepModeController.this.mContext.getContentResolver(), "enable_bluetooth_restricte", 0);
                SleepState sleepState = PadSleepModeController.this.mSleepState;
                Objects.requireNonNull(PadSleepModeController.this.mSleepState);
                boolean previousEnable = sleepState.getPreviousEnable(2048);
                if ((i2 >= 23 || i2 < 5) && zIsEnabled && i3 == 1 && previousEnable) {
                    PadSleepModeController padSleepModeController = PadSleepModeController.this;
                    padSleepModeController.sendRestrictStateChanged(padSleepModeController.mContext, 12, 10, true);
                    if (PadSleepModeController.DBG_SLEEP) {
                        Log.d(PadSleepModeController.TAG, "the bt state is halfOn and hour of day = " + i2);
                    }
                    PadSleepModeController.this.unregisterBluetoothStartReceiver();
                }
            }
        }

        private BluetoothStartReceiver() {
        }
    }

    private class CheckDeepStationaryReceiver extends BroadcastReceiver {
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent == null ? null : intent.getAction();
            if (PadSleepModeController.ACTION_AMD_SENSOR_TIMEOUT_DEEP.equals(action)) {
                PadSleepModeController.this.logWithsLocal("receive ACTION_AMD_SENSOR_TIMEOUT_DEEP");
                PadSleepModeController.this.mHandler.sendEmptyMessage(13);
            } else if (PadSleepModeController.ACTION_CHECK_STATIONARY_DEEP.equals(action)) {
                PadSleepModeController.this.logWithsLocal("receive ACTION_CHECK_STATIONARY_DEEP");
                PadSleepModeController.this.mHandler.sendEmptyMessage(14);
            }
        }

        private CheckDeepStationaryReceiver() {
        }
    }

    private class CheckSleepPower extends BroadcastReceiver {
        private final String ACTION_SLEEP_CHECK_POWER;
        private final int DEFAULT_DELAY;
        private PendingIntent mCheckPowerPi;

        public CheckSleepPower() {
            this.DEFAULT_DELAY = PadSleepModeController.DBG_COMPRESS_TIME ? PowerCheckerCloudConfigure.DEFAULT_WAKEUP_ALARM_INTERVAL : CustomerPowerManager.SYSTEM_BAD_SIGNAL_TIME;
            this.ACTION_SLEEP_CHECK_POWER = "com.miui.powerkeeper.sleep_check_power";
            if (PadSleepModeController.DBG_SLEEP) {
                Slog.d(PadSleepModeController.TAG, "CheckSleepPower construct");
            }
            PadSleepModeController.this.mContext.registerReceiver(this, new IntentFilter("com.miui.powerkeeper.sleep_check_power"), null, PadSleepModeController.this.mHandler, 2);
        }

        public boolean cancelCheckPowerAlarm() {
            if (this.mCheckPowerPi == null) {
                return false;
            }
            if (PadSleepModeController.DBG_SLEEP) {
                Slog.d(PadSleepModeController.TAG, "cancelCheckPowerAlarm reason=");
            }
            ((AlarmManager) PadSleepModeController.this.mContext.getSystemService("alarm")).cancel(this.mCheckPowerPi);
            this.mCheckPowerPi = null;
            return true;
        }

        public void dispose() {
            if (PadSleepModeController.DBG_SLEEP) {
                Slog.d(PadSleepModeController.TAG, "CheckSleepPower dispose");
            }
            PadSleepModeController.this.mContext.unregisterReceiver(this);
            cancelCheckPowerAlarm();
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("com.miui.powerkeeper.sleep_check_power".equals(intent == null ? null : intent.getAction())) {
                if (PadSleepModeController.this.mSleepState.state[PadSleepModeController.this.mSleepState.index] != 3) {
                    PadSleepModeController.this.logWithsLocal("CheckSleepPower abandon");
                    return;
                }
                int i2 = Utils.toInt(CloudFunctionConfig.getFunctionParameters(PadSleepModeController.this.mContext, "sleep_reboot", ""), -1);
                if (!PadSleepModeController.DBG_COMPRESS_TIME && i2 <= 0) {
                    PadSleepModeController.this.logWithsLocal("CheckSleepPower invalid threshold");
                    return;
                }
                int curBatteryLevel = Utils.getCurBatteryLevel(PadSleepModeController.this.mContext);
                if (PadSleepModeController.this.mSleepState.sleepBatteryLevel - curBatteryLevel >= i2) {
                    SimpleSettings.Misc.putString(PadSleepModeController.this.mContext, PadSleepModeController.KEY_SLEEP_REBOOT_TIME, Utils.getCurrentFormatTime());
                    ((PowerManager) PadSleepModeController.this.mContext.getSystemService(NightStandbyRecord.KEY_POWER)).reboot("quiescent");
                    return;
                }
                PadSleepModeController.this.logWithsLocal("CheckSleepPower ignore threshold=" + i2 + " currentLevel=" + curBatteryLevel + " sleepLevel=" + PadSleepModeController.this.mSleepState.sleepBatteryLevel);
            }
        }

        public void resetCheckPowerAlarm() {
            AlarmManager alarmManager = (AlarmManager) PadSleepModeController.this.mContext.getSystemService("alarm");
            PendingIntent pendingIntent = this.mCheckPowerPi;
            if (pendingIntent == null) {
                this.mCheckPowerPi = PendingIntent.getBroadcast(PadSleepModeController.this.mContext, 0, new Intent("com.miui.powerkeeper.sleep_check_power"), 67108864);
                PadSleepModeController.this.logWithsLocal("resetCheckPowerAlarm");
            } else {
                alarmManager.cancel(pendingIntent);
                PadSleepModeController.this.logWithsLocal("resetCheckPowerAlarm with cancel");
            }
            alarmManager.set(3, SystemClock.elapsedRealtime() + ((long) this.DEFAULT_DELAY), this.mCheckPowerPi);
        }
    }

    private class CheckStationaryReceiver extends BroadcastReceiver {
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent == null ? null : intent.getAction();
            if (PadSleepModeController.ACTION_SCREEN_OFF_TIMEOUT.equals(action)) {
                PadSleepModeController.this.logWithsLocal("receive ACTION_SCREEN_OFF_TIMEOUT");
                PadSleepModeController.this.mHandler.sendEmptyMessage(8);
            } else if (PadSleepModeController.ACTION_CHECK_STATIONARY.equals(action)) {
                PadSleepModeController.this.logWithsLocal("receive ACTION_CHECK_STATIONARY");
                PadSleepModeController.this.mHandler.sendEmptyMessage(9);
            }
        }

        private CheckStationaryReceiver() {
        }
    }

    private class ClockReceiver extends BroadcastReceiver {
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent == null ? null : intent.getAction();
            if (PadSleepModeController.ACTION_ENTER_NIGHT.equals(action)) {
                PadSleepModeController.this.mHandler.sendMessageAtFrontOfQueue(PadSleepModeController.this.mHandler.obtainMessage(6, LogType.LOG_TYPE_SAMPLING_SIGNAL_STRENGTH, -1));
                return;
            }
            if (PadSleepModeController.ACTION_ENTER_DAY.equals(action)) {
                PadSleepModeController.this.logWithsLocal("receive ACTION_ENTER_DAY");
                PadSleepModeController.this.mHandler.sendMessageAtFrontOfQueue(PadSleepModeController.this.mHandler.obtainMessage(7));
            } else if (PadSleepModeController.ACTION_BLUETOOTH_OPP_INBOUND_START.equals(action) || PadSleepModeController.ACTION_BLUETOOTH_OPP_OUTBOUND_START.equals(action)) {
                PadSleepModeController.this.oppState = true;
            } else if (PadSleepModeController.ACTION_BLUETOOTH_OPP_INBOUND_END.equals(action) || PadSleepModeController.ACTION_BLUETOOTH_OPP_OUTBOUND_END.equals(action)) {
                PadSleepModeController.this.oppState = false;
            }
        }

        private ClockReceiver() {
        }
    }

    private class DeepSleepData {
        public double deepSleepEnterCa;
        public long deepSleepEnterTime;
        public double deepSleepExitCa;
        public long deepSleepExitTime;

        private DeepSleepData() {
            this.deepSleepEnterCa = 0.0d;
            this.deepSleepEnterTime = 0L;
            this.deepSleepExitCa = 0.0d;
            this.deepSleepExitTime = 0L;
        }
    }

    private class DeepSleepModeCloudParamsObserver implements b.a.d {
        @Override // b.a.d
        public void onChanged(JSONObject jSONObject) {
            if (jSONObject != null) {
                PadSleepModeController.this.putCloudDataToDB(jSONObject);
            }
        }

        private DeepSleepModeCloudParamsObserver() {
        }
    }

    private static class HotsSpotSoftApCallback implements WifiManager.SoftApCallback {
        private HotsSpotSoftApCallback() {
        }

        public void onConnectedClientsChanged(@NonNull SoftApInfo softApInfo, @NonNull List<WifiClient> list) {
            PadSleepModeController.mNumConnectedDevices = list.size();
        }
    }

    private class LightSensorListener implements SensorEventListener {
        @Override // android.hardware.SensorEventListener
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == 5) {
                int i2 = PadSleepModeController.this.mSleepState.lightLux;
                PadSleepModeController.this.mSleepState.lightLux = (int) sensorEvent.values[0];
                if ((i2 <= PadSleepModeController.this.mSleepState.LIGHT_LUX_THRESHOLD || PadSleepModeController.this.mSleepState.lightLux > PadSleepModeController.this.mSleepState.LIGHT_LUX_THRESHOLD) && (i2 > PadSleepModeController.this.mSleepState.LIGHT_LUX_THRESHOLD || PadSleepModeController.this.mSleepState.lightLux <= PadSleepModeController.this.mSleepState.LIGHT_LUX_THRESHOLD)) {
                    if (PadSleepModeController.DBG_SLEEP) {
                        Slog.d(PadSleepModeController.TAG, "onSensorChanged TYPE_LIGHT ignore for shaking value0=" + sensorEvent.values[0]);
                        return;
                    }
                    return;
                }
                if (PadSleepModeController.DBG_SLEEP) {
                    Slog.d(PadSleepModeController.TAG, "onSensorChanged TYPE_LIGHT value0=" + sensorEvent.values[0]);
                }
                PadSleepModeController.this.mSleepState.lightTime = (int) (sensorEvent.timestamp / 1000000000);
            }
        }

        private LightSensorListener() {
        }

        @Override // android.hardware.SensorEventListener
        public void onAccuracyChanged(Sensor sensor, int i2) {
        }
    }

    private class RequestWakeReceiver extends BroadcastReceiver {
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent != null && "com.miui.powerkeeper_request_wake".equals(intent.getAction())) {
                int intExtra = intent.getIntExtra("reason", 0);
                PadSleepModeController.this.logWithsLocal("receive ACTION_REQUEST_WAKE reason=" + intExtra);
                if (intExtra == 1) {
                    PadSleepModeController.this.mHandler.sendEmptyMessage(10);
                }
            }
        }

        private RequestWakeReceiver() {
        }
    }

    private class SleepHandler extends Handler {
        public SleepHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i2;
            int i3;
            if (PadSleepModeController.DBG_SLEEP) {
                Slog.d(PadSleepModeController.TAG, "type = " + message.what + " arg1 = " + message.arg1);
            }
            int i4 = message.what;
            if (i4 == -8) {
                PadSleepModeController.this.mSleepState.isCharging = ((Boolean) ((AsyncResult) message.obj).result).booleanValue();
                PadSleepModeController.this.logWithsLocal("is charging state " + PadSleepModeController.this.mSleepState.isCharging);
                if (PadSleepModeController.this.mSleepState.isCharging) {
                    PadSleepModeController.this.mSleepState.chargingTime = (int) (SystemClock.elapsedRealtime() / 1000);
                    if (PadSleepModeController.this.mSleepState.state[PadSleepModeController.this.mSleepState.index] == 2 || PadSleepModeController.this.mSleepState.state[PadSleepModeController.this.mSleepState.index] == 3 || PadSleepModeController.this.mSleepState.state[PadSleepModeController.this.mSleepState.index] == 5) {
                        PadSleepModeController.this.tryToQuitSleep(2);
                        return;
                    }
                    return;
                }
                if (PadSleepModeController.this.hasTimeToSleep()) {
                    if (PadSleepModeController.this.mSleepState.chargingTime != 0) {
                        PadSleepModeController.this.mSleepState.chargedTotalDuration += ((int) (SystemClock.elapsedRealtime() / 1000)) - PadSleepModeController.this.mSleepState.chargingTime;
                        PadSleepModeController.this.mSleepState.chargingTime = 0;
                    }
                    if (PadSleepModeController.this.mSleepState.state[PadSleepModeController.this.mSleepState.index] == 2) {
                        PadSleepModeController.this.resetScreenOffAlarm(LogType.LOG_TYPE_SAMPLING_NETWORK_MODE, PadSleepModeController.CHECK_SCREEN_OFF_DURATION[PadSleepModeController.this.getIndex()]);
                        return;
                    }
                    return;
                }
                return;
            }
            if (i4 == -7) {
                PadSleepModeController.this.mSleepState.isScreenOn = true;
                PadSleepModeController.this.tryToQuitSleep(8);
                return;
            }
            if (i4 == -6) {
                if (PadSleepModeController.this.mSleepState.state[PadSleepModeController.this.mSleepState.index] != 4 && PadSleepModeController.this.mSleepState.state[PadSleepModeController.this.mSleepState.index] != 2 && PadSleepModeController.this.mSleepState.state[PadSleepModeController.this.mSleepState.index] != 1 && PadSleepModeController.this.mSleepState.state[PadSleepModeController.this.mSleepState.index] != 0) {
                    PadSleepModeController.this.tryToQuitSleep(1);
                    return;
                } else {
                    if (PadSleepModeController.DBG_SLEEP) {
                        Slog.d(PadSleepModeController.TAG, "Ignore power key event for not in sleep");
                        return;
                    }
                    return;
                }
            }
            if (i4 == -5) {
                PadSleepModeController.this.unregisterAmdSensor();
                if (PadSleepModeController.this.hasTimeToSleep()) {
                    if (PadSleepModeController.DBG_SLEEP) {
                        Slog.d(PadSleepModeController.TAG, "screen off amdStopTime = " + PadSleepModeController.this.mSleepState.amdStopTime);
                    }
                    PadSleepModeController.this.mSleepState.isScreenOn = false;
                    if (PadSleepModeController.this.mSleepState.isCharging) {
                        return;
                    }
                    PadSleepModeController.this.resetScreenOffAlarm(LogType.LOG_TYPE_SAMPLING_CLOCK, PadSleepModeController.CHECK_SCREEN_OFF_DURATION[PadSleepModeController.this.getIndex()]);
                    return;
                }
                return;
            }
            if (i4 == -4) {
                if (PadSleepModeController.DBG_SLEEP) {
                    Slog.d(PadSleepModeController.TAG, "screen on");
                }
                if (PadSleepModeController.this.mWifiManager != null && PadSleepModeController.this.mHotsSpotSoftApCallback != null) {
                    PadSleepModeController.this.mWifiManager.unregisterSoftApCallback(PadSleepModeController.this.mHotsSpotSoftApCallback);
                    PadSleepModeController.this.mWifiManager = null;
                    PadSleepModeController.this.mHotsSpotSoftApCallback = null;
                }
                PadSleepModeController.this.mSleepState.isScreenOn = true;
                PadSleepModeController.this.registerAmdSensor();
                PadSleepModeController.this.tryToQuitSleep(4);
                return;
            }
            switch (i4) {
                case 6:
                    PadSleepModeController.this.mSleepState.resetThreshold();
                    if (Build.IS_INTERNATIONAL_BUILD) {
                        if (!SystemProperties.get("ro.product.mod_device", "").contains("_in_")) {
                            PadSleepModeController.this.logWithsLocal("quit for region by intertional devices");
                        } else if (!PadSleepModeController.this.isCheckRegion("IN")) {
                            PadSleepModeController.this.logWithsLocal("quit for region");
                        }
                    }
                    if (!PadSleepModeController.this.hasTimeToSleep()) {
                        PadSleepModeController.this.logWithsLocal("state in day");
                        if (PadSleepModeController.this.isDeepSleepModeCloudEnabled()) {
                            PadSleepModeController.this.startListenDeviceState();
                            PadSleepModeController.this.registerDeepCheckStationaryReceiver();
                        }
                        PadSleepModeController.this.startNightClockTimer();
                    } else {
                        PadSleepModeController.this.mSleepState.state[PadSleepModeController.this.mSleepState.index] = 1;
                        PadSleepModeController.this.mSleepState.nightTime = (int) (SystemClock.elapsedRealtime() / 1000);
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(13, 0);
                        calendar.set(12, 0);
                        calendar.set(11, 0);
                        PadSleepModeController.this.mSleepState.nightTimeOffset = (int) ((System.currentTimeMillis() - calendar.getTimeInMillis()) / 1000);
                        PadSleepModeController.this.mSleepState.nightPowerConsumed = Utils.getScreenOffPowerConsumed();
                        PadSleepModeController.this.checkChineseUser();
                        PadSleepModeController.this.registerSleepSwitchObserver();
                        PadSleepModeController.this.startListenDeviceStateIfNeeded(message.arg1);
                        PadSleepModeController.this.startDayClockTimer();
                        if (SimpleSettings.Misc.getBoolean(PadSleepModeController.this.mContext, "sleep_reboot", false)) {
                            PadSleepModeController.this.mCheckSleepPower = PadSleepModeController.this.new CheckSleepPower();
                        }
                    }
                    break;
                case 7:
                    PadSleepModeController.this.mSleepState.dayTime = (int) (SystemClock.elapsedRealtime() / 1000);
                    if (PadSleepModeController.this.mSleepState.chargingTime != 0) {
                        PadSleepModeController.this.mSleepState.chargedTotalDuration += PadSleepModeController.this.mSleepState.dayTime - PadSleepModeController.this.mSleepState.chargingTime;
                        PadSleepModeController.this.mSleepState.chargingTime = 0;
                    }
                    PadSleepModeController.this.mSleepState.dayPowerConsumed = Utils.getScreenOffPowerConsumed();
                    boolean z2 = PadSleepModeController.this.mSleepState.state[PadSleepModeController.this.mSleepState.index] == 3;
                    if (PadSleepModeController.this.mSleepState.state[PadSleepModeController.this.mSleepState.index] == 3) {
                        PadSleepModeController.this.mSleepState.state[PadSleepModeController.this.mSleepState.index] = 4;
                        if (PadSleepModeController.this.mSleepState.sleepBatteryLevel > 0) {
                            PadSleepModeController.this.mSleepState.sleepTotalConsumeBattery += PadSleepModeController.this.mSleepState.sleepBatteryLevel - Utils.getCurBatteryLevel(PadSleepModeController.this.mContext);
                        }
                        PadSleepModeController.this.mSleepState.wakeTime[PadSleepModeController.this.mSleepState.index] = PadSleepModeController.this.mSleepState.dayTime;
                    }
                    PadSleepModeController.this.logWithsLocal("ACTION_ENTER_DAY mSleepState=" + PadSleepModeController.this.mSleepState);
                    if (PadSleepModeController.this.mSleepState.state[PadSleepModeController.this.mSleepState.index] != 0) {
                        PadSleepModeController padSleepModeController = PadSleepModeController.this;
                        padSleepModeController.uploadSleepStatistic(padSleepModeController.mSleepState);
                    }
                    if (z2) {
                        PadSleepModeController padSleepModeController2 = PadSleepModeController.this;
                        padSleepModeController2.restoreSleepConfig(padSleepModeController2.mSleepState);
                    }
                    PadSleepModeController padSleepModeController3 = PadSleepModeController.this;
                    padSleepModeController3.writeToDb(padSleepModeController3.mSleepState);
                    PadSleepModeController.this.unregisterAll(16);
                    if (PadSleepModeController.this.mCheckSleepPower != null) {
                        PadSleepModeController.this.mCheckSleepPower.dispose();
                        PadSleepModeController.this.mCheckSleepPower = null;
                    }
                    PadSleepModeController.this.startNightClockTimer();
                    break;
                case 8:
                    PadSleepModeController padSleepModeController4 = PadSleepModeController.this;
                    padSleepModeController4.mWifiManager = (WifiManager) padSleepModeController4.mContext.getSystemService("wifi");
                    PadSleepModeController.this.mHotsSpotSoftApCallback = new HotsSpotSoftApCallback();
                    PadSleepModeController.this.mWifiManager.registerSoftApCallback(new HandlerExecutor(new Handler(Looper.myLooper())), PadSleepModeController.this.mHotsSpotSoftApCallback);
                    int i5 = PadSleepModeController.this.mSleepState.isCharging ? 2 : PadSleepModeController.this.mSleepState.isScreenOn ? 4 : 0;
                    PadSleepModeController.this.logWithsLocal("onReceive ACTION_SCREEN_OFF_TIMEOUT reason=" + PadSleepModeController.this.reasonToString(i5));
                    if (i5 != 0) {
                        PadSleepModeController.this.mSleepState.failedReason[PadSleepModeController.this.mSleepState.index] = i5;
                        PadSleepModeController padSleepModeController5 = PadSleepModeController.this;
                        padSleepModeController5.notifySleepState(1, padSleepModeController5.reasonToString(i5));
                    } else {
                        PadSleepModeController.this.registerLightSensor();
                        WhetstoneActivityManager.sleepModeRegisterGpsCallback();
                        PadSleepModeController.this.resetCheckStationaryAlarm(LogType.LOG_TYPE_SAMPLING_TRANSMIT_POWER);
                    }
                    break;
                case 9:
                    if (PadSleepModeController.this.mSleepState.state[PadSleepModeController.this.mSleepState.index] == 3) {
                        PadSleepModeController.this.logWithsLocal("ERROR already in asleep");
                    } else if (PadSleepModeController.this.mSleepState.state[PadSleepModeController.this.mSleepState.index] != 5) {
                        int iElapsedRealtime = (int) (SystemClock.elapsedRealtime() / 1000);
                        boolean z3 = iElapsedRealtime - PadSleepModeController.this.mSleepState.amdStopTime > PadSleepModeController.this.mSleepState.STATIONARY_TIME_THRESHOLD;
                        PadSleepModeController.this.mGpsPkg = WhetstoneActivityManager.getGpsPkg();
                        PadSleepModeController.this.updateSleepStateGpsStopTime(WhetstoneActivityManager.getSleepStateGpsStopTime());
                        PadSleepModeController.this.logWithsLocal("gps is running mGpsPkg =" + PadSleepModeController.this.mGpsPkg + " gpsStopTime=" + PadSleepModeController.this.mSleepState.gpsStopTime + " interval=" + (iElapsedRealtime - PadSleepModeController.this.mSleepState.gpsStopTime));
                        boolean z4 = PadSleepModeController.this.mGpsPkg == null && iElapsedRealtime - PadSleepModeController.this.mSleepState.gpsStopTime > PadSleepModeController.this.mSleepState.STATIONARY_TIME_THRESHOLD;
                        boolean z5 = PadSleepModeController.this.mSleepState.lightLux <= PadSleepModeController.this.mSleepState.LIGHT_LUX_THRESHOLD && iElapsedRealtime - PadSleepModeController.this.mSleepState.lightTime > PadSleepModeController.this.mSleepState.STATIONARY_TIME_THRESHOLD;
                        if (PadSleepModeController.DBG_SLEEP) {
                            Log.d(PadSleepModeController.TAG, "mNumConnectedDevices -> " + PadSleepModeController.mNumConnectedDevices);
                        }
                        boolean z6 = PadSleepModeController.mNumConnectedDevices > 0;
                        PadSleepModeController padSleepModeController6 = PadSleepModeController.this;
                        boolean zIsAudioActiveWhenScreenOff = padSleepModeController6.isAudioActiveWhenScreenOff(padSleepModeController6.mContext);
                        if (PadSleepModeController.this.mSleepState.isCharging) {
                            i2 = 2;
                        } else {
                            int i6 = !z3 ? 1024 : 0;
                            if (!z4) {
                                i6 |= 2048;
                            }
                            if (z6) {
                                i6 |= 4096;
                            }
                            if (PadSleepModeController.this.mSleepState.isScreenOn) {
                                i6 |= 4;
                            }
                            i2 = !z5 ? i6 | 256 : i6;
                            if (zIsAudioActiveWhenScreenOff) {
                                i2 |= Integer.MIN_VALUE;
                            }
                        }
                        if (i2 != 0) {
                            PadSleepModeController.this.logWithsLocal("failedReason=" + PadSleepModeController.this.reasonToString(i2) + ((i2 | 256) != 0 ? " light=" + PadSleepModeController.this.mSleepState.lightLux + " time=" + PadSleepModeController.this.mSleepState.lightTime : ""));
                            PadSleepModeController.this.mSleepState.failedReason[PadSleepModeController.this.mSleepState.index] = i2;
                            PadSleepModeController.this.resetCheckStationaryAlarm(i2);
                            PadSleepModeController padSleepModeController7 = PadSleepModeController.this;
                            padSleepModeController7.notifySleepState(1, padSleepModeController7.reasonToString(i2));
                        } else if (!PadSleepModeController.this.isBackupAndRestore()) {
                            PadSleepModeController.this.mSleepState.sleepTime[PadSleepModeController.this.mSleepState.index] = (int) (SystemClock.elapsedRealtime() / 1000);
                            PadSleepModeController.this.mSleepState.state[PadSleepModeController.this.mSleepState.index] = 3;
                            PadSleepModeController.this.mSleepState.sleepBatteryLevel = Utils.getCurBatteryLevel(PadSleepModeController.this.mContext);
                            PadSleepModeController padSleepModeController8 = PadSleepModeController.this;
                            padSleepModeController8.applySleepConfig(padSleepModeController8.mSleepState);
                            if (PadSleepModeController.this.mWifiManager != null && PadSleepModeController.this.mHotsSpotSoftApCallback != null) {
                                PadSleepModeController.this.mWifiManager.unregisterSoftApCallback(PadSleepModeController.this.mHotsSpotSoftApCallback);
                                PadSleepModeController.this.mWifiManager = null;
                                PadSleepModeController.this.mHotsSpotSoftApCallback = null;
                            }
                            PadSleepModeController.this.cancelScreenOffAlarm(8388608);
                            PadSleepModeController.this.cancelCheckStationaryAlarm(8388608);
                            PadSleepModeController.this.unregisterLightSensor();
                            WhetstoneActivityManager.sleepModeUnregisterGpsCallback();
                            if (PadSleepModeController.this.mCheckSleepPower != null) {
                                PadSleepModeController.this.mCheckSleepPower.resetCheckPowerAlarm();
                            }
                        } else {
                            PadSleepModeController.this.mSleepState.failedReason[PadSleepModeController.this.mSleepState.index] = 512;
                            PadSleepModeController.this.logWithsLocal("failedReason=" + PadSleepModeController.this.reasonToString(512));
                            PadSleepModeController.this.unregisterAll(512);
                            PadSleepModeController padSleepModeController9 = PadSleepModeController.this;
                            padSleepModeController9.notifySleepState(1, padSleepModeController9.reasonToString(512));
                        }
                    } else {
                        PadSleepModeController.this.logWithsLocal("ERROR already in deep sleep");
                        PadSleepModeController.this.cancelScreenOffAlarm(8388608);
                        PadSleepModeController.this.cancelCheckStationaryAlarm(8388608);
                        PadSleepModeController.this.unregisterLightSensor();
                        WhetstoneActivityManager.sleepModeUnregisterGpsCallback();
                        if (PadSleepModeController.this.mCheckSleepPower != null) {
                            PadSleepModeController.this.mCheckSleepPower.resetCheckPowerAlarm();
                        }
                    }
                    break;
                case 10:
                    PadSleepModeController.this.tryToQuitSleep(32);
                    break;
                case 11:
                    PowerManager.WakeLock wakeLockNewWakeLock = ((PowerManager) PadSleepModeController.this.mContext.getSystemService(NightStandbyRecord.KEY_POWER)).newWakeLock(1, "padSleepMode:sleep_gps");
                    wakeLockNewWakeLock.acquire(200L);
                    if (PadSleepModeController.this.closeGps()) {
                        sendEmptyMessageDelayed(12, 2000L);
                    } else {
                        PadSleepModeController.this.closeWifiAp();
                    }
                    if (wakeLockNewWakeLock.isHeld()) {
                        wakeLockNewWakeLock.release();
                    }
                    break;
                case 12:
                    PowerManager.WakeLock wakeLockNewWakeLock2 = ((PowerManager) PadSleepModeController.this.mContext.getSystemService(NightStandbyRecord.KEY_POWER)).newWakeLock(1, "padSleepMode:sleep_wifiap");
                    wakeLockNewWakeLock2.acquire(200L);
                    PadSleepModeController.this.closeWifiAp();
                    if (wakeLockNewWakeLock2.isHeld()) {
                        wakeLockNewWakeLock2.release();
                    }
                    break;
                case 13:
                    PadSleepModeController.this.logWithsLocal("onReceive ACTION_AMD_SENSOR_TIMEOUT_DEEP");
                    if (PadSleepModeController.this.mSleepState.state[PadSleepModeController.this.mSleepState.index] != 5) {
                        PadSleepModeController padSleepModeController10 = PadSleepModeController.this;
                        padSleepModeController10.mWifiManager = (WifiManager) padSleepModeController10.mContext.getSystemService("wifi");
                        PadSleepModeController.this.mHotsSpotSoftApCallback = new HotsSpotSoftApCallback();
                        PadSleepModeController.this.mWifiManager.registerSoftApCallback(new HandlerExecutor(new Handler(Looper.myLooper())), PadSleepModeController.this.mHotsSpotSoftApCallback);
                        WhetstoneActivityManager.sleepModeRegisterGpsCallback();
                        PadSleepModeController.this.resetCheckStationaryAlarmDeepSleep(DateUtils.FORMAT_ABBREV_WEEKDAY);
                    } else {
                        PadSleepModeController.this.logWithsLocal("already in deep sleep");
                    }
                    break;
                case 14:
                    if (PadSleepModeController.this.mSleepState.state[PadSleepModeController.this.mSleepState.index] == 5) {
                        PadSleepModeController.this.logWithsLocal("ERROR already in deep_asleep");
                    } else if (!Utils.isSleepModeEnabled(PadSleepModeController.this.mContext) || !PadSleepModeController.this.isDeepSleepModeCloudEnabled()) {
                        PadSleepModeController.this.logWithsLocal("user or could close");
                    } else if (PadSleepModeController.this.mSleepState.state[PadSleepModeController.this.mSleepState.index] != 3) {
                        PadSleepModeController.this.logWithsLocal("NOT SLEEP STATE CURRENT STATE IS " + PadSleepModeController.this.mSleepState.state[PadSleepModeController.this.mSleepState.index]);
                        int iElapsedRealtime2 = (int) (SystemClock.elapsedRealtime() / 1000);
                        boolean z7 = ((long) iElapsedRealtime2) - (PadSleepModeController.this.mAmdStopTime / 1000) > ((long) PadSleepModeController.this.mSleepState.STATIONARY_TIME_THRESHOLD);
                        boolean z8 = PadSleepModeController.this.mGpsPkg == null && iElapsedRealtime2 - PadSleepModeController.this.mSleepState.gpsStopTime > PadSleepModeController.this.mSleepState.STATIONARY_TIME_THRESHOLD;
                        if (PadSleepModeController.DBG_SLEEP) {
                            Log.d(PadSleepModeController.TAG, "mNumConnectedDevicesDeep -> " + PadSleepModeController.mNumConnectedDevices);
                        }
                        boolean z9 = PadSleepModeController.mNumConnectedDevices > 0;
                        PadSleepModeController padSleepModeController11 = PadSleepModeController.this;
                        boolean zIsAudioActiveWhenScreenOff2 = padSleepModeController11.isAudioActiveWhenScreenOff(padSleepModeController11.mContext);
                        PowerKeeperManager powerKeeperManager = PowerKeeperManager.getInstance();
                        if (powerKeeperManager.isCharging()) {
                            i3 = 2;
                        } else {
                            int i7 = !z7 ? 1024 : 0;
                            if (!z8) {
                                i7 |= 2048;
                            }
                            if (z9) {
                                i7 |= 4096;
                            }
                            i3 = powerKeeperManager.isScreenOn() ? i7 | 4 : i7;
                            if (zIsAudioActiveWhenScreenOff2) {
                                i3 |= Integer.MIN_VALUE;
                            }
                        }
                        if (i3 != 0) {
                            PadSleepModeController.this.logWithsLocal("failedReason=" + PadSleepModeController.this.reasonToString(i3));
                            PadSleepModeController.this.mSleepState.failedReason[PadSleepModeController.this.mSleepState.index] = i3;
                            PadSleepModeController.this.resetCheckStationaryAlarmDeepSleep(i3);
                        } else if (!PadSleepModeController.this.isBackupAndRestore()) {
                            PadSleepModeController.this.mSleepState.sleepTime[PadSleepModeController.this.mSleepState.index] = (int) (SystemClock.elapsedRealtime() / 1000);
                            PadSleepModeController.this.mSleepState.state[PadSleepModeController.this.mSleepState.index] = 5;
                            PadSleepModeController.this.mSleepState.sleepBatteryLevel = Utils.getCurBatteryLevel(PadSleepModeController.this.mContext);
                            PadSleepModeController padSleepModeController12 = PadSleepModeController.this;
                            padSleepModeController12.applySleepConfig(padSleepModeController12.mSleepState);
                            PadSleepModeController padSleepModeController13 = PadSleepModeController.this;
                            padSleepModeController13.switchDifferentPowerSavePolicy(padSleepModeController13.mSleepState);
                            if (PadSleepModeController.this.mWifiManager != null && PadSleepModeController.this.mHotsSpotSoftApCallback != null) {
                                PadSleepModeController.this.mWifiManager.unregisterSoftApCallback(PadSleepModeController.this.mHotsSpotSoftApCallback);
                                PadSleepModeController.this.mWifiManager = null;
                                PadSleepModeController.this.mHotsSpotSoftApCallback = null;
                            }
                            PadSleepModeController.this.cancelAmdSensorAlarmEnterDeep(67108864);
                            PadSleepModeController.this.cancelCheckStationaryAlarmDeepSleep(67108864);
                            PadSleepModeController.this.unregisterDeepCheckStationaryReceiver();
                            PadSleepModeController.this.cancelClockTimer();
                            PadSleepModeController.this.unregisterDayNightClockReceiver();
                            WhetstoneActivityManager.sleepModeUnregisterGpsCallback();
                            if (PadSleepModeController.this.mCheckSleepPower != null) {
                                PadSleepModeController.this.mCheckSleepPower.resetCheckPowerAlarm();
                            }
                            if (PadSleepModeController.this.mDeepSleepData != null) {
                                PadSleepModeController.this.mDeepSleepData.deepSleepEnterTime = SystemClock.elapsedRealtime();
                                PadSleepModeController.this.mDeepSleepData.deepSleepEnterCa = BSHelper.getCurrentBatteryCap();
                            }
                        } else {
                            PadSleepModeController.this.mSleepState.failedReason[PadSleepModeController.this.mSleepState.index] = 512;
                            PadSleepModeController.this.logWithsLocal("deep sleep check fail failedReason=" + PadSleepModeController.this.reasonToString(512));
                            PadSleepModeController.this.unregisterAll(512);
                        }
                    } else {
                        PadSleepModeController.this.logWithsLocal("change from asleep to deep_asleep");
                        PadSleepModeController.this.mSleepState.state[PadSleepModeController.this.mSleepState.index] = 5;
                        PadSleepModeController padSleepModeController14 = PadSleepModeController.this;
                        padSleepModeController14.switchDifferentPowerSavePolicy(padSleepModeController14.mSleepState);
                        PadSleepModeController.this.cancelAmdSensorAlarmEnterDeep(67108864);
                        PadSleepModeController.this.cancelCheckStationaryAlarmDeepSleep(67108864);
                        PadSleepModeController.this.unregisterDeepCheckStationaryReceiver();
                        PadSleepModeController.this.cancelClockTimer();
                        PadSleepModeController.this.unregisterDayNightClockReceiver();
                        if (PadSleepModeController.this.mDeepSleepData != null) {
                            PadSleepModeController.this.mDeepSleepData.deepSleepEnterTime = SystemClock.elapsedRealtime();
                            PadSleepModeController.this.mDeepSleepData.deepSleepEnterCa = BSHelper.getCurrentBatteryCap();
                        }
                    }
                    break;
                case 15:
                    PadSleepModeController.this.logWithsLocal("amd sensor change mAmdStopTime = " + PadSleepModeController.this.mAmdStopTime);
                    if (!PowerKeeperManager.getInstance().isCharging() && Utils.isSleepModeEnabled(PadSleepModeController.this.mContext) && PadSleepModeController.this.isDeepSleepModeCloudEnabled()) {
                        PadSleepModeController padSleepModeController15 = PadSleepModeController.this;
                        padSleepModeController15.resetAmdSensorAlarmEnterDeep(DateUtils.FORMAT_ABBREV_TIME, padSleepModeController15.mSleepState.PAD_STAND_TIME);
                        break;
                    }
                    break;
                case 16:
                    PadSleepModeController.this.tryToQuitSleep(32768);
                    break;
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
            ContentResolver contentResolverForUser = PadSleepModeController.this.mContext.getContentResolverForUser(UserHandle.SYSTEM);
            if (PadSleepModeController.SETTINGS_SLEEP_MODE_SWITCH_ATTR.equals(lastPathSegment)) {
                int intForUser = Settings.Secure.getIntForUser(contentResolverForUser, PadSleepModeController.SETTINGS_SLEEP_MODE_SWITCH_ATTR, 1, 0);
                if (intForUser == 1) {
                    SleepModeControllerNew.setSleepModeSwitch(true, 1);
                } else if (intForUser == 0) {
                    SleepModeControllerNew.setSleepModeSwitch(false, 1);
                } else {
                    Log.e(PadSleepModeController.TAG, "error, parameter type don't match");
                }
            }
        }
    }

    private class SleepState {
        int BEGIN_TIME;
        int END_TIME;
        final int INDEX_BLUETOOTH;
        final int INDEX_BLUETOOTH_HALF;
        final int INDEX_DATA;
        final int INDEX_FINGER;
        final int INDEX_GPS;
        final int INDEX_LIGHT;
        final int INDEX_NOTIFICATION;
        final int INDEX_PICKUP_WAKE;
        final int INDEX_WIFI;
        final int INDEX_WIFI_AP;
        final int INDEX_WIFI_SCAN;
        int LIGHT_LUX_THRESHOLD;
        int MAX_SLEEP_TIMES;
        int PAD_STAND_TIME;
        int STATIONARY_TIME_THRESHOLD;
        int amdStopTime;
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
        boolean isDeepSleepCloudOff;
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
        int[] wakeTime;

        /* JADX INFO: Access modifiers changed from: private */
        public String nightToString() {
            StringBuilder sb = new StringBuilder(256);
            sb.append('{');
            sb.append("index=");
            sb.append(this.index);
            sb.append(" state=");
            sb.append(PadSleepModeController.this.stateToString(this.state[this.index]));
            sb.append(" isCharging=");
            sb.append(this.isCharging);
            sb.append(" isUserOff=");
            sb.append(this.isUserOff);
            sb.append(" isCloudOff=");
            sb.append(this.isCloudOff);
            sb.append(" isDeepSleepCloudOff=");
            sb.append(this.isDeepSleepCloudOff);
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
            this.isDeepSleepCloudOff = false;
            this.foreignSim = 0;
            this.foreignNet = 0;
            this.nightTimeOffset = 0;
            this.nightTime = 0;
            this.monitorTime = 0;
            this.dayTime = 0;
            this.amdStopTime = 0;
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
            int i3 = PadSleepModeController.DEFAULT_SLEEP_MAX_TIMES;
            this.STATIONARY_TIME_THRESHOLD = PadSleepModeController.DEFAULT_STATIONARY_TIME_THRESHOLD;
            this.LIGHT_LUX_THRESHOLD = 5;
            this.BEGIN_TIME = 23;
            this.END_TIME = 7;
            this.PAD_STAND_TIME = PadSleepModeController.CHECK_SCREEN_OFF_DURATION_FOR_DEEP_SLEEP;
            String str = null;
            String functionParameters2 = CloudFunctionConfig.getFunctionParameters2(PadSleepModeController.this.mContext, Constant.FUNC_SLEEP_MODE_CLOUD, null);
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
                    if (PadSleepModeController.DBG_SLEEP) {
                        Slog.d(PadSleepModeController.TAG, "resetThreshold1 " + e2);
                    }
                }
            }
            if (separatedStringAt2 != null && !separatedStringAt2.isEmpty()) {
                try {
                    this.STATIONARY_TIME_THRESHOLD = Integer.parseInt(separatedStringAt2, 10);
                } catch (NumberFormatException e3) {
                    if (PadSleepModeController.DBG_SLEEP) {
                        Slog.d(PadSleepModeController.TAG, "resetThreshold2 " + e3);
                    }
                }
            }
            if (separatedStringAt3 != null && !separatedStringAt3.isEmpty()) {
                try {
                    this.LIGHT_LUX_THRESHOLD = Integer.parseInt(separatedStringAt3, 10);
                } catch (NumberFormatException e4) {
                    if (PadSleepModeController.DBG_SLEEP) {
                        Slog.d(PadSleepModeController.TAG, "resetThreshold3 " + e4);
                    }
                }
            }
            if (separatedStringAt4 != null && !separatedStringAt4.isEmpty() && separatedStringAt != null && !separatedStringAt.isEmpty()) {
                try {
                    this.BEGIN_TIME = Integer.parseInt(separatedStringAt4, 10);
                    this.END_TIME = Integer.parseInt(separatedStringAt, 10);
                } catch (NumberFormatException e5) {
                    if (PadSleepModeController.DBG_SLEEP) {
                        Slog.d(PadSleepModeController.TAG, "resetThreshold4 " + e5);
                    }
                }
            }
            if (PadSleepModeController.DBG_COMPRESS_TIME || i3 <= 0 || i3 > 10) {
                i3 = PadSleepModeController.DEFAULT_SLEEP_MAX_TIMES;
            }
            if (PadSleepModeController.DBG_COMPRESS_TIME || (i2 = this.STATIONARY_TIME_THRESHOLD) <= 0 || i2 > 3600) {
                this.STATIONARY_TIME_THRESHOLD = PadSleepModeController.DEFAULT_STATIONARY_TIME_THRESHOLD;
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
            if (PadSleepModeController.DBG_SLEEP) {
                Slog.d(PadSleepModeController.TAG, "resetThreshold times=" + this.MAX_SLEEP_TIMES + " stationary=" + this.STATIONARY_TIME_THRESHOLD + " light=" + this.LIGHT_LUX_THRESHOLD + " begin=" + this.BEGIN_TIME + " end=" + this.END_TIME + " newMaxSleepTimes=" + i3);
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
            int i6 = Utils.toInt(CloudFunctionConfig.getFunctionParameters(PadSleepModeController.this.mContext, Constant.FUNC_DEEP_SLEEP_MODE_CLOUD, ""), -1) * 3600000;
            this.PAD_STAND_TIME = i6;
            if (i6 <= 0) {
                this.PAD_STAND_TIME = PadSleepModeController.CHECK_SCREEN_OFF_DURATION_FOR_DEEP_SLEEP;
                PadSleepModeController.this.logWithsLocal("pad cloud invalid threshold");
            }
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
        /* JADX WARN: Type inference failed for: r1v27 */
        /* JADX WARN: Type inference failed for: r1v28 */
        /* JADX WARN: Type inference failed for: r1v29 */
        /* JADX WARN: Type inference failed for: r1v30 */
        /* JADX WARN: Type inference failed for: r1v31 */
        /* JADX WARN: Type inference failed for: r1v32 */
        /* JADX WARN: Type inference failed for: r1v33 */
        /* JADX WARN: Type inference failed for: r1v5 */
        /* JADX WARN: Type inference failed for: r1v6 */
        /* JADX WARN: Type inference failed for: r1v8 */
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
                ?? r2 = r1;
                if (this.isDeepSleepCloudOff) {
                    r2 = (r1 == true ? 1 : 0) | 32;
                }
                int i10 = this.foreignSim;
                ?? r3 = r2;
                if (i10 >= 2) {
                    r3 = (r2 == true ? 1 : 0) | 16;
                } else if (i10 == 1) {
                    r3 = (r2 == true ? 1 : 0) | 8;
                }
                int i11 = r3;
                if (this.foreignNet >= 1) {
                    i11 = (r3 == true ? 1 : 0) | 4;
                }
                jSONObject.put("index", this.index).put("sleepTotalDuration", sleepTime / 60).put("sleepCount", i2).put("sleepTotalConsumeBattery", this.sleepTotalConsumeBattery).put("nightScreenOffConsumeBattery", getNightScreenOffConsumeBattery()).put("previousNotification", this.previousNotification).put("previous", this.previous).put("restoreWarning", this.restoreWarning).put("rejectReason", i11).put("nightTimeOffset", this.nightTimeOffset / 60).put("nightTime", this.nightTime / 60).put("monitorTime", this.monitorTime / 60).put("dayTime", this.dayTime / 60).put("lightLux", this.lightLux).put("chargedTotalDuration", this.chargedTotalDuration / 60);
            } catch (JSONException e2) {
                Slog.w(PadSleepModeController.TAG, "toJson exception", e2);
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
            if (PadSleepModeController.this.mSleepState.nightTime <= 0) {
                sb.append(" nightDuration={-1, -1, 0}");
            } else {
                int iElapsedRealtime = PadSleepModeController.this.mSleepState.dayTime <= 0 ? (int) (SystemClock.elapsedRealtime() / 1000) : PadSleepModeController.this.mSleepState.dayTime;
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
                sb.append(decimalFormat.format(((double) (iElapsedRealtime - PadSleepModeController.this.mSleepState.nightTime)) / 3600.0d));
                sb.append('h');
                sb.append(", ");
                sb.append(decimalFormat.format(((double) i2) / 3600.0d));
                sb.append('h');
                sb.append(", ");
                sb.append(decimalFormat.format(((double) i4) / 3600.0d));
                sb.append("h}");
            }
            if (PadSleepModeController.this.mSleepState.nightPowerConsumed < 0) {
                sb.append(" battery={-1");
            } else {
                sb.append(" battery={");
                sb.append(PadSleepModeController.this.mSleepState.dayPowerConsumed - PadSleepModeController.this.mSleepState.nightPowerConsumed);
            }
            sb.append(", ");
            sb.append(PadSleepModeController.this.mSleepState.sleepTotalConsumeBattery);
            sb.append(", ");
            sb.append(this.nightPowerConsumed);
            sb.append(", ");
            sb.append(this.dayPowerConsumed);
            sb.append('}');
            sb.append(" cloud={");
            sb.append(this.isCloudOff);
            sb.append(", ");
            sb.append(this.isDeepSleepCloudOff);
            sb.append(", ");
            sb.append(this.MAX_SLEEP_TIMES);
            sb.append(", ");
            sb.append(this.PAD_STAND_TIME);
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
            sb.append(this.amdStopTime);
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
            this.INDEX_BLUETOOTH_HALF = 2048;
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
                if (PadSleepModeController.this.mSleepState.isUserOff != Utils.isSleepModeEnabled(PadSleepModeController.this.mContext)) {
                    return;
                }
                PadSleepModeController.this.mSleepState.isUserOff = !PadSleepModeController.this.mSleepState.isUserOff;
                if (PadSleepModeController.DBG_SLEEP) {
                    Slog.d(PadSleepModeController.TAG, "onChange isUserOff=" + PadSleepModeController.this.mSleepState.isUserOff);
                }
                if (PadSleepModeController.this.mSleepState.isUserOff) {
                    PadSleepModeController.this.tryToQuitSleep(64);
                    return;
                } else {
                    if (PadSleepModeController.this.hasTimeToSleep()) {
                        PadSleepModeController.this.startListenDeviceStateIfNeeded(LogType.LOG_TYPE_SAMPLING_INTERRUPT);
                        return;
                    }
                    return;
                }
            }
            if (Constant.FUNC_SLEEP_MODE_CLOUD.equals(lastPathSegment)) {
                if (PadSleepModeController.this.mSleepState.isCloudOff != PadSleepModeController.this.isSleepModeCloudEnabled()) {
                    return;
                }
                PadSleepModeController.this.mSleepState.isCloudOff = !PadSleepModeController.this.mSleepState.isCloudOff;
                if (PadSleepModeController.DBG_SLEEP) {
                    Slog.d(PadSleepModeController.TAG, "onChange isCloudOff=" + PadSleepModeController.this.mSleepState.isCloudOff);
                }
                if (PadSleepModeController.this.mSleepState.isCloudOff) {
                    PadSleepModeController.this.tryToQuitSleep(128);
                    return;
                } else {
                    if (PadSleepModeController.this.hasTimeToSleep()) {
                        PadSleepModeController.this.startListenDeviceStateIfNeeded(LogType.LOG_TYPE_SAMPLING_WIFI_DATA);
                        return;
                    }
                    return;
                }
            }
            if (Constant.FUNC_DEEP_SLEEP_MODE_CLOUD.equals(lastPathSegment) && PadSleepModeController.this.mSleepState.isDeepSleepCloudOff == PadSleepModeController.this.isDeepSleepModeCloudEnabled()) {
                PadSleepModeController.this.mSleepState.isDeepSleepCloudOff = !PadSleepModeController.this.mSleepState.isDeepSleepCloudOff;
                if (PadSleepModeController.DBG_SLEEP) {
                    Slog.d(PadSleepModeController.TAG, "onChange isDeepSleepCloudOff=" + PadSleepModeController.this.mSleepState.isDeepSleepCloudOff);
                }
                if (PadSleepModeController.this.mSleepState.isDeepSleepCloudOff) {
                    PadSleepModeController.this.tryToQuitSleep(536870912);
                } else if (PadSleepModeController.this.hasTimeToSleep()) {
                    PadSleepModeController.this.startListenDeviceStateIfNeeded(LogType.LOG_TYPE_F_SAMPLING_CURRENT);
                }
            }
        }
    }

    static {
        boolean zIsLoggable = Log.isLoggable("power.sleep.time.pad", 3);
        DBG_COMPRESS_TIME = zIsLoggable;
        DBG_SLEEP = zIsLoggable || Log.isLoggable(TAG, 3);
        RANDOM_DURATION = (zIsLoggable ? 1 : 30) * 60000;
        CHECK_SCREEN_OFF_DURATION_FOR_DEEP_SLEEP = (zIsLoggable ? 1 : DateTimeHelper.DAY_IN_MINUTE) * 60000;
        CHECK_SCREEN_OFF_DURATION = zIsLoggable ? new int[]{60000, 60000, 60000, 60000} : new int[]{1800000, Constants.MAX_VALID_TIME_FOR_REGISTRATION_REQUEST, 600000, PowerCheckerCloudConfigure.DEFAULT_WAKEUP_ALARM_INTERVAL};
        CHECK_STATIONARY_DURATION = (zIsLoggable ? 1 : 10) * 60000;
        CHECK_STATIONARY_DURATION_FOR_DEEP_SLEEP = (zIsLoggable ? 1 : 15) * 60000;
        DEFAULT_SLEEP_MAX_TIMES = zIsLoggable ? 50 : 2;
        DEFAULT_STATIONARY_TIME_THRESHOLD = (zIsLoggable ? 1 : 9) * 60;
        KEY_RESOURCE_STATE = "key_sleep_state";
        KEY_SLEEP_REBOOT_TIME = "sleep_reboot_time";
        mRestrictPkgs = InternationalAdapter.mRestrictPkgs;
        mNumConnectedDevices = 0;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private PadSleepModeController(Context context) {
        this.mLocalLog = new LocalLog(DBG_SLEEP ? 128 : 64);
        this.mGpsPkg = null;
        this.mGpsStartTime = 0;
        this.mAmdStopTime = 0L;
        this.mPenHallStatus = 1;
        this.listeners = new ArrayList();
        this.mSetData = new HashSet();
        this.mDeepSleepData = new DeepSleepData();
        this.mDeepSleepDevice = new ArrayList(Arrays.asList("muyu"));
        this.mIsSupportDeadSleep = false;
        this.mNetCheck = false;
        this.mEnhanceSleepDevice = new ArrayList(Arrays.asList("muyu"));
        this.mIsEnhanceSleep = false;
        this.mPenPoseObserver = new UEventObserver() { // from class: com.miui.powerkeeper.statemachine.PadSleepModeController.4
            public void onUEvent(UEventObserver.UEvent uEvent) {
                String str = uEvent.get(PadSleepModeController.POWER_SUPPLY_PEN_HALL3_EVENT);
                String str2 = uEvent.get(PadSleepModeController.POWER_SUPPLY_PEN_HALL4_EVENT);
                if (str == null || str2 == null) {
                    return;
                }
                int i2 = PadSleepModeController.this.parseInt(str);
                int i3 = PadSleepModeController.this.parseInt(str2);
                if (i2 == 1 && i3 == 1 && PadSleepModeController.this.mPenHallStatus == 0) {
                    if (PadSleepModeController.DBG_SLEEP) {
                        Slog.d(PadSleepModeController.TAG, "hall far");
                    }
                    PadSleepModeController.this.mPenHallStatus = 1;
                    if (PadSleepModeController.this.mSleepState.state[PadSleepModeController.this.mSleepState.index] == 5) {
                        PadSleepModeController.this.mHandler.sendEmptyMessage(16);
                        return;
                    }
                    return;
                }
                if (((i2 == 1 && i3 == 0) || (i2 == 0 && i3 == 1)) && PadSleepModeController.this.mPenHallStatus == 1) {
                    if (PadSleepModeController.DBG_SLEEP) {
                        Slog.d(PadSleepModeController.TAG, "hall close");
                    }
                    PadSleepModeController.this.mPenHallStatus = 0;
                }
            }
        };
        this.mContext = context;
        this.mSleepState = new SleepState();
        this.mHandler = new SleepHandler(PowerStateMachineService.getThread().getLooper());
        registerDayNightClockReceiver();
        registerAmdSensor();
        startUeventObserver();
        getDeepSleepModeCloudFromNewCloud();
        b.a.p().t(b.b.N, new DeepSleepModeCloudParamsObserver());
        readFromDb(this.mSleepState);
        SleepState sleepState = this.mSleepState;
        int i2 = sleepState.state[0];
        if (i2 == 3 || i2 == 5) {
            restoreSleepConfig(sleepState);
            writeToDb(this.mSleepState.reset());
        }
        this.mHandler.sendMessage(this.mHandler.obtainMessage(6, 16777216, -1));
        List<String> list = this.mDeepSleepDevice;
        String str = android.os.Build.DEVICE;
        this.mIsSupportDeadSleep = list.contains(str);
        this.mIsEnhanceSleep = this.mEnhanceSleepDevice.contains(str);
        if (this.mIsSupportDeadSleep) {
            uploadDeadSleepData();
        }
        Slog.d(TAG, " sleep mode private constructor mRestrictPkgs = " + Arrays.toString(mRestrictPkgs) + " mIsSupportDeadSleep=" + this.mIsSupportDeadSleep);
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor(SETTINGS_SLEEP_MODE_SWITCH_ATTR), false, new SleepModeSwitchObserver(this.mHandler));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void applySleepConfig(SleepState sleepState) {
        WifiInfo connectionInfo;
        PowerManager.WakeLock wakeLockNewWakeLock = ((PowerManager) this.mContext.getSystemService(NightStandbyRecord.KEY_POWER)).newWakeLock(1, "padSleepMode:sleep_enter");
        boolean z2 = SystemProperties.getBoolean("persist.sys.spc.powerkill.newpolicy.enable", false);
        this.mNetCheck = NetUtils.isNetConnected(this.mContext);
        wakeLockNewWakeLock.acquire(200L);
        StringBuilder sb = new StringBuilder(256);
        sb.append("applySleepConfig index=" + sleepState.index);
        Slog.i(TAG, "applySleepConfig milletEnable is" + z2 + "  mNetCheck=" + this.mNetCheck + "  KillAppEnable=" + getNightKillAppEnable());
        if (!z2 || getNightKillAppEnable()) {
            clearApp();
        }
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
            sb.append("setWakeUpForKeyguardNotification to default 0");
            Settings.System.putInt(this.mContext.getContentResolver(), "wakeup_for_keyguard_notification", 0);
            i2 = 0;
        }
        sleepState.previousNotification = i2;
        sleepState.setRestore(16, i2 > 0);
        writeToDb(sleepState);
        if (i2 > 0) {
            sb.append(" setWakeUpForKeyguardNotification from " + i2 + " to 0");
            Settings.System.putInt(this.mContext.getContentResolver(), "wakeup_for_keyguard_notification", 0);
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
        boolean z4 = Settings.Global.getInt(this.mContext.getContentResolver(), "bluetooth_restricte_state", 0) == 1;
        boolean zIsBlueToothConnected = isBlueToothConnected(this.mContext);
        sleepState.setPreviousEnable(1024, z3);
        sleepState.setPreviousEnable(2048, z4);
        writeToDb(sleepState);
        if (sleepState.getPreviousEnable(1024) && !zIsBlueToothConnected) {
            sb.append("  setBluetoothEnabled false");
            Settings.Secure.putInt(this.mContext.getContentResolver(), "bluetooth_on", 0);
            adapter.disable();
        }
        restrictAlarm(true);
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
    public boolean cancelAmdSensorAlarmEnterDeep(int i2) {
        if (this.mAmdSensorStopPiDeepSleep == null) {
            return false;
        }
        if (DBG_SLEEP) {
            Slog.d(TAG, "cancelAmdSensorAlarmEnterDeep reason=" + reasonToString(i2));
        }
        ((AlarmManager) this.mContext.getSystemService("alarm")).cancel(this.mAmdSensorStopPiDeepSleep);
        this.mAmdSensorStopPiDeepSleep = null;
        return true;
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

    /* JADX INFO: Access modifiers changed from: private */
    public boolean cancelCheckStationaryAlarmDeepSleep(int i2) {
        if (this.mCheckStationaryPiDeepSleep == null) {
            return false;
        }
        if (DBG_SLEEP) {
            Slog.d(TAG, "cancelCheckStationaryAlarmDeepSleep reason=" + reasonToString(i2));
        }
        ((AlarmManager) this.mContext.getSystemService("alarm")).cancel(this.mCheckStationaryPiDeepSleep);
        this.mCheckStationaryPiDeepSleep = null;
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelClockTimer() {
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

    private void clearAllApp() {
        ProcessConfig processConfig = new ProcessConfig(1);
        List<String> activePkgList = PowerKeeperManager.getInstance().getActiveController().getActivePkgList(this.mContext, 3);
        if (activePkgList != null) {
            processConfig.setWhiteList(activePkgList);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("onekeyclean in sleep ");
        sb.append(activePkgList == null ? "" : activePkgList.toString());
        PowerLog.recordKey(sb.toString());
        if (DBG_SLEEP) {
            Slog.d(TAG, "clearAllApp whiteList=" + activePkgList);
        }
        try {
            ProcessManager.kill(processConfig);
        } catch (Exception e2) {
            Slog.e(TAG, "clearApp", e2);
        }
        PowerStateMachine.clearUnactiveApps(this.mContext);
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

    /* JADX INFO: Access modifiers changed from: private */
    public boolean closeGps() {
        int i2;
        SleepState sleepState = this.mSleepState;
        int i3 = sleepState.index;
        int[] iArr = sleepState.state;
        if (i3 >= iArr.length || !((i2 = iArr[i3]) == 3 || i2 == 5)) {
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
        int i2;
        SleepState sleepState = this.mSleepState;
        int i3 = sleepState.index;
        int[] iArr = sleepState.state;
        if (i3 >= iArr.length || !((i2 = iArr[i3]) == 3 || i2 == 5)) {
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

    private JSONObject convertToJson() {
        try {
            JSONObject jSONObject = new JSONObject();
            JSONArray jSONArray = new JSONArray();
            for (DeepSleepData deepSleepData : this.mSetData) {
                if ((deepSleepData.deepSleepExitTime == 0 && deepSleepData.deepSleepExitCa == 0.0d) || (deepSleepData.deepSleepEnterTime == 0 && deepSleepData.deepSleepEnterCa == 0.0d)) {
                    logWithsLocal("no exit or enter data");
                } else {
                    JSONObject jSONObject2 = new JSONObject();
                    jSONObject2.put("deepSleepEnterCa", deepSleepData.deepSleepEnterCa);
                    jSONObject2.put("deepSleepEnterTime", deepSleepData.deepSleepEnterTime);
                    jSONObject2.put("deepSleepExitCa", deepSleepData.deepSleepExitCa);
                    jSONObject2.put("deepSleepExitTime", deepSleepData.deepSleepExitTime);
                    jSONArray.put(jSONObject2);
                }
            }
            jSONObject.put("deepSleepData", jSONArray);
            return jSONObject;
        } catch (Exception unused) {
            logWithsLocal("convertToJson error");
            return null;
        }
    }

    private void exitDeepSleep(int i2) {
        SleepState sleepState = this.mSleepState;
        if (sleepState.state[sleepState.index] == 5) {
            logWithsLocal("deep sleep exit reason " + reasonToString(i2));
            registerDayNightClockReceiver();
            if (i2 != 4 && i2 != 8 && i2 != 2 && i2 != 1 && i2 != 32768) {
                if (i2 != 32) {
                    unregisterCheckStationaryReceiver();
                    stopListenDeviceState(i2);
                    startDayClockTimer();
                    return;
                } else if (hasTimeToSleep()) {
                    startDayClockTimer();
                    return;
                } else {
                    startNightClockTimer();
                    return;
                }
            }
            registerDeepCheckStationaryReceiver();
            if (!hasTimeToSleep()) {
                logWithsLocal("is day time");
                startNightClockTimer();
                unregisterCheckStationaryReceiver();
            } else {
                logWithsLocal("is night time");
                if (this.mCheckStationaryReceiver == null && this.mLightSensor == null) {
                    this.mHandler.sendMessage(this.mHandler.obtainMessage(6, 134217728, -1));
                }
            }
        }
    }

    private void getDeepSleepModeCloudFromNewCloud() {
        JSONObject jSONObjectL;
        b.a aVarP = b.a.p();
        int i2 = b.b.N;
        if (!aVarP.s(i2) || (jSONObjectL = b.a.p().l(i2)) == null) {
            return;
        }
        putCloudDataToDB(jSONObjectL);
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

    private boolean getPenOrKeyboardConnectStatus(String str) {
        BluetoothAdapter adapter;
        Set<BluetoothDevice> bondedDevices;
        BluetoothManager bluetoothManager = (BluetoothManager) this.mContext.getSystemService("bluetooth");
        if (bluetoothManager != null && (adapter = bluetoothManager.getAdapter()) != null && (bondedDevices = adapter.getBondedDevices()) != null && !bondedDevices.isEmpty()) {
            for (BluetoothDevice bluetoothDevice : bondedDevices) {
                String name = bluetoothDevice.getName();
                if (name != null && name.toLowerCase().contains(str) && bluetoothDevice.isConnected()) {
                    if (!DBG_SLEEP) {
                        return true;
                    }
                    Slog.d(TAG, "type=" + bluetoothDevice.getType() + " name=" + bluetoothDevice.getName() + " address=" + bluetoothDevice.getAddress() + " isConnect=" + bluetoothDevice.isConnected());
                    return true;
                }
            }
        }
        return false;
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

    public static synchronized PadSleepModeController init(Context context) {
        Slog.d(TAG, " sleep mode init mRestrictPkgs = " + Arrays.toString(mRestrictPkgs));
        if (!Utils.isSleepModeViewVisable(true)) {
            if (DBG_SLEEP) {
                Slog.d(TAG, "init return for not in regison china");
            }
            return null;
        }
        long totalPhysicalMemory = HardwareInfo.getTotalPhysicalMemory() / 1073741824;
        if (totalPhysicalMemory >= 3) {
            if (sInstance == null) {
                sInstance = new PadSleepModeController(context);
            }
            return sInstance;
        }
        if (DBG_SLEEP) {
            Slog.d(TAG, "init return for sizeGb = " + totalPhysicalMemory);
        }
        return null;
    }

    private boolean isAlarmSet() {
        String string = Settings.System.getString(this.mContext.getContentResolver(), "next_alarm_formatted");
        if (TextUtils.isEmpty(string)) {
            return false;
        }
        Log.i(TAG, "has set clock=" + string);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isAudioActiveWhenScreenOff(Context context) {
        if (PowerKeeperManager.getInstance().isScreenOn()) {
            return false;
        }
        AudioManager audioManager = (AudioManager) context.getSystemService("audio");
        int mode = audioManager.getMode();
        int streamVolume = audioManager.getStreamVolume(0);
        logWithsLocal("is audio or chartcall active = " + audioManager.isMusicActive() + " mode = " + mode + " volume= " + streamVolume);
        if (!isAudioCall(mode) || streamVolume <= 0) {
            return audioManager.isMusicActive();
        }
        return true;
    }

    private boolean isAudioCall(int i2) {
        return i2 == 2 || i2 == 3;
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

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isDeepSleepModeCloudEnabled() {
        return SimpleSettings.Misc.getBooleanForUser(this.mContext, Constant.FUNC_DEEP_SLEEP_MODE_CLOUD, true, 0);
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

    /* JADX INFO: Access modifiers changed from: private */
    public int parseInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException unused) {
            logWithsLocal("Invalid integer argument ");
            return -1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void putCloudDataToDB(JSONObject jSONObject) {
        boolean zOptBoolean = jSONObject.optBoolean("fucSwitch", true);
        SimpleSettings.Misc.putBoolean(this.mContext, Constant.FUNC_DEEP_SLEEP_MODE_CLOUD, zOptBoolean);
        String strOptString = jSONObject.optString("param");
        if (TextUtils.isEmpty(strOptString)) {
            SimpleSettings.Misc.delete(this.mContext, DEEP_SLEEP_MODE_PARAMS);
        } else {
            SimpleSettings.Misc.putString(this.mContext, DEEP_SLEEP_MODE_PARAMS, strOptString);
        }
        if (DBG_SLEEP) {
            Log.d(TAG, "cloud Data => " + zOptBoolean + " - " + strOptString);
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
        logWithsLocal("readFromDb ss: state=" + sleepState.state[0] + " previous=0x" + Integer.toHexString(sleepState.previous) + " restore=0x" + Integer.toHexString(sleepState.restore) + " noti=" + sleepState.previousNotification + " rebootTime=" + SimpleSettings.Misc.getString(this.mContext, KEY_SLEEP_REBOOT_TIME, "") + "oldFeatureStatus=" + sleepState.oldFeatureStatus);
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
                str = "amd";
            } else {
                str = str + ",amd";
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
        if ((67108864 & i2) != 0) {
            if (str == null) {
                str = "enter_deep_sleep";
            } else {
                str = str + ",enter_deep_sleep";
            }
        }
        if ((134217728 & i2) != 0) {
            if (str == null) {
                str = "exit_deep_sleep";
            } else {
                str = str + ",exit_deep_sleep";
            }
        }
        if ((268435456 & i2) != 0) {
            if (str == null) {
                str = "not_in_sleep_state";
            } else {
                str = str + ",not_in_sleep_state";
            }
        }
        if ((1073741824 & i2) != 0) {
            if (str == null) {
                str = "deep_cloud_on";
            } else {
                str = str + ",deep_cloud_on";
            }
        }
        if ((536870912 & i2) != 0) {
            if (str == null) {
                str = "deep_cloud_off";
            } else {
                str = str + ",deep_cloud_off";
            }
        }
        if ((Integer.MIN_VALUE & i2) != 0) {
            if (str == null) {
                str = "audio_active_in_bg";
            } else {
                str = str + ",audio_active_in_bg";
            }
        }
        if ((i2 & DateUtils.FORMAT_ABBREV_WEEKDAY) != 0) {
            if (str == null) {
                str = "amd_sensor_time_out";
            } else {
                str = str + ",amd_sensor_time_out";
            }
        }
        if ((i2 & DateUtils.FORMAT_ABBREV_TIME) != 0) {
            if (str == null) {
                str = "amd_sensor_change";
            } else {
                str = str + ",amd_sensor_change";
            }
        }
        if ((i2 & 32768) != 0) {
            if (str == null) {
                str = "pen_pick_up";
            } else {
                str = str + ",pen_pick_up";
            }
        }
        return str == null ? "default" : str;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerAmdSensor() {
        if (this.mAmdSensor != null && this.mAmdSensorListener != null) {
            logWithsLocal("already register amd sensor");
            return;
        }
        SensorManager sensorManager = (SensorManager) this.mContext.getSystemService(HideModeStateMachineConfig.STRATEGY_SENSOR);
        if (this.mAmdSensor == null) {
            this.mAmdSensor = sensorManager.getDefaultSensor(33171211, false);
        }
        if (this.mAmdSensor != null && this.mAmdSensorListener == null) {
            this.mAmdSensorListener = new AmdSensorListener();
        }
        if (this.mAmdSensor != null) {
            if (DBG_SLEEP) {
                Slog.d(TAG, "registerAmdSensor mAmdSensor=" + this.mAmdSensor);
            }
            sensorManager.registerListener(this.mAmdSensorListener, this.mAmdSensor, 3);
        }
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

    private void registerDayNightClockReceiver() {
        if (this.mClockReceiver != null) {
            return;
        }
        logWithsLocal("registerDayNightClockReceiver");
        this.mClockReceiver = new ClockReceiver();
        IntentFilter intentFilter = new IntentFilter(ACTION_ENTER_NIGHT);
        intentFilter.addAction(ACTION_ENTER_DAY);
        intentFilter.addAction(ACTION_BLUETOOTH_OPP_INBOUND_START);
        intentFilter.addAction(ACTION_BLUETOOTH_OPP_INBOUND_END);
        intentFilter.addAction(ACTION_BLUETOOTH_OPP_OUTBOUND_START);
        intentFilter.addAction(ACTION_BLUETOOTH_OPP_OUTBOUND_END);
        this.mContext.registerReceiver(this.mClockReceiver, intentFilter, 2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerDeepCheckStationaryReceiver() {
        if (this.mCheckDeepStationaryReceiver != null) {
            return;
        }
        if (DBG_SLEEP) {
            Slog.d(TAG, "registerCheckDeepStationaryReceiver");
        }
        this.mCheckDeepStationaryReceiver = new CheckDeepStationaryReceiver();
        IntentFilter intentFilter = new IntentFilter(ACTION_AMD_SENSOR_TIMEOUT_DEEP);
        intentFilter.addAction(ACTION_CHECK_STATIONARY_DEEP);
        this.mContext.registerReceiver(this.mCheckDeepStationaryReceiver, intentFilter, 2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerLightSensor() {
        if (this.mLightSensor != null && this.mLightSensorListener != null) {
            logWithsLocal("already register light sensor");
            return;
        }
        SensorManager sensorManager = (SensorManager) this.mContext.getSystemService(HideModeStateMachineConfig.STRATEGY_SENSOR);
        if (this.mLightSensor == null) {
            this.mLightSensor = sensorManager.getDefaultSensor(5, false);
        }
        if (this.mLightSensor != null && this.mLightSensorListener == null) {
            this.mLightSensorListener = new LightSensorListener();
        }
        if (this.mLightSensor != null) {
            if (DBG_SLEEP) {
                Slog.d(TAG, "registerLightSensor mLightSensor=" + this.mLightSensor);
            }
            SleepState sleepState = this.mSleepState;
            sleepState.lightLux = -1;
            sleepState.lightTime = 0;
            sensorManager.registerListener(this.mLightSensorListener, this.mLightSensor, 3);
        }
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
        this.mSleepState.isDeepSleepCloudOff = !isDeepSleepModeCloudEnabled();
        contentResolverForUser.registerContentObserver(SimpleSettings.Misc.getUriFor(Constant.FUNC_SLEEP_MODE_USER), false, this.mSleepSwitchObserver);
        contentResolverForUser.registerContentObserver(SimpleSettings.Misc.getUriFor(Constant.FUNC_SLEEP_MODE_CLOUD), false, this.mSleepSwitchObserver);
        contentResolverForUser.registerContentObserver(SimpleSettings.Misc.getUriFor(Constant.FUNC_DEEP_SLEEP_MODE_CLOUD), false, this.mSleepSwitchObserver);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetAmdSensorAlarmEnterDeep(int i2, long j2) {
        AlarmManager alarmManager = (AlarmManager) this.mContext.getSystemService("alarm");
        PendingIntent pendingIntent = this.mAmdSensorStopPiDeepSleep;
        if (pendingIntent == null) {
            this.mAmdSensorStopPiDeepSleep = PendingIntent.getBroadcast(this.mContext, 0, new Intent(ACTION_AMD_SENSOR_TIMEOUT_DEEP), 67108864);
            logWithsLocal("resetAmdSensorAlarmEnterDeep delay=" + j2 + "ms reason=" + reasonToString(i2));
        } else {
            alarmManager.cancel(pendingIntent);
            logWithsLocal("resetAmdSensorAlarmEnterDeep delay=" + j2 + "ms with cancel reason=" + reasonToString(i2));
        }
        alarmManager.set(3, SystemClock.elapsedRealtime() + j2, this.mAmdSensorStopPiDeepSleep);
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
    public void resetCheckStationaryAlarmDeepSleep(int i2) {
        AlarmManager alarmManager = (AlarmManager) this.mContext.getSystemService("alarm");
        PendingIntent pendingIntent = this.mCheckStationaryPiDeepSleep;
        if (pendingIntent == null) {
            this.mCheckStationaryPiDeepSleep = PendingIntent.getBroadcast(this.mContext, 0, new Intent(ACTION_CHECK_STATIONARY_DEEP), 67108864);
            if (DBG_SLEEP) {
                Slog.d(TAG, "resetCheckStationaryAlarmDeepSleep reason=" + reasonToString(i2));
            }
        } else {
            alarmManager.cancel(pendingIntent);
            if (DBG_SLEEP) {
                Slog.d(TAG, "resetCheckStationaryAlarmDeepSleep with cancel reason=" + reasonToString(i2));
            }
        }
        alarmManager.set(3, SystemClock.elapsedRealtime() + ((long) CHECK_STATIONARY_DURATION_FOR_DEEP_SLEEP), this.mCheckStationaryPiDeepSleep);
    }

    private void resetData() {
        this.mSetData.clear();
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
        sb.append("restoreSleepConfig index=" + sleepState.index + " reason=" + sleepState.quitReason[sleepState.index]);
        final WifiManager wifiManager = (WifiManager) this.mContext.getSystemService("wifi");
        if (sleepState.needRestore(2)) {
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
        } else {
            j2 = 0;
        }
        if (sleepState.needRestore(4)) {
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
        if (sleepState.needRestore(1)) {
            sleepState.setRestore(1, false);
            final boolean previousEnable3 = sleepState.getPreviousEnable(1);
            WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            int ipAddress = connectionInfo == null ? 0 : connectionInfo.getIpAddress();
            if (previousEnable3 == CommonAdapter.isDataEnabled(telephonyManager)) {
                sb.append(" setDataEnabled:warning");
                sleepState.setRestoreWarning(1);
            } else if (j2 > 0) {
                final int i2 = ipAddress;
                Runnable runnable = new Runnable() { // from class: com.miui.powerkeeper.statemachine.PadSleepModeController.2
                    @Override // java.lang.Runnable
                    public void run() {
                        PadSleepModeController.this.logWithsLocal("run setDataEnabled:" + previousEnable3);
                        long jCurrentTimeMillis = System.currentTimeMillis();
                        while (System.currentTimeMillis() - jCurrentTimeMillis <= 700 && (!wifiManager.isWifiEnabled() || i2 == 0)) {
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
        if (sleepState.needRestore(8)) {
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
        if (sleepState.needRestore(16)) {
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
        restrictAlarm(false);
        logWithsLocal(sb.toString());
        broadcastSleepState(2);
        notifySleepState(2, reasonToString(sleepState.quitReason[sleepState.index]));
        setSleepModeStatus(this.mContext, false);
        unregisterRequestWakeReceiver();
        if (this.mIsSupportDeadSleep) {
            sendDeadSleepControl(0);
        }
    }

    private void restrictAlarm(boolean z2) {
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

    private void sendDeadSleepControl(int i2) {
        if (this.mContext != null) {
            Intent intent = new Intent(ACTION_DEAD_SLEEP_CONTROL);
            intent.putExtra("deepSleepStatus", i2);
            Log.d(TAG, "sendDeadSleepControl deepsleep status=" + i2);
            this.mContext.sendBroadcast(intent);
        }
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
        this.mSleepState.isScreenOn = PowerKeeperManager.getInstance().isScreenOn();
        this.mSleepState.isCharging = PowerKeeperManager.getInstance().isCharging();
        SleepState sleepState2 = this.mSleepState;
        if (sleepState2.isCharging) {
            sleepState2.chargingTime = sleepState2.nightTime;
        }
        startListenDeviceState();
        registerCheckStationaryReceiver();
        if (isDeepSleepModeCloudEnabled()) {
            registerDeepCheckStationaryReceiver();
        }
        SleepState sleepState3 = this.mSleepState;
        if (sleepState3.isCharging) {
            sleepState3.failedReason[sleepState3.index] = 2;
        } else if (sleepState3.isScreenOn) {
            sleepState3.failedReason[sleepState3.index] = 4;
        } else {
            long lastScreenOffTime = PowerKeeperManager.getInstance().getLastScreenOffTime();
            long jElapsedRealtime = lastScreenOffTime != 0 ? SystemClock.elapsedRealtime() - lastScreenOffTime : 0L;
            logWithsLocal("screenOffDuration = " + jElapsedRealtime);
            int i4 = CHECK_SCREEN_OFF_DURATION[0];
            if (jElapsedRealtime >= i4) {
                registerLightSensor();
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
            calendar.set(11, 23);
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

    private void startUeventObserver() {
        this.mPenPoseObserver.startObserving(POWER_SUPPLY_PEN_HALL3_EVENT);
        this.mPenPoseObserver.startObserving(POWER_SUPPLY_PEN_HALL4_EVENT);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String stateToString(int i2) {
        if (i2 == 1) {
            return "in_night";
        }
        if (i2 == 2) {
            return "monitoring";
        }
        if (i2 == 3) {
            return "asleep";
        }
        if (i2 != 4) {
            return i2 != 5 ? "default" : "deep_sleep";
        }
        return "awake";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void switchDifferentPowerSavePolicy(SleepState sleepState) {
        Objects.requireNonNull(sleepState);
        boolean previousEnable = sleepState.getPreviousEnable(128);
        if (previousEnable != isPickupWakeupEnable()) {
            logWithsLocal(" deep_sleep setPickupWakeupEnable:" + previousEnable);
            setPickupWakeupEnable(previousEnable);
        } else {
            logWithsLocal(" deep_sleep no need to setPickupWakeupEnable");
        }
        boolean zIsBlueToothConnected = isBlueToothConnected(this.mContext);
        if (sleepState.getPreviousEnable(1024) && zIsBlueToothConnected) {
            BluetoothAdapter adapter = ((BluetoothManager) this.mContext.getSystemService("bluetooth")).getAdapter();
            if (getPenOrKeyboardConnectStatus(PEN) && !getPenOrKeyboardConnectStatus(KEYBOARD) && this.mPenHallStatus == 0) {
                logWithsLocal("pen setBluetoothEnabled false");
                Settings.Secure.putInt(this.mContext.getContentResolver(), "bluetooth_on", 0);
                adapter.disable();
            }
        }
        if (this.mIsSupportDeadSleep) {
            sendDeadSleepControl(1);
        }
        Log.i(TAG, "deep sleep check  mNetCheck=" + this.mNetCheck + "   hasAlarm=" + isAlarmSet() + "   isBTConnected=" + zIsBlueToothConnected + "  mIsEnhanceSleep=" + this.mIsEnhanceSleep);
        if (!this.mIsEnhanceSleep || this.mNetCheck || isAlarmSet() || zIsBlueToothConnected) {
            return;
        }
        clearAllApp();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void tryToQuitSleep(int i2) {
        this.mNetCheck = false;
        SleepState sleepState = this.mSleepState;
        int i3 = sleepState.state[sleepState.index];
        boolean z2 = true;
        if (i3 == 2) {
            if (i2 == 536870912) {
                logWithsLocal("deep_cloud off");
                cancelAmdSensorAlarmEnterDeep(i2);
                cancelCheckStationaryAlarmDeepSleep(i2);
                unregisterDeepCheckStationaryReceiver();
                return;
            }
            boolean zCancelScreenOffAlarm = cancelScreenOffAlarm(i2);
            if (cancelCheckStationaryAlarm(i2)) {
                zCancelScreenOffAlarm = true;
            }
            unregisterLightSensor();
            WhetstoneActivityManager.sleepModeUnregisterGpsCallback();
            SleepState sleepState2 = this.mSleepState;
            if (sleepState2.isCharging) {
                sleepState2.failedReason[sleepState2.index] = 2;
            } else {
                sleepState2.failedReason[sleepState2.index] = i2;
            }
            if (i2 == 64 || i2 == 128 || i2 == 32) {
                unregisterCheckStationaryReceiver();
                cancelAmdSensorAlarmEnterDeep(i2);
                unregisterDeepCheckStationaryReceiver();
                stopListenDeviceState(i2);
            } else {
                z2 = zCancelScreenOffAlarm;
            }
            if (i2 == 32) {
                unregisterSleepSwitchObserver();
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
        if (i3 != 3 && i3 != 5) {
            if (i2 == 64 || i2 == 128 || i2 == 32) {
                if (DBG_SLEEP) {
                    Slog.d(TAG, "unregister all");
                }
                cancelAmdSensorAlarmEnterDeep(i2);
                cancelCheckStationaryAlarmDeepSleep(i2);
                unregisterDeepCheckStationaryReceiver();
                unregisterAll(i2);
                return;
            }
            return;
        }
        if (i3 == 3 && i2 == 536870912) {
            logWithsLocal("deep_cloud off but alseep not exit");
            cancelAmdSensorAlarmEnterDeep(i2);
            cancelCheckStationaryAlarmDeepSleep(i2);
            unregisterDeepCheckStationaryReceiver();
            return;
        }
        exitDeepSleep(i2);
        SleepState sleepState5 = this.mSleepState;
        sleepState5.state[sleepState5.index] = 4;
        int i4 = sleepState5.sleepBatteryLevel;
        if (i4 > 0) {
            sleepState5.sleepTotalConsumeBattery += i4 - Utils.getCurBatteryLevel(this.mContext);
            this.mSleepState.sleepBatteryLevel = 0;
        }
        SleepState sleepState6 = this.mSleepState;
        int[] iArr = sleepState6.quitReason;
        int i5 = sleepState6.index;
        iArr[i5] = i2;
        sleepState6.wakeTime[i5] = (int) (SystemClock.elapsedRealtime() / 1000);
        final PowerManager.WakeLock wakeLockNewWakeLock = ((PowerManager) this.mContext.getSystemService(NightStandbyRecord.KEY_POWER)).newWakeLock(1, "padSleepMode:pen_pickup");
        if (i2 == 32768) {
            wakeLockNewWakeLock.acquire(3000L);
        }
        restoreSleepConfig(this.mSleepState);
        writeToDb(this.mSleepState);
        CheckSleepPower checkSleepPower = this.mCheckSleepPower;
        if (checkSleepPower != null) {
            checkSleepPower.cancelCheckPowerAlarm();
        }
        SleepState sleepState7 = this.mSleepState;
        int i6 = sleepState7.index;
        if (i6 + 1 >= sleepState7.MAX_SLEEP_TIMES) {
            if (DBG_SLEEP) {
                Slog.d(TAG, "exceed max times");
            }
            unregisterAll(LogType.LOG_TYPE_SAMPLING_CPUFREQ);
        } else if (i2 == 32) {
            if (DBG_SLEEP) {
                Slog.d(TAG, "desk clock request");
            }
            this.mSleepState.index++;
            unregisterAll(32);
        } else {
            int i7 = i6 + 1;
            sleepState7.index = i7;
            if (sleepState7.isUserOff || sleepState7.isCloudOff || sleepState7.isDeepSleepCloudOff || sleepState7.foreignSim > 0 || sleepState7.foreignNet > 0) {
                sleepState7.state[i7] = 1;
            } else {
                sleepState7.state[i7] = 2;
            }
        }
        if (i2 == 32768) {
            this.mHandler.postDelayed(new Runnable() { // from class: com.miui.powerkeeper.statemachine.PadSleepModeController.1
                @Override // java.lang.Runnable
                public void run() {
                    PowerManager.WakeLock wakeLock = wakeLockNewWakeLock;
                    if (wakeLock == null || !wakeLock.isHeld()) {
                        return;
                    }
                    wakeLockNewWakeLock.release();
                    if (PadSleepModeController.DBG_SLEEP) {
                        Slog.d(PadSleepModeController.TAG, "rel pen_lock");
                    }
                }
            }, 3000L);
        }
        DeepSleepData deepSleepData = this.mDeepSleepData;
        if (deepSleepData != null) {
            deepSleepData.deepSleepExitTime = SystemClock.elapsedRealtime();
            this.mDeepSleepData.deepSleepExitCa = BSHelper.getCurrentBatteryCap();
            this.mSetData.add(this.mDeepSleepData);
            uploadDeepSleepData();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unregisterAll(int i2) {
        cancelScreenOffAlarm(i2);
        cancelCheckStationaryAlarm(i2);
        unregisterLightSensor();
        WhetstoneActivityManager.sleepModeUnregisterGpsCallback();
        if (i2 == 64 || i2 == 128 || i2 == 32 || i2 == 33554432 || i2 == 512 || i2 == 16 || i2 == 262144) {
            unregisterCheckStationaryReceiver();
            if (i2 != 16) {
                unregisterDeepCheckStationaryReceiver();
                stopListenDeviceState(i2);
            }
        }
        if (i2 == 32 || i2 == 33554432 || i2 == 512 || i2 == 16 || i2 == 262144) {
            unregisterSleepSwitchObserver();
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
    public void unregisterAmdSensor() {
        if (this.mAmdSensorListener == null) {
            return;
        }
        SensorManager sensorManager = (SensorManager) this.mContext.getSystemService(HideModeStateMachineConfig.STRATEGY_SENSOR);
        if (this.mAmdSensor != null) {
            if (DBG_SLEEP) {
                Slog.d(TAG, "unregisterAmdSensor mAmdSensor");
            }
            sensorManager.unregisterListener(this.mAmdSensorListener, this.mAmdSensor);
            this.mAmdSensor = null;
        }
        this.mAmdSensorListener = null;
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

    /* JADX INFO: Access modifiers changed from: private */
    public void unregisterDayNightClockReceiver() {
        if (this.mClockReceiver == null) {
            return;
        }
        logWithsLocal("unregisterDayNightClockReceiver");
        this.mContext.unregisterReceiver(this.mClockReceiver);
        this.mClockReceiver = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unregisterDeepCheckStationaryReceiver() {
        if (this.mCheckDeepStationaryReceiver == null) {
            return;
        }
        if (DBG_SLEEP) {
            Slog.d(TAG, "unregisterDeepCheckStationaryReceiver");
        }
        this.mContext.unregisterReceiver(this.mCheckDeepStationaryReceiver);
        this.mCheckDeepStationaryReceiver = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unregisterLightSensor() {
        if (this.mLightSensorListener == null) {
            return;
        }
        SensorManager sensorManager = (SensorManager) this.mContext.getSystemService(HideModeStateMachineConfig.STRATEGY_SENSOR);
        if (this.mLightSensor != null) {
            if (DBG_SLEEP) {
                Slog.d(TAG, "unregisterLightSensor mLightSensor");
            }
            sensorManager.unregisterListener(this.mLightSensorListener, this.mLightSensor);
            this.mLightSensor = null;
        }
        this.mLightSensorListener = null;
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

    private void uploadDeadSleepData() {
        String str = SystemProperties.get("persist.sys.battery.shipmode", "0");
        TrackerManager trackerManager = TrackerManager.getInstance(this.mContext);
        Slog.d(TAG, " uploadDeadSleepData shipmode = " + str + " manager=" + trackerManager);
        if (trackerManager == null || str == null || !str.equalsIgnoreCase("1")) {
            return;
        }
        trackerManager.track(3, EVENT_RECORD_SHIPMODE_DATA, str);
    }

    private void uploadDeepSleepData() {
        JSONObject jSONObjectConvertToJson = convertToJson();
        if (jSONObjectConvertToJson == null) {
            logWithsLocal("json null error");
            return;
        }
        TrackerManager.getInstance(this.mContext).track(3, EVENT_RECORD_SLEEP_DATA, jSONObjectConvertToJson);
        SleepState sleepState = this.mSleepState;
        int i2 = sleepState.state[sleepState.index];
        if (i2 == 3 || i2 == 5) {
            logWithsLocal("no need to clear");
        } else {
            resetData();
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
        printWriter.println("dump of PadSleepModeController:");
        this.mLocalLog.dump(fileDescriptor, printWriter, strArr);
        printWriter.println("SleepState=" + this.mSleepState);
        printWriter.println(KEY_SLEEP_REBOOT_TIME + '=' + SimpleSettings.Misc.getString(this.mContext, KEY_SLEEP_REBOOT_TIME, ""));
        StringBuilder sb = new StringBuilder(256);
        if (this.mCheckStationaryReceiver != null) {
            sb.append(" mCheckStationaryReceiver");
        }
        if (this.mCheckDeepStationaryReceiver != null) {
            sb.append(" mCheckDeepStationaryReceiver");
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
        if (this.mAmdSensorListener != null) {
            sb.append(" mAmdSensorListener");
        }
        if (this.mLightSensorListener != null) {
            sb.append(" mLightSensorListener");
        }
        if (this.mAmdSensor != null) {
            sb.append(" mAmdSensor");
        }
        if (this.mLightSensor != null) {
            sb.append(" mLightSensor");
        }
        if (sb.length() > 0) {
            printWriter.println("Not empty pointers:" + ((Object) sb));
        }
    }

    @Override // com.miui.powerkeeper.statemachine.SleepModeControllerNew
    public boolean getSleepModeForEarthquakeWarnFlag() {
        return false;
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
        PadSleepModeController padSleepModeController = sInstance;
        if (padSleepModeController == null) {
            return false;
        }
        SleepState sleepState = padSleepModeController.mSleepState;
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

    public void startListenDeviceState() {
        EventsAggregator eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
        eventsAggregator.registerForPowerKeyPressed(this.mHandler, null);
        eventsAggregator.registerForScreenOn(this.mHandler, null);
        eventsAggregator.registerForScreenOff(this.mHandler, null);
        eventsAggregator.registerForUserPresent(this.mHandler, null);
        eventsAggregator.registerForCharging(this.mHandler);
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
        eventsAggregator.unregisterForCharging(this.mHandler);
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
