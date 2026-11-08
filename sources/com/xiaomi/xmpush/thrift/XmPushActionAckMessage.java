package com.xiaomi.xmpush.thrift;

import com.xiaomi.push.mpcd.Constants;
import java.io.Serializable;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.protocol.a;
import org.apache.thrift.protocol.c;
import org.apache.thrift.protocol.d;
import org.apache.thrift.protocol.e;
import org.apache.thrift.protocol.g;

/* JADX INFO: loaded from: classes.dex */
public class XmPushActionAckMessage implements TBase<XmPushActionAckMessage, Object>, Serializable, Cloneable {
    private static final int __DEVICESTATUS_ISSET_ID = 2;
    private static final int __GEOMSGSTATUS_ISSET_ID = 3;
    private static final int __ISONLINE_ISSET_ID = 1;
    private static final int __MESSAGETS_ISSET_ID = 0;
    private static final int __PASSTHROUGH_ISSET_ID = 4;
    private BitSet __isset_bit_vector;
    public String aliasName;
    public String appId;
    public String callbackUrl;
    public String category;
    public String debug;
    public String deviceId;
    public short deviceStatus;
    public Map<String, String> extra;
    public short geoMsgStatus;
    public String id;
    public String imeiMd5;
    public boolean isOnline;
    public long messageTs;
    public String packageName;
    public int passThrough;
    public String regId;
    public XmPushActionSendMessage request;
    public Target target;
    public String topic;
    public String userAccount;
    private static final g STRUCT_DESC = new g("XmPushActionAckMessage");
    private static final a DEBUG_FIELD_DESC = new a("", (byte) 11, 1);
    private static final a TARGET_FIELD_DESC = new a("", (byte) 12, 2);
    private static final a ID_FIELD_DESC = new a("", (byte) 11, 3);
    private static final a APP_ID_FIELD_DESC = new a("", (byte) 11, 4);
    private static final a MESSAGE_TS_FIELD_DESC = new a("", (byte) 10, 5);
    private static final a TOPIC_FIELD_DESC = new a("", (byte) 11, 6);
    private static final a ALIAS_NAME_FIELD_DESC = new a("", (byte) 11, 7);
    private static final a REQUEST_FIELD_DESC = new a("", (byte) 12, 8);
    private static final a PACKAGE_NAME_FIELD_DESC = new a("", (byte) 11, 9);
    private static final a CATEGORY_FIELD_DESC = new a("", (byte) 11, 10);
    private static final a IS_ONLINE_FIELD_DESC = new a("", (byte) 2, 11);
    private static final a REG_ID_FIELD_DESC = new a("", (byte) 11, 12);
    private static final a CALLBACK_URL_FIELD_DESC = new a("", (byte) 11, 13);
    private static final a USER_ACCOUNT_FIELD_DESC = new a("", (byte) 11, 14);
    private static final a DEVICE_STATUS_FIELD_DESC = new a("", (byte) 6, 15);
    private static final a GEO_MSG_STATUS_FIELD_DESC = new a("", (byte) 6, 16);
    private static final a IMEI_MD5_FIELD_DESC = new a("", (byte) 11, 20);
    private static final a DEVICE_ID_FIELD_DESC = new a("", (byte) 11, 21);
    private static final a PASS_THROUGH_FIELD_DESC = new a("", (byte) 8, 22);
    private static final a EXTRA_FIELD_DESC = new a("", (byte) 13, 23);

    public XmPushActionAckMessage() {
        this.__isset_bit_vector = new BitSet(5);
        this.isOnline = false;
    }

