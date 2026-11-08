package com.xiaomi.common.logger.thrift.mfs;

import com.xiaomi.push.mpcd.Constants;
import java.io.Serializable;
import java.util.BitSet;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.meta_data.MapMetaData;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.protocol.a;
import org.apache.thrift.protocol.c;
import org.apache.thrift.protocol.d;
import org.apache.thrift.protocol.e;
import org.apache.thrift.protocol.g;

/* JADX INFO: loaded from: classes.dex */
public class LandNodeInfo implements TBase<LandNodeInfo, _Fields>, Serializable, Cloneable {
    private static final int __DURATION_ISSET_ID = 2;
    private static final int __FAILED_COUNT_ISSET_ID = 0;
    private static final int __SIZE_ISSET_ID = 3;
    private static final int __SUCCESS_COUNT_ISSET_ID = 1;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    private BitSet __isset_bit_vector;
    private long duration;
    private Map<String, Integer> exp_info;
    private int failed_count;
    private Map<Integer, Integer> http_info;
    private String ip;
    private int size;
    private int success_count;
    private static final g STRUCT_DESC = new g("LandNodeInfo");
    private static final a IP_FIELD_DESC = new a("ip", (byte) 11, 1);
    private static final a FAILED_COUNT_FIELD_DESC = new a("failed_count", (byte) 8, 2);
    private static final a SUCCESS_COUNT_FIELD_DESC = new a("success_count", (byte) 8, 3);
    private static final a DURATION_FIELD_DESC = new a("duration", (byte) 10, 4);
    private static final a SIZE_FIELD_DESC = new a("size", (byte) 8, 5);
    private static final a EXP_INFO_FIELD_DESC = new a("exp_info", (byte) 13, 6);
    private static final a HTTP_INFO_FIELD_DESC = new a("http_info", (byte) 13, 7);

