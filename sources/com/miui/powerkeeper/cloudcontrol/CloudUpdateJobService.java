package com.miui.powerkeeper.cloudcontrol;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.util.Slog;
import com.miui.powerkeeper.PowerKeeperApplication;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.utils.Constant;
import com.miui.powerkeeper.utils.NetUtils;
import com.miui.powerkeeper.utils.PowerLog;
import com.miui.powerkeeper.utils.Utils;

/* JADX INFO: loaded from: classes.dex */
public class CloudUpdateJobService extends JobService {
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    private static String TAG = "PowerKeeper.CloudControl";

    public static void scheduleCloudUpdateJob(Context context) {
        if (!LocalUpdateUtils.isOtaUpgrade(context)) {
            if (LocalUpdateUtils.hasSuccessfulCloudSync(context)) {
                scheduleJob(context, 86400000L);
                return;
            } else {
                scheduleJob(context, 7200000L);
                return;
            }
        }
        long jRandom = (long) (Math.random() * 7200000.0d);
        if (DEBUG) {
            Log.i(TAG, "ota delay time:" + jRandom);
        }
        scheduleJob(context, jRandom);
    }

    public static boolean scheduleJob(Context context, long j2) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(LogType.LOG_TYPE_JOB_NAME);
        if (jobScheduler == null) {
            Slog.w(TAG, "scheduleJob(): JobScheduler not supported");
            return false;
        }
        jobScheduler.cancel(Constant.JOB_ID_CLOUD_UPDATE);
        JobInfo.Builder persisted = new JobInfo.Builder(Constant.JOB_ID_CLOUD_UPDATE, new ComponentName(context, (Class<?>) CloudUpdateJobService.class)).setMinimumLatency(j2).setPersisted(true);
        if (LocalUpdateUtils.hasSuccessfulCloudSync(context)) {
            persisted.setRequiredNetworkType(2);
        }
        if (jobScheduler.schedule(persisted.build()) > 0) {
            return true;
        }
        Slog.w(TAG, String.format("scheduleJob failed: job id is %s", Integer.valueOf(Constant.JOB_ID_CLOUD_UPDATE)));
        return false;
    }

    @Override // android.app.job.JobService
    public boolean onStartJob(final JobParameters jobParameters) {
        Slog.d(TAG, "circle job is scheduled");
        PowerLog.recordKey("job start");
        new Thread(new Runnable() { // from class: com.miui.powerkeeper.cloudcontrol.CloudUpdateJobService.1
            @Override // java.lang.Runnable
            public void run() {
                boolean zIsWifiConnected = NetUtils.isWifiConnected(CloudUpdateJobService.this.getApplicationContext());
                boolean zIsCtaAllowed = CloudUpdateReceiver.isCtaAllowed(CloudUpdateJobService.this.getApplicationContext());
                boolean zIsNetConnected = NetUtils.isNetConnected(CloudUpdateJobService.this.getApplicationContext());
                boolean zHasSuccessfulCloudSync = LocalUpdateUtils.hasSuccessfulCloudSync(CloudUpdateJobService.this.getApplicationContext());
                boolean cloudAllowState = LocalUpdateUtils.getCloudAllowState(CloudUpdateJobService.this.getApplicationContext());
                if ((!zHasSuccessfulCloudSync || zIsWifiConnected) && zIsCtaAllowed && ((zHasSuccessfulCloudSync || zIsNetConnected) && cloudAllowState && PowerKeeperApplication.d())) {
                    PowerLog.recordKey("job success");
                    LocalUpdateUtils.startCloudSyncData(CloudUpdateJobService.this.getApplicationContext(), LocalUpdateUtils.isOtaUpgrade(Utils.APP_CONTEXT));
                    LocalUpdateUtils.setLastBuildVersion(CloudUpdateJobService.this.getApplicationContext());
                } else {
                    if (CloudUpdateJobService.DEBUG) {
                        Log.w(CloudUpdateJobService.TAG, "circle job restricted, isWifiConnected:" + zIsWifiConnected + " hasSync:" + zHasSuccessfulCloudSync + " isCtaAllowed:" + zIsCtaAllowed + " isNetConnected:" + zIsNetConnected + " isAutoSyncAllowed:" + cloudAllowState);
                    }
                    PowerLog.recordKey("job failed for W=" + zIsWifiConnected + " N=" + zIsNetConnected + " C=" + zIsCtaAllowed + " H=" + zHasSuccessfulCloudSync + " A=" + cloudAllowState + " P=" + PowerKeeperApplication.d());
                }
                CloudUpdateJobService.this.jobFinished(jobParameters, false);
                CloudUpdateJobService.scheduleCloudUpdateJob(CloudUpdateJobService.this.getApplicationContext());
            }
        }).start();
        return true;
    }

    @Override // android.app.job.JobService
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
