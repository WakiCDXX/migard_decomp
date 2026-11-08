package com.miui.powerkeeper.thermal.processor;

/* JADX INFO: loaded from: classes.dex */
public class MiShowCGameProcessor extends CGameProcessor {
    @Override // com.miui.powerkeeper.thermal.processor.CGameProcessor, com.miui.powerkeeper.thermal.processor.GameProcessor, com.miui.powerkeeper.thermal.processor.IProcessor
    public void execute(int i2) {
        if (i2 >= 900) {
            i2 -= 900;
        }
        super.execute(i2);
    }
}
