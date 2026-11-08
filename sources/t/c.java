package t;

import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
class c implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ String f2343a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    final /* synthetic */ a.HandlerC0027a f2344b;

    c(a.HandlerC0027a handlerC0027a, String str) {
        this.f2344b = handlerC0027a;
        this.f2343a = str;
    }

    @Override // java.lang.Runnable
    public void run() {
        a.o(Arrays.asList(this.f2343a));
        a.f2341d.put(this.f2343a, Boolean.FALSE);
    }
}
