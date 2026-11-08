package com.xiaomi.push.clientreport;

import android.content.Context;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.network.Network;
import com.xiaomi.channel.commonutils.network.NetworkInfoWrapper;
import com.xiaomi.push.service.HeartbeatHolder;
import com.xiaomi.smack.Connection;

/* JADX INFO: loaded from: classes.dex */
public class DisconnectStatsHelper {
    private static final int NET_TYPE_MOBILE = 0;
    private static final int NET_TYPE_UNKNOWN = -1;
    private static final int NET_TYPE_WIFI = 1;
    public static final String TAG = "Push-DiscntStats";
    private static int connectedNetworkType = -1;
    private static DisconnectionListener disconnectionListener;
    private static String netId;

    private static synchronized String getNetId() {
        return netId;
    }

    static void log(String str) {
        ConnectionQualityStatsHelper.log(TAG, str);
    }

    public static void onDisconnection(Context context, String str, int i2) {
        if (!shouldSampling(context)) {
            log("onDisconnection shouldSampling = false");
            return;
        }
        DisconnectStatsSP.onDisconnection(context, str, Network.isConnectivity(context), System.currentTimeMillis(), i2, HeartbeatHolder.getInstance(context).getLastPingInterval(), refreshNetworkInfo(context), getNetId(), connectedNetworkType);
        log("onDisconnection");
    }

    public static void onReconnection(Context context) {
        if (!shouldSampling(context)) {
            log("onReconnection shouldSampling = false");
            return;
        }
        long jCurrentTimeMillis = System.currentTimeMillis();
        connectedNetworkType = refreshNetworkInfo(context);
        DisconnectStatsSP.onReconnection(context, jCurrentTimeMillis);
        log("onReconnection connectedNetworkType = " + connectedNetworkType);
    }

    public static void onWifiChanged(Context context, String str) {
        if (!shouldSampling(context)) {
            log("onWifiChanged shouldSampling = false");
            return;
        }
        log("onWifiChanged wifiDigest = " + str);
        if (TextUtils.isEmpty(str)) {
            return;
        }
        updateNetId(HeartbeatHolder.NETID_PREFIX_WIFI + str);
    }

    private static int refreshNetworkInfo(Context context) {
        String str;
        try {
            NetworkInfoWrapper networkInfo = Network.getNetworkInfo();
            if (networkInfo == null) {
                updateNetId(null);
                return -1;
            }
            if (networkInfo.getType() != 0) {
                if (networkInfo.getType() != 1 && networkInfo.getType() != 6) {
                    updateNetId(null);
                    return -1;
                }
                updateNetId(HeartbeatHolder.NETID_UNKNOWN_WIFI);
                return 1;
            }
            String subtypeName = networkInfo.getSubtypeName();
            if (TextUtils.isEmpty(subtypeName) || "UNKNOWN".equalsIgnoreCase(subtypeName)) {
                str = null;
            } else {
                str = HeartbeatHolder.NETID_PREFIX_MOBILE + subtypeName;
            }
            updateNetId(str);
            return 0;
        } catch (Exception e2) {
            MyLog.e("DisconnectStatsHelper getNetType occurred error: " + e2.getMessage());
            updateNetId(null);
            return -1;
        }
    }

    private static boolean shouldSampling(Context context) {
        return ConnectionQualityStatsHelper.shouldSampling(context);
    }

    public static void startStats(Context context, Connection connection) {
        if (shouldSampling(context)) {
            if (disconnectionListener == null) {
                disconnectionListener = new DisconnectionListener(context);
            }
            connection.addConnectionListener(disconnectionListener);
            log("startStats");
        }
    }

    public static void stopStats(Context context, Connection connection) {
        DisconnectionListener disconnectionListener2 = disconnectionListener;
        if (disconnectionListener2 != null) {
            connection.removeConnectionListener(disconnectionListener2);
            disconnectionListener = null;
            log("stopStats");
        }
    }

    private static synchronized void updateNetId(String str) {
        try {
            if (HeartbeatHolder.NETID_UNKNOWN_WIFI.equals(str)) {
                String str2 = netId;
                if (str2 == null || !str2.startsWith(HeartbeatHolder.NETID_PREFIX_WIFI)) {
                    netId = null;
                }
            } else {
                netId = str;
            }
            log("updateNetId new networkId = " + str + ", finally netId = " + netId);
        } catch (Throwable th) {
            throw th;
        }
    }
}
