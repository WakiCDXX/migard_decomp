package com.xiaomi.xmpush.thrift;

import com.xiaomi.push.mpcd.Constants;
import com.xiaomi.push.service.PacketHelper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.protocol.a;
import org.apache.thrift.protocol.b;
import org.apache.thrift.protocol.d;
import org.apache.thrift.protocol.e;
import org.apache.thrift.protocol.g;

/* JADX INFO: loaded from: classes.dex */
public class XmPushActionRegistrationResult implements TBase<XmPushActionRegistrationResult, Object>, Serializable, Cloneable {
    private static final int __APPVERSIONCODE_ISSET_ID = 4;
    private static final int __COSTTIME_ISSET_ID = 2;
    private static final int __ERRORCODE_ISSET_ID = 0;
    private static final int __ISHYBRIDFRAME_ISSET_ID = 5;
    private static final int __PUSHSDKVERSIONCODE_ISSET_ID = 3;
    private static final int __REGISTEREDAT_ISSET_ID = 1;
    private BitSet __isset_bit_vector;
    public String aliasName;
    public String appId;
    public String appVersion;
    public int appVersionCode;
    public List<String> autoMarkPkgs;
    public String clientId;
    public long costTime;
    public String debug;
    public long errorCode;
    public String hybridPushEndpoint;
    public String id;
    public boolean isHybridFrame;
    public String packageName;
    public int pushSdkVersionCode;
    public String reason;
    public String regId;
    public String regSecret;
    public String region;
    public long registeredAt;
    public Target target;
    private static final g STRUCT_DESC = new g("XmPushActionRegistrationResult");
    private static final a DEBUG_FIELD_DESC = new a("", (byte) 11, 1);
    private static final a TARGET_FIELD_DESC = new a("", (byte) 12, 2);
    private static final a ID_FIELD_DESC = new a("", (byte) 11, 3);
    private static final a APP_ID_FIELD_DESC = new a("", (byte) 11, 4);
    private static final a ERROR_CODE_FIELD_DESC = new a("", (byte) 10, 6);
    private static final a REASON_FIELD_DESC = new a("", (byte) 11, 7);
    private static final a REG_ID_FIELD_DESC = new a("", (byte) 11, 8);
    private static final a REG_SECRET_FIELD_DESC = new a("", (byte) 11, 9);
    private static final a PACKAGE_NAME_FIELD_DESC = new a("", (byte) 11, 10);
    private static final a REGISTERED_AT_FIELD_DESC = new a("", (byte) 10, 11);
    private static final a ALIAS_NAME_FIELD_DESC = new a("", (byte) 11, 12);
    private static final a CLIENT_ID_FIELD_DESC = new a("", (byte) 11, 13);
    private static final a COST_TIME_FIELD_DESC = new a("", (byte) 10, 14);
    private static final a APP_VERSION_FIELD_DESC = new a("", (byte) 11, 15);
    private static final a PUSH_SDK_VERSION_CODE_FIELD_DESC = new a("", (byte) 8, 16);
    private static final a HYBRID_PUSH_ENDPOINT_FIELD_DESC = new a("", (byte) 11, 17);
    private static final a APP_VERSION_CODE_FIELD_DESC = new a("", (byte) 8, 18);
    private static final a REGION_FIELD_DESC = new a("", (byte) 11, 19);
    private static final a IS_HYBRID_FRAME_FIELD_DESC = new a("", (byte) 2, 20);
    private static final a AUTO_MARK_PKGS_FIELD_DESC = new a("", (byte) 15, 21);

    public XmPushActionRegistrationResult() {
        this.__isset_bit_vector = new BitSet(6);
        this.isHybridFrame = false;
    }

    public void addToAutoMarkPkgs(String str) {
        if (this.autoMarkPkgs == null) {
            this.autoMarkPkgs = new ArrayList();
        }
        this.autoMarkPkgs.add(str);
    }

