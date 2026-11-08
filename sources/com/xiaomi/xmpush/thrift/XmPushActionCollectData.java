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
public class XmPushActionCollectData implements TBase<XmPushActionCollectData, Object>, Serializable, Cloneable {
    public List<DataCollectionItem> dataCollectionItems;
    private static final g STRUCT_DESC = new g("XmPushActionCollectData");
    private static final a DATA_COLLECTION_ITEMS_FIELD_DESC = new a("", (byte) 15, 1);

    public XmPushActionCollectData() {
    }

    public void addToDataCollectionItems(DataCollectionItem dataCollectionItem) {
        if (this.dataCollectionItems == null) {
            this.dataCollectionItems = new ArrayList();
        }
        this.dataCollectionItems.add(dataCollectionItem);
    }

    public void clear() {
        this.dataCollectionItems = null;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof XmPushActionCollectData)) {
            return equals((XmPushActionCollectData) obj);
        }
        return false;
    }

    public List<DataCollectionItem> getDataCollectionItems() {
        return this.dataCollectionItems;
    }

    public Iterator<DataCollectionItem> getDataCollectionItemsIterator() {
        List<DataCollectionItem> list = this.dataCollectionItems;
        if (list == null) {
            return null;
        }
        return list.iterator();
    }

    public int getDataCollectionItemsSize() {
        List<DataCollectionItem> list = this.dataCollectionItems;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public int hashCode() {
        return 0;
    }

    public boolean isSetDataCollectionItems() {
        return this.dataCollectionItems != null;
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
                this.dataCollectionItems = new ArrayList(bVarJ.f2153b);
                for (int i2 = 0; i2 < bVarJ.f2153b; i2++) {
                    DataCollectionItem dataCollectionItem = new DataCollectionItem();
                    dataCollectionItem.read(dVar);
                    this.dataCollectionItems.add(dataCollectionItem);
                }
                dVar.k();
            } else {
                e.a(dVar, b2);
            }
            dVar.f();
        }
    }

    public XmPushActionCollectData setDataCollectionItems(List<DataCollectionItem> list) {
        this.dataCollectionItems = list;
        return this;
    }

    public void setDataCollectionItemsIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.dataCollectionItems = null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("XmPushActionCollectData(");
        sb.append("dataCollectionItems:");
        List<DataCollectionItem> list = this.dataCollectionItems;
        if (list == null) {
            sb.append("null");
        } else {
            sb.append(list);
        }
        sb.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
        return sb.toString();
    }

    public void unsetDataCollectionItems() {
        this.dataCollectionItems = null;
    }

    public void validate() throws TException {
        if (this.dataCollectionItems != null) {
            return;
        }
        throw new TProtocolException("Required field 'dataCollectionItems' was not present! Struct: " + toString());
    }

    @Override // org.apache.thrift.TBase
    public void write(d dVar) throws TException {
        validate();
        dVar.J(STRUCT_DESC);
        if (this.dataCollectionItems != null) {
            dVar.w(DATA_COLLECTION_ITEMS_FIELD_DESC);
            dVar.C(new b((byte) 12, this.dataCollectionItems.size()));
            Iterator<DataCollectionItem> it = this.dataCollectionItems.iterator();
            while (it.hasNext()) {
                it.next().write(dVar);
            }
            dVar.D();
            dVar.x();
        }
        dVar.y();
        dVar.K();
    }

    public XmPushActionCollectData(List<DataCollectionItem> list) {
        this();
        this.dataCollectionItems = list;
    }

    @Override // java.lang.Comparable
    public int compareTo(XmPushActionCollectData xmPushActionCollectData) {
        int iH;
        if (!getClass().equals(xmPushActionCollectData.getClass())) {
            return getClass().getName().compareTo(xmPushActionCollectData.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetDataCollectionItems()).compareTo(Boolean.valueOf(xmPushActionCollectData.isSetDataCollectionItems()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (!isSetDataCollectionItems() || (iH = g0.a.h(this.dataCollectionItems, xmPushActionCollectData.dataCollectionItems)) == 0) {
            return 0;
        }
        return iH;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public XmPushActionCollectData m31deepCopy() {
        return new XmPushActionCollectData(this);
    }

    public boolean equals(XmPushActionCollectData xmPushActionCollectData) {
        if (xmPushActionCollectData == null) {
            return false;
        }
        boolean zIsSetDataCollectionItems = isSetDataCollectionItems();
        boolean zIsSetDataCollectionItems2 = xmPushActionCollectData.isSetDataCollectionItems();
        if (zIsSetDataCollectionItems || zIsSetDataCollectionItems2) {
            return zIsSetDataCollectionItems && zIsSetDataCollectionItems2 && this.dataCollectionItems.equals(xmPushActionCollectData.dataCollectionItems);
        }
        return true;
    }

    public XmPushActionCollectData(XmPushActionCollectData xmPushActionCollectData) {
        if (xmPushActionCollectData.isSetDataCollectionItems()) {
            ArrayList arrayList = new ArrayList();
            Iterator<DataCollectionItem> it = xmPushActionCollectData.dataCollectionItems.iterator();
            while (it.hasNext()) {
                arrayList.add(new DataCollectionItem(it.next()));
            }
            this.dataCollectionItems = arrayList;
        }
    }
}
