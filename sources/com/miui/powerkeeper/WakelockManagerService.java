package com.miui.powerkeeper;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.IndentingPrintWriter;
import com.google.android.collect.Maps;
import com.google.android.collect.Sets;
import com.miui.whetstone.server.IWhetstoneActivityManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class WakelockManagerService extends Service {

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static final String f325f = WakelockManagerService.class.getSimpleName();

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private static final boolean f326g = Build.IS_DEBUGGABLE;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private LocalService f327a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private Handler f328b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private final Map<ComponentName, b> f329c = Maps.newHashMap();

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private final SparseArray<Set<String>> f330d = new SparseArray<>();

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private final Object f331e = new Object();

    public final class LocalService extends IWakelockManager.Stub {
        public LocalService() {
        }

        @Override // com.miui.powerkeeper.IWakelockManager
        public void setWakelockBlock(boolean z2, ComponentName componentName, int i2, List<String> list) throws RemoteException {
            WakelockManagerService.this.f328b.post(WakelockManagerService.this.new a(z2, componentName, i2, list));
        }
    }

    private final class a implements Runnable {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private ComponentName f332a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        private int f333b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        private boolean f334c;

        /* JADX INFO: renamed from: d, reason: collision with root package name */
        private List<String> f335d;

        public a(boolean z2, ComponentName componentName, int i2, List<String> list) {
            this.f334c = z2;
            this.f332a = componentName;
            this.f333b = i2;
            this.f335d = list;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.f332a == null || this.f333b == -1) {
                return;
            }
            synchronized (WakelockManagerService.this.f331e) {
                WakelockManagerService.this.g(this.f334c, this.f332a, this.f333b, this.f335d);
            }
        }
    }

    class b {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        ComponentName f337a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        SparseArray<Set<String>> f338b = new SparseArray<>();

        public b(ComponentName componentName) {
            this.f337a = componentName;
        }

        public void a(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
            indentingPrintWriter.println("Component " + this.f337a.toString() + " Enable Uid Wakelock Block Array");
            indentingPrintWriter.increaseIndent();
            for (int i2 = 0; i2 < this.f338b.size(); i2++) {
                int iKeyAt = this.f338b.keyAt(i2);
                Set<String> setValueAt = this.f338b.valueAt(i2);
                indentingPrintWriter.print("uid: " + iKeyAt);
                if (setValueAt == null) {
                    indentingPrintWriter.print(", all tags blocked");
                } else {
                    indentingPrintWriter.print(", tag:");
                    Iterator<String> it = setValueAt.iterator();
                    while (it.hasNext()) {
                        indentingPrintWriter.print(" " + it.next());
                    }
                }
                indentingPrintWriter.println();
            }
            indentingPrintWriter.decreaseIndent();
        }

        public Set<String> b(int i2) {
            if (this.f338b.indexOfKey(i2) >= 0) {
                return this.f338b.get(i2);
            }
            return null;
        }

        public boolean c(int i2) {
            return this.f338b.indexOfKey(i2) >= 0;
        }

        public void d(boolean z2, int i2, List<String> list) {
            HashSet hashSetNewHashSet = null;
            if (this.f338b.indexOfKey(i2) < 0) {
                if (z2) {
                    if (list != null && list.size() != 0) {
                        hashSetNewHashSet = Sets.newHashSet();
                        hashSetNewHashSet.addAll(list);
                    }
                    this.f338b.put(i2, hashSetNewHashSet);
                    return;
                }
                Log.w(WakelockManagerService.f325f, "uid: " + i2 + " is unblock state");
                return;
            }
            Set<String> set = this.f338b.get(i2);
            if (z2) {
                if ((list == null || list.size() == 0) && set != null) {
                    this.f338b.put(i2, null);
                    return;
                }
                if (list != null && set != null) {
                    for (String str : list) {
                        if (!set.contains(str)) {
                            set.add(str);
                        }
                    }
                    return;
                }
                Log.w(WakelockManagerService.f325f, "uid: " + i2 + " is already all blocked");
                return;
            }
            if (list == null || list.size() == 0) {
                this.f338b.remove(i2);
                return;
            }
            if (set == null) {
                Log.w(WakelockManagerService.f325f, "cannot unblock uid: " + i2 + " wakelock since it is all blocked");
                return;
            }
            for (String str2 : list) {
                if (set.contains(str2)) {
                    set.remove(str2);
                }
            }
            if (set.size() == 0) {
                this.f338b.remove(i2);
            }
        }
    }

    private void e(int i2) {
        HashSet<String> hashSetNewHashSet = Sets.newHashSet();
        boolean z2 = false;
        for (b bVar : this.f329c.values()) {
            if (bVar.c(i2)) {
                Set<String> setB = bVar.b(i2);
                if (setB == null) {
                    z2 = true;
                    hashSetNewHashSet = null;
                    break;
                } else {
                    hashSetNewHashSet.addAll(setB);
                    z2 = true;
                }
            }
        }
        if (!z2) {
            if (this.f330d.indexOfKey(i2) >= 0) {
                this.f330d.remove(i2);
                f(i2, null, false);
                return;
            }
            return;
        }
        if (hashSetNewHashSet == null) {
            if (this.f330d.indexOfKey(i2) < 0) {
                this.f330d.put(i2, null);
                f(i2, null, true);
                return;
            } else {
                if (this.f330d.get(i2) != null) {
                    this.f330d.put(i2, null);
                    f(i2, null, true);
                    return;
                }
                return;
            }
        }
        if (this.f330d.indexOfKey(i2) < 0) {
            this.f330d.put(i2, hashSetNewHashSet);
            Iterator it = hashSetNewHashSet.iterator();
            while (it.hasNext()) {
                f(i2, (String) it.next(), true);
            }
            return;
        }
        Set<String> set = this.f330d.get(i2);
        if (set == null) {
            f(i2, null, false);
            this.f330d.put(i2, hashSetNewHashSet);
            Iterator it2 = hashSetNewHashSet.iterator();
            while (it2.hasNext()) {
                f(i2, (String) it2.next(), true);
            }
            return;
        }
        if (!hashSetNewHashSet.containsAll(set)) {
            HashSet hashSetNewHashSet2 = Sets.newHashSet();
            for (String str : set) {
                if (!hashSetNewHashSet.contains(str)) {
                    hashSetNewHashSet2.add(str);
                }
            }
            set.removeAll(hashSetNewHashSet2);
            Iterator it3 = hashSetNewHashSet2.iterator();
            while (it3.hasNext()) {
                f(i2, (String) it3.next(), false);
            }
        }
        if (set.containsAll(hashSetNewHashSet)) {
            return;
        }
        for (String str2 : hashSetNewHashSet) {
            if (!set.contains(str2)) {
                set.add(str2);
                f(i2, str2, true);
            }
        }
    }

    private void f(int i2, String str, boolean z2) {
        try {
            IWhetstoneActivityManager iWhetstoneActivityManagerAsInterface = IWhetstoneActivityManager.Stub.asInterface(ServiceManager.getService("whetstone.activity"));
            if (iWhetstoneActivityManagerAsInterface == null) {
                Log.d(f325f, "ws is null");
                return;
            }
            iWhetstoneActivityManagerAsInterface.getPowerKeeperPolicy().updateWakelockBlockedUid(i2, str, z2);
            Log.d(f325f, "updateWakelockBlockedUid, uid = " + i2 + ", tag = " + str + ", isBlocked = " + z2);
        } catch (RemoteException unused) {
            Log.e(f325f, "IWhetstoneActivityManager work abnormal");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void g(boolean z2, ComponentName componentName, int i2, List<String> list) {
        if (f326g) {
            Log.d(f325f, "updateWakelockInfoLocked, Component " + componentName.toString() + ", uid = " + i2 + ", enable = " + z2 + ", tagList = " + list);
        }
        b bVar = this.f329c.get(componentName);
        if (z2) {
            if (bVar == null) {
                bVar = new b(componentName);
                this.f329c.put(componentName, bVar);
            }
            bVar.d(true, i2, list);
        } else if (bVar != null) {
            bVar.d(false, i2, list);
        }
        e(i2);
    }

    @Override // android.app.Service
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        synchronized (this.f331e) {
            try {
                IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
                indentingPrintWriter.println("Enable Uid Wakelock Block Array");
                indentingPrintWriter.increaseIndent();
                for (int i2 = 0; i2 < this.f330d.size(); i2++) {
                    int iKeyAt = this.f330d.keyAt(i2);
                    Set<String> setValueAt = this.f330d.valueAt(i2);
                    indentingPrintWriter.print("uid: " + iKeyAt);
                    if (setValueAt == null) {
                        indentingPrintWriter.print(", all tags blocked");
                    } else {
                        indentingPrintWriter.print(", tag:");
                        Iterator<String> it = setValueAt.iterator();
                        while (it.hasNext()) {
                            indentingPrintWriter.print(" " + it.next());
                        }
                    }
                    indentingPrintWriter.println();
                }
                indentingPrintWriter.decreaseIndent();
                if (strArr.length == 0) {
                    Iterator<ComponentName> it2 = this.f329c.keySet().iterator();
                    while (it2.hasNext()) {
                        this.f329c.get(it2.next()).a(fileDescriptor, printWriter, strArr);
                    }
                } else if (strArr.length == 1) {
                    for (ComponentName componentName : this.f329c.keySet()) {
                        if (componentName.getClassName().equalsIgnoreCase(strArr[0])) {
                            this.f329c.get(componentName).a(fileDescriptor, printWriter, strArr);
                        }
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.f327a.asBinder();
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        this.f327a = new LocalService();
        this.f328b = new Handler(BackgroundThread.get().getLooper());
    }

    @Override // android.app.Service
    public void onDestroy() {
        this.f327a = null;
        this.f328b.removeCallbacksAndMessages(null);
        this.f328b = null;
        super.onDestroy();
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i2, int i3) {
        return 1;
    }
}
