package com.xiaomi.channel.commonutils.network;

import com.miui.powerkeeper.powerchecker.BatteryStatsHelper;
import com.xiaomi.channel.commonutils.string.UrlBase64Coder;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import miui.util.CoderUtils;
import miui.util.HashUtils;

/* JADX INFO: loaded from: classes.dex */
public class AESEncryption {

    public static class AESDecodeException extends Exception {
        private static final long serialVersionUID = 8671822568355001199L;

        public AESDecodeException(String str) {
            super(str);
        }

        public AESDecodeException(String str, Exception exc) {
            super(str, exc);
        }
    }

    public static class AESEncodeException extends Exception {
        private static final long serialVersionUID = 4826692804389845727L;

        public AESEncodeException(String str) {
            super(str);
        }

        public AESEncodeException(String str, Exception exc) {
            super(str, exc);
        }
    }

    public static String byte2hex(byte[] bArr) {
        String str = "";
        for (byte b2 : bArr) {
            String hexString = Integer.toHexString(b2 & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL);
            str = hexString.length() == 1 ? str + "0" + hexString : str + hexString;
        }
        return str.toUpperCase();
    }

    public static byte[] decrypt(byte[] bArr, byte[] bArr2) throws AESDecodeException {
        try {
            if (bArr2 == null) {
                throw new AESEncodeException("Key为空null");
            }
            if (bArr2.length != 16) {
                throw new AESEncodeException("Key长度不是16位");
            }
            SecretKeySpec secretKeySpec = new SecretKeySpec(bArr2, "AES");
            Cipher cipher = Cipher.getInstance(CoderUtils.AES_ALGORITHM);
            try {
                MessageDigest messageDigest = MessageDigest.getInstance(HashUtils.MD5);
                messageDigest.update(bArr2, 0, bArr2.length);
                cipher.init(2, secretKeySpec, new IvParameterSpec(messageDigest.digest()));
                return cipher.doFinal(bArr);
            } catch (NoSuchAlgorithmException unused) {
                return null;
            }
        } catch (Exception e2) {
            throw new AESDecodeException("AES加密错误", e2);
        }
    }

    public static byte[] encrypt(String str, byte[] bArr) throws AESEncodeException {
        try {
            if (bArr == null) {
                throw new AESEncodeException("Key为空null");
            }
            if (bArr.length != 16) {
                throw new AESEncodeException("Key长度不是16位");
            }
            SecretKeySpec secretKeySpec = new SecretKeySpec(bArr, "AES");
            Cipher cipher = Cipher.getInstance(CoderUtils.AES_ALGORITHM);
            try {
                MessageDigest messageDigest = MessageDigest.getInstance(HashUtils.MD5);
                messageDigest.update(bArr, 0, bArr.length);
                cipher.init(1, secretKeySpec, new IvParameterSpec(messageDigest.digest()));
                return cipher.doFinal(str.getBytes());
            } catch (NoSuchAlgorithmException unused) {
                return null;
            }
        } catch (Exception e2) {
            throw new AESEncodeException("AES加密错误", e2);
        }
    }

    public static byte[] hex2byte(String str) {
        if (str == null) {
            return null;
        }
        int length = str.length();
        if (length % 2 == 1) {
            return null;
        }
        int i2 = length / 2;
        byte[] bArr = new byte[i2];
        for (int i3 = 0; i3 != i2; i3++) {
            int i4 = i3 * 2;
            bArr[i3] = (byte) Integer.parseInt(str.substring(i4, i4 + 2), 16);
        }
        return bArr;
    }

    public static String hexDecrypt(String str, String str2) throws AESDecodeException, UnsupportedEncodingException {
        return new String(decrypt(UrlBase64Coder.decode(str), hex2byte(str2)));
    }

    public static String hexEncrypt(String str, String str2) throws AESEncodeException, UnsupportedEncodingException {
        return new String(UrlBase64Coder.encode(encrypt(str, hex2byte(str2))));
    }

    public static void test() throws AESDecodeException, UnsupportedEncodingException {
    }
}
