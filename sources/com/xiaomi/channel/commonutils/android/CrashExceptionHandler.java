package com.xiaomi.channel.commonutils.android;

import com.xiaomi.channel.commonutils.logger.MyLog;

/* JADX INFO: loaded from: classes.dex */
public abstract class CrashExceptionHandler implements Thread.UncaughtExceptionHandler {
    public static final int PRIORITY_HIGH = 3;
    public static final int PRIORITY_LOW = 1;
    public static final int PRIORITY_MID = 2;

    public static void caughtSafeException(Throwable th, int i2) {
        Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (defaultUncaughtExceptionHandler instanceof CrashExceptionHandler) {
            ((CrashExceptionHandler) defaultUncaughtExceptionHandler).handleCrashException(Thread.currentThread(), th, i2);
            return;
        }
        MyLog.e("UncaughtHandler not ceh:" + defaultUncaughtExceptionHandler);
    }

    public abstract void handleCrashException(Thread thread, Throwable th, int i2);

    @Override // java.lang.Thread.UncaughtExceptionHandler
    public final void uncaughtException(Thread thread, Throwable th) {
        handleCrashException(thread, th, 3);
    }
}
