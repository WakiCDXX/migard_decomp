package com.xiaomi.onetrack;

import android.text.TextUtils;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class ServiceQualityEvent {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private String f848a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private String f849b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private Integer f850c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private String f851d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private String f852e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private Integer f853f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private Integer f854g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private String f855h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private String f856i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private Integer f857j;

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private Long f858k;

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    private Long f859l;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    private Long f860m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    private Long f861n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    private Long f862o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    private Long f863p;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    private Long f864q;

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    private Long f865r;

    /* JADX INFO: renamed from: s, reason: collision with root package name */
    private String f866s;

    /* JADX INFO: renamed from: t, reason: collision with root package name */
    private String f867t;

    /* JADX INFO: renamed from: u, reason: collision with root package name */
    private Map<String, Object> f868u;

    public static final class Builder {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private String f869a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        private String f870b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        private Integer f871c;

        /* JADX INFO: renamed from: d, reason: collision with root package name */
        private String f872d;

        /* JADX INFO: renamed from: e, reason: collision with root package name */
        private String f873e;

        /* JADX INFO: renamed from: f, reason: collision with root package name */
        private Integer f874f;

        /* JADX INFO: renamed from: g, reason: collision with root package name */
        private Integer f875g;

        /* JADX INFO: renamed from: h, reason: collision with root package name */
        private String f876h;

        /* JADX INFO: renamed from: i, reason: collision with root package name */
        private ResultType f877i;

        /* JADX INFO: renamed from: j, reason: collision with root package name */
        private Integer f878j;

        /* JADX INFO: renamed from: k, reason: collision with root package name */
        private Long f879k;

        /* JADX INFO: renamed from: l, reason: collision with root package name */
        private Long f880l;

        /* JADX INFO: renamed from: m, reason: collision with root package name */
        private Long f881m;

        /* JADX INFO: renamed from: n, reason: collision with root package name */
        private Long f882n;

        /* JADX INFO: renamed from: o, reason: collision with root package name */
        private Long f883o;

        /* JADX INFO: renamed from: p, reason: collision with root package name */
        private Long f884p;

        /* JADX INFO: renamed from: q, reason: collision with root package name */
        private Long f885q;

        /* JADX INFO: renamed from: r, reason: collision with root package name */
        private Long f886r;

        /* JADX INFO: renamed from: s, reason: collision with root package name */
        private OneTrack.NetType f887s;

        /* JADX INFO: renamed from: t, reason: collision with root package name */
        private String f888t;

        /* JADX INFO: renamed from: u, reason: collision with root package name */
        private Map<String, Object> f889u;

        public ServiceQualityEvent build() {
            return new ServiceQualityEvent(this);
        }

        public Builder setDnsLookupTime(Long l2) {
            this.f879k = l2;
            return this;
        }

        public Builder setDuration(Long l2) {
            this.f885q = l2;
            return this;
        }

        public Builder setExceptionTag(String str) {
            this.f876h = str;
            return this;
        }

        public Builder setExtraParams(Map<String, Object> map) {
            this.f889u = map;
            return this;
        }

        public Builder setHandshakeTime(Long l2) {
            this.f881m = l2;
            return this;
        }

        public Builder setHost(String str) {
            this.f870b = str;
            return this;
        }

        public Builder setIps(String... strArr) {
            if (strArr != null) {
                this.f873e = TextUtils.join(",", strArr);
            }
            return this;
        }

        public Builder setNetSdkVersion(String str) {
            this.f888t = str;
            return this;
        }

        public Builder setPath(String str) {
            this.f872d = str;
            return this;
        }

        public Builder setPort(Integer num) {
            this.f871c = num;
            return this;
        }

        public Builder setReceiveAllByteTime(Long l2) {
            this.f884p = l2;
            return this;
        }

        public Builder setReceiveFirstByteTime(Long l2) {
            this.f883o = l2;
            return this;
        }

        public Builder setRequestDataSendTime(Long l2) {
            this.f882n = l2;
            return this;
        }

        public Builder setRequestNetType(OneTrack.NetType netType) {
            this.f887s = netType;
            return this;
        }

        public Builder setRequestTimestamp(Long l2) {
            this.f886r = l2;
            return this;
        }

        public Builder setResponseCode(Integer num) {
            this.f874f = num;
            return this;
        }

        public Builder setResultType(ResultType resultType) {
            this.f877i = resultType;
            return this;
        }

        public Builder setRetryCount(Integer num) {
            this.f878j = num;
            return this;
        }

        public Builder setScheme(String str) {
            this.f869a = str;
            return this;
        }

        public Builder setStatusCode(Integer num) {
            this.f875g = num;
            return this;
        }

        public Builder setTcpConnectTime(Long l2) {
            this.f880l = l2;
            return this;
        }
    }

    public enum ResultType {
        SUCCESS("ok"),
        FAILED("failed"),
        TIMEOUT("timeout");


        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private String f891a;

        ResultType(String str) {
            this.f891a = str;
        }

        public String getResultType() {
            return this.f891a;
        }
    }

    public Long getDnsLookupTime() {
        return this.f858k;
    }

    public Long getDuration() {
        return this.f864q;
    }

    public String getExceptionTag() {
        return this.f855h;
    }

    public Map<String, Object> getExtraParams() {
        return this.f868u;
    }

    public Long getHandshakeTime() {
        return this.f860m;
    }

    public String getHost() {
        return this.f849b;
    }

    public String getIps() {
        return this.f852e;
    }

    public String getNetSdkVersion() {
        return this.f867t;
    }

    public String getPath() {
        return this.f851d;
    }

    public Integer getPort() {
        return this.f850c;
    }

    public Long getReceiveAllByteTime() {
        return this.f863p;
    }

    public Long getReceiveFirstByteTime() {
        return this.f862o;
    }

    public Long getRequestDataSendTime() {
        return this.f861n;
    }

    public String getRequestNetType() {
        return this.f866s;
    }

    public Long getRequestTimestamp() {
        return this.f865r;
    }

    public Integer getResponseCode() {
        return this.f853f;
    }

    public String getResultType() {
        return this.f856i;
    }

    public Integer getRetryCount() {
        return this.f857j;
    }

    public String getScheme() {
        return this.f848a;
    }

    public Integer getStatusCode() {
        return this.f854g;
    }

    public Long getTcpConnectTime() {
        return this.f859l;
    }

    private ServiceQualityEvent(Builder builder) {
        this.f848a = builder.f869a;
        this.f849b = builder.f870b;
        this.f850c = builder.f871c;
        this.f851d = builder.f872d;
        this.f852e = builder.f873e;
        this.f853f = builder.f874f;
        this.f854g = builder.f875g;
        this.f855h = builder.f876h;
        this.f856i = builder.f877i != null ? builder.f877i.getResultType() : null;
        this.f857j = builder.f878j;
        this.f858k = builder.f879k;
        this.f859l = builder.f880l;
        this.f860m = builder.f881m;
        this.f862o = builder.f883o;
        this.f863p = builder.f884p;
        this.f865r = builder.f886r;
        this.f866s = builder.f887s != null ? builder.f887s.toString() : null;
        this.f861n = builder.f882n;
        this.f864q = builder.f885q;
        this.f867t = builder.f888t;
        this.f868u = builder.f889u;
    }
}
