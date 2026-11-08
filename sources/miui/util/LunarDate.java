package miui.util;

import android.content.res.Resources;
import android.text.TextUtils;
import android.text.format.Time;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.thermal.resource.ScenarioConfig;
import com.xiaomi.mipush.sdk.Constants;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import q.a;
import q.h;

/* JADX INFO: loaded from: classes.dex */
public class LunarDate {
    public static final int MAX_LUNAR_YEAR = 2050;
    public static final int MIN_LUNAR_YEAR = 1900;
    private static final long[] luYearData = {19416, 19168, 42352, 21717, 53856, 55632, 91476, 22176, 39632, 21970, 19168, 42422, 42192, 53840, 119381, 46400, 54944, 44450, 38320, 84343, 18800, 42160, 46261, 27216, 27968, 109396, 11104, 38256, 21234, 18800, 25958, 54432, 59984, 28309, 23248, 11104, 100067, 37600, 116951, 51536, 54432, 120998, 46416, 22176, 107956, 9680, 37584, 53938, 43344, 46423, 27808, 46416, 86869, 19872, 42448, 83315, 21200, 43432, 59728, 27296, 44710, 43856, 19296, 43748, 42352, 21088, 62051, 55632, 23383, 22176, 38608, 19925, 19152, 42192, 54484, 53840, 54616, 46400, 46752, 103846, 38320, 18864, 43380, 42160, 45690, 27216, 27968, 44870, 43872, 38256, 19189, 18800, 25776, 29859, 59984, 27480, 23232, 43872, 38613, 37600, 51552, 55636, 54432, 55888, 30034, 22176, 43959, 9680, 37584, 51893, 43344, 46240, 47780, 44368, 21977, 19360, 42416, 86390, 21168, 43312, 31060, 27296, 44368, 23378, 19296, 42726, 42208, 53856, 60005, 54576, 23200, 30371, 38608, 19415, 19152, 42192, 118966, 53840, 54560, 56645, 46496, 22224, 21938, 18864, 42359, 42160, 43600, 111189, 27936, 44448};
    private static final char[] iSolarLunarOffsetTable = {'1', '&', 28, '.', '\"', 24, '+', ' ', 21, '(', 29, '0', '$', 25, ',', '!', 22, ')', 31, '2', '&', 27, '.', '#', 23, '+', ' ', 22, '(', 29, '/', '$', 25, ',', '\"', 23, ')', 30, '1', '&', 26, '-', '#', 24, '+', ' ', 21, '(', 28, '/', '$', 26, ',', '!', 23, '*', 30, '0', '&', 27, '-', '#', 24, '+', ' ', 20, '\'', 29, '/', '$', 26, '-', '!', 22, ')', 30, '0', '%', 27, '.', '#', 24, '+', ' ', '2', '\'', 28, '/', '$', 26, '-', '\"', 22, '(', 30, '1', '%', 27, '.', '#', 23, '*', 31, 21, '\'', 28, '0', '%', 25, ',', '!', 22, '(', 30, '1', '&', 27, '.', '#', 24, '*', 31, 21, '(', 28, '/', '$', 25, '+', '!', 22, ')', 30, '1', '&', 27, '-', '\"', 23, '*', 31, 21, '(', 29, '/', '$', 25, ',', ' ', 22};
    private static int[] lunarHolidaysTable = {101, 115, 505, 707, 815, 909, 1208};
    private static int[] solarHolidaysTable = {101, 214, 308, 312, 401, 501, 504, 601, 701, 801, 910, 1001, 1225};
    private static int[] solarHolidaysTable_TW = {101, 214, 228, 308, 312, 314, 329, 404, 501, 715, 903, 928, 1010, 1024, 1025, 1112, 1225};
    private static int[] solarHolidaysTable_HK = {101, 214, 501, 701, 1001, 1225};
    private static int[] lunarHolidays = {h.F1, h.D0, h.B1, h.f2281l, h.D1, h.A1, h.C1};
    private static int[] solarHolidays = {h.o1, h.K1, h.B0, h.f2266c, h.n0, h.C0, h.f2276h, h.f2274g, h.p1, h.z1, h.y1, h.m1, h.f2278i};
    private static int[] solarHolidays_TW = {h.o1, h.K1, h.q1, h.B0, h.f2266c, h.f2264b, h.I1, h.H1, h.C0, h.f2262a, h.f2268d, h.y1, h.m1, h.J1, h.r1, h.n1, h.f2278i};
    private static int[] solarHolidays_HK = {h.o1, h.K1, h.C0, h.y0, h.m1, h.f2278i};
    private static int[] solarTerms = {h.s1, h.r0, h.u1, h.E1, h.A0, h.L1, h.f2279j, h.q0, h.w1, h.o0, h.p0, h.x1, h.t1, h.s0, h.f2270e, h.v1, h.M1, h.f2272f, h.f2280k, h.z0, h.N1, h.E0, h.x0, h.O1};
    private static char[] solarTermsTable = {150, 180, 150, 166, 151, 151, 'x', 'y', 'y', 'i', 'x', 'w', 150, 164, 150, 150, 151, 135, 'y', 'y', 'y', 'i', 'x', 'x', 150, 165, 135, 150, 135, 135, 'y', 'i', 'i', 'i', 'x', 'x', 134, 165, 150, 165, 150, 151, 136, 'x', 'x', 'y', 'x', 135, 150, 180, 150, 166, 151, 151, 'x', 'y', 'y', 'i', 'x', 'w', 150, 164, 150, 150, 151, 151, 'y', 'y', 'y', 'i', 'x', 'x', 150, 165, 135, 150, 135, 135, 'y', 'i', 'i', 'i', 'x', 'x', 134, 165, 150, 165, 150, 151, 136, 'x', 'x', 'i', 'x', 135, 150, 180, 150, 166, 151, 151, 'x', 'y', 'y', 'i', 'x', 'w', 150, 164, 150, 150, 151, 151, 'y', 'y', 'y', 'i', 'x', 'x', 150, 165, 135, 150, 135, 135, 'y', 'i', 'i', 'i', 'x', 'x', 134, 165, 150, 165, 150, 151, 136, 'x', 'x', 'i', 'x', 135, 149, 180, 150, 166, 151, 151, 'x', 'y', 'y', 'i', 'x', 'w', 150, 180, 150, 166, 151, 151, 'y', 'y', 'y', 'i', 'x', 'x', 150, 165, 151, 150, 151, 135, 'y', 'y', 'i', 'i', 'x', 'x', 150, 165, 150, 165, 150, 151, 136, 'x', 'x', 'y', 'w', 135, 149, 180, 150, 166, 150, 151, 'x', 'y', 'x', 'i', 'x', 135, 150, 180, 150, 166, 151, 151, 'y', 'y', 'y', 'i', 'x', 'w', 150, 165, 151, 150, 151, 135, 'y', 'y', 'i', 'i', 'x', 'x', 150, 165, 150, 165, 150, 151, 136, 'x', 'x', 'y', 'w', 135, 149, 180, 150, 165, 150, 151, 'x', 'y', 'x', 'i', 'x', 135, 150, 180, 150, 166, 151, 151, 'y', 'y', 'y', 'i', 'x', 'w', 150, 164, 150, 150, 151, 135, 'y', 'y', 'i', 'i', 'x', 'x', 150, 165, 150, 165, 150, 151, 136, 'x', 'x', 'y', 'w', 135, 149, 180, 150, 165, 150, 151, 'x', 'y', 'x', 'i', 'x', 135, 150, 180, 150, 166, 151, 151, 'x', 'y', 'y', 'i', 'x', 'w', 150, 164, 150, 150, 151, 135, 'y', 'y', 'y', 'i', 'x', 'x', 150, 165, 150, 165, 150, 150, 136, 'x', 'x', 'x', 135, 135, 149, 180, 150, 165, 150, 151, 136, 'x', 'x', 'y', 'w', 135, 150, 180, 150, 166, 151, 151, 'x', 'y', 'y', 'i', 'x', 'w', 150, 164, 150, 150, 151, 135, 'y', 'y', 'y', 'i', 'x', 'x', 150, 165, 150, 165, 150, 150, 136, 'x', 'x', 'x', 135, 135, 149, 180, 150, 165, 150, 151, 136, 'x', 'x', 'i', 'x', 135, 150, 180, 150, 166, 151, 151, 'x', 'y', 'y', 'i', 'x', 'w', 150, 164, 150, 150, 151, 151, 'y', 'y', 'y', 'i', 'x', 'x', 150, 165, 150, 165, 150, 150, 136, 'x', 'x', 'x', 135, 135, 149, 180, 150, 165, 150, 151, 136, 'x', 'x', 'i', 'x', 135, 150, 180, 150, 166, 151, 151, 'x', 'y', 'y', 'i', 'x', 'w', 150, 164, 150, 150, 151, 151, 'y', 'y', 'y', 'i', 'x', 'x', 150, 165, 150, 165, 150, 150, 136, 'x', 'x', 'x', 135, 135, 149, 180, 150, 165, 150, 151, 136, 'x', 'x', 'i', 'x', 135, 150, 180, 150, 166, 151, 151, 'x', 'y', 'y', 'i', 'x', 'w', 150, 164, 150, 150, 151, 151, 'y', 'y', 'y', 'i', 'x', 'x', 150, 165, 150, 165, 166, 150, 136, 'x', 'x', 'x', 135, 135, 149, 180, 150, 165, 150, 151, 136, 'x', 'x', 'y', 'w', 135, 149, 180, 150, 166, 151, 151, 'x', 'y', 'x', 'i', 'x', 'w', 150, 180, 150, 166, 151, 151, 'y', 'y', 'y', 'i', 'x', 'x', 150, 165, 166, 165, 166, 150, 136, 136, 'x', 'x', 135, 135, 165, 180, 150, 165, 150, 151, 136, 'y', 'x', 'y', 'w', 135, 149, 180, 150, 165, 150, 151, 'x', 'y', 'x', 'i', 'x', 'w', 150, 180, 150, 166, 151, 151, 'y', 'y', 'y', 'i', 'x', 'x', 150, 165, 166, 165, 166, 150, 136, 136, 'x', 'x', 135, 135, 165, 180, 150, 165, 150, 151, 136, 'x', 'x', 'y', 'w', 135, 149, 180, 150, 165, 150, 151, 'x', 'y', 'x', 'h', 'x', 135, 150, 180, 150, 166, 151, 151, 'x', 'y', 'y', 'i', 'x', 'w', 150, 165, 165, 165, 166, 150, 136, 136, 'x', 'x', 135, 135, 165, 180, 150, 165, 150, 151, 136, 'x', 'x', 'y', 'w', 135, 149, 180, 150, 165, 150, 151, 136, 'x', 'x', 'i', 'x', 135, 150, 180, 150, 166, 151, 151, 'x', 'y', 'y', 'i', 'x', 'w', 150, 164, 165, 165, 166, 150, 136, 136, 136, 'x', 135, 135, 165, 180, 150, 165, 150, 150, 136, 'x', 'x', 'x', 135, 135, 150, 180, 150, 165, 150, 151, 136, 'x', 'x', 'i', 'x', 135, 150, 180, 150, 166, 151, 151, 'x', 'y', 'y', 'i', 'x', 'w', 150, 164, 165, 165, 166, 150, 136, 136, 136, 'x', 135, 135, 165, 180, 150, 165, 150, 150, 136, 'x', 'x', 'x', 135, 135, 149, 180, 150, 165, 150, 151, 136, 'x', 'x', 'i', 'x', 135, 150, 180, 150, 166, 151, 151, 'x', 'y', 'y', 'i', 'x', 'w', 150, 164, 165, 165, 166, 166, 136, 136, 136, 'x', 135, 135, 165, 180, 150, 165, 150, 150, 136, 'x', 'x', 'x', 135, 135, 149, 180, 150, 165, 150, 151, 136, 'x', 'x', 'i', 'x', 135, 150, 180, 150, 166, 151, 151, 'x', 'y', 'y', 'i', 'x', 'w', 150, 164, 165, 165, 166, 166, 136, 136, 136, 'x', 135, 135, 165, 181, 150, 165, 166, 150, 136, 'x', 'x', 'x', 135, 135, 149, 180, 150, 165, 150, 151, 136, 'x', 'x', 'i', 'x', 135, 150, 180, 150, 166, 151, 151, 'x', 'y', 'x', 'i', 'x', 'w', 150, 164, 165, 181, 166, 166, 136, 137, 136, 'x', 135, 135, 165, 180, 150, 165, 150, 150, 136, 136, 'x', 'x', 135, 135, 149, 180, 150, 165, 150, 151, 136, 'x', 'x', 'y', 'x', 135, 150, 180, 150, 166, 150, 151, 'x', 'y', 'x', 'i', 'x', 'w', 150, 164, 165, 181, 166, 166, 136, 136, 136, 'x', 135, 135, 165, 180, 150, 165, 166, 150, 136, 136, 'x', 'x', 'w', 135, 149, 180, 150, 165, 150, 151, 136, 'x', 'x', 'y', 'w', 135, 149, 180, 150, 165, 150, 151, 'x', 'y', 'x', 'i', 'x', 'w', 150, 180, 165, 181, 
    166, 166, 135, 136, 136, 'x', 135, 135, 165, 180, 166, 165, 166, 150, 136, 136, 'x', 'x', 135, 135, 165, 180, 150, 165, 150, 151, 136, 'x', 'x', 'y', 'w', 135, 149, 180, 150, 165, 150, 151, 136, 'y', 'x', 'i', 'x', 135, 150, 180, 165, 181, 166, 166, 135, 136, 136, 'x', 135, 134, 165, 180, 165, 165, 166, 150, 136, 136, 136, 'x', 135, 135, 165, 180, 150, 165, 150, 150, 136, 'x', 'x', 'y', 'w', 135, 149, 180, 150, 165, 134, 151, 136, 'x', 'x', 'i', 'x', 135, 150, 180, 165, 181, 166, 166, 135, 136, 136, 'x', 135, 134, 165, 179, 165, 165, 166, 150, 136, 136, 136, 'x', 135, 135, 165, 180, 150, 165, 150, 150, 136, 'x', 'x', 'x', 135, 135, 149, 180, 150, 165, 150, 151, 136, 'v', 'x', 'i', 'x', 135, 150, 180, 165, 181, 166, 166, 135, 136, 136, 'x', 135, 134, 165, 179, 165, 165, 166, 166, 136, 136, 136, 'x', 135, 135, 165, 180, 150, 165, 150, 150, 136, 'x', 'x', 'x', 135, 135, 149, 180, 150, 165, 150, 151, 136, 'x', 'x', 'i', 'x', 135, 150, 180, 165, 181, 166, 166, 135, 136, 136, 'x', 135, 134, 165, 179, 165, 165, 166, 166, 136, 136, 136, 'x', 135, 135, 165, 180, 150, 165, 150, 150, 136, 'x', 'x', 'x', 135, 135, 149, 180, 150, 165, 150, 151, 136, 'x', 'x', 'i', 'x', 135, 150, 180, 165, 181, 166, 166, 135, 136, 136, 'x', 135, 134, 165, 179, 165, 165, 166, 166, 136, 136, 136, 'x', 135, 135, 165, 180, 150, 165, 166, 150, 136, 136, 'x', 'x', 135, 135, 149, 180, 150, 165, 150, 151, 136, 'x', 'x', 'i', 'x', 135, 150, 180, 165, 181, 166, 166, 135, 136, 135, 'x', 135, 134, 165, 179, 165, 181, 166, 166, 136, 136, 136, 'x', 135, 135, 165, 180, 150, 165, 166, 150, 136, 136, 'x', 'x', 135, 135, 149, 180, 150, 165, 150, 151, 136, 'x', 'x', 'y', 'x', 135, 150, 180, 165, 181, 165, 166, 135, 136, 135, 'x', 135, 134, 165, 179, 165, 181, 166, 166, 135, 136, 136, 'x', 135, 135, 165, 180, 150, 165, 166, 150, 136, 136, 'x', 'x', 135, 135, 149, 180, 150, 165, 150, 151, 136, 'x', 'x', 'y', 'w', 135, 149, 180, 165, 180, 165, 166, 135, 136, 135, 'x', 135, 134, 165, 195, 165, 181, 166, 166, 135, 136, 136, 'x', 135, 135, 165, 180, 166, 165, 166, 150, 136, 136, 'x', 'x', 135, 135, 165, 180, 150, 165, 150, 150, 136, 'x', 'x', 'y', 'w', 135, 149, 180, 165, 180, 165, 166, 151, 135, 135, 'x', 135, 134, 165, 195, 165, 181, 166, 166, 135, 136, 136, 'x', 135, 134, 165, 180, 165, 165, 166, 150, 136, 136, 136, 'x', 135, 135, 165, 180, 150, 165, 150, 150, 136, 'x', 'x', 'y', 'w', 135, 149, 180, 165, 180, 165, 166, 151, 135, 135, 'x', 135, 150, 165, 195, 165, 181, 166, 166, 135, 136, 136, 'x', 135, 134, 165, 179, 165, 165, 166, 166, 136, 136, 136, 'x', 135, 135, 165, 180, 150, 165, 150, 150, 136, 'x', 'x', 'x', 135, 135, 149, 180, 165, 180, 165, 166, 151, 135, 135, 'x', 135, 150, 165, 195, 165, 181, 166, 166, 135, 136, 136, 'x', 135, 134, 165, 179, 165, 165, 166, 166, 136, 136, 136, 'x', 135, 135, 165, 180, 150, 165, 150, 150, 136, 'x', 'x', 'x', 135, 135, 149, 180, 165, 180, 165, 166, 151, 135, 135, 'x', 135, 150, 165, 195, 165, 181, 166, 166, 136, 136, 136, 'x', 135, 134, 165, 179, 165, 165, 166, 166, 136, 'x', 136, 'x', 135, 135, 165, 180, 150, 165, 166, 150, 136, 136, 'x', 'x', 135, 135, 149, 180, 165, 180, 165, 166, 151, 135, 135, 'x', 135, 150, 165, 195, 165, 181, 166, 166, 135, 136, 136, 'x', 135, 134, 165, 179, 165, 165, 166, 166, 136, 136, 136, 'x', 135, 135, 165, 180, 150, 165, 166, 150, 136, 136, 'x', 'x', 135, 135, 149, 180, 165, 180, 165, 166, 151, 135, 135, 'x', 135, 150, 165, 195, 165, 181, 165, 166, 135, 136, 135, 'x', 135, 134, 165, 179, 165, 181, 166, 166, 136, 136, 136, 'x', 135, 135, 165, 180, 150, 165, 166, 150, 136, 136, 'x', 'x', 135, 135, 149, 180, 165, 180, 165, 166, 151, 135, 135, 136, 135, 150, 165, 195, 165, 180, 165, 166, 135, 136, 135, 'x', 135, 134, 165, 179, 165, 181, 166, 166, 135, 136, 136, 'x', 135, 135, 165, 180, 150, 165, 166, 150, 136, 136, 'x', 'x', 135, 135, 149, 180, 165, 180, 165, 165, 151, 135, 135, 136, 134, 150, 164, 195, 165, 165, 165, 166, 151, 135, 135, 'x', 135, 134, 165, 195, 165, 181, 166, 166, 135, 136, 'x', 'x', 135, 135};
    private static final HashMap<Integer, Integer> sEasterCache = new HashMap<>();

