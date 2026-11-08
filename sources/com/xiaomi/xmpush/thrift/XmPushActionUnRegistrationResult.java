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
public class XmPushActionUnRegistrationResult implements TBase<XmPushActionUnRegistrationResult, Object>, Serializable, Cloneable {
    private static final int __COSTTIME_ISSET_ID = 2;
    private static final int __ERRORCODE_ISSET_ID = 0;
    private static final int __UNREGISTEREDAT_ISSET_ID = 1;
    private BitSet __isset_bit_vector;
    public String appId;
    public long costTime;
    public String debug;
    public long errorCode;
    public String id;
    public String packageName;
    public String reason;
    public Target target;
    public long unRegisteredAt;
    private static final g STRUCT_DESC = new g("XmPushActionUnRegistrationResult");
    private static final a DEBUG_FIELD_DESC = new a("", (byte) 11, 1);
    private static final a TARGET_FIELD_DESC = new a("", (byte) 12, 2);
    private static final a ID_FIELD_DESC = new a("", (byte) 11, 3);
    private static final a APP_ID_FIELD_DESC = new a("", (byte) 11, 4);
    private static final a ERROR_CODE_FIELD_DESC = new a("", (byte) 10, 6);
    private static final a REASON_FIELD_DESC = new a("", (byte) 11, 7);
    private static final a PACKAGE_NAME_FIELD_DESC = new a("", (byte) 11, 8);
    private static final a UN_REGISTERED_AT_FIELD_DESC = new a("", (byte) 10, 9);
    private static final a COST_TIME_FIELD_DESC = new a("", (byte) 10, 10);

    public XmPushActionUnRegistrationResult() {
        this.__isset_bit_vector = new BitSet(3);
    }

