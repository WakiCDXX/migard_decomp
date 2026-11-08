package com.xiaomi.slim;

import android.text.TextUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.mipush.sdk.Constants;
import com.xiaomi.push.protobuf.ChannelMessage;
import com.xiaomi.push.service.PacketHelper;
import com.xiaomi.push.service.RC4Cryption;
import com.xiaomi.smack.packet.Packet;
import com.xiaomi.smack.util.StringUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/* JADX INFO: loaded from: classes.dex */
public class Blob {
    static final int CHCKSUM_SIZE = 4;
    static final int CIPHER_AES = 2;
    static final int CIPHER_NONE = 0;
    static final int CIPHER_RC4 = 1;
    public static final String CLIENT_PING_ID = "0";
    public static final String CMD_BIND = "BIND";
    public static final String CMD_CLOSE = "CLOSE";
    public static final String CMD_CONN = "CONN";
    public static final String CMD_KICK = "KICK";
    public static final String CMD_NOTIFY = "NOTIFY";
    public static final String CMD_PING = "PING";
    public static final String CMD_SECMSG = "SECMSG";
    public static final String CMD_SYNC = "SYNC";
    public static final String CMD_UNBIND = "UBND";
    public static final String CMD_XMLMSG = "XMLMSG";
    public static final int ERROR_ALLOCATE_SESSION_FAILED = 206;
    public static final int ERROR_BIND_TIMEOUT = 206;
    public static final int ERROR_FE_TRANSPORT_CLOSED = 303;
    public static final int ERROR_FIRST_PACKET_NOT_CONN = 103;
    public static final int ERROR_HOST_UNKNOWN = 100;
    public static final int ERROR_INVALID_CHID = 200;
    public static final int ERROR_INVALID_FROM = 201;
    public static final int ERROR_INVALID_XML = 205;
    public static final int ERROR_PARSE_CONN = 102;
    public static final int ERROR_PARSE_REQUEST = 204;
    public static final int ERROR_SEND_TO_GATEWAY_FAILED = 302;
    public static final int ERROR_SEND_TO_IMS_FAILED = 300;
    public static final int ERROR_SEND_TO_XMQ_FAILED = 301;
    public static final int ERROR_UNEXPECTED_REQUEST = 202;
    public static final int ERROR_UNSUPPORTED_VERSION = 101;
    public static final int ERROR_USR_NOT_BIND = 203;
    static final int HEADER_SIZE = 8;
    private static final byte HEAD_LEN_OFFSET = 2;
    public static final String ID_NOT_AVAILABLE = "ID_NOT_AVAILABLE";
    static final byte LENGTH_OFFSET = 4;
    public static final short MAGIC = -15618;
    static final byte MAGIC_OFFSET = 0;
    public static final int MAX_BLOB_SIZE = 32768;
    static final short PAYLOAD_BINARY = 2;
    private static final byte PAYLOAD_LEN_OFFSET = 4;
    public static final short PAYLOAD_PROTO = 2;
    public static final short PAYLOAD_THRIFT = 1;
    private static final byte PAYLOAD_TYPE_OFFSET = 0;
    public static final short PAYLOAD_XML = 3;
    public static final String SERVER_PING_ID = "1";
    public static final String SUBCMD_CONF = "CONF";
    private static final byte SUB_HEADER_OFFSET = 8;
    static final int TYPE_DOWNSTREAM = 2;
    static final int TYPE_UPSTREAM = 1;
    public static final short VERSION = 5;
    public static final short VERSION5 = 4;
    static final byte VERSION_OFFSET = 2;
    public static final String XIAOMI_SERVER = "xiaomi.com";
    private final long mCreateTime;
    private ChannelMessage.ClientHeader mHeader;
    String mPackageName;
    private byte[] mPayload;
    private short mPayloadType;
    int mStreamType;
    private static String prefix = StringUtils.randomString(5) + Constants.ACCEPT_TIME_SEPARATOR_SERVER;
    private static long id = 0;
    private static final byte[] EMPTY = new byte[0];