    public static class BirthHoroscope {
        private static final String BASE_DATE_STRING = "1900-1-31";
        private static final long DAY_IN_MILLS = 86400000;
        private static Date sBaseDate;
        private static final SimpleDateFormat sChineseDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        private static String[] sDiZhi;
        private static boolean sIsInitialized;
        private static String[] sJiaZi;
        private static String[] sTianGan;
        private Calendar mCalendar;
        private int mDay;
        private int mHour;
        private int mMonth;
        private int mYear;

        public BirthHoroscope(Calendar calendar, int i2, int i3, int i4, int i5) {
            this.mCalendar = calendar;
            this.mYear = i2;
            this.mMonth = i3;
            this.mDay = i4;
            this.mHour = i5;
        }

        private static int getLunarHourIndex(int i2) {
            return ((i2 + 1) % 24) / 2;
        }

        public static BirthHoroscope newInstance(Resources resources, Calendar calendar) {
            try {
                if (!sIsInitialized) {
                    sTianGan = resources.getStringArray(a.f2188t);
                    sDiZhi = resources.getStringArray(a.f2176h);
                    sJiaZi = resources.getStringArray(a.f2181m);
                    sBaseDate = sChineseDateFormat.parse(BASE_DATE_STRING);
                    sIsInitialized = true;
                }
                int time = (int) ((calendar.getTime().getTime() - sBaseDate.getTime()) / 86400000);
                int i2 = LunarDate.MIN_LUNAR_YEAR;
                int iYrDays = 0;
                while (i2 < 2050 && time > 0) {
                    iYrDays = LunarDate.yrDays(i2);
                    time -= iYrDays;
                    i2++;
                }
                if (time < 0) {
                    time += iYrDays;
                    i2--;
                }
                int i3 = i2;
                int iRMonth = LunarDate.rMonth(i3);
                boolean z2 = false;
                int iMthDays = 0;
                int i4 = 1;
                while (i4 < 13 && time > 0) {
                    if (iRMonth <= 0 || i4 != iRMonth + 1 || z2) {
                        iMthDays = LunarDate.mthDays(i3, i4);
                    } else {
                        i4--;
                        iMthDays = LunarDate.rMthDays(i3);
                        z2 = true;
                    }
                    time -= iMthDays;
                    if (z2 && i4 == iRMonth + 1) {
                        z2 = false;
                    }
                    i4++;
                }
                if (time == 0 && iRMonth > 0 && i4 == iRMonth + 1 && !z2) {
                    i4--;
                }
                if (time < 0) {
                    time += iMthDays;
                    i4--;
                }
                return new BirthHoroscope(calendar, i3, i4, time + 1, calendar.get(11));
            } catch (Exception e2) {
                e2.printStackTrace();
                return null;
            }
        }

