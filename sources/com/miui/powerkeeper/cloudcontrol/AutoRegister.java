package com.miui.powerkeeper.cloudcontrol;

import android.content.Context;
import com.miui.powerkeeper.PowerKeeperApplication;

/* JADX INFO: loaded from: classes.dex */
public abstract class AutoRegister implements IUseCloud {
    public AutoRegister() {
        CloudControlManager.getInstance().registerListener(this, Integer.valueOf(getModel()));
    }

    protected static Context getContext() {
        return PowerKeeperApplication.b();
    }

    protected abstract int getModel();
}
