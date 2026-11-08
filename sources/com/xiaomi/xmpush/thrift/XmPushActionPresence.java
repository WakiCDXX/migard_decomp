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
public class XmPushActionPresence implements TBase<XmPushActionPresence, Object>, Serializable, Cloneable {
    private static final int __ISONLINE_ISSET_ID = 0;
    private static final int __SID_ISSET_ID = 1;
    private BitSet __isset_bit_vector;
    public String appId;
    public String appVersion;
    public String debug;
    public String from;
    public String id;
    public boolean isOnline;
    public Map<String, String> params;
    public String sdkVersion;
    public ByteBuffer sessionSecurity;
    public long sid;
    public Target target;
    private static final g STRUCT_DESC = new g("XmPushActionPresence");
    private static final a DEBUG_FIELD_DESC = new a("", (byte) 11, 1);
    private static final a TARGET_FIELD_DESC = new a("", (byte) 12, 2);
    private static final a ID_FIELD_DESC = new a("", (byte) 11, 3);
    private static final a APP_ID_FIELD_DESC = new a("", (byte) 11, 4);
    private static final a APP_VERSION_FIELD_DESC = new a("", (byte) 11, 5);
    private static final a SDK_VERSION_FIELD_DESC = new a("", (byte) 11, 6);
    private static final a PARAMS_FIELD_DESC = new a("", (byte) 13, 7);
    private static final a IS_ONLINE_FIELD_DESC = new a("", (byte) 2, 8);
    private static final a FROM_FIELD_DESC = new a("", (byte) 11, 9);
    private static final a SESSION_SECURITY_FIELD_DESC = new a("", (byte) 11, 10);
    private static final a SID_FIELD_DESC = new a("", (byte) 10, 11);

    public XmPushActionPresence() {
        this.__isset_bit_vector = new BitSet(2);
        this.sid = 0L;
    }

    public ByteBuffer BufferForSessionSecurity() {
        return this.sessionSecurity;
    }

