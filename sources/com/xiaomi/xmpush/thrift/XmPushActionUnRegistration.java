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
public class XmPushActionUnRegistration implements TBase<XmPushActionUnRegistration, Object>, Serializable, Cloneable {
    private static final int __CREATEDTS_ISSET_ID = 1;
    private static final int __NEEDACK_ISSET_ID = 0;
    private BitSet __isset_bit_vector;
    public String aliasName;
    public String appId;
    public String appVersion;
    public long createdTs;
    public String debug;
    public String deviceId;
    public String id;
    public boolean needAck;
    public String packageName;
    public String regId;
    public Target target;
    public String token;
    private static final g STRUCT_DESC = new g("XmPushActionUnRegistration");
    private static final a DEBUG_FIELD_DESC = new a("", (byte) 11, 1);
    private static final a TARGET_FIELD_DESC = new a("", (byte) 12, 2);
    private static final a ID_FIELD_DESC = new a("", (byte) 11, 3);
    private static final a APP_ID_FIELD_DESC = new a("", (byte) 11, 4);
    private static final a REG_ID_FIELD_DESC = new a("", (byte) 11, 5);
    private static final a APP_VERSION_FIELD_DESC = new a("", (byte) 11, 6);
    private static final a PACKAGE_NAME_FIELD_DESC = new a("", (byte) 11, 7);
    private static final a TOKEN_FIELD_DESC = new a("", (byte) 11, 8);
    private static final a DEVICE_ID_FIELD_DESC = new a("", (byte) 11, 9);
    private static final a ALIAS_NAME_FIELD_DESC = new a("", (byte) 11, 10);
    private static final a NEED_ACK_FIELD_DESC = new a("", (byte) 2, 11);
    private static final a CREATED_TS_FIELD_DESC = new a("", (byte) 10, 12);

    public XmPushActionUnRegistration() {
        this.__isset_bit_vector = new BitSet(2);
        this.needAck = true;
    }

