package org.apache.thrift.protocol;

import java.nio.ByteBuffer;
import org.apache.thrift.TException;

/* JADX INFO: compiled from: TProtocol.java */
/* JADX INFO: loaded from: classes.dex */
public abstract class d {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    protected org.apache.thrift.transport.d f2157a;

    protected d(org.apache.thrift.transport.d dVar) {
        this.f2157a = dVar;
    }

    public abstract void A(int i2) throws TException;

    public abstract void B(long j2) throws TException;

    public abstract void C(b bVar) throws TException;

    public abstract void D() throws TException;

    public abstract void E(c cVar) throws TException;

    public abstract void F() throws TException;

    public abstract void G(f fVar) throws TException;

    public abstract void H() throws TException;

    public abstract void I(String str) throws TException;

    public abstract void J(g gVar) throws TException;

    public abstract void K() throws TException;

    public abstract ByteBuffer a() throws TException;

    public abstract boolean b() throws TException;

    public abstract byte c() throws TException;

    public abstract double d() throws TException;

    public abstract a e() throws TException;

    public abstract void f() throws TException;

    public abstract short g() throws TException;

    public abstract int h() throws TException;

    public abstract long i() throws TException;

    public abstract b j() throws TException;

    public abstract void k() throws TException;

    public abstract c l() throws TException;

    public abstract void m() throws TException;

    public abstract f n() throws TException;

    public abstract void o() throws TException;

    public abstract String p() throws TException;

    public abstract g q() throws TException;

    public abstract void r() throws TException;

    public abstract void t(ByteBuffer byteBuffer) throws TException;

    public abstract void u(boolean z2) throws TException;

    public abstract void v(byte b2) throws TException;

    public abstract void w(a aVar) throws TException;

    public abstract void x() throws TException;

    public abstract void y() throws TException;

    public abstract void z(short s2) throws TException;

    public void s() {
    }
}
