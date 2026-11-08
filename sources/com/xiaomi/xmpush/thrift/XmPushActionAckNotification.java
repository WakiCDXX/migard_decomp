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
public class XmPushActionAckNotification implements TBase<XmPushActionAckNotification, Object>, Serializable, Cloneable {
    private static final int __ERRORCODE_ISSET_ID = 0;
    private BitSet __isset_bit_vector;
    public String appId;
    public String category;
    public String debug;
    public long errorCode;
    public Map<String, String> extra;
    public String id;
    public String packageName;
    public String reason;
    public Target target;
    public String type;
    private static final g STRUCT_DESC = new g("XmPushActionAckNotification");
    private static final a DEBUG_FIELD_DESC = new a("", (byte) 11, 1);
    private static final a TARGET_FIELD_DESC = new a("", (byte) 12, 2);
    private static final a ID_FIELD_DESC = new a("", (byte) 11, 3);
    private static final a APP_ID_FIELD_DESC = new a("", (byte) 11, 4);
    private static final a TYPE_FIELD_DESC = new a("", (byte) 11, 5);
    private static final a ERROR_CODE_FIELD_DESC = new a("", (byte) 10, 7);
    private static final a REASON_FIELD_DESC = new a("", (byte) 11, 8);
    private static final a EXTRA_FIELD_DESC = new a("", (byte) 13, 9);
    private static final a PACKAGE_NAME_FIELD_DESC = new a("", (byte) 11, 10);
    private static final a CATEGORY_FIELD_DESC = new a("", (byte) 11, 11);

    public XmPushActionAckNotification() {
        this.__isset_bit_vector = new BitSet(1);
        this.errorCode = 0L;
    }