    public void clear() {
        this.debug = null;
        this.target = null;
        this.id = null;
        this.appId = null;
        setErrorCodeIsSet(false);
        this.errorCode = 0L;
        this.reason = null;
        this.regId = null;
        this.regSecret = null;
        this.packageName = null;
        setRegisteredAtIsSet(false);
        this.registeredAt = 0L;
        this.aliasName = null;
        this.clientId = null;
        setCostTimeIsSet(false);
        this.costTime = 0L;
        this.appVersion = null;
        setPushSdkVersionCodeIsSet(false);
        this.pushSdkVersionCode = 0;
        this.hybridPushEndpoint = null;
        setAppVersionCodeIsSet(false);
        this.appVersionCode = 0;
        this.region = null;
        this.isHybridFrame = false;
        this.autoMarkPkgs = null;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof XmPushActionRegistrationResult)) {
            return equals((XmPushActionRegistrationResult) obj);
        }
        return false;
    }

    public String getAliasName() {
        return this.aliasName;
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

    public List<String> getAutoMarkPkgs() {
        return this.autoMarkPkgs;
    }

    public Iterator<String> getAutoMarkPkgsIterator() {
        List<String> list = this.autoMarkPkgs;
        if (list == null) {
            return null;
        }
        return list.iterator();
    }

    public int getAutoMarkPkgsSize() {
        List<String> list = this.autoMarkPkgs;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public String getClientId() {
        return this.clientId;
    }

    public long getCostTime() {
        return this.costTime;
    }

    public String getDebug() {
        return this.debug;
    }

    public long getErrorCode() {
        return this.errorCode;
    }

    public String getHybridPushEndpoint() {
        return this.hybridPushEndpoint;
    }

    public String getId() {
        return this.id;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public int getPushSdkVersionCode() {
        return this.pushSdkVersionCode;
    }

    public String getReason() {
        return this.reason;
    }

    public String getRegId() {
        return this.regId;
    }

    public String getRegSecret() {
        return this.regSecret;
    }

    public String getRegion() {
        return this.region;
    }

    public long getRegisteredAt() {
        return this.registeredAt;
    }

    public Target getTarget() {
        return this.target;
    }

    public int hashCode() {
        return 0;
    }

    public boolean isIsHybridFrame() {
        return this.isHybridFrame;
    }

    public boolean isSetAliasName() {
        return this.aliasName != null;
    }

    public boolean isSetAppId() {
        return this.appId != null;
    }

    public boolean isSetAppVersion() {
        return this.appVersion != null;
    }

    public boolean isSetAppVersionCode() {
        return this.__isset_bit_vector.get(4);
    }

    public boolean isSetAutoMarkPkgs() {
        return this.autoMarkPkgs != null;
    }

    public boolean isSetClientId() {
        return this.clientId != null;
    }

    public boolean isSetCostTime() {
        return this.__isset_bit_vector.get(2);
    }

    public boolean isSetDebug() {
        return this.debug != null;
    }

    public boolean isSetErrorCode() {
        return this.__isset_bit_vector.get(0);
    }

    public boolean isSetHybridPushEndpoint() {
        return this.hybridPushEndpoint != null;
    }

    public boolean isSetId() {
        return this.id != null;
    }

    public boolean isSetIsHybridFrame() {
        return this.__isset_bit_vector.get(5);
    }

    public boolean isSetPackageName() {
        return this.packageName != null;
    }

    public boolean isSetPushSdkVersionCode() {
        return this.__isset_bit_vector.get(3);
    }

    public boolean isSetReason() {
        return this.reason != null;
    }

    public boolean isSetRegId() {
        return this.regId != null;
    }

    public boolean isSetRegSecret() {
        return this.regSecret != null;
    }

    public boolean isSetRegion() {
        return this.region != null;
    }

    public boolean isSetRegisteredAt() {
        return this.__isset_bit_vector.get(1);
    }

    public boolean isSetTarget() {
        return this.target != null;
    }

    @Override // org.apache.thrift.TBase
    public void read(d dVar) throws TException {
        dVar.q();
        while (true) {
            a aVarE = dVar.e();
            byte b2 = aVarE.f2150b;
            if (b2 == 0) {
                dVar.r();
                if (isSetErrorCode()) {
                    validate();
                    return;
                }
                throw new TProtocolException("Required field 'errorCode' was not found in serialized data! Struct: " + toString());
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
                default:
                    e.a(dVar, b2);
                    break;
                case 6:
                    if (b2 == 10) {
                        this.errorCode = dVar.i();
                        setErrorCodeIsSet(true);
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 7:
                    if (b2 == 11) {
                        this.reason = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 8:
                    if (b2 == 11) {
                        this.regId = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 9:
                    if (b2 == 11) {
                        this.regSecret = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 10:
                    if (b2 == 11) {
                        this.packageName = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 11:
                    if (b2 == 10) {
                        this.registeredAt = dVar.i();
                        setRegisteredAtIsSet(true);
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 12:
                    if (b2 == 11) {
                        this.aliasName = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 13:
                    if (b2 == 11) {
                        this.clientId = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 14:
                    if (b2 == 10) {
                        this.costTime = dVar.i();
                        setCostTimeIsSet(true);
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 15:
                    if (b2 == 11) {
                        this.appVersion = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 16:
                    if (b2 == 8) {
                        this.pushSdkVersionCode = dVar.h();
                        setPushSdkVersionCodeIsSet(true);
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 17:
                    if (b2 == 11) {
                        this.hybridPushEndpoint = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 18:
                    if (b2 == 8) {
                        this.appVersionCode = dVar.h();
                        setAppVersionCodeIsSet(true);
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 19:
                    if (b2 == 11) {
                        this.region = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 20:
                    if (b2 == 2) {
                        this.isHybridFrame = dVar.b();
                        setIsHybridFrameIsSet(true);
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 21:
                    if (b2 == 15) {
                        b bVarJ = dVar.j();
                        this.autoMarkPkgs = new ArrayList(bVarJ.f2153b);
                        for (int i2 = 0; i2 < bVarJ.f2153b; i2++) {
                            this.autoMarkPkgs.add(dVar.p());
                        }
                        dVar.k();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
            }
            dVar.f();
        }
    }

    public XmPushActionRegistrationResult setAliasName(String str) {
        this.aliasName = str;
        return this;
    }

    public void setAliasNameIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.aliasName = null;
    }

    public XmPushActionRegistrationResult setAppId(String str) {
        this.appId = str;
        return this;
    }

    public void setAppIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.appId = null;
    }

    public XmPushActionRegistrationResult setAppVersion(String str) {
        this.appVersion = str;
        return this;
    }

    public XmPushActionRegistrationResult setAppVersionCode(int i2) {
        this.appVersionCode = i2;
        setAppVersionCodeIsSet(true);
        return this;
    }

    public void setAppVersionCodeIsSet(boolean z2) {
        this.__isset_bit_vector.set(4, z2);
    }

    public void setAppVersionIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.appVersion = null;
    }

    public XmPushActionRegistrationResult setAutoMarkPkgs(List<String> list) {
        this.autoMarkPkgs = list;
        return this;
    }

    public void setAutoMarkPkgsIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.autoMarkPkgs = null;
    }

    public XmPushActionRegistrationResult setClientId(String str) {
        this.clientId = str;
        return this;
    }

    public void setClientIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.clientId = null;
    }

    public XmPushActionRegistrationResult setCostTime(long j2) {
        this.costTime = j2;
        setCostTimeIsSet(true);
        return this;
    }

    public void setCostTimeIsSet(boolean z2) {
        this.__isset_bit_vector.set(2, z2);
    }

    public XmPushActionRegistrationResult setDebug(String str) {
        this.debug = str;
        return this;
    }

    public void setDebugIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.debug = null;
    }

    public XmPushActionRegistrationResult setErrorCode(long j2) {
        this.errorCode = j2;
        setErrorCodeIsSet(true);
        return this;
    }

    public void setErrorCodeIsSet(boolean z2) {
        this.__isset_bit_vector.set(0, z2);
    }

    public XmPushActionRegistrationResult setHybridPushEndpoint(String str) {
        this.hybridPushEndpoint = str;
        return this;
    }

    public void setHybridPushEndpointIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.hybridPushEndpoint = null;
    }

    public XmPushActionRegistrationResult setId(String str) {
        this.id = str;
        return this;
    }

    public void setIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.id = null;
    }

    public XmPushActionRegistrationResult setIsHybridFrame(boolean z2) {
        this.isHybridFrame = z2;
        setIsHybridFrameIsSet(true);
        return this;
    }

    public void setIsHybridFrameIsSet(boolean z2) {
        this.__isset_bit_vector.set(5, z2);
    }

    public XmPushActionRegistrationResult setPackageName(String str) {
        this.packageName = str;
        return this;
    }

    public void setPackageNameIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.packageName = null;
    }

    public XmPushActionRegistrationResult setPushSdkVersionCode(int i2) {
        this.pushSdkVersionCode = i2;
        setPushSdkVersionCodeIsSet(true);
        return this;
    }

    public void setPushSdkVersionCodeIsSet(boolean z2) {
        this.__isset_bit_vector.set(3, z2);
    }

    public XmPushActionRegistrationResult setReason(String str) {
        this.reason = str;
        return this;
    }

    public void setReasonIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.reason = null;
    }

    public XmPushActionRegistrationResult setRegId(String str) {
        this.regId = str;
        return this;
    }

    public void setRegIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.regId = null;
    }

    public XmPushActionRegistrationResult setRegSecret(String str) {
        this.regSecret = str;
        return this;
    }

    public void setRegSecretIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.regSecret = null;
    }

    public XmPushActionRegistrationResult setRegion(String str) {
        this.region = str;
        return this;
    }

    public void setRegionIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.region = null;
    }

    public XmPushActionRegistrationResult setRegisteredAt(long j2) {
        this.registeredAt = j2;
        setRegisteredAtIsSet(true);
        return this;
    }

    public void setRegisteredAtIsSet(boolean z2) {
        this.__isset_bit_vector.set(1, z2);
    }

    public XmPushActionRegistrationResult setTarget(Target target) {
        this.target = target;
        return this;
    }

    public void setTargetIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.target = null;
    }

    public String toString() {
        boolean z2;
        StringBuilder sb = new StringBuilder("XmPushActionRegistrationResult(");
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
        sb.append(", ");
        sb.append("errorCode:");
        sb.append(this.errorCode);
        if (isSetReason()) {
            sb.append(", ");
            sb.append("reason:");
            String str4 = this.reason;
            if (str4 == null) {
                sb.append("null");
            } else {
                sb.append(str4);
            }
        }
        if (isSetRegId()) {
            sb.append(", ");
            sb.append("regId:");
            String str5 = this.regId;
            if (str5 == null) {
                sb.append("null");
            } else {
                sb.append(str5);
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
        if (isSetRegisteredAt()) {
            sb.append(", ");
            sb.append("registeredAt:");
            sb.append(this.registeredAt);
        }
        if (isSetAliasName()) {
            sb.append(", ");
            sb.append("aliasName:");
            String str7 = this.aliasName;
            if (str7 == null) {
                sb.append("null");
            } else {
                sb.append(str7);
            }
        }
        if (isSetClientId()) {
            sb.append(", ");
            sb.append("clientId:");
            String str8 = this.clientId;
            if (str8 == null) {
                sb.append("null");
            } else {
                sb.append(str8);
            }
        }
        if (isSetCostTime()) {
            sb.append(", ");
            sb.append("costTime:");
            sb.append(this.costTime);
        }
        if (isSetAppVersion()) {
            sb.append(", ");
            sb.append("appVersion:");
            String str9 = this.appVersion;
            if (str9 == null) {
                sb.append("null");
            } else {
                sb.append(str9);
            }
        }
        if (isSetPushSdkVersionCode()) {
            sb.append(", ");
            sb.append("pushSdkVersionCode:");
            sb.append(this.pushSdkVersionCode);
        }
        if (isSetHybridPushEndpoint()) {
            sb.append(", ");
            sb.append("hybridPushEndpoint:");
            String str10 = this.hybridPushEndpoint;
            if (str10 == null) {
                sb.append("null");
            } else {
                sb.append(str10);
            }
        }
        if (isSetAppVersionCode()) {
            sb.append(", ");
            sb.append("appVersionCode:");
            sb.append(this.appVersionCode);
        }
        if (isSetRegion()) {
            sb.append(", ");
            sb.append("region:");
            String str11 = this.region;
            if (str11 == null) {
                sb.append("null");
            } else {
                sb.append(str11);
            }
        }
        if (isSetIsHybridFrame()) {
            sb.append(", ");
            sb.append("isHybridFrame:");
            sb.append(this.isHybridFrame);
        }
        if (isSetAutoMarkPkgs()) {
            sb.append(", ");
            sb.append("autoMarkPkgs:");
            List<String> list = this.autoMarkPkgs;
            if (list == null) {
                sb.append("null");
            } else {
                sb.append(list);
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

    public void unsetAppVersion() {
        this.appVersion = null;
    }

    public void unsetAppVersionCode() {
        this.__isset_bit_vector.clear(4);
    }

    public void unsetAutoMarkPkgs() {
        this.autoMarkPkgs = null;
    }

    public void unsetClientId() {
        this.clientId = null;
    }

    public void unsetCostTime() {
        this.__isset_bit_vector.clear(2);
    }

    public void unsetDebug() {
        this.debug = null;
    }

    public void unsetErrorCode() {
        this.__isset_bit_vector.clear(0);
    }

    public void unsetHybridPushEndpoint() {
        this.hybridPushEndpoint = null;
    }

    public void unsetId() {
        this.id = null;
    }

    public void unsetIsHybridFrame() {
        this.__isset_bit_vector.clear(5);
    }

    public void unsetPackageName() {
        this.packageName = null;
    }

    public void unsetPushSdkVersionCode() {
        this.__isset_bit_vector.clear(3);
    }

    public void unsetReason() {
        this.reason = null;
    }

    public void unsetRegId() {
        this.regId = null;
    }

    public void unsetRegSecret() {
        this.regSecret = null;
    }

    public void unsetRegion() {
        this.region = null;
    }

    public void unsetRegisteredAt() {
        this.__isset_bit_vector.clear(1);
    }

    public void unsetTarget() {
        this.target = null;
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
        dVar.w(ERROR_CODE_FIELD_DESC);
        dVar.B(this.errorCode);
        dVar.x();
        if (this.reason != null && isSetReason()) {
            dVar.w(REASON_FIELD_DESC);
            dVar.I(this.reason);
            dVar.x();
        }
        if (this.regId != null && isSetRegId()) {
            dVar.w(REG_ID_FIELD_DESC);
            dVar.I(this.regId);
            dVar.x();
        }
        if (this.regSecret != null && isSetRegSecret()) {
            dVar.w(REG_SECRET_FIELD_DESC);
            dVar.I(this.regSecret);
            dVar.x();
        }
        if (this.packageName != null && isSetPackageName()) {
            dVar.w(PACKAGE_NAME_FIELD_DESC);
            dVar.I(this.packageName);
            dVar.x();
        }
        if (isSetRegisteredAt()) {
            dVar.w(REGISTERED_AT_FIELD_DESC);
            dVar.B(this.registeredAt);
            dVar.x();
        }
        if (this.aliasName != null && isSetAliasName()) {
            dVar.w(ALIAS_NAME_FIELD_DESC);
            dVar.I(this.aliasName);
            dVar.x();
        }
        if (this.clientId != null && isSetClientId()) {
            dVar.w(CLIENT_ID_FIELD_DESC);
            dVar.I(this.clientId);
            dVar.x();
        }
        if (isSetCostTime()) {
            dVar.w(COST_TIME_FIELD_DESC);
            dVar.B(this.costTime);
            dVar.x();
        }
        if (this.appVersion != null && isSetAppVersion()) {
            dVar.w(APP_VERSION_FIELD_DESC);
            dVar.I(this.appVersion);
            dVar.x();
        }
        if (isSetPushSdkVersionCode()) {
            dVar.w(PUSH_SDK_VERSION_CODE_FIELD_DESC);
            dVar.A(this.pushSdkVersionCode);
            dVar.x();
        }
        if (this.hybridPushEndpoint != null && isSetHybridPushEndpoint()) {
            dVar.w(HYBRID_PUSH_ENDPOINT_FIELD_DESC);
            dVar.I(this.hybridPushEndpoint);
            dVar.x();
        }
        if (isSetAppVersionCode()) {
            dVar.w(APP_VERSION_CODE_FIELD_DESC);
            dVar.A(this.appVersionCode);
            dVar.x();
        }
        if (this.region != null && isSetRegion()) {
            dVar.w(REGION_FIELD_DESC);
            dVar.I(this.region);
            dVar.x();
        }
        if (isSetIsHybridFrame()) {
            dVar.w(IS_HYBRID_FRAME_FIELD_DESC);
            dVar.u(this.isHybridFrame);
            dVar.x();
        }
        if (this.autoMarkPkgs != null && isSetAutoMarkPkgs()) {
            dVar.w(AUTO_MARK_PKGS_FIELD_DESC);
            dVar.C(new b((byte) 11, this.autoMarkPkgs.size()));
            Iterator<String> it = this.autoMarkPkgs.iterator();
            while (it.hasNext()) {
                dVar.I(it.next());
            }
            dVar.D();
            dVar.x();
        }
        dVar.y();
        dVar.K();
    }

    @Override // java.lang.Comparable
    public int compareTo(XmPushActionRegistrationResult xmPushActionRegistrationResult) {
        int iH;
        int iL;
        int iG;
        int iD;
        int iG2;
        int iD2;
        int iG3;
        int iE;
        int iG4;
        int iG5;
        int iE2;
        int iG6;
        int iG7;
        int iG8;
        int iG9;
        int iE3;
        int iG10;
        int iG11;
        int iF;
        int iG12;
        if (!getClass().equals(xmPushActionRegistrationResult.getClass())) {
            return getClass().getName().compareTo(xmPushActionRegistrationResult.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetDebug()).compareTo(Boolean.valueOf(xmPushActionRegistrationResult.isSetDebug()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetDebug() && (iG12 = g0.a.g(this.debug, xmPushActionRegistrationResult.debug)) != 0) {
            return iG12;
        }
        int iCompareTo2 = Boolean.valueOf(isSetTarget()).compareTo(Boolean.valueOf(xmPushActionRegistrationResult.isSetTarget()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (isSetTarget() && (iF = g0.a.f(this.target, xmPushActionRegistrationResult.target)) != 0) {
            return iF;
        }
        int iCompareTo3 = Boolean.valueOf(isSetId()).compareTo(Boolean.valueOf(xmPushActionRegistrationResult.isSetId()));
        if (iCompareTo3 != 0) {
            return iCompareTo3;
        }
        if (isSetId() && (iG11 = g0.a.g(this.id, xmPushActionRegistrationResult.id)) != 0) {
            return iG11;
        }
        int iCompareTo4 = Boolean.valueOf(isSetAppId()).compareTo(Boolean.valueOf(xmPushActionRegistrationResult.isSetAppId()));
        if (iCompareTo4 != 0) {
            return iCompareTo4;
        }
        if (isSetAppId() && (iG10 = g0.a.g(this.appId, xmPushActionRegistrationResult.appId)) != 0) {
            return iG10;
        }
        int iCompareTo5 = Boolean.valueOf(isSetErrorCode()).compareTo(Boolean.valueOf(xmPushActionRegistrationResult.isSetErrorCode()));
        if (iCompareTo5 != 0) {
            return iCompareTo5;
        }
        if (isSetErrorCode() && (iE3 = g0.a.e(this.errorCode, xmPushActionRegistrationResult.errorCode)) != 0) {
            return iE3;
        }
        int iCompareTo6 = Boolean.valueOf(isSetReason()).compareTo(Boolean.valueOf(xmPushActionRegistrationResult.isSetReason()));
        if (iCompareTo6 != 0) {
            return iCompareTo6;
        }
        if (isSetReason() && (iG9 = g0.a.g(this.reason, xmPushActionRegistrationResult.reason)) != 0) {
            return iG9;
        }
        int iCompareTo7 = Boolean.valueOf(isSetRegId()).compareTo(Boolean.valueOf(xmPushActionRegistrationResult.isSetRegId()));
        if (iCompareTo7 != 0) {
            return iCompareTo7;
        }
        if (isSetRegId() && (iG8 = g0.a.g(this.regId, xmPushActionRegistrationResult.regId)) != 0) {
            return iG8;
        }
        int iCompareTo8 = Boolean.valueOf(isSetRegSecret()).compareTo(Boolean.valueOf(xmPushActionRegistrationResult.isSetRegSecret()));
        if (iCompareTo8 != 0) {
            return iCompareTo8;
        }
        if (isSetRegSecret() && (iG7 = g0.a.g(this.regSecret, xmPushActionRegistrationResult.regSecret)) != 0) {
            return iG7;
        }
        int iCompareTo9 = Boolean.valueOf(isSetPackageName()).compareTo(Boolean.valueOf(xmPushActionRegistrationResult.isSetPackageName()));
        if (iCompareTo9 != 0) {
            return iCompareTo9;
        }
        if (isSetPackageName() && (iG6 = g0.a.g(this.packageName, xmPushActionRegistrationResult.packageName)) != 0) {
            return iG6;
        }
        int iCompareTo10 = Boolean.valueOf(isSetRegisteredAt()).compareTo(Boolean.valueOf(xmPushActionRegistrationResult.isSetRegisteredAt()));
        if (iCompareTo10 != 0) {
            return iCompareTo10;
        }
        if (isSetRegisteredAt() && (iE2 = g0.a.e(this.registeredAt, xmPushActionRegistrationResult.registeredAt)) != 0) {
            return iE2;
        }
        int iCompareTo11 = Boolean.valueOf(isSetAliasName()).compareTo(Boolean.valueOf(xmPushActionRegistrationResult.isSetAliasName()));
        if (iCompareTo11 != 0) {
            return iCompareTo11;
        }
        if (isSetAliasName() && (iG5 = g0.a.g(this.aliasName, xmPushActionRegistrationResult.aliasName)) != 0) {
            return iG5;
        }
        int iCompareTo12 = Boolean.valueOf(isSetClientId()).compareTo(Boolean.valueOf(xmPushActionRegistrationResult.isSetClientId()));
        if (iCompareTo12 != 0) {
            return iCompareTo12;
        }
        if (isSetClientId() && (iG4 = g0.a.g(this.clientId, xmPushActionRegistrationResult.clientId)) != 0) {
            return iG4;
        }
        int iCompareTo13 = Boolean.valueOf(isSetCostTime()).compareTo(Boolean.valueOf(xmPushActionRegistrationResult.isSetCostTime()));
        if (iCompareTo13 != 0) {
            return iCompareTo13;
        }
        if (isSetCostTime() && (iE = g0.a.e(this.costTime, xmPushActionRegistrationResult.costTime)) != 0) {
            return iE;
        }
        int iCompareTo14 = Boolean.valueOf(isSetAppVersion()).compareTo(Boolean.valueOf(xmPushActionRegistrationResult.isSetAppVersion()));
        if (iCompareTo14 != 0) {
            return iCompareTo14;
        }
        if (isSetAppVersion() && (iG3 = g0.a.g(this.appVersion, xmPushActionRegistrationResult.appVersion)) != 0) {
            return iG3;
        }
        int iCompareTo15 = Boolean.valueOf(isSetPushSdkVersionCode()).compareTo(Boolean.valueOf(xmPushActionRegistrationResult.isSetPushSdkVersionCode()));
        if (iCompareTo15 != 0) {
            return iCompareTo15;
        }
        if (isSetPushSdkVersionCode() && (iD2 = g0.a.d(this.pushSdkVersionCode, xmPushActionRegistrationResult.pushSdkVersionCode)) != 0) {
            return iD2;
        }
        int iCompareTo16 = Boolean.valueOf(isSetHybridPushEndpoint()).compareTo(Boolean.valueOf(xmPushActionRegistrationResult.isSetHybridPushEndpoint()));
        if (iCompareTo16 != 0) {
            return iCompareTo16;
        }
        if (isSetHybridPushEndpoint() && (iG2 = g0.a.g(this.hybridPushEndpoint, xmPushActionRegistrationResult.hybridPushEndpoint)) != 0) {
            return iG2;
        }
        int iCompareTo17 = Boolean.valueOf(isSetAppVersionCode()).compareTo(Boolean.valueOf(xmPushActionRegistrationResult.isSetAppVersionCode()));
        if (iCompareTo17 != 0) {
            return iCompareTo17;
        }
        if (isSetAppVersionCode() && (iD = g0.a.d(this.appVersionCode, xmPushActionRegistrationResult.appVersionCode)) != 0) {
            return iD;
        }
        int iCompareTo18 = Boolean.valueOf(isSetRegion()).compareTo(Boolean.valueOf(xmPushActionRegistrationResult.isSetRegion()));
        if (iCompareTo18 != 0) {
            return iCompareTo18;
        }
        if (isSetRegion() && (iG = g0.a.g(this.region, xmPushActionRegistrationResult.region)) != 0) {
            return iG;
        }
        int iCompareTo19 = Boolean.valueOf(isSetIsHybridFrame()).compareTo(Boolean.valueOf(xmPushActionRegistrationResult.isSetIsHybridFrame()));
        if (iCompareTo19 != 0) {
            return iCompareTo19;
        }
        if (isSetIsHybridFrame() && (iL = g0.a.l(this.isHybridFrame, xmPushActionRegistrationResult.isHybridFrame)) != 0) {
            return iL;
        }
        int iCompareTo20 = Boolean.valueOf(isSetAutoMarkPkgs()).compareTo(Boolean.valueOf(xmPushActionRegistrationResult.isSetAutoMarkPkgs()));
        if (iCompareTo20 != 0) {
            return iCompareTo20;
        }
        if (!isSetAutoMarkPkgs() || (iH = g0.a.h(this.autoMarkPkgs, xmPushActionRegistrationResult.autoMarkPkgs)) == 0) {
            return 0;
        }
        return iH;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public XmPushActionRegistrationResult m40deepCopy() {
        return new XmPushActionRegistrationResult(this);
    }

    public boolean equals(XmPushActionRegistrationResult xmPushActionRegistrationResult) {
        if (xmPushActionRegistrationResult == null) {
            return false;
        }
        boolean zIsSetDebug = isSetDebug();
        boolean zIsSetDebug2 = xmPushActionRegistrationResult.isSetDebug();
        if ((zIsSetDebug || zIsSetDebug2) && !(zIsSetDebug && zIsSetDebug2 && this.debug.equals(xmPushActionRegistrationResult.debug))) {
            return false;
        }
        boolean zIsSetTarget = isSetTarget();
        boolean zIsSetTarget2 = xmPushActionRegistrationResult.isSetTarget();
        if ((zIsSetTarget || zIsSetTarget2) && !(zIsSetTarget && zIsSetTarget2 && this.target.equals(xmPushActionRegistrationResult.target))) {
            return false;
        }
        boolean zIsSetId = isSetId();
        boolean zIsSetId2 = xmPushActionRegistrationResult.isSetId();
        if ((zIsSetId || zIsSetId2) && !(zIsSetId && zIsSetId2 && this.id.equals(xmPushActionRegistrationResult.id))) {
            return false;
        }
        boolean zIsSetAppId = isSetAppId();
        boolean zIsSetAppId2 = xmPushActionRegistrationResult.isSetAppId();
        if (((zIsSetAppId || zIsSetAppId2) && !(zIsSetAppId && zIsSetAppId2 && this.appId.equals(xmPushActionRegistrationResult.appId))) || this.errorCode != xmPushActionRegistrationResult.errorCode) {
            return false;
        }
        boolean zIsSetReason = isSetReason();
        boolean zIsSetReason2 = xmPushActionRegistrationResult.isSetReason();
        if ((zIsSetReason || zIsSetReason2) && !(zIsSetReason && zIsSetReason2 && this.reason.equals(xmPushActionRegistrationResult.reason))) {
            return false;
        }
        boolean zIsSetRegId = isSetRegId();
        boolean zIsSetRegId2 = xmPushActionRegistrationResult.isSetRegId();
        if ((zIsSetRegId || zIsSetRegId2) && !(zIsSetRegId && zIsSetRegId2 && this.regId.equals(xmPushActionRegistrationResult.regId))) {
            return false;
        }
        boolean zIsSetRegSecret = isSetRegSecret();
        boolean zIsSetRegSecret2 = xmPushActionRegistrationResult.isSetRegSecret();
        if ((zIsSetRegSecret || zIsSetRegSecret2) && !(zIsSetRegSecret && zIsSetRegSecret2 && this.regSecret.equals(xmPushActionRegistrationResult.regSecret))) {
            return false;
        }
        boolean zIsSetPackageName = isSetPackageName();
        boolean zIsSetPackageName2 = xmPushActionRegistrationResult.isSetPackageName();
        if ((zIsSetPackageName || zIsSetPackageName2) && !(zIsSetPackageName && zIsSetPackageName2 && this.packageName.equals(xmPushActionRegistrationResult.packageName))) {
            return false;
        }
        boolean zIsSetRegisteredAt = isSetRegisteredAt();
        boolean zIsSetRegisteredAt2 = xmPushActionRegistrationResult.isSetRegisteredAt();
        if ((zIsSetRegisteredAt || zIsSetRegisteredAt2) && !(zIsSetRegisteredAt && zIsSetRegisteredAt2 && this.registeredAt == xmPushActionRegistrationResult.registeredAt)) {
            return false;
        }
        boolean zIsSetAliasName = isSetAliasName();
        boolean zIsSetAliasName2 = xmPushActionRegistrationResult.isSetAliasName();
        if ((zIsSetAliasName || zIsSetAliasName2) && !(zIsSetAliasName && zIsSetAliasName2 && this.aliasName.equals(xmPushActionRegistrationResult.aliasName))) {
            return false;
        }
        boolean zIsSetClientId = isSetClientId();
        boolean zIsSetClientId2 = xmPushActionRegistrationResult.isSetClientId();
        if ((zIsSetClientId || zIsSetClientId2) && !(zIsSetClientId && zIsSetClientId2 && this.clientId.equals(xmPushActionRegistrationResult.clientId))) {
            return false;
        }
        boolean zIsSetCostTime = isSetCostTime();
        boolean zIsSetCostTime2 = xmPushActionRegistrationResult.isSetCostTime();
        if ((zIsSetCostTime || zIsSetCostTime2) && !(zIsSetCostTime && zIsSetCostTime2 && this.costTime == xmPushActionRegistrationResult.costTime)) {
            return false;
        }
        boolean zIsSetAppVersion = isSetAppVersion();
        boolean zIsSetAppVersion2 = xmPushActionRegistrationResult.isSetAppVersion();
        if ((zIsSetAppVersion || zIsSetAppVersion2) && !(zIsSetAppVersion && zIsSetAppVersion2 && this.appVersion.equals(xmPushActionRegistrationResult.appVersion))) {
            return false;
        }
        boolean zIsSetPushSdkVersionCode = isSetPushSdkVersionCode();
        boolean zIsSetPushSdkVersionCode2 = xmPushActionRegistrationResult.isSetPushSdkVersionCode();
        if ((zIsSetPushSdkVersionCode || zIsSetPushSdkVersionCode2) && !(zIsSetPushSdkVersionCode && zIsSetPushSdkVersionCode2 && this.pushSdkVersionCode == xmPushActionRegistrationResult.pushSdkVersionCode)) {
            return false;
        }
        boolean zIsSetHybridPushEndpoint = isSetHybridPushEndpoint();
        boolean zIsSetHybridPushEndpoint2 = xmPushActionRegistrationResult.isSetHybridPushEndpoint();
        if ((zIsSetHybridPushEndpoint || zIsSetHybridPushEndpoint2) && !(zIsSetHybridPushEndpoint && zIsSetHybridPushEndpoint2 && this.hybridPushEndpoint.equals(xmPushActionRegistrationResult.hybridPushEndpoint))) {
            return false;
        }
        boolean zIsSetAppVersionCode = isSetAppVersionCode();
        boolean zIsSetAppVersionCode2 = xmPushActionRegistrationResult.isSetAppVersionCode();
        if ((zIsSetAppVersionCode || zIsSetAppVersionCode2) && !(zIsSetAppVersionCode && zIsSetAppVersionCode2 && this.appVersionCode == xmPushActionRegistrationResult.appVersionCode)) {
            return false;
        }
        boolean zIsSetRegion = isSetRegion();
        boolean zIsSetRegion2 = xmPushActionRegistrationResult.isSetRegion();
        if ((zIsSetRegion || zIsSetRegion2) && !(zIsSetRegion && zIsSetRegion2 && this.region.equals(xmPushActionRegistrationResult.region))) {
            return false;
        }
        boolean zIsSetIsHybridFrame = isSetIsHybridFrame();
        boolean zIsSetIsHybridFrame2 = xmPushActionRegistrationResult.isSetIsHybridFrame();
        if ((zIsSetIsHybridFrame || zIsSetIsHybridFrame2) && !(zIsSetIsHybridFrame && zIsSetIsHybridFrame2 && this.isHybridFrame == xmPushActionRegistrationResult.isHybridFrame)) {
            return false;
        }
        boolean zIsSetAutoMarkPkgs = isSetAutoMarkPkgs();
        boolean zIsSetAutoMarkPkgs2 = xmPushActionRegistrationResult.isSetAutoMarkPkgs();
        if (zIsSetAutoMarkPkgs || zIsSetAutoMarkPkgs2) {
            return zIsSetAutoMarkPkgs && zIsSetAutoMarkPkgs2 && this.autoMarkPkgs.equals(xmPushActionRegistrationResult.autoMarkPkgs);
        }
        return true;
    }

    public XmPushActionRegistrationResult(String str, String str2, long j2) {
        this();
        this.id = str;
        this.appId = str2;
        this.errorCode = j2;
        setErrorCodeIsSet(true);
    }

    public XmPushActionRegistrationResult(XmPushActionRegistrationResult xmPushActionRegistrationResult) {
        BitSet bitSet = new BitSet(6);
        this.__isset_bit_vector = bitSet;
        bitSet.clear();
        this.__isset_bit_vector.or(xmPushActionRegistrationResult.__isset_bit_vector);
        if (xmPushActionRegistrationResult.isSetDebug()) {
            this.debug = xmPushActionRegistrationResult.debug;
        }
        if (xmPushActionRegistrationResult.isSetTarget()) {
            this.target = new Target(xmPushActionRegistrationResult.target);
        }
        if (xmPushActionRegistrationResult.isSetId()) {
            this.id = xmPushActionRegistrationResult.id;
        }
        if (xmPushActionRegistrationResult.isSetAppId()) {
            this.appId = xmPushActionRegistrationResult.appId;
        }
        this.errorCode = xmPushActionRegistrationResult.errorCode;
        if (xmPushActionRegistrationResult.isSetReason()) {
            this.reason = xmPushActionRegistrationResult.reason;
        }
        if (xmPushActionRegistrationResult.isSetRegId()) {
            this.regId = xmPushActionRegistrationResult.regId;
        }
        if (xmPushActionRegistrationResult.isSetRegSecret()) {
            this.regSecret = xmPushActionRegistrationResult.regSecret;
        }
        if (xmPushActionRegistrationResult.isSetPackageName()) {
            this.packageName = xmPushActionRegistrationResult.packageName;
        }
        this.registeredAt = xmPushActionRegistrationResult.registeredAt;
        if (xmPushActionRegistrationResult.isSetAliasName()) {
            this.aliasName = xmPushActionRegistrationResult.aliasName;
        }
        if (xmPushActionRegistrationResult.isSetClientId()) {
            this.clientId = xmPushActionRegistrationResult.clientId;
        }
        this.costTime = xmPushActionRegistrationResult.costTime;
        if (xmPushActionRegistrationResult.isSetAppVersion()) {
            this.appVersion = xmPushActionRegistrationResult.appVersion;
        }
        this.pushSdkVersionCode = xmPushActionRegistrationResult.pushSdkVersionCode;
        if (xmPushActionRegistrationResult.isSetHybridPushEndpoint()) {
            this.hybridPushEndpoint = xmPushActionRegistrationResult.hybridPushEndpoint;
        }
        this.appVersionCode = xmPushActionRegistrationResult.appVersionCode;
        if (xmPushActionRegistrationResult.isSetRegion()) {
            this.region = xmPushActionRegistrationResult.region;
        }
        this.isHybridFrame = xmPushActionRegistrationResult.isHybridFrame;
        if (xmPushActionRegistrationResult.isSetAutoMarkPkgs()) {
            ArrayList arrayList = new ArrayList();
            Iterator<String> it = xmPushActionRegistrationResult.autoMarkPkgs.iterator();
            while (it.hasNext()) {
                arrayList.add(it.next());
            }
            this.autoMarkPkgs = arrayList;
        }
    }
}
