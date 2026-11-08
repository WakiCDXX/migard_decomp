package h;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/* JADX INFO: compiled from: TimerImpl.java */
/* JADX INFO: loaded from: classes.dex */
public class d {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    protected final Collection<e> f1871a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    protected long f1872b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    protected long f1873c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    protected boolean f1874d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    protected long f1875e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    protected long f1876f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    protected long f1877g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    protected long f1878h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    protected long f1879i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    protected long f1880j;

    public d(boolean z2) {
        this.f1871a = z2 ? new HashSet<>() : new ArrayList<>();
    }

    public void a(e eVar) {
        this.f1871a.add(eVar);
    }

    public long b(long j2, int i2) {
        return this.f1872b + e(j2);
    }

    public long c(long j2) {
        long j3 = this.f1877g;
        return this.f1874d ? j3 + (j2 - this.f1878h) : j3;
    }

    public long d() {
        return this.f1878h;
    }

    public long e(long j2) {
        long j3 = this.f1875e;
        return this.f1874d ? j3 + (j2 - this.f1876f) : j3;
    }

    public void f(long j2, long j3) {
        this.f1873c = 0L;
        this.f1872b = 0L;
        this.f1875e = 0L;
        this.f1877g = 0L;
        this.f1876f = j2;
        this.f1878h = j3;
        this.f1879i = e(j2);
        this.f1880j = c(this.f1878h);
    }

    public boolean g() {
        return this.f1874d;
    }

    public void h(e eVar) {
        this.f1871a.remove(eVar);
    }

    public boolean i(boolean z2, long j2, long j3) {
        if (this.f1874d == z2) {
            return false;
        }
        this.f1874d = z2;
        if (!z2) {
            this.f1875e += j2 - this.f1876f;
            this.f1877g += j3 - this.f1878h;
            long jE = e(j2);
            long jC = c(j3);
            Iterator<e> it = this.f1871a.iterator();
            while (it.hasNext()) {
                it.next().onTimeStopped(j3, jE, jC);
            }
            return true;
        }
        this.f1876f = j2;
        this.f1878h = j3;
        long jE2 = e(j2);
        this.f1879i = jE2;
        long jC2 = c(j3);
        this.f1880j = jC2;
        Iterator<e> it2 = this.f1871a.iterator();
        while (it2.hasNext()) {
            it2.next().onTimeStarted(j3, jE2, jC2);
        }
        return true;
    }

    public d() {
        this(false);
    }
}
