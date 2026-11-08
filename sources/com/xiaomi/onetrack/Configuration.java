package com.xiaomi.onetrack;

import android.text.TextUtils;

/* JADX INFO: loaded from: classes.dex */
public class Configuration {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private String f774a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private String f775b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private String f776c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private boolean f777d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private String f778e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private OneTrack.Mode f779f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private boolean f780g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private boolean f781h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private boolean f782i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private boolean f783j;

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private boolean f784k;

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    private boolean f785l;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    private String f786m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    private boolean f787n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    private String f788o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    private OneTrack.IEventHook f789p;

    public static class Builder {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private String f790a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        private String f791b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        private String f792c;

        /* JADX INFO: renamed from: d, reason: collision with root package name */
        private boolean f793d;

        /* JADX INFO: renamed from: e, reason: collision with root package name */
        private String f794e;

        /* JADX INFO: renamed from: m, reason: collision with root package name */
        private String f802m;

        /* JADX INFO: renamed from: o, reason: collision with root package name */
        private String f804o;

        /* JADX INFO: renamed from: f, reason: collision with root package name */
        private OneTrack.Mode f795f = OneTrack.Mode.APP;

        /* JADX INFO: renamed from: g, reason: collision with root package name */
        private boolean f796g = true;

        /* JADX INFO: renamed from: h, reason: collision with root package name */
        private boolean f797h = true;

        /* JADX INFO: renamed from: i, reason: collision with root package name */
        private boolean f798i = true;

        /* JADX INFO: renamed from: j, reason: collision with root package name */
        private boolean f799j = false;

        /* JADX INFO: renamed from: k, reason: collision with root package name */
        private boolean f800k = true;

        /* JADX INFO: renamed from: l, reason: collision with root package name */
        private boolean f801l = false;

        /* JADX INFO: renamed from: n, reason: collision with root package name */
        private boolean f803n = false;

        public Configuration build() {
            return new Configuration(this);
        }

        public Builder setAdEventAppId(String str) {
            this.f804o = str;
            return this;
        }

        public Builder setAppId(String str) {
            this.f790a = str;
            return this;
        }

        public Builder setAutoTrackActivityAction(boolean z2) {
            this.f800k = z2;
            return this;
        }

        public Builder setChannel(String str) {
            this.f792c = str;
            return this;
        }

        public Builder setExceptionCatcherEnable(boolean z2) {
            this.f799j = z2;
            return this;
        }

        @Deprecated
        public Builder setGAIDEnable(boolean z2) {
            this.f796g = z2;
            return this;
        }

        public Builder setImeiEnable(boolean z2) {
            this.f798i = z2;
            return this;
        }

        public Builder setImsiEnable(boolean z2) {
            this.f797h = z2;
            return this;
        }

        public Builder setInstanceId(String str) {
            this.f802m = str;
            return this;
        }

        public Builder setInternational(boolean z2) {
            this.f793d = z2;
            return this;
        }

        public Builder setMode(OneTrack.Mode mode) {
            this.f795f = mode;
            return this;
        }

        public Builder setOverrideMiuiRegionSetting(boolean z2) {
            this.f801l = z2;
            return this;
        }

        public Builder setPluginId(String str) {
            this.f791b = str;
            return this;
        }

        public Builder setRegion(String str) {
            this.f794e = str;
            return this;
        }

        public Builder setUseCustomPrivacyPolicy(boolean z2) {
            this.f803n = z2;
            return this;
        }
    }

    private String a(String str) {
        StringBuilder sb = new StringBuilder();
        if (TextUtils.isEmpty(str) || str.length() <= 4) {
            sb.append(str);
        } else {
            for (int i2 = 0; i2 < str.length(); i2++) {
                if (i2 == 0 || i2 == 1 || i2 == str.length() - 2 || i2 == str.length() - 1) {
                    sb.append(str.charAt(i2));
                } else {
                    sb.append("*");
                }
            }
        }
        return sb.toString();
    }

    public String getAdEventAppId() {
        return this.f788o;
    }

    public String getAppId() {
        return this.f774a;
    }

    public String getChannel() {
        return this.f776c;
    }

    public String getInstanceId() {
        return this.f786m;
    }

    public OneTrack.Mode getMode() {
        return this.f779f;
    }

    public String getPluginId() {
        return this.f775b;
    }

    public String getRegion() {
        return this.f778e;
    }

    public boolean isAutoTrackActivityAction() {
        return this.f784k;
    }

    public boolean isExceptionCatcherEnable() {
        return this.f783j;
    }

    @Deprecated
    public boolean isGAIDEnable() {
        return this.f780g;
    }

    public boolean isIMEIEnable() {
        return this.f782i;
    }

    public boolean isIMSIEnable() {
        return this.f781h;
    }

    public boolean isInternational() {
        return this.f777d;
    }

    public boolean isOverrideMiuiRegionSetting() {
        return this.f785l;
    }

    public boolean isUseCustomPrivacyPolicy() {
        return this.f787n;
    }

    public String toString() {
        try {
            return "Configuration{appId='" + a(this.f774a) + "', pluginId='" + a(this.f775b) + "', channel='" + this.f776c + "', international=" + this.f777d + ", region='" + this.f778e + "', overrideMiuiRegionSetting=" + this.f785l + ", mode=" + this.f779f + ", GAIDEnable=" + this.f780g + ", IMSIEnable=" + this.f781h + ", IMEIEnable=" + this.f782i + ", ExceptionCatcherEnable=" + this.f783j + ", instanceId=" + a(this.f786m) + '}';
        } catch (Exception unused) {
            return "";
        }
    }

    private Configuration(Builder builder) {
        this.f779f = OneTrack.Mode.APP;
        this.f780g = true;
        this.f781h = true;
        this.f782i = true;
        this.f784k = true;
        this.f785l = false;
        this.f787n = false;
        this.f774a = builder.f790a;
        this.f775b = builder.f791b;
        this.f776c = builder.f792c;
        this.f777d = builder.f793d;
        this.f778e = builder.f794e;
        this.f779f = builder.f795f;
        this.f780g = builder.f796g;
        this.f782i = builder.f798i;
        this.f781h = builder.f797h;
        this.f783j = builder.f799j;
        this.f784k = builder.f800k;
        this.f785l = builder.f801l;
        this.f786m = builder.f802m;
        this.f787n = builder.f803n;
        this.f788o = builder.f804o;
    }
}
