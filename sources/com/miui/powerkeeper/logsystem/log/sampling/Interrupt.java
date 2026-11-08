package com.miui.powerkeeper.logsystem.log.sampling;

import android.content.Context;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.logsystem.utils.LSUtils;
import com.miui.powerkeeper.utils.ShellUtils;

/* JADX INFO: loaded from: classes.dex */
public class Interrupt extends SamplingCatchedLog {
    private String FILE_PATH;
    private int count;
    private String mFirstString;
    private String mLastString;
    private boolean mStarted;

    public Interrupt(Context context) {
        super(context);
        this.FILE_PATH = "/proc/interrupts";
        this.mStarted = false;
        this.count = 0;
    }

    @Override // com.miui.powerkeeper.logsystem.log.sampling.SamplingCatchedLog
    public void catchLog() {
        if (this.mFirstString != null && this.mLastString != null) {
            StringBuilder sb = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            String[] strArrSplit = this.mFirstString.split(ShellUtils.COMMAND_LINE_END);
            String[] strArrSplit2 = this.mLastString.split(ShellUtils.COMMAND_LINE_END);
            sb.append(strArrSplit[0]);
            sb.append(ShellUtils.COMMAND_LINE_END);
            sb2.append(strArrSplit2[0]);
            sb2.append(ShellUtils.COMMAND_LINE_END);
            for (int i2 = 1; i2 < strArrSplit.length && i2 < strArrSplit2.length; i2++) {
                if (!strArrSplit[i2].equals(strArrSplit2[i2])) {
                    sb.append(strArrSplit[i2]);
                    sb.append(ShellUtils.COMMAND_LINE_END);
                    sb2.append(strArrSplit2[i2]);
                    sb2.append(ShellUtils.COMMAND_LINE_END);
                }
            }
            addContent("Time: " + LSUtils.getStringDate(this.mList.get(0).time));
            addContent(sb.toString());
            addContent(ShellUtils.COMMAND_LINE_END);
            addContent("Time: " + LSUtils.getStringDate(this.mList.get(1).time));
            addContent(sb2.toString());
        }
        this.mFirstString = null;
        this.mLastString = null;
        this.mStarted = false;
        this.count = 0;
        super.catchLog();
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public int getInterfaceLogType() {
        return LogType.LOG_TYPE_SAMPLING_INTERRUPT;
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public String getInterfaceName() {
        return LogType.LOG_TYPE_SAMPLING_INTERRUPT_NAME;
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog
    protected boolean realProduceSystemLog(String str) {
        if (this.mStarted) {
            int i2 = this.count + 1;
            this.count = i2;
            if (i2 == 3) {
                this.mLastString = LSUtils.readFromDynamicFile(this.FILE_PATH);
                SamplingCatchedLog.Data data = new SamplingCatchedLog.Data();
                data.time = System.currentTimeMillis();
                this.mList.add(data);
            }
        } else {
            this.mStarted = true;
            this.count++;
            this.mFirstString = LSUtils.readFromDynamicFile(this.FILE_PATH);
            SamplingCatchedLog.Data data2 = new SamplingCatchedLog.Data();
            data2.time = System.currentTimeMillis();
            this.mList.add(data2);
        }
        return true;
    }
}
