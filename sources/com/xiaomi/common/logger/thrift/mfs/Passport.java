package com.xiaomi.common.logger.thrift.mfs;

import com.xiaomi.push.mpcd.Constants;
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
public class Passport implements TBase<Passport, _Fields>, Serializable, Cloneable {
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    private String category;
    private Set<PassportHostInfo> host_info;
    private Location location;
    private String network;
    private String rid;
    private String uuid;
    private String version;
    private static final g STRUCT_DESC = new g("Passport");
    private static final a CATEGORY_FIELD_DESC = new a(YellowPageStatistic.Display.CATEGORY, (byte) 11, 1);
    private static final a UUID_FIELD_DESC = new a("uuid", (byte) 11, 2);
    private static final a VERSION_FIELD_DESC = new a("version", (byte) 11, 3);
    private static final a NETWORK_FIELD_DESC = new a("network", (byte) 11, 4);
    private static final a RID_FIELD_DESC = new a("rid", (byte) 11, 5);
    private static final a LOCATION_FIELD_DESC = new a("location", (byte) 12, 6);
    private static final a HOST_INFO_FIELD_DESC = new a("host_info", (byte) 14, 7);

    /* JADX INFO: renamed from: com.xiaomi.common.logger.thrift.mfs.Passport$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$xiaomi$common$logger$thrift$mfs$Passport$_Fields;

        static {
            int[] iArr = new int[_Fields.values().length];
            $SwitchMap$com$xiaomi$common$logger$thrift$mfs$Passport$_Fields = iArr;
            try {
                iArr[_Fields.CATEGORY.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$Passport$_Fields[_Fields.UUID.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$Passport$_Fields[_Fields.VERSION.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$Passport$_Fields[_Fields.NETWORK.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$Passport$_Fields[_Fields.RID.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$Passport$_Fields[_Fields.LOCATION.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$xiaomi$common$logger$thrift$mfs$Passport$_Fields[_Fields.HOST_INFO.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
        }
    }

    public enum _Fields {
        CATEGORY(1, YellowPageStatistic.Display.CATEGORY),
        UUID(2, "uuid"),
        VERSION(3, "version"),
        NETWORK(4, "network"),
        RID(5, "rid"),
        LOCATION(6, "location"),
        HOST_INFO(7, "host_info");

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
                    return RID;
                case 6:
                    return LOCATION;
                case 7:
                    return HOST_INFO;
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
        enumMap.put(_Fields.RID, new FieldMetaData("rid", (byte) 1, new FieldValueMetaData((byte) 11)));
        enumMap.put(_Fields.LOCATION, new FieldMetaData("location", (byte) 2, new StructMetaData((byte) 12, Location.class)));
        enumMap.put(_Fields.HOST_INFO, new FieldMetaData("host_info", (byte) 2, new SetMetaData((byte) 14, new StructMetaData((byte) 12, PassportHostInfo.class))));
        Map<_Fields, FieldMetaData> mapUnmodifiableMap = Collections.unmodifiableMap(enumMap);
        metaDataMap = mapUnmodifiableMap;
        FieldMetaData.b(Passport.class, mapUnmodifiableMap);
    }

    public Passport() {
        this.category = "";
    }

    public void addToHost_info(PassportHostInfo passportHostInfo) {
        if (this.host_info == null) {
            this.host_info = new HashSet();
        }
        this.host_info.add(passportHostInfo);
    }

    public void clear() {
        this.category = "";
        this.uuid = null;
        this.version = null;
        this.network = null;
        this.rid = null;
        this.location = null;
        this.host_info = null;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof Passport)) {
            return equals((Passport) obj);
        }
        return false;
    }

    public _Fields fieldForId(int i2) {
        return _Fields.findByThriftId(i2);
    }

    public String getCategory() {
        return this.category;
    }

    public Object getFieldValue(_Fields _fields) {
        switch (AnonymousClass1.$SwitchMap$com$xiaomi$common$logger$thrift$mfs$Passport$_Fields[_fields.ordinal()]) {
            case 1:
                return getCategory();
            case 2:
                return getUuid();
            case 3:
                return getVersion();
            case 4:
                return getNetwork();
            case 5:
                return getRid();
            case 6:
                return getLocation();
            case 7:
                return getHost_info();
            default:
                throw new IllegalStateException();
        }
    }

    public Set<PassportHostInfo> getHost_info() {
        return this.host_info;
    }

    public Iterator<PassportHostInfo> getHost_infoIterator() {
        Set<PassportHostInfo> set = this.host_info;
        if (set == null) {
            return null;
        }
        return set.iterator();
    }

    public int getHost_infoSize() {
        Set<PassportHostInfo> set = this.host_info;
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

    public String getRid() {
        return this.rid;
    }

    public String getUuid() {
        return this.uuid;
    }

    public String getVersion() {
        return this.version;
    }

    public int hashCode() {
        return 0;
    }

    public boolean isSet(_Fields _fields) {
        if (_fields == null) {
            throw new IllegalArgumentException();
        }
        switch (AnonymousClass1.$SwitchMap$com$xiaomi$common$logger$thrift$mfs$Passport$_Fields[_fields.ordinal()]) {
            case 1:
                return isSetCategory();
            case 2:
                return isSetUuid();
            case 3:
                return isSetVersion();
            case 4:
                return isSetNetwork();
            case 5:
                return isSetRid();
            case 6:
                return isSetLocation();
            case 7:
                return isSetHost_info();
            default:
                throw new IllegalStateException();
        }
    }

    public boolean isSetCategory() {
        return this.category != null;
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

    public boolean isSetRid() {
        return this.rid != null;
    }

    public boolean isSetUuid() {
        return this.uuid != null;
    }

    public boolean isSetVersion() {
        return this.version != null;
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
                        this.rid = dVar.p();
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
                            PassportHostInfo passportHostInfo = new PassportHostInfo();
                            passportHostInfo.read(dVar);
                            this.host_info.add(passportHostInfo);
                        }
                        dVar.o();
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

    public Passport setCategory(String str) {
        this.category = str;
        return this;
    }

    public void setCategoryIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.category = null;
    }

    public void setFieldValue(_Fields _fields, Object obj) {
        switch (AnonymousClass1.$SwitchMap$com$xiaomi$common$logger$thrift$mfs$Passport$_Fields[_fields.ordinal()]) {
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
                    setRid((String) obj);
                } else {
                    unsetRid();
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
        }
    }

    public Passport setHost_info(Set<PassportHostInfo> set) {
        this.host_info = set;
        return this;
    }

    public void setHost_infoIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.host_info = null;
    }

    public Passport setLocation(Location location) {
        this.location = location;
        return this;
    }

    public void setLocationIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.location = null;
    }

    public Passport setNetwork(String str) {
        this.network = str;
        return this;
    }

    public void setNetworkIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.network = null;
    }

    public Passport setRid(String str) {
        this.rid = str;
        return this;
    }

    public void setRidIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.rid = null;
    }

    public Passport setUuid(String str) {
        this.uuid = str;
        return this;
    }

    public void setUuidIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.uuid = null;
    }

    public Passport setVersion(String str) {
        this.version = str;
        return this;
    }

    public void setVersionIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.version = null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Passport(");
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
        sb.append(", ");
        sb.append("rid:");
        String str5 = this.rid;
        if (str5 == null) {
            sb.append("null");
        } else {
            sb.append(str5);
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
            Set<PassportHostInfo> set = this.host_info;
            if (set == null) {
                sb.append("null");
            } else {
                sb.append(set);
            }
        }
        sb.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
        return sb.toString();
    }

    public void unsetCategory() {
        this.category = null;
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

    public void unsetRid() {
        this.rid = null;
    }

    public void unsetUuid() {
        this.uuid = null;
    }

    public void unsetVersion() {
        this.version = null;
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
        if (this.network == null) {
            throw new TProtocolException("Required field 'network' was not present! Struct: " + toString());
        }
        if (this.rid != null) {
            return;
        }
        throw new TProtocolException("Required field 'rid' was not present! Struct: " + toString());
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
        if (this.rid != null) {
            dVar.w(RID_FIELD_DESC);
            dVar.I(this.rid);
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
            Iterator<PassportHostInfo> it = this.host_info.iterator();
            while (it.hasNext()) {
                it.next().write(dVar);
            }
            dVar.H();
            dVar.x();
        }
        dVar.y();
        dVar.K();
    }

    @Override // java.lang.Comparable
    public int compareTo(Passport passport) {
        int iJ;
        int iF;
        int iG;
        int iG2;
        int iG3;
        int iG4;
        int iG5;
        if (!getClass().equals(passport.getClass())) {
            return getClass().getName().compareTo(passport.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetCategory()).compareTo(Boolean.valueOf(passport.isSetCategory()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetCategory() && (iG5 = g0.a.g(this.category, passport.category)) != 0) {
            return iG5;
        }
        int iCompareTo2 = Boolean.valueOf(isSetUuid()).compareTo(Boolean.valueOf(passport.isSetUuid()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (isSetUuid() && (iG4 = g0.a.g(this.uuid, passport.uuid)) != 0) {
            return iG4;
        }
        int iCompareTo3 = Boolean.valueOf(isSetVersion()).compareTo(Boolean.valueOf(passport.isSetVersion()));
        if (iCompareTo3 != 0) {
            return iCompareTo3;
        }
        if (isSetVersion() && (iG3 = g0.a.g(this.version, passport.version)) != 0) {
            return iG3;
        }
        int iCompareTo4 = Boolean.valueOf(isSetNetwork()).compareTo(Boolean.valueOf(passport.isSetNetwork()));
        if (iCompareTo4 != 0) {
            return iCompareTo4;
        }
        if (isSetNetwork() && (iG2 = g0.a.g(this.network, passport.network)) != 0) {
            return iG2;
        }
        int iCompareTo5 = Boolean.valueOf(isSetRid()).compareTo(Boolean.valueOf(passport.isSetRid()));
        if (iCompareTo5 != 0) {
            return iCompareTo5;
        }
        if (isSetRid() && (iG = g0.a.g(this.rid, passport.rid)) != 0) {
            return iG;
        }
        int iCompareTo6 = Boolean.valueOf(isSetLocation()).compareTo(Boolean.valueOf(passport.isSetLocation()));
        if (iCompareTo6 != 0) {
            return iCompareTo6;
        }
        if (isSetLocation() && (iF = g0.a.f(this.location, passport.location)) != 0) {
            return iF;
        }
        int iCompareTo7 = Boolean.valueOf(isSetHost_info()).compareTo(Boolean.valueOf(passport.isSetHost_info()));
        if (iCompareTo7 != 0) {
            return iCompareTo7;
        }
        if (!isSetHost_info() || (iJ = g0.a.j(this.host_info, passport.host_info)) == 0) {
            return 0;
        }
        return iJ;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public Passport m11deepCopy() {
        return new Passport(this);
    }

    public Passport(String str, String str2, String str3, String str4, String str5) {
        this();
        this.category = str;
        this.uuid = str2;
        this.version = str3;
        this.network = str4;
        this.rid = str5;
    }

    public boolean equals(Passport passport) {
        if (passport == null) {
            return false;
        }
        boolean zIsSetCategory = isSetCategory();
        boolean zIsSetCategory2 = passport.isSetCategory();
        if ((zIsSetCategory || zIsSetCategory2) && !(zIsSetCategory && zIsSetCategory2 && this.category.equals(passport.category))) {
            return false;
        }
        boolean zIsSetUuid = isSetUuid();
        boolean zIsSetUuid2 = passport.isSetUuid();
        if ((zIsSetUuid || zIsSetUuid2) && !(zIsSetUuid && zIsSetUuid2 && this.uuid.equals(passport.uuid))) {
            return false;
        }
        boolean zIsSetVersion = isSetVersion();
        boolean zIsSetVersion2 = passport.isSetVersion();
        if ((zIsSetVersion || zIsSetVersion2) && !(zIsSetVersion && zIsSetVersion2 && this.version.equals(passport.version))) {
            return false;
        }
        boolean zIsSetNetwork = isSetNetwork();
        boolean zIsSetNetwork2 = passport.isSetNetwork();
        if ((zIsSetNetwork || zIsSetNetwork2) && !(zIsSetNetwork && zIsSetNetwork2 && this.network.equals(passport.network))) {
            return false;
        }
        boolean zIsSetRid = isSetRid();
        boolean zIsSetRid2 = passport.isSetRid();
        if ((zIsSetRid || zIsSetRid2) && !(zIsSetRid && zIsSetRid2 && this.rid.equals(passport.rid))) {
            return false;
        }
        boolean zIsSetLocation = isSetLocation();
        boolean zIsSetLocation2 = passport.isSetLocation();
        if ((zIsSetLocation || zIsSetLocation2) && !(zIsSetLocation && zIsSetLocation2 && this.location.equals(passport.location))) {
            return false;
        }
        boolean zIsSetHost_info = isSetHost_info();
        boolean zIsSetHost_info2 = passport.isSetHost_info();
        if (zIsSetHost_info || zIsSetHost_info2) {
            return zIsSetHost_info && zIsSetHost_info2 && this.host_info.equals(passport.host_info);
        }
        return true;
    }

    public Passport(Passport passport) {
        if (passport.isSetCategory()) {
            this.category = passport.category;
        }
        if (passport.isSetUuid()) {
            this.uuid = passport.uuid;
        }
        if (passport.isSetVersion()) {
            this.version = passport.version;
        }
        if (passport.isSetNetwork()) {
            this.network = passport.network;
        }
        if (passport.isSetRid()) {
            this.rid = passport.rid;
        }
        if (passport.isSetLocation()) {
            this.location = new Location(passport.location);
        }
        if (passport.isSetHost_info()) {
            HashSet hashSet = new HashSet();
            Iterator<PassportHostInfo> it = passport.host_info.iterator();
            while (it.hasNext()) {
                hashSet.add(new PassportHostInfo(it.next()));
            }
            this.host_info = hashSet;
        }
    }
}
