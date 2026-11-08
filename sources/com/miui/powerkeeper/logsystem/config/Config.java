package com.miui.powerkeeper.logsystem.config;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
class Config {

    @SerializedName("fucSwitch")
    private boolean mEnabled = false;

    @SerializedName("threshold")
    private float mThreshold = 2.0f;

    @SerializedName("deviceList")
    private String mDeviceList = null;

    @SerializedName("allLogTypes")
    private int mLogType = 0;

    @SerializedName("minInterval")
    private long minIntervalMin = 360;

    @SerializedName("clearInterval")
    private long clearIntervalHour = 24;

    @SerializedName("sceneList")
    private ArrayList<Scene> mScenes = null;

    Config() {
    }

    public long getClearIntervalHour() {
        return this.clearIntervalHour;
    }

    String getDeviceList() {
        return this.mDeviceList;
    }

    int getLogType() {
        return this.mLogType;
    }

    long getMinIntervalMin() {
        return this.minIntervalMin;
    }

    ArrayList<Scene> getScenes() {
        return this.mScenes;
    }

    float getThreshold() {
        return this.mThreshold;
    }

    boolean isFucEnabled() {
        return this.mEnabled;
    }
}
