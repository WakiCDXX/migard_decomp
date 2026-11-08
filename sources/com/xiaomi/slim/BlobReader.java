package com.xiaomi.slim;

import android.text.TextUtils;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.misc.DebugUtils;
import com.xiaomi.push.protobuf.ChannelMessage;
import com.xiaomi.push.service.PushClientsManager;
import com.xiaomi.push.service.RC4Cryption;
import com.xiaomi.smack.packet.Packet;
import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.zip.Adler32;

/* JADX INFO: loaded from: classes.dex */
class BlobReader {
    private static final int MAX_BLOB_THRSHOLD = 4096;
    private static final int MIN_BLOB_SIZE = 2048;
    private SlimConnection mConnection;
    private volatile boolean mDone;
    private InputStream mInputStream;
    private byte[] mKey;
    private ByteBuffer mBuffer = ByteBuffer.allocate(2048);
    private ByteBuffer mCRCBuf = ByteBuffer.allocate(4);
    private Adler32 mChecksumTool = new Adler32();
    private PacketParser mPacketParser = new PacketParser();

    BlobReader(InputStream inputStream, SlimConnection slimConnection) {
        this.mInputStream = new BufferedInputStream(inputStream);
        this.mConnection = slimConnection;
    }

    private void loop() throws IOException {
        boolean z2 = false;
        this.mDone = false;
        Blob blob = read();
        if (Blob.CMD_CONN.equals(blob.getCmd())) {
            ChannelMessage.XMMsgConnResp from = ChannelMessage.XMMsgConnResp.parseFrom(blob.getPayload());
            if (from.hasChallenge()) {
                this.mConnection.setChallenge(from.getChallenge());
                z2 = true;
            }
            if (from.hasPsc()) {
                ChannelMessage.PushServiceConfigMsg psc = from.getPsc();
                Blob blob2 = new Blob();
                blob2.setCmd(Blob.CMD_SYNC, Blob.SUBCMD_CONF);
                blob2.setPayload(psc.toByteArray(), null);
                this.mConnection.notifyDataArrived(blob2);
            }
            MyLog.w("[Slim] CONN: host = " + from.getHost());
        }
        if (!z2) {
            MyLog.w("[Slim] Invalid CONN");
            throw new IOException("Invalid Connection");
        }
        this.mKey = this.mConnection.getKey();
        while (!this.mDone) {
            Blob blob3 = read();
            long jCurrentTimeMillis = System.currentTimeMillis();
            this.mConnection.setReadAlive();
            short payloadType = blob3.getPayloadType();
            if (payloadType == 1) {
                this.mConnection.notifyDataArrived(blob3);
            } else if (payloadType != 2) {
                if (payloadType != 3) {
                    MyLog.w("[Slim] unknow blob type " + ((int) blob3.getPayloadType()));
                } else {
                    try {
                        this.mConnection.notifyDataArrived(this.mPacketParser.parse(blob3.getPayload(), this.mConnection));
                    } catch (Exception e2) {
                        MyLog.w("[Slim] Parse packet from Blob chid=" + blob3.getChannelId() + "; Id=" + blob3.getPacketID() + " failure:" + e2.getMessage());
                    }
                }
            } else if (Blob.CMD_SECMSG.equals(blob3.getCmd()) && ((blob3.getChannelId() == 2 || blob3.getChannelId() == 3) && TextUtils.isEmpty(blob3.getSubcmd()))) {
                try {
                    Packet packet = this.mPacketParser.parse(blob3.getDecryptedPayload(PushClientsManager.getInstance().getClientLoginInfoByChidAndUserId(Integer.valueOf(blob3.getChannelId()).toString(), blob3.getFullUserName()).security), this.mConnection);
                    packet.receiveTime = jCurrentTimeMillis;
                    this.mConnection.notifyDataArrived(packet);
                } catch (Exception e3) {
                    MyLog.w("[Slim] Parse packet from Blob chid=" + blob3.getChannelId() + "; Id=" + blob3.getPacketID() + " failure:" + e3.getMessage());
                }
            } else {
                this.mConnection.notifyDataArrived(blob3);
            }
        }
    }

