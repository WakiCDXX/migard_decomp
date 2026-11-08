package com.xiaomi.xmpush.thrift;

/* JADX INFO: loaded from: classes.dex */
public enum ConfigType {
    INT(1),
    LONG(2),
    STRING(3),
    BOOLEAN(4);

    private final int value;

    ConfigType(int i2) {
        this.value = i2;
    }

    public static ConfigType findByValue(int i2) {
        if (i2 == 1) {
            return INT;
        }
        if (i2 == 2) {
            return LONG;
        }
        if (i2 == 3) {
            return STRING;
        }
        if (i2 != 4) {
            return null;
        }
        return BOOLEAN;
    }

    public int getValue() {
        return this.value;
    }
}
