package com.xiaomi.xmpush.thrift;

import com.xiaomi.push.mpcd.Constants;
import java.io.Serializable;
import java.nio.ByteBuffer;
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
public class XmPushActionNotification implements TBase<XmPushActionNotification, Object>, Serializable, Cloneable {
    private static final int __ALREADYLOGCLICKINXMQ_ISSET_ID = 2;
    private static final int __CREATEDTS_ISSET_ID = 1;
    private static final int __REQUIREACK_ISSET_ID = 0;
    private BitSet __isset_bit_vector;
    public String aliasName;
    public boolean alreadyLogClickInXmq;
    public String appId;
    public ByteBuffer binaryExtra;
    public String category;
    public long createdTs;
    public String debug;
    public Map<String, String> extra;
    public String id;
    public String packageName;
    public String payload;
    public String regId;
    public boolean requireAck;
    public Target target;
    public String type;
    private static final g STRUCT_DESC = new g("XmPushActionNotification");
    private static final a DEBUG_FIELD_DESC = new a("", (byte) 11, 1);
    private static final a TARGET_FIELD_DESC = new a("", (byte) 12, 2);
    private static final a ID_FIELD_DESC = new a("", (byte) 11, 3);
    private static final a APP_ID_FIELD_DESC = new a("", (byte) 11, 4);
    private static final a TYPE_FIELD_DESC = new a("", (byte) 11, 5);
    private static final a REQUIRE_ACK_FIELD_DESC = new a("", (byte) 2, 6);
    private static final a PAYLOAD_FIELD_DESC = new a("", (byte) 11, 7);
    private static final a EXTRA_FIELD_DESC = new a("", (byte) 13, 8);
    private static final a PACKAGE_NAME_FIELD_DESC = new a("", (byte) 11, 9);
    private static final a CATEGORY_FIELD_DESC = new a("", (byte) 11, 10);
    private static final a REG_ID_FIELD_DESC = new a("", (byte) 11, 12);
    private static final a ALIAS_NAME_FIELD_DESC = new a("", (byte) 11, 13);
    private static final a BINARY_EXTRA_FIELD_DESC = new a("", (byte) 11, 14);
    private static final a CREATED_TS_FIELD_DESC = new a("", (byte) 10, 15);
    private static final a ALREADY_LOG_CLICK_IN_XMQ_FIELD_DESC = new a("", (byte) 2, 20);

    public XmPushActionNotification() {
        this.__isset_bit_vector = new BitSet(3);
        this.requireAck = true;
        this.alreadyLogClickInXmq = false;
    }

    public ByteBuffer BufferForBinaryExtra() {
        return this.binaryExtra;
    }

