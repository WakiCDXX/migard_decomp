package com.miui.powerkeeper.batterylife.data;

import android.content.Context;
import android.provider.Settings;
import com.miui.powerkeeper.batterylife.BLUtils;
import com.miui.powerkeeper.batterylife.BatteryLifeChecker;
import com.miui.powerkeeper.utils.ShellUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class BatteryLife implements Serializable {
    public ArrayList<Data> dataList = new ArrayList<>();
    public String date;

    public static class Data implements Serializable {
        public static final int MAX_TYPES = 5;
        public static final int TYPE_COMPREHENSIVE = 4;
        public static final int TYPE_SCREEN_OFF = 1;
        public static final int TYPE_SCREEN_OFF_ACTIVE = 2;
        public static final int TYPE_SCREEN_OFF_AT_NIGHT = 3;
        public static final int TYPE_SCREEN_ON = 0;
        public int powerMode;
        public int[] count = {0, 0, 0, 0, 0};
        public int[] life = {0, 0, 0, 0, 0};
        public int[] lifeD = {0, 0, 0, 0, 0};
        public long[] duration = {0, 0, 0, 0, 0};
        public long[] totalDuration = {0, 0, 0, 0, 0};
        public double[] batteryDry = {0.0d, 0.0d, 0.0d, 0.0d, 0.0d};
        public ArrayList<MatchingData> matchingDatas = new ArrayList<>();

        public Data cloneData() {
            Data data = new Data();
            data.powerMode = this.powerMode;
            int[] iArr = this.life;
            data.life = Arrays.copyOf(iArr, iArr.length);
            int[] iArr2 = this.count;
            data.count = Arrays.copyOf(iArr2, iArr2.length);
            long[] jArr = this.duration;
            data.duration = Arrays.copyOf(jArr, jArr.length);
            double[] dArr = this.batteryDry;
            data.batteryDry = Arrays.copyOf(dArr, dArr.length);
            return data;
        }

        void resetCount() {
            for (int i2 = 0; i2 < this.count.length; i2++) {
                setCount(i2, this.life[i2]);
            }
        }

        public void setCount(int i2, int i3) {
            this.count[i2] = i3 > 0 ? 1 : 0;
        }
    }

    public static class FluctuatingState implements Serializable {
        public float[] fluctuatingStateComp = new float[6];
        public float[] fluctuatingStateOff = new float[6];
        public float[] fluctuatingStateOn = new float[6];
    }

    public static class MatchingData implements Serializable {
        public static final int TYPE_NIGHT_LONG_OFF_AT_NIGHT = 0;
        public static final int TYPE_NIGHT_LONG_ON = 1;
        public double consumption;
        public long duration;
        public double endCap;
        public int endCapLevel;
        public long endTime;
        public long endVoltage;
        public double startCap;
        public int startCapLevel;
        public long startTime;
        public long startVoltage;
        public int type;

        public static boolean match(int i2, Object obj) {
            return i2 == 0 && ((Long) obj).longValue() >= 14400;
        }
    }

    private <T> String arraysToString(T[] tArr) {
        if (tArr == null || tArr.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(tArr[0]);
        for (int i2 = 1; i2 < tArr.length; i2++) {
            sb.append("\t\t");
            sb.append(tArr[i2]);
        }
        sb.append(ShellUtils.COMMAND_LINE_END);
        return sb.toString();
    }

    private ArrayList<Data> cloneDataList(ArrayList<Data> arrayList) {
        ArrayList<Data> arrayList2 = new ArrayList<>();
        synchronized (this) {
            try {
                Iterator<Data> it = arrayList.iterator();
                while (it.hasNext()) {
                    arrayList2.add(it.next().cloneData());
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return arrayList2;
    }

    public void add(BatteryLife batteryLife) {
        if (batteryLife.dataList.size() == 0) {
            return;
        }
        if (this.dataList.size() == 0) {
            this.dataList = cloneDataList(batteryLife.dataList);
            return;
        }
        for (Data data : batteryLife.dataList) {
            Iterator<Data> it = this.dataList.iterator();
            while (true) {
                if (!it.hasNext()) {
                    this.dataList.add(data.cloneData());
                    break;
                }
                Data next = it.next();
                if (next.powerMode == data.powerMode) {
                    for (int i2 = 0; i2 < 5; i2++) {
                        int[] iArr = next.life;
                        iArr[i2] = iArr[i2] + data.life[i2];
                        int[] iArr2 = next.count;
                        iArr2[i2] = iArr2[i2] + data.count[i2];
                    }
                    break;
                }
            }
        }
    }

    public BatteryLife getAverage() {
        int i2;
        BatteryLife batteryLife = new BatteryLife();
        batteryLife.date = this.date;
        ArrayList<Data> arrayListCloneDataList = cloneDataList(this.dataList);
        batteryLife.dataList = arrayListCloneDataList;
        for (Data data : arrayListCloneDataList) {
            for (int i3 = 0; i3 < 5; i3++) {
                int[] iArr = data.life;
                int i4 = iArr[i3];
                if (i4 <= 0 || (i2 = data.count[i3]) < 1) {
                    iArr[i3] = 0;
                } else {
                    iArr[i3] = i4 / i2;
                }
            }
            data.resetCount();
        }
        return batteryLife;
    }

    public String getBatteryLifeInfoDate() {
        StringBuilder sb = new StringBuilder();
        sb.append("  date=" + this.date + ShellUtils.COMMAND_LINE_END);
        return sb.toString();
    }

    public String getBatteryLifeInfoPowerMode(Context context) {
        String str;
        int i2;
        String str2;
        char c2;
        Iterator<Data> it;
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("    %-20s%-24s%-24s%-36s%-36s%n", "PowerMode", "ScreenOn_Duration", "ScreenOn_totalDuration", "ScreenOn_BatteryLife(ChargeDesign)", "ScreenOn_BatteryLife(ChargeFull)"));
        Iterator<Data> it2 = this.dataList.iterator();
        while (true) {
            str = "    %-20s";
            i2 = 100;
            str2 = "%-24s";
            c2 = 0;
            if (!it2.hasNext()) {
                break;
            }
            Data next = it2.next();
            int i3 = next.powerMode;
            if (i3 == 100) {
                sb.append(String.format("    %-20s", BLUtils.getPowerModeNameEng(i3)));
                sb.append(String.format("%-24s", BLUtils.msToHourNoParentheses(next.duration[0] * 1000)));
                sb.append(String.format("%-24s", BLUtils.msToHourNoParentheses(next.totalDuration[0] * 1000)));
                sb.append(String.format("%-36s", String.format("%.2f", Double.valueOf(((double) next.lifeD[0]) / 3600.0d)) + "h"));
                sb.append(String.format("%-36s", String.format("%.2f", Double.valueOf(((double) next.life[0]) / 3600.0d)) + "h"));
                sb.append(ShellUtils.COMMAND_LINE_END);
                break;
            }
        }
        Iterator<Data> it3 = this.dataList.iterator();
        double d2 = 0.0d;
        double d3 = 0.0d;
        double d4 = 0.0d;
        long j2 = 0;
        long j3 = 0;
        long j4 = 0;
        long j5 = 0;
        long j6 = 0;
        boolean z2 = false;
        int i4 = 0;
        int i5 = 0;
        double d5 = 0.0d;
        while (it3.hasNext()) {
            Data next2 = it3.next();
            int i6 = next2.powerMode;
            if (i6 != i2) {
                sb.append(String.format(str, BLUtils.getPowerModeNameEng(i6)));
                sb.append(String.format(str2, BLUtils.msToHourNoParentheses(next2.duration[c2] * 1000)));
                sb.append(String.format(str2, BLUtils.msToHourNoParentheses(next2.totalDuration[c2] * 1000)));
                StringBuilder sb2 = new StringBuilder();
                String str3 = str;
                sb2.append(String.format("%.2f", Double.valueOf(((double) next2.lifeD[c2]) / 3600.0d)));
                sb2.append("h");
                sb.append(String.format("%-36s", sb2.toString()));
                StringBuilder sb3 = new StringBuilder();
                String str4 = str2;
                double d6 = d5;
                sb3.append(String.format("%.2f", Double.valueOf(((double) next2.life[c2]) / 3600.0d)));
                sb3.append("h");
                sb.append(String.format("%-36s", sb3.toString()));
                sb.append(ShellUtils.COMMAND_LINE_END);
                ArrayList<MatchingData> arrayList = next2.matchingDatas;
                if (arrayList == null || arrayList.size() <= 0) {
                    it = it3;
                    d5 = d6;
                } else {
                    d5 = d6;
                    for (MatchingData matchingData : next2.matchingDatas) {
                        if (matchingData.type == 0 && !z2) {
                            double d7 = matchingData.consumption;
                            long j7 = matchingData.duration;
                            double d8 = d7 / (j7 / 3600.0d);
                            long j8 = matchingData.startTime;
                            long j9 = matchingData.endTime;
                            double d9 = matchingData.startCap;
                            double d10 = matchingData.endCap;
                            int i7 = matchingData.startCapLevel;
                            int i8 = matchingData.endCapLevel;
                            j5 = matchingData.startVoltage;
                            i5 = i8;
                            j6 = matchingData.endVoltage;
                            z2 = true;
                            d2 = d8;
                            i4 = i7;
                            d4 = d10;
                            j4 = j9;
                            j2 = j7;
                            d5 = d7;
                            d3 = d9;
                            j3 = j8;
                        }
                        it3 = it3;
                    }
                    it = it3;
                }
                it3 = it;
                str2 = str4;
                str = str3;
                i2 = 100;
                c2 = 0;
            }
        }
        double d11 = d5;
        sb.append(ShellUtils.COMMAND_LINE_END);
        sb.append(String.format("    %-20s%-24s%s%n", "Current_Lastnight", "Consumption_Lastnight", "Duration_Lastnight"));
        if (z2) {
            sb.append(String.format("    %-20s%-24s%s%n", String.format("%.4f", Double.valueOf(d2)) + "mA", d11 + "mAh", BLUtils.msToHourNoParentheses(j2 * 1000)));
            sb.append("    startTime=" + j3 + ", endTime=" + j4 + ShellUtils.COMMAND_LINE_END);
            sb.append("    startCap=" + d3 + ", endCap=" + d4 + ShellUtils.COMMAND_LINE_END);
            sb.append("    startCapLevel=" + i4 + ", endCapLevel=" + i5 + ShellUtils.COMMAND_LINE_END);
            sb.append("    startVoltage=" + j5 + ", endVoltage=" + j6 + ShellUtils.COMMAND_LINE_END);
        } else {
            sb.append("    No Data\n");
        }
        sb.append("    FLAG_NIGHTCURRENT=" + Settings.System.getInt(context.getContentResolver(), BatteryLifeChecker.FLAG_NIGHTCURRENT, 32) + ShellUtils.COMMAND_LINE_END);
        sb.append(ShellUtils.COMMAND_LINE_END);
        return sb.toString();
    }

    public void subtract(BatteryLife batteryLife) {
        if (this.dataList.size() == 0) {
            return;
        }
        for (Data data : batteryLife.dataList) {
            for (Data data2 : this.dataList) {
                if (data2.powerMode == data.powerMode) {
                    for (int i2 = 0; i2 < 5; i2++) {
                        int[] iArr = data2.life;
                        iArr[i2] = iArr[i2] - data.life[i2];
                        int[] iArr2 = data2.count;
                        iArr2[i2] = iArr2[i2] - data.count[i2];
                    }
                    break;
                }
            }
        }
    }

    public String toString(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(str + "date=" + this.date + ShellUtils.COMMAND_LINE_END);
        for (Data data : this.dataList) {
            sb.append(str + "powerMode: " + data.powerMode + ShellUtils.COMMAND_LINE_END);
            sb.append(str);
            sb.append("  Type    \t\tS-On \t\tS-Off \t\tS-OffActive \tS-OffAtNight \tComp\n");
            sb.append(str);
            sb.append("  count[]=\t\t");
            sb.append(Arrays.toString(data.count));
            sb.append(str);
            sb.append("  life(s)[]=\t\t");
            sb.append(Arrays.toString(data.life));
            sb.append(str);
            sb.append("  duration(s)[]=\t");
            sb.append(Arrays.toString(data.duration));
            sb.append(str);
            sb.append("  batteryDry[]=\t\t");
            sb.append(Arrays.toString(data.batteryDry));
            if (data.matchingDatas.size() > 0) {
                sb.append("  matchingData:");
                for (MatchingData matchingData : data.matchingDatas) {
                    sb.append(str);
                    sb.append("    data(t, d, c):" + matchingData.type + ", " + matchingData.duration + "s, " + matchingData.consumption + "mAh\n");
                }
            }
        }
        return sb.toString();
    }

    public String toString() {
        return toString("");
    }
}
