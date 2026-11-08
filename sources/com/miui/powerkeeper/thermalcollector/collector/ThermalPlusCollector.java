package com.miui.powerkeeper.thermalcollector.collector;

import com.miui.powerkeeper.feedbackcontrol.ThermalInfoUtils;
import com.miui.powerkeeper.thermalcollector.CollectorManager;
import com.miui.powerkeeper.thermalcollector.source.CollectorConfig;
import com.miui.powerkeeper.utils.FileUtil;
import com.miui.powerkeeper.utils.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import miui.util.FeatureParser;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class ThermalPlusCollector extends BaseCollector {
    private static final int CPU_NUMBER = 8;
    private static final String KEY_CPU_FREQ = "cpu_freq";
    private static final String KEY_CPU_MAX_FREQ = "cpu_max_freq";
    private static final String KEY_CPU_PROCESS = "cpu_process";
    private static final String KEY_CURRENT = "current";
    private static final String KEY_NTC = "ntc";
    private static final String KEY_VOLTAGE = "voltage";
    private static final String PATH_CPU_FREQ = "sys/devices/system/cpu/cpu%d/cpufreq/scaling_cur_freq";
    private static final String PATH_CPU_MAX_FREQ = "sys/devices/system/cpu/cpu%d/cpufreq/scaling_max_freq";
    private static final String PATH_THERMAL_TEMP = "sys/class/thermal/thermal_zone%d/temp";
    private static final String PATH_THERMAL_TYPE = "sys/class/thermal/thermal_zone%d/type";
    private static final String TAG = "CollectorThermalPlus";
    private static final Map<String, Integer> sNtcTypes = new HashMap(36);
    private static final List<String> sCpusFreq = new ArrayList(8);

    public ThermalPlusCollector() {
        super("CollectorThermalPlus");
        initNtc();
    }

    private List<String> getCpusFreq(String str) {
        sCpusFreq.clear();
        String str2 = str.equals(KEY_CPU_FREQ) ? PATH_CPU_FREQ : PATH_CPU_MAX_FREQ;
        for (int i2 = 0; i2 < 8; i2++) {
            sCpusFreq.add(FileUtil.getFileString(String.format(Locale.CHINA, str2, Integer.valueOf(i2))));
        }
        return sCpusFreq;
    }

    public static int getNTCId(String str) {
        Map<String, Integer> map = sNtcTypes;
        if (map.containsKey(str)) {
            return map.get(str).intValue();
        }
        return -1;
    }

    private Map<String, String> getNtcThermalTemp() {
        return (Map) sNtcTypes.entrySet().stream().collect(Collectors.toMap(new f(), new Function() { // from class: com.miui.powerkeeper.thermalcollector.collector.g
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return this.f593a.lambda$getNtcThermalTemp$0((Map.Entry) obj);
            }
        }));
    }

    private void initNtc() {
        int i2 = 0;
        Set<String> set = FeatureParser.getBoolean("is_mediatek", false) ? CollectorConfig.MTK_NTC_NAMES : CollectorConfig.QCOM_NTC_NAMES;
        while (true) {
            String fileString = FileUtil.getFileString(String.format(Locale.CHINA, PATH_THERMAL_TYPE, Integer.valueOf(i2)));
            if (fileString == null) {
                return;
            }
            if (set.contains(fileString)) {
                sNtcTypes.put(fileString, Integer.valueOf(i2));
            }
            i2++;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getNtcThermalTemp$0(Map.Entry entry) {
        return readNtcTemp(((Integer) entry.getValue()).intValue());
    }

    private String readNtcTemp(int i2) {
        return FileUtil.getFileString(String.format(Locale.CHINA, PATH_THERMAL_TEMP, Integer.valueOf(i2)));
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.BaseCollector
    public void collect() {
        JSONObject json = getJson();
        try {
            json.put("current", ThermalInfoUtils.getBatteryCurrentNow(this.mContext));
            json.put(KEY_VOLTAGE, Utils.getBatteryVoltage(this.mContext));
            json.put(KEY_NTC, getNtcThermalTemp());
            json.put(KEY_CPU_FREQ, getCpusFreq(KEY_CPU_FREQ));
            json.put(KEY_CPU_MAX_FREQ, getCpusFreq(KEY_CPU_MAX_FREQ));
            if (CollectorManager.getDataCollectorVersion().equals("2.0")) {
                json.put(KEY_CPU_PROCESS, Utils.getTopNProcessInfoFromCpuInfo(10));
            } else {
                json.put(KEY_CPU_PROCESS, ThermalInfoUtils.getProcessList().get("total"));
            }
        } catch (Exception e2) {
            CollectorManager.getHistory().log(e2.getMessage());
        }
    }

    @Override // com.miui.powerkeeper.thermalcollector.collector.BaseCollector
    protected void prepareBeDestroy() {
    }
}
