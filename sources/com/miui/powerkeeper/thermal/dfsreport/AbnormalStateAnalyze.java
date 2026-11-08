package com.miui.powerkeeper.thermal.dfsreport;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.miui.powerkeeper.thermal.ThermalService;
import com.miui.powerkeeper.thermal.dfscheck.CheckHightTemp;
import com.miui.powerkeeper.thermal.dfscheck.CheckUtils;
import com.miui.powerkeeper.thermal.dfscheck.CollectionData;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import miui.process.ForegroundInfo;

/* JADX INFO: loaded from: classes.dex */
public class AbnormalStateAnalyze {
    private static final boolean DEBUG = Build.IS_USERDEBUG;
    private static final String TAG = "powerkeeper.dfsanalyze";
    private static AbnormalStateAnalyze mAbnormalStateAnalyzeInstance;
    private AbnormalAnalyzeHandler mAbnormalAnalyzeHandler;
    private CheckHightTemp mCheckHightTemp;
    private CheckUtils mCheckUtils;
    private CollectionData mCollectionData;
    private ExecutorService mFixThreadPool;
    private ThermalDfsThreadPool mThermalDfsThreadPool;
    private ThermalStateObserver mThermalStateObserver;

    /* JADX INFO: Access modifiers changed from: private */
    class AbnormalAnalyzeHandler extends Handler {
        public AbnormalAnalyzeHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i2 = message.what;
            if (i2 == -37) {
                AbnormalStateAnalyze.this.mThermalStateObserver.updateBoardTemp();
                return;
            }
            if (i2 == -9) {
                AbnormalStateAnalyze.this.mCollectionData.setPackageName((ForegroundInfo) message.obj);
                return;
            }
            if (i2 == 101) {
                if (AbnormalStateAnalyze.DEBUG) {
                    Log.d(AbnormalStateAnalyze.TAG, "AbnormalAnalyzeHandler get BOARD_SENSOR_TEMP_HIGHT");
                }
                AbnormalStateAnalyze.this.analyzeBoardTempHight();
            } else if (i2 == 103) {
                if (AbnormalStateAnalyze.DEBUG) {
                    Log.d(AbnormalStateAnalyze.TAG, "AbnormalAnalyzeHandler get DEVICE_STATE_REPORT");
                }
                AbnormalStateAnalyze.this.analyzeDeviceReport();
            } else if (i2 == -5) {
                AbnormalStateAnalyze.this.mCollectionData.setScreenOn(false);
                Log.d(AbnormalStateAnalyze.TAG, "screen is off");
            } else {
                if (i2 != -4) {
                    return;
                }
                AbnormalStateAnalyze.this.mCollectionData.setScreenOn(true);
                Log.d(AbnormalStateAnalyze.TAG, "screen is on");
            }
        }
    }

    private AbnormalStateAnalyze() {
        init();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void analyzeBoardTempHight() {
        Log.d(TAG, "for ThermalAbnormalCallback analyzeBoardTempHight");
        List<ThermalDfsInterface.ThermalAbnormalCallback> hightTempCallbackArray = this.mCheckHightTemp.getHightTempCallbackArray();
        Log.d(TAG, "callback size is " + hightTempCallbackArray.size());
        this.mCheckHightTemp.resetCallbackCount();
        for (ThermalDfsInterface.ThermalAbnormalCallback thermalAbnormalCallback : hightTempCallbackArray) {
            if (thermalAbnormalCallback != null && !thermalAbnormalCallback.mDfsc.checkTarget()) {
                Log.d(TAG, "get false to stop");
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void analyzeDeviceReport() {
        this.mFixThreadPool.execute(new Runnable() { // from class: com.miui.powerkeeper.thermal.dfsreport.AbnormalStateAnalyze.1
            @Override // java.lang.Runnable
            public void run() {
                AbnormalStateAnalyze.this.updateDeviceData(AbnormalStateAnalyze.this.mThermalStateObserver.getReportSplit());
            }
        });
    }

    public static synchronized AbnormalStateAnalyze getAbnormalStateAnalyzeInstance() {
        try {
            if (mAbnormalStateAnalyzeInstance == null) {
                mAbnormalStateAnalyzeInstance = new AbnormalStateAnalyze();
            }
        } catch (Throwable th) {
            throw th;
        }
        return mAbnormalStateAnalyzeInstance;
    }

    private void init() {
        Log.d(TAG, "init()");
        this.mAbnormalAnalyzeHandler = new AbnormalAnalyzeHandler(ThermalService.getThread().getLooper());
        this.mCollectionData = CollectionData.getCollectionDataInstance();
        this.mCheckUtils = CheckUtils.getCheckUtilsInstance();
        this.mCheckHightTemp = CheckHightTemp.getCheckHighTempInstance();
        this.mThermalStateObserver = ThermalStateObserver.getThermalStateObserverInstance();
        ThermalDfsThreadPool thermalDfsThreadPoolInstance = ThermalDfsThreadPool.getThermalDfsThreadPoolInstance();
        this.mThermalDfsThreadPool = thermalDfsThreadPoolInstance;
        this.mFixThreadPool = thermalDfsThreadPoolInstance.getFixThreadPool();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDeviceData(ArrayList<String> arrayList) {
        boolean z2;
        CheckUtils.getCheckUtilsInstance().getDeviceMap();
        Iterator<String> it = arrayList.iterator();
        loop0: while (true) {
            z2 = false;
            while (true) {
                if (!it.hasNext()) {
                    break loop0;
                }
                String[] strArrSplit = it.next().split(":");
                if (strArrSplit.length > 1) {
                    String str = strArrSplit[0];
                    if (str.trim().equals("gpu")) {
                        if (!this.mCollectionData.setGpu(this.mCheckUtils.levelTransToInteger(TAG, strArrSplit[1]).intValue()) && !z2) {
                            break;
                        }
                        z2 = true;
                    } else if (str.contains("hotplug")) {
                        if (!this.mCollectionData.setHotPlugDataMap(strArrSplit[0], this.mCheckUtils.levelTransToInteger(TAG, strArrSplit[1]).intValue()) && !z2) {
                            break;
                        }
                        z2 = true;
                    } else {
                        continue;
                    }
                }
            }
        }
        if (z2) {
            Log.d(TAG, "gpu or hotplug is be modified send " + this.mCollectionData.printAuxiliaryInfo());
            this.mCollectionData.auxiliaryInfoSendEvent();
        }
    }

    public AbnormalAnalyzeHandler getAbnormalAnalyzeHandler() {
        return this.mAbnormalAnalyzeHandler;
    }
}
