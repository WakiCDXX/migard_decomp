package com.miui.powerkeeper.perfengine;

import android.os.Build;
import miui.process.ForegroundInfo;

/* JADX INFO: loaded from: classes.dex */
public abstract class SchedController implements c, b {
    public boolean b(String str) {
        return false;
    }

    protected String c() {
        return Build.DEVICE;
    }

    @Override // com.miui.powerkeeper.perfengine.c
    public void a(ForegroundInfo foregroundInfo) {
    }
}
