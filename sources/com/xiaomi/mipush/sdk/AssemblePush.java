package com.xiaomi.mipush.sdk;

/* JADX INFO: loaded from: classes.dex */
public enum AssemblePush {
    ASSEMBLE_PUSH_HUAWEI(1),
    ASSEMBLE_PUSH_FCM(2),
    ASSEMBLE_PUSH_COS(3),
    ASSEMBLE_PUSH_FTOS(4);

    private int mValue;

    AssemblePush(int i2) {
        this.mValue = i2;
    }
}
