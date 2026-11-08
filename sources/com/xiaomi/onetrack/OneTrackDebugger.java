package com.xiaomi.onetrack;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.xiaomi.onetrack.util.o;
import com.xiaomi.onetrack.util.oaid.a;
import java.util.concurrent.ConcurrentHashMap;

/* JADX INFO: loaded from: classes.dex */
public class OneTrackDebugger {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static volatile OneTrackDebugger f845a = null;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static String f846b = "com.xiaomi.onetrack.otdebugger.FloatWindowService";

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private ConcurrentHashMap<Long, Configuration> f847c = new ConcurrentHashMap<>();

    private OneTrackDebugger() {
    }

    public static OneTrackDebugger getInstance() {
        if (f845a == null) {
            synchronized (OneTrackDebugger.class) {
                try {
                    if (f845a == null) {
                        f845a = new OneTrackDebugger();
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return f845a;
    }

    public String getInstanceId() {
        return o.a().b();
    }

    public String getOaid(Context context) {
        return a.a().a(context.getApplicationContext());
    }

    public ConcurrentHashMap<Long, Configuration> getSdkConfig() {
        return this.f847c;
    }

    public void setSdkConfig(Configuration configuration) {
        this.f847c.put(Long.valueOf(System.currentTimeMillis()), configuration);
    }

    public void startDebugger() {
        try {
            com.xiaomi.onetrack.f.a.b().startService(new Intent(com.xiaomi.onetrack.f.a.b(), Class.forName(f846b)));
        } catch (Throwable th) {
            Log.d("startDebugger", th.getMessage());
        }
    }
}
