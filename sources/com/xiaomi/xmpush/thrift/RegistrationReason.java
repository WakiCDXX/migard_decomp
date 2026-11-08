package com.xiaomi.xmpush.thrift;

/* JADX INFO: loaded from: classes.dex */
public enum RegistrationReason {
    RegIdExpired(0),
    PackageUnregistered(1),
    Init(2);

    private final int value;

    RegistrationReason(int i2) {
        this.value = i2;
    }

    public static RegistrationReason findByValue(int i2) {
        if (i2 == 0) {
            return RegIdExpired;
        }
        if (i2 == 1) {
            return PackageUnregistered;
        }
        if (i2 != 2) {
            return null;
        }
        return Init;
    }

    public int getValue() {
        return this.value;
    }
}
