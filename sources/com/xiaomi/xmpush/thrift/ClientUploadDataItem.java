package com.xiaomi.xmpush.thrift;

import com.xiaomi.push.mpcd.Constants;
import java.io.Serializable;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.a;
import org.apache.thrift.protocol.c;
import org.apache.thrift.protocol.d;
import org.apache.thrift.protocol.e;
import org.apache.thrift.protocol.g;

/* JADX INFO: loaded from: classes.dex */
public class ClientUploadDataItem implements TBase<ClientUploadDataItem, Object>, Serializable, Cloneable {
    private static final int __COUNTER_ISSET_ID = 0;
    private static final int __FROMSDK_ISSET_ID = 2;
    private static final int __TIMESTAMP_ISSET_ID = 1;
    private BitSet __isset_bit_vector;
    public String category;
    public String channel;
    public long counter;
    public String data;
    public Map<String, String> extra;
    public boolean fromSdk;
    public String id;
    public String name;
    public String pkgName;
    public String sourcePackage;
    public long timestamp;
    private static final g STRUCT_DESC = new g("ClientUploadDataItem");
    private static final a CHANNEL_FIELD_DESC = new a("", (byte) 11, 1);
    private static final a DATA_FIELD_DESC = new a("", (byte) 11, 2);
    private static final a NAME_FIELD_DESC = new a("", (byte) 11, 3);
    private static final a COUNTER_FIELD_DESC = new a("", (byte) 10, 4);
    private static final a TIMESTAMP_FIELD_DESC = new a("", (byte) 10, 5);
    private static final a FROM_SDK_FIELD_DESC = new a("", (byte) 2, 6);
    private static final a CATEGORY_FIELD_DESC = new a("", (byte) 11, 7);
    private static final a SOURCE_PACKAGE_FIELD_DESC = new a("", (byte) 11, 8);
    private static final a ID_FIELD_DESC = new a("", (byte) 11, 9);
    private static final a EXTRA_FIELD_DESC = new a("", (byte) 13, 10);
    private static final a PKG_NAME_FIELD_DESC = new a("", (byte) 11, 11);

    public ClientUploadDataItem() {
        this.__isset_bit_vector = new BitSet(3);
    }

