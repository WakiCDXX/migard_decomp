package k;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.os.ServiceManager;
import android.os.storage.IStorageManager;
import android.text.TextUtils;
import android.util.Log;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.unionpower.UnionPowerService;
import com.miui.powerkeeper.unionpower.utils.UnionPowerConfig;
import com.miui.powerkeeper.utils.PackageUtil;
import com.miui.powerkeeper.utils.Utils;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import l.l;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: compiled from: MifsHandler.java */
/* JADX INFO: loaded from: classes.dex */
public class b extends Handler {

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static b f2012i;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private EventsAggregator f2014a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private l f2015b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private final ConcurrentHashMap<String, a> f2016c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private final IStorageManager f2017d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private Context f2018e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private volatile int f2019f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private String f2020g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private static final boolean f2011h = Log.isLoggable("UnionPower.Mifs", 3);

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private static final Object f2013j = new Object();

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: compiled from: MifsHandler.java */
    static class a {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        String f2021a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        Map<String, Object> f2022b;

        private a() {
        }

        public static a a(String str, String str2, String str3, String str4) {
            a aVar = new a();
            aVar.f2021a = str;
            HashMap map = new HashMap();
            aVar.f2022b = map;
            try {
                map.put("cachesifter_enable", Integer.valueOf(Integer.parseInt(str2)));
                aVar.f2022b.put("app_version_lower", new BigInteger(str3));
                aVar.f2022b.put("app_version_upper", new BigInteger(str4));
                return aVar;
            } catch (NumberFormatException unused) {
                return null;
            }
        }
    }

    private b(Context context) {
        super(UnionPowerService.b().getLooper());
        this.f2019f = 0;
        this.f2020g = "";
        this.f2018e = context;
        this.f2016c = new ConcurrentHashMap<>();
        this.f2017d = e();
        EventsAggregator eventsAggregator = EventsAggregator.getInstance(context);
        this.f2014a = eventsAggregator;
        this.f2015b = l.b(context, eventsAggregator);
    }

    private long[] c(String str) {
        long jLongValue;
        int iIntValue;
        long jLongValue2;
        if (this.f2016c.get(str) != null) {
            iIntValue = ((Integer) this.f2016c.get(str).f2022b.get("cachesifter_enable")).intValue();
            jLongValue = ((BigInteger) this.f2016c.get(str).f2022b.get("app_version_lower")).longValue();
            jLongValue2 = ((BigInteger) this.f2016c.get(str).f2022b.get("app_version_upper")).longValue();
        } else {
            jLongValue = 0;
            iIntValue = 0;
            jLongValue2 = 0;
        }
        return new long[]{iIntValue, jLongValue, jLongValue2};
    }

    public static synchronized b d(Context context) {
        try {
            if (!h()) {
                return null;
            }
            if (f2012i == null && context != null) {
                b bVar = new b(context);
                f2012i = bVar;
                bVar.n("mifs_cfg");
            }
            return f2012i;
        } catch (Throwable th) {
            throw th;
        }
    }

    private IStorageManager e() {
        try {
            return IStorageManager.Stub.asInterface(ServiceManager.getService("mount"));
        } catch (VerifyError unused) {
            return null;
        }
    }

    private long f(Context context, String str) {
        try {
            return context.getPackageManager().getPackageInfo(str, PackageManager.PackageInfoFlags.of(0L)).getLongVersionCode();
        } catch (PackageManager.NameNotFoundException unused) {
            return 0L;
        }
    }

    private void g(int i2) {
        if (i2 == this.f2019f || this.f2014a == null) {
            return;
        }
        if (i2 == 1) {
            this.f2015b.e(this, UnionPowerConfig.ObserverEventEnum.FgActivityEvent, 2);
        } else {
            this.f2015b.f(this, UnionPowerConfig.ObserverEventEnum.FgActivityEvent);
        }
    }

