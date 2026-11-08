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
public class XmPushActionSubscription implements TBase<XmPushActionSubscription, Object>, Serializable, Cloneable {
    public List<String> aliases;
    public String appId;
    public String category;
    public String debug;
    public String id;
    public String packageName;
    public Target target;
    public String topic;
    private static final g STRUCT_DESC = new g("XmPushActionSubscription");
    private static final a DEBUG_FIELD_DESC = new a("", (byte) 11, 1);
    private static final a TARGET_FIELD_DESC = new a("", (byte) 12, 2);
    private static final a ID_FIELD_DESC = new a("", (byte) 11, 3);
    private static final a APP_ID_FIELD_DESC = new a("", (byte) 11, 4);
    private static final a TOPIC_FIELD_DESC = new a("", (byte) 11, 5);
    private static final a PACKAGE_NAME_FIELD_DESC = new a("", (byte) 11, 6);
    private static final a CATEGORY_FIELD_DESC = new a("", (byte) 11, 7);
    private static final a ALIASES_FIELD_DESC = new a("", (byte) 15, 8);

    public XmPushActionSubscription() {
    }

    public void addToAliases(String str) {
        if (this.aliases == null) {
            this.aliases = new ArrayList();
        }
        this.aliases.add(str);
    }

    public void clear() {
        this.debug = null;
        this.target = null;
        this.id = null;
        this.appId = null;
        this.topic = null;
        this.packageName = null;
        this.category = null;
        this.aliases = null;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof XmPushActionSubscription)) {
            return equals((XmPushActionSubscription) obj);
        }
        return false;
    }

    public List<String> getAliases() {
        return this.aliases;
    }

    public Iterator<String> getAliasesIterator() {
        List<String> list = this.aliases;
        if (list == null) {
            return null;
        }
        return list.iterator();
    }

    public int getAliasesSize() {
        List<String> list = this.aliases;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public String getAppId() {
        return this.appId;
    }

    public String getCategory() {
        return this.category;
    }

    public String getDebug() {
        return this.debug;
    }

    public String getId() {
        return this.id;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public Target getTarget() {
        return this.target;
    }

    public String getTopic() {
        return this.topic;
    }

    public int hashCode() {
        return 0;
    }

    public boolean isSetAliases() {
        return this.aliases != null;
    }

    public boolean isSetAppId() {
        return this.appId != null;
    }

    public boolean isSetCategory() {
        return this.category != null;
    }

    public boolean isSetDebug() {
        return this.debug != null;
    }

    public boolean isSetId() {
        return this.id != null;
    }

    public boolean isSetPackageName() {
        return this.packageName != null;
    }

    public boolean isSetTarget() {
        return this.target != null;
    }

    public boolean isSetTopic() {
        return this.topic != null;
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
                        this.debug = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 2:
                    if (b2 == 12) {
                        Target target = new Target();
                        this.target = target;
                        target.read(dVar);
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 3:
                    if (b2 == 11) {
                        this.id = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 4:
                    if (b2 == 11) {
                        this.appId = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 5:
                    if (b2 == 11) {
                        this.topic = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 6:
                    if (b2 == 11) {
                        this.packageName = dVar.p();
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
                    if (b2 == 15) {
                        b bVarJ = dVar.j();
                        this.aliases = new ArrayList(bVarJ.f2153b);
                        for (int i2 = 0; i2 < bVarJ.f2153b; i2++) {
                            this.aliases.add(dVar.p());
                        }
                        dVar.k();
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

    public XmPushActionSubscription setAliases(List<String> list) {
        this.aliases = list;
        return this;
    }

    public void setAliasesIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.aliases = null;
    }

    public XmPushActionSubscription setAppId(String str) {
        this.appId = str;
        return this;
    }

    public void setAppIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.appId = null;
    }

    public XmPushActionSubscription setCategory(String str) {
        this.category = str;
        return this;
    }

    public void setCategoryIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.category = null;
    }

    public XmPushActionSubscription setDebug(String str) {
        this.debug = str;
        return this;
    }

    public void setDebugIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.debug = null;
    }

    public XmPushActionSubscription setId(String str) {
        this.id = str;
        return this;
    }

    public void setIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.id = null;
    }

    public XmPushActionSubscription setPackageName(String str) {
        this.packageName = str;
        return this;
    }

    public void setPackageNameIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.packageName = null;
    }

    public XmPushActionSubscription setTarget(Target target) {
        this.target = target;
        return this;
    }

    public void setTargetIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.target = null;
    }

    public XmPushActionSubscription setTopic(String str) {
        this.topic = str;
        return this;
    }

    public void setTopicIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.topic = null;
    }

    public String toString() {
        boolean z2;
        StringBuilder sb = new StringBuilder("XmPushActionSubscription(");
        boolean z3 = false;
        if (isSetDebug()) {
            sb.append("debug:");
            String str = this.debug;
            if (str == null) {
                sb.append("null");
            } else {
                sb.append(str);
            }
            z2 = false;
        } else {
            z2 = true;
        }
        if (isSetTarget()) {
            if (!z2) {
                sb.append(", ");
            }
            sb.append("target:");
            Target target = this.target;
            if (target == null) {
                sb.append("null");
            } else {
                sb.append(target);
            }
        } else {
            z3 = z2;
        }
        if (!z3) {
            sb.append(", ");
        }
        sb.append("id:");
        String str2 = this.id;
        if (str2 == null) {
            sb.append("null");
        } else {
            sb.append(str2);
        }
        sb.append(", ");
        sb.append("appId:");
        String str3 = this.appId;
        if (str3 == null) {
            sb.append("null");
        } else {
            sb.append(str3);
        }
        sb.append(", ");
        sb.append("topic:");
        String str4 = this.topic;
        if (str4 == null) {
            sb.append("null");
        } else {
            sb.append(str4);
        }
        if (isSetPackageName()) {
            sb.append(", ");
            sb.append("packageName:");
            String str5 = this.packageName;
            if (str5 == null) {
                sb.append("null");
            } else {
                sb.append(str5);
            }
        }
        if (isSetCategory()) {
            sb.append(", ");
            sb.append("category:");
            String str6 = this.category;
            if (str6 == null) {
                sb.append("null");
            } else {
                sb.append(str6);
            }
        }
        if (isSetAliases()) {
            sb.append(", ");
            sb.append("aliases:");
            List<String> list = this.aliases;
            if (list == null) {
                sb.append("null");
            } else {
                sb.append(list);
            }
        }
        sb.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
        return sb.toString();
    }

    public void unsetAliases() {
        this.aliases = null;
    }

    public void unsetAppId() {
        this.appId = null;
    }

    public void unsetCategory() {
        this.category = null;
    }

    public void unsetDebug() {
        this.debug = null;
    }

    public void unsetId() {
        this.id = null;
    }

    public void unsetPackageName() {
        this.packageName = null;
    }

    public void unsetTarget() {
        this.target = null;
    }

    public void unsetTopic() {
        this.topic = null;
    }

    public void validate() throws TException {
        if (this.id == null) {
            throw new TProtocolException("Required field 'id' was not present! Struct: " + toString());
        }
        if (this.appId == null) {
            throw new TProtocolException("Required field 'appId' was not present! Struct: " + toString());
        }
        if (this.topic != null) {
            return;
        }
        throw new TProtocolException("Required field 'topic' was not present! Struct: " + toString());
    }

    @Override // org.apache.thrift.TBase
    public void write(d dVar) throws TException {
        validate();
        dVar.J(STRUCT_DESC);
        if (this.debug != null && isSetDebug()) {
            dVar.w(DEBUG_FIELD_DESC);
            dVar.I(this.debug);
            dVar.x();
        }
        if (this.target != null && isSetTarget()) {
            dVar.w(TARGET_FIELD_DESC);
            this.target.write(dVar);
            dVar.x();
        }
        if (this.id != null) {
            dVar.w(ID_FIELD_DESC);
            dVar.I(this.id);
            dVar.x();
        }
        if (this.appId != null) {
            dVar.w(APP_ID_FIELD_DESC);
            dVar.I(this.appId);
            dVar.x();
        }
        if (this.topic != null) {
            dVar.w(TOPIC_FIELD_DESC);
            dVar.I(this.topic);
            dVar.x();
        }
        if (this.packageName != null && isSetPackageName()) {
            dVar.w(PACKAGE_NAME_FIELD_DESC);
            dVar.I(this.packageName);
            dVar.x();
        }
        if (this.category != null && isSetCategory()) {
            dVar.w(CATEGORY_FIELD_DESC);
            dVar.I(this.category);
            dVar.x();
        }
        if (this.aliases != null && isSetAliases()) {
            dVar.w(ALIASES_FIELD_DESC);
            dVar.C(new b((byte) 11, this.aliases.size()));
            Iterator<String> it = this.aliases.iterator();
            while (it.hasNext()) {
                dVar.I(it.next());
            }
            dVar.D();
            dVar.x();
        }
        dVar.y();
        dVar.K();
    }

    public XmPushActionSubscription(String str, String str2, String str3) {
        this();
        this.id = str;
        this.appId = str2;
        this.topic = str3;
    }

    @Override // java.lang.Comparable
    public int compareTo(XmPushActionSubscription xmPushActionSubscription) {
        int iH;
        int iG;
        int iG2;
        int iG3;
        int iG4;
        int iG5;
        int iF;
        int iG6;
        if (!getClass().equals(xmPushActionSubscription.getClass())) {
            return getClass().getName().compareTo(xmPushActionSubscription.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetDebug()).compareTo(Boolean.valueOf(xmPushActionSubscription.isSetDebug()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetDebug() && (iG6 = g0.a.g(this.debug, xmPushActionSubscription.debug)) != 0) {
            return iG6;
        }
        int iCompareTo2 = Boolean.valueOf(isSetTarget()).compareTo(Boolean.valueOf(xmPushActionSubscription.isSetTarget()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (isSetTarget() && (iF = g0.a.f(this.target, xmPushActionSubscription.target)) != 0) {
            return iF;
        }
        int iCompareTo3 = Boolean.valueOf(isSetId()).compareTo(Boolean.valueOf(xmPushActionSubscription.isSetId()));
        if (iCompareTo3 != 0) {
            return iCompareTo3;
        }
        if (isSetId() && (iG5 = g0.a.g(this.id, xmPushActionSubscription.id)) != 0) {
            return iG5;
        }
        int iCompareTo4 = Boolean.valueOf(isSetAppId()).compareTo(Boolean.valueOf(xmPushActionSubscription.isSetAppId()));
        if (iCompareTo4 != 0) {
            return iCompareTo4;
        }
        if (isSetAppId() && (iG4 = g0.a.g(this.appId, xmPushActionSubscription.appId)) != 0) {
            return iG4;
        }
        int iCompareTo5 = Boolean.valueOf(isSetTopic()).compareTo(Boolean.valueOf(xmPushActionSubscription.isSetTopic()));
        if (iCompareTo5 != 0) {
            return iCompareTo5;
        }
        if (isSetTopic() && (iG3 = g0.a.g(this.topic, xmPushActionSubscription.topic)) != 0) {
            return iG3;
        }
        int iCompareTo6 = Boolean.valueOf(isSetPackageName()).compareTo(Boolean.valueOf(xmPushActionSubscription.isSetPackageName()));
        if (iCompareTo6 != 0) {
            return iCompareTo6;
        }
        if (isSetPackageName() && (iG2 = g0.a.g(this.packageName, xmPushActionSubscription.packageName)) != 0) {
            return iG2;
        }
        int iCompareTo7 = Boolean.valueOf(isSetCategory()).compareTo(Boolean.valueOf(xmPushActionSubscription.isSetCategory()));
        if (iCompareTo7 != 0) {
            return iCompareTo7;
        }
        if (isSetCategory() && (iG = g0.a.g(this.category, xmPushActionSubscription.category)) != 0) {
            return iG;
        }
        int iCompareTo8 = Boolean.valueOf(isSetAliases()).compareTo(Boolean.valueOf(xmPushActionSubscription.isSetAliases()));
        if (iCompareTo8 != 0) {
            return iCompareTo8;
        }
        if (!isSetAliases() || (iH = g0.a.h(this.aliases, xmPushActionSubscription.aliases)) == 0) {
            return 0;
        }
        return iH;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public XmPushActionSubscription m44deepCopy() {
        return new XmPushActionSubscription(this);
    }

    public boolean equals(XmPushActionSubscription xmPushActionSubscription) {
        if (xmPushActionSubscription == null) {
            return false;
        }
        boolean zIsSetDebug = isSetDebug();
        boolean zIsSetDebug2 = xmPushActionSubscription.isSetDebug();
        if ((zIsSetDebug || zIsSetDebug2) && !(zIsSetDebug && zIsSetDebug2 && this.debug.equals(xmPushActionSubscription.debug))) {
            return false;
        }
        boolean zIsSetTarget = isSetTarget();
        boolean zIsSetTarget2 = xmPushActionSubscription.isSetTarget();
        if ((zIsSetTarget || zIsSetTarget2) && !(zIsSetTarget && zIsSetTarget2 && this.target.equals(xmPushActionSubscription.target))) {
            return false;
        }
        boolean zIsSetId = isSetId();
        boolean zIsSetId2 = xmPushActionSubscription.isSetId();
        if ((zIsSetId || zIsSetId2) && !(zIsSetId && zIsSetId2 && this.id.equals(xmPushActionSubscription.id))) {
            return false;
        }
        boolean zIsSetAppId = isSetAppId();
        boolean zIsSetAppId2 = xmPushActionSubscription.isSetAppId();
        if ((zIsSetAppId || zIsSetAppId2) && !(zIsSetAppId && zIsSetAppId2 && this.appId.equals(xmPushActionSubscription.appId))) {
            return false;
        }
        boolean zIsSetTopic = isSetTopic();
        boolean zIsSetTopic2 = xmPushActionSubscription.isSetTopic();
        if ((zIsSetTopic || zIsSetTopic2) && !(zIsSetTopic && zIsSetTopic2 && this.topic.equals(xmPushActionSubscription.topic))) {
            return false;
        }
        boolean zIsSetPackageName = isSetPackageName();
        boolean zIsSetPackageName2 = xmPushActionSubscription.isSetPackageName();
        if ((zIsSetPackageName || zIsSetPackageName2) && !(zIsSetPackageName && zIsSetPackageName2 && this.packageName.equals(xmPushActionSubscription.packageName))) {
            return false;
        }
        boolean zIsSetCategory = isSetCategory();
        boolean zIsSetCategory2 = xmPushActionSubscription.isSetCategory();
        if ((zIsSetCategory || zIsSetCategory2) && !(zIsSetCategory && zIsSetCategory2 && this.category.equals(xmPushActionSubscription.category))) {
            return false;
        }
        boolean zIsSetAliases = isSetAliases();
        boolean zIsSetAliases2 = xmPushActionSubscription.isSetAliases();
        if (zIsSetAliases || zIsSetAliases2) {
            return zIsSetAliases && zIsSetAliases2 && this.aliases.equals(xmPushActionSubscription.aliases);
        }
        return true;
    }

    public XmPushActionSubscription(XmPushActionSubscription xmPushActionSubscription) {
        if (xmPushActionSubscription.isSetDebug()) {
            this.debug = xmPushActionSubscription.debug;
        }
        if (xmPushActionSubscription.isSetTarget()) {
            this.target = new Target(xmPushActionSubscription.target);
        }
        if (xmPushActionSubscription.isSetId()) {
            this.id = xmPushActionSubscription.id;
        }
        if (xmPushActionSubscription.isSetAppId()) {
            this.appId = xmPushActionSubscription.appId;
        }
        if (xmPushActionSubscription.isSetTopic()) {
            this.topic = xmPushActionSubscription.topic;
        }
        if (xmPushActionSubscription.isSetPackageName()) {
            this.packageName = xmPushActionSubscription.packageName;
        }
        if (xmPushActionSubscription.isSetCategory()) {
            this.category = xmPushActionSubscription.category;
        }
        if (xmPushActionSubscription.isSetAliases()) {
            ArrayList arrayList = new ArrayList();
            Iterator<String> it = xmPushActionSubscription.aliases.iterator();
            while (it.hasNext()) {
                arrayList.add(it.next());
            }
            this.aliases = arrayList;
        }
    }
}
