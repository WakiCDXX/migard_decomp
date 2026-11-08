package g0;

import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocolFactory;

/* JADX INFO: compiled from: TDeserializer.java */
/* JADX INFO: loaded from: classes.dex */
public class c {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private final org.apache.thrift.protocol.d f1860a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private final org.apache.thrift.transport.c f1861b;

    public c(TProtocolFactory tProtocolFactory) {
        org.apache.thrift.transport.c cVar = new org.apache.thrift.transport.c();
        this.f1861b = cVar;
        this.f1860a = tProtocolFactory.a(cVar);
    }

    public void a(TBase tBase, byte[] bArr) throws TException {
        try {
            this.f1861b.h(bArr);
            tBase.read(this.f1860a);
        } finally {
            this.f1860a.s();
        }
    }
}
