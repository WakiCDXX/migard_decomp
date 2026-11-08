package com.xiaomi.common.logger.thrift.mfs;

import com.xiaomi.push.mpcd.Constants;
import java.io.Serializable;
import java.util.ArrayList;
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
public class HostInfo implements TBase<HostInfo, _Fields>, Serializable, Cloneable {
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    private String host;
    private List<LandNodeInfo> land_node_info;
    private static final g STRUCT_DESC = new g("HostInfo");
    private static final a HOST_FIELD_DESC = new a("host", (byte) 11, 1);
    private static final a LAND_NODE_INFO_FIELD_DESC = new a("land_node_info", (byte) 15, 2);

    /* JADX INFO: renamed from: com.xiaomi.common.logger.thrift.mfs.HostInfo$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$xiaomi$common$logger$thrift$mfs$HostInfo$_Fields;

        static {
            int[] iArr = new int[_Fields.values().length];
            $SwitchMap$com$xiaomi$common$logger$thrift$mfs$HostInfo$_Fields = iArr;
            try {
                iArr[_Fields.HOST.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$HostInfo$_Fields[_Fields.LAND_NODE_INFO.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    public enum _Fields {
        HOST(1, "host"),
        LAND_NODE_INFO(2, "land_node_info");

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
                return HOST;
            }
            if (i2 != 2) {
                return null;
            }
            return LAND_NODE_INFO;
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
        enumMap.put(_Fields.HOST, new FieldMetaData("host", (byte) 1, new FieldValueMetaData((byte) 11)));
        enumMap.put(_Fields.LAND_NODE_INFO, new FieldMetaData("land_node_info", (byte) 1, new ListMetaData((byte) 15, new StructMetaData((byte) 12, LandNodeInfo.class))));
        Map<_Fields, FieldMetaData> mapUnmodifiableMap = Collections.unmodifiableMap(enumMap);
        metaDataMap = mapUnmodifiableMap;
        FieldMetaData.b(HostInfo.class, mapUnmodifiableMap);
    }

    public HostInfo() {
    }

    public void addToLand_node_info(LandNodeInfo landNodeInfo) {
        if (this.land_node_info == null) {
            this.land_node_info = new ArrayList();
        }
        this.land_node_info.add(landNodeInfo);
    }

    public void clear() {
        this.host = null;
        this.land_node_info = null;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof HostInfo)) {
            return equals((HostInfo) obj);
        }
        return false;
    }

    public _Fields fieldForId(int i2) {
        return _Fields.findByThriftId(i2);
    }

    public Object getFieldValue(_Fields _fields) {
        int i2 = AnonymousClass1.$SwitchMap$com$xiaomi$common$logger$thrift$mfs$HostInfo$_Fields[_fields.ordinal()];
        if (i2 == 1) {
            return getHost();
        }
        if (i2 == 2) {
            return getLand_node_info();
        }
        throw new IllegalStateException();
    }

    public String getHost() {
        return this.host;
    }

    public List<LandNodeInfo> getLand_node_info() {
        return this.land_node_info;
    }

    public Iterator<LandNodeInfo> getLand_node_infoIterator() {
        List<LandNodeInfo> list = this.land_node_info;
        if (list == null) {
            return null;
        }
        return list.iterator();
    }

    public int getLand_node_infoSize() {
        List<LandNodeInfo> list = this.land_node_info;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public int hashCode() {
        return 0;
    }

    public boolean isSet(_Fields _fields) {
        if (_fields == null) {
            throw new IllegalArgumentException();
        }
        int i2 = AnonymousClass1.$SwitchMap$com$xiaomi$common$logger$thrift$mfs$HostInfo$_Fields[_fields.ordinal()];
        if (i2 == 1) {
            return isSetHost();
        }
        if (i2 == 2) {
            return isSetLand_node_info();
        }
        throw new IllegalStateException();
    }

    public boolean isSetHost() {
        return this.host != null;
    }

    public boolean isSetLand_node_info() {
        return this.land_node_info != null;
    }

    @Override // org.apache.thrift.TBase
    public void read(d dVar) throws TException {
        dVar.q();
        while (true) {
            a aVarE = dVar.e();
            byte b2 = aVarE.f2150b;
            if (b2 == 0) {
                dVar.r();
                validate();
                return;
            }
            short s2 = aVarE.f2151c;
            if (s2 != 1) {
                if (s2 != 2) {
                    e.a(dVar, b2);
                } else if (b2 == 15) {
                    b bVarJ = dVar.j();
                    this.land_node_info = new ArrayList(bVarJ.f2153b);
                    for (int i2 = 0; i2 < bVarJ.f2153b; i2++) {
                        LandNodeInfo landNodeInfo = new LandNodeInfo();
                        landNodeInfo.read(dVar);
                        this.land_node_info.add(landNodeInfo);
                    }
                    dVar.k();
                } else {
                    e.a(dVar, b2);
                }
            } else if (b2 == 11) {
                this.host = dVar.p();
            } else {
                e.a(dVar, b2);
            }
            dVar.f();
        }
    }

    public void setFieldValue(_Fields _fields, Object obj) {
        int i2 = AnonymousClass1.$SwitchMap$com$xiaomi$common$logger$thrift$mfs$HostInfo$_Fields[_fields.ordinal()];
        if (i2 == 1) {
            if (obj == null) {
                unsetHost();
                return;
            } else {
                setHost((String) obj);
                return;
            }
        }
        if (i2 != 2) {
            return;
        }
        if (obj == null) {
            unsetLand_node_info();
        } else {
            setLand_node_info((List) obj);
        }
    }

    public HostInfo setHost(String str) {
        this.host = str;
        return this;
    }

    public void setHostIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.host = null;
    }

    public HostInfo setLand_node_info(List<LandNodeInfo> list) {
        this.land_node_info = list;
        return this;
    }

    public void setLand_node_infoIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.land_node_info = null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("HostInfo(");
        sb.append("host:");
        String str = this.host;
        if (str == null) {
            sb.append("null");
        } else {
            sb.append(str);
        }
        sb.append(", ");
        sb.append("land_node_info:");
        List<LandNodeInfo> list = this.land_node_info;
        if (list == null) {
            sb.append("null");
        } else {
            sb.append(list);
        }
        sb.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
        return sb.toString();
    }

    public void unsetHost() {
        this.host = null;
    }

    public void unsetLand_node_info() {
        this.land_node_info = null;
    }

    public void validate() throws TException {
        if (this.host == null) {
            throw new TProtocolException("Required field 'host' was not present! Struct: " + toString());
        }
        if (this.land_node_info != null) {
            return;
        }
        throw new TProtocolException("Required field 'land_node_info' was not present! Struct: " + toString());
    }

    @Override // org.apache.thrift.TBase
    public void write(d dVar) throws TException {
        validate();
        dVar.J(STRUCT_DESC);
        if (this.host != null) {
            dVar.w(HOST_FIELD_DESC);
            dVar.I(this.host);
            dVar.x();
        }
        if (this.land_node_info != null) {
            dVar.w(LAND_NODE_INFO_FIELD_DESC);
            dVar.C(new b((byte) 12, this.land_node_info.size()));
            Iterator<LandNodeInfo> it = this.land_node_info.iterator();
            while (it.hasNext()) {
                it.next().write(dVar);
            }
            dVar.D();
            dVar.x();
        }
        dVar.y();
        dVar.K();
    }

    public HostInfo(String str, List<LandNodeInfo> list) {
        this();
        this.host = str;
        this.land_node_info = list;
    }

    @Override // java.lang.Comparable
    public int compareTo(HostInfo hostInfo) {
        int iH;
        int iG;
        if (!getClass().equals(hostInfo.getClass())) {
            return getClass().getName().compareTo(hostInfo.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetHost()).compareTo(Boolean.valueOf(hostInfo.isSetHost()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetHost() && (iG = g0.a.g(this.host, hostInfo.host)) != 0) {
            return iG;
        }
        int iCompareTo2 = Boolean.valueOf(isSetLand_node_info()).compareTo(Boolean.valueOf(hostInfo.isSetLand_node_info()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (!isSetLand_node_info() || (iH = g0.a.h(this.land_node_info, hostInfo.land_node_info)) == 0) {
            return 0;
        }
        return iH;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public HostInfo m4deepCopy() {
        return new HostInfo(this);
    }

    public boolean equals(HostInfo hostInfo) {
        if (hostInfo == null) {
            return false;
        }
        boolean zIsSetHost = isSetHost();
        boolean zIsSetHost2 = hostInfo.isSetHost();
        if ((zIsSetHost || zIsSetHost2) && !(zIsSetHost && zIsSetHost2 && this.host.equals(hostInfo.host))) {
            return false;
        }
        boolean zIsSetLand_node_info = isSetLand_node_info();
        boolean zIsSetLand_node_info2 = hostInfo.isSetLand_node_info();
        if (zIsSetLand_node_info || zIsSetLand_node_info2) {
            return zIsSetLand_node_info && zIsSetLand_node_info2 && this.land_node_info.equals(hostInfo.land_node_info);
        }
        return true;
    }

    public HostInfo(HostInfo hostInfo) {
        if (hostInfo.isSetHost()) {
            this.host = hostInfo.host;
        }
        if (hostInfo.isSetLand_node_info()) {
            ArrayList arrayList = new ArrayList();
            Iterator<LandNodeInfo> it = hostInfo.land_node_info.iterator();
            while (it.hasNext()) {
                arrayList.add(new LandNodeInfo(it.next()));
            }
            this.land_node_info = arrayList;
        }
    }
}
