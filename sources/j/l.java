package j;

import android.content.ComponentName;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.util.Slog;
import android.util.SparseBooleanArray;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.unionpower.utils.UnionPowerConfig;
import com.miui.powerkeeper.utils.PackageUtil;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import miui.process.ActiveUidInfo;
import miui.process.ProcessManager;
import miui.util.FeatureParser;
import org.json.JSONObject;

/* JADX INFO: compiled from: VideoIdleHandler.java */
/* JADX INFO: loaded from: classes.dex */
public class l extends j.a {
    private static l A;

    /* JADX INFO: renamed from: z, reason: collision with root package name */
    private static final boolean f1985z = Log.isLoggable("power.videoidle", 3);

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private PowerKeeperInterface.j f1992k;

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    private ContentObserver f1993l;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    private PowerKeeperInterface.f0 f1994m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    private boolean f1995n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    private ArrayList<String> f1996o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    private long f1997p;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    private boolean f1998q;

    /* JADX INFO: renamed from: s, reason: collision with root package name */
    private boolean f2000s;

    /* JADX INFO: renamed from: t, reason: collision with root package name */
    private boolean f2001t;

    /* JADX INFO: renamed from: u, reason: collision with root package name */
    private SparseBooleanArray f2002u;

    /* JADX INFO: renamed from: v, reason: collision with root package name */
    private boolean f2003v;

    /* JADX INFO: renamed from: w, reason: collision with root package name */
    private int f2004w;

    /* JADX INFO: renamed from: x, reason: collision with root package name */
    private String f2005x;

    /* JADX INFO: renamed from: y, reason: collision with root package name */
    private String f2006y;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private final boolean f1986e = false;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private final int f1987f = 180000;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private final long f1988g = 1000;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private final String f1989h = "vtb_boosting";

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private final String f1990i = "video_idle_timeout";

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private final Object f1991j = new Object();

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    private boolean f1999r = true;

    /* JADX INFO: compiled from: VideoIdleHandler.java */
    class a extends ContentObserver {
        a(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z2) {
            super.onChange(z2);
            l.this.f1998q = Settings.Secure.getInt(j.a.f1894d.getContentResolver(), "vtb_boosting", 0) == 1;
            i.a.a("UnionPower.VideoIdle", "vxbBoosting: " + l.this.f1998q);
            l.this.m();
        }
    }

