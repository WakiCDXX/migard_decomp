package y;

import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
final class j implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ String f2437a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ String f2438b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    final /* synthetic */ String f2439c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    final /* synthetic */ String f2440d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    final /* synthetic */ Map f2441e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    final /* synthetic */ boolean f2442f;

    j(String str, String str2, String str3, String str4, Map map, boolean z2) {
        this.f2437a = str;
        this.f2438b = str2;
        this.f2439c = str3;
        this.f2440d = str4;
        this.f2441e = map;
        this.f2442f = z2;
    }

    @Override // java.lang.Runnable
    public void run() {
        i.h(this.f2437a, this.f2438b, this.f2439c, this.f2440d, this.f2441e, this.f2442f);
        com.ot.pubsub.util.k.c("NetworkAccessManager", "save data successfully");
    }
}