    public void clear() {
        this.channel = null;
        this.data = null;
        this.name = null;
        setCounterIsSet(false);
        this.counter = 0L;
        setTimestampIsSet(false);
        this.timestamp = 0L;
        setFromSdkIsSet(false);
        this.fromSdk = false;
        this.category = null;
        this.sourcePackage = null;
        this.id = null;
        this.extra = null;
        this.pkgName = null;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof ClientUploadDataItem)) {
            return equals((ClientUploadDataItem) obj);
        }
        return false;
    }

    public String getCategory() {
        return this.category;
    }

    public String getChannel() {
        return this.channel;
    }

    public long getCounter() {
        return this.counter;
    }

    public String getData() {
        return this.data;
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

    public String getName() {
        return this.name;
    }

    public String getPkgName() {
        return this.pkgName;
    }

    public String getSourcePackage() {
        return this.sourcePackage;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public int hashCode() {
        return 0;
    }

    public boolean isFromSdk() {
        return this.fromSdk;
    }

    public boolean isSetCategory() {
        return this.category != null;
    }

    public boolean isSetChannel() {
        return this.channel != null;
    }

    public boolean isSetCounter() {
        return this.__isset_bit_vector.get(0);
    }

    public boolean isSetData() {
        return this.data != null;
    }

    public boolean isSetExtra() {
        return this.extra != null;
    }

    public boolean isSetFromSdk() {
        return this.__isset_bit_vector.get(2);
    }

    public boolean isSetId() {
        return this.id != null;
    }

    public boolean isSetName() {
        return this.name != null;
    }

    public boolean isSetPkgName() {
        return this.pkgName != null;
    }

    public boolean isSetSourcePackage() {
        return this.sourcePackage != null;
    }

    public boolean isSetTimestamp() {
        return this.__isset_bit_vector.get(1);
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
                validate();
                return;
            }
            switch (aVarE.f2151c) {
                case 1:
                    if (b2 == 11) {
                        this.channel = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 2:
                    if (b2 == 11) {
                        this.data = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 3:
                    if (b2 == 11) {
                        this.name = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 4:
                    if (b2 == 10) {
                        this.counter = dVar.i();
                        setCounterIsSet(true);
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 5:
                    if (b2 == 10) {
                        this.timestamp = dVar.i();
                        setTimestampIsSet(true);
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 6:
                    if (b2 == 2) {
                        this.fromSdk = dVar.b();
                        setFromSdkIsSet(true);
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 7:
                    if (b2 == 11) {
                        this.category = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 8:
                    if (b2 == 11) {
                        this.sourcePackage = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 9:
                    if (b2 == 11) {
                        this.id = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 10:
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
                case 11:
                    if (b2 == 11) {
                        this.pkgName = dVar.p();
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

    public ClientUploadDataItem setCategory(String str) {
        this.category = str;
        return this;
    }

    public void setCategoryIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.category = null;
    }

    public ClientUploadDataItem setChannel(String str) {
        this.channel = str;
        return this;
    }

    public void setChannelIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.channel = null;
    }

    public ClientUploadDataItem setCounter(long j2) {
        this.counter = j2;
        setCounterIsSet(true);
        return this;
    }

    public void setCounterIsSet(boolean z2) {
        this.__isset_bit_vector.set(0, z2);
    }

    public ClientUploadDataItem setData(String str) {
        this.data = str;
        return this;
    }

    public void setDataIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.data = null;
    }

    public ClientUploadDataItem setExtra(Map<String, String> map) {
        this.extra = map;
        return this;
    }

    public void setExtraIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.extra = null;
    }

    public ClientUploadDataItem setFromSdk(boolean z2) {
        this.fromSdk = z2;
        setFromSdkIsSet(true);
        return this;
    }

    public void setFromSdkIsSet(boolean z2) {
        this.__isset_bit_vector.set(2, z2);
    }

    public ClientUploadDataItem setId(String str) {
        this.id = str;
        return this;
    }

    public void setIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.id = null;
    }

    public ClientUploadDataItem setName(String str) {
        this.name = str;
        return this;
    }

    public void setNameIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.name = null;
    }

    public ClientUploadDataItem setPkgName(String str) {
        this.pkgName = str;
        return this;
    }

    public void setPkgNameIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.pkgName = null;
    }

    public ClientUploadDataItem setSourcePackage(String str) {
        this.sourcePackage = str;
        return this;
    }

    public void setSourcePackageIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.sourcePackage = null;
    }

    public ClientUploadDataItem setTimestamp(long j2) {
        this.timestamp = j2;
        setTimestampIsSet(true);
        return this;
    }

    public void setTimestampIsSet(boolean z2) {
        this.__isset_bit_vector.set(1, z2);
    }

    public String toString() {
        boolean z2;
        StringBuilder sb = new StringBuilder("ClientUploadDataItem(");
        boolean z3 = false;
        if (isSetChannel()) {
            sb.append("channel:");
            String str = this.channel;
            if (str == null) {
                sb.append("null");
            } else {
                sb.append(str);
            }
            z2 = false;
        } else {
            z2 = true;
        }
        if (isSetData()) {
            if (!z2) {
                sb.append(", ");
            }
            sb.append("data:");
            String str2 = this.data;
            if (str2 == null) {
                sb.append("null");
            } else {
                sb.append(str2);
            }
            z2 = false;
        }
        if (isSetName()) {
            if (!z2) {
                sb.append(", ");
            }
            sb.append("name:");
            String str3 = this.name;
            if (str3 == null) {
                sb.append("null");
            } else {
                sb.append(str3);
            }
            z2 = false;
        }
        if (isSetCounter()) {
            if (!z2) {
                sb.append(", ");
            }
            sb.append("counter:");
            sb.append(this.counter);
            z2 = false;
        }
        if (isSetTimestamp()) {
            if (!z2) {
                sb.append(", ");
            }
            sb.append("timestamp:");
            sb.append(this.timestamp);
            z2 = false;
        }
        if (isSetFromSdk()) {
            if (!z2) {
                sb.append(", ");
            }
            sb.append("fromSdk:");
            sb.append(this.fromSdk);
            z2 = false;
        }
        if (isSetCategory()) {
            if (!z2) {
                sb.append(", ");
            }
            sb.append("category:");
            String str4 = this.category;
            if (str4 == null) {
                sb.append("null");
            } else {
                sb.append(str4);
            }
            z2 = false;
        }
        if (isSetSourcePackage()) {
            if (!z2) {
                sb.append(", ");
            }
            sb.append("sourcePackage:");
            String str5 = this.sourcePackage;
            if (str5 == null) {
                sb.append("null");
            } else {
                sb.append(str5);
            }
            z2 = false;
        }
        if (isSetId()) {
            if (!z2) {
                sb.append(", ");
            }
            sb.append("id:");
            String str6 = this.id;
            if (str6 == null) {
                sb.append("null");
            } else {
                sb.append(str6);
            }
            z2 = false;
        }
        if (isSetExtra()) {
            if (!z2) {
                sb.append(", ");
            }
            sb.append("extra:");
            Map<String, String> map = this.extra;
            if (map == null) {
                sb.append("null");
            } else {
                sb.append(map);
            }
        } else {
            z3 = z2;
        }
        if (isSetPkgName()) {
            if (!z3) {
                sb.append(", ");
            }
            sb.append("pkgName:");
            String str7 = this.pkgName;
            if (str7 == null) {
                sb.append("null");
            } else {
                sb.append(str7);
            }
        }
        sb.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
        return sb.toString();
    }

    public void unsetCategory() {
        this.category = null;
    }

    public void unsetChannel() {
        this.channel = null;
    }

    public void unsetCounter() {
        this.__isset_bit_vector.clear(0);
    }

    public void unsetData() {
        this.data = null;
    }

    public void unsetExtra() {
        this.extra = null;
    }

    public void unsetFromSdk() {
        this.__isset_bit_vector.clear(2);
    }

    public void unsetId() {
        this.id = null;
    }

    public void unsetName() {
        this.name = null;
    }

    public void unsetPkgName() {
        this.pkgName = null;
    }

    public void unsetSourcePackage() {
        this.sourcePackage = null;
    }

    public void unsetTimestamp() {
        this.__isset_bit_vector.clear(1);
    }

    @Override // org.apache.thrift.TBase
    public void write(d dVar) throws TException {
        validate();
        dVar.J(STRUCT_DESC);
        if (this.channel != null && isSetChannel()) {
            dVar.w(CHANNEL_FIELD_DESC);
            dVar.I(this.channel);
            dVar.x();
        }
        if (this.data != null && isSetData()) {
            dVar.w(DATA_FIELD_DESC);
            dVar.I(this.data);
            dVar.x();
        }
        if (this.name != null && isSetName()) {
            dVar.w(NAME_FIELD_DESC);
            dVar.I(this.name);
            dVar.x();
        }
        if (isSetCounter()) {
            dVar.w(COUNTER_FIELD_DESC);
            dVar.B(this.counter);
            dVar.x();
        }
        if (isSetTimestamp()) {
            dVar.w(TIMESTAMP_FIELD_DESC);
            dVar.B(this.timestamp);
            dVar.x();
        }
        if (isSetFromSdk()) {
            dVar.w(FROM_SDK_FIELD_DESC);
            dVar.u(this.fromSdk);
            dVar.x();
        }
        if (this.category != null && isSetCategory()) {
            dVar.w(CATEGORY_FIELD_DESC);
            dVar.I(this.category);
            dVar.x();
        }
        if (this.sourcePackage != null && isSetSourcePackage()) {
            dVar.w(SOURCE_PACKAGE_FIELD_DESC);
            dVar.I(this.sourcePackage);
            dVar.x();
        }
        if (this.id != null && isSetId()) {
            dVar.w(ID_FIELD_DESC);
            dVar.I(this.id);
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
        if (this.pkgName != null && isSetPkgName()) {
            dVar.w(PKG_NAME_FIELD_DESC);
            dVar.I(this.pkgName);
            dVar.x();
        }
        dVar.y();
        dVar.K();
    }

    @Override // java.lang.Comparable
    public int compareTo(ClientUploadDataItem clientUploadDataItem) {
        int iG;
        int i2;
        int iG2;
        int iG3;
        int iG4;
        int iL;
        int iE;
        int iE2;
        int iG5;
        int iG6;
        int iG7;
        if (!getClass().equals(clientUploadDataItem.getClass())) {
            return getClass().getName().compareTo(clientUploadDataItem.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetChannel()).compareTo(Boolean.valueOf(clientUploadDataItem.isSetChannel()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetChannel() && (iG7 = g0.a.g(this.channel, clientUploadDataItem.channel)) != 0) {
            return iG7;
        }
        int iCompareTo2 = Boolean.valueOf(isSetData()).compareTo(Boolean.valueOf(clientUploadDataItem.isSetData()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (isSetData() && (iG6 = g0.a.g(this.data, clientUploadDataItem.data)) != 0) {
            return iG6;
        }
        int iCompareTo3 = Boolean.valueOf(isSetName()).compareTo(Boolean.valueOf(clientUploadDataItem.isSetName()));
        if (iCompareTo3 != 0) {
            return iCompareTo3;
        }
        if (isSetName() && (iG5 = g0.a.g(this.name, clientUploadDataItem.name)) != 0) {
            return iG5;
        }
        int iCompareTo4 = Boolean.valueOf(isSetCounter()).compareTo(Boolean.valueOf(clientUploadDataItem.isSetCounter()));
        if (iCompareTo4 != 0) {
            return iCompareTo4;
        }
        if (isSetCounter() && (iE2 = g0.a.e(this.counter, clientUploadDataItem.counter)) != 0) {
            return iE2;
        }
        int iCompareTo5 = Boolean.valueOf(isSetTimestamp()).compareTo(Boolean.valueOf(clientUploadDataItem.isSetTimestamp()));
        if (iCompareTo5 != 0) {
            return iCompareTo5;
        }
        if (isSetTimestamp() && (iE = g0.a.e(this.timestamp, clientUploadDataItem.timestamp)) != 0) {
            return iE;
        }
        int iCompareTo6 = Boolean.valueOf(isSetFromSdk()).compareTo(Boolean.valueOf(clientUploadDataItem.isSetFromSdk()));
        if (iCompareTo6 != 0) {
            return iCompareTo6;
        }
        if (isSetFromSdk() && (iL = g0.a.l(this.fromSdk, clientUploadDataItem.fromSdk)) != 0) {
            return iL;
        }
        int iCompareTo7 = Boolean.valueOf(isSetCategory()).compareTo(Boolean.valueOf(clientUploadDataItem.isSetCategory()));
        if (iCompareTo7 != 0) {
            return iCompareTo7;
        }
        if (isSetCategory() && (iG4 = g0.a.g(this.category, clientUploadDataItem.category)) != 0) {
            return iG4;
        }
        int iCompareTo8 = Boolean.valueOf(isSetSourcePackage()).compareTo(Boolean.valueOf(clientUploadDataItem.isSetSourcePackage()));
        if (iCompareTo8 != 0) {
            return iCompareTo8;
        }
        if (isSetSourcePackage() && (iG3 = g0.a.g(this.sourcePackage, clientUploadDataItem.sourcePackage)) != 0) {
            return iG3;
        }
        int iCompareTo9 = Boolean.valueOf(isSetId()).compareTo(Boolean.valueOf(clientUploadDataItem.isSetId()));
        if (iCompareTo9 != 0) {
            return iCompareTo9;
        }
        if (isSetId() && (iG2 = g0.a.g(this.id, clientUploadDataItem.id)) != 0) {
            return iG2;
        }
        int iCompareTo10 = Boolean.valueOf(isSetExtra()).compareTo(Boolean.valueOf(clientUploadDataItem.isSetExtra()));
        if (iCompareTo10 != 0) {
            return iCompareTo10;
        }
        if (isSetExtra() && (i2 = g0.a.i(this.extra, clientUploadDataItem.extra)) != 0) {
            return i2;
        }
        int iCompareTo11 = Boolean.valueOf(isSetPkgName()).compareTo(Boolean.valueOf(clientUploadDataItem.isSetPkgName()));
        if (iCompareTo11 != 0) {
            return iCompareTo11;
        }
        if (!isSetPkgName() || (iG = g0.a.g(this.pkgName, clientUploadDataItem.pkgName)) == 0) {
            return 0;
        }
        return iG;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public ClientUploadDataItem m18deepCopy() {
        return new ClientUploadDataItem(this);
    }

    public ClientUploadDataItem(ClientUploadDataItem clientUploadDataItem) {
        BitSet bitSet = new BitSet(3);
        this.__isset_bit_vector = bitSet;
        bitSet.clear();
        this.__isset_bit_vector.or(clientUploadDataItem.__isset_bit_vector);
        if (clientUploadDataItem.isSetChannel()) {
            this.channel = clientUploadDataItem.channel;
        }
        if (clientUploadDataItem.isSetData()) {
            this.data = clientUploadDataItem.data;
        }
        if (clientUploadDataItem.isSetName()) {
            this.name = clientUploadDataItem.name;
        }
        this.counter = clientUploadDataItem.counter;
        this.timestamp = clientUploadDataItem.timestamp;
        this.fromSdk = clientUploadDataItem.fromSdk;
        if (clientUploadDataItem.isSetCategory()) {
            this.category = clientUploadDataItem.category;
        }
        if (clientUploadDataItem.isSetSourcePackage()) {
            this.sourcePackage = clientUploadDataItem.sourcePackage;
        }
        if (clientUploadDataItem.isSetId()) {
            this.id = clientUploadDataItem.id;
        }
        if (clientUploadDataItem.isSetExtra()) {
            HashMap map = new HashMap();
            for (Map.Entry<String, String> entry : clientUploadDataItem.extra.entrySet()) {
                map.put(entry.getKey(), entry.getValue());
            }
            this.extra = map;
        }
        if (clientUploadDataItem.isSetPkgName()) {
            this.pkgName = clientUploadDataItem.pkgName;
        }
    }

    public boolean equals(ClientUploadDataItem clientUploadDataItem) {
        if (clientUploadDataItem == null) {
            return false;
        }
        boolean zIsSetChannel = isSetChannel();
        boolean zIsSetChannel2 = clientUploadDataItem.isSetChannel();
        if ((zIsSetChannel || zIsSetChannel2) && !(zIsSetChannel && zIsSetChannel2 && this.channel.equals(clientUploadDataItem.channel))) {
            return false;
        }
        boolean zIsSetData = isSetData();
        boolean zIsSetData2 = clientUploadDataItem.isSetData();
        if ((zIsSetData || zIsSetData2) && !(zIsSetData && zIsSetData2 && this.data.equals(clientUploadDataItem.data))) {
            return false;
        }
        boolean zIsSetName = isSetName();
        boolean zIsSetName2 = clientUploadDataItem.isSetName();
        if ((zIsSetName || zIsSetName2) && !(zIsSetName && zIsSetName2 && this.name.equals(clientUploadDataItem.name))) {
            return false;
        }
        boolean zIsSetCounter = isSetCounter();
        boolean zIsSetCounter2 = clientUploadDataItem.isSetCounter();
        if ((zIsSetCounter || zIsSetCounter2) && !(zIsSetCounter && zIsSetCounter2 && this.counter == clientUploadDataItem.counter)) {
            return false;
        }
        boolean zIsSetTimestamp = isSetTimestamp();
        boolean zIsSetTimestamp2 = clientUploadDataItem.isSetTimestamp();
        if ((zIsSetTimestamp || zIsSetTimestamp2) && !(zIsSetTimestamp && zIsSetTimestamp2 && this.timestamp == clientUploadDataItem.timestamp)) {
            return false;
        }
        boolean zIsSetFromSdk = isSetFromSdk();
        boolean zIsSetFromSdk2 = clientUploadDataItem.isSetFromSdk();
        if ((zIsSetFromSdk || zIsSetFromSdk2) && !(zIsSetFromSdk && zIsSetFromSdk2 && this.fromSdk == clientUploadDataItem.fromSdk)) {
            return false;
        }
        boolean zIsSetCategory = isSetCategory();
        boolean zIsSetCategory2 = clientUploadDataItem.isSetCategory();
        if ((zIsSetCategory || zIsSetCategory2) && !(zIsSetCategory && zIsSetCategory2 && this.category.equals(clientUploadDataItem.category))) {
            return false;
        }
        boolean zIsSetSourcePackage = isSetSourcePackage();
        boolean zIsSetSourcePackage2 = clientUploadDataItem.isSetSourcePackage();
        if ((zIsSetSourcePackage || zIsSetSourcePackage2) && !(zIsSetSourcePackage && zIsSetSourcePackage2 && this.sourcePackage.equals(clientUploadDataItem.sourcePackage))) {
            return false;
        }
        boolean zIsSetId = isSetId();
        boolean zIsSetId2 = clientUploadDataItem.isSetId();
        if ((zIsSetId || zIsSetId2) && !(zIsSetId && zIsSetId2 && this.id.equals(clientUploadDataItem.id))) {
            return false;
        }
        boolean zIsSetExtra = isSetExtra();
        boolean zIsSetExtra2 = clientUploadDataItem.isSetExtra();
        if ((zIsSetExtra || zIsSetExtra2) && !(zIsSetExtra && zIsSetExtra2 && this.extra.equals(clientUploadDataItem.extra))) {
            return false;
        }
        boolean zIsSetPkgName = isSetPkgName();
        boolean zIsSetPkgName2 = clientUploadDataItem.isSetPkgName();
        if (zIsSetPkgName || zIsSetPkgName2) {
            return zIsSetPkgName && zIsSetPkgName2 && this.pkgName.equals(clientUploadDataItem.pkgName);
        }
        return true;
    }

    public void validate() throws TException {
    }
}
