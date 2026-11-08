package com.miui.powerkeeper.controller;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.UserHandle;
import android.util.LocalLog;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import com.android.internal.util.IndentingPrintWriter;
import com.miui.powerkeeper.DeviceIdlePolicyHelper;
import com.miui.powerkeeper.PowerKeeperApplication;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.provider.GlobalFeatureConfigure;
import com.miui.powerkeeper.provider.GlobalFeatureConfigureHelper;
import com.miui.powerkeeper.utils.PackageUtil;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import miui.os.Build;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class DeviceIdleController extends Controller {
    static final long MSG_DELAY_STEP_MILLS = 1000;
    static final int MSG_SET_UID_WHITE_LIST = 1;
    private static final String TAG = "DeviceIdleController";
    private static final String sNewCloudDozeAttr = "doze_whitelist_apps";
    private SparseBooleanArray mAppIdState;
    private SparseArray<AppIdState> mAppIdStateMap;
    private PowerKeeperInterface.c mAppRuleChangedCallback;
    private PowerKeeperInterface.k mAppRuleChangedListener;
    private PowerKeeperInterface.l mDeviceIdleAppRuleChecker;
    private EventsAggregator mEventsAggregator;
    private LocalLog mHistoryLog;
    private int mPowerMode;
    private Set<Integer> mTempNonWhitelistAppIds;
    private Set<Integer> mTempRemoveWhitelistAppIds;
    private Set<Integer> mTempWhitelistAppIds;
    private SparseArray<SparseBooleanArray> mUserAppIdState;
    private static Set<String> sAlwaysWhiteApps = new HashSet<String>() { // from class: com.miui.powerkeeper.controller.DeviceIdleController.1
        {
            if (Build.IS_INTERNATIONAL_BUILD) {
                return;
            }
            add("com.tencent.mm");
            add("com.tencent.mobileqq");
            add("com.tencent.qqlite");
        }
    };
    private static Set<String> sPowerSaveModeAlwaysWhiteApps = new HashSet<String>() { // from class: com.miui.powerkeeper.controller.DeviceIdleController.2
        {
            if (Build.IS_INTERNATIONAL_BUILD) {
                add("com.trustonic.telecoms.client.standard.dlc.playground");
                add("com.trustonic.telecoms.standard.dpc");
                add("com.trustonic.telecoms.standard.dlc");
                add("com.trustonic.telecoms.xti.dpc");
                add("com.payjoy.status");
                add("com.airtelx.airtelkiosk");
            }
        }
    };

    class AppIdState {
        boolean allow;
        int appId;

        public AppIdState(int i2, boolean z2) {
            this.appId = i2;
            this.allow = z2;
        }
    }

    /* JADX WARN: Illegal instructions before constructor call */
    public DeviceIdleController(Context context, PowerKeeperManager powerKeeperManager) {
        String str = TAG;
        super(context, powerKeeperManager, str);
        this.mUserAppIdState = new SparseArray<>();
        this.mAppIdState = new SparseBooleanArray();
        this.mAppIdStateMap = new SparseArray<>();
        this.mTempWhitelistAppIds = new HashSet();
        this.mTempNonWhitelistAppIds = new HashSet();
        this.mTempRemoveWhitelistAppIds = new HashSet();
        this.mHistoryLog = new LocalLog(Controller.MAX_HISTORY_ITEMS);
        this.mAppRuleChangedListener = new PowerKeeperInterface.k() { // from class: com.miui.powerkeeper.controller.DeviceIdleController.4
            @Override // com.miui.powerkeeper.PowerKeeperInterface.k
            public void onAppRuleChange(int i2, int i3) {
                synchronized (DeviceIdleController.this.mLock) {
                    try {
                        DeviceIdleController deviceIdleController = DeviceIdleController.this;
                        if (deviceIdleController.isEnabled) {
                            deviceIdleController.updateRuleForUidLocked(i2, i3);
                        } else {
                            Slog.v(DeviceIdleController.TAG, "still disabled");
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        };
        b.a.p().t(b.b.Q, new b.a.d() { // from class: com.miui.powerkeeper.controller.DeviceIdleController.3
            @Override // b.a.d
            public void onChanged(JSONObject jSONObject) {
                if (jSONObject != null) {
                    String strOptString = jSONObject.optString("doze_whitelist_apps", "");
                    if (Build.IS_DEBUGGABLE) {
                        Log.d(DeviceIdleController.TAG, "cloud data changed : " + strOptString);
                    }
                    DeviceIdleController.setWhiteListByNewCloud(PowerKeeperApplication.b(), strOptString);
                }
            }
        });
        if (Build.IS_DEBUGGABLE) {
            Log.d(str, "init getDozeWhiteListAppFromNewCloud ");
        }
        getDozeWhiteListAppFromNewCloud();
    }

    private void addAppIdToTempRemoveWhitelistAppIds() {
        sAlwaysWhiteApps.removeAll(sPowerSaveModeAlwaysWhiteApps);
        Iterator<String> it = sPowerSaveModeAlwaysWhiteApps.iterator();
        while (it.hasNext()) {
            int uidByPackageName = PackageUtil.getUidByPackageName(this.mContext, it.next());
            if (uidByPackageName > 0) {
                int uidRule = getUidRule(uidByPackageName);
                int appId = UserHandle.getAppId(uidByPackageName);
                if (uidRule != 0) {
                    this.mTempRemoveWhitelistAppIds.add(Integer.valueOf(appId));
                }
            }
        }
    }

    private boolean getAppIdState(int i2) {
        if (this.mAppIdState.indexOfKey(i2) >= 0) {
            return this.mAppIdState.get(i2);
        }
        boolean zBooleanValue = DeviceIdlePolicyHelper.i(this.mContext).m(i2).booleanValue();
        this.mAppIdState.put(i2, zBooleanValue);
        return zBooleanValue;
    }

    private SparseBooleanArray getAppIdStateArray(int i2) {
        SparseBooleanArray sparseBooleanArray = this.mUserAppIdState.get(i2);
        if (sparseBooleanArray != null) {
            return sparseBooleanArray;
        }
        SparseBooleanArray sparseBooleanArray2 = new SparseBooleanArray();
        this.mUserAppIdState.put(i2, sparseBooleanArray2);
        return sparseBooleanArray2;
    }

    private void getDozeWhiteListAppFromNewCloud() {
        JSONObject jSONObjectL;
        b.a aVarP = b.a.p();
        int i2 = b.b.Q;
        if (!aVarP.s(i2) || (jSONObjectL = b.a.p().l(i2)) == null) {
            return;
        }
        try {
            String string = jSONObjectL.getString("doze_whitelist_apps");
            setWhiteListByNewCloud(this.mContext, string);
            if (Build.IS_DEBUGGABLE) {
                Log.d(TAG, "new Cloud Data str : " + string);
            }
        } catch (JSONException unused) {
            Log.e(TAG, "new cloud don't have this attr doze_whitelist_apps");
        }
    }

    private int getUidRule(int i2) {
        return this.mDeviceIdleAppRuleChecker.getAppRule(i2);
    }

    private boolean getUidState(int i2) {
        return getAppIdState(UserHandle.getAppId(i2));
    }

    private SparseIntArray getUidsRule(int[] iArr) {
        return this.mDeviceIdleAppRuleChecker.getAppsRule(iArr);
    }

    private void removePowerSaveWhitelistUids() {
        Set<Integer> set = this.mTempRemoveWhitelistAppIds;
        if (set == null || set.size() <= 0) {
            return;
        }
        Set<Integer> set2 = this.mTempRemoveWhitelistAppIds;
        DeviceIdlePolicyHelper.i(this.mContext).q((Integer[]) set2.toArray(new Integer[set2.size()]));
        this.mTempRemoveWhitelistAppIds.clear();
    }

    private void setUidState(int i2, boolean z2) {
        int userId = UserHandle.getUserId(i2);
        int appId = UserHandle.getAppId(i2);
        SparseBooleanArray appIdStateArray = getAppIdStateArray(userId);
        if (appIdStateArray.indexOfKey(appId) < 0) {
            appIdStateArray.put(appId, getAppIdState(appId));
        }
        if (z2 != appIdStateArray.get(appId)) {
            appIdStateArray.put(appId, z2);
            boolean z3 = false;
            for (int i3 = 0; i3 < this.mUserAppIdState.size(); i3++) {
                if (this.mUserAppIdState.valueAt(i3).indexOfKey(appId) >= 0) {
                    z3 |= this.mUserAppIdState.valueAt(i3).get(appId);
                }
            }
            if (z3 != getAppIdState(appId)) {
                this.mAppIdState.put(appId, z3);
                if (this.mAppIdStateMap.get(appId) == null) {
                    this.mAppIdStateMap.put(appId, new AppIdState(appId, true));
                }
                this.mAppIdStateMap.get(appId).allow = z2;
                if (z2) {
                    this.mTempWhitelistAppIds.add(Integer.valueOf(appId));
                    this.mHistoryLog.log("add white app: " + appId);
                } else {
                    this.mTempNonWhitelistAppIds.add(Integer.valueOf(appId));
                    this.mHistoryLog.log("add non white app: " + appId);
                }
                if (this.mHandler.hasMessages(1)) {
                    return;
                }
                Message messageObtain = Message.obtain();
                messageObtain.what = 1;
                this.mHandler.sendMessageDelayed(messageObtain, 1000L);
            }
        }
    }

    public static void setWhiteListByNewCloud(Context context, String str) {
        Bundle bundle = new Bundle();
        bundle.putString(GlobalFeatureConfigure.DOZE_WHITELIST_APPS, str);
        GlobalFeatureConfigureHelper.updateGlobalConfigure(context, bundle);
    }

    private void updateForUidRemovedLocked(int i2, int i3) {
        if (Controller.DEBUG) {
            Slog.v(TAG, "updateForUidRemovedLocked, userId=" + i2 + ", uid=" + i3);
        }
        updateForUserAppIdRemovedLocked(i2, UserHandle.getAppId(i3));
    }

    private void updateForUserAppIdRemovedLocked(int i2, int i3) {
        if (Controller.DEBUG) {
            Slog.v(TAG, "updateForUserAppIdRemovedLocked, userId =" + i2 + ", appId=" + i3);
        }
        SparseBooleanArray appIdStateArray = getAppIdStateArray(i2);
        appIdStateArray.delete(i3);
        if (appIdStateArray.size() == 0) {
            this.mUserAppIdState.delete(i2);
        }
        boolean z2 = false;
        boolean z3 = false;
        for (int i4 = 0; i4 < this.mUserAppIdState.size(); i4++) {
            if (this.mUserAppIdState.valueAt(i4).indexOfKey(i3) >= 0) {
                z3 = this.mUserAppIdState.valueAt(i4).get(i3) | z3;
                z2 = true;
            }
        }
        if (!z2) {
            this.mAppIdState.delete(i3);
            AppIdState appIdState = this.mAppIdStateMap.get(i3);
            if (appIdState != null && this.mHandler.hasMessages(1, appIdState)) {
                this.mHandler.removeMessages(1, appIdState);
            }
            this.mAppIdStateMap.delete(i3);
            return;
        }
        if (z3 != getAppIdState(i3)) {
            this.mAppIdState.put(i3, z3);
            if (this.mAppIdStateMap.get(i3) == null) {
                this.mAppIdStateMap.put(i3, new AppIdState(i3, true));
            }
            AppIdState appIdState2 = this.mAppIdStateMap.get(i3);
            appIdState2.allow = z3;
            this.mTempWhitelistAppIds.remove(Integer.valueOf(i3));
            if (this.mHandler.hasMessages(1, appIdState2)) {
                return;
            }
            Message messageObtain = Message.obtain();
            messageObtain.what = 1;
            this.mHandler.sendMessageDelayed(messageObtain, 1000L);
        }
    }

    private void updateForUserRemovedLocked(int i2) {
        if (Controller.DEBUG) {
            Slog.v(TAG, "updateForUserRemovedLocked, userId=" + i2);
        }
        SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
        if (this.mUserAppIdState.indexOfKey(i2) >= 0) {
            SparseBooleanArray sparseBooleanArray2 = this.mUserAppIdState.get(i2);
            for (int i3 = 0; i3 < sparseBooleanArray2.size(); i3++) {
                sparseBooleanArray.put(sparseBooleanArray2.keyAt(i3), true);
            }
        }
        for (int i4 = 0; i4 < sparseBooleanArray.size(); i4++) {
            updateForUserAppIdRemovedLocked(i2, sparseBooleanArray.keyAt(i4));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateRuleForUidLocked(int i2, int i3) {
        if (UserHandle.isApp(i2)) {
            if (this.mUidPoliy.get(i2, 0) != 0) {
                return;
            }
            setUidState(i2, i3 == 0);
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        synchronized (this.mLock) {
            indentingPrintWriter.println("#######dump##DeviceIdleController#######");
            indentingPrintWriter.println("DeviceIdleController operation history:");
            this.mHistoryLog.dump(fileDescriptor, indentingPrintWriter, strArr);
            DeviceIdlePolicyHelper.i(this.mContext).f(fileDescriptor, printWriter, strArr);
            indentingPrintWriter.println("end#######dump##DeviceIdleController#######end");
        }
    }

    public SparseBooleanArray getUidStates() {
        SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
        synchronized (this.mLock) {
            for (int i2 = 0; i2 < this.mAppIdStateMap.size(); i2++) {
                try {
                    AppIdState appIdState = this.mAppIdStateMap.get(this.mAppIdStateMap.keyAt(i2));
                    sparseBooleanArray.put(appIdState.appId, appIdState.allow);
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return sparseBooleanArray;
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onDisable() {
        this.mDeviceIdleAppRuleChecker.unregisterAppRuleChangeListener(this.mAppRuleChangedCallback);
        EventsAggregator eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
        this.mEventsAggregator = eventsAggregator;
        if (eventsAggregator != null) {
            eventsAggregator.unregisterPowerModeListener(this.mHandler);
        }
        this.mAppRuleChangedCallback.a();
        this.mAppRuleChangedCallback = null;
        this.mDeviceIdleAppRuleChecker = null;
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onEnable() {
        String str = TAG;
        Slog.i(str, "DeviceIdleController enable");
        this.mHistoryLog.log("DeviceIdleController enable");
        this.mUserAppIdState.clear();
        this.mAppIdState.clear();
        this.mDeviceIdleAppRuleChecker = this.mPowerKeeperManager.getDeviceIdleAppRuleChecker();
        PowerKeeperInterface.c cVar = new PowerKeeperInterface.c(this.mHandler, this.mAppRuleChangedListener);
        this.mAppRuleChangedCallback = cVar;
        this.mDeviceIdleAppRuleChecker.registerAppRuleChangeListener(cVar);
        EventsAggregator eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
        this.mEventsAggregator = eventsAggregator;
        if (eventsAggregator != null) {
            eventsAggregator.registerPowerModeListener(this.mHandler);
        }
        int powerMode = this.mPowerKeeperManager.getEventsAggregator().getPowerMode();
        Slog.i(str, "DeviceIdleController enable powerMode is " + powerMode);
        if (powerMode != 3 && sPowerSaveModeAlwaysWhiteApps.size() > 0) {
            addAppIdToTempRemoveWhitelistAppIds();
        }
        removePowerSaveWhitelistUids();
        int[] allUids = getAllUids();
        for (int i2 : allUids) {
            getAppIdStateArray(UserHandle.getUserId(i2)).put(UserHandle.getAppId(i2), getUidState(i2));
        }
        SparseIntArray uidsRule = getUidsRule(allUids);
        for (int i3 = 0; i3 < uidsRule.size(); i3++) {
            updateRuleForUidLocked(uidsRule.keyAt(i3), uidsRule.valueAt(i3));
        }
        Slog.i(TAG, "DeviceIdleController enable end");
        this.mHistoryLog.log("DeviceIdleController enable end");
    }

    @Override // com.miui.powerkeeper.controller.Controller
    public void onHandleMessage(Message message) {
        synchronized (this.mLock) {
            try {
                int i2 = message.what;
                if (i2 == -22) {
                    Slog.w(TAG, "Power Mode Changed:" + message.arg1);
                    int i3 = message.arg1;
                    if (i3 == 3 && sPowerSaveModeAlwaysWhiteApps.size() > 0) {
                        sAlwaysWhiteApps.addAll(sPowerSaveModeAlwaysWhiteApps);
                        Message messageObtain = Message.obtain();
                        messageObtain.what = 1;
                        this.mHandler.sendMessage(messageObtain);
                    } else if (this.mPowerMode == 3 && i3 != 3 && sPowerSaveModeAlwaysWhiteApps.size() > 0) {
                        addAppIdToTempRemoveWhitelistAppIds();
                    }
                    removePowerSaveWhitelistUids();
                    this.mPowerMode = i3;
                } else if (i2 == 1) {
                    if (this.mPowerKeeperManager.getEventsAggregator().getPowerMode() != 5) {
                        Iterator<String> it = sAlwaysWhiteApps.iterator();
                        while (it.hasNext()) {
                            int uidByPackageName = PackageUtil.getUidByPackageName(this.mContext, it.next());
                            if (uidByPackageName > 0) {
                                int appId = UserHandle.getAppId(uidByPackageName);
                                this.mTempNonWhitelistAppIds.remove(Integer.valueOf(appId));
                                this.mTempWhitelistAppIds.add(Integer.valueOf(appId));
                            }
                        }
                    }
                    Set<Integer> set = this.mTempNonWhitelistAppIds;
                    Integer[] numArr = (Integer[]) set.toArray(new Integer[set.size()]);
                    Set<Integer> set2 = this.mTempWhitelistAppIds;
                    Integer[] numArr2 = (Integer[]) set2.toArray(new Integer[set2.size()]);
                    StringBuilder sb = new StringBuilder(256);
                    sb.append("removeList=");
                    sb.append(Arrays.toString(numArr));
                    sb.append(" AddList=");
                    sb.append(Arrays.toString(numArr2));
                    String string = sb.toString();
                    Slog.w(TAG, string);
                    this.mHistoryLog.log(string);
                    if (numArr != null && numArr.length > 0) {
                        DeviceIdlePolicyHelper.i(this.mContext).q(numArr);
                    }
                    this.mTempNonWhitelistAppIds.clear();
                    if (numArr2 != null && numArr2.length > 0) {
                        DeviceIdlePolicyHelper.i(this.mContext).e(numArr2);
                    }
                    this.mTempWhitelistAppIds.clear();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onSetUidPolicy(int i2, int i3) {
        if (i3 == 0) {
            updateRuleForUidLocked(i2, getUidRule(i2));
        }
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onUidRemoved(int i2, int i3) {
        updateForUidRemovedLocked(i2, i3);
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onUserEnableStatusChange(int i2, boolean z2) {
        if (z2) {
            return;
        }
        updateForUserRemovedLocked(i2);
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onUserRunningStatusChange(int i2, boolean z2) {
        if (z2) {
            return;
        }
        updateForUserRemovedLocked(i2);
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onXSpaceStatusChange(int i2, boolean z2) {
        if (z2) {
            return;
        }
        updateForUserRemovedLocked(i2);
    }
}
