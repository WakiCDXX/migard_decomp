package com.xiaomi.channel.commonutils.misc;

/* JADX INFO: loaded from: classes.dex */
public class BuildSettings {
    public static final String BETA = "BETA";
    public static final String DEBUG = "DEBUG";
    public static final boolean IsBetaBuild = false;

    @Deprecated
    public static final boolean IsDebugBuild = false;

    @Deprecated
    public static final boolean IsDefaultChannel = false;

    @Deprecated
    public static final boolean IsForYYBuild = false;

    @Deprecated
    public static final boolean IsLogableBuild = false;
    public static final boolean IsRCBuild = false;
    public static boolean IsTestBuild = false;
    public static final String LOGABLE = "LOGABLE";
    public static final int Official = 1;
    public static final int OneBox = 3;
    public static final String ReleaseChannel;
    public static final int SandBox = 2;
    public static final String TEST = "TEST";
    public static final String YY = "YY";
    private static int envType;

    static {
        String str = DebugSwitch.sDebugServerHost ? "ONEBOX" : "@SHIP.TO.2A2FE0D7@";
        ReleaseChannel = str;
        IsTestBuild = false;
        envType = 1;
        if (str.equalsIgnoreCase("SANDBOX")) {
            envType = 2;
        } else if (str.equalsIgnoreCase("ONEBOX")) {
            envType = 3;
        } else {
            envType = 1;
        }
    }

    public static boolean IsOneBoxBuild() {
        return envType == 3;
    }

    public static boolean IsSandBoxBuild() {
        return envType == 2;
    }

    public static int getEnvType() {
        return envType;
    }

    public static void setEnvType(int i2) {
        envType = i2;
    }
}
