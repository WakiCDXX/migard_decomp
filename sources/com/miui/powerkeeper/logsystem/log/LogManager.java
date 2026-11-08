package com.miui.powerkeeper.logsystem.log;

import android.content.Context;
import android.util.ArrayMap;
import android.util.Log;
import com.android.internal.util.IndentingPrintWriter;
import com.miui.powerkeeper.logsystem.config.ConfigManager;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.logsystem.config.Scene;
import com.miui.powerkeeper.logsystem.log.logcatch.FileCatch;
import com.miui.powerkeeper.logsystem.log.logcatch.IndexCatch;
import com.miui.powerkeeper.logsystem.log.logcatch.MemCache;
import com.miui.powerkeeper.logsystem.log.sampling.Clock;
import com.miui.powerkeeper.logsystem.log.sampling.CommonStaticLog;
import com.miui.powerkeeper.logsystem.log.sampling.CpuFreq;
import com.miui.powerkeeper.logsystem.log.sampling.CurrentSampling;
import com.miui.powerkeeper.logsystem.log.sampling.Interrupt;
import com.miui.powerkeeper.logsystem.log.sampling.MobileData;
import com.miui.powerkeeper.logsystem.log.sampling.NetworkMode;
import com.miui.powerkeeper.logsystem.log.sampling.SamplingCatchedLog;
import com.miui.powerkeeper.logsystem.log.sampling.Top;
import com.miui.powerkeeper.logsystem.log.sampling.WifiData;
import com.miui.powerkeeper.logsystem.log.system.CpuLog;
import com.miui.powerkeeper.logsystem.log.system.DisplayLog;
import com.miui.powerkeeper.logsystem.log.system.KernelLog;
import com.miui.powerkeeper.logsystem.log.system.PerfserviceLog;
import com.miui.powerkeeper.logsystem.log.system.SensorLog;
import com.miui.powerkeeper.logsystem.log.system.SurfaceFlingerLog;
import com.miui.powerkeeper.logsystem.policy.PowerCheckPolicy;
import com.miui.powerkeeper.logsystem.utils.LSUtils;
import com.miui.powerkeeper.tracker.TrackerManager;
import com.miui.powerkeeper.utils.SharedPrefUtil;
import com.xiaomi.mipush.sdk.stat.db.MessageInfoContract;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/* JADX INFO: loaded from: classes.dex */
public class LogManager {
    public static final int FREQ_SAMPLING_FREQ = 1000;
    public static final int FREQ_SAMPLING_MAX_TIMES = 60;
    private static final String TAG = "LS-LogManager";
    private static final int TIMED_SAMPLING_INTERVAL = 30000;
    private static final int TIMED_SAMPLING_MAX_TIMES = 3;
    private CommonStaticLog mCommonStaticLog;
    private Context mContext;
    private CurrentSampling mCurrentSampling;
    private Timer mFreqSamplingTimer;
    private int mFreqSamplingTimes;
    private long mLoggingStartTime;
    private PowerCheckPolicy mPolicy;
    public ShellCmdLogs mShellCmdLogs;
    private Timer mTimedSamplingTimer;
    private int mTimedSamplingTimes;
    private ArrayList<LogInterface> mInterfaces = new ArrayList<>();
    private int mCurrentSceneId = -1;
    private volatile int mLogCatchingTask = 0;
    private volatile boolean mWorking = false;
    private volatile boolean mLogging = false;
    private boolean mCatchLogCommitted = false;

    public LogManager(Context context) {
        this.mContext = context;
        this.mPolicy = new PowerCheckPolicy(context);
        initInterface();
        this.mShellCmdLogs = new ShellCmdLogs(this.mContext);
    }

    private void catchSamplingLog(Scene scene) {
        for (LogInterface logInterface : this.mInterfaces) {
            if (isSutabileLogType(scene, logInterface, LogType.LOG_TYPE_SAMPLING_MASK)) {
                if (logInterface instanceof SamplingCatchedLog) {
                    ((SamplingCatchedLog) logInterface).catchLog();
                } else {
                    Log.d(TAG, "Type not support MemCatch.");
                }
            }
        }
    }

