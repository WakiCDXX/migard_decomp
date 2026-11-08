package com.xiaomi.xmpush.thrift;

import com.xiaomi.push.mpcd.Constants;
import java.io.Serializable;
import java.util.BitSet;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.protocol.a;
import org.apache.thrift.protocol.d;
import org.apache.thrift.protocol.e;
import org.apache.thrift.protocol.g;

/* JADX INFO: loaded from: classes.dex */
public class Target implements TBase<Target, Object>, Serializable, Cloneable {
    private static final int __CHANNELID_ISSET_ID = 0;
    private static final int __ISPREVIEW_ISSET_ID = 1;
    private BitSet __isset_bit_vector;
    public long channelId;
    public boolean isPreview;
    public String resource;
    public String server;
    public String token;
    public String userId;
    private static final g STRUCT_DESC = new g("Target");
    private static final a CHANNEL_ID_FIELD_DESC = new a("", (byte) 10, 1);
    private static final a USER_ID_FIELD_DESC = new a("", (byte) 11, 2);
    private static final a SERVER_FIELD_DESC = new a("", (byte) 11, 3);
    private static final a RESOURCE_FIELD_DESC = new a("", (byte) 11, 4);
    private static final a IS_PREVIEW_FIELD_DESC = new a("", (byte) 2, 5);
    private static final a TOKEN_FIELD_DESC = new a("", (byte) 11, 7);

    public Target() {
        this.__isset_bit_vector = new BitSet(2);
        this.channelId = 5L;
        this.server = "xiaomi.com";
        this.resource = "";
        this.isPreview = false;
    }