        public String getBirthHoroscope() {
            int i2 = (this.mYear - 1864) % 60;
            String str = sJiaZi[i2];
            int i3 = ((i2 % 5) + 1) * 2;
            if (i3 == 10) {
                i3 = 0;
            }
            String str2 = sTianGan[((i3 + this.mMonth) - 1) % 10] + sDiZhi[(this.mMonth + 1) % 12];
            int riZhu = getRiZhu();
            String str3 = sJiaZi[riZhu];
            int lunarHourIndex = getLunarHourIndex(this.mHour);
            return str + str2 + str3 + (sTianGan[(((riZhu % 5) * 2) + lunarHourIndex) % 10] + sDiZhi[lunarHourIndex]);
        }

        public int getRiZhu() {
            return (((int) ((this.mCalendar.getTime().getTime() - sBaseDate.getTime()) / 86400000)) + 40) % sJiaZi.length;
        }
    }

    public static final long[] calLunar(int i2, int i3, int i4) {
        char c2;
        long[] jArr = new long[7];
        long dayOffset = getDayOffset(i2, i3, i4);
        jArr[5] = 40 + dayOffset;
        jArr[4] = 14;
        char c3 = 0;
        int i5 = MIN_LUNAR_YEAR;
        int iMthDays = 0;
        while (i5 < 2050 && dayOffset > 0) {
            iMthDays = yrDays(i5);
            dayOffset -= (long) iMthDays;
            jArr[4] = jArr[4] + 12;
            i5++;
        }
        if (dayOffset < 0) {
            dayOffset += (long) iMthDays;
            i5--;
            jArr[4] = jArr[4] - 12;
        }
        jArr[0] = i5;
        jArr[3] = i5 - 1864;
        int iRMonth = rMonth(i5);
        jArr[6] = 0;
        int i6 = 1;
        while (i6 < 13 && dayOffset > 0) {
            if (iRMonth > 0 && i6 == iRMonth + 1 && jArr[6] == 0) {
                jArr[6] = 1;
                iMthDays = rMthDays((int) jArr[c3]);
                i6--;
            } else {
                iMthDays = mthDays((int) jArr[c3], i6);
            }
            if (jArr[6] == 1 && i6 == iRMonth + 1) {
                jArr[6] = 0;
            }
            int i7 = iRMonth;
            dayOffset -= (long) iMthDays;
            if (jArr[6] == 0) {
                jArr[4] = jArr[4] + 1;
            }
            i6++;
            iRMonth = i7;
            c3 = 0;
        }
        int i8 = iRMonth;
        if (dayOffset != 0 || i8 <= 0 || i6 != i8 + 1) {
            c2 = 4;
        } else if (jArr[6] == 1) {
            jArr[6] = 0;
            c2 = 4;
        } else {
            jArr[6] = 1;
            i6--;
            c2 = 4;
            jArr[4] = jArr[4] - 1;
        }
        if (dayOffset < 0) {
            dayOffset += (long) iMthDays;
            i6--;
            jArr[c2] = jArr[c2] - 1;
        }
        jArr[1] = i6;
        jArr[2] = dayOffset + 1;
        return jArr;
    }

