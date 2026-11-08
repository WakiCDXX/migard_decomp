package com.xiaomi.channel.commonutils.network;

import android.net.NetworkInfo;
import java.util.concurrent.ConcurrentHashMap;

/* JADX INFO: loaded from: classes.dex */
public class NetworkInfoWrapper {
    private static final String GET_DETAILED_STATE_M = "getDetailedState";
    private static final String GET_EXTRAINFO_M = "getExtraInfo";
    private static final String GET_REASON_M = "getReason";
    private static final String GET_STATE_M = "getState";
    private static final String GET_SUBTYPENAME_M = "getSubtypeName";
    private static final String GET_SUBTYPE_M = "getSubtype";
    private static final String GET_TYPENAME_M = "getTypeName";
    private static final String GET_TYPE_M = "getType";
    private static final String IS_AVAILABLE_M = "isAvailable";
    private static final String IS_CONNECTED_M = "isConnected";
    private static final String IS_CONNECTED_OR_CONNECTING_M = "isConnectedOrConnecting";
    private static final String IS_FAILOVER_M = "isFailover";
    private static final String IS_ROAMING_M = "isRoaming";
    private static final String TO_STRING_M = "toString";
    private final NetworkInfo mNetworkInfo;
    private final ConcurrentHashMap<String, Object> mNetworkInfoField = new ConcurrentHashMap<>();

    public NetworkInfoWrapper(NetworkInfo networkInfo) {
        this.mNetworkInfo = networkInfo;
    }

    /* JADX WARN: Code duplicated, block: B:55:0x00bc  */
    private <T> T getValue(String str) {
        T t2;
        if (this.mNetworkInfoField.containsKey(str)) {
            return (T) this.mNetworkInfoField.get(str);
        }
        synchronized (str) {
            try {
                Object objValueOf = null;
                if (!this.mNetworkInfoField.contains(str)) {
                    switch (str) {
                        case "getType":
                            objValueOf = Integer.valueOf(this.mNetworkInfo.getType());
                            break;
                        case "getTypeName":
                            objValueOf = this.mNetworkInfo.getTypeName();
                            break;
                        case "getSubtype":
                            objValueOf = Integer.valueOf(this.mNetworkInfo.getSubtype());
                            break;
                        case "getSubtypeName":
                            objValueOf = this.mNetworkInfo.getSubtypeName();
                            break;
                        case "isConnectedOrConnecting":
                            objValueOf = Boolean.valueOf(this.mNetworkInfo.isConnectedOrConnecting());
                            break;
                        case "isConnected":
                            objValueOf = Boolean.valueOf(this.mNetworkInfo.isConnected());
                            break;
                        case "isAvailable":
                            objValueOf = Boolean.valueOf(this.mNetworkInfo.isAvailable());
                            break;
                        case "isFailover":
                            objValueOf = Boolean.valueOf(this.mNetworkInfo.isFailover());
                            break;
                        case "isRoaming":
                            objValueOf = Boolean.valueOf(this.mNetworkInfo.isRoaming());
                            break;
                        case "getState":
                            objValueOf = this.mNetworkInfo.getState();
                            break;
                        case "getDetailedState":
                            objValueOf = this.mNetworkInfo.getDetailedState();
                            break;
                        case "getReason":
                            objValueOf = this.mNetworkInfo.getReason();
                            break;
                        case "getExtraInfo":
                            objValueOf = this.mNetworkInfo.getExtraInfo();
                            break;
                        case "toString":
                            objValueOf = this.mNetworkInfo.toString();
                            break;
                    }
                    if (objValueOf != null) {
                        this.mNetworkInfoField.put(str, objValueOf);
                    }
                }
                t2 = (T) objValueOf;
            } catch (Throwable th) {
                throw th;
            }
        }
        return t2;
    }

    public NetworkInfo.DetailedState getDetailedState() {
        return (NetworkInfo.DetailedState) getValue(GET_DETAILED_STATE_M);
    }

    public String getExtraInfo() {
        return (String) getValue(GET_EXTRAINFO_M);
    }

    public String getReason() {
        return (String) getValue(GET_REASON_M);
    }

    public NetworkInfo.State getState() {
        return (NetworkInfo.State) getValue(GET_STATE_M);
    }

    public int getSubtype() {
        return ((Integer) getValue(GET_SUBTYPE_M)).intValue();
    }

    public String getSubtypeName() {
        return (String) getValue(GET_SUBTYPENAME_M);
    }

    public int getType() {
        return ((Integer) getValue(GET_TYPE_M)).intValue();
    }

    public String getTypeName() {
        return (String) getValue(GET_TYPENAME_M);
    }

    public boolean isAvailable() {
        return ((Boolean) getValue(IS_AVAILABLE_M)).booleanValue();
    }

    public boolean isConnected() {
        return ((Boolean) getValue(IS_CONNECTED_M)).booleanValue();
    }

    public boolean isConnectedOrConnecting() {
        return ((Boolean) getValue(IS_CONNECTED_OR_CONNECTING_M)).booleanValue();
    }

    public boolean isFailover() {
        return ((Boolean) getValue(IS_FAILOVER_M)).booleanValue();
    }

    public boolean isRoaming() {
        return ((Boolean) getValue(IS_ROAMING_M)).booleanValue();
    }

    public String toString() {
        return (String) getValue(TO_STRING_M);
    }
}
