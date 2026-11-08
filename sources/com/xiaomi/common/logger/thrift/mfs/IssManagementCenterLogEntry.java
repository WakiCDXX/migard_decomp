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
public class IssManagementCenterLogEntry implements WithCommon, TBase<IssManagementCenterLogEntry, _Fields>, Serializable, Cloneable {
    private static final int __APPID_ISSET_ID = 0;
    private static final int __FILESIZE_ISSET_ID = 2;
    private static final int __STORETYPE_ISSET_ID = 1;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    private BitSet __isset_bit_vector;
    public int appId;
    public Common common;
    public String fileId;
    public long fileSize;
    public String mimeType;
    public int storeType;
    public String url;
    private static final g STRUCT_DESC = new g("IssManagementCenterLogEntry");
    private static final a COMMON_FIELD_DESC = new a(Constants.BUCKET_NAME_THERMAL_COMMON, (byte) 12, 1);
    private static final a APP_ID_FIELD_DESC = new a("appId", (byte) 8, 2);
    private static final a URL_FIELD_DESC = new a("url", (byte) 11, 3);
    private static final a STORE_TYPE_FIELD_DESC = new a("storeType", (byte) 8, 4);
    private static final a FILE_ID_FIELD_DESC = new a("fileId", (byte) 11, 5);
    private static final a MIME_TYPE_FIELD_DESC = new a("mimeType", (byte) 11, 6);
    private static final a FILE_SIZE_FIELD_DESC = new a("fileSize", (byte) 10, 7);

