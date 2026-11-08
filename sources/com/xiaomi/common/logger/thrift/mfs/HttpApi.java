package com.xiaomi.common.logger.thrift.mfs;

import com.xiaomi.mipush.sdk.Constants;
import java.io.Serializable;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import miui.yellowpage.YellowPageStatistic;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.meta_data.SetMetaData;
import org.apache.thrift.meta_data.StructMetaData;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.protocol.a;
import org.apache.thrift.protocol.d;
import org.apache.thrift.protocol.e;
import org.apache.thrift.protocol.f;
import org.apache.thrift.protocol.g;

/* JADX INFO: loaded from: classes.dex */
public class HttpApi implements TBase<HttpApi, _Fields>, Serializable, Cloneable {
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    private String app_name;
    private String app_version;
    private String category;
    private String client_ip;
    private Set<HostInfo> host_info;
    private Location location;
    private String network;
    private String uuid;
    private String version;
    private String version_type;
    private static final g STRUCT_DESC = new g("HttpApi");
    private static final a CATEGORY_FIELD_DESC = new a(YellowPageStatistic.Display.CATEGORY, (byte) 11, 1);
    private static final a UUID_FIELD_DESC = new a("uuid", (byte) 11, 2);
    private static final a VERSION_FIELD_DESC = new a("version", (byte) 11, 3);
    private static final a NETWORK_FIELD_DESC = new a("network", (byte) 11, 4);
    private static final a CLIENT_IP_FIELD_DESC = new a("client_ip", (byte) 11, 5);
    private static final a LOCATION_FIELD_DESC = new a("location", (byte) 12, 6);
    private static final a HOST_INFO_FIELD_DESC = new a("host_info", (byte) 14, 7);
    private static final a VERSION_TYPE_FIELD_DESC = new a("version_type", (byte) 11, 8);
    private static final a APP_NAME_FIELD_DESC = new a("app_name", (byte) 11, 9);
    private static final a APP_VERSION_FIELD_DESC = new a(Constants.EXTRA_KEY_APP_VERSION, (byte) 11, 10);