    public static String formatLunarDate(int i2, int i3, int i4) {
        StringBuilder sb = new StringBuilder();
        if (i2 > 0) {
            sb.append(i2);
            sb.append(Constants.ACCEPT_TIME_SEPARATOR_SERVER);
        }
        sb.append(i3 + 1);
        sb.append(Constants.ACCEPT_TIME_SEPARATOR_SERVER);
        sb.append(i4);
        return sb.toString();
    }

    private static final int getDayOffset(int i2, int i3, int i4) {
        GregorianCalendar gregorianCalendar = (GregorianCalendar) Calendar.getInstance();
        gregorianCalendar.clear();
        int i5 = 0;
        for (int i6 = 1900; i6 < i2; i6++) {
            i5 = gregorianCalendar.isLeapYear(i6) ? i5 + 366 : i5 + 365;
        }
        gregorianCalendar.set(i2, i3, i4);
        int i7 = i5 + gregorianCalendar.get(6);
        gregorianCalendar.set(MIN_LUNAR_YEAR, 0, 31);
        return i7 - gregorianCalendar.get(6);
    }

    public static final String getDayString(Resources resources, int i2) {
        if (i2 == 10) {
            return resources.getString(h.H0);
        }
        if (i2 == 20) {
            return resources.getString(h.J0);
        }
        if (i2 == 30) {
            return resources.getString(h.R0);
        }
        int i3 = i2 / 10;
        String string = i3 == 0 ? resources.getString(h.G0) : "";
        if (i3 == 1) {
            string = resources.getString(h.S0);
        }
        if (i3 == 2) {
            string = resources.getString(h.O0);
        }
        if (i3 == 3) {
            string = resources.getString(h.Q0);
        }
        switch (i2 % 10) {
            case 1:
                return string + resources.getString(h.Y0);
            case 2:
                return string + resources.getString(h.I0);
            case 3:
                return string + resources.getString(h.Q0);
            case 4:
                return string + resources.getString(h.V0);
            case 5:
                return string + resources.getString(h.W0);
            case 6:
                return string + resources.getString(h.N0);
            case 7:
                return string + resources.getString(h.P0);
            case 8:
                return string + resources.getString(h.F0);
            case 9:
                return string + resources.getString(h.K0);
            default:
                return string;
        }
    }

