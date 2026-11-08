package com.miui.powerkeeper.thermal.setting;

import android.annotation.NonNull;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class SettingDTO {

    @SerializedName("listeners")
    private final List<String> mListeners = new ArrayList();

    @SerializedName("scenarios")
    private final List<ScenarioDTO> mScenarios = new ArrayList();

    @SerializedName("processors")
    private final List<ProcessorDTO> mProcessors = new ArrayList();

    public static final class ProcessorDTO {

        @SerializedName("groups")
        private final Map<String, Set<Integer>> mGroups;

        @SerializedName("processor_name")
        private final String mName;

        public ProcessorDTO(String str, Map<String, Set<Integer>> map) {
            this.mName = str;
            this.mGroups = map;
        }

        public Map<String, Set<Integer>> getGroups() {
            return this.mGroups;
        }

        public String getName() {
            return this.mName;
        }

        @NonNull
        public String toString() {
            return "ProcessorDTO{mName='" + this.mName + "', mGroups=" + this.mGroups + '}';
        }
    }

    public static final class ScenarioDTO {

        @SerializedName("define")
        private final List<Integer> mDefine;

        @SerializedName("id")
        private final int mID;

        @SerializedName("scenario_name")
        private final String mName;

        public ScenarioDTO(String str, int i2, List<Integer> list) {
            this.mName = str;
            this.mID = i2;
            this.mDefine = list;
        }

        public List<Integer> getDefine() {
            return this.mDefine;
        }

        public int getID() {
            return this.mID;
        }

        public String getName() {
            return this.mName;
        }

        @NonNull
        public String toString() {
            return "ScenarioDTO{mName='" + this.mName + "', mID=" + this.mID + ", mDefine=" + this.mDefine + '}';
        }
    }

    public List<String> getListeners() {
        return this.mListeners;
    }

    public List<ProcessorDTO> getProcessors() {
        return this.mProcessors;
    }

    public List<ScenarioDTO> getScenarios() {
        return this.mScenarios;
    }

    public void setsListener(String str) {
        this.mListeners.add(str);
    }

    public void setsProcessor(ProcessorDTO processorDTO) {
        this.mProcessors.add(processorDTO);
    }

    public void setsScenario(ScenarioDTO scenarioDTO) {
        this.mScenarios.add(scenarioDTO);
    }

    @NonNull
    public String toString() {
        return "SettingDTO{mListeners=" + this.mListeners + ", mScenarios=" + this.mScenarios + ", mProcessors=" + this.mProcessors + '}';
    }
}
