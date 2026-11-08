package com.miui.powerkeeper.batterylife;

import android.annotation.NonNull;
import android.annotation.Nullable;
import android.content.Context;
import android.content.IntentFilter;
import android.os.BatteryStats;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import com.android.internal.app.IBatteryStats;
import com.android.internal.os.BackgroundThread;
import com.android.internal.os.PowerProfile;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.batterylife.data.AppScreenPowerData;
import com.miui.powerkeeper.batterylife.data.ScreenPowerProfile;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.logsystem.utils.GsonUtils;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.utils.AssetUtils;
import com.xiaomi.push.service.PushServiceConstants;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import miui.os.Build;
import miui.process.ForegroundInfo;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class ProcScreenPowerManager {
    private static final double MILLIS_IN_HOUR = 3600000.0d;
    private static final String TAG = "ProcScreenPowerManager";
    private static final double ZERO = 1.0E-6d;
    private boolean isSupprotAllPkgs;
    private IBatteryStats mBatteryStats;
    private Context mContext;
    private Handler mHandler;
    private boolean mOnBattery;
    private double mScreenOnMah;
    private String[] mSupportPkgs;
    private ArrayMap<String, AppScreenPowerData> mLastAppScreenPowerMap = new ArrayMap<>();
    private ArrayMap<String, AppScreenPowerData> mTotalAppScreenPowerMap = new ArrayMap<>();
    private List<ScreenPowerProfile> mAppScreenPowerProfiles = new ArrayList();

    public ProcScreenPowerManager(Context context) {
        this.mContext = context;
        int intExtra = this.mContext.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED")).getIntExtra("status", -1);
        this.mOnBattery = (intExtra == 2 || intExtra == 5) ? false : true;
        this.mBatteryStats = IBatteryStats.Stub.asInterface(ServiceManager.getService("batterystats"));
        this.mScreenOnMah = new PowerProfile(context).getAveragePower("screen.on");
        this.isSupprotAllPkgs = false;
        String string = SimpleSettings.Misc.getString(this.mContext, BLUtils.CLOUD_PROC_CPU_TIME_IN_STATE, "");
        if (string.length() <= 0) {
            return;
        }
        try {
            JSONObject jSONObject = new JSONObject(string);
            if (jSONObject.has("pkgList_all")) {
                String strOptString = jSONObject.optString("pkgList_all");
                Log.d(TAG, "pkgList1:" + strOptString);
                if (strOptString.length() > 0 && PushServiceConstants.EXTENSION_ELE_NAME_ALL.equalsIgnoreCase(strOptString)) {
                    this.mSupportPkgs = null;
                    this.isSupprotAllPkgs = true;
                }
            } else {
                String strOptString2 = Build.IS_INTERNATIONAL_BUILD ? jSONObject.optString("pkgList_intl") : jSONObject.optString("pkgList");
                Log.d(TAG, "pkgList2:" + strOptString2);
                if (strOptString2.length() > 0) {
                    if ("none".equalsIgnoreCase(strOptString2)) {
                        this.mSupportPkgs = null;
                    } else {
                        this.mSupportPkgs = strOptString2.split(";");
                    }
                }
            }
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
        this.mHandler = new Handler(BackgroundThread.getHandler().getLooper()) { // from class: com.miui.powerkeeper.batterylife.ProcScreenPowerManager.1
            @Override // android.os.Handler
            public void handleMessage(@NonNull Message message) {
                ProcScreenPowerManager.this.handleMessage(message);
            }
        };
        EventsAggregator eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
        if (eventsAggregator != null) {
            eventsAggregator.registerForForegroundInfo(this.mHandler);
            eventsAggregator.registerForScreenOn(this.mHandler, null);
        }
    }

    private AppScreenPowerData addDeltaData(AppScreenPowerData appScreenPowerData, AppScreenPowerData appScreenPowerData2) {
        appScreenPowerData.powerMah += appScreenPowerData2.powerMah;
        appScreenPowerData.fgTimeMs += appScreenPowerData2.fgTimeMs;
        for (int i2 = 0; i2 < 100; i2++) {
            long[] jArr = appScreenPowerData.brightnessBinTimes;
            long j2 = jArr[i2];
            long[] jArr2 = appScreenPowerData2.brightnessBinTimes;
            jArr[i2] = j2 + jArr2[i2];
            appScreenPowerData.brightnessBinTotalTimes += jArr2[i2];
        }
        return appScreenPowerData;
    }

    private AppScreenPowerData calcDeltaData(AppScreenPowerData appScreenPowerData, AppScreenPowerData appScreenPowerData2) {
        if (!TextUtils.equals(appScreenPowerData.blPkgName, appScreenPowerData2.blPkgName)) {
            return null;
        }
        AppScreenPowerData appScreenPowerData3 = new AppScreenPowerData(appScreenPowerData.blPkgName);
        appScreenPowerData3.powerMah = appScreenPowerData.powerMah - appScreenPowerData2.powerMah;
        appScreenPowerData3.fgTimeMs = appScreenPowerData.fgTimeMs - appScreenPowerData2.fgTimeMs;
        for (int i2 = 0; i2 < 100; i2++) {
            long[] jArr = appScreenPowerData3.brightnessBinTimes;
            long j2 = appScreenPowerData.brightnessBinTimes[i2] - appScreenPowerData2.brightnessBinTimes[i2];
            jArr[i2] = j2;
            appScreenPowerData3.brightnessBinTotalTimes += j2;
        }
        return appScreenPowerData3;
    }

    private double calculateAppsBinPower(String str, long j2, long[] jArr) {
        ScreenPowerProfile screenPowerProfile;
        double d2;
        long j3;
        int i2 = 0;
        while (true) {
            if (i2 >= this.mAppScreenPowerProfiles.size()) {
                screenPowerProfile = null;
                break;
            }
            if (this.mAppScreenPowerProfiles.get(i2) != null && TextUtils.equals(this.mAppScreenPowerProfiles.get(i2).pkgName, str)) {
                screenPowerProfile = this.mAppScreenPowerProfiles.get(i2);
                break;
            }
            i2++;
        }
        if (screenPowerProfile == null) {
            return 0.0d;
        }
        if (Math.abs(screenPowerProfile.screenFullMah) <= ZERO && Math.abs(screenPowerProfile.screenHbm) <= ZERO) {
            return 0.0d;
        }
        double d3 = ((this.mScreenOnMah * j2) / MILLIS_IN_HOUR) + 0.0d;
        for (int i3 = 0; i3 < 100; i3++) {
            if (i3 <= 50) {
                double d4 = screenPowerProfile.screenFullMah;
                double d5 = screenPowerProfile.screenLowMah;
                d2 = (((d4 - d5) * ((double) i3)) / 50.0d) + d5;
                j3 = jArr[i3];
            } else {
                double d6 = screenPowerProfile.screenHbm;
                double d7 = screenPowerProfile.screenFullMah;
                d2 = (((d6 - d7) * ((double) (i3 - 50))) / 49.0d) + d7;
                j3 = jArr[i3];
            }
            double d8 = (d2 * j3) / MILLIS_IN_HOUR;
            if (android.os.Build.IS_DEBUGGABLE && d8 != 0.0d) {
                Log.d(TAG, "Screen bin #" + i3 + ": time=" + jArr[i3] + " power=" + BatteryStats.formatCharge(d8));
            }
            d3 += d8;
        }
        return d3;
    }

    @Nullable
    private AppScreenPowerData getCurrentAppScreenInfo(String str) {
        Parcel parcelObtain = Parcel.obtain();
        try {
            byte[] targetScreenInfo = this.mBatteryStats.getTargetScreenInfo(str);
            if (targetScreenInfo == null) {
                return null;
            }
            AppScreenPowerData appScreenPowerData = new AppScreenPowerData(str);
            parcelObtain.unmarshall(targetScreenInfo, 0, targetScreenInfo.length);
            parcelObtain.setDataPosition(0);
            if (parcelObtain.readInt() != 0 && parcelObtain.readInt() != 0) {
                appScreenPowerData.fgTimeMs = parcelObtain.readLong();
                parcelObtain.readLongArray(appScreenPowerData.brightnessBinTimes);
                appScreenPowerData.powerMah = calculateAppsBinPower(str, appScreenPowerData.fgTimeMs, appScreenPowerData.brightnessBinTimes);
                for (int i2 = 0; i2 < 100; i2++) {
                    appScreenPowerData.brightnessBinTotalTimes += appScreenPowerData.brightnessBinTimes[i2];
                }
            }
            if (android.os.Build.IS_DEBUGGABLE) {
                Log.d(TAG, appScreenPowerData.toString());
            }
            return appScreenPowerData;
        } catch (Exception e2) {
            Log.e(TAG, e2.getMessage());
            return null;
        } finally {
            parcelObtain.recycle();
        }
    }

    @Nullable
    private AppScreenPowerData getCurrentDyScreenPower(String str) {
        try {
            byte[] dyAppScreenPower = this.mBatteryStats.getDyAppScreenPower();
            if (dyAppScreenPower == null) {
                return null;
            }
            AppScreenPowerData appScreenPowerData = new AppScreenPowerData(str);
            Parcel parcelObtain = Parcel.obtain();
            try {
                int i2 = 0;
                parcelObtain.unmarshall(dyAppScreenPower, 0, dyAppScreenPower.length);
                parcelObtain.setDataPosition(0);
                appScreenPowerData.powerMah = parcelObtain.readDouble();
                appScreenPowerData.fgTimeMs = parcelObtain.readLong();
                parcelObtain.readLongArray(appScreenPowerData.brightnessBinTimes);
                while (true) {
                    long[] jArr = appScreenPowerData.brightnessBinTimes;
                    if (i2 >= jArr.length) {
                        break;
                    }
                    long j2 = jArr[i2] / 1000;
                    jArr[i2] = j2;
                    appScreenPowerData.brightnessBinTotalTimes += j2;
                    i2++;
                }
            } catch (Exception e2) {
                Log.e(TAG, e2.getMessage());
            } finally {
                parcelObtain.recycle();
            }
            return appScreenPowerData;
        } catch (RemoteException | NullPointerException e3) {
            Log.e(TAG, e3.getMessage());
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleMessage(Message message) {
        EventsAggregator eventsAggregator;
        int i2 = message.what;
        if (i2 == -9) {
            ForegroundInfo foregroundInfo = (ForegroundInfo) message.obj;
            initPkgsData(foregroundInfo.mForegroundUid, foregroundInfo.mForegroundPackageName);
        } else {
            if (i2 != -4 || (eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator()) == null) {
                return;
            }
            ForegroundInfo foregroundInfo2 = eventsAggregator.getForegroundInfo();
            initPkgsData(foregroundInfo2.mForegroundUid, foregroundInfo2.mForegroundPackageName);
        }
    }

    private void initPowerData() {
        JsonObject jsonObject;
        JsonArray asJsonArray;
        String strFromAssetFile = AssetUtils.getStrFromAssetFile(this.mContext, "screen_power_profile.json");
        if (TextUtils.isEmpty(strFromAssetFile) || (jsonObject = (JsonObject) GsonUtils.GsonToBeanSafety(strFromAssetFile, JsonObject.class)) == null || (asJsonArray = jsonObject.get("pkg").getAsJsonArray()) == null) {
            return;
        }
        this.mAppScreenPowerProfiles = GsonUtils.GsonToList(asJsonArray, ScreenPowerProfile.class);
    }

    private void initSupportPkgsData(String[] strArr) {
        if (strArr != null) {
            for (String str : strArr) {
                this.mTotalAppScreenPowerMap.put(str, new AppScreenPowerData(str));
                AppScreenPowerData currentAppScreenInfo = getCurrentAppScreenInfo(str);
                if (currentAppScreenInfo != null) {
                    this.mLastAppScreenPowerMap.put(str, currentAppScreenInfo);
                } else {
                    this.mLastAppScreenPowerMap.put(str, new AppScreenPowerData(str));
                }
                initPowerData();
            }
        }
    }

    private boolean isAppSupport(int i2, String str) {
        if (i2 < 10000) {
            return false;
        }
        String[] strArr = this.mSupportPkgs;
        if (strArr != null) {
            for (String str2 : strArr) {
                if (str2.equals(str)) {
                    return true;
                }
            }
        } else if (this.isSupprotAllPkgs) {
            return true;
        }
        return false;
    }

    private void resetLastScreenPower(String str) {
        this.mLastAppScreenPowerMap.put(str, new AppScreenPowerData(str));
    }

    void destroy() {
        ArrayMap<String, AppScreenPowerData> arrayMap = this.mTotalAppScreenPowerMap;
        if (arrayMap != null) {
            arrayMap.clear();
            this.mTotalAppScreenPowerMap = null;
        }
        ArrayMap<String, AppScreenPowerData> arrayMap2 = this.mLastAppScreenPowerMap;
        if (arrayMap2 != null) {
            arrayMap2.clear();
            this.mLastAppScreenPowerMap = null;
        }
        EventsAggregator eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
        if (eventsAggregator != null) {
            eventsAggregator.unregisterForForegroundInfo(this.mHandler);
            eventsAggregator.unregisterForScreenOn(this.mHandler);
        }
    }

    public List<AppScreenPowerData> doUploadAppScreenPower() {
        ArrayList arrayList = new ArrayList();
        if (this.isSupprotAllPkgs || this.mSupportPkgs != null) {
            for (int i2 = 0; i2 < this.mTotalAppScreenPowerMap.size(); i2++) {
                String strKeyAt = this.mTotalAppScreenPowerMap.keyAt(i2);
                AppScreenPowerData currentAppScreenInfo = getCurrentAppScreenInfo(strKeyAt);
                AppScreenPowerData appScreenPowerData = this.mTotalAppScreenPowerMap.get(strKeyAt);
                AppScreenPowerData appScreenPowerData2 = this.mLastAppScreenPowerMap.get(strKeyAt);
                if (currentAppScreenInfo != null && appScreenPowerData != null && appScreenPowerData2 != null) {
                    long j2 = currentAppScreenInfo.fgTimeMs;
                    long j3 = appScreenPowerData2.fgTimeMs;
                    if (j2 > j3) {
                        AppScreenPowerData appScreenPowerDataCalcDeltaData = calcDeltaData(currentAppScreenInfo, appScreenPowerData2);
                        if (appScreenPowerDataCalcDeltaData != null) {
                            addDeltaData(appScreenPowerData, appScreenPowerDataCalcDeltaData);
                            this.mTotalAppScreenPowerMap.put(strKeyAt, appScreenPowerData);
                            arrayList.add(appScreenPowerData);
                        }
                        if (android.os.Build.IS_DEBUGGABLE) {
                            Log.d(TAG, "current powerMah=" + currentAppScreenInfo.powerMah + " fgTimeMs=" + currentAppScreenInfo.fgTimeMs);
                        }
                    } else if (j2 == j3) {
                        arrayList.add(appScreenPowerData);
                    }
                    this.mLastAppScreenPowerMap.put(strKeyAt, currentAppScreenInfo);
                }
            }
        }
        return arrayList;
    }

    void dump(PrintWriter printWriter) {
        if (this.isSupprotAllPkgs || this.mSupportPkgs != null) {
            printWriter.println("Screen stats for Pkgs:");
            List<AppScreenPowerData> listDoUploadAppScreenPower = doUploadAppScreenPower();
            for (int i2 = 0; i2 < listDoUploadAppScreenPower.size(); i2++) {
                AppScreenPowerData appScreenPowerData = listDoUploadAppScreenPower.get(i2);
                printWriter.println("screen power " + appScreenPowerData.blPkgName);
                printWriter.print("  powerMah= ");
                printWriter.print(appScreenPowerData.powerMah);
                printWriter.print("  fgTimeMs= ");
                printWriter.print(appScreenPowerData.fgTimeMs);
                printWriter.print("  brightnessBin= ");
                printWriter.print(Arrays.toString(appScreenPowerData.brightnessBinTimes));
                printWriter.print("  brightnessBinTotalTimes= ");
                printWriter.println(appScreenPowerData.brightnessBinTotalTimes);
            }
            printWriter.println("mTotalAppScreenPowerMap:");
            for (int i3 = 0; i3 < this.mTotalAppScreenPowerMap.size(); i3++) {
                printWriter.println("pkg name:" + this.mTotalAppScreenPowerMap.keyAt(i3));
                AppScreenPowerData appScreenPowerDataValueAt = this.mTotalAppScreenPowerMap.valueAt(i3);
                printWriter.println("screen power " + appScreenPowerDataValueAt.blPkgName);
                printWriter.print("  powerMah= ");
                printWriter.print(appScreenPowerDataValueAt.powerMah);
                printWriter.print("  fgTimeMs= ");
                printWriter.print(appScreenPowerDataValueAt.fgTimeMs);
                printWriter.print("\n\n");
            }
        }
    }

    public String[] getSupportPkgs() {
        return this.mSupportPkgs;
    }

    public ArrayMap<String, AppScreenPowerData> getTotalAppScreenPower() {
        return this.mTotalAppScreenPowerMap;
    }

    public void initPkgsData(int i2, String str) {
        if (isAppSupport(i2, str)) {
            if (this.mTotalAppScreenPowerMap.get(str) == null) {
                Log.d(TAG, "totalAppScreenPowerData is null");
                this.mTotalAppScreenPowerMap.put(str, new AppScreenPowerData(str));
            }
            if (this.mLastAppScreenPowerMap.get(str) == null) {
                Log.d(TAG, "lastAppScreenPowerData is null");
                AppScreenPowerData currentAppScreenInfo = getCurrentAppScreenInfo(str);
                if (currentAppScreenInfo != null) {
                    this.mLastAppScreenPowerMap.put(str, currentAppScreenInfo);
                } else {
                    this.mLastAppScreenPowerMap.put(str, new AppScreenPowerData(str));
                }
            }
        }
    }

    public void onBatteryChanged(boolean z2) {
        AppScreenPowerData appScreenPowerDataCalcDeltaData;
        if (z2 == this.mOnBattery) {
            return;
        }
        if (this.isSupprotAllPkgs || this.mSupportPkgs != null) {
            for (int i2 = 0; i2 < this.mTotalAppScreenPowerMap.size(); i2++) {
                String strKeyAt = this.mTotalAppScreenPowerMap.keyAt(i2);
                AppScreenPowerData currentAppScreenInfo = getCurrentAppScreenInfo(strKeyAt);
                AppScreenPowerData appScreenPowerData = this.mTotalAppScreenPowerMap.get(strKeyAt);
                AppScreenPowerData appScreenPowerData2 = this.mLastAppScreenPowerMap.get(strKeyAt);
                if (currentAppScreenInfo != null && appScreenPowerData != null && appScreenPowerData2 != null) {
                    if (z2) {
                        long j2 = currentAppScreenInfo.fgTimeMs;
                        long j3 = appScreenPowerData2.fgTimeMs;
                        if (j2 < j3) {
                            this.mTotalAppScreenPowerMap.put(strKeyAt, addDeltaData(appScreenPowerData, currentAppScreenInfo));
                            resetLastScreenPower(strKeyAt);
                        } else if (j2 > j3) {
                            AppScreenPowerData appScreenPowerDataCalcDeltaData2 = calcDeltaData(currentAppScreenInfo, appScreenPowerData2);
                            if (appScreenPowerDataCalcDeltaData2 != null) {
                                this.mTotalAppScreenPowerMap.put(strKeyAt, addDeltaData(appScreenPowerData, appScreenPowerDataCalcDeltaData2));
                            }
                            this.mLastAppScreenPowerMap.put(strKeyAt, currentAppScreenInfo);
                        }
                    } else {
                        if (currentAppScreenInfo.fgTimeMs > appScreenPowerData2.fgTimeMs && (appScreenPowerDataCalcDeltaData = calcDeltaData(currentAppScreenInfo, appScreenPowerData2)) != null) {
                            this.mTotalAppScreenPowerMap.put(strKeyAt, addDeltaData(appScreenPowerData, appScreenPowerDataCalcDeltaData));
                        }
                        this.mLastAppScreenPowerMap.put(strKeyAt, currentAppScreenInfo);
                    }
                }
            }
        }
        this.mOnBattery = z2;
    }

    public void resetAllData() {
        if (this.isSupprotAllPkgs || this.mSupportPkgs != null) {
            ArrayMap<String, AppScreenPowerData> arrayMap = this.mTotalAppScreenPowerMap;
            if (arrayMap != null) {
                arrayMap.clear();
            }
            ArrayMap<String, AppScreenPowerData> arrayMap2 = this.mLastAppScreenPowerMap;
            if (arrayMap2 != null) {
                arrayMap2.clear();
            }
        }
    }

    public void setSupportPkgs(String[] strArr, boolean z2) {
        this.mSupportPkgs = strArr;
        this.isSupprotAllPkgs = z2;
    }
}
