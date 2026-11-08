package com.miui.powerkeeper.millet;

import android.os.Build;
import android.os.IBinder;
import android.os.Parcel;
import android.os.ServiceManager;
import android.util.ArraySet;
import android.util.Log;
import com.miui.powerkeeper.PowerKeeperInterface;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import miui.greeze.IGreezeCallback;

/* JADX INFO: loaded from: classes.dex */
public class FreezeBinder {
    private static final int AUDIO_GPS_UID = 11;
    private static final int CHECK_UID_FROZEN = 17;
    private static final int CLOSE_SOCKET = 19;
    private static final int FREEZE_PIDS = 5;
    private static final int FREEZE_UIDS = 6;
    private static final int GET_FROZEN_PIDS = 10;
    private static final int GET_FROZEN_UIDS = 14;
    private static final int GET_IMPORTANT_APPS = 22;
    private static final int GREEZER_MODULE_ALL = 9999;
    private static final int GREEZER_MODULE_POWER = 1;
    private static final int REGISTER_CALLBACK = 15;
    private static String TAG = "FreezeBinder";
    private static final int THAWED_ALL = 9;
    private static final int THAWED_PIDS = 7;
    private static final int THAWED_UIDS = 8;
    private static final int UPDATE_UID_RULE = 18;
    private static String mServiceName = "miui.greeze.IGreezeManager";
    private static FreezeBinder sInstance;
    public IBinder IGreeze;
    private List<PowerKeeperInterface.u> listeners = new ArrayList();

    private FreezeBinder() {
        this.IGreeze = null;
        try {
            this.IGreeze = ServiceManager.getService("greezer");
        } catch (Exception e2) {
            Log.e(TAG, "get greezer service", e2);
        }
    }

    private void closeSocket(int[] iArr) {
        if (MilletConfig.getRestrictNet()) {
            Parcel parcelObtain = Parcel.obtain();
            Parcel parcelObtain2 = Parcel.obtain();
            try {
                try {
                    parcelObtain.writeInterfaceToken(mServiceName);
                    parcelObtain.writeIntArray(iArr);
                    this.IGreeze.transact(19, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    parcelObtain.recycle();
                } catch (Exception e2) {
                    e2.fillInStackTrace();
                    Log.e(TAG, "closeSocket:" + e2);
                    if (parcelObtain != null) {
                        parcelObtain.recycle();
                    }
                    if (parcelObtain2 == null) {
                        return;
                    }
                }
                parcelObtain2.recycle();
            } catch (Throwable th) {
                if (parcelObtain != null) {
                    parcelObtain.recycle();
                }
                if (parcelObtain2 != null) {
                    parcelObtain2.recycle();
                }
                throw th;
            }
        }
    }

    public static boolean freezeUid(int i2, String str) {
        List<Integer> listFreezeUids = getInstance().freezeUids(new int[]{i2}, 0L, str, false);
        return listFreezeUids.size() == 1 && listFreezeUids.get(0).intValue() == i2;
    }

    private int[] getFrozenUids(int i2) {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        int[] iArrCreateIntArray = new int[0];
        try {
            try {
                parcelObtain.writeInterfaceToken(mServiceName);
                parcelObtain.writeInt(i2);
                this.IGreeze.transact(14, parcelObtain, parcelObtain2, 0);
                parcelObtain2.readException();
                iArrCreateIntArray = parcelObtain2.createIntArray();
                parcelObtain.recycle();
            } catch (Exception e2) {
                e2.fillInStackTrace();
                Log.e(TAG, "getFrozenUids:" + e2);
                if (parcelObtain != null) {
                    parcelObtain.recycle();
                }
                if (parcelObtain2 != null) {
                }
                return iArrCreateIntArray;
            }
            parcelObtain2.recycle();
            return iArrCreateIntArray;
        } catch (Throwable th) {
            if (parcelObtain != null) {
                parcelObtain.recycle();
            }
            if (parcelObtain2 != null) {
                parcelObtain2.recycle();
            }
            throw th;
        }
    }

    public static FreezeBinder getInstance() {
        if (sInstance == null) {
            sInstance = new FreezeBinder();
        }
        return sInstance;
    }

    private void resourceControl(int i2, boolean z2) {
        if (MilletConfig.getSensorEnable()) {
            MilletUtils.setAppSensorsControlPolicy(i2, !z2);
        }
    }

    public static boolean thawUid(int i2, String str) {
        List<Integer> listThawUids = getInstance().thawUids(new int[]{i2}, str);
        return listThawUids.size() == 1 && listThawUids.get(0).intValue() == i2;
    }

    private void updateAurogonUidRule(int i2, boolean z2) {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            try {
                parcelObtain.writeInterfaceToken(mServiceName);
                parcelObtain.writeInt(i2);
                parcelObtain.writeBoolean(z2);
                this.IGreeze.transact(18, parcelObtain, parcelObtain2, 0);
                parcelObtain2.readException();
                parcelObtain.recycle();
            } catch (Exception e2) {
                e2.fillInStackTrace();
                Log.e(TAG, "updateAurogonUidRule:" + e2);
                if (parcelObtain != null) {
                    parcelObtain.recycle();
                }
                if (parcelObtain2 == null) {
                    return;
                }
            }
            parcelObtain2.recycle();
        } catch (Throwable th) {
            if (parcelObtain != null) {
                parcelObtain.recycle();
            }
            if (parcelObtain2 != null) {
                parcelObtain2.recycle();
            }
            throw th;
        }
    }

