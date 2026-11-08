package com.xiaomi.channel.commonutils.msa;

/* JADX INFO: loaded from: classes.dex */
interface IdManager {
    String getAAID();

    String getOAID();

    String getUDID();

    String getVAID();

    boolean isAllowOAID();

    boolean isSupported();
}
