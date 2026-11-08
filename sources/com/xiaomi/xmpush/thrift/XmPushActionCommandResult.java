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
public class XmPushActionCommandResult implements TBase<XmPushActionCommandResult, Object>, Serializable, Cloneable {
    private static final int __ERRORCODE_ISSET_ID = 0;
    private static final int __RESPONSE2CLIENT_ISSET_ID = 1;
    private BitSet __isset_bit_vector;
    public String appId;
    public String category;
    public List<String> cmdArgs;
    public String cmdName;
    public long errorCode;
    public String id;
    public String packageName;
    public String reason;
    public boolean response2Client;
    public Target target;
    private static final g STRUCT_DESC = new g("XmPushActionCommandResult");
    private static final a TARGET_FIELD_DESC = new a("", (byte) 12, 2);
    private static final a ID_FIELD_DESC = new a("", (byte) 11, 3);
    private static final a APP_ID_FIELD_DESC = new a("", (byte) 11, 4);
    private static final a CMD_NAME_FIELD_DESC = new a("", (byte) 11, 5);
    private static final a ERROR_CODE_FIELD_DESC = new a("", (byte) 10, 7);
    private static final a REASON_FIELD_DESC = new a("", (byte) 11, 8);
    private static final a PACKAGE_NAME_FIELD_DESC = new a("", (byte) 11, 9);
    private static final a CMD_ARGS_FIELD_DESC = new a("", (byte) 15, 10);
    private static final a CATEGORY_FIELD_DESC = new a("", (byte) 11, 12);
    private static final a RESPONSE2_CLIENT_FIELD_DESC = new a("", (byte) 2, 13);

