package com.miui.powerkeeper.thermalcollector.collector;

import android.annotation.NonNull;
import android.app.ActivityManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.media.AudioRecordingConfiguration;
import android.media.audiofx.AudioEffectCenter;
import android.media.audiofx.MiEffectUtils;
import android.net.TrafficStats;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import com.miui.powerkeeper.PowerKeeperApplication;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.appcontrol.HideModeStateMachineConfig;
import com.miui.powerkeeper.event.EventLogTags;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.feedbackcontrol.ThermalInfoUtils;
import com.miui.powerkeeper.feedbackcontrol.ThermalManager;
import com.miui.powerkeeper.thermal.ScenarioManager;
import com.miui.powerkeeper.thermal.resource.ScenarioConfig;
import com.miui.powerkeeper.thermalcollector.CollectorManager;
import com.miui.powerkeeper.thermalcollector.source.CollectorConfig;
import com.miui.powerkeeper.utils.FloatSceneObserver;
import com.miui.powerkeeper.utils.Utils;
import com.xiaomi.channel.commonutils.android.AppInfoUtils;
import com.xiaomi.mipush.sdk.stat.db.MessageInfoContract;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.function.IntPredicate;
import miui.process.ForegroundInfo;
import miui.util.FeatureParser;

/* JADX INFO: loaded from: classes.dex */
public class ScenarioCollectorUtils {
    public static final String KEY_AUDIO = "audio";
    public static final String KEY_BATTERY_TEMP_ALLOW_KILL = "allowed_kill_battery_temp_threshhold";
    public static final String KEY_BOOT_STATE = "boot_state";
    public static final String KEY_CAMERA = "camera";
    public static final String KEY_CAMERA_STATE = "camera_state";
    public static final String KEY_CASTING = "casting";
    public static final String KEY_CHARGER_PROTOCOL = "charger_protocol";
    public static final String KEY_CHARGER_TEMP = "charger_temp";
    public static final String KEY_DOWNLOAD = "download";
    public static final String KEY_FAST_CHARGER_MODE = "fast_charger_mode";
    public static final String KEY_FLOAT_PACKAGE_NAME = "float_pkn";
    public static final String KEY_FLOAT_WINDOW = "float_window";
    public static final String KEY_FOREGROUND_PACKAGE_NAME = "foreground_pkn";
    public static final String KEY_GPS = "gps";
    public static final String KEY_HAND_STATE = "hand_state";
    public static final String KEY_HDR_STATE = "hdr_state";
    public static final String KEY_MAX_BOARD_TEMP = "max_board_temp";
    public static final String KEY_MAX_BOARD_TEMP_TIMESTAMP = "max_board_temp_timestamp";
    public static final String KEY_MAX_FLASH_TEMP = "max_flash_temp";
    public static final String KEY_MAX_FLASH_TEMP_TIMESTAMP = "max_flash_temp_timestamp";
    public static final String KEY_MICRO_PHONE_STATE = "micro_phone_state";
    public static final String KEY_NET_DOWN_SPEED = "net_down_speed";
    public static final String KEY_NET_UP_SPEED = "net_up_speed";
    public static final String KEY_POPUP_WINDOW_STATE = "popup_window_state";
    public static final String KEY_POWER_MODE = "power_mode";
    public static final String KEY_SCENARIO_NAME = "scenario_name";
    public static final String KEY_SCREEN = "screen";
    public static final String KEY_SECURE_FAST_CHARGE_ENABLED = "key_fast_charge_enabled";
    public static final String KEY_SPLIT = "split";
    public static final String KEY_THERMAL_GROUP = "thermal_group";
    public static final String KEY_TORCH_STATE = "torch_state";
    public static final String KEY_VIDEO_TOOL_BOX_STATE = "video_tool_box_state";
    private static final int MESSAGE_STATE_CAMERA_CHANGED = 0;
    private static final int SENSOR_TYPE_HANDSENSOR = 33171120;
    private static final String TAG = "ScenarioCollectorUtils";
    private static final String VALUE_DEFAULT = "NULL";
    private static final String VALUE_OFF = "OFF";
    private static final String VALUE_ON = "ON";
    public static final String VB_SYSTEM_PROPERTIES_FRC_X7 = "extreme_video_mode_switch";
    private static ScenarioCollectorUtils sInstance;
    private FlashStateListener mFlashStateListener;
    private SensorManager mSensorManager;
    private Sensor mSensorSubstanceHand;
    private final EventsAggregator mAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
    private final ScenarioHandler mHandler = new ScenarioHandler(CollectorManager.getThread().getLooper(), this);
    private final int SENSOR_DESTKTOP_STATE = 1;
    private final int SENSOR_HOLDER_STATE = 2;
    private final int SENSOR_SINGLE_HAND_STATE = 3;
    private final int SENSOR_DOUBLE_HANDS_HORIZONTAL = 4;
    private final int SENSOR_DOUBLE_HANDS_VERTICAL = 5;
    private final int SENSOR_READY_TO_LISTEN = 6;
    private final String CHARGER_TEMP_PATH = "/sys/class/thermal/thermal_message/charger_temp";
    private String mFloatPkn = VALUE_DEFAULT;
    private String mFloatWindow = VALUE_DEFAULT;
    private String mCamera = VALUE_DEFAULT;
    private String mFrontCamera = VALUE_DEFAULT;
    private String mBackCamera = VALUE_DEFAULT;
    private String mCasting = VALUE_DEFAULT;
    private String mDownload = VALUE_DEFAULT;
    private String mGps = VALUE_DEFAULT;
    private String mAudio = VALUE_DEFAULT;
    private String mSplit = VALUE_DEFAULT;
    private String mRecord = VALUE_DEFAULT;
    private String mMicroPhone = VALUE_DEFAULT;
    private String mOffHookCallState = VALUE_DEFAULT;
    private String mScreenState = VALUE_DEFAULT;
    private String mCurForeGroundPkg = VALUE_DEFAULT;
    private int mhandSensorState = 100;
    private boolean mIsFirstBoot = false;
    private int mPlugType = 0;
    private boolean mCurrentFlashState = false;
    private int mCurrentFlashTemp = 0;
    private int mCurrentAmbientTemp = -1;
    private int mMaxBoardTemp = 0;
    private int mMaxFlashTemp = 0;
    private long mMaxBoardTempTimeStamp = 0;
    private long mMaxFlashTempTimeStamp = 0;
    private boolean mCameraTrigger = false;
    private boolean mMicroPhoneTrigger = false;
    private int mCurrentCameraState = 0;
    private int mPopupWindowStatus = 1000;
    private final Map<String, Map<String, String>> mUndefCScenarioParams = new HashMap();
    private final Map<String, Integer> mUndefCScenariosStats = new HashMap();
    private final Map<String, Boolean> mUndefCScenariosBackStats = new HashMap();
    private final Map<String, String> mUndefCScenariosFWindowStats = new HashMap();
    private String mUndefListenForeGroundPkgs = "";
    private String mUndefListenBackGroundPkgs = "";
    private String mUndefListenFloatWindows = "";
    private String mUndefListenCallStates = "";
    private long mCurAdditionalScenarioId = 0;
    private Stack<String> mUploadUndefCScenarioIds = new Stack<>();
    private long mPrevDownloadTimeStamp = -1;
    private long mPrevUploadTimeStamp = -1;
    private long mPrevDownloadData = -1;
    private long mPrevUploadData = -1;
    private PowerKeeperInterface.m mBatteryStatusReceiver = new PowerKeeperInterface.m() { // from class: com.miui.powerkeeper.thermalcollector.collector.ScenarioCollectorUtils.2
        @Override // com.miui.powerkeeper.PowerKeeperInterface.m
        public void onBatteryChanged(int i2, int i3, int i4, int i5, int i6, int i7) {
            ScenarioCollectorUtils.this.mPlugType = i6;
        }
    };
    private CameraManager.AvailabilityCallback mAvailabilityCallback = new CameraManager.AvailabilityCallback() { // from class: com.miui.powerkeeper.thermalcollector.collector.ScenarioCollectorUtils.3
        @Override // android.hardware.camera2.CameraManager.AvailabilityCallback
        public void onCameraAvailable(String str) {
            if (1 == Integer.parseInt(str)) {
                ScenarioCollectorUtils.this.mFrontCamera = ScenarioCollectorUtils.VALUE_OFF;
            } else if (Integer.parseInt(str) == 0) {
                ScenarioCollectorUtils.this.mBackCamera = ScenarioCollectorUtils.VALUE_OFF;
            }
            int cameraState = ScenarioCollectorUtils.this.getCameraState();
            if (cameraState != ScenarioCollectorUtils.this.mCurrentCameraState) {
                ScenarioCollectorUtils.this.mCurrentCameraState = cameraState;
            }
        }

        @Override // android.hardware.camera2.CameraManager.AvailabilityCallback
        public void onCameraUnavailable(String str) {
            if (1 == Integer.parseInt(str)) {
                ScenarioCollectorUtils.this.mFrontCamera = ScenarioCollectorUtils.VALUE_ON;
            } else if (Integer.parseInt(str) == 0) {
                ScenarioCollectorUtils.this.mBackCamera = ScenarioCollectorUtils.VALUE_ON;
            }
            int cameraState = ScenarioCollectorUtils.this.getCameraState();
            if (cameraState != ScenarioCollectorUtils.this.mCurrentCameraState) {
                ScenarioCollectorUtils.this.mCurrentCameraState = cameraState;
            }
        }
    };
    private final SensorEventListener mSensorEventListener = new SensorEventListener() { // from class: com.miui.powerkeeper.thermalcollector.collector.ScenarioCollectorUtils.4
        @Override // android.hardware.SensorEventListener
        public void onSensorChanged(SensorEvent sensorEvent) {
            try {
                if (sensorEvent.sensor.getType() == ScenarioCollectorUtils.SENSOR_TYPE_HANDSENSOR) {
                    ScenarioCollectorUtils.this.mhandSensorState = (int) sensorEvent.values[0];
                }
            } catch (Exception e2) {
                Log.e(ScenarioCollectorUtils.TAG, "onSensorChanged: error cause :" + e2);
            }
        }

        @Override // android.hardware.SensorEventListener
        public void onAccuracyChanged(Sensor sensor, int i2) {
        }
    };

