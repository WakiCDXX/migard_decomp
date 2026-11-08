package com.miui.powerkeeper.thermalcollector.collector;

import android.annotation.NonNull;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.cloudcontrol.AutoRegister;
import com.miui.powerkeeper.cloudcontrol.CloudFunctionConfig;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.provider.SimpleSettingKeys;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.thermal.ScenarioManager;
import com.miui.powerkeeper.thermal.resource.ScenarioConfig;
import com.miui.powerkeeper.thermalcollector.CollectorManager;
import com.miui.powerkeeper.utils.FloatSceneObserver;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import miui.process.ForegroundInfo;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class ScenarioCollector extends BaseCollector {
    public static final String KEY_AUDIO = "audio";
    public static final String KEY_CAMERA = "camera";
    public static final String KEY_CASTING = "casting";
    public static final String KEY_DOWNLOAD = "download";
    public static final String KEY_FLOAT_PACKAGE_NAME = "float_pkn";
    public static final String KEY_FLOAT_WINDOW = "float_window";
    public static final String KEY_FOREGROUND_PACKAGE_NAME = "foreground_pkn";
    public static final String KEY_GPS = "gps";
    public static final String KEY_SCREEN = "screen";
    public static final String KEY_SPLIT = "split";
    public static final String KEY_THERMAL_GROUP = "thermal_group";
    private static final int MESSAGE_STATE_CAMERA_CHANGED = 0;
    private static final String TAG = "CollectorScenario";
    private static final String VALUE_DEFAULT = "NULL";
    private static final String VALUE_GROUP_CAMERA = "camera";
    private static final String VALUE_GROUP_CLASS0 = "class0";
    private static final String VALUE_GROUP_GAME = "game";
    private static final String VALUE_GROUP_NAVIGATION = "navigation";
    private static final String VALUE_GROUP_OTHER = "other";
    private static final String VALUE_GROUP_VIDEO = "video";
    private static final String VALUE_GROUP_VIDEO_CHAT = "video_chat";
    private static final String VALUE_OFF = "OFF";
    private static final String VALUE_ON = "ON";
    private final EventsAggregator mAggregator;
    private String mAudio;
    private String mCamera;
    private String mCasting;
    private final AutoRegister mCloudUser;
    private String mDownload;
    private String mFloatPkn;
    private String mFloatWindow;
    private final Map<String, String> mForegroundMap;
    private String mForegroundPkn;
    private String mGps;
    private final ScenarioHandler mHandler;
    private final BroadcastReceiver mReceiver;
    private String mScreen;
    private String mSplit;
    private String mThermalGroup;

    private static class ScenarioHandler extends Handler {
        SoftReference<ScenarioCollector> mCollectorReference;

        public ScenarioHandler(@NonNull Looper looper, ScenarioCollector scenarioCollector) {
            super(looper);
            this.mCollectorReference = new SoftReference<>(scenarioCollector);
        }

        @Override // android.os.Handler
        public void handleMessage(@NonNull Message message) {
            ScenarioCollector scenarioCollector = this.mCollectorReference.get();
            int i2 = message.what;
            if (i2 == -34) {
                scenarioCollector.setSmartScenario(((Long) message.obj).longValue());
                return;
            }
            if (i2 == -31) {
                scenarioCollector.setFloatWindow((FloatSceneObserver.FloatWindowState) message.obj);
                return;
            }
            if (i2 == -9) {
                scenarioCollector.setForegroundPackageName(((ForegroundInfo) message.obj).mForegroundPackageName);
            } else if (i2 == -5) {
                scenarioCollector.setScreen(ScenarioCollector.VALUE_OFF);
            } else {
                if (i2 != -4) {
                    return;
                }
                scenarioCollector.setScreen(ScenarioCollector.VALUE_ON);
            }
        }
    }

    public ScenarioCollector() {
        super("CollectorScenario");
        EventsAggregator eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
        this.mAggregator = eventsAggregator;
        this.mHandler = new ScenarioHandler(CollectorManager.getThread().getLooper(), this);
        this.mForegroundMap = new HashMap();
        this.mForegroundPkn = eventsAggregator.getForegroundInfo().mForegroundPackageName;
        this.mFloatPkn = VALUE_DEFAULT;
        this.mScreen = VALUE_ON;
        this.mThermalGroup = VALUE_DEFAULT;
        this.mFloatWindow = VALUE_DEFAULT;
        this.mCamera = VALUE_DEFAULT;
        this.mCasting = VALUE_DEFAULT;
        this.mDownload = VALUE_DEFAULT;
        this.mGps = VALUE_DEFAULT;
        this.mAudio = VALUE_DEFAULT;
        this.mSplit = VALUE_DEFAULT;
        this.mReceiver = new BroadcastReceiver() { // from class: com.miui.powerkeeper.thermalcollector.collector.ScenarioCollector.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                if (intent == null || intent.getAction() == null || !ScenarioConfig.ACTION_CAMERA_STATE.equals(intent.getAction())) {
                    return;
                }
                ScenarioCollector.this.mHandler.obtainMessage(0, intent).sendToTarget();
            }
        };
        AutoRegister autoRegister = new AutoRegister() { // from class: com.miui.powerkeeper.thermalcollector.collector.ScenarioCollector.2
            @Override // com.miui.powerkeeper.cloudcontrol.AutoRegister
            protected int getModel() {
                return b.b.C;
            }

            @Override // com.miui.powerkeeper.cloudcontrol.IUseCloud
            public void updateCloudData() {
                JSONObject jSONObject;
                String string = SimpleSettings.Misc.getString(AutoRegister.getContext(), "thermal_group", "");
                String string2 = SimpleSettings.Misc.getString(AutoRegister.getContext(), SimpleSettingKeys.KEY_OPTIMIZE_GAME_NAMES, "");
                String functionParameters2 = CloudFunctionConfig.getFunctionParameters2(AutoRegister.getContext(), SimpleSettingKeys.FUNC_THERMAL_VIDEOCHAT_CONFIG_ENABLE, "");
                try {
                    jSONObject = new JSONObject(string);
                } catch (Exception e2) {
                    StringWriter stringWriter = new StringWriter();
                    e2.printStackTrace(new PrintWriter(stringWriter));
                    ScenarioManager.getHistory().log(stringWriter.toString());
                    jSONObject = null;
                }
                if (jSONObject == null) {
                    return;
                }
                ScenarioCollector.this.mForegroundMap.clear();
                String strOptString = jSONObject.optString(ScenarioCollector.VALUE_GROUP_GAME, "");
                if (!strOptString.isEmpty()) {
                    for (String str : strOptString.split(",")) {
                        ScenarioCollector.this.mForegroundMap.put(str, ScenarioCollector.VALUE_GROUP_GAME);
                    }
                }
                String strOptString2 = jSONObject.optString("pubg", "");
                if (!strOptString2.isEmpty()) {
                    for (String str2 : strOptString2.split(",")) {
                        ScenarioCollector.this.mForegroundMap.put(str2, ScenarioCollector.VALUE_GROUP_GAME);
                    }
                }
                String strOptString3 = jSONObject.optString("game2", "");
                if (!strOptString3.isEmpty()) {
                    for (String str3 : strOptString3.split(",")) {
                        ScenarioCollector.this.mForegroundMap.put(str3, ScenarioCollector.VALUE_GROUP_GAME);
                    }
                }
                String strOptString4 = jSONObject.optString("yuanshen", "");
                if (!strOptString4.isEmpty()) {
                    for (String str4 : strOptString4.split(",")) {
                        ScenarioCollector.this.mForegroundMap.put(str4, ScenarioCollector.VALUE_GROUP_GAME);
                    }
                }
                String strOptString5 = jSONObject.optString("camera", "");
                if (!strOptString5.isEmpty()) {
                    for (String str5 : strOptString5.split(",")) {
                        ScenarioCollector.this.mForegroundMap.put(str5, "camera");
                    }
                }
                String strOptString6 = jSONObject.optString(ScenarioCollector.VALUE_GROUP_CLASS0, "");
                if (!strOptString6.isEmpty()) {
                    for (String str6 : strOptString6.split(",")) {
                        ScenarioCollector.this.mForegroundMap.put(str6, ScenarioCollector.VALUE_GROUP_CLASS0);
                    }
                }
                String strOptString7 = jSONObject.optString("navigation", "");
                if (!strOptString7.isEmpty()) {
                    for (String str7 : strOptString7.split(",")) {
                        ScenarioCollector.this.mForegroundMap.put(str7, "navigation");
                    }
                }
                String strOptString8 = jSONObject.optString(ScenarioCollector.VALUE_GROUP_VIDEO, "");
                if (!strOptString8.isEmpty()) {
                    for (String str8 : strOptString8.split(",")) {
                        ScenarioCollector.this.mForegroundMap.put(str8, ScenarioCollector.VALUE_GROUP_VIDEO);
                    }
                }
                for (String str9 : string2.split(",")) {
                    ScenarioCollector.this.mForegroundMap.putIfAbsent(str9, ScenarioCollector.VALUE_GROUP_GAME);
                }
                for (String str10 : functionParameters2.split(",")) {
                    ScenarioCollector.this.mForegroundMap.put(str10, ScenarioCollector.VALUE_GROUP_VIDEO_CHAT);
                }
            }
        };
        this.mCloudUser = autoRegister;
        initDefaultValue();
        autoRegister.updateCloudData();
        registerListener();
    }

    private void initDefaultValue() {
        this.mForegroundPkn = this.mAggregator.getForegroundInfo().mForegroundPackageName;
        this.mFloatPkn = VALUE_DEFAULT;
        this.mScreen = VALUE_ON;
        this.mThermalGroup = "other";
        this.mFloatWindow = VALUE_OFF;
        this.mCamera = VALUE_OFF;
        this.mCasting = VALUE_OFF;
        this.mDownload = VALUE_OFF;
        this.mGps = VALUE_OFF;
        this.mAudio = VALUE_OFF;
        this.mSplit = VALUE_OFF;
    }

    private void registerListener() {
        this.mAggregator.registerForScreenOn(this.mHandler, null);
        this.mAggregator.registerForScreenOff(this.mHandler, null);
        this.mAggregator.registerForForegroundInfo(this.mHandler);
        this.mAggregator.registerForFloatSceneEvent(this.mHandler);
        this.mAggregator.registerSmartPowerScenarioIdChanged(this.mHandler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFloatWindow(FloatSceneObserver.FloatWindowState floatWindowState) {
        this.mFloatWindow = floatWindowState.getIsFloated() ? VALUE_ON : VALUE_OFF;
        this.mFloatPkn = floatWindowState.getIsFloated() ? floatWindowState.getPkgName() : VALUE_DEFAULT;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setForegroundPackageName(String str) {
        this.mForegroundPkn = str;
        this.mThermalGroup = this.mForegroundMap.getOrDefault(str, "other");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setScreen(String str) {
        this.mScreen = str;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSmartScenario(long j2) {
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
        }
    }

    private void unregisterListener() {
        this.mAggregator.unregisterForScreenOn(this.mHandler);
        this.mAggregator.unregisterForScreenOff(this.mHandler);
        this.mAggregator.unregisterForForegroundInfo(this.mHandler);
        this.mAggregator.unregisterForFloatSceneEvent(this.mHandler);
        this.mAggregator.unregisterSmartPowerScenarioIdChanged(this.mHandler);
        this.mContext.unregisterReceiver(this.mReceiver);
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.BaseCollector
    protected void collect() {
        JSONObject json = getJson();
        try {
            json.put("foreground_pkn", this.mForegroundPkn);
            json.put("float_pkn", this.mFloatPkn);
            json.put("screen", this.mScreen);
            json.put("thermal_group", this.mThermalGroup);
            json.put("float_window", this.mFloatWindow);
            json.put("camera", this.mCamera);
            json.put("casting", this.mCasting);
            json.put("download", this.mDownload);
            json.put("gps", this.mGps);
            json.put("audio", this.mAudio);
            json.put("split", this.mSplit);
        } catch (JSONException e2) {
            CollectorManager.getHistory().log(e2.getMessage());
        }
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.BaseCollector
    protected void prepareBeDestroy() {
        unregisterListener();
    }
}
