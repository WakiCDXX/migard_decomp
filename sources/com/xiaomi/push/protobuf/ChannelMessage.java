package com.xiaomi.push.protobuf;

import com.google.protobuf.micro.ByteStringMicro;
import com.google.protobuf.micro.CodedInputStreamMicro;
import com.google.protobuf.micro.CodedOutputStreamMicro;
import com.google.protobuf.micro.InvalidProtocolBufferMicroException;
import com.google.protobuf.micro.MessageMicro;
import com.miui.powerkeeper.controller.FrozenAppController;
import com.xiaomi.channel.commonutils.network.Network;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public final class ChannelMessage {

    public static final class ClientHeader extends MessageMicro {
        public static final int CHID_FIELD_NUMBER = 1;
        public static final int CIPHER_FIELD_NUMBER = 9;
        public static final int CMD_FIELD_NUMBER = 5;
        public static final int CS_ONEWAY = 1;
        public static final int CS_REQ = 2;
        public static final int CS_RESP = 3;
        public static final int DELAY_FIELD_NUMBER = 13;
        public static final int DIR_FLAG_FIELD_NUMBER = 8;
        public static final int ERR_CODE_FIELD_NUMBER = 10;
        public static final int ERR_STR_FIELD_NUMBER = 11;
        public static final int ID_FIELD_NUMBER = 7;
        public static final int RESOURCE_FIELD_NUMBER = 4;
        public static final int SC_ONEWAY = 4;
        public static final int SC_REQ = 5;
        public static final int SC_RESP = 6;
        public static final int SERVER_FIELD_NUMBER = 3;
        public static final int SUBCMD_FIELD_NUMBER = 6;
        public static final int TIMESTAMP_FIELD_NUMBER = 12;
        public static final int UUID_FIELD_NUMBER = 2;
        private boolean hasChid;
        private boolean hasCipher;
        private boolean hasCmd;
        private boolean hasDelay;
        private boolean hasDirFlag;
        private boolean hasErrCode;
        private boolean hasErrStr;
        private boolean hasId;
        private boolean hasResource;
        private boolean hasServer;
        private boolean hasSubcmd;
        private boolean hasTimestamp;
        private boolean hasUuid;
        private int chid_ = 0;
        private long uuid_ = 0;
        private String server_ = "";
        private String resource_ = "";
        private String cmd_ = "";
        private String subcmd_ = "";
        private String id_ = "";
        private int dirFlag_ = 1;
        private int cipher_ = 0;
        private int errCode_ = 0;
        private String errStr_ = "";
        private long timestamp_ = 0;
        private long delay_ = 0;
        private int cachedSize = -1;

        public static ClientHeader parseFrom(byte[] bArr) throws InvalidProtocolBufferMicroException {
            return (ClientHeader) new ClientHeader().mergeFrom(bArr);
        }

        public final ClientHeader clear() {
            clearChid();
            clearUuid();
            clearServer();
            clearResource();
            clearCmd();
            clearSubcmd();
            clearId();
            clearDirFlag();
            clearCipher();
            clearErrCode();
            clearErrStr();
            clearTimestamp();
            clearDelay();
            this.cachedSize = -1;
            return this;
        }

        public ClientHeader clearChid() {
            this.hasChid = false;
            this.chid_ = 0;
            return this;
        }

        public ClientHeader clearCipher() {
            this.hasCipher = false;
            this.cipher_ = 0;
            return this;
        }

        public ClientHeader clearCmd() {
            this.hasCmd = false;
            this.cmd_ = "";
            return this;
        }

        public ClientHeader clearDelay() {
            this.hasDelay = false;
            this.delay_ = 0L;
            return this;
        }

        public ClientHeader clearDirFlag() {
            this.hasDirFlag = false;
            this.dirFlag_ = 1;
            return this;
        }

        public ClientHeader clearErrCode() {
            this.hasErrCode = false;
            this.errCode_ = 0;
            return this;
        }

        public ClientHeader clearErrStr() {
            this.hasErrStr = false;
            this.errStr_ = "";
            return this;
        }

        public ClientHeader clearId() {
            this.hasId = false;
            this.id_ = "";
            return this;
        }

        public ClientHeader clearResource() {
            this.hasResource = false;
            this.resource_ = "";
            return this;
        }

        public ClientHeader clearServer() {
            this.hasServer = false;
            this.server_ = "";
            return this;
        }

        public ClientHeader clearSubcmd() {
            this.hasSubcmd = false;
            this.subcmd_ = "";
            return this;
        }

        public ClientHeader clearTimestamp() {
            this.hasTimestamp = false;
            this.timestamp_ = 0L;
            return this;
        }

        public ClientHeader clearUuid() {
            this.hasUuid = false;
            this.uuid_ = 0L;
            return this;
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public int getCachedSize() {
            if (this.cachedSize < 0) {
                getSerializedSize();
            }
            return this.cachedSize;
        }

        public int getChid() {
            return this.chid_;
        }

        public int getCipher() {
            return this.cipher_;
        }

        public String getCmd() {
            return this.cmd_;
        }

        public long getDelay() {
            return this.delay_;
        }

        public int getDirFlag() {
            return this.dirFlag_;
        }

        public int getErrCode() {
            return this.errCode_;
        }

        public String getErrStr() {
            return this.errStr_;
        }

        public String getId() {
            return this.id_;
        }

        public String getResource() {
            return this.resource_;
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public int getSerializedSize() {
            int iComputeInt32Size = hasChid() ? CodedOutputStreamMicro.computeInt32Size(1, getChid()) : 0;
            if (hasUuid()) {
                iComputeInt32Size += CodedOutputStreamMicro.computeInt64Size(2, getUuid());
            }
            if (hasServer()) {
                iComputeInt32Size += CodedOutputStreamMicro.computeStringSize(3, getServer());
            }
            if (hasResource()) {
                iComputeInt32Size += CodedOutputStreamMicro.computeStringSize(4, getResource());
            }
            if (hasCmd()) {
                iComputeInt32Size += CodedOutputStreamMicro.computeStringSize(5, getCmd());
            }
            if (hasSubcmd()) {
                iComputeInt32Size += CodedOutputStreamMicro.computeStringSize(6, getSubcmd());
            }
            if (hasId()) {
                iComputeInt32Size += CodedOutputStreamMicro.computeStringSize(7, getId());
            }
            if (hasDirFlag()) {
                iComputeInt32Size += CodedOutputStreamMicro.computeInt32Size(8, getDirFlag());
            }
            if (hasCipher()) {
                iComputeInt32Size += CodedOutputStreamMicro.computeInt32Size(9, getCipher());
            }
            if (hasErrCode()) {
                iComputeInt32Size += CodedOutputStreamMicro.computeInt32Size(10, getErrCode());
            }
            if (hasErrStr()) {
                iComputeInt32Size += CodedOutputStreamMicro.computeStringSize(11, getErrStr());
            }
            if (hasTimestamp()) {
                iComputeInt32Size += CodedOutputStreamMicro.computeInt64Size(12, getTimestamp());
            }
            if (hasDelay()) {
                iComputeInt32Size += CodedOutputStreamMicro.computeInt64Size(13, getDelay());
            }
            this.cachedSize = iComputeInt32Size;
            return iComputeInt32Size;
        }

        public String getServer() {
            return this.server_;
        }

        public String getSubcmd() {
            return this.subcmd_;
        }

        public long getTimestamp() {
            return this.timestamp_;
        }

        public long getUuid() {
            return this.uuid_;
        }

        public boolean hasChid() {
            return this.hasChid;
        }

        public boolean hasCipher() {
            return this.hasCipher;
        }

        public boolean hasCmd() {
            return this.hasCmd;
        }

        public boolean hasDelay() {
            return this.hasDelay;
        }

        public boolean hasDirFlag() {
            return this.hasDirFlag;
        }

        public boolean hasErrCode() {
            return this.hasErrCode;
        }

        public boolean hasErrStr() {
            return this.hasErrStr;
        }

        public boolean hasId() {
            return this.hasId;
        }

        public boolean hasResource() {
            return this.hasResource;
        }

        public boolean hasServer() {
            return this.hasServer;
        }

        public boolean hasSubcmd() {
            return this.hasSubcmd;
        }

        public boolean hasTimestamp() {
            return this.hasTimestamp;
        }

        public boolean hasUuid() {
            return this.hasUuid;
        }

        public final boolean isInitialized() {
            return true;
        }

        public ClientHeader setChid(int i2) {
            this.hasChid = true;
            this.chid_ = i2;
            return this;
        }

        public ClientHeader setCipher(int i2) {
            this.hasCipher = true;
            this.cipher_ = i2;
            return this;
        }

        public ClientHeader setCmd(String str) {
            this.hasCmd = true;
            this.cmd_ = str;
            return this;
        }

        public ClientHeader setDelay(long j2) {
            this.hasDelay = true;
            this.delay_ = j2;
            return this;
        }

        public ClientHeader setDirFlag(int i2) {
            this.hasDirFlag = true;
            this.dirFlag_ = i2;
            return this;
        }

        public ClientHeader setErrCode(int i2) {
            this.hasErrCode = true;
            this.errCode_ = i2;
            return this;
        }

        public ClientHeader setErrStr(String str) {
            this.hasErrStr = true;
            this.errStr_ = str;
            return this;
        }

        public ClientHeader setId(String str) {
            this.hasId = true;
            this.id_ = str;
            return this;
        }

        public ClientHeader setResource(String str) {
            this.hasResource = true;
            this.resource_ = str;
            return this;
        }

        public ClientHeader setServer(String str) {
            this.hasServer = true;
            this.server_ = str;
            return this;
        }

        public ClientHeader setSubcmd(String str) {
            this.hasSubcmd = true;
            this.subcmd_ = str;
            return this;
        }

        public ClientHeader setTimestamp(long j2) {
            this.hasTimestamp = true;
            this.timestamp_ = j2;
            return this;
        }

        public ClientHeader setUuid(long j2) {
            this.hasUuid = true;
            this.uuid_ = j2;
            return this;
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public void writeTo(CodedOutputStreamMicro codedOutputStreamMicro) throws IOException {
            if (hasChid()) {
                codedOutputStreamMicro.writeInt32(1, getChid());
            }
            if (hasUuid()) {
                codedOutputStreamMicro.writeInt64(2, getUuid());
            }
            if (hasServer()) {
                codedOutputStreamMicro.writeString(3, getServer());
            }
            if (hasResource()) {
                codedOutputStreamMicro.writeString(4, getResource());
            }
            if (hasCmd()) {
                codedOutputStreamMicro.writeString(5, getCmd());
            }
            if (hasSubcmd()) {
                codedOutputStreamMicro.writeString(6, getSubcmd());
            }
            if (hasId()) {
                codedOutputStreamMicro.writeString(7, getId());
            }
            if (hasDirFlag()) {
                codedOutputStreamMicro.writeInt32(8, getDirFlag());
            }
            if (hasCipher()) {
                codedOutputStreamMicro.writeInt32(9, getCipher());
            }
            if (hasErrCode()) {
                codedOutputStreamMicro.writeInt32(10, getErrCode());
            }
            if (hasErrStr()) {
                codedOutputStreamMicro.writeString(11, getErrStr());
            }
            if (hasTimestamp()) {
                codedOutputStreamMicro.writeInt64(12, getTimestamp());
            }
            if (hasDelay()) {
                codedOutputStreamMicro.writeInt64(13, getDelay());
            }
        }

        public static ClientHeader parseFrom(CodedInputStreamMicro codedInputStreamMicro) throws IOException {
            return new ClientHeader().mergeFrom(codedInputStreamMicro);
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public ClientHeader mergeFrom(CodedInputStreamMicro codedInputStreamMicro) throws IOException {
            while (true) {
                int tag = codedInputStreamMicro.readTag();
                switch (tag) {
                    case 0:
                        return this;
                    case 8:
                        setChid(codedInputStreamMicro.readInt32());
                        break;
                    case 16:
                        setUuid(codedInputStreamMicro.readInt64());
                        break;
                    case 26:
                        setServer(codedInputStreamMicro.readString());
                        break;
                    case 34:
                        setResource(codedInputStreamMicro.readString());
                        break;
                    case 42:
                        setCmd(codedInputStreamMicro.readString());
                        break;
                    case 50:
                        setSubcmd(codedInputStreamMicro.readString());
                        break;
                    case 58:
                        setId(codedInputStreamMicro.readString());
                        break;
                    case 64:
                        setDirFlag(codedInputStreamMicro.readInt32());
                        break;
                    case 72:
                        setCipher(codedInputStreamMicro.readInt32());
                        break;
                    case Network.CMWAP_PORT /* 80 */:
                        setErrCode(codedInputStreamMicro.readInt32());
                        break;
                    case 90:
                        setErrStr(codedInputStreamMicro.readString());
                        break;
                    case 96:
                        setTimestamp(codedInputStreamMicro.readInt64());
                        break;
                    case 104:
                        setDelay(codedInputStreamMicro.readInt64());
                        break;
                    default:
                        if (!parseUnknownField(codedInputStreamMicro, tag)) {
                            return this;
                        }
                        break;
                        break;
                }
            }
        }
    }

    public static final class PushServiceConfigMsg extends MessageMicro {
        public static final int CLIENTVERSION_FIELD_NUMBER = 3;
        public static final int CLOUDVERSION_FIELD_NUMBER = 4;
        public static final int DOTS_FIELD_NUMBER = 5;
        public static final int FETCHBUCKET_FIELD_NUMBER = 1;
        private boolean hasClientVersion;
        private boolean hasCloudVersion;
        private boolean hasDots;
        private boolean hasFetchBucket;
        private boolean fetchBucket_ = false;
        private int clientVersion_ = 0;
        private int cloudVersion_ = 0;
        private int dots_ = 0;
        private int cachedSize = -1;

        public static PushServiceConfigMsg parseFrom(byte[] bArr) throws InvalidProtocolBufferMicroException {
            return (PushServiceConfigMsg) new PushServiceConfigMsg().mergeFrom(bArr);
        }

        public final PushServiceConfigMsg clear() {
            clearFetchBucket();
            clearClientVersion();
            clearCloudVersion();
            clearDots();
            this.cachedSize = -1;
            return this;
        }

        public PushServiceConfigMsg clearClientVersion() {
            this.hasClientVersion = false;
            this.clientVersion_ = 0;
            return this;
        }

        public PushServiceConfigMsg clearCloudVersion() {
            this.hasCloudVersion = false;
            this.cloudVersion_ = 0;
            return this;
        }

        public PushServiceConfigMsg clearDots() {
            this.hasDots = false;
            this.dots_ = 0;
            return this;
        }

        public PushServiceConfigMsg clearFetchBucket() {
            this.hasFetchBucket = false;
            this.fetchBucket_ = false;
            return this;
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public int getCachedSize() {
            if (this.cachedSize < 0) {
                getSerializedSize();
            }
            return this.cachedSize;
        }

        public int getClientVersion() {
            return this.clientVersion_;
        }

        public int getCloudVersion() {
            return this.cloudVersion_;
        }

        public int getDots() {
            return this.dots_;
        }

        public boolean getFetchBucket() {
            return this.fetchBucket_;
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public int getSerializedSize() {
            int iComputeBoolSize = hasFetchBucket() ? CodedOutputStreamMicro.computeBoolSize(1, getFetchBucket()) : 0;
            if (hasClientVersion()) {
                iComputeBoolSize += CodedOutputStreamMicro.computeInt32Size(3, getClientVersion());
            }
            if (hasCloudVersion()) {
                iComputeBoolSize += CodedOutputStreamMicro.computeInt32Size(4, getCloudVersion());
            }
            if (hasDots()) {
                iComputeBoolSize += CodedOutputStreamMicro.computeInt32Size(5, getDots());
            }
            this.cachedSize = iComputeBoolSize;
            return iComputeBoolSize;
        }

        public boolean hasClientVersion() {
            return this.hasClientVersion;
        }

        public boolean hasCloudVersion() {
            return this.hasCloudVersion;
        }

        public boolean hasDots() {
            return this.hasDots;
        }

        public boolean hasFetchBucket() {
            return this.hasFetchBucket;
        }

        public final boolean isInitialized() {
            return true;
        }

        public PushServiceConfigMsg setClientVersion(int i2) {
            this.hasClientVersion = true;
            this.clientVersion_ = i2;
            return this;
        }

        public PushServiceConfigMsg setCloudVersion(int i2) {
            this.hasCloudVersion = true;
            this.cloudVersion_ = i2;
            return this;
        }

        public PushServiceConfigMsg setDots(int i2) {
            this.hasDots = true;
            this.dots_ = i2;
            return this;
        }

        public PushServiceConfigMsg setFetchBucket(boolean z2) {
            this.hasFetchBucket = true;
            this.fetchBucket_ = z2;
            return this;
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public void writeTo(CodedOutputStreamMicro codedOutputStreamMicro) throws IOException {
            if (hasFetchBucket()) {
                codedOutputStreamMicro.writeBool(1, getFetchBucket());
            }
            if (hasClientVersion()) {
                codedOutputStreamMicro.writeInt32(3, getClientVersion());
            }
            if (hasCloudVersion()) {
                codedOutputStreamMicro.writeInt32(4, getCloudVersion());
            }
            if (hasDots()) {
                codedOutputStreamMicro.writeInt32(5, getDots());
            }
        }

        public static PushServiceConfigMsg parseFrom(CodedInputStreamMicro codedInputStreamMicro) throws IOException {
            return new PushServiceConfigMsg().mergeFrom(codedInputStreamMicro);
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public PushServiceConfigMsg mergeFrom(CodedInputStreamMicro codedInputStreamMicro) throws IOException {
            while (true) {
                int tag = codedInputStreamMicro.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 8) {
                    setFetchBucket(codedInputStreamMicro.readBool());
                } else if (tag == 24) {
                    setClientVersion(codedInputStreamMicro.readInt32());
                } else if (tag == 32) {
                    setCloudVersion(codedInputStreamMicro.readInt32());
                } else if (tag == 40) {
                    setDots(codedInputStreamMicro.readInt32());
                } else if (!parseUnknownField(codedInputStreamMicro, tag)) {
                    return this;
                }
            }
        }
    }

    public static final class XMMsgBind extends MessageMicro {
        public static final int CLIENT_ATTRS_FIELD_NUMBER = 4;
        public static final int CLOUD_ATTRS_FIELD_NUMBER = 5;
        public static final int KICK_FIELD_NUMBER = 2;
        public static final int METHOD_FIELD_NUMBER = 3;
        public static final int SIG_FIELD_NUMBER = 6;
        public static final int TOKEN_FIELD_NUMBER = 1;
        private boolean hasClientAttrs;
        private boolean hasCloudAttrs;
        private boolean hasKick;
        private boolean hasMethod;
        private boolean hasSig;
        private boolean hasToken;
        private String token_ = "";
        private String kick_ = "";
        private String method_ = "";
        private String clientAttrs_ = "";
        private String cloudAttrs_ = "";
        private String sig_ = "";
        private int cachedSize = -1;

        public static XMMsgBind parseFrom(byte[] bArr) throws InvalidProtocolBufferMicroException {
            return (XMMsgBind) new XMMsgBind().mergeFrom(bArr);
        }

        public final XMMsgBind clear() {
            clearToken();
            clearKick();
            clearMethod();
            clearClientAttrs();
            clearCloudAttrs();
            clearSig();
            this.cachedSize = -1;
            return this;
        }

        public XMMsgBind clearClientAttrs() {
            this.hasClientAttrs = false;
            this.clientAttrs_ = "";
            return this;
        }

        public XMMsgBind clearCloudAttrs() {
            this.hasCloudAttrs = false;
            this.cloudAttrs_ = "";
            return this;
        }

        public XMMsgBind clearKick() {
            this.hasKick = false;
            this.kick_ = "";
            return this;
        }

        public XMMsgBind clearMethod() {
            this.hasMethod = false;
            this.method_ = "";
            return this;
        }

        public XMMsgBind clearSig() {
            this.hasSig = false;
            this.sig_ = "";
            return this;
        }

        public XMMsgBind clearToken() {
            this.hasToken = false;
            this.token_ = "";
            return this;
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public int getCachedSize() {
            if (this.cachedSize < 0) {
                getSerializedSize();
            }
            return this.cachedSize;
        }

        public String getClientAttrs() {
            return this.clientAttrs_;
        }

        public String getCloudAttrs() {
            return this.cloudAttrs_;
        }

        public String getKick() {
            return this.kick_;
        }

        public String getMethod() {
            return this.method_;
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public int getSerializedSize() {
            int iComputeStringSize = hasToken() ? CodedOutputStreamMicro.computeStringSize(1, getToken()) : 0;
            if (hasKick()) {
                iComputeStringSize += CodedOutputStreamMicro.computeStringSize(2, getKick());
            }
            if (hasMethod()) {
                iComputeStringSize += CodedOutputStreamMicro.computeStringSize(3, getMethod());
            }
            if (hasClientAttrs()) {
                iComputeStringSize += CodedOutputStreamMicro.computeStringSize(4, getClientAttrs());
            }
            if (hasCloudAttrs()) {
                iComputeStringSize += CodedOutputStreamMicro.computeStringSize(5, getCloudAttrs());
            }
            if (hasSig()) {
                iComputeStringSize += CodedOutputStreamMicro.computeStringSize(6, getSig());
            }
            this.cachedSize = iComputeStringSize;
            return iComputeStringSize;
        }

        public String getSig() {
            return this.sig_;
        }

        public String getToken() {
            return this.token_;
        }

        public boolean hasClientAttrs() {
            return this.hasClientAttrs;
        }

        public boolean hasCloudAttrs() {
            return this.hasCloudAttrs;
        }

        public boolean hasKick() {
            return this.hasKick;
        }

        public boolean hasMethod() {
            return this.hasMethod;
        }

        public boolean hasSig() {
            return this.hasSig;
        }

        public boolean hasToken() {
            return this.hasToken;
        }

        public final boolean isInitialized() {
            return true;
        }

        public XMMsgBind setClientAttrs(String str) {
            this.hasClientAttrs = true;
            this.clientAttrs_ = str;
            return this;
        }

        public XMMsgBind setCloudAttrs(String str) {
            this.hasCloudAttrs = true;
            this.cloudAttrs_ = str;
            return this;
        }

        public XMMsgBind setKick(String str) {
            this.hasKick = true;
            this.kick_ = str;
            return this;
        }

        public XMMsgBind setMethod(String str) {
            this.hasMethod = true;
            this.method_ = str;
            return this;
        }

        public XMMsgBind setSig(String str) {
            this.hasSig = true;
            this.sig_ = str;
            return this;
        }

        public XMMsgBind setToken(String str) {
            this.hasToken = true;
            this.token_ = str;
            return this;
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public void writeTo(CodedOutputStreamMicro codedOutputStreamMicro) throws IOException {
            if (hasToken()) {
                codedOutputStreamMicro.writeString(1, getToken());
            }
            if (hasKick()) {
                codedOutputStreamMicro.writeString(2, getKick());
            }
            if (hasMethod()) {
                codedOutputStreamMicro.writeString(3, getMethod());
            }
            if (hasClientAttrs()) {
                codedOutputStreamMicro.writeString(4, getClientAttrs());
            }
            if (hasCloudAttrs()) {
                codedOutputStreamMicro.writeString(5, getCloudAttrs());
            }
            if (hasSig()) {
                codedOutputStreamMicro.writeString(6, getSig());
            }
        }

        public static XMMsgBind parseFrom(CodedInputStreamMicro codedInputStreamMicro) throws IOException {
            return new XMMsgBind().mergeFrom(codedInputStreamMicro);
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public XMMsgBind mergeFrom(CodedInputStreamMicro codedInputStreamMicro) throws IOException {
            while (true) {
                int tag = codedInputStreamMicro.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 10) {
                    setToken(codedInputStreamMicro.readString());
                } else if (tag == 18) {
                    setKick(codedInputStreamMicro.readString());
                } else if (tag == 26) {
                    setMethod(codedInputStreamMicro.readString());
                } else if (tag == 34) {
                    setClientAttrs(codedInputStreamMicro.readString());
                } else if (tag == 42) {
                    setCloudAttrs(codedInputStreamMicro.readString());
                } else if (tag == 50) {
                    setSig(codedInputStreamMicro.readString());
                } else if (!parseUnknownField(codedInputStreamMicro, tag)) {
                    return this;
                }
            }
        }
    }

    public static final class XMMsgBindResp extends MessageMicro {
        public static final int ERROR_DESC_FIELD_NUMBER = 4;
        public static final int ERROR_REASON_FIELD_NUMBER = 3;
        public static final int ERROR_TYPE_FIELD_NUMBER = 2;
        public static final int RESULT_FIELD_NUMBER = 1;
        private boolean hasErrorDesc;
        private boolean hasErrorReason;
        private boolean hasErrorType;
        private boolean hasResult;
        private boolean result_ = false;
        private String errorType_ = "";
        private String errorReason_ = "";
        private String errorDesc_ = "";
        private int cachedSize = -1;

        public static XMMsgBindResp parseFrom(byte[] bArr) throws InvalidProtocolBufferMicroException {
            return (XMMsgBindResp) new XMMsgBindResp().mergeFrom(bArr);
        }

        public final XMMsgBindResp clear() {
            clearResult();
            clearErrorType();
            clearErrorReason();
            clearErrorDesc();
            this.cachedSize = -1;
            return this;
        }

        public XMMsgBindResp clearErrorDesc() {
            this.hasErrorDesc = false;
            this.errorDesc_ = "";
            return this;
        }

        public XMMsgBindResp clearErrorReason() {
            this.hasErrorReason = false;
            this.errorReason_ = "";
            return this;
        }

        public XMMsgBindResp clearErrorType() {
            this.hasErrorType = false;
            this.errorType_ = "";
            return this;
        }

        public XMMsgBindResp clearResult() {
            this.hasResult = false;
            this.result_ = false;
            return this;
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public int getCachedSize() {
            if (this.cachedSize < 0) {
                getSerializedSize();
            }
            return this.cachedSize;
        }

        public String getErrorDesc() {
            return this.errorDesc_;
        }

        public String getErrorReason() {
            return this.errorReason_;
        }

        public String getErrorType() {
            return this.errorType_;
        }

        public boolean getResult() {
            return this.result_;
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public int getSerializedSize() {
            int iComputeBoolSize = hasResult() ? CodedOutputStreamMicro.computeBoolSize(1, getResult()) : 0;
            if (hasErrorType()) {
                iComputeBoolSize += CodedOutputStreamMicro.computeStringSize(2, getErrorType());
            }
            if (hasErrorReason()) {
                iComputeBoolSize += CodedOutputStreamMicro.computeStringSize(3, getErrorReason());
            }
            if (hasErrorDesc()) {
                iComputeBoolSize += CodedOutputStreamMicro.computeStringSize(4, getErrorDesc());
            }
            this.cachedSize = iComputeBoolSize;
            return iComputeBoolSize;
        }

        public boolean hasErrorDesc() {
            return this.hasErrorDesc;
        }

        public boolean hasErrorReason() {
            return this.hasErrorReason;
        }

        public boolean hasErrorType() {
            return this.hasErrorType;
        }

        public boolean hasResult() {
            return this.hasResult;
        }

        public final boolean isInitialized() {
            return true;
        }

        public XMMsgBindResp setErrorDesc(String str) {
            this.hasErrorDesc = true;
            this.errorDesc_ = str;
            return this;
        }

        public XMMsgBindResp setErrorReason(String str) {
            this.hasErrorReason = true;
            this.errorReason_ = str;
            return this;
        }

        public XMMsgBindResp setErrorType(String str) {
            this.hasErrorType = true;
            this.errorType_ = str;
            return this;
        }

        public XMMsgBindResp setResult(boolean z2) {
            this.hasResult = true;
            this.result_ = z2;
            return this;
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public void writeTo(CodedOutputStreamMicro codedOutputStreamMicro) throws IOException {
            if (hasResult()) {
                codedOutputStreamMicro.writeBool(1, getResult());
            }
            if (hasErrorType()) {
                codedOutputStreamMicro.writeString(2, getErrorType());
            }
            if (hasErrorReason()) {
                codedOutputStreamMicro.writeString(3, getErrorReason());
            }
            if (hasErrorDesc()) {
                codedOutputStreamMicro.writeString(4, getErrorDesc());
            }
        }

        public static XMMsgBindResp parseFrom(CodedInputStreamMicro codedInputStreamMicro) throws IOException {
            return new XMMsgBindResp().mergeFrom(codedInputStreamMicro);
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public XMMsgBindResp mergeFrom(CodedInputStreamMicro codedInputStreamMicro) throws IOException {
            while (true) {
                int tag = codedInputStreamMicro.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 8) {
                    setResult(codedInputStreamMicro.readBool());
                } else if (tag == 18) {
                    setErrorType(codedInputStreamMicro.readString());
                } else if (tag == 26) {
                    setErrorReason(codedInputStreamMicro.readString());
                } else if (tag == 34) {
                    setErrorDesc(codedInputStreamMicro.readString());
                } else if (!parseUnknownField(codedInputStreamMicro, tag)) {
                    return this;
                }
            }
        }
    }

    public static final class XMMsgConn extends MessageMicro {
        public static final int ANDVER_FIELD_NUMBER = 10;
        public static final int CONNPT_FIELD_NUMBER = 6;
        public static final int HOST_FIELD_NUMBER = 7;
        public static final int LOCALE_FIELD_NUMBER = 8;
        public static final int MODEL_FIELD_NUMBER = 2;
        public static final int OS_FIELD_NUMBER = 3;
        public static final int PSC_FIELD_NUMBER = 9;
        public static final int PUBLICKEYE_FIELD_NUMBER = 12;
        public static final int PUBLICKEYN_FIELD_NUMBER = 11;
        public static final int SDK_FIELD_NUMBER = 5;
        public static final int UDID_FIELD_NUMBER = 4;
        public static final int VERSION_FIELD_NUMBER = 1;
        public static final int XMSFVER_FIELD_NUMBER = 13;
        private int cachedSize;
        private boolean hasAndver;
        private boolean hasConnpt;
        private boolean hasHost;
        private boolean hasLocale;
        private boolean hasModel;
        private boolean hasOs;
        private boolean hasPsc;
        private boolean hasPublicKeyE;
        private boolean hasPublicKeyN;
        private boolean hasSdk;
        private boolean hasUdid;
        private boolean hasVersion;
        private boolean hasXmsfver;
        private ByteStringMicro publicKeyE_;
        private ByteStringMicro publicKeyN_;
        private int xmsfver_;
        private int version_ = 0;
        private String model_ = "";
        private String os_ = "";
        private String udid_ = "";
        private int sdk_ = 0;
        private String connpt_ = "";
        private String host_ = "";
        private String locale_ = "";
        private PushServiceConfigMsg psc_ = null;
        private int andver_ = 0;

        public XMMsgConn() {
            ByteStringMicro byteStringMicro = ByteStringMicro.EMPTY;
            this.publicKeyN_ = byteStringMicro;
            this.publicKeyE_ = byteStringMicro;
            this.xmsfver_ = 0;
            this.cachedSize = -1;
        }

        public static XMMsgConn parseFrom(byte[] bArr) throws InvalidProtocolBufferMicroException {
            return (XMMsgConn) new XMMsgConn().mergeFrom(bArr);
        }

        public final XMMsgConn clear() {
            clearVersion();
            clearModel();
            clearOs();
            clearUdid();
            clearSdk();
            clearConnpt();
            clearHost();
            clearLocale();
            clearPsc();
            clearAndver();
            clearPublicKeyN();
            clearPublicKeyE();
            clearXmsfver();
            this.cachedSize = -1;
            return this;
        }

        public XMMsgConn clearAndver() {
            this.hasAndver = false;
            this.andver_ = 0;
            return this;
        }

        public XMMsgConn clearConnpt() {
            this.hasConnpt = false;
            this.connpt_ = "";
            return this;
        }

        public XMMsgConn clearHost() {
            this.hasHost = false;
            this.host_ = "";
            return this;
        }

        public XMMsgConn clearLocale() {
            this.hasLocale = false;
            this.locale_ = "";
            return this;
        }

        public XMMsgConn clearModel() {
            this.hasModel = false;
            this.model_ = "";
            return this;
        }

        public XMMsgConn clearOs() {
            this.hasOs = false;
            this.os_ = "";
            return this;
        }

        public XMMsgConn clearPsc() {
            this.hasPsc = false;
            this.psc_ = null;
            return this;
        }

        public XMMsgConn clearPublicKeyE() {
            this.hasPublicKeyE = false;
            this.publicKeyE_ = ByteStringMicro.EMPTY;
            return this;
        }

        public XMMsgConn clearPublicKeyN() {
            this.hasPublicKeyN = false;
            this.publicKeyN_ = ByteStringMicro.EMPTY;
            return this;
        }

        public XMMsgConn clearSdk() {
            this.hasSdk = false;
            this.sdk_ = 0;
            return this;
        }

        public XMMsgConn clearUdid() {
            this.hasUdid = false;
            this.udid_ = "";
            return this;
        }

        public XMMsgConn clearVersion() {
            this.hasVersion = false;
            this.version_ = 0;
            return this;
        }

        public XMMsgConn clearXmsfver() {
            this.hasXmsfver = false;
            this.xmsfver_ = 0;
            return this;
        }

        public int getAndver() {
            return this.andver_;
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public int getCachedSize() {
            if (this.cachedSize < 0) {
                getSerializedSize();
            }
            return this.cachedSize;
        }

        public String getConnpt() {
            return this.connpt_;
        }

        public String getHost() {
            return this.host_;
        }

        public String getLocale() {
            return this.locale_;
        }

        public String getModel() {
            return this.model_;
        }

        public String getOs() {
            return this.os_;
        }

        public PushServiceConfigMsg getPsc() {
            return this.psc_;
        }

        public ByteStringMicro getPublicKeyE() {
            return this.publicKeyE_;
        }

        public ByteStringMicro getPublicKeyN() {
            return this.publicKeyN_;
        }

        public int getSdk() {
            return this.sdk_;
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public int getSerializedSize() {
            int iComputeUInt32Size = hasVersion() ? CodedOutputStreamMicro.computeUInt32Size(1, getVersion()) : 0;
            if (hasModel()) {
                iComputeUInt32Size += CodedOutputStreamMicro.computeStringSize(2, getModel());
            }
            if (hasOs()) {
                iComputeUInt32Size += CodedOutputStreamMicro.computeStringSize(3, getOs());
            }
            if (hasUdid()) {
                iComputeUInt32Size += CodedOutputStreamMicro.computeStringSize(4, getUdid());
            }
            if (hasSdk()) {
                iComputeUInt32Size += CodedOutputStreamMicro.computeInt32Size(5, getSdk());
            }
            if (hasConnpt()) {
                iComputeUInt32Size += CodedOutputStreamMicro.computeStringSize(6, getConnpt());
            }
            if (hasHost()) {
                iComputeUInt32Size += CodedOutputStreamMicro.computeStringSize(7, getHost());
            }
            if (hasLocale()) {
                iComputeUInt32Size += CodedOutputStreamMicro.computeStringSize(8, getLocale());
            }
            if (hasPsc()) {
                iComputeUInt32Size += CodedOutputStreamMicro.computeMessageSize(9, getPsc());
            }
            if (hasAndver()) {
                iComputeUInt32Size += CodedOutputStreamMicro.computeInt32Size(10, getAndver());
            }
            if (hasPublicKeyN()) {
                iComputeUInt32Size += CodedOutputStreamMicro.computeBytesSize(11, getPublicKeyN());
            }
            if (hasPublicKeyE()) {
                iComputeUInt32Size += CodedOutputStreamMicro.computeBytesSize(12, getPublicKeyE());
            }
            if (hasXmsfver()) {
                iComputeUInt32Size += CodedOutputStreamMicro.computeInt32Size(13, getXmsfver());
            }
            this.cachedSize = iComputeUInt32Size;
            return iComputeUInt32Size;
        }

        public String getUdid() {
            return this.udid_;
        }

        public int getVersion() {
            return this.version_;
        }

        public int getXmsfver() {
            return this.xmsfver_;
        }

        public boolean hasAndver() {
            return this.hasAndver;
        }

        public boolean hasConnpt() {
            return this.hasConnpt;
        }

        public boolean hasHost() {
            return this.hasHost;
        }

        public boolean hasLocale() {
            return this.hasLocale;
        }

        public boolean hasModel() {
            return this.hasModel;
        }

        public boolean hasOs() {
            return this.hasOs;
        }

        public boolean hasPsc() {
            return this.hasPsc;
        }

        public boolean hasPublicKeyE() {
            return this.hasPublicKeyE;
        }

        public boolean hasPublicKeyN() {
            return this.hasPublicKeyN;
        }

        public boolean hasSdk() {
            return this.hasSdk;
        }

        public boolean hasUdid() {
            return this.hasUdid;
        }

        public boolean hasVersion() {
            return this.hasVersion;
        }

        public boolean hasXmsfver() {
            return this.hasXmsfver;
        }

        public final boolean isInitialized() {
            return true;
        }

        public XMMsgConn setAndver(int i2) {
            this.hasAndver = true;
            this.andver_ = i2;
            return this;
        }

        public XMMsgConn setConnpt(String str) {
            this.hasConnpt = true;
            this.connpt_ = str;
            return this;
        }

        public XMMsgConn setHost(String str) {
            this.hasHost = true;
            this.host_ = str;
            return this;
        }

        public XMMsgConn setLocale(String str) {
            this.hasLocale = true;
            this.locale_ = str;
            return this;
        }

        public XMMsgConn setModel(String str) {
            this.hasModel = true;
            this.model_ = str;
            return this;
        }

        public XMMsgConn setOs(String str) {
            this.hasOs = true;
            this.os_ = str;
            return this;
        }

        public XMMsgConn setPsc(PushServiceConfigMsg pushServiceConfigMsg) {
            pushServiceConfigMsg.getClass();
            this.hasPsc = true;
            this.psc_ = pushServiceConfigMsg;
            return this;
        }

        public XMMsgConn setPublicKeyE(ByteStringMicro byteStringMicro) {
            this.hasPublicKeyE = true;
            this.publicKeyE_ = byteStringMicro;
            return this;
        }

        public XMMsgConn setPublicKeyN(ByteStringMicro byteStringMicro) {
            this.hasPublicKeyN = true;
            this.publicKeyN_ = byteStringMicro;
            return this;
        }

        public XMMsgConn setSdk(int i2) {
            this.hasSdk = true;
            this.sdk_ = i2;
            return this;
        }

        public XMMsgConn setUdid(String str) {
            this.hasUdid = true;
            this.udid_ = str;
            return this;
        }

        public XMMsgConn setVersion(int i2) {
            this.hasVersion = true;
            this.version_ = i2;
            return this;
        }

        public XMMsgConn setXmsfver(int i2) {
            this.hasXmsfver = true;
            this.xmsfver_ = i2;
            return this;
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public void writeTo(CodedOutputStreamMicro codedOutputStreamMicro) throws IOException {
            if (hasVersion()) {
                codedOutputStreamMicro.writeUInt32(1, getVersion());
            }
            if (hasModel()) {
                codedOutputStreamMicro.writeString(2, getModel());
            }
            if (hasOs()) {
                codedOutputStreamMicro.writeString(3, getOs());
            }
            if (hasUdid()) {
                codedOutputStreamMicro.writeString(4, getUdid());
            }
            if (hasSdk()) {
                codedOutputStreamMicro.writeInt32(5, getSdk());
            }
            if (hasConnpt()) {
                codedOutputStreamMicro.writeString(6, getConnpt());
            }
            if (hasHost()) {
                codedOutputStreamMicro.writeString(7, getHost());
            }
            if (hasLocale()) {
                codedOutputStreamMicro.writeString(8, getLocale());
            }
            if (hasPsc()) {
                codedOutputStreamMicro.writeMessage(9, getPsc());
            }
            if (hasAndver()) {
                codedOutputStreamMicro.writeInt32(10, getAndver());
            }
            if (hasPublicKeyN()) {
                codedOutputStreamMicro.writeBytes(11, getPublicKeyN());
            }
            if (hasPublicKeyE()) {
                codedOutputStreamMicro.writeBytes(12, getPublicKeyE());
            }
            if (hasXmsfver()) {
                codedOutputStreamMicro.writeInt32(13, getXmsfver());
            }
        }

        public static XMMsgConn parseFrom(CodedInputStreamMicro codedInputStreamMicro) throws IOException {
            return new XMMsgConn().mergeFrom(codedInputStreamMicro);
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public XMMsgConn mergeFrom(CodedInputStreamMicro codedInputStreamMicro) throws IOException {
            while (true) {
                int tag = codedInputStreamMicro.readTag();
                switch (tag) {
                    case 0:
                        return this;
                    case 8:
                        setVersion(codedInputStreamMicro.readUInt32());
                        break;
                    case 18:
                        setModel(codedInputStreamMicro.readString());
                        break;
                    case 26:
                        setOs(codedInputStreamMicro.readString());
                        break;
                    case 34:
                        setUdid(codedInputStreamMicro.readString());
                        break;
                    case FrozenAppController.FREEZE_CONDITION_ALL /* 40 */:
                        setSdk(codedInputStreamMicro.readInt32());
                        break;
                    case 50:
                        setConnpt(codedInputStreamMicro.readString());
                        break;
                    case 58:
                        setHost(codedInputStreamMicro.readString());
                        break;
                    case 66:
                        setLocale(codedInputStreamMicro.readString());
                        break;
                    case 74:
                        PushServiceConfigMsg pushServiceConfigMsg = new PushServiceConfigMsg();
                        codedInputStreamMicro.readMessage(pushServiceConfigMsg);
                        setPsc(pushServiceConfigMsg);
                        break;
                    case Network.CMWAP_PORT /* 80 */:
                        setAndver(codedInputStreamMicro.readInt32());
                        break;
                    case 90:
                        setPublicKeyN(codedInputStreamMicro.readBytes());
                        break;
                    case 98:
                        setPublicKeyE(codedInputStreamMicro.readBytes());
                        break;
                    case 104:
                        setXmsfver(codedInputStreamMicro.readInt32());
                        break;
                    default:
                        if (!parseUnknownField(codedInputStreamMicro, tag)) {
                            return this;
                        }
                        break;
                        break;
                }
            }
        }
    }

    public static final class XMMsgConnResp extends MessageMicro {
        public static final int CHALLENGE_FIELD_NUMBER = 1;
        public static final int HOST_FIELD_NUMBER = 2;
        public static final int PSC_FIELD_NUMBER = 3;
        private boolean hasChallenge;
        private boolean hasHost;
        private boolean hasPsc;
        private String challenge_ = "";
        private String host_ = "";
        private PushServiceConfigMsg psc_ = null;
        private int cachedSize = -1;

        public static XMMsgConnResp parseFrom(byte[] bArr) throws InvalidProtocolBufferMicroException {
            return (XMMsgConnResp) new XMMsgConnResp().mergeFrom(bArr);
        }

        public final XMMsgConnResp clear() {
            clearChallenge();
            clearHost();
            clearPsc();
            this.cachedSize = -1;
            return this;
        }

        public XMMsgConnResp clearChallenge() {
            this.hasChallenge = false;
            this.challenge_ = "";
            return this;
        }

        public XMMsgConnResp clearHost() {
            this.hasHost = false;
            this.host_ = "";
            return this;
        }

        public XMMsgConnResp clearPsc() {
            this.hasPsc = false;
            this.psc_ = null;
            return this;
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public int getCachedSize() {
            if (this.cachedSize < 0) {
                getSerializedSize();
            }
            return this.cachedSize;
        }

        public String getChallenge() {
            return this.challenge_;
        }

        public String getHost() {
            return this.host_;
        }

        public PushServiceConfigMsg getPsc() {
            return this.psc_;
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public int getSerializedSize() {
            int iComputeStringSize = hasChallenge() ? CodedOutputStreamMicro.computeStringSize(1, getChallenge()) : 0;
            if (hasHost()) {
                iComputeStringSize += CodedOutputStreamMicro.computeStringSize(2, getHost());
            }
            if (hasPsc()) {
                iComputeStringSize += CodedOutputStreamMicro.computeMessageSize(3, getPsc());
            }
            this.cachedSize = iComputeStringSize;
            return iComputeStringSize;
        }

        public boolean hasChallenge() {
            return this.hasChallenge;
        }

        public boolean hasHost() {
            return this.hasHost;
        }

        public boolean hasPsc() {
            return this.hasPsc;
        }

        public final boolean isInitialized() {
            return true;
        }

        public XMMsgConnResp setChallenge(String str) {
            this.hasChallenge = true;
            this.challenge_ = str;
            return this;
        }

        public XMMsgConnResp setHost(String str) {
            this.hasHost = true;
            this.host_ = str;
            return this;
        }

        public XMMsgConnResp setPsc(PushServiceConfigMsg pushServiceConfigMsg) {
            pushServiceConfigMsg.getClass();
            this.hasPsc = true;
            this.psc_ = pushServiceConfigMsg;
            return this;
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public void writeTo(CodedOutputStreamMicro codedOutputStreamMicro) throws IOException {
            if (hasChallenge()) {
                codedOutputStreamMicro.writeString(1, getChallenge());
            }
            if (hasHost()) {
                codedOutputStreamMicro.writeString(2, getHost());
            }
            if (hasPsc()) {
                codedOutputStreamMicro.writeMessage(3, getPsc());
            }
        }

        public static XMMsgConnResp parseFrom(CodedInputStreamMicro codedInputStreamMicro) throws IOException {
            return new XMMsgConnResp().mergeFrom(codedInputStreamMicro);
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public XMMsgConnResp mergeFrom(CodedInputStreamMicro codedInputStreamMicro) throws IOException {
            while (true) {
                int tag = codedInputStreamMicro.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 10) {
                    setChallenge(codedInputStreamMicro.readString());
                } else if (tag == 18) {
                    setHost(codedInputStreamMicro.readString());
                } else if (tag == 26) {
                    PushServiceConfigMsg pushServiceConfigMsg = new PushServiceConfigMsg();
                    codedInputStreamMicro.readMessage(pushServiceConfigMsg);
                    setPsc(pushServiceConfigMsg);
                } else if (!parseUnknownField(codedInputStreamMicro, tag)) {
                    return this;
                }
            }
        }
    }

    public static final class XMMsgKick extends MessageMicro {
        public static final int DESC_FIELD_NUMBER = 3;
        public static final int REASON_FIELD_NUMBER = 2;
        public static final int TYPE_FIELD_NUMBER = 1;
        private boolean hasDesc;
        private boolean hasReason;
        private boolean hasType;
        private String type_ = "";
        private String reason_ = "";
        private String desc_ = "";
        private int cachedSize = -1;

        public static XMMsgKick parseFrom(byte[] bArr) throws InvalidProtocolBufferMicroException {
            return (XMMsgKick) new XMMsgKick().mergeFrom(bArr);
        }

        public final XMMsgKick clear() {
            clearType();
            clearReason();
            clearDesc();
            this.cachedSize = -1;
            return this;
        }

        public XMMsgKick clearDesc() {
            this.hasDesc = false;
            this.desc_ = "";
            return this;
        }

        public XMMsgKick clearReason() {
            this.hasReason = false;
            this.reason_ = "";
            return this;
        }

        public XMMsgKick clearType() {
            this.hasType = false;
            this.type_ = "";
            return this;
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public int getCachedSize() {
            if (this.cachedSize < 0) {
                getSerializedSize();
            }
            return this.cachedSize;
        }

        public String getDesc() {
            return this.desc_;
        }

        public String getReason() {
            return this.reason_;
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public int getSerializedSize() {
            int iComputeStringSize = hasType() ? CodedOutputStreamMicro.computeStringSize(1, getType()) : 0;
            if (hasReason()) {
                iComputeStringSize += CodedOutputStreamMicro.computeStringSize(2, getReason());
            }
            if (hasDesc()) {
                iComputeStringSize += CodedOutputStreamMicro.computeStringSize(3, getDesc());
            }
            this.cachedSize = iComputeStringSize;
            return iComputeStringSize;
        }

        public String getType() {
            return this.type_;
        }

        public boolean hasDesc() {
            return this.hasDesc;
        }

        public boolean hasReason() {
            return this.hasReason;
        }

        public boolean hasType() {
            return this.hasType;
        }

        public final boolean isInitialized() {
            return true;
        }

        public XMMsgKick setDesc(String str) {
            this.hasDesc = true;
            this.desc_ = str;
            return this;
        }

        public XMMsgKick setReason(String str) {
            this.hasReason = true;
            this.reason_ = str;
            return this;
        }

        public XMMsgKick setType(String str) {
            this.hasType = true;
            this.type_ = str;
            return this;
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public void writeTo(CodedOutputStreamMicro codedOutputStreamMicro) throws IOException {
            if (hasType()) {
                codedOutputStreamMicro.writeString(1, getType());
            }
            if (hasReason()) {
                codedOutputStreamMicro.writeString(2, getReason());
            }
            if (hasDesc()) {
                codedOutputStreamMicro.writeString(3, getDesc());
            }
        }

        public static XMMsgKick parseFrom(CodedInputStreamMicro codedInputStreamMicro) throws IOException {
            return new XMMsgKick().mergeFrom(codedInputStreamMicro);
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public XMMsgKick mergeFrom(CodedInputStreamMicro codedInputStreamMicro) throws IOException {
            while (true) {
                int tag = codedInputStreamMicro.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 10) {
                    setType(codedInputStreamMicro.readString());
                } else if (tag == 18) {
                    setReason(codedInputStreamMicro.readString());
                } else if (tag == 26) {
                    setDesc(codedInputStreamMicro.readString());
                } else if (!parseUnknownField(codedInputStreamMicro, tag)) {
                    return this;
                }
            }
        }
    }

    public static final class XMMsgNotify extends MessageMicro {
        public static final int ERR_CODE_FIELD_NUMBER = 1;
        public static final int ERR_STR_FIELD_NUMBER = 2;
        private boolean hasErrCode;
        private boolean hasErrStr;
        private int errCode_ = 0;
        private String errStr_ = "";
        private int cachedSize = -1;

        public static XMMsgNotify parseFrom(byte[] bArr) throws InvalidProtocolBufferMicroException {
            return (XMMsgNotify) new XMMsgNotify().mergeFrom(bArr);
        }

        public final XMMsgNotify clear() {
            clearErrCode();
            clearErrStr();
            this.cachedSize = -1;
            return this;
        }

        public XMMsgNotify clearErrCode() {
            this.hasErrCode = false;
            this.errCode_ = 0;
            return this;
        }

        public XMMsgNotify clearErrStr() {
            this.hasErrStr = false;
            this.errStr_ = "";
            return this;
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public int getCachedSize() {
            if (this.cachedSize < 0) {
                getSerializedSize();
            }
            return this.cachedSize;
        }

        public int getErrCode() {
            return this.errCode_;
        }

        public String getErrStr() {
            return this.errStr_;
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public int getSerializedSize() {
            int iComputeInt32Size = hasErrCode() ? CodedOutputStreamMicro.computeInt32Size(1, getErrCode()) : 0;
            if (hasErrStr()) {
                iComputeInt32Size += CodedOutputStreamMicro.computeStringSize(2, getErrStr());
            }
            this.cachedSize = iComputeInt32Size;
            return iComputeInt32Size;
        }

        public boolean hasErrCode() {
            return this.hasErrCode;
        }

        public boolean hasErrStr() {
            return this.hasErrStr;
        }

        public final boolean isInitialized() {
            return true;
        }

        public XMMsgNotify setErrCode(int i2) {
            this.hasErrCode = true;
            this.errCode_ = i2;
            return this;
        }

        public XMMsgNotify setErrStr(String str) {
            this.hasErrStr = true;
            this.errStr_ = str;
            return this;
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public void writeTo(CodedOutputStreamMicro codedOutputStreamMicro) throws IOException {
            if (hasErrCode()) {
                codedOutputStreamMicro.writeInt32(1, getErrCode());
            }
            if (hasErrStr()) {
                codedOutputStreamMicro.writeString(2, getErrStr());
            }
        }

        public static XMMsgNotify parseFrom(CodedInputStreamMicro codedInputStreamMicro) throws IOException {
            return new XMMsgNotify().mergeFrom(codedInputStreamMicro);
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public XMMsgNotify mergeFrom(CodedInputStreamMicro codedInputStreamMicro) throws IOException {
            while (true) {
                int tag = codedInputStreamMicro.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 8) {
                    setErrCode(codedInputStreamMicro.readInt32());
                } else if (tag == 18) {
                    setErrStr(codedInputStreamMicro.readString());
                } else if (!parseUnknownField(codedInputStreamMicro, tag)) {
                    return this;
                }
            }
        }
    }

    public static final class XMMsgP extends MessageMicro {
        public static final int COOKIE_FIELD_NUMBER = 1;
        private boolean hasCookie;
        private ByteStringMicro cookie_ = ByteStringMicro.EMPTY;
        private int cachedSize = -1;

        public static XMMsgP parseFrom(byte[] bArr) throws InvalidProtocolBufferMicroException {
            return (XMMsgP) new XMMsgP().mergeFrom(bArr);
        }

        public final XMMsgP clear() {
            clearCookie();
            this.cachedSize = -1;
            return this;
        }

        public XMMsgP clearCookie() {
            this.hasCookie = false;
            this.cookie_ = ByteStringMicro.EMPTY;
            return this;
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public int getCachedSize() {
            if (this.cachedSize < 0) {
                getSerializedSize();
            }
            return this.cachedSize;
        }

        public ByteStringMicro getCookie() {
            return this.cookie_;
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public int getSerializedSize() {
            int iComputeBytesSize = hasCookie() ? CodedOutputStreamMicro.computeBytesSize(1, getCookie()) : 0;
            this.cachedSize = iComputeBytesSize;
            return iComputeBytesSize;
        }

        public boolean hasCookie() {
            return this.hasCookie;
        }

        public final boolean isInitialized() {
            return true;
        }

        public XMMsgP setCookie(ByteStringMicro byteStringMicro) {
            this.hasCookie = true;
            this.cookie_ = byteStringMicro;
            return this;
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public void writeTo(CodedOutputStreamMicro codedOutputStreamMicro) throws IOException {
            if (hasCookie()) {
                codedOutputStreamMicro.writeBytes(1, getCookie());
            }
        }

        public static XMMsgP parseFrom(CodedInputStreamMicro codedInputStreamMicro) throws IOException {
            return new XMMsgP().mergeFrom(codedInputStreamMicro);
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public XMMsgP mergeFrom(CodedInputStreamMicro codedInputStreamMicro) throws IOException {
            while (true) {
                int tag = codedInputStreamMicro.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 10) {
                    setCookie(codedInputStreamMicro.readBytes());
                } else if (!parseUnknownField(codedInputStreamMicro, tag)) {
                    return this;
                }
            }
        }
    }

    public static final class XMMsgPing extends MessageMicro {
        public static final int PSC_FIELD_NUMBER = 2;
        public static final int STATS_FIELD_NUMBER = 1;
        private boolean hasPsc;
        private boolean hasStats;
        private ByteStringMicro stats_ = ByteStringMicro.EMPTY;
        private PushServiceConfigMsg psc_ = null;
        private int cachedSize = -1;

        public static XMMsgPing parseFrom(byte[] bArr) throws InvalidProtocolBufferMicroException {
            return (XMMsgPing) new XMMsgPing().mergeFrom(bArr);
        }

        public final XMMsgPing clear() {
            clearStats();
            clearPsc();
            this.cachedSize = -1;
            return this;
        }

        public XMMsgPing clearPsc() {
            this.hasPsc = false;
            this.psc_ = null;
            return this;
        }

        public XMMsgPing clearStats() {
            this.hasStats = false;
            this.stats_ = ByteStringMicro.EMPTY;
            return this;
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public int getCachedSize() {
            if (this.cachedSize < 0) {
                getSerializedSize();
            }
            return this.cachedSize;
        }

        public PushServiceConfigMsg getPsc() {
            return this.psc_;
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public int getSerializedSize() {
            int iComputeBytesSize = hasStats() ? CodedOutputStreamMicro.computeBytesSize(1, getStats()) : 0;
            if (hasPsc()) {
                iComputeBytesSize += CodedOutputStreamMicro.computeMessageSize(2, getPsc());
            }
            this.cachedSize = iComputeBytesSize;
            return iComputeBytesSize;
        }

        public ByteStringMicro getStats() {
            return this.stats_;
        }

        public boolean hasPsc() {
            return this.hasPsc;
        }

        public boolean hasStats() {
            return this.hasStats;
        }

        public final boolean isInitialized() {
            return true;
        }

        public XMMsgPing setPsc(PushServiceConfigMsg pushServiceConfigMsg) {
            pushServiceConfigMsg.getClass();
            this.hasPsc = true;
            this.psc_ = pushServiceConfigMsg;
            return this;
        }

        public XMMsgPing setStats(ByteStringMicro byteStringMicro) {
            this.hasStats = true;
            this.stats_ = byteStringMicro;
            return this;
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public void writeTo(CodedOutputStreamMicro codedOutputStreamMicro) throws IOException {
            if (hasStats()) {
                codedOutputStreamMicro.writeBytes(1, getStats());
            }
            if (hasPsc()) {
                codedOutputStreamMicro.writeMessage(2, getPsc());
            }
        }

        public static XMMsgPing parseFrom(CodedInputStreamMicro codedInputStreamMicro) throws IOException {
            return new XMMsgPing().mergeFrom(codedInputStreamMicro);
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public XMMsgPing mergeFrom(CodedInputStreamMicro codedInputStreamMicro) throws IOException {
            while (true) {
                int tag = codedInputStreamMicro.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 10) {
                    setStats(codedInputStreamMicro.readBytes());
                } else if (tag == 18) {
                    PushServiceConfigMsg pushServiceConfigMsg = new PushServiceConfigMsg();
                    codedInputStreamMicro.readMessage(pushServiceConfigMsg);
                    setPsc(pushServiceConfigMsg);
                } else if (!parseUnknownField(codedInputStreamMicro, tag)) {
                    return this;
                }
            }
        }
    }

    public static final class XMMsgU extends MessageMicro {
        public static final int END_FIELD_NUMBER = 4;
        public static final int FORCE_FIELD_NUMBER = 5;
        public static final int MAXLEN_FIELD_NUMBER = 6;
        public static final int START_FIELD_NUMBER = 3;
        public static final int TOKEN_FIELD_NUMBER = 2;
        public static final int URL_FIELD_NUMBER = 1;
        private boolean hasEnd;
        private boolean hasForce;
        private boolean hasMaxlen;
        private boolean hasStart;
        private boolean hasToken;
        private boolean hasUrl;
        private String url_ = "";
        private String token_ = "";
        private long start_ = 0;
        private long end_ = 0;
        private boolean force_ = false;
        private int maxlen_ = 0;
        private int cachedSize = -1;

        public static XMMsgU parseFrom(byte[] bArr) throws InvalidProtocolBufferMicroException {
            return (XMMsgU) new XMMsgU().mergeFrom(bArr);
        }

        public final XMMsgU clear() {
            clearUrl();
            clearToken();
            clearStart();
            clearEnd();
            clearForce();
            clearMaxlen();
            this.cachedSize = -1;
            return this;
        }

        public XMMsgU clearEnd() {
            this.hasEnd = false;
            this.end_ = 0L;
            return this;
        }

        public XMMsgU clearForce() {
            this.hasForce = false;
            this.force_ = false;
            return this;
        }

        public XMMsgU clearMaxlen() {
            this.hasMaxlen = false;
            this.maxlen_ = 0;
            return this;
        }

        public XMMsgU clearStart() {
            this.hasStart = false;
            this.start_ = 0L;
            return this;
        }

        public XMMsgU clearToken() {
            this.hasToken = false;
            this.token_ = "";
            return this;
        }

        public XMMsgU clearUrl() {
            this.hasUrl = false;
            this.url_ = "";
            return this;
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public int getCachedSize() {
            if (this.cachedSize < 0) {
                getSerializedSize();
            }
            return this.cachedSize;
        }

        public long getEnd() {
            return this.end_;
        }

        public boolean getForce() {
            return this.force_;
        }

        public int getMaxlen() {
            return this.maxlen_;
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public int getSerializedSize() {
            int iComputeStringSize = hasUrl() ? CodedOutputStreamMicro.computeStringSize(1, getUrl()) : 0;
            if (hasToken()) {
                iComputeStringSize += CodedOutputStreamMicro.computeStringSize(2, getToken());
            }
            if (hasStart()) {
                iComputeStringSize += CodedOutputStreamMicro.computeUInt64Size(3, getStart());
            }
            if (hasEnd()) {
                iComputeStringSize += CodedOutputStreamMicro.computeUInt64Size(4, getEnd());
            }
            if (hasForce()) {
                iComputeStringSize += CodedOutputStreamMicro.computeBoolSize(5, getForce());
            }
            if (hasMaxlen()) {
                iComputeStringSize += CodedOutputStreamMicro.computeInt32Size(6, getMaxlen());
            }
            this.cachedSize = iComputeStringSize;
            return iComputeStringSize;
        }

        public long getStart() {
            return this.start_;
        }

        public String getToken() {
            return this.token_;
        }

        public String getUrl() {
            return this.url_;
        }

        public boolean hasEnd() {
            return this.hasEnd;
        }

        public boolean hasForce() {
            return this.hasForce;
        }

        public boolean hasMaxlen() {
            return this.hasMaxlen;
        }

        public boolean hasStart() {
            return this.hasStart;
        }

        public boolean hasToken() {
            return this.hasToken;
        }

        public boolean hasUrl() {
            return this.hasUrl;
        }

        public final boolean isInitialized() {
            return true;
        }

        public XMMsgU setEnd(long j2) {
            this.hasEnd = true;
            this.end_ = j2;
            return this;
        }

        public XMMsgU setForce(boolean z2) {
            this.hasForce = true;
            this.force_ = z2;
            return this;
        }

        public XMMsgU setMaxlen(int i2) {
            this.hasMaxlen = true;
            this.maxlen_ = i2;
            return this;
        }

        public XMMsgU setStart(long j2) {
            this.hasStart = true;
            this.start_ = j2;
            return this;
        }

        public XMMsgU setToken(String str) {
            this.hasToken = true;
            this.token_ = str;
            return this;
        }

        public XMMsgU setUrl(String str) {
            this.hasUrl = true;
            this.url_ = str;
            return this;
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public void writeTo(CodedOutputStreamMicro codedOutputStreamMicro) throws IOException {
            if (hasUrl()) {
                codedOutputStreamMicro.writeString(1, getUrl());
            }
            if (hasToken()) {
                codedOutputStreamMicro.writeString(2, getToken());
            }
            if (hasStart()) {
                codedOutputStreamMicro.writeUInt64(3, getStart());
            }
            if (hasEnd()) {
                codedOutputStreamMicro.writeUInt64(4, getEnd());
            }
            if (hasForce()) {
                codedOutputStreamMicro.writeBool(5, getForce());
            }
            if (hasMaxlen()) {
                codedOutputStreamMicro.writeInt32(6, getMaxlen());
            }
        }

        public static XMMsgU parseFrom(CodedInputStreamMicro codedInputStreamMicro) throws IOException {
            return new XMMsgU().mergeFrom(codedInputStreamMicro);
        }

        @Override // com.google.protobuf.micro.MessageMicro
        public XMMsgU mergeFrom(CodedInputStreamMicro codedInputStreamMicro) throws IOException {
            while (true) {
                int tag = codedInputStreamMicro.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 10) {
                    setUrl(codedInputStreamMicro.readString());
                } else if (tag == 18) {
                    setToken(codedInputStreamMicro.readString());
                } else if (tag == 24) {
                    setStart(codedInputStreamMicro.readUInt64());
                } else if (tag == 32) {
                    setEnd(codedInputStreamMicro.readUInt64());
                } else if (tag == 40) {
                    setForce(codedInputStreamMicro.readBool());
                } else if (tag == 48) {
                    setMaxlen(codedInputStreamMicro.readInt32());
                } else if (!parseUnknownField(codedInputStreamMicro, tag)) {
                    return this;
                }
            }
        }
    }

    private ChannelMessage() {
    }
}
