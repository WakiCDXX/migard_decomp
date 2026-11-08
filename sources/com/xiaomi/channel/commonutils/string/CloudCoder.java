package com.xiaomi.channel.commonutils.string;

import android.net.Uri;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import miui.util.CoderUtils;
import miui.util.HashUtils;

/* JADX INFO: loaded from: classes.dex */
public class CloudCoder {
    private static final String RC4_ALGORITHM_NAME = "RC4";

    public static String generateSignature(String str, String str2, Map<String, String> map, String str3) {
        if (TextUtils.isEmpty(str3)) {
            throw new InvalidParameterException("security is not nullable");
        }
        ArrayList<String> arrayList = new ArrayList();
        if (str != null) {
            arrayList.add(str.toUpperCase());
        }
        if (str2 != null) {
            arrayList.add(Uri.parse(str2).getEncodedPath());
        }
        if (map != null && !map.isEmpty()) {
            for (Map.Entry entry : new TreeMap(map).entrySet()) {
                arrayList.add(String.format("%s=%s", entry.getKey(), entry.getValue()));
            }
        }
        arrayList.add(str3);
        StringBuilder sb = new StringBuilder();
        boolean z2 = true;
        for (String str4 : arrayList) {
            if (!z2) {
                sb.append('&');
            }
            sb.append(str4);
            z2 = false;
        }
        return hash4SHA1(sb.toString());
    }

    public static String hash4SHA1(String str) {
        try {
            return String.valueOf(Base64Coder.encode(MessageDigest.getInstance(HashUtils.SHA1).digest(str.getBytes("UTF-8"))));
        } catch (UnsupportedEncodingException e2) {
            MyLog.e("CloudCoder.hash4SHA1 ", e2);
            throw new IllegalStateException("failed to SHA1");
        } catch (NoSuchAlgorithmException e3) {
            MyLog.e("CloudCoder.hash4SHA1 ", e3);
            throw new IllegalStateException("failed to SHA1");
        } catch (Exception e4) {
            MyLog.e("CloudCoder.hash4SHA1 ", e4);
            throw new IllegalStateException("failed to SHA1");
        }
    }

    public static Cipher newAESCipher(String str, int i2) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64Coder.decode(str), "AES");
        try {
            Cipher cipher = Cipher.getInstance(CoderUtils.AES_ALGORITHM);
            cipher.init(i2, secretKeySpec, new IvParameterSpec("0102030405060708".getBytes()));
            return cipher;
        } catch (InvalidAlgorithmParameterException e2) {
            e2.printStackTrace();
            return null;
        } catch (InvalidKeyException e3) {
            e3.printStackTrace();
            return null;
        } catch (NoSuchAlgorithmException e4) {
            e4.printStackTrace();
            return null;
        } catch (NoSuchPaddingException e5) {
            e5.printStackTrace();
            return null;
        }
    }

    public static Cipher newRC4Cipher(byte[] bArr, int i2) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(bArr, RC4_ALGORITHM_NAME);
        try {
            Cipher cipher = Cipher.getInstance(RC4_ALGORITHM_NAME);
            cipher.init(i2, secretKeySpec);
            return cipher;
        } catch (InvalidKeyException e2) {
            e2.printStackTrace();
            return null;
        } catch (NoSuchAlgorithmException e3) {
            e3.printStackTrace();
            return null;
        } catch (NoSuchPaddingException e4) {
            e4.printStackTrace();
            return null;
        }
    }
}
