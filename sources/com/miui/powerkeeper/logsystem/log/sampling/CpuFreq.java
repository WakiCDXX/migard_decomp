package com.miui.powerkeeper.logsystem.log.sampling;

import android.content.Context;
import android.os.BatteryStats;
import android.util.SparseArray;
import com.miui.powerkeeper.batterylife.systeminfo.CpuStatus;
import com.miui.powerkeeper.customerpower.CustomerPowerUtil;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.logsystem.log.BaseLog;
import com.miui.powerkeeper.logsystem.utils.LSUtils;
import com.miui.powerkeeper.powerchecker.BatteryStatsHelper;
import com.miui.powerkeeper.powerchecker.UidStatsData;
import com.miui.powerkeeper.utils.ShellUtils;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class CpuFreq extends SamplingCatchedLog {
    public static final String SCALING_MIN_FREQ = "/sys/devices/system/cpu/cpufreq/%s/scaling_min_freq";
    public static final String TIME_IN_STATE = "/sys/devices/system/cpu/cpufreq/%s/stats/time_in_state";
    private String[] mClusters;
    private Context mContext;
    private String mCpuState;
    private boolean mFirst;
    private long[] mLastCpuFreqTimes;

    class CpuFreqData extends SamplingCatchedLog.Data {
        String cpuFreq;

        CpuFreqData() {
            super();
        }
    }

    public CpuFreq(Context context) {
        super(context);
        this.mClusters = CpuStatus.mCpuClusters;
        this.mFirst = true;
        this.mContext = context;
    }

    private String getBatteryStatsInfo(UidStatsData uidStatsData) {
        StringBuilder sb = new StringBuilder();
        long userCpuTimeUs = uidStatsData.getUserCpuTimeUs();
        long systemCpuTimeUs = uidStatsData.getSystemCpuTimeUs();
        if (userCpuTimeUs > 0 || systemCpuTimeUs > 0) {
            sb.append("Total cpu time: u=");
            BatteryStats.formatTimeMs(sb, userCpuTimeUs / 1000);
            sb.append(", s=");
            BatteryStats.formatTimeMs(sb, systemCpuTimeUs / 1000);
        }
        long[] cpuFreqTimes = uidStatsData.getCpuFreqTimes();
        if (cpuFreqTimes != null) {
            sb.append(ShellUtils.COMMAND_LINE_END);
            long[] jArr = this.mLastCpuFreqTimes;
            int i2 = 0;
            if (jArr == null || jArr.length != cpuFreqTimes.length) {
                sb.append("Total cpu time per freq:");
                while (i2 < cpuFreqTimes.length) {
                    sb.append(" ");
                    sb.append(cpuFreqTimes[i2]);
                    i2++;
                }
            } else {
                sb.append("Total cpu time increased per freq:");
                while (i2 < cpuFreqTimes.length) {
                    sb.append(" ");
                    long j2 = cpuFreqTimes[i2];
                    long j3 = this.mLastCpuFreqTimes[i2];
                    sb.append(j2 > j3 ? j2 - j3 : 0L);
                    i2++;
                }
            }
            this.mLastCpuFreqTimes = cpuFreqTimes;
        }
        return sb.toString();
    }

    private String getCpuState() {
        if (this.mClusters == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("\ntime_in_state:\n");
        for (int i2 = 0; i2 < this.mClusters.length; i2++) {
            sb.append(String.format(Locale.getDefault(), "%s:", this.mClusters[i2]));
            sb.append(ShellUtils.COMMAND_LINE_END);
            sb.append(LSUtils.readSysNodeInfo(String.format(Locale.getDefault(), TIME_IN_STATE, this.mClusters[i2])));
            sb.append(ShellUtils.COMMAND_LINE_END);
        }
        sb.append("\nscaling_min_freq:\n");
        for (int i3 = 0; i3 < this.mClusters.length; i3++) {
            sb.append(String.format(Locale.getDefault(), "%s: ", this.mClusters[i3]));
            sb.append(LSUtils.readSysNodeInfo(String.format(Locale.getDefault(), SCALING_MIN_FREQ, this.mClusters[i3])));
            sb.append(ShellUtils.COMMAND_LINE_END);
        }
        return sb.toString();
    }

    @Override // com.miui.powerkeeper.logsystem.log.sampling.SamplingCatchedLog
    public void catchLog() {
        this.mFirst = true;
        for (SamplingCatchedLog.Data data : this.mList) {
            addContent("Time: " + LSUtils.getStringDate(data.time));
            addContent("cpuFreq: " + ((CpuFreqData) data).cpuFreq);
        }
        addContent(this.mCpuState);
        super.catchLog();
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public int getInterfaceLogType() {
        return LogType.LOG_TYPE_SAMPLING_CPUFREQ;
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public String getInterfaceName() {
        return LogType.LOG_TYPE_SAMPLING_CPUFREQ_NAME;
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog
    protected boolean realProduceSystemLog(String str) {
        if (this.mFirst) {
            this.mFirst = false;
            this.mCpuState = getCpuState();
        }
        if (BaseLog.mScene == null) {
            return true;
        }
        CpuFreqData cpuFreqData = new CpuFreqData();
        cpuFreqData.time = System.currentTimeMillis();
        SparseArray<UidStatsData> uidStats = BatteryStatsHelper.getUidStats();
        if (uidStats.size() > 0) {
            for (int i2 = 0; i2 < uidStats.size(); i2++) {
                int iKeyAt = uidStats.keyAt(i2);
                UidStatsData uidStatsDataValueAt = uidStats.valueAt(i2);
                int uidByPackageName = CustomerPowerUtil.getUidByPackageName(this.mContext, BaseLog.mScene.getName());
                if (uidStatsDataValueAt != null && uidByPackageName == iKeyAt) {
                    cpuFreqData.cpuFreq = getBatteryStatsInfo(uidStatsDataValueAt);
                    break;
                }
            }
            this.mList.add(cpuFreqData);
        }
        return true;
    }
}
