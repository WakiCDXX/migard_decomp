package com.miui.powerkeeper.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.util.ArraySet;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.content.PackageMonitor;
import com.android.internal.os.BackgroundThread;
import com.google.android.collect.Lists;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.UserCheckManager;
import com.miui.powerkeeper.event.EventsAggregator;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import miui.content.pm.PreloadedAppPolicy;

/* JADX INFO: loaded from: classes.dex */
public class PowerKeeperPackageManager {
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    private static final String TAG = "PowerKeeperPackageManager";
    private static volatile PowerKeeperPackageManager sInstance;
    private final List<PowerKeeperInterface.d0> mCallbacks;
    private final Context mContext;
    private final EventsAggregator mEventsAggregator;
    private final Handler mHandler;
    private final Object mLock;
    private MyPackageMonitor mPackageMonitor;
    private final UserCheckManager mUserCheckManager;
    private final PowerKeeperInterface.g0 mUserStatusChangedCallback;
    private final SparseArray<Map<String, Integer>> mUserPackageUidMap = new SparseArray<>();
    private final ArraySet<String> mInstalledThirdPartApps = new ArraySet<>(128);
    private final ArraySet<String> mUnremovableApps = new ArraySet<>(512);
    private PowerKeeperInterface.b0 mUserStatusChangedListener = new PowerKeeperInterface.b0() { // from class: com.miui.powerkeeper.utils.PowerKeeperPackageManager.2
        @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
        public void onUserEnableStatusChange(int i2, boolean z2) {
            synchronized (PowerKeeperPackageManager.this.mLock) {
                if (!z2) {
                    try {
                        Map map = (Map) PowerKeeperPackageManager.this.mUserPackageUidMap.get(i2, null);
                        if (map != null) {
                            map.clear();
                            PowerKeeperPackageManager.this.mUserPackageUidMap.delete(i2);
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
        public void onUserRunningStatusChange(int i2, boolean z2) {
            Map map;
            synchronized (PowerKeeperPackageManager.this.mLock) {
                try {
                    if (!z2) {
                        Map map2 = (Map) PowerKeeperPackageManager.this.mUserPackageUidMap.get(i2, null);
                        if (map2 != null) {
                            map2.clear();
                            PowerKeeperPackageManager.this.mUserPackageUidMap.delete(i2);
                        }
                    } else if (PowerKeeperPackageManager.this.updateCurrentPackageLocked(i2) && (map = (Map) PowerKeeperPackageManager.this.mUserPackageUidMap.get(i2, null)) != null) {
                        PowerKeeperPackageManager.this.notifyUserAdded(i2, new HashMap(map));
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
            PowerKeeperPackageManager.this.mHandler.postDelayed(new Runnable() { // from class: com.miui.powerkeeper.utils.PowerKeeperPackageManager.2.1
                @Override // java.lang.Runnable
                public void run() {
                    PowerKeeperPackageManager.this.updateInstalledThirdPartApps(null, false);
                }
            }, 3000L);
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
        public void onXSpaceStatusChange(int i2, boolean z2) {
            Map map;
            synchronized (PowerKeeperPackageManager.this.mLock) {
                try {
                    if (!z2) {
                        Map map2 = (Map) PowerKeeperPackageManager.this.mUserPackageUidMap.get(i2, null);
                        if (map2 != null) {
                            map2.clear();
                            PowerKeeperPackageManager.this.mUserPackageUidMap.delete(i2);
                        }
                    } else if (PowerKeeperPackageManager.this.updateXSpacePackageLocked(i2) && (map = (Map) PowerKeeperPackageManager.this.mUserPackageUidMap.get(i2, null)) != null) {
                        PowerKeeperPackageManager.this.notifyUserAdded(i2, new HashMap(map));
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
        public void onUserForegroundStatusChange(int i2, boolean z2) {
        }
    };

    private class MyPackageMonitor extends PackageMonitor {
        public void onPackageAdded(String str, int i2) {
            if (PowerKeeperPackageManager.this.needUpdateInstalledThirdPartApps(str, i2)) {
                PowerKeeperPackageManager.this.updateInstalledThirdPartApps(str, true);
            }
            PowerKeeperPackageManager.this.addPackage(str, i2);
        }

        public void onPackageModified(String str) {
            getChangingUserId();
        }

        public void onPackageRemoved(String str, int i2) {
            PowerKeeperPackageManager.this.removePackage(str, i2);
        }

        public void onPackageRemovedAllUsers(String str, int i2) {
            PowerKeeperPackageManager.this.updateInstalledThirdPartApps(str, false);
            PowerKeeperPackageManager.this.removePackage(str, i2);
        }

        public void onPackageUpdateFinished(String str, int i2) {
            PowerKeeperPackageManager.this.addPackage(str, i2);
        }

        public void onUidRemoved(int i2) {
            PowerKeeperPackageManager.this.removeUid(i2);
        }

        private MyPackageMonitor() {
        }
    }

    private PowerKeeperPackageManager(Context context, UserCheckManager userCheckManager, EventsAggregator eventsAggregator) {
        this.mPackageMonitor = null;
        this.mContext = context;
        this.mUserCheckManager = userCheckManager;
        this.mEventsAggregator = eventsAggregator;
        updateInstalledThirdPartApps(null, false);
        Object obj = new Object();
        this.mLock = obj;
        this.mCallbacks = Lists.newArrayList();
        Looper looper = BackgroundThread.getHandler().getLooper();
        Handler handler = new Handler(looper);
        this.mHandler = handler;
        PowerKeeperInterface.g0 g0Var = new PowerKeeperInterface.g0(handler, this.mUserStatusChangedListener);
        this.mUserStatusChangedCallback = g0Var;
        userCheckManager.J(g0Var);
        MyPackageMonitor myPackageMonitor = new MyPackageMonitor();
        this.mPackageMonitor = myPackageMonitor;
        myPackageMonitor.register(context, looper, UserHandle.ALL, true);
        synchronized (obj) {
            try {
                for (int i2 : userCheckManager.x()) {
                    updateCurrentPackageLocked(i2);
                }
                if (this.mUserCheckManager.E()) {
                    updateXSpacePackageLocked(this.mUserCheckManager.z());
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:19:0x007b  */
    public void addPackage(String str, int i2) {
        boolean z2;
        synchronized (this.mLock) {
            try {
                if (DEBUG) {
                    Log.d(TAG, "addPackage: " + str + " " + i2);
                }
                int userId = UserHandle.getUserId(i2);
                int i3 = 0;
                boolean z3 = true;
                if (this.mUserCheckManager.E()) {
                    int iZ = this.mUserCheckManager.z();
                    String str2 = TAG;
                    Log.d(str2, "addPackage: userId = " + userId + ", xspaceUserId = " + iZ);
                    if (userId != iZ) {
                        z2 = false;
                    } else {
                        if (!pkgHasIcon(str)) {
                            Log.d(str2, str + " cannot add to XSpace");
                            return;
                        }
                        z2 = true;
                    }
                } else {
                    z2 = false;
                }
                if (!z2) {
                    int[] iArrX = this.mUserCheckManager.x();
                    while (true) {
                        if (i3 >= iArrX.length) {
                            z3 = z2;
                            break;
                        } else if (userId == iArrX[i3]) {
                            break;
                        } else {
                            i3++;
                        }
                    }
                } else {
                    z3 = z2;
                    break;
                }
                if (!z3) {
                    if (DEBUG) {
                        Log.d(TAG, "addPackage: cannot find valid userId " + userId);
                    }
                    return;
                }
                Map<String, Integer> map = this.mUserPackageUidMap.get(userId, null);
                if (map == null) {
                    map = new HashMap<>();
                    this.mUserPackageUidMap.put(userId, map);
                }
                if (map.containsKey(str)) {
                    int iIntValue = map.get(str).intValue();
                    if (iIntValue != i2) {
                        map.put(str, Integer.valueOf(i2));
                        notifyPackageRemoved(userId, str, iIntValue);
                        notifyPackageAdded(userId, str, userId);
                    }
                } else {
                    map.put(str, Integer.valueOf(i2));
                    notifyPackageAdded(userId, str, i2);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public static ArraySet<String> getInstalledThirdPartApps() {
        PowerKeeperPackageManager powerKeeperPackageManager = sInstance;
        ArraySet<String> arraySet = new ArraySet<>();
        if (powerKeeperPackageManager != null) {
            synchronized (powerKeeperPackageManager.mInstalledThirdPartApps) {
                arraySet.addAll((ArraySet<? extends String>) powerKeeperPackageManager.mInstalledThirdPartApps);
            }
        }
        return arraySet;
    }

    public static PowerKeeperPackageManager getInstance(Context context, UserCheckManager userCheckManager, EventsAggregator eventsAggregator) {
        if (sInstance == null) {
            synchronized (PowerKeeperPackageManager.class) {
                try {
                    if (sInstance == null) {
                        sInstance = new PowerKeeperPackageManager(context.getApplicationContext(), userCheckManager, eventsAggregator);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return sInstance;
    }

    public static boolean isUnremovableApp(String str) {
        PowerKeeperPackageManager powerKeeperPackageManager = sInstance;
        if (powerKeeperPackageManager == null || str == null) {
            return false;
        }
        return powerKeeperPackageManager.mUnremovableApps.contains(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean needUpdateInstalledThirdPartApps(String str, int i2) {
        int userId = UserHandle.getUserId(i2);
        if (userId == 0) {
            return true;
        }
        try {
            ApplicationInfo applicationInfoAsUser = this.mContext.getPackageManager().getApplicationInfoAsUser(str, 0, userId);
            if (applicationInfoAsUser != null) {
                if (!applicationInfoAsUser.isSystemApp()) {
                    if (PreloadedAppPolicy.isProtectedDataApp(this.mContext, applicationInfoAsUser.packageName, 0)) {
                    }
                }
                if (DEBUG) {
                    Log.d(TAG, "needUpdateInstalledThirdPartApps false for " + str);
                }
                return false;
            }
        } catch (Exception e2) {
            Log.w(TAG, "needUpdateInstalledThirdPartApps Exception", e2);
        }
        return true;
    }

    private void notifyPackageAdded(final int i2, final String str, final int i3) {
        EventsAggregator.getInstance(this.mContext).notifyPackageAddedListeners(i2, i3, str);
        synchronized (this.mLock) {
            try {
                for (final PowerKeeperInterface.d0 d0Var : this.mCallbacks) {
                    Handler handler = d0Var.f253a;
                    if (handler == null) {
                        handler = this.mHandler;
                    }
                    handler.post(new Runnable() { // from class: com.miui.powerkeeper.utils.PowerKeeperPackageManager.4
                        @Override // java.lang.Runnable
                        public void run() {
                            PowerKeeperInterface.x xVar;
                            synchronized (PowerKeeperPackageManager.this.mLock) {
                                xVar = d0Var.f254b;
                            }
                            if (xVar != null) {
                                xVar.onPackageAdded(i2, str, i3);
                            }
                        }
                    });
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private void notifyPackageRemoved(final int i2, final String str, final int i3) {
        EventsAggregator.getInstance(this.mContext).notifyPackageRemovedListeners(i2, i3, str);
        synchronized (this.mLock) {
            try {
                for (final PowerKeeperInterface.d0 d0Var : this.mCallbacks) {
                    Handler handler = d0Var.f253a;
                    if (handler == null) {
                        handler = this.mHandler;
                    }
                    handler.post(new Runnable() { // from class: com.miui.powerkeeper.utils.PowerKeeperPackageManager.5
                        @Override // java.lang.Runnable
                        public void run() {
                            PowerKeeperInterface.x xVar;
                            synchronized (PowerKeeperPackageManager.this.mLock) {
                                xVar = d0Var.f254b;
                            }
                            if (xVar != null) {
                                xVar.onPackageRemoved(i2, str, i3);
                            }
                        }
                    });
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private void notifyUidRemoved(final int i2, final int i3) {
        EventsAggregator.getInstance(this.mContext).notifyUidRemovedListeners(i2, i3);
        synchronized (this.mLock) {
            try {
                for (final PowerKeeperInterface.d0 d0Var : this.mCallbacks) {
                    Handler handler = d0Var.f253a;
                    if (handler == null) {
                        handler = this.mHandler;
                    }
                    handler.post(new Runnable() { // from class: com.miui.powerkeeper.utils.PowerKeeperPackageManager.6
                        @Override // java.lang.Runnable
                        public void run() {
                            PowerKeeperInterface.x xVar;
                            synchronized (PowerKeeperPackageManager.this.mLock) {
                                xVar = d0Var.f254b;
                            }
                            if (xVar != null) {
                                xVar.onUidRemoved(i2, i3);
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
    public void notifyUserAdded(final int i2, final Map<String, Integer> map) {
        synchronized (this.mLock) {
            try {
                for (final PowerKeeperInterface.d0 d0Var : this.mCallbacks) {
                    Handler handler = d0Var.f253a;
                    if (handler == null) {
                        handler = this.mHandler;
                    }
                    handler.post(new Runnable() { // from class: com.miui.powerkeeper.utils.PowerKeeperPackageManager.3
                        @Override // java.lang.Runnable
                        public void run() {
                            PowerKeeperInterface.x xVar;
                            synchronized (PowerKeeperPackageManager.this.mLock) {
                                xVar = d0Var.f254b;
                            }
                            if (xVar != null) {
                                xVar.onUserReadyPackageAdded(i2, map);
                            }
                        }
                    });
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private boolean pkgHasIcon(String str) {
        return this.mContext.getPackageManager().getLaunchIntentForPackage(str) != null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removePackage(String str, int i2) {
        synchronized (this.mLock) {
            try {
                if (DEBUG) {
                    Log.d(TAG, "removePackage: " + str + " " + i2);
                }
                int userId = UserHandle.getUserId(i2);
                Map<String, Integer> map = this.mUserPackageUidMap.get(userId, null);
                if (map != null && map.containsKey(str)) {
                    map.remove(str);
                    notifyPackageRemoved(userId, str, i2);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeUid(int i2) {
        synchronized (this.mLock) {
            try {
                if (DEBUG) {
                    Log.d(TAG, "removeUid: " + i2);
                }
                notifyUidRemoved(UserHandle.getUserId(i2), i2);
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean updateCurrentPackageLocked(int i2) {
        Log.d(TAG, "updateCurrentPackageLocked, userId = " + i2);
        Map<String, Integer> map = this.mUserPackageUidMap.get(i2, null);
        if (map == null) {
            map = new HashMap<>();
            this.mUserPackageUidMap.put(i2, map);
        }
        map.clear();
        List<ApplicationInfo> allApps = PackageUtil.getAllApps(this.mContext, i2);
        for (int i3 = 0; i3 < allApps.size(); i3++) {
            ApplicationInfo applicationInfo = allApps.get(i3);
            String str = applicationInfo.packageName;
            if (!map.containsKey(str)) {
                map.put(str, Integer.valueOf(applicationInfo.uid));
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateInstalledThirdPartApps(String str, boolean z2) {
        if (DEBUG) {
            Log.d(TAG, "updateInstalledThirdPartApps pkg: " + str + " isAdd:" + z2);
        }
        synchronized (this.mInstalledThirdPartApps) {
            try {
                if (str == null) {
                    this.mUnremovableApps.clear();
                    this.mInstalledThirdPartApps.clear();
                    int[] iArrX = this.mUserCheckManager.x();
                    PackageManager packageManager = this.mContext.getPackageManager();
                    for (int i2 : iArrX) {
                        for (ApplicationInfo applicationInfo : packageManager.getInstalledApplicationsAsUser(0, i2)) {
                            if (applicationInfo.isSystemApp()) {
                                this.mUnremovableApps.add(applicationInfo.packageName);
                            } else if (PreloadedAppPolicy.isProtectedDataApp(this.mContext, applicationInfo.packageName, 0)) {
                                this.mUnremovableApps.add(applicationInfo.packageName);
                            } else {
                                this.mInstalledThirdPartApps.add(applicationInfo.packageName);
                            }
                        }
                    }
                    this.mEventsAggregator.notifyForPkgExistentState(new EventsAggregator.EventResult(2, new ArraySet((ArraySet) this.mInstalledThirdPartApps)));
                } else if (z2) {
                    if (this.mInstalledThirdPartApps.add(str)) {
                        this.mEventsAggregator.notifyForPkgExistentState(new EventsAggregator.EventResult(1, str));
                    }
                } else if (this.mInstalledThirdPartApps.remove(str)) {
                    this.mEventsAggregator.notifyForPkgExistentState(new EventsAggregator.EventResult(0, str));
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean updateXSpacePackageLocked(int i2) {
        Log.d(TAG, "updateXSpacePackageLocked, userId = " + i2);
        Map<String, Integer> map = this.mUserPackageUidMap.get(i2, null);
        if (map == null) {
            map = new HashMap<>();
            this.mUserPackageUidMap.put(i2, map);
        }
        map.clear();
        List<ApplicationInfo> allApps = PackageUtil.getAllApps(this.mContext, i2);
        for (int i3 = 0; i3 < allApps.size(); i3++) {
            ApplicationInfo applicationInfo = allApps.get(i3);
            String str = applicationInfo.packageName;
            if (pkgHasIcon(str)) {
                if (DEBUG) {
                    Log.d(TAG, str + " add to XSpace");
                }
                if (!map.containsKey(str)) {
                    map.put(str, Integer.valueOf(applicationInfo.uid));
                }
            }
        }
        return true;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("dump PowerKeeperPackageManager: ");
        if (Build.IS_DEBUGGABLE) {
            try {
                printWriter.println("unremovableApps=" + this.mUnremovableApps);
                printWriter.println("installedThirdApp=" + this.mInstalledThirdPartApps);
            } catch (Exception e2) {
                printWriter.println("exception=" + e2);
            }
        }
    }

    public Map<String, Integer> getPackageUidsMap(int i2) {
        synchronized (this.mLock) {
            try {
                Map<String, Integer> map = this.mUserPackageUidMap.get(i2, null);
                if (map != null) {
                    return new HashMap(map);
                }
                return new HashMap();
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void registerPackageChangedListener(final PowerKeeperInterface.d0 d0Var) {
        synchronized (this.mLock) {
            try {
                if (!this.mCallbacks.contains(d0Var)) {
                    this.mCallbacks.add(d0Var);
                    Handler handler = d0Var.f253a;
                    if (handler == null) {
                        handler = BackgroundThread.getHandler();
                    }
                    handler.post(new Runnable() { // from class: com.miui.powerkeeper.utils.PowerKeeperPackageManager.1
                        @Override // java.lang.Runnable
                        public void run() {
                            PowerKeeperInterface.x xVar;
                            synchronized (PowerKeeperPackageManager.this.mLock) {
                                xVar = d0Var.f254b;
                            }
                            if (xVar != null) {
                                xVar.onRegistered();
                            }
                        }
                    });
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void unregisterPackageChangedListener(PowerKeeperInterface.d0 d0Var) {
        synchronized (this.mLock) {
            try {
                if (this.mCallbacks.contains(d0Var)) {
                    this.mCallbacks.remove(d0Var);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
