package com.miui.powerkeeper.thermal.setting;

/* JADX INFO: loaded from: classes.dex */
public interface IProvider<T> {
    void registerCallback(IProviderCallback<T> iProviderCallback);

    void unregisterCallback(IProviderCallback<T> iProviderCallback);
}
