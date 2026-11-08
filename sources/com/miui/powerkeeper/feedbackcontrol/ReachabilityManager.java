package com.miui.powerkeeper.feedbackcontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.LocalLog;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.IndentingPrintWriter;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.utils.CommonAdapter;
import com.miui.powerkeeper.utils.NetUtils;
import com.miui.powerkeeper.utils.VpnTracker;
import com.miui.whetstone.INetStateCallback;
import com.miui.whetstone.server.IWhetstoneActivityManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* JADX INFO: loaded from: classes.dex */
public class ReachabilityManager {
    private static final boolean DEBUG;
    private static final long DELAY_TIME = 3000;
    private static final int MAX_HISTORY_ITEMS;
    private static final String TAG = "PowerKeeper.Reach";
    private final Handler.Callback mCallback;
    private Context mContext;
    private int mFeatureEnabled;
    private GoogleHost mGoogleHost;
    private Handler mHandler;
    private boolean mNetworkConnected;
    private Handler mNotifyHandler;
    private final BroadcastReceiver mReceiver;
    private final VpnTracker.StatusCallback mVpnCallback;
    private boolean mVpnConnected;
    private LocalLog mHistoryLog = new LocalLog(MAX_HISTORY_ITEMS);
    private final int EVENT_NETWORK_CHANGED = 0;
    private final int EVENT_VPN_CHANGED = 1;
    private final int MSG_CHECK_NETWORK = 2;

    private class GoogleHost extends Host {
        private final int NET_CACHE_TIME;
        private boolean mEnabled;

        public GoogleHost() {
            super();
            this.NET_CACHE_TIME = 1800000;
            this.mEnabled = false;
            this.mChecker = ReachabilityManager.this.new ReachableChecker(this);
            this.mURL = "http://clients3.google.com/generate_204";
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void onVpnChanged() {
            ReachabilityManager.this.mHistoryLog.log("PowerKeeper.Reach vpn connected " + ReachabilityManager.this.mVpnConnected);
            ReachabilityManager.this.mHandler.removeMessages(2);
            if (ReachabilityManager.this.mVpnConnected) {
                notifyListeners(true);
            } else {
                onConnectedChanged();
            }
        }

        @Override // com.miui.powerkeeper.feedbackcontrol.ReachabilityManager.Host
        public void cacheCurrentNet(boolean z2) {
            int subscriptionId = ReachabilityManager.this.getSubscriptionId();
            if (!this.mEnabled || ReachabilityManager.this.mVpnConnected || !NetUtils.isMobileConnected(ReachabilityManager.this.mContext) || subscriptionId == 0) {
                return;
            }
            Network activeNetwork = ((ConnectivityManager) ReachabilityManager.this.mContext.getSystemService(LogType.LOG_TYPE_CONNECTIVITY_NAME)).getActiveNetwork();
            NetState netState = this.mNetStatusCache.get(subscriptionId);
            if (activeNetwork == null || netState == null || CommonAdapter.getNetId(activeNetwork) != netState.netId) {
                return;
            }
            netState.setState(System.currentTimeMillis(), z2);
            ReachabilityManager.this.mHistoryLog.log("PowerKeeper.Reach record network subId = " + subscriptionId + " reach = " + z2);
        }

        @Override // com.miui.powerkeeper.feedbackcontrol.ReachabilityManager.Host
        public void notifyListeners(boolean z2) {
            if (this.mEnabled) {
                if (super.needNotifyListeners(z2 || (ReachabilityManager.this.mNetworkConnected && ReachabilityManager.this.mVpnConnected))) {
                    if (z2 || (ReachabilityManager.this.mNetworkConnected && ReachabilityManager.this.mVpnConnected)) {
                        ReachabilityManager.this.mNotifyHandler.obtainMessage(4).sendToTarget();
                    } else {
                        ReachabilityManager.this.mNotifyHandler.obtainMessage(5).sendToTarget();
                    }
                }
            }
        }

        @Override // com.miui.powerkeeper.feedbackcontrol.ReachabilityManager.Host
        public void onConnectedChanged() {
            ReachabilityManager.this.mHandler.removeMessages(2);
            if (!ReachabilityManager.this.mNetworkConnected) {
                cacheCurrentNet(false);
                notifyListeners(false);
                ReachabilityManager.this.mHistoryLog.log("PowerKeeper.Reach Notify listeners for mNetworkConnected being false.");
                return;
            }
            Network activeNetwork = ((ConnectivityManager) ReachabilityManager.this.mContext.getSystemService(LogType.LOG_TYPE_CONNECTIVITY_NAME)).getActiveNetwork();
            if (activeNetwork == null) {
                cacheCurrentNet(false);
                notifyListeners(false);
                ReachabilityManager.this.mHistoryLog.log("PowerKeeper.Reach Notify listeners for ConnectivityManager missing.");
                return;
            }
            int netId = CommonAdapter.getNetId(activeNetwork);
            if (!NetUtils.isMobileConnected(ReachabilityManager.this.mContext) || !ReachabilityManager.this.isChinaMainlandTime()) {
                ReachabilityManager.this.mHandler.sendMessageDelayed(ReachabilityManager.this.mHandler.obtainMessage(2, netId, 0), ReachabilityManager.DELAY_TIME);
                return;
            }
            int subscriptionId = ReachabilityManager.this.getSubscriptionId();
            if (subscriptionId != 0) {
                NetState netState = this.mNetStatusCache.get(subscriptionId);
                if (netState == null) {
                    netState = new NetState();
                    this.mNetStatusCache.put(subscriptionId, netState);
                }
                if (System.currentTimeMillis() - netState.mCheckTime <= 1800000) {
                    notifyListeners(netState.mReach);
                    return;
                }
                netState.reset(netId);
                ReachabilityManager.this.mHandler.sendMessageDelayed(ReachabilityManager.this.mHandler.obtainMessage(2, netId, 0), ReachabilityManager.DELAY_TIME);
            }
        }
    }

