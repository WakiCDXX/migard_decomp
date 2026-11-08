package com.miui.powerkeeper.feedbackcontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import com.android.internal.os.BackgroundThread;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.powerchecker.PowerCheckerCloudConfigure;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.provider.ThermalInfoHelper;
import com.miui.powerkeeper.thermalcollector.CollectorManager;
import com.miui.powerkeeper.utils.CommonAdapter;
import com.miui.powerkeeper.utils.FileUtil;
import com.miui.powerkeeper.utils.PackageUtil;
import com.miui.powerkeeper.utils.ShellUtils;
import com.miui.powerkeeper.utils.Utils;
import com.miui.whetstone.WhetstoneActivityManager;
import com.xiaomi.mipush.sdk.Constants;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import miui.process.ForegroundInfo;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class ThermalInfoCollector {
    private static final int COLLECT_AMBIENT_TEMP_DELAY_TIME = 22000;
    private static final long COLLECT_THERMALINFO_INTERVAL;
    private static final int CURRENT_DATA_VERSION = 10;
    public static final long DEFAULT_CATCH_LOG_MIN_INTERVAL;
    public static final long DEFAULT_COLLECT_APPS_INFO_MIN_CHANGED_INTERVAL = 1800000;
    public static final long DEFAULT_COLLECT_APPS_INFO_MIN_INTERVAL = 1800000;
    public static final int DEFAULT_COLLECT_TOTAL_INFO_BATTERY_LEVEL;
    public static final long DEFAULT_COLLECT_UNRE_APPS_INFO_MIN_INTERVAL = 600000;
    public static final boolean IS_UN_RELEASED;
    private static final String KEY_AMBIENT_THERMAL_TEMP = "ambient";
    private static final String KEY_APP_END_TIME = "apet";
    private static final String KEY_APP_FPS = "app_fps";
    private static final String KEY_APP_NAME = "pkn";
    private static final String KEY_APP_START_TIME = "apst";
    private static final String KEY_APP_VERSION = "pkn_version";
    private static final String KEY_BATTERY_TEMP = "bt_temp";
    private static final String KEY_BLUETOOTH_STATE = "bluetooth";
    private static final String KEY_BOARD_FILE = "board_file";
    private static final String KEY_B_PLUG_TYPE = "b_plug_type";
    private static final String KEY_B_TIME = "tobt";
    private static final String KEY_COLLECTOR_VERSION = "system_version";
    private static final String KEY_DOWN_SPEED = "net_down_speed";
    private static final String KEY_END_BATTERY_CAPACITY = "capend";
    private static final String KEY_EXCEP = "excep";
    private static final String KEY_E_CURRENT = "e_current";
    private static final String KEY_E_PLUG_TYPE = "e_plug_type";
    private static final String KEY_E_TIME = "toet";
    private static final String KEY_E_VOLTAGE = "e_voltage";
    private static final String KEY_GPS_STATE = "gps";
    private static final String KEY_GPS_STRENGTH = "gps_rssi";
    private static final String KEY_HARDWARE_ID = "hardware_id";
    private static final String KEY_IN_72H = "in72h";
    private static final String KEY_MODEM_SIGNAL = "modem_sig";
    private static final String KEY_MULTI_TEMP = "ntc_items";
    private static final String KEY_NETWORK_TYPE = "netype";
    private static final String KEY_PACKAGE_VERSION = "Power_version";
    private static final String KEY_PLUG_TYPE = "plug_type";
    private static final String KEY_POWER = "Power_cal";
    private static final String KEY_REFRESH_RATE = "refresh";
    private static final String KEY_SCREEN_RESOLUTION = "resolution";
    private static final String KEY_SCRREN_BRIGHTNESS = "brightness";
    private static final String KEY_START_BATTERY_CAPACITY = "capstart";
    private static final String KEY_TOP_CPU = "top_cpu";
    private static final String KEY_TORCH_STATE = "torch";
    private static final String KEY_UP_SPEED = "net_up_speed";
    private static final String KEY_URL = "url";
    private static final String KEY_VERSION = "v";
    private static final String KEY_VOL_INDEX = "volume";
    private static final String KEY_WIFI_SIGNAL = "wifi_sig";
    private static final String KEY_XO_TEMP = "xo_temp";
    private static long LOG_MAX_SIZE = 0;
    private static final int MSG_CLEAN_THERMALINFO = 2;
    private static final int MSG_COLLECT_THERMALINFO = 0;
    private static final int MSG_PLUGED_STATUS_CHANGED = 6;
    private static final int MSG_START_THERMALINFO_COLLECTOR = 3;
    private static final int MSG_STOP_THERMALINFO_COLLECTOR = 4;
    private static final int MSG_UPLOAD_THERMALINFO = 1;
    public static final String PHONE_BLTEMP_TYPE;
    public static final String PHONE_HIGHTEMP_TYPE;
    public static final String PHONE_THERMAL_IEC_STATE_TYPE;
    public static final String PHONE_TORCH_BRIGHTNESS_LEVEL_TYPE;
    private static final int RECORD_APPS_INFO_MAX_COUNT = 3;
    private static final int SCREEN_OFF_INTERVAL;
    private static final String TAG = "ThermalInfoCollector";
    public static final String THERMAL_EXCEPTION_LOG_PATH = "/data/vendor/thermal/log";
    public static final String TOTAL_PHONE_DATA_TYPE = "total";
    public static final String TYPICAL_APPS_DATA_EXCP_TYPE;
    public static final String TYPICAL_APPS_DATA_TYPE = "apps";
    public static final String TYPICAL_UNRE_APPS_DATA_TYPE = "unre_apps";
    private static final int UPDATE_AMBIENT_INTERVAL;
    private static final long UPLOAD_THERMALINFO_INTERVAL;
    private static ThermalInfoCollector sInstance;
    private static boolean sIsTestMode;
    private long chargingstatus;
    private long lastAppChangedElapsedTime;
    private int mAppFps;
    private long mAppsCurrentCapacity;
    private int mBatteryTemperature;
    private Context mContext;
    private int mCurBatteryLevel;
    private long mCurBatteryLevelElapsedTime;
    private long mCurrentAppChangedElapsedTime;
    private long mCurrentAppChangedTime;
    private int mCurrentAppPlugType;
    private int mCurrentTotalPlugType;
    private long mFirstboot;
    private boolean mForgroundInfoChangeRegisterred;
    private int mLastBatteryLevel;
    private long mLastCollectTotalElapsedTime;
    private long mLastCollectTotalTime;
    private int mLastPlugType;
    private long mLastScreenOffTime;
    private long mLastUpdateAmbientTempTime;
    private long mLastUploadElapsedTime;
    private int mPlugType;
    private int mPreBatteryLevel;
    private long mPreBatteryLevelElapsedTime;
    private int mPreBatteryLevelrecord;
    private boolean mScreenChangeRegisterred;
    private boolean mStarted;
    private long mTotalCurrentCapacity;
    private long mTotalDownBytes;
    private long mTotalUpBytes;
    private boolean mWifiChangeRegisterred;
    private int mXoTermalTemperature;
    private long notchargingstatus;
    private final List<ArrayMap<String, Object>> mAppsInfoArray = new ArrayList();
    private final Object mLock = new Object();
    private final Object mThermalMapsLock = new Object();
    private final JSONArray mPlugStatusInfoApps = new JSONArray();
    private final JSONArray mPlugStatusInfoTotal = new JSONArray();
    private String mCatchLogThreshhold = " ";
    private boolean mCatchLogEnable = false;
    private long mCurrentCatchElapsedTime = 0;
    private long mCollectAppsInfoMinChangedInterval = 1800000;
    private long mCollectAppsInfoMinInterval = 1800000;
    private long mCatchLogMinInterval = DEFAULT_CATCH_LOG_MIN_INTERVAL;
    private int mCollectTotalInfoBatteryLevel = DEFAULT_COLLECT_TOTAL_INFO_BATTERY_LEVEL;
    private ArrayMap<String, Integer> mThermalPkgMap = new ArrayMap<>(64);
    private double powercal = 0.0d;
    private boolean isQcomPlatform = false;
    private boolean firstbatterystatsreport = false;
    private boolean firstforegroundreport = false;
    public String packageVersion = null;
    private BroadcastReceiver mScreenStatusReceiver = new BroadcastReceiver() { // from class: com.miui.powerkeeper.feedbackcontrol.ThermalInfoCollector.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ThermalInfoCollector.sIsTestMode) {
                Log.d(ThermalInfoCollector.TAG, "receive action = " + action);
            }
            if ("android.intent.action.SCREEN_OFF".equals(action)) {
                ThermalInfoCollector.this.mLastScreenOffTime = SystemClock.elapsedRealtime();
            } else if ("android.intent.action.SCREEN_ON".equals(action)) {
                long jElapsedRealtime = SystemClock.elapsedRealtime();
                if (ThermalInfoCollector.this.mLastScreenOffTime == 0 || jElapsedRealtime - ThermalInfoCollector.this.mLastScreenOffTime <= ThermalInfoCollector.SCREEN_OFF_INTERVAL || jElapsedRealtime - ThermalInfoCollector.this.mLastUpdateAmbientTempTime < ThermalInfoCollector.UPDATE_AMBIENT_INTERVAL || ThermalInfoCollector.this.mHandler == null) {
                    return;
                }
                ThermalInfoCollector.this.mHandler.postDelayed(new Runnable() { // from class: com.miui.powerkeeper.feedbackcontrol.ThermalInfoCollector.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        int ambientTemp = ThermalManager.ThermalTempInfoHelper.getAmbientTemp();
                        ThermalInfoCollector.this.mLastUpdateAmbientTempTime = SystemClock.elapsedRealtime();
                        if (ThermalInfoCollector.sIsTestMode) {
                            Log.d(ThermalInfoCollector.TAG, "get current ambient temp " + ambientTemp);
                        }
                    }
                }, 22000L);
            }
        }
    };
    private String mCurrentAppName = "null";
    private Handler mHandler = new ThermalCollectorHandler(BackgroundThread.getHandler().getLooper());

    private class ThermalCollectorHandler extends Handler {
        public ThermalCollectorHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) throws Throwable {
            int i2 = message.what;
            if (i2 == -9) {
                ThermalInfoCollector.this.collectForgroundAppsInfo((ForegroundInfo) message.obj);
                return;
            }
            if (i2 == 4) {
                ThermalInfoCollector.this.stop();
                return;
            }
            if (i2 == 6) {
                ThermalInfoCollector.this.collectPlugStatusInfo(message.arg1);
                return;
            }
            if (i2 == -5) {
                ThermalInfoCollector.this.onScreenOff();
                return;
            }
            if (i2 == -4) {
                ThermalInfoCollector.this.onScreenOn();
                return;
            }
            if (i2 == 0) {
                ThermalInfoCollector.this.collectThermalInfo();
            } else if (i2 == 1) {
                ThermalInfoHelper.uploadThermalInfo(ThermalInfoCollector.this.mContext);
            } else {
                if (i2 != 2) {
                    return;
                }
                ThermalInfoHelper.cleanThermalInfo(ThermalInfoCollector.this.mContext);
            }
        }
    }

    static {
        boolean zIsTestMode = ThermalInfoUtils.isTestMode();
        sIsTestMode = zIsTestMode;
        SCREEN_OFF_INTERVAL = zIsTestMode ? PowerCheckerCloudConfigure.DEFAULT_WAKEUP_ALARM_INTERVAL : 1800000;
        COLLECT_THERMALINFO_INTERVAL = zIsTestMode ? 120000L : DEFAULT_COLLECT_UNRE_APPS_INFO_MIN_INTERVAL;
        UPLOAD_THERMALINFO_INTERVAL = zIsTestMode ? 120000L : 86400000L;
        UPDATE_AMBIENT_INTERVAL = zIsTestMode ? Constants.MAX_VALID_TIME_FOR_REGISTRATION_REQUEST : 3600000;
        TYPICAL_APPS_DATA_EXCP_TYPE = ThermalInfoUtils.isUnReleased() ? "apps_unre_excp" : "apps_excp";
        PHONE_HIGHTEMP_TYPE = ThermalInfoUtils.isUnReleased() ? "unre_hightemp" : "hightemp";
        PHONE_BLTEMP_TYPE = ThermalInfoUtils.isUnReleased() ? "unre_bltemp" : "bltemp";
        PHONE_TORCH_BRIGHTNESS_LEVEL_TYPE = ThermalInfoUtils.isUnReleased() ? "unre_torch_brightness_level" : "torch_brightness_level";
        PHONE_THERMAL_IEC_STATE_TYPE = ThermalInfoUtils.isUnReleased() ? "unre_iec_state" : "iec_state";
        IS_UN_RELEASED = ThermalInfoUtils.isUnReleased();
        DEFAULT_CATCH_LOG_MIN_INTERVAL = ThermalInfoUtils.isUnReleased() ? 3600000L : 10800000L;
        DEFAULT_COLLECT_TOTAL_INFO_BATTERY_LEVEL = ThermalInfoUtils.isUnReleased() ? 3 : 10;
        LOG_MAX_SIZE = 10485760L;
    }

    private ThermalInfoCollector(Context context) {
        this.mContext = context;
        initData();
    }

    private double Caculatepower() {
        if (this.mPreBatteryLevelElapsedTime >= this.lastAppChangedElapsedTime && this.mCurrentAppChangedElapsedTime >= this.mCurBatteryLevelElapsedTime && this.isQcomPlatform && TiLv90toLv40(this.mPreBatteryLevel, this.mCurBatteryLevel)) {
            long j2 = this.notchargingstatus;
            if (j2 >= this.chargingstatus && j2 <= this.mPreBatteryLevelElapsedTime) {
                return Double.valueOf(new DecimalFormat("#.00").format((((((double) Math.abs(this.mCurBatteryLevel - this.mPreBatteryLevel)) * ThermalInfoUtils.getLearningCapacity().doubleValue()) / 100.0d) / ((this.mCurBatteryLevelElapsedTime - this.mPreBatteryLevelElapsedTime) / 60000)) * 60.0d)).doubleValue();
            }
        }
        return 0.0d;
    }

    private boolean TiLv90toLv40(int i2, int i3) {
        return i2 <= 90 && i3 >= 40 && i3 <= i2;
    }

    private void catchExcpInfoIfNeeded(final ArrayMap<String, Object> arrayMap, final List<String> list) {
        if (sIsTestMode) {
            Log.i(TAG, "catchExcpInfoIfNeeded");
        }
        long j2 = this.mCurrentCatchElapsedTime;
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        this.mCurrentCatchElapsedTime = jElapsedRealtime;
        if (jElapsedRealtime - j2 <= this.mCatchLogMinInterval) {
            return;
        }
        new Thread(new Runnable() { // from class: com.miui.powerkeeper.feedbackcontrol.ThermalInfoCollector.2
            @Override // java.lang.Runnable
            public void run() {
                ThermalInfoCollector.this.catchLogAndCollectInfo(arrayMap, list);
            }
        }).start();
    }

    private void catchLog(String str, String str2, List<String> list) throws Throwable {
        if (sIsTestMode) {
            Log.i(TAG, "catchLog");
        }
        if (TextUtils.isEmpty(str)) {
            return;
        }
        try {
            File file = new File("/data/vendor/thermal/log");
            if (!file.exists()) {
                file.mkdir();
            }
            if (file.length() >= LOG_MAX_SIZE) {
                return;
            }
            String str3 = "/data/vendor/thermal/log/" + str;
            File file2 = new File(str3);
            if (!file2.exists()) {
                file2.mkdir();
            }
            if (new File("/data/vendor/thermal/thermal.dump").exists() && FileUtil.copyFile("/data/vendor/thermal/thermal.dump", str3)) {
                StringBuilder sb = new StringBuilder();
                sb.append("dataDetail:" + str2 + ShellUtils.COMMAND_LINE_END);
                sb.append("top cpu info:\n");
                for (int i2 = 0; i2 < list.size(); i2++) {
                    sb.append(list.get(i2) + ShellUtils.COMMAND_LINE_END);
                }
                FileUtil.appendFileString(str3, sb.toString());
                FileUtil.zipLog(str3);
                FileUtil.deleteFile(str3);
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void catchLogAndCollectInfo(ArrayMap<String, Object> arrayMap, List<String> list) {
        try {
            insertExcepInfoIntoDatabase(arrayMap.toString());
            synchronized (ThermalInfoHelper.sThermalLogLock) {
                catchLog(arrayMap.get("url").toString(), arrayMap.toString(), list);
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void clearAppsInfo() {
        try {
            if (this.mAppsInfoArray.size() > 0) {
                for (int size = this.mAppsInfoArray.size() - 1; size >= 0; size--) {
                    this.mAppsInfoArray.remove(size);
                }
            }
            if (sIsTestMode) {
                Log.i(TAG, "mAppsInfoArray length :" + this.mAppsInfoArray.size());
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void clearPlugStatusAppsArray() {
        try {
            synchronized (this.mPlugStatusInfoApps) {
                try {
                    if (this.mPlugStatusInfoApps.length() > 0) {
                        for (int length = this.mPlugStatusInfoApps.length() - 1; length >= 0; length--) {
                            this.mPlugStatusInfoApps.remove(length);
                        }
                        if (sIsTestMode) {
                            Log.i(TAG, "mPlugStatusInfoApps length:" + this.mPlugStatusInfoApps.length());
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void clearPlugStatusTotalArray() {
        try {
            synchronized (this.mPlugStatusInfoTotal) {
                try {
                    if (this.mPlugStatusInfoTotal.length() > 0) {
                        for (int length = this.mPlugStatusInfoTotal.length() - 1; length >= 0; length--) {
                            this.mPlugStatusInfoTotal.remove(length);
                        }
                        if (sIsTestMode) {
                            Log.i(TAG, "mPlugStatusInfoTotal length:" + this.mPlugStatusInfoTotal.length());
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void collectForgroundAppsInfo(ForegroundInfo foregroundInfo) throws Throwable {
        String str;
        if (foregroundInfo == null || !ThermalManager.ThermalCollectorEnabled()) {
            return;
        }
        String str2 = foregroundInfo.mForegroundPackageName;
        if (str2 == null || (str = foregroundInfo.mLastForegroundPackageName) == null) {
            this.mCurrentAppChangedElapsedTime = SystemClock.elapsedRealtime();
        } else {
            this.mCurrentAppName = str2;
            collectForgroundAppsInfo(str);
        }
    }

    public static synchronized ThermalInfoCollector get(Context context) {
        try {
            if (sInstance == null) {
                sInstance = new ThermalInfoCollector(context);
            }
        } catch (Throwable th) {
            throw th;
        }
        return sInstance;
    }

    private int getPlugType() {
        int i2 = this.mLastPlugType;
        if (i2 == -1) {
            return this.mPlugType;
        }
        this.mLastPlugType = -1;
        return i2;
    }

    private void initData() {
        if (SimpleSettings.Misc.getLong(this.mContext, "firstBoot", 0L) == 0) {
            SimpleSettings.Misc.putLong(this.mContext, "firstBoot", System.currentTimeMillis());
        }
    }

    private void insertExcepInfoIntoDatabase(String str) {
        if (sIsTestMode) {
            Log.i(TAG, "insertExcepInfoIntoDatabase");
        }
        ThermalInfoHelper.insertThermalInfo(this.mContext, str, TYPICAL_APPS_DATA_EXCP_TYPE);
    }

    private void insertIntoDatabaseIfNeeded(boolean z2, boolean z3) {
        for (int i2 = 0; i2 < this.mAppsInfoArray.size(); i2++) {
            try {
                ArrayMap<String, Object> arrayMap = this.mAppsInfoArray.get(i2);
                arrayMap.put(KEY_VERSION, 10);
                arrayMap.put(KEY_HARDWARE_ID, ThermalInfoUtils.getHardwareId());
                if (z2) {
                    ThermalInfoHelper.insertThermalInfo(this.mContext, arrayMap.toString(), TYPICAL_UNRE_APPS_DATA_TYPE);
                }
                if (z3) {
                    ThermalInfoHelper.insertThermalInfo(this.mContext, arrayMap.toString(), "apps");
                }
                if (sIsTestMode) {
                    Log.i(TAG, "appsInfoObj info inserted into the database:" + arrayMap.toString());
                }
                clearAppsInfo();
                clearPlugStatusAppsArray();
            } catch (Exception e2) {
                e2.printStackTrace();
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onScreenOff() throws Throwable {
        collectForgroundAppsInfo(this.mCurrentAppName);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onScreenOn() throws Throwable {
        collectForgroundAppsInfo("SCREEN_OFF");
    }

    private void registerForgroundInfoChanged() {
        if (this.mForgroundInfoChangeRegisterred) {
            return;
        }
        this.mForgroundInfoChangeRegisterred = true;
        if (PowerKeeperManager.getInstance() == null || PowerKeeperManager.getInstance().getEventsAggregator() == null || this.mHandler == null) {
            return;
        }
        PowerKeeperManager.getInstance().getEventsAggregator().registerForForegroundInfo(this.mHandler);
    }

    private void registerScreenChanged() {
        if (this.mScreenChangeRegisterred) {
            return;
        }
        this.mScreenChangeRegisterred = true;
        IntentFilter intentFilter = new IntentFilter("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        this.mContext.getApplicationContext().registerReceiver(this.mScreenStatusReceiver, intentFilter, 2);
        PowerKeeperManager.getInstance().getEventsAggregator().registerForScreenOff(this.mHandler, null);
        PowerKeeperManager.getInstance().getEventsAggregator().registerForScreenOn(this.mHandler, null);
    }

    private void unRegisterScreenChanged() {
        if (this.mScreenChangeRegisterred) {
            this.mScreenChangeRegisterred = false;
            this.mContext.getApplicationContext().unregisterReceiver(this.mScreenStatusReceiver);
            PowerKeeperManager.getInstance().getEventsAggregator().unregisterForScreenOff(this.mHandler);
            PowerKeeperManager.getInstance().getEventsAggregator().unregisterForScreenOn(this.mHandler);
        }
    }

    private void unregisterForgroundInfoChanged() {
        if (this.mForgroundInfoChangeRegisterred) {
            this.mForgroundInfoChangeRegisterred = false;
            if (PowerKeeperManager.getInstance() == null || PowerKeeperManager.getInstance().getEventsAggregator() == null || this.mHandler == null) {
                return;
            }
            PowerKeeperManager.getInstance().getEventsAggregator().unregisterForForegroundInfo(this.mHandler);
        }
    }

    public boolean catchLogEnable() {
        return this.mCatchLogEnable;
    }

    public void checkNeedCollectThermalInfo(int i2, int i3, int i4) {
        this.mXoTermalTemperature = i2;
        this.mBatteryTemperature = i3;
        if (this.mHandler == null || !ThermalManager.ThermalCollectorEnabled()) {
            return;
        }
        if (sIsTestMode) {
            Log.d(TAG, "xoTermalTemperature :" + i2 + " batteryLevel:" + i4);
        }
        if (!this.firstbatterystatsreport) {
            this.mPreBatteryLevelrecord = i4;
            this.mCurBatteryLevel = i4;
            this.mPreBatteryLevel = i4;
            long jElapsedRealtime = SystemClock.elapsedRealtime();
            this.mCurBatteryLevelElapsedTime = jElapsedRealtime;
            this.mPreBatteryLevelElapsedTime = jElapsedRealtime;
            this.firstbatterystatsreport = true;
        }
        if (Math.abs(i4 - this.mPreBatteryLevelrecord) >= 2) {
            this.mPreBatteryLevelrecord = i4;
            this.mPreBatteryLevel = this.mCurBatteryLevel;
            this.mCurBatteryLevel = i4;
            this.mPreBatteryLevelElapsedTime = this.mCurBatteryLevelElapsedTime;
            this.mCurBatteryLevelElapsedTime = SystemClock.elapsedRealtime();
        }
        if (i2 < ThermalManager.getXoTempThreshold()) {
            return;
        }
        long jElapsedRealtime2 = SystemClock.elapsedRealtime();
        if (jElapsedRealtime2 - this.mLastCollectTotalElapsedTime >= COLLECT_THERMALINFO_INTERVAL && Math.abs(i4 - this.mLastBatteryLevel) >= this.mCollectTotalInfoBatteryLevel) {
            this.mHandler.obtainMessage(0, i3, i2).sendToTarget();
            this.mLastBatteryLevel = i4;
            this.mLastCollectTotalElapsedTime = jElapsedRealtime2;
        }
    }

    public void cleanThermalInfo() {
        if (this.mHandler == null || !ThermalManager.ThermalCollectorEnabled()) {
            return;
        }
        this.mHandler.sendEmptyMessage(2);
    }

    public void collectPlugStatusInfo(int i2) {
        try {
            Log.i(TAG, "collectPlugStatusInfo plugStatus :" + i2);
            JSONObject jSONObject = new JSONObject();
            jSONObject.put(KEY_XO_TEMP, this.mXoTermalTemperature);
            jSONObject.put(KEY_AMBIENT_THERMAL_TEMP, ThermalManager.ThermalTempInfoHelper.getAmbientTemp());
            jSONObject.put(KEY_BATTERY_TEMP, this.mBatteryTemperature);
            synchronized (this.mPlugStatusInfoApps) {
                try {
                    this.mPlugStatusInfoApps.put(jSONObject);
                    if (sIsTestMode) {
                        Log.i(TAG, "mPlugStatusInfoApps info:" + this.mPlugStatusInfoApps.toString());
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put(KEY_XO_TEMP, this.mXoTermalTemperature);
            jSONObject2.put(KEY_AMBIENT_THERMAL_TEMP, ThermalManager.ThermalTempInfoHelper.getAmbientTemp());
            jSONObject2.put(KEY_BATTERY_TEMP, this.mBatteryTemperature);
            synchronized (this.mPlugStatusInfoTotal) {
                try {
                    this.mPlugStatusInfoTotal.put(jSONObject2);
                    if (sIsTestMode) {
                        Log.i(TAG, "mPlugStatusInfoTotal info:" + this.mPlugStatusInfoTotal.toString());
                    }
                } catch (Throwable th2) {
                    throw th2;
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public void collectThermalInfo() {
        if (this.mHandler == null || !ThermalManager.ThermalCollectorEnabled()) {
            return;
        }
        if (sIsTestMode) {
            Log.d(TAG, "collectThermalInfo");
        }
        ArrayMap arrayMap = new ArrayMap();
        try {
            long j2 = this.mTotalCurrentCapacity;
            this.mTotalCurrentCapacity = ThermalInfoUtils.getCurrentCapacity();
            long jCurrentTimeMillis = System.currentTimeMillis();
            int i2 = this.mCurrentTotalPlugType;
            this.mCurrentTotalPlugType = this.mPlugType;
            arrayMap.put(KEY_B_TIME, Long.valueOf(this.mLastCollectTotalTime));
            arrayMap.put(KEY_E_TIME, Long.valueOf(jCurrentTimeMillis));
            arrayMap.put(KEY_BATTERY_TEMP, Integer.valueOf(this.mBatteryTemperature));
            arrayMap.put(KEY_XO_TEMP, Integer.valueOf(this.mXoTermalTemperature));
            arrayMap.put(KEY_B_PLUG_TYPE, Integer.valueOf(i2));
            arrayMap.put(KEY_E_PLUG_TYPE, Integer.valueOf(this.mCurrentTotalPlugType));
            arrayMap.put(KEY_E_CURRENT, Integer.valueOf(ThermalInfoUtils.getBatteryCurrentNow(this.mContext)));
            arrayMap.put(KEY_E_VOLTAGE, Integer.valueOf(Utils.getBatteryVoltage(this.mContext)));
            arrayMap.put(KEY_AMBIENT_THERMAL_TEMP, Integer.valueOf(ThermalManager.ThermalTempInfoHelper.getAmbientTemp()));
            arrayMap.put(KEY_START_BATTERY_CAPACITY, Long.valueOf(j2));
            arrayMap.put(KEY_END_BATTERY_CAPACITY, Long.valueOf(this.mTotalCurrentCapacity));
            arrayMap.put(KEY_HARDWARE_ID, ThermalInfoUtils.getHardwareId());
            arrayMap.put(KEY_VERSION, 10);
            arrayMap.put(KEY_COLLECTOR_VERSION, Float.valueOf(1.0f));
            arrayMap.put(KEY_IN_72H, Boolean.valueOf(jCurrentTimeMillis < this.mFirstboot));
            arrayMap.put(KEY_GPS_STRENGTH, Integer.valueOf(WhetstoneActivityManager.getGnssStatus()));
            ThermalInfoHelper.insertThermalInfo(this.mContext, arrayMap.toString(), "total");
            if (sIsTestMode) {
                Log.d(TAG, "collect info " + arrayMap.toString());
            }
            clearPlugStatusTotalArray();
            this.mLastCollectTotalTime = System.currentTimeMillis();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public void onPlugedStatusChanged(int i2) {
        if (sIsTestMode) {
            Log.i(TAG, "onPlugedStatusChanged plugStatus info:" + i2);
        }
        if (this.mHandler == null || !ThermalManager.ThermalCollectorEnabled()) {
            return;
        }
        this.mPlugType = i2;
        if (this.mLastPlugType == -1) {
            this.mLastPlugType = i2;
        }
        if (i2 == 0) {
            this.notchargingstatus = SystemClock.elapsedRealtime();
        } else {
            this.chargingstatus = SystemClock.elapsedRealtime();
        }
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(6, i2, 0));
    }

    public void onThermalConfigChanged(boolean z2, String str, long j2, long j3, long j4, int i2) {
        this.mCatchLogEnable = z2;
        this.mCatchLogThreshhold = str;
        this.mCatchLogMinInterval = j2;
        this.mCollectAppsInfoMinChangedInterval = j3;
        this.mCollectAppsInfoMinInterval = j4;
        this.mCollectTotalInfoBatteryLevel = i2;
        if (TextUtils.isEmpty(str)) {
            return;
        }
        try {
            for (String str2 : this.mCatchLogThreshhold.split(",")) {
                String[] strArrSplit = str2.split(":");
                String str3 = strArrSplit[0];
                Integer numValueOf = Integer.valueOf(Integer.parseInt(strArrSplit[1]));
                synchronized (this.mThermalMapsLock) {
                    try {
                        this.mThermalPkgMap.put(str3, numValueOf);
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public void setCurrentFPS(int i2) {
        this.mAppFps = i2;
    }

    public void start() {
        Log.d(TAG, "start");
        synchronized (this.mLock) {
            try {
                if (!this.mStarted) {
                    this.mStarted = true;
                    registerScreenChanged();
                    registerForgroundInfoChanged();
                    long jElapsedRealtime = SystemClock.elapsedRealtime();
                    this.mLastUploadElapsedTime = jElapsedRealtime;
                    this.mLastCollectTotalElapsedTime = jElapsedRealtime;
                    long jElapsedRealtime2 = SystemClock.elapsedRealtime();
                    this.mCurrentAppChangedElapsedTime = jElapsedRealtime2;
                    this.mCurrentCatchElapsedTime = jElapsedRealtime2;
                    long currentCapacity = ThermalInfoUtils.getCurrentCapacity();
                    this.mAppsCurrentCapacity = currentCapacity;
                    this.mTotalCurrentCapacity = currentCapacity;
                    long jCurrentTimeMillis = System.currentTimeMillis();
                    this.mLastCollectTotalTime = jCurrentTimeMillis;
                    this.mCurrentAppChangedTime = jCurrentTimeMillis;
                    int plugType = ThermalInfoUtils.getPlugType(this.mContext);
                    this.mPlugType = plugType;
                    this.mCurrentTotalPlugType = plugType;
                    this.mCurrentAppPlugType = plugType;
                    this.mLastPlugType = -1;
                    if (plugType == 0) {
                        this.notchargingstatus = SystemClock.elapsedRealtime();
                    } else {
                        this.chargingstatus = SystemClock.elapsedRealtime();
                    }
                    this.mAppFps = ThermalInfoUtils.getRefreshRate(this.mContext);
                    this.mTotalUpBytes = TrafficStats.getTotalTxBytes();
                    this.mTotalDownBytes = TrafficStats.getTotalRxBytes();
                    if (Build.HARDWARE.matches("qcom")) {
                        this.isQcomPlatform = true;
                    }
                    this.mFirstboot = SimpleSettings.Misc.getLong(this.mContext, "firstBoot", 0L) + 259200000;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void startCollectInfo() {
        Handler handler = this.mHandler;
        if (handler != null) {
            if (handler.hasMessages(3)) {
                this.mHandler.removeMessages(3);
            }
            Handler handler2 = this.mHandler;
            handler2.sendMessage(handler2.obtainMessage(3));
        }
    }

    public void stop() {
        Log.d(TAG, "stop");
        synchronized (this.mLock) {
            try {
                if (this.mStarted) {
                    this.mStarted = false;
                    unRegisterScreenChanged();
                    unregisterForgroundInfoChanged();
                    this.mLastUploadElapsedTime = 0L;
                    this.mLastCollectTotalElapsedTime = 0L;
                    this.mCurrentAppChangedElapsedTime = 0L;
                    this.mAppsCurrentCapacity = 0L;
                    this.mTotalCurrentCapacity = 0L;
                    this.mLastCollectTotalTime = 0L;
                    this.mCurrentAppChangedTime = 0L;
                    this.mPlugType = -1;
                    this.mCurrentTotalPlugType = -1;
                    this.mCurrentAppPlugType = -1;
                    this.mAppFps = 0;
                    this.mTotalDownBytes = 0L;
                    this.mTotalUpBytes = 0L;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void stopCollectInfo() {
        Handler handler = this.mHandler;
        if (handler != null) {
            if (handler.hasMessages(4)) {
                this.mHandler.removeMessages(4);
            }
            Handler handler2 = this.mHandler;
            handler2.sendMessage(handler2.obtainMessage(4));
        }
    }

    public void uploadThermalInfo() {
        if (this.mHandler == null || !ThermalManager.ThermalCollectorEnabled()) {
            return;
        }
        this.mHandler.sendEmptyMessageDelayed(1, 10000L);
    }

    private void collectForgroundAppsInfo(String str) throws Throwable {
        Log.i(TAG, "collectForgroundAppsInfo: " + str);
        try {
            CpuruntimeResource.getInstance(this.mContext).getcpuinfo(this.firstforegroundreport);
            if (!this.firstforegroundreport) {
                this.mCurrentAppChangedElapsedTime = SystemClock.elapsedRealtime();
                this.firstforegroundreport = true;
            }
            this.lastAppChangedElapsedTime = this.mCurrentAppChangedElapsedTime;
            long jElapsedRealtime = SystemClock.elapsedRealtime();
            this.mCurrentAppChangedElapsedTime = jElapsedRealtime;
            long j2 = jElapsedRealtime - this.lastAppChangedElapsedTime;
            long j3 = this.mCurrentAppChangedTime;
            long j4 = this.mAppsCurrentCapacity;
            this.mAppsCurrentCapacity = ThermalInfoUtils.getCurrentCapacity();
            this.mCurrentAppChangedTime = System.currentTimeMillis();
            int i2 = this.mCurrentAppPlugType;
            this.mCurrentAppPlugType = this.mPlugType;
            long j5 = this.mTotalUpBytes;
            long j6 = this.mTotalDownBytes;
            this.mTotalUpBytes = TrafficStats.getTotalTxBytes();
            this.mTotalDownBytes = TrafficStats.getTotalRxBytes();
            this.powercal = Caculatepower();
            boolean z2 = j2 >= DEFAULT_COLLECT_UNRE_APPS_INFO_MIN_INTERVAL && IS_UN_RELEASED;
            boolean z3 = j2 >= this.mCollectAppsInfoMinChangedInterval;
            if (z2 || z3) {
                this.packageVersion = PackageUtil.getAppVersion(this.mContext.getPackageManager(), str);
                ArrayMap<String, Object> arrayMap = new ArrayMap<>();
                arrayMap.put(KEY_APP_NAME, str);
                arrayMap.put(KEY_APP_START_TIME, Long.valueOf(j3));
                boolean z4 = z2;
                boolean z5 = z3;
                arrayMap.put(KEY_APP_END_TIME, Long.valueOf(this.mCurrentAppChangedTime));
                arrayMap.put(KEY_START_BATTERY_CAPACITY, Long.valueOf(j4));
                arrayMap.put(KEY_END_BATTERY_CAPACITY, Long.valueOf(this.mAppsCurrentCapacity));
                arrayMap.put(KEY_XO_TEMP, Integer.valueOf(this.mXoTermalTemperature));
                arrayMap.put(KEY_BOARD_FILE, ThermalManager.ThermalTempInfoHelper.getBoardFile());
                arrayMap.put(KEY_AMBIENT_THERMAL_TEMP, Integer.valueOf(ThermalManager.ThermalTempInfoHelper.getAmbientTemp()));
                arrayMap.put(KEY_BATTERY_TEMP, Integer.valueOf(this.mBatteryTemperature));
                arrayMap.put(KEY_B_PLUG_TYPE, Integer.valueOf(i2));
                arrayMap.put(KEY_E_PLUG_TYPE, Integer.valueOf(this.mCurrentAppPlugType));
                arrayMap.put(KEY_E_CURRENT, Integer.valueOf(ThermalInfoUtils.getBatteryCurrentNow(this.mContext)));
                arrayMap.put(KEY_NETWORK_TYPE, Integer.valueOf(ThermalInfoUtils.getNetType(this.mContext)));
                arrayMap.put(KEY_E_VOLTAGE, Integer.valueOf(Utils.getBatteryVoltage(this.mContext)));
                arrayMap.put(KEY_MULTI_TEMP, ThermalInfoUtils.getMultiThermalTemp());
                arrayMap.put(KEY_MODEM_SIGNAL, Integer.valueOf(ThermalInfoUtils.getModemSignal(this.mContext)));
                arrayMap.put(KEY_WIFI_SIGNAL, Integer.valueOf(ThermalInfoUtils.getWifiSignal(this.mContext)));
                arrayMap.put(KEY_POWER, Double.valueOf(this.powercal));
                arrayMap.put(KEY_PACKAGE_VERSION, this.packageVersion);
                arrayMap.put(KEY_COLLECTOR_VERSION, Float.valueOf(1.0f));
                String str2 = Build.DEVICE;
                if (str2.equalsIgnoreCase("star") || str2.equalsIgnoreCase("venus") || str2.equalsIgnoreCase("cetus")) {
                    arrayMap.put(KEY_SCREEN_RESOLUTION, ThermalInfoUtils.getResolution(this.mContext));
                }
                if (CollectorManager.getDataCollectorVersion().equals("2.0")) {
                    arrayMap.put(KEY_TOP_CPU, Utils.getTopNProcessInfoFromCpuInfo(10));
                } else {
                    JSONObject processList = ThermalInfoUtils.getProcessList();
                    if (processList != null) {
                        arrayMap.put(KEY_TOP_CPU, processList.get("total"));
                    }
                }
                arrayMap.put(KEY_APP_VERSION, ThermalInfoUtils.getAppVersionName(str, this.mContext));
                arrayMap.put(KEY_SCRREN_BRIGHTNESS, Integer.valueOf(ThermalInfoUtils.getScreenBrightness(this.mContext)));
                arrayMap.put(KEY_REFRESH_RATE, Integer.valueOf(ThermalInfoUtils.getRefreshRate(this.mContext)));
                arrayMap.put("net_up_speed", Integer.valueOf(ThermalInfoUtils.calculateNetSpeed(j2, this.mTotalUpBytes - j5)));
                arrayMap.put("net_down_speed", Integer.valueOf(ThermalInfoUtils.calculateNetSpeed(j2, this.mTotalDownBytes - j6)));
                arrayMap.put(KEY_VOL_INDEX, Integer.valueOf(ThermalInfoUtils.getVolumeForApp(this.mContext)));
                arrayMap.put("gps", Boolean.valueOf(CommonAdapter.isGpsEnabled(this.mContext)));
                arrayMap.put(KEY_GPS_STRENGTH, Integer.valueOf(WhetstoneActivityManager.getGnssStatus()));
                arrayMap.put(KEY_TORCH_STATE, Boolean.valueOf(ThermalInfoUtils.isTorchState(this.mContext)));
                arrayMap.put(KEY_BLUETOOTH_STATE, Boolean.valueOf(Utils.isBlueToothConnectedOrConnecting(this.mContext)));
                int i3 = this.mXoTermalTemperature * 1000;
                ArrayMap<String, Integer> arrayMap2 = this.mThermalPkgMap;
                arrayMap.put(KEY_EXCEP, Boolean.valueOf(i3 >= arrayMap2.getOrDefault(str, arrayMap2.getOrDefault("default", 48000)).intValue()));
                arrayMap.put("plug_type", Integer.valueOf(getPlugType()));
                arrayMap.put(KEY_IN_72H, Boolean.valueOf(this.mCurrentAppChangedElapsedTime < this.mFirstboot));
                this.mAppsInfoArray.add(arrayMap);
                if (sIsTestMode) {
                    Log.i(TAG, "mAppsInfoArray info :" + this.mAppsInfoArray.toString());
                }
                insertIntoDatabaseIfNeeded(z4, z5);
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }
}
