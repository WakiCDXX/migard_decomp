package com.miui.powerkeeper.cloudcontrol;

import java.util.function.Consumer;

/* JADX INFO: compiled from: R8$$SyntheticClass */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class h implements Consumer {
    @Override // java.util.function.Consumer
    public final void accept(Object obj) {
        ((IUseCloud) obj).updateCloudData();
    }
}
