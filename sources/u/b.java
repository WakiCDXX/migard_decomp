package u;

import android.content.Context;
import android.util.Base64;
import com.ot.pubsub.util.k;
import com.xiaomi.onetrack.OneTrack;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Cipher;
import miui.util.RSAUtils;

/* JADX INFO: loaded from: classes.dex */
public class b {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final String f2375a = d();

    public static synchronized String a(Context context, String str) throws Exception {
        Cipher cipher = Cipher.getInstance(RSAUtils.CIPHER_RSA);
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        c(context, keyStore);
        Certificate certificate = keyStore.getCertificate("RSA_KEY");
        if (certificate == null) {
            return null;
        }
        cipher.init(1, certificate.getPublicKey());
        return Base64.encodeToString(cipher.doFinal(str.getBytes("UTF-8")), 0);
    }

    private static void b() throws IllegalAccessException, NoSuchFieldException, LinkageError, NoSuchMethodException, NoSuchAlgorithmException, InstantiationException, ClassNotFoundException, NoSuchProviderException, InvocationTargetException, InvalidAlgorithmParameterException {
        Class<?> cls = Class.forName("android.security.keystore.KeyGenParameterSpec$Builder");
        Constructor<?> constructor = cls.getConstructor(String.class, Integer.TYPE);
        Class<?> cls2 = Class.forName("android.security.keystore.KeyProperties");
        Object objNewInstance = constructor.newInstance("RSA_KEY", Integer.valueOf(cls2.getDeclaredField("PURPOSE_ENCRYPT").getInt(null) | cls2.getDeclaredField("PURPOSE_DECRYPT").getInt(null)));
        cls.getMethod("setDigests", String[].class).invoke(objNewInstance, new String[]{(String) cls2.getDeclaredField("DIGEST_SHA256").get(null), (String) cls2.getDeclaredField("DIGEST_SHA512").get(null)});
        cls.getMethod("setEncryptionPaddings", String[].class).invoke(objNewInstance, new String[]{(String) cls2.getDeclaredField("ENCRYPTION_PADDING_RSA_PKCS1").get(null)});
        Class[] clsArr = new Class[0];
        Object objInvoke = cls.getMethod(OneTrack.Param.BUILD, null).invoke(objNewInstance, null);
        Class<?> cls3 = Class.forName("java.security.KeyPairGenerator");
        KeyPairGenerator keyPairGenerator = (KeyPairGenerator) cls3.getMethod("getInstance", String.class, String.class).invoke(null, RSAUtils.KEY_RSA, "AndroidKeyStore");
        cls3.getMethod("initialize", AlgorithmParameterSpec.class).invoke(keyPairGenerator, objInvoke);
        keyPairGenerator.generateKeyPair();
    }

    private static void c(Context context, KeyStore keyStore) {
        try {
            if (keyStore.containsAlias("RSA_KEY")) {
                return;
            }
            b();
        } catch (Throwable th) {
            k.i("AndroidKeyStore", "createKey e", th);
        }
    }

    private static String d() {
        try {
            return new String("h*Y31]c!@5Ns_jPsvej!@3[(*w(!l".getBytes(), "UTF-8");
        } catch (Exception e2) {
            k.h("AndroidKeyStore", e2.getMessage());
            return "";
        }
    }

    public static synchronized String e(Context context, String str) throws Exception {
        Cipher cipher;
        cipher = Cipher.getInstance(RSAUtils.CIPHER_RSA);
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        c(context, keyStore);
        cipher.init(2, (PrivateKey) keyStore.getKey("RSA_KEY", null));
        return new String(cipher.doFinal(Base64.decode(str, 0)), "UTF-8");
    }
}
