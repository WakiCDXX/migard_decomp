package com.miui.powerkeeper.thermal.dfscheck;

import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.miui.misight.MiEvent;
import com.miui.misight.MiSight;
import com.miui.powerkeeper.PowerKeeperApplication;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.batterylife.systeminfo.BSHelper;
import com.miui.powerkeeper.feedbackcontrol.ThermalInfoCollector;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.powerchecker.NightStandbyRecord;
import com.miui.powerkeeper.powerchecker.PowerCheckerCloudConfigure;
import com.miui.powerkeeper.thermal.dfscheck.scenejudge.EvaluationJudge;
import com.miui.powerkeeper.thermal.dfsreport.ReportEventNum;
import com.miui.powerkeeper.thermal.dfsreport.ThermalDfsInterface;
import com.miui.powerkeeper.thermal.dfsreport.ThermalDfsThreadPool;
import com.miui.powerkeeper.thermal.dfsreport.ThermalStateObserver;
import com.miui.powerkeeper.utils.FileUtil;
import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import miui.process.ForegroundInfo;
import miui.provider.Weather;

/* JADX INFO: loaded from: classes.dex */
public class CollectionData {
    public static final int BOARD_BATTERY_TEMP_GAP = 5000;
    private static final String TAG = "powerkeeper.dfscollect";
    private int mAmbientTemp;
    private int mBattery;
    private int mBrightness;
    private double mCacuCapacity;
    private double mCapacity;
    private CheckHightTemp mCheckHightTemp;
    private CheckUtils mCheckUtils;
    private int mCurrent;
    private List<String> mCurrentList;
    private Map<String, String> mDevicemap;
    private EvaluationJudge mEvaluationJudge;
    private GetAverageTemp mGetAverageTemp;
    private int mGpu;
    private String mGpuFreqPath;
    private int mLastSceneTemp;
    private long mLastTimeStamp;
    private int mModem;
    private PowerManager mPowerManager;
    private Map<Integer, String> mScenarioMap;
    private long mSceneStartTimeStamp;
    private int mSceneTemp;
    private ScheduledExecutorService mScheduledExecutorService;
    public SimpleDateFormat mSimpleDateFormat;
    private int mTempState;
    private ThermalDfsThreadPool mThermalDfsThreadPool;
    private int mWifi;
    private static final boolean DEBUG = Build.IS_USERDEBUG;
    private static CollectionData mCollectionDataInstance = null;
    private final String TEMPSTATEPATH = "/sys/class/thermal/thermal_message/temp_state";
    private final String CPUFREQHEADPATH = "/sys/devices/system/cpu/cpufreq";
    private final String CURRENT_NOW_PATH = "/sys/class/power_supply/battery/current_now";
    private final int CURRENT_LIST_SIZE = 3;
    private CollectionDataFunction mCollectionDataFunctionInstance = null;
    private PowerKeeperInterface.d mBatteryChangedCallback = null;
    private int mLastBatteryLevel = 0;
    private boolean mScreenOn = false;
    private boolean isOdmXml = false;
    private String mCpu = "";
    private String mSconfig = "-1";
    private String mPackageName = "";
    private String mScenarioName = "";
    private String mPreScenarioName = "";
    private String mSceneNamePrefix = "";
    private final ArrayList<Integer> mUnfoldList = new ArrayList<>(Arrays.asList(1, 3, 6, 8));
    private PowerKeeperInterface.m mBatteryStatusReceiver = new PowerKeeperInterface.m() { // from class: com.miui.powerkeeper.thermal.dfscheck.CollectionData.1
        Date date;

        @Override // com.miui.powerkeeper.PowerKeeperInterface.m
        public void onBatteryChanged(int i2, int i3, int i4, int i5, int i6, int i7) {
            CollectionData.this.setBattery(i5);
            if (i4 != CollectionData.this.mLastBatteryLevel) {
                if (CollectionData.this.mLastBatteryLevel == 0) {
                    CollectionData.this.mLastBatteryLevel = i4;
                    return;
                }
                long jCurrentTimeMillis = System.currentTimeMillis();
                double d2 = CollectionData.this.mLastTimeStamp > 0 ? ((CollectionData.this.mCacuCapacity * ((double) (CollectionData.this.mLastBatteryLevel - i4))) / (jCurrentTimeMillis - CollectionData.this.mLastTimeStamp)) * 1000.0d * 3600.0d : 0.0d;
                CollectionData.this.mLastTimeStamp = jCurrentTimeMillis;
                CollectionData.this.mLastBatteryLevel = i4;
                this.date = new Date(jCurrentTimeMillis);
                CollectionData collectionData = CollectionData.this;
                if (collectionData.mSimpleDateFormat == null) {
                    collectionData.putCurrentList(d2 + "");
                    if (CollectionData.DEBUG) {
                        Log.d(CollectionData.TAG, "Average current during last battery level: " + d2);
                        return;
                    }
                    return;
                }
                if (CollectionData.DEBUG) {
                    Log.d(CollectionData.TAG, "Average current during last battery level: " + d2 + " time " + CollectionData.this.mSimpleDateFormat.format(this.date));
                }
                CollectionData.this.putCurrentList(CollectionData.this.mSimpleDateFormat.format(this.date) + " " + d2);
            }
        }
    };
    private Map<Long, Integer> mTemperatureList = new HashMap();
    private Map<String, Integer> mNtcDataMap = new HashMap();
    private Map<String, Integer> mHotPlugDataMap = new HashMap();
    private Map<String, String> mDumpMap = new HashMap();

