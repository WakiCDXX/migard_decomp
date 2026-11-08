package com.miui.powerkeeper;

import android.content.ComponentName;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.util.IndentingPrintWriter;
import com.google.android.collect.Lists;
import com.google.android.collect.Maps;
import com.miui.powerkeeper.provider.SimpleSettingKeys;
import com.miui.powerkeeper.provider.SimpleSettings;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class WakelockPolicyManager {

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private static final String f340k = "WakelockPolicyManager";

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private Context f341a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private PowerKeeperManager f342b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private UserCheckManager f343c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private Handler f344d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private PowerKeeperInterface.d0 f345e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    protected PowerKeeperInterface.g0 f346f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private ComponentName f347g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private ContentObserver f348h = new a(new Handler());

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private PowerKeeperInterface.x f349i = new c();

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private PowerKeeperInterface.b0 f350j = new d();

    class a extends ContentObserver {
        a(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z2) {
            super.onChange(z2);
            WakelockPolicyManager.this.f344d.sendEmptyMessage(1);
        }
    }

    class b implements Handler.Callback {
        b() {
        }

        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            return WakelockPolicyManager.this.f(message);
        }
    }

    public WakelockPolicyManager(Context context, PowerKeeperManager powerKeeperManager) {
        this.f341a = context;
        this.f342b = powerKeeperManager;
        this.f343c = powerKeeperManager.getUserCheckManager();
        this.f347g = new ComponentName(this.f341a, (Class<?>) WakelockPolicyManager.class);
        Handler handler = new Handler(PowerKeeperManager.getThread().getLooper(), new b());
        this.f344d = handler;
        this.f345e = new PowerKeeperInterface.d0(handler, this.f349i);
        this.f346f = new PowerKeeperInterface.g0(this.f344d, this.f350j);
        this.f342b.registerPackageChangedListener(this.f345e);
        this.f343c.J(this.f346f);
        this.f341a.getContentResolver().registerContentObserver(SimpleSettings.Misc.getUriFor(SimpleSettingKeys.KEY_WAKELOCK_CLOUD_CONTENT), false, this.f348h);
        this.f344d.sendEmptyMessage(1);
    }

    private Map<String, List<String>> d() {
        HashMap mapNewHashMap = Maps.newHashMap();
        String string = SimpleSettings.Misc.getString(this.f341a, SimpleSettingKeys.KEY_WAKELOCK_CLOUD_CONTENT, "");
        if (string != null && !"".equals(string)) {
            try {
                JSONArray jSONArray = new JSONArray(string);
                for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                    JSONObject jSONObject = jSONArray.getJSONObject(i2);
                    mapNewHashMap.put(jSONObject.getString("pkg"), e(jSONObject.getString("tag")));
                }
            } catch (JSONException e2) {
                Log.e(f340k, "getPkgTagSet", e2);
            } catch (Exception e3) {
                Log.e(f340k, "getPkgTagSet", e3);
            }
        }
        return mapNewHashMap;
    }

    private List<String> e(String str) {
        ArrayList arrayListNewArrayList = Lists.newArrayList();
        if (str != null && !"".equals(str)) {
            for (String str2 : str.split(":")) {
                arrayListNewArrayList.add(str2);
            }
        }
        return arrayListNewArrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean f(Message message) {
        if (message.what != 1) {
            return false;
        }
        IWakelockManager wakelockManagerInterface = this.f342b.getWakelockManagerInterface();
        if (wakelockManagerInterface == null) {
            this.f344d.sendEmptyMessageDelayed(1, 10000L);
            return false;
        }
        g(wakelockManagerInterface);
        return false;
    }

    private void g(IWakelockManager iWakelockManager) {
        Log.i(f340k, "setWakelockPolicy");
        int[] iArrX = this.f343c.x();
        SparseArray sparseArray = new SparseArray();
        SparseArray sparseArray2 = new SparseArray();
        Map<String, List<String>> mapD = d();
        Set<String> setKeySet = mapD.keySet();
        for (int i2 : iArrX) {
            Map<String, Integer> packageUidsMap = this.f342b.getPackageUidsMap(i2);
            for (String str : packageUidsMap.keySet()) {
                Integer num = packageUidsMap.get(str);
                if (setKeySet.contains(str)) {
                    sparseArray.put(num.intValue(), mapD.get(str));
                } else {
                    sparseArray2.put(num.intValue(), null);
                }
            }
        }
        if (this.f343c.E()) {
            Map<String, Integer> packageUidsMap2 = this.f342b.getPackageUidsMap(this.f343c.z());
            for (String str2 : packageUidsMap2.keySet()) {
                Integer num2 = packageUidsMap2.get(str2);
                if (setKeySet.contains(str2)) {
                    sparseArray.put(num2.intValue(), mapD.get(str2));
                } else {
                    sparseArray2.put(num2.intValue(), null);
                }
            }
        }
        for (int i3 = 0; i3 < sparseArray2.size(); i3++) {
            try {
                iWakelockManager.setWakelockBlock(false, this.f347g, sparseArray2.keyAt(i3), null);
            } catch (Exception e2) {
                Log.e(f340k, "setWakelockPolicy", e2);
            }
        }
        for (int i4 = 0; i4 < sparseArray.size(); i4++) {
            try {
                iWakelockManager.setWakelockBlock(false, this.f347g, sparseArray.keyAt(i4), null);
                iWakelockManager.setWakelockBlock(true, this.f347g, sparseArray.keyAt(i4), (List) sparseArray.valueAt(i4));
            } catch (Exception e3) {
                Log.e(f340k, "setWakelockPolicy", e3);
            }
        }
    }

    public void c(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        indentingPrintWriter.println("#######dump WakelockPolicyManager#######");
        indentingPrintWriter.increaseIndent();
        Map<String, List<String>> mapD = d();
        for (String str : mapD.keySet()) {
            indentingPrintWriter.print("package: " + str);
            List<String> list = mapD.get(str);
            if (list.size() == 0) {
                indentingPrintWriter.print(", all tags blocked");
            } else {
                indentingPrintWriter.print(", tag:");
                Iterator<String> it = list.iterator();
                while (it.hasNext()) {
                    indentingPrintWriter.print(" " + it.next());
                }
            }
            indentingPrintWriter.println();
        }
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("end#######dump WakelockPolicyManager#######end");
    }

    class c implements PowerKeeperInterface.x {
        c() {
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.x
        public void onPackageAdded(int i2, String str, int i3) {
            WakelockPolicyManager.this.f344d.sendEmptyMessage(1);
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.x
        public void onPackageRemoved(int i2, String str, int i3) {
            WakelockPolicyManager.this.f344d.sendEmptyMessage(1);
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.x
        public void onUserReadyPackageAdded(int i2, Map<String, Integer> map) {
            WakelockPolicyManager.this.f344d.sendEmptyMessage(1);
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.x
        public void onRegistered() {
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.x
        public void onUidRemoved(int i2, int i3) {
        }
    }

    class d implements PowerKeeperInterface.b0 {
        d() {
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
        public void onUserRunningStatusChange(int i2, boolean z2) {
            WakelockPolicyManager.this.f344d.sendEmptyMessage(1);
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
        public void onXSpaceStatusChange(int i2, boolean z2) {
            WakelockPolicyManager.this.f344d.sendEmptyMessage(1);
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
        public void onUserEnableStatusChange(int i2, boolean z2) {
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
        public void onUserForegroundStatusChange(int i2, boolean z2) {
        }
    }
}
