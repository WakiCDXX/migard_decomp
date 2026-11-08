package com.xiaomi.channel.commonutils.Math;

/* JADX INFO: loaded from: classes.dex */
public class Random {
    private static final java.util.Random random = new java.util.Random();

    public static final boolean randomBoolean() {
        return random.nextBoolean();
    }

    public static final int randomInt(int i2) {
        return random.nextInt(Math.abs(i2));
    }
}
