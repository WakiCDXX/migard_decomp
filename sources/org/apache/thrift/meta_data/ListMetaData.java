package org.apache.thrift.meta_data;

/* JADX INFO: loaded from: classes.dex */
public class ListMetaData extends FieldValueMetaData {
    public final FieldValueMetaData elemMetaData;

    public ListMetaData(byte b2, FieldValueMetaData fieldValueMetaData) {
        super(b2);
        this.elemMetaData = fieldValueMetaData;
    }
}