    public Blob() {
        this.mPayloadType = (short) 2;
        this.mPayload = EMPTY;
        this.mPackageName = null;
        this.mCreateTime = System.currentTimeMillis();
        this.mHeader = new ChannelMessage.ClientHeader();
        this.mStreamType = 1;
    }

    @Deprecated
    public static Blob from(Packet packet, String str) {
        int i2;
        Blob blob = new Blob();
        try {
            i2 = Integer.parseInt(packet.getChannelId());
        } catch (Exception e2) {
            MyLog.w("Blob parse chid err " + e2.getMessage());
            i2 = 1;
        }
        blob.setChannelId(i2);
        blob.setPacketID(packet.getPacketID());
        blob.setFrom(packet.getFrom());
        blob.setPackageName(packet.getPackageName());
        blob.setCmd(CMD_XMLMSG, null);
        try {
            blob.setPayload(packet.toXML().getBytes("utf8"), str);
            if (TextUtils.isEmpty(str)) {
                blob.setPayloadType((short) 3);
            } else {
                blob.setPayloadType((short) 2);
                blob.setCmd(CMD_SECMSG, null);
            }
        } catch (UnsupportedEncodingException e3) {
            MyLog.w("Blob setPayload err： " + e3.getMessage());
        }
        return blob;
    }

    public static synchronized String nextID() {
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append(prefix);
        long j2 = id;
        id = 1 + j2;
        sb.append(Long.toString(j2));
        return sb.toString();
    }

    public int getChannelId() {
        return this.mHeader.getChid();
    }

    public String getCmd() {
        return this.mHeader.getCmd();
    }

    public long getCreateTime() {
        return this.mCreateTime;
    }

    public byte[] getDecryptedPayload(String str) {
        if (this.mHeader.getCipher() == 1) {
            return BlobCompressed.decompress(this, RC4Cryption.encrypt(RC4Cryption.generateKeyForRC4(str, getPacketID()), this.mPayload));
        }
        if (this.mHeader.getCipher() == 0) {
            return BlobCompressed.decompress(this, this.mPayload);
        }
        MyLog.w("unknow cipher = " + this.mHeader.getCipher());
        return BlobCompressed.decompress(this, this.mPayload);
    }

    public int getErrCode() {
        return this.mHeader.getErrCode();
    }

    public String getErrStr() {
        return this.mHeader.getErrStr();
    }

    public String getFullUserName() {
        if (!this.mHeader.hasUuid()) {
            return null;
        }
        return Long.toString(this.mHeader.getUuid()) + "@" + this.mHeader.getServer() + "/" + this.mHeader.getResource();
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public String getPacketID() {
        String id2 = this.mHeader.getId();
        if ("ID_NOT_AVAILABLE".equals(id2)) {
            return null;
        }
        if (this.mHeader.hasId()) {
            return id2;
        }
        String strNextID = nextID();
        this.mHeader.setId(strNextID);
        return strNextID;
    }

    public byte[] getPayload() {
        return BlobCompressed.decompress(this, this.mPayload);
    }

    public short getPayloadType() {
        return this.mPayloadType;
    }

    public int getSerializedSize() {
        return this.mHeader.getSerializedSize() + 8 + this.mPayload.length;
    }

    public String getSubcmd() {
        return this.mHeader.getSubcmd();
    }

    public long getTimestamp() {
        return this.mHeader.getTimestamp();
    }

    public long getUUID() {
        return this.mHeader.getUuid();
    }

    public boolean hasErr() {
        return this.mHeader.hasErrCode();
    }

    public boolean hasTimestamp() {
        return this.mHeader.hasTimestamp();
    }

    public void setChannelId(int i2) {
        this.mHeader.setChid(i2);
    }

    public void setCmd(String str, String str2) {
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("command should not be empty");
        }
        this.mHeader.setCmd(str);
        this.mHeader.clearSubcmd();
        if (TextUtils.isEmpty(str2)) {
            return;
        }
        this.mHeader.setSubcmd(str2);
    }

    public void setDelay(long j2) {
        this.mHeader.setDelay(j2);
    }