    private static String getDigitString(Resources resources, int i2) {
        switch (i2) {
            case 0:
                return resources.getString(h.M0);
            case 1:
                return resources.getString(h.Y0);
            case 2:
                return resources.getString(h.I0);
            case 3:
                return resources.getString(h.Q0);
            case 4:
                return resources.getString(h.V0);
            case 5:
                return resources.getString(h.W0);
            case 6:
                return resources.getString(h.N0);
            case 7:
                return resources.getString(h.P0);
            case 8:
                return resources.getString(h.F0);
            case 9:
                return resources.getString(h.K0);
            default:
                return "";
        }
    }

    public static String getHoliday(Resources resources, long[] jArr, Calendar calendar, String str) {
        int[] iArr;
        int[] iArr2;
        try {
            int i2 = calendar.get(2) + 1;
            int i3 = calendar.get(5);
            if (miui.os.Build.checkRegion("TW")) {
                iArr = solarHolidaysTable_TW;
                iArr2 = solarHolidays_TW;
            } else if (miui.os.Build.checkRegion("HK")) {
                iArr = solarHolidaysTable_HK;
                iArr2 = solarHolidays_HK;
            } else {
                iArr = solarHolidaysTable;
                iArr2 = solarHolidays;
            }
            int length = iArr.length;
            for (int i4 = 0; i4 < length; i4++) {
                int i5 = iArr[i4];
                if (i5 / 100 == i2 && i5 % 100 == i3) {
                    return resources.getString(iArr2[i4]);
                }
            }
            if (miui.os.Build.checkRegion("HK")) {
                String strIsEasterDay = isEasterDay(resources, calendar);
                if (!TextUtils.isEmpty(strIsEasterDay)) {
                    return strIsEasterDay;
                }
            }
            if (jArr[6] == 1) {
                return null;
            }
            int i6 = (int) jArr[1];
            int i7 = (int) jArr[2];
            int length2 = lunarHolidaysTable.length;
            for (int i8 = 0; i8 < length2; i8++) {
                int i9 = lunarHolidaysTable[i8];
                if (i9 / 100 == i6 && i9 % 100 == i7) {
                    return resources.getString(lunarHolidays[i8]);
                }
            }
            return null;
        } catch (Exception unused) {
        }
    }

