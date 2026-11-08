package miui.util;

import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;

/* JADX INFO: loaded from: classes.dex */
public class RSAUtils {
    public static final String CIPHER_RSA = "RSA/ECB/PKCS1Padding";
    private static final int DEFAULT_RADIX = 16;
    public static final int FLAG_CRLF = 4;
    public static final int FLAG_DEFAULT = 0;
    public static final int FLAG_NO_CLOSE = 16;
    public static final int FLAG_NO_PADDING = 1;
    public static final int FLAG_NO_WRAP = 2;
    public static final int FLAG_URL_SAFE = 8;
    public static final String KEY_RSA = "RSA";
    private static final int MAX_DECRYPT_BLOCK = 128;
    private static final int MAX_ENCRYPT_BLOCK = 117;
    public static final String SIGNATURE_MD5_WITH_RSA = "MD5withRSA";
    public static final String SIGNATURE_SHA1_WITH_RSA = "SHA1withRSA";

    private RSAUtils() {
    }

    public static byte[] decrypt(byte[] bArr, Key key, String str) throws Exception {
        return doEncryptOrDecrypt(bArr, key, str, 2);
    }

    private static byte[] doEncryptOrDecrypt(byte[] bArr, Key key, String str, int i2) throws Exception {
        int maxBlock = getMaxBlock(i2);
        Cipher cipher = Cipher.getInstance(str);
        cipher.init(i2, key);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (int i3 = 0; bArr.length - i3 > 0; i3 += maxBlock) {
            byteArrayOutputStream.write(bArr.length - i3 > maxBlock ? cipher.doFinal(bArr, i3, maxBlock) : cipher.doFinal(bArr, i3, bArr.length - i3));
        }
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        return byteArray;
    }

    public static byte[] encrypt(byte[] bArr, Key key, String str) throws Exception {
        return doEncryptOrDecrypt(bArr, key, str, 1);
    }

    private static int getMaxBlock(int i2) throws Exception {
        if (i2 == 1) {
            return MAX_ENCRYPT_BLOCK;
        }
        if (i2 == 2) {
            return 128;
        }
        throw new IllegalArgumentException("wrong operation mode");
    }

    public static PrivateKey getPrivateKey(KeySpec keySpec) throws Exception {
        return KeyFactory.getInstance(KEY_RSA).generatePrivate(keySpec);
    }

    public static PublicKey getPublicKey(KeySpec keySpec) throws Exception {
        return KeyFactory.getInstance(KEY_RSA).generatePublic(keySpec);
    }

    public static byte[] sign(byte[] bArr, PrivateKey privateKey, String str) throws Exception {
        Signature signature = Signature.getInstance(str);
        signature.initSign(privateKey);
        signature.update(bArr);
        return signature.sign();
    }

    public static boolean verify(byte[] bArr, PublicKey publicKey, byte[] bArr2, String str) throws Exception {
        Signature signature = Signature.getInstance(str);
        signature.initVerify(publicKey);
        signature.update(bArr);
        return signature.verify(bArr2);
    }

    public static byte[] decrypt(byte[] bArr, Key key) throws Exception {
        return decrypt(bArr, key, CIPHER_RSA);
    }

    public static byte[] encrypt(byte[] bArr, Key key) throws Exception {
        return encrypt(bArr, key, CIPHER_RSA);
    }

    public static String decrypt(String str, Key key, String str2) throws Exception {
        return new String(decrypt(str.getBytes(), key, str2));
    }

    public static String encrypt(String str, Key key, String str2) throws Exception {
        return new String(encrypt(str.getBytes(), key, str2));
    }

    public static PrivateKey getPrivateKey(String str, String str2, int i2) throws Exception {
        return getPrivateKey(new RSAPrivateKeySpec(new BigInteger(str, i2), new BigInteger(str2, i2)));
    }

    public static PublicKey getPublicKey(String str, String str2, int i2) throws Exception {
        return getPublicKey(new RSAPublicKeySpec(new BigInteger(str, i2), new BigInteger(str2, i2)));
    }

    public static String decrypt(String str, Key key) throws Exception {
        return decrypt(str, key, CIPHER_RSA);
    }

    public static String encrypt(String str, Key key) throws Exception {
        return encrypt(str, key, CIPHER_RSA);
    }

    public static byte[] sign(byte[] bArr, PrivateKey privateKey) throws Exception {
        return sign(bArr, privateKey, SIGNATURE_SHA1_WITH_RSA);
    }

    public static boolean verify(byte[] bArr, PublicKey publicKey, byte[] bArr2) throws Exception {
        return verify(bArr, publicKey, bArr2, SIGNATURE_SHA1_WITH_RSA);
    }

    public static PrivateKey getPrivateKey(String str, String str2) throws Exception {
        return getPrivateKey(str, str2, 16);
    }

    public static PublicKey getPublicKey(String str, String str2) throws Exception {
        return getPublicKey(str, str2, 16);
    }

    public static String sign(String str, PrivateKey privateKey, String str2) throws Exception {
        return new String(sign(str.getBytes(), privateKey, str2));
    }

    public static boolean verify(String str, PublicKey publicKey, String str2, String str3) throws Exception {
        return verify(str.getBytes(), publicKey, str2.getBytes(), str3);
    }

    public static PrivateKey getPrivateKey(String str, int i2) throws Exception {
        return getPrivateKey(new PKCS8EncodedKeySpec(Base64.decode(str, i2)));
    }

    public static PublicKey getPublicKey(String str, int i2) throws Exception {
        return getPublicKey(new X509EncodedKeySpec(Base64.decode(str, i2)));
    }

    public static String sign(String str, PrivateKey privateKey) throws Exception {
        return sign(str, privateKey, SIGNATURE_SHA1_WITH_RSA);
    }

    public static boolean verify(String str, PublicKey publicKey, String str2) throws Exception {
        return verify(str, publicKey, str2, SIGNATURE_SHA1_WITH_RSA);
    }

    public static PrivateKey getPrivateKey(String str) throws Exception {
        return getPrivateKey(str, 0);
    }

    public static PublicKey getPublicKey(String str) throws Exception {
        return getPublicKey(str, 0);
    }
}
