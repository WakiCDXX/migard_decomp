package com.miui.powerkeeper.perfengine;

import android.content.Context;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* JADX INFO: loaded from: classes.dex */
public class PerfEngineManager {

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static volatile PerfEngineManager f474d;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private PerfEngineController f475a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private PeSchedManager f476b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private PeCloudManager f477c;

    private PerfEngineManager(Context context) {
        this.f475a = null;
        this.f476b = null;
        this.f477c = null;
        this.f477c = PeCloudManager.e(context);
        this.f475a = PerfEngineController.b(context);
        this.f476b = PeSchedManager.e(context);
    }

    public static void a() {
        f474d = null;
    }

    public static PerfEngineManager d() {
        return f474d;
    }

    public static PerfEngineManager e(Context context) {
        if (f474d == null) {
            synchronized (PerfEngineManager.class) {
                try {
                    if (f474d == null) {
                        f474d = new PerfEngineManager(context);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return f474d;
    }

    public void b(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        this.f476b.a(fileDescriptor, printWriter, strArr);
    }

    public String c() {
        PeSchedManager peSchedManager = this.f476b;
        return peSchedManager != null ? peSchedManager.d() : "";
    }

    public PeCloudManager f() {
        return this.f477c;
    }

    public PeSchedManager g() {
        return this.f476b;
    }
}
