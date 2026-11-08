package com.xiaomi.push.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;
import com.xiaomi.channel.commonutils.android.AppInfoUtils;
import com.xiaomi.channel.commonutils.android.MIUIUtils;
import com.xiaomi.channel.commonutils.android.Region;
import com.xiaomi.channel.commonutils.logger.MyLog;
import com.xiaomi.channel.commonutils.network.NetworkInfoWrapper;
import com.xiaomi.mipush.sdk.Constants;
import com.xiaomi.onetrack.OneTrack;
import com.xiaomi.smack.SmackConfiguration;
import com.xiaomi.tinyData.TinyDataManager;
import com.xiaomi.xmpush.thrift.ClientUploadDataItem;
import com.xiaomi.xmpush.thrift.ConfigKey;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class HeartbeatHolder {
    private static final int ACCUMULATIVE_NAT_COUNT = 5;
    private static final int ACCUMULATIVE_NAT_MIN_COUNT = 3;
    private static final String FIELD_SEPARATOR = ":::";
    private static final String GROUP_SEPARATOR = "###";
    private static final int INVALID_HEARTBEAT_INTERVAL = -1;
    public static final String NETID_PREFIX_MOBILE = "M-";
    public static final String NETID_PREFIX_WIFI = "W-";
    public static final String NETID_UNKNOWN_WIFI = "WIFI-ID-UNKNOWN";
    private static final int NET_TYPE_MOBILE = 0;
    private static final int NET_TYPE_UNKNOWN = -1;
    private static final int NET_TYPE_WIFI = 1;
    private static final String PREFS_FILE_NAME = "hb_record";
    private static final long REPORT_SUPPORT_WIFI_DEGIST_PERIOD_MS = 1296000000;
    private static final int SAFETY_PING_INTERVAL_MS = 235000;
    private static final int SAFETY_PING_INTERVAL_SEC = 235;
    private static final int SHORT_HB_COUNT_CACHE_THRESHOLD = 5;
    private static final long SHORT_HB_EFFECTIVE_PERIOD_MS = 777600000;
    private static final String TINY_DATA_CATEGORY_HB_CHANGE = "category_hb_change";
    private static final String TINY_DATA_CATEGORY_HB_COUNT = "category_hb_count";
    private static final String TINY_DATA_CATEGORY_LC_PTC = "category_lc_ptc";
    private static final String TINY_DATA_CHANNEL_HB = "hb_channel";
    private static final String TINY_DATA_NAME_HB = "hb_name";
    private static final long UPLOAD_DATA_INTERVAL_MS = 259200000;

    @SuppressLint({"StaticFieldLeak"})
    private static volatile HeartbeatHolder sInstance;
    private final Context appContext;
    private final boolean mEnableIntelligentHb;
    private final boolean mIsXmsf;
    private long mPTCStart;
    private final SharedPreferences mSp;
    private final AtomicInteger mPTC = new AtomicInteger(0);
    private String mNetid = null;
    private volatile boolean mMonitoring = false;
    private String mNetidBeforePing = null;
    private final AtomicInteger mShortHbCount = new AtomicInteger(0);
    private final AtomicInteger mLongHbCount = new AtomicInteger(0);
    private int mNetType = -1;
    private long lastPingInterval = -1;

    private static class HbHelperCompat {
        private HbHelperCompat() {
        }

        public static String getHbChangeRecordKey() {
            return "record_hb_change";
        }

        public static String getHbCountStartRecordKey() {
            return "record_hb_count_start";
        }

        public static String getHbDeadTimeKey(String str) {
            return String.format("HB_dead_time_%s", str);
        }

        public static String getHbIntervalKey(String str) {
            return String.format("HB_%s", str);
        }

        public static String getKeepShortHbEffectiveTimeKey() {
            return "keep_short_hb_effective_time";
        }

        public static String getLongHbCountRecordKey() {
            return "record_long_hb_count";
        }

        public static String getMobilePTCRecordKey() {
            return "record_mobile_ptc";
        }

        public static String getPTCStartRecordKey() {
            return "record_ptc_start";
        }

        public static String getReportSupportWifiDigestRecordKey() {
            return "record_support_wifi_digest_reported";
        }

        public static String getShortHbCountRecordKey() {
            return "record_short_hb_count";
        }

        public static String getSupportWifiDigestKey() {
            return "support_wifi_digest";
        }

        public static String getSupportWifiDigestReportedTimeRecordKey() {
            return "record_support_wifi_digest_reported_time";
        }

        public static String getWifiPTCRecordKey() {
            return "record_wifi_ptc";
        }
    }

    private HeartbeatHolder(Context context) {
        this.appContext = context;
        this.mIsXmsf = MIUIUtils.isXMSF(context);
        this.mEnableIntelligentHb = OnlineConfig.getInstance(context).getBooleanValue(ConfigKey.IntelligentHeartbeatSwitchBoolean.getValue(), true);
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_FILE_NAME, 0);
        this.mSp = sharedPreferences;
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (sharedPreferences.getLong(HbHelperCompat.getHbCountStartRecordKey(), -1L) == -1) {
            sharedPreferences.edit().putLong(HbHelperCompat.getHbCountStartRecordKey(), jCurrentTimeMillis).apply();
        }
        long j2 = sharedPreferences.getLong(HbHelperCompat.getPTCStartRecordKey(), -1L);
        this.mPTCStart = j2;
        if (j2 == -1) {
            this.mPTCStart = jCurrentTimeMillis;
            sharedPreferences.edit().putLong(HbHelperCompat.getPTCStartRecordKey(), jCurrentTimeMillis).apply();
        }
    }

    private void checkDataUpload() {
        int i2;
        String[] strArrSplit;
        String[] strArrSplit2;
        if (shouldCollectAndUploadData()) {
            String string = this.mSp.getString(HbHelperCompat.getHbChangeRecordKey(), null);
            char c2 = 0;
            if (!TextUtils.isEmpty(string) && (strArrSplit = string.split(GROUP_SEPARATOR)) != null) {
                int i3 = 0;
                while (i3 < strArrSplit.length) {
                    if (!TextUtils.isEmpty(strArrSplit[i3]) && (strArrSplit2 = strArrSplit[i3].split(FIELD_SEPARATOR)) != null && strArrSplit2.length >= 4) {
                        String str = strArrSplit2[c2];
                        String str2 = strArrSplit2[1];
                        String str3 = strArrSplit2[2];
                        String str4 = strArrSplit2[3];
                        HashMap map = new HashMap();
                        map.put("event", "change");
                        map.put(OneTrack.Param.MODEL, Build.MODEL);
                        map.put("net_type", str2);
                        map.put("net_name", str);
                        map.put("interval", str3);
                        map.put("timestamp", str4);
                        doUpload(TINY_DATA_CATEGORY_HB_CHANGE, null, map);
                        MyLog.w("[HB] report hb changed events.");
                    }
                    i3++;
                    c2 = 0;
                }
                this.mSp.edit().remove(HbHelperCompat.getHbChangeRecordKey()).apply();
            }
            if (this.mSp.getBoolean(HbHelperCompat.getSupportWifiDigestKey(), false)) {
                long j2 = this.mSp.getLong(HbHelperCompat.getSupportWifiDigestReportedTimeRecordKey(), 0L);
                long jCurrentTimeMillis = System.currentTimeMillis();
                if (jCurrentTimeMillis - j2 > REPORT_SUPPORT_WIFI_DEGIST_PERIOD_MS) {
                    HashMap map2 = new HashMap();
                    map2.put("event", "support");
                    map2.put(OneTrack.Param.MODEL, Build.MODEL);
                    map2.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
                    doUpload(TINY_DATA_CATEGORY_HB_CHANGE, null, map2);
                    MyLog.w("[HB] report support wifi digest events.");
                    this.mSp.edit().putLong(HbHelperCompat.getSupportWifiDigestReportedTimeRecordKey(), jCurrentTimeMillis).apply();
                }
            }
            if (shouldUploadHbCountDataRightNow()) {
                int i4 = this.mSp.getInt(HbHelperCompat.getShortHbCountRecordKey(), 0);
                int i5 = this.mSp.getInt(HbHelperCompat.getLongHbCountRecordKey(), 0);
                if (i4 > 0 || i5 > 0) {
                    long j3 = this.mSp.getLong(HbHelperCompat.getHbCountStartRecordKey(), -1L);
                    String strValueOf = String.valueOf(SAFETY_PING_INTERVAL_MS);
                    String strValueOf2 = String.valueOf(j3);
                    String strValueOf3 = String.valueOf(System.currentTimeMillis());
                    try {
                        JSONObject jSONObject = new JSONObject();
                        jSONObject.put("interval", strValueOf);
                        jSONObject.put("c_short", String.valueOf(i4));
                        jSONObject.put("c_long", String.valueOf(i5));
                        jSONObject.put("count", String.valueOf(i4 + i5));
                        jSONObject.put("start_time", strValueOf2);
                        jSONObject.put("end_time", strValueOf3);
                        String string2 = jSONObject.toString();
                        HashMap map3 = new HashMap();
                        map3.put("event", "long_and_short_hb_count");
                        doUpload(TINY_DATA_CATEGORY_HB_COUNT, string2, map3);
                        MyLog.w("[HB] report short/long hb count events.");
                    } catch (Throwable unused) {
                    }
                }
                this.mSp.edit().putInt(HbHelperCompat.getShortHbCountRecordKey(), 0).putInt(HbHelperCompat.getLongHbCountRecordKey(), 0).putLong(HbHelperCompat.getHbCountStartRecordKey(), System.currentTimeMillis()).apply();
            }
            if (shouldUploadPTCDataRightNow()) {
                String strValueOf4 = String.valueOf(this.mPTCStart);
                String strValueOf5 = String.valueOf(System.currentTimeMillis());
                int i6 = this.mSp.getInt(HbHelperCompat.getMobilePTCRecordKey(), 0);
                if (i6 > 0) {
                    try {
                        JSONObject jSONObject2 = new JSONObject();
                        jSONObject2.put("net_type", "M");
                        jSONObject2.put("ptc", i6);
                        jSONObject2.put("start_time", strValueOf4);
                        jSONObject2.put("end_time", strValueOf5);
                        String string3 = jSONObject2.toString();
                        HashMap map4 = new HashMap();
                        map4.put("event", "ptc_event");
                        doUpload(TINY_DATA_CATEGORY_LC_PTC, string3, map4);
                        MyLog.w("[HB] report ping timeout count events of mobile network.");
                        this.mSp.edit().putInt(HbHelperCompat.getMobilePTCRecordKey(), 0).apply();
                    } catch (Throwable unused2) {
                        i2 = 0;
                        this.mSp.edit().putInt(HbHelperCompat.getMobilePTCRecordKey(), 0).apply();
                    }
                }
                i2 = 0;
                int i7 = this.mSp.getInt(HbHelperCompat.getWifiPTCRecordKey(), i2);
                if (i7 > 0) {
                    try {
                        JSONObject jSONObject3 = new JSONObject();
                        jSONObject3.put("net_type", "W");
                        jSONObject3.put("ptc", i7);
                        jSONObject3.put("start_time", strValueOf4);
                        jSONObject3.put("end_time", strValueOf5);
                        String string4 = jSONObject3.toString();
                        HashMap map5 = new HashMap();
                        map5.put("event", "ptc_event");
                        doUpload(TINY_DATA_CATEGORY_LC_PTC, string4, map5);
                        MyLog.w("[HB] report ping timeout count events of wifi network.");
                    } catch (Throwable unused3) {
                    }
                    this.mSp.edit().putInt(HbHelperCompat.getWifiPTCRecordKey(), 0).apply();
                }
                this.mPTCStart = System.currentTimeMillis();
                this.mSp.edit().putLong(HbHelperCompat.getPTCStartRecordKey(), this.mPTCStart).apply();
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:12:0x0058  */
    private void doUpload(String str, String str2, Map<String, String> map) {
        String str3;
        ClientUploadDataItem clientUploadDataItem = new ClientUploadDataItem();
        clientUploadDataItem.setCategory(str);
        clientUploadDataItem.setName(TINY_DATA_NAME_HB);
        clientUploadDataItem.setChannel(TINY_DATA_CHANNEL_HB);
        clientUploadDataItem.setCounter(1L);
        clientUploadDataItem.setData(str2);
        clientUploadDataItem.setFromSdk(false);
        clientUploadDataItem.setTimestamp(System.currentTimeMillis());
        clientUploadDataItem.setPkgName(this.appContext.getPackageName());
        clientUploadDataItem.setSourcePackage(PushConstants.PUSH_SERVICE_PACKAGE_NAME);
        if (map == null) {
            map = new HashMap<>();
        }
        MIPushAccount mIPushAccount = MIPushAccountUtils.getMIPushAccount(this.appContext);
        if (mIPushAccount == null || TextUtils.isEmpty(mIPushAccount.account)) {
            str3 = null;
        } else {
            String[] strArrSplit = mIPushAccount.account.split("@");
            if (strArrSplit.length > 0) {
                str3 = strArrSplit[0];
            } else {
                str3 = null;
            }
        }
        map.put("uuid", str3);
        map.put(OneTrack.Param.MODEL, Build.MODEL);
        Context context = this.appContext;
        map.put("avc", String.valueOf(AppInfoUtils.getVersionCode(context, context.getPackageName())));
        map.put("pvc", String.valueOf(PushConstants.PUSH_VERSION_CODE));
        map.put("cvc", String.valueOf(48));
        clientUploadDataItem.setExtra(map);
        TinyDataManager tinyDataManager = TinyDataManager.getInstance(this.appContext);
        if (tinyDataManager != null) {
            tinyDataManager.upload(clientUploadDataItem, this.appContext.getPackageName());
        }
    }

    private int getHeartbeatInterval() {
        if (TextUtils.isEmpty(this.mNetid)) {
            return -1;
        }
        try {
            return this.mSp.getInt(HbHelperCompat.getHbIntervalKey(this.mNetid), -1);
        } catch (Throwable unused) {
            return -1;
        }
    }

    public static HeartbeatHolder getInstance(Context context) {
        if (sInstance == null) {
            synchronized (HeartbeatHolder.class) {
                try {
                    if (sInstance == null) {
                        sInstance = new HeartbeatHolder(context);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return sInstance;
    }

    private long getShortHbEffectivePeriodMs() {
        return OnlineConfig.getInstance(this.appContext).getLongValue(ConfigKey.ShortHeartbeatEffectivePeriodMsLong.getValue(), SHORT_HB_EFFECTIVE_PERIOD_MS);
    }

    private long getShortHbEffectiveTime() {
        return this.mSp.getLong(HbHelperCompat.getKeepShortHbEffectiveTimeKey(), -1L);
    }

    private void handleNetIdLocked(String str) {
        if (NETID_UNKNOWN_WIFI.equals(str)) {
            String str2 = this.mNetid;
            if (str2 == null || !str2.startsWith(NETID_PREFIX_WIFI)) {
                this.mNetid = null;
            }
        } else {
            this.mNetid = str;
        }
        int i2 = this.mSp.getInt(HbHelperCompat.getHbIntervalKey(this.mNetid), -1);
        long j2 = this.mSp.getLong(HbHelperCompat.getHbDeadTimeKey(this.mNetid), -1L);
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (i2 != -1) {
            if (j2 == -1) {
                this.mSp.edit().putLong(HbHelperCompat.getHbDeadTimeKey(this.mNetid), jCurrentTimeMillis + getShortHbEffectivePeriodMs()).apply();
            } else if (jCurrentTimeMillis > j2) {
                this.mSp.edit().remove(HbHelperCompat.getHbIntervalKey(this.mNetid)).remove(HbHelperCompat.getHbDeadTimeKey(this.mNetid)).apply();
            }
        }
        this.mPTC.getAndSet(0);
        if (TextUtils.isEmpty(this.mNetid) || getHeartbeatInterval() != -1) {
            this.mMonitoring = false;
        } else {
            this.mMonitoring = true;
        }
        MyLog.w(String.format("[HB] network changed, netid:%s, %s", this.mNetid, Boolean.valueOf(this.mMonitoring)));
    }

    private boolean isIntelligentHeartbeatEnabled() {
        boolean z2 = getShortHbEffectiveTime() >= System.currentTimeMillis();
        if (this.mIsXmsf) {
            return this.mEnableIntelligentHb || z2;
        }
        return false;
    }

    private boolean isNetidValid(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return str.startsWith(NETID_PREFIX_WIFI) || str.startsWith(NETID_PREFIX_MOBILE);
    }

    private boolean notUseShortHeartbeat() {
        return (TextUtils.isEmpty(this.mNetid) || !this.mNetid.startsWith(NETID_PREFIX_MOBILE) || OnlineConfig.getInstance(this.appContext).getBooleanValue(ConfigKey.IntelligentHeartbeatUseInMobileNetworkBoolean.getValue(), false)) ? false : true;
    }

    private void recordHbCount(boolean z2) {
        if (shouldCollectAndUploadData()) {
            int iIncrementAndGet = (z2 ? this.mShortHbCount : this.mLongHbCount).incrementAndGet();
            MyLog.i(String.format("[HB] %s ping interval count: %s", z2 ? "short" : "long", Integer.valueOf(iIncrementAndGet)));
            if (iIncrementAndGet >= 5) {
                String shortHbCountRecordKey = z2 ? HbHelperCompat.getShortHbCountRecordKey() : HbHelperCompat.getLongHbCountRecordKey();
                int i2 = this.mSp.getInt(shortHbCountRecordKey, 0) + iIncrementAndGet;
                this.mSp.edit().putInt(shortHbCountRecordKey, i2).apply();
                MyLog.w(String.format("[HB] accumulate %s hb count(%s) and write to file. ", z2 ? "short" : "long", Integer.valueOf(i2)));
                if (z2) {
                    this.mShortHbCount.set(0);
                } else {
                    this.mLongHbCount.set(0);
                }
            }
        }
    }

    private void recordHbIntervalChanged(String str) {
        String str2;
        String string;
        if (shouldCollectAndUploadData() && !TextUtils.isEmpty(str)) {
            if (str.startsWith(NETID_PREFIX_WIFI)) {
                str2 = "W";
            } else if (!str.startsWith(NETID_PREFIX_MOBILE)) {
                return;
            } else {
                str2 = "M";
            }
            String strValueOf = String.valueOf(SAFETY_PING_INTERVAL_MS);
            String strValueOf2 = String.valueOf(System.currentTimeMillis() / 1000);
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append(FIELD_SEPARATOR);
            sb.append(str2);
            sb.append(FIELD_SEPARATOR);
            sb.append(strValueOf);
            sb.append(FIELD_SEPARATOR);
            sb.append(strValueOf2);
            String string2 = this.mSp.getString(HbHelperCompat.getHbChangeRecordKey(), null);
            if (TextUtils.isEmpty(string2)) {
                string = sb.toString();
            } else {
                string = string2 + GROUP_SEPARATOR + sb.toString();
            }
            this.mSp.edit().putString(HbHelperCompat.getHbChangeRecordKey(), string).apply();
        }
    }

    private void recordPingTimeout() {
        String mobilePTCRecordKey;
        int i2 = this.mNetType;
        if (i2 != 0) {
            mobilePTCRecordKey = i2 != 1 ? null : HbHelperCompat.getWifiPTCRecordKey();
        } else {
            mobilePTCRecordKey = HbHelperCompat.getMobilePTCRecordKey();
        }
        if (TextUtils.isEmpty(mobilePTCRecordKey)) {
            return;
        }
        if (this.mSp.getLong(HbHelperCompat.getPTCStartRecordKey(), -1L) == -1) {
            this.mPTCStart = System.currentTimeMillis();
            this.mSp.edit().putLong(HbHelperCompat.getPTCStartRecordKey(), this.mPTCStart).apply();
        }
        this.mSp.edit().putInt(mobilePTCRecordKey, this.mSp.getInt(mobilePTCRecordKey, 0) + 1).apply();
    }

    private void recordSupportWifiDigestIfNeed() {
        if (this.mSp.getBoolean(HbHelperCompat.getSupportWifiDigestKey(), false)) {
            return;
        }
        this.mSp.edit().putBoolean(HbHelperCompat.getSupportWifiDigestKey(), true).apply();
    }

    private void setHeartbeatInterval(String str) {
        if (isNetidValid(str)) {
            this.mSp.edit().putInt(HbHelperCompat.getHbIntervalKey(str), SAFETY_PING_INTERVAL_MS).apply();
            this.mSp.edit().putLong(HbHelperCompat.getHbDeadTimeKey(this.mNetid), System.currentTimeMillis() + getShortHbEffectivePeriodMs()).apply();
        }
    }

    private boolean shouldChangeHeartbeatInterval() {
        return this.mPTC.get() >= Math.max(OnlineConfig.getInstance(this.appContext).getIntValue(ConfigKey.IntelligentHeartbeatNATCountInt.getValue(), 5), 3);
    }

    private boolean shouldCollectAndUploadData() {
        return isIntelligentHeartbeatEnabled() && OnlineConfig.getInstance(this.appContext).getBooleanValue(ConfigKey.IntelligentHeartbeatDataCollectSwitchBoolean.getValue(), true) && Region.China.name().equals(AppRegionStorage.getInstance(this.appContext).getRegion());
    }

    private boolean shouldUploadHbCountDataRightNow() {
        long j2 = this.mSp.getLong(HbHelperCompat.getHbCountStartRecordKey(), -1L);
        if (j2 == -1) {
            return false;
        }
        long jCurrentTimeMillis = System.currentTimeMillis();
        return j2 > jCurrentTimeMillis || jCurrentTimeMillis - j2 >= 259200000;
    }

    private boolean shouldUploadPTCDataRightNow() {
        if (this.mPTCStart == -1) {
            return false;
        }
        long jCurrentTimeMillis = System.currentTimeMillis();
        long j2 = this.mPTCStart;
        return j2 > jCurrentTimeMillis || jCurrentTimeMillis - j2 >= 259200000;
    }

    public long getLastPingInterval() {
        return this.lastPingInterval;
    }

    public long getPingIntervalAndRecordEvents() {
        int heartbeatInterval;
        long pingInteval = SmackConfiguration.getPingInteval();
        if (this.mIsXmsf && !notUseShortHeartbeat() && ((OnlineConfig.getInstance(this.appContext).getBooleanValue(ConfigKey.IntelligentHeartbeatSwitchBoolean.getValue(), true) || getShortHbEffectiveTime() >= System.currentTimeMillis()) && (heartbeatInterval = getHeartbeatInterval()) != -1)) {
            pingInteval = heartbeatInterval;
        }
        if (!TextUtils.isEmpty(this.mNetid) && !NETID_UNKNOWN_WIFI.equals(this.mNetid) && this.mNetType == 1) {
            recordHbCount(pingInteval < Constants.ASSEMBLE_PUSH_NETWORK_INTERVAL);
        }
        this.lastPingInterval = pingInteval;
        MyLog.w("[HB] ping interval:" + pingInteval);
        return pingInteval;
    }

    public synchronized void onNetworkChanged(NetworkInfoWrapper networkInfoWrapper) {
        try {
            if (isIntelligentHeartbeatEnabled()) {
                String str = null;
                if (networkInfoWrapper == null) {
                    handleNetIdLocked(null);
                    this.mNetType = -1;
                } else if (networkInfoWrapper.getType() == 0) {
                    String subtypeName = networkInfoWrapper.getSubtypeName();
                    if (!TextUtils.isEmpty(subtypeName) && !"UNKNOWN".equalsIgnoreCase(subtypeName)) {
                        str = NETID_PREFIX_MOBILE + subtypeName;
                    }
                    handleNetIdLocked(str);
                    this.mNetType = 0;
                } else if (networkInfoWrapper.getType() == 1 || networkInfoWrapper.getType() == 6) {
                    handleNetIdLocked(NETID_UNKNOWN_WIFI);
                    this.mNetType = 1;
                } else {
                    handleNetIdLocked(null);
                    this.mNetType = -1;
                }
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    public void onPing() {
        if (isIntelligentHeartbeatEnabled()) {
            this.mNetidBeforePing = this.mNetid;
        }
    }

    public void onPingTimeout() {
        if (isIntelligentHeartbeatEnabled()) {
            recordPingTimeout();
            if (this.mMonitoring && !TextUtils.isEmpty(this.mNetid) && this.mNetid.equals(this.mNetidBeforePing)) {
                this.mPTC.getAndIncrement();
                MyLog.w("[HB] ping timeout count:" + this.mPTC);
                if (shouldChangeHeartbeatInterval()) {
                    MyLog.w("[HB] change hb interval for net:" + this.mNetid);
                    setHeartbeatInterval(this.mNetid);
                    this.mMonitoring = false;
                    this.mPTC.getAndSet(0);
                    recordHbIntervalChanged(this.mNetid);
                }
            }
        }
    }

    public void onPong() {
        if (isIntelligentHeartbeatEnabled()) {
            checkDataUpload();
            if (this.mMonitoring) {
                this.mPTC.getAndSet(0);
            }
        }
    }

    public synchronized void onWifiChanged(String str) {
        try {
            if (!TextUtils.isEmpty(str)) {
                recordSupportWifiDigestIfNeed();
            }
            if (isIntelligentHeartbeatEnabled() && !TextUtils.isEmpty(str)) {
                handleNetIdLocked(NETID_PREFIX_WIFI + str);
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    public void updateShortHbEffectivePeriod(int i2) {
        this.mSp.edit().putLong(HbHelperCompat.getKeepShortHbEffectiveTimeKey(), System.currentTimeMillis() + ((long) (i2 * 1000))).apply();
    }

    public void init() {
    }
}