    public List<Integer> freezePids(int[] iArr, long j2, String str) {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        ArrayList arrayList = new ArrayList();
        try {
            try {
                parcelObtain.writeInterfaceToken(mServiceName);
                parcelObtain.writeIntArray(iArr);
                parcelObtain.writeLong(j2);
                parcelObtain.writeInt(1);
                parcelObtain.writeString(str);
                this.IGreeze.transact(5, parcelObtain, parcelObtain2, 0);
                parcelObtain2.readException();
                parcelObtain2.readList(arrayList, null);
                parcelObtain.recycle();
            } catch (Exception e2) {
                e2.fillInStackTrace();
                Log.e(TAG, "freezePids:" + e2);
                if (parcelObtain != null) {
                    parcelObtain.recycle();
                }
                if (parcelObtain2 != null) {
                }
                return arrayList;
            }
            parcelObtain2.recycle();
            return arrayList;
        } catch (Throwable th) {
            if (parcelObtain != null) {
                parcelObtain.recycle();
            }
            if (parcelObtain2 != null) {
                parcelObtain2.recycle();
            }
            throw th;
        }
    }

    /* JADX WARN: Code duplicated, block: B:16:0x006c  */
    /* JADX WARN: Code duplicated, block: B:18:0x007a  */
    /* JADX WARN: Code duplicated, block: B:21:0x0097 A[LOOP:0: B:19:0x0091->B:21:0x0097, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:24:0x00a4 A[LOOP:1: B:23:0x00a2->B:24:0x00a4, LOOP_END] */
    public List<Integer> freezeUids(int[] iArr, long j2, String str, boolean z2) {
        ArrayList arrayList;
        int[] array;
        Iterator<PowerKeeperInterface.u> it;
        ArrayList arrayList2 = new ArrayList();
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            try {
                parcelObtain.writeInterfaceToken(mServiceName);
                parcelObtain.writeIntArray(iArr);
                parcelObtain.writeLong(j2);
                parcelObtain.writeInt(1);
                parcelObtain.writeString(str);
                parcelObtain.writeBoolean(z2);
                this.IGreeze.transact(6, parcelObtain, parcelObtain2, 0);
                parcelObtain2.readException();
                parcelObtain2.readList(arrayList2, null);
                HashSet hashSet = new HashSet(arrayList2);
                arrayList2.clear();
                arrayList2.addAll(hashSet);
                parcelObtain.recycle();
            } catch (Exception e2) {
                e2.fillInStackTrace();
                Log.e(TAG, "freezeUids:" + e2);
                if (parcelObtain != null) {
                    parcelObtain.recycle();
                }
                if (parcelObtain2 != null) {
                }
                if (arrayList2.size() > 0) {
                    arrayList = new ArrayList();
                    arrayList.addAll(arrayList2);
                    if (arrayList.size() > 0) {
                        array = arrayList.stream().mapToInt(new com.miui.powerkeeper.c()).toArray();
                        it = this.listeners.iterator();
                        while (it.hasNext()) {
                            it.next().a(array);
                        }
                        for (int i2 : array) {
                            resourceControl(i2, true);
                        }
                    }
                }
                return arrayList2;
            }
            parcelObtain2.recycle();
            if (arrayList2.size() > 0) {
                arrayList = new ArrayList();
                arrayList.addAll(arrayList2);
                if (arrayList.size() > 0) {
                    array = arrayList.stream().mapToInt(new com.miui.powerkeeper.c()).toArray();
                    it = this.listeners.iterator();
                    while (it.hasNext()) {
                        it.next().a(array);
                    }
                    while (i < r8) {
                        resourceControl(i2, true);
                    }
                }
            }
            return arrayList2;
        } catch (Throwable th) {
            if (parcelObtain != null) {
                parcelObtain.recycle();
            }
            if (parcelObtain2 != null) {
                parcelObtain2.recycle();
            }
            throw th;
        }
    }

    public int[] getFrozenPids(int i2) {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        int[] iArrCreateIntArray = new int[0];
        try {
            try {
                parcelObtain.writeInterfaceToken(mServiceName);
                if (i2 != 0) {
                    parcelObtain.writeInt(1);
                } else {
                    parcelObtain.writeInt(i2);
                }
                this.IGreeze.transact(10, parcelObtain, parcelObtain2, 0);
                parcelObtain2.readException();
                iArrCreateIntArray = parcelObtain2.createIntArray();
                parcelObtain.recycle();
            } catch (Exception e2) {
                e2.fillInStackTrace();
                Log.e(TAG, "getFrozenPids:" + e2);
                if (parcelObtain != null) {
                    parcelObtain.recycle();
                }
                if (parcelObtain2 != null) {
                }
                return iArrCreateIntArray;
            }
            parcelObtain2.recycle();
            return iArrCreateIntArray;
        } catch (Throwable th) {
            if (parcelObtain != null) {
                parcelObtain.recycle();
            }
            if (parcelObtain2 != null) {
                parcelObtain2.recycle();
            }
            throw th;
        }
    }

    public Set<Integer> getFrozenUidsAll() {
        ArraySet arraySet = new ArraySet();
        for (int i2 : getFrozenUids(GREEZER_MODULE_ALL)) {
            arraySet.add(Integer.valueOf(i2));
        }
        return arraySet;
    }

    public Set<Integer> getFrozenUidsPower() {
        ArraySet arraySet = new ArraySet();
        for (int i2 : getFrozenUids(1)) {
            arraySet.add(Integer.valueOf(i2));
        }
        return arraySet;
    }

    public int[] getFrozenUidsPowerArray() {
        return getFrozenUids(1);
    }

    public int[] getImportantApps() {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        int[] iArrCreateIntArray = new int[0];
        try {
            try {
                parcelObtain.writeInterfaceToken(mServiceName);
                this.IGreeze.transact(22, parcelObtain, parcelObtain2, 0);
                parcelObtain2.readException();
                iArrCreateIntArray = parcelObtain2.createIntArray();
                parcelObtain.recycle();
            } catch (Exception e2) {
                e2.fillInStackTrace();
                Log.e(TAG, "getImportantApps:" + e2);
                if (parcelObtain != null) {
                    parcelObtain.recycle();
                }
                if (parcelObtain2 != null) {
                }
                return iArrCreateIntArray;
            }
            parcelObtain2.recycle();
            return iArrCreateIntArray;
        } catch (Throwable th) {
            if (parcelObtain != null) {
                parcelObtain.recycle();
            }
            if (parcelObtain2 != null) {
                parcelObtain2.recycle();
            }
            throw th;
        }
    }

    public boolean hasGreeze() {
        return this.IGreeze != null;
    }

    public boolean isUidActive(int i2) {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        boolean z2 = true;
        try {
            try {
                parcelObtain.writeInterfaceToken(mServiceName);
                parcelObtain.writeInt(i2);
                this.IGreeze.transact(11, parcelObtain, parcelObtain2, 0);
                parcelObtain2.readException();
                z2 = parcelObtain2.readBoolean();
                if (z2) {
                    Log.d(TAG, "isUidActive true uid=" + i2);
                }
                parcelObtain.recycle();
            } catch (Exception e2) {
                e2.fillInStackTrace();
                Log.e(TAG, "isUidActive:" + e2);
                if (parcelObtain != null) {
                    parcelObtain.recycle();
                }
                if (parcelObtain2 != null) {
                }
                return z2;
            }
            parcelObtain2.recycle();
            return z2;
        } catch (Throwable th) {
            if (parcelObtain != null) {
                parcelObtain.recycle();
            }
            if (parcelObtain2 != null) {
                parcelObtain2.recycle();
            }
            throw th;
        }
    }

    public boolean isUidFrozen(int i2) {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        boolean z2 = false;
        try {
            try {
                parcelObtain.writeInterfaceToken(mServiceName);
                parcelObtain.writeInt(i2);
                this.IGreeze.transact(17, parcelObtain, parcelObtain2, 0);
                parcelObtain2.readException();
                z2 = parcelObtain2.readBoolean();
                parcelObtain.recycle();
            } catch (Exception e2) {
                e2.fillInStackTrace();
                Log.e(TAG, "isUidFrozen:" + e2);
                if (parcelObtain != null) {
                    parcelObtain.recycle();
                }
                if (parcelObtain2 != null) {
                }
                return z2;
            }
            parcelObtain2.recycle();
            return z2;
        } catch (Throwable th) {
            if (parcelObtain != null) {
                parcelObtain.recycle();
            }
            if (parcelObtain2 != null) {
                parcelObtain2.recycle();
            }
            throw th;
        }
    }

    public boolean registerCallback(IGreezeCallback iGreezeCallback) {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        Boolean boolValueOf = Boolean.FALSE;
        try {
            try {
                parcelObtain.writeInterfaceToken(mServiceName);
                parcelObtain.writeStrongInterface(iGreezeCallback);
                parcelObtain.writeInt(1);
                this.IGreeze.transact(15, parcelObtain, parcelObtain2, 0);
                parcelObtain2.readException();
                boolValueOf = Boolean.valueOf(parcelObtain2.readBoolean());
                Log.d(TAG, "registerCallback ret =" + boolValueOf);
                parcelObtain.recycle();
            } catch (Exception e2) {
                e2.fillInStackTrace();
                Log.e(TAG, "millet registerCallback:" + e2);
                if (parcelObtain != null) {
                    parcelObtain.recycle();
                }
                if (parcelObtain2 != null) {
                }
                return boolValueOf.booleanValue();
            }
            parcelObtain2.recycle();
            return boolValueOf.booleanValue();
        } catch (Throwable th) {
            if (parcelObtain != null) {
                parcelObtain.recycle();
            }
            if (parcelObtain2 != null) {
                parcelObtain2.recycle();
            }
            throw th;
        }
    }

    public void registerFreezeListener(PowerKeeperInterface.u uVar) {
        this.listeners.add(uVar);
    }

    public boolean thawAll(String str, boolean z2) {
        thawUids(getFrozenUidsPowerArray(), str);
        if (!z2) {
            Iterator<PowerKeeperInterface.u> it = this.listeners.iterator();
            while (it.hasNext()) {
                it.next().b(str);
            }
        }
        if (!Build.IS_DEBUGGABLE) {
            return false;
        }
        Log.d(TAG, "thawAll");
        return false;
    }

    public List<Integer> thawPids(int[] iArr, String str) {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        ArrayList arrayList = new ArrayList();
        try {
            try {
                parcelObtain.writeInterfaceToken(mServiceName);
                parcelObtain.writeIntArray(iArr);
                parcelObtain.writeInt(1);
                parcelObtain.writeString(str);
                this.IGreeze.transact(7, parcelObtain, parcelObtain2, 0);
                parcelObtain2.readException();
                parcelObtain2.readList(arrayList, null);
                parcelObtain.recycle();
            } catch (Exception e2) {
                e2.fillInStackTrace();
                Log.e(TAG, "thawPids:" + e2);
                if (parcelObtain != null) {
                    parcelObtain.recycle();
                }
                if (parcelObtain2 != null) {
                }
                return arrayList;
            }
            parcelObtain2.recycle();
            return arrayList;
        } catch (Throwable th) {
            if (parcelObtain != null) {
                parcelObtain.recycle();
            }
            if (parcelObtain2 != null) {
                parcelObtain2.recycle();
            }
            throw th;
        }
    }

    /* JADX WARN: Code duplicated, block: B:17:0x006b A[LOOP:0: B:15:0x0065->B:17:0x006b, LOOP_END] */
    public List<Integer> thawUids(int[] iArr, String str) {
        Iterator it;
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        ArrayList arrayList = new ArrayList();
        try {
            try {
                parcelObtain.writeInterfaceToken(mServiceName);
                parcelObtain.writeIntArray(iArr);
                parcelObtain.writeInt(1);
                parcelObtain.writeString(str);
                this.IGreeze.transact(8, parcelObtain, parcelObtain2, 0);
                parcelObtain2.readException();
                parcelObtain2.readList(arrayList, null);
                HashSet hashSet = new HashSet(arrayList);
                arrayList.clear();
                arrayList.addAll(hashSet);
                parcelObtain.recycle();
            } catch (Exception e2) {
                e2.fillInStackTrace();
                Log.e(TAG, "thawUids:" + e2);
                if (parcelObtain != null) {
                    parcelObtain.recycle();
                }
                if (parcelObtain2 != null) {
                }
                it = arrayList.iterator();
                while (it.hasNext()) {
                    resourceControl(((Integer) it.next()).intValue(), false);
                }
                return arrayList;
            }
            parcelObtain2.recycle();
            it = arrayList.iterator();
            while (it.hasNext()) {
                resourceControl(((Integer) it.next()).intValue(), false);
            }
            return arrayList;
        } catch (Throwable th) {
            if (parcelObtain != null) {
                parcelObtain.recycle();
            }
            if (parcelObtain2 != null) {
                parcelObtain2.recycle();
            }
            throw th;
        }
    }

    public void unregisterFreezeListener(PowerKeeperInterface.u uVar) {
        this.listeners.remove(uVar);
    }
}