    private class FlashStateListener extends FileObserver {
        public FlashStateListener(String str) {
            super(str);
        }

        @Override // android.os.FileObserver
        public void onEvent(int i2, String str) {
            boolean flashStateNode;
            if (i2 != 2 || ScenarioCollectorUtils.this.mCurrentFlashState == (flashStateNode = ScenarioCollectorUtils.this.getFlashStateNode())) {
                return;
            }
            ScenarioCollectorUtils.this.mAggregator.notifyForFlashStateChanged(flashStateNode);
            ScenarioCollectorUtils.this.mCurrentFlashState = flashStateNode;
            String flashTemp = ThermalManager.ThermalTempInfoHelper.getFlashTemp();
            int i3 = Utils.isNumber(flashTemp) ? Integer.parseInt(flashTemp) : 0;
            ScenarioCollectorUtils.this.mCurrentFlashTemp = i3;
            PowerKeeperManager.getInstance().getEventsAggregator().notifyForFlashTemperature(i3);
        }
    }

    private static class ScenarioHandler extends Handler {
        SoftReference<ScenarioCollectorUtils> mCollectorReference;

        public ScenarioHandler(@NonNull Looper looper, ScenarioCollectorUtils scenarioCollectorUtils) {
            super(looper);
            this.mCollectorReference = new SoftReference<>(scenarioCollectorUtils);
        }

