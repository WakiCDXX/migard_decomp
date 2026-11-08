package com.miui.powerkeeper.logsystem.log.sampling;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.provider.Settings;
import com.miui.powerkeeper.logsystem.config.ConfigManager;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.logsystem.config.Scene;
import com.miui.powerkeeper.logsystem.log.BaseLog;
import com.miui.powerkeeper.logsystem.utils.LSUtils;
import com.miui.powerkeeper.statemachine.SleepModeControllerNew;

/* JADX INFO: loaded from: classes.dex */
public class CommonStaticLog extends SamplingCatchedLog {
    private static final String COMMON_S_DISABLED = "disabled";
    private static final String COMMON_S_DISABLING = "disabling";
    private static final String COMMON_S_ENABLED = "enabled";
    private static final String COMMON_S_ENABLING = "enabling";
    public static final String FIVEG_ICON_GROUP_SETTING_PREFIX = "5g_icon_group_mode";
    private static final String SETTINGS_SYSTEM_PREF_KEY_NFC_STATE = "system_key_nfc_state";
    private Context mContext;

    public CommonStaticLog(Context context) {
        super(context);
        this.mContext = context;
    }

    private int getBrightness() {
        try {
            return Settings.System.getInt(this.mContext.getContentResolver(), "screen_brightness");
        } catch (Settings.SettingNotFoundException e2) {
            e2.printStackTrace();
            return 0;
        }
    }

    private String getMobileDataState() {
        int i2;
        try {
            i2 = Settings.Global.getInt(this.mContext.getContentResolver(), "mobile_data");
        } catch (Settings.SettingNotFoundException e2) {
            e2.printStackTrace();
            i2 = 0;
        }
        return i2 == 1 ? COMMON_S_ENABLED : COMMON_S_DISABLED;
    }

    private String getNFCState() {
        int adapterState;
        NfcAdapter defaultAdapter;
        try {
            adapterState = Settings.System.getInt(this.mContext.getContentResolver(), SETTINGS_SYSTEM_PREF_KEY_NFC_STATE);
        } catch (Settings.SettingNotFoundException e2) {
            e2.printStackTrace();
            adapterState = -1;
        }
        if (adapterState == -1 && (defaultAdapter = ((NfcManager) this.mContext.getSystemService("nfc")).getDefaultAdapter()) != null) {
            adapterState = defaultAdapter.getAdapterState();
        }
        if (adapterState == 1) {
            return "off";
        }
        if (adapterState == 2) {
            return "truning on";
        }
        if (adapterState != 3) {
            return adapterState != 4 ? "unknown" : "truning off";
        }
        return SleepModeControllerNew.SLEEP_MODE_ON;
    }

    private String getNetworkType(int i2) {
        int i3;
        try {
            i3 = Settings.Global.getInt(this.mContext.getContentResolver(), FIVEG_ICON_GROUP_SETTING_PREFIX + i2);
        } catch (Settings.SettingNotFoundException e2) {
            e2.printStackTrace();
            i3 = 0;
        }
        if (i3 == 1) {
            return "5G NSA";
        }
        return i3 == 2 ? "5G SA" : "Other";
    }

    private String getThrethold() {
        Scene scene = BaseLog.mScene;
        return scene == null ? "Error" : String.valueOf(scene.getPower() * ConfigManager.getInstance(this.mContext).getThreshold());
    }

    private int getVolume(String str) {
        try {
            return Settings.System.getInt(this.mContext.getContentResolver(), str);
        } catch (Settings.SettingNotFoundException e2) {
            e2.printStackTrace();
            return 0;
        }
    }

    private String getWifiApState() {
        switch (((WifiManager) this.mContext.getSystemService("wifi")).getWifiApState()) {
            case 10:
                return COMMON_S_DISABLING;
            case 11:
                return COMMON_S_DISABLED;
            case 12:
                return COMMON_S_ENABLING;
            case 13:
                return COMMON_S_ENABLED;
            default:
                return "failed";
        }
    }

    private String getWifiState() {
        int wifiState = ((WifiManager) this.mContext.getSystemService("wifi")).getWifiState();
        if (wifiState == 0) {
            return COMMON_S_DISABLING;
        }
        if (wifiState == 1) {
            return COMMON_S_DISABLED;
        }
        if (wifiState != 2) {
            return wifiState != 3 ? "unknown" : COMMON_S_ENABLED;
        }
        return COMMON_S_ENABLING;
    }

    private String getWindow() {
        return LSUtils.execShellCommandForResult("dumpsys window d", 0, "Application tokens in", "");
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public int getInterfaceLogType() {
        return 0;
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog, com.miui.powerkeeper.logsystem.log.LogInterface
    public String getInterfaceName() {
        return LogType.LOG_TYPE_COMMON_STATIC_NAME;
    }

    @Override // com.miui.powerkeeper.logsystem.log.BaseLog
    protected boolean realProduceSystemLog(String str) {
        addContent("Power threshold: " + getThrethold() + "mAh");
        StringBuilder sb = new StringBuilder();
        sb.append("Brightness: ");
        sb.append(getBrightness());
        addContent(sb.toString());
        addContent("Wifi state: " + getWifiState());
        addContent("Mobild data state: " + getMobileDataState());
        addContent("Wifi Ap state: " + getWifiApState());
        addContent("Volume: alarm-" + getVolume("volume_alarm") + " music-" + getVolume("volume_music") + " ring-" + getVolume("volume_ring") + " voice-" + getVolume("volume_voice"));
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Nfc state: ");
        sb2.append(getNFCState());
        addContent(sb2.toString());
        addContent("Phone Network type: ");
        addContent("    Slot0 : " + getNetworkType(0) + ", Slot1: " + getNetworkType(1));
        addContent("Window display area:");
        addContent(getWindow());
        catchLog();
        return true;
    }
}