    /* JADX INFO: compiled from: VideoIdleHandler.java */
    class b implements PowerKeeperInterface.z {
        b() {
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.z
        public void onRegistered(boolean z2) {
            l.this.f2000s = z2;
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.z
        public void onScreenChanged(boolean z2) {
            l.this.f2000s = z2;
            l.this.m();
        }
    }

    /* JADX INFO: compiled from: VideoIdleHandler.java */
    class c implements PowerKeeperInterface.j {
        c() {
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.j
        public void onAppActiveChange(int i2, boolean z2) {
            if (l.f1985z) {
                Log.d("UnionPower.VideoIdle", "uid:" + i2 + " active: " + z2);
            }
            l.this.obtainMessage(16, i2, 0, Boolean.valueOf(z2)).sendToTarget();
        }
    }

    public static boolean d() {
        return FeatureParser.getBoolean("support_video_idle_dim", true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void m() {
        EventsAggregator eventsAggregator;
        boolean z2 = this.f1995n && this.f1998q && this.f1999r && this.f2000s;
        if (z2 != this.f2001t) {
            if (f1985z) {
                Log.d("UnionPower.VideoIdle", "enableMonitor " + z2);
            }
            this.f2001t = z2;
            this.f2004w = -1;
            this.f2002u.clear();
            PowerKeeperManager powerKeeperManager = this.f1895a;
            if (powerKeeperManager != null && (eventsAggregator = this.f1896b) != null) {
                if (z2) {
                    powerKeeperManager.getActiveController().loadActiveUid(this.f2002u, 1);
                    this.f1992k = new c();
                    this.f1895a.getActiveController().registerActiveListener(this.f1992k, 1);
                    try {
                        ComponentName currentComponentName = this.f1896b.getCurrentComponentName();
                        if (currentComponentName != null) {
                            obtainMessage(4, this.f1896b.getCurrentUserId(), 0, currentComponentName).sendToTarget();
                        }
                    } catch (Exception e2) {
                        Slog.w("UnionPower.VideoIdle", "init component name msg failed=" + e2.getMessage());
                    }
                    this.f1896b.registerForegroundComponentChange(this);
                } else {
                    eventsAggregator.unregisterForForegroundInfo(this);
                    this.f1896b.unregisterForegroundComponentChange(this);
                    if (this.f1992k != null) {
                        this.f1895a.getActiveController().unregisterActiveListener(this.f1992k, 1);
                        this.f1992k = null;
                    }
                }
            }
        }
        r();
    }

    public static synchronized h n(Context context) {
        if (!d()) {
            if (Settings.Secure.getInt(context.getContentResolver(), "video_idle_status", 0) == 1) {
                Settings.Secure.putInt(context.getContentResolver(), "video_idle_status", 0);
            }
            return null;
        }
        if (A == null && context != null) {
            j.a.f1894d = context;
            l lVar = new l();
            A = lVar;
            lVar.g("video_idle_config");
        }
        return A;
    }

    private boolean o(int i2, int i3) {
        try {
            List activeUidInfo = ProcessManager.getActiveUidInfo(i3);
            if (activeUidInfo == null) {
                return false;
            }
            Iterator it = activeUidInfo.iterator();
            while (it.hasNext()) {
                if (((ActiveUidInfo) it.next()).uid == i2) {
                    return true;
                }
            }
        } catch (Exception unused) {
            Log.e("UnionPower.VideoIdle", "Failed to get active uid: " + i2 + " type=" + i3);
        }
        return false;
    }

    private void p(boolean z2) {
        if (f1985z) {
            Slog.w("UnionPower.VideoIdle", "notifyVideoIdleStatus=" + z2);
        }
        if (z2 && o(this.f2004w, 1)) {
            i.a.a("UnionPower.VideoIdle", "stopping notifyVideoIdleStatus");
            z2 = false;
        }
        synchronized (this.f1991j) {
            try {
                if (this.f2003v == z2) {
                    return;
                }
                if (!Settings.Secure.putInt(j.a.f1894d.getContentResolver(), "video_idle_status", z2 ? 1 : 0)) {
                    z2 = this.f2003v;
                }
                this.f2003v = z2;
                i.a.a("UnionPower.VideoIdle", "notifyVideoIdleStatus=" + this.f2003v + ", uid=" + this.f2004w + ", atv=" + this.f2005x);
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private void q(long j2) {
        if (f1985z) {
            Slog.w("UnionPower.VideoIdle", "notifyVideoIdleTimeout=" + j2);
        }
        i.a.a("UnionPower.VideoIdle", "notifyVideoIdleTimeout=" + j2);
        Settings.Secure.putLong(j.a.f1894d.getContentResolver(), "video_idle_timeout", j2);
    }

    private void r() {
        int i2;
        boolean z2 = (!this.f2001t || (i2 = this.f2004w) == -1 || this.f2002u.get(i2, false)) ? false : true;
        if (f1985z) {
            Log.d("UnionPower.VideoIdle", "isMonitor" + this.f2001t + ", isTrigger=" + z2 + ", targetUid=" + this.f2004w + ", audio=" + this.f2002u.get(this.f2004w, false));
        }
        if (hasMessages(8)) {
            removeMessages(8);
        }
        Message messageObtainMessage = obtainMessage(8);
        if (z2) {
            messageObtainMessage.arg1 = 1;
            sendMessageDelayed(messageObtainMessage, 1000L);
        } else {
            messageObtainMessage.arg1 = 0;
            sendMessage(messageObtainMessage);
        }
    }

    @Override // j.h
    public String a() {
        return "UnionPower.VideoIdle";
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
        if (f1985z) {
            printWriter.println("funcSwitch: " + this.f1995n);
            printWriter.println("timeout: " + this.f1997p);
            printWriter.println("activities: " + this.f1996o);
        }
        printWriter.println("vxbBoosting: " + this.f1998q);
        printWriter.println("full window: " + this.f1999r);
        printWriter.println("is monitor: " + this.f2001t);
        printWriter.println("fg package: " + this.f2006y);
        printWriter.println("fg activity: " + this.f2005x);
        printWriter.println("target uid: " + this.f2004w);
        if (this.f2004w != -1) {
            printWriter.println("audio active: " + o(this.f2004w, 1));
        }
        StringBuilder sb = new StringBuilder();
        sb.append("status: ");
        sb.append(Settings.Secure.getInt(j.a.f1894d.getContentResolver(), "video_idle_status", 0) == 1);
        printWriter.println(sb.toString());
    }

    @Override // j.a
    protected void e() {
        this.f2001t = false;
        Settings.Secure.putInt(j.a.f1894d.getContentResolver(), "video_idle_status", 0);
        this.f1996o = new ArrayList<>();
        this.f1995n = false;
        this.f1997p = 180000L;
        this.f2002u = new SparseBooleanArray();
        this.f1993l = new a(this);
        this.f1994m = new PowerKeeperInterface.f0(this, new b());
    }

    @Override // j.a
    protected void f() {
        boolean zOptBoolean;
        String strOptString = "";
        String string = SimpleSettings.Misc.getString(j.a.f1894d, "video_idle_config", "");
        if (f1985z) {
            Log.d("UnionPower.VideoIdle", "updateConfig=" + string);
        }
        long jOptLong = 180000;
        if (string == null || string.isEmpty()) {
            zOptBoolean = false;
        } else {
            try {
                JSONObject jSONObject = new JSONObject(string);
                zOptBoolean = jSONObject.optBoolean("funcSwitch", false);
                try {
                    strOptString = jSONObject.optString("activities", "");
                    jOptLong = jSONObject.optLong("timeout", 180000L);
                } catch (Exception e2) {
                    e = e2;
                    Log.d("UnionPower.VideoIdle", "parse exception=" + e.getMessage());
                }
            } catch (Exception e3) {
                e = e3;
                zOptBoolean = false;
            }
        }
        this.f1996o.clear();
        this.f1996o.addAll(Arrays.asList(strOptString.split(",")));
        if (this.f1997p != jOptLong) {
            q(jOptLong);
            this.f1997p = jOptLong;
        }
        if (zOptBoolean != this.f1995n) {
            if (zOptBoolean) {
                this.f1998q = Settings.Secure.getInt(j.a.f1894d.getContentResolver(), "vtb_boosting", 0) == 1;
                j.a.f1894d.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("vtb_boosting"), false, this.f1993l);
                PowerKeeperManager powerKeeperManager = this.f1895a;
                if (powerKeeperManager != null) {
                    powerKeeperManager.registerScreenChangedListener(this.f1994m);
                }
                this.f1897c.e(this, UnionPowerConfig.ObserverEventEnum.MultiWindowEvent, 2);
            } else {
                j.a.f1894d.getContentResolver().unregisterContentObserver(this.f1993l);
                this.f1998q = false;
                PowerKeeperManager powerKeeperManager2 = this.f1895a;
                if (powerKeeperManager2 != null) {
                    powerKeeperManager2.unregisterScreenChangedListener(this.f1994m);
                }
                this.f1897c.f(this, UnionPowerConfig.ObserverEventEnum.MultiWindowEvent);
            }
            this.f1995n = zOptBoolean;
        }
        m();
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i2 = message.what;
        if (i2 == -33 || i2 == 4) {
            ComponentName componentName = (ComponentName) message.obj;
            int i3 = message.arg1;
            i.a.a("UnionPower.VideoIdle", "ComponentName: " + componentName);
            if (componentName != null) {
                this.f2005x = componentName.getClassName();
                this.f2006y = componentName.getPackageName();
                if (this.f1996o.contains(this.f2005x)) {
                    this.f2004w = PackageUtil.getUidByPackageName(j.a.f1894d, this.f2006y, i3);
                } else {
                    this.f2004w = -1;
                }
            }
            if (f1985z) {
                Log.d("UnionPower.VideoIdle", "COMMON_MSG_FG_COMPONENT_CHANGE, componentName=" + componentName + ", mTargetUid=" + this.f2004w + ", user=" + i3);
            }
            r();
            return;
        }
        if (i2 == 8) {
            p(message.arg1 == 1);
            return;
        }
        if (i2 == 16) {
            int i4 = message.arg1;
            boolean zBooleanValue = ((Boolean) message.obj).booleanValue();
            if (zBooleanValue != this.f2002u.get(i4, false)) {
                this.f2002u.put(i4, zBooleanValue);
                r();
                return;
            }
            return;
        }
        if (i2 == 1) {
            Settings.Secure.putInt(j.a.f1894d.getContentResolver(), "video_idle_status", 0);
            f();
        } else {
            if (i2 != 2) {
                return;
            }
            this.f1999r = ((Boolean) message.obj).booleanValue();
            i.a.a("UnionPower.VideoIdle", "fullWindow: " + this.f1999r);
            m();
        }
    }
}
