package com.ot.pubsub.util.oaid.helpers;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import com.ot.pubsub.util.k;
import com.ot.pubsub.util.u;
import f0.h;
import f0.i;
import f0.j;
import f0.m;
import f0.n;
import f0.o;
import f0.p;
import f0.q;
import f0.r;

/* JADX INFO: loaded from: classes.dex */
public class b {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    static String f745a = "b";

    enum a {
        asus("ASUS"),
        huawei("HUAWEI"),
        lenovo("LENOVO"),
        motolora("MOTOLORA"),
        meizu("MEIZU"),
        oppo("OPPO"),
        samsung("SAMSUNG"),
        numbia("NUBIA"),
        vivo("VIVO"),
        xiaomi("XIAOMI"),
        redmi("REDMI"),
        blackshark("BLACKSHARK"),
        oneplus("ONEPLUS"),
        zte("ZTE"),
        freemeos("FERRMEOS"),
        ssui("SSUI");


        /* JADX INFO: renamed from: q, reason: collision with root package name */
        public final String f763q;

        a(String str) {
            this.f763q = str;
        }

        static a c(String str) {
            for (a aVar : values()) {
                if (aVar.f763q.equals(str)) {
                    return aVar;
                }
            }
            return null;
        }
    }

    private static String e() {
        return Build.MANUFACTURER.toUpperCase();
    }

    public String a(Context context) {
        try {
            return b(context, e());
        } catch (Exception e2) {
            k.c(f745a, e2.getMessage());
            return "";
        }
    }

    String b(Context context, String str) throws Exception {
        a aVarC = a.c(str);
        if (c()) {
            aVarC = a.freemeos;
        }
        if (d()) {
            aVarC = a.ssui;
        }
        if (aVarC == null) {
            throw new Exception(String.format("undefined oaid method of manufacturer %s", str));
        }
        switch (com.ot.pubsub.util.oaid.helpers.a.f744a[aVarC.ordinal()]) {
            case 1:
                return new h().a(context);
            case 2:
                return new i().a(context);
            case 3:
            case 4:
                return new j().a(context);
            case 5:
                return new f0.k().a(context);
            case 6:
                return new o().a(context);
            case 7:
                return new p().a(context);
            case 8:
                return new m().a(context);
            case 9:
                return new q().a(context);
            case 10:
            case 11:
            case 12:
                return com.ot.pubsub.util.j.b(context);
            case 13:
                return new n().a(context);
            case 14:
            case 15:
            case 16:
                return new r().a(context);
            default:
                return "";
        }
    }

    public boolean c() {
        String strA = u.a("ro.build.freeme.label");
        return !TextUtils.isEmpty(strA) && strA.equalsIgnoreCase("FREEMEOS");
    }

    public boolean d() {
        String strA = u.a("ro.ssui.product");
        return (TextUtils.isEmpty(strA) || strA.equalsIgnoreCase("unknown")) ? false : true;
    }
}
