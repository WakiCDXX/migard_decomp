package com.ot.pubsub;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.ot.pubsub.util.e;
import com.ot.pubsub.util.k;
import java.util.HashMap;
import java.util.Map;
import r.b;
import s.c;
import t.l;

/* JADX INFO: loaded from: classes.dex */
public class PubSubTrack {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static volatile boolean f678c = false;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private b f679a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private c f680b;

    public enum NetType {
        NOT_CONNECTED("NONE"),
        MOBILE_2G("2G"),
        MOBILE_3G("3G"),
        MOBILE_4G("4G"),
        MOBILE_5G("5G"),
        WIFI("WIFI"),
        ETHERNET("ETHERNET"),
        UNKNOWN("UNKNOWN");


        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private String f690a;

        NetType(String str) {
            this.f690a = str;
        }

        @Override // java.lang.Enum
        public String toString() {
            return this.f690a;
        }
    }

    public interface a {
        boolean isRecommendEvent(String str);
    }

    private PubSubTrack(Context context, b bVar) {
        this.f679a = bVar;
        l.b().d(bVar);
        com.ot.pubsub.util.b.b(context.getApplicationContext());
        this.f680b = new c(context, bVar);
    }

    private static void a(Context context) {
        if (context == null) {
            throw new IllegalStateException("context is null!");
        }
        com.ot.pubsub.util.b.b(context.getApplicationContext());
    }

    public static PubSubTrack b(Context context, b bVar) {
        if (bVar == null || TextUtils.isEmpty(bVar.f()) || TextUtils.isEmpty(bVar.e())) {
            if (k.f724a) {
                throw new IllegalStateException("PubSubAnalytics init error Configuration , ProjectId , PrivateKeyId can't null !!!");
            }
            Log.e("PubSubAnalytics", "PubSubAnalytics init error ,Configuration , ProjectId , PrivateKeyId can't null !!!");
        }
        return new PubSubTrack(context, bVar);
    }

    public static boolean c() {
        return f678c;
    }

    public static void d(Context context, boolean z2) {
        a(context);
        e.a(new com.ot.pubsub.a(z2));
    }

    public static void e(boolean z2) {
        k.f(z2);
    }

    public static void f(boolean z2) {
        f678c = z2;
    }

    public void g(String str, String str2, Map<String, Object> map) {
        h(str, str2, map, new HashMap());
    }

    public void h(String str, String str2, Map<String, Object> map, Map<String, String> map2) {
        b bVar = this.f679a;
        if (bVar == null || this.f680b == null || TextUtils.isEmpty(bVar.b()) || TextUtils.isEmpty(this.f679a.f()) || TextUtils.isEmpty(this.f679a.e()) || TextUtils.isEmpty(str) || c()) {
            k.c("PubSubAnalytics", "The Configuration or ProjectId or PrivateKeyId or AppId or Topic or data is empty, skip it!");
            return;
        }
        if (map == null) {
            map = new HashMap<>();
        }
        Map<String, Object> map3 = map;
        if (map2 == null) {
            map2 = new HashMap<>();
        }
        this.f680b.c(this.f679a.f(), str, str2, map3, map2);
    }
}
