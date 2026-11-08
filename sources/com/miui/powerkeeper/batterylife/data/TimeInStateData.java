package com.miui.powerkeeper.batterylife.data;

import com.miui.powerkeeper.batterylife.systeminfo.CpuStatus;
import com.miui.powerkeeper.utils.ShellUtils;
import java.io.Serializable;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class TimeInStateData implements Serializable {
    public HashMap<String, String> mTimeInStateMap;
    public long time;

    public String toString(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(str + "mTimeInStateMap=" + this.time + ShellUtils.COMMAND_LINE_END);
        String[] cpuCluster = CpuStatus.getCpuCluster();
        if (cpuCluster == null) {
            return sb.toString();
        }
        for (int i2 = 0; i2 < this.mTimeInStateMap.size() && i2 < cpuCluster.length; i2++) {
            sb.append(str + "time_in_state policy" + i2 + ": " + this.mTimeInStateMap.get(cpuCluster[i2]) + ShellUtils.COMMAND_LINE_END);
        }
        return sb.toString();
    }

    public String toString() {
        return toString("");
    }
}
