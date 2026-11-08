package com.xiaomi.clientreport.job;

import android.content.Context;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.misc.ScheduledJobConstants;
import com.xiaomi.channel.commonutils.misc.ScheduledJobManager;
import com.xiaomi.clientreport.manager.ClientReportLogicManager;

/* JADX INFO: loaded from: classes.dex */
public class PerfUploadJob extends ScheduledJobManager.Job {
    private Context mContext;

    public PerfUploadJob(Context context) {
        this.mContext = context;
    }

    private boolean checkPerfNeedUpload() {
        return ClientReportLogicManager.getInstance(this.mContext).getConfig().isPerfUploadSwitchOpen();
    }

    @Override // com.xiaomi.channel.commonutils.misc.ScheduledJobManager.Job
    public String getJobId() {
        return ScheduledJobConstants.PERF_UPLOAD_JOB_ID;
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            if (checkPerfNeedUpload()) {
                ClientReportLogicManager.getInstance(this.mContext).sendPerf();
                MyLog.v(this.mContext.getPackageName() + " perf begin upload");
            }
        } catch (Exception e2) {
            MyLog.e("fail to send perf data. " + e2);
        }
    }
}
