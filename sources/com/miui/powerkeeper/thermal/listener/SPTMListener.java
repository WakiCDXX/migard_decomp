package com.miui.powerkeeper.thermal.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import com.miui.powerkeeper.cloudcontrol.AutoRegister;
import com.miui.powerkeeper.cloudcontrol.CloudFunctionConfig;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.thermal.resource.ScenarioConfig;
import com.miui.powerkeeper.utils.Constant;

/* JADX INFO: loaded from: classes.dex */
public class SPTMListener extends ElementListener {
    private boolean mEnable;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.miui.powerkeeper.thermal.listener.SPTMListener.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (SPTMListener.this.mEnable && intent != null && intent.getAction() != null && intent.getAction().equals(ScenarioConfig.ACTION_SPTM_STATE)) {
                SPTMListener.this.modifyCurrentState(intent.getBooleanExtra("sptm_state", false) ? 98 : 99);
            }
        }
    };

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener
    void init() {
        AutoRegister.getContext().registerReceiver(this.mReceiver, new IntentFilter(ScenarioConfig.ACTION_SPTM_STATE), 2);
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

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener, com.miui.powerkeeper.cloudcontrol.IUseCloud
    public void updateCloudData() {
        for (String str : CloudFunctionConfig.getFunctionParameters(AutoRegister.getContext(), Constant.FUNC_THERMAL_SPTM_CONFIG_ENABLE, "").trim().split(";")) {
            if (str.equals(Build.DEVICE) || str.equals("ALL_DEVICES")) {
                this.mEnable = SimpleSettings.Misc.getBoolean(AutoRegister.getContext(), Constant.FUNC_THERMAL_SPTM_CONFIG_ENABLE, false);
            }
        }
        this.mEnable = false;
    }

    @Override // com.miui.powerkeeper.thermal.listener.IElement
    public void elementChanged(int i2, Object obj, int i3, int i4) {
    }
}
