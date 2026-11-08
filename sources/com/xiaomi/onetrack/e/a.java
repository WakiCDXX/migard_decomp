package com.xiaomi.onetrack.e;

import com.xiaomi.onetrack.b.h;
import com.xiaomi.onetrack.util.p;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class a extends com.xiaomi.onetrack.f.b {

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private static final String f1355d = "CustomEvent";

    public a(String str, String str2, String str3, String str4) {
        try {
            a(str);
            c(str3);
            b(str2);
            b(System.currentTimeMillis());
            b(new JSONObject(str4));
            a(h.a().a(str, str3, "level", 1));
        } catch (Exception e2) {
            p.b(f1355d, "CustomEvent error:" + e2.toString());
        }
    }
}
