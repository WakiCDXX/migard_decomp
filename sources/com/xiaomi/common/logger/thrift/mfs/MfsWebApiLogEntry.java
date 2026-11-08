package com.xiaomi.common.logger.thrift.mfs;

import com.miui.powerkeeper.cloudcontrol.Constants;
import com.xiaomi.common.logger.WithCommon;
import com.xiaomi.common.logger.thrift.Common;
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
import org.apache.thrift.meta_data.StructMetaData;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.protocol.a;
import org.apache.thrift.protocol.d;
import org.apache.thrift.protocol.e;
import org.apache.thrift.protocol.g;

/* JADX INFO: loaded from: classes.dex */
public class MfsWebApiLogEntry implements WithCommon, TBase<MfsWebApiLogEntry, _Fields>, Serializable, Cloneable {
    private static final int __FILESIZE_ISSET_ID = 0;
    private static final int __PERMENANT_ISSET_ID = 1;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    private BitSet __isset_bit_vector;
    public Common common;
    public String digest;
    public long fileSize;
    public boolean permenant;
    public String toId;
    private static final g STRUCT_DESC = new g("MfsWebApiLogEntry");
    private static final a COMMON_FIELD_DESC = new a(Constants.BUCKET_NAME_THERMAL_COMMON, (byte) 12, 1);
    private static final a TO_ID_FIELD_DESC = new a("toId", (byte) 11, 2);
    private static final a DIGEST_FIELD_DESC = new a("digest", (byte) 11, 3);
    private static final a FILE_SIZE_FIELD_DESC = new a("fileSize", (byte) 10, 4);
    private static final a PERMENANT_FIELD_DESC = new a("permenant", (byte) 2, 5);

