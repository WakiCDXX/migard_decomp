package com.miui.powerkeeper.thermal.dfscheck;

import android.os.Build;
import android.os.Handler;
import android.util.Log;
import com.miui.powerkeeper.thermal.dfsreport.ThermalDfsInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/* JADX INFO: loaded from: classes.dex */
public class CheckHightTemp implements ThermalDfsInterface.dfsAbnormalCheckRegister {
    private static final String TAG = "powerKeeper.dfshightemp";
    private int mCallbackSize;
    private CheckUtils mCheckUtils;
    private List<ThermalDfsInterface.ThermalAbnormalCallback> mHightTempCallbackArray;
    private Handler mhandler;
    private static final boolean DEBUG = Build.IS_USERDEBUG;
    private static CheckHightTemp mCheckHightTempInstance = null;
    private AtomicInteger mCallbackCount = new AtomicInteger();
    private AtomicBoolean mAllowReportHighTemp = new AtomicBoolean(true);

    private CheckHightTemp() {
        this.mCheckUtils = null;
        this.mHightTempCallbackArray = null;
        this.mHightTempCallbackArray = new ArrayList();
        this.mCheckUtils = CheckUtils.getCheckUtilsInstance();
    }

    public static synchronized CheckHightTemp getCheckHighTempInstance() {
        try {
            if (mCheckHightTempInstance == null) {
                mCheckHightTempInstance = new CheckHightTemp();
            }
        } catch (Throwable th) {
            throw th;
        }
        return mCheckHightTempInstance;
    }

    public void finshCallback(String str) {
        Log.d(TAG, str + " has finsh");
        this.mCallbackCount.decrementAndGet();
    }

    public Boolean getAllowReportHighTemp() {
        return Boolean.valueOf(this.mAllowReportHighTemp.get());
    }

    public int getCallbackCount() {
        return this.mCallbackCount.get();
    }

    public int getCallbackSize() {
        return this.mCallbackSize;
    }

    public List<ThermalDfsInterface.ThermalAbnormalCallback> getHightTempCallbackArray() {
        return this.mHightTempCallbackArray;
    }

    public void notAllowReportHighTemp() {
        this.mAllowReportHighTemp.set(false);
    }

    @Override // com.miui.powerkeeper.thermal.dfsreport.ThermalDfsInterface.dfsAbnormalCheckRegister
    public void registerCallbackInArray(ThermalDfsInterface.ThermalAbnormalCallback thermalAbnormalCallback) {
        synchronized (this.mHightTempCallbackArray) {
            try {
                if (!this.mHightTempCallbackArray.contains(thermalAbnormalCallback)) {
                    this.mHightTempCallbackArray.add(thermalAbnormalCallback);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void resetAllowReportHighTempToTrue() {
        this.mAllowReportHighTemp.set(true);
    }

    public void resetCallbackCount() {
        if (DEBUG) {
            Log.d(TAG, "reset callbackcount is " + this.mCallbackSize);
        }
        this.mCallbackCount.set(this.mCallbackSize);
    }

    public void rgisterCheckCallback(Handler handler) {
        if (this.mCallbackSize > 0) {
            Log.i(TAG, "already register call back");
            return;
        }
        Log.i(TAG, "register call back");
        this.mhandler = handler;
        registerCallbackInArray(new ThermalDfsInterface.ThermalAbnormalCallback(this.mhandler, CheckNtcFunction.getCheckNtcFunctionInstance()));
        registerCallbackInArray(new ThermalDfsInterface.ThermalAbnormalCallback(this.mhandler, CheckNetWorkFunction.getCheckNetWorkFunctionInstance()));
        registerCallbackInArray(new ThermalDfsInterface.ThermalAbnormalCallback(this.mhandler, CollectionData.getCollectionDataInstance().getInCollectionDataFunctionInstance()));
        this.mCallbackSize = this.mHightTempCallbackArray.size();
        Log.i(TAG, "Hight temp register callback size is " + this.mCallbackSize);
    }

    @Override // com.miui.powerkeeper.thermal.dfsreport.ThermalDfsInterface.dfsAbnormalCheckRegister
    public void unRegisterCallbackInArray(ThermalDfsInterface.ThermalAbnormalCallback thermalAbnormalCallback) {
        synchronized (this.mHightTempCallbackArray) {
            try {
                if (this.mHightTempCallbackArray.contains(thermalAbnormalCallback)) {
                    this.mHightTempCallbackArray.remove(thermalAbnormalCallback);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
