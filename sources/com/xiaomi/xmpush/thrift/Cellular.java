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
public class Cellular implements TBase<Cellular, Object>, Serializable, Cloneable {
    private static final int __ID_ISSET_ID = 0;
    private static final int __SIGNALSTRENGTH_ISSET_ID = 1;
    private BitSet __isset_bit_vector;
    public int id;
    public int signalStrength;
    private static final g STRUCT_DESC = new g("Cellular");
    private static final a ID_FIELD_DESC = new a("", (byte) 8, 1);
    private static final a SIGNAL_STRENGTH_FIELD_DESC = new a("", (byte) 8, 2);

    public Cellular() {
        this.__isset_bit_vector = new BitSet(2);
    }

    public void clear() {
        setIdIsSet(false);
        this.id = 0;
        setSignalStrengthIsSet(false);
        this.signalStrength = 0;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof Cellular)) {
            return equals((Cellular) obj);
        }
        return false;
    }

    public int getId() {
        return this.id;
    }

    public int getSignalStrength() {
        return this.signalStrength;
    }

    public int hashCode() {
        return 0;
    }

    public boolean isSetId() {
        return this.__isset_bit_vector.get(0);
    }

    public boolean isSetSignalStrength() {
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
                    this.signalStrength = dVar.h();
                    setSignalStrengthIsSet(true);
                } else {
                    e.a(dVar, b2);
                }
            } else if (b2 == 8) {
                this.id = dVar.h();
                setIdIsSet(true);
            } else {
                e.a(dVar, b2);
            }
            dVar.f();
        }
        dVar.r();
        if (!isSetId()) {
            throw new TProtocolException("Required field 'id' was not found in serialized data! Struct: " + toString());
        }
        if (isSetSignalStrength()) {
            validate();
            return;
        }
        throw new TProtocolException("Required field 'signalStrength' was not found in serialized data! Struct: " + toString());
    }

    public Cellular setId(int i2) {
        this.id = i2;
        setIdIsSet(true);
        return this;
    }

    public void setIdIsSet(boolean z2) {
        this.__isset_bit_vector.set(0, z2);
    }

    public Cellular setSignalStrength(int i2) {
        this.signalStrength = i2;
        setSignalStrengthIsSet(true);
        return this;
    }

    public void setSignalStrengthIsSet(boolean z2) {
        this.__isset_bit_vector.set(1, z2);
    }

    public String toString() {
        return "Cellular(id:" + this.id + ", signalStrength:" + this.signalStrength + Constants.SEPARATOR_RIGHT_PARENTESIS;
    }

    public void unsetId() {
        this.__isset_bit_vector.clear(0);
    }

    public void unsetSignalStrength() {
        this.__isset_bit_vector.clear(1);
    }

    @Override // org.apache.thrift.TBase
    public void write(d dVar) throws TException {
        validate();
        dVar.J(STRUCT_DESC);
        dVar.w(ID_FIELD_DESC);
        dVar.A(this.id);
        dVar.x();
        dVar.w(SIGNAL_STRENGTH_FIELD_DESC);
        dVar.A(this.signalStrength);
        dVar.x();
        dVar.y();
        dVar.K();
    }

    @Override // java.lang.Comparable
    public int compareTo(Cellular cellular) {
        int iD;
        int iD2;
        if (!getClass().equals(cellular.getClass())) {
            return getClass().getName().compareTo(cellular.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetId()).compareTo(Boolean.valueOf(cellular.isSetId()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetId() && (iD2 = g0.a.d(this.id, cellular.id)) != 0) {
            return iD2;
        }
        int iCompareTo2 = Boolean.valueOf(isSetSignalStrength()).compareTo(Boolean.valueOf(cellular.isSetSignalStrength()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (!isSetSignalStrength() || (iD = g0.a.d(this.signalStrength, cellular.signalStrength)) == 0) {
            return 0;
        }
        return iD;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public Cellular m16deepCopy() {
        return new Cellular(this);
    }

    public Cellular(int i2, int i3) {
        this();
        this.id = i2;
        setIdIsSet(true);
        this.signalStrength = i3;
        setSignalStrengthIsSet(true);
    }

    public boolean equals(Cellular cellular) {
        return cellular != null && this.id == cellular.id && this.signalStrength == cellular.signalStrength;
    }

    public Cellular(Cellular cellular) {
        BitSet bitSet = new BitSet(2);
        this.__isset_bit_vector = bitSet;
        bitSet.clear();
        this.__isset_bit_vector.or(cellular.__isset_bit_vector);
        this.id = cellular.id;
        this.signalStrength = cellular.signalStrength;
    }

    public void validate() throws TException {
    }
}