    public static boolean h() {
        return Utils.isBelongToDeviceSeries(new String[]{"corot"}[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void i(StringBuilder sb, String str, a aVar) {
        sb.append("{ appName: ");
        sb.append(str);
        sb.append(", AppConfig: ");
        sb.append(aVar != null ? aVar.toString() : "null");
        sb.append("}");
    }

    private void j() {
        if (this.f2019f != 1) {
            o(0, this.f2019f);
            return;
        }
        long[] jArrC = c(this.f2020g);
        if (TextUtils.isEmpty(this.f2020g) || jArrC[1] > f(this.f2018e, this.f2020g) || jArrC[2] < f(this.f2018e, this.f2020g)) {
            o(0, this.f2019f);
        } else {
            o((int) jArrC[0], this.f2019f);
        }
    }

    private a m(JSONObject jSONObject) {
        try {
            String string = jSONObject.getString("app_name");
            String strOptString = jSONObject.optString("cachesifter_enable");
            String strOptString2 = jSONObject.optString("app_version_lower");
            String strOptString3 = jSONObject.optString("app_version_upper");
            if (strOptString2.isEmpty() || strOptString3.isEmpty()) {
                strOptString2 = "0";
                strOptString3 = "0";
            }
            return a.a(string, strOptString, strOptString2, strOptString3);
        } catch (JSONException unused) {
            return null;
        }
    }

    private void n(String str) {
        this.f2015b.d(this, UnionPowerConfig.CloudModuleEnum.OTHER, 1, str);
        l();
    }

    private void o(int i2, int i3) {
        try {
            Class<?> cls = Class.forName("android.os.storage.IStorageManager");
            Class<?> cls2 = Integer.TYPE;
            Method method = cls.getMethod("syncMifsEnable", cls2);
            Method method2 = cls.getMethod("syncMifskoSwitch", cls2);
            try {
                IStorageManager iStorageManager = this.f2017d;
                if (i3 == 0) {
                    i2 = 0;
                }
                method.invoke(iStorageManager, Integer.valueOf(i2));
                method2.invoke(this.f2017d, Integer.valueOf(i3));
            } catch (Exception e2) {
                Log.d("UnionPower.Mifs", e2.toString());
            }
        } catch (ClassNotFoundException | NoSuchMethodException unused) {
            Log.d("UnionPower.Mifs", "vold methods not found");
            this.f2019f = 0;
            this.f2016c.clear();
            this.f2015b.f(this, UnionPowerConfig.ObserverEventEnum.FgActivityEvent);
        }
    }

    public void b(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        final StringBuilder sb = new StringBuilder();
        sb.append("mifsEnable: ");
        sb.append(this.f2019f);
        sb.append(", ");
        this.f2016c.forEach(new BiConsumer() { // from class: k.a
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                b.i(sb, (String) obj, (b.a) obj2);
            }
        });
        printWriter.println("mifs config list :[" + ((Object) sb) + "]");
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i2 = message.what;
        if (i2 != 1) {
            if (i2 != 2) {
                return;
            }
            k(message.arg1, ((Boolean) message.obj).booleanValue());
        } else {
            if (f2011h) {
                Log.i("UnionPower.Mifs", "Start to update");
            }
            l();
        }
    }

    public void k(int i2, boolean z2) {
        if (z2) {
            String packageNameByUid = PackageUtil.getPackageNameByUid(this.f2018e, i2);
            if (TextUtils.isEmpty(packageNameByUid)) {
                return;
            }
            this.f2020g = packageNameByUid;
            j();
        }
    }

    protected void l() {
        if (f2011h) {
            Log.i("UnionPower.Mifs", "enter updateConfig");
        }
        String string = SimpleSettings.Misc.getString(this.f2018e, "mifs_cfg", "");
        if (string.isEmpty()) {
            return;
        }
        synchronized (f2013j) {
            try {
                JSONObject jSONObject = new JSONObject(string);
                String strOptString = jSONObject.optString("mifs_enable");
                g("1".equals(strOptString) ? 1 : 0);
                this.f2019f = "1".equals(strOptString) ? 1 : 0;
                if (this.f2019f == 0) {
                    this.f2016c.clear();
                    o(0, 0);
                    Log.i("UnionPower.Mifs", "mifs shutdown");
                    return;
                }
                JSONArray jSONArrayOptJSONArray = jSONObject.optJSONArray("app_list");
                this.f2016c.clear();
                for (int i2 = 0; jSONArrayOptJSONArray != null && i2 < jSONArrayOptJSONArray.length(); i2++) {
                    a aVarM = m(jSONArrayOptJSONArray.optJSONObject(i2));
                    if (aVarM != null) {
                        this.f2016c.put(aVarM.f2021a, aVarM);
                    }
                }
            } catch (Exception e2) {
                Log.e("UnionPower.Mifs", "update config failed" + e2);
                this.f2019f = 0;
                this.f2016c.clear();
                o(0, 0);
            }
        }
    }
}
