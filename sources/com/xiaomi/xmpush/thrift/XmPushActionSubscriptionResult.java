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
public class XmPushActionSubscriptionResult implements TBase<XmPushActionSubscriptionResult, Object>, Serializable, Cloneable {
    private static final int __ERRORCODE_ISSET_ID = 0;
    private BitSet __isset_bit_vector;
    public String appId;
    public String category;
    public String debug;
    public long errorCode;
    public String id;
    public String packageName;
    public String reason;
    public Target target;
    public String topic;
    private static final g STRUCT_DESC = new g("XmPushActionSubscriptionResult");
    private static final a DEBUG_FIELD_DESC = new a("", (byte) 11, 1);
    private static final a TARGET_FIELD_DESC = new a("", (byte) 12, 2);
    private static final a ID_FIELD_DESC = new a("", (byte) 11, 3);
    private static final a APP_ID_FIELD_DESC = new a("", (byte) 11, 4);
    private static final a ERROR_CODE_FIELD_DESC = new a("", (byte) 10, 6);
    private static final a REASON_FIELD_DESC = new a("", (byte) 11, 7);
    private static final a TOPIC_FIELD_DESC = new a("", (byte) 11, 8);
    private static final a PACKAGE_NAME_FIELD_DESC = new a("", (byte) 11, 9);
    private static final a CATEGORY_FIELD_DESC = new a("", (byte) 11, 10);

