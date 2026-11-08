package com.xiaomi.xmpush.thrift;

import com.xiaomi.push.mpcd.Constants;
import java.io.Serializable;
import java.util.BitSet;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.protocol.a;
import org.apache.thrift.protocol.d;
import org.apache.thrift.protocol.e;
import org.apache.thrift.protocol.g;

/* JADX INFO: loaded from: classes.dex */
public class DataCollectionItem implements TBase<DataCollectionItem, Object>, Serializable, Cloneable {
    private static final int __COLLECTEDAT_ISSET_ID = 0;
    private BitSet __isset_bit_vector;
    public long collectedAt;
    public ClientCollectionType collectionType;
    public String content;
    private static final g STRUCT_DESC = new g("DataCollectionItem");
    private static final a COLLECTED_AT_FIELD_DESC = new a("", (byte) 10, 1);
    private static final a COLLECTION_TYPE_FIELD_DESC = new a("", (byte) 8, 2);
    private static final a CONTENT_FIELD_DESC = new a("", (byte) 11, 3);

    public DataCollectionItem() {
        this.__isset_bit_vector = new BitSet(1);
    }

    public void clear() {
        setCollectedAtIsSet(false);
        this.collectedAt = 0L;
        this.collectionType = null;
        this.content = null;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof DataCollectionItem)) {
            return equals((DataCollectionItem) obj);
        }
        return false;
    }

    public long getCollectedAt() {
        return this.collectedAt;
    }

    public ClientCollectionType getCollectionType() {
        return this.collectionType;
    }

    public String getContent() {
        return this.content;
    }

    public int hashCode() {
        return 0;
    }

    public boolean isSetCollectedAt() {
        return this.__isset_bit_vector.get(0);
    }

    public boolean isSetCollectionType() {
        return this.collectionType != null;
    }

    public boolean isSetContent() {
        return this.content != null;
    }

    @Override // org.apache.thrift.TBase
    public void read(d dVar) throws TException {
        dVar.q();
        while (true) {
            a aVarE = dVar.e();
            byte b2 = aVarE.f2150b;
            if (b2 == 0) {
                break;
            }
            short s2 = aVarE.f2151c;
            if (s2 != 1) {
                if (s2 != 2) {
                    if (s2 != 3) {
                        e.a(dVar, b2);
                    } else if (b2 == 11) {
                        this.content = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                } else if (b2 == 8) {
                    this.collectionType = ClientCollectionType.findByValue(dVar.h());
                } else {
                    e.a(dVar, b2);
                }
            } else if (b2 == 10) {
                this.collectedAt = dVar.i();
                setCollectedAtIsSet(true);
            } else {
                e.a(dVar, b2);
            }
            dVar.f();
        }
        dVar.r();
        if (isSetCollectedAt()) {
            validate();
            return;
        }
        throw new TProtocolException("Required field 'collectedAt' was not found in serialized data! Struct: " + toString());
    }

    public DataCollectionItem setCollectedAt(long j2) {
        this.collectedAt = j2;
        setCollectedAtIsSet(true);
        return this;
    }

    public void setCollectedAtIsSet(boolean z2) {
        this.__isset_bit_vector.set(0, z2);
    }

    public DataCollectionItem setCollectionType(ClientCollectionType clientCollectionType) {
        this.collectionType = clientCollectionType;
        return this;
    }

    public void setCollectionTypeIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.collectionType = null;
    }

    public DataCollectionItem setContent(String str) {
        this.content = str;
        return this;
    }

    public void setContentIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.content = null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("DataCollectionItem(");
        sb.append("collectedAt:");
        sb.append(this.collectedAt);
        sb.append(", ");
        sb.append("collectionType:");
        ClientCollectionType clientCollectionType = this.collectionType;
        if (clientCollectionType == null) {
            sb.append("null");
        } else {
            sb.append(clientCollectionType);
        }
        sb.append(", ");
        sb.append("content:");
        String str = this.content;
        if (str == null) {
            sb.append("null");
        } else {
            sb.append(str);
        }
        sb.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
        return sb.toString();
    }

    public void unsetCollectedAt() {
        this.__isset_bit_vector.clear(0);
    }

    public void unsetCollectionType() {
        this.collectionType = null;
    }

    public void unsetContent() {
        this.content = null;
    }

    public void validate() throws TException {
        if (this.collectionType == null) {
            throw new TProtocolException("Required field 'collectionType' was not present! Struct: " + toString());
        }
        if (this.content != null) {
            return;
        }
        throw new TProtocolException("Required field 'content' was not present! Struct: " + toString());
    }

    @Override // org.apache.thrift.TBase
    public void write(d dVar) throws TException {
        validate();
        dVar.J(STRUCT_DESC);
        dVar.w(COLLECTED_AT_FIELD_DESC);
        dVar.B(this.collectedAt);
        dVar.x();
        if (this.collectionType != null) {
            dVar.w(COLLECTION_TYPE_FIELD_DESC);
            dVar.A(this.collectionType.getValue());
            dVar.x();
        }
        if (this.content != null) {
            dVar.w(CONTENT_FIELD_DESC);
            dVar.I(this.content);
            dVar.x();
        }
        dVar.y();
        dVar.K();
    }

    @Override // java.lang.Comparable
    public int compareTo(DataCollectionItem dataCollectionItem) {
        int iG;
        int iF;
        int iE;
        if (!getClass().equals(dataCollectionItem.getClass())) {
            return getClass().getName().compareTo(dataCollectionItem.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetCollectedAt()).compareTo(Boolean.valueOf(dataCollectionItem.isSetCollectedAt()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetCollectedAt() && (iE = g0.a.e(this.collectedAt, dataCollectionItem.collectedAt)) != 0) {
            return iE;
        }
        int iCompareTo2 = Boolean.valueOf(isSetCollectionType()).compareTo(Boolean.valueOf(dataCollectionItem.isSetCollectionType()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (isSetCollectionType() && (iF = g0.a.f(this.collectionType, dataCollectionItem.collectionType)) != 0) {
            return iF;
        }
        int iCompareTo3 = Boolean.valueOf(isSetContent()).compareTo(Boolean.valueOf(dataCollectionItem.isSetContent()));
        if (iCompareTo3 != 0) {
            return iCompareTo3;
        }
        if (!isSetContent() || (iG = g0.a.g(this.content, dataCollectionItem.content)) == 0) {
            return 0;
        }
        return iG;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public DataCollectionItem m19deepCopy() {
        return new DataCollectionItem(this);
    }

    public DataCollectionItem(long j2, ClientCollectionType clientCollectionType, String str) {
        this();
        this.collectedAt = j2;
        setCollectedAtIsSet(true);
        this.collectionType = clientCollectionType;
        this.content = str;
    }

    public boolean equals(DataCollectionItem dataCollectionItem) {
        if (dataCollectionItem == null || this.collectedAt != dataCollectionItem.collectedAt) {
            return false;
        }
        boolean zIsSetCollectionType = isSetCollectionType();
        boolean zIsSetCollectionType2 = dataCollectionItem.isSetCollectionType();
        if ((zIsSetCollectionType || zIsSetCollectionType2) && !(zIsSetCollectionType && zIsSetCollectionType2 && this.collectionType.equals(dataCollectionItem.collectionType))) {
            return false;
        }
        boolean zIsSetContent = isSetContent();
        boolean zIsSetContent2 = dataCollectionItem.isSetContent();
        if (zIsSetContent || zIsSetContent2) {
            return zIsSetContent && zIsSetContent2 && this.content.equals(dataCollectionItem.content);
        }
        return true;
    }

    public DataCollectionItem(DataCollectionItem dataCollectionItem) {
        BitSet bitSet = new BitSet(1);
        this.__isset_bit_vector = bitSet;
        bitSet.clear();
        this.__isset_bit_vector.or(dataCollectionItem.__isset_bit_vector);
        this.collectedAt = dataCollectionItem.collectedAt;
        if (dataCollectionItem.isSetCollectionType()) {
            this.collectionType = dataCollectionItem.collectionType;
        }
        if (dataCollectionItem.isSetContent()) {
            this.content = dataCollectionItem.content;
        }
    }
}
