package com.miui.powerkeeper.statemachine;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.media.AudioPlaybackConfiguration;
import android.net.TrafficStats;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.cloudcontrol.CloudFunctionConfig;
import com.miui.powerkeeper.customerpower.CustomerPowerManager;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.feedbackcontrol.ThermalInfoCollector;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.provider.ThermalInfoHelper;
import com.miui.powerkeeper.thermal.resource.ScenarioConfig;
import com.miui.powerkeeper.thermalcollector.CollectorManager;
import com.miui.powerkeeper.utils.Constant;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import miui.process.ForegroundInfo;
import miui.process.ProcessManager;
import miui.process.RunningProcessInfo;
import miui.provider.Weather;
import miui.yellowpage.YellowPageContract;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class ThermalIECHandler extends Handler {
    private static final String ACTION_CAMERA_STATE = "com.miui.powerkeeper.CAMERA_STATE";
    private static final String AMBIENT_SENSOR_TEMP_PATH = "/sys/class/thermal/thermal_message/ambient_sensor_temp";
    private static final String BOARD_SENSOR_TEMP_PATH = "/sys/class/thermal/thermal_message/board_sensor_temp";
    private static final String CLOUD_THERMAL_GROUP = "thermal_group";
    private static final String[] DATA_TYPES;
    private static final String DATA_TYPE_BATTERY = "2";
    private static final String DATA_TYPE_FIRST_IEC = "0";
    private static final String DATA_TYPE_NO_BATTERY = "3";
    private static final String DATA_TYPE_REIEC = "1";
    private static final int DEFAULT_ACTIVITY_STATE_LEVEL = 19;
    private static final int DEFAULT_ALLOWED_IEC_BATTERY_AVG_TEMP;
    private static final int DEFAULT_ALLOWED_IEC_BATTERY_TEMP;
    public static final String DEFAULT_ALLOWED_IEC_SKUS = "";
    private static final boolean DEFAULT_AMBIENT_TEMP_MONITOR_ENABLED = false;
    private static final int DEFAULT_AMBIENT_TEMP_VOTE_COUNT_NUM_THRESH = 2;
    private static final int DEFAULT_AMBIENT_TEMP_VOTE_THRESH = 300;
    private static final boolean DEFAULT_BATTERY_MONITOR_ENABLED = true;
    private static final int DEFAULT_BOARD_SENSOR_TEMP_DOWN_DIFF_VALUE = 1;
    private static final int DEFAULT_BOARD_SENSOR_TEMP_ENABLED = 0;
    private static final int DEFAULT_BOARD_SENSOR_TEMP_RESET_VALUE = 38;
    private static final int DEFAULT_BOARD_SENSOR_TEMP_THRESHOLD = 40;
    private static final int DEFAULT_BOARD_SENSOR_TEMP_UP_DIFF_VALUE = 1;
    private static final boolean DEFAULT_IEC_ALL_48_ENABLED = false;
    private static final boolean DEFAULT_IEC_ENABLED = false;
    private static final int DEFAULT_IEC_KILL_ALL_BACK_PRO_BATTERY_TEMP = 44;
    private static final int DEFAULT_IEC_KILL_BATTERY_TEMP = 44;
    private static final int DEFAULT_IEC_KILL_SOME_BACK_PRO_BATTERY_TEMP = 44;
    private static final boolean DEFAULT_IEC_MONITOR_ENABLED = false;
    private static final long DEFAULT_IEC_SCREEN_OFF_TIME_PERIOD;
    private static final long DEFAULT_IEC_SCREEN_OFF_UPDATE_TIME = 43200000;
    private static final int DEFAULT_IEC_TEST_SCORE = 4;
    private static final int DEFAULT_KEEP_IEC_INTERVAL;
    private static final String DEFAULT_MONITOR_SCENES_FUNC = "11111110";
    private static final int DEFAULT_PROC_STATE_LEVEL = 19;
    private static final boolean DEFAULT_UPLOAD_FOR_STATS_CONFIG_SWITCH_ENABLED = true;
    private static final boolean DEFAULT_UPLOAD_FOR_STATS_ENABLED = false;
    private static final int DOWNLOAD_MB_PER_SECOND = 4;
    private static final int DOWNLOAD_STATE_SCEONDS = 10;
    private static final String DOWNLOAD_STATS_APPS_BACKGROUND = "com.miui.home;com.mi.android.globallauncher";
    private static final String EXTRA_CAMERA_STATE = "camera_state";
    private static final String FLASH_STATE_PATH = "/sys/class/thermal/thermal_message/flash_state";
    public static final int IECTest = 98;
    public static final String IEC_ALL48 = "IECAll48";
    public static final int IEC_ALL_48 = 1;
    public static final String IEC_MONITOR = "IECMonitor";
    private static final String IEC_MONITOR_BYTE_STYLE_IS_IEC_BATTERY = "is_IEC_monitor_battery";
    private static final String IEC_MONITOR_BYTE_STYLE_IS_IEC_START = "is_IEC_monitor_start";
    private static final String IEC_MONITOR_BYTE_STYLE_IS_IEC_TEST = "is_IEC_monitor_test";
    private static final String IEC_STATE_IS_IEC_LESS_FIXED_TEMP = "100";
    private static final String IEC_STATE_IS_IEC_TEST = "1";
    private static final String IEC_STATE_IS_IEC_TEST_START = "10";
    private static final String IEC_TYPE_ALL_48_IEC = "1";
    private static final String IEC_TYPE_MONITOR_IEC = "2";
    private static final String IEC_TYPE_NO_IEC = "0";
    private static final String IEC_UPLOAD_DATA_VERSION = "data_version";
    private static final String IEC_UPLOAD_DATA_VERSION_VALUE = "0.2";
    private static final String IEC_UPLOAD_INFO_BATTERY_TEMP = "battery_temp";
    private static final String IEC_UPLOAD_INFO_BOARD_TEMP = "board_temp";
    private static final String IEC_UPLOAD_INFO_DATA_TYPE = "iec_data_type";
    private static final String IEC_UPLOAD_INFO_IEC_TYPE = "iec_type";
    private static final String IEC_UPLOAD_INFO_SCENE_STATE = "scene_state";
    private static final String MONITOR_CAMERA = "camera";
    private static final String MONITOR_CHARGING = "charging";
    private static final String MONITOR_DOWNLOAD = "download";
    private static final String MONITOR_NAV = "navigation";
    private static final int NOT_DOWNLOAD_KB_PER_SECOND = 500;
    public static final int OFF = 99;
    private static final int SMART_POWER_ADDITIONAL_TYPE_DOWNLOAD_SHIFT = 2097152;
    private static final long SMART_POWER_ADDITIONAL_TYPE_NAVIGATION_SHIFT = 2305843009213693952L;
    private static final long SMART_POWER_ADDITIONAL_TYPE_VIDEO_PLAY_SHIFT = 72057594037927936L;
    private static final int SMART_POWER_ADDITIONAL_TYPE_XIAOMI_MARKET_DOWNLOAD_SHIFT = 4194304;
    private static final int SMART_POWER_ADDITIONAL_TYPE_XUNLEI_DOWNLOAD_SHIFT = 8388608;
    public static final String TAG = "ThermalIECHandler";
    private static final String THERMAL_IEC_MONITOR_PERSISTENCE_BATTERY_STATE = "thermal_iec_monitor_persistence_battery_state";
    private static final String THERMAL_IEC_MONITOR_PERSISTENCE_DATA_STYLE_BATTERY_STATE = "thermal_iec_monitor_persistence_data_style_battery_state";
    private static final String THERMAL_IEC_MONITOR_PERSISTENCE_DATA_STYLE_IEC_STATE = "thermal_iec_monitor_persistence_data_style_iec_state";
    private static final String THERMAL_IEC_MONITOR_PERSISTENCE_DATA_STYLE_SHUT_DOWN = "thermal_iec_monitor_persistence_data_style_shut_down";
    private static final String THERMAL_IEC_MONITOR_PERSISTENCE_IEC_SWITCH_BY_BST = "thermal_iec_monitor_persistence_iec_switch_by_bst";
    private static final String THERMAL_IEC_MONITOR_PERSISTENCE_IS_START = "thermal_iec_monitor_persistence_is_start";
    private static final String THERMAL_IEC_MONITOR_PERSISTENCE_SCREEN_OFF_OVER_PERIOD_TIME = "thermal_iec_monitor_persistence_screen_off_over_period_time";
    private static final String THERMAL_IEC_MONITOR_PERSISTENCE_START_TIME = "thermal_iec_monitor_persistence_start_time";
    private static int mIECState;
    private static ThermalIECHandler sInstance;
    private byte IECStateByte;
    private final int MSG_BATTERY_TEMP_STATE_UPDATE;
    private final int MSG_CAMERA_STATE_CHANGED;
    private final int MSG_DOWNLOAD_STATE_CHANGED;
    private final int MSG_FOREGROUND_DOWNLOAD_STATE_UPDATE;
    private final int MSG_IS_IEC_TEST_START;
    private final int MSG_IS_IEC_TEST_STATE;
    private final int MSG_IS_IEC_TIMER_END;
    private final int MSG_NAVIGATION_STATE_CHANGED;
    private final int MSG_OBSERVE_CONTENT_UPDATE;
    private final int MSG_VIDEO_PLAY_STATE_CHANGED;
    private final int MSG_VOICECALL_STATE_CHANGED;
    private AudioManager audioManager;
    private AudioStateCallback audioStateCallback;
    private EventsAggregator eventsAggregator;
    private boolean mAllowedForeDownloadStats;
    private int mAllowedIECBatteryAvgTemp;
    private int mAllowedIECBatteryTemp;
    public String mAllowedIECSkus;
    private boolean mAllowedIECSwitchByBST;
    private boolean mAmbientTempEnabled;
    private int mAmbientTempVoteCount;
    private int mAmbientTempVoteCountNumThresh;
    private int mAmbientTempVoteThresh;
    private boolean mBatteryMonitorEnabled;
    private int mBatteryTempCount;
    private long mBatteryTempTotal;
    private int mBoardSensorTempDownDiffValue;
    private int mBoardSensorTempEnabled;
    private BoardSensorTempListener mBoardSensorTempListener;
    private int mBoardSensorTempResetValue;
    private int mBoardSensorTempUpDiffValue;
    private int mBoardSensorTempValueThreshold;
    private Context mContext;
    private int mCurrentBatteryTemp;
    private boolean mCurrentIECAll48Allowed;
    private boolean mDefaultIECEnable;
    private String mDefaultIECEnableParam1;
    private String mDefaultIECEnableParam2;
    private int mDownloadCount;
    private int mFirstBoardSensorTempOver40;
    private FlashStateListener mFlashStateListener;
    private ForegroundInfo mForegroundInfo;
    private boolean mIECAll48Enabled;
    private boolean mIECEnabled;
    public int mIECKillAllBackProBatteryTemp;
    public int mIECKillBatteryTemp;
    public int mIECKillSomeBackProBatteryTemp;
    private boolean mIECMonitorEnabled;
    private long mIECScreenOffOverPeriodTime;
    private long mIECScreenOffTimePeriod;
    private long mIECScreenOffUpdateTime;
    private int mIECTestScore;
    private boolean mIsCamera;
    private boolean mIsFirstIECMonitorEnd;
    private boolean mIsFlashListenerState;
    private boolean mIsForegroundDownload;
    private boolean mIsIECAll48Allowed;
    private boolean mIsIECMonitorAllowed;
    private boolean mIsTeleCallActive;
    private boolean mIsUsedProcStateLevel;
    private boolean mIsVoipCallActive;
    private int mKeepIECInterval;
    private int mLastUploadBatteryTemp;
    private String mLastUploadSceneState;
    private String mMonitorScenesFunc;
    private int mNotDownloadCount;
    private int mPrevBatteryTemp;
    private int mPrevBoardSensorTemp;
    private long mPrevDownloadData;
    private int mProcActivityLevel;
    private int mProcStateLevel;
    private BroadcastReceiver mReceiver;
    private long mScreenOffTime;
    private boolean mSmartPowerDownloadIsStart;
    private boolean mSmartPowerNavigation;
    private boolean mSmartPowerVideoPlay;
    private boolean mSmartPowerXiaomiMarketDownload;
    private boolean mSmartPowerXunleiDownload;
    private long mStartIECTime;
    private boolean mUploadForStatsConfigSwitchEnabled;
    private boolean mUploadForStatsEnabled;
    private ConcurrentHashMap<String, Boolean> monitorSceneMap;
    private static final boolean DEBUG = Log.isLoggable("thermal.iec", 3);
    private static final String MONITOR_WIFIAP = "wifiAp";
    private static final String MONITOR_FLASH = "flash";
    private static final String MONITOR_GAME = "game";
    private static final String MONITOR_VOICECALL = "voicecall";
    private static final String MONITOR_VIDEO = "video";
    private static final String[] MONITOR_SCENES = {MONITOR_WIFIAP, MONITOR_FLASH, "charging", "camera", MONITOR_GAME, "navigation", MONITOR_VOICECALL, MONITOR_VIDEO, "download"};

    private class AudioStateCallback extends AudioManager.AudioPlaybackCallback {
        @Override // android.media.AudioManager.AudioPlaybackCallback
        public void onPlaybackConfigChanged(List<AudioPlaybackConfiguration> list) {
            boolean z2;
            Iterator<AudioPlaybackConfiguration> it = list.iterator();
            while (true) {
                if (!it.hasNext()) {
                    z2 = false;
                    break;
                }
                AudioPlaybackConfiguration next = it.next();
                if (ThermalIECHandler.this.isActive(next) && ThermalIECHandler.this.isVoiceSource(next) && ThermalIECHandler.this.isVoiceMode()) {
                    z2 = true;
                    break;
                }
            }
            if (z2 != ThermalIECHandler.this.mIsVoipCallActive) {
                ThermalIECHandler.this.mIsVoipCallActive = z2;
                ThermalIECHandler thermalIECHandler = ThermalIECHandler.this;
                thermalIECHandler.sendMessage(thermalIECHandler.obtainMessage(3));
            }
        }

        private AudioStateCallback() {
        }
    }

    private class BoardSensorTempListener extends FileObserver {
        public BoardSensorTempListener(String str) {
            super(str);
        }

        @Override // android.os.FileObserver
        public void onEvent(int i2, String str) {
            int currentBoardSensorTemp;
            if (i2 != 2 || ThermalIECHandler.this.mPrevBoardSensorTemp == (currentBoardSensorTemp = ThermalIECHandler.this.getCurrentBoardSensorTemp())) {
                return;
            }
            ThermalIECHandler.this.mPrevBoardSensorTemp = currentBoardSensorTemp;
            int currentAmbientSensorTemp = ThermalIECHandler.this.getCurrentAmbientSensorTemp();
            if (ThermalIECHandler.this.mBoardSensorTempEnabled != 1) {
                if (ThermalIECHandler.this.mBoardSensorTempEnabled == 2 && currentBoardSensorTemp > ThermalIECHandler.this.mBoardSensorTempValueThreshold * 1000 && ThermalIECHandler.this.isIECTest()) {
                    ThermalIECHandler thermalIECHandler = ThermalIECHandler.this;
                    thermalIECHandler.sendMessage(thermalIECHandler.obtainMessage(9));
                    ThermalIECHandler thermalIECHandler2 = ThermalIECHandler.this;
                    thermalIECHandler2.insertIECUploadInfo("0", currentBoardSensorTemp, thermalIECHandler2.mCurrentBatteryTemp, ThermalIECHandler.this.getUploadSceneState());
                    return;
                }
                return;
            }
            boolean z2 = ThermalIECHandler.this.mFirstBoardSensorTempOver40 != -1;
            if (ThermalIECHandler.DEBUG) {
                Log.d(ThermalIECHandler.TAG, "isFirstBoardSensorTempSceneEnable: " + z2 + "; mAllowedIECSwitchByBST: " + ThermalIECHandler.this.mAllowedIECSwitchByBST + "; isIECTest: " + ThermalIECHandler.this.isIECTest() + "; currentBoardSensorTemp: " + currentBoardSensorTemp + "; currentAmbientSensorTemp: " + currentAmbientSensorTemp);
            }
            if (!z2 && !ThermalIECHandler.this.mAllowedIECSwitchByBST && ThermalIECHandler.this.isIECTest() && currentBoardSensorTemp > ThermalIECHandler.this.mBoardSensorTempValueThreshold * 1000) {
                ThermalIECHandler.this.mFirstBoardSensorTempOver40 = currentBoardSensorTemp;
                if (ThermalIECHandler.this.mAmbientTempEnabled && currentAmbientSensorTemp <= ThermalIECHandler.this.mAmbientTempVoteThresh) {
                    ThermalIECHandler.this.mAmbientTempVoteCount++;
                    if (ThermalIECHandler.DEBUG) {
                        Log.d(ThermalIECHandler.TAG, "enter first IEC state checkpoint: board_temp: " + currentBoardSensorTemp + "; ambient_temp: " + currentAmbientSensorTemp + "; iec_state:" + ThermalIECHandler.this.getUploadSceneState());
                    }
                }
            }
            if (z2 && !ThermalIECHandler.this.mAllowedIECSwitchByBST) {
                if (currentBoardSensorTemp > ThermalIECHandler.this.mFirstBoardSensorTempOver40 + (ThermalIECHandler.this.mBoardSensorTempUpDiffValue * 1000)) {
                    if (ThermalIECHandler.this.mAmbientTempEnabled && currentAmbientSensorTemp <= ThermalIECHandler.this.mAmbientTempVoteThresh) {
                        ThermalIECHandler.this.mAmbientTempVoteCount++;
                        if (ThermalIECHandler.DEBUG) {
                            Log.d(ThermalIECHandler.TAG, "enter second IEC state ambient temp checkpoint: board_temp: " + currentBoardSensorTemp + "; ambient_temp: " + currentAmbientSensorTemp + "; iec_state:" + ThermalIECHandler.this.getUploadSceneState());
                        }
                    }
                    if (ThermalIECHandler.this.isIECTest()) {
                        if (!ThermalIECHandler.this.mAmbientTempEnabled) {
                            ThermalIECHandler.this.mAllowedIECSwitchByBST = true;
                            ThermalIECHandler thermalIECHandler3 = ThermalIECHandler.this;
                            thermalIECHandler3.sendMessage(thermalIECHandler3.obtainMessage(9));
                            ThermalIECHandler thermalIECHandler4 = ThermalIECHandler.this;
                            thermalIECHandler4.insertIECUploadInfo("0", currentBoardSensorTemp, thermalIECHandler4.mCurrentBatteryTemp, ThermalIECHandler.this.getUploadSceneState());
                            ThermalIECHandler.this.storeIECMonitorPersistenceData(ThermalIECHandler.THERMAL_IEC_MONITOR_PERSISTENCE_IEC_SWITCH_BY_BST);
                        } else if (ThermalIECHandler.this.mAmbientTempVoteCount >= ThermalIECHandler.this.mAmbientTempVoteCountNumThresh) {
                            ThermalIECHandler.this.mAllowedIECSwitchByBST = true;
                            ThermalIECHandler thermalIECHandler5 = ThermalIECHandler.this;
                            thermalIECHandler5.sendMessage(thermalIECHandler5.obtainMessage(9));
                            ThermalIECHandler thermalIECHandler6 = ThermalIECHandler.this;
                            thermalIECHandler6.insertIECUploadInfo("0", currentBoardSensorTemp, thermalIECHandler6.mCurrentBatteryTemp, ThermalIECHandler.this.getUploadSceneState());
                            ThermalIECHandler.this.storeIECMonitorPersistenceData(ThermalIECHandler.THERMAL_IEC_MONITOR_PERSISTENCE_IEC_SWITCH_BY_BST);
                        }
                    }
                    ThermalIECHandler.this.mFirstBoardSensorTempOver40 = -1;
                    ThermalIECHandler.this.mAmbientTempVoteCount = 0;
                }
                if (currentBoardSensorTemp < ThermalIECHandler.this.mFirstBoardSensorTempOver40 - (ThermalIECHandler.this.mBoardSensorTempDownDiffValue * 1000)) {
                    ThermalIECHandler.this.mFirstBoardSensorTempOver40 = -1;
                    ThermalIECHandler.this.mAmbientTempVoteCount = 0;
                }
            }
            if (z2 || !ThermalIECHandler.this.mAllowedIECSwitchByBST || currentBoardSensorTemp >= ThermalIECHandler.this.mBoardSensorTempResetValue * 1000) {
                return;
            }
            ThermalIECHandler.this.mAllowedIECSwitchByBST = false;
            ThermalIECHandler.this.storeIECMonitorPersistenceData(ThermalIECHandler.THERMAL_IEC_MONITOR_PERSISTENCE_IEC_SWITCH_BY_BST);
            if (ThermalIECHandler.this.getIECStateByte(ThermalIECHandler.IEC_MONITOR_BYTE_STYLE_IS_IEC_START).equals("10")) {
                ThermalIECHandler thermalIECHandler7 = ThermalIECHandler.this;
                thermalIECHandler7.sendMessage(thermalIECHandler7.obtainMessage(10));
            }
        }
    }

    private class FlashStateListener extends FileObserver {
        public FlashStateListener(String str) {
            super(str);
        }

        @Override // android.os.FileObserver
        public void onEvent(int i2, String str) throws Throwable {
            if (i2 == 2) {
                boolean flashState = ThermalIECHandler.this.getFlashState();
                if (ThermalIECHandler.this.monitorSceneMap.containsKey(ThermalIECHandler.MONITOR_FLASH)) {
                    ThermalIECHandler.this.monitorSceneMap.put(ThermalIECHandler.MONITOR_FLASH, Boolean.valueOf(flashState));
                    ThermalIECHandler thermalIECHandler = ThermalIECHandler.this;
                    thermalIECHandler.sendMessage(thermalIECHandler.obtainMessage(8, Boolean.valueOf(thermalIECHandler.isIECTest())));
                }
            }
        }
    }

    static {
        DEFAULT_KEEP_IEC_INTERVAL = isTestMode() ? 30000 : CustomerPowerManager.SYSTEM_BAD_SIGNAL_TIME;
        DEFAULT_IEC_SCREEN_OFF_TIME_PERIOD = isTestMode() ? 120000L : ThermalInfoCollector.DEFAULT_COLLECT_UNRE_APPS_INFO_MIN_INTERVAL;
        DEFAULT_ALLOWED_IEC_BATTERY_AVG_TEMP = isTestMode() ? 500 : 300;
        DEFAULT_ALLOWED_IEC_BATTERY_TEMP = isTestMode() ? 500 : 300;
        DATA_TYPES = new String[]{"0", "1", "2", "3"};
        mIECState = 99;
    }

    private ThermalIECHandler(Context context, Looper looper) {
        super(looper);
        this.mPrevDownloadData = -1L;
        this.mDownloadCount = 0;
        this.mNotDownloadCount = 0;
        this.mIsForegroundDownload = false;
        this.mAllowedForeDownloadStats = false;
        this.mForegroundInfo = null;
        this.eventsAggregator = null;
        this.audioManager = null;
        this.audioStateCallback = null;
        this.monitorSceneMap = new ConcurrentHashMap<>();
        this.mSmartPowerDownloadIsStart = false;
        this.mSmartPowerXiaomiMarketDownload = false;
        this.mSmartPowerXunleiDownload = false;
        this.mSmartPowerVideoPlay = false;
        this.mSmartPowerNavigation = false;
        this.IECStateByte = (byte) 0;
        this.mIECEnabled = false;
        this.mIECAll48Enabled = false;
        this.mIECMonitorEnabled = false;
        this.mAllowedIECSkus = "";
        this.mIECKillSomeBackProBatteryTemp = 44;
        this.mIECKillAllBackProBatteryTemp = 44;
        this.mIECKillBatteryTemp = 44;
        this.mIECTestScore = 4;
        this.mKeepIECInterval = DEFAULT_KEEP_IEC_INTERVAL;
        this.mIECScreenOffTimePeriod = DEFAULT_IEC_SCREEN_OFF_TIME_PERIOD;
        this.mIECScreenOffUpdateTime = DEFAULT_IEC_SCREEN_OFF_UPDATE_TIME;
        this.mAllowedIECBatteryAvgTemp = DEFAULT_ALLOWED_IEC_BATTERY_AVG_TEMP;
        this.mAllowedIECBatteryTemp = DEFAULT_ALLOWED_IEC_BATTERY_TEMP;
        this.mMonitorScenesFunc = DEFAULT_MONITOR_SCENES_FUNC;
        this.mProcStateLevel = 19;
        this.mProcActivityLevel = 19;
        this.mIsUsedProcStateLevel = true;
        this.mBatteryMonitorEnabled = true;
        this.mBoardSensorTempEnabled = 0;
        this.mBoardSensorTempValueThreshold = 40;
        this.mBoardSensorTempResetValue = DEFAULT_BOARD_SENSOR_TEMP_RESET_VALUE;
        this.mBoardSensorTempUpDiffValue = 1;
        this.mBoardSensorTempDownDiffValue = 1;
        this.mUploadForStatsEnabled = false;
        this.mUploadForStatsConfigSwitchEnabled = true;
        this.mAmbientTempEnabled = false;
        this.mAmbientTempVoteThresh = 300;
        this.mAmbientTempVoteCountNumThresh = 2;
        this.mIsFirstIECMonitorEnd = true;
        this.mDefaultIECEnable = false;
        this.mDefaultIECEnableParam1 = "";
        this.mDefaultIECEnableParam2 = "";
        this.MSG_OBSERVE_CONTENT_UPDATE = 0;
        this.MSG_BATTERY_TEMP_STATE_UPDATE = 1;
        this.MSG_CAMERA_STATE_CHANGED = 2;
        this.MSG_VOICECALL_STATE_CHANGED = 3;
        this.MSG_DOWNLOAD_STATE_CHANGED = 4;
        this.MSG_VIDEO_PLAY_STATE_CHANGED = 5;
        this.MSG_NAVIGATION_STATE_CHANGED = 6;
        this.MSG_FOREGROUND_DOWNLOAD_STATE_UPDATE = 7;
        this.MSG_IS_IEC_TEST_STATE = 8;
        this.MSG_IS_IEC_TEST_START = 9;
        this.MSG_IS_IEC_TIMER_END = 10;
        this.mStartIECTime = -1L;
        this.mScreenOffTime = -1L;
        this.mIECScreenOffOverPeriodTime = -1L;
        this.mIsCamera = false;
        this.mFlashStateListener = null;
        this.mIsFlashListenerState = false;
        this.mBoardSensorTempListener = null;
        this.mIsVoipCallActive = false;
        this.mIsTeleCallActive = false;
        this.mPrevBoardSensorTemp = -1;
        this.mPrevBatteryTemp = -1;
        this.mCurrentBatteryTemp = -1;
        this.mBatteryTempTotal = 0L;
        this.mBatteryTempCount = 0;
        this.mLastUploadBatteryTemp = 0;
        this.mFirstBoardSensorTempOver40 = -1;
        this.mAllowedIECSwitchByBST = false;
        this.mAmbientTempVoteCount = 0;
        this.mLastUploadSceneState = "";
        this.mIsIECMonitorAllowed = false;
        this.mIsIECAll48Allowed = false;
        this.mCurrentIECAll48Allowed = false;
        this.mReceiver = new BroadcastReceiver() { // from class: com.miui.powerkeeper.statemachine.ThermalIECHandler.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                byte b2;
                boolean z2;
                if (intent == null) {
                    return;
                }
                String action = intent.getAction();
                switch (action.hashCode()) {
                    case -1538406691:
                        b2 = !action.equals("android.intent.action.BATTERY_CHANGED") ? (byte) -1 : (byte) 1;
                        break;
                    case -1326089125:
                        b2 = !action.equals("android.intent.action.PHONE_STATE") ? (byte) -1 : (byte) 3;
                        break;
                    case 409953495:
                        b2 = !action.equals("android.net.wifi.WIFI_AP_STATE_CHANGED") ? (byte) -1 : (byte) 2;
                        break;
                    case 1776107135:
                        b2 = !action.equals("com.miui.powerkeeper.CAMERA_STATE") ? (byte) -1 : (byte) 0;
                        break;
                    case 1947666138:
                        b2 = !action.equals("android.intent.action.ACTION_SHUTDOWN") ? (byte) -1 : (byte) 4;
                        break;
                    case 2039811242:
                        b2 = !action.equals("android.intent.action.REBOOT") ? (byte) -1 : (byte) 5;
                        break;
                    default:
                        b2 = -1;
                        break;
                }
                if (b2 == 0) {
                    int intExtra = intent.getIntExtra("camera_state", -1);
                    ThermalIECHandler thermalIECHandler = ThermalIECHandler.this;
                    thermalIECHandler.sendMessage(thermalIECHandler.obtainMessage(2, Integer.valueOf(intExtra)));
                    return;
                }
                if (b2 == 1) {
                    int intExtra2 = intent.getIntExtra("status", -1);
                    z2 = intExtra2 == 2 || intExtra2 == 5;
                    int intExtra3 = intent.getIntExtra(Weather.WeatherBaseColumns.TEMPERATURE, -1);
                    ThermalIECHandler thermalIECHandler2 = ThermalIECHandler.this;
                    thermalIECHandler2.sendMessage(thermalIECHandler2.obtainMessage(1, Integer.valueOf(intExtra3)));
                    if (!ThermalIECHandler.this.monitorSceneMap.containsKey("charging") || ((Boolean) ThermalIECHandler.this.monitorSceneMap.get("charging")).booleanValue() == z2) {
                        return;
                    }
                    ThermalIECHandler.this.monitorSceneMap.put("charging", Boolean.valueOf(z2));
                    ThermalIECHandler thermalIECHandler3 = ThermalIECHandler.this;
                    thermalIECHandler3.sendMessage(thermalIECHandler3.obtainMessage(8, Boolean.valueOf(thermalIECHandler3.isIECTest())));
                    return;
                }
                if (b2 == 2) {
                    int intExtra4 = intent.getIntExtra("wifi_state", 0);
                    z2 = intExtra4 == 13 || intExtra4 == 12;
                    if (ThermalIECHandler.this.monitorSceneMap.containsKey(ThermalIECHandler.MONITOR_WIFIAP)) {
                        ThermalIECHandler.this.monitorSceneMap.put(ThermalIECHandler.MONITOR_WIFIAP, Boolean.valueOf(z2));
                        ThermalIECHandler thermalIECHandler4 = ThermalIECHandler.this;
                        thermalIECHandler4.sendMessage(thermalIECHandler4.obtainMessage(8, Boolean.valueOf(thermalIECHandler4.isIECTest())));
                        return;
                    }
                    return;
                }
                if (b2 != 3) {
                    if (b2 == 4 || b2 == 5) {
                        ThermalIECHandler.this.storeIECMonitorPersistenceData(ThermalIECHandler.THERMAL_IEC_MONITOR_PERSISTENCE_DATA_STYLE_SHUT_DOWN);
                        return;
                    } else {
                        Log.w(ThermalIECHandler.TAG, "warning for access here");
                        return;
                    }
                }
                String stringExtra = intent.getStringExtra(YellowPageContract.Permission.STATE);
                if (stringExtra == null) {
                    Log.w(ThermalIECHandler.TAG, "No information attached to call intent");
                    return;
                }
                if (stringExtra.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    ThermalIECHandler.this.mIsTeleCallActive = false;
                } else {
                    ThermalIECHandler.this.mIsTeleCallActive = true;
                }
                ThermalIECHandler thermalIECHandler5 = ThermalIECHandler.this;
                thermalIECHandler5.sendMessage(thermalIECHandler5.obtainMessage(3));
            }
        };
        this.mContext = context;
        if (context == null) {
            return;
        }
        this.eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
        registerReceiver();
        EventsAggregator eventsAggregator = this.eventsAggregator;
        if (eventsAggregator != null) {
            this.mForegroundInfo = eventsAggregator.getForegroundInfo();
            this.eventsAggregator.registerForForegroundInfo(this);
            this.eventsAggregator.registerForScreenOff(this, null);
            this.eventsAggregator.registerForScreenOn(this, null);
            this.eventsAggregator.registerSmartPowerScenarioIdChanged(this);
            this.eventsAggregator.registerForFlashStateChanged(this);
        }
        ContentResolver contentResolverForUser = this.mContext.getContentResolverForUser(UserHandle.SYSTEM);
        ContentObserver contentObserver = new ContentObserver(this) { // from class: com.miui.powerkeeper.statemachine.ThermalIECHandler.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z2, Uri uri) {
                if (uri.equals(SimpleSettings.Misc.getUriFor("thermal_group"))) {
                    ThermalIECHandler.this.updateAppSceneState();
                } else {
                    ThermalIECHandler.this.setIECCloudParas();
                }
                ThermalIECHandler thermalIECHandler = ThermalIECHandler.this;
                thermalIECHandler.sendMessage(thermalIECHandler.obtainMessage(0, uri));
            }
        };
        contentResolverForUser.registerContentObserver(SimpleSettings.Misc.getUriFor("thermal_group"), false, contentObserver);
        contentResolverForUser.registerContentObserver(SimpleSettings.Misc.getUriFor(Constant.FUNC_THERMAL_IECTEST_CONFIG_ENABLE), false, contentObserver);
        contentResolverForUser.registerContentObserver(SimpleSettings.Misc.getUriFor("thermal_IECtest_config_enable_params"), false, contentObserver);
        contentResolverForUser.registerContentObserver(SimpleSettings.Misc.getUriFor("thermal_IECtest_config_enable_params2"), false, contentObserver);
        this.audioManager = (AudioManager) context.getSystemService("audio");
        AudioStateCallback audioStateCallback = new AudioStateCallback();
        this.audioStateCallback = audioStateCallback;
        this.audioManager.registerAudioPlaybackCallback(audioStateCallback, this);
        if (!isFlashTempTrigger()) {
            this.mFlashStateListener = new FlashStateListener("/sys/class/thermal/thermal_message/flash_state");
        }
        this.mBoardSensorTempListener = new BoardSensorTempListener("/sys/class/thermal/thermal_message/board_sensor_temp");
        this.mDefaultIECEnable = SimpleSettings.Misc.getBoolean(this.mContext, Constant.FUNC_THERMAL_IECTEST_CONFIG_ENABLE, false);
        this.mDefaultIECEnableParam1 = CloudFunctionConfig.getFunctionParameters(this.mContext, Constant.FUNC_THERMAL_IECTEST_CONFIG_ENABLE, "").trim();
        this.mDefaultIECEnableParam2 = CloudFunctionConfig.getFunctionParameters2(this.mContext, Constant.FUNC_THERMAL_IECTEST_CONFIG_ENABLE, "").trim();
        setIECCloudParas();
        initIECMonitorPersistenceData();
        this.eventsAggregator.registerSmartPowerScenarioIdChanged(this);
        sendMessage(obtainMessage(8, Boolean.valueOf(isIECTest())));
    }

    private boolean getAppRunningState(String str) {
        String listenerAppPkgs = getListenerAppPkgs(str);
        if (!this.mIsUsedProcStateLevel) {
            ActivityManager activityManager = (ActivityManager) this.mContext.getSystemService("activity");
            List<ActivityManager.RunningAppProcessInfo> arrayList = new ArrayList<>();
            if (activityManager != null) {
                arrayList = activityManager.getRunningAppProcesses();
            }
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : arrayList) {
                if (listenerAppPkgs.contains(runningAppProcessInfo.processName) && runningAppProcessInfo.processState < this.mProcActivityLevel) {
                    if (!DEBUG) {
                        return true;
                    }
                    Log.d(TAG, "pkgName: " + runningAppProcessInfo.processName + "; processState: " + runningAppProcessInfo.processState);
                    return true;
                }
            }
        } else if (!listenerAppPkgs.equals("")) {
            for (String str2 : listenerAppPkgs.split(",")) {
                if (isAppRunning(str2)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean getCameraState() {
        String str;
        ForegroundInfo foregroundInfo = this.mForegroundInfo;
        boolean z2 = (foregroundInfo == null || (str = foregroundInfo.mForegroundPackageName) == null || !str.equals("com.android.camera")) ? false : true;
        if (z2) {
            this.mIsCamera = true;
        }
        return z2;
    }

    private boolean getChargingState() {
        int intExtra = this.mContext.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"), 2).getIntExtra("status", -1);
        return intExtra == 2 || intExtra == 5;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getCurrentAmbientSensorTemp() {
        String strTrim = getFileNodeState(AMBIENT_SENSOR_TEMP_PATH).trim();
        if (strTrim.equals("") || Integer.parseInt(strTrim) < 1000 || strTrim.length() != 4) {
            return 1000;
        }
        return Integer.parseInt(strTrim.substring(1));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getCurrentBoardSensorTemp() throws Throwable {
        String fileNodeState = getFileNodeState("/sys/class/thermal/thermal_message/board_sensor_temp");
        if (fileNodeState.equals("")) {
            return -1;
        }
        return Integer.parseInt(fileNodeState);
    }

    private double getDownloadSpeedKBPerSecond() {
        long totalRxBytes = TrafficStats.getTotalRxBytes();
        long j2 = this.mPrevDownloadData;
        double d2 = j2 != -1 ? (totalRxBytes - j2) / 1024.0d : 0.0d;
        this.mPrevDownloadData = totalRxBytes;
        if (DEBUG) {
            Log.d(TAG, "foreground downloadSpeed(KB/s): " + d2);
        }
        return d2;
    }

    private boolean getDownloadState() {
        return this.mAllowedForeDownloadStats ? this.mIsForegroundDownload : this.mSmartPowerDownloadIsStart;
    }

    /* JADX WARN: Code duplicated, block: B:40:0x0096 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    private String getFileNodeState(String str) throws Throwable {
        Throwable th;
        BufferedReader bufferedReader;
        Exception e2;
        BufferedReader bufferedReader2 = null;
        try {
            try {
                File file = new File(str);
                if (!file.exists()) {
                    return "";
                }
                StringBuilder sb = new StringBuilder();
                bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                try {
                    for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                        sb.append(line);
                    }
                    String string = sb.toString();
                    try {
                        bufferedReader.close();
                    } catch (IOException e3) {
                        Log.e(TAG, "getNodeState: " + str + e3);
                    }
                    return string;
                } catch (Exception e4) {
                    e2 = e4;
                }
            } catch (Throwable th2) {
                th = th2;
                if (0 != 0) {
                    try {
                        bufferedReader2.close();
                    } catch (IOException e5) {
                        Log.e(TAG, "getNodeState: " + str + e5);
                    }
                }
                throw th;
            }
        } catch (Exception e6) {
            bufferedReader = null;
            e2 = e6;
        } catch (Throwable th3) {
            th = th3;
            if (0 != 0) {
                bufferedReader2.close();
            }
            throw th;
        }
        Log.e(TAG, "getNodeState: " + str + e2);
        if (bufferedReader == null) {
            return "";
        }
        try {
            bufferedReader.close();
            return "";
        } catch (IOException e7) {
            Log.e(TAG, "getNodeState: " + str + e7);
            return "";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean getFlashState() throws Throwable {
        String fileNodeState = getFileNodeState("/sys/class/thermal/thermal_message/flash_state");
        return !fileNodeState.equals("") && Integer.parseInt(fileNodeState) == 1;
    }

    public static int getIECState() {
        return mIECState;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:18:0x0034  */
    public String getIECStateByte(String str) {
        byte b2;
        int iHashCode = str.hashCode();
        if (iHashCode != -174015493) {
            if (iHashCode != -91195952) {
                if (iHashCode == 689810788 && str.equals(IEC_MONITOR_BYTE_STYLE_IS_IEC_TEST)) {
                    b2 = 0;
                } else {
                    b2 = -1;
                }
            } else if (str.equals(IEC_MONITOR_BYTE_STYLE_IS_IEC_START)) {
                b2 = 1;
            } else {
                b2 = -1;
            }
        } else if (str.equals(IEC_MONITOR_BYTE_STYLE_IS_IEC_BATTERY)) {
            b2 = 2;
        } else {
            b2 = -1;
        }
        if (b2 == 0) {
            return Integer.toBinaryString(this.IECStateByte & 1);
        }
        if (b2 == 1) {
            return Integer.toBinaryString(this.IECStateByte & 2);
        }
        if (b2 == 2) {
            return Integer.toBinaryString(this.IECStateByte & 4);
        }
        Log.w(TAG, "warning for access here");
        return "";
    }

    private boolean getIECStrategyTypeEnabled(String str) {
        String str2 = SystemProperties.get("ro.thermal.iec.enable", "");
        boolean zIsSkuAllowed = str2.equals("") ? isSkuAllowed(this.mAllowedIECSkus.split(";")) : Boolean.valueOf(str2).booleanValue();
        if (!this.mIECEnabled || !zIsSkuAllowed) {
            return false;
        }
        if (str.equals(IEC_ALL48)) {
            return this.mIECAll48Enabled;
        }
        if (str.equals(IEC_MONITOR)) {
            return this.mIECMonitorEnabled;
        }
        return false;
    }

    private String getIECType() {
        if (this.mIsIECMonitorAllowed) {
            return "2";
        }
        return this.mIsIECAll48Allowed ? "1" : "0";
    }

    /* JADX WARN: Code duplicated, block: B:17:0x0069  */
    /* JADX WARN: Code duplicated, block: B:34:0x009a  */
    private String getListenerAppPkgs(String str) {
        String str2;
        String strOptString;
        String strOptString2;
        byte b2;
        String string = SimpleSettings.Misc.getString(this.mContext, "thermal_group", "");
        if (string != null) {
            try {
                if ("".equals(string)) {
                    str2 = "";
                    strOptString = str2;
                    strOptString2 = strOptString;
                } else {
                    JSONObject jSONObject = new JSONObject(string);
                    str2 = jSONObject.optString(MONITOR_GAME, "") + "," + jSONObject.optString("pubg", "") + "," + jSONObject.optString("game2", "") + "," + jSONObject.optString("yuanshen", "");
                    try {
                        strOptString = jSONObject.optString("navigation", "");
                        try {
                            strOptString2 = jSONObject.optString(MONITOR_VIDEO, "");
                        } catch (JSONException e2) {
                            e = e2;
                            e.printStackTrace();
                            strOptString2 = "";
                        }
                    } catch (JSONException e3) {
                        e = e3;
                        strOptString = "";
                    }
                }
            } catch (JSONException e4) {
                e = e4;
                str2 = "";
                strOptString = str2;
            }
        } else {
            str2 = "";
            strOptString = str2;
            strOptString2 = strOptString;
        }
        int iHashCode = str.hashCode();
        if (iHashCode != 3165170) {
            if (iHashCode != 112202875) {
                if (iHashCode == 1862666772 && str.equals("navigation")) {
                    b2 = 1;
                } else {
                    b2 = -1;
                }
            } else if (str.equals(MONITOR_VIDEO)) {
                b2 = 2;
            } else {
                b2 = -1;
            }
        } else if (str.equals(MONITOR_GAME)) {
            b2 = 0;
        } else {
            b2 = -1;
        }
        if (b2 == 0) {
            return str2;
        }
        if (b2 == 1) {
            return strOptString;
        }
        if (b2 == 2) {
            return strOptString2;
        }
        Log.w(TAG, "warning for access here.");
        return "";
    }

    private boolean getNavigationState() {
        return this.mSmartPowerNavigation;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:32:0x0064  */
    private boolean getSceneState(String str) {
        byte b2;
        switch (str.hashCode()) {
            case -1367751899:
                if (!str.equals("camera")) {
                    b2 = -1;
                } else {
                    b2 = 5;
                }
                break;
            case -1018596720:
                if (!str.equals(MONITOR_VOICECALL)) {
                    b2 = -1;
                } else {
                    b2 = 7;
                }
                break;
            case -787986908:
                if (!str.equals(MONITOR_WIFIAP)) {
                    b2 = -1;
                } else {
                    b2 = 4;
                }
                break;
            case 3165170:
                if (!str.equals(MONITOR_GAME)) {
                    b2 = -1;
                } else {
                    b2 = 1;
                }
                break;
            case 97513456:
                if (!str.equals(MONITOR_FLASH)) {
                    b2 = -1;
                } else {
                    b2 = 6;
                }
                break;
            case 112202875:
                if (!str.equals(MONITOR_VIDEO)) {
                    b2 = -1;
                } else {
                    b2 = 3;
                }
                break;
            case 1427818632:
                if (!str.equals("download")) {
                    b2 = -1;
                } else {
                    b2 = 8;
                }
                break;
            case 1436115569:
                if (!str.equals("charging")) {
                    b2 = -1;
                } else {
                    b2 = 0;
                }
                break;
            case 1862666772:
                if (!str.equals("navigation")) {
                    b2 = -1;
                } else {
                    b2 = 2;
                }
                break;
            default:
                b2 = -1;
                break;
        }
        switch (b2) {
            case 0:
                return getChargingState();
            case 1:
            case 2:
            case 3:
                return getAppRunningState(str);
            case 4:
                return getWifiApState();
            case 5:
                return getCameraState();
            case 6:
                return getFlashState();
            case 7:
                return getVoiceCallState();
            case 8:
                return getDownloadState();
            default:
                Log.w(TAG, "warning for access here");
                return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getUploadSceneState() {
        StringBuilder sb = new StringBuilder();
        for (String str : MONITOR_SCENES) {
            if (this.monitorSceneMap.containsKey(str) ? this.monitorSceneMap.get(str).booleanValue() : false) {
                sb.append("1");
            } else {
                sb.append("0");
            }
        }
        return String.valueOf(sb);
    }

    private boolean getVideoPlayState() {
        return this.mSmartPowerVideoPlay;
    }

    private boolean getVoiceCallState() {
        int i2 = 0;
        for (AudioPlaybackConfiguration audioPlaybackConfiguration : this.audioManager.getActivePlaybackConfigurations()) {
            i2++;
            if (isActive(audioPlaybackConfiguration) && isVoiceSource(audioPlaybackConfiguration) && isVoiceMode()) {
                this.mIsVoipCallActive = true;
                return true;
            }
            if (i2 == this.audioManager.getActivePlaybackConfigurations().size()) {
                this.mIsVoipCallActive = false;
            }
        }
        if (PowerKeeperManager.getInstance().getEventsAggregator().isInCall()) {
            this.mIsTeleCallActive = true;
            return true;
        }
        this.mIsTeleCallActive = false;
        return false;
    }

    private boolean getWifiApState() {
        int wifiApState = ((WifiManager) this.mContext.getSystemService("wifi")).getWifiApState();
        return wifiApState == 13 || wifiApState == 12;
    }

    private boolean handleAfterIECTestStart() {
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        if (jElapsedRealtime - this.mStartIECTime <= this.mKeepIECInterval) {
            return false;
        }
        if (!getIECStateByte(IEC_MONITOR_BYTE_STYLE_IS_IEC_TEST).equals("1")) {
            sendMessage(obtainMessage(10));
            return false;
        }
        if (DEBUG) {
            Log.d(TAG, "reIEC");
        }
        this.mStartIECTime = jElapsedRealtime;
        return true;
    }

    private void handleBatteryTempState(int i2) {
        int i3;
        this.mCurrentBatteryTemp = i2;
        if (this.mIsIECMonitorAllowed) {
            if (i2 != -1 && this.mPrevBatteryTemp == -1 && this.mScreenOffTime != -1) {
                this.mPrevBatteryTemp = i2;
                this.mBatteryTempTotal = i2;
            }
            if (i2 == -1 || (i3 = this.mPrevBatteryTemp) == -1 || Math.abs(i2 - i3) < 5) {
                return;
            }
            this.mPrevBatteryTemp = i2;
            long j2 = this.mBatteryTempTotal;
            if (j2 > 9223372036854774807L) {
                this.mBatteryTempTotal = j2 / ((long) (this.mBatteryTempCount + 1));
                this.mBatteryTempCount = 0;
            } else {
                this.mBatteryTempCount++;
                this.mBatteryTempTotal = j2 + ((long) i2);
            }
        }
    }

    private void handleForegroundDownloadStats(String str) {
        if (this.mIsIECMonitorAllowed) {
            boolean z2 = (str == null || DOWNLOAD_STATS_APPS_BACKGROUND.contains(str)) ? false : true;
            this.mAllowedForeDownloadStats = z2;
            if (z2) {
                if (this.mPrevDownloadData == -1) {
                    sendMessage(obtainMessage(7, this));
                    return;
                }
                return;
            }
            this.mDownloadCount = 0;
            this.mNotDownloadCount = 0;
            this.mIsForegroundDownload = false;
            this.mPrevDownloadData = -1L;
            if (hasMessages(7, this)) {
                removeMessages(7, this);
            }
        }
    }

    private void handleIECCloudBSTParaEnabled(int i2) {
        if (i2 != this.mBoardSensorTempEnabled && getIECStateByte(IEC_MONITOR_BYTE_STYLE_IS_IEC_START).equals("10")) {
            sendMessage(obtainMessage(10));
        }
        if (Math.abs(i2 - this.mBoardSensorTempEnabled) == 1 && (i2 == 0 || i2 == 2)) {
            this.mFirstBoardSensorTempOver40 = -1;
            this.mAmbientTempVoteCount = 0;
            if (this.mAllowedIECSwitchByBST) {
                this.mAllowedIECSwitchByBST = false;
                storeIECMonitorPersistenceData(THERMAL_IEC_MONITOR_PERSISTENCE_IEC_SWITCH_BY_BST);
            }
        }
        if (i2 == 0) {
            this.mBoardSensorTempListener.stopWatching();
        } else {
            this.mBoardSensorTempListener.startWatching();
        }
    }

    private void handleIECCloudParas(char[] cArr) {
        for (int i2 = 0; i2 < cArr.length; i2++) {
            if (cArr[i2] == '1') {
                ConcurrentHashMap<String, Boolean> concurrentHashMap = this.monitorSceneMap;
                String[] strArr = MONITOR_SCENES;
                String str = strArr[i2];
                concurrentHashMap.put(str, Boolean.valueOf(getSceneState(str)));
                if (strArr[i2].equals(MONITOR_FLASH) && !this.mIsFlashListenerState) {
                    this.mIsFlashListenerState = true;
                    if (!isFlashTempTrigger()) {
                        this.mFlashStateListener.startWatching();
                    }
                }
            } else {
                ConcurrentHashMap<String, Boolean> concurrentHashMap2 = this.monitorSceneMap;
                String[] strArr2 = MONITOR_SCENES;
                if (concurrentHashMap2.containsKey(strArr2[i2])) {
                    this.monitorSceneMap.remove(strArr2[i2]);
                    if (strArr2[i2].equals(MONITOR_FLASH) && this.mIsFlashListenerState) {
                        this.mIsFlashListenerState = false;
                        if (!isFlashTempTrigger()) {
                            this.mFlashStateListener.stopWatching();
                        }
                    }
                }
            }
        }
    }

    private void handleIsIECTestState(Message message) throws Throwable {
        int i2;
        if (!this.mIsIECMonitorAllowed) {
            if (getIECStateByte(IEC_MONITOR_BYTE_STYLE_IS_IEC_START).equals("10")) {
                sendMessage(obtainMessage(10));
                return;
            }
            return;
        }
        boolean zBooleanValue = ((Boolean) message.obj).booleanValue();
        setIECStateBit(IEC_MONITOR_BYTE_STYLE_IS_IEC_TEST, zBooleanValue);
        int currentBoardSensorTemp = getCurrentBoardSensorTemp();
        boolean zHandleAfterIECTestStart = true;
        if (this.mBoardSensorTempEnabled == 1) {
            if (this.mAllowedIECSwitchByBST) {
                boolean zEquals = getIECStateByte(IEC_MONITOR_BYTE_STYLE_IS_IEC_START).equals("10");
                if (zBooleanValue) {
                    if (!zEquals) {
                        sendMessage(obtainMessage(9));
                        insertIECUploadInfo("1", currentBoardSensorTemp, this.mCurrentBatteryTemp, getUploadSceneState());
                    }
                } else if (zEquals) {
                    sendMessage(obtainMessage(10));
                }
            } else if (this.mFirstBoardSensorTempOver40 == -1 && zBooleanValue && currentBoardSensorTemp > this.mBoardSensorTempValueThreshold * 1000) {
                this.mFirstBoardSensorTempOver40 = currentBoardSensorTemp;
                int currentAmbientSensorTemp = getCurrentAmbientSensorTemp();
                if (this.mAmbientTempEnabled && (i2 = this.mAmbientTempVoteCount) == 0 && currentAmbientSensorTemp <= this.mAmbientTempVoteThresh) {
                    this.mAmbientTempVoteCount = i2 + 1;
                    if (DEBUG) {
                        Log.d(TAG, "enter first IEC state ambient temp checkpoint: board_temp: " + currentBoardSensorTemp + "; ambient_temp: " + currentAmbientSensorTemp + "; iec_state:" + getUploadSceneState());
                    }
                }
            }
            zHandleAfterIECTestStart = false;
        } else if (getIECStateByte(IEC_MONITOR_BYTE_STYLE_IS_IEC_START).equals("10")) {
            zHandleAfterIECTestStart = handleAfterIECTestStart();
            if (zHandleAfterIECTestStart) {
                insertIECUploadInfo("1", -1, -1, getUploadSceneState());
            }
        } else if (zBooleanValue && ((this.mBoardSensorTempEnabled == 0 && isIECBatteryTempAllowed()) || (this.mBoardSensorTempEnabled == 2 && isIECBoardTempAllowed()))) {
            if (this.mBoardSensorTempEnabled == 0) {
                insertIECUploadInfo("0", currentBoardSensorTemp, this.mBatteryMonitorEnabled ? this.mLastUploadBatteryTemp : this.mCurrentBatteryTemp, getUploadSceneState());
            } else {
                insertIECUploadInfo("0", currentBoardSensorTemp, this.mCurrentBatteryTemp, getUploadSceneState());
            }
            sendMessage(obtainMessage(9));
        } else {
            zHandleAfterIECTestStart = false;
        }
        if (!zHandleAfterIECTestStart) {
            String uploadSceneState = getUploadSceneState();
            if (zBooleanValue && !uploadSceneState.equals(this.mLastUploadSceneState)) {
                this.mLastUploadSceneState = uploadSceneState;
                insertIECUploadInfo("3", -1, -1, uploadSceneState);
            }
        }
        printIECStateByte();
    }

    private void handleMsgScreenOn() {
        if (this.mIsIECMonitorAllowed) {
            long jElapsedRealtime = SystemClock.elapsedRealtime();
            if (this.mBatteryTempTotal != 0) {
                long j2 = this.mScreenOffTime;
                if (j2 != -1 && jElapsedRealtime - j2 > this.mIECScreenOffTimePeriod) {
                    this.mIECScreenOffOverPeriodTime = SystemClock.elapsedRealtime();
                    if (this.mBatteryTempTotal < ((long) (this.mBatteryTempCount + 1)) * ((long) this.mAllowedIECBatteryAvgTemp)) {
                        setIECStateBit(IEC_MONITOR_BYTE_STYLE_IS_IEC_BATTERY, true);
                        this.mLastUploadBatteryTemp = (int) (this.mBatteryTempTotal / ((long) (this.mBatteryTempCount + 1)));
                        if (this.mBatteryMonitorEnabled) {
                            insertIECUploadInfo("2", getCurrentBoardSensorTemp(), this.mLastUploadBatteryTemp, "-1");
                        }
                    } else {
                        setIECStateBit(IEC_MONITOR_BYTE_STYLE_IS_IEC_BATTERY, false);
                    }
                    storeIECMonitorPersistenceData(THERMAL_IEC_MONITOR_PERSISTENCE_DATA_STYLE_BATTERY_STATE);
                }
            }
            this.mScreenOffTime = -1L;
            this.mBatteryTempTotal = 0L;
            this.mBatteryTempCount = 0;
            printIECStateByte();
        }
    }

    private void handleSmartPowerScenarioChanged(long[] jArr) {
        if (this.mIsIECMonitorAllowed) {
            if (jArr != null && jArr.length == 2) {
                long j2 = jArr[1];
                this.mSmartPowerDownloadIsStart = (ScenarioConfig.INTERACTIVE_ACTION_TYPE_DOWNLOAD & j2) != 0;
                this.mSmartPowerXiaomiMarketDownload = (ScenarioConfig.INTERACTIVE_ACTION_TYPE_DOWNLOAD_MARKET & j2) != 0;
                this.mSmartPowerXunleiDownload = (ScenarioConfig.INTERACTIVE_ACTION_TYPE_DOWNLOAD_XUNLEI & j2) != 0;
                this.mSmartPowerVideoPlay = (72057594037927936L & j2) != 0;
                this.mSmartPowerNavigation = (j2 & SMART_POWER_ADDITIONAL_TYPE_NAVIGATION_SHIFT) != 0;
            }
            if (!this.monitorSceneMap.containsKey("download") || getDownloadState() == Boolean.TRUE.equals(this.monitorSceneMap.get("download"))) {
                return;
            }
            sendMessage(obtainMessage(4));
        }
    }

    static synchronized ThermalIECHandler init(Context context, Looper looper) {
        try {
            if (sInstance == null) {
                sInstance = new ThermalIECHandler(context, looper);
            }
        } catch (Throwable th) {
            throw th;
        }
        return sInstance;
    }

    private void initIECMonitorPersistenceData() {
        if (this.mBoardSensorTempEnabled == 1) {
            if (SimpleSettings.Misc.contains(this.mContext, THERMAL_IEC_MONITOR_PERSISTENCE_IEC_SWITCH_BY_BST)) {
                this.mAllowedIECSwitchByBST = SimpleSettings.Misc.getBoolean(this.mContext, THERMAL_IEC_MONITOR_PERSISTENCE_IEC_SWITCH_BY_BST, false);
                return;
            }
            return;
        }
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        if (SimpleSettings.Misc.contains(this.mContext, THERMAL_IEC_MONITOR_PERSISTENCE_START_TIME)) {
            this.mStartIECTime = SimpleSettings.Misc.getLong(this.mContext, THERMAL_IEC_MONITOR_PERSISTENCE_START_TIME, -1L);
        }
        if (SimpleSettings.Misc.contains(this.mContext, THERMAL_IEC_MONITOR_PERSISTENCE_SCREEN_OFF_OVER_PERIOD_TIME)) {
            this.mIECScreenOffOverPeriodTime = SimpleSettings.Misc.getLong(this.mContext, THERMAL_IEC_MONITOR_PERSISTENCE_SCREEN_OFF_OVER_PERIOD_TIME, -1L);
        }
        if (jElapsedRealtime < this.mStartIECTime || jElapsedRealtime < this.mIECScreenOffOverPeriodTime) {
            this.mStartIECTime = -1L;
            this.mIECScreenOffOverPeriodTime = -1L;
            storeIECMonitorPersistenceData(THERMAL_IEC_MONITOR_PERSISTENCE_DATA_STYLE_SHUT_DOWN);
            return;
        }
        if (SimpleSettings.Misc.contains(this.mContext, THERMAL_IEC_MONITOR_PERSISTENCE_IS_START) && SimpleSettings.Misc.getBoolean(this.mContext, THERMAL_IEC_MONITOR_PERSISTENCE_IS_START, false)) {
            setIECStateBit(IEC_MONITOR_BYTE_STYLE_IS_IEC_START, true);
            if (this.eventsAggregator != null && this.mUploadForStatsConfigSwitchEnabled) {
                notifyIECStateChanged(98);
                mIECState = 98;
            }
        }
        if (SimpleSettings.Misc.contains(this.mContext, THERMAL_IEC_MONITOR_PERSISTENCE_BATTERY_STATE) && SimpleSettings.Misc.getBoolean(this.mContext, THERMAL_IEC_MONITOR_PERSISTENCE_BATTERY_STATE, false)) {
            setIECStateBit(IEC_MONITOR_BYTE_STYLE_IS_IEC_BATTERY, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void insertIECUploadInfo(String str, int i2, int i3, String str2) {
        if (Arrays.toString(DATA_TYPES).contains(str) && this.mUploadForStatsEnabled) {
            if (str.equals("0") || str.equals("1")) {
                Log.d(TAG, "dataType: " + str + " batteryTemp: " + i3 + " boardTemp: " + i2 + " sceneState: " + str2);
                StringBuilder sb = new StringBuilder();
                for (String str3 : MONITOR_SCENES) {
                    sb.append(str3);
                    sb.append(":");
                    sb.append(this.monitorSceneMap.get(str3));
                    sb.append(" ");
                }
                Log.d(TAG, sb.toString());
            }
            try {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put(IEC_UPLOAD_DATA_VERSION, IEC_UPLOAD_DATA_VERSION_VALUE);
                jSONObject.put(IEC_UPLOAD_INFO_IEC_TYPE, getIECType());
                jSONObject.put(IEC_UPLOAD_INFO_DATA_TYPE, str);
                jSONObject.put("battery_temp", i3);
                jSONObject.put(IEC_UPLOAD_INFO_BOARD_TEMP, i2);
                jSONObject.put(IEC_UPLOAD_INFO_SCENE_STATE, str2);
                ThermalInfoHelper.insertThermalInfo(this.mContext, jSONObject.toString(), ThermalInfoCollector.PHONE_THERMAL_IEC_STATE_TYPE);
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isActive(AudioPlaybackConfiguration audioPlaybackConfiguration) {
        return audioPlaybackConfiguration.toString().contains("state:started");
    }

    private boolean isAppRunning(String str) {
        try {
            RunningProcessInfo runningProcessInfoByProcessName = ProcessManager.getRunningProcessInfoByProcessName(str);
            if (runningProcessInfoByProcessName == null || runningProcessInfoByProcessName.mProcState >= this.mProcStateLevel) {
                return false;
            }
            Log.d(TAG, "pkgName: " + str + "; processState: " + runningProcessInfoByProcessName.mProcState);
            return true;
        } catch (RemoteException unused) {
            Log.d(TAG, "isAppRunning: " + str);
            return false;
        }
    }

    private boolean isFlashTempTrigger() {
        return CollectorManager.getInstance().isFlashTempTrigger();
    }

    private boolean isIECBatteryTempAllowed() {
        if (this.mBatteryMonitorEnabled) {
            return this.mIECScreenOffOverPeriodTime != -1 && SystemClock.elapsedRealtime() - this.mIECScreenOffOverPeriodTime < this.mIECScreenOffUpdateTime && getIECStateByte(IEC_MONITOR_BYTE_STYLE_IS_IEC_BATTERY).equals(IEC_STATE_IS_IEC_LESS_FIXED_TEMP);
        }
        return this.mCurrentBatteryTemp < this.mAllowedIECBatteryTemp;
    }

    private boolean isIECBoardTempAllowed() {
        return getCurrentBoardSensorTemp() > this.mBoardSensorTempValueThreshold * 1000;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isIECTest() {
        Iterator<Boolean> it = this.monitorSceneMap.values().iterator();
        int i2 = 0;
        while (it.hasNext()) {
            if (it.next().booleanValue()) {
                i2++;
            }
        }
        return i2 >= this.mIECTestScore;
    }

    private boolean isSkuAllowed(String[] strArr) {
        String str = SystemProperties.get("ro.product.mod_device", "");
        if (str.equals("")) {
            return false;
        }
        for (String str2 : strArr) {
            if (str.equals(str2)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isTestMode() {
        try {
            return SystemProperties.getBoolean("persist.sys.iecmonitor.test", false);
        } catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isVoiceMode() {
        int mode = this.audioManager.getMode();
        return mode == 2 || mode == 3;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isVoiceSource(AudioPlaybackConfiguration audioPlaybackConfiguration) {
        return audioPlaybackConfiguration.getAudioAttributes().getUsage() == 2;
    }

    private void notifyIECStateChanged(int i2) {
        if (this.mIsIECAll48Allowed && i2 == 98) {
            return;
        }
        this.eventsAggregator.notifyIECStateChanged(i2);
    }

    private void printIECStateByte() {
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            for (String str : MONITOR_SCENES) {
                sb.append(str);
                sb.append(":");
                sb.append(this.monitorSceneMap.get(str));
                sb.append(" ");
            }
            Log.d(TAG, sb.toString() + "\r\n IECStateByte: " + Integer.toBinaryString(this.IECStateByte));
        }
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.miui.powerkeeper.CAMERA_STATE");
        intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
        intentFilter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        intentFilter.addAction("android.intent.action.ACTION_SHUTDOWN");
        intentFilter.addAction("android.intent.action.REBOOT");
        intentFilter.setPriority(1000);
        this.mContext.registerReceiver(this.mReceiver, intentFilter, 2);
    }

    private void setFlashState(boolean z2) {
        if (this.monitorSceneMap.containsKey(MONITOR_FLASH)) {
            this.monitorSceneMap.put(MONITOR_FLASH, Boolean.valueOf(z2));
            sendMessage(obtainMessage(8, Boolean.valueOf(isIECTest())));
        }
    }

    private void setIECCloudPara1(String str) {
        if (str.equals("")) {
            this.mIECAll48Enabled = false;
            this.mIECMonitorEnabled = false;
            this.mAllowedIECSkus = "";
            this.mIECKillBatteryTemp = 44;
            this.mIECKillSomeBackProBatteryTemp = 44;
            this.mIECKillAllBackProBatteryTemp = 44;
            return;
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            this.mIECAll48Enabled = Boolean.parseBoolean(jSONObject.optString("IECAll48Enabled", String.valueOf(false)));
            this.mIECMonitorEnabled = Boolean.parseBoolean(jSONObject.optString("IECMonitorEnabled", String.valueOf(false)));
            this.mAllowedIECSkus = jSONObject.optString("IECTestAllowedSkus", "");
            this.mIECKillBatteryTemp = Integer.parseInt(jSONObject.optString("IECKillBatteryTemp", String.valueOf(44)));
            this.mIECKillSomeBackProBatteryTemp = Integer.parseInt(jSONObject.optString("IECKillSomeBackProBatteryTemp", String.valueOf(44)));
            this.mIECKillAllBackProBatteryTemp = Integer.parseInt(jSONObject.optString("IECKillAllBackProBatteryTemp", String.valueOf(44)));
        } catch (JSONException e2) {
            this.mIECAll48Enabled = false;
            this.mIECMonitorEnabled = false;
            this.mAllowedIECSkus = "";
            this.mIECKillBatteryTemp = 44;
            this.mIECKillSomeBackProBatteryTemp = 44;
            this.mIECKillAllBackProBatteryTemp = 44;
            e2.printStackTrace();
        }
    }

    private void setIECCloudPara2(String str) {
        String str2;
        if (str.equals("")) {
            this.mMonitorScenesFunc = DEFAULT_MONITOR_SCENES_FUNC;
            this.mIECTestScore = 4;
            this.mKeepIECInterval = DEFAULT_KEEP_IEC_INTERVAL;
            this.mIECScreenOffTimePeriod = DEFAULT_IEC_SCREEN_OFF_TIME_PERIOD;
            this.mAllowedIECBatteryAvgTemp = DEFAULT_ALLOWED_IEC_BATTERY_AVG_TEMP;
            this.mAllowedIECBatteryTemp = DEFAULT_ALLOWED_IEC_BATTERY_TEMP;
            this.mIECScreenOffUpdateTime = DEFAULT_IEC_SCREEN_OFF_UPDATE_TIME;
            this.mProcStateLevel = 19;
            this.mBatteryMonitorEnabled = true;
            this.mBoardSensorTempEnabled = 0;
            this.mBoardSensorTempValueThreshold = 40;
            this.mBoardSensorTempResetValue = DEFAULT_BOARD_SENSOR_TEMP_RESET_VALUE;
            this.mBoardSensorTempUpDiffValue = 1;
            this.mBoardSensorTempDownDiffValue = 1;
            this.mUploadForStatsEnabled = false;
            this.mUploadForStatsConfigSwitchEnabled = true;
            this.mAmbientTempEnabled = false;
            this.mAmbientTempVoteThresh = 300;
            this.mAmbientTempVoteCountNumThresh = 2;
            return;
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            this.mMonitorScenesFunc = jSONObject.optString("monitorSceneFunc", DEFAULT_MONITOR_SCENES_FUNC);
            this.mIECTestScore = Integer.parseInt(jSONObject.optString("IECTestScore", String.valueOf(4)));
            if (isTestMode()) {
                str2 = DEFAULT_MONITOR_SCENES_FUNC;
            } else {
                this.mKeepIECInterval = Integer.parseInt(jSONObject.optString("keepIECInterval", String.valueOf(DEFAULT_KEEP_IEC_INTERVAL)));
                String strOptString = jSONObject.optString("IECScreenOffTimePeriod", String.valueOf(DEFAULT_IEC_SCREEN_OFF_TIME_PERIOD));
                str2 = DEFAULT_MONITOR_SCENES_FUNC;
                try {
                    this.mIECScreenOffTimePeriod = Long.parseLong(strOptString);
                    int i2 = DEFAULT_ALLOWED_IEC_BATTERY_AVG_TEMP;
                    this.mAllowedIECBatteryAvgTemp = Integer.parseInt(jSONObject.optString("allowedIECBatteryAvgTemp", String.valueOf(i2)));
                    this.mAllowedIECBatteryTemp = Integer.parseInt(jSONObject.optString("allowedIECBatteryTemp", String.valueOf(i2)));
                } catch (JSONException e2) {
                    e = e2;
                    this.mMonitorScenesFunc = str2;
                    this.mIECTestScore = 4;
                    this.mKeepIECInterval = DEFAULT_KEEP_IEC_INTERVAL;
                    this.mIECScreenOffTimePeriod = DEFAULT_IEC_SCREEN_OFF_TIME_PERIOD;
                    this.mAllowedIECBatteryAvgTemp = DEFAULT_ALLOWED_IEC_BATTERY_AVG_TEMP;
                    this.mAllowedIECBatteryTemp = DEFAULT_ALLOWED_IEC_BATTERY_TEMP;
                    this.mIECScreenOffUpdateTime = DEFAULT_IEC_SCREEN_OFF_UPDATE_TIME;
                    this.mProcStateLevel = 19;
                    this.mBatteryMonitorEnabled = true;
                    this.mBoardSensorTempEnabled = 0;
                    this.mBoardSensorTempValueThreshold = 40;
                    this.mBoardSensorTempResetValue = DEFAULT_BOARD_SENSOR_TEMP_RESET_VALUE;
                    this.mBoardSensorTempUpDiffValue = 1;
                    this.mBoardSensorTempDownDiffValue = 1;
                    this.mUploadForStatsEnabled = false;
                    this.mUploadForStatsConfigSwitchEnabled = true;
                    this.mAmbientTempEnabled = false;
                    this.mAmbientTempVoteThresh = 300;
                    this.mAmbientTempVoteCountNumThresh = 2;
                    e.printStackTrace();
                    return;
                }
            }
            this.mIECScreenOffUpdateTime = Long.parseLong(jSONObject.optString("IECScreenOffUpdateTime", String.valueOf(DEFAULT_IEC_SCREEN_OFF_UPDATE_TIME)));
            if (jSONObject.has("procStateLevel")) {
                this.mProcStateLevel = Integer.parseInt(jSONObject.optString("procStateLevel", String.valueOf(19)));
                this.mIsUsedProcStateLevel = true;
            } else {
                this.mIsUsedProcStateLevel = false;
            }
            if (jSONObject.has("activityStateLevel")) {
                this.mProcActivityLevel = Integer.parseInt(jSONObject.optString("activityStateLevel", String.valueOf(19)));
                this.mIsUsedProcStateLevel = false;
            }
            this.mBatteryMonitorEnabled = Boolean.parseBoolean(jSONObject.optString("batteryMonitorEnabled", String.valueOf(true)));
            int i3 = Integer.parseInt(jSONObject.optString("boardSensorTempEnabled", String.valueOf(0)));
            handleIECCloudBSTParaEnabled(i3);
            if (this.mBoardSensorTempEnabled != i3) {
                this.mBoardSensorTempEnabled = i3;
            }
            this.mBoardSensorTempValueThreshold = Integer.parseInt(jSONObject.optString("boardSensorTempValue", String.valueOf(40)));
            this.mBoardSensorTempResetValue = Integer.parseInt(jSONObject.optString("boardSensorTempResetValue", String.valueOf(DEFAULT_BOARD_SENSOR_TEMP_RESET_VALUE)));
            this.mBoardSensorTempUpDiffValue = Integer.parseInt(jSONObject.optString("boardSensorTempUpDiffValue", String.valueOf(1)));
            this.mBoardSensorTempDownDiffValue = Integer.parseInt(jSONObject.optString("boardSensorTempDownDiffValue", String.valueOf(1)));
            this.mUploadForStatsEnabled = Boolean.parseBoolean(jSONObject.optString("uploadForStatsEnabled", String.valueOf(false)));
            this.mUploadForStatsConfigSwitchEnabled = Boolean.parseBoolean(jSONObject.optString("uploadForStatsConfigSwitchEnabled", String.valueOf(true)));
            this.mAmbientTempEnabled = Boolean.parseBoolean(jSONObject.optString("ambientTempMonitorEnabled", String.valueOf(false)));
            this.mAmbientTempVoteThresh = Integer.parseInt(jSONObject.optString("ambientTempVoteThresh", String.valueOf(300)));
            this.mAmbientTempVoteCountNumThresh = Integer.parseInt(jSONObject.optString("ambientTempVoteCountNumThresh", String.valueOf(2)));
        } catch (JSONException e3) {
            e = e3;
            str2 = DEFAULT_MONITOR_SCENES_FUNC;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setIECCloudParas() {
        this.mIECEnabled = SimpleSettings.Misc.getBoolean(this.mContext, Constant.FUNC_THERMAL_IECTEST_CONFIG_ENABLE, this.mDefaultIECEnable);
        String strTrim = CloudFunctionConfig.getFunctionParameters(this.mContext, Constant.FUNC_THERMAL_IECTEST_CONFIG_ENABLE, this.mDefaultIECEnableParam1).trim();
        String strTrim2 = CloudFunctionConfig.getFunctionParameters2(this.mContext, Constant.FUNC_THERMAL_IECTEST_CONFIG_ENABLE, this.mDefaultIECEnableParam2).trim();
        if (strTrim.equals("")) {
            strTrim = this.mDefaultIECEnableParam1;
        }
        setIECCloudPara1(strTrim);
        if (strTrim2.equals("")) {
            strTrim2 = this.mDefaultIECEnableParam2;
        }
        setIECCloudPara2(strTrim2);
        this.mIsIECMonitorAllowed = getIECStrategyTypeEnabled(IEC_MONITOR);
        boolean iECStrategyTypeEnabled = getIECStrategyTypeEnabled(IEC_ALL48);
        this.mIsIECAll48Allowed = iECStrategyTypeEnabled;
        if (this.mIsIECMonitorAllowed && iECStrategyTypeEnabled) {
            this.mIsIECAll48Allowed = false;
        }
        boolean z2 = this.mCurrentIECAll48Allowed;
        boolean z3 = this.mIsIECAll48Allowed;
        if (z2 != z3) {
            this.mCurrentIECAll48Allowed = z3;
            if (z3) {
                notifyIECStateChanged(1);
                mIECState = 1;
            } else {
                notifyIECStateChanged(99);
                mIECState = 99;
            }
        }
        this.eventsAggregator.notifyIECStateBatteryChanged(new int[]{this.mIECKillBatteryTemp, this.mIECKillSomeBackProBatteryTemp, this.mIECKillAllBackProBatteryTemp});
        handleIECCloudParas(this.mMonitorScenesFunc.toCharArray());
        Log.d(TAG, "IECAll48Enabled: " + this.mIECAll48Enabled + "; IECMonitorEnabled: " + this.mIECMonitorEnabled + "; mAllowedIECSkus: " + this.mAllowedIECSkus + "; monitorScenesFunc: " + this.mMonitorScenesFunc + "; IECTestScore: " + this.mIECTestScore + "; keepIECInterval: " + this.mKeepIECInterval + "; IECScreenOffTimePeriod: " + this.mIECScreenOffTimePeriod + "; allowedIECBatteryAvgTemp: " + this.mAllowedIECBatteryAvgTemp + "; allowedIECBatteryTemp: " + this.mAllowedIECBatteryTemp + "; IECScreenOffUpdateTime: " + this.mIECScreenOffUpdateTime + "; procStateLevel: " + this.mProcStateLevel + "; activityStateLevel: " + this.mProcActivityLevel + "; usedProcStateLevel: " + this.mIsUsedProcStateLevel + "; batteryMonitorEnabled:" + this.mBatteryMonitorEnabled + "; boardSensorTempEnabled:" + this.mBoardSensorTempEnabled + "; boardSensorTempValueThreshold:" + this.mBoardSensorTempValueThreshold + "; boardSensorTempResetValue:" + this.mBoardSensorTempResetValue + "; boardSensorTempUpDiffValue:" + this.mBoardSensorTempUpDiffValue + "; boardSensorTempDownDiffValue:" + this.mBoardSensorTempDownDiffValue + "; uploadForStatsEnabled: " + this.mUploadForStatsEnabled + "; uploadForStatsConfigSwitchEnabled: " + this.mUploadForStatsConfigSwitchEnabled + "; ambientTempMonitorEnabled: " + this.mAmbientTempEnabled + "; ambientTempVoteThresh: " + this.mAmbientTempVoteThresh + "; ambientTempVoteCountNumThresh: " + this.mAmbientTempVoteCountNumThresh + "; IECMonitorKillBatteryTemp: " + this.mIECKillBatteryTemp + "; IECMonitorKillSomeBackProBatteryTemp: " + this.mIECKillSomeBackProBatteryTemp + "; IECMonitorKillAllBackProBatteryTemp: " + this.mIECKillAllBackProBatteryTemp + "; IECMonitorAllowed: " + this.mIsIECMonitorAllowed + "; IECAll48Allowed: " + this.mIsIECAll48Allowed);
    }

    /* JADX WARN: Code duplicated, block: B:18:0x0034  */
    private void setIECStateBit(String str, boolean z2) {
        byte b2;
        int iHashCode = str.hashCode();
        if (iHashCode != -174015493) {
            if (iHashCode != -91195952) {
                if (iHashCode == 689810788 && str.equals(IEC_MONITOR_BYTE_STYLE_IS_IEC_TEST)) {
                    b2 = 0;
                } else {
                    b2 = -1;
                }
            } else if (str.equals(IEC_MONITOR_BYTE_STYLE_IS_IEC_START)) {
                b2 = 1;
            } else {
                b2 = -1;
            }
        } else if (str.equals(IEC_MONITOR_BYTE_STYLE_IS_IEC_BATTERY)) {
            b2 = 2;
        } else {
            b2 = -1;
        }
        if (b2 == 0) {
            if (z2) {
                this.IECStateByte = (byte) (this.IECStateByte | 1);
                return;
            } else {
                this.IECStateByte = (byte) (this.IECStateByte & 14);
                return;
            }
        }
        if (b2 == 1) {
            if (z2) {
                this.IECStateByte = (byte) (this.IECStateByte | 2);
                return;
            } else {
                this.IECStateByte = (byte) (this.IECStateByte & 12);
                return;
            }
        }
        if (b2 != 2) {
            Log.w(TAG, "warning for access here");
        } else if (this.mBatteryMonitorEnabled) {
            if (z2) {
                this.IECStateByte = (byte) (this.IECStateByte | 4);
            } else {
                this.IECStateByte = (byte) (this.IECStateByte & 11);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:20:0x006c  */
    public void storeIECMonitorPersistenceData(String str) {
        byte b2;
        if (DEBUG) {
            Log.d(TAG, "storeDataStyle: " + str + "; startIECTime: " + this.mStartIECTime + "; IECScreenOffOverPeriodTime: " + this.mIECScreenOffOverPeriodTime + "; allowedIECSwitchByBST: " + this.mAllowedIECSwitchByBST);
        }
        switch (str.hashCode()) {
            case -2023558277:
                if (!str.equals(THERMAL_IEC_MONITOR_PERSISTENCE_DATA_STYLE_IEC_STATE)) {
                    b2 = -1;
                } else {
                    b2 = 0;
                }
                break;
            case -1976862103:
                if (!str.equals(THERMAL_IEC_MONITOR_PERSISTENCE_IEC_SWITCH_BY_BST)) {
                    b2 = -1;
                } else {
                    b2 = 3;
                }
                break;
            case -1651770047:
                if (!str.equals(THERMAL_IEC_MONITOR_PERSISTENCE_DATA_STYLE_BATTERY_STATE)) {
                    b2 = -1;
                } else {
                    b2 = 1;
                }
                break;
            case 1687673007:
                if (!str.equals(THERMAL_IEC_MONITOR_PERSISTENCE_DATA_STYLE_SHUT_DOWN)) {
                    b2 = -1;
                } else {
                    b2 = 2;
                }
                break;
            default:
                b2 = -1;
                break;
        }
        if (b2 == 0) {
            if (getIECStateByte(IEC_MONITOR_BYTE_STYLE_IS_IEC_START).equals("10")) {
                this.mIsFirstIECMonitorEnd = true;
                SimpleSettings.Misc.putBoolean(this.mContext, THERMAL_IEC_MONITOR_PERSISTENCE_IS_START, true);
                SimpleSettings.Misc.putLong(this.mContext, THERMAL_IEC_MONITOR_PERSISTENCE_START_TIME, this.mStartIECTime);
                return;
            } else {
                if (this.mIsFirstIECMonitorEnd) {
                    this.mIsFirstIECMonitorEnd = false;
                    SimpleSettings.Misc.putBoolean(this.mContext, THERMAL_IEC_MONITOR_PERSISTENCE_IS_START, false);
                    return;
                }
                return;
            }
        }
        if (b2 == 1) {
            if (this.mBatteryMonitorEnabled) {
                SimpleSettings.Misc.putBoolean(this.mContext, THERMAL_IEC_MONITOR_PERSISTENCE_BATTERY_STATE, getIECStateByte(IEC_MONITOR_BYTE_STYLE_IS_IEC_BATTERY).equals(IEC_STATE_IS_IEC_LESS_FIXED_TEMP));
                SimpleSettings.Misc.putLong(this.mContext, THERMAL_IEC_MONITOR_PERSISTENCE_SCREEN_OFF_OVER_PERIOD_TIME, this.mIECScreenOffOverPeriodTime);
                return;
            }
            return;
        }
        if (b2 != 2) {
            if (b2 != 3) {
                Log.w(TAG, "warning for access here");
                return;
            } else {
                SimpleSettings.Misc.putBoolean(this.mContext, THERMAL_IEC_MONITOR_PERSISTENCE_IEC_SWITCH_BY_BST, this.mAllowedIECSwitchByBST);
                return;
            }
        }
        SimpleSettings.Misc.putBoolean(this.mContext, THERMAL_IEC_MONITOR_PERSISTENCE_IS_START, false);
        SimpleSettings.Misc.putLong(this.mContext, THERMAL_IEC_MONITOR_PERSISTENCE_START_TIME, -1L);
        SimpleSettings.Misc.putBoolean(this.mContext, THERMAL_IEC_MONITOR_PERSISTENCE_BATTERY_STATE, false);
        SimpleSettings.Misc.putLong(this.mContext, THERMAL_IEC_MONITOR_PERSISTENCE_SCREEN_OFF_OVER_PERIOD_TIME, -1L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAppSceneState() {
        if (this.monitorSceneMap.containsKey(MONITOR_GAME)) {
            this.monitorSceneMap.put(MONITOR_GAME, Boolean.valueOf(getSceneState(MONITOR_GAME)));
        }
        if (this.monitorSceneMap.containsKey("navigation")) {
            this.monitorSceneMap.put("navigation", Boolean.valueOf(getSceneState("navigation")));
        }
        if (this.monitorSceneMap.containsKey(MONITOR_VIDEO)) {
            this.monitorSceneMap.put(MONITOR_VIDEO, Boolean.valueOf(getSceneState(MONITOR_VIDEO)));
        }
    }

    public void destroy() {
        this.eventsAggregator.unregisterForForegroundInfo(this);
        this.eventsAggregator.unregisterForScreenOn(this);
        this.eventsAggregator.unregisterForScreenOff(this);
        this.mContext.unregisterReceiver(this.mReceiver);
        this.audioManager.unregisterAudioPlaybackCallback(this.audioStateCallback);
        this.monitorSceneMap.clear();
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) throws Throwable {
        int i2 = message.what;
        if (i2 == -40) {
            setFlashState(((Boolean) message.obj).booleanValue());
        }
        if (i2 == -34) {
            handleSmartPowerScenarioChanged(new long[]{0, ((Long) message.obj).longValue()});
            return;
        }
        if (i2 == -9) {
            handleForegroundDownloadStats(((ForegroundInfo) message.obj).mForegroundPackageName);
            updateAppSceneState();
            sendMessage(obtainMessage(8, Boolean.valueOf(isIECTest())));
            return;
        }
        if (i2 == -5) {
            this.mScreenOffTime = SystemClock.elapsedRealtime();
            this.mPrevBatteryTemp = -1;
            this.mAllowedForeDownloadStats = false;
            if (hasMessages(7, this)) {
                removeMessages(7, this);
                return;
            }
            return;
        }
        if (i2 == -4) {
            handleMsgScreenOn();
            return;
        }
        switch (i2) {
            case 0:
                sendMessage(obtainMessage(8, Boolean.valueOf(isIECTest())));
                break;
            case 1:
                handleBatteryTempState(((Integer) message.obj).intValue());
                break;
            case 2:
                int iIntValue = ((Integer) message.obj).intValue();
                if (iIntValue == 1) {
                    this.mIsCamera = true;
                } else if (iIntValue == 2) {
                    this.mIsCamera = false;
                }
                if (this.monitorSceneMap.containsKey("camera")) {
                    this.monitorSceneMap.put("camera", Boolean.valueOf(this.mIsCamera));
                    sendMessage(obtainMessage(8, Boolean.valueOf(isIECTest())));
                }
                break;
            case 3:
                if (this.monitorSceneMap.containsKey(MONITOR_VOICECALL)) {
                    if (this.mIsTeleCallActive || this.mIsVoipCallActive) {
                        this.monitorSceneMap.put(MONITOR_VOICECALL, Boolean.TRUE);
                    } else {
                        this.monitorSceneMap.put(MONITOR_VOICECALL, Boolean.FALSE);
                    }
                    sendMessage(obtainMessage(8, Boolean.valueOf(isIECTest())));
                }
                break;
            case 4:
                if (this.monitorSceneMap.containsKey("download")) {
                    this.monitorSceneMap.put("download", Boolean.valueOf(getDownloadState()));
                }
                sendMessage(obtainMessage(8, Boolean.valueOf(isIECTest())));
                break;
            case 5:
                if (this.monitorSceneMap.containsKey(MONITOR_VIDEO)) {
                    this.monitorSceneMap.put(MONITOR_VIDEO, Boolean.valueOf(getVideoPlayState()));
                }
                sendMessage(obtainMessage(8, Boolean.valueOf(isIECTest())));
                break;
            case 6:
                if (this.monitorSceneMap.containsKey("navigation")) {
                    this.monitorSceneMap.put("navigation", Boolean.valueOf(getNavigationState()));
                }
                sendMessage(obtainMessage(8, Boolean.valueOf(isIECTest())));
                break;
            case 7:
                handleForegroundDownloadStats();
                break;
            case 8:
                handleIsIECTestState(message);
                break;
            case 9:
                if (this.mUploadForStatsConfigSwitchEnabled) {
                    notifyIECStateChanged(98);
                    mIECState = 98;
                }
                this.mStartIECTime = SystemClock.elapsedRealtime();
                setIECStateBit(IEC_MONITOR_BYTE_STYLE_IS_IEC_START, true);
                storeIECMonitorPersistenceData(THERMAL_IEC_MONITOR_PERSISTENCE_DATA_STYLE_IEC_STATE);
                if (DEBUG) {
                    Log.d(TAG, "IECStateByte: " + Integer.toBinaryString(this.IECStateByte));
                }
                break;
            case 10:
                if (this.mUploadForStatsConfigSwitchEnabled) {
                    notifyIECStateChanged(99);
                    mIECState = 99;
                }
                setIECStateBit(IEC_MONITOR_BYTE_STYLE_IS_IEC_START, false);
                storeIECMonitorPersistenceData(THERMAL_IEC_MONITOR_PERSISTENCE_DATA_STYLE_IEC_STATE);
                if (DEBUG) {
                    Log.d(TAG, "IECStateByte: " + Integer.toBinaryString(this.IECStateByte));
                }
                break;
            default:
                Log.w(TAG, "warning for accessing here.");
                break;
        }
    }

    private void handleForegroundDownloadStats() {
        if (this.mIsIECMonitorAllowed) {
            double downloadSpeedKBPerSecond = getDownloadSpeedKBPerSecond();
            if (downloadSpeedKBPerSecond / 1024.0d >= 4.0d) {
                this.mNotDownloadCount = 0;
                int i2 = this.mDownloadCount + 1;
                this.mDownloadCount = i2;
                if (i2 == Integer.MAX_VALUE) {
                    this.mDownloadCount = 10;
                }
                if (this.mDownloadCount >= 10 && !this.mIsForegroundDownload) {
                    this.mIsForegroundDownload = true;
                    sendMessage(obtainMessage(4));
                }
            } else if (downloadSpeedKBPerSecond < 500.0d) {
                this.mDownloadCount = 0;
                int i3 = this.mNotDownloadCount + 1;
                this.mNotDownloadCount = i3;
                if (i3 == Integer.MAX_VALUE) {
                    this.mNotDownloadCount = 10;
                }
                if (this.mNotDownloadCount >= 10 && this.mIsForegroundDownload) {
                    this.mIsForegroundDownload = false;
                    sendMessage(obtainMessage(4));
                }
            } else {
                this.mDownloadCount = 0;
                this.mNotDownloadCount = 0;
            }
            if (this.mAllowedForeDownloadStats) {
                sendMessageDelayed(obtainMessage(7, this), 1000L);
            }
        }
    }
}
