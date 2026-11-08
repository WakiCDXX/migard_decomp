package com.miui.powerkeeper.millet;

import android.content.Context;
import android.os.SystemProperties;
import android.util.Log;
import com.miui.powerkeeper.controller.FrozenAppController;
import com.miui.powerkeeper.provider.GlobalFeatureConfigureHelper;
import com.xiaomi.channelcollection.RegisterAppChannelInfoCollectionJob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import miui.os.Build;

/* JADX INFO: loaded from: classes.dex */
public class MilletConfig {
    public static List<String> ENABLE_DOUBLE_APP = null;
    private static List<String> ENABLE_GLOBAL_DEVICE = null;
    private static List<String> ENABLE_SYSTEMAPP_DEVICE = null;
    private static String PR_NAME = SystemProperties.get("ro.product.name", "");
    private static String TAG = "MilletConfig";
    private static String alarmWhite = "";
    public static boolean enable = false;
    private static boolean enableFun = false;
    private static String fgBlack = "com.baidu.netdisk,com.ucmobile.lite,com.UCMobile,com.tencent.qqlive,cmccwm.mobilemusic,com.xs.fm,com.kmxs.reader,com.dragon.read,com.youku.phone,tv.danmaku.bili,com.ss.android.ugc.aweme,app.podcast.cosmos,com.ximalaya.ting.lite,com.ss.android.article.news,com.tencent.android.qqdownloader,com.ss.android.ugc.aweme.lite,com.ss.android.ugc.live,com.kuaishou.nebula,com.tencent.qqmusicpad,cn.kuwo.kwmusichd,tv.danmaku.bilibilihd,com.hiresmusic,com.duowan.mobile,cn.missevan,com.facetech.konking,com.shinyv.cnr,com.vvvdj.player,com.xs.fm.lite,com.duowan.kiwi,com.kugou.android.tingshu,com.qidian.QDReader,com.ss.android.article.lite,com.luna.music";
    public static int firstScreenOffDelayTime = 62000;
    private static boolean initSystemApp = false;
    public static boolean isGlobal = false;
    public static boolean restrictAsyncBinder = false;
    public static boolean restrictBroadast = false;
    public static boolean restrictNet = false;
    private static boolean supportAlarm = false;
    private static boolean supportCharging = true;
    private static boolean supportDoubleApp = true;
    private static boolean supportFg = true;
    private static boolean supportSensor = true;
    private static boolean supportSystemApp;
    private static String systemAppBlack;

    static {
        isGlobal = (!Build.IS_INTERNATIONAL_BUILD && "CN".equals(SystemProperties.get("ro.miui.region", "unknown")) && (!PR_NAME.contains(RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT) || PR_NAME.contains("_cn") || PR_NAME.contains("_CN"))) ? false : true;
        ENABLE_GLOBAL_DEVICE = new ArrayList(Arrays.asList("vermeer", "duchamp", "sky", "iron", "river", "XIG03", "garnet", "zircon", "gold", "shennong", "houji", "sapphire", "vermeer", "duchamp", "aurora", "ruyi", "sheng", "tides", "moon", "dizi", "ruan", "peridot", "breeze", "chenfeng", "degas", "spark", "corot", "uke", "muyu", "dada", "xuanyuan", "haotian", "zorn", "miro", "rodin"));
        ENABLE_DOUBLE_APP = new ArrayList(Arrays.asList("nuwa", "ishtar", "babylon", "fuxi"));
        enableFun = SystemProperties.getBoolean("persist.sys.millet.newfun", true);
        supportSystemApp = false;
        initSystemApp = false;
        systemAppBlack = "com.miui.player, com.android.browser, com.miui.video, com.duokan.phone.remotecontroller";
        ENABLE_SYSTEMAPP_DEVICE = new ArrayList(Arrays.asList("vermeer", "duchamp", "shennong", "houji", "manet", "gold", "ishtar"));
    }

