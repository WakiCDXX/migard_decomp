package com.miui.powerkeeper.powerchecker;

import android.os.Parcel;
import android.util.Log;
import com.android.internal.app.IBatteryStats;
import com.miui.powerkeeper.customerpower.CustomerPowerConstant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class PowerCheckerUtils {
    private static final long BYTES_PER_GB = 1073741824;
    private static final long BYTES_PER_KB = 1024;
    private static final long BYTES_PER_MB = 1048576;
    private static final String TAG = "PowerCheckerUtils";
    private static StringBuilder mFormatBuilder = new StringBuilder(32);
    private static Formatter mFormatter = new Formatter(mFormatBuilder);
    static final Comparator<TimerEntry> timerComparator = new Comparator<TimerEntry>() { // from class: com.miui.powerkeeper.powerchecker.PowerCheckerUtils.1
        @Override // java.util.Comparator
        public int compare(TimerEntry timerEntry, TimerEntry timerEntry2) {
            long j2 = timerEntry.mTime;
            long j3 = timerEntry2.mTime;
            if (j2 < j3) {
                return 1;
            }
            return j2 > j3 ? -1 : 0;
        }
    };

    static final class TimerEntry {
        final int mCount;
        final int mId;
        final String mName;
        final long mTime;

        TimerEntry(String str, int i2, int i3, long j2) {
            this.mName = str;
            this.mId = i2;
            this.mCount = i3;
            this.mTime = j2;
        }
    }

    public static final String formatBytesLocked(long j2) {
        mFormatBuilder.setLength(0);
        if (j2 < BYTES_PER_KB) {
            return j2 + com.xiaomi.onetrack.api.e.f1070a;
        }
        if (j2 < 1048576) {
            mFormatter.format("%.2fKB", Double.valueOf(j2 / 1024.0d));
            return mFormatBuilder.toString();
        }
        if (j2 < BYTES_PER_GB) {
            mFormatter.format("%.2fMB", Double.valueOf(j2 / 1048576.0d));
            return mFormatBuilder.toString();
        }
        mFormatter.format("%.2fGB", Double.valueOf(j2 / 1.073741824E9d));
        return mFormatBuilder.toString();
    }

    public static final String formatRatioLocked(long j2, long j3) {
        if (j3 == 0) {
            return "--%";
        }
        mFormatBuilder.setLength(0);
        mFormatter.format("%.1f%%", Float.valueOf((j2 / j3) * 100.0f));
        return mFormatBuilder.toString();
    }

    public static final void formatTime(StringBuilder sb, long j2) {
        long j3 = j2 / 100;
        formatTimeRaw(sb, j3);
        sb.append((j2 - (j3 * 100)) * 10);
        sb.append("ms ");
    }

    public static final void formatTimeMs(StringBuilder sb, long j2) {
        long j3 = j2 / 1000;
        formatTimeRaw(sb, j3);
        sb.append(j2 - (j3 * 1000));
        sb.append("ms ");
    }

    private static final void formatTimeRaw(StringBuilder sb, long j2) {
        long j3 = j2 / 86400;
        if (j3 != 0) {
            sb.append(j3);
            sb.append("d ");
        }
        long j4 = j3 * 86400;
        long j5 = (j2 - j4) / 3600;
        if (j5 != 0 || j4 != 0) {
            sb.append(j5);
            sb.append("h ");
        }
        long j6 = j4 + (j5 * 3600);
        long j7 = (j2 - j6) / 60;
        if (j7 != 0 || j6 != 0) {
            sb.append(j7);
            sb.append("m ");
        }
        long j8 = j6 + (j7 * 60);
        if (j2 == 0 && j8 == 0) {
            return;
        }
        sb.append(j2 - j8);
        sb.append("s ");
    }

    public static ArrayList<TimerEntry> getKernelWakelockStats(IBatteryStats iBatteryStats) {
        ArrayList<TimerEntry> arrayList = new ArrayList<>();
        Parcel parcelObtain = Parcel.obtain();
        try {
            try {
                byte[] kernelWakelockStats = iBatteryStats.getKernelWakelockStats();
                if (kernelWakelockStats != null) {
                    parcelObtain.unmarshall(kernelWakelockStats, 0, kernelWakelockStats.length);
                    parcelObtain.setDataPosition(0);
                    int i2 = parcelObtain.readInt();
                    for (int i3 = 0; i3 < i2; i3++) {
                        String string = parcelObtain.readString();
                        int i4 = parcelObtain.readInt();
                        long j2 = parcelObtain.readLong();
                        if (string != null && string.length() > 0 && j2 > 0) {
                            if (CustomerPowerConstant.DEBUG) {
                                Log.d(TAG, "kernel wakelock wakelockName = " + string + ", length = " + string.length() + ", wakelockCount = " + i4 + ", wakelockTime = " + j2);
                            }
                            arrayList.add(new TimerEntry(string, 0, i4, j2));
                        }
                    }
                }
            } catch (Exception e2) {
                Log.e(TAG, e2.getMessage());
            }
            return arrayList;
        } finally {
            parcelObtain.recycle();
        }
    }

    public static ArrayList<TimerEntry> getWakeupReasonStats(IBatteryStats iBatteryStats) {
        ArrayList<TimerEntry> arrayList = new ArrayList<>();
        Parcel parcelObtain = Parcel.obtain();
        try {
            try {
                byte[] wakeupReasonStats = iBatteryStats.getWakeupReasonStats();
                if (wakeupReasonStats != null) {
                    parcelObtain.unmarshall(wakeupReasonStats, 0, wakeupReasonStats.length);
                    parcelObtain.setDataPosition(0);
                    int i2 = parcelObtain.readInt();
                    for (int i3 = 0; i3 < i2; i3++) {
                        String string = parcelObtain.readString();
                        int i4 = parcelObtain.readInt();
                        long j2 = parcelObtain.readLong();
                        if (string != null && string.length() > 0 && j2 > 0) {
                            if (CustomerPowerConstant.DEBUG) {
                                Log.d(TAG, "kernel wakelock wakelockName = " + string + ", length = " + string.length() + ", wakelockCount = " + i4 + ", wakelockTime = " + j2);
                            }
                            arrayList.add(new TimerEntry(string, 0, i4, j2));
                        }
                    }
                }
            } catch (Exception e2) {
                Log.e(TAG, e2.getMessage());
            }
            return arrayList;
        } finally {
            parcelObtain.recycle();
        }
    }

    public static String makemAh(double d2) {
        String str;
        if (d2 == 0.0d) {
            return "0";
        }
        if (d2 < 1.0E-5d) {
            str = "%.8f";
        } else if (d2 < 1.0E-4d) {
            str = "%.7f";
        } else if (d2 < 0.001d) {
            str = "%.6f";
        } else if (d2 < 0.01d) {
            str = "%.5f";
        } else if (d2 < 0.1d) {
            str = "%.4f";
        } else if (d2 < 1.0d) {
            str = "%.3f";
        } else if (d2 < 10.0d) {
            str = "%.2f";
        } else {
            str = d2 < 100.0d ? "%.1f" : "%.0f";
        }
        return String.format(Locale.ENGLISH, str, Double.valueOf(d2));
    }

    public static void sortTimerEntry(ArrayList<TimerEntry> arrayList) {
        Collections.sort(arrayList, timerComparator);
    }
}
