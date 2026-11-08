package com.miui.powerkeeper.statemachine;

import com.miui.powerkeeper.thermal.setting.IProviderCallback;
import com.miui.powerkeeper.thermal.setting.SettingDTO;

/* JADX INFO: compiled from: R8$$SyntheticClass */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class z implements IProviderCallback {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ PowerSaveModeHandler.ThermalScenarioObserver f561a;

    public /* synthetic */ z(PowerSaveModeHandler.ThermalScenarioObserver thermalScenarioObserver) {
        this.f561a = thermalScenarioObserver;
    }

    @Override // com.miui.powerkeeper.thermal.setting.IProviderCallback
    public final void onChange(Object obj) {
        this.f561a.onNewScenarioData((SettingDTO) obj);
    }
}
