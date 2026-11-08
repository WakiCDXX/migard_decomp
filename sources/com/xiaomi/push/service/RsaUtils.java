package com.xiaomi.push.service;

import android.text.TextUtils;
import android.util.Base64;
import com.xiaomi.channel.commonutils.logger.MyLog;
import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import miui.util.RSAUtils;

/* JADX INFO: loaded from: classes.dex */
public class RsaUtils {
    private static final String CHARSET = "UTF-8";
    private static final byte[] DEFAULT_RSA_PUB_KEY;
    static final String KEY_PAIR_VERSION = "1";
    private static final String RSA_ALGORITHM = "RSA";
    private static RSAPublicKey publicKey;

    static {
        byte[] bArr = {48, -127, -97, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 3, -127, -115, 0, 48, -127, -119, 2, -127, -127, 0, -109, -38, -114, 26, -72, 78, 16, 70, -90, 113, -30, 36, 85, -3, -43, 123, 61, -98, 4, -16, 67, 19, -90, -73, -5, -89, 36, 44, -27, 59, -123, 72, -73, -48, 49, 13, 16, 50, -27, -82, 18, -28, 84, 0, -41, 16, 69, -39, 7, 82, 56, 79, -37, 40, 85, 107, 98, 33, 123, -34, -49, 111, -11, 49, 28, 117, -74, 114, -122, -29, -84, 82, 22, -122, 42, -40, -79, 18, -116, -42, 101, -70, 44, 11, 62, -49, -3, -22, -2, 66, 90, -116, -75, -99, 34, 121, 69, 10, -81, -57, 89, -23, -36, -60, -81, 67, -114, 10, 79, 100, 29, 47, -24, 110, -66, -7, 87, 16, -125, -91, -43, -103, 67, -20, 41, 117, -37, -11, 2, 3, 1, 0, 1};
        DEFAULT_RSA_PUB_KEY = bArr;
        try {
            publicKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(bArr));
        } catch (Throwable unused) {
            MyLog.e("rsa key pair init failure!!!");
        }
    }

    public static String rsaEncrypt(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            Cipher cipher = Cipher.getInstance(RSAUtils.CIPHER_RSA);
            cipher.init(1, publicKey);
            return Base64.encodeToString(rsaSplitCodec(cipher, 1, str.getBytes(CHARSET), publicKey.getModulus().bitLength()), 2);
        } catch (Throwable unused) {
            return null;
        }
    }

    private static byte[] rsaSplitCodec(Cipher cipher, int i2, byte[] bArr, int i3) {
        if (cipher == null || bArr == null) {
            return null;
        }
        int i4 = i2 == 2 ? i3 / 8 : (i3 / 8) - 11;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int i5 = 0;
            int i6 = 0;
            while (bArr.length > i5) {
                byte[] bArrDoFinal = bArr.length - i5 > i4 ? cipher.doFinal(bArr, i5, i4) : cipher.doFinal(bArr, i5, bArr.length - i5);
                byteArrayOutputStream.write(bArrDoFinal, 0, bArrDoFinal.length);
                i6++;
                i5 = i6 * i4;
            }
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e2) {
            throw new RuntimeException(e2);
        }
    }

    static boolean testDecrypt() {
        return true;
    }

    static boolean testEncrypt() {
        return true;
    }
}
