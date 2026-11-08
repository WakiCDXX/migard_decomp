package com.miui.powerkeeper.batterylife.data;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class AppTransactionData {

    @SerializedName("tr_pkg_name")
    String mPakageName;

    @SerializedName("tr_transaction_list")
    public ArrayList<TagTransactionData> mTransactionDataList;

    public AppTransactionData(String str, ArrayList<TagTransactionData> arrayList) {
        this.mPakageName = str;
        this.mTransactionDataList = arrayList;
    }

    public String toString() {
        return "AppTransactionData{mPakageName='" + this.mPakageName + "', mTransactionDataList=" + this.mTransactionDataList + '}';
    }
}
