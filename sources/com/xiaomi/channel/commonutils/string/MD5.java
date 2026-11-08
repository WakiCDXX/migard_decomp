package com.xiaomi.channel.commonutils.string;

import android.text.TextUtils;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import miui.util.HashUtils;

/* JADX INFO: loaded from: classes.dex */
public class MD5 {
    public static String MD5_16(String str) {
        return MD5_32(str).subSequence(8, 24).toString();
    }

    public static String MD5_32(String str) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(HashUtils.MD5);
            StringBuffer stringBuffer = new StringBuffer();
            messageDigest.update(str.getBytes(), 0, str.length());
            for (byte b2 : messageDigest.digest()) {
                stringBuffer.append(byte2Hex(b2));
            }
            return stringBuffer.toString();
        } catch (NoSuchAlgorithmException unused) {
            return null;
        }
    }

    private static String byte2Hex(byte b2) {
        int i2 = (b2 & 127) + (b2 < 0 ? 128 : 0);
        StringBuilder sb = new StringBuilder();
        sb.append(i2 < 16 ? "0" : "");
        sb.append(Integer.toHexString(i2).toLowerCase());
        return sb.toString();
    }

    public static byte[] getMd5Digest(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(HashUtils.MD5);
            messageDigest.update(str.getBytes());
            return messageDigest.digest();
        } catch (Exception unused) {
            return null;
        }
    }
}
