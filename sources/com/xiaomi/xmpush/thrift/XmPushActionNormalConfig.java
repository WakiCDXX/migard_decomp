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
public class XmPushActionNormalConfig implements TBase<XmPushActionNormalConfig, Object>, Serializable, Cloneable {
    public List<NormalConfig> normalConfigs;
    private static final g STRUCT_DESC = new g("XmPushActionNormalConfig");
    private static final a NORMAL_CONFIGS_FIELD_DESC = new a("", (byte) 15, 1);

    public XmPushActionNormalConfig() {
    }

    public void addToNormalConfigs(NormalConfig normalConfig) {
        if (this.normalConfigs == null) {
            this.normalConfigs = new ArrayList();
        }
        this.normalConfigs.add(normalConfig);
    }

    public void clear() {
        this.normalConfigs = null;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof XmPushActionNormalConfig)) {
            return equals((XmPushActionNormalConfig) obj);
        }
        return false;
    }

    public List<NormalConfig> getNormalConfigs() {
        return this.normalConfigs;
    }

    public Iterator<NormalConfig> getNormalConfigsIterator() {
        List<NormalConfig> list = this.normalConfigs;
        if (list == null) {
            return null;
        }
        return list.iterator();
    }

    public int getNormalConfigsSize() {
        List<NormalConfig> list = this.normalConfigs;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public int hashCode() {
        return 0;
    }

    public boolean isSetNormalConfigs() {
        return this.normalConfigs != null;
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
                this.normalConfigs = new ArrayList(bVarJ.f2153b);
                for (int i2 = 0; i2 < bVarJ.f2153b; i2++) {
                    NormalConfig normalConfig = new NormalConfig();
                    normalConfig.read(dVar);
                    this.normalConfigs.add(normalConfig);
                }
                dVar.k();
            } else {
                e.a(dVar, b2);
            }
            dVar.f();
        }
    }

    public XmPushActionNormalConfig setNormalConfigs(List<NormalConfig> list) {
        this.normalConfigs = list;
        return this;
    }

    public void setNormalConfigsIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.normalConfigs = null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("XmPushActionNormalConfig(");
        sb.append("normalConfigs:");
        List<NormalConfig> list = this.normalConfigs;
        if (list == null) {
            sb.append("null");
        } else {
            sb.append(list);
        }
        sb.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
        return sb.toString();
    }

    public void unsetNormalConfigs() {
        this.normalConfigs = null;
    }

    public void validate() throws TException {
        if (this.normalConfigs != null) {
            return;
        }
        throw new TProtocolException("Required field 'normalConfigs' was not present! Struct: " + toString());
    }

    @Override // org.apache.thrift.TBase
    public void write(d dVar) throws TException {
        validate();
        dVar.J(STRUCT_DESC);
        if (this.normalConfigs != null) {
            dVar.w(NORMAL_CONFIGS_FIELD_DESC);
            dVar.C(new b((byte) 12, this.normalConfigs.size()));
            Iterator<NormalConfig> it = this.normalConfigs.iterator();
            while (it.hasNext()) {
                it.next().write(dVar);
            }
            dVar.D();
            dVar.x();
        }
        dVar.y();
        dVar.K();
    }

    public XmPushActionNormalConfig(List<NormalConfig> list) {
        this();
        this.normalConfigs = list;
    }

    @Override // java.lang.Comparable
    public int compareTo(XmPushActionNormalConfig xmPushActionNormalConfig) {
        int iH;
        if (!getClass().equals(xmPushActionNormalConfig.getClass())) {
            return getClass().getName().compareTo(xmPushActionNormalConfig.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetNormalConfigs()).compareTo(Boolean.valueOf(xmPushActionNormalConfig.isSetNormalConfigs()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (!isSetNormalConfigs() || (iH = g0.a.h(this.normalConfigs, xmPushActionNormalConfig.normalConfigs)) == 0) {
            return 0;
        }
        return iH;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public XmPushActionNormalConfig m36deepCopy() {
        return new XmPushActionNormalConfig(this);
    }

    public boolean equals(XmPushActionNormalConfig xmPushActionNormalConfig) {
        if (xmPushActionNormalConfig == null) {
            return false;
        }
        boolean zIsSetNormalConfigs = isSetNormalConfigs();
        boolean zIsSetNormalConfigs2 = xmPushActionNormalConfig.isSetNormalConfigs();
        if (zIsSetNormalConfigs || zIsSetNormalConfigs2) {
            return zIsSetNormalConfigs && zIsSetNormalConfigs2 && this.normalConfigs.equals(xmPushActionNormalConfig.normalConfigs);
        }
        return true;
    }

    public XmPushActionNormalConfig(XmPushActionNormalConfig xmPushActionNormalConfig) {
        if (xmPushActionNormalConfig.isSetNormalConfigs()) {
            ArrayList arrayList = new ArrayList();
            Iterator<NormalConfig> it = xmPushActionNormalConfig.normalConfigs.iterator();
            while (it.hasNext()) {
                arrayList.add(new NormalConfig(it.next()));
            }
            this.normalConfigs = arrayList;
        }
    }
}
