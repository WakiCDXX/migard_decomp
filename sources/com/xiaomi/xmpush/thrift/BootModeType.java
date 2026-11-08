package com.xiaomi.xmpush.thrift;

/* JADX INFO: loaded from: classes.dex */
public enum BootModeType {
    START(0),
    BIND(1);

    private final int value;

    BootModeType(int i2) {
        this.value = i2;
    }

    public static BootModeType findByValue(int i2) {
        if (i2 == 0) {
            return START;
        }
        if (i2 != 1) {
            return null;
        }
        return BIND;
    }

    public int getValue() {
        return this.value;
    }
}
