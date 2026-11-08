package com.miui.powerkeeper.thermal.listener;

import com.miui.powerkeeper.thermal.resource.ScenarioConfig;

/* JADX INFO: loaded from: classes.dex */
public class FloatWindowListener2 extends FloatWindowListener {
    @Override // com.miui.powerkeeper.thermal.listener.FloatWindowListener, com.miui.powerkeeper.thermal.listener.IElement
    public void elementChanged(int i2, Object obj, int i3, int i4) {
        int i5;
        long jLongValue = ((Long) obj).longValue();
        boolean z2 = true;
        if ((256 & jLongValue) != 0 || (ScenarioConfig.INTERACTIVE_ACTION_TYPE_VIDEOCALL_LARK & jLongValue) != 0) {
            i5 = 4;
        } else if ((64 & jLongValue) != 0 || (ScenarioConfig.INTERACTIVE_ACTION_TYPE_VIDEOCALL_WEWORK & jLongValue) != 0) {
            i5 = 5;
        } else if ((8 & jLongValue) == 0 && (jLongValue & ScenarioConfig.INTERACTIVE_ACTION_TYPE_VIDEOCALL_QQ) == 0) {
            i5 = 99;
            z2 = false;
        } else {
            i5 = 3;
        }
        if (z2) {
            modifyCurrentState(i5);
        } else {
            super.elementChanged(i2, obj, i3, i4);
        }
    }
}
