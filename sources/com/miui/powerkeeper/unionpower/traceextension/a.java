package com.miui.powerkeeper.unionpower.traceextension;

import android.content.Context;
import android.util.Log;
import com.xiaomi.mipush.sdk.Constants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import n.c;

/* JADX INFO: compiled from: TraceController.java */
/* JADX INFO: loaded from: classes.dex */
public class a {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final String f652c = "com.miui.powerkeeper.unionpower.traceextension.a";

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private List<n.a> f653a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private Map<String, Runnable> f654b;

    /* JADX INFO: renamed from: com.miui.powerkeeper.unionpower.traceextension.a$a, reason: collision with other inner class name */
    /* JADX INFO: compiled from: TraceController.java */
    private static class C0003a {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private static final a f655a = new a();
    }

    public static a d() {
        return C0003a.f655a;
    }

    public void a() {
        Iterator<n.a> it = this.f653a.iterator();
        while (it.hasNext()) {
            it.next().b();
        }
    }

    public void b() {
        Iterator<n.a> it = this.f653a.iterator();
        while (it.hasNext()) {
            it.next().a();
        }
    }

    void c(String str, int i2) {
        String str2 = str + Constants.ACCEPT_TIME_SEPARATOR_SERVER + i2;
        Runnable runnable = this.f654b.get(str2);
        if (runnable != null) {
            String str3 = f652c;
            Log.d(str3, str2 + "is running");
            TraceExtensionService.z(str3, str2);
            runnable.run();
        }
    }

    public void e(Context context) {
        Iterator<n.a> it = this.f653a.iterator();
        while (it.hasNext()) {
            it.next().c(context);
        }
    }

    void f(String str, int i2, Runnable runnable) {
        if (this.f654b == null) {
            Log.d(f652c, "function map initialization failed");
            return;
        }
        this.f654b.put(str + Constants.ACCEPT_TIME_SEPARATOR_SERVER + i2, runnable);
    }

    void g(String str, int i2) {
        if (this.f654b == null) {
            Log.d(f652c, "function map initialization failed");
            return;
        }
        this.f654b.remove(str + Constants.ACCEPT_TIME_SEPARATOR_SERVER + i2);
    }

    private a() {
        this.f654b = new HashMap();
        ArrayList arrayList = new ArrayList();
        this.f653a = arrayList;
        arrayList.add(new c());
    }
}
