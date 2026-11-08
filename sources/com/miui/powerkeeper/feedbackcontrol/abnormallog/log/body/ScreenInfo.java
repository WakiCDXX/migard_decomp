package com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body;

import android.content.Context;
import android.os.Build;
import com.miui.powerkeeper.feedbackcontrol.ThermalInfoUtils;

/* JADX INFO: loaded from: classes.dex */
public class ScreenInfo extends LogCollector {
    private final String TABLE;
    private final String TAG;

    public ScreenInfo(Context context) {
        super(context);
        this.TAG = "ScreenInfoCollector";
        this.TABLE = "Screen Info:";
        this.mLog.d("ScreenInfoCollector", "create");
    }

    @Override // com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.LogCollector
    public void collect() {
        if (this.mPublicSpace.isContinueCollect()) {
            this.mLog.d("ScreenInfoCollector", "collect");
            int refreshRate = ThermalInfoUtils.getRefreshRate(this.mContext);
            int screenBrightness = ThermalInfoUtils.getScreenBrightness(this.mContext);
            String str = Build.DEVICE;
            String resolution = (str.equalsIgnoreCase("star") || str.equalsIgnoreCase("venus")) ? ThermalInfoUtils.getResolution(this.mContext) : "default";
            this.mCache.append("Screen Info:[Refresh Rate:" + refreshRate + " Screen Brightness:" + screenBrightness + " Screen Resolution:" + resolution + "] ");
        }
    }

    @Override // com.miui.powerkeeper.feedbackcontrol.abnormallog.log.body.LogCollector
    public void logout() {
        this.mLog.d("ScreenInfoCollector", "logout");
    }
}
