package com.miui.powerkeeper.logsystem.log.logcatch;

/* JADX INFO: loaded from: classes.dex */
public class IndexCatch {
    private static final String SP_KEY_INDEX = "LogInterface";
    public static final String VALUE_DIVIDER = ";";

    private IndexCatch() {
    }

    public static String deleteFileIndex(String str, String str2) {
        if (str.length() <= str2.length()) {
            return "";
        }
        return str.startsWith(str2) ? str.substring(str2.length() + 1) : str.substring(0, (str.length() - str2.length()) - 1);
    }

    public static String getSPkey(int i2) {
        return "LogInterface_" + i2;
    }
}
