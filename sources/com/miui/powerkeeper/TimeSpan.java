package com.miui.powerkeeper;

import android.util.Log;
import android.util.Pair;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class TimeSpan {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final boolean f286c = Log.isLoggable("PowerKeeper.Time", 3);

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private Pair<Integer, Integer> f287a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private List<a> f288b = new ArrayList();

    public interface a {
        void inTimeSpan();

        void outTimeSpan();
    }

    public TimeSpan(int i2, int i3) {
        this.f287a = new Pair<>(Integer.valueOf(i2), Integer.valueOf(i3));
    }

    public static boolean c() {
        return new TimeSpan(23, 7).d(System.currentTimeMillis());
    }

    private boolean d(long j2) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(j2);
        int i2 = calendar.get(11);
        boolean z2 = f286c;
        if (z2) {
            Log.d("PowerKeeper.Time", "hour: " + i2 + ", first: " + this.f287a.first + ", second:" + this.f287a.second);
        }
        if (((Integer) this.f287a.first).intValue() < ((Integer) this.f287a.second).intValue() && i2 >= ((Integer) this.f287a.first).intValue() && i2 < ((Integer) this.f287a.second).intValue()) {
            if (z2) {
                Log.d("PowerKeeper.Time", "in span");
            }
            return true;
        }
        if ((i2 < 0 || i2 >= ((Integer) this.f287a.second).intValue()) && i2 < ((Integer) this.f287a.first).intValue()) {
            return false;
        }
        if (z2) {
            Log.d("PowerKeeper.Time", "in span 2");
        }
        return true;
    }

    public void a(a aVar) {
        if (this.f288b.contains(aVar)) {
            return;
        }
        this.f288b.add(aVar);
    }

    public long b(long j2) {
        boolean z2 = f286c;
        if (z2) {
            Log.d("PowerKeeper.Time", "getNextAlarmTime now is: " + j2);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(j2);
        int iIntValue = d(j2) ? ((((Integer) this.f287a.second).intValue() + 24) - calendar.get(11)) % 24 : ((((Integer) this.f287a.first).intValue() + 24) - calendar.get(11)) % 24;
        if (z2) {
            Log.d("PowerKeeper.Time", "current hour: " + calendar.get(11) + ", add interval: " + iIntValue);
        }
        calendar.add(11, iIntValue);
        calendar.set(12, 0);
        calendar.set(13, 0);
        return calendar.getTimeInMillis();
    }

    public void e(long j2) {
        if (f286c) {
            Log.d("PowerKeeper.Time", "notify: " + j2);
        }
        if (d(j2)) {
            Iterator<a> it = this.f288b.iterator();
            while (it.hasNext()) {
                it.next().inTimeSpan();
            }
        } else {
            Iterator<a> it2 = this.f288b.iterator();
            while (it2.hasNext()) {
                it2.next().outTimeSpan();
            }
        }
    }

    public void f(a aVar) {
        if (this.f288b.contains(aVar)) {
            this.f288b.remove(aVar);
        }
    }
}
