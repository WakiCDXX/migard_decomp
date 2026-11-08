package u;

import com.ot.pubsub.util.i;
import com.ot.pubsub.util.k;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

/* JADX INFO: loaded from: classes.dex */
public class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static KeyGenerator f2372a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final String f2373b = f();

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final String f2374c = i();

    static {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            f2372a = keyGenerator;
            keyGenerator.init(128);
        } catch (Exception e2) {
            k.i("AES", "AesUtil e", e2);
        }
    }

    public static String a(String str) {
        try {
            char[] charArray = (str + b.f2375a).toCharArray();
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
            return d.j(new String(charArray));
        } catch (Exception unused) {
            k.h("AES", "encodeFromSalt ");
            return "";
        }
    }

    public static String b(String str, String str2) {
        return i.c(l(str, str2));
    }

    public static byte[] c() {
        return f2372a.generateKey().getEncoded();
    }

    private static byte[] d(byte[] bArr, String str) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(g(str), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(2, secretKeySpec);
            return cipher.doFinal(bArr);
        } catch (Exception e2) {
            k.e("AES", "decrypt exception:", e2);
            return null;
        }
    }

    public static byte[] e(byte[] bArr, byte[] bArr2) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(bArr2, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(1, secretKeySpec);
            return cipher.doFinal(bArr);
        } catch (Exception e2) {
            k.i("AES", "encrypt exception:", e2);
            return null;
        }
    }

    private static String f() {
        try {
            return new String("6AiSfshj3pD/9r91".getBytes(), "UTF-8");
        } catch (Exception e2) {
            k.h("AES", e2.getMessage());
            return "";
        }
    }

    private static byte[] g(String str) {
        if (str != null) {
            return str.getBytes();
        }
        return null;
    }

    public static byte[] h(byte[] bArr, byte[] bArr2) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(bArr2, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(2, secretKeySpec);
            return cipher.doFinal(bArr);
        } catch (Exception e2) {
            k.i("AES", "decrypt exception:", e2);
            k.h("AES", "content len=" + bArr.length + ", passwd len=" + bArr2.length);
            return null;
        }
    }

    private static String i() {
        try {
            return new String("h*Y31]c!@5Ns_jPsvej!@3[(*w(!l".getBytes(), "UTF-8");
        } catch (Exception e2) {
            k.h("AES", e2.getMessage());
            return "";
        }
    }

    public static String j(String str, String str2) {
        return new String(d(k(str), str2));
    }

    private static byte[] k(String str) {
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

    private static byte[] l(String str, String str2) {
        return e(str.getBytes(), str2.getBytes());
    }
}
