package com.xiaomi.onetrack.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes.dex */
public class ac {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final long f1520a = 604800000;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final int f1521b = 86400000;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final int f1522c = 43200000;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final int f1523d = 3600000;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final int f1524e = 60000;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public static final int f1525f = 1000;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private static final String f1526g = "TimeUtil";

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private static final long f1527h = 300000;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private static long f1528i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private static long f1529j;

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private static long f1530k;

    public static long a() {
        return System.currentTimeMillis();
    }

    public static boolean b(long j2) {
        p.a(f1526g, "inTodayClientTime,current ts :" + System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        long timeInMillis = calendar.getTimeInMillis();
        long j3 = timeInMillis + 86400000;
        p.a(f1526g, "[start]:" + timeInMillis + "\n[end]:" + j3 + "duration" + ((j3 - timeInMillis) - 86400000));
        StringBuilder sb = new StringBuilder();
        sb.append("is in today:");
        sb.append(timeInMillis <= j2 && j2 < j3);
        p.a(f1526g, sb.toString());
        return timeInMillis <= j2 && j2 < j3;
    }

    public static String c(long j2) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(j2);
            return new SimpleDateFormat("HH:mm:ss yy-MM-dd").format(calendar.getTime());
        } catch (Exception unused) {
            return "";
        }
    }

    public static boolean d(long j2) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        Calendar calendar2 = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        calendar2.setTimeInMillis(j2);
        return calendar2.get(1) == calendar.get(1) && calendar2.get(2) == calendar.get(2) && calendar2.get(5) == calendar.get(5);
    }

    public static boolean a(long j2, long j3) {
        return Math.abs(System.currentTimeMillis() - j2) >= j3;
    }

    public static boolean a(long j2) {
        p.a(f1526g, "inToday,current ts :" + a());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(a());
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        long timeInMillis = calendar.getTimeInMillis();
        long j3 = timeInMillis + 86400000;
        p.a(f1526g, "[start]:" + timeInMillis + "\n[end]:" + j3 + "duration" + ((j3 - timeInMillis) - 86400000));
        StringBuilder sb = new StringBuilder();
        sb.append("is in today:");
        sb.append(timeInMillis <= j2 && j2 < j3);
        p.a(f1526g, sb.toString());
        return timeInMillis <= j2 && j2 < j3;
    }

    public static long b() {
        Calendar calendar;
        try {
            calendar = Calendar.getInstance(TimeZone.getTimeZone(q.b()));
        } catch (Exception unused) {
            calendar = Calendar.getInstance();
        }
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar.getTimeInMillis();
    }

    public static boolean a(long j2, int i2) {
        return Math.abs(System.currentTimeMillis() - j2) >= ((long) (i2 + new Random().nextInt(i2 / 2)));
    }
}
