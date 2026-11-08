package org.apache.thrift.transport;

/* JADX INFO: compiled from: TMemoryInputTransport.java */
/* JADX INFO: loaded from: classes.dex */
public final class c extends d {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private byte[] f2166a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private int f2167b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private int f2168c;

    @Override // org.apache.thrift.transport.d
    public void a(int i2) {
        this.f2167b += i2;
    }

    @Override // org.apache.thrift.transport.d
    public byte[] b() {
        return this.f2166a;
    }

    @Override // org.apache.thrift.transport.d
    public int c() {
        return this.f2167b;
    }

    @Override // org.apache.thrift.transport.d
    public int d() {
        return this.f2168c - this.f2167b;
    }

    @Override // org.apache.thrift.transport.d
    public int e(byte[] bArr, int i2, int i3) throws TTransportException {
        int iD = d();
        if (i3 > iD) {
            i3 = iD;
        }
        if (i3 > 0) {
            System.arraycopy(this.f2166a, this.f2167b, bArr, i2, i3);
            a(i3);
        }
        return i3;
    }

    @Override // org.apache.thrift.transport.d
    public void g(byte[] bArr, int i2, int i3) throws TTransportException {
        throw new UnsupportedOperationException("No writing allowed!");
    }

    public void h(byte[] bArr) {
        i(bArr, 0, bArr.length);
    }

    public void i(byte[] bArr, int i2, int i3) {
        this.f2166a = bArr;
        this.f2167b = i2;
        this.f2168c = i2 + i3;
    }
}
