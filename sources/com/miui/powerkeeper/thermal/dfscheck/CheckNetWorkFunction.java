package com.miui.powerkeeper.thermal.dfscheck;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.miui.powerkeeper.PowerKeeperApplication;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.thermal.dfsreport.ThermalDfsInterface;
import com.miui.powerkeeper.thermal.dfsreport.ThermalDfsThreadPool;
import java.util.concurrent.ExecutorService;

/* JADX INFO: loaded from: classes.dex */
public class CheckNetWorkFunction implements ThermalDfsInterface.dfsCheck {
    private static final String TAG = "powerkeeper.dfsnetwork";
    private static CheckNetWorkFunction mCheckNetWorkFunctionInstance;
    private String mNetworkType = "";
    private int mSignalStrength = -1;
    private CollectionData mCollectionData = CollectionData.getCollectionDataInstance();
    private ExecutorService mFixThreadPool = ThermalDfsThreadPool.getThermalDfsThreadPoolInstance().getFixThreadPool();

    private CheckNetWorkFunction() {
    }

    public static synchronized CheckNetWorkFunction getCheckNetWorkFunctionInstance() {
        try {
            if (mCheckNetWorkFunctionInstance == null) {
                mCheckNetWorkFunctionInstance = new CheckNetWorkFunction();
            }
        } catch (Throwable th) {
            throw th;
        }
        return mCheckNetWorkFunctionInstance;
    }

    @Override // com.miui.powerkeeper.thermal.dfsreport.ThermalDfsInterface.dfsCheck
    public boolean checkTarget() {
        Log.d(TAG, "network");
        this.mFixThreadPool.execute(new Runnable() { // from class: com.miui.powerkeeper.thermal.dfscheck.CheckNetWorkFunction.1
            @Override // java.lang.Runnable
            public void run() {
                CheckNetWorkFunction.this.mCollectionData.clearModem();
                CheckNetWorkFunction.this.mCollectionData.clearWifi();
                CheckNetWorkFunction.this.netWorkUtil(PowerKeeperApplication.b());
                CheckHightTemp.getCheckHighTempInstance().finshCallback(CheckNetWorkFunction.TAG);
                Log.d(CheckNetWorkFunction.TAG, "type " + CheckNetWorkFunction.this.mNetworkType + " signalstrength " + CheckNetWorkFunction.this.mSignalStrength);
            }
        });
        return true;
    }

    public void netWorkUtil(Context context) {
        int level;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(LogType.LOG_TYPE_CONNECTIVITY_NAME);
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        if (connectivityManager == null || telephonyManager == null) {
            Log.e(TAG, "can not get manager");
            return;
        }
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null) {
            Log.e(TAG, "not have activate net");
            return;
        }
        int type = activeNetworkInfo.getType();
        if (type == 1) {
            this.mNetworkType = "wifi";
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService("wifi");
            WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            Log.d(TAG, "wifi strength:" + connectionInfo.getRssi() + " speed:" + connectionInfo.getLinkSpeed());
            level = (int) ((((double) WifiManager.calculateSignalLevel(wifiManager.getConnectionInfo().getRssi(), 5)) / 4.0d) * 100.0d);
            this.mSignalStrength = level;
            this.mCollectionData.setWifi(level);
        } else if (type == 0) {
            if (telephonyManager.getNetworkType() == 13) {
                this.mNetworkType = "4G";
            } else if (telephonyManager.getNetworkType() == 20) {
                this.mNetworkType = "5G";
            }
            level = telephonyManager.getSignalStrength().getLevel();
            this.mSignalStrength = level;
            this.mCollectionData.setModem(level);
        } else {
            level = 0;
        }
        Log.d(TAG, "networkType: " + type);
        Log.d(TAG, "networkStrength: " + level);
    }
}
