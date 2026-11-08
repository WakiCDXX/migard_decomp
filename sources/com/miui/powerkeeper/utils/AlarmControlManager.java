package com.miui.powerkeeper.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import com.android.internal.os.BackgroundThread;
import com.google.android.collect.Lists;
import com.miui.powerkeeper.PowerKeeperBackgroundService;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.event.EventsAggregator;
import com.xiaomi.mipush.sdk.Constants;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class AlarmControlManager {
    private List<PowerKeeperInterface.a> mAlarmHappenCallbacks = Lists.newArrayList();
    private Context mContext;

    public AlarmControlManager(Context context) {
        this.mContext = context;
    }

    private void disableAlarm() {
        Intent intent = new Intent(this.mContext, (Class<?>) PowerKeeperBackgroundService.class);
        intent.setAction(PowerKeeperManager.ACTION_ALARM);
        ((AlarmManager) this.mContext.getSystemService("alarm")).cancel(PendingIntent.getService(this.mContext, 0, intent, 201326592));
    }

    private void enableAlarm() {
        long jElapsedRealtime = SystemClock.elapsedRealtime() + Constants.ASSEMBLE_PUSH_NETWORK_INTERVAL;
        Intent intent = new Intent(this.mContext, (Class<?>) PowerKeeperBackgroundService.class);
        intent.setAction(PowerKeeperManager.ACTION_ALARM);
        ((AlarmManager) this.mContext.getSystemService("alarm")).setRepeating(3, jElapsedRealtime, Constants.ASSEMBLE_PUSH_NETWORK_INTERVAL, PendingIntent.getService(this.mContext, 0, intent, 201326592));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyAlarmHappenListeners() {
        final long jElapsedRealtime = SystemClock.elapsedRealtime();
        EventsAggregator.getInstance(this.mContext).notifyAlarmHappenListeners(jElapsedRealtime);
        synchronized (this.mAlarmHappenCallbacks) {
            try {
                for (final PowerKeeperInterface.a aVar : this.mAlarmHappenCallbacks) {
                    Handler handler = aVar.f245a;
                    if (handler == null) {
                        handler = BackgroundThread.getHandler();
                    }
                    handler.post(new Runnable() { // from class: com.miui.powerkeeper.utils.AlarmControlManager.2
                        @Override // java.lang.Runnable
                        public void run() {
                            PowerKeeperInterface.i iVar = aVar.f246b;
                            if (iVar != null) {
                                iVar.onAlarmHappen(jElapsedRealtime);
                            }
                        }
                    });
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void onAlarm() {
        PowerKeeperManager.getHandler().post(new Runnable() { // from class: com.miui.powerkeeper.utils.AlarmControlManager.1
            @Override // java.lang.Runnable
            public void run() {
                AlarmControlManager.this.notifyAlarmHappenListeners();
            }
        });
    }

    public void registerAlarmHappenListener(PowerKeeperInterface.a aVar) {
        synchronized (this.mAlarmHappenCallbacks) {
            try {
                if (!this.mAlarmHappenCallbacks.contains(aVar)) {
                    this.mAlarmHappenCallbacks.add(aVar);
                }
                if (this.mAlarmHappenCallbacks.size() == 1) {
                    enableAlarm();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void unregisterAlarmHappenListener(PowerKeeperInterface.a aVar) {
        synchronized (this.mAlarmHappenCallbacks) {
            try {
                if (this.mAlarmHappenCallbacks.contains(aVar)) {
                    this.mAlarmHappenCallbacks.remove(aVar);
                }
                if (this.mAlarmHappenCallbacks.size() == 0) {
                    disableAlarm();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
