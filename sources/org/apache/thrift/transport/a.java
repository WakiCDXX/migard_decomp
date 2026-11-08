package org.apache.thrift.transport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* JADX INFO: compiled from: TIOStreamTransport.java */
/* JADX INFO: loaded from: classes.dex */
public class a extends d {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    protected InputStream f2162a = null;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    protected OutputStream f2163b;

    public a(OutputStream outputStream) {
        this.f2163b = outputStream;
    }

    @Override // org.apache.thrift.transport.d
    public int e(byte[] bArr, int i2, int i3) throws TTransportException {
        InputStream inputStream = this.f2162a;
        if (inputStream == null) {
            throw new TTransportException(1, "Cannot read from null inputStream");
        }
        try {
            int i4 = inputStream.read(bArr, i2, i3);
            if (i4 >= 0) {
                return i4;
            }
            throw new TTransportException(4);
        } catch (IOException e2) {
            throw new TTransportException(0, e2);
        }
    }

    @Override // org.apache.thrift.transport.d
    public void g(byte[] bArr, int i2, int i3) throws TTransportException {
        OutputStream outputStream = this.f2163b;
        if (outputStream == null) {
            throw new TTransportException(1, "Cannot write to null outputStream");
        }
        try {
            outputStream.write(bArr, i2, i3);
        } catch (IOException e2) {
            throw new TTransportException(0, e2);
        }
    }
}
