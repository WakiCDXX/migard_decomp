package j;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.util.Slog;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.unionpower.utils.UnionPowerConfig;
import com.miui.powerkeeper.utils.FileUtil;
import com.miui.powerkeeper.utils.ShellUtils;
import com.xiaomi.mipush.sdk.stat.db.MessageInfoContract;
import com.xiaomi.push.extension.PluginConstants;
import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import miui.util.FeatureParser;
import org.json.JSONObject;

/* JADX INFO: compiled from: CpuDozeHandler.java */
/* JADX INFO: loaded from: classes.dex */
public class e extends j.a {
    public static final boolean E;
    private static final boolean F;
    private static final boolean G;
    private static e H;

    /* JADX INFO: renamed from: u, reason: collision with root package name */
    private a f1917u;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private int[] f1901e = {-1, -1, -1};

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private final String f1902f = "/sys/module/unionpower/parameters/FRAME_MONITOR_ENABLE";

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private final String f1903g = "/sys/unionpower/frame_jank";

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private final boolean f1904h = e.f.j();

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private boolean f1905i = false;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private List<b> f1906j = new ArrayList(3);

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private Map<String, String> f1907k = new HashMap();

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    private Map<String, String> f1908l = new HashMap();

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    private String f1909m = "";

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    private String f1910n = "";

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    private boolean f1911o = false;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    private int f1912p = 1;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    private int f1913q = -1;

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    private boolean f1914r = false;

    /* JADX INFO: renamed from: s, reason: collision with root package name */
    private long f1915s = MessageInfoContract.TIMEOUT;

    /* JADX INFO: renamed from: t, reason: collision with root package name */
    private c f1916t = null;

    /* JADX INFO: renamed from: v, reason: collision with root package name */
    private long f1918v = 0;

    /* JADX INFO: renamed from: w, reason: collision with root package name */
    private Class<?> f1919w = null;

    /* JADX INFO: renamed from: x, reason: collision with root package name */
    private Class<?> f1920x = null;

    /* JADX INFO: renamed from: y, reason: collision with root package name */
    private Method f1921y = null;

    /* JADX INFO: renamed from: z, reason: collision with root package name */
    private Method f1922z = null;
    private Object A = null;
    private boolean B = false;
    private volatile boolean C = true;
    private int D = 0;

    /* JADX INFO: compiled from: CpuDozeHandler.java */
    private class a extends FileObserver {
        public a(String str) {
            super(str);
        }

