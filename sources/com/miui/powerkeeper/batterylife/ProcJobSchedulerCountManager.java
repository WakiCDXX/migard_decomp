package com.miui.powerkeeper.batterylife;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseArrayMap;
import com.android.internal.os.BackgroundThread;
import com.miui.powerkeeper.DeviceIdlePolicyHelper;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.batterylife.data.JobSchedulerData;
import com.miui.powerkeeper.batterylife.data.JobTrack;
import com.miui.powerkeeper.event.EventMonitor;
import com.miui.powerkeeper.event.EventsAggregator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import miui.process.ForegroundInfo;

/* JADX INFO: loaded from: classes.dex */
public class ProcJobSchedulerCountManager extends Handler {
    private static final int BG = 2;
    private static final int FG = 1;
    public static final String TAG = "ProcJobSchedulerCountManager";
    private static final long THRESHOLD_TIME = 28800000;
    private static final Object lock = new Object();
    private final boolean DBG_JOB_COUNT;
    private EventMonitor.EventCallback eventCallback;
    private Context mContext;
    private volatile ForegroundInfo mCurrentFgInfo;
    private volatile int mCurrentUid;
    private final SparseArray<JobSchedulerData> mJobSchedulerDataMap;
    private final SparseArrayMap<Integer, JobTrack> mJobTrackMap;
    private volatile int mLastForegroundUid;

