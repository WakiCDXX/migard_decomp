package com.miui.powerkeeper.logsystem.log.sampling;

import android.content.Context;
import com.miui.powerkeeper.batterylife.BLUtils;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.logsystem.utils.LSUtils;

/* JADX INFO: loaded from: classes.dex */
public class Clock extends SamplingCatchedLog {
    private String FILE_PATH;

    class ClockData extends SamplingCatchedLog.Data {
        String result;

        ClockData() {
            super();
        }
    }

    public Clock(Context context) {
        super(context);
        this.FILE_PATH = "/d/clk/clk_enabled_list";
    }

    @Override // com.miui.powerkeeper.logsystem.log.sampling.SamplingCatchedLog
    public void catchLog() {
        for (SamplingCatchedLog.Data data : this.mList) {
            addContent("Time: " + LSUtils.getStringDate(data.time));
            addContent(((ClockData) data).result);
        }
        super.catchLog();
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public int getInterfaceLogType() {
        return LogType.LOG_TYPE_SAMPLING_CLOCK;
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public String getInterfaceName() {
        return LogType.LOG_TYPE_SAMPLING_CLOCK_NAME;
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog
    protected boolean realProduceSystemLog(String str) {
        ClockData clockData = new ClockData();
        clockData.time = System.currentTimeMillis();
        clockData.result = BLUtils.readSysNodeInfoByWhetstone(this.FILE_PATH);
        this.mList.add(clockData);
        return true;
    }
}
