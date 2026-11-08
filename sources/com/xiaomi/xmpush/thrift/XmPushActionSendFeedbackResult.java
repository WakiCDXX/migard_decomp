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
public class XmPushActionSendFeedbackResult implements TBase<XmPushActionSendFeedbackResult, Object>, Serializable, Cloneable {
    private static final int __ERRORCODE_ISSET_ID = 0;
    private BitSet __isset_bit_vector;
    public String appId;
    public String category;
    public String debug;
    public long errorCode;
    public String id;
    public String reason;
    public Target target;
    private static final g STRUCT_DESC = new g("XmPushActionSendFeedbackResult");
    private static final a DEBUG_FIELD_DESC = new a("", (byte) 11, 1);
    private static final a TARGET_FIELD_DESC = new a("", (byte) 12, 2);
    private static final a ID_FIELD_DESC = new a("", (byte) 11, 3);
    private static final a APP_ID_FIELD_DESC = new a("", (byte) 11, 4);
    private static final a ERROR_CODE_FIELD_DESC = new a("", (byte) 10, 6);
    private static final a REASON_FIELD_DESC = new a("", (byte) 11, 7);
    private static final a CATEGORY_FIELD_DESC = new a("", (byte) 11, 8);

    public XmPushActionSendFeedbackResult() {
        this.__isset_bit_vector = new BitSet(1);
    }

