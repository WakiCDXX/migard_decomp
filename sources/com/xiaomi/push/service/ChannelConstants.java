package com.xiaomi.push.service;

import com.xiaomi.slim.Blob;

/* JADX INFO: loaded from: classes.dex */
public abstract class ChannelConstants {
    public static final int ALARM_JOB_ID = 1;
    public static final int CHID_DEBUG = 99999;
    static final String CHID_IOTDCM = "10";
    static final int CHID_IOTDCM_INT = 10;
    static final String CHID_MICLOUD = "2";
    static final String CHID_MILIAO = "1";
    static final String CHID_MIPUB = "8";
    static final String CHID_MIPUSH = "5";
    static final int CHID_MIPUSH_INT = 5;
    static final String CHID_MIXIN = "3";
    static final String CHID_PING = "0";
    public static final int VERSION_CODE = 48;

    public static boolean isIOTDCMStatTime(Blob blob) {
        return blob != null && blob.getChannelId() == 10 && blob.hasTimestamp();
    }

    public static boolean isMIPUSHStatTime(Blob blob) {
        return blob != null && blob.getChannelId() == 5 && blob.hasTimestamp();
    }
}
