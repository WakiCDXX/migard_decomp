package com.ot.pubsub.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.ot.pubsub.PubSubTrack;

/* JADX INFO: loaded from: classes.dex */
public class l {
    @SuppressLint({"MissingPermission"})
    public static boolean a() {
        Context contextC = b.c();
        if (contextC == null) {
            return false;
        }
        try {
            NetworkInfo activeNetworkInfo = ((ConnectivityManager) contextC.getSystemService(LogType.LOG_TYPE_CONNECTIVITY_NAME)).getActiveNetworkInfo();
            if (activeNetworkInfo != null) {
                return activeNetworkInfo.isConnectedOrConnecting();
            }
            return false;
        } catch (Exception e2) {
            k.c("NetworkUtil", "isNetworkConnected exception: " + e2.getMessage());
            return false;
        }
    }

    public static PubSubTrack.NetType b(Context context) {
        try {
            NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService(LogType.LOG_TYPE_CONNECTIVITY_NAME)).getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                if (activeNetworkInfo.getType() == 1) {
                    return PubSubTrack.NetType.WIFI;
                }
                if (activeNetworkInfo.getType() != 0) {
                    if (activeNetworkInfo.getType() == 9) {
                        return PubSubTrack.NetType.ETHERNET;
                    }
                    return PubSubTrack.NetType.UNKNOWN;
                }
                switch (activeNetworkInfo.getSubtype()) {
                    case 1:
                    case 2:
                    case 4:
                    case 7:
                    case 11:
                    case 16:
                        return PubSubTrack.NetType.MOBILE_2G;
                    case 3:
                    case 5:
                    case 6:
                    case 8:
                    case 9:
                    case 10:
                    case 12:
                    case 14:
                    case 15:
                    case 17:
                        return PubSubTrack.NetType.MOBILE_3G;
                    case 13:
                    case 18:
                    case 19:
                        return PubSubTrack.NetType.MOBILE_4G;
                    case 20:
                        return PubSubTrack.NetType.MOBILE_5G;
                    default:
                        return PubSubTrack.NetType.UNKNOWN;
                }
            }
            return PubSubTrack.NetType.NOT_CONNECTED;
        } catch (Exception e2) {
            k.i("NetworkUtil", "getNetworkState error", e2);
        }
    }
}
