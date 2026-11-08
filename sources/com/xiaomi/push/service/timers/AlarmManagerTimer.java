package com.xiaomi.push.service.timers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.reflect.JavaCalls;
import com.xiaomi.push.service.HeartbeatHolder;
import com.xiaomi.push.service.PushConstants;

/* JADX INFO: loaded from: classes.dex */
class AlarmManagerTimer implements Alarm.IAlarm {
    protected Context mContext;
    private PendingIntent mPi = null;
    private volatile long mNextPingTs = 0;

    public AlarmManagerTimer(Context context) {
        this.mContext = null;
        this.mContext = context;
    }

    private void setExact(AlarmManager alarmManager, long j2, PendingIntent pendingIntent) {
        try {
            AlarmManager.class.getMethod("setExact", Integer.TYPE, Long.TYPE, PendingIntent.class).invoke(alarmManager, 2, Long.valueOf(j2), pendingIntent);
        } catch (Exception e2) {
            MyLog.e("[Alarm] invoke setExact method meet error. " + e2);
        }
    }

    @Override // com.xiaomi.push.service.timers.Alarm.IAlarm
    public boolean isAlive() {
        return this.mNextPingTs != 0;
    }

    public void register(Intent intent, long j2) {
        AlarmManager alarmManager = (AlarmManager) this.mContext.getSystemService("alarm");
        this.mPi = PendingIntent.getBroadcast(this.mContext, 0, intent, LogType.LOG_TYPE_SAMPLING_CPUFREQ);
        if (MIUIUtils.isXMSF(this.mContext)) {
            JavaCalls.callMethod(alarmManager, "setExactAndAllowWhileIdle", 2, Long.valueOf(j2), this.mPi);
        } else {
            alarmManager.set(2, j2, this.mPi);
        }
        MyLog.v("[Alarm] register timer " + j2);
    }

    @Override // com.xiaomi.push.service.timers.Alarm.IAlarm
    public void registerPing(boolean z2) {
        long pingIntervalAndRecordEvents = HeartbeatHolder.getInstance(this.mContext).getPingIntervalAndRecordEvents();
        if (z2 || this.mNextPingTs != 0) {
            if (z2) {
                stop();
            }
            long jElapsedRealtime = SystemClock.elapsedRealtime();
            if (z2 || this.mNextPingTs == 0) {
                this.mNextPingTs = jElapsedRealtime + (pingIntervalAndRecordEvents - (jElapsedRealtime % pingIntervalAndRecordEvents));
            } else if (this.mNextPingTs <= jElapsedRealtime) {
                this.mNextPingTs += pingIntervalAndRecordEvents;
                if (this.mNextPingTs < jElapsedRealtime) {
                    this.mNextPingTs = jElapsedRealtime + pingIntervalAndRecordEvents;
                }
            }
            Intent intent = new Intent(PushConstants.ACTION_PING_TIMER);
            intent.setPackage(this.mContext.getPackageName());
            register(intent, this.mNextPingTs);
        }
    }

    @Override // com.xiaomi.push.service.timers.Alarm.IAlarm
    public void stop() {
        if (this.mPi != null) {
            try {
                ((AlarmManager) this.mContext.getSystemService("alarm")).cancel(this.mPi);
            } catch (Exception unused) {
            } finally {
                this.mPi = null;
                MyLog.v("[Alarm] unregister timer");
                this.mNextPingTs = 0L;
            }
        }
        this.mNextPingTs = 0L;
    }
}
