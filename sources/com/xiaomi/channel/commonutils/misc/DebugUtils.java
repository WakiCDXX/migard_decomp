package com.xiaomi.channel.commonutils.misc;

import android.content.Context;
import com.miui.powerkeeper.powerchecker.BatteryStatsHelper;

/* JADX INFO: loaded from: classes.dex */
public class DebugUtils {
    static final char[] HEXCHARS = "0123456789ABCDEF".toCharArray();

    public static String bytes2Hex(byte[] bArr, int i2, int i3) {
        StringBuilder sb = new StringBuilder(i3 * 2);
        for (int i4 = 0; i4 < i3; i4++) {
            byte b2 = bArr[i2 + i4];
            int i5 = b2 & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL;
            char[] cArr = HEXCHARS;
            sb.append(cArr[i5 >> 4]);
            sb.append(cArr[b2 & 15]);
        }
        return sb.toString();
    }

    public static boolean isTesting(Context context) {
        return DebugSwitch.sDebugServerHost;
    }
}
