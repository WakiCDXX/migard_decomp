package com.miui.powerkeeper.batterylife;

import android.content.Context;
import android.os.Parcel;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.util.ArraySet;
import android.util.Log;
import com.miui.powerkeeper.batterylife.data.AppTransactionData;
import com.miui.powerkeeper.batterylife.data.SsruSystemData;
import com.miui.powerkeeper.batterylife.data.TagTransactionData;
import com.miui.powerkeeper.utils.PowerKeeperPackageManager;
import com.miui.whetstone.server.IWhetstoneActivityManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ProcSsruTransactionManager {
    public static final String TAG = "ProcSsruTransactionManager";
    private Context mContext;
    private IWhetstoneActivityManager mWs;
    private final boolean DBG_SSRU = Log.isLoggable("power.batterylife_ssru", 3);
    private long lastTransactionTime = 0;
    private long lastSystemDataTime = 0;

    ProcSsruTransactionManager(Context context) {
        this.mContext = context;
        Log.i(TAG, "ProcSsruTransactionManager init ");
        this.mWs = IWhetstoneActivityManager.Stub.asInterface(ServiceManager.getService("whetstone.activity"));
        initWhetStoneData();
    }

    private void initWhetStoneData() {
        IWhetstoneActivityManager iWhetstoneActivityManager = this.mWs;
        if (iWhetstoneActivityManager != null) {
            try {
                iWhetstoneActivityManager.processTransactionData("clear");
                StringBuilder sb = new StringBuilder("app_list");
                for (String str : PowerKeeperPackageManager.getInstalledThirdPartApps()) {
                    sb.append(",");
                    sb.append(str);
                }
                Log.i(TAG, "stringBuilder = " + sb.toString());
                this.mWs.processTransactionData(sb.toString());
            } catch (Exception e2) {
                Log.e(TAG, e2.getMessage());
            }
        }
    }

    public void destroy() {
        Log.i(TAG, "destroy ");
        try {
            this.mWs.processTransactionData("clear");
        } catch (Exception e2) {
            Log.e(TAG, "resetAllData error" + e2);
        }
        this.mWs = null;
    }

    public String dump() {
        StringBuilder sb = new StringBuilder();
        sb.append("lastTransactionTime = " + this.lastTransactionTime + '\n');
        sb.append("lastSystemDataTime = " + this.lastSystemDataTime + '\n');
        return sb.toString();
    }

    public SsruSystemData getSystemData() {
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        SsruSystemData ssruSystemData = null;
        if (this.mWs != null) {
            Parcel parcelObtain = Parcel.obtain();
            try {
                try {
                    byte[] bArrProcessTransactionData = this.mWs.processTransactionData("");
                    if (bArrProcessTransactionData == null) {
                        parcelObtain.recycle();
                        return null;
                    }
                    parcelObtain.unmarshall(bArrProcessTransactionData, 0, bArrProcessTransactionData.length);
                    parcelObtain.setDataPosition(0);
                    if (parcelObtain.readInt() > 0) {
                        ssruSystemData = new SsruSystemData(parcelObtain.readLong(), parcelObtain.readLong(), parcelObtain.readInt(), parcelObtain.readInt(), parcelObtain.readLong(), parcelObtain.readInt(), parcelObtain.readLong(), parcelObtain.readInt(), parcelObtain.readLong(), parcelObtain.readInt(), parcelObtain.readLong(), parcelObtain.readInt(), parcelObtain.readLong(), parcelObtain.readInt(), parcelObtain.readLong(), parcelObtain.readLong());
                    }
                    parcelObtain.recycle();
                } catch (Exception e2) {
                    Log.e(TAG, e2.getMessage());
                }
            } catch (Throwable th) {
                parcelObtain.recycle();
                throw th;
            }
        }
        long jElapsedRealtime2 = SystemClock.elapsedRealtime() - jElapsedRealtime;
        if (jElapsedRealtime2 > this.lastSystemDataTime) {
            this.lastSystemDataTime = jElapsedRealtime2;
        }
        Log.i(TAG, "getSystemData time = " + this.lastSystemDataTime);
        return ssruSystemData;
    }

    public List<AppTransactionData> getTransactionData() {
        ArrayList arrayList = new ArrayList(51);
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        ArraySet<String> installedThirdPartApps = PowerKeeperPackageManager.getInstalledThirdPartApps();
        if (this.DBG_SSRU) {
            Log.i(TAG, "installedThirdPartyApps size = " + installedThirdPartApps.size());
        }
        if (this.mWs != null) {
            Parcel parcelObtain = Parcel.obtain();
            try {
                try {
                    Iterator<String> it = installedThirdPartApps.iterator();
                    while (it.hasNext()) {
                        byte[] bArrProcessTransactionData = this.mWs.processTransactionData(it.next());
                        if (bArrProcessTransactionData != null) {
                            parcelObtain.unmarshall(bArrProcessTransactionData, 0, bArrProcessTransactionData.length);
                            parcelObtain.setDataPosition(0);
                            if (parcelObtain.readInt() > 0) {
                                ArrayList arrayList2 = new ArrayList(10);
                                int i2 = parcelObtain.readInt();
                                if (i2 >= 1) {
                                    String string = parcelObtain.readString();
                                    for (int i3 = 0; i3 < i2; i3++) {
                                        arrayList2.add(new TagTransactionData(parcelObtain.readLong(), parcelObtain.readString(), parcelObtain.readLong(), parcelObtain.readLong(), parcelObtain.readInt(), parcelObtain.readLong(), parcelObtain.readInt()));
                                    }
                                    if (!string.isEmpty()) {
                                        if (this.DBG_SSRU) {
                                            Log.i(TAG, "processTransactionData pkgName = " + string + " transactionDataArrayList = " + arrayList2.toString());
                                        }
                                        arrayList.add(new AppTransactionData(string, arrayList2));
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e2) {
                    Log.e(TAG, e2.getMessage());
                }
                parcelObtain.recycle();
            } catch (Throwable th) {
                parcelObtain.recycle();
                throw th;
            }
        }
        long jElapsedRealtime2 = SystemClock.elapsedRealtime() - jElapsedRealtime;
        if (jElapsedRealtime2 > this.lastTransactionTime) {
            this.lastTransactionTime = jElapsedRealtime2;
        }
        Log.i(TAG, "processTransactionData time = " + this.lastTransactionTime + "size = " + arrayList.size());
        return arrayList;
    }

    public void resetAllData() {
        Log.i(TAG, "resetAllData ");
        initWhetStoneData();
    }
}