        @Override // android.os.Handler
        public void handleMessage(@NonNull Message message) {
            ScenarioCollectorUtils scenarioCollectorUtils = this.mCollectorReference.get();
            int i2 = message.what;
            String str = ScenarioCollectorUtils.VALUE_OFF;
            if (i2 == -36) {
                if (((Boolean) message.obj).booleanValue()) {
                    str = ScenarioCollectorUtils.VALUE_ON;
                }
                if (str.equals(scenarioCollectorUtils.mOffHookCallState)) {
                    return;
                }
                scenarioCollectorUtils.mOffHookCallState = str;
                return;
            }
            if (i2 == -34) {
                scenarioCollectorUtils.setSmartScenario(((Long) message.obj).longValue());
                return;
            }
            if (i2 == -31) {
                scenarioCollectorUtils.setFloatWindow((FloatSceneObserver.FloatWindowState) message.obj);
                return;
            }
            if (i2 == -9) {
                String str2 = ((ForegroundInfo) message.obj).mForegroundPackageName;
                if (str2.equals(scenarioCollectorUtils.mCurForeGroundPkg)) {
                    return;
                }
                scenarioCollectorUtils.mCurForeGroundPkg = str2;
                if (CollectorManager.getCollectedCompoundScenarios().isEmpty()) {
                    return;
                }
                scenarioCollectorUtils.onCScenarioStateVerify(1);
                return;
            }
            if (i2 == 1) {
                int iIntValue = ((Integer) message.obj).intValue();
                if (iIntValue == 4) {
                    scenarioCollectorUtils.setRecordState(ScenarioCollectorUtils.VALUE_ON);
                    return;
                } else {
                    if (iIntValue == 3) {
                        scenarioCollectorUtils.setRecordState(ScenarioCollectorUtils.VALUE_OFF);
                        return;
                    }
                    return;
                }
            }
            if (i2 == 15) {
                Object obj = message.obj;
                if (obj != null) {
                    scenarioCollectorUtils.onCScenarioStateVerify(((Integer) obj).intValue() + 1);
                    return;
                }
                return;
            }
            if (i2 != -5) {
                if (i2 == -4 && !scenarioCollectorUtils.mScreenState.equals(ScenarioCollectorUtils.VALUE_ON)) {
                    scenarioCollectorUtils.mScreenState = ScenarioCollectorUtils.VALUE_ON;
                    return;
                }
                return;
            }
            if (scenarioCollectorUtils.mScreenState.equals(ScenarioCollectorUtils.VALUE_OFF)) {
                return;
            }
            scenarioCollectorUtils.mScreenState = ScenarioCollectorUtils.VALUE_OFF;
            if (CollectorManager.getCollectedCompoundScenarios().isEmpty()) {
                return;
            }
            scenarioCollectorUtils.onResetCScenarioState();
        }
    }

    private ScenarioCollectorUtils() {
        this.mFlashStateListener = null;
        initDefaultValue();
        registerListener();
        FlashStateListener flashStateListener = new FlashStateListener(CollectorConfig.FLASH_STATE_PATH);
        this.mFlashStateListener = flashStateListener;
        flashStateListener.startWatching();
        CameraManager cameraManager = (CameraManager) PowerKeeperApplication.b().getSystemService("camera");
        if (cameraManager != null) {
            cameraManager.registerAvailabilityCallback(this.mAvailabilityCallback, new Handler());
        }
    }

