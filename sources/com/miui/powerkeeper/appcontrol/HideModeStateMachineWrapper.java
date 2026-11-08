package com.miui.powerkeeper.appcontrol;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseIntArray;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;

/* JADX INFO: loaded from: classes.dex */
public class HideModeStateMachineWrapper implements PowerKeeperInterface.l {
    protected static final boolean DEBUG = Build.IS_DEBUGGABLE;
    protected String TAG = HideModeStateMachineWrapper.class.getSimpleName();
    protected Context mContext;
    protected String mName;
    protected PowerKeeperManager mPowerKeeperManager;

    public HideModeStateMachineWrapper(Context context, PowerKeeperManager powerKeeperManager, String str, String str2, Bundle bundle) {
        this.mContext = context;
        this.mPowerKeeperManager = powerKeeperManager;
        this.mName = str;
        HideModeStateMachineConfig.getInstance().addFeature(str2, str, bundle);
    }

    @Override // com.miui.powerkeeper.PowerKeeperInterface.l
    public void disable() {
        this.mPowerKeeperManager.getHideModeStateMachine().setFeatureEnable(this.mName, false);
    }

    @Override // com.miui.powerkeeper.PowerKeeperInterface.l
    public void enable() {
        this.mPowerKeeperManager.getHideModeStateMachine().setFeatureEnable(this.mName, true);
    }

    @Override // com.miui.powerkeeper.PowerKeeperInterface.l
    public int getAppRule(int i2) {
        return this.mPowerKeeperManager.getHideModeStateMachine().getUidRule(this.mName, i2);
    }

    @Override // com.miui.powerkeeper.PowerKeeperInterface.l
    public SparseIntArray getAppsRule(int[] iArr) {
        return this.mPowerKeeperManager.getHideModeStateMachine().getUidsRule(this.mName, iArr);
    }

    public Bundle getUidPolicy(int i2) {
        return null;
    }

    @Override // com.miui.powerkeeper.PowerKeeperInterface.l
    public void registerAppRuleChangeListener(PowerKeeperInterface.c cVar) {
        this.mPowerKeeperManager.getHideModeStateMachine().registerAppRuleChangeListener(this.mName, cVar);
    }

    @Override // com.miui.powerkeeper.PowerKeeperInterface.l
    public void setUidPolicy(int i2, Bundle bundle) {
        this.mPowerKeeperManager.getHideModeStateMachine().setUidPolicy(this.mName, i2, bundle);
    }

    @Override // com.miui.powerkeeper.PowerKeeperInterface.l
    public void unregisterAppRuleChangeListener(PowerKeeperInterface.c cVar) {
        this.mPowerKeeperManager.getHideModeStateMachine().unregisterAppRuleChangeListener(this.mName, cVar);
    }
}
