package com.miui.powerkeeper.cloudcontrol;

import java.util.Map;
import java.util.function.Function;

/* JADX INFO: compiled from: R8$$SyntheticClass */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class g implements Function {
    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        return (IUseCloud) ((Map.Entry) obj).getKey();
    }
}
