package com.xiaomi.onetrack.b;

import java.util.Arrays;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
class d implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ String f1183a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ a.HandlerC0011a f1184b;

    d(a.HandlerC0011a handlerC0011a, String str) {
        this.f1184b = handlerC0011a;
        this.f1183a = str;
    }

    @Override // java.lang.Runnable
    public void run() {
        a.b((List<String>) Arrays.asList(this.f1183a));
        a.A.put(this.f1183a, Boolean.FALSE);
    }
}
