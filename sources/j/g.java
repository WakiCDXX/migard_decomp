package j;

import android.content.Context;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.ArraySet;
import android.util.LocalLog;
import android.util.Log;
import android.util.Slog;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.unionpower.utils.UnionPowerConfig;
import com.xiaomi.channel.commonutils.android.DeviceInfo;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;
import miui.process.ForegroundInfo;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: compiled from: DisplayFeatureHandler.java */
/* JADX INFO: loaded from: classes.dex */
public class g extends a {

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    private static final boolean f1939m = Log.isLoggable("DisplayFeatureHandler", 3);

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    private static g f1940n;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private ForegroundInfo f1946j;

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private boolean f1947k;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private LocalLog f1941e = new LocalLog(64);

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private ArraySet<String> f1942f = new ArraySet<>(10);

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private final int f1943g = 1;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private final int f1944h = 2;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private final int f1945i = 31107;

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    private final int f1948l = DeviceInfo.DUAL_SPACE_ID;

    public static synchronized h h(Context context) {
        try {
            if (!a.d()) {
                return null;
            }
            if (f1940n == null && context != null) {
                a.f1894d = context;
                g gVar = new g();
                f1940n = gVar;
                gVar.g("display_feature_group");
            }
            return f1940n;
        } catch (Throwable th) {
            throw th;
        }
    }

    private void i() {
        boolean zContains = this.f1942f.contains(this.f1946j.mForegroundPackageName);
        if (zContains != this.f1947k) {
            this.f1947k = zContains;
            j(DeviceInfo.DUAL_SPACE_ID, zContains ? 1 : 0, this.f1946j.mForegroundPackageName);
            if (f1939m) {
                Log.d("DisplayFeatureHandler", "onForegroundChanged for OD value=" + (zContains ? 1 : 0));
            }
        }
    }

    private void j(int i2, int i3, String str) {
        IBinder service = ServiceManager.getService(LogType.LOG_TYPE_SF_NAME);
        if (service != null) {
            Parcel parcelObtain = Parcel.obtain();
            parcelObtain.writeInterfaceToken("android.ui.ISurfaceComposer");
            parcelObtain.writeInt(i2);
            parcelObtain.writeInt(i3);
            parcelObtain.writeString(str);
            try {
                try {
                    service.transact(31107, parcelObtain, null, 0);
                } catch (RemoteException | SecurityException e2) {
                    Slog.e("DisplayFeatureHandler", "Failed to notify SurfaceFlinger", e2);
                }
            } finally {
                parcelObtain.recycle();
            }
        }
    }

    private void k(String str) {
        try {
            str = new JSONObject(str).optString("cloud_od_group", "");
        } catch (JSONException e2) {
            l("parseODappFromDb e=" + e2);
        }
        if (str == null || str.isEmpty()) {
            l("parseODappFromDb empty");
            return;
        }
        this.f1942f.clear();
        this.f1942f.addAll(Arrays.asList(str.split(",")));
    }

    private void l(String str) {
        if (f1939m) {
            Log.i("DisplayFeatureHandler", str);
        }
        this.f1941e.log(str);
    }

    @Override // j.h
    public String a() {
        return "DisplayFeatureHandler";
    }

    @Override // j.h
    public Integer b() {
        return 2;
    }

    @Override // j.h
    public UnionPowerConfig.CloudModuleEnum c() {
        return UnionPowerConfig.CloudModuleEnum.OTHER;
    }

    @Override // j.h
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        this.f1941e.dump(fileDescriptor, printWriter, strArr);
        printWriter.println("mOdPackages: " + this.f1942f.toString());
    }

    @Override // j.a
    protected void e() {
        this.f1897c.e(this, UnionPowerConfig.ObserverEventEnum.FgComponentEvent, 1);
    }

    @Override // j.a
    protected void f() {
        l("onUpdateConfig DisplayFeatureHandler");
        String string = SimpleSettings.Misc.getString(a.f1894d, "display_feature_group", "");
        if (string.isEmpty()) {
            return;
        }
        k(string);
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i2 = message.what;
        if (i2 == 1) {
            this.f1946j = (ForegroundInfo) message.obj;
            i();
        } else {
            if (i2 != 2) {
                return;
            }
            if (f1939m) {
                Log.d("DisplayFeatureHandler", "MSG_ID_CLOUD_UPDATE");
            }
            f();
        }
    }
}
