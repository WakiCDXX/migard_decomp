package com.xiaomi.push.service.timers;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.SystemClock;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.push.service.XMJobService;
import com.xiaomi.smack.SmackConfiguration;

/* JADX INFO: loaded from: classes.dex */
@TargetApi(21)
public class AlarmV21 implements Alarm.IAlarm {
    Context mContext;
    JobScheduler mJobScheduler;
    private boolean mStarted = false;

    AlarmV21(Context context) {
        this.mContext = context;
        this.mJobScheduler = (JobScheduler) context.getSystemService(LogType.LOG_TYPE_JOB_NAME);
    }

    @Override // com.xiaomi.push.service.timers.Alarm.IAlarm
    public boolean isAlive() {
        return this.mStarted;
    }

    void register(long j2) {
        JobInfo.Builder builder = new JobInfo.Builder(1, new ComponentName(this.mContext.getPackageName(), XMJobService.class.getName()));
        builder.setMinimumLatency(j2);
        builder.setOverrideDeadline(j2);
        builder.setRequiredNetworkType(1);
        MyLog.v("schedule Job = " + builder.build().getId() + " in " + j2);
        this.mJobScheduler.schedule(builder.build());
    }

    @Override // com.xiaomi.push.service.timers.Alarm.IAlarm
    public void registerPing(boolean z2) {
        if (z2 || this.mStarted) {
            long pingInteval = SmackConfiguration.getPingInteval();
            if (z2) {
                stop();
                pingInteval -= SystemClock.elapsedRealtime() % pingInteval;
            }
            this.mStarted = true;
            register(pingInteval);
        }
    }

    @Override // com.xiaomi.push.service.timers.Alarm.IAlarm
    public void stop() {
        this.mStarted = false;
        this.mJobScheduler.cancel(1);
    }
}