    private boolean checkPower() {
        Log.d(TAG, "checkPower threadId:" + Thread.currentThread().getId());
        Scene currentScene = this.mPolicy.getCurrentScene();
        if (currentScene == null) {
            return false;
        }
        this.mCurrentSceneId = currentScene.getId();
        boolean zIsPowerAbnormal = this.mPolicy.isPowerAbnormal(currentScene);
        if (zIsPowerAbnormal) {
            zIsPowerAbnormal = getLog();
        }
        Log.d(TAG, "checkPower isAbnormal = " + zIsPowerAbnormal);
        return zIsPowerAbnormal;
    }

    private void clearLog(int i2) {
        String stringValue = SharedPrefUtil.getInstance(this.mContext, "log_system_config").getStringValue(IndexCatch.getSPkey(i2), "");
        String[] strArrSplit = stringValue.split(";");
        long jCurrentTimeMillis = System.currentTimeMillis();
        for (String str : strArrSplit) {
            long longValue = LSUtils.getLongValue(str);
            if (jCurrentTimeMillis - longValue >= ConfigManager.getInstance(this.mContext).getClearIntervalMills()) {
                Iterator<LogInterface> it = this.mInterfaces.iterator();
                boolean zClearSystemLog = true;
                while (it.hasNext()) {
                    zClearSystemLog &= it.next().clearSystemLog(this.mCurrentSceneId, longValue);
                }
                if (zClearSystemLog) {
                    stringValue = IndexCatch.deleteFileIndex(stringValue, str);
                }
            }
        }
        SharedPrefUtil.getInstance(this.mContext, "log_system_config").save(IndexCatch.getSPkey(i2), stringValue);
    }

