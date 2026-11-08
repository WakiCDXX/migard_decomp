package com.miui.powerkeeper.thermal.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.miui.powerkeeper.cloudcontrol.AutoRegister;

/* JADX INFO: loaded from: classes.dex */
public class LowTempChargeListener extends ElementListener {
    private static final String ACTION_PK_LOW_TEMP_CHARGE = "miui.intent.action.ACTION_POWER_KEEPER_LOW_TEMP_FAST_CHARGING";
    private static final int DEFAULT_VALUE = 0;
    private static final String EXTRA_PK_LOW_TEMP_CHARGE = "miui.intent.extra.ACTION_POWER_KEEPER_LOW_TEMP_FAST_CHARGING";
    private static final String TAG = "LowTempChargeListener";
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.miui.powerkeeper.thermal.listener.LowTempChargeListener.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null || !intent.getAction().equals(LowTempChargeListener.ACTION_PK_LOW_TEMP_CHARGE)) {
                return;
            }
            int intExtra = intent.getIntExtra(LowTempChargeListener.EXTRA_PK_LOW_TEMP_CHARGE, 0);
            LowTempChargeListener.this.modifyCurrentState(intExtra == 1 ? intExtra : 0);
        }
    };

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener
    void init() {
        AutoRegister.getContext().registerReceiver(this.mReceiver, new IntentFilter(ACTION_PK_LOW_TEMP_CHARGE));
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener
    public int initCurrentState() {
        return 0;
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
