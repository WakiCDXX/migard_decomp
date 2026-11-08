package o;

import android.content.Context;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.feedbackcontrol.ThermalManager;
import com.miui.powerkeeper.unionpower.traceextension.TraceExtensionService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import miui.yellowpage.Tag;
import org.json.JSONObject;

/* JADX INFO: compiled from: ThermalTriggerHandler.java */
/* JADX INFO: loaded from: classes.dex */
public class d extends o.b {

    /* JADX INFO: renamed from: s, reason: collision with root package name */
    public static final boolean f2101s = Log.isLoggable("TraceExtension.trigger_thermal", 3);

    /* JADX INFO: renamed from: t, reason: collision with root package name */
    private static final Integer f2102t = 43;

    /* JADX INFO: renamed from: u, reason: collision with root package name */
    private static final Integer f2103u = 40;

    /* JADX INFO: renamed from: v, reason: collision with root package name */
    private static final Integer f2104v = 2;

    /* JADX INFO: renamed from: w, reason: collision with root package name */
    private static final Integer f2105w = 5;

    /* JADX INFO: renamed from: x, reason: collision with root package name */
    private static final Integer f2106x = 5;

    /* JADX INFO: renamed from: y, reason: collision with root package name */
    private static final Integer f2107y = 3;

    /* JADX INFO: renamed from: z, reason: collision with root package name */
    private static d f2108z;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private Context f2109b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private boolean f2110c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private boolean f2111d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private boolean f2112e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private Integer f2113f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private Integer f2114g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private ArrayList<b> f2115h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private Integer f2116i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private Integer f2117j;

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private Integer f2118k;

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    private Integer f2119l;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    private Integer f2120m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    private Integer f2121n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    private ArrayList<Integer> f2122o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    private HashMap<Integer, Runnable> f2123p;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    private PowerKeeperInterface.d f2124q;

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    private final PowerKeeperInterface.m f2125r;

    /* JADX INFO: compiled from: ThermalTriggerHandler.java */
    class a implements PowerKeeperInterface.m {
        a() {
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.m
        public void onBatteryChanged(int i2, int i3, int i4, int i5, int i6, int i7) {
            if (d.f2101s) {
                Log.d("TraceExtension.trigger_thermal", "onBatteryChanged temp=" + i5 + " status=" + i2);
            }
            if (i2 == 2 || i2 == 5) {
                d.this.r();
                return;
            }
            if (d.this.f2112e) {
                if (i5 <= d.this.f2117j.intValue()) {
                    d.this.r();
                }
            } else if (i5 >= d.this.f2116i.intValue()) {
                d.this.f2112e = true;
                d.this.sendEmptyMessage(2);
            }
        }
    }

    /* JADX INFO: compiled from: ThermalTriggerHandler.java */
    private class b {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        int f2127a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        int f2128b;

        b(int i2, int i3) {
            this.f2127a = i2;
            this.f2128b = i3;
        }

        public String toString() {
            return "{temp=" + this.f2127a + ", delta=" + this.f2128b + '}';
        }
    }

    private d(Context context, Looper looper) {
        super(looper);
        this.f2110c = false;
        this.f2111d = false;
        this.f2112e = false;
        this.f2113f = 0;
        this.f2114g = -1;
        this.f2115h = new ArrayList<>();
        this.f2116i = f2102t;
        this.f2117j = f2103u;
        this.f2118k = f2104v;
        this.f2119l = f2105w;
        this.f2120m = f2106x;
        this.f2121n = f2107y;
        this.f2122o = new ArrayList<>();
        this.f2123p = new HashMap<>();
        this.f2125r = new a();
        this.f2109b = context;
    }

    public static d m(Context context, Looper looper) {
        if (n() && f2108z == null) {
            f2108z = new d(context, looper);
        }
        return f2108z;
    }

