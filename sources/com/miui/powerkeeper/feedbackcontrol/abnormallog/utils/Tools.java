package com.miui.powerkeeper.feedbackcontrol.abnormallog.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/* JADX INFO: loaded from: classes.dex */
public class Tools {
    public static final long A_DAY = 86400000;

    private Tools() {
    }

    public static String getTimestamp() {
        return new SimpleDateFormat("MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
    }

    public static String getTimestampFile() {
        return new SimpleDateFormat("MM-dd-HH-mm-ss").format(new Date(System.currentTimeMillis()));
    }
}