    public static int getCurrentPowerMode() {
        return PowerKeeperManager.getInstance().getEventsAggregator().getPowerMode();
    }

    public static int getCurrentScenarioId() {
        return ScenarioManager.getInstance().getCurrentScenarioName();
    }

    public static int getFastChargeEnabled() {
        return Settings.Secure.getInt(PowerKeeperApplication.b().getContentResolver(), KEY_SECURE_FAST_CHARGE_ENABLED, -1);
    }

    /* JADX WARN: Code duplicated, block: B:40:0x0096 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    public static String getFileNodeState(String str) throws Throwable {
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
    public boolean getFlashStateNode() throws Throwable {
        String fileNodeState = getFileNodeState(CollectorConfig.FLASH_STATE_PATH);
        return !fileNodeState.equals("") && Integer.parseInt(fileNodeState) == 1;
    }

    public static ScenarioCollectorUtils getInstance() {
        ScenarioCollectorUtils scenarioCollectorUtils = sInstance;
        if (scenarioCollectorUtils != null) {
            return scenarioCollectorUtils;
        }
        ScenarioCollectorUtils scenarioCollectorUtils2 = new ScenarioCollectorUtils();
        sInstance = scenarioCollectorUtils2;
        return scenarioCollectorUtils2;
    }

    public static boolean getTorchState() {
        return ThermalInfoUtils.isTorchState(PowerKeeperApplication.b());
    }

    private void initDefaultValue() {
        this.mFloatPkn = VALUE_DEFAULT;
        this.mFloatWindow = VALUE_OFF;
        this.mCamera = VALUE_OFF;
        this.mBackCamera = VALUE_OFF;
        this.mFrontCamera = VALUE_OFF;
        this.mCasting = VALUE_OFF;
        this.mDownload = VALUE_OFF;
        this.mGps = VALUE_OFF;
        this.mAudio = VALUE_OFF;
        this.mSplit = VALUE_OFF;
        this.mRecord = VALUE_OFF;
        this.mMicroPhone = VALUE_OFF;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$isCompoundScenarioId$3(int i2, int i3) {
        return i3 == i2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ Integer lambda$onResetCScenarioState$0(String str, Integer num) {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ Boolean lambda$onResetCScenarioState$1(String str, Boolean bool) {
        return Boolean.FALSE;
    }

    private void registerListener() {
        PowerKeeperManager.getInstance().registerBatteryChangedListener(new PowerKeeperInterface.d(this.mHandler, this.mBatteryStatusReceiver));
        ((AudioManager) PowerKeeperApplication.b().getSystemService("audio")).registerAudioRecordingCallback(new AudioManager.AudioRecordingCallback() { // from class: com.miui.powerkeeper.thermalcollector.collector.ScenarioCollectorUtils.1
            @Override // android.media.AudioManager.AudioRecordingCallback
            public void onRecordingConfigChanged(List<AudioRecordingConfiguration> list) {
                String str = list.size() == 0 ? ScenarioCollectorUtils.VALUE_OFF : ScenarioCollectorUtils.VALUE_ON;
                if (ScenarioCollectorUtils.this.mMicroPhone.equals(str)) {
                    return;
                }
                ScenarioCollectorUtils.this.setMicroPhoneState(str);
            }
        }, new Handler());
        SensorManager sensorManager = (SensorManager) PowerKeeperApplication.b().getSystemService(HideModeStateMachineConfig.STRATEGY_SENSOR);
        this.mSensorManager = sensorManager;
        if (sensorManager != null) {
            Sensor defaultSensor = sensorManager.getDefaultSensor(SENSOR_TYPE_HANDSENSOR);
            this.mSensorSubstanceHand = defaultSensor;
            if (defaultSensor != null) {
                this.mSensorManager.registerListener(this.mSensorEventListener, defaultSensor, 3);
            }
        }
        this.mAggregator.registerForFloatSceneEvent(this.mHandler);
        this.mAggregator.registerSmartPowerScenarioIdChanged(this.mHandler);
        this.mAggregator.registerForCallState(this.mHandler);
        this.mAggregator.registerForForegroundInfo(this.mHandler);
        this.mAggregator.registerForScreenOff(this.mHandler, null);
        this.mAggregator.registerForScreenOn(this.mHandler, null);
        this.mAggregator.registerForOffHookCallState(this.mHandler);
        this.mAggregator.registerForVideoRecordState(this.mHandler, 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFloatWindow(FloatSceneObserver.FloatWindowState floatWindowState) {
        this.mFloatWindow = floatWindowState.getIsFloated() ? VALUE_ON : VALUE_OFF;
        this.mFloatPkn = floatWindowState.getIsFloated() ? floatWindowState.getPkgName() : VALUE_DEFAULT;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setMicroPhoneState(String str) {
        this.mMicroPhone = str;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setRecordState(String str) {
        this.mRecord = str;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSmartScenario(long j2) {
        if (j2 != this.mCurAdditionalScenarioId) {
            this.mCurAdditionalScenarioId = j2;
        }
        if ((ScenarioConfig.INTERACTIVE_ACTION_TYPE_CASTING & j2) != 0) {
            this.mCasting = VALUE_ON;
        } else {
            this.mCasting = VALUE_OFF;
        }
        if ((ScenarioConfig.INTERACTIVE_ACTION_TYPE_DOWNLOAD & j2) == 0 && (ScenarioConfig.INTERACTIVE_ACTION_TYPE_DOWNLOAD_MARKET & j2) == 0 && (ScenarioConfig.INTERACTIVE_ACTION_TYPE_DOWNLOAD_XUNLEI & j2) == 0) {
            this.mDownload = VALUE_OFF;
        } else {
            this.mDownload = VALUE_ON;
        }
        if ((ScenarioConfig.INTERACTIVE_ACTION_TYPE_VIDEOPLAY & j2) != 0) {
            this.mAudio = VALUE_ON;
        } else {
            this.mAudio = VALUE_OFF;
        }
        if ((ScenarioConfig.INTERACTIVE_ACTION_TYPE_SPLIT & j2) != 0) {
            this.mSplit = VALUE_ON;
        } else {
            this.mSplit = VALUE_OFF;
        }
        if ((j2 & ScenarioConfig.INTERACTIVE_ACTION_TYPE_CAMERA) != 0) {
            this.mCamera = VALUE_ON;
        } else {
            this.mCamera = VALUE_OFF;
            this.mFrontCamera = VALUE_OFF;
            this.mBackCamera = VALUE_OFF;
        }
        int cameraState = getCameraState();
        if (cameraState != this.mCurrentCameraState) {
            this.mCurrentCameraState = cameraState;
        }
    }

    public int getAmbientTemp() {
        return this.mCurrentAmbientTemp;
    }

    public int getCameraState() {
        if (!VALUE_ON.equals(this.mCamera)) {
            return 0;
        }
        if (VALUE_ON.equals(this.mBackCamera) && VALUE_ON.equals(this.mFrontCamera)) {
            return 3;
        }
        if (VALUE_ON.equals(this.mBackCamera)) {
            return 1;
        }
        return VALUE_ON.equals(this.mFrontCamera) ? 2 : 4;
    }

    public boolean getCameraTrigger() {
        return this.mCameraTrigger;
    }

    public String getCastingState() {
        return this.mCasting;
    }

    public String getChargerTemp() {
        return getFileNodeState("/sys/class/thermal/thermal_message/charger_temp");
    }

    public String getChargingProtocol() {
        return getFileNodeState(FeatureParser.getBoolean("is_mediatek", false) ? "/sys/class/power_supply/wireless/tx_adapter" : "/sys/class/qcom-battery/tx_adapter");
    }

    public int getChargingState() {
        return this.mPlugType;
    }

    public int getDownloadSpeed() {
        int i2;
        long totalRxBytes = TrafficStats.getTotalRxBytes();
        long jCurrentTimeMillis = System.currentTimeMillis();
        long j2 = this.mPrevDownloadTimeStamp;
        if (j2 != -1) {
            i2 = (int) (((totalRxBytes - this.mPrevDownloadData) / 1024) / ((jCurrentTimeMillis - j2) / 1000));
        } else {
            i2 = -1;
        }
        this.mPrevDownloadTimeStamp = jCurrentTimeMillis;
        this.mPrevDownloadData = totalRxBytes;
        return i2;
    }

    public boolean getFirstBootState() {
        return this.mIsFirstBoot;
    }

    public int getFlashScenarioId() {
        if (!this.mCurrentFlashState) {
            return 0;
        }
        if (VALUE_ON.equals(this.mRecord)) {
            return 2;
        }
        if (VALUE_ON.equals(this.mCamera)) {
            return "com.android.camera".equals(PowerKeeperManager.getInstance().getEventsAggregator().getForegroundInfo().mForegroundPackageName) ? 1 : 3;
        }
        return getTorchState() ? 4 : 5;
    }

    public boolean getFlashState() {
        return this.mCurrentFlashState;
    }

    public int getFlashTemp() {
        return this.mCurrentFlashTemp;
    }

    public String getHDRState() {
        return Settings.System.getIntForUser(PowerKeeperApplication.b().getContentResolver(), ScenarioConfig.BRIGHTNESS_THROTTLER_STATUS, 0, -2) == 1 ? VALUE_ON : VALUE_OFF;
    }

    public int getHandState() {
        int i2 = this.mhandSensorState;
        if (i2 == -1) {
            return 1;
        }
        if (i2 == 0) {
            return 2;
        }
        if (i2 == 1 || i2 == 2) {
            return 3;
        }
        if (i2 == 4) {
            return 5;
        }
        if (i2 == 5 || i2 == 6) {
            return 3;
        }
        if (i2 == 12 || i2 == 16) {
            return 4;
        }
        return (i2 == 101 || i2 == 102) ? 6 : -1;
    }

    public int getMaxBoardTemp() {
        return this.mMaxBoardTemp;
    }

    public long getMaxBoardTempTimeStamp() {
        return this.mMaxBoardTempTimeStamp;
    }

    public int getMaxFlashTemp() {
        return this.mMaxFlashTemp;
    }

    public long getMaxFlashTempTimeStamp() {
        return this.mMaxFlashTempTimeStamp;
    }

    public String getMicroPhoneState() {
        return this.mMicroPhone;
    }

    public boolean getMicroPhoneTrigger() {
        return this.mMicroPhoneTrigger;
    }

    public int getPopUpWindowStatus() {
        return this.mPopupWindowStatus;
    }

    public boolean getRecordState() {
        return VALUE_ON.equals(this.mRecord);
    }

    public int getUploadSpeed() {
        int i2;
        long totalTxBytes = TrafficStats.getTotalTxBytes();
        long jCurrentTimeMillis = System.currentTimeMillis();
        long j2 = this.mPrevUploadTimeStamp;
        if (j2 != -1) {
            i2 = (int) (((totalTxBytes - this.mPrevUploadData) / 1024) / ((jCurrentTimeMillis - j2) / 1000));
        } else {
            i2 = -1;
        }
        this.mPrevUploadTimeStamp = jCurrentTimeMillis;
        this.mPrevUploadData = totalTxBytes;
        return i2;
    }

    /* JADX WARN: Code duplicated, block: B:21:0x0079  */
    /* JADX WARN: Code duplicated, block: B:23:0x007d  */
    /* JADX WARN: Code duplicated, block: B:26:0x0086  */
    /* JADX WARN: Code duplicated, block: B:30:0x0091 A[PHI: r4
      0x0091: PHI (r4v13 int) = (r4v8 int), (r4v15 int) binds: [B:36:0x00a5, B:28:0x008e] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:31:0x0094  */
    /* JADX WARN: Code duplicated, block: B:34:0x009b  */
    /* JADX WARN: Code duplicated, block: B:40:0x00ae  */
    public String getVideoToolStyle() {
        boolean zEquals = Objects.equals(SystemProperties.get("ro.vendor.audio.fweffect", ""), "true");
        AudioEffectCenter audioEffectCenter = AudioEffectCenter.getInstance(PowerKeeperApplication.b());
        a.a aVar = new a.a(-1, 0);
        AudioManager audioManager = (AudioManager) PowerKeeperApplication.b().getSystemService("audio");
        int i2 = SystemProperties.getBoolean("debug.media.video.ais", false) ? 11111111 : 10111111;
        if (SystemProperties.getBoolean("ro.vendor.media.video.style.support", false)) {
            if (SystemProperties.getInt("debug.media.video.style", 0) == 0) {
            }
            if (Settings.Global.getInt(PowerKeeperApplication.b().getContentResolver(), VB_SYSTEM_PROPERTIES_FRC_X7, 0) != 1 && !SystemProperties.getBoolean("debug.media.video.frc ", false)) {
                i2 -= 10000;
            }
            if (!SystemProperties.getBoolean("debug.media.video.vpp", false)) {
                i2 -= 1000;
            }
            if (zEquals) {
                if (!audioEffectCenter.isEffectActive("dolby")) {
                    i2 -= 100;
                }
                if (!audioEffectCenter.isEffectActive("spatial")) {
                    i2 -= 10;
                }
            } else {
                if (!aVar.d()) {
                    i2 -= 100;
                }
                if (!audioManager.getSpatializer().isEnabled()) {
                    i2 -= 10;
                }
            }
            if (!MiEffectUtils.getSpatialStatus()) {
                i2--;
            }
            return String.valueOf(i2);
        }
        FeatureParser.getBoolean("support_videobox_display_effect", false);
        i2 -= AppInfoUtils.PATTERN;
        if (Settings.Global.getInt(PowerKeeperApplication.b().getContentResolver(), VB_SYSTEM_PROPERTIES_FRC_X7, 0) != 1) {
            i2 -= 10000;
        }
        if (!SystemProperties.getBoolean("debug.media.video.vpp", false)) {
            i2 -= 1000;
        }
        if (zEquals) {
            if (!audioEffectCenter.isEffectActive("dolby")) {
                i2 -= 100;
            }
            if (!audioEffectCenter.isEffectActive("spatial")) {
                i2 -= 10;
            }
        } else {
            if (!aVar.d()) {
                i2 -= 100;
            }
            if (!audioManager.getSpatializer().isEnabled()) {
                i2 -= 10;
            }
        }
        if (!MiEffectUtils.getSpatialStatus()) {
            i2--;
        }
        return String.valueOf(i2);
    }

