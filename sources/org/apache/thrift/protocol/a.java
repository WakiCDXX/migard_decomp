package org.apache.thrift.protocol;

/* JADX INFO: compiled from: TField.java */
/* JADX INFO: loaded from: classes.dex */
public class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f2149a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final byte f2150b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final short f2151c;

    public a(String str, byte b2, short s2) {
        this.f2149a = str;
        this.f2150b = b2;
        this.f2151c = s2;
    }

    public String toString() {
        return "<TField name:'" + this.f2149a + "' type:" + ((int) this.f2150b) + " field-id:" + ((int) this.f2151c) + ">";
    }
}
