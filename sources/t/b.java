package t;

import android.text.TextUtils;

/* JADX INFO: loaded from: classes.dex */
final class b implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ String f2342a;

    b(String str) {
        this.f2342a = str;
    }

    @Override // java.lang.Runnable
    public void run() {
        if (TextUtils.isEmpty(this.f2342a)) {
            return;
        }
        a.n(this.f2342a);
    }
}