    public static int[][] getLunarBirthdays(int i2, int i3, int i4) {
        int[][] iArr;
        int i5 = i3 + 1;
        if (i5 > 12) {
            if (i3 - 11 == rMonth(i2)) {
                iArr = new int[2][];
                int iMin = Math.min(rMthDays(i2), i4);
                int[] iArrLunarToSolar = lunarToSolar(i2, i5, iMin);
                iArr[0] = new int[]{iArrLunarToSolar[0], iArrLunarToSolar[1] - 1, iArrLunarToSolar[2], iMin == i4 ? 0 : 1};
            } else {
                iArr = new int[1][];
            }
            i5 = i3 - 11;
        } else {
            iArr = new int[1][];
        }
        int iMin2 = Math.min(mthDays(i2, i5), i4);
        int[] iArrLunarToSolar2 = lunarToSolar(i2, i5, iMin2);
        iArr[iArr.length - 1] = new int[]{iArrLunarToSolar2[0], iArrLunarToSolar2[1] - 1, iArrLunarToSolar2[2], iMin2 != i4 ? 1 : 0};
        return iArr;
    }

    private static int getLunarNewYearOffsetDays(int i2, int i3, int i4) {
        int iMthDays;
        int iRMonth = rMonth(i2);
        if (iRMonth <= 0 || iRMonth != i3 - 12) {
            iMthDays = 0;
        } else {
            iMthDays = mthDays(i2, iRMonth);
            i3 = iRMonth;
        }
        for (int i5 = 1; i5 < i3; i5++) {
            iMthDays += mthDays(i2, i5);
            if (i5 == iRMonth) {
                iMthDays += rMthDays(i2);
            }
        }
        return iMthDays + (i4 - 1);
    }

