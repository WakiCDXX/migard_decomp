package com.xiaomi.xmpush.thrift;

/* JADX INFO: loaded from: classes.dex */
public enum ConfigListType {
    MISC_CONFIG(1),
    PLUGIN_CONFIG(2);

    private final int value;

    ConfigListType(int i2) {
        this.value = i2;
    }

    public static ConfigListType findByValue(int i2) {
        if (i2 == 1) {
            return MISC_CONFIG;
        }
        if (i2 != 2) {
            return null;
        }
        return PLUGIN_CONFIG;
    }

    public int getValue() {
        return this.value;
    }
}
