package com.xiaomi.xmpush.thrift;

import com.xiaomi.push.mpcd.Constants;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.BitSet;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.protocol.a;
import org.apache.thrift.protocol.d;
import org.apache.thrift.protocol.e;
import org.apache.thrift.protocol.g;

/* JADX INFO: loaded from: classes.dex */
public class XmPushActionContainer implements TBase<XmPushActionContainer, Object>, Serializable, Cloneable {
    private static final int __ENCRYPTACTION_ISSET_ID = 0;
    private static final int __ISREQUEST_ISSET_ID = 1;
    private BitSet __isset_bit_vector;
    public ActionType action;
    public String appid;
    public boolean encryptAction;
    public boolean isRequest;
    public PushMetaInfo metaInfo;
    public String packageName;
    public ByteBuffer pushAction;
    public Target target;
    private static final g STRUCT_DESC = new g("XmPushActionContainer");
    private static final a ACTION_FIELD_DESC = new a("", (byte) 8, 1);
    private static final a ENCRYPT_ACTION_FIELD_DESC = new a("", (byte) 2, 2);
    private static final a IS_REQUEST_FIELD_DESC = new a("", (byte) 2, 3);
    private static final a PUSH_ACTION_FIELD_DESC = new a("", (byte) 11, 4);
    private static final a APPID_FIELD_DESC = new a("", (byte) 11, 5);
    private static final a PACKAGE_NAME_FIELD_DESC = new a("", (byte) 11, 6);
    private static final a TARGET_FIELD_DESC = new a("", (byte) 12, 7);
    private static final a META_INFO_FIELD_DESC = new a("", (byte) 12, 8);

    public XmPushActionContainer() {
        this.__isset_bit_vector = new BitSet(2);
        this.encryptAction = true;
        this.isRequest = true;
    }

    public ByteBuffer BufferForPushAction() {
        return this.pushAction;
    }

