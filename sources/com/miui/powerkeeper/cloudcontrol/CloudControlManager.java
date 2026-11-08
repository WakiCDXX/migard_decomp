package com.miui.powerkeeper.cloudcontrol;

import android.content.Context;
import com.miui.powerkeeper.PowerKeeperApplication;
import com.miui.powerkeeper.provider.SimpleSettingKeys;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.utils.ThermalStoreUtils;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class CloudControlManager {
    private static final CloudControlManager sInstance = new CloudControlManager();
    private final Context mContext = PowerKeeperApplication.b();
    private final b.a mObserver = b.a.p();
    private final Map<IUseCloud, Integer> mUsers = new HashMap();
    private final b.a.d mThermalCollectorCallback = new b.a.d() { // from class: com.miui.powerkeeper.cloudcontrol.a
        @Override // b.a.d
        public final void onChanged(JSONObject jSONObject) {
            this.f379a.lambda$new$1(jSONObject);
        }
    };
    private final b.a.d mScenarioCallback = new b.a.d() { // from class: com.miui.powerkeeper.cloudcontrol.b
        @Override // b.a.d
        public final void onChanged(JSONObject jSONObject) {
            this.f380a.lambda$new$2(jSONObject);
        }
    };
    private final b.a.d mThermalDfsDevicesCallback = new b.a.d() { // from class: com.miui.powerkeeper.cloudcontrol.c
        @Override // b.a.d
        public final void onChanged(JSONObject jSONObject) {
            this.f381a.lambda$new$3(jSONObject);
        }
    };
    private final b.a.d mMiscCallback = new b.a.d() { // from class: com.miui.powerkeeper.cloudcontrol.d
        @Override // b.a.d
        public final void onChanged(JSONObject jSONObject) {
            this.f382a.saveToMisc(jSONObject);
        }
    };
    private final b.a.d mFileCallback = new b.a.d() { // from class: com.miui.powerkeeper.cloudcontrol.e
        @Override // b.a.d
        public final void onChanged(JSONObject jSONObject) {
            this.f383a.lambda$new$4(jSONObject);
        }
    };

    private CloudControlManager() {
        initCloudData();
        registerCallbacks();
    }

    public static CloudControlManager getInstance() {
        return sInstance;
    }

    private void initCloudData() {
        this.mMiscCallback.onChanged(this.mObserver.l(b.b.B));
        this.mThermalDfsDevicesCallback.onChanged(this.mObserver.l(b.b.P));
        this.mThermalCollectorCallback.onChanged(this.mObserver.l(b.b.f44z));
        this.mScenarioCallback.onChanged(this.mObserver.l(b.b.C));
        this.mFileCallback.onChanged(this.mObserver.l(b.b.A));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(JSONObject jSONObject) {
        saveToMisc(jSONObject);
        userUpdate(b.b.f44z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(JSONObject jSONObject) {
        saveToMisc(jSONObject);
        userUpdate(b.b.C);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(JSONObject jSONObject) {
        saveToMisc(jSONObject);
        userUpdate(b.b.P);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$4(JSONObject jSONObject) {
        String string;
        if (jSONObject == null) {
            return;
        }
        String string2 = SimpleSettings.Misc.getString(this.mContext, SimpleSettingKeys.KEY_THERMAL_PERFORMANCE_LAST_UPDATE_FILEID, "");
        try {
            string = jSONObject.getString("exloc");
        } catch (Exception e2) {
            e2.printStackTrace();
            string = null;
        }
        if (string == null) {
            return;
        }
        if (ThermalStoreUtils.checkThermalConfigReady() && string2.equalsIgnoreCase(string)) {
            return;
        }
        String str = Constants.MARKET_SERVER + string;
        if (HttpUtils.loadFile(str, ThermalStoreUtils.getConfigFileSavePath(false))) {
            SimpleSettings.Misc.putString(this.mContext, SimpleSettingKeys.KEY_THERMAL_PERFORMANCE_LAST_UPDATE_FILEID, string);
            ThermalStoreUtils.unZipConfig(str);
            userUpdate(b.b.A);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$unregisterListener$0(Integer num, Map.Entry entry) {
        return ((Integer) entry.getValue()).equals(num);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$userUpdate$5(int i2, Map.Entry entry) {
        return ((Integer) entry.getValue()).equals(Integer.valueOf(i2));
    }

    private void registerCallbacks() {
        this.mObserver.t(b.b.B, this.mMiscCallback);
        this.mObserver.t(b.b.f44z, this.mThermalCollectorCallback);
        this.mObserver.t(b.b.C, this.mScenarioCallback);
        this.mObserver.t(b.b.A, this.mFileCallback);
        this.mObserver.t(b.b.P, this.mThermalDfsDevicesCallback);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveToMisc(JSONObject jSONObject) {
        if (jSONObject == null) {
            return;
        }
        Iterator<String> itKeys = jSONObject.keys();
        while (itKeys.hasNext()) {
            saveToMisc(itKeys.next(), jSONObject);
        }
    }

    private void userUpdate(final int i2) {
        synchronized (this.mUsers) {
            this.mUsers.entrySet().stream().filter(new Predicate() { // from class: com.miui.powerkeeper.cloudcontrol.f
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return CloudControlManager.lambda$userUpdate$5(i2, (Map.Entry) obj);
                }
            }).map(new g()).forEach(new h());
        }
    }

    public void registerListener(IUseCloud iUseCloud, Integer num) {
        synchronized (this.mUsers) {
            this.mUsers.put(iUseCloud, num);
        }
    }

    public void unregisterListener(IUseCloud iUseCloud) {
        synchronized (this.mUsers) {
            this.mUsers.remove(iUseCloud);
        }
    }

    public void usersUpdateCloud() {
        initCloudData();
    }

    private void saveToMisc(String str, JSONObject jSONObject) {
        try {
            SimpleSettings.Misc.putString(this.mContext, str, jSONObject.getString(str));
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public void unregisterListener(final Integer num) {
        synchronized (this.mUsers) {
            ((List) this.mUsers.entrySet().stream().filter(new Predicate() { // from class: com.miui.powerkeeper.cloudcontrol.i
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return CloudControlManager.lambda$unregisterListener$0(num, (Map.Entry) obj);
                }
            }).map(new g()).collect(Collectors.toList())).forEach(new Consumer() { // from class: com.miui.powerkeeper.cloudcontrol.j
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    this.f386a.unregisterListener((IUseCloud) obj);
                }
            });
        }
    }
}
