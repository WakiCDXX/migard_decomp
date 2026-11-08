package com.miui.powerkeeper.thermalcollector.DTO;

import android.annotation.NonNull;
import com.google.gson.annotations.SerializedName;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class GenAppsCloudDTO {

    @SerializedName("ambient_collectors")
    private Set<String> mAmbientNames;

    @SerializedName("end_collectors")
    private Set<String> mEndNames;

    @SerializedName("flash_collectors")
    private Set<String> mFlashNames;

    @SerializedName("interval")
    private long mInterval;

    @SerializedName("start_collectors")
    private Set<String> mStartNames;

    @SerializedName("temp_collectors")
    private Set<String> mTempNames;

    public Set<String> getAmbientNames() {
        return this.mAmbientNames;
    }

    public Set<String> getEndNames() {
        return this.mEndNames;
    }

    public Set<String> getFlashTempNames() {
        return this.mFlashNames;
    }

    public long getInterval() {
        return this.mInterval;
    }

    public Set<String> getStartNames() {
        return this.mStartNames;
    }

    public Set<String> getTempNames() {
        return this.mTempNames;
    }

    @NonNull
    public String toString() {
        return "GenappsCollectorDTO{mStartNames='" + this.mStartNames + "', mEndNames='" + this.mEndNames + "', mTempNames='" + this.mTempNames + "', mAmbientNames='" + this.mAmbientNames + "', mFlashNames='" + this.mFlashNames + "'}";
    }
}
