package com.miui.powerkeeper.feedbackcontrol;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes.dex */
public class ThreadPool {
    private static final ThreadPoolExecutor sExecutor;

    static {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(4, 16, 1L, TimeUnit.SECONDS, new LinkedBlockingQueue());
        sExecutor = threadPoolExecutor;
        threadPoolExecutor.allowCoreThreadTimeOut(true);
    }

    private ThreadPool() {
    }

    public static void execute(Runnable runnable) {
        sExecutor.execute(runnable);
    }
}
