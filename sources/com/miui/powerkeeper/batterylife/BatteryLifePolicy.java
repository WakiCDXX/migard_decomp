package com.miui.powerkeeper.batterylife;

import android.content.Context;
import android.content.IntentFilter;
import android.os.SystemProperties;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseLongArray;
import android.view.WindowManager;
import com.miui.powerkeeper.TimeSpan;
import com.miui.powerkeeper.batterylife.data.BatteryDryData;
import com.miui.powerkeeper.batterylife.data.BatteryLife;
import com.miui.powerkeeper.batterylife.data.TimeInStateData;
import com.miui.powerkeeper.batterylife.systeminfo.BSHelper;
import com.miui.powerkeeper.batterylife.systeminfo.CpuStatus;
import com.miui.powerkeeper.batterylife.systeminfo.TelephonyHelper;
import com.miui.powerkeeper.batterylife.topapps.TopAppsManager;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.utils.ShellUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;

/* JADX INFO: loaded from: classes.dex */
public class BatteryLifePolicy {
    public static final String TAG = "BL-BatteryLifePolicy";
    private boolean mAtNight;
    private int mBatteryLevel;
    private Context mContext;
    private long mCurDozeTime;
    private BatteryData mData;
    private double mDesignedBatteryCap;
    private TelephonyHelper mHelper;
    private double mLastBatteryCap;
    private int mLastBatteryCapLevel;
    private double mLastBatteryCapUncharged;
    private long mLastBatteryVoltage;
    private long mLastCharingTime;
    private long mLastOnBatteryTime;
    private TimeInStateData mLastTimeInStateData;
    private boolean mOnBattery;
    private boolean mOnScreenOn;
    private int mPowerMode;
    private long mStartTime;
    private TopAppsManager mTopAppsManager;
    private double mTotalBatteryCap;
    private long mLastDozeTime = 0;
    private double mCurBatteryCap = -1.0d;
    private boolean mCallInIdle = true;
    private BatteryLife mTotalHistrical = new BatteryLife();
    private double mTotalBatteryConsumption = 0.0d;
    private long mTotalChargingDuration = 0;
    private long mTotalOnBatteryDuaration = 0;
    private int mCapacityOnSaveBattery = 0;
    private BatteryLife.FluctuatingState mFluctuatingState = new BatteryLife.FluctuatingState();
    private int mCurrentFgUid = -1;

    BatteryLifePolicy(Context context) {
        this.mLastCharingTime = 0L;
        this.mLastOnBatteryTime = 0L;
        Log.d(TAG, " BatteryLifePolicy start.");
        this.mContext = context;
        BatteryData batteryData = (BatteryData) BLUtils.readFile(getFilePath());
        this.mData = batteryData;
        if (batteryData == null) {
            this.mData = new BatteryData();
        } else {
            Log.d(TAG, "BatteryLifePolicy mData(from file) =\n " + this.mData.toString());
            BatteryData batteryData2 = this.mData;
            if (batteryData2.mBatteryDryOneDayDQ == null) {
                batteryData2.mBatteryDryOneDayDQ = new ArrayDeque<>();
            }
        }
        if (BLUtils.isFlagSupport(1) && this.mData.mEstimateBatteryLifeDQ.size() != 0) {
            initHistoricalBatteryLife();
        }
        this.mTopAppsManager = new TopAppsManager(this.mContext);
        this.mLastTimeInStateData = new TimeInStateData();
        resetTimeInState();
        this.mHelper = new TelephonyHelper(this.mContext);
        this.mTotalBatteryCap = BSHelper.getTotalBatteryCapacity(this.mContext);
        this.mDesignedBatteryCap = BSHelper.getDesignedBatteryCapacity(this.mContext);
        int intExtra = this.mContext.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"), 4).getIntExtra("status", -1);
        boolean z2 = (intExtra == 2 || intExtra == 5) ? false : true;
        this.mOnBattery = z2;
        if (z2) {
            this.mLastBatteryCapUncharged = BSHelper.getCurrentBatteryCap(100);
            this.mLastOnBatteryTime = System.currentTimeMillis();
        } else {
            this.mLastCharingTime = System.currentTimeMillis();
        }
        this.mAtNight = TimeSpan.c();
        this.mPowerMode = EventsAggregator.getInstance(this.mContext).getPowerMode();
        WindowManager windowManager = (WindowManager) context.getSystemService("window");
        if (windowManager != null) {
            this.mOnScreenOn = windowManager.getDefaultDisplay().getState() == 2;
        }
        Log.d(TAG, " BatteryLifePolicy mOnBattery = " + this.mOnBattery + ", mOnScreenOn = " + this.mOnScreenOn);
        startNewData();
    }

    private void addHistoricalBatteryLife(BatteryLife batteryLife) {
        if (batteryLife.dataList.size() == 0) {
            return;
        }
        this.mTotalHistrical.add(batteryLife);
    }

