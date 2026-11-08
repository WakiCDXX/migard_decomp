package org.apache.thrift.protocol;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import org.apache.thrift.TException;

/* JADX INFO: loaded from: classes.dex */
public class XmPushTBinaryProtocol extends TBinaryProtocol {

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    private static int f2144o = 10000;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    private static int f2145p = 10000;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    private static int f2146q = 10000;

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    private static int f2147r = 10485760;

    /* JADX INFO: renamed from: s, reason: collision with root package name */
    private static int f2148s = 104857600;

    public static class Factory extends TBinaryProtocol.Factory {
        public Factory() {
            super(false, true);
        }

        @Override // org.apache.thrift.protocol.TBinaryProtocol.Factory, org.apache.thrift.protocol.TProtocolFactory
        public d a(org.apache.thrift.transport.d dVar) {
            XmPushTBinaryProtocol xmPushTBinaryProtocol = new XmPushTBinaryProtocol(dVar, this.strictRead_, this.strictWrite_);
            int i2 = this.readLength_;
            if (i2 != 0) {
                xmPushTBinaryProtocol.O(i2);
            }
            return xmPushTBinaryProtocol;
        }

        public Factory(boolean z2, boolean z3, int i2) {
            super(z2, z3, i2);
        }
    }

    public XmPushTBinaryProtocol(org.apache.thrift.transport.d dVar, boolean z2, boolean z3) {
        super(dVar, z2, z3);
    }

    @Override // org.apache.thrift.protocol.TBinaryProtocol, org.apache.thrift.protocol.d
    public ByteBuffer a() throws TException {
        int iH = h();
        if (iH > f2148s) {
            throw new TProtocolException(3, "Thrift binary size " + iH + " out of range!");
        }
        L(iH);
        if (this.f2157a.d() >= iH) {
            ByteBuffer byteBufferWrap = ByteBuffer.wrap(this.f2157a.b(), this.f2157a.c(), iH);
            this.f2157a.a(iH);
            return byteBufferWrap;
        }
        byte[] bArr = new byte[iH];
        this.f2157a.f(bArr, 0, iH);
        return ByteBuffer.wrap(bArr);
    }

    @Override // org.apache.thrift.protocol.TBinaryProtocol, org.apache.thrift.protocol.d
    public b j() throws TException {
        byte bC = c();
        int iH = h();
        if (iH <= f2145p) {
            return new b(bC, iH);
        }
        throw new TProtocolException(3, "Thrift list size " + iH + " out of range!");
    }

    @Override // org.apache.thrift.protocol.TBinaryProtocol, org.apache.thrift.protocol.d
    public c l() throws TException {
        byte bC = c();
        byte bC2 = c();
        int iH = h();
        if (iH <= f2144o) {
            return new c(bC, bC2, iH);
        }
        throw new TProtocolException(3, "Thrift map size " + iH + " out of range!");
    }

    @Override // org.apache.thrift.protocol.TBinaryProtocol, org.apache.thrift.protocol.d
    public f n() throws TException {
        byte bC = c();
        int iH = h();
        if (iH <= f2146q) {
            return new f(bC, iH);
        }
        throw new TProtocolException(3, "Thrift set size " + iH + " out of range!");
    }

    @Override // org.apache.thrift.protocol.TBinaryProtocol, org.apache.thrift.protocol.d
    public String p() throws TException {
        int iH = h();
        if (iH > f2147r) {
            throw new TProtocolException(3, "Thrift string size " + iH + " out of range!");
        }
        if (this.f2157a.d() < iH) {
            return N(iH);
        }
        try {
            String str = new String(this.f2157a.b(), this.f2157a.c(), iH, "UTF-8");
            this.f2157a.a(iH);
            return str;
        } catch (UnsupportedEncodingException unused) {
            throw new TException("JVM DOES NOT SUPPORT UTF-8");
        }
    }
}
