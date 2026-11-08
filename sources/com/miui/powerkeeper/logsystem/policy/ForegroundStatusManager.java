package com.miui.powerkeeper.logsystem.policy;

import android.annotation.NonNull;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.logsystem.utils.LSUtils;
import java.util.ArrayList;
import java.util.Iterator;
import miui.process.ForegroundInfo;

/* JADX INFO: loaded from: classes.dex */
public class ForegroundStatusManager extends Handler implements IWork {
    private static final String SCREEN_OFF_NAME = "screen_off";
    private static final int SCREEN_OFF_UID = -10101;
    public static final String TAG = "LS-ForegroundStatusManager";
    private Context mContext;
    private int mCurrentForegroundUid;
    private AppStates mLastAppStates;
    private int mLastForegroundUid;
    private PowerKeeperInterface.f0 mScreenChangedCallback;
    private volatile boolean mWorking;
    private ArrayList<AppStates> mForegroundApps = new ArrayList<>();
    private PowerKeeperInterface.z mScreenReceiver = new PowerKeeperInterface.z() { // from class: com.miui.powerkeeper.logsystem.policy.ForegroundStatusManager.1
        @Override // com.miui.powerkeeper.PowerKeeperInterface.z
        public void onScreenChanged(boolean z2) {
            if (!z2) {
                ForegroundStatusManager.this.updateForegroundApps(ForegroundStatusManager.SCREEN_OFF_UID);
            } else if (ForegroundStatusManager.this.mLastForegroundUid == 0) {
                ForegroundStatusManager.this.resetData();
            } else {
                ForegroundStatusManager foregroundStatusManager = ForegroundStatusManager.this;
                foregroundStatusManager.updateForegroundApps(foregroundStatusManager.mLastForegroundUid);
            }
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.z
        public void onRegistered(boolean z2) {
        }
    };
    private PowerKeeperManager mPowerKeeperManager = PowerKeeperManager.getInstance();

    class AppStates {
        String pkgName;
        WatchTimer timer;
        int uid;

        AppStates() {
        }
    }

    class WatchTimer {
        int nest = 0;
        long startTime;
        long totleTime;

        WatchTimer() {
        }

        void startRunning() {
            if (this.nest > 0) {
                return;
            }
            this.startTime = System.currentTimeMillis();
            this.nest++;
        }

        void stopRunning() {
            this.totleTime += System.currentTimeMillis() - this.startTime;
            this.startTime = 0L;
            this.nest--;
        }
    }

    ForegroundStatusManager(Context context) {
        this.mContext = context;
        init();
    }

    private String getPkgNameByUid(int i2) {
        String[] packagesForUid = this.mContext.getPackageManager().getPackagesForUid(i2);
        if (packagesForUid == null || packagesForUid.length <= 0) {
            return null;
        }
        return packagesForUid[0];
    }

    private void init() {
        PowerKeeperManager.getInstance().getEventsAggregator().registerForForegroundInfo(this);
        PowerKeeperInterface.f0 f0Var = new PowerKeeperInterface.f0(null, this.mScreenReceiver);
        this.mScreenChangedCallback = f0Var;
        this.mPowerKeeperManager.registerScreenChangedListener(f0Var);
    }

    private boolean isValidScene() {
        Iterator<AppStates> it = this.mForegroundApps.iterator();
        long j2 = 0;
        long j3 = 0;
        while (it.hasNext()) {
            long j4 = it.next().timer.totleTime;
            j2 += j4;
            if (j4 > j3) {
                j3 = j4;
            }
        }
        Log.d(TAG, "isValidScene totalTime = " + j2 + ", maxTime = " + j3);
        return j2 > (j2 - j3) * 20;
    }

    private void onForegroundChanged(int i2) {
        this.mCurrentForegroundUid = i2;
        this.mLastForegroundUid = i2;
        if (this.mWorking) {
            updateForegroundApps(i2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetData() {
        this.mForegroundApps.clear();
        this.mLastAppStates = null;
        this.mWorking = false;
    }

    private void stopCurrentAppTimer() {
        for (AppStates appStates : this.mForegroundApps) {
            if (appStates.uid == this.mCurrentForegroundUid) {
                appStates.timer.stopRunning();
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateForegroundApps(int i2) {
        this.mCurrentForegroundUid = i2;
        if (this.mWorking) {
            AppStates appStates = this.mLastAppStates;
            if (appStates != null) {
                if (appStates.uid == i2) {
                    appStates.timer.startRunning();
                    return;
                }
                appStates.timer.stopRunning();
            }
            for (AppStates appStates2 : this.mForegroundApps) {
                if (appStates2.uid == i2) {
                    appStates2.timer.startRunning();
                    this.mLastAppStates = appStates2;
                    return;
                }
            }
            AppStates appStates3 = new AppStates();
            appStates3.uid = i2;
            if (i2 != SCREEN_OFF_UID) {
                String pkgNameByUid = getPkgNameByUid(i2);
                appStates3.pkgName = pkgNameByUid;
                if (LSUtils.isEmptyString(pkgNameByUid)) {
                    return;
                }
            } else {
                appStates3.pkgName = "screen_off";
            }
            WatchTimer watchTimer = new WatchTimer();
            appStates3.timer = watchTimer;
            watchTimer.startRunning();
            this.mForegroundApps.add(appStates3);
            this.mLastAppStates = appStates3;
        }
    }

    @Override // com.miui.powerkeeper.logsystem.policy.IWork
    public void destroy() {
        PowerKeeperManager.getInstance().getEventsAggregator().unregisterForForegroundInfo(this);
        this.mPowerKeeperManager.unregisterScreenChangedListener(this.mScreenChangedCallback);
        this.mScreenChangedCallback.a();
        this.mScreenChangedCallback = null;
    }

    String getForegroundPkgName() {
        String str = null;
        if (!this.mWorking) {
            return null;
        }
        stopCurrentAppTimer();
        if (!isValidScene()) {
            return null;
        }
        long j2 = 0;
        for (AppStates appStates : this.mForegroundApps) {
            Log.d(TAG, "getForegroundPkgName mForegroundApps = " + appStates.pkgName + ", timer = " + appStates.timer);
            long j3 = appStates.timer.totleTime;
            if (j3 > j2) {
                str = appStates.pkgName;
                j2 = j3;
            }
        }
        Log.d(TAG, "getForegroundPkgName pkg = " + str);
        return str;
    }

    @Override // android.os.Handler
    public void handleMessage(@NonNull Message message) {
        int i2;
        if (message.what != -9 || (i2 = ((ForegroundInfo) message.obj).mForegroundUid) == this.mCurrentForegroundUid) {
            return;
        }
        onForegroundChanged(i2);
    }

    @Override // com.miui.powerkeeper.logsystem.policy.IWork
    public void startWorking() {
        resetData();
        this.mWorking = true;
        updateForegroundApps(this.mCurrentForegroundUid);
    }

    @Override // com.miui.powerkeeper.logsystem.policy.IWork
    public void stopWorking() {
        this.mWorking = false;
    }
}
