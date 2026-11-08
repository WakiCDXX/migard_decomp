package org.apache.thrift.protocol;

import org.apache.thrift.TException;

/* JADX INFO: compiled from: TProtocolUtil.java */
/* JADX INFO: loaded from: classes.dex */
public class e {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static int f2158a = Integer.MAX_VALUE;

    public static void a(d dVar, byte b2) throws TException {
        b(dVar, b2, f2158a);
    }

    public static void b(d dVar, byte b2, int i2) throws TException {
        if (i2 <= 0) {
            throw new TException("Maximum skip depth exceeded");
        }
        int i3 = 0;
        switch (b2) {
            case 2:
                dVar.b();
                return;
            case 3:
                dVar.c();
                return;
            case 4:
                dVar.d();
                return;
            case 5:
            case 7:
            case 9:
            default:
                return;
            case 6:
                dVar.g();
                return;
            case 8:
                dVar.h();
                return;
            case 10:
                dVar.i();
                return;
            case 11:
                dVar.a();
                return;
            case 12:
                dVar.q();
                while (true) {
                    byte b3 = dVar.e().f2150b;
                    if (b3 == 0) {
                        dVar.r();
                        return;
                    } else {
                        b(dVar, b3, i2 - 1);
                        dVar.f();
                    }
                }
                break;
            case 13:
                c cVarL = dVar.l();
                while (i3 < cVarL.f2156c) {
                    int i4 = i2 - 1;
                    b(dVar, cVarL.f2154a, i4);
                    b(dVar, cVarL.f2155b, i4);
                    i3++;
                }
                dVar.m();
                return;
            case 14:
                f fVarN = dVar.n();
                while (i3 < fVarN.f2160b) {
                    b(dVar, fVarN.f2159a, i2 - 1);
                    i3++;
                }
                dVar.o();
                return;
            case 15:
                b bVarJ = dVar.j();
                while (i3 < bVarJ.f2153b) {
                    b(dVar, bVarJ.f2152a, i2 - 1);
                    i3++;
                }
                dVar.k();
                return;
        }
    }
}
