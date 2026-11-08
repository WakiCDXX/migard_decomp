package com.miui.powerkeeper.ui;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;

/* JADX INFO: loaded from: classes.dex */
public class AppStateInfo implements Parcelable, Serializable {
    public static final Parcelable.Creator<AppStateInfo> CREATOR = new Parcelable.Creator<AppStateInfo>() { // from class: com.miui.powerkeeper.ui.AppStateInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AppStateInfo createFromParcel(Parcel parcel) {
            return new AppStateInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AppStateInfo[] newArray(int i2) {
            return new AppStateInfo[i2];
        }
    };
    public String activeState;
    public int uid;

    public AppStateInfo() {
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i2) {
        parcel.writeInt(this.uid);
        parcel.writeString(this.activeState);
    }

    public AppStateInfo(Parcel parcel) {
        this.uid = parcel.readInt();
        this.activeState = parcel.readString();
    }
}
