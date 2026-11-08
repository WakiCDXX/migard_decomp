package com.miui.powerkeeper.utils;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.util.Slog;
import android.view.MotionEvent;
import com.android.internal.util.ArrayUtils;
import com.miui.powerkeeper.PowerKeeperManager;
import java.util.ArrayList;
import miui.process.IActivityChangeListener;
import miui.process.ProcessManager;
import miui.util.FeatureParser;

/* JADX INFO: loaded from: classes.dex */
public class DanmuScenarioUtils {
    private static final ArrayList<String> APP_ACTIVITY_LIST;
    private static final ArrayList<String> APP_PACKAGE_LIST;
    private static final int MSG_NOTIFY_FCM = 1;
    private static final int UISERVICE_TRANSACTION_FCM = 4;
    private static final String UI_SERVICE_NAME = "uiservice";
    private static String mCurActivityName;
    private static Boolean mIsFullScreenVideoActivity;
    private static Boolean mIsVideoActivity;
    private static DanmuScenarioUtils sInstance;
    private Context mContext;
    private Handler mHandler;
    private static final String TAG = "DanmuScenarioUtils";
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);
    private static final Rect fixedArea = new Rect(0, 1250, 800, 1400);
    private static final boolean SUPPORT_DANMU = FeatureParser.getBoolean("support_danmu", false);
    private TouchEventListener listener = new TouchEventListener() { // from class: com.miui.powerkeeper.utils.DanmuScenarioUtils.1
        @Override // com.miui.powerkeeper.utils.TouchEventListener
        public void onPointerEvent(MotionEvent motionEvent) {
            if (motionEvent.getAction() == 0) {
                int rawX = (int) motionEvent.getRawX();
                int rawY = (int) motionEvent.getRawY();
                if (DanmuScenarioUtils.mIsFullScreenVideoActivity.booleanValue()) {
                    if (DanmuScenarioUtils.fixedArea.contains(rawX, rawY)) {
                        DanmuScenarioUtils.this.mHandler.sendMessageDelayed(DanmuScenarioUtils.this.mHandler.obtainMessage(1), 100L);
                    } else {
                        DanmuScenarioUtils.this.mHandler.sendMessageDelayed(DanmuScenarioUtils.this.mHandler.obtainMessage(1), 1000L);
                    }
                }
            }
        }
    };
    private BroadcastReceiver mConfigurationChangeReceiver = new BroadcastReceiver() { // from class: com.miui.powerkeeper.utils.DanmuScenarioUtils.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.CONFIGURATION_CHANGED")) {
                int i2 = DanmuScenarioUtils.this.mContext.getResources().getConfiguration().orientation;
                if (i2 != 2) {
                    if (i2 == 1) {
                        Log.i(DanmuScenarioUtils.TAG, "mConfigurationChangeReceiver orientation = " + i2);
                        TouchEventMonitor.getInstance(DanmuScenarioUtils.this.mContext).unregisterPointerEventListener(DanmuScenarioUtils.this.listener);
                        DanmuScenarioUtils.mIsFullScreenVideoActivity = Boolean.FALSE;
                        return;
                    }
                    return;
                }
                Log.i(DanmuScenarioUtils.TAG, "mConfigurationChangeReceiver orientation = " + i2);
                TouchEventMonitor.getInstance(DanmuScenarioUtils.this.mContext).registerPointerEventListener(DanmuScenarioUtils.this.listener);
                if (DanmuScenarioUtils.mIsVideoActivity.booleanValue()) {
                    DanmuScenarioUtils.mIsFullScreenVideoActivity = Boolean.TRUE;
                    DanmuScenarioUtils.this.mHandler.sendMessageDelayed(DanmuScenarioUtils.this.mHandler.obtainMessage(1), 1000L);
                }
            }
        }
    };
    private IActivityChangeListener.Stub mActListener = new IActivityChangeListener.Stub() { // from class: com.miui.powerkeeper.utils.DanmuScenarioUtils.3
        public void onActivityChanged(ComponentName componentName, ComponentName componentName2) {
            DanmuScenarioUtils.mCurActivityName = componentName2.getClassName();
            String packageName = componentName2.getPackageName();
            if (DanmuScenarioUtils.mCurActivityName == null || packageName == null) {
                return;
            }
            if (ArrayUtils.contains(DanmuScenarioUtils.APP_ACTIVITY_LIST, DanmuScenarioUtils.mCurActivityName)) {
                DanmuScenarioUtils.mIsVideoActivity = Boolean.TRUE;
            } else {
                DanmuScenarioUtils.mIsVideoActivity = Boolean.FALSE;
            }
        }
    };

    private final class MyHandler extends Handler {
        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what != 1) {
                return;
            }
            DanmuScenarioUtils.this.notifyUIServiceFcm(true);
        }
    }

    static {
        Boolean bool = Boolean.FALSE;
        mIsVideoActivity = bool;
        mIsFullScreenVideoActivity = bool;
        ArrayList<String> arrayList = new ArrayList<>();
        APP_ACTIVITY_LIST = arrayList;
        arrayList.add("com.youku.ui.activity.DetailActivity");
        arrayList.add("com.tencent.qqlive.ona.activity.VideoDetailActivity");
        arrayList.add("org.iqiyi.video.activity.PlayerActivity");
        arrayList.add("tv.danmaku.bili.ui.video.VideoDetailsActivity");
        arrayList.add("com.bilibili.ship.theseus.detail.UnitedBizDetailsActivity");
        arrayList.add("com.bilibili.bangumi.ui.page.detail.BangumiDetailActivityV3");
        arrayList.add("com.bilibili.bililive.room.ui.roomv3.LiveRoomActivityV3");
        ArrayList<String> arrayList2 = new ArrayList<>();
        APP_PACKAGE_LIST = arrayList2;
        arrayList2.add("com.youku.phone");
        arrayList2.add("com.tencent.qqlive");
        arrayList2.add("com.qiyi.video");
        arrayList2.add("tv.danmaku.bili");
    }

    private DanmuScenarioUtils(Context context) {
        this.mContext = context;
    }

    public static DanmuScenarioUtils getInstance(Context context) {
        Log.i(TAG, "getInstance");
        if (sInstance == null) {
            synchronized (DanmuScenarioUtils.class) {
                try {
                    if (sInstance == null) {
                        sInstance = new DanmuScenarioUtils(context);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return sInstance;
    }

    public static boolean isDanmuSupported() {
        return SUPPORT_DANMU;
    }

    private void registerActivityChangeListener() {
        if (this.mActListener != null) {
            Log.d(TAG, "registerActivityChangeListener");
            ProcessManager.unregisterActivityChanageListener(this.mActListener);
            ProcessManager.registerActivityChangeListener(APP_PACKAGE_LIST, APP_ACTIVITY_LIST, this.mActListener);
        }
    }

    private void registerConfigurationChangeReceiver() {
        Log.i(TAG, "registerConfigurationChangeReceiver!");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.CONFIGURATION_CHANGED");
        this.mContext.registerReceiver(this.mConfigurationChangeReceiver, intentFilter, 2);
    }

    private void unregisterConfigurationChangeReceiver() {
        Log.i(TAG, "unregisterConfigurationChangeReceiver!");
        this.mContext.unregisterReceiver(this.mConfigurationChangeReceiver);
    }

    public void notifyUIServiceFcm(boolean z2) {
        IBinder service = ServiceManager.getService(UI_SERVICE_NAME);
        if (service == null) {
            Log.e(TAG, "null binder");
            return;
        }
        Parcel parcelObtain = Parcel.obtain();
        parcelObtain.writeInterfaceToken("com.android.server.ui.IUiService");
        parcelObtain.writeInt(z2 ? 1 : 0);
        try {
            service.transact(4, parcelObtain, null, 1);
        } catch (RemoteException e2) {
            Slog.e(TAG, "uiService abnormal=", e2);
        }
    }

    public void onCreate() {
        Log.i(TAG, "onCreate");
        this.mHandler = new MyHandler(PowerKeeperManager.getThread().getLooper());
        registerActivityChangeListener();
        registerConfigurationChangeReceiver();
    }
}
