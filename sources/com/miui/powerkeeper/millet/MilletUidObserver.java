package com.miui.powerkeeper.millet;

import android.app.AppOpsManager;
import android.app.IUidObserver;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.util.ArraySet;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.utils.PackageUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class MilletUidObserver extends IUidObserver.Stub {
    private static boolean LOG_DEBUG = true;
    private static String TAG = "UidObserver";
    private static Context mCtx;
    private MilletHandler mH;
    private PowerKeeperManager mPowerKeeperManager;
    private static SparseArray<String> mUidPkgName = new SparseArray<>();
    public static SparseBooleanArray mUidSystem = new SparseBooleanArray();
    private static SparseBooleanArray mUidRun = new SparseBooleanArray();
    private static SparseIntArray mUidState = new SparseIntArray();
    private static Set<Integer> autoStartLs = new ArraySet();
    private static int HOME_UID = -1;
    private AppOpsManager mAppOpsManager = null;
    private boolean isScreenOn = false;

    public MilletUidObserver(Context context, MilletHandler milletHandler, PowerKeeperManager powerKeeperManager) {
        mCtx = context;
        this.mH = milletHandler;
        this.mPowerKeeperManager = powerKeeperManager;
    }

    private void dealNewProcess(final int i2, final int i3) {
        MilletHandler milletHandler;
        if (this.isScreenOn || mUidState.indexOfKey(i2) >= 0 || this.mH == null) {
            if (!this.isScreenOn || mUidState.indexOfKey(i2) >= 0 || (milletHandler = this.mH) == null) {
                return;
            }
            milletHandler.postDelayed(new Runnable() { // from class: com.miui.powerkeeper.millet.MilletUidObserver.2
                @Override // java.lang.Runnable
                public void run() {
                    if (!MilletUidObserver.this.isScreenOn || MilletUidObserver.this.mH.getFrozenCtrl() == null) {
                        return;
                    }
                    MilletUidObserver.this.mH.getFrozenCtrl().takeNewProcess(i2, null);
                }
            }, 60000L);
            return;
        }
        Log.d(TAG, "dealNewProcess uid=" + i2);
        this.mH.postDelayed(new Runnable() { // from class: com.miui.powerkeeper.millet.MilletUidObserver.1
            @Override // java.lang.Runnable
            public void run() {
                if (i3 > 5) {
                    Message messageObtain = Message.obtain();
                    messageObtain.what = 15;
                    messageObtain.arg1 = i2;
                    MilletUidObserver.this.mH.sendMessage(messageObtain);
                }
            }
        }, 60000L);
    }

    public static List<String> getAllPackages() {
        ArrayList arrayList = new ArrayList();
        int size = mUidPkgName.size();
        for (int i2 = 0; i2 < size; i2++) {
            arrayList.add(mUidPkgName.valueAt(i2));
        }
        return arrayList;
    }

    public static Set<Integer> getAutoStartList() {
        return autoStartLs;
    }

    public static boolean getIsSystemApp(int i2) {
        String pkgNameByUid = getPkgNameByUid(i2);
        if (pkgNameByUid == null || !MilletConfig.supportSystemApp(pkgNameByUid)) {
            return mUidSystem.get(i2, false);
        }
        return false;
    }

    private String getPkgName(int i2) {
        if (mUidPkgName.indexOfKey(i2) < 0) {
            try {
                String[] packagesForUid = MilletUtils.getPackagesForUid(i2);
                if (packagesForUid == null || packagesForUid.length < 1) {
                    Log.d(TAG, "getPkgName array null uid:" + i2);
                    return null;
                }
                StringBuilder sb = new StringBuilder();
                sb.append("uid:");
                sb.append(i2);
                for (int i3 = 0; i3 < packagesForUid.length; i3++) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(" ");
                    sb2.append(packagesForUid[i3]);
                    if (isAllowAutoStart(packagesForUid[i3], i2)) {
                        Log.d(TAG, "autoStart name=" + packagesForUid[i3] + " uid=" + i2);
                        autoStartLs.add(Integer.valueOf(i2));
                        break;
                    }
                }
                Log.d(TAG, "getPkgName uid=" + i2 + " name=" + packagesForUid[0]);
                mUidPkgName.put(i2, packagesForUid[0]);
            } catch (Exception e2) {
                Log.e(TAG, "getPkgName error:", e2);
                return null;
            }
        }
        return mUidPkgName.get(i2);
    }

    public static String getPkgNameByUid(int i2) {
        if (mUidPkgName.indexOfKey(i2) > -1) {
            return mUidPkgName.get(i2);
        }
        try {
            String packageNameByUid = PackageUtil.getPackageNameByUid(mCtx, i2);
            if (packageNameByUid.length() > 8) {
                mUidPkgName.put(i2, packageNameByUid);
            }
            return mUidPkgName.get(i2);
        } catch (Exception unused) {
            Log.d(TAG, "getPkgNameByUid error uid = " + i2);
            return null;
        }
    }

    private boolean isAllowAutoStart(String str, int i2) {
        AppOpsManager appOpsManager = this.mAppOpsManager;
        return appOpsManager == null || appOpsManager.checkOpNoThrow(10008, i2, str) == 0;
    }

    public static boolean isHomeUid(int i2) {
        if (HOME_UID == -1) {
            HOME_UID = PackageUtil.getUidByPackageName(mCtx, "com.miui.home");
        }
        return i2 == HOME_UID;
    }

    public static void removePackage(String str) {
        int iKeyAt;
        for (int i2 = 0; i2 < mUidPkgName.size(); i2++) {
            if (str.equals(mUidPkgName.valueAt(i2))) {
                iKeyAt = mUidPkgName.keyAt(i2);
                mUidPkgName.delete(iKeyAt);
                mUidSystem.delete(iKeyAt);
                mUidRun.delete(iKeyAt);
                mUidState.delete(iKeyAt);
            }
        }
        iKeyAt = -1;
        mUidSystem.delete(iKeyAt);
        mUidRun.delete(iKeyAt);
        mUidState.delete(iKeyAt);
    }

    public void onUidActive(int i2) throws RemoteException {
        if (LOG_DEBUG) {
            Log.d(TAG, "onUidActive entry uid = " + i2);
        }
        if (!MilletUtils.isAppUid(i2) || getPkgName(i2) == null || mUidSystem.get(i2, false)) {
            return;
        }
        Log.i(TAG, "onUidActive uid:" + i2 + " name:" + mUidPkgName.get(i2) + " isScreenOn=" + this.isScreenOn);
    }

    public void onUidGone(int i2, boolean z2) throws RemoteException {
        Log.d(TAG, "onUidGone entry uid = " + i2);
        if (MilletUtils.isAppUid(i2)) {
            if (mUidRun.indexOfKey(i2) >= 0 && mUidRun.get(i2)) {
                setAppRunInBackGroundOp(i2, false);
            }
            mUidState.delete(i2);
            if (this.mH != null) {
                Message messageObtain = Message.obtain();
                messageObtain.what = 7;
                Bundle bundle = new Bundle();
                bundle.putInt("uid", i2);
                bundle.putInt("pid", -1);
                messageObtain.setData(bundle);
                this.mH.sendMessage(messageObtain);
            }
        }
    }

    public void onUidIdle(final int i2, boolean z2) throws RemoteException {
        if (MilletUtils.isAppUid(i2) && mUidState.indexOfKey(i2) < 0 && this.mH != null) {
            mUidState.put(i2, 100);
            this.mH.postDelayed(new Runnable() { // from class: com.miui.powerkeeper.millet.MilletUidObserver.3
                @Override // java.lang.Runnable
                public void run() {
                    if (MilletUidObserver.mUidState.indexOfKey(i2) < 0 || MilletUidObserver.mUidState.get(i2, 100) <= 5) {
                        return;
                    }
                    Log.d(MilletUidObserver.TAG, "onUidIdle uid=" + i2 + " uidstate=" + MilletUidObserver.mUidState.get(i2, -1));
                    Message messageObtain = Message.obtain();
                    messageObtain.what = 15;
                    messageObtain.arg1 = i2;
                    if (!MilletUidObserver.this.isScreenOn) {
                        MilletUidObserver.this.mH.sendMessage(messageObtain);
                    } else if (MilletUidObserver.this.mH.getFrozenCtrl() != null) {
                        MilletUidObserver.this.mH.getFrozenCtrl().takeNewProcess(i2, null);
                    }
                }
            }, 60000L);
        }
    }

    public void onUidStateChanged(int i2, int i3, long j2) throws RemoteException {
        if (LOG_DEBUG) {
            Log.d(TAG, "onUidStateChanged entry uid = " + i2 + " procState=" + i3);
        }
        if (i3 == 2 && this.mH != null && !this.isScreenOn) {
            Message messageObtain = Message.obtain();
            messageObtain.what = 2;
            this.mH.sendMessage(messageObtain);
        }
        if (MilletUtils.isAppUid(i2)) {
            if (this.isScreenOn && i3 <= 5 && getPkgName(i2) != null && (mUidState.indexOfKey(i2) < 0 || (mUidState.indexOfKey(i2) >= 0 && mUidState.get(i2, 100) > 5))) {
                setAppRunInBackGroundOp(i2, false);
            }
            dealNewProcess(i2, i3);
            mUidState.put(i2, i3);
        }
    }

    public void putIsSystem(int i2, boolean z2) {
        if (mUidSystem.indexOfKey(i2) < 0) {
            mUidSystem.put(i2, z2);
            getPkgName(i2);
        }
    }

    public void setScreenState(boolean z2) {
        this.isScreenOn = z2;
    }

    public void onUidStateChanged(int i2, int i3, long j2, int i4) throws RemoteException {
        if (LOG_DEBUG) {
            Log.d(TAG, "onUidStateChanged entry uid = " + i2 + " procState=" + i3);
        }
        if (i3 == 2 && this.mH != null && !this.isScreenOn) {
            Message messageObtain = Message.obtain();
            messageObtain.what = 2;
            this.mH.sendMessage(messageObtain);
        }
        if (MilletUtils.isAppUid(i2)) {
            if (this.isScreenOn && i3 <= 5 && getPkgName(i2) != null && (mUidState.indexOfKey(i2) < 0 || (mUidState.indexOfKey(i2) >= 0 && mUidState.get(i2, 100) > 5))) {
                setAppRunInBackGroundOp(i2, false);
            }
            dealNewProcess(i2, i3);
            mUidState.put(i2, i3);
        }
    }

    public void onUidCachedChanged(int i2, boolean z2) {
    }

    public void onUidProcAdjChanged(int i2, int i3) {
    }

    public void setAppRunInBackGroundOp(int i2, boolean z2) {
    }
}
