package com.google.protobuf.micro;

import com.miui.powerkeeper.powerchecker.BatteryStatsHelper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

/* JADX INFO: loaded from: classes.dex */
public final class CodedInputStreamMicro {
    private static final int BUFFER_SIZE = 4096;
    private static final int DEFAULT_RECURSION_LIMIT = 64;
    private static final int DEFAULT_SIZE_LIMIT = 67108864;
    private final byte[] buffer;
    private int bufferPos;
    private int bufferSize;
    private int bufferSizeAfterLimit;
    private int currentLimit;
    private final InputStream input;
    private int lastTag;
    private int recursionDepth;
    private int recursionLimit;
    private int sizeLimit;
    private int totalBytesRetired;

    private CodedInputStreamMicro(byte[] bArr, int i2, int i3) {
        this.currentLimit = Integer.MAX_VALUE;
        this.recursionLimit = 64;
        this.sizeLimit = DEFAULT_SIZE_LIMIT;
        this.buffer = bArr;
        this.bufferSize = i3 + i2;
        this.bufferPos = i2;
        this.input = null;
    }

    public static int decodeZigZag32(int i2) {
        return (-(i2 & 1)) ^ (i2 >>> 1);
    }

    public static long decodeZigZag64(long j2) {
        return (-(j2 & 1)) ^ (j2 >>> 1);
    }

    public static CodedInputStreamMicro newInstance(InputStream inputStream) {
        return new CodedInputStreamMicro(inputStream);
    }

    private void recomputeBufferSizeAfterLimit() {
        int i2 = this.bufferSize + this.bufferSizeAfterLimit;
        this.bufferSize = i2;
        int i3 = this.totalBytesRetired + i2;
        int i4 = this.currentLimit;
        if (i3 <= i4) {
            this.bufferSizeAfterLimit = 0;
            return;
        }
        int i5 = i3 - i4;
        this.bufferSizeAfterLimit = i5;
        this.bufferSize = i2 - i5;
    }

    private boolean refillBuffer(boolean z2) throws IOException {
        int i2 = this.bufferPos;
        int i3 = this.bufferSize;
        if (i2 < i3) {
            throw new IllegalStateException("refillBuffer() called when buffer wasn't empty.");
        }
        int i4 = this.totalBytesRetired;
        if (i4 + i3 == this.currentLimit) {
            if (z2) {
                throw InvalidProtocolBufferMicroException.truncatedMessage();
            }
            return false;
        }
        this.totalBytesRetired = i4 + i3;
        this.bufferPos = 0;
        InputStream inputStream = this.input;
        int i5 = inputStream == null ? -1 : inputStream.read(this.buffer);
        this.bufferSize = i5;
        if (i5 == 0 || i5 < -1) {
            throw new IllegalStateException("InputStream#read(byte[]) returned invalid result: " + this.bufferSize + "\nThe InputStream implementation is buggy.");
        }
        if (i5 == -1) {
            this.bufferSize = 0;
            if (z2) {
                throw InvalidProtocolBufferMicroException.truncatedMessage();
            }
            return false;
        }
        recomputeBufferSizeAfterLimit();
        int i6 = this.totalBytesRetired + this.bufferSize + this.bufferSizeAfterLimit;
        if (i6 > this.sizeLimit || i6 < 0) {
            throw InvalidProtocolBufferMicroException.sizeLimitExceeded();
        }
        return true;
    }

    public void checkLastTagWas(int i2) throws InvalidProtocolBufferMicroException {
        if (this.lastTag != i2) {
            throw InvalidProtocolBufferMicroException.invalidEndTag();
        }
    }

    public int getBytesUntilLimit() {
        int i2 = this.currentLimit;
        if (i2 == Integer.MAX_VALUE) {
            return -1;
        }
        return i2 - (this.totalBytesRetired + this.bufferPos);
    }

    public boolean isAtEnd() throws IOException {
        return this.bufferPos == this.bufferSize && !refillBuffer(false);
    }

    public void popLimit(int i2) {
        this.currentLimit = i2;
        recomputeBufferSizeAfterLimit();
    }

    public int pushLimit(int i2) throws InvalidProtocolBufferMicroException {
        if (i2 < 0) {
            throw InvalidProtocolBufferMicroException.negativeSize();
        }
        int i3 = i2 + this.totalBytesRetired + this.bufferPos;
        int i4 = this.currentLimit;
        if (i3 > i4) {
            throw InvalidProtocolBufferMicroException.truncatedMessage();
        }
        this.currentLimit = i3;
        recomputeBufferSizeAfterLimit();
        return i4;
    }

