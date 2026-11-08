package com.miui.powerkeeper.utils;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import miui.util.RSAUtils;

/* JADX INFO: loaded from: classes.dex */
public class EncryptUtils {
    private static final String TAG = "EncryptUtils";

    public static String base64Encoded(byte[] bArr) {
        return Base64.getEncoder().encodeToString(bArr);
    }

    public static String encryptByPrivateKey(String str, String str2) {
        try {
            PrivateKey privateKey = getPrivateKey(str2);
            try {
                Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");
                try {
                    cipher.init(1, privateKey);
                    try {
                        return base64Encoded(cipher.doFinal(str.getBytes()));
                    } catch (BadPaddingException | IllegalBlockSizeException e2) {
                        throw new RuntimeException(e2);
                    }
                } catch (InvalidKeyException e3) {
                    throw new RuntimeException(e3);
                }
            } catch (NoSuchAlgorithmException | NoSuchPaddingException e4) {
                throw new RuntimeException(e4);
            }
        } catch (Exception e5) {
            throw new RuntimeException(e5);
        }
    }

    public static PrivateKey getPrivateKey(String str) {
        try {
            try {
                return KeyFactory.getInstance(RSAUtils.KEY_RSA).generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(str.getBytes())));
            } catch (InvalidKeySpecException e2) {
                throw new RuntimeException(e2);
            }
        } catch (NoSuchAlgorithmException e3) {
            throw new RuntimeException(e3);
        }
    }
}