    private void calcFluctuationRate(BatteryLife batteryLife) {
        BatteryLife historicalAverage = getHistoricalAverage();
        for (BatteryLife.Data data : batteryLife.dataList) {
            for (BatteryLife.Data data2 : historicalAverage.dataList) {
                if (data.powerMode == data2.powerMode) {
                    updateFluctuatingState(data, data2);
                    break;
                }
            }
        }
        this.mData.mFLuctuatingStateList.addLast(this.mFluctuatingState);
        if (this.mData.mFLuctuatingStateList.size() > 7) {
            this.mData.mFLuctuatingStateList.removeFirst();
        }
    }

    private void calculateBatteryLife() {
        int i2;
        if (this.mData.mBatteryDryOneDayDQ.size() <= 0) {
            return;
        }
        BatteryLife batteryLife = new BatteryLife();
        batteryLife.date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        SparseArray sparseArray = new SparseArray();
        SparseArray sparseArray2 = new SparseArray();
        SparseLongArray sparseLongArray = new SparseLongArray();
        BatteryLife.MatchingData matchingData = new BatteryLife.MatchingData();
        for (BatteryDryData batteryDryData : this.mData.mBatteryDryOneDayDQ) {
            if (Double.compare(batteryDryData.batteryDry, 0.0d) > 0) {
                BatteryLife.Data data = (BatteryLife.Data) sparseArray.get(batteryDryData.powerMode, new BatteryLife.Data());
                data.powerMode = batteryDryData.powerMode;
                int i3 = (int) (batteryDryData.durationTime * (this.mTotalBatteryCap / batteryDryData.batteryDry));
                int[] iArr = data.life;
                int i4 = batteryDryData.type;
                iArr[i4] = i3;
                data.setCount(i4, i3);
                long[] jArr = data.duration;
                int i5 = batteryDryData.type;
                long j2 = batteryDryData.durationTime;
                jArr[i5] = j2;
                data.totalDuration[i5] = batteryDryData.totalDurationTime;
                double[] dArr = data.batteryDry;
                double d2 = batteryDryData.batteryDry;
                dArr[i5] = d2;
                SparseArray sparseArray3 = sparseArray2;
                SparseLongArray sparseLongArray2 = sparseLongArray;
                data.lifeD[i5] = (int) (j2 * (this.mDesignedBatteryCap / d2));
                BatteryLife.MatchingData matchingData2 = batteryDryData.matchingData;
                if (matchingData2.duration > 0) {
                    data.matchingDatas.add(matchingData2);
                    BatteryLife.MatchingData matchingData3 = batteryDryData.matchingData;
                    matchingData.type = matchingData3.type;
                    matchingData.duration = matchingData3.duration;
                    matchingData.consumption = matchingData3.consumption;
                    matchingData.startTime = matchingData3.startTime;
                    matchingData.endTime = matchingData3.endTime;
                    matchingData.startCap = matchingData3.startCap;
                    matchingData.endCap = matchingData3.endCap;
                    matchingData.startCapLevel = matchingData3.startCapLevel;
                    matchingData.endCapLevel = matchingData3.endCapLevel;
                    matchingData.startVoltage = matchingData3.startVoltage;
                    matchingData.endVoltage = matchingData3.endVoltage;
                }
                sparseArray.put(batteryDryData.powerMode, data);
                int i6 = batteryDryData.powerMode;
                sparseArray3.put(i6, Double.valueOf(((Double) sparseArray3.get(i6, Double.valueOf(0.0d))).doubleValue() + batteryDryData.batteryDry));
                int i7 = batteryDryData.powerMode;
                sparseLongArray = sparseLongArray2;
                sparseLongArray.put(i7, sparseLongArray.get(i7, 0L) + batteryDryData.durationTime);
                sparseArray2 = sparseArray3;
            }
        }
        SparseArray sparseArray4 = sparseArray2;
        for (int i8 = 0; i8 < sparseArray.size(); i8++) {
            int iKeyAt = sparseArray.keyAt(i8);
            BatteryLife.Data data2 = (BatteryLife.Data) sparseArray.valueAt(i8);
            if (Double.compare(((Double) sparseArray4.get(iKeyAt)).doubleValue(), 0.0d) > 0) {
                data2.life[4] = (int) (sparseLongArray.get(iKeyAt) * (this.mTotalBatteryCap / ((Double) sparseArray4.get(iKeyAt)).doubleValue()));
                sparseLongArray = sparseLongArray;
                data2.lifeD[4] = (int) (sparseLongArray.get(iKeyAt) * (this.mDesignedBatteryCap / ((Double) sparseArray4.get(iKeyAt)).doubleValue()));
                data2.setCount(4, data2.life[4]);
                batteryLife.dataList.add(data2);
            }
        }
        BatteryLife.Data data3 = new BatteryLife.Data();
        data3.powerMode = 100;
        int i9 = 0;
        while (true) {
            if (i9 >= sparseArray.size()) {
                break;
            }
            BatteryLife.Data data4 = (BatteryLife.Data) sparseArray.valueAt(i9);
            for (int i10 = 0; i10 < 5; i10++) {
                long[] jArr2 = data3.duration;
                jArr2[i10] = jArr2[i10] + data4.duration[i10];
                long[] jArr3 = data3.totalDuration;
                jArr3[i10] = jArr3[i10] + data4.totalDuration[i10];
                double[] dArr2 = data3.batteryDry;
                dArr2[i10] = dArr2[i10] + data4.batteryDry[i10];
            }
            i9++;
        }
        double d3 = 0.0d;
        int i11 = 0;
        long j3 = 0;
        for (i2 = 5; i11 < i2; i2 = 5) {
            long j4 = data3.duration[i11];
            BatteryLife.MatchingData matchingData4 = matchingData;
            double d4 = this.mTotalBatteryCap;
            double d5 = data3.batteryDry[i11];
            int[] iArr2 = data3.life;
            iArr2[i11] = iArr2[i11] + ((int) (j4 * (d4 / d5)));
            data3.lifeD[i11] = (int) (j4 * (this.mDesignedBatteryCap / d5));
            j3 += j4;
            d3 += d5;
            i11++;
            matchingData = matchingData4;
        }
        BatteryLife.MatchingData matchingData5 = matchingData;
        double d6 = d3;
        if (d6 != 0.0d) {
            double d7 = j3;
            data3.life[4] = (int) ((this.mTotalBatteryCap / d6) * d7);
            data3.lifeD[4] = (int) (d7 * (this.mDesignedBatteryCap / d6));
            if (matchingData5.duration > 0) {
                data3.matchingDatas.add(matchingData5);
            }
            batteryLife.dataList.add(data3);
        }
        this.mData.mEstimateBatteryLifeDQ.addLast(batteryLife);
        BatteryLife batteryLifeRemoveFirst = this.mData.mEstimateBatteryLifeDQ.size() > 7 ? this.mData.mEstimateBatteryLifeDQ.removeFirst() : null;
        if (BLUtils.isFlagSupport(1)) {
            calcFluctuationRate(batteryLife);
            if (batteryLifeRemoveFirst != null) {
                subtractHistoricalBatteryLife(batteryLifeRemoveFirst);
            }
            addHistoricalBatteryLife(batteryLife);
        }
    }

