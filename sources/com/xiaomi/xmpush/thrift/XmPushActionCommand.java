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
public class XmPushActionCommand implements TBase<XmPushActionCommand, Object>, Serializable, Cloneable {
    private static final int __CREATEDTS_ISSET_ID = 2;
    private static final int __RESPONSE2CLIENT_ISSET_ID = 1;
    private static final int __UPDATECACHE_ISSET_ID = 0;
    private BitSet __isset_bit_vector;
    public String appId;
    public String category;
    public List<String> cmdArgs;
    public String cmdName;
    public long createdTs;
    public String id;
    public String packageName;
    public boolean response2Client;
    public Target target;
    public boolean updateCache;
    private static final g STRUCT_DESC = new g("XmPushActionCommand");
    private static final a TARGET_FIELD_DESC = new a("", (byte) 12, 2);
    private static final a ID_FIELD_DESC = new a("", (byte) 11, 3);
    private static final a APP_ID_FIELD_DESC = new a("", (byte) 11, 4);
    private static final a CMD_NAME_FIELD_DESC = new a("", (byte) 11, 5);
    private static final a CMD_ARGS_FIELD_DESC = new a("", (byte) 15, 6);
    private static final a PACKAGE_NAME_FIELD_DESC = new a("", (byte) 11, 7);
    private static final a CATEGORY_FIELD_DESC = new a("", (byte) 11, 9);
    private static final a UPDATE_CACHE_FIELD_DESC = new a("", (byte) 2, 10);
    private static final a RESPONSE2_CLIENT_FIELD_DESC = new a("", (byte) 2, 11);
    private static final a CREATED_TS_FIELD_DESC = new a("", (byte) 10, 12);

    public XmPushActionCommand() {
        this.__isset_bit_vector = new BitSet(3);
        this.updateCache = false;
        this.response2Client = true;
    }

    public void addToCmdArgs(String str) {
        if (this.cmdArgs == null) {
            this.cmdArgs = new ArrayList();
        }
        this.cmdArgs.add(str);
    }

