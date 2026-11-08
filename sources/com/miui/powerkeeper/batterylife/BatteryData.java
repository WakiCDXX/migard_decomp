package com.miui.powerkeeper.batterylife;

import android.content.Context;
import com.miui.powerkeeper.batterylife.data.BatteryDryData;
import com.miui.powerkeeper.batterylife.data.BatteryLife;
import com.miui.powerkeeper.batterylife.systeminfo.BSHelper;
import com.miui.powerkeeper.utils.ShellUtils;
import java.io.Serializable;
import java.util.ArrayDeque;

/* JADX INFO: loaded from: classes.dex */
public class BatteryData implements Serializable {
    private static final long serialVersionUID = 4359733211352400087L;
    public ArrayDeque<BatteryLife> mEstimateBatteryLifeDQ = new ArrayDeque<>();
    public transient ArrayDeque<BatteryDryData> mBatteryDryOneDayDQ = new ArrayDeque<>();
    public ArrayDeque<BatteryLife.FluctuatingState> mFLuctuatingStateList = new ArrayDeque<>();
    transient double mTotalBatteryConsumption = 0.0d;
    transient long mTotalChargingDuration = 0;
    transient long mTotalOnBatteryDuaration = 0;
    transient int mCapacityOnSaveBattery = 0;

    public String getTodayDumpOfConsumption(Context context) {
        StringBuilder sb = new StringBuilder();
        if (this.mBatteryDryOneDayDQ != null) {
            sb.append(String.format("    %-20s%-24s%-24s%s%n", "Consumption", "ChargingDuration", "onBatteryDuration", "BatteryLife >= 24h"));
            sb.append(String.format("    %-20s", this.mTotalBatteryConsumption + "mAh"));
            sb.append(String.format("%-24s", BLUtils.msToHourNoParentheses(this.mTotalChargingDuration)));
            sb.append(String.format("%-24s", BLUtils.msToHourNoParentheses(this.mTotalOnBatteryDuaration)));
            if (this.mTotalBatteryConsumption <= BSHelper.getTotalBatteryCapacity(context)) {
                sb.append("TRUE\n");
            } else {
                sb.append("FALSE\n");
            }
            sb.append(ShellUtils.COMMAND_LINE_END);
        }
        return sb.toString();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        ArrayDeque<BatteryDryData> arrayDeque = this.mBatteryDryOneDayDQ;
        int i2 = 0;
        if (arrayDeque != null) {
            sb.append("mBatteryDryOneDayDQ:\n");
            int i3 = 0;
            for (BatteryDryData batteryDryData : arrayDeque) {
                sb.append("  mBatteryDryOneDayDQ " + i3 + ":\n");
                sb.append(batteryDryData.toString("    "));
                i3++;
            }
        }
        sb.append("mEstimateBatteryLifeDQ:\n");
        for (BatteryLife batteryLife : this.mEstimateBatteryLifeDQ) {
            sb.append("  mEstimateBatteryLifeDQ " + i2 + ":\n");
            sb.append(batteryLife.toString("    "));
            i2++;
        }
        sb.append("mTotalBatteryConsumption:" + this.mTotalBatteryConsumption + "mAh\n");
        sb.append("mTotalChargingDuration:" + this.mTotalChargingDuration + BLUtils.msToHour(this.mTotalChargingDuration) + ShellUtils.COMMAND_LINE_END);
        sb.append("mTotalOnBatteryDuaration:" + this.mTotalOnBatteryDuaration + BLUtils.msToHour(this.mTotalOnBatteryDuaration) + ShellUtils.COMMAND_LINE_END);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("mCapacityOnSaveBattery:");
        sb2.append(this.mCapacityOnSaveBattery);
        sb2.append(ShellUtils.COMMAND_LINE_END);
        sb.append(sb2.toString());
        return sb.toString();
    }
}
