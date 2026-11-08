package com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body;

import android.content.Context;
import com.miui.powerkeeper.feedbackcontrol.ThermalInfoUtils;

/* JADX INFO: loaded from: classes.dex */
public class AppInfo extends LogCollector {
    private final String TABLE;
    private final String TAG;

    public AppInfo(Context context) {
        super(context);
        this.TAG = "AppInfoCollector";
        this.TABLE = "App Info:";
        this.mLog.d("AppInfoCollector", "create");
    }

    @Override // com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.LogCollector
    public void collect() {
        if (this.mPublicSpace.isContinueCollect()) {
            this.mLog.d("AppInfoCollector", "collect");
            String currentForeground = this.mPublicSpace.getCurrentForeground();
            String appVersionName = ThermalInfoUtils.getAppVersionName(currentForeground, this.mContext);
            int refreshRate = ThermalInfoUtils.getRefreshRate(this.mContext);
            int volumeForApp = ThermalInfoUtils.getVolumeForApp(this.mContext);
            this.mCache.append("App Info:[App Name:" + currentForeground + " App version:" + appVersionName + " App fps:" + refreshRate + " App volume:" + volumeForApp + "]");
        }
    }

    @Override // com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.LogCollector
    public void logout() {
        this.mLog.d("AppInfoCollector", "logout");
    }
}
