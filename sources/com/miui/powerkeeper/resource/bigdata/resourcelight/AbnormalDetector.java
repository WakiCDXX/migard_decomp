package com.miui.powerkeeper.resource.bigdata.resourcelight;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.batterylife.systeminfo.BSHelper;
import com.miui.powerkeeper.statemachine.SleepModeControllerNew;
import com.xiaomi.push.extension.PluginConstants;

/* JADX INFO: loaded from: classes.dex */
public class AbnormalDetector implements PowerKeeperInterface.a0 {
    public static final long CURRENTNOW_STANDARD;
    private static final boolean DEBUG;
    public static final long HOUR_TO_MS = 3600000;
    public static String TAG = "AbnormalDetctor";
    public static String TAG_OPT = "OPT-TF-Power";
    private Context mContext;
    private final OriginalData mOriginalData;

    static {
        boolean zIsLoggable = Log.isLoggable("debug.detector", 3);
        DEBUG = zIsLoggable;
        CURRENTNOW_STANDARD = zIsLoggable ? 1L : 20L;
    }

    public AbnormalDetector(Context context) {
        this.mContext = null;
        if (DEBUG) {
            Log.d(TAG, "AbnormalDetector()");
        }
        this.mOriginalData = new OriginalData();
        this.mContext = context;
    }

    public void checkStandbyAvCurrent(OriginalData originalData) {
        long j2 = originalData.mStopTimeMs - originalData.mStartTimeMs;
        if (j2 <= 0) {
            throw new IllegalArgumentException("Elapsed time must be greater than zero.");
        }
        int i2 = (int) (((originalData.mStartChargeCounter - originalData.mStopChargeCounter) * 3600000.0d) / j2);
        if (i2 > CURRENTNOW_STANDARD) {
            Log.e(TAG_OPT, "avgCurrent= " + i2);
        }
    }

    public void init() {
        Log.d(TAG, "init()");
        new Thread(new Runnable() { // from class: com.miui.powerkeeper.resource.bigdata.resourcelight.AbnormalDetector.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    Thread.sleep(PluginConstants.TTS_REMOVE_BEAN_TIME);
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                    Thread.currentThread().interrupt();
                }
                SleepModeControllerNew sleepModeControllerNew = SleepModeControllerNew.getInstance();
                if (sleepModeControllerNew != null) {
                    Log.d(AbnormalDetector.TAG, "register()");
                    sleepModeControllerNew.registerSleepListener(AbnormalDetector.this);
                }
            }
        }).start();
    }

    @Override // com.miui.powerkeeper.PowerKeeperInterface.a0
    public void onSleepMode(boolean z2) {
        Log.d(TAG, "onSleepMode start=" + z2);
        if (z2) {
            startRecoderBatterStats();
        } else {
            stopRecoderBatterStats();
        }
    }

    public void startRecoderBatterStats() {
        Log.d(TAG, "startRecoderBatterStats");
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        double currentBatteryCap = BSHelper.getCurrentBatteryCap();
        this.mOriginalData.reset();
        OriginalData originalData = this.mOriginalData;
        originalData.mStartTimeMs = jElapsedRealtime;
        originalData.mStartChargeCounter = currentBatteryCap;
    }

    public void stopRecoderBatterStats() {
        Log.d(TAG, "stopRecoderBatterStats");
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        double currentBatteryCap = BSHelper.getCurrentBatteryCap();
        OriginalData originalData = this.mOriginalData;
        originalData.mStopTimeMs = jElapsedRealtime;
        originalData.mStopChargeCounter = currentBatteryCap;
        checkStandbyAvCurrent(originalData);
    }
}