    public void clear() {
        this.channelId = 5L;
        this.userId = null;
        this.server = "xiaomi.com";
        this.resource = "";
        this.isPreview = false;
        this.token = null;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof Target)) {
            return equals((Target) obj);
        }
        return false;
    }

    public long getChannelId() {
        return this.channelId;
    }

    public String getResource() {
        return this.resource;
    }

    public String getServer() {
        return this.server;
    }

    public String getToken() {
        return this.token;
    }

    public String getUserId() {
        return this.userId;
    }

    public int hashCode() {
        return 0;
    }

    public boolean isIsPreview() {
        return this.isPreview;
    }

    public boolean isSetChannelId() {
        return this.__isset_bit_vector.get(0);
    }

    public boolean isSetIsPreview() {
        return this.__isset_bit_vector.get(1);
    }

    public boolean isSetResource() {
        return this.resource != null;
    }

    public boolean isSetServer() {
        return this.server != null;
    }

    public boolean isSetToken() {
        return this.token != null;
    }

    public boolean isSetUserId() {
        return this.userId != null;
    }

    @Override // org.apache.thrift.TBase
    public void read(d dVar) throws TException {
        dVar.q();
        while (true) {
            a aVarE = dVar.e();
            byte b2 = aVarE.f2150b;
            if (b2 == 0) {
                break;
            }
            short s2 = aVarE.f2151c;
            if (s2 != 1) {
                if (s2 != 2) {
                    if (s2 != 3) {
                        if (s2 != 4) {
                            if (s2 != 5) {
                                if (s2 != 7) {
                                    e.a(dVar, b2);
                                } else if (b2 == 11) {
                                    this.token = dVar.p();
                                } else {
                                    e.a(dVar, b2);
                                }
                            } else if (b2 == 2) {
                                this.isPreview = dVar.b();
                                setIsPreviewIsSet(true);
                            } else {
                                e.a(dVar, b2);
                            }
                        } else if (b2 == 11) {
                            this.resource = dVar.p();
                        } else {
                            e.a(dVar, b2);
                        }
                    } else if (b2 == 11) {
                        this.server = dVar.p();
                    } else {
                        e.a(dVar, b2);
                    }
                } else if (b2 == 11) {
                    this.userId = dVar.p();
                } else {
                    e.a(dVar, b2);
                }
            } else if (b2 == 10) {
                this.channelId = dVar.i();
                setChannelIdIsSet(true);
            } else {
                e.a(dVar, b2);
            }
            dVar.f();
        }
        dVar.r();
        if (isSetChannelId()) {
            validate();
            return;
        }
        throw new TProtocolException("Required field 'channelId' was not found in serialized data! Struct: " + toString());
    }

    public Target setChannelId(long j2) {
        this.channelId = j2;
        setChannelIdIsSet(true);
        return this;
    }

    public void setChannelIdIsSet(boolean z2) {
        this.__isset_bit_vector.set(0, z2);
    }

    public Target setIsPreview(boolean z2) {
        this.isPreview = z2;
        setIsPreviewIsSet(true);
        return this;
    }

    public void setIsPreviewIsSet(boolean z2) {
        this.__isset_bit_vector.set(1, z2);
    }

    public Target setResource(String str) {
        this.resource = str;
        return this;
    }

    public void setResourceIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.resource = null;
    }

    public Target setServer(String str) {
        this.server = str;
        return this;
    }

    public void setServerIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.server = null;
    }

    public Target setToken(String str) {
        this.token = str;
        return this;
    }

    public void setTokenIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.token = null;
    }

    public Target setUserId(String str) {
        this.userId = str;
        return this;
    }

    public void setUserIdIsSet(boolean z2) {
        if (z2) {
            return;
        }
        this.userId = null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Target(");
        sb.append("channelId:");
        sb.append(this.channelId);
        sb.append(", ");
        sb.append("userId:");
        String str = this.userId;
        if (str == null) {
            sb.append("null");
        } else {
            sb.append(str);
        }
        if (isSetServer()) {
            sb.append(", ");
            sb.append("server:");
            String str2 = this.server;
            if (str2 == null) {
                sb.append("null");
            } else {
                sb.append(str2);
            }
        }
        if (isSetResource()) {
            sb.append(", ");
            sb.append("resource:");
            String str3 = this.resource;
            if (str3 == null) {
                sb.append("null");
            } else {
                sb.append(str3);
            }
        }
        if (isSetIsPreview()) {
            sb.append(", ");
            sb.append("isPreview:");
            sb.append(this.isPreview);
        }
        if (isSetToken()) {
            sb.append(", ");
            sb.append("token:");
            String str4 = this.token;
            if (str4 == null) {
                sb.append("null");
            } else {
                sb.append(str4);
            }
        }
        sb.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
        return sb.toString();
    }

    public void unsetChannelId() {
        this.__isset_bit_vector.clear(0);
    }

    public void unsetIsPreview() {
        this.__isset_bit_vector.clear(1);
    }

    public void unsetResource() {
        this.resource = null;
    }

    public void unsetServer() {
        this.server = null;
    }

    public void unsetToken() {
        this.token = null;
    }

    public void unsetUserId() {
        this.userId = null;
    }

    public void validate() throws TException {
        if (this.userId != null) {
            return;
        }
        throw new TProtocolException("Required field 'userId' was not present! Struct: " + toString());
    }

    @Override // org.apache.thrift.TBase
    public void write(d dVar) throws TException {
        validate();
        dVar.J(STRUCT_DESC);
        dVar.w(CHANNEL_ID_FIELD_DESC);
        dVar.B(this.channelId);
        dVar.x();
        if (this.userId != null) {
            dVar.w(USER_ID_FIELD_DESC);
            dVar.I(this.userId);
            dVar.x();
        }
        if (this.server != null && isSetServer()) {
            dVar.w(SERVER_FIELD_DESC);
            dVar.I(this.server);
            dVar.x();
        }
        if (this.resource != null && isSetResource()) {
            dVar.w(RESOURCE_FIELD_DESC);
            dVar.I(this.resource);
            dVar.x();
        }
        if (isSetIsPreview()) {
            dVar.w(IS_PREVIEW_FIELD_DESC);
            dVar.u(this.isPreview);
            dVar.x();
        }
        if (this.token != null && isSetToken()) {
            dVar.w(TOKEN_FIELD_DESC);
            dVar.I(this.token);
            dVar.x();
        }
        dVar.y();
        dVar.K();
    }

    @Override // java.lang.Comparable
    public int compareTo(Target target) {
        int iG;
        int iL;
        int iG2;
        int iG3;
        int iG4;
        int iE;
        if (!getClass().equals(target.getClass())) {
            return getClass().getName().compareTo(target.getClass().getName());
        }
        int iCompareTo = Boolean.valueOf(isSetChannelId()).compareTo(Boolean.valueOf(target.isSetChannelId()));
        if (iCompareTo != 0) {
            return iCompareTo;
        }
        if (isSetChannelId() && (iE = g0.a.e(this.channelId, target.channelId)) != 0) {
            return iE;
        }
        int iCompareTo2 = Boolean.valueOf(isSetUserId()).compareTo(Boolean.valueOf(target.isSetUserId()));
        if (iCompareTo2 != 0) {
            return iCompareTo2;
        }
        if (isSetUserId() && (iG4 = g0.a.g(this.userId, target.userId)) != 0) {
            return iG4;
        }
        int iCompareTo3 = Boolean.valueOf(isSetServer()).compareTo(Boolean.valueOf(target.isSetServer()));
        if (iCompareTo3 != 0) {
            return iCompareTo3;
        }
        if (isSetServer() && (iG3 = g0.a.g(this.server, target.server)) != 0) {
            return iG3;
        }
        int iCompareTo4 = Boolean.valueOf(isSetResource()).compareTo(Boolean.valueOf(target.isSetResource()));
        if (iCompareTo4 != 0) {
            return iCompareTo4;
        }
        if (isSetResource() && (iG2 = g0.a.g(this.resource, target.resource)) != 0) {
            return iG2;
        }
        int iCompareTo5 = Boolean.valueOf(isSetIsPreview()).compareTo(Boolean.valueOf(target.isSetIsPreview()));
        if (iCompareTo5 != 0) {
            return iCompareTo5;
        }
        if (isSetIsPreview() && (iL = g0.a.l(this.isPreview, target.isPreview)) != 0) {
            return iL;
        }
        int iCompareTo6 = Boolean.valueOf(isSetToken()).compareTo(Boolean.valueOf(target.isSetToken()));
        if (iCompareTo6 != 0) {
            return iCompareTo6;
        }
        if (!isSetToken() || (iG = g0.a.g(this.token, target.token)) == 0) {
            return 0;
        }
        return iG;
    }

    /* JADX INFO: renamed from: deepCopy, reason: merged with bridge method [inline-methods] */
    public Target m26deepCopy() {
        return new Target(this);
    }

    public boolean equals(Target target) {
        if (target == null || this.channelId != target.channelId) {
            return false;
        }
        boolean zIsSetUserId = isSetUserId();
        boolean zIsSetUserId2 = target.isSetUserId();
        if ((zIsSetUserId || zIsSetUserId2) && !(zIsSetUserId && zIsSetUserId2 && this.userId.equals(target.userId))) {
            return false;
        }
        boolean zIsSetServer = isSetServer();
        boolean zIsSetServer2 = target.isSetServer();
        if ((zIsSetServer || zIsSetServer2) && !(zIsSetServer && zIsSetServer2 && this.server.equals(target.server))) {
            return false;
        }
        boolean zIsSetResource = isSetResource();
        boolean zIsSetResource2 = target.isSetResource();
        if ((zIsSetResource || zIsSetResource2) && !(zIsSetResource && zIsSetResource2 && this.resource.equals(target.resource))) {
            return false;
        }
        boolean zIsSetIsPreview = isSetIsPreview();
        boolean zIsSetIsPreview2 = target.isSetIsPreview();
        if ((zIsSetIsPreview || zIsSetIsPreview2) && !(zIsSetIsPreview && zIsSetIsPreview2 && this.isPreview == target.isPreview)) {
            return false;
        }
        boolean zIsSetToken = isSetToken();
        boolean zIsSetToken2 = target.isSetToken();
        if (zIsSetToken || zIsSetToken2) {
            return zIsSetToken && zIsSetToken2 && this.token.equals(target.token);
        }
        return true;
    }

    public Target(long j2, String str) {
        this();
        this.channelId = j2;
        setChannelIdIsSet(true);
        this.userId = str;
    }

    public Target(Target target) {
        BitSet bitSet = new BitSet(2);
        this.__isset_bit_vector = bitSet;
        bitSet.clear();
        this.__isset_bit_vector.or(target.__isset_bit_vector);
        this.channelId = target.channelId;
        if (target.isSetUserId()) {
            this.userId = target.userId;
        }
        if (target.isSetServer()) {
            this.server = target.server;
        }
        if (target.isSetResource()) {
            this.resource = target.resource;
        }
        this.isPreview = target.isPreview;
        if (target.isSetToken()) {
            this.token = target.token;
        }
    }
}
