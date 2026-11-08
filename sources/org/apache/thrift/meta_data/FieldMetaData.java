package org.apache.thrift.meta_data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.apache.thrift.TBase;

/* JADX INFO: loaded from: classes.dex */
public class FieldMetaData implements Serializable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static Map<Class<? extends TBase>, Map<?, FieldMetaData>> f2130a = new HashMap();
    public final String fieldName;
    public final byte requirementType;
    public final FieldValueMetaData valueMetaData;

    public FieldMetaData(String str, byte b2, FieldValueMetaData fieldValueMetaData) {
        this.fieldName = str;
        this.requirementType = b2;
        this.valueMetaData = fieldValueMetaData;
    }

    public static void b(Class<? extends TBase> cls, Map<?, FieldMetaData> map) {
        f2130a.put(cls, map);
    }
}
