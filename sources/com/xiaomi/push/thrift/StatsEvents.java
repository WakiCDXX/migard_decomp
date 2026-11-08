package com.xiaomi.push.thrift;

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
public class StatsEvents implements TBase<StatsEvents, Object>, Serializable, Cloneable {
    public List<StatsEvent> events;
    public String operator;
    public String uuid;
    private static final g STRUCT_DESC = new g("StatsEvents");
    private static final a UUID_FIELD_DESC = new a("", (byte) 11, 1);
    private static final a OPERATOR_FIELD_DESC = new a("", (byte) 11, 2);
    private static final a EVENTS_FIELD_DESC = new a("", (byte) 15, 3);

    public StatsEvents() {
    }

    public void addToEvents(StatsEvent statsEvent) {
        if (this.events == null) {
            this.events = new ArrayList();
        }
        this.events.add(statsEvent);
    }

    public void clear() {
        this.uuid = null;
        this.operator = null;
        this.events = null;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof StatsEvents)) {
            return equals((StatsEvents) obj);
        }
        return false;
    }

    public List<StatsEvent> getEvents() {
        return this.events;
    }

    public Iterator<StatsEvent> getEventsIterator() {
        List<StatsEvent> list = this.events;
        if (list == null) {
            return null;
        }
        return list.iterator();
    }

    public int getEventsSize() {
        List<StatsEvent> list = this.events;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public String getOperator() {
        return this.operator;
    }

    public String getUuid() {
        return this.uuid;
    }

    public int hashCode() {
        return 0;
    }

    public boolean isSetEvents() {
        return this.events != null;
    }

    public boolean isSetOperator() {
        return this.operator != null;
    }

    public boolean isSetUuid() {
        return this.uuid != null;
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
            short s2 = aVarE.f2151c;
            if (s2 != 1) {
                if (s2 != 2) {
                    if (s2 != 3) {
                        e.a(dVar, b2);
                    } else if (b2 == 15) {
                        b bVarJ = dVar.j();
                        this.events = new ArrayList(bVarJ.f2153b);
                        for (int i2 = 0; i2 < bVarJ.f2153b; i2++) {
                            StatsEvent statsEvent = new StatsEvent();
                            statsEvent.read(dVar);
                            this.events.add(statsEvent);
                        }
                        dVar.k();
                    } else {
                        e.a(dVar, b2);
                    }
                } else if (b2 == 11) {
                    this.operator = dVar.p();
                } else {
                    e.a(dVar, b2);
                }
            } else if (b2 == 11) {
                this.uuid = dVar.p();
            } else {
                e.a(dVar, b2);
            }
            dVar.f();
        }
    }

    public StatsEvents setEvents(List<StatsEvent> list) {
        this.events = list;
        return this;
    }

    public void setEventsIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.events = null;
    }

    public StatsEvents setOperator(String str) {
        this.operator = str;
        return this;
    }

    public void setOperatorIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.operator = null;
    }

    public StatsEvents setUuid(String str) {
        this.uuid = str;
        return this;
    }

    public void setUuidIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.uuid = null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("StatsEvents(");
        sb.append("uuid:");
        String str = this.uuid;
        if (str == null) {
            sb.append("null");
        } else {
            sb.append(str);
        }
        if (isSetOperator()) {
            sb.append(", ");
            sb.append("operator:");
            String str2 = this.operator;
            if (str2 == null) {
                sb.append("null");
            } else {
                sb.append(str2);
            }
        }
        sb.append(", ");
        sb.append("events:");
        List<StatsEvent> list = this.events;
        if (list == null) {
            sb.append("null");
        } else {
            sb.append(list);
        }
        sb.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
        return sb.toString();
    }

    public void unsetEvents() {
        this.events = null;
    }

    public void unsetOperator() {
        this.operator = null;
    }

    public void unsetUuid() {
        this.uuid = null;
    }

    public void validate() throws TException {
        if (this.uuid == null) {
            throw new TProtocolException("Required field 'uuid' was not present! Struct: " + toString());
        }
        if (this.events != null) {
            return;
        }
        throw new TProtocolException("Required field 'events' was not present! Struct: " + toString());
    }

    @Override // org.apache.thrift.TBase
    public void write(d dVar) throws TException {
        validate();
        dVar.J(STRUCT_DESC);
        if (this.uuid != null) {
            dVar.w(UUID_FIELD_DESC);
            dVar.I(this.uuid);
            dVar.x();
        }
        if (this.operator != null && isSetOperator()) {
            dVar.w(OPERATOR_FIELD_DESC);
            dVar.I(this.operator);
            dVar.x();
        }
        if (this.events != null) {
            dVar.w(EVENTS_FIELD_DESC);
            dVar.C(new b((byte) 12, this.events.size()));
            Iterator<StatsEvent> it = this.events.iterator();
            while (it.hasNext()) {
                it.next().write(dVar);
            }
            dVar.D();
            dVar.x();
        }
        dVar.y();
        dVar.K();
    }

    public StatsEvents(String str, List<StatsEvent> list) {
        this();
        this.uuid = str;
        this.events = list;
    }

    @Override // java.lang.Comparable
    public int compareTo(StatsEvents statsEvents) {
        int iH;
        int iG;
        int iG2;
        if (!getClass().equals(statsEvents.getClass())) {
            return getClass().getName().compareTo(statsEvents.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetUuid()).compareTo(Boolean.valueOf(statsEvents.isSetUuid()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetUuid() && (iG2 = g0.a.g(this.uuid, statsEvents.uuid)) != 0) {
            return iG2;
        }
        int iCompareTo2 = Boolean.valueOf(isSetOperator()).compareTo(Boolean.valueOf(statsEvents.isSetOperator()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (isSetOperator() && (iG = g0.a.g(this.operator, statsEvents.operator)) != 0) {
            return iG;
        }
        int iCompareTo3 = Boolean.valueOf(isSetEvents()).compareTo(Boolean.valueOf(statsEvents.isSetEvents()));
        if (iCompareTo3 != 0) {
            return iCompareTo3;
        }
        if (!isSetEvents() || (iH = g0.a.h(this.events, statsEvents.events)) == 0) {
            return 0;
        }
        return iH;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public StatsEvents m15deepCopy() {
        return new StatsEvents(this);
    }

    public boolean equals(StatsEvents statsEvents) {
        if (statsEvents == null) {
            return false;
        }
        boolean zIsSetUuid = isSetUuid();
        boolean zIsSetUuid2 = statsEvents.isSetUuid();
        if ((zIsSetUuid || zIsSetUuid2) && !(zIsSetUuid && zIsSetUuid2 && this.uuid.equals(statsEvents.uuid))) {
            return false;
        }
        boolean zIsSetOperator = isSetOperator();
        boolean zIsSetOperator2 = statsEvents.isSetOperator();
        if ((zIsSetOperator || zIsSetOperator2) && !(zIsSetOperator && zIsSetOperator2 && this.operator.equals(statsEvents.operator))) {
            return false;
        }
        boolean zIsSetEvents = isSetEvents();
        boolean zIsSetEvents2 = statsEvents.isSetEvents();
        if (zIsSetEvents || zIsSetEvents2) {
            return zIsSetEvents && zIsSetEvents2 && this.events.equals(statsEvents.events);
        }
        return true;
    }

    public StatsEvents(StatsEvents statsEvents) {
        if (statsEvents.isSetUuid()) {
            this.uuid = statsEvents.uuid;
        }
        if (statsEvents.isSetOperator()) {
            this.operator = statsEvents.operator;
        }
        if (statsEvents.isSetEvents()) {
            ArrayList arrayList = new ArrayList();
            Iterator<StatsEvent> it = statsEvents.events.iterator();
            while (it.hasNext()) {
                arrayList.add(new StatsEvent(it.next()));
            }
            this.events = arrayList;
        }
    }
}
