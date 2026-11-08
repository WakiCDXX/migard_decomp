package j;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.util.Slog;
import android.util.SparseBooleanArray;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.provider.UserConfigureHelper;
import com.miui.powerkeeper.unionpower.utils.UnionPowerConfig;
import com.miui.powerkeeper.utils.Constant;
import com.miui.powerkeeper.utils.PackageUtil;
import com.miui.powerkeeper.utils.Utils;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import miui.process.ActiveUidInfo;
import miui.process.ProcessConfig;
import miui.process.ProcessManager;
import org.json.JSONObject;

/* JADX INFO: compiled from: MusicIdleAppHandler.java */
/* JADX INFO: loaded from: classes.dex */
public class j extends j.a {

    /* JADX INFO: renamed from: s, reason: collision with root package name */
    private static final boolean f1960s = Log.isLoggable("MusicIdleAppHandler", 3);

    /* JADX INFO: renamed from: t, reason: collision with root package name */
    private static j f1961t;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    private AlarmManager f1974q;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private final int f1962e = 0;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private final int f1963f = 1;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private final int f1964g = 2;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private final int f1965h = 3;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private final PowerKeeperInterface.j f1966i = new a();

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private final BroadcastReceiver f1967j = new b();

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private final Object f1968k = new Object();

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    private List<String> f1969l = new ArrayList();

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    private long f1970m = 600;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    private SparseBooleanArray f1971n = new SparseBooleanArray();

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    private SparseBooleanArray f1972o = new SparseBooleanArray();

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    private Map<Integer, c> f1973p = new HashMap();

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    private boolean f1975r = true;

    /* JADX INFO: compiled from: MusicIdleAppHandler.java */
    class a implements PowerKeeperInterface.j {
        a() {
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.j
        public void onAppActiveChange(int i2, boolean z2) {
            j.this.obtainMessage(1, i2, 0, Boolean.valueOf(z2)).sendToTarget();
        }
    }

    /* JADX INFO: compiled from: MusicIdleAppHandler.java */
    class b extends BroadcastReceiver {
        b() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            int intExtra;
            if ("com.miui.powerkeeper.clean_music_task".equals(intent.getAction()) && (intExtra = intent.getIntExtra("uid", -10000)) != -10000) {
                j.this.obtainMessage(2, Integer.valueOf(intExtra)).sendToTarget();
            }
        }
    }

    /* JADX INFO: compiled from: MusicIdleAppHandler.java */
    private class c {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private final String f1978a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        private final int f1979b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        private PendingIntent f1980c;

        c(int i2, String str) {
            this.f1980c = null;
            this.f1979b = i2;
            this.f1978a = str;
            Intent intent = new Intent("com.miui.powerkeeper.clean_music_task");
            intent.putExtra("uid", i2);
            intent.setIdentifier(String.valueOf(i2));
            this.f1980c = PendingIntent.getBroadcast(j.a.f1894d, 0, intent, 67108864);
            if (j.f1960s) {
                Slog.w("MusicIdleAppHandler", this.f1980c.toString());
            }
            j.this.f1974q.setExact(3, SystemClock.elapsedRealtime() + (j.this.f1970m * 1000), this.f1980c);
            if (j.f1960s) {
                Slog.w("MusicIdleAppHandler", "alarm has been set");
            }
            j.this.f1973p.put(Integer.valueOf(i2), this);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean d() {
            try {
                j.this.f1974q.cancel(this.f1980c);
                Slog.w("MusicIdleAppHandler", this.f1980c.toString());
                if (!j.f1960s) {
                    return true;
                }
                Slog.w("MusicIdleAppHandler", "alarm has been cancel");
                return true;
            } catch (Exception e2) {
                Slog.e("MusicIdleAppHandler", String.format("Failed to cancel the clean task: [%d, %s]", Integer.valueOf(this.f1979b), this.f1978a), e2);
                return false;
            }
        }
    }

    private void l(int i2, String str) {
        c cVar;
        if (this.f1971n.get(i2) || this.f1972o.get(i2)) {
            if (this.f1973p.containsKey(Integer.valueOf(i2)) && (cVar = this.f1973p.get(Integer.valueOf(i2))) != null && cVar.d()) {
                this.f1973p.remove(Integer.valueOf(cVar.f1979b));
                Slog.w("MusicIdleAppHandler", String.format("Clean task cancel:[%d, %s]", Integer.valueOf(cVar.f1979b), cVar.f1978a));
                return;
            }
            return;
        }
        Slog.w("MusicIdleAppHandler", "Music App inactive and in background:" + str);
        c cVar2 = new c(i2, str);
        Slog.w("MusicIdleAppHandler", String.format("cleaning Music App:[%d, %s]", Integer.valueOf(cVar2.f1979b), cVar2.f1978a));
    }

    private void m(boolean z2) {
        if (z2) {
            this.f1895a.getActiveController().registerActiveListener(this.f1966i, 3);
            this.f1897c.e(this, UnionPowerConfig.ObserverEventEnum.FgActivityEvent, 0);
        } else {
            this.f1895a.getActiveController().unregisterActiveListener(this.f1966i, 3);
            this.f1897c.f(this, UnionPowerConfig.ObserverEventEnum.FgActivityEvent);
        }
    }

    public static synchronized h n(Context context) {
        try {
            if (!j.a.d()) {
                return null;
            }
            if (f1961t == null && context != null) {
                j.a.f1894d = context;
                j jVar = new j();
                f1961t = jVar;
                jVar.g(Constant.FUNC_CLEAR_UNACTIVE_MUSIC_APPS);
            }
            return f1961t;
        } catch (Throwable th) {
            throw th;
        }
    }

