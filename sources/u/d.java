package u;

import android.text.TextUtils;
import com.ot.pubsub.util.k;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import miui.util.HashUtils;

/* JADX INFO: loaded from: classes.dex */
public class d {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final char[] f2378a = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static final char[] f2379b = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String a(byte[] bArr, boolean z2) {
        return new String(f(bArr, z2 ? f2378a : f2379b));
    }

    private static MessageDigest b() {
        return c(HashUtils.MD5);
    }

    static MessageDigest c(String str) {
        try {
            return MessageDigest.getInstance(str);
        } catch (NoSuchAlgorithmException e2) {
            throw new RuntimeException(e2.getMessage());
        }
    }

    private static byte[] d(String str, String str2) {
        if (str == null) {
            return null;
        }
        try {
            return str.getBytes(str2);
        } catch (UnsupportedEncodingException unused) {
            return null;
        }
    }

    public static byte[] e(byte[] bArr) {
        return b().digest(bArr);
    }

    private static char[] f(byte[] bArr, char[] cArr) {
        char[] cArr2 = new char[bArr.length << 1];
        int i2 = 0;
        for (byte b2 : bArr) {
            int i3 = i2 + 1;
            cArr2[i2] = cArr[(b2 & 240) >>> 4];
            i2 += 2;
            cArr2[i3] = cArr[b2 & 15];
        }
        return cArr2;
    }

    public static byte[] g(String str) {
        return e(d(str, "UTF-8"));
    }

    public static String h(String str) {
        return a(g(str), true);
    }

    public static String i(byte[] bArr) {
        String str;
        if (bArr != null) {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance(HashUtils.MD5);
                messageDigest.update(bArr);
                str = String.format("%1$032X", new BigInteger(1, messageDigest.digest()));
            } catch (Exception e2) {
                k.h("DigestUtil", "getMD5 exception: " + e2);
                str = "";
            }
        } else {
            str = "";
        }
        return str.toLowerCase();
    }

    public static String j(String str) {
        return TextUtils.isEmpty(str) ? "" : i(str.getBytes());
    }
}
