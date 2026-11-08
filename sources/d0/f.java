package d0;

import android.text.TextUtils;

/* JADX INFO: loaded from: classes.dex */
class f implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ String f1772a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ e f1773b;

    f(e eVar, String str) {
        this.f1773b = eVar;
        this.f1772a = str;
    }

    @Override // java.lang.Runnable
    public void run() {
        if (TextUtils.equals(this.f1772a, "android.intent.action.SCREEN_OFF")) {
            this.f1773b.f1771a.t();
            d.a().d(false);
            d.a().b(2, false);
        } else if (TextUtils.equals(this.f1772a, "android.intent.action.SCREEN_ON")) {
            this.f1773b.f1771a.s();
            d.a().d(false);
            d.a().b(0, false);
        }
    }
}
