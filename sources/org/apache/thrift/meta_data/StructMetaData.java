package org.apache.thrift.meta_data;

import org.apache.thrift.TBase;

/* JADX INFO: loaded from: classes.dex */
public class StructMetaData extends FieldValueMetaData {
    public final Class<? extends TBase> structClass;

    public StructMetaData(byte b2, Class<? extends TBase> cls) {
        super(b2);
        this.structClass = cls;
    }
}
