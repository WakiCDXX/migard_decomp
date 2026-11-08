package com.miui.powerkeeper.tracker;

import android.content.Context;
import com.miui.powerkeeper.batterylife.TrackData;
import com.ot.pubsub.PubSubTrack;
import com.xiaomi.onetrack.Configuration;
import com.xiaomi.onetrack.OneTrack;
import java.util.Map;
import r.b;

/* JADX INFO: loaded from: classes.dex */
public class Tracker {
    private Context mContext;
    private boolean mInternational;
    private OneTrack mOneTrack;
    private TrackPolicy mPolicy;
    private PubSubTrack mPubSubTrack;

    public static class TrackPolicy {
        private boolean mAllowTrack;
        private String mAppId;
        private String mChannel;
        private String mEvent;
        private boolean mInternational;
        private int mModule;
        private String mPrivateKeyId;
        private String mProjectId;
        private String mTopic;

        public TrackPolicy(boolean z2, int i2, String str) {
            this.mInternational = z2;
            this.mModule = i2;
            this.mEvent = str;
            shunt();
        }

        private void shunt() {
            String str;
            if (this.mInternational) {
                str = this.mEvent + TrackerConfig.EVENT_SUFFIX;
            } else {
                str = this.mEvent;
            }
            this.mEvent = str;
            this.mAllowTrack = true;
            int i2 = this.mModule;
            if (i2 == 0) {
                this.mTopic = "mqs_power";
                this.mAppId = this.mInternational ? "31000000922" : TrackData.ONE_TRACK_APPID;
                return;
            }
            if (i2 == 1) {
                this.mTopic = "mqs_thermal";
                this.mAppId = this.mInternational ? "31000000917" : "31000000124";
                return;
            }
            if (i2 == 2) {
                if (this.mInternational) {
                    this.mAllowTrack = false;
                }
                this.mTopic = "";
                this.mAppId = "31000000428";
                return;
            }
            if (i2 == 3) {
                if (this.mInternational) {
                    this.mAllowTrack = false;
                }
                this.mTopic = "";
                this.mAppId = "31000000480";
                return;
            }
            if (i2 == 4) {
                if (this.mInternational) {
                    this.mAllowTrack = false;
                }
                this.mTopic = "";
                this.mAppId = "31000000483";
                return;
            }
            if (i2 != 5) {
                this.mAllowTrack = false;
                return;
            }
            if (this.mInternational) {
                this.mAllowTrack = false;
            }
            this.mTopic = "";
            this.mAppId = "31000001000";
        }

        public boolean equals(Object obj) {
            TrackPolicy trackPolicy = (TrackPolicy) obj;
            return trackPolicy.getAppId().equals(this.mAppId) && trackPolicy.isInternational() == this.mInternational;
        }

        public String getAppId() {
            return this.mAppId;
        }

        public String getChannel() {
            return this.mChannel;
        }

        public String getEvent() {
            return this.mEvent;
        }

        public String getPrivateKeyId() {
            return this.mPrivateKeyId;
        }

        public String getProjectId() {
            return this.mProjectId;
        }

        public String getTopic() {
            return this.mTopic;
        }

        public int hashCode() {
            return (this.mAppId + this.mInternational).hashCode();
        }

        public boolean isAllowTrack() {
            return this.mAllowTrack;
        }

        public boolean isInternational() {
            return this.mInternational;
        }

        public TrackPolicy setChannel(String str) {
            this.mChannel = str;
            return this;
        }

        public TrackPolicy setPrivateKeyId(String str) {
            this.mPrivateKeyId = str;
            return this;
        }

        public TrackPolicy setProjectId(String str) {
            this.mProjectId = str;
            return this;
        }

        public String toString() {
            return "TrackPolicy{mInternational=" + this.mInternational + ", mModule=" + this.mModule + ", mAppId='" + this.mAppId + "', mEvent='" + this.mEvent + "', mChannel='" + this.mChannel + "', mTopic='" + this.mTopic + "', mProjectId='" + this.mProjectId + "', mPrivateKeyId='" + this.mPrivateKeyId + "', mAllowTrack=" + this.mAllowTrack + '}';
        }
    }

    public Tracker(Context context, TrackPolicy trackPolicy) {
        this.mContext = context;
        this.mPolicy = trackPolicy;
        boolean z2 = trackPolicy.mInternational;
        this.mInternational = z2;
        if (z2) {
            this.mPubSubTrack = PubSubTrack.b(this.mContext.getApplicationContext(), new b.a().l(this.mPolicy.getAppId()).m(this.mPolicy.getChannel()).r(this.mPolicy.getProjectId()).q(this.mPolicy.getPrivateKeyId()).c());
        } else {
            this.mOneTrack = OneTrack.createInstance(this.mContext.getApplicationContext(), new Configuration.Builder().setAppId(this.mPolicy.getAppId()).setChannel(this.mPolicy.getChannel()).setMode(OneTrack.Mode.APP).setAutoTrackActivityAction(true).build());
        }
    }

    public static void setAccessNetworkEnable(Context context, boolean z2) {
        PubSubTrack.d(context, z2);
    }

    public static void setDebugMode(boolean z2) {
        PubSubTrack.e(z2);
    }

    public static void setDisable(boolean z2) {
        PubSubTrack.f(z2);
    }

    public boolean isInternational() {
        return this.mInternational;
    }

    public void track(TrackPolicy trackPolicy, Map<String, Object> map) {
        if (this.mInternational) {
            this.mPubSubTrack.g(this.mPolicy.getTopic(), trackPolicy.getEvent(), map);
        } else {
            this.mOneTrack.track(trackPolicy.getEvent(), map);
        }
    }
}
