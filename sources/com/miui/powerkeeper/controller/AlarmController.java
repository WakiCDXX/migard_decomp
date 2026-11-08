package com.miui.powerkeeper.controller;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.cloudcontrol.CloudFunctionConfig;
import com.miui.powerkeeper.provider.SimpleSettingKeys;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.utils.Constant;
import com.miui.powerkeeper.utils.PackageUtil;
import com.miui.whetstone.AlarmPolicy;
import com.miui.whetstone.server.IWhetstoneActivityManager;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class AlarmController extends Controller {
    private static final String ALARM_CONTROL_PARAMS = "alarm_control_params";
    private static final String FUCSWITCH = "fucSwitch";
    private static final String TAG = "PowerKeeper.Alarm";
    private final boolean DEBUG;
    public final int PARAM_TYPE_ALIGN_CONFIG;
    private SparseArray<String[]> mAlarmCloudRestricts;
    private AlarmControlObserver mAlarmControlObserver;
    private SparseArray<String[]> mAlarmRestricts;
    private boolean mIsEnable;
    private final Object mLock;

    private class AlarmControlCloudParamsObserver implements b.a.d {
        @Override // b.a.d
        public void onChanged(JSONObject jSONObject) {
            if (jSONObject != null) {
                AlarmController.this.putCloudDataToDB(jSONObject);
            }
        }

        private AlarmControlCloudParamsObserver() {
        }
    }

    private class AlarmControlObserver extends ContentObserver {
        public AlarmControlObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z2, Uri uri) {
            if (uri.equals(SimpleSettings.Misc.getUriFor(Constant.FUNC_ALARM_CONTROL))) {
                boolean zIsAlarmControlEnabled = AlarmController.this.isAlarmControlEnabled();
                if (zIsAlarmControlEnabled != AlarmController.this.mIsEnable) {
                    AlarmController.this.mIsEnable = zIsAlarmControlEnabled;
                    AlarmController.this.resetAlarmState();
                }
                if (AlarmController.this.DEBUG) {
                    Slog.d(AlarmController.TAG, "onchange, alarmcontrol enable " + zIsAlarmControlEnabled);
                    return;
                }
                return;
            }
            if (!uri.equals(SimpleSettings.Misc.getUriFor(AlarmController.ALARM_CONTROL_PARAMS))) {
                if (uri.equals(SimpleSettings.Misc.getUriFor(SimpleSettingKeys.KEY_ALARM_ALIGN_LIST))) {
                    AlarmController.this.updateAlarmAlignConfig();
                }
            } else {
                AlarmController.this.updateAlarmRestrictListLocked();
                AlarmController.this.resetAlarmState();
                if (AlarmController.this.DEBUG) {
                    Slog.d(AlarmController.TAG, "onchange, alarm restrict list changed");
                }
            }
        }
    }

    public AlarmController(Context context, PowerKeeperManager powerKeeperManager) {
        super(context, powerKeeperManager);
        this.PARAM_TYPE_ALIGN_CONFIG = 3;
        this.DEBUG = Build.IS_DEBUGGABLE;
        this.mLock = new Object();
        this.mHandler = new Controller.MyHandler(PowerKeeperManager.getThread().getLooper());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isAlarmControlEnabled() {
        return SimpleSettings.Misc.getBoolean(this.mContext, Constant.FUNC_ALARM_CONTROL, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void putCloudDataToDB(JSONObject jSONObject) {
        boolean zOptBoolean = jSONObject.optBoolean("fucSwitch", true);
        SimpleSettings.Misc.putBoolean(this.mContext, Constant.FUNC_ALARM_CONTROL, zOptBoolean);
        String strOptString = jSONObject.optString("param");
        if (TextUtils.isEmpty(strOptString)) {
            SimpleSettings.Misc.delete(this.mContext, ALARM_CONTROL_PARAMS);
        } else {
            SimpleSettings.Misc.putString(this.mContext, ALARM_CONTROL_PARAMS, strOptString);
        }
        if (this.DEBUG) {
            Log.d(TAG, "cloud Data => " + zOptBoolean + " - " + strOptString);
        }
    }

    private void registerContentObserver() {
        if (this.mAlarmControlObserver != null) {
            return;
        }
        ContentResolver contentResolver = this.mContext.getContentResolver();
        this.mAlarmControlObserver = new AlarmControlObserver(this.mHandler);
        contentResolver.registerContentObserver(SimpleSettings.Misc.getUriFor(SimpleSettingKeys.KEY_ALARM_ALIGN_LIST), false, this.mAlarmControlObserver);
        contentResolver.registerContentObserver(SimpleSettings.Misc.getUriFor(Constant.FUNC_ALARM_CONTROL), false, this.mAlarmControlObserver);
        contentResolver.registerContentObserver(SimpleSettings.Misc.getUriFor(ALARM_CONTROL_PARAMS), false, this.mAlarmControlObserver);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetAlarmState() {
        Controller.MyHandler myHandler = this.mHandler;
        if (myHandler == null) {
            return;
        }
        myHandler.post(new Runnable() { // from class: com.miui.powerkeeper.controller.AlarmController.2
            @Override // java.lang.Runnable
            public void run() {
                synchronized (AlarmController.this.mLock) {
                    try {
                        AlarmPolicy[] alarmPolicyArr = new AlarmPolicy[0];
                        if (AlarmController.this.mIsEnable) {
                            int size = AlarmController.this.mAlarmCloudRestricts.size();
                            AlarmPolicy[] alarmPolicyArr2 = new AlarmPolicy[size];
                            for (int i2 = 0; i2 < size; i2++) {
                                alarmPolicyArr2[i2] = new AlarmPolicy(AlarmController.this.mAlarmCloudRestricts.keyAt(i2), (String[]) AlarmController.this.mAlarmCloudRestricts.valueAt(i2));
                            }
                            alarmPolicyArr = alarmPolicyArr2;
                        }
                        AlarmController.this.updateAlarmStateLocked(alarmPolicyArr, true);
                        if (AlarmController.this.mIsEnable) {
                            int size2 = AlarmController.this.mAlarmRestricts.size();
                            AlarmPolicy[] alarmPolicyArr3 = new AlarmPolicy[size2];
                            for (int i3 = 0; i3 < size2; i3++) {
                                alarmPolicyArr3[i3] = new AlarmPolicy(AlarmController.this.mAlarmRestricts.keyAt(i3), (String[]) AlarmController.this.mAlarmRestricts.valueAt(i3));
                            }
                            AlarmController.this.updateAlarmStateLocked(alarmPolicyArr3, false);
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        });
    }

    private void unregisterContentObserver() {
        if (this.mAlarmControlObserver == null) {
            return;
        }
        this.mContext.getContentResolver().unregisterContentObserver(this.mAlarmControlObserver);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAlarmAlignConfig() {
        synchronized (this.mLock) {
            ArrayList arrayList = new ArrayList();
            String string = SimpleSettings.Misc.getString(this.mContext, SimpleSettingKeys.KEY_ALARM_ALIGN_LIST);
            if (string == null) {
                return;
            }
            try {
                JSONObject jSONObject = new JSONObject(string);
                boolean zOptBoolean = jSONObject.optBoolean("fucSwitch", true);
                String strOptString = jSONObject.optString("white_list", "");
                String[] strArr = new String[0];
                if (!strOptString.isEmpty()) {
                    JSONArray jSONArray = new JSONArray(strOptString);
                    int length = jSONArray.length();
                    for (int i2 = 0; i2 < length; i2++) {
                        JSONObject jSONObject2 = jSONArray.getJSONObject(i2);
                        String strOptString2 = jSONObject2.optString("pkg");
                        String strOptString3 = jSONObject2.optString("tags");
                        String[] strArrSplit = !TextUtils.isEmpty(strOptString3) ? strOptString3.split(",") : strArr;
                        int uidByPackageName = PackageUtil.getUidByPackageName(this.mContext, strOptString2);
                        if (uidByPackageName != -1) {
                            arrayList.add(new AlarmPolicy(uidByPackageName, strArrSplit));
                        }
                    }
                }
                synchronized (this.mLock) {
                    try {
                        Iterator<Integer> it = this.mIgnoreUids.iterator();
                        while (it.hasNext()) {
                            arrayList.add(new AlarmPolicy(it.next().intValue(), strArr));
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
                IWhetstoneActivityManager iWhetstoneActivityManagerAsInterface = IWhetstoneActivityManager.Stub.asInterface(ServiceManager.getService("whetstone.activity"));
                if (iWhetstoneActivityManagerAsInterface == null) {
                    Log.d(TAG, "ws is null");
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putBoolean("align_enable", zOptBoolean);
                if (arrayList.size() > 0) {
                    bundle.putParcelableArray("align_policy", (Parcelable[]) arrayList.toArray(new AlarmPolicy[0]));
                }
                iWhetstoneActivityManagerAsInterface.getPowerKeeperPolicy().setParam(3, (String) null, bundle);
            } catch (Exception unused) {
                Log.e(TAG, "IWhetstoneActivityManager work abnormal");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAlarmRestrictListLocked() {
        b.a aVarP = b.a.p();
        int i2 = b.b.T;
        JSONObject jSONObjectL = aVarP.l(i2);
        String functionParameters = (!b.a.p().s(i2) || jSONObjectL == null) ? CloudFunctionConfig.getFunctionParameters(this.mContext, Constant.FUNC_ALARM_CONTROL, "") : jSONObjectL.optString("param", null);
        synchronized (this.mLock) {
            try {
                this.mAlarmCloudRestricts.clear();
                if (TextUtils.isEmpty(functionParameters)) {
                    return;
                }
                JSONArray jSONArray = new JSONArray(functionParameters);
                int length = jSONArray.length();
                for (int i3 = 0; i3 < length; i3++) {
                    JSONObject jSONObject = jSONArray.getJSONObject(i3);
                    String strOptString = jSONObject.optString("pkg");
                    String strOptString2 = jSONObject.optString("tags");
                    String[] strArrSplit = !TextUtils.isEmpty(strOptString2) ? strOptString2.split(",") : new String[0];
                    int uidByPackageName = PackageUtil.getUidByPackageName(this.mContext, strOptString);
                    if (uidByPackageName != -1) {
                        this.mAlarmCloudRestricts.put(uidByPackageName, strArrSplit);
                    }
                }
            } catch (Exception e2) {
                if (this.DEBUG) {
                    Log.d(TAG, "parse json error", e2);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAlarmStateLocked(AlarmPolicy[] alarmPolicyArr, boolean z2) {
        try {
            IWhetstoneActivityManager iWhetstoneActivityManagerAsInterface = IWhetstoneActivityManager.Stub.asInterface(ServiceManager.getService("whetstone.activity"));
            if (iWhetstoneActivityManagerAsInterface == null) {
                Log.d(TAG, "ws is null");
            } else {
                iWhetstoneActivityManagerAsInterface.getPowerKeeperPolicy().setAlarmPolicy(alarmPolicyArr, z2);
            }
        } catch (RemoteException unused) {
            Log.e(TAG, "IWhetstoneActivityManager work abnormal");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAppsAlarmStateLocked(ArrayList<AlarmPolicy> arrayList) {
        synchronized (this.mLock) {
            try {
                if (this.mIsEnable) {
                    int size = arrayList.size();
                    AlarmPolicy[] alarmPolicyArr = new AlarmPolicy[size];
                    for (int i2 = 0; i2 < size; i2++) {
                        AlarmPolicy alarmPolicy = arrayList.get(i2);
                        String[] strArr = alarmPolicy.mTags;
                        if (strArr == null) {
                            this.mAlarmRestricts.delete(alarmPolicy.mUid);
                        } else {
                            this.mAlarmRestricts.put(alarmPolicy.mUid, strArr);
                        }
                        alarmPolicyArr[i2] = alarmPolicy;
                    }
                    updateAlarmStateLocked(alarmPolicyArr, false);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onDisable() {
        unregisterContentObserver();
        this.mAlarmCloudRestricts.clear();
        this.mAlarmRestricts.clear();
        resetAlarmState();
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onEnable() {
        this.mAlarmRestricts = new SparseArray<>();
        this.mAlarmCloudRestricts = new SparseArray<>();
        this.mIsEnable = isAlarmControlEnabled();
        updateAlarmRestrictListLocked();
        updateAlarmAlignConfig();
        registerContentObserver();
        resetAlarmState();
        b.a.p().t(b.b.T, new AlarmControlCloudParamsObserver());
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onPackageAdded(int i2, String str, int i3) {
        updateAlarmAlignConfig();
    }

    @Override // com.miui.powerkeeper.controller.Controller
    protected void onUidRemoved(int i2, int i3) {
        updateAlarmAlignConfig();
    }

    public void setAppsAlarmState(final ArrayList arrayList) {
        Controller.MyHandler myHandler = this.mHandler;
        if (myHandler == null) {
            return;
        }
        myHandler.post(new Runnable() { // from class: com.miui.powerkeeper.controller.AlarmController.1
            @Override // java.lang.Runnable
            public void run() {
                AlarmController.this.updateAppsAlarmStateLocked(arrayList);
            }
        });
    }
}
