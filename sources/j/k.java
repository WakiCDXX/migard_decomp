package j;

import android.content.Context;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.unionpower.utils.UnionPowerConfig;
import com.miui.powerkeeper.utils.FileUtil;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import miui.yellowpage.YellowPageContract;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: compiled from: PerfFeatureHandler.java */
/* JADX INFO: loaded from: classes.dex */
public class k extends a {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static final boolean f1982e = Log.isLoggable("PerfFeatureHandler", 3);

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static k f1983f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private static Set<String> f1984g;

    public static boolean d() {
        return UnionPowerConfig.f657b;
    }

    public static synchronized h h(Context context) {
        if (!d()) {
            Log.d("PerfFeatureHandler", "no support");
            return null;
        }
        if (f1983f == null && context != null) {
            if (f1982e) {
                Log.d("PerfFeatureHandler", "PerfFeatureHandler sInstance null!");
            }
            a.f1894d = context;
            k kVar = new k();
            f1983f = kVar;
            kVar.g("performance_feature_group");
        }
        return f1983f;
    }

    private void i() {
        if (SystemProperties.getBoolean("persist.sys.performance.appCacheOptimization", false)) {
            f1984g.add("app_cache_optimization_cfg");
        }
    }

    private void j() {
        f1984g.add("app_laucnh_preload_cfg");
    }

    private void k(String str) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            Iterator<String> itKeys = jSONObject.keys();
            while (itKeys.hasNext()) {
                String next = itKeys.next();
                if (o(next)) {
                    l(jSONObject.getJSONObject(next));
                }
            }
        } catch (JSONException e2) {
            p("parseAllConfig e=" + e2);
        }
    }

    private void l(JSONObject jSONObject) {
        try {
            n(jSONObject);
            m(jSONObject);
        } catch (JSONException e2) {
            p("parseConfig e=" + e2);
        }
    }

    private void m(JSONObject jSONObject) throws JSONException {
        if (f1982e) {
            Log.d("PerfFeatureHandler", "enter parseSwitchFile!");
        }
        JSONArray jSONArrayOptJSONArray = jSONObject.optJSONArray("switch_file");
        if (jSONArrayOptJSONArray == null || jSONArrayOptJSONArray.length() == 0) {
            Log.d("PerfFeatureHandler", "parseSwitchFile failed, fileAttributes null!");
            return;
        }
        for (int i2 = 0; i2 < jSONArrayOptJSONArray.length(); i2++) {
            JSONObject jSONObject2 = jSONArrayOptJSONArray.getJSONObject(i2);
            if (jSONObject2 == null || jSONObject2.optString("file_path", "").isEmpty()) {
                Log.d("PerfFeatureHandler", "wrong in attribute of writing file");
            } else {
                sendMessage(obtainMessage(2, jSONObject2));
            }
        }
    }

    private void n(JSONObject jSONObject) throws JSONException {
        Log.d("PerfFeatureHandler", "parseSwitchProp!");
        JSONArray jSONArrayOptJSONArray = jSONObject.optJSONArray("switch_prop");
        if (jSONArrayOptJSONArray == null || jSONArrayOptJSONArray.length() == 0) {
            Log.d("PerfFeatureHandler", "parseSwitchProp failed, props null!");
            return;
        }
        for (int i2 = 0; i2 < jSONArrayOptJSONArray.length(); i2++) {
            JSONObject jSONObject2 = jSONArrayOptJSONArray.getJSONObject(i2);
            String strOptString = jSONObject2.optString("prop_name", "");
            String strOptString2 = jSONObject2.optString(YellowPageContract.Permission.STATE, "");
            if (!strOptString.isEmpty()) {
                try {
                    SystemProperties.set(strOptString, strOptString2);
                    p(String.format("set prop %s=%s", strOptString, strOptString2));
                } catch (Exception e2) {
                    p(String.format("set prop %s failed:%s", strOptString, e2));
                }
            }
        }
    }

    private boolean o(String str) {
        return f1984g.contains(str);
    }

    private void p(String str) {
        if (f1982e) {
            Log.i("PerfFeatureHandler", str);
        }
    }

    @Override // j.h
    public String a() {
        return "PerfFeatureHandler";
    }

    @Override // j.h
    public Integer b() {
        return 1;
    }

    @Override // j.h
    public UnionPowerConfig.CloudModuleEnum c() {
        return UnionPowerConfig.CloudModuleEnum.OTHER;
    }

    @Override // j.a
    protected void e() {
        f1984g = new HashSet();
        Log.d("PerfFeatureHandler", "onPrepare");
        i();
        j();
    }

    @Override // j.a
    protected void f() {
        boolean z2 = f1982e;
        if (z2) {
            Log.i("PerfFeatureHandler", "enter updateConfig");
        }
        String string = SimpleSettings.Misc.getString(a.f1894d, "performance_feature_group", "");
        if (z2) {
            Log.d("PerfFeatureHandler", "updateConfig=" + string);
        }
        if (string == null || string.isEmpty()) {
            Log.d("PerfFeatureHandler", "configStr null!");
        } else {
            k(string);
        }
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i2 = message.what;
        if (i2 == 1) {
            if (f1982e) {
                Log.i("PerfFeatureHandler", "Start to update");
            }
            f();
            return;
        }
        if (i2 != 2) {
            return;
        }
        JSONObject jSONObject = (JSONObject) message.obj;
        String strOptString = jSONObject.optString("file_path", "");
        String strOptString2 = jSONObject.optString("content", "");
        if (FileUtil.writeToFile(strOptString, strOptString2)) {
            if (f1982e) {
                Log.d("PerfFeatureHandler", "write file suceess, path:" + strOptString);
                return;
            }
            return;
        }
        if (f1982e) {
            Log.e("PerfFeatureHandler", "write failed, path:" + strOptString + "\n content:" + strOptString2);
        }
    }

    @Override // j.h
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
    }
}
