package com.xiaomi.xmpush.thrift;

import com.xiaomi.push.mpcd.Constants;
import com.xiaomi.push.service.PacketHelper;
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
public class XmPushActionRegistration implements TBase<XmPushActionRegistration, Object>, Serializable, Cloneable {
    private static final int __APPVERSIONCODE_ISSET_ID = 1;
    private static final int __CLEANOLDREGINFO_ISSET_ID = 7;
    private static final int __CREATEDTS_ISSET_ID = 5;
    private static final int __ISHYBRIDFRAME_ISSET_ID = 6;
    private static final int __MIID_ISSET_ID = 4;
    private static final int __PUSHSDKVERSIONCODE_ISSET_ID = 0;
    private static final int __SPACEID_ISSET_ID = 2;
    private static final int __VALIDATETOKEN_ISSET_ID = 3;
    private BitSet __isset_bit_vector;
    public String aliasName;
    public String androidId;
    public String appId;
    public String appVersion;
    public int appVersionCode;
    public boolean cleanOldRegInfo;
    public Map<String, String> connectionAttrs;
    public long createdTs;
    public String debug;
    public String deviceId;
    public String id;
    public String imei;
    public String imeiMd5;
    public boolean isHybridFrame;
    public long miid;
    public String oldRegId;
    public String packageName;
    public int pushSdkVersionCode;
    public String pushSdkVersionName;
    public RegistrationReason reason;
    public String regId;
    public String sdkVersion;
    public String serial;
    public int spaceId;
    public String subImei;
    public String subImeiMd5;
    public Target target;
    public String token;
    public boolean validateToken;
    private static final g STRUCT_DESC = new g("XmPushActionRegistration");
    private static final a DEBUG_FIELD_DESC = new a("", (byte) 11, 1);
    private static final a TARGET_FIELD_DESC = new a("", (byte) 12, 2);
    private static final a ID_FIELD_DESC = new a("", (byte) 11, 3);
    private static final a APP_ID_FIELD_DESC = new a("", (byte) 11, 4);
    private static final a APP_VERSION_FIELD_DESC = new a("", (byte) 11, 5);
    private static final a PACKAGE_NAME_FIELD_DESC = new a("", (byte) 11, 6);
    private static final a TOKEN_FIELD_DESC = new a("", (byte) 11, 7);
    private static final a DEVICE_ID_FIELD_DESC = new a("", (byte) 11, 8);
    private static final a ALIAS_NAME_FIELD_DESC = new a("", (byte) 11, 9);
    private static final a SDK_VERSION_FIELD_DESC = new a("", (byte) 11, 10);
    private static final a REG_ID_FIELD_DESC = new a("", (byte) 11, 11);
    private static final a PUSH_SDK_VERSION_NAME_FIELD_DESC = new a("", (byte) 11, 12);
    private static final a PUSH_SDK_VERSION_CODE_FIELD_DESC = new a("", (byte) 8, 13);
    private static final a APP_VERSION_CODE_FIELD_DESC = new a("", (byte) 8, 14);
    private static final a ANDROID_ID_FIELD_DESC = new a("", (byte) 11, 15);
    private static final a IMEI_FIELD_DESC = new a("", (byte) 11, 16);
    private static final a SERIAL_FIELD_DESC = new a("", (byte) 11, 17);
    private static final a IMEI_MD5_FIELD_DESC = new a("", (byte) 11, 18);
    private static final a SPACE_ID_FIELD_DESC = new a("", (byte) 8, 19);
    private static final a REASON_FIELD_DESC = new a("", (byte) 8, 20);
    private static final a VALIDATE_TOKEN_FIELD_DESC = new a("", (byte) 2, 21);
    private static final a MIID_FIELD_DESC = new a("", (byte) 10, 22);
    private static final a CREATED_TS_FIELD_DESC = new a("", (byte) 10, 23);
    private static final a SUB_IMEI_FIELD_DESC = new a("", (byte) 11, 24);
    private static final a SUB_IMEI_MD5_FIELD_DESC = new a("", (byte) 11, 25);
    private static final a IS_HYBRID_FRAME_FIELD_DESC = new a("", (byte) 2, 26);
    private static final a CONNECTION_ATTRS_FIELD_DESC = new a("", (byte) 13, 100);
    private static final a CLEAN_OLD_REG_INFO_FIELD_DESC = new a("", (byte) 2, 101);
    private static final a OLD_REG_ID_FIELD_DESC = new a("", (byte) 11, 102);

    public XmPushActionRegistration() {
        this.__isset_bit_vector = new BitSet(8);
        this.validateToken = true;
        this.cleanOldRegInfo = false;
        this.isHybridFrame = false;
    }