    public void clear() {
        this.debug = null;
        this.target = null;
        this.id = null;
        this.appId = null;
        setMessageTsIsSet(false);
        this.messageTs = 0L;
        this.topic = null;
        this.aliasName = null;
        this.request = null;
        this.packageName = null;
        this.category = null;
        this.isOnline = false;
        this.regId = null;
        this.callbackUrl = null;
        this.userAccount = null;
        setDeviceStatusIsSet(false);
        this.deviceStatus = (short) 0;
        setGeoMsgStatusIsSet(false);
        this.geoMsgStatus = (short) 0;
        this.imeiMd5 = null;
        this.deviceId = null;
        setPassThroughIsSet(false);
        this.passThrough = 0;
        this.extra = null;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof XmPushActionAckMessage)) {
            return equals((XmPushActionAckMessage) obj);
        }
        return false;
    }

    public String getAliasName() {
        return this.aliasName;
    }

    public String getAppId() {
        return this.appId;
    }

    public String getCallbackUrl() {
        return this.callbackUrl;
    }

    public String getCategory() {
        return this.category;
    }

    public String getDebug() {
        return this.debug;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public short getDeviceStatus() {
        return this.deviceStatus;
    }

    public Map<String, String> getExtra() {
        return this.extra;
    }

    public int getExtraSize() {
        Map<String, String> map = this.extra;
        if (map == null) {
            return 0;
        }
        return map.size();
    }

    public short getGeoMsgStatus() {
        return this.geoMsgStatus;
    }

    public String getId() {
        return this.id;
    }

    public String getImeiMd5() {
        return this.imeiMd5;
    }

    public long getMessageTs() {
        return this.messageTs;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public int getPassThrough() {
        return this.passThrough;
    }

    public String getRegId() {
        return this.regId;
    }

    public XmPushActionSendMessage getRequest() {
        return this.request;
    }

    public Target getTarget() {
        return this.target;
    }

    public String getTopic() {
        return this.topic;
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

    public boolean isSetCallbackUrl() {
        return this.callbackUrl != null;
    }

    public boolean isSetCategory() {
        return this.category != null;
    }

    public boolean isSetDebug() {
        return this.debug != null;
    }

    public boolean isSetDeviceId() {
        return this.deviceId != null;
    }

    public boolean isSetDeviceStatus() {
        return this.__isset_bit_vector.get(2);
    }

    public boolean isSetExtra() {
        return this.extra != null;
    }

    public boolean isSetGeoMsgStatus() {
        return this.__isset_bit_vector.get(3);
    }

    public boolean isSetId() {
        return this.id != null;
    }

    public boolean isSetImeiMd5() {
        return this.imeiMd5 != null;
    }

    public boolean isSetIsOnline() {
        return this.__isset_bit_vector.get(1);
    }

    public boolean isSetMessageTs() {
        return this.__isset_bit_vector.get(0);
    }

    public boolean isSetPackageName() {
        return this.packageName != null;
    }

    public boolean isSetPassThrough() {
        return this.__isset_bit_vector.get(4);
    }

    public boolean isSetRegId() {
        return this.regId != null;
    }

    public boolean isSetRequest() {
        return this.request != null;
    }

    public boolean isSetTarget() {
        return this.target != null;
    }

    public boolean isSetTopic() {
        return this.topic != null;
    }

    public boolean isSetUserAccount() {
        return this.userAccount != null;
    }

    public void putToExtra(String str, String str2) {
        if (this.extra == null) {
            this.extra = new HashMap();
        }
        this.extra.put(str, str2);
    }

    @Override // org.apache.thrift.TBase
    public void read(d dVar) throws TException {
        dVar.q();
        while (true) {
            a aVarE = dVar.e();
            byte b2 = aVarE.f2150b;
            if (b2 == 0) {
                dVar.r();
                if (isSetMessageTs()) {
                    validate();
                    return;
                }
                throw new TProtocolException("Required field 'messageTs' was not found in serialized data! Struct: " + toString());
            }
            switch (aVarE.f2151c) {
                case 1:
                    if (b2 == 11) {
                        this.debug = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 2:
                    if (b2 == 12) {
                        Target target = new Target();
                        this.target = target;
                        target.read(dVar);
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 3:
                    if (b2 == 11) {
                        this.id = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 4:
                    if (b2 == 11) {
                        this.appId = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 5:
                    if (b2 == 10) {
                        this.messageTs = dVar.i();
                        setMessageTsIsSet(true);
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 6:
                    if (b2 == 11) {
                        this.topic = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 7:
                    if (b2 == 11) {
                        this.aliasName = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 8:
                    if (b2 == 12) {
                        XmPushActionSendMessage xmPushActionSendMessage = new XmPushActionSendMessage();
                        this.request = xmPushActionSendMessage;
                        xmPushActionSendMessage.read(dVar);
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 9:
                    if (b2 == 11) {
                        this.packageName = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 10:
                    if (b2 == 11) {
                        this.category = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 11:
                    if (b2 == 2) {
                        this.isOnline = dVar.b();
                        setIsOnlineIsSet(true);
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 12:
                    if (b2 == 11) {
                        this.regId = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 13:
                    if (b2 == 11) {
                        this.callbackUrl = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 14:
                    if (b2 == 11) {
                        this.userAccount = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 15:
                    if (b2 == 6) {
                        this.deviceStatus = dVar.g();
                        setDeviceStatusIsSet(true);
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 16:
                    if (b2 == 6) {
                        this.geoMsgStatus = dVar.g();
                        setGeoMsgStatusIsSet(true);
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 17:
                case 18:
                case 19:
                default:
                    e.a(dVar, b2);
                    break;
                case 20:
                    if (b2 == 11) {
                        this.imeiMd5 = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 21:
                    if (b2 == 11) {
                        this.deviceId = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 22:
                    if (b2 == 8) {
                        this.passThrough = dVar.h();
                        setPassThroughIsSet(true);
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 23:
                    if (b2 == 13) {
                        c cVarL = dVar.l();
                        this.extra = new HashMap(cVarL.f2156c * 2);
                        for (int i2 = 0; i2 < cVarL.f2156c; i2++) {
                            this.extra.put(dVar.p(), dVar.p());
                        }
                        dVar.m();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
            }
            dVar.f();
        }
    }

    public XmPushActionAckMessage setAliasName(String str) {
        this.aliasName = str;
        return this;
    }

    public void setAliasNameIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.aliasName = null;
    }

    public XmPushActionAckMessage setAppId(String str) {
        this.appId = str;
        return this;
    }

    public void setAppIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.appId = null;
    }

    public XmPushActionAckMessage setCallbackUrl(String str) {
        this.callbackUrl = str;
        return this;
    }

    public void setCallbackUrlIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.callbackUrl = null;
    }

    public XmPushActionAckMessage setCategory(String str) {
        this.category = str;
        return this;
    }

    public void setCategoryIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.category = null;
    }

    public XmPushActionAckMessage setDebug(String str) {
        this.debug = str;
        return this;
    }

    public void setDebugIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.debug = null;
    }

    public XmPushActionAckMessage setDeviceId(String str) {
        this.deviceId = str;
        return this;
    }

    public void setDeviceIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.deviceId = null;
    }

    public XmPushActionAckMessage setDeviceStatus(short s2) {
        this.deviceStatus = s2;
        setDeviceStatusIsSet(true);
        return this;
    }

    public void setDeviceStatusIsSet(boolean z2) {
        this.__isset_bit_vector.set(2, z2);
    }

    public XmPushActionAckMessage setExtra(Map<String, String> map) {
        this.extra = map;
        return this;
    }

    public void setExtraIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.extra = null;
    }

    public XmPushActionAckMessage setGeoMsgStatus(short s2) {
        this.geoMsgStatus = s2;
        setGeoMsgStatusIsSet(true);
        return this;
    }

    public void setGeoMsgStatusIsSet(boolean z2) {
        this.__isset_bit_vector.set(3, z2);
    }

    public XmPushActionAckMessage setId(String str) {
        this.id = str;
        return this;
    }

    public void setIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.id = null;
    }

    public XmPushActionAckMessage setImeiMd5(String str) {
        this.imeiMd5 = str;
        return this;
    }

    public void setImeiMd5IsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.imeiMd5 = null;
    }

    public XmPushActionAckMessage setIsOnline(boolean z2) {
        this.isOnline = z2;
        setIsOnlineIsSet(true);
        return this;
    }

    public void setIsOnlineIsSet(boolean z2) {
        this.__isset_bit_vector.set(1, z2);
    }

    public XmPushActionAckMessage setMessageTs(long j2) {
        this.messageTs = j2;
        setMessageTsIsSet(true);
        return this;
    }

    public void setMessageTsIsSet(boolean z2) {
        this.__isset_bit_vector.set(0, z2);
    }

    public XmPushActionAckMessage setPackageName(String str) {
        this.packageName = str;
        return this;
    }

    public void setPackageNameIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.packageName = null;
    }

    public XmPushActionAckMessage setPassThrough(int i2) {
        this.passThrough = i2;
        setPassThroughIsSet(true);
        return this;
    }

    public void setPassThroughIsSet(boolean z2) {
        this.__isset_bit_vector.set(4, z2);
    }

    public XmPushActionAckMessage setRegId(String str) {
        this.regId = str;
        return this;
    }

    public void setRegIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.regId = null;
    }

    public XmPushActionAckMessage setRequest(XmPushActionSendMessage xmPushActionSendMessage) {
        this.request = xmPushActionSendMessage;
        return this;
    }

    public void setRequestIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.request = null;
    }

    public XmPushActionAckMessage setTarget(Target target) {
        this.target = target;
        return this;
    }

    public void setTargetIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.target = null;
    }

    public XmPushActionAckMessage setTopic(String str) {
        this.topic = str;
        return this;
    }

    public void setTopicIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.topic = null;
    }

    public XmPushActionAckMessage setUserAccount(String str) {
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
        boolean z2;
        StringBuilder sb = new StringBuilder("XmPushActionAckMessage(");
        boolean z3 = false;
        if (isSetDebug()) {
            sb.append("debug:");
            String str = this.debug;
            if (str == null) {
                sb.append("null");
            } else {
                sb.append(str);
            }
            z2 = false;
        } else {
            z2 = true;
        }
        if (isSetTarget()) {
            if (!z2) {
                sb.append(", ");
            }
            sb.append("target:");
            Target target = this.target;
            if (target == null) {
                sb.append("null");
            } else {
                sb.append(target);
            }
        } else {
            z3 = z2;
        }
        if (!z3) {
            sb.append(", ");
        }
        sb.append("id:");
        String str2 = this.id;
        if (str2 == null) {
            sb.append("null");
        } else {
            sb.append(str2);
        }
        sb.append(", ");
        sb.append("appId:");
        String str3 = this.appId;
        if (str3 == null) {
            sb.append("null");
        } else {
            sb.append(str3);
        }
        sb.append(", ");
        sb.append("messageTs:");
        sb.append(this.messageTs);
        if (isSetTopic()) {
            sb.append(", ");
            sb.append("topic:");
            String str4 = this.topic;
            if (str4 == null) {
                sb.append("null");
            } else {
                sb.append(str4);
            }
        }
        if (isSetAliasName()) {
            sb.append(", ");
            sb.append("aliasName:");
            String str5 = this.aliasName;
            if (str5 == null) {
                sb.append("null");
            } else {
                sb.append(str5);
            }
        }
        if (isSetRequest()) {
            sb.append(", ");
            sb.append("request:");
            XmPushActionSendMessage xmPushActionSendMessage = this.request;
            if (xmPushActionSendMessage == null) {
                sb.append("null");
            } else {
                sb.append(xmPushActionSendMessage);
            }
        }
        if (isSetPackageName()) {
            sb.append(", ");
            sb.append("packageName:");
            String str6 = this.packageName;
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
        if (isSetIsOnline()) {
            sb.append(", ");
            sb.append("isOnline:");
            sb.append(this.isOnline);
        }
        if (isSetRegId()) {
            sb.append(", ");
            sb.append("regId:");
            String str8 = this.regId;
            if (str8 == null) {
                sb.append("null");
            } else {
                sb.append(str8);
            }
        }
        if (isSetCallbackUrl()) {
            sb.append(", ");
            sb.append("callbackUrl:");
            String str9 = this.callbackUrl;
            if (str9 == null) {
                sb.append("null");
            } else {
                sb.append(str9);
            }
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
        if (isSetDeviceStatus()) {
            sb.append(", ");
            sb.append("deviceStatus:");
            sb.append((int) this.deviceStatus);
        }
        if (isSetGeoMsgStatus()) {
            sb.append(", ");
            sb.append("geoMsgStatus:");
            sb.append((int) this.geoMsgStatus);
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
        if (isSetPassThrough()) {
            sb.append(", ");
            sb.append("passThrough:");
            sb.append(this.passThrough);
        }
        if (isSetExtra()) {
            sb.append(", ");
            sb.append("extra:");
            Map<String, String> map = this.extra;
            if (map == null) {
                sb.append("null");
            } else {
                sb.append(map);
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

    public void unsetCallbackUrl() {
        this.callbackUrl = null;
    }

    public void unsetCategory() {
        this.category = null;
    }

    public void unsetDebug() {
        this.debug = null;
    }

    public void unsetDeviceId() {
        this.deviceId = null;
    }

    public void unsetDeviceStatus() {
        this.__isset_bit_vector.clear(2);
    }

    public void unsetExtra() {
        this.extra = null;
    }

    public void unsetGeoMsgStatus() {
        this.__isset_bit_vector.clear(3);
    }

    public void unsetId() {
        this.id = null;
    }

    public void unsetImeiMd5() {
        this.imeiMd5 = null;
    }

    public void unsetIsOnline() {
        this.__isset_bit_vector.clear(1);
    }

    public void unsetMessageTs() {
        this.__isset_bit_vector.clear(0);
    }

    public void unsetPackageName() {
        this.packageName = null;
    }

    public void unsetPassThrough() {
        this.__isset_bit_vector.clear(4);
    }

    public void unsetRegId() {
        this.regId = null;
    }

    public void unsetRequest() {
        this.request = null;
    }

    public void unsetTarget() {
        this.target = null;
    }

    public void unsetTopic() {
        this.topic = null;
    }

    public void unsetUserAccount() {
        this.userAccount = null;
    }

    public void validate() throws TException {
        if (this.id == null) {
            throw new TProtocolException("Required field 'id' was not present! Struct: " + toString());
        }
        if (this.appId != null) {
            return;
        }
        throw new TProtocolException("Required field 'appId' was not present! Struct: " + toString());
    }

    @Override // org.apache.thrift.TBase
    public void write(d dVar) throws TException {
        validate();
        dVar.J(STRUCT_DESC);
        if (this.debug != null && isSetDebug()) {
            dVar.w(DEBUG_FIELD_DESC);
            dVar.I(this.debug);
            dVar.x();
        }
        if (this.target != null && isSetTarget()) {
            dVar.w(TARGET_FIELD_DESC);
            this.target.write(dVar);
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
        dVar.w(MESSAGE_TS_FIELD_DESC);
        dVar.B(this.messageTs);
        dVar.x();
        if (this.topic != null && isSetTopic()) {
            dVar.w(TOPIC_FIELD_DESC);
            dVar.I(this.topic);
            dVar.x();
        }
        if (this.aliasName != null && isSetAliasName()) {
            dVar.w(ALIAS_NAME_FIELD_DESC);
            dVar.I(this.aliasName);
            dVar.x();
        }
        if (this.request != null && isSetRequest()) {
            dVar.w(REQUEST_FIELD_DESC);
            this.request.write(dVar);
            dVar.x();
        }
        if (this.packageName != null && isSetPackageName()) {
            dVar.w(PACKAGE_NAME_FIELD_DESC);
            dVar.I(this.packageName);
            dVar.x();
        }
        if (this.category != null && isSetCategory()) {
            dVar.w(CATEGORY_FIELD_DESC);
            dVar.I(this.category);
            dVar.x();
        }
        if (isSetIsOnline()) {
            dVar.w(IS_ONLINE_FIELD_DESC);
            dVar.u(this.isOnline);
            dVar.x();
        }
        if (this.regId != null && isSetRegId()) {
            dVar.w(REG_ID_FIELD_DESC);
            dVar.I(this.regId);
            dVar.x();
        }
        if (this.callbackUrl != null && isSetCallbackUrl()) {
            dVar.w(CALLBACK_URL_FIELD_DESC);
            dVar.I(this.callbackUrl);
            dVar.x();
        }
        if (this.userAccount != null && isSetUserAccount()) {
            dVar.w(USER_ACCOUNT_FIELD_DESC);
            dVar.I(this.userAccount);
            dVar.x();
        }
        if (isSetDeviceStatus()) {
            dVar.w(DEVICE_STATUS_FIELD_DESC);
            dVar.z(this.deviceStatus);
            dVar.x();
        }
        if (isSetGeoMsgStatus()) {
            dVar.w(GEO_MSG_STATUS_FIELD_DESC);
            dVar.z(this.geoMsgStatus);
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
        if (isSetPassThrough()) {
            dVar.w(PASS_THROUGH_FIELD_DESC);
            dVar.A(this.passThrough);
            dVar.x();
        }
        if (this.extra != null && isSetExtra()) {
            dVar.w(EXTRA_FIELD_DESC);
            dVar.E(new c((byte) 11, (byte) 11, this.extra.size()));
            for (Map.Entry<String, String> entry : this.extra.entrySet()) {
                dVar.I(entry.getKey());
                dVar.I(entry.getValue());
            }
            dVar.F();
            dVar.x();
        }
        dVar.y();
        dVar.K();
    }

    @Override // java.lang.Comparable
    public int compareTo(XmPushActionAckMessage xmPushActionAckMessage) {
        int i2;
        int iD;
        int iG;
        int iG2;
        int iK;
        int iK2;
        int iG3;
        int iG4;
        int iG5;
        int iL;
        int iG6;
        int iG7;
        int iF;
        int iG8;
        int iG9;
        int iE;
        int iG10;
        int iG11;
        int iF2;
        int iG12;
        if (!getClass().equals(xmPushActionAckMessage.getClass())) {
            return getClass().getName().compareTo(xmPushActionAckMessage.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetDebug()).compareTo(Boolean.valueOf(xmPushActionAckMessage.isSetDebug()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetDebug() && (iG12 = g0.a.g(this.debug, xmPushActionAckMessage.debug)) != 0) {
            return iG12;
        }
        int iCompareTo2 = Boolean.valueOf(isSetTarget()).compareTo(Boolean.valueOf(xmPushActionAckMessage.isSetTarget()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (isSetTarget() && (iF2 = g0.a.f(this.target, xmPushActionAckMessage.target)) != 0) {
            return iF2;
        }
        int iCompareTo3 = Boolean.valueOf(isSetId()).compareTo(Boolean.valueOf(xmPushActionAckMessage.isSetId()));
        if (iCompareTo3 != 0) {
            return iCompareTo3;
        }
        if (isSetId() && (iG11 = g0.a.g(this.id, xmPushActionAckMessage.id)) != 0) {
            return iG11;
        }
        int iCompareTo4 = Boolean.valueOf(isSetAppId()).compareTo(Boolean.valueOf(xmPushActionAckMessage.isSetAppId()));
        if (iCompareTo4 != 0) {
            return iCompareTo4;
        }
        if (isSetAppId() && (iG10 = g0.a.g(this.appId, xmPushActionAckMessage.appId)) != 0) {
            return iG10;
        }
        int iCompareTo5 = Boolean.valueOf(isSetMessageTs()).compareTo(Boolean.valueOf(xmPushActionAckMessage.isSetMessageTs()));
        if (iCompareTo5 != 0) {
            return iCompareTo5;
        }
        if (isSetMessageTs() && (iE = g0.a.e(this.messageTs, xmPushActionAckMessage.messageTs)) != 0) {
            return iE;
        }
        int iCompareTo6 = Boolean.valueOf(isSetTopic()).compareTo(Boolean.valueOf(xmPushActionAckMessage.isSetTopic()));
        if (iCompareTo6 != 0) {
            return iCompareTo6;
        }
        if (isSetTopic() && (iG9 = g0.a.g(this.topic, xmPushActionAckMessage.topic)) != 0) {
            return iG9;
        }
        int iCompareTo7 = Boolean.valueOf(isSetAliasName()).compareTo(Boolean.valueOf(xmPushActionAckMessage.isSetAliasName()));
        if (iCompareTo7 != 0) {
            return iCompareTo7;
        }
        if (isSetAliasName() && (iG8 = g0.a.g(this.aliasName, xmPushActionAckMessage.aliasName)) != 0) {
            return iG8;
        }
        int iCompareTo8 = Boolean.valueOf(isSetRequest()).compareTo(Boolean.valueOf(xmPushActionAckMessage.isSetRequest()));
        if (iCompareTo8 != 0) {
            return iCompareTo8;
        }
        if (isSetRequest() && (iF = g0.a.f(this.request, xmPushActionAckMessage.request)) != 0) {
            return iF;
        }
        int iCompareTo9 = Boolean.valueOf(isSetPackageName()).compareTo(Boolean.valueOf(xmPushActionAckMessage.isSetPackageName()));
        if (iCompareTo9 != 0) {
            return iCompareTo9;
        }
        if (isSetPackageName() && (iG7 = g0.a.g(this.packageName, xmPushActionAckMessage.packageName)) != 0) {
            return iG7;
        }
        int iCompareTo10 = Boolean.valueOf(isSetCategory()).compareTo(Boolean.valueOf(xmPushActionAckMessage.isSetCategory()));
        if (iCompareTo10 != 0) {
            return iCompareTo10;
        }
        if (isSetCategory() && (iG6 = g0.a.g(this.category, xmPushActionAckMessage.category)) != 0) {
            return iG6;
        }
        int iCompareTo11 = Boolean.valueOf(isSetIsOnline()).compareTo(Boolean.valueOf(xmPushActionAckMessage.isSetIsOnline()));
        if (iCompareTo11 != 0) {
            return iCompareTo11;
        }
        if (isSetIsOnline() && (iL = g0.a.l(this.isOnline, xmPushActionAckMessage.isOnline)) != 0) {
            return iL;
        }
        int iCompareTo12 = Boolean.valueOf(isSetRegId()).compareTo(Boolean.valueOf(xmPushActionAckMessage.isSetRegId()));
        if (iCompareTo12 != 0) {
            return iCompareTo12;
        }
        if (isSetRegId() && (iG5 = g0.a.g(this.regId, xmPushActionAckMessage.regId)) != 0) {
            return iG5;
        }
        int iCompareTo13 = Boolean.valueOf(isSetCallbackUrl()).compareTo(Boolean.valueOf(xmPushActionAckMessage.isSetCallbackUrl()));
        if (iCompareTo13 != 0) {
            return iCompareTo13;
        }
        if (isSetCallbackUrl() && (iG4 = g0.a.g(this.callbackUrl, xmPushActionAckMessage.callbackUrl)) != 0) {
            return iG4;
        }
        int iCompareTo14 = Boolean.valueOf(isSetUserAccount()).compareTo(Boolean.valueOf(xmPushActionAckMessage.isSetUserAccount()));
        if (iCompareTo14 != 0) {
            return iCompareTo14;
        }
        if (isSetUserAccount() && (iG3 = g0.a.g(this.userAccount, xmPushActionAckMessage.userAccount)) != 0) {
            return iG3;
        }
        int iCompareTo15 = Boolean.valueOf(isSetDeviceStatus()).compareTo(Boolean.valueOf(xmPushActionAckMessage.isSetDeviceStatus()));
        if (iCompareTo15 != 0) {
            return iCompareTo15;
        }
        if (isSetDeviceStatus() && (iK2 = g0.a.k(this.deviceStatus, xmPushActionAckMessage.deviceStatus)) != 0) {
            return iK2;
        }
        int iCompareTo16 = Boolean.valueOf(isSetGeoMsgStatus()).compareTo(Boolean.valueOf(xmPushActionAckMessage.isSetGeoMsgStatus()));
        if (iCompareTo16 != 0) {
            return iCompareTo16;
        }
        if (isSetGeoMsgStatus() && (iK = g0.a.k(this.geoMsgStatus, xmPushActionAckMessage.geoMsgStatus)) != 0) {
            return iK;
        }
        int iCompareTo17 = Boolean.valueOf(isSetImeiMd5()).compareTo(Boolean.valueOf(xmPushActionAckMessage.isSetImeiMd5()));
        if (iCompareTo17 != 0) {
            return iCompareTo17;
        }
        if (isSetImeiMd5() && (iG2 = g0.a.g(this.imeiMd5, xmPushActionAckMessage.imeiMd5)) != 0) {
            return iG2;
        }
        int iCompareTo18 = Boolean.valueOf(isSetDeviceId()).compareTo(Boolean.valueOf(xmPushActionAckMessage.isSetDeviceId()));
        if (iCompareTo18 != 0) {
            return iCompareTo18;
        }
        if (isSetDeviceId() && (iG = g0.a.g(this.deviceId, xmPushActionAckMessage.deviceId)) != 0) {
            return iG;
        }
        int iCompareTo19 = Boolean.valueOf(isSetPassThrough()).compareTo(Boolean.valueOf(xmPushActionAckMessage.isSetPassThrough()));
        if (iCompareTo19 != 0) {
            return iCompareTo19;
        }
        if (isSetPassThrough() && (iD = g0.a.d(this.passThrough, xmPushActionAckMessage.passThrough)) != 0) {
            return iD;
        }
        int iCompareTo20 = Boolean.valueOf(isSetExtra()).compareTo(Boolean.valueOf(xmPushActionAckMessage.isSetExtra()));
        if (iCompareTo20 != 0) {
            return iCompareTo20;
        }
        if (!isSetExtra() || (i2 = g0.a.i(this.extra, xmPushActionAckMessage.extra)) == 0) {
            return 0;
        }
        return i2;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public XmPushActionAckMessage m28deepCopy() {
        return new XmPushActionAckMessage(this);
    }

    public boolean equals(XmPushActionAckMessage xmPushActionAckMessage) {
        if (xmPushActionAckMessage == null) {
            return false;
        }
        boolean zIsSetDebug = isSetDebug();
        boolean zIsSetDebug2 = xmPushActionAckMessage.isSetDebug();
        if ((zIsSetDebug || zIsSetDebug2) && !(zIsSetDebug && zIsSetDebug2 && this.debug.equals(xmPushActionAckMessage.debug))) {
            return false;
        }
        boolean zIsSetTarget = isSetTarget();
        boolean zIsSetTarget2 = xmPushActionAckMessage.isSetTarget();
        if ((zIsSetTarget || zIsSetTarget2) && !(zIsSetTarget && zIsSetTarget2 && this.target.equals(xmPushActionAckMessage.target))) {
            return false;
        }
        boolean zIsSetId = isSetId();
        boolean zIsSetId2 = xmPushActionAckMessage.isSetId();
        if ((zIsSetId || zIsSetId2) && !(zIsSetId && zIsSetId2 && this.id.equals(xmPushActionAckMessage.id))) {
            return false;
        }
        boolean zIsSetAppId = isSetAppId();
        boolean zIsSetAppId2 = xmPushActionAckMessage.isSetAppId();
        if (((zIsSetAppId || zIsSetAppId2) && !(zIsSetAppId && zIsSetAppId2 && this.appId.equals(xmPushActionAckMessage.appId))) || this.messageTs != xmPushActionAckMessage.messageTs) {
            return false;
        }
        boolean zIsSetTopic = isSetTopic();
        boolean zIsSetTopic2 = xmPushActionAckMessage.isSetTopic();
        if ((zIsSetTopic || zIsSetTopic2) && !(zIsSetTopic && zIsSetTopic2 && this.topic.equals(xmPushActionAckMessage.topic))) {
            return false;
        }
        boolean zIsSetAliasName = isSetAliasName();
        boolean zIsSetAliasName2 = xmPushActionAckMessage.isSetAliasName();
        if ((zIsSetAliasName || zIsSetAliasName2) && !(zIsSetAliasName && zIsSetAliasName2 && this.aliasName.equals(xmPushActionAckMessage.aliasName))) {
            return false;
        }
        boolean zIsSetRequest = isSetRequest();
        boolean zIsSetRequest2 = xmPushActionAckMessage.isSetRequest();
        if ((zIsSetRequest || zIsSetRequest2) && !(zIsSetRequest && zIsSetRequest2 && this.request.equals(xmPushActionAckMessage.request))) {
            return false;
        }
        boolean zIsSetPackageName = isSetPackageName();
        boolean zIsSetPackageName2 = xmPushActionAckMessage.isSetPackageName();
        if ((zIsSetPackageName || zIsSetPackageName2) && !(zIsSetPackageName && zIsSetPackageName2 && this.packageName.equals(xmPushActionAckMessage.packageName))) {
            return false;
        }
        boolean zIsSetCategory = isSetCategory();
        boolean zIsSetCategory2 = xmPushActionAckMessage.isSetCategory();
        if ((zIsSetCategory || zIsSetCategory2) && !(zIsSetCategory && zIsSetCategory2 && this.category.equals(xmPushActionAckMessage.category))) {
            return false;
        }
        boolean zIsSetIsOnline = isSetIsOnline();
        boolean zIsSetIsOnline2 = xmPushActionAckMessage.isSetIsOnline();
        if ((zIsSetIsOnline || zIsSetIsOnline2) && !(zIsSetIsOnline && zIsSetIsOnline2 && this.isOnline == xmPushActionAckMessage.isOnline)) {
            return false;
        }
        boolean zIsSetRegId = isSetRegId();
        boolean zIsSetRegId2 = xmPushActionAckMessage.isSetRegId();
        if ((zIsSetRegId || zIsSetRegId2) && !(zIsSetRegId && zIsSetRegId2 && this.regId.equals(xmPushActionAckMessage.regId))) {
            return false;
        }
        boolean zIsSetCallbackUrl = isSetCallbackUrl();
        boolean zIsSetCallbackUrl2 = xmPushActionAckMessage.isSetCallbackUrl();
        if ((zIsSetCallbackUrl || zIsSetCallbackUrl2) && !(zIsSetCallbackUrl && zIsSetCallbackUrl2 && this.callbackUrl.equals(xmPushActionAckMessage.callbackUrl))) {
            return false;
        }
        boolean zIsSetUserAccount = isSetUserAccount();
        boolean zIsSetUserAccount2 = xmPushActionAckMessage.isSetUserAccount();
        if ((zIsSetUserAccount || zIsSetUserAccount2) && !(zIsSetUserAccount && zIsSetUserAccount2 && this.userAccount.equals(xmPushActionAckMessage.userAccount))) {
            return false;
        }
        boolean zIsSetDeviceStatus = isSetDeviceStatus();
        boolean zIsSetDeviceStatus2 = xmPushActionAckMessage.isSetDeviceStatus();
        if ((zIsSetDeviceStatus || zIsSetDeviceStatus2) && !(zIsSetDeviceStatus && zIsSetDeviceStatus2 && this.deviceStatus == xmPushActionAckMessage.deviceStatus)) {
            return false;
        }
        boolean zIsSetGeoMsgStatus = isSetGeoMsgStatus();
        boolean zIsSetGeoMsgStatus2 = xmPushActionAckMessage.isSetGeoMsgStatus();
        if ((zIsSetGeoMsgStatus || zIsSetGeoMsgStatus2) && !(zIsSetGeoMsgStatus && zIsSetGeoMsgStatus2 && this.geoMsgStatus == xmPushActionAckMessage.geoMsgStatus)) {
            return false;
        }
        boolean zIsSetImeiMd5 = isSetImeiMd5();
        boolean zIsSetImeiMd6 = xmPushActionAckMessage.isSetImeiMd5();
        if ((zIsSetImeiMd5 || zIsSetImeiMd6) && !(zIsSetImeiMd5 && zIsSetImeiMd6 && this.imeiMd5.equals(xmPushActionAckMessage.imeiMd5))) {
            return false;
        }
        boolean zIsSetDeviceId = isSetDeviceId();
        boolean zIsSetDeviceId2 = xmPushActionAckMessage.isSetDeviceId();
        if ((zIsSetDeviceId || zIsSetDeviceId2) && !(zIsSetDeviceId && zIsSetDeviceId2 && this.deviceId.equals(xmPushActionAckMessage.deviceId))) {
            return false;
        }
        boolean zIsSetPassThrough = isSetPassThrough();
        boolean zIsSetPassThrough2 = xmPushActionAckMessage.isSetPassThrough();
        if ((zIsSetPassThrough || zIsSetPassThrough2) && !(zIsSetPassThrough && zIsSetPassThrough2 && this.passThrough == xmPushActionAckMessage.passThrough)) {
            return false;
        }
        boolean zIsSetExtra = isSetExtra();
        boolean zIsSetExtra2 = xmPushActionAckMessage.isSetExtra();
        if (zIsSetExtra || zIsSetExtra2) {
            return zIsSetExtra && zIsSetExtra2 && this.extra.equals(xmPushActionAckMessage.extra);
        }
        return true;
    }

    public XmPushActionAckMessage(String str, String str2, long j2) {
        this();
        this.id = str;
        this.appId = str2;
        this.messageTs = j2;
        setMessageTsIsSet(true);
    }

    public XmPushActionAckMessage(XmPushActionAckMessage xmPushActionAckMessage) {
        BitSet bitSet = new BitSet(5);
        this.__isset_bit_vector = bitSet;
        bitSet.clear();
        this.__isset_bit_vector.or(xmPushActionAckMessage.__isset_bit_vector);
        if (xmPushActionAckMessage.isSetDebug()) {
            this.debug = xmPushActionAckMessage.debug;
        }
        if (xmPushActionAckMessage.isSetTarget()) {
            this.target = new Target(xmPushActionAckMessage.target);
        }
        if (xmPushActionAckMessage.isSetId()) {
            this.id = xmPushActionAckMessage.id;
        }
        if (xmPushActionAckMessage.isSetAppId()) {
            this.appId = xmPushActionAckMessage.appId;
        }
        this.messageTs = xmPushActionAckMessage.messageTs;
        if (xmPushActionAckMessage.isSetTopic()) {
            this.topic = xmPushActionAckMessage.topic;
        }
        if (xmPushActionAckMessage.isSetAliasName()) {
            this.aliasName = xmPushActionAckMessage.aliasName;
        }
        if (xmPushActionAckMessage.isSetRequest()) {
            this.request = new XmPushActionSendMessage(xmPushActionAckMessage.request);
        }
        if (xmPushActionAckMessage.isSetPackageName()) {
            this.packageName = xmPushActionAckMessage.packageName;
        }
        if (xmPushActionAckMessage.isSetCategory()) {
            this.category = xmPushActionAckMessage.category;
        }
        this.isOnline = xmPushActionAckMessage.isOnline;
        if (xmPushActionAckMessage.isSetRegId()) {
            this.regId = xmPushActionAckMessage.regId;
        }
        if (xmPushActionAckMessage.isSetCallbackUrl()) {
            this.callbackUrl = xmPushActionAckMessage.callbackUrl;
        }
        if (xmPushActionAckMessage.isSetUserAccount()) {
            this.userAccount = xmPushActionAckMessage.userAccount;
        }
        this.deviceStatus = xmPushActionAckMessage.deviceStatus;
        this.geoMsgStatus = xmPushActionAckMessage.geoMsgStatus;
        if (xmPushActionAckMessage.isSetImeiMd5()) {
            this.imeiMd5 = xmPushActionAckMessage.imeiMd5;
        }
        if (xmPushActionAckMessage.isSetDeviceId()) {
            this.deviceId = xmPushActionAckMessage.deviceId;
        }
        this.passThrough = xmPushActionAckMessage.passThrough;
        if (xmPushActionAckMessage.isSetExtra()) {
            HashMap map = new HashMap();
            for (Map.Entry<String, String> entry : xmPushActionAckMessage.extra.entrySet()) {
                map.put(entry.getKey(), entry.getValue());
            }
            this.extra = map;
        }
    }
}
