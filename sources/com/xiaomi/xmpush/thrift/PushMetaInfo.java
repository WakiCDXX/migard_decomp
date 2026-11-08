package com.xiaomi.xmpush.thrift;

import com.xiaomi.push.mpcd.Constants;
import com.xiaomi.push.service.PacketHelper;
import java.io.Serializable;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.protocol.a;
import org.apache.thrift.protocol.c;
import org.apache.thrift.protocol.d;
import org.apache.thrift.protocol.e;
import org.apache.thrift.protocol.g;

/* JADX INFO: loaded from: classes.dex */
public class PushMetaInfo implements TBase<PushMetaInfo, Object>, Serializable, Cloneable {
    private static final int __IGNOREREGINFO_ISSET_ID = 4;
    private static final int __MESSAGETS_ISSET_ID = 0;
    private static final int __NOTIFYID_ISSET_ID = 3;
    private static final int __NOTIFYTYPE_ISSET_ID = 1;
    private static final int __PASSTHROUGH_ISSET_ID = 2;
    private BitSet __isset_bit_vector;
    public Map<String, String> apsProperFields;
    public String description;
    public Map<String, String> extra;
    public String id;
    public boolean ignoreRegInfo;
    public Map<String, String> internal;
    public long messageTs;
    public int notifyId;
    public int notifyType;
    public int passThrough;
    public String title;
    public String topic;
    public String url;
    private static final g STRUCT_DESC = new g("PushMetaInfo");
    private static final a ID_FIELD_DESC = new a("", (byte) 11, 1);
    private static final a MESSAGE_TS_FIELD_DESC = new a("", (byte) 10, 2);
    private static final a TOPIC_FIELD_DESC = new a("", (byte) 11, 3);
    private static final a TITLE_FIELD_DESC = new a("", (byte) 11, 4);
    private static final a DESCRIPTION_FIELD_DESC = new a("", (byte) 11, 5);
    private static final a NOTIFY_TYPE_FIELD_DESC = new a("", (byte) 8, 6);
    private static final a URL_FIELD_DESC = new a("", (byte) 11, 7);
    private static final a PASS_THROUGH_FIELD_DESC = new a("", (byte) 8, 8);
    private static final a NOTIFY_ID_FIELD_DESC = new a("", (byte) 8, 9);
    private static final a EXTRA_FIELD_DESC = new a("", (byte) 13, 10);
    private static final a INTERNAL_FIELD_DESC = new a("", (byte) 13, 11);
    private static final a IGNORE_REG_INFO_FIELD_DESC = new a("", (byte) 2, 12);
    private static final a APS_PROPER_FIELDS_FIELD_DESC = new a("", (byte) 13, 13);

    public PushMetaInfo() {
        this.__isset_bit_vector = new BitSet(5);
        this.ignoreRegInfo = false;
    }

