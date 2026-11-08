package org.apache.thrift.protocol;

import com.miui.powerkeeper.powerchecker.BatteryStatsHelper;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import org.apache.thrift.TException;

/* JADX INFO: loaded from: classes.dex */
public class TBinaryProtocol extends d {

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    private static final g f2131n = new g();

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    protected boolean f2132b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    protected boolean f2133c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    protected int f2134d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    protected boolean f2135e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private byte[] f2136f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private byte[] f2137g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private byte[] f2138h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private byte[] f2139i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private byte[] f2140j;

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private byte[] f2141k;

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    private byte[] f2142l;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    private byte[] f2143m;

    public static class Factory implements TProtocolFactory {
        protected int readLength_;
        protected boolean strictRead_;
        protected boolean strictWrite_;

        public Factory() {
            this(false, true);
        }

        @Override // org.apache.thrift.protocol.TProtocolFactory
        public d a(org.apache.thrift.transport.d dVar) {
            TBinaryProtocol tBinaryProtocol = new TBinaryProtocol(dVar, this.strictRead_, this.strictWrite_);
            int i2 = this.readLength_;
            if (i2 != 0) {
                tBinaryProtocol.O(i2);
            }
            return tBinaryProtocol;
        }

        public Factory(boolean z2, boolean z3) {
            this(z2, z3, 0);
        }

        public Factory(boolean z2, boolean z3, int i2) {
            this.strictRead_ = z2;
            this.strictWrite_ = z3;
            this.readLength_ = i2;
        }
    }

    public TBinaryProtocol(org.apache.thrift.transport.d dVar, boolean z2, boolean z3) {
        super(dVar);
        this.f2135e = false;
        this.f2136f = new byte[1];
        this.f2137g = new byte[2];
        this.f2138h = new byte[4];
        this.f2139i = new byte[8];
        this.f2140j = new byte[1];
        this.f2141k = new byte[2];
        this.f2142l = new byte[4];
        this.f2143m = new byte[8];
        this.f2132b = z2;
        this.f2133c = z3;
    }

    private int M(byte[] bArr, int i2, int i3) throws TException {
        L(i3);
        return this.f2157a.f(bArr, i2, i3);
    }

    @Override // org.apache.thrift.protocol.d
    public void A(int i2) throws TException {
        byte[] bArr = this.f2138h;
        bArr[0] = (byte) ((i2 >> 24) & 255);
        bArr[1] = (byte) ((i2 >> 16) & 255);
        bArr[2] = (byte) ((i2 >> 8) & 255);
        bArr[3] = (byte) (i2 & 255);
        this.f2157a.g(bArr, 0, 4);
    }

    @Override // org.apache.thrift.protocol.d
    public void B(long j2) throws TException {
        byte[] bArr = this.f2139i;
        bArr[0] = (byte) ((j2 >> 56) & 255);
        bArr[1] = (byte) ((j2 >> 48) & 255);
        bArr[2] = (byte) ((j2 >> 40) & 255);
        bArr[3] = (byte) ((j2 >> 32) & 255);
        bArr[4] = (byte) ((j2 >> 24) & 255);
        bArr[5] = (byte) ((j2 >> 16) & 255);
        bArr[6] = (byte) ((j2 >> 8) & 255);
        bArr[7] = (byte) (j2 & 255);
        this.f2157a.g(bArr, 0, 8);
    }

    @Override // org.apache.thrift.protocol.d
    public void C(b bVar) throws TException {
        v(bVar.f2152a);
        A(bVar.f2153b);
    }

    @Override // org.apache.thrift.protocol.d
    public void E(c cVar) throws TException {
        v(cVar.f2154a);
        v(cVar.f2155b);
        A(cVar.f2156c);
    }

    @Override // org.apache.thrift.protocol.d
    public void G(f fVar) throws TException {
        v(fVar.f2159a);
        A(fVar.f2160b);
    }

    @Override // org.apache.thrift.protocol.d
    public void I(String str) throws TException {
        try {
            byte[] bytes = str.getBytes("UTF-8");
            A(bytes.length);
            this.f2157a.g(bytes, 0, bytes.length);
        } catch (UnsupportedEncodingException unused) {
            throw new TException("JVM DOES NOT SUPPORT UTF-8");
        }
    }

    protected void L(int i2) throws TException {
        if (i2 < 0) {
            throw new TException("Negative length: " + i2);
        }
        if (this.f2135e) {
            int i3 = this.f2134d - i2;
            this.f2134d = i3;
            if (i3 >= 0) {
                return;
            }
            throw new TException("Message length exceeded: " + i2);
        }
    }

    public String N(int i2) throws TException {
        try {
            L(i2);
            byte[] bArr = new byte[i2];
            this.f2157a.f(bArr, 0, i2);
            return new String(bArr, "UTF-8");
        } catch (UnsupportedEncodingException unused) {
            throw new TException("JVM DOES NOT SUPPORT UTF-8");
        }
    }

    public void O(int i2) {
        this.f2134d = i2;
        this.f2135e = true;
    }

