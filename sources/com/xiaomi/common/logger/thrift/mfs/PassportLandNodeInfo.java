package com.xiaomi.common.logger.thrift.mfs;

import com.miui.powerkeeper.powerchecker.NightStandbyRecord;
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
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.protocol.a;
import org.apache.thrift.protocol.d;
import org.apache.thrift.protocol.e;
import org.apache.thrift.protocol.g;

/* JADX INFO: loaded from: classes.dex */
public class PassportLandNodeInfo implements TBase<PassportLandNodeInfo, _Fields>, Serializable, Cloneable {
    private static final int __EID_ISSET_ID = 1;
    private static final int __IP_ISSET_ID = 0;
    private static final int __RT_ISSET_ID = 2;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    private BitSet __isset_bit_vector;
    private int eid;
    private int ip;
    private int rt;
    private static final g STRUCT_DESC = new g("PassportLandNodeInfo");
    private static final a IP_FIELD_DESC = new a("ip", (byte) 8, 1);
    private static final a EID_FIELD_DESC = new a("eid", (byte) 8, 2);
    private static final a RT_FIELD_DESC = new a(NightStandbyRecord.KEY_RECORD_TIME, (byte) 8, 3);

    /* JADX INFO: renamed from: com.xiaomi.common.logger.thrift.mfs.PassportLandNodeInfo$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$xiaomi$common$logger$thrift$mfs$PassportLandNodeInfo$_Fields;

        static {
            int[] iArr = new int[_Fields.values().length];
            $SwitchMap$com$xiaomi$common$logger$thrift$mfs$PassportLandNodeInfo$_Fields = iArr;
            try {
                iArr[_Fields.IP.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$PassportLandNodeInfo$_Fields[_Fields.EID.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$PassportLandNodeInfo$_Fields[_Fields.RT.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    public enum _Fields {
        IP(1, "ip"),
        EID(2, "eid"),
        RT(3, NightStandbyRecord.KEY_RECORD_TIME);

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
                return IP;
            }
            if (i2 == 2) {
                return EID;
            }
            if (i2 != 3) {
                return null;
            }
            return RT;
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
        enumMap.put(_Fields.IP, new FieldMetaData("ip", (byte) 1, new FieldValueMetaData((byte) 8)));
        enumMap.put(_Fields.EID, new FieldMetaData("eid", (byte) 1, new FieldValueMetaData((byte) 8)));
        enumMap.put(_Fields.RT, new FieldMetaData(NightStandbyRecord.KEY_RECORD_TIME, (byte) 1, new FieldValueMetaData((byte) 8)));
        Map<_Fields, FieldMetaData> mapUnmodifiableMap = Collections.unmodifiableMap(enumMap);
        metaDataMap = mapUnmodifiableMap;
        FieldMetaData.b(PassportLandNodeInfo.class, mapUnmodifiableMap);
    }

    public PassportLandNodeInfo() {
        this.__isset_bit_vector = new BitSet(3);
    }

    public void clear() {
        setIpIsSet(false);
        this.ip = 0;
        setEidIsSet(false);
        this.eid = 0;
        setRtIsSet(false);
        this.rt = 0;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof PassportLandNodeInfo)) {
            return equals((PassportLandNodeInfo) obj);
        }
        return false;
    }

    public _Fields fieldForId(int i2) {
        return _Fields.findByThriftId(i2);
    }

    public int getEid() {
        return this.eid;
    }

    public Object getFieldValue(_Fields _fields) {
        int i2 = AnonymousClass1.$SwitchMap$com$xiaomi$common$logger$thrift$mfs$PassportLandNodeInfo$_Fields[_fields.ordinal()];
        if (i2 == 1) {
            return new Integer(getIp());
        }
        if (i2 == 2) {
            return new Integer(getEid());
        }
        if (i2 == 3) {
            return new Integer(getRt());
        }
        throw new IllegalStateException();
    }

    public int getIp() {
        return this.ip;
    }

    public int getRt() {
        return this.rt;
    }

    public int hashCode() {
        return 0;
    }

    public boolean isSet(_Fields _fields) {
        if (_fields == null) {
            throw new IllegalArgumentException();
        }
        int i2 = AnonymousClass1.$SwitchMap$com$xiaomi$common$logger$thrift$mfs$PassportLandNodeInfo$_Fields[_fields.ordinal()];
        if (i2 == 1) {
            return isSetIp();
        }
        if (i2 == 2) {
            return isSetEid();
        }
        if (i2 == 3) {
            return isSetRt();
        }
        throw new IllegalStateException();
    }

    public boolean isSetEid() {
        return this.__isset_bit_vector.get(1);
    }

    public boolean isSetIp() {
        return this.__isset_bit_vector.get(0);
    }

    public boolean isSetRt() {
        return this.__isset_bit_vector.get(2);
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
                    } else if (b2 == 8) {
                        this.rt = dVar.h();
                        setRtIsSet(true);
                    } else {
                        e.a(dVar, b2);
                    }
                } else if (b2 == 8) {
                    this.eid = dVar.h();
                    setEidIsSet(true);
                } else {
                    e.a(dVar, b2);
                }
            } else if (b2 == 8) {
                this.ip = dVar.h();
                setIpIsSet(true);
            } else {
                e.a(dVar, b2);
            }
            dVar.f();
        }
        dVar.r();
        if (!isSetIp()) {
            throw new TProtocolException("Required field 'ip' was not found in serialized data! Struct: " + toString());
        }
        if (!isSetEid()) {
            throw new TProtocolException("Required field 'eid' was not found in serialized data! Struct: " + toString());
        }
        if (isSetRt()) {
            validate();
            return;
        }
        throw new TProtocolException("Required field 'rt' was not found in serialized data! Struct: " + toString());
    }

    public PassportLandNodeInfo setEid(int i2) {
        this.eid = i2;
        setEidIsSet(true);
        return this;
    }

    public void setEidIsSet(boolean z2) {
        this.__isset_bit_vector.set(1, z2);
    }

    public void setFieldValue(_Fields _fields, Object obj) {
        int i2 = AnonymousClass1.$SwitchMap$com$xiaomi$common$logger$thrift$mfs$PassportLandNodeInfo$_Fields[_fields.ordinal()];
        if (i2 == 1) {
            if (obj == null) {
                unsetIp();
                return;
            } else {
                setIp(((Integer) obj).intValue());
                return;
            }
        }
        if (i2 == 2) {
            if (obj == null) {
                unsetEid();
                return;
            } else {
                setEid(((Integer) obj).intValue());
                return;
            }
        }
        if (i2 != 3) {
            return;
        }
        if (obj == null) {
            unsetRt();
        } else {
            setRt(((Integer) obj).intValue());
        }
    }

    public PassportLandNodeInfo setIp(int i2) {
        this.ip = i2;
        setIpIsSet(true);
        return this;
    }

    public void setIpIsSet(boolean z2) {
        this.__isset_bit_vector.set(0, z2);
    }

    public PassportLandNodeInfo setRt(int i2) {
        this.rt = i2;
        setRtIsSet(true);
        return this;
    }

    public void setRtIsSet(boolean z2) {
        this.__isset_bit_vector.set(2, z2);
    }

    public String toString() {
        return "PassportLandNodeInfo(ip:" + this.ip + ", eid:" + this.eid + ", rt:" + this.rt + Constants.SEPARATOR_RIGHT_PARENTESIS;
    }

    public void unsetEid() {
        this.__isset_bit_vector.clear(1);
    }

    public void unsetIp() {
        this.__isset_bit_vector.clear(0);
    }

    public void unsetRt() {
        this.__isset_bit_vector.clear(2);
    }

    @Override // org.apache.thrift.TBase
    public void write(d dVar) throws TException {
        validate();
        dVar.J(STRUCT_DESC);
        dVar.w(IP_FIELD_DESC);
        dVar.A(this.ip);
        dVar.x();
        dVar.w(EID_FIELD_DESC);
        dVar.A(this.eid);
        dVar.x();
        dVar.w(RT_FIELD_DESC);
        dVar.A(this.rt);
        dVar.x();
        dVar.y();
        dVar.K();
    }

    @Override // java.lang.Comparable
    public int compareTo(PassportLandNodeInfo passportLandNodeInfo) {
        int iD;
        int iD2;
        int iD3;
        if (!getClass().equals(passportLandNodeInfo.getClass())) {
            return getClass().getName().compareTo(passportLandNodeInfo.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetIp()).compareTo(Boolean.valueOf(passportLandNodeInfo.isSetIp()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetIp() && (iD3 = g0.a.d(this.ip, passportLandNodeInfo.ip)) != 0) {
            return iD3;
        }
        int iCompareTo2 = Boolean.valueOf(isSetEid()).compareTo(Boolean.valueOf(passportLandNodeInfo.isSetEid()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (isSetEid() && (iD2 = g0.a.d(this.eid, passportLandNodeInfo.eid)) != 0) {
            return iD2;
        }
        int iCompareTo3 = Boolean.valueOf(isSetRt()).compareTo(Boolean.valueOf(passportLandNodeInfo.isSetRt()));
        if (iCompareTo3 != 0) {
            return iCompareTo3;
        }
        if (!isSetRt() || (iD = g0.a.d(this.rt, passportLandNodeInfo.rt)) == 0) {
            return 0;
        }
        return iD;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public PassportLandNodeInfo m13deepCopy() {
        return new PassportLandNodeInfo(this);
    }

    public PassportLandNodeInfo(int i2, int i3, int i4) {
        this();
        this.ip = i2;
        setIpIsSet(true);
        this.eid = i3;
        setEidIsSet(true);
        this.rt = i4;
        setRtIsSet(true);
    }

    public boolean equals(PassportLandNodeInfo passportLandNodeInfo) {
        return passportLandNodeInfo != null && this.ip == passportLandNodeInfo.ip && this.eid == passportLandNodeInfo.eid && this.rt == passportLandNodeInfo.rt;
    }

    public PassportLandNodeInfo(PassportLandNodeInfo passportLandNodeInfo) {
        BitSet bitSet = new BitSet(3);
        this.__isset_bit_vector = bitSet;
        bitSet.clear();
        this.__isset_bit_vector.or(passportLandNodeInfo.__isset_bit_vector);
        this.ip = passportLandNodeInfo.ip;
        this.eid = passportLandNodeInfo.eid;
        this.rt = passportLandNodeInfo.rt;
    }

    public void validate() throws TException {
    }
}