    public void clear() {
        this.debug = null;
        this.target = null;
        this.id = null;
        this.appId = null;
        this.type = null;
        this.errorCode = 0L;
        this.reason = null;
        this.extra = null;
        this.packageName = null;
        this.category = null;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof XmPushActionAckNotification)) {
            return equals((XmPushActionAckNotification) obj);
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

    public String getType() {
        return this.type;
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

    public boolean isSetExtra() {
        return this.extra != null;
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

    public boolean isSetType() {
        return this.type != null;
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
                validate();
                return;
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
                    if (b2 == 11) {
                        this.type = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 6:
                default:
                    e.a(dVar, b2);
                    break;
                case 7:
                    if (b2 == 10) {
                        this.errorCode = dVar.i();
                        setErrorCodeIsSet(true);
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 8:
                    if (b2 == 11) {
                        this.reason = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 9:
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
                case 10:
                    if (b2 == 11) {
                        this.packageName = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 11:
                    if (b2 == 11) {
                        this.category = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
            }
            dVar.f();
        }
    }

    public XmPushActionAckNotification setAppId(String str) {
        this.appId = str;
        return this;
    }

    public void setAppIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.appId = null;
    }

    public XmPushActionAckNotification setCategory(String str) {
        this.category = str;
        return this;
    }

    public void setCategoryIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.category = null;
    }

    public XmPushActionAckNotification setDebug(String str) {
        this.debug = str;
        return this;
    }

    public void setDebugIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.debug = null;
    }

    public XmPushActionAckNotification setErrorCode(long j2) {
        this.errorCode = j2;
        setErrorCodeIsSet(true);
        return this;
    }

    public void setErrorCodeIsSet(boolean z2) {
        this.__isset_bit_vector.set(0, z2);
    }

    public XmPushActionAckNotification setExtra(Map<String, String> map) {
        this.extra = map;
        return this;
    }

    public void setExtraIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.extra = null;
    }

    public XmPushActionAckNotification setId(String str) {
        this.id = str;
        return this;
    }

    public void setIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.id = null;
    }

    public XmPushActionAckNotification setPackageName(String str) {
        this.packageName = str;
        return this;
    }

    public void setPackageNameIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.packageName = null;
    }

    public XmPushActionAckNotification setReason(String str) {
        this.reason = str;
        return this;
    }

    public void setReasonIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.reason = null;
    }

    public XmPushActionAckNotification setTarget(Target target) {
        this.target = target;
        return this;
    }

    public void setTargetIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.target = null;
    }

    public XmPushActionAckNotification setType(String str) {
        this.type = str;
        return this;
    }

    public void setTypeIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.type = null;
    }

    public String toString() {
        boolean z2;
        StringBuilder sb = new StringBuilder("XmPushActionAckNotification(");
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
        if (isSetType()) {
            sb.append(", ");
            sb.append("type:");
            String str4 = this.type;
            if (str4 == null) {
                sb.append("null");
            } else {
                sb.append(str4);
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
            String str5 = this.reason;
            if (str5 == null) {
                sb.append("null");
            } else {
                sb.append(str5);
            }
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

    public void unsetExtra() {
        this.extra = null;
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

    public void unsetType() {
        this.type = null;
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
        if (this.type != null && isSetType()) {
            dVar.w(TYPE_FIELD_DESC);
            dVar.I(this.type);
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
    public int compareTo(XmPushActionAckNotification xmPushActionAckNotification) {
        int iG;
        int iG2;
        int i2;
        int iG3;
        int iE;
        int iG4;
        int iG5;
        int iG6;
        int iF;
        int iG7;
        if (!getClass().equals(xmPushActionAckNotification.getClass())) {
            return getClass().getName().compareTo(xmPushActionAckNotification.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetDebug()).compareTo(Boolean.valueOf(xmPushActionAckNotification.isSetDebug()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetDebug() && (iG7 = g0.a.g(this.debug, xmPushActionAckNotification.debug)) != 0) {
            return iG7;
        }
        int iCompareTo2 = Boolean.valueOf(isSetTarget()).compareTo(Boolean.valueOf(xmPushActionAckNotification.isSetTarget()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (isSetTarget() && (iF = g0.a.f(this.target, xmPushActionAckNotification.target)) != 0) {
            return iF;
        }
        int iCompareTo3 = Boolean.valueOf(isSetId()).compareTo(Boolean.valueOf(xmPushActionAckNotification.isSetId()));
        if (iCompareTo3 != 0) {
            return iCompareTo3;
        }
        if (isSetId() && (iG6 = g0.a.g(this.id, xmPushActionAckNotification.id)) != 0) {
            return iG6;
        }
        int iCompareTo4 = Boolean.valueOf(isSetAppId()).compareTo(Boolean.valueOf(xmPushActionAckNotification.isSetAppId()));
        if (iCompareTo4 != 0) {
            return iCompareTo4;
        }
        if (isSetAppId() && (iG5 = g0.a.g(this.appId, xmPushActionAckNotification.appId)) != 0) {
            return iG5;
        }
        int iCompareTo5 = Boolean.valueOf(isSetType()).compareTo(Boolean.valueOf(xmPushActionAckNotification.isSetType()));
        if (iCompareTo5 != 0) {
            return iCompareTo5;
        }
        if (isSetType() && (iG4 = g0.a.g(this.type, xmPushActionAckNotification.type)) != 0) {
            return iG4;
        }
        int iCompareTo6 = Boolean.valueOf(isSetErrorCode()).compareTo(Boolean.valueOf(xmPushActionAckNotification.isSetErrorCode()));
        if (iCompareTo6 != 0) {
            return iCompareTo6;
        }
        if (isSetErrorCode() && (iE = g0.a.e(this.errorCode, xmPushActionAckNotification.errorCode)) != 0) {
            return iE;
        }
        int iCompareTo7 = Boolean.valueOf(isSetReason()).compareTo(Boolean.valueOf(xmPushActionAckNotification.isSetReason()));
        if (iCompareTo7 != 0) {
            return iCompareTo7;
        }
        if (isSetReason() && (iG3 = g0.a.g(this.reason, xmPushActionAckNotification.reason)) != 0) {
            return iG3;
        }
        int iCompareTo8 = Boolean.valueOf(isSetExtra()).compareTo(Boolean.valueOf(xmPushActionAckNotification.isSetExtra()));
        if (iCompareTo8 != 0) {
            return iCompareTo8;
        }
        if (isSetExtra() && (i2 = g0.a.i(this.extra, xmPushActionAckNotification.extra)) != 0) {
            return i2;
        }
        int iCompareTo9 = Boolean.valueOf(isSetPackageName()).compareTo(Boolean.valueOf(xmPushActionAckNotification.isSetPackageName()));
        if (iCompareTo9 != 0) {
            return iCompareTo9;
        }
        if (isSetPackageName() && (iG2 = g0.a.g(this.packageName, xmPushActionAckNotification.packageName)) != 0) {
            return iG2;
        }
        int iCompareTo10 = Boolean.valueOf(isSetCategory()).compareTo(Boolean.valueOf(xmPushActionAckNotification.isSetCategory()));
        if (iCompareTo10 != 0) {
            return iCompareTo10;
        }
        if (!isSetCategory() || (iG = g0.a.g(this.category, xmPushActionAckNotification.category)) == 0) {
            return 0;
        }
        return iG;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public XmPushActionAckNotification m29deepCopy() {
        return new XmPushActionAckNotification(this);
    }

    public boolean equals(XmPushActionAckNotification xmPushActionAckNotification) {
        if (xmPushActionAckNotification == null) {
            return false;
        }
        boolean zIsSetDebug = isSetDebug();
        boolean zIsSetDebug2 = xmPushActionAckNotification.isSetDebug();
        if ((zIsSetDebug || zIsSetDebug2) && !(zIsSetDebug && zIsSetDebug2 && this.debug.equals(xmPushActionAckNotification.debug))) {
            return false;
        }
        boolean zIsSetTarget = isSetTarget();
        boolean zIsSetTarget2 = xmPushActionAckNotification.isSetTarget();
        if ((zIsSetTarget || zIsSetTarget2) && !(zIsSetTarget && zIsSetTarget2 && this.target.equals(xmPushActionAckNotification.target))) {
            return false;
        }
        boolean zIsSetId = isSetId();
        boolean zIsSetId2 = xmPushActionAckNotification.isSetId();
        if ((zIsSetId || zIsSetId2) && !(zIsSetId && zIsSetId2 && this.id.equals(xmPushActionAckNotification.id))) {
            return false;
        }
        boolean zIsSetAppId = isSetAppId();
        boolean zIsSetAppId2 = xmPushActionAckNotification.isSetAppId();
        if ((zIsSetAppId || zIsSetAppId2) && !(zIsSetAppId && zIsSetAppId2 && this.appId.equals(xmPushActionAckNotification.appId))) {
            return false;
        }
        boolean zIsSetType = isSetType();
        boolean zIsSetType2 = xmPushActionAckNotification.isSetType();
        if ((zIsSetType || zIsSetType2) && !(zIsSetType && zIsSetType2 && this.type.equals(xmPushActionAckNotification.type))) {
            return false;
        }
        boolean zIsSetErrorCode = isSetErrorCode();
        boolean zIsSetErrorCode2 = xmPushActionAckNotification.isSetErrorCode();
        if ((zIsSetErrorCode || zIsSetErrorCode2) && !(zIsSetErrorCode && zIsSetErrorCode2 && this.errorCode == xmPushActionAckNotification.errorCode)) {
            return false;
        }
        boolean zIsSetReason = isSetReason();
        boolean zIsSetReason2 = xmPushActionAckNotification.isSetReason();
        if ((zIsSetReason || zIsSetReason2) && !(zIsSetReason && zIsSetReason2 && this.reason.equals(xmPushActionAckNotification.reason))) {
            return false;
        }
        boolean zIsSetExtra = isSetExtra();
        boolean zIsSetExtra2 = xmPushActionAckNotification.isSetExtra();
        if ((zIsSetExtra || zIsSetExtra2) && !(zIsSetExtra && zIsSetExtra2 && this.extra.equals(xmPushActionAckNotification.extra))) {
            return false;
        }
        boolean zIsSetPackageName = isSetPackageName();
        boolean zIsSetPackageName2 = xmPushActionAckNotification.isSetPackageName();
        if ((zIsSetPackageName || zIsSetPackageName2) && !(zIsSetPackageName && zIsSetPackageName2 && this.packageName.equals(xmPushActionAckNotification.packageName))) {
            return false;
        }
        boolean zIsSetCategory = isSetCategory();
        boolean zIsSetCategory2 = xmPushActionAckNotification.isSetCategory();
        if (zIsSetCategory || zIsSetCategory2) {
            return zIsSetCategory && zIsSetCategory2 && this.category.equals(xmPushActionAckNotification.category);
        }
        return true;
    }

    public XmPushActionAckNotification(String str) {
        this();
        this.id = str;
    }

    public XmPushActionAckNotification(XmPushActionAckNotification xmPushActionAckNotification) {
        BitSet bitSet = new BitSet(1);
        this.__isset_bit_vector = bitSet;
        bitSet.clear();
        this.__isset_bit_vector.or(xmPushActionAckNotification.__isset_bit_vector);
        if (xmPushActionAckNotification.isSetDebug()) {
            this.debug = xmPushActionAckNotification.debug;
        }
        if (xmPushActionAckNotification.isSetTarget()) {
            this.target = new Target(xmPushActionAckNotification.target);
        }
        if (xmPushActionAckNotification.isSetId()) {
            this.id = xmPushActionAckNotification.id;
        }
        if (xmPushActionAckNotification.isSetAppId()) {
            this.appId = xmPushActionAckNotification.appId;
        }
        if (xmPushActionAckNotification.isSetType()) {
            this.type = xmPushActionAckNotification.type;
        }
        this.errorCode = xmPushActionAckNotification.errorCode;
        if (xmPushActionAckNotification.isSetReason()) {
            this.reason = xmPushActionAckNotification.reason;
        }
        if (xmPushActionAckNotification.isSetExtra()) {
            HashMap map = new HashMap();
            for (Map.Entry<String, String> entry : xmPushActionAckNotification.extra.entrySet()) {
                map.put(entry.getKey(), entry.getValue());
            }
            this.extra = map;
        }
        if (xmPushActionAckNotification.isSetPackageName()) {
            this.packageName = xmPushActionAckNotification.packageName;
        }
        if (xmPushActionAckNotification.isSetCategory()) {
            this.category = xmPushActionAckNotification.category;
        }
    }
}
