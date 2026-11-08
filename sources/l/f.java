package l;

import com.miui.powerkeeper.utils.MultiScreenObserver;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* JADX INFO: compiled from: MultiWindowObserver.java */
/* JADX INFO: loaded from: classes.dex */
public class f extends b {

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private static f f2043f;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private boolean f2044d = false;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private a f2045e;

    /* JADX INFO: compiled from: MultiWindowObserver.java */
    private class a extends MultiScreenObserver {
        @Override // com.miui.powerkeeper.utils.MultiScreenObserver
        protected void onMultiScreenChanged(boolean z2) {
            f.this.l(z2);
        }

        private a() {
        }
    }

    private f() {
        a aVar = new a();
        this.f2045e = aVar;
        aVar.registerFreeformCallback();
        this.f2045e.registerTaskStackListener();
    }

    static synchronized l.a k() {
        try {
            if (f2043f == null) {
                f2043f = new f();
            }
        } catch (Throwable th) {
            throw th;
        }
        return f2043f;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void l(boolean z2) {
        if (this.f2044d != z2) {
            this.f2044d = z2;
            h(Boolean.valueOf(z2));
        }
    }

    @Override // l.l.a
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
    }
}