    /* JADX INFO: renamed from: com.xiaomi.common.logger.thrift.mfs.HttpApi$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$xiaomi$common$logger$thrift$mfs$HttpApi$_Fields;

        static {
            int[] iArr = new int[_Fields.values().length];
            $SwitchMap$com$xiaomi$common$logger$thrift$mfs$HttpApi$_Fields = iArr;
            try {
                iArr[_Fields.CATEGORY.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$HttpApi$_Fields[_Fields.UUID.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$HttpApi$_Fields[_Fields.VERSION.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$HttpApi$_Fields[_Fields.NETWORK.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$HttpApi$_Fields[_Fields.CLIENT_IP.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$HttpApi$_Fields[_Fields.LOCATION.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$HttpApi$_Fields[_Fields.HOST_INFO.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$HttpApi$_Fields[_Fields.VERSION_TYPE.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$HttpApi$_Fields[_Fields.APP_NAME.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$HttpApi$_Fields[_Fields.APP_VERSION.ordinal()] = 10;
            } catch (NoSuchFieldError unused10) {
            }
        }
    }

    public enum _Fields {
        CATEGORY(1, YellowPageStatistic.Display.CATEGORY),
        UUID(2, "uuid"),
        VERSION(3, "version"),
        NETWORK(4, "network"),
        CLIENT_IP(5, "client_ip"),
        LOCATION(6, "location"),
        HOST_INFO(7, "host_info"),
        VERSION_TYPE(8, "version_type"),
        APP_NAME(9, "app_name"),
        APP_VERSION(10, Constants.EXTRA_KEY_APP_VERSION);

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
                    return CATEGORY;
                case 2:
                    return UUID;
                case 3:
                    return VERSION;
                case 4:
                    return NETWORK;
                case 5:
                    return CLIENT_IP;
                case 6:
                    return LOCATION;
                case 7:
                    return HOST_INFO;
                case 8:
                    return VERSION_TYPE;
                case 9:
                    return APP_NAME;
                case 10:
                    return APP_VERSION;
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
        enumMap.put(_Fields.CATEGORY, new FieldMetaData(YellowPageStatistic.Display.CATEGORY, (byte) 1, new FieldValueMetaData((byte) 11)));
        enumMap.put(_Fields.UUID, new FieldMetaData("uuid", (byte) 1, new FieldValueMetaData((byte) 11)));
        enumMap.put(_Fields.VERSION, new FieldMetaData("version", (byte) 1, new FieldValueMetaData((byte) 11)));
        enumMap.put(_Fields.NETWORK, new FieldMetaData("network", (byte) 1, new FieldValueMetaData((byte) 11)));
        enumMap.put(_Fields.CLIENT_IP, new FieldMetaData("client_ip", (byte) 2, new FieldValueMetaData((byte) 11)));
        enumMap.put(_Fields.LOCATION, new FieldMetaData("location", (byte) 2, new StructMetaData((byte) 12, Location.class)));
        enumMap.put(_Fields.HOST_INFO, new FieldMetaData("host_info", (byte) 2, new SetMetaData((byte) 14, new StructMetaData((byte) 12, HostInfo.class))));
        enumMap.put(_Fields.VERSION_TYPE, new FieldMetaData("version_type", (byte) 2, new FieldValueMetaData((byte) 11)));
        enumMap.put(_Fields.APP_NAME, new FieldMetaData("app_name", (byte) 2, new FieldValueMetaData((byte) 11)));
        enumMap.put(_Fields.APP_VERSION, new FieldMetaData(Constants.EXTRA_KEY_APP_VERSION, (byte) 2, new FieldValueMetaData((byte) 11)));
        Map<_Fields, FieldMetaData> mapUnmodifiableMap = Collections.unmodifiableMap(enumMap);
        metaDataMap = mapUnmodifiableMap;
        FieldMetaData.b(HttpApi.class, mapUnmodifiableMap);
    }

    public HttpApi() {
        this.category = "";
        this.version_type = "";
        this.app_name = "";
        this.app_version = "";
    }

    public void addToHost_info(HostInfo hostInfo) {
        if (this.host_info == null) {
            this.host_info = new HashSet();
        }
        this.host_info.add(hostInfo);
    }

    public void clear() {
        this.category = "";
        this.uuid = null;
        this.version = null;
        this.network = null;
        this.client_ip = null;
        this.location = null;
        this.host_info = null;
        this.version_type = "";
        this.app_name = "";
        this.app_version = "";
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof HttpApi)) {
            return equals((HttpApi) obj);
        }
        return false;
    }

    public _Fields fieldForId(int i2) {
        return _Fields.findByThriftId(i2);
    }

    public String getApp_name() {
        return this.app_name;
    }

    public String getApp_version() {
        return this.app_version;
    }

    public String getCategory() {
        return this.category;
    }

    public String getClient_ip() {
        return this.client_ip;
    }

    public Object getFieldValue(_Fields _fields) {
        switch (AnonymousClass1.$SwitchMap$com$xiaomi$common$logger$thrift$mfs$HttpApi$_Fields[_fields.ordinal()]) {
            case 1:
                return getCategory();
            case 2:
                return getUuid();
            case 3:
                return getVersion();
            case 4:
                return getNetwork();
            case 5:
                return getClient_ip();
            case 6:
                return getLocation();
            case 7:
                return getHost_info();
            case 8:
                return getVersion_type();
            case 9:
                return getApp_name();
            case 10:
                return getApp_version();
            default:
                throw new IllegalStateException();
        }
    }

    public Set<HostInfo> getHost_info() {
        return this.host_info;
    }

    public Iterator<HostInfo> getHost_infoIterator() {
        Set<HostInfo> set = this.host_info;
        if (set == null) {
            return null;
        }
        return set.iterator();
    }

    public int getHost_infoSize() {
        Set<HostInfo> set = this.host_info;
        if (set == null) {
            return 0;
        }
        return set.size();
    }

    public Location getLocation() {
        return this.location;
    }

    public String getNetwork() {
        return this.network;
    }

    public String getUuid() {
        return this.uuid;
    }

    public String getVersion() {
        return this.version;
    }

    public String getVersion_type() {
        return this.version_type;
    }

    public int hashCode() {
        return 0;
    }

    public boolean isSet(_Fields _fields) {
        if (_fields == null) {
            throw new IllegalArgumentException();
        }
        switch (AnonymousClass1.$SwitchMap$com$xiaomi$common$logger$thrift$mfs$HttpApi$_Fields[_fields.ordinal()]) {
            case 1:
                return isSetCategory();
            case 2:
                return isSetUuid();
            case 3:
                return isSetVersion();
            case 4:
                return isSetNetwork();
            case 5:
                return isSetClient_ip();
            case 6:
                return isSetLocation();
            case 7:
                return isSetHost_info();
            case 8:
                return isSetVersion_type();
            case 9:
                return isSetApp_name();
            case 10:
                return isSetApp_version();
            default:
                throw new IllegalStateException();
        }
    }

    public boolean isSetApp_name() {
        return this.app_name != null;
    }

    public boolean isSetApp_version() {
        return this.app_version != null;
    }

    public boolean isSetCategory() {
        return this.category != null;
    }

    public boolean isSetClient_ip() {
        return this.client_ip != null;
    }

    public boolean isSetHost_info() {
        return this.host_info != null;
    }

    public boolean isSetLocation() {
        return this.location != null;
    }

    public boolean isSetNetwork() {
        return this.network != null;
    }

    public boolean isSetUuid() {
        return this.uuid != null;
    }

    public boolean isSetVersion() {
        return this.version != null;
    }

    public boolean isSetVersion_type() {
        return this.version_type != null;
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
            switch (aVarE.f2151c) {
                case 1:
                    if (b2 == 11) {
                        this.category = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 2:
                    if (b2 == 11) {
                        this.uuid = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 3:
                    if (b2 == 11) {
                        this.version = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 4:
                    if (b2 == 11) {
                        this.network = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 5:
                    if (b2 == 11) {
                        this.client_ip = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 6:
                    if (b2 == 12) {
                        Location location = new Location();
                        this.location = location;
                        location.read(dVar);
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 7:
                    if (b2 == 14) {
                        f fVarN = dVar.n();
                        this.host_info = new HashSet(fVarN.f2160b * 2);
                        for (int i2 = 0; i2 < fVarN.f2160b; i2++) {
                            HostInfo hostInfo = new HostInfo();
                            hostInfo.read(dVar);
                            this.host_info.add(hostInfo);
                        }
                        dVar.o();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 8:
                    if (b2 == 11) {
                        this.version_type = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 9:
                    if (b2 == 11) {
                        this.app_name = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 10:
                    if (b2 == 11) {
                        this.app_version = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                default:
                    e.a(dVar, b2);
                    break;
            }
            dVar.f();
        }
    }

    public HttpApi setApp_name(String str) {
        this.app_name = str;
        return this;
    }

    public void setApp_nameIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.app_name = null;
    }

    public HttpApi setApp_version(String str) {
        this.app_version = str;
        return this;
    }

    public void setApp_versionIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.app_version = null;
    }

    public HttpApi setCategory(String str) {
        this.category = str;
        return this;
    }

    public void setCategoryIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.category = null;
    }

    public HttpApi setClient_ip(String str) {
        this.client_ip = str;
        return this;
    }

    public void setClient_ipIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.client_ip = null;
    }

    public void setFieldValue(_Fields _fields, Object obj) {
        switch (AnonymousClass1.$SwitchMap$com$xiaomi$common$logger$thrift$mfs$HttpApi$_Fields[_fields.ordinal()]) {
            case 1:
                if (obj != null) {
                    setCategory((String) obj);
                } else {
                    unsetCategory();
                }
                break;
            case 2:
                if (obj != null) {
                    setUuid((String) obj);
                } else {
                    unsetUuid();
                }
                break;
            case 3:
                if (obj != null) {
                    setVersion((String) obj);
                } else {
                    unsetVersion();
                }
                break;
            case 4:
                if (obj != null) {
                    setNetwork((String) obj);
                } else {
                    unsetNetwork();
                }
                break;
            case 5:
                if (obj != null) {
                    setClient_ip((String) obj);
                } else {
                    unsetClient_ip();
                }
                break;
            case 6:
                if (obj != null) {
                    setLocation((Location) obj);
                } else {
                    unsetLocation();
                }
                break;
            case 7:
                if (obj != null) {
                    setHost_info((Set) obj);
                } else {
                    unsetHost_info();
                }
                break;
            case 8:
                if (obj != null) {
                    setVersion_type((String) obj);
                } else {
                    unsetVersion_type();
                }
                break;
            case 9:
                if (obj != null) {
                    setApp_name((String) obj);
                } else {
                    unsetApp_name();
                }
                break;
            case 10:
                if (obj != null) {
                    setApp_version((String) obj);
                } else {
                    unsetApp_version();
                }
                break;
        }
    }

    public HttpApi setHost_info(Set<HostInfo> set) {
        this.host_info = set;
        return this;
    }

    public void setHost_infoIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.host_info = null;
    }

    public HttpApi setLocation(Location location) {
        this.location = location;
        return this;
    }

    public void setLocationIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.location = null;
    }

    public HttpApi setNetwork(String str) {
        this.network = str;
        return this;
    }

    public void setNetworkIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.network = null;
    }

    public HttpApi setUuid(String str) {
        this.uuid = str;
        return this;
    }

    public void setUuidIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.uuid = null;
    }

    public HttpApi setVersion(String str) {
        this.version = str;
        return this;
    }

    public void setVersionIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.version = null;
    }

    public HttpApi setVersion_type(String str) {
        this.version_type = str;
        return this;
    }

    public void setVersion_typeIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.version_type = null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("HttpApi(");
        sb.append("category:");
        String str = this.category;
        if (str == null) {
            sb.append("null");
        } else {
            sb.append(str);
        }
        sb.append(", ");
        sb.append("uuid:");
        String str2 = this.uuid;
        if (str2 == null) {
            sb.append("null");
        } else {
            sb.append(str2);
        }
        sb.append(", ");
        sb.append("version:");
        String str3 = this.version;
        if (str3 == null) {
            sb.append("null");
        } else {
            sb.append(str3);
        }
        sb.append(", ");
        sb.append("network:");
        String str4 = this.network;
        if (str4 == null) {
            sb.append("null");
        } else {
            sb.append(str4);
        }
        if (isSetClient_ip()) {
            sb.append(", ");
            sb.append("client_ip:");
            String str5 = this.client_ip;
            if (str5 == null) {
                sb.append("null");
            } else {
                sb.append(str5);
            }
        }
        if (isSetLocation()) {
            sb.append(", ");
            sb.append("location:");
            Location location = this.location;
            if (location == null) {
                sb.append("null");
            } else {
                sb.append(location);
            }
        }
        if (isSetHost_info()) {
            sb.append(", ");
            sb.append("host_info:");
            Set<HostInfo> set = this.host_info;
            if (set == null) {
                sb.append("null");
            } else {
                sb.append(set);
            }
        }
        if (isSetVersion_type()) {
            sb.append(", ");
            sb.append("version_type:");
            String str6 = this.version_type;
            if (str6 == null) {
                sb.append("null");
            } else {
                sb.append(str6);
            }
        }
        if (isSetApp_name()) {
            sb.append(", ");
            sb.append("app_name:");
            String str7 = this.app_name;
            if (str7 == null) {
                sb.append("null");
            } else {
                sb.append(str7);
            }
        }
        if (isSetApp_version()) {
            sb.append(", ");
            sb.append("app_version:");
            String str8 = this.app_version;
            if (str8 == null) {
                sb.append("null");
            } else {
                sb.append(str8);
            }
        }
        sb.append(com.xiaomi.push.mpcd.Constants.SEPARATOR_RIGHT_PARENTESIS);
        return sb.toString();
    }

    public void unsetApp_name() {
        this.app_name = null;
    }

    public void unsetApp_version() {
        this.app_version = null;
    }

    public void unsetCategory() {
        this.category = null;
    }

    public void unsetClient_ip() {
        this.client_ip = null;
    }

    public void unsetHost_info() {
        this.host_info = null;
    }

    public void unsetLocation() {
        this.location = null;
    }

    public void unsetNetwork() {
        this.network = null;
    }

    public void unsetUuid() {
        this.uuid = null;
    }

    public void unsetVersion() {
        this.version = null;
    }

    public void unsetVersion_type() {
        this.version_type = null;
    }

    public void validate() throws TException {
        if (this.category == null) {
            throw new TProtocolException("Required field 'category' was not present! Struct: " + toString());
        }
        if (this.uuid == null) {
            throw new TProtocolException("Required field 'uuid' was not present! Struct: " + toString());
        }
        if (this.version == null) {
            throw new TProtocolException("Required field 'version' was not present! Struct: " + toString());
        }
        if (this.network != null) {
            return;
        }
        throw new TProtocolException("Required field 'network' was not present! Struct: " + toString());
    }

    @Override // org.apache.thrift.TBase
    public void write(d dVar) throws TException {
        validate();
        dVar.J(STRUCT_DESC);
        if (this.category != null) {
            dVar.w(CATEGORY_FIELD_DESC);
            dVar.I(this.category);
            dVar.x();
        }
        if (this.uuid != null) {
            dVar.w(UUID_FIELD_DESC);
            dVar.I(this.uuid);
            dVar.x();
        }
        if (this.version != null) {
            dVar.w(VERSION_FIELD_DESC);
            dVar.I(this.version);
            dVar.x();
        }
        if (this.network != null) {
            dVar.w(NETWORK_FIELD_DESC);
            dVar.I(this.network);
            dVar.x();
        }
        if (this.client_ip != null && isSetClient_ip()) {
            dVar.w(CLIENT_IP_FIELD_DESC);
            dVar.I(this.client_ip);
            dVar.x();
        }
        if (this.location != null && isSetLocation()) {
            dVar.w(LOCATION_FIELD_DESC);
            this.location.write(dVar);
            dVar.x();
        }
        if (this.host_info != null && isSetHost_info()) {
            dVar.w(HOST_INFO_FIELD_DESC);
            dVar.G(new f((byte) 12, this.host_info.size()));
            Iterator<HostInfo> it = this.host_info.iterator();
            while (it.hasNext()) {
                it.next().write(dVar);
            }
            dVar.H();
            dVar.x();
        }
        if (this.version_type != null && isSetVersion_type()) {
            dVar.w(VERSION_TYPE_FIELD_DESC);
            dVar.I(this.version_type);
            dVar.x();
        }
        if (this.app_name != null && isSetApp_name()) {
            dVar.w(APP_NAME_FIELD_DESC);
            dVar.I(this.app_name);
            dVar.x();
        }
        if (this.app_version != null && isSetApp_version()) {
            dVar.w(APP_VERSION_FIELD_DESC);
            dVar.I(this.app_version);
            dVar.x();
        }
        dVar.y();
        dVar.K();
    }

    @Override // java.lang.Comparable
    public int compareTo(HttpApi httpApi) {
        int iG;
        int iG2;
        int iG3;
        int iJ;
        int iF;
        int iG4;
        int iG5;
        int iG6;
        int iG7;
        int iG8;
        if (!getClass().equals(httpApi.getClass())) {
            return getClass().getName().compareTo(httpApi.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetCategory()).compareTo(Boolean.valueOf(httpApi.isSetCategory()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetCategory() && (iG8 = g0.a.g(this.category, httpApi.category)) != 0) {
            return iG8;
        }
        int iCompareTo2 = Boolean.valueOf(isSetUuid()).compareTo(Boolean.valueOf(httpApi.isSetUuid()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (isSetUuid() && (iG7 = g0.a.g(this.uuid, httpApi.uuid)) != 0) {
            return iG7;
        }
        int iCompareTo3 = Boolean.valueOf(isSetVersion()).compareTo(Boolean.valueOf(httpApi.isSetVersion()));
        if (iCompareTo3 != 0) {
            return iCompareTo3;
        }
        if (isSetVersion() && (iG6 = g0.a.g(this.version, httpApi.version)) != 0) {
            return iG6;
        }
        int iCompareTo4 = Boolean.valueOf(isSetNetwork()).compareTo(Boolean.valueOf(httpApi.isSetNetwork()));
        if (iCompareTo4 != 0) {
            return iCompareTo4;
        }
        if (isSetNetwork() && (iG5 = g0.a.g(this.network, httpApi.network)) != 0) {
            return iG5;
        }
        int iCompareTo5 = Boolean.valueOf(isSetClient_ip()).compareTo(Boolean.valueOf(httpApi.isSetClient_ip()));
        if (iCompareTo5 != 0) {
            return iCompareTo5;
        }
        if (isSetClient_ip() && (iG4 = g0.a.g(this.client_ip, httpApi.client_ip)) != 0) {
            return iG4;
        }
        int iCompareTo6 = Boolean.valueOf(isSetLocation()).compareTo(Boolean.valueOf(httpApi.isSetLocation()));
        if (iCompareTo6 != 0) {
            return iCompareTo6;
        }
        if (isSetLocation() && (iF = g0.a.f(this.location, httpApi.location)) != 0) {
            return iF;
        }
        int iCompareTo7 = Boolean.valueOf(isSetHost_info()).compareTo(Boolean.valueOf(httpApi.isSetHost_info()));
        if (iCompareTo7 != 0) {
            return iCompareTo7;
        }
        if (isSetHost_info() && (iJ = g0.a.j(this.host_info, httpApi.host_info)) != 0) {
            return iJ;
        }
        int iCompareTo8 = Boolean.valueOf(isSetVersion_type()).compareTo(Boolean.valueOf(httpApi.isSetVersion_type()));
        if (iCompareTo8 != 0) {
            return iCompareTo8;
        }
        if (isSetVersion_type() && (iG3 = g0.a.g(this.version_type, httpApi.version_type)) != 0) {
            return iG3;
        }
        int iCompareTo9 = Boolean.valueOf(isSetApp_name()).compareTo(Boolean.valueOf(httpApi.isSetApp_name()));
        if (iCompareTo9 != 0) {
            return iCompareTo9;
        }
        if (isSetApp_name() && (iG2 = g0.a.g(this.app_name, httpApi.app_name)) != 0) {
            return iG2;
        }
        int iCompareTo10 = Boolean.valueOf(isSetApp_version()).compareTo(Boolean.valueOf(httpApi.isSetApp_version()));
        if (iCompareTo10 != 0) {
            return iCompareTo10;
        }
        if (!isSetApp_version() || (iG = g0.a.g(this.app_version, httpApi.app_version)) == 0) {
            return 0;
        }
        return iG;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public HttpApi m5deepCopy() {
        return new HttpApi(this);
    }

    public boolean equals(HttpApi httpApi) {
        if (httpApi == null) {
            return false;
        }
        boolean zIsSetCategory = isSetCategory();
        boolean zIsSetCategory2 = httpApi.isSetCategory();
        if ((zIsSetCategory || zIsSetCategory2) && !(zIsSetCategory && zIsSetCategory2 && this.category.equals(httpApi.category))) {
            return false;
        }
        boolean zIsSetUuid = isSetUuid();
        boolean zIsSetUuid2 = httpApi.isSetUuid();
        if ((zIsSetUuid || zIsSetUuid2) && !(zIsSetUuid && zIsSetUuid2 && this.uuid.equals(httpApi.uuid))) {
            return false;
        }
        boolean zIsSetVersion = isSetVersion();
        boolean zIsSetVersion2 = httpApi.isSetVersion();
        if ((zIsSetVersion || zIsSetVersion2) && !(zIsSetVersion && zIsSetVersion2 && this.version.equals(httpApi.version))) {
            return false;
        }
        boolean zIsSetNetwork = isSetNetwork();
        boolean zIsSetNetwork2 = httpApi.isSetNetwork();
        if ((zIsSetNetwork || zIsSetNetwork2) && !(zIsSetNetwork && zIsSetNetwork2 && this.network.equals(httpApi.network))) {
            return false;
        }
        boolean zIsSetClient_ip = isSetClient_ip();
        boolean zIsSetClient_ip2 = httpApi.isSetClient_ip();
        if ((zIsSetClient_ip || zIsSetClient_ip2) && !(zIsSetClient_ip && zIsSetClient_ip2 && this.client_ip.equals(httpApi.client_ip))) {
            return false;
        }
        boolean zIsSetLocation = isSetLocation();
        boolean zIsSetLocation2 = httpApi.isSetLocation();
        if ((zIsSetLocation || zIsSetLocation2) && !(zIsSetLocation && zIsSetLocation2 && this.location.equals(httpApi.location))) {
            return false;
        }
        boolean zIsSetHost_info = isSetHost_info();
        boolean zIsSetHost_info2 = httpApi.isSetHost_info();
        if ((zIsSetHost_info || zIsSetHost_info2) && !(zIsSetHost_info && zIsSetHost_info2 && this.host_info.equals(httpApi.host_info))) {
            return false;
        }
        boolean zIsSetVersion_type = isSetVersion_type();
        boolean zIsSetVersion_type2 = httpApi.isSetVersion_type();
        if ((zIsSetVersion_type || zIsSetVersion_type2) && !(zIsSetVersion_type && zIsSetVersion_type2 && this.version_type.equals(httpApi.version_type))) {
            return false;
        }
        boolean zIsSetApp_name = isSetApp_name();
        boolean zIsSetApp_name2 = httpApi.isSetApp_name();
        if ((zIsSetApp_name || zIsSetApp_name2) && !(zIsSetApp_name && zIsSetApp_name2 && this.app_name.equals(httpApi.app_name))) {
            return false;
        }
        boolean zIsSetApp_version = isSetApp_version();
        boolean zIsSetApp_version2 = httpApi.isSetApp_version();
        if (zIsSetApp_version || zIsSetApp_version2) {
            return zIsSetApp_version && zIsSetApp_version2 && this.app_version.equals(httpApi.app_version);
        }
        return true;
    }

    public HttpApi(String str, String str2, String str3, String str4) {
        this();
        this.category = str;
        this.uuid = str2;
        this.version = str3;
        this.network = str4;
    }

    public HttpApi(HttpApi httpApi) {
        if (httpApi.isSetCategory()) {
            this.category = httpApi.category;
        }
        if (httpApi.isSetUuid()) {
            this.uuid = httpApi.uuid;
        }
        if (httpApi.isSetVersion()) {
            this.version = httpApi.version;
        }
        if (httpApi.isSetNetwork()) {
            this.network = httpApi.network;
        }
        if (httpApi.isSetClient_ip()) {
            this.client_ip = httpApi.client_ip;
        }
        if (httpApi.isSetLocation()) {
            this.location = new Location(httpApi.location);
        }
        if (httpApi.isSetHost_info()) {
            HashSet hashSet = new HashSet();
            Iterator<HostInfo> it = httpApi.host_info.iterator();
            while (it.hasNext()) {
                hashSet.add(new HostInfo(it.next()));
            }
            this.host_info = hashSet;
        }
        if (httpApi.isSetVersion_type()) {
            this.version_type = httpApi.version_type;
        }
        if (httpApi.isSetApp_name()) {
            this.app_name = httpApi.app_name;
        }
        if (httpApi.isSetApp_version()) {
            this.app_version = httpApi.app_version;
        }
    }
}