    private BatteryLife getTotalBatteryLife() {
        int i2;
        if (this.mData.mBatteryDryOneDayDQ.size() <= 0) {
            return null;
        }
        BatteryLife batteryLife = new BatteryLife();
        batteryLife.date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        SparseArray sparseArray = new SparseArray();
        SparseArray sparseArray2 = new SparseArray();
        SparseLongArray sparseLongArray = new SparseLongArray();
        BatteryLife.MatchingData matchingData = new BatteryLife.MatchingData();
        for (BatteryDryData batteryDryData : this.mData.mBatteryDryOneDayDQ) {
            if (Double.compare(batteryDryData.batteryDry, 0.0d) > 0) {
                BatteryLife.Data data = (BatteryLife.Data) sparseArray.get(batteryDryData.powerMode, new BatteryLife.Data());
                data.powerMode = batteryDryData.powerMode;
                int i3 = (int) (batteryDryData.durationTime * (this.mTotalBatteryCap / batteryDryData.batteryDry));
                int[] iArr = data.life;
                int i4 = batteryDryData.type;
                iArr[i4] = i3;
                data.setCount(i4, i3);
                long[] jArr = data.duration;
                int i5 = batteryDryData.type;
                long j2 = batteryDryData.durationTime;
                jArr[i5] = j2;
                data.totalDuration[i5] = batteryDryData.totalDurationTime;
                double[] dArr = data.batteryDry;
                double d2 = batteryDryData.batteryDry;
                dArr[i5] = d2;
                SparseArray sparseArray3 = sparseArray2;
                SparseLongArray sparseLongArray2 = sparseLongArray;
                data.lifeD[i5] = (int) (j2 * (this.mDesignedBatteryCap / d2));
                BatteryLife.MatchingData matchingData2 = batteryDryData.matchingData;
                if (matchingData2.duration > 0) {
                    data.matchingDatas.add(matchingData2);
                    BatteryLife.MatchingData matchingData3 = batteryDryData.matchingData;
                    matchingData.type = matchingData3.type;
                    matchingData.duration = matchingData3.duration;
                    matchingData.consumption = matchingData3.consumption;
                    matchingData.startTime = matchingData3.startTime;
                    matchingData.endTime = matchingData3.endTime;
                    matchingData.startCap = matchingData3.startCap;
                    matchingData.endCap = matchingData3.endCap;
                    matchingData.startCapLevel = matchingData3.startCapLevel;
                    matchingData.endCapLevel = matchingData3.endCapLevel;
                    matchingData.startVoltage = matchingData3.startVoltage;
                    matchingData.endVoltage = matchingData3.endVoltage;
                }
                sparseArray.put(batteryDryData.powerMode, data);
                int i6 = batteryDryData.powerMode;
                sparseArray3.put(i6, Double.valueOf(((Double) sparseArray3.get(i6, Double.valueOf(0.0d))).doubleValue() + batteryDryData.batteryDry));
                int i7 = batteryDryData.powerMode;
                sparseLongArray = sparseLongArray2;
                sparseLongArray.put(i7, sparseLongArray.get(i7, 0L) + batteryDryData.durationTime);
                sparseArray2 = sparseArray3;
            }
        }
        SparseArray sparseArray4 = sparseArray2;
        for (int i8 = 0; i8 < sparseArray.size(); i8++) {
            int iKeyAt = sparseArray.keyAt(i8);
            BatteryLife.Data data2 = (BatteryLife.Data) sparseArray.valueAt(i8);
            if (Double.compare(((Double) sparseArray4.get(iKeyAt)).doubleValue(), 0.0d) > 0) {
                data2.life[4] = (int) (sparseLongArray.get(iKeyAt) * (this.mTotalBatteryCap / ((Double) sparseArray4.get(iKeyAt)).doubleValue()));
                sparseLongArray = sparseLongArray;
                data2.lifeD[4] = (int) (sparseLongArray.get(iKeyAt) * (this.mDesignedBatteryCap / ((Double) sparseArray4.get(iKeyAt)).doubleValue()));
                data2.setCount(4, data2.life[4]);
                batteryLife.dataList.add(data2);
            }
        }
        BatteryLife.Data data3 = new BatteryLife.Data();
        data3.powerMode = 100;
        int i9 = 0;
        while (true) {
            if (i9 >= sparseArray.size()) {
                break;
            }
            BatteryLife.Data data4 = (BatteryLife.Data) sparseArray.valueAt(i9);
            for (int i10 = 0; i10 < 5; i10++) {
                long[] jArr2 = data3.duration;
                jArr2[i10] = jArr2[i10] + data4.duration[i10];
                long[] jArr3 = data3.totalDuration;
                jArr3[i10] = jArr3[i10] + data4.totalDuration[i10];
                double[] dArr2 = data3.batteryDry;
                dArr2[i10] = dArr2[i10] + data4.batteryDry[i10];
            }
            i9++;
        }
        double d3 = 0.0d;
        int i11 = 0;
        long j3 = 0;
        for (i2 = 5; i11 < i2; i2 = 5) {
            long j4 = data3.duration[i11];
            BatteryLife.MatchingData matchingData4 = matchingData;
            double d4 = this.mTotalBatteryCap;
            double d5 = data3.batteryDry[i11];
            int[] iArr2 = data3.life;
            iArr2[i11] = iArr2[i11] + ((int) (j4 * (d4 / d5)));
            data3.lifeD[i11] = (int) (j4 * (this.mDesignedBatteryCap / d5));
            j3 += j4;
            d3 += d5;
            i11++;
            matchingData = matchingData4;
        }
        BatteryLife.MatchingData matchingData5 = matchingData;
        double d6 = d3;
        if (d6 != 0.0d) {
            double d7 = j3;
            data3.life[4] = (int) ((this.mTotalBatteryCap / d6) * d7);
            data3.lifeD[4] = (int) (d7 * (this.mDesignedBatteryCap / d6));
            if (matchingData5.duration > 0) {
                data3.matchingDatas.add(matchingData5);
            }
            batteryLife.dataList.add(data3);
        }
        return batteryLife;
    }