    public XmPushActionSubscriptionResult() {
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
        this.topic = null;
        this.packageName = null;
        this.category = null;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof XmPushActionSubscriptionResult)) {
            return equals((XmPushActionSubscriptionResult) obj);
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

    public String getPackageName() {
        return this.packageName;
    }

    public String getReason() {
        return this.reason;
    }

    public Target getTarget() {
        return this.target;
    }

    public String getTopic() {
        return this.topic;
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

    public boolean isSetPackageName() {
        return this.packageName != null;
    }

    public boolean isSetReason() {
        return this.reason != null;
    }

    public boolean isSetTarget() {
        return this.target != null;
    }

    public boolean isSetTopic() {
        return this.topic != null;
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
                        this.topic = dVar.p();
                    }
                    break;
                case 9:
                    if (b2 != 11) {
                        e.a(dVar, b2);
                    } else {
                        this.packageName = dVar.p();
                    }
                    break;
                case 10:
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

    public XmPushActionSubscriptionResult setAppId(String str) {
        this.appId = str;
        return this;
    }

    public void setAppIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.appId = null;
    }

    public XmPushActionSubscriptionResult setCategory(String str) {
        this.category = str;
        return this;
    }

    public void setCategoryIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.category = null;
    }

    public XmPushActionSubscriptionResult setDebug(String str) {
        this.debug = str;
        return this;
    }

    public void setDebugIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.debug = null;
    }

    public XmPushActionSubscriptionResult setErrorCode(long j2) {
        this.errorCode = j2;
        setErrorCodeIsSet(true);
        return this;
    }

    public void setErrorCodeIsSet(boolean z2) {
        this.__isset_bit_vector.set(0, z2);
    }

    public XmPushActionSubscriptionResult setId(String str) {
        this.id = str;
        return this;
    }

    public void setIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.id = null;
    }

    public XmPushActionSubscriptionResult setPackageName(String str) {
        this.packageName = str;
        return this;
    }

    public void setPackageNameIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.packageName = null;
    }

    public XmPushActionSubscriptionResult setReason(String str) {
        this.reason = str;
        return this;
    }

    public void setReasonIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.reason = null;
    }

    public XmPushActionSubscriptionResult setTarget(Target target) {
        this.target = target;
        return this;
    }

    public void setTargetIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.target = null;
    }

    public XmPushActionSubscriptionResult setTopic(String str) {
        this.topic = str;
        return this;
    }

    public void setTopicIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.topic = null;
    }

    public String toString() {
        boolean z2;
        StringBuilder sb = new StringBuilder("XmPushActionSubscriptionResult(");
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
        if (isSetAppId()) {
            sb.append(", ");
            sb.append("appId:");
            String str3 = this.appId;
            if (str3 == null) {
                sb.append("null");
            } else {
                sb.append(str3);
            }
        }
        if (isSetErrorCode()) {
            sb.append(", ");
            sb.append("errorCode:");
            sb.append(this.errorCode);
        }
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
        if (isSetTopic()) {
            sb.append(", ");
            sb.append("topic:");
            String str5 = this.topic;
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

    public void unsetPackageName() {
        this.packageName = null;
    }

    public void unsetReason() {
        this.reason = null;
    }

    public void unsetTarget() {
        this.target = null;
    }

    public void unsetTopic() {
        this.topic = null;
    }

    public void validate() throws TException {
        if (this.id != null) {
            return;
        }
        throw new TProtocolException("Required field 'id' was not present! Struct: " + toString());
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
        if (this.appId != null && isSetAppId()) {
            dVar.w(APP_ID_FIELD_DESC);
            dVar.I(this.appId);
            dVar.x();
        }
        if (isSetErrorCode()) {
            dVar.w(ERROR_CODE_FIELD_DESC);
            dVar.B(this.errorCode);
            dVar.x();
        }
        if (this.reason != null && isSetReason()) {
            dVar.w(REASON_FIELD_DESC);
            dVar.I(this.reason);
            dVar.x();
        }
        if (this.topic != null && isSetTopic()) {
            dVar.w(TOPIC_FIELD_DESC);
            dVar.I(this.topic);
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
        dVar.y();
        dVar.K();
    }

    @Override // java.lang.Comparable
    public int compareTo(XmPushActionSubscriptionResult xmPushActionSubscriptionResult) {
        int iG;
        int iG2;
        int iG3;
        int iG4;
        int iE;
        int iG5;
        int iG6;
        int iF;
        int iG7;
        if (!getClass().equals(xmPushActionSubscriptionResult.getClass())) {
            return getClass().getName().compareTo(xmPushActionSubscriptionResult.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetDebug()).compareTo(Boolean.valueOf(xmPushActionSubscriptionResult.isSetDebug()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetDebug() && (iG7 = g0.a.g(this.debug, xmPushActionSubscriptionResult.debug)) != 0) {
            return iG7;
        }
        int iCompareTo2 = Boolean.valueOf(isSetTarget()).compareTo(Boolean.valueOf(xmPushActionSubscriptionResult.isSetTarget()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (isSetTarget() && (iF = g0.a.f(this.target, xmPushActionSubscriptionResult.target)) != 0) {
            return iF;
        }
        int iCompareTo3 = Boolean.valueOf(isSetId()).compareTo(Boolean.valueOf(xmPushActionSubscriptionResult.isSetId()));
        if (iCompareTo3 != 0) {
            return iCompareTo3;
        }
        if (isSetId() && (iG6 = g0.a.g(this.id, xmPushActionSubscriptionResult.id)) != 0) {
            return iG6;
        }
        int iCompareTo4 = Boolean.valueOf(isSetAppId()).compareTo(Boolean.valueOf(xmPushActionSubscriptionResult.isSetAppId()));
        if (iCompareTo4 != 0) {
            return iCompareTo4;
        }
        if (isSetAppId() && (iG5 = g0.a.g(this.appId, xmPushActionSubscriptionResult.appId)) != 0) {
            return iG5;
        }
        int iCompareTo5 = Boolean.valueOf(isSetErrorCode()).compareTo(Boolean.valueOf(xmPushActionSubscriptionResult.isSetErrorCode()));
        if (iCompareTo5 != 0) {
            return iCompareTo5;
        }
        if (isSetErrorCode() && (iE = g0.a.e(this.errorCode, xmPushActionSubscriptionResult.errorCode)) != 0) {
            return iE;
        }
        int iCompareTo6 = Boolean.valueOf(isSetReason()).compareTo(Boolean.valueOf(xmPushActionSubscriptionResult.isSetReason()));
        if (iCompareTo6 != 0) {
            return iCompareTo6;
        }
        if (isSetReason() && (iG4 = g0.a.g(this.reason, xmPushActionSubscriptionResult.reason)) != 0) {
            return iG4;
        }
        int iCompareTo7 = Boolean.valueOf(isSetTopic()).compareTo(Boolean.valueOf(xmPushActionSubscriptionResult.isSetTopic()));
        if (iCompareTo7 != 0) {
            return iCompareTo7;
        }
        if (isSetTopic() && (iG3 = g0.a.g(this.topic, xmPushActionSubscriptionResult.topic)) != 0) {
            return iG3;
        }
        int iCompareTo8 = Boolean.valueOf(isSetPackageName()).compareTo(Boolean.valueOf(xmPushActionSubscriptionResult.isSetPackageName()));
        if (iCompareTo8 != 0) {
            return iCompareTo8;
        }
        if (isSetPackageName() && (iG2 = g0.a.g(this.packageName, xmPushActionSubscriptionResult.packageName)) != 0) {
            return iG2;
        }
        int iCompareTo9 = Boolean.valueOf(isSetCategory()).compareTo(Boolean.valueOf(xmPushActionSubscriptionResult.isSetCategory()));
        if (iCompareTo9 != 0) {
            return iCompareTo9;
        }
        if (!isSetCategory() || (iG = g0.a.g(this.category, xmPushActionSubscriptionResult.category)) == 0) {
            return 0;
        }
        return iG;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public XmPushActionSubscriptionResult m45deepCopy() {
        return new XmPushActionSubscriptionResult(this);
    }

    public XmPushActionSubscriptionResult(String str) {
        this();
        this.id = str;
    }

    public boolean equals(XmPushActionSubscriptionResult xmPushActionSubscriptionResult) {
        if (xmPushActionSubscriptionResult == null) {
            return false;
        }
        boolean zIsSetDebug = isSetDebug();
        boolean zIsSetDebug2 = xmPushActionSubscriptionResult.isSetDebug();
        if ((zIsSetDebug || zIsSetDebug2) && !(zIsSetDebug && zIsSetDebug2 && this.debug.equals(xmPushActionSubscriptionResult.debug))) {
            return false;
        }
        boolean zIsSetTarget = isSetTarget();
        boolean zIsSetTarget2 = xmPushActionSubscriptionResult.isSetTarget();
        if ((zIsSetTarget || zIsSetTarget2) && !(zIsSetTarget && zIsSetTarget2 && this.target.equals(xmPushActionSubscriptionResult.target))) {
            return false;
        }
        boolean zIsSetId = isSetId();
        boolean zIsSetId2 = xmPushActionSubscriptionResult.isSetId();
        if ((zIsSetId || zIsSetId2) && !(zIsSetId && zIsSetId2 && this.id.equals(xmPushActionSubscriptionResult.id))) {
            return false;
        }
        boolean zIsSetAppId = isSetAppId();
        boolean zIsSetAppId2 = xmPushActionSubscriptionResult.isSetAppId();
        if ((zIsSetAppId || zIsSetAppId2) && !(zIsSetAppId && zIsSetAppId2 && this.appId.equals(xmPushActionSubscriptionResult.appId))) {
            return false;
        }
        boolean zIsSetErrorCode = isSetErrorCode();
        boolean zIsSetErrorCode2 = xmPushActionSubscriptionResult.isSetErrorCode();
        if ((zIsSetErrorCode || zIsSetErrorCode2) && !(zIsSetErrorCode && zIsSetErrorCode2 && this.errorCode == xmPushActionSubscriptionResult.errorCode)) {
            return false;
        }
        boolean zIsSetReason = isSetReason();
        boolean zIsSetReason2 = xmPushActionSubscriptionResult.isSetReason();
        if ((zIsSetReason || zIsSetReason2) && !(zIsSetReason && zIsSetReason2 && this.reason.equals(xmPushActionSubscriptionResult.reason))) {
            return false;
        }
        boolean zIsSetTopic = isSetTopic();
        boolean zIsSetTopic2 = xmPushActionSubscriptionResult.isSetTopic();
        if ((zIsSetTopic || zIsSetTopic2) && !(zIsSetTopic && zIsSetTopic2 && this.topic.equals(xmPushActionSubscriptionResult.topic))) {
            return false;
        }
        boolean zIsSetPackageName = isSetPackageName();
        boolean zIsSetPackageName2 = xmPushActionSubscriptionResult.isSetPackageName();
        if ((zIsSetPackageName || zIsSetPackageName2) && !(zIsSetPackageName && zIsSetPackageName2 && this.packageName.equals(xmPushActionSubscriptionResult.packageName))) {
            return false;
        }
        boolean zIsSetCategory = isSetCategory();
        boolean zIsSetCategory2 = xmPushActionSubscriptionResult.isSetCategory();
        if (zIsSetCategory || zIsSetCategory2) {
            return zIsSetCategory && zIsSetCategory2 && this.category.equals(xmPushActionSubscriptionResult.category);
        }
        return true;
    }

    public XmPushActionSubscriptionResult(XmPushActionSubscriptionResult xmPushActionSubscriptionResult) {
        BitSet bitSet = new BitSet(1);
        this.__isset_bit_vector = bitSet;
        bitSet.clear();
        this.__isset_bit_vector.or(xmPushActionSubscriptionResult.__isset_bit_vector);
        if (xmPushActionSubscriptionResult.isSetDebug()) {
            this.debug = xmPushActionSubscriptionResult.debug;
        }
        if (xmPushActionSubscriptionResult.isSetTarget()) {
            this.target = new Target(xmPushActionSubscriptionResult.target);
        }
        if (xmPushActionSubscriptionResult.isSetId()) {
            this.id = xmPushActionSubscriptionResult.id;
        }
        if (xmPushActionSubscriptionResult.isSetAppId()) {
            this.appId = xmPushActionSubscriptionResult.appId;
        }
        this.errorCode = xmPushActionSubscriptionResult.errorCode;
        if (xmPushActionSubscriptionResult.isSetReason()) {
            this.reason = xmPushActionSubscriptionResult.reason;
        }
        if (xmPushActionSubscriptionResult.isSetTopic()) {
            this.topic = xmPushActionSubscriptionResult.topic;
        }
        if (xmPushActionSubscriptionResult.isSetPackageName()) {
            this.packageName = xmPushActionSubscriptionResult.packageName;
        }
        if (xmPushActionSubscriptionResult.isSetCategory()) {
            this.category = xmPushActionSubscriptionResult.category;
        }
    }
}