    public static boolean n() {
        return TraceExtensionService.v() && SystemProperties.getBoolean("persist.sys.traceextension.thermal", false);
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) throws Throwable {
        int i2 = message.what;
        if (i2 != 0) {
            if (i2 == 1) {
                String str = (String) message.obj;
                if (f2101s) {
                    Log.d("TraceExtension.trigger_thermal", str + " mEnable:" + this.f2110c);
                }
                t(str);
                return;
            }
            if (i2 != 2) {
                if (i2 != 3) {
                    if (f2101s) {
                        Log.e("TraceExtension.trigger_thermal", "error msg info");
                        return;
                    }
                    return;
                } else {
                    if (f2101s) {
                        Log.d("TraceExtension.trigger_thermal", "MSG_ID_UPDATE_TRIGGER_ENABLE: " + this.f2110c);
                    }
                    s(this.f2110c);
                    return;
                }
            }
            if (f2101s) {
                Log.d("TraceExtension.trigger_thermal", "MSG_ID_UPDATE_STATE_CYCLE: " + this.f2110c);
            }
            if (!TraceExtensionService.u() || !this.f2110c) {
                r();
            } else {
                sendEmptyMessageDelayed(2, this.f2120m.intValue() * 60000);
                u();
            }
        }
    }

    public void k(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("ThermalTriggerHandler");
        printWriter.println("  enable=" + this.f2110c);
        printWriter.println("  TempTrigger=" + this.f2116i);
        printWriter.println("  TempClear=" + this.f2117j);
        printWriter.println("  TempDelta=" + this.f2118k);
        printWriter.println("  TriggerTimes=" + this.f2119l);
        printWriter.println("  TriggerDozeMin=" + this.f2120m);
        printWriter.println("  TriggerLevel=" + this.f2121n);
        printWriter.println("  TriggerThresholds=" + this.f2122o);
        printWriter.println("  isWhiteFg=" + this.f2111d);
        printWriter.println("  isTriggerBegin=" + this.f2112e);
        printWriter.println("  curCount=" + this.f2113f);
        printWriter.println("  lastTemp=" + this.f2114g);
        printWriter.println("  TriggerRecord=" + this.f2115h);
        printWriter.println("end ThermalTriggerHandler");
    }

    int l() {
        if (this.f2122o.isEmpty()) {
            return 0;
        }
        Integer num = this.f2114g;
        int iIntValue = num.intValue();
        int iIndexOf = this.f2122o.indexOf(num);
        int size = this.f2122o.size();
        if (iIndexOf != -1) {
            return iIndexOf;
        }
        int i2 = size - 1;
        if (iIntValue > this.f2122o.get(i2).intValue()) {
            return i2;
        }
        for (Integer num2 : this.f2122o) {
            if (iIntValue < num2.intValue()) {
                iIndexOf = this.f2122o.indexOf(num2) - 1;
                break;
            }
        }
        if (iIndexOf == -1) {
            return 0;
        }
        return iIndexOf;
    }

    boolean o() {
        if (!this.f2110c || !this.f2112e || this.f2113f.intValue() < this.f2121n.intValue()) {
            return false;
        }
        TraceExtensionService.A("TraceExtension.trigger_thermal", l());
        return true;
    }

    void p(JSONObject jSONObject) {
        ArrayList arrayList = new ArrayList();
        Iterator<String> itKeys = jSONObject.keys();
        while (itKeys.hasNext()) {
            arrayList.add(Integer.valueOf(Integer.parseInt(itKeys.next())));
        }
        arrayList.sort(new Comparator() { // from class: o.c
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return ((Integer) obj).compareTo((Integer) obj2);
            }
        });
        Iterator it = arrayList.iterator();
        int i2 = 0;
        while (it.hasNext()) {
            this.f2123p.put(Integer.valueOf(i2), d(jSONObject.optJSONObject(((Integer) it.next()).toString())));
            i2++;
        }
        this.f2122o.addAll(arrayList);
    }

    void q() {
        if (f2101s) {
            Log.d("TraceExtension.trigger_thermal", "resetConfig");
        }
        this.f2116i = f2102t;
        this.f2117j = f2103u;
        this.f2118k = f2104v;
        this.f2119l = f2105w;
        this.f2120m = f2106x;
        this.f2121n = f2107y;
    }

    void r() {
        if (f2101s) {
            Log.d("TraceExtension.trigger_thermal", "resetStatus");
        }
        if (hasMessages(2)) {
            removeMessages(2);
        }
        this.f2112e = false;
        this.f2115h.clear();
        this.f2113f = 0;
        this.f2114g = -1;
    }