    private void initHistoricalBatteryLife() {
        ArrayDeque<BatteryLife> arrayDequeClone = this.mData.mEstimateBatteryLifeDQ.clone();
        while (arrayDequeClone.size() > 0) {
            this.mTotalHistrical.add(arrayDequeClone.pop());
        }
    }

    private void startNewData() {
        if (this.mStartTime == 0 || this.mLastBatteryCap < 0.1d) {
            long dozeTimeSec = BSHelper.getDozeTimeSec(this.mContext);
            this.mLastDozeTime = dozeTimeSec;
            this.mCurDozeTime = dozeTimeSec;
        } else {
            this.mLastDozeTime = this.mCurDozeTime;
        }
        this.mLastBatteryCap = BSHelper.getCurrentBatteryCap(100);
        this.mCallInIdle = this.mHelper.isIdle();
        this.mStartTime = System.currentTimeMillis();
        this.mLastBatteryCapLevel = BSHelper.getCurrentBatteryCapLevel();
        this.mLastBatteryVoltage = BSHelper.getCurrentBatteryVoltage();
        this.mTopAppsManager.startNewData();
    }

    private void subtractHistoricalBatteryLife(BatteryLife batteryLife) {
        if (batteryLife.dataList.size() == 0 || this.mTotalHistrical.dataList.size() == 0) {
            return;
        }
        this.mTotalHistrical.subtract(batteryLife);
    }

