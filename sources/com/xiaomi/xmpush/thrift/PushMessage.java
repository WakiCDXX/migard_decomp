package com.xiaomi.xmpush.thrift;

import com.xiaomi.push.mpcd.Constants;
import java.io.Serializable;
import java.util.BitSet;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.protocol.a;
import org.apache.thrift.protocol.d;
import org.apache.thrift.protocol.e;
import org.apache.thrift.protocol.g;

/* JADX INFO: loaded from: classes.dex */
public class PushMessage implements TBase<PushMessage, Object>, Serializable, Cloneable {
    private static final int __CREATEAT_ISSET_ID = 0;
    private static final int __ISONLINE_ISSET_ID = 2;
    private static final int __MIID_ISSET_ID = 3;
    private static final int __TTL_ISSET_ID = 1;
    private BitSet __isset_bit_vector;
    public String aliasName;
    public String appId;
    public String category;
    public String collapseKey;
    public long createAt;
    public String deviceId;
    public String id;
    public String imeiMd5;
    public boolean isOnline;
    public PushMetaInfo metaInfo;
    public long miid;
    public String packageName;
    public String payload;
    public String regId;
    public Target to;
    public String topic;
    public long ttl;
    public String userAccount;
    private static final g STRUCT_DESC = new g("PushMessage");
    private static final a TO_FIELD_DESC = new a("", (byte) 12, 1);
    private static final a ID_FIELD_DESC = new a("", (byte) 11, 2);
    private static final a APP_ID_FIELD_DESC = new a("", (byte) 11, 3);
    private static final a PAYLOAD_FIELD_DESC = new a("", (byte) 11, 4);
    private static final a CREATE_AT_FIELD_DESC = new a("", (byte) 10, 5);
    private static final a TTL_FIELD_DESC = new a("", (byte) 10, 6);
    private static final a COLLAPSE_KEY_FIELD_DESC = new a("", (byte) 11, 7);
    private static final a PACKAGE_NAME_FIELD_DESC = new a("", (byte) 11, 8);
    private static final a REG_ID_FIELD_DESC = new a("", (byte) 11, 9);
    private static final a CATEGORY_FIELD_DESC = new a("", (byte) 11, 10);
    private static final a TOPIC_FIELD_DESC = new a("", (byte) 11, 11);
    private static final a META_INFO_FIELD_DESC = new a("", (byte) 12, 12);
    private static final a ALIAS_NAME_FIELD_DESC = new a("", (byte) 11, 13);
    private static final a IS_ONLINE_FIELD_DESC = new a("", (byte) 2, 14);
    private static final a USER_ACCOUNT_FIELD_DESC = new a("", (byte) 11, 15);
    private static final a MIID_FIELD_DESC = new a("", (byte) 10, 16);
    private static final a IMEI_MD5_FIELD_DESC = new a("", (byte) 11, 20);
    private static final a DEVICE_ID_FIELD_DESC = new a("", (byte) 11, 21);

    public PushMessage() {
        this.__isset_bit_vector = new BitSet(4);
        this.isOnline = false;
    }

