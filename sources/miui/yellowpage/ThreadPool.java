package miui.yellowpage;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes.dex */
public class ThreadPool {
    private static int THREAD_POOL_COUNT = 32;
    private static ThreadPoolExecutor sExecutor;

    static {
        int i2 = THREAD_POOL_COUNT;
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(i2, i2, 1L, TimeUnit.SECONDS, new LinkedBlockingQueue());
        sExecutor = threadPoolExecutor;
        threadPoolExecutor.allowCoreThreadTimeOut(true);
    }

    private ThreadPool() {
    }

    public static void execute(Runnable runnable) {
        sExecutor.execute(runnable);
    }
}