    public void clear() {
        this.target = null;
        this.id = null;
        this.appId = null;
        this.cmdName = null;
        this.cmdArgs = null;
        this.packageName = null;
        this.category = null;
        this.updateCache = false;
        this.response2Client = true;
        setCreatedTsIsSet(false);
        this.createdTs = 0L;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof XmPushActionCommand)) {
            return equals((XmPushActionCommand) obj);
        }
        return false;
    }

    public String getAppId() {
        return this.appId;
    }

    public String getCategory() {
        return this.category;
    }

    public List<String> getCmdArgs() {
        return this.cmdArgs;
    }

    public Iterator<String> getCmdArgsIterator() {
        List<String> list = this.cmdArgs;
        if (list == null) {
            return null;
        }
        return list.iterator();
    }

    public int getCmdArgsSize() {
        List<String> list = this.cmdArgs;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public String getCmdName() {
        return this.cmdName;
    }

    public long getCreatedTs() {
        return this.createdTs;
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

    public int hashCode() {
        return 0;
    }

    public boolean isResponse2Client() {
        return this.response2Client;
    }

    public boolean isSetAppId() {
        return this.appId != null;
    }

    public boolean isSetCategory() {
        return this.category != null;
    }

    public boolean isSetCmdArgs() {
        return this.cmdArgs != null;
    }

    public boolean isSetCmdName() {
        return this.cmdName != null;
    }

    public boolean isSetCreatedTs() {
        return this.__isset_bit_vector.get(2);
    }

    public boolean isSetId() {
        return this.id != null;
    }

    public boolean isSetPackageName() {
        return this.packageName != null;
    }

    public boolean isSetResponse2Client() {
        return this.__isset_bit_vector.get(1);
    }

    public boolean isSetTarget() {
        return this.target != null;
    }

    public boolean isSetUpdateCache() {
        return this.__isset_bit_vector.get(0);
    }

    public boolean isUpdateCache() {
        return this.updateCache;
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
                        this.cmdName = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 6:
                    if (b2 == 15) {
                        b bVarJ = dVar.j();
                        this.cmdArgs = new ArrayList(bVarJ.f2153b);
                        for (int i2 = 0; i2 < bVarJ.f2153b; i2++) {
                            this.cmdArgs.add(dVar.p());
                        }
                        dVar.k();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 7:
                    if (b2 == 11) {
                        this.packageName = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 8:
                default:
                    e.a(dVar, b2);
                    break;
                case 9:
                    if (b2 == 11) {
                        this.category = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 10:
                    if (b2 == 2) {
                        this.updateCache = dVar.b();
                        setUpdateCacheIsSet(true);
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 11:
                    if (b2 == 2) {
                        this.response2Client = dVar.b();
                        setResponse2ClientIsSet(true);
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 12:
                    if (b2 == 10) {
                        this.createdTs = dVar.i();
                        setCreatedTsIsSet(true);
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
            }
            dVar.f();
        }
    }

    public XmPushActionCommand setAppId(String str) {
        this.appId = str;
        return this;
    }

    public void setAppIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.appId = null;
    }

    public XmPushActionCommand setCategory(String str) {
        this.category = str;
        return this;
    }

    public void setCategoryIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.category = null;
    }

    public XmPushActionCommand setCmdArgs(List<String> list) {
        this.cmdArgs = list;
        return this;
    }

    public void setCmdArgsIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.cmdArgs = null;
    }

    public XmPushActionCommand setCmdName(String str) {
        this.cmdName = str;
        return this;
    }

    public void setCmdNameIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.cmdName = null;
    }

    public XmPushActionCommand setCreatedTs(long j2) {
        this.createdTs = j2;
        setCreatedTsIsSet(true);
        return this;
    }

    public void setCreatedTsIsSet(boolean z2) {
        this.__isset_bit_vector.set(2, z2);
    }

    public XmPushActionCommand setId(String str) {
        this.id = str;
        return this;
    }

    public void setIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.id = null;
    }

    public XmPushActionCommand setPackageName(String str) {
        this.packageName = str;
        return this;
    }

    public void setPackageNameIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.packageName = null;
    }

    public XmPushActionCommand setResponse2Client(boolean z2) {
        this.response2Client = z2;
        setResponse2ClientIsSet(true);
        return this;
    }

    public void setResponse2ClientIsSet(boolean z2) {
        this.__isset_bit_vector.set(1, z2);
    }

    public XmPushActionCommand setTarget(Target target) {
        this.target = target;
        return this;
    }

    public void setTargetIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.target = null;
    }

    public XmPushActionCommand setUpdateCache(boolean z2) {
        this.updateCache = z2;
        setUpdateCacheIsSet(true);
        return this;
    }

    public void setUpdateCacheIsSet(boolean z2) {
        this.__isset_bit_vector.set(0, z2);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("XmPushActionCommand(");
        if (isSetTarget()) {
            sb.append("target:");
            Target target = this.target;
            if (target == null) {
                sb.append("null");
            } else {
                sb.append(target);
            }
            sb.append(", ");
        }
        sb.append("id:");
        String str = this.id;
        if (str == null) {
            sb.append("null");
        } else {
            sb.append(str);
        }
        sb.append(", ");
        sb.append("appId:");
        String str2 = this.appId;
        if (str2 == null) {
            sb.append("null");
        } else {
            sb.append(str2);
        }
        sb.append(", ");
        sb.append("cmdName:");
        String str3 = this.cmdName;
        if (str3 == null) {
            sb.append("null");
        } else {
            sb.append(str3);
        }
        if (isSetCmdArgs()) {
            sb.append(", ");
            sb.append("cmdArgs:");
            List<String> list = this.cmdArgs;
            if (list == null) {
                sb.append("null");
            } else {
                sb.append(list);
            }
        }
        if (isSetPackageName()) {
            sb.append(", ");
            sb.append("packageName:");
            String str4 = this.packageName;
            if (str4 == null) {
                sb.append("null");
            } else {
                sb.append(str4);
            }
        }
        if (isSetCategory()) {
            sb.append(", ");
            sb.append("category:");
            String str5 = this.category;
            if (str5 == null) {
                sb.append("null");
            } else {
                sb.append(str5);
            }
        }
        if (isSetUpdateCache()) {
            sb.append(", ");
            sb.append("updateCache:");
            sb.append(this.updateCache);
        }
        if (isSetResponse2Client()) {
            sb.append(", ");
            sb.append("response2Client:");
            sb.append(this.response2Client);
        }
        if (isSetCreatedTs()) {
            sb.append(", ");
            sb.append("createdTs:");
            sb.append(this.createdTs);
        }
        sb.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
        return sb.toString();
    }

    public void unsetAppId() {
        this.appId = null;
    }

    public void unsetCategory() {
        this.category = null;
    }

    public void unsetCmdArgs() {
        this.cmdArgs = null;
    }

    public void unsetCmdName() {
        this.cmdName = null;
    }

    public void unsetCreatedTs() {
        this.__isset_bit_vector.clear(2);
    }

    public void unsetId() {
        this.id = null;
    }

    public void unsetPackageName() {
        this.packageName = null;
    }

    public void unsetResponse2Client() {
        this.__isset_bit_vector.clear(1);
    }

    public void unsetTarget() {
        this.target = null;
    }

    public void unsetUpdateCache() {
        this.__isset_bit_vector.clear(0);
    }

    public void validate() throws TException {
        if (this.id == null) {
            throw new TProtocolException("Required field 'id' was not present! Struct: " + toString());
        }
        if (this.appId == null) {
            throw new TProtocolException("Required field 'appId' was not present! Struct: " + toString());
        }
        if (this.cmdName != null) {
            return;
        }
        throw new TProtocolException("Required field 'cmdName' was not present! Struct: " + toString());
    }

    @Override // org.apache.thrift.TBase
    public void write(d dVar) throws TException {
        validate();
        dVar.J(STRUCT_DESC);
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
        if (this.cmdName != null) {
            dVar.w(CMD_NAME_FIELD_DESC);
            dVar.I(this.cmdName);
            dVar.x();
        }
        if (this.cmdArgs != null && isSetCmdArgs()) {
            dVar.w(CMD_ARGS_FIELD_DESC);
            dVar.C(new b((byte) 11, this.cmdArgs.size()));
            Iterator<String> it = this.cmdArgs.iterator();
            while (it.hasNext()) {
                dVar.I(it.next());
            }
            dVar.D();
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
        if (isSetUpdateCache()) {
            dVar.w(UPDATE_CACHE_FIELD_DESC);
            dVar.u(this.updateCache);
            dVar.x();
        }
        if (isSetResponse2Client()) {
            dVar.w(RESPONSE2_CLIENT_FIELD_DESC);
            dVar.u(this.response2Client);
            dVar.x();
        }
        if (isSetCreatedTs()) {
            dVar.w(CREATED_TS_FIELD_DESC);
            dVar.B(this.createdTs);
            dVar.x();
        }
        dVar.y();
        dVar.K();
    }

    @Override // java.lang.Comparable
    public int compareTo(XmPushActionCommand xmPushActionCommand) {
        int iE;
        int iL;
        int iL2;
        int iG;
        int iG2;
        int iH;
        int iG3;
        int iG4;
        int iG5;
        int iF;
        if (!getClass().equals(xmPushActionCommand.getClass())) {
            return getClass().getName().compareTo(xmPushActionCommand.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetTarget()).compareTo(Boolean.valueOf(xmPushActionCommand.isSetTarget()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetTarget() && (iF = g0.a.f(this.target, xmPushActionCommand.target)) != 0) {
            return iF;
        }
        int iCompareTo2 = Boolean.valueOf(isSetId()).compareTo(Boolean.valueOf(xmPushActionCommand.isSetId()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (isSetId() && (iG5 = g0.a.g(this.id, xmPushActionCommand.id)) != 0) {
            return iG5;
        }
        int iCompareTo3 = Boolean.valueOf(isSetAppId()).compareTo(Boolean.valueOf(xmPushActionCommand.isSetAppId()));
        if (iCompareTo3 != 0) {
            return iCompareTo3;
        }
        if (isSetAppId() && (iG4 = g0.a.g(this.appId, xmPushActionCommand.appId)) != 0) {
            return iG4;
        }
        int iCompareTo4 = Boolean.valueOf(isSetCmdName()).compareTo(Boolean.valueOf(xmPushActionCommand.isSetCmdName()));
        if (iCompareTo4 != 0) {
            return iCompareTo4;
        }
        if (isSetCmdName() && (iG3 = g0.a.g(this.cmdName, xmPushActionCommand.cmdName)) != 0) {
            return iG3;
        }
        int iCompareTo5 = Boolean.valueOf(isSetCmdArgs()).compareTo(Boolean.valueOf(xmPushActionCommand.isSetCmdArgs()));
        if (iCompareTo5 != 0) {
            return iCompareTo5;
        }
        if (isSetCmdArgs() && (iH = g0.a.h(this.cmdArgs, xmPushActionCommand.cmdArgs)) != 0) {
            return iH;
        }
        int iCompareTo6 = Boolean.valueOf(isSetPackageName()).compareTo(Boolean.valueOf(xmPushActionCommand.isSetPackageName()));
        if (iCompareTo6 != 0) {
            return iCompareTo6;
        }
        if (isSetPackageName() && (iG2 = g0.a.g(this.packageName, xmPushActionCommand.packageName)) != 0) {
            return iG2;
        }
        int iCompareTo7 = Boolean.valueOf(isSetCategory()).compareTo(Boolean.valueOf(xmPushActionCommand.isSetCategory()));
        if (iCompareTo7 != 0) {
            return iCompareTo7;
        }
        if (isSetCategory() && (iG = g0.a.g(this.category, xmPushActionCommand.category)) != 0) {
            return iG;
        }
        int iCompareTo8 = Boolean.valueOf(isSetUpdateCache()).compareTo(Boolean.valueOf(xmPushActionCommand.isSetUpdateCache()));
        if (iCompareTo8 != 0) {
            return iCompareTo8;
        }
        if (isSetUpdateCache() && (iL2 = g0.a.l(this.updateCache, xmPushActionCommand.updateCache)) != 0) {
            return iL2;
        }
        int iCompareTo9 = Boolean.valueOf(isSetResponse2Client()).compareTo(Boolean.valueOf(xmPushActionCommand.isSetResponse2Client()));
        if (iCompareTo9 != 0) {
            return iCompareTo9;
        }
        if (isSetResponse2Client() && (iL = g0.a.l(this.response2Client, xmPushActionCommand.response2Client)) != 0) {
            return iL;
        }
        int iCompareTo10 = Boolean.valueOf(isSetCreatedTs()).compareTo(Boolean.valueOf(xmPushActionCommand.isSetCreatedTs()));
        if (iCompareTo10 != 0) {
            return iCompareTo10;
        }
        if (!isSetCreatedTs() || (iE = g0.a.e(this.createdTs, xmPushActionCommand.createdTs)) == 0) {
            return 0;
        }
        return iE;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public XmPushActionCommand m32deepCopy() {
        return new XmPushActionCommand(this);
    }

    public boolean equals(XmPushActionCommand xmPushActionCommand) {
        if (xmPushActionCommand == null) {
            return false;
        }
        boolean zIsSetTarget = isSetTarget();
        boolean zIsSetTarget2 = xmPushActionCommand.isSetTarget();
        if ((zIsSetTarget || zIsSetTarget2) && !(zIsSetTarget && zIsSetTarget2 && this.target.equals(xmPushActionCommand.target))) {
            return false;
        }
        boolean zIsSetId = isSetId();
        boolean zIsSetId2 = xmPushActionCommand.isSetId();
        if ((zIsSetId || zIsSetId2) && !(zIsSetId && zIsSetId2 && this.id.equals(xmPushActionCommand.id))) {
            return false;
        }
        boolean zIsSetAppId = isSetAppId();
        boolean zIsSetAppId2 = xmPushActionCommand.isSetAppId();
        if ((zIsSetAppId || zIsSetAppId2) && !(zIsSetAppId && zIsSetAppId2 && this.appId.equals(xmPushActionCommand.appId))) {
            return false;
        }
        boolean zIsSetCmdName = isSetCmdName();
        boolean zIsSetCmdName2 = xmPushActionCommand.isSetCmdName();
        if ((zIsSetCmdName || zIsSetCmdName2) && !(zIsSetCmdName && zIsSetCmdName2 && this.cmdName.equals(xmPushActionCommand.cmdName))) {
            return false;
        }
        boolean zIsSetCmdArgs = isSetCmdArgs();
        boolean zIsSetCmdArgs2 = xmPushActionCommand.isSetCmdArgs();
        if ((zIsSetCmdArgs || zIsSetCmdArgs2) && !(zIsSetCmdArgs && zIsSetCmdArgs2 && this.cmdArgs.equals(xmPushActionCommand.cmdArgs))) {
            return false;
        }
        boolean zIsSetPackageName = isSetPackageName();
        boolean zIsSetPackageName2 = xmPushActionCommand.isSetPackageName();
        if ((zIsSetPackageName || zIsSetPackageName2) && !(zIsSetPackageName && zIsSetPackageName2 && this.packageName.equals(xmPushActionCommand.packageName))) {
            return false;
        }
        boolean zIsSetCategory = isSetCategory();
        boolean zIsSetCategory2 = xmPushActionCommand.isSetCategory();
        if ((zIsSetCategory || zIsSetCategory2) && !(zIsSetCategory && zIsSetCategory2 && this.category.equals(xmPushActionCommand.category))) {
            return false;
        }
        boolean zIsSetUpdateCache = isSetUpdateCache();
        boolean zIsSetUpdateCache2 = xmPushActionCommand.isSetUpdateCache();
        if ((zIsSetUpdateCache || zIsSetUpdateCache2) && !(zIsSetUpdateCache && zIsSetUpdateCache2 && this.updateCache == xmPushActionCommand.updateCache)) {
            return false;
        }
        boolean zIsSetResponse2Client = isSetResponse2Client();
        boolean zIsSetResponse2Client2 = xmPushActionCommand.isSetResponse2Client();
        if ((zIsSetResponse2Client || zIsSetResponse2Client2) && !(zIsSetResponse2Client && zIsSetResponse2Client2 && this.response2Client == xmPushActionCommand.response2Client)) {
            return false;
        }
        boolean zIsSetCreatedTs = isSetCreatedTs();
        boolean zIsSetCreatedTs2 = xmPushActionCommand.isSetCreatedTs();
        if (zIsSetCreatedTs || zIsSetCreatedTs2) {
            return zIsSetCreatedTs && zIsSetCreatedTs2 && this.createdTs == xmPushActionCommand.createdTs;
        }
        return true;
    }

    public XmPushActionCommand(String str, String str2, String str3) {
        this();
        this.id = str;
        this.appId = str2;
        this.cmdName = str3;
    }

    public XmPushActionCommand(XmPushActionCommand xmPushActionCommand) {
        BitSet bitSet = new BitSet(3);
        this.__isset_bit_vector = bitSet;
        bitSet.clear();
        this.__isset_bit_vector.or(xmPushActionCommand.__isset_bit_vector);
        if (xmPushActionCommand.isSetTarget()) {
            this.target = new Target(xmPushActionCommand.target);
        }
        if (xmPushActionCommand.isSetId()) {
            this.id = xmPushActionCommand.id;
        }
        if (xmPushActionCommand.isSetAppId()) {
            this.appId = xmPushActionCommand.appId;
        }
        if (xmPushActionCommand.isSetCmdName()) {
            this.cmdName = xmPushActionCommand.cmdName;
        }
        if (xmPushActionCommand.isSetCmdArgs()) {
            ArrayList arrayList = new ArrayList();
            Iterator<String> it = xmPushActionCommand.cmdArgs.iterator();
            while (it.hasNext()) {
                arrayList.add(it.next());
            }
            this.cmdArgs = arrayList;
        }
        if (xmPushActionCommand.isSetPackageName()) {
            this.packageName = xmPushActionCommand.packageName;
        }
        if (xmPushActionCommand.isSetCategory()) {
            this.category = xmPushActionCommand.category;
        }
        this.updateCache = xmPushActionCommand.updateCache;
        this.response2Client = xmPushActionCommand.response2Client;
        this.createdTs = xmPushActionCommand.createdTs;
    }
}
