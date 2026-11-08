package com.miui.powerkeeper;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.UserHandle;
import android.util.Log;
import com.android.internal.os.BackgroundThread;
import com.google.android.collect.Lists;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class FeedBackManager extends IFeedbackControlListener.Stub {
    private static final int MSG_BATTERY_HEAT = 1000;
    private static final int MSG_DATA_WIFI_SETTING_CHANGED = 1003;
    private static final int MSG_GOOGLE_NETWORK_CONNECTED = 1002;
    private static final int MSG_NETWORK_CONNECTED = 1001;
    private static FeedBackManager sInstance;
    private Context mContext;
    private static String TAG = FeedBackManager.class.getSimpleName();
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    private Boolean mGoogleNetworkConnected = null;
    private Handler.Callback mCallback = new a();
    private final Object mLock = new Object();
    private final List<PowerKeeperInterface.e> mCallbacks = Lists.newArrayList();
    private boolean mBatteryHeat = false;
    private boolean mDataWifiSettingDisabled = false;
    private boolean mNetworkConnected = false;
    private Handler mHandler = new Handler(BackgroundThread.get().getLooper(), this.mCallback);

    class a implements Handler.Callback {
        a() {
        }

        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 1000:
                    FeedBackManager.this.updateBatteryHeat(message.arg1 > 0);
                    break;
                case 1001:
                    FeedBackManager.this.updateNetworkConnected(message.arg1 > 0);
                    break;
                case 1002:
                    FeedBackManager.this.updateGoogleNetworkConnected(message.arg1 > 0);
                    break;
                case 1003:
                    FeedBackManager.this.updataDataWifiSettingChanged(message.arg1 > 0);
                    break;
                default:
                    return false;
            }
            return true;
        }
    }

    class b implements Runnable {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final /* synthetic */ PowerKeeperInterface.e f181a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        final /* synthetic */ boolean f182b;

        b(PowerKeeperInterface.e eVar, boolean z2) {
            this.f181a = eVar;
            this.f182b = z2;
        }

        @Override // java.lang.Runnable
        public void run() {
            PowerKeeperInterface.o oVar = this.f181a.f256b;
            if (oVar != null) {
                if (this.f182b) {
                    oVar.batteryBecomeHeat();
                } else {
                    oVar.batteryCoolDown();
                }
            }
        }
    }

    class c implements Runnable {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final /* synthetic */ PowerKeeperInterface.e f184a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        final /* synthetic */ boolean f185b;

        c(PowerKeeperInterface.e eVar, boolean z2) {
            this.f184a = eVar;
            this.f185b = z2;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.f184a.getClass();
        }
    }

    class d implements Runnable {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final /* synthetic */ PowerKeeperInterface.e f187a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        final /* synthetic */ boolean f188b;

        d(PowerKeeperInterface.e eVar, boolean z2) {
            this.f187a = eVar;
            this.f188b = z2;
        }

        @Override // java.lang.Runnable
        public void run() {
            PowerKeeperInterface.p pVar = this.f187a.f257c;
            if (pVar != null) {
                if (this.f188b) {
                    pVar.googleNetworkConnected();
                } else {
                    pVar.googleNetworkDisconnect();
                }
            }
        }
    }

    class e implements Runnable {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final /* synthetic */ PowerKeeperInterface.e f190a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        final /* synthetic */ boolean f191b;

        e(PowerKeeperInterface.e eVar, boolean z2) {
            this.f190a = eVar;
            this.f191b = z2;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.f190a.getClass();
        }
    }

    private FeedBackManager(Context context) {
        this.mContext = context;
    }

    public static synchronized FeedBackManager getInstance(Context context) {
        try {
            if (sInstance == null) {
                sInstance = new FeedBackManager(context);
            }
        } catch (Throwable th) {
            throw th;
        }
        return sInstance;
    }

    private void notifyBatteryBecomeHeat(boolean z2) {
        this.mHandler.obtainMessage(1000, z2 ? 1 : 0, 0).sendToTarget();
    }

    private void notifyDataWifiSettingChanged(boolean z2) {
        this.mHandler.obtainMessage(1003, z2 ? 1 : 0, 0).sendToTarget();
    }

    private void notifyGoogleNetworkConnected(boolean z2) {
        this.mHandler.obtainMessage(1002, z2 ? 1 : 0, 0).sendToTarget();
    }

    private void notifyNetworkConnected(boolean z2) {
        this.mHandler.obtainMessage(1001, z2 ? 1 : 0, 0).sendToTarget();
    }

    private void sendBroadCastNotifyNetworkConnected(String str) {
        Intent intent = new Intent(str);
        intent.addFlags(1342177280);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updataDataWifiSettingChanged(boolean z2) {
        Log.d(TAG, "updataDataWifiSettingChanged, " + z2);
        synchronized (this.mLock) {
            try {
                if (this.mDataWifiSettingDisabled == z2) {
                    return;
                }
                this.mDataWifiSettingDisabled = z2;
                synchronized (this.mCallbacks) {
                    try {
                        for (PowerKeeperInterface.e eVar : this.mCallbacks) {
                            Handler handler = eVar.f255a;
                            if (handler == null) {
                                handler = this.mHandler;
                            }
                            handler.post(new e(eVar, z2));
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            } catch (Throwable th2) {
                throw th2;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateBatteryHeat(boolean z2) {
        Log.d(TAG, "updateBatteryHeat, " + z2);
        synchronized (this.mLock) {
            try {
                if (this.mBatteryHeat == z2) {
                    return;
                }
                this.mBatteryHeat = z2;
                EventsAggregator.getInstance(this.mContext).notifyBatteryHeatListeners(z2);
                synchronized (this.mCallbacks) {
                    try {
                        for (PowerKeeperInterface.e eVar : this.mCallbacks) {
                            Handler handler = eVar.f255a;
                            if (handler == null) {
                                handler = this.mHandler;
                            }
                            handler.post(new b(eVar, z2));
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            } catch (Throwable th2) {
                throw th2;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateGoogleNetworkConnected(boolean z2) {
        Log.d(TAG, "updateGoogleNetworkConnected, " + z2);
        synchronized (this.mLock) {
            try {
                Boolean bool = this.mGoogleNetworkConnected;
                if (bool == null || bool.booleanValue() != z2) {
                    this.mGoogleNetworkConnected = Boolean.valueOf(z2);
                    synchronized (this.mCallbacks) {
                        try {
                            for (PowerKeeperInterface.e eVar : this.mCallbacks) {
                                Handler handler = eVar.f255a;
                                if (handler == null) {
                                    handler = this.mHandler;
                                }
                                handler.post(new d(eVar, z2));
                            }
                        } catch (Throwable th) {
                            throw th;
                        }
                    }
                }
            } catch (Throwable th2) {
                throw th2;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNetworkConnected(boolean z2) {
        Log.d(TAG, "updateNetworkConnected, " + z2);
        synchronized (this.mLock) {
            try {
                if (this.mNetworkConnected == z2) {
                    return;
                }
                this.mNetworkConnected = z2;
                synchronized (this.mCallbacks) {
                    try {
                        for (PowerKeeperInterface.e eVar : this.mCallbacks) {
                            Handler handler = eVar.f255a;
                            if (handler == null) {
                                handler = this.mHandler;
                            }
                            handler.post(new c(eVar, z2));
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            } catch (Throwable th2) {
                throw th2;
            }
        }
    }

    @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener
    public void batteryBecomeHeat() {
        if (DEBUG) {
            Log.d(TAG, "battery become heat");
        }
        notifyBatteryBecomeHeat(true);
    }

    @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener
    public void batteryCoolDown() {
        if (DEBUG) {
            Log.d(TAG, "battery cool down");
        }
        notifyBatteryBecomeHeat(false);
    }

    @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener
    public void dataWifiSettingToDisable() {
        notifyDataWifiSettingChanged(true);
    }

    @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener
    public void dataWifiSettingToEnable() {
        notifyDataWifiSettingChanged(false);
    }

    @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener
    public void googleNetworkConnected() {
        if (DEBUG) {
            Log.d(TAG, "google network connected");
        }
        notifyGoogleNetworkConnected(true);
    }

    @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener
    public void googleNetworkDisconnect() {
        if (DEBUG) {
            Log.d(TAG, "google network disconnected");
        }
        notifyGoogleNetworkConnected(false);
    }

    public boolean isBatteryHeat() {
        boolean z2;
        synchronized (this.mLock) {
            z2 = this.mBatteryHeat;
        }
        return z2;
    }

    public boolean isGoogleNetworkConnected() {
        boolean zBooleanValue;
        synchronized (this.mLock) {
            Boolean bool = this.mGoogleNetworkConnected;
            zBooleanValue = bool == null ? true : bool.booleanValue();
        }
        return zBooleanValue;
    }

    public boolean isNetworkConnected() {
        boolean z2;
        synchronized (this.mLock) {
            z2 = this.mNetworkConnected;
        }
        return z2;
    }

    @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener
    public void networkDisconnectFromInternet() {
        if (DEBUG) {
            Log.d(TAG, "network disconnected from internet");
        }
        notifyNetworkConnected(false);
        sendBroadCastNotifyNetworkConnected("android.os.action.networkDisconnectFromInternet");
    }

    @Override // com.miui.powerkeeper.feedbackcontrol.IFeedbackControlListener
    public void networkReconnectToInternet() {
        if (DEBUG) {
            Log.d(TAG, "network reconnected to internet");
        }
        notifyNetworkConnected(true);
        sendBroadCastNotifyNetworkConnected("android.os.action.networkReconnectToInternet");
    }

    public void registerFeedBackListener(PowerKeeperInterface.e eVar) {
        synchronized (this.mCallbacks) {
            try {
                if (!this.mCallbacks.contains(eVar)) {
                    this.mCallbacks.add(eVar);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void unregisterFeedBackListener(PowerKeeperInterface.e eVar) {
        synchronized (this.mCallbacks) {
            try {
                if (this.mCallbacks.contains(eVar)) {
                    this.mCallbacks.remove(eVar);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
