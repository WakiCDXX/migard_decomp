package com.miui.powerkeeper.batterylife.data;

import com.miui.powerkeeper.utils.ShellUtils;
import java.io.Serializable;

/* JADX INFO: loaded from: classes.dex */
public class MyBatterySipper implements Comparable<MyBatterySipper>, Serializable {
    private double deltaPowerMah;
    private long foregroundTime;
    private long totalForegroundTime;
    private double totalPower;
    private int uid;

    public double getDeltaPowerMah() {
        return this.deltaPowerMah;
    }

    public long getForegroundTime() {
        return this.foregroundTime;
    }

    public long getTotalForegroundTime() {
        return this.totalForegroundTime;
    }

    public double getTotalPower() {
        return this.totalPower;
    }

    public int getUid() {
        return this.uid;
    }

    public void setDeltaPowerMah(double d2) {
        this.deltaPowerMah = d2;
    }

    public void setForegroundTime(long j2) {
        this.foregroundTime = j2;
    }

    public void setTotalForegroundTime(long j2) {
        this.totalForegroundTime = j2;
    }

    public void setTotalPower(double d2) {
        this.totalPower = d2;
    }

    public void setUid(int i2) {
        this.uid = i2;
    }

    public String toString(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(str + "deltaPowerMah=" + this.deltaPowerMah + ShellUtils.COMMAND_LINE_END);
        sb.append(str + "uid=" + this.uid + ShellUtils.COMMAND_LINE_END);
        sb.append(str + "foregroundTime=" + this.foregroundTime + ShellUtils.COMMAND_LINE_END);
        sb.append(str + "totalPower=" + this.totalPower + ShellUtils.COMMAND_LINE_END);
        return sb.toString();
    }

    @Override // java.lang.Comparable
    public int compareTo(MyBatterySipper myBatterySipper) {
        return Long.compare(myBatterySipper.foregroundTime, this.foregroundTime);
    }

    public String toString() {
        return toString("");
    }
}