    public XmPushActionCommandResult() {
        this.__isset_bit_vector = new BitSet(2);
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
        setErrorCodeIsSet(false);
        this.errorCode = 0L;
        this.reason = null;
        this.packageName = null;
        this.cmdArgs = null;
        this.category = null;
        this.response2Client = true;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof XmPushActionCommandResult)) {
            return equals((XmPushActionCommandResult) obj);
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

    public long getErrorCode() {
        return this.errorCode;
    }

    public String getId() {
        return this.id;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getReason() {
        return this.reason;
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

    public boolean isSetErrorCode() {
        return this.__isset_bit_vector.get(0);
    }

    public boolean isSetId() {
        return this.id != null;
    }

    public boolean isSetPackageName() {
        return this.packageName != null;
    }

    public boolean isSetReason() {
        return this.reason != null;
    }

    public boolean isSetResponse2Client() {
        return this.__isset_bit_vector.get(1);
    }

    public boolean isSetTarget() {
        return this.target != null;
    }

    @Override // org.apache.thrift.TBase
    public void read(d dVar) throws TException {
        dVar.q();
        while (true) {
            a aVarE = dVar.e();
            byte b2 = aVarE.f2150b;
            if (b2 == 0) {
                dVar.r();
                if (isSetErrorCode()) {
                    validate();
                    return;
                }
                throw new TProtocolException("Required field 'errorCode' was not found in serialized data! Struct: " + toString());
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
                case 11:
                default:
                    e.a(dVar, b2);
                    break;
                case 7:
                    if (b2 == 10) {
                        this.errorCode = dVar.i();
                        setErrorCodeIsSet(true);
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 8:
                    if (b2 == 11) {
                        this.reason = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 9:
                    if (b2 == 11) {
                        this.packageName = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 10:
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
                case 12:
                    if (b2 == 11) {
                        this.category = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
                case 13:
                    if (b2 == 2) {
                        this.response2Client = dVar.b();
                        setResponse2ClientIsSet(true);
                    } else {
                        e.a(dVar, b2);
                    }
                    break;
            }
            dVar.f();
        }
    }

    public XmPushActionCommandResult setAppId(String str) {
        this.appId = str;
        return this;
    }

    public void setAppIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.appId = null;
    }

    public XmPushActionCommandResult setCategory(String str) {
        this.category = str;
        return this;
    }

    public void setCategoryIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.category = null;
    }

    public XmPushActionCommandResult setCmdArgs(List<String> list) {
        this.cmdArgs = list;
        return this;
    }

    public void setCmdArgsIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.cmdArgs = null;
    }

    public XmPushActionCommandResult setCmdName(String str) {
        this.cmdName = str;
        return this;
    }

    public void setCmdNameIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.cmdName = null;
    }

    public XmPushActionCommandResult setErrorCode(long j2) {
        this.errorCode = j2;
        setErrorCodeIsSet(true);
        return this;
    }

    public void setErrorCodeIsSet(boolean z2) {
        this.__isset_bit_vector.set(0, z2);
    }

    public XmPushActionCommandResult setId(String str) {
        this.id = str;
        return this;
    }

    public void setIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.id = null;
    }

    public XmPushActionCommandResult setPackageName(String str) {
        this.packageName = str;
        return this;
    }

    public void setPackageNameIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.packageName = null;
    }

    public XmPushActionCommandResult setReason(String str) {
        this.reason = str;
        return this;
    }

    public void setReasonIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.reason = null;
    }

    public XmPushActionCommandResult setResponse2Client(boolean z2) {
        this.response2Client = z2;
        setResponse2ClientIsSet(true);
        return this;
    }

    public void setResponse2ClientIsSet(boolean z2) {
        this.__isset_bit_vector.set(1, z2);
    }

    public XmPushActionCommandResult setTarget(Target target) {
        this.target = target;
        return this;
    }

    public void setTargetIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.target = null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("XmPushActionCommandResult(");
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
        sb.append(", ");
        sb.append("errorCode:");
        sb.append(this.errorCode);
        if (isSetReason()) {
            sb.append(", ");
            sb.append("reason:");
            String str4 = this.reason;
            if (str4 == null) {
                sb.append("null");
            } else {
                sb.append(str4);
            }
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
        if (isSetResponse2Client()) {
            sb.append(", ");
            sb.append("response2Client:");
            sb.append(this.response2Client);
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

    public void unsetErrorCode() {
        this.__isset_bit_vector.clear(0);
    }

    public void unsetId() {
        this.id = null;
    }

    public void unsetPackageName() {
        this.packageName = null;
    }

    public void unsetReason() {
        this.reason = null;
    }

    public void unsetResponse2Client() {
        this.__isset_bit_vector.clear(1);
    }

    public void unsetTarget() {
        this.target = null;
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
        dVar.w(ERROR_CODE_FIELD_DESC);
        dVar.B(this.errorCode);
        dVar.x();
        if (this.reason != null && isSetReason()) {
            dVar.w(REASON_FIELD_DESC);
            dVar.I(this.reason);
            dVar.x();
        }
        if (this.packageName != null && isSetPackageName()) {
            dVar.w(PACKAGE_NAME_FIELD_DESC);
            dVar.I(this.packageName);
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
        if (this.category != null && isSetCategory()) {
            dVar.w(CATEGORY_FIELD_DESC);
            dVar.I(this.category);
            dVar.x();
        }
        if (isSetResponse2Client()) {
            dVar.w(RESPONSE2_CLIENT_FIELD_DESC);
            dVar.u(this.response2Client);
            dVar.x();
        }
        dVar.y();
        dVar.K();
    }

    @Override // java.lang.Comparable
    public int compareTo(XmPushActionCommandResult xmPushActionCommandResult) {
        int iL;
        int iG;
        int iH;
        int iG2;
        int iG3;
        int iE;
        int iG4;
        int iG5;
        int iG6;
        int iF;
        if (!getClass().equals(xmPushActionCommandResult.getClass())) {
            return getClass().getName().compareTo(xmPushActionCommandResult.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetTarget()).compareTo(Boolean.valueOf(xmPushActionCommandResult.isSetTarget()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetTarget() && (iF = g0.a.f(this.target, xmPushActionCommandResult.target)) != 0) {
            return iF;
        }
        int iCompareTo2 = Boolean.valueOf(isSetId()).compareTo(Boolean.valueOf(xmPushActionCommandResult.isSetId()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (isSetId() && (iG6 = g0.a.g(this.id, xmPushActionCommandResult.id)) != 0) {
            return iG6;
        }
        int iCompareTo3 = Boolean.valueOf(isSetAppId()).compareTo(Boolean.valueOf(xmPushActionCommandResult.isSetAppId()));
        if (iCompareTo3 != 0) {
            return iCompareTo3;
        }
        if (isSetAppId() && (iG5 = g0.a.g(this.appId, xmPushActionCommandResult.appId)) != 0) {
            return iG5;
        }
        int iCompareTo4 = Boolean.valueOf(isSetCmdName()).compareTo(Boolean.valueOf(xmPushActionCommandResult.isSetCmdName()));
        if (iCompareTo4 != 0) {
            return iCompareTo4;
        }
        if (isSetCmdName() && (iG4 = g0.a.g(this.cmdName, xmPushActionCommandResult.cmdName)) != 0) {
            return iG4;
        }
        int iCompareTo5 = Boolean.valueOf(isSetErrorCode()).compareTo(Boolean.valueOf(xmPushActionCommandResult.isSetErrorCode()));
        if (iCompareTo5 != 0) {
            return iCompareTo5;
        }
        if (isSetErrorCode() && (iE = g0.a.e(this.errorCode, xmPushActionCommandResult.errorCode)) != 0) {
            return iE;
        }
        int iCompareTo6 = Boolean.valueOf(isSetReason()).compareTo(Boolean.valueOf(xmPushActionCommandResult.isSetReason()));
        if (iCompareTo6 != 0) {
            return iCompareTo6;
        }
        if (isSetReason() && (iG3 = g0.a.g(this.reason, xmPushActionCommandResult.reason)) != 0) {
            return iG3;
        }
        int iCompareTo7 = Boolean.valueOf(isSetPackageName()).compareTo(Boolean.valueOf(xmPushActionCommandResult.isSetPackageName()));
        if (iCompareTo7 != 0) {
            return iCompareTo7;
        }
        if (isSetPackageName() && (iG2 = g0.a.g(this.packageName, xmPushActionCommandResult.packageName)) != 0) {
            return iG2;
        }
        int iCompareTo8 = Boolean.valueOf(isSetCmdArgs()).compareTo(Boolean.valueOf(xmPushActionCommandResult.isSetCmdArgs()));
        if (iCompareTo8 != 0) {
            return iCompareTo8;
        }
        if (isSetCmdArgs() && (iH = g0.a.h(this.cmdArgs, xmPushActionCommandResult.cmdArgs)) != 0) {
            return iH;
        }
        int iCompareTo9 = Boolean.valueOf(isSetCategory()).compareTo(Boolean.valueOf(xmPushActionCommandResult.isSetCategory()));
        if (iCompareTo9 != 0) {
            return iCompareTo9;
        }
        if (isSetCategory() && (iG = g0.a.g(this.category, xmPushActionCommandResult.category)) != 0) {
            return iG;
        }
        int iCompareTo10 = Boolean.valueOf(isSetResponse2Client()).compareTo(Boolean.valueOf(xmPushActionCommandResult.isSetResponse2Client()));
        if (iCompareTo10 != 0) {
            return iCompareTo10;
        }
        if (!isSetResponse2Client() || (iL = g0.a.l(this.response2Client, xmPushActionCommandResult.response2Client)) == 0) {
            return 0;
        }
        return iL;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public XmPushActionCommandResult m33deepCopy() {
        return new XmPushActionCommandResult(this);
    }

    public boolean equals(XmPushActionCommandResult xmPushActionCommandResult) {
        if (xmPushActionCommandResult == null) {
            return false;
        }
        boolean zIsSetTarget = isSetTarget();
        boolean zIsSetTarget2 = xmPushActionCommandResult.isSetTarget();
        if ((zIsSetTarget || zIsSetTarget2) && !(zIsSetTarget && zIsSetTarget2 && this.target.equals(xmPushActionCommandResult.target))) {
            return false;
        }
        boolean zIsSetId = isSetId();
        boolean zIsSetId2 = xmPushActionCommandResult.isSetId();
        if ((zIsSetId || zIsSetId2) && !(zIsSetId && zIsSetId2 && this.id.equals(xmPushActionCommandResult.id))) {
            return false;
        }
        boolean zIsSetAppId = isSetAppId();
        boolean zIsSetAppId2 = xmPushActionCommandResult.isSetAppId();
        if ((zIsSetAppId || zIsSetAppId2) && !(zIsSetAppId && zIsSetAppId2 && this.appId.equals(xmPushActionCommandResult.appId))) {
            return false;
        }
        boolean zIsSetCmdName = isSetCmdName();
        boolean zIsSetCmdName2 = xmPushActionCommandResult.isSetCmdName();
        if (((zIsSetCmdName || zIsSetCmdName2) && !(zIsSetCmdName && zIsSetCmdName2 && this.cmdName.equals(xmPushActionCommandResult.cmdName))) || this.errorCode != xmPushActionCommandResult.errorCode) {
            return false;
        }
        boolean zIsSetReason = isSetReason();
        boolean zIsSetReason2 = xmPushActionCommandResult.isSetReason();
        if ((zIsSetReason || zIsSetReason2) && !(zIsSetReason && zIsSetReason2 && this.reason.equals(xmPushActionCommandResult.reason))) {
            return false;
        }
        boolean zIsSetPackageName = isSetPackageName();
        boolean zIsSetPackageName2 = xmPushActionCommandResult.isSetPackageName();
        if ((zIsSetPackageName || zIsSetPackageName2) && !(zIsSetPackageName && zIsSetPackageName2 && this.packageName.equals(xmPushActionCommandResult.packageName))) {
            return false;
        }
        boolean zIsSetCmdArgs = isSetCmdArgs();
        boolean zIsSetCmdArgs2 = xmPushActionCommandResult.isSetCmdArgs();
        if ((zIsSetCmdArgs || zIsSetCmdArgs2) && !(zIsSetCmdArgs && zIsSetCmdArgs2 && this.cmdArgs.equals(xmPushActionCommandResult.cmdArgs))) {
            return false;
        }
        boolean zIsSetCategory = isSetCategory();
        boolean zIsSetCategory2 = xmPushActionCommandResult.isSetCategory();
        if ((zIsSetCategory || zIsSetCategory2) && !(zIsSetCategory && zIsSetCategory2 && this.category.equals(xmPushActionCommandResult.category))) {
            return false;
        }
        boolean zIsSetResponse2Client = isSetResponse2Client();
        boolean zIsSetResponse2Client2 = xmPushActionCommandResult.isSetResponse2Client();
        if (zIsSetResponse2Client || zIsSetResponse2Client2) {
            return zIsSetResponse2Client && zIsSetResponse2Client2 && this.response2Client == xmPushActionCommandResult.response2Client;
        }
        return true;
    }

    public XmPushActionCommandResult(String str, String str2, String str3, long j2) {
        this();
        this.id = str;
        this.appId = str2;
        this.cmdName = str3;
        this.errorCode = j2;
        setErrorCodeIsSet(true);
    }

    public XmPushActionCommandResult(XmPushActionCommandResult xmPushActionCommandResult) {
        BitSet bitSet = new BitSet(2);
        this.__isset_bit_vector = bitSet;
        bitSet.clear();
        this.__isset_bit_vector.or(xmPushActionCommandResult.__isset_bit_vector);
        if (xmPushActionCommandResult.isSetTarget()) {
            this.target = new Target(xmPushActionCommandResult.target);
        }
        if (xmPushActionCommandResult.isSetId()) {
            this.id = xmPushActionCommandResult.id;
        }
        if (xmPushActionCommandResult.isSetAppId()) {
            this.appId = xmPushActionCommandResult.appId;
        }
        if (xmPushActionCommandResult.isSetCmdName()) {
            this.cmdName = xmPushActionCommandResult.cmdName;
        }
        this.errorCode = xmPushActionCommandResult.errorCode;
        if (xmPushActionCommandResult.isSetReason()) {
            this.reason = xmPushActionCommandResult.reason;
        }
        if (xmPushActionCommandResult.isSetPackageName()) {
            this.packageName = xmPushActionCommandResult.packageName;
        }
        if (xmPushActionCommandResult.isSetCmdArgs()) {
            ArrayList arrayList = new ArrayList();
            Iterator<String> it = xmPushActionCommandResult.cmdArgs.iterator();
            while (it.hasNext()) {
                arrayList.add(it.next());
            }
            this.cmdArgs = arrayList;
        }
        if (xmPushActionCommandResult.isSetCategory()) {
            this.category = xmPushActionCommandResult.category;
        }
        this.response2Client = xmPushActionCommandResult.response2Client;
    }
}
