package com.miui.powerkeeper.perfengine;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import com.android.internal.os.BackgroundThread;
import com.miui.powerkeeper.provider.SimpleSettingKeys;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.utils.PowerLog;
import com.miui.powerkeeper.utils.SharedPrefUtil;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class PeCloudManager {

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private static volatile PeCloudManager f404g;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private b.a f405a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private Context f406b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private c f407c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private List<com.miui.powerkeeper.perfengine.b> f408d = new ArrayList();

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private final String f409e = "com.miui.powerkeeper.perfengine_preferences";

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private ContentObserver f410f;

    class a extends ContentObserver {
        a(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z2) {
            super.onChange(z2);
            Log.i("PeCloudManager", "updateCldConfig onChange");
            PeCloudManager.this.f407c.sendEmptyMessage(1);
        }
    }

    class b implements b.a.d {
        b() {
        }

        @Override // b.a.d
        public void onChanged(JSONObject jSONObject) {
            Log.i("PeCloudManager", "new cloud");
            PeCloudManager.this.h();
        }
    }

    private class c extends Handler {
        public c(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what != 1) {
                return;
            }
            String str = SystemProperties.get("ro.build.version.incremental", "UNKNOWN");
            if (!str.equalsIgnoreCase(SharedPrefUtil.getInstance(PeCloudManager.this.f406b, "com.miui.powerkeeper.perfengine_preferences").getStringValue("ROM_VERSION", ""))) {
                SharedPrefUtil.getInstance(PeCloudManager.this.f406b, "com.miui.powerkeeper.perfengine_preferences").save("ROM_VERSION", str);
                boolean zA = PeCloudUpdate.a(PeCloudManager.this.f406b);
                if (PowerLog.V) {
                    PowerLog.v("PeCloudManager", "MSG_PERF_CLOUD_UPDATE isDelete: " + zA);
                    return;
                }
                return;
            }
            String strD = PeCloudUpdate.d(PeCloudManager.this.f406b, PeCloudManager.this.f405a, b.b.f31m);
            if (PowerLog.V) {
                PowerLog.v("PeCloudManager", "MSG_PERF_CLOUD_UPDATE updateCloudConfig: " + strD);
            }
            if (strD != null) {
                synchronized (PeCloudManager.this.f408d) {
                    try {
                        for (com.miui.powerkeeper.perfengine.b bVar : PeCloudManager.this.f408d) {
                            if (bVar instanceof PeGameController) {
                                bVar.b(strD);
                            }
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        }
    }

    private PeCloudManager(Context context) {
        this.f406b = null;
        this.f407c = null;
        this.f410f = new a(this.f407c);
        this.f406b = context;
        f();
        this.f407c = new c(BackgroundThread.getHandler().getLooper());
        this.f406b.getContentResolver().registerContentObserver(SimpleSettings.Misc.getUriFor(SimpleSettingKeys.KEY_PERF_ENGINE_UPID), false, this.f410f);
    }

    public static PeCloudManager e(Context context) {
        if (f404g == null) {
            synchronized (PeCloudManager.class) {
                try {
                    if (f404g == null) {
                        f404g = new PeCloudManager(context);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return f404g;
    }

    private void f() {
        b.a aVarP = b.a.p();
        this.f405a = aVarP;
        aVarP.t(b.b.f31m, new b());
    }

    public void g(com.miui.powerkeeper.perfengine.b bVar) {
        synchronized (this.f408d) {
            try {
                if (bVar == null) {
                    Log.e("PeCloudManager", "listener is null!");
                } else {
                    this.f408d.add(bVar);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void h() {
        this.f407c.sendEmptyMessage(1);
    }

    public void i(int i2) {
        this.f407c.sendEmptyMessage(i2);
    }
}
