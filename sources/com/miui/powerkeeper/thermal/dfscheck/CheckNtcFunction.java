package com.miui.powerkeeper.thermal.dfscheck;

import android.os.Build;
import android.util.Log;
import com.miui.powerkeeper.thermal.dfsreport.SceneAndNTCTemperatureData;
import com.miui.powerkeeper.thermal.dfsreport.ThermalDfsInterface;
import com.miui.powerkeeper.thermal.dfsreport.ThermalDfsThreadPool;
import com.miui.powerkeeper.utils.FileUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/* JADX INFO: loaded from: classes.dex */
public class CheckNtcFunction implements ThermalDfsInterface.dfsCheck {
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    private static final int NTC_LIST_SIZE = 20;
    private static final String TAG = "powerkeeper.dfsntc";
    private static CheckNtcFunction mCheckNtcFunctionInstance;
    private final int DEFAULT_XO_TEMP_DIV = 1;
    private final int DEFAULT_XO_TEMP_DIV_THOUSANDS = 1000;
    private int mXoTempDiv = 1;
    private ArrayList<String> mReportNtcList = new ArrayList<>();
    private CheckHightTemp mCheckHightTemp = CheckHightTemp.getCheckHighTempInstance();
    private ExecutorService mFixThreadPool = ThermalDfsThreadPool.getThermalDfsThreadPoolInstance().getFixThreadPool();
    private CollectionData mCollectionData = CollectionData.getCollectionDataInstance();

    private CheckNtcFunction() {
    }

    public static synchronized CheckNtcFunction getCheckNtcFunctionInstance() {
        try {
            if (mCheckNtcFunctionInstance == null) {
                mCheckNtcFunctionInstance = new CheckNtcFunction();
            }
        } catch (Throwable th) {
            throw th;
        }
        return mCheckNtcFunctionInstance;
    }

    public void check(String str, String str2) {
        int curNtcLimitTemp = SceneAndNTCTemperatureData.getSceneTemperatureDataInstance().getCurNtcLimitTemp(str);
        boolean z2 = DEBUG;
        if (z2) {
            Log.d(TAG, "get " + str + "limitTemp is " + curNtcLimitTemp);
        }
        String fileString = FileUtil.getFileString(str2);
        if (fileString == null) {
            Log.e(TAG, "can not read temp from " + str2);
            return;
        }
        int iIntValue = CheckUtils.getCheckUtilsInstance().transToInteger(TAG, fileString).intValue();
        if (iIntValue == CheckUtils.getCheckUtilsInstance().mTransError) {
            Log.e(TAG, "can not trans temp");
            return;
        }
        if (z2) {
            Log.d(TAG, "get " + str + " : " + iIntValue);
        }
        this.mCollectionData.updateNtcDataMap(str, iIntValue);
        if (iIntValue > curNtcLimitTemp || iIntValue < -30000) {
            Log.e(TAG, "NTC " + str + " Temp is " + iIntValue);
            if (this.mReportNtcList.size() < 20) {
                this.mReportNtcList.add(str + ":" + iIntValue);
            }
        }
    }

    @Override // com.miui.powerkeeper.thermal.dfsreport.ThermalDfsInterface.dfsCheck
    public boolean checkTarget() {
        final HashMap<String, String> ntcMap = CheckUtils.getCheckUtilsInstance().getNtcMap();
        if (this.mCollectionData.getBattery() != 0 && this.mCollectionData.getSceneTemp() - this.mCollectionData.getBattery() > 5000) {
            Log.e(TAG, this.mCollectionData.getSceneTemp() + " is big gap with battery " + this.mCollectionData.getBattery());
            this.mCheckHightTemp.notAllowReportHighTemp();
            this.mCollectionData.boardTempInaccurateSendEvent();
        }
        this.mFixThreadPool.execute(new Runnable() { // from class: com.miui.powerkeeper.thermal.dfscheck.CheckNtcFunction.1
            @Override // java.lang.Runnable
            public void run() {
                CheckNtcFunction.this.mReportNtcList.clear();
                for (Map.Entry entry : ntcMap.entrySet()) {
                    CheckNtcFunction.this.check((String) entry.getKey(), (String) entry.getValue());
                }
                if (CheckNtcFunction.this.mReportNtcList.size() <= 0 || CheckNtcFunction.this.mReportNtcList.size() > 20) {
                    Log.d(CheckNtcFunction.TAG, "reportNtcList size is  " + CheckNtcFunction.this.mReportNtcList.size() + " not report");
                } else {
                    CheckNtcFunction.this.mCollectionData.ntcAbnormaSendEvent((String[]) CheckNtcFunction.this.mReportNtcList.toArray(new String[CheckNtcFunction.this.mReportNtcList.size()]));
                }
                CheckNtcFunction.this.mCheckHightTemp.finshCallback(CheckNtcFunction.TAG);
            }
        });
        return true;
    }
}
