package com.miui.powerkeeper.batterylife.topapps;

import android.content.Context;
import android.util.SparseArray;
import com.miui.powerkeeper.batterylife.BLUtils;
import com.miui.powerkeeper.batterylife.data.MyBatterySipper;
import com.miui.powerkeeper.batterylife.systeminfo.BSHelper;
import com.miui.powerkeeper.utils.ShellUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class TopAppsManager {
    public static final String TAG = "BL-TopAppsManager";
    private Context mContext;
    private long mStartTime;
    private List<MyBatterySipper> mUsageList = new ArrayList();
    private SparseArray<MyBatterySipper> mUsageMap = new SparseArray<>();
    private double mStartPower = -1.0d;

    public TopAppsManager(Context context) {
        this.mContext = context;
    }

    public void addLastDataToMap(int i2) {
        long jCurrentTimeMillis = System.currentTimeMillis();
        double currentBatteryCap = BSHelper.getCurrentBatteryCap();
        if (i2 < 10000) {
            return;
        }
        long j2 = this.mStartTime;
        if (jCurrentTimeMillis - j2 < 60000 && currentBatteryCap <= this.mStartPower && j2 != -1) {
            MyBatterySipper myBatterySipper = this.mUsageMap.get(i2);
            if (myBatterySipper == null) {
                MyBatterySipper myBatterySipper2 = new MyBatterySipper();
                myBatterySipper2.setUid(i2);
                myBatterySipper2.setTotalForegroundTime(jCurrentTimeMillis - this.mStartTime);
                this.mUsageMap.put(i2, myBatterySipper2);
            } else {
                myBatterySipper.setTotalForegroundTime((jCurrentTimeMillis - this.mStartTime) + myBatterySipper.getTotalForegroundTime());
            }
        }
        long j3 = this.mStartTime;
        if (j3 == -1 || jCurrentTimeMillis - j3 < 60000 || currentBatteryCap >= this.mStartPower) {
            this.mStartTime = jCurrentTimeMillis;
            this.mStartPower = currentBatteryCap;
            return;
        }
        MyBatterySipper myBatterySipper3 = this.mUsageMap.get(i2);
        if (myBatterySipper3 != null) {
            myBatterySipper3.setForegroundTime((jCurrentTimeMillis - this.mStartTime) + myBatterySipper3.getForegroundTime());
            myBatterySipper3.setTotalForegroundTime((jCurrentTimeMillis - this.mStartTime) + myBatterySipper3.getTotalForegroundTime());
            myBatterySipper3.setTotalPower((this.mStartPower - currentBatteryCap) + myBatterySipper3.getTotalPower());
        } else {
            MyBatterySipper myBatterySipper4 = new MyBatterySipper();
            myBatterySipper4.setUid(i2);
            myBatterySipper4.setForegroundTime(jCurrentTimeMillis - this.mStartTime);
            myBatterySipper4.setTotalForegroundTime(jCurrentTimeMillis - this.mStartTime);
            myBatterySipper4.setTotalPower(this.mStartPower - currentBatteryCap);
            this.mUsageMap.put(i2, myBatterySipper4);
        }
    }

    public String dump() {
        StringBuilder sb = new StringBuilder();
        sb.append("    App Usage:\n");
        sb.append(String.format("    %-40s%-8s%-16s%-20s%-20s%s%n", "PackageName", "Uid", "Current", "Consumption", "ForegroundTime", "totalForegroundTime"));
        for (int i2 = 0; i2 < this.mUsageMap.size(); i2++) {
            int uid = this.mUsageMap.valueAt(i2).getUid();
            sb.append(String.format("    %-40s", BLUtils.getPackageForUid(this.mContext, uid)));
            sb.append(String.format("%-8s", Integer.valueOf(uid)));
            sb.append(String.format("%-16s", String.format("%.4f", Double.valueOf((this.mUsageMap.valueAt(i2).getTotalPower() * 1000.0d) / (this.mUsageMap.valueAt(i2).getForegroundTime() / 3600))) + "mA"));
            sb.append(String.format("%-20s", this.mUsageMap.valueAt(i2).getTotalPower() + "mAh"));
            sb.append(String.format("%-20s", BLUtils.msToHourNoParentheses(this.mUsageMap.valueAt(i2).getForegroundTime())));
            sb.append(BLUtils.msToHourNoParentheses(this.mUsageMap.valueAt(i2).getTotalForegroundTime()) + ShellUtils.COMMAND_LINE_END);
        }
        return sb.toString();
    }

    public List<MyBatterySipper> getUsageList() {
        return this.mUsageList;
    }

    public String getYesterdayTopAppsDump() {
        StringBuilder sb = new StringBuilder();
        sb.append("    App Usage:\n");
        sb.append(String.format("    %-40s%-8s%-16s%-20s%-20s%s%n", "PackageName", "Uid", "Current", "Consumption", "ForegroundTime", "totalForegroundTime"));
        for (int i2 = 0; i2 < this.mUsageList.size(); i2++) {
            int uid = this.mUsageList.get(i2).getUid();
            sb.append(String.format("    %-40s", BLUtils.getPackageForUid(this.mContext, uid)));
            sb.append(String.format("%-8s", Integer.valueOf(uid)));
            sb.append(String.format("%-16s", String.format("%.4f", Double.valueOf((this.mUsageList.get(i2).getTotalPower() * 1000.0d) / (this.mUsageList.get(i2).getForegroundTime() / 3600))) + "mA"));
            sb.append(String.format("%-20s", this.mUsageList.get(i2).getTotalPower() + "mAh"));
            sb.append(String.format("%-20s", BLUtils.msToHourNoParentheses(this.mUsageList.get(i2).getForegroundTime())));
            sb.append(BLUtils.msToHourNoParentheses(this.mUsageList.get(i2).getTotalForegroundTime()) + ShellUtils.COMMAND_LINE_END);
        }
        return sb.toString();
    }

    public void sortDataToList() {
        this.mUsageList.clear();
        for (int i2 = 0; i2 < this.mUsageMap.size(); i2++) {
            this.mUsageList.add(this.mUsageMap.valueAt(i2));
        }
        Collections.sort(this.mUsageList);
        this.mUsageMap.clear();
    }

    public void startNewData() {
        this.mStartTime = System.currentTimeMillis();
        this.mStartPower = BSHelper.getCurrentBatteryCap();
    }
}
