package u;

import com.ot.pubsub.util.k;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import miui.util.RSAUtils;

/* JADX INFO: loaded from: classes.dex */
public class e {
    private static RSAPublicKey a(String str) throws Exception {
        return (RSAPublicKey) KeyFactory.getInstance(RSAUtils.KEY_RSA).generatePublic(new X509EncodedKeySpec(c.c(str)));
    }

    public static byte[] b(byte[] bArr) throws Exception {
        try {
            RSAPublicKey rSAPublicKeyA = a(com.xiaomi.onetrack.d.e.f1341a);
            Cipher cipher = Cipher.getInstance(RSAUtils.CIPHER_RSA, "BC");
            cipher.init(1, rSAPublicKeyA);
            return cipher.doFinal(bArr);
        } catch (Exception e2) {
            k.i("RsaUtils", "RsaUtils encrypt exception:", e2);
            return null;
        }
    }
}
