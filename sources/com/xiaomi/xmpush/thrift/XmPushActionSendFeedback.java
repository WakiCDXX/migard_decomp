package com.xiaomi.xmpush.thrift;

import com.xiaomi.push.mpcd.Constants;
import java.io.Serializable;
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
public class XmPushActionSendFeedback implements TBase<XmPushActionSendFeedback, Object>, Serializable, Cloneable {
    public String appId;
    public String category;
    public String debug;
    public Map<String, String> feedbacks;
    public String id;
    public Target target;
    private static final g STRUCT_DESC = new g("XmPushActionSendFeedback");
    private static final a DEBUG_FIELD_DESC = new a("", (byte) 11, 1);
    private static final a TARGET_FIELD_DESC = new a("", (byte) 12, 2);
    private static final a ID_FIELD_DESC = new a("", (byte) 11, 3);
    private static final a APP_ID_FIELD_DESC = new a("", (byte) 11, 4);
    private static final a FEEDBACKS_FIELD_DESC = new a("", (byte) 13, 5);
    private static final a CATEGORY_FIELD_DESC = new a("", (byte) 11, 6);

    public XmPushActionSendFeedback() {
    }

    public void clear() {
        this.debug = null;
        this.target = null;
        this.id = null;
        this.appId = null;
        this.feedbacks = null;
        this.category = null;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof XmPushActionSendFeedback)) {
            return equals((XmPushActionSendFeedback) obj);
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

    public Map<String, String> getFeedbacks() {
        return this.feedbacks;
    }

    public int getFeedbacksSize() {
        Map<String, String> map = this.feedbacks;
        if (map == null) {
            return 0;
        }
        return map.size();
    }

    public String getId() {
        return this.id;
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

    public boolean isSetFeedbacks() {
        return this.feedbacks != null;
    }

    public boolean isSetId() {
        return this.id != null;
    }

    public boolean isSetTarget() {
        return this.target != null;
    }

    public void putToFeedbacks(String str, String str2) {
        if (this.feedbacks == null) {
            this.feedbacks = new HashMap();
        }
        this.feedbacks.put(str, str2);
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
                    if (b2 == 13) {
                        c cVarL = dVar.l();
                        this.feedbacks = new HashMap(cVarL.f2156c * 2);
                        for (int i2 = 0; i2 < cVarL.f2156c; i2++) {
                            this.feedbacks.put(dVar.p(), dVar.p());
                        }
                        dVar.m();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 6:
                    if (b2 == 11) {
                        this.category = dVar.p();
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

    public XmPushActionSendFeedback setAppId(String str) {
        this.appId = str;
        return this;
    }

    public void setAppIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.appId = null;
    }

    public XmPushActionSendFeedback setCategory(String str) {
        this.category = str;
        return this;
    }

    public void setCategoryIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.category = null;
    }

    public XmPushActionSendFeedback setDebug(String str) {
        this.debug = str;
        return this;
    }

    public void setDebugIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.debug = null;
    }

    public XmPushActionSendFeedback setFeedbacks(Map<String, String> map) {
        this.feedbacks = map;
        return this;
    }

    public void setFeedbacksIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.feedbacks = null;
    }

    public XmPushActionSendFeedback setId(String str) {
        this.id = str;
        return this;
    }

    public void setIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.id = null;
    }

    public XmPushActionSendFeedback setTarget(Target target) {
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
        StringBuilder sb = new StringBuilder("XmPushActionSendFeedback(");
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
        if (isSetFeedbacks()) {
            sb.append(", ");
            sb.append("feedbacks:");
            Map<String, String> map = this.feedbacks;
            if (map == null) {
                sb.append("null");
            } else {
                sb.append(map);
            }
        }
        if (isSetCategory()) {
            sb.append(", ");
            sb.append("category:");
            String str4 = this.category;
            if (str4 == null) {
                sb.append("null");
            } else {
                sb.append(str4);
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

    public void unsetFeedbacks() {
        this.feedbacks = null;
    }

    public void unsetId() {
        this.id = null;
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
        if (this.feedbacks != null && isSetFeedbacks()) {
            dVar.w(FEEDBACKS_FIELD_DESC);
            dVar.E(new c((byte) 11, (byte) 11, this.feedbacks.size()));
            for (Map.Entry<String, String> entry : this.feedbacks.entrySet()) {
                dVar.I(entry.getKey());
                dVar.I(entry.getValue());
            }
            dVar.F();
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

    public XmPushActionSendFeedback(String str, String str2) {
        this();
        this.id = str;
        this.appId = str2;
    }

    @Override // java.lang.Comparable
    public int compareTo(XmPushActionSendFeedback xmPushActionSendFeedback) {
        int iG;
        int i2;
        int iG2;
        int iG3;
        int iF;
        int iG4;
        if (!getClass().equals(xmPushActionSendFeedback.getClass())) {
            return getClass().getName().compareTo(xmPushActionSendFeedback.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetDebug()).compareTo(Boolean.valueOf(xmPushActionSendFeedback.isSetDebug()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetDebug() && (iG4 = g0.a.g(this.debug, xmPushActionSendFeedback.debug)) != 0) {
            return iG4;
        }
        int iCompareTo2 = Boolean.valueOf(isSetTarget()).compareTo(Boolean.valueOf(xmPushActionSendFeedback.isSetTarget()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (isSetTarget() && (iF = g0.a.f(this.target, xmPushActionSendFeedback.target)) != 0) {
            return iF;
        }
        int iCompareTo3 = Boolean.valueOf(isSetId()).compareTo(Boolean.valueOf(xmPushActionSendFeedback.isSetId()));
        if (iCompareTo3 != 0) {
            return iCompareTo3;
        }
        if (isSetId() && (iG3 = g0.a.g(this.id, xmPushActionSendFeedback.id)) != 0) {
            return iG3;
        }
        int iCompareTo4 = Boolean.valueOf(isSetAppId()).compareTo(Boolean.valueOf(xmPushActionSendFeedback.isSetAppId()));
        if (iCompareTo4 != 0) {
            return iCompareTo4;
        }
        if (isSetAppId() && (iG2 = g0.a.g(this.appId, xmPushActionSendFeedback.appId)) != 0) {
            return iG2;
        }
        int iCompareTo5 = Boolean.valueOf(isSetFeedbacks()).compareTo(Boolean.valueOf(xmPushActionSendFeedback.isSetFeedbacks()));
        if (iCompareTo5 != 0) {
            return iCompareTo5;
        }
        if (isSetFeedbacks() && (i2 = g0.a.i(this.feedbacks, xmPushActionSendFeedback.feedbacks)) != 0) {
            return i2;
        }
        int iCompareTo6 = Boolean.valueOf(isSetCategory()).compareTo(Boolean.valueOf(xmPushActionSendFeedback.isSetCategory()));
        if (iCompareTo6 != 0) {
            return iCompareTo6;
        }
        if (!isSetCategory() || (iG = g0.a.g(this.category, xmPushActionSendFeedback.category)) == 0) {
            return 0;
        }
        return iG;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public XmPushActionSendFeedback m41deepCopy() {
        return new XmPushActionSendFeedback(this);
    }

    public boolean equals(XmPushActionSendFeedback xmPushActionSendFeedback) {
        if (xmPushActionSendFeedback == null) {
            return false;
        }
        boolean zIsSetDebug = isSetDebug();
        boolean zIsSetDebug2 = xmPushActionSendFeedback.isSetDebug();
        if ((zIsSetDebug || zIsSetDebug2) && !(zIsSetDebug && zIsSetDebug2 && this.debug.equals(xmPushActionSendFeedback.debug))) {
            return false;
        }
        boolean zIsSetTarget = isSetTarget();
        boolean zIsSetTarget2 = xmPushActionSendFeedback.isSetTarget();
        if ((zIsSetTarget || zIsSetTarget2) && !(zIsSetTarget && zIsSetTarget2 && this.target.equals(xmPushActionSendFeedback.target))) {
            return false;
        }
        boolean zIsSetId = isSetId();
        boolean zIsSetId2 = xmPushActionSendFeedback.isSetId();
        if ((zIsSetId || zIsSetId2) && !(zIsSetId && zIsSetId2 && this.id.equals(xmPushActionSendFeedback.id))) {
            return false;
        }
        boolean zIsSetAppId = isSetAppId();
        boolean zIsSetAppId2 = xmPushActionSendFeedback.isSetAppId();
        if ((zIsSetAppId || zIsSetAppId2) && !(zIsSetAppId && zIsSetAppId2 && this.appId.equals(xmPushActionSendFeedback.appId))) {
            return false;
        }
        boolean zIsSetFeedbacks = isSetFeedbacks();
        boolean zIsSetFeedbacks2 = xmPushActionSendFeedback.isSetFeedbacks();
        if ((zIsSetFeedbacks || zIsSetFeedbacks2) && !(zIsSetFeedbacks && zIsSetFeedbacks2 && this.feedbacks.equals(xmPushActionSendFeedback.feedbacks))) {
            return false;
        }
        boolean zIsSetCategory = isSetCategory();
        boolean zIsSetCategory2 = xmPushActionSendFeedback.isSetCategory();
        if (zIsSetCategory || zIsSetCategory2) {
            return zIsSetCategory && zIsSetCategory2 && this.category.equals(xmPushActionSendFeedback.category);
        }
        return true;
    }

    public XmPushActionSendFeedback(XmPushActionSendFeedback xmPushActionSendFeedback) {
        if (xmPushActionSendFeedback.isSetDebug()) {
            this.debug = xmPushActionSendFeedback.debug;
        }
        if (xmPushActionSendFeedback.isSetTarget()) {
            this.target = new Target(xmPushActionSendFeedback.target);
        }
        if (xmPushActionSendFeedback.isSetId()) {
            this.id = xmPushActionSendFeedback.id;
        }
        if (xmPushActionSendFeedback.isSetAppId()) {
            this.appId = xmPushActionSendFeedback.appId;
        }
        if (xmPushActionSendFeedback.isSetFeedbacks()) {
            HashMap map = new HashMap();
            for (Map.Entry<String, String> entry : xmPushActionSendFeedback.feedbacks.entrySet()) {
                map.put(entry.getKey(), entry.getValue());
            }
            this.feedbacks = map;
        }
        if (xmPushActionSendFeedback.isSetCategory()) {
            this.category = xmPushActionSendFeedback.category;
        }
    }
}
