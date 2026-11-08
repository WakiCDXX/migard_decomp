package com.miui.powerkeeper.thermalcollector.DTO;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class GenAppsReportDTO {

    @SerializedName("end")
    private String mEnd;

    @SerializedName("process_data")
    private List<String> mProcess;

    @SerializedName("start")
    private String mStart;

    public GenAppsReportDTO(List<String> list) {
        this.mProcess = list;
    }

    public void clear() {
        this.mStart = "";
        this.mEnd = "";
        this.mProcess.clear();
    }

    public String getEnd() {
        return this.mEnd;
    }

    public List<String> getProcess() {
        return this.mProcess;
    }

    public String getStart() {
        return this.mStart;
    }

    public void setEnd(String str) {
        this.mEnd = str;
    }

    public void setProcess(List<String> list) {
        this.mProcess = list;
    }

    public void setStart(String str) {
        this.mStart = str;
    }

    public String toString() {
        return "AppsDTO{mStart='" + this.mStart + "', mProcess=" + this.mProcess + ", mEnd='" + this.mEnd + "'}";
    }
}
