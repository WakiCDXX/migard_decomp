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
public class ClientUploadData implements TBase<ClientUploadData, Object>, Serializable, Cloneable {
    private static final g STRUCT_DESC = new g("ClientUploadData");
    private static final a UPLOAD_DATA_ITEMS_FIELD_DESC = new a("", (byte) 15, 1);
    public List<ClientUploadDataItem> uploadDataItems;

    public ClientUploadData() {
    }

    public void addToUploadDataItems(ClientUploadDataItem clientUploadDataItem) {
        if (this.uploadDataItems == null) {
            this.uploadDataItems = new ArrayList();
        }
        this.uploadDataItems.add(clientUploadDataItem);
    }

    public void clear() {
        this.uploadDataItems = null;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof ClientUploadData)) {
            return equals((ClientUploadData) obj);
        }
        return false;
    }

    public List<ClientUploadDataItem> getUploadDataItems() {
        return this.uploadDataItems;
    }

    public Iterator<ClientUploadDataItem> getUploadDataItemsIterator() {
        List<ClientUploadDataItem> list = this.uploadDataItems;
        if (list == null) {
            return null;
        }
        return list.iterator();
    }

    public int getUploadDataItemsSize() {
        List<ClientUploadDataItem> list = this.uploadDataItems;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public int hashCode() {
        return 0;
    }

    public boolean isSetUploadDataItems() {
        return this.uploadDataItems != null;
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
                this.uploadDataItems = new ArrayList(bVarJ.f2153b);
                for (int i2 = 0; i2 < bVarJ.f2153b; i2++) {
                    ClientUploadDataItem clientUploadDataItem = new ClientUploadDataItem();
                    clientUploadDataItem.read(dVar);
                    this.uploadDataItems.add(clientUploadDataItem);
                }
                dVar.k();
            } else {
                e.a(dVar, b2);
            }
            dVar.f();
        }
    }

    public ClientUploadData setUploadDataItems(List<ClientUploadDataItem> list) {
        this.uploadDataItems = list;
        return this;
    }

    public void setUploadDataItemsIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.uploadDataItems = null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ClientUploadData(");
        sb.append("uploadDataItems:");
        List<ClientUploadDataItem> list = this.uploadDataItems;
        if (list == null) {
            sb.append("null");
        } else {
            sb.append(list);
        }
        sb.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
        return sb.toString();
    }

    public void unsetUploadDataItems() {
        this.uploadDataItems = null;
    }

    public void validate() throws TException {
        if (this.uploadDataItems != null) {
            return;
        }
        throw new TProtocolException("Required field 'uploadDataItems' was not present! Struct: " + toString());
    }

    @Override // org.apache.thrift.TBase
    public void write(d dVar) throws TException {
        validate();
        dVar.J(STRUCT_DESC);
        if (this.uploadDataItems != null) {
            dVar.w(UPLOAD_DATA_ITEMS_FIELD_DESC);
            dVar.C(new b((byte) 12, this.uploadDataItems.size()));
            Iterator<ClientUploadDataItem> it = this.uploadDataItems.iterator();
            while (it.hasNext()) {
                it.next().write(dVar);
            }
            dVar.D();
            dVar.x();
        }
        dVar.y();
        dVar.K();
    }

    public ClientUploadData(List<ClientUploadDataItem> list) {
        this();
        this.uploadDataItems = list;
    }

    @Override // java.lang.Comparable
    public int compareTo(ClientUploadData clientUploadData) {
        int iH;
        if (!getClass().equals(clientUploadData.getClass())) {
            return getClass().getName().compareTo(clientUploadData.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetUploadDataItems()).compareTo(Boolean.valueOf(clientUploadData.isSetUploadDataItems()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (!isSetUploadDataItems() || (iH = g0.a.h(this.uploadDataItems, clientUploadData.uploadDataItems)) == 0) {
            return 0;
        }
        return iH;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public ClientUploadData m17deepCopy() {
        return new ClientUploadData(this);
    }

    public boolean equals(ClientUploadData clientUploadData) {
        if (clientUploadData == null) {
            return false;
        }
        boolean zIsSetUploadDataItems = isSetUploadDataItems();
        boolean zIsSetUploadDataItems2 = clientUploadData.isSetUploadDataItems();
        if (zIsSetUploadDataItems || zIsSetUploadDataItems2) {
            return zIsSetUploadDataItems && zIsSetUploadDataItems2 && this.uploadDataItems.equals(clientUploadData.uploadDataItems);
        }
        return true;
    }

    public ClientUploadData(ClientUploadData clientUploadData) {
        if (clientUploadData.isSetUploadDataItems()) {
            ArrayList arrayList = new ArrayList();
            Iterator<ClientUploadDataItem> it = clientUploadData.uploadDataItems.iterator();
            while (it.hasNext()) {
                arrayList.add(new ClientUploadDataItem(it.next()));
            }
            this.uploadDataItems = arrayList;
        }
    }
}