    public static String getLunarString(Resources resources, int i2, int i3, int i4) {
        StringBuilder sb = new StringBuilder();
        if (i2 > 0) {
            sb.append(Integer.toString(i2));
            sb.append(resources.getString(h.X0));
        }
        if (i3 >= 12) {
            sb.append(resources.getString(h.L0));
            i3 -= 12;
        }
        sb.append(getMonthString(resources, i3 + 1));
        sb.append(resources.getString(h.Z0));
        sb.append(getDayString(resources, i4));
        return sb.toString();
    }

    public static String getMonthString(Resources resources, int i2) {
        if (i2 > 12) {
            return null;
        }
        switch (i2) {
            case 0:
                return "";
            case 1:
                return resources.getString(h.a1);
            case 2:
                return resources.getString(h.I0);
            case 3:
                return resources.getString(h.Q0);
            case 4:
                return resources.getString(h.V0);
            case 5:
                return resources.getString(h.W0);
            case 6:
                return resources.getString(h.N0);
            case 7:
                return resources.getString(h.P0);
            case 8:
                return resources.getString(h.F0);
            case 9:
                return resources.getString(h.K0);
            case 10:
                return resources.getString(h.S0);
            case 11:
                return resources.getString(h.U0);
            case 12:
                return resources.getString(h.T0);
            default:
                return null;
        }
    }

    public static long getNextLunarBirthday(int i2, int i3) {
        Time time = new Time();
        time.setToNow();
        int i4 = (int) calLunar(time.year, time.month, time.monthDay)[0];
        time.second = 0;
        time.minute = 0;
        time.hour = 0;
        long jNormalize = time.normalize(false);
        long j2 = Long.MAX_VALUE;
        long jMin = Long.MAX_VALUE;
        while (j2 == jMin && i4 >= 1900 && i4 < 2050) {
            for (int[] iArr : getLunarBirthdays(i4, i2, i3)) {
                time.set(iArr[2], iArr[1], iArr[0]);
                long jNormalize2 = time.normalize(false);
                if (jNormalize2 >= jNormalize) {
                    jMin = Math.min(jMin, jNormalize2);
                }
            }
            i4++;
            j2 = Long.MAX_VALUE;
        }
        if (j2 == jMin) {
            return 0L;
        }
        return jMin;
    }

    public static String getSolarTerm(Resources resources, Calendar calendar) {
        int i2 = calendar.get(1);
        int i3 = calendar.get(2);
        int i4 = calendar.get(5);
        char c2 = solarTermsTable[((i2 - 1901) * 12) + i3];
        if (i4 == (c2 % 16) + 15) {
            return resources.getString(solarTerms[(i3 * 2) + 1]);
        }
        if (i4 == 15 - (c2 / 16)) {
            return resources.getString(solarTerms[i3 * 2]);
        }
        return null;
    }

    static int getSolarYearMonthDays(int i2, int i3) {
        if (i3 == 1 || i3 == 3 || i3 == 5 || i3 == 7 || i3 == 8 || i3 == 10 || i3 == 12) {
            return 31;
        }
        if (i3 == 4 || i3 == 6 || i3 == 9 || i3 == 11) {
            return 30;
        }
        if (i3 == 2) {
            return isSolarLeapYear(i2) ? 29 : 28;
        }
        return 0;
    }

    public static String getString(Resources resources, Calendar calendar) {
        return solar2lunar(resources, calendar.get(1), calendar.get(2), calendar.get(5));
    }

