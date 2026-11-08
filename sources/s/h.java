package s;

import android.text.TextUtils;

/* JADX INFO: loaded from: classes.dex */
class h implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ String f2332a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ g f2333b;

    h(g gVar, String str) {
        this.f2333b = gVar;
        this.f2332a = str;
    }

    @Override // java.lang.Runnable
    public void run() {
        if (TextUtils.equals(this.f2332a, "android.intent.action.SCREEN_OFF") || TextUtils.equals(this.f2332a, "android.intent.action.SCREEN_ON")) {
            this.f2333b.f2331a.m();
        }
    }
}
