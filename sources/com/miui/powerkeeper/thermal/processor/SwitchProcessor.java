package com.miui.powerkeeper.thermal.processor;

import android.annotation.NonNull;
import android.os.Build;
import android.util.Slog;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.cloudcontrol.AutoRegister;
import com.miui.powerkeeper.cloudcontrol.CloudFunctionConfig;
import com.miui.powerkeeper.feedbackcontrol.ThermalManager;
import com.miui.powerkeeper.provider.SimpleSettingKeys;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.thermal.resource.DBObserver;
import com.miui.powerkeeper.thermal.resource.ScenarioConfig;
import com.miui.powerkeeper.thermalcollector.CollectorManager;
import com.miui.powerkeeper.thermalcollector.collector.ScenarioCollectorUtils;
import com.miui.powerkeeper.utils.FileUtil;
import com.miui.powerkeeper.utils.ShellUtils;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

/* JADX INFO: loaded from: classes.dex */
public class SwitchProcessor extends ThermalProcessor {
    private static final int NORMAL_SCENARIO = 0;
    private static final String TAG = "SwitchProcessor";
    private static final String THERMAL_SCONFIG = "/sys/class/thermal/thermal_message/sconfig";
    protected int mCurrentThermalId = 0;
    protected int mCurrentScenario = 0;
    private final Policy mPolicy = new Policy();
    private final Map<String, Set<Integer>> mGroups = new HashMap();
    private final DBObserver mDBObserver = DBObserver.getInstance();

    private static class Policy {
        static final int mPowerModeBoundary;
        private Boolean mIsEnableOptimize;
        private Boolean mNeedOptimize;
        private boolean mVideochatConfigEnable;

        static {
            mPowerModeBoundary = ScenarioConfig.NEW_DEVICES ? 50 : 30;
        }

        private Policy() {
        }

        boolean getConfigMode(int i2) {
            Boolean bool = this.mIsEnableOptimize;
            return bool != null ? bool.booleanValue() : getGameModeDefault(i2);
        }

        boolean getGameModeDefault(int i2) {
            Boolean bool = this.mNeedOptimize;
            if (bool != null) {
                return bool.booleanValue();
            }
            return i2 % 100 >= mPowerModeBoundary;
        }

        public String toString() {
            return "Policy{mPowerModeBoundary=" + mPowerModeBoundary + ", mIsEnableOptimize=" + this.mIsEnableOptimize + ", mNeedOptimize=" + this.mNeedOptimize + ", mVideochatConfigEnable=" + this.mVideochatConfigEnable + '}';
        }
    }

    private void getDefaultNeedOptimize() {
        String string = SimpleSettings.Misc.getString(AutoRegister.getContext(), ScenarioConfig.KEY_NEED_CLOUD_OPTIMIZE, "");
        if (string.isEmpty()) {
            this.mPolicy.mNeedOptimize = null;
        } else {
            this.mPolicy.mNeedOptimize = Boolean.valueOf(Boolean.parseBoolean(string));
        }
    }

    private void getEnableOptimize() {
        String string = SimpleSettings.Misc.getString(AutoRegister.getContext(), SimpleSettingKeys.KEY_IS_ENABLE_OPTIMIZE_GAME, "");
        if (string.isEmpty()) {
            this.mPolicy.mIsEnableOptimize = null;
        } else {
            this.mPolicy.mIsEnableOptimize = Boolean.valueOf(Boolean.parseBoolean(string));
        }
    }

