package com.xiaomi.xmpush.thrift;

import com.xiaomi.push.mpcd.Constants;
import java.io.Serializable;
import java.util.ArrayList;
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
public class XmPushActionCustomConfig implements TBase<XmPushActionCustomConfig, Object>, Serializable, Cloneable {
    public List<OnlineConfigItem> customConfigs;
    private static final g STRUCT_DESC = new g("XmPushActionCustomConfig");
    private static final a CUSTOM_CONFIGS_FIELD_DESC = new a("", (byte) 15, 1);

    public XmPushActionCustomConfig() {
    }

    public void addToCustomConfigs(OnlineConfigItem onlineConfigItem) {
        if (this.customConfigs == null) {
            this.customConfigs = new ArrayList();
        }
        this.customConfigs.add(onlineConfigItem);
    }

    public void clear() {
        this.customConfigs = null;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof XmPushActionCustomConfig)) {
            return equals((XmPushActionCustomConfig) obj);
        }
        return false;
    }

    public List<OnlineConfigItem> getCustomConfigs() {
        return this.customConfigs;
    }

    public Iterator<OnlineConfigItem> getCustomConfigsIterator() {
        List<OnlineConfigItem> list = this.customConfigs;
        if (list == null) {
            return null;
        }
        return list.iterator();
    }

    public int getCustomConfigsSize() {
        List<OnlineConfigItem> list = this.customConfigs;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public int hashCode() {
        return 0;
    }

    public boolean isSetCustomConfigs() {
        return this.customConfigs != null;
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
            if (aVarE.f2151c != 1) {
                e.a(dVar, b2);
            } else if (b2 == 15) {
                b bVarJ = dVar.j();
                this.customConfigs = new ArrayList(bVarJ.f2153b);
                for (int i2 = 0; i2 < bVarJ.f2153b; i2++) {
                    OnlineConfigItem onlineConfigItem = new OnlineConfigItem();
                    onlineConfigItem.read(dVar);
                    this.customConfigs.add(onlineConfigItem);
                }
                dVar.k();
            } else {
                e.a(dVar, b2);
            }
            dVar.f();
        }
    }

    public XmPushActionCustomConfig setCustomConfigs(List<OnlineConfigItem> list) {
        this.customConfigs = list;
        return this;
    }

    public void setCustomConfigsIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.customConfigs = null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("XmPushActionCustomConfig(");
        sb.append("customConfigs:");
        List<OnlineConfigItem> list = this.customConfigs;
        if (list == null) {
            sb.append("null");
        } else {
            sb.append(list);
        }
        sb.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
        return sb.toString();
    }

    public void unsetCustomConfigs() {
        this.customConfigs = null;
    }

    public void validate() throws TException {
        if (this.customConfigs != null) {
            return;
        }
        throw new TProtocolException("Required field 'customConfigs' was not present! Struct: " + toString());
    }

    @Override // org.apache.thrift.TBase
    public void write(d dVar) throws TException {
        validate();
        dVar.J(STRUCT_DESC);
        if (this.customConfigs != null) {
            dVar.w(CUSTOM_CONFIGS_FIELD_DESC);
            dVar.C(new b((byte) 12, this.customConfigs.size()));
            Iterator<OnlineConfigItem> it = this.customConfigs.iterator();
            while (it.hasNext()) {
                it.next().write(dVar);
            }
            dVar.D();
            dVar.x();
        }
        dVar.y();
        dVar.K();
    }

    public XmPushActionCustomConfig(List<OnlineConfigItem> list) {
        this();
        this.customConfigs = list;
    }

    @Override // java.lang.Comparable
    public int compareTo(XmPushActionCustomConfig xmPushActionCustomConfig) {
        int iH;
        if (!getClass().equals(xmPushActionCustomConfig.getClass())) {
            return getClass().getName().compareTo(xmPushActionCustomConfig.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetCustomConfigs()).compareTo(Boolean.valueOf(xmPushActionCustomConfig.isSetCustomConfigs()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (!isSetCustomConfigs() || (iH = g0.a.h(this.customConfigs, xmPushActionCustomConfig.customConfigs)) == 0) {
            return 0;
        }
        return iH;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public XmPushActionCustomConfig m35deepCopy() {
        return new XmPushActionCustomConfig(this);
    }

    public boolean equals(XmPushActionCustomConfig xmPushActionCustomConfig) {
        if (xmPushActionCustomConfig == null) {
            return false;
        }
        boolean zIsSetCustomConfigs = isSetCustomConfigs();
        boolean zIsSetCustomConfigs2 = xmPushActionCustomConfig.isSetCustomConfigs();
        if (zIsSetCustomConfigs || zIsSetCustomConfigs2) {
            return zIsSetCustomConfigs && zIsSetCustomConfigs2 && this.customConfigs.equals(xmPushActionCustomConfig.customConfigs);
        }
        return true;
    }

    public XmPushActionCustomConfig(XmPushActionCustomConfig xmPushActionCustomConfig) {
        if (xmPushActionCustomConfig.isSetCustomConfigs()) {
            ArrayList arrayList = new ArrayList();
            Iterator<OnlineConfigItem> it = xmPushActionCustomConfig.customConfigs.iterator();
            while (it.hasNext()) {
                arrayList.add(new OnlineConfigItem(it.next()));
            }
            this.customConfigs = arrayList;
        }
    }
}