    public boolean isCompoundScenarioId(final int i2) {
        return Arrays.stream(CollectorConfig.COMPOUND_SCENARIO_IDS).anyMatch(new IntPredicate() { // from class: com.miui.powerkeeper.thermalcollector.collector.b
            @Override // java.util.function.IntPredicate
            public final boolean test(int i3) {
                return ScenarioCollectorUtils.lambda$isCompoundScenarioId$3(i2, i3);
            }
        });
    }

    public void onBackGroundStateVerify() {
        ActivityManager activityManager = (ActivityManager) PowerKeeperApplication.b().getSystemService("activity");
        List<ActivityManager.RunningAppProcessInfo> arrayList = new ArrayList<>();
        if (activityManager != null) {
            arrayList = activityManager.getRunningAppProcesses();
        }
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : arrayList) {
            if (this.mUndefListenBackGroundPkgs.contains(runningAppProcessInfo.processName) && runningAppProcessInfo.processState < 15) {
                this.mUndefCScenariosBackStats.put(runningAppProcessInfo.processName, Boolean.TRUE);
            }
        }
    }

    public void onCScenarioStateVerify(int i2) {
        int virtualBoardSensorTemp;
        if (i2 == 1) {
            onResetCScenarioState();
            if (this.mUndefListenForeGroundPkgs.contains(this.mCurForeGroundPkg)) {
                Message messageObtainMessage = this.mHandler.obtainMessage(15);
                messageObtainMessage.obj = Integer.valueOf(i2);
                this.mHandler.sendMessageDelayed(messageObtainMessage, MessageInfoContract.TIMEOUT);
            }
        }
        if (i2 == 2) {
            onBackGroundStateVerify();
            onFloatWindowStateVerify();
            onMultiSingleScenarioVerify(i2);
            Message messageObtainMessage2 = this.mHandler.obtainMessage(15);
            messageObtainMessage2.obj = Integer.valueOf(i2);
            this.mHandler.sendMessageDelayed(messageObtainMessage2, MessageInfoContract.TIMEOUT);
        }
        if (i2 == 3) {
            onBackGroundStateVerify();
            onFloatWindowStateVerify();
            onMultiSingleScenarioVerify(i2);
            if (!this.mUndefCScenariosStats.containsValue(3) || (virtualBoardSensorTemp = ThermalManager.ThermalTempInfoHelper.getVirtualBoardSensorTemp()) == -1) {
                return;
            }
            for (String str : this.mUndefCScenariosStats.keySet()) {
                if (this.mUndefCScenariosStats.get(str).intValue() == 3) {
                    this.mUploadUndefCScenarioIds.add(str);
                    PowerKeeperManager.getInstance().getEventsAggregator().notifyForBoardTemperature(virtualBoardSensorTemp);
                }
            }
        }
    }

    public void onFloatWindowStateVerify() {
        if ((this.mCurAdditionalScenarioId & ScenarioConfig.INTERACTIVE_ACTION_TYPE_VIDEOCALL_WECHAT) != 0) {
            this.mUndefCScenariosFWindowStats.put(CollectorConfig.FLOAT_WINDOW_WECHAT_VIDEOCHAT, VALUE_ON);
        } else {
            this.mUndefCScenariosFWindowStats.put(CollectorConfig.FLOAT_WINDOW_WECHAT_VIDEOCHAT, VALUE_OFF);
        }
        if ((this.mCurAdditionalScenarioId & 4) != 0) {
            this.mUndefCScenariosFWindowStats.put(CollectorConfig.FLOAT_WINDOW_WECHAT_VOICECHAT, VALUE_ON);
        } else {
            this.mUndefCScenariosFWindowStats.put(CollectorConfig.FLOAT_WINDOW_WECHAT_VOICECHAT, VALUE_OFF);
        }
    }

    public void onMultiSingleScenarioVerify(int i2) {
        boolean z2;
        for (String str : this.mUndefCScenarioParams.keySet()) {
            Map<String, String> map = this.mUndefCScenarioParams.get(str);
            Iterator<String> it = map.keySet().iterator();
            while (true) {
                if (!it.hasNext()) {
                    z2 = true;
                    break;
                }
                String next = it.next();
                String str2 = map.get(next);
                z2 = false;
                if (CollectorConfig.CLOUD_COMPOUND_SCENARIOS_FOREGROUND_INFO.equals(next)) {
                    if (!str2.equals(this.mCurForeGroundPkg)) {
                        break;
                    }
                } else if (CollectorConfig.CLOUD_COMPOUND_SCENARIOS_BACKGROUND_INFO.equals(next)) {
                    if (this.mUndefCScenariosBackStats.containsKey(str2) && !this.mUndefCScenariosBackStats.get(str2).booleanValue()) {
                        break;
                    }
                } else if (CollectorConfig.CLOUD_COMPOUND_SCENARIOS_CALL_INFO.equals(next)) {
                    if (!VALUE_ON.equals(this.mOffHookCallState)) {
                        break;
                    }
                } else {
                    if (CollectorConfig.CLOUD_COMPOUND_SCENARIOS_FLOATWINDOW_INFO.equals(next) && this.mUndefCScenariosFWindowStats.containsKey(str2) && ("com.tencent.mm".equals(str2) || !this.mUndefCScenariosFWindowStats.get(str2).equals(VALUE_ON))) {
                        break;
                    }
                }
            }
            if (i2 == 2) {
                if (z2) {
                    this.mUndefCScenariosStats.put(str, Integer.valueOf(i2));
                }
            } else if (i2 == 3 && z2 && this.mUndefCScenariosStats.get(str).intValue() == 2) {
                this.mUndefCScenariosStats.put(str, Integer.valueOf(i2));
            }
        }
    }

    public void onResetCScenarioState() {
        if (this.mHandler.hasMessages(15)) {
            this.mHandler.removeMessages(15);
        }
        this.mUndefCScenariosStats.replaceAll(new BiFunction() { // from class: com.miui.powerkeeper.thermalcollector.collector.c
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ScenarioCollectorUtils.lambda$onResetCScenarioState$0((String) obj, (Integer) obj2);
            }
        });
        this.mUndefCScenariosBackStats.replaceAll(new BiFunction() { // from class: com.miui.powerkeeper.thermalcollector.collector.d
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ScenarioCollectorUtils.lambda$onResetCScenarioState$1((String) obj, (Boolean) obj2);
            }
        });
        this.mUndefCScenariosFWindowStats.replaceAll(new BiFunction() { // from class: com.miui.powerkeeper.thermalcollector.collector.e
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ScenarioCollectorUtils.VALUE_DEFAULT;
            }
        });
        this.mUploadUndefCScenarioIds.clear();
    }

    public String peekUploadScenarioId() {
        return !this.mUploadUndefCScenarioIds.isEmpty() ? this.mUploadUndefCScenarioIds.peek() : "";
    }

    public String popUploadScenarioId() {
        return !this.mUploadUndefCScenarioIds.isEmpty() ? this.mUploadUndefCScenarioIds.pop() : "";
    }

    public void resetTrafficData() {
        long totalRxBytes = TrafficStats.getTotalRxBytes();
        long totalTxBytes = TrafficStats.getTotalTxBytes();
        long jCurrentTimeMillis = System.currentTimeMillis();
        this.mPrevDownloadData = totalRxBytes;
        this.mPrevUploadData = totalTxBytes;
        this.mPrevDownloadTimeStamp = jCurrentTimeMillis;
        this.mPrevUploadTimeStamp = jCurrentTimeMillis;
    }

    public void setAmbientTemp(int i2) {
        this.mCurrentAmbientTemp = i2;
    }

    public void setCameraTrigger(boolean z2) {
        this.mCameraTrigger = z2;
    }

    public void setFirstBootState(boolean z2) {
        this.mIsFirstBoot = z2;
    }

    public void setFlashTemp(int i2) {
        this.mCurrentFlashTemp = i2;
    }

    public void setMaxBoardTempInfo(int i2, long j2) {
        this.mMaxBoardTemp = i2;
        this.mMaxBoardTempTimeStamp = j2;
    }

    public void setMaxFlashTempInfo(int i2, long j2) {
        this.mMaxFlashTemp = i2;
        this.mMaxFlashTempTimeStamp = j2;
    }

    public void setMicroPhoneTrigger(boolean z2) {
        this.mMicroPhoneTrigger = z2;
    }

    public void setPopupWindowState(int i2) {
        int i3 = Settings.Secure.getIntForUser(PowerKeeperApplication.b().getContentResolver(), "allowed_kill_battery_temp_threshhold", 0, -2) == 1 ? 1111 : 1011;
        if ((i2 / EventLogTags.EVENT_USER_DEFINE_START) % 10 == 0) {
            i3 -= 10;
        }
        String str = this.mAggregator.getForegroundInfo().mForegroundPackageName;
        if (i2 % 10 != 1 || !"com.android.camera".equals(str)) {
            i3--;
        }
        this.mPopupWindowStatus = i3;
    }

    public void setUndefCompoundScenarios(String str, Map<String, String> map) {
        this.mUndefCScenarioParams.put(str, map);
        this.mUndefCScenariosStats.put(str, 0);
    }

    public void setUndefListenBackGroundPkgs(String str) {
        this.mUndefListenBackGroundPkgs = str;
        for (String str2 : str.split(":")) {
            this.mUndefCScenariosBackStats.put(str2, Boolean.FALSE);
        }
    }

    public void setUndefListenCallStates(String str) {
        this.mUndefListenCallStates = str;
    }

    public void setUndefListenFloatWindows(String str) {
        this.mUndefListenFloatWindows = str;
        for (String str2 : str.split(":")) {
            this.mUndefCScenariosFWindowStats.put(str2, VALUE_DEFAULT);
        }
    }

    public void setUndefListenForeGroundPkgs(String str) {
        this.mUndefListenForeGroundPkgs = str;
    }
}
