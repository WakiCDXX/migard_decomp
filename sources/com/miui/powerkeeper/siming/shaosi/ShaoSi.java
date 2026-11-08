package com.miui.powerkeeper.siming.shaosi;

import android.os.Build;
import android.os.Looper;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import com.android.internal.os.BackgroundThread;
import com.miui.powerkeeper.siming.IDivision;
import com.miui.powerkeeper.siming.Subdivisions;

/* JADX INFO: loaded from: classes.dex */
public class ShaoSi {
    private static ShaoSi sInstance;
    private final Looper mLooper;
    SparseArray<S> ssList = new SparseArray<>();

    private ShaoSi(Looper looper) {
        this.mLooper = looper;
    }

    public static synchronized ShaoSi createInstance(Looper looper) {
        try {
            if (sInstance == null) {
                sInstance = new ShaoSi(looper);
            }
        } catch (Throwable th) {
            throw th;
        }
        return sInstance;
    }

    public static synchronized ShaoSi getInstance() {
        ShaoSi shaoSi = sInstance;
        if (shaoSi != null) {
            return shaoSi;
        }
        ShaoSi shaoSiCreateInstance = createInstance(BackgroundThread.getHandler().getLooper());
        sInstance = shaoSiCreateInstance;
        return shaoSiCreateInstance;
    }

    private boolean tryEnable(int i2) {
        if (i2 != 300) {
            return false;
        }
        String str = Build.DEVICE;
        if (!str.equals("haotian") && !str.equals("dada")) {
            return false;
        }
        Subdivisions.enable(i2, true);
        return true;
    }

    public IDivision getDivision(int i2) {
        S s2 = this.ssList.get(i2);
        return s2 == null ? new S(this.mLooper, i2) : s2;
    }

    public void init() {
        this.ssList.put(103, new CommonStatus(this.mLooper));
        int i2 = 0;
        while (true) {
            SparseBooleanArray sparseBooleanArray = Subdivisions.sList;
            if (i2 >= sparseBooleanArray.size()) {
                return;
            }
            initDivisions(sparseBooleanArray.keyAt(i2));
            i2++;
        }
    }

    protected void initDivisions(int i2) {
        if (i2 == 103) {
        }
        if (Subdivisions.isDisabled(i2) && !tryEnable(i2)) {
            this.ssList.put(i2, new S(this.mLooper, i2));
            return;
        }
        if (i2 == 104) {
            this.ssList.put(i2, new AppUsageN(this.mLooper));
            return;
        }
        if (i2 == 300) {
            this.ssList.put(i2, new ThemeMonitorM(this.mLooper));
            return;
        }
        switch (i2) {
            case 100:
                this.ssList.put(i2, new DeviceIdleN(this.mLooper));
                break;
            case 101:
                this.ssList.put(i2, new SubsystemN(this.mLooper));
                break;
            case 102:
                this.ssList.put(i2, new SleepModeN(this.mLooper));
                break;
        }
    }

    public void update(int i2) {
        this.ssList.delete(i2);
        initDivisions(i2);
    }
}