    public void clear() {
        this.debug = null;
        this.target = null;
        this.id = null;
        this.appId = null;
        setErrorCodeIsSet(false);
        this.errorCode = 0L;
        this.reason = null;
        this.packageName = null;
        setUnRegisteredAtIsSet(false);
        this.unRegisteredAt = 0L;
        setCostTimeIsSet(false);
        this.costTime = 0L;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof XmPushActionUnRegistrationResult)) {
            return equals((XmPushActionUnRegistrationResult) obj);
        }
        return false;
    }

    public String getAppId() {
        return this.appId;
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

    public String getId() {
        return this.id;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getReason() {
        return this.reason;
    }

    public Target getTarget() {
        return this.target;
    }

    public long getUnRegisteredAt() {
        return this.unRegisteredAt;
    }

    public int hashCode() {
        return 0;
    }

    public boolean isSetAppId() {
        return this.appId != null;
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

    public boolean isSetId() {
        return this.id != null;
    }

    public boolean isSetPackageName() {
        return this.packageName != null;
    }

    public boolean isSetReason() {
        return this.reason != null;
    }

    public boolean isSetTarget() {
        return this.target != null;
    }

    public boolean isSetUnRegisteredAt() {
        return this.__isset_bit_vector.get(1);
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
                default:
                    e.a(dVar, b2);
                    break;
                case 6:
                    if (b2 != 10) {
                        e.a(dVar, b2);
                    } else {
                        this.errorCode = dVar.i();
                        setErrorCodeIsSet(true);
                    }
                    break;
                case 7:
                    if (b2 != 11) {
                        e.a(dVar, b2);
                    } else {
                        this.reason = dVar.p();
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
                    if (b2 != 10) {
                        e.a(dVar, b2);
                    } else {
                        this.unRegisteredAt = dVar.i();
                        setUnRegisteredAtIsSet(true);
                    }
                    break;
                case 10:
                    if (b2 != 10) {
                        e.a(dVar, b2);
                    } else {
                        this.costTime = dVar.i();
                        setCostTimeIsSet(true);
                    }
                    break;
            }
            dVar.f();
        }
    }

    public XmPushActionUnRegistrationResult setAppId(String str) {
        this.appId = str;
        return this;
    }

    public void setAppIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.appId = null;
    }

    public XmPushActionUnRegistrationResult setCostTime(long j2) {
        this.costTime = j2;
        setCostTimeIsSet(true);
        return this;
    }

    public void setCostTimeIsSet(boolean z2) {
        this.__isset_bit_vector.set(2, z2);
    }

    public XmPushActionUnRegistrationResult setDebug(String str) {
        this.debug = str;
        return this;
    }

    public void setDebugIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.debug = null;
    }

    public XmPushActionUnRegistrationResult setErrorCode(long j2) {
        this.errorCode = j2;
        setErrorCodeIsSet(true);
        return this;
    }

    public void setErrorCodeIsSet(boolean z2) {
        this.__isset_bit_vector.set(0, z2);
    }

    public XmPushActionUnRegistrationResult setId(String str) {
        this.id = str;
        return this;
    }

    public void setIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.id = null;
    }

    public XmPushActionUnRegistrationResult setPackageName(String str) {
        this.packageName = str;
        return this;
    }

    public void setPackageNameIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.packageName = null;
    }

    public XmPushActionUnRegistrationResult setReason(String str) {
        this.reason = str;
        return this;
    }

    public void setReasonIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.reason = null;
    }

    public XmPushActionUnRegistrationResult setTarget(Target target) {
        this.target = target;
        return this;
    }

    public void setTargetIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.target = null;
    }

    public XmPushActionUnRegistrationResult setUnRegisteredAt(long j2) {
        this.unRegisteredAt = j2;
        setUnRegisteredAtIsSet(true);
        return this;
    }

    public void setUnRegisteredAtIsSet(boolean z2) {
        this.__isset_bit_vector.set(1, z2);
    }

    public String toString() {
        boolean z2;
        StringBuilder sb = new StringBuilder("XmPushActionUnRegistrationResult(");
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
        if (isSetUnRegisteredAt()) {
            sb.append(", ");
            sb.append("unRegisteredAt:");
            sb.append(this.unRegisteredAt);
        }
        if (isSetCostTime()) {
            sb.append(", ");
            sb.append("costTime:");
            sb.append(this.costTime);
        }
        sb.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
        return sb.toString();
    }

    public void unsetAppId() {
        this.appId = null;
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

    public void unsetId() {
        this.id = null;
    }

    public void unsetPackageName() {
        this.packageName = null;
    }

    public void unsetReason() {
        this.reason = null;
    }

    public void unsetTarget() {
        this.target = null;
    }

    public void unsetUnRegisteredAt() {
        this.__isset_bit_vector.clear(1);
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
        if (this.packageName != null && isSetPackageName()) {
            dVar.w(PACKAGE_NAME_FIELD_DESC);
            dVar.I(this.packageName);
            dVar.x();
        }
        if (isSetUnRegisteredAt()) {
            dVar.w(UN_REGISTERED_AT_FIELD_DESC);
            dVar.B(this.unRegisteredAt);
            dVar.x();
        }
        if (isSetCostTime()) {
            dVar.w(COST_TIME_FIELD_DESC);
            dVar.B(this.costTime);
            dVar.x();
        }
        dVar.y();
        dVar.K();
    }

    @Override // java.lang.Comparable
    public int compareTo(XmPushActionUnRegistrationResult xmPushActionUnRegistrationResult) {
        int iE;
        int iE2;
        int iG;
        int iG2;
        int iE3;
        int iG3;
        int iG4;
        int iF;
        int iG5;
        if (!getClass().equals(xmPushActionUnRegistrationResult.getClass())) {
            return getClass().getName().compareTo(xmPushActionUnRegistrationResult.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetDebug()).compareTo(Boolean.valueOf(xmPushActionUnRegistrationResult.isSetDebug()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetDebug() && (iG5 = g0.a.g(this.debug, xmPushActionUnRegistrationResult.debug)) != 0) {
            return iG5;
        }
        int iCompareTo2 = Boolean.valueOf(isSetTarget()).compareTo(Boolean.valueOf(xmPushActionUnRegistrationResult.isSetTarget()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (isSetTarget() && (iF = g0.a.f(this.target, xmPushActionUnRegistrationResult.target)) != 0) {
            return iF;
        }
        int iCompareTo3 = Boolean.valueOf(isSetId()).compareTo(Boolean.valueOf(xmPushActionUnRegistrationResult.isSetId()));
        if (iCompareTo3 != 0) {
            return iCompareTo3;
        }
        if (isSetId() && (iG4 = g0.a.g(this.id, xmPushActionUnRegistrationResult.id)) != 0) {
            return iG4;
        }
        int iCompareTo4 = Boolean.valueOf(isSetAppId()).compareTo(Boolean.valueOf(xmPushActionUnRegistrationResult.isSetAppId()));
        if (iCompareTo4 != 0) {
            return iCompareTo4;
        }
        if (isSetAppId() && (iG3 = g0.a.g(this.appId, xmPushActionUnRegistrationResult.appId)) != 0) {
            return iG3;
        }
        int iCompareTo5 = Boolean.valueOf(isSetErrorCode()).compareTo(Boolean.valueOf(xmPushActionUnRegistrationResult.isSetErrorCode()));
        if (iCompareTo5 != 0) {
            return iCompareTo5;
        }
        if (isSetErrorCode() && (iE3 = g0.a.e(this.errorCode, xmPushActionUnRegistrationResult.errorCode)) != 0) {
            return iE3;
        }
        int iCompareTo6 = Boolean.valueOf(isSetReason()).compareTo(Boolean.valueOf(xmPushActionUnRegistrationResult.isSetReason()));
        if (iCompareTo6 != 0) {
            return iCompareTo6;
        }
        if (isSetReason() && (iG2 = g0.a.g(this.reason, xmPushActionUnRegistrationResult.reason)) != 0) {
            return iG2;
        }
        int iCompareTo7 = Boolean.valueOf(isSetPackageName()).compareTo(Boolean.valueOf(xmPushActionUnRegistrationResult.isSetPackageName()));
        if (iCompareTo7 != 0) {
            return iCompareTo7;
        }
        if (isSetPackageName() && (iG = g0.a.g(this.packageName, xmPushActionUnRegistrationResult.packageName)) != 0) {
            return iG;
        }
        int iCompareTo8 = Boolean.valueOf(isSetUnRegisteredAt()).compareTo(Boolean.valueOf(xmPushActionUnRegistrationResult.isSetUnRegisteredAt()));
        if (iCompareTo8 != 0) {
            return iCompareTo8;
        }
        if (isSetUnRegisteredAt() && (iE2 = g0.a.e(this.unRegisteredAt, xmPushActionUnRegistrationResult.unRegisteredAt)) != 0) {
            return iE2;
        }
        int iCompareTo9 = Boolean.valueOf(isSetCostTime()).compareTo(Boolean.valueOf(xmPushActionUnRegistrationResult.isSetCostTime()));
        if (iCompareTo9 != 0) {
            return iCompareTo9;
        }
        if (!isSetCostTime() || (iE = g0.a.e(this.costTime, xmPushActionUnRegistrationResult.costTime)) == 0) {
            return 0;
        }
        return iE;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public XmPushActionUnRegistrationResult m47deepCopy() {
        return new XmPushActionUnRegistrationResult(this);
    }

    public XmPushActionUnRegistrationResult(String str, String str2, long j2) {
        this();
        this.id = str;
        this.appId = str2;
        this.errorCode = j2;
        setErrorCodeIsSet(true);
    }

    public boolean equals(XmPushActionUnRegistrationResult xmPushActionUnRegistrationResult) {
        if (xmPushActionUnRegistrationResult == null) {
            return false;
        }
        boolean zIsSetDebug = isSetDebug();
        boolean zIsSetDebug2 = xmPushActionUnRegistrationResult.isSetDebug();
        if ((zIsSetDebug || zIsSetDebug2) && !(zIsSetDebug && zIsSetDebug2 && this.debug.equals(xmPushActionUnRegistrationResult.debug))) {
            return false;
        }
        boolean zIsSetTarget = isSetTarget();
        boolean zIsSetTarget2 = xmPushActionUnRegistrationResult.isSetTarget();
        if ((zIsSetTarget || zIsSetTarget2) && !(zIsSetTarget && zIsSetTarget2 && this.target.equals(xmPushActionUnRegistrationResult.target))) {
            return false;
        }
        boolean zIsSetId = isSetId();
        boolean zIsSetId2 = xmPushActionUnRegistrationResult.isSetId();
        if ((zIsSetId || zIsSetId2) && !(zIsSetId && zIsSetId2 && this.id.equals(xmPushActionUnRegistrationResult.id))) {
            return false;
        }
        boolean zIsSetAppId = isSetAppId();
        boolean zIsSetAppId2 = xmPushActionUnRegistrationResult.isSetAppId();
        if (((zIsSetAppId || zIsSetAppId2) && !(zIsSetAppId && zIsSetAppId2 && this.appId.equals(xmPushActionUnRegistrationResult.appId))) || this.errorCode != xmPushActionUnRegistrationResult.errorCode) {
            return false;
        }
        boolean zIsSetReason = isSetReason();
        boolean zIsSetReason2 = xmPushActionUnRegistrationResult.isSetReason();
        if ((zIsSetReason || zIsSetReason2) && !(zIsSetReason && zIsSetReason2 && this.reason.equals(xmPushActionUnRegistrationResult.reason))) {
            return false;
        }
        boolean zIsSetPackageName = isSetPackageName();
        boolean zIsSetPackageName2 = xmPushActionUnRegistrationResult.isSetPackageName();
        if ((zIsSetPackageName || zIsSetPackageName2) && !(zIsSetPackageName && zIsSetPackageName2 && this.packageName.equals(xmPushActionUnRegistrationResult.packageName))) {
            return false;
        }
        boolean zIsSetUnRegisteredAt = isSetUnRegisteredAt();
        boolean zIsSetUnRegisteredAt2 = xmPushActionUnRegistrationResult.isSetUnRegisteredAt();
        if ((zIsSetUnRegisteredAt || zIsSetUnRegisteredAt2) && !(zIsSetUnRegisteredAt && zIsSetUnRegisteredAt2 && this.unRegisteredAt == xmPushActionUnRegistrationResult.unRegisteredAt)) {
            return false;
        }
        boolean zIsSetCostTime = isSetCostTime();
        boolean zIsSetCostTime2 = xmPushActionUnRegistrationResult.isSetCostTime();
        if (zIsSetCostTime || zIsSetCostTime2) {
            return zIsSetCostTime && zIsSetCostTime2 && this.costTime == xmPushActionUnRegistrationResult.costTime;
        }
        return true;
    }

    public XmPushActionUnRegistrationResult(XmPushActionUnRegistrationResult xmPushActionUnRegistrationResult) {
        BitSet bitSet = new BitSet(3);
        this.__isset_bit_vector = bitSet;
        bitSet.clear();
        this.__isset_bit_vector.or(xmPushActionUnRegistrationResult.__isset_bit_vector);
        if (xmPushActionUnRegistrationResult.isSetDebug()) {
            this.debug = xmPushActionUnRegistrationResult.debug;
        }
        if (xmPushActionUnRegistrationResult.isSetTarget()) {
            this.target = new Target(xmPushActionUnRegistrationResult.target);
        }
        if (xmPushActionUnRegistrationResult.isSetId()) {
            this.id = xmPushActionUnRegistrationResult.id;
        }
        if (xmPushActionUnRegistrationResult.isSetAppId()) {
            this.appId = xmPushActionUnRegistrationResult.appId;
        }
        this.errorCode = xmPushActionUnRegistrationResult.errorCode;
        if (xmPushActionUnRegistrationResult.isSetReason()) {
            this.reason = xmPushActionUnRegistrationResult.reason;
        }
        if (xmPushActionUnRegistrationResult.isSetPackageName()) {
            this.packageName = xmPushActionUnRegistrationResult.packageName;
        }
        this.unRegisteredAt = xmPushActionUnRegistrationResult.unRegisteredAt;
        this.costTime = xmPushActionUnRegistrationResult.costTime;
    }
}
