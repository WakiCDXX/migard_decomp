package com.miui.powerkeeper.statemachine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.TelephonyManager;
import com.miui.powerkeeper.event.EventsAggregator;
import com.xiaomi.push.service.clientReport.ReportConstants;
import miui.yellowpage.YellowPageContract;

/* JADX INFO: loaded from: classes.dex */
public class CallStateReceiver extends BroadcastReceiver {
    private EventsAggregator mEventsAggregator;
    private boolean mIsInCall;
    private boolean mIsOffHookInCall;

    public CallStateReceiver(Context context, EventsAggregator eventsAggregator) {
        this.mIsInCall = TelephonyManager.from(context).getCallState() != 0;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        context.registerReceiver(this, intentFilter, 2);
        this.mEventsAggregator = eventsAggregator;
        eventsAggregator.setObserver(this);
    }

    public boolean isInCall() {
        return this.mIsInCall;
    }

    public boolean isOffHookInCall() {
        return this.mIsOffHookInCall;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        String stringExtra = intent.getStringExtra(YellowPageContract.Permission.STATE);
        String str = TelephonyManager.EXTRA_STATE_RINGING;
        boolean z2 = stringExtra.equals(str) || stringExtra.equals(TelephonyManager.EXTRA_STATE_OFFHOOK);
        boolean zEquals = stringExtra.equals(TelephonyManager.EXTRA_STATE_OFFHOOK);
        if (z2 != this.mIsInCall) {
            this.mIsInCall = z2;
            if (z2) {
                PowerStateMachineProxy.getInstance().sendEvent(4000, null);
            } else {
                PowerStateMachineProxy.getInstance().sendEvent(ReportConstants.CLIENT_CRASH, null);
            }
            this.mEventsAggregator.notifyForCallState(this.mIsInCall);
        }
        if ((this.mIsOffHookInCall && stringExtra.equals(str)) || zEquals == this.mIsOffHookInCall) {
            return;
        }
        this.mIsOffHookInCall = zEquals;
        this.mEventsAggregator.notifyForOffHookCallState(zEquals);
    }
}
