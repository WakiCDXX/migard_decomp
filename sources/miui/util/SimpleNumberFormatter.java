package miui.util;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class SimpleNumberFormatter {
    private static Locale sLocale = Locale.getDefault();
    private static char sZeroDigit = new DecimalFormatSymbols(sLocale).getZeroDigit();

    private static String convertInt(int i2, int i3) {
        int i4;
        StringBuilder sbAcquire = Pools.getStringBuilderPool().acquire();
        if (i3 < 0) {
            i3 = -i3;
            i2--;
            sbAcquire.append('-');
        }
        if (i3 >= 10000) {
            String string = Integer.toString(i3);
            for (int length = string.length(); length < i2; length++) {
                sbAcquire.append('0');
            }
            sbAcquire.append(string);
        } else {
            if (i3 >= 1000) {
                i4 = 4;
            } else if (i3 >= 100) {
                i4 = 3;
            } else {
                i4 = i3 >= 10 ? 2 : 1;
            }
            while (i4 < i2) {
                sbAcquire.append('0');
                i4++;
            }
            sbAcquire.append(i3);
        }
        String string2 = sbAcquire.toString();
        Pools.getStringBuilderPool().release(sbAcquire);
        return string2;
    }

    public static String format(int i2) {
        return format(-1, i2);
    }

    private static char getZeroDigit(Locale locale) {
        if (locale == null) {
            throw new NullPointerException("locale == null");
        }
        if (!locale.equals(sLocale)) {
            sZeroDigit = new DecimalFormatSymbols(locale).getZeroDigit();
            sLocale = locale;
        }
        return sZeroDigit;
    }

    private static String localizeDigits(char c2, String str) {
        int length = str.length();
        int i2 = c2 - '0';
        StringBuilder sbAcquire = Pools.getStringBuilderPool().acquire();
        for (int i3 = 0; i3 < length; i3++) {
            char cCharAt = str.charAt(i3);
            if (cCharAt >= '0' && cCharAt <= '9') {
                cCharAt = (char) (cCharAt + i2);
            }
            sbAcquire.append(cCharAt);
        }
        String string = sbAcquire.toString();
        Pools.getStringBuilderPool().release(sbAcquire);
        return string;
    }

    public static String format(int i2, int i3) {
        char zeroDigit = getZeroDigit(Locale.getDefault());
        String strConvertInt = convertInt(i2, i3);
        return zeroDigit != '0' ? localizeDigits(zeroDigit, strConvertInt) : strConvertInt;
    }
}
