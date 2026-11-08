package com.miui.powerkeeper.controller;

import android.annotation.Nullable;
import android.app.ActivityManager;
import android.app.IUidObserver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.LocalLog;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.android.internal.util.IndentingPrintWriter;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.provider.SimpleSettingKeys;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.utils.ClusterUtils;
import com.miui.powerkeeper.utils.PackageUtil;
import com.miui.whetstone.IPowerKeeperPolicy;
import com.miui.whetstone.server.IWhetstoneActivityManager;
import com.xiaomi.channelcollection.RegisterAppChannelInfoCollectionJob;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class AppClusterController extends Controller {
    public static final int CLUSTER_BLACKLIST = 2;
    public static final int CLUSTER_DEFAULT = 0;
    public static final int CLUSTER_DISABLE = -1;
    public static final int CLUSTER_WHITELIST = 1;
    public static final String KEY_POLICY = "POLICY";
    private static final int MSG_CHECK_RUNNING_PROCESS = 2;
    private static final int MSG_UID_ACTIVE = 1;
    private static final int MSG_UID_GONE = 0;
    public static final int POLICY_CONTROL_DISABLE_ALWAYS = 1;
    public static final int POLICY_CONTROL_ENABLE_ALWAYS = 0;
    public static final int POLICY_CONTROL_FOREGROUND_DISABLE = 3;
    public static final int POLICY_CONTROL_FOREGROUND_ENABLE = 2;
    public static final int POLICY_RESTRICT_BTASK = 3;
    public static final int POLICY_RESTRICT_LTASK = 1;
    public static final int POLICY_RESTRICT_MTASK = 2;
    public static final int POLICY_RESTRICT_NTASK = 0;
    private static final List<String> SYSTEM_WHITE_LIST = new ArrayList<String>() { // from class: com.miui.powerkeeper.controller.AppClusterController.1
        {
            add("com.miui.packageinstaller");
        }
    };
    private static final String TAG = "PowerKeeper.AppCluster";
    public static final int TYPE_PID_CONTROL = 240;
    public static final int TYPE_UID_CONTROL = 15;
    private final int APP_STATE_BCLUS;
    private final int APP_STATE_FREEZE;
    private final int APP_STATE_LCLUS;
    private final int APP_STATE_MCLUS;
    private final int APP_STATE_NCLUS;
    private final int CLUSTER_STATE;
    private final int FREEZE_STATE;
    private final int RESTRICT_CHANGED;
    private final int RESTRICT_PACKAGE_CHANGED;
    private boolean mAppRestrictEnabled;
    private ContentObserver mContentObserver;
    private final List<Integer> mControlledTids;
    private int mCurrentProcessTask;
    private final LocalLog mHistoryLog;
    private boolean mProcessRestrictEnable;
    private final HashSet<Integer> mSystemWhiteList;
    private MyUidObserver mUidObserver;
    private final SparseArray<UidProcessState> mUidProcessState;
    private final SparseIntArray mUidProcessTasks;
    private final SparseIntArray mUidState;

    private class MyUidObserver extends IUidObserver.Stub {
        public void onUidActive(final int i2) throws RemoteException {
            if (AppClusterController.this.isControlApp(i2)) {
                AppClusterController.this.mHandler.post(new Runnable() { // from class: com.miui.powerkeeper.controller.AppClusterController.MyUidObserver.2
                    @Override // java.lang.Runnable
                    public void run() {
                        if (AppClusterController.this.mUidProcessState.indexOfKey(i2) < 0) {
                            UidProcessState uidProcessState = AppClusterController.this.getUidProcessState(i2);
                            Message messageObtainMessage = AppClusterController.this.mHandler.obtainMessage(1);
                            messageObtainMessage.obj = uidProcessState;
                            messageObtainMessage.arg1 = i2;
                            AppClusterController.this.mHandler.sendMessageDelayed(messageObtainMessage, 60000L);
                        }
                    }
                });
            }
        }

        public void onUidGone(final int i2, boolean z2) throws RemoteException {
            if (AppClusterController.this.isControlApp(i2)) {
                AppClusterController.this.mHandler.post(new Runnable() { // from class: com.miui.powerkeeper.controller.AppClusterController.MyUidObserver.1
                    @Override // java.lang.Runnable
                    public void run() {
                        UidProcessState uidProcessState = (UidProcessState) AppClusterController.this.mUidProcessState.get(i2);
                        if (uidProcessState != null) {
                            if (AppClusterController.this.mHandler.hasMessages(1, uidProcessState)) {
                                AppClusterController.this.mHandler.removeMessages(1, uidProcessState);
                            }
                            AppClusterController.this.mUidProcessState.delete(i2);
                            Message messageObtainMessage = AppClusterController.this.mHandler.obtainMessage(0);
                            messageObtainMessage.arg1 = i2;
                            AppClusterController.this.mHandler.sendMessage(messageObtainMessage);
                        }
                    }
                });
            }
        }

        public void onUidStateChanged(int i2, int i3, long j2) {
        }

        private MyUidObserver() {
        }

        public void onUidStateChanged(int i2, int i3, long j2, int i4) throws RemoteException {
        }

        public void onUidCachedChanged(int i2, boolean z2) throws RemoteException {
        }

        public void onUidIdle(int i2, boolean z2) throws RemoteException {
        }

        public void onUidProcAdjChanged(int i2, int i3) {
        }
    }

    public AppClusterController(Context context, PowerKeeperManager powerKeeperManager) {
        super(context, powerKeeperManager, TAG);
        this.APP_STATE_NCLUS = 0;
        this.APP_STATE_LCLUS = 1;
        this.APP_STATE_MCLUS = 2;
        this.APP_STATE_BCLUS = 4;
        this.APP_STATE_FREEZE = 8;
        this.CLUSTER_STATE = 7;
        this.FREEZE_STATE = 8;
        this.mUidState = new SparseIntArray();
        this.mUidProcessState = new SparseArray<>();
        this.mSystemWhiteList = new HashSet<>();
        this.mControlledTids = new ArrayList();
        this.mUidProcessTasks = new SparseIntArray(4);
        this.mHistoryLog = new LocalLog(Controller.MAX_HISTORY_ITEMS);
        this.RESTRICT_CHANGED = 1;
        this.RESTRICT_PACKAGE_CHANGED = 2;
    }

    private ClusterUtils.Cluster getAppCluster(int i2) {
        if (i2 == 1) {
            return ClusterUtils.Cluster.LCLUS;
        }
        if (i2 == 2) {
            return ClusterUtils.Cluster.MCLUS;
        }
        if (i2 == 4) {
            return ClusterUtils.Cluster.BCLUS;
        }
        return null;
    }

    private ClusterUtils.Cluster getProcessCluster(int i2) {
        if (i2 == 1) {
            return ClusterUtils.Cluster.LTASK;
        }
        if (i2 == 2) {
            return ClusterUtils.Cluster.MTASK;
        }
        if (i2 == 3) {
            return ClusterUtils.Cluster.BTASK;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public UidProcessState getUidProcessState(int i2) {
        UidProcessState uidProcessState = this.mUidProcessState.get(i2);
        if (uidProcessState != null) {
            return uidProcessState;
        }
        UidProcessState uidProcessState2 = new UidProcessState(i2);
        this.mUidProcessState.put(i2, uidProcessState2);
        return uidProcessState2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void parseProcessAppFromDb(int i2, String str) throws Throwable {
        int i3;
        Integer num;
        String strOptString;
        String string = SimpleSettings.Misc.getString(this.mContext, SimpleSettingKeys.KEY_PROCESS_CLUSTER_GROUP);
        if (string == null || string.equals("")) {
            return;
        }
        try {
            JSONObject jSONObject = new JSONObject(string);
            boolean zOptBoolean = jSONObject.optBoolean("fucSwitch", true);
            this.mAppRestrictEnabled = jSONObject.optBoolean("appRestrict", false);
            if (this.mProcessRestrictEnable != zOptBoolean) {
                this.mProcessRestrictEnable = zOptBoolean;
                if (!zOptBoolean) {
                    for (int size = this.mUidState.size() - 1; size > 0; size--) {
                        if ((this.mUidState.valueAt(size) & TYPE_PID_CONTROL) != 0) {
                            synchronized (this.mLock) {
                                updateUidPolicyLocked(this.mUidState.keyAt(size), 0, TYPE_PID_CONTROL);
                            }
                        }
                    }
                    this.mUidProcessTasks.clear();
                    updateForProcessRestrictionChanged();
                    this.mControlledTids.clear();
                    return;
                }
                Log.e(TAG, "parseProcessAppFromDb: ", e);
            }
            this.mUidProcessTasks.put(-1, jSONObject.optInt("defaultTask", 3));
            if ((i2 & 1) != 0) {
                this.mControlledTids.clear();
                if (this.mProcessRestrictEnable && (strOptString = jSONObject.optString("process", "")) != null && !strOptString.equals("")) {
                    String[] strArrSplit = strOptString.trim().split(",");
                    Method declaredMethod = IPowerKeeperPolicy.class.getDeclaredMethod("isProcessesRunning", List.class);
                    if (declaredMethod == null) {
                        Log.d(TAG, "isProcessesRunning return for no method");
                        return;
                    }
                    IWhetstoneActivityManager iWhetstoneActivityManagerAsInterface = IWhetstoneActivityManager.Stub.asInterface(ServiceManager.getService("whetstone.activity"));
                    if (iWhetstoneActivityManagerAsInterface == null) {
                        Log.d(TAG, "ws is null");
                        return;
                    } else {
                        Map map = (Map) declaredMethod.invoke(iWhetstoneActivityManagerAsInterface.getPowerKeeperPolicy(), Arrays.asList(strArrSplit));
                        if (map != null) {
                            this.mControlledTids.addAll(map.keySet());
                        }
                    }
                }
            }
            if ((i2 & 2) != 0) {
                Map<String, Integer> packageUidsMap = this.mPowerKeeperManager.getPackageUidsMap(0);
                for (String str2 : jSONObject.keySet()) {
                    if (str2 != null && str2.contains("pkgTask_")) {
                        String strOptString2 = jSONObject.optString(str2);
                        if (this.mProcessRestrictEnable) {
                            int i4 = Integer.parseInt(str2.split(RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT)[1]);
                            if (i4 > 3) {
                                Log.e(TAG, "cloud config error, process task overflow policy=" + i4);
                            } else {
                                i3 = i4 << 4;
                            }
                        } else {
                            i3 = 0;
                        }
                        for (String str3 : strOptString2.split(",")) {
                            if ((str == null || str.equals(str3)) && (num = packageUidsMap.get(str3)) != null && num.intValue() != -1) {
                                synchronized (this.mLock) {
                                    updateUidPolicyLocked(num.intValue(), i3, TYPE_PID_CONTROL);
                                }
                            }
                        }
                    }
                }
            }
            updateForProcessRestrictionChanged();
        } catch (Exception e2) {
            Log.e(TAG, "parseProcessAppFromDb: ", e2);
        }
    }

    private void registerContentObserver() {
        if (this.mContentObserver != null) {
            return;
        }
        this.mContentObserver = new ContentObserver(this.mHandler) { // from class: com.miui.powerkeeper.controller.AppClusterController.3
            @Override // android.database.ContentObserver
            public void onChange(boolean z2, @Nullable Uri uri) throws Throwable {
                if (uri != null && uri.equals(SimpleSettings.Misc.getUriFor(SimpleSettingKeys.KEY_PROCESS_CLUSTER_GROUP))) {
                    AppClusterController.this.parseProcessAppFromDb(-1, null);
                }
            }
        };
        this.mContext.getContentResolver().registerContentObserver(SimpleSettings.Misc.getUriFor(SimpleSettingKeys.KEY_PROCESS_CLUSTER_GROUP), false, this.mContentObserver);
    }

    private void setAppCluster(int i2, int i3) throws Throwable {
        if (this.mAppRestrictEnabled) {
            int i4 = this.mUidState.get(i2);
            int i5 = i4 & 7;
            int i6 = i4 & 8;
            if (i5 != i3) {
                if (i5 > 0 && i6 == 0) {
                    setUidState(i2, getAppCluster(i5), true);
                }
                if (i3 > 0 && i6 == 0) {
                    setUidState(i2, getAppCluster(i3), false);
                }
                int i7 = i3 | i6;
                if (i7 > 0) {
                    this.mUidState.put(i2, i7);
                } else {
                    this.mUidState.delete(i2);
                }
            }
        }
    }

    private void setProcessCluster(List<Integer> list, ClusterUtils.Cluster cluster, boolean z2) throws Throwable {
        if (Controller.DEBUG) {
            Slog.d(TAG, "setPidState, pids = " + list + " cluster = " + cluster + " allow = " + z2);
        }
        try {
            if (z2) {
                ClusterUtils.delProcessFromCluster(list, cluster);
                this.mHistoryLog.log("unrestrict pid=" + list + " cluster = " + cluster);
            } else {
                ClusterUtils.addProcessToCluster(list, cluster);
                this.mHistoryLog.log("restrict pid=" + list + " cluster = " + cluster);
            }
        } catch (Exception e2) {
            Slog.e(TAG, "setPidState", e2);
        }
    }

    private void setUidProcessRestriction(int i2, int i3) throws Throwable {
        int i4;
        if (this.mProcessRestrictEnable && (i4 = i3 >> 4) != this.mUidProcessTasks.get(i2)) {
            if (i4 == 0) {
                this.mUidProcessTasks.delete(i2);
            } else {
                this.mUidProcessTasks.put(i2, i4);
            }
            updateForProcessRestrictionChanged();
        }
    }

    private void setUidState(int i2, ClusterUtils.Cluster cluster, boolean z2) throws Throwable {
        if (Controller.DEBUG) {
            Slog.d(TAG, "setUidState, uid = " + i2 + " cluster = " + cluster + " allow = " + z2);
        }
        try {
            if (z2) {
                ClusterUtils.delAppFromCluster(i2, cluster);
                this.mHistoryLog.log("unrestrict uid=" + i2 + " cluster = " + cluster);
            } else {
                ClusterUtils.addAppToCluster(i2, cluster);
                this.mHistoryLog.log("restrict uid=" + i2 + " cluster = " + cluster);
            }
        } catch (Exception e2) {
            Slog.e(TAG, "setUidState", e2);
        }
    }

    private void unregisterContentObserver() {
        if (this.mContentObserver == null) {
            return;
        }
        this.mContext.getContentResolver().unregisterContentObserver(this.mContentObserver);
        this.mContentObserver = null;
    }

    private void updateForFreezeChanged(Bundle bundle) throws Throwable {
        boolean z2 = 'T' == bundle.getChar("freeze");
        Iterator<Integer> it = bundle.getIntegerArrayList("uids").iterator();
        while (it.hasNext()) {
            int iIntValue = it.next().intValue();
            int i2 = this.mUidState.get(iIntValue);
            int i3 = i2 & 7;
            int i4 = i2 & 8;
            if ((i4 == 0 && z2) || (i4 != 0 && !z2)) {
                if (i3 != 0) {
                    setUidState(iIntValue, getAppCluster(i3), z2);
                }
                int i5 = z2 ? i2 | 8 : i2 & (-9);
                if (i5 > 0) {
                    this.mUidState.put(iIntValue, i5);
                } else {
                    this.mUidState.delete(iIntValue);
                }
            }
        }
    }

    private void updateForProcessRestrictionChanged() throws Throwable {
        int iValueAt = this.mUidProcessTasks.size() > 0 ? this.mUidProcessTasks.valueAt(0) : 0;
        for (int size = this.mUidProcessTasks.size() - 1; size > 0; size--) {
            iValueAt = Math.min(iValueAt, this.mUidProcessTasks.valueAt(size));
        }
        int i2 = this.mCurrentProcessTask;
        if (iValueAt != i2) {
            if (i2 > 0 && iValueAt == 0) {
                setProcessCluster(this.mControlledTids, getProcessCluster(i2), true);
            }
            this.mCurrentProcessTask = iValueAt;
            if (iValueAt > 0) {
                setProcessCluster(this.mControlledTids, getProcessCluster(iValueAt), false);
            }
        }
    }

    private void updateForSystemWhiteListed(boolean z2) throws Throwable {
        if (z2) {
            updateForUidForeground(1000);
        } else {
            updateForUidStateChanged(1000);
        }
    }

    private void updateForUidActive(int i2) throws Throwable {
        updateForUidStateChanged(i2);
    }

    private void updateForUidBackground(int i2) throws Throwable {
        int i3 = this.mUidPoliy.get(i2);
        if ((i3 & 15) != 0) {
            int appId = UserHandle.getAppId(i2);
            if (appId >= 1000 && appId <= 10000) {
                setAppCluster(i2, 2);
            } else if (appId >= 10000 && appId <= 19999) {
                setAppCluster(i2, 1);
            }
        } else {
            setAppCluster(i2, 0);
        }
        if (this.mSystemWhiteList.contains(Integer.valueOf(i2))) {
            updateForSystemWhiteListed(false);
        }
        if ((i3 & TYPE_PID_CONTROL) != 0) {
            setUidProcessRestriction(i2, 0);
        }
    }

    private void updateForUidFgService(int i2) throws Throwable {
        int i3 = this.mUidPoliy.get(i2);
        if ((i3 & 15) != 0) {
            setAppCluster(i2, 2);
        } else {
            setAppCluster(i2, 0);
        }
        if ((i3 & TYPE_PID_CONTROL) != 0) {
            setUidProcessRestriction(i2, 0);
        }
    }

    private void updateForUidForeground(int i2) throws Throwable {
        int i3 = this.mUidPoliy.get(i2);
        int i4 = i3 & 15;
        if (i4 == 1 || i4 == 3) {
            setAppCluster(i2, 2);
        } else {
            setAppCluster(i2, 0);
        }
        if (this.mSystemWhiteList.contains(Integer.valueOf(i2))) {
            updateForSystemWhiteListed(true);
        }
        int i5 = i3 & TYPE_PID_CONTROL;
        if (i5 != 0) {
            setUidProcessRestriction(i2, i5);
        }
    }

    private void updateForUidGone(int i2) throws Throwable {
        setAppCluster(i2, 0);
        this.mUidState.delete(i2);
        this.mUidProcessTasks.delete(i2);
        updateForProcessRestrictionChanged();
    }

    private void updateForUidStateChanged(int i2) throws Throwable {
        UidProcessState uidProcessState = this.mUidProcessState.get(i2);
        if (uidProcessState == null) {
            return;
        }
        if (uidProcessState.foreground) {
            updateForUidForeground(i2);
        } else if (uidProcessState.foregroundService) {
            updateForUidFgService(i2);
        } else {
            updateForUidBackground(i2);
        }
    }

    private void updateForegroundActivityChanged(int i2, boolean z2) throws Throwable {
        if (isControlApp(i2)) {
            UidProcessState uidProcessState = getUidProcessState(i2);
            if (this.mHandler.hasMessages(1, uidProcessState)) {
                this.mHandler.removeMessages(1, uidProcessState);
            }
            if (uidProcessState.foreground != z2) {
                uidProcessState.foreground = z2;
                updateForUidStateChanged(i2);
            }
        }
    }

    private void updateForegroundServiceChanged(int i2, boolean z2) throws Throwable {
        if (isControlApp(i2)) {
            UidProcessState uidProcessState = getUidProcessState(i2);
            if (this.mHandler.hasMessages(1, uidProcessState)) {
                this.mHandler.removeMessages(1, uidProcessState);
            }
            if (uidProcessState.foregroundService != z2) {
                uidProcessState.foregroundService = z2;
                updateForUidStateChanged(i2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateUidPolicyLocked(int i2, Bundle bundle) throws Throwable {
        updateUidPolicyLocked(i2, bundle.getInt("POLICY"), 15);
    }

    public void checkRunningProcess() throws Throwable {
        Iterator<ActivityManager.RunningAppProcessInfo> it = ((ActivityManager) this.mContext.getSystemService("activity")).getRunningAppProcesses().iterator();
        while (it.hasNext()) {
            int i2 = it.next().uid;
            if (isControlApp(i2) && this.mUidProcessState.indexOfKey(i2) < 0) {
                this.mUidProcessState.put(i2, new UidProcessState(i2, this.mPowerKeeperManager.getProcessObserver().isUidForeground(i2), this.mPowerKeeperManager.getProcessObserver().isUidForegroundService(i2)));
                updateForUidStateChanged(i2);
            }
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        synchronized (this.mLock) {
            indentingPrintWriter.println("#######dump##AppClusterController#######");
            indentingPrintWriter.println("AppClusterController operation history:");
            this.mHistoryLog.dump(fileDescriptor, indentingPrintWriter, strArr);
            indentingPrintWriter.println("end#######dump##AppClusterController#######end");
        }
    }

    boolean isControlApp(int i2) {
        int appId;
        return i2 > 0 && i2 <= 19999 && (appId = UserHandle.getAppId(i2)) >= 1000 && appId <= 19999;
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onDisable() throws Throwable {
        for (int i2 = 0; i2 < this.mUidState.size(); i2++) {
            setAppCluster(this.mUidState.keyAt(i2), 0);
        }
        if (this.mUidProcessTasks.size() > 0) {
            this.mUidProcessTasks.clear();
            updateForProcessRestrictionChanged();
        }
        try {
            ActivityManager.getService().unregisterUidObserver(this.mUidObserver);
            this.mUidObserver = null;
        } catch (RemoteException e2) {
            Log.e(TAG, "onEnable: ", e2);
        }
        EventsAggregator eventsAggregator = this.mPowerKeeperManager.getEventsAggregator();
        eventsAggregator.unregisterPackageAddedListener(this.mHandler);
        eventsAggregator.unregisterForFreezeState(this.mHandler);
        eventsAggregator.unregisterForegroundServiceChangeListener(this.mHandler);
        eventsAggregator.unregisterForegroundActivityChangeListener(this.mHandler);
        eventsAggregator.unregisterPowerModeListener(this.mHandler);
        unregisterContentObserver();
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onEnable() throws Throwable {
        EventsAggregator eventsAggregator = this.mPowerKeeperManager.getEventsAggregator();
        eventsAggregator.registerForFreezeState(this.mHandler);
        eventsAggregator.registerForegroundServiceChangeListener(this.mHandler);
        eventsAggregator.registerForegroundActivityChangeListener(this.mHandler);
        eventsAggregator.registerPowerModeListener(this.mHandler);
        this.mUidObserver = new MyUidObserver();
        Iterator<String> it = SYSTEM_WHITE_LIST.iterator();
        while (it.hasNext()) {
            int uidByPackageName = PackageUtil.getUidByPackageName(this.mContext, it.next());
            if (uidByPackageName != -1 && uidByPackageName != 1000) {
                this.mSystemWhiteList.add(Integer.valueOf(uidByPackageName));
            }
        }
        try {
            ActivityManager.getService().registerUidObserver(this.mUidObserver, 10, 6, this.mContext.getOpPackageName());
        } catch (RemoteException e2) {
            Log.e(TAG, "onEnable: ", e2);
        }
        registerContentObserver();
        parseProcessAppFromDb(-1, null);
        this.mHandler.obtainMessage(2).sendToTarget();
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onHandleMessage(Message message) {
        synchronized (this.mLock) {
            try {
                int i2 = message.what;
                if (i2 != -24) {
                    if (i2 == -11) {
                        updateForegroundServiceChanged(message.arg1, message.arg2 == 1);
                    } else if (i2 == -10) {
                        updateForegroundActivityChanged(message.arg1, message.arg2 == 1);
                    } else if (i2 == 0) {
                        updateForUidGone(message.arg1);
                    } else if (i2 == 1) {
                        updateForUidActive(message.arg1);
                    } else if (i2 == 2) {
                        checkRunningProcess();
                    }
                } else {
                    updateForFreezeChanged((Bundle) message.obj);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onPackageAdded(int i2, String str, int i3) throws Throwable {
        if (SYSTEM_WHITE_LIST.contains(str)) {
            this.mSystemWhiteList.add(Integer.valueOf(i3));
        }
        parseProcessAppFromDb(2, str);
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onUidRemoved(int i2, int i3) throws Throwable {
        setAppCluster(i3, 0);
        this.mUidState.delete(i3);
        this.mSystemWhiteList.remove(Integer.valueOf(i3));
        this.mUidProcessTasks.delete(i3);
        updateForProcessRestrictionChanged();
    }

    @Override // com.miui.powerkeeper.controller.Controller
    public void setUidPolicy(int i2, int i3) {
        Bundle bundle = new Bundle();
        bundle.putInt("POLICY", i3);
        setUidPolicy(i2, bundle);
    }

    private static class UidProcessState {
        boolean foreground;
        boolean foregroundService;
        boolean running;
        int uid;

        public UidProcessState(int i2) {
            this.uid = i2;
            this.running = true;
        }

        public UidProcessState(int i2, boolean z2, boolean z3) {
            this(i2);
            this.foreground = z2;
            this.foregroundService = z3;
        }
    }

    private void updateUidPolicyLocked(int i2, int i3, int i4) throws Throwable {
        int i5 = this.mUidPoliy.get(i2);
        int i6 = i3 | ((~i4) & i5);
        if (i6 != i5) {
            if (Controller.DEBUG) {
                Log.d(TAG, "update uid policy uid=" + i2 + " policy=" + i6);
            }
            if (i6 == 0) {
                this.mUidPoliy.delete(i2);
            } else {
                this.mUidPoliy.put(i2, i6);
            }
            updateForUidStateChanged(i2);
        }
    }

    public void setUidPolicy(final int i2, final Bundle bundle) {
        if (!isControlApp(i2)) {
            if (Controller.DEBUG) {
                Log.e(TAG, "cannot apply policy to UID " + i2);
                return;
            }
            return;
        }
        synchronized (this.mLock) {
            try {
                if (this.mIgnoreUids.contains(Integer.valueOf(i2))) {
                    return;
                }
                if (this.isEnabled) {
                    this.mHandler.post(new Runnable() { // from class: com.miui.powerkeeper.controller.AppClusterController.2
                        @Override // java.lang.Runnable
                        public void run() {
                            synchronized (AppClusterController.this.mLock) {
                                AppClusterController.this.updateUidPolicyLocked(i2, bundle);
                            }
                        }
                    });
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
