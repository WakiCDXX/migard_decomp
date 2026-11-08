package com.ot.pubsub.util.oaid.a;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;

/* JADX INFO: loaded from: classes.dex */
public interface a extends IInterface {

    /* JADX INFO: renamed from: com.ot.pubsub.util.oaid.a.a$a, reason: collision with other inner class name */
    public static final class C0004a implements a {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private IBinder f737a;

        public C0004a(IBinder iBinder) {
            this.f737a = iBinder;
        }

        @Override // com.ot.pubsub.util.oaid.a.a
        public String a() {
            String string;
            Parcel parcelObtain = Parcel.obtain();
            Parcel parcelObtain2 = Parcel.obtain();
            try {
                parcelObtain.writeInterfaceToken("com.asus.msa.SupplementaryDID.IDidAidlInterface");
                this.f737a.transact(3, parcelObtain, parcelObtain2, 0);
                parcelObtain2.readException();
                string = parcelObtain2.readString();
            } catch (Throwable th) {
                parcelObtain.recycle();
                parcelObtain2.recycle();
                th.printStackTrace();
                string = null;
            }
            parcelObtain.recycle();
            parcelObtain2.recycle();
            return string;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this.f737a;
        }
    }

    String a();
}
