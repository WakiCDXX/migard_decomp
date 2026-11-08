package com.xiaomi.common.logger.thrift.mfs;

import com.miui.powerkeeper.cloudcontrol.Constants;
import com.xiaomi.common.logger.thrift.Common;
import java.io.Serializable;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import miui.yellowpage.YellowPageStatistic;
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
public class HttpLog implements TBase<HttpLog, _Fields>, Serializable, Cloneable {
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    private String category;
    private Common common;
    private HttpApi httpApi;
    private Passport passport;
    private static final g STRUCT_DESC = new g("HttpLog");
    private static final a COMMON_FIELD_DESC = new a(Constants.BUCKET_NAME_THERMAL_COMMON, (byte) 12, 1);
    private static final a CATEGORY_FIELD_DESC = new a(YellowPageStatistic.Display.CATEGORY, (byte) 11, 2);
    private static final a HTTP_API_FIELD_DESC = new a("httpApi", (byte) 12, 3);
    private static final a PASSPORT_FIELD_DESC = new a("passport", (byte) 12, 4);

    /* JADX INFO: renamed from: com.xiaomi.common.logger.thrift.mfs.HttpLog$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$xiaomi$common$logger$thrift$mfs$HttpLog$_Fields;

        static {
            int[] iArr = new int[_Fields.values().length];
            $SwitchMap$com$xiaomi$common$logger$thrift$mfs$HttpLog$_Fields = iArr;
            try {
                iArr[_Fields.COMMON.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$HttpLog$_Fields[_Fields.CATEGORY.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$HttpLog$_Fields[_Fields.HTTP_API.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$HttpLog$_Fields[_Fields.PASSPORT.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    public enum _Fields {
        COMMON(1, Constants.BUCKET_NAME_THERMAL_COMMON),
        CATEGORY(2, YellowPageStatistic.Display.CATEGORY),
        HTTP_API(3, "httpApi"),
        PASSPORT(4, "passport");

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
                return CATEGORY;
            }
            if (i2 == 3) {
                return HTTP_API;
            }
            if (i2 != 4) {
                return null;
            }
            return PASSPORT;
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
        enumMap.put(_Fields.CATEGORY, new FieldMetaData(YellowPageStatistic.Display.CATEGORY, (byte) 1, new FieldValueMetaData((byte) 11)));
        enumMap.put(_Fields.HTTP_API, new FieldMetaData("httpApi", (byte) 2, new StructMetaData((byte) 12, HttpApi.class)));
        enumMap.put(_Fields.PASSPORT, new FieldMetaData("passport", (byte) 2, new StructMetaData((byte) 12, Passport.class)));
        Map<_Fields, FieldMetaData> mapUnmodifiableMap = Collections.unmodifiableMap(enumMap);
        metaDataMap = mapUnmodifiableMap;
        FieldMetaData.b(HttpLog.class, mapUnmodifiableMap);
    }

    public HttpLog() {
        this.category = "";
    }

    public void clear() {
        this.common = null;
        this.category = "";
        this.httpApi = null;
        this.passport = null;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof HttpLog)) {
            return equals((HttpLog) obj);
        }
        return false;
    }

    public _Fields fieldForId(int i2) {
        return _Fields.findByThriftId(i2);
    }

    public String getCategory() {
        return this.category;
    }

    public Common getCommon() {
        return this.common;
    }

    public Object getFieldValue(_Fields _fields) {
        int i2 = AnonymousClass1.$SwitchMap$com$xiaomi$common$logger$thrift$mfs$HttpLog$_Fields[_fields.ordinal()];
        if (i2 == 1) {
            return getCommon();
        }
        if (i2 == 2) {
            return getCategory();
        }
        if (i2 == 3) {
            return getHttpApi();
        }
        if (i2 == 4) {
            return getPassport();
        }
        throw new IllegalStateException();
    }

    public HttpApi getHttpApi() {
        return this.httpApi;
    }

    public Passport getPassport() {
        return this.passport;
    }

    public int hashCode() {
        return 0;
    }

    public boolean isSet(_Fields _fields) {
        if (_fields == null) {
            throw new IllegalArgumentException();
        }
        int i2 = AnonymousClass1.$SwitchMap$com$xiaomi$common$logger$thrift$mfs$HttpLog$_Fields[_fields.ordinal()];
        if (i2 == 1) {
            return isSetCommon();
        }
        if (i2 == 2) {
            return isSetCategory();
        }
        if (i2 == 3) {
            return isSetHttpApi();
        }
        if (i2 == 4) {
            return isSetPassport();
        }
        throw new IllegalStateException();
    }

    public boolean isSetCategory() {
        return this.category != null;
    }

    public boolean isSetCommon() {
        return this.common != null;
    }

    public boolean isSetHttpApi() {
        return this.httpApi != null;
    }

    public boolean isSetPassport() {
        return this.passport != null;
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
                        } else if (b2 == 12) {
                            Passport passport = new Passport();
                            this.passport = passport;
                            passport.read(dVar);
                        } else {
                            e.a(dVar, b2);
                        }
                    } else if (b2 == 12) {
                        HttpApi httpApi = new HttpApi();
                        this.httpApi = httpApi;
                        httpApi.read(dVar);
                    } else {
                        e.a(dVar, b2);
                    }
                } else if (b2 == 11) {
                    this.category = dVar.p();
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
    }

    public HttpLog setCategory(String str) {
        this.category = str;
        return this;
    }

    public void setCategoryIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.category = null;
    }

    public HttpLog setCommon(Common common) {
        this.common = common;
        return this;
    }

    public void setCommonIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.common = null;
    }

    public void setFieldValue(_Fields _fields, Object obj) {
        int i2 = AnonymousClass1.$SwitchMap$com$xiaomi$common$logger$thrift$mfs$HttpLog$_Fields[_fields.ordinal()];
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
                unsetCategory();
                return;
            } else {
                setCategory((String) obj);
                return;
            }
        }
        if (i2 == 3) {
            if (obj == null) {
                unsetHttpApi();
                return;
            } else {
                setHttpApi((HttpApi) obj);
                return;
            }
        }
        if (i2 != 4) {
            return;
        }
        if (obj == null) {
            unsetPassport();
        } else {
            setPassport((Passport) obj);
        }
    }

    public HttpLog setHttpApi(HttpApi httpApi) {
        this.httpApi = httpApi;
        return this;
    }

    public void setHttpApiIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.httpApi = null;
    }

    public HttpLog setPassport(Passport passport) {
        this.passport = passport;
        return this;
    }

    public void setPassportIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.passport = null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("HttpLog(");
        sb.append("common:");
        Common common = this.common;
        if (common == null) {
            sb.append("null");
        } else {
            sb.append(common);
        }
        sb.append(", ");
        sb.append("category:");
        String str = this.category;
        if (str == null) {
            sb.append("null");
        } else {
            sb.append(str);
        }
        if (isSetHttpApi()) {
            sb.append(", ");
            sb.append("httpApi:");
            HttpApi httpApi = this.httpApi;
            if (httpApi == null) {
                sb.append("null");
            } else {
                sb.append(httpApi);
            }
        }
        if (isSetPassport()) {
            sb.append(", ");
            sb.append("passport:");
            Passport passport = this.passport;
            if (passport == null) {
                sb.append("null");
            } else {
                sb.append(passport);
            }
        }
        sb.append(com.xiaomi.push.mpcd.Constants.SEPARATOR_RIGHT_PARENTESIS);
        return sb.toString();
    }

    public void unsetCategory() {
        this.category = null;
    }

    public void unsetCommon() {
        this.common = null;
    }

    public void unsetHttpApi() {
        this.httpApi = null;
    }

    public void unsetPassport() {
        this.passport = null;
    }

    public void validate() throws TException {
        if (this.common == null) {
            throw new TProtocolException("Required field 'common' was not present! Struct: " + toString());
        }
        if (this.category != null) {
            return;
        }
        throw new TProtocolException("Required field 'category' was not present! Struct: " + toString());
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
        if (this.category != null) {
            dVar.w(CATEGORY_FIELD_DESC);
            dVar.I(this.category);
            dVar.x();
        }
        if (this.httpApi != null && isSetHttpApi()) {
            dVar.w(HTTP_API_FIELD_DESC);
            this.httpApi.write(dVar);
            dVar.x();
        }
        if (this.passport != null && isSetPassport()) {
            dVar.w(PASSPORT_FIELD_DESC);
            this.passport.write(dVar);
            dVar.x();
        }
        dVar.y();
        dVar.K();
    }

    @Override // java.lang.Comparable
    public int compareTo(HttpLog httpLog) {
        int iF;
        int iF2;
        int iG;
        int iF3;
        if (!getClass().equals(httpLog.getClass())) {
            return getClass().getName().compareTo(httpLog.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetCommon()).compareTo(Boolean.valueOf(httpLog.isSetCommon()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetCommon() && (iF3 = g0.a.f(this.common, httpLog.common)) != 0) {
            return iF3;
        }
        int iCompareTo2 = Boolean.valueOf(isSetCategory()).compareTo(Boolean.valueOf(httpLog.isSetCategory()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (isSetCategory() && (iG = g0.a.g(this.category, httpLog.category)) != 0) {
            return iG;
        }
        int iCompareTo3 = Boolean.valueOf(isSetHttpApi()).compareTo(Boolean.valueOf(httpLog.isSetHttpApi()));
        if (iCompareTo3 != 0) {
            return iCompareTo3;
        }
        if (isSetHttpApi() && (iF2 = g0.a.f(this.httpApi, httpLog.httpApi)) != 0) {
            return iF2;
        }
        int iCompareTo4 = Boolean.valueOf(isSetPassport()).compareTo(Boolean.valueOf(httpLog.isSetPassport()));
        if (iCompareTo4 != 0) {
            return iCompareTo4;
        }
        if (!isSetPassport() || (iF = g0.a.f(this.passport, httpLog.passport)) == 0) {
            return 0;
        }
        return iF;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public HttpLog m6deepCopy() {
        return new HttpLog(this);
    }

    public HttpLog(Common common, String str) {
        this();
        this.common = common;
        this.category = str;
    }

    public boolean equals(HttpLog httpLog) {
        if (httpLog == null) {
            return false;
        }
        boolean zIsSetCommon = isSetCommon();
        boolean zIsSetCommon2 = httpLog.isSetCommon();
        if ((zIsSetCommon || zIsSetCommon2) && !(zIsSetCommon && zIsSetCommon2 && this.common.equals(httpLog.common))) {
            return false;
        }
        boolean zIsSetCategory = isSetCategory();
        boolean zIsSetCategory2 = httpLog.isSetCategory();
        if ((zIsSetCategory || zIsSetCategory2) && !(zIsSetCategory && zIsSetCategory2 && this.category.equals(httpLog.category))) {
            return false;
        }
        boolean zIsSetHttpApi = isSetHttpApi();
        boolean zIsSetHttpApi2 = httpLog.isSetHttpApi();
        if ((zIsSetHttpApi || zIsSetHttpApi2) && !(zIsSetHttpApi && zIsSetHttpApi2 && this.httpApi.equals(httpLog.httpApi))) {
            return false;
        }
        boolean zIsSetPassport = isSetPassport();
        boolean zIsSetPassport2 = httpLog.isSetPassport();
        if (zIsSetPassport || zIsSetPassport2) {
            return zIsSetPassport && zIsSetPassport2 && this.passport.equals(httpLog.passport);
        }
        return true;
    }

    public HttpLog(HttpLog httpLog) {
        if (httpLog.isSetCommon()) {
            this.common = new Common(httpLog.common);
        }
        if (httpLog.isSetCategory()) {
            this.category = httpLog.category;
        }
        if (httpLog.isSetHttpApi()) {
            this.httpApi = new HttpApi(httpLog.httpApi);
        }
        if (httpLog.isSetPassport()) {
            this.passport = new Passport(httpLog.passport);
        }
    }
}
