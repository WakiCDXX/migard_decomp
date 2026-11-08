package com.miui.powerkeeper.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.INetworkManagementEventObserver;
import android.os.Handler;
import android.os.INetworkManagementService;
import android.os.Message;
import android.os.ServiceManager;
import android.util.Log;
import com.android.internal.net.VpnConfig;
import com.android.server.net.BaseNetworkObserver;
import com.miui.powerkeeper.PowerKeeperManager;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class VpnTracker {
    private static final boolean DEBUG = true;
    private static final int EVENT_INTERFACE_ADDED = 0;
    private static final int EVENT_INTERFACE_REMOVED = 1;
    private static final String TAG = "PowerKeeper.Vpn";
    private static final String VPN_TAG = "tun";
    private List<StatusCallback> mCallbacks;
    private Context mContext;
    private final INetworkManagementEventObserver mIfaceObserver;
    private final INetworkManagementService mNetworkManager;
    final Map<String, VpnInfo> mVpns = new HashMap();
    private Object mLock = new Object();
    private Handler mHandler = new Handler(PowerKeeperManager.getThread().getLooper()) { // from class: com.miui.powerkeeper.utils.VpnTracker.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            String str = (String) message.obj;
            int i2 = message.what;
            if (i2 == 0) {
                VpnTracker.this.handleVpnAdded(str);
            } else {
                if (i2 != 1) {
                    return;
                }
                VpnTracker.this.handleVpnRemoved(str);
            }
        }
    };

    public interface StatusCallback {
        void onConnected(int i2);

        void onDisconnected(int i2);
    }

    public static class VpnInfo {
        public int ownerUid;
        public String vpnIface;
    }

    public VpnTracker(Context context) {
        BaseNetworkObserver baseNetworkObserver = new BaseNetworkObserver() { // from class: com.miui.powerkeeper.utils.VpnTracker.2
            public void interfaceAdded(String str) {
                if (str.indexOf(VpnTracker.VPN_TAG) >= 0) {
                    Log.d(VpnTracker.TAG, "added vpn iface: " + str);
                    VpnTracker.this.mHandler.obtainMessage(0, str).sendToTarget();
                }
            }

            public void interfaceRemoved(String str) {
                if (str.indexOf(VpnTracker.VPN_TAG) >= 0) {
                    Log.d(VpnTracker.TAG, "removed vpn iface: " + str);
                    VpnTracker.this.mHandler.obtainMessage(1, str).sendToTarget();
                }
            }

            public void interfaceLinkStateChanged(String str, boolean z2) {
            }
        };
        this.mIfaceObserver = baseNetworkObserver;
        Log.d(TAG, "create vpn tracker");
        this.mContext = context;
        this.mCallbacks = new ArrayList();
        INetworkManagementService iNetworkManagementServiceAsInterface = INetworkManagementService.Stub.asInterface(ServiceManager.getService("network_management"));
        this.mNetworkManager = iNetworkManagementServiceAsInterface;
        try {
            iNetworkManagementServiceAsInterface.registerObserver(baseNetworkObserver);
        } catch (Exception e2) {
            Log.d(TAG, "error: " + e2);
        }
        checkExistInterfaces();
    }

    private void checkExistInterfaces() {
        Enumeration<NetworkInterface> networkInterfaces;
        try {
            networkInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e2) {
            Log.i(TAG, "error: " + e2);
            networkInterfaces = null;
        }
        if (networkInterfaces == null) {
            return;
        }
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterfaceNextElement = networkInterfaces.nextElement();
            if (networkInterfaceNextElement.getName().indexOf(VPN_TAG) >= 0) {
                Log.i(TAG, "added vpn iface: " + networkInterfaceNextElement.getName());
                this.mHandler.obtainMessage(0, networkInterfaceNextElement.getName()).sendToTarget();
            }
        }
    }

    private int getAppUid(String str, int i2) {
        try {
            return this.mContext.getPackageManager().getPackageUid(str, i2);
        } catch (PackageManager.NameNotFoundException unused) {
            return -1;
        } catch (Exception e2) {
            Log.e(TAG, "error: " + e2);
            return -1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleVpnAdded(String str) {
        try {
            for (int i2 : PowerKeeperManager.getInstance().getUserCheckManager().x()) {
                VpnConfig vpnConfig = CommonAdapter.getVpnConfig(this.mContext, i2);
                if (vpnConfig != null && vpnConfig.interfaze.equals(str)) {
                    VpnInfo vpnInfo = new VpnInfo();
                    vpnInfo.ownerUid = getAppUid(vpnConfig.user, i2);
                    vpnInfo.vpnIface = str;
                    Log.d(TAG, "add vpn: user = " + vpnConfig.user + ", uid = " + vpnInfo.ownerUid + ", userId = " + i2);
                    synchronized (this.mLock) {
                        try {
                            this.mVpns.put(str, vpnInfo);
                            Iterator<StatusCallback> it = this.mCallbacks.iterator();
                            while (it.hasNext()) {
                                it.next().onConnected(vpnInfo.ownerUid);
                            }
                        } catch (Throwable th) {
                            throw th;
                        }
                    }
                }
            }
        } catch (Exception e2) {
            Log.e(TAG, "error: " + e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleVpnRemoved(String str) {
        synchronized (this.mLock) {
            try {
                Iterator<Map.Entry<String, VpnInfo>> it = this.mVpns.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, VpnInfo> next = it.next();
                    if (next.getKey().equals(str)) {
                        Log.d(TAG, "removed vpn: " + next.getValue().ownerUid);
                        it.remove();
                        Iterator<StatusCallback> it2 = this.mCallbacks.iterator();
                        while (it2.hasNext()) {
                            it2.next().onDisconnected(next.getValue().ownerUid);
                        }
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public int[] getCurrentVpn() {
        int[] iArr;
        synchronized (this.mLock) {
            try {
                iArr = new int[this.mVpns.values().size()];
                Iterator<VpnInfo> it = this.mVpns.values().iterator();
                int i2 = 0;
                while (it.hasNext()) {
                    iArr[i2] = it.next().ownerUid;
                    i2++;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return iArr;
    }

    public boolean registerVpnStatusCallback(StatusCallback statusCallback) {
        if (statusCallback == null) {
            return false;
        }
        synchronized (this.mLock) {
            try {
                if (this.mCallbacks.contains(statusCallback)) {
                    Log.d(TAG, "vpn StatusCallback already registered");
                    return false;
                }
                this.mCallbacks.add(statusCallback);
                if (this.mVpns.size() > 0) {
                    Iterator<Map.Entry<String, VpnInfo>> it = this.mVpns.entrySet().iterator();
                    while (it.hasNext()) {
                        statusCallback.onConnected(it.next().getValue().ownerUid);
                    }
                }
                return true;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public boolean unregisterVpnStatusCallback(StatusCallback statusCallback) {
        if (statusCallback == null) {
            return false;
        }
        synchronized (this.mLock) {
            try {
                if (this.mCallbacks.contains(statusCallback)) {
                    this.mCallbacks.remove(statusCallback);
                    return true;
                }
                Log.d(TAG, "vpn StatusCallback not registered");
                return false;
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
