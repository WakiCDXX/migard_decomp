package com.xiaomi.onetrack.b;

import com.xiaomi.onetrack.util.p;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
class i implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ ArrayList f1208a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ h f1209b;

    i(h hVar, ArrayList arrayList) {
        this.f1209b = hVar;
        this.f1208a = arrayList;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        if (p.f1665a) {
            p.a("ConfigDbManager", "update: " + this.f1208a);
        }
        this.f1209b.b((ArrayList<l>) this.f1208a);
    }
}
