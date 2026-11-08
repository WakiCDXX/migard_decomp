package com.miui.powerkeeper.utils;

import android.annotation.Nullable;
import android.app.ActivityManager;
import android.app.IMiuiProcessObserver;
import android.app.IProcessObserver;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.RemoteException;
import android.util.LocalLog;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.IndentingPrintWriter;
import com.google.android.collect.Lists;
import com.miui.powerkeeper.ForegroundServiceManager;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.event.EventsAggregator;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import miui.app.IFreeformCallback;
import miui.app.MiuiFreeFormManager;
import miui.process.ForegroundInfo;
import miui.process.IForegroundInfoListener;
import miui.process.ProcessManager;
import miui.process.RunningProcessInfo;

/* JADX INFO: loaded from: classes.dex */
public class ProcessObserver {
    private static final boolean DEBUG = Log.isLoggable("power.process", 3);
    private static final int LOCAL_LOG_SIZE = 160;
    private static final String TAG = "ProcessObserver";
    private Context mContext;
    private EventsAggregator mEventsAggregator;
    private Handler mHandler;
    private HandlerThread mThread;
    private final Object mRulesLock = new Object();
    private SparseIntArray mUidState = new SparseIntArray();
    private SparseArray<SparseIntArray> mUidPidState = new SparseArray<>();
    private SparseIntArray mUidServiceState = new SparseIntArray();
    private SparseArray<SparseIntArray> mUidPidServiceState = new SparseArray<>();
    private SparseBooleanArray mEventFgUidArray = new SparseBooleanArray(2);
    private int mEventPreUid = -1;
    private SparseBooleanArray mUidFg = new SparseBooleanArray();
    private SparseBooleanArray mUidServiceFg = new SparseBooleanArray();
    private SparseBooleanArray mUidFgPreTask = new SparseBooleanArray();
    private int mCurForegroundState = 2;
    private int mCurForegroundServiceState = 4;
    private List<PowerKeeperInterface.f> mForegroundActivitiesChangedCallbacks = new ArrayList();
    private List<PowerKeeperInterface.h> mForegroundServiceChangedCallbacks = new ArrayList();
    private List<PowerKeeperInterface.e0> mProcessDiedCallbacks = new ArrayList();
    private List<PowerKeeperInterface.g> mForegroundPreTaskChangedCallbacks = Lists.newArrayList();
    private ForegroundInfo mAppInfo = null;
    private ForegroundListener mForegroundListener = null;
    private boolean mUseSmallWindowChecker = false;
    private List<Integer> mSmallWindowUids = Lists.newArrayList();
    IFreeformCallback mFreeformCallBack = new IFreeformCallback.Stub() { // from class: com.miui.powerkeeper.utils.ProcessObserver.2
        public void dispatchFreeFormStackModeChanged(int i2, MiuiFreeFormManager.MiuiFreeFormStackInfo miuiFreeFormStackInfo) {
            if (i2 == 0 || i2 == 1 || i2 == 3 || i2 == 5) {
                ProcessObserver.this.updateSmallWindowState();
            }
        }
    };
    private IProcessObserver mProcessObserver = new IMiuiProcessObserver() { // from class: com.miui.powerkeeper.utils.ProcessObserver.4
        public void onForegroundActivitiesChanged(final int i2, final int i3, final boolean z2) {
            if (ProcessObserver.this.mUseSmallWindowChecker) {
                return;
            }
            ProcessObserver.this.mHandler.post(new Runnable() { // from class: com.miui.powerkeeper.utils.ProcessObserver.4.1
                @Override // java.lang.Runnable
                public void run() {
                    if (ProcessObserver.DEBUG) {
                        Log.d(ProcessObserver.TAG, "onForegroundActivitiesChanged, pid: " + i2 + " uid: " + i3 + " foregroundActivities: " + z2);
                    }
                    synchronized (ProcessObserver.this.mRulesLock) {
                        try {
                            SparseIntArray sparseIntArray = (SparseIntArray) ProcessObserver.this.mUidPidState.get(i3);
                            int i4 = 2;
                            if (sparseIntArray == null) {
                                sparseIntArray = new SparseIntArray(2);
                                ProcessObserver.this.mUidPidState.put(i3, sparseIntArray);
                            }
                            int i5 = i2;
                            if (!z2) {
                                i4 = 19;
                            }
                            sparseIntArray.put(i5, i4);
                            ProcessObserver.this.computeUidStateLocked(i3);
                            ProcessObserver.this.computeUidForegroundPreTaskLocked(i3);
                        } catch (Throwable th) {
                            throw th;
                        }
                    }
                }
            });
        }

        public void onForegroundServicesChanged(final int i2, final int i3, final int i4) throws RemoteException {
            ProcessObserver.this.mHandler.post(new Runnable() { // from class: com.miui.powerkeeper.utils.ProcessObserver.4.2
                @Override // java.lang.Runnable
                public void run() {
                    RunningProcessInfo runningProcessInfoByPid;
                    if (ProcessObserver.DEBUG) {
                        Log.d(ProcessObserver.TAG, "onForegroundServicesChanged, pid: " + i2 + " uid: " + i3 + " serviceTypes: " + i4);
                    }
                    synchronized (ProcessObserver.this.mRulesLock) {
                        SparseIntArray sparseIntArray = (SparseIntArray) ProcessObserver.this.mUidPidServiceState.get(i3);
                        if (sparseIntArray == null) {
                            sparseIntArray = new SparseIntArray(2);
                            ProcessObserver.this.mUidPidServiceState.put(i3, sparseIntArray);
                        }
                        try {
                            runningProcessInfoByPid = ProcessManager.getRunningProcessInfoByPid(i2);
                        } catch (RemoteException unused) {
                            runningProcessInfoByPid = null;
                        }
                        sparseIntArray.put(i2, (runningProcessInfoByPid != null && runningProcessInfoByPid.mHasForegroundServices && ForegroundServiceManager.b(null).c(i3, i2)) ? 4 : 19);
                        ProcessObserver.this.computeUidServiceStateLocked(i3);
                    }
                }
            });
        }

        public void onProcessDied(final int i2, final int i3) {
            ProcessObserver.this.mHandler.post(new Runnable() { // from class: com.miui.powerkeeper.utils.ProcessObserver.4.3
                @Override // java.lang.Runnable
                public void run() {
                    synchronized (ProcessObserver.this.mRulesLock) {
                        try {
                            ProcessObserver.this.notifyProcessDiedListeners(i3, i2);
                            SparseIntArray sparseIntArray = (SparseIntArray) ProcessObserver.this.mUidPidState.get(i3);
                            if (sparseIntArray != null) {
                                sparseIntArray.delete(i2);
                                if (sparseIntArray.size() <= 0) {
                                    ProcessObserver.this.mUidPidState.remove(i3);
                                }
                                ProcessObserver.this.computeUidStateLocked(i3);
                            }
                            SparseIntArray sparseIntArray2 = (SparseIntArray) ProcessObserver.this.mUidPidServiceState.get(i3);
                            if (sparseIntArray2 != null) {
                                sparseIntArray2.delete(i2);
                                if (sparseIntArray2.size() <= 0) {
                                    ProcessObserver.this.mUidPidServiceState.remove(i3);
                                }
                                ProcessObserver.this.computeUidServiceStateLocked(i3);
                            }
                        } catch (Throwable th) {
                            throw th;
                        }
                    }
                }
            });
        }

        public void onImportanceChanged(int i2, int i3, int i4) {
        }

        public void onProcessStateChanged(int i2, int i3, int i4) {
        }

        public void onProcessStarted(int i2, int i3, int i4, String str, String str2) {
        }
    };
    private Runnable mForegroundInfoChangedTask = new Runnable() { // from class: com.miui.powerkeeper.utils.ProcessObserver.5
        @Override // java.lang.Runnable
        public void run() {
            if (ProcessObserver.this.mAppInfo == null) {
                Log.e(ProcessObserver.TAG, "ForegroundInfoChangedTask error for AppInfo is null");
                return;
            }
            if (ProcessObserver.DEBUG) {
                if (ProcessObserver.this.mAppInfo.mForegroundPackageName != null) {
                    Log.d(ProcessObserver.TAG, "onScreenAppChanged, FgUid: " + ProcessObserver.this.mAppInfo.mForegroundUid + ", FgPackageName: " + ProcessObserver.this.mAppInfo.mForegroundPackageName);
                }
                if (ProcessObserver.this.mAppInfo.mMultiWindowForegroundPackageName != null) {
                    Log.d(ProcessObserver.TAG, "onScreenAppChanged, TopMultiUid: " + ProcessObserver.this.mAppInfo.mMultiWindowForegroundUid + ", TopMultiPackageName: " + ProcessObserver.this.mAppInfo.mMultiWindowForegroundPackageName);
                }
                if (ProcessObserver.this.mAppInfo.mLastForegroundPackageName != null) {
                    Log.d(ProcessObserver.TAG, "onScreenAppChanged, PreTaskUid: " + ProcessObserver.this.mAppInfo.mLastForegroundUid + ", PreTaskPackageName: " + ProcessObserver.this.mAppInfo.mLastForegroundPackageName);
                }
            }
            SparseBooleanArray sparseBooleanArray = new SparseBooleanArray(4);
            SparseBooleanArray sparseBooleanArray2 = new SparseBooleanArray(2);
            int i2 = ProcessObserver.this.mAppInfo.mMultiWindowForegroundUid;
            if (i2 > 0) {
                if (ProcessObserver.this.mEventFgUidArray.indexOfKey(i2) < 0) {
                    sparseBooleanArray.put(i2, true);
                }
                sparseBooleanArray2.put(i2, true);
            }
            int i3 = ProcessObserver.this.mAppInfo.mForegroundUid;
            if (i3 > 0) {
                if (ProcessObserver.this.mEventFgUidArray.indexOfKey(i3) < 0) {
                    sparseBooleanArray.put(i3, true);
                }
                sparseBooleanArray2.put(i3, true);
            }
            int size = ProcessObserver.this.mSmallWindowUids.size();
            for (int i4 = 0; i4 < size; i4++) {
                int iIntValue = ((Integer) ProcessObserver.this.mSmallWindowUids.get(i4)).intValue();
                if (sparseBooleanArray2.indexOfKey(iIntValue) < 0) {
                    sparseBooleanArray.put(iIntValue, true);
                }
                sparseBooleanArray2.put(iIntValue, true);
            }
            int size2 = ProcessObserver.this.mEventFgUidArray.size();
            for (int i5 = 0; i5 < size2; i5++) {
                int iKeyAt = ProcessObserver.this.mEventFgUidArray.keyAt(i5);
                if (sparseBooleanArray2.indexOfKey(iKeyAt) < 0) {
                    sparseBooleanArray.put(iKeyAt, false);
                }
            }
            ProcessObserver.this.mEventFgUidArray = sparseBooleanArray2;
            int size3 = sparseBooleanArray.size();
            for (int i6 = 0; i6 < size3; i6++) {
                ProcessObserver.this.computeUidForegroundLocked(sparseBooleanArray.keyAt(i6));
            }
            SparseBooleanArray sparseBooleanArray3 = new SparseBooleanArray();
            int i7 = ProcessObserver.this.mAppInfo.mLastForegroundUid;
            if (ProcessObserver.this.mEventPreUid != i7) {
                if (i7 > 0) {
                    sparseBooleanArray3.put(i7, true);
                }
                if (ProcessObserver.this.mEventPreUid > 0) {
                    sparseBooleanArray3.put(ProcessObserver.this.mEventPreUid, false);
                }
            }
            ProcessObserver processObserver = ProcessObserver.this;
            processObserver.mEventPreUid = processObserver.mAppInfo.mLastForegroundUid;
            int size4 = sparseBooleanArray3.size();
            for (int i8 = 0; i8 < size4; i8++) {
                ProcessObserver.this.computeUidForegroundPreTaskLocked(sparseBooleanArray3.keyAt(i8));
            }
        }
    };
    private IForegroundInfoListener.Stub mAppObserver = new IForegroundInfoListener.Stub() { // from class: com.miui.powerkeeper.utils.ProcessObserver.6
        public void onForegroundInfoChanged(ForegroundInfo foregroundInfo) throws RemoteException {
            ProcessObserver.this.notifyForegroundListener(foregroundInfo);
            if (ProcessObserver.this.mHandler.hasCallbacks(ProcessObserver.this.mForegroundInfoChangedTask)) {
                return;
            }
            ProcessObserver.this.mHandler.post(ProcessObserver.this.mForegroundInfoChangedTask);
        }
    };
    private LocalLog mLocalLog = new LocalLog(LOCAL_LOG_SIZE);

