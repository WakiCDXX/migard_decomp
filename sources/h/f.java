package h;

import android.util.Log;
import com.miui.powerkeeper.utils.Constant;

/* JADX INFO: compiled from: TimerImpl.java */
/* JADX INFO: loaded from: classes.dex */
public abstract class f extends b implements e {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    protected final a f1881a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    protected final int f1882b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    protected final d f1883c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    protected int f1884d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    protected long f1885e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    protected long f1886f;

    public f(a aVar, int i2, d dVar) {
        this.f1881a = aVar;
        this.f1882b = i2;
        this.f1883c = dVar;
        dVar.a(this);
    }

    protected abstract int a();

    protected abstract long b(long j2, long j3);

    public void c() {
        this.f1883c.h(this);
    }

    public int d(int i2) {
        return a();
    }

    public long e(long j2, int i2) {
        return b(this.f1883c.c(j2), j2);
    }

    public boolean f(boolean z2, long j2) {
        this.f1886f = 0L;
        this.f1885e = 0L;
        this.f1884d = 0;
        if (!z2) {
            return true;
        }
        c();
        return true;
    }

    public void onTimeStopped(long j2, long j3, long j4) {
        boolean z2 = Constant.DEBUG;
        if (z2 && this.f1882b < 0) {
            Log.v("Timer", "plug #" + this.f1882b + ": realtime=" + j4 + " old mTotalTime=" + this.f1885e);
        }
        this.f1885e = b(j4, j2);
        this.f1884d = a();
        if (!z2 || this.f1882b >= 0) {
            return;
        }
        Log.v("Timer", "plug #" + this.f1882b + ": new mTotalTime=" + this.f1885e);
    }

    @Override // h.e
    public void onTimeStarted(long j2, long j3, long j4) {
    }
}
