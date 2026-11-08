package org.apache.thrift.transport;

/* JADX INFO: compiled from: TMemoryBuffer.java */
/* JADX INFO: loaded from: classes.dex */
public class b extends d {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private g0.b f2164a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private int f2165b;

    public b(int i2) {
        this.f2164a = new g0.b(i2);
    }

    @Override // org.apache.thrift.transport.d
    public int e(byte[] bArr, int i2, int i3) {
        byte[] bArrA = this.f2164a.a();
        if (i3 > this.f2164a.b() - this.f2165b) {
            i3 = this.f2164a.b() - this.f2165b;
        }
        if (i3 > 0) {
            System.arraycopy(bArrA, this.f2165b, bArr, i2, i3);
            this.f2165b += i3;
        }
        return i3;
    }

    @Override // org.apache.thrift.transport.d
    public void g(byte[] bArr, int i2, int i3) {
        this.f2164a.write(bArr, i2, i3);
    }

    public int h() {
        return this.f2164a.size();
    }
}
