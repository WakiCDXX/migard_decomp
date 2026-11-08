package d;

import android.os.Build;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import miui.migard.IMiGard;

/* JADX INFO: compiled from: MigardWhitelistUpdate.java */
/* JADX INFO: loaded from: classes.dex */
public class a {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static final String f1749c = "a";

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static boolean f1750d = Build.IS_DEBUGGABLE;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static a f1751e = new a();

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private IMiGard f1752a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private ArrayList<String> f1753b = new ArrayList<>();

    private a() {
        this.f1752a = null;
        try {
            if (this.f1752a == null) {
                this.f1752a = IMiGard.Stub.asInterface(ServiceManager.getService("migard"));
            }
        } catch (Exception e2) {
            this.f1752a = null;
            Log.e(f1749c, "get migard service failed:" + e2);
        }
    }

    public static a e() {
        return f1751e;
    }

    public void a(String str, boolean z2) {
        if (this.f1752a == null || str == null || str.isEmpty()) {
            return;
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(str);
        b(arrayList, z2);
    }

    public void b(List<String> list, boolean z2) {
        if (this.f1752a == null || list == null || list.isEmpty()) {
            return;
        }
        if (f1750d) {
            Log.d(f1749c, "addWhiteList " + list);
        }
        synchronized (this.f1753b) {
            list.removeAll(this.f1753b);
            if (!list.isEmpty()) {
                try {
                    this.f1752a.addGameCleanUserProtectList(list, z2);
                    this.f1753b.addAll(list);
                } catch (RemoteException e2) {
                    Log.e(f1749c, "add whilte list failed:" + e2);
                }
            }
        }
    }

    public void c(String str) {
        if (this.f1752a == null || str == null || str.isEmpty()) {
            return;
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(str);
        d(arrayList);
    }

    public void d(List<String> list) {
        if (this.f1752a == null || list == null || list.isEmpty()) {
            return;
        }
        if (f1750d) {
            Log.d(f1749c, "delWhiteList " + list);
        }
        synchronized (this.f1753b) {
            list.retainAll(this.f1753b);
            if (!list.isEmpty()) {
                try {
                    this.f1752a.removeGameCleanUserProtectList(list);
                    this.f1753b.removeAll(list);
                } catch (RemoteException e2) {
                    Log.e(f1749c, "del whilte list failed:" + e2);
                }
            }
        }
    }

    public void f(String str) {
        IMiGard iMiGard = this.f1752a;
        if (iMiGard == null || str == null) {
            return;
        }
        try {
            iMiGard.configPowerWhiteList(str);
        } catch (RemoteException e2) {
            Log.e(f1749c, "update power whilte list failed:" + e2);
        }
    }
}
