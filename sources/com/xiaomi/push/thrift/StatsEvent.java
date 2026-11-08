package com.xiaomi.push.thrift;

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
public class StatsEvent implements TBase<StatsEvent, Object>, Serializable, Cloneable {
    private static final int __CHID_ISSET_ID = 0;
    private static final int __CLIENTIP_ISSET_ID = 5;
    private static final int __SUBVALUE_ISSET_ID = 3;
    private static final int __TIME_ISSET_ID = 4;
    private static final int __TYPE_ISSET_ID = 1;
    private static final int __VALUE_ISSET_ID = 2;
    private BitSet __isset_bit_vector;
    public String annotation;
    public byte chid;
    public int clientIp;
    public String connpt;
    public String host;
    public int subvalue;
    public int time;
    public int type;
    public String user;
    public int value;
    private static final g STRUCT_DESC = new g("StatsEvent");
    private static final a CHID_FIELD_DESC = new a("", (byte) 3, 1);
    private static final a TYPE_FIELD_DESC = new a("", (byte) 8, 2);
    private static final a VALUE_FIELD_DESC = new a("", (byte) 8, 3);
    private static final a CONNPT_FIELD_DESC = new a("", (byte) 11, 4);
    private static final a HOST_FIELD_DESC = new a("", (byte) 11, 5);
    private static final a SUBVALUE_FIELD_DESC = new a("", (byte) 8, 6);
    private static final a ANNOTATION_FIELD_DESC = new a("", (byte) 11, 7);
    private static final a USER_FIELD_DESC = new a("", (byte) 11, 8);
    private static final a TIME_FIELD_DESC = new a("", (byte) 8, 9);
    private static final a CLIENT_IP_FIELD_DESC = new a("", (byte) 8, 10);

    public StatsEvent() {
        this.__isset_bit_vector = new BitSet(6);
    }

