package com.miui.powerkeeper.perfengine;

import android.text.TextUtils;

/* JADX INFO: compiled from: AdPerfCmd.java */
/* JADX INFO: loaded from: classes.dex */
public class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public String f500a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public String f501b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public String f502c;

    public a(String str) {
        this.f500a = "";
        this.f501b = "";
        this.f502c = "";
        if (TextUtils.isEmpty(str)) {
            return;
        }
        String[] strArrSplit = str.split("#");
        if (strArrSplit.length == 3) {
            this.f500a = strArrSplit[0];
            this.f501b = strArrSplit[1];
            this.f502c = strArrSplit[2];
        }
    }
}
