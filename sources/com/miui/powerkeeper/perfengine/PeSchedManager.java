package com.miui.powerkeeper.perfengine;

import android.content.ComponentName;
import android.content.Context;
import android.text.TextUtils;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* JADX INFO: loaded from: classes.dex */
public class PeSchedManager {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static PeGameController f460a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static PeSchedController f461b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static PeSchedManager f462c = new PeSchedManager();

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static PeThermalController f463d = null;

    private PeSchedManager() {
    }

    public static PeSchedManager e(Context context) {
        if (f460a == null) {
            f460a = new PeGameController(context);
        }
        if (f461b == null) {
            f461b = new PeSchedController(context);
        }
        if (f463d == null) {
            f463d = new PeThermalController(context);
        }
        return f462c;
    }

    public void a(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        b(fileDescriptor, printWriter, strArr);
    }

    public void b(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        f460a.u(fileDescriptor, printWriter, strArr);
    }

    public int c(String str) {
        PeGameController peGameController;
        if (TextUtils.isEmpty(str) || (peGameController = f460a) == null) {
            return -1;
        }
        return peGameController.y(str);
    }

    public String d() {
        PeGameController peGameController = f460a;
        return peGameController != null ? peGameController.z() : "";
    }

    public boolean f(String str) {
        PeGameController peGameController = f460a;
        if (peGameController != null) {
            return peGameController.I(str);
        }
        return false;
    }

    public void g(ComponentName componentName) {
        PeGameController peGameController;
        if (TextUtils.isEmpty(componentName.getPackageName()) || (peGameController = f460a) == null) {
            return;
        }
        peGameController.K(componentName);
    }

    public void h(ComponentName componentName, int i2, int i3) {
        PeGameController peGameController;
        if (TextUtils.isEmpty(componentName.getPackageName()) || (peGameController = f460a) == null) {
            return;
        }
        peGameController.L(componentName);
    }

    public void i(ComponentName componentName, int i2, int i3) {
        PeGameController peGameController;
        if (TextUtils.isEmpty(componentName.getPackageName()) || (peGameController = f460a) == null) {
            return;
        }
        peGameController.M(componentName);
    }

    public void j(int i2) {
        PeGameController peGameController = f460a;
        if (peGameController != null) {
            peGameController.N(i2);
        }
    }

    public void k(int i2) {
        f463d.h(i2);
    }
}
