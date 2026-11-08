package com.xiaomi.common.logger.thrift.mfs;

import com.xiaomi.push.mpcd.Constants;
import java.io.Serializable;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.protocol.a;
import org.apache.thrift.protocol.d;
import org.apache.thrift.protocol.e;
import org.apache.thrift.protocol.g;

/* JADX INFO: loaded from: classes.dex */
public class Location implements TBase<Location, _Fields>, Serializable, Cloneable {
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    private String city;
    private String contry;
    private String isp;
    private String province;
    private static final g STRUCT_DESC = new g("Location");
    private static final a CONTRY_FIELD_DESC = new a("contry", (byte) 11, 1);
    private static final a PROVINCE_FIELD_DESC = new a("province", (byte) 11, 2);
    private static final a CITY_FIELD_DESC = new a("city", (byte) 11, 3);
    private static final a ISP_FIELD_DESC = new a("isp", (byte) 11, 4);

    /* JADX INFO: renamed from: com.xiaomi.common.logger.thrift.mfs.Location$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$xiaomi$common$logger$thrift$mfs$Location$_Fields;

        static {
            int[] iArr = new int[_Fields.values().length];
            $SwitchMap$com$xiaomi$common$logger$thrift$mfs$Location$_Fields = iArr;
            try {
                iArr[_Fields.CONTRY.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$Location$_Fields[_Fields.PROVINCE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$Location$_Fields[_Fields.CITY.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$Location$_Fields[_Fields.ISP.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    public enum _Fields {
        CONTRY(1, "contry"),
        PROVINCE(2, "province"),
        CITY(3, "city"),
        ISP(4, "isp");

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
                return CONTRY;
            }
            if (i2 == 2) {
                return PROVINCE;
            }
            if (i2 == 3) {
                return CITY;
            }
            if (i2 != 4) {
                return null;
            }
            return ISP;
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
        enumMap.put(_Fields.CONTRY, new FieldMetaData("contry", (byte) 2, new FieldValueMetaData((byte) 11)));
        enumMap.put(_Fields.PROVINCE, new FieldMetaData("province", (byte) 2, new FieldValueMetaData((byte) 11)));
        enumMap.put(_Fields.CITY, new FieldMetaData("city", (byte) 2, new FieldValueMetaData((byte) 11)));
        enumMap.put(_Fields.ISP, new FieldMetaData("isp", (byte) 2, new FieldValueMetaData((byte) 11)));
        Map<_Fields, FieldMetaData> mapUnmodifiableMap = Collections.unmodifiableMap(enumMap);
        metaDataMap = mapUnmodifiableMap;
        FieldMetaData.b(Location.class, mapUnmodifiableMap);
    }

    public Location() {
    }

    public void clear() {
        this.contry = null;
        this.province = null;
        this.city = null;
        this.isp = null;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof Location)) {
            return equals((Location) obj);
        }
        return false;
    }

    public _Fields fieldForId(int i2) {
        return _Fields.findByThriftId(i2);
    }

    public String getCity() {
        return this.city;
    }

    public String getContry() {
        return this.contry;
    }

    public Object getFieldValue(_Fields _fields) {
        int i2 = AnonymousClass1.$SwitchMap$com$xiaomi$common$logger$thrift$mfs$Location$_Fields[_fields.ordinal()];
        if (i2 == 1) {
            return getContry();
        }
        if (i2 == 2) {
            return getProvince();
        }
        if (i2 == 3) {
            return getCity();
        }
        if (i2 == 4) {
            return getIsp();
        }
        throw new IllegalStateException();
    }

    public String getIsp() {
        return this.isp;
    }

    public String getProvince() {
        return this.province;
    }

    public int hashCode() {
        return 0;
    }

    public boolean isSet(_Fields _fields) {
        if (_fields == null) {
            throw new IllegalArgumentException();
        }
        int i2 = AnonymousClass1.$SwitchMap$com$xiaomi$common$logger$thrift$mfs$Location$_Fields[_fields.ordinal()];
        if (i2 == 1) {
            return isSetContry();
        }
        if (i2 == 2) {
            return isSetProvince();
        }
        if (i2 == 3) {
            return isSetCity();
        }
        if (i2 == 4) {
            return isSetIsp();
        }
        throw new IllegalStateException();
    }

    public boolean isSetCity() {
        return this.city != null;
    }

    public boolean isSetContry() {
        return this.contry != null;
    }

    public boolean isSetIsp() {
        return this.isp != null;
    }

    public boolean isSetProvince() {
        return this.province != null;
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
                    if (s2 != 3) {
                        if (s2 != 4) {
                            e.a(dVar, b2);
                        } else if (b2 == 11) {
                            this.isp = dVar.p();
                        } else {
                            e.a(dVar, b2);
                        }
                    } else if (b2 == 11) {
                        this.city = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                } else if (b2 == 11) {
                    this.province = dVar.p();
                } else {
                    e.a(dVar, b2);
                }
            } else if (b2 == 11) {
                this.contry = dVar.p();
            } else {
                e.a(dVar, b2);
            }
            dVar.f();
        }
    }

    public Location setCity(String str) {
        this.city = str;
        return this;
    }

    public void setCityIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.city = null;
    }

    public Location setContry(String str) {
        this.contry = str;
        return this;
    }

    public void setContryIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.contry = null;
    }

    public void setFieldValue(_Fields _fields, Object obj) {
        int i2 = AnonymousClass1.$SwitchMap$com$xiaomi$common$logger$thrift$mfs$Location$_Fields[_fields.ordinal()];
        if (i2 == 1) {
            if (obj == null) {
                unsetContry();
                return;
            } else {
                setContry((String) obj);
                return;
            }
        }
        if (i2 == 2) {
            if (obj == null) {
                unsetProvince();
                return;
            } else {
                setProvince((String) obj);
                return;
            }
        }
        if (i2 == 3) {
            if (obj == null) {
                unsetCity();
                return;
            } else {
                setCity((String) obj);
                return;
            }
        }
        if (i2 != 4) {
            return;
        }
        if (obj == null) {
            unsetIsp();
        } else {
            setIsp((String) obj);
        }
    }

    public Location setIsp(String str) {
        this.isp = str;
        return this;
    }

    public void setIspIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.isp = null;
    }

    public Location setProvince(String str) {
        this.province = str;
        return this;
    }

    public void setProvinceIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.province = null;
    }

    public String toString() {
        boolean z2;
        StringBuilder sb = new StringBuilder("Location(");
        boolean z3 = false;
        if (isSetContry()) {
            sb.append("contry:");
            String str = this.contry;
            if (str == null) {
                sb.append("null");
            } else {
                sb.append(str);
            }
            z2 = false;
        } else {
            z2 = true;
        }
        if (isSetProvince()) {
            if (!z2) {
                sb.append(", ");
            }
            sb.append("province:");
            String str2 = this.province;
            if (str2 == null) {
                sb.append("null");
            } else {
                sb.append(str2);
            }
            z2 = false;
        }
        if (isSetCity()) {
            if (!z2) {
                sb.append(", ");
            }
            sb.append("city:");
            String str3 = this.city;
            if (str3 == null) {
                sb.append("null");
            } else {
                sb.append(str3);
            }
        } else {
            z3 = z2;
        }
        if (isSetIsp()) {
            if (!z3) {
                sb.append(", ");
            }
            sb.append("isp:");
            String str4 = this.isp;
            if (str4 == null) {
                sb.append("null");
            } else {
                sb.append(str4);
            }
        }
        sb.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
        return sb.toString();
    }

    public void unsetCity() {
        this.city = null;
    }

    public void unsetContry() {
        this.contry = null;
    }

    public void unsetIsp() {
        this.isp = null;
    }

    public void unsetProvince() {
        this.province = null;
    }

    @Override // org.apache.thrift.TBase
    public void write(d dVar) throws TException {
        validate();
        dVar.J(STRUCT_DESC);
        if (this.contry != null && isSetContry()) {
            dVar.w(CONTRY_FIELD_DESC);
            dVar.I(this.contry);
            dVar.x();
        }
        if (this.province != null && isSetProvince()) {
            dVar.w(PROVINCE_FIELD_DESC);
            dVar.I(this.province);
            dVar.x();
        }
        if (this.city != null && isSetCity()) {
            dVar.w(CITY_FIELD_DESC);
            dVar.I(this.city);
            dVar.x();
        }
        if (this.isp != null && isSetIsp()) {
            dVar.w(ISP_FIELD_DESC);
            dVar.I(this.isp);
            dVar.x();
        }
        dVar.y();
        dVar.K();
    }

    public Location(Location location) {
        if (location.isSetContry()) {
            this.contry = location.contry;
        }
        if (location.isSetProvince()) {
            this.province = location.province;
        }
        if (location.isSetCity()) {
            this.city = location.city;
        }
        if (location.isSetIsp()) {
            this.isp = location.isp;
        }
    }

    @Override // java.lang.Comparable
    public int compareTo(Location location) {
        int iG;
        int iG2;
        int iG3;
        int iG4;
        if (!getClass().equals(location.getClass())) {
            return getClass().getName().compareTo(location.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetContry()).compareTo(Boolean.valueOf(location.isSetContry()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetContry() && (iG4 = g0.a.g(this.contry, location.contry)) != 0) {
            return iG4;
        }
        int iCompareTo2 = Boolean.valueOf(isSetProvince()).compareTo(Boolean.valueOf(location.isSetProvince()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (isSetProvince() && (iG3 = g0.a.g(this.province, location.province)) != 0) {
            return iG3;
        }
        int iCompareTo3 = Boolean.valueOf(isSetCity()).compareTo(Boolean.valueOf(location.isSetCity()));
        if (iCompareTo3 != 0) {
            return iCompareTo3;
        }
        if (isSetCity() && (iG2 = g0.a.g(this.city, location.city)) != 0) {
            return iG2;
        }
        int iCompareTo4 = Boolean.valueOf(isSetIsp()).compareTo(Boolean.valueOf(location.isSetIsp()));
        if (iCompareTo4 != 0) {
            return iCompareTo4;
        }
        if (!isSetIsp() || (iG = g0.a.g(this.isp, location.isp)) == 0) {
            return 0;
        }
        return iG;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public Location m9deepCopy() {
        return new Location(this);
    }

    public boolean equals(Location location) {
        if (location == null) {
            return false;
        }
        boolean zIsSetContry = isSetContry();
        boolean zIsSetContry2 = location.isSetContry();
        if ((zIsSetContry || zIsSetContry2) && !(zIsSetContry && zIsSetContry2 && this.contry.equals(location.contry))) {
            return false;
        }
        boolean zIsSetProvince = isSetProvince();
        boolean zIsSetProvince2 = location.isSetProvince();
        if ((zIsSetProvince || zIsSetProvince2) && !(zIsSetProvince && zIsSetProvince2 && this.province.equals(location.province))) {
            return false;
        }
        boolean zIsSetCity = isSetCity();
        boolean zIsSetCity2 = location.isSetCity();
        if ((zIsSetCity || zIsSetCity2) && !(zIsSetCity && zIsSetCity2 && this.city.equals(location.city))) {
            return false;
        }
        boolean zIsSetIsp = isSetIsp();
        boolean zIsSetIsp2 = location.isSetIsp();
        if (zIsSetIsp || zIsSetIsp2) {
            return zIsSetIsp && zIsSetIsp2 && this.isp.equals(location.isp);
        }
        return true;
    }

    public void validate() throws TException {
    }
}
