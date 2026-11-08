package com.xiaomi.onetrack.api;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.xiaomi.onetrack.Configuration;
import com.xiaomi.onetrack.CrashAnalysis;
import com.xiaomi.onetrack.OnMainThreadException;
import com.xiaomi.onetrack.OneTrack;
import com.xiaomi.onetrack.ServiceQualityEvent;
import com.xiaomi.onetrack.util.DeviceUtil;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class j {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final String f1107a = "OneTrackImp";

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static ExecutorService f1108c;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private g f1109b;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private Context f1110d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private h f1111e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private Configuration f1112f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private OneTrack.ICommonPropertyProvider f1113g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private OneTrack.IEventHook f1114h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private com.xiaomi.onetrack.util.v f1115i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private boolean f1116j = false;

    public j(Context context, Configuration configuration) {
        Context applicationContext = context.getApplicationContext();
        this.f1110d = applicationContext;
        this.f1112f = configuration;
        b(applicationContext);
        Log.d(f1107a, "OneTrackImp init : " + configuration.toString());
        Log.d(f1107a, "OneTrackImp sdk ver : 2.0.5");
    }

    private void i() {
        f1108c.execute(new z(this));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void j() {
        if (com.xiaomi.onetrack.c.i.d()) {
            f1108c.execute(new aa(this));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void k() {
        try {
            if (this.f1112f.getMode() != OneTrack.Mode.APP) {
                return;
            }
            long jD = com.xiaomi.onetrack.f.a.d();
            String strA = a(jD, com.xiaomi.onetrack.f.a.c());
            String strA2 = com.xiaomi.onetrack.util.aa.A();
            if (TextUtils.isEmpty(strA2)) {
                com.xiaomi.onetrack.util.aa.j(strA);
                return;
            }
            JSONObject jSONObject = new JSONObject(strA2);
            long jOptLong = jSONObject.optLong(d.X);
            String strOptString = jSONObject.optString(d.Y);
            if (jOptLong != jD) {
                com.xiaomi.onetrack.util.aa.j(strA);
                this.f1109b.a(d.f1053j, e.a(jOptLong, strOptString, jD, com.xiaomi.onetrack.f.a.f(), this.f1112f, this.f1114h, this.f1115i, this.f1116j));
            }
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b(f1107a, "trackUpgradeEvent error: " + e2.toString());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void d() {
        f1108c.execute(new p(this));
    }

    private boolean e() {
        if (com.xiaomi.onetrack.util.p.f1665a) {
            com.xiaomi.onetrack.util.p.a(f1107a, "enable:" + f() + " isSupportEmptyEvent: " + g() + "_isSupportAdMonitor():" + h());
        }
        return f() && g() && h();
    }

    private boolean f() {
        try {
            int componentEnabledSetting = com.xiaomi.onetrack.f.a.b().getPackageManager().getComponentEnabledSetting(new ComponentName("com.miui.analytics", an.f997b));
            return componentEnabledSetting == 1 || componentEnabledSetting == 0;
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b(f1107a, "enable error:" + e2.toString());
            return false;
        }
    }

    private static boolean g() {
        try {
            int i2 = com.xiaomi.onetrack.f.a.b().getPackageManager().getPackageInfo("com.miui.analytics", 0).versionCode;
            if (i2 >= 2020062900) {
                return true;
            }
            com.xiaomi.onetrack.util.p.a(f1107a, "system analytics version: " + i2);
        } catch (Throwable th) {
            com.xiaomi.onetrack.util.p.b(f1107a, "isSupportEmptyEvent error:" + th.getMessage());
        }
        return false;
    }

    private boolean h() {
        try {
            if (TextUtils.isEmpty(this.f1112f.getAdEventAppId()) || OneTrack.isUseSystemNetTrafficOnly()) {
                return true;
            }
            int i2 = com.xiaomi.onetrack.f.a.b().getPackageManager().getPackageInfo("com.miui.analytics", 0).versionCode;
            com.xiaomi.onetrack.util.p.a(f1107a, "system analytics version: " + i2);
            return i2 >= 2022042900;
        } catch (Throwable th) {
            com.xiaomi.onetrack.util.p.b(f1107a, "isSupportAdMonitor error:" + th.getMessage());
            return true;
        }
    }

    private void b(Context context) {
        com.xiaomi.onetrack.util.p.a();
        com.xiaomi.onetrack.util.q.a(this.f1112f.isInternational(), this.f1112f.getRegion(), this.f1112f.getMode());
        if (f1108c == null) {
            f1108c = new ThreadPoolExecutor(0, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue());
        }
        this.f1115i = new com.xiaomi.onetrack.util.v(this.f1112f);
        if (com.xiaomi.onetrack.util.q.a() && e() && c()) {
            com.xiaomi.onetrack.util.o.a().a(Boolean.TRUE);
            this.f1109b = new al(this.f1112f, this.f1115i);
        } else {
            com.xiaomi.onetrack.util.o.a().a(Boolean.FALSE);
            this.f1109b = new aj(context, this.f1112f, this.f1115i);
        }
        if (this.f1112f.getMode() == OneTrack.Mode.APP) {
            com.xiaomi.onetrack.util.q.a(this.f1112f.isOverrideMiuiRegionSetting());
            c(context);
            if (this.f1112f.isExceptionCatcherEnable()) {
                CrashAnalysis.start(context, this);
                if (!CrashAnalysis.isSupport()) {
                    h hVar = new h();
                    this.f1111e = hVar;
                    hVar.a();
                }
            }
        }
        f1108c.execute(new k(this));
    }

    private boolean c() {
        if (this.f1112f.isOverrideMiuiRegionSetting()) {
            return TextUtils.equals(com.xiaomi.onetrack.util.q.j(), this.f1112f.getRegion());
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void d(boolean z2) {
        f1108c.execute(new s(this, z2));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean d(String str) {
        boolean zA = com.xiaomi.onetrack.util.r.a(str);
        if (!zA) {
            com.xiaomi.onetrack.util.p.b(f1107a, String.format("Invalid eventname: %s. Eventname can only consist of numbers, letters, underscores ,and can not start with a number or \"onetrack_\" or \"ot_\"", str));
        }
        return !zA;
    }

    private void c(Context context) {
        ((Application) context).registerActivityLifecycleCallbacks(new o(this));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean e(String str) {
        if ("onetrack_dau".equals(str) || "view".equals(str) || "ot_login".equals(str) || "ot_logout".equals(str)) {
            return false;
        }
        boolean zA = com.xiaomi.onetrack.util.r.a(str);
        if (!zA) {
            com.xiaomi.onetrack.util.p.b(f1107a, String.format("Invalid eventname: %s. Eventname can only consist of numbers, letters, underscores ,and can not start with a number or \"onetrack_\" or \"ot_\"", str));
        }
        return !zA;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public JSONObject f(String str) throws Throwable {
        try {
            OneTrack.ICommonPropertyProvider iCommonPropertyProvider = this.f1113g;
            JSONObject jSONObjectA = com.xiaomi.onetrack.util.r.a(iCommonPropertyProvider != null ? iCommonPropertyProvider.getDynamicProperty(str) : null, false);
            String strA = com.xiaomi.onetrack.util.k.a(com.xiaomi.onetrack.util.r.a(this.f1112f));
            return com.xiaomi.onetrack.util.r.a(jSONObjectA, !TextUtils.isEmpty(strA) ? new JSONObject(strA) : null);
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b(f1107a, "getCommonProperty: " + e2.toString());
            return null;
        }
    }

    public void a(String str, String str2, Map<String, Object> map) {
        f1108c.execute(new v(this, str2, map, str));
    }

    public void c(Map<String, Object> map) {
        if (map == null) {
            return;
        }
        f1108c.execute(new t(this, map));
    }

    public void a(String str, Map<String, Object> map) {
        f1108c.execute(new ac(this, str, map));
    }

    public void c(String str) {
        f1108c.execute(new x(this, str));
    }

    public void a(String str, Map<String, Object> map, List<String> list) {
        f1108c.execute(new ad(this, str, map, list));
    }

    public void c(boolean z2) {
        this.f1116j = z2;
    }

    public void a(String str) {
        f1108c.execute(new ae(this, str));
    }

    public void a(String str, String str2, String str3, String str4, String str5, long j2) {
        f1108c.execute(new af(this, str, str2, str3, str5, str4, j2));
    }

    public void a(Map<String, Object> map) {
        f1108c.execute(new ag(this, map));
    }

    public void a(String str, Object obj) {
        f1108c.execute(new ah(this, obj, str));
    }

    public void a(String str, OneTrack.UserIdType userIdType, Map<String, Object> map, boolean z2) {
        f1108c.execute(new ai(this, str, userIdType, z2, map));
    }

    public void a(String str, Number number) {
        f1108c.execute(new m(this, str, number));
    }

    public void a(Map<String, Object> map, boolean z2) {
        f1108c.execute(new n(this, z2, map));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(String str, boolean z2) {
        f1108c.execute(new q(this, str, z2));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(String str, long j2) {
        f1108c.execute(new r(this, str, j2));
    }

    public void a() {
        f1108c.execute(new u(this));
    }

    public void a(OneTrack.ICommonPropertyProvider iCommonPropertyProvider) {
        this.f1113g = iCommonPropertyProvider;
    }

    public String a(Context context) throws OnMainThreadException {
        if (!com.xiaomi.onetrack.util.w.a()) {
            return DeviceUtil.y(context);
        }
        throw new OnMainThreadException("Can't call this method on main thread");
    }

    public void b(Map<String, ? extends Number> map) {
        f1108c.execute(new l(this, map));
    }

    public void a(ServiceQualityEvent serviceQualityEvent) {
        if (serviceQualityEvent == null) {
            return;
        }
        f1108c.execute(new y(this, serviceQualityEvent));
    }

    public void b(String str) {
        f1108c.execute(new w(this, str));
    }

    public void a(boolean z2) {
        com.xiaomi.onetrack.util.p.f1665a = z2;
    }

    public String b() throws OnMainThreadException {
        if (!com.xiaomi.onetrack.util.w.a()) {
            return com.xiaomi.onetrack.util.o.a().b();
        }
        throw new OnMainThreadException("Can't call this method on main thread");
    }

    private String a(long j2, String str) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put(d.Y, str);
        jSONObject.put(d.X, j2);
        return jSONObject.toString();
    }

    public void b(boolean z2) {
        if (this.f1112f.isUseCustomPrivacyPolicy()) {
            f1108c.execute(new ab(this, z2));
        }
    }

    public void a(OneTrack.IEventHook iEventHook) {
        this.f1114h = iEventHook;
        this.f1115i.a(iEventHook);
    }
}
