package org.apache.thrift.protocol;

import org.apache.thrift.TException;

/* JADX INFO: loaded from: classes.dex */
public class TProtocolException extends TException {
    private static final long serialVersionUID = 1;
    protected int type_;

    public TProtocolException(int i2, String str) {
        super(str);
        this.type_ = i2;
    }

    public TProtocolException(String str) {
        super(str);
        this.type_ = 0;
    }
}