    public class CollectionDataFunction implements ThermalDfsInterface.dfsCheck {
        int retryCount;
        Runnable runnableReport;

        @Override // com.miui.powerkeeper.thermal.dfsreport.ThermalDfsInterface.dfsCheck
        public boolean checkTarget() {
            if (CollectionData.DEBUG) {
                Log.d(CollectionData.TAG, "collection");
            }
            this.retryCount = -1;
            CollectionData.this.setTempState();
            CollectionData.this.setCpu();
            CollectionData.this.mCheckHightTemp.finshCallback(CollectionData.TAG);
            CollectionData.this.mScheduledExecutorService.schedule(this.runnableReport, 0L, TimeUnit.SECONDS);
            return true;
        }

        private CollectionDataFunction() {
            this.retryCount = -1;
            this.runnableReport = new Runnable() { // from class: com.miui.powerkeeper.thermal.dfscheck.CollectionData.CollectionDataFunction.1
                @Override // java.lang.Runnable
                public void run() {
                    CollectionDataFunction.this.retryCount++;
                    Log.d(CollectionData.TAG, "callback count is " + CollectionData.this.mCheckHightTemp.getCallbackCount() + " retry count is " + CollectionDataFunction.this.retryCount);
                    CollectionDataFunction collectionDataFunction = CollectionDataFunction.this;
                    if (collectionDataFunction.retryCount <= 3) {
                        if (CollectionData.this.mCheckHightTemp.getCallbackCount() > 0) {
                            if (CollectionData.this.mCheckHightTemp.getCallbackCount() > 0) {
                                Log.d(CollectionData.TAG, " wait 1s");
                                CollectionData.this.mScheduledExecutorService.schedule(CollectionDataFunction.this.runnableReport, 1L, TimeUnit.SECONDS);
                                return;
                            }
                            return;
                        }
                        if (!CollectionData.this.mCheckHightTemp.getAllowReportHighTemp().booleanValue()) {
                            Log.d(CollectionData.TAG, "allow report hight is false not report");
                        } else {
                            if (CollectionData.this.mEvaluationJudge.isInNolimitInflunce()) {
                                return;
                            }
                            CollectionData.this.highTempDataColltSendEvent();
                            Log.d(CollectionData.TAG, CollectionData.this.printToString());
                        }
                    }
                }
            };
        }
    }

    private class GetAverageTemp {
        private Date date;
        private Runnable runnable;
        private boolean startAverageTemp;
        private String time;