    public static String getYearString(Resources resources, int i2) {
        StringBuffer stringBuffer = new StringBuffer();
        do {
            int i3 = i2 % 10;
            i2 /= 10;
            stringBuffer.insert(0, getDigitString(resources, i3));
        } while (i2 > 0);
        return stringBuffer.toString();
    }

    private static String isEasterDay(Resources resources, Calendar calendar) {
        int iIntValue;
        int i2 = calendar.get(1);
        HashMap<Integer, Integer> map = sEasterCache;
        int i3 = 31;
        int i4 = 4;
        if (map.containsKey(Integer.valueOf(i2))) {
            iIntValue = map.get(Integer.valueOf(i2)).intValue();
        } else {
            int i5 = i2 - 1900;
            int i6 = i5 % 19;
            int iFloor = (int) Math.floor(i5 / 4);
            int iFloor2 = (((i6 * 11) + 4) - ((int) Math.floor(((i6 * 7) + 1) / 19))) % 29;
            iIntValue = (25 - iFloor2) - ((((i5 + iFloor) + 31) - iFloor2) % 7);
            map.put(Integer.valueOf(i2), Integer.valueOf(iIntValue));
        }
        if (iIntValue == 0) {
            i4 = 3;
        } else if (iIntValue > 0) {
            i3 = iIntValue;
        } else {
            i3 = iIntValue + 31;
            i4 = 3;
        }
        Date date = new Date();
        date.setMonth(calendar.get(2));
        date.setDate(calendar.get(5));
        Date date2 = new Date();
        date2.setMonth(i4 - 1);
        date2.setDate(i3);
        int i7 = ((int) ((date2.getTime() - date.getTime()) / 86400000)) == 0 ? h.f2282m : 0;
        if (i7 > 0) {
            return resources.getString(i7);
        }
        return null;
    }

    static boolean isSolarLeapYear(int i2) {
        return (i2 % 4 == 0 && i2 % 100 != 0) || i2 % 400 == 0;
    }

    public static int[] lunarToSolar(int i2, int i3, int i4) {
        int lunarNewYearOffsetDays = getLunarNewYearOffsetDays(i2, i3, i4) + iSolarLunarOffsetTable[i2 - 1901];
        int i5 = isSolarLeapYear(i2) ? 366 : 365;
        if (lunarNewYearOffsetDays >= i5) {
            i2++;
            lunarNewYearOffsetDays -= i5;
        }
        int i6 = lunarNewYearOffsetDays + 1;
        int i7 = 1;
        while (lunarNewYearOffsetDays >= 0) {
            i6 = lunarNewYearOffsetDays + 1;
            lunarNewYearOffsetDays -= getSolarYearMonthDays(i2, i7);
            i7++;
        }
        return new int[]{i2, i7 - 1, i6};
    }

    public static final int mthDays(int i2, int i3) {
        return (((long) (LogType.LOG_TYPE_SAMPLING_INTERRUPT >> i3)) & luYearData[i2 + (-1900)]) == 0 ? 29 : 30;
    }

    public static int[] parseLunarDate(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        int[] iArr = new int[3];
        try {
            String[] strArrSplit = str.split(Constants.ACCEPT_TIME_SEPARATOR_SERVER);
            if (strArrSplit.length == 2) {
                iArr[0] = Integer.parseInt(strArrSplit[1].trim());
                iArr[1] = Integer.parseInt(strArrSplit[0].trim()) - 1;
                iArr[2] = 0;
            } else {
                if (strArrSplit.length != 3) {
                    return null;
                }
                iArr[0] = Integer.parseInt(strArrSplit[2].trim());
                iArr[1] = Integer.parseInt(strArrSplit[1].trim()) - 1;
                iArr[2] = Integer.parseInt(strArrSplit[0].trim());
            }
            return iArr;
        } catch (NumberFormatException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public static final int rMonth(int i2) {
        return (int) (luYearData[i2 - 1900] & 15);
    }

    public static final int rMthDays(int i2) {
        if (rMonth(i2) != 0) {
            return (luYearData[i2 + (-1900)] & ScenarioConfig.INTERACTIVE_ACTION_TYPE_VIDEOCALL_WEWORK) != 0 ? 30 : 29;
        }
        return 0;
    }

    public static String solar2lunar(Resources resources, int i2, int i3, int i4) {
        long[] jArrCalLunar = calLunar(i2, i3, i4);
        StringBuffer stringBuffer = new StringBuffer();
        if (jArrCalLunar[6] == 1) {
            stringBuffer.append(resources.getString(h.L0));
        }
        stringBuffer.append(getMonthString(resources, (int) jArrCalLunar[1]));
        stringBuffer.append(resources.getString(h.Z0));
        stringBuffer.append(getDayString(resources, (int) jArrCalLunar[2]));
        return stringBuffer.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final int yrDays(int i2) {
        int i3 = 348;
        for (int i4 = 32768; i4 > 8; i4 >>= 1) {
            if ((luYearData[i2 - 1900] & ((long) i4)) != 0) {
                i3++;
            }
        }
        return i3 + rMthDays(i2);
    }
}
