package com.xiaomi.push.clientreport;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.channel.commonutils.android.ToolUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.misc.ScheduledJobManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class DisconnectStatsSP {
    private static final String KEY_ANDROID_VC = "android_vc";
    private static final String KEY_CONNECTED_NETWORK_TYPE = "connected_network_type";
    private static final String KEY_DISCONNECTION_TIME = "disconnect_time";
    private static final String KEY_DISCONNECT_COUNT = "discnt_count";
    private static final String KEY_DISCONNECT_COUNT_IN_24H = "discnt_count_in_day";
    private static final String KEY_HOST = "host";
    private static final String KEY_LAST_DISCONNECT_TIME = "last_discnt_time";
    private static final String KEY_NETWORK_STATE = "network_state";
    private static final String KEY_NETWORK_TYPE = "network_type";
    private static final String KEY_PING_INTERVAL = "ping_interval";
    private static final String KEY_REASON = "reason";
    private static final String KEY_RECONNECTED_COUNT = "cnt_count";
    private static final String KEY_RECONNECTION_TIME = "connected_time";
    private static final String KEY_START_TIME_FOR_24H = "start_time_for_day";
    private static final String KEY_WIFI_DIGEST = "wifi_digest";
    private static final String KEY_XMSF_VC = "xmsf_vc";
    private static final int MAX_DISCONNECTION_COUNT_FOR_UPLOAD = 10;
    private static final int MAX_DISCONNECTION_COUNT_IN_24H = 100;
    private static final int MILLIS_FOR_1Min = 60000;
    private static final int MILLIS_FOR_24H = 86400000;
    private static final String SEPARATOR = ";";
    private static final String SP_FILE_NAME = "sp_disconnect_stats";
    private static int XMSF_VC;

    private static String append(String str, String str2) {
        if (str2 == null || str2.length() == 0) {
            str2 = "null";
        }
        if (str == null || str.length() <= 0) {
            return str2;
        }
        return str + ";" + str2;
    }

    private static List<DisconnectStatsModel> buildModel(Context context) {
        SharedPreferences sp = getSp(context);
        String[] strArrSplit = split(sp.getString("host", null));
        if (strArrSplit == null || strArrSplit.length <= 0) {
            MyLog.w("DisconnectStatsSP Cached hosts data is empty,drop.");
            return null;
        }
        String[] strArrSplit2 = split(sp.getString(KEY_NETWORK_STATE, null));
        String[] strArrSplit3 = split(sp.getString(KEY_REASON, null));
        String[] strArrSplit4 = split(sp.getString(KEY_PING_INTERVAL, null));
        String[] strArrSplit5 = split(sp.getString("network_type", null));
        String[] strArrSplit6 = split(sp.getString(KEY_WIFI_DIGEST, null));
        String[] strArrSplit7 = split(sp.getString(KEY_CONNECTED_NETWORK_TYPE, null));
        String[] strArrSplit8 = split(sp.getString(KEY_DISCONNECTION_TIME, null));
        String[] strArrSplit9 = split(sp.getString(KEY_RECONNECTION_TIME, null));
        String[] strArrSplit10 = split(sp.getString("xmsf_vc", null));
        String[] strArrSplit11 = split(sp.getString(KEY_ANDROID_VC, null));
        if (strArrSplit2 == null || strArrSplit3 == null || strArrSplit4 == null || strArrSplit5 == null || strArrSplit6 == null || strArrSplit7 == null || strArrSplit8 == null || strArrSplit9 == null || strArrSplit10 == null || strArrSplit11 == null || strArrSplit.length != strArrSplit2.length || strArrSplit.length != strArrSplit3.length || strArrSplit.length != strArrSplit4.length || strArrSplit.length != strArrSplit5.length || strArrSplit.length != strArrSplit6.length || strArrSplit.length != strArrSplit7.length || strArrSplit.length != strArrSplit8.length || strArrSplit.length != strArrSplit9.length || strArrSplit.length != strArrSplit10.length || strArrSplit.length != strArrSplit11.length) {
            MyLog.w("DisconnectStatsSP Cached data incorrect,drop.");
            return null;
        }
        ArrayList arrayList = new ArrayList(strArrSplit.length);
        int i2 = 0;
        while (i2 < strArrSplit.length) {
            DisconnectStatsModel disconnectStatsModel = new DisconnectStatsModel();
            disconnectStatsModel.setCount(1);
            disconnectStatsModel.setHost(strArrSplit[i2]);
            disconnectStatsModel.setNetState(ToolUtils.parseInt(strArrSplit2[i2], -1));
            disconnectStatsModel.setReason(ToolUtils.parseInt(strArrSplit3[i2], -1));
            String[] strArr = strArrSplit2;
            String[] strArr2 = strArrSplit;
            ArrayList arrayList2 = arrayList;
            disconnectStatsModel.setPingInterval(ToolUtils.parseLong(strArrSplit4[i2], -1L));
            disconnectStatsModel.setNetType(ToolUtils.parseInt(strArrSplit5[i2], -1));
            disconnectStatsModel.setWifiDigest(strArrSplit6[i2]);
            disconnectStatsModel.setConnectedNetType(ToolUtils.parseInt(strArrSplit7[i2], -1));
            long j2 = ToolUtils.parseLong(strArrSplit8[i2], -1L);
            long j3 = ToolUtils.parseLong(strArrSplit9[i2], -1L);
            disconnectStatsModel.setDuration(j3 - j2);
            disconnectStatsModel.setDisconnectionTime(j2);
            disconnectStatsModel.setReconnectionTime(j3);
            disconnectStatsModel.setXmsfVc(ToolUtils.parseInt(strArrSplit10[i2], -1));
            disconnectStatsModel.setAndroidVc(ToolUtils.parseInt(strArrSplit11[i2], -1));
            arrayList2.add(disconnectStatsModel);
            i2++;
            strArrSplit2 = strArr;
            arrayList = arrayList2;
            strArrSplit4 = strArrSplit4;
            strArrSplit3 = strArrSplit3;
            strArrSplit = strArr2;
            strArrSplit5 = strArrSplit5;
        }
        return arrayList;
    }

    private static SharedPreferences getSp(Context context) {
        return context.getSharedPreferences(SP_FILE_NAME, 0);
    }

    private static int getXmsfVersionCode(Context context) {
        if (XMSF_VC <= 0) {
            XMSF_VC = MIUIUtils.getXmsfVersionCode(context);
        }
        return XMSF_VC;
    }

    public static void onDisconnection(final Context context, final String str, final boolean z2, final long j2, final int i2, final long j3, final int i3, final String str2, final int i4) {
        ScheduledJobManager.getInstance(context).addOneShootJob(new Runnable() { // from class: com.xiaomi.push.clientreport.DisconnectStatsSP.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    DisconnectStatsSP.recordDisconnection(context, str, z2, j2, i2, j3, i3, str2, i4);
                } catch (Exception e2) {
                    MyLog.w("DisconnectStatsSP onDisconnection exception: " + e2.getMessage());
                }
            }
        });
    }

    public static void onReconnection(final Context context, final long j2) {
        ScheduledJobManager.getInstance(context).addOneShootJob(new Runnable() { // from class: com.xiaomi.push.clientreport.DisconnectStatsSP.2
            @Override // java.lang.Runnable
            public void run() {
                try {
                    DisconnectStatsSP.recordReconnection(context, j2);
                } catch (Exception e2) {
                    MyLog.w("DisconnectStatsSP onReconnection exception: " + e2.getMessage());
                }
            }
        });
    }

    private static void recordDisconnectInfo(Context context, String str, int i2, long j2, int i3, long j3, int i4, String str2, int i5) {
        DisconnectStatsHelper.log(String.format(Locale.US, "recordDisconnectInfo host=%s, netState=%d, currentTimeMillis=%d, reason=%d, pingInterval=%d, netType=%d, wifiDigest=%s, connectedNetType=%d", str, Integer.valueOf(i2), Long.valueOf(j2), Integer.valueOf(i3), Long.valueOf(j3), Integer.valueOf(i4), str2, Integer.valueOf(i5)));
        SharedPreferences sp = getSp(context);
        String string = sp.getString("host", null);
        String string2 = sp.getString(KEY_NETWORK_STATE, null);
        String string3 = sp.getString(KEY_REASON, null);
        String string4 = sp.getString(KEY_PING_INTERVAL, null);
        String string5 = sp.getString("network_type", null);
        String string6 = sp.getString(KEY_WIFI_DIGEST, null);
        String string7 = sp.getString(KEY_CONNECTED_NETWORK_TYPE, null);
        String string8 = sp.getString(KEY_DISCONNECTION_TIME, null);
        String string9 = sp.getString("xmsf_vc", null);
        String string10 = sp.getString(KEY_ANDROID_VC, null);
        String strAppend = append(string, str);
        String strAppend2 = append(string2, i2);
        String strAppend3 = append(string3, i3);
        String strAppend4 = append(string4, j3);
        String strAppend5 = append(string5, i4);
        String strAppend6 = append(string6, str2);
        String strAppend7 = append(string7, i5);
        String strAppend8 = append(string8, j2);
        String strAppend9 = append(string9, getXmsfVersionCode(context));
        sp.edit().putString("host", strAppend).putString(KEY_NETWORK_STATE, strAppend2).putString(KEY_REASON, strAppend3).putString(KEY_PING_INTERVAL, strAppend4).putString("network_type", strAppend5).putString(KEY_WIFI_DIGEST, strAppend6).putString(KEY_CONNECTED_NETWORK_TYPE, strAppend7).putString(KEY_DISCONNECTION_TIME, strAppend8).putString("xmsf_vc", strAppend9).putString(KEY_ANDROID_VC, append(string10, Build.VERSION.SDK_INT)).apply();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static synchronized void recordDisconnection(Context context, String str, boolean z2, long j2, int i2, long j3, int i3, String str2, int i4) {
        SharedPreferences sp = getSp(context);
        long j4 = sp.getLong(KEY_START_TIME_FOR_24H, 0L);
        if (j4 == 0) {
            DisconnectStatsHelper.log("recordDisconnection not initialized");
            return;
        }
        if (j2 - sp.getLong(KEY_LAST_DISCONNECT_TIME, 0L) < 60000) {
            DisconnectStatsHelper.log("recordDisconnection anti-shake");
            return;
        }
        if (j2 - j4 < 86400000) {
            int i5 = sp.getInt(KEY_DISCONNECT_COUNT_IN_24H, 0);
            if (i5 > 100) {
                DisconnectStatsHelper.log("recordDisconnection count > 100 in 24H cycle,abandon.");
                return;
            } else {
                sp.edit().putInt(KEY_DISCONNECT_COUNT_IN_24H, i5 + 1).apply();
            }
        } else {
            DisconnectStatsHelper.log("recordDisconnection with the current time exceeds 24H cycle, go on.");
        }
        int i6 = sp.getInt(KEY_DISCONNECT_COUNT, 0);
        if (i6 == sp.getInt(KEY_RECONNECTED_COUNT, 0)) {
            recordDisconnectInfo(context, str, getNetState(z2), j2, i2, j3, i3, str2, i4);
            sp.edit().putLong(KEY_LAST_DISCONNECT_TIME, j2).putInt(KEY_DISCONNECT_COUNT, i6 + 1).apply();
        }
        DisconnectStatsHelper.log("recordDisconnection complete");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static synchronized void recordReconnection(Context context, long j2) {
        SharedPreferences sp = getSp(context);
        long j3 = sp.getLong(KEY_START_TIME_FOR_24H, 0L);
        if (j3 == 0) {
            sp.edit().putLong(KEY_START_TIME_FOR_24H, j2).putLong(KEY_LAST_DISCONNECT_TIME, 0L).putInt(KEY_DISCONNECT_COUNT_IN_24H, 0).putInt(KEY_DISCONNECT_COUNT, 0).putInt(KEY_RECONNECTED_COUNT, 0).apply();
            return;
        }
        int i2 = sp.getInt(KEY_DISCONNECT_COUNT, 0);
        int i3 = sp.getInt(KEY_RECONNECTED_COUNT, 0);
        if (i2 > i3) {
            sp.edit().putInt(KEY_RECONNECTED_COUNT, i3 + 1).putString(KEY_RECONNECTION_TIME, append(sp.getString(KEY_RECONNECTION_TIME, null), j2)).apply();
        }
        if (j2 - j3 >= 86400000) {
            sp.edit().putLong(KEY_START_TIME_FOR_24H, j2).putInt(KEY_DISCONNECT_COUNT_IN_24H, 0).apply();
            upload(context);
        } else if (i2 >= 10) {
            upload(context);
        }
    }

    private static void resetAfterUpload(Context context) {
        DisconnectStatsHelper.log("resetAfterUpload");
        getSp(context).edit().putString("host", null).putString(KEY_NETWORK_STATE, null).putString(KEY_REASON, null).putString(KEY_PING_INTERVAL, null).putString("network_type", null).putString(KEY_WIFI_DIGEST, null).putString(KEY_CONNECTED_NETWORK_TYPE, null).putString(KEY_DISCONNECTION_TIME, null).putString(KEY_RECONNECTION_TIME, null).putLong(KEY_LAST_DISCONNECT_TIME, 0L).putInt(KEY_DISCONNECT_COUNT, 0).putInt(KEY_RECONNECTED_COUNT, 0).putString("xmsf_vc", null).putString(KEY_ANDROID_VC, null).apply();
    }

    private static String[] split(String str) {
        if (str == null || str.length() <= 0) {
            return null;
        }
        return str.split(";");
    }

    private static void upload(Context context) {
        DisconnectStatsHelper.log("upload");
        new DisconnectStatsReporter().upload(context, buildModel(context));
        resetAfterUpload(context);
    }

    private static String append(String str, int i2) {
        return append(str, String.valueOf(i2));
    }

    private static String append(String str, long j2) {
        return append(str, String.valueOf(j2));
    }

    private static int getNetState(boolean z2) {
        return z2 ? 1 : 0;
    }
}
