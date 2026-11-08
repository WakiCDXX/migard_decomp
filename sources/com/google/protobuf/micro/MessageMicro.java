package com.google.protobuf.micro;

import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public abstract class MessageMicro {
    public abstract int getCachedSize();

    public abstract int getSerializedSize();

    public abstract MessageMicro mergeFrom(CodedInputStreamMicro codedInputStreamMicro) throws IOException;

    public MessageMicro mergeFrom(byte[] bArr) throws InvalidProtocolBufferMicroException {
        return mergeFrom(bArr, 0, bArr.length);
    }

    protected boolean parseUnknownField(CodedInputStreamMicro codedInputStreamMicro, int i2) throws IOException {
        return codedInputStreamMicro.skipField(i2);
    }

    public byte[] toByteArray() {
        int serializedSize = getSerializedSize();
        byte[] bArr = new byte[serializedSize];
        toByteArray(bArr, 0, serializedSize);
        return bArr;
    }

    public abstract void writeTo(CodedOutputStreamMicro codedOutputStreamMicro) throws IOException;

    public MessageMicro mergeFrom(byte[] bArr, int i2, int i3) throws InvalidProtocolBufferMicroException {
        try {
            CodedInputStreamMicro codedInputStreamMicroNewInstance = CodedInputStreamMicro.newInstance(bArr, i2, i3);
            mergeFrom(codedInputStreamMicroNewInstance);
            codedInputStreamMicroNewInstance.checkLastTagWas(0);
            return this;
        } catch (InvalidProtocolBufferMicroException e2) {
            throw e2;
        } catch (IOException unused) {
            throw new RuntimeException("Reading from a byte array threw an IOException (should never happen).");
        }
    }

    public void toByteArray(byte[] bArr, int i2, int i3) {
        try {
            CodedOutputStreamMicro codedOutputStreamMicroNewInstance = CodedOutputStreamMicro.newInstance(bArr, i2, i3);
            writeTo(codedOutputStreamMicroNewInstance);
            codedOutputStreamMicroNewInstance.checkNoSpaceLeft();
        } catch (IOException unused) {
            throw new RuntimeException("Serializing to a byte array threw an IOException (should never happen).");
        }
    }
}
