package com.xiaomi.push.clientreport;

/* JADX INFO: loaded from: classes.dex */
public class PowerStatsModel {
    private int androidVc;
    private long endTime;
    private int offDownCount;
    private long offDuration;
    private int offPingCount;
    private int offPongCount;
    private int offUpCount;
    private int onDownCount;
    private long onDuration;
    private int onPingCount;
    private int onPongCount;
    private int onUpCount;
    private long startTime;
    private int xmsfVc;

    public int getAndroidVc() {
        return this.androidVc;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public int getOffDownCount() {
        return this.offDownCount;
    }

    public long getOffDuration() {
        return this.offDuration;
    }

    public int getOffPingCount() {
        return this.offPingCount;
    }

    public int getOffPongCount() {
        return this.offPongCount;
    }

    public int getOffUpCount() {
        return this.offUpCount;
    }

    public int getOnDownCount() {
        return this.onDownCount;
    }

    public long getOnDuration() {
        return this.onDuration;
    }

    public int getOnPingCount() {
        return this.onPingCount;
    }

    public int getOnPongCount() {
        return this.onPongCount;
    }

    public int getOnUpCount() {
        return this.onUpCount;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public int getXmsfVc() {
        return this.xmsfVc;
    }

    public void setAndroidVc(int i2) {
        this.androidVc = i2;
    }

    public void setEndTime(long j2) {
        this.endTime = j2;
    }

    public void setOffDownCount(int i2) {
        this.offDownCount = i2;
    }

    public void setOffDuration(long j2) {
        this.offDuration = j2;
    }

    public void setOffPingCount(int i2) {
        this.offPingCount = i2;
    }

    public void setOffPongCount(int i2) {
        this.offPongCount = i2;
    }

    public void setOffUpCount(int i2) {
        this.offUpCount = i2;
    }

    public void setOnDownCount(int i2) {
        this.onDownCount = i2;
    }

    public void setOnDuration(long j2) {
        this.onDuration = j2;
    }

    public void setOnPingCount(int i2) {
        this.onPingCount = i2;
    }

    public void setOnPongCount(int i2) {
        this.onPongCount = i2;
    }

    public void setOnUpCount(int i2) {
        this.onUpCount = i2;
    }

    public void setStartTime(long j2) {
        this.startTime = j2;
    }

    public void setXmsfVc(int i2) {
        this.xmsfVc = i2;
    }

    public String toString() {
        return "PowerStatsModel{offUpCount=" + this.offUpCount + ", offDownCount=" + this.offDownCount + ", offPingCount=" + this.offPingCount + ", offPongCount=" + this.offPongCount + ", offDuration=" + this.offDuration + ", onUpCount=" + this.onUpCount + ", onDownCount=" + this.onDownCount + ", onPingCount=" + this.onPingCount + ", onPongCount=" + this.onPongCount + ", onDuration=" + this.onDuration + ", startTime=" + this.startTime + ", endTime=" + this.endTime + ", xmsfVc=" + this.xmsfVc + ", androidVc=" + this.androidVc + '}';
    }
}
