package com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body;

import android.content.Context;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.feedbackcontrol.ThermalManager;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class TempInfo extends LogCollector {
    private final String TABLE;
    private final String TAG;
    private PowerKeeperInterface.m mBatteryChangedListener;
    private int mBatteryTemp;
    private int mXoThermalTemperature;

    public TempInfo(Context context) {
        super(context);
        this.TAG = "TempInfoCollector";
        this.TABLE = "Temp Info:";
        this.mBatteryTemp = 0;
        this.mXoThermalTemperature = 0;
        PowerKeeperInterface.m mVar = new PowerKeeperInterface.m() { // from class: com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.TempInfo.1
            @Override // com.miui.powerkeeper.PowerKeeperInterface.m
            public void onBatteryChanged(int i2, int i3, int i4, int i5, int i6, int i7) {
                TempInfo.this.mBatteryTemp = i5;
                TempInfo.this.mXoThermalTemperature = ThermalManager.ThermalTempInfoHelper.getXoThermalTemp();
            }
        };
        this.mBatteryChangedListener = mVar;
        PowerKeeperManager.getInstance().registerBatteryChangedListener(new PowerKeeperInterface.d(null, mVar));
        this.mLog.d("TempInfoCollector", "create");
    }

    private boolean isAbnormal() {
        Map<String, Integer> pkgThreshold = this.mPublicSpace.getPkgThreshold();
        String currentForeground = this.mPublicSpace.getCurrentForeground();
        return pkgThreshold.containsKey(currentForeground) && this.mXoThermalTemperature * 1000 >= pkgThreshold.get(currentForeground).intValue();
    }

    @Override // com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.LogCollector
    public void collect() {
        if (this.mPublicSpace.isContinueCollect()) {
            this.mLog.d("TempInfoCollector", "collect");
            boolean zIsAbnormal = isAbnormal();
            this.mCache.append("Temp Info:[Battery Temp:" + this.mBatteryTemp + " Xo Temp:" + this.mXoThermalTemperature + " Abnormal:" + zIsAbnormal + "]");
        }
    }

    @Override // com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.LogCollector
    public void logout() {
        this.mLog.d("TempInfoCollector", "logout");
    }
}
