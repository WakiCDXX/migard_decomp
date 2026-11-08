package com.miui.powerkeeper.feedbackcontrol.abnormallog.utils;

import android.util.ArrayMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class PublicSpace {
    private static PublicSpace mInstance;
    private String mCurrentForeground;
    private List<String> mForegroundList;
    private Map<String, Long> mSceneMap;
    private int mStartCollectTemp = 40;
    private int mStopCollectTemp = 38;
    public final String THERMAL_DUMP_PATH = "/data/vendor/thermal/thermal.dump";
    public final String THERMAL_LOG_PATH = "/data/vendor/thermal/thermalLog";
    private boolean mContinueCollect = false;
    private boolean mCollectAllInfo = false;
    private long mCollectInfoMinInterval = 0;
    private final Map<String, Integer> mPkgThreshold = new ArrayMap(64);

    private PublicSpace() {
    }

    public static PublicSpace getInstance() {
        if (mInstance == null) {
            mInstance = new PublicSpace();
        }
        return mInstance;
    }

    public void collectAllInfo(boolean z2) {
        this.mCollectAllInfo = z2;
    }

    public long getCollectInfoMinInterval() {
        return this.mCollectInfoMinInterval;
    }

    public String getCurrentForeground() {
        return this.mCurrentForeground;
    }

    public List<String> getForegroundList() {
        return this.mForegroundList;
    }

    public Map<String, Integer> getPkgThreshold() {
        return this.mPkgThreshold;
    }

    public Map<String, Long> getSceneMap() {
        return this.mSceneMap;
    }

    public int getStartCollectTemp() {
        return this.mStartCollectTemp;
    }

    public int getStopCollectTemp() {
        return this.mStopCollectTemp;
    }

    public boolean isContinueCollect() {
        if (this.mCollectAllInfo) {
            return true;
        }
        return this.mContinueCollect;
    }

    public void onCollectEnd() {
        this.mContinueCollect = false;
    }

    public void setCollectInfoMinInterval(long j2) {
        this.mCollectInfoMinInterval = j2 * 2;
    }

    public void setContinueCollect(boolean z2) {
        this.mContinueCollect = z2;
    }

    public void setCurrentForeground(String str) {
        this.mCurrentForeground = str;
    }

    public void setForegroundList(List<String> list) {
        this.mForegroundList = list;
    }

    public void setPkgThreshold(String str, Integer num) {
        this.mPkgThreshold.put(str, num);
    }

    public void setSceneMap(Map<String, Long> map) {
        this.mSceneMap = map;
    }
}
