package com.miui.powerkeeper.provider.ThermalDB;

import com.google.gson.annotations.SerializedName;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.control.MessageType;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class ThermalDurationDTO {

    @SerializedName("date")
    private long mDate;

    @SerializedName("temp_duration")
    private String mDuration;

    @SerializedName("temp_scene")
    private String mScene;

    @SerializedName("system_version")
    private float mVersion;

    public ThermalDurationDTO(long j2, Map<String, Map<Integer, Long>> map, float f2) {
        this.mDate = j2;
        this.mDuration = map.get(MessageType.ENTIRETY).toString();
        map.remove(MessageType.ENTIRETY);
        this.mScene = map.toString();
        this.mVersion = f2;
    }
}
