package com.miui.powerkeeper.powerchecker;

import android.content.Context;
import android.content.Intent;
import android.util.ArrayMap;
import android.util.LocalLog;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class PowerCheckerNotificationManager {
    private static final String ABNORMAL_POWER_APP_HANDLE_ACTION = "miui.intent.action.ABNORMAL_POWER_APP_HANDLE";
    private static final boolean DEBUG = Log.isLoggable("power.checker", 3);
    public static final int LOCAL_LOG_SIZE = 30;
    private static final String NOTIFACATION_RECEIVER_PACKAGE = "com.miui.securitycenter";
    private static final String POWER_NOTIFY_ACTION = "com.miui.powerkeeper.action.POWER_NOTIFY";
    private static final String POWER_NOTIFY_PERMISSION = "com.miui.powerkeeper.permission.POWER_NOTIFY";
    private static String TAG = "PowerChecker.Notify";
    private Context mContext;
    private ArrayMap<String, Integer> mPartialWakelockNotifyList = new ArrayMap<>();
    private ArrayMap<String, Integer> mWifiScanNotifyList = new ArrayMap<>();
    private ArrayMap<String, Integer> mBackgroundGPSNotifyList = new ArrayMap<>();
    private ArrayMap<String, Integer> mBackgroundCPUNotifyList = new ArrayMap<>();
    private ArrayMap<String, Integer> mWakeupAlarmNotifyList = new ArrayMap<>();
    private ArrayMap<String, Integer> mMobileRadioNotifyList = new ArrayMap<>();
    private JSONArray jsonArrayAllAbnormalData = new JSONArray();
    private LocalLog mLocalLog = new LocalLog(30);

    public PowerCheckerNotificationManager(Context context) {
        this.mContext = context;
    }

    private void logd(String str) {
        if (DEBUG) {
            Log.d(TAG, str);
        }
        this.mLocalLog.log(str);
    }

    private void sendNotifyInfo(String str) {
        Intent intent = new Intent();
        intent.setAction(POWER_NOTIFY_ACTION);
        intent.putExtra("notify_info", str);
        logd("send notify info to SecurityCenter notify_info = " + str);
        this.mContext.sendBroadcast(intent, POWER_NOTIFY_PERMISSION);
    }

    private JSONObject toJson(int i2, String str) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(str, i2);
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
        return jSONObject;
    }

    public void addNotificationInfo(int i2, int i3, String str) {
        if (DEBUG) {
            Log.d(TAG, "addNotification, type = " + i2 + ", uid = " + i3 + ", pkg = " + str);
        }
        if (PowerCheckerManager.isValidType(i2)) {
            switch (i2) {
                case 1:
                    this.mPartialWakelockNotifyList.put(str, Integer.valueOf(i3));
                    break;
                case 2:
                    this.mWifiScanNotifyList.put(str, Integer.valueOf(i3));
                    break;
                case 3:
                    this.mBackgroundGPSNotifyList.put(str, Integer.valueOf(i3));
                    break;
                case 4:
                    this.mBackgroundCPUNotifyList.put(str, Integer.valueOf(i3));
                    break;
                case 5:
                    this.mWakeupAlarmNotifyList.put(str, Integer.valueOf(i3));
                    break;
                case 6:
                    this.mMobileRadioNotifyList.put(str, Integer.valueOf(i3));
                    break;
            }
        }
    }

    public void addNotificationInfos(int i2, int i3, String str, JSONObject jSONObject) {
        if (PowerCheckerManager.isValidType(i2)) {
            if (jSONObject != null) {
                this.jsonArrayAllAbnormalData.put(jSONObject);
            }
            if (DEBUG) {
                Log.d(TAG, "addNotifications, json = " + jSONObject);
            }
        }
    }

    public void clearNotifyInfoList() {
        this.mPartialWakelockNotifyList.clear();
        this.mWifiScanNotifyList.clear();
        this.mBackgroundGPSNotifyList.clear();
        this.mBackgroundCPUNotifyList.clear();
        this.mWakeupAlarmNotifyList.clear();
        this.mMobileRadioNotifyList.clear();
        this.jsonArrayAllAbnormalData = new JSONArray();
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("#######dump PowerChecker Notify#######");
        this.mLocalLog.dump(fileDescriptor, printWriter, strArr);
        printWriter.println("end#######dump PowerChecker Notify#######end");
    }

    public boolean removeNotification(int i2, int i3, String str) {
        if (!DEBUG) {
            return true;
        }
        Log.d(TAG, "removeNotification, type = " + i2 + ", uid = " + i3 + ", pkg = " + str);
        return true;
    }

    public void updateNotification() {
        JSONArray jSONArray = new JSONArray();
        int size = this.mPartialWakelockNotifyList.size();
        int size2 = this.mBackgroundCPUNotifyList.size();
        int size3 = this.mWifiScanNotifyList.size();
        int size4 = this.mBackgroundGPSNotifyList.size();
        int size5 = this.mWakeupAlarmNotifyList.size();
        int size6 = this.mMobileRadioNotifyList.size();
        if (size > 0 || size2 > 0 || size3 > 0 || size4 > 0 || size5 > 0 || size6 > 0) {
            if (size > 0) {
                for (int i2 = 0; i2 < this.mPartialWakelockNotifyList.size(); i2++) {
                    jSONArray.put(toJson(1, this.mPartialWakelockNotifyList.keyAt(i2)));
                }
            }
            if (size2 > 0) {
                for (int i3 = 0; i3 < this.mBackgroundCPUNotifyList.size(); i3++) {
                    jSONArray.put(toJson(4, this.mBackgroundCPUNotifyList.keyAt(i3)));
                }
            }
            if (size3 > 0) {
                for (int i4 = 0; i4 < this.mWifiScanNotifyList.size(); i4++) {
                    jSONArray.put(toJson(2, this.mWifiScanNotifyList.keyAt(i4)));
                }
            }
            if (size4 > 0) {
                for (int i5 = 0; i5 < this.mBackgroundGPSNotifyList.size(); i5++) {
                    jSONArray.put(toJson(3, this.mBackgroundGPSNotifyList.keyAt(i5)));
                }
            }
            if (size5 > 0) {
                for (int i6 = 0; i6 < this.mWakeupAlarmNotifyList.size(); i6++) {
                    jSONArray.put(toJson(5, this.mWakeupAlarmNotifyList.keyAt(i6)));
                }
            }
            if (size6 > 0) {
                for (int i7 = 0; i7 < this.mMobileRadioNotifyList.size(); i7++) {
                    jSONArray.put(toJson(6, this.mMobileRadioNotifyList.keyAt(i7)));
                }
            }
            if (DEBUG) {
                Log.d(TAG, "jsonArray.toString is:" + jSONArray.toString());
            }
            if (!"[]".equals(jSONArray.toString()) && jSONArray.length() > 0) {
                sendNotifyInfo(jSONArray.toString());
            }
        }
        if (this.jsonArrayAllAbnormalData.length() > 0) {
            Intent intent = new Intent();
            intent.setAction(ABNORMAL_POWER_APP_HANDLE_ACTION);
            intent.setPackage(NOTIFACATION_RECEIVER_PACKAGE);
            intent.putExtra("notify_abnormal_data", this.jsonArrayAllAbnormalData.toString());
            logd("send notify all abnormal infos to SecurityCenter notify_abnormal_data =" + this.jsonArrayAllAbnormalData.toString());
            this.mContext.sendBroadcast(intent);
        }
    }
}
