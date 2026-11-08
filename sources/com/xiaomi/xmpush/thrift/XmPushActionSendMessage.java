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
public class XmPushActionSendMessage implements TBase<XmPushActionSendMessage, Object>, Serializable, Cloneable {
    private static final int __NEEDACK_ISSET_ID = 0;
    private BitSet __isset_bit_vector;
    public String aliasName;
    public String appId;
    public String category;
    public String debug;
    public String id;
    public PushMessage message;
    public boolean needAck;
    public String packageName;
    public Map<String, String> params;
    public Target target;
    public String topic;
    public String userAccount;
    private static final g STRUCT_DESC = new g("XmPushActionSendMessage");
    private static final a DEBUG_FIELD_DESC = new a("", (byte) 11, 1);
    private static final a TARGET_FIELD_DESC = new a("", (byte) 12, 2);
    private static final a ID_FIELD_DESC = new a("", (byte) 11, 3);
    private static final a APP_ID_FIELD_DESC = new a("", (byte) 11, 4);
    private static final a PACKAGE_NAME_FIELD_DESC = new a("", (byte) 11, 5);
    private static final a TOPIC_FIELD_DESC = new a("", (byte) 11, 6);
    private static final a ALIAS_NAME_FIELD_DESC = new a("", (byte) 11, 7);
    private static final a MESSAGE_FIELD_DESC = new a("", (byte) 12, 8);
    private static final a NEED_ACK_FIELD_DESC = new a("", (byte) 2, 9);
    private static final a PARAMS_FIELD_DESC = new a("", (byte) 13, 10);
    private static final a CATEGORY_FIELD_DESC = new a("", (byte) 11, 11);
    private static final a USER_ACCOUNT_FIELD_DESC = new a("", (byte) 11, 12);

    public XmPushActionSendMessage() {
        this.__isset_bit_vector = new BitSet(1);
        this.needAck = true;
    }

