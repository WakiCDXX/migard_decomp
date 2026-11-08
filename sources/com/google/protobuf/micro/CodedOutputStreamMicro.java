package com.google.protobuf.micro;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/* JADX INFO: loaded from: classes.dex */
public final class CodedOutputStreamMicro {
    public static final int DEFAULT_BUFFER_SIZE = 4096;
    public static final int LITTLE_ENDIAN_32_SIZE = 4;
    public static final int LITTLE_ENDIAN_64_SIZE = 8;
    private final byte[] buffer;
    private final int limit;
    private final OutputStream output;
    private int position;

    public static class OutOfSpaceException extends IOException {
        private static final long serialVersionUID = -6947486886997889499L;

        OutOfSpaceException() {
            super("CodedOutputStream was writing to a flat byte array and ran out of space.");
        }
    }

    private CodedOutputStreamMicro(byte[] bArr, int i2, int i3) {
        this.output = null;
        this.buffer = bArr;
        this.position = i2;
        this.limit = i2 + i3;
    }

    public static int computeBoolSize(int i2, boolean z2) {
        return computeTagSize(i2) + computeBoolSizeNoTag(z2);
    }

    public static int computeBoolSizeNoTag(boolean z2) {
        return 1;
    }

    public static int computeByteArraySize(int i2, byte[] bArr) {
        return computeTagSize(i2) + computeByteArraySizeNoTag(bArr);
    }

    public static int computeByteArraySizeNoTag(byte[] bArr) {
        return computeRawVarint32Size(bArr.length) + bArr.length;
    }

    public static int computeBytesSize(int i2, ByteStringMicro byteStringMicro) {
        return computeTagSize(i2) + computeBytesSizeNoTag(byteStringMicro);
    }

    public static int computeBytesSizeNoTag(ByteStringMicro byteStringMicro) {
        return computeRawVarint32Size(byteStringMicro.size()) + byteStringMicro.size();
    }

    public static int computeDoubleSize(int i2, double d2) {
        return computeTagSize(i2) + computeDoubleSizeNoTag(d2);
    }

    public static int computeDoubleSizeNoTag(double d2) {
        return 8;
    }

    public static int computeEnumSize(int i2, int i3) {
        return computeTagSize(i2) + computeEnumSizeNoTag(i3);
    }

    public static int computeEnumSizeNoTag(int i2) {
        return computeRawVarint32Size(i2);
    }

    public static int computeFixed32Size(int i2, int i3) {
        return computeTagSize(i2) + computeFixed32SizeNoTag(i3);
    }

    public static int computeFixed32SizeNoTag(int i2) {
        return 4;
    }

    public static int computeFixed64Size(int i2, long j2) {
        return computeTagSize(i2) + computeFixed64SizeNoTag(j2);
    }

    public static int computeFixed64SizeNoTag(long j2) {
        return 8;
    }

    public static int computeFloatSize(int i2, float f2) {
        return computeTagSize(i2) + computeFloatSizeNoTag(f2);
    }

    public static int computeFloatSizeNoTag(float f2) {
        return 4;
    }

    public static int computeGroupSize(int i2, MessageMicro messageMicro) {
        return (computeTagSize(i2) * 2) + computeGroupSizeNoTag(messageMicro);
    }

    public static int computeGroupSizeNoTag(MessageMicro messageMicro) {
        return messageMicro.getSerializedSize();
    }

    public static int computeInt32Size(int i2, int i3) {
        return computeTagSize(i2) + computeInt32SizeNoTag(i3);
    }

    public static int computeInt32SizeNoTag(int i2) {
        if (i2 >= 0) {
            return computeRawVarint32Size(i2);
        }
        return 10;
    }

    public static int computeInt64Size(int i2, long j2) {
        return computeTagSize(i2) + computeInt64SizeNoTag(j2);
    }

    public static int computeInt64SizeNoTag(long j2) {
        return computeRawVarint64Size(j2);
    }

    public static int computeMessageSize(int i2, MessageMicro messageMicro) {
        return computeTagSize(i2) + computeMessageSizeNoTag(messageMicro);
    }

    public static int computeMessageSizeNoTag(MessageMicro messageMicro) {
        int serializedSize = messageMicro.getSerializedSize();
        return computeRawVarint32Size(serializedSize) + serializedSize;
    }

