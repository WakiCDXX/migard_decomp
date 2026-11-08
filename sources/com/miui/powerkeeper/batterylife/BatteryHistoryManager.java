package com.miui.powerkeeper.batterylife;

import android.content.Context;
import android.os.BatteryConsumer;
import android.os.BatteryStats;
import android.os.BatteryStatsManager;
import android.os.BatteryUsageStats;
import android.os.BatteryUsageStatsQuery;
import android.os.UidBatteryConsumer;
import android.util.Log;
import android.util.Range;
import com.miui.powerkeeper.utils.ShellUtils;
import com.xiaomi.mipush.sdk.Constants;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;

/* JADX INFO: loaded from: classes.dex */
public class BatteryHistoryManager {
    private static final String TAG = "BatteryHistoryManager";
    private static final String pkgSpecific = "com.ss.android.ugc.aweme";
    private BatteryStatsManager mBatteryStatsManager;
    private Context mContext;

    BatteryHistoryManager(Context context) {
        this.mContext = context;
        this.mBatteryStatsManager = (BatteryStatsManager) context.getSystemService(BatteryStatsManager.class);
    }

    private BatteryUsageStats getBatteryUsageStats() {
        try {
            return this.mBatteryStatsManager.getBatteryUsageStats(new BatteryUsageStatsQuery.Builder().build());
        } catch (RuntimeException e2) {
            Log.e(TAG, "getBatteryUsageStats", e2);
            return new BatteryUsageStats.Builder(new String[0]).build();
        }
    }