    public void clear() {
        this.debug = null;
        this.target = null;
        this.id = null;
        this.appId = null;
        this.appVersion = null;
        this.packageName = null;
        this.token = null;
        this.deviceId = null;
        this.aliasName = null;
        this.sdkVersion = null;
        this.regId = null;
        this.pushSdkVersionName = null;
        setPushSdkVersionCodeIsSet(false);
        this.pushSdkVersionCode = 0;
        setAppVersionCodeIsSet(false);
        this.appVersionCode = 0;
        this.androidId = null;
        this.imei = null;
        this.serial = null;
        this.imeiMd5 = null;
        setSpaceIdIsSet(false);
        this.spaceId = 0;
        this.reason = null;
        this.validateToken = true;
        setMiidIsSet(false);
        this.miid = 0L;
        setCreatedTsIsSet(false);
        this.createdTs = 0L;
        this.subImei = null;
        this.subImeiMd5 = null;
        setIsHybridFrameIsSet(false);
        this.isHybridFrame = false;
        this.connectionAttrs = null;
        this.cleanOldRegInfo = false;
        this.oldRegId = null;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof XmPushActionRegistration)) {
            return equals((XmPushActionRegistration) obj);
        }
        return false;
    }

    public String getAliasName() {
        return this.aliasName;
    }

    public String getAndroidId() {
        return this.androidId;
    }

    public String getAppId() {
        return this.appId;
    }

    public String getAppVersion() {
        return this.appVersion;
    }

    public int getAppVersionCode() {
        return this.appVersionCode;
    }

    public Map<String, String> getConnectionAttrs() {
        return this.connectionAttrs;
    }

    public int getConnectionAttrsSize() {
        Map<String, String> map = this.connectionAttrs;
        if (map == null) {
            return 0;
        }
        return map.size();
    }

    public long getCreatedTs() {
        return this.createdTs;
    }

    public String getDebug() {
        return this.debug;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public String getId() {
        return this.id;
    }

    public String getImei() {
        return this.imei;
    }

    public String getImeiMd5() {
        return this.imeiMd5;
    }

    public long getMiid() {
        return this.miid;
    }

    public String getOldRegId() {
        return this.oldRegId;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public int getPushSdkVersionCode() {
        return this.pushSdkVersionCode;
    }

    public String getPushSdkVersionName() {
        return this.pushSdkVersionName;
    }

    public RegistrationReason getReason() {
        return this.reason;
    }

    public String getRegId() {
        return this.regId;
    }

    public String getSdkVersion() {
        return this.sdkVersion;
    }

    public String getSerial() {
        return this.serial;
    }

    public int getSpaceId() {
        return this.spaceId;
    }

    public String getSubImei() {
        return this.subImei;
    }

    public String getSubImeiMd5() {
        return this.subImeiMd5;
    }

    public Target getTarget() {
        return this.target;
    }

    public String getToken() {
        return this.token;
    }

    public int hashCode() {
        return 0;
    }

    public boolean isCleanOldRegInfo() {
        return this.cleanOldRegInfo;
    }

    public boolean isIsHybridFrame() {
        return this.isHybridFrame;
    }

    public boolean isSetAliasName() {
        return this.aliasName != null;
    }

    public boolean isSetAndroidId() {
        return this.androidId != null;
    }

    public boolean isSetAppId() {
        return this.appId != null;
    }

    public boolean isSetAppVersion() {
        return this.appVersion != null;
    }

    public boolean isSetAppVersionCode() {
        return this.__isset_bit_vector.get(1);
    }

    public boolean isSetCleanOldRegInfo() {
        return this.__isset_bit_vector.get(7);
    }

    public boolean isSetConnectionAttrs() {
        return this.connectionAttrs != null;
    }

    public boolean isSetCreatedTs() {
        return this.__isset_bit_vector.get(5);
    }

    public boolean isSetDebug() {
        return this.debug != null;
    }

    public boolean isSetDeviceId() {
        return this.deviceId != null;
    }

    public boolean isSetId() {
        return this.id != null;
    }

    public boolean isSetImei() {
        return this.imei != null;
    }

    public boolean isSetImeiMd5() {
        return this.imeiMd5 != null;
    }

    public boolean isSetIsHybridFrame() {
        return this.__isset_bit_vector.get(6);
    }

    public boolean isSetMiid() {
        return this.__isset_bit_vector.get(4);
    }

    public boolean isSetOldRegId() {
        return this.oldRegId != null;
    }

    public boolean isSetPackageName() {
        return this.packageName != null;
    }

    public boolean isSetPushSdkVersionCode() {
        return this.__isset_bit_vector.get(0);
    }

    public boolean isSetPushSdkVersionName() {
        return this.pushSdkVersionName != null;
    }

    public boolean isSetReason() {
        return this.reason != null;
    }

    public boolean isSetRegId() {
        return this.regId != null;
    }

    public boolean isSetSdkVersion() {
        return this.sdkVersion != null;
    }

    public boolean isSetSerial() {
        return this.serial != null;
    }

    public boolean isSetSpaceId() {
        return this.__isset_bit_vector.get(2);
    }

    public boolean isSetSubImei() {
        return this.subImei != null;
    }

    public boolean isSetSubImeiMd5() {
        return this.subImeiMd5 != null;
    }

    public boolean isSetTarget() {
        return this.target != null;
    }

    public boolean isSetToken() {
        return this.token != null;
    }

    public boolean isSetValidateToken() {
        return this.__isset_bit_vector.get(3);
    }

    public boolean isValidateToken() {
        return this.validateToken;
    }

    public void putToConnectionAttrs(String str, String str2) {
        if (this.connectionAttrs == null) {
            this.connectionAttrs = new HashMap();
        }
        this.connectionAttrs.put(str, str2);
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
            switch (s2) {
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
                    if (b2 == 11) {
                        this.appVersion = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 6:
                    if (b2 == 11) {
                        this.packageName = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 7:
                    if (b2 == 11) {
                        this.token = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 8:
                    if (b2 == 11) {
                        this.deviceId = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 9:
                    if (b2 == 11) {
                        this.aliasName = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 10:
                    if (b2 == 11) {
                        this.sdkVersion = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 11:
                    if (b2 == 11) {
                        this.regId = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 12:
                    if (b2 == 11) {
                        this.pushSdkVersionName = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 13:
                    if (b2 == 8) {
                        this.pushSdkVersionCode = dVar.h();
                        setPushSdkVersionCodeIsSet(true);
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 14:
                    if (b2 == 8) {
                        this.appVersionCode = dVar.h();
                        setAppVersionCodeIsSet(true);
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 15:
                    if (b2 == 11) {
                        this.androidId = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 16:
                    if (b2 == 11) {
                        this.imei = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 17:
                    if (b2 == 11) {
                        this.serial = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 18:
                    if (b2 == 11) {
                        this.imeiMd5 = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 19:
                    if (b2 == 8) {
                        this.spaceId = dVar.h();
                        setSpaceIdIsSet(true);
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 20:
                    if (b2 == 8) {
                        this.reason = RegistrationReason.findByValue(dVar.h());
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 21:
                    if (b2 == 2) {
                        this.validateToken = dVar.b();
                        setValidateTokenIsSet(true);
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 22:
                    if (b2 == 10) {
                        this.miid = dVar.i();
                        setMiidIsSet(true);
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 23:
                    if (b2 == 10) {
                        this.createdTs = dVar.i();
                        setCreatedTsIsSet(true);
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 24:
                    if (b2 == 11) {
                        this.subImei = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 25:
                    if (b2 == 11) {
                        this.subImeiMd5 = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 26:
                    if (b2 == 2) {
                        this.isHybridFrame = dVar.b();
                        setIsHybridFrameIsSet(true);
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                default:
                    switch (s2) {
                        case 100:
                            if (b2 == 13) {
                                c cVarL = dVar.l();
                                this.connectionAttrs = new HashMap(cVarL.f2156c * 2);
                                for (int i2 = 0; i2 < cVarL.f2156c; i2++) {
                                    this.connectionAttrs.put(dVar.p(), dVar.p());
                                }
                                dVar.m();
                            } else {
                                e.a(dVar, b2);
                            }
                            break;
                        case 101:
                            if (b2 == 2) {
                                this.cleanOldRegInfo = dVar.b();
                                setCleanOldRegInfoIsSet(true);
                            } else {
                                e.a(dVar, b2);
                            }
                            break;
                        case 102:
                            if (b2 == 11) {
                                this.oldRegId = dVar.p();
                            } else {
                                e.a(dVar, b2);
                            }
                            break;
                        default:
                            e.a(dVar, b2);
                            break;
                    }
                    break;
            }
            dVar.f();
        }
    }

    public XmPushActionRegistration setAliasName(String str) {
        this.aliasName = str;
        return this;
    }

    public void setAliasNameIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.aliasName = null;
    }

    public XmPushActionRegistration setAndroidId(String str) {
        this.androidId = str;
        return this;
    }

    public void setAndroidIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.androidId = null;
    }

    public XmPushActionRegistration setAppId(String str) {
        this.appId = str;
        return this;
    }

    public void setAppIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.appId = null;
    }

    public XmPushActionRegistration setAppVersion(String str) {
        this.appVersion = str;
        return this;
    }

    public XmPushActionRegistration setAppVersionCode(int i2) {
        this.appVersionCode = i2;
        setAppVersionCodeIsSet(true);
        return this;
    }

    public void setAppVersionCodeIsSet(boolean z2) {
        this.__isset_bit_vector.set(1, z2);
    }

    public void setAppVersionIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.appVersion = null;
    }

    public XmPushActionRegistration setCleanOldRegInfo(boolean z2) {
        this.cleanOldRegInfo = z2;
        setCleanOldRegInfoIsSet(true);
        return this;
    }

    public void setCleanOldRegInfoIsSet(boolean z2) {
        this.__isset_bit_vector.set(7, z2);
    }

    public XmPushActionRegistration setConnectionAttrs(Map<String, String> map) {
        this.connectionAttrs = map;
        return this;
    }

    public void setConnectionAttrsIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.connectionAttrs = null;
    }

    public XmPushActionRegistration setCreatedTs(long j2) {
        this.createdTs = j2;
        setCreatedTsIsSet(true);
        return this;
    }

    public void setCreatedTsIsSet(boolean z2) {
        this.__isset_bit_vector.set(5, z2);
    }

    public XmPushActionRegistration setDebug(String str) {
        this.debug = str;
        return this;
    }

    public void setDebugIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.debug = null;
    }

    public XmPushActionRegistration setDeviceId(String str) {
        this.deviceId = str;
        return this;
    }

    public void setDeviceIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.deviceId = null;
    }

    public XmPushActionRegistration setId(String str) {
        this.id = str;
        return this;
    }

    public void setIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.id = null;
    }

    public XmPushActionRegistration setImei(String str) {
        this.imei = str;
        return this;
    }

    public void setImeiIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.imei = null;
    }

    public XmPushActionRegistration setImeiMd5(String str) {
        this.imeiMd5 = str;
        return this;
    }

    public void setImeiMd5IsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.imeiMd5 = null;
    }

    public XmPushActionRegistration setIsHybridFrame(boolean z2) {
        this.isHybridFrame = z2;
        setIsHybridFrameIsSet(true);
        return this;
    }

    public void setIsHybridFrameIsSet(boolean z2) {
        this.__isset_bit_vector.set(6, z2);
    }

    public XmPushActionRegistration setMiid(long j2) {
        this.miid = j2;
        setMiidIsSet(true);
        return this;
    }

    public void setMiidIsSet(boolean z2) {
        this.__isset_bit_vector.set(4, z2);
    }

    public XmPushActionRegistration setOldRegId(String str) {
        this.oldRegId = str;
        return this;
    }

    public void setOldRegIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.oldRegId = null;
    }

    public XmPushActionRegistration setPackageName(String str) {
        this.packageName = str;
        return this;
    }

    public void setPackageNameIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.packageName = null;
    }

    public XmPushActionRegistration setPushSdkVersionCode(int i2) {
        this.pushSdkVersionCode = i2;
        setPushSdkVersionCodeIsSet(true);
        return this;
    }

    public void setPushSdkVersionCodeIsSet(boolean z2) {
        this.__isset_bit_vector.set(0, z2);
    }

    public XmPushActionRegistration setPushSdkVersionName(String str) {
        this.pushSdkVersionName = str;
        return this;
    }

    public void setPushSdkVersionNameIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.pushSdkVersionName = null;
    }

    public XmPushActionRegistration setReason(RegistrationReason registrationReason) {
        this.reason = registrationReason;
        return this;
    }

    public void setReasonIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.reason = null;
    }

    public XmPushActionRegistration setRegId(String str) {
        this.regId = str;
        return this;
    }

    public void setRegIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.regId = null;
    }

    public XmPushActionRegistration setSdkVersion(String str) {
        this.sdkVersion = str;
        return this;
    }

    public void setSdkVersionIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.sdkVersion = null;
    }

    public XmPushActionRegistration setSerial(String str) {
        this.serial = str;
        return this;
    }

    public void setSerialIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.serial = null;
    }

    public XmPushActionRegistration setSpaceId(int i2) {
        this.spaceId = i2;
        setSpaceIdIsSet(true);
        return this;
    }

    public void setSpaceIdIsSet(boolean z2) {
        this.__isset_bit_vector.set(2, z2);
    }

    public XmPushActionRegistration setSubImei(String str) {
        this.subImei = str;
        return this;
    }

    public void setSubImeiIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.subImei = null;
    }

    public XmPushActionRegistration setSubImeiMd5(String str) {
        this.subImeiMd5 = str;
        return this;
    }

    public void setSubImeiMd5IsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.subImeiMd5 = null;
    }

    public XmPushActionRegistration setTarget(Target target) {
        this.target = target;
        return this;
    }

    public void setTargetIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.target = null;
    }

    public XmPushActionRegistration setToken(String str) {
        this.token = str;
        return this;
    }

    public void setTokenIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.token = null;
    }

    public XmPushActionRegistration setValidateToken(boolean z2) {
        this.validateToken = z2;
        setValidateTokenIsSet(true);
        return this;
    }

    public void setValidateTokenIsSet(boolean z2) {
        this.__isset_bit_vector.set(3, z2);
    }

    public String toString() {
        boolean z2;
        StringBuilder sb = new StringBuilder("XmPushActionRegistration(");
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
            sb.append(PacketHelper.filterRegistrationPacketIdForLog(str2));
        }
        sb.append(", ");
        sb.append("appId:");
        String str3 = this.appId;
        if (str3 == null) {
            sb.append("null");
        } else {
            sb.append(str3);
        }
        if (isSetAppVersion()) {
            sb.append(", ");
            sb.append("appVersion:");
            String str4 = this.appVersion;
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
        sb.append(", ");
        sb.append("token:");
        String str6 = this.token;
        if (str6 == null) {
            sb.append("null");
        } else {
            sb.append(str6);
        }
        if (isSetDeviceId()) {
            sb.append(", ");
            sb.append("deviceId:");
            String str7 = this.deviceId;
            if (str7 == null) {
                sb.append("null");
            } else {
                sb.append(str7);
            }
        }
        if (isSetAliasName()) {
            sb.append(", ");
            sb.append("aliasName:");
            String str8 = this.aliasName;
            if (str8 == null) {
                sb.append("null");
            } else {
                sb.append(str8);
            }
        }
        if (isSetSdkVersion()) {
            sb.append(", ");
            sb.append("sdkVersion:");
            String str9 = this.sdkVersion;
            if (str9 == null) {
                sb.append("null");
            } else {
                sb.append(str9);
            }
        }
        if (isSetRegId()) {
            sb.append(", ");
            sb.append("regId:");
            String str10 = this.regId;
            if (str10 == null) {
                sb.append("null");
            } else {
                sb.append(str10);
            }
        }
        if (isSetPushSdkVersionName()) {
            sb.append(", ");
            sb.append("pushSdkVersionName:");
            String str11 = this.pushSdkVersionName;
            if (str11 == null) {
                sb.append("null");
            } else {
                sb.append(str11);
            }
        }
        if (isSetPushSdkVersionCode()) {
            sb.append(", ");
            sb.append("pushSdkVersionCode:");
            sb.append(this.pushSdkVersionCode);
        }
        if (isSetAppVersionCode()) {
            sb.append(", ");
            sb.append("appVersionCode:");
            sb.append(this.appVersionCode);
        }
        if (isSetAndroidId()) {
            sb.append(", ");
            sb.append("androidId:");
            String str12 = this.androidId;
            if (str12 == null) {
                sb.append("null");
            } else {
                sb.append(str12);
            }
        }
        if (isSetImei()) {
            sb.append(", ");
            sb.append("imei:");
            String str13 = this.imei;
            if (str13 == null) {
                sb.append("null");
            } else {
                sb.append(str13);
            }
        }
        if (isSetSerial()) {
            sb.append(", ");
            sb.append("serial:");
            String str14 = this.serial;
            if (str14 == null) {
                sb.append("null");
            } else {
                sb.append(str14);
            }
        }
        if (isSetImeiMd5()) {
            sb.append(", ");
            sb.append("imeiMd5:");
            String str15 = this.imeiMd5;
            if (str15 == null) {
                sb.append("null");
            } else {
                sb.append(str15);
            }
        }
        if (isSetSpaceId()) {
            sb.append(", ");
            sb.append("spaceId:");
            sb.append(this.spaceId);
        }
        if (isSetReason()) {
            sb.append(", ");
            sb.append("reason:");
            RegistrationReason registrationReason = this.reason;
            if (registrationReason == null) {
                sb.append("null");
            } else {
                sb.append(registrationReason);
            }
        }
        if (isSetValidateToken()) {
            sb.append(", ");
            sb.append("validateToken:");
            sb.append(this.validateToken);
        }
        if (isSetMiid()) {
            sb.append(", ");
            sb.append("miid:");
            sb.append(this.miid);
        }
        if (isSetCreatedTs()) {
            sb.append(", ");
            sb.append("createdTs:");
            sb.append(this.createdTs);
        }
        if (isSetSubImei()) {
            sb.append(", ");
            sb.append("subImei:");
            String str16 = this.subImei;
            if (str16 == null) {
                sb.append("null");
            } else {
                sb.append(str16);
            }
        }
        if (isSetSubImeiMd5()) {
            sb.append(", ");
            sb.append("subImeiMd5:");
            String str17 = this.subImeiMd5;
            if (str17 == null) {
                sb.append("null");
            } else {
                sb.append(str17);
            }
        }
        if (isSetIsHybridFrame()) {
            sb.append(", ");
            sb.append("isHybridFrame:");
            sb.append(this.isHybridFrame);
        }
        if (isSetConnectionAttrs()) {
            sb.append(", ");
            sb.append("connectionAttrs:");
            Map<String, String> map = this.connectionAttrs;
            if (map == null) {
                sb.append("null");
            } else {
                sb.append(map);
            }
        }
        if (isSetCleanOldRegInfo()) {
            sb.append(", ");
            sb.append("cleanOldRegInfo:");
            sb.append(this.cleanOldRegInfo);
        }
        if (isSetOldRegId()) {
            sb.append(", ");
            sb.append("oldRegId:");
            String str18 = this.oldRegId;
            if (str18 == null) {
                sb.append("null");
            } else {
                sb.append(str18);
            }
        }
        sb.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
        return sb.toString();
    }

    public void unsetAliasName() {
        this.aliasName = null;
    }

    public void unsetAndroidId() {
        this.androidId = null;
    }

    public void unsetAppId() {
        this.appId = null;
    }

    public void unsetAppVersion() {
        this.appVersion = null;
    }

    public void unsetAppVersionCode() {
        this.__isset_bit_vector.clear(1);
    }

    public void unsetCleanOldRegInfo() {
        this.__isset_bit_vector.clear(7);
    }

    public void unsetConnectionAttrs() {
        this.connectionAttrs = null;
    }

    public void unsetCreatedTs() {
        this.__isset_bit_vector.clear(5);
    }

    public void unsetDebug() {
        this.debug = null;
    }

    public void unsetDeviceId() {
        this.deviceId = null;
    }

    public void unsetId() {
        this.id = null;
    }

    public void unsetImei() {
        this.imei = null;
    }

    public void unsetImeiMd5() {
        this.imeiMd5 = null;
    }

    public void unsetIsHybridFrame() {
        this.__isset_bit_vector.clear(6);
    }

    public void unsetMiid() {
        this.__isset_bit_vector.clear(4);
    }

    public void unsetOldRegId() {
        this.oldRegId = null;
    }

    public void unsetPackageName() {
        this.packageName = null;
    }

    public void unsetPushSdkVersionCode() {
        this.__isset_bit_vector.clear(0);
    }

    public void unsetPushSdkVersionName() {
        this.pushSdkVersionName = null;
    }

    public void unsetReason() {
        this.reason = null;
    }

    public void unsetRegId() {
        this.regId = null;
    }

    public void unsetSdkVersion() {
        this.sdkVersion = null;
    }

    public void unsetSerial() {
        this.serial = null;
    }

    public void unsetSpaceId() {
        this.__isset_bit_vector.clear(2);
    }

    public void unsetSubImei() {
        this.subImei = null;
    }

    public void unsetSubImeiMd5() {
        this.subImeiMd5 = null;
    }

    public void unsetTarget() {
        this.target = null;
    }

    public void unsetToken() {
        this.token = null;
    }

    public void unsetValidateToken() {
        this.__isset_bit_vector.clear(3);
    }

    public void validate() throws TException {
        if (this.id == null) {
            throw new TProtocolException("Required field 'id' was not present! Struct: " + toString());
        }
        if (this.appId == null) {
            throw new TProtocolException("Required field 'appId' was not present! Struct: " + toString());
        }
        if (this.token != null) {
            return;
        }
        throw new TProtocolException("Required field 'token' was not present! Struct: " + toString());
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
        if (this.appVersion != null && isSetAppVersion()) {
            dVar.w(APP_VERSION_FIELD_DESC);
            dVar.I(this.appVersion);
            dVar.x();
        }
        if (this.packageName != null && isSetPackageName()) {
            dVar.w(PACKAGE_NAME_FIELD_DESC);
            dVar.I(this.packageName);
            dVar.x();
        }
        if (this.token != null) {
            dVar.w(TOKEN_FIELD_DESC);
            dVar.I(this.token);
            dVar.x();
        }
        if (this.deviceId != null && isSetDeviceId()) {
            dVar.w(DEVICE_ID_FIELD_DESC);
            dVar.I(this.deviceId);
            dVar.x();
        }
        if (this.aliasName != null && isSetAliasName()) {
            dVar.w(ALIAS_NAME_FIELD_DESC);
            dVar.I(this.aliasName);
            dVar.x();
        }
        if (this.sdkVersion != null && isSetSdkVersion()) {
            dVar.w(SDK_VERSION_FIELD_DESC);
            dVar.I(this.sdkVersion);
            dVar.x();
        }
        if (this.regId != null && isSetRegId()) {
            dVar.w(REG_ID_FIELD_DESC);
            dVar.I(this.regId);
            dVar.x();
        }
        if (this.pushSdkVersionName != null && isSetPushSdkVersionName()) {
            dVar.w(PUSH_SDK_VERSION_NAME_FIELD_DESC);
            dVar.I(this.pushSdkVersionName);
            dVar.x();
        }
        if (isSetPushSdkVersionCode()) {
            dVar.w(PUSH_SDK_VERSION_CODE_FIELD_DESC);
            dVar.A(this.pushSdkVersionCode);
            dVar.x();
        }
        if (isSetAppVersionCode()) {
            dVar.w(APP_VERSION_CODE_FIELD_DESC);
            dVar.A(this.appVersionCode);
            dVar.x();
        }
        if (this.androidId != null && isSetAndroidId()) {
            dVar.w(ANDROID_ID_FIELD_DESC);
            dVar.I(this.androidId);
            dVar.x();
        }
        if (this.imei != null && isSetImei()) {
            dVar.w(IMEI_FIELD_DESC);
            dVar.I(this.imei);
            dVar.x();
        }
        if (this.serial != null && isSetSerial()) {
            dVar.w(SERIAL_FIELD_DESC);
            dVar.I(this.serial);
            dVar.x();
        }
        if (this.imeiMd5 != null && isSetImeiMd5()) {
            dVar.w(IMEI_MD5_FIELD_DESC);
            dVar.I(this.imeiMd5);
            dVar.x();
        }
        if (isSetSpaceId()) {
            dVar.w(SPACE_ID_FIELD_DESC);
            dVar.A(this.spaceId);
            dVar.x();
        }
        if (this.reason != null && isSetReason()) {
            dVar.w(REASON_FIELD_DESC);
            dVar.A(this.reason.getValue());
            dVar.x();
        }
        if (isSetValidateToken()) {
            dVar.w(VALIDATE_TOKEN_FIELD_DESC);
            dVar.u(this.validateToken);
            dVar.x();
        }
        if (isSetMiid()) {
            dVar.w(MIID_FIELD_DESC);
            dVar.B(this.miid);
            dVar.x();
        }
        if (isSetCreatedTs()) {
            dVar.w(CREATED_TS_FIELD_DESC);
            dVar.B(this.createdTs);
            dVar.x();
        }
        if (this.subImei != null && isSetSubImei()) {
            dVar.w(SUB_IMEI_FIELD_DESC);
            dVar.I(this.subImei);
            dVar.x();
        }
        if (this.subImeiMd5 != null && isSetSubImeiMd5()) {
            dVar.w(SUB_IMEI_MD5_FIELD_DESC);
            dVar.I(this.subImeiMd5);
            dVar.x();
        }
        if (isSetIsHybridFrame()) {
            dVar.w(IS_HYBRID_FRAME_FIELD_DESC);
            dVar.u(this.isHybridFrame);
            dVar.x();
        }
        if (this.connectionAttrs != null && isSetConnectionAttrs()) {
            dVar.w(CONNECTION_ATTRS_FIELD_DESC);
            dVar.E(new c((byte) 11, (byte) 11, this.connectionAttrs.size()));
            for (Map.Entry<String, String> entry : this.connectionAttrs.entrySet()) {
                dVar.I(entry.getKey());
                dVar.I(entry.getValue());
            }
            dVar.F();
            dVar.x();
        }
        if (isSetCleanOldRegInfo()) {
            dVar.w(CLEAN_OLD_REG_INFO_FIELD_DESC);
            dVar.u(this.cleanOldRegInfo);
            dVar.x();
        }
        if (this.oldRegId != null && isSetOldRegId()) {
            dVar.w(OLD_REG_ID_FIELD_DESC);
            dVar.I(this.oldRegId);
            dVar.x();
        }
        dVar.y();
        dVar.K();
    }

    @Override // java.lang.Comparable
    public int compareTo(XmPushActionRegistration xmPushActionRegistration) {
        int iG;
        int iL;
        int i2;
        int iL2;
        int iG2;
        int iG3;
        int iE;
        int iE2;
        int iL3;
        int iF;
        int iD;
        int iG4;
        int iG5;
        int iG6;
        int iG7;
        int iD2;
        int iD3;
        int iG8;
        int iG9;
        int iG10;
        int iG11;
        int iG12;
        int iG13;
        int iG14;
        int iG15;
        int iG16;
        int iG17;
        int iF2;
        int iG18;
        if (!getClass().equals(xmPushActionRegistration.getClass())) {
            return getClass().getName().compareTo(xmPushActionRegistration.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetDebug()).compareTo(Boolean.valueOf(xmPushActionRegistration.isSetDebug()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetDebug() && (iG18 = g0.a.g(this.debug, xmPushActionRegistration.debug)) != 0) {
            return iG18;
        }
        int iCompareTo2 = Boolean.valueOf(isSetTarget()).compareTo(Boolean.valueOf(xmPushActionRegistration.isSetTarget()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (isSetTarget() && (iF2 = g0.a.f(this.target, xmPushActionRegistration.target)) != 0) {
            return iF2;
        }
        int iCompareTo3 = Boolean.valueOf(isSetId()).compareTo(Boolean.valueOf(xmPushActionRegistration.isSetId()));
        if (iCompareTo3 != 0) {
            return iCompareTo3;
        }
        if (isSetId() && (iG17 = g0.a.g(this.id, xmPushActionRegistration.id)) != 0) {
            return iG17;
        }
        int iCompareTo4 = Boolean.valueOf(isSetAppId()).compareTo(Boolean.valueOf(xmPushActionRegistration.isSetAppId()));
        if (iCompareTo4 != 0) {
            return iCompareTo4;
        }
        if (isSetAppId() && (iG16 = g0.a.g(this.appId, xmPushActionRegistration.appId)) != 0) {
            return iG16;
        }
        int iCompareTo5 = Boolean.valueOf(isSetAppVersion()).compareTo(Boolean.valueOf(xmPushActionRegistration.isSetAppVersion()));
        if (iCompareTo5 != 0) {
            return iCompareTo5;
        }
        if (isSetAppVersion() && (iG15 = g0.a.g(this.appVersion, xmPushActionRegistration.appVersion)) != 0) {
            return iG15;
        }
        int iCompareTo6 = Boolean.valueOf(isSetPackageName()).compareTo(Boolean.valueOf(xmPushActionRegistration.isSetPackageName()));
        if (iCompareTo6 != 0) {
            return iCompareTo6;
        }
        if (isSetPackageName() && (iG14 = g0.a.g(this.packageName, xmPushActionRegistration.packageName)) != 0) {
            return iG14;
        }
        int iCompareTo7 = Boolean.valueOf(isSetToken()).compareTo(Boolean.valueOf(xmPushActionRegistration.isSetToken()));
        if (iCompareTo7 != 0) {
            return iCompareTo7;
        }
        if (isSetToken() && (iG13 = g0.a.g(this.token, xmPushActionRegistration.token)) != 0) {
            return iG13;
        }
        int iCompareTo8 = Boolean.valueOf(isSetDeviceId()).compareTo(Boolean.valueOf(xmPushActionRegistration.isSetDeviceId()));
        if (iCompareTo8 != 0) {
            return iCompareTo8;
        }
        if (isSetDeviceId() && (iG12 = g0.a.g(this.deviceId, xmPushActionRegistration.deviceId)) != 0) {
            return iG12;
        }
        int iCompareTo9 = Boolean.valueOf(isSetAliasName()).compareTo(Boolean.valueOf(xmPushActionRegistration.isSetAliasName()));
        if (iCompareTo9 != 0) {
            return iCompareTo9;
        }
        if (isSetAliasName() && (iG11 = g0.a.g(this.aliasName, xmPushActionRegistration.aliasName)) != 0) {
            return iG11;
        }
        int iCompareTo10 = Boolean.valueOf(isSetSdkVersion()).compareTo(Boolean.valueOf(xmPushActionRegistration.isSetSdkVersion()));
        if (iCompareTo10 != 0) {
            return iCompareTo10;
        }
        if (isSetSdkVersion() && (iG10 = g0.a.g(this.sdkVersion, xmPushActionRegistration.sdkVersion)) != 0) {
            return iG10;
        }
        int iCompareTo11 = Boolean.valueOf(isSetRegId()).compareTo(Boolean.valueOf(xmPushActionRegistration.isSetRegId()));
        if (iCompareTo11 != 0) {
            return iCompareTo11;
        }
        if (isSetRegId() && (iG9 = g0.a.g(this.regId, xmPushActionRegistration.regId)) != 0) {
            return iG9;
        }
        int iCompareTo12 = Boolean.valueOf(isSetPushSdkVersionName()).compareTo(Boolean.valueOf(xmPushActionRegistration.isSetPushSdkVersionName()));
        if (iCompareTo12 != 0) {
            return iCompareTo12;
        }
        if (isSetPushSdkVersionName() && (iG8 = g0.a.g(this.pushSdkVersionName, xmPushActionRegistration.pushSdkVersionName)) != 0) {
            return iG8;
        }
        int iCompareTo13 = Boolean.valueOf(isSetPushSdkVersionCode()).compareTo(Boolean.valueOf(xmPushActionRegistration.isSetPushSdkVersionCode()));
        if (iCompareTo13 != 0) {
            return iCompareTo13;
        }
        if (isSetPushSdkVersionCode() && (iD3 = g0.a.d(this.pushSdkVersionCode, xmPushActionRegistration.pushSdkVersionCode)) != 0) {
            return iD3;
        }
        int iCompareTo14 = Boolean.valueOf(isSetAppVersionCode()).compareTo(Boolean.valueOf(xmPushActionRegistration.isSetAppVersionCode()));
        if (iCompareTo14 != 0) {
            return iCompareTo14;
        }
        if (isSetAppVersionCode() && (iD2 = g0.a.d(this.appVersionCode, xmPushActionRegistration.appVersionCode)) != 0) {
            return iD2;
        }
        int iCompareTo15 = Boolean.valueOf(isSetAndroidId()).compareTo(Boolean.valueOf(xmPushActionRegistration.isSetAndroidId()));
        if (iCompareTo15 != 0) {
            return iCompareTo15;
        }
        if (isSetAndroidId() && (iG7 = g0.a.g(this.androidId, xmPushActionRegistration.androidId)) != 0) {
            return iG7;
        }
        int iCompareTo16 = Boolean.valueOf(isSetImei()).compareTo(Boolean.valueOf(xmPushActionRegistration.isSetImei()));
        if (iCompareTo16 != 0) {
            return iCompareTo16;
        }
        if (isSetImei() && (iG6 = g0.a.g(this.imei, xmPushActionRegistration.imei)) != 0) {
            return iG6;
        }
        int iCompareTo17 = Boolean.valueOf(isSetSerial()).compareTo(Boolean.valueOf(xmPushActionRegistration.isSetSerial()));
        if (iCompareTo17 != 0) {
            return iCompareTo17;
        }
        if (isSetSerial() && (iG5 = g0.a.g(this.serial, xmPushActionRegistration.serial)) != 0) {
            return iG5;
        }
        int iCompareTo18 = Boolean.valueOf(isSetImeiMd5()).compareTo(Boolean.valueOf(xmPushActionRegistration.isSetImeiMd5()));
        if (iCompareTo18 != 0) {
            return iCompareTo18;
        }
        if (isSetImeiMd5() && (iG4 = g0.a.g(this.imeiMd5, xmPushActionRegistration.imeiMd5)) != 0) {
            return iG4;
        }
        int iCompareTo19 = Boolean.valueOf(isSetSpaceId()).compareTo(Boolean.valueOf(xmPushActionRegistration.isSetSpaceId()));
        if (iCompareTo19 != 0) {
            return iCompareTo19;
        }
        if (isSetSpaceId() && (iD = g0.a.d(this.spaceId, xmPushActionRegistration.spaceId)) != 0) {
            return iD;
        }
        int iCompareTo20 = Boolean.valueOf(isSetReason()).compareTo(Boolean.valueOf(xmPushActionRegistration.isSetReason()));
        if (iCompareTo20 != 0) {
            return iCompareTo20;
        }
        if (isSetReason() && (iF = g0.a.f(this.reason, xmPushActionRegistration.reason)) != 0) {
            return iF;
        }
        int iCompareTo21 = Boolean.valueOf(isSetValidateToken()).compareTo(Boolean.valueOf(xmPushActionRegistration.isSetValidateToken()));
        if (iCompareTo21 != 0) {
            return iCompareTo21;
        }
        if (isSetValidateToken() && (iL3 = g0.a.l(this.validateToken, xmPushActionRegistration.validateToken)) != 0) {
            return iL3;
        }
        int iCompareTo22 = Boolean.valueOf(isSetMiid()).compareTo(Boolean.valueOf(xmPushActionRegistration.isSetMiid()));
        if (iCompareTo22 != 0) {
            return iCompareTo22;
        }
        if (isSetMiid() && (iE2 = g0.a.e(this.miid, xmPushActionRegistration.miid)) != 0) {
            return iE2;
        }
        int iCompareTo23 = Boolean.valueOf(isSetCreatedTs()).compareTo(Boolean.valueOf(xmPushActionRegistration.isSetCreatedTs()));
        if (iCompareTo23 != 0) {
            return iCompareTo23;
        }
        if (isSetCreatedTs() && (iE = g0.a.e(this.createdTs, xmPushActionRegistration.createdTs)) != 0) {
            return iE;
        }
        int iCompareTo24 = Boolean.valueOf(isSetSubImei()).compareTo(Boolean.valueOf(xmPushActionRegistration.isSetSubImei()));
        if (iCompareTo24 != 0) {
            return iCompareTo24;
        }
        if (isSetSubImei() && (iG3 = g0.a.g(this.subImei, xmPushActionRegistration.subImei)) != 0) {
            return iG3;
        }
        int iCompareTo25 = Boolean.valueOf(isSetSubImeiMd5()).compareTo(Boolean.valueOf(xmPushActionRegistration.isSetSubImeiMd5()));
        if (iCompareTo25 != 0) {
            return iCompareTo25;
        }
        if (isSetSubImeiMd5() && (iG2 = g0.a.g(this.subImeiMd5, xmPushActionRegistration.subImeiMd5)) != 0) {
            return iG2;
        }
        int iCompareTo26 = Boolean.valueOf(isSetIsHybridFrame()).compareTo(Boolean.valueOf(xmPushActionRegistration.isSetIsHybridFrame()));
        if (iCompareTo26 != 0) {
            return iCompareTo26;
        }
        if (isSetIsHybridFrame() && (iL2 = g0.a.l(this.isHybridFrame, xmPushActionRegistration.isHybridFrame)) != 0) {
            return iL2;
        }
        int iCompareTo27 = Boolean.valueOf(isSetConnectionAttrs()).compareTo(Boolean.valueOf(xmPushActionRegistration.isSetConnectionAttrs()));
        if (iCompareTo27 != 0) {
            return iCompareTo27;
        }
        if (isSetConnectionAttrs() && (i2 = g0.a.i(this.connectionAttrs, xmPushActionRegistration.connectionAttrs)) != 0) {
            return i2;
        }
        int iCompareTo28 = Boolean.valueOf(isSetCleanOldRegInfo()).compareTo(Boolean.valueOf(xmPushActionRegistration.isSetCleanOldRegInfo()));
        if (iCompareTo28 != 0) {
            return iCompareTo28;
        }
        if (isSetCleanOldRegInfo() && (iL = g0.a.l(this.cleanOldRegInfo, xmPushActionRegistration.cleanOldRegInfo)) != 0) {
            return iL;
        }
        int iCompareTo29 = Boolean.valueOf(isSetOldRegId()).compareTo(Boolean.valueOf(xmPushActionRegistration.isSetOldRegId()));
        if (iCompareTo29 != 0) {
            return iCompareTo29;
        }
        if (!isSetOldRegId() || (iG = g0.a.g(this.oldRegId, xmPushActionRegistration.oldRegId)) == 0) {
            return 0;
        }
        return iG;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public XmPushActionRegistration m39deepCopy() {
        return new XmPushActionRegistration(this);
    }

    public boolean equals(XmPushActionRegistration xmPushActionRegistration) {
        if (xmPushActionRegistration == null) {
            return false;
        }
        boolean zIsSetDebug = isSetDebug();
        boolean zIsSetDebug2 = xmPushActionRegistration.isSetDebug();
        if ((zIsSetDebug || zIsSetDebug2) && !(zIsSetDebug && zIsSetDebug2 && this.debug.equals(xmPushActionRegistration.debug))) {
            return false;
        }
        boolean zIsSetTarget = isSetTarget();
        boolean zIsSetTarget2 = xmPushActionRegistration.isSetTarget();
        if ((zIsSetTarget || zIsSetTarget2) && !(zIsSetTarget && zIsSetTarget2 && this.target.equals(xmPushActionRegistration.target))) {
            return false;
        }
        boolean zIsSetId = isSetId();
        boolean zIsSetId2 = xmPushActionRegistration.isSetId();
        if ((zIsSetId || zIsSetId2) && !(zIsSetId && zIsSetId2 && this.id.equals(xmPushActionRegistration.id))) {
            return false;
        }
        boolean zIsSetAppId = isSetAppId();
        boolean zIsSetAppId2 = xmPushActionRegistration.isSetAppId();
        if ((zIsSetAppId || zIsSetAppId2) && !(zIsSetAppId && zIsSetAppId2 && this.appId.equals(xmPushActionRegistration.appId))) {
            return false;
        }
        boolean zIsSetAppVersion = isSetAppVersion();
        boolean zIsSetAppVersion2 = xmPushActionRegistration.isSetAppVersion();
        if ((zIsSetAppVersion || zIsSetAppVersion2) && !(zIsSetAppVersion && zIsSetAppVersion2 && this.appVersion.equals(xmPushActionRegistration.appVersion))) {
            return false;
        }
        boolean zIsSetPackageName = isSetPackageName();
        boolean zIsSetPackageName2 = xmPushActionRegistration.isSetPackageName();
        if ((zIsSetPackageName || zIsSetPackageName2) && !(zIsSetPackageName && zIsSetPackageName2 && this.packageName.equals(xmPushActionRegistration.packageName))) {
            return false;
        }
        boolean zIsSetToken = isSetToken();
        boolean zIsSetToken2 = xmPushActionRegistration.isSetToken();
        if ((zIsSetToken || zIsSetToken2) && !(zIsSetToken && zIsSetToken2 && this.token.equals(xmPushActionRegistration.token))) {
            return false;
        }
        boolean zIsSetDeviceId = isSetDeviceId();
        boolean zIsSetDeviceId2 = xmPushActionRegistration.isSetDeviceId();
        if ((zIsSetDeviceId || zIsSetDeviceId2) && !(zIsSetDeviceId && zIsSetDeviceId2 && this.deviceId.equals(xmPushActionRegistration.deviceId))) {
            return false;
        }
        boolean zIsSetAliasName = isSetAliasName();
        boolean zIsSetAliasName2 = xmPushActionRegistration.isSetAliasName();
        if ((zIsSetAliasName || zIsSetAliasName2) && !(zIsSetAliasName && zIsSetAliasName2 && this.aliasName.equals(xmPushActionRegistration.aliasName))) {
            return false;
        }
        boolean zIsSetSdkVersion = isSetSdkVersion();
        boolean zIsSetSdkVersion2 = xmPushActionRegistration.isSetSdkVersion();
        if ((zIsSetSdkVersion || zIsSetSdkVersion2) && !(zIsSetSdkVersion && zIsSetSdkVersion2 && this.sdkVersion.equals(xmPushActionRegistration.sdkVersion))) {
            return false;
        }
        boolean zIsSetRegId = isSetRegId();
        boolean zIsSetRegId2 = xmPushActionRegistration.isSetRegId();
        if ((zIsSetRegId || zIsSetRegId2) && !(zIsSetRegId && zIsSetRegId2 && this.regId.equals(xmPushActionRegistration.regId))) {
            return false;
        }
        boolean zIsSetPushSdkVersionName = isSetPushSdkVersionName();
        boolean zIsSetPushSdkVersionName2 = xmPushActionRegistration.isSetPushSdkVersionName();
        if ((zIsSetPushSdkVersionName || zIsSetPushSdkVersionName2) && !(zIsSetPushSdkVersionName && zIsSetPushSdkVersionName2 && this.pushSdkVersionName.equals(xmPushActionRegistration.pushSdkVersionName))) {
            return false;
        }
        boolean zIsSetPushSdkVersionCode = isSetPushSdkVersionCode();
        boolean zIsSetPushSdkVersionCode2 = xmPushActionRegistration.isSetPushSdkVersionCode();
        if ((zIsSetPushSdkVersionCode || zIsSetPushSdkVersionCode2) && !(zIsSetPushSdkVersionCode && zIsSetPushSdkVersionCode2 && this.pushSdkVersionCode == xmPushActionRegistration.pushSdkVersionCode)) {
            return false;
        }
        boolean zIsSetAppVersionCode = isSetAppVersionCode();
        boolean zIsSetAppVersionCode2 = xmPushActionRegistration.isSetAppVersionCode();
        if ((zIsSetAppVersionCode || zIsSetAppVersionCode2) && !(zIsSetAppVersionCode && zIsSetAppVersionCode2 && this.appVersionCode == xmPushActionRegistration.appVersionCode)) {
            return false;
        }
        boolean zIsSetAndroidId = isSetAndroidId();
        boolean zIsSetAndroidId2 = xmPushActionRegistration.isSetAndroidId();
        if ((zIsSetAndroidId || zIsSetAndroidId2) && !(zIsSetAndroidId && zIsSetAndroidId2 && this.androidId.equals(xmPushActionRegistration.androidId))) {
            return false;
        }
        boolean zIsSetImei = isSetImei();
        boolean zIsSetImei2 = xmPushActionRegistration.isSetImei();
        if ((zIsSetImei || zIsSetImei2) && !(zIsSetImei && zIsSetImei2 && this.imei.equals(xmPushActionRegistration.imei))) {
            return false;
        }
        boolean zIsSetSerial = isSetSerial();
        boolean zIsSetSerial2 = xmPushActionRegistration.isSetSerial();
        if ((zIsSetSerial || zIsSetSerial2) && !(zIsSetSerial && zIsSetSerial2 && this.serial.equals(xmPushActionRegistration.serial))) {
            return false;
        }
        boolean zIsSetImeiMd5 = isSetImeiMd5();
        boolean zIsSetImeiMd6 = xmPushActionRegistration.isSetImeiMd5();
        if ((zIsSetImeiMd5 || zIsSetImeiMd6) && !(zIsSetImeiMd5 && zIsSetImeiMd6 && this.imeiMd5.equals(xmPushActionRegistration.imeiMd5))) {
            return false;
        }
        boolean zIsSetSpaceId = isSetSpaceId();
        boolean zIsSetSpaceId2 = xmPushActionRegistration.isSetSpaceId();
        if ((zIsSetSpaceId || zIsSetSpaceId2) && !(zIsSetSpaceId && zIsSetSpaceId2 && this.spaceId == xmPushActionRegistration.spaceId)) {
            return false;
        }
        boolean zIsSetReason = isSetReason();
        boolean zIsSetReason2 = xmPushActionRegistration.isSetReason();
        if ((zIsSetReason || zIsSetReason2) && !(zIsSetReason && zIsSetReason2 && this.reason.equals(xmPushActionRegistration.reason))) {
            return false;
        }
        boolean zIsSetValidateToken = isSetValidateToken();
        boolean zIsSetValidateToken2 = xmPushActionRegistration.isSetValidateToken();
        if ((zIsSetValidateToken || zIsSetValidateToken2) && !(zIsSetValidateToken && zIsSetValidateToken2 && this.validateToken == xmPushActionRegistration.validateToken)) {
            return false;
        }
        boolean zIsSetMiid = isSetMiid();
        boolean zIsSetMiid2 = xmPushActionRegistration.isSetMiid();
        if ((zIsSetMiid || zIsSetMiid2) && !(zIsSetMiid && zIsSetMiid2 && this.miid == xmPushActionRegistration.miid)) {
            return false;
        }
        boolean zIsSetCreatedTs = isSetCreatedTs();
        boolean zIsSetCreatedTs2 = xmPushActionRegistration.isSetCreatedTs();
        if ((zIsSetCreatedTs || zIsSetCreatedTs2) && !(zIsSetCreatedTs && zIsSetCreatedTs2 && this.createdTs == xmPushActionRegistration.createdTs)) {
            return false;
        }
        boolean zIsSetSubImei = isSetSubImei();
        boolean zIsSetSubImei2 = xmPushActionRegistration.isSetSubImei();
        if ((zIsSetSubImei || zIsSetSubImei2) && !(zIsSetSubImei && zIsSetSubImei2 && this.subImei.equals(xmPushActionRegistration.subImei))) {
            return false;
        }
        boolean zIsSetSubImeiMd5 = isSetSubImeiMd5();
        boolean zIsSetSubImeiMd6 = xmPushActionRegistration.isSetSubImeiMd5();
        if ((zIsSetSubImeiMd5 || zIsSetSubImeiMd6) && !(zIsSetSubImeiMd5 && zIsSetSubImeiMd6 && this.subImeiMd5.equals(xmPushActionRegistration.subImeiMd5))) {
            return false;
        }
        boolean zIsSetIsHybridFrame = isSetIsHybridFrame();
        boolean zIsSetIsHybridFrame2 = xmPushActionRegistration.isSetIsHybridFrame();
        if ((zIsSetIsHybridFrame || zIsSetIsHybridFrame2) && !(zIsSetIsHybridFrame && zIsSetIsHybridFrame2 && this.isHybridFrame == xmPushActionRegistration.isHybridFrame)) {
            return false;
        }
        boolean zIsSetConnectionAttrs = isSetConnectionAttrs();
        boolean zIsSetConnectionAttrs2 = xmPushActionRegistration.isSetConnectionAttrs();
        if ((zIsSetConnectionAttrs || zIsSetConnectionAttrs2) && !(zIsSetConnectionAttrs && zIsSetConnectionAttrs2 && this.connectionAttrs.equals(xmPushActionRegistration.connectionAttrs))) {
            return false;
        }
        boolean zIsSetCleanOldRegInfo = isSetCleanOldRegInfo();
        boolean zIsSetCleanOldRegInfo2 = xmPushActionRegistration.isSetCleanOldRegInfo();
        if ((zIsSetCleanOldRegInfo || zIsSetCleanOldRegInfo2) && !(zIsSetCleanOldRegInfo && zIsSetCleanOldRegInfo2 && this.cleanOldRegInfo == xmPushActionRegistration.cleanOldRegInfo)) {
            return false;
        }
        boolean zIsSetOldRegId = isSetOldRegId();
        boolean zIsSetOldRegId2 = xmPushActionRegistration.isSetOldRegId();
        if (zIsSetOldRegId || zIsSetOldRegId2) {
            return zIsSetOldRegId && zIsSetOldRegId2 && this.oldRegId.equals(xmPushActionRegistration.oldRegId);
        }
        return true;
    }

    public XmPushActionRegistration(String str, String str2, String str3) {
        this();
        this.id = str;
        this.appId = str2;
        this.token = str3;
    }

    public XmPushActionRegistration(XmPushActionRegistration xmPushActionRegistration) {
        BitSet bitSet = new BitSet(8);
        this.__isset_bit_vector = bitSet;
        bitSet.clear();
        this.__isset_bit_vector.or(xmPushActionRegistration.__isset_bit_vector);
        if (xmPushActionRegistration.isSetDebug()) {
            this.debug = xmPushActionRegistration.debug;
        }
        if (xmPushActionRegistration.isSetTarget()) {
            this.target = new Target(xmPushActionRegistration.target);
        }
        if (xmPushActionRegistration.isSetId()) {
            this.id = xmPushActionRegistration.id;
        }
        if (xmPushActionRegistration.isSetAppId()) {
            this.appId = xmPushActionRegistration.appId;
        }
        if (xmPushActionRegistration.isSetAppVersion()) {
            this.appVersion = xmPushActionRegistration.appVersion;
        }
        if (xmPushActionRegistration.isSetPackageName()) {
            this.packageName = xmPushActionRegistration.packageName;
        }
        if (xmPushActionRegistration.isSetToken()) {
            this.token = xmPushActionRegistration.token;
        }
        if (xmPushActionRegistration.isSetDeviceId()) {
            this.deviceId = xmPushActionRegistration.deviceId;
        }
        if (xmPushActionRegistration.isSetAliasName()) {
            this.aliasName = xmPushActionRegistration.aliasName;
        }
        if (xmPushActionRegistration.isSetSdkVersion()) {
            this.sdkVersion = xmPushActionRegistration.sdkVersion;
        }
        if (xmPushActionRegistration.isSetRegId()) {
            this.regId = xmPushActionRegistration.regId;
        }
        if (xmPushActionRegistration.isSetPushSdkVersionName()) {
            this.pushSdkVersionName = xmPushActionRegistration.pushSdkVersionName;
        }
        this.pushSdkVersionCode = xmPushActionRegistration.pushSdkVersionCode;
        this.appVersionCode = xmPushActionRegistration.appVersionCode;
        if (xmPushActionRegistration.isSetAndroidId()) {
            this.androidId = xmPushActionRegistration.androidId;
        }
        if (xmPushActionRegistration.isSetImei()) {
            this.imei = xmPushActionRegistration.imei;
        }
        if (xmPushActionRegistration.isSetSerial()) {
            this.serial = xmPushActionRegistration.serial;
        }
        if (xmPushActionRegistration.isSetImeiMd5()) {
            this.imeiMd5 = xmPushActionRegistration.imeiMd5;
        }
        this.spaceId = xmPushActionRegistration.spaceId;
        if (xmPushActionRegistration.isSetReason()) {
            this.reason = xmPushActionRegistration.reason;
        }
        this.validateToken = xmPushActionRegistration.validateToken;
        this.miid = xmPushActionRegistration.miid;
        this.createdTs = xmPushActionRegistration.createdTs;
        if (xmPushActionRegistration.isSetSubImei()) {
            this.subImei = xmPushActionRegistration.subImei;
        }
        if (xmPushActionRegistration.isSetSubImeiMd5()) {
            this.subImeiMd5 = xmPushActionRegistration.subImeiMd5;
        }
        this.isHybridFrame = xmPushActionRegistration.isHybridFrame;
        if (xmPushActionRegistration.isSetConnectionAttrs()) {
            HashMap map = new HashMap();
            for (Map.Entry<String, String> entry : xmPushActionRegistration.connectionAttrs.entrySet()) {
                map.put(entry.getKey(), entry.getValue());
            }
            this.connectionAttrs = map;
        }
        this.cleanOldRegInfo = xmPushActionRegistration.cleanOldRegInfo;
        if (xmPushActionRegistration.isSetOldRegId()) {
            this.oldRegId = xmPushActionRegistration.oldRegId;
        }
    }
}
