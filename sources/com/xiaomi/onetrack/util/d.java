package com.xiaomi.onetrack.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class d {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final String f1544a = "d";

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static final String f1545b = "com.xiaomi.onetrack.DEBUG";

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static final String f1546c = "com.xiaomi.onetrack.permissions.DEBUG_MODE";

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static final String f1547d = "/api/open/device/writeBack";

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static final String f1548e = "http://";

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static final String f1549f = "https://";

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private static final String f1550g = ".mi.com";

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private static volatile d f1551h = null;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private static final int f1552i = 100;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private final Context f1553j;

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private Handler f1554k = new e(this, Looper.getMainLooper());

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    private BroadcastReceiver f1555l = new f(this);

    private d() {
        Context contextB = com.xiaomi.onetrack.f.a.b();
        this.f1553j = contextB;
        a(contextB);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void c(String str) {
        Toast.makeText(this.f1553j, str, 1).show();
    }

    public static d a() {
        if (f1551h == null) {
            synchronized (d.class) {
                try {
                    if (f1551h == null) {
                        f1551h = new d();
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return f1551h;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void b(String str) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            int iOptInt = jSONObject.optInt("code");
            String strOptString = jSONObject.optString("message");
            String strOptString2 = jSONObject.optString(com.xiaomi.onetrack.api.d.L);
            boolean zOptBoolean = jSONObject.optBoolean("success");
            Message messageObtain = Message.obtain();
            messageObtain.what = 100;
            Bundle bundle = new Bundle();
            if (iOptInt == 0 && zOptBoolean) {
                bundle.putString("hint", strOptString2);
            } else {
                bundle.putString("hint", strOptString);
            }
            messageObtain.setData(bundle);
            this.f1554k.sendMessage(messageObtain);
        } catch (JSONException e2) {
            p.b(f1544a, e2.getMessage());
        }
    }

    private void a(Context context) {
        if (context == null) {
            return;
        }
        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(f1545b);
            context.registerReceiver(this.f1555l, intentFilter, f1546c, null);
        } catch (Exception e2) {
            p.a(f1544a, "registerDebugModeReceiver: " + e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean a(String str) {
        return !TextUtils.isEmpty(str) && str.endsWith(f1550g);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(String str, String str2, String str3) {
        i.a(new g(this, str, str2, str3));
    }
}
