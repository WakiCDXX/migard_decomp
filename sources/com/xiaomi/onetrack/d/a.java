package com.xiaomi.onetrack.d;

import android.util.Base64;
import com.xiaomi.onetrack.util.m;
import com.xiaomi.onetrack.util.p;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

/* JADX INFO: loaded from: classes.dex */
public class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final String f1326a = "AES";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static final String f1327b = "AES/ECB/PKCS5Padding";

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static final String f1328c = "AES";

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static KeyGenerator f1329d;

    static {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            f1329d = keyGenerator;
            keyGenerator.init(128);
        } catch (Exception e2) {
            p.b(p.a("AES"), "AesUtil e", e2);
        }
    }

    public static byte[] a() {
        return f1329d.generateKey().getEncoded();
    }

    public static byte[] b(byte[] bArr, byte[] bArr2) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(bArr2, "AES");
            Cipher cipher = Cipher.getInstance(f1327b);
            cipher.init(2, secretKeySpec);
            return cipher.doFinal(bArr);
        } catch (Exception e2) {
            p.b("AES", "decrypt exception:", e2);
            p.b("AES", "content len=" + bArr.length + ", passwd len=" + bArr2.length);
            return null;
        }
    }

    private static byte[] c(String str) {
        if (str == null || str.length() < 1) {
            return null;
        }
        byte[] bArr = new byte[str.length() / 2];
        for (int i2 = 0; i2 < str.length() / 2; i2++) {
            int i3 = i2 * 2;
            int i4 = i3 + 1;
            bArr[i2] = (byte) ((Integer.parseInt(str.substring(i3, i4), 16) * 16) + Integer.parseInt(str.substring(i4, i3 + 2), 16));
        }
        return bArr;
    }

    public static String d(String str, String str2) {
        return new String(a(Base64.decode(str, 10), str2));
    }

    private static byte[] e(String str, String str2) {
        return a(str.getBytes(), str2.getBytes());
    }

    public static byte[] a(byte[] bArr, byte[] bArr2) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(bArr2, "AES");
            Cipher cipher = Cipher.getInstance(f1327b);
            cipher.init(1, secretKeySpec);
            return cipher.doFinal(bArr);
        } catch (Exception e2) {
            p.b(p.a("AES"), "encrypt exception:", e2);
            return null;
        }
    }

    private static byte[] a(byte[] bArr, String str) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(b(str), "AES");
            Cipher cipher = Cipher.getInstance(f1327b);
            cipher.init(2, secretKeySpec);
            return cipher.doFinal(bArr);
        } catch (Exception e2) {
            p.b(p.a("AES"), "decrypt exception:", e2);
            return null;
        }
    }

    private static byte[] b(String str) {
        if (str != null) {
            return str.getBytes();
        }
        return null;
    }

    public static String c(String str, String str2) {
        return new String(a(c(str), str2));
    }

    public static String b(String str, String str2) {
        return Base64.encodeToString(e(str, str2), 10);
    }

    public static String a(String str, String str2) {
        return m.a(e(str, str2));
    }

    public static String a(String str) {
        try {
            char[] charArray = (str + b.f1330a).toCharArray();
            for (int i2 = 0; i2 < charArray.length; i2++) {
                for (int i3 = 0; i3 < charArray.length - 1; i3++) {
                    char c2 = charArray[i2];
                    char c3 = charArray[i3];
                    if (c2 < c3) {
                        charArray[i2] = c3;
                        charArray[i3] = c2;
                    }
                }
            }
            return d.h(new String(charArray));
        } catch (Exception unused) {
            p.b("AES", "encodeFromSalt ");
            return "";
        }
    }
}
