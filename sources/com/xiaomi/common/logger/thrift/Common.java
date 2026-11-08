package com.xiaomi.common.logger.thrift;

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
import org.apache.thrift.protocol.a;
import org.apache.thrift.protocol.d;
import org.apache.thrift.protocol.e;
import org.apache.thrift.protocol.g;

/* JADX INFO: loaded from: classes.dex */
public class Common implements TBase<Common, _Fields>, Serializable, Cloneable {
    private static final int __UUID_ISSET_ID = 0;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    private BitSet __isset_bit_vector;
    public String clientIp;
    public String serverHost;
    public String serverIp;
    public String time;
    public long uuid;
    private static final g STRUCT_DESC = new g("Common");
    private static final a UUID_FIELD_DESC = new a("uuid", (byte) 10, 1);
    private static final a TIME_FIELD_DESC = new a("time", (byte) 11, 2);
    private static final a CLIENT_IP_FIELD_DESC = new a("clientIp", (byte) 11, 3);
    private static final a SERVER_IP_FIELD_DESC = new a("serverIp", (byte) 11, 4);
    private static final a SERVER_HOST_FIELD_DESC = new a("serverHost", (byte) 11, 5);

    /* JADX INFO: renamed from: com.xiaomi.common.logger.thrift.Common$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$xiaomi$common$logger$thrift$Common$_Fields;

        static {
            int[] iArr = new int[_Fields.values().length];
            $SwitchMap$com$xiaomi$common$logger$thrift$Common$_Fields = iArr;
            try {
                iArr[_Fields.UUID.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$Common$_Fields[_Fields.TIME.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$Common$_Fields[_Fields.CLIENT_IP.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$Common$_Fields[_Fields.SERVER_IP.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$Common$_Fields[_Fields.SERVER_HOST.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
        }
    }

    public enum _Fields {
        UUID(1, "uuid"),
        TIME(2, "time"),
        CLIENT_IP(3, "clientIp"),
        SERVER_IP(4, "serverIp"),
        SERVER_HOST(5, "serverHost");

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
                return UUID;
            }
            if (i2 == 2) {
                return TIME;
            }
            if (i2 == 3) {
                return CLIENT_IP;
            }
            if (i2 == 4) {
                return SERVER_IP;
            }
            if (i2 != 5) {
                return null;
            }
            return SERVER_HOST;
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
        enumMap.put(_Fields.UUID, new FieldMetaData("uuid", (byte) 2, new FieldValueMetaData((byte) 10)));
        enumMap.put(_Fields.TIME, new FieldMetaData("time", (byte) 2, new FieldValueMetaData((byte) 11)));
        enumMap.put(_Fields.CLIENT_IP, new FieldMetaData("clientIp", (byte) 2, new FieldValueMetaData((byte) 11)));
        enumMap.put(_Fields.SERVER_IP, new FieldMetaData("serverIp", (byte) 2, new FieldValueMetaData((byte) 11)));
        enumMap.put(_Fields.SERVER_HOST, new FieldMetaData("serverHost", (byte) 2, new FieldValueMetaData((byte) 11)));
        Map<_Fields, FieldMetaData> mapUnmodifiableMap = Collections.unmodifiableMap(enumMap);
        metaDataMap = mapUnmodifiableMap;
        FieldMetaData.b(Common.class, mapUnmodifiableMap);
    }

    public Common() {
        this.__isset_bit_vector = new BitSet(1);
        this.uuid = 0L;
        this.time = "";
        this.clientIp = "";
        this.serverIp = "";
        this.serverHost = "";
    }

    public void clear() {
        this.uuid = 0L;
        this.time = "";
        this.clientIp = "";
        this.serverIp = "";
        this.serverHost = "";
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof Common)) {
            return equals((Common) obj);
        }
        return false;
    }

    public _Fields fieldForId(int i2) {
        return _Fields.findByThriftId(i2);
    }

    public String getClientIp() {
        return this.clientIp;
    }

    public Object getFieldValue(_Fields _fields) {
        int i2 = AnonymousClass1.$SwitchMap$com$xiaomi$common$logger$thrift$Common$_Fields[_fields.ordinal()];
        if (i2 == 1) {
            return new Long(getUuid());
        }
        if (i2 == 2) {
            return getTime();
        }
        if (i2 == 3) {
            return getClientIp();
        }
        if (i2 == 4) {
            return getServerIp();
        }
        if (i2 == 5) {
            return getServerHost();
        }
        throw new IllegalStateException();
    }

    public String getServerHost() {
        return this.serverHost;
    }

    public String getServerIp() {
        return this.serverIp;
    }

    public String getTime() {
        return this.time;
    }

    public long getUuid() {
        return this.uuid;
    }

    public int hashCode() {
        return 0;
    }

    public boolean isSet(_Fields _fields) {
        if (_fields == null) {
            throw new IllegalArgumentException();
        }
        int i2 = AnonymousClass1.$SwitchMap$com$xiaomi$common$logger$thrift$Common$_Fields[_fields.ordinal()];
        if (i2 == 1) {
            return isSetUuid();
        }
        if (i2 == 2) {
            return isSetTime();
        }
        if (i2 == 3) {
            return isSetClientIp();
        }
        if (i2 == 4) {
            return isSetServerIp();
        }
        if (i2 == 5) {
            return isSetServerHost();
        }
        throw new IllegalStateException();
    }

    public boolean isSetClientIp() {
        return this.clientIp != null;
    }

    public boolean isSetServerHost() {
        return this.serverHost != null;
    }

    public boolean isSetServerIp() {
        return this.serverIp != null;
    }

    public boolean isSetTime() {
        return this.time != null;
    }

    public boolean isSetUuid() {
        return this.__isset_bit_vector.get(0);
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
                            if (s2 != 5) {
                                e.a(dVar, b2);
                            } else if (b2 == 11) {
                                this.serverHost = dVar.p();
                            } else {
                                e.a(dVar, b2);
                            }
                        } else if (b2 == 11) {
                            this.serverIp = dVar.p();
                        } else {
                            e.a(dVar, b2);
                        }
                    } else if (b2 == 11) {
                        this.clientIp = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                } else if (b2 == 11) {
                    this.time = dVar.p();
                } else {
                    e.a(dVar, b2);
                }
            } else if (b2 == 10) {
                this.uuid = dVar.i();
                setUuidIsSet(true);
            } else {
                e.a(dVar, b2);
            }
            dVar.f();
        }
    }

    public Common setClientIp(String str) {
        this.clientIp = str;
        return this;
    }

    public void setClientIpIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.clientIp = null;
    }

    public void setFieldValue(_Fields _fields, Object obj) {
        int i2 = AnonymousClass1.$SwitchMap$com$xiaomi$common$logger$thrift$Common$_Fields[_fields.ordinal()];
        if (i2 == 1) {
            if (obj == null) {
                unsetUuid();
                return;
            } else {
                setUuid(((Long) obj).longValue());
                return;
            }
        }
        if (i2 == 2) {
            if (obj == null) {
                unsetTime();
                return;
            } else {
                setTime((String) obj);
                return;
            }
        }
        if (i2 == 3) {
            if (obj == null) {
                unsetClientIp();
                return;
            } else {
                setClientIp((String) obj);
                return;
            }
        }
        if (i2 == 4) {
            if (obj == null) {
                unsetServerIp();
                return;
            } else {
                setServerIp((String) obj);
                return;
            }
        }
        if (i2 != 5) {
            return;
        }
        if (obj == null) {
            unsetServerHost();
        } else {
            setServerHost((String) obj);
        }
    }

    public Common setServerHost(String str) {
        this.serverHost = str;
        return this;
    }

    public void setServerHostIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.serverHost = null;
    }

    public Common setServerIp(String str) {
        this.serverIp = str;
        return this;
    }

    public void setServerIpIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.serverIp = null;
    }

    public Common setTime(String str) {
        this.time = str;
        return this;
    }

    public void setTimeIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.time = null;
    }

    public Common setUuid(long j2) {
        this.uuid = j2;
        setUuidIsSet(true);
        return this;
    }

    public void setUuidIsSet(boolean z2) {
        this.__isset_bit_vector.set(0, z2);
    }

    public String toString() {
        boolean z2;
        StringBuilder sb = new StringBuilder("Common(");
        boolean z3 = false;
        if (isSetUuid()) {
            sb.append("uuid:");
            sb.append(this.uuid);
            z2 = false;
        } else {
            z2 = true;
        }
        if (isSetTime()) {
            if (!z2) {
                sb.append(", ");
            }
            sb.append("time:");
            String str = this.time;
            if (str == null) {
                sb.append("null");
            } else {
                sb.append(str);
            }
            z2 = false;
        }
        if (isSetClientIp()) {
            if (!z2) {
                sb.append(", ");
            }
            sb.append("clientIp:");
            String str2 = this.clientIp;
            if (str2 == null) {
                sb.append("null");
            } else {
                sb.append(str2);
            }
            z2 = false;
        }
        if (isSetServerIp()) {
            if (!z2) {
                sb.append(", ");
            }
            sb.append("serverIp:");
            String str3 = this.serverIp;
            if (str3 == null) {
                sb.append("null");
            } else {
                sb.append(str3);
            }
        } else {
            z3 = z2;
        }
        if (isSetServerHost()) {
            if (!z3) {
                sb.append(", ");
            }
            sb.append("serverHost:");
            String str4 = this.serverHost;
            if (str4 == null) {
                sb.append("null");
            } else {
                sb.append(str4);
            }
        }
        sb.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
        return sb.toString();
    }

    public void unsetClientIp() {
        this.clientIp = null;
    }

    public void unsetServerHost() {
        this.serverHost = null;
    }

    public void unsetServerIp() {
        this.serverIp = null;
    }

    public void unsetTime() {
        this.time = null;
    }

    public void unsetUuid() {
        this.__isset_bit_vector.clear(0);
    }

    @Override // org.apache.thrift.TBase
    public void write(d dVar) throws TException {
        validate();
        dVar.J(STRUCT_DESC);
        if (isSetUuid()) {
            dVar.w(UUID_FIELD_DESC);
            dVar.B(this.uuid);
            dVar.x();
        }
        if (this.time != null && isSetTime()) {
            dVar.w(TIME_FIELD_DESC);
            dVar.I(this.time);
            dVar.x();
        }
        if (this.clientIp != null && isSetClientIp()) {
            dVar.w(CLIENT_IP_FIELD_DESC);
            dVar.I(this.clientIp);
            dVar.x();
        }
        if (this.serverIp != null && isSetServerIp()) {
            dVar.w(SERVER_IP_FIELD_DESC);
            dVar.I(this.serverIp);
            dVar.x();
        }
        if (this.serverHost != null && isSetServerHost()) {
            dVar.w(SERVER_HOST_FIELD_DESC);
            dVar.I(this.serverHost);
            dVar.x();
        }
        dVar.y();
        dVar.K();
    }

    @Override // java.lang.Comparable
    public int compareTo(Common common) {
        int iG;
        int iG2;
        int iG3;
        int iG4;
        int iE;
        if (!getClass().equals(common.getClass())) {
            return getClass().getName().compareTo(common.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetUuid()).compareTo(Boolean.valueOf(common.isSetUuid()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetUuid() && (iE = g0.a.e(this.uuid, common.uuid)) != 0) {
            return iE;
        }
        int iCompareTo2 = Boolean.valueOf(isSetTime()).compareTo(Boolean.valueOf(common.isSetTime()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (isSetTime() && (iG4 = g0.a.g(this.time, common.time)) != 0) {
            return iG4;
        }
        int iCompareTo3 = Boolean.valueOf(isSetClientIp()).compareTo(Boolean.valueOf(common.isSetClientIp()));
        if (iCompareTo3 != 0) {
            return iCompareTo3;
        }
        if (isSetClientIp() && (iG3 = g0.a.g(this.clientIp, common.clientIp)) != 0) {
            return iG3;
        }
        int iCompareTo4 = Boolean.valueOf(isSetServerIp()).compareTo(Boolean.valueOf(common.isSetServerIp()));
        if (iCompareTo4 != 0) {
            return iCompareTo4;
        }
        if (isSetServerIp() && (iG2 = g0.a.g(this.serverIp, common.serverIp)) != 0) {
            return iG2;
        }
        int iCompareTo5 = Boolean.valueOf(isSetServerHost()).compareTo(Boolean.valueOf(common.isSetServerHost()));
        if (iCompareTo5 != 0) {
            return iCompareTo5;
        }
        if (!isSetServerHost() || (iG = g0.a.g(this.serverHost, common.serverHost)) == 0) {
            return 0;
        }
        return iG;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public Common m3deepCopy() {
        return new Common(this);
    }

    public boolean equals(Common common) {
        if (common == null) {
            return false;
        }
        boolean zIsSetUuid = isSetUuid();
        boolean zIsSetUuid2 = common.isSetUuid();
        if ((zIsSetUuid || zIsSetUuid2) && !(zIsSetUuid && zIsSetUuid2 && this.uuid == common.uuid)) {
            return false;
        }
        boolean zIsSetTime = isSetTime();
        boolean zIsSetTime2 = common.isSetTime();
        if ((zIsSetTime || zIsSetTime2) && !(zIsSetTime && zIsSetTime2 && this.time.equals(common.time))) {
            return false;
        }
        boolean zIsSetClientIp = isSetClientIp();
        boolean zIsSetClientIp2 = common.isSetClientIp();
        if ((zIsSetClientIp || zIsSetClientIp2) && !(zIsSetClientIp && zIsSetClientIp2 && this.clientIp.equals(common.clientIp))) {
            return false;
        }
        boolean zIsSetServerIp = isSetServerIp();
        boolean zIsSetServerIp2 = common.isSetServerIp();
        if ((zIsSetServerIp || zIsSetServerIp2) && !(zIsSetServerIp && zIsSetServerIp2 && this.serverIp.equals(common.serverIp))) {
            return false;
        }
        boolean zIsSetServerHost = isSetServerHost();
        boolean zIsSetServerHost2 = common.isSetServerHost();
        if (zIsSetServerHost || zIsSetServerHost2) {
            return zIsSetServerHost && zIsSetServerHost2 && this.serverHost.equals(common.serverHost);
        }
        return true;
    }

    public Common(Common common) {
        BitSet bitSet = new BitSet(1);
        this.__isset_bit_vector = bitSet;
        bitSet.clear();
        this.__isset_bit_vector.or(common.__isset_bit_vector);
        this.uuid = common.uuid;
        if (common.isSetTime()) {
            this.time = common.time;
        }
        if (common.isSetClientIp()) {
            this.clientIp = common.clientIp;
        }
        if (common.isSetServerIp()) {
            this.serverIp = common.serverIp;
        }
        if (common.isSetServerHost()) {
            this.serverHost = common.serverHost;
        }
    }

    public void validate() throws TException {
    }
}
