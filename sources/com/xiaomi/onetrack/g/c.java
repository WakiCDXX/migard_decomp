package com.xiaomi.onetrack.g;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.xiaomi.onetrack.OneTrack;
import com.xiaomi.onetrack.util.p;

/* JADX INFO: loaded from: classes.dex */
public class c {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final String f1441a = "NetworkUtil";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static final int f1442b = 16;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static final int f1443c = 17;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static final int f1444d = 18;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static final int f1445e = 19;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static final int f1446f = 20;

    @SuppressLint({"MissingPermission"})
    public static boolean a() {
        Context contextB = com.xiaomi.onetrack.f.a.b();
        if (contextB == null) {
            return false;
        }
        try {
            NetworkInfo activeNetworkInfo = ((ConnectivityManager) contextB.getSystemService(LogType.LOG_TYPE_CONNECTIVITY_NAME)).getActiveNetworkInfo();
            if (activeNetworkInfo != null) {
                return activeNetworkInfo.isConnectedOrConnecting();
            }
            return false;
        } catch (Exception unused) {
            p.a(f1441a, "isNetworkConnected exception");
            return false;
        }
    }

    public static OneTrack.NetType a(Context context) {
        try {
            NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService(LogType.LOG_TYPE_CONNECTIVITY_NAME)).getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                if (activeNetworkInfo.getType() == 1) {
                    return OneTrack.NetType.WIFI;
                }
                if (activeNetworkInfo.getType() == 0) {
                    switch (activeNetworkInfo.getSubtype()) {
                        case 1:
                        case 2:
                        case 4:
                        case 7:
                        case 11:
                        case 16:
                            return OneTrack.NetType.MOBILE_2G;
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
                            return OneTrack.NetType.MOBILE_3G;
                        case 13:
                        case 18:
                        case 19:
                            return OneTrack.NetType.MOBILE_4G;
                        case 20:
                            return OneTrack.NetType.MOBILE_5G;
                        default:
                            return OneTrack.NetType.UNKNOWN;
                    }
                }
                if (activeNetworkInfo.getType() == 9) {
                    return OneTrack.NetType.ETHERNET;
                }
                return OneTrack.NetType.UNKNOWN;
            }
            return OneTrack.NetType.NOT_CONNECTED;
        } catch (Exception e2) {
            p.b(f1441a, "getNetworkState error", e2);
        }
    }
}
