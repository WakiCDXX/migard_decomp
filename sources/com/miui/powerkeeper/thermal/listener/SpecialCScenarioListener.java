package com.miui.powerkeeper.thermal.listener;

import android.os.Build;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.thermal.resource.ScenarioConfig;
import java.util.HashSet;
import java.util.Set;
import miui.process.ForegroundInfo;

/* JADX INFO: loaded from: classes.dex */
public class SpecialCScenarioListener extends ElementListener {
    private static final int STATE_COMPOUND_GAME = 2;
    private static final int STATE_FLOAT_VIDEO = 1;
    private final Set<String> mSpecialForegroundSet;
    private boolean mGameState = false;
    private boolean mFloatState = false;

    public SpecialCScenarioListener() {
        HashSet hashSet = new HashSet();
        this.mSpecialForegroundSet = hashSet;
        hashSet.add("com.tencent.tmgp.sgame");
        hashSet.add("com.mobile.legends");
        hashSet.add("com.vng.mlbbvn");
        hashSet.add("com.mobilelegends.bp");
        hashSet.add("com.mobilelegends.hwag");
        hashSet.add("com.mobilelegends.amazon");
        hashSet.add("com.mobilelegends.shareit");
        hashSet.add("com.dfjz.moba");
        hashSet.add("com.mlbbcn.moba");
        hashSet.add("com.miHoYo.Yuanshen");
        hashSet.add("com.miHoYo.ys.mi");
        hashSet.add("com.miHoYo.ys.bilibili");
        hashSet.add("com.miHoYo.GenshinImpact");
        hashSet.add("com.tencent.tmpg.pubghmd");
        hashSet.add("com.tencent.ig");
    }

    private void onCurrentStateChanged() {
        int i2;
        if (this.mFloatState) {
            i2 = this.mGameState ? 2 : 1;
        } else {
            i2 = 99;
        }
        modifyCurrentState(i2);
    }

    private void onForegroundChanged(String str) {
        this.mGameState = this.mSpecialForegroundSet.contains(str);
    }

    private void onSmartChanged(long j2) {
        this.mFloatState = ((4 & j2) == 0 && (ScenarioConfig.INTERACTIVE_ACTION_TYPE_VIDEOCALL_WECHAT & j2) == 0 && (32 & j2) == 0 && (ScenarioConfig.INTERACTIVE_ACTION_TYPE_VIDEOCALL_DINGTALK & j2) == 0 && (128 & j2) == 0 && (ScenarioConfig.INTERACTIVE_ACTION_TYPE_VIDEOCALL_WHATSAPP & j2) == 0 && (64 & j2) == 0 && (ScenarioConfig.INTERACTIVE_ACTION_TYPE_VIDEOCALL_WEWORK & j2) == 0 && (256 & j2) == 0 && (ScenarioConfig.INTERACTIVE_ACTION_TYPE_VIDEOCALL_LARK & j2) == 0 && (8 & j2) == 0 && (j2 & ScenarioConfig.INTERACTIVE_ACTION_TYPE_VIDEOCALL_QQ) == 0) ? false : true;
    }

    @Override // com.miui.powerkeeper.thermal.listener.IElement
    public void elementChanged(int i2, Object obj, int i3, int i4) {
        if (i2 == -34) {
            onSmartChanged(((Long) obj).longValue());
        } else if (i2 != -9) {
            return;
        } else {
            onForegroundChanged(((ForegroundInfo) obj).mForegroundPackageName);
        }
        onCurrentStateChanged();
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener
    void init() {
        if (Build.DEVICE.equals("manet")) {
            this.mSpecialForegroundSet.add("com.tencent.tmgp.pubgmhd");
        }
        EventsAggregator eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
        eventsAggregator.registerSmartPowerScenarioIdChanged(this.mElementHandler);
        eventsAggregator.registerForForegroundInfo(this.mElementHandler);
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener
    public int initCurrentState() {
        return 99;
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener, com.miui.powerkeeper.thermal.listener.IListener
    public void uninstall() {
        EventsAggregator eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
        eventsAggregator.unregisterSmartPowerScenarioIdChanged(this.mElementHandler);
        eventsAggregator.unregisterForForegroundInfo(this.mElementHandler);
    }
}
