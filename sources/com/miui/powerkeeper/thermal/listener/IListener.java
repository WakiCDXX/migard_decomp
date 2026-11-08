package com.miui.powerkeeper.thermal.listener;

import android.os.Handler;
import com.miui.powerkeeper.thermal.resource.IDumpable;

/* JADX INFO: loaded from: classes.dex */
public interface IListener extends IDumpable {
    int initDefaultValue();

    void register(Handler handler, int i2);

    void uninstall();
}
