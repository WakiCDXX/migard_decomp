package com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body;

import android.content.Context;
import com.miui.powerkeeper.feedbackcontrol.ThermalInfoUtils;
import com.miui.powerkeeper.utils.Utils;

/* JADX INFO: loaded from: classes.dex */
public class BatteryInfo extends LogCollector {
    private final String TABLE;
    private final String TAG;

    public BatteryInfo(Context context) {
        super(context);
        this.TAG = "BatteryInfoCollector";
        this.TABLE = "Battery Info:";
        this.mLog.d("BatteryInfoCollector", "create");
    }

    @Override // com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.LogCollector
    public void collect() {
        if (this.mPublicSpace.isContinueCollect()) {
            this.mLog.d("BatteryInfoCollector", "collect");
            long currentCapacity = ThermalInfoUtils.getCurrentCapacity();
            int plugType = ThermalInfoUtils.getPlugType(this.mContext);
            int batteryCurrentNow = ThermalInfoUtils.getBatteryCurrentNow(this.mContext);
            int batteryVoltage = Utils.getBatteryVoltage(this.mContext);
            this.mCache.append("Battery Info:[Battery Capacity:" + currentCapacity + " Plug Type:" + plugType + " Battery Current:" + batteryCurrentNow + " Battery Voltage:" + batteryVoltage + "] ");
        }
    }

    @Override // com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.LogCollector
    public void logout() {
        this.mLog.d("BatteryInfoCollector", "logout");
    }
}
