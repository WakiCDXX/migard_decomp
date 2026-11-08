package com.miui.powerkeeper.powerchecker;

import android.os.BatteryStats;
import com.miui.powerkeeper.statemachine.SleepModeControllerNew;
import java.lang.reflect.Field;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class BatteryUsageCondition {
    int MobileDataState;
    int MobileState;
    boolean bluetoothOn;
    boolean gpsOn;
    boolean phoneInCall;
    boolean pluggedIn;
    boolean screenOn;
    boolean wifiOn;

    public BatteryUsageCondition() {
    }

    public static boolean checkDirtyCondition(BatteryUsageCondition batteryUsageCondition, BatteryStats.HistoryItem historyItem) {
        BatteryUsageCondition batteryUsageCondition2 = new BatteryUsageCondition();
        updateCondition(batteryUsageCondition2, historyItem);
        return !batteryUsageCondition2.equals(batteryUsageCondition);
    }

    public static boolean checkScreenCondition(BatteryUsageCondition batteryUsageCondition, boolean z2) {
        return batteryUsageCondition.screenOn == z2;
    }

    public static Field getConditionField(String str) {
        try {
            return BatteryUsageCondition.class.getDeclaredField(str);
        } catch (NoSuchFieldException unused) {
            return null;
        }
    }

    public static void updateCondition(BatteryUsageCondition batteryUsageCondition, BatteryUsageCondition batteryUsageCondition2) {
        if (batteryUsageCondition == null || batteryUsageCondition2 == null) {
            return;
        }
        batteryUsageCondition.gpsOn = batteryUsageCondition2.gpsOn;
        batteryUsageCondition.screenOn = batteryUsageCondition2.screenOn;
        batteryUsageCondition.wifiOn = batteryUsageCondition2.wifiOn;
        batteryUsageCondition.pluggedIn = batteryUsageCondition2.pluggedIn;
        batteryUsageCondition.bluetoothOn = batteryUsageCondition2.bluetoothOn;
        batteryUsageCondition.phoneInCall = batteryUsageCondition2.phoneInCall;
        batteryUsageCondition.MobileDataState = batteryUsageCondition2.MobileDataState;
        batteryUsageCondition.MobileState = batteryUsageCondition2.MobileState;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        BatteryUsageCondition batteryUsageCondition = (BatteryUsageCondition) obj;
        return this.gpsOn == batteryUsageCondition.gpsOn && this.screenOn == batteryUsageCondition.screenOn && this.wifiOn == batteryUsageCondition.wifiOn && this.pluggedIn == batteryUsageCondition.pluggedIn && this.bluetoothOn == batteryUsageCondition.bluetoothOn && this.phoneInCall == batteryUsageCondition.phoneInCall && this.MobileDataState == batteryUsageCondition.MobileDataState && this.MobileState == batteryUsageCondition.MobileState;
    }

    public int hashCode() {
        return Objects.hash(Boolean.valueOf(this.gpsOn), Boolean.valueOf(this.screenOn), Boolean.valueOf(this.wifiOn), Boolean.valueOf(this.pluggedIn), Boolean.valueOf(this.bluetoothOn), Boolean.valueOf(this.phoneInCall), Integer.valueOf(this.MobileDataState), Integer.valueOf(this.MobileState));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("gps:");
        sb.append(this.gpsOn ? SleepModeControllerNew.SLEEP_MODE_ON : "off");
        sb.append(",screen:");
        sb.append(this.screenOn ? SleepModeControllerNew.SLEEP_MODE_ON : "off");
        sb.append(",wifi:");
        sb.append(this.wifiOn ? SleepModeControllerNew.SLEEP_MODE_ON : "off");
        sb.append(",bluetooth:");
        sb.append(this.bluetoothOn ? SleepModeControllerNew.SLEEP_MODE_ON : "off");
        sb.append(",phoneInCall:");
        sb.append(this.phoneInCall);
        sb.append(",MobileState:");
        sb.append(this.MobileState);
        sb.append(",mobileData:");
        sb.append(this.MobileDataState);
        sb.append(",plugged:");
        sb.append(this.pluggedIn ? "plugged" : "none");
        sb.append("]");
        return sb.toString();
    }

    public BatteryUsageCondition(BatteryUsageCondition batteryUsageCondition) {
        this.gpsOn = batteryUsageCondition.gpsOn;
        this.screenOn = batteryUsageCondition.screenOn;
        this.wifiOn = batteryUsageCondition.wifiOn;
        this.pluggedIn = batteryUsageCondition.pluggedIn;
        this.bluetoothOn = batteryUsageCondition.bluetoothOn;
        this.phoneInCall = batteryUsageCondition.phoneInCall;
        this.MobileDataState = batteryUsageCondition.MobileDataState;
        this.MobileState = batteryUsageCondition.MobileState;
    }

    public static class MatchCondition {
        Field matchField;
        Object matchValue;

        public MatchCondition(Field field, Object obj) {
            this.matchField = field;
            this.matchValue = obj;
        }

        public boolean matched(BatteryUsageCondition batteryUsageCondition) {
            Field field = this.matchField;
            if (field == null) {
                return true;
            }
            try {
                Object obj = field.get(batteryUsageCondition);
                if ((obj instanceof Boolean) && (this.matchValue instanceof Boolean)) {
                    return ((Boolean) obj).booleanValue() == ((Boolean) this.matchValue).booleanValue();
                }
                return (obj instanceof Integer) && (this.matchValue instanceof Integer) && ((Integer) obj).intValue() == ((Integer) this.matchValue).intValue();
            } catch (Exception unused) {
            }
            return false;
        }

        public MatchCondition(String str, Object obj) {
            this.matchField = BatteryUsageCondition.getConditionField(str);
            this.matchValue = obj;
        }
    }

    public static void updateCondition(BatteryUsageCondition batteryUsageCondition, BatteryStats.HistoryItem historyItem) {
        if (batteryUsageCondition != null) {
            batteryUsageCondition.gpsOn = BatteryStatsHelper.getGpsState(historyItem);
            batteryUsageCondition.pluggedIn = BatteryStatsHelper.getPlugState(historyItem);
            batteryUsageCondition.screenOn = BatteryStatsHelper.getScreenState(historyItem);
            batteryUsageCondition.wifiOn = BatteryStatsHelper.getWifiState(historyItem);
            batteryUsageCondition.bluetoothOn = BatteryStatsHelper.getBluetoothState(historyItem);
            batteryUsageCondition.phoneInCall = BatteryStatsHelper.getPhoneInCallState(historyItem);
            batteryUsageCondition.MobileState = BatteryStatsHelper.getMobileState(historyItem);
            batteryUsageCondition.MobileDataState = BatteryStatsHelper.getMobileDataState(historyItem);
        }
    }
}
