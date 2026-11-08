package miui.date;

import android.app.Application;
import android.content.res.Resources;
import android.text.format.DateFormat;
import com.miui.internal.app.SystemApplication;
import java.util.TimeZone;
import miui.util.Pools;
import q.g;
import q.h;

/* JADX INFO: loaded from: classes.dex */
public class DateUtils {
    private static final Pools.Pool<Calendar> CALENDAR_POOL = Pools.createSoftReferencePool(new Pools.Manager<Calendar>() { // from class: miui.date.DateUtils.1
        @Override // miui.util.Pools.Manager
        public Calendar createInstance() {
            return new Calendar();
        }
    }, 1);
    public static final int FORMAT_12HOUR = 16;
    public static final int FORMAT_24HOUR = 32;
    public static final int FORMAT_ABBREV_ALL = 28672;
    public static final int FORMAT_ABBREV_MONTH = 4096;
    public static final int FORMAT_ABBREV_TIME = 16384;
    public static final int FORMAT_ABBREV_WEEKDAY = 8192;
    public static final int FORMAT_NO_AM_PM = 64;
    public static final int FORMAT_NUMERIC_DATE = 32768;
    public static final int FORMAT_SHOW_BRIEF_TIME = 12;
    public static final int FORMAT_SHOW_DATE = 896;
    public static final int FORMAT_SHOW_HOUR = 8;
    public static final int FORMAT_SHOW_MILLISECOND = 1;
    public static final int FORMAT_SHOW_MINUTE = 4;
    public static final int FORMAT_SHOW_MONTH = 256;
    public static final int FORMAT_SHOW_MONTH_DAY = 128;
    public static final int FORMAT_SHOW_SECOND = 2;
    public static final int FORMAT_SHOW_TIME = 15;
    public static final int FORMAT_SHOW_TIME_ZONE = 2048;
    public static final int FORMAT_SHOW_WEEKDAY = 1024;
    public static final int FORMAT_SHOW_YEAR = 512;

