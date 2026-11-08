package com.miui.powerkeeper.thermal.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.miui.powerkeeper.cloudcontrol.AutoRegister;

/* JADX INFO: loaded from: classes.dex */
public class PogoPinStateListener extends ElementListener {
    private static final String ACTION_POGO_CONNECTED_STATE = "miui.intent.action.ACTION_POGO_CONNECTED_STATE";
    private static final String EXTRA_POGO_CONNECTED_STATE = "miui.intent.extra.EXTRA_POGO_CONNECTED_STATE";
    private final int DEFAULT_VALUE = 99;
    private int mCurrentState = 99;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.miui.powerkeeper.thermal.listener.PogoPinStateListener.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(PogoPinStateListener.ACTION_POGO_CONNECTED_STATE)) {
                int intExtra = intent.getIntExtra(PogoPinStateListener.EXTRA_POGO_CONNECTED_STATE, 0);
                if (intExtra != 1) {
                    intExtra = 99;
                }
                if (intExtra != PogoPinStateListener.this.mCurrentState) {
                    PogoPinStateListener.this.mCurrentState = intExtra;
                    PogoPinStateListener pogoPinStateListener = PogoPinStateListener.this;
                    pogoPinStateListener.modifyCurrentState(pogoPinStateListener.mCurrentState);
                }
            }
        }
    };

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener
    void init() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_POGO_CONNECTED_STATE);
        AutoRegister.getContext().registerReceiver(this.mReceiver, intentFilter, 2);
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener
    public int initCurrentState() {
        return 99;
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener, com.miui.powerkeeper.thermal.listener.IListener
    public void uninstall() {
        super.uninstall();
        AutoRegister.getContext().unregisterReceiver(this.mReceiver);
    }

    @Override // com.miui.powerkeeper.thermal.listener.IElement
    public void elementChanged(int i2, Object obj, int i3, int i4) {
    }
}
