package com.miui.powerkeeper.provider;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.UriMatcher;
import android.content.pm.ApplicationInfo;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.NetworkPolicyManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import b.a;
import b.b;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.IndentingPrintWriter;
import com.google.android.collect.Maps;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.UserCheckManager;
import com.miui.powerkeeper.cloudcontrol.CloudUpdateHideMode;
import com.miui.powerkeeper.cloudcontrol.Constants;
import com.miui.powerkeeper.utils.GmsObserver;
import com.miui.powerkeeper.utils.PackageUtil;
import com.miui.powerkeeper.utils.Utils;
import com.miui.powerkeeper.utils.VpnTracker;
import com.miui.whetstone.server.IWhetstoneActivityManager;
import e.e;
import f.m;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class PowerKeeperConfigureManager {
    private static boolean DEBUG = Build.IS_DEBUGGABLE;
    private static final String TAG = "PowerKeeperConfigureManager";
    private static final int TYPE_CLOUD_APP = 2;
    private static final int TYPE_GLOBAL = 1;
    private static final int TYPE_USER_APP = 3;
    private static final int TYPE_USER_APP_ID = 4;
    private static a mCloudObserver;
    private static final UriMatcher sMatcher;
    private ContentObserver mContentObserver;
    private Context mContext;
    private SparseBooleanArray mDisableArray;
    private Handler mHandler;
    private PowerKeeperInterface.w mIMEChangedCallback;
    private PowerKeeperInterface.d0 mPackageManagerCallback;
    private PowerKeeperManager mPowerKeeperManager;
    private SparseBooleanArray mSystemCoreArray;
    private SparseBooleanArray mSystemOtherArray;
    private UserCheckManager mUserCheckManager;
    protected PowerKeeperInterface.g0 mUserStatusChangedCallback;
    private int[] mVpnUids;
    private PowerKeeperInterface.h0 mWidgetChangedCallback;
    private SparseArray<Map<String, PowerKeeperAppConfigure>> mUserAppConfigure = new SparseArray<>();
    private int mConfigureState = 2;
    private SparseIntArray mUserIMEUid = new SparseIntArray();
    private SparseArray<Integer[]> mUserWidgetUids = new SparseArray<>();
    private final AtomicBoolean mInitializing = new AtomicBoolean(false);
    private PowerKeeperInterface.b0 mUserStatusChangedListener = new PowerKeeperInterface.b0() { // from class: com.miui.powerkeeper.provider.PowerKeeperConfigureManager.4
        @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
        public void onUserEnableStatusChange(int i2, boolean z2) {
            synchronized (PowerKeeperConfigureManager.this.mLock) {
                try {
                    Log.i(PowerKeeperConfigureManager.TAG, "onUserEnableStatusChange, userId: " + i2 + ", " + z2);
                    if (!z2) {
                        PowerKeeperConfigureManager.this.removeUserConfigure(i2);
                        if (PowerKeeperConfigureManager.this.mUserAppConfigure.indexOfKey(i2) >= 0) {
                            PowerKeeperConfigureManager.this.removeSpecialAppsLocked(i2);
                            PowerKeeperConfigureManager.this.removeUserAppConfigureLocked(i2);
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
        public void onUserForegroundStatusChange(int i2, boolean z2) {
            Log.i(PowerKeeperConfigureManager.TAG, "onUserForegroundStatusChange, userId: " + i2 + ", " + z2);
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
        public void onUserRunningStatusChange(int i2, boolean z2) {
            synchronized (PowerKeeperConfigureManager.this.mLock) {
                try {
                    Log.i(PowerKeeperConfigureManager.TAG, "onUserRunningStatusChange, userId: " + i2 + ", " + z2);
                    if (!z2 && PowerKeeperConfigureManager.this.mUserAppConfigure.indexOfKey(i2) >= 0) {
                        PowerKeeperConfigureManager.this.removeSpecialAppsLocked(i2);
                        PowerKeeperConfigureManager.this.removeUserAppConfigureLocked(i2);
                        Log.d(PowerKeeperConfigureManager.TAG, "onUserRunningStatusChange, Big Change, Reinit");
                        PowerKeeperConfigureManager.this.initLocked(false);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
        public void onXSpaceStatusChange(int i2, boolean z2) {
            synchronized (PowerKeeperConfigureManager.this.mLock) {
                try {
                    Log.i(PowerKeeperConfigureManager.TAG, "onXSpaceStatusChange, userId: " + i2 + ", " + z2);
                    if (!z2) {
                        PowerKeeperConfigureManager.this.removeSpecialAppsLocked(i2);
                        PowerKeeperConfigureManager.this.removeUserAppConfigureLocked(i2);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    };
    private PowerKeeperInterface.x mPackageReceiver = new PowerKeeperInterface.x() { // from class: com.miui.powerkeeper.provider.PowerKeeperConfigureManager.5
        @Override // com.miui.powerkeeper.PowerKeeperInterface.x
        public void onPackageAdded(int i2, String str, int i3) {
            synchronized (PowerKeeperConfigureManager.this.mLock) {
                try {
                    if (PowerKeeperConfigureManager.DEBUG) {
                        Log.d(PowerKeeperConfigureManager.TAG, "onPackageAdded, userId: " + i2 + " packageName: " + str + " uid: " + i3);
                    }
                    if (PowerKeeperConfigureManager.this.isControlApp(i3)) {
                        int iZ = PowerKeeperConfigureManager.this.mUserCheckManager.z();
                        if (PowerKeeperConfigureManager.DEBUG) {
                            Log.d(PowerKeeperConfigureManager.TAG, "onPackageAdded, xspaceUserId: " + iZ);
                        }
                        if (i2 == iZ) {
                            Map mapNewHashMap = (Map) PowerKeeperConfigureManager.this.mUserAppConfigure.get(i2);
                            if (mapNewHashMap == null) {
                                mapNewHashMap = Maps.newHashMap();
                                PowerKeeperConfigureManager.this.mUserAppConfigure.put(i2, mapNewHashMap);
                            }
                            if (mapNewHashMap.containsKey(str)) {
                                ((PowerKeeperAppConfigure) mapNewHashMap.get(str)).clearResource();
                                mapNewHashMap.remove(str);
                            }
                            Map map = (Map) PowerKeeperConfigureManager.this.mUserAppConfigure.get(PowerKeeperConfigureManager.this.mUserCheckManager.w());
                            if (map != null && map.containsKey(str)) {
                                PowerKeeperAppConfigure powerKeeperAppConfigureM1clone = ((PowerKeeperAppConfigure) map.get(str)).m1clone();
                                powerKeeperAppConfigureM1clone.setUid(i3);
                                mapNewHashMap.put(str, powerKeeperAppConfigureM1clone);
                                PowerKeeperConfigureManager.this.setUidPolicy(powerKeeperAppConfigureM1clone);
                            }
                            return;
                        }
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("userId", Integer.valueOf(i2));
                        contentValues.put("pkgName", str);
                        contentValues.put(UserConfigure.Columns.BG_CONTROL, "miuiAuto");
                        PowerKeeperConfigureManager.this.mContext.getContentResolverForUser(UserHandle.OWNER).insert(UserConfigure.CONTENT_URI, contentValues);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.x
        public void onPackageRemoved(int i2, String str, int i3) {
            synchronized (PowerKeeperConfigureManager.this.mLock) {
                try {
                    if (PowerKeeperConfigureManager.DEBUG) {
                        Log.d(PowerKeeperConfigureManager.TAG, "onPackageRemoved, userId: " + i2 + " packageName: " + str + " uid: " + i3);
                    }
                    PowerSaveConfigureManager.sHasVisited.remove(str);
                    if (PowerKeeperConfigureManager.this.mUserAppConfigure.indexOfKey(i2) >= 0) {
                        Map map = (Map) PowerKeeperConfigureManager.this.mUserAppConfigure.get(i2);
                        if (map.containsKey(str)) {
                            ((PowerKeeperAppConfigure) map.get(str)).clearResource();
                            map.remove(str);
                        }
                    }
                    if (i2 == PowerKeeperConfigureManager.this.mUserCheckManager.z()) {
                        return;
                    }
                    PowerKeeperConfigureManager.this.mContext.getContentResolverForUser(UserHandle.OWNER).delete(UserConfigure.CONTENT_URI, "userId = ? AND pkgName = ?", new String[]{Integer.toString(i2), str});
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.x
        public void onUserReadyPackageAdded(int i2, Map<String, Integer> map) {
            synchronized (PowerKeeperConfigureManager.this.mLock) {
                try {
                    Log.i(PowerKeeperConfigureManager.TAG, "onUserReadyPackageAdded, userId: " + i2);
                    int iZ = PowerKeeperConfigureManager.this.mUserCheckManager.z();
                    if (PowerKeeperConfigureManager.DEBUG) {
                        Log.d(PowerKeeperConfigureManager.TAG, "onUserReadyPackageAdded, xspaceUserId: " + iZ);
                    }
                    if (i2 == iZ) {
                        PowerKeeperConfigureManager.this.initXSpaceUserAppConfigureLocked();
                    } else {
                        PowerKeeperConfigureManager.this.initUserConfigure(i2);
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
        public void onUidRemoved(int i2, int i3) {
        }
    };
    private VpnTracker.StatusCallback mVpnStatusCallback = new VpnTracker.StatusCallback() { // from class: com.miui.powerkeeper.provider.PowerKeeperConfigureManager.6
        @Override // com.miui.powerkeeper.utils.VpnTracker.StatusCallback
        public void onConnected(final int i2) {
            PowerKeeperConfigureManager.this.mHandler.post(new Runnable() { // from class: com.miui.powerkeeper.provider.PowerKeeperConfigureManager.6.1
                @Override // java.lang.Runnable
                public void run() {
                    synchronized (PowerKeeperConfigureManager.this.mLock) {
                        PowerKeeperConfigureManager.this.addToSystemCoreListLocked(i2);
                        Log.i(PowerKeeperConfigureManager.TAG, "Vpn connect, uid is " + i2);
                    }
                }
            });
        }

        @Override // com.miui.powerkeeper.utils.VpnTracker.StatusCallback
        public void onDisconnected(final int i2) {
            PowerKeeperConfigureManager.this.mHandler.post(new Runnable() { // from class: com.miui.powerkeeper.provider.PowerKeeperConfigureManager.6.2
                @Override // java.lang.Runnable
                public void run() {
                    synchronized (PowerKeeperConfigureManager.this.mLock) {
                        Log.i(PowerKeeperConfigureManager.TAG, "Vpn disconnect, uid is " + i2);
                        PowerKeeperConfigureManager.this.removeFromSystemCoreListLocked(i2);
                    }
                }
            });
        }
    };
    private GmsObserver.StatusCallback mGmsStatusCallback = new GmsObserver.StatusCallback() { // from class: com.miui.powerkeeper.provider.PowerKeeperConfigureManager.7
        @Override // com.miui.powerkeeper.utils.GmsObserver.StatusCallback
        public void onGmsStatusChanged(final int i2, final boolean z2) {
            PowerKeeperConfigureManager.this.mHandler.post(new Runnable() { // from class: com.miui.powerkeeper.provider.PowerKeeperConfigureManager.7.1
                @Override // java.lang.Runnable
                public void run() {
                    synchronized (PowerKeeperConfigureManager.this.mLock) {
                        try {
                            if (z2) {
                                PowerKeeperConfigureManager.this.addToDisableListLocked(i2);
                            } else {
                                PowerKeeperConfigureManager.this.removeFromDisableListLocked(i2);
                            }
                        } catch (Throwable th) {
                            throw th;
                        }
                    }
                }
            });
        }
    };
    private PowerKeeperInterface.v mIMEChangedListener = new PowerKeeperInterface.v() { // from class: com.miui.powerkeeper.provider.PowerKeeperConfigureManager.8
        @Override // com.miui.powerkeeper.PowerKeeperInterface.v
        public void onIMEChange(int i2, int i3) {
            synchronized (PowerKeeperConfigureManager.this.mLock) {
                try {
                    int i4 = PowerKeeperConfigureManager.this.mUserIMEUid.get(i2, -1);
                    if (i4 != -1) {
                        Log.i(PowerKeeperConfigureManager.TAG, "userId is " + i2 + ", Ime outdated, uid is " + i4);
                        PowerKeeperConfigureManager.this.mUserIMEUid.delete(i2);
                        PowerKeeperConfigureManager.this.removeFromSystemCoreListLocked(i4);
                    }
                    if (i3 != -1) {
                        PowerKeeperConfigureManager.this.mUserIMEUid.put(i2, i3);
                        PowerKeeperConfigureManager.this.addToSystemCoreListLocked(i3);
                        Log.i(PowerKeeperConfigureManager.TAG, "userId is " + i2 + ", Ime update, uid is " + i3);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    };
    private PowerKeeperInterface.c0 mWidgetChangedListener = new PowerKeeperInterface.c0() { // from class: com.miui.powerkeeper.provider.PowerKeeperConfigureManager.9
        @Override // com.miui.powerkeeper.PowerKeeperInterface.c0
        public void onWidgetChange(int i2, Integer[] numArr) {
            synchronized (PowerKeeperConfigureManager.this.mLock) {
                try {
                    Integer[] numArr2 = (Integer[]) PowerKeeperConfigureManager.this.mUserWidgetUids.get(i2, null);
                    if (numArr2 != null && numArr2.length > 0) {
                        Log.i(PowerKeeperConfigureManager.TAG, "userId is " + i2 + ", Widget outdated, uid is " + numArr2);
                        int length = numArr2.length;
                        for (int i3 = 0; i3 < length; i3++) {
                            PowerKeeperConfigureManager.this.removeFromSystemOtherListLocked(numArr2[i3].intValue());
                        }
                    }
                    if (numArr != null && numArr.length > 0) {
                        PowerKeeperConfigureManager.this.mUserWidgetUids.put(i2, numArr);
                        for (Integer num : numArr) {
                            PowerKeeperConfigureManager.this.addToSystemOtherListLocked(num.intValue());
                        }
                        Log.i(PowerKeeperConfigureManager.TAG, "userId is " + i2 + ", Widget update, uid is " + numArr);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    };
    private Object mLock = new Object();

    static {
        UriMatcher uriMatcher = new UriMatcher(-1);
        sMatcher = uriMatcher;
        uriMatcher.addURI(PowerKeeperConfigure.AUTHORITY, GlobalFeatureConfigure.TABLE, 1);
        uriMatcher.addURI(PowerKeeperConfigure.AUTHORITY, CloudAppConfigure.TABLE, 2);
        uriMatcher.addURI(PowerKeeperConfigure.AUTHORITY, UserConfigure.TABLE, 3);
        uriMatcher.addURI(PowerKeeperConfigure.AUTHORITY, "userTable/#", 4);
    }

    public PowerKeeperConfigureManager(Context context, PowerKeeperManager powerKeeperManager) {
        this.mContext = context;
        this.mPowerKeeperManager = powerKeeperManager;
        this.mUserCheckManager = powerKeeperManager.getUserCheckManager();
        Handler handler = new Handler(BackgroundThread.getHandler().getLooper(), new Handler.Callback() { // from class: com.miui.powerkeeper.provider.PowerKeeperConfigureManager.1
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message message) {
                return PowerKeeperConfigureManager.this.onMessageHandler(message);
            }
        });
        this.mHandler = handler;
        this.mIMEChangedCallback = new PowerKeeperInterface.w(handler, this.mIMEChangedListener);
        this.mWidgetChangedCallback = new PowerKeeperInterface.h0(this.mHandler, this.mWidgetChangedListener);
        this.mPackageManagerCallback = new PowerKeeperInterface.d0(this.mHandler, this.mPackageReceiver);
        this.mUserStatusChangedCallback = new PowerKeeperInterface.g0(this.mHandler, this.mUserStatusChangedListener);
        this.mContentObserver = new ContentObserver(this.mHandler) { // from class: com.miui.powerkeeper.provider.PowerKeeperConfigureManager.2
            @Override // android.database.ContentObserver
            public void onChange(boolean z2, Uri uri) {
                if (uri == null) {
                    return;
                }
                synchronized (PowerKeeperConfigureManager.this.mLock) {
                    PowerKeeperConfigureManager.this.onContentChangeLocked(z2, uri);
                }
            }
        };
        this.mSystemCoreArray = new SparseBooleanArray();
        this.mSystemOtherArray = new SparseBooleanArray();
        this.mDisableArray = new SparseBooleanArray();
        registerCloudObserver();
        updateConfig();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addToDisableListLocked(int i2) {
        if (i2 >= 0 && this.mDisableArray.indexOfKey(i2) < 0) {
            this.mDisableArray.append(i2, true);
            for (int i3 = 0; i3 < this.mUserAppConfigure.size(); i3++) {
                Iterator<Map.Entry<String, PowerKeeperAppConfigure>> it = this.mUserAppConfigure.valueAt(i3).entrySet().iterator();
                while (it.hasNext()) {
                    PowerKeeperAppConfigure value = it.next().getValue();
                    if (i2 == value.getUid()) {
                        setDisableApp(value, true);
                        break;
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addToSystemCoreListLocked(int i2) {
        if (i2 >= 0 && this.mSystemCoreArray.indexOfKey(i2) < 0) {
            this.mSystemCoreArray.append(i2, true);
            for (int i3 = 0; i3 < this.mUserAppConfigure.size(); i3++) {
                Iterator<Map.Entry<String, PowerKeeperAppConfigure>> it = this.mUserAppConfigure.valueAt(i3).entrySet().iterator();
                while (it.hasNext()) {
                    PowerKeeperAppConfigure value = it.next().getValue();
                    if (i2 == value.getUid()) {
                        setSystemCoreApp(value);
                        break;
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addToSystemOtherListLocked(int i2) {
        if (i2 >= 0 && this.mSystemOtherArray.indexOfKey(i2) < 0) {
            this.mSystemOtherArray.append(i2, true);
            for (int i3 = 0; i3 < this.mUserAppConfigure.size(); i3++) {
                Iterator<Map.Entry<String, PowerKeeperAppConfigure>> it = this.mUserAppConfigure.valueAt(i3).entrySet().iterator();
                while (it.hasNext()) {
                    PowerKeeperAppConfigure value = it.next().getValue();
                    if (i2 == value.getUid()) {
                        setSystemOtherApp(value);
                        break;
                    }
                }
            }
        }
    }

    private void clearUserAppConfigureLocked() {
        for (int i2 = 0; i2 < this.mUserAppConfigure.size(); i2++) {
            Map<String, PowerKeeperAppConfigure> mapValueAt = this.mUserAppConfigure.valueAt(i2);
            Iterator<Map.Entry<String, PowerKeeperAppConfigure>> it = mapValueAt.entrySet().iterator();
            while (it.hasNext()) {
                it.next().getValue().clearResource();
            }
            mapValueAt.clear();
        }
        this.mUserAppConfigure.clear();
    }

    private int[] getAllUids() {
        int[] iArrX = this.mUserCheckManager.x();
        SparseArray sparseArray = new SparseArray();
        int i2 = 0;
        for (int i3 : iArrX) {
            Map<String, Integer> packageUidsMap = this.mPowerKeeperManager.getPackageUidsMap(i3);
            sparseArray.put(i3, (Integer[]) packageUidsMap.values().toArray(new Integer[packageUidsMap.values().size()]));
        }
        if (this.mUserCheckManager.E()) {
            int iZ = this.mUserCheckManager.z();
            Map<String, Integer> packageUidsMap2 = this.mPowerKeeperManager.getPackageUidsMap(iZ);
            sparseArray.put(iZ, (Integer[]) packageUidsMap2.values().toArray(new Integer[packageUidsMap2.values().size()]));
        }
        int length = 0;
        for (int i4 = 0; i4 < sparseArray.size(); i4++) {
            length += ((Integer[]) sparseArray.valueAt(i4)).length;
        }
        int[] iArr = new int[length];
        int i5 = 0;
        while (i2 < sparseArray.size()) {
            Integer[] numArr = (Integer[]) sparseArray.valueAt(i2);
            int length2 = numArr.length + i5;
            for (int i6 = i5; i6 < length2; i6++) {
                iArr[i6] = numArr[i6 - i5].intValue();
            }
            i2++;
            i5 = length2;
        }
        return iArr;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initLocked(boolean z2) {
        this.mInitializing.set(true);
        boolean configureStatusBoolean = GlobalFeatureConfigureHelper.getConfigureStatusBoolean(this.mContext);
        Bundle bundleQueryGlobalConfigure = GlobalFeatureConfigureHelper.queryGlobalConfigure(this.mContext);
        String userConfigure = GlobalFeatureConfigureHelper.getUserConfigure(this.mContext, UserHandle.myUserId());
        if (configureStatusBoolean || isEnableManually(userConfigure)) {
            boolean z3 = GlobalFeatureConfigureHelper.getCloudFeatureConfigure(bundleQueryGlobalConfigure) || isEnableManually(userConfigure);
            boolean cloudSensorConfigure = GlobalFeatureConfigureHelper.getCloudSensorConfigure(bundleQueryGlobalConfigure);
            boolean cloudBleScanBlockConfigure = GlobalFeatureConfigureHelper.getCloudBleScanBlockConfigure(bundleQueryGlobalConfigure);
            boolean cloudBGIdleConfigure = GlobalFeatureConfigureHelper.getCloudBGIdleConfigure(this.mContext);
            boolean cloudAppIdleConfigure = GlobalFeatureConfigureHelper.getCloudAppIdleConfigure(bundleQueryGlobalConfigure);
            boolean cloudNetRechableConfigure = GlobalFeatureConfigureHelper.getCloudNetRechableConfigure(bundleQueryGlobalConfigure);
            boolean cloudFrozenConfigure = GlobalFeatureConfigureHelper.getCloudFrozenConfigure(bundleQueryGlobalConfigure);
            boolean cloudClusterConfigure = GlobalFeatureConfigureHelper.getCloudClusterConfigure(bundleQueryGlobalConfigure);
            this.mConfigureState = this.mPowerKeeperManager.getEventsAggregator().toPowerMode(userConfigure);
            if (z3 || cloudFrozenConfigure || cloudAppIdleConfigure) {
                setActiveChecker(true, z2);
            } else {
                setActiveChecker(false, z2);
            }
            setNormalControl(z3, z2);
            setClusterControl(cloudClusterConfigure, z2);
            setBleScanBlockControl(cloudBleScanBlockConfigure, z2);
            setFrozenControl(cloudFrozenConfigure, z2);
            setSensorControl(cloudSensorConfigure, z2);
            setAppIdleControl(cloudAppIdleConfigure, z2);
            setNetReachableControl(cloudNetRechableConfigure, z2);
            setBGIdleControl(cloudBGIdleConfigure, z2);
            clearUserAppConfigureLocked();
            initRunningUserAppConfigureLocked();
            initXSpaceUserAppConfigureLocked();
        } else {
            this.mConfigureState = 0;
            setNormalControl(false, z2);
            setClusterControl(false, z2);
            setBleScanBlockControl(false, z2);
            setFrozenControl(false, z2);
            setAppIdleControl(false, z2);
            setSensorControl(false, z2);
            setActiveChecker(false, z2);
            setNetReachableControl(false, z2);
            setBGIdleControl(false, z2);
            clearUserAppConfigureLocked();
            initRunningUserAppConfigureLocked();
            initXSpaceUserAppConfigureLocked();
        }
        this.mInitializing.set(false);
    }

    private void initRunningUserAppConfigureLocked() {
        for (int i2 : this.mUserCheckManager.x()) {
            Map<String, PowerKeeperAppConfigure> mapCreateMap = PowerKeeperAppConfigure.createMap(this.mContext, i2);
            this.mUserAppConfigure.put(i2, mapCreateMap);
            Iterator<Map.Entry<String, PowerKeeperAppConfigure>> it = mapCreateMap.entrySet().iterator();
            while (it.hasNext()) {
                setUidPolicy(it.next().getValue());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initUserConfigure() {
        if (DEBUG) {
            Log.d(TAG, "initUserConfigure");
        }
        int[] iArrT = this.mUserCheckManager.t();
        String[] strArr = new String[iArrT.length];
        String str = "userId <> ?";
        String str2 = "userId <> ?";
        int i2 = 0;
        while (i2 < iArrT.length) {
            strArr[i2] = Integer.toString(iArrT[i2]);
            Log.i(TAG, "selectionArgs is " + strArr[i2]);
            i2++;
            if (i2 < iArrT.length) {
                str2 = str2 + " AND userId <> ?";
            }
        }
        Log.i(TAG, "selection is " + str2);
        ContentResolver contentResolverForUser = this.mContext.getContentResolverForUser(UserHandle.OWNER);
        contentResolverForUser.delete(UserConfigure.CONTENT_URI, str2, strArr);
        String[] strArr2 = new String[iArrT.length];
        int i3 = 0;
        while (i3 < iArrT.length) {
            strArr2[i3] = Integer.toString(iArrT[i3]);
            Log.i(TAG, "selectionArgs is " + strArr2[i3]);
            i3++;
            if (i3 < iArrT.length) {
                str = str + " AND userId <> ?";
            }
        }
        Log.i(TAG, "selection is " + str);
        contentResolverForUser.delete(GlobalFeatureConfigure.CONTENT_URI, str, strArr2);
        for (int i4 : this.mUserCheckManager.x()) {
            initUserConfigure(i4);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initXSpaceUserAppConfigureLocked() {
        if (this.mUserCheckManager.E()) {
            int iZ = this.mUserCheckManager.z();
            Map<String, PowerKeeperAppConfigure> map = this.mUserAppConfigure.get(this.mUserCheckManager.w());
            HashMap mapNewHashMap = Maps.newHashMap();
            this.mUserAppConfigure.put(iZ, mapNewHashMap);
            if (map != null) {
                Map<String, Integer> packageUidsMap = this.mPowerKeeperManager.getPackageUidsMap(iZ);
                for (String str : packageUidsMap.keySet()) {
                    int iIntValue = packageUidsMap.get(str).intValue();
                    if (isControlApp(iIntValue)) {
                        if (map.containsKey(str)) {
                            PowerKeeperAppConfigure powerKeeperAppConfigureM1clone = map.get(str).m1clone();
                            powerKeeperAppConfigureM1clone.setUid(iIntValue);
                            mapNewHashMap.put(str, powerKeeperAppConfigureM1clone);
                        } else {
                            Log.e(TAG, "initXSpaceUserAppConfigureLocked, " + str + " not existed in main space");
                        }
                    }
                }
                Iterator it = mapNewHashMap.entrySet().iterator();
                while (it.hasNext()) {
                    setUidPolicy((PowerKeeperAppConfigure) ((Map.Entry) it.next()).getValue());
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isControlApp(int i2) {
        int appId;
        return i2 > 0 && (appId = UserHandle.getAppId(i2)) >= 1000 && appId <= 19999;
    }

    private boolean isEnableManually(String str) {
        if (DEBUG) {
            Log.d(TAG, "manually enable, config = " + str);
        }
        return str != null && (str.equals(GlobalFeatureConfigure.USER_CONFIGURE_LEVEL_ULTIMATE) || str.equals(GlobalFeatureConfigure.USER_CONFIGURE_LEVEL_SLEEP) || str.equals("performance") || str.equals(GlobalFeatureConfigure.USER_CONFIGURE_LEVEL_ULTIMATE_EXTRA));
    }

    private boolean isInDisableList(int i2) {
        return this.mDisableArray.indexOfKey(i2) >= 0;
    }

    private boolean isInSystemCoreList(int i2) {
        return this.mSystemCoreArray.indexOfKey(i2) >= 0;
    }

    private boolean isInSystemOtherList(int i2) {
        return this.mSystemOtherArray.indexOfKey(i2) >= 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onContentChangeLocked(boolean z2, Uri uri) {
        if (DEBUG) {
            Log.d(TAG, "onContentChangeLocked, " + uri.toSafeString());
        }
        int iMatch = sMatcher.match(uri);
        if (iMatch == 1 || iMatch == 2 || iMatch == 3) {
            Log.d(TAG, "onContentChangeLocked, Big Change, Reinit");
            initLocked(false);
            return;
        }
        if (iMatch != 4) {
            return;
        }
        Cursor cursorQuery = this.mContext.getContentResolverForUser(UserHandle.OWNER).query(uri, null, null, null, null);
        if (cursorQuery == null || cursorQuery.getCount() == 0) {
            if (cursorQuery != null) {
                cursorQuery.close();
                return;
            }
            return;
        }
        UserConfigureHelper userConfigureHelperCreateFirstFromTable = UserConfigureHelper.createFirstFromTable(cursorQuery);
        int userId = userConfigureHelperCreateFirstFromTable.getUserId();
        Map<String, PowerKeeperAppConfigure> arrayMap = this.mUserAppConfigure.get(userId);
        if (arrayMap == null) {
            arrayMap = new ArrayMap<>();
            this.mUserAppConfigure.put(userId, arrayMap);
        }
        String packageName = userConfigureHelperCreateFirstFromTable.getPackageName();
        if (packageName != null && arrayMap.containsKey(packageName)) {
            arrayMap.get(packageName).clearResource();
            arrayMap.remove(packageName);
        }
        PowerKeeperAppConfigure powerKeeperAppConfigureCreateOne = PowerKeeperAppConfigure.createOne(this.mContext, userId, userConfigureHelperCreateFirstFromTable);
        if (powerKeeperAppConfigureCreateOne != null) {
            arrayMap.put(powerKeeperAppConfigureCreateOne.getPackage(), powerKeeperAppConfigureCreateOne);
            setUidPolicy(powerKeeperAppConfigureCreateOne);
        }
        cursorQuery.close();
        Log.d(TAG, "onContentChangeLocked, notify to migard!");
        if (powerKeeperAppConfigureCreateOne != null) {
            if (powerKeeperAppConfigureCreateOne.isNoRestrict()) {
                d.a.e().a(powerKeeperAppConfigureCreateOne.getPackage(), true);
            } else {
                d.a.e().c(powerKeeperAppConfigureCreateOne.getPackage());
            }
        }
        int iW = this.mUserCheckManager.w();
        if (userId == iW && this.mUserCheckManager.D()) {
            Map<String, PowerKeeperAppConfigure> map = this.mUserAppConfigure.get(this.mUserCheckManager.z(), null);
            if (map == null || !map.containsKey(packageName)) {
                return;
            }
            int uid = map.get(packageName).getUid();
            map.get(packageName).clearResource();
            map.remove(packageName);
            PowerKeeperAppConfigure powerKeeperAppConfigureM1clone = this.mUserAppConfigure.get(iW).get(packageName).m1clone();
            powerKeeperAppConfigureM1clone.setUid(uid);
            map.put(packageName, powerKeeperAppConfigureM1clone);
            setUidPolicy(powerKeeperAppConfigureM1clone);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean onMessageHandler(Message message) {
        return false;
    }

    private boolean pkgHasIcon(String str) {
        return Utils.pkgHasIcon(this.mContext, str);
    }

    private void registerCloudObserver() {
        a aVarP = a.p();
        mCloudObserver = aVarP;
        int i2 = b.D;
        aVarP.t(i2, new a.d() { // from class: com.miui.powerkeeper.provider.PowerKeeperConfigureManager.10
            @Override // b.a.d
            public void onChanged(JSONObject jSONObject) {
                try {
                    Log.i(PowerKeeperConfigureManager.TAG, "Cloud-Observer onChange - app list controller object is " + jSONObject);
                    if (jSONObject != null) {
                        CloudUpdateHideMode.parseApplistResult(PowerKeeperConfigureManager.this.mContext, jSONObject);
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        });
        Log.i(TAG, "Cloud-Observer app list controller isNewCloudConfigured is " + mCloudObserver.s(i2));
        mCloudObserver.t(b.I, new a.d() { // from class: com.miui.powerkeeper.provider.PowerKeeperConfigureManager.11
            @Override // b.a.d
            public void onChanged(JSONObject jSONObject) {
                Log.i(PowerKeeperConfigureManager.TAG, "Cloud-Observer onChange - open screen on kill app is " + jSONObject);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeFromDisableListLocked(int i2) {
        if (i2 >= 0 && this.mDisableArray.indexOfKey(i2) >= 0) {
            this.mDisableArray.delete(i2);
            for (int i3 = 0; i3 < this.mUserAppConfigure.size(); i3++) {
                Iterator<Map.Entry<String, PowerKeeperAppConfigure>> it = this.mUserAppConfigure.valueAt(i3).entrySet().iterator();
                while (it.hasNext()) {
                    PowerKeeperAppConfigure value = it.next().getValue();
                    if (i2 == value.getUid()) {
                        setDisableApp(value, false);
                        break;
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeFromSystemCoreListLocked(int i2) {
        if (i2 >= 0 && this.mSystemCoreArray.indexOfKey(i2) >= 0) {
            this.mSystemCoreArray.delete(i2);
            for (int i3 = 0; i3 < this.mUserAppConfigure.size(); i3++) {
                Iterator<Map.Entry<String, PowerKeeperAppConfigure>> it = this.mUserAppConfigure.valueAt(i3).entrySet().iterator();
                while (it.hasNext()) {
                    PowerKeeperAppConfigure value = it.next().getValue();
                    if (i2 == value.getUid()) {
                        setAppConfigureUidPolicy(value);
                        break;
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeFromSystemOtherListLocked(int i2) {
        if (i2 >= 0 && this.mSystemOtherArray.indexOfKey(i2) >= 0) {
            this.mSystemOtherArray.delete(i2);
            for (int i3 = 0; i3 < this.mUserAppConfigure.size(); i3++) {
                Iterator<Map.Entry<String, PowerKeeperAppConfigure>> it = this.mUserAppConfigure.valueAt(i3).entrySet().iterator();
                while (it.hasNext()) {
                    PowerKeeperAppConfigure value = it.next().getValue();
                    if (i2 == value.getUid()) {
                        setAppConfigureUidPolicy(value);
                        break;
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeSpecialAppsLocked(int i2) {
        if (DEBUG) {
            Log.d(TAG, "removeSpecialAppsLocked, userId = " + i2);
        }
        int i3 = this.mUserIMEUid.get(i2, -1);
        if (i3 != -1) {
            Log.i(TAG, "userId is " + i2 + ", Ime outdated, uid is " + i3);
            this.mUserIMEUid.delete(i2);
            removeFromSystemCoreListLocked(i3);
        }
        Integer[] numArr = this.mUserWidgetUids.get(i2, null);
        if (numArr == null || numArr.length <= 0) {
            return;
        }
        Log.i(TAG, "userId is " + i2 + ", Widget outdated, uid is " + numArr);
        for (Integer num : numArr) {
            removeFromSystemOtherListLocked(num.intValue());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeUserAppConfigureLocked(int i2) {
        Map<String, PowerKeeperAppConfigure> map = this.mUserAppConfigure.get(i2);
        if (map != null) {
            Iterator<Map.Entry<String, PowerKeeperAppConfigure>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                it.next().getValue().clearResource();
            }
            map.clear();
        }
        this.mUserAppConfigure.delete(i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeUserConfigure(int i2) {
        if (DEBUG) {
            Log.d(TAG, "removeUserConfigure, userId = " + i2);
        }
        ContentResolver contentResolverForUser = this.mContext.getContentResolverForUser(UserHandle.OWNER);
        contentResolverForUser.delete(UserConfigure.CONTENT_URI, "userId = ?", new String[]{Integer.toString(i2)});
        contentResolverForUser.delete(GlobalFeatureConfigure.CONTENT_URI, "userId = ?", new String[]{Integer.toString(i2)});
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void restoreToDefaultState() {
        if (DEBUG) {
            Log.d(TAG, "restoreToDefaultState");
        }
        if (NetworkPolicyManager.from(this.mContext).getNetworkPolicies() == null) {
            Log.e(TAG, "restoreToDefaultState: getNetworkPolicies is null");
        } else {
            Log.i(TAG, "restoreToDefaultState: No need wifi is no restricted");
        }
    }

    private void setActiveChecker(boolean z2, boolean z3) {
        if (z2) {
            this.mPowerKeeperManager.getAppActiveChecker().w();
        } else {
            this.mPowerKeeperManager.getAppActiveChecker().t();
        }
    }

    private void setAppConfigureUidPolicy(PowerKeeperAppConfigure powerKeeperAppConfigure) {
        if (DEBUG) {
            Log.d(TAG, powerKeeperAppConfigure.toString());
        }
        int uid = powerKeeperAppConfigure.getUid();
        this.mPowerKeeperManager.getAppActiveChecker().J(uid, powerKeeperAppConfigure.toAppActiveCheckerParams());
        this.mPowerKeeperManager.getLocationAppRuleChecker().setUidPolicy(uid, powerKeeperAppConfigure.toLocationAppRuleCheckerParams());
        this.mPowerKeeperManager.getFrozenAppRuleChecker().setUidPolicy(uid, powerKeeperAppConfigure.toFrozenAppRuleCheckerParams());
        this.mPowerKeeperManager.getKillProcessController().setUidPolicy(uid, powerKeeperAppConfigure.bgKillPolicy);
        this.mPowerKeeperManager.getKillProcessAppRuleChecker().setUidPolicy(uid, powerKeeperAppConfigure.toKillProcessAppRuleCheckerParams());
        this.mPowerKeeperManager.getSensorController().onSetUidDisableType(uid, powerKeeperAppConfigure.bgSDisableType);
        this.mPowerKeeperManager.getSensorAppRuleChecker().setUidPolicy(uid, powerKeeperAppConfigure.toSensorAppRuleCheckerParams());
        this.mPowerKeeperManager.getActiveStateController().setUidPolicy(uid, powerKeeperAppConfigure.toActiveStateControllerParams());
        this.mPowerKeeperManager.getBgIdleRuleChecker().setUidPolicy(uid, powerKeeperAppConfigure.toBGIdleAppRuleCheckerParams());
        this.mPowerKeeperManager.getDeviceIdleAppRuleChecker().setUidPolicy(uid, powerKeeperAppConfigure.toDeviceIdleAppRuleCheckerParams());
        this.mPowerKeeperManager.getAppIdleAppRuleChecker().setUidPolicy(uid, powerKeeperAppConfigure.toAppIdleAppRuleCheckerParams());
        this.mPowerKeeperManager.getAppClusterController().setUidPolicy(uid, powerKeeperAppConfigure.toAppClusterControllerParams());
    }

    private void setAppIdleControl(boolean z2, boolean z3) {
        if (z2) {
            this.mPowerKeeperManager.getAppIdleAppRuleChecker().enable();
            this.mPowerKeeperManager.getAppIdleController().enable();
        } else if (z3) {
            this.mPowerKeeperManager.getAppIdleController().restoreToDefaultState();
            this.mPowerKeeperManager.getAppIdleAppRuleChecker().disable();
        } else {
            this.mPowerKeeperManager.getAppIdleController().disable();
            this.mPowerKeeperManager.getAppIdleAppRuleChecker().disable();
        }
    }

    private void setBGIdleControl(boolean z2, boolean z3) {
        if (z2) {
            this.mPowerKeeperManager.getBgIdleRuleChecker().enable();
            this.mPowerKeeperManager.getBgIdleController().enable();
        } else if (z3) {
            this.mPowerKeeperManager.getBgIdleController().restoreToDefaultState();
            this.mPowerKeeperManager.getBgIdleRuleChecker().disable();
        } else {
            this.mPowerKeeperManager.getBgIdleController().disable();
            this.mPowerKeeperManager.getBgIdleRuleChecker().disable();
        }
    }

    private void setBleScanBlockControl(boolean z2, boolean z3) {
        String cloudBleScanBlockParam;
        IWhetstoneActivityManager iWhetstoneActivityManagerAsInterface = IWhetstoneActivityManager.Stub.asInterface(ServiceManager.getService("whetstone.activity"));
        if (iWhetstoneActivityManagerAsInterface == null) {
            return;
        }
        try {
            iWhetstoneActivityManagerAsInterface.getPowerKeeperPolicy().setLeScanFeature(z2);
        } catch (RemoteException unused) {
            Log.e(TAG, "IWhetstoneActivityManager work abnormal");
        }
        if (!z2 || (cloudBleScanBlockParam = GlobalFeatureConfigureHelper.getCloudBleScanBlockParam(this.mContext)) == null || "".equals(cloudBleScanBlockParam)) {
            return;
        }
        try {
            JSONArray jSONArrayOptJSONArray = new JSONObject(cloudBleScanBlockParam).optJSONArray("parolePeriodArray");
            if (jSONArrayOptJSONArray == null) {
                return;
            }
            int length = jSONArrayOptJSONArray.length();
            long[] jArr = new long[length];
            for (int i2 = 0; i2 < length; i2++) {
                jArr[i2] = jSONArrayOptJSONArray.optLong(i2);
            }
            Bundle bundle = new Bundle();
            bundle.putLongArray("parolePeriodArray", jArr);
            if (DEBUG) {
                Log.d(TAG, "setBleScanBlockControl, param = " + jSONArrayOptJSONArray);
            }
            iWhetstoneActivityManagerAsInterface.getPowerKeeperPolicy().setLeScanParam(bundle);
        } catch (RemoteException unused2) {
            Log.e(TAG, "IWhetstoneActivityManager work abnormal");
        } catch (JSONException e2) {
            Log.e(TAG, "setBleScanBlockControl", e2);
        }
    }

    private void setClusterControl(boolean z2, boolean z3) {
        if (z2) {
            this.mPowerKeeperManager.getAppClusterController().enable();
        } else {
            this.mPowerKeeperManager.getAppClusterController().disable();
        }
    }

    private void setDisableApp(PowerKeeperAppConfigure powerKeeperAppConfigure, boolean z2) {
        PowerKeeperAppConfigure powerKeeperAppConfigureM1clone = powerKeeperAppConfigure.m1clone();
        powerKeeperAppConfigureM1clone.setScenarioAsDisable(UserConfigureHelper.getUserConfigureHelperByPkg(this.mContext, UserHandle.getUserId(powerKeeperAppConfigure.getUid()), powerKeeperAppConfigure.getPackage()).getBgControl(), z2);
        setAppConfigureUidPolicy(powerKeeperAppConfigureM1clone);
    }

    private void setFrozenControl(boolean z2, boolean z3) {
        if (z2) {
            this.mPowerKeeperManager.getFrozenAppRuleChecker().enable();
            this.mPowerKeeperManager.getFrozenAppController().enable();
        } else if (z3) {
            this.mPowerKeeperManager.getFrozenAppController().restoreToDefaultState();
            this.mPowerKeeperManager.getFrozenAppRuleChecker().disable();
        } else {
            this.mPowerKeeperManager.getFrozenAppController().disable();
            this.mPowerKeeperManager.getFrozenAppRuleChecker().disable();
        }
    }

    private void setNetReachableControl(boolean z2, boolean z3) {
        try {
            PowerKeeperManager powerKeeperManager = this.mPowerKeeperManager;
            if (powerKeeperManager != null && powerKeeperManager.getFeedbackControlManager() != null) {
                int controlFeature = this.mPowerKeeperManager.getFeedbackControlManager().getControlFeature();
                if (z2) {
                    if ((controlFeature & 4) > 0) {
                        return;
                    }
                    this.mPowerKeeperManager.getFeedbackControlManager().setControlFeature(controlFeature | 4);
                    return;
                }
                if ((controlFeature & 4) == 0) {
                    return;
                }
                this.mPowerKeeperManager.getFeedbackControlManager().setControlFeature(controlFeature & (-5));
                return;
            }
            Log.d(TAG, "invalid param mPowerKeeperManager = " + this.mPowerKeeperManager);
        } catch (RemoteException e2) {
            Log.e(TAG, "setNetReachableControl", e2);
        }
    }

    private void setNormalControl(boolean z2, boolean z3) {
        this.mPowerKeeperManager.getDeviceIdleAppRuleChecker().enable();
        this.mPowerKeeperManager.getDeviceIdleController().enable();
        if (z2) {
            this.mPowerKeeperManager.getLocationAppRuleChecker().enable();
            this.mPowerKeeperManager.getKillProcessAppRuleChecker().enable();
            this.mPowerKeeperManager.getActiveStateController().enable();
            this.mPowerKeeperManager.getKillProcessController().enable();
            this.mPowerKeeperManager.getKillProcessController().update();
            this.mPowerKeeperManager.getAlarmController().enable();
            return;
        }
        if (z3) {
            this.mPowerKeeperManager.getActiveStateController().disable();
            this.mPowerKeeperManager.getKillProcessController().restoreToDefaultState();
            this.mPowerKeeperManager.getLocationAppRuleChecker().disable();
            this.mPowerKeeperManager.getKillProcessAppRuleChecker().disable();
            this.mPowerKeeperManager.getAlarmController().disable();
            return;
        }
        this.mPowerKeeperManager.getActiveStateController().disable();
        this.mPowerKeeperManager.getKillProcessController().disable();
        this.mPowerKeeperManager.getLocationAppRuleChecker().disable();
        this.mPowerKeeperManager.getKillProcessAppRuleChecker().disable();
        this.mPowerKeeperManager.getAlarmController().disable();
    }

    private void setSensorControl(boolean z2, boolean z3) {
        if (z2) {
            this.mPowerKeeperManager.getSensorAppRuleChecker().enable();
            this.mPowerKeeperManager.getSensorController().enable();
        } else if (z3) {
            this.mPowerKeeperManager.getSensorController().restoreToDefaultState();
            this.mPowerKeeperManager.getSensorAppRuleChecker().disable();
        } else {
            this.mPowerKeeperManager.getSensorController().disable();
            this.mPowerKeeperManager.getSensorAppRuleChecker().disable();
        }
    }

    private void setSystemCoreApp(PowerKeeperAppConfigure powerKeeperAppConfigure) {
        PowerKeeperAppConfigure powerKeeperAppConfigureM1clone = powerKeeperAppConfigure.m1clone();
        int userId = UserHandle.getUserId(powerKeeperAppConfigure.getUid());
        powerKeeperAppConfigureM1clone.setScenarioAsSystemCoreException(UserConfigureHelper.getUserConfigureHelperByPkg(this.mContext, userId, powerKeeperAppConfigure.getPackage()).getBgControl(), GlobalFeatureConfigureHelper.getUserConfigure(this.mContext, userId));
        setAppConfigureUidPolicy(powerKeeperAppConfigureM1clone);
    }

    private void setSystemOtherApp(PowerKeeperAppConfigure powerKeeperAppConfigure) {
        PowerKeeperAppConfigure powerKeeperAppConfigureM1clone = powerKeeperAppConfigure.m1clone();
        int userId = UserHandle.getUserId(powerKeeperAppConfigure.getUid());
        powerKeeperAppConfigureM1clone.setScenarioAsSystemOtherException(UserConfigureHelper.getUserConfigureHelperByPkg(this.mContext, userId, powerKeeperAppConfigure.getPackage()).getBgControl(), GlobalFeatureConfigureHelper.getUserConfigure(this.mContext, userId));
        setAppConfigureUidPolicy(powerKeeperAppConfigureM1clone);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setUidPolicy(PowerKeeperAppConfigure powerKeeperAppConfigure) {
        if (isInSystemCoreList(powerKeeperAppConfigure.getUid())) {
            setSystemCoreApp(powerKeeperAppConfigure);
            return;
        }
        if (isInSystemOtherList(powerKeeperAppConfigure.getUid())) {
            setSystemOtherApp(powerKeeperAppConfigure);
        } else if (isInDisableList(powerKeeperAppConfigure.getUid())) {
            setDisableApp(powerKeeperAppConfigure, true);
        } else {
            setAppConfigureUidPolicy(powerKeeperAppConfigure);
        }
    }

    private void updateConfig() {
        a aVar = mCloudObserver;
        if (aVar == null) {
            return;
        }
        int i2 = b.D;
        if (aVar.s(i2)) {
            try {
                JSONObject jSONObjectL = mCloudObserver.l(i2);
                Log.i(TAG, "updateConfig - app list controller object is " + jSONObjectL);
                if (jSONObjectL != null) {
                    CloudUpdateHideMode.parseApplistResult(this.mContext, jSONObjectL);
                }
            } catch (Exception e2) {
                Log.e(TAG, "updateConfig error", e2);
            }
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        String str;
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        indentingPrintWriter.println("#######dump Hide Mode Info#######");
        if (this.mInitializing.get()) {
            indentingPrintWriter.println("Do initializing, please dump later");
            return;
        }
        int i2 = this.mConfigureState;
        if (i2 == 1) {
            str = "Performance";
        } else if (i2 == 2) {
            str = "Balance";
        } else if (i2 == 3) {
            str = "Normal_save";
        } else if (i2 == 4) {
            str = "Ultimate_save";
        } else {
            str = i2 == 5 ? "Sleep" : "Disable";
        }
        indentingPrintWriter.println("STATE is " + str);
        Bundle bundleQueryGlobalConfigure = GlobalFeatureConfigureHelper.queryGlobalConfigure(this.mContext);
        indentingPrintWriter.println("hotFeature:" + Boolean.parseBoolean(bundleQueryGlobalConfigure.getString(GlobalFeatureConfigure.HOT_FEEDBACK_FEATURE, "false")) + ", networkFeature:" + Boolean.parseBoolean(bundleQueryGlobalConfigure.getString(GlobalFeatureConfigure.NETWORK_FEEDBACK_FEATURE, "false")));
        indentingPrintWriter.println("All control apps:");
        indentingPrintWriter.increaseIndent();
        if (this.mUserAppConfigure != null) {
            for (int i3 = 0; i3 < this.mUserAppConfigure.size(); i3++) {
                try {
                    indentingPrintWriter.println("userId: " + this.mUserAppConfigure.keyAt(i3));
                    for (Map.Entry<String, PowerKeeperAppConfigure> entry : this.mUserAppConfigure.valueAt(i3).entrySet()) {
                        indentingPrintWriter.print("package:" + entry.getKey() + " ");
                        StringBuilder sb = new StringBuilder();
                        sb.append("scenario:");
                        sb.append(entry.getValue().getScenario());
                        indentingPrintWriter.print(sb.toString());
                        indentingPrintWriter.println();
                    }
                } catch (Exception e2) {
                    indentingPrintWriter.println("exception mUserAppConfigure for index=" + i3 + " e=" + e2.getMessage());
                }
            }
        }
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("White List apps:");
        indentingPrintWriter.increaseIndent();
        int size = this.mSystemCoreArray.size();
        for (int i4 = 0; i4 < size; i4++) {
            try {
                String[] packagesForUid = this.mContext.getPackageManager().getPackagesForUid(this.mSystemCoreArray.keyAt(i4));
                if (packagesForUid != null) {
                    for (String str2 : packagesForUid) {
                        indentingPrintWriter.print("package:" + str2 + " ");
                    }
                }
            } catch (Exception e3) {
                indentingPrintWriter.println("exception mSystemCoreArray for index=" + i4 + " e=" + e3.getMessage());
            }
        }
        int size2 = this.mSystemOtherArray.size();
        for (int i5 = 0; i5 < size2; i5++) {
            try {
                String[] packagesForUid2 = this.mContext.getPackageManager().getPackagesForUid(this.mSystemOtherArray.keyAt(i5));
                if (packagesForUid2 != null) {
                    for (String str3 : packagesForUid2) {
                        indentingPrintWriter.print("package:" + str3 + " ");
                    }
                }
            } catch (Exception e4) {
                indentingPrintWriter.println("exception mSystemOtherArray for index=" + i5 + " e=" + e4.getMessage());
            }
        }
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("\nDisable List apps:");
        indentingPrintWriter.increaseIndent();
        int size3 = this.mDisableArray.size();
        for (int i6 = 0; i6 < size3; i6++) {
            try {
                String[] packagesForUid3 = this.mContext.getPackageManager().getPackagesForUid(this.mDisableArray.keyAt(i6));
                if (packagesForUid3 != null) {
                    for (String str4 : packagesForUid3) {
                        indentingPrintWriter.print("package:" + str4 + " ");
                    }
                }
            } catch (Exception e5) {
                indentingPrintWriter.println("exception mDisableArray for index=" + i6 + " e=" + e5.getMessage());
            }
        }
        indentingPrintWriter.println();
        indentingPrintWriter.decreaseIndent();
        CloudUpdateHideMode.dump(this.mContext, fileDescriptor, printWriter, strArr);
        indentingPrintWriter.println("end#######dump Hide Mode Info#######end");
    }

    public void onBootCompleted() {
        this.mHandler.post(new Runnable() { // from class: com.miui.powerkeeper.provider.PowerKeeperConfigureManager.3
            @Override // java.lang.Runnable
            public void run() {
                synchronized (PowerKeeperConfigureManager.this.mLock) {
                    try {
                        PowerKeeperConfigureManager.this.restoreToDefaultState();
                        PowerKeeperConfigureManager.this.initUserConfigure();
                        PowerKeeperConfigureManager.this.mPowerKeeperManager.registerPackageChangedListener(PowerKeeperConfigureManager.this.mPackageManagerCallback);
                        ContentResolver contentResolverForUser = PowerKeeperConfigureManager.this.mContext.getContentResolverForUser(UserHandle.OWNER);
                        contentResolverForUser.registerContentObserver(UserConfigure.CONTENT_URI, true, PowerKeeperConfigureManager.this.mContentObserver);
                        contentResolverForUser.registerContentObserver(CloudAppConfigure.CONTENT_URI, true, PowerKeeperConfigureManager.this.mContentObserver);
                        contentResolverForUser.registerContentObserver(GlobalFeatureConfigure.CONTENT_URI, true, PowerKeeperConfigureManager.this.mContentObserver);
                        PowerKeeperConfigureManager.this.mPowerKeeperManager.registerVpnStatusCallback(PowerKeeperConfigureManager.this.mVpnStatusCallback);
                        PowerKeeperConfigureManager.this.mPowerKeeperManager.registerIMEChangeListener(PowerKeeperConfigureManager.this.mIMEChangedCallback);
                        PowerKeeperConfigureManager.this.mPowerKeeperManager.registerWidgetChangeListener(PowerKeeperConfigureManager.this.mWidgetChangedCallback);
                        PowerKeeperConfigureManager.this.mUserCheckManager.J(PowerKeeperConfigureManager.this.mUserStatusChangedCallback);
                        PowerKeeperConfigureManager.this.mContext.registerReceiverAsUser(new BroadcastReceiver() { // from class: com.miui.powerkeeper.provider.PowerKeeperConfigureManager.3.1
                            @Override // android.content.BroadcastReceiver
                            public void onReceive(Context context, Intent intent) {
                                String action = intent.getAction();
                                if (action == null || !Constants.TEST_INTENT_ACTION.equals(action)) {
                                    return;
                                }
                                CloudUpdateHideMode.setTestFunc(PowerKeeperConfigureManager.this.mContext, intent);
                            }
                        }, UserHandle.ALL, new IntentFilter(Constants.TEST_INTENT_ACTION), null, PowerKeeperConfigureManager.this.mHandler, 2);
                        PowerKeeperConfigureManager powerKeeperConfigureManager = PowerKeeperConfigureManager.this;
                        powerKeeperConfigureManager.mVpnUids = powerKeeperConfigureManager.mPowerKeeperManager.getCurrentVpn();
                        for (int i2 : PowerKeeperConfigureManager.this.mVpnUids) {
                            if (i2 > 0) {
                                PowerKeeperConfigureManager.this.addToSystemCoreListLocked(i2);
                                Log.i(PowerKeeperConfigureManager.TAG, "VPN Uid is " + i2);
                            }
                        }
                        for (int i3 : PowerKeeperConfigureManager.this.mUserCheckManager.x()) {
                            int currentIME = PowerKeeperConfigureManager.this.mPowerKeeperManager.getCurrentIME(i3);
                            if (currentIME > 0) {
                                PowerKeeperConfigureManager.this.mUserIMEUid.put(i3, currentIME);
                                PowerKeeperConfigureManager.this.addToSystemCoreListLocked(currentIME);
                                Log.i(PowerKeeperConfigureManager.TAG, "userId is " + i3 + ", IME Uid is " + currentIME);
                            }
                            Integer[] currentWidgets = PowerKeeperConfigureManager.this.mPowerKeeperManager.getCurrentWidgets(i3);
                            for (Integer num : currentWidgets) {
                                int iIntValue = num.intValue();
                                PowerKeeperConfigureManager.this.mUserWidgetUids.put(i3, currentWidgets);
                                PowerKeeperConfigureManager.this.addToSystemOtherListLocked(iIntValue);
                                Log.i(PowerKeeperConfigureManager.TAG, "userId is " + i3 + ", widgetUids is " + currentWidgets);
                            }
                        }
                        PowerKeeperConfigureManager.this.initLocked(true);
                        m mVar = (m) e.i(17);
                        if (mVar == null || mVar.c() == null) {
                            Log.d(PowerKeeperConfigureManager.TAG, "registerGmsStatusCallback failed because gmsObserver init failed.");
                        } else {
                            mVar.c().registerGmsStatusCallback(PowerKeeperConfigureManager.this.mGmsStatusCallback);
                        }
                        Log.d(PowerKeeperConfigureManager.TAG, "init, notify to migard!");
                        ArrayList arrayList = new ArrayList();
                        for (Map.Entry entry : ((Map) PowerKeeperConfigureManager.this.mUserAppConfigure.get(UserHandle.myUserId())).entrySet()) {
                            String str = (String) entry.getKey();
                            if (((PowerKeeperAppConfigure) entry.getValue()).isNoRestrict() && !PackageUtil.isSystemApp(PowerKeeperConfigureManager.this.mContext, str)) {
                                arrayList.add(str);
                            }
                        }
                        d.a.e().b(arrayList, false);
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        });
    }

    public void onDestory() {
        this.mHandler.removeCallbacksAndMessages(null);
        this.mHandler = null;
    }

    private boolean isControlApp(ApplicationInfo applicationInfo) {
        return UserHandle.isApp(applicationInfo.uid);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initUserConfigure(int i2) {
        String string;
        if (DEBUG) {
            Log.d(TAG, "initUserConfigure, userId = " + i2);
        }
        ContentResolver contentResolverForUser = this.mContext.getContentResolverForUser(UserHandle.OWNER);
        String[] strArr = {"pkgName"};
        String[] strArr2 = {Integer.toString(i2)};
        HashSet<String> hashSet = new HashSet();
        try {
            Cursor cursorQuery = contentResolverForUser.query(UserConfigure.CONTENT_URI, strArr, "userId = ?", strArr2, null);
            if (cursorQuery != null && cursorQuery.getCount() > 0) {
                int columnIndex = cursorQuery.getColumnIndex("pkgName");
                while (cursorQuery.moveToNext()) {
                    hashSet.add(cursorQuery.getString(columnIndex));
                }
            }
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            Map<String, Integer> packageUidsMap = this.mPowerKeeperManager.getPackageUidsMap(i2);
            HashSet<String> hashSet2 = new HashSet();
            HashSet hashSet3 = new HashSet();
            PreSetGroup.initGroup();
            for (String str : packageUidsMap.keySet()) {
                if (PreSetGroup.isGroupHead(str)) {
                    PreSetGroup.setGroupUid(str, packageUidsMap.get(str).intValue());
                }
            }
            StringBuilder sb = null;
            for (String str2 : packageUidsMap.keySet()) {
                int iIntValue = packageUidsMap.get(str2).intValue();
                if (isControlApp(iIntValue)) {
                    String groupHead = PreSetGroup.isGroupUid(iIntValue) ? PreSetGroup.getGroupHead(iIntValue) : str2;
                    hashSet2.add(groupHead);
                    if (!pkgHasIcon(groupHead)) {
                        hashSet3.add(groupHead);
                        if (sb == null) {
                            sb = new StringBuilder(4096);
                            sb.append(str2);
                        } else {
                            sb.append(":");
                            sb.append(str2);
                        }
                    }
                }
            }
            for (String str3 : PreSetApp.getPreSetAppMap().keySet()) {
                if (sb == null) {
                    sb = new StringBuilder(4096);
                    sb.append(str3);
                } else {
                    sb.append(":");
                    sb.append(str3);
                }
            }
            if (sb == null) {
                SimpleSettings.Misc.putStringForUser(this.mContext, SimpleSettingKeys.KEY_USER_DECONFIGURED_APPS, "", i2);
            } else {
                SimpleSettings.Misc.putStringForUser(this.mContext, SimpleSettingKeys.KEY_USER_DECONFIGURED_APPS, sb.toString(), i2);
            }
            for (String str4 : hashSet) {
                if (!hashSet2.contains(str4)) {
                    String[] strArr3 = {Integer.toString(i2), str4};
                    if (DEBUG) {
                        Log.d(TAG, "delete " + str4);
                    }
                    contentResolverForUser.delete(UserConfigure.CONTENT_URI, "userId = ? AND pkgName = ?", strArr3);
                }
            }
            ContentValues contentValues = new ContentValues();
            for (String str5 : hashSet2) {
                if (!hashSet.contains(str5)) {
                    if (DEBUG) {
                        Log.d(TAG, "insert " + str5);
                    }
                    contentValues.put("userId", Integer.valueOf(i2));
                    contentValues.put("pkgName", str5);
                    contentValues.put(UserConfigure.Columns.BG_CONTROL, "miuiAuto");
                    contentResolverForUser.insert(UserConfigure.CONTENT_URI, contentValues);
                } else if (hashSet3.contains(str5)) {
                    if (DEBUG) {
                        Log.d(TAG, "insert " + str5);
                    }
                    contentValues.put("userId", Integer.valueOf(i2));
                    contentValues.put("pkgName", str5);
                    contentValues.put(UserConfigure.Columns.BG_CONTROL, "miuiAuto");
                    contentResolverForUser.insert(UserConfigure.CONTENT_URI, contentValues);
                }
            }
            Bundle bundleQueryGlobalConfigure = GlobalFeatureConfigureHelper.queryGlobalConfigure(this.mContext, i2);
            if (bundleQueryGlobalConfigure.containsKey(GlobalFeatureConfigure.USER_CONFIGURE_STATUS)) {
                string = bundleQueryGlobalConfigure.getString(GlobalFeatureConfigure.USER_CONFIGURE_STATUS);
            } else {
                string = "enhance";
            }
            bundleQueryGlobalConfigure.clear();
            bundleQueryGlobalConfigure.putString(GlobalFeatureConfigure.USER_CONFIGURE_STATUS, string);
            GlobalFeatureConfigureHelper.insertGlobalConfigure(this.mContext, i2, bundleQueryGlobalConfigure);
        } catch (Exception e2) {
            Log.e(TAG, "initUserConfigure", e2);
        }
    }
}