    public static int computeRawVarint32Size(int i2) {
        if ((i2 & (-128)) == 0) {
            return 1;
        }
        if ((i2 & (-16384)) == 0) {
            return 2;
        }
        if (((-2097152) & i2) == 0) {
            return 3;
        }
        return (i2 & (-268435456)) == 0 ? 4 : 5;
    }

    public static int computeRawVarint64Size(long j2) {
        if (((-128) & j2) == 0) {
            return 1;
        }
        if (((-16384) & j2) == 0) {
            return 2;
        }
        if (((-2097152) & j2) == 0) {
            return 3;
        }
        if (((-268435456) & j2) == 0) {
            return 4;
        }
        if (((-34359738368L) & j2) == 0) {
            return 5;
        }
        if (((-4398046511104L) & j2) == 0) {
            return 6;
        }
        if (((-562949953421312L) & j2) == 0) {
            return 7;
        }
        if (((-72057594037927936L) & j2) == 0) {
            return 8;
        }
        return (j2 & Long.MIN_VALUE) == 0 ? 9 : 10;
    }

    public static int computeSFixed32Size(int i2, int i3) {
        return computeTagSize(i2) + computeSFixed32SizeNoTag(i3);
    }

    public static int computeSFixed32SizeNoTag(int i2) {
        return 4;
    }

    public static int computeSFixed64Size(int i2, long j2) {
        return computeTagSize(i2) + computeSFixed64SizeNoTag(j2);
    }

    public static int computeSFixed64SizeNoTag(long j2) {
        return 8;
    }

    public static int computeSInt32Size(int i2, int i3) {
        return computeTagSize(i2) + computeSInt32SizeNoTag(i3);
    }

    public static int computeSInt32SizeNoTag(int i2) {
        return computeRawVarint32Size(encodeZigZag32(i2));
    }

    public static int computeSInt64Size(int i2, long j2) {
        return computeTagSize(i2) + computeSInt64SizeNoTag(j2);
    }

    public static int computeSInt64SizeNoTag(long j2) {
        return computeRawVarint64Size(encodeZigZag64(j2));
    }

    public static int computeStringSize(int i2, String str) {
        return computeTagSize(i2) + computeStringSizeNoTag(str);
    }

    public static int computeStringSizeNoTag(String str) {
        try {
            byte[] bytes = str.getBytes("UTF-8");
            return computeRawVarint32Size(bytes.length) + bytes.length;
        } catch (UnsupportedEncodingException unused) {
            throw new RuntimeException("UTF-8 not supported.");
        }
    }

    public static int computeTagSize(int i2) {
        return computeRawVarint32Size(WireFormatMicro.makeTag(i2, 0));
    }

    public static int computeUInt32Size(int i2, int i3) {
        return computeTagSize(i2) + computeUInt32SizeNoTag(i3);
    }

    public static int computeUInt32SizeNoTag(int i2) {
        return computeRawVarint32Size(i2);
    }

    public static int computeUInt64Size(int i2, long j2) {
        return computeTagSize(i2) + computeUInt64SizeNoTag(j2);
    }

    public static int computeUInt64SizeNoTag(long j2) {
        return computeRawVarint64Size(j2);
    }

    public static int encodeZigZag32(int i2) {
        return (i2 >> 31) ^ (i2 << 1);
    }

    public static long encodeZigZag64(long j2) {
        return (j2 >> 63) ^ (j2 << 1);
    }

    public static CodedOutputStreamMicro newInstance(OutputStream outputStream) {
        return newInstance(outputStream, 4096);
    }

    private void refreshBuffer() throws IOException {
        OutputStream outputStream = this.output;
        if (outputStream == null) {
            throw new OutOfSpaceException();
        }
        outputStream.write(this.buffer, 0, this.position);
        this.position = 0;
    }

    public void checkNoSpaceLeft() {
        if (spaceLeft() != 0) {
            throw new IllegalStateException("Did not write as much data as expected.");
        }
    }

    public void flush() throws IOException {
        if (this.output != null) {
            refreshBuffer();
        }
    }

    public int spaceLeft() {
        if (this.output == null) {
            return this.limit - this.position;
        }
        throw new UnsupportedOperationException("spaceLeft() can only be called on CodedOutputStreams that are writing to a flat array.");
    }

    public void writeBool(int i2, boolean z2) throws IOException {
        writeTag(i2, 0);
        writeBoolNoTag(z2);
    }

