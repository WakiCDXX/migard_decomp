package com.miui.powerkeeper.thermal.dfsreport;

import android.os.Build;
import android.os.FileObserver;
import android.os.Handler;
import android.util.Log;
import b.b;
import com.miui.powerkeeper.PowerKeeperApplication;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.cloudcontrol.AutoRegister;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.provider.SimpleSettingKeys;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.thermal.dfscheck.CheckHightTemp;
import com.miui.powerkeeper.thermal.dfscheck.CheckUtils;
import com.miui.powerkeeper.thermal.dfscheck.CollectionData;
import com.miui.powerkeeper.utils.FileUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class ThermalStateObserver extends AutoRegister {
    private static final boolean DEBUG = Build.IS_USERDEBUG;
    private static final String TAG = "powerkeeper.dfsobserve";
    private static ThermalStateObserver mThermalStateObserverInstance;
    public boolean mAllowDfsDevice;
    private CheckHightTemp mCheckHightTemp;
    private Set<String> mDfsDeviceArray;
    private EventNotifier mEventNotifier;
    private EventsAggregator mEventsAggregator;
    private Handler mObservehandler;
    private OtherDfsReportListener mOtherDfsReportListener;
    private int mScencLimitTemp;
    private SceneAndNTCTemperatureData mSceneAndNTCTemperatureData;
    private ThermalDfsThreadPool mThermalDfsThreadPool;
    private boolean mBoardSensorTempHightReport = false;
    private boolean board_sensor_temp_up_fast_checked = false;
    private boolean module_func_abnormal_checked = false;
    private boolean mDfsIsWorking = false;
    private final String mReportNodePath = "/data/vendor/thermal/report.dump";
    private final String mCpuHotPlugPath = "/sys/devices/system/cpu/offline";
    private final String mBoardSensorPath = "/sys/class/thermal/thermal_message/board_sensor_temp";
    private final String mSconfigPath = "/sys/class/thermal/thermal_message/sconfig";
    private final String mAmbitionPath = "/sys/class/thermal/thermal_message/ambient_sensor_temp";
    private String mDeviceName = Build.DEVICE;
    private ReportNodeFileListener mReportNodeFileListener = null;
    private SconfigFileListener mSconfigFileListener = null;
    private AmbitionFileListener mAmbitionFileListener = null;
    private CollectionData mCollectionData = null;
    private CheckUtils mCheckUtils = null;
    private ArrayList<String> mReportSplit = null;

    private class AmbitionFileListener extends FileObserver {
        AmbitionFileListener(String str, int i2) {
            super(str, i2);
        }

        @Override // android.os.FileObserver
        public void onEvent(int i2, String str) {
            String fileString;
            if (i2 == 2 && (fileString = FileUtil.getFileString("/sys/class/thermal/thermal_message/ambient_sensor_temp")) != null) {
                if (ThermalStateObserver.DEBUG) {
                    Log.d(ThermalStateObserver.TAG, "ambition modify to " + fileString);
                }
                ThermalStateObserver.this.mCollectionData.setAmbientTemp(ThermalStateObserver.this.mCheckUtils.transToInteger(ThermalStateObserver.TAG, fileString).intValue());
            }
        }
    }

    private class ReportNodeFileListener extends FileObserver {
        ReportNodeFileListener(String str, int i2) {
            super(str, i2);
        }

        @Override // android.os.FileObserver
        public void onEvent(int i2, String str) {
            if (i2 != 2) {
                return;
            }
            if (!ThermalStateObserver.this.getReportMessage("/data/vendor/thermal/report.dump").isEmpty()) {
                ThermalStateObserver thermalStateObserver = ThermalStateObserver.this;
                thermalStateObserver.notifyBoardTempAbnormal(thermalStateObserver.mObservehandler, 103);
            } else {
                Log.e(ThermalStateObserver.TAG, str + " the obtained content is illegal");
            }
        }
    }

    private class SconfigFileListener extends FileObserver {
        SconfigFileListener(String str, int i2) {
            super(str, i2);
        }

        @Override // android.os.FileObserver
        public void onEvent(int i2, String str) {
            String fileString;
            if (i2 == 2 && (fileString = FileUtil.getFileString("/sys/class/thermal/thermal_message/sconfig")) != null) {
                if (ThermalStateObserver.DEBUG) {
                    Log.d(ThermalStateObserver.TAG, "sconfig modify to " + fileString);
                }
                ThermalStateObserver.this.mCollectionData.setSconfig(fileString);
                ThermalStateObserver.this.mCollectionData.setLastSceneTemp(ThermalStateObserver.this.mCollectionData.getSceneTemp());
                ThermalStateObserver.this.mCollectionData.convertScenarioName();
                ThermalStateObserver thermalStateObserver = ThermalStateObserver.this;
                thermalStateObserver.mScencLimitTemp = thermalStateObserver.mSceneAndNTCTemperatureData.getCurSceneLimitTemp(fileString);
                if (ThermalStateObserver.DEBUG) {
                    Log.d(ThermalStateObserver.TAG, "scencLimitTemp modify to " + ThermalStateObserver.this.mScencLimitTemp);
                }
            }
        }
    }

    private ThermalStateObserver() {
        HashSet hashSet = new HashSet(Arrays.asList("rodin", "babylon", "garnet", "degas", "shennong", "houji", "vermeer", "duchamp", "manet", "rothko", "aurora", "ruyi", "chenfeng", "peridot", "goku", "sheng", "uke", "muyu", "dada", "haotian", "dijun", "amethyst", "sapphire", "sapphiren", "tapas", "xun"));
        this.mDfsDeviceArray = hashSet;
        this.mAllowDfsDevice = hashSet.contains(this.mDeviceName);
        this.mSceneAndNTCTemperatureData = null;
        this.mThermalDfsThreadPool = null;
        this.mCheckHightTemp = null;
        this.mEventsAggregator = null;
        this.mEventNotifier = null;
        this.mOtherDfsReportListener = null;
        Log.d(TAG, "ThermalStateObserve6.0");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ArrayList<String> getReportMessage(String str) {
        ArrayList<String> arrayList = new ArrayList<>();
        String fileString = FileUtil.getFileString(str);
        if (fileString == null) {
            Log.e(TAG, "get " + str + "is null");
            return arrayList;
        }
        String[] strArrSplit = fileString.trim().split(" ");
        if (strArrSplit.length > 1) {
            for (String str2 : strArrSplit) {
                arrayList.add(str2);
            }
        }
        this.mReportSplit = arrayList;
        return arrayList;
    }

    public static synchronized ThermalStateObserver getThermalStateObserverInstance() {
        try {
            if (mThermalStateObserverInstance == null) {
                mThermalStateObserverInstance = new ThermalStateObserver();
            }
        } catch (Throwable th) {
            throw th;
        }
        return mThermalStateObserverInstance;
    }

    public synchronized void checkAbnormal() {
        updateFromMisc();
        boolean z2 = this.mDfsIsWorking;
        if (z2 && !this.mAllowDfsDevice) {
            stopObserve();
            this.mDfsIsWorking = false;
            return;
        }
        if (z2 && this.mAllowDfsDevice) {
            return;
        }
        if (!z2 && this.mAllowDfsDevice) {
            this.mDfsIsWorking = true;
            init();
            this.mReportNodeFileListener.startWatching();
            this.mSconfigFileListener.startWatching();
            this.mAmbitionFileListener.startWatching();
            this.mCollectionData.startGetAverageTemp();
            this.mCollectionData.registerBatteryChange();
            this.mSceneAndNTCTemperatureData.parseSceneTempToMap();
            this.mSceneAndNTCTemperatureData.printMapInDump();
            this.mScencLimitTemp = this.mSceneAndNTCTemperatureData.getDefaultSceneTemp();
            this.mEventsAggregator.registerForForegroundInfo(this.mObservehandler);
            this.mEventsAggregator.registerForScreenOn(this.mObservehandler, null);
            this.mEventsAggregator.registerForScreenOff(this.mObservehandler, null);
            this.mEventsAggregator.registerForBoardTemperature(this.mObservehandler);
            OtherDfsReportListener otherDfsReportListenerInstance = OtherDfsReportListener.getOtherDfsReportListenerInstance();
            this.mOtherDfsReportListener = otherDfsReportListenerInstance;
            this.mEventNotifier.registerListener(otherDfsReportListenerInstance);
        }
    }

    @Override // com.miui.powerkeeper.cloudcontrol.AutoRegister
    protected int getModel() {
        return b.P;
    }

    public Handler getObservehandler() {
        return this.mObservehandler;
    }

    public ArrayList<String> getReportSplit() {
        return this.mReportSplit;
    }

    public void init() throws Throwable {
        this.mReportNodeFileListener = new ReportNodeFileListener("/data/vendor/thermal/report.dump", 2);
        this.mSconfigFileListener = new SconfigFileListener("/sys/class/thermal/thermal_message/sconfig", 2);
        this.mAmbitionFileListener = new AmbitionFileListener("/sys/class/thermal/thermal_message/ambient_sensor_temp", 2);
        this.mObservehandler = AbnormalStateAnalyze.getAbnormalStateAnalyzeInstance().getAbnormalAnalyzeHandler();
        this.mCollectionData = CollectionData.getCollectionDataInstance();
        this.mCheckUtils = CheckUtils.getCheckUtilsInstance();
        this.mCheckHightTemp = CheckHightTemp.getCheckHighTempInstance();
        this.mSceneAndNTCTemperatureData = SceneAndNTCTemperatureData.getSceneTemperatureDataInstance();
        this.mThermalDfsThreadPool = ThermalDfsThreadPool.getThermalDfsThreadPoolInstance();
        this.mEventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
        this.mCheckUtils.registerNtcAndDeviceMap();
        this.mCheckHightTemp.rgisterCheckCallback(this.mObservehandler);
        this.mEventNotifier = EventNotifier.getDfsEventNotifierInstance();
    }

    public void notifyBoardTempAbnormal(Handler handler, int i2) {
        Log.d(TAG, "send " + i2);
        handler.sendMessage(handler.obtainMessage(i2));
    }

    public void setReportSplit(ArrayList<String> arrayList) {
        this.mReportSplit = arrayList;
    }

    public void stopObserve() {
        Log.d(TAG, "stop observe");
        if (mThermalStateObserverInstance != null) {
            this.mReportNodeFileListener.stopWatching();
            this.mSconfigFileListener.stopWatching();
            this.mAmbitionFileListener.stopWatching();
            CollectionData collectionData = this.mCollectionData;
            if (collectionData != null) {
                collectionData.stopCollectionData();
            }
            ThermalDfsThreadPool thermalDfsThreadPool = this.mThermalDfsThreadPool;
            if (thermalDfsThreadPool != null) {
                thermalDfsThreadPool.stopThreadTask();
            }
            this.mEventsAggregator.unregisterForForegroundInfo(this.mObservehandler);
            this.mEventsAggregator.unregisterForScreenOn(this.mObservehandler);
            this.mEventsAggregator.unregisterForScreenOff(this.mObservehandler);
            this.mEventsAggregator.unregisterForBoardTemperature(this.mObservehandler);
            this.mEventNotifier.clearListener();
        }
    }

    public void updateBoardTemp() {
        int iIntValue = this.mCheckUtils.transToInteger(TAG, FileUtil.getFileString("/sys/class/thermal/thermal_message/board_sensor_temp")).intValue();
        this.mCollectionData.setSceneTemp(iIntValue);
        this.mCollectionData.putTemperatureList(Long.valueOf(System.currentTimeMillis()), iIntValue);
        if (iIntValue <= this.mScencLimitTemp && this.mCollectionData.getBattery() <= this.mScencLimitTemp) {
            if (this.mBoardSensorTempHightReport) {
                Log.d(TAG, iIntValue + " temp is low to limit " + this.mScencLimitTemp + " set false");
                this.mBoardSensorTempHightReport = false;
                return;
            }
            return;
        }
        if (this.mBoardSensorTempHightReport) {
            if (DEBUG) {
                Log.d(TAG, iIntValue + " temp or battery temp " + this.mCollectionData.getBattery() + " temp is higt to limit " + this.mScencLimitTemp + " but already report");
                return;
            }
            return;
        }
        Log.d(TAG, iIntValue + " temp or battery temp " + this.mCollectionData.getBattery() + " is higt to limit " + this.mScencLimitTemp);
        this.mCheckHightTemp.resetAllowReportHighTempToTrue();
        notifyBoardTempAbnormal(this.mObservehandler, 101);
        if (this.mCollectionData.getLastSceneTemp() > this.mScencLimitTemp) {
            this.mCheckHightTemp.notAllowReportHighTemp();
            this.mCollectionData.lastBoardTempHighReportSendEvent();
        }
        this.mBoardSensorTempHightReport = true;
    }

    @Override // com.miui.powerkeeper.cloudcontrol.IUseCloud
    public void updateCloudData() {
        checkAbnormal();
    }

    public void updateFromMisc() {
        String string = SimpleSettings.Misc.getString(PowerKeeperApplication.b(), SimpleSettingKeys.DFS_DEVICE_LIST, "");
        if (string.length() > 1) {
            String[] strArrSplit = string.split(",");
            if (strArrSplit.length > 0) {
                this.mDfsDeviceArray.clear();
                for (int i2 = 0; i2 < strArrSplit.length; i2++) {
                    this.mDfsDeviceArray.add(strArrSplit[i2].trim());
                    Log.d(TAG, "dfs device add " + strArrSplit[i2]);
                }
                this.mAllowDfsDevice = this.mDfsDeviceArray.contains(this.mDeviceName);
            }
        }
    }

    public void notifyBoardTempAbnormal(Handler handler, int i2, ArrayList arrayList) {
        Log.d(TAG, "send " + i2);
        handler.sendMessage(handler.obtainMessage(i2, arrayList));
    }
}
