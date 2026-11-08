package com.miui.powerkeeper.statemachine;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.hardware.fingerprint.FingerprintManager;
import android.media.AudioManager;
import android.media.AudioSystem;
import android.media.audiofx.MiSound;
import android.net.Uri;
import android.os.AsyncResult;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.ArrayMap;
import android.util.LocalLog;
import android.util.Log;
import android.util.Slog;
import android.view.IWindowManager;
import com.miui.powerkeeper.PowerKeeperApplication;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.batterylife.BLUtils;
import com.miui.powerkeeper.batterylife.BatteryData;
import com.miui.powerkeeper.batterylife.data.BatteryLife;
import com.miui.powerkeeper.batterylife.systeminfo.BSHelper;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.thermal.ThermalSwitchHandler;
import com.miui.powerkeeper.utils.PowerLog;
import com.miui.powerkeeper.utils.ShellUtils;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miui.os.Build;
import miui.process.ForegroundInfo;
import miui.process.ProcessConfig;
import miui.process.ProcessManager;
import miui.util.IMiCharge;
import miui.yellowpage.YellowPageContract;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class DynamicTurboPowerHandler extends Handler {
    private static final int A_SCENE_TIME_WINDOW = 5;
    private static final String CLOUD_DYNAMIC_TURBO_POWER = "dynamic_turbo_power";
    private static final String COMPREHENSIVE_BATTERY_LIFE = "persist.sys.power.comp.bat.life";
    private static final double DEFAULT_AVERAGE_VOLTAGE = 3.8d;
    private static final int DELAY_TIME_SET_SECOND_START = 12000;
    private static final int DELAY_TIME_SET_START = 10000;
    private static final int FIVEG_ENABLED_SETTING_ON = 1;
    private static final int FIVEG_SWITCH_INVISIBLE = 0;
    private static final int FIVEG_SWITCH_UNSETTED = -1;
    private static final int FIVEG_SWITCH_VISIBLE = 1;
    private static final int FLAG_A_SCENE_NORMAL = 1;
    private static final int FLAG_A_SCENE_SATELLITE = 2;
    private static final String KEY_ALL_DAY_POWER_STATE = "key_all_day_power";
    private static final String KEY_STANDBY_POWER_STATE = "key_Standby_power";
    private static final String LOW_POWER_AOD_ENABLE = "lower_power_aod_enable";
    private static final String LOW_POWER_AUTO_DOWNLOAD_ENABLE = "lower_power_auto_download_enable";
    private static final String LOW_POWER_AUTO_UPDATE_ENABLE = "lower_power_auto_update_enable";
    private static final String LOW_POWER_FINGER_AOD_ENABLE = "low_power_finger_aod_enable";
    private static final String LOW_POWER_HAPTIC_FEED_BACK_ENABLE = "lower_power_haptic_feed_back_enable";
    private static final String LOW_POWER_IS_5G_ENABLE = "low_power_is_5G_enable";
    private static final String LOW_POWER_LIGHT_PULSE_ENABLE = "lower_power_light_pulse_enable";
    private static final String LOW_POWER_LIMIT_MAX_AUDIO_ENABLE = "low_power_limit_max_audio_enable";
    private static final String LOW_POWER_LOCATION_ENABLE = "low_power_location_enable";
    private static final String LOW_POWER_LOWER_AUDIO_ENABLE = "low_power_lower_audio_enable";
    private static final String LOW_POWER_LOWER_DOLBY_AUDIO_ENABLE = "low_power_lower_dolby_audio_enable";
    private static final String LOW_POWER_SLEEP_SECONDS_ENABLE = "lower_power_sleep_seconds_enable";
    private static final String LOW_POWER_SOUND_CLICK_ENABLE = "low_power_sound_click_enable";
    private static final String LOW_POWER_SOUND_DELETE_ENABLE = "low_power_sound_delete_enable";
    private static final String LOW_POWER_SOUND_DIALER_ENABLE = "low_power_sound_dialer_enable";
    private static final String LOW_POWER_SOUND_LOCK_ENABLE = "low_power_sound_lock_enable";
    private static final String LOW_POWER_SOUND_SCREENSHOT_ENABLE = "low_power_sound_screenshot_enable";
    private static final String LOW_POWER_SYNCHRONIZE_ENABLE = "low_power_synchronize_enable";
    private static final String LOW_POWER_WAKEUP_PICKUP = "low_power_wakeup_pickup_enable";
    private static final String MICHARGE_PATH_BASE_AVERAGE = "average_current";
    private static final String MICHARGE_PATH_BASE_CURRENT_VOLTAGE = "";
    private static final String MICHARGE_PATH_BASE_POWER = "constant_power";
    private static final String MICHARGE_PATH_BASE_REMAINING_TIME = "remaining_time";
    private static final String MICHARGE_PATH_FLIP_AVERAGE = "";
    private static final String MICHARGE_PATH_FLIP_CURRENT_VOLTAGE = "";
    private static final String MICHARGE_PATH_FLIP_GET_DESIGN_CAPACITY = "fg2_design_capacity";
    private static final String MICHARGE_PATH_FLIP_GET_LEARN_POWER = "";
    private static final String MICHARGE_PATH_FLIP_GET_LEARN_POWER_B = "";
    private static final String MICHARGE_PATH_FLIP_GET_REMAINING_CAPACITY = "";
    private static final String MICHARGE_PATH_FLIP_POWER = "";
    private static final String MICHARGE_PATH_FLIP_REMAINING_TIME = "";
    private static final String MICHARGE_PATH_FLIP_SET_LEARN_POWER = "";
    private static final String MICHARGE_PATH_FLIP_START_LEARN = "";
    private static final String MICHARGE_PATH_FLIP_START_LEARN_B = "";
    private static final String MICHARGE_PATH_FLIP_STOP_LEARN = "";
    private static final String MICHARGE_PATH_FLIP_STOP_LEARN_B = "";
    private static final String MICHARGE_PATH_GET_DESIGN_CAPACITY = "fg1_design_capacity";
    private static final String MICHARGE_PATH_GET_LEARN_POWER = "get_learn_power";
    private static final String MICHARGE_PATH_GET_LEARN_POWER_B = "learn_power_b";
    private static final String MICHARGE_PATH_GET_REMAINING_CAPACITY = "";
    private static final String MICHARGE_PATH_SET_LEARN_POWER = "set_learn_power";
    private static final String MICHARGE_PATH_START_LEARN = "start_learning";
    private static final String MICHARGE_PATH_START_LEARN_B = "start_learn_b";
    private static final String MICHARGE_PATH_STOP_LEARN = "stop_learning";
    private static final String MICHARGE_PATH_STOP_LEARN_B = "stop_learn_b";
    private static final String NORMAL_SAVE_BATTERY_LIFE = "persist.sys.power.normal.save.bat.life";
    private static final String PATH_BASE_AVERAGE = "sys/class/qcom-battery/average_current";
    private static final String PATH_BASE_CURRENT_VOLTAGE = "sys/class/qcom-battery/fg1_vol";
    private static final String PATH_BASE_POWER = "sys/class/qcom-battery/constant_power";
    private static final String PATH_BASE_REMAINING_TIME = "sys/class/qcom-battery/remaining_time";
    private static final String PATH_FLIP_AVERAGE = "sys/class/qcom-battery/fg2_average_current";
    private static final String PATH_FLIP_CURRENT_VOLTAGE = "sys/class/qcom-battery/fg2_vol";
    private static final String PATH_FLIP_GET_DESIGN_CAPACITY = "sys/class/qcom-battery/fg2_design_capacity";
    private static final String PATH_FLIP_GET_LEARN_POWER = "sys/class/qcom-battery/fg2_get_learn_power";
    private static final String PATH_FLIP_GET_LEARN_POWER_B = "sys/class/qcom-battery/fg2_stop_learn_b";
    private static final String PATH_FLIP_GET_REMAINING_CAPACITY = "sys/class/qcom-battery/fg2_rm";
    private static final String PATH_FLIP_POWER = "sys/class/qcom-battery/fg2_constant_power";
    private static final String PATH_FLIP_REMAINING_TIME = "sys/class/qcom-battery/fg2_remaining_time";
    private static final String PATH_FLIP_SET_LEARN_POWER = "sys/class/qcom-battery/fg2_set_learn_power";
    private static final String PATH_FLIP_START_LEARN = "sys/class/qcom-battery/fg2_start_learn";
    private static final String PATH_FLIP_START_LEARN_B = "sys/class/qcom-battery/fg2_set_learn_power_b";
    private static final String PATH_FLIP_STOP_LEARN = "sys/class/qcom-battery/fg2_stop_learn";
    private static final String PATH_FLIP_STOP_LEARN_B = "sys/class/qcom-battery/fg2_stop_learn_b";
    private static final String PATH_GET_DESIGN_CAPACITY = "sys/class/qcom-battery/fg1_design_capacity";
    private static final String PATH_GET_LEARN_POWER = "sys/class/qcom-battery/get_learn_power";
    private static final String PATH_GET_LEARN_POWER_B = "sys/class/qcom-battery/get_learn_power_b";
    private static final String PATH_GET_REMAINING_CAPACITY = "sys/class/qcom-battery/fg1_rm";
    private static final String PATH_SET_LEARN_POWER = "sys/class/qcom-battery/set_learn_power";
    private static final String PATH_START_LEARN = "sys/class/qcom-battery/start_learn";
    private static final String PATH_START_LEARN_B = "sys/class/qcom-battery/start_learn_b";
    private static final String PATH_STOP_LEARN = "sys/class/qcom-battery/stop_learn";
    private static final String PATH_STOP_LEARN_B = "sys/class/qcom-battery/stop_learn_b";
    private static final String PERFORMANCE_BATTERY_LIFE = "persist.sys.power.performance.bat.life";
    private static final String POWER_FUEL_GAUGE = "persist.sys.power.fuel.gauge";
    private static final String SATELLITE_SAVE_BATTERY_LIFE = "persist.sys.power.satellite.bat.life";
    private static final String SCENE_BATTERY_LIFE = "persist.sys.power.scene.bat.life";
    private static final String SETTING_AOD_MODE = "aod_mode";
    private static final String SETTING_AUTO_DOWNLOAD = "auto_download";
    private static final String SETTING_AUTO_UPDATE = "auto_update";
    private static final String SETTING_FIVEG_ENABLED = "fiveg_user_enable";
    private static final String SETTING_FIVEG_SWITCH_VISIBLE = "fiveg_switch_visible";
    private static final String SETTING_HAPTIC_FEED_BACK = "haptic_feedback_enabled";
    private static final String SETTING_ICON_AOD_SHOW = "gxzw_icon_aod_show_enable";
    private static final String SETTING_SOUND_CLICK = "sound_effects_enabled";
    private static final String SETTING_SOUND_DELETE = "delete_sound_effect";
    private static final String SETTING_SOUND_DIALER = "dtmf_tone";
    private static final String SETTING_SOUND_LOCK = "lockscreen_sounds_enabled";
    private static final String SETTING_SOUND_SCREENSHOT = "has_screenshot_sound";
    private static final int SLEEP_SECONDS = 15;
    private static final String TAG = "DynamicTurboPowerHandler";
    private static final int TRANSITION_ANIMATION_SCALE_SELECTOR = 1;
    private static final String ULTIMATE_SAVE_BATTERY_LIFE = "persist.sys.power.ultimate.save.bat.life";
    private static final int VENDOR_SAVING_CLASSIFICATION_POWER_HINT_ID = 5617;
    private static final int WINDOW_ANIMATION_SCALE_SELECTOR = 0;
    private static b.a mCloudObserver;
    private static String mForegroundPackageName;
    private static String mLastForegroundPackageName;
    private static DynamicTurboPowerHandler sInstance;
    private int B_SCENE_COUNT_WINDOW;
    private int B_SCENE_TIME_WINDOW;
    private final boolean DBG_POWER;
    private final int MSG_CONFIG_UPDATE;
    private final int MSG_TIMEDELAY_A;
    private final int MSG_TIMEDELAY_B;
    private double NORMAL_MODULUS;
    private double NORMAL_SAVE_MODULUS;
    private double PERFORMANCE_MODULUS;
    private double ULTIMATE_SAVE_MODULUS;
    private int mAPostDelay;
    private Runnable mAScenesRunnable;
    private boolean mAScenesStatus;
    private int mAStartPostDelay;
    private int mAccumulateSize;
    private int mActualPower;
    private int mAfterBatteryLevel;
    private int mAllAveragePower;
    private int mAllDayAveragePower;
    private int mAllTime;
    private int mAll_Count;
    private AudioManager mAudioManager;
    private int mAveragePower;
    private int mBAveragePower;
    private Runnable mBScenesRunnable;
    private int mB_Count;
    private batteryInfo mBatteryInfo;
    private int mBeforeBatteryLevel;
    private Context mContext;
    private int mCount;
    private int mCurrentLevel;
    private boolean mDisplay;
    private boolean mDisplayIdle;
    private a.a mDolbyAudio;
    private energyLearning mEnergyLearning;
    private int mEstimatePower;
    private int mFirstStage;
    private boolean mFiveG;
    private ForegroundInfo mForegroundInfo;
    private IntentReceiver mIntentReceiver;
    private boolean mIsFeature32V;
    private boolean mIsFeatureBatteryLife;
    private boolean mIsFeatureFlip;
    private boolean mIsInCall;
    private boolean mIsMiChargeOn;
    private boolean mIsPerfHind;
    private int mLastActualPower;
    private double mLastBatteryTime;
    private int mLastTime;
    private LocalLog mLocalLog;
    private boolean mLowSound;
    private MiSound mMiSound;
    private boolean mNeedRelease;
    private boolean mOnBattery;
    private Method mPerfHint;
    private int mPostDelay;
    private int mPowerMode;
    private int mSatelliteAveragePower;
    private int mSatelliteTime;
    private int mSecondStage;
    private Runnable mSetSecondStartRunnable;
    private Runnable mSetStartRunnable;
    private int mStandbyTime;
    Runnable mStopAScenesSelfLearn;
    private boolean mSystemSetting;
    private boolean mSystemTuning;
    private boolean mThermal;
    private boolean mThermalConfig;
    private int mThirdStage;
    private int mTime;
    private final IWindowManager mWindowManager;
    private Object performance;
    private Class<?> sPerfClass;
    private static ArrayMap<String, List<Integer>> mGeneralScenes = new ArrayMap<>(256);
    private static ArrayMap<Integer, String> mTopFive = new ArrayMap<>(5);
    private static List<Integer> mPowerRealB = new ArrayList();
    private static List<Integer> mPowerRealStandby = new ArrayList();
    private static int[] mArgs = {1095057408, 6, 1095057664, 6, 1095057920, 6, 1095057408, 50, 1095057664, 50, 1095041536, 50, 1086816256, 800};
    private static IMiCharge mMiCharge = null;
    private static int mLastType = 0;
    private static boolean mIsFeatureOn = false;
    private static boolean mIsNVT = true;

    private class IntentReceiver extends BroadcastReceiver {
        private void dealSleepModeChanged(int i2) throws Throwable {
            if (DynamicTurboPowerHandler.mIsNVT) {
                if (i2 == 2) {
                    DynamicTurboPowerHandler dynamicTurboPowerHandler = DynamicTurboPowerHandler.this;
                    dynamicTurboPowerHandler.removeCallbacks(dynamicTurboPowerHandler.mAScenesRunnable);
                    DynamicTurboPowerHandler.this.writeLocalLog("-------------------STATE_EXIT_SLEEP---------");
                    DynamicTurboPowerHandler.this.mEnergyLearning.setStart_B();
                    Runnable runnable = new Runnable() { // from class: com.miui.powerkeeper.statemachine.DynamicTurboPowerHandler.IntentReceiver.1
                        @Override // java.lang.Runnable
                        public void run() throws Throwable {
                            DynamicTurboPowerHandler.this.mEnergyLearning.setStart_B();
                        }
                    };
                    DynamicTurboPowerHandler.this.removeCallbacks(runnable);
                    DynamicTurboPowerHandler.this.postDelayed(runnable, 2000L);
                    return;
                }
                if (i2 == 1) {
                    DynamicTurboPowerHandler dynamicTurboPowerHandler2 = DynamicTurboPowerHandler.this;
                    dynamicTurboPowerHandler2.removeCallbacks(dynamicTurboPowerHandler2.mAScenesRunnable);
                    DynamicTurboPowerHandler.this.writeLocalLog("--------------------STATE_ENTER_SLEEP---------");
                    DynamicTurboPowerHandler.this.mEnergyLearning.setReport_B();
                    Runnable runnable2 = new Runnable() { // from class: com.miui.powerkeeper.statemachine.DynamicTurboPowerHandler.IntentReceiver.2
                        @Override // java.lang.Runnable
                        public void run() throws Throwable {
                            int pReal_B = DynamicTurboPowerHandler.this.mEnergyLearning.getPReal_B();
                            DynamicTurboPowerHandler.this.writeLocalLog("StandbyPower = " + pReal_B);
                            DynamicTurboPowerHandler.mPowerRealStandby = DynamicTurboPowerHandler.this.readStandbyFromDb();
                            if (DynamicTurboPowerHandler.mPowerRealStandby.size() == 5) {
                                DynamicTurboPowerHandler.mPowerRealStandby.remove(0);
                            }
                            DynamicTurboPowerHandler.mPowerRealStandby.add(Integer.valueOf(pReal_B));
                            DynamicTurboPowerHandler.this.writeStandbyToDb(DynamicTurboPowerHandler.mPowerRealStandby);
                            DynamicTurboPowerHandler.this.setStandbyPowerToEndTime();
                        }
                    };
                    DynamicTurboPowerHandler.this.removeCallbacks(runnable2);
                    DynamicTurboPowerHandler.this.postDelayed(runnable2, 2000L);
                }
            }
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) throws Throwable {
            if ("com.miui.powerkeeper_sleep_changed".equals(intent != null ? intent.getAction() : null)) {
                dealSleepModeChanged(intent.getIntExtra(YellowPageContract.Permission.STATE, -1));
            }
        }

        private IntentReceiver() {
        }
    }

    public class batteryInfo {
        int currentDetection = 0;
        int tempDetection = 0;

        public batteryInfo() {
        }

        public int getCurrentDetection(String str) throws Throwable {
            String node = DynamicTurboPowerHandler.this.readNode(str);
            if (node != null) {
                this.currentDetection = Integer.parseInt(node.trim());
            }
            return this.currentDetection;
        }
    }

    public class batteryProtection {
        int currentPeak = 0;
        int powerPeak = 0;
        int overPeakFlag = 0;
        int currentDeviation = 0;
        int powerDeviation = 0;
        int currentDetectionPeak = 0;
        int powerDetectionPeak = 0;

        public batteryProtection() {
        }

        public int getCurrentDetectionPeak() throws Throwable {
            String node = DynamicTurboPowerHandler.this.readNode("");
            if (node != null) {
                this.currentDetectionPeak = Integer.parseInt(node.trim());
            }
            return this.currentDetectionPeak;
        }

        public int getCurrentPeak() throws Throwable {
            String node = DynamicTurboPowerHandler.this.readNode("");
            if (node != null) {
                this.currentPeak = Integer.parseInt(node.trim());
            }
            return this.currentPeak;
        }

        public int getPowerDetectionPeak() throws Throwable {
            String node = DynamicTurboPowerHandler.this.readNode("");
            if (node != null) {
                this.powerDetectionPeak = Integer.parseInt(node.trim());
            }
            return this.powerDetectionPeak;
        }

        public int getPowerPeak() throws Throwable {
            String node = DynamicTurboPowerHandler.this.readNode("");
            if (node != null) {
                this.powerPeak = Integer.parseInt(node.trim());
            }
            return this.powerPeak;
        }
    }

    public class energyLearning {
        int tEndReal_B;
        int pReal_A = 0;
        int pDev_A = 0;
        int tEndDev_A = 0;
        int tEndEva_A = 0;
        int tEndReal_A = 0;
        int pReal_B = 0;
        int pDev_B = 0;
        int tEndDev_B = 0;
        int tEndEva_B = 0;
        int powerToEnd = 0;
        int timeToEnd = 0;
        int refPower = 0;
        int maxCurrent = 0;
        int maxPower = 0;

        public energyLearning() {
        }

        public int getPReal_A(String str) throws Throwable {
            String node = DynamicTurboPowerHandler.this.readNode(str);
            if (node != null) {
                this.pReal_A = Integer.parseInt(node.trim());
            }
            DynamicTurboPowerHandler.this.writeLocalLog("getPReal_A is " + this.pReal_A);
            return this.pReal_A;
        }

        public int getPReal_B() throws Throwable {
            String node;
            if (DynamicTurboPowerHandler.this.mIsMiChargeOn) {
                this.pReal_B = DynamicTurboPowerHandler.this.getMiChargeDataToInt(DynamicTurboPowerHandler.MICHARGE_PATH_GET_LEARN_POWER_B);
                DynamicTurboPowerHandler.this.writeLocalLog("pReal_B before is " + this.pReal_B);
                if (DynamicTurboPowerHandler.this.mIsFeatureFlip) {
                    this.pReal_B += DynamicTurboPowerHandler.this.getMiChargeDataToInt("");
                }
            } else {
                String node2 = DynamicTurboPowerHandler.this.readNode(DynamicTurboPowerHandler.PATH_GET_LEARN_POWER_B);
                if (node2 != null) {
                    this.pReal_B = Integer.parseInt(node2.trim());
                }
                DynamicTurboPowerHandler.this.writeLocalLog("pReal_B before is " + this.pReal_B);
                if (DynamicTurboPowerHandler.this.mIsFeatureFlip && (node = DynamicTurboPowerHandler.this.readNode("sys/class/qcom-battery/fg2_stop_learn_b")) != null) {
                    this.pReal_B += Integer.parseInt(node.trim());
                }
            }
            DynamicTurboPowerHandler.this.writeLocalLog("pReal_B is " + this.pReal_B);
            return this.pReal_B;
        }

        public int getTimeToEnd() throws Throwable {
            if (DynamicTurboPowerHandler.this.DBG_POWER) {
                Log.d(DynamicTurboPowerHandler.TAG, "getTimeToEnd mIsFeatureFlip is " + DynamicTurboPowerHandler.this.mIsFeatureFlip);
            }
            if (DynamicTurboPowerHandler.this.mIsMiChargeOn) {
                this.timeToEnd = DynamicTurboPowerHandler.this.getMiChargeDataToInt(DynamicTurboPowerHandler.MICHARGE_PATH_BASE_REMAINING_TIME);
                if (DynamicTurboPowerHandler.this.mIsFeatureFlip) {
                    int miChargeDataToInt = DynamicTurboPowerHandler.this.getMiChargeDataToInt("");
                    int i2 = this.timeToEnd;
                    this.timeToEnd = (i2 + miChargeDataToInt) / 2;
                    DynamicTurboPowerHandler.this.writeLocalLog("timeToEnd is " + this.timeToEnd + ", base time is " + i2 + ", flip time is " + miChargeDataToInt);
                }
            } else {
                String node = DynamicTurboPowerHandler.this.readNode(DynamicTurboPowerHandler.PATH_BASE_REMAINING_TIME);
                if (node != null) {
                    this.timeToEnd = Integer.parseInt(node.trim());
                }
                if (DynamicTurboPowerHandler.this.mIsFeatureFlip) {
                    String node2 = DynamicTurboPowerHandler.this.readNode(DynamicTurboPowerHandler.PATH_FLIP_REMAINING_TIME);
                    if (node != null && node2 != null) {
                        int i3 = Integer.parseInt(node2.trim());
                        this.timeToEnd = (this.timeToEnd + i3) / 2;
                        DynamicTurboPowerHandler.this.writeLocalLog("timeToEnd is " + this.timeToEnd + ", base time is " + Integer.parseInt(node.trim()) + ", flip time is " + i3);
                    }
                }
            }
            if (DynamicTurboPowerHandler.this.DBG_POWER) {
                Log.d(DynamicTurboPowerHandler.TAG, "mIsFeature32V is " + DynamicTurboPowerHandler.this.mIsFeature32V + ", mCurrentLevel is " + DynamicTurboPowerHandler.this.mCurrentLevel);
            }
            if (DynamicTurboPowerHandler.this.mIsFeature32V && DynamicTurboPowerHandler.this.mCurrentLevel <= 10) {
                float f2 = DynamicTurboPowerHandler.this.mCurrentLevel / (DynamicTurboPowerHandler.this.mCurrentLevel + 2.0f);
                DynamicTurboPowerHandler.this.writeLocalLog("3.2V correct time = " + this.timeToEnd + ", coefficient is " + f2);
                this.timeToEnd = (int) (((float) this.timeToEnd) * f2);
            }
            return this.timeToEnd;
        }

        public void setAScenesPowerToEnd(String str, int i2) throws Throwable {
            DynamicTurboPowerHandler.this.writeFile(str, i2);
        }

        public void setPowerToEnd(int i2) throws Throwable {
            double batteryCapacity;
            double batteryCapacity2;
            double batteryCapacity3;
            double batteryCapacity4;
            DynamicTurboPowerHandler.this.writeLocalLog("setPowerToEnd power is " + i2 + ", mIsFeatureFlip is " + DynamicTurboPowerHandler.this.mIsFeatureFlip);
            if (!DynamicTurboPowerHandler.this.mIsFeatureFlip) {
                if (DynamicTurboPowerHandler.this.mIsMiChargeOn) {
                    DynamicTurboPowerHandler.this.setMiChargeData(DynamicTurboPowerHandler.MICHARGE_PATH_BASE_POWER, Integer.toString(i2));
                    return;
                } else {
                    DynamicTurboPowerHandler.this.writeFile(DynamicTurboPowerHandler.PATH_BASE_POWER, i2);
                    return;
                }
            }
            if (DynamicTurboPowerHandler.this.mIsMiChargeOn) {
                batteryCapacity = DynamicTurboPowerHandler.this.getMiChargeDataToDouble(DynamicTurboPowerHandler.MICHARGE_PATH_GET_DESIGN_CAPACITY);
                batteryCapacity2 = DynamicTurboPowerHandler.this.getMiChargeDataToDouble(DynamicTurboPowerHandler.MICHARGE_PATH_FLIP_GET_DESIGN_CAPACITY);
                batteryCapacity3 = DynamicTurboPowerHandler.this.getMiChargeDataToDouble("");
                batteryCapacity4 = DynamicTurboPowerHandler.this.getMiChargeDataToDouble("");
            } else {
                batteryCapacity = DynamicTurboPowerHandler.this.getBatteryCapacity(DynamicTurboPowerHandler.PATH_GET_DESIGN_CAPACITY);
                batteryCapacity2 = DynamicTurboPowerHandler.this.getBatteryCapacity(DynamicTurboPowerHandler.PATH_FLIP_GET_DESIGN_CAPACITY);
                batteryCapacity3 = DynamicTurboPowerHandler.this.getBatteryCapacity(DynamicTurboPowerHandler.PATH_GET_REMAINING_CAPACITY);
                batteryCapacity4 = DynamicTurboPowerHandler.this.getBatteryCapacity(DynamicTurboPowerHandler.PATH_FLIP_GET_REMAINING_CAPACITY);
            }
            DynamicTurboPowerHandler.this.writeLocalLog("setPowerToEnd baseDesignCapacity is " + batteryCapacity + ", flipDesignCapacity is " + batteryCapacity2 + ", baseRmCapacity is " + batteryCapacity3 + ", flipRmCapacity is " + batteryCapacity4 + ", mIsMiChargeOn is " + DynamicTurboPowerHandler.this.mIsMiChargeOn);
            if (batteryCapacity == 0.0d || batteryCapacity2 == 0.0d) {
                return;
            }
            double d2 = batteryCapacity / (batteryCapacity2 + batteryCapacity);
            double d3 = i2;
            int i3 = (int) (d3 * d2);
            double d4 = 1.0d - d2;
            int i4 = (int) (d3 * d4);
            if (DynamicTurboPowerHandler.this.mIsMiChargeOn) {
                DynamicTurboPowerHandler.this.setMiChargeData(DynamicTurboPowerHandler.MICHARGE_PATH_BASE_POWER, Integer.toString(i3));
                DynamicTurboPowerHandler.this.setMiChargeData("", Integer.toString(i4));
            } else {
                DynamicTurboPowerHandler.this.writeFile(DynamicTurboPowerHandler.PATH_BASE_POWER, i3);
                DynamicTurboPowerHandler.this.writeFile(DynamicTurboPowerHandler.PATH_FLIP_POWER, i4);
            }
            DynamicTurboPowerHandler.this.writeLocalLog("setPowerToEnd basePower is " + i3 + ", flipPower is " + i4 + ", rate is " + d2 + ", other is " + d4);
        }

        public void setReport_A() throws Throwable {
            DynamicTurboPowerHandler.this.writeLocalLog("setReport_A");
            if (DynamicTurboPowerHandler.this.mIsMiChargeOn) {
                DynamicTurboPowerHandler.this.setMiChargeData(DynamicTurboPowerHandler.MICHARGE_PATH_STOP_LEARN, Integer.toString(1));
                if (DynamicTurboPowerHandler.this.mIsFeatureFlip) {
                    DynamicTurboPowerHandler.this.setMiChargeData("", Integer.toString(1));
                    return;
                }
                return;
            }
            DynamicTurboPowerHandler.this.writeFile(DynamicTurboPowerHandler.PATH_STOP_LEARN, 1);
            if (DynamicTurboPowerHandler.this.mIsFeatureFlip) {
                DynamicTurboPowerHandler.this.writeFile(DynamicTurboPowerHandler.PATH_FLIP_STOP_LEARN, 1);
            }
        }

        public void setReport_B() throws Throwable {
            DynamicTurboPowerHandler.this.writeLocalLog("setReport_B");
            if (DynamicTurboPowerHandler.this.mIsMiChargeOn) {
                DynamicTurboPowerHandler.this.setMiChargeData(DynamicTurboPowerHandler.MICHARGE_PATH_STOP_LEARN_B, Integer.toString(1));
                if (DynamicTurboPowerHandler.this.mIsFeatureFlip) {
                    DynamicTurboPowerHandler.this.setMiChargeData("", Integer.toString(1));
                    return;
                }
                return;
            }
            DynamicTurboPowerHandler.this.writeFile(DynamicTurboPowerHandler.PATH_STOP_LEARN_B, 1);
            if (DynamicTurboPowerHandler.this.mIsFeatureFlip) {
                DynamicTurboPowerHandler.this.writeFile("sys/class/qcom-battery/fg2_stop_learn_b", 1);
            }
        }

        public void setStart_A() throws Throwable {
            DynamicTurboPowerHandler.this.writeLocalLog("setStart_A");
            if (DynamicTurboPowerHandler.this.mIsMiChargeOn) {
                DynamicTurboPowerHandler.this.setMiChargeData(DynamicTurboPowerHandler.MICHARGE_PATH_START_LEARN, "1");
                if (DynamicTurboPowerHandler.this.mIsFeatureFlip) {
                    DynamicTurboPowerHandler.this.setMiChargeData("", "1");
                    return;
                }
                return;
            }
            DynamicTurboPowerHandler.this.writeFile(DynamicTurboPowerHandler.PATH_START_LEARN, 1);
            if (DynamicTurboPowerHandler.this.mIsFeatureFlip) {
                DynamicTurboPowerHandler.this.writeFile(DynamicTurboPowerHandler.PATH_FLIP_START_LEARN, 1);
            }
        }

        public void setStart_B() throws Throwable {
            DynamicTurboPowerHandler.this.writeLocalLog("setStart_B");
            if (DynamicTurboPowerHandler.this.mIsMiChargeOn) {
                DynamicTurboPowerHandler.this.setMiChargeData(DynamicTurboPowerHandler.MICHARGE_PATH_START_LEARN_B, "1");
                if (DynamicTurboPowerHandler.this.mIsFeatureFlip) {
                    DynamicTurboPowerHandler.this.setMiChargeData("", "1");
                    return;
                }
                return;
            }
            DynamicTurboPowerHandler.this.writeFile(DynamicTurboPowerHandler.PATH_START_LEARN_B, 1);
            if (DynamicTurboPowerHandler.this.mIsFeatureFlip) {
                DynamicTurboPowerHandler.this.writeFile(DynamicTurboPowerHandler.PATH_FLIP_START_LEARN_B, 1);
            }
        }
    }

    private DynamicTurboPowerHandler(Context context, Looper looper) throws Throwable {
        super(looper);
        this.DBG_POWER = Log.isLoggable("power.dynamic", 3);
        this.MSG_CONFIG_UPDATE = 1;
        this.MSG_TIMEDELAY_B = 2;
        this.MSG_TIMEDELAY_A = 3;
        this.mIsMiChargeOn = false;
        this.mForegroundInfo = null;
        this.mIntentReceiver = null;
        this.performance = null;
        this.sPerfClass = null;
        this.mPerfHint = null;
        this.mDolbyAudio = null;
        this.mLocalLog = new LocalLog(256);
        this.mPowerMode = 2;
        this.mPostDelay = 20000;
        this.mAPostDelay = 20000;
        this.mAStartPostDelay = 110000;
        this.mAccumulateSize = 5;
        this.mCount = 0;
        this.mB_Count = 1;
        this.mAll_Count = 0;
        this.mAveragePower = 0;
        this.mBAveragePower = 0;
        this.mAllAveragePower = 0;
        this.mActualPower = 0;
        this.mLastActualPower = 0;
        this.mTime = 0;
        this.mAllTime = 0;
        this.mStandbyTime = 0;
        this.mLastTime = 0;
        this.B_SCENE_COUNT_WINDOW = 30;
        this.B_SCENE_TIME_WINDOW = 10;
        this.mFirstStage = 30;
        this.mSecondStage = 20;
        this.mThirdStage = 5;
        this.NORMAL_MODULUS = 1.0d;
        this.PERFORMANCE_MODULUS = 0.949999988079071d;
        this.NORMAL_SAVE_MODULUS = 1.1299999952316284d;
        this.ULTIMATE_SAVE_MODULUS = 2.7300000190734863d;
        this.mAllDayAveragePower = 2200;
        this.mSatelliteAveragePower = 3500;
        this.mIsInCall = false;
        this.mSatelliteTime = 0;
        this.mThermalConfig = false;
        this.mDisplayIdle = false;
        this.mFiveG = false;
        this.mLowSound = false;
        this.mSystemTuning = false;
        this.mIsPerfHind = false;
        this.mOnBattery = true;
        this.mAScenesStatus = false;
        this.mThermal = false;
        this.mDisplay = false;
        this.mNeedRelease = false;
        this.mSystemSetting = false;
        this.mEstimatePower = 0;
        this.mLastBatteryTime = 0.0d;
        this.mIsFeature32V = false;
        this.mIsFeatureFlip = false;
        this.mIsFeatureBatteryLife = false;
        this.mBScenesRunnable = new Runnable() { // from class: com.miui.powerkeeper.statemachine.DynamicTurboPowerHandler.2
            @Override // java.lang.Runnable
            public void run() throws Throwable {
                int i2;
                int currentDetection;
                double currentVoltage;
                int currentDetection2;
                double currentVoltage2;
                try {
                    if (DynamicTurboPowerHandler.mIsFeatureOn && DynamicTurboPowerHandler.mIsNVT) {
                        DynamicTurboPowerHandler dynamicTurboPowerHandler = DynamicTurboPowerHandler.this;
                        dynamicTurboPowerHandler.postDelayed(this, dynamicTurboPowerHandler.mPostDelay);
                        if (DynamicTurboPowerHandler.this.mIsFeatureFlip) {
                            if (DynamicTurboPowerHandler.this.mIsMiChargeOn) {
                                currentDetection = DynamicTurboPowerHandler.this.getMiChargeDataToInt(DynamicTurboPowerHandler.MICHARGE_PATH_BASE_AVERAGE);
                                currentVoltage = DynamicTurboPowerHandler.this.getMiChargeDataToDouble("");
                                currentDetection2 = DynamicTurboPowerHandler.this.getMiChargeDataToInt("");
                                currentVoltage2 = DynamicTurboPowerHandler.this.getMiChargeDataToDouble("");
                            } else {
                                currentDetection = DynamicTurboPowerHandler.this.mBatteryInfo.getCurrentDetection(DynamicTurboPowerHandler.PATH_BASE_AVERAGE);
                                currentVoltage = DynamicTurboPowerHandler.this.getCurrentVoltage(DynamicTurboPowerHandler.PATH_BASE_CURRENT_VOLTAGE);
                                currentDetection2 = DynamicTurboPowerHandler.this.mBatteryInfo.getCurrentDetection(DynamicTurboPowerHandler.PATH_FLIP_AVERAGE);
                                currentVoltage2 = DynamicTurboPowerHandler.this.getCurrentVoltage(DynamicTurboPowerHandler.PATH_FLIP_CURRENT_VOLTAGE);
                            }
                            DynamicTurboPowerHandler.this.writeLocalLog("Flip actualCurrent is " + currentDetection + ", actualBaseVolage is " + currentVoltage + ", actualFlipCurrent is " + currentDetection2 + ", actualFlipVolage is " + currentVoltage2 + ", mIsMiChargeOn is " + DynamicTurboPowerHandler.this.mIsMiChargeOn);
                            double d2 = ((double) currentDetection) * currentVoltage;
                            double d3 = ((double) currentDetection2) * currentVoltage2;
                            i2 = (d2 == 0.0d || d3 == 0.0d) ? 0 : ((int) d2) + ((int) d3);
                        } else {
                            int miChargeDataToInt = DynamicTurboPowerHandler.this.mIsMiChargeOn ? DynamicTurboPowerHandler.this.getMiChargeDataToInt(DynamicTurboPowerHandler.MICHARGE_PATH_BASE_AVERAGE) : DynamicTurboPowerHandler.this.mBatteryInfo.getCurrentDetection(DynamicTurboPowerHandler.PATH_BASE_AVERAGE);
                            double currentVoltage3 = DynamicTurboPowerHandler.this.getCurrentVoltage("/sys/class/power_supply/battery/voltage_now");
                            DynamicTurboPowerHandler.this.writeLocalLog("actualCurrent is " + miChargeDataToInt + ", actualBaseVolage is " + currentVoltage3);
                            i2 = (int) (((double) miChargeDataToInt) * currentVoltage3);
                        }
                        if (i2 == 0) {
                            return;
                        }
                        DynamicTurboPowerHandler.this.mBAveragePower += i2;
                        if (DynamicTurboPowerHandler.this.mB_Count % DynamicTurboPowerHandler.this.B_SCENE_COUNT_WINDOW == 0) {
                            DynamicTurboPowerHandler.this.mBAveragePower /= DynamicTurboPowerHandler.this.B_SCENE_COUNT_WINDOW;
                            DynamicTurboPowerHandler.this.writeLocalLog("--------mB_Count =" + DynamicTurboPowerHandler.this.mB_Count + " mBAveragePower =" + DynamicTurboPowerHandler.this.mBAveragePower);
                            DynamicTurboPowerHandler dynamicTurboPowerHandler2 = DynamicTurboPowerHandler.this;
                            dynamicTurboPowerHandler2.allSceneTimeRetry(dynamicTurboPowerHandler2.mBAveragePower);
                            DynamicTurboPowerHandler dynamicTurboPowerHandler3 = DynamicTurboPowerHandler.this;
                            dynamicTurboPowerHandler3.recordBrightScreenPower(dynamicTurboPowerHandler3.mBAveragePower);
                            DynamicTurboPowerHandler.this.mBAveragePower = 0;
                        }
                        DynamicTurboPowerHandler.this.mB_Count++;
                    }
                } catch (Exception unused) {
                    Log.e(DynamicTurboPowerHandler.TAG, "bscenes runnable crash");
                }
            }
        };
        this.mSetStartRunnable = new Runnable() { // from class: com.miui.powerkeeper.statemachine.DynamicTurboPowerHandler.3
            @Override // java.lang.Runnable
            public void run() throws Throwable {
                if (DynamicTurboPowerHandler.mIsFeatureOn && DynamicTurboPowerHandler.mIsNVT) {
                    DynamicTurboPowerHandler.this.mEnergyLearning.setStart_A();
                }
            }
        };
        this.mSetSecondStartRunnable = new Runnable() { // from class: com.miui.powerkeeper.statemachine.DynamicTurboPowerHandler.4
            @Override // java.lang.Runnable
            public void run() throws Throwable {
                if (DynamicTurboPowerHandler.mIsFeatureOn && DynamicTurboPowerHandler.mIsNVT) {
                    DynamicTurboPowerHandler.this.mEnergyLearning.setStart_A();
                }
            }
        };
        this.mAScenesRunnable = new Runnable() { // from class: com.miui.powerkeeper.statemachine.DynamicTurboPowerHandler.5
            @Override // java.lang.Runnable
            public void run() throws Throwable {
                if (DynamicTurboPowerHandler.mIsFeatureOn && DynamicTurboPowerHandler.mIsNVT) {
                    DynamicTurboPowerHandler dynamicTurboPowerHandler = DynamicTurboPowerHandler.this;
                    dynamicTurboPowerHandler.postDelayed(this, dynamicTurboPowerHandler.mAPostDelay);
                    if (DynamicTurboPowerHandler.this.mIsInCall) {
                        DynamicTurboPowerHandler dynamicTurboPowerHandler2 = DynamicTurboPowerHandler.this;
                        dynamicTurboPowerHandler2.startAScenesSelfLearningSatellite(dynamicTurboPowerHandler2.mSatelliteAveragePower);
                    } else if (DynamicTurboPowerHandler.this.mIsFeatureFlip) {
                        DynamicTurboPowerHandler dynamicTurboPowerHandler3 = DynamicTurboPowerHandler.this;
                        dynamicTurboPowerHandler3.startAScenesSelfLearningFlip(dynamicTurboPowerHandler3.mEstimatePower);
                    } else {
                        DynamicTurboPowerHandler dynamicTurboPowerHandler4 = DynamicTurboPowerHandler.this;
                        dynamicTurboPowerHandler4.startAScenesSelfLearning(dynamicTurboPowerHandler4.mEstimatePower);
                    }
                }
            }
        };
        this.mStopAScenesSelfLearn = new Runnable() { // from class: com.miui.powerkeeper.statemachine.a
            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                this.f556a.lambda$new$0();
            }
        };
        Log.i(TAG, "DynamicTurboPowerHandler constructive");
        this.mContext = context;
        this.mWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
        initMiCharge();
        this.mBatteryInfo = new batteryInfo();
        this.mEnergyLearning = new energyLearning();
        this.mCurrentLevel = ((BatteryManager) context.getSystemService("batterymanager")).getIntProperty(4);
        this.mOnBattery = PowerKeeperManager.getInstance().isCharging();
        writeLocalLog("mCurrentLevel = " + this.mCurrentLevel + " mOnBattery = " + this.mOnBattery);
        EventsAggregator eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
        if (eventsAggregator != null) {
            this.mForegroundInfo = eventsAggregator.getForegroundInfo();
            eventsAggregator.registerForForegroundInfo(this);
            eventsAggregator.registerPowerModeListener(this);
            eventsAggregator.registerBatteryLevelListener(this);
            eventsAggregator.registerForCharging(this);
            eventsAggregator.registerForOffHookCallState(this);
        }
        registerCloudObserver();
        this.mIntentReceiver = new IntentReceiver();
        this.mContext.registerReceiver(this.mIntentReceiver, new IntentFilter("com.miui.powerkeeper_sleep_changed"), 2);
        qtiPerformance();
        updateConfig();
        restoreLowPowerPolicy();
        isNVTCloseStatistics();
        allScenePowerStatistics();
        setSatelliteTime(this.mSatelliteAveragePower, this.mCurrentLevel);
    }

    private void SocperfHint(int i2) {
        if (mIsFeatureOn && this.mIsPerfHind && mLastType != i2) {
            mLastType = i2;
            try {
                this.mPerfHint.invoke(this.performance, Integer.valueOf(VENDOR_SAVING_CLASSIFICATION_POWER_HINT_ID), null, -1, Integer.valueOf(i2));
                writeLocalLog("workType = " + i2 + "  saving mPerfHint.invoke true");
            } catch (IllegalAccessException e2) {
                Log.e(TAG, "Dynamic perfHint e:" + e2.getMessage());
            } catch (Exception e3) {
                Log.e(TAG, "Dynamic perfHint e:" + e3.getMessage());
            }
        }
    }

    private void allScenePowerStatistics() throws Throwable {
        if (!this.mOnBattery) {
            mIsNVT = isSupportNVT();
            int i2 = this.mCurrentLevel;
            this.mAfterBatteryLevel = i2;
            this.mB_Count = 1;
            this.mAllTime = 0;
            this.mBAveragePower = 0;
            this.mAll_Count = 0;
            this.mAllAveragePower = 0;
            this.mCount = 0;
            this.mAveragePower = 0;
            if (i2 <= 10 && i2 > 2) {
                this.B_SCENE_COUNT_WINDOW = 15;
                this.B_SCENE_TIME_WINDOW = 5;
            } else if (i2 <= 2) {
                this.B_SCENE_COUNT_WINDOW = 3;
                this.B_SCENE_TIME_WINDOW = 10;
            } else {
                this.B_SCENE_COUNT_WINDOW = 30;
                this.B_SCENE_TIME_WINDOW = 10;
            }
            postDelayed(this.mBScenesRunnable, this.mPostDelay);
            postDelayedAScenesRunnable();
            allSceneTimeCorrection();
            return;
        }
        mIsNVT = false;
        this.mBeforeBatteryLevel = this.mCurrentLevel;
        removeAScenesRunnable();
        removeCallbacks(this.mBScenesRunnable);
        writeLocalLog("mAll_Count is " + this.mAll_Count);
        int i3 = this.mAll_Count;
        if (i3 > 5) {
            if (this.mIsFeatureBatteryLife) {
                this.mAllAveragePower = getBatteryLifePower();
            } else {
                this.mAllAveragePower /= i3;
            }
            int i4 = this.mAllAveragePower;
            double d2 = i4;
            int i5 = this.mAllDayAveragePower;
            if (d2 > ((double) i5) * 0.8d && i4 < ((double) i5) * 1.2d) {
                if (mPowerRealB.size() == 5) {
                    mPowerRealB.remove(0);
                }
                mPowerRealB.add(Integer.valueOf(this.mAllAveragePower));
                writeAllDayToDb(mPowerRealB);
            }
        }
        this.mLastBatteryTime = this.mAllTime;
    }

    private void allSceneTimeCorrection() throws Throwable {
        List<Integer> allDayFromDb = readAllDayFromDb();
        mPowerRealB = allDayFromDb;
        int size = 0;
        if (!allDayFromDb.isEmpty()) {
            int iIntValue = 0;
            while (size < mPowerRealB.size()) {
                iIntValue += mPowerRealB.get(size).intValue();
                size++;
            }
            size = (iIntValue + this.mAllDayAveragePower) / (mPowerRealB.size() + 1);
            writeLocalLog("allSceneTimeCorrection averyPower =  " + size);
        }
        if (size == 0) {
            size = this.mAllDayAveragePower;
        }
        allSceneTimeRetry(size);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void allSceneTimeRetry(int i2) throws Throwable {
        writeLocalLog("allSceneTimeRetry power =  " + i2);
        this.mEnergyLearning.setPowerToEnd(i2);
        sendMessageDelayed(obtainMessage(2, Integer.valueOf(i2)), 1500L);
    }

    private void applyLowPowerPolicy(int i2) {
        if (mIsFeatureOn) {
            writeLocalLog("applyLowPowerPolicy :");
            if (i2 <= this.mFirstStage) {
                if (!this.mThermal) {
                    alterThermalConfig(1);
                }
                if (!this.mNeedRelease) {
                    systemTuning();
                }
                if (this.mPowerMode == 3) {
                    setSynchronizeStatus(this.mContext, false);
                    closeAutoUpdateMode(this.mContext);
                    closeAutoDownloadMode(this.mContext);
                }
                lowerAudio(true);
            }
            if (i2 <= this.mThirdStage) {
                if (this.mSystemSetting && this.mPowerMode == 4) {
                    setGpsStatus(this.mContext, false);
                    closeFingerAodMode(this.mContext);
                    closeSoundMode(this.mContext);
                    closePickupWakeupMode(this.mContext);
                    closeTapVibrateMode(this.mContext);
                    closeLightPulseMode(this.mContext);
                    closeAodMode(this.mContext);
                    closeFiveGMode(this.mContext);
                    closeSleepSecondsMode(this.mContext);
                }
                clearApp();
                limitTopAudio(true);
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
        if (this.DBG_POWER) {
            Slog.d(TAG, "clearApp whiteList=" + activePkgList);
        }
        try {
            writeLocalLog("clearApp true");
            ProcessManager.kill(processConfig);
        } catch (Exception e2) {
            Slog.e(TAG, "clearApp", e2);
        }
        PowerStateMachine.clearUnactiveApps(this.mContext);
    }

    private void closeAodMode(Context context) {
        int i2 = Settings.System.getInt(context.getContentResolver(), LOW_POWER_AOD_ENABLE, -1);
        int i3 = Settings.Secure.getInt(context.getContentResolver(), SETTING_AOD_MODE, -1);
        if (i2 != -1 || i3 == 0) {
            return;
        }
        Settings.System.putInt(context.getContentResolver(), LOW_POWER_AOD_ENABLE, i3);
        Settings.Secure.putInt(context.getContentResolver(), SETTING_AOD_MODE, 0);
        writeLocalLog("Aod Mode status: false");
    }

    private void closeAutoDownloadMode(Context context) {
        int i2 = Settings.System.getInt(context.getContentResolver(), LOW_POWER_AUTO_DOWNLOAD_ENABLE, -1);
        int i3 = Settings.Secure.getInt(context.getContentResolver(), SETTING_AUTO_DOWNLOAD, 1);
        if (i2 == -1) {
            Settings.System.putInt(context.getContentResolver(), LOW_POWER_AUTO_DOWNLOAD_ENABLE, i3);
        }
        if (i3 != 0) {
            Settings.Secure.putInt(context.getContentResolver(), SETTING_AUTO_DOWNLOAD, 0);
            writeLocalLog("Auto Download status: false");
        }
    }

    private void closeAutoUpdateMode(Context context) {
        int i2 = Settings.System.getInt(context.getContentResolver(), LOW_POWER_AUTO_UPDATE_ENABLE, -1);
        int i3 = Settings.Secure.getInt(context.getContentResolver(), SETTING_AUTO_UPDATE, 1);
        if (i2 == -1) {
            Settings.System.putInt(context.getContentResolver(), LOW_POWER_AUTO_UPDATE_ENABLE, i3);
        }
        if (i3 != 0) {
            Settings.Secure.putInt(context.getContentResolver(), SETTING_AUTO_UPDATE, 0);
            writeLocalLog("Auto Update status: false");
        }
    }

    private void closeDolbyAudio() {
        if (isSupportDolbyDax() && Settings.System.getInt(this.mContext.getContentResolver(), LOW_POWER_LOWER_DOLBY_AUDIO_ENABLE, -1) != 1) {
            writeLocalLog("closeDolbyAudio");
            this.mDolbyAudio = new a.a(0, 0);
            AudioManager audioManager = (AudioManager) this.mContext.getSystemService("audio");
            this.mAudioManager = audioManager;
            audioManager.setParameter("dolby_video_tool_enable", "0");
            this.mDolbyAudio.k(0);
            this.mDolbyAudio.i(false);
            MiSound miSound = new MiSound(0, 0);
            this.mMiSound = miSound;
            miSound.setEnabled(true);
            Settings.System.putInt(this.mContext.getContentResolver(), LOW_POWER_LOWER_DOLBY_AUDIO_ENABLE, 1);
        }
    }

    private void closeFingerAodMode(Context context) {
        if (isGxzwSensor() && getEnrolledFingerprintsCount(context) > 0 && UserHandle.myUserId() == 0) {
            int i2 = Settings.System.getInt(context.getContentResolver(), LOW_POWER_FINGER_AOD_ENABLE, -1);
            int i3 = Settings.Secure.getInt(context.getContentResolver(), SETTING_ICON_AOD_SHOW, 1);
            if (i2 == -1) {
                Settings.System.putInt(context.getContentResolver(), LOW_POWER_FINGER_AOD_ENABLE, i3);
            }
            if (i3 != 0) {
                Settings.Secure.putInt(context.getContentResolver(), SETTING_ICON_AOD_SHOW, 0);
                writeLocalLog("FingerAod status: false");
            }
        }
    }

    private void closeFiveGMode(Context context) {
        if (supportFiveGCapability() && isSupportFiveGEnabled(context) && !isNoSimCard()) {
            int i2 = Settings.System.getInt(context.getContentResolver(), LOW_POWER_IS_5G_ENABLE, -1);
            boolean zIsFiveGModePrefEnabled = isFiveGModePrefEnabled(context);
            if (-1 == i2) {
                Settings.System.putInt(context.getContentResolver(), LOW_POWER_IS_5G_ENABLE, zIsFiveGModePrefEnabled ? 1 : 0);
            }
            if (zIsFiveGModePrefEnabled) {
                setFiveGModeEnabled(false);
                writeLocalLog("5G status: true");
            }
        }
    }

    private void closeLightPulseMode(Context context) {
        int i2 = Settings.System.getInt(context.getContentResolver(), LOW_POWER_LIGHT_PULSE_ENABLE, -1);
        int intForUser = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "light_turn_on", 0, -2);
        if (i2 == -1) {
            Settings.System.putInt(context.getContentResolver(), LOW_POWER_LIGHT_PULSE_ENABLE, intForUser);
        }
        if (intForUser != 0) {
            Settings.Secure.putInt(this.mContext.getContentResolver(), "light_turn_on", 0);
            writeLocalLog("Light Pulse status: false");
        }
    }

    private void closePickupWakeupMode(Context context) {
        int i2 = Settings.System.getInt(context.getContentResolver(), LOW_POWER_WAKEUP_PICKUP, -1);
        boolean zIsPickupWakeupEnable = isPickupWakeupEnable(context);
        if (i2 == -1) {
            Settings.System.putInt(context.getContentResolver(), LOW_POWER_WAKEUP_PICKUP, zIsPickupWakeupEnable ? 1 : 0);
        }
        if (zIsPickupWakeupEnable) {
            MiuiSettings.System.putBooleanForUser(this.mContext.getContentResolver(), "pick_up_gesture_wakeup_mode", false, UserHandle.myUserId());
            writeLocalLog("Pickup Wakeup status: false");
        }
    }

    private void closeSleepSecondsMode(Context context) {
        int i2 = Settings.System.getInt(context.getContentResolver(), LOW_POWER_SLEEP_SECONDS_ENABLE, -1);
        int sleepSeconds = getSleepSeconds();
        if (i2 == -1) {
            Settings.System.putInt(context.getContentResolver(), LOW_POWER_SLEEP_SECONDS_ENABLE, sleepSeconds);
        }
        if (sleepSeconds != 15) {
            setSleepSeconds(context, 15);
            writeLocalLog("Sleep Seconds = 15");
        }
    }

    private void closeSoundMode(Context context) {
        DynamicTurboPowerHandler dynamicTurboPowerHandler;
        int i2 = Settings.System.getInt(context.getContentResolver(), LOW_POWER_SOUND_DIALER_ENABLE, -1);
        int i3 = Settings.System.getInt(context.getContentResolver(), LOW_POWER_SOUND_CLICK_ENABLE, -1);
        int i4 = Settings.System.getInt(context.getContentResolver(), LOW_POWER_SOUND_LOCK_ENABLE, -1);
        int i5 = Settings.System.getInt(context.getContentResolver(), LOW_POWER_SOUND_SCREENSHOT_ENABLE, -1);
        int i6 = Settings.System.getInt(context.getContentResolver(), LOW_POWER_SOUND_DELETE_ENABLE, -1);
        int i7 = Settings.System.getInt(context.getContentResolver(), SETTING_SOUND_DIALER, 1);
        int i8 = Settings.System.getInt(context.getContentResolver(), SETTING_SOUND_CLICK, 1);
        int i9 = Settings.System.getInt(context.getContentResolver(), SETTING_SOUND_LOCK, 1);
        int i10 = Settings.System.getInt(context.getContentResolver(), SETTING_SOUND_SCREENSHOT, 1);
        int i11 = Settings.System.getInt(context.getContentResolver(), SETTING_SOUND_DELETE, 1);
        if (i2 == -1) {
            Settings.System.putInt(context.getContentResolver(), LOW_POWER_SOUND_DIALER_ENABLE, i7);
        }
        if (i3 == -1) {
            Settings.System.putInt(context.getContentResolver(), LOW_POWER_SOUND_CLICK_ENABLE, i8);
        }
        if (i4 == -1) {
            Settings.System.putInt(context.getContentResolver(), LOW_POWER_SOUND_LOCK_ENABLE, i9);
        }
        if (i5 == -1) {
            Settings.System.putInt(context.getContentResolver(), LOW_POWER_SOUND_SCREENSHOT_ENABLE, i10);
        }
        if (i6 == -1) {
            Settings.System.putInt(context.getContentResolver(), LOW_POWER_SOUND_DELETE_ENABLE, i11);
        }
        if (i7 != 0) {
            Settings.System.putInt(context.getContentResolver(), SETTING_SOUND_DIALER, 0);
            dynamicTurboPowerHandler = this;
            dynamicTurboPowerHandler.writeLocalLog("Dialer Sound status: false");
        } else {
            dynamicTurboPowerHandler = this;
        }
        if (i8 != 0) {
            Settings.System.putInt(context.getContentResolver(), SETTING_SOUND_CLICK, 0);
            dynamicTurboPowerHandler.writeLocalLog("Click Sound status: false");
        }
        if (i9 != 0) {
            Settings.System.putInt(context.getContentResolver(), SETTING_SOUND_LOCK, 0);
            dynamicTurboPowerHandler.writeLocalLog("Lock Sound status: false");
        }
        if (i10 != 0) {
            Settings.System.putInt(context.getContentResolver(), SETTING_SOUND_SCREENSHOT, 0);
            dynamicTurboPowerHandler.writeLocalLog("Screenshot Sound status: false");
        }
        if (i11 != 0) {
            Settings.System.putInt(context.getContentResolver(), SETTING_SOUND_DELETE, 0);
            dynamicTurboPowerHandler.writeLocalLog("Delete Sound status: false");
        }
    }

    private void closeTapVibrateMode(Context context) {
        if (isHapTicLinear()) {
            return;
        }
        int i2 = Settings.System.getInt(context.getContentResolver(), LOW_POWER_HAPTIC_FEED_BACK_ENABLE, -1);
        int i3 = Settings.System.getInt(context.getContentResolver(), SETTING_HAPTIC_FEED_BACK, 1);
        if (i2 == -1) {
            Settings.System.putInt(context.getContentResolver(), LOW_POWER_HAPTIC_FEED_BACK_ENABLE, i3);
        }
        if (i3 != 0) {
            Settings.System.putInt(context.getContentResolver(), SETTING_HAPTIC_FEED_BACK, 0);
            writeLocalLog("Tap Vibrate status: false");
        }
    }

    private void disable() {
        unregisterReceiver();
        removeAScenesRunnable();
        removeCallbacks(this.mBScenesRunnable);
        Log.d(TAG, "DynamicTurboPowerHandler disable.");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public double getBatteryCapacity(String str) throws Throwable {
        String node = readNode(str);
        if (node != null) {
            return Integer.parseInt(node.trim());
        }
        return 0.0d;
    }

    private int getBatteryLifePower() {
        double d2 = 0.0d;
        try {
            BatteryData batteryData = (BatteryData) BLUtils.readFile(getFilePath());
            double totalBatteryCapacity = BSHelper.getTotalBatteryCapacity(this.mContext);
            if (batteryData != null && totalBatteryCapacity != -1.0d) {
                Iterator<BatteryLife> it = batteryData.mEstimateBatteryLifeDQ.iterator();
                double d3 = 0.0d;
                double d4 = 0.0d;
                while (it.hasNext()) {
                    try {
                        for (BatteryLife.Data data : it.next().dataList) {
                            double d5 = data.life[0];
                            if (d5 != 0.0d && data.powerMode == 100) {
                                double d6 = (((totalBatteryCapacity * 60.0d) * 60.0d) * DEFAULT_AVERAGE_VOLTAGE) / d5;
                                int i2 = this.mAllDayAveragePower;
                                if (d6 > ((double) i2) * 0.8d && d6 < ((double) i2) * 1.2d) {
                                    d4 += d6;
                                    writeLocalLog("timeScreenOn is " + d5 + ", batteryLifePower is " + d4 + ", batteryCap is " + totalBatteryCapacity);
                                    d3 += 1.0d;
                                }
                            }
                        }
                    } catch (Exception unused) {
                        d2 = d4;
                        Log.e(TAG, "get battery life error.");
                    }
                }
                if (d3 != 0.0d) {
                    d2 = d4 / d3;
                    writeLocalLog("batteryLifePower is " + d2 + ", dayCount is " + d3);
                    d4 = d2;
                }
                return (int) d4;
            }
            return (int) 0.0d;
        } catch (Exception unused2) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public double getCurrentVoltage(String str) throws Throwable {
        String node = readNode(str);
        if (node != null) {
            return ((double) Integer.parseInt(node.trim())) * 1.0E-6d;
        }
        return 0.0d;
    }

    private int getEnrolledFingerprintsCount(Context context) {
        FingerprintManager fingerprintManager = (FingerprintManager) context.getApplicationContext().getSystemService("fingerprint");
        if (fingerprintManager == null) {
            return 0;
        }
        try {
            List enrolledFingerprints = fingerprintManager.getEnrolledFingerprints();
            if (enrolledFingerprints == null || enrolledFingerprints.isEmpty()) {
                return 0;
            }
            return enrolledFingerprints.size();
        } catch (Exception e2) {
            Log.e(TAG, "getEnrolledFingerprintsCount exception: ", e2);
            return 0;
        }
    }

    private String getFilePath() {
        return this.mContext.getCacheDir().getAbsolutePath() + "/battery_life_data";
    }

    private int getGps(Context context) {
        try {
            return Settings.Secure.getInt(context.getContentResolver(), "location_mode");
        } catch (Settings.SettingNotFoundException e2) {
            Log.e(TAG, "", e2);
            return 0;
        }
    }

    static DynamicTurboPowerHandler getInstance() {
        return sInstance;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public double getMiChargeDataToDouble(String str) {
        double d2 = 0.0d;
        try {
            String miChargePath = mMiCharge.getMiChargePath(str);
            if (miChargePath != null) {
                d2 = ((double) Integer.parseInt(miChargePath.trim())) * 1.0E-6d;
            }
        } catch (Exception e2) {
            Slog.w(TAG, "getMiChargeData exception", e2);
        }
        if (this.DBG_POWER) {
            Slog.w(TAG, "getMiChargeDataToDouble nodeName = " + str + " ret = " + d2);
        }
        return d2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getMiChargeDataToInt(String str) {
        int i2 = 0;
        try {
            String miChargePath = mMiCharge.getMiChargePath(str);
            if (miChargePath != null) {
                i2 = Integer.parseInt(miChargePath.trim());
            }
        } catch (Exception e2) {
            Slog.w(TAG, "getMiChargeData exception", e2);
        }
        if (this.DBG_POWER) {
            Slog.w(TAG, "getMiChargeDataToInt nodeName = " + str + " ret = " + i2);
        }
        return i2;
    }

    private int getSleepSeconds() {
        return (int) (Settings.System.getLong(this.mContext.getContentResolver(), "screen_off_timeout", 2147483647L) / 1000);
    }

    private boolean getSynchronization() {
        return ContentResolver.getMasterSyncAutomatically();
    }

    public static synchronized DynamicTurboPowerHandler init(Context context, Looper looper) {
        try {
            if (sInstance == null) {
                sInstance = new DynamicTurboPowerHandler(context, looper);
            }
        } catch (Throwable th) {
            throw th;
        }
        return sInstance;
    }

    private void initMiCharge() {
        IMiCharge iMiCharge = IMiCharge.getInstance();
        mMiCharge = iMiCharge;
        if (iMiCharge == null) {
            Slog.w(TAG, "init MiCharge obj = null");
            this.mIsMiChargeOn = false;
            return;
        }
        String miChargePath = iMiCharge.getMiChargePath("is_support_mca");
        if (miChargePath != null && !miChargePath.isEmpty()) {
            this.mIsMiChargeOn = true;
        }
        Slog.w(TAG, "init MiCharge mIsMiChargeOn = " + this.mIsMiChargeOn + " isSupportMca = " + miChargePath);
    }

    private boolean isFiveGModePrefEnabled(Context context) {
        try {
            return 1 == Settings.Global.getInt(context.getContentResolver(), SETTING_FIVEG_ENABLED, 1);
        } catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
    }

    private boolean isGxzwSensor() {
        return SystemProperties.getBoolean("ro.hardware.fp.fod", false);
    }

    private boolean isHapTicLinear() {
        String str = SystemProperties.get("sys.haptic.motor", "");
        return str.equals("linear") || str.equals("zlinear");
    }

    private void isNVTCloseStatistics() {
        if (isSupportNVT()) {
            return;
        }
        mIsNVT = false;
        writeLocalLog("Not is NVT");
    }

    private boolean isNoSimCard() {
        TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
        return telephonyManager.getSimState() == 1 || telephonyManager.getSimState() == 0;
    }

    private boolean isPickupWakeupEnable(Context context) {
        boolean booleanForUser = MiuiSettings.System.getBooleanForUser(context.getContentResolver(), "pick_up_gesture_wakeup_mode", false, UserHandle.myUserId());
        if (this.DBG_POWER) {
            Slog.d(TAG, "isPickupWakeupEnable ret=" + booleanForUser);
        }
        return booleanForUser;
    }

    private boolean isSupportDolbyDax() {
        return SystemProperties.getBoolean("ro.vendor.audio.dolby.dax.support", false);
    }

    private boolean isSupportFiveGEnabled(Context context) {
        int i2 = Settings.Global.getInt(context.getContentResolver(), SETTING_FIVEG_SWITCH_VISIBLE, -1);
        if (i2 != -1) {
            return i2 == 1;
        }
        return miui.telephony.TelephonyManager.getDefault().isUserFiveGEnabled();
    }

    private boolean isSupportNVT() throws Throwable {
        int miChargeDataToInt;
        try {
            if (this.mIsMiChargeOn) {
                miChargeDataToInt = getMiChargeDataToInt("vendor");
            } else {
                String node = readNode("sys/class/qcom-battery/fg_vendor");
                miChargeDataToInt = node != null ? Integer.parseInt(node.trim()) : 0;
            }
            if (miChargeDataToInt != 0) {
                SystemProperties.set(POWER_FUEL_GAUGE, "1");
                return true;
            }
            SystemProperties.set(POWER_FUEL_GAUGE, "0");
            return false;
        } catch (NumberFormatException e2) {
            Log.e(TAG, "NumberFormatException is " + e2);
            return false;
        } catch (Exception e3) {
            Log.e(TAG, "Exception is " + e3);
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() throws Throwable {
        int pReal_A = this.mEnergyLearning.getPReal_A(PATH_GET_LEARN_POWER);
        this.mActualPower = pReal_A;
        if (this.mIsFeatureFlip) {
            int miChargeDataToInt = this.mIsMiChargeOn ? getMiChargeDataToInt("") : this.mEnergyLearning.getPReal_A(PATH_FLIP_GET_LEARN_POWER);
            writeLocalLog("stopAScenesSelfLearning basePower is " + pReal_A + ", flipPower is " + miChargeDataToInt);
            if (miChargeDataToInt != 0) {
                this.mActualPower += miChargeDataToInt;
            }
        }
        writeLocalLog("mForegroundPackageName = " + mForegroundPackageName + "   A Scenes Average ActualPower = " + this.mActualPower);
        int i2 = this.mActualPower;
        if (i2 == 0 || this.mLastActualPower == i2) {
            return;
        }
        List<Integer> orDefault = mGeneralScenes.getOrDefault(mForegroundPackageName, new ArrayList());
        int i3 = this.mActualPower;
        this.mLastActualPower = i3;
        orDefault.add(Integer.valueOf(i3));
        mGeneralScenes.put(mForegroundPackageName, orDefault);
    }

    private void limitTopAudio(boolean z2) {
        if (this.mLowSound) {
            int i2 = Settings.System.getInt(this.mContext.getContentResolver(), LOW_POWER_LIMIT_MAX_AUDIO_ENABLE, -1);
            if (i2 == -1 && z2) {
                writeLocalLog("audio_limit_model true");
                AudioSystem.setParameters("spatial_max_audio_limit_model=true");
                Settings.System.putInt(this.mContext.getContentResolver(), LOW_POWER_LIMIT_MAX_AUDIO_ENABLE, 1);
            } else {
                if (i2 != 1 || z2) {
                    return;
                }
                writeLocalLog("audio_limit_model false");
                AudioSystem.setParameters("spatial_max_audio_limit_model=false");
                Settings.System.putInt(this.mContext.getContentResolver(), LOW_POWER_LIMIT_MAX_AUDIO_ENABLE, -1);
            }
        }
    }

    private void lowerAudio(boolean z2) {
        if (this.mLowSound) {
            int i2 = Settings.System.getInt(this.mContext.getContentResolver(), LOW_POWER_LOWER_AUDIO_ENABLE, -1);
            if (i2 == -1 && z2) {
                writeLocalLog("audio_low_power_model true");
                AudioSystem.setParameters("spatial_audio_low_power_model=true");
                Settings.System.putInt(this.mContext.getContentResolver(), LOW_POWER_LOWER_AUDIO_ENABLE, 1);
            } else {
                if (i2 != 1 || z2) {
                    return;
                }
                writeLocalLog("audio_low_power_model false");
                AudioSystem.setParameters("spatial_audio_low_power_model=false");
                Settings.System.putInt(this.mContext.getContentResolver(), LOW_POWER_LOWER_AUDIO_ENABLE, -1);
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:23:0x0098  */
    /* JADX WARN: Code duplicated, block: B:38:0x0102 A[Catch: Exception -> 0x0197, TRY_ENTER, TryCatch #0 {Exception -> 0x0197, blocks: (B:3:0x0002, B:6:0x0030, B:11:0x003c, B:9:0x0037, B:12:0x0061, B:14:0x0068, B:17:0x006f, B:24:0x0099, B:26:0x00a5, B:28:0x00ab, B:30:0x00d0, B:34:0x00de, B:35:0x00f6, B:38:0x0102, B:39:0x013e, B:41:0x0143, B:43:0x014d, B:45:0x0151, B:46:0x0153, B:42:0x0146, B:18:0x0086), top: B:50:0x0002 }] */
    /* JADX WARN: Code duplicated, block: B:39:0x013e A[Catch: Exception -> 0x0197, TryCatch #0 {Exception -> 0x0197, blocks: (B:3:0x0002, B:6:0x0030, B:11:0x003c, B:9:0x0037, B:12:0x0061, B:14:0x0068, B:17:0x006f, B:24:0x0099, B:26:0x00a5, B:28:0x00ab, B:30:0x00d0, B:34:0x00de, B:35:0x00f6, B:38:0x0102, B:39:0x013e, B:41:0x0143, B:43:0x014d, B:45:0x0151, B:46:0x0153, B:42:0x0146, B:18:0x0086), top: B:50:0x0002 }] */
    /* JADX WARN: Code duplicated, block: B:41:0x0143 A[Catch: Exception -> 0x0197, TryCatch #0 {Exception -> 0x0197, blocks: (B:3:0x0002, B:6:0x0030, B:11:0x003c, B:9:0x0037, B:12:0x0061, B:14:0x0068, B:17:0x006f, B:24:0x0099, B:26:0x00a5, B:28:0x00ab, B:30:0x00d0, B:34:0x00de, B:35:0x00f6, B:38:0x0102, B:39:0x013e, B:41:0x0143, B:43:0x014d, B:45:0x0151, B:46:0x0153, B:42:0x0146, B:18:0x0086), top: B:50:0x0002 }] */
    /* JADX WARN: Code duplicated, block: B:42:0x0146 A[Catch: Exception -> 0x0197, TryCatch #0 {Exception -> 0x0197, blocks: (B:3:0x0002, B:6:0x0030, B:11:0x003c, B:9:0x0037, B:12:0x0061, B:14:0x0068, B:17:0x006f, B:24:0x0099, B:26:0x00a5, B:28:0x00ab, B:30:0x00d0, B:34:0x00de, B:35:0x00f6, B:38:0x0102, B:39:0x013e, B:41:0x0143, B:43:0x014d, B:45:0x0151, B:46:0x0153, B:42:0x0146, B:18:0x0086), top: B:50:0x0002 }] */
    /* JADX WARN: Code duplicated, block: B:45:0x0151 A[Catch: Exception -> 0x0197, TryCatch #0 {Exception -> 0x0197, blocks: (B:3:0x0002, B:6:0x0030, B:11:0x003c, B:9:0x0037, B:12:0x0061, B:14:0x0068, B:17:0x006f, B:24:0x0099, B:26:0x00a5, B:28:0x00ab, B:30:0x00d0, B:34:0x00de, B:35:0x00f6, B:38:0x0102, B:39:0x013e, B:41:0x0143, B:43:0x014d, B:45:0x0151, B:46:0x0153, B:42:0x0146, B:18:0x0086), top: B:50:0x0002 }] */
    /* JADX WARN: Instruction removed from duplicated block: B:38:0x0102, please report this as an issue */
    private void mAScenePowerToTime(int i2) {
        int iRound;
        float f2;
        int i3;
        int i4;
        int i5;
        int i6;
        double d2;
        try {
            this.mTime = this.mEnergyLearning.getTimeToEnd();
            writeLocalLog("mAScenePowerToTime time is " + this.mTime + ", count is " + this.mCount);
            boolean z2 = this.mIsFeatureFlip;
            if ((z2 && this.mCount == 6) || (!z2 && this.mCount == 3)) {
                this.mLastTime = this.mTime;
                writeLocalLog("First time = " + this.mTime);
                SystemProperties.set(SCENE_BATTERY_LIFE, String.valueOf(this.mTime));
                return;
            }
            if (this.mCount % 15 == 0) {
                int i7 = this.mTime;
                float f3 = 0.0f;
                if (i7 > 0) {
                    if (z2) {
                        float f4 = this.mLastTime - i7;
                        int i8 = this.mCurrentLevel;
                        float f5 = f4 / i8;
                        float f6 = ((this.B_SCENE_TIME_WINDOW * i8) * f5) / i7;
                        iRound = Math.round(f6);
                        f3 = f5;
                        f2 = f6;
                    } else {
                        int i9 = this.mLastTime - i7;
                        int i10 = this.mCurrentLevel;
                        iRound = ((i9 / i10) * (i10 * 5)) / i7;
                    }
                    if (iRound < -5) {
                        iRound = -4;
                    }
                    i3 = this.mLastTime - (iRound + 5);
                    this.mTime = i3;
                    i4 = this.mCurrentLevel;
                    if (i4 < 15 && this.mCount % 30 == 0) {
                        i6 = (int) (((((((double) (i4 - 1)) * 0.1875d) - 0.6076d) * 1000.0d) / ((double) i2)) * 60.0d);
                        d2 = i6;
                        if ((i3 >= 0.7d * d2 || i3 > d2 * 1.3d) && i6 < i3) {
                            this.mTime = i6;
                            writeLocalLog("A-Scene stage Calibration time = " + this.mTime);
                        }
                    }
                    i5 = this.mTime;
                    if (i5 > 0) {
                        this.mLastTime = i5;
                        writeLocalLog("A-Now available time = " + this.mTime + " count = " + this.mCount + " des = " + iRound + ", timeDifferRate is " + f3 + ", timeDiffer is " + f2);
                        SystemProperties.set(SCENE_BATTERY_LIFE, String.valueOf(this.mTime));
                        return;
                    }
                    if (this.mCurrentLevel < 2) {
                        this.mLastTime = 1;
                    } else {
                        this.mLastTime = (this.mLastTime - 5) / 2;
                    }
                    if (this.mLastTime <= 0) {
                        this.mLastTime = 1;
                    }
                    writeLocalLog("A-LowPower corrent mLastTime = " + this.mLastTime + " time = " + this.mTime + " count = " + this.mCount + " des = " + iRound + ", timeDifferRate is " + f3 + ", timeDiffer is " + f2);
                    SystemProperties.set(SCENE_BATTERY_LIFE, String.valueOf(this.mLastTime));
                }
                iRound = 0;
                f2 = 0.0f;
                if (iRound < -5) {
                    iRound = -4;
                }
                i3 = this.mLastTime - (iRound + 5);
                this.mTime = i3;
                i4 = this.mCurrentLevel;
                if (i4 < 15) {
                    i6 = (int) (((((((double) (i4 - 1)) * 0.1875d) - 0.6076d) * 1000.0d) / ((double) i2)) * 60.0d);
                    d2 = i6;
                    if (i3 >= 0.7d * d2) {
                        this.mTime = i6;
                        writeLocalLog("A-Scene stage Calibration time = " + this.mTime);
                    } else {
                        this.mTime = i6;
                        writeLocalLog("A-Scene stage Calibration time = " + this.mTime);
                    }
                }
                i5 = this.mTime;
                if (i5 > 0) {
                    this.mLastTime = i5;
                    writeLocalLog("A-Now available time = " + this.mTime + " count = " + this.mCount + " des = " + iRound + ", timeDifferRate is " + f3 + ", timeDiffer is " + f2);
                    SystemProperties.set(SCENE_BATTERY_LIFE, String.valueOf(this.mTime));
                    return;
                }
                if (this.mCurrentLevel < 2) {
                    this.mLastTime = 1;
                } else {
                    this.mLastTime = (this.mLastTime - 5) / 2;
                }
                if (this.mLastTime <= 0) {
                    this.mLastTime = 1;
                }
                writeLocalLog("A-LowPower corrent mLastTime = " + this.mLastTime + " time = " + this.mTime + " count = " + this.mCount + " des = " + iRound + ", timeDifferRate is " + f3 + ", timeDiffer is " + f2);
                SystemProperties.set(SCENE_BATTERY_LIFE, String.valueOf(this.mLastTime));
            }
        } catch (Exception unused) {
            Log.e(TAG, "dynamic turbo power handler crash");
        }
    }

    public static void notifyUpdateNewCloud(JSONObject jSONObject) {
        if (sInstance != null) {
            try {
                Log.i(TAG, "Cloud-Observer onChange - DY object is " + jSONObject);
                if (jSONObject == null) {
                    Log.i(TAG, "Cloud-Observer DY object is null");
                    mIsFeatureOn = false;
                } else {
                    sInstance.updateConfigFromSettingDB(jSONObject);
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public static void onCloudFunctionUpdated(Context context, JSONObject jSONObject) {
        b.a aVar = mCloudObserver;
        if (aVar == null) {
            Log.i(TAG, "onCloudFunctionUpdated failed");
            return;
        }
        if (aVar.s(b.b.f35q)) {
            return;
        }
        String strOptString = jSONObject.optString(CLOUD_DYNAMIC_TURBO_POWER, "");
        String string = SimpleSettings.Misc.getString(context, CLOUD_DYNAMIC_TURBO_POWER, "");
        Log.i(TAG, "onCloudFunctionUpdated newCloud is " + strOptString + ", oldCloud is " + string);
        if (strOptString.equals(string)) {
            return;
        }
        SimpleSettings.Misc.putString(context, CLOUD_DYNAMIC_TURBO_POWER, strOptString);
        DynamicTurboPowerHandler dynamicTurboPowerHandler = sInstance;
        if (dynamicTurboPowerHandler != null) {
            dynamicTurboPowerHandler.sendMessage(dynamicTurboPowerHandler.obtainMessage(1, strOptString));
        }
    }

    private void onForeSceneChange(int i2) throws Throwable {
        if (this.mIsInCall) {
            if (i2 == 2) {
                stopAScenesSelfLearning();
                this.mCount = 0;
                this.mAveragePower = 0;
                processAScenesSelfRecord(this.mSatelliteAveragePower);
                postDelayedAScenesRunnable();
                return;
            }
            return;
        }
        if (i2 == 1) {
            ForegroundInfo foregroundInfo = this.mForegroundInfo;
            if (foregroundInfo == null) {
                Log.e(TAG, "a scene error for foregroundInfo is null");
                return;
            }
            if (!foregroundInfo.mForegroundPackageName.equals(mLastForegroundPackageName)) {
                stopAScenesSelfLearning();
            }
            mLastForegroundPackageName = this.mForegroundInfo.mForegroundPackageName;
            writeLocalLog("ForegroundPackageName = " + this.mForegroundInfo.mForegroundPackageName);
            this.mCount = 0;
            this.mAveragePower = 0;
            if (mGeneralScenes.containsKey(this.mForegroundInfo.mForegroundPackageName)) {
                updateLearnAScenes(mGeneralScenes);
                return;
            }
            int i3 = this.mAllDayAveragePower;
            this.mEstimatePower = i3;
            processAScenesSelfRecord(i3);
            postDelayedAScenesRunnable();
        }
    }

    private void openAodMode(Context context) {
        int i2 = Settings.System.getInt(context.getContentResolver(), LOW_POWER_AOD_ENABLE, -1);
        int i3 = Settings.Secure.getInt(context.getContentResolver(), SETTING_AOD_MODE, -1);
        if (i2 == -1) {
            return;
        }
        if (i2 != i3) {
            Settings.Secure.putInt(context.getContentResolver(), SETTING_AOD_MODE, i2);
            StringBuilder sb = new StringBuilder();
            sb.append("Aod Mode status: ");
            sb.append(i2 == 1);
            writeLocalLog(sb.toString());
        }
        Settings.System.putInt(context.getContentResolver(), LOW_POWER_AOD_ENABLE, -1);
    }

    private void openAutoDownloadMode(Context context) {
        int i2 = Settings.System.getInt(context.getContentResolver(), LOW_POWER_AUTO_DOWNLOAD_ENABLE, -1);
        int i3 = Settings.Secure.getInt(context.getContentResolver(), SETTING_AUTO_DOWNLOAD, 1);
        if (i2 == -1) {
            return;
        }
        if (i2 != i3) {
            Settings.Secure.putInt(context.getContentResolver(), SETTING_AUTO_DOWNLOAD, i2);
            StringBuilder sb = new StringBuilder();
            sb.append("Auto Download status: ");
            sb.append(i2 == 1);
            writeLocalLog(sb.toString());
        }
        Settings.System.putInt(context.getContentResolver(), LOW_POWER_AUTO_DOWNLOAD_ENABLE, -1);
    }

    private void openAutoUpdateMode(Context context) {
        int i2 = Settings.System.getInt(context.getContentResolver(), LOW_POWER_AUTO_UPDATE_ENABLE, -1);
        int i3 = Settings.Secure.getInt(context.getContentResolver(), SETTING_AUTO_UPDATE, 1);
        if (i2 == -1) {
            return;
        }
        if (i2 != i3) {
            Settings.Secure.putInt(context.getContentResolver(), SETTING_AUTO_UPDATE, i2);
            StringBuilder sb = new StringBuilder();
            sb.append("Auto Update status: ");
            sb.append(i2 == 1);
            writeLocalLog(sb.toString());
        }
        Settings.System.putInt(context.getContentResolver(), LOW_POWER_AUTO_UPDATE_ENABLE, -1);
    }

    private void openDolbyAudio() {
        if (isSupportDolbyDax() && Settings.System.getInt(this.mContext.getContentResolver(), LOW_POWER_LOWER_DOLBY_AUDIO_ENABLE, -1) != -1) {
            writeLocalLog("openDolbyAudio");
            MiSound miSound = new MiSound(0, 0);
            this.mMiSound = miSound;
            miSound.setEnabled(false);
            a.a aVar = new a.a(0, 0);
            this.mDolbyAudio = aVar;
            aVar.i(true);
            AudioManager audioManager = (AudioManager) this.mContext.getSystemService("audio");
            this.mAudioManager = audioManager;
            audioManager.setParameter("dolby_video_tool_enable", "1");
            Settings.System.putInt(this.mContext.getContentResolver(), LOW_POWER_LOWER_DOLBY_AUDIO_ENABLE, -1);
        }
    }

    private void openFingerAodMode(Context context) {
        int i2;
        if (!isGxzwSensor() || getEnrolledFingerprintsCount(context) <= 0 || UserHandle.myUserId() != 0 || (i2 = Settings.System.getInt(context.getContentResolver(), LOW_POWER_FINGER_AOD_ENABLE, -1)) == -1) {
            return;
        }
        if (i2 != Settings.Secure.getInt(context.getContentResolver(), SETTING_ICON_AOD_SHOW, 0)) {
            Settings.Secure.putInt(context.getContentResolver(), SETTING_ICON_AOD_SHOW, i2);
            StringBuilder sb = new StringBuilder();
            sb.append("FingerAod status: ");
            sb.append(i2 == 1);
            writeLocalLog(sb.toString());
        }
        Settings.System.putInt(context.getContentResolver(), LOW_POWER_FINGER_AOD_ENABLE, -1);
    }

    private void openFiveGMode(Context context) {
        int i2;
        if (!supportFiveGCapability() || !isSupportFiveGEnabled(context) || isNoSimCard() || (i2 = Settings.System.getInt(context.getContentResolver(), LOW_POWER_IS_5G_ENABLE, -1)) == -1) {
            return;
        }
        boolean z2 = i2 == 1;
        if (z2 != isFiveGModePrefEnabled(context)) {
            setFiveGModeEnabled(z2);
            writeLocalLog("5G status: " + z2);
        }
        Settings.System.putInt(context.getContentResolver(), LOW_POWER_IS_5G_ENABLE, -1);
    }

    private void openLightPulseMode(Context context) {
        int i2 = Settings.System.getInt(context.getContentResolver(), LOW_POWER_LIGHT_PULSE_ENABLE, -1);
        if (i2 == -1) {
            return;
        }
        if (i2 != Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "light_turn_on", 0, -2)) {
            Settings.Secure.putInt(this.mContext.getContentResolver(), "light_turn_on", i2);
            StringBuilder sb = new StringBuilder();
            sb.append("Light Pulse status: ");
            sb.append(i2 == 1);
            writeLocalLog(sb.toString());
        }
        Settings.System.putInt(context.getContentResolver(), LOW_POWER_LIGHT_PULSE_ENABLE, -1);
    }

    private void openPickupWakeupMode(Context context) {
        int i2 = Settings.System.getInt(context.getContentResolver(), LOW_POWER_WAKEUP_PICKUP, -1);
        if (i2 == -1) {
            return;
        }
        if (i2 != isPickupWakeupEnable(context)) {
            MiuiSettings.System.putBooleanForUser(this.mContext.getContentResolver(), "pick_up_gesture_wakeup_mode", i2 == 1, UserHandle.myUserId());
            StringBuilder sb = new StringBuilder();
            sb.append("Pickup Wakeup status: ");
            sb.append(i2 == 1);
            writeLocalLog(sb.toString());
        }
        Settings.System.putInt(context.getContentResolver(), LOW_POWER_WAKEUP_PICKUP, -1);
    }

    private void openSleepSecondsMode(Context context) {
        int i2 = Settings.System.getInt(context.getContentResolver(), LOW_POWER_SLEEP_SECONDS_ENABLE, -1);
        if (i2 == -1) {
            return;
        }
        if (i2 != getSleepSeconds()) {
            setSleepSeconds(context, i2);
            writeLocalLog("Sleep Seconds = " + i2);
        }
        Settings.System.putInt(context.getContentResolver(), LOW_POWER_SLEEP_SECONDS_ENABLE, -1);
    }

    private void openSoundMode(Context context) {
        int i2 = Settings.System.getInt(context.getContentResolver(), LOW_POWER_SOUND_DIALER_ENABLE, -1);
        int i3 = Settings.System.getInt(context.getContentResolver(), LOW_POWER_SOUND_CLICK_ENABLE, -1);
        int i4 = Settings.System.getInt(context.getContentResolver(), LOW_POWER_SOUND_LOCK_ENABLE, -1);
        int i5 = Settings.System.getInt(context.getContentResolver(), LOW_POWER_SOUND_SCREENSHOT_ENABLE, -1);
        int i6 = Settings.System.getInt(context.getContentResolver(), LOW_POWER_SOUND_DELETE_ENABLE, -1);
        if (i2 != -1 && i2 != Settings.System.getInt(context.getContentResolver(), SETTING_SOUND_DIALER, 0)) {
            Settings.System.putInt(context.getContentResolver(), SETTING_SOUND_DIALER, i2);
            StringBuilder sb = new StringBuilder();
            sb.append("Dialer Sound status: ");
            sb.append(i2 == 1);
            writeLocalLog(sb.toString());
        }
        if (i3 != -1 && i3 != Settings.System.getInt(context.getContentResolver(), SETTING_SOUND_CLICK, 0)) {
            Settings.System.putInt(context.getContentResolver(), SETTING_SOUND_CLICK, i3);
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Click Sound status: ");
            sb2.append(i3 == 1);
            writeLocalLog(sb2.toString());
        }
        if (i4 != -1 && i4 != Settings.System.getInt(context.getContentResolver(), SETTING_SOUND_LOCK, 0)) {
            Settings.System.putInt(context.getContentResolver(), SETTING_SOUND_LOCK, i4);
            StringBuilder sb3 = new StringBuilder();
            sb3.append("Lock Sound status: ");
            sb3.append(i4 == 1);
            writeLocalLog(sb3.toString());
        }
        if (i5 != -1 && i5 != Settings.System.getInt(context.getContentResolver(), SETTING_SOUND_SCREENSHOT, 0)) {
            Settings.System.putInt(context.getContentResolver(), SETTING_SOUND_SCREENSHOT, i5);
            StringBuilder sb4 = new StringBuilder();
            sb4.append("Screenshot Sound status: ");
            sb4.append(i5 == 1);
            writeLocalLog(sb4.toString());
        }
        if (i6 != -1 && i6 != Settings.System.getInt(context.getContentResolver(), SETTING_SOUND_DELETE, 0)) {
            Settings.System.putInt(context.getContentResolver(), SETTING_SOUND_DELETE, i6);
            StringBuilder sb5 = new StringBuilder();
            sb5.append("Delete Sound status: ");
            sb5.append(i6 == 1);
            writeLocalLog(sb5.toString());
        }
        Settings.System.putInt(context.getContentResolver(), LOW_POWER_SOUND_DIALER_ENABLE, -1);
        Settings.System.putInt(context.getContentResolver(), LOW_POWER_SOUND_CLICK_ENABLE, -1);
        Settings.System.putInt(context.getContentResolver(), LOW_POWER_SOUND_LOCK_ENABLE, -1);
        Settings.System.putInt(context.getContentResolver(), LOW_POWER_SOUND_SCREENSHOT_ENABLE, -1);
        Settings.System.putInt(context.getContentResolver(), LOW_POWER_SOUND_DELETE_ENABLE, -1);
    }

    private void openTapVibrateMode(Context context) {
        int i2;
        if (isHapTicLinear() || (i2 = Settings.System.getInt(context.getContentResolver(), LOW_POWER_HAPTIC_FEED_BACK_ENABLE, -1)) == -1) {
            return;
        }
        if (i2 != Settings.System.getInt(context.getContentResolver(), SETTING_HAPTIC_FEED_BACK, 0)) {
            Settings.System.putInt(context.getContentResolver(), SETTING_HAPTIC_FEED_BACK, i2);
            StringBuilder sb = new StringBuilder();
            sb.append("Tap Vibrate status: ");
            sb.append(i2 == 1);
            writeLocalLog(sb.toString());
        }
        Settings.System.putInt(context.getContentResolver(), LOW_POWER_HAPTIC_FEED_BACK_ENABLE, -1);
    }

    private void postDelayedAScenesRunnable() {
        removeAScenesRunnable();
        postDelayed(this.mSetStartRunnable, 10000L);
        postDelayed(this.mSetSecondStartRunnable, 12000L);
        postDelayed(this.mAScenesRunnable, this.mAStartPostDelay);
    }

    private void processAScenesSelfLearning() throws Throwable {
        writeLocalLog("stopAScenesSelfLearning");
        this.mEnergyLearning.setReport_A();
        removeCallbacks(this.mStopAScenesSelfLearn);
        postDelayed(this.mStopAScenesSelfLearn, 3000L);
    }

    private void processAScenesSelfRecord(int i2) throws Throwable {
        double batteryCapacity;
        double batteryCapacity2;
        double batteryCapacity3;
        double batteryCapacity4;
        if (!this.mIsFeatureFlip) {
            if (this.mIsMiChargeOn) {
                setMiChargeData(MICHARGE_PATH_SET_LEARN_POWER, Integer.toString(i2));
                return;
            } else {
                this.mEnergyLearning.setAScenesPowerToEnd(PATH_SET_LEARN_POWER, i2);
                return;
            }
        }
        if (this.mIsMiChargeOn) {
            batteryCapacity = getMiChargeDataToDouble(MICHARGE_PATH_GET_DESIGN_CAPACITY);
            batteryCapacity2 = getMiChargeDataToDouble(MICHARGE_PATH_FLIP_GET_DESIGN_CAPACITY);
            batteryCapacity3 = getMiChargeDataToDouble("");
            batteryCapacity4 = getMiChargeDataToDouble("");
        } else {
            batteryCapacity = getBatteryCapacity(PATH_GET_DESIGN_CAPACITY);
            batteryCapacity2 = getBatteryCapacity(PATH_FLIP_GET_DESIGN_CAPACITY);
            batteryCapacity3 = getBatteryCapacity(PATH_GET_REMAINING_CAPACITY);
            batteryCapacity4 = getBatteryCapacity(PATH_FLIP_GET_REMAINING_CAPACITY);
        }
        if (batteryCapacity == 0.0d || batteryCapacity2 == 0.0d) {
            writeLocalLog("AScenes  Capacity is 0");
            return;
        }
        double d2 = batteryCapacity / (batteryCapacity + batteryCapacity2);
        StringBuilder sb = new StringBuilder();
        sb.append("AScenes baseDesignCapacity is ");
        sb.append(batteryCapacity);
        sb.append(", flipDesignCapacity is ");
        sb.append(batteryCapacity2);
        sb.append(", baseRmCapacity is ");
        sb.append(batteryCapacity3);
        sb.append(", flipRmCapacity is ");
        sb.append(batteryCapacity4);
        sb.append(", capacityRatio is ");
        sb.append(d2);
        sb.append(", other is ");
        double d3 = 1.0d - d2;
        sb.append(d3);
        writeLocalLog(sb.toString());
        double d4 = i2;
        int i3 = (int) (d2 * d4);
        int i4 = (int) (d4 * d3);
        if (this.mIsMiChargeOn) {
            setMiChargeData(MICHARGE_PATH_SET_LEARN_POWER, Integer.toString(i3));
            setMiChargeData("", Integer.toString(i4));
        } else {
            this.mEnergyLearning.setAScenesPowerToEnd(PATH_SET_LEARN_POWER, i3);
            this.mEnergyLearning.setAScenesPowerToEnd(PATH_FLIP_SET_LEARN_POWER, i4);
        }
        writeLocalLog("Real_A mAllDayAveragePower is " + i2 + ", baseLearnPower is " + i3 + ", flipLearnPower is " + i4);
    }

    private void proofreadBPowerToEndTime(int i2, int i3) {
        int iRound;
        int i4;
        int i5;
        if (mIsNVT) {
            writeLocalLog("B Scenes time = " + i2);
            int i6 = this.mAllTime;
            if (i6 == 0 || i2 <= 0 || (i5 = this.mCurrentLevel) <= 0) {
                iRound = 0;
            } else if (this.mIsFeatureFlip) {
                float f2 = (i6 - i2) / i5;
                float f3 = ((this.B_SCENE_TIME_WINDOW * i5) * f2) / i2;
                iRound = Math.round(f3);
                writeLocalLog("B Scenes time = " + i2 + ", timeDifferRate is " + f2 + ", timeDiffer is " + f3 + ", res is " + iRound);
            } else {
                iRound = (((i6 - i2) / i5) * (this.B_SCENE_TIME_WINDOW * i5)) / i2;
            }
            int i7 = this.mCurrentLevel;
            if (i7 != 30 && (i4 = this.mAllTime) != 0 && (this.mIsFeatureFlip || i7 > 10)) {
                i2 = i4 - (this.B_SCENE_TIME_WINDOW + iRound);
            }
            if (i2 > 0) {
                int i8 = this.mAllTime;
                if (i8 < i2 && i8 > 1) {
                    this.mAllTime = i8 - 1;
                } else if (i8 == 1) {
                    this.mAllTime = 1;
                } else {
                    this.mAllTime = i2;
                }
            } else {
                int i9 = this.mAllTime;
                if (i9 > 2) {
                    this.mAllTime = i9 - 1;
                } else {
                    this.mAllTime = 1;
                }
            }
            if (i7 <= 2) {
                int i10 = this.mAllTime;
                if (i10 > 2) {
                    this.mAllTime = i10 - 1;
                } else {
                    this.mAllTime = 1;
                }
                writeLocalLog("Countdown B Scenes mAllTime= " + this.mAllTime);
            }
            writeLocalLog("B Scenes mAllTime= " + this.mAllTime + " res = " + iRound);
            stageCalibration(i3);
            writeTimeToSettings(this.mAllTime);
        }
    }

    private void qtiPerformance() {
        try {
            Class<?> cls = Class.forName("com.qualcomm.qti.Performance");
            this.sPerfClass = cls;
            Class[] clsArr = new Class[0];
            Constructor<?> constructor = cls.getConstructor(null);
            if (constructor != null) {
                this.performance = constructor.newInstance(null);
            }
            Class<?> cls2 = Integer.TYPE;
            this.mPerfHint = this.sPerfClass.getMethod("perfHint", cls2, String.class, cls2, cls2);
            this.mIsPerfHind = true;
        } catch (Exception unused) {
            Log.e(TAG, "preload class com.qualcomm.qti.Performance failed");
        }
    }

    private List<Integer> readAllDayFromDb() {
        ArrayList arrayList = new ArrayList();
        writeLocalLog("**************readAllDayFromDb************");
        try {
            String string = SimpleSettings.Misc.getString(this.mContext, KEY_ALL_DAY_POWER_STATE, "");
            if (!string.isEmpty()) {
                JSONObject jSONObject = new JSONObject(string);
                if (jSONObject.optInt("First", 0) != 0) {
                    arrayList.add(Integer.valueOf(jSONObject.optInt("First", 0)));
                }
                if (jSONObject.optInt("Second", 0) != 0) {
                    arrayList.add(Integer.valueOf(jSONObject.optInt("Second", 0)));
                }
                if (jSONObject.optInt("Third", 0) != 0) {
                    arrayList.add(Integer.valueOf(jSONObject.optInt("Third", 0)));
                }
                if (jSONObject.optInt("Fourth", 0) != 0) {
                    arrayList.add(Integer.valueOf(jSONObject.optInt("Fourth", 0)));
                }
                if (jSONObject.optInt("Fifth", 0) != 0) {
                    arrayList.add(Integer.valueOf(jSONObject.optInt("Fifth", 0)));
                }
            }
        } catch (JSONException e2) {
            Slog.w(TAG, "readAllDayFromDb exception", e2);
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<Integer> readStandbyFromDb() {
        ArrayList arrayList = new ArrayList();
        writeLocalLog("**************readStandbyFromDb************");
        try {
            String string = SimpleSettings.Misc.getString(this.mContext, KEY_STANDBY_POWER_STATE, "");
            if (!string.isEmpty()) {
                JSONObject jSONObject = new JSONObject(string);
                if (jSONObject.optInt("FirstDay", 0) != 0) {
                    arrayList.add(Integer.valueOf(jSONObject.optInt("FirstDay", 0)));
                }
                if (jSONObject.optInt("SecondDay", 0) != 0) {
                    arrayList.add(Integer.valueOf(jSONObject.optInt("SecondDay", 0)));
                }
                if (jSONObject.optInt("ThirdDay", 0) != 0) {
                    arrayList.add(Integer.valueOf(jSONObject.optInt("ThirdDay", 0)));
                }
                if (jSONObject.optInt("FourthDay", 0) != 0) {
                    arrayList.add(Integer.valueOf(jSONObject.optInt("FourthDay", 0)));
                }
                if (jSONObject.optInt("FifthDay", 0) != 0) {
                    arrayList.add(Integer.valueOf(jSONObject.optInt("FifthDay", 0)));
                }
            }
        } catch (JSONException e2) {
            Slog.w(TAG, "readStandbyFromDb exception", e2);
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void recordBrightScreenPower(int i2) {
        this.mAllAveragePower += i2;
        this.mAll_Count++;
    }

    private void registerCloudObserver() {
        mCloudObserver = b.a.p();
        StringBuilder sb = new StringBuilder();
        sb.append("Cloud-Observer isNewCloudConfigured is ");
        b.a aVar = mCloudObserver;
        int i2 = b.b.f35q;
        sb.append(aVar.s(i2));
        Log.i(TAG, sb.toString());
        if (mCloudObserver.s(i2)) {
            return;
        }
        this.mContext.getContentResolverForUser(UserHandle.SYSTEM).registerContentObserver(SimpleSettings.Misc.getUriFor(CLOUD_DYNAMIC_TURBO_POWER), false, new ContentObserver(this) { // from class: com.miui.powerkeeper.statemachine.DynamicTurboPowerHandler.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z2, Uri uri) {
                DynamicTurboPowerHandler dynamicTurboPowerHandler = DynamicTurboPowerHandler.this;
                dynamicTurboPowerHandler.sendMessage(dynamicTurboPowerHandler.obtainMessage(1, uri));
            }
        });
    }

    private void removeAScenesRunnable() {
        removeCallbacks(this.mSetStartRunnable);
        removeCallbacks(this.mSetSecondStartRunnable);
        removeCallbacks(this.mAScenesRunnable);
    }

    private void restoreLowPowerPolicy() {
        if (mIsFeatureOn) {
            writeLocalLog("restoreLowPowerPolicy :");
            if (this.mThermal) {
                alterThermalConfig(0);
            }
            if (this.mNeedRelease) {
                systemTuning();
            }
            setSynchronizeStatus(this.mContext, true);
            setGpsStatus(this.mContext, true);
            openFiveGMode(this.mContext);
            openFingerAodMode(this.mContext);
            openSoundMode(this.mContext);
            openPickupWakeupMode(this.mContext);
            openTapVibrateMode(this.mContext);
            openLightPulseMode(this.mContext);
            openAodMode(this.mContext);
            openSleepSecondsMode(this.mContext);
            openAutoUpdateMode(this.mContext);
            openAutoDownloadMode(this.mContext);
            lowerAudio(false);
            limitTopAudio(false);
        }
    }

    private int setAScenePowerToTime(int i2, int i3) throws Throwable {
        writeLocalLog("setAScenePowerToTime power is " + i2 + ", count is " + i3);
        this.mEnergyLearning.setPowerToEnd(i2);
        removeMessages(3);
        if (this.mIsFeatureFlip) {
            sendMessageDelayed(obtainMessage(3, Integer.valueOf(i2)), 1000L);
        } else {
            sendMessageDelayed(obtainMessage(3, Integer.valueOf(i2)), 2000L);
        }
        return this.mTime;
    }

    private void setFiveGModeEnabled(boolean z2) {
        miui.telephony.TelephonyManager.getDefault().setUserFiveGEnabled(z2);
    }

    private void setGps(Context context, int i2) {
        Settings.Secure.putInt(context.getContentResolver(), "location_mode", i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean setMiChargeData(String str, String str2) {
        boolean miChargePath;
        try {
            miChargePath = mMiCharge.setMiChargePath(str, str2);
        } catch (Exception e2) {
            Slog.w(TAG, "setMiChargeData exception", e2);
            miChargePath = false;
        }
        if (this.DBG_POWER) {
            Slog.w(TAG, "setMiChargeData nodeName = " + str + " val = " + str2 + " ret = " + miChargePath);
        }
        return miChargePath;
    }

    private void setSatelliteTime(int i2, int i3) {
        int i4 = (int) (((((((double) i3) * 0.1875d) - 0.6076d) * 1000.0d) / ((double) i2)) * 60.0d);
        if (i4 < 1) {
            i4 = 1;
        }
        this.mSatelliteTime = i4;
        SystemProperties.set(SATELLITE_SAVE_BATTERY_LIFE, String.valueOf(i4));
        writeLocalLog("Satellite-getSatelliteTime compute_time = " + i4 + " level = " + i3 + " power = " + i2);
    }

    private void setSleepSeconds(Context context, int i2) {
        long j2 = ((long) i2) * 1000;
        if (j2 == 0) {
            j2 = 2147483647L;
        }
        Settings.System.putLong(context.getContentResolver(), "screen_off_timeout", j2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setStandbyPowerToEndTime() throws Throwable {
        List<Integer> list;
        if (!mIsNVT || (list = mPowerRealStandby) == null || list.isEmpty()) {
            return;
        }
        removeCallbacks(this.mAScenesRunnable);
        int iIntValue = 0;
        for (int i2 = 0; i2 < mPowerRealStandby.size(); i2++) {
            iIntValue += mPowerRealStandby.get(i2).intValue();
        }
        int size = iIntValue / mPowerRealStandby.size();
        if (size == 0) {
            size = this.mAllDayAveragePower;
        }
        this.mEnergyLearning.setPowerToEnd(size);
        Runnable runnable = new Runnable() { // from class: com.miui.powerkeeper.statemachine.DynamicTurboPowerHandler.6
            @Override // java.lang.Runnable
            public void run() throws Throwable {
                int timeToEnd = DynamicTurboPowerHandler.this.mEnergyLearning.getTimeToEnd();
                if (timeToEnd != DynamicTurboPowerHandler.this.mStandbyTime) {
                    DynamicTurboPowerHandler.this.mStandbyTime = timeToEnd;
                }
                DynamicTurboPowerHandler.this.writeLocalLog("Standby mStandbyTime = " + DynamicTurboPowerHandler.this.mStandbyTime);
            }
        };
        removeCallbacks(runnable);
        postDelayed(runnable, 1000L);
    }

    private void setSynchronization(boolean z2) {
        ContentResolver.setMasterSyncAutomatically(z2);
    }

    private void stageCalibration(int i2) {
        int i3 = this.mCurrentLevel;
        if (i3 > 20 || i3 % 2 != 0) {
            return;
        }
        int i4 = (int) (((((((double) i3) * 0.1875d) - 0.6076d) * 1000.0d) / ((double) i2)) * 60.0d);
        int i5 = this.mAllTime;
        double d2 = i4;
        boolean z2 = ((double) i5) >= 0.7d * d2 && ((double) i5) <= d2 * 1.5d;
        if (!z2 && i4 < i5) {
            this.mAllTime = i4;
        }
        writeLocalLog("stageCalibration mAllTime = " + this.mAllTime + ",is_correct = " + z2 + ",compute_time = " + i4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startAScenesSelfLearning(int i2) throws Throwable {
        if (mIsFeatureOn && mIsNVT) {
            mForegroundPackageName = this.mForegroundInfo.mForegroundPackageName;
            int miChargeDataToInt = this.mIsMiChargeOn ? getMiChargeDataToInt(MICHARGE_PATH_BASE_AVERAGE) : this.mBatteryInfo.getCurrentDetection(PATH_BASE_AVERAGE);
            String node = readNode("/sys/class/power_supply/battery/voltage_now");
            int i3 = (int) (((double) miChargeDataToInt) * (node != null ? ((double) Integer.parseInt(node.trim())) * 1.0E-6d : 0.0d));
            if (i3 == 0) {
                return;
            }
            int i4 = this.mCount + 1;
            this.mCount = i4;
            if (i4 != 0) {
                this.mAveragePower += i3;
            }
            if (i4 == 0 || i4 % 3 != 0) {
                return;
            }
            if (i4 == 3) {
                this.mAveragePower /= 3;
            } else {
                this.mAveragePower /= 4;
            }
            if (i4 == 3 || i4 % 15 == 0) {
                writeLocalLog("mAveragePower = " + this.mAveragePower);
                setAScenePowerToTime(this.mAveragePower, this.mCount);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startAScenesSelfLearningFlip(int i2) throws Throwable {
        int pReal_A;
        int pReal_A2;
        int currentDetection;
        double currentVoltage;
        int currentDetection2;
        double currentVoltage2;
        if (mIsFeatureOn && mIsNVT) {
            mForegroundPackageName = this.mForegroundInfo.mForegroundPackageName;
            int i3 = 0;
            if (this.mIsFeatureFlip) {
                if (this.mIsMiChargeOn) {
                    currentDetection = getMiChargeDataToInt(MICHARGE_PATH_BASE_AVERAGE);
                    currentVoltage = getMiChargeDataToDouble("");
                    currentDetection2 = getMiChargeDataToInt("");
                    currentVoltage2 = getMiChargeDataToDouble("");
                } else {
                    currentDetection = this.mBatteryInfo.getCurrentDetection(PATH_BASE_AVERAGE);
                    currentVoltage = getCurrentVoltage(PATH_BASE_CURRENT_VOLTAGE);
                    currentDetection2 = this.mBatteryInfo.getCurrentDetection(PATH_FLIP_AVERAGE);
                    currentVoltage2 = getCurrentVoltage(PATH_FLIP_CURRENT_VOLTAGE);
                }
                double d2 = ((double) currentDetection) * currentVoltage;
                double d3 = ((double) currentDetection2) * currentVoltage2;
                if (d2 != 0.0d && d3 != 0.0d) {
                    i3 = ((int) d2) + ((int) d3);
                }
                writeLocalLog("startAScenesSelfLearning actualCurrent is " + currentDetection + ", actualFlipCurrent is " + currentDetection2 + ", actualVoltage is " + currentVoltage + ", actualFlipVoltage is " + currentVoltage2 + ", actualPower is " + i3 + "， mCount, " + this.mCount + ", mIsMiChargeOn is " + this.mIsMiChargeOn);
            }
            if (i3 == 0) {
                return;
            }
            this.mCount++;
            writeLocalLog("startAScenesSelfLearning mCount = " + this.mCount);
            int i4 = this.mCount;
            if (i4 != 0) {
                this.mAveragePower += i3;
            }
            if (i4 != 0 && i4 % 3 == 0) {
                if (i4 == 6) {
                    this.mAveragePower /= 6;
                } else if (i4 != 3) {
                    this.mAveragePower /= 4;
                }
                if (i4 == 6 || i4 % 15 == 0) {
                    writeLocalLog("mAveragePower = " + this.mAveragePower);
                    setAScenePowerToTime(this.mAveragePower, this.mCount);
                }
            }
            int i5 = this.mCount;
            if (i5 != 0) {
                if (i5 == 6 || i5 % 15 == 0) {
                    this.mEnergyLearning.setReport_A();
                    if (this.mIsMiChargeOn) {
                        pReal_A = getMiChargeDataToInt(MICHARGE_PATH_GET_LEARN_POWER);
                        pReal_A2 = getMiChargeDataToInt("");
                    } else {
                        pReal_A = this.mEnergyLearning.getPReal_A(PATH_GET_LEARN_POWER);
                        pReal_A2 = this.mEnergyLearning.getPReal_A(PATH_FLIP_GET_LEARN_POWER);
                    }
                    int i6 = pReal_A + pReal_A2;
                    this.mAveragePower = i6;
                    writeLocalLog("total = " + i6 + ", basePower is " + pReal_A + ", flipPower is " + pReal_A2);
                    this.mEnergyLearning.setStart_A();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startAScenesSelfLearningSatellite(int i2) throws Throwable {
        if (mIsFeatureOn && mIsNVT) {
            int miChargeDataToInt = this.mIsMiChargeOn ? getMiChargeDataToInt(MICHARGE_PATH_BASE_AVERAGE) : this.mBatteryInfo.getCurrentDetection(PATH_BASE_AVERAGE);
            String node = readNode("/sys/class/power_supply/battery/voltage_now");
            int i3 = (int) (((double) miChargeDataToInt) * (node != null ? ((double) Integer.parseInt(node.trim())) * 1.0E-6d : 0.0d));
            if (i3 == 0) {
                return;
            }
            int i4 = this.mCount + 1;
            this.mCount = i4;
            if (i4 != 0) {
                this.mAveragePower += i3;
            }
            if (i4 == 0 || i4 % 3 != 0) {
                return;
            }
            if (i4 == 3) {
                this.mAveragePower /= 3;
            } else {
                this.mAveragePower /= 4;
            }
            if (i4 == 3 || i4 % 15 == 0) {
                int i5 = this.mAveragePower;
                double d2 = i5;
                int i6 = this.mSatelliteAveragePower;
                if (d2 < ((double) i6) * 1.2d && ((double) i6) * 0.8d < i5) {
                    this.mSatelliteAveragePower = (int) (((double) i6) + (((double) (i5 - i6)) * 0.1d));
                }
                writeLocalLog("Satellite-mAveragePower = " + this.mAveragePower + " mSatelliteAveragePower = " + this.mSatelliteAveragePower);
            }
        }
    }

    private void stopAScenesSelfLearning() throws Throwable {
        if (mIsFeatureOn && mIsNVT) {
            processAScenesSelfLearning();
        }
    }

    private boolean supportFiveGCapability() {
        return "andromeda".equals(Build.DEVICE) || SystemProperties.getInt("ro.vendor.radio.5g", 0) > 0;
    }

    private void systemTuning() {
        if (this.mSystemTuning) {
            if (mArgs != null && !this.mNeedRelease) {
                writeLocalLog("systemTuning perfLockAcquire");
                com.miui.powerkeeper.perfengine.g.c().e(com.miui.powerkeeper.perfengine.g.f531k, mArgs);
                this.mNeedRelease = true;
            } else if (this.mNeedRelease) {
                writeLocalLog("systemTuning perfLockRelease");
                com.miui.powerkeeper.perfengine.g.c().f();
                this.mNeedRelease = false;
            }
        }
    }

    private void unregisterReceiver() {
        EventsAggregator eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
        if (eventsAggregator != null) {
            eventsAggregator.unregisterForForegroundInfo(this);
            eventsAggregator.unregisterBatteryLevelListener(this);
            eventsAggregator.unregisterForCharging(this);
            eventsAggregator.unregisterPowerModeListener(this);
            eventsAggregator.unregisterForOffHookCallState(this);
        }
    }

    private void updateConfig() {
        b.a aVar = mCloudObserver;
        int i2 = b.b.f35q;
        if (aVar.s(i2)) {
            try {
                JSONObject jSONObjectL = mCloudObserver.l(i2);
                Log.i(TAG, "updateConfig - DY object is " + jSONObjectL);
                updateConfigFromSettingDB(jSONObjectL);
                return;
            } catch (Exception e2) {
                Log.e(TAG, "updateConfig error", e2);
                return;
            }
        }
        String string = SimpleSettings.Misc.getString(this.mContext, CLOUD_DYNAMIC_TURBO_POWER, "");
        if (string.isEmpty()) {
            if (this.DBG_POWER) {
                Log.d(TAG, "updateConfig empty cloud");
                return;
            }
            return;
        }
        try {
            JSONObject jSONObject = new JSONObject(string);
            mIsFeatureOn = jSONObject.optBoolean("fucSwitch", false);
            this.mThermalConfig = jSONObject.optBoolean("thermalConfig", false);
            this.mDisplayIdle = jSONObject.optBoolean("displayIdle", false);
            this.mFiveG = jSONObject.optBoolean("fiveG", false);
            this.mLowSound = jSONObject.optBoolean("lowSound", false);
            this.mSystemTuning = jSONObject.optBoolean("systemTuning", false);
            this.mFirstStage = jSONObject.optInt("firstStage", 30);
            this.mSecondStage = jSONObject.optInt("secondStage", 20);
            this.mThirdStage = jSONObject.optInt("thirdStage", 5);
            this.NORMAL_MODULUS = jSONObject.optDouble("normal_modulus", 1.0d);
            this.PERFORMANCE_MODULUS = jSONObject.optDouble("performance_modulus", 0.949999988079071d);
            this.NORMAL_SAVE_MODULUS = jSONObject.optDouble("normal_save_modulus", 1.1299999952316284d);
            this.ULTIMATE_SAVE_MODULUS = jSONObject.optDouble("ultimate_save_modulus", 2.7300000190734863d);
            this.mAllDayAveragePower = jSONObject.optInt("dayAveragePower", 1160);
            this.mSystemSetting = jSONObject.optBoolean("system_setting", false);
            this.mAStartPostDelay = jSONObject.optInt("a_scenes_post_delay", 110000);
            this.mIsFeature32V = jSONObject.optBoolean("feature_3_2_v", false);
            this.mIsFeatureFlip = jSONObject.optBoolean("feature_flip", false);
            this.mIsFeatureBatteryLife = jSONObject.optBoolean("feature_battery_life", false);
            this.mSatelliteAveragePower = jSONObject.optInt("satelliteAveragePower", 3500);
        } catch (JSONException e3) {
            Log.e(TAG, "updateConfig error", e3);
        }
        Log.i(TAG, "updateConfig newCloud =" + string);
    }

    private void updateConfigFromSettingDB(JSONObject jSONObject) {
        boolean zOptBoolean = jSONObject.optBoolean("bcasEnableOnly", false);
        if (zOptBoolean) {
            Log.i(TAG, "updateConfigFromSettingDB ingore, bcasEnableOnly = " + zOptBoolean);
            mIsFeatureOn = false;
            return;
        }
        mIsFeatureOn = jSONObject.optBoolean("fucSwitch", false);
        this.mThermalConfig = jSONObject.optBoolean("thermalConfig", false);
        this.mDisplayIdle = jSONObject.optBoolean("displayIdle", false);
        this.mFiveG = jSONObject.optBoolean("fiveG", false);
        this.mLowSound = jSONObject.optBoolean("lowSound", false);
        this.mSystemTuning = jSONObject.optBoolean("systemTuning", false);
        this.mFirstStage = jSONObject.optInt("firstStage", 30);
        this.mSecondStage = jSONObject.optInt("secondStage", 20);
        this.mThirdStage = jSONObject.optInt("thirdStage", 5);
        this.NORMAL_MODULUS = jSONObject.optDouble("normal_modulus", 1.0d);
        this.PERFORMANCE_MODULUS = jSONObject.optDouble("performance_modulus", 0.949999988079071d);
        this.NORMAL_SAVE_MODULUS = jSONObject.optDouble("normal_save_modulus", 1.1299999952316284d);
        this.ULTIMATE_SAVE_MODULUS = jSONObject.optDouble("ultimate_save_modulus", 2.7300000190734863d);
        this.mAllDayAveragePower = jSONObject.optInt("dayAveragePower", 1160);
        this.mSystemSetting = jSONObject.optBoolean("system_setting", false);
        this.mAStartPostDelay = jSONObject.optInt("a_scenes_post_delay", 110000);
        this.mIsFeature32V = jSONObject.optBoolean("feature_3_2_v", false);
        this.mIsFeatureFlip = jSONObject.optBoolean("feature_flip", false);
        this.mIsFeatureBatteryLife = jSONObject.optBoolean("feature_battery_life", false);
        this.mSatelliteAveragePower = jSONObject.optInt("satelliteAveragePower", 3500);
        Log.i(TAG, "Cloud-Observer mIsFeatureOn=" + mIsFeatureOn + ", mThermalConfig = " + this.mThermalConfig + ", mDisplayIdle = " + this.mDisplayIdle + ", mFiveG = " + this.mFiveG + ", mLowSound = " + this.mLowSound + ", mSystemTuning = " + this.mSystemTuning + ", mFirstStage = " + this.mFirstStage + ", mSecondStage = " + this.mSecondStage + ", mThirdStage = " + this.mThirdStage + ", NORMAL_MODULUS = " + this.NORMAL_MODULUS + ", NORMAL_SAVE_MODULUS = " + this.NORMAL_SAVE_MODULUS + ", ULTIMATE_SAVE_MODULUS = " + this.ULTIMATE_SAVE_MODULUS + ", mAllDayAveragePower = " + this.mAllDayAveragePower + ", mSystemSetting = " + this.mSystemSetting + ", mAStartPostDelay = " + this.mAStartPostDelay + ", mIsFeatureFlip = " + this.mIsFeatureFlip + ", mIsFeatureBatteryLife = " + this.mIsFeatureBatteryLife + ", mSatelliteAveragePower = " + this.mSatelliteAveragePower);
    }

    private void updateLearnAScenes(ArrayMap<String, List<Integer>> arrayMap) throws Throwable {
        new ArrayList();
        List<Integer> list = arrayMap.get(this.mForegroundInfo.mForegroundPackageName);
        if (list.size() < this.mAccumulateSize) {
            int iIntValue = list.get(0).intValue();
            this.mEstimatePower = iIntValue;
            processAScenesSelfRecord(iIntValue);
            postDelayedAScenesRunnable();
            return;
        }
        int iIntValue2 = 0;
        for (int i2 = 0; i2 < list.size(); i2++) {
            iIntValue2 += list.get(i2).intValue();
        }
        int size = iIntValue2 / list.size();
        list.clear();
        list.add(0, Integer.valueOf(size));
        mGeneralScenes.put(this.mForegroundInfo.mForegroundPackageName, list);
        this.mEstimatePower = size;
        processAScenesSelfRecord(size);
        postDelayedAScenesRunnable();
    }

    private void writeAllDayToDb(List<Integer> list) {
        JSONObject jSONObject = new JSONObject();
        if (list == null || list.isEmpty()) {
            return;
        }
        writeLocalLog("**************writeAllDayToDb************");
        try {
            jSONObject.put("First", list.size() >= 1 ? list.get(0).intValue() : 0).put("Second", list.size() >= 2 ? list.get(1).intValue() : 0).put("Third", list.size() >= 3 ? list.get(2).intValue() : 0).put("Fourth", list.size() >= 4 ? list.get(3).intValue() : 0).put("Fifth", list.size() == 5 ? list.get(4).intValue() : 0);
        } catch (JSONException e2) {
            Slog.w(TAG, "writeAllDayToDb exception", e2);
        }
        SimpleSettings.Misc.putString(this.mContext, KEY_ALL_DAY_POWER_STATE, jSONObject.toString());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:30:0x0041 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    public void writeFile(String str, int i2) throws Throwable {
        FileOutputStream fileOutputStream;
        Throwable th;
        Exception e2;
        File file = new File(str);
        if (file.exists()) {
            try {
                try {
                    fileOutputStream = new FileOutputStream(file);
                    try {
                        try {
                            fileOutputStream.write(Integer.toString(i2).getBytes());
                            fileOutputStream.close();
                        } catch (Exception e3) {
                            e2 = e3;
                            Log.e(TAG, "io failed to write", e2);
                            if (fileOutputStream == null) {
                            } else {
                                fileOutputStream.close();
                            }
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        if (fileOutputStream != null) {
                            try {
                                fileOutputStream.close();
                            } catch (IOException e4) {
                                Log.e(TAG, "io failed to close", e4);
                            }
                        }
                        throw th;
                    }
                } catch (IOException e5) {
                    Log.e(TAG, "io failed to close", e5);
                }
            } catch (Exception e6) {
                fileOutputStream = null;
                e2 = e6;
            } catch (Throwable th3) {
                fileOutputStream = null;
                th = th3;
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void writeLocalLog(String str) {
        this.mLocalLog.log(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void writeStandbyToDb(List<Integer> list) {
        JSONObject jSONObject = new JSONObject();
        if (list == null || list.isEmpty()) {
            return;
        }
        writeLocalLog("**************writeStandbyToDb************");
        try {
            jSONObject.put("FirstDay", list.size() >= 1 ? list.get(0).intValue() : 0).put("SecondDay", list.size() >= 2 ? list.get(1).intValue() : 0).put("ThirdDay", list.size() >= 3 ? list.get(2).intValue() : 0).put("FourthDay", list.size() >= 4 ? list.get(3).intValue() : 0).put("FifthDay", list.size() == 5 ? list.get(4).intValue() : 0);
        } catch (JSONException e2) {
            Slog.w(TAG, "writeStandbyToDb exception", e2);
        }
        SimpleSettings.Misc.putString(this.mContext, KEY_STANDBY_POWER_STATE, jSONObject.toString());
    }

    private void writeTimeToSettings(int i2) {
        double d2 = i2;
        SystemProperties.set(COMPREHENSIVE_BATTERY_LIFE, String.valueOf((int) (this.NORMAL_MODULUS * d2)));
        SystemProperties.set(PERFORMANCE_BATTERY_LIFE, String.valueOf((int) (this.PERFORMANCE_MODULUS * d2)));
        SystemProperties.set(NORMAL_SAVE_BATTERY_LIFE, String.valueOf((int) (this.NORMAL_SAVE_MODULUS * d2)));
        SystemProperties.set(ULTIMATE_SAVE_BATTERY_LIFE, String.valueOf((int) (d2 * this.ULTIMATE_SAVE_MODULUS)));
    }

    public void alterThermalConfig(int i2) {
        if (this.mThermalConfig) {
            ThermalSwitchHandler.getInstance().onPowerSaveModeChanged(i2);
            if (i2 == 1) {
                this.mThermal = true;
            } else {
                this.mThermal = false;
            }
            writeLocalLog("alterThermalConfig = " + i2);
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("dump of DynamicTurboPowerHandler: mIsFeatureOn=" + mIsFeatureOn + " mThermalConfig = " + this.mThermalConfig + " mDisplayIdle = " + this.mDisplayIdle + " mFiveG = " + this.mFiveG + " mLowSound = " + this.mLowSound + " mSystemTuning = " + this.mSystemTuning + " mFirstStage = " + this.mFirstStage + " mSecondStage = " + this.mSecondStage + " mThirdStage = " + this.mThirdStage + " mAllDayAveragePower = " + this.mAllDayAveragePower + " NORMAL_MODULUS = " + this.NORMAL_MODULUS + " PERFORMANCE_MODULUS = " + this.PERFORMANCE_MODULUS + " NORMAL_SAVE_MODULUS = " + this.NORMAL_SAVE_MODULUS + " ULTIMATE_SAVE_MODULUS = " + this.ULTIMATE_SAVE_MODULUS + " mIsFeatureFlip = " + this.mIsFeatureFlip + " mIsFeatureBatteryLife = " + this.mIsFeatureBatteryLife + " mSatelliteAveragePower = " + this.mSatelliteAveragePower + " mIsMiChargeOn = " + this.mIsMiChargeOn);
        if (mGeneralScenes.size() > 0) {
            for (int i2 = 0; i2 < mGeneralScenes.size(); i2++) {
                printWriter.println("Pkg name = " + mGeneralScenes.keyAt(i2));
                new ArrayList();
                List<Integer> listValueAt = mGeneralScenes.valueAt(i2);
                if (listValueAt != null || !listValueAt.isEmpty()) {
                    printWriter.print("  Power list = ");
                    for (int i3 = 0; i3 < listValueAt.size(); i3++) {
                        printWriter.print(listValueAt.get(i3));
                        printWriter.print(" ");
                    }
                }
                printWriter.println(ShellUtils.COMMAND_LINE_END);
            }
        }
        List<Integer> allDayFromDb = readAllDayFromDb();
        mPowerRealB = allDayFromDb;
        if (allDayFromDb != null && !allDayFromDb.isEmpty()) {
            printWriter.println(" mPowerRealB = ");
            for (int i4 = 0; i4 < mPowerRealB.size(); i4++) {
                printWriter.print(mPowerRealB.get(i4));
                printWriter.print(" ");
            }
            printWriter.println(ShellUtils.COMMAND_LINE_END);
        }
        List<Integer> standbyFromDb = readStandbyFromDb();
        mPowerRealStandby = standbyFromDb;
        if (standbyFromDb != null && !standbyFromDb.isEmpty()) {
            printWriter.println(" mPowerRealStandby = ");
            for (int i5 = 0; i5 < mPowerRealStandby.size(); i5++) {
                printWriter.print(mPowerRealStandby.get(i5));
                printWriter.print(" ");
            }
            printWriter.println(ShellUtils.COMMAND_LINE_END);
        }
        this.mLocalLog.dump(fileDescriptor, printWriter, strArr);
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) throws Throwable {
        int i2;
        int i3 = message.what;
        if (i3 == -36) {
            if (this.mOnBattery) {
                this.mIsInCall = false;
                return;
            }
            boolean zBooleanValue = ((Boolean) message.obj).booleanValue();
            boolean z2 = Settings.System.getInt(PowerKeeperApplication.b().getContentResolver(), "satellite_state", 0) == 1;
            if (z2 && zBooleanValue) {
                onForeSceneChange(2);
                this.mIsInCall = true;
            } else {
                this.mIsInCall = false;
            }
            writeLocalLog("Satellite-Foreground in Satellite call state = " + this.mIsInCall + " isSatelliteEnabled = " + z2 + " isCall = " + zBooleanValue);
            return;
        }
        if (i3 == -22) {
            int i4 = message.arg1;
            this.mPowerMode = i4;
            if (i4 == 3 || i4 == 4) {
                alterThermalConfig(1);
                return;
            } else {
                alterThermalConfig(0);
                return;
            }
        }
        if (i3 == -20) {
            int i5 = message.arg1;
            if (this.mCurrentLevel - i5 == 1) {
                writeLocalLog("BATTERY_LEVEL_CHANGE level = " + i5);
                setSatelliteTime(this.mSatelliteAveragePower, i5);
                if (i5 <= this.mFirstStage) {
                    applyLowPowerPolicy(i5);
                }
                if (i5 == this.mFirstStage) {
                    allSceneTimeCorrection();
                }
                if (i5 == 10) {
                    removeCallbacks(this.mBScenesRunnable);
                    this.mB_Count = 1;
                    this.mBAveragePower = 0;
                    this.B_SCENE_COUNT_WINDOW = 15;
                    this.B_SCENE_TIME_WINDOW = 5;
                    postDelayed(this.mBScenesRunnable, this.mPostDelay);
                }
                if (i5 == 2) {
                    removeCallbacks(this.mBScenesRunnable);
                    this.mB_Count = 1;
                    this.mBAveragePower = 0;
                    this.B_SCENE_COUNT_WINDOW = 3;
                    postDelayed(this.mBScenesRunnable, this.mPostDelay);
                }
            }
            this.mCurrentLevel = message.arg1;
            return;
        }
        if (i3 == -9) {
            this.mForegroundInfo = (ForegroundInfo) message.obj;
            onForeSceneChange(1);
            return;
        }
        if (i3 == -8) {
            if (this.mOnBattery != ((Boolean) ((AsyncResult) message.obj).result).booleanValue()) {
                this.mOnBattery = !this.mOnBattery;
                writeLocalLog("mOnBattery = " + this.mOnBattery);
                allScenePowerStatistics();
                if (this.mOnBattery) {
                    restoreLowPowerPolicy();
                }
                if (!this.mOnBattery && (i2 = this.mCurrentLevel) <= this.mFirstStage) {
                    applyLowPowerPolicy(i2);
                }
                setSatelliteTime(this.mSatelliteAveragePower, this.mCurrentLevel);
                return;
            }
            return;
        }
        if (i3 == 1) {
            if (this.DBG_POWER) {
                Log.d(TAG, "MSG_CONFIG_UPDATE " + message.obj);
            }
            updateConfig();
            if (mIsFeatureOn) {
                return;
            }
            disable();
            restoreLowPowerPolicy();
            return;
        }
        if (i3 != 2) {
            if (i3 != 3) {
                Log.w(TAG, "warning for access here");
                return;
            } else {
                mAScenePowerToTime(((Integer) message.obj).intValue());
                return;
            }
        }
        int timeToEnd = this.mEnergyLearning.getTimeToEnd();
        int iIntValue = ((Integer) message.obj).intValue();
        writeLocalLog("MSG_TIMEDELAY_B power = " + iIntValue + ",time = " + timeToEnd);
        if (this.mAfterBatteryLevel - this.mBeforeBatteryLevel >= 5) {
            int i6 = (int) (((((((double) this.mCurrentLevel) * 0.1875d) - 0.6076d) * 1000.0d) / ((double) iIntValue)) * 60.0d);
            double d2 = timeToEnd;
            double d3 = i6;
            boolean z3 = d2 >= 0.8d * d3 && d2 <= d3 * 1.2d;
            if (!z3 && i6 < this.mAllTime) {
                timeToEnd = i6;
            }
            this.mAfterBatteryLevel = 0;
            this.mBeforeBatteryLevel = 0;
            writeLocalLog("MSG_TIMEDELAY_B time = " + timeToEnd + ",MSG_TIMEDELAY_B compute_time = " + i6 + ", mLastBatteryTime = " + this.mLastBatteryTime + ",is_correct = " + z3);
        }
        proofreadBPowerToEndTime(timeToEnd, iIntValue);
    }

    public String readNode(String str) throws Throwable {
        FileInputStream fileInputStream;
        File file = new File(str);
        FileInputStream fileInputStream2 = null;
        if (file.exists()) {
            if (this.DBG_POWER) {
                Slog.w(TAG, "file exist");
            }
            try {
                fileInputStream = new FileInputStream(file);
                try {
                    try {
                        byte[] bArr = new byte[fileInputStream.available()];
                        fileInputStream.read(bArr);
                        String str2 = new String(bArr);
                        try {
                            fileInputStream.close();
                        } catch (IOException e2) {
                            Slog.e(TAG, "io failed to close", e2);
                        }
                        return str2;
                    } catch (Throwable th) {
                        th = th;
                        fileInputStream2 = fileInputStream;
                        if (fileInputStream2 != null) {
                            try {
                                fileInputStream2.close();
                            } catch (IOException e3) {
                                Slog.e(TAG, "io failed to close", e3);
                            }
                        }
                        throw th;
                    }
                } catch (Exception e4) {
                    e = e4;
                    Slog.e(TAG, "io failed to read", e);
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (IOException e5) {
                            Slog.e(TAG, "io failed to close", e5);
                        }
                    }
                    return null;
                }
            } catch (Exception e6) {
                e = e6;
                fileInputStream = null;
            } catch (Throwable th2) {
                th = th2;
            }
        }
        return null;
    }

    public void setGpsStatus(Context context, boolean z2) {
        int gps = getGps(context);
        if (z2) {
            int i2 = Settings.System.getInt(context.getContentResolver(), LOW_POWER_LOCATION_ENABLE, 0);
            if ((gps == 2 || gps == 0) && gps != i2) {
                setGps(context, i2);
                writeLocalLog("GPS status: " + z2);
                return;
            }
            return;
        }
        Settings.System.putInt(context.getContentResolver(), LOW_POWER_LOCATION_ENABLE, gps);
        if (gps == 3) {
            setGps(context, 2);
            writeLocalLog("GPS status: " + z2 + "mode is battery saving");
            return;
        }
        if (gps == 1) {
            setGps(context, 0);
            writeLocalLog("GPS status: " + z2 + "mode is off");
        }
    }

    public void setSynchronizeStatus(Context context, boolean z2) {
        boolean z3 = Settings.System.getInt(context.getContentResolver(), LOW_POWER_SYNCHRONIZE_ENABLE, 0) == 1;
        boolean synchronization = getSynchronization();
        if (z2) {
            if (z3 && !synchronization) {
                setSynchronization(z3);
                Settings.System.putInt(context.getContentResolver(), LOW_POWER_SYNCHRONIZE_ENABLE, 0);
                writeLocalLog("Synchronize status: " + z2);
                return;
            }
            return;
        }
        if (synchronization) {
            Settings.System.putInt(context.getContentResolver(), LOW_POWER_SYNCHRONIZE_ENABLE, synchronization ? 1 : 0);
            writeLocalLog("Synchronize status: " + z2);
            if (synchronization) {
                setSynchronization(z2);
            }
        }
    }

    public void toDisplayIdle(int i2) {
        if (this.mDisplayIdle) {
            if (i2 == 1) {
                this.mDisplay = true;
            } else {
                this.mDisplay = false;
            }
            writeLocalLog("toDisplayIdle = " + i2);
        }
    }
}
