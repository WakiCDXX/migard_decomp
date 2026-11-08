package org.apache.thrift.meta_data;

import java.io.Serializable;

/* JADX INFO: loaded from: classes.dex */
public class FieldValueMetaData implements Serializable {
    public final byte type;
    private final boolean isTypedefType = false;
    private final String typedefName = null;

    public FieldValueMetaData(byte b2) {
        this.type = b2;
    }
}