    public void writeBoolNoTag(boolean z2) throws IOException {
        writeRawByte(z2 ? 1 : 0);
    }

    public void writeByteArray(int i2, byte[] bArr) throws IOException {
        writeTag(i2, 2);
        writeByteArrayNoTag(bArr);
    }

    public void writeByteArrayNoTag(byte[] bArr) throws IOException {
        writeRawVarint32(bArr.length);
        writeRawBytes(bArr);
    }

    public void writeBytes(int i2, ByteStringMicro byteStringMicro) throws IOException {
        writeTag(i2, 2);
        writeBytesNoTag(byteStringMicro);
    }

    public void writeBytesNoTag(ByteStringMicro byteStringMicro) throws IOException {
        byte[] byteArray = byteStringMicro.toByteArray();
        writeRawVarint32(byteArray.length);
        writeRawBytes(byteArray);
    }

    public void writeDouble(int i2, double d2) throws IOException {
        writeTag(i2, 1);
        writeDoubleNoTag(d2);
    }

    public void writeDoubleNoTag(double d2) throws IOException {
        writeRawLittleEndian64(Double.doubleToLongBits(d2));
    }

    public void writeEnum(int i2, int i3) throws IOException {
        writeTag(i2, 0);
        writeEnumNoTag(i3);
    }

    public void writeEnumNoTag(int i2) throws IOException {
        writeRawVarint32(i2);
    }

    public void writeFixed32(int i2, int i3) throws IOException {
        writeTag(i2, 5);
        writeFixed32NoTag(i3);
    }

    public void writeFixed32NoTag(int i2) throws IOException {
        writeRawLittleEndian32(i2);
    }

    public void writeFixed64(int i2, long j2) throws IOException {
        writeTag(i2, 1);
        writeFixed64NoTag(j2);
    }

    public void writeFixed64NoTag(long j2) throws IOException {
        writeRawLittleEndian64(j2);
    }

    public void writeFloat(int i2, float f2) throws IOException {
        writeTag(i2, 5);
        writeFloatNoTag(f2);
    }

    public void writeFloatNoTag(float f2) throws IOException {
        writeRawLittleEndian32(Float.floatToIntBits(f2));
    }

    public void writeGroup(int i2, MessageMicro messageMicro) throws IOException {
        writeTag(i2, 3);
        writeGroupNoTag(messageMicro);
        writeTag(i2, 4);
    }

    public void writeGroupNoTag(MessageMicro messageMicro) throws IOException {
        messageMicro.writeTo(this);
    }

    public void writeInt32(int i2, int i3) throws IOException {
        writeTag(i2, 0);
        writeInt32NoTag(i3);
    }

    public void writeInt32NoTag(int i2) throws IOException {
        if (i2 >= 0) {
            writeRawVarint32(i2);
        } else {
            writeRawVarint64(i2);
        }
    }

    public void writeInt64(int i2, long j2) throws IOException {
        writeTag(i2, 0);
        writeInt64NoTag(j2);
    }

    public void writeInt64NoTag(long j2) throws IOException {
        writeRawVarint64(j2);
    }

    public void writeMessage(int i2, MessageMicro messageMicro) throws IOException {
        writeTag(i2, 2);
        writeMessageNoTag(messageMicro);
    }

    public void writeMessageNoTag(MessageMicro messageMicro) throws IOException {
        writeRawVarint32(messageMicro.getCachedSize());
        messageMicro.writeTo(this);
    }

    public void writeRawByte(byte b2) throws IOException {
        if (this.position == this.limit) {
            refreshBuffer();
        }
        byte[] bArr = this.buffer;
        int i2 = this.position;
        this.position = i2 + 1;
        bArr[i2] = b2;
    }

    public void writeRawBytes(byte[] bArr) throws IOException {
        writeRawBytes(bArr, 0, bArr.length);
    }

    public void writeRawLittleEndian32(int i2) throws IOException {
        writeRawByte(i2 & 255);
        writeRawByte((i2 >> 8) & 255);
        writeRawByte((i2 >> 16) & 255);
        writeRawByte((i2 >> 24) & 255);
    }