    public ProcJobSchedulerCountManager(Context context) {
        super(BackgroundThread.get().getLooper());
        this.DBG_JOB_COUNT = Log.isLoggable("power.batterylife_job", 3);
        this.mJobSchedulerDataMap = new SparseArray<>();
        this.mJobTrackMap = new SparseArrayMap<>();
        this.mCurrentFgInfo = null;
        this.mCurrentUid = 0;
        this.mLastForegroundUid = 0;
        this.eventCallback = new EventMonitor.EventCallback() { // from class: com.miui.powerkeeper.batterylife.ProcJobSchedulerCountManager.1
            @Override // com.miui.powerkeeper.event.EventMonitor.EventCallback
            public void notifyEvent(int i2, Bundle bundle) {
                JobTrack jobTrack;
                if (bundle == null) {
                    if (ProcJobSchedulerCountManager.this.DBG_JOB_COUNT) {
                        Log.d(ProcJobSchedulerCountManager.TAG, "EventMonitor.RESOURCE_ID_JOBSCHEDULER_TRIGGER bundle = null");
                        return;
                    }
                    return;
                }
                synchronized (ProcJobSchedulerCountManager.lock) {
                    try {
                        String string = bundle.getString("sourcePkg");
                        int i3 = bundle.getInt("jobId");
                        int i4 = bundle.getInt("sourceUid");
                        int i5 = bundle.getInt("status");
                        long j2 = bundle.getLong("elapsedRealtime");
                        if (i5 == 1) {
                            if (ProcJobSchedulerCountManager.this.DBG_JOB_COUNT) {
                                Log.d(ProcJobSchedulerCountManager.TAG, "Job start pkgName = " + string);
                            }
                            ProcJobSchedulerCountManager.this.mJobTrackMap.add(i4, Integer.valueOf(i3), new JobTrack(i3, i4, ProcJobSchedulerCountManager.this.mCurrentUid == i4 ? 1 : 2, j2, PowerKeeperManager.getInstance().isCharging()));
                        } else if (i5 == 0 && (jobTrack = (JobTrack) ProcJobSchedulerCountManager.this.mJobTrackMap.get(i4, Integer.valueOf(i3))) != null) {
                            JobSchedulerData jobSchedulerData = (JobSchedulerData) ProcJobSchedulerCountManager.this.mJobSchedulerDataMap.get(i4);
                            if (jobSchedulerData == null) {
                                jobSchedulerData = new JobSchedulerData(string, i4);
                            }
                            if (ProcJobSchedulerCountManager.this.DBG_JOB_COUNT) {
                                Log.d(ProcJobSchedulerCountManager.TAG, "Job process jobSchedulerData = " + jobSchedulerData.toString());
                            }
                            long jElapsedRealtime = SystemClock.elapsedRealtime();
                            if (jobTrack.getBgTime() == 0 && jobTrack.getFgTime() == 0) {
                                long startTime = jElapsedRealtime - jobTrack.getStartTime();
                                if (startTime < ProcJobSchedulerCountManager.THRESHOLD_TIME) {
                                    int lastStatus = jobTrack.getLastStatus();
                                    if (lastStatus == 1) {
                                        jobSchedulerData.setForegroundCount(jobSchedulerData.getForegroundCount() + 1);
                                        jobSchedulerData.setForegroundAllTime(jobSchedulerData.getForegroundAllTime() + startTime);
                                    } else if (lastStatus == 2) {
                                        jobSchedulerData.setBackgroundCount(jobSchedulerData.getBackgroundCount() + 1);
                                        jobSchedulerData.setBackgroundAllTime(jobSchedulerData.getBackgroundAllTime() + startTime);
                                        if (!jobTrack.getOnCharge()) {
                                            jobSchedulerData.setBackgroundUnChargeCount(jobSchedulerData.getBackgroundUnChargeCount() + 1);
                                            jobSchedulerData.setBackgroundUnChargeAllTime(jobSchedulerData.getBackgroundUnChargeAllTime() + startTime);
                                        }
                                    }
                                }
                            } else if (jobTrack.getBgTime() > 0 || jobTrack.getFgTime() > 0) {
                                if (ProcJobSchedulerCountManager.this.mCurrentUid == i4) {
                                    jobTrack.setFgTime(jobTrack.getFgTime() + (jElapsedRealtime - (jobTrack.getLastUpdateTime() == 0 ? jobTrack.getStartTime() : jobTrack.getLastUpdateTime())));
                                    jobTrack.setLastUpdateTime(jElapsedRealtime);
                                } else {
                                    jobTrack.setBgTime(jobTrack.getBgTime() + (jElapsedRealtime - (jobTrack.getLastUpdateTime() == 0 ? jobTrack.getStartTime() : jobTrack.getLastUpdateTime())));
                                    jobTrack.setLastUpdateTime(jElapsedRealtime);
                                }
                                long startTime2 = jElapsedRealtime - jobTrack.getStartTime();
                                if (startTime2 < ProcJobSchedulerCountManager.THRESHOLD_TIME) {
                                    float f2 = startTime2;
                                    if (jobTrack.getFgTime() / f2 >= 0.9d) {
                                        jobSchedulerData.setForegroundCount(jobSchedulerData.getForegroundCount() + 1);
                                        jobSchedulerData.setForegroundAllTime(jobSchedulerData.getForegroundAllTime() + jobTrack.getFgTime());
                                        jobSchedulerData.setBackgroundSwitchSwitchCount(jobSchedulerData.getBackgroundSwitchSwitchCount() + 1);
                                    }
                                    if (jobTrack.getBgTime() / f2 >= 0.9d) {
                                        jobSchedulerData.setBackgroundCount(jobSchedulerData.getBackgroundCount() + 1);
                                        jobSchedulerData.setBackgroundAllTime(jobSchedulerData.getBackgroundAllTime() + jobTrack.getBgTime());
                                        jobSchedulerData.setBackgroundSwitchSwitchCount(jobSchedulerData.getBackgroundSwitchSwitchCount() + 1);
                                        if (!jobTrack.getOnCharge()) {
                                            jobSchedulerData.setBackgroundUnChargeCount(jobSchedulerData.getBackgroundUnChargeCount() + 1);
                                            jobSchedulerData.setBackgroundUnChargeAllTime(jobSchedulerData.getBackgroundUnChargeAllTime() + jobTrack.getBgTime());
                                        }
                                    }
                                }
                            }
                            jobSchedulerData.setWhitelistApp(DeviceIdlePolicyHelper.i(ProcJobSchedulerCountManager.this.mContext).k(string));
                            if (ProcJobSchedulerCountManager.this.DBG_JOB_COUNT) {
                                Log.d(ProcJobSchedulerCountManager.TAG, "Job stop jobSchedulerData = " + jobSchedulerData.toString());
                            }
                            ProcJobSchedulerCountManager.this.mJobSchedulerDataMap.put(i4, jobSchedulerData);
                            ProcJobSchedulerCountManager.this.mJobTrackMap.delete(i4, Integer.valueOf(i3));
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        };
        this.mContext = context;
        EventsAggregator eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
        if (eventsAggregator != null) {
            eventsAggregator.registerForForegroundInfo(this);
        }
        EventMonitor.getInstance().registerCallback(256, this.eventCallback);
    }

    void destroy() {
        EventsAggregator eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
        if (eventsAggregator != null) {
            eventsAggregator.unregisterForForegroundInfo(this);
        }
        EventMonitor.getInstance().unregisterCallback(256, this.eventCallback);
        this.mJobSchedulerDataMap.clear();
        this.mJobTrackMap.clear();
    }

    public String dump() {
        StringBuilder sb = new StringBuilder();
        sb.append("-----------JobSchedulerDataMap-------------");
        sb.append('\n');
        for (int i2 = 0; i2 < this.mJobSchedulerDataMap.size(); i2++) {
            sb.append(this.mJobSchedulerDataMap.valueAt(i2).toString());
            sb.append('\n');
        }
        sb.append("-----------JobTrackMap-------------");
        sb.append('\n');
        for (int i3 = 0; i3 < this.mJobTrackMap.numMaps(); i3++) {
            for (int i4 = 0; i4 < this.mJobTrackMap.numElementsForKeyAt(i3); i4++) {
                sb.append(((JobTrack) this.mJobTrackMap.valueAt(i3, i4)).toString());
                sb.append('\n');
            }
        }
        return sb.toString();
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        if (message.what != -9) {
            return;
        }
        synchronized (lock) {
            try {
                this.mCurrentFgInfo = (ForegroundInfo) message.obj;
                if (this.mCurrentFgInfo == null) {
                    return;
                }
                this.mCurrentUid = this.mCurrentFgInfo.mForegroundUid;
                this.mLastForegroundUid = this.mCurrentFgInfo.mLastForegroundUid;
                if (this.DBG_JOB_COUNT) {
                    Log.d(TAG, "foreground change mCurrentFgInfo = " + this.mCurrentFgInfo.toString());
                }
                long jElapsedRealtime = SystemClock.elapsedRealtime();
                for (int i2 = 0; i2 < this.mJobTrackMap.numMaps(); i2++) {
                    int iKeyAt = this.mJobTrackMap.keyAt(i2);
                    if (iKeyAt == this.mCurrentUid) {
                        for (int i3 = 0; i3 < this.mJobTrackMap.numElementsForKeyAt(i2); i3++) {
                            ((Integer) this.mJobTrackMap.keyAt(i2, i3)).intValue();
                            JobTrack jobTrack = (JobTrack) this.mJobTrackMap.valueAt(i2, i3);
                            if (this.DBG_JOB_COUNT) {
                                Log.d(TAG, "foreground change fg before jobTrack = " + jobTrack.toString());
                            }
                            if (jobTrack.getLastStatus() == 2) {
                                jobTrack.setLastStatus(1);
                                jobTrack.setBgTime(jobTrack.getBgTime() + (jElapsedRealtime - (jobTrack.getLastUpdateTime() == 0 ? jobTrack.getStartTime() : jobTrack.getLastUpdateTime())));
                                jobTrack.setLastUpdateTime(jElapsedRealtime);
                                if (this.DBG_JOB_COUNT) {
                                    Log.d(TAG, "foreground change fg after jobTrack = " + jobTrack.toString());
                                }
                            }
                        }
                    } else if (iKeyAt == this.mLastForegroundUid) {
                        for (int i4 = 0; i4 < this.mJobTrackMap.numElementsForKeyAt(i2); i4++) {
                            ((Integer) this.mJobTrackMap.keyAt(i2, i4)).intValue();
                            JobTrack jobTrack2 = (JobTrack) this.mJobTrackMap.valueAt(i2, i4);
                            if (this.DBG_JOB_COUNT) {
                                Log.d(TAG, "foreground change bg before jobTrack = " + jobTrack2.toString());
                            }
                            if (jobTrack2.getLastStatus() == 1) {
                                jobTrack2.setLastStatus(2);
                                jobTrack2.setFgTime(jobTrack2.getFgTime() + (jElapsedRealtime - (jobTrack2.getLastUpdateTime() == 0 ? jobTrack2.getStartTime() : jobTrack2.getLastUpdateTime())));
                                jobTrack2.setLastUpdateTime(jElapsedRealtime);
                                if (this.DBG_JOB_COUNT) {
                                    Log.d(TAG, "foreground change bg after jobTrack = " + jobTrack2.toString());
                                }
                            }
                        }
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void resetAllData() {
        this.mJobSchedulerDataMap.clear();
        for (int i2 = 0; i2 < this.mJobTrackMap.numMaps(); i2++) {
            for (int i3 = 0; i3 < this.mJobTrackMap.numElementsForKeyAt(i2); i3++) {
                int iKeyAt = this.mJobTrackMap.keyAt(i2);
                JobTrack jobTrack = (JobTrack) this.mJobTrackMap.valueAt(i2, i3);
                Integer num = (Integer) this.mJobTrackMap.keyAt(i2, i3);
                num.intValue();
                if (SystemClock.elapsedRealtime() - jobTrack.getStartTime() >= THRESHOLD_TIME) {
                    this.mJobTrackMap.delete(iKeyAt, num);
                }
            }
        }
    }

    public List<JobSchedulerData> sortDataToList() {
        ArrayList arrayList = new ArrayList();
        for (int i2 = 0; i2 < this.mJobSchedulerDataMap.size(); i2++) {
            arrayList.add(this.mJobSchedulerDataMap.valueAt(i2));
        }
        Collections.sort(arrayList);
        return arrayList;
    }
}