    public Map<String, Object> doUploadBatteryStats() {
        HashMap map = new HashMap();
        BatteryUsageStats batteryUsageStats = getBatteryUsageStats();
        map.put("Computed drain:", BatteryStats.formatCharge(batteryUsageStats.getConsumedPower()));
        List<UidBatteryConsumer> uidBatteryConsumers = batteryUsageStats.getUidBatteryConsumers();
        uidBatteryConsumers.sort(Comparator.comparingDouble(new ToDoubleFunction() { // from class: com.miui.powerkeeper.batterylife.a
            @Override // java.util.function.ToDoubleFunction
            public final double applyAsDouble(Object obj) {
                return ((BatteryConsumer) obj).getConsumedPower();
            }
        }).reversed());
        for (UidBatteryConsumer uidBatteryConsumer : uidBatteryConsumers) {
            int uid = uidBatteryConsumer.getUid();
            if (BLUtils.getPackageForUid(this.mContext, uid).equalsIgnoreCase(pkgSpecific)) {
                map.put("UID:", Integer.valueOf(uid));
                map.put("totalPower", BatteryStats.formatCharge(uidBatteryConsumer.getConsumedPower()) + ShellUtils.COMMAND_LINE_END);
                long usageDurationMillis = uidBatteryConsumer.getUsageDurationMillis(0);
                double consumedPower = uidBatteryConsumer.getConsumedPower(0);
                map.put("screenTime", Long.valueOf(usageDurationMillis));
                map.put("screenConsumedPower", BatteryStats.formatCharge(consumedPower));
                long usageDurationMillis2 = uidBatteryConsumer.getUsageDurationMillis(1);
                double consumedPower2 = uidBatteryConsumer.getConsumedPower(1);
                map.put("cpuTime", Long.valueOf(usageDurationMillis2));
                map.put("cpuConsumedPower", BatteryStats.formatCharge(consumedPower2));
                long usageDurationMillis3 = uidBatteryConsumer.getUsageDurationMillis(2);
                double consumedPower3 = uidBatteryConsumer.getConsumedPower(2);
                map.put("btTime", Long.valueOf(usageDurationMillis3));
                map.put("btConsumedPower", BatteryStats.formatCharge(consumedPower3));
                long usageDurationMillis4 = uidBatteryConsumer.getUsageDurationMillis(3);
                double consumedPower4 = uidBatteryConsumer.getConsumedPower(3);
                map.put("cameraTime", Long.valueOf(usageDurationMillis4));
                map.put("cameraConsumedPower", BatteryStats.formatCharge(consumedPower4));
                long usageDurationMillis5 = uidBatteryConsumer.getUsageDurationMillis(4);
                double consumedPower5 = uidBatteryConsumer.getConsumedPower(4);
                map.put("audioTime", Long.valueOf(usageDurationMillis5));
                map.put("audioConsumedPower", BatteryStats.formatCharge(consumedPower5));
                long usageDurationMillis6 = uidBatteryConsumer.getUsageDurationMillis(5);
                double consumedPower6 = uidBatteryConsumer.getConsumedPower(5);
                map.put("videoTime", Long.valueOf(usageDurationMillis6));
                map.put("videoConsumedPower", BatteryStats.formatCharge(consumedPower6));
                long usageDurationMillis7 = uidBatteryConsumer.getUsageDurationMillis(6);
                double consumedPower7 = uidBatteryConsumer.getConsumedPower(6);
                map.put("flashlightTime", Long.valueOf(usageDurationMillis7));
                map.put("flashlightConsumedPower", BatteryStats.formatCharge(consumedPower7));
                long usageDurationMillis8 = uidBatteryConsumer.getUsageDurationMillis(7);
                double consumedPower8 = uidBatteryConsumer.getConsumedPower(7);
                map.put("systemServiceTime", Long.valueOf(usageDurationMillis8));
                map.put("systemServiceConsumedPower", BatteryStats.formatCharge(consumedPower8));
                long usageDurationMillis9 = uidBatteryConsumer.getUsageDurationMillis(8);
                double consumedPower9 = uidBatteryConsumer.getConsumedPower(8);
                map.put("mobileRadioTime", Long.valueOf(usageDurationMillis9));
                map.put("mobileRadioConsumedPower", BatteryStats.formatCharge(consumedPower9));
                long usageDurationMillis10 = uidBatteryConsumer.getUsageDurationMillis(9);
                double consumedPower10 = uidBatteryConsumer.getConsumedPower(9);
                map.put("sensorsTime", Long.valueOf(usageDurationMillis10));
                map.put("sensorsConsumedPower", BatteryStats.formatCharge(consumedPower10));
                long usageDurationMillis11 = uidBatteryConsumer.getUsageDurationMillis(10);
                double consumedPower11 = uidBatteryConsumer.getConsumedPower(10);
                map.put("gnssTime", Long.valueOf(usageDurationMillis11));
                map.put("gnssConsumedPower", BatteryStats.formatCharge(consumedPower11));
                long usageDurationMillis12 = uidBatteryConsumer.getUsageDurationMillis(11);
                double consumedPower12 = uidBatteryConsumer.getConsumedPower(11);
                map.put("wifiTime", Long.valueOf(usageDurationMillis12));
                map.put("wifiConsumedPower", BatteryStats.formatCharge(consumedPower12));
                long usageDurationMillis13 = uidBatteryConsumer.getUsageDurationMillis(12);
                double consumedPower13 = uidBatteryConsumer.getConsumedPower(12);
                map.put("wakelockTime", Long.valueOf(usageDurationMillis13));
                map.put("wakelockConsumedPower", BatteryStats.formatCharge(consumedPower13));
                long usageDurationMillis14 = uidBatteryConsumer.getUsageDurationMillis(13);
                double consumedPower14 = uidBatteryConsumer.getConsumedPower(13);
                map.put("memoryTime", Long.valueOf(usageDurationMillis14));
                map.put("memoryConsumedPower", BatteryStats.formatCharge(consumedPower14));
                long usageDurationMillis15 = uidBatteryConsumer.getUsageDurationMillis(14);
                double consumedPower15 = uidBatteryConsumer.getConsumedPower(14);
                map.put("phoneTime", Long.valueOf(usageDurationMillis15));
                map.put("phoneConsumedPower", BatteryStats.formatCharge(consumedPower15));
                long usageDurationMillis16 = uidBatteryConsumer.getUsageDurationMillis(15);
                double consumedPower16 = uidBatteryConsumer.getConsumedPower(15);
                map.put("ambientDisplayTime", Long.valueOf(usageDurationMillis16));
                map.put("ambientDisplayConsumedPower", BatteryStats.formatCharge(consumedPower16));
                long usageDurationMillis17 = uidBatteryConsumer.getUsageDurationMillis(16);
                double consumedPower17 = uidBatteryConsumer.getConsumedPower(16);
                map.put("idleTime", Long.valueOf(usageDurationMillis17));
                map.put("idleConsumedPower", BatteryStats.formatCharge(consumedPower17));
                long usageDurationMillis18 = uidBatteryConsumer.getUsageDurationMillis(17);
                double consumedPower18 = uidBatteryConsumer.getConsumedPower(17);
                map.put("reattributedTime", Long.valueOf(usageDurationMillis18));
                map.put("reattributedConsumedPower", BatteryStats.formatCharge(consumedPower18));
                break;
            }
        }
        return map;
    }

