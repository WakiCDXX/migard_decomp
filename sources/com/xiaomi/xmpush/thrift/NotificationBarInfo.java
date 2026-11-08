package com.xiaomi.xmpush.thrift;

import com.xiaomi.push.mpcd.Constants;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.meta_data.ListMetaData;
import org.apache.thrift.meta_data.StructMetaData;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.protocol.a;
import org.apache.thrift.protocol.b;
import org.apache.thrift.protocol.d;
import org.apache.thrift.protocol.e;
import org.apache.thrift.protocol.g;

/* JADX INFO: loaded from: classes.dex */
public class NotificationBarInfo implements TBase<NotificationBarInfo, _Fields>, Serializable, Cloneable {
    private static final int __VERSION_ISSET_ID = 0;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    private BitSet __isset_bit_vector;
    public List<NotificationBarInfoItem> data;
    public int version;
    private static final g STRUCT_DESC = new g("NotificationBarInfo");
    private static final a VERSION_FIELD_DESC = new a("version", (byte) 8, 1);
    private static final a DATA_FIELD_DESC = new a("data", (byte) 15, 2);

    /* JADX INFO: renamed from: com.xiaomi.xmpush.thrift.NotificationBarInfo$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$xiaomi$xmpush$thrift$NotificationBarInfo$_Fields;

        static {
            int[] iArr = new int[_Fields.values().length];
            $SwitchMap$com$xiaomi$xmpush$thrift$NotificationBarInfo$_Fields = iArr;
            try {
                iArr[_Fields.VERSION.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$xiaomi$xmpush$thrift$NotificationBarInfo$_Fields[_Fields.DATA.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    public enum _Fields {
        VERSION(1, "version"),
        DATA(2, "data");

        private static final Map<String, _Fields> byName = new HashMap();
        private final String _fieldName;
        private final short _thriftId;

        static {
            for (_Fields _fields : EnumSet.allOf(_Fields.class)) {
                byName.put(_fields.getFieldName(), _fields);
            }
        }

        _Fields(short s2, String str) {
            this._thriftId = s2;
            this._fieldName = str;
        }

        public static _Fields findByName(String str) {
            return byName.get(str);
        }

        public static _Fields findByThriftId(int i2) {
            if (i2 == 1) {
                return VERSION;
            }
            if (i2 != 2) {
                return null;
            }
            return DATA;
        }

        public static _Fields findByThriftIdOrThrow(int i2) {
            _Fields _fieldsFindByThriftId = findByThriftId(i2);
            if (_fieldsFindByThriftId != null) {
                return _fieldsFindByThriftId;
            }
            throw new IllegalArgumentException("Field " + i2 + " doesn't exist!");
        }

        public String getFieldName() {
            return this._fieldName;
        }

        public short getThriftFieldId() {
            return this._thriftId;
        }
    }

    static {
        EnumMap enumMap = new EnumMap(_Fields.class);
        enumMap.put(_Fields.VERSION, new FieldMetaData("version", (byte) 1, new FieldValueMetaData((byte) 8)));
        enumMap.put(_Fields.DATA, new FieldMetaData("data", (byte) 2, new ListMetaData((byte) 15, new StructMetaData((byte) 12, NotificationBarInfoItem.class))));
        Map<_Fields, FieldMetaData> mapUnmodifiableMap = Collections.unmodifiableMap(enumMap);
        metaDataMap = mapUnmodifiableMap;
        FieldMetaData.b(NotificationBarInfo.class, mapUnmodifiableMap);
    }

    public NotificationBarInfo() {
        this.__isset_bit_vector = new BitSet(1);
    }

    public void addToData(NotificationBarInfoItem notificationBarInfoItem) {
        if (this.data == null) {
            this.data = new ArrayList();
        }
        this.data.add(notificationBarInfoItem);
    }

    public void clear() {
        setVersionIsSet(false);
        this.version = 0;
        this.data = null;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof NotificationBarInfo)) {
            return equals((NotificationBarInfo) obj);
        }
        return false;
    }

    public _Fields fieldForId(int i2) {
        return _Fields.findByThriftId(i2);
    }

    public List<NotificationBarInfoItem> getData() {
        return this.data;
    }

    public Iterator<NotificationBarInfoItem> getDataIterator() {
        List<NotificationBarInfoItem> list = this.data;
        if (list == null) {
            return null;
        }
        return list.iterator();
    }

    public int getDataSize() {
        List<NotificationBarInfoItem> list = this.data;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public Object getFieldValue(_Fields _fields) {
        int i2 = AnonymousClass1.$SwitchMap$com$xiaomi$xmpush$thrift$NotificationBarInfo$_Fields[_fields.ordinal()];
        if (i2 == 1) {
            return new Integer(getVersion());
        }
        if (i2 == 2) {
            return getData();
        }
        throw new IllegalStateException();
    }

    public int getVersion() {
        return this.version;
    }

    public int hashCode() {
        return 0;
    }

    public boolean isSet(_Fields _fields) {
        if (_fields == null) {
            throw new IllegalArgumentException();
        }
        int i2 = AnonymousClass1.$SwitchMap$com$xiaomi$xmpush$thrift$NotificationBarInfo$_Fields[_fields.ordinal()];
        if (i2 == 1) {
            return isSetVersion();
        }
        if (i2 == 2) {
            return isSetData();
        }
        throw new IllegalStateException();
    }

    public boolean isSetData() {
        return this.data != null;
    }

    public boolean isSetVersion() {
        return this.__isset_bit_vector.get(0);
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
                    e.a(dVar, b2);
                } else if (b2 == 15) {
                    b bVarJ = dVar.j();
                    this.data = new ArrayList(bVarJ.f2153b);
                    for (int i2 = 0; i2 < bVarJ.f2153b; i2++) {
                        NotificationBarInfoItem notificationBarInfoItem = new NotificationBarInfoItem();
                        notificationBarInfoItem.read(dVar);
                        this.data.add(notificationBarInfoItem);
                    }
                    dVar.k();
                } else {
                    e.a(dVar, b2);
                }
            } else if (b2 == 8) {
                this.version = dVar.h();
                setVersionIsSet(true);
            } else {
                e.a(dVar, b2);
            }
            dVar.f();
        }
        dVar.r();
        if (isSetVersion()) {
            validate();
            return;
        }
        throw new TProtocolException("Required field 'version' was not found in serialized data! Struct: " + toString());
    }

    public NotificationBarInfo setData(List<NotificationBarInfoItem> list) {
        this.data = list;
        return this;
    }

    public void setDataIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.data = null;
    }

    public void setFieldValue(_Fields _fields, Object obj) {
        int i2 = AnonymousClass1.$SwitchMap$com$xiaomi$xmpush$thrift$NotificationBarInfo$_Fields[_fields.ordinal()];
        if (i2 == 1) {
            if (obj == null) {
                unsetVersion();
                return;
            } else {
                setVersion(((Integer) obj).intValue());
                return;
            }
        }
        if (i2 != 2) {
            return;
        }
        if (obj == null) {
            unsetData();
        } else {
            setData((List) obj);
        }
    }

    public NotificationBarInfo setVersion(int i2) {
        this.version = i2;
        setVersionIsSet(true);
        return this;
    }

    public void setVersionIsSet(boolean z2) {
        this.__isset_bit_vector.set(0, z2);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("NotificationBarInfo(");
        sb.append("version:");
        sb.append(this.version);
        if (isSetData()) {
            sb.append(", ");
            sb.append("data:");
            List<NotificationBarInfoItem> list = this.data;
            if (list == null) {
                sb.append("null");
            } else {
                sb.append(list);
            }
        }
        sb.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
        return sb.toString();
    }

    public void unsetData() {
        this.data = null;
    }

    public void unsetVersion() {
        this.__isset_bit_vector.clear(0);
    }

    @Override // org.apache.thrift.TBase
    public void write(d dVar) throws TException {
        validate();
        dVar.J(STRUCT_DESC);
        dVar.w(VERSION_FIELD_DESC);
        dVar.A(this.version);
        dVar.x();
        if (this.data != null && isSetData()) {
            dVar.w(DATA_FIELD_DESC);
            dVar.C(new b((byte) 12, this.data.size()));
            Iterator<NotificationBarInfoItem> it = this.data.iterator();
            while (it.hasNext()) {
                it.next().write(dVar);
            }
            dVar.D();
            dVar.x();
        }
        dVar.y();
        dVar.K();
    }

    @Override // java.lang.Comparable
    public int compareTo(NotificationBarInfo notificationBarInfo) {
        int iH;
        int iD;
        if (!getClass().equals(notificationBarInfo.getClass())) {
            return getClass().getName().compareTo(notificationBarInfo.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetVersion()).compareTo(Boolean.valueOf(notificationBarInfo.isSetVersion()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetVersion() && (iD = g0.a.d(this.version, notificationBarInfo.version)) != 0) {
            return iD;
        }
        int iCompareTo2 = Boolean.valueOf(isSetData()).compareTo(Boolean.valueOf(notificationBarInfo.isSetData()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (!isSetData() || (iH = g0.a.h(this.data, notificationBarInfo.data)) == 0) {
            return 0;
        }
        return iH;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public NotificationBarInfo m21deepCopy() {
        return new NotificationBarInfo(this);
    }

    public NotificationBarInfo(int i2) {
        this();
        this.version = i2;
        setVersionIsSet(true);
    }

    public boolean equals(NotificationBarInfo notificationBarInfo) {
        if (notificationBarInfo == null || this.version != notificationBarInfo.version) {
            return false;
        }
        boolean zIsSetData = isSetData();
        boolean zIsSetData2 = notificationBarInfo.isSetData();
        if (zIsSetData || zIsSetData2) {
            return zIsSetData && zIsSetData2 && this.data.equals(notificationBarInfo.data);
        }
        return true;
    }

    public NotificationBarInfo(NotificationBarInfo notificationBarInfo) {
        BitSet bitSet = new BitSet(1);
        this.__isset_bit_vector = bitSet;
        bitSet.clear();
        this.__isset_bit_vector.or(notificationBarInfo.__isset_bit_vector);
        this.version = notificationBarInfo.version;
        if (notificationBarInfo.isSetData()) {
            ArrayList arrayList = new ArrayList();
            Iterator<NotificationBarInfoItem> it = notificationBarInfo.data.iterator();
            while (it.hasNext()) {
                arrayList.add(new NotificationBarInfoItem(it.next()));
            }
            this.data = arrayList;
        }
    }

    public void validate() throws TException {
    }
}