    private ByteBuffer readOnePacket() throws IOException {
        this.mBuffer.clear();
        read(this.mBuffer, 8);
        short s2 = this.mBuffer.getShort(0);
        short s3 = this.mBuffer.getShort(2);
        if (s2 != -15618 || s3 != 5) {
            throw new IOException("Malformed Input");
        }
        int i2 = this.mBuffer.getInt(4);
        int iPosition = this.mBuffer.position();
        if (i2 > 32768) {
            throw new IOException("Blob size too large");
        }
        if (i2 + 4 > this.mBuffer.remaining()) {
            ByteBuffer byteBufferAllocate = ByteBuffer.allocate(i2 + 2048);
            byteBufferAllocate.put(this.mBuffer.array(), 0, this.mBuffer.arrayOffset() + this.mBuffer.position());
            this.mBuffer = byteBufferAllocate;
        } else if (this.mBuffer.capacity() > 4096 && i2 < 2048) {
            ByteBuffer byteBufferAllocate2 = ByteBuffer.allocate(2048);
            byteBufferAllocate2.put(this.mBuffer.array(), 0, this.mBuffer.arrayOffset() + this.mBuffer.position());
            this.mBuffer = byteBufferAllocate2;
        }
        read(this.mBuffer, i2);
        this.mCRCBuf.clear();
        read(this.mCRCBuf, 4);
        this.mCRCBuf.position(0);
        int i3 = this.mCRCBuf.getInt();
        this.mChecksumTool.reset();
        this.mChecksumTool.update(this.mBuffer.array(), 0, this.mBuffer.position());
        if (i3 == ((int) this.mChecksumTool.getValue())) {
            byte[] bArr = this.mKey;
            if (bArr != null) {
                RC4Cryption.encrypt(bArr, this.mBuffer.array(), true, iPosition, i2);
            }
            return this.mBuffer;
        }
        MyLog.w("CRC = " + ((int) this.mChecksumTool.getValue()) + " and " + i3);
        throw new IOException("Corrupted Blob bad CRC");
    }

    Blob read() throws IOException {
        int iPosition;
        try {
            ByteBuffer onePacket = readOnePacket();
            iPosition = onePacket.position();
            try {
                onePacket.flip();
                onePacket.position(8);
                Blob ping = iPosition == 8 ? new Ping() : Blob.from(onePacket.slice());
                MyLog.v("[Slim] Read {cmd=" + ping.getCmd() + ";chid=" + ping.getChannelId() + ";len=" + iPosition + "}");
                return ping;
            } catch (IOException e2) {
                e = e2;
                if (iPosition == 0) {
                    iPosition = this.mBuffer.position();
                }
                StringBuilder sb = new StringBuilder();
                sb.append("[Slim] read Blob [");
                byte[] bArrArray = this.mBuffer.array();
                if (iPosition > 128) {
                    iPosition = 128;
                }
                sb.append(DebugUtils.bytes2Hex(bArrArray, 0, iPosition));
                sb.append("] Err:");
                sb.append(e.getMessage());
                MyLog.w(sb.toString());
                throw e;
            }
        } catch (IOException e3) {
            e = e3;
            iPosition = 0;
        }
    }

    void shutdown() {
        this.mDone = true;
    }

    void start() throws IOException {
        try {
            loop();
        } catch (IOException e2) {
            if (!this.mDone) {
                throw e2;
            }
        }
    }

    private void read(ByteBuffer byteBuffer, int i2) throws IOException {
        int iPosition = byteBuffer.position();
        do {
            int i3 = this.mInputStream.read(byteBuffer.array(), iPosition, i2);
            if (i3 == -1) {
                throw new EOFException();
            }
            i2 -= i3;
            iPosition += i3;
        } while (i2 > 0);
        byteBuffer.position(iPosition);
    }
}