    public void setFrom(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        int iIndexOf = str.indexOf("@");
        try {
            long j2 = Long.parseLong(str.substring(0, iIndexOf));
            int iIndexOf2 = str.indexOf("/", iIndexOf);
            String strSubstring = str.substring(iIndexOf + 1, iIndexOf2);
            String strSubstring2 = str.substring(iIndexOf2 + 1);
            this.mHeader.setUuid(j2);
            this.mHeader.setServer(strSubstring);
            this.mHeader.setResource(strSubstring2);
        } catch (Exception e2) {
            MyLog.w("Blob parse user err " + e2.getMessage());
        }
    }

    public void setPackageName(String str) {
        this.mPackageName = str;
    }

    public void setPacketID(String str) {
        this.mHeader.setId(str);
    }

    public void setPayload(byte[] bArr, String str) {
        if (TextUtils.isEmpty(str)) {
            this.mHeader.setCipher(0);
            this.mPayload = bArr;
        } else {
            this.mHeader.setCipher(1);
            this.mPayload = RC4Cryption.encrypt(RC4Cryption.generateKeyForRC4(str, getPacketID()), bArr);
        }
    }

    public void setPayloadType(short s2) {
        this.mPayloadType = s2;
    }

    public void setTimestamp(long j2) {
        this.mHeader.setTimestamp(j2);
    }

    public void setUUID(long j2) {
        this.mHeader.setUuid(j2);
    }

    ByteBuffer toByteArray(ByteBuffer byteBuffer) {
        if (byteBuffer == null) {
            byteBuffer = ByteBuffer.allocate(getSerializedSize());
        }
        byteBuffer.putShort(this.mPayloadType);
        byteBuffer.putShort((short) this.mHeader.getCachedSize());
        byteBuffer.putInt(this.mPayload.length);
        int iPosition = byteBuffer.position();
        this.mHeader.toByteArray(byteBuffer.array(), byteBuffer.arrayOffset() + iPosition, this.mHeader.getCachedSize());
        byteBuffer.position(iPosition + this.mHeader.getCachedSize());
        byteBuffer.put(this.mPayload);
        return byteBuffer;
    }

    public String toString() {
        return "Blob [chid=" + getChannelId() + "; Id=" + PacketHelper.filterRegistrationPacketIdForLog(getPacketID()) + "; cmd=" + getCmd() + "; type=" + ((int) getPayloadType()) + "; from=" + getFullUserName() + " ]";
    }

    Blob(ChannelMessage.ClientHeader clientHeader, short s2, byte[] bArr) {
        this.mPayloadType = (short) 2;
        this.mPayload = EMPTY;
        this.mPackageName = null;
        this.mCreateTime = System.currentTimeMillis();
        this.mHeader = clientHeader;
        this.mPayloadType = s2;
        this.mPayload = bArr;
        this.mStreamType = 2;
    }

    public void setFrom(long j2, String str, String str2) {
        if (j2 != 0) {
            this.mHeader.setUuid(j2);
        }
        if (!TextUtils.isEmpty(str)) {
            this.mHeader.setServer(str);
        }
        if (TextUtils.isEmpty(str2)) {
            return;
        }
        this.mHeader.setResource(str2);
    }

    static Blob from(ByteBuffer byteBuffer) throws IOException {
        try {
            ByteBuffer byteBufferSlice = byteBuffer.slice();
            short s2 = byteBufferSlice.getShort(0);
            short s3 = byteBufferSlice.getShort(2);
            int i2 = byteBufferSlice.getInt(4);
            ChannelMessage.ClientHeader clientHeader = new ChannelMessage.ClientHeader();
            clientHeader.mergeFrom(byteBufferSlice.array(), byteBufferSlice.arrayOffset() + 8, s3);
            byte[] bArr = new byte[i2];
            byteBufferSlice.position(s3 + 8);
            byteBufferSlice.get(bArr, 0, i2);
            return new Blob(clientHeader, s2, bArr);
        } catch (Exception e2) {
            MyLog.w("read Blob err :" + e2.getMessage());
            throw new IOException("Malformed Input");
        }
    }
}