    private void updateFluctuatingState(BatteryLife.Data data, BatteryLife.Data data2) {
        for (int i2 = 0; i2 < 5; i2++) {
            int i3 = data2.life[i2];
            if (i3 != 0) {
                this.mFluctuatingState.fluctuatingStateComp[data.powerMode] = data.life[i2] / i3;
            }
        }
    }

    boolean accounting() {
        if (this.mOnBattery) {
            if (this.mOnScreenOn) {
                this.mTopAppsManager.addLastDataToMap(this.mCurrentFgUid);
            }
            this.mTopAppsManager.startNewData();
            addLastData();
            startNewData();
        }
        this.mTopAppsManager.sortDataToList();
        Log.d(TAG, " accounting mData = " + this.mData.toString());
        double currentBatteryCap = BSHelper.getCurrentBatteryCap(100);
        if (this.mLastBatteryCapUncharged < currentBatteryCap) {
            this.mLastBatteryCapUncharged = currentBatteryCap;
        }
        this.mTotalBatteryConsumption = 0.0d;
        this.mTotalOnBatteryDuaration = 0L;
        this.mTotalChargingDuration = 0L;
        this.mCapacityOnSaveBattery = 0;
        ArrayDeque<BatteryDryData> arrayDeque = this.mData.mBatteryDryOneDayDQ;
        if (arrayDeque == null || arrayDeque.size() <= 0) {
            Log.d(TAG, " cancel accounting, data is empty");
            this.mLastBatteryCapUncharged = currentBatteryCap;
            BatteryData batteryData = this.mData;
            batteryData.mTotalBatteryConsumption = 0.0d;
            batteryData.mTotalOnBatteryDuaration = 0L;
            batteryData.mTotalChargingDuration = 0L;
            batteryData.mCapacityOnSaveBattery = 0;
            return false;
        }
        calculateBatteryLife();
        this.mData.mBatteryDryOneDayDQ.clear();
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (this.mOnBattery) {
            BatteryData batteryData2 = this.mData;
            batteryData2.mTotalBatteryConsumption += this.mLastBatteryCapUncharged - currentBatteryCap;
            batteryData2.mTotalOnBatteryDuaration += jCurrentTimeMillis - this.mLastOnBatteryTime;
            this.mLastOnBatteryTime = jCurrentTimeMillis;
            this.mLastCharingTime = 0L;
        } else {
            this.mData.mTotalChargingDuration += jCurrentTimeMillis - this.mLastCharingTime;
            this.mLastOnBatteryTime = 0L;
            this.mLastCharingTime = jCurrentTimeMillis;
        }
        this.mLastBatteryCapUncharged = currentBatteryCap;
        BatteryData batteryData3 = this.mData;
        this.mTotalBatteryConsumption = batteryData3.mTotalBatteryConsumption;
        this.mTotalOnBatteryDuaration = batteryData3.mTotalOnBatteryDuaration;
        this.mTotalChargingDuration = batteryData3.mTotalChargingDuration;
        this.mCapacityOnSaveBattery = batteryData3.mCapacityOnSaveBattery;
        batteryData3.mTotalBatteryConsumption = 0.0d;
        batteryData3.mTotalOnBatteryDuaration = 0L;
        batteryData3.mTotalChargingDuration = 0L;
        batteryData3.mCapacityOnSaveBattery = 0;
        BLUtils.writeFile(getFilePath(), this.mData);
        return true;
    }

