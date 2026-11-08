package com.miui.powerkeeper.logsystem.policy;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import com.miui.powerkeeper.batterylife.systeminfo.BSHelper;
import com.miui.powerkeeper.logsystem.config.ConfigManager;
import com.miui.powerkeeper.logsystem.config.Scene;

/* JADX INFO: loaded from: classes.dex */
public class PowerCheckPolicy implements IWork {
    private static final String TAG = "LS-PowerCheckPolicy";
    private double mAvgCurrent = 0.0d;
    private Context mContext;
    private double mLastPowerCapacity;
    private long mLastPowerLossTime;
    private PowerScenePolicy mScenePolicy;

    public PowerCheckPolicy(Context context) {
        this.mContext = context;
        this.mScenePolicy = new PowerScenePolicy(context);
    }

    private double calcActualPower() {
        double currentBatteryCap = BSHelper.getCurrentBatteryCap();
        Log.d(TAG, "calcActualPower curPowerCapacity = " + currentBatteryCap + ", mLastPowerCapacity = " + this.mLastPowerCapacity + ", Avg Power = " + ((this.mLastPowerCapacity - currentBatteryCap) / ((SystemClock.elapsedRealtime() - this.mLastPowerLossTime) / 3600000)));
        return this.mLastPowerCapacity - currentBatteryCap;
    }

    private double calcEstimatedPower(Scene scene) {
        return (((double) scene.getPower()) * (SystemClock.elapsedRealtime() - this.mLastPowerLossTime)) / 3600000.0d;
    }

    @Override // com.miui.powerkeeper.logsystem.policy.IWork
    public void destroy() {
        this.mScenePolicy.destroy();
    }

    public double getAvgCurrent() {
        return this.mAvgCurrent;
    }

    public Scene getCurrentScene() {
        return this.mScenePolicy.getCurrentScene();
    }

    public boolean isPowerAbnormal(Scene scene) {
        if (scene == null) {
            return false;
        }
        double dCalcActualPower = calcActualPower();
        double dCalcEstimatedPower = calcEstimatedPower(scene);
        this.mAvgCurrent = (3600000.0d * dCalcActualPower) / (SystemClock.elapsedRealtime() - this.mLastPowerLossTime);
        Log.d(TAG, "checkPower actualPower = " + dCalcActualPower + ", estimatedPower1 = " + dCalcEstimatedPower + ", threshold = " + ConfigManager.getInstance(this.mContext).getThreshold() + ", mAvgCurrent = " + this.mAvgCurrent);
        return dCalcActualPower > dCalcEstimatedPower * ((double) ConfigManager.getInstance(this.mContext).getThreshold());
    }

    @Override // com.miui.powerkeeper.logsystem.policy.IWork
    public void startWorking() {
        Log.d(TAG, "start working!");
        this.mLastPowerCapacity = BSHelper.getCurrentBatteryCap();
        this.mLastPowerLossTime = SystemClock.elapsedRealtime();
        this.mScenePolicy.startWorking();
    }

    @Override // com.miui.powerkeeper.logsystem.policy.IWork
    public void stopWorking() {
        Log.d(TAG, "stop working!");
        this.mLastPowerCapacity = -1.0d;
        this.mLastPowerLossTime = -1L;
        this.mScenePolicy.stopWorking();
    }
}