    public void clear() {
        this.debug = null;
        this.target = null;
        this.id = null;
        this.appId = null;
        this.packageName = null;
        this.topic = null;
        this.aliasName = null;
        this.message = null;
        this.needAck = true;
        this.params = null;
        this.category = null;
        this.userAccount = null;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof XmPushActionSendMessage)) {
            return equals((XmPushActionSendMessage) obj);
        }
        return false;
    }

    public String getAliasName() {
        return this.aliasName;
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

    public String getId() {
        return this.id;
    }

    public PushMessage getMessage() {
        return this.message;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public Map<String, String> getParams() {
        return this.params;
    }

    public int getParamsSize() {
        Map<String, String> map = this.params;
        if (map == null) {
            return 0;
        }
        return map.size();
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

    public boolean isNeedAck() {
        return this.needAck;
    }

    public boolean isSetAliasName() {
        return this.aliasName != null;
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

    public boolean isSetId() {
        return this.id != null;
    }

    public boolean isSetMessage() {
        return this.message != null;
    }

    public boolean isSetNeedAck() {
        return this.__isset_bit_vector.get(0);
    }

    public boolean isSetPackageName() {
        return this.packageName != null;
    }

    public boolean isSetParams() {
        return this.params != null;
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

    public void putToParams(String str, String str2) {
        if (this.params == null) {
            this.params = new HashMap();
        }
        this.params.put(str, str2);
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
                        this.packageName = dVar.p();
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
                        PushMessage pushMessage = new PushMessage();
                        this.message = pushMessage;
                        pushMessage.read(dVar);
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 9:
                    if (b2 == 2) {
                        this.needAck = dVar.b();
                        setNeedAckIsSet(true);
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 10:
                    if (b2 == 13) {
                        c cVarL = dVar.l();
                        this.params = new HashMap(cVarL.f2156c * 2);
                        for (int i2 = 0; i2 < cVarL.f2156c; i2++) {
                            this.params.put(dVar.p(), dVar.p());
                        }
                        dVar.m();
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
                case 12:
                    if (b2 == 11) {
                        this.userAccount = dVar.p();
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

    public XmPushActionSendMessage setAliasName(String str) {
        this.aliasName = str;
        return this;
    }

    public void setAliasNameIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.aliasName = null;
    }

    public XmPushActionSendMessage setAppId(String str) {
        this.appId = str;
        return this;
    }

    public void setAppIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.appId = null;
    }

    public XmPushActionSendMessage setCategory(String str) {
        this.category = str;
        return this;
    }

    public void setCategoryIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.category = null;
    }

    public XmPushActionSendMessage setDebug(String str) {
        this.debug = str;
        return this;
    }

    public void setDebugIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.debug = null;
    }

    public XmPushActionSendMessage setId(String str) {
        this.id = str;
        return this;
    }

    public void setIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.id = null;
    }

    public XmPushActionSendMessage setMessage(PushMessage pushMessage) {
        this.message = pushMessage;
        return this;
    }

    public void setMessageIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.message = null;
    }

    public XmPushActionSendMessage setNeedAck(boolean z2) {
        this.needAck = z2;
        setNeedAckIsSet(true);
        return this;
    }

    public void setNeedAckIsSet(boolean z2) {
        this.__isset_bit_vector.set(0, z2);
    }

    public XmPushActionSendMessage setPackageName(String str) {
        this.packageName = str;
        return this;
    }

    public void setPackageNameIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.packageName = null;
    }

    public XmPushActionSendMessage setParams(Map<String, String> map) {
        this.params = map;
        return this;
    }

    public void setParamsIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.params = null;
    }

    public XmPushActionSendMessage setTarget(Target target) {
        this.target = target;
        return this;
    }

    public void setTargetIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.target = null;
    }

    public XmPushActionSendMessage setTopic(String str) {
        this.topic = str;
        return this;
    }

    public void setTopicIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.topic = null;
    }

    public XmPushActionSendMessage setUserAccount(String str) {
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
        StringBuilder sb = new StringBuilder("XmPushActionSendMessage(");
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
        if (isSetPackageName()) {
            sb.append(", ");
            sb.append("packageName:");
            String str4 = this.packageName;
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
        if (isSetAliasName()) {
            sb.append(", ");
            sb.append("aliasName:");
            String str6 = this.aliasName;
            if (str6 == null) {
                sb.append("null");
            } else {
                sb.append(str6);
            }
        }
        if (isSetMessage()) {
            sb.append(", ");
            sb.append("message:");
            PushMessage pushMessage = this.message;
            if (pushMessage == null) {
                sb.append("null");
            } else {
                sb.append(pushMessage);
            }
        }
        if (isSetNeedAck()) {
            sb.append(", ");
            sb.append("needAck:");
            sb.append(this.needAck);
        }
        if (isSetParams()) {
            sb.append(", ");
            sb.append("params:");
            Map<String, String> map = this.params;
            if (map == null) {
                sb.append("null");
            } else {
                sb.append(map);
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
        if (isSetUserAccount()) {
            sb.append(", ");
            sb.append("userAccount:");
            String str8 = this.userAccount;
            if (str8 == null) {
                sb.append("null");
            } else {
                sb.append(str8);
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

    public void unsetCategory() {
        this.category = null;
    }

    public void unsetDebug() {
        this.debug = null;
    }

    public void unsetId() {
        this.id = null;
    }

    public void unsetMessage() {
        this.message = null;
    }

    public void unsetNeedAck() {
        this.__isset_bit_vector.clear(0);
    }

    public void unsetPackageName() {
        this.packageName = null;
    }

    public void unsetParams() {
        this.params = null;
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
        if (this.packageName != null && isSetPackageName()) {
            dVar.w(PACKAGE_NAME_FIELD_DESC);
            dVar.I(this.packageName);
            dVar.x();
        }
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
        if (this.message != null && isSetMessage()) {
            dVar.w(MESSAGE_FIELD_DESC);
            this.message.write(dVar);
            dVar.x();
        }
        if (isSetNeedAck()) {
            dVar.w(NEED_ACK_FIELD_DESC);
            dVar.u(this.needAck);
            dVar.x();
        }
        if (this.params != null && isSetParams()) {
            dVar.w(PARAMS_FIELD_DESC);
            dVar.E(new c((byte) 11, (byte) 11, this.params.size()));
            for (Map.Entry<String, String> entry : this.params.entrySet()) {
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
        if (this.userAccount != null && isSetUserAccount()) {
            dVar.w(USER_ACCOUNT_FIELD_DESC);
            dVar.I(this.userAccount);
            dVar.x();
        }
        dVar.y();
        dVar.K();
    }

    @Override // java.lang.Comparable
    public int compareTo(XmPushActionSendMessage xmPushActionSendMessage) {
        int iG;
        int iG2;
        int i2;
        int iL;
        int iF;
        int iG3;
        int iG4;
        int iG5;
        int iG6;
        int iG7;
        int iF2;
        int iG8;
        if (!getClass().equals(xmPushActionSendMessage.getClass())) {
            return getClass().getName().compareTo(xmPushActionSendMessage.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetDebug()).compareTo(Boolean.valueOf(xmPushActionSendMessage.isSetDebug()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetDebug() && (iG8 = g0.a.g(this.debug, xmPushActionSendMessage.debug)) != 0) {
            return iG8;
        }
        int iCompareTo2 = Boolean.valueOf(isSetTarget()).compareTo(Boolean.valueOf(xmPushActionSendMessage.isSetTarget()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (isSetTarget() && (iF2 = g0.a.f(this.target, xmPushActionSendMessage.target)) != 0) {
            return iF2;
        }
        int iCompareTo3 = Boolean.valueOf(isSetId()).compareTo(Boolean.valueOf(xmPushActionSendMessage.isSetId()));
        if (iCompareTo3 != 0) {
            return iCompareTo3;
        }
        if (isSetId() && (iG7 = g0.a.g(this.id, xmPushActionSendMessage.id)) != 0) {
            return iG7;
        }
        int iCompareTo4 = Boolean.valueOf(isSetAppId()).compareTo(Boolean.valueOf(xmPushActionSendMessage.isSetAppId()));
        if (iCompareTo4 != 0) {
            return iCompareTo4;
        }
        if (isSetAppId() && (iG6 = g0.a.g(this.appId, xmPushActionSendMessage.appId)) != 0) {
            return iG6;
        }
        int iCompareTo5 = Boolean.valueOf(isSetPackageName()).compareTo(Boolean.valueOf(xmPushActionSendMessage.isSetPackageName()));
        if (iCompareTo5 != 0) {
            return iCompareTo5;
        }
        if (isSetPackageName() && (iG5 = g0.a.g(this.packageName, xmPushActionSendMessage.packageName)) != 0) {
            return iG5;
        }
        int iCompareTo6 = Boolean.valueOf(isSetTopic()).compareTo(Boolean.valueOf(xmPushActionSendMessage.isSetTopic()));
        if (iCompareTo6 != 0) {
            return iCompareTo6;
        }
        if (isSetTopic() && (iG4 = g0.a.g(this.topic, xmPushActionSendMessage.topic)) != 0) {
            return iG4;
        }
        int iCompareTo7 = Boolean.valueOf(isSetAliasName()).compareTo(Boolean.valueOf(xmPushActionSendMessage.isSetAliasName()));
        if (iCompareTo7 != 0) {
            return iCompareTo7;
        }
        if (isSetAliasName() && (iG3 = g0.a.g(this.aliasName, xmPushActionSendMessage.aliasName)) != 0) {
            return iG3;
        }
        int iCompareTo8 = Boolean.valueOf(isSetMessage()).compareTo(Boolean.valueOf(xmPushActionSendMessage.isSetMessage()));
        if (iCompareTo8 != 0) {
            return iCompareTo8;
        }
        if (isSetMessage() && (iF = g0.a.f(this.message, xmPushActionSendMessage.message)) != 0) {
            return iF;
        }
        int iCompareTo9 = Boolean.valueOf(isSetNeedAck()).compareTo(Boolean.valueOf(xmPushActionSendMessage.isSetNeedAck()));
        if (iCompareTo9 != 0) {
            return iCompareTo9;
        }
        if (isSetNeedAck() && (iL = g0.a.l(this.needAck, xmPushActionSendMessage.needAck)) != 0) {
            return iL;
        }
        int iCompareTo10 = Boolean.valueOf(isSetParams()).compareTo(Boolean.valueOf(xmPushActionSendMessage.isSetParams()));
        if (iCompareTo10 != 0) {
            return iCompareTo10;
        }
        if (isSetParams() && (i2 = g0.a.i(this.params, xmPushActionSendMessage.params)) != 0) {
            return i2;
        }
        int iCompareTo11 = Boolean.valueOf(isSetCategory()).compareTo(Boolean.valueOf(xmPushActionSendMessage.isSetCategory()));
        if (iCompareTo11 != 0) {
            return iCompareTo11;
        }
        if (isSetCategory() && (iG2 = g0.a.g(this.category, xmPushActionSendMessage.category)) != 0) {
            return iG2;
        }
        int iCompareTo12 = Boolean.valueOf(isSetUserAccount()).compareTo(Boolean.valueOf(xmPushActionSendMessage.isSetUserAccount()));
        if (iCompareTo12 != 0) {
            return iCompareTo12;
        }
        if (!isSetUserAccount() || (iG = g0.a.g(this.userAccount, xmPushActionSendMessage.userAccount)) == 0) {
            return 0;
        }
        return iG;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public XmPushActionSendMessage m43deepCopy() {
        return new XmPushActionSendMessage(this);
    }

    public boolean equals(XmPushActionSendMessage xmPushActionSendMessage) {
        if (xmPushActionSendMessage == null) {
            return false;
        }
        boolean zIsSetDebug = isSetDebug();
        boolean zIsSetDebug2 = xmPushActionSendMessage.isSetDebug();
        if ((zIsSetDebug || zIsSetDebug2) && !(zIsSetDebug && zIsSetDebug2 && this.debug.equals(xmPushActionSendMessage.debug))) {
            return false;
        }
        boolean zIsSetTarget = isSetTarget();
        boolean zIsSetTarget2 = xmPushActionSendMessage.isSetTarget();
        if ((zIsSetTarget || zIsSetTarget2) && !(zIsSetTarget && zIsSetTarget2 && this.target.equals(xmPushActionSendMessage.target))) {
            return false;
        }
        boolean zIsSetId = isSetId();
        boolean zIsSetId2 = xmPushActionSendMessage.isSetId();
        if ((zIsSetId || zIsSetId2) && !(zIsSetId && zIsSetId2 && this.id.equals(xmPushActionSendMessage.id))) {
            return false;
        }
        boolean zIsSetAppId = isSetAppId();
        boolean zIsSetAppId2 = xmPushActionSendMessage.isSetAppId();
        if ((zIsSetAppId || zIsSetAppId2) && !(zIsSetAppId && zIsSetAppId2 && this.appId.equals(xmPushActionSendMessage.appId))) {
            return false;
        }
        boolean zIsSetPackageName = isSetPackageName();
        boolean zIsSetPackageName2 = xmPushActionSendMessage.isSetPackageName();
        if ((zIsSetPackageName || zIsSetPackageName2) && !(zIsSetPackageName && zIsSetPackageName2 && this.packageName.equals(xmPushActionSendMessage.packageName))) {
            return false;
        }
        boolean zIsSetTopic = isSetTopic();
        boolean zIsSetTopic2 = xmPushActionSendMessage.isSetTopic();
        if ((zIsSetTopic || zIsSetTopic2) && !(zIsSetTopic && zIsSetTopic2 && this.topic.equals(xmPushActionSendMessage.topic))) {
            return false;
        }
        boolean zIsSetAliasName = isSetAliasName();
        boolean zIsSetAliasName2 = xmPushActionSendMessage.isSetAliasName();
        if ((zIsSetAliasName || zIsSetAliasName2) && !(zIsSetAliasName && zIsSetAliasName2 && this.aliasName.equals(xmPushActionSendMessage.aliasName))) {
            return false;
        }
        boolean zIsSetMessage = isSetMessage();
        boolean zIsSetMessage2 = xmPushActionSendMessage.isSetMessage();
        if ((zIsSetMessage || zIsSetMessage2) && !(zIsSetMessage && zIsSetMessage2 && this.message.equals(xmPushActionSendMessage.message))) {
            return false;
        }
        boolean zIsSetNeedAck = isSetNeedAck();
        boolean zIsSetNeedAck2 = xmPushActionSendMessage.isSetNeedAck();
        if ((zIsSetNeedAck || zIsSetNeedAck2) && !(zIsSetNeedAck && zIsSetNeedAck2 && this.needAck == xmPushActionSendMessage.needAck)) {
            return false;
        }
        boolean zIsSetParams = isSetParams();
        boolean zIsSetParams2 = xmPushActionSendMessage.isSetParams();
        if ((zIsSetParams || zIsSetParams2) && !(zIsSetParams && zIsSetParams2 && this.params.equals(xmPushActionSendMessage.params))) {
            return false;
        }
        boolean zIsSetCategory = isSetCategory();
        boolean zIsSetCategory2 = xmPushActionSendMessage.isSetCategory();
        if ((zIsSetCategory || zIsSetCategory2) && !(zIsSetCategory && zIsSetCategory2 && this.category.equals(xmPushActionSendMessage.category))) {
            return false;
        }
        boolean zIsSetUserAccount = isSetUserAccount();
        boolean zIsSetUserAccount2 = xmPushActionSendMessage.isSetUserAccount();
        if (zIsSetUserAccount || zIsSetUserAccount2) {
            return zIsSetUserAccount && zIsSetUserAccount2 && this.userAccount.equals(xmPushActionSendMessage.userAccount);
        }
        return true;
    }

    public XmPushActionSendMessage(String str, String str2) {
        this();
        this.id = str;
        this.appId = str2;
    }

    public XmPushActionSendMessage(XmPushActionSendMessage xmPushActionSendMessage) {
        BitSet bitSet = new BitSet(1);
        this.__isset_bit_vector = bitSet;
        bitSet.clear();
        this.__isset_bit_vector.or(xmPushActionSendMessage.__isset_bit_vector);
        if (xmPushActionSendMessage.isSetDebug()) {
            this.debug = xmPushActionSendMessage.debug;
        }
        if (xmPushActionSendMessage.isSetTarget()) {
            this.target = new Target(xmPushActionSendMessage.target);
        }
        if (xmPushActionSendMessage.isSetId()) {
            this.id = xmPushActionSendMessage.id;
        }
        if (xmPushActionSendMessage.isSetAppId()) {
            this.appId = xmPushActionSendMessage.appId;
        }
        if (xmPushActionSendMessage.isSetPackageName()) {
            this.packageName = xmPushActionSendMessage.packageName;
        }
        if (xmPushActionSendMessage.isSetTopic()) {
            this.topic = xmPushActionSendMessage.topic;
        }
        if (xmPushActionSendMessage.isSetAliasName()) {
            this.aliasName = xmPushActionSendMessage.aliasName;
        }
        if (xmPushActionSendMessage.isSetMessage()) {
            this.message = new PushMessage(xmPushActionSendMessage.message);
        }
        this.needAck = xmPushActionSendMessage.needAck;
        if (xmPushActionSendMessage.isSetParams()) {
            HashMap map = new HashMap();
            for (Map.Entry<String, String> entry : xmPushActionSendMessage.params.entrySet()) {
                map.put(entry.getKey(), entry.getValue());
            }
            this.params = map;
        }
        if (xmPushActionSendMessage.isSetCategory()) {
            this.category = xmPushActionSendMessage.category;
        }
        if (xmPushActionSendMessage.isSetUserAccount()) {
            this.userAccount = xmPushActionSendMessage.userAccount;
        }
    }
}