    public void clear() {
        this.to = null;
        this.id = null;
        this.appId = null;
        this.payload = null;
        setCreateAtIsSet(false);
        this.createAt = 0L;
        setTtlIsSet(false);
        this.ttl = 0L;
        this.collapseKey = null;
        this.packageName = null;
        this.regId = null;
        this.category = null;
        this.topic = null;
        this.metaInfo = null;
        this.aliasName = null;
        this.isOnline = false;
        this.userAccount = null;
        setMiidIsSet(false);
        this.miid = 0L;
        this.imeiMd5 = null;
        this.deviceId = null;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof PushMessage)) {
            return equals((PushMessage) obj);
        }
        return false;
    }

    public String getAliasName() {
        return this.aliasName;
    }

    public String getAppId() {
        return this.appId;
    }

    public String getCategory() {
        return this.category;
    }

    public String getCollapseKey() {
        return this.collapseKey;
    }

    public long getCreateAt() {
        return this.createAt;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public String getId() {
        return this.id;
    }

    public String getImeiMd5() {
        return this.imeiMd5;
    }

    public PushMetaInfo getMetaInfo() {
        return this.metaInfo;
    }

    public long getMiid() {
        return this.miid;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getPayload() {
        return this.payload;
    }

    public String getRegId() {
        return this.regId;
    }

    public Target getTo() {
        return this.to;
    }

    public String getTopic() {
        return this.topic;
    }

    public long getTtl() {
        return this.ttl;
    }

    public String getUserAccount() {
        return this.userAccount;
    }

    public int hashCode() {
        return 0;
    }

    public boolean isIsOnline() {
        return this.isOnline;
    }

    public boolean isSetAliasName() {
        return this.aliasName != null;
    }

    public boolean isSetAppId() {
        return this.appId != null;
    }

    public boolean isSetCategory() {
        return this.category != null;
    }

    public boolean isSetCollapseKey() {
        return this.collapseKey != null;
    }

    public boolean isSetCreateAt() {
        return this.__isset_bit_vector.get(0);
    }

    public boolean isSetDeviceId() {
        return this.deviceId != null;
    }

    public boolean isSetId() {
        return this.id != null;
    }

    public boolean isSetImeiMd5() {
        return this.imeiMd5 != null;
    }

    public boolean isSetIsOnline() {
        return this.__isset_bit_vector.get(2);
    }

    public boolean isSetMetaInfo() {
        return this.metaInfo != null;
    }

    public boolean isSetMiid() {
        return this.__isset_bit_vector.get(3);
    }

    public boolean isSetPackageName() {
        return this.packageName != null;
    }

    public boolean isSetPayload() {
        return this.payload != null;
    }

    public boolean isSetRegId() {
        return this.regId != null;
    }

    public boolean isSetTo() {
        return this.to != null;
    }

    public boolean isSetTopic() {
        return this.topic != null;
    }

    public boolean isSetTtl() {
        return this.__isset_bit_vector.get(1);
    }

    public boolean isSetUserAccount() {
        return this.userAccount != null;
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
            if (s2 != 20) {
                if (s2 != 21) {
                    switch (s2) {
                        case 1:
                            if (b2 != 12) {
                                e.a(dVar, b2);
                            } else {
                                Target target = new Target();
                                this.to = target;
                                target.read(dVar);
                            }
                            break;
                        case 2:
                            if (b2 != 11) {
                                e.a(dVar, b2);
                            } else {
                                this.id = dVar.p();
                            }
                            break;
                        case 3:
                            if (b2 != 11) {
                                e.a(dVar, b2);
                            } else {
                                this.appId = dVar.p();
                            }
                            break;
                        case 4:
                            if (b2 != 11) {
                                e.a(dVar, b2);
                            } else {
                                this.payload = dVar.p();
                            }
                            break;
                        case 5:
                            if (b2 != 10) {
                                e.a(dVar, b2);
                            } else {
                                this.createAt = dVar.i();
                                setCreateAtIsSet(true);
                            }
                            break;
                        case 6:
                            if (b2 != 10) {
                                e.a(dVar, b2);
                            } else {
                                this.ttl = dVar.i();
                                setTtlIsSet(true);
                            }
                            break;
                        case 7:
                            if (b2 != 11) {
                                e.a(dVar, b2);
                            } else {
                                this.collapseKey = dVar.p();
                            }
                            break;
                        case 8:
                            if (b2 != 11) {
                                e.a(dVar, b2);
                            } else {
                                this.packageName = dVar.p();
                            }
                            break;
                        case 9:
                            if (b2 != 11) {
                                e.a(dVar, b2);
                            } else {
                                this.regId = dVar.p();
                            }
                            break;
                        case 10:
                            if (b2 != 11) {
                                e.a(dVar, b2);
                            } else {
                                this.category = dVar.p();
                            }
                            break;
                        case 11:
                            if (b2 != 11) {
                                e.a(dVar, b2);
                            } else {
                                this.topic = dVar.p();
                            }
                            break;
                        case 12:
                            if (b2 != 12) {
                                e.a(dVar, b2);
                            } else {
                                PushMetaInfo pushMetaInfo = new PushMetaInfo();
                                this.metaInfo = pushMetaInfo;
                                pushMetaInfo.read(dVar);
                            }
                            break;
                        case 13:
                            if (b2 != 11) {
                                e.a(dVar, b2);
                            } else {
                                this.aliasName = dVar.p();
                            }
                            break;
                        case 14:
                            if (b2 != 2) {
                                e.a(dVar, b2);
                            } else {
                                this.isOnline = dVar.b();
                                setIsOnlineIsSet(true);
                            }
                            break;
                        case 15:
                            if (b2 != 11) {
                                e.a(dVar, b2);
                            } else {
                                this.userAccount = dVar.p();
                            }
                            break;
                        case 16:
                            if (b2 != 10) {
                                e.a(dVar, b2);
                            } else {
                                this.miid = dVar.i();
                                setMiidIsSet(true);
                            }
                            break;
                        default:
                            e.a(dVar, b2);
                            break;
                    }
                } else if (b2 == 11) {
                    this.deviceId = dVar.p();
                } else {
                    e.a(dVar, b2);
                }
            } else if (b2 == 11) {
                this.imeiMd5 = dVar.p();
            } else {
                e.a(dVar, b2);
            }
            dVar.f();
        }
    }

    public PushMessage setAliasName(String str) {
        this.aliasName = str;
        return this;
    }

    public void setAliasNameIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.aliasName = null;
    }

    public PushMessage setAppId(String str) {
        this.appId = str;
        return this;
    }

    public void setAppIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.appId = null;
    }

    public PushMessage setCategory(String str) {
        this.category = str;
        return this;
    }

    public void setCategoryIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.category = null;
    }

    public PushMessage setCollapseKey(String str) {
        this.collapseKey = str;
        return this;
    }

    public void setCollapseKeyIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.collapseKey = null;
    }

    public PushMessage setCreateAt(long j2) {
        this.createAt = j2;
        setCreateAtIsSet(true);
        return this;
    }

    public void setCreateAtIsSet(boolean z2) {
        this.__isset_bit_vector.set(0, z2);
    }

    public PushMessage setDeviceId(String str) {
        this.deviceId = str;
        return this;
    }

    public void setDeviceIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.deviceId = null;
    }

    public PushMessage setId(String str) {
        this.id = str;
        return this;
    }

    public void setIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.id = null;
    }

    public PushMessage setImeiMd5(String str) {
        this.imeiMd5 = str;
        return this;
    }

    public void setImeiMd5IsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.imeiMd5 = null;
    }

    public PushMessage setIsOnline(boolean z2) {
        this.isOnline = z2;
        setIsOnlineIsSet(true);
        return this;
    }

    public void setIsOnlineIsSet(boolean z2) {
        this.__isset_bit_vector.set(2, z2);
    }

    public PushMessage setMetaInfo(PushMetaInfo pushMetaInfo) {
        this.metaInfo = pushMetaInfo;
        return this;
    }

    public void setMetaInfoIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.metaInfo = null;
    }

    public PushMessage setMiid(long j2) {
        this.miid = j2;
        setMiidIsSet(true);
        return this;
    }

    public void setMiidIsSet(boolean z2) {
        this.__isset_bit_vector.set(3, z2);
    }

    public PushMessage setPackageName(String str) {
        this.packageName = str;
        return this;
    }

    public void setPackageNameIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.packageName = null;
    }

    public PushMessage setPayload(String str) {
        this.payload = str;
        return this;
    }

    public void setPayloadIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.payload = null;
    }

    public PushMessage setRegId(String str) {
        this.regId = str;
        return this;
    }

    public void setRegIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.regId = null;
    }

    public PushMessage setTo(Target target) {
        this.to = target;
        return this;
    }

    public void setToIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.to = null;
    }

    public PushMessage setTopic(String str) {
        this.topic = str;
        return this;
    }

    public void setTopicIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.topic = null;
    }

    public PushMessage setTtl(long j2) {
        this.ttl = j2;
        setTtlIsSet(true);
        return this;
    }

    public void setTtlIsSet(boolean z2) {
        this.__isset_bit_vector.set(1, z2);
    }

    public PushMessage setUserAccount(String str) {
        this.userAccount = str;
        return this;
    }

    public void setUserAccountIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.userAccount = null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("PushMessage(");
        if (isSetTo()) {
            sb.append("to:");
            Target target = this.to;
            if (target == null) {
                sb.append("null");
            } else {
                sb.append(target);
            }
            sb.append(", ");
        }
        sb.append("id:");
        String str = this.id;
        if (str == null) {
            sb.append("null");
        } else {
            sb.append(str);
        }
        sb.append(", ");
        sb.append("appId:");
        String str2 = this.appId;
        if (str2 == null) {
            sb.append("null");
        } else {
            sb.append(str2);
        }
        sb.append(", ");
        sb.append("payload:");
        String str3 = this.payload;
        if (str3 == null) {
            sb.append("null");
        } else {
            sb.append(str3);
        }
        if (isSetCreateAt()) {
            sb.append(", ");
            sb.append("createAt:");
            sb.append(this.createAt);
        }
        if (isSetTtl()) {
            sb.append(", ");
            sb.append("ttl:");
            sb.append(this.ttl);
        }
        if (isSetCollapseKey()) {
            sb.append(", ");
            sb.append("collapseKey:");
            String str4 = this.collapseKey;
            if (str4 == null) {
                sb.append("null");
            } else {
                sb.append(str4);
            }
        }
        if (isSetPackageName()) {
            sb.append(", ");
            sb.append("packageName:");
            String str5 = this.packageName;
            if (str5 == null) {
                sb.append("null");
            } else {
                sb.append(str5);
            }
        }
        if (isSetRegId()) {
            sb.append(", ");
            sb.append("regId:");
            String str6 = this.regId;
            if (str6 == null) {
                sb.append("null");
            } else {
                sb.append(str6);
            }
        }
        if (isSetCategory()) {
            sb.append(", ");
            sb.append("category:");
            String str7 = this.category;
            if (str7 == null) {
                sb.append("null");
            } else {
                sb.append(str7);
            }
        }
        if (isSetTopic()) {
            sb.append(", ");
            sb.append("topic:");
            String str8 = this.topic;
            if (str8 == null) {
                sb.append("null");
            } else {
                sb.append(str8);
            }
        }
        if (isSetMetaInfo()) {
            sb.append(", ");
            sb.append("metaInfo:");
            PushMetaInfo pushMetaInfo = this.metaInfo;
            if (pushMetaInfo == null) {
                sb.append("null");
            } else {
                sb.append(pushMetaInfo);
            }
        }
        if (isSetAliasName()) {
            sb.append(", ");
            sb.append("aliasName:");
            String str9 = this.aliasName;
            if (str9 == null) {
                sb.append("null");
            } else {
                sb.append(str9);
            }
        }
        if (isSetIsOnline()) {
            sb.append(", ");
            sb.append("isOnline:");
            sb.append(this.isOnline);
        }
        if (isSetUserAccount()) {
            sb.append(", ");
            sb.append("userAccount:");
            String str10 = this.userAccount;
            if (str10 == null) {
                sb.append("null");
            } else {
                sb.append(str10);
            }
        }
        if (isSetMiid()) {
            sb.append(", ");
            sb.append("miid:");
            sb.append(this.miid);
        }
        if (isSetImeiMd5()) {
            sb.append(", ");
            sb.append("imeiMd5:");
            String str11 = this.imeiMd5;
            if (str11 == null) {
                sb.append("null");
            } else {
                sb.append(str11);
            }
        }
        if (isSetDeviceId()) {
            sb.append(", ");
            sb.append("deviceId:");
            String str12 = this.deviceId;
            if (str12 == null) {
                sb.append("null");
            } else {
                sb.append(str12);
            }
        }
        sb.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
        return sb.toString();
    }

    public void unsetAliasName() {
        this.aliasName = null;
    }

    public void unsetAppId() {
        this.appId = null;
    }

    public void unsetCategory() {
        this.category = null;
    }

    public void unsetCollapseKey() {
        this.collapseKey = null;
    }

    public void unsetCreateAt() {
        this.__isset_bit_vector.clear(0);
    }

    public void unsetDeviceId() {
        this.deviceId = null;
    }

    public void unsetId() {
        this.id = null;
    }

    public void unsetImeiMd5() {
        this.imeiMd5 = null;
    }

    public void unsetIsOnline() {
        this.__isset_bit_vector.clear(2);
    }

    public void unsetMetaInfo() {
        this.metaInfo = null;
    }

    public void unsetMiid() {
        this.__isset_bit_vector.clear(3);
    }

    public void unsetPackageName() {
        this.packageName = null;
    }

    public void unsetPayload() {
        this.payload = null;
    }

    public void unsetRegId() {
        this.regId = null;
    }

    public void unsetTo() {
        this.to = null;
    }

    public void unsetTopic() {
        this.topic = null;
    }

    public void unsetTtl() {
        this.__isset_bit_vector.clear(1);
    }

    public void unsetUserAccount() {
        this.userAccount = null;
    }

    public void validate() throws TException {
        if (this.id == null) {
            throw new TProtocolException("Required field 'id' was not present! Struct: " + toString());
        }
        if (this.appId == null) {
            throw new TProtocolException("Required field 'appId' was not present! Struct: " + toString());
        }
        if (this.payload != null) {
            return;
        }
        throw new TProtocolException("Required field 'payload' was not present! Struct: " + toString());
    }

    @Override // org.apache.thrift.TBase
    public void write(d dVar) throws TException {
        validate();
        dVar.J(STRUCT_DESC);
        if (this.to != null && isSetTo()) {
            dVar.w(TO_FIELD_DESC);
            this.to.write(dVar);
            dVar.x();
        }
        if (this.id != null) {
            dVar.w(ID_FIELD_DESC);
            dVar.I(this.id);
            dVar.x();
        }
        if (this.appId != null) {
            dVar.w(APP_ID_FIELD_DESC);
            dVar.I(this.appId);
            dVar.x();
        }
        if (this.payload != null) {
            dVar.w(PAYLOAD_FIELD_DESC);
            dVar.I(this.payload);
            dVar.x();
        }
        if (isSetCreateAt()) {
            dVar.w(CREATE_AT_FIELD_DESC);
            dVar.B(this.createAt);
            dVar.x();
        }
        if (isSetTtl()) {
            dVar.w(TTL_FIELD_DESC);
            dVar.B(this.ttl);
            dVar.x();
        }
        if (this.collapseKey != null && isSetCollapseKey()) {
            dVar.w(COLLAPSE_KEY_FIELD_DESC);
            dVar.I(this.collapseKey);
            dVar.x();
        }
        if (this.packageName != null && isSetPackageName()) {
            dVar.w(PACKAGE_NAME_FIELD_DESC);
            dVar.I(this.packageName);
            dVar.x();
        }
        if (this.regId != null && isSetRegId()) {
            dVar.w(REG_ID_FIELD_DESC);
            dVar.I(this.regId);
            dVar.x();
        }
        if (this.category != null && isSetCategory()) {
            dVar.w(CATEGORY_FIELD_DESC);
            dVar.I(this.category);
            dVar.x();
        }
        if (this.topic != null && isSetTopic()) {
            dVar.w(TOPIC_FIELD_DESC);
            dVar.I(this.topic);
            dVar.x();
        }
        if (this.metaInfo != null && isSetMetaInfo()) {
            dVar.w(META_INFO_FIELD_DESC);
            this.metaInfo.write(dVar);
            dVar.x();
        }
        if (this.aliasName != null && isSetAliasName()) {
            dVar.w(ALIAS_NAME_FIELD_DESC);
            dVar.I(this.aliasName);
            dVar.x();
        }
        if (isSetIsOnline()) {
            dVar.w(IS_ONLINE_FIELD_DESC);
            dVar.u(this.isOnline);
            dVar.x();
        }
        if (this.userAccount != null && isSetUserAccount()) {
            dVar.w(USER_ACCOUNT_FIELD_DESC);
            dVar.I(this.userAccount);
            dVar.x();
        }
        if (isSetMiid()) {
            dVar.w(MIID_FIELD_DESC);
            dVar.B(this.miid);
            dVar.x();
        }
        if (this.imeiMd5 != null && isSetImeiMd5()) {
            dVar.w(IMEI_MD5_FIELD_DESC);
            dVar.I(this.imeiMd5);
            dVar.x();
        }
        if (this.deviceId != null && isSetDeviceId()) {
            dVar.w(DEVICE_ID_FIELD_DESC);
            dVar.I(this.deviceId);
            dVar.x();
        }
        dVar.y();
        dVar.K();
    }

    @Override // java.lang.Comparable
    public int compareTo(PushMessage pushMessage) {
        int iG;
        int iG2;
        int iE;
        int iG3;
        int iL;
        int iG4;
        int iF;
        int iG5;
        int iG6;
        int iG7;
        int iG8;
        int iG9;
        int iE2;
        int iE3;
        int iG10;
        int iG11;
        int iG12;
        int iF2;
        if (!getClass().equals(pushMessage.getClass())) {
            return getClass().getName().compareTo(pushMessage.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetTo()).compareTo(Boolean.valueOf(pushMessage.isSetTo()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetTo() && (iF2 = g0.a.f(this.to, pushMessage.to)) != 0) {
            return iF2;
        }
        int iCompareTo2 = Boolean.valueOf(isSetId()).compareTo(Boolean.valueOf(pushMessage.isSetId()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (isSetId() && (iG12 = g0.a.g(this.id, pushMessage.id)) != 0) {
            return iG12;
        }
        int iCompareTo3 = Boolean.valueOf(isSetAppId()).compareTo(Boolean.valueOf(pushMessage.isSetAppId()));
        if (iCompareTo3 != 0) {
            return iCompareTo3;
        }
        if (isSetAppId() && (iG11 = g0.a.g(this.appId, pushMessage.appId)) != 0) {
            return iG11;
        }
        int iCompareTo4 = Boolean.valueOf(isSetPayload()).compareTo(Boolean.valueOf(pushMessage.isSetPayload()));
        if (iCompareTo4 != 0) {
            return iCompareTo4;
        }
        if (isSetPayload() && (iG10 = g0.a.g(this.payload, pushMessage.payload)) != 0) {
            return iG10;
        }
        int iCompareTo5 = Boolean.valueOf(isSetCreateAt()).compareTo(Boolean.valueOf(pushMessage.isSetCreateAt()));
        if (iCompareTo5 != 0) {
            return iCompareTo5;
        }
        if (isSetCreateAt() && (iE3 = g0.a.e(this.createAt, pushMessage.createAt)) != 0) {
            return iE3;
        }
        int iCompareTo6 = Boolean.valueOf(isSetTtl()).compareTo(Boolean.valueOf(pushMessage.isSetTtl()));
        if (iCompareTo6 != 0) {
            return iCompareTo6;
        }
        if (isSetTtl() && (iE2 = g0.a.e(this.ttl, pushMessage.ttl)) != 0) {
            return iE2;
        }
        int iCompareTo7 = Boolean.valueOf(isSetCollapseKey()).compareTo(Boolean.valueOf(pushMessage.isSetCollapseKey()));
        if (iCompareTo7 != 0) {
            return iCompareTo7;
        }
        if (isSetCollapseKey() && (iG9 = g0.a.g(this.collapseKey, pushMessage.collapseKey)) != 0) {
            return iG9;
        }
        int iCompareTo8 = Boolean.valueOf(isSetPackageName()).compareTo(Boolean.valueOf(pushMessage.isSetPackageName()));
        if (iCompareTo8 != 0) {
            return iCompareTo8;
        }
        if (isSetPackageName() && (iG8 = g0.a.g(this.packageName, pushMessage.packageName)) != 0) {
            return iG8;
        }
        int iCompareTo9 = Boolean.valueOf(isSetRegId()).compareTo(Boolean.valueOf(pushMessage.isSetRegId()));
        if (iCompareTo9 != 0) {
            return iCompareTo9;
        }
        if (isSetRegId() && (iG7 = g0.a.g(this.regId, pushMessage.regId)) != 0) {
            return iG7;
        }
        int iCompareTo10 = Boolean.valueOf(isSetCategory()).compareTo(Boolean.valueOf(pushMessage.isSetCategory()));
        if (iCompareTo10 != 0) {
            return iCompareTo10;
        }
        if (isSetCategory() && (iG6 = g0.a.g(this.category, pushMessage.category)) != 0) {
            return iG6;
        }
        int iCompareTo11 = Boolean.valueOf(isSetTopic()).compareTo(Boolean.valueOf(pushMessage.isSetTopic()));
        if (iCompareTo11 != 0) {
            return iCompareTo11;
        }
        if (isSetTopic() && (iG5 = g0.a.g(this.topic, pushMessage.topic)) != 0) {
            return iG5;
        }
        int iCompareTo12 = Boolean.valueOf(isSetMetaInfo()).compareTo(Boolean.valueOf(pushMessage.isSetMetaInfo()));
        if (iCompareTo12 != 0) {
            return iCompareTo12;
        }
        if (isSetMetaInfo() && (iF = g0.a.f(this.metaInfo, pushMessage.metaInfo)) != 0) {
            return iF;
        }
        int iCompareTo13 = Boolean.valueOf(isSetAliasName()).compareTo(Boolean.valueOf(pushMessage.isSetAliasName()));
        if (iCompareTo13 != 0) {
            return iCompareTo13;
        }
        if (isSetAliasName() && (iG4 = g0.a.g(this.aliasName, pushMessage.aliasName)) != 0) {
            return iG4;
        }
        int iCompareTo14 = Boolean.valueOf(isSetIsOnline()).compareTo(Boolean.valueOf(pushMessage.isSetIsOnline()));
        if (iCompareTo14 != 0) {
            return iCompareTo14;
        }
        if (isSetIsOnline() && (iL = g0.a.l(this.isOnline, pushMessage.isOnline)) != 0) {
            return iL;
        }
        int iCompareTo15 = Boolean.valueOf(isSetUserAccount()).compareTo(Boolean.valueOf(pushMessage.isSetUserAccount()));
        if (iCompareTo15 != 0) {
            return iCompareTo15;
        }
        if (isSetUserAccount() && (iG3 = g0.a.g(this.userAccount, pushMessage.userAccount)) != 0) {
            return iG3;
        }
        int iCompareTo16 = Boolean.valueOf(isSetMiid()).compareTo(Boolean.valueOf(pushMessage.isSetMiid()));
        if (iCompareTo16 != 0) {
            return iCompareTo16;
        }
        if (isSetMiid() && (iE = g0.a.e(this.miid, pushMessage.miid)) != 0) {
            return iE;
        }
        int iCompareTo17 = Boolean.valueOf(isSetImeiMd5()).compareTo(Boolean.valueOf(pushMessage.isSetImeiMd5()));
        if (iCompareTo17 != 0) {
            return iCompareTo17;
        }
        if (isSetImeiMd5() && (iG2 = g0.a.g(this.imeiMd5, pushMessage.imeiMd5)) != 0) {
            return iG2;
        }
        int iCompareTo18 = Boolean.valueOf(isSetDeviceId()).compareTo(Boolean.valueOf(pushMessage.isSetDeviceId()));
        if (iCompareTo18 != 0) {
            return iCompareTo18;
        }
        if (!isSetDeviceId() || (iG = g0.a.g(this.deviceId, pushMessage.deviceId)) == 0) {
            return 0;
        }
        return iG;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public PushMessage m24deepCopy() {
        return new PushMessage(this);
    }

    public boolean equals(PushMessage pushMessage) {
        if (pushMessage == null) {
            return false;
        }
        boolean zIsSetTo = isSetTo();
        boolean zIsSetTo2 = pushMessage.isSetTo();
        if ((zIsSetTo || zIsSetTo2) && !(zIsSetTo && zIsSetTo2 && this.to.equals(pushMessage.to))) {
            return false;
        }
        boolean zIsSetId = isSetId();
        boolean zIsSetId2 = pushMessage.isSetId();
        if ((zIsSetId || zIsSetId2) && !(zIsSetId && zIsSetId2 && this.id.equals(pushMessage.id))) {
            return false;
        }
        boolean zIsSetAppId = isSetAppId();
        boolean zIsSetAppId2 = pushMessage.isSetAppId();
        if ((zIsSetAppId || zIsSetAppId2) && !(zIsSetAppId && zIsSetAppId2 && this.appId.equals(pushMessage.appId))) {
            return false;
        }
        boolean zIsSetPayload = isSetPayload();
        boolean zIsSetPayload2 = pushMessage.isSetPayload();
        if ((zIsSetPayload || zIsSetPayload2) && !(zIsSetPayload && zIsSetPayload2 && this.payload.equals(pushMessage.payload))) {
            return false;
        }
        boolean zIsSetCreateAt = isSetCreateAt();
        boolean zIsSetCreateAt2 = pushMessage.isSetCreateAt();
        if ((zIsSetCreateAt || zIsSetCreateAt2) && !(zIsSetCreateAt && zIsSetCreateAt2 && this.createAt == pushMessage.createAt)) {
            return false;
        }
        boolean zIsSetTtl = isSetTtl();
        boolean zIsSetTtl2 = pushMessage.isSetTtl();
        if ((zIsSetTtl || zIsSetTtl2) && !(zIsSetTtl && zIsSetTtl2 && this.ttl == pushMessage.ttl)) {
            return false;
        }
        boolean zIsSetCollapseKey = isSetCollapseKey();
        boolean zIsSetCollapseKey2 = pushMessage.isSetCollapseKey();
        if ((zIsSetCollapseKey || zIsSetCollapseKey2) && !(zIsSetCollapseKey && zIsSetCollapseKey2 && this.collapseKey.equals(pushMessage.collapseKey))) {
            return false;
        }
        boolean zIsSetPackageName = isSetPackageName();
        boolean zIsSetPackageName2 = pushMessage.isSetPackageName();
        if ((zIsSetPackageName || zIsSetPackageName2) && !(zIsSetPackageName && zIsSetPackageName2 && this.packageName.equals(pushMessage.packageName))) {
            return false;
        }
        boolean zIsSetRegId = isSetRegId();
        boolean zIsSetRegId2 = pushMessage.isSetRegId();
        if ((zIsSetRegId || zIsSetRegId2) && !(zIsSetRegId && zIsSetRegId2 && this.regId.equals(pushMessage.regId))) {
            return false;
        }
        boolean zIsSetCategory = isSetCategory();
        boolean zIsSetCategory2 = pushMessage.isSetCategory();
        if ((zIsSetCategory || zIsSetCategory2) && !(zIsSetCategory && zIsSetCategory2 && this.category.equals(pushMessage.category))) {
            return false;
        }
        boolean zIsSetTopic = isSetTopic();
        boolean zIsSetTopic2 = pushMessage.isSetTopic();
        if ((zIsSetTopic || zIsSetTopic2) && !(zIsSetTopic && zIsSetTopic2 && this.topic.equals(pushMessage.topic))) {
            return false;
        }
        boolean zIsSetMetaInfo = isSetMetaInfo();
        boolean zIsSetMetaInfo2 = pushMessage.isSetMetaInfo();
        if ((zIsSetMetaInfo || zIsSetMetaInfo2) && !(zIsSetMetaInfo && zIsSetMetaInfo2 && this.metaInfo.equals(pushMessage.metaInfo))) {
            return false;
        }
        boolean zIsSetAliasName = isSetAliasName();
        boolean zIsSetAliasName2 = pushMessage.isSetAliasName();
        if ((zIsSetAliasName || zIsSetAliasName2) && !(zIsSetAliasName && zIsSetAliasName2 && this.aliasName.equals(pushMessage.aliasName))) {
            return false;
        }
        boolean zIsSetIsOnline = isSetIsOnline();
        boolean zIsSetIsOnline2 = pushMessage.isSetIsOnline();
        if ((zIsSetIsOnline || zIsSetIsOnline2) && !(zIsSetIsOnline && zIsSetIsOnline2 && this.isOnline == pushMessage.isOnline)) {
            return false;
        }
        boolean zIsSetUserAccount = isSetUserAccount();
        boolean zIsSetUserAccount2 = pushMessage.isSetUserAccount();
        if ((zIsSetUserAccount || zIsSetUserAccount2) && !(zIsSetUserAccount && zIsSetUserAccount2 && this.userAccount.equals(pushMessage.userAccount))) {
            return false;
        }
        boolean zIsSetMiid = isSetMiid();
        boolean zIsSetMiid2 = pushMessage.isSetMiid();
        if ((zIsSetMiid || zIsSetMiid2) && !(zIsSetMiid && zIsSetMiid2 && this.miid == pushMessage.miid)) {
            return false;
        }
        boolean zIsSetImeiMd5 = isSetImeiMd5();
        boolean zIsSetImeiMd6 = pushMessage.isSetImeiMd5();
        if ((zIsSetImeiMd5 || zIsSetImeiMd6) && !(zIsSetImeiMd5 && zIsSetImeiMd6 && this.imeiMd5.equals(pushMessage.imeiMd5))) {
            return false;
        }
        boolean zIsSetDeviceId = isSetDeviceId();
        boolean zIsSetDeviceId2 = pushMessage.isSetDeviceId();
        if (zIsSetDeviceId || zIsSetDeviceId2) {
            return zIsSetDeviceId && zIsSetDeviceId2 && this.deviceId.equals(pushMessage.deviceId);
        }
        return true;
    }

    public PushMessage(String str, String str2, String str3) {
        this();
        this.id = str;
        this.appId = str2;
        this.payload = str3;
    }

    public PushMessage(PushMessage pushMessage) {
        BitSet bitSet = new BitSet(4);
        this.__isset_bit_vector = bitSet;
        bitSet.clear();
        this.__isset_bit_vector.or(pushMessage.__isset_bit_vector);
        if (pushMessage.isSetTo()) {
            this.to = new Target(pushMessage.to);
        }
        if (pushMessage.isSetId()) {
            this.id = pushMessage.id;
        }
        if (pushMessage.isSetAppId()) {
            this.appId = pushMessage.appId;
        }
        if (pushMessage.isSetPayload()) {
            this.payload = pushMessage.payload;
        }
        this.createAt = pushMessage.createAt;
        this.ttl = pushMessage.ttl;
        if (pushMessage.isSetCollapseKey()) {
            this.collapseKey = pushMessage.collapseKey;
        }
        if (pushMessage.isSetPackageName()) {
            this.packageName = pushMessage.packageName;
        }
        if (pushMessage.isSetRegId()) {
            this.regId = pushMessage.regId;
        }
        if (pushMessage.isSetCategory()) {
            this.category = pushMessage.category;
        }
        if (pushMessage.isSetTopic()) {
            this.topic = pushMessage.topic;
        }
        if (pushMessage.isSetMetaInfo()) {
            this.metaInfo = new PushMetaInfo(pushMessage.metaInfo);
        }
        if (pushMessage.isSetAliasName()) {
            this.aliasName = pushMessage.aliasName;
        }
        this.isOnline = pushMessage.isOnline;
        if (pushMessage.isSetUserAccount()) {
            this.userAccount = pushMessage.userAccount;
        }
        this.miid = pushMessage.miid;
        if (pushMessage.isSetImeiMd5()) {
            this.imeiMd5 = pushMessage.imeiMd5;
        }
        if (pushMessage.isSetDeviceId()) {
            this.deviceId = pushMessage.deviceId;
        }
    }
}
