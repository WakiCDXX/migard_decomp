package com.xiaomi.channel.commonutils.string;

import com.miui.powerkeeper.powerchecker.BatteryStatsHelper;

/* JADX INFO: loaded from: classes.dex */
public class Base64Coder {
    private static byte[] map2;
    private static final String systemLineSeparator = System.getProperty("line.separator");
    private static char[] map1 = new char[64];

    static {
        char c2 = 'A';
        int i2 = 0;
        while (c2 <= 'Z') {
            map1[i2] = c2;
            c2 = (char) (c2 + 1);
            i2++;
        }
        char c3 = 'a';
        while (c3 <= 'z') {
            map1[i2] = c3;
            c3 = (char) (c3 + 1);
            i2++;
        }
        char c4 = '0';
        while (c4 <= '9') {
            map1[i2] = c4;
            c4 = (char) (c4 + 1);
            i2++;
        }
        char[] cArr = map1;
        cArr[i2] = '+';
        cArr[i2 + 1] = '/';
        map2 = new byte[128];
        int i3 = 0;
        while (true) {
            byte[] bArr = map2;
            if (i3 >= bArr.length) {
                break;
            }
            bArr[i3] = -1;
            i3++;
        }
        for (int i4 = 0; i4 < 64; i4++) {
            map2[map1[i4]] = (byte) i4;
        }
    }

    private Base64Coder() {
    }

    public static byte[] decode(String str) {
        return decode(str.toCharArray());
    }

    public static byte[] decodeLines(String str) {
        char[] cArr = new char[str.length()];
        int i2 = 0;
        for (int i3 = 0; i3 < str.length(); i3++) {
            char cCharAt = str.charAt(i3);
            if (cCharAt != ' ' && cCharAt != '\r' && cCharAt != '\n' && cCharAt != '\t') {
                cArr[i2] = cCharAt;
                i2++;
            }
        }
        return decode(cArr, 0, i2);
    }

    public static String decodeString(String str) {
        return new String(decode(str));
    }

    public static char[] encode(byte[] bArr) {
        return encode(bArr, 0, bArr.length);
    }

    public static String encodeLines(byte[] bArr) {
        return encodeLines(bArr, 0, bArr.length, 76, systemLineSeparator);
    }

    public static String encodeString(String str) {
        return new String(encode(str.getBytes()));
    }

    public static byte[] decode(char[] cArr) {
        return decode(cArr, 0, cArr.length);
    }

    public static char[] encode(byte[] bArr, int i2) {
        return encode(bArr, 0, i2);
    }

    public static String encodeLines(byte[] bArr, int i2, int i3, int i4, String str) {
        int i5 = (i4 * 3) / 4;
        if (i5 <= 0) {
            throw new IllegalArgumentException();
        }
        StringBuilder sb = new StringBuilder((((i3 + 2) / 3) * 4) + ((((i3 + i5) - 1) / i5) * str.length()));
        int i6 = 0;
        while (i6 < i3) {
            int iMin = Math.min(i3 - i6, i5);
            sb.append(encode(bArr, i2 + i6, iMin));
            sb.append(str);
            i6 += iMin;
        }
        return sb.toString();
    }

    public static byte[] decode(char[] cArr, int i2, int i3) {
        char c2;
        if (i3 % 4 == 0) {
            while (i3 > 0 && cArr[(i2 + i3) - 1] == '=') {
                i3--;
            }
            int i4 = (i3 * 3) / 4;
            byte[] bArr = new byte[i4];
            int i5 = i3 + i2;
            int i6 = 0;
            while (i2 < i5) {
                char c3 = cArr[i2];
                int i7 = i2 + 2;
                char c4 = cArr[i2 + 1];
                char c5 = 'A';
                if (i7 < i5) {
                    i2 += 3;
                    c2 = cArr[i7];
                } else {
                    i2 = i7;
                    c2 = 'A';
                }
                if (i2 < i5) {
                    c5 = cArr[i2];
                    i2++;
                }
                if (c3 <= 127 && c4 <= 127 && c2 <= 127 && c5 <= 127) {
                    byte[] bArr2 = map2;
                    byte b2 = bArr2[c3];
                    byte b3 = bArr2[c4];
                    byte b4 = bArr2[c2];
                    byte b5 = bArr2[c5];
                    if (b2 >= 0 && b3 >= 0 && b4 >= 0 && b5 >= 0) {
                        int i8 = (b2 << 2) | (b3 >>> 4);
                        int i9 = ((b3 & 15) << 4) | (b4 >>> 2);
                        int i10 = ((b4 & 3) << 6) | b5;
                        int i11 = i6 + 1;
                        bArr[i6] = (byte) i8;
                        if (i11 < i4) {
                            bArr[i11] = (byte) i9;
                            i11 = i6 + 2;
                        }
                        if (i11 < i4) {
                            i6 = i11 + 1;
                            bArr[i11] = (byte) i10;
                        } else {
                            i6 = i11;
                        }
                    } else {
                        throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
                    }
                } else {
                    throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
                }
            }
            return bArr;
        }
        throw new IllegalArgumentException("Length of Base64 encoded input string is not a multiple of 4.");
    }

    public static char[] encode(byte[] bArr, int i2, int i3) {
        int i4;
        int i5;
        int i6;
        int i7;
        int i8 = ((i3 * 4) + 2) / 3;
        char[] cArr = new char[((i3 + 2) / 3) * 4];
        int i9 = i3 + i2;
        int i10 = 0;
        while (i2 < i9) {
            int i11 = i2 + 1;
            byte b2 = bArr[i2];
            int i12 = b2 & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL;
            if (i11 < i9) {
                i4 = i2 + 2;
                i5 = bArr[i11] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL;
            } else {
                i4 = i11;
                i5 = 0;
            }
            if (i4 < i9) {
                i6 = i4 + 1;
                i7 = bArr[i4] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL;
            } else {
                i6 = i4;
                i7 = 0;
            }
            int i13 = ((b2 & 3) << 4) | (i5 >>> 4);
            int i14 = ((i5 & 15) << 2) | (i7 >>> 6);
            int i15 = i7 & 63;
            char[] cArr2 = map1;
            cArr[i10] = cArr2[i12 >>> 2];
            int i16 = i10 + 2;
            cArr[i10 + 1] = cArr2[i13];
            char c2 = '=';
            cArr[i16] = i16 < i8 ? cArr2[i14] : '=';
            int i17 = i10 + 3;
            if (i17 < i8) {
                c2 = cArr2[i15];
            }
            cArr[i17] = c2;
            i10 += 4;
            i2 = i6;
        }
        return cArr;
    }
}
