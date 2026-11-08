package com.miui.powerkeeper.active;

import android.app.AppOpsManager;
import android.content.Context;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.SystemClock;
import android.util.LocalLog;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.provider.AppActiveConfigure;
import com.miui.powerkeeper.provider.PowerKeeperConfigure;
import com.miui.powerkeeper.utils.Constant;
import com.miui.powerkeeper.utils.PackageUtil;
import com.miui.powerkeeper.utils.Utils;
import com.miui.powerkeeper.utils.VpnTracker;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ActiveController {
    public static final int ACTIVE_TYPE_ALL = 31;
    public static final int ACTIVE_TYPE_AUDIO = 3;
    public static final int ACTIVE_TYPE_AUDIO_GPS_VPN = 15;
    public static final int ACTIVE_TYPE_AUDIO_PLAY = 1;
    public static final int ACTIVE_TYPE_AUDIO_RECORD = 2;
    public static final int ACTIVE_TYPE_AUDIO_VPN = 11;
    public static final int ACTIVE_TYPE_DOWNLOAD = 16;
    public static final int ACTIVE_TYPE_DOWNLOAD_VPN_GPS = 28;
    public static final int ACTIVE_TYPE_GPS = 4;
    public static final int ACTIVE_TYPE_NONE = 0;
    public static final int ACTIVE_TYPE_VPN = 8;
    private static final long DISGUISE_INTER_TIME = 65000;
    static final String TAG = "PowerKeeper.Active";
    private static final Uri sURI = Uri.withAppendedPath(PowerKeeperConfigure.CONTENT_URI, AppActiveConfigure.Columns.ACTIVE);
    private SparseArray<Integer> mActiveResult;
    private SparseLongArray mAudioDisguiseUids;
    private final LocalLog mLocalLog;
    private final Object mLock;
    private AppOpsManager.OnOpActiveChangedListener mOpListener;
    private HashSet<Integer> mPlayPids;
    private SparseArray<SparseArray<ArrayList>> mPlayUid;
    private PowerKeeperManager mPowerKeeperManager;
    private HashSet<Integer> mRecordPids;
    private SparseArray<ArrayList<PowerKeeperInterface.j>> mTypeListener;
    private SparseIntArray mUidUnctiveTime;
    private VpnTracker.StatusCallback mVpnCallback;
    private VpnTracker mVpnTracker;

    public ActiveController(VpnTracker vpnTracker, PowerKeeperManager powerKeeperManager, Context context) {
        this.mLocalLog = new LocalLog(Constant.DEBUG ? 256 : 128);
        this.mLock = new Object();
        this.mTypeListener = new SparseArray<>(2);
        this.mPlayPids = new HashSet<>(2);
        this.mRecordPids = new HashSet<>(2);
        this.mPlayUid = new SparseArray<>(4);
        this.mAudioDisguiseUids = new SparseLongArray();
        this.mUidUnctiveTime = new SparseIntArray();
        this.mOpListener = new AppOpsManager.OnOpActiveChangedListener() { // from class: com.miui.powerkeeper.active.ActiveController.1
            public void onOpActiveChanged(int i2, int i3, String str, boolean z2) {
                if (Constant.DBG_ACTIVE) {
                    Slog.d(ActiveController.TAG, "onOpActiveChanged uid=" + i3 + " pkg=" + str + " active=" + z2);
                }
                synchronized (ActiveController.this.mLock) {
                    try {
                        if (z2) {
                            ActiveController.this.addActiveLocked(i3, 4);
                        } else {
                            ActiveController.this.removeActiveByUidLocked(i3, 4);
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }

            @Override // android.app.AppOpsManager.OnOpActiveChangedListener
            public void onOpActiveChanged(String str, int i2, String str2, boolean z2) {
                if (Constant.DBG_ACTIVE) {
                    Slog.d(ActiveController.TAG, "onOpActiveChanged r uid=" + i2 + " pkg=" + str2 + " active=" + z2);
                }
                synchronized (ActiveController.this.mLock) {
                    try {
                        if (z2) {
                            ActiveController.this.addActiveLocked(i2, 4);
                        } else {
                            ActiveController.this.removeActiveByUidLocked(i2, 4);
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        };
        this.mVpnCallback = new VpnTracker.StatusCallback() { // from class: com.miui.powerkeeper.active.ActiveController.2
            @Override // com.miui.powerkeeper.utils.VpnTracker.StatusCallback
            public void onConnected(int i2) {
                synchronized (ActiveController.this.mLock) {
                    ActiveController.this.addActiveLocked(i2, 8);
                }
            }

            @Override // com.miui.powerkeeper.utils.VpnTracker.StatusCallback
            public void onDisconnected(int i2) {
                synchronized (ActiveController.this.mLock) {
                    ActiveController.this.removeActiveByUidLocked(i2, 8);
                }
            }
        };
        this.mActiveResult = new SparseArray<>();
        this.mVpnTracker = vpnTracker;
        vpnTracker.registerVpnStatusCallback(this.mVpnCallback);
        this.mPowerKeeperManager = powerKeeperManager;
        ((AppOpsManager) context.getSystemService("appops")).startWatchingActive(new int[]{2}, this.mOpListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addActiveLocked(int i2, int i3) {
        String str = "add active uid = " + i2 + ", type = " + i3;
        this.mLocalLog.log(str);
        if (Constant.DEBUG) {
            Slog.d(TAG, str);
        }
        int iIntValue = this.mActiveResult.get(i2, 0).intValue();
        int i4 = i3 | iIntValue;
        this.mActiveResult.put(i2, Integer.valueOf(i4));
        notifyChange(i2, iIntValue, i4);
    }

    private void genItem(MatrixCursor matrixCursor, int i2, Integer num) {
        num.intValue();
        matrixCursor.addRow(new Object[]{Integer.valueOf(i2), num});
    }

    private void notifyChange(int i2, int i3, int i4) {
        if (i3 != i4) {
            int size = this.mTypeListener.size();
            if (Constant.DBG_ACTIVE) {
                Slog.d(TAG, "notifyChange uid = " + i2 + " oldType=" + i3 + " newType=" + i4 + " size=" + size);
            }
            for (int i5 = 0; i5 < size; i5++) {
                boolean z2 = (this.mTypeListener.keyAt(i5) & i3) != 0;
                boolean z3 = (this.mTypeListener.keyAt(i5) & i4) != 0;
                if (z2 != z3) {
                    Iterator<PowerKeeperInterface.j> it = this.mTypeListener.valueAt(i5).iterator();
                    while (it.hasNext()) {
                        it.next().onAppActiveChange(i2, z3);
                    }
                    EventsAggregator.getInstance(null).notifyActiveListeners(i2, z3);
                }
            }
            Utils.APP_CONTEXT.getContentResolver().notifyChange(sURI, null);
        }
    }

    private void removeActiveByTypeLocked(int i2) {
        String str = "remove active type = " + i2;
        this.mLocalLog.log(str);
        if (Constant.DEBUG) {
            Slog.d(TAG, str);
        }
        for (int i3 = 0; i3 < this.mActiveResult.size(); i3++) {
            int iKeyAt = this.mActiveResult.keyAt(i3);
            Integer numValueAt = this.mActiveResult.valueAt(i3);
            int iIntValue = numValueAt == null ? 0 : numValueAt.intValue();
            int i4 = (~i2) & iIntValue;
            if (i4 != 0) {
                this.mActiveResult.put(iKeyAt, Integer.valueOf(i4));
            } else {
                this.mActiveResult.remove(iKeyAt);
            }
            notifyChange(iKeyAt, iIntValue, i4);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeActiveByUidLocked(int i2, int i3) {
        Integer num = this.mActiveResult.get(i2);
        if (num != null) {
            int iIntValue = num.intValue() & (~i3);
            String str = "remove active uid = " + i2 + ", type = " + i3 + ", new=" + iIntValue;
            this.mLocalLog.log(str);
            if (Constant.DEBUG) {
                Slog.d(TAG, str);
            }
            if (iIntValue != 0) {
                this.mActiveResult.put(i2, Integer.valueOf(iIntValue));
            } else {
                this.mActiveResult.remove(i2);
            }
            notifyChange(i2, num.intValue(), iIntValue);
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("dump of ActiveController:");
        synchronized (this.mLock) {
            try {
                this.mLocalLog.dump(fileDescriptor, printWriter, strArr);
                printWriter.println("mPlayPids = " + this.mPlayPids + " mRecordPids = " + this.mRecordPids);
                for (int i2 = 0; i2 < this.mActiveResult.size(); i2++) {
                    int iKeyAt = this.mActiveResult.keyAt(i2);
                    printWriter.println("uid = " + iKeyAt + ", active: " + this.mActiveResult.get(iKeyAt));
                }
                printWriter.println("mAudioDisguiseUids = " + this.mAudioDisguiseUids);
                printWriter.println("mPlayUid = " + this.mPlayUid);
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public ArrayList<Integer> getActiveAudioPids() {
        ArrayList<Integer> arrayList = new ArrayList<>(3);
        synchronized (this.mLock) {
            arrayList.addAll(this.mPlayPids);
            arrayList.addAll(this.mRecordPids);
        }
        return arrayList;
    }

    public List<String> getActivePkgList(Context context, int i2) {
        List<Integer> activeUids = getActiveUids(i2);
        ArrayList arrayList = null;
        for (Integer num : activeUids) {
            if (arrayList == null) {
                arrayList = new ArrayList(activeUids.size());
            }
            arrayList.add(PackageUtil.getPackageNameByUid(context, num.intValue()));
        }
        return arrayList;
    }

    public List<Integer> getActiveUids(int i2) {
        ArrayList arrayList = new ArrayList();
        synchronized (this.mLock) {
            try {
                int size = this.mActiveResult.size();
                for (int i3 = 0; i3 < size; i3++) {
                    if ((this.mActiveResult.valueAt(i3).intValue() & i2) != 0) {
                        arrayList.add(Integer.valueOf(this.mActiveResult.keyAt(i3)));
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return arrayList;
    }

    public int getAppActiveType(int i2) {
        int iIntValue;
        synchronized (this.mLock) {
            iIntValue = this.mActiveResult.get(i2, 0).intValue();
        }
        return iIntValue;
    }

    public SparseIntArray getUnActiveTime() {
        return this.mUidUnctiveTime;
    }

    public boolean hasActiveApp(int i2) {
        synchronized (this.mLock) {
            try {
                int size = this.mActiveResult.size();
                for (int i3 = 0; i3 < size; i3++) {
                    if ((this.mActiveResult.valueAt(i3).intValue() & i2) != 0) {
                        return true;
                    }
                }
                return false;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public boolean isAppActive(int i2) {
        return getAppActiveType(i2) != 0;
    }

    public boolean isDisguiseChecked(int i2) {
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        synchronized (this.mAudioDisguiseUids) {
            try {
                long j2 = this.mAudioDisguiseUids.get(i2, 0L);
                return j2 > 0 && jElapsedRealtime - j2 < DISGUISE_INTER_TIME;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void loadActiveListByApp(MatrixCursor matrixCursor, int i2) {
        synchronized (this.mLock) {
            if (matrixCursor != null) {
                try {
                    Integer num = this.mActiveResult.get(i2);
                    if (num != null) {
                        genItem(matrixCursor, i2, num);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    public void loadActiveListByType(MatrixCursor matrixCursor, int i2) {
        synchronized (this.mLock) {
            if (matrixCursor != null) {
                for (int i3 = 0; i3 < this.mActiveResult.size(); i3++) {
                    try {
                        int iKeyAt = this.mActiveResult.keyAt(i3);
                        Integer num = this.mActiveResult.get(iKeyAt);
                        if (num != null && (num.intValue() & i2) != 0) {
                            genItem(matrixCursor, iKeyAt, num);
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        }
    }

    public SparseBooleanArray loadActiveUid(SparseBooleanArray sparseBooleanArray, int i2) {
        if (sparseBooleanArray == null) {
            sparseBooleanArray = new SparseBooleanArray(this.mActiveResult.size());
        }
        synchronized (this.mLock) {
            try {
                int size = this.mActiveResult.size();
                for (int i3 = 0; i3 < size; i3++) {
                    if ((this.mActiveResult.valueAt(i3).intValue() & i2) != 0) {
                        sparseBooleanArray.put(this.mActiveResult.keyAt(i3), true);
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return sparseBooleanArray;
    }

    public void onAudioStatusChanged(int i2, int i3, int i4) {
        if (Constant.DBG_ACTIVE) {
            Slog.d(TAG, "onAudioStatusChanged pid = " + i2 + " uid=" + i3 + " status=" + i4);
        }
        synchronized (this.mLock) {
            try {
                if (i4 == 1) {
                    this.mPlayPids.add(Integer.valueOf(i2));
                    this.mUidUnctiveTime.put(i3, 0);
                    addActiveLocked(i3, 1);
                } else {
                    this.mPlayPids.remove(Integer.valueOf(i2));
                    this.mUidUnctiveTime.put(i3, (int) (SystemClock.elapsedRealtime() / 1000));
                    removeActiveByUidLocked(i3, 1);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void onDisguiseChecked(int i2, int i3) {
        boolean zIsUidForeground = this.mPowerKeeperManager.isUidForeground(i3);
        boolean zIsScreenOn = this.mPowerKeeperManager.isScreenOn();
        if (!zIsUidForeground) {
            EventsAggregator.getInstance(null).notifyAudioDisguise(i3, i2);
        }
        if (Constant.DBG_ACTIVE) {
            Slog.d(TAG, "onDisguiseChecked pid = " + i2 + " uid=" + i3 + ", isForegroundUid" + zIsUidForeground + ", isScreenOn = " + zIsScreenOn);
        }
        synchronized (this.mAudioDisguiseUids) {
            if (!zIsUidForeground || !zIsScreenOn) {
                this.mAudioDisguiseUids.append(i3, SystemClock.elapsedRealtime());
            }
        }
    }

    public void onRecordStatusChanged(int i2, int i3, int i4) {
        if (Constant.DBG_ACTIVE) {
            Slog.d(TAG, "onRecordStatusChanged pid = " + i2 + " uid=" + i3 + " status=" + i4);
        }
        synchronized (this.mLock) {
            try {
                if (i4 == 1) {
                    this.mRecordPids.add(Integer.valueOf(i2));
                    this.mUidUnctiveTime.put(i3, 0);
                    addActiveLocked(i3, 2);
                } else {
                    this.mRecordPids.remove(Integer.valueOf(i2));
                    this.mUidUnctiveTime.put(i3, (int) (SystemClock.elapsedRealtime() / 1000));
                    removeActiveByUidLocked(i3, 2);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void registerActiveListener(PowerKeeperInterface.j jVar, int i2) {
        if (jVar == null) {
            return;
        }
        if (Constant.DBG_ACTIVE) {
            Slog.d(TAG, "registerActiveListener type = " + i2 + " l=" + jVar.getClass().getName());
        }
        synchronized (this.mLock) {
            try {
                ArrayList<PowerKeeperInterface.j> arrayList = this.mTypeListener.get(i2);
                if (arrayList == null) {
                    ArrayList<PowerKeeperInterface.j> arrayList2 = new ArrayList<>(1);
                    arrayList2.add(jVar);
                    this.mTypeListener.put(i2, arrayList2);
                } else if (!arrayList.contains(jVar)) {
                    arrayList.add(jVar);
                    arrayList.trimToSize();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void unregisterActiveListener(PowerKeeperInterface.j jVar, int i2) {
        if (jVar == null) {
            return;
        }
        if (Constant.DBG_ACTIVE) {
            Slog.d(TAG, "unregisterActiveListener type = " + i2 + " l=" + jVar.getClass().getName());
        }
        synchronized (this.mLock) {
            try {
                ArrayList<PowerKeeperInterface.j> arrayList = this.mTypeListener.get(i2);
                if (arrayList != null) {
                    arrayList.remove(jVar);
                    if (arrayList.isEmpty()) {
                        this.mTypeListener.delete(i2);
                    } else {
                        arrayList.trimToSize();
                    }
                    return;
                }
                Slog.w(TAG, "unregisterActiveListener type = " + i2 + " l=" + jVar.getClass().getName());
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void onDisguiseChecked(int i2, int i3, int i4, int i5) {
        boolean zIsUidForeground = this.mPowerKeeperManager.isUidForeground(i3);
        boolean zIsScreenOn = this.mPowerKeeperManager.isScreenOn();
        Slog.d(TAG, "onDisguiseChecked isForegroundUid = " + zIsUidForeground);
        if (!zIsUidForeground || !zIsScreenOn) {
            int[] iArr = {i4, i5};
            Slog.i(TAG, "onDisguiseChecked sesionIdStatus = " + iArr);
            EventsAggregator.getInstance(null).notifyAudioDisguise(i3, i2, iArr);
        }
        if (Constant.DBG_ACTIVE) {
            Slog.d(TAG, "onDisguiseChecked pid = " + i2 + " uid=" + i3 + ", isForegroundUid" + zIsUidForeground + ", isScreenOn = " + zIsScreenOn + ", status is " + i5);
        }
        synchronized (this.mAudioDisguiseUids) {
            if ((!zIsUidForeground || !zIsScreenOn) && i5 == 0) {
                try {
                    this.mAudioDisguiseUids.append(i3, SystemClock.elapsedRealtime());
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    public void onAudioStatusChanged(int i2, int i3, int i4, int i5) {
        SparseArray<ArrayList> sparseArray;
        ArrayList arrayList;
        if (Constant.DBG_ACTIVE) {
            Slog.d(TAG, "onAudioStatusChanged pid = " + i2 + " uid=" + i3 + " status=" + i4 + " sessionId=" + i5);
        }
        synchronized (this.mLock) {
            try {
                if (i4 == 0) {
                    SparseArray<ArrayList> sparseArray2 = this.mPlayUid.get(i3, null);
                    if (sparseArray2 != null) {
                        sparseArray2.delete(i2);
                        this.mPlayPids.remove(Integer.valueOf(i2));
                        if (sparseArray2.size() == 0) {
                            this.mPlayUid.remove(i3);
                            this.mUidUnctiveTime.put(i3, (int) (SystemClock.elapsedRealtime() / 1000));
                            removeActiveByUidLocked(i3, 1);
                        }
                    }
                } else if (i4 == 1) {
                    SparseArray<ArrayList> sparseArray3 = this.mPlayUid.get(i3, null);
                    if (sparseArray3 == null) {
                        sparseArray3 = new SparseArray<>(2);
                        this.mPlayUid.put(i3, sparseArray3);
                    }
                    ArrayList arrayList2 = sparseArray3.get(i2, null);
                    if (arrayList2 == null) {
                        arrayList2 = new ArrayList(2);
                        sparseArray3.put(i2, arrayList2);
                    }
                    arrayList2.add(Integer.valueOf(i5));
                    this.mPlayPids.add(Integer.valueOf(i2));
                    this.mUidUnctiveTime.put(i3, 0);
                    addActiveLocked(i3, 1);
                } else if (i4 == 2 && (sparseArray = this.mPlayUid.get(i3, null)) != null && (arrayList = sparseArray.get(i2, null)) != null) {
                    Iterator it = arrayList.iterator();
                    while (it.hasNext()) {
                        if (((Integer) it.next()).equals(Integer.valueOf(i5))) {
                            it.remove();
                            break;
                        }
                    }
                    if (arrayList.size() == 0) {
                        sparseArray.delete(i2);
                        this.mPlayPids.remove(Integer.valueOf(i2));
                        if (sparseArray.size() == 0) {
                            this.mPlayUid.remove(i3);
                            this.mUidUnctiveTime.put(i3, (int) (SystemClock.elapsedRealtime() / 1000));
                            removeActiveByUidLocked(i3, 1);
                        }
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