        public float caculAverageTemp() {
            long jCurrentTimeMillis = System.currentTimeMillis();
            Date date = new Date(jCurrentTimeMillis);
            this.date = date;
            this.time = CollectionData.this.mSimpleDateFormat.format(date);
            long j2 = jCurrentTimeMillis - ThermalInfoCollector.DEFAULT_COLLECT_UNRE_APPS_INFO_MIN_INTERVAL;
            int i2 = 0;
            float fIntValue = 0.0f;
            for (Map.Entry entry : CollectionData.this.mTemperatureList.entrySet()) {
                long jLongValue = ((Long) entry.getKey()).longValue();
                if (jLongValue >= j2 && jLongValue < jCurrentTimeMillis) {
                    fIntValue += ((Integer) entry.getValue()).intValue();
                    i2++;
                }
            }
            float f2 = i2 > 0 ? fIntValue / i2 : 0.0f;
            CollectionData.this.clearTemperatureList();
            CollectionData.this.mScheduledExecutorService.schedule(this.runnable, ThermalInfoCollector.DEFAULT_COLLECT_UNRE_APPS_INFO_MIN_INTERVAL, TimeUnit.MILLISECONDS);
            if (CollectionData.this.mSimpleDateFormat != null && CollectionData.DEBUG) {
                Log.d(CollectionData.TAG, this.time + " cacult get temp is " + f2 + " after clear list size is " + CollectionData.this.mTemperatureList.size());
            }
            CollectionData.this.boardTempReportSendEvent(this.time, (int) f2);
            return f2;
        }

        public synchronized void startCaculAverageTemp() {
            if (this.startAverageTemp) {
                return;
            }
            CollectionData.this.mScheduledExecutorService.schedule(this.runnable, ThermalInfoCollector.DEFAULT_COLLECT_UNRE_APPS_INFO_MIN_INTERVAL, TimeUnit.MILLISECONDS);
            this.startAverageTemp = true;
        }

        public synchronized void stopCaculAverageTemp() {
            if (!this.startAverageTemp) {
                Log.i(CollectionData.TAG, "cacul average temp already stop");
            } else {
                this.startAverageTemp = false;
                CollectionData.this.clearTemperatureList();
            }
        }

        private GetAverageTemp() {
            this.startAverageTemp = false;
            this.time = "";
            this.runnable = new Runnable() { // from class: com.miui.powerkeeper.thermal.dfscheck.CollectionData.GetAverageTemp.1
                @Override // java.lang.Runnable
                public void run() {
                    GetAverageTemp.this.caculAverageTemp();
                }
            };
        }
    }