    public void clear() {
        this.debug = null;
        this.target = null;
        this.id = null;
        this.appId = null;
        this.appVersion = null;
        this.sdkVersion = null;
        this.params = null;
        setIsOnlineIsSet(false);
        this.isOnline = false;
        this.from = null;
        this.sessionSecurity = null;
        this.sid = 0L;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof XmPushActionPresence)) {
            return equals((XmPushActionPresence) obj);
        }
        return false;
    }

    public String getAppId() {
        return this.appId;
    }

    public String getAppVersion() {
        return this.appVersion;
    }

    public String getDebug() {
        return this.debug;
    }

    public String getFrom() {
        return this.from;
    }

    public String getId() {
        return this.id;
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

    public String getSdkVersion() {
        return this.sdkVersion;
    }

    public byte[] getSessionSecurity() {
        setSessionSecurity(g0.a.p(this.sessionSecurity));
        return this.sessionSecurity.array();
    }

    public long getSid() {
        return this.sid;
    }

    public Target getTarget() {
        return this.target;
    }

    public int hashCode() {
        return 0;
    }

    public boolean isIsOnline() {
        return this.isOnline;
    }

    public boolean isSetAppId() {
        return this.appId != null;
    }

    public boolean isSetAppVersion() {
        return this.appVersion != null;
    }

    public boolean isSetDebug() {
        return this.debug != null;
    }

    public boolean isSetFrom() {
        return this.from != null;
    }

    public boolean isSetId() {
        return this.id != null;
    }

    public boolean isSetIsOnline() {
        return this.__isset_bit_vector.get(0);
    }

    public boolean isSetParams() {
        return this.params != null;
    }

    public boolean isSetSdkVersion() {
        return this.sdkVersion != null;
    }

    public boolean isSetSessionSecurity() {
        return this.sessionSecurity != null;
    }

    public boolean isSetSid() {
        return this.__isset_bit_vector.get(1);
    }

    public boolean isSetTarget() {
        return this.target != null;
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
                        this.appVersion = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 6:
                    if (b2 == 11) {
                        this.sdkVersion = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 7:
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
                case 8:
                    if (b2 == 2) {
                        this.isOnline = dVar.b();
                        setIsOnlineIsSet(true);
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 9:
                    if (b2 == 11) {
                        this.from = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 10:
                    if (b2 == 11) {
                        this.sessionSecurity = dVar.a();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 11:
                    if (b2 == 10) {
                        this.sid = dVar.i();
                        setSidIsSet(true);
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

    public XmPushActionPresence setAppId(String str) {
        this.appId = str;
        return this;
    }

    public void setAppIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.appId = null;
    }

    public XmPushActionPresence setAppVersion(String str) {
        this.appVersion = str;
        return this;
    }

    public void setAppVersionIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.appVersion = null;
    }

    public XmPushActionPresence setDebug(String str) {
        this.debug = str;
        return this;
    }

    public void setDebugIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.debug = null;
    }

    public XmPushActionPresence setFrom(String str) {
        this.from = str;
        return this;
    }

    public void setFromIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.from = null;
    }

    public XmPushActionPresence setId(String str) {
        this.id = str;
        return this;
    }

    public void setIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.id = null;
    }

    public XmPushActionPresence setIsOnline(boolean z2) {
        this.isOnline = z2;
        setIsOnlineIsSet(true);
        return this;
    }

    public void setIsOnlineIsSet(boolean z2) {
        this.__isset_bit_vector.set(0, z2);
    }

    public XmPushActionPresence setParams(Map<String, String> map) {
        this.params = map;
        return this;
    }

    public void setParamsIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.params = null;
    }

    public XmPushActionPresence setSdkVersion(String str) {
        this.sdkVersion = str;
        return this;
    }

    public void setSdkVersionIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.sdkVersion = null;
    }

    public XmPushActionPresence setSessionSecurity(byte[] bArr) {
        setSessionSecurity(ByteBuffer.wrap(bArr));
        return this;
    }

    public void setSessionSecurityIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.sessionSecurity = null;
    }

    public XmPushActionPresence setSid(long j2) {
        this.sid = j2;
        setSidIsSet(true);
        return this;
    }

    public void setSidIsSet(boolean z2) {
        this.__isset_bit_vector.set(1, z2);
    }

    public XmPushActionPresence setTarget(Target target) {
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
        StringBuilder sb = new StringBuilder("XmPushActionPresence(");
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
        if (isSetAppVersion()) {
            sb.append(", ");
            sb.append("appVersion:");
            String str4 = this.appVersion;
            if (str4 == null) {
                sb.append("null");
            } else {
                sb.append(str4);
            }
        }
        if (isSetSdkVersion()) {
            sb.append(", ");
            sb.append("sdkVersion:");
            String str5 = this.sdkVersion;
            if (str5 == null) {
                sb.append("null");
            } else {
                sb.append(str5);
            }
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
        if (isSetIsOnline()) {
            sb.append(", ");
            sb.append("isOnline:");
            sb.append(this.isOnline);
        }
        if (isSetFrom()) {
            sb.append(", ");
            sb.append("from:");
            String str6 = this.from;
            if (str6 == null) {
                sb.append("null");
            } else {
                sb.append(str6);
            }
        }
        if (isSetSessionSecurity()) {
            sb.append(", ");
            sb.append("sessionSecurity:");
            ByteBuffer byteBuffer = this.sessionSecurity;
            if (byteBuffer == null) {
                sb.append("null");
            } else {
                g0.a.q(byteBuffer, sb);
            }
        }
        if (isSetSid()) {
            sb.append(", ");
            sb.append("sid:");
            sb.append(this.sid);
        }
        sb.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
        return sb.toString();
    }

    public void unsetAppId() {
        this.appId = null;
    }

    public void unsetAppVersion() {
        this.appVersion = null;
    }

    public void unsetDebug() {
        this.debug = null;
    }

    public void unsetFrom() {
        this.from = null;
    }

    public void unsetId() {
        this.id = null;
    }

    public void unsetIsOnline() {
        this.__isset_bit_vector.clear(0);
    }

    public void unsetParams() {
        this.params = null;
    }

    public void unsetSdkVersion() {
        this.sdkVersion = null;
    }

    public void unsetSessionSecurity() {
        this.sessionSecurity = null;
    }

    public void unsetSid() {
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
        if (this.appVersion != null && isSetAppVersion()) {
            dVar.w(APP_VERSION_FIELD_DESC);
            dVar.I(this.appVersion);
            dVar.x();
        }
        if (this.sdkVersion != null && isSetSdkVersion()) {
            dVar.w(SDK_VERSION_FIELD_DESC);
            dVar.I(this.sdkVersion);
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
        if (isSetIsOnline()) {
            dVar.w(IS_ONLINE_FIELD_DESC);
            dVar.u(this.isOnline);
            dVar.x();
        }
        if (this.from != null && isSetFrom()) {
            dVar.w(FROM_FIELD_DESC);
            dVar.I(this.from);
            dVar.x();
        }
        if (this.sessionSecurity != null && isSetSessionSecurity()) {
            dVar.w(SESSION_SECURITY_FIELD_DESC);
            dVar.t(this.sessionSecurity);
            dVar.x();
        }
        if (isSetSid()) {
            dVar.w(SID_FIELD_DESC);
            dVar.B(this.sid);
            dVar.x();
        }
        dVar.y();
        dVar.K();
    }

    @Override // java.lang.Comparable
    public int compareTo(XmPushActionPresence xmPushActionPresence) {
        int iE;
        int iF;
        int iG;
        int iL;
        int i2;
        int iG2;
        int iG3;
        int iG4;
        int iG5;
        int iF2;
        int iG6;
        if (!getClass().equals(xmPushActionPresence.getClass())) {
            return getClass().getName().compareTo(xmPushActionPresence.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetDebug()).compareTo(Boolean.valueOf(xmPushActionPresence.isSetDebug()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetDebug() && (iG6 = g0.a.g(this.debug, xmPushActionPresence.debug)) != 0) {
            return iG6;
        }
        int iCompareTo2 = Boolean.valueOf(isSetTarget()).compareTo(Boolean.valueOf(xmPushActionPresence.isSetTarget()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (isSetTarget() && (iF2 = g0.a.f(this.target, xmPushActionPresence.target)) != 0) {
            return iF2;
        }
        int iCompareTo3 = Boolean.valueOf(isSetId()).compareTo(Boolean.valueOf(xmPushActionPresence.isSetId()));
        if (iCompareTo3 != 0) {
            return iCompareTo3;
        }
        if (isSetId() && (iG5 = g0.a.g(this.id, xmPushActionPresence.id)) != 0) {
            return iG5;
        }
        int iCompareTo4 = Boolean.valueOf(isSetAppId()).compareTo(Boolean.valueOf(xmPushActionPresence.isSetAppId()));
        if (iCompareTo4 != 0) {
            return iCompareTo4;
        }
        if (isSetAppId() && (iG4 = g0.a.g(this.appId, xmPushActionPresence.appId)) != 0) {
            return iG4;
        }
        int iCompareTo5 = Boolean.valueOf(isSetAppVersion()).compareTo(Boolean.valueOf(xmPushActionPresence.isSetAppVersion()));
        if (iCompareTo5 != 0) {
            return iCompareTo5;
        }
        if (isSetAppVersion() && (iG3 = g0.a.g(this.appVersion, xmPushActionPresence.appVersion)) != 0) {
            return iG3;
        }
        int iCompareTo6 = Boolean.valueOf(isSetSdkVersion()).compareTo(Boolean.valueOf(xmPushActionPresence.isSetSdkVersion()));
        if (iCompareTo6 != 0) {
            return iCompareTo6;
        }
        if (isSetSdkVersion() && (iG2 = g0.a.g(this.sdkVersion, xmPushActionPresence.sdkVersion)) != 0) {
            return iG2;
        }
        int iCompareTo7 = Boolean.valueOf(isSetParams()).compareTo(Boolean.valueOf(xmPushActionPresence.isSetParams()));
        if (iCompareTo7 != 0) {
            return iCompareTo7;
        }
        if (isSetParams() && (i2 = g0.a.i(this.params, xmPushActionPresence.params)) != 0) {
            return i2;
        }
        int iCompareTo8 = Boolean.valueOf(isSetIsOnline()).compareTo(Boolean.valueOf(xmPushActionPresence.isSetIsOnline()));
        if (iCompareTo8 != 0) {
            return iCompareTo8;
        }
        if (isSetIsOnline() && (iL = g0.a.l(this.isOnline, xmPushActionPresence.isOnline)) != 0) {
            return iL;
        }
        int iCompareTo9 = Boolean.valueOf(isSetFrom()).compareTo(Boolean.valueOf(xmPushActionPresence.isSetFrom()));
        if (iCompareTo9 != 0) {
            return iCompareTo9;
        }
        if (isSetFrom() && (iG = g0.a.g(this.from, xmPushActionPresence.from)) != 0) {
            return iG;
        }
        int iCompareTo10 = Boolean.valueOf(isSetSessionSecurity()).compareTo(Boolean.valueOf(xmPushActionPresence.isSetSessionSecurity()));
        if (iCompareTo10 != 0) {
            return iCompareTo10;
        }
        if (isSetSessionSecurity() && (iF = g0.a.f(this.sessionSecurity, xmPushActionPresence.sessionSecurity)) != 0) {
            return iF;
        }
        int iCompareTo11 = Boolean.valueOf(isSetSid()).compareTo(Boolean.valueOf(xmPushActionPresence.isSetSid()));
        if (iCompareTo11 != 0) {
            return iCompareTo11;
        }
        if (!isSetSid() || (iE = g0.a.e(this.sid, xmPushActionPresence.sid)) == 0) {
            return 0;
        }
        return iE;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public XmPushActionPresence m38deepCopy() {
        return new XmPushActionPresence(this);
    }

    public XmPushActionPresence setSessionSecurity(ByteBuffer byteBuffer) {
        this.sessionSecurity = byteBuffer;
        return this;
    }

    public boolean equals(XmPushActionPresence xmPushActionPresence) {
        if (xmPushActionPresence == null) {
            return false;
        }
        boolean zIsSetDebug = isSetDebug();
        boolean zIsSetDebug2 = xmPushActionPresence.isSetDebug();
        if ((zIsSetDebug || zIsSetDebug2) && !(zIsSetDebug && zIsSetDebug2 && this.debug.equals(xmPushActionPresence.debug))) {
            return false;
        }
        boolean zIsSetTarget = isSetTarget();
        boolean zIsSetTarget2 = xmPushActionPresence.isSetTarget();
        if ((zIsSetTarget || zIsSetTarget2) && !(zIsSetTarget && zIsSetTarget2 && this.target.equals(xmPushActionPresence.target))) {
            return false;
        }
        boolean zIsSetId = isSetId();
        boolean zIsSetId2 = xmPushActionPresence.isSetId();
        if ((zIsSetId || zIsSetId2) && !(zIsSetId && zIsSetId2 && this.id.equals(xmPushActionPresence.id))) {
            return false;
        }
        boolean zIsSetAppId = isSetAppId();
        boolean zIsSetAppId2 = xmPushActionPresence.isSetAppId();
        if ((zIsSetAppId || zIsSetAppId2) && !(zIsSetAppId && zIsSetAppId2 && this.appId.equals(xmPushActionPresence.appId))) {
            return false;
        }
        boolean zIsSetAppVersion = isSetAppVersion();
        boolean zIsSetAppVersion2 = xmPushActionPresence.isSetAppVersion();
        if ((zIsSetAppVersion || zIsSetAppVersion2) && !(zIsSetAppVersion && zIsSetAppVersion2 && this.appVersion.equals(xmPushActionPresence.appVersion))) {
            return false;
        }
        boolean zIsSetSdkVersion = isSetSdkVersion();
        boolean zIsSetSdkVersion2 = xmPushActionPresence.isSetSdkVersion();
        if ((zIsSetSdkVersion || zIsSetSdkVersion2) && !(zIsSetSdkVersion && zIsSetSdkVersion2 && this.sdkVersion.equals(xmPushActionPresence.sdkVersion))) {
            return false;
        }
        boolean zIsSetParams = isSetParams();
        boolean zIsSetParams2 = xmPushActionPresence.isSetParams();
        if ((zIsSetParams || zIsSetParams2) && !(zIsSetParams && zIsSetParams2 && this.params.equals(xmPushActionPresence.params))) {
            return false;
        }
        boolean zIsSetIsOnline = isSetIsOnline();
        boolean zIsSetIsOnline2 = xmPushActionPresence.isSetIsOnline();
        if ((zIsSetIsOnline || zIsSetIsOnline2) && !(zIsSetIsOnline && zIsSetIsOnline2 && this.isOnline == xmPushActionPresence.isOnline)) {
            return false;
        }
        boolean zIsSetFrom = isSetFrom();
        boolean zIsSetFrom2 = xmPushActionPresence.isSetFrom();
        if ((zIsSetFrom || zIsSetFrom2) && !(zIsSetFrom && zIsSetFrom2 && this.from.equals(xmPushActionPresence.from))) {
            return false;
        }
        boolean zIsSetSessionSecurity = isSetSessionSecurity();
        boolean zIsSetSessionSecurity2 = xmPushActionPresence.isSetSessionSecurity();
        if ((zIsSetSessionSecurity || zIsSetSessionSecurity2) && !(zIsSetSessionSecurity && zIsSetSessionSecurity2 && this.sessionSecurity.equals(xmPushActionPresence.sessionSecurity))) {
            return false;
        }
        boolean zIsSetSid = isSetSid();
        boolean zIsSetSid2 = xmPushActionPresence.isSetSid();
        if (zIsSetSid || zIsSetSid2) {
            return zIsSetSid && zIsSetSid2 && this.sid == xmPushActionPresence.sid;
        }
        return true;
    }

    public XmPushActionPresence(String str, String str2) {
        this();
        this.id = str;
        this.appId = str2;
    }

    public XmPushActionPresence(XmPushActionPresence xmPushActionPresence) {
        BitSet bitSet = new BitSet(2);
        this.__isset_bit_vector = bitSet;
        bitSet.clear();
        this.__isset_bit_vector.or(xmPushActionPresence.__isset_bit_vector);
        if (xmPushActionPresence.isSetDebug()) {
            this.debug = xmPushActionPresence.debug;
        }
        if (xmPushActionPresence.isSetTarget()) {
            this.target = new Target(xmPushActionPresence.target);
        }
        if (xmPushActionPresence.isSetId()) {
            this.id = xmPushActionPresence.id;
        }
        if (xmPushActionPresence.isSetAppId()) {
            this.appId = xmPushActionPresence.appId;
        }
        if (xmPushActionPresence.isSetAppVersion()) {
            this.appVersion = xmPushActionPresence.appVersion;
        }
        if (xmPushActionPresence.isSetSdkVersion()) {
            this.sdkVersion = xmPushActionPresence.sdkVersion;
        }
        if (xmPushActionPresence.isSetParams()) {
            HashMap map = new HashMap();
            for (Map.Entry<String, String> entry : xmPushActionPresence.params.entrySet()) {
                map.put(entry.getKey(), entry.getValue());
            }
            this.params = map;
        }
        this.isOnline = xmPushActionPresence.isOnline;
        if (xmPushActionPresence.isSetFrom()) {
            this.from = xmPushActionPresence.from;
        }
        if (xmPushActionPresence.isSetSessionSecurity()) {
            this.sessionSecurity = g0.a.n(xmPushActionPresence.sessionSecurity);
        }
        this.sid = xmPushActionPresence.sid;
    }
}
