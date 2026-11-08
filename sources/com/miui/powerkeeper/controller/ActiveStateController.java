package com.miui.powerkeeper.controller;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.ApplicationInfo;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import com.android.internal.util.IndentingPrintWriter;
import com.miui.powerkeeper.AppActiveChecker;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.UserCheckManager;
import com.miui.powerkeeper.provider.AppActiveConfigure;
import com.miui.powerkeeper.provider.PowerKeeperConfigure;
import com.miui.powerkeeper.provider.UserConfigure;
import com.miui.powerkeeper.provider.UserConfigureHelper;
import com.miui.powerkeeper.utils.PackageUtil;
import com.miui.powerkeeper.utils.VpnTracker;
import e.e;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class ActiveStateController {
    protected static final boolean DEBUG = Log.isLoggable("power.debug", 3);
    public static final String KEY_DELAY_PERIOD_MINUTE = "DELAY_PERIOD_MINUTE";
    public static final String KEY_POLICY = "POLICY";
    private static final int MSG_DELAY_TIMER_OUT = 1;
    public static final int POLICY_CONTROL_DISABLE_ALWAYS = 1;
    public static final int POLICY_CONTROL_ENABLE_ALWAYS = 0;
    public static final int POLICY_CONTROL_WITH_APP_ACTIVE = 3;
    public static final int POLICY_CONTROL_WITH_DELAY = 2;
    public static final int RULE_ALLOW_APP = 0;
    public static final int RULE_RESTRICT_APP = 1;
    protected static final int STATE_BACKGROUND_IN_TIMER = 1;
    protected static final int STATE_BACKGROUND_TIMEOUT = 2;
    protected static final int STATE_FOREGROUND = 0;
    private static final int TYPE_USER_APP_ID = 1;
    private static final UriMatcher sMatcher;
    protected String TAG;
    private boolean isEnabled;
    private PowerKeeperInterface.a mAlarmHappenCallback;
    private PowerKeeperInterface.i mAlarmHappenListener;
    private PowerKeeperInterface.b mAppActiveChangedCallback;
    private PowerKeeperInterface.j mAppActiveChangedListener;
    private AppActiveChecker mAppActiveChecker;
    private ContentObserver mContentObserver;
    private Context mContext;
    private PowerKeeperInterface.f mForegroundActivitiesChangedCallback;
    private Handler mHandler;
    private PowerKeeperInterface.w mIMEChangedCallback;
    private PowerKeeperInterface.v mIMEChangedListener;
    private final Object mLock;
    private MessageHandler mMessageHandler;
    private PowerKeeperInterface.d0 mPackageManagerCallback;
    private PowerKeeperInterface.x mPackageReceiver;
    private PowerKeeperManager mPowerKeeperManager;
    private PowerKeeperInterface.r mProcessObserver;
    private PowerKeeperInterface.f0 mScreenChangedCallback;
    private volatile boolean mScreenOn;
    private PowerKeeperInterface.z mScreenReceiver;
    private SparseArray<AppState> mUidAppState;
    private UserCheckManager mUserCheckManager;
    private SparseIntArray mUserIMEUid;
    private SparseBooleanArray mUserNoRestrictUids;
    private PowerKeeperInterface.g0 mUserStatusChangedCallback;
    private PowerKeeperInterface.b0 mUserStatusChangedListener;
    private VpnTracker.StatusCallback mVpnStatusCallback;
    private int[] mVpnUids;

    class AppState {
        static final long DEFAULT_PERIOD_MS_AFTER_BACKGROUND = 0;
        int appRunningState = 2;
        long periodMsAfterBackground;
        int policy;
        int property;
        int property_record;
        int uid;

        public AppState(int i2) {
            this.uid = i2;
            clearParam();
        }

        AppState backupParam() {
            AppState appState = ActiveStateController.this.new AppState(this.uid);
            appState.policy = this.policy;
            appState.periodMsAfterBackground = this.periodMsAfterBackground;
            return appState;
        }

        void clearParam() {
            this.policy = 1;
            this.periodMsAfterBackground = 0L;
            this.property_record = 0;
            this.property = 0;
        }

        boolean compareParam(AppState appState) {
            return this.policy == appState.policy && this.periodMsAfterBackground == appState.periodMsAfterBackground;
        }

        Bundle getParam() {
            Bundle bundle = new Bundle();
            bundle.putInt("POLICY", this.policy);
            long j2 = this.periodMsAfterBackground;
            if (j2 < 0) {
                bundle.putInt(ActiveStateController.KEY_DELAY_PERIOD_MINUTE, -1);
            } else {
                bundle.putInt(ActiveStateController.KEY_DELAY_PERIOD_MINUTE, (int) (j2 / 60000));
            }
            return bundle;
        }

        void resetStateToBACKGROUNDINTIMER() {
            this.appRunningState = 1;
        }

        void setParam(Bundle bundle) {
            int i2 = bundle.getInt("POLICY", 1);
            this.policy = i2;
            this.periodMsAfterBackground = 0L;
            if (i2 == 2) {
                if (bundle.containsKey(ActiveStateController.KEY_DELAY_PERIOD_MINUTE)) {
                    this.periodMsAfterBackground = bundle.getInt(ActiveStateController.KEY_DELAY_PERIOD_MINUTE) * 60000;
                }
                if (this.periodMsAfterBackground < 0) {
                    this.periodMsAfterBackground = -1L;
                }
            }
        }

        void setStateToBACKGROUNDTIMEOUT() {
            this.appRunningState = 2;
        }

        void setStateToFOREGROUND() {
            this.appRunningState = 0;
        }
    }

    class MessageHandler {
        private Handler handler;
        private SparseLongArray uidRealTimeArray = new SparseLongArray();

        MessageHandler(Handler handler) {
            this.handler = handler;
        }

        void onAlarm(long j2) {
            for (int size = this.uidRealTimeArray.size() - 1; size >= 0; size--) {
                int iKeyAt = this.uidRealTimeArray.keyAt(size);
                long jValueAt = this.uidRealTimeArray.valueAt(size);
                if (jValueAt <= j2) {
                    if (ActiveStateController.DEBUG) {
                        Log.d(ActiveStateController.this.TAG, "onAlarm: uid = " + iKeyAt + " elapsedRealtime = " + jValueAt);
                    }
                    AppState appStateLocked = ActiveStateController.this.getAppStateLocked(iKeyAt);
                    this.handler.removeMessages(1, appStateLocked);
                    this.handler.sendMessage(Message.obtain(this.handler, 1, appStateLocked));
                    this.uidRealTimeArray.delete(iKeyAt);
                }
            }
        }

        void onDestory() {
            removeCallbacksAndMessages();
            this.handler = null;
            this.uidRealTimeArray = null;
        }

        void removeCallbacksAndMessages() {
            this.handler.removeCallbacksAndMessages(null);
            this.uidRealTimeArray.clear();
        }

        void removeMessages(int i2) {
            AppState appStateLocked = ActiveStateController.this.getAppStateLocked(i2);
            if (this.handler.hasMessages(1, appStateLocked) && ActiveStateController.DEBUG) {
                Log.d(ActiveStateController.this.TAG, "removeMessages: hasMessage, uid = " + i2);
            }
            this.handler.removeMessages(1, appStateLocked);
            this.uidRealTimeArray.delete(i2);
        }

        void sendMessageDelayed(int i2, long j2) {
            AppState appStateLocked = ActiveStateController.this.getAppStateLocked(i2);
            if (j2 <= 0) {
                this.handler.sendMessage(Message.obtain(this.handler, 1, appStateLocked));
                return;
            }
            this.handler.sendMessageDelayed(Message.obtain(this.handler, 1, appStateLocked), j2);
            long jElapsedRealtime = SystemClock.elapsedRealtime() + j2;
            this.uidRealTimeArray.put(i2, jElapsedRealtime);
            if (ActiveStateController.DEBUG) {
                Log.d(ActiveStateController.this.TAG, "sendMessageDelayed: uid = " + i2 + " delayMillis = " + j2 + " elapsedRealtime = " + jElapsedRealtime);
            }
        }
    }

    static {
        UriMatcher uriMatcher = new UriMatcher(-1);
        sMatcher = uriMatcher;
        uriMatcher.addURI(PowerKeeperConfigure.AUTHORITY, "userTable/#", 1);
    }

    public ActiveStateController(Context context, PowerKeeperManager powerKeeperManager) {
        this.TAG = ActiveStateController.class.getSimpleName();
        this.mLock = new Object();
        this.isEnabled = false;
        this.mUidAppState = new SparseArray<>();
        this.mUserIMEUid = new SparseIntArray();
        this.mUserNoRestrictUids = new SparseBooleanArray();
        this.mContentObserver = new ContentObserver(null) { // from class: com.miui.powerkeeper.controller.ActiveStateController.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z2, Uri uri) {
                ActiveStateController.this.onContentChange(z2, uri);
            }
        };
        this.mVpnStatusCallback = new VpnTracker.StatusCallback() { // from class: com.miui.powerkeeper.controller.ActiveStateController.4
            @Override // com.miui.powerkeeper.utils.VpnTracker.StatusCallback
            public void onConnected(final int i2) {
                ActiveStateController.this.mHandler.post(new Runnable() { // from class: com.miui.powerkeeper.controller.ActiveStateController.4.1
                    @Override // java.lang.Runnable
                    public void run() {
                        synchronized (ActiveStateController.this.mLock) {
                            try {
                                if (!ActiveStateController.this.isEnabled) {
                                    if (ActiveStateController.DEBUG) {
                                        Log.v(ActiveStateController.this.TAG, "still disabled");
                                    }
                                    return;
                                }
                                ActiveStateController.this.getAppStateLocked(i2).property |= 4;
                                ActiveStateController activeStateController = ActiveStateController.this;
                                activeStateController.updateRuleForUidLocked(activeStateController.getAppStateLocked(i2));
                                f.c cVar = (f.c) e.i(1073741826);
                                Log.i(ActiveStateController.this.TAG, "Vpn connect, uid is " + i2 + ", dfsPower is " + cVar);
                                if (cVar != null && cVar.c() != null && cVar.c().getPowerUsageMonitor() != null) {
                                    Log.i(ActiveStateController.this.TAG, "notify vpn Connect uid is " + i2);
                                    cVar.c().getPowerUsageMonitor().notifyVpnConnect(i2, 1);
                                }
                            } catch (Throwable th) {
                                throw th;
                            }
                        }
                    }
                });
            }

            @Override // com.miui.powerkeeper.utils.VpnTracker.StatusCallback
            public void onDisconnected(final int i2) {
                ActiveStateController.this.mHandler.post(new Runnable() { // from class: com.miui.powerkeeper.controller.ActiveStateController.4.2
                    @Override // java.lang.Runnable
                    public void run() {
                        synchronized (ActiveStateController.this.mLock) {
                            try {
                                if (!ActiveStateController.this.isEnabled) {
                                    if (ActiveStateController.DEBUG) {
                                        Log.v(ActiveStateController.this.TAG, "still disabled");
                                    }
                                    return;
                                }
                                ActiveStateController.this.getAppStateLocked(i2).property &= -5;
                                ActiveStateController activeStateController = ActiveStateController.this;
                                activeStateController.updateRuleForUidLocked(activeStateController.getAppStateLocked(i2));
                                f.c cVar = (f.c) e.i(1073741826);
                                Log.i(ActiveStateController.this.TAG, "Vpn disconnect, uid is " + i2 + ", dfsPower is " + cVar);
                                if (cVar != null && cVar.c() != null && cVar.c().getPowerUsageMonitor() != null) {
                                    Log.i(ActiveStateController.this.TAG, "notify vpn disconnect uid is " + i2);
                                    cVar.c().getPowerUsageMonitor().notifyVpnConnect(i2, 0);
                                }
                            } catch (Throwable th) {
                                throw th;
                            }
                        }
                    }
                });
            }
        };
        this.mIMEChangedListener = new PowerKeeperInterface.v() { // from class: com.miui.powerkeeper.controller.ActiveStateController.5
            @Override // com.miui.powerkeeper.PowerKeeperInterface.v
            public void onIMEChange(int i2, int i3) {
                synchronized (ActiveStateController.this.mLock) {
                    try {
                        if (!ActiveStateController.this.isEnabled) {
                            if (ActiveStateController.DEBUG) {
                                Log.v(ActiveStateController.this.TAG, "still disabled");
                            }
                            return;
                        }
                        int i4 = ActiveStateController.this.mUserIMEUid.get(i2, -1);
                        if (i4 != -1) {
                            Log.i(ActiveStateController.this.TAG, "Ime outdated, uid is " + i4);
                            AppState appStateLocked = ActiveStateController.this.getAppStateLocked(i4);
                            appStateLocked.property = appStateLocked.property & (-9);
                            ActiveStateController activeStateController = ActiveStateController.this;
                            activeStateController.updateRuleForUidLocked(activeStateController.getAppStateLocked(i4));
                        }
                        if (i3 != -1) {
                            ActiveStateController.this.mUserIMEUid.put(i2, i3);
                            ActiveStateController.this.getAppStateLocked(i3).property |= 8;
                            ActiveStateController activeStateController2 = ActiveStateController.this;
                            activeStateController2.updateRuleForUidLocked(activeStateController2.getAppStateLocked(i3));
                            Log.i(ActiveStateController.this.TAG, "Ime update, uid is " + i3);
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        };
        this.mProcessObserver = new PowerKeeperInterface.r() { // from class: com.miui.powerkeeper.controller.ActiveStateController.7
            @Override // com.miui.powerkeeper.PowerKeeperInterface.r
            public void onForegroundActivitiesChanged(int i2, boolean z2) {
                synchronized (ActiveStateController.this.mLock) {
                    try {
                        if (ActiveStateController.this.isEnabled) {
                            ActiveStateController.this.updateStateForFgUidChangedLocked(i2, z2);
                        } else {
                            if (ActiveStateController.DEBUG) {
                                Log.v(ActiveStateController.this.TAG, "still disabled");
                            }
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        };
        this.mScreenReceiver = new PowerKeeperInterface.z() { // from class: com.miui.powerkeeper.controller.ActiveStateController.8
            @Override // com.miui.powerkeeper.PowerKeeperInterface.z
            public void onRegistered(boolean z2) {
                synchronized (ActiveStateController.this.mLock) {
                    try {
                        if (ActiveStateController.this.isEnabled) {
                            if (ActiveStateController.this.mScreenOn != ActiveStateController.this.mPowerKeeperManager.isScreenOn()) {
                                ActiveStateController.this.updateForScreenChangedLocked();
                            }
                        } else {
                            if (ActiveStateController.DEBUG) {
                                Log.v(ActiveStateController.this.TAG, "still disabled");
                            }
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }

            @Override // com.miui.powerkeeper.PowerKeeperInterface.z
            public void onScreenChanged(boolean z2) {
                synchronized (ActiveStateController.this.mLock) {
                    try {
                        if (ActiveStateController.this.isEnabled) {
                            if (ActiveStateController.this.mScreenOn != ActiveStateController.this.mPowerKeeperManager.isScreenOn()) {
                                ActiveStateController.this.updateForScreenChangedLocked();
                            }
                        } else {
                            if (ActiveStateController.DEBUG) {
                                Log.v(ActiveStateController.this.TAG, "still disabled");
                            }
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        };
        this.mPackageReceiver = new PowerKeeperInterface.x() { // from class: com.miui.powerkeeper.controller.ActiveStateController.9
            @Override // com.miui.powerkeeper.PowerKeeperInterface.x
            public void onUidRemoved(int i2, int i3) {
                synchronized (ActiveStateController.this.mLock) {
                    try {
                        if (ActiveStateController.this.isEnabled) {
                            ActiveStateController.this.updateForUidRemovedLocked(i3);
                        } else {
                            if (ActiveStateController.DEBUG) {
                                Log.v(ActiveStateController.this.TAG, "still disabled");
                            }
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }

            @Override // com.miui.powerkeeper.PowerKeeperInterface.x
            public void onRegistered() {
            }

            @Override // com.miui.powerkeeper.PowerKeeperInterface.x
            public void onUserReadyPackageAdded(int i2, Map<String, Integer> map) {
            }

            @Override // com.miui.powerkeeper.PowerKeeperInterface.x
            public void onPackageAdded(int i2, String str, int i3) {
            }

            @Override // com.miui.powerkeeper.PowerKeeperInterface.x
            public void onPackageRemoved(int i2, String str, int i3) {
            }
        };
        this.mAlarmHappenListener = new PowerKeeperInterface.i() { // from class: com.miui.powerkeeper.controller.ActiveStateController.10
            @Override // com.miui.powerkeeper.PowerKeeperInterface.i
            public void onAlarmHappen(long j2) {
                synchronized (ActiveStateController.this.mLock) {
                    try {
                        if (!ActiveStateController.this.isEnabled) {
                            if (ActiveStateController.DEBUG) {
                                Log.v(ActiveStateController.this.TAG, "still disabled");
                            }
                            return;
                        }
                        if (ActiveStateController.DEBUG) {
                            Log.d(ActiveStateController.this.TAG, "onAlarmHappen, elapsedRealtime = " + j2 + "ms");
                        }
                        ActiveStateController.this.mMessageHandler.onAlarm(j2);
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        };
        this.mAppActiveChangedListener = new PowerKeeperInterface.j() { // from class: com.miui.powerkeeper.controller.ActiveStateController.11
            @Override // com.miui.powerkeeper.PowerKeeperInterface.j
            public void onAppActiveChange(int i2, boolean z2) {
                synchronized (ActiveStateController.this.mLock) {
                    try {
                        if (ActiveStateController.this.isEnabled) {
                            ActiveStateController.this.updateStateForUidActiveChangedLocked(i2, z2);
                        } else {
                            if (ActiveStateController.DEBUG) {
                                Log.v(ActiveStateController.this.TAG, "still disabled");
                            }
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        };
        this.mUserStatusChangedListener = new PowerKeeperInterface.b0() { // from class: com.miui.powerkeeper.controller.ActiveStateController.12
            @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
            public void onUserEnableStatusChange(int i2, boolean z2) {
                synchronized (ActiveStateController.this.mLock) {
                    try {
                        if (!ActiveStateController.this.isEnabled) {
                            if (ActiveStateController.DEBUG) {
                                Log.v(ActiveStateController.this.TAG, "still disabled");
                            }
                        } else {
                            if (!z2) {
                                ActiveStateController.this.updateForUserRemovedLocked(i2);
                                ActiveStateController.this.mUserIMEUid.delete(i2);
                            }
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }

            @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
            public void onUserRunningStatusChange(int i2, boolean z2) {
                synchronized (ActiveStateController.this.mLock) {
                    try {
                        if (!ActiveStateController.this.isEnabled) {
                            if (ActiveStateController.DEBUG) {
                                Log.v(ActiveStateController.this.TAG, "still disabled");
                            }
                        } else {
                            if (!z2) {
                                ActiveStateController.this.updateForUserRemovedLocked(i2);
                                ActiveStateController.this.mUserIMEUid.delete(i2);
                            }
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }

            @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
            public void onXSpaceStatusChange(int i2, boolean z2) {
                synchronized (ActiveStateController.this.mLock) {
                    try {
                        if (ActiveStateController.this.isEnabled) {
                            if (!z2) {
                                ActiveStateController.this.updateForUserRemovedLocked(i2);
                            }
                        } else {
                            if (ActiveStateController.DEBUG) {
                                Log.v(ActiveStateController.this.TAG, "still disabled");
                            }
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }

            @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
            public void onUserForegroundStatusChange(int i2, boolean z2) {
            }
        };
        this.mContext = context;
        this.mPowerKeeperManager = powerKeeperManager;
        this.mUserCheckManager = powerKeeperManager.getUserCheckManager();
    }

    private void clearAppActiveTable() {
        this.mContext.getContentResolverForUser(UserHandle.OWNER).delete(AppActiveConfigure.CONTENT_URI, null, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public AppState getAppStateLocked(int i2) {
        if (this.mUidAppState.get(i2) == null) {
            this.mUidAppState.put(i2, new AppState(i2));
        }
        return this.mUidAppState.get(i2);
    }

    private boolean isUidActive(int i2) {
        return this.mAppActiveChecker.y(i2);
    }

    private boolean isUidForegroundLocked(int i2) {
        return this.mPowerKeeperManager.isUidForeground(i2);
    }

    private boolean isUidValidForRules(int i2) {
        return UserHandle.isApp(i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onContentChange(boolean z2, final Uri uri) {
        this.mHandler.post(new Runnable() { // from class: com.miui.powerkeeper.controller.ActiveStateController.6
            @Override // java.lang.Runnable
            public void run() {
                if (!ActiveStateController.this.isEnabled) {
                    if (ActiveStateController.DEBUG) {
                        Log.v(ActiveStateController.this.TAG, "still disabled");
                        return;
                    }
                    return;
                }
                if (ActiveStateController.sMatcher.match(uri) != 1) {
                    return;
                }
                synchronized (ActiveStateController.this.mLock) {
                    try {
                        Cursor cursorQuery = ActiveStateController.this.mContext.getContentResolverForUser(UserHandle.OWNER).query(uri, null, null, null, null);
                        if (cursorQuery != null && cursorQuery.getCount() != 0) {
                            UserConfigureHelper userConfigureHelperCreateFirstFromTable = UserConfigureHelper.createFirstFromTable(cursorQuery);
                            String packageName = userConfigureHelperCreateFirstFromTable.getPackageName();
                            int userId = userConfigureHelperCreateFirstFromTable.getUserId();
                            cursorQuery.getColumnIndex(UserConfigure.Columns.BG_CONTROL);
                            int uidByPackageName = PackageUtil.getUidByPackageName(ActiveStateController.this.mContext, packageName, userId);
                            boolean zEquals = userConfigureHelperCreateFirstFromTable.getBgControl().equals(UserConfigure.BG_CONTROL_NO_RESTRICT);
                            if (zEquals && ActiveStateController.this.mUserNoRestrictUids.indexOfKey(uidByPackageName) < 0) {
                                ActiveStateController.this.mUserNoRestrictUids.put(uidByPackageName, true);
                                ActiveStateController.this.getAppStateLocked(uidByPackageName).property |= 2;
                                ActiveStateController activeStateController = ActiveStateController.this;
                                activeStateController.updateRuleForUidLocked(activeStateController.getAppStateLocked(uidByPackageName));
                            } else if (!zEquals && ActiveStateController.this.mUserNoRestrictUids.indexOfKey(uidByPackageName) >= 0) {
                                ActiveStateController.this.getAppStateLocked(uidByPackageName).property &= -3;
                                ActiveStateController activeStateController2 = ActiveStateController.this;
                                activeStateController2.updateRuleForUidLocked(activeStateController2.getAppStateLocked(uidByPackageName));
                                ActiveStateController.this.mUserNoRestrictUids.delete(uidByPackageName);
                            }
                        }
                        if (cursorQuery != null) {
                            cursorQuery.close();
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean onMessageHandler(Message message) {
        if (message.what != 1) {
            return false;
        }
        int i2 = ((AppState) message.obj).uid;
        synchronized (this.mLock) {
            try {
                if (this.isEnabled) {
                    updateStateForTimeOutLocked(i2);
                    return true;
                }
                if (DEBUG) {
                    Log.v(this.TAG, "still disabled");
                }
                return true;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private void removeUidState(int i2) {
        this.mContext.getContentResolverForUser(UserHandle.OWNER).delete(ContentUris.withAppendedId(AppActiveConfigure.CONTENT_URI, i2), null, null);
    }

    private void setUidState(int i2, int i3, boolean z2) {
        ContentResolver contentResolverForUser = this.mContext.getContentResolverForUser(UserHandle.OWNER);
        ContentValues contentValues = new ContentValues();
        contentValues.put("uid", Integer.valueOf(i2));
        contentValues.put(AppActiveConfigure.Columns.PROPERTY, Integer.valueOf(i3));
        if (z2) {
            contentValues.put(AppActiveConfigure.Columns.ACTIVE, "true");
        } else {
            contentValues.put(AppActiveConfigure.Columns.ACTIVE, "false");
        }
        contentResolverForUser.insert(AppActiveConfigure.CONTENT_URI, contentValues);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateForScreenChangedLocked() {
        this.mScreenOn = this.mPowerKeeperManager.isScreenOn();
        if (DEBUG) {
            Log.d(this.TAG, "updateForScreenChangedLocked, mScreenOn = " + this.mScreenOn);
        }
        int size = this.mUidAppState.size();
        for (int i2 = 0; i2 < size; i2++) {
            updateStateForScreenChangedLocked(this.mUidAppState.keyAt(i2));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateForUidRemovedLocked(int i2) {
        if (DEBUG) {
            Log.v(this.TAG, "updateForUidRemovedLocked, uid=" + i2);
        }
        this.mMessageHandler.removeMessages(i2);
        this.mUidAppState.remove(i2);
        removeUidState(i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateForUserRemovedLocked(int i2) {
        if (DEBUG) {
            Log.v(this.TAG, "updateForUserRemovedLocked, userId=" + i2);
        }
        SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
        int size = this.mUidAppState.size();
        for (int i3 = 0; i3 < size; i3++) {
            if (UserHandle.getUserId(this.mUidAppState.keyAt(i3)) == i2) {
                sparseBooleanArray.put(this.mUidAppState.keyAt(i3), true);
            }
        }
        for (int i4 = 0; i4 < sparseBooleanArray.size(); i4++) {
            updateForUidRemovedLocked(sparseBooleanArray.keyAt(i4));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateRuleForUidLocked(AppState appState) {
        int i2 = appState.uid;
        boolean z2 = true;
        if (appState.appRunningState == 1) {
            appState.property |= 32;
        } else {
            appState.property &= -33;
        }
        if (appState.property_record != appState.property) {
            if (DEBUG) {
                Log.d(this.TAG, "updateRuleForUidLocked, uid = " + i2 + " property = " + appState.property);
            }
            int i3 = appState.property;
            appState.property_record = i3;
            if ((i3 & 8) == 0 && (i3 & 16) == 0 && (i3 & 4) == 0 && (i3 & 32) == 0) {
                z2 = false;
            }
            setUidState(i2, i3, z2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateStateForFgUidChangedLocked(int i2, boolean z2) {
        if (isUidValidForRules(i2)) {
            if (DEBUG) {
                Log.d(this.TAG, "updateStateForFgUidChangedLocked: uid = " + i2 + " foreground = " + z2);
            }
            AppState appStateLocked = getAppStateLocked(i2);
            if (!this.mScreenOn) {
                int i3 = appStateLocked.appRunningState;
                if (i3 == 0) {
                    this.mMessageHandler.removeMessages(i2);
                    int i4 = appStateLocked.policy;
                    if (i4 == 2) {
                        if (appStateLocked.periodMsAfterBackground > 0) {
                            appStateLocked.resetStateToBACKGROUNDINTIMER();
                            this.mMessageHandler.sendMessageDelayed(i2, appStateLocked.periodMsAfterBackground);
                        } else {
                            appStateLocked.setStateToBACKGROUNDTIMEOUT();
                        }
                    } else if (i4 == 3 && isUidActive(i2)) {
                        appStateLocked.resetStateToBACKGROUNDINTIMER();
                    } else {
                        appStateLocked.setStateToBACKGROUNDTIMEOUT();
                    }
                } else if (i3 == 1) {
                    int i5 = appStateLocked.policy;
                    if (i5 == 2) {
                        if (appStateLocked.periodMsAfterBackground < 0) {
                            this.mMessageHandler.removeMessages(i2);
                            appStateLocked.setStateToBACKGROUNDTIMEOUT();
                        }
                    } else if (i5 != 3 || !isUidActive(i2)) {
                        appStateLocked.setStateToBACKGROUNDTIMEOUT();
                    }
                }
            } else if (z2) {
                this.mMessageHandler.removeMessages(i2);
                appStateLocked.setStateToFOREGROUND();
            } else {
                int i6 = appStateLocked.appRunningState;
                if (i6 == 0) {
                    this.mMessageHandler.removeMessages(i2);
                    int i7 = appStateLocked.policy;
                    if (i7 == 2) {
                        long j2 = appStateLocked.periodMsAfterBackground;
                        if (j2 > 0) {
                            appStateLocked.resetStateToBACKGROUNDINTIMER();
                            this.mMessageHandler.sendMessageDelayed(i2, appStateLocked.periodMsAfterBackground);
                        } else if (j2 == 0) {
                            appStateLocked.setStateToBACKGROUNDTIMEOUT();
                        } else {
                            appStateLocked.resetStateToBACKGROUNDINTIMER();
                        }
                    } else if (i7 == 3 && isUidActive(i2)) {
                        appStateLocked.resetStateToBACKGROUNDINTIMER();
                    } else {
                        appStateLocked.setStateToBACKGROUNDTIMEOUT();
                    }
                } else if (i6 == 1 && appStateLocked.policy == 3 && !isUidActive(i2)) {
                    appStateLocked.setStateToBACKGROUNDTIMEOUT();
                }
            }
            updateRuleForUidLocked(appStateLocked);
        }
    }

    private void updateStateForScreenChangedLocked(int i2) {
        if (isUidValidForRules(i2)) {
            AppState appStateLocked = getAppStateLocked(i2);
            if (!this.mScreenOn) {
                int i3 = appStateLocked.appRunningState;
                if (i3 == 0) {
                    this.mMessageHandler.removeMessages(i2);
                    int i4 = appStateLocked.policy;
                    if (i4 == 2) {
                        if (appStateLocked.periodMsAfterBackground > 0) {
                            appStateLocked.resetStateToBACKGROUNDINTIMER();
                            this.mMessageHandler.sendMessageDelayed(i2, appStateLocked.periodMsAfterBackground);
                        } else {
                            appStateLocked.setStateToBACKGROUNDTIMEOUT();
                        }
                    } else if (i4 == 3 && isUidActive(i2)) {
                        appStateLocked.resetStateToBACKGROUNDINTIMER();
                    } else {
                        appStateLocked.setStateToBACKGROUNDTIMEOUT();
                    }
                } else if (i3 == 1) {
                    int i5 = appStateLocked.policy;
                    if (i5 == 2) {
                        if (appStateLocked.periodMsAfterBackground < 0) {
                            this.mMessageHandler.removeMessages(i2);
                            appStateLocked.setStateToBACKGROUNDTIMEOUT();
                        }
                    } else if (i5 != 3 || !isUidActive(i2)) {
                        appStateLocked.setStateToBACKGROUNDTIMEOUT();
                    }
                }
            } else if (isUidForegroundLocked(i2)) {
                this.mMessageHandler.removeMessages(i2);
                appStateLocked.setStateToFOREGROUND();
            }
            updateRuleForUidLocked(appStateLocked);
        }
    }

    private void updateStateForTimeOutLocked(int i2) {
        AppState appStateLocked = getAppStateLocked(i2);
        this.mMessageHandler.removeMessages(i2);
        if (appStateLocked.policy == 2 && appStateLocked.appRunningState == 1) {
            if (DEBUG) {
                Log.d(this.TAG, "updateStateForTimeOutLocked, uid: " + i2);
            }
            appStateLocked.setStateToBACKGROUNDTIMEOUT();
            updateRuleForUidLocked(appStateLocked);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateStateForUidActiveChangedLocked(int i2, boolean z2) {
        if (isUidValidForRules(i2)) {
            AppState appStateLocked = getAppStateLocked(i2);
            if (appStateLocked.policy != 3 || appStateLocked.appRunningState == 0) {
                return;
            }
            if (DEBUG) {
                Log.d(this.TAG, "updateStateForUidActiveChangedLocked, uid: " + i2 + " active: " + z2);
            }
            if (z2) {
                appStateLocked.resetStateToBACKGROUNDINTIMER();
            } else {
                appStateLocked.setStateToBACKGROUNDTIMEOUT();
            }
            updateRuleForUidLocked(appStateLocked);
        }
    }

    private void updateStateForUidResetPolicyLocked(int i2) {
        if (isUidValidForRules(i2)) {
            if (DEBUG) {
                Log.d(this.TAG, "updateStateForUidResetPolicyLocked, uid = " + i2);
            }
            boolean zIsUidForegroundLocked = isUidForegroundLocked(i2);
            AppState appStateLocked = getAppStateLocked(i2);
            this.mMessageHandler.removeMessages(i2);
            if (!this.mScreenOn) {
                int i3 = appStateLocked.appRunningState;
                if (i3 == 0 || i3 == 1) {
                    int i4 = appStateLocked.policy;
                    if (i4 == 2) {
                        if (appStateLocked.periodMsAfterBackground > 0) {
                            appStateLocked.resetStateToBACKGROUNDINTIMER();
                            this.mMessageHandler.sendMessageDelayed(i2, appStateLocked.periodMsAfterBackground);
                        } else {
                            appStateLocked.setStateToBACKGROUNDTIMEOUT();
                        }
                    } else if (i4 == 3 && isUidActive(i2)) {
                        appStateLocked.resetStateToBACKGROUNDINTIMER();
                    } else {
                        appStateLocked.setStateToBACKGROUNDTIMEOUT();
                    }
                } else if (i3 == 2) {
                    appStateLocked.setStateToBACKGROUNDTIMEOUT();
                }
            } else if (zIsUidForegroundLocked) {
                appStateLocked.setStateToFOREGROUND();
            } else {
                int i5 = appStateLocked.appRunningState;
                if (i5 == 0 || i5 == 1) {
                    int i6 = appStateLocked.policy;
                    if (i6 == 2) {
                        long j2 = appStateLocked.periodMsAfterBackground;
                        if (j2 > 0) {
                            appStateLocked.resetStateToBACKGROUNDINTIMER();
                            this.mMessageHandler.sendMessageDelayed(i2, appStateLocked.periodMsAfterBackground);
                        } else if (j2 == 0) {
                            appStateLocked.setStateToBACKGROUNDTIMEOUT();
                        } else {
                            appStateLocked.resetStateToBACKGROUNDINTIMER();
                        }
                    } else if (i6 == 3 && isUidActive(i2)) {
                        appStateLocked.resetStateToBACKGROUNDINTIMER();
                    } else {
                        appStateLocked.setStateToBACKGROUNDTIMEOUT();
                    }
                } else if (i5 == 2) {
                    appStateLocked.setStateToBACKGROUNDTIMEOUT();
                }
            }
            updateRuleForUidLocked(appStateLocked);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateUidPolicyUnchecked(int i2, Bundle bundle) {
        boolean z2 = DEBUG;
        if (z2) {
            Log.d(this.TAG, "updateUidPolicyUnchecked, uid: " + i2 + " bundle: " + bundle);
        }
        synchronized (this.mLock) {
            try {
                if (!this.isEnabled) {
                    if (z2) {
                        Log.v(this.TAG, "updateUidPolicyUnchecked, still disabled");
                    }
                    return;
                }
                AppState appStateLocked = getAppStateLocked(i2);
                AppState appStateBackupParam = appStateLocked.backupParam();
                appStateLocked.setParam(bundle);
                if (!appStateLocked.compareParam(appStateBackupParam)) {
                    updateStateForUidResetPolicyLocked(i2);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void disable() {
        synchronized (this.mLock) {
            try {
                if (!this.isEnabled) {
                    if (DEBUG) {
                        Log.v(this.TAG, "still disabled");
                    }
                    return;
                }
                Log.i(this.TAG, "disable");
                this.isEnabled = false;
                this.mUidAppState.clear();
                clearAppActiveTable();
                ContentResolver contentResolverForUser = this.mContext.getContentResolverForUser(UserHandle.OWNER);
                this.mUserIMEUid.clear();
                this.mUserNoRestrictUids.clear();
                this.mPowerKeeperManager.unregisterVpnStatusCallback(this.mVpnStatusCallback);
                this.mPowerKeeperManager.unregisterIMEChangeListener(this.mIMEChangedCallback);
                contentResolverForUser.unregisterContentObserver(this.mContentObserver);
                this.mIMEChangedCallback.a();
                this.mIMEChangedCallback = null;
                this.mUserCheckManager.K(this.mUserStatusChangedCallback);
                this.mAppActiveChecker.L(this.mAppActiveChangedCallback);
                this.mPowerKeeperManager.unregisterAlarmListener(this.mAlarmHappenCallback);
                this.mPowerKeeperManager.unregisterForegroundActivitiesChangedListener(this.mForegroundActivitiesChangedCallback);
                this.mPowerKeeperManager.unregisterScreenChangedListener(this.mScreenChangedCallback);
                this.mPowerKeeperManager.unregisterPackageChangedListener(this.mPackageManagerCallback);
                this.mPackageManagerCallback.a();
                this.mPackageManagerCallback = null;
                this.mScreenChangedCallback.a();
                this.mScreenChangedCallback = null;
                this.mForegroundActivitiesChangedCallback.a();
                this.mForegroundActivitiesChangedCallback = null;
                this.mAlarmHappenCallback.a();
                this.mAlarmHappenCallback = null;
                this.mAppActiveChangedCallback.a();
                this.mAppActiveChangedCallback = null;
                this.mUserStatusChangedCallback.a();
                this.mUserStatusChangedCallback = null;
                this.mAppActiveChecker = null;
                this.mMessageHandler.onDestory();
                this.mMessageHandler = null;
                this.mHandler.removeCallbacksAndMessages(null);
                this.mHandler = null;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        indentingPrintWriter.println("#######dump Active State Info#######");
        Cursor cursorQuery = this.mContext.getContentResolverForUser(UserHandle.OWNER).query(AppActiveConfigure.CONTENT_URI, new String[]{"uid", AppActiveConfigure.Columns.PROPERTY, AppActiveConfigure.Columns.ACTIVE}, null, null, null);
        while (cursorQuery != null && cursorQuery.moveToNext()) {
            int columnIndex = cursorQuery.getColumnIndex("uid");
            int columnIndex2 = cursorQuery.getColumnIndex(AppActiveConfigure.Columns.PROPERTY);
            int columnIndex3 = cursorQuery.getColumnIndex(AppActiveConfigure.Columns.ACTIVE);
            int i2 = cursorQuery.getInt(columnIndex);
            int i3 = cursorQuery.getInt(columnIndex2);
            String string = cursorQuery.getString(columnIndex3);
            indentingPrintWriter.print("uid: " + i2);
            indentingPrintWriter.print(" property: 0b" + Integer.toBinaryString(i3));
            indentingPrintWriter.print(" active: " + string);
            indentingPrintWriter.println();
        }
        if (cursorQuery != null) {
            cursorQuery.close();
        }
        indentingPrintWriter.println("end#######dump Active State Info#######end");
    }

    public void enable() {
        synchronized (this.mLock) {
            try {
                if (this.isEnabled) {
                    if (DEBUG) {
                        Log.v(this.TAG, "already enabled");
                    }
                    return;
                }
                Log.i(this.TAG, "enable");
                this.isEnabled = true;
                this.mUidAppState.clear();
                clearAppActiveTable();
                this.mAppActiveChecker = this.mPowerKeeperManager.getAppActiveChecker();
                Handler handler = new Handler(PowerKeeperManager.getThread().getLooper(), new Handler.Callback() { // from class: com.miui.powerkeeper.controller.ActiveStateController.2
                    @Override // android.os.Handler.Callback
                    public boolean handleMessage(Message message) {
                        return ActiveStateController.this.onMessageHandler(message);
                    }
                });
                this.mHandler = handler;
                this.mMessageHandler = new MessageHandler(handler);
                updateForScreenChangedLocked();
                ContentResolver contentResolverForUser = this.mContext.getContentResolverForUser(UserHandle.OWNER);
                this.mPackageManagerCallback = new PowerKeeperInterface.d0(this.mHandler, this.mPackageReceiver);
                this.mScreenChangedCallback = new PowerKeeperInterface.f0(this.mHandler, this.mScreenReceiver);
                this.mForegroundActivitiesChangedCallback = new PowerKeeperInterface.f(this.mHandler, this.mProcessObserver);
                this.mAlarmHappenCallback = new PowerKeeperInterface.a(this.mHandler, this.mAlarmHappenListener);
                this.mAppActiveChangedCallback = new PowerKeeperInterface.b(this.mHandler, this.mAppActiveChangedListener);
                this.mUserStatusChangedCallback = new PowerKeeperInterface.g0(this.mHandler, this.mUserStatusChangedListener);
                this.mPowerKeeperManager.registerPackageChangedListener(this.mPackageManagerCallback);
                this.mPowerKeeperManager.registerScreenChangedListener(this.mScreenChangedCallback);
                this.mPowerKeeperManager.registerForegroundActivitiesChangedListener(this.mForegroundActivitiesChangedCallback);
                this.mPowerKeeperManager.registerAlarmListener(this.mAlarmHappenCallback);
                this.mAppActiveChecker.I(this.mAppActiveChangedCallback);
                this.mUserCheckManager.J(this.mUserStatusChangedCallback);
                this.mIMEChangedCallback = new PowerKeeperInterface.w(this.mHandler, this.mIMEChangedListener);
                this.mPowerKeeperManager.registerVpnStatusCallback(this.mVpnStatusCallback);
                this.mPowerKeeperManager.registerIMEChangeListener(this.mIMEChangedCallback);
                contentResolverForUser.registerContentObserver(UserConfigure.CONTENT_URI, true, this.mContentObserver);
                int[] currentVpn = this.mPowerKeeperManager.getCurrentVpn();
                this.mVpnUids = currentVpn;
                for (int i2 : currentVpn) {
                    if (i2 > 0) {
                        getAppStateLocked(i2).property |= 4;
                    }
                }
                for (int i3 : this.mUserCheckManager.x()) {
                    int currentIME = this.mPowerKeeperManager.getCurrentIME(i3);
                    if (currentIME > 0) {
                        this.mUserIMEUid.put(i3, currentIME);
                        getAppStateLocked(currentIME).property |= 8;
                    }
                }
                for (ApplicationInfo applicationInfo : PackageUtil.getAllApps(this.mContext)) {
                    if (PackageUtil.isSystemApp(this.mContext, applicationInfo.packageName)) {
                        getAppStateLocked(applicationInfo.uid).property |= 1;
                    }
                }
                this.mUserNoRestrictUids.clear();
                Cursor cursorQuery = contentResolverForUser.query(UserConfigure.CONTENT_URI, null, null, null, null);
                while (cursorQuery != null && cursorQuery.moveToNext()) {
                    int columnIndex = cursorQuery.getColumnIndex("pkgName");
                    int columnIndex2 = cursorQuery.getColumnIndex(UserConfigure.Columns.BG_CONTROL);
                    int uidByPackageName = PackageUtil.getUidByPackageName(this.mContext, cursorQuery.getString(columnIndex));
                    if (!cursorQuery.isNull(columnIndex2) && cursorQuery.getString(columnIndex2).equals(UserConfigure.BG_CONTROL_NO_RESTRICT)) {
                        this.mUserNoRestrictUids.put(uidByPackageName, true);
                        getAppStateLocked(uidByPackageName).property |= 2;
                    }
                }
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
                int size = this.mUidAppState.size();
                for (int i4 = 0; i4 < size; i4++) {
                    updateRuleForUidLocked(this.mUidAppState.valueAt(i4));
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public Bundle getUidPolicy(int i2) {
        Bundle param;
        synchronized (this.mLock) {
            param = getAppStateLocked(i2).getParam();
        }
        return param;
    }

    public void setUidPolicy(final int i2, final Bundle bundle) {
        if (!UserHandle.isApp(i2)) {
            if (DEBUG) {
                Log.e(this.TAG, "cannot apply policy to UID " + i2);
                return;
            }
            return;
        }
        synchronized (this.mLock) {
            try {
                if (this.isEnabled) {
                    this.mHandler.post(new Runnable() { // from class: com.miui.powerkeeper.controller.ActiveStateController.3
                        @Override // java.lang.Runnable
                        public void run() {
                            ActiveStateController.this.updateUidPolicyUnchecked(i2, bundle);
                        }
                    });
                } else {
                    if (DEBUG) {
                        Log.v(this.TAG, "still disabled");
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public ActiveStateController(Context context, PowerKeeperManager powerKeeperManager, String str) {
        this.TAG = ActiveStateController.class.getSimpleName();
        this.mLock = new Object();
        this.isEnabled = false;
        this.mUidAppState = new SparseArray<>();
        this.mUserIMEUid = new SparseIntArray();
        this.mUserNoRestrictUids = new SparseBooleanArray();
        this.mContentObserver = new ContentObserver(null) { // from class: com.miui.powerkeeper.controller.ActiveStateController.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z2, Uri uri) {
                ActiveStateController.this.onContentChange(z2, uri);
            }
        };
        this.mVpnStatusCallback = new VpnTracker.StatusCallback() { // from class: com.miui.powerkeeper.controller.ActiveStateController.4
            @Override // com.miui.powerkeeper.utils.VpnTracker.StatusCallback
            public void onConnected(final int i2) {
                ActiveStateController.this.mHandler.post(new Runnable() { // from class: com.miui.powerkeeper.controller.ActiveStateController.4.1
                    @Override // java.lang.Runnable
                    public void run() {
                        synchronized (ActiveStateController.this.mLock) {
                            try {
                                if (!ActiveStateController.this.isEnabled) {
                                    if (ActiveStateController.DEBUG) {
                                        Log.v(ActiveStateController.this.TAG, "still disabled");
                                    }
                                    return;
                                }
                                ActiveStateController.this.getAppStateLocked(i2).property |= 4;
                                ActiveStateController activeStateController = ActiveStateController.this;
                                activeStateController.updateRuleForUidLocked(activeStateController.getAppStateLocked(i2));
                                f.c cVar = (f.c) e.i(1073741826);
                                Log.i(ActiveStateController.this.TAG, "Vpn connect, uid is " + i2 + ", dfsPower is " + cVar);
                                if (cVar != null && cVar.c() != null && cVar.c().getPowerUsageMonitor() != null) {
                                    Log.i(ActiveStateController.this.TAG, "notify vpn Connect uid is " + i2);
                                    cVar.c().getPowerUsageMonitor().notifyVpnConnect(i2, 1);
                                }
                            } catch (Throwable th) {
                                throw th;
                            }
                        }
                    }
                });
            }

            @Override // com.miui.powerkeeper.utils.VpnTracker.StatusCallback
            public void onDisconnected(final int i2) {
                ActiveStateController.this.mHandler.post(new Runnable() { // from class: com.miui.powerkeeper.controller.ActiveStateController.4.2
                    @Override // java.lang.Runnable
                    public void run() {
                        synchronized (ActiveStateController.this.mLock) {
                            try {
                                if (!ActiveStateController.this.isEnabled) {
                                    if (ActiveStateController.DEBUG) {
                                        Log.v(ActiveStateController.this.TAG, "still disabled");
                                    }
                                    return;
                                }
                                ActiveStateController.this.getAppStateLocked(i2).property &= -5;
                                ActiveStateController activeStateController = ActiveStateController.this;
                                activeStateController.updateRuleForUidLocked(activeStateController.getAppStateLocked(i2));
                                f.c cVar = (f.c) e.i(1073741826);
                                Log.i(ActiveStateController.this.TAG, "Vpn disconnect, uid is " + i2 + ", dfsPower is " + cVar);
                                if (cVar != null && cVar.c() != null && cVar.c().getPowerUsageMonitor() != null) {
                                    Log.i(ActiveStateController.this.TAG, "notify vpn disconnect uid is " + i2);
                                    cVar.c().getPowerUsageMonitor().notifyVpnConnect(i2, 0);
                                }
                            } catch (Throwable th) {
                                throw th;
                            }
                        }
                    }
                });
            }
        };
        this.mIMEChangedListener = new PowerKeeperInterface.v() { // from class: com.miui.powerkeeper.controller.ActiveStateController.5
            @Override // com.miui.powerkeeper.PowerKeeperInterface.v
            public void onIMEChange(int i2, int i3) {
                synchronized (ActiveStateController.this.mLock) {
                    try {
                        if (!ActiveStateController.this.isEnabled) {
                            if (ActiveStateController.DEBUG) {
                                Log.v(ActiveStateController.this.TAG, "still disabled");
                            }
                            return;
                        }
                        int i4 = ActiveStateController.this.mUserIMEUid.get(i2, -1);
                        if (i4 != -1) {
                            Log.i(ActiveStateController.this.TAG, "Ime outdated, uid is " + i4);
                            AppState appStateLocked = ActiveStateController.this.getAppStateLocked(i4);
                            appStateLocked.property = appStateLocked.property & (-9);
                            ActiveStateController activeStateController = ActiveStateController.this;
                            activeStateController.updateRuleForUidLocked(activeStateController.getAppStateLocked(i4));
                        }
                        if (i3 != -1) {
                            ActiveStateController.this.mUserIMEUid.put(i2, i3);
                            ActiveStateController.this.getAppStateLocked(i3).property |= 8;
                            ActiveStateController activeStateController2 = ActiveStateController.this;
                            activeStateController2.updateRuleForUidLocked(activeStateController2.getAppStateLocked(i3));
                            Log.i(ActiveStateController.this.TAG, "Ime update, uid is " + i3);
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        };
        this.mProcessObserver = new PowerKeeperInterface.r() { // from class: com.miui.powerkeeper.controller.ActiveStateController.7
            @Override // com.miui.powerkeeper.PowerKeeperInterface.r
            public void onForegroundActivitiesChanged(int i2, boolean z2) {
                synchronized (ActiveStateController.this.mLock) {
                    try {
                        if (ActiveStateController.this.isEnabled) {
                            ActiveStateController.this.updateStateForFgUidChangedLocked(i2, z2);
                        } else {
                            if (ActiveStateController.DEBUG) {
                                Log.v(ActiveStateController.this.TAG, "still disabled");
                            }
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        };
        this.mScreenReceiver = new PowerKeeperInterface.z() { // from class: com.miui.powerkeeper.controller.ActiveStateController.8
            @Override // com.miui.powerkeeper.PowerKeeperInterface.z
            public void onRegistered(boolean z2) {
                synchronized (ActiveStateController.this.mLock) {
                    try {
                        if (ActiveStateController.this.isEnabled) {
                            if (ActiveStateController.this.mScreenOn != ActiveStateController.this.mPowerKeeperManager.isScreenOn()) {
                                ActiveStateController.this.updateForScreenChangedLocked();
                            }
                        } else {
                            if (ActiveStateController.DEBUG) {
                                Log.v(ActiveStateController.this.TAG, "still disabled");
                            }
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }

            @Override // com.miui.powerkeeper.PowerKeeperInterface.z
            public void onScreenChanged(boolean z2) {
                synchronized (ActiveStateController.this.mLock) {
                    try {
                        if (ActiveStateController.this.isEnabled) {
                            if (ActiveStateController.this.mScreenOn != ActiveStateController.this.mPowerKeeperManager.isScreenOn()) {
                                ActiveStateController.this.updateForScreenChangedLocked();
                            }
                        } else {
                            if (ActiveStateController.DEBUG) {
                                Log.v(ActiveStateController.this.TAG, "still disabled");
                            }
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        };
        this.mPackageReceiver = new PowerKeeperInterface.x() { // from class: com.miui.powerkeeper.controller.ActiveStateController.9
            @Override // com.miui.powerkeeper.PowerKeeperInterface.x
            public void onUidRemoved(int i2, int i3) {
                synchronized (ActiveStateController.this.mLock) {
                    try {
                        if (ActiveStateController.this.isEnabled) {
                            ActiveStateController.this.updateForUidRemovedLocked(i3);
                        } else {
                            if (ActiveStateController.DEBUG) {
                                Log.v(ActiveStateController.this.TAG, "still disabled");
                            }
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }

            @Override // com.miui.powerkeeper.PowerKeeperInterface.x
            public void onRegistered() {
            }

            @Override // com.miui.powerkeeper.PowerKeeperInterface.x
            public void onUserReadyPackageAdded(int i2, Map<String, Integer> map) {
            }

            @Override // com.miui.powerkeeper.PowerKeeperInterface.x
            public void onPackageAdded(int i2, String str2, int i3) {
            }

            @Override // com.miui.powerkeeper.PowerKeeperInterface.x
            public void onPackageRemoved(int i2, String str2, int i3) {
            }
        };
        this.mAlarmHappenListener = new PowerKeeperInterface.i() { // from class: com.miui.powerkeeper.controller.ActiveStateController.10
            @Override // com.miui.powerkeeper.PowerKeeperInterface.i
            public void onAlarmHappen(long j2) {
                synchronized (ActiveStateController.this.mLock) {
                    try {
                        if (!ActiveStateController.this.isEnabled) {
                            if (ActiveStateController.DEBUG) {
                                Log.v(ActiveStateController.this.TAG, "still disabled");
                            }
                            return;
                        }
                        if (ActiveStateController.DEBUG) {
                            Log.d(ActiveStateController.this.TAG, "onAlarmHappen, elapsedRealtime = " + j2 + "ms");
                        }
                        ActiveStateController.this.mMessageHandler.onAlarm(j2);
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        };
        this.mAppActiveChangedListener = new PowerKeeperInterface.j() { // from class: com.miui.powerkeeper.controller.ActiveStateController.11
            @Override // com.miui.powerkeeper.PowerKeeperInterface.j
            public void onAppActiveChange(int i2, boolean z2) {
                synchronized (ActiveStateController.this.mLock) {
                    try {
                        if (ActiveStateController.this.isEnabled) {
                            ActiveStateController.this.updateStateForUidActiveChangedLocked(i2, z2);
                        } else {
                            if (ActiveStateController.DEBUG) {
                                Log.v(ActiveStateController.this.TAG, "still disabled");
                            }
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        };
        this.mUserStatusChangedListener = new PowerKeeperInterface.b0() { // from class: com.miui.powerkeeper.controller.ActiveStateController.12
            @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
            public void onUserEnableStatusChange(int i2, boolean z2) {
                synchronized (ActiveStateController.this.mLock) {
                    try {
                        if (!ActiveStateController.this.isEnabled) {
                            if (ActiveStateController.DEBUG) {
                                Log.v(ActiveStateController.this.TAG, "still disabled");
                            }
                        } else {
                            if (!z2) {
                                ActiveStateController.this.updateForUserRemovedLocked(i2);
                                ActiveStateController.this.mUserIMEUid.delete(i2);
                            }
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }

            @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
            public void onUserRunningStatusChange(int i2, boolean z2) {
                synchronized (ActiveStateController.this.mLock) {
                    try {
                        if (!ActiveStateController.this.isEnabled) {
                            if (ActiveStateController.DEBUG) {
                                Log.v(ActiveStateController.this.TAG, "still disabled");
                            }
                        } else {
                            if (!z2) {
                                ActiveStateController.this.updateForUserRemovedLocked(i2);
                                ActiveStateController.this.mUserIMEUid.delete(i2);
                            }
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }

            @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
            public void onXSpaceStatusChange(int i2, boolean z2) {
                synchronized (ActiveStateController.this.mLock) {
                    try {
                        if (ActiveStateController.this.isEnabled) {
                            if (!z2) {
                                ActiveStateController.this.updateForUserRemovedLocked(i2);
                            }
                        } else {
                            if (ActiveStateController.DEBUG) {
                                Log.v(ActiveStateController.this.TAG, "still disabled");
                            }
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }

            @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
            public void onUserForegroundStatusChange(int i2, boolean z2) {
            }
        };
        this.mContext = context;
        this.mPowerKeeperManager = powerKeeperManager;
        this.mUserCheckManager = powerKeeperManager.getUserCheckManager();
        this.TAG = str;
    }
}
