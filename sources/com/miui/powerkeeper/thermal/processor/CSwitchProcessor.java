package com.miui.powerkeeper.thermal.processor;

import android.annotation.NonNull;

/* JADX INFO: loaded from: classes.dex */
public class CSwitchProcessor extends SwitchProcessor {
    private static final int C_SCENARIO_BOUNDARY = 700;

    @Override // com.miui.powerkeeper.thermal.processor.SwitchProcessor
    protected int mapToThermalId(int i2) {
        if (i2 >= 900) {
            if (i2 - 900 >= 700) {
                return i2;
            }
        } else if (i2 >= 700) {
            return i2;
        }
        return super.mapToThermalId(i2);
    }

    @Override // com.miui.powerkeeper.thermal.processor.SwitchProcessor
    @NonNull
    public String toString() {
        return "--- CSwitchProcessor \n" + super.toString();
    }
}
