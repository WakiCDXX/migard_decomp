package com.miui.powerkeeper;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import com.miui.powerkeeper.customerpower.CustomerPowerReceiver;
import miui.os.Build;

/* JADX INFO: loaded from: classes.dex */
public class PowerKeeperApplication extends Application {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static Context f241c;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    ContentResolver f242a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    ContentObserver f243b = new a(Handler.getMain());

    class a extends ContentObserver {
        a(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z2) {
            if (PowerKeeperApplication.d()) {
                Log.d("PowerKeeperApplication", "PowerKeeperApplication init after received provision finished msg.");
                PowerKeeperApplication.this.c();
                PowerKeeperApplication powerKeeperApplication = PowerKeeperApplication.this;
                powerKeeperApplication.f242a.unregisterContentObserver(powerKeeperApplication.f243b);
            }
        }
    }

    public static final Context b() {
        return f241c;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void c() {
        PowerKeeperReceiver.a(getApplicationContext());
        if ("com.miui.powerkeeper".equals(Application.getProcessName())) {
            CustomerPowerReceiver.register(getApplicationContext());
        }
    }

    public static boolean d() {
        return Build.IS_INTERNATIONAL_BUILD || SystemProperties.getBoolean("persist.vendor.sensor.satellite_box", false) || Settings.Global.getInt(f241c.getContentResolver(), "device_provisioning_service_agreed", 0) == 1 || Settings.Global.getInt(f241c.getContentResolver(), "device_provisioned", 0) == 1;
    }

    @Override // android.content.ContextWrapper
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        f241c = this;
    }

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        if (d()) {
            c();
            return;
        }
        Log.d("PowerKeeperApplication", "PowerKeeperApplication create delayed for provision.");
        ContentResolver contentResolver = f241c.getContentResolver();
        this.f242a = contentResolver;
        contentResolver.registerContentObserver(Settings.Global.getUriFor("device_provisioning_service_agreed"), false, this.f243b, -1);
        this.f242a.registerContentObserver(Settings.Global.getUriFor("device_provisioned"), false, this.f243b, -1);
    }

    @Override // android.app.Application
    public void onTerminate() {
        super.onTerminate();
        if ("com.miui.powerkeeper".equals(Application.getProcessName())) {
            CustomerPowerReceiver.unregister(getApplicationContext());
        }
    }
}
