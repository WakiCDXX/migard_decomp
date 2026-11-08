package com.xiaomi.onetrack.api;

import com.xiaomi.onetrack.ServiceQualityEvent;

/* JADX INFO: loaded from: classes.dex */
class y implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ ServiceQualityEvent f1152a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ j f1153b;

    y(j jVar, ServiceQualityEvent serviceQualityEvent) {
        this.f1153b = jVar;
        this.f1152a = serviceQualityEvent;
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            this.f1153b.f1109b.a(d.f1048e, e.a(this.f1152a, this.f1153b.f1112f, this.f1153b.f1114h, this.f1153b.f1115i, this.f1153b.f1116j));
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "trackNetAvailableEvent error: " + e2.toString());
        }
    }
}
