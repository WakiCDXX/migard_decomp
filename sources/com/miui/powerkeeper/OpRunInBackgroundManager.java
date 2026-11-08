package com.miui.powerkeeper;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.Handler;
import android.util.SparseBooleanArray;
import com.android.internal.util.IndentingPrintWriter;
import com.google.android.collect.Maps;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class OpRunInBackgroundManager {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private final PowerKeeperManager f225b;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private AppOpsManager f231h;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private final Object f226c = new Object();

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private final Map<String, SparseBooleanArray> f227d = Maps.newHashMap();

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private final Map<String, Boolean> f228e = Maps.newHashMap();

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private final SparseBooleanArray f229f = new SparseBooleanArray();

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private boolean f230g = false;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private String[] f232i = {"com.android.deskclock", "com.tencent.qqmusic", "android.content.syncmanager.cts", "android.content.syncmanager.cts.app1", "android.content.syncmanager.cts.app2", "com.baidu.carlife.xiaomi", "com.mi.car.mobile"};

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private Handler f224a = new Handler(PowerKeeperManager.getThread().getLooper());

    class a implements Runnable {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final /* synthetic */ boolean f233a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        final /* synthetic */ int f234b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        final /* synthetic */ String f235c;

        /* JADX INFO: renamed from: d, reason: collision with root package name */
        final /* synthetic */ String f236d;

        a(boolean z2, int i2, String str, String str2) {
            this.f233a = z2;
            this.f234b = i2;
            this.f235c = str;
            this.f236d = str2;
        }

        @Override // java.lang.Runnable
        public void run() {
            synchronized (OpRunInBackgroundManager.this.f226c) {
                try {
                    if (!this.f233a || !OpRunInBackgroundManager.this.f225b.getProcessObserver().isUidForegroundService(this.f234b)) {
                        OpRunInBackgroundManager.this.n(this.f235c, this.f234b, this.f236d, this.f233a);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    class b implements Runnable {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final /* synthetic */ String f238a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        final /* synthetic */ boolean f239b;

        b(String str, boolean z2) {
            this.f238a = str;
            this.f239b = z2;
        }

        @Override // java.lang.Runnable
        public void run() {
            synchronized (OpRunInBackgroundManager.this.f226c) {
                OpRunInBackgroundManager.this.m(this.f238a, this.f239b);
            }
        }
    }

    public OpRunInBackgroundManager(Context context, PowerKeeperManager powerKeeperManager) {
        this.f231h = null;
        this.f231h = (AppOpsManager) context.getSystemService("appops");
        this.f225b = powerKeeperManager;
    }

    private void e(int i2, String str, boolean z2) {
        this.f231h.setMode(70, i2, str, (!z2 || j(str)) ? 0 : 1);
    }

    private void g() {
        boolean z2;
        for (Map.Entry<String, Boolean> entry : this.f228e.entrySet()) {
            if (entry.getValue() != null) {
                z2 = true;
                if (true == entry.getValue().booleanValue()) {
                    this.f230g = z2;
                }
            }
        }
        z2 = false;
        this.f230g = z2;
    }

    private void h(int i2, String str) {
        boolean z2;
        Iterator<Map.Entry<String, SparseBooleanArray>> it = this.f227d.entrySet().iterator();
        while (true) {
            z2 = false;
            if (!it.hasNext()) {
                break;
            }
            SparseBooleanArray value = it.next().getValue();
            if (value != null) {
                z2 = true;
                if (true == value.get(i2, false)) {
                    break;
                }
            }
        }
        this.f229f.put(i2, z2);
        e(i2, str, z2);
    }

    private boolean j(String str) {
        boolean z2 = false;
        for (String str2 : this.f232i) {
            z2 = z2 || str.equals(str2);
        }
        return z2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void m(String str, boolean z2) {
        this.f228e.put(str, Boolean.valueOf(z2));
        g();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void n(String str, int i2, String str2, boolean z2) {
        SparseBooleanArray value;
        if (!this.f227d.containsKey(str)) {
            SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
            sparseBooleanArray.put(i2, z2);
            this.f227d.put(str, sparseBooleanArray);
            h(i2, str2);
            return;
        }
        for (Map.Entry<String, SparseBooleanArray> entry : this.f227d.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(str) && (value = entry.getValue()) != null) {
                value.put(i2, z2);
                h(i2, str2);
                return;
            }
        }
    }

    public boolean f(int i2, String str) {
        return this.f231h.noteOpNoThrow(70, i2, str) != 0;
    }

    public void i(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        int i2;
        synchronized (this.f226c) {
            try {
                IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
                indentingPrintWriter.println("#######dump OpRunInBackgroundManager#######");
                indentingPrintWriter.println("#######feature config state#######");
                for (Map.Entry<String, Boolean> entry : this.f228e.entrySet()) {
                    String key = entry.getKey();
                    Boolean value = entry.getValue();
                    if (key != null && value != null) {
                        indentingPrintWriter.println("    name  :  " + key + "    status:  " + value);
                    }
                }
                indentingPrintWriter.println("#######feature actual state####### " + this.f230g);
                indentingPrintWriter.println("#######config state#######");
                Iterator<Map.Entry<String, SparseBooleanArray>> it = this.f227d.entrySet().iterator();
                while (true) {
                    i2 = 0;
                    if (!it.hasNext()) {
                        break;
                    }
                    Map.Entry<String, SparseBooleanArray> next = it.next();
                    String key2 = next.getKey();
                    SparseBooleanArray value2 = next.getValue();
                    if (key2 != null && value2 != null) {
                        indentingPrintWriter.println("    name    " + key2);
                        while (i2 < value2.size()) {
                            if (true == value2.valueAt(i2)) {
                                indentingPrintWriter.println("    uid : " + value2.keyAt(i2) + " true");
                            }
                            i2++;
                        }
                    }
                }
                indentingPrintWriter.println("#######actual state#######");
                while (i2 < this.f229f.size()) {
                    if (true == this.f229f.valueAt(i2)) {
                        indentingPrintWriter.println("    uid : " + this.f229f.keyAt(i2) + " true");
                    }
                    i2++;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void k(String str, int i2, String str2, boolean z2) {
        this.f224a.post(new a(z2, i2, str, str2));
    }

    public void l(String str, boolean z2) {
        this.f224a.post(new b(str, z2));
    }
}
