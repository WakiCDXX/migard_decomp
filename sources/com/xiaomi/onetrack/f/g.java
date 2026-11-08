package com.xiaomi.onetrack.f;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

/* JADX INFO: loaded from: classes.dex */
public class g {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final String f1416a = "OneTrackApp";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static g f1417b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static BroadcastReceiver f1418c = new i();

    private g(Context context) {
        com.xiaomi.onetrack.util.i.a(new h(this, context.getApplicationContext()));
    }

    public static void a(Context context) {
        if (f1417b == null) {
            f1417b = new g(context);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void c(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        context.registerReceiver(f1418c, intentFilter);
    }
}
