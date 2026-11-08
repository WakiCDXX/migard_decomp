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
public class XmPushActionCheckClientInfo implements TBase<XmPushActionCheckClientInfo, Object>, Serializable, Cloneable {
    private static final int __MISCCONFIGVERSION_ISSET_ID = 0;
    private static final int __PLUGINCONFIGVERSION_ISSET_ID = 1;
    private BitSet __isset_bit_vector;
    public int miscConfigVersion;
    public int pluginConfigVersion;
    private static final g STRUCT_DESC = new g("XmPushActionCheckClientInfo");
    private static final a MISC_CONFIG_VERSION_FIELD_DESC = new a("", (byte) 8, 1);
    private static final a PLUGIN_CONFIG_VERSION_FIELD_DESC = new a("", (byte) 8, 2);

    public XmPushActionCheckClientInfo() {
        this.__isset_bit_vector = new BitSet(2);
    }

    public void clear() {
        setMiscConfigVersionIsSet(false);
        this.miscConfigVersion = 0;
        setPluginConfigVersionIsSet(false);
        this.pluginConfigVersion = 0;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof XmPushActionCheckClientInfo)) {
            return equals((XmPushActionCheckClientInfo) obj);
        }
        return false;
    }

    public int getMiscConfigVersion() {
        return this.miscConfigVersion;
    }

    public int getPluginConfigVersion() {
        return this.pluginConfigVersion;
    }

    public int hashCode() {
        return 0;
    }

    public boolean isSetMiscConfigVersion() {
        return this.__isset_bit_vector.get(0);
    }

    public boolean isSetPluginConfigVersion() {
        return this.__isset_bit_vector.get(1);
    }

    @Override // org.apache.thrift.TBase
    public void read(d dVar) throws TException {
        dVar.q();
        while (true) {
            a aVarE = dVar.e();
            byte b2 = aVarE.f2150b;
            if (b2 == 0) {
                break;
            }
            short s2 = aVarE.f2151c;
            if (s2 != 1) {
                if (s2 != 2) {
                    e.a(dVar, b2);
                } else if (b2 == 8) {
                    this.pluginConfigVersion = dVar.h();
                    setPluginConfigVersionIsSet(true);
                } else {
                    e.a(dVar, b2);
                }
            } else if (b2 == 8) {
                this.miscConfigVersion = dVar.h();
                setMiscConfigVersionIsSet(true);
            } else {
                e.a(dVar, b2);
            }
            dVar.f();
        }
        dVar.r();
        if (!isSetMiscConfigVersion()) {
            throw new TProtocolException("Required field 'miscConfigVersion' was not found in serialized data! Struct: " + toString());
        }
        if (isSetPluginConfigVersion()) {
            validate();
            return;
        }
        throw new TProtocolException("Required field 'pluginConfigVersion' was not found in serialized data! Struct: " + toString());
    }

    public XmPushActionCheckClientInfo setMiscConfigVersion(int i2) {
        this.miscConfigVersion = i2;
        setMiscConfigVersionIsSet(true);
        return this;
    }

    public void setMiscConfigVersionIsSet(boolean z2) {
        this.__isset_bit_vector.set(0, z2);
    }

    public XmPushActionCheckClientInfo setPluginConfigVersion(int i2) {
        this.pluginConfigVersion = i2;
        setPluginConfigVersionIsSet(true);
        return this;
    }

    public void setPluginConfigVersionIsSet(boolean z2) {
        this.__isset_bit_vector.set(1, z2);
    }

    public String toString() {
        return "XmPushActionCheckClientInfo(miscConfigVersion:" + this.miscConfigVersion + ", pluginConfigVersion:" + this.pluginConfigVersion + Constants.SEPARATOR_RIGHT_PARENTESIS;
    }

    public void unsetMiscConfigVersion() {
        this.__isset_bit_vector.clear(0);
    }

    public void unsetPluginConfigVersion() {
        this.__isset_bit_vector.clear(1);
    }

    @Override // org.apache.thrift.TBase
    public void write(d dVar) throws TException {
        validate();
        dVar.J(STRUCT_DESC);
        dVar.w(MISC_CONFIG_VERSION_FIELD_DESC);
        dVar.A(this.miscConfigVersion);
        dVar.x();
        dVar.w(PLUGIN_CONFIG_VERSION_FIELD_DESC);
        dVar.A(this.pluginConfigVersion);
        dVar.x();
        dVar.y();
        dVar.K();
    }

    @Override // java.lang.Comparable
    public int compareTo(XmPushActionCheckClientInfo xmPushActionCheckClientInfo) {
        int iD;
        int iD2;
        if (!getClass().equals(xmPushActionCheckClientInfo.getClass())) {
            return getClass().getName().compareTo(xmPushActionCheckClientInfo.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetMiscConfigVersion()).compareTo(Boolean.valueOf(xmPushActionCheckClientInfo.isSetMiscConfigVersion()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetMiscConfigVersion() && (iD2 = g0.a.d(this.miscConfigVersion, xmPushActionCheckClientInfo.miscConfigVersion)) != 0) {
            return iD2;
        }
        int iCompareTo2 = Boolean.valueOf(isSetPluginConfigVersion()).compareTo(Boolean.valueOf(xmPushActionCheckClientInfo.isSetPluginConfigVersion()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (!isSetPluginConfigVersion() || (iD = g0.a.d(this.pluginConfigVersion, xmPushActionCheckClientInfo.pluginConfigVersion)) == 0) {
            return 0;
        }
        return iD;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public XmPushActionCheckClientInfo m30deepCopy() {
        return new XmPushActionCheckClientInfo(this);
    }

    public XmPushActionCheckClientInfo(int i2, int i3) {
        this();
        this.miscConfigVersion = i2;
        setMiscConfigVersionIsSet(true);
        this.pluginConfigVersion = i3;
        setPluginConfigVersionIsSet(true);
    }

    public boolean equals(XmPushActionCheckClientInfo xmPushActionCheckClientInfo) {
        return xmPushActionCheckClientInfo != null && this.miscConfigVersion == xmPushActionCheckClientInfo.miscConfigVersion && this.pluginConfigVersion == xmPushActionCheckClientInfo.pluginConfigVersion;
    }

    public XmPushActionCheckClientInfo(XmPushActionCheckClientInfo xmPushActionCheckClientInfo) {
        BitSet bitSet = new BitSet(2);
        this.__isset_bit_vector = bitSet;
        bitSet.clear();
        this.__isset_bit_vector.or(xmPushActionCheckClientInfo.__isset_bit_vector);
        this.miscConfigVersion = xmPushActionCheckClientInfo.miscConfigVersion;
        this.pluginConfigVersion = xmPushActionCheckClientInfo.pluginConfigVersion;
    }

    public void validate() throws TException {
    }
}
