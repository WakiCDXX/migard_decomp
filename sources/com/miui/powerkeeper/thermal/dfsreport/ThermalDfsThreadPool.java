package com.miui.powerkeeper.thermal.dfsreport;

import android.util.Log;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes.dex */
public class ThermalDfsThreadPool {
    private static final String TAG = "powerkeeper.dfsTPool";
    private static final int WAIT_TERMINATION_TIME = 10;
    private static int mNumberOfCores = Runtime.getRuntime().availableProcessors();
    private static ThermalDfsThreadPool mThermalDfsThreadPoolInstance;
    private ExecutorService mFixThreadPool = Executors.newFixedThreadPool(mNumberOfCores);
    private ScheduledExecutorService mScheduledExecutorService = Executors.newScheduledThreadPool(mNumberOfCores);

    private ThermalDfsThreadPool() {
    }

    public static synchronized ThermalDfsThreadPool getThermalDfsThreadPoolInstance() {
        try {
            if (mThermalDfsThreadPoolInstance == null) {
                mThermalDfsThreadPoolInstance = new ThermalDfsThreadPool();
            }
        } catch (Throwable th) {
            throw th;
        }
        return mThermalDfsThreadPoolInstance;
    }

    public ExecutorService getFixThreadPool() {
        return this.mFixThreadPool;
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return this.mScheduledExecutorService;
    }

    public void stopThreadTask() {
        this.mFixThreadPool.shutdown();
        this.mScheduledExecutorService.shutdown();
        try {
            if (!this.mFixThreadPool.awaitTermination(10L, TimeUnit.SECONDS)) {
                Log.d(TAG, "cannot wait 10 to stop fixthreadpool");
                this.mFixThreadPool.shutdownNow();
            }
        } catch (InterruptedException unused) {
            Log.e(TAG, "fixThreadPool wait 10 has been interrupt");
            this.mFixThreadPool.shutdownNow();
        }
        try {
            if (!this.mScheduledExecutorService.awaitTermination(10L, TimeUnit.SECONDS)) {
                Log.d(TAG, "cannot wait 10 to stop scheduledExecutorService");
                this.mScheduledExecutorService.shutdownNow();
            }
        } catch (InterruptedException unused2) {
            Log.e(TAG, "scheduledExecutorService wait 10 has been interrupt");
            this.mScheduledExecutorService.shutdownNow();
        }
        if (this.mFixThreadPool.isShutdown()) {
            this.mFixThreadPool = Executors.newFixedThreadPool(mNumberOfCores);
            Log.d(TAG, "fixThreadPool is shutdown");
        }
        if (this.mScheduledExecutorService.isShutdown()) {
            this.mScheduledExecutorService = Executors.newScheduledThreadPool(mNumberOfCores);
            Log.d(TAG, "mScheduledExecutorService is shutdown");
        }
    }
}
