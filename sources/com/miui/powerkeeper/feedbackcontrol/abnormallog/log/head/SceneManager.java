package com.miui.powerkeeper.feedbackcontrol.abnormallog.log.head;

import android.annotation.NonNull;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.ArrayMap;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.utils.LogPrinter;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.utils.PublicSpace;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.utils.Tools;
import com.xiaomi.mipush.sdk.AssemblePushUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import miui.process.ForegroundInfo;

/* JADX INFO: loaded from: classes.dex */
public class SceneManager extends Handler {
    private final String SCREEN_OFF;
    private final String TAG;
    private String mCurrentForeground;
    private List<String> mForegroundList;
    private Long mForegroundStartTime;
    private String mLastForeground;
    private final LogPrinter mLog;
    private final PowerKeeperManager mPowerKeeperManager;
    private final PublicSpace mPublicSpace;
    private Map<String, Long> mSceneMap;
    private PowerKeeperInterface.f0 mScreenChangedCallback;
    private PowerKeeperInterface.z mScreenReceiver;
    private boolean mWorking;

    public SceneManager(@NonNull Looper looper) {
        super(looper);
        this.TAG = "SceneManager";
        this.SCREEN_OFF = "screen.off";
        this.mWorking = false;
        this.mLog = LogPrinter.getLogPrinter();
        this.mScreenReceiver = new PowerKeeperInterface.z() { // from class: com.miui.powerkeeper.feedbackcontrol.abnormallog.log.head.SceneManager.1
            @Override // com.miui.powerkeeper.PowerKeeperInterface.z
            public void onRegistered(boolean z2) {
                if (z2) {
                    return;
                }
                SceneManager.this.mCurrentForeground = "screen.off";
            }

            @Override // com.miui.powerkeeper.PowerKeeperInterface.z
            public void onScreenChanged(boolean z2) {
                if (!z2) {
                    SceneManager.this.onForegroundChange("screen.off");
                } else {
                    SceneManager sceneManager = SceneManager.this;
                    sceneManager.onForegroundChange(sceneManager.mLastForeground);
                }
            }
        };
        this.mPublicSpace = PublicSpace.getInstance();
        this.mPowerKeeperManager = PowerKeeperManager.getInstance();
        init();
    }

    private void init() {
        this.mLog.d("SceneManager", AssemblePushUtils.REASON_SYNC_TOKEN_INIT);
        this.mPowerKeeperManager.getEventsAggregator().registerForForegroundInfo(this);
        PowerKeeperInterface.f0 f0Var = new PowerKeeperInterface.f0(this, this.mScreenReceiver);
        this.mScreenChangedCallback = f0Var;
        this.mPowerKeeperManager.registerScreenChangedListener(f0Var);
        this.mSceneMap = new ArrayMap();
        this.mForegroundList = new ArrayList();
        this.mPublicSpace.setSceneMap(this.mSceneMap);
        this.mPublicSpace.setForegroundList(this.mForegroundList);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onForegroundChange(String str) {
        this.mLog.d("SceneManager", "on foreground change: " + str);
        this.mLastForeground = this.mCurrentForeground;
        this.mCurrentForeground = str;
        this.mPublicSpace.setCurrentForeground(str);
        if (this.mWorking) {
            Long l2 = this.mSceneMap.get(this.mLastForeground);
            Long lValueOf = Long.valueOf((l2 == null ? System.currentTimeMillis() : l2.longValue() + System.currentTimeMillis()) - this.mForegroundStartTime.longValue());
            this.mForegroundStartTime = Long.valueOf(System.currentTimeMillis());
            this.mSceneMap.put(this.mLastForeground, lValueOf);
            this.mForegroundList.add(Tools.getTimestamp() + ": " + this.mCurrentForeground);
        }
    }

    public void destroy() {
        this.mLog.d("SceneManager", "destroy");
        PowerKeeperManager.getInstance().getEventsAggregator().unregisterForForegroundInfo(this);
        this.mPowerKeeperManager.unregisterScreenChangedListener(this.mScreenChangedCallback);
        getLooper().quitSafely();
    }

    public String getCurrentForeground() {
        return this.mCurrentForeground;
    }

    public List<String> getForegroundList() {
        return this.mForegroundList;
    }

    @Override // android.os.Handler
    public void handleMessage(@NonNull Message message) {
        if (message.what == -9) {
            onForegroundChange(((ForegroundInfo) message.obj).mForegroundPackageName);
        }
    }

    public void startWork() {
        this.mLog.d("SceneManager", "start");
        this.mWorking = true;
        this.mForegroundStartTime = Long.valueOf(System.currentTimeMillis());
        this.mForegroundList.add(Tools.getTimestamp() + ": " + this.mCurrentForeground);
    }

    public void stopWork() {
        this.mLog.d("SceneManager", "stop");
        Long l2 = this.mSceneMap.get(this.mCurrentForeground);
        this.mSceneMap.put(this.mCurrentForeground, Long.valueOf((l2 == null ? System.currentTimeMillis() : l2.longValue() + System.currentTimeMillis()) - this.mForegroundStartTime.longValue()));
        this.mWorking = false;
    }
}