    public void clear() {
        this.id = null;
        setMessageTsIsSet(false);
        this.messageTs = 0L;
        this.topic = null;
        this.title = null;
        this.description = null;
        setNotifyTypeIsSet(false);
        this.notifyType = 0;
        this.url = null;
        setPassThroughIsSet(false);
        this.passThrough = 0;
        setNotifyIdIsSet(false);
        this.notifyId = 0;
        this.extra = null;
        this.internal = null;
        this.ignoreRegInfo = false;
        this.apsProperFields = null;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof PushMetaInfo)) {
            return equals((PushMetaInfo) obj);
        }
        return false;
    }

    public Map<String, String> getApsProperFields() {
        return this.apsProperFields;
    }

    public int getApsProperFieldsSize() {
        Map<String, String> map = this.apsProperFields;
        if (map == null) {
            return 0;
        }
        return map.size();
    }

    public String getDescription() {
        return this.description;
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

    public Map<String, String> getInternal() {
        return this.internal;
    }

    public int getInternalSize() {
        Map<String, String> map = this.internal;
        if (map == null) {
            return 0;
        }
        return map.size();
    }

    public long getMessageTs() {
        return this.messageTs;
    }

    public int getNotifyId() {
        return this.notifyId;
    }

    public int getNotifyType() {
        return this.notifyType;
    }

    public int getPassThrough() {
        return this.passThrough;
    }

    public String getTitle() {
        return this.title;
    }

    public String getTopic() {
        return this.topic;
    }

    public String getUrl() {
        return this.url;
    }

    public int hashCode() {
        return 0;
    }

    public boolean isIgnoreRegInfo() {
        return this.ignoreRegInfo;
    }

    public boolean isSetApsProperFields() {
        return this.apsProperFields != null;
    }

    public boolean isSetDescription() {
        return this.description != null;
    }

    public boolean isSetExtra() {
        return this.extra != null;
    }

    public boolean isSetId() {
        return this.id != null;
    }

    public boolean isSetIgnoreRegInfo() {
        return this.__isset_bit_vector.get(4);
    }

    public boolean isSetInternal() {
        return this.internal != null;
    }

    public boolean isSetMessageTs() {
        return this.__isset_bit_vector.get(0);
    }

    public boolean isSetNotifyId() {
        return this.__isset_bit_vector.get(3);
    }

    public boolean isSetNotifyType() {
        return this.__isset_bit_vector.get(1);
    }

    public boolean isSetPassThrough() {
        return this.__isset_bit_vector.get(2);
    }

    public boolean isSetTitle() {
        return this.title != null;
    }

    public boolean isSetTopic() {
        return this.topic != null;
    }

    public boolean isSetUrl() {
        return this.url != null;
    }

    public void putToApsProperFields(String str, String str2) {
        if (this.apsProperFields == null) {
            this.apsProperFields = new HashMap();
        }
        this.apsProperFields.put(str, str2);
    }

    public void putToExtra(String str, String str2) {
        if (this.extra == null) {
            this.extra = new HashMap();
        }
        this.extra.put(str, str2);
    }

    public void putToInternal(String str, String str2) {
        if (this.internal == null) {
            this.internal = new HashMap();
        }
        this.internal.put(str, str2);
    }

    @Override // org.apache.thrift.TBase
    public void read(d dVar) throws TException {
        dVar.q();
        while (true) {
            a aVarE = dVar.e();
            byte b2 = aVarE.f2150b;
            if (b2 == 0) {
                dVar.r();
                if (isSetMessageTs()) {
                    validate();
                    return;
                }
                throw new TProtocolException("Required field 'messageTs' was not found in serialized data! Struct: " + toString());
            }
            int i2 = 0;
            switch (aVarE.f2151c) {
                case 1:
                    if (b2 != 11) {
                        e.a(dVar, b2);
                    } else {
                        this.id = dVar.p();
                    }
                    break;
                case 2:
                    if (b2 != 10) {
                        e.a(dVar, b2);
                    } else {
                        this.messageTs = dVar.i();
                        setMessageTsIsSet(true);
                    }
                    break;
                case 3:
                    if (b2 != 11) {
                        e.a(dVar, b2);
                    } else {
                        this.topic = dVar.p();
                    }
                    break;
                case 4:
                    if (b2 != 11) {
                        e.a(dVar, b2);
                    } else {
                        this.title = dVar.p();
                    }
                    break;
                case 5:
                    if (b2 != 11) {
                        e.a(dVar, b2);
                    } else {
                        this.description = dVar.p();
                    }
                    break;
                case 6:
                    if (b2 != 8) {
                        e.a(dVar, b2);
                    } else {
                        this.notifyType = dVar.h();
                        setNotifyTypeIsSet(true);
                    }
                    break;
                case 7:
                    if (b2 != 11) {
                        e.a(dVar, b2);
                    } else {
                        this.url = dVar.p();
                    }
                    break;
                case 8:
                    if (b2 != 8) {
                        e.a(dVar, b2);
                    } else {
                        this.passThrough = dVar.h();
                        setPassThroughIsSet(true);
                    }
                    break;
                case 9:
                    if (b2 != 8) {
                        e.a(dVar, b2);
                    } else {
                        this.notifyId = dVar.h();
                        setNotifyIdIsSet(true);
                    }
                    break;
                case 10:
                    if (b2 != 13) {
                        e.a(dVar, b2);
                    } else {
                        c cVarL = dVar.l();
                        this.extra = new HashMap(cVarL.f2156c * 2);
                        while (i2 < cVarL.f2156c) {
                            this.extra.put(dVar.p(), dVar.p());
                            i2++;
                        }
                        dVar.m();
                    }
                    break;
                case 11:
                    if (b2 != 13) {
                        e.a(dVar, b2);
                    } else {
                        c cVarL2 = dVar.l();
                        this.internal = new HashMap(cVarL2.f2156c * 2);
                        while (i2 < cVarL2.f2156c) {
                            this.internal.put(dVar.p(), dVar.p());
                            i2++;
                        }
                        dVar.m();
                    }
                    break;
                case 12:
                    if (b2 != 2) {
                        e.a(dVar, b2);
                    } else {
                        this.ignoreRegInfo = dVar.b();
                        setIgnoreRegInfoIsSet(true);
                    }
                    break;
                case 13:
                    if (b2 != 13) {
                        e.a(dVar, b2);
                    } else {
                        c cVarL3 = dVar.l();
                        this.apsProperFields = new HashMap(cVarL3.f2156c * 2);
                        while (i2 < cVarL3.f2156c) {
                            this.apsProperFields.put(dVar.p(), dVar.p());
                            i2++;
                        }
                        dVar.m();
                    }
                    break;
                default:
                    e.a(dVar, b2);
                    break;
            }
            dVar.f();
        }
    }

    public PushMetaInfo setApsProperFields(Map<String, String> map) {
        this.apsProperFields = map;
        return this;
    }

    public void setApsProperFieldsIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.apsProperFields = null;
    }

    public PushMetaInfo setDescription(String str) {
        this.description = str;
        return this;
    }

    public void setDescriptionIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.description = null;
    }

    public PushMetaInfo setExtra(Map<String, String> map) {
        this.extra = map;
        return this;
    }

    public void setExtraIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.extra = null;
    }

    public PushMetaInfo setId(String str) {
        this.id = str;
        return this;
    }

    public void setIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.id = null;
    }

    public PushMetaInfo setIgnoreRegInfo(boolean z2) {
        this.ignoreRegInfo = z2;
        setIgnoreRegInfoIsSet(true);
        return this;
    }

    public void setIgnoreRegInfoIsSet(boolean z2) {
        this.__isset_bit_vector.set(4, z2);
    }

    public PushMetaInfo setInternal(Map<String, String> map) {
        this.internal = map;
        return this;
    }

    public void setInternalIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.internal = null;
    }

    public PushMetaInfo setMessageTs(long j2) {
        this.messageTs = j2;
        setMessageTsIsSet(true);
        return this;
    }

    public void setMessageTsIsSet(boolean z2) {
        this.__isset_bit_vector.set(0, z2);
    }

    public PushMetaInfo setNotifyId(int i2) {
        this.notifyId = i2;
        setNotifyIdIsSet(true);
        return this;
    }

    public void setNotifyIdIsSet(boolean z2) {
        this.__isset_bit_vector.set(3, z2);
    }

    public PushMetaInfo setNotifyType(int i2) {
        this.notifyType = i2;
        setNotifyTypeIsSet(true);
        return this;
    }

    public void setNotifyTypeIsSet(boolean z2) {
        this.__isset_bit_vector.set(1, z2);
    }

    public PushMetaInfo setPassThrough(int i2) {
        this.passThrough = i2;
        setPassThroughIsSet(true);
        return this;
    }

    public void setPassThroughIsSet(boolean z2) {
        this.__isset_bit_vector.set(2, z2);
    }

    public PushMetaInfo setTitle(String str) {
        this.title = str;
        return this;
    }

    public void setTitleIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.title = null;
    }

    public PushMetaInfo setTopic(String str) {
        this.topic = str;
        return this;
    }

    public void setTopicIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.topic = null;
    }

    public PushMetaInfo setUrl(String str) {
        this.url = str;
        return this;
    }

    public void setUrlIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.url = null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("PushMetaInfo(");
        sb.append("id:");
        String str = this.id;
        if (str == null) {
            sb.append("null");
        } else {
            sb.append(PacketHelper.filterRegistrationPacketIdForLog(str));
        }
        sb.append(", ");
        sb.append("messageTs:");
        sb.append(this.messageTs);
        if (isSetTopic()) {
            sb.append(", ");
            sb.append("topic:");
            String str2 = this.topic;
            if (str2 == null) {
                sb.append("null");
            } else {
                sb.append(str2);
            }
        }
        if (isSetTitle()) {
            sb.append(", ");
            sb.append("title:");
            String str3 = this.title;
            if (str3 == null) {
                sb.append("null");
            } else {
                sb.append(str3);
            }
        }
        if (isSetDescription()) {
            sb.append(", ");
            sb.append("description:");
            String str4 = this.description;
            if (str4 == null) {
                sb.append("null");
            } else {
                sb.append(str4);
            }
        }
        if (isSetNotifyType()) {
            sb.append(", ");
            sb.append("notifyType:");
            sb.append(this.notifyType);
        }
        if (isSetUrl()) {
            sb.append(", ");
            sb.append("url:");
            String str5 = this.url;
            if (str5 == null) {
                sb.append("null");
            } else {
                sb.append(str5);
            }
        }
        if (isSetPassThrough()) {
            sb.append(", ");
            sb.append("passThrough:");
            sb.append(this.passThrough);
        }
        if (isSetNotifyId()) {
            sb.append(", ");
            sb.append("notifyId:");
            sb.append(this.notifyId);
        }
        if (isSetExtra()) {
            sb.append(", ");
            sb.append("extra:");
            Map<String, String> map = this.extra;
            if (map == null) {
                sb.append("null");
            } else {
                sb.append(map);
            }
        }
        if (isSetInternal()) {
            sb.append(", ");
            sb.append("internal:");
            Map<String, String> map2 = this.internal;
            if (map2 == null) {
                sb.append("null");
            } else {
                sb.append(map2);
            }
        }
        if (isSetIgnoreRegInfo()) {
            sb.append(", ");
            sb.append("ignoreRegInfo:");
            sb.append(this.ignoreRegInfo);
        }
        if (isSetApsProperFields()) {
            sb.append(", ");
            sb.append("apsProperFields:");
            Map<String, String> map3 = this.apsProperFields;
            if (map3 == null) {
                sb.append("null");
            } else {
                sb.append(map3);
            }
        }
        sb.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
        return sb.toString();
    }

    public void unsetApsProperFields() {
        this.apsProperFields = null;
    }

    public void unsetDescription() {
        this.description = null;
    }

    public void unsetExtra() {
        this.extra = null;
    }

    public void unsetId() {
        this.id = null;
    }

    public void unsetIgnoreRegInfo() {
        this.__isset_bit_vector.clear(4);
    }

    public void unsetInternal() {
        this.internal = null;
    }

    public void unsetMessageTs() {
        this.__isset_bit_vector.clear(0);
    }

    public void unsetNotifyId() {
        this.__isset_bit_vector.clear(3);
    }

    public void unsetNotifyType() {
        this.__isset_bit_vector.clear(1);
    }

    public void unsetPassThrough() {
        this.__isset_bit_vector.clear(2);
    }

    public void unsetTitle() {
        this.title = null;
    }

    public void unsetTopic() {
        this.topic = null;
    }

    public void unsetUrl() {
        this.url = null;
    }

    public void validate() throws TException {
        if (this.id != null) {
            return;
        }
        throw new TProtocolException("Required field 'id' was not present! Struct: " + toString());
    }

    @Override // org.apache.thrift.TBase
    public void write(d dVar) throws TException {
        validate();
        dVar.J(STRUCT_DESC);
        if (this.id != null) {
            dVar.w(ID_FIELD_DESC);
            dVar.I(this.id);
            dVar.x();
        }
        dVar.w(MESSAGE_TS_FIELD_DESC);
        dVar.B(this.messageTs);
        dVar.x();
        if (this.topic != null && isSetTopic()) {
            dVar.w(TOPIC_FIELD_DESC);
            dVar.I(this.topic);
            dVar.x();
        }
        if (this.title != null && isSetTitle()) {
            dVar.w(TITLE_FIELD_DESC);
            dVar.I(this.title);
            dVar.x();
        }
        if (this.description != null && isSetDescription()) {
            dVar.w(DESCRIPTION_FIELD_DESC);
            dVar.I(this.description);
            dVar.x();
        }
        if (isSetNotifyType()) {
            dVar.w(NOTIFY_TYPE_FIELD_DESC);
            dVar.A(this.notifyType);
            dVar.x();
        }
        if (this.url != null && isSetUrl()) {
            dVar.w(URL_FIELD_DESC);
            dVar.I(this.url);
            dVar.x();
        }
        if (isSetPassThrough()) {
            dVar.w(PASS_THROUGH_FIELD_DESC);
            dVar.A(this.passThrough);
            dVar.x();
        }
        if (isSetNotifyId()) {
            dVar.w(NOTIFY_ID_FIELD_DESC);
            dVar.A(this.notifyId);
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
        if (this.internal != null && isSetInternal()) {
            dVar.w(INTERNAL_FIELD_DESC);
            dVar.E(new c((byte) 11, (byte) 11, this.internal.size()));
            for (Map.Entry<String, String> entry2 : this.internal.entrySet()) {
                dVar.I(entry2.getKey());
                dVar.I(entry2.getValue());
            }
            dVar.F();
            dVar.x();
        }
        if (isSetIgnoreRegInfo()) {
            dVar.w(IGNORE_REG_INFO_FIELD_DESC);
            dVar.u(this.ignoreRegInfo);
            dVar.x();
        }
        if (this.apsProperFields != null && isSetApsProperFields()) {
            dVar.w(APS_PROPER_FIELDS_FIELD_DESC);
            dVar.E(new c((byte) 11, (byte) 11, this.apsProperFields.size()));
            for (Map.Entry<String, String> entry3 : this.apsProperFields.entrySet()) {
                dVar.I(entry3.getKey());
                dVar.I(entry3.getValue());
            }
            dVar.F();
            dVar.x();
        }
        dVar.y();
        dVar.K();
    }

    @Override // java.lang.Comparable
    public int compareTo(PushMetaInfo pushMetaInfo) {
        int i2;
        int iL;
        int i3;
        int i4;
        int iD;
        int iD2;
        int iG;
        int iD3;
        int iG2;
        int iG3;
        int iG4;
        int iE;
        int iG5;
        if (!getClass().equals(pushMetaInfo.getClass())) {
            return getClass().getName().compareTo(pushMetaInfo.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetId()).compareTo(Boolean.valueOf(pushMetaInfo.isSetId()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetId() && (iG5 = g0.a.g(this.id, pushMetaInfo.id)) != 0) {
            return iG5;
        }
        int iCompareTo2 = Boolean.valueOf(isSetMessageTs()).compareTo(Boolean.valueOf(pushMetaInfo.isSetMessageTs()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (isSetMessageTs() && (iE = g0.a.e(this.messageTs, pushMetaInfo.messageTs)) != 0) {
            return iE;
        }
        int iCompareTo3 = Boolean.valueOf(isSetTopic()).compareTo(Boolean.valueOf(pushMetaInfo.isSetTopic()));
        if (iCompareTo3 != 0) {
            return iCompareTo3;
        }
        if (isSetTopic() && (iG4 = g0.a.g(this.topic, pushMetaInfo.topic)) != 0) {
            return iG4;
        }
        int iCompareTo4 = Boolean.valueOf(isSetTitle()).compareTo(Boolean.valueOf(pushMetaInfo.isSetTitle()));
        if (iCompareTo4 != 0) {
            return iCompareTo4;
        }
        if (isSetTitle() && (iG3 = g0.a.g(this.title, pushMetaInfo.title)) != 0) {
            return iG3;
        }
        int iCompareTo5 = Boolean.valueOf(isSetDescription()).compareTo(Boolean.valueOf(pushMetaInfo.isSetDescription()));
        if (iCompareTo5 != 0) {
            return iCompareTo5;
        }
        if (isSetDescription() && (iG2 = g0.a.g(this.description, pushMetaInfo.description)) != 0) {
            return iG2;
        }
        int iCompareTo6 = Boolean.valueOf(isSetNotifyType()).compareTo(Boolean.valueOf(pushMetaInfo.isSetNotifyType()));
        if (iCompareTo6 != 0) {
            return iCompareTo6;
        }
        if (isSetNotifyType() && (iD3 = g0.a.d(this.notifyType, pushMetaInfo.notifyType)) != 0) {
            return iD3;
        }
        int iCompareTo7 = Boolean.valueOf(isSetUrl()).compareTo(Boolean.valueOf(pushMetaInfo.isSetUrl()));
        if (iCompareTo7 != 0) {
            return iCompareTo7;
        }
        if (isSetUrl() && (iG = g0.a.g(this.url, pushMetaInfo.url)) != 0) {
            return iG;
        }
        int iCompareTo8 = Boolean.valueOf(isSetPassThrough()).compareTo(Boolean.valueOf(pushMetaInfo.isSetPassThrough()));
        if (iCompareTo8 != 0) {
            return iCompareTo8;
        }
        if (isSetPassThrough() && (iD2 = g0.a.d(this.passThrough, pushMetaInfo.passThrough)) != 0) {
            return iD2;
        }
        int iCompareTo9 = Boolean.valueOf(isSetNotifyId()).compareTo(Boolean.valueOf(pushMetaInfo.isSetNotifyId()));
        if (iCompareTo9 != 0) {
            return iCompareTo9;
        }
        if (isSetNotifyId() && (iD = g0.a.d(this.notifyId, pushMetaInfo.notifyId)) != 0) {
            return iD;
        }
        int iCompareTo10 = Boolean.valueOf(isSetExtra()).compareTo(Boolean.valueOf(pushMetaInfo.isSetExtra()));
        if (iCompareTo10 != 0) {
            return iCompareTo10;
        }
        if (isSetExtra() && (i4 = g0.a.i(this.extra, pushMetaInfo.extra)) != 0) {
            return i4;
        }
        int iCompareTo11 = Boolean.valueOf(isSetInternal()).compareTo(Boolean.valueOf(pushMetaInfo.isSetInternal()));
        if (iCompareTo11 != 0) {
            return iCompareTo11;
        }
        if (isSetInternal() && (i3 = g0.a.i(this.internal, pushMetaInfo.internal)) != 0) {
            return i3;
        }
        int iCompareTo12 = Boolean.valueOf(isSetIgnoreRegInfo()).compareTo(Boolean.valueOf(pushMetaInfo.isSetIgnoreRegInfo()));
        if (iCompareTo12 != 0) {
            return iCompareTo12;
        }
        if (isSetIgnoreRegInfo() && (iL = g0.a.l(this.ignoreRegInfo, pushMetaInfo.ignoreRegInfo)) != 0) {
            return iL;
        }
        int iCompareTo13 = Boolean.valueOf(isSetApsProperFields()).compareTo(Boolean.valueOf(pushMetaInfo.isSetApsProperFields()));
        if (iCompareTo13 != 0) {
            return iCompareTo13;
        }
        if (!isSetApsProperFields() || (i2 = g0.a.i(this.apsProperFields, pushMetaInfo.apsProperFields)) == 0) {
            return 0;
        }
        return i2;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public PushMetaInfo m25deepCopy() {
        return new PushMetaInfo(this);
    }

    public boolean equals(PushMetaInfo pushMetaInfo) {
        if (pushMetaInfo == null) {
            return false;
        }
        boolean zIsSetId = isSetId();
        boolean zIsSetId2 = pushMetaInfo.isSetId();
        if (((zIsSetId || zIsSetId2) && !(zIsSetId && zIsSetId2 && this.id.equals(pushMetaInfo.id))) || this.messageTs != pushMetaInfo.messageTs) {
            return false;
        }
        boolean zIsSetTopic = isSetTopic();
        boolean zIsSetTopic2 = pushMetaInfo.isSetTopic();
        if ((zIsSetTopic || zIsSetTopic2) && !(zIsSetTopic && zIsSetTopic2 && this.topic.equals(pushMetaInfo.topic))) {
            return false;
        }
        boolean zIsSetTitle = isSetTitle();
        boolean zIsSetTitle2 = pushMetaInfo.isSetTitle();
        if ((zIsSetTitle || zIsSetTitle2) && !(zIsSetTitle && zIsSetTitle2 && this.title.equals(pushMetaInfo.title))) {
            return false;
        }
        boolean zIsSetDescription = isSetDescription();
        boolean zIsSetDescription2 = pushMetaInfo.isSetDescription();
        if ((zIsSetDescription || zIsSetDescription2) && !(zIsSetDescription && zIsSetDescription2 && this.description.equals(pushMetaInfo.description))) {
            return false;
        }
        boolean zIsSetNotifyType = isSetNotifyType();
        boolean zIsSetNotifyType2 = pushMetaInfo.isSetNotifyType();
        if ((zIsSetNotifyType || zIsSetNotifyType2) && !(zIsSetNotifyType && zIsSetNotifyType2 && this.notifyType == pushMetaInfo.notifyType)) {
            return false;
        }
        boolean zIsSetUrl = isSetUrl();
        boolean zIsSetUrl2 = pushMetaInfo.isSetUrl();
        if ((zIsSetUrl || zIsSetUrl2) && !(zIsSetUrl && zIsSetUrl2 && this.url.equals(pushMetaInfo.url))) {
            return false;
        }
        boolean zIsSetPassThrough = isSetPassThrough();
        boolean zIsSetPassThrough2 = pushMetaInfo.isSetPassThrough();
        if ((zIsSetPassThrough || zIsSetPassThrough2) && !(zIsSetPassThrough && zIsSetPassThrough2 && this.passThrough == pushMetaInfo.passThrough)) {
            return false;
        }
        boolean zIsSetNotifyId = isSetNotifyId();
        boolean zIsSetNotifyId2 = pushMetaInfo.isSetNotifyId();
        if ((zIsSetNotifyId || zIsSetNotifyId2) && !(zIsSetNotifyId && zIsSetNotifyId2 && this.notifyId == pushMetaInfo.notifyId)) {
            return false;
        }
        boolean zIsSetExtra = isSetExtra();
        boolean zIsSetExtra2 = pushMetaInfo.isSetExtra();
        if ((zIsSetExtra || zIsSetExtra2) && !(zIsSetExtra && zIsSetExtra2 && this.extra.equals(pushMetaInfo.extra))) {
            return false;
        }
        boolean zIsSetInternal = isSetInternal();
        boolean zIsSetInternal2 = pushMetaInfo.isSetInternal();
        if ((zIsSetInternal || zIsSetInternal2) && !(zIsSetInternal && zIsSetInternal2 && this.internal.equals(pushMetaInfo.internal))) {
            return false;
        }
        boolean zIsSetIgnoreRegInfo = isSetIgnoreRegInfo();
        boolean zIsSetIgnoreRegInfo2 = pushMetaInfo.isSetIgnoreRegInfo();
        if ((zIsSetIgnoreRegInfo || zIsSetIgnoreRegInfo2) && !(zIsSetIgnoreRegInfo && zIsSetIgnoreRegInfo2 && this.ignoreRegInfo == pushMetaInfo.ignoreRegInfo)) {
            return false;
        }
        boolean zIsSetApsProperFields = isSetApsProperFields();
        boolean zIsSetApsProperFields2 = pushMetaInfo.isSetApsProperFields();
        if (zIsSetApsProperFields || zIsSetApsProperFields2) {
            return zIsSetApsProperFields && zIsSetApsProperFields2 && this.apsProperFields.equals(pushMetaInfo.apsProperFields);
        }
        return true;
    }

    public PushMetaInfo(String str, long j2) {
        this();
        this.id = str;
        this.messageTs = j2;
        setMessageTsIsSet(true);
    }

    public PushMetaInfo(PushMetaInfo pushMetaInfo) {
        BitSet bitSet = new BitSet(5);
        this.__isset_bit_vector = bitSet;
        bitSet.clear();
        this.__isset_bit_vector.or(pushMetaInfo.__isset_bit_vector);
        if (pushMetaInfo.isSetId()) {
            this.id = pushMetaInfo.id;
        }
        this.messageTs = pushMetaInfo.messageTs;
        if (pushMetaInfo.isSetTopic()) {
            this.topic = pushMetaInfo.topic;
        }
        if (pushMetaInfo.isSetTitle()) {
            this.title = pushMetaInfo.title;
        }
        if (pushMetaInfo.isSetDescription()) {
            this.description = pushMetaInfo.description;
        }
        this.notifyType = pushMetaInfo.notifyType;
        if (pushMetaInfo.isSetUrl()) {
            this.url = pushMetaInfo.url;
        }
        this.passThrough = pushMetaInfo.passThrough;
        this.notifyId = pushMetaInfo.notifyId;
        if (pushMetaInfo.isSetExtra()) {
            HashMap map = new HashMap();
            for (Map.Entry<String, String> entry : pushMetaInfo.extra.entrySet()) {
                map.put(entry.getKey(), entry.getValue());
            }
            this.extra = map;
        }
        if (pushMetaInfo.isSetInternal()) {
            HashMap map2 = new HashMap();
            for (Map.Entry<String, String> entry2 : pushMetaInfo.internal.entrySet()) {
                map2.put(entry2.getKey(), entry2.getValue());
            }
            this.internal = map2;
        }
        this.ignoreRegInfo = pushMetaInfo.ignoreRegInfo;
        if (pushMetaInfo.isSetApsProperFields()) {
            HashMap map3 = new HashMap();
            for (Map.Entry<String, String> entry3 : pushMetaInfo.apsProperFields.entrySet()) {
                map3.put(entry3.getKey(), entry3.getValue());
            }
            this.apsProperFields = map3;
        }
    }
}
