package h;

import android.os.SystemClock;

/* JADX INFO: compiled from: Clock.java */
/* JADX INFO: loaded from: classes.dex */
public abstract class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final a f1865a = new C0023a();

    /* JADX INFO: renamed from: h.a$a, reason: collision with other inner class name */
    /* JADX INFO: compiled from: Clock.java */
    class C0023a extends a {
        C0023a() {
        }

        @Override // h.a
        public long a() {
            return System.currentTimeMillis();
        }

        @Override // h.a
        public long b() {
            return SystemClock.elapsedRealtime();
        }

        @Override // h.a
        public long c() {
            return SystemClock.uptimeMillis();
        }
    }

    public abstract long a();

    public abstract long b();

    public abstract long c();
}