    /* JADX INFO: renamed from: com.xiaomi.common.logger.thrift.mfs.IssManagementCenterLogEntry$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$xiaomi$common$logger$thrift$mfs$IssManagementCenterLogEntry$_Fields;

        static {
            int[] iArr = new int[_Fields.values().length];
            $SwitchMap$com$xiaomi$common$logger$thrift$mfs$IssManagementCenterLogEntry$_Fields = iArr;
            try {
                iArr[_Fields.COMMON.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$IssManagementCenterLogEntry$_Fields[_Fields.APP_ID.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$IssManagementCenterLogEntry$_Fields[_Fields.URL.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$IssManagementCenterLogEntry$_Fields[_Fields.STORE_TYPE.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$IssManagementCenterLogEntry$_Fields[_Fields.FILE_ID.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$IssManagementCenterLogEntry$_Fields[_Fields.MIME_TYPE.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$IssManagementCenterLogEntry$_Fields[_Fields.FILE_SIZE.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
        }
    }

    public enum _Fields {
        COMMON(1, Constants.BUCKET_NAME_THERMAL_COMMON),
        APP_ID(2, "appId"),
        URL(3, "url"),
        STORE_TYPE(4, "storeType"),
        FILE_ID(5, "fileId"),
        MIME_TYPE(6, "mimeType"),
        FILE_SIZE(7, "fileSize");

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
                    return COMMON;
                case 2:
                    return APP_ID;
                case 3:
                    return URL;
                case 4:
                    return STORE_TYPE;
                case 5:
                    return FILE_ID;
                case 6:
                    return MIME_TYPE;
                case 7:
                    return FILE_SIZE;
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
        enumMap.put(_Fields.COMMON, new FieldMetaData(Constants.BUCKET_NAME_THERMAL_COMMON, (byte) 1, new StructMetaData((byte) 12, Common.class)));
        enumMap.put(_Fields.APP_ID, new FieldMetaData("appId", (byte) 1, new FieldValueMetaData((byte) 8)));
        enumMap.put(_Fields.URL, new FieldMetaData("url", (byte) 1, new FieldValueMetaData((byte) 11)));
        enumMap.put(_Fields.STORE_TYPE, new FieldMetaData("storeType", (byte) 1, new FieldValueMetaData((byte) 8)));
        enumMap.put(_Fields.FILE_ID, new FieldMetaData("fileId", (byte) 2, new FieldValueMetaData((byte) 11)));
        enumMap.put(_Fields.MIME_TYPE, new FieldMetaData("mimeType", (byte) 2, new FieldValueMetaData((byte) 11)));
        enumMap.put(_Fields.FILE_SIZE, new FieldMetaData("fileSize", (byte) 2, new FieldValueMetaData((byte) 10)));
        Map<_Fields, FieldMetaData> mapUnmodifiableMap = Collections.unmodifiableMap(enumMap);
        metaDataMap = mapUnmodifiableMap;
        FieldMetaData.b(IssManagementCenterLogEntry.class, mapUnmodifiableMap);
    }

    public IssManagementCenterLogEntry() {
        this.__isset_bit_vector = new BitSet(3);
    }

    public void clear() {
        this.common = null;
        setAppIdIsSet(false);
        this.appId = 0;
        this.url = null;
        setStoreTypeIsSet(false);
        this.storeType = 0;
        this.fileId = null;
        this.mimeType = null;
        setFileSizeIsSet(false);
        this.fileSize = 0L;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof IssManagementCenterLogEntry)) {
            return equals((IssManagementCenterLogEntry) obj);
        }
        return false;
    }

    public _Fields fieldForId(int i2) {
        return _Fields.findByThriftId(i2);
    }

    public int getAppId() {
        return this.appId;
    }

    @Override // com.xiaomi.common.logger.WithCommon
    public Common getCommon() {
        return this.common;
    }

    public Object getFieldValue(_Fields _fields) {
        switch (AnonymousClass1.$SwitchMap$com$xiaomi$common$logger$thrift$mfs$IssManagementCenterLogEntry$_Fields[_fields.ordinal()]) {
            case 1:
                return getCommon();
            case 2:
                return new Integer(getAppId());
            case 3:
                return getUrl();
            case 4:
                return new Integer(getStoreType());
            case 5:
                return getFileId();
            case 6:
                return getMimeType();
            case 7:
                return new Long(getFileSize());
            default:
                throw new IllegalStateException();
        }
    }

    public String getFileId() {
        return this.fileId;
    }

    public long getFileSize() {
        return this.fileSize;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public int getStoreType() {
        return this.storeType;
    }

    public String getUrl() {
        return this.url;
    }

    public int hashCode() {
        return 0;
    }

    public boolean isSet(_Fields _fields) {
        if (_fields == null) {
            throw new IllegalArgumentException();
        }
        switch (AnonymousClass1.$SwitchMap$com$xiaomi$common$logger$thrift$mfs$IssManagementCenterLogEntry$_Fields[_fields.ordinal()]) {
            case 1:
                return isSetCommon();
            case 2:
                return isSetAppId();
            case 3:
                return isSetUrl();
            case 4:
                return isSetStoreType();
            case 5:
                return isSetFileId();
            case 6:
                return isSetMimeType();
            case 7:
                return isSetFileSize();
            default:
                throw new IllegalStateException();
        }
    }

    public boolean isSetAppId() {
        return this.__isset_bit_vector.get(0);
    }

    public boolean isSetCommon() {
        return this.common != null;
    }

    public boolean isSetFileId() {
        return this.fileId != null;
    }

    public boolean isSetFileSize() {
        return this.__isset_bit_vector.get(2);
    }

    public boolean isSetMimeType() {
        return this.mimeType != null;
    }

    public boolean isSetStoreType() {
        return this.__isset_bit_vector.get(1);
    }

    public boolean isSetUrl() {
        return this.url != null;
    }

    @Override // org.apache.thrift.TBase
    public void read(d dVar) throws TException {
        dVar.q();
        while (true) {
            a aVarE = dVar.e();
            byte b2 = aVarE.f2150b;
            if (b2 == 0) {
                dVar.r();
                if (!isSetAppId()) {
                    throw new TProtocolException("Required field 'appId' was not found in serialized data! Struct: " + toString());
                }
                if (isSetStoreType()) {
                    validate();
                    return;
                }
                throw new TProtocolException("Required field 'storeType' was not found in serialized data! Struct: " + toString());
            }
            switch (aVarE.f2151c) {
                case 1:
                    if (b2 != 12) {
                        e.a(dVar, b2);
                    } else {
                        Common common = new Common();
                        this.common = common;
                        common.read(dVar);
                    }
                    break;
                case 2:
                    if (b2 != 8) {
                        e.a(dVar, b2);
                    } else {
                        this.appId = dVar.h();
                        setAppIdIsSet(true);
                    }
                    break;
                case 3:
                    if (b2 != 11) {
                        e.a(dVar, b2);
                    } else {
                        this.url = dVar.p();
                    }
                    break;
                case 4:
                    if (b2 != 8) {
                        e.a(dVar, b2);
                    } else {
                        this.storeType = dVar.h();
                        setStoreTypeIsSet(true);
                    }
                    break;
                case 5:
                    if (b2 != 11) {
                        e.a(dVar, b2);
                    } else {
                        this.fileId = dVar.p();
                    }
                    break;
                case 6:
                    if (b2 != 11) {
                        e.a(dVar, b2);
                    } else {
                        this.mimeType = dVar.p();
                    }
                    break;
                case 7:
                    if (b2 != 10) {
                        e.a(dVar, b2);
                    } else {
                        this.fileSize = dVar.i();
                        setFileSizeIsSet(true);
                    }
                    break;
                default:
                    e.a(dVar, b2);
                    break;
            }
            dVar.f();
        }
    }

    public IssManagementCenterLogEntry setAppId(int i2) {
        this.appId = i2;
        setAppIdIsSet(true);
        return this;
    }

    public void setAppIdIsSet(boolean z2) {
        this.__isset_bit_vector.set(0, z2);
    }

    public void setCommonIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.common = null;
    }

    public void setFieldValue(_Fields _fields, Object obj) {
        switch (AnonymousClass1.$SwitchMap$com$xiaomi$common$logger$thrift$mfs$IssManagementCenterLogEntry$_Fields[_fields.ordinal()]) {
            case 1:
                if (obj != null) {
                    setCommon((Common) obj);
                } else {
                    unsetCommon();
                }
                break;
            case 2:
                if (obj != null) {
                    setAppId(((Integer) obj).intValue());
                } else {
                    unsetAppId();
                }
                break;
            case 3:
                if (obj != null) {
                    setUrl((String) obj);
                } else {
                    unsetUrl();
                }
                break;
            case 4:
                if (obj != null) {
                    setStoreType(((Integer) obj).intValue());
                } else {
                    unsetStoreType();
                }
                break;
            case 5:
                if (obj != null) {
                    setFileId((String) obj);
                } else {
                    unsetFileId();
                }
                break;
            case 6:
                if (obj != null) {
                    setMimeType((String) obj);
                } else {
                    unsetMimeType();
                }
                break;
            case 7:
                if (obj != null) {
                    setFileSize(((Long) obj).longValue());
                } else {
                    unsetFileSize();
                }
                break;
        }
    }

    public IssManagementCenterLogEntry setFileId(String str) {
        this.fileId = str;
        return this;
    }

    public void setFileIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.fileId = null;
    }

    public IssManagementCenterLogEntry setFileSize(long j2) {
        this.fileSize = j2;
        setFileSizeIsSet(true);
        return this;
    }

    public void setFileSizeIsSet(boolean z2) {
        this.__isset_bit_vector.set(2, z2);
    }

    public IssManagementCenterLogEntry setMimeType(String str) {
        this.mimeType = str;
        return this;
    }

    public void setMimeTypeIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.mimeType = null;
    }

    public IssManagementCenterLogEntry setStoreType(int i2) {
        this.storeType = i2;
        setStoreTypeIsSet(true);
        return this;
    }

    public void setStoreTypeIsSet(boolean z2) {
        this.__isset_bit_vector.set(1, z2);
    }

    public IssManagementCenterLogEntry setUrl(String str) {
        this.url = str;
        return this;
    }

    public void setUrlIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.url = null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("IssManagementCenterLogEntry(");
        sb.append("common:");
        Common common = this.common;
        if (common == null) {
            sb.append("null");
        } else {
            sb.append(common);
        }
        sb.append(", ");
        sb.append("appId:");
        sb.append(this.appId);
        sb.append(", ");
        sb.append("url:");
        String str = this.url;
        if (str == null) {
            sb.append("null");
        } else {
            sb.append(str);
        }
        sb.append(", ");
        sb.append("storeType:");
        sb.append(this.storeType);
        if (isSetFileId()) {
            sb.append(", ");
            sb.append("fileId:");
            String str2 = this.fileId;
            if (str2 == null) {
                sb.append("null");
            } else {
                sb.append(str2);
            }
        }
        if (isSetMimeType()) {
            sb.append(", ");
            sb.append("mimeType:");
            String str3 = this.mimeType;
            if (str3 == null) {
                sb.append("null");
            } else {
                sb.append(str3);
            }
        }
        if (isSetFileSize()) {
            sb.append(", ");
            sb.append("fileSize:");
            sb.append(this.fileSize);
        }
        sb.append(com.xiaomi.push.mpcd.Constants.SEPARATOR_RIGHT_PARENTESIS);
        return sb.toString();
    }

    public void unsetAppId() {
        this.__isset_bit_vector.clear(0);
    }

    public void unsetCommon() {
        this.common = null;
    }

    public void unsetFileId() {
        this.fileId = null;
    }

    public void unsetFileSize() {
        this.__isset_bit_vector.clear(2);
    }

    public void unsetMimeType() {
        this.mimeType = null;
    }

    public void unsetStoreType() {
        this.__isset_bit_vector.clear(1);
    }

    public void unsetUrl() {
        this.url = null;
    }

    public void validate() throws TException {
        if (this.common == null) {
            throw new TProtocolException("Required field 'common' was not present! Struct: " + toString());
        }
        if (this.url != null) {
            return;
        }
        throw new TProtocolException("Required field 'url' was not present! Struct: " + toString());
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
        dVar.w(APP_ID_FIELD_DESC);
        dVar.A(this.appId);
        dVar.x();
        if (this.url != null) {
            dVar.w(URL_FIELD_DESC);
            dVar.I(this.url);
            dVar.x();
        }
        dVar.w(STORE_TYPE_FIELD_DESC);
        dVar.A(this.storeType);
        dVar.x();
        if (this.fileId != null && isSetFileId()) {
            dVar.w(FILE_ID_FIELD_DESC);
            dVar.I(this.fileId);
            dVar.x();
        }
        if (this.mimeType != null && isSetMimeType()) {
            dVar.w(MIME_TYPE_FIELD_DESC);
            dVar.I(this.mimeType);
            dVar.x();
        }
        if (isSetFileSize()) {
            dVar.w(FILE_SIZE_FIELD_DESC);
            dVar.B(this.fileSize);
            dVar.x();
        }
        dVar.y();
        dVar.K();
    }

    @Override // java.lang.Comparable
    public int compareTo(IssManagementCenterLogEntry issManagementCenterLogEntry) {
        int iE;
        int iG;
        int iG2;
        int iD;
        int iG3;
        int iD2;
        int iF;
        if (!getClass().equals(issManagementCenterLogEntry.getClass())) {
            return getClass().getName().compareTo(issManagementCenterLogEntry.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetCommon()).compareTo(Boolean.valueOf(issManagementCenterLogEntry.isSetCommon()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetCommon() && (iF = g0.a.f(this.common, issManagementCenterLogEntry.common)) != 0) {
            return iF;
        }
        int iCompareTo2 = Boolean.valueOf(isSetAppId()).compareTo(Boolean.valueOf(issManagementCenterLogEntry.isSetAppId()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (isSetAppId() && (iD2 = g0.a.d(this.appId, issManagementCenterLogEntry.appId)) != 0) {
            return iD2;
        }
        int iCompareTo3 = Boolean.valueOf(isSetUrl()).compareTo(Boolean.valueOf(issManagementCenterLogEntry.isSetUrl()));
        if (iCompareTo3 != 0) {
            return iCompareTo3;
        }
        if (isSetUrl() && (iG3 = g0.a.g(this.url, issManagementCenterLogEntry.url)) != 0) {
            return iG3;
        }
        int iCompareTo4 = Boolean.valueOf(isSetStoreType()).compareTo(Boolean.valueOf(issManagementCenterLogEntry.isSetStoreType()));
        if (iCompareTo4 != 0) {
            return iCompareTo4;
        }
        if (isSetStoreType() && (iD = g0.a.d(this.storeType, issManagementCenterLogEntry.storeType)) != 0) {
            return iD;
        }
        int iCompareTo5 = Boolean.valueOf(isSetFileId()).compareTo(Boolean.valueOf(issManagementCenterLogEntry.isSetFileId()));
        if (iCompareTo5 != 0) {
            return iCompareTo5;
        }
        if (isSetFileId() && (iG2 = g0.a.g(this.fileId, issManagementCenterLogEntry.fileId)) != 0) {
            return iG2;
        }
        int iCompareTo6 = Boolean.valueOf(isSetMimeType()).compareTo(Boolean.valueOf(issManagementCenterLogEntry.isSetMimeType()));
        if (iCompareTo6 != 0) {
            return iCompareTo6;
        }
        if (isSetMimeType() && (iG = g0.a.g(this.mimeType, issManagementCenterLogEntry.mimeType)) != 0) {
            return iG;
        }
        int iCompareTo7 = Boolean.valueOf(isSetFileSize()).compareTo(Boolean.valueOf(issManagementCenterLogEntry.isSetFileSize()));
        if (iCompareTo7 != 0) {
            return iCompareTo7;
        }
        if (!isSetFileSize() || (iE = g0.a.e(this.fileSize, issManagementCenterLogEntry.fileSize)) == 0) {
            return 0;
        }
        return iE;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public IssManagementCenterLogEntry m7deepCopy() {
        return new IssManagementCenterLogEntry(this);
    }

    @Override // com.xiaomi.common.logger.WithCommon
    public IssManagementCenterLogEntry setCommon(Common common) {
        this.common = common;
        return this;
    }

    public IssManagementCenterLogEntry(Common common, int i2, String str, int i3) {
        this();
        this.common = common;
        this.appId = i2;
        setAppIdIsSet(true);
        this.url = str;
        this.storeType = i3;
        setStoreTypeIsSet(true);
    }

    public boolean equals(IssManagementCenterLogEntry issManagementCenterLogEntry) {
        if (issManagementCenterLogEntry == null) {
            return false;
        }
        boolean zIsSetCommon = isSetCommon();
        boolean zIsSetCommon2 = issManagementCenterLogEntry.isSetCommon();
        if (((zIsSetCommon || zIsSetCommon2) && !(zIsSetCommon && zIsSetCommon2 && this.common.equals(issManagementCenterLogEntry.common))) || this.appId != issManagementCenterLogEntry.appId) {
            return false;
        }
        boolean zIsSetUrl = isSetUrl();
        boolean zIsSetUrl2 = issManagementCenterLogEntry.isSetUrl();
        if (((zIsSetUrl || zIsSetUrl2) && !(zIsSetUrl && zIsSetUrl2 && this.url.equals(issManagementCenterLogEntry.url))) || this.storeType != issManagementCenterLogEntry.storeType) {
            return false;
        }
        boolean zIsSetFileId = isSetFileId();
        boolean zIsSetFileId2 = issManagementCenterLogEntry.isSetFileId();
        if ((zIsSetFileId || zIsSetFileId2) && !(zIsSetFileId && zIsSetFileId2 && this.fileId.equals(issManagementCenterLogEntry.fileId))) {
            return false;
        }
        boolean zIsSetMimeType = isSetMimeType();
        boolean zIsSetMimeType2 = issManagementCenterLogEntry.isSetMimeType();
        if ((zIsSetMimeType || zIsSetMimeType2) && !(zIsSetMimeType && zIsSetMimeType2 && this.mimeType.equals(issManagementCenterLogEntry.mimeType))) {
            return false;
        }
        boolean zIsSetFileSize = isSetFileSize();
        boolean zIsSetFileSize2 = issManagementCenterLogEntry.isSetFileSize();
        if (zIsSetFileSize || zIsSetFileSize2) {
            return zIsSetFileSize && zIsSetFileSize2 && this.fileSize == issManagementCenterLogEntry.fileSize;
        }
        return true;
    }

    public IssManagementCenterLogEntry(IssManagementCenterLogEntry issManagementCenterLogEntry) {
        BitSet bitSet = new BitSet(3);
        this.__isset_bit_vector = bitSet;
        bitSet.clear();
        this.__isset_bit_vector.or(issManagementCenterLogEntry.__isset_bit_vector);
        if (issManagementCenterLogEntry.isSetCommon()) {
            this.common = new Common(issManagementCenterLogEntry.common);
        }
        this.appId = issManagementCenterLogEntry.appId;
        if (issManagementCenterLogEntry.isSetUrl()) {
            this.url = issManagementCenterLogEntry.url;
        }
        this.storeType = issManagementCenterLogEntry.storeType;
        if (issManagementCenterLogEntry.isSetFileId()) {
            this.fileId = issManagementCenterLogEntry.fileId;
        }
        if (issManagementCenterLogEntry.isSetMimeType()) {
            this.mimeType = issManagementCenterLogEntry.mimeType;
        }
        this.fileSize = issManagementCenterLogEntry.fileSize;
    }
}