    private void getVideoChatConfig() {
        String functionParameters = CloudFunctionConfig.getFunctionParameters(AutoRegister.getContext(), SimpleSettingKeys.FUNC_THERMAL_VIDEOCHAT_CONFIG_ENABLE, "");
        if (functionParameters.contains(Build.DEVICE) || functionParameters.contains("ALL_DEVICES")) {
            this.mPolicy.mVideochatConfigEnable = SimpleSettings.Misc.getBoolean(AutoRegister.getContext(), SimpleSettingKeys.FUNC_THERMAL_VIDEOCHAT_CONFIG_ENABLE, false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$toString$0(StringBuilder sb, String str, Set set) {
        sb.append(str);
        sb.append(" : ");
        sb.append(set);
        sb.append(ShellUtils.COMMAND_LINE_END);
    }

    protected void calculateThermalId(int i2) {
        int iMapToThermalId = mapToThermalId(i2);
        if (this.mCurrentThermalId != iMapToThermalId) {
            this.mCurrentThermalId = iMapToThermalId;
            writeThermalId(iMapToThermalId);
        }
    }

    public void execute(int i2) {
        this.mCurrentScenario = i2;
        calculateThermalId(i2);
    }

    @Override // com.miui.powerkeeper.thermal.processor.ThermalProcessor
    public void init(Map<String, Set<Integer>> map) {
        final Map<String, Set<Integer>> map2 = this.mGroups;
        Objects.requireNonNull(map2);
        map.forEach(new BiConsumer() { // from class: com.miui.powerkeeper.thermal.processor.d
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                map2.put((String) obj, (Set) obj2);
            }
        });
        updateCloudData();
        this.mDBObserver.registerUser(SimpleSettingKeys.KEY_IS_ENABLE_OPTIMIZE_GAME, this);
    }

    protected int mapToThermalId(int i2) {
        int i3;
        if (i2 == 0) {
            return 0;
        }
        int i4 = i2 / 100;
        int i5 = i2 % 100;
        if (!this.mGroups.getOrDefault("need_map", Collections.emptySet()).contains(Integer.valueOf(i5))) {
            return i2;
        }
        if (this.mGroups.getOrDefault("game", Collections.emptySet()).contains(Integer.valueOf(i5))) {
            i3 = this.mPolicy.getConfigMode(i2) ? ScenarioConfig.SCONFIG_TGAME : ScenarioConfig.SCONFIG_MGAME;
        } else if (this.mGroups.getOrDefault("performance_boost_game", Collections.emptySet()).contains(Integer.valueOf(i5))) {
            i3 = this.mPolicy.getConfigMode(i2) ? 25 : ScenarioConfig.SCONFIG_MGAME;
        } else {
            if (!this.mGroups.getOrDefault("yuanshen", Collections.emptySet()).contains(Integer.valueOf(i5))) {
                if (this.mGroups.getOrDefault("camera", Collections.emptySet()).contains(Integer.valueOf(i5))) {
                    i3 = this.mPolicy.mVideochatConfigEnable ? ScenarioConfig.SCONFIG_VIDEO_CHAT : ScenarioConfig.SCONFIG_CAMERA;
                } else if (this.mGroups.getOrDefault("unperfromance", Collections.emptySet()).contains(Integer.valueOf(i5))) {
                    i5 -= Policy.mPowerModeBoundary;
                }
                return (i4 * 100) + i5;
            }
            i3 = this.mPolicy.getConfigMode(i2) ? ScenarioConfig.SCONFIG_YUANSHEN : ScenarioConfig.SCONFIG_MGAME;
        }
        i5 = i3;
        return (i4 * 100) + i5;
    }

    @NonNull
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("--- SwitchProcessor ---");
        sb.append(ShellUtils.COMMAND_LINE_END);
        sb.append("mCurrentThermalId");
        sb.append(this.mCurrentThermalId);
        sb.append(ShellUtils.COMMAND_LINE_END);
        sb.append("groups: ");
        sb.append(ShellUtils.COMMAND_LINE_END);
        this.mGroups.forEach(new BiConsumer() { // from class: com.miui.powerkeeper.thermal.processor.c
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                SwitchProcessor.lambda$toString$0(sb, (String) obj, (Set) obj2);
            }
        });
        sb.append(this.mPolicy);
        return sb.toString();
    }

    @Override // com.miui.powerkeeper.thermal.processor.IProcessor
    public void uninstall() {
        this.mDBObserver.unregisterUser(SimpleSettingKeys.KEY_IS_ENABLE_OPTIMIZE_GAME, this);
    }

    @Override // com.miui.powerkeeper.thermal.processor.ThermalProcessor, com.miui.powerkeeper.cloudcontrol.IUseCloud
    public void updateCloudData() {
        getVideoChatConfig();
        getDefaultNeedOptimize();
        getEnableOptimize();
        calculateThermalId(this.mCurrentScenario);
    }

    protected void writeThermalId(int i2) {
        int virtualBoardSensorTemp;
        if (CollectorManager.getInstance().isCompoundScenarioCollector() && ScenarioCollectorUtils.getInstance().isCompoundScenarioId(i2) && (virtualBoardSensorTemp = ThermalManager.ThermalTempInfoHelper.getVirtualBoardSensorTemp()) != -1) {
            PowerKeeperManager.getInstance().getEventsAggregator().notifyForBoardTemperature(virtualBoardSensorTemp);
        }
        if (FileUtil.writeToFile(THERMAL_SCONFIG, i2)) {
            return;
        }
        Slog.e(TAG, "writeThermalId failed id=" + this.mCurrentThermalId);
    }
}
