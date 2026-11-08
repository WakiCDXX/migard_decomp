package com.miui.powerkeeper.thermalcollector.DTO;

import android.annotation.NonNull;
import android.util.ArrayMap;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.miui.powerkeeper.controller.FrozenAppController;
import com.miui.powerkeeper.feedbackcontrol.ThermalManager;
import com.miui.powerkeeper.millet.MilletHandler;
import com.miui.powerkeeper.thermalcollector.CollectorManager;
import com.miui.powerkeeper.thermalcollector.source.CollectorConfig;
import java.util.Map;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class ThermalScenarioDTO {

    @SerializedName("audio")
    private String mAudioState;

    @SerializedName("camera")
    private String mCameraState;

    @SerializedName("casting")
    private String mCastingState;

    @SerializedName("download")
    private String mDownloadState;

    @SerializedName("float_counts")
    private String mFloatCountsJson;

    @SerializedName("float_window")
    private String mFloatWindowState;

    @SerializedName("foreground_counts")
    private String mForegroundCountsJson;

    @SerializedName("gps")
    private String mGPSState;

    @SerializedName("plug")
    private String mPlugState;

    @SerializedName("screen")
    private String mScreenState;

    @SerializedName("split")
    private String mSplit;

    @SerializedName("thermal_group")
    private String mThermalGroupState;
    private final transient Map<String, Integer> mForegroundCounts = new ArrayMap();
    private final transient Map<String, Integer> mFloatCounts = new ArrayMap();

    @SerializedName("temp_bottom")
    private int mTempBottom = 0;

    @SerializedName("temp25")
    private int mTemp25 = 0;

    @SerializedName("temp26")
    private int mTemp26 = 0;

    @SerializedName("temp27")
    private int mTemp27 = 0;

    @SerializedName("temp28")
    private int mTemp28 = 0;

    @SerializedName("temp29")
    private int mTemp29 = 0;

    @SerializedName("temp30")
    private int mTemp30 = 0;

    @SerializedName("temp31")
    private int mTemp31 = 0;

    @SerializedName("temp32")
    private int mTemp32 = 0;

    @SerializedName("temp33")
    private int mTemp33 = 0;

    @SerializedName("temp34")
    private int mTemp34 = 0;

    @SerializedName("temp35")
    private int mTemp35 = 0;

    @SerializedName("temp36")
    private int mTemp36 = 0;

    @SerializedName("temp37")
    private int mTemp37 = 0;

    @SerializedName("temp38")
    private int mTemp38 = 0;

    @SerializedName("temp39")
    private int mTemp39 = 0;

    @SerializedName("temp40")
    private int mTemp40 = 0;

    @SerializedName("temp41")
    private int mTemp41 = 0;

    @SerializedName("temp42")
    private int mTemp42 = 0;

    @SerializedName("temp43")
    private int mTemp43 = 0;

    @SerializedName("temp44")
    private int mTemp44 = 0;

    @SerializedName("temp45")
    private int mTemp45 = 0;

    @SerializedName("temp46")
    private int mTemp46 = 0;

    @SerializedName("temp47")
    private int mTemp47 = 0;

    @SerializedName("temp48")
    private int mTemp48 = 0;

    @SerializedName("temp49")
    private int mTemp49 = 0;

    @SerializedName("temp50")
    private int mTemp50 = 0;

    @SerializedName("temp51")
    private int mTemp51 = 0;

    @SerializedName("temp52")
    private int mTemp52 = 0;

    @SerializedName("temp53")
    private int mTemp53 = 0;

    @SerializedName("temp_top")
    private int mTempTop = 0;

    public static class ThermalScenarioKey {
        private String mAudioState;
        private String mCameraState;
        private String mCastingState;
        private String mDownloadState;
        private String mFloatWindowState;
        private String mGPSState;
        private String mPlugState;
        private String mScreenState;
        private String mSplit;
        private String mThermalGroupState;

        public ThermalScenarioKey() {
            this.mScreenState = "";
            this.mThermalGroupState = "";
            this.mFloatWindowState = "";
            this.mCameraState = "";
            this.mCastingState = "";
            this.mDownloadState = "";
            this.mGPSState = "";
            this.mAudioState = "";
            this.mSplit = "";
            this.mPlugState = "";
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            ThermalScenarioKey thermalScenarioKey = (ThermalScenarioKey) obj;
            return this.mScreenState.equals(thermalScenarioKey.mScreenState) && this.mThermalGroupState.equals(thermalScenarioKey.mThermalGroupState) && this.mFloatWindowState.equals(thermalScenarioKey.mFloatWindowState) && this.mCameraState.equals(thermalScenarioKey.mCameraState) && this.mCastingState.equals(thermalScenarioKey.mCastingState) && this.mDownloadState.equals(thermalScenarioKey.mDownloadState) && this.mGPSState.equals(thermalScenarioKey.mGPSState) && this.mAudioState.equals(thermalScenarioKey.mAudioState) && this.mSplit.equals(thermalScenarioKey.mSplit) && this.mPlugState.equals(thermalScenarioKey.mPlugState);
        }

        public int hashCode() {
            return Objects.hash(this.mScreenState, this.mThermalGroupState, this.mFloatWindowState, this.mCameraState, this.mCastingState, this.mDownloadState, this.mGPSState, this.mAudioState, this.mSplit, this.mPlugState);
        }

        public void setAudioState(String str) {
            this.mAudioState = str;
        }

        public void setCameraState(String str) {
            this.mCameraState = str;
        }

        public void setCastingState(String str) {
            this.mCastingState = str;
        }

        public void setDownloadState(String str) {
            this.mDownloadState = str;
        }

        public void setFloatWindowState(String str) {
            this.mFloatWindowState = str;
        }

        public void setGPSState(String str) {
            this.mGPSState = str;
        }

        public void setPlugState(int i2) {
            if (i2 == 0) {
                this.mPlugState = "OFF";
                return;
            }
            if (i2 == 1) {
                this.mPlugState = "AC";
            } else if (i2 == 2) {
                this.mPlugState = "USB";
            } else {
                if (i2 != 4) {
                    return;
                }
                this.mPlugState = "WIRELESS";
            }
        }

        public void setScreenState(String str) {
            this.mScreenState = str;
        }

        public void setSplit(String str) {
            this.mSplit = str;
        }

        public void setThermalGroupState(String str) {
            this.mThermalGroupState = str;
        }

        @NonNull
        /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
        public ThermalScenarioKey m2clone() {
            return new ThermalScenarioKey(this);
        }

        public ThermalScenarioKey(ThermalScenarioKey thermalScenarioKey) {
            this.mScreenState = "";
            this.mThermalGroupState = "";
            this.mFloatWindowState = "";
            this.mCameraState = "";
            this.mCastingState = "";
            this.mDownloadState = "";
            this.mGPSState = "";
            this.mAudioState = "";
            this.mSplit = "";
            this.mPlugState = "";
            this.mScreenState = thermalScenarioKey.mScreenState;
            this.mThermalGroupState = thermalScenarioKey.mThermalGroupState;
            this.mFloatWindowState = thermalScenarioKey.mFloatWindowState;
            this.mCameraState = thermalScenarioKey.mCameraState;
            this.mCastingState = thermalScenarioKey.mCastingState;
            this.mDownloadState = thermalScenarioKey.mDownloadState;
            this.mGPSState = thermalScenarioKey.mGPSState;
            this.mAudioState = thermalScenarioKey.mAudioState;
            this.mSplit = thermalScenarioKey.mSplit;
            this.mPlugState = thermalScenarioKey.mPlugState;
        }
    }

    public static class ThermalScenarioValue {
        private String mForegroundName = "null";
        private String mFloatWindowName = "null";
        private int mTemp = 0;

        public void setFloatWindowName(String str) {
            this.mFloatWindowName = str;
        }

        public void setForegroundName(String str) {
            this.mForegroundName = str;
        }

        public void setTemp(int i2) {
            this.mTemp = i2;
        }
    }

    public ThermalScenarioDTO(ThermalScenarioKey thermalScenarioKey) {
        this.mScreenState = thermalScenarioKey.mScreenState;
        this.mThermalGroupState = thermalScenarioKey.mThermalGroupState;
        this.mFloatWindowState = thermalScenarioKey.mFloatWindowState;
        this.mCameraState = thermalScenarioKey.mCameraState;
        this.mCastingState = thermalScenarioKey.mCastingState;
        this.mDownloadState = thermalScenarioKey.mDownloadState;
        this.mGPSState = thermalScenarioKey.mGPSState;
        this.mAudioState = thermalScenarioKey.mAudioState;
        this.mSplit = thermalScenarioKey.mSplit;
        this.mPlugState = thermalScenarioKey.mPlugState;
    }

    private void setFloatCounts(String str) {
        Log.d(CollectorManager.TAG, "ThermalScenarioValue setFloatCounts: " + str);
        Map<String, Integer> map = this.mFloatCounts;
        map.put(str, Integer.valueOf(map.getOrDefault(str, 0).intValue() + 1));
    }

    private void setForegroundCounts(String str) {
        Log.d(CollectorManager.TAG, "ThermalScenarioValue setForegroundCounts: " + str);
        Map<String, Integer> map = this.mForegroundCounts;
        map.put(str, Integer.valueOf(map.getOrDefault(str, 0).intValue() + 1));
    }

    private void setTemp(int i2) {
        Log.d(CollectorManager.TAG, "ThermalScenarioValue setTemp: " + i2);
        if (i2 < 25) {
            this.mTempBottom++;
        } else if (i2 > 53) {
            this.mTempTop++;
        }
        switch (i2) {
            case 25:
                this.mTemp25++;
                break;
            case 26:
                this.mTemp26++;
                break;
            case 27:
                this.mTemp27++;
                break;
            case 28:
                this.mTemp28++;
                break;
            case 29:
                this.mTemp29++;
                break;
            case 30:
                this.mTemp30++;
                break;
            case 31:
                this.mTemp31++;
                break;
            case 32:
                this.mTemp32++;
                break;
            case MilletHandler.MSG_USER_PRESENT /* 33 */:
                this.mTemp33++;
                break;
            case 34:
                this.mTemp34++;
                break;
            case 35:
                this.mTemp35++;
                break;
            case 36:
                this.mTemp36++;
                break;
            case 37:
                this.mTemp37++;
                break;
            case 38:
                this.mTemp38++;
                break;
            case 39:
                this.mTemp39++;
                break;
            case FrozenAppController.FREEZE_CONDITION_ALL /* 40 */:
                this.mTemp40++;
                break;
            case 41:
                this.mTemp41++;
                break;
            case 42:
                this.mTemp42++;
                break;
            case 43:
                this.mTemp43++;
                break;
            case 44:
                this.mTemp44++;
                break;
            case ThermalManager.DEFAULT_ALLOWED_SHOW_BENCHMARK_DIALOG_TEMP_THRESHHOLD /* 45 */:
                this.mTemp45++;
                break;
            case 46:
                this.mTemp46++;
                break;
            case ThermalManager.DEFAULT_BATTERY_THRESHOLD /* 47 */:
                this.mTemp47++;
                break;
            case 48:
                this.mTemp48++;
                break;
            case 49:
                this.mTemp49++;
                break;
            case 50:
                this.mTemp50++;
                break;
            case 51:
                this.mTemp51++;
                break;
            case ThermalManager.DEFAULT_XO_TEMP_THRESHOLD /* 52 */:
                this.mTemp52++;
                break;
            case 53:
                this.mTemp53++;
                break;
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ThermalScenarioDTO thermalScenarioDTO = (ThermalScenarioDTO) obj;
        return this.mScreenState.equals(thermalScenarioDTO.mScreenState) && this.mThermalGroupState.equals(thermalScenarioDTO.mThermalGroupState) && this.mFloatWindowState.equals(thermalScenarioDTO.mFloatWindowState) && this.mCameraState.equals(thermalScenarioDTO.mCameraState) && this.mCastingState.equals(thermalScenarioDTO.mCastingState) && this.mDownloadState.equals(thermalScenarioDTO.mDownloadState) && this.mGPSState.equals(thermalScenarioDTO.mGPSState) && this.mAudioState.equals(thermalScenarioDTO.mAudioState) && this.mPlugState.equals(thermalScenarioDTO.mPlugState);
    }

    public int hashCode() {
        return Objects.hash(this.mScreenState, this.mThermalGroupState, this.mFloatWindowState, this.mCameraState, this.mCastingState, this.mDownloadState, this.mGPSState, this.mAudioState, this.mPlugState);
    }

    public void setValue(ThermalScenarioValue thermalScenarioValue) {
        setForegroundCounts(thermalScenarioValue.mForegroundName);
        setFloatCounts(thermalScenarioValue.mFloatWindowName);
        setTemp(thermalScenarioValue.mTemp);
    }

    public void settlement() {
        Gson gson = CollectorConfig.sGson;
        this.mForegroundCountsJson = gson.toJson(this.mForegroundCounts);
        this.mFloatCountsJson = gson.toJson(this.mFloatCounts);
    }
}