    public void clear() {
        setChidIsSet(false);
        this.chid = (byte) 0;
        setTypeIsSet(false);
        this.type = 0;
        setValueIsSet(false);
        this.value = 0;
        this.connpt = null;
        this.host = null;
        setSubvalueIsSet(false);
        this.subvalue = 0;
        this.annotation = null;
        this.user = null;
        setTimeIsSet(false);
        this.time = 0;
        setClientIpIsSet(false);
        this.clientIp = 0;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof StatsEvent)) {
            return equals((StatsEvent) obj);
        }
        return false;
    }

    public String getAnnotation() {
        return this.annotation;
    }

    public byte getChid() {
        return this.chid;
    }

    public int getClientIp() {
        return this.clientIp;
    }

    public String getConnpt() {
        return this.connpt;
    }

    public String getHost() {
        return this.host;
    }

    public int getSubvalue() {
        return this.subvalue;
    }

    public int getTime() {
        return this.time;
    }

    public int getType() {
        return this.type;
    }

    public String getUser() {
        return this.user;
    }

    public int getValue() {
        return this.value;
    }

    public int hashCode() {
        return 0;
    }

    public boolean isSetAnnotation() {
        return this.annotation != null;
    }

    public boolean isSetChid() {
        return this.__isset_bit_vector.get(0);
    }

    public boolean isSetClientIp() {
        return this.__isset_bit_vector.get(5);
    }

    public boolean isSetConnpt() {
        return this.connpt != null;
    }

    public boolean isSetHost() {
        return this.host != null;
    }

    public boolean isSetSubvalue() {
        return this.__isset_bit_vector.get(3);
    }

    public boolean isSetTime() {
        return this.__isset_bit_vector.get(4);
    }

    public boolean isSetType() {
        return this.__isset_bit_vector.get(1);
    }

    public boolean isSetUser() {
        return this.user != null;
    }

    public boolean isSetValue() {
        return this.__isset_bit_vector.get(2);
    }

    @Override // org.apache.thrift.TBase
    public void read(d dVar) throws TException {
        dVar.q();
        while (true) {
            a aVarE = dVar.e();
            byte b2 = aVarE.f2150b;
            if (b2 == 0) {
                dVar.r();
                if (!isSetChid()) {
                    throw new TProtocolException("Required field 'chid' was not found in serialized data! Struct: " + toString());
                }
                if (!isSetType()) {
                    throw new TProtocolException("Required field 'type' was not found in serialized data! Struct: " + toString());
                }
                if (isSetValue()) {
                    validate();
                    return;
                }
                throw new TProtocolException("Required field 'value' was not found in serialized data! Struct: " + toString());
            }
            switch (aVarE.f2151c) {
                case 1:
                    if (b2 != 3) {
                        e.a(dVar, b2);
                    } else {
                        this.chid = dVar.c();
                        setChidIsSet(true);
                    }
                    break;
                case 2:
                    if (b2 != 8) {
                        e.a(dVar, b2);
                    } else {
                        this.type = dVar.h();
                        setTypeIsSet(true);
                    }
                    break;
                case 3:
                    if (b2 != 8) {
                        e.a(dVar, b2);
                    } else {
                        this.value = dVar.h();
                        setValueIsSet(true);
                    }
                    break;
                case 4:
                    if (b2 != 11) {
                        e.a(dVar, b2);
                    } else {
                        this.connpt = dVar.p();
                    }
                    break;
                case 5:
                    if (b2 != 11) {
                        e.a(dVar, b2);
                    } else {
                        this.host = dVar.p();
                    }
                    break;
                case 6:
                    if (b2 != 8) {
                        e.a(dVar, b2);
                    } else {
                        this.subvalue = dVar.h();
                        setSubvalueIsSet(true);
                    }
                    break;
                case 7:
                    if (b2 != 11) {
                        e.a(dVar, b2);
                    } else {
                        this.annotation = dVar.p();
                    }
                    break;
                case 8:
                    if (b2 != 11) {
                        e.a(dVar, b2);
                    } else {
                        this.user = dVar.p();
                    }
                    break;
                case 9:
                    if (b2 != 8) {
                        e.a(dVar, b2);
                    } else {
                        this.time = dVar.h();
                        setTimeIsSet(true);
                    }
                    break;
                case 10:
                    if (b2 != 8) {
                        e.a(dVar, b2);
                    } else {
                        this.clientIp = dVar.h();
                        setClientIpIsSet(true);
                    }
                    break;
                default:
                    e.a(dVar, b2);
                    break;
            }
            dVar.f();
        }
    }

    public StatsEvent setAnnotation(String str) {
        this.annotation = str;
        return this;
    }

    public void setAnnotationIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.annotation = null;
    }

    public StatsEvent setChid(byte b2) {
        this.chid = b2;
        setChidIsSet(true);
        return this;
    }

    public void setChidIsSet(boolean z2) {
        this.__isset_bit_vector.set(0, z2);
    }

    public StatsEvent setClientIp(int i2) {
        this.clientIp = i2;
        setClientIpIsSet(true);
        return this;
    }

    public void setClientIpIsSet(boolean z2) {
        this.__isset_bit_vector.set(5, z2);
    }

    public StatsEvent setConnpt(String str) {
        this.connpt = str;
        return this;
    }

    public void setConnptIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.connpt = null;
    }

    public StatsEvent setHost(String str) {
        this.host = str;
        return this;
    }

    public void setHostIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.host = null;
    }

    public StatsEvent setSubvalue(int i2) {
        this.subvalue = i2;
        setSubvalueIsSet(true);
        return this;
    }

    public void setSubvalueIsSet(boolean z2) {
        this.__isset_bit_vector.set(3, z2);
    }

    public StatsEvent setTime(int i2) {
        this.time = i2;
        setTimeIsSet(true);
        return this;
    }

    public void setTimeIsSet(boolean z2) {
        this.__isset_bit_vector.set(4, z2);
    }

    public StatsEvent setType(int i2) {
        this.type = i2;
        setTypeIsSet(true);
        return this;
    }

    public void setTypeIsSet(boolean z2) {
        this.__isset_bit_vector.set(1, z2);
    }

    public StatsEvent setUser(String str) {
        this.user = str;
        return this;
    }

    public void setUserIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.user = null;
    }

    public StatsEvent setValue(int i2) {
        this.value = i2;
        setValueIsSet(true);
        return this;
    }

    public void setValueIsSet(boolean z2) {
        this.__isset_bit_vector.set(2, z2);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("StatsEvent(");
        sb.append("chid:");
        sb.append((int) this.chid);
        sb.append(", ");
        sb.append("type:");
        sb.append(this.type);
        sb.append(", ");
        sb.append("value:");
        sb.append(this.value);
        sb.append(", ");
        sb.append("connpt:");
        String str = this.connpt;
        if (str == null) {
            sb.append("null");
        } else {
            sb.append(str);
        }
        if (isSetHost()) {
            sb.append(", ");
            sb.append("host:");
            String str2 = this.host;
            if (str2 == null) {
                sb.append("null");
            } else {
                sb.append(str2);
            }
        }
        if (isSetSubvalue()) {
            sb.append(", ");
            sb.append("subvalue:");
            sb.append(this.subvalue);
        }
        if (isSetAnnotation()) {
            sb.append(", ");
            sb.append("annotation:");
            String str3 = this.annotation;
            if (str3 == null) {
                sb.append("null");
            } else {
                sb.append(str3);
            }
        }
        if (isSetUser()) {
            sb.append(", ");
            sb.append("user:");
            String str4 = this.user;
            if (str4 == null) {
                sb.append("null");
            } else {
                sb.append(str4);
            }
        }
        if (isSetTime()) {
            sb.append(", ");
            sb.append("time:");
            sb.append(this.time);
        }
        if (isSetClientIp()) {
            sb.append(", ");
            sb.append("clientIp:");
            sb.append(this.clientIp);
        }
        sb.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
        return sb.toString();
    }

    public void unsetAnnotation() {
        this.annotation = null;
    }

    public void unsetChid() {
        this.__isset_bit_vector.clear(0);
    }

    public void unsetClientIp() {
        this.__isset_bit_vector.clear(5);
    }

    public void unsetConnpt() {
        this.connpt = null;
    }

    public void unsetHost() {
        this.host = null;
    }

    public void unsetSubvalue() {
        this.__isset_bit_vector.clear(3);
    }

    public void unsetTime() {
        this.__isset_bit_vector.clear(4);
    }

    public void unsetType() {
        this.__isset_bit_vector.clear(1);
    }

    public void unsetUser() {
        this.user = null;
    }

    public void unsetValue() {
        this.__isset_bit_vector.clear(2);
    }

    public void validate() throws TException {
        if (this.connpt != null) {
            return;
        }
        throw new TProtocolException("Required field 'connpt' was not present! Struct: " + toString());
    }

    @Override // org.apache.thrift.TBase
    public void write(d dVar) throws TException {
        validate();
        dVar.J(STRUCT_DESC);
        dVar.w(CHID_FIELD_DESC);
        dVar.v(this.chid);
        dVar.x();
        dVar.w(TYPE_FIELD_DESC);
        dVar.A(this.type);
        dVar.x();
        dVar.w(VALUE_FIELD_DESC);
        dVar.A(this.value);
        dVar.x();
        if (this.connpt != null) {
            dVar.w(CONNPT_FIELD_DESC);
            dVar.I(this.connpt);
            dVar.x();
        }
        if (this.host != null && isSetHost()) {
            dVar.w(HOST_FIELD_DESC);
            dVar.I(this.host);
            dVar.x();
        }
        if (isSetSubvalue()) {
            dVar.w(SUBVALUE_FIELD_DESC);
            dVar.A(this.subvalue);
            dVar.x();
        }
        if (this.annotation != null && isSetAnnotation()) {
            dVar.w(ANNOTATION_FIELD_DESC);
            dVar.I(this.annotation);
            dVar.x();
        }
        if (this.user != null && isSetUser()) {
            dVar.w(USER_FIELD_DESC);
            dVar.I(this.user);
            dVar.x();
        }
        if (isSetTime()) {
            dVar.w(TIME_FIELD_DESC);
            dVar.A(this.time);
            dVar.x();
        }
        if (isSetClientIp()) {
            dVar.w(CLIENT_IP_FIELD_DESC);
            dVar.A(this.clientIp);
            dVar.x();
        }
        dVar.y();
        dVar.K();
    }

    @Override // java.lang.Comparable
    public int compareTo(StatsEvent statsEvent) {
        int iD;
        int iD2;
        int iG;
        int iG2;
        int iD3;
        int iG3;
        int iG4;
        int iD4;
        int iD5;
        int iC;
        if (!getClass().equals(statsEvent.getClass())) {
            return getClass().getName().compareTo(statsEvent.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetChid()).compareTo(Boolean.valueOf(statsEvent.isSetChid()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetChid() && (iC = g0.a.c(this.chid, statsEvent.chid)) != 0) {
            return iC;
        }
        int iCompareTo2 = Boolean.valueOf(isSetType()).compareTo(Boolean.valueOf(statsEvent.isSetType()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (isSetType() && (iD5 = g0.a.d(this.type, statsEvent.type)) != 0) {
            return iD5;
        }
        int iCompareTo3 = Boolean.valueOf(isSetValue()).compareTo(Boolean.valueOf(statsEvent.isSetValue()));
        if (iCompareTo3 != 0) {
            return iCompareTo3;
        }
        if (isSetValue() && (iD4 = g0.a.d(this.value, statsEvent.value)) != 0) {
            return iD4;
        }
        int iCompareTo4 = Boolean.valueOf(isSetConnpt()).compareTo(Boolean.valueOf(statsEvent.isSetConnpt()));
        if (iCompareTo4 != 0) {
            return iCompareTo4;
        }
        if (isSetConnpt() && (iG4 = g0.a.g(this.connpt, statsEvent.connpt)) != 0) {
            return iG4;
        }
        int iCompareTo5 = Boolean.valueOf(isSetHost()).compareTo(Boolean.valueOf(statsEvent.isSetHost()));
        if (iCompareTo5 != 0) {
            return iCompareTo5;
        }
        if (isSetHost() && (iG3 = g0.a.g(this.host, statsEvent.host)) != 0) {
            return iG3;
        }
        int iCompareTo6 = Boolean.valueOf(isSetSubvalue()).compareTo(Boolean.valueOf(statsEvent.isSetSubvalue()));
        if (iCompareTo6 != 0) {
            return iCompareTo6;
        }
        if (isSetSubvalue() && (iD3 = g0.a.d(this.subvalue, statsEvent.subvalue)) != 0) {
            return iD3;
        }
        int iCompareTo7 = Boolean.valueOf(isSetAnnotation()).compareTo(Boolean.valueOf(statsEvent.isSetAnnotation()));
        if (iCompareTo7 != 0) {
            return iCompareTo7;
        }
        if (isSetAnnotation() && (iG2 = g0.a.g(this.annotation, statsEvent.annotation)) != 0) {
            return iG2;
        }
        int iCompareTo8 = Boolean.valueOf(isSetUser()).compareTo(Boolean.valueOf(statsEvent.isSetUser()));
        if (iCompareTo8 != 0) {
            return iCompareTo8;
        }
        if (isSetUser() && (iG = g0.a.g(this.user, statsEvent.user)) != 0) {
            return iG;
        }
        int iCompareTo9 = Boolean.valueOf(isSetTime()).compareTo(Boolean.valueOf(statsEvent.isSetTime()));
        if (iCompareTo9 != 0) {
            return iCompareTo9;
        }
        if (isSetTime() && (iD2 = g0.a.d(this.time, statsEvent.time)) != 0) {
            return iD2;
        }
        int iCompareTo10 = Boolean.valueOf(isSetClientIp()).compareTo(Boolean.valueOf(statsEvent.isSetClientIp()));
        if (iCompareTo10 != 0) {
            return iCompareTo10;
        }
        if (!isSetClientIp() || (iD = g0.a.d(this.clientIp, statsEvent.clientIp)) == 0) {
            return 0;
        }
        return iD;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public StatsEvent m14deepCopy() {
        return new StatsEvent(this);
    }

    public StatsEvent(byte b2, int i2, int i3, String str) {
        this();
        this.chid = b2;
        setChidIsSet(true);
        this.type = i2;
        setTypeIsSet(true);
        this.value = i3;
        setValueIsSet(true);
        this.connpt = str;
    }

    public boolean equals(StatsEvent statsEvent) {
        if (statsEvent == null || this.chid != statsEvent.chid || this.type != statsEvent.type || this.value != statsEvent.value) {
            return false;
        }
        boolean zIsSetConnpt = isSetConnpt();
        boolean zIsSetConnpt2 = statsEvent.isSetConnpt();
        if ((zIsSetConnpt || zIsSetConnpt2) && !(zIsSetConnpt && zIsSetConnpt2 && this.connpt.equals(statsEvent.connpt))) {
            return false;
        }
        boolean zIsSetHost = isSetHost();
        boolean zIsSetHost2 = statsEvent.isSetHost();
        if ((zIsSetHost || zIsSetHost2) && !(zIsSetHost && zIsSetHost2 && this.host.equals(statsEvent.host))) {
            return false;
        }
        boolean zIsSetSubvalue = isSetSubvalue();
        boolean zIsSetSubvalue2 = statsEvent.isSetSubvalue();
        if ((zIsSetSubvalue || zIsSetSubvalue2) && !(zIsSetSubvalue && zIsSetSubvalue2 && this.subvalue == statsEvent.subvalue)) {
            return false;
        }
        boolean zIsSetAnnotation = isSetAnnotation();
        boolean zIsSetAnnotation2 = statsEvent.isSetAnnotation();
        if ((zIsSetAnnotation || zIsSetAnnotation2) && !(zIsSetAnnotation && zIsSetAnnotation2 && this.annotation.equals(statsEvent.annotation))) {
            return false;
        }
        boolean zIsSetUser = isSetUser();
        boolean zIsSetUser2 = statsEvent.isSetUser();
        if ((zIsSetUser || zIsSetUser2) && !(zIsSetUser && zIsSetUser2 && this.user.equals(statsEvent.user))) {
            return false;
        }
        boolean zIsSetTime = isSetTime();
        boolean zIsSetTime2 = statsEvent.isSetTime();
        if ((zIsSetTime || zIsSetTime2) && !(zIsSetTime && zIsSetTime2 && this.time == statsEvent.time)) {
            return false;
        }
        boolean zIsSetClientIp = isSetClientIp();
        boolean zIsSetClientIp2 = statsEvent.isSetClientIp();
        if (zIsSetClientIp || zIsSetClientIp2) {
            return zIsSetClientIp && zIsSetClientIp2 && this.clientIp == statsEvent.clientIp;
        }
        return true;
    }

    public StatsEvent(StatsEvent statsEvent) {
        BitSet bitSet = new BitSet(6);
        this.__isset_bit_vector = bitSet;
        bitSet.clear();
        this.__isset_bit_vector.or(statsEvent.__isset_bit_vector);
        this.chid = statsEvent.chid;
        this.type = statsEvent.type;
        this.value = statsEvent.value;
        if (statsEvent.isSetConnpt()) {
            this.connpt = statsEvent.connpt;
        }
        if (statsEvent.isSetHost()) {
            this.host = statsEvent.host;
        }
        this.subvalue = statsEvent.subvalue;
        if (statsEvent.isSetAnnotation()) {
            this.annotation = statsEvent.annotation;
        }
        if (statsEvent.isSetUser()) {
            this.user = statsEvent.user;
        }
        this.time = statsEvent.time;
        this.clientIp = statsEvent.clientIp;
    }
}
