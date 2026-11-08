package com.xiaomi.onetrack.b;

import android.text.TextUtils;

/* JADX INFO: loaded from: classes.dex */
final class b implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ String f1181a;

    b(String str) {
        this.f1181a = str;
    }

    @Override // java.lang.Runnable
    public void run() {
        if (TextUtils.isEmpty(this.f1181a)) {
            return;
        }
        a.b(this.f1181a);
    }
}