    public interface ForegroundListener {
        void onForegroundChanged();
    }

    public ProcessObserver(Context context, EventsAggregator eventsAggregator) {
        this.mContext = context;
        this.mEventsAggregator = eventsAggregator;
        HandlerThread handlerThread = new HandlerThread(TAG);
        this.mThread = handlerThread;
        handlerThread.start();
        this.mHandler = new Handler(this.mThread.getLooper());
        this.mEventsAggregator.setObserver(this);
        this.mHandler.post(new Runnable() { // from class: com.miui.powerkeeper.utils.ProcessObserver.1
            @Override // java.lang.Runnable
            public void run() {
                synchronized (ProcessObserver.this.mRulesLock) {
                    try {
                        ActivityManager.getService().registerProcessObserver(ProcessObserver.this.mProcessObserver);
                        MiuiFreeFormManager.registerFreeformCallback(ProcessObserver.this.mFreeformCallBack);
                        ProcessObserver.this.mUseSmallWindowChecker = true;
                    } catch (Exception unused) {
                        Log.d(ProcessObserver.TAG, "registerFreeformCallback failed");
                    }
                    ProcessManager.registerForegroundInfoListener(ProcessObserver.this.mAppObserver);
                    ForegroundInfo foregroundInfo = ProcessManager.getForegroundInfo();
                    ProcessObserver.this.mAppInfo = foregroundInfo;
                    if (foregroundInfo != null) {
                        if (foregroundInfo.mForegroundUid > 0) {
                            ProcessObserver.this.mEventFgUidArray.put(foregroundInfo.mForegroundUid, true);
                            ProcessObserver.this.initForegroundUidStateLocked(foregroundInfo.mForegroundUid);
                        }
                        if (foregroundInfo.mMultiWindowForegroundUid > 0) {
                            ProcessObserver.this.mEventFgUidArray.put(foregroundInfo.mMultiWindowForegroundUid, true);
                            ProcessObserver.this.initForegroundUidStateLocked(foregroundInfo.mMultiWindowForegroundUid);
                        }
                        int i2 = foregroundInfo.mLastForegroundUid;
                        if (i2 > 0) {
                            ProcessObserver.this.mEventPreUid = i2;
                        }
                        ProcessObserver.this.initUidServiceFgLocked();
                    }
                    ProcessObserver.this.updateSmallWindowState();
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void computeUidForegroundLocked(int i2) {
        boolean z2 = this.mUidState.get(i2, 19) <= this.mCurForegroundState || this.mEventFgUidArray.get(i2, false);
        if (this.mUidFg.get(i2, false) != z2) {
            if (z2) {
                this.mUidFg.put(i2, z2);
                logd("Uid: " + i2 + " Foreground");
            } else {
                this.mUidFg.delete(i2);
                logd("Uid: " + i2 + " Background");
            }
            notifyForegroundActivitiesChangedListeners(i2, z2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void computeUidForegroundPreTaskLocked(int i2) {
        boolean z2 = true;
        boolean z3 = this.mEventPreUid == i2;
        boolean z4 = this.mUidFg.get(i2, false);
        if (!z3 && !z4) {
            z2 = false;
        }
        if (this.mUidFgPreTask.get(i2, false) != z2) {
            if (z2) {
                this.mUidFgPreTask.put(i2, z2);
                logd("Uid: " + i2 + " ForePreTask");
            } else {
                this.mUidFgPreTask.delete(i2);
                logd("Uid: " + i2 + " BackPreTask");
            }
            notifyForegroundPreTaskChangedListeners(i2, z2);
        }
    }

    private void computeUidForegroundServiceLocked(int i2) {
        boolean z2 = this.mUidServiceState.get(i2, 19) <= this.mCurForegroundServiceState;
        if (this.mUidServiceFg.get(i2, false) != z2) {
            if (z2) {
                this.mUidServiceFg.put(i2, true);
                if (DEBUG) {
                    logd("Uid: " + i2 + " foreground service added ");
                }
            } else {
                this.mUidServiceFg.delete(i2);
                if (DEBUG) {
                    logd("Uid: " + i2 + " foreground service removed ");
                }
            }
            notifyForegroundServiceChangedListeners(i2, z2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void computeUidServiceStateLocked(int i2) {
        int i3;
        SparseIntArray sparseIntArray = this.mUidPidServiceState.get(i2);
        if (sparseIntArray != null) {
            int size = sparseIntArray.size();
            i3 = 19;
            for (int i4 = 0; i4 < size; i4++) {
                int iValueAt = sparseIntArray.valueAt(i4);
                if (iValueAt < i3) {
                    i3 = iValueAt;
                }
            }
        } else {
            i3 = 19;
        }
        if (this.mUidServiceState.get(i2, 19) != i3) {
            this.mUidServiceState.put(i2, i3);
        }
        computeUidForegroundServiceLocked(i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void computeUidStateLocked(int i2) {
        int i3;
        SparseIntArray sparseIntArray = this.mUidPidState.get(i2);
        if (sparseIntArray != null) {
            int size = sparseIntArray.size();
            i3 = 19;
            for (int i4 = 0; i4 < size; i4++) {
                int iValueAt = sparseIntArray.valueAt(i4);
                if (iValueAt < i3) {
                    i3 = iValueAt;
                }
            }
        } else {
            i3 = 19;
        }
        if (this.mUidState.get(i2, 19) != i3) {
            this.mUidState.put(i2, i3);
        }
        computeUidForegroundLocked(i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initForegroundUidStateLocked(int i2) {
        if (this.mUidPidState.get(i2) == null) {
            this.mUidPidState.put(i2, new SparseIntArray(2));
        }
        computeUidStateLocked(i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initUidServiceFgLocked() {
        RunningProcessInfo runningProcessInfoByPid;
        this.mUidServiceFg.clear();
        ActivityManager activityManager = (ActivityManager) this.mContext.getSystemService("activity");
        List<ActivityManager.RunningAppProcessInfo> arrayList = new ArrayList<>();
        if (activityManager != null) {
            arrayList = activityManager.getRunningAppProcesses();
        }
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : arrayList) {
            if (runningAppProcessInfo.uid >= 10000 && runningAppProcessInfo.importance <= 125) {
                try {
                    runningProcessInfoByPid = ProcessManager.getRunningProcessInfoByPid(runningAppProcessInfo.pid);
                } catch (RemoteException unused) {
                    runningProcessInfoByPid = null;
                }
                if (runningProcessInfoByPid != null && runningProcessInfoByPid.mHasForegroundServices && ForegroundServiceManager.b(null).c(runningProcessInfoByPid.mUid, runningProcessInfoByPid.mPid)) {
                    SparseIntArray sparseIntArray = this.mUidPidServiceState.get(runningProcessInfoByPid.mUid);
                    if (sparseIntArray == null) {
                        sparseIntArray = new SparseIntArray(2);
                        this.mUidPidServiceState.put(runningProcessInfoByPid.mUid, sparseIntArray);
                    }
                    sparseIntArray.put(runningProcessInfoByPid.mPid, 4);
                    computeUidServiceStateLocked(runningProcessInfoByPid.mUid);
                }
            }
        }
    }

    private boolean isUidForegroundLocked(int i2) {
        return this.mUidFg.get(i2, false);
    }

    private void logd(String str) {
        if (DEBUG) {
            Log.d(TAG, str);
        }
        this.mLocalLog.log(str);
    }

    private void loge(String str) {
        Log.e(TAG, str);
        this.mLocalLog.log(str);
    }

    private void notifyForegroundActivitiesChangedListeners(final int i2, final boolean z2) {
        this.mEventsAggregator.notifyForegroundActivityChangeListeners(i2, z2);
        synchronized (this.mForegroundActivitiesChangedCallbacks) {
            try {
                for (final PowerKeeperInterface.f fVar : this.mForegroundActivitiesChangedCallbacks) {
                    Handler handler = fVar.f260a;
                    if (handler == null) {
                        handler = BackgroundThread.getHandler();
                    }
                    handler.post(new Runnable() { // from class: com.miui.powerkeeper.utils.ProcessObserver.7
                        @Override // java.lang.Runnable
                        public void run() {
                            PowerKeeperInterface.r rVar = fVar.f261b;
                            if (rVar != null) {
                                rVar.onForegroundActivitiesChanged(i2, z2);
                            }
                        }
                    });
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyForegroundListener(ForegroundInfo foregroundInfo) {
        this.mAppInfo = foregroundInfo;
        ForegroundListener foregroundListener = this.mForegroundListener;
        if (foregroundListener != null) {
            foregroundListener.onForegroundChanged();
        }
        if (foregroundInfo == null || foregroundInfo.mForegroundPackageName == null) {
            return;
        }
        this.mEventsAggregator.notifyForForegroundInfo(foregroundInfo);
    }

    private void notifyForegroundPreTaskChangedListeners(final int i2, final boolean z2) {
        this.mEventsAggregator.notifyForegroundPreTaskChangeListeners(i2, z2);
        synchronized (this.mForegroundPreTaskChangedCallbacks) {
            try {
                for (final PowerKeeperInterface.g gVar : this.mForegroundPreTaskChangedCallbacks) {
                    Handler handler = gVar.f264a;
                    if (handler == null) {
                        handler = BackgroundThread.getHandler();
                    }
                    handler.post(new Runnable() { // from class: com.miui.powerkeeper.utils.ProcessObserver.8
                        @Override // java.lang.Runnable
                        public void run() {
                            PowerKeeperInterface.s sVar = gVar.f265b;
                            if (sVar != null) {
                                sVar.onForegroundPreTaskChanged(i2, z2);
                            }
                        }
                    });
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private void notifyForegroundServiceChangedListeners(final int i2, final boolean z2) {
        this.mEventsAggregator.notifyForegroundServiceChangeListeners(i2, z2);
        synchronized (this.mForegroundServiceChangedCallbacks) {
            try {
                for (final PowerKeeperInterface.h hVar : this.mForegroundServiceChangedCallbacks) {
                    Handler handler = hVar.f268a;
                    if (handler == null) {
                        handler = BackgroundThread.getHandler();
                    }
                    handler.post(new Runnable() { // from class: com.miui.powerkeeper.utils.ProcessObserver.9
                        @Override // java.lang.Runnable
                        public void run() {
                            PowerKeeperInterface.t tVar = hVar.f269b;
                            if (tVar != null) {
                                tVar.onForegroundServiceChanged(i2, z2);
                            }
                        }
                    });
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyProcessDiedListeners(final int i2, final int i3) {
        synchronized (this.mProcessDiedCallbacks) {
            try {
                for (final PowerKeeperInterface.e0 e0Var : this.mProcessDiedCallbacks) {
                    Handler handler = e0Var.f258a;
                    if (handler == null) {
                        handler = BackgroundThread.getHandler();
                    }
                    handler.post(new Runnable() { // from class: com.miui.powerkeeper.utils.ProcessObserver.10
                        @Override // java.lang.Runnable
                        public void run() {
                            PowerKeeperInterface.y yVar = e0Var.f259b;
                            if (yVar != null) {
                                yVar.onProcessDiedListener(i2, i3);
                            }
                        }
                    });
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        indentingPrintWriter.println("#######dump Process Observer#######");
        synchronized (this.mRulesLock) {
            try {
                indentingPrintWriter.increaseIndent();
                indentingPrintWriter.println("Foreground Uids:");
                for (int i2 = 0; i2 < this.mUidFg.size(); i2++) {
                    if (this.mUidFg.valueAt(i2)) {
                        indentingPrintWriter.println(this.mUidFg.keyAt(i2));
                    }
                }
                indentingPrintWriter.println("Foreground & Pretask Uids:");
                for (int i3 = 0; i3 < this.mUidFgPreTask.size(); i3++) {
                    if (this.mUidFgPreTask.valueAt(i3)) {
                        indentingPrintWriter.println(this.mUidFgPreTask.keyAt(i3));
                    }
                }
                indentingPrintWriter.println("Foreground & mUidServiceFg Uids:");
                for (int i4 = 0; i4 < this.mUidServiceFg.size(); i4++) {
                    if (this.mUidServiceFg.valueAt(i4)) {
                        indentingPrintWriter.println(this.mUidServiceFg.keyAt(i4));
                    }
                }
                indentingPrintWriter.decreaseIndent();
                indentingPrintWriter.increaseIndent();
                indentingPrintWriter.println();
                indentingPrintWriter.println("ProcessObserver Local Log:");
                this.mLocalLog.dump(fileDescriptor, indentingPrintWriter, strArr);
                indentingPrintWriter.decreaseIndent();
            } catch (Throwable th) {
                throw th;
            }
        }
        indentingPrintWriter.println("end#######dump Process Observer#######end");
    }

    @Nullable
    public ForegroundInfo getAppInfo() {
        return this.mAppInfo;
    }

    public int getUidState(int i2) {
        return this.mUidState.get(i2, 19);
    }

    public boolean isUidForeground(int i2) {
        boolean zIsUidForegroundLocked;
        synchronized (this.mRulesLock) {
            zIsUidForegroundLocked = isUidForegroundLocked(i2);
        }
        return zIsUidForegroundLocked;
    }

    public boolean isUidForegroundPreTask(int i2) {
        boolean z2;
        synchronized (this.mRulesLock) {
            try {
                z2 = this.mUidFg.get(i2, false) || this.mEventPreUid == i2;
            } catch (Throwable th) {
                throw th;
            }
        }
        return z2;
    }

    public boolean isUidForegroundService(int i2) {
        boolean z2;
        synchronized (this.mRulesLock) {
            z2 = this.mUidServiceFg.get(i2, false);
        }
        return z2;
    }

    public void registerForegroundActivitiesChangedListener(PowerKeeperInterface.f fVar) {
        synchronized (this.mForegroundActivitiesChangedCallbacks) {
            try {
                if (!this.mForegroundActivitiesChangedCallbacks.contains(fVar)) {
                    this.mForegroundActivitiesChangedCallbacks.add(fVar);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void registerForegroundPreTaskChangedListener(PowerKeeperInterface.g gVar) {
        synchronized (this.mForegroundPreTaskChangedCallbacks) {
            try {
                if (!this.mForegroundPreTaskChangedCallbacks.contains(gVar)) {
                    this.mForegroundPreTaskChangedCallbacks.add(gVar);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void registerIForegroundServiceChangedRegister(PowerKeeperInterface.h hVar) {
        synchronized (this.mForegroundServiceChangedCallbacks) {
            try {
                if (!this.mForegroundServiceChangedCallbacks.contains(hVar)) {
                    this.mForegroundServiceChangedCallbacks.add(hVar);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void registerProcessDiedListener(PowerKeeperInterface.e0 e0Var) {
        synchronized (this.mProcessDiedCallbacks) {
            try {
                if (!this.mProcessDiedCallbacks.contains(e0Var)) {
                    this.mProcessDiedCallbacks.add(e0Var);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void setForegroundListener(ForegroundListener foregroundListener) {
        this.mForegroundListener = foregroundListener;
    }

    public void unregisterForegroundActivitiesChangedListener(PowerKeeperInterface.f fVar) {
        synchronized (this.mForegroundActivitiesChangedCallbacks) {
            try {
                if (this.mForegroundActivitiesChangedCallbacks.contains(fVar)) {
                    this.mForegroundActivitiesChangedCallbacks.remove(fVar);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void unregisterForegroundPreTaskChangedListener(PowerKeeperInterface.g gVar) {
        synchronized (this.mForegroundPreTaskChangedCallbacks) {
            try {
                if (this.mForegroundPreTaskChangedCallbacks.contains(gVar)) {
                    this.mForegroundPreTaskChangedCallbacks.remove(gVar);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void unregisterIForegroundServiceChangedRegister(PowerKeeperInterface.h hVar) {
        synchronized (this.mForegroundServiceChangedCallbacks) {
            try {
                if (this.mForegroundServiceChangedCallbacks.contains(hVar)) {
                    this.mForegroundServiceChangedCallbacks.remove(hVar);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void unregisterProcessDiedListener(PowerKeeperInterface.e0 e0Var) {
        synchronized (this.mProcessDiedCallbacks) {
            try {
                if (this.mProcessDiedCallbacks.contains(e0Var)) {
                    this.mProcessDiedCallbacks.remove(e0Var);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void updateSmallWindowState() {
        if (this.mUseSmallWindowChecker) {
            this.mHandler.post(new Runnable() { // from class: com.miui.powerkeeper.utils.ProcessObserver.3
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        ProcessObserver.this.mSmallWindowUids.clear();
                        List<MiuiFreeFormManager.MiuiFreeFormStackInfo> allFreeFormStackInfosOnDisplay = MiuiFreeFormManager.getAllFreeFormStackInfosOnDisplay(ProcessObserver.this.mContext.getDisplayId());
                        if (allFreeFormStackInfosOnDisplay == null) {
                            return;
                        }
                        for (MiuiFreeFormManager.MiuiFreeFormStackInfo miuiFreeFormStackInfo : allFreeFormStackInfosOnDisplay) {
                            if (miuiFreeFormStackInfo.isInMiniFreeFormMode() || miuiFreeFormStackInfo.isInFreeFormMode()) {
                                Integer num = PowerKeeperManager.getInstance().getPackageUidsMap(miuiFreeFormStackInfo.userId).get(miuiFreeFormStackInfo.packageName);
                                if (num.intValue() > 0) {
                                    ProcessObserver.this.mSmallWindowUids.add(num);
                                }
                            }
                        }
                        if (!ProcessObserver.this.mHandler.hasCallbacks(ProcessObserver.this.mForegroundInfoChangedTask)) {
                            ProcessObserver.this.mHandler.post(ProcessObserver.this.mForegroundInfoChangedTask);
                        }
                        if (ProcessObserver.DEBUG) {
                            Log.d(ProcessObserver.TAG, "updateSmallWindowState, mSmallWindowUids=" + ProcessObserver.this.mSmallWindowUids.toString());
                        }
                    } catch (Exception e2) {
                        Log.e(ProcessObserver.TAG, "updateSmallWindowState error", e2);
                    }
                }
            });
        }
    }
}
