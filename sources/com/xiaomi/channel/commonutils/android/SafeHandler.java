package com.xiaomi.channel.commonutils.android;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/* JADX INFO: loaded from: classes.dex */
public class SafeHandler extends Handler {
    public SafeHandler(Looper looper) {
        super(looper);
    }

    @Override // android.os.Handler
    public void dispatchMessage(Message message) {
        try {
            super.dispatchMessage(message);
        } catch (Throwable th) {
            CrashExceptionHandler.caughtSafeException(th, 2);
        }
    }

    public SafeHandler(Looper looper, Handler.Callback callback) {
        super(looper, callback);
    }
}
