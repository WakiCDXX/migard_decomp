package com.miui.powerkeeper.tracker;

import android.content.Context;
import android.util.ArrayMap;
import android.util.Log;
import com.miui.powerkeeper.utils.Utils;
import com.xiaomi.onetrack.DataConverter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import miui.os.Build;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class TrackerManager {
    public static final String TAG = "TrackerManager";
    public static volatile TrackerManager sInstance;
    private Context mApplicationContext;
    private PrivacyPolicy mPrivacyPolicy;
    private final Map<Tracker.TrackPolicy, Tracker> mTrackerMap = new ArrayMap(5);

    private class PrivacyPolicy {
        private boolean mAllowTrack;
        private Context mContext;
        private boolean mInternational;
        private boolean mIsCloudControlAllow;
        private boolean mIsPrivacyEnable;
        private boolean mIsUserAllow;
        private boolean mPubSubDisable;
        private String mRegion = Build.getRegion();

        public PrivacyPolicy(Context context) {
            this.mContext = context;
        }

        public boolean isAllowTrack() {
            return this.mAllowTrack;
        }

        public boolean isInternational() {
            return this.mInternational;
        }

        public boolean isPubSubDisable() {
            return this.mPubSubDisable;
        }

        public String toString() {
            return "PrivacyPolicy{mAllowTrack=" + this.mAllowTrack + ", mPubSubDisable=" + this.mPubSubDisable + ", mInternational=" + this.mInternational + ", mIsCloudControlAllow=" + this.mIsCloudControlAllow + ", mIsUserAllow=" + this.mIsUserAllow + ", mIsPrivacyEnable=" + this.mIsPrivacyEnable + ", mRegion='" + this.mRegion + "'}";
        }

        public void updateLicense() {
            boolean z2 = false;
            this.mInternational = Build.IS_INTERNATIONAL_BUILD && !this.mRegion.equals("RU");
            this.mAllowTrack = Utils.isUserExperienceAndPrivacyAllowed(this.mContext);
            if (!Utils.isPrivacyEnable(this.mContext) && Utils.isInEURegion()) {
                z2 = true;
            }
            this.mPubSubDisable = z2;
            this.mIsCloudControlAllow = Utils.isCloudControlAllowed(this.mContext);
            this.mIsUserAllow = Utils.isUserAllowed(this.mContext);
            this.mIsPrivacyEnable = Utils.isPrivacyEnable(this.mContext);
        }
    }

    private TrackerManager(Context context) {
        Context applicationContext = context.getApplicationContext();
        this.mApplicationContext = applicationContext;
        this.mPrivacyPolicy = new PrivacyPolicy(applicationContext);
    }

    private void checkPrivacyPolicy() {
        this.mPrivacyPolicy.updateLicense();
        Tracker.setDisable(this.mPrivacyPolicy.isPubSubDisable());
    }

    public static TrackerManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (TrackerManager.class) {
                try {
                    if (sInstance == null) {
                        sInstance = new TrackerManager(context);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        sInstance.checkPrivacyPolicy();
        return sInstance;
    }

    private void trackCheckPrivacy(int i2, String str, Map<String, Object> map) {
        if (this.mPrivacyPolicy.isAllowTrack()) {
            trackShunt(i2, str, map);
            return;
        }
        Log.i(TAG, "隐私未授权，禁止上报:" + this.mPrivacyPolicy);
    }

    private void trackReally(Tracker.TrackPolicy trackPolicy, Map<String, Object> map) {
        Tracker orDefault = this.mTrackerMap.getOrDefault(trackPolicy, null);
        if (orDefault == null) {
            synchronized (this.mTrackerMap) {
                orDefault = new Tracker(this.mApplicationContext, trackPolicy);
                this.mTrackerMap.put(trackPolicy, orDefault);
            }
        }
        orDefault.track(trackPolicy, map);
    }

    private void trackShunt(int i2, String str, Map<String, Object> map) {
        Tracker.TrackPolicy trackPolicy = new Tracker.TrackPolicy(this.mPrivacyPolicy.mInternational, i2, str);
        if (trackPolicy.isAllowTrack()) {
            trackPolicy.setChannel("com.miui.powerkeeper").setProjectId("battery-and-performance").setPrivateKeyId("9502ee098a6d81aacbfaa50cc2c99eba9ff528a0");
            trackReally(trackPolicy, map);
        } else {
            Log.i(TAG, "不符合上报策略:" + trackPolicy);
        }
    }

    public void track(int i2, String str, String str2, List<Map<String, Object>> list) {
        HashMap map = new HashMap();
        map.put(str2, list);
        track(i2, str, map);
    }

    public void trackDomestic(int i2, String str, JSONObject jSONObject) {
        if (this.mPrivacyPolicy.isInternational()) {
            return;
        }
        track(i2, str, jSONObject);
    }

    public void track(int i2, String str, String str2) {
        try {
            track(i2, str, new JSONObject(str2));
        } catch (JSONException e2) {
            Log.i(TAG, "解析json错误: " + str2);
            e2.printStackTrace();
        }
    }

    public void track(int i2, String str, JSONObject jSONObject) {
        track(i2, str, DataConverter.fromJson(jSONObject));
    }

    public void track(int i2, String str, Map<String, Object> map) {
        trackCheckPrivacy(i2, str, map);
    }
}