    void s(boolean z2) {
        if (!n()) {
            if (f2101s) {
                Log.w("TraceExtension.trigger_thermal", "noSupport");
            }
            z2 = false;
        }
        if (z2 == this.f2110c && TraceExtensionService.w() == this.f2111d) {
            return;
        }
        PowerKeeperManager powerKeeperManager = PowerKeeperManager.getInstance();
        if (!z2 || TraceExtensionService.w()) {
            PowerKeeperInterface.d dVar = this.f2124q;
            if (dVar != null) {
                if (powerKeeperManager != null) {
                    powerKeeperManager.unregisterBatteryChangedListener(dVar);
                }
                this.f2124q.a();
                this.f2124q = null;
            }
            Log.d("TraceExtension.trigger_thermal", "unregister battery status listener.");
            r();
        } else {
            r();
            if (powerKeeperManager != null && TraceExtensionService.u()) {
                PowerKeeperInterface.d dVar2 = new PowerKeeperInterface.d(f2108z, this.f2125r);
                this.f2124q = dVar2;
                powerKeeperManager.registerBatteryChangedListener(dVar2);
                Log.d("TraceExtension.trigger_thermal", "register battery status listener.");
            }
        }
        this.f2111d = TraceExtensionService.w();
        this.f2110c = z2;
    }

    void t(String str) {
        if (!this.f2123p.isEmpty()) {
            f("TraceExtension.trigger_thermal", this.f2123p.keySet());
            this.f2123p.clear();
            this.f2122o.clear();
        }
        if (str == null || str.isEmpty()) {
            if (this.f2110c) {
                s(false);
                q();
                return;
            }
            return;
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            this.f2116i = Integer.valueOf(jSONObject.optInt("temp_trig", f2102t.intValue()));
            this.f2117j = Integer.valueOf(jSONObject.optInt("temp_clr", f2103u.intValue()));
            this.f2118k = Integer.valueOf(jSONObject.optInt("delta", f2104v.intValue()));
            this.f2119l = Integer.valueOf(jSONObject.optInt("times", f2105w.intValue()));
            this.f2120m = Integer.valueOf(jSONObject.optInt("doze_min", f2106x.intValue()));
            this.f2121n = Integer.valueOf(jSONObject.optInt("level", f2107y.intValue()));
            JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject(Tag.TagServicesData.SERVICE_ACTIONS);
            if (jSONObjectOptJSONObject != null) {
                p(jSONObjectOptJSONObject);
            }
            if (!this.f2123p.isEmpty()) {
                e("TraceExtension.trigger_thermal", this.f2123p);
            }
            s(true);
        } catch (Exception e2) {
            Log.e("TraceExtension.trigger_thermal", e2.getMessage());
        }
    }

    void u() throws Throwable {
        int boardThermalTemp = ThermalManager.ThermalTempInfoHelper.getBoardThermalTemp();
        if (this.f2114g.intValue() < 0) {
            this.f2114g = Integer.valueOf(boardThermalTemp);
            return;
        }
        if (this.f2115h.size() == this.f2119l.intValue()) {
            this.f2113f = Integer.valueOf(this.f2113f.intValue() - (this.f2115h.get(0).f2128b > this.f2118k.intValue() ? 1 : 0));
            this.f2115h.remove(0);
        }
        this.f2115h.add(new b(boardThermalTemp, boardThermalTemp - this.f2114g.intValue()));
        this.f2113f = Integer.valueOf(this.f2113f.intValue() + (boardThermalTemp - this.f2114g.intValue() < this.f2118k.intValue() ? 0 : 1));
        this.f2114g = Integer.valueOf(boardThermalTemp);
        if (f2101s) {
            Log.d("TraceExtension.trigger_thermal", "updateStatus mTriggerRecord: " + this.f2115h + " mCurLevel: " + this.f2113f);
        }
        if (o()) {
            TraceExtensionService.z("TraceExtension.trigger_thermal", "updateStatus mTriggerRecord: " + this.f2115h);
            r();
        }
    }
}