    abstract class Host {
        protected ReachableChecker mChecker;
        private boolean mFisrtCheck;
        private boolean mLastReachability;
        protected SparseArray<NetState> mNetStatusCache = new SparseArray<>();
        protected String mURL;

        public Host() {
            reset();
        }

        public abstract void cacheCurrentNet(boolean z2);

        public String getURL() {
            return this.mURL;
        }

        protected boolean needNotifyListeners(boolean z2) {
            if (!this.mFisrtCheck && z2 == this.mLastReachability) {
                return false;
            }
            this.mLastReachability = z2;
            this.mFisrtCheck = false;
            return true;
        }

        public abstract void notifyListeners(boolean z2);

        public abstract void onConnectedChanged();

        protected void onHostEnabled() {
            Network activeNetwork;
            ReachabilityManager.this.mHandler.removeMessages(2);
            if (!ReachabilityManager.this.mNetworkConnected || (activeNetwork = ((ConnectivityManager) ReachabilityManager.this.mContext.getSystemService(LogType.LOG_TYPE_CONNECTIVITY_NAME)).getActiveNetwork()) == null) {
                return;
            }
            ReachabilityManager.this.mHandler.sendMessageDelayed(ReachabilityManager.this.mHandler.obtainMessage(2, CommonAdapter.getNetId(activeNetwork), 0), ReachabilityManager.DELAY_TIME);
        }

        protected void reset() {
            this.mLastReachability = true;
            this.mFisrtCheck = true;
            this.mNetStatusCache.clear();
        }
    }

    private class NetState {
        public long mCheckTime;
        public boolean mReach;
        public int netId;

        public void reset(int i2) {
            this.netId = i2;
            this.mCheckTime = 0L;
        }

        public void setState(long j2, boolean z2) {
            this.mCheckTime = j2;
            this.mReach = z2;
        }

        private NetState() {
        }
    }

    private class ReachableChecker {
        private Host mHost;
        private INetStateCallback mNetworkCallback = new INetStateCallback.Stub() { // from class: com.miui.powerkeeper.feedbackcontrol.ReachabilityManager.ReachableChecker.1
            public void onCheckComplete(boolean z2, int i2) throws RemoteException {
                Network activeNetwork = ((ConnectivityManager) ReachabilityManager.this.mContext.getSystemService(LogType.LOG_TYPE_CONNECTIVITY_NAME)).getActiveNetwork();
                if (activeNetwork == null || i2 != CommonAdapter.getNetId(activeNetwork)) {
                    return;
                }
                ReachableChecker.this.mHost.cacheCurrentNet(z2);
                ReachableChecker.this.mHost.notifyListeners(z2);
            }
        };

        public ReachableChecker(Host host) {
            this.mHost = host;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void checkNetworkState(int i2) {
            try {
                IWhetstoneActivityManager iWhetstoneActivityManagerAsInterface = IWhetstoneActivityManager.Stub.asInterface(ServiceManager.getService("whetstone.activity"));
                if (iWhetstoneActivityManagerAsInterface == null) {
                    Log.d(ReachabilityManager.TAG, "ws is null");
                } else {
                    iWhetstoneActivityManagerAsInterface.getPowerKeeperPolicy().checkNetworkState(this.mHost.getURL(), i2, this.mNetworkCallback);
                }
            } catch (RemoteException unused) {
                Log.e(ReachabilityManager.TAG, "IWhetstoneActivityManager work abnormal");
            }
        }
    }

    static {
        boolean z2 = Build.IS_DEBUGGABLE;
        DEBUG = z2;
        MAX_HISTORY_ITEMS = z2 ? 512 : 256;
    }