    public void addLastData() {
        int i2;
        boolean z2 = this.mCallInIdle;
        this.mCallInIdle = this.mHelper.isIdle();
        if (!BLUtils.isPowerModeSupport(this.mPowerMode)) {
            Log.d(TAG, " addLastData, Power mode(" + this.mPowerMode + ") is not Support.");
            return;
        }
        if (!this.mOnBattery) {
            Log.d(TAG, " addLastData, discard because charging.");
            return;
        }
        if (this.mStartTime == 0 || Double.compare(this.mLastBatteryCap, 0.0d) <= 0) {
            Log.d(TAG, " addLastData, discard because on first time.");
            return;
        }
        double currentBatteryCap = BSHelper.getCurrentBatteryCap(100);
        this.mCurBatteryCap = currentBatteryCap;
        if (this.mLastBatteryCap - currentBatteryCap < 0.0d) {
            this.mLastBatteryCap = currentBatteryCap;
            Log.d(TAG, " addLastData, discard because batteryDry abnormal. mCurBatteryCap = " + this.mCurBatteryCap + ", mLastBatteryCap = " + this.mLastBatteryCap);
            return;
        }
        if (!this.mOnScreenOn) {
            this.mCurDozeTime = BSHelper.getDozeTimeSec(this.mContext);
        }
        if (this.mOnScreenOn) {
            i2 = 0;
        } else if (z2) {
            i2 = this.mAtNight ? 3 : 1;
        } else {
            i2 = 2;
        }
        long jCurrentTimeMillis = System.currentTimeMillis();
        long j2 = (jCurrentTimeMillis - this.mStartTime) / 1000;
        if (j2 > 0 && j2 < 20) {
            Log.d(TAG, " addLastData, discard because the duration is too short. duration = " + j2);
            boolean z3 = false;
            for (BatteryDryData batteryDryData : this.mData.mBatteryDryOneDayDQ) {
                if (batteryDryData.powerMode == this.mPowerMode && batteryDryData.type == i2) {
                    batteryDryData.totalDurationTime += j2;
                    z3 = true;
                }
            }
            if (z3) {
                return;
            }
            BatteryDryData batteryDryData2 = new BatteryDryData();
            batteryDryData2.type = i2;
            batteryDryData2.powerMode = this.mPowerMode;
            batteryDryData2.totalDurationTime = j2;
            this.mData.mBatteryDryOneDayDQ.addLast(batteryDryData2);
            return;
        }
        double d2 = this.mLastBatteryCap;
        long j3 = jCurrentTimeMillis;
        double d3 = this.mCurBatteryCap;
        if (d2 - d3 <= 0.0d) {
            this.mLastBatteryCap = d3;
            Log.d(TAG, " addLastData, discard because batteryDry abnormal. mCurBatteryCap = " + this.mCurBatteryCap + ", mLastBatteryCap = " + this.mLastBatteryCap);
            return;
        }
        BLUtils.LOCAL_LOG.log("mLastBatteryCap=" + this.mLastBatteryCap + ", mCurBatteryCap=" + this.mCurBatteryCap + ", mCurrentBatteryLevel=" + this.mBatteryLevel + ", mScreenOn=" + this.mOnScreenOn + ", duration=" + j2 + BLUtils.msToHour(1000 * j2));
        for (BatteryDryData batteryDryData3 : this.mData.mBatteryDryOneDayDQ) {
            if (batteryDryData3.powerMode == this.mPowerMode && batteryDryData3.type == i2) {
                batteryDryData3.durationTime += j2;
                batteryDryData3.totalDurationTime += j2;
                batteryDryData3.batteryDry += this.mLastBatteryCap - this.mCurBatteryCap;
                batteryDryData3.dozeTime += this.mOnScreenOn ? 0L : this.mCurDozeTime - this.mLastDozeTime;
                batteryDryData3.dataTotalTimes++;
                if (i2 == 3 && BatteryLife.MatchingData.match(0, Long.valueOf(j2))) {
                    BatteryLife.MatchingData matchingData = batteryDryData3.matchingData;
                    double d4 = matchingData.consumption;
                    double d5 = this.mLastBatteryCap;
                    double d6 = this.mCurBatteryCap;
                    matchingData.consumption = d4 + (d5 - d6);
                    matchingData.duration += j2;
                    matchingData.type = 0;
                    matchingData.startTime = this.mStartTime;
                    matchingData.endTime = j3;
                    matchingData.startCap = d5;
                    matchingData.endCap = d6;
                    matchingData.startCapLevel = this.mLastBatteryCapLevel;
                    matchingData.endCapLevel = BSHelper.getCurrentBatteryCapLevel();
                    BatteryLife.MatchingData matchingData2 = batteryDryData3.matchingData;
                    matchingData2.startVoltage = this.mLastBatteryVoltage;
                    matchingData2.endVoltage = BSHelper.getCurrentBatteryVoltage();
                }
                if (i2 == 0 && BatteryLife.MatchingData.match(1, Long.valueOf(j2))) {
                    BatteryLife.MatchingData matchingData3 = batteryDryData3.matchingData;
                    matchingData3.consumption += this.mLastBatteryCap - this.mCurBatteryCap;
                    matchingData3.duration += j2;
                    matchingData3.type = 1;
                }
                BLUtils.writeFile(getFilePath(), this.mData);
            }
            j3 = j3;
        }
        long j4 = j3;
        BatteryDryData batteryDryData4 = new BatteryDryData();
        batteryDryData4.startTime = this.mStartTime;
        batteryDryData4.durationTime = j2;
        batteryDryData4.totalDurationTime = j2;
        batteryDryData4.batteryDry = this.mLastBatteryCap - this.mCurBatteryCap;
        batteryDryData4.dozeTime = this.mOnScreenOn ? 0L : this.mCurDozeTime - this.mLastDozeTime;
        batteryDryData4.type = i2;
        batteryDryData4.powerMode = this.mPowerMode;
        batteryDryData4.dataTotalTimes = 1;
        this.mData.mBatteryDryOneDayDQ.addLast(batteryDryData4);
        if (i2 == 3 && BatteryLife.MatchingData.match(0, Long.valueOf(j2))) {
            BatteryLife.MatchingData matchingData4 = batteryDryData4.matchingData;
            double d7 = matchingData4.consumption;
            double d8 = this.mLastBatteryCap;
            double d9 = this.mCurBatteryCap;
            matchingData4.consumption = d7 + (d8 - d9);
            matchingData4.duration += j2;
            matchingData4.type = 0;
            matchingData4.startTime = this.mStartTime;
            matchingData4.endTime = j4;
            matchingData4.startCap = d8;
            matchingData4.endCap = d9;
            matchingData4.startCapLevel = this.mLastBatteryCapLevel;
            matchingData4.endCapLevel = BSHelper.getCurrentBatteryCapLevel();
            BatteryLife.MatchingData matchingData5 = batteryDryData4.matchingData;
            matchingData5.startVoltage = this.mLastBatteryVoltage;
            matchingData5.endVoltage = BSHelper.getCurrentBatteryVoltage();
        }
        if (i2 == 0 && BatteryLife.MatchingData.match(1, Long.valueOf(j2))) {
            BatteryLife.MatchingData matchingData6 = batteryDryData4.matchingData;
            matchingData6.consumption += this.mLastBatteryCap - this.mCurBatteryCap;
            matchingData6.duration += j2;
            matchingData6.type = 1;
        }
        BLUtils.writeFile(getFilePath(), this.mData);
    }