    /* JADX INFO: renamed from: com.xiaomi.common.logger.thrift.mfs.MfsWebApiLogEntry$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$xiaomi$common$logger$thrift$mfs$MfsWebApiLogEntry$_Fields;

        static {
            int[] iArr = new int[_Fields.values().length];
            $SwitchMap$com$xiaomi$common$logger$thrift$mfs$MfsWebApiLogEntry$_Fields = iArr;
            try {
                iArr[_Fields.COMMON.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$MfsWebApiLogEntry$_Fields[_Fields.TO_ID.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$MfsWebApiLogEntry$_Fields[_Fields.DIGEST.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$MfsWebApiLogEntry$_Fields[_Fields.FILE_SIZE.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$MfsWebApiLogEntry$_Fields[_Fields.PERMENANT.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
        }
    }

    public enum _Fields {
        COMMON(1, Constants.BUCKET_NAME_THERMAL_COMMON),
        TO_ID(2, "toId"),
        DIGEST(3, "digest"),
        FILE_SIZE(4, "fileSize"),
        PERMENANT(5, "permenant");

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
                return COMMON;
            }
            if (i2 == 2) {
                return TO_ID;
            }
            if (i2 == 3) {
                return DIGEST;
            }
            if (i2 == 4) {
                return FILE_SIZE;
            }
            if (i2 != 5) {
                return null;
            }
            return PERMENANT;
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
        enumMap.put(_Fields.COMMON, new FieldMetaData(Constants.BUCKET_NAME_THERMAL_COMMON, (byte) 1, new StructMetaData((byte) 12, Common.class)));
        enumMap.put(_Fields.TO_ID, new FieldMetaData("toId", (byte) 1, new FieldValueMetaData((byte) 11)));
        enumMap.put(_Fields.DIGEST, new FieldMetaData("digest", (byte) 1, new FieldValueMetaData((byte) 11)));
        enumMap.put(_Fields.FILE_SIZE, new FieldMetaData("fileSize", (byte) 1, new FieldValueMetaData((byte) 10)));
        enumMap.put(_Fields.PERMENANT, new FieldMetaData("permenant", (byte) 1, new FieldValueMetaData((byte) 2)));
        Map<_Fields, FieldMetaData> mapUnmodifiableMap = Collections.unmodifiableMap(enumMap);
        metaDataMap = mapUnmodifiableMap;
        FieldMetaData.b(MfsWebApiLogEntry.class, mapUnmodifiableMap);
    }

    public MfsWebApiLogEntry() {
        this.__isset_bit_vector = new BitSet(2);
    }

    public void clear() {
        this.common = null;
        this.toId = null;
        this.digest = null;
        setFileSizeIsSet(false);
        this.fileSize = 0L;
        setPermenantIsSet(false);
        this.permenant = false;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof MfsWebApiLogEntry)) {
            return equals((MfsWebApiLogEntry) obj);
        }
        return false;
    }

    public _Fields fieldForId(int i2) {
        return _Fields.findByThriftId(i2);
    }

    @Override // com.xiaomi.common.logger.WithCommon
    public Common getCommon() {
        return this.common;
    }

    public String getDigest() {
        return this.digest;
    }

    public Object getFieldValue(_Fields _fields) {
        int i2 = AnonymousClass1.$SwitchMap$com$xiaomi$common$logger$thrift$mfs$MfsWebApiLogEntry$_Fields[_fields.ordinal()];
        if (i2 == 1) {
            return getCommon();
        }
        if (i2 == 2) {
            return getToId();
        }
        if (i2 == 3) {
            return getDigest();
        }
        if (i2 == 4) {
            return new Long(getFileSize());
        }
        if (i2 == 5) {
            return new Boolean(isPermenant());
        }
        throw new IllegalStateException();
    }

    public long getFileSize() {
        return this.fileSize;
    }

    public String getToId() {
        return this.toId;
    }

    public int hashCode() {
        return 0;
    }

    public boolean isPermenant() {
        return this.permenant;
    }

    public boolean isSet(_Fields _fields) {
        if (_fields == null) {
            throw new IllegalArgumentException();
        }
        int i2 = AnonymousClass1.$SwitchMap$com$xiaomi$common$logger$thrift$mfs$MfsWebApiLogEntry$_Fields[_fields.ordinal()];
        if (i2 == 1) {
            return isSetCommon();
        }
        if (i2 == 2) {
            return isSetToId();
        }
        if (i2 == 3) {
            return isSetDigest();
        }
        if (i2 == 4) {
            return isSetFileSize();
        }
        if (i2 == 5) {
            return isSetPermenant();
        }
        throw new IllegalStateException();
    }

    public boolean isSetCommon() {
        return this.common != null;
    }

    public boolean isSetDigest() {
        return this.digest != null;
    }

    public boolean isSetFileSize() {
        return this.__isset_bit_vector.get(0);
    }

    public boolean isSetPermenant() {
        return this.__isset_bit_vector.get(1);
    }

    public boolean isSetToId() {
        return this.toId != null;
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
                        if (s2 != 4) {
                            if (s2 != 5) {
                                e.a(dVar, b2);
                            } else if (b2 == 2) {
                                this.permenant = dVar.b();
                                setPermenantIsSet(true);
                            } else {
                                e.a(dVar, b2);
                            }
                        } else if (b2 == 10) {
                            this.fileSize = dVar.i();
                            setFileSizeIsSet(true);
                        } else {
                            e.a(dVar, b2);
                        }
                    } else if (b2 == 11) {
                        this.digest = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                } else if (b2 == 11) {
                    this.toId = dVar.p();
                } else {
                    e.a(dVar, b2);
                }
            } else if (b2 == 12) {
                Common common = new Common();
                this.common = common;
                common.read(dVar);
            } else {
                e.a(dVar, b2);
            }
            dVar.f();
        }
        dVar.r();
        if (!isSetFileSize()) {
            throw new TProtocolException("Required field 'fileSize' was not found in serialized data! Struct: " + toString());
        }
        if (isSetPermenant()) {
            validate();
            return;
        }
        throw new TProtocolException("Required field 'permenant' was not found in serialized data! Struct: " + toString());
    }

    public void setCommonIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.common = null;
    }

    public MfsWebApiLogEntry setDigest(String str) {
        this.digest = str;
        return this;
    }

    public void setDigestIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.digest = null;
    }

    public void setFieldValue(_Fields _fields, Object obj) {
        int i2 = AnonymousClass1.$SwitchMap$com$xiaomi$common$logger$thrift$mfs$MfsWebApiLogEntry$_Fields[_fields.ordinal()];
        if (i2 == 1) {
            if (obj == null) {
                unsetCommon();
                return;
            } else {
                setCommon((Common) obj);
                return;
            }
        }
        if (i2 == 2) {
            if (obj == null) {
                unsetToId();
                return;
            } else {
                setToId((String) obj);
                return;
            }
        }
        if (i2 == 3) {
            if (obj == null) {
                unsetDigest();
                return;
            } else {
                setDigest((String) obj);
                return;
            }
        }
        if (i2 == 4) {
            if (obj == null) {
                unsetFileSize();
                return;
            } else {
                setFileSize(((Long) obj).longValue());
                return;
            }
        }
        if (i2 != 5) {
            return;
        }
        if (obj == null) {
            unsetPermenant();
        } else {
            setPermenant(((Boolean) obj).booleanValue());
        }
    }

    public MfsWebApiLogEntry setFileSize(long j2) {
        this.fileSize = j2;
        setFileSizeIsSet(true);
        return this;
    }

    public void setFileSizeIsSet(boolean z2) {
        this.__isset_bit_vector.set(0, z2);
    }

    public MfsWebApiLogEntry setPermenant(boolean z2) {
        this.permenant = z2;
        setPermenantIsSet(true);
        return this;
    }

    public void setPermenantIsSet(boolean z2) {
        this.__isset_bit_vector.set(1, z2);
    }

    public MfsWebApiLogEntry setToId(String str) {
        this.toId = str;
        return this;
    }

    public void setToIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.toId = null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("MfsWebApiLogEntry(");
        sb.append("common:");
        Common common = this.common;
        if (common == null) {
            sb.append("null");
        } else {
            sb.append(common);
        }
        sb.append(", ");
        sb.append("toId:");
        String str = this.toId;
        if (str == null) {
            sb.append("null");
        } else {
            sb.append(str);
        }
        sb.append(", ");
        sb.append("digest:");
        String str2 = this.digest;
        if (str2 == null) {
            sb.append("null");
        } else {
            sb.append(str2);
        }
        sb.append(", ");
        sb.append("fileSize:");
        sb.append(this.fileSize);
        sb.append(", ");
        sb.append("permenant:");
        sb.append(this.permenant);
        sb.append(com.xiaomi.push.mpcd.Constants.SEPARATOR_RIGHT_PARENTESIS);
        return sb.toString();
    }

    public void unsetCommon() {
        this.common = null;
    }

    public void unsetDigest() {
        this.digest = null;
    }

    public void unsetFileSize() {
        this.__isset_bit_vector.clear(0);
    }

    public void unsetPermenant() {
        this.__isset_bit_vector.clear(1);
    }

    public void unsetToId() {
        this.toId = null;
    }

    public void validate() throws TException {
        if (this.common == null) {
            throw new TProtocolException("Required field 'common' was not present! Struct: " + toString());
        }
        if (this.toId == null) {
            throw new TProtocolException("Required field 'toId' was not present! Struct: " + toString());
        }
        if (this.digest != null) {
            return;
        }
        throw new TProtocolException("Required field 'digest' was not present! Struct: " + toString());
    }

    @Override // org.apache.thrift.TBase
    public void write(d dVar) throws TException {
        validate();
        dVar.J(STRUCT_DESC);
        if (this.common != null) {
            dVar.w(COMMON_FIELD_DESC);
            this.common.write(dVar);
            dVar.x();
        }
        if (this.toId != null) {
            dVar.w(TO_ID_FIELD_DESC);
            dVar.I(this.toId);
            dVar.x();
        }
        if (this.digest != null) {
            dVar.w(DIGEST_FIELD_DESC);
            dVar.I(this.digest);
            dVar.x();
        }
        dVar.w(FILE_SIZE_FIELD_DESC);
        dVar.B(this.fileSize);
        dVar.x();
        dVar.w(PERMENANT_FIELD_DESC);
        dVar.u(this.permenant);
        dVar.x();
        dVar.y();
        dVar.K();
    }

    @Override // java.lang.Comparable
    public int compareTo(MfsWebApiLogEntry mfsWebApiLogEntry) {
        int iL;
        int iE;
        int iG;
        int iG2;
        int iF;
        if (!getClass().equals(mfsWebApiLogEntry.getClass())) {
            return getClass().getName().compareTo(mfsWebApiLogEntry.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetCommon()).compareTo(Boolean.valueOf(mfsWebApiLogEntry.isSetCommon()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetCommon() && (iF = g0.a.f(this.common, mfsWebApiLogEntry.common)) != 0) {
            return iF;
        }
        int iCompareTo2 = Boolean.valueOf(isSetToId()).compareTo(Boolean.valueOf(mfsWebApiLogEntry.isSetToId()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (isSetToId() && (iG2 = g0.a.g(this.toId, mfsWebApiLogEntry.toId)) != 0) {
            return iG2;
        }
        int iCompareTo3 = Boolean.valueOf(isSetDigest()).compareTo(Boolean.valueOf(mfsWebApiLogEntry.isSetDigest()));
        if (iCompareTo3 != 0) {
            return iCompareTo3;
        }
        if (isSetDigest() && (iG = g0.a.g(this.digest, mfsWebApiLogEntry.digest)) != 0) {
            return iG;
        }
        int iCompareTo4 = Boolean.valueOf(isSetFileSize()).compareTo(Boolean.valueOf(mfsWebApiLogEntry.isSetFileSize()));
        if (iCompareTo4 != 0) {
            return iCompareTo4;
        }
        if (isSetFileSize() && (iE = g0.a.e(this.fileSize, mfsWebApiLogEntry.fileSize)) != 0) {
            return iE;
        }
        int iCompareTo5 = Boolean.valueOf(isSetPermenant()).compareTo(Boolean.valueOf(mfsWebApiLogEntry.isSetPermenant()));
        if (iCompareTo5 != 0) {
            return iCompareTo5;
        }
        if (!isSetPermenant() || (iL = g0.a.l(this.permenant, mfsWebApiLogEntry.permenant)) == 0) {
            return 0;
        }
        return iL;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public MfsWebApiLogEntry m10deepCopy() {
        return new MfsWebApiLogEntry(this);
    }

    @Override // com.xiaomi.common.logger.WithCommon
    public MfsWebApiLogEntry setCommon(Common common) {
        this.common = common;
        return this;
    }

    public MfsWebApiLogEntry(Common common, String str, String str2, long j2, boolean z2) {
        this();
        this.common = common;
        this.toId = str;
        this.digest = str2;
        this.fileSize = j2;
        setFileSizeIsSet(true);
        this.permenant = z2;
        setPermenantIsSet(true);
    }

    public boolean equals(MfsWebApiLogEntry mfsWebApiLogEntry) {
        if (mfsWebApiLogEntry == null) {
            return false;
        }
        boolean zIsSetCommon = isSetCommon();
        boolean zIsSetCommon2 = mfsWebApiLogEntry.isSetCommon();
        if ((zIsSetCommon || zIsSetCommon2) && !(zIsSetCommon && zIsSetCommon2 && this.common.equals(mfsWebApiLogEntry.common))) {
            return false;
        }
        boolean zIsSetToId = isSetToId();
        boolean zIsSetToId2 = mfsWebApiLogEntry.isSetToId();
        if ((zIsSetToId || zIsSetToId2) && !(zIsSetToId && zIsSetToId2 && this.toId.equals(mfsWebApiLogEntry.toId))) {
            return false;
        }
        boolean zIsSetDigest = isSetDigest();
        boolean zIsSetDigest2 = mfsWebApiLogEntry.isSetDigest();
        return (!(zIsSetDigest || zIsSetDigest2) || (zIsSetDigest && zIsSetDigest2 && this.digest.equals(mfsWebApiLogEntry.digest))) && this.fileSize == mfsWebApiLogEntry.fileSize && this.permenant == mfsWebApiLogEntry.permenant;
    }

    public MfsWebApiLogEntry(MfsWebApiLogEntry mfsWebApiLogEntry) {
        BitSet bitSet = new BitSet(2);
        this.__isset_bit_vector = bitSet;
        bitSet.clear();
        this.__isset_bit_vector.or(mfsWebApiLogEntry.__isset_bit_vector);
        if (mfsWebApiLogEntry.isSetCommon()) {
            this.common = new Common(mfsWebApiLogEntry.common);
        }
        if (mfsWebApiLogEntry.isSetToId()) {
            this.toId = mfsWebApiLogEntry.toId;
        }
        if (mfsWebApiLogEntry.isSetDigest()) {
            this.digest = mfsWebApiLogEntry.digest;
        }
        this.fileSize = mfsWebApiLogEntry.fileSize;
        this.permenant = mfsWebApiLogEntry.permenant;
    }
}
