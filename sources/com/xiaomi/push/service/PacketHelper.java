package com.xiaomi.push.service;

import android.text.TextUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.string.XMStringUtils;

/* JADX INFO: loaded from: classes.dex */
public class PacketHelper {
    private static final int REGISTRATION_PACKET_ID_LENGTH = 32;
    private static final int REGISTRATION_PACKET_ID_SHOW_LENGTH = 8;
    private static String prefix = "";
    private static long sCurMsgId;

    public static String filterRegistrationPacketIdForLog(String str) {
        if (TextUtils.isEmpty(str) || str.length() < 32) {
            return str;
        }
        try {
            return "BlockId_" + str.substring(8);
        } catch (Exception e2) {
            MyLog.e("Exception occurred when filtering registration packet id for log. " + e2);
            return "UnexpectedId";
        }
    }

    public static String generatePacketID() {
        if (TextUtils.isEmpty(prefix)) {
            prefix = XMStringUtils.generateRandomString(4);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        long j2 = sCurMsgId;
        sCurMsgId = 1 + j2;
        sb.append(j2);
        return sb.toString();
    }

    public static String generatePacketIdForRegistration() {
        return XMStringUtils.generateRandomString(32);
    }
}
