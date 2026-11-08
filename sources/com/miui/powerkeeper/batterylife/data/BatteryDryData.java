package com.miui.powerkeeper.batterylife.data;

import com.miui.powerkeeper.batterylife.BLUtils;
import com.miui.powerkeeper.utils.ShellUtils;
import java.io.Serializable;

/* JADX INFO: loaded from: classes.dex */
public class BatteryDryData implements Serializable {
    public double batteryDry;
    public int dataTotalTimes;
    public long dozeTime;
    public long durationTime;
    public int powerMode;
    public long totalDurationTime;
    public int type;
    public long startTime = 0;
    public BatteryLife.MatchingData matchingData = new BatteryLife.MatchingData();

    public String toString(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(str + "batteryDry=" + this.batteryDry + "mAh\n");
        sb.append(str + "dozeTime=" + this.dozeTime + BLUtils.msToHour(this.dozeTime * 1000) + ShellUtils.COMMAND_LINE_END);
        sb.append(str + "durationTime=" + this.durationTime + BLUtils.msToHour(this.durationTime * 1000) + ShellUtils.COMMAND_LINE_END);
        sb.append(str + "totalDurationTime=" + this.totalDurationTime + BLUtils.msToHour(this.totalDurationTime * 1000) + ShellUtils.COMMAND_LINE_END);
        StringBuilder sb2 = new StringBuilder();
        sb2.append(str);
        sb2.append("startTime=");
        sb2.append(this.startTime);
        sb2.append(ShellUtils.COMMAND_LINE_END);
        sb.append(sb2.toString());
        sb.append(str + "type=" + this.type + ShellUtils.COMMAND_LINE_END);
        sb.append(str + "powerMode=" + this.powerMode + ShellUtils.COMMAND_LINE_END);
        sb.append(str + "dataTotalTimes=" + this.dataTotalTimes + ShellUtils.COMMAND_LINE_END);
        sb.append(str + "matchingData(t,d,c)=" + this.matchingData.type + ", " + this.matchingData.duration + BLUtils.msToHour(this.matchingData.duration * 1000) + ", " + this.matchingData.consumption + "mAh\n");
        return sb.toString();
    }

    public String toString() {
        return toString("");
    }
}
