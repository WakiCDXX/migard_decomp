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
public class Wifi implements TBase<Wifi, Object>, Serializable, Cloneable {
    private static final int __SIGNALSTRENGTH_ISSET_ID = 0;
    private BitSet __isset_bit_vector;
    public String macAddress;
    public int signalStrength;
    public String ssid;
    private static final g STRUCT_DESC = new g("Wifi");
    private static final a MAC_ADDRESS_FIELD_DESC = new a("", (byte) 11, 1);
    private static final a SIGNAL_STRENGTH_FIELD_DESC = new a("", (byte) 8, 2);
    private static final a SSID_FIELD_DESC = new a("", (byte) 11, 3);

    public Wifi() {
        this.__isset_bit_vector = new BitSet(1);
    }

    public void clear() {
        this.macAddress = null;
        setSignalStrengthIsSet(false);
        this.signalStrength = 0;
        this.ssid = null;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof Wifi)) {
            return equals((Wifi) obj);
        }
        return false;
    }

    public String getMacAddress() {
        return this.macAddress;
    }

    public int getSignalStrength() {
        return this.signalStrength;
    }

    public String getSsid() {
        return this.ssid;
    }

    public int hashCode() {
        return 0;
    }

    public boolean isSetMacAddress() {
        return this.macAddress != null;
    }

    public boolean isSetSignalStrength() {
        return this.__isset_bit_vector.get(0);
    }

    public boolean isSetSsid() {
        return this.ssid != null;
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
                    if (s2 != 3) {
                        e.a(dVar, b2);
                    } else if (b2 == 11) {
                        this.ssid = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                } else if (b2 == 8) {
                    this.signalStrength = dVar.h();
                    setSignalStrengthIsSet(true);
                } else {
                    e.a(dVar, b2);
                }
            } else if (b2 == 11) {
                this.macAddress = dVar.p();
            } else {
                e.a(dVar, b2);
            }
            dVar.f();
        }
        dVar.r();
        if (isSetSignalStrength()) {
            validate();
            return;
        }
        throw new TProtocolException("Required field 'signalStrength' was not found in serialized data! Struct: " + toString());
    }

    public Wifi setMacAddress(String str) {
        this.macAddress = str;
        return this;
    }

    public void setMacAddressIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.macAddress = null;
    }

    public Wifi setSignalStrength(int i2) {
        this.signalStrength = i2;
        setSignalStrengthIsSet(true);
        return this;
    }

    public void setSignalStrengthIsSet(boolean z2) {
        this.__isset_bit_vector.set(0, z2);
    }

    public Wifi setSsid(String str) {
        this.ssid = str;
        return this;
    }

    public void setSsidIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.ssid = null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Wifi(");
        sb.append("macAddress:");
        String str = this.macAddress;
        if (str == null) {
            sb.append("null");
        } else {
            sb.append(str);
        }
        sb.append(", ");
        sb.append("signalStrength:");
        sb.append(this.signalStrength);
        if (isSetSsid()) {
            sb.append(", ");
            sb.append("ssid:");
            String str2 = this.ssid;
            if (str2 == null) {
                sb.append("null");
            } else {
                sb.append(str2);
            }
        }
        sb.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
        return sb.toString();
    }

    public void unsetMacAddress() {
        this.macAddress = null;
    }

    public void unsetSignalStrength() {
        this.__isset_bit_vector.clear(0);
    }

    public void unsetSsid() {
        this.ssid = null;
    }

    public void validate() throws TException {
        if (this.macAddress != null) {
            return;
        }
        throw new TProtocolException("Required field 'macAddress' was not present! Struct: " + toString());
    }

    @Override // org.apache.thrift.TBase
    public void write(d dVar) throws TException {
        validate();
        dVar.J(STRUCT_DESC);
        if (this.macAddress != null) {
            dVar.w(MAC_ADDRESS_FIELD_DESC);
            dVar.I(this.macAddress);
            dVar.x();
        }
        dVar.w(SIGNAL_STRENGTH_FIELD_DESC);
        dVar.A(this.signalStrength);
        dVar.x();
        if (this.ssid != null && isSetSsid()) {
            dVar.w(SSID_FIELD_DESC);
            dVar.I(this.ssid);
            dVar.x();
        }
        dVar.y();
        dVar.K();
    }

    @Override // java.lang.Comparable
    public int compareTo(Wifi wifi) {
        int iG;
        int iD;
        int iG2;
        if (!getClass().equals(wifi.getClass())) {
            return getClass().getName().compareTo(wifi.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetMacAddress()).compareTo(Boolean.valueOf(wifi.isSetMacAddress()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetMacAddress() && (iG2 = g0.a.g(this.macAddress, wifi.macAddress)) != 0) {
            return iG2;
        }
        int iCompareTo2 = Boolean.valueOf(isSetSignalStrength()).compareTo(Boolean.valueOf(wifi.isSetSignalStrength()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (isSetSignalStrength() && (iD = g0.a.d(this.signalStrength, wifi.signalStrength)) != 0) {
            return iD;
        }
        int iCompareTo3 = Boolean.valueOf(isSetSsid()).compareTo(Boolean.valueOf(wifi.isSetSsid()));
        if (iCompareTo3 != 0) {
            return iCompareTo3;
        }
        if (!isSetSsid() || (iG = g0.a.g(this.ssid, wifi.ssid)) == 0) {
            return 0;
        }
        return iG;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public Wifi m27deepCopy() {
        return new Wifi(this);
    }

    public Wifi(String str, int i2) {
        this();
        this.macAddress = str;
        this.signalStrength = i2;
        setSignalStrengthIsSet(true);
    }

    public boolean equals(Wifi wifi) {
        if (wifi == null) {
            return false;
        }
        boolean zIsSetMacAddress = isSetMacAddress();
        boolean zIsSetMacAddress2 = wifi.isSetMacAddress();
        if (((zIsSetMacAddress || zIsSetMacAddress2) && !(zIsSetMacAddress && zIsSetMacAddress2 && this.macAddress.equals(wifi.macAddress))) || this.signalStrength != wifi.signalStrength) {
            return false;
        }
        boolean zIsSetSsid = isSetSsid();
        boolean zIsSetSsid2 = wifi.isSetSsid();
        if (zIsSetSsid || zIsSetSsid2) {
            return zIsSetSsid && zIsSetSsid2 && this.ssid.equals(wifi.ssid);
        }
        return true;
    }

    public Wifi(Wifi wifi) {
        BitSet bitSet = new BitSet(1);
        this.__isset_bit_vector = bitSet;
        bitSet.clear();
        this.__isset_bit_vector.or(wifi.__isset_bit_vector);
        if (wifi.isSetMacAddress()) {
            this.macAddress = wifi.macAddress;
        }
        this.signalStrength = wifi.signalStrength;
        if (wifi.isSetSsid()) {
            this.ssid = wifi.ssid;
        }
    }
}
