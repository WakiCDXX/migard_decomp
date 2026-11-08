package x;

import java.util.Map;
import y.f;

/* JADX INFO: loaded from: classes.dex */
final class c implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ String f2394a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ String f2395b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    final /* synthetic */ String f2396c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    final /* synthetic */ String f2397d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    final /* synthetic */ String f2398e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    final /* synthetic */ String f2399f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    final /* synthetic */ Map f2400g;

    c(String str, String str2, String str3, String str4, String str5, String str6, Map map) {
        this.f2394a = str;
        this.f2395b = str2;
        this.f2396c = str3;
        this.f2397d = str4;
        this.f2398e = str5;
        this.f2399f = str6;
        this.f2400g = map;
    }

    @Override // java.lang.Runnable
    public void run() {
        f.e().f(this.f2398e, this.f2399f, a.a(this.f2394a, this.f2395b, this.f2396c, this.f2397d), this.f2400g);
    }
}