    private boolean o(String str) {
        List<String> noRestrictApps = UserConfigureHelper.getNoRestrictApps(j.a.f1894d);
        if (noRestrictApps == null || noRestrictApps.isEmpty()) {
            return false;
        }
        return noRestrictApps.contains(str);
    }

    private boolean p(int i2) {
        try {
            if (this.f1895a.getActiveController().getActiveUids(24).contains(Integer.valueOf(i2))) {
                return true;
            }
            List activeUidInfo = ProcessManager.getActiveUidInfo(1);
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
            Log.e("MusicIdleAppHandler", "Failed to get active uid: " + i2);
        }
        return false;
    }

    private void q(int i2, boolean z2) {
        if (f1960s) {
            Slog.w("MusicIdleAppHandler", "AudioStateChanged---uid:" + i2 + " active:" + z2);
        }
        String packageNameByUid = PackageUtil.getPackageNameByUid(Utils.APP_CONTEXT, i2);
        if (this.f1969l.contains(packageNameByUid)) {
            this.f1971n.put(i2, z2);
            synchronized (this.f1968k) {
                l(i2, packageNameByUid);
            }
        }
    }

    private void r(int i2, boolean z2) {
        if (f1960s) {
            Slog.w("MusicIdleAppHandler", "ForegroundStateChanged---uid:" + i2 + " active:" + z2);
        }
        String packageNameByUid = PackageUtil.getPackageNameByUid(j.a.f1894d, i2);
        if (this.f1969l.contains(packageNameByUid)) {
            this.f1972o.put(i2, z2);
            synchronized (this.f1968k) {
                l(i2, packageNameByUid);
            }
        }
    }

    private void s(int i2) {
        synchronized (this.f1968k) {
            try {
                c cVar = this.f1973p.get(Integer.valueOf(i2));
                if (cVar != null) {
                    if (!this.f1972o.get(cVar.f1979b, false) && !p(cVar.f1979b)) {
                        if (ProcessManager.isLockedApplication(cVar.f1978a, 0)) {
                            if (f1960s) {
                                Slog.e("MusicIdleAppHandler", "app locked");
                            }
                            return;
                        }
                        if (o(cVar.f1978a)) {
                            if (f1960s) {
                                Slog.e("MusicIdleAppHandler", "app policy is no restrict, pkg = " + cVar.f1978a + ";uid = " + cVar.f1979b);
                            }
                            return;
                        }
                        ProcessManager.kill(new ProcessConfig(13, cVar.f1978a, cVar.f1979b));
                        if (f1960s) {
                            Slog.w("MusicIdleAppHandler", "killed apk:" + cVar.f1978a);
                        }
                        i.a.a("MusicIdleAppHandler", "pkg=" + cVar.f1978a + " uid=" + cVar.f1979b);
                    }
                    if (f1960s) {
                        Slog.e("MusicIdleAppHandler", "quit to kill music app");
                    }
                    return;
                }
                this.f1973p.remove(Integer.valueOf(i2));
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // j.h
    public String a() {
        return "MusicIdleAppHandler";
    }

    @Override // j.h
    public Integer b() {
        return 3;
    }

    @Override // j.h
    public UnionPowerConfig.CloudModuleEnum c() {
        return UnionPowerConfig.CloudModuleEnum.BASE;
    }

    @Override // j.h
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("IsAppStateMonitor: " + this.f1975r);
        printWriter.println("mPkgList: " + this.f1969l);
        printWriter.println("mCleanTaskDelaySecond= " + this.f1970m);
    }

    @Override // j.a
    protected void e() {
        this.f1974q = (AlarmManager) j.a.f1894d.getSystemService("alarm");
        m(this.f1975r);
        j.a.f1894d.registerReceiver(this.f1967j, new IntentFilter("com.miui.powerkeeper.clean_music_task"), 2);
    }

    @Override // j.a
    protected void f() {
        if (f1960s) {
            Slog.w("MusicIdleAppHandler", "start updateMusicAppList");
        }
        String string = SimpleSettings.Misc.getString(j.a.f1894d, Constant.FUNC_CLEAR_UNACTIVE_MUSIC_APPS, "");
        String strOptString = "";
        boolean zOptBoolean = false;
        if (!string.isEmpty()) {
            try {
                JSONObject jSONObject = new JSONObject(string);
                zOptBoolean = jSONObject.optBoolean("fucSwitch", false);
                strOptString = jSONObject.optString("param", "");
            } catch (Exception unused) {
                Log.w("MusicIdleAppHandler", "parse err");
            }
        }
        if (this.f1975r != zOptBoolean) {
            this.f1975r = zOptBoolean;
            m(zOptBoolean);
        }
        synchronized (this.f1968k) {
            try {
                this.f1969l.clear();
                if (!strOptString.isEmpty()) {
                    this.f1969l.addAll(Arrays.asList(strOptString.split(",")));
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i2 = message.what;
        if (i2 == 0) {
            r(message.arg1, ((Boolean) message.obj).booleanValue());
            return;
        }
        if (i2 == 1) {
            q(message.arg1, ((Boolean) message.obj).booleanValue());
        } else if (i2 == 2) {
            s(((Integer) message.obj).intValue());
        } else {
            if (i2 != 3) {
                return;
            }
            f();
        }
    }
}
