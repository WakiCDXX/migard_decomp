package com.miui.powerkeeper.feedbackcontrol.abnormallog.utils;

import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class LogPrinter {
    private static LogPrinter sLogPrinter;
    private boolean mIsDebug = true;

    public static LogPrinter getLogPrinter() {
        if (sLogPrinter == null) {
            sLogPrinter = new LogPrinter();
        }
        return sLogPrinter;
    }

    public void d(String str, String str2) {
        if (this.mIsDebug) {
            Log.d(str, str2);
        }
    }

    public void e(String str, String str2) {
        Log.e(str, str2);
    }

    public void setDebug(boolean z2) {
        this.mIsDebug = z2;
    }
}
