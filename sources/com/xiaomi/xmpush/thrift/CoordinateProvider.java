package com.xiaomi.xmpush.thrift;

/* JADX INFO: loaded from: classes.dex */
public enum CoordinateProvider {
    Baidu(0),
    Tencent(1),
    AutoNavi(2),
    Google(3),
    GPS(4);

    private final int value;

    CoordinateProvider(int i2) {
        this.value = i2;
    }

    public static CoordinateProvider findByValue(int i2) {
        if (i2 == 0) {
            return Baidu;
        }
        if (i2 == 1) {
            return Tencent;
        }
        if (i2 == 2) {
            return AutoNavi;
        }
        if (i2 == 3) {
            return Google;
        }
        if (i2 != 4) {
            return null;
        }
        return GPS;
    }

    public int getValue() {
        return this.value;
    }
}