    public void clear() {
        this.debug = null;
        this.target = null;
        this.id = null;
        this.appId = null;
        setErrorCodeIsSet(false);
        this.errorCode = 0L;
        this.reason = null;
        this.category = null;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof XmPushActionSendFeedbackResult)) {
            return equals((XmPushActionSendFeedbackResult) obj);
        }
        return false;
    }

    public String getAppId() {
        return this.appId;
    }

    public String getCategory() {
        return this.category;
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

    public String getReason() {
        return this.reason;
    }

    public Target getTarget() {
        return this.target;
    }

    public int hashCode() {
        return 0;
    }

    public boolean isSetAppId() {
        return this.appId != null;
    }

    public boolean isSetCategory() {
        return this.category != null;
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

    public boolean isSetReason() {
        return this.reason != null;
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
                        this.category = dVar.p();
                    }
                    break;
            }
            dVar.f();
        }
    }

    public XmPushActionSendFeedbackResult setAppId(String str) {
        this.appId = str;
        return this;
    }

    public void setAppIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.appId = null;
    }

    public XmPushActionSendFeedbackResult setCategory(String str) {
        this.category = str;
        return this;
    }

    public void setCategoryIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.category = null;
    }

    public XmPushActionSendFeedbackResult setDebug(String str) {
        this.debug = str;
        return this;
    }

    public void setDebugIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.debug = null;
    }

    public XmPushActionSendFeedbackResult setErrorCode(long j2) {
        this.errorCode = j2;
        setErrorCodeIsSet(true);
        return this;
    }

    public void setErrorCodeIsSet(boolean z2) {
        this.__isset_bit_vector.set(0, z2);
    }

    public XmPushActionSendFeedbackResult setId(String str) {
        this.id = str;
        return this;
    }

    public void setIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.id = null;
    }

    public XmPushActionSendFeedbackResult setReason(String str) {
        this.reason = str;
        return this;
    }

    public void setReasonIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.reason = null;
    }

    public XmPushActionSendFeedbackResult setTarget(Target target) {
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
        StringBuilder sb = new StringBuilder("XmPushActionSendFeedbackResult(");
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
        if (isSetCategory()) {
            sb.append(", ");
            sb.append("category:");
            String str5 = this.category;
            if (str5 == null) {
                sb.append("null");
            } else {
                sb.append(str5);
            }
        }
        sb.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
        return sb.toString();
    }

    public void unsetAppId() {
        this.appId = null;
    }

    public void unsetCategory() {
        this.category = null;
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

    public void unsetReason() {
        this.reason = null;
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
        if (this.category != null && isSetCategory()) {
            dVar.w(CATEGORY_FIELD_DESC);
            dVar.I(this.category);
            dVar.x();
        }
        dVar.y();
        dVar.K();
    }

    @Override // java.lang.Comparable
    public int compareTo(XmPushActionSendFeedbackResult xmPushActionSendFeedbackResult) {
        int iG;
        int iG2;
        int iE;
        int iG3;
        int iG4;
        int iF;
        int iG5;
        if (!getClass().equals(xmPushActionSendFeedbackResult.getClass())) {
            return getClass().getName().compareTo(xmPushActionSendFeedbackResult.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetDebug()).compareTo(Boolean.valueOf(xmPushActionSendFeedbackResult.isSetDebug()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetDebug() && (iG5 = g0.a.g(this.debug, xmPushActionSendFeedbackResult.debug)) != 0) {
            return iG5;
        }
        int iCompareTo2 = Boolean.valueOf(isSetTarget()).compareTo(Boolean.valueOf(xmPushActionSendFeedbackResult.isSetTarget()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (isSetTarget() && (iF = g0.a.f(this.target, xmPushActionSendFeedbackResult.target)) != 0) {
            return iF;
        }
        int iCompareTo3 = Boolean.valueOf(isSetId()).compareTo(Boolean.valueOf(xmPushActionSendFeedbackResult.isSetId()));
        if (iCompareTo3 != 0) {
            return iCompareTo3;
        }
        if (isSetId() && (iG4 = g0.a.g(this.id, xmPushActionSendFeedbackResult.id)) != 0) {
            return iG4;
        }
        int iCompareTo4 = Boolean.valueOf(isSetAppId()).compareTo(Boolean.valueOf(xmPushActionSendFeedbackResult.isSetAppId()));
        if (iCompareTo4 != 0) {
            return iCompareTo4;
        }
        if (isSetAppId() && (iG3 = g0.a.g(this.appId, xmPushActionSendFeedbackResult.appId)) != 0) {
            return iG3;
        }
        int iCompareTo5 = Boolean.valueOf(isSetErrorCode()).compareTo(Boolean.valueOf(xmPushActionSendFeedbackResult.isSetErrorCode()));
        if (iCompareTo5 != 0) {
            return iCompareTo5;
        }
        if (isSetErrorCode() && (iE = g0.a.e(this.errorCode, xmPushActionSendFeedbackResult.errorCode)) != 0) {
            return iE;
        }
        int iCompareTo6 = Boolean.valueOf(isSetReason()).compareTo(Boolean.valueOf(xmPushActionSendFeedbackResult.isSetReason()));
        if (iCompareTo6 != 0) {
            return iCompareTo6;
        }
        if (isSetReason() && (iG2 = g0.a.g(this.reason, xmPushActionSendFeedbackResult.reason)) != 0) {
            return iG2;
        }
        int iCompareTo7 = Boolean.valueOf(isSetCategory()).compareTo(Boolean.valueOf(xmPushActionSendFeedbackResult.isSetCategory()));
        if (iCompareTo7 != 0) {
            return iCompareTo7;
        }
        if (!isSetCategory() || (iG = g0.a.g(this.category, xmPushActionSendFeedbackResult.category)) == 0) {
            return 0;
        }
        return iG;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public XmPushActionSendFeedbackResult m42deepCopy() {
        return new XmPushActionSendFeedbackResult(this);
    }

    public XmPushActionSendFeedbackResult(String str, String str2, long j2) {
        this();
        this.id = str;
        this.appId = str2;
        this.errorCode = j2;
        setErrorCodeIsSet(true);
    }

    public boolean equals(XmPushActionSendFeedbackResult xmPushActionSendFeedbackResult) {
        if (xmPushActionSendFeedbackResult == null) {
            return false;
        }
        boolean zIsSetDebug = isSetDebug();
        boolean zIsSetDebug2 = xmPushActionSendFeedbackResult.isSetDebug();
        if ((zIsSetDebug || zIsSetDebug2) && !(zIsSetDebug && zIsSetDebug2 && this.debug.equals(xmPushActionSendFeedbackResult.debug))) {
            return false;
        }
        boolean zIsSetTarget = isSetTarget();
        boolean zIsSetTarget2 = xmPushActionSendFeedbackResult.isSetTarget();
        if ((zIsSetTarget || zIsSetTarget2) && !(zIsSetTarget && zIsSetTarget2 && this.target.equals(xmPushActionSendFeedbackResult.target))) {
            return false;
        }
        boolean zIsSetId = isSetId();
        boolean zIsSetId2 = xmPushActionSendFeedbackResult.isSetId();
        if ((zIsSetId || zIsSetId2) && !(zIsSetId && zIsSetId2 && this.id.equals(xmPushActionSendFeedbackResult.id))) {
            return false;
        }
        boolean zIsSetAppId = isSetAppId();
        boolean zIsSetAppId2 = xmPushActionSendFeedbackResult.isSetAppId();
        if (((zIsSetAppId || zIsSetAppId2) && !(zIsSetAppId && zIsSetAppId2 && this.appId.equals(xmPushActionSendFeedbackResult.appId))) || this.errorCode != xmPushActionSendFeedbackResult.errorCode) {
            return false;
        }
        boolean zIsSetReason = isSetReason();
        boolean zIsSetReason2 = xmPushActionSendFeedbackResult.isSetReason();
        if ((zIsSetReason || zIsSetReason2) && !(zIsSetReason && zIsSetReason2 && this.reason.equals(xmPushActionSendFeedbackResult.reason))) {
            return false;
        }
        boolean zIsSetCategory = isSetCategory();
        boolean zIsSetCategory2 = xmPushActionSendFeedbackResult.isSetCategory();
        if (zIsSetCategory || zIsSetCategory2) {
            return zIsSetCategory && zIsSetCategory2 && this.category.equals(xmPushActionSendFeedbackResult.category);
        }
        return true;
    }

    public XmPushActionSendFeedbackResult(XmPushActionSendFeedbackResult xmPushActionSendFeedbackResult) {
        BitSet bitSet = new BitSet(1);
        this.__isset_bit_vector = bitSet;
        bitSet.clear();
        this.__isset_bit_vector.or(xmPushActionSendFeedbackResult.__isset_bit_vector);
        if (xmPushActionSendFeedbackResult.isSetDebug()) {
            this.debug = xmPushActionSendFeedbackResult.debug;
        }
        if (xmPushActionSendFeedbackResult.isSetTarget()) {
            this.target = new Target(xmPushActionSendFeedbackResult.target);
        }
        if (xmPushActionSendFeedbackResult.isSetId()) {
            this.id = xmPushActionSendFeedbackResult.id;
        }
        if (xmPushActionSendFeedbackResult.isSetAppId()) {
            this.appId = xmPushActionSendFeedbackResult.appId;
        }
        this.errorCode = xmPushActionSendFeedbackResult.errorCode;
        if (xmPushActionSendFeedbackResult.isSetReason()) {
            this.reason = xmPushActionSendFeedbackResult.reason;
        }
        if (xmPushActionSendFeedbackResult.isSetCategory()) {
            this.category = xmPushActionSendFeedbackResult.category;
        }
    }
}
