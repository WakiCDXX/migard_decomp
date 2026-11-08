package com.miui.powerkeeper.logsystem.log.sampling;

import android.content.Context;
import android.os.BatteryManager;
import android.util.Log;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.logsystem.log.BaseLog;
import com.miui.powerkeeper.logsystem.utils.LSUtils;
import com.miui.powerkeeper.utils.ShellUtils;

/* JADX INFO: loaded from: classes.dex */
public class CurrentSampling extends SamplingCatchedLog {
    private BatteryManager mBatteryManager;
    private StringBuilder mCurrent;
    private int mCurrentSum;
    private boolean mFirst;
    private int mSamplingTimes;

    public CurrentSampling(Context context) {
        super(context);
        this.mCurrent = new StringBuilder();
        this.mFirst = true;
        this.mBatteryManager = (BatteryManager) context.getSystemService("batterymanager");
    }

    private int getBatteryCurrent() {
        BatteryManager batteryManager = this.mBatteryManager;
        if (batteryManager == null) {
            return 0;
        }
        int intProperty = batteryManager.getIntProperty(2) / 1000;
        Log.d(BaseLog.TAG, "CurrentSampling, current = " + intProperty);
        return intProperty;
    }

    @Override // com.miui.powerkeeper.logsystem.log.sampling.SamplingCatchedLog
    public void catchLog() {
        addContent(this.mCurrent.toString());
        if (this.mSamplingTimes != 0) {
            addContent("Avg Current: " + (this.mCurrentSum / this.mSamplingTimes) + "mA");
        }
        this.mCurrent = new StringBuilder();
        this.mFirst = true;
        super.catchLog();
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public int getInterfaceLogType() {
        return LogType.LOG_TYPE_F_SAMPLING_CURRENT;
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public String getInterfaceName() {
        return LogType.LOG_TYPE_F_SAMPLING_CURRENT_NAME;
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog
    protected boolean realProduceSystemLog(String str) {
        if (this.mFirst) {
            StringBuilder sb = this.mCurrent;
            sb.append(getInterfaceName());
            sb.append(" ");
            StringBuilder sb2 = this.mCurrent;
            sb2.append("Start Time: ");
            sb2.append(LSUtils.getStringDate(System.currentTimeMillis()));
            sb2.append(ShellUtils.COMMAND_LINE_END);
            StringBuilder sb3 = this.mCurrent;
            sb3.append("Sampling Frequency: ");
            sb3.append(1);
            sb3.append("s, ");
            StringBuilder sb4 = this.mCurrent;
            sb4.append("Sampling Times: ");
            sb4.append(60);
            sb4.append(ShellUtils.COMMAND_LINE_END);
            this.mFirst = false;
        }
        int batteryCurrent = getBatteryCurrent();
        this.mCurrentSum += batteryCurrent;
        this.mSamplingTimes++;
        this.mCurrent.append(batteryCurrent);
        this.mCurrent.append(" ");
        return true;
    }
}
