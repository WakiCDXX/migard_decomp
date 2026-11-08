package miui.util;

import android.util.Base64;
import com.xiaomi.channelcollection.RegisterAppChannelInfoCollectionJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/* JADX INFO: loaded from: classes.dex */
public class CoderUtils {
    public static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String[] hexDigits = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", RegisterAppChannelInfoCollectionJob.JSON_KEY_APP_ID, "b", "c", "d", "e", "f"};

    public static final String base64AesEncode(String str, String str2) {
        byte[] bytes;
        if (str != null && str.length() != 0 && (bytes = str2.getBytes()) != null && bytes.length == 16) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(bytes, "AES");
            try {
                Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
                cipher.init(1, secretKeySpec, new IvParameterSpec("0102030405060708".getBytes()));
                return new String(encodeBase64(cipher.doFinal(str.getBytes())));
            } catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException unused) {
            }
        }
        return null;
    }

    public static final String base6AesDecode(String str, String str2) {
        byte[] bytes;
        if (str != null && str.length() != 0 && (bytes = str2.getBytes()) != null && bytes.length == 16) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(bytes, "AES");
            try {
                Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
                cipher.init(2, secretKeySpec, new IvParameterSpec("0102030405060708".getBytes()));
                byte[] bArrDecodeBase64Bytes = decodeBase64Bytes(str);
                if (bArrDecodeBase64Bytes == null) {
                    return null;
                }
                return new String(cipher.doFinal(bArrDecodeBase64Bytes));
            } catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException unused) {
            }
        }
        return null;
    }

    private static String byteArrayToString(byte[] bArr) {
        StringBuffer stringBuffer = new StringBuffer();
        for (byte b2 : bArr) {
            stringBuffer.append(byteToHexString(b2));
        }
        return stringBuffer.toString();
    }

    private static String byteToHexString(byte b2) {
        int i2 = b2;
        if (b2 < 0) {
            i2 = b2 + 256;
        }
        StringBuilder sb = new StringBuilder();
        String[] strArr = hexDigits;
        sb.append(strArr[i2 / 16]);
        sb.append(strArr[i2 % 16]);
        return sb.toString();
    }

    public static final String decodeBase64(String str) {
        return new String(Base64.decode(str.getBytes(), 0));
    }

    public static final byte[] decodeBase64Bytes(String str) {
        return Base64.decode(str.getBytes(), 0);
    }

    public static final byte[] encodeBase64(String str) {
        return Base64.encode(str.getBytes(), 2);
    }

    public static final byte[] encodeBase64Bytes(String str) {
        return Base64.encode(str.getBytes(), 2);
    }

    public static final String encodeMD5(String str) {
        if (str != null && str.length() != 0) {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance(HashUtils.MD5);
                messageDigest.update(str.getBytes());
                return byteArrayToString(messageDigest.digest());
            } catch (NoSuchAlgorithmException e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }

    public static final String encodeSHA(String str) {
        if (str != null && str.length() != 0) {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(str.getBytes());
                return byteArrayToString(messageDigest.digest());
            } catch (NoSuchAlgorithmException e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }

    public static final byte[] encodeSHABytes(String str) {
        if (str != null && str.length() != 0) {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(str.getBytes());
                return messageDigest.digest();
            } catch (NoSuchAlgorithmException e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }

    public static final byte[] encodeBase64(byte[] bArr) {
        return Base64.encode(bArr, 2);
    }

    public static final String encodeMD5(File file) {
        FileInputStream fileInputStream;
        byte[] bArr = new byte[1024];
        try {
            try {
                fileInputStream = new FileInputStream(file);
                try {
                    try {
                        MessageDigest messageDigest = MessageDigest.getInstance(HashUtils.MD5);
                        while (true) {
                            int i2 = fileInputStream.read(bArr);
                            if (i2 > 0) {
                                messageDigest.update(bArr, 0, i2);
                            } else {
                                try {
                                    break;
                                } catch (IOException e2) {
                                    e2.printStackTrace();
                                }
                            }
                        }
                        fileInputStream.close();
                        return byteArrayToString(messageDigest.digest());
                    } catch (IOException e3) {
                        e3.printStackTrace();
                        try {
                            fileInputStream.close();
                        } catch (IOException e4) {
                            e4.printStackTrace();
                        }
                        return null;
                    }
                } catch (NoSuchAlgorithmException e5) {
                    e5.printStackTrace();
                    try {
                        fileInputStream.close();
                    } catch (IOException e6) {
                        e6.printStackTrace();
                    }
                    return null;
                }
            } catch (FileNotFoundException e7) {
                e7.printStackTrace();
                return null;
            }
        } catch (Throwable th) {
            try {
                fileInputStream.close();
            } catch (IOException e8) {
                e8.printStackTrace();
            }
            throw th;
        }
    }
}
