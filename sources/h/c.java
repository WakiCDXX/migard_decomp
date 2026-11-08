package h;

import android.util.Log;
import com.miui.powerkeeper.utils.Constant;
import java.util.ArrayList;

/* JADX INFO: compiled from: TimerImpl.java */
/* JADX INFO: loaded from: classes.dex */
public class c extends f {

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    final ArrayList<c> f1866g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    int f1867h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    long f1868i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    long f1869j;

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    long f1870k;

    public c(a aVar, g gVar, int i2, ArrayList<c> arrayList, d dVar) {
        super(aVar, i2, dVar);
        this.f1869j = -1L;
        this.f1866g = arrayList;
    }

    private static long h(long j2, ArrayList<c> arrayList, c cVar) {
        int size = arrayList.size();
        long j3 = 0;
        for (int i2 = size - 1; i2 >= 0; i2--) {
            c cVar2 = arrayList.get(i2);
            long j4 = j2 - cVar2.f1868i;
            if (j4 > 0) {
                long j5 = j4 / ((long) size);
                if (cVar2 == cVar) {
                    j3 = j5;
                }
                cVar2.f1885e += j5;
            }
            cVar2.f1868i = j2;
        }
        return j3;
    }

    @Override // h.f
    protected int a() {
        return this.f1884d;
    }

    @Override // h.f
    protected long b(long j2, long j3) {
        long j4 = this.f1870k;
        long size = 0;
        if (j4 > 0) {
            long j5 = this.f1868i;
            if (j2 > j5 + j4) {
                j2 = j5 + j4;
            }
        }
        long j6 = this.f1885e;
        if (this.f1867h > 0) {
            long j7 = j2 - this.f1868i;
            ArrayList<c> arrayList = this.f1866g;
            size = j7 / ((long) (arrayList != null ? arrayList.size() : 1));
        }
        return j6 + size;
    }

    @Override // h.f
    public void c() {
        super.c();
        ArrayList<c> arrayList = this.f1866g;
        if (arrayList != null) {
            arrayList.remove(this);
        }
    }

    @Override // h.f
    public boolean f(boolean z2, long j2) {
        boolean z3 = false;
        boolean z4 = this.f1867h <= 0;
        if (z4 && z2) {
            z3 = true;
        }
        super.f(z3, j2);
        if (this.f1867h > 0) {
            this.f1868i = this.f1883c.c(j2);
        }
        this.f1869j = -1L;
        return z4;
    }

    public boolean g() {
        return this.f1867h > 0;
    }

    public void i(long j2) {
        int i2 = this.f1867h;
        this.f1867h = i2 + 1;
        if (i2 == 0) {
            long jC = this.f1883c.c(j2 * 1000);
            this.f1868i = jC;
            ArrayList<c> arrayList = this.f1866g;
            if (arrayList != null) {
                h(jC, arrayList, null);
                this.f1866g.add(this);
            }
            if (this.f1883c.g()) {
                this.f1884d++;
                this.f1869j = this.f1885e;
            } else {
                this.f1869j = -1L;
            }
            if (!Constant.DEBUG || this.f1882b >= 0) {
                return;
            }
            Log.v("Timer", "start #" + this.f1882b + ": mUpdateTime=" + this.f1868i + " mTotalTime=" + this.f1885e + " mCount=" + this.f1884d + " mAcquireTime=" + this.f1869j);
        }
    }

    public void j(long j2) {
        int i2 = this.f1867h;
        if (i2 == 0) {
            return;
        }
        int i3 = i2 - 1;
        this.f1867h = i3;
        if (i3 == 0) {
            long j3 = j2 * 1000;
            long jC = this.f1883c.c(j3);
            ArrayList<c> arrayList = this.f1866g;
            if (arrayList != null) {
                h(jC, arrayList, null);
                this.f1866g.remove(this);
            } else {
                this.f1867h = 1;
                this.f1885e = b(jC, j3);
                this.f1867h = 0;
            }
            if (Constant.DEBUG && this.f1882b < 0) {
                Log.v("Timer", "stop #" + this.f1882b + ": mUpdateTime=" + this.f1868i + " mTotalTime=" + this.f1885e + " mCount=" + this.f1884d + " mAcquireTime=" + this.f1869j);
            }
            long j4 = this.f1869j;
            if (j4 < 0 || this.f1885e != j4) {
                return;
            }
            this.f1884d--;
        }
    }

    @Override // h.f, h.e
    public void onTimeStopped(long j2, long j3, long j4) {
        if (this.f1867h > 0) {
            boolean z2 = Constant.DEBUG;
            if (z2 && this.f1882b < 0) {
                Log.v("Timer", "old mUpdateTime=" + this.f1868i);
            }
            super.onTimeStopped(j2, j3, j4);
            this.f1868i = j4;
            if (!z2 || this.f1882b >= 0) {
                return;
            }
            Log.v("Timer", "new mUpdateTime=" + this.f1868i);
        }
    }
}
