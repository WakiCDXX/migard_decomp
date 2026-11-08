package com.miui.powerkeeper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class TimeScheduleManager {

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public static final boolean f279f = Log.isLoggable("PowerKeeper.Time", 3);

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private Context f280a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private AlarmManager f281b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private final Object f282c = new Object();

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private BroadcastReceiver f283d = new a();

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private TimeSpan f284e;

    class a extends BroadcastReceiver {
        a() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TimeScheduleManager.f279f) {
                Log.d("PowerKeeper.Time", "receive: " + action);
            }
            if ("miui.intent.action.powerkeeper_schedule".equals(action)) {
                TimeScheduleManager.this.d(System.currentTimeMillis());
            }
        }
    }

    public TimeScheduleManager(Context context) {
        Log.d("PowerKeeper.Time", "TimeScheduleManager created");
        this.f280a = context;
        this.f281b = (AlarmManager) context.getSystemService("alarm");
        this.f284e = new TimeSpan(23, 7);
        this.f280a.registerReceiver(this.f283d, new IntentFilter("miui.intent.action.powerkeeper_schedule"), 2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void c(TimeSpan.a aVar) {
        synchronized (this.f282c) {
            this.f284e.a(aVar);
            d(System.currentTimeMillis());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void d(long j2) {
        synchronized (this.f282c) {
            this.f284e.e(j2);
            f(j2);
        }
    }

    private void f(long j2) {
        if (f279f) {
            Log.d("PowerKeeper.Time", "setNextAlarm");
        }
        PendingIntent broadcast = PendingIntent.getBroadcast(this.f280a, 0, new Intent("miui.intent.action.powerkeeper_schedule"), 67108864);
        this.f281b.setExact(1, this.f284e.b(j2), broadcast);
    }

    public void e(int i2, final TimeSpan.a aVar) {
        synchronized (this.f282c) {
            if (i2 == 0) {
                try {
                    PowerKeeperManager.getHandler().post(new Runnable() { // from class: com.miui.powerkeeper.e
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f394a.c(aVar);
                        }
                    });
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    public void g(int i2, TimeSpan.a aVar) {
        synchronized (this.f282c) {
            if (i2 == 0) {
                try {
                    this.f284e.f(aVar);
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }
}