    private void commitCatchLog() {
        synchronized (this) {
            try {
                if (!this.mCatchLogCommitted) {
                    this.mCatchLogCommitted = true;
                    Log.d(TAG, "Write the catch to file: " + FileCatch.getInstance(this.mContext).getFilePath(this.mCurrentSceneId, this.mLoggingStartTime, this.mCommonStaticLog));
                    MemCache.getInstance().commit(FileCatch.getInstance(this.mContext).getFilePath(this.mCurrentSceneId, this.mLoggingStartTime, this.mCommonStaticLog));
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private void dumpAllTypeLogs(PrintWriter printWriter, int i2, String str) {
        this.mCommonStaticLog.dumpSystemLog(printWriter, i2, str);
        for (LogInterface logInterface : this.mInterfaces) {
            if (logInterface.getLogCatchType() != 1) {
                logInterface.dumpSystemLog(printWriter, i2, str);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getCurrentSamplingLogs() {
        Scene sceneById = ConfigManager.getInstance(this.mContext).getSceneById(this.mCurrentSceneId);
        if (sceneById == null || sceneById.getLogType() <= 0) {
            return;
        }
        this.mCurrentSampling.produceSystemLog(this.mCurrentSceneId, this.mLoggingStartTime);
        Log.d(TAG, "mFreqSamplingTimes = " + this.mFreqSamplingTimes + ", mLogging = " + this.mLogging + ", mLogCatchingTask = " + this.mLogCatchingTask);
        int i2 = this.mFreqSamplingTimes + 1;
        this.mFreqSamplingTimes = i2;
        if (i2 >= 60) {
            this.mFreqSamplingTimer.cancel();
            this.mCurrentSampling.catchLog();
            synchronized (this) {
                try {
                    int i3 = this.mLogCatchingTask - 1;
                    this.mLogCatchingTask = i3;
                    if (i3 <= 0) {
                        this.mLogging = false;
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        if (this.mLogging) {
            return;
        }
        commitCatchLog();
    }

    private boolean getLog() {
        synchronized (this) {
            this.mCatchLogCommitted = false;
        }
        this.mTimedSamplingTimes = 0;
        this.mFreqSamplingTimes = 0;
        Scene sceneById = ConfigManager.getInstance(this.mContext).getSceneById(this.mCurrentSceneId);
        if (sceneById == null || sceneById.getLogType() <= 0) {
            return false;
        }
        if (sceneById.getTimestamp(this.mContext) > 0 && System.currentTimeMillis() - sceneById.getTimestamp(this.mContext) < ConfigManager.getInstance(this.mContext).getMinIntervelMills()) {
            Log.d(TAG, "in interval, discard!");
            return false;
        }
        this.mLogging = true;
        this.mLoggingStartTime = System.currentTimeMillis();
        BaseLog.setWorkingScene(sceneById);
        this.mCommonStaticLog.catchFileIndex(this.mCurrentSceneId, this.mLoggingStartTime);
        MemCache.getInstance().reset();
        ArrayMap arrayMap = new ArrayMap();
        arrayMap.put("scenario_name", sceneById.getName());
        arrayMap.put(LSUtils.PARAM_AVG_CURRENT, String.format(Locale.getDefault(), "%.2f", Double.valueOf(this.mPolicy.getAvgCurrent())));
        TrackerManager.getInstance(this.mContext).track(5, LSUtils.ONE_TRACK_EVENT, arrayMap);
        this.mCommonStaticLog.produceSystemLog(this.mCurrentSceneId, this.mLoggingStartTime);
        boolean z2 = false;
        boolean z3 = false;
        for (LogInterface logInterface : this.mInterfaces) {
            if (isSutabileLogType(sceneById, logInterface, 65535)) {
                logInterface.produceSystemLog(this.mCurrentSceneId, this.mLoggingStartTime);
            }
            if (isSutabileLogType(sceneById, logInterface, LogType.LOG_TYPE_SAMPLING_MASK)) {
                z2 = true;
            }
            if (isSutabileLogType(sceneById, logInterface, LogType.LOG_TYPE_F_SAMPLING_CURRENT)) {
                z3 = true;
            }
        }
        clearLog(this.mCurrentSceneId);
        this.mLogging = false;
        if (z2) {
            this.mLogging = true;
            scheduleTimedSamplingTimer();
        }
        if (z3) {
            this.mLogging = true;
            scheduleFreqSamplingTimer();
        }
        if (!z2 && !z3) {
            commitCatchLog();
        }
        sceneById.setTimestamp(this.mContext, System.currentTimeMillis());
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getTimedSamplingLogs() {
        Scene sceneById = ConfigManager.getInstance(this.mContext).getSceneById(this.mCurrentSceneId);
        if (sceneById == null || sceneById.getLogType() <= 0) {
            return;
        }
        for (LogInterface logInterface : this.mInterfaces) {
            if (isSutabileLogType(sceneById, logInterface, LogType.LOG_TYPE_SAMPLING_MASK)) {
                logInterface.produceSystemLog(this.mCurrentSceneId, this.mLoggingStartTime);
            }
        }
        Log.d(TAG, "mTimedSamplingTimes = " + this.mTimedSamplingTimes + ", mLogging = " + this.mLogging + ", mLogCatchingTask = " + this.mLogCatchingTask);
        int i2 = this.mTimedSamplingTimes + 1;
        this.mTimedSamplingTimes = i2;
        if (i2 >= 3) {
            this.mTimedSamplingTimer.cancel();
            catchSamplingLog(sceneById);
            synchronized (this) {
                try {
                    int i3 = this.mLogCatchingTask - 1;
                    this.mLogCatchingTask = i3;
                    if (i3 <= 0) {
                        this.mLogging = false;
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        if (this.mLogging) {
            return;
        }
        commitCatchLog();
    }

    private void initInterface() {
        this.mCommonStaticLog = new CommonStaticLog(this.mContext);
        this.mInterfaces.add(new CpuLog(this.mContext));
        this.mInterfaces.add(new DisplayLog(this.mContext));
        this.mInterfaces.add(new KernelLog(this.mContext));
        this.mInterfaces.add(new SensorLog(this.mContext));
        this.mInterfaces.add(new SurfaceFlingerLog(this.mContext));
        this.mInterfaces.add(new PerfserviceLog(this.mContext));
        this.mInterfaces.add(new Clock(this.mContext));
        this.mInterfaces.add(new Interrupt(this.mContext));
        this.mInterfaces.add(new MobileData(this.mContext));
        this.mInterfaces.add(new WifiData(this.mContext));
        this.mInterfaces.add(new NetworkMode(this.mContext));
        this.mInterfaces.add(new Top(this.mContext));
        this.mInterfaces.add(new CpuFreq(this.mContext));
        CurrentSampling currentSampling = new CurrentSampling(this.mContext);
        this.mCurrentSampling = currentSampling;
        this.mInterfaces.add(currentSampling);
    }

    private boolean isSutabileLogType(Scene scene, LogInterface logInterface, int i2) {
        int interfaceLogType = logInterface.getInterfaceLogType();
        return ((scene.getLogType() & interfaceLogType) == 0 || !ConfigManager.getInstance(this.mContext).isLogTypeSupport(interfaceLogType) || (interfaceLogType & i2) == 0) ? false : true;
    }

    private void rescheduleWork() {
        this.mPolicy.stopWorking();
        this.mPolicy.startWorking();
        this.mWorking = true;
    }

    private void scheduleFreqSamplingTimer() {
        synchronized (this) {
            this.mLogCatchingTask++;
        }
        Timer timer = new Timer();
        this.mFreqSamplingTimer = timer;
        timer.schedule(new TimerTask() { // from class: com.miui.powerkeeper.logsystem.log.LogManager.1
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                LogManager.this.getCurrentSamplingLogs();
            }
        }, 0L, 1000L);
    }

    private void scheduleTimedSamplingTimer() {
        synchronized (this) {
            this.mLogCatchingTask++;
        }
        Timer timer = new Timer();
        this.mTimedSamplingTimer = timer;
        timer.schedule(new TimerTask() { // from class: com.miui.powerkeeper.logsystem.log.LogManager.2
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                LogManager.this.getTimedSamplingLogs();
            }
        }, 0L, MessageInfoContract.TIMEOUT);
    }

    public void destroy() {
        this.mPolicy.destroy();
        this.mShellCmdLogs.destroy();
    }

    public void dumpInterfaceLog(String[] strArr) {
        if (strArr == null || strArr.length != 2) {
            return;
        }
        for (LogInterface logInterface : this.mInterfaces) {
            if (logInterface.getInterfaceName().equals(strArr[1])) {
                if ((logInterface.getInterfaceLogType() & 65535) != 0) {
                    logInterface.produceSystemLog(this.mCurrentSceneId, this.mLoggingStartTime);
                } else if ((logInterface.getInterfaceLogType() & LogType.LOG_TYPE_SAMPLING_MASK) != 0) {
                    scheduleTimedSamplingTimer();
                }
            }
        }
    }

    public void dumpLog(PrintWriter printWriter) {
        int[] validSceneIds = ConfigManager.getInstance(this.mContext).getValidSceneIds();
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "    ");
        indentingPrintWriter.increaseIndent();
        for (int i2 : validSceneIds) {
            String stringValue = SharedPrefUtil.getInstance(this.mContext, "log_system_config").getStringValue(IndexCatch.getSPkey(i2), "");
            if (!LSUtils.isEmptyString(stringValue)) {
                Scene sceneById = ConfigManager.getInstance(this.mContext).getSceneById(i2);
                if (sceneById == null) {
                    return;
                }
                printWriter.println("^_^^_^ Current Scene : " + sceneById.getName());
                String[] strArrSplit = stringValue.split(";");
                int length = strArrSplit.length;
                int i3 = 0;
                int i4 = 0;
                while (i3 < length) {
                    String str = strArrSplit[i3];
                    indentingPrintWriter.println("Time" + i4 + " : " + LSUtils.getStringDate(LSUtils.getLongValue(str)));
                    dumpAllTypeLogs(indentingPrintWriter, i2, str);
                    i3++;
                    i4++;
                }
            }
        }
    }

    public void noteBatteryStatsChanged() {
        if (this.mLogging) {
            Log.d(TAG, "noteBatteryStatsChanged, In Logging status, discard this stats.");
            return;
        }
        if (!this.mWorking) {
            this.mWorking = true;
            this.mPolicy.startWorking();
            return;
        }
        this.mWorking = false;
        if (checkPower()) {
            this.mPolicy.stopWorking();
        } else {
            rescheduleWork();
        }
    }
}