        @Override // android.os.FileObserver
        public void onEvent(int i2, String str) throws Throwable {
            if (i2 == 2) {
                String fromFile = FileUtil.readFromFile("/sys/unionpower/frame_jank");
                e.H.obtainMessage(9, Boolean.valueOf(fromFile != null ? fromFile.trim().equals("1") : false)).sendToTarget();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: compiled from: CpuDozeHandler.java */
    class b {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        boolean f1924a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        int f1925b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        long f1926c;

        /* JADX INFO: renamed from: d, reason: collision with root package name */
        Set<String> f1927d;

        /* JADX INFO: renamed from: e, reason: collision with root package name */
        Map<String, c> f1928e;

        /* JADX INFO: renamed from: f, reason: collision with root package name */
        c f1929f;

        b(b bVar, String str) {
            if (str != null && !str.isEmpty()) {
                try {
                    JSONObject jSONObject = new JSONObject(str);
                    this.f1924a = jSONObject.optBoolean("multiScreen", false);
                    this.f1925b = jSONObject.optInt("touchStatus", 2);
                    this.f1926c = jSONObject.optLong("startTimeout", MessageInfoContract.TIMEOUT);
                    String strOptString = jSONObject.optString("white", "");
                    this.f1927d = strOptString.isEmpty() ? Collections.emptySet() : Set.of((Object[]) strOptString.split(","));
                    this.f1928e = new HashMap();
                    JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject("blackConf");
                    if (jSONObjectOptJSONObject != null) {
                        Iterator<String> itKeys = jSONObjectOptJSONObject.keys();
                        while (itKeys.hasNext()) {
                            String next = itKeys.next();
                            this.f1928e.put(next, e.this.new c(next, jSONObjectOptJSONObject.optJSONObject(next)));
                        }
                    }
                    JSONObject jSONObjectOptJSONObject2 = jSONObject.optJSONObject("commonConf");
                    if (jSONObjectOptJSONObject2 != null) {
                        this.f1929f = e.this.new c(null, jSONObjectOptJSONObject2);
                        return;
                    }
                    return;
                } catch (Exception e2) {
                    Log.e("UnionPower.CpuDoze", "parse err " + e2.getMessage());
                }
            }
            if (bVar == null) {
                this.f1924a = false;
                this.f1925b = 2;
                this.f1926c = MessageInfoContract.TIMEOUT;
                this.f1927d = Collections.emptySet();
                this.f1928e = Collections.emptyMap();
                this.f1929f = null;
                return;
            }
            this.f1924a = bVar.f1924a;
            this.f1925b = bVar.f1925b;
            this.f1926c = bVar.f1926c;
            this.f1927d = bVar.f1927d;
            this.f1928e = bVar.f1928e;
            this.f1929f = bVar.f1929f;
        }

        private void b() throws Throwable {
            long jUptimeMillis = SystemClock.uptimeMillis();
            if (jUptimeMillis < e.this.f1918v + PluginConstants.TTS_REVERT_VOLUME_TIME) {
                return;
            }
            if (e.this.f1916t.f1932b == null || e.this.f1916t.f1932b.isEmpty()) {
                e.this.J();
                e eVar = e.this;
                eVar.I(eVar.f1916t.f1933c);
                e.this.f1918v = jUptimeMillis;
                return;
            }
            e eVar2 = e.this;
            if (eVar2.C(eVar2.f1916t.f1932b)) {
                e.this.J();
                e eVar3 = e.this;
                eVar3.I(eVar3.f1916t.f1933c);
                e eVar4 = e.this;
                eVar4.G(null, eVar4.f1916t.f1932b, e.this.f1907k);
                e.this.f1918v = jUptimeMillis;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ boolean c(String str, String str2) {
            if (str2.length() > 2) {
                if (str2.startsWith(str + "#") && str2.endsWith("*")) {
                    return true;
                }
            }
            return false;
        }

        c d(final String str, String str2) {
            if (this.f1928e.isEmpty()) {
                return this.f1929f;
            }
            String str3 = str + "#" + str2;
            if (this.f1928e.containsKey(str3)) {
                return this.f1928e.get(str3);
            }
            for (String str4 : (Set) this.f1928e.keySet().stream().filter(new Predicate() { // from class: j.f
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return e.b.c(str, (String) obj);
                }
            }).collect(Collectors.toSet())) {
                if (str3.startsWith(str4.substring(0, str4.length() - 2))) {
                    return this.f1928e.get(str4);
                }
            }
            return this.f1928e.getOrDefault(str, this.f1929f);
        }

        /* JADX WARN: Code duplicated, block: B:11:0x006b A[DONT_INVERT] */
        /* JADX WARN: Code duplicated, block: B:12:0x006d  */
        void e(boolean z2, int i2, String str, String str2, boolean z3) throws Throwable {
            c cVarD;
            boolean z4 = e.E;
            if (z4) {
                Log.d("UnionPower.CpuDoze", "matchScene: " + z2 + "|" + i2 + "|" + str + "|" + str2 + "|" + z3);
            }
            if (this.f1927d.contains(str)) {
                if (z4) {
                    Log.i("UnionPower.CpuDoze", "not Support scene");
                }
                cVarD = null;
            } else {
                if (this.f1927d.contains(str + "#" + str2)) {
                    if (z4) {
                        Log.i("UnionPower.CpuDoze", "not Support scene");
                    }
                    cVarD = null;
                } else {
                    cVarD = d(str, str2);
                }
            }
            if (cVarD == null) {
                if (e.this.f1916t != null && e.this.f1916t.f1934d) {
                    if (e.this.f1917u != null) {
                        e.this.f1917u.stopWatching();
                        e.this.f1914r = false;
                    }
                    FileUtil.writeToFile("/sys/module/unionpower/parameters/FRAME_MONITOR_ENABLE", "0");
                    e eVar = e.this;
                    eVar.G(eVar.f1916t.f1935e, null, e.this.f1908l);
                }
                f(null, false);
                return;
            }
            if (!cVarD.equals(e.this.f1916t) && e.this.f1917u != null) {
                e.this.f1914r = false;
                if (cVarD.f1934d == (e.this.f1916t == null || !e.this.f1916t.f1934d)) {
                    if (cVarD.f1934d) {
                        FileUtil.writeToFile("/sys/module/unionpower/parameters/FRAME_MONITOR_ENABLE", "1");
                        e.this.f1917u.startWatching();
                    } else {
                        e.this.f1917u.stopWatching();
                        FileUtil.writeToFile("/sys/module/unionpower/parameters/FRAME_MONITOR_ENABLE", "0");
                    }
                }
                e eVar2 = e.this;
                eVar2.G(eVar2.f1916t != null ? e.this.f1916t.f1935e : null, cVarD.f1935e, e.this.f1908l);
            }
            long jK = l.a.k(str);
            long jUptimeMillis = SystemClock.uptimeMillis();
            if (z4) {
                StringBuilder sb = new StringBuilder();
                sb.append("check cold timeout, ");
                sb.append(jK);
                sb.append(", ");
                sb.append(this.f1926c);
                sb.append(", ");
                sb.append(jUptimeMillis);
                sb.append(", ");
                sb.append(this.f1926c + jK > jUptimeMillis);
                Log.i("UnionPower.CpuDoze", sb.toString());
            }
            if (this.f1926c + jK > jUptimeMillis) {
                if (!e.this.hasMessages(7, str)) {
                    e eVar3 = e.this;
                    eVar3.sendMessageAtTime(eVar3.obtainMessage(7, str), jK + this.f1926c);
                }
            } else if ((this.f1924a || !z2) && i2 >= this.f1925b && !z3) {
                f(cVarD, true);
                return;
            }
            f(cVarD, false);
        }

        void f(c cVar, boolean z2) throws Throwable {
            if (cVar == null || !z2) {
                if (e.this.f1916t != null && e.this.f1916t.f1936f != -1) {
                    e.this.J();
                    e eVar = e.this;
                    eVar.G(eVar.f1916t.f1932b, null, e.this.f1907k);
                }
                if (cVar != null) {
                    cVar.f1936f = -1;
                }
                e.this.f1916t = cVar;
                if (e.E) {
                    Log.d("UnionPower.CpuDoze", "onConfigChange: to be match or handle, " + e.this.f1913q + "|" + e.this.f1911o + "|" + e.this.f1915s + "|" + e.this.f1912p + "|" + e.this.f1914r + "|" + cVar);
                    return;
                }
                return;
            }
            if (!cVar.equals(e.this.f1916t)) {
                e.this.f1918v = SystemClock.uptimeMillis();
                e.this.J();
                e.this.I(cVar.f1933c);
                e eVar2 = e.this;
                eVar2.G(eVar2.f1916t != null ? e.this.f1916t.f1932b : null, cVar.f1932b, e.this.f1907k);
                e.this.f1916t = cVar;
            } else {
                if (e.this.f1916t.f1936f != -1) {
                    b();
                    return;
                }
                e.this.f1918v = SystemClock.uptimeMillis();
                e eVar3 = e.this;
                eVar3.I(eVar3.f1916t.f1933c);
                e eVar4 = e.this;
                eVar4.G(null, eVar4.f1916t.f1932b, e.this.f1907k);
            }
            e.this.f1916t.f1936f = 1;
            if (e.E) {
                Log.d("UnionPower.CpuDoze", "onConfigChange: match & handle, " + e.this.f1913q + "|" + e.this.f1911o + "|" + e.this.f1915s + "|" + e.this.f1912p + "|" + e.this.f1914r + "|" + cVar);
            }
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("PolicyConfig { multiScreen:");
            sb.append(this.f1924a);
            sb.append(",  touchStatus:");
            sb.append(this.f1925b);
            sb.append(",  startTimeout:");
            sb.append(this.f1926c);
            sb.append(",  white:");
            sb.append(this.f1927d);
            sb.append(",  common:");
            sb.append(this.f1929f);
            sb.append(",  black:");
            Map<String, c> map = this.f1928e;
            sb.append(map == null ? "null" : map.keySet());
            sb.append(" }");
            return sb.toString();
        }
    }

    /* JADX INFO: compiled from: CpuDozeHandler.java */
    private class c {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        public String f1931a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        public Map<String, String> f1932b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        public int[] f1933c;

        /* JADX INFO: renamed from: d, reason: collision with root package name */
        public boolean f1934d;

        /* JADX INFO: renamed from: e, reason: collision with root package name */
        public Map<String, String> f1935e;

        /* JADX INFO: renamed from: f, reason: collision with root package name */
        public int f1936f = -1;

        public c(String str, JSONObject jSONObject) {
            this.f1934d = false;
            this.f1931a = str;
            if (jSONObject != null) {
                d(jSONObject.optString("perf_cmd", ""));
                c(jSONObject.optString("node_cmd", ""));
                boolean zOptBoolean = jSONObject.optBoolean("monitor_frame", false);
                this.f1934d = zOptBoolean;
                if (zOptBoolean) {
                    b(jSONObject.optString("frame_conf", ""));
                }
            }
        }

        private void b(String str) {
            if (str == null || str.isEmpty()) {
                return;
            }
            this.f1935e = new HashMap();
            for (String str2 : str.split(";")) {
                String[] strArrSplit = str2.split("#");
                if (strArrSplit.length == 2) {
                    this.f1935e.put(strArrSplit[0], strArrSplit[1]);
                }
            }
        }

        private void c(String str) {
            if (str == null || str.isEmpty()) {
                return;
            }
            this.f1932b = new HashMap();
            for (String str2 : str.split(";")) {
                String[] strArrSplit = str2.split("#");
                if (strArrSplit.length == 2) {
                    this.f1932b.put(strArrSplit[0], strArrSplit[1]);
                }
            }
        }

        private void d(String str) {
            if (str == null || str.isEmpty()) {
                return;
            }
            String[] strArrSplit = str.split(",");
            this.f1933c = new int[strArrSplit.length];
            for (int i2 = 0; i2 < strArrSplit.length; i2++) {
                this.f1933c[i2] = Integer.decode(strArrSplit[i2].trim()).intValue();
            }
        }

        public boolean a(String str) {
            String str2 = this.f1931a;
            if (str2 == null || str2.equals(str)) {
                return true;
            }
            String str3 = this.f1931a;
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append("#");
            return str3.startsWith(sb.toString());
        }

        public String toString() {
            if (!e.E) {
                return "{" + this.f1931a + ", " + this.f1936f + "}";
            }
            return "{" + this.f1931a + ", " + this.f1936f + ", " + Arrays.toString(this.f1933c) + ", " + this.f1932b + "}";
        }
    }

    static {
        E = Log.isLoggable("UnionPower.CpuDoze", 3) || Build.IS_DEBUGGABLE;
        F = FeatureParser.getBoolean("support_cpu_doze", false);
        G = FeatureParser.getBoolean("is_mediatek", false);
    }

    public static synchronized h A(Context context) {
        try {
            if (!d()) {
                return null;
            }
            if (H == null && context != null) {
                j.a.f1894d = context;
                e eVar = new e();
                H = eVar;
                eVar.g("cpu_doze_group");
            }
            return H;
        } catch (Throwable th) {
            throw th;
        }
    }

    private void B(boolean z2) throws Throwable {
        if (this.f1905i == z2) {
            return;
        }
        if (z2) {
            this.f1897c.e(this, UnionPowerConfig.ObserverEventEnum.TouchEvent, 3, 1, 4, 2);
            this.f1897c.e(this, UnionPowerConfig.ObserverEventEnum.MultiWindowEvent, 2);
            this.f1897c.e(this, UnionPowerConfig.ObserverEventEnum.PowerModeEvent, 6);
            this.f1897c.e(this, UnionPowerConfig.ObserverEventEnum.AMProcessEvent, 5);
        } else {
            a aVar = this.f1917u;
            if (aVar != null) {
                aVar.stopWatching();
                FileUtil.writeToFile("/sys/module/unionpower/parameters/FRAME_MONITOR_ENABLE", "0");
            }
            this.f1897c.f(this, UnionPowerConfig.ObserverEventEnum.TouchEvent);
            this.f1897c.f(this, UnionPowerConfig.ObserverEventEnum.MultiWindowEvent);
            this.f1897c.f(this, UnionPowerConfig.ObserverEventEnum.PowerModeEvent);
            this.f1897c.f(this, UnionPowerConfig.ObserverEventEnum.AMProcessEvent);
        }
        this.f1905i = z2;
        Log.w("UnionPower.CpuDoze", "CpuDozeHandler funcSwitch=" + z2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean C(Map<String, String> map) {
        if (map == null) {
            return false;
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String fileString = FileUtil.getFileString(entry.getKey());
            String value = entry.getValue();
            if (value != null && fileString != null && !value.equals(fileString)) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void D(Set set, Map.Entry entry) {
        set.add((String) entry.getKey());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean E(Set set, Map.Entry entry) {
        return !set.contains(entry.getKey());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void F(Set set, Map.Entry entry) {
        set.add((String) entry.getKey());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void G(Map<String, String> map, Map<String, String> map2, Map<String, String> map3) throws Throwable {
        final HashSet<String> hashSet = new HashSet();
        if (map2 == null || map2.isEmpty()) {
            map3.entrySet().forEach(new Consumer() { // from class: j.b
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    e.D(hashSet, (Map.Entry) obj);
                }
            });
        } else if (map != null && !map.isEmpty()) {
            final Set<String> setKeySet = map2.keySet();
            map3.entrySet().stream().filter(new Predicate() { // from class: j.c
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return e.E(setKeySet, (Map.Entry) obj);
                }
            }).forEach(new Consumer() { // from class: j.d
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    e.F(hashSet, (Map.Entry) obj);
                }
            });
        }
        for (String str : hashSet) {
            FileUtil.writeToFile(str, map3.get(str));
            map3.remove(str);
        }
        hashSet.clear();
        if (map2 == null || map2.isEmpty()) {
            return;
        }
        for (Map.Entry<String, String> entry : map2.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (entry.getKey() != null && !entry.getKey().isEmpty()) {
                if (!map3.containsKey(entry.getKey())) {
                    map3.put(key, FileUtil.getFileString(key));
                }
                FileUtil.writeToFile(key, value);
            }
        }
    }

    private void H() throws Throwable {
        int i2;
        if (this.f1905i && (i2 = this.f1913q) != -1 && i2 < this.f1906j.size()) {
            b bVar = this.f1906j.get(this.f1913q);
            if (bVar != null) {
                long j2 = bVar.f1926c;
                if (j2 != this.f1915s) {
                    this.f1915s = j2;
                    removeMessages(7);
                }
                bVar.e(this.f1911o, this.f1912p, this.f1909m, this.f1910n, this.f1914r);
                return;
            }
            return;
        }
        if (E) {
            Log.i("UnionPower.CpuDoze", "status fail: " + this.f1905i + " " + this.f1913q + " " + this.f1906j.size());
        }
        c cVar = this.f1916t;
        if (cVar != null && cVar.f1936f != -1) {
            J();
            G(this.f1916t.f1932b, null, this.f1907k);
        }
        this.f1916t = null;
    }

    private synchronized void K() {
        try {
            try {
                Class<?> cls = Class.forName("android.perf.MTKBoostFramework");
                this.f1919w = cls;
                Class[] clsArr = new Class[0];
                Constructor<?> constructor = cls.getConstructor(null);
                if (constructor != null) {
                    this.A = constructor.newInstance(null);
                }
                Class<?> cls2 = Integer.TYPE;
                this.f1922z = this.f1919w.getMethod("perfLockAcquire", cls2, int[].class);
                this.f1921y = this.f1919w.getMethod("perfLockReleaseHandler", cls2);
                if (E) {
                    Slog.i("UnionPower.CpuDoze", "preload MTKBoostFramework succeed.");
                }
                this.B = true;
            } catch (Exception unused) {
                Log.e("UnionPower.CpuDoze", "preload class android.perf.MTKBoostFramework failed");
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    private synchronized void L() {
        try {
            try {
                Class<?> cls = Class.forName("android.util.BoostFramework");
                this.f1920x = cls;
                Class[] clsArr = new Class[0];
                Constructor<?> constructor = cls.getConstructor(null);
                if (constructor != null) {
                    this.A = constructor.newInstance(null);
                }
                Class<?> cls2 = Integer.TYPE;
                this.f1922z = this.f1920x.getMethod("perfLockAcquire", cls2, int[].class);
                this.f1921y = this.f1920x.getMethod("perfLockReleaseHandler", cls2);
                if (E) {
                    Slog.i("UnionPower.CpuDoze", "preload QcomBoostFramework succeed.");
                }
                this.B = true;
            } catch (Exception e2) {
                Log.e("UnionPower.CpuDoze", e2.getMessage());
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    public static boolean d() {
        return F;
    }

    public synchronized void I(int[] iArr) {
        if (iArr != null) {
            if (iArr.length != 0) {
                if (!this.B) {
                    if (E) {
                        Log.w("UnionPower.CpuDoze", "perfLockAcquire !mIsPreloadDone return");
                    }
                    return;
                }
                if (!this.C) {
                    if (E) {
                        Log.w("UnionPower.CpuDoze", "perfLockAcquire !mIsReleased return");
                    }
                    return;
                }
                try {
                    Method method = this.f1922z;
                    if (method != null) {
                        this.D = ((Integer) method.invoke(this.A, Integer.valueOf(G ? -1 : 0), iArr)).intValue();
                        this.C = false;
                    }
                } catch (Exception e2) {
                    Log.e("UnionPower.CpuDoze", "perfLockAcquire e: " + e2.getMessage());
                }
            }
        }
    }

    public synchronized void J() {
        if (!this.B) {
            if (E) {
                Log.w("UnionPower.CpuDoze", "perfLockRelease !mIsPreloadDone return");
            }
            return;
        }
        if (this.C) {
            if (E) {
                Log.w("UnionPower.CpuDoze", "perfLockRelease mIsReleased return");
            }
            return;
        }
        try {
            Method method = this.f1921y;
            if (method != null) {
                method.invoke(this.A, Integer.valueOf(this.D));
                this.C = true;
            }
        } catch (Exception e2) {
            Log.e("UnionPower.CpuDoze", "perfLockRelease e: " + e2.getMessage());
        }
    }

    @Override // j.h
    public String a() {
        return "UnionPower.CpuDoze";
    }

    @Override // j.h
    public Integer b() {
        return 1;
    }

    @Override // j.h
    public UnionPowerConfig.CloudModuleEnum c() {
        return UnionPowerConfig.CloudModuleEnum.SPECIAL;
    }

    @Override // j.h
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (UnionPowerConfig.f656a) {
            StringBuilder sb = new StringBuilder();
            Iterator<b> it = this.f1906j.iterator();
            while (it.hasNext()) {
                sb.append(it.next());
                sb.append(ShellUtils.COMMAND_LINE_END);
            }
            printWriter.println("CpuDoze SceneList:[\n" + ((Object) sb) + "]");
        }
        printWriter.println(" status: " + this.f1913q + "|" + this.f1911o + "|" + this.f1915s + "|" + this.f1912p + "|" + this.f1914r);
        if (this.f1916t != null) {
            printWriter.println("  " + this.f1916t.f1931a + "|" + this.f1916t.f1936f);
        }
    }

    @Override // j.a
    protected void e() {
        int iK = l.g.k();
        this.f1913q = (iK < 2 || iK > 4) ? -1 : iK - 2;
        b bVar = new b(null, "");
        this.f1906j.clear();
        for (int i2 = 0; i2 < 3; i2++) {
            this.f1906j.add(bVar);
        }
        if (G) {
            K();
            Slog.w("UnionPower.CpuDoze", "preloadMtkBoost invoke done");
        } else {
            L();
            Slog.w("UnionPower.CpuDoze", "preloadQcomBoost invoke done");
        }
        try {
            if (new File("/sys/unionpower/frame_jank").exists()) {
                this.f1917u = new a("/sys/unionpower/frame_jank");
            }
        } catch (Exception e2) {
            Log.e("UnionPower.CpuDoze", "failure observer FRAME_JANK: " + e2.getMessage());
        }
    }

    @Override // j.a
    protected void f() throws Throwable {
        if (E) {
            Log.i("UnionPower.CpuDoze", "enter updateConfig");
        }
        String string = SimpleSettings.Misc.getString(j.a.f1894d, "cpu_doze_group", "");
        b bVar = new b(null, "");
        if (string.isEmpty()) {
            for (int i2 = 0; i2 < 3; i2++) {
                this.f1906j.set(i2, bVar);
            }
            B(false);
            return;
        }
        try {
            JSONObject jSONObject = new JSONObject(string);
            B(jSONObject.optBoolean("funcSwitch", false));
            b bVar2 = new b(null, jSONObject.optString("level_0", ""));
            this.f1906j.set(0, bVar2);
            int i3 = 1;
            if (this.f1904h) {
                b bVar3 = new b(null, "");
                while (i3 < 3) {
                    this.f1906j.set(i3, bVar3);
                    i3++;
                }
                return;
            }
            while (i3 < 3) {
                b bVar4 = new b(bVar2, jSONObject.optString("level_" + i3, ""));
                this.f1906j.set(i3, bVar4);
                i3++;
                bVar2 = bVar4;
            }
        } catch (Exception e2) {
            Log.e("UnionPower.CpuDoze", "update config failed" + e2);
            b bVar5 = new b(null, "");
            for (int i4 = 0; i4 < 3; i4++) {
                this.f1906j.set(i4, bVar5);
            }
        }
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) throws Throwable {
        switch (message.what) {
            case 1:
                if (E) {
                    Log.i("UnionPower.CpuDoze", "Start to update");
                }
                f();
                break;
            case 2:
                boolean zBooleanValue = ((Boolean) message.obj).booleanValue();
                if (zBooleanValue == this.f1911o) {
                    return;
                } else {
                    this.f1911o = zBooleanValue;
                }
                break;
            case 3:
                int i2 = message.arg1;
                if (i2 == this.f1912p) {
                    return;
                } else {
                    this.f1912p = i2;
                }
                break;
            case 4:
                Bundle bundle = (Bundle) message.obj;
                if (bundle == null) {
                    return;
                }
                this.f1909m = bundle.getString("pkg", "");
                this.f1910n = bundle.getString("className", "");
                break;
            case 5:
            case 7:
                String str = (String) message.obj;
                c cVar = this.f1916t;
                if (cVar == null || !cVar.a(str)) {
                    return;
                }
                break;
            case 6:
                int i3 = message.arg1;
                if (i3 >= 2 && i3 <= 4) {
                    this.f1913q = i3 - 2;
                } else if (this.f1913q == -1) {
                    return;
                } else {
                    this.f1913q = -1;
                }
                break;
            case 9:
                boolean zBooleanValue2 = ((Boolean) message.obj).booleanValue();
                if (this.f1914r == zBooleanValue2) {
                    return;
                } else {
                    this.f1914r = zBooleanValue2;
                }
                break;
        }
        H();
    }
}
