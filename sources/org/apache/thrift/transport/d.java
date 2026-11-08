package org.apache.thrift.transport;

/* JADX INFO: compiled from: TTransport.java */
/* JADX INFO: loaded from: classes.dex */
public abstract class d {
    public byte[] b() {
        return null;
    }

    public int c() {
        return 0;
    }

    public int d() {
        return -1;
    }

    public abstract int e(byte[] bArr, int i2, int i3) throws TTransportException;

    public int f(byte[] bArr, int i2, int i3) throws TTransportException {
        int i4 = 0;
        while (i4 < i3) {
            int iE = e(bArr, i2 + i4, i3 - i4);
            if (iE <= 0) {
                throw new TTransportException("Cannot read. Remote side has closed. Tried to read " + i3 + " bytes, but only got " + i4 + " bytes.");
            }
            i4 += iE;
        }
        return i4;
    }

    public abstract void g(byte[] bArr, int i2, int i3) throws TTransportException;

    public void a(int i2) {
    }
}