    private CollectionData() {
        this.mThermalDfsThreadPool = null;
        this.mScheduledExecutorService = null;
        this.mPowerManager = null;
        this.mCheckUtils = null;
        this.mCheckHightTemp = null;
        this.mEvaluationJudge = null;
        this.mGpuFreqPath = "";
        this.mCurrentList = null;
        this.mScenarioMap = null;
        CheckUtils checkUtilsInstance = CheckUtils.getCheckUtilsInstance();
        this.mCheckUtils = checkUtilsInstance;
        this.mDevicemap = checkUtilsInstance.getDeviceMap();
        this.mCheckHightTemp = CheckHightTemp.getCheckHighTempInstance();
        this.mEvaluationJudge = EvaluationJudge.getEvaluationJudgeInstance();
        this.mGpuFreqPath = this.mDevicemap.get("gpu");
        this.mCurrentList = new ArrayList();
        HashMap map = new HashMap();
        this.mScenarioMap = map;
        if (!this.isOdmXml) {
            map.put(0, "normal");
            this.mScenarioMap.put(1, "huanji");
            this.mScenarioMap.put(2, Weather.AlertInfo.ABNORMAL);
            this.mScenarioMap.put(3, "nightvideo");
            this.mScenarioMap.put(4, "dolbyvision");
            this.mScenarioMap.put(5, "phone");
            this.mScenarioMap.put(6, "evaluation");
            this.mScenarioMap.put(7, "class0");
            this.mScenarioMap.put(8, "video");
            this.mScenarioMap.put(9, "evaluation");
            this.mScenarioMap.put(10, "navigation");
            this.mScenarioMap.put(11, "video");
            this.mScenarioMap.put(12, "demo");
            this.mScenarioMap.put(13, "sptm");
            this.mScenarioMap.put(14, "videochat");
            this.mScenarioMap.put(15, "camera");
            this.mScenarioMap.put(16, "4k");
            this.mScenarioMap.put(17, "4k");
            this.mScenarioMap.put(18, "game");
            this.mScenarioMap.put(19, "game");
            this.mScenarioMap.put(20, "game");
        }
        this.mLastTimeStamp = System.currentTimeMillis();
        this.mPowerManager = (PowerManager) PowerKeeperApplication.b().getSystemService(NightStandbyRecord.KEY_POWER);
        double totalBatteryCapacity = BSHelper.getTotalBatteryCapacity(PowerKeeperApplication.b());
        this.mCapacity = totalBatteryCapacity;
        this.mCacuCapacity = totalBatteryCapacity / 100.0d;
        ThermalDfsThreadPool thermalDfsThreadPoolInstance = ThermalDfsThreadPool.getThermalDfsThreadPoolInstance();
        this.mThermalDfsThreadPool = thermalDfsThreadPoolInstance;
        this.mScheduledExecutorService = thermalDfsThreadPoolInstance.getScheduledExecutorService();
        this.mSimpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        if (DEBUG) {
            Log.d(TAG, "get capacity is " + this.mCapacity);
        }
    }

    public static synchronized CollectionData getCollectionDataInstance() {
        try {
            if (mCollectionDataInstance == null) {
                mCollectionDataInstance = new CollectionData();
            }
        } catch (Throwable th) {
            throw th;
        }
        return mCollectionDataInstance;
    }

    public void ambientTempSendEvent(String str) {
        ReportEventNum reportEventNum = ReportEventNum.AmbitionTempReportNum;
        MiEvent miEvent = new MiEvent(reportEventNum.getDataNum());
        miEvent.addStr("time", str);
        miEvent.addInt("ambitiom", this.mAmbientTemp);
        MiSight.sendEvent(miEvent);
        setDumpMap(reportEventNum.getDataNum() + "", "time: " + str + " ambition: " + this.mAmbientTemp);
    }

    public void auxiliaryInfoSendEvent() {
        MiEvent miEvent = new MiEvent(ReportEventNum.AuxiliaryInformationNum.getDataNum());
        String str = "";
        for (Map.Entry<String, Integer> entry : this.mHotPlugDataMap.entrySet()) {
            str = str + entry.getKey() + ":" + entry.getValue() + " ";
        }
        miEvent.addStr("hotplug", str);
        miEvent.addInt("gpu", this.mGpu);
        MiSight.sendEvent(miEvent);
        setDumpMap(ReportEventNum.AuxiliaryInformationNum.getDataNum() + "", printAuxiliaryInfo());
    }

    public void boardTempInaccurateSendEvent() {
        ReportEventNum reportEventNum = ReportEventNum.BoardTempInaccurateNum;
        MiEvent miEvent = new MiEvent(reportEventNum.getDataNum());
        miEvent.addStr("packageName", this.mPackageName);
        miEvent.addStr("scenarioName", this.mScenarioName);
        miEvent.addInt("broadTemp", this.mSceneTemp);
        miEvent.addInt("batteryTemp", this.mBattery);
        MiSight.sendEvent(miEvent);
        setDumpMap(reportEventNum.getDataNum() + "", " sceneTemp:" + this.mSceneTemp + " battery:" + this.mBattery);
    }