    public void clear() {
        this.debug = null;
        this.target = null;
        this.id = null;
        this.appId = null;
        this.regId = null;
        this.appVersion = null;
        this.packageName = null;
        this.token = null;
        this.deviceId = null;
        this.aliasName = null;
        this.needAck = true;
        setCreatedTsIsSet(false);
        this.createdTs = 0L;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof XmPushActionUnRegistration)) {
            return equals((XmPushActionUnRegistration) obj);
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

    public String getPackageName() {
        return this.packageName;
    }

    public String getRegId() {
        return this.regId;
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

    public boolean isNeedAck() {
        return this.needAck;
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

    public boolean isSetCreatedTs() {
        return this.__isset_bit_vector.get(1);
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

    public boolean isSetNeedAck() {
        return this.__isset_bit_vector.get(0);
    }

    public boolean isSetPackageName() {
        return this.packageName != null;
    }

    public boolean isSetRegId() {
        return this.regId != null;
    }

    public boolean isSetTarget() {
        return this.target != null;
    }

    public boolean isSetToken() {
        return this.token != null;
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
                    if (b2 != 11) {
                        e.a(dVar, b2);
                    } else {
                        this.debug = dVar.p();
                    }
                    break;
                case 2:
                    if (b2 != 12) {
                        e.a(dVar, b2);
                    } else {
                        Target target = new Target();
                        this.target = target;
                        target.read(dVar);
                    }
                    break;
                case 3:
                    if (b2 != 11) {
                        e.a(dVar, b2);
                    } else {
                        this.id = dVar.p();
                    }
                    break;
                case 4:
                    if (b2 != 11) {
                        e.a(dVar, b2);
                    } else {
                        this.appId = dVar.p();
                    }
                    break;
                case 5:
                    if (b2 != 11) {
                        e.a(dVar, b2);
                    } else {
                        this.regId = dVar.p();
                    }
                    break;
                case 6:
                    if (b2 != 11) {
                        e.a(dVar, b2);
                    } else {
                        this.appVersion = dVar.p();
                    }
                    break;
                case 7:
                    if (b2 != 11) {
                        e.a(dVar, b2);
                    } else {
                        this.packageName = dVar.p();
                    }
                    break;
                case 8:
                    if (b2 != 11) {
                        e.a(dVar, b2);
                    } else {
                        this.token = dVar.p();
                    }
                    break;
                case 9:
                    if (b2 != 11) {
                        e.a(dVar, b2);
                    } else {
                        this.deviceId = dVar.p();
                    }
                    break;
                case 10:
                    if (b2 != 11) {
                        e.a(dVar, b2);
                    } else {
                        this.aliasName = dVar.p();
                    }
                    break;
                case 11:
                    if (b2 != 2) {
                        e.a(dVar, b2);
                    } else {
                        this.needAck = dVar.b();
                        setNeedAckIsSet(true);
                    }
                    break;
                case 12:
                    if (b2 != 10) {
                        e.a(dVar, b2);
                    } else {
                        this.createdTs = dVar.i();
                        setCreatedTsIsSet(true);
                    }
                    break;
                default:
                    e.a(dVar, b2);
                    break;
            }
            dVar.f();
        }
    }

    public XmPushActionUnRegistration setAliasName(String str) {
        this.aliasName = str;
        return this;
    }

    public void setAliasNameIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.aliasName = null;
    }

    public XmPushActionUnRegistration setAppId(String str) {
        this.appId = str;
        return this;
    }

    public void setAppIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.appId = null;
    }

    public XmPushActionUnRegistration setAppVersion(String str) {
        this.appVersion = str;
        return this;
    }

    public void setAppVersionIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.appVersion = null;
    }

    public XmPushActionUnRegistration setCreatedTs(long j2) {
        this.createdTs = j2;
        setCreatedTsIsSet(true);
        return this;
    }

    public void setCreatedTsIsSet(boolean z2) {
        this.__isset_bit_vector.set(1, z2);
    }

    public XmPushActionUnRegistration setDebug(String str) {
        this.debug = str;
        return this;
    }

    public void setDebugIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.debug = null;
    }

    public XmPushActionUnRegistration setDeviceId(String str) {
        this.deviceId = str;
        return this;
    }

    public void setDeviceIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.deviceId = null;
    }

    public XmPushActionUnRegistration setId(String str) {
        this.id = str;
        return this;
    }

    public void setIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.id = null;
    }

    public XmPushActionUnRegistration setNeedAck(boolean z2) {
        this.needAck = z2;
        setNeedAckIsSet(true);
        return this;
    }

    public void setNeedAckIsSet(boolean z2) {
        this.__isset_bit_vector.set(0, z2);
    }

    public XmPushActionUnRegistration setPackageName(String str) {
        this.packageName = str;
        return this;
    }

    public void setPackageNameIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.packageName = null;
    }

    public XmPushActionUnRegistration setRegId(String str) {
        this.regId = str;
        return this;
    }

    public void setRegIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.regId = null;
    }

    public XmPushActionUnRegistration setTarget(Target target) {
        this.target = target;
        return this;
    }

    public void setTargetIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.target = null;
    }

    public XmPushActionUnRegistration setToken(String str) {
        this.token = str;
        return this;
    }

    public void setTokenIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.token = null;
    }

    public String toString() {
        boolean z2;
        StringBuilder sb = new StringBuilder("XmPushActionUnRegistration(");
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
        if (isSetRegId()) {
            sb.append(", ");
            sb.append("regId:");
            String str4 = this.regId;
            if (str4 == null) {
                sb.append("null");
            } else {
                sb.append(str4);
            }
        }
        if (isSetAppVersion()) {
            sb.append(", ");
            sb.append("appVersion:");
            String str5 = this.appVersion;
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
        if (isSetToken()) {
            sb.append(", ");
            sb.append("token:");
            String str7 = this.token;
            if (str7 == null) {
                sb.append("null");
            } else {
                sb.append(str7);
            }
        }
        if (isSetDeviceId()) {
            sb.append(", ");
            sb.append("deviceId:");
            String str8 = this.deviceId;
            if (str8 == null) {
                sb.append("null");
            } else {
                sb.append(str8);
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
        if (isSetNeedAck()) {
            sb.append(", ");
            sb.append("needAck:");
            sb.append(this.needAck);
        }
        if (isSetCreatedTs()) {
            sb.append(", ");
            sb.append("createdTs:");
            sb.append(this.createdTs);
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

    public void unsetCreatedTs() {
        this.__isset_bit_vector.clear(1);
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

    public void unsetNeedAck() {
        this.__isset_bit_vector.clear(0);
    }

    public void unsetPackageName() {
        this.packageName = null;
    }

    public void unsetRegId() {
        this.regId = null;
    }

    public void unsetTarget() {
        this.target = null;
    }

    public void unsetToken() {
        this.token = null;
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
        if (this.regId != null && isSetRegId()) {
            dVar.w(REG_ID_FIELD_DESC);
            dVar.I(this.regId);
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
        if (this.token != null && isSetToken()) {
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
        if (isSetNeedAck()) {
            dVar.w(NEED_ACK_FIELD_DESC);
            dVar.u(this.needAck);
            dVar.x();
        }
        if (isSetCreatedTs()) {
            dVar.w(CREATED_TS_FIELD_DESC);
            dVar.B(this.createdTs);
            dVar.x();
        }
        dVar.y();
        dVar.K();
    }

    @Override // java.lang.Comparable
    public int compareTo(XmPushActionUnRegistration xmPushActionUnRegistration) {
        int iE;
        int iL;
        int iG;
        int iG2;
        int iG3;
        int iG4;
        int iG5;
        int iG6;
        int iG7;
        int iG8;
        int iF;
        int iG9;
        if (!getClass().equals(xmPushActionUnRegistration.getClass())) {
            return getClass().getName().compareTo(xmPushActionUnRegistration.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetDebug()).compareTo(Boolean.valueOf(xmPushActionUnRegistration.isSetDebug()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetDebug() && (iG9 = g0.a.g(this.debug, xmPushActionUnRegistration.debug)) != 0) {
            return iG9;
        }
        int iCompareTo2 = Boolean.valueOf(isSetTarget()).compareTo(Boolean.valueOf(xmPushActionUnRegistration.isSetTarget()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (isSetTarget() && (iF = g0.a.f(this.target, xmPushActionUnRegistration.target)) != 0) {
            return iF;
        }
        int iCompareTo3 = Boolean.valueOf(isSetId()).compareTo(Boolean.valueOf(xmPushActionUnRegistration.isSetId()));
        if (iCompareTo3 != 0) {
            return iCompareTo3;
        }
        if (isSetId() && (iG8 = g0.a.g(this.id, xmPushActionUnRegistration.id)) != 0) {
            return iG8;
        }
        int iCompareTo4 = Boolean.valueOf(isSetAppId()).compareTo(Boolean.valueOf(xmPushActionUnRegistration.isSetAppId()));
        if (iCompareTo4 != 0) {
            return iCompareTo4;
        }
        if (isSetAppId() && (iG7 = g0.a.g(this.appId, xmPushActionUnRegistration.appId)) != 0) {
            return iG7;
        }
        int iCompareTo5 = Boolean.valueOf(isSetRegId()).compareTo(Boolean.valueOf(xmPushActionUnRegistration.isSetRegId()));
        if (iCompareTo5 != 0) {
            return iCompareTo5;
        }
        if (isSetRegId() && (iG6 = g0.a.g(this.regId, xmPushActionUnRegistration.regId)) != 0) {
            return iG6;
        }
        int iCompareTo6 = Boolean.valueOf(isSetAppVersion()).compareTo(Boolean.valueOf(xmPushActionUnRegistration.isSetAppVersion()));
        if (iCompareTo6 != 0) {
            return iCompareTo6;
        }
        if (isSetAppVersion() && (iG5 = g0.a.g(this.appVersion, xmPushActionUnRegistration.appVersion)) != 0) {
            return iG5;
        }
        int iCompareTo7 = Boolean.valueOf(isSetPackageName()).compareTo(Boolean.valueOf(xmPushActionUnRegistration.isSetPackageName()));
        if (iCompareTo7 != 0) {
            return iCompareTo7;
        }
        if (isSetPackageName() && (iG4 = g0.a.g(this.packageName, xmPushActionUnRegistration.packageName)) != 0) {
            return iG4;
        }
        int iCompareTo8 = Boolean.valueOf(isSetToken()).compareTo(Boolean.valueOf(xmPushActionUnRegistration.isSetToken()));
        if (iCompareTo8 != 0) {
            return iCompareTo8;
        }
        if (isSetToken() && (iG3 = g0.a.g(this.token, xmPushActionUnRegistration.token)) != 0) {
            return iG3;
        }
        int iCompareTo9 = Boolean.valueOf(isSetDeviceId()).compareTo(Boolean.valueOf(xmPushActionUnRegistration.isSetDeviceId()));
        if (iCompareTo9 != 0) {
            return iCompareTo9;
        }
        if (isSetDeviceId() && (iG2 = g0.a.g(this.deviceId, xmPushActionUnRegistration.deviceId)) != 0) {
            return iG2;
        }
        int iCompareTo10 = Boolean.valueOf(isSetAliasName()).compareTo(Boolean.valueOf(xmPushActionUnRegistration.isSetAliasName()));
        if (iCompareTo10 != 0) {
            return iCompareTo10;
        }
        if (isSetAliasName() && (iG = g0.a.g(this.aliasName, xmPushActionUnRegistration.aliasName)) != 0) {
            return iG;
        }
        int iCompareTo11 = Boolean.valueOf(isSetNeedAck()).compareTo(Boolean.valueOf(xmPushActionUnRegistration.isSetNeedAck()));
        if (iCompareTo11 != 0) {
            return iCompareTo11;
        }
        if (isSetNeedAck() && (iL = g0.a.l(this.needAck, xmPushActionUnRegistration.needAck)) != 0) {
            return iL;
        }
        int iCompareTo12 = Boolean.valueOf(isSetCreatedTs()).compareTo(Boolean.valueOf(xmPushActionUnRegistration.isSetCreatedTs()));
        if (iCompareTo12 != 0) {
            return iCompareTo12;
        }
        if (!isSetCreatedTs() || (iE = g0.a.e(this.createdTs, xmPushActionUnRegistration.createdTs)) == 0) {
            return 0;
        }
        return iE;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public XmPushActionUnRegistration m46deepCopy() {
        return new XmPushActionUnRegistration(this);
    }

    public boolean equals(XmPushActionUnRegistration xmPushActionUnRegistration) {
        if (xmPushActionUnRegistration == null) {
            return false;
        }
        boolean zIsSetDebug = isSetDebug();
        boolean zIsSetDebug2 = xmPushActionUnRegistration.isSetDebug();
        if ((zIsSetDebug || zIsSetDebug2) && !(zIsSetDebug && zIsSetDebug2 && this.debug.equals(xmPushActionUnRegistration.debug))) {
            return false;
        }
        boolean zIsSetTarget = isSetTarget();
        boolean zIsSetTarget2 = xmPushActionUnRegistration.isSetTarget();
        if ((zIsSetTarget || zIsSetTarget2) && !(zIsSetTarget && zIsSetTarget2 && this.target.equals(xmPushActionUnRegistration.target))) {
            return false;
        }
        boolean zIsSetId = isSetId();
        boolean zIsSetId2 = xmPushActionUnRegistration.isSetId();
        if ((zIsSetId || zIsSetId2) && !(zIsSetId && zIsSetId2 && this.id.equals(xmPushActionUnRegistration.id))) {
            return false;
        }
        boolean zIsSetAppId = isSetAppId();
        boolean zIsSetAppId2 = xmPushActionUnRegistration.isSetAppId();
        if ((zIsSetAppId || zIsSetAppId2) && !(zIsSetAppId && zIsSetAppId2 && this.appId.equals(xmPushActionUnRegistration.appId))) {
            return false;
        }
        boolean zIsSetRegId = isSetRegId();
        boolean zIsSetRegId2 = xmPushActionUnRegistration.isSetRegId();
        if ((zIsSetRegId || zIsSetRegId2) && !(zIsSetRegId && zIsSetRegId2 && this.regId.equals(xmPushActionUnRegistration.regId))) {
            return false;
        }
        boolean zIsSetAppVersion = isSetAppVersion();
        boolean zIsSetAppVersion2 = xmPushActionUnRegistration.isSetAppVersion();
        if ((zIsSetAppVersion || zIsSetAppVersion2) && !(zIsSetAppVersion && zIsSetAppVersion2 && this.appVersion.equals(xmPushActionUnRegistration.appVersion))) {
            return false;
        }
        boolean zIsSetPackageName = isSetPackageName();
        boolean zIsSetPackageName2 = xmPushActionUnRegistration.isSetPackageName();
        if ((zIsSetPackageName || zIsSetPackageName2) && !(zIsSetPackageName && zIsSetPackageName2 && this.packageName.equals(xmPushActionUnRegistration.packageName))) {
            return false;
        }
        boolean zIsSetToken = isSetToken();
        boolean zIsSetToken2 = xmPushActionUnRegistration.isSetToken();
        if ((zIsSetToken || zIsSetToken2) && !(zIsSetToken && zIsSetToken2 && this.token.equals(xmPushActionUnRegistration.token))) {
            return false;
        }
        boolean zIsSetDeviceId = isSetDeviceId();
        boolean zIsSetDeviceId2 = xmPushActionUnRegistration.isSetDeviceId();
        if ((zIsSetDeviceId || zIsSetDeviceId2) && !(zIsSetDeviceId && zIsSetDeviceId2 && this.deviceId.equals(xmPushActionUnRegistration.deviceId))) {
            return false;
        }
        boolean zIsSetAliasName = isSetAliasName();
        boolean zIsSetAliasName2 = xmPushActionUnRegistration.isSetAliasName();
        if ((zIsSetAliasName || zIsSetAliasName2) && !(zIsSetAliasName && zIsSetAliasName2 && this.aliasName.equals(xmPushActionUnRegistration.aliasName))) {
            return false;
        }
        boolean zIsSetNeedAck = isSetNeedAck();
        boolean zIsSetNeedAck2 = xmPushActionUnRegistration.isSetNeedAck();
        if ((zIsSetNeedAck || zIsSetNeedAck2) && !(zIsSetNeedAck && zIsSetNeedAck2 && this.needAck == xmPushActionUnRegistration.needAck)) {
            return false;
        }
        boolean zIsSetCreatedTs = isSetCreatedTs();
        boolean zIsSetCreatedTs2 = xmPushActionUnRegistration.isSetCreatedTs();
        if (zIsSetCreatedTs || zIsSetCreatedTs2) {
            return zIsSetCreatedTs && zIsSetCreatedTs2 && this.createdTs == xmPushActionUnRegistration.createdTs;
        }
        return true;
    }

    public XmPushActionUnRegistration(String str, String str2) {
        this();
        this.id = str;
        this.appId = str2;
    }

    public XmPushActionUnRegistration(XmPushActionUnRegistration xmPushActionUnRegistration) {
        BitSet bitSet = new BitSet(2);
        this.__isset_bit_vector = bitSet;
        bitSet.clear();
        this.__isset_bit_vector.or(xmPushActionUnRegistration.__isset_bit_vector);
        if (xmPushActionUnRegistration.isSetDebug()) {
            this.debug = xmPushActionUnRegistration.debug;
        }
        if (xmPushActionUnRegistration.isSetTarget()) {
            this.target = new Target(xmPushActionUnRegistration.target);
        }
        if (xmPushActionUnRegistration.isSetId()) {
            this.id = xmPushActionUnRegistration.id;
        }
        if (xmPushActionUnRegistration.isSetAppId()) {
            this.appId = xmPushActionUnRegistration.appId;
        }
        if (xmPushActionUnRegistration.isSetRegId()) {
            this.regId = xmPushActionUnRegistration.regId;
        }
        if (xmPushActionUnRegistration.isSetAppVersion()) {
            this.appVersion = xmPushActionUnRegistration.appVersion;
        }
        if (xmPushActionUnRegistration.isSetPackageName()) {
            this.packageName = xmPushActionUnRegistration.packageName;
        }
        if (xmPushActionUnRegistration.isSetToken()) {
            this.token = xmPushActionUnRegistration.token;
        }
        if (xmPushActionUnRegistration.isSetDeviceId()) {
            this.deviceId = xmPushActionUnRegistration.deviceId;
        }
        if (xmPushActionUnRegistration.isSetAliasName()) {
            this.aliasName = xmPushActionUnRegistration.aliasName;
        }
        this.needAck = xmPushActionUnRegistration.needAck;
        this.createdTs = xmPushActionUnRegistration.createdTs;
    }
}
