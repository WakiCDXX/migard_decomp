package com.miui.powerkeeper;

import android.R;
import android.app.usage.IUsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IDeviceIdleController;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.LocalLog;
import android.util.Log;
import com.miui.powerkeeper.utils.CommonAdapter;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;

/* JADX INFO: loaded from: classes.dex */
public class DeviceIdlePolicyHelper {

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public static final String f170g = "DeviceIdlePolicyHelper";

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private static DeviceIdlePolicyHelper f171h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private static ArraySet<String> f172i;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private boolean f173a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private boolean f174b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private Context f175c;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private LocalLog f178f = new LocalLog(64);

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private IDeviceIdleController f176d = IDeviceIdleController.Stub.asInterface(ServiceManager.getService("deviceidle"));

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private IUsageStatsManager f177e = IUsageStatsManager.Stub.asInterface(ServiceManager.getService("usagestats"));

    class a extends BroadcastReceiver {
        a() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.os.action.POWER_SAVE_WHITELIST_CHANGED".equals(intent.getAction())) {
                try {
                    DeviceIdlePolicyHelper.f172i = new ArraySet(DeviceIdlePolicyHelper.this.f176d.getFullPowerWhitelist());
                } catch (RemoteException unused) {
                }
            }
        }
    }

    private DeviceIdlePolicyHelper(Context context) {
        this.f173a = false;
        this.f174b = false;
        this.f175c = context;
        try {
            boolean z2 = context.getResources().getBoolean(R.bool.config_earcFeatureDisabled_default);
            this.f173a = z2;
            this.f174b = z2;
            Log.i(f170g, "mDozeEnabled:" + this.f173a);
        } catch (Exception e2) {
            Log.e(f170g, "DeviceIdlePolicyHelper()", e2);
            this.f173a = false;
            this.f174b = false;
        }
        try {
            f172i = new ArraySet<>(this.f176d.getFullPowerWhitelist());
        } catch (RemoteException unused) {
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.os.action.POWER_SAVE_WHITELIST_CHANGED");
        context.registerReceiverAsUser(new a(), UserHandle.ALL, intentFilter, null, null);
    }

    public static DeviceIdlePolicyHelper i(Context context) {
        if (f171h == null) {
            f171h = new DeviceIdlePolicyHelper(context.getApplicationContext());
        }
        return f171h;
    }

    private Boolean j(String str, int i2) {
        try {
            return Boolean.valueOf(CommonAdapter.isAppInactive(this.f177e, str, i2, this.f175c.getOpPackageName()));
        } catch (Exception unused) {
            return Boolean.FALSE;
        }
    }

    private Boolean l(String str) {
        if (TextUtils.isEmpty(str)) {
            return Boolean.FALSE;
        }
        try {
            return Boolean.valueOf(this.f176d.isPowerSaveWhitelistApp(str));
        } catch (Exception unused) {
            return Boolean.FALSE;
        }
    }

    private void r(String str, boolean z2, int i2) {
        try {
            this.f177e.setAppInactive(str, z2, i2);
        } catch (Exception unused) {
        }
    }

    private void t() throws RemoteException {
        ServiceManager.getService("deviceidle").dump(FileDescriptor.out, new String[]{"unforce"});
    }

    public void c(String[] strArr) {
        if (strArr == null || strArr.length == 0) {
            return;
        }
        try {
            this.f178f.log("add " + Arrays.toString(strArr));
            CommonAdapter.addPowerSaveWhitelistApps(this.f176d, strArr);
        } catch (Exception e2) {
            Log.e(f170g, "addPowerSaveWhitelistApps: " + Arrays.toString(strArr), e2);
        }
    }

    public void d(int i2) {
        String[] packagesForUid = this.f175c.getPackageManager().getPackagesForUid(i2);
        if (packagesForUid == null || packagesForUid.length == 0) {
            return;
        }
        c(packagesForUid);
    }

    public void e(Integer[] numArr) {
        if (numArr != null) {
            HashSet hashSet = new HashSet();
            for (Integer num : numArr) {
                String[] packagesForUid = this.f175c.getPackageManager().getPackagesForUid(num.intValue());
                if (packagesForUid != null && packagesForUid.length != 0) {
                    for (String str : packagesForUid) {
                        hashSet.add(str);
                    }
                }
            }
            c((String[]) hashSet.toArray(new String[hashSet.size()]));
        }
    }

    public void f(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("#######DeviceIdlePolicyHelper#######");
        this.f178f.dump(fileDescriptor, printWriter, strArr);
    }

    public void g() {
        try {
            t();
        } catch (RemoteException e2) {
            Log.e(f170g, "exitDeviceIdleMode", e2);
        }
    }

    public void h() {
        try {
            ServiceManager.getService("deviceidle").dump(FileDescriptor.out, new String[]{"force-idle"});
        } catch (RemoteException e2) {
            Log.e(f170g, "forceToDeviceIdleMode", e2);
        }
    }

    public boolean k(String str) {
        ArraySet<String> arraySet = f172i;
        if (arraySet == null || arraySet.isEmpty()) {
            return false;
        }
        return f172i.contains(str);
    }

    public Boolean m(int i2) {
        String[] packagesForUid = this.f175c.getPackageManager().getPackagesForUid(i2);
        if (packagesForUid == null || packagesForUid.length == 0) {
            return Boolean.FALSE;
        }
        for (String str : packagesForUid) {
            if (!l(str).booleanValue()) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    public Boolean n(int i2) {
        String[] packagesForUid = this.f175c.getPackageManager().getPackagesForUid(i2);
        if (packagesForUid == null || packagesForUid.length == 0) {
            return Boolean.FALSE;
        }
        for (String str : packagesForUid) {
            if (!j(str, UserHandle.getUserId(i2)).booleanValue()) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    public void o(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        try {
            this.f178f.log("remove " + str);
            this.f176d.removePowerSaveWhitelistApp(str);
        } catch (Exception e2) {
            Log.e(f170g, "removePowerSaveWhitelistApp: " + str, e2);
        }
    }

    public void p(String[] strArr) {
        if (strArr == null || strArr.length == 0) {
            return;
        }
        try {
            this.f178f.log("remove " + Arrays.toString(strArr));
            CommonAdapter.removePowerSaveWhitelistApps(this.f176d, strArr);
        } catch (Exception e2) {
            Log.e(f170g, "removePowerSaveWhitelistApps: " + Arrays.toString(strArr), e2);
        }
    }

    public void q(Integer[] numArr) {
        if (numArr != null) {
            HashSet hashSet = new HashSet();
            for (Integer num : numArr) {
                String[] packagesForUid = this.f175c.getPackageManager().getPackagesForUid(num.intValue());
                if (packagesForUid != null && packagesForUid.length != 0) {
                    for (String str : packagesForUid) {
                        hashSet.add(str);
                    }
                }
            }
            p((String[]) hashSet.toArray(new String[hashSet.size()]));
        }
    }

    public void s(int i2, boolean z2) {
        String[] packagesForUid = this.f175c.getPackageManager().getPackagesForUid(i2);
        if (packagesForUid == null || packagesForUid.length == 0) {
            return;
        }
        for (String str : packagesForUid) {
            r(str, z2, UserHandle.getUserId(i2));
        }
    }
}
