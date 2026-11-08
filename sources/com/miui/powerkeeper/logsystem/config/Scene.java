package com.miui.powerkeeper.logsystem.config;

import android.content.Context;
import android.os.Build;
import com.google.gson.annotations.SerializedName;
import com.miui.powerkeeper.utils.SharedPrefUtil;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class Scene {
    private ArrayList<DeviceConfig> deviceConfigList = new ArrayList<>();

    @SerializedName("id")
    private int id;
    private int logType;
    private String name;
    private int power;
    private int type;

    public class DeviceConfig {
        String deviceName;
        int logType = -1;
        int power;

        public DeviceConfig() {
        }

        public String getDeviceName() {
            return this.deviceName;
        }

        public int getLogType() {
            return this.logType;
        }

        public int getPower() {
            return this.power;
        }
    }

    protected Scene(int i2, int i3, String str, int i4, int i5) {
        this.id = i2;
        this.type = i3;
        this.name = str;
        this.logType = i4;
        this.power = i5;
    }

    public ArrayList<DeviceConfig> getDevicePowers() {
        return this.deviceConfigList;
    }

    public int getId() {
        return this.id;
    }

    public int getLogType() {
        int i2;
        if (this.logType == 0) {
            return 1128202399;
        }
        ArrayList<DeviceConfig> arrayList = this.deviceConfigList;
        if (arrayList == null || arrayList.isEmpty()) {
            return this.logType;
        }
        for (DeviceConfig deviceConfig : this.deviceConfigList) {
            if (deviceConfig.deviceName.equals(Build.DEVICE) && (i2 = deviceConfig.logType) >= 0) {
                if (i2 == 0) {
                    return 1128202399;
                }
                return i2;
            }
        }
        return this.logType;
    }

    public String getName() {
        return this.name;
    }

    public int getPower() {
        int i2;
        ArrayList<DeviceConfig> arrayList = this.deviceConfigList;
        if (arrayList == null || arrayList.isEmpty()) {
            return this.power;
        }
        for (DeviceConfig deviceConfig : this.deviceConfigList) {
            if (deviceConfig.deviceName.equals(Build.DEVICE) && (i2 = deviceConfig.power) != 0) {
                return i2;
            }
        }
        return this.power;
    }

    public long getTimestamp(Context context) {
        return SharedPrefUtil.getInstance(context, "log_system_config").getLongValue("scene_interval_" + this.id, 0L);
    }

    public int getType() {
        return this.type;
    }

    public void setTimestamp(Context context, long j2) {
        SharedPrefUtil.getInstance(context, "log_system_config").save("scene_interval_" + this.id, j2);
    }
}
