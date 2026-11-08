package g0;

import java.io.ByteArrayOutputStream;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocolFactory;

/* JADX INFO: compiled from: TSerializer.java */
/* JADX INFO: loaded from: classes.dex */
public class d {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private final ByteArrayOutputStream f1862a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private final org.apache.thrift.transport.a f1863b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private org.apache.thrift.protocol.d f1864c;

    public d(TProtocolFactory tProtocolFactory) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        this.f1862a = byteArrayOutputStream;
        org.apache.thrift.transport.a aVar = new org.apache.thrift.transport.a(byteArrayOutputStream);
        this.f1863b = aVar;
        this.f1864c = tProtocolFactory.a(aVar);
    }

    public byte[] a(TBase tBase) throws TException {
        this.f1862a.reset();
        tBase.write(this.f1864c);
        return this.f1862a.toByteArray();
    }
}
