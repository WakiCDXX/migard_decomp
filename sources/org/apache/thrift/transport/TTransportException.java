package org.apache.thrift.transport;

import org.apache.thrift.TException;

/* JADX INFO: loaded from: classes.dex */
public class TTransportException extends TException {
    private static final long serialVersionUID = 1;
    protected int type_;

    public TTransportException(int i2) {
        this.type_ = i2;
    }

    public TTransportException(int i2, String str) {
        super(str);
        this.type_ = i2;
    }

    public TTransportException(String str) {
        super(str);
        this.type_ = 0;
    }

    public TTransportException(int i2, Throwable th) {
        super(th);
        this.type_ = i2;
    }
}
