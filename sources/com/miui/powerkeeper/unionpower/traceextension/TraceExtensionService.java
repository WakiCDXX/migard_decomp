package com.miui.powerkeeper.unionpower.traceextension;

import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.util.LocalLog;
import android.util.Log;
import com.android.internal.os.BackgroundThread;
import com.miui.powerkeeper.cloudcontrol.Constants;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.utils.FileUtil;
import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class TraceExtensionService extends Service {

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    public static final boolean f631k = Log.isLoggable("TraceExtension", 3);

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    public static final Set<Integer> f632l;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    private static d f633m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    private static boolean f634n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    private static boolean f635o;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private o.d f639d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private ContentObserver f640e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private c f641f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private com.miui.powerkeeper.unionpower.traceextension.a f642g;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private int f636a = -1;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private int f637b = 5;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private long f638c = 1800000;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private LocalLog f643h = new LocalLog(256);

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private int f644i = 0;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private long f645j = -1;

    class a extends ContentObserver {
        a(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z2) {
            if (TraceExtensionService.f631k) {
                Log.d("TraceExtension", "onCloudChange");
            }
            TraceExtensionService.this.F();
        }
    }

    private static class b {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final String f647a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        final HashMap<Integer, Runnable> f648b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        final ArrayList<Integer> f649c;

        public b(String str, HashMap<Integer, Runnable> map, Set<Integer> set) {
            this.f647a = str;
            if (map != null) {
                HashMap<Integer, Runnable> map2 = new HashMap<>();
                this.f648b = map2;
                map2.putAll(map);
            } else {
                this.f648b = null;
            }
            if (set == null) {
                this.f649c = null;
                return;
            }
            ArrayList<Integer> arrayList = new ArrayList<>();
            this.f649c = arrayList;
            arrayList.addAll(set);
        }
    }

    private class c extends FileObserver {
        public c(String str) {
            super(str);
            TraceExtensionService.this.C();
        }

        @Override // android.os.FileObserver
        public void onEvent(int i2, String str) {
            if (i2 == 2) {
                TraceExtensionService.this.C();
                boolean z2 = TraceExtensionService.this.f636a >= 55 || TraceExtensionService.f632l.contains(Integer.valueOf(TraceExtensionService.this.f636a % 30));
                if (z2 != TraceExtensionService.f635o) {
                    TraceExtensionService.f635o = z2;
                    if (TraceExtensionService.this.f639d != null) {
                        TraceExtensionService.this.f639d.sendEmptyMessage(3);
                    }
                }
            }
        }
    }

    private class d extends Handler {
        public d(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            HashMap<Integer, Runnable> map;
            ArrayList<Integer> arrayList;
            int i2 = message.what;
            if (i2 == 0) {
                TraceExtensionService.this.t();
                return;
            }
            if (i2 == 1) {
                TraceExtensionService.this.F();
                return;
            }
            if (i2 != 2) {
                if (i2 == 4) {
                    b bVar = (b) message.obj;
                    if (TraceExtensionService.f631k) {
                        Log.d("TraceExtension", "MSG_ID_ACTION_REGISTER: " + bVar.f647a);
                    }
                    if (TraceExtensionService.this.f642g == null || (map = bVar.f648b) == null) {
                        return;
                    }
                    for (Map.Entry<Integer, Runnable> entry : map.entrySet()) {
                        TraceExtensionService.this.f642g.f(bVar.f647a, entry.getKey().intValue(), entry.getValue());
                    }
                    return;
                }
                if (i2 != 5) {
                    if (i2 != 6) {
                        return;
                    }
                    TraceExtensionService.this.f643h.log((String) message.obj);
                    return;
                }
                b bVar2 = (b) message.obj;
                if (TraceExtensionService.f631k) {
                    Log.d("TraceExtension", "MSG_ID_ACTION_UNREGISTER: " + bVar2.f647a + ", " + bVar2.f649c);
                }
                if (TraceExtensionService.this.f642g == null || (arrayList = bVar2.f649c) == null) {
                    return;
                }
                Iterator<Integer> it = arrayList.iterator();
                while (it.hasNext()) {
                    TraceExtensionService.this.f642g.g(bVar2.f647a, it.next().intValue());
                }
                return;
            }
            int i3 = message.arg1;
            String str = (String) message.obj;
            boolean z2 = TraceExtensionService.f631k;
            if (z2) {
                Log.d("TraceExtension", "MSG_ID_COLLECTION: " + TraceExtensionService.f634n + TraceExtensionService.f635o + TraceExtensionService.this.f644i);
            }
            if (!TraceExtensionService.f634n || TraceExtensionService.f635o) {
                return;
            }
            long jCurrentTimeMillis = System.currentTimeMillis();
            if (z2) {
                Log.d("TraceExtension", "now=" + jCurrentTimeMillis + " next=" + TraceExtensionService.this.f645j);
            }
            if (TraceExtensionService.this.f645j <= jCurrentTimeMillis) {
                if (TraceExtensionService.this.f644i >= TraceExtensionService.this.f637b) {
                    TraceExtensionService.this.f645j = TraceExtensionService.y(1);
                    if (TraceExtensionService.this.f642g != null) {
                        TraceExtensionService.this.f642g.b();
                    }
                    TraceExtensionService.this.f644i = 0;
                    return;
                }
                TraceExtensionService.this.f644i++;
                if (TraceExtensionService.this.f644i < TraceExtensionService.this.f637b) {
                    TraceExtensionService traceExtensionService = TraceExtensionService.this;
                    traceExtensionService.f645j = jCurrentTimeMillis + traceExtensionService.f638c;
                }
                if (z2) {
                    Log.d("TraceExtension", "tag=" + str + " index=" + i3);
                }
                TraceExtensionService.this.s(str, i3);
            }
        }
    }

    static {
        HashSet hashSet = new HashSet();
        f632l = hashSet;
        hashSet.add(9);
        hashSet.add(10);
        hashSet.add(13);
        hashSet.add(15);
        hashSet.add(16);
        hashSet.add(20);
        hashSet.add(23);
        f634n = false;
        f635o = false;
    }

    public static void A(String str, int i2) {
        d dVar = f633m;
        if (dVar != null) {
            dVar.obtainMessage(2, i2, 0, str).sendToTarget();
        }
    }

    public static void B(String str, HashMap<Integer, Runnable> map) {
        if (f633m != null) {
            f633m.obtainMessage(4, new b(str, map, null)).sendToTarget();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void C() {
        try {
            String fileString = FileUtil.getFileString("/sys/class/thermal/thermal_message/sconfig");
            if (fileString != null && !fileString.isEmpty()) {
                this.f636a = Integer.parseInt(fileString);
            }
            if (f631k) {
                Log.d("TraceExtension", "thermalId=" + this.f636a);
            }
        } catch (Exception e2) {
            Log.e("TraceExtension", "updateThermalId error=" + e2.getMessage());
            this.f636a = -1;
        }
    }

    public static void D(String str, Set<Integer> set) {
        if (f633m != null) {
            f633m.obtainMessage(5, new b(str, null, set)).sendToTarget();
        }
    }

    private void E() {
        this.f645j = -1L;
        this.f644i = 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void F() {
        String string = SimpleSettings.Misc.getString(this, "trace_extension", "");
        boolean z2 = f631k;
        if (z2) {
            Log.d("TraceExtension", "updating: " + string + ", FunSwitch: " + f634n);
        }
        this.f643h.log("updating: " + string + ", FunSwitch: " + f634n);
        if (string.isEmpty() && f634n) {
            G(false);
            H(new JSONObject());
            E();
            return;
        }
        try {
            JSONObject jSONObject = new JSONObject(string);
            boolean zOptBoolean = jSONObject.optBoolean("funcSwitch", f634n);
            int iOptInt = jSONObject.optInt("count", this.f637b);
            long jOptLong = jSONObject.optLong("doze_period", this.f638c);
            x(jSONObject);
            this.f637b = iOptInt;
            this.f638c = jOptLong;
            G(zOptBoolean);
            H(jSONObject);
            E();
            if (z2) {
                Log.d("TraceExtension", "update: funcSwitch= " + f634n + ", mCount=" + this.f637b + ", mDozePeriod=" + this.f638c + ", sIsWhiteFg=" + f635o);
            }
        } catch (Exception e2) {
            Log.w("TraceExtension", "updateConfig err=" + e2.getMessage());
        }
    }

    private void G(boolean z2) {
        if (z2 == f634n) {
            return;
        }
        c cVar = this.f641f;
        if (cVar != null) {
            if (z2) {
                cVar.startWatching();
            } else {
                cVar.stopWatching();
            }
        }
        int i2 = this.f636a;
        f635o = i2 >= 55 || f632l.contains(Integer.valueOf(i2 % 30));
        f634n = z2;
        if (f631k) {
            Log.d("TraceExtension", "updateFuncSwitch sFunSwitch: " + f634n);
        }
    }

    private void H(JSONObject jSONObject) {
        if (this.f639d != null) {
            this.f639d.sendMessage(this.f639d.obtainMessage(1, jSONObject.optString(Constants.REGION_NAME_THERMAL, "")));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void s(String str, int i2) {
        this.f643h.log("collection: " + str + " - " + i2);
        com.miui.powerkeeper.unionpower.traceextension.a aVar = this.f642g;
        if (aVar != null) {
            aVar.c(str, i2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void t() {
        this.f642g = com.miui.powerkeeper.unionpower.traceextension.a.d();
        this.f639d = o.d.m(this, BackgroundThread.getHandler().getLooper());
        try {
            if (new File("/sys/class/thermal/thermal_message/sconfig").exists()) {
                this.f641f = new c("/sys/class/thermal/thermal_message/sconfig");
            }
        } catch (Exception e2) {
            Log.e("TraceExtension", "failure observer thermalId: " + e2.getMessage());
        }
        F();
        if (this.f640e == null) {
            this.f640e = new a(f633m);
            getContentResolver().registerContentObserver(SimpleSettings.Misc.getUriFor("trace_extension"), false, this.f640e);
        }
    }

    public static boolean u() {
        return f634n;
    }

    public static boolean v() {
        boolean z2 = SystemProperties.getBoolean("persist.sys.unionpower.traceextension", false);
        if (!z2) {
            Log.d("TraceExtension", "noSupport");
        }
        return z2;
    }

    public static boolean w() {
        return f635o;
    }

    private void x(JSONObject jSONObject) {
        com.miui.powerkeeper.unionpower.traceextension.a aVar = this.f642g;
        if (aVar != null) {
            aVar.e(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static long y(int i2) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        calendar.add(5, i2);
        return calendar.getTime().getTime();
    }

    public static void z(String str, String str2) {
        d dVar = f633m;
        if (dVar != null) {
            dVar.obtainMessage(6, str + ": " + str2).sendToTarget();
        }
    }

    @Override // android.app.Service
    protected void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        int i2;
        printWriter.println("dump of TraceExtensionService:");
        printWriter.println(" thermalId=" + this.f636a);
        printWriter.println(" count=" + this.f637b);
        printWriter.println(" dozePeriod=" + this.f638c);
        printWriter.println(" curCount=" + this.f644i);
        printWriter.println(" nextPeriod=" + this.f645j);
        o.d dVar = this.f639d;
        if (dVar != null) {
            dVar.k(fileDescriptor, printWriter, strArr);
        }
        this.f643h.dump(fileDescriptor, printWriter, strArr);
        for (int i3 = 0; i3 < strArr.length; i3++) {
            if (strArr[i3].equalsIgnoreCase("collect") && (i2 = i3 + 1) < strArr.length) {
                try {
                    String str = strArr[i2];
                    int i4 = i3 + 2;
                    int i5 = i4 < strArr.length ? Integer.parseInt(strArr[i4]) : 0;
                    d dVar2 = f633m;
                    if (dVar2 != null) {
                        dVar2.obtainMessage(2, i5, 0, str).sendToTarget();
                        printWriter.println(" collect debug:" + str + " - " + i5);
                    }
                } catch (Exception e2) {
                    printWriter.println(" collect debug err:" + e2.getMessage());
                }
            }
        }
        printWriter.println("end TraceExtensionService:");
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        if (UserHandle.myUserId() != 0) {
            stopSelf();
        } else {
            if (v()) {
                return;
            }
            stopSelf();
        }
    }

    @Override // android.app.Service
    public void onDestroy() {
        c cVar;
        if (f634n && (cVar = this.f641f) != null) {
            cVar.stopWatching();
            this.f641f = null;
        }
        if (this.f640e != null) {
            getContentResolver().unregisterContentObserver(this.f640e);
        }
        com.miui.powerkeeper.unionpower.traceextension.a aVar = this.f642g;
        if (aVar != null) {
            aVar.a();
        }
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i2, int i3) {
        if (UserHandle.myUserId() != 0) {
            stopSelf();
            return 1;
        }
        if (!v()) {
            stopSelf();
            return 1;
        }
        d dVar = new d(BackgroundThread.getHandler().getLooper());
        f633m = dVar;
        dVar.sendEmptyMessage(0);
        return 1;
    }
}
