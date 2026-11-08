package com.xiaomi.push.clientreport;

/* JADX INFO: loaded from: classes.dex */
public class DisconnectStatsModel {
    private int androidVc;
    private int connectedNetType;
    private int count;
    private long disconnectionTime;
    private long duration;
    private String host;
    private int netState;
    private int netType;
    private long pingInterval;
    private int reason;
    private long reconnectionTime;
    private String wifiDigest;
    private int xmsfVc;

    public int getAndroidVc() {
        return this.androidVc;
    }

    public int getConnectedNetType() {
        return this.connectedNetType;
    }

    public int getCount() {
        return this.count;
    }

    public long getDisconnectionTime() {
        return this.disconnectionTime;
    }

    public long getDuration() {
        return this.duration;
    }

    public String getHost() {
        return this.host;
    }

    public int getNetState() {
        return this.netState;
    }

    public int getNetType() {
        return this.netType;
    }

    public long getPingInterval() {
        return this.pingInterval;
    }

    public int getReason() {
        return this.reason;
    }

    public long getReconnectionTime() {
        return this.reconnectionTime;
    }

    public String getWifiDigest() {
        return this.wifiDigest;
    }

    public int getXmsfVc() {
        return this.xmsfVc;
    }

    public void setAndroidVc(int i2) {
        this.androidVc = i2;
    }

    public void setConnectedNetType(int i2) {
        this.connectedNetType = i2;
    }

    public void setCount(int i2) {
        this.count = i2;
    }

    public void setDisconnectionTime(long j2) {
        this.disconnectionTime = j2;
    }

    public void setDuration(long j2) {
        this.duration = j2;
    }

    public void setHost(String str) {
        this.host = str;
    }

    public void setNetState(int i2) {
        this.netState = i2;
    }

    public void setNetType(int i2) {
        this.netType = i2;
    }

    public void setPingInterval(long j2) {
        this.pingInterval = j2;
    }

    public void setReason(int i2) {
        this.reason = i2;
    }

    public void setReconnectionTime(long j2) {
        this.reconnectionTime = j2;
    }

    public void setWifiDigest(String str) {
        this.wifiDigest = str;
    }

    public void setXmsfVc(int i2) {
        this.xmsfVc = i2;
    }

    public String toString() {
        return "DisconnectStatsModel{count=" + this.count + ", host='" + this.host + "', netState=" + this.netState + ", reason=" + this.reason + ", pingInterval=" + this.pingInterval + ", netType=" + this.netType + ", wifiDigest='" + this.wifiDigest + "', connectedNetType=" + this.connectedNetType + ", duration=" + this.duration + ", disconnectionTime=" + this.disconnectionTime + ", reconnectionTime=" + this.reconnectionTime + ", xmsfVc=" + this.xmsfVc + ", androidVc=" + this.androidVc + '}';
    }
}
