package com.miui.powerkeeper.feedbackcontrol.abnormallog.control;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.text.TextUtils;
import com.miui.powerkeeper.feedbackcontrol.ThermalInfoCollector;
import com.miui.powerkeeper.feedbackcontrol.ThermalInfoUtils;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.utils.LogPrinter;
import com.miui.powerkeeper.feedbackcontrol.abnormallog.utils.PublicSpace;
import com.miui.powerkeeper.provider.SimpleSettingKeys;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.utils.Utils;

/* JADX INFO: loaded from: classes.dex */
public class ControlManager {
    private final String TAG = "ThermalControlManager";
    private boolean mCatchLogEnable;
    private long mCatchLogMinInterval;
    private ContentObserver mCloudControlObserver;
    private long mCollectAppsInfoMinChangedInterval;
    private long mCollectAppsInfoMinInterval;
    private int mCollectTotalBatteryLevel;
    private final Context mContext;
    private final Handler mHandler;
    private boolean mIsTestMode;
    private boolean mIsUnReleased;
    private final LogPrinter mLog;
    private final PublicSpace mPublicSpace;
    private boolean mUserAllow;

    public ControlManager(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
        LogPrinter logPrinter = LogPrinter.getLogPrinter();
        this.mLog = logPrinter;
        this.mPublicSpace = PublicSpace.getInstance();
        this.mCloudControlObserver = new ContentObserver(handler) { // from class: com.miui.powerkeeper.feedbackcontrol.abnormallog.control.ControlManager.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z2) {
                ControlManager.this.mLog.d("ThermalControlManager", "cloud control changed");
                ControlManager.this.updateCloudControlData();
            }
        };
        logPrinter.d("ThermalControlManager", "control manager create");
        updateCloudControlData();
        start();
    }

    private void analysisPkgThreshold(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        for (String str2 : str.split(",")) {
            String[] strArrSplit = str2.split(":");
            this.mPublicSpace.setPkgThreshold(strArrSplit[0], Integer.valueOf(Integer.parseInt(strArrSplit[1])));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCloudControlData() {
        this.mLog.d("ThermalControlManager", "updateCloudControlData");
        this.mIsTestMode = ThermalInfoUtils.isTestMode();
        this.mIsUnReleased = ThermalInfoUtils.isUnReleased();
        this.mUserAllow = Utils.isUserExperienceAndPrivacyAllowed(this.mContext);
        boolean z2 = SimpleSettings.Misc.getBoolean(this.mContext, SimpleSettingKeys.KEY_THERMAL_ENABLE, false);
        this.mLog.setDebug(this.mIsTestMode);
        if (z2 && this.mUserAllow) {
            if (this.mIsUnReleased) {
                updateUnreleased();
            } else {
                updateReleased();
            }
            this.mHandler.sendEmptyMessage(0);
            return;
        }
        this.mLog.d("ThermalControlManager", "service stop because: thermalEnable:" + z2 + "  userAll: " + this.mUserAllow);
        this.mHandler.sendEmptyMessage(1);
    }

    private void updateReleased() {
        this.mCatchLogEnable = SimpleSettings.Misc.getBoolean(this.mContext, SimpleSettingKeys.KEY_CATCH_LOG_RELEASED_ENABLE, false);
        this.mCatchLogMinInterval = SimpleSettings.Misc.getLong(this.mContext, SimpleSettingKeys.KEY_CATCH_LOG_RELEASED_MIN_INTERVAL, ThermalInfoCollector.DEFAULT_CATCH_LOG_MIN_INTERVAL);
        this.mCollectAppsInfoMinChangedInterval = SimpleSettings.Misc.getLong(this.mContext, SimpleSettingKeys.KEY_COLLECT_APPS_INFO_RELEASED_MIN_CHANGED_INTERVAL, 1800000L);
        this.mCollectAppsInfoMinInterval = SimpleSettings.Misc.getLong(this.mContext, SimpleSettingKeys.KEY_COLLECT_APPS_INFO_RELEASED_MIN_INTERVAL, 1800000L);
        this.mCollectTotalBatteryLevel = SimpleSettings.Misc.getInt(this.mContext, SimpleSettingKeys.KEY_COLLECT_TOTAL_INFO_RELEASED_BATTERY_LEVEL, ThermalInfoCollector.DEFAULT_COLLECT_TOTAL_INFO_BATTERY_LEVEL);
        analysisPkgThreshold(SimpleSettings.Misc.getString(this.mContext, SimpleSettingKeys.KEY_CATCH_LOG_RELEASED_THRESHHOLD, ""));
    }

    private void updateUnreleased() {
        this.mCatchLogEnable = SimpleSettings.Misc.getBoolean(this.mContext, SimpleSettingKeys.KEY_CATCH_LOG_UNRELEASED_ENABLE, false);
        this.mCatchLogMinInterval = SimpleSettings.Misc.getLong(this.mContext, SimpleSettingKeys.KEY_CATCH_LOG_UNRELEASED_MIN_INTERVAL, ThermalInfoCollector.DEFAULT_CATCH_LOG_MIN_INTERVAL);
        this.mCollectAppsInfoMinChangedInterval = SimpleSettings.Misc.getLong(this.mContext, SimpleSettingKeys.KEY_COLLECT_APPS_INFO_UNRELEASED_MIN_CHANGED_INTERVAL, 1800000L);
        this.mCollectAppsInfoMinInterval = SimpleSettings.Misc.getLong(this.mContext, SimpleSettingKeys.KEY_COLLECT_APPS_INFO_UNRELEASED_MIN_INTERVAL, 1800000L);
        this.mCollectTotalBatteryLevel = SimpleSettings.Misc.getInt(this.mContext, SimpleSettingKeys.KEY_COLLECT_TOTAL_INFO_UNRELEASED_BATTERY_LEVEL, ThermalInfoCollector.DEFAULT_COLLECT_TOTAL_INFO_BATTERY_LEVEL);
        analysisPkgThreshold(SimpleSettings.Misc.getString(this.mContext, SimpleSettingKeys.KEY_CATCH_LOG_UNRELEASED_THRESHHOLD, ""));
    }

    public long getCatchLogMinInterval() {
        return this.mCatchLogMinInterval;
    }

    public long getCollectAppsInfoMinChangedInterval() {
        return this.mCollectAppsInfoMinChangedInterval;
    }

    public long getCollectAppsInfoMinInterval() {
        return this.mCollectAppsInfoMinInterval;
    }

    public int getCollectTotalBatteryLevel() {
        return this.mCollectTotalBatteryLevel;
    }

    public boolean isCatchLogEnable() {
        return this.mCatchLogEnable;
    }

    public boolean isTestMode() {
        return this.mIsTestMode;
    }

    public boolean isUserAllow() {
        return this.mUserAllow;
    }

    public void start() {
        this.mContext.getContentResolver().registerContentObserver(SimpleSettings.Misc.getUriFor(SimpleSettingKeys.KEY_THERMAL_LAST_DATA_MD5), false, this.mCloudControlObserver);
    }
}
