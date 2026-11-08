package com.miui.powerkeeper.thermalcollector.DTO;

import android.annotation.NonNull;
import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class EventCloudDTO {

    @SerializedName("work_status")
    private final String mStatus = "";

    @SerializedName("thermal_cloud_event")
    private final Set<String> mEventNames = Collections.emptySet();

    @SerializedName("parameters")
    private final Set<EventParameter> mParameters = Collections.emptySet();

    public static class EventParameter {

        @SerializedName("collectors")
        private final Map<String, Set<String>> mCollectors;

        @SerializedName("event")
        private final String mEventName;

        @SerializedName("extra")
        private final String mExtraInfo;

        @SerializedName("rule")
        private final Set<String> mRule;

        @SerializedName("trigger")
        private final Map<String, String> mTrigger;

        public EventParameter(String str, Set<String> set, Map<String, Set<String>> map, Map<String, String> map2, String str2) {
            this.mEventName = str;
            this.mRule = set;
            this.mCollectors = map;
            this.mTrigger = map2;
            this.mExtraInfo = str2;
        }

        public Map<String, Set<String>> getCollectors() {
            return this.mCollectors;
        }

        public String getEventName() {
            return this.mEventName;
        }

        public String getExtraInfo() {
            return this.mExtraInfo;
        }

        public Set<String> getRule() {
            return this.mRule;
        }

        public Map<String, String> getTrigger() {
            return this.mTrigger;
        }

        @NonNull
        public String toString() {
            return "EventParameter{mEventName='" + this.mEventName + "', mRule='" + this.mRule + "', mCollectors='" + this.mCollectors + "'}";
        }
    }

    public Set<String> getEventNames() {
        return this.mEventNames;
    }

    public EventParameter getParameterByEvent(String str) {
        for (EventParameter eventParameter : this.mParameters) {
            if (eventParameter.getEventName().equals(str)) {
                return eventParameter;
            }
        }
        return null;
    }

    public Set<EventParameter> getParameters() {
        return this.mParameters;
    }

    public String getStatus() {
        return this.mStatus;
    }

    @NonNull
    public String toString() {
        return "EventCloudDTO{mStatus='" + this.mStatus + "', mEventNames=" + this.mEventNames + ", mParameters=" + this.mParameters + '}';
    }
}
