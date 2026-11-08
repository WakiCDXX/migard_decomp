package com.xiaomi.onetrack.a.c;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/* JADX INFO: loaded from: classes.dex */
class c extends BroadcastReceiver {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ b f939a;

    c(b bVar) {
        this.f939a = bVar;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (this.f939a.f937d != null) {
            this.f939a.f937d.sendEmptyMessage(10);
        }
    }
}
