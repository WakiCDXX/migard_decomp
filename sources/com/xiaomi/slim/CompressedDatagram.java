package com.xiaomi.slim;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/* JADX INFO: loaded from: classes.dex */
public class CompressedDatagram {
    public static final byte COMPRESS_ALGORITHM_GZIP = 2;
    public static final byte COMPRESS_ALGORITHM_NONE = 0;
    public static final byte COMPRESS_ALGORITHM_ZSTD = 1;
    public static final byte[] PUSH_PAYLOAD_MAGIC = {80, 85, 83, 72};
    public static final short PUSH_PAYLOAD_VERSION_100 = 1;
    private byte algorithm;
    private byte[] body;
    private int originalLength;
    private short version;

    public static class CompressedDatagramUtils {
        public static final int DEFAULT_COMPRESS_THRESHOLD = 512;
        public static final GZipCompressor GZIP_COMPRESSOR = new GZipCompressor();
        public static final GzipDecompressor GZIP_DECOMPRESSOR = new GzipDecompressor();

        public static byte[] compress(byte[] bArr) {
            return compress(bArr, GZIP_COMPRESSOR);
        }

        public static byte[] decompress(byte[] bArr) {
            return decompress(bArr, GZIP_DECOMPRESSOR);
        }

        public static byte[] compress(byte[] bArr, Compressor compressor) {
            return compress(bArr, compressor, 512);
        }

        public static byte[] decompress(byte[] bArr, Decompressor decompressor) {
            if (!CompressedDatagram.compressedDatagram(bArr)) {
                return bArr;
            }
            CompressedDatagram compressedDatagramDecode = CompressedDatagram.decode(bArr);
            return (compressedDatagramDecode.algorithm == 0 || compressedDatagramDecode.algorithm != decompressor.algorithm()) ? compressedDatagramDecode.body : decompressor.decompress(compressedDatagramDecode.body, compressedDatagramDecode.originalLength);
        }

        public static byte[] compress(byte[] bArr, Compressor compressor, int i2) {
            return (CompressedDatagram.compressedDatagram(bArr) || bArr.length <= i2) ? bArr : CompressedDatagram.encode(CompressedDatagram.build(compressor.algorithm(), bArr.length, compressor.compress(bArr)));
        }
    }

    public interface Compressor {
        byte algorithm();

        byte[] compress(byte[] bArr);
    }

    public interface Decompressor {
        byte algorithm();

        byte[] decompress(byte[] bArr, int i2);
    }

    public static final class GZipCompressor implements Compressor {
        @Override // com.xiaomi.slim.CompressedDatagram.Compressor
        public byte algorithm() {
            return (byte) 2;
        }