    /* JADX INFO: renamed from: com.xiaomi.common.logger.thrift.mfs.LandNodeInfo$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$xiaomi$common$logger$thrift$mfs$LandNodeInfo$_Fields;

        static {
            int[] iArr = new int[_Fields.values().length];
            $SwitchMap$com$xiaomi$common$logger$thrift$mfs$LandNodeInfo$_Fields = iArr;
            try {
                iArr[_Fields.IP.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$LandNodeInfo$_Fields[_Fields.FAILED_COUNT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$LandNodeInfo$_Fields[_Fields.SUCCESS_COUNT.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$LandNodeInfo$_Fields[_Fields.DURATION.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$LandNodeInfo$_Fields[_Fields.SIZE.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$LandNodeInfo$_Fields[_Fields.EXP_INFO.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$LandNodeInfo$_Fields[_Fields.HTTP_INFO.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
        }
    }

    public enum _Fields {
        IP(1, "ip"),
        FAILED_COUNT(2, "failed_count"),
        SUCCESS_COUNT(3, "success_count"),
        DURATION(4, "duration"),
        SIZE(5, "size"),
        EXP_INFO(6, "exp_info"),
        HTTP_INFO(7, "http_info");

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
            switch (i2) {
                case 1:
                    return IP;
                case 2:
                    return FAILED_COUNT;
                case 3:
                    return SUCCESS_COUNT;
                case 4:
                    return DURATION;
                case 5:
                    return SIZE;
                case 6:
                    return EXP_INFO;
                case 7:
                    return HTTP_INFO;
                default:
                    return null;
            }
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
        enumMap.put(_Fields.IP, new FieldMetaData("ip", (byte) 1, new FieldValueMetaData((byte) 11)));
        enumMap.put(_Fields.FAILED_COUNT, new FieldMetaData("failed_count", (byte) 1, new FieldValueMetaData((byte) 8)));
        enumMap.put(_Fields.SUCCESS_COUNT, new FieldMetaData("success_count", (byte) 1, new FieldValueMetaData((byte) 8)));
        enumMap.put(_Fields.DURATION, new FieldMetaData("duration", (byte) 1, new FieldValueMetaData((byte) 10)));
        enumMap.put(_Fields.SIZE, new FieldMetaData("size", (byte) 1, new FieldValueMetaData((byte) 8)));
        enumMap.put(_Fields.EXP_INFO, new FieldMetaData("exp_info", (byte) 2, new MapMetaData((byte) 13, new FieldValueMetaData((byte) 11), new FieldValueMetaData((byte) 8))));
        enumMap.put(_Fields.HTTP_INFO, new FieldMetaData("http_info", (byte) 2, new MapMetaData((byte) 13, new FieldValueMetaData((byte) 8), new FieldValueMetaData((byte) 8))));
        Map<_Fields, FieldMetaData> mapUnmodifiableMap = Collections.unmodifiableMap(enumMap);
        metaDataMap = mapUnmodifiableMap;
        FieldMetaData.b(LandNodeInfo.class, mapUnmodifiableMap);
    }

    public LandNodeInfo() {
        this.__isset_bit_vector = new BitSet(4);
    }

    public void clear() {
        this.ip = null;
        setFailed_countIsSet(false);
        this.failed_count = 0;
        setSuccess_countIsSet(false);
        this.success_count = 0;
        setDurationIsSet(false);
        this.duration = 0L;
        setSizeIsSet(false);
        this.size = 0;
        this.exp_info = null;
        this.http_info = null;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof LandNodeInfo)) {
            return equals((LandNodeInfo) obj);
        }
        return false;
    }

    public _Fields fieldForId(int i2) {
        return _Fields.findByThriftId(i2);
    }

    public long getDuration() {
        return this.duration;
    }

    public Map<String, Integer> getExp_info() {
        return this.exp_info;
    }

    public int getExp_infoSize() {
        Map<String, Integer> map = this.exp_info;
        if (map == null) {
            return 0;
        }
        return map.size();
    }

    public int getFailed_count() {
        return this.failed_count;
    }

    public Object getFieldValue(_Fields _fields) {
        switch (AnonymousClass1.$SwitchMap$com$xiaomi$common$logger$thrift$mfs$LandNodeInfo$_Fields[_fields.ordinal()]) {
            case 1:
                return getIp();
            case 2:
                return new Integer(getFailed_count());
            case 3:
                return new Integer(getSuccess_count());
            case 4:
                return new Long(getDuration());
            case 5:
                return new Integer(getSize());
            case 6:
                return getExp_info();
            case 7:
                return getHttp_info();
            default:
                throw new IllegalStateException();
        }
    }

    public Map<Integer, Integer> getHttp_info() {
        return this.http_info;
    }

    public int getHttp_infoSize() {
        Map<Integer, Integer> map = this.http_info;
        if (map == null) {
            return 0;
        }
        return map.size();
    }

    public String getIp() {
        return this.ip;
    }

    public int getSize() {
        return this.size;
    }

    public int getSuccess_count() {
        return this.success_count;
    }

    public int hashCode() {
        return 0;
    }

    public boolean isSet(_Fields _fields) {
        if (_fields == null) {
            throw new IllegalArgumentException();
        }
        switch (AnonymousClass1.$SwitchMap$com$xiaomi$common$logger$thrift$mfs$LandNodeInfo$_Fields[_fields.ordinal()]) {
            case 1:
                return isSetIp();
            case 2:
                return isSetFailed_count();
            case 3:
                return isSetSuccess_count();
            case 4:
                return isSetDuration();
            case 5:
                return isSetSize();
            case 6:
                return isSetExp_info();
            case 7:
                return isSetHttp_info();
            default:
                throw new IllegalStateException();
        }
    }

    public boolean isSetDuration() {
        return this.__isset_bit_vector.get(2);
    }

    public boolean isSetExp_info() {
        return this.exp_info != null;
    }

    public boolean isSetFailed_count() {
        return this.__isset_bit_vector.get(0);
    }

    public boolean isSetHttp_info() {
        return this.http_info != null;
    }

    public boolean isSetIp() {
        return this.ip != null;
    }

    public boolean isSetSize() {
        return this.__isset_bit_vector.get(3);
    }

    public boolean isSetSuccess_count() {
        return this.__isset_bit_vector.get(1);
    }

    public void putToExp_info(String str, int i2) {
        if (this.exp_info == null) {
            this.exp_info = new HashMap();
        }
        this.exp_info.put(str, Integer.valueOf(i2));
    }

    public void putToHttp_info(int i2, int i3) {
        if (this.http_info == null) {
            this.http_info = new HashMap();
        }
        this.http_info.put(Integer.valueOf(i2), Integer.valueOf(i3));
    }

    @Override // org.apache.thrift.TBase
    public void read(d dVar) throws TException {
        dVar.q();
        while (true) {
            a aVarE = dVar.e();
            byte b2 = aVarE.f2150b;
            if (b2 == 0) {
                dVar.r();
                if (!isSetFailed_count()) {
                    throw new TProtocolException("Required field 'failed_count' was not found in serialized data! Struct: " + toString());
                }
                if (!isSetSuccess_count()) {
                    throw new TProtocolException("Required field 'success_count' was not found in serialized data! Struct: " + toString());
                }
                if (!isSetDuration()) {
                    throw new TProtocolException("Required field 'duration' was not found in serialized data! Struct: " + toString());
                }
                if (isSetSize()) {
                    validate();
                    return;
                }
                throw new TProtocolException("Required field 'size' was not found in serialized data! Struct: " + toString());
            }
            int i2 = 0;
            switch (aVarE.f2151c) {
                case 1:
                    if (b2 != 11) {
                        e.a(dVar, b2);
                    } else {
                        this.ip = dVar.p();
                    }
                    break;
                case 2:
                    if (b2 != 8) {
                        e.a(dVar, b2);
                    } else {
                        this.failed_count = dVar.h();
                        setFailed_countIsSet(true);
                    }
                    break;
                case 3:
                    if (b2 != 8) {
                        e.a(dVar, b2);
                    } else {
                        this.success_count = dVar.h();
                        setSuccess_countIsSet(true);
                    }
                    break;
                case 4:
                    if (b2 != 10) {
                        e.a(dVar, b2);
                    } else {
                        this.duration = dVar.i();
                        setDurationIsSet(true);
                    }
                    break;
                case 5:
                    if (b2 != 8) {
                        e.a(dVar, b2);
                    } else {
                        this.size = dVar.h();
                        setSizeIsSet(true);
                    }
                    break;
                case 6:
                    if (b2 != 13) {
                        e.a(dVar, b2);
                    } else {
                        c cVarL = dVar.l();
                        this.exp_info = new HashMap(cVarL.f2156c * 2);
                        while (i2 < cVarL.f2156c) {
                            this.exp_info.put(dVar.p(), Integer.valueOf(dVar.h()));
                            i2++;
                        }
                        dVar.m();
                    }
                    break;
                case 7:
                    if (b2 != 13) {
                        e.a(dVar, b2);
                    } else {
                        c cVarL2 = dVar.l();
                        this.http_info = new HashMap(cVarL2.f2156c * 2);
                        while (i2 < cVarL2.f2156c) {
                            this.http_info.put(Integer.valueOf(dVar.h()), Integer.valueOf(dVar.h()));
                            i2++;
                        }
                        dVar.m();
                    }
                    break;
                default:
                    e.a(dVar, b2);
                    break;
            }
            dVar.f();
        }
    }

    public LandNodeInfo setDuration(long j2) {
        this.duration = j2;
        setDurationIsSet(true);
        return this;
    }

    public void setDurationIsSet(boolean z2) {
        this.__isset_bit_vector.set(2, z2);
    }

    public LandNodeInfo setExp_info(Map<String, Integer> map) {
        this.exp_info = map;
        return this;
    }

    public void setExp_infoIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.exp_info = null;
    }

    public LandNodeInfo setFailed_count(int i2) {
        this.failed_count = i2;
        setFailed_countIsSet(true);
        return this;
    }

    public void setFailed_countIsSet(boolean z2) {
        this.__isset_bit_vector.set(0, z2);
    }

    public void setFieldValue(_Fields _fields, Object obj) {
        switch (AnonymousClass1.$SwitchMap$com$xiaomi$common$logger$thrift$mfs$LandNodeInfo$_Fields[_fields.ordinal()]) {
            case 1:
                if (obj != null) {
                    setIp((String) obj);
                } else {
                    unsetIp();
                }
                break;
            case 2:
                if (obj != null) {
                    setFailed_count(((Integer) obj).intValue());
                } else {
                    unsetFailed_count();
                }
                break;
            case 3:
                if (obj != null) {
                    setSuccess_count(((Integer) obj).intValue());
                } else {
                    unsetSuccess_count();
                }
                break;
            case 4:
                if (obj != null) {
                    setDuration(((Long) obj).longValue());
                } else {
                    unsetDuration();
                }
                break;
            case 5:
                if (obj != null) {
                    setSize(((Integer) obj).intValue());
                } else {
                    unsetSize();
                }
                break;
            case 6:
                if (obj != null) {
                    setExp_info((Map) obj);
                } else {
                    unsetExp_info();
                }
                break;
            case 7:
                if (obj != null) {
                    setHttp_info((Map) obj);
                } else {
                    unsetHttp_info();
                }
                break;
        }
    }

    public LandNodeInfo setHttp_info(Map<Integer, Integer> map) {
        this.http_info = map;
        return this;
    }

    public void setHttp_infoIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.http_info = null;
    }

    public LandNodeInfo setIp(String str) {
        this.ip = str;
        return this;
    }

    public void setIpIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.ip = null;
    }

    public LandNodeInfo setSize(int i2) {
        this.size = i2;
        setSizeIsSet(true);
        return this;
    }

    public void setSizeIsSet(boolean z2) {
        this.__isset_bit_vector.set(3, z2);
    }

    public LandNodeInfo setSuccess_count(int i2) {
        this.success_count = i2;
        setSuccess_countIsSet(true);
        return this;
    }

    public void setSuccess_countIsSet(boolean z2) {
        this.__isset_bit_vector.set(1, z2);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("LandNodeInfo(");
        sb.append("ip:");
        String str = this.ip;
        if (str == null) {
            sb.append("null");
        } else {
            sb.append(str);
        }
        sb.append(", ");
        sb.append("failed_count:");
        sb.append(this.failed_count);
        sb.append(", ");
        sb.append("success_count:");
        sb.append(this.success_count);
        sb.append(", ");
        sb.append("duration:");
        sb.append(this.duration);
        sb.append(", ");
        sb.append("size:");
        sb.append(this.size);
        if (isSetExp_info()) {
            sb.append(", ");
            sb.append("exp_info:");
            Map<String, Integer> map = this.exp_info;
            if (map == null) {
                sb.append("null");
            } else {
                sb.append(map);
            }
        }
        if (isSetHttp_info()) {
            sb.append(", ");
            sb.append("http_info:");
            Map<Integer, Integer> map2 = this.http_info;
            if (map2 == null) {
                sb.append("null");
            } else {
                sb.append(map2);
            }
        }
        sb.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
        return sb.toString();
    }

    public void unsetDuration() {
        this.__isset_bit_vector.clear(2);
    }

    public void unsetExp_info() {
        this.exp_info = null;
    }

    public void unsetFailed_count() {
        this.__isset_bit_vector.clear(0);
    }

    public void unsetHttp_info() {
        this.http_info = null;
    }

    public void unsetIp() {
        this.ip = null;
    }

    public void unsetSize() {
        this.__isset_bit_vector.clear(3);
    }

    public void unsetSuccess_count() {
        this.__isset_bit_vector.clear(1);
    }

    public void validate() throws TException {
        if (this.ip != null) {
            return;
        }
        throw new TProtocolException("Required field 'ip' was not present! Struct: " + toString());
    }

    @Override // org.apache.thrift.TBase
    public void write(d dVar) throws TException {
        validate();
        dVar.J(STRUCT_DESC);
        if (this.ip != null) {
            dVar.w(IP_FIELD_DESC);
            dVar.I(this.ip);
            dVar.x();
        }
        dVar.w(FAILED_COUNT_FIELD_DESC);
        dVar.A(this.failed_count);
        dVar.x();
        dVar.w(SUCCESS_COUNT_FIELD_DESC);
        dVar.A(this.success_count);
        dVar.x();
        dVar.w(DURATION_FIELD_DESC);
        dVar.B(this.duration);
        dVar.x();
        dVar.w(SIZE_FIELD_DESC);
        dVar.A(this.size);
        dVar.x();
        if (this.exp_info != null && isSetExp_info()) {
            dVar.w(EXP_INFO_FIELD_DESC);
            dVar.E(new c((byte) 11, (byte) 8, this.exp_info.size()));
            for (Map.Entry<String, Integer> entry : this.exp_info.entrySet()) {
                dVar.I(entry.getKey());
                dVar.A(entry.getValue().intValue());
            }
            dVar.F();
            dVar.x();
        }
        if (this.http_info != null && isSetHttp_info()) {
            dVar.w(HTTP_INFO_FIELD_DESC);
            dVar.E(new c((byte) 8, (byte) 8, this.http_info.size()));
            for (Map.Entry<Integer, Integer> entry2 : this.http_info.entrySet()) {
                dVar.A(entry2.getKey().intValue());
                dVar.A(entry2.getValue().intValue());
            }
            dVar.F();
            dVar.x();
        }
        dVar.y();
        dVar.K();
    }

    @Override // java.lang.Comparable
    public int compareTo(LandNodeInfo landNodeInfo) {
        int i2;
        int i3;
        int iD;
        int iE;
        int iD2;
        int iD3;
        int iG;
        if (!getClass().equals(landNodeInfo.getClass())) {
            return getClass().getName().compareTo(landNodeInfo.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetIp()).compareTo(Boolean.valueOf(landNodeInfo.isSetIp()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetIp() && (iG = g0.a.g(this.ip, landNodeInfo.ip)) != 0) {
            return iG;
        }
        int iCompareTo2 = Boolean.valueOf(isSetFailed_count()).compareTo(Boolean.valueOf(landNodeInfo.isSetFailed_count()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (isSetFailed_count() && (iD3 = g0.a.d(this.failed_count, landNodeInfo.failed_count)) != 0) {
            return iD3;
        }
        int iCompareTo3 = Boolean.valueOf(isSetSuccess_count()).compareTo(Boolean.valueOf(landNodeInfo.isSetSuccess_count()));
        if (iCompareTo3 != 0) {
            return iCompareTo3;
        }
        if (isSetSuccess_count() && (iD2 = g0.a.d(this.success_count, landNodeInfo.success_count)) != 0) {
            return iD2;
        }
        int iCompareTo4 = Boolean.valueOf(isSetDuration()).compareTo(Boolean.valueOf(landNodeInfo.isSetDuration()));
        if (iCompareTo4 != 0) {
            return iCompareTo4;
        }
        if (isSetDuration() && (iE = g0.a.e(this.duration, landNodeInfo.duration)) != 0) {
            return iE;
        }
        int iCompareTo5 = Boolean.valueOf(isSetSize()).compareTo(Boolean.valueOf(landNodeInfo.isSetSize()));
        if (iCompareTo5 != 0) {
            return iCompareTo5;
        }
        if (isSetSize() && (iD = g0.a.d(this.size, landNodeInfo.size)) != 0) {
            return iD;
        }
        int iCompareTo6 = Boolean.valueOf(isSetExp_info()).compareTo(Boolean.valueOf(landNodeInfo.isSetExp_info()));
        if (iCompareTo6 != 0) {
            return iCompareTo6;
        }
        if (isSetExp_info() && (i3 = g0.a.i(this.exp_info, landNodeInfo.exp_info)) != 0) {
            return i3;
        }
        int iCompareTo7 = Boolean.valueOf(isSetHttp_info()).compareTo(Boolean.valueOf(landNodeInfo.isSetHttp_info()));
        if (iCompareTo7 != 0) {
            return iCompareTo7;
        }
        if (!isSetHttp_info() || (i2 = g0.a.i(this.http_info, landNodeInfo.http_info)) == 0) {
            return 0;
        }
        return i2;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public LandNodeInfo m8deepCopy() {
        return new LandNodeInfo(this);
    }

    public LandNodeInfo(String str, int i2, int i3, long j2, int i4) {
        this();
        this.ip = str;
        this.failed_count = i2;
        setFailed_countIsSet(true);
        this.success_count = i3;
        setSuccess_countIsSet(true);
        this.duration = j2;
        setDurationIsSet(true);
        this.size = i4;
        setSizeIsSet(true);
    }

    public boolean equals(LandNodeInfo landNodeInfo) {
        if (landNodeInfo == null) {
            return false;
        }
        boolean zIsSetIp = isSetIp();
        boolean zIsSetIp2 = landNodeInfo.isSetIp();
        if (((zIsSetIp || zIsSetIp2) && (!zIsSetIp || !zIsSetIp2 || !this.ip.equals(landNodeInfo.ip))) || this.failed_count != landNodeInfo.failed_count || this.success_count != landNodeInfo.success_count || this.duration != landNodeInfo.duration || this.size != landNodeInfo.size) {
            return false;
        }
        boolean zIsSetExp_info = isSetExp_info();
        boolean zIsSetExp_info2 = landNodeInfo.isSetExp_info();
        if ((zIsSetExp_info || zIsSetExp_info2) && !(zIsSetExp_info && zIsSetExp_info2 && this.exp_info.equals(landNodeInfo.exp_info))) {
            return false;
        }
        boolean zIsSetHttp_info = isSetHttp_info();
        boolean zIsSetHttp_info2 = landNodeInfo.isSetHttp_info();
        if (zIsSetHttp_info || zIsSetHttp_info2) {
            return zIsSetHttp_info && zIsSetHttp_info2 && this.http_info.equals(landNodeInfo.http_info);
        }
        return true;
    }

    public LandNodeInfo(LandNodeInfo landNodeInfo) {
        BitSet bitSet = new BitSet(4);
        this.__isset_bit_vector = bitSet;
        bitSet.clear();
        this.__isset_bit_vector.or(landNodeInfo.__isset_bit_vector);
        if (landNodeInfo.isSetIp()) {
            this.ip = landNodeInfo.ip;
        }
        this.failed_count = landNodeInfo.failed_count;
        this.success_count = landNodeInfo.success_count;
        this.duration = landNodeInfo.duration;
        this.size = landNodeInfo.size;
        if (landNodeInfo.isSetExp_info()) {
            HashMap map = new HashMap();
            for (Map.Entry<String, Integer> entry : landNodeInfo.exp_info.entrySet()) {
                map.put(entry.getKey(), entry.getValue());
            }
            this.exp_info = map;
        }
        if (landNodeInfo.isSetHttp_info()) {
            HashMap map2 = new HashMap();
            for (Map.Entry<Integer, Integer> entry2 : landNodeInfo.http_info.entrySet()) {
                map2.put(entry2.getKey(), entry2.getValue());
            }
            this.http_info = map2;
        }
    }
}
