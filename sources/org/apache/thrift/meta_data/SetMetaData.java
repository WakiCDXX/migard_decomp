package org.apache.thrift.meta_data;

/* JADX INFO: loaded from: classes.dex */
public class SetMetaData extends FieldValueMetaData {
    public final FieldValueMetaData elemMetaData;

    public SetMetaData(byte b2, FieldValueMetaData fieldValueMetaData) {
        super(b2);
        this.elemMetaData = fieldValueMetaData;
    }
}
