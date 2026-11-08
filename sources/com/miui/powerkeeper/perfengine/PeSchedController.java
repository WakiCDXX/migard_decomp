package com.miui.powerkeeper.perfengine;

import android.content.Context;
import android.util.Slog;

/* JADX INFO: loaded from: classes.dex */
public class PeSchedController extends SchedController {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private Context f451a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private SchedConfig f452b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private volatile boolean f453c = false;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private volatile int f454d = -1;

    public PeSchedController(Context context) {
        this.f451a = null;
        this.f452b = null;
        this.f451a = context;
        SchedConfig schedConfig = new SchedConfig(c());
        this.f452b = schedConfig;
        try {
            schedConfig.e(this.f451a.getAssets().open("default_perf_config"));
        } catch (Exception e2) {
            Slog.e("PeSchedController", "parse default config error: " + e2.toString());
        }
        d();
        PeCloudManager.e(this.f451a).g(this);
    }

    private void d() {
        e(-1);
    }

    private void e(int i2) {
        this.f454d = i2;
    }

    @Override // com.miui.powerkeeper.perfengine.SchedController, com.miui.powerkeeper.perfengine.b
    public boolean b(String str) {
        return true;
    }
}
