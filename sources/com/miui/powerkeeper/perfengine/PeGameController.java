package com.miui.powerkeeper.perfengine;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.os.BackgroundThread;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.statemachine.DisplayFrameSetting;
import com.miui.powerkeeper.utils.EncryptUtils;
import com.miui.powerkeeper.utils.PackageUtil;
import com.miui.powerkeeper.utils.PowerLog;
import com.miui.powerkeeper.utils.Utils;
import com.xiaomi.channelcollection.RegisterAppChannelInfoCollectionJob;
import com.xiaomi.onetrack.util.z;
import com.xiaomi.push.mpcd.Constants;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import miui.process.ForegroundInfo;
import miui.process.IActivityChangeListener;
import miui.process.ProcessManager;
import miui.util.FeatureParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class PeGameController extends SchedController {
    public static String W = "";
    public static String X = "";
    public static String Y = "";
    public static String Z = "";

    /* JADX INFO: renamed from: a0, reason: collision with root package name */
    public static String f414a0 = "";

    /* JADX INFO: renamed from: b0, reason: collision with root package name */
    public static String f415b0 = "";

    /* JADX INFO: renamed from: c0, reason: collision with root package name */
    public static String f416c0 = "";

    /* JADX INFO: renamed from: d0, reason: collision with root package name */
    public static String f417d0 = "";

    /* JADX INFO: renamed from: e0, reason: collision with root package name */
    public static String f418e0 = "";

    /* JADX INFO: renamed from: f0, reason: collision with root package name */
    public static String f419f0 = "";

    /* JADX INFO: renamed from: g0, reason: collision with root package name */
    public static String f420g0 = "";

    /* JADX INFO: renamed from: h0, reason: collision with root package name */
    private static final Pattern f421h0 = Pattern.compile("\\#");
    private static final Pattern i0 = Pattern.compile("(\\/*\\w+\\/)+\\w+");
    public static volatile String j0 = "";
    public static volatile String k0 = "";
    public static int l0 = 60;
    public static boolean m0 = false;
    public static volatile boolean n0 = false;
    private static String o0 = "com_android_module_app_ui_test_activity_ActivityScoreBench#com_epicgames_ue4_GameActivity#";
    private static String p0 = "com_epicgames_ue4_GameActivity#com_epicgames_ue4_GameActivity_Seasons_OffScreen#";
    private static String q0 = "com_epicgames_ue4_GameActivity_Seasons_OffScreen#com_epicgames_ue4_GameActivity_Coastline2_OnScreen#";
    private static long r0 = SystemProperties.getLong("persist.miui.seasonsOnScreenTime", 92000);
    private static long s0 = SystemProperties.getLong("persist.miui.seasonsOffScreenTime", 100000);
    private static long t0 = SystemProperties.getLong("persist.miui.coastline2OnScreenTime", 43000);
    private e D;
    private a H;
    private b O;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private Context f422a;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private volatile boolean f427f;

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    private c f433l;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private String[] f423b = null;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private ConcurrentHashMap<String, f> f424c = new ConcurrentHashMap<>();

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private volatile boolean f425d = false;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private volatile boolean f426e = false;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private volatile boolean f428g = false;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private volatile int f429h = 0;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private volatile String f430i = "";

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private volatile String f431j = "";

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private volatile String f432k = "";

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    private String f434m = "mishow_installed";

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    private String f435n = "";

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    private HandlerThread f436o = null;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    private PeSchedHandler f437p = null;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    private HandlerThread f438q = null;

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    private HashMap<String, String> f439r = new HashMap<>();

    /* JADX INFO: renamed from: s, reason: collision with root package name */
    private ArrayList<String> f440s = new ArrayList<>();

    /* JADX INFO: renamed from: t, reason: collision with root package name */
    private ArrayList<String> f441t = new ArrayList<>();

    /* JADX INFO: renamed from: u, reason: collision with root package name */
    private boolean f442u = false;

    /* JADX INFO: renamed from: v, reason: collision with root package name */
    private boolean f443v = false;

    /* JADX INFO: renamed from: w, reason: collision with root package name */
    private boolean f444w = false;

    /* JADX INFO: renamed from: x, reason: collision with root package name */
    private boolean f445x = false;

    /* JADX INFO: renamed from: y, reason: collision with root package name */
    private boolean f446y = false;

    /* JADX INFO: renamed from: z, reason: collision with root package name */
    private boolean f447z = false;
    private String A = "";
    private String B = "";
    private String C = "";
    private boolean E = false;
    private d F = new d();
    private boolean G = false;
    private String I = "";
    private List<String> J = new ArrayList();
    private List<String> K = new ArrayList();
    private List<String> L = new ArrayList();
    private List<ApplicationInfo> M = new ArrayList();
    private List<String> N = new ArrayList();
    private String P = "";
    private String Q = "";
    private String R = "";
    private String S = "";
    private boolean T = false;
    private int U = 0;
    private IActivityChangeListener.Stub V = new IActivityChangeListener.Stub() { // from class: com.miui.powerkeeper.perfengine.PeGameController.1
        public void onActivityChanged(ComponentName componentName, ComponentName componentName2) {
            String packageName = componentName2.getPackageName();
            String className = componentName2.getClassName();
            PeGameController.this.f437p.d(packageName, className);
            if ("com.miui.wakepath.ui.ConfirmStartActivity".equals(className) && "com.miui.securitycenter".equals(packageName)) {
                PeGameController.this.T = true;
            } else {
                PeGameController.this.T = false;
            }
            if (PowerLog.V) {
                PowerLog.v("PeGameController", "onActivityChanged, curPackageName:" + packageName + ", activity:" + className + ", mIsConfirmStartActivity:" + PeGameController.this.T);
            }
        }
    };

    class a extends Handler {
        public a(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (PowerLog.V) {
                PowerLog.v("PeGameController", "BgHandler, handleMessage msg.what:" + message.what);
            }
            int i2 = message.what;
            if (i2 == 123) {
                PeGameController.this.J();
                PerfEngineManager perfEngineManagerD = PerfEngineManager.d();
                if (perfEngineManagerD != null && perfEngineManagerD.f() != null) {
                    perfEngineManagerD.f().h();
                }
                EventsAggregator eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
                if (eventsAggregator != null) {
                    PeGameController.this.d0(eventsAggregator.getPowerMode());
                    return;
                }
                return;
            }
            if (i2 != 456) {
                return;
            }
            PeGameController.this.K.add((String) message.obj);
            for (String str : PeGameController.this.L) {
                for (String str2 : PeGameController.this.K) {
                    if (str2.length() > 53) {
                        break;
                    }
                    if (EncryptUtils.encryptByPrivateKey(str2, PeGameController.f420g0).equals(str) && !PeGameController.this.N.contains(str2)) {
                        PeGameController.this.N.add(str2);
                        break;
                    }
                }
            }
            PeGameController peGameController = PeGameController.this;
            peGameController.V(peGameController.N);
        }
    }

    class b extends BroadcastReceiver {
        b() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
                PeGameController.this.H.sendMessage(PeGameController.this.H.obtainMessage(456, intent.getData().getSchemeSpecificPart()));
            }
        }
    }

    class c extends ContentObserver {
        public c() {
            super(new Handler(BackgroundThread.getHandler().getLooper()));
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z2) {
            super.onChange(z2);
            int i2 = Settings.System.getInt(PeGameController.this.f422a.getContentResolver(), PeGameController.this.f434m, -1);
            if (i2 == 1) {
                PeGameController.this.f427f = true;
            } else {
                PeGameController.this.f427f = false;
            }
            if (PowerLog.V) {
                PowerLog.v("PeGameController", "onChange MSProperty: " + i2);
            }
        }
    }

    public PeGameController(Context context) {
        this.f422a = null;
        this.f427f = false;
        this.D = null;
        this.H = null;
        this.f422a = context;
        W(context);
        X();
        this.D = new e(false);
        a aVar = new a(BackgroundThread.getHandler().getLooper());
        this.H = aVar;
        aVar.sendMessageDelayed(aVar.obtainMessage(123), 3000L);
        PeCloudManager.e(this.f422a).g(this);
        PerfEngineController.b(this.f422a).c(this);
        this.f433l = new c();
        this.f422a.getContentResolver().registerContentObserver(Settings.System.getUriFor(this.f434m), true, this.f433l);
        this.f427f = A();
        this.f439r.put("com.tencent.mm", "com.tencent.mm:push");
        this.f439r.put("com.sankuai.meituan.dispatch.homebrew", "com.sankuai.meituan.dispatch.homebrew:dppushservice");
        this.f439r.put("com.dwd.rider", "com.dwd.rider:pushservice");
        if (PowerKeeperManager.JNI_LOADED) {
            W = nativeGetAt("A");
            X = nativeGetAt(com.xiaomi.onetrack.api.e.f1070a);
            Y = nativeGetAt("C");
            Z = nativeGetAt("GN");
            f414a0 = nativeGetAt("GC");
            f415b0 = nativeGetAt("AndBen");
            f416c0 = nativeGetAt("GFBenA");
            f417d0 = nativeGetAt("GFBenB");
            f418e0 = nativeGetAt("GFBenC");
            f419f0 = nativeGetAt("mGFBenPrim");
            f420g0 = nativeGetPrivateKey("E");
        }
        this.f440s.add("com.tencent.mm");
        this.f440s.add(W);
        this.f440s.add(X);
        this.f440s.add(Y);
        this.f440s.add(Z);
        this.f440s.add(f414a0);
        this.f441t.add("com.miui.home");
        this.f441t.add("com.android.settings");
        this.f441t.add("key_guard");
    }

    private boolean A() {
        int i2 = Settings.System.getInt(this.f422a.getContentResolver(), this.f434m, -1);
        if (PowerLog.V) {
            PowerLog.v("PeGameController", "getMSProperty: " + i2);
        }
        return Settings.System.getInt(this.f422a.getContentResolver(), this.f434m, 0) == 1;
    }

    private void D() {
        try {
            if (TextUtils.isEmpty(this.I)) {
                return;
            }
            String strNativeGetDefaultConfig = PowerKeeperManager.JNI_LOADED ? nativeGetDefaultConfig(this.I) : "";
            if (TextUtils.isEmpty(strNativeGetDefaultConfig)) {
                Log.e("PeGameController", "initDefaultConfig data is empty, return");
                return;
            }
            JSONObject jSONObject = new JSONObject(strNativeGetDefaultConfig);
            if (PowerLog.V) {
                PowerLog.v("PeGameController", "initDefaultConfig json:" + jSONObject);
            }
            JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject("perf_engine");
            if (jSONObjectOptJSONObject == null) {
                Log.e("PeGameController", "initDefaultConfig ull == schedJson return");
            } else {
                T(jSONObjectOptJSONObject);
            }
        } catch (Exception e2) {
            Log.e("PeGameController", "initDefaultConfig IOException e:" + e2.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void J() {
        String strC = c();
        if (PowerLog.V) {
            PowerLog.v("PeGameController", "loadDefaultConfig deviceName:" + strC);
        }
        if (PowerKeeperManager.JNI_LOADED) {
            this.I = nativeParseDevice(strC);
        }
        if (PowerLog.V) {
            PowerLog.v("PeGameController", "nativeParseDevice:" + this.I);
        }
        if (TextUtils.isEmpty(this.I)) {
            Log.i("PeGameController", "device not support default config");
        } else {
            try {
                D();
                Log.i("PeGameController", "parse default " + this.I + " config sussess");
            } catch (Exception e2) {
                Log.w("PeGameController", "parse default " + this.I + " config unsussessful :" + e2.toString());
            }
        }
        this.f442u = FeatureParser.getBoolean("is_mediatek", false);
        this.f443v = FeatureParser.getBoolean("is_xring", false);
    }

    private boolean O(JSONObject jSONObject, ConcurrentHashMap<String, f> concurrentHashMap) {
        try {
            JSONArray jSONArray = jSONObject.getJSONArray("sched_apps");
            JSONArray jSONArray2 = jSONObject.getJSONArray("sched_cmds_relations");
            JSONArray jSONArrayOptJSONArray = jSONObject.optJSONArray("more_sched_cmds_relations");
            concurrentHashMap.clear();
            if (jSONArray != null && jSONArray.length() != 0 && jSONArray2 != null && jSONArray2.length() != 0 && jSONArray.length() == jSONArray2.length()) {
                ArrayList arrayList = new ArrayList();
                for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                    String string = jSONArray.getString(i2);
                    ArrayList arrayList2 = new ArrayList();
                    for (String str : jSONArray2.getString(i2).split("#")) {
                        arrayList2.add(str);
                    }
                    ArrayList arrayList3 = new ArrayList();
                    if (jSONArrayOptJSONArray != null) {
                        arrayList3.add(jSONArrayOptJSONArray.getString(i2));
                    }
                    arrayList.add(string);
                    this.L.add(string);
                    concurrentHashMap.put(string, new f(arrayList2, arrayList3));
                }
                Collections.sort(this.L);
                if (!this.L.equals(this.J)) {
                    this.M = PackageUtil.getAllApps(this.f422a);
                    this.J = new ArrayList(this.L);
                    this.N.clear();
                    for (String str2 : this.J) {
                        for (ApplicationInfo applicationInfo : this.M) {
                            if ((applicationInfo.flags & 1) == 0) {
                                String str3 = applicationInfo.packageName;
                                if (str3.length() > 53) {
                                    break;
                                }
                                if (EncryptUtils.encryptByPrivateKey(str3, f420g0).equals(str2) && !this.N.contains(str3)) {
                                    this.N.add(str3);
                                    break;
                                }
                            }
                        }
                    }
                    V(this.N);
                }
                return true;
            }
            Log.i("PeGameController", "CPU sched app is empty.");
            return false;
        } catch (JSONException e2) {
            Log.e("PeGameController", "apps exception : " + e2.toString());
            return false;
        }
    }

    private JSONObject P(JSONObject jSONObject) {
        try {
            JSONObject jSONObject2 = jSONObject.getJSONObject("cmds_collections");
            if (jSONObject2 != null && jSONObject2.length() != 0) {
                return jSONObject2;
            }
            Log.i("PeGameController", "No commands has not been configed.");
            return null;
        } catch (JSONException e2) {
            Log.e("PeGameController", "jObjCollections exception : " + e2.toString());
            return null;
        }
    }

    private void Q(JSONObject jSONObject, ConcurrentHashMap<String, f> concurrentHashMap) {
        if (concurrentHashMap.isEmpty()) {
            return;
        }
        Iterator<String> it = concurrentHashMap.keySet().iterator();
        while (it.hasNext()) {
            try {
                try {
                    String next = it.next();
                    ArrayList<String> arrayList = concurrentHashMap.get(next).f528a;
                    ArrayList<String> arrayList2 = new ArrayList<>();
                    Iterator<String> it2 = arrayList.iterator();
                    while (true) {
                        if (!it2.hasNext()) {
                            break;
                        }
                        try {
                            JSONArray jSONArray = jSONObject.getJSONArray("sched_cmds" + it2.next());
                            jSONObject.length();
                            String[] strArr = new String[jSONArray.length()];
                            for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                                strArr[i2] = jSONArray.getString(i2);
                            }
                            a0(strArr, arrayList2);
                        } catch (JSONException e2) {
                            Log.e("PeGameController", "jAryCmds exception : " + e2.toString());
                            it.remove();
                        }
                    }
                    concurrentHashMap.get(next).f528a = arrayList2;
                    ArrayList<String> arrayList3 = concurrentHashMap.get(next).f529b;
                    ArrayList<String> arrayList4 = new ArrayList<>();
                    Iterator<String> it3 = arrayList3.iterator();
                    while (it3.hasNext()) {
                        try {
                            JSONArray jSONArray2 = jSONObject.getJSONArray("sched_cmds" + it3.next());
                            jSONObject.length();
                            String[] strArr2 = new String[jSONArray2.length()];
                            for (int i3 = 0; i3 < jSONArray2.length(); i3++) {
                                strArr2[i3] = jSONArray2.getString(i3);
                            }
                            a0(strArr2, arrayList4);
                        } catch (JSONException e3) {
                            Log.e("PeGameController", "jAryCmds exception : " + e3.toString());
                            it.remove();
                        }
                    }
                    concurrentHashMap.get(next).f529b = arrayList4;
                } catch (Exception e4) {
                    Log.e("PeGameController", "parseCommands Exception : " + e4.toString());
                    return;
                }
            } catch (JSONException e5) {
                Log.e("PeGameController", "parseCommands JSONException : " + e5.toString());
                return;
            }
        }
    }

    private ArrayList<String> R(ArrayList<String> arrayList) {
        ArrayList<String> arrayList2 = new ArrayList<>();
        Iterator<String> it = arrayList.iterator();
        while (it.hasNext()) {
            String[] strArrSplit = it.next().split("#");
            if (strArrSplit != null && strArrSplit.length == 3) {
                arrayList2.add(strArrSplit[0] + "#" + strArrSplit[2]);
            }
        }
        return arrayList2;
    }

    private ArrayList<String> S(ArrayList<String> arrayList) {
        ArrayList<String> arrayList2 = new ArrayList<>();
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            String[] strArrSplit = arrayList.get(size).split("#");
            if (strArrSplit != null && strArrSplit.length == 3) {
                arrayList2.add(strArrSplit[0] + "#" + strArrSplit[1]);
            }
        }
        return arrayList2;
    }

    private boolean T(JSONObject jSONObject) {
        JSONObject jSONObjectP;
        try {
            if (PowerLog.V) {
                PowerLog.v("PeGameController", "parseUpdateConfig");
            }
            this.f425d = Boolean.parseBoolean(jSONObject.getString("fucSwitch"));
            if (this.f425d) {
                v();
            }
            this.f426e = Boolean.parseBoolean(jSONObject.getString("more_func_switch"));
            this.f429h = jSONObject.optInt("version", 1);
            this.f430i = jSONObject.optString("name", "NAN");
            this.f431j = jSONObject.optString("perf_lock", "");
            j0 = jSONObject.optString("mtk_new_perf_lock", "");
            this.f432k = jSONObject.optString("new_perf_lock", "");
            n0 = jSONObject.optBoolean("sFpsWC", false);
            k0 = jSONObject.optString("xr_common_perflock", "");
            if (TextUtils.isEmpty(this.f431j) && TextUtils.isEmpty(this.f432k)) {
                this.f447z = false;
            } else {
                this.f447z = true;
            }
            this.f444w = Boolean.parseBoolean(jSONObject.optString("mtk_perflock", "false"));
            this.D.c(jSONObject.optJSONObject("new_advance_sched"));
            b0();
            if (!this.f425d) {
                t();
                return true;
            }
        } catch (Exception e2) {
            Log.e("PeGameController", "invalid update2: " + e2.toString());
            e2.printStackTrace();
        }
        if (PowerLog.V) {
            PowerLog.v("PeGameController", "perfengine mIsEnable:" + this.f425d + " MoreFunc: " + this.f426e);
        }
        JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject("param");
        if (jSONObjectOptJSONObject == null || (jSONObjectP = P(jSONObjectOptJSONObject)) == null || !O(jSONObjectOptJSONObject, this.f424c)) {
            return true;
        }
        Q(jSONObjectP, this.f424c);
        return true;
    }

    private void U(ArrayList<String> arrayList) {
        PeSchedHandler peSchedHandler = this.f437p;
        if (peSchedHandler != null) {
            peSchedHandler.f(R(arrayList));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void V(List<String> list) {
        ProcessManager.unregisterActivityChanageListener(this.V);
        ProcessManager.registerActivityChangeListener(list, (List) null, this.V);
    }

    private void W(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addDataScheme("package");
        b bVar = new b();
        this.O = bVar;
        context.registerReceiver(bVar, intentFilter, 4);
    }

    private void X() {
        for (String str : this.f424c.keySet()) {
            if (str != null && !str.isEmpty()) {
                ArrayList<String> arrayList = this.f424c.get(str).f528a;
                arrayList.addAll(this.f424c.get(str).f529b);
                Z(arrayList);
            }
        }
    }

    private void Y() {
        int i2 = DisplayFrameSetting.getDFSInstance().getmUserFps();
        if (PowerLog.V) {
            PowerLog.v("PeGameController", "restoreBoost mATNode:" + ((Object) this.F.b()) + ", fps:" + i2);
        }
        ArrayList<String> arrayList = this.F.f512j;
        if (DisplayFrameSetting.getDFSInstance().getCurrentFps() != i2) {
            l0 = i2;
            DisplayFrameSetting.setFpsAync(W, i2);
        }
        Z(arrayList);
        if (this.f442u) {
            MtkBoost.b(this.f422a, false);
        } else if (!this.f443v) {
            g.c().f();
        } else if (this.U > 0) {
            i.a().c(this.U);
            this.U = 0;
        }
        this.F.d();
    }

    private void Z(ArrayList<String> arrayList) {
        PeSchedHandler peSchedHandler = this.f437p;
        if (peSchedHandler != null) {
            peSchedHandler.g(S(arrayList));
        }
    }

    private void a0(String[] strArr, ArrayList<String> arrayList) {
        for (String str : strArr) {
            if (str == null || str.equals("")) {
                Log.w("PeGameController", "Perf command is null.");
                return;
            }
            String[] strArrSplit = f421h0.split(str);
            if (strArrSplit == null) {
                Log.w("PeGameController", "Perf command format is error.");
                return;
            }
            if (!i0.matcher(strArrSplit[0]).matches()) {
                arrayList.add(str + "#");
            } else {
                if (strArrSplit.length != 3) {
                    Log.w("PeGameController", "Perf command arguments count is error.");
                    return;
                }
                arrayList.add(str);
            }
        }
    }

    private void b0() {
        String versionName = Utils.getVersionName(this.f422a, W);
        if (PowerLog.V) {
            PowerLog.v("PeGameController", "updateAllBenVer begin atVer:" + versionName);
        }
        this.A = "default";
        if (!TextUtils.isEmpty(versionName) && versionName.contains(Constants.DOT_SEPARATOR)) {
            this.A = versionName.replaceAll(z.f1744a, RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT);
        }
        if (PowerLog.V) {
            PowerLog.v("PeGameController", "updateAllBenVer end mAtVer:" + this.A);
        }
        String versionName2 = Utils.getVersionName(this.f422a, f415b0);
        if (PowerLog.V) {
            PowerLog.v("PeGameController", "updateAllBenVer begin2222444 andBenVer:" + versionName2);
        }
        this.B = "default";
        if (!TextUtils.isEmpty(versionName2) && versionName2.contains(Constants.DOT_SEPARATOR)) {
            this.B = versionName2.replaceAll(z.f1744a, RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT);
        }
        if (PowerLog.V) {
            PowerLog.v("PeGameController", "updateAllBenVer end mAndBenVer:" + this.B);
        }
        c0();
    }

    private void c0() {
        String versionName = Utils.getVersionName(this.f422a, f419f0);
        if (PowerLog.V) {
            PowerLog.v("PeGameController", "updateAllGFBenVer gfBenPrimVer:" + versionName);
        }
        this.C = "default";
        if (TextUtils.isEmpty(versionName)) {
            String versionName2 = Utils.getVersionName(this.f422a, f416c0);
            String versionName3 = Utils.getVersionName(this.f422a, f417d0);
            String versionName4 = Utils.getVersionName(this.f422a, f418e0);
            if (PowerLog.V) {
                PowerLog.v("PeGameController", "updateAllGFBenVer begin gfBenAVer:" + versionName2);
            }
            if (PowerLog.V) {
                PowerLog.v("PeGameController", "updateAllGFBenVer begin gfBenBVer:" + versionName3);
            }
            if (PowerLog.V) {
                PowerLog.v("PeGameController", "updateAllGFBenVer begin gfBenCVer:" + versionName4);
            }
            if (!TextUtils.isEmpty(versionName2) && versionName2.contains(Constants.DOT_SEPARATOR)) {
                this.C = versionName2.replaceAll(z.f1744a, RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT);
            } else if (!TextUtils.isEmpty(versionName3) && versionName3.contains(Constants.DOT_SEPARATOR)) {
                this.C = versionName3.replaceAll(z.f1744a, RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT);
            } else if (!TextUtils.isEmpty(versionName4) && versionName4.contains(Constants.DOT_SEPARATOR)) {
                this.C = versionName4.replaceAll(z.f1744a, RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT);
            }
        } else if (versionName.contains(Constants.DOT_SEPARATOR)) {
            this.C = versionName.replaceAll(z.f1744a, RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT);
        }
        if (PowerLog.V) {
            PowerLog.v("PeGameController", "updateAllGFBenVer end ver:" + this.C);
        }
    }

    private static native String nativeGetAt(String str);

    private static native String nativeGetDefaultConfig(String str);

    private static native String nativeGetPrivateKey(String str);

    private static native String nativeParseDevice(String str);

    /* JADX WARN: Code duplicated, block: B:10:0x001c  */
    private void p() {
        String str;
        if (this.f446y) {
            if (TextUtils.isEmpty(this.f431j)) {
                str = "";
            } else {
                str = this.f431j;
            }
        } else if (TextUtils.isEmpty(this.f432k)) {
            str = "";
        } else {
            str = this.f432k;
        }
        if (!str.startsWith("0x")) {
            if (PowerLog.V) {
                PowerLog.v("PeGameController", "callPerflock error cmd:" + str + ", return");
                return;
            }
            return;
        }
        String[] strArrSplit = str.replaceAll(" ", "").replaceAll("#", "").split(",");
        int length = strArrSplit.length;
        if (length % 2 != 0) {
            if (PowerLog.V) {
                PowerLog.v("PeGameController", "callPerflock error cmd:" + str + ", return");
                return;
            }
            return;
        }
        if (PowerLog.V) {
            PowerLog.v("PeGameController", "callPerflock good cmd:" + str);
        }
        try {
            int[] iArr = new int[length];
            for (int i2 = 0; i2 < length; i2++) {
                String str2 = strArrSplit[i2];
                iArr[i2] = Integer.parseInt(str2.substring(2, str2.length()), 16);
                if (PowerLog.V) {
                    PowerLog.v("PeGameController", "callPerflock args:" + iArr[i2]);
                }
            }
            g.c().e(g.f531k, iArr);
        } catch (NumberFormatException e2) {
            Log.e("PeGameController", "callPerflock NumberFormatException ex:" + e2.getMessage());
        }
    }

    private void q(String str) {
        if (TextUtils.isEmpty(str)) {
            if (PowerLog.V) {
                PowerLog.v("PeGameController", "callQcomPerflock cmd is empty return");
                return;
            }
            return;
        }
        if (!str.startsWith("0x")) {
            if (PowerLog.V) {
                PowerLog.v("PeGameController", "callQcomPerflock error cmd:" + str + ", return");
                return;
            }
            return;
        }
        String[] strArrSplit = str.replaceAll(" ", "").replaceAll("#", "").split(",");
        int length = strArrSplit.length;
        if (length % 2 != 0) {
            if (PowerLog.V) {
                PowerLog.v("PeGameController", "callQcomPerflock error cmd:" + str + ", return");
                return;
            }
            return;
        }
        if (PowerLog.V) {
            PowerLog.v("PeGameController", "callQcomPerflock good cmd:" + str);
        }
        try {
            int[] iArr = new int[length];
            for (int i2 = 0; i2 < length; i2++) {
                String str2 = strArrSplit[i2];
                iArr[i2] = Integer.parseInt(str2.substring(2, str2.length()), 16);
                if (PowerLog.V) {
                    PowerLog.v("PeGameController", "callQcomPerflock args:" + iArr[i2]);
                }
            }
            g.c().e(g.f531k, iArr);
        } catch (NumberFormatException e2) {
            Log.e("PeGameController", "callQcomPerflock NumberFormatException ex:" + e2.getMessage());
        }
    }

    private void r(String str) {
        if (TextUtils.isEmpty(str)) {
            if (PowerLog.V) {
                PowerLog.v("PeGameController", "callXRPerflock cmd is empty return");
                return;
            }
            return;
        }
        String[] strArrSplit = str.replaceAll(" ", "").replaceAll("#", "").split(",");
        int length = strArrSplit.length;
        if (length % 2 != 0) {
            if (PowerLog.V) {
                PowerLog.v("PeGameController", "callXRPerflock error cmd:" + str + ", return");
                return;
            }
            return;
        }
        if (PowerLog.V) {
            PowerLog.v("PeGameController", "callXRPerflock good cmd:" + str);
        }
        try {
            int[] iArr = new int[length];
            for (int i2 = 0; i2 < length; i2++) {
                iArr[i2] = Integer.parseInt(strArrSplit[i2]);
                if (PowerLog.V) {
                    PowerLog.v("PeGameController", "callXRPerflock args:" + iArr[i2]);
                }
            }
            this.U = i.a().b(0, 0, iArr);
            if (PowerLog.V) {
                PowerLog.v("PeGameController", "callXRPerflock done mXRPerflockHandleID:" + this.U);
            }
        } catch (NumberFormatException e2) {
            Log.e("PeGameController", "callXRPerflock NumberFormatException ex:" + e2.getMessage());
        }
    }

    private boolean s(String str, ConcurrentHashMap<String, f> concurrentHashMap) {
        if (str == null) {
            return false;
        }
        return concurrentHashMap.containsKey(str);
    }

    private synchronized void t() {
        try {
            if (this.f436o != null) {
                if (PowerLog.V) {
                    PowerLog.v("PeGameController", "disablePerf");
                }
                EventsAggregator eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
                if (eventsAggregator != null) {
                    eventsAggregator.unregisterPowerModeListener(this.f437p);
                }
                this.f436o.quitSafely();
                this.f436o = null;
                this.f437p = null;
                IActivityChangeListener.Stub stub = this.V;
                if (stub != null) {
                    ProcessManager.unregisterActivityChanageListener(stub);
                    this.J.clear();
                }
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    private synchronized void v() {
        try {
            if (this.f436o == null) {
                if (PowerLog.V) {
                    PowerLog.v("PeGameController", "enablePerf3");
                }
                HandlerThread handlerThread = new HandlerThread("perfwork");
                this.f436o = handlerThread;
                handlerThread.start();
                this.f437p = new PeSchedHandler(this.f436o.getLooper(), this, this.f422a);
                EventsAggregator eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
                if (eventsAggregator != null) {
                    if (PowerLog.V) {
                        PowerLog.v("PeGameController", "enablePerf registerPowerModeListener success");
                    }
                    eventsAggregator.registerPowerModeListener(this.f437p);
                } else if (PowerLog.V) {
                    PowerLog.v("PeGameController", "enablePerf registerPowerModeListener fail");
                }
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    private void w() {
        if (PowerLog.V) {
            PowerLog.v("PeGameController", "enterAdvBen");
        }
        b0();
    }

    private void x() {
        if (PowerLog.V) {
            PowerLog.v("PeGameController", "exitAdvBen");
        }
        if (this.E) {
            Y();
            this.E = false;
        }
    }

    /* JADX WARN: Code duplicated, block: B:70:0x0164  */
    public void B(String str, String str2) {
        boolean z2;
        boolean z3;
        if (!this.G) {
            if (PowerLog.V) {
                PowerLog.v("PeGameController", "handleActivityAndLogcat !mIsPowermodePerformance return");
                return;
            }
            return;
        }
        if (PowerLog.V) {
            PowerLog.v("PeGameController", "handleActivityAndLogcat RitterTest888888");
        }
        if (!G()) {
            if (PowerLog.V) {
                PowerLog.v("PeGameController", "handleActivityAndLogcat !isAdvanceSchedEnabled() return");
                return;
            }
            return;
        }
        if (!TextUtils.isEmpty(str) && !this.Q.equals(str)) {
            this.P = this.Q;
            this.Q = str;
        }
        String strReplaceAll = (this.P + "#" + this.Q + "#" + str2).replaceAll(z.f1744a, RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT);
        boolean z4 = false;
        if (SystemProperties.getBoolean("persist.miui.gpu.partition.enable", false)) {
            if (o0.equals(strReplaceAll)) {
                this.f437p.e("com.antutu.benchmark.full", "com.epicgames.ue4_GameActivity.Seasons.OffScreen", r0);
            } else if (p0.equals(strReplaceAll)) {
                this.f437p.e("com.antutu.benchmark.full", "com.epicgames.ue4_GameActivity.Coastline2.OnScreen", s0);
            } else if (q0.equals(strReplaceAll)) {
                this.f437p.e("com.antutu.benchmark.full", "com.epicgames.ue4_GameActivity.Coastline2.OffScreen", t0);
            }
        }
        d dVarA = this.D.a(this.A, this.B, this.C, strReplaceAll);
        if (dVarA == null) {
            if (PowerLog.V) {
                PowerLog.v("PeGameController", "handleActivityAndLogcat get node null");
                return;
            }
            return;
        }
        boolean z5 = true;
        this.E = true;
        if (!dVarA.f506d.equals(this.F.f506d)) {
            if (PowerLog.V) {
                PowerLog.v("PeGameController", "handleActivityAndLogcat old node:" + ((Object) this.F.b()));
            }
            if (PowerLog.V) {
                PowerLog.v("PeGameController", "handleActivityAndLogcat new node:" + ((Object) dVarA.b()));
            }
            int i2 = dVarA.f507e;
            boolean z6 = (i2 == this.F.f507e && i2 == DisplayFrameSetting.getDFSInstance().getCurrentFps()) ? false : true;
            int i3 = dVarA.f508f;
            d dVar = this.F;
            if (i3 != dVar.f508f) {
                Z(dVar.f512j);
                z2 = true;
            } else {
                z2 = false;
            }
            if (this.f442u) {
                if (dVarA.f510h != this.F.f510h) {
                    MtkBoost.b(this.f422a, false);
                    z3 = false;
                    z4 = true;
                    z5 = false;
                } else {
                    z5 = false;
                    z3 = false;
                }
            } else if (this.f443v) {
                if (dVarA.f511i != this.F.f511i) {
                    if (this.U > 0) {
                        i.a().c(this.U);
                        this.U = 0;
                    }
                    z3 = false;
                } else {
                    z5 = false;
                    z3 = false;
                }
            } else if (dVarA.f509g != this.F.f509g) {
                g.c().f();
                z3 = true;
                z5 = false;
            } else {
                z5 = false;
                z3 = false;
            }
            if (z2 || z4 || z5 || z3) {
                try {
                    if (PowerLog.V) {
                        PowerLog.v("PeGameController", "handleActivityAndLogcat sleep 500ms");
                    }
                    Thread.sleep(500L);
                } catch (InterruptedException unused) {
                }
            }
            this.F.a(dVarA);
            if (z6) {
                if (PowerLog.V) {
                    PowerLog.v("PeGameController", "handleActivityAndLogcat fps ----change");
                }
                int i4 = dVarA.f507e;
                l0 = i4;
                if (DisplayFrameSetting.getDFSInstance().getCurrentFps() != i4) {
                    DisplayFrameSetting.setFpsAync(W, i4);
                } else if (PowerLog.V) {
                    PowerLog.v("PeGameController", "handleActivityAndLogcat current FPS already getCurrentFps == node.fps:" + DisplayFrameSetting.getDFSInstance().getCurrentFps());
                }
            }
            if (z2) {
                if (PowerLog.V) {
                    PowerLog.v("PeGameController", "handleActivityAndLogcat CMD ----change");
                }
                U(dVarA.f512j);
            }
            if (z3) {
                if (PowerLog.V) {
                    PowerLog.v("PeGameController", "handleActivityAndLogcat QPerflock ----change");
                }
                String str3 = dVarA.f513k;
                if (!this.f442u && !this.f443v && !TextUtils.isEmpty(str3)) {
                    q(str3);
                }
            }
            if (z4) {
                if (PowerLog.V) {
                    PowerLog.v("PeGameController", "handleActivityAndLogcat MPerflock ----change");
                }
                String str4 = dVarA.f514l;
                if (this.f442u && !TextUtils.isEmpty(str4)) {
                    MtkBoost.a(this.f422a, str4);
                }
            }
            z4 = z5;
        }
        if (z4) {
            if (PowerLog.V) {
                PowerLog.v("PeGameController", "handleActivityAndLogcat XRPerflock ----change");
            }
            String str5 = dVarA.f515m;
            if (!this.f443v || TextUtils.isEmpty(str5)) {
                return;
            }
            r(str5);
        }
    }

    public void C(String str) {
        if (str.equals(Z) || str.equals(f414a0)) {
            this.f446y = true;
        } else {
            this.f446y = false;
        }
        if (!this.f425d) {
            t();
            return;
        }
        v();
        if (TextUtils.isEmpty(str)) {
            X();
            return;
        }
        this.f428g = PerfUtils.b(this.f422a);
        if (PowerLog.V) {
            PowerLog.v("PeGameController", "handleFGChangeinBG foreground:" + str + ", isGameBoosting:" + this.f428g);
        }
        String str2 = this.S;
        this.S = str;
        boolean z2 = str2.equals(W) || str2.equals(X) || str2.equals(Y);
        boolean z3 = str.equals(W) || str.equals(X) || str.equals(Y);
        boolean zEquals = str2.equals(f415b0);
        boolean zEquals2 = str.equals(f415b0);
        boolean z4 = str2.equals(f416c0) || str2.equals(f417d0) || str2.equals(f418e0) || str2.equals(f419f0);
        boolean z5 = str.equals(f416c0) || str.equals(f417d0) || str.equals(f418e0) || str.equals(f419f0);
        if (this.G && G()) {
            if (F()) {
                if (z2 && !z3) {
                    if (this.T) {
                        if (PowerLog.V) {
                            PowerLog.v("PeGameController", "handleFGChangeinBG isAdvanceSchedEnabled still at, ConfirmStartActivity true, return");
                            return;
                        }
                        return;
                    } else {
                        if (PowerLog.V) {
                            PowerLog.v("PeGameController", "handleFGChangeinBG isAdvanceSchedEnabled exit adv, return");
                        }
                        x();
                        return;
                    }
                }
                if (!z2 && z3) {
                    if (PowerLog.V) {
                        PowerLog.v("PeGameController", "handleFGChangeinBG isAdvanceSchedEnabled enter adv, return");
                    }
                    w();
                    return;
                } else if (z2 && z3) {
                    if (PowerLog.V) {
                        PowerLog.v("PeGameController", "handleFGChangeinBG isAdvanceSchedEnabled still adv, return");
                        return;
                    }
                    return;
                } else if (PowerLog.V) {
                    PowerLog.v("PeGameController", "handleFGChangeinBG isAdvanceSchedEnabled not adv, go on");
                }
            }
            if (E()) {
                if (zEquals && !zEquals2) {
                    if (this.T) {
                        if (PowerLog.V) {
                            PowerLog.v("PeGameController", "handleFGChangeinBG isAdvanceSchedEnabled still in app, ConfirmStartActivity true, return");
                            return;
                        }
                        return;
                    } else {
                        if (PowerLog.V) {
                            PowerLog.v("PeGameController", "handleFGChangeinBG isAdvanceSchedEnabled exit adv, return");
                        }
                        x();
                        return;
                    }
                }
                if (!zEquals && zEquals2) {
                    if (PowerLog.V) {
                        PowerLog.v("PeGameController", "handleFGChangeinBG isAdvanceSchedEnabled enter adv, return");
                    }
                    w();
                    return;
                } else if (zEquals && zEquals2) {
                    if (PowerLog.V) {
                        PowerLog.v("PeGameController", "handleFGChangeinBG isAdvanceSchedEnabled still adv, return");
                        return;
                    }
                    return;
                } else if (PowerLog.V) {
                    PowerLog.v("PeGameController", "handleFGChangeinBG isAdvanceSchedEnabled not adv, go on");
                }
            }
            if (H()) {
                if (z4 && !z5) {
                    if (this.T) {
                        if (PowerLog.V) {
                            PowerLog.v("PeGameController", "handleFGChangeinBG isAdvanceSchedEnabled still in app, ConfirmStartActivity true, return");
                            return;
                        }
                        return;
                    } else {
                        if (PowerLog.V) {
                            PowerLog.v("PeGameController", "handleFGChangeinBG isAdvanceSchedEnabled exit adv, return");
                        }
                        x();
                        return;
                    }
                }
                if (!z4 && z5) {
                    if (PowerLog.V) {
                        PowerLog.v("PeGameController", "handleFGChangeinBG isAdvanceSchedEnabled enter adv, return");
                    }
                    w();
                    return;
                } else if (z4 && z5) {
                    if (PowerLog.V) {
                        PowerLog.v("PeGameController", "handleFGChangeinBG isAdvanceSchedEnabled still adv, return");
                        return;
                    }
                    return;
                } else if (PowerLog.V) {
                    PowerLog.v("PeGameController", "handleFGChangeinBG isAdvanceSchedEnabled not adv, go on");
                }
            }
        }
        String strEncryptByPrivateKey = str2.length() <= 53 ? EncryptUtils.encryptByPrivateKey(str2, f420g0) : null;
        String strEncryptByPrivateKey2 = str.length() <= 53 ? EncryptUtils.encryptByPrivateKey(str, f420g0) : null;
        if (!s(strEncryptByPrivateKey, this.f424c) && !s(strEncryptByPrivateKey2, this.f424c)) {
            if (PowerLog.V) {
                PowerLog.v("PeGameController", "handleFGChangeinBG Prev and current not in perfApps ret");
                return;
            }
            return;
        }
        if (strEncryptByPrivateKey.equals(strEncryptByPrivateKey2)) {
            if (PowerLog.V) {
                PowerLog.v("PeGameController", "handleFGChangeinBG Prev proc equals current foreground proc. ret");
                return;
            }
            return;
        }
        if (s(strEncryptByPrivateKey, this.f424c)) {
            if (PowerLog.V) {
                PowerLog.v("PeGameController", "First ------restore:" + strEncryptByPrivateKey);
            }
            if (this.f424c.get(strEncryptByPrivateKey).f530c != null) {
                Z(this.f424c.get(strEncryptByPrivateKey).f530c);
            }
            if (!this.f442u && !this.f443v) {
                if (!TextUtils.isEmpty(this.f431j) || !TextUtils.isEmpty(this.f432k)) {
                    g.c().f();
                } else if (PowerLog.V) {
                    PowerLog.v("PeGameController", "mPerflock empty, not call qcom perfLockRelease");
                }
            }
            if (this.f442u) {
                if (this.f444w || this.f445x) {
                    PowerLog.v("PeGameController", "handleFGChangeinBG mIsCloudConfMTKPerf == true");
                    MtkBoost.b(this.f422a, false);
                    this.f445x = false;
                } else if (PowerLog.V) {
                    PowerLog.v("PeGameController", "handleFGChangeinBG mIsCloudConfMTKPerf == false");
                }
            } else if (PowerLog.V) {
                PowerLog.v("PeGameController", "handleFGChangeinBG not MTK device");
            }
            if (this.f443v) {
                if (this.U <= 0 || TextUtils.isEmpty(k0)) {
                    PowerLog.v("PeGameController", "handleFGChangeinBG mXRPerflockHandleID < 0:" + this.U + ", mXRCommonPerflock=" + k0);
                } else {
                    PowerLog.v("PeGameController", "handleFGChangeinBG mXRPerflockHandleID > 0:" + this.U + ", mXRCommonPerflock=" + k0);
                    i.a().c(this.U);
                    this.U = 0;
                }
            } else if (PowerLog.V) {
                PowerLog.v("PeGameController", "handleFGChangeinBG not XR device");
            }
        }
        if (!this.G) {
            if (PowerLog.V) {
                PowerLog.v("PeGameController", "handleFGChangeinBG foreground:" + str + ", !mIsPowermodePerformance, not boost, return");
                return;
            }
            return;
        }
        if (s(strEncryptByPrivateKey2, this.f424c)) {
            if (PowerLog.V) {
                PowerLog.v("PeGameController", "Second -----------perf:" + strEncryptByPrivateKey2);
            }
            new ArrayList();
            this.f424c.get(strEncryptByPrivateKey2).f530c = (ArrayList) this.f424c.get(strEncryptByPrivateKey2).f528a.clone();
            if (this.f426e && (this.f427f || this.f428g)) {
                this.f424c.get(strEncryptByPrivateKey2).f530c.addAll(this.f424c.get(strEncryptByPrivateKey2).f529b);
            }
            ArrayList<String> arrayList = (ArrayList) this.f424c.get(strEncryptByPrivateKey2).f530c.clone();
            if (this.f439r.get(str) != null) {
                if (PowerLog.V) {
                    PowerLog.v("PeGameController", "mPushBoostPkgs has:" + str);
                }
                String strA = PerfUtils.a(this.f422a, this.f439r.get(str));
                if (PowerLog.V) {
                    PowerLog.v("PeGameController", "MSG_FG_CHANGE pushProc pid:" + strA);
                }
                if (!TextUtils.isEmpty(strA)) {
                    String str3 = "/dev/stune/top-app/cgroup.procs#9999999#" + strA + "#";
                    arrayList.add(str3);
                    arrayList.add("/dev/cpuset/top-app/cgroup.procs#9999999#" + strA + "#");
                }
            } else if (PowerLog.V) {
                PowerLog.v("PeGameController", "mPushBoostPkgs not has:" + str);
            }
            if (!this.f442u && !this.f443v) {
                if (!TextUtils.isEmpty(this.f431j) || !TextUtils.isEmpty(this.f432k)) {
                    p();
                } else if (PowerLog.V) {
                    PowerLog.v("PeGameController", "mPerflock empty, not call callPerflock");
                }
            }
            U(arrayList);
            if (this.f442u) {
                if (this.f444w) {
                    PowerLog.v("PeGameController", "handleFGChangeinBG mIsCloudConfMTKPerf == true");
                    MtkBoost.b(this.f422a, true);
                    this.f445x = true;
                } else if (PowerLog.V) {
                    PowerLog.v("PeGameController", "handleFGChangeinBG mIsCloudConfMTKPerf == false");
                }
            } else if (PowerLog.V) {
                PowerLog.v("PeGameController", "handleFGChangeinBG not MTK device");
            }
            if (!this.f443v) {
                if (PowerLog.V) {
                    PowerLog.v("PeGameController", "handleFGChangeinBG not XR device");
                    return;
                }
                return;
            }
            if (this.U > 0 || TextUtils.isEmpty(k0)) {
                PowerLog.v("PeGameController", "handleFGChangeinBG mXRPerflockHandleID > 0:" + this.U + ", mXRCommonPerflock=" + k0);
                return;
            }
            PowerLog.v("PeGameController", "handleFGChangeinBG mXRPerflockHandleID <= 0:" + this.U + ", mXRCommonPerflock=" + k0);
            r(k0);
        }
    }

    public boolean E() {
        return this.D.f518c;
    }

    public boolean F() {
        return this.D.f517b;
    }

    public boolean G() {
        return this.D.f516a;
    }

    public boolean H() {
        return this.D.f519d;
    }

    public boolean I(String str) {
        return this.f424c.containsKey(str.length() <= 53 ? EncryptUtils.encryptByPrivateKey(str, f420g0) : null);
    }

    public void K(ComponentName componentName) {
        PeSchedHandler peSchedHandler = this.f437p;
        if (peSchedHandler != null) {
            peSchedHandler.a(componentName);
        }
    }

    public void L(ComponentName componentName) {
        PeSchedHandler peSchedHandler = this.f437p;
        if (peSchedHandler != null) {
            peSchedHandler.b(componentName);
        }
    }

    public void M(ComponentName componentName) {
        PeSchedHandler peSchedHandler = this.f437p;
        if (peSchedHandler != null) {
            peSchedHandler.c(componentName);
        }
    }

    public void N(int i2) {
        PeSchedHandler peSchedHandler = this.f437p;
        if (peSchedHandler != null) {
            peSchedHandler.h(i2);
        }
    }

    @Override // com.miui.powerkeeper.perfengine.SchedController, com.miui.powerkeeper.perfengine.c
    public void a(ForegroundInfo foregroundInfo) {
        if (PowerLog.V) {
            PowerLog.v("PeGameController", "onForegroundChanged");
        }
        if (foregroundInfo != null) {
            this.f435n = foregroundInfo.mForegroundPackageName;
        }
    }

    @Override // com.miui.powerkeeper.perfengine.SchedController, com.miui.powerkeeper.perfengine.b
    public boolean b(String str) {
        try {
            String strOptString = new JSONObject(str).optString("perf_engine");
            if (TextUtils.isEmpty(strOptString)) {
                if (PowerLog.V) {
                    PowerLog.v("PeGameController", "update KEY_PERFENGINE_TAG is empty, return");
                }
                return true;
            }
            T(new JSONObject(strOptString));
            Log.i("PeGameController", "parse cl conf sussess");
            EventsAggregator eventsAggregator = PowerKeeperManager.getInstance().getEventsAggregator();
            if (eventsAggregator != null) {
                d0(eventsAggregator.getPowerMode());
            }
            return true;
        } catch (Exception e2) {
            Log.e("PeGameController", "invalid update1: " + e2.toString());
            e2.printStackTrace();
        }
    }

    public void d0(int i2) {
        boolean zEndsWith = SystemProperties.get("ro.product.mod_device", "").endsWith("_global");
        String str = SystemProperties.get("persist.sys.power.default.powermode", "");
        if (zEndsWith) {
            if (str.equals("enhance")) {
                if (i2 == 1) {
                    this.G = true;
                } else {
                    this.G = false;
                }
            } else if (i2 <= 2) {
                this.G = true;
            } else {
                this.G = false;
            }
        } else if (i2 == 1 || i2 == 2) {
            this.G = true;
        } else {
            this.G = false;
        }
        PowerLog.i("PeGameController", "updatePowerMode 220609_currentMode:" + str + ", powerMode:" + i2 + ", powerM:" + this.G);
    }

    public void u(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("SCHED CONFIGURATION:");
        printWriter.println("============================================== ");
        printWriter.println("ver:" + this.f429h);
        printWriter.println("name:" + this.f430i);
        printWriter.println("normal:" + this.f425d);
        printWriter.println("more:" + this.f426e);
        printWriter.println("MShow:" + this.f427f);
        printWriter.println("GB:" + this.f428g);
        printWriter.println("M:" + this.f444w);
        printWriter.println("Q:" + this.f447z);
        printWriter.println("devSpt:" + m0);
        printWriter.println("smtF:" + l0);
        printWriter.println("smtWC:" + n0);
        printWriter.println("AdSched:" + G());
        printWriter.println("AdAtSched:" + F());
        printWriter.println("AdAnBSched:" + E());
        printWriter.println("AdGfBSched:" + H());
        printWriter.println("powerM:" + this.G);
        printWriter.println("isM:" + this.f442u);
        printWriter.println("isXR:" + this.f443v);
        printWriter.println("============================================== ");
    }

    public int y(String str) {
        if (m0 && this.G && (str.equals(W) || str.equals(X) || str.equals(Y))) {
            if (G()) {
                int i2 = l0;
                if (!PowerLog.V) {
                    return i2;
                }
                PowerLog.v("PeGameController", "getBenchMarkFPS at cloud config smart FPS on:" + l0);
                return i2;
            }
            if (PowerLog.V) {
                PowerLog.v("PeGameController", "getBenchMarkFPS at cloud config smart FPS off");
            }
        }
        return -1;
    }

    public String z() {
        return this.f435n;
    }
}
