package com.miui.powerkeeper.batterylife.data;

/* JADX INFO: loaded from: classes.dex */
public class JobTrack {
    private int jobId;
    private int lastStatus;
    private boolean onCharge;
    private long startTime;
    private int uid;
    private long lastUpdateTime = 0;
    private long fgTime = 0;
    private long bgTime = 0;

    public JobTrack(int i2, int i3, int i4, long j2, boolean z2) {
        this.jobId = i2;
        this.uid = i3;
        this.lastStatus = i4;
        this.startTime = j2;
        this.onCharge = z2;
    }

    public long getBgTime() {
        return this.bgTime;
    }

    public long getFgTime() {
        return this.fgTime;
    }

    public int getJobId() {
        return this.jobId;
    }

    public int getLastStatus() {
        return this.lastStatus;
    }

    public long getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    public boolean getOnCharge() {
        return this.onCharge;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public int getUid() {
        return this.uid;
    }

    public void setBgTime(long j2) {
        this.bgTime = j2;
    }

    public void setFgTime(long j2) {
        this.fgTime = j2;
    }

    public void setJobId(int i2) {
        this.jobId = i2;
    }

    public void setLastStatus(int i2) {
        this.lastStatus = i2;
    }

    public void setLastUpdateTime(long j2) {
        this.lastUpdateTime = j2;
    }

    public void setStartTime(long j2) {
        this.startTime = j2;
    }

    public void setUid(int i2) {
        this.uid = i2;
    }

    public String toString() {
        return "JobTrack{jobId=" + this.jobId + ", uid=" + this.uid + ", lastStatus=" + this.lastStatus + ", startTime=" + this.startTime + ", lastUpdateTime=" + this.lastUpdateTime + ", fgTime=" + this.fgTime + ", bgTime=" + this.bgTime + ", onCharge=" + this.onCharge + '}';
    }
}
