package com.xiaomi.xmpush.thrift;

import com.xiaomi.push.mpcd.Constants;
import java.io.Serializable;
import java.util.BitSet;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.a;
import org.apache.thrift.protocol.d;
import org.apache.thrift.protocol.e;
import org.apache.thrift.protocol.g;

/* JADX INFO: loaded from: classes.dex */
public class OnlineConfigItem implements TBase<OnlineConfigItem, Object>, Serializable, Cloneable {
    private static final int __BOOLVALUE_ISSET_ID = 5;
    private static final int __CLEAR_ISSET_ID = 2;
    private static final int __INTVALUE_ISSET_ID = 3;
    private static final int __KEY_ISSET_ID = 0;
    private static final int __LONGVALUE_ISSET_ID = 4;
    private static final int __TYPE_ISSET_ID = 1;
    private BitSet __isset_bit_vector;
    public boolean boolValue;
    public boolean clear;
    public int intValue;
    public int key;
    public long longValue;
    public String stringValue;
    public int type;
    private static final g STRUCT_DESC = new g("OnlineConfigItem");
    private static final a KEY_FIELD_DESC = new a("", (byte) 8, 1);
    private static final a TYPE_FIELD_DESC = new a("", (byte) 8, 2);
    private static final a CLEAR_FIELD_DESC = new a("", (byte) 2, 3);
    private static final a INT_VALUE_FIELD_DESC = new a("", (byte) 8, 4);
    private static final a LONG_VALUE_FIELD_DESC = new a("", (byte) 10, 5);
    private static final a STRING_VALUE_FIELD_DESC = new a("", (byte) 11, 6);
    private static final a BOOL_VALUE_FIELD_DESC = new a("", (byte) 2, 7);

    public OnlineConfigItem() {
        this.__isset_bit_vector = new BitSet(6);
    }

