package miui.util;

import com.miui.powerkeeper.powerchecker.BatteryStatsHelper;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import miui.date.DateUtils;

/* JADX INFO: loaded from: classes.dex */
public class HashUtils {
    public static final String MD5 = "MD5";
    public static final String SHA1 = "SHA1";

    private HashUtils() {
    }

    public static String getHash(String str, String str2) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(str2);
            messageDigest.update(str.getBytes());
            return toHexString(messageDigest.digest());
        } catch (NoSuchAlgorithmException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public static String getMD5(String str) {
        return getHash(str, MD5);
    }

    public static String getSHA1(String str) {
        return getHash(str, SHA1);
    }

    public static String toHexString(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        for (byte b2 : bArr) {
            sb.append(String.format("%02x", Integer.valueOf(b2 & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL)));
        }
        return sb.toString();
    }

    public static String getMD5(InputStream inputStream) {
        return getHash(inputStream, MD5);
    }

    public static String getSHA1(InputStream inputStream) {
        return getHash(inputStream, SHA1);
    }

    public static String getMD5(File file) {
        return getHash(file, MD5);
    }

    public static String getSHA1(File file) {
        return getHash(file, SHA1);
    }

    public static String getHash(InputStream inputStream, String str) {
        try {
            try {
                try {
                    try {
                        MessageDigest messageDigest = MessageDigest.getInstance(str);
                        byte[] bArr = new byte[DateUtils.FORMAT_ABBREV_WEEKDAY];
                        while (true) {
                            int i2 = inputStream.read(bArr);
                            if (i2 > 0) {
                                messageDigest.update(bArr, 0, i2);
                            } else {
                                String hexString = toHexString(messageDigest.digest());
                                try {
                                    inputStream.close();
                                    return hexString;
                                } catch (IOException e2) {
                                    e2.printStackTrace();
                                    return hexString;
                                }
                            }
                        }
                    } catch (Throwable th) {
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (IOException e3) {
                                e3.printStackTrace();
                            }
                        }
                        throw th;
                    }
                } catch (IOException e4) {
                    e4.printStackTrace();
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    return null;
                }
            } catch (NoSuchAlgorithmException e5) {
                e5.printStackTrace();
                if (inputStream != null) {
                    inputStream.close();
                }
                return null;
            }
        } catch (IOException e6) {
            e6.printStackTrace();
        }
    }

    public static String getHash(File file, String str) {
        try {
            return getHash(new BufferedInputStream(new FileInputStream(file)), str);
        } catch (FileNotFoundException e2) {
            e2.printStackTrace();
            return null;
        }
    }
}