    @Override // org.apache.thrift.protocol.d
    public ByteBuffer a() throws TException {
        int iH = h();
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

    @Override // org.apache.thrift.protocol.d
    public boolean b() throws TException {
        return c() == 1;
    }

    @Override // org.apache.thrift.protocol.d
    public byte c() throws TException {
        if (this.f2157a.d() < 1) {
            M(this.f2140j, 0, 1);
            return this.f2140j[0];
        }
        byte b2 = this.f2157a.b()[this.f2157a.c()];
        this.f2157a.a(1);
        return b2;
    }

    @Override // org.apache.thrift.protocol.d
    public double d() throws TException {
        return Double.longBitsToDouble(i());
    }

    @Override // org.apache.thrift.protocol.d
    public a e() throws TException {
        byte bC = c();
        return new a("", bC, bC == 0 ? (short) 0 : g());
    }

    @Override // org.apache.thrift.protocol.d
    public short g() throws TException {
        int iC;
        byte[] bArrB = this.f2141k;
        if (this.f2157a.d() >= 2) {
            bArrB = this.f2157a.b();
            iC = this.f2157a.c();
            this.f2157a.a(2);
        } else {
            M(this.f2141k, 0, 2);
            iC = 0;
        }
        return (short) (((bArrB[iC] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL) << 8) | (bArrB[iC + 1] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL));
    }

    @Override // org.apache.thrift.protocol.d
    public int h() throws TException {
        int iC;
        byte[] bArrB = this.f2142l;
        if (this.f2157a.d() >= 4) {
            bArrB = this.f2157a.b();
            iC = this.f2157a.c();
            this.f2157a.a(4);
        } else {
            M(this.f2142l, 0, 4);
            iC = 0;
        }
        return ((bArrB[iC] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL) << 24) | ((bArrB[iC + 1] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL) << 16) | ((bArrB[iC + 2] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL) << 8) | (bArrB[iC + 3] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL);
    }

    @Override // org.apache.thrift.protocol.d
    public long i() throws TException {
        int iC;
        byte[] bArrB = this.f2143m;
        if (this.f2157a.d() >= 8) {
            bArrB = this.f2157a.b();
            iC = this.f2157a.c();
            this.f2157a.a(8);
        } else {
            M(this.f2143m, 0, 8);
            iC = 0;
        }
        return ((long) (bArrB[iC + 7] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL)) | (((long) (bArrB[iC] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL)) << 56) | (((long) (bArrB[iC + 1] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL)) << 48) | (((long) (bArrB[iC + 2] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL)) << 40) | (((long) (bArrB[iC + 3] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL)) << 32) | (((long) (bArrB[iC + 4] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL)) << 24) | (((long) (bArrB[iC + 5] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL)) << 16) | (((long) (bArrB[iC + 6] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL)) << 8);
    }

    @Override // org.apache.thrift.protocol.d
    public b j() throws TException {
        return new b(c(), h());
    }

    @Override // org.apache.thrift.protocol.d
    public c l() throws TException {
        return new c(c(), c(), h());
    }

    @Override // org.apache.thrift.protocol.d
    public f n() throws TException {
        return new f(c(), h());
    }

    @Override // org.apache.thrift.protocol.d
    public String p() throws TException {
        int iH = h();
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

    @Override // org.apache.thrift.protocol.d
    public g q() {
        return f2131n;
    }

    @Override // org.apache.thrift.protocol.d
    public void t(ByteBuffer byteBuffer) throws TException {
        int iLimit = (byteBuffer.limit() - byteBuffer.position()) - byteBuffer.arrayOffset();
        A(iLimit);
        this.f2157a.g(byteBuffer.array(), byteBuffer.position() + byteBuffer.arrayOffset(), iLimit);
    }

    @Override // org.apache.thrift.protocol.d
    public void u(boolean z2) throws TException {
        v(z2 ? (byte) 1 : (byte) 0);
    }

    @Override // org.apache.thrift.protocol.d
    public void v(byte b2) throws TException {
        byte[] bArr = this.f2136f;
        bArr[0] = b2;
        this.f2157a.g(bArr, 0, 1);
    }

    @Override // org.apache.thrift.protocol.d
    public void w(a aVar) throws TException {
        v(aVar.f2150b);
        z(aVar.f2151c);
    }

    @Override // org.apache.thrift.protocol.d
    public void y() throws TException {
        v((byte) 0);
    }

    @Override // org.apache.thrift.protocol.d
    public void z(short s2) throws TException {
        byte[] bArr = this.f2137g;
        bArr[0] = (byte) ((s2 >> 8) & 255);
        bArr[1] = (byte) (s2 & 255);
        this.f2157a.g(bArr, 0, 2);
    }

    @Override // org.apache.thrift.protocol.d
    public void D() {
    }

    @Override // org.apache.thrift.protocol.d
    public void F() {
    }

    @Override // org.apache.thrift.protocol.d
    public void H() {
    }

    @Override // org.apache.thrift.protocol.d
    public void K() {
    }

    @Override // org.apache.thrift.protocol.d
    public void f() {
    }

    @Override // org.apache.thrift.protocol.d
    public void k() {
    }

    @Override // org.apache.thrift.protocol.d
    public void m() {
    }

    @Override // org.apache.thrift.protocol.d
    public void o() {
    }

    @Override // org.apache.thrift.protocol.d
    public void r() {
    }

    @Override // org.apache.thrift.protocol.d
    public void x() {
    }

    @Override // org.apache.thrift.protocol.d
    public void J(g gVar) {
    }
}