        @Override // com.xiaomi.slim.CompressedDatagram.Compressor
        public byte[] compress(byte[] bArr) throws Throwable {
            GZIPOutputStream gZIPOutputStream;
            Throwable th;
            GZIPOutputStream gZIPOutputStream2 = null;
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                gZIPOutputStream = new GZIPOutputStream(byteArrayOutputStream);
                try {
                    gZIPOutputStream.write(bArr);
                    gZIPOutputStream.finish();
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    try {
                        gZIPOutputStream.close();
                    } catch (IOException unused) {
                    }
                    return byteArray;
                } catch (IOException unused2) {
                    gZIPOutputStream2 = gZIPOutputStream;
                    if (gZIPOutputStream2 != null) {
                        try {
                            gZIPOutputStream2.close();
                        } catch (IOException unused3) {
                        }
                    }
                    return bArr;
                } catch (Throwable th2) {
                    th = th2;
                    if (gZIPOutputStream != null) {
                        try {
                            gZIPOutputStream.close();
                        } catch (IOException unused4) {
                        }
                    }
                    throw th;
                }
            } catch (IOException unused5) {
            } catch (Throwable th3) {
                gZIPOutputStream = null;
                th = th3;
            }
        }
    }

    public static final class GzipDecompressor implements Decompressor {
        @Override // com.xiaomi.slim.CompressedDatagram.Decompressor
        public byte algorithm() {
            return (byte) 2;
        }

        @Override // com.xiaomi.slim.CompressedDatagram.Decompressor
        public byte[] decompress(byte[] bArr, int i2) throws Throwable {
            GZIPInputStream gZIPInputStream;
            Throwable th;
            GZIPInputStream gZIPInputStream2 = null;
            try {
                gZIPInputStream = new GZIPInputStream(new ByteArrayInputStream(bArr), i2);
                try {
                    byte[] bArr2 = new byte[i2];
                    gZIPInputStream.read(bArr2);
                    try {
                        gZIPInputStream.close();
                    } catch (IOException unused) {
                    }
                    return bArr2;
                } catch (IOException unused2) {
                    gZIPInputStream2 = gZIPInputStream;
                    if (gZIPInputStream2 != null) {
                        try {
                            gZIPInputStream2.close();
                        } catch (IOException unused3) {
                        }
                    }
                    return bArr;
                } catch (Throwable th2) {
                    th = th2;
                    if (gZIPInputStream != null) {
                        try {
                            gZIPInputStream.close();
                        } catch (IOException unused4) {
                        }
                    }
                    throw th;
                }
            } catch (IOException unused5) {
            } catch (Throwable th3) {
                gZIPInputStream = null;
                th = th3;
            }
        }
    }

    protected CompressedDatagram(byte b2, int i2, byte[] bArr) {
        this((short) 1, b2, i2, bArr);
    }

    public static CompressedDatagram build(byte b2, int i2, byte[] bArr) {
        return new CompressedDatagram(b2, i2, bArr);
    }

    public static boolean compareBytes(byte[] bArr, byte[] bArr2, int i2) {
        if (bArr.length < i2 || bArr2.length < i2) {
            return false;
        }
        for (int i3 = 0; i3 < i2; i3++) {
            if (bArr[i3] != bArr2[i3]) {
                return false;
            }
        }
        return true;
    }

    public static byte compressedAlgorithm(byte[] bArr) {
        return bArr[6];
    }

    public static boolean compressedDatagram(byte[] bArr) {
        byte[] bArr2 = PUSH_PAYLOAD_MAGIC;
        return compareBytes(bArr2, bArr, bArr2.length);
    }

    public static CompressedDatagram decode(byte[] bArr) {
        if (!compressedDatagram(bArr)) {
            return build((byte) 0, bArr.length, bArr);
        }
        ByteBuffer byteBufferOrder = ByteBuffer.wrap(bArr).order(ByteOrder.BIG_ENDIAN);
        byteBufferOrder.getInt();
        short s2 = byteBufferOrder.getShort();
        byte b2 = byteBufferOrder.get();
        int i2 = byteBufferOrder.getInt();
        byte[] bArr2 = new byte[byteBufferOrder.getInt()];
        byteBufferOrder.get(bArr2);
        return build(s2, b2, i2, bArr2);
    }

    public static byte[] encode(CompressedDatagram compressedDatagram) {
        return ByteBuffer.allocate(compressedDatagram.body.length + 15).order(ByteOrder.BIG_ENDIAN).put(PUSH_PAYLOAD_MAGIC).putShort(compressedDatagram.version).put(compressedDatagram.algorithm).putInt(compressedDatagram.originalLength).putInt(compressedDatagram.body.length).put(compressedDatagram.body).array();
    }

    protected CompressedDatagram(short s2, byte b2, int i2, byte[] bArr) {
        this.version = s2;
        this.algorithm = b2;
        this.originalLength = i2;
        this.body = bArr;
    }

    public static CompressedDatagram build(short s2, byte b2, int i2, byte[] bArr) {
        return new CompressedDatagram(s2, b2, i2, bArr);
    }
}