    public void clear() {
        this.action = null;
        this.encryptAction = true;
        this.isRequest = true;
        this.pushAction = null;
        this.appid = null;
        this.packageName = null;
        this.target = null;
        this.metaInfo = null;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof XmPushActionContainer)) {
            return equals((XmPushActionContainer) obj);
        }
        return false;
    }

    public ActionType getAction() {
        return this.action;
    }

    public String getAppid() {
        return this.appid;
    }

    public PushMetaInfo getMetaInfo() {
        return this.metaInfo;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public byte[] getPushAction() {
        setPushAction(g0.a.p(this.pushAction));
        return this.pushAction.array();
    }

    public Target getTarget() {
        return this.target;
    }

    public int hashCode() {
        return 0;
    }

    public boolean isEncryptAction() {
        return this.encryptAction;
    }

    public boolean isIsRequest() {
        return this.isRequest;
    }

    public boolean isSetAction() {
        return this.action != null;
    }

    public boolean isSetAppid() {
        return this.appid != null;
    }

    public boolean isSetEncryptAction() {
        return this.__isset_bit_vector.get(0);
    }

    public boolean isSetIsRequest() {
        return this.__isset_bit_vector.get(1);
    }

    public boolean isSetMetaInfo() {
        return this.metaInfo != null;
    }

    public boolean isSetPackageName() {
        return this.packageName != null;
    }

    public boolean isSetPushAction() {
        return this.pushAction != null;
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
                if (!isSetEncryptAction()) {
                    throw new TProtocolException("Required field 'encryptAction' was not found in serialized data! Struct: " + toString());
                }
                if (isSetIsRequest()) {
                    validate();
                    return;
                }
                throw new TProtocolException("Required field 'isRequest' was not found in serialized data! Struct: " + toString());
            }
            switch (aVarE.f2151c) {
                case 1:
                    if (b2 != 8) {
                        e.a(dVar, b2);
                    } else {
                        this.action = ActionType.findByValue(dVar.h());
                    }
                    break;
                case 2:
                    if (b2 != 2) {
                        e.a(dVar, b2);
                    } else {
                        this.encryptAction = dVar.b();
                        setEncryptActionIsSet(true);
                    }
                    break;
                case 3:
                    if (b2 != 2) {
                        e.a(dVar, b2);
                    } else {
                        this.isRequest = dVar.b();
                        setIsRequestIsSet(true);
                    }
                    break;
                case 4:
                    if (b2 != 11) {
                        e.a(dVar, b2);
                    } else {
                        this.pushAction = dVar.a();
                    }
                    break;
                case 5:
                    if (b2 != 11) {
                        e.a(dVar, b2);
                    } else {
                        this.appid = dVar.p();
                    }
                    break;
                case 6:
                    if (b2 != 11) {
                        e.a(dVar, b2);
                    } else {
                        this.packageName = dVar.p();
                    }
                    break;
                case 7:
                    if (b2 != 12) {
                        e.a(dVar, b2);
                    } else {
                        Target target = new Target();
                        this.target = target;
                        target.read(dVar);
                    }
                    break;
                case 8:
                    if (b2 != 12) {
                        e.a(dVar, b2);
                    } else {
                        PushMetaInfo pushMetaInfo = new PushMetaInfo();
                        this.metaInfo = pushMetaInfo;
                        pushMetaInfo.read(dVar);
                    }
                    break;
                default:
                    e.a(dVar, b2);
                    break;
            }
            dVar.f();
        }
    }

    public XmPushActionContainer setAction(ActionType actionType) {
        this.action = actionType;
        return this;
    }

    public void setActionIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.action = null;
    }

    public XmPushActionContainer setAppid(String str) {
        this.appid = str;
        return this;
    }

    public void setAppidIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.appid = null;
    }

    public XmPushActionContainer setEncryptAction(boolean z2) {
        this.encryptAction = z2;
        setEncryptActionIsSet(true);
        return this;
    }

    public void setEncryptActionIsSet(boolean z2) {
        this.__isset_bit_vector.set(0, z2);
    }

    public XmPushActionContainer setIsRequest(boolean z2) {
        this.isRequest = z2;
        setIsRequestIsSet(true);
        return this;
    }

    public void setIsRequestIsSet(boolean z2) {
        this.__isset_bit_vector.set(1, z2);
    }

    public XmPushActionContainer setMetaInfo(PushMetaInfo pushMetaInfo) {
        this.metaInfo = pushMetaInfo;
        return this;
    }

    public void setMetaInfoIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.metaInfo = null;
    }

    public XmPushActionContainer setPackageName(String str) {
        this.packageName = str;
        return this;
    }

    public void setPackageNameIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.packageName = null;
    }

    public XmPushActionContainer setPushAction(byte[] bArr) {
        setPushAction(ByteBuffer.wrap(bArr));
        return this;
    }

    public void setPushActionIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.pushAction = null;
    }

    public XmPushActionContainer setTarget(Target target) {
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
        StringBuilder sb = new StringBuilder("XmPushActionContainer(");
        sb.append("action:");
        ActionType actionType = this.action;
        if (actionType == null) {
            sb.append("null");
        } else {
            sb.append(actionType);
        }
        sb.append(", ");
        sb.append("encryptAction:");
        sb.append(this.encryptAction);
        sb.append(", ");
        sb.append("isRequest:");
        sb.append(this.isRequest);
        if (isSetAppid()) {
            sb.append(", ");
            sb.append("appid:");
            String str = this.appid;
            if (str == null) {
                sb.append("null");
            } else {
                sb.append(str);
            }
        }
        if (isSetPackageName()) {
            sb.append(", ");
            sb.append("packageName:");
            String str2 = this.packageName;
            if (str2 == null) {
                sb.append("null");
            } else {
                sb.append(str2);
            }
        }
        sb.append(", ");
        sb.append("target:");
        Target target = this.target;
        if (target == null) {
            sb.append("null");
        } else {
            sb.append(target);
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
        sb.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
        return sb.toString();
    }

    public void unsetAction() {
        this.action = null;
    }

    public void unsetAppid() {
        this.appid = null;
    }

    public void unsetEncryptAction() {
        this.__isset_bit_vector.clear(0);
    }

    public void unsetIsRequest() {
        this.__isset_bit_vector.clear(1);
    }

    public void unsetMetaInfo() {
        this.metaInfo = null;
    }

    public void unsetPackageName() {
        this.packageName = null;
    }

    public void unsetPushAction() {
        this.pushAction = null;
    }

    public void unsetTarget() {
        this.target = null;
    }

    public void validate() throws TException {
        if (this.action == null) {
            throw new TProtocolException("Required field 'action' was not present! Struct: " + toString());
        }
        if (this.pushAction == null) {
            throw new TProtocolException("Required field 'pushAction' was not present! Struct: " + toString());
        }
        if (this.target != null) {
            return;
        }
        throw new TProtocolException("Required field 'target' was not present! Struct: " + toString());
    }

    @Override // org.apache.thrift.TBase
    public void write(d dVar) throws TException {
        validate();
        dVar.J(STRUCT_DESC);
        if (this.action != null) {
            dVar.w(ACTION_FIELD_DESC);
            dVar.A(this.action.getValue());
            dVar.x();
        }
        dVar.w(ENCRYPT_ACTION_FIELD_DESC);
        dVar.u(this.encryptAction);
        dVar.x();
        dVar.w(IS_REQUEST_FIELD_DESC);
        dVar.u(this.isRequest);
        dVar.x();
        if (this.pushAction != null) {
            dVar.w(PUSH_ACTION_FIELD_DESC);
            dVar.t(this.pushAction);
            dVar.x();
        }
        if (this.appid != null && isSetAppid()) {
            dVar.w(APPID_FIELD_DESC);
            dVar.I(this.appid);
            dVar.x();
        }
        if (this.packageName != null && isSetPackageName()) {
            dVar.w(PACKAGE_NAME_FIELD_DESC);
            dVar.I(this.packageName);
            dVar.x();
        }
        if (this.target != null) {
            dVar.w(TARGET_FIELD_DESC);
            this.target.write(dVar);
            dVar.x();
        }
        if (this.metaInfo != null && isSetMetaInfo()) {
            dVar.w(META_INFO_FIELD_DESC);
            this.metaInfo.write(dVar);
            dVar.x();
        }
        dVar.y();
        dVar.K();
    }

    @Override // java.lang.Comparable
    public int compareTo(XmPushActionContainer xmPushActionContainer) {
        int iF;
        int iF2;
        int iG;
        int iG2;
        int iF3;
        int iL;
        int iL2;
        int iF4;
        if (!getClass().equals(xmPushActionContainer.getClass())) {
            return getClass().getName().compareTo(xmPushActionContainer.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetAction()).compareTo(Boolean.valueOf(xmPushActionContainer.isSetAction()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetAction() && (iF4 = g0.a.f(this.action, xmPushActionContainer.action)) != 0) {
            return iF4;
        }
        int iCompareTo2 = Boolean.valueOf(isSetEncryptAction()).compareTo(Boolean.valueOf(xmPushActionContainer.isSetEncryptAction()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (isSetEncryptAction() && (iL2 = g0.a.l(this.encryptAction, xmPushActionContainer.encryptAction)) != 0) {
            return iL2;
        }
        int iCompareTo3 = Boolean.valueOf(isSetIsRequest()).compareTo(Boolean.valueOf(xmPushActionContainer.isSetIsRequest()));
        if (iCompareTo3 != 0) {
            return iCompareTo3;
        }
        if (isSetIsRequest() && (iL = g0.a.l(this.isRequest, xmPushActionContainer.isRequest)) != 0) {
            return iL;
        }
        int iCompareTo4 = Boolean.valueOf(isSetPushAction()).compareTo(Boolean.valueOf(xmPushActionContainer.isSetPushAction()));
        if (iCompareTo4 != 0) {
            return iCompareTo4;
        }
        if (isSetPushAction() && (iF3 = g0.a.f(this.pushAction, xmPushActionContainer.pushAction)) != 0) {
            return iF3;
        }
        int iCompareTo5 = Boolean.valueOf(isSetAppid()).compareTo(Boolean.valueOf(xmPushActionContainer.isSetAppid()));
        if (iCompareTo5 != 0) {
            return iCompareTo5;
        }
        if (isSetAppid() && (iG2 = g0.a.g(this.appid, xmPushActionContainer.appid)) != 0) {
            return iG2;
        }
        int iCompareTo6 = Boolean.valueOf(isSetPackageName()).compareTo(Boolean.valueOf(xmPushActionContainer.isSetPackageName()));
        if (iCompareTo6 != 0) {
            return iCompareTo6;
        }
        if (isSetPackageName() && (iG = g0.a.g(this.packageName, xmPushActionContainer.packageName)) != 0) {
            return iG;
        }
        int iCompareTo7 = Boolean.valueOf(isSetTarget()).compareTo(Boolean.valueOf(xmPushActionContainer.isSetTarget()));
        if (iCompareTo7 != 0) {
            return iCompareTo7;
        }
        if (isSetTarget() && (iF2 = g0.a.f(this.target, xmPushActionContainer.target)) != 0) {
            return iF2;
        }
        int iCompareTo8 = Boolean.valueOf(isSetMetaInfo()).compareTo(Boolean.valueOf(xmPushActionContainer.isSetMetaInfo()));
        if (iCompareTo8 != 0) {
            return iCompareTo8;
        }
        if (!isSetMetaInfo() || (iF = g0.a.f(this.metaInfo, xmPushActionContainer.metaInfo)) == 0) {
            return 0;
        }
        return iF;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public XmPushActionContainer m34deepCopy() {
        return new XmPushActionContainer(this);
    }

    public XmPushActionContainer setPushAction(ByteBuffer byteBuffer) {
        this.pushAction = byteBuffer;
        return this;
    }

    public boolean equals(XmPushActionContainer xmPushActionContainer) {
        if (xmPushActionContainer == null) {
            return false;
        }
        boolean zIsSetAction = isSetAction();
        boolean zIsSetAction2 = xmPushActionContainer.isSetAction();
        if (((zIsSetAction || zIsSetAction2) && (!zIsSetAction || !zIsSetAction2 || !this.action.equals(xmPushActionContainer.action))) || this.encryptAction != xmPushActionContainer.encryptAction || this.isRequest != xmPushActionContainer.isRequest) {
            return false;
        }
        boolean zIsSetPushAction = isSetPushAction();
        boolean zIsSetPushAction2 = xmPushActionContainer.isSetPushAction();
        if ((zIsSetPushAction || zIsSetPushAction2) && !(zIsSetPushAction && zIsSetPushAction2 && this.pushAction.equals(xmPushActionContainer.pushAction))) {
            return false;
        }
        boolean zIsSetAppid = isSetAppid();
        boolean zIsSetAppid2 = xmPushActionContainer.isSetAppid();
        if ((zIsSetAppid || zIsSetAppid2) && !(zIsSetAppid && zIsSetAppid2 && this.appid.equals(xmPushActionContainer.appid))) {
            return false;
        }
        boolean zIsSetPackageName = isSetPackageName();
        boolean zIsSetPackageName2 = xmPushActionContainer.isSetPackageName();
        if ((zIsSetPackageName || zIsSetPackageName2) && !(zIsSetPackageName && zIsSetPackageName2 && this.packageName.equals(xmPushActionContainer.packageName))) {
            return false;
        }
        boolean zIsSetTarget = isSetTarget();
        boolean zIsSetTarget2 = xmPushActionContainer.isSetTarget();
        if ((zIsSetTarget || zIsSetTarget2) && !(zIsSetTarget && zIsSetTarget2 && this.target.equals(xmPushActionContainer.target))) {
            return false;
        }
        boolean zIsSetMetaInfo = isSetMetaInfo();
        boolean zIsSetMetaInfo2 = xmPushActionContainer.isSetMetaInfo();
        if (zIsSetMetaInfo || zIsSetMetaInfo2) {
            return zIsSetMetaInfo && zIsSetMetaInfo2 && this.metaInfo.equals(xmPushActionContainer.metaInfo);
        }
        return true;
    }

    public XmPushActionContainer(ActionType actionType, boolean z2, boolean z3, ByteBuffer byteBuffer, Target target) {
        this();
        this.action = actionType;
        this.encryptAction = z2;
        setEncryptActionIsSet(true);
        this.isRequest = z3;
        setIsRequestIsSet(true);
        this.pushAction = byteBuffer;
        this.target = target;
    }

    public XmPushActionContainer(XmPushActionContainer xmPushActionContainer) {
        BitSet bitSet = new BitSet(2);
        this.__isset_bit_vector = bitSet;
        bitSet.clear();
        this.__isset_bit_vector.or(xmPushActionContainer.__isset_bit_vector);
        if (xmPushActionContainer.isSetAction()) {
            this.action = xmPushActionContainer.action;
        }
        this.encryptAction = xmPushActionContainer.encryptAction;
        this.isRequest = xmPushActionContainer.isRequest;
        if (xmPushActionContainer.isSetPushAction()) {
            this.pushAction = g0.a.n(xmPushActionContainer.pushAction);
        }
        if (xmPushActionContainer.isSetAppid()) {
            this.appid = xmPushActionContainer.appid;
        }
        if (xmPushActionContainer.isSetPackageName()) {
            this.packageName = xmPushActionContainer.packageName;
        }
        if (xmPushActionContainer.isSetTarget()) {
            this.target = new Target(xmPushActionContainer.target);
        }
        if (xmPushActionContainer.isSetMetaInfo()) {
            this.metaInfo = new PushMetaInfo(xmPushActionContainer.metaInfo);
        }
    }
}
