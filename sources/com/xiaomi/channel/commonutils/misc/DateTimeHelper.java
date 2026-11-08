package com.xiaomi.channel.commonutils.misc;

import android.text.TextUtils;
import android.util.Log;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: classes.dex */
public class DateTimeHelper {
    private static final int BJ_TIME_ZONE = 8;
    public static final int DAY_IN_HOUR = 24;
    public static final int DAY_IN_MINUTE = 1440;
    public static final int DAY_IN_MS = 86400000;
    public static final int DAY_IN_SECOND = 86400;
    public static final int HOUR_IN_MINUTE = 60;
    public static final int HOUR_IN_MS = 3600000;
    public static final int HOUR_IN_SECOND = 3600;
    private static final String LOG_TAG = "common/DateTimeHelper";
    public static final int MINUTE_IN_MS = 60000;
    public static final int MINUTE_IN_SECOND = 60;
    public static final int SECOND_IN_MS = 1000;
    public static final int WEEK_IN_DAY = 7;
    public static final int WEEK_IN_HOUR = 168;
    public static final int WEEK_IN_MINUTE = 10080;
    public static final int WEEK_IN_MS = 604800000;
    public static final int WEEK_IN_SECOND = 604800;
    public static final TimeZone sBeijingTimeZone = TimeZone.getTimeZone("Asia/Shanghai");
    public static final long sHourInMinutes = 60;

    public static String getCurrentString(String str) {
        return new SimpleDateFormat(str).format(new Date(System.currentTimeMillis()));
    }

    public static final long getCurrentTiemstamp() {
        return Calendar.getInstance(sBeijingTimeZone).getTimeInMillis();
    }

    public static final long getElapsedMinutesFromHour() {
        return getElapsedMinutesFromHour(getCurrentTiemstamp());
    }

    public static final long getElapsedMinutesFromToday() {
        return getElapsedMinutesFromToday(getCurrentTiemstamp());
    }

    public static final long getTodayStartTimestamp() {
        return getTodayStartTimestamp(getCurrentTiemstamp());
    }

    public static final long getTomorrowStartTimestamp(long j2) {
        return (j2 - (j2 % 86400000)) + 86400000;
    }

    public static String getWeekday(Date date) {
        int day = date.getDay();
        if (day == 0) {
            return "周日";
        }
        if (day == 1) {
            return "周一";
        }
        if (day == 2) {
            return "周二";
        }
        if (day == 3) {
            return "周三";
        }
        if (day != 4) {
            return day != 5 ? "周六" : "周五";
        }
        return "周四";
    }

    public static long parseDate(String str) throws SAXException {
        if (TextUtils.isEmpty(str)) {
            return -1L;
        }
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        try {
            gregorianCalendar.setTime(new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(str));
            gregorianCalendar.setTimeZone(sBeijingTimeZone);
            return gregorianCalendar.getTimeInMillis();
        } catch (ParseException e2) {
            Log.e(LOG_TAG, "Failed to parse date", e2);
            return -1L;
        }
    }

    public static final long getElapsedMinutesFromHour(long j2) {
        return getElapsedMinutesFromToday(j2) % 60;
    }

    public static final long getElapsedMinutesFromToday(long j2) {
        return ((j2 + 28800000) % 86400000) / 60000;
    }

    public static final long getTodayStartTimestamp(long j2) {
        return j2 - ((28800000 + j2) % 86400000);
    }
}
