package com.xiaomi.channel.commonutils.android;

/* JADX INFO: loaded from: classes.dex */
public abstract class SafeRunnable implements Runnable {
    private String mName;

    public SafeRunnable() {
        this("");
    }

    @Override // java.lang.Runnable
    public final void run() {
        try {
            safeRun();
        } catch (Throwable th) {
            CrashExceptionHandler.caughtSafeException(th, 1);
        }
    }

    public abstract void safeRun();

    public SafeRunnable(String str) {
        this.mName = str;
    }
}