    public void writeRawLittleEndian64(long j2) throws IOException {
        writeRawByte(((int) j2) & 255);
        writeRawByte(((int) (j2 >> 8)) & 255);
        writeRawByte(((int) (j2 >> 16)) & 255);
        writeRawByte(((int) (j2 >> 24)) & 255);
        writeRawByte(((int) (j2 >> 32)) & 255);
        writeRawByte(((int) (j2 >> 40)) & 255);
        writeRawByte(((int) (j2 >> 48)) & 255);
        writeRawByte(((int) (j2 >> 56)) & 255);
    }

    public void writeRawVarint32(int i2) throws IOException {
        while ((i2 & (-128)) != 0) {
            writeRawByte((i2 & 127) | 128);
            i2 >>>= 7;
        }
        writeRawByte(i2);
    }

    public void writeRawVarint64(long j2) throws IOException {
        while (((-128) & j2) != 0) {
            writeRawByte((((int) j2) & 127) | 128);
            j2 >>>= 7;
        }
        writeRawByte((int) j2);
    }

    public void writeSFixed32(int i2, int i3) throws IOException {
        writeTag(i2, 5);
        writeSFixed32NoTag(i3);
    }

    public void writeSFixed32NoTag(int i2) throws IOException {
        writeRawLittleEndian32(i2);
    }

    public void writeSFixed64(int i2, long j2) throws IOException {
        writeTag(i2, 1);
        writeSFixed64NoTag(j2);
    }

    public void writeSFixed64NoTag(long j2) throws IOException {
        writeRawLittleEndian64(j2);
    }

    public void writeSInt32(int i2, int i3) throws IOException {
        writeTag(i2, 0);
        writeSInt32NoTag(i3);
    }

    public void writeSInt32NoTag(int i2) throws IOException {
        writeRawVarint32(encodeZigZag32(i2));
    }

    public void writeSInt64(int i2, long j2) throws IOException {
        writeTag(i2, 0);
        writeSInt64NoTag(j2);
    }

    public void writeSInt64NoTag(long j2) throws IOException {
        writeRawVarint64(encodeZigZag64(j2));
    }

    public void writeString(int i2, String str) throws IOException {
        writeTag(i2, 2);
        writeStringNoTag(str);
    }

    public void writeStringNoTag(String str) throws IOException {
        byte[] bytes = str.getBytes("UTF-8");
        writeRawVarint32(bytes.length);
        writeRawBytes(bytes);
    }

    public void writeTag(int i2, int i3) throws IOException {
        writeRawVarint32(WireFormatMicro.makeTag(i2, i3));
    }

    public void writeUInt32(int i2, int i3) throws IOException {
        writeTag(i2, 0);
        writeUInt32NoTag(i3);
    }

    public void writeUInt32NoTag(int i2) throws IOException {
        writeRawVarint32(i2);
    }

    public void writeUInt64(int i2, long j2) throws IOException {
        writeTag(i2, 0);
        writeUInt64NoTag(j2);
    }

    public void writeUInt64NoTag(long j2) throws IOException {
        writeRawVarint64(j2);
    }

    public static CodedOutputStreamMicro newInstance(OutputStream outputStream, int i2) {
        return new CodedOutputStreamMicro(outputStream, new byte[i2]);
    }

    public void writeRawBytes(byte[] bArr, int i2, int i3) throws IOException {
        int i4 = this.limit;
        int i5 = this.position;
        if (i4 - i5 >= i3) {
            System.arraycopy(bArr, i2, this.buffer, i5, i3);
            this.position += i3;
            return;
        }
        int i6 = i4 - i5;
        System.arraycopy(bArr, i2, this.buffer, i5, i6);
        int i7 = i2 + i6;
        int i8 = i3 - i6;
        this.position = this.limit;
        refreshBuffer();
        if (i8 > this.limit) {
            this.output.write(bArr, i7, i8);
        } else {
            System.arraycopy(bArr, i7, this.buffer, 0, i8);
            this.position = i8;
        }
    }

    public static CodedOutputStreamMicro newInstance(byte[] bArr) {
        return newInstance(bArr, 0, bArr.length);
    }

    public static CodedOutputStreamMicro newInstance(byte[] bArr, int i2, int i3) {
        return new CodedOutputStreamMicro(bArr, i2, i3);
    }

    public void writeRawByte(int i2) throws IOException {
        writeRawByte((byte) i2);
    }

    private CodedOutputStreamMicro(OutputStream outputStream, byte[] bArr) {
        this.output = outputStream;
        this.buffer = bArr;
        this.position = 0;
        this.limit = bArr.length;
    }
}