    protected DateUtils() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate utility class");
    }

    public static String formatDateTime(long j2, int i2) {
        StringBuilder sbAcquire = Pools.getStringBuilderPool().acquire();
        String string = formatDateTime(sbAcquire, j2, i2, null).toString();
        Pools.getStringBuilderPool().release(sbAcquire);
        return string;
    }

    public static String formatRelativeTime(long j2, boolean z2) {
        StringBuilder sbAcquire = Pools.getStringBuilderPool().acquire();
        String string = formatRelativeTime(sbAcquire, j2, z2, null).toString();
        Pools.getStringBuilderPool().release(sbAcquire);
        return string;
    }

    private static int getDatePatternResId(int i2) {
        if ((i2 & 32768) == 32768) {
            if ((i2 & 512) == 512) {
                if ((i2 & 256) == 256) {
                    return (i2 & 128) == 128 ? h.f2295z : h.f2294y;
                }
                return h.f2293x;
            }
            if ((i2 & 256) == 256) {
                return (i2 & 128) == 128 ? h.f2292w : h.f2291v;
            }
            if ((i2 & 128) == 128) {
                return h.f2290u;
            }
            throw new IllegalArgumentException("no any time date");
        }
        if ((i2 & 4096) == 4096) {
            if ((i2 & 512) == 512) {
                if ((i2 & 256) == 256) {
                    return (i2 & 128) == 128 ? h.D : h.C;
                }
                return h.H;
            }
            if ((i2 & 256) == 256) {
                return (i2 & 128) == 128 ? h.B : h.A;
            }
            if ((i2 & 128) == 128) {
                return h.f2285p;
            }
            throw new IllegalArgumentException("no any time date");
        }
        if ((i2 & 512) == 512) {
            if ((i2 & 256) == 256) {
                return (i2 & 128) == 128 ? h.f2289t : h.f2288s;
            }
            return h.H;
        }
        if ((i2 & 256) == 256) {
            return (i2 & 128) == 128 ? h.f2287r : h.f2286q;
        }
        if ((i2 & 128) == 128) {
            return h.f2285p;
        }
        throw new IllegalArgumentException("no any time date");
    }

    private static int getFormatResId(int i2) {
        if ((i2 & 1024) == 1024) {
            if ((i2 & 896) != 0) {
                if ((i2 & 15) != 0) {
                    return (i2 & 2048) == 2048 ? h.f2275g0 : h.f2273f0;
                }
                return (i2 & 2048) == 2048 ? h.f2277h0 : h.f2271e0;
            }
            if ((i2 & 15) != 0) {
                return (i2 & 2048) == 2048 ? h.l0 : h.k0;
            }
            return (i2 & 2048) == 2048 ? h.m0 : h.f2269d0;
        }
        if ((i2 & 896) != 0) {
            if ((i2 & 15) != 0) {
                return (i2 & 2048) == 2048 ? h.F : h.E;
            }
            return (i2 & 2048) == 2048 ? h.G : h.f2284o;
        }
        if ((i2 & 15) != 0) {
            return (i2 & 2048) == 2048 ? h.f2265b0 : h.I;
        }
        return (i2 & 2048) == 2048 ? h.f2267c0 : h.f2283n;
    }

    /* JADX WARN: Code duplicated, block: B:21:0x003b A[PHI: r0
      0x003b: PHI (r0v8 int) = (r0v6 int), (r0v7 int), (r0v7 int), (r0v6 int) binds: [B:15:0x0028, B:17:0x0032, B:19:0x0036, B:13:0x0024] A[DONT_GENERATE, DONT_INLINE]] */
    private static int getTimePatternResId(Calendar calendar, int i2) {
        if ((i2 & FORMAT_ABBREV_TIME) == 16384 && (((i2 & 1) != 1 || calendar.get(22) == 0) && (i2 & 14) != 0)) {
            int i3 = i2 & (-2);
            if (((i2 & 2) != 2 || calendar.get(21) == 0) && (i2 & 12) != 0) {
                i3 = i2 & (-4);
                if (calendar.get(20) != 0 || (i2 & 8) == 0) {
                    i2 = i3;
                } else {
                    i2 &= -8;
                }
            } else {
                i2 = i3;
            }
        }
        if ((i2 & 8) != 8) {
            if ((i2 & 4) == 4) {
                if ((i2 & 2) == 2) {
                    return (i2 & 1) == 1 ? h.Y : h.X;
                }
                return h.W;
            }
            if ((i2 & 2) == 2) {
                return (i2 & 1) == 1 ? h.f2263a0 : h.Z;
            }
            if ((i2 & 1) == 1) {
                return h.V;
            }
            throw new IllegalArgumentException("no any time date");
        }
        if ((i2 & 16) != 16) {
            if ((i2 & 4) != 4) {
                return h.R;
            }
            if ((i2 & 2) == 2) {
                return (i2 & 1) == 1 ? h.U : h.T;
            }
            return h.S;
        }
        if ((i2 & 64) == 64) {
            if ((i2 & 4) != 4) {
                return h.J;
            }
            if ((i2 & 2) == 2) {
                return (i2 & 1) == 1 ? h.N : h.M;
            }
            return h.K;
        }
        if ((i2 & 4) != 4) {
            return h.Q;
        }
        if ((i2 & 2) == 2) {
            return (i2 & 1) == 1 ? h.O : h.P;
        }
        return h.L;
    }

    private static int getWeekdayPatternResId(int i2) {
        return (i2 & FORMAT_ABBREV_WEEKDAY) == 8192 ? h.j0 : h.i0;
    }

    public static String formatDateTime(long j2, int i2, TimeZone timeZone) {
        StringBuilder sbAcquire = Pools.getStringBuilderPool().acquire();
        String string = formatDateTime(sbAcquire, j2, i2, timeZone).toString();
        Pools.getStringBuilderPool().release(sbAcquire);
        return string;
    }

    public static String formatRelativeTime(long j2, boolean z2, TimeZone timeZone) {
        StringBuilder sbAcquire = Pools.getStringBuilderPool().acquire();
        String string = formatRelativeTime(sbAcquire, j2, z2, timeZone).toString();
        Pools.getStringBuilderPool().release(sbAcquire);
        return string;
    }

    public static StringBuilder formatDateTime(StringBuilder sb, long j2, int i2) {
        return formatDateTime(sb, j2, i2, null);
    }

    public static StringBuilder formatRelativeTime(StringBuilder sb, long j2, boolean z2) {
        return formatRelativeTime(sb, j2, z2, null);
    }

    public static StringBuilder formatDateTime(StringBuilder sb, long j2, int i2, TimeZone timeZone) {
        Application currentApplication = SystemApplication.getCurrentApplication();
        if ((i2 & 16) == 0 && (i2 & 32) == 0) {
            i2 |= DateFormat.is24HourFormat(currentApplication) ? 32 : 16;
        }
        String string = currentApplication.getString(getFormatResId(i2));
        StringBuilder sbAcquire = Pools.getStringBuilderPool().acquire();
        Calendar calendarAcquire = CALENDAR_POOL.acquire();
        calendarAcquire.setTimeZone(timeZone);
        calendarAcquire.setTimeInMillis(j2);
        int length = string.length();
        for (int i3 = 0; i3 < length; i3++) {
            char cCharAt = string.charAt(i3);
            if (cCharAt == 'D') {
                sbAcquire.append(currentApplication.getString(getDatePatternResId(i2)));
            } else if (cCharAt == 'T') {
                sbAcquire.append(currentApplication.getString(getTimePatternResId(calendarAcquire, i2)));
            } else if (cCharAt != 'W') {
                sbAcquire.append(cCharAt);
            } else {
                sbAcquire.append(currentApplication.getString(getWeekdayPatternResId(i2)));
            }
        }
        calendarAcquire.format(sb, sbAcquire);
        Pools.getStringBuilderPool().release(sbAcquire);
        CALENDAR_POOL.release(calendarAcquire);
        return sb;
    }

    /* JADX WARN: Code duplicated, block: B:55:0x00f0  */
    /* JADX WARN: Code duplicated, block: B:57:0x00f4 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:58:0x00f6  */
    /* JADX WARN: Code duplicated, block: B:59:0x00f9  */
    /* JADX WARN: Code duplicated, block: B:61:0x0100 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:62:0x0102  */
    /* JADX WARN: Code duplicated, block: B:63:0x0105  */
    public static StringBuilder formatRelativeTime(StringBuilder sb, long j2, boolean z2, TimeZone timeZone) {
        int i2;
        int i3;
        int i4;
        long jCurrentTimeMillis = System.currentTimeMillis();
        boolean z3 = jCurrentTimeMillis >= j2;
        long jAbs = Math.abs(jCurrentTimeMillis - j2) / 60000;
        Resources resources = SystemApplication.getCurrentApplication().getResources();
        if (jAbs <= 60 && !z2) {
            if (z3) {
                if (jAbs == 60) {
                    i4 = g.f2254a;
                } else if (jAbs == 30) {
                    i4 = g.f2255b;
                } else if (jAbs == 0) {
                    i4 = g.f2260g;
                } else {
                    i4 = g.f2261h;
                }
            } else if (jAbs == 60) {
                i4 = g.f2256c;
            } else if (jAbs == 30) {
                i4 = g.f2257d;
            } else if (jAbs == 0) {
                i4 = g.f2258e;
            } else {
                i4 = g.f2259f;
            }
            sb.append(String.format(resources.getQuantityString(i4, (int) jAbs), Long.valueOf(jAbs)));
        } else {
            Pools.Pool<Calendar> pool = CALENDAR_POOL;
            Calendar calendarAcquire = pool.acquire();
            calendarAcquire.setTimeZone(timeZone);
            calendarAcquire.setTimeInMillis(jCurrentTimeMillis);
            int i5 = calendarAcquire.get(1);
            int i6 = calendarAcquire.get(12);
            int i7 = calendarAcquire.get(14);
            calendarAcquire.setTimeInMillis(j2);
            boolean z4 = i5 == calendarAcquire.get(1);
            if (z4 && i6 == calendarAcquire.get(12)) {
                formatDateTime(sb, j2, 12300, timeZone);
            } else if (z4 && Math.abs(i6 - calendarAcquire.get(12)) < 2) {
                sb.append(resources.getString(z3 ? h.P1 : h.G1));
                sb.append(' ');
                formatDateTime(sb, j2, 12300, timeZone);
            } else if (z4 && Math.abs(i6 - calendarAcquire.get(12)) < 7) {
                if (z3 == (i7 > calendarAcquire.get(14))) {
                    formatDateTime(sb, j2, 13324, timeZone);
                } else if (z4) {
                    if (z2) {
                        i3 = 396;
                    } else {
                        i3 = 384;
                    }
                    formatDateTime(sb, j2, i3 | 12288, timeZone);
                } else {
                    if (z2) {
                        i2 = 908;
                    } else {
                        i2 = 896;
                    }
                    formatDateTime(sb, j2, i2 | 12288, timeZone);
                }
            } else if (z4) {
                if (z2) {
                    i3 = 396;
                } else {
                    i3 = 384;
                }
                formatDateTime(sb, j2, i3 | 12288, timeZone);
            } else {
                if (z2) {
                    i2 = 908;
                } else {
                    i2 = 896;
                }
                formatDateTime(sb, j2, i2 | 12288, timeZone);
            }
            pool.release(calendarAcquire);
        }
        return sb;
    }
}