    public void clear() {
        setKeyIsSet(false);
        this.key = 0;
        setTypeIsSet(false);
        this.type = 0;
        setClearIsSet(false);
        this.clear = false;
        setIntValueIsSet(false);
        this.intValue = 0;
        setLongValueIsSet(false);
        this.longValue = 0L;
        this.stringValue = null;
        setBoolValueIsSet(false);
        this.boolValue = false;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof OnlineConfigItem)) {
            return equals((OnlineConfigItem) obj);
        }
        return false;
    }

    public int getIntValue() {
        return this.intValue;
    }

    public int getKey() {
        return this.key;
    }

    public long getLongValue() {
        return this.longValue;
    }

    public String getStringValue() {
        return this.stringValue;
    }

    public int getType() {
        return this.type;
    }

    public int hashCode() {
        return 0;
    }

    public boolean isBoolValue() {
        return this.boolValue;
    }

    public boolean isClear() {
        return this.clear;
    }

    public boolean isSetBoolValue() {
        return this.__isset_bit_vector.get(5);
    }

    public boolean isSetClear() {
        return this.__isset_bit_vector.get(2);
    }

    public boolean isSetIntValue() {
        return this.__isset_bit_vector.get(3);
    }

    public boolean isSetKey() {
        return this.__isset_bit_vector.get(0);
    }

    public boolean isSetLongValue() {
        return this.__isset_bit_vector.get(4);
    }

    public boolean isSetStringValue() {
        return this.stringValue != null;
    }

    public boolean isSetType() {
        return this.__isset_bit_vector.get(1);
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
                    if (b2 != 8) {
                        e.a(dVar, b2);
                    } else {
                        this.key = dVar.h();
                        setKeyIsSet(true);
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
                    if (b2 != 2) {
                        e.a(dVar, b2);
                    } else {
                        this.clear = dVar.b();
                        setClearIsSet(true);
                    }
                    break;
                case 4:
                    if (b2 != 8) {
                        e.a(dVar, b2);
                    } else {
                        this.intValue = dVar.h();
                        setIntValueIsSet(true);
                    }
                    break;
                case 5:
                    if (b2 != 10) {
                        e.a(dVar, b2);
                    } else {
                        this.longValue = dVar.i();
                        setLongValueIsSet(true);
                    }
                    break;
                case 6:
                    if (b2 != 11) {
                        e.a(dVar, b2);
                    } else {
                        this.stringValue = dVar.p();
                    }
                    break;
                case 7:
                    if (b2 != 2) {
                        e.a(dVar, b2);
                    } else {
                        this.boolValue = dVar.b();
                        setBoolValueIsSet(true);
                    }
                    break;
                default:
                    e.a(dVar, b2);
                    break;
            }
            dVar.f();
        }
    }

    public OnlineConfigItem setBoolValue(boolean z2) {
        this.boolValue = z2;
        setBoolValueIsSet(true);
        return this;
    }

    public void setBoolValueIsSet(boolean z2) {
        this.__isset_bit_vector.set(5, z2);
    }

    public OnlineConfigItem setClear(boolean z2) {
        this.clear = z2;
        setClearIsSet(true);
        return this;
    }

    public void setClearIsSet(boolean z2) {
        this.__isset_bit_vector.set(2, z2);
    }

    public OnlineConfigItem setIntValue(int i2) {
        this.intValue = i2;
        setIntValueIsSet(true);
        return this;
    }

    public void setIntValueIsSet(boolean z2) {
        this.__isset_bit_vector.set(3, z2);
    }

    public OnlineConfigItem setKey(int i2) {
        this.key = i2;
        setKeyIsSet(true);
        return this;
    }

    public void setKeyIsSet(boolean z2) {
        this.__isset_bit_vector.set(0, z2);
    }

    public OnlineConfigItem setLongValue(long j2) {
        this.longValue = j2;
        setLongValueIsSet(true);
        return this;
    }

    public void setLongValueIsSet(boolean z2) {
        this.__isset_bit_vector.set(4, z2);
    }

    public OnlineConfigItem setStringValue(String str) {
        this.stringValue = str;
        return this;
    }

    public void setStringValueIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.stringValue = null;
    }

    public OnlineConfigItem setType(int i2) {
        this.type = i2;
        setTypeIsSet(true);
        return this;
    }

    public void setTypeIsSet(boolean z2) {
        this.__isset_bit_vector.set(1, z2);
    }

    public String toString() {
        boolean z2;
        StringBuilder sb = new StringBuilder("OnlineConfigItem(");
        boolean z3 = false;
        if (isSetKey()) {
            sb.append("key:");
            sb.append(this.key);
            z2 = false;
        } else {
            z2 = true;
        }
        if (isSetType()) {
            if (!z2) {
                sb.append(", ");
            }
            sb.append("type:");
            sb.append(this.type);
            z2 = false;
        }
        if (isSetClear()) {
            if (!z2) {
                sb.append(", ");
            }
            sb.append("clear:");
            sb.append(this.clear);
            z2 = false;
        }
        if (isSetIntValue()) {
            if (!z2) {
                sb.append(", ");
            }
            sb.append("intValue:");
            sb.append(this.intValue);
            z2 = false;
        }
        if (isSetLongValue()) {
            if (!z2) {
                sb.append(", ");
            }
            sb.append("longValue:");
            sb.append(this.longValue);
            z2 = false;
        }
        if (isSetStringValue()) {
            if (!z2) {
                sb.append(", ");
            }
            sb.append("stringValue:");
            String str = this.stringValue;
            if (str == null) {
                sb.append("null");
            } else {
                sb.append(str);
            }
        } else {
            z3 = z2;
        }
        if (isSetBoolValue()) {
            if (!z3) {
                sb.append(", ");
            }
            sb.append("boolValue:");
            sb.append(this.boolValue);
        }
        sb.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
        return sb.toString();
    }

    public void unsetBoolValue() {
        this.__isset_bit_vector.clear(5);
    }

    public void unsetClear() {
        this.__isset_bit_vector.clear(2);
    }

    public void unsetIntValue() {
        this.__isset_bit_vector.clear(3);
    }

    public void unsetKey() {
        this.__isset_bit_vector.clear(0);
    }

    public void unsetLongValue() {
        this.__isset_bit_vector.clear(4);
    }

    public void unsetStringValue() {
        this.stringValue = null;
    }

    public void unsetType() {
        this.__isset_bit_vector.clear(1);
    }

    @Override // org.apache.thrift.TBase
    public void write(d dVar) throws TException {
        validate();
        dVar.J(STRUCT_DESC);
        if (isSetKey()) {
            dVar.w(KEY_FIELD_DESC);
            dVar.A(this.key);
            dVar.x();
        }
        if (isSetType()) {
            dVar.w(TYPE_FIELD_DESC);
            dVar.A(this.type);
            dVar.x();
        }
        if (isSetClear()) {
            dVar.w(CLEAR_FIELD_DESC);
            dVar.u(this.clear);
            dVar.x();
        }
        if (isSetIntValue()) {
            dVar.w(INT_VALUE_FIELD_DESC);
            dVar.A(this.intValue);
            dVar.x();
        }
        if (isSetLongValue()) {
            dVar.w(LONG_VALUE_FIELD_DESC);
            dVar.B(this.longValue);
            dVar.x();
        }
        if (this.stringValue != null && isSetStringValue()) {
            dVar.w(STRING_VALUE_FIELD_DESC);
            dVar.I(this.stringValue);
            dVar.x();
        }
        if (isSetBoolValue()) {
            dVar.w(BOOL_VALUE_FIELD_DESC);
            dVar.u(this.boolValue);
            dVar.x();
        }
        dVar.y();
        dVar.K();
    }

    @Override // java.lang.Comparable
    public int compareTo(OnlineConfigItem onlineConfigItem) {
        int iL;
        int iG;
        int iE;
        int iD;
        int iL2;
        int iD2;
        int iD3;
        if (!getClass().equals(onlineConfigItem.getClass())) {
            return getClass().getName().compareTo(onlineConfigItem.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetKey()).compareTo(Boolean.valueOf(onlineConfigItem.isSetKey()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetKey() && (iD3 = g0.a.d(this.key, onlineConfigItem.key)) != 0) {
            return iD3;
        }
        int iCompareTo2 = Boolean.valueOf(isSetType()).compareTo(Boolean.valueOf(onlineConfigItem.isSetType()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (isSetType() && (iD2 = g0.a.d(this.type, onlineConfigItem.type)) != 0) {
            return iD2;
        }
        int iCompareTo3 = Boolean.valueOf(isSetClear()).compareTo(Boolean.valueOf(onlineConfigItem.isSetClear()));
        if (iCompareTo3 != 0) {
            return iCompareTo3;
        }
        if (isSetClear() && (iL2 = g0.a.l(this.clear, onlineConfigItem.clear)) != 0) {
            return iL2;
        }
        int iCompareTo4 = Boolean.valueOf(isSetIntValue()).compareTo(Boolean.valueOf(onlineConfigItem.isSetIntValue()));
        if (iCompareTo4 != 0) {
            return iCompareTo4;
        }
        if (isSetIntValue() && (iD = g0.a.d(this.intValue, onlineConfigItem.intValue)) != 0) {
            return iD;
        }
        int iCompareTo5 = Boolean.valueOf(isSetLongValue()).compareTo(Boolean.valueOf(onlineConfigItem.isSetLongValue()));
        if (iCompareTo5 != 0) {
            return iCompareTo5;
        }
        if (isSetLongValue() && (iE = g0.a.e(this.longValue, onlineConfigItem.longValue)) != 0) {
            return iE;
        }
        int iCompareTo6 = Boolean.valueOf(isSetStringValue()).compareTo(Boolean.valueOf(onlineConfigItem.isSetStringValue()));
        if (iCompareTo6 != 0) {
            return iCompareTo6;
        }
        if (isSetStringValue() && (iG = g0.a.g(this.stringValue, onlineConfigItem.stringValue)) != 0) {
            return iG;
        }
        int iCompareTo7 = Boolean.valueOf(isSetBoolValue()).compareTo(Boolean.valueOf(onlineConfigItem.isSetBoolValue()));
        if (iCompareTo7 != 0) {
            return iCompareTo7;
        }
        if (!isSetBoolValue() || (iL = g0.a.l(this.boolValue, onlineConfigItem.boolValue)) == 0) {
            return 0;
        }
        return iL;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public OnlineConfigItem m23deepCopy() {
        return new OnlineConfigItem(this);
    }

    public OnlineConfigItem(OnlineConfigItem onlineConfigItem) {
        BitSet bitSet = new BitSet(6);
        this.__isset_bit_vector = bitSet;
        bitSet.clear();
        this.__isset_bit_vector.or(onlineConfigItem.__isset_bit_vector);
        this.key = onlineConfigItem.key;
        this.type = onlineConfigItem.type;
        this.clear = onlineConfigItem.clear;
        this.intValue = onlineConfigItem.intValue;
        this.longValue = onlineConfigItem.longValue;
        if (onlineConfigItem.isSetStringValue()) {
            this.stringValue = onlineConfigItem.stringValue;
        }
        this.boolValue = onlineConfigItem.boolValue;
    }

    public boolean equals(OnlineConfigItem onlineConfigItem) {
        if (onlineConfigItem == null) {
            return false;
        }
        boolean zIsSetKey = isSetKey();
        boolean zIsSetKey2 = onlineConfigItem.isSetKey();
        if ((zIsSetKey || zIsSetKey2) && !(zIsSetKey && zIsSetKey2 && this.key == onlineConfigItem.key)) {
            return false;
        }
        boolean zIsSetType = isSetType();
        boolean zIsSetType2 = onlineConfigItem.isSetType();
        if ((zIsSetType || zIsSetType2) && !(zIsSetType && zIsSetType2 && this.type == onlineConfigItem.type)) {
            return false;
        }
        boolean zIsSetClear = isSetClear();
        boolean zIsSetClear2 = onlineConfigItem.isSetClear();
        if ((zIsSetClear || zIsSetClear2) && !(zIsSetClear && zIsSetClear2 && this.clear == onlineConfigItem.clear)) {
            return false;
        }
        boolean zIsSetIntValue = isSetIntValue();
        boolean zIsSetIntValue2 = onlineConfigItem.isSetIntValue();
        if ((zIsSetIntValue || zIsSetIntValue2) && !(zIsSetIntValue && zIsSetIntValue2 && this.intValue == onlineConfigItem.intValue)) {
            return false;
        }
        boolean zIsSetLongValue = isSetLongValue();
        boolean zIsSetLongValue2 = onlineConfigItem.isSetLongValue();
        if ((zIsSetLongValue || zIsSetLongValue2) && !(zIsSetLongValue && zIsSetLongValue2 && this.longValue == onlineConfigItem.longValue)) {
            return false;
        }
        boolean zIsSetStringValue = isSetStringValue();
        boolean zIsSetStringValue2 = onlineConfigItem.isSetStringValue();
        if ((zIsSetStringValue || zIsSetStringValue2) && !(zIsSetStringValue && zIsSetStringValue2 && this.stringValue.equals(onlineConfigItem.stringValue))) {
            return false;
        }
        boolean zIsSetBoolValue = isSetBoolValue();
        boolean zIsSetBoolValue2 = onlineConfigItem.isSetBoolValue();
        if (zIsSetBoolValue || zIsSetBoolValue2) {
            return zIsSetBoolValue && zIsSetBoolValue2 && this.boolValue == onlineConfigItem.boolValue;
        }
        return true;
    }

    public void validate() throws TException {
    }
}
