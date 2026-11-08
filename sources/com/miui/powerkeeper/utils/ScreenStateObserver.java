package com.miui.powerkeeper.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IPowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import com.android.internal.os.BackgroundThread;
import com.google.android.collect.Lists;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.powerchecker.NightStandbyRecord;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ScreenStateObserver {
    private Context mContext;
    EventsAggregator mEventsAggregator;
    private long mLastSceenOffTime;
    private final IPowerManager mPowerManager;
    private boolean mScreenOn;
    private List<PowerKeeperInterface.f0> mScreenChangedCallbacks = Lists.newArrayList();
    private final Object mRulesLock = new Object();
    private BroadcastReceiver mScreenReceiver = new AnonymousClass1();

    /* JADX INFO: renamed from: com.miui.powerkeeper.utils.ScreenStateObserver$1, reason: invalid class name */
    class AnonymousClass1 extends BroadcastReceiver {
        AnonymousClass1() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onReceive$0(boolean z2) {
            ScreenStateObserver.this.mEventsAggregator.notifyScreenStateChanged(z2);
            ScreenStateObserver.this.notifyScreenChangedListeners();
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            final boolean z2;
            if (intent == null) {
                return;
            }
            String action = intent.getAction();
            if (action.equals("android.intent.action.SCREEN_ON")) {
                z2 = true;
            } else if (!action.equals("android.intent.action.SCREEN_OFF")) {
                return;
            } else {
                z2 = false;
            }
            synchronized (ScreenStateObserver.this.mRulesLock) {
                try {
                    if (z2 != ScreenStateObserver.this.mScreenOn) {
                        ScreenStateObserver.this.mScreenOn = z2;
                        new Handler().post(new Runnable() { // from class: com.miui.powerkeeper.utils.c
                            @Override // java.lang.Runnable
                            public final void run() {
                                this.f676a.lambda$onReceive$0(z2);
                            }
                        });
                    }
                    ScreenStateObserver screenStateObserver = ScreenStateObserver.this;
                    screenStateObserver.mLastSceenOffTime = screenStateObserver.mScreenOn ? 0L : SystemClock.elapsedRealtime();
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    public ScreenStateObserver(Context context, EventsAggregator eventsAggregator) {
        this.mContext = context;
        this.mEventsAggregator = eventsAggregator;
        IPowerManager iPowerManagerAsInterface = IPowerManager.Stub.asInterface(ServiceManager.getService(NightStandbyRecord.KEY_POWER));
        this.mPowerManager = iPowerManagerAsInterface;
        try {
            this.mScreenOn = iPowerManagerAsInterface.isInteractive();
        } catch (RemoteException unused) {
        }
        this.mLastSceenOffTime = this.mScreenOn ? 0L : SystemClock.elapsedRealtime();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        this.mContext.registerReceiverAsUser(this.mScreenReceiver, UserHandle.ALL, intentFilter, null, null, 4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyScreenChangedListeners() {
        synchronized (this.mRulesLock) {
            try {
                for (final PowerKeeperInterface.f0 f0Var : this.mScreenChangedCallbacks) {
                    Handler handler = f0Var.f262a;
                    if (handler == null) {
                        handler = BackgroundThread.getHandler();
                    }
                    handler.post(new Runnable() { // from class: com.miui.powerkeeper.utils.ScreenStateObserver.2
                        @Override // java.lang.Runnable
                        public void run() {
                            PowerKeeperInterface.z zVar = f0Var.f263b;
                            if (zVar != null) {
                                zVar.onScreenChanged(ScreenStateObserver.this.mScreenOn);
                            }
                        }
                    });
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public long getLastScreenOffTime() {
        long j2;
        synchronized (this.mRulesLock) {
            j2 = this.mLastSceenOffTime;
        }
        return j2;
    }

    public boolean isScreenOn() {
        boolean z2;
        synchronized (this.mRulesLock) {
            z2 = this.mScreenOn;
        }
        return z2;
    }

    @Deprecated
    public void registerScreenChangedListener(final PowerKeeperInterface.f0 f0Var) {
        synchronized (this.mRulesLock) {
            try {
                if (!this.mScreenChangedCallbacks.contains(f0Var)) {
                    this.mScreenChangedCallbacks.add(f0Var);
                    Handler handler = f0Var.f262a;
                    if (handler == null) {
                        handler = BackgroundThread.getHandler();
                    }
                    handler.post(new Runnable() { // from class: com.miui.powerkeeper.utils.ScreenStateObserver.3
                        @Override // java.lang.Runnable
                        public void run() {
                            PowerKeeperInterface.z zVar = f0Var.f263b;
                            if (zVar != null) {
                                zVar.onRegistered(ScreenStateObserver.this.mScreenOn);
                            }
                        }
                    });
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void unregisterScreenChangedListener(PowerKeeperInterface.f0 f0Var) {
        synchronized (this.mRulesLock) {
            try {
                if (this.mScreenChangedCallbacks.contains(f0Var)) {
                    this.mScreenChangedCallbacks.remove(f0Var);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
