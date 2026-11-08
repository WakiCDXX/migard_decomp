package com.miui.powerkeeper.thermal.processor;

/* JADX INFO: loaded from: classes.dex */
public class MiShowSwitchProcessor extends SwitchProcessor {
    @Override // com.miui.powerkeeper.thermal.processor.SwitchProcessor, com.miui.powerkeeper.thermal.processor.IProcessor
    public void execute(int i2) {
        if (i2 >= 900) {
            i2 -= 900;
        }
        super.execute(i2);
    }
}
