package com.xiaomi.xmpush.thrift;

import com.xiaomi.push.mpcd.Constants;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.protocol.a;
import org.apache.thrift.protocol.b;
import org.apache.thrift.protocol.d;
import org.apache.thrift.protocol.e;
import org.apache.thrift.protocol.g;

/* JADX INFO: loaded from: classes.dex */
public class NormalConfig implements TBase<NormalConfig, Object>, Serializable, Cloneable {
    private static final int __VERSION_ISSET_ID = 0;
    private BitSet __isset_bit_vector;
    public List<OnlineConfigItem> configItems;
    public ConfigListType type;
    public int version;
    private static final g STRUCT_DESC = new g("NormalConfig");
    private static final a VERSION_FIELD_DESC = new a("", (byte) 8, 1);
    private static final a CONFIG_ITEMS_FIELD_DESC = new a("", (byte) 15, 2);
    private static final a TYPE_FIELD_DESC = new a("", (byte) 8, 3);

    public NormalConfig() {
        this.__isset_bit_vector = new BitSet(1);
    }

    public void addToConfigItems(OnlineConfigItem onlineConfigItem) {
        if (this.configItems == null) {
            this.configItems = new ArrayList();
        }
        this.configItems.add(onlineConfigItem);
    }

    public void clear() {
        setVersionIsSet(false);
        this.version = 0;
        this.configItems = null;
        this.type = null;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof NormalConfig)) {
            return equals((NormalConfig) obj);
        }
        return false;
    }

    public List<OnlineConfigItem> getConfigItems() {
        return this.configItems;
    }

    public Iterator<OnlineConfigItem> getConfigItemsIterator() {
        List<OnlineConfigItem> list = this.configItems;
        if (list == null) {
            return null;
        }
        return list.iterator();
    }

    public int getConfigItemsSize() {
        List<OnlineConfigItem> list = this.configItems;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public ConfigListType getType() {
        return this.type;
    }

    public int getVersion() {
        return this.version;
    }

    public int hashCode() {
        return 0;
    }

    public boolean isSetConfigItems() {
        return this.configItems != null;
    }

    public boolean isSetType() {
        return this.type != null;
    }

    public boolean isSetVersion() {
        return this.__isset_bit_vector.get(0);
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
                    } else if (b2 == 8) {
                        this.type = ConfigListType.findByValue(dVar.h());
                    } else {
                        e.a(dVar, b2);
                    }
                } else if (b2 == 15) {
                    b bVarJ = dVar.j();
                    this.configItems = new ArrayList(bVarJ.f2153b);
                    for (int i2 = 0; i2 < bVarJ.f2153b; i2++) {
                        OnlineConfigItem onlineConfigItem = new OnlineConfigItem();
                        onlineConfigItem.read(dVar);
                        this.configItems.add(onlineConfigItem);
                    }
                    dVar.k();
                } else {
                    e.a(dVar, b2);
                }
            } else if (b2 == 8) {
                this.version = dVar.h();
                setVersionIsSet(true);
            } else {
                e.a(dVar, b2);
            }
            dVar.f();
        }
        dVar.r();
        if (isSetVersion()) {
            validate();
            return;
        }
        throw new TProtocolException("Required field 'version' was not found in serialized data! Struct: " + toString());
    }

    public NormalConfig setConfigItems(List<OnlineConfigItem> list) {
        this.configItems = list;
        return this;
    }

    public void setConfigItemsIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.configItems = null;
    }

    public NormalConfig setType(ConfigListType configListType) {
        this.type = configListType;
        return this;
    }

    public void setTypeIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.type = null;
    }

    public NormalConfig setVersion(int i2) {
        this.version = i2;
        setVersionIsSet(true);
        return this;
    }

    public void setVersionIsSet(boolean z2) {
        this.__isset_bit_vector.set(0, z2);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("NormalConfig(");
        sb.append("version:");
        sb.append(this.version);
        sb.append(", ");
        sb.append("configItems:");
        List<OnlineConfigItem> list = this.configItems;
        if (list == null) {
            sb.append("null");
        } else {
            sb.append(list);
        }
        if (isSetType()) {
            sb.append(", ");
            sb.append("type:");
            ConfigListType configListType = this.type;
            if (configListType == null) {
                sb.append("null");
            } else {
                sb.append(configListType);
            }
        }
        sb.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
        return sb.toString();
    }

    public void unsetConfigItems() {
        this.configItems = null;
    }

    public void unsetType() {
        this.type = null;
    }

    public void unsetVersion() {
        this.__isset_bit_vector.clear(0);
    }

    public void validate() throws TException {
        if (this.configItems != null) {
            return;
        }
        throw new TProtocolException("Required field 'configItems' was not present! Struct: " + toString());
    }

    @Override // org.apache.thrift.TBase
    public void write(d dVar) throws TException {
        validate();
        dVar.J(STRUCT_DESC);
        dVar.w(VERSION_FIELD_DESC);
        dVar.A(this.version);
        dVar.x();
        if (this.configItems != null) {
            dVar.w(CONFIG_ITEMS_FIELD_DESC);
            dVar.C(new b((byte) 12, this.configItems.size()));
            Iterator<OnlineConfigItem> it = this.configItems.iterator();
            while (it.hasNext()) {
                it.next().write(dVar);
            }
            dVar.D();
            dVar.x();
        }
        if (this.type != null && isSetType()) {
            dVar.w(TYPE_FIELD_DESC);
            dVar.A(this.type.getValue());
            dVar.x();
        }
        dVar.y();
        dVar.K();
    }

    @Override // java.lang.Comparable
    public int compareTo(NormalConfig normalConfig) {
        int iF;
        int iH;
        int iD;
        if (!getClass().equals(normalConfig.getClass())) {
            return getClass().getName().compareTo(normalConfig.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetVersion()).compareTo(Boolean.valueOf(normalConfig.isSetVersion()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetVersion() && (iD = g0.a.d(this.version, normalConfig.version)) != 0) {
            return iD;
        }
        int iCompareTo2 = Boolean.valueOf(isSetConfigItems()).compareTo(Boolean.valueOf(normalConfig.isSetConfigItems()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (isSetConfigItems() && (iH = g0.a.h(this.configItems, normalConfig.configItems)) != 0) {
            return iH;
        }
        int iCompareTo3 = Boolean.valueOf(isSetType()).compareTo(Boolean.valueOf(normalConfig.isSetType()));
        if (iCompareTo3 != 0) {
            return iCompareTo3;
        }
        if (!isSetType() || (iF = g0.a.f(this.type, normalConfig.type)) == 0) {
            return 0;
        }
        return iF;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public NormalConfig m20deepCopy() {
        return new NormalConfig(this);
    }

    public NormalConfig(int i2, List<OnlineConfigItem> list) {
        this();
        this.version = i2;
        setVersionIsSet(true);
        this.configItems = list;
    }

    public boolean equals(NormalConfig normalConfig) {
        if (normalConfig == null || this.version != normalConfig.version) {
            return false;
        }
        boolean zIsSetConfigItems = isSetConfigItems();
        boolean zIsSetConfigItems2 = normalConfig.isSetConfigItems();
        if ((zIsSetConfigItems || zIsSetConfigItems2) && !(zIsSetConfigItems && zIsSetConfigItems2 && this.configItems.equals(normalConfig.configItems))) {
            return false;
        }
        boolean zIsSetType = isSetType();
        boolean zIsSetType2 = normalConfig.isSetType();
        if (zIsSetType || zIsSetType2) {
            return zIsSetType && zIsSetType2 && this.type.equals(normalConfig.type);
        }
        return true;
    }

    public NormalConfig(NormalConfig normalConfig) {
        BitSet bitSet = new BitSet(1);
        this.__isset_bit_vector = bitSet;
        bitSet.clear();
        this.__isset_bit_vector.or(normalConfig.__isset_bit_vector);
        this.version = normalConfig.version;
        if (normalConfig.isSetConfigItems()) {
            ArrayList arrayList = new ArrayList();
            Iterator<OnlineConfigItem> it = normalConfig.configItems.iterator();
            while (it.hasNext()) {
                arrayList.add(new OnlineConfigItem(it.next()));
            }
            this.configItems = arrayList;
        }
        if (normalConfig.isSetType()) {
            this.type = normalConfig.type;
        }
    }
}