    public String dump() {
        StringBuilder sb = new StringBuilder();
        sb.append("battery_capacity = " + BSHelper.getDesignedBatteryCapacity(this.mContext) + "mAh\n");
        double totalBatteryCapacity = BSHelper.getTotalBatteryCapacity(this.mContext);
        sb.append("Current battery_charge_full = " + totalBatteryCapacity + "mAh\n");
        sb.append("Current battery_charge_counter = " + BSHelper.getCurrentBatteryCap() + "mAh\n");
        sb.append("Current battery_voltage = " + BSHelper.getCurrentBatteryVoltage() + ShellUtils.COMMAND_LINE_END);
        sb.append("battery_on = " + this.mOnBattery + ShellUtils.COMMAND_LINE_END);
        sb.append("PowerMode = " + BLUtils.getPowerModeNameEng(this.mPowerMode) + ShellUtils.COMMAND_LINE_END);
        sb.append("atNight = " + this.mAtNight + ShellUtils.COMMAND_LINE_END);
        sb.append("capacity on save_battery first = " + this.mData.mCapacityOnSaveBattery + "\n\n");
        if (isDevelopmentOrDebugVersion()) {
            sb.append("Today: \n");
            sb.append(this.mData.getTodayDumpOfConsumption(this.mContext));
            BatteryLife totalBatteryLife = getTotalBatteryLife();
            if (totalBatteryLife != null) {
                sb.append(totalBatteryLife.getBatteryLifeInfoPowerMode(this.mContext));
            } else {
                sb.append(String.format("    %-20s%-24s%-24s%-36s%-36s%n%n", "PowerMode", "ScreenOn_Duration", "ScreenOn_totalDuration", "ScreenOn_BatteryLife(ChargeDesign)", "ScreenOn_BatteryLife(ChargeFull)"));
            }
            sb.append(this.mTopAppsManager.dump() + ShellUtils.COMMAND_LINE_END);
            if (this.mTopAppsManager.getUsageList().size() <= 0) {
                sb.append("Yesterday:\n");
                sb.append("    there is no data.\n\n");
            } else if (this.mData.mEstimateBatteryLifeDQ.size() > 0) {
                BatteryLife last = this.mData.mEstimateBatteryLifeDQ.getLast();
                sb.append(last.getBatteryLifeInfoDate());
                sb.append(String.format("    %-20s%-24s%-24s%s%n", "Consumption", "ChargingDuration", "onBatteryDuration", "BatteryLife >= 24h"));
                sb.append(String.format("    %-20s", this.mTotalBatteryConsumption + "mAh"));
                sb.append(String.format("%-24s", BLUtils.msToHourNoParentheses(this.mTotalChargingDuration)));
                sb.append(String.format("%-24s", BLUtils.msToHourNoParentheses(this.mTotalOnBatteryDuaration)));
                if (this.mTotalBatteryConsumption <= totalBatteryCapacity) {
                    sb.append("TRUE\n");
                } else {
                    sb.append("FALSE\n");
                }
                sb.append(ShellUtils.COMMAND_LINE_END);
                sb.append(last.getBatteryLifeInfoPowerMode(this.mContext));
                sb.append(this.mTopAppsManager.getYesterdayTopAppsDump() + ShellUtils.COMMAND_LINE_END);
            } else {
                sb.append("    there is no data.\n\n");
            }
        } else {
            sb.append("not develop device!\n");
        }
        return sb.toString();
    }