    public String refreshStats() {
        StringBuilder sb = new StringBuilder();
        BatteryUsageStats batteryUsageStats = getBatteryUsageStats();
        sb.append("Computed drain:" + BatteryStats.formatCharge(batteryUsageStats.getConsumedPower()));
        Range dischargedPowerRange = batteryUsageStats.getDischargedPowerRange();
        sb.append(", actual drain: ");
        sb.append(BatteryStats.formatCharge(((Double) dischargedPowerRange.getLower()).doubleValue()));
        if (!((Double) dischargedPowerRange.getLower()).equals(dischargedPowerRange.getUpper())) {
            sb.append(Constants.ACCEPT_TIME_SEPARATOR_SERVER);
            sb.append(BatteryStats.formatCharge(((Double) dischargedPowerRange.getUpper()).doubleValue()));
        }
        sb.append(ShellUtils.COMMAND_LINE_END);
        for (UidBatteryConsumer uidBatteryConsumer : batteryUsageStats.getUidBatteryConsumers()) {
            int uid = uidBatteryConsumer.getUid();
            if (BLUtils.getPackageForUid(this.mContext, uid).equalsIgnoreCase(pkgSpecific)) {
                sb.append("UID:" + uid);
                sb.append(", " + BatteryStats.formatCharge(uidBatteryConsumer.getConsumedPower()) + ShellUtils.COMMAND_LINE_END);
                sb.append("    screenTime:" + uidBatteryConsumer.getUsageDurationMillis(0) + ", screenConsumedPower:" + uidBatteryConsumer.getConsumedPower(0) + ShellUtils.COMMAND_LINE_END);
                sb.append("    cpuTime:" + uidBatteryConsumer.getUsageDurationMillis(1) + ", cpuConsumedPower:" + uidBatteryConsumer.getConsumedPower(1) + ShellUtils.COMMAND_LINE_END);
                sb.append("    btTime:" + uidBatteryConsumer.getUsageDurationMillis(2) + ", btConsumedPower:" + uidBatteryConsumer.getConsumedPower(2) + ShellUtils.COMMAND_LINE_END);
                sb.append("    cameraTime:" + uidBatteryConsumer.getUsageDurationMillis(3) + ", cameraConsumedPower:" + uidBatteryConsumer.getConsumedPower(3) + ShellUtils.COMMAND_LINE_END);
                sb.append("    audioTime:" + uidBatteryConsumer.getUsageDurationMillis(4) + ", audioConsumedPower:" + uidBatteryConsumer.getConsumedPower(4) + ShellUtils.COMMAND_LINE_END);
                sb.append("    videoTime:" + uidBatteryConsumer.getUsageDurationMillis(5) + ", videoConsumedPower:" + uidBatteryConsumer.getConsumedPower(5) + ShellUtils.COMMAND_LINE_END);
                sb.append("    flashlightTime:" + uidBatteryConsumer.getUsageDurationMillis(6) + ", flashlightConsumedPower:" + uidBatteryConsumer.getConsumedPower(6) + ShellUtils.COMMAND_LINE_END);
                sb.append("    systemServiceTime:" + uidBatteryConsumer.getUsageDurationMillis(7) + ", systemServiceConsumedPower:" + uidBatteryConsumer.getConsumedPower(7) + ShellUtils.COMMAND_LINE_END);
                sb.append("    mobileRadioTime:" + uidBatteryConsumer.getUsageDurationMillis(8) + ", mobileRadioConsumedPower:" + uidBatteryConsumer.getConsumedPower(8) + ShellUtils.COMMAND_LINE_END);
                sb.append("    sensorsTime:" + uidBatteryConsumer.getUsageDurationMillis(9) + ", sensorsConsumedPower:" + uidBatteryConsumer.getConsumedPower(9) + ShellUtils.COMMAND_LINE_END);
                sb.append("    gnssTime:" + uidBatteryConsumer.getUsageDurationMillis(10) + ", gnssConsumedPower:" + uidBatteryConsumer.getConsumedPower(10) + ShellUtils.COMMAND_LINE_END);
                sb.append("    wifiTime:" + uidBatteryConsumer.getUsageDurationMillis(11) + ", wifiConsumedPower:" + uidBatteryConsumer.getConsumedPower(11) + ShellUtils.COMMAND_LINE_END);
                sb.append("    wakelockTime:" + uidBatteryConsumer.getUsageDurationMillis(12) + ", wakelockConsumedPower:" + uidBatteryConsumer.getConsumedPower(12) + ShellUtils.COMMAND_LINE_END);
                sb.append("    memoryTime:" + uidBatteryConsumer.getUsageDurationMillis(13) + ", memoryConsumedPower:" + uidBatteryConsumer.getConsumedPower(13) + ShellUtils.COMMAND_LINE_END);
                sb.append("    phoneTime:" + uidBatteryConsumer.getUsageDurationMillis(14) + ", phoneConsumedPower:" + uidBatteryConsumer.getConsumedPower(14) + ShellUtils.COMMAND_LINE_END);
                sb.append("    ambientDisplayTime:" + uidBatteryConsumer.getUsageDurationMillis(15) + ", ambientDisplayConsumedPower:" + uidBatteryConsumer.getConsumedPower(15) + ShellUtils.COMMAND_LINE_END);
                sb.append("    idleTime:" + uidBatteryConsumer.getUsageDurationMillis(16) + ", idleConsumedPower:" + uidBatteryConsumer.getConsumedPower(16) + ShellUtils.COMMAND_LINE_END);
                sb.append("    reattributedTime:" + uidBatteryConsumer.getUsageDurationMillis(17) + ", reattributedConsumedPower:" + uidBatteryConsumer.getConsumedPower(17) + ShellUtils.COMMAND_LINE_END);
            }
        }
        return sb.toString();
    }
}