    public boolean readBool() throws IOException {
        return readRawVarint32() != 0;
    }

    public ByteStringMicro readBytes() throws IOException {
        int rawVarint32 = readRawVarint32();
        int i2 = this.bufferSize;
        int i3 = this.bufferPos;
        if (rawVarint32 > i2 - i3 || rawVarint32 <= 0) {
            return ByteStringMicro.copyFrom(readRawBytes(rawVarint32));
        }
        ByteStringMicro byteStringMicroCopyFrom = ByteStringMicro.copyFrom(this.buffer, i3, rawVarint32);
        this.bufferPos += rawVarint32;
        return byteStringMicroCopyFrom;
    }

    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readRawLittleEndian64());
    }

    public int readEnum() throws IOException {
        return readRawVarint32();
    }

    public int readFixed32() throws IOException {
        return readRawLittleEndian32();
    }

    public long readFixed64() throws IOException {
        return readRawLittleEndian64();
    }

    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readRawLittleEndian32());
    }

    public void readGroup(MessageMicro messageMicro, int i2) throws IOException {
        int i3 = this.recursionDepth;
        if (i3 >= this.recursionLimit) {
            throw InvalidProtocolBufferMicroException.recursionLimitExceeded();
        }
        this.recursionDepth = i3 + 1;
        messageMicro.mergeFrom(this);
        checkLastTagWas(WireFormatMicro.makeTag(i2, 4));
        this.recursionDepth--;
    }

    public int readInt32() throws IOException {
        return readRawVarint32();
    }

    public long readInt64() throws IOException {
        return readRawVarint64();
    }

    public void readMessage(MessageMicro messageMicro) throws IOException {
        int rawVarint32 = readRawVarint32();
        if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferMicroException.recursionLimitExceeded();
        }
        int iPushLimit = pushLimit(rawVarint32);
        this.recursionDepth++;
        messageMicro.mergeFrom(this);
        checkLastTagWas(0);
        this.recursionDepth--;
        popLimit(iPushLimit);
    }

    public byte readRawByte() throws IOException {
        if (this.bufferPos == this.bufferSize) {
            refillBuffer(true);
        }
        byte[] bArr = this.buffer;
        int i2 = this.bufferPos;
        this.bufferPos = i2 + 1;
        return bArr[i2];
    }

    public byte[] readRawBytes(int i2) throws IOException {
        if (i2 < 0) {
            throw InvalidProtocolBufferMicroException.negativeSize();
        }
        int i3 = this.totalBytesRetired;
        int i4 = this.bufferPos;
        int i5 = i3 + i4 + i2;
        int i6 = this.currentLimit;
        if (i5 > i6) {
            skipRawBytes((i6 - i3) - i4);
            throw InvalidProtocolBufferMicroException.truncatedMessage();
        }
        int i7 = this.bufferSize;
        if (i2 <= i7 - i4) {
            byte[] bArr = new byte[i2];
            System.arraycopy(this.buffer, i4, bArr, 0, i2);
            this.bufferPos += i2;
            return bArr;
        }
        if (i2 >= 4096) {
            this.totalBytesRetired = i3 + i7;
            this.bufferPos = 0;
            this.bufferSize = 0;
            int length = i7 - i4;
            int i8 = i2 - length;
            Vector vector = new Vector();
            while (i8 > 0) {
                int iMin = Math.min(i8, 4096);
                byte[] bArr2 = new byte[iMin];
                int i9 = 0;
                while (i9 < iMin) {
                    InputStream inputStream = this.input;
                    int i10 = inputStream == null ? -1 : inputStream.read(bArr2, i9, iMin - i9);
                    if (i10 == -1) {
                        throw InvalidProtocolBufferMicroException.truncatedMessage();
                    }
                    this.totalBytesRetired += i10;
                    i9 += i10;
                }
                i8 -= iMin;
                vector.addElement(bArr2);
            }
            byte[] bArr3 = new byte[i2];
            System.arraycopy(this.buffer, i4, bArr3, 0, length);
            for (int i11 = 0; i11 < vector.size(); i11++) {
                byte[] bArr4 = (byte[]) vector.elementAt(i11);
                System.arraycopy(bArr4, 0, bArr3, length, bArr4.length);
                length += bArr4.length;
            }
            return bArr3;
        }
        byte[] bArr5 = new byte[i2];
        int i12 = i7 - i4;
        System.arraycopy(this.buffer, i4, bArr5, 0, i12);
        this.bufferPos = this.bufferSize;
        refillBuffer(true);
        while (true) {
            int i13 = i2 - i12;
            int i14 = this.bufferSize;
            if (i13 <= i14) {
                System.arraycopy(this.buffer, 0, bArr5, i12, i13);
                this.bufferPos = i13;
                return bArr5;
            }
            System.arraycopy(this.buffer, 0, bArr5, i12, i14);
            int i15 = this.bufferSize;
            i12 += i15;
            this.bufferPos = i15;
            refillBuffer(true);
        }
    }

    public int readRawLittleEndian32() throws IOException {
        byte rawByte = readRawByte();
        byte rawByte2 = readRawByte();
        byte rawByte3 = readRawByte();
        byte rawByte4 = readRawByte();
        return ((rawByte4 & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL) << 24) | (rawByte & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL) | ((rawByte2 & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL) << 8) | ((rawByte3 & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL) << 16);
    }

    public long readRawLittleEndian64() throws IOException {
        byte rawByte = readRawByte();
        return ((((long) readRawByte()) & 255) << 8) | (((long) rawByte) & 255) | ((((long) readRawByte()) & 255) << 16) | ((((long) readRawByte()) & 255) << 24) | ((((long) readRawByte()) & 255) << 32) | ((((long) readRawByte()) & 255) << 40) | ((((long) readRawByte()) & 255) << 48) | ((((long) readRawByte()) & 255) << 56);
    }

    public int readRawVarint32() throws IOException {
        int i2;
        byte rawByte = readRawByte();
        if (rawByte >= 0) {
            return rawByte;
        }
        int i3 = rawByte & 127;
        byte rawByte2 = readRawByte();
        if (rawByte2 >= 0) {
            i2 = rawByte2 << 7;
        } else {
            i3 |= (rawByte2 & 127) << 7;
            byte rawByte3 = readRawByte();
            if (rawByte3 >= 0) {
                i2 = rawByte3 << 14;
            } else {
                i3 |= (rawByte3 & 127) << 14;
                byte rawByte4 = readRawByte();
                if (rawByte4 < 0) {
                    int i4 = i3 | ((rawByte4 & 127) << 21);
                    byte rawByte5 = readRawByte();
                    int i5 = i4 | (rawByte5 << 28);
                    if (rawByte5 >= 0) {
                        return i5;
                    }
                    for (int i6 = 0; i6 < 5; i6++) {
                        if (readRawByte() >= 0) {
                            return i5;
                        }
                    }
                    throw InvalidProtocolBufferMicroException.malformedVarint();
                }
                i2 = rawByte4 << 21;
            }
        }
        return i2 | i3;
    }

    public long readRawVarint64() throws IOException {
        long j2 = 0;
        for (int i2 = 0; i2 < 64; i2 += 7) {
            byte rawByte = readRawByte();
            j2 |= ((long) (rawByte & 127)) << i2;
            if ((rawByte & 128) == 0) {
                return j2;
            }
        }
        throw InvalidProtocolBufferMicroException.malformedVarint();
    }

    public int readSFixed32() throws IOException {
        return readRawLittleEndian32();
    }

    public long readSFixed64() throws IOException {
        return readRawLittleEndian64();
    }

    public int readSInt32() throws IOException {
        return decodeZigZag32(readRawVarint32());
    }

    public long readSInt64() throws IOException {
        return decodeZigZag64(readRawVarint64());
    }

    public String readString() throws IOException {
        int rawVarint32 = readRawVarint32();
        int i2 = this.bufferSize;
        int i3 = this.bufferPos;
        if (rawVarint32 > i2 - i3 || rawVarint32 <= 0) {
            return new String(readRawBytes(rawVarint32), "UTF-8");
        }
        String str = new String(this.buffer, i3, rawVarint32, "UTF-8");
        this.bufferPos += rawVarint32;
        return str;
    }

    public int readTag() throws IOException {
        if (isAtEnd()) {
            this.lastTag = 0;
            return 0;
        }
        int rawVarint32 = readRawVarint32();
        this.lastTag = rawVarint32;
        if (rawVarint32 != 0) {
            return rawVarint32;
        }
        throw InvalidProtocolBufferMicroException.invalidTag();
    }

    public int readUInt32() throws IOException {
        return readRawVarint32();
    }

    public long readUInt64() throws IOException {
        return readRawVarint64();
    }

    public void resetSizeCounter() {
        this.totalBytesRetired = 0;
    }

    public int setRecursionLimit(int i2) {
        if (i2 >= 0) {
            int i3 = this.recursionLimit;
            this.recursionLimit = i2;
            return i3;
        }
        throw new IllegalArgumentException("Recursion limit cannot be negative: " + i2);
    }

    public int setSizeLimit(int i2) {
        if (i2 >= 0) {
            int i3 = this.sizeLimit;
            this.sizeLimit = i2;
            return i3;
        }
        throw new IllegalArgumentException("Size limit cannot be negative: " + i2);
    }

    public boolean skipField(int i2) throws IOException {
        int tagWireType = WireFormatMicro.getTagWireType(i2);
        if (tagWireType == 0) {
            readInt32();
            return true;
        }
        if (tagWireType == 1) {
            readRawLittleEndian64();
            return true;
        }
        if (tagWireType == 2) {
            skipRawBytes(readRawVarint32());
            return true;
        }
        if (tagWireType == 3) {
            skipMessage();
            checkLastTagWas(WireFormatMicro.makeTag(WireFormatMicro.getTagFieldNumber(i2), 4));
            return true;
        }
        if (tagWireType == 4) {
            return false;
        }
        if (tagWireType != 5) {
            throw InvalidProtocolBufferMicroException.invalidWireType();
        }
        readRawLittleEndian32();
        return true;
    }

    public void skipMessage() throws IOException {
        int tag;
        do {
            tag = readTag();
            if (tag == 0) {
                return;
            }
        } while (skipField(tag));
    }

    public void skipRawBytes(int i2) throws IOException {
        if (i2 < 0) {
            throw InvalidProtocolBufferMicroException.negativeSize();
        }
        int i3 = this.totalBytesRetired;
        int i4 = this.bufferPos;
        int i5 = i3 + i4 + i2;
        int i6 = this.currentLimit;
        if (i5 > i6) {
            skipRawBytes((i6 - i3) - i4);
            throw InvalidProtocolBufferMicroException.truncatedMessage();
        }
        int i7 = this.bufferSize;
        if (i2 <= i7 - i4) {
            this.bufferPos = i4 + i2;
            return;
        }
        int i8 = i7 - i4;
        this.totalBytesRetired = i3 + i7;
        this.bufferPos = 0;
        this.bufferSize = 0;
        while (i8 < i2) {
            InputStream inputStream = this.input;
            int iSkip = inputStream == null ? -1 : (int) inputStream.skip(i2 - i8);
            if (iSkip <= 0) {
                throw InvalidProtocolBufferMicroException.truncatedMessage();
            }
            i8 += iSkip;
            this.totalBytesRetired += iSkip;
        }
    }

    public static CodedInputStreamMicro newInstance(byte[] bArr) {
        return newInstance(bArr, 0, bArr.length);
    }

    public static CodedInputStreamMicro newInstance(byte[] bArr, int i2, int i3) {
        return new CodedInputStreamMicro(bArr, i2, i3);
    }

    static int readRawVarint32(InputStream inputStream) throws IOException {
        int i2 = 0;
        int i3 = 0;
        while (i2 < 32) {
            int i4 = inputStream.read();
            if (i4 == -1) {
                throw InvalidProtocolBufferMicroException.truncatedMessage();
            }
            i3 |= (i4 & 127) << i2;
            if ((i4 & 128) == 0) {
                return i3;
            }
            i2 += 7;
        }
        while (i2 < 64) {
            int i5 = inputStream.read();
            if (i5 == -1) {
                throw InvalidProtocolBufferMicroException.truncatedMessage();
            }
            if ((i5 & 128) == 0) {
                return i3;
            }
            i2 += 7;
        }
        throw InvalidProtocolBufferMicroException.malformedVarint();
    }

    private CodedInputStreamMicro(InputStream inputStream) {
        this.currentLimit = Integer.MAX_VALUE;
        this.recursionLimit = 64;
        this.sizeLimit = DEFAULT_SIZE_LIMIT;
        this.buffer = new byte[4096];
        this.bufferSize = 0;
        this.bufferPos = 0;
        this.input = inputStream;
    }
}
