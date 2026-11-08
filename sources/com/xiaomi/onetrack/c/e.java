package com.xiaomi.onetrack.c;

import android.content.Intent;

/* JADX INFO: loaded from: classes.dex */
class e implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ Intent f1263a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ d f1264b;

    e(d dVar, Intent intent) {
        this.f1264b = dVar;
        this.f1263a = intent;
    }

    @Override // java.lang.Runnable
    public void run() {
        String action = this.f1263a.getAction();
        if (action.equals("android.intent.action.SCREEN_OFF") || action.equals("android.intent.action.SCREEN_ON")) {
            com.xiaomi.onetrack.util.p.a("EventManager", "screen on/off");
            t.a().a(action.equals("android.intent.action.SCREEN_ON") ? 0 : 2, false);
            com.xiaomi.onetrack.a.c.b.a().b();
        }
    }
}
