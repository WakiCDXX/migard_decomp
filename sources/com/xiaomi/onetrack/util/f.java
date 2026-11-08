package com.xiaomi.onetrack.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.miui.powerkeeper.powerchecker.NightStandbyRecord;

/* JADX INFO: loaded from: classes.dex */
class f extends BroadcastReceiver {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ d f1557a;

    f(d dVar) {
        this.f1557a = dVar;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        try {
            String stringExtra = intent.getStringExtra("host");
            String stringExtra2 = intent.getStringExtra("packagename");
            String stringExtra3 = intent.getStringExtra("projectId");
            String stringExtra4 = intent.getStringExtra(NightStandbyRecord.KEY_USER);
            boolean booleanExtra = intent.getBooleanExtra("logon", false);
            boolean booleanExtra2 = intent.getBooleanExtra("quickuploadon", false);
            String strE = com.xiaomi.onetrack.f.a.e();
            if (!TextUtils.isEmpty(stringExtra2) && !"".equals(stringExtra2) && strE.equals(stringExtra2)) {
                p.f1665a = booleanExtra;
                p.f1666b = booleanExtra2;
                if (booleanExtra2 && this.f1557a.a(stringExtra)) {
                    this.f1557a.a(stringExtra, stringExtra3, stringExtra4);
                }
            }
        } catch (Exception e2) {
            p.b(d.f1544a, e2.getMessage());
        }
    }
}
