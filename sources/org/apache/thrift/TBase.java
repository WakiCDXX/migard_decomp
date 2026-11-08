package org.apache.thrift;

import java.io.Serializable;
import org.apache.thrift.TBase;
import org.apache.thrift.protocol.d;

/* JADX INFO: loaded from: classes.dex */
public interface TBase<T extends TBase, F> extends Comparable<T>, Serializable {
    void read(d dVar) throws TException;

    void write(d dVar) throws TException;
}