    public void clear() {
        this.debug = null;
        this.target = null;
        this.id = null;
        this.appId = null;
        this.type = null;
        this.requireAck = true;
        this.payload = null;
        this.extra = null;
        this.packageName = null;
        this.category = null;
        this.regId = null;
        this.aliasName = null;
        this.binaryExtra = null;
        setCreatedTsIsSet(false);
        this.createdTs = 0L;
        this.alreadyLogClickInXmq = false;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof XmPushActionNotification)) {
            return equals((XmPushActionNotification) obj);
        }
        return false;
    }

    public String getAliasName() {
        return this.aliasName;
    }

    public String getAppId() {
        return this.appId;
    }

    public byte[] getBinaryExtra() {
        setBinaryExtra(g0.a.p(this.binaryExtra));
        return this.binaryExtra.array();
    }

    public String getCategory() {
        return this.category;
    }

    public long getCreatedTs() {
        return this.createdTs;
    }

    public String getDebug() {
        return this.debug;
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

    public String getPayload() {
        return this.payload;
    }

    public String getRegId() {
        return this.regId;
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

    public boolean isAlreadyLogClickInXmq() {
        return this.alreadyLogClickInXmq;
    }

    public boolean isRequireAck() {
        return this.requireAck;
    }

    public boolean isSetAliasName() {
        return this.aliasName != null;
    }

    public boolean isSetAlreadyLogClickInXmq() {
        return this.__isset_bit_vector.get(2);
    }

    public boolean isSetAppId() {
        return this.appId != null;
    }

    public boolean isSetBinaryExtra() {
        return this.binaryExtra != null;
    }

    public boolean isSetCategory() {
        return this.category != null;
    }

    public boolean isSetCreatedTs() {
        return this.__isset_bit_vector.get(1);
    }

    public boolean isSetDebug() {
        return this.debug != null;
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

    public boolean isSetPayload() {
        return this.payload != null;
    }

    public boolean isSetRegId() {
        return this.regId != null;
    }

    public boolean isSetRequireAck() {
        return this.__isset_bit_vector.get(0);
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
                if (isSetRequireAck()) {
                    validate();
                    return;
                }
                throw new TProtocolException("Required field 'requireAck' was not found in serialized data! Struct: " + toString());
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
                    if (b2 == 2) {
                        this.requireAck = dVar.b();
                        setRequireAckIsSet(true);
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 7:
                    if (b2 == 11) {
                        this.payload = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 8:
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
                case 9:
                    if (b2 == 11) {
                        this.packageName = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 10:
                    if (b2 == 11) {
                        this.category = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 11:
                case 16:
                case 17:
                case 18:
                case 19:
                default:
                    e.a(dVar, b2);
                    break;
                case 12:
                    if (b2 == 11) {
                        this.regId = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 13:
                    if (b2 == 11) {
                        this.aliasName = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 14:
                    if (b2 == 11) {
                        this.binaryExtra = dVar.a();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 15:
                    if (b2 == 10) {
                        this.createdTs = dVar.i();
                        setCreatedTsIsSet(true);
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 20:
                    if (b2 == 2) {
                        this.alreadyLogClickInXmq = dVar.b();
                        setAlreadyLogClickInXmqIsSet(true);
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
            }
            dVar.f();
        }
    }

    public XmPushActionNotification setAliasName(String str) {
        this.aliasName = str;
        return this;
    }

    public void setAliasNameIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.aliasName = null;
    }

    public XmPushActionNotification setAlreadyLogClickInXmq(boolean z2) {
        this.alreadyLogClickInXmq = z2;
        setAlreadyLogClickInXmqIsSet(true);
        return this;
    }

    public void setAlreadyLogClickInXmqIsSet(boolean z2) {
        this.__isset_bit_vector.set(2, z2);
    }

    public XmPushActionNotification setAppId(String str) {
        this.appId = str;
        return this;
    }

    public void setAppIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.appId = null;
    }

    public XmPushActionNotification setBinaryExtra(byte[] bArr) {
        setBinaryExtra(ByteBuffer.wrap(bArr));
        return this;
    }

    public void setBinaryExtraIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.binaryExtra = null;
    }

    public XmPushActionNotification setCategory(String str) {
        this.category = str;
        return this;
    }

    public void setCategoryIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.category = null;
    }

    public XmPushActionNotification setCreatedTs(long j2) {
        this.createdTs = j2;
        setCreatedTsIsSet(true);
        return this;
    }

    public void setCreatedTsIsSet(boolean z2) {
        this.__isset_bit_vector.set(1, z2);
    }

    public XmPushActionNotification setDebug(String str) {
        this.debug = str;
        return this;
    }

    public void setDebugIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.debug = null;
    }

    public XmPushActionNotification setExtra(Map<String, String> map) {
        this.extra = map;
        return this;
    }

    public void setExtraIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.extra = null;
    }

    public XmPushActionNotification setId(String str) {
        this.id = str;
        return this;
    }

    public void setIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.id = null;
    }

    public XmPushActionNotification setPackageName(String str) {
        this.packageName = str;
        return this;
    }

    public void setPackageNameIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.packageName = null;
    }

    public XmPushActionNotification setPayload(String str) {
        this.payload = str;
        return this;
    }

    public void setPayloadIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.payload = null;
    }

    public XmPushActionNotification setRegId(String str) {
        this.regId = str;
        return this;
    }

    public void setRegIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.regId = null;
    }

    public XmPushActionNotification setRequireAck(boolean z2) {
        this.requireAck = z2;
        setRequireAckIsSet(true);
        return this;
    }

    public void setRequireAckIsSet(boolean z2) {
        this.__isset_bit_vector.set(0, z2);
    }

    public XmPushActionNotification setTarget(Target target) {
        this.target = target;
        return this;
    }

    public void setTargetIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.target = null;
    }

    public XmPushActionNotification setType(String str) {
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
        StringBuilder sb = new StringBuilder("XmPushActionNotification(");
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
        sb.append(", ");
        sb.append("requireAck:");
        sb.append(this.requireAck);
        if (isSetPayload()) {
            sb.append(", ");
            sb.append("payload:");
            String str5 = this.payload;
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
        if (isSetRegId()) {
            sb.append(", ");
            sb.append("regId:");
            String str8 = this.regId;
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
        if (isSetBinaryExtra()) {
            sb.append(", ");
            sb.append("binaryExtra:");
            ByteBuffer byteBuffer = this.binaryExtra;
            if (byteBuffer == null) {
                sb.append("null");
            } else {
                g0.a.q(byteBuffer, sb);
            }
        }
        if (isSetCreatedTs()) {
            sb.append(", ");
            sb.append("createdTs:");
            sb.append(this.createdTs);
        }
        if (isSetAlreadyLogClickInXmq()) {
            sb.append(", ");
            sb.append("alreadyLogClickInXmq:");
            sb.append(this.alreadyLogClickInXmq);
        }
        sb.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
        return sb.toString();
    }

    public void unsetAliasName() {
        this.aliasName = null;
    }

    public void unsetAlreadyLogClickInXmq() {
        this.__isset_bit_vector.clear(2);
    }

    public void unsetAppId() {
        this.appId = null;
    }

    public void unsetBinaryExtra() {
        this.binaryExtra = null;
    }

    public void unsetCategory() {
        this.category = null;
    }

    public void unsetCreatedTs() {
        this.__isset_bit_vector.clear(1);
    }

    public void unsetDebug() {
        this.debug = null;
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

    public void unsetPayload() {
        this.payload = null;
    }

    public void unsetRegId() {
        this.regId = null;
    }

    public void unsetRequireAck() {
        this.__isset_bit_vector.clear(0);
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
        dVar.w(REQUIRE_ACK_FIELD_DESC);
        dVar.u(this.requireAck);
        dVar.x();
        if (this.payload != null && isSetPayload()) {
            dVar.w(PAYLOAD_FIELD_DESC);
            dVar.I(this.payload);
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
        if (this.regId != null && isSetRegId()) {
            dVar.w(REG_ID_FIELD_DESC);
            dVar.I(this.regId);
            dVar.x();
        }
        if (this.aliasName != null && isSetAliasName()) {
            dVar.w(ALIAS_NAME_FIELD_DESC);
            dVar.I(this.aliasName);
            dVar.x();
        }
        if (this.binaryExtra != null && isSetBinaryExtra()) {
            dVar.w(BINARY_EXTRA_FIELD_DESC);
            dVar.t(this.binaryExtra);
            dVar.x();
        }
        if (isSetCreatedTs()) {
            dVar.w(CREATED_TS_FIELD_DESC);
            dVar.B(this.createdTs);
            dVar.x();
        }
        if (isSetAlreadyLogClickInXmq()) {
            dVar.w(ALREADY_LOG_CLICK_IN_XMQ_FIELD_DESC);
            dVar.u(this.alreadyLogClickInXmq);
            dVar.x();
        }
        dVar.y();
        dVar.K();
    }

    @Override // java.lang.Comparable
    public int compareTo(XmPushActionNotification xmPushActionNotification) {
        int iL;
        int iE;
        int iF;
        int iG;
        int iG2;
        int iG3;
        int iG4;
        int i2;
        int iG5;
        int iL2;
        int iG6;
        int iG7;
        int iG8;
        int iF2;
        int iG9;
        if (!getClass().equals(xmPushActionNotification.getClass())) {
            return getClass().getName().compareTo(xmPushActionNotification.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetDebug()).compareTo(Boolean.valueOf(xmPushActionNotification.isSetDebug()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetDebug() && (iG9 = g0.a.g(this.debug, xmPushActionNotification.debug)) != 0) {
            return iG9;
        }
        int iCompareTo2 = Boolean.valueOf(isSetTarget()).compareTo(Boolean.valueOf(xmPushActionNotification.isSetTarget()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (isSetTarget() && (iF2 = g0.a.f(this.target, xmPushActionNotification.target)) != 0) {
            return iF2;
        }
        int iCompareTo3 = Boolean.valueOf(isSetId()).compareTo(Boolean.valueOf(xmPushActionNotification.isSetId()));
        if (iCompareTo3 != 0) {
            return iCompareTo3;
        }
        if (isSetId() && (iG8 = g0.a.g(this.id, xmPushActionNotification.id)) != 0) {
            return iG8;
        }
        int iCompareTo4 = Boolean.valueOf(isSetAppId()).compareTo(Boolean.valueOf(xmPushActionNotification.isSetAppId()));
        if (iCompareTo4 != 0) {
            return iCompareTo4;
        }
        if (isSetAppId() && (iG7 = g0.a.g(this.appId, xmPushActionNotification.appId)) != 0) {
            return iG7;
        }
        int iCompareTo5 = Boolean.valueOf(isSetType()).compareTo(Boolean.valueOf(xmPushActionNotification.isSetType()));
        if (iCompareTo5 != 0) {
            return iCompareTo5;
        }
        if (isSetType() && (iG6 = g0.a.g(this.type, xmPushActionNotification.type)) != 0) {
            return iG6;
        }
        int iCompareTo6 = Boolean.valueOf(isSetRequireAck()).compareTo(Boolean.valueOf(xmPushActionNotification.isSetRequireAck()));
        if (iCompareTo6 != 0) {
            return iCompareTo6;
        }
        if (isSetRequireAck() && (iL2 = g0.a.l(this.requireAck, xmPushActionNotification.requireAck)) != 0) {
            return iL2;
        }
        int iCompareTo7 = Boolean.valueOf(isSetPayload()).compareTo(Boolean.valueOf(xmPushActionNotification.isSetPayload()));
        if (iCompareTo7 != 0) {
            return iCompareTo7;
        }
        if (isSetPayload() && (iG5 = g0.a.g(this.payload, xmPushActionNotification.payload)) != 0) {
            return iG5;
        }
        int iCompareTo8 = Boolean.valueOf(isSetExtra()).compareTo(Boolean.valueOf(xmPushActionNotification.isSetExtra()));
        if (iCompareTo8 != 0) {
            return iCompareTo8;
        }
        if (isSetExtra() && (i2 = g0.a.i(this.extra, xmPushActionNotification.extra)) != 0) {
            return i2;
        }
        int iCompareTo9 = Boolean.valueOf(isSetPackageName()).compareTo(Boolean.valueOf(xmPushActionNotification.isSetPackageName()));
        if (iCompareTo9 != 0) {
            return iCompareTo9;
        }
        if (isSetPackageName() && (iG4 = g0.a.g(this.packageName, xmPushActionNotification.packageName)) != 0) {
            return iG4;
        }
        int iCompareTo10 = Boolean.valueOf(isSetCategory()).compareTo(Boolean.valueOf(xmPushActionNotification.isSetCategory()));
        if (iCompareTo10 != 0) {
            return iCompareTo10;
        }
        if (isSetCategory() && (iG3 = g0.a.g(this.category, xmPushActionNotification.category)) != 0) {
            return iG3;
        }
        int iCompareTo11 = Boolean.valueOf(isSetRegId()).compareTo(Boolean.valueOf(xmPushActionNotification.isSetRegId()));
        if (iCompareTo11 != 0) {
            return iCompareTo11;
        }
        if (isSetRegId() && (iG2 = g0.a.g(this.regId, xmPushActionNotification.regId)) != 0) {
            return iG2;
        }
        int iCompareTo12 = Boolean.valueOf(isSetAliasName()).compareTo(Boolean.valueOf(xmPushActionNotification.isSetAliasName()));
        if (iCompareTo12 != 0) {
            return iCompareTo12;
        }
        if (isSetAliasName() && (iG = g0.a.g(this.aliasName, xmPushActionNotification.aliasName)) != 0) {
            return iG;
        }
        int iCompareTo13 = Boolean.valueOf(isSetBinaryExtra()).compareTo(Boolean.valueOf(xmPushActionNotification.isSetBinaryExtra()));
        if (iCompareTo13 != 0) {
            return iCompareTo13;
        }
        if (isSetBinaryExtra() && (iF = g0.a.f(this.binaryExtra, xmPushActionNotification.binaryExtra)) != 0) {
            return iF;
        }
        int iCompareTo14 = Boolean.valueOf(isSetCreatedTs()).compareTo(Boolean.valueOf(xmPushActionNotification.isSetCreatedTs()));
        if (iCompareTo14 != 0) {
            return iCompareTo14;
        }
        if (isSetCreatedTs() && (iE = g0.a.e(this.createdTs, xmPushActionNotification.createdTs)) != 0) {
            return iE;
        }
        int iCompareTo15 = Boolean.valueOf(isSetAlreadyLogClickInXmq()).compareTo(Boolean.valueOf(xmPushActionNotification.isSetAlreadyLogClickInXmq()));
        if (iCompareTo15 != 0) {
            return iCompareTo15;
        }
        if (!isSetAlreadyLogClickInXmq() || (iL = g0.a.l(this.alreadyLogClickInXmq, xmPushActionNotification.alreadyLogClickInXmq)) == 0) {
            return 0;
        }
        return iL;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public XmPushActionNotification m37deepCopy() {
        return new XmPushActionNotification(this);
    }

    public XmPushActionNotification setBinaryExtra(ByteBuffer byteBuffer) {
        this.binaryExtra = byteBuffer;
        return this;
    }

    public boolean equals(XmPushActionNotification xmPushActionNotification) {
        if (xmPushActionNotification == null) {
            return false;
        }
        boolean zIsSetDebug = isSetDebug();
        boolean zIsSetDebug2 = xmPushActionNotification.isSetDebug();
        if ((zIsSetDebug || zIsSetDebug2) && !(zIsSetDebug && zIsSetDebug2 && this.debug.equals(xmPushActionNotification.debug))) {
            return false;
        }
        boolean zIsSetTarget = isSetTarget();
        boolean zIsSetTarget2 = xmPushActionNotification.isSetTarget();
        if ((zIsSetTarget || zIsSetTarget2) && !(zIsSetTarget && zIsSetTarget2 && this.target.equals(xmPushActionNotification.target))) {
            return false;
        }
        boolean zIsSetId = isSetId();
        boolean zIsSetId2 = xmPushActionNotification.isSetId();
        if ((zIsSetId || zIsSetId2) && !(zIsSetId && zIsSetId2 && this.id.equals(xmPushActionNotification.id))) {
            return false;
        }
        boolean zIsSetAppId = isSetAppId();
        boolean zIsSetAppId2 = xmPushActionNotification.isSetAppId();
        if ((zIsSetAppId || zIsSetAppId2) && !(zIsSetAppId && zIsSetAppId2 && this.appId.equals(xmPushActionNotification.appId))) {
            return false;
        }
        boolean zIsSetType = isSetType();
        boolean zIsSetType2 = xmPushActionNotification.isSetType();
        if (((zIsSetType || zIsSetType2) && !(zIsSetType && zIsSetType2 && this.type.equals(xmPushActionNotification.type))) || this.requireAck != xmPushActionNotification.requireAck) {
            return false;
        }
        boolean zIsSetPayload = isSetPayload();
        boolean zIsSetPayload2 = xmPushActionNotification.isSetPayload();
        if ((zIsSetPayload || zIsSetPayload2) && !(zIsSetPayload && zIsSetPayload2 && this.payload.equals(xmPushActionNotification.payload))) {
            return false;
        }
        boolean zIsSetExtra = isSetExtra();
        boolean zIsSetExtra2 = xmPushActionNotification.isSetExtra();
        if ((zIsSetExtra || zIsSetExtra2) && !(zIsSetExtra && zIsSetExtra2 && this.extra.equals(xmPushActionNotification.extra))) {
            return false;
        }
        boolean zIsSetPackageName = isSetPackageName();
        boolean zIsSetPackageName2 = xmPushActionNotification.isSetPackageName();
        if ((zIsSetPackageName || zIsSetPackageName2) && !(zIsSetPackageName && zIsSetPackageName2 && this.packageName.equals(xmPushActionNotification.packageName))) {
            return false;
        }
        boolean zIsSetCategory = isSetCategory();
        boolean zIsSetCategory2 = xmPushActionNotification.isSetCategory();
        if ((zIsSetCategory || zIsSetCategory2) && !(zIsSetCategory && zIsSetCategory2 && this.category.equals(xmPushActionNotification.category))) {
            return false;
        }
        boolean zIsSetRegId = isSetRegId();
        boolean zIsSetRegId2 = xmPushActionNotification.isSetRegId();
        if ((zIsSetRegId || zIsSetRegId2) && !(zIsSetRegId && zIsSetRegId2 && this.regId.equals(xmPushActionNotification.regId))) {
            return false;
        }
        boolean zIsSetAliasName = isSetAliasName();
        boolean zIsSetAliasName2 = xmPushActionNotification.isSetAliasName();
        if ((zIsSetAliasName || zIsSetAliasName2) && !(zIsSetAliasName && zIsSetAliasName2 && this.aliasName.equals(xmPushActionNotification.aliasName))) {
            return false;
        }
        boolean zIsSetBinaryExtra = isSetBinaryExtra();
        boolean zIsSetBinaryExtra2 = xmPushActionNotification.isSetBinaryExtra();
        if ((zIsSetBinaryExtra || zIsSetBinaryExtra2) && !(zIsSetBinaryExtra && zIsSetBinaryExtra2 && this.binaryExtra.equals(xmPushActionNotification.binaryExtra))) {
            return false;
        }
        boolean zIsSetCreatedTs = isSetCreatedTs();
        boolean zIsSetCreatedTs2 = xmPushActionNotification.isSetCreatedTs();
        if ((zIsSetCreatedTs || zIsSetCreatedTs2) && !(zIsSetCreatedTs && zIsSetCreatedTs2 && this.createdTs == xmPushActionNotification.createdTs)) {
            return false;
        }
        boolean zIsSetAlreadyLogClickInXmq = isSetAlreadyLogClickInXmq();
        boolean zIsSetAlreadyLogClickInXmq2 = xmPushActionNotification.isSetAlreadyLogClickInXmq();
        if (zIsSetAlreadyLogClickInXmq || zIsSetAlreadyLogClickInXmq2) {
            return zIsSetAlreadyLogClickInXmq && zIsSetAlreadyLogClickInXmq2 && this.alreadyLogClickInXmq == xmPushActionNotification.alreadyLogClickInXmq;
        }
        return true;
    }

    public XmPushActionNotification(String str, boolean z2) {
        this();
        this.id = str;
        this.requireAck = z2;
        setRequireAckIsSet(true);
    }

    public XmPushActionNotification(XmPushActionNotification xmPushActionNotification) {
        BitSet bitSet = new BitSet(3);
        this.__isset_bit_vector = bitSet;
        bitSet.clear();
        this.__isset_bit_vector.or(xmPushActionNotification.__isset_bit_vector);
        if (xmPushActionNotification.isSetDebug()) {
            this.debug = xmPushActionNotification.debug;
        }
        if (xmPushActionNotification.isSetTarget()) {
            this.target = new Target(xmPushActionNotification.target);
        }
        if (xmPushActionNotification.isSetId()) {
            this.id = xmPushActionNotification.id;
        }
        if (xmPushActionNotification.isSetAppId()) {
            this.appId = xmPushActionNotification.appId;
        }
        if (xmPushActionNotification.isSetType()) {
            this.type = xmPushActionNotification.type;
        }
        this.requireAck = xmPushActionNotification.requireAck;
        if (xmPushActionNotification.isSetPayload()) {
            this.payload = xmPushActionNotification.payload;
        }
        if (xmPushActionNotification.isSetExtra()) {
            HashMap map = new HashMap();
            for (Map.Entry<String, String> entry : xmPushActionNotification.extra.entrySet()) {
                map.put(entry.getKey(), entry.getValue());
            }
            this.extra = map;
        }
        if (xmPushActionNotification.isSetPackageName()) {
            this.packageName = xmPushActionNotification.packageName;
        }
        if (xmPushActionNotification.isSetCategory()) {
            this.category = xmPushActionNotification.category;
        }
        if (xmPushActionNotification.isSetRegId()) {
            this.regId = xmPushActionNotification.regId;
        }
        if (xmPushActionNotification.isSetAliasName()) {
            this.aliasName = xmPushActionNotification.aliasName;
        }
        if (xmPushActionNotification.isSetBinaryExtra()) {
            this.binaryExtra = g0.a.n(xmPushActionNotification.binaryExtra);
        }
        this.createdTs = xmPushActionNotification.createdTs;
        this.alreadyLogClickInXmq = xmPushActionNotification.alreadyLogClickInXmq;
    }
}
