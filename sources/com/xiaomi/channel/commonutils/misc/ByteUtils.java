package com.xiaomi.channel.commonutils.misc;

import com.miui.powerkeeper.powerchecker.BatteryStatsHelper;

/* JADX INFO: loaded from: classes.dex */
public class ByteUtils {
    private ByteUtils() {
    }

    public static byte[] parseInt(int i2) {
        return new byte[]{(byte) (i2 >> 24), (byte) (i2 >> 16), (byte) (i2 >> 8), (byte) i2};
    }

    public static int toInt(byte[] bArr) {
        if (bArr.length != 4) {
            throw new IllegalArgumentException("the length of bytes must be 4");
        }
        return (bArr[3] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL) | ((bArr[0] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL) << 24) | ((bArr[1] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL) << 16) | ((bArr[2] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL) << 8);
    }
}