    public ReachabilityManager(Context context, Handler handler, int i2) {
        Handler.Callback callback = new Handler.Callback() { // from class: com.miui.powerkeeper.feedbackcontrol.ReachabilityManager.1
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message message) {
                int i3 = message.what;
                if (i3 == 0) {
                    ReachabilityManager reachabilityManager = ReachabilityManager.this;
                    reachabilityManager.mNetworkConnected = NetUtils.isNetConnected(reachabilityManager.mContext);
                    if (ReachabilityManager.this.mGoogleHost != null && ReachabilityManager.this.mGoogleHost.mEnabled) {
                        ReachabilityManager.this.mGoogleHost.onConnectedChanged();
                    }
                } else if (i3 == 1) {
                    ReachabilityManager reachabilityManager2 = ReachabilityManager.this;
                    reachabilityManager2.mNetworkConnected = NetUtils.isNetConnected(reachabilityManager2.mContext);
                    ReachabilityManager.this.mVpnConnected = ((Boolean) message.obj).booleanValue();
                    if (ReachabilityManager.this.mGoogleHost != null && ReachabilityManager.this.mGoogleHost.mEnabled) {
                        ReachabilityManager.this.mGoogleHost.onVpnChanged();
                    }
                } else if (i3 == 2 && ReachabilityManager.this.mGoogleHost != null && ReachabilityManager.this.mGoogleHost.mEnabled) {
                    ReachabilityManager.this.mGoogleHost.mChecker.checkNetworkState(message.arg1);
                }
                return true;
            }
        };
        this.mCallback = callback;
        this.mVpnCallback = new VpnTracker.StatusCallback() { // from class: com.miui.powerkeeper.feedbackcontrol.ReachabilityManager.2
            @Override // com.miui.powerkeeper.utils.VpnTracker.StatusCallback
            public void onConnected(int i3) {
                ReachabilityManager.this.mHandler.obtainMessage(1, Boolean.TRUE).sendToTarget();
            }

            @Override // com.miui.powerkeeper.utils.VpnTracker.StatusCallback
            public void onDisconnected(int i3) {
                ReachabilityManager.this.mHandler.obtainMessage(1, Boolean.FALSE).sendToTarget();
            }
        };
        this.mReceiver = new BroadcastReceiver() { // from class: com.miui.powerkeeper.feedbackcontrol.ReachabilityManager.3
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
                    if (ReachabilityManager.DEBUG) {
                        Log.d(ReachabilityManager.TAG, "receive connectivity action");
                    }
                    int intExtra = intent.getIntExtra("networkType", -1);
                    if (intExtra == 3 || intExtra == 17) {
                        return;
                    }
                    ReachabilityManager.this.mHandler.obtainMessage(0).sendToTarget();
                }
            }
        };
        Log.d(TAG, "create ReachabilityManager");
        this.mContext = context;
        this.mFeatureEnabled = 0;
        this.mNotifyHandler = handler;
        this.mHandler = new Handler(BackgroundThread.getHandler().getLooper(), callback);
        enableMonitor(i2);
    }

    private void enableGmsHost(boolean z2) {
        if (z2) {
            if (this.mGoogleHost == null) {
                this.mGoogleHost = new GoogleHost();
            }
            this.mGoogleHost.mEnabled = true;
            PowerKeeperManager.getInstance().registerVpnStatusCallback(this.mVpnCallback);
            this.mGoogleHost.onHostEnabled();
            return;
        }
        GoogleHost googleHost = this.mGoogleHost;
        if (googleHost == null || !googleHost.mEnabled) {
            return;
        }
        this.mGoogleHost.notifyListeners(true);
        this.mGoogleHost.mEnabled = false;
        PowerKeeperManager.getInstance().unregisterVpnStatusCallback(this.mVpnCallback);
        this.mGoogleHost.reset();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getSubscriptionId() {
        SubscriptionInfo defaultDataSubscriptionInfo = ((SubscriptionManager) this.mContext.getSystemService("telephony_subscription_service")).getDefaultDataSubscriptionInfo();
        if (defaultDataSubscriptionInfo != null) {
            return defaultDataSubscriptionInfo.getSubscriptionId();
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isChinaMainlandTime() {
        String str = SystemProperties.get("persist.sys.timezone");
        return "Asia/Shanghai".equals(str) || "Asia/Urumqi".equals(str);
    }

    private void registerNetworkReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        this.mContext.registerReceiver(this.mReceiver, intentFilter, 2);
    }

    private void unRegisterNetworkReceiver() {
        this.mContext.unregisterReceiver(this.mReceiver);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        indentingPrintWriter.println("#######dump##RechabilityManager#######");
        synchronized (this.mHistoryLog) {
            printWriter.println("\t ReachabilityManager status:" + this.mFeatureEnabled);
            this.mHistoryLog.dump(fileDescriptor, printWriter, strArr);
        }
        indentingPrintWriter.println("end#######dump##RechabilityManager#######end");
    }

    public void enableMonitor(int i2) {
        Log.d(TAG, "enableMonitor:" + i2 + ", last: " + this.mFeatureEnabled);
        int i3 = this.mFeatureEnabled;
        if (i3 == 0 && i2 != 0) {
            registerNetworkReceiver();
        } else if (i3 != 0 && i2 == 0) {
            unRegisterNetworkReceiver();
        }
        if (((this.mFeatureEnabled ^ i2) & 512) != 0) {
            if ((i2 & 512) != 0) {
                enableGmsHost(true);
            } else {
                enableGmsHost(false);
            }
        }
        this.mFeatureEnabled = i2;
    }
}
