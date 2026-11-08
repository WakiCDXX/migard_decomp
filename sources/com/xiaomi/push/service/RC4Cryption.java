package com.xiaomi.push.service;

import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.string.Base64Coder;

/* JADX INFO: loaded from: classes.dex */
public class RC4Cryption {
    private static int keylength = 8;
    private byte[] S;
    private int next_j;
    private int the_i;
    private int the_j;

    public RC4Cryption() {
        this.next_j = -666;
        this.S = new byte[256];
        this.the_j = 0;
        this.the_i = 0;
    }

    public static byte[] buildkey(byte[] bArr, byte[] bArr2) {
        byte[] bArr3 = new byte[keylength];
        int length = bArr.length;
        for (int i2 = 0; i2 < length; i2++) {
            bArr3[i2] = bArr[i2];
        }
        for (int i3 = length; i3 < keylength; i3++) {
            bArr3[i3] = bArr2[i3 - length];
        }
        return bArr3;
    }

    public static String byte2string(byte b2) {
        return ("" + "0123456789abcdef".charAt((b2 >> 4) & 15)) + "0123456789abcdef".charAt(b2 & 15);
    }

    public static byte[] decrypt(byte[] bArr, String str) {
        return encrypt(bArr, Base64Coder.decode(str));
    }

    public static byte[] encrypt(byte[] bArr, byte[] bArr2) {
        byte[] bArr3 = new byte[bArr2.length];
        RC4Cryption rC4Cryption = new RC4Cryption();
        rC4Cryption.ksa(bArr);
        rC4Cryption.init();
        for (int i2 = 0; i2 < bArr2.length; i2++) {
            bArr3[i2] = (byte) (bArr2[i2] ^ rC4Cryption.nextVal());
        }
        return bArr3;
    }

    public static byte[] generateKeyForRC4(String str, String str2) {
        byte[] bArrDecode = Base64Coder.decode(str);
        byte[] bytes = str2.getBytes();
        byte[] bArr = new byte[bArrDecode.length + 1 + bytes.length];
        for (int i2 = 0; i2 < bArrDecode.length; i2++) {
            bArr[i2] = bArrDecode[i2];
        }
        bArr[bArrDecode.length] = 95;
        for (int i3 = 0; i3 < bytes.length; i3++) {
            bArr[bArrDecode.length + 1 + i3] = bytes[i3];
        }
        return bArr;
    }

    private static int hexval(char c2) {
        if ('0' <= c2 && c2 <= '9') {
            return c2 - '0';
        }
        if ('a' <= c2 && c2 <= 'f') {
            return c2 - 'W';
        }
        if ('A' > c2 || c2 > 'F') {
            return 0;
        }
        return c2 - '7';
    }

    private void init() {
        this.the_j = 0;
        this.the_i = 0;
    }

    private void ksa(int i2, byte[] bArr, boolean z2) {
        int length = bArr.length;
        for (int i3 = 0; i3 < 256; i3++) {
            this.S[i3] = (byte) i3;
        }
        this.the_j = 0;
        this.the_i = 0;
        while (true) {
            int i4 = this.the_i;
            if (i4 >= i2) {
                break;
            }
            int iPosify = ((this.the_j + posify(this.S[i4])) + posify(bArr[this.the_i % length])) % 256;
            this.the_j = iPosify;
            sswap(this.S, this.the_i, iPosify);
            this.the_i++;
        }
        if (i2 != 256) {
            this.next_j = ((this.the_j + posify(this.S[i2])) + posify(bArr[i2 % length])) % 256;
        }
        if (z2) {
            StringBuilder sb = new StringBuilder();
            sb.append("S_");
            int i5 = i2 - 1;
            sb.append(i5);
            sb.append(":");
            for (int i6 = 0; i6 <= i2; i6++) {
                sb.append(" ");
                sb.append(posify(this.S[i6]));
            }
            sb.append("   j_");
            sb.append(i5);
            sb.append("=");
            sb.append(this.the_j);
            sb.append("   j_");
            sb.append(i2);
            sb.append("=");
            sb.append(this.next_j);
            sb.append("   S_");
            sb.append(i5);
            sb.append("[j_");
            sb.append(i5);
            sb.append("]=");
            sb.append(posify(this.S[this.the_j]));
            sb.append("   S_");
            sb.append(i5);
            sb.append("[j_");
            sb.append(i2);
            sb.append("]=");
            sb.append(posify(this.S[this.next_j]));
            if (this.S[1] != 0) {
                sb.append("   S[1]!=0");
            }
            MyLog.w(sb.toString());
        }
    }

    public static int posify(byte b2) {
        return b2 >= 0 ? b2 : b2 + 256;
    }

    private static void sswap(byte[] bArr, int i2, int i3) {
        byte b2 = bArr[i2];
        bArr[i2] = bArr[i3];
        bArr[i3] = b2;
    }

    public static byte[] string2byte(String str) {
        int length = str.length() / 2;
        byte[] bArr = new byte[length];
        for (int i2 = 0; i2 < length; i2++) {
            int i3 = i2 * 2;
            bArr[i2] = (byte) ((hexval(str.charAt(i3)) * 16) + hexval(str.charAt(i3 + 1)));
        }
        return bArr;
    }

    int S(int i2) {
        return posify(this.S[(byte) i2]);
    }

    byte inverse(byte b2) {
        for (int i2 = 0; i2 < 256; i2++) {
            if (b2 == this.S[i2]) {
                return (byte) i2;
            }
        }
        return (byte) 0;
    }

    byte nextVal() {
        int i2 = (this.the_i + 1) % 256;
        this.the_i = i2;
        int iPosify = (this.the_j + posify(this.S[i2])) % 256;
        this.the_j = iPosify;
        sswap(this.S, this.the_i, iPosify);
        byte[] bArr = this.S;
        return bArr[(posify(bArr[this.the_i]) + posify(this.S[this.the_j])) % 256];
    }

    int next_j() {
        return this.next_j;
    }

    int the_i() {
        return this.the_i;
    }

    int the_j() {
        return this.the_j;
    }

    public static String byte2string(byte[] bArr) {
        String str = "";
        for (byte b2 : bArr) {
            str = str + byte2string(b2);
        }
        return str;
    }

    public RC4Cryption(byte[] bArr) {
        this.next_j = -666;
        this.S = bArr;
        this.the_j = 0;
        this.the_i = 0;
    }

    public static byte[] encrypt(byte[] bArr, byte[] bArr2, boolean z2, int i2, int i3) {
        byte[] bArr3;
        int i4;
        if (i2 >= 0 && i2 <= bArr2.length && i2 + i3 <= bArr2.length) {
            if (z2) {
                bArr3 = bArr2;
                i4 = i2;
            } else {
                bArr3 = new byte[i3];
                i4 = 0;
            }
            RC4Cryption rC4Cryption = new RC4Cryption();
            rC4Cryption.ksa(bArr);
            rC4Cryption.init();
            for (int i5 = 0; i5 < i3; i5++) {
                bArr3[i4 + i5] = (byte) (bArr2[i2 + i5] ^ rC4Cryption.nextVal());
            }
            return bArr3;
        }
        throw new IllegalArgumentException("start = " + i2 + " len = " + i3);
    }

    public static String encrypt(byte[] bArr, String str) {
        return String.valueOf(Base64Coder.encode(encrypt(bArr, str.getBytes())));
    }

    private void ksa(byte[] bArr) {
        ksa(256, bArr, false);
    }
}