    public void boardTempReportSendEvent(String str, int i2) {
        ReportEventNum reportEventNum = ReportEventNum.BoardTempReport;
        MiEvent miEvent = new MiEvent(reportEventNum.getDataNum());
        miEvent.addStr("time", str);
        miEvent.addInt("broadTemp", i2);
        miEvent.addStr("packageName", this.mPackageName);
        miEvent.addStr("scenarioName", this.mScenarioName);
        MiSight.sendEvent(miEvent);
        setDumpMap(reportEventNum.getDataNum() + "", "time:" + str + " temp:" + i2 + " packageName:" + this.mPackageName + " scenarioName:" + this.mScenarioName);
    }

    public synchronized void clearModem() {
        this.mModem = -1;
    }

    public void clearTemperatureList() {
        if (this.mTemperatureList.size() > 0) {
            synchronized (this.mTemperatureList) {
                this.mTemperatureList.clear();
            }
        }
    }

    public synchronized void clearWifi() {
        this.mWifi = -1;
    }

    public synchronized String convertScenarioName() {
        try {
            setPreScenarioName(this.mScenarioName);
            this.mSceneNamePrefix = "";
            this.mScenarioName = "normal";
            Integer numTransToInteger = this.mCheckUtils.transToInteger(TAG, this.mSconfig);
            int iIntValue = numTransToInteger.intValue();
            if (!this.isOdmXml) {
                int i2 = iIntValue % 100;
                int i3 = iIntValue / 100;
                if (i2 >= 50) {
                    this.mSceneNamePrefix += "pre-";
                    i2 -= 50;
                }
                if (this.mUnfoldList.contains(Integer.valueOf(i3))) {
                    this.mSceneNamePrefix += "unfold-";
                }
                if (DEBUG) {
                    Log.d(TAG, "get configTemp and unfoldTemp is " + i2 + " " + i3);
                }
                if (iIntValue < 400) {
                    if (this.mScenarioMap.containsKey(Integer.valueOf(i2))) {
                        this.mScenarioName = this.mScenarioMap.get(Integer.valueOf(i2));
                    }
                } else if (iIntValue < 700) {
                    if (i2 == 1) {
                        this.mScenarioName = "hp-game";
                    } else {
                        this.mScenarioName = "hp-normal";
                    }
                } else if (i2 == 0) {
                    this.mScenarioName = "cgame";
                } else if (i2 == 1) {
                    this.mScenarioName = "cclassvideo";
                } else if (i2 == 2) {
                    this.mScenarioName = "comp";
                }
                this.mScenarioName = this.mSceneNamePrefix + this.mScenarioName;
            } else if (this.mScenarioMap.containsKey(numTransToInteger)) {
                this.mScenarioName = this.mScenarioMap.get(numTransToInteger);
            }
            this.mSceneStartTimeStamp = System.currentTimeMillis();
            Log.d(TAG, "get scenarioname is " + this.mScenarioName);
        } catch (Throwable th) {
            throw th;
        }
        return this.mScenarioName;
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("<<< DFS >>>");
        setDumpMap("device Ntc", CheckUtils.getCheckUtilsInstance().printNtcAndDevice());
        Map<String, String> map = this.mDumpMap;
        if (map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                printWriter.print(entry.getKey());
                printWriter.print(":");
                printWriter.println(entry.getValue() + " ");
            }
            printWriter.println();
            printWriter.flush();
        }
    }

    public synchronized int getAmbientTemp() {
        return this.mAmbientTemp;
    }

    public synchronized int getBattery() {
        return this.mBattery;
    }

    public int getBrightness() {
        this.mBrightness = -1;
        try {
            if (isScreenOn()) {
                this.mBrightness = Settings.System.getInt(PowerKeeperApplication.b().getContentResolver(), "screen_brightness");
            } else {
                this.mBrightness = 0;
            }
        } catch (Settings.SettingNotFoundException unused) {
            Log.e(TAG, "Settings.SettingNotFoundException in get SCREEN_BRIGHTNESS");
        }
        return this.mBrightness;
    }

    public String getCpu() {
        return this.mCpu;
    }

    public synchronized int getCurrent() {
        int i2;
        try {
            i2 = Integer.parseInt(FileUtil.getFileString("/sys/class/power_supply/battery/current_now"));
            this.mCurrent = i2;
        } catch (NumberFormatException unused) {
            Log.e(TAG, "can not get current");
            return 0;
        }
        return i2;
    }

    public int getGpu() {
        return this.mGpu;
    }

    public Map<String, Integer> getHotPlugDataMap() {
        return this.mHotPlugDataMap;
    }

    public synchronized CollectionDataFunction getInCollectionDataFunctionInstance() {
        try {
            if (this.mCollectionDataFunctionInstance == null) {
                this.mCollectionDataFunctionInstance = new CollectionDataFunction();
            }
        } catch (Throwable th) {
            throw th;
        }
        return this.mCollectionDataFunctionInstance;
    }

    public synchronized int getLastSceneTemp() {
        return this.mLastSceneTemp;
    }

    public synchronized int getModem() {
        return this.mModem;
    }

    public Map<String, Integer> getNtcDataMap() {
        Map<String, Integer> map = this.mNtcDataMap;
        if (map == null || map.size() <= 0) {
            return null;
        }
        return this.mNtcDataMap;
    }

    public synchronized String getPackageName() {
        return this.mPackageName;
    }

    public synchronized String getScenarioName() {
        return this.mScenarioName;
    }

    public synchronized int getSceneTemp() {
        return this.mSceneTemp;
    }

    public String getSconfig() {
        return this.mSconfig;
    }

    public int getTempState() {
        return this.mTempState;
    }

    public Map<Long, Integer> getTemperatureList() {
        return this.mTemperatureList;
    }

    public synchronized int getWifi() {
        return this.mWifi;
    }

    public void highTempDataColltSendEvent() {
        MiEvent miEvent = new MiEvent(ReportEventNum.HighTempDataColltNum.getDataNum());
        miEvent.addStr("packageName", this.mPackageName);
        miEvent.addStr("scenarioName", this.mScenarioName);
        miEvent.addInt("broadTemp", this.mSceneTemp);
        miEvent.addInt("ambientTemp", this.mAmbientTemp);
        miEvent.addInt("startTemp", this.mLastSceneTemp);
        miEvent.addInt("modem", this.mModem);
        miEvent.addInt("wifi", this.mWifi);
        miEvent.addInt("tempState", this.mTempState);
        miEvent.addInt("gpu", this.mGpu);
        miEvent.addInt("brightness", getBrightness());
        miEvent.addStr(LogType.LOG_TYPE_CPU_NAME, this.mCpu);
        String str = "battery:" + this.mBattery + " ";
        for (Map.Entry<String, Integer> entry : this.mNtcDataMap.entrySet()) {
            str = str + entry.getKey() + ":" + entry.getValue() + " ";
        }
        miEvent.addStr("ntc", str);
        miEvent.addStr(LogType.LOG_TYPE_F_SAMPLING_CURRENT_NAME, printCurrentToString());
        MiSight.sendEvent(miEvent);
        setDumpMap(ReportEventNum.HighTempDataColltNum.getDataNum() + "", printToString());
    }

    public synchronized boolean isScreenOn() {
        return this.mScreenOn;
    }

    public void lastBoardTempHighReportSendEvent() {
        ReportEventNum reportEventNum = ReportEventNum.LastBoardTempReport;
        MiEvent miEvent = new MiEvent(reportEventNum.getDataNum());
        miEvent.addStr("packageName", this.mPackageName);
        miEvent.addStr("scenarioName", this.mScenarioName);
        miEvent.addInt("startTemp", this.mLastSceneTemp);
        miEvent.addInt("broadTemp", this.mSceneTemp);
        MiSight.sendEvent(miEvent);
        setDumpMap(reportEventNum.getDataNum() + "", "startTemp:" + this.mLastSceneTemp + " packageName:" + this.mPackageName + " scenarioName:" + this.mScenarioName);
    }

    public void ntcAbnormaSendEvent(String[] strArr) {
        ReportEventNum reportEventNum = ReportEventNum.NtcAbnormalNum;
        MiEvent miEvent = new MiEvent(reportEventNum.getDataNum());
        miEvent.addStrArray("ntcDatas", strArr);
        MiSight.sendEvent(miEvent);
        setDumpMap(reportEventNum.getDataNum() + "", "ntcDatas:" + String.join(";", strArr));
    }

    public String printAuxiliaryInfo() {
        String str = "";
        for (Map.Entry<String, Integer> entry : this.mHotPlugDataMap.entrySet()) {
            str = str + entry.getKey() + ":" + entry.getValue() + " ";
        }
        return str + "gpu:" + this.mGpu;
    }

    public String printCurrentToString() {
        String str = "";
        for (String str2 : this.mCurrentList) {
            List<String> list = this.mCurrentList;
            str = str2.equals(list.get(list.size() - 1)) ? str + str2 : str + str2 + ";";
        }
        return str;
    }

    public String printToString() {
        String str = "packageName:" + this.mPackageName + " scenarioName:" + this.mScenarioName + " boardTemp:" + this.mSceneTemp + " ambientTemp:null StartTemp:" + this.mLastSceneTemp + " modem:" + this.mModem + " wifi:" + this.mWifi + " tempState:" + this.mTempState + " cpu:" + this.mCpu + " gpu:" + this.mGpu + " brightness:" + getBrightness() + " battery:" + this.mBattery + " ";
        for (Map.Entry<String, Integer> entry : this.mNtcDataMap.entrySet()) {
            str = str + entry.getKey() + ":" + entry.getValue() + " ";
        }
        return str + "current:" + printCurrentToString();
    }

    public synchronized void putCurrentList(String str) {
        try {
            if (this.mCurrentList.size() >= 3) {
                String strRemove = this.mCurrentList.remove(0);
                if (DEBUG) {
                    Log.d(TAG, "delet " + strRemove);
                }
            }
            this.mCurrentList.add(str);
            if (DEBUG) {
                Log.d(TAG, "add " + str + " the first is " + this.mCurrentList.get(0));
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    public void putScenarioMap(int i2, String str) {
        synchronized (this.mScenarioMap) {
            this.mScenarioMap.put(Integer.valueOf(i2), str);
        }
    }

    public void putTemperatureList(Long l2, int i2) {
        Map<Long, Integer> map = this.mTemperatureList;
        if (map != null) {
            synchronized (map) {
                try {
                    if (!this.mTemperatureList.containsKey(l2) && i2 > this.mCheckUtils.mTransError) {
                        this.mTemperatureList.put(l2, Integer.valueOf(i2));
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    public void registerBatteryChange() {
        if (this.mBatteryChangedCallback == null) {
            this.mBatteryChangedCallback = new PowerKeeperInterface.d(ThermalStateObserver.getThermalStateObserverInstance().getObservehandler(), this.mBatteryStatusReceiver);
        }
        PowerKeeperManager.getInstance().registerBatteryChangedListener(this.mBatteryChangedCallback);
    }

    public synchronized void setAmbientTemp(int i2) {
        if (this.mAmbientTemp == i2) {
            Log.d(TAG, "ambienttemp is not change");
            return;
        }
        this.mAmbientTemp = i2;
        ambientTempSendEvent(this.mSimpleDateFormat.format(new Date(System.currentTimeMillis())));
        Log.d(TAG, "set ambition temp is " + i2);
    }

    public synchronized void setBattery(int i2) {
        try {
            if (i2 < 100) {
                this.mBattery = i2 * 1000;
            } else {
                this.mBattery = i2;
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    public synchronized void setCpu() {
        String str = "";
        File file = new File("/sys/devices/system/cpu/cpufreq");
        if (!file.exists()) {
            Log.e(TAG, "cpu freq head is not exists");
            return;
        }
        for (File file2 : file.listFiles()) {
            if (file2.isDirectory() && file2.getName().contains(PowerCheckerCloudConfigure.POLICY_ITEM)) {
                StringBuilder sb = new StringBuilder();
                sb.append("/sys/devices/system/cpu/cpufreq");
                String str2 = File.separator;
                sb.append(str2);
                sb.append(file2.getName());
                sb.append(str2);
                sb.append("scaling_max_freq");
                str = str + file2.getName() + ":" + FileUtil.getFileString(sb.toString()) + " ";
            }
        }
        this.mCpu = str;
    }

    public synchronized void setCurrent(int i2) {
        this.mCurrent = i2;
    }

    public void setDumpMap(String str, String str2) {
        Map<String, String> map = this.mDumpMap;
        if (map != null) {
            synchronized (map) {
                this.mDumpMap.put(str, str2);
            }
        }
    }

    public synchronized boolean setGpu(int i2) {
        if (this.mGpu == i2) {
            return false;
        }
        this.mGpu = i2;
        return true;
    }

    public boolean setHotPlugDataMap(String str, int i2) {
        if (!str.contains("hotplug")) {
            return false;
        }
        if (this.mHotPlugDataMap.containsKey(str) && i2 == this.mHotPlugDataMap.get(str).intValue()) {
            return false;
        }
        synchronized (this.mHotPlugDataMap) {
            this.mHotPlugDataMap.put(str, Integer.valueOf(i2));
        }
        return true;
    }

    public synchronized void setIsOdmXml(boolean z2) {
        this.isOdmXml = z2;
    }

    public synchronized void setLastSceneTemp(int i2) {
        this.mLastSceneTemp = i2;
    }

    public synchronized void setModem(int i2) {
        this.mModem = i2;
    }

    public synchronized void setPackageName(ForegroundInfo foregroundInfo) {
        String str = foregroundInfo.mForegroundPackageName;
        if (!TextUtils.isEmpty(str)) {
            this.mPackageName = str;
            if (DEBUG) {
                Log.d(TAG, "get package is " + this.mPackageName);
            }
        }
    }

    public synchronized void setPreScenarioName(String str) {
        try {
            if (!this.mPreScenarioName.equals(str)) {
                this.mPreScenarioName = str;
                if (str.contains("evaluation") || this.mPreScenarioName.contains("arvr")) {
                    this.mEvaluationJudge.setIsNolimit(this.mSceneStartTimeStamp, getSceneTemp());
                }
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    public synchronized void setSceneTemp(int i2) {
        this.mSceneTemp = i2;
    }

    public void setSconfig(String str) {
        this.mSconfig = str;
    }

    public synchronized void setScreenOn(boolean z2) {
        this.mScreenOn = z2;
    }

    public synchronized void setTempState() {
        int i2;
        try {
            i2 = Integer.parseInt(FileUtil.getFileString("/sys/class/thermal/thermal_message/temp_state"));
        } catch (NumberFormatException e2) {
            Log.e(TAG, "can not get tempstate " + e2);
            i2 = -1;
        }
        this.mTempState = i2;
    }

    public synchronized void setWifi(int i2) {
        this.mWifi = i2;
    }

    public synchronized void startGetAverageTemp() {
        try {
            if (this.mGetAverageTemp == null) {
                this.mGetAverageTemp = new GetAverageTemp();
            }
            this.mGetAverageTemp.startCaculAverageTemp();
        } catch (Throwable th) {
            throw th;
        }
    }

    public void stopCollectionData() {
        PowerKeeperManager.getInstance().unregisterBatteryChangedListener(this.mBatteryChangedCallback);
        GetAverageTemp getAverageTemp = this.mGetAverageTemp;
        if (getAverageTemp != null) {
            getAverageTemp.stopCaculAverageTemp();
        }
        this.mLastBatteryLevel = 0;
    }

    public void updateNtcDataMap(String str, int i2) {
        Map<String, Integer> map = this.mNtcDataMap;
        if (map != null) {
            synchronized (map) {
                this.mNtcDataMap.put(str, Integer.valueOf(i2));
            }
        }
    }
}
