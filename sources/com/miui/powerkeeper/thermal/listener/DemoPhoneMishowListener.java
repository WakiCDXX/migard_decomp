package com.miui.powerkeeper.thermal.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.miui.powerkeeper.cloudcontrol.AutoRegister;
import com.miui.powerkeeper.thermal.resource.ScenarioConfig;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class DemoPhoneMishowListener extends ElementListener {
    private static final int DEMO_THERMAL_MODE_CHANGED = 0;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.miui.powerkeeper.thermal.listener.DemoPhoneMishowListener.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null || !Objects.equals(intent.getAction(), ScenarioConfig.ACTION_DEMO_THERMAL_MODE_ON)) {
                return;
            }
            DemoPhoneMishowListener.this.mElementHandler.obtainMessage(0).sendToTarget();
        }
    };

    @Override // com.miui.powerkeeper.thermal.listener.IElement
    public void elementChanged(int i2, Object obj, int i3, int i4) {
        if (i2 != 0) {
            return;
        }
        modifyCurrentState(98);
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener
    void init() {
        AutoRegister.getContext().registerReceiver(this.mReceiver, new IntentFilter(ScenarioConfig.ACTION_DEMO_THERMAL_MODE_ON), 2);
        this.mElementHandler.postDelayed(new Runnable() { // from class: com.miui.powerkeeper.thermal.listener.DemoPhoneMishowListener.2
            @Override // java.lang.Runnable
            public void run() {
                Intent intent = new Intent(ScenarioConfig.ACTION_DEMO_PHONE_LISTENER_INIT);
                intent.setPackage("com.xiaomi.mihomemanager");
                AutoRegister.getContext().sendBroadcast(intent);
            }
        }, 1000L);
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener
    public int initCurrentState() {
        return 99;
    }
}