    int getCapacityOnSaveBattery() {
        return this.mCapacityOnSaveBattery;
    }

    public BatteryData getData() {
        return this.mData;
    }

    String getFilePath() {
        return this.mContext.getCacheDir().getAbsolutePath() + "/battery_life_data";
    }

    BatteryLife.FluctuatingState getFluctuatingState() {
        return this.mFluctuatingState;
    }

    public BatteryLife getHistoricalAverage() {
        return this.mTotalHistrical.getAverage();
    }

    TimeInStateData getLastTimeInState() {
        return this.mLastTimeInStateData;
    }

    TopAppsManager getTopAppsManager() {
        return this.mTopAppsManager;
    }

    double getTotalBatteryCap() {
        return this.mTotalBatteryCap;
    }

    double getTotalBatteryConsumption() {
        return this.mTotalBatteryConsumption;
    }

    public long getTotalChargingDuration() {
        return this.mTotalChargingDuration;
    }

    public long getTotalOnBatteryDuaration() {
        return this.mTotalOnBatteryDuaration;
    }

    void handleException() {
        this.mTopAppsManager.addLastDataToMap(this.mCurrentFgUid);
        addLastData();
    }

    void intoNight(boolean z2) {
        if ((!this.mAtNight) ^ z2) {
            Log.d(TAG, " intoNight, status not changed.");
        } else {
            if (this.mOnScreenOn) {
                this.mAtNight = z2;
                return;
            }
            addLastData();
            this.mAtNight = z2;
            startNewData();
        }
    }

    public boolean isDevelopmentOrDebugVersion() {
        return SystemProperties.getBoolean("ro.mi.development", false);
    }

    public void onBatteryChanged(boolean z2, int i2) {
        this.mBatteryLevel = i2;
        if (z2 == this.mOnBattery) {
            return;
        }
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (z2) {
            this.mTopAppsManager.startNewData();
            this.mLastBatteryCapUncharged = BSHelper.getCurrentBatteryCap();
            long j2 = this.mLastCharingTime;
            if (j2 != 0) {
                this.mData.mTotalChargingDuration += jCurrentTimeMillis - j2;
            }
            this.mLastOnBatteryTime = jCurrentTimeMillis;
            startNewData();
        } else {
            if (this.mOnScreenOn) {
                this.mTopAppsManager.addLastDataToMap(this.mCurrentFgUid);
            }
            double d2 = this.mLastBatteryCapUncharged;
            if (d2 == -1.0d) {
                this.mLastBatteryCapUncharged = BSHelper.getCurrentBatteryCap();
            } else {
                this.mData.mTotalBatteryConsumption += d2 - BSHelper.getCurrentBatteryCap();
            }
            long j3 = this.mLastOnBatteryTime;
            if (j3 != 0) {
                this.mData.mTotalOnBatteryDuaration += jCurrentTimeMillis - j3;
            }
            this.mLastCharingTime = jCurrentTimeMillis;
            addLastData();
        }
        this.mOnBattery = z2;
    }

    void onForegroundChanged(int i2) {
        if (!this.mOnScreenOn || !this.mOnBattery) {
            this.mCurrentFgUid = i2;
            return;
        }
        int i3 = this.mCurrentFgUid;
        if (i3 != -1) {
            this.mTopAppsManager.addLastDataToMap(i3);
        }
        this.mCurrentFgUid = i2;
        this.mTopAppsManager.startNewData();
    }

    void onPowerModeChanged(int i2) {
        int i3 = this.mPowerMode;
        if (i2 == i3) {
            return;
        }
        if (BLUtils.isPowerModeSupport(i3)) {
            addLastData();
        }
        if (BLUtils.isPowerModeSupport(i2)) {
            startNewData();
            if (i2 == 3) {
                BatteryData batteryData = this.mData;
                if (batteryData.mCapacityOnSaveBattery == 0) {
                    batteryData.mCapacityOnSaveBattery = BSHelper.getCurrentBatteryCapLevel();
                }
            }
        }
        this.mPowerMode = i2;
    }

    public void onScreenChanged(boolean z2) {
        if (z2 == this.mOnScreenOn) {
            return;
        }
        if (z2) {
            this.mTopAppsManager.startNewData();
        } else if (this.mOnBattery) {
            this.mTopAppsManager.addLastDataToMap(this.mCurrentFgUid);
        } else {
            Log.d(TAG, "onScreenChanged, discard because of charging");
        }
        addLastData();
        this.mOnScreenOn = z2;
        startNewData();
    }

    void resetTimeInState() {
        this.mLastTimeInStateData.time = System.currentTimeMillis();
        this.mLastTimeInStateData.mTimeInStateMap = CpuStatus.getTimeInStateMap();
    }
}