    private static void checkKey(String str) {
        if (!str.contains("charging:")) {
            supportCharging = true;
        }
        if (!str.contains("sensor:")) {
            supportSensor = true;
        }
        if (!str.contains("fgservice:")) {
            supportFg = true;
        }
        if (!str.contains("alarm:")) {
            supportAlarm = false;
        }
        if (!str.contains("systemapp:")) {
            supportSystemApp = false;
        }
        if (str.contains("doubleapp:")) {
            return;
        }
        supportDoubleApp = false;
    }

    public static void dealCloudData(String str) {
        if (str == null || str.isEmpty()) {
            resetCloud();
            return;
        }
        checkKey(str);
        for (String str2 : str.split(";")) {
            if (str2.contains("charging:")) {
                supportCharging = str2.contains("true");
            } else if (str2.contains("sensor:")) {
                supportSensor = str2.contains("true");
            } else if (str2.contains("fgservice:")) {
                supportFg = str2.contains("enable:true");
                if (str2.contains(RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT)) {
                    fgBlack = str2.split(RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT)[1];
                }
            } else if (str2.contains("alarm:")) {
                supportAlarm = str2.contains("enable:true");
                if (str2.contains(RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT)) {
                    alarmWhite = str2.split(RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT)[1];
                }
            } else if (str2.contains("systemapp:")) {
                supportSystemApp = str2.contains("systemapp:true");
                initSystemApp = true;
                if (str2.contains(RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT)) {
                    systemAppBlack = str2.split(RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT)[1];
                }
            } else if (str2.contains("doubleapp:")) {
                supportDoubleApp = !str2.contains("doubleapp:false");
            }
        }
        Log.d(TAG, "dealCloudData alarm:" + supportAlarm + " alarmWhite:" + alarmWhite + " supportFg:" + supportFg + " fgBlack:" + fgBlack + " sensor:" + supportSensor + " charging:" + supportCharging + " supportSystem:" + supportSystemApp + " system:" + systemAppBlack);
    }

    public static boolean getBroadcastCancel() {
        return true;
    }

    public static boolean getEnable(Context context) {
        String str = FrozenAppController.newCloudFrozenNewStatus;
        boolean cloudFrozenNewConfigure = str == null ? GlobalFeatureConfigureHelper.getCloudFrozenNewConfigure(context) : "true".equals(str);
        Log.d(TAG, "getEnable ret " + cloudFrozenNewConfigure);
        return cloudFrozenNewConfigure;
    }

    public static boolean getEnableFun() {
        return enableFun;
    }

    public static boolean getRestrictAsyncBinder() {
        return !isGlobal;
    }

    public static boolean getRestrictFg(String str) {
        if (enableFun && supportFg) {
            return fgBlack.contains(str);
        }
        return false;
    }

    public static boolean getRestrictNet() {
        return !isGlobal;
    }

    public static int getScreenOffDelay() {
        return 5000;
    }

    public static int getScreenOnDelay() {
        return 5000;
    }

    public static boolean getSensorEnable() {
        if (enableFun) {
            return supportSensor;
        }
        return false;
    }

    private static void resetCloud() {
        supportCharging = true;
        supportSensor = true;
        supportAlarm = false;
        supportFg = true;
        supportSystemApp = false;
        supportDoubleApp = true;
    }

    public static boolean supportAlarm() {
        return supportAlarm;
    }

    public static boolean supportCharging() {
        return !isGlobal && supportCharging;
    }

    public static boolean supportDoubleApp() {
        return supportDoubleApp;
    }

    public static String supportGlobalNewFeature() {
        return (!isGlobal || ENABLE_GLOBAL_DEVICE.contains(android.os.Build.DEVICE)) ? "true" : "false";
    }

    public static boolean supportSystemApp(String str) {
        if (str == null) {
            return false;
        }
        if (supportSystemApp) {
            return systemAppBlack.contains(str);
        }
        if (initSystemApp || !ENABLE_SYSTEMAPP_DEVICE.contains(android.os.Build.DEVICE)) {
            return false;
        }
        return systemAppBlack.contains(str);
    }
}
