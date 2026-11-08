package com.miui.powerkeeper.thermal.processor;

import android.annotation.NonNull;
import android.content.Intent;
import com.miui.powerkeeper.cloudcontrol.AutoRegister;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class CGameProcessor extends GameProcessor {
    private static final String EXTRA_CALL = "GameAndCall";
    private static final String EXTRA_LEVEL = "leave";
    private static final String EXTRA_NAME = "scenario_name";
    private static final String EXTRA_WECHAT = "GameAndWechat";
    private boolean mInCGame = false;
    private final Map<Integer, String> mExtraInfo = new HashMap(2);

    private void onScenarioChange(int i2) {
        boolean z2;
        Intent intent = new Intent("com.miui.powerkeeper.Thermal_C_GAME");
        if (this.mExtraInfo.containsKey(Integer.valueOf(i2))) {
            intent.putExtra("scenario_name", this.mExtraInfo.get(Integer.valueOf(i2)));
            z2 = true;
        } else {
            intent.putExtra("scenario_name", EXTRA_LEVEL);
            z2 = false;
        }
        if (z2 || this.mInCGame) {
            AutoRegister.getContext().sendBroadcast(intent);
        }
        this.mInCGame = z2;
    }

    @Override // com.miui.powerkeeper.thermal.processor.GameProcessor, com.miui.powerkeeper.thermal.processor.IProcessor
    public void execute(int i2) {
        super.execute(i2);
        onScenarioChange(i2);
    }

    @Override // com.miui.powerkeeper.thermal.processor.GameProcessor, com.miui.powerkeeper.thermal.processor.ThermalProcessor
    public void init(Map<String, Set<Integer>> map) {
        Iterator<Integer> it = map.get("wechatGame").iterator();
        while (it.hasNext()) {
            this.mExtraInfo.put(it.next(), EXTRA_WECHAT);
        }
        Iterator<Integer> it2 = map.get("callGame").iterator();
        while (it2.hasNext()) {
            this.mExtraInfo.put(it2.next(), EXTRA_CALL);
        }
        super.init(map);
    }

    @Override // com.miui.powerkeeper.thermal.processor.GameProcessor
    @NonNull
    public String toString() {
        return super.toString() + "@c_game \nextraInfo: " + this.mExtraInfo.toString();
    }
}
