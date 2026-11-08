package org.apache.thrift;

/* JADX INFO: loaded from: classes.dex */
public class TException extends Exception {
    private static final long serialVersionUID = 1;

    public TException() {
    }

    public TException(String str) {
        super(str);
    }

    public TException(Throwable th) {
        super(th);
    }
}
