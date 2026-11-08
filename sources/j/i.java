package j;

import android.content.Context;
import android.os.Message;
import android.os.ServiceManager;
import android.util.Log;
import android.util.Slog;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.unionpower.utils.UnionPowerConfig;
import com.miui.whetstone.IPowerKeeperPolicy;
import com.miui.whetstone.server.IWhetstoneActivityManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import miui.util.FeatureParser;
import org.json.JSONObject;

/* JADX INFO: compiled from: LazyFpsSetting.java */
/* JADX INFO: loaded from: classes.dex */
public class i extends a {

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    private static i f1949o;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private IPowerKeeperPolicy f1951f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private IWhetstoneActivityManager f1952g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private Method f1953h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private Method f1954i;

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    private List<String> f1957l;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    private int f1958m;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private boolean f1950e = Log.isLoggable("UnionPower.LazyFps", 3);

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private boolean f1955j = false;

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private boolean f1956k = true;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    private String f1959n = "";

    public static boolean d() {
        return FeatureParser.getBoolean("support_lazy_fps", false);
    }

    private void h(boolean z2, List<String> list, int i2) {
        if (list != null) {
            this.f1957l.addAll(list);
        }
        this.f1958m = i2;
        if (this.f1953h == null) {
            Slog.e("UnionPower.LazyFps", "Get update method failed");
            return;
        }
        IWhetstoneActivityManager iWhetstoneActivityManager = this.f1952g;
        if (iWhetstoneActivityManager == null) {
            Slog.e("UnionPower.LazyFps", "Fail to get mWs");
            return;
        }
        try {
            if (this.f1951f == null) {
                this.f1951f = iWhetstoneActivityManager.getPowerKeeperPolicy();
            }
            if (this.f1950e) {
                Slog.d("UnionPower.LazyFps", "start to update config");
            }
            this.f1953h.invoke(this.f1951f, this.f1957l, Integer.valueOf(this.f1958m));
        } catch (Exception e2) {
            Log.e("UnionPower.LazyFps", "update config failed=" + e2);
        }
        if (z2 != this.f1955j) {
            if (z2) {
                this.f1897c.e(this, UnionPowerConfig.ObserverEventEnum.TouchEvent, 2, 1);
                EventsAggregator eventsAggregator = this.f1896b;
                if (eventsAggregator != null) {
                    eventsAggregator.registerCurrentActivityChangeListener(this);
                }
            } else {
                this.f1897c.f(this, UnionPowerConfig.ObserverEventEnum.TouchEvent);
                EventsAggregator eventsAggregator2 = this.f1896b;
                if (eventsAggregator2 != null) {
                    eventsAggregator2.unregisterCurrentActivityChangeListener(this);
                }
            }
            this.f1955j = z2;
        }
    }

    public static synchronized h i(Context context) {
        try {
            if (!d()) {
                return null;
            }
            if (f1949o == null && context != null) {
                a.f1894d = context;
                i iVar = new i();
                f1949o = iVar;
                iVar.g("lazy_fps_group");
            }
            return f1949o;
        } catch (Throwable th) {
            throw th;
        }
    }

    private void j(int i2) {
        if (this.f1954i == null || this.f1952g == null || i2 < 1 || i2 > 3) {
            return;
        }
        boolean z2 = i2 == 1;
        if ((true ^ this.f1956k) ^ z2) {
            return;
        }
        if (this.f1950e) {
            Log.d("UnionPower.LazyFps", "Set " + this.f1959n + " state " + z2);
        }
        try {
            if (this.f1951f == null) {
                this.f1951f = this.f1952g.getPowerKeeperPolicy();
            }
            this.f1954i.invoke(this.f1951f, Boolean.valueOf(z2));
            this.f1956k = z2;
        } catch (Exception e2) {
            Log.e("UnionPower.LazyFps", " Set state failed. " + e2);
        }
    }

    @Override // j.h
    public String a() {
        return "UnionPower.LazyFps";
    }

    @Override // j.h
    public Integer b() {
        return 1;
    }

    @Override // j.h
    public UnionPowerConfig.CloudModuleEnum c() {
        return UnionPowerConfig.CloudModuleEnum.BASE;
    }

    @Override // j.h
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("LazyFpsList: [" + SimpleSettings.Misc.getString(a.f1894d, "lazy_fps_group", "") + "]");
    }

    @Override // j.a
    protected void e() {
        this.f1957l = new ArrayList();
        this.f1952g = IWhetstoneActivityManager.Stub.asInterface(ServiceManager.getService("whetstone.activity"));
        try {
            this.f1953h = IPowerKeeperPolicy.class.getDeclaredMethod("notifyLazyFpsConfigChanged", List.class, Integer.TYPE);
            this.f1954i = IPowerKeeperPolicy.class.getDeclaredMethod("setState", Boolean.TYPE);
        } catch (Exception e2) {
            Log.e("UnionPower.LazyFps", "Prepare methods failed", e2);
        }
    }

    @Override // j.a
    protected void f() {
        String string = SimpleSettings.Misc.getString(a.f1894d, "lazy_fps_group", "");
        Log.d("UnionPower.LazyFps", "onUpdateConfig=" + string);
        boolean zOptBoolean = false;
        List<String> listAsList = null;
        int iOptInt = 60;
        try {
            JSONObject jSONObject = new JSONObject(string);
            zOptBoolean = jSONObject.optBoolean("fucSwitch", false);
            if (zOptBoolean) {
                iOptInt = jSONObject.optInt("frameRate", 60);
                listAsList = Arrays.asList(jSONObject.optString("controlList", "").split(","));
            }
        } catch (Exception e2) {
            Log.e("UnionPower.LazyFps", "update config failed" + e2);
        }
        h(zOptBoolean, listAsList, iOptInt);
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i2 = message.what;
        if (i2 != -29) {
            if (i2 == 1) {
                f();
                return;
            } else {
                if (i2 == 2 && this.f1957l.contains(this.f1959n)) {
                    j(((Integer) message.obj).intValue());
                    return;
                }
                return;
            }
        }
        this.f1959n = (String) message.obj;
        if (this.f1950e) {
            Log.d("UnionPower.LazyFps", "Activity change = " + this.f1959n);
        }
    }
}
