package com.miui.powerkeeper.feedbackcontrol.abnormallog.log;

import android.content.Context;
import com.android.internal.os.BackgroundThread;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.AppInfo;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.BatteryInfo;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.BluetoothInfo;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.CpuInfo;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.GpsInfo;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.LogCollector;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.NetworkInfo;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.ScreenInfo;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.SensorInfo;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.TempInfo;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.Timestamp;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.TorchInfo;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.log.head.SceneManager;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.storage.Cache;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.utils.LogPrinter;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.utils.PublicSpace;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes.dex */
public class LogManager {
    private static final String TAG = "ThermalLogManager";
    private final Context mContext;
    private boolean mIsCollectAllInfo;
    private SceneManager mSceneManager;
    private RunnableScheduledFuture<?> mScheduledFuture;
    private ScheduledThreadPoolExecutor mScheduledThreadPoolExecutor;
    private boolean mIsWorking = false;
    private int mDivertPeriod = 30;
    private int mInitialDelay = 0;
    private final List<LogCollector> mLogCollectors = new ArrayList();
    private long mLastStopTime = 0;
    private final Runnable mCollectRunnable = new Runnable() { // from class: com.miui.powerkeeper.feedbackcontrol.abnormallog.log.LogManager.1
        @Override // java.lang.Runnable
        public void run() {
            LogManager.this.mPublicSpace.collectAllInfo(LogManager.this.mIsCollectAllInfo);
            LogManager.this.mLogCollectors.forEach(new Consumer() { // from class: com.miui.powerkeeper.feedbackcontrol.abnormallog.log.b
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((LogCollector) obj).collect();
                }
            });
            LogManager.this.mCache.add();
        }
    };
    private final LogPrinter mLog = LogPrinter.getLogPrinter();
    private final Cache mCache = Cache.getCache();
    private final PublicSpace mPublicSpace = PublicSpace.getInstance();

    public LogManager(Context context) {
        this.mContext = context;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCollectStop$0() {
        this.mCollectRunnable.run();
        this.mSceneManager.stopWork();
        this.mCache.commit();
        this.mPublicSpace.setCollectInfoMinInterval(System.currentTimeMillis() - this.mLastStopTime);
        this.mLastStopTime = System.currentTimeMillis();
        this.mIsWorking = false;
    }

    private void onCollectStart() {
        this.mIsCollectAllInfo = true;
        this.mScheduledThreadPoolExecutor.execute(this.mCollectRunnable);
    }

    private void onCollectStop() {
        this.mIsCollectAllInfo = true;
        this.mScheduledThreadPoolExecutor.execute(new Runnable() { // from class: com.miui.powerkeeper.feedbackcontrol.abnormallog.log.a
            @Override // java.lang.Runnable
            public final void run() {
                this.f399a.lambda$onCollectStop$0();
            }
        });
    }

    public void destroy() {
        this.mLog.d(TAG, "destroy");
        stopCollectInformation();
        this.mSceneManager.destroy();
    }

    public void init() {
        this.mLog.d(TAG, "log manager init");
        this.mLogCollectors.add(new Timestamp(this.mContext));
        this.mLogCollectors.add(new CpuInfo(this.mContext));
        this.mLogCollectors.add(new AppInfo(this.mContext));
        this.mLogCollectors.add(new BatteryInfo(this.mContext));
        this.mLogCollectors.add(new BluetoothInfo(this.mContext));
        this.mLogCollectors.add(new GpsInfo(this.mContext));
        this.mLogCollectors.add(new NetworkInfo(this.mContext));
        this.mLogCollectors.add(new ScreenInfo(this.mContext));
        this.mLogCollectors.add(new SensorInfo(this.mContext));
        this.mLogCollectors.add(new TempInfo(this.mContext));
        this.mLogCollectors.add(new TorchInfo(this.mContext));
    }

    public void start() {
        this.mScheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        this.mSceneManager = new SceneManager(BackgroundThread.getHandler().getLooper());
        init();
    }

    public void startCollectInformation() {
        this.mLog.d(TAG, "start collect information");
        if (this.mIsWorking || System.currentTimeMillis() - this.mLastStopTime < this.mPublicSpace.getCollectInfoMinInterval()) {
            return;
        }
        this.mLog.d(TAG, "logManager start work");
        this.mSceneManager.startWork();
        onCollectStart();
        this.mIsCollectAllInfo = false;
        this.mScheduledFuture = (RunnableScheduledFuture) this.mScheduledThreadPoolExecutor.scheduleAtFixedRate(this.mCollectRunnable, this.mInitialDelay, this.mDivertPeriod, TimeUnit.SECONDS);
        this.mIsWorking = true;
    }

    public void stop() {
        stopCollectInformation();
        this.mSceneManager.destroy();
        this.mSceneManager = null;
        this.mLogCollectors.clear();
    }

    public void stopCollectInformation() {
        this.mLog.d(TAG, "stop collect information");
        if (this.mIsWorking) {
            this.mLog.d(TAG, "logManager stop work");
            this.mScheduledThreadPoolExecutor.remove(this.mScheduledFuture);
            onCollectStop();
        }
    }
}
