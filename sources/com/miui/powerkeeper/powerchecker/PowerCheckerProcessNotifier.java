package com.miui.powerkeeper.powerchecker;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.Log;
import com.google.android.collect.Lists;
import com.miui.powerkeeper.utils.PackageUtil;
import com.miui.powerkeeper.utils.ShellUtils;
import com.xiaomi.push.mpcd.Constants;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class PowerCheckerProcessNotifier extends PowerCheckerNotifier {
    public static final String KEY_ACTION = "action";
    public static final String KEY_ACTIVE_TYPE = "active_type";
    public static final String KEY_APP_HEAVY = "app_heavy";
    public static final String KEY_BATTERY_LEVEL = "battery_level";
    public static final String KEY_CONSECUTIVE_COUNT = "consecutive_count";
    public static final String KEY_FG_CPU_TIME = "fg_cpu_time";
    public static final String KEY_FULL_WIFI_LOCK_TIME = "full_wifi_lock_time";
    public static final String KEY_GPS_COUNT = "gps_count";
    public static final String KEY_GPS_TIME = "gps_time";
    public static final String KEY_ITEMS = "items";
    public static final String KEY_KERNEL_TYPE = "kernel_type";
    public static final String KEY_KERNEL_WAKELOCK_COUNT = "kernel_wakelock_count";
    public static final String KEY_KERNEL_WAKELOCK_TIME = "kernel_wakelock_time";
    public static final String KEY_KERNEL_WAKEUP_COUNT = "kernel_wakeup_count";
    public static final String KEY_KERNEL_WAKEUP_TIME = "kernel_wakeup_time";
    public static final String KEY_KILL_SUCCESS = "kill_suc";
    public static final String KEY_KWL_COUNT = "kwl_count";
    public static final String KEY_KWL_NAME = "kwl_name";
    public static final String KEY_KWL_TIME = "kwl_time";
    public static final String KEY_MOBILE_ACTIVE_COUNT = "mobile_active_count";
    public static final String KEY_MOBILE_ACTIVE_TIME = "mobile_active_time";
    public static final String KEY_MOBILE_BG_RX_BYTES = "mobile_bg_rx_bytes";
    public static final String KEY_MOBILE_BG_RX_PACKETS = "mobile_bg_rx_packets";
    public static final String KEY_MOBILE_BG_TX_BYTES = "mobile_bg_tx_bytes";
    public static final String KEY_MOBILE_BG_TX_PACKETS = "mobile_bg_tx_packets";
    public static final String KEY_MOBILE_RX_BYTES = "mobile_rx_bytes";
    public static final String KEY_MOBILE_RX_PACKETS = "mobile_rx_packets";
    public static final String KEY_MOBILE_TX_BYTES = "mobile_tx_bytes";
    public static final String KEY_MOBILE_TX_PACKETS = "mobile_tx_packets";
    public static final String KEY_NOTIFY_REASON = "notify_reason";
    public static final String KEY_PACKAGE_NAME = "package_name";
    public static final String KEY_PACKAGE_VERSION = "package_version";
    public static final String KEY_PRIORITY = "priority";
    public static final String KEY_PROCESS_RESULT = "process_result";
    public static final String KEY_PWL = "pwl";
    public static final String KEY_PWL_COUNT = "pwl_count";
    public static final String KEY_PWL_NAME = "pwl_name";
    public static final String KEY_PWL_TIME = "pwl_time";
    public static final String KEY_RECORD_TIME = "record_time";
    public static final String KEY_SUB_TYPE = "sub_type";
    public static final String KEY_SUMMARY = "summary";
    public static final String KEY_SYNC = "sync";
    public static final String KEY_SYNC_COUNT = "sync_count";
    public static final String KEY_SYNC_NAME = "sync_name";
    public static final String KEY_SYNC_TIME = "sync_time";
    public static final String KEY_SYSTEM_CPU_TIME = "system_cpu_time";
    public static final String KEY_TIME_ON_BATTERY = "time_on_battery";
    public static final String KEY_TYPE = "type";
    public static final String KEY_UID = "uid";
    public static final String KEY_USER_CPU_TIME = "user_cpu_time";
    public static final String KEY_WAKEUP_ALARM = "wakeup_alarm";
    public static final String KEY_WIFI_BG_RX_BYTES = "wifi_bg_rx_bytes";
    public static final String KEY_WIFI_BG_RX_PACKETS = "wifi_bg_rx_packets";
    public static final String KEY_WIFI_BG_TX_BYTES = "wifi_bg_tx_bytes";
    public static final String KEY_WIFI_BG_TX_PACKETS = "wifi_bg_tx_packets";
    public static final String KEY_WIFI_RX_BYTES = "wifi_rx_bytes";
    public static final String KEY_WIFI_RX_PACKETS = "wifi_rx_packets";
    public static final String KEY_WIFI_SCAN_TIME = "wifi_scan_time";
    public static final String KEY_WIFI_TX_BYTES = "wifi_tx_bytes";
    public static final String KEY_WIFI_TX_PACKETS = "wifi_tx_packets";
    public static final String KEY_kWL = "kwl";
    private static final String TAG = "PowerChecker.PrNotifier";
    private static final String[] sRecordTypeStringMap = {"processed_kernel_wakelock_hold_too_long", "processed_android_wakelock_hold_too_long", "processed_wifi_scan_too_many_times", "processed_hold_gps_at_background_too_long", "processed_consume_too_much_cpu_at_background", "processed_alarm_wakeup_device_too_many_times", "processed_mobile_radio_active_too_many_times", "processed_unknown_reason_excessive_power", "processed_sub_system_excessive_power"};
    private Context mContext;
    private PackageManager mPackageManager;
    private HashMap<String, PowerExceedRecord> mPlatformWakelockRecords = new HashMap<>();
    private HashMap<Integer, PowerExceedRecord> mUidWifiScanRecords = new HashMap<>();
    private HashMap<Integer, PowerExceedRecord> mUidBgGPSTimeRecords = new HashMap<>();
    private HashMap<Integer, PowerExceedRecord> mUidPartialWaklockRecords = new HashMap<>();
    private HashMap<Integer, PowerExceedRecord> mUidMobileRadioRecords = new HashMap<>();
    private HashMap<String, PowerExceedRecord> mProcBgCPUTimeRecords = new HashMap<>();
    private HashMap<String, PowerExceedRecord> mPkgWakeupTimesRecords = new HashMap<>();
    private PowerExceedRecord mUnknownReasonRecord = null;
    private PowerExceedRecord mSubSystemRecord = null;

    private class PowerExceedRecord {
        public int mAction;
        public long mBatteryRealTime;
        public StringBuilder mKeywords;
        public String mPackageName;
        public String mPackageVersion;
        public int mPriority;
        public String mProcName;
        public int mRecordType;
        public StringBuilder mSummaryDesc;
        public int mUid;
        public List<RecordTimer> mKernelWakelocks = Lists.newArrayList();
        public List<RecordTimer> mPartialWakelocks = Lists.newArrayList();
        public List<RecordTimer> mSyncStats = Lists.newArrayList();
        public long mRecordTime = System.currentTimeMillis();
        public boolean mKillResult = true;
        public int mActiveType = 0;
        public int mProcessResult = -1;
        public int mSubType = 0;
        public int mConsecutiveCount = 0;
        public int mBatteryLevel = -1;
        public int mKernelType = 0;
        public long mKernelWakelockTime = 0;
        public int mKernelWakelockCount = 0;
        public long mKernelWakeupTime = 0;
        public int mKernelWakeupCount = 0;
        public long mMobileActiveTime = 0;
        public int mMobileActiveCount = 0;
        public long mMobileRxBytes = 0;
        public long mMobileTxBytes = 0;
        public long mMobileRxPackets = 0;
        public long mMobileTxPackets = 0;
        public long mWifiRxBytes = 0;
        public long mWifiTxBytes = 0;
        public long mWifiRxPackets = 0;
        public long mWifiTxPackets = 0;
        public long mMobileBgRxBytes = 0;
        public long mMobileBgTxBytes = 0;
        public long mMobileBgRxPackets = 0;
        public long mMobileBgTxPackets = 0;
        public long mWifiBgRxBytes = 0;
        public long mWifiBgTxBytes = 0;
        public long mWifiBgRxPackets = 0;
        public long mWifiBgTxPackets = 0;
        public long mFullWifiLockTime = 0;
        public long mWifiScanTime = 0;
        public long mGpsSensorHoldTime = 0;
        public int mGpsCount = 0;
        public long mTotalUserCpuTime = 0;
        public long mTotalSystemCpuTime = 0;
        public long mTotalFgCpuTime = 0;
        public int mWakeupTimes = 0;

        protected PowerExceedRecord(int i2, int i3, String str, String str2, String str3, int i4, int i5, StringBuilder sb, StringBuilder sb2, long j2) {
            this.mUid = i2;
            this.mRecordType = i3;
            this.mBatteryRealTime = j2;
            this.mPackageName = str;
            this.mPackageVersion = str2;
            this.mProcName = str3;
            this.mPriority = i4;
            this.mAction = i5;
            this.mKeywords = sb;
            this.mSummaryDesc = sb2;
            this.mKernelWakelocks.clear();
            this.mPartialWakelocks.clear();
            this.mSyncStats.clear();
        }

        public JSONObject toJson() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("type", this.mRecordType);
                jSONObject.put("priority", this.mPriority);
                jSONObject.put("action", this.mAction);
                jSONObject.put("uid", this.mUid);
                Object obj = this.mPackageName;
                if (obj == null) {
                    obj = this.mProcName;
                }
                jSONObject.put("package_name", obj);
                Object obj2 = this.mPackageVersion;
                if (obj2 != null) {
                    jSONObject.put("package_version", obj2);
                }
                jSONObject.put("record_time", simpleDateFormat.format(new Date(this.mRecordTime)));
                jSONObject.put(PowerCheckerProcessNotifier.KEY_TIME_ON_BATTERY, this.mBatteryRealTime / 1000);
                if (this.mAction == 204) {
                    StringBuilder sb = new StringBuilder();
                    PowerCheckerUtils.formatTimeMs(sb, this.mBatteryRealTime / 1000);
                    jSONObject.put(PowerCheckerProcessNotifier.KEY_SUMMARY, "Time on Battery:" + sb.toString() + ShellUtils.COMMAND_LINE_END + ((Object) this.mSummaryDesc));
                }
                JSONObject jSONObject2 = new JSONObject();
                boolean z2 = this.mKillResult;
                if (!z2) {
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_KILL_SUCCESS, z2);
                }
                if (this.mAction == 202) {
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_ACTIVE_TYPE, this.mActiveType);
                }
                if (this.mAction == 1) {
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_PROCESS_RESULT, this.mProcessResult);
                }
                if (this.mRecordType == 7) {
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_SUB_TYPE, this.mSubType);
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_CONSECUTIVE_COUNT, this.mConsecutiveCount);
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_BATTERY_LEVEL, this.mBatteryLevel);
                }
                if (this.mRecordType == 0) {
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_KERNEL_TYPE, this.mKernelType);
                }
                long j2 = this.mKernelWakelockTime;
                if (j2 > 0 || this.mKernelWakelockCount > 0) {
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_KERNEL_WAKELOCK_TIME, j2);
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_KERNEL_WAKELOCK_COUNT, this.mKernelWakelockCount);
                }
                long j3 = this.mKernelWakeupTime;
                if (j3 > 0 || this.mKernelWakeupCount > 0) {
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_KERNEL_WAKEUP_TIME, j3);
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_KERNEL_WAKEUP_COUNT, this.mKernelWakeupCount);
                }
                long j4 = this.mMobileActiveTime;
                if (j4 > 0) {
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_MOBILE_ACTIVE_TIME, j4);
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_MOBILE_ACTIVE_COUNT, this.mMobileActiveCount);
                }
                long j5 = this.mMobileRxBytes;
                if (j5 > 0 || this.mMobileTxBytes > 0 || this.mMobileRxPackets > 0 || this.mMobileTxPackets > 0) {
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_MOBILE_RX_BYTES, j5);
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_MOBILE_TX_BYTES, this.mMobileTxBytes);
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_MOBILE_RX_PACKETS, this.mMobileRxPackets);
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_MOBILE_TX_PACKETS, this.mMobileTxPackets);
                }
                long j6 = this.mWifiRxBytes;
                if (j6 > 0 || this.mWifiTxBytes > 0 || this.mWifiRxPackets > 0 || this.mWifiTxPackets > 0) {
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_WIFI_RX_BYTES, j6);
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_WIFI_TX_BYTES, this.mWifiTxBytes);
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_WIFI_RX_PACKETS, this.mWifiRxPackets);
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_WIFI_TX_PACKETS, this.mWifiTxPackets);
                }
                long j7 = this.mMobileBgRxBytes;
                if (j7 > 0 || this.mMobileBgTxBytes > 0 || this.mMobileBgRxPackets > 0 || this.mMobileBgTxPackets > 0) {
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_MOBILE_BG_RX_BYTES, j7);
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_MOBILE_BG_TX_BYTES, this.mMobileBgTxBytes);
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_MOBILE_BG_RX_PACKETS, this.mMobileBgRxPackets);
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_MOBILE_BG_TX_PACKETS, this.mMobileBgTxPackets);
                }
                long j8 = this.mWifiBgRxBytes;
                if (j8 > 0 || this.mWifiBgTxBytes > 0 || this.mWifiBgRxPackets > 0 || this.mWifiBgTxPackets > 0) {
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_WIFI_BG_RX_BYTES, j8);
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_WIFI_BG_TX_BYTES, this.mWifiBgTxBytes);
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_WIFI_BG_RX_PACKETS, this.mWifiBgRxPackets);
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_WIFI_BG_TX_PACKETS, this.mWifiBgTxPackets);
                }
                if (this.mWifiScanTime > 0) {
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_FULL_WIFI_LOCK_TIME, this.mFullWifiLockTime);
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_WIFI_SCAN_TIME, this.mWifiScanTime);
                }
                long j9 = this.mGpsSensorHoldTime;
                if (j9 > 0) {
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_GPS_TIME, j9);
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_GPS_COUNT, this.mGpsCount);
                }
                long j10 = this.mTotalUserCpuTime;
                if (j10 > 0 || this.mTotalSystemCpuTime > 0 || this.mTotalFgCpuTime > 0) {
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_USER_CPU_TIME, j10);
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_SYSTEM_CPU_TIME, this.mTotalSystemCpuTime);
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_FG_CPU_TIME, this.mTotalFgCpuTime);
                }
                int i2 = this.mWakeupTimes;
                if (i2 > 0) {
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_WAKEUP_ALARM, i2);
                }
                JSONArray jSONArray = new JSONArray();
                if (this.mKernelWakelocks.size() > 0) {
                    for (RecordTimer recordTimer : this.mKernelWakelocks) {
                        JSONObject jSONObject3 = new JSONObject();
                        jSONObject3.put(PowerCheckerProcessNotifier.KEY_KWL_NAME, recordTimer.name);
                        jSONObject3.put(PowerCheckerProcessNotifier.KEY_KWL_TIME, recordTimer.time);
                        jSONObject3.put(PowerCheckerProcessNotifier.KEY_KWL_COUNT, recordTimer.count);
                        jSONArray.put(jSONObject3);
                    }
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_kWL, jSONArray);
                }
                JSONArray jSONArray2 = new JSONArray();
                if (this.mPartialWakelocks.size() > 0) {
                    for (RecordTimer recordTimer2 : this.mPartialWakelocks) {
                        JSONObject jSONObject4 = new JSONObject();
                        jSONObject4.put(PowerCheckerProcessNotifier.KEY_PWL_NAME, recordTimer2.name);
                        jSONObject4.put(PowerCheckerProcessNotifier.KEY_PWL_TIME, recordTimer2.time);
                        jSONObject4.put(PowerCheckerProcessNotifier.KEY_PWL_COUNT, recordTimer2.count);
                        jSONArray2.put(jSONObject4);
                    }
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_PWL, jSONArray2);
                }
                JSONArray jSONArray3 = new JSONArray();
                if (this.mSyncStats.size() > 0) {
                    for (RecordTimer recordTimer3 : this.mSyncStats) {
                        JSONObject jSONObject5 = new JSONObject();
                        jSONObject5.put(PowerCheckerProcessNotifier.KEY_SYNC_NAME, recordTimer3.name);
                        jSONObject5.put(PowerCheckerProcessNotifier.KEY_SYNC_TIME, recordTimer3.time);
                        jSONObject5.put(PowerCheckerProcessNotifier.KEY_SYNC_COUNT, recordTimer3.count);
                        jSONArray3.put(jSONObject5);
                    }
                    jSONObject2.put(PowerCheckerProcessNotifier.KEY_SYNC, jSONArray3);
                }
                jSONObject.put("items", jSONObject2);
                return jSONObject;
            } catch (JSONException e2) {
                Log.e(PowerCheckerProcessNotifier.TAG, "toJson", e2);
                return null;
            }
        }

        public String toString() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            StringBuilder sb = new StringBuilder();
            sb.append("priority:" + this.mPriority + ShellUtils.COMMAND_LINE_END);
            sb.append("action:" + this.mAction + ShellUtils.COMMAND_LINE_END);
            sb.append("keyword: " + ((Object) this.mKeywords) + ShellUtils.COMMAND_LINE_END);
            sb.append("uid = " + this.mUid + ShellUtils.COMMAND_LINE_END);
            StringBuilder sb2 = new StringBuilder();
            sb2.append("package_name = ");
            String str = this.mPackageName;
            if (str == null) {
                str = this.mProcName;
            }
            sb2.append(str);
            sb2.append(ShellUtils.COMMAND_LINE_END);
            sb.append(sb2.toString());
            if (this.mPackageVersion != null) {
                sb.append("package_version = " + this.mPackageVersion + ShellUtils.COMMAND_LINE_END);
            }
            sb.append("record_time: " + simpleDateFormat.format(new Date(this.mRecordTime)) + ShellUtils.COMMAND_LINE_END);
            StringBuilder sb3 = new StringBuilder();
            PowerCheckerUtils.formatTimeMs(sb3, this.mBatteryRealTime / 1000);
            sb.append("time_on_battery: " + sb3.toString() + ShellUtils.COMMAND_LINE_END);
            if (!this.mKillResult) {
                sb.append("killResult = " + this.mKillResult + ShellUtils.COMMAND_LINE_END);
            }
            if (this.mAction == 202) {
                sb.append("  active_type = " + this.mActiveType + ShellUtils.COMMAND_LINE_END);
            }
            if (this.mAction == 1) {
                sb.append("  process_result = " + this.mProcessResult + ShellUtils.COMMAND_LINE_END);
            }
            if (this.mRecordType == 7) {
                sb.append("  sub_type = " + this.mSubType + ShellUtils.COMMAND_LINE_END);
                sb.append("  consecutive_count = " + this.mConsecutiveCount + ShellUtils.COMMAND_LINE_END);
                sb.append("  battery_level = " + this.mBatteryLevel + ShellUtils.COMMAND_LINE_END);
            }
            if (this.mRecordType == 0) {
                sb.append("  kernel_type = " + this.mKernelType + ShellUtils.COMMAND_LINE_END);
            }
            if (this.mKernelWakelockTime > 0 || this.mKernelWakelockCount > 0) {
                sb.append("  kernel_wakelock_time = " + this.mKernelWakelockTime + "ms");
                sb.append(" kernel_wakelock_count = " + this.mKernelWakelockCount + ShellUtils.COMMAND_LINE_END);
            }
            if (this.mKernelWakeupTime > 0 || this.mKernelWakeupCount > 0) {
                sb.append("  kernel_wakeup_time = " + this.mKernelWakeupTime + "ms");
                sb.append(" kernel_wakeup_count = " + this.mKernelWakeupCount + ShellUtils.COMMAND_LINE_END);
            }
            if (this.mMobileActiveTime > 0) {
                sb.append("  mobile_active_time = " + this.mMobileActiveTime + "ms");
                sb.append(" mobile_active_count = " + this.mMobileActiveCount + ShellUtils.COMMAND_LINE_END);
            }
            if (this.mMobileRxBytes > 0 || this.mMobileTxBytes > 0 || this.mMobileRxPackets > 0 || this.mMobileTxPackets > 0) {
                sb.append("  mobile_rx_bytes = " + this.mMobileRxBytes + " ");
                sb.append(" mobile_tx_bytes = " + this.mMobileTxBytes + ShellUtils.COMMAND_LINE_END);
                sb.append("  mobile_rx_packets = " + this.mMobileRxPackets + " ");
                sb.append(" mobile_tx_packets = " + this.mMobileTxPackets + ShellUtils.COMMAND_LINE_END);
            }
            if (this.mWifiRxBytes > 0 || this.mWifiTxBytes > 0 || this.mWifiRxPackets > 0 || this.mWifiTxPackets > 0) {
                sb.append("  wifi_rx_bytes = " + this.mWifiRxBytes + " ");
                sb.append(" wifi_tx_bytes = " + this.mWifiTxBytes + ShellUtils.COMMAND_LINE_END);
                sb.append("  wifi_rx_packets = " + this.mWifiRxPackets + " ");
                sb.append(" wifi_tx_packets = " + this.mWifiTxPackets + ShellUtils.COMMAND_LINE_END);
            }
            if (this.mMobileBgRxBytes > 0 || this.mMobileBgTxBytes > 0 || this.mMobileBgRxPackets > 0 || this.mMobileBgTxPackets > 0) {
                sb.append("  mobile_bg_rx_bytes = " + this.mMobileBgRxBytes + " ");
                sb.append(" mobile_bg_tx_bytes = " + this.mMobileBgTxBytes + ShellUtils.COMMAND_LINE_END);
                sb.append("  mobile_bg_rx_packets = " + this.mMobileBgRxPackets + " ");
                sb.append(" mobile_bg_tx_packets = " + this.mMobileBgTxPackets + ShellUtils.COMMAND_LINE_END);
            }
            if (this.mWifiBgRxBytes > 0 || this.mWifiBgTxBytes > 0 || this.mWifiBgRxPackets > 0 || this.mWifiBgTxPackets > 0) {
                sb.append("  wifi_bg_rx_bytes = " + this.mWifiBgRxBytes + " ");
                sb.append(" wifi_bg_tx_bytes = " + this.mWifiBgTxBytes + ShellUtils.COMMAND_LINE_END);
                sb.append("  wifi_bg_rx_packets = " + this.mWifiBgRxPackets + " ");
                sb.append(" wifi_bg_tx_packets = " + this.mWifiBgTxPackets + ShellUtils.COMMAND_LINE_END);
            }
            if (this.mWifiScanTime > 0) {
                sb.append("  full_wifi_lock_time = " + this.mFullWifiLockTime + "ms");
                sb.append(" wifi_scan_time = " + this.mWifiScanTime + "ms\n");
            }
            if (this.mGpsSensorHoldTime > 0) {
                sb.append("  gps_time = " + this.mGpsSensorHoldTime + "ms");
                sb.append(" gps_count = " + this.mGpsCount + ShellUtils.COMMAND_LINE_END);
            }
            if (this.mTotalUserCpuTime > 0 || this.mTotalSystemCpuTime > 0 || this.mTotalFgCpuTime > 0) {
                sb.append("  user_cpu_time = " + this.mTotalUserCpuTime + "ms");
                sb.append(" system_cpu_time = " + this.mTotalSystemCpuTime + "ms");
                sb.append(" fg_cpu_time = " + this.mTotalFgCpuTime + "ms\n");
            }
            if (this.mWakeupTimes > 0) {
                sb.append("  wakeup_alarm = " + this.mWakeupTimes + ShellUtils.COMMAND_LINE_END);
            }
            if (this.mKernelWakelocks.size() > 0) {
                sb.append("  kernel wake lock:\n");
                for (RecordTimer recordTimer : this.mKernelWakelocks) {
                    sb.append("    " + recordTimer.name);
                    sb.append(" time = " + recordTimer.time + "ms");
                    sb.append(" count = " + recordTimer.count + ShellUtils.COMMAND_LINE_END);
                }
            }
            if (this.mPartialWakelocks.size() > 0) {
                sb.append("  partial wake lock:\n");
                for (RecordTimer recordTimer2 : this.mPartialWakelocks) {
                    sb.append("    " + recordTimer2.name);
                    sb.append(" time = " + recordTimer2.time + "ms");
                    sb.append(" count = " + recordTimer2.count + ShellUtils.COMMAND_LINE_END);
                }
            }
            if (this.mSyncStats.size() > 0) {
                sb.append("  sync stats:\n");
                for (RecordTimer recordTimer3 : this.mSyncStats) {
                    sb.append("    " + recordTimer3.name);
                    sb.append(" time = " + recordTimer3.time + "ms");
                    sb.append(" count = " + recordTimer3.count + ShellUtils.COMMAND_LINE_END);
                }
            }
            sb.append("summary description:\n" + ((Object) this.mSummaryDesc));
            return sb.toString();
        }

        public void updateAction(int i2) {
            this.mProcessResult = i2;
        }
    }

    public static class RecordTimer extends PowerCheckerNotifier.Timer {
        String name;

        public RecordTimer(String str, long j2, int i2) {
            super(j2, i2);
            this.name = str;
        }

        @Override // com.miui.powerkeeper.powerchecker.PowerCheckerNotifier.Timer
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(this.name + " ");
            PowerCheckerUtils.formatTimeMs(sb, this.time);
            sb.append(" (" + this.count + " times) realtime");
            return sb.toString();
        }
    }

    public PowerCheckerProcessNotifier(Context context) {
        this.mContext = context;
        this.mPackageManager = context.getPackageManager();
    }

    private void clearPowerExceedRecords() {
        this.mPlatformWakelockRecords.clear();
        this.mUidPartialWaklockRecords.clear();
        this.mUidWifiScanRecords.clear();
        this.mUidBgGPSTimeRecords.clear();
        this.mPkgWakeupTimesRecords.clear();
        this.mProcBgCPUTimeRecords.clear();
        this.mUidMobileRadioRecords.clear();
        this.mUnknownReasonRecord = null;
        this.mSubSystemRecord = null;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("-------------PowerCheckerProcessNotifier-------------");
        if (this.mPlatformWakelockRecords.size() > 0) {
            printWriter.println("kernel wakelock hold long exceeded the limit:");
            Iterator<Map.Entry<String, PowerExceedRecord>> it = this.mPlatformWakelockRecords.entrySet().iterator();
            while (it.hasNext()) {
                printWriter.println(it.next().getValue().toString());
            }
        }
        if (this.mUidPartialWaklockRecords.size() > 0) {
            printWriter.println("android wakelock hold long exceeded the limit:");
            Iterator<Map.Entry<Integer, PowerExceedRecord>> it2 = this.mUidPartialWaklockRecords.entrySet().iterator();
            while (it2.hasNext()) {
                printWriter.println(it2.next().getValue().toString());
            }
        }
        if (this.mUidWifiScanRecords.size() > 0) {
            printWriter.println("applicaiton scan wifi too many exceeded the limit:");
            Iterator<Map.Entry<Integer, PowerExceedRecord>> it3 = this.mUidWifiScanRecords.entrySet().iterator();
            while (it3.hasNext()) {
                printWriter.println(it3.next().getValue().toString());
            }
        }
        if (this.mUidBgGPSTimeRecords.size() > 0) {
            printWriter.println("application hold GPS too long exceed the limit:");
            Iterator<Map.Entry<Integer, PowerExceedRecord>> it4 = this.mUidBgGPSTimeRecords.entrySet().iterator();
            while (it4.hasNext()) {
                printWriter.println(it4.next().getValue().toString());
            }
        }
        if (this.mProcBgCPUTimeRecords.size() > 0) {
            printWriter.println("process use too many cpu resource exceeded the limit:");
            Iterator<Map.Entry<String, PowerExceedRecord>> it5 = this.mProcBgCPUTimeRecords.entrySet().iterator();
            while (it5.hasNext()) {
                printWriter.println(it5.next().getValue().toString());
            }
        }
        if (this.mPkgWakeupTimesRecords.size() > 0) {
            printWriter.println("application wakeup deivce too many times exceeded the limit:");
            Iterator<Map.Entry<String, PowerExceedRecord>> it6 = this.mPkgWakeupTimesRecords.entrySet().iterator();
            while (it6.hasNext()) {
                printWriter.println(it6.next().getValue().toString());
            }
        }
        if (this.mUidMobileRadioRecords.size() > 0) {
            printWriter.println("applicaiton use too many mobile radio exceeded the limit:");
            Iterator<Map.Entry<Integer, PowerExceedRecord>> it7 = this.mUidMobileRadioRecords.entrySet().iterator();
            while (it7.hasNext()) {
                printWriter.println(it7.next().getValue().toString());
            }
        }
        if (this.mUnknownReasonRecord != null) {
            printWriter.println("unknown reason excessive power:");
            printWriter.println(this.mUnknownReasonRecord.toString());
        }
        if (this.mSubSystemRecord != null) {
            printWriter.println("sub system excessive power:");
            printWriter.println(this.mSubSystemRecord.toString());
        }
        printWriter.println("-----------------------------------------------");
        printWriter.println();
    }

    /* JADX WARN: Code duplicated, block: B:62:0x02a3  */
    /* JADX WARN: Code duplicated, block: B:66:0x02eb  */
    /* JADX WARN: Instruction removed from duplicated block: B:62:0x02a3, please report this as an issue */
    void notifyPlatformWakelockExceedLimit(PowerCheckerPolicy.PolicyResult policyResult, PowerCheckerNotifier.PowerExceedInfo powerExceedInfo, boolean z2) {
        long j2;
        int i2;
        int i3;
        int i4;
        long j3;
        String str;
        int i5;
        PowerCheckerUtils.TimerEntry timerEntry;
        String str2;
        String str3;
        int i6 = policyResult.mActionRule;
        PowerCheckerNotifier.KWLPowerExceedInfo kWLPowerExceedInfo = (PowerCheckerNotifier.KWLPowerExceedInfo) powerExceedInfo;
        int i7 = kWLPowerExceedInfo.kType;
        int i8 = kWLPowerExceedInfo.priority;
        ArrayList<PowerCheckerUtils.TimerEntry> arrayList = kWLPowerExceedInfo.ktimers;
        PowerCheckerUtils.TimerEntry timerEntry2 = kWLPowerExceedInfo.ktimer;
        PowerCheckerNotifier.Timer timer = kWLPowerExceedInfo.lastKtimer;
        long j4 = kWLPowerExceedInfo.totalKTime;
        int i9 = kWLPowerExceedInfo.totalKCount;
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        if (timerEntry2 == null) {
            if (i7 == 1 || i7 == 2) {
                str3 = "kernel_wakelocks";
            } else {
                str3 = (i7 == 3 || i7 == 4) ? "kernel_wakeups" : null;
            }
            if (str3 != null) {
                sb.append("[" + str3 + "]:" + sRecordTypeStringMap[0]);
                if (i7 == 1 || i7 == 2) {
                    sb2.append("Total Kernel Wake lock ");
                } else if (i7 == 3 || i7 == 4) {
                    sb2.append("Total Kernel Wake up ");
                }
                PowerCheckerUtils.formatTimeMs(sb2, j4);
                sb2.append(" (" + i9 + " times) realtime\n");
                sb2.append("Stats since unplugged:\n");
                sb2.append("priority: " + i8 + ShellUtils.COMMAND_LINE_END);
                sb2.append("Time on Battery: ");
                PowerCheckerUtils.formatTimeMs(sb2, this.mBatteryStatsTime.whichBatteryRealtime / 1000);
                sb2.append(ShellUtils.COMMAND_LINE_END);
                int i10 = 0;
                while (i10 < arrayList.size() && i10 < 10) {
                    PowerCheckerUtils.TimerEntry timerEntry3 = arrayList.get(i10);
                    sb2.append("Kernel Wake lock ");
                    sb2.append(timerEntry3.mName + ":");
                    long j5 = j4;
                    long j6 = timerEntry3.mTime;
                    int i11 = timerEntry3.mCount;
                    PowerCheckerUtils.formatTimeMs(sb2, j6);
                    sb2.append(" (" + i11 + " times) realtime\n");
                    i10++;
                    j4 = j5;
                }
                long j7 = j4;
                if (this.mPlatformWakelockRecords.containsKey(str3)) {
                    this.mPlatformWakelockRecords.remove(str3);
                }
                PowerExceedRecord powerExceedRecord = new PowerExceedRecord(0, 0, str3, null, null, i8, i6, sb, sb2, this.mBatteryRealTimeInc);
                powerExceedRecord.mKernelType = i7;
                powerExceedRecord.mKernelWakelockTime = j7;
                powerExceedRecord.mKernelWakelockCount = i9;
                this.mPlatformWakelockRecords.put(str3, powerExceedRecord);
                uploadRecord(powerExceedRecord, !policyResult.mIgnoreOverLimit);
                return;
            }
            return;
        }
        String str4 = "Kernel Wake lock ";
        ArrayList<PowerCheckerUtils.TimerEntry> arrayList2 = arrayList;
        StringBuilder sb3 = sb;
        sb3.append("[" + timerEntry2.mName + "]:" + sRecordTypeStringMap[0]);
        sb2.append(str4);
        sb2.append(timerEntry2.mName + ":");
        if (i7 != 1 && i7 != 2) {
            if (i7 == 3 || i7 == 4) {
                j2 = timerEntry2.mTime - timer.time;
                i2 = timerEntry2.mCount;
                i3 = timer.count;
            } else {
                str4 = str4;
                sb3 = sb3;
                j3 = 0;
                i4 = 0;
            }
            PowerCheckerUtils.formatTimeMs(sb2, j3);
            sb2.append(" (" + i4 + " times) realtime\n");
            sb2.append("Stats since unplugged:\n");
            sb2.append("priority: " + i8 + ShellUtils.COMMAND_LINE_END);
            sb2.append("Time on Battery: ");
            PowerCheckerUtils.formatTimeMs(sb2, this.mBatteryStatsTime.whichBatteryRealtime / 1000);
            sb2.append(ShellUtils.COMMAND_LINE_END);
            str = str4;
            sb2.append(str);
            sb2.append(timerEntry2.mName + ":");
            long j8 = timerEntry2.mTime;
            int i12 = timerEntry2.mCount;
            PowerCheckerUtils.formatTimeMs(sb2, j8);
            sb2.append(" (" + i12 + " times) realtime\n");
            i5 = 0;
            while (i5 < arrayList2.size() && i5 < 5) {
                timerEntry = arrayList2.get(i5);
                str2 = timerEntry.mName;
                ArrayList<PowerCheckerUtils.TimerEntry> arrayList3 = arrayList2;
                if (str2 != null || !str2.equals(timerEntry2.mName)) {
                    sb2.append(str);
                    sb2.append(timerEntry.mName + ":");
                    long j9 = timerEntry2.mTime;
                    int i13 = timerEntry.mCount;
                    PowerCheckerUtils.formatTimeMs(sb2, j9);
                    sb2.append(" (" + i13 + " times) realtime\n");
                }
                i5++;
                j3 = j3;
                arrayList2 = arrayList3;
            }
            long j10 = j3;
            if (this.mPlatformWakelockRecords.containsKey(timerEntry2.mName)) {
                this.mPlatformWakelockRecords.remove(timerEntry2.mName);
            }
            PowerExceedRecord powerExceedRecord2 = new PowerExceedRecord(0, 0, timerEntry2.mName, null, null, i8, i6, sb3, sb2, this.mBatteryRealTimeInc);
            powerExceedRecord2.mKernelType = i7;
            powerExceedRecord2.mKernelWakelockTime = j10;
            powerExceedRecord2.mKernelWakelockCount = i4;
            this.mPlatformWakelockRecords.put(timerEntry2.mName, powerExceedRecord2);
            uploadRecord(powerExceedRecord2, !policyResult.mIgnoreOverLimit);
        }
        j2 = timerEntry2.mTime - timer.time;
        i2 = timerEntry2.mCount;
        i3 = timer.count;
        i4 = i2 - i3;
        j3 = j2;
        PowerCheckerUtils.formatTimeMs(sb2, j3);
        sb2.append(" (" + i4 + " times) realtime\n");
        sb2.append("Stats since unplugged:\n");
        sb2.append("priority: " + i8 + ShellUtils.COMMAND_LINE_END);
        sb2.append("Time on Battery: ");
        PowerCheckerUtils.formatTimeMs(sb2, this.mBatteryStatsTime.whichBatteryRealtime / 1000);
        sb2.append(ShellUtils.COMMAND_LINE_END);
        str = str4;
        sb2.append(str);
        sb2.append(timerEntry2.mName + ":");
        long j11 = timerEntry2.mTime;
        int i14 = timerEntry2.mCount;
        PowerCheckerUtils.formatTimeMs(sb2, j11);
        sb2.append(" (" + i14 + " times) realtime\n");
        i5 = 0;
        while (i5 < arrayList2.size()) {
            timerEntry = arrayList2.get(i5);
            str2 = timerEntry.mName;
            ArrayList<PowerCheckerUtils.TimerEntry> arrayList4 = arrayList2;
            if (str2 != null) {
                sb2.append(str);
                sb2.append(timerEntry.mName + ":");
                long j12 = timerEntry2.mTime;
                int i15 = timerEntry.mCount;
                PowerCheckerUtils.formatTimeMs(sb2, j12);
                sb2.append(" (" + i15 + " times) realtime\n");
            } else {
                sb2.append(str);
                sb2.append(timerEntry.mName + ":");
                long j13 = timerEntry2.mTime;
                int i16 = timerEntry.mCount;
                PowerCheckerUtils.formatTimeMs(sb2, j13);
                sb2.append(" (" + i16 + " times) realtime\n");
            }
            i5++;
            j3 = j3;
            arrayList2 = arrayList4;
        }
        long j14 = j3;
        if (this.mPlatformWakelockRecords.containsKey(timerEntry2.mName)) {
            this.mPlatformWakelockRecords.remove(timerEntry2.mName);
        }
        PowerExceedRecord powerExceedRecord3 = new PowerExceedRecord(0, 0, timerEntry2.mName, null, null, i8, i6, sb3, sb2, this.mBatteryRealTimeInc);
        powerExceedRecord3.mKernelType = i7;
        powerExceedRecord3.mKernelWakelockTime = j14;
        powerExceedRecord3.mKernelWakelockCount = i4;
        this.mPlatformWakelockRecords.put(timerEntry2.mName, powerExceedRecord3);
        uploadRecord(powerExceedRecord3, !policyResult.mIgnoreOverLimit);
    }

    void notifySubSystemExcessivePower(PowerCheckerPolicy.PolicyResult policyResult, PowerCheckerNotifier.PowerExceedInfo powerExceedInfo, boolean z2) {
        int i2;
        int i3;
        int i4;
        int i5 = policyResult.mActionRule;
        PowerCheckerNotifier.SubSystemPowerExceedInfo subSystemPowerExceedInfo = (PowerCheckerNotifier.SubSystemPowerExceedInfo) powerExceedInfo;
        int i6 = subSystemPowerExceedInfo.priority;
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        sb.append("[sub_system]:" + sRecordTypeStringMap[7]);
        sb2.append("last history item: " + subSystemPowerExceedInfo.lastItem + ShellUtils.COMMAND_LINE_END);
        sb2.append("cur history item: " + subSystemPowerExceedInfo.curItem + ShellUtils.COMMAND_LINE_END);
        sb2.append(subSystemPowerExceedInfo.debugInfo);
        PowerExceedRecord powerExceedRecord = this.mSubSystemRecord;
        if (powerExceedRecord != null) {
            i2 = powerExceedRecord.mAction;
            i4 = powerExceedRecord.mConsecutiveCount;
            i3 = powerExceedRecord.mBatteryLevel;
        } else {
            i2 = 0;
            i3 = -1;
            i4 = 0;
        }
        PowerCheckerNotifier.BatteryHistoryItem batteryHistoryItem = subSystemPowerExceedInfo.curItem;
        int i7 = batteryHistoryItem != null ? batteryHistoryItem.batteryLevel : -1;
        int i8 = (i3 < 0 || i7 < 0 || i3 != i7 + 1 || i5 != i2) ? 1 : i4 + 1;
        PowerExceedRecord powerExceedRecord2 = new PowerExceedRecord(0, 7, "sub_system", null, null, i6, i5, sb, sb2, this.mBatteryRealTimeInc);
        this.mSubSystemRecord = powerExceedRecord2;
        powerExceedRecord2.mSubType = subSystemPowerExceedInfo.subType;
        powerExceedRecord2.mConsecutiveCount = i8;
        powerExceedRecord2.mBatteryLevel = i7;
        uploadRecord(powerExceedRecord2, !policyResult.mIgnoreOverLimit);
    }

    void notifyUidBgGPSTimeExceedLimit(PowerCheckerPolicy.PolicyResult policyResult, PowerCheckerNotifier.PowerExceedInfo powerExceedInfo, boolean z2) {
        PowerExceedRecord powerExceedRecord;
        int i2 = policyResult.mActionRule;
        PowerCheckerNotifier.AppPowerExceedInfo appPowerExceedInfo = (PowerCheckerNotifier.AppPowerExceedInfo) powerExceedInfo;
        int i3 = appPowerExceedInfo.uid;
        int i4 = appPowerExceedInfo.priority;
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        if (i3 < 10000) {
            sb.append(i3 + ":" + sRecordTypeStringMap[3]);
        } else {
            sb.append(appPowerExceedInfo.packageName + ":" + sRecordTypeStringMap[3]);
        }
        sb2.append("Sensor GPS: ");
        long j2 = appPowerExceedInfo.info.gpsSensorHoldTime - appPowerExceedInfo.lastInfo.gpsSensorHoldTime;
        PowerCheckerUtils.formatTimeMs(sb2, j2);
        int i5 = appPowerExceedInfo.info.countGPS - appPowerExceedInfo.lastInfo.countGPS;
        sb2.append("realtime (" + i5 + " times)\n");
        sb2.append("Stats since unplugged:\n");
        sb2.append("Time on Battery: ");
        PowerCheckerUtils.formatTimeMs(sb2, this.mBatteryStatsTime.whichBatteryRealtime / 1000);
        sb2.append(ShellUtils.COMMAND_LINE_END);
        sb2.append("Sensor GPS: ");
        PowerCheckerUtils.formatTimeMs(sb2, appPowerExceedInfo.info.gpsSensorHoldTime);
        sb2.append("realtime (" + appPowerExceedInfo.info.countGPS + " times)\n");
        if (this.mUidBgGPSTimeRecords.containsKey(Integer.valueOf(i3))) {
            this.mUidBgGPSTimeRecords.remove(Integer.valueOf(i3));
        }
        PowerExceedRecord powerExceedRecord2 = new PowerExceedRecord(i3, 3, appPowerExceedInfo.packageName, appPowerExceedInfo.packageVersion, null, i4, i2, sb, sb2, this.mBatteryRealTimeInc);
        if (i2 == 2) {
            powerExceedRecord = powerExceedRecord2;
            powerExceedRecord.mKillResult = z2;
        } else {
            powerExceedRecord = powerExceedRecord2;
        }
        if (i2 == 202) {
            powerExceedRecord.mActiveType = policyResult.mActiveType;
        }
        powerExceedRecord.mGpsSensorHoldTime = j2;
        powerExceedRecord.mGpsCount = i5;
        this.mUidBgGPSTimeRecords.put(Integer.valueOf(i3), powerExceedRecord);
        uploadRecord(powerExceedRecord, !policyResult.mIgnoreOverLimit);
    }

    void notifyUidCPUTimeExceedLimit(PowerCheckerPolicy.PolicyResult policyResult, PowerCheckerNotifier.PowerExceedInfo powerExceedInfo, boolean z2) {
        PowerCheckerPolicy.PolicyResult policyResult2;
        int i2 = policyResult.mActionRule;
        PowerCheckerNotifier.AppPowerExceedInfo appPowerExceedInfo = (PowerCheckerNotifier.AppPowerExceedInfo) powerExceedInfo;
        int i3 = appPowerExceedInfo.uid;
        int i4 = appPowerExceedInfo.priority;
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        String str = appPowerExceedInfo.proc;
        PowerCheckerNotifier.CpuTime cpuTime = appPowerExceedInfo.cpuTime;
        PowerCheckerNotifier.CpuTime cpuTime2 = appPowerExceedInfo.lastCpuTime;
        PowerCheckerNotifier.CpuTime cpuTime3 = new PowerCheckerNotifier.CpuTime(cpuTime.userTime - cpuTime2.userTime, cpuTime.systemTime - cpuTime2.systemTime, cpuTime.foregroundTime - cpuTime2.foregroundTime);
        sb.append(str + ":" + sRecordTypeStringMap[4]);
        sb2.append("Proc " + str + ":\n");
        sb2.append("CPU: " + cpuTime3.toString() + ShellUtils.COMMAND_LINE_END);
        sb2.append("Foreground activities: ");
        long j2 = appPowerExceedInfo.info.totalFgTimeMs - appPowerExceedInfo.lastInfo.totalFgTimeMs;
        PowerCheckerUtils.formatTimeMs(sb2, j2);
        sb2.append(Constants.SEPARATOR_LEFT_PARENTESIS);
        PowerCheckerUtils.formatTimeMs(sb2, appPowerExceedInfo.info.totalFgTimeMs);
        sb2.append(" - ");
        PowerCheckerUtils.formatTimeMs(sb2, appPowerExceedInfo.lastInfo.totalFgTimeMs);
        sb2.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
        sb2.append(" (" + (appPowerExceedInfo.info.totalFgTimes - appPowerExceedInfo.lastInfo.totalFgTimes) + Constants.SEPARATOR_LEFT_PARENTESIS + appPowerExceedInfo.info.totalFgTimes + " - " + appPowerExceedInfo.lastInfo.totalFgTimes + ") times)\n");
        sb2.append("Total cpu time: u=");
        long j3 = appPowerExceedInfo.info.totalUserCpuTime - appPowerExceedInfo.lastInfo.totalUserCpuTime;
        PowerCheckerUtils.formatTimeMs(sb2, j3);
        sb2.append(" s=");
        long j4 = appPowerExceedInfo.info.totalSystemCpuTime - appPowerExceedInfo.lastInfo.totalSystemCpuTime;
        PowerCheckerUtils.formatTimeMs(sb2, j4);
        sb2.append(ShellUtils.COMMAND_LINE_END);
        sb2.append("Stats since unplugged:\n");
        sb2.append("Time on Battery: ");
        PowerCheckerUtils.formatTimeMs(sb2, this.mBatteryStatsTime.whichBatteryRealtime / 1000);
        sb2.append(ShellUtils.COMMAND_LINE_END);
        sb2.append("Proc " + str + ":\n");
        sb2.append("CPU: " + cpuTime.toString() + ShellUtils.COMMAND_LINE_END);
        sb2.append("Foreground activities: ");
        PowerCheckerUtils.formatTimeMs(sb2, appPowerExceedInfo.info.totalFgTimeMs);
        sb2.append(" (" + appPowerExceedInfo.info.totalFgTimes + " times)\n");
        sb2.append("Total cpu time: u=");
        PowerCheckerUtils.formatTimeMs(sb2, appPowerExceedInfo.info.totalUserCpuTime);
        sb2.append(" s=");
        PowerCheckerUtils.formatTimeMs(sb2, appPowerExceedInfo.info.totalSystemCpuTime);
        sb2.append(ShellUtils.COMMAND_LINE_END);
        sb2.append("Power checker battery stats since current:\n");
        sb2.append("Time on Battery: ");
        PowerCheckerUtils.formatTimeMs(sb2, this.mDummyRealTimeSinceCurrent / 1000);
        sb2.append(ShellUtils.COMMAND_LINE_END);
        if (appPowerExceedInfo.info.backgroundBytes > 0) {
            sb2.append("Background network: ");
            sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.backgroundBytes) + " \n");
        }
        sb2.append("Foreground activities: ");
        PowerCheckerUtils.formatTimeMs(sb2, appPowerExceedInfo.info.fgActivityTimeMs);
        sb2.append(" (" + appPowerExceedInfo.info.fgActivityTimes + " times)\n");
        if (appPowerExceedInfo.info.backgroundAudioOnTimeMs > 0) {
            sb2.append("Background audio: ");
            PowerCheckerUtils.formatTimeMs(sb2, appPowerExceedInfo.info.backgroundAudioOnTimeMs);
            sb2.append("realtime (" + appPowerExceedInfo.info.backgroundAudioOnTimes + " times)\n");
        }
        if (this.mProcBgCPUTimeRecords.containsKey(str)) {
            this.mProcBgCPUTimeRecords.remove(str);
        }
        String appVersion = appPowerExceedInfo.packageVersion;
        if (!UserHandle.isApp(i3)) {
            appVersion = PackageUtil.getAppVersion(this.mPackageManager, str);
        }
        PowerExceedRecord powerExceedRecord = new PowerExceedRecord(i3, 4, null, appVersion, str, i4, i2, sb, sb2, this.mBatteryRealTimeInc);
        if (i2 == 2) {
            powerExceedRecord.mKillResult = z2;
        }
        if (i2 == 202) {
            policyResult2 = policyResult;
            powerExceedRecord.mActiveType = policyResult2.mActiveType;
        } else {
            policyResult2 = policyResult;
        }
        powerExceedRecord.mTotalUserCpuTime = j3;
        powerExceedRecord.mTotalSystemCpuTime = j4;
        powerExceedRecord.mTotalFgCpuTime = j2;
        this.mProcBgCPUTimeRecords.put(str, powerExceedRecord);
        uploadRecord(powerExceedRecord, !policyResult2.mIgnoreOverLimit);
    }

    void notifyUidMobileRadioActiveExceedLimit(PowerCheckerPolicy.PolicyResult policyResult, PowerCheckerNotifier.PowerExceedInfo powerExceedInfo, boolean z2) {
        StringBuilder sb;
        long j2;
        PowerCheckerPolicy.PolicyResult policyResult2;
        int i2 = policyResult.mActionRule;
        PowerCheckerNotifier.AppPowerExceedInfo appPowerExceedInfo = (PowerCheckerNotifier.AppPowerExceedInfo) powerExceedInfo;
        int i3 = appPowerExceedInfo.uid;
        int i4 = appPowerExceedInfo.priority;
        StringBuilder sb2 = new StringBuilder();
        StringBuilder sb3 = new StringBuilder();
        if (i3 < 10000) {
            sb2.append(i3 + ":" + sRecordTypeStringMap[6]);
        } else {
            sb2.append(appPowerExceedInfo.packageName + ":" + sRecordTypeStringMap[6]);
        }
        PowerCheckerNotifier.BaseInfo baseInfo = appPowerExceedInfo.info;
        long j3 = baseInfo.mobileRxBytes;
        PowerCheckerNotifier.BaseInfo baseInfo2 = appPowerExceedInfo.lastInfo;
        long j4 = j3 - baseInfo2.mobileRxBytes;
        long j5 = baseInfo.mobileTxBytes - baseInfo2.mobileTxBytes;
        long j6 = baseInfo.mobileRxPackets - baseInfo2.mobileRxPackets;
        long j7 = baseInfo.mobileTxPackets - baseInfo2.mobileTxPackets;
        long j8 = baseInfo.mobileBgRxBytes - baseInfo2.mobileBgRxBytes;
        long j9 = baseInfo.mobileBgTxBytes - baseInfo2.mobileBgTxBytes;
        long j10 = baseInfo.mobileBgRxPackets - baseInfo2.mobileBgRxPackets;
        long j11 = baseInfo.mobileBgTxPackets - baseInfo2.mobileBgTxPackets;
        long j12 = baseInfo.wifiBgRxBytes - baseInfo2.wifiBgRxBytes;
        long j13 = baseInfo.wifiBgTxBytes - baseInfo2.wifiBgTxBytes;
        long j14 = baseInfo.wifiBgRxPackets - baseInfo2.wifiBgRxPackets;
        long j15 = baseInfo.wifiBgTxPackets - baseInfo2.wifiBgTxPackets;
        if (j4 > 0 || j5 > 0 || j6 > 0 || j7 > 0) {
            sb = sb3;
            sb.append("Mobile network: ");
            sb.append(PowerCheckerUtils.formatBytesLocked(j4) + " received, ");
            sb.append(PowerCheckerUtils.formatBytesLocked(j5) + " sent ");
            StringBuilder sb4 = new StringBuilder();
            sb4.append("(packets ");
            sb4.append(j6);
            sb4.append(" received, ");
            j2 = j7;
            sb4.append(j2);
            sb4.append(" sent)\n");
            sb.append(sb4.toString());
        } else {
            sb = sb3;
            j2 = j7;
        }
        sb.append("Mobile radio active: ");
        long j16 = appPowerExceedInfo.info.mobileActiveTime - appPowerExceedInfo.lastInfo.mobileActiveTime;
        PowerCheckerUtils.formatTimeMs(sb, j16);
        sb.append(Constants.SEPARATOR_LEFT_PARENTESIS + PowerCheckerUtils.formatRatioLocked(j16 * 1000, this.mBatteryStatsTime.whichBatteryRealtime) + ") ");
        int i5 = appPowerExceedInfo.info.mobileActiveCount - appPowerExceedInfo.lastInfo.mobileActiveCount;
        sb.append(i5 + "x");
        long j17 = j6 + j2;
        long j18 = j2;
        sb.append(" @ " + PowerCheckerUtils.makemAh(j16 / (j17 == 0 ? 1L : j17)) + " mspp\n");
        sb.append("Foreground activities: ");
        long j19 = appPowerExceedInfo.info.totalFgTimeMs - appPowerExceedInfo.lastInfo.totalFgTimeMs;
        PowerCheckerUtils.formatTimeMs(sb, j19);
        if (j19 > 0) {
            sb.append(Constants.SEPARATOR_LEFT_PARENTESIS);
            PowerCheckerUtils.formatTimeMs(sb, appPowerExceedInfo.info.totalFgTimeMs);
            sb.append(" - ");
            PowerCheckerUtils.formatTimeMs(sb, appPowerExceedInfo.lastInfo.totalFgTimeMs);
            sb.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
            sb.append(" (" + (appPowerExceedInfo.info.totalFgTimes - appPowerExceedInfo.lastInfo.totalFgTimes) + Constants.SEPARATOR_LEFT_PARENTESIS + appPowerExceedInfo.info.totalFgTimes + " - " + appPowerExceedInfo.lastInfo.totalFgTimes + ") times)");
        }
        sb.append(ShellUtils.COMMAND_LINE_END);
        sb.append("Stats since unplugged:\n");
        sb.append("Time on Battery: ");
        PowerCheckerUtils.formatTimeMs(sb, this.mBatteryStatsTime.whichBatteryRealtime / 1000);
        sb.append(ShellUtils.COMMAND_LINE_END);
        PowerCheckerNotifier.BaseInfo baseInfo3 = appPowerExceedInfo.info;
        if (baseInfo3.mobileRxBytes > 0 || baseInfo3.mobileTxBytes > 0 || baseInfo3.mobileRxPackets > 0 || baseInfo3.mobileTxPackets > 0) {
            sb.append("Mobile network: ");
            sb.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.mobileRxBytes) + " received, ");
            sb.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.mobileTxBytes) + " sent ");
            sb.append("(packets " + appPowerExceedInfo.info.mobileRxPackets + " received, " + appPowerExceedInfo.info.mobileTxPackets + " sent)\n");
        }
        sb.append("Mobile radio active: ");
        PowerCheckerUtils.formatTimeMs(sb, appPowerExceedInfo.info.mobileActiveTime);
        sb.append(Constants.SEPARATOR_LEFT_PARENTESIS + PowerCheckerUtils.formatRatioLocked(appPowerExceedInfo.info.mobileActiveTime * 1000, this.mBatteryStatsTime.whichBatteryRealtime) + ") ");
        StringBuilder sb5 = new StringBuilder();
        sb5.append(appPowerExceedInfo.info.mobileActiveCount);
        sb5.append("x");
        sb.append(sb5.toString());
        PowerCheckerNotifier.BaseInfo baseInfo4 = appPowerExceedInfo.info;
        long j20 = baseInfo4.mobileRxPackets + baseInfo4.mobileTxPackets;
        if (j20 == 0) {
            j20 = 1;
        }
        sb.append(" @ " + PowerCheckerUtils.makemAh(appPowerExceedInfo.info.mobileActiveTime / j20) + " mspp\n");
        sb.append("Power checker battery stats since current:\n");
        sb.append("Time on Battery: ");
        PowerCheckerUtils.formatTimeMs(sb, this.mDummyRealTimeSinceCurrent / 1000);
        sb.append(ShellUtils.COMMAND_LINE_END);
        if (appPowerExceedInfo.info.backgroundBytes > 0) {
            sb.append("Background network: ");
            sb.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.backgroundBytes) + " \n");
        }
        sb.append("Foreground activities: ");
        PowerCheckerUtils.formatTimeMs(sb, appPowerExceedInfo.info.fgActivityTimeMs);
        sb.append(" (" + appPowerExceedInfo.info.fgActivityTimes + " times)\n");
        if (this.mUidMobileRadioRecords.containsKey(Integer.valueOf(i3))) {
            this.mUidMobileRadioRecords.remove(Integer.valueOf(i3));
        }
        PowerExceedRecord powerExceedRecord = new PowerExceedRecord(i3, 6, appPowerExceedInfo.packageName, appPowerExceedInfo.packageVersion, null, i4, i2, sb2, sb, this.mBatteryRealTimeInc);
        if (i2 == 2) {
            powerExceedRecord.mKillResult = z2;
        }
        if (i2 == 202) {
            policyResult2 = policyResult;
            powerExceedRecord.mActiveType = policyResult2.mActiveType;
        } else {
            policyResult2 = policyResult;
        }
        powerExceedRecord.mMobileRxBytes = j4;
        powerExceedRecord.mMobileTxBytes = j5;
        powerExceedRecord.mMobileRxPackets = j6;
        powerExceedRecord.mMobileTxPackets = j18;
        powerExceedRecord.mMobileBgRxBytes = j8;
        powerExceedRecord.mMobileBgTxBytes = j9;
        powerExceedRecord.mMobileBgRxPackets = j10;
        powerExceedRecord.mMobileBgTxPackets = j11;
        powerExceedRecord.mWifiBgRxBytes = j12;
        powerExceedRecord.mWifiBgTxBytes = j13;
        powerExceedRecord.mWifiBgRxPackets = j14;
        powerExceedRecord.mWifiBgTxPackets = j15;
        powerExceedRecord.mMobileActiveTime = j16;
        powerExceedRecord.mMobileActiveCount = i5;
        this.mUidMobileRadioRecords.put(Integer.valueOf(i3), powerExceedRecord);
        uploadRecord(powerExceedRecord, !policyResult2.mIgnoreOverLimit);
    }

    /* JADX WARN: Code duplicated, block: B:104:0x03d7  */
    /* JADX WARN: Code duplicated, block: B:110:0x03e9  */
    /* JADX WARN: Code duplicated, block: B:115:0x0456  */
    /* JADX WARN: Code duplicated, block: B:121:0x0468  */
    /* JADX WARN: Code duplicated, block: B:124:0x04ca  */
    /* JADX WARN: Code duplicated, block: B:127:0x04d4 A[LOOP:2: B:125:0x04ce->B:127:0x04d4, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:130:0x05a1 A[Catch: Exception -> 0x05ef, TryCatch #0 {Exception -> 0x05ef, blocks: (B:128:0x0597, B:130:0x05a1, B:131:0x05a5, B:133:0x05ab, B:135:0x05d4, B:138:0x05f5), top: B:166:0x0597 }] */
    /* JADX WARN: Code duplicated, block: B:133:0x05ab A[Catch: Exception -> 0x05ef, TryCatch #0 {Exception -> 0x05ef, blocks: (B:128:0x0597, B:130:0x05a1, B:131:0x05a5, B:133:0x05ab, B:135:0x05d4, B:138:0x05f5), top: B:166:0x0597 }] */
    /* JADX WARN: Code duplicated, block: B:135:0x05d4 A[Catch: Exception -> 0x05ef, TryCatch #0 {Exception -> 0x05ef, blocks: (B:128:0x0597, B:130:0x05a1, B:131:0x05a5, B:133:0x05ab, B:135:0x05d4, B:138:0x05f5), top: B:166:0x0597 }] */
    /* JADX WARN: Code duplicated, block: B:138:0x05f5 A[Catch: Exception -> 0x05ef, TRY_LEAVE, TryCatch #0 {Exception -> 0x05ef, blocks: (B:128:0x0597, B:130:0x05a1, B:131:0x05a5, B:133:0x05ab, B:135:0x05d4, B:138:0x05f5), top: B:166:0x0597 }] */
    /* JADX WARN: Code duplicated, block: B:143:0x0622  */
    /* JADX WARN: Code duplicated, block: B:146:0x064b  */
    /* JADX WARN: Code duplicated, block: B:149:0x067c  */
    /* JADX WARN: Code duplicated, block: B:152:0x06bd  */
    /* JADX WARN: Code duplicated, block: B:155:0x06c5  */
    /* JADX WARN: Code duplicated, block: B:157:0x06ce  */
    /* JADX WARN: Code duplicated, block: B:160:0x06f5  */
    /* JADX WARN: Code duplicated, block: B:163:0x06ff  */
    /* JADX WARN: Code duplicated, block: B:186:0x031f A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:86:0x0358  */
    /* JADX WARN: Instruction removed from duplicated block: B:121:0x0468, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:127:0x04d4, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:135:0x05d4, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:143:0x0622, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:146:0x064b, please report this as an issue */
    void notifyUidPartialWakelockExceedLimit(PowerCheckerPolicy.PolicyResult policyResult, PowerCheckerNotifier.PowerExceedInfo powerExceedInfo, boolean z2) {
        ArrayList arrayList;
        String str;
        String str2;
        String str3;
        String str4;
        String str5;
        String str6;
        PowerCheckerNotifier.BaseInfo baseInfo;
        String str7;
        PowerCheckerNotifier.BaseInfo baseInfo2;
        String str8;
        String str9;
        PowerExceedRecord powerExceedRecord;
        PowerCheckerPolicy.PolicyResult policyResult2;
        List<UidStatsData.Timer> syncTimer;
        long j2;
        int i2;
        String str10;
        Iterator<UidStatsData.WakeLock> it;
        long j3;
        int i3;
        long j4;
        int i4;
        String str11;
        PowerCheckerNotifier.Timer timer;
        int i5 = policyResult.mActionRule;
        PowerCheckerNotifier.AppPowerExceedInfo appPowerExceedInfo = (PowerCheckerNotifier.AppPowerExceedInfo) powerExceedInfo;
        int i6 = appPowerExceedInfo.uid;
        int i7 = appPowerExceedInfo.priority;
        UidStatsData uidStatsData = appPowerExceedInfo.uidObj;
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        if (i6 < 10000) {
            sb.append(i6 + ":" + sRecordTypeStringMap[1]);
        } else {
            sb.append(appPowerExceedInfo.packageName + ":" + sRecordTypeStringMap[1]);
        }
        PowerCheckerNotifier.BaseInfo baseInfo3 = appPowerExceedInfo.info;
        long j5 = baseInfo3.totalFull;
        PowerCheckerNotifier.BaseInfo baseInfo4 = appPowerExceedInfo.lastInfo;
        String str12 = "extract sync stasts info";
        String str13 = TAG;
        long j6 = j5 - baseInfo4.totalFull;
        String str14 = " times)\n";
        String str15 = "realtime (";
        long j7 = baseInfo3.totalPartial - baseInfo4.totalPartial;
        String str16 = "(not used)\n";
        String str17 = ": ";
        long j8 = baseInfo3.totalWindow - baseInfo4.totalWindow;
        String str18 = ":";
        long j9 = baseInfo3.totalPartialSinceCharged - baseInfo4.totalPartialSinceCharged;
        sb2.append("Total Full Wake Lock: ");
        PowerCheckerUtils.formatTimeMs(sb2, j6);
        sb2.append(ShellUtils.COMMAND_LINE_END);
        sb2.append("Total Partial Wake Lock (unplugged): ");
        PowerCheckerUtils.formatTimeMs(sb2, j7);
        sb2.append(ShellUtils.COMMAND_LINE_END);
        sb2.append("Total Partial Wake Lock (charged): ");
        PowerCheckerUtils.formatTimeMs(sb2, j9);
        sb2.append(Constants.SEPARATOR_LEFT_PARENTESIS);
        PowerCheckerUtils.formatTimeMs(sb2, appPowerExceedInfo.info.totalPartialSinceCharged);
        sb2.append(" - ");
        PowerCheckerUtils.formatTimeMs(sb2, appPowerExceedInfo.lastInfo.totalPartialSinceCharged);
        sb2.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
        sb2.append(ShellUtils.COMMAND_LINE_END);
        sb2.append("Total Window Wake Lock: ");
        PowerCheckerUtils.formatTimeMs(sb2, j8);
        sb2.append(ShellUtils.COMMAND_LINE_END);
        PowerCheckerNotifier.BaseInfo baseInfo5 = appPowerExceedInfo.info;
        long j10 = baseInfo5.mobileRxBytes;
        PowerCheckerNotifier.BaseInfo baseInfo6 = appPowerExceedInfo.lastInfo;
        long j11 = j10 - baseInfo6.mobileRxBytes;
        long j12 = baseInfo5.mobileTxBytes - baseInfo6.mobileTxBytes;
        long j13 = baseInfo5.mobileRxPackets - baseInfo6.mobileRxPackets;
        long j14 = baseInfo5.mobileTxPackets - baseInfo6.mobileTxPackets;
        String str19 = " sent)\n";
        String str20 = "Sync ";
        if (j11 > 0 || j12 > 0 || j13 > 0 || j14 > 0) {
            sb2.append("Mobile network: ");
            sb2.append(PowerCheckerUtils.formatBytesLocked(j11) + " received, ");
            sb2.append(PowerCheckerUtils.formatBytesLocked(j12) + " sent ");
            sb2.append("(packets " + j13 + " received, " + j14 + " sent)\n");
        }
        PowerCheckerNotifier.BaseInfo baseInfo7 = appPowerExceedInfo.info;
        long j15 = baseInfo7.wifiRxBytes;
        PowerCheckerNotifier.BaseInfo baseInfo8 = appPowerExceedInfo.lastInfo;
        long j16 = j15 - baseInfo8.wifiRxBytes;
        long j17 = baseInfo7.wifiTxBytes - baseInfo8.wifiTxBytes;
        long j18 = baseInfo7.wifiRxPackets - baseInfo8.wifiRxPackets;
        long j19 = baseInfo7.wifiTxPackets - baseInfo8.wifiTxPackets;
        if (j16 > 0 || j17 > 0 || j18 > 0 || j19 > 0) {
            sb2.append("Wi-Fi network: ");
            sb2.append(PowerCheckerUtils.formatBytesLocked(j16) + " received, ");
            sb2.append(PowerCheckerUtils.formatBytesLocked(j17) + " sent ");
            sb2.append("(packets " + j18 + " received, " + j19 + " sent)\n");
        }
        ArrayList arrayListNewArrayList = Lists.newArrayList();
        List<UidStatsData.WakeLock> wakeLockList = uidStatsData.getWakeLockList();
        String str21 = " times) realtime\n";
        String str22 = " (";
        if (wakeLockList.size() > 0) {
            Iterator<UidStatsData.WakeLock> it2 = wakeLockList.iterator();
            while (it2.hasNext()) {
                UidStatsData.WakeLock next = it2.next();
                long j20 = j17;
                long j21 = next.wlPartialTime;
                Iterator<UidStatsData.WakeLock> it3 = it2;
                int i8 = next.wlPartialCount;
                long j22 = j18;
                ArrayMap<String, PowerCheckerNotifier.Timer> arrayMap = appPowerExceedInfo.lastPartialWakelocks;
                if (arrayMap == null || (timer = arrayMap.get(next.wlName)) == null) {
                    j4 = 0;
                    i4 = 0;
                } else {
                    j4 = j21 - timer.time;
                    i4 = i8 - timer.count;
                }
                if (j4 > 0) {
                    sb2.append("Wake lock ");
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append(next.wlName);
                    str11 = str18;
                    sb3.append(str11);
                    sb2.append(sb3.toString());
                    PowerCheckerUtils.formatTimeMs(sb2, j4);
                    sb2.append("partial (" + i4 + " times) realtime\n");
                    arrayListNewArrayList.add(new RecordTimer(next.wlName, j4, i4));
                } else {
                    str11 = str18;
                }
                str18 = str11;
                j17 = j20;
                it2 = it3;
                j18 = j22;
                str19 = str19;
            }
        }
        long j23 = j17;
        long j24 = j18;
        String str23 = str19;
        String str24 = str18;
        ArrayList arrayListNewArrayList2 = Lists.newArrayList();
        try {
            List<UidStatsData.Timer> syncTimer2 = uidStatsData.getSyncTimer();
            if (syncTimer2.size() > 0) {
                Iterator<UidStatsData.Timer> it4 = syncTimer2.iterator();
                while (it4.hasNext()) {
                    UidStatsData.Timer next2 = it4.next();
                    long j25 = next2.totalTime;
                    int i9 = next2.count;
                    arrayList = arrayListNewArrayList;
                    String str25 = str20;
                    try {
                        sb2.append(str25);
                        Iterator<UidStatsData.Timer> it5 = it4;
                        StringBuilder sb4 = new StringBuilder();
                        str6 = str25;
                        try {
                            sb4.append(next2.name);
                            String str26 = str17;
                            try {
                                sb4.append(str26);
                                sb2.append(sb4.toString());
                                ArrayMap<String, PowerCheckerNotifier.Timer> arrayMap2 = appPowerExceedInfo.lastSyncs;
                                if (arrayMap2 != null) {
                                    str17 = str26;
                                    try {
                                        PowerCheckerNotifier.Timer timer2 = arrayMap2.get(next2.name);
                                        if (timer2 != null) {
                                            str = str21;
                                            str2 = str22;
                                            try {
                                                j3 = j25 - timer2.time;
                                                i3 = i9 - timer2.count;
                                            } catch (Exception e2) {
                                                e = e2;
                                                str3 = str14;
                                                str4 = str15;
                                                str5 = str16;
                                                Log.e(str13, str12, e);
                                                sb2.append("Stats since unplugged:\n");
                                                sb2.append("Time on Battery: ");
                                                String str27 = str12;
                                                String str28 = str13;
                                                PowerCheckerUtils.formatTimeMs(sb2, this.mBatteryStatsTime.whichBatteryRealtime / 1000);
                                                sb2.append(ShellUtils.COMMAND_LINE_END);
                                                baseInfo = appPowerExceedInfo.info;
                                                if (baseInfo.mobileRxBytes <= 0) {
                                                    sb2.append("Mobile network: ");
                                                    sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.mobileRxBytes) + " received, ");
                                                    sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.mobileTxBytes) + " sent ");
                                                    StringBuilder sb5 = new StringBuilder();
                                                    sb5.append("(packets ");
                                                    sb5.append(appPowerExceedInfo.info.mobileRxPackets);
                                                    sb5.append(" received, ");
                                                    sb5.append(appPowerExceedInfo.info.mobileTxPackets);
                                                    str7 = str23;
                                                    sb5.append(str7);
                                                    sb2.append(sb5.toString());
                                                } else {
                                                    sb2.append("Mobile network: ");
                                                    sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.mobileRxBytes) + " received, ");
                                                    sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.mobileTxBytes) + " sent ");
                                                    StringBuilder sb6 = new StringBuilder();
                                                    sb6.append("(packets ");
                                                    sb6.append(appPowerExceedInfo.info.mobileRxPackets);
                                                    sb6.append(" received, ");
                                                    sb6.append(appPowerExceedInfo.info.mobileTxPackets);
                                                    str7 = str23;
                                                    sb6.append(str7);
                                                    sb2.append(sb6.toString());
                                                }
                                                baseInfo2 = appPowerExceedInfo.info;
                                                str8 = str3;
                                                str9 = str5;
                                                if (baseInfo2.wifiRxBytes <= 0) {
                                                    sb2.append("Wi-Fi network: ");
                                                    sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.wifiRxBytes) + " received, ");
                                                    sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.wifiTxBytes) + " sent ");
                                                    sb2.append("(packets " + appPowerExceedInfo.info.wifiRxPackets + " received, " + appPowerExceedInfo.info.wifiTxPackets + str7);
                                                } else {
                                                    sb2.append("Wi-Fi network: ");
                                                    sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.wifiRxBytes) + " received, ");
                                                    sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.wifiTxBytes) + " sent ");
                                                    sb2.append("(packets " + appPowerExceedInfo.info.wifiRxPackets + " received, " + appPowerExceedInfo.info.wifiTxPackets + str7);
                                                }
                                                if (wakeLockList.size() > 0) {
                                                    it = wakeLockList.iterator();
                                                    while (it.hasNext()) {
                                                        UidStatsData.WakeLock next3 = it.next();
                                                        sb2.append("Wake lock ");
                                                        sb2.append(next3.wlName + str24);
                                                        long j26 = next3.wlFullTime;
                                                        int i10 = next3.wlFullCount;
                                                        PowerCheckerUtils.formatTimeMs(sb2, j26);
                                                        StringBuilder sb7 = new StringBuilder();
                                                        sb7.append("full");
                                                        String str29 = str2;
                                                        sb7.append(str29);
                                                        sb7.append(i10);
                                                        sb7.append(str);
                                                        sb2.append(sb7.toString());
                                                        long j27 = next3.wlPartialTime;
                                                        int i11 = next3.wlPartialCount;
                                                        PowerCheckerUtils.formatTimeMs(sb2, j27);
                                                        sb2.append("partial(unplugged)" + str29 + i11 + str);
                                                        long j28 = next3.wlWindowTime;
                                                        int i12 = next3.wlWindowCount;
                                                        PowerCheckerUtils.formatTimeMs(sb2, j28);
                                                        StringBuilder sb8 = new StringBuilder();
                                                        Iterator<UidStatsData.WakeLock> it6 = it;
                                                        sb8.append("window");
                                                        sb8.append(str29);
                                                        sb8.append(i12);
                                                        sb8.append(str);
                                                        sb2.append(sb8.toString());
                                                        PowerCheckerUtils.formatTimeMs(sb2, j28);
                                                        sb2.append("Wake lock ");
                                                        sb2.append(next3.wlName + str24);
                                                        long j29 = next3.wlPartialTime;
                                                        int i13 = next3.wlPartialCount;
                                                        PowerCheckerUtils.formatTimeMs(sb2, j29);
                                                        sb2.append("partial(charged)" + str29 + i13 + str);
                                                        it = it6;
                                                    }
                                                }
                                                syncTimer = uidStatsData.getSyncTimer();
                                                if (syncTimer.size() > 0) {
                                                    for (UidStatsData.Timer timer3 : syncTimer) {
                                                        j2 = timer3.totalTime;
                                                        i2 = timer3.count;
                                                        String str30 = str6;
                                                        sb2.append(str30);
                                                        StringBuilder sb9 = new StringBuilder();
                                                        sb9.append(timer3.name);
                                                        String str31 = str17;
                                                        sb9.append(str31);
                                                        sb2.append(sb9.toString());
                                                        if (j2 != 0) {
                                                            PowerCheckerUtils.formatTimeMs(sb2, j2);
                                                            sb2.append(str4 + i2 + str8);
                                                            str10 = str9;
                                                        } else {
                                                            str10 = str9;
                                                            sb2.append(str10);
                                                        }
                                                        str17 = str31;
                                                        str9 = str10;
                                                        str6 = str30;
                                                    }
                                                }
                                                sb2.append("Power checker battery stats since current:\n");
                                                sb2.append("Time on Battery and Screen off: ");
                                                PowerCheckerUtils.formatTimeMs(sb2, this.mBatteryRealTimeSinceCurrent / 1000);
                                                sb2.append(ShellUtils.COMMAND_LINE_END);
                                                if (appPowerExceedInfo.info.screenOffBytes > 0) {
                                                    sb2.append("Screen off network: ");
                                                    sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.screenOffBytes) + " \n");
                                                }
                                                if (appPowerExceedInfo.info.screenOffAudioOnTimeMs > 0) {
                                                    sb2.append("Screen off audio: ");
                                                    PowerCheckerUtils.formatTimeMs(sb2, appPowerExceedInfo.info.screenOffAudioOnTimeMs);
                                                    sb2.append(str4 + appPowerExceedInfo.info.screenOffAudioOnTimes + str8);
                                                }
                                                if (this.mUidPartialWaklockRecords.containsKey(Integer.valueOf(i6))) {
                                                    this.mUidPartialWaklockRecords.remove(Integer.valueOf(i6));
                                                }
                                                powerExceedRecord = new PowerExceedRecord(i6, 1, appPowerExceedInfo.packageName, appPowerExceedInfo.packageVersion, null, i7, i5, sb, sb2, this.mBatteryRealTimeInc);
                                                if (i5 == 2) {
                                                    powerExceedRecord.mKillResult = z2;
                                                }
                                                if (i5 == 202) {
                                                    policyResult2 = policyResult;
                                                    powerExceedRecord.mActiveType = policyResult2.mActiveType;
                                                } else {
                                                    policyResult2 = policyResult;
                                                }
                                                powerExceedRecord.mMobileRxBytes = j11;
                                                powerExceedRecord.mMobileTxBytes = j12;
                                                powerExceedRecord.mMobileRxPackets = j13;
                                                powerExceedRecord.mMobileTxPackets = j14;
                                                powerExceedRecord.mWifiRxBytes = j16;
                                                powerExceedRecord.mWifiTxBytes = j23;
                                                powerExceedRecord.mWifiRxPackets = j24;
                                                powerExceedRecord.mWifiTxPackets = j19;
                                                if (arrayList.size() > 0) {
                                                    powerExceedRecord.mPartialWakelocks = arrayList;
                                                }
                                                if (arrayListNewArrayList2.size() > 0) {
                                                    powerExceedRecord.mSyncStats = arrayListNewArrayList2;
                                                }
                                                this.mUidPartialWaklockRecords.put(Integer.valueOf(i6), powerExceedRecord);
                                                uploadRecord(powerExceedRecord, !policyResult2.mIgnoreOverLimit);
                                            }
                                        }
                                        if (j3 > 0) {
                                            try {
                                                PowerCheckerUtils.formatTimeMs(sb2, j3);
                                                StringBuilder sb10 = new StringBuilder();
                                                str4 = str15;
                                                try {
                                                    sb10.append(str4);
                                                    sb10.append(i3);
                                                    str3 = str14;
                                                    try {
                                                        sb10.append(str3);
                                                        sb2.append(sb10.toString());
                                                        arrayListNewArrayList2.add(new RecordTimer(next2.name, j3, i3));
                                                        str5 = str16;
                                                    } catch (Exception e3) {
                                                        e = e3;
                                                        str5 = str16;
                                                        Log.e(str13, str12, e);
                                                        sb2.append("Stats since unplugged:\n");
                                                        sb2.append("Time on Battery: ");
                                                        String str210 = str12;
                                                        String str211 = str13;
                                                        PowerCheckerUtils.formatTimeMs(sb2, this.mBatteryStatsTime.whichBatteryRealtime / 1000);
                                                        sb2.append(ShellUtils.COMMAND_LINE_END);
                                                        baseInfo = appPowerExceedInfo.info;
                                                        if (baseInfo.mobileRxBytes <= 0) {
                                                            sb2.append("Mobile network: ");
                                                            sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.mobileRxBytes) + " received, ");
                                                            sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.mobileTxBytes) + " sent ");
                                                            StringBuilder sb11 = new StringBuilder();
                                                            sb11.append("(packets ");
                                                            sb11.append(appPowerExceedInfo.info.mobileRxPackets);
                                                            sb11.append(" received, ");
                                                            sb11.append(appPowerExceedInfo.info.mobileTxPackets);
                                                            str7 = str23;
                                                            sb11.append(str7);
                                                            sb2.append(sb11.toString());
                                                        } else {
                                                            sb2.append("Mobile network: ");
                                                            sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.mobileRxBytes) + " received, ");
                                                            sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.mobileTxBytes) + " sent ");
                                                            StringBuilder sb12 = new StringBuilder();
                                                            sb12.append("(packets ");
                                                            sb12.append(appPowerExceedInfo.info.mobileRxPackets);
                                                            sb12.append(" received, ");
                                                            sb12.append(appPowerExceedInfo.info.mobileTxPackets);
                                                            str7 = str23;
                                                            sb12.append(str7);
                                                            sb2.append(sb12.toString());
                                                        }
                                                        baseInfo2 = appPowerExceedInfo.info;
                                                        str8 = str3;
                                                        str9 = str5;
                                                        if (baseInfo2.wifiRxBytes <= 0) {
                                                            sb2.append("Wi-Fi network: ");
                                                            sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.wifiRxBytes) + " received, ");
                                                            sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.wifiTxBytes) + " sent ");
                                                            sb2.append("(packets " + appPowerExceedInfo.info.wifiRxPackets + " received, " + appPowerExceedInfo.info.wifiTxPackets + str7);
                                                        } else {
                                                            sb2.append("Wi-Fi network: ");
                                                            sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.wifiRxBytes) + " received, ");
                                                            sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.wifiTxBytes) + " sent ");
                                                            sb2.append("(packets " + appPowerExceedInfo.info.wifiRxPackets + " received, " + appPowerExceedInfo.info.wifiTxPackets + str7);
                                                        }
                                                        if (wakeLockList.size() > 0) {
                                                            it = wakeLockList.iterator();
                                                            while (it.hasNext()) {
                                                                UidStatsData.WakeLock next4 = it.next();
                                                                sb2.append("Wake lock ");
                                                                sb2.append(next4.wlName + str24);
                                                                long j210 = next4.wlFullTime;
                                                                int i14 = next4.wlFullCount;
                                                                PowerCheckerUtils.formatTimeMs(sb2, j210);
                                                                StringBuilder sb13 = new StringBuilder();
                                                                sb13.append("full");
                                                                String str212 = str2;
                                                                sb13.append(str212);
                                                                sb13.append(i14);
                                                                sb13.append(str);
                                                                sb2.append(sb13.toString());
                                                                long j211 = next4.wlPartialTime;
                                                                int i15 = next4.wlPartialCount;
                                                                PowerCheckerUtils.formatTimeMs(sb2, j211);
                                                                sb2.append("partial(unplugged)" + str212 + i15 + str);
                                                                long j212 = next4.wlWindowTime;
                                                                int i16 = next4.wlWindowCount;
                                                                PowerCheckerUtils.formatTimeMs(sb2, j212);
                                                                StringBuilder sb14 = new StringBuilder();
                                                                Iterator<UidStatsData.WakeLock> it7 = it;
                                                                sb14.append("window");
                                                                sb14.append(str212);
                                                                sb14.append(i16);
                                                                sb14.append(str);
                                                                sb2.append(sb14.toString());
                                                                PowerCheckerUtils.formatTimeMs(sb2, j212);
                                                                sb2.append("Wake lock ");
                                                                sb2.append(next4.wlName + str24);
                                                                long j213 = next4.wlPartialTime;
                                                                int i17 = next4.wlPartialCount;
                                                                PowerCheckerUtils.formatTimeMs(sb2, j213);
                                                                sb2.append("partial(charged)" + str212 + i17 + str);
                                                                it = it7;
                                                            }
                                                        }
                                                        syncTimer = uidStatsData.getSyncTimer();
                                                        if (syncTimer.size() > 0) {
                                                            while (r0.hasNext()) {
                                                                j2 = timer3.totalTime;
                                                                i2 = timer3.count;
                                                                String str32 = str6;
                                                                sb2.append(str32);
                                                                StringBuilder sb15 = new StringBuilder();
                                                                sb15.append(timer3.name);
                                                                String str33 = str17;
                                                                sb15.append(str33);
                                                                sb2.append(sb15.toString());
                                                                if (j2 != 0) {
                                                                    PowerCheckerUtils.formatTimeMs(sb2, j2);
                                                                    sb2.append(str4 + i2 + str8);
                                                                    str10 = str9;
                                                                } else {
                                                                    str10 = str9;
                                                                    sb2.append(str10);
                                                                }
                                                                str17 = str33;
                                                                str9 = str10;
                                                                str6 = str32;
                                                            }
                                                        }
                                                        sb2.append("Power checker battery stats since current:\n");
                                                        sb2.append("Time on Battery and Screen off: ");
                                                        PowerCheckerUtils.formatTimeMs(sb2, this.mBatteryRealTimeSinceCurrent / 1000);
                                                        sb2.append(ShellUtils.COMMAND_LINE_END);
                                                        if (appPowerExceedInfo.info.screenOffBytes > 0) {
                                                            sb2.append("Screen off network: ");
                                                            sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.screenOffBytes) + " \n");
                                                        }
                                                        if (appPowerExceedInfo.info.screenOffAudioOnTimeMs > 0) {
                                                            sb2.append("Screen off audio: ");
                                                            PowerCheckerUtils.formatTimeMs(sb2, appPowerExceedInfo.info.screenOffAudioOnTimeMs);
                                                            sb2.append(str4 + appPowerExceedInfo.info.screenOffAudioOnTimes + str8);
                                                        }
                                                        if (this.mUidPartialWaklockRecords.containsKey(Integer.valueOf(i6))) {
                                                            this.mUidPartialWaklockRecords.remove(Integer.valueOf(i6));
                                                        }
                                                        powerExceedRecord = new PowerExceedRecord(i6, 1, appPowerExceedInfo.packageName, appPowerExceedInfo.packageVersion, null, i7, i5, sb, sb2, this.mBatteryRealTimeInc);
                                                        if (i5 == 2) {
                                                            powerExceedRecord.mKillResult = z2;
                                                        }
                                                        if (i5 == 202) {
                                                            policyResult2 = policyResult;
                                                            powerExceedRecord.mActiveType = policyResult2.mActiveType;
                                                        } else {
                                                            policyResult2 = policyResult;
                                                        }
                                                        powerExceedRecord.mMobileRxBytes = j11;
                                                        powerExceedRecord.mMobileTxBytes = j12;
                                                        powerExceedRecord.mMobileRxPackets = j13;
                                                        powerExceedRecord.mMobileTxPackets = j14;
                                                        powerExceedRecord.mWifiRxBytes = j16;
                                                        powerExceedRecord.mWifiTxBytes = j23;
                                                        powerExceedRecord.mWifiRxPackets = j24;
                                                        powerExceedRecord.mWifiTxPackets = j19;
                                                        if (arrayList.size() > 0) {
                                                            powerExceedRecord.mPartialWakelocks = arrayList;
                                                        }
                                                        if (arrayListNewArrayList2.size() > 0) {
                                                            powerExceedRecord.mSyncStats = arrayListNewArrayList2;
                                                        }
                                                        this.mUidPartialWaklockRecords.put(Integer.valueOf(i6), powerExceedRecord);
                                                        uploadRecord(powerExceedRecord, !policyResult2.mIgnoreOverLimit);
                                                    }
                                                } catch (Exception e4) {
                                                    e = e4;
                                                    str3 = str14;
                                                }
                                            } catch (Exception e5) {
                                                e = e5;
                                                str3 = str14;
                                                str4 = str15;
                                            }
                                        } else {
                                            str3 = str14;
                                            str4 = str15;
                                            str5 = str16;
                                            try {
                                                sb2.append(str5);
                                            } catch (Exception e6) {
                                                e = e6;
                                                str12 = str12;
                                                str13 = str13;
                                                Log.e(str13, str12, e);
                                                sb2.append("Stats since unplugged:\n");
                                                sb2.append("Time on Battery: ");
                                                String str213 = str12;
                                                String str214 = str13;
                                                PowerCheckerUtils.formatTimeMs(sb2, this.mBatteryStatsTime.whichBatteryRealtime / 1000);
                                                sb2.append(ShellUtils.COMMAND_LINE_END);
                                                baseInfo = appPowerExceedInfo.info;
                                                if (baseInfo.mobileRxBytes <= 0) {
                                                    sb2.append("Mobile network: ");
                                                    sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.mobileRxBytes) + " received, ");
                                                    sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.mobileTxBytes) + " sent ");
                                                    StringBuilder sb16 = new StringBuilder();
                                                    sb16.append("(packets ");
                                                    sb16.append(appPowerExceedInfo.info.mobileRxPackets);
                                                    sb16.append(" received, ");
                                                    sb16.append(appPowerExceedInfo.info.mobileTxPackets);
                                                    str7 = str23;
                                                    sb16.append(str7);
                                                    sb2.append(sb16.toString());
                                                } else {
                                                    sb2.append("Mobile network: ");
                                                    sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.mobileRxBytes) + " received, ");
                                                    sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.mobileTxBytes) + " sent ");
                                                    StringBuilder sb17 = new StringBuilder();
                                                    sb17.append("(packets ");
                                                    sb17.append(appPowerExceedInfo.info.mobileRxPackets);
                                                    sb17.append(" received, ");
                                                    sb17.append(appPowerExceedInfo.info.mobileTxPackets);
                                                    str7 = str23;
                                                    sb17.append(str7);
                                                    sb2.append(sb17.toString());
                                                }
                                                baseInfo2 = appPowerExceedInfo.info;
                                                str8 = str3;
                                                str9 = str5;
                                                if (baseInfo2.wifiRxBytes <= 0) {
                                                    sb2.append("Wi-Fi network: ");
                                                    sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.wifiRxBytes) + " received, ");
                                                    sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.wifiTxBytes) + " sent ");
                                                    sb2.append("(packets " + appPowerExceedInfo.info.wifiRxPackets + " received, " + appPowerExceedInfo.info.wifiTxPackets + str7);
                                                } else {
                                                    sb2.append("Wi-Fi network: ");
                                                    sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.wifiRxBytes) + " received, ");
                                                    sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.wifiTxBytes) + " sent ");
                                                    sb2.append("(packets " + appPowerExceedInfo.info.wifiRxPackets + " received, " + appPowerExceedInfo.info.wifiTxPackets + str7);
                                                }
                                                if (wakeLockList.size() > 0) {
                                                    it = wakeLockList.iterator();
                                                    while (it.hasNext()) {
                                                        UidStatsData.WakeLock next5 = it.next();
                                                        sb2.append("Wake lock ");
                                                        sb2.append(next5.wlName + str24);
                                                        long j214 = next5.wlFullTime;
                                                        int i18 = next5.wlFullCount;
                                                        PowerCheckerUtils.formatTimeMs(sb2, j214);
                                                        StringBuilder sb18 = new StringBuilder();
                                                        sb18.append("full");
                                                        String str215 = str2;
                                                        sb18.append(str215);
                                                        sb18.append(i18);
                                                        sb18.append(str);
                                                        sb2.append(sb18.toString());
                                                        long j215 = next5.wlPartialTime;
                                                        int i19 = next5.wlPartialCount;
                                                        PowerCheckerUtils.formatTimeMs(sb2, j215);
                                                        sb2.append("partial(unplugged)" + str215 + i19 + str);
                                                        long j216 = next5.wlWindowTime;
                                                        int i110 = next5.wlWindowCount;
                                                        PowerCheckerUtils.formatTimeMs(sb2, j216);
                                                        StringBuilder sb19 = new StringBuilder();
                                                        Iterator<UidStatsData.WakeLock> it8 = it;
                                                        sb19.append("window");
                                                        sb19.append(str215);
                                                        sb19.append(i110);
                                                        sb19.append(str);
                                                        sb2.append(sb19.toString());
                                                        PowerCheckerUtils.formatTimeMs(sb2, j216);
                                                        sb2.append("Wake lock ");
                                                        sb2.append(next5.wlName + str24);
                                                        long j217 = next5.wlPartialTime;
                                                        int i111 = next5.wlPartialCount;
                                                        PowerCheckerUtils.formatTimeMs(sb2, j217);
                                                        sb2.append("partial(charged)" + str215 + i111 + str);
                                                        it = it8;
                                                    }
                                                }
                                                syncTimer = uidStatsData.getSyncTimer();
                                                if (syncTimer.size() > 0) {
                                                    while (r0.hasNext()) {
                                                        j2 = timer3.totalTime;
                                                        i2 = timer3.count;
                                                        String str34 = str6;
                                                        sb2.append(str34);
                                                        StringBuilder sb110 = new StringBuilder();
                                                        sb110.append(timer3.name);
                                                        String str35 = str17;
                                                        sb110.append(str35);
                                                        sb2.append(sb110.toString());
                                                        if (j2 != 0) {
                                                            PowerCheckerUtils.formatTimeMs(sb2, j2);
                                                            sb2.append(str4 + i2 + str8);
                                                            str10 = str9;
                                                        } else {
                                                            str10 = str9;
                                                            sb2.append(str10);
                                                        }
                                                        str17 = str35;
                                                        str9 = str10;
                                                        str6 = str34;
                                                    }
                                                }
                                                sb2.append("Power checker battery stats since current:\n");
                                                sb2.append("Time on Battery and Screen off: ");
                                                PowerCheckerUtils.formatTimeMs(sb2, this.mBatteryRealTimeSinceCurrent / 1000);
                                                sb2.append(ShellUtils.COMMAND_LINE_END);
                                                if (appPowerExceedInfo.info.screenOffBytes > 0) {
                                                    sb2.append("Screen off network: ");
                                                    sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.screenOffBytes) + " \n");
                                                }
                                                if (appPowerExceedInfo.info.screenOffAudioOnTimeMs > 0) {
                                                    sb2.append("Screen off audio: ");
                                                    PowerCheckerUtils.formatTimeMs(sb2, appPowerExceedInfo.info.screenOffAudioOnTimeMs);
                                                    sb2.append(str4 + appPowerExceedInfo.info.screenOffAudioOnTimes + str8);
                                                }
                                                if (this.mUidPartialWaklockRecords.containsKey(Integer.valueOf(i6))) {
                                                    this.mUidPartialWaklockRecords.remove(Integer.valueOf(i6));
                                                }
                                                powerExceedRecord = new PowerExceedRecord(i6, 1, appPowerExceedInfo.packageName, appPowerExceedInfo.packageVersion, null, i7, i5, sb, sb2, this.mBatteryRealTimeInc);
                                                if (i5 == 2) {
                                                    powerExceedRecord.mKillResult = z2;
                                                }
                                                if (i5 == 202) {
                                                    policyResult2 = policyResult;
                                                    powerExceedRecord.mActiveType = policyResult2.mActiveType;
                                                } else {
                                                    policyResult2 = policyResult;
                                                }
                                                powerExceedRecord.mMobileRxBytes = j11;
                                                powerExceedRecord.mMobileTxBytes = j12;
                                                powerExceedRecord.mMobileRxPackets = j13;
                                                powerExceedRecord.mMobileTxPackets = j14;
                                                powerExceedRecord.mWifiRxBytes = j16;
                                                powerExceedRecord.mWifiTxBytes = j23;
                                                powerExceedRecord.mWifiRxPackets = j24;
                                                powerExceedRecord.mWifiTxPackets = j19;
                                                if (arrayList.size() > 0) {
                                                    powerExceedRecord.mPartialWakelocks = arrayList;
                                                }
                                                if (arrayListNewArrayList2.size() > 0) {
                                                    powerExceedRecord.mSyncStats = arrayListNewArrayList2;
                                                }
                                                this.mUidPartialWaklockRecords.put(Integer.valueOf(i6), powerExceedRecord);
                                                uploadRecord(powerExceedRecord, !policyResult2.mIgnoreOverLimit);
                                            }
                                        }
                                        str15 = str4;
                                        str14 = str3;
                                        str16 = str5;
                                        it4 = it5;
                                        str20 = str6;
                                        str22 = str2;
                                        str21 = str;
                                        arrayListNewArrayList = arrayList;
                                    } catch (Exception e7) {
                                        e = e7;
                                        str = str21;
                                        str2 = str22;
                                    }
                                } else {
                                    str17 = str26;
                                }
                                str = str21;
                                str2 = str22;
                                j3 = 0;
                                i3 = 0;
                                if (j3 > 0) {
                                    PowerCheckerUtils.formatTimeMs(sb2, j3);
                                    StringBuilder sb111 = new StringBuilder();
                                    str4 = str15;
                                    sb111.append(str4);
                                    sb111.append(i3);
                                    str3 = str14;
                                    sb111.append(str3);
                                    sb2.append(sb111.toString());
                                    arrayListNewArrayList2.add(new RecordTimer(next2.name, j3, i3));
                                    str5 = str16;
                                } else {
                                    str3 = str14;
                                    str4 = str15;
                                    str5 = str16;
                                    sb2.append(str5);
                                }
                                str15 = str4;
                                str14 = str3;
                                str16 = str5;
                                it4 = it5;
                                str20 = str6;
                                str22 = str2;
                                str21 = str;
                                arrayListNewArrayList = arrayList;
                            } catch (Exception e8) {
                                e = e8;
                                str17 = str26;
                                str = str21;
                                str2 = str22;
                                str3 = str14;
                                str4 = str15;
                                str5 = str16;
                                str12 = str12;
                                str13 = str13;
                                Log.e(str13, str12, e);
                                sb2.append("Stats since unplugged:\n");
                                sb2.append("Time on Battery: ");
                                String str216 = str12;
                                String str217 = str13;
                                PowerCheckerUtils.formatTimeMs(sb2, this.mBatteryStatsTime.whichBatteryRealtime / 1000);
                                sb2.append(ShellUtils.COMMAND_LINE_END);
                                baseInfo = appPowerExceedInfo.info;
                                if (baseInfo.mobileRxBytes <= 0) {
                                    sb2.append("Mobile network: ");
                                    sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.mobileRxBytes) + " received, ");
                                    sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.mobileTxBytes) + " sent ");
                                    StringBuilder sb112 = new StringBuilder();
                                    sb112.append("(packets ");
                                    sb112.append(appPowerExceedInfo.info.mobileRxPackets);
                                    sb112.append(" received, ");
                                    sb112.append(appPowerExceedInfo.info.mobileTxPackets);
                                    str7 = str23;
                                    sb112.append(str7);
                                    sb2.append(sb112.toString());
                                } else {
                                    sb2.append("Mobile network: ");
                                    sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.mobileRxBytes) + " received, ");
                                    sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.mobileTxBytes) + " sent ");
                                    StringBuilder sb113 = new StringBuilder();
                                    sb113.append("(packets ");
                                    sb113.append(appPowerExceedInfo.info.mobileRxPackets);
                                    sb113.append(" received, ");
                                    sb113.append(appPowerExceedInfo.info.mobileTxPackets);
                                    str7 = str23;
                                    sb113.append(str7);
                                    sb2.append(sb113.toString());
                                }
                                baseInfo2 = appPowerExceedInfo.info;
                                str8 = str3;
                                str9 = str5;
                                if (baseInfo2.wifiRxBytes <= 0) {
                                    sb2.append("Wi-Fi network: ");
                                    sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.wifiRxBytes) + " received, ");
                                    sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.wifiTxBytes) + " sent ");
                                    sb2.append("(packets " + appPowerExceedInfo.info.wifiRxPackets + " received, " + appPowerExceedInfo.info.wifiTxPackets + str7);
                                } else {
                                    sb2.append("Wi-Fi network: ");
                                    sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.wifiRxBytes) + " received, ");
                                    sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.wifiTxBytes) + " sent ");
                                    sb2.append("(packets " + appPowerExceedInfo.info.wifiRxPackets + " received, " + appPowerExceedInfo.info.wifiTxPackets + str7);
                                }
                                if (wakeLockList.size() > 0) {
                                    it = wakeLockList.iterator();
                                    while (it.hasNext()) {
                                        UidStatsData.WakeLock next6 = it.next();
                                        sb2.append("Wake lock ");
                                        sb2.append(next6.wlName + str24);
                                        long j218 = next6.wlFullTime;
                                        int i112 = next6.wlFullCount;
                                        PowerCheckerUtils.formatTimeMs(sb2, j218);
                                        StringBuilder sb114 = new StringBuilder();
                                        sb114.append("full");
                                        String str218 = str2;
                                        sb114.append(str218);
                                        sb114.append(i112);
                                        sb114.append(str);
                                        sb2.append(sb114.toString());
                                        long j219 = next6.wlPartialTime;
                                        int i113 = next6.wlPartialCount;
                                        PowerCheckerUtils.formatTimeMs(sb2, j219);
                                        sb2.append("partial(unplugged)" + str218 + i113 + str);
                                        long j2110 = next6.wlWindowTime;
                                        int i114 = next6.wlWindowCount;
                                        PowerCheckerUtils.formatTimeMs(sb2, j2110);
                                        StringBuilder sb115 = new StringBuilder();
                                        Iterator<UidStatsData.WakeLock> it9 = it;
                                        sb115.append("window");
                                        sb115.append(str218);
                                        sb115.append(i114);
                                        sb115.append(str);
                                        sb2.append(sb115.toString());
                                        PowerCheckerUtils.formatTimeMs(sb2, j2110);
                                        sb2.append("Wake lock ");
                                        sb2.append(next6.wlName + str24);
                                        long j2111 = next6.wlPartialTime;
                                        int i115 = next6.wlPartialCount;
                                        PowerCheckerUtils.formatTimeMs(sb2, j2111);
                                        sb2.append("partial(charged)" + str218 + i115 + str);
                                        it = it9;
                                    }
                                }
                                syncTimer = uidStatsData.getSyncTimer();
                                if (syncTimer.size() > 0) {
                                    while (r0.hasNext()) {
                                        j2 = timer3.totalTime;
                                        i2 = timer3.count;
                                        String str36 = str6;
                                        sb2.append(str36);
                                        StringBuilder sb116 = new StringBuilder();
                                        sb116.append(timer3.name);
                                        String str37 = str17;
                                        sb116.append(str37);
                                        sb2.append(sb116.toString());
                                        if (j2 != 0) {
                                            PowerCheckerUtils.formatTimeMs(sb2, j2);
                                            sb2.append(str4 + i2 + str8);
                                            str10 = str9;
                                        } else {
                                            str10 = str9;
                                            sb2.append(str10);
                                        }
                                        str17 = str37;
                                        str9 = str10;
                                        str6 = str36;
                                    }
                                }
                                sb2.append("Power checker battery stats since current:\n");
                                sb2.append("Time on Battery and Screen off: ");
                                PowerCheckerUtils.formatTimeMs(sb2, this.mBatteryRealTimeSinceCurrent / 1000);
                                sb2.append(ShellUtils.COMMAND_LINE_END);
                                if (appPowerExceedInfo.info.screenOffBytes > 0) {
                                    sb2.append("Screen off network: ");
                                    sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.screenOffBytes) + " \n");
                                }
                                if (appPowerExceedInfo.info.screenOffAudioOnTimeMs > 0) {
                                    sb2.append("Screen off audio: ");
                                    PowerCheckerUtils.formatTimeMs(sb2, appPowerExceedInfo.info.screenOffAudioOnTimeMs);
                                    sb2.append(str4 + appPowerExceedInfo.info.screenOffAudioOnTimes + str8);
                                }
                                if (this.mUidPartialWaklockRecords.containsKey(Integer.valueOf(i6))) {
                                    this.mUidPartialWaklockRecords.remove(Integer.valueOf(i6));
                                }
                                powerExceedRecord = new PowerExceedRecord(i6, 1, appPowerExceedInfo.packageName, appPowerExceedInfo.packageVersion, null, i7, i5, sb, sb2, this.mBatteryRealTimeInc);
                                if (i5 == 2) {
                                    powerExceedRecord.mKillResult = z2;
                                }
                                if (i5 == 202) {
                                    policyResult2 = policyResult;
                                    powerExceedRecord.mActiveType = policyResult2.mActiveType;
                                } else {
                                    policyResult2 = policyResult;
                                }
                                powerExceedRecord.mMobileRxBytes = j11;
                                powerExceedRecord.mMobileTxBytes = j12;
                                powerExceedRecord.mMobileRxPackets = j13;
                                powerExceedRecord.mMobileTxPackets = j14;
                                powerExceedRecord.mWifiRxBytes = j16;
                                powerExceedRecord.mWifiTxBytes = j23;
                                powerExceedRecord.mWifiRxPackets = j24;
                                powerExceedRecord.mWifiTxPackets = j19;
                                if (arrayList.size() > 0) {
                                    powerExceedRecord.mPartialWakelocks = arrayList;
                                }
                                if (arrayListNewArrayList2.size() > 0) {
                                    powerExceedRecord.mSyncStats = arrayListNewArrayList2;
                                }
                                this.mUidPartialWaklockRecords.put(Integer.valueOf(i6), powerExceedRecord);
                                uploadRecord(powerExceedRecord, !policyResult2.mIgnoreOverLimit);
                            }
                        } catch (Exception e9) {
                            e = e9;
                        }
                    } catch (Exception e10) {
                        e = e10;
                        str6 = str25;
                    }
                }
            }
            arrayList = arrayListNewArrayList;
            str = str21;
            str2 = str22;
            str3 = str14;
            str4 = str15;
            str5 = str16;
            str6 = str20;
            str12 = str12;
            str13 = str13;
        } catch (Exception e11) {
            e = e11;
            arrayList = arrayListNewArrayList;
            str = str21;
            str2 = str22;
            str3 = str14;
            str4 = str15;
            str5 = str16;
            str6 = str20;
        }
        sb2.append("Stats since unplugged:\n");
        sb2.append("Time on Battery: ");
        String str219 = str12;
        String str2110 = str13;
        PowerCheckerUtils.formatTimeMs(sb2, this.mBatteryStatsTime.whichBatteryRealtime / 1000);
        sb2.append(ShellUtils.COMMAND_LINE_END);
        baseInfo = appPowerExceedInfo.info;
        if (baseInfo.mobileRxBytes <= 0 || baseInfo.mobileTxBytes > 0 || baseInfo.mobileRxPackets > 0 || baseInfo.mobileTxPackets > 0) {
            sb2.append("Mobile network: ");
            sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.mobileRxBytes) + " received, ");
            sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.mobileTxBytes) + " sent ");
            StringBuilder sb117 = new StringBuilder();
            sb117.append("(packets ");
            sb117.append(appPowerExceedInfo.info.mobileRxPackets);
            sb117.append(" received, ");
            sb117.append(appPowerExceedInfo.info.mobileTxPackets);
            str7 = str23;
            sb117.append(str7);
            sb2.append(sb117.toString());
        } else {
            str7 = str23;
        }
        baseInfo2 = appPowerExceedInfo.info;
        str8 = str3;
        str9 = str5;
        if (baseInfo2.wifiRxBytes <= 0 || baseInfo2.wifiTxBytes > 0 || baseInfo2.wifiRxPackets > 0 || baseInfo2.wifiTxPackets > 0) {
            sb2.append("Wi-Fi network: ");
            sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.wifiRxBytes) + " received, ");
            sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.wifiTxBytes) + " sent ");
            sb2.append("(packets " + appPowerExceedInfo.info.wifiRxPackets + " received, " + appPowerExceedInfo.info.wifiTxPackets + str7);
        }
        if (wakeLockList.size() > 0) {
            it = wakeLockList.iterator();
            while (it.hasNext()) {
                UidStatsData.WakeLock next7 = it.next();
                sb2.append("Wake lock ");
                sb2.append(next7.wlName + str24);
                long j2112 = next7.wlFullTime;
                int i116 = next7.wlFullCount;
                PowerCheckerUtils.formatTimeMs(sb2, j2112);
                StringBuilder sb118 = new StringBuilder();
                sb118.append("full");
                String str2111 = str2;
                sb118.append(str2111);
                sb118.append(i116);
                sb118.append(str);
                sb2.append(sb118.toString());
                long j2113 = next7.wlPartialTime;
                int i117 = next7.wlPartialCount;
                PowerCheckerUtils.formatTimeMs(sb2, j2113);
                sb2.append("partial(unplugged)" + str2111 + i117 + str);
                long j2114 = next7.wlWindowTime;
                int i118 = next7.wlWindowCount;
                PowerCheckerUtils.formatTimeMs(sb2, j2114);
                StringBuilder sb119 = new StringBuilder();
                Iterator<UidStatsData.WakeLock> it10 = it;
                sb119.append("window");
                sb119.append(str2111);
                sb119.append(i118);
                sb119.append(str);
                sb2.append(sb119.toString());
                PowerCheckerUtils.formatTimeMs(sb2, j2114);
                sb2.append("Wake lock ");
                sb2.append(next7.wlName + str24);
                long j2115 = next7.wlPartialTime;
                int i119 = next7.wlPartialCount;
                PowerCheckerUtils.formatTimeMs(sb2, j2115);
                sb2.append("partial(charged)" + str2111 + i119 + str);
                it = it10;
            }
        }
        try {
            syncTimer = uidStatsData.getSyncTimer();
            if (syncTimer.size() > 0) {
                while (r0.hasNext()) {
                    j2 = timer3.totalTime;
                    i2 = timer3.count;
                    String str38 = str6;
                    sb2.append(str38);
                    StringBuilder sb1110 = new StringBuilder();
                    sb1110.append(timer3.name);
                    String str39 = str17;
                    sb1110.append(str39);
                    sb2.append(sb1110.toString());
                    if (j2 != 0) {
                        PowerCheckerUtils.formatTimeMs(sb2, j2);
                        sb2.append(str4 + i2 + str8);
                        str10 = str9;
                    } else {
                        str10 = str9;
                        sb2.append(str10);
                    }
                    str17 = str39;
                    str9 = str10;
                    str6 = str38;
                }
            }
        } catch (Exception e12) {
            Log.e(str2110, str219, e12);
        }
        sb2.append("Power checker battery stats since current:\n");
        sb2.append("Time on Battery and Screen off: ");
        PowerCheckerUtils.formatTimeMs(sb2, this.mBatteryRealTimeSinceCurrent / 1000);
        sb2.append(ShellUtils.COMMAND_LINE_END);
        if (appPowerExceedInfo.info.screenOffBytes > 0) {
            sb2.append("Screen off network: ");
            sb2.append(PowerCheckerUtils.formatBytesLocked(appPowerExceedInfo.info.screenOffBytes) + " \n");
        }
        if (appPowerExceedInfo.info.screenOffAudioOnTimeMs > 0) {
            sb2.append("Screen off audio: ");
            PowerCheckerUtils.formatTimeMs(sb2, appPowerExceedInfo.info.screenOffAudioOnTimeMs);
            sb2.append(str4 + appPowerExceedInfo.info.screenOffAudioOnTimes + str8);
        }
        if (this.mUidPartialWaklockRecords.containsKey(Integer.valueOf(i6))) {
            this.mUidPartialWaklockRecords.remove(Integer.valueOf(i6));
        }
        powerExceedRecord = new PowerExceedRecord(i6, 1, appPowerExceedInfo.packageName, appPowerExceedInfo.packageVersion, null, i7, i5, sb, sb2, this.mBatteryRealTimeInc);
        if (i5 == 2) {
            powerExceedRecord.mKillResult = z2;
        }
        if (i5 == 202) {
            policyResult2 = policyResult;
            powerExceedRecord.mActiveType = policyResult2.mActiveType;
        } else {
            policyResult2 = policyResult;
        }
        powerExceedRecord.mMobileRxBytes = j11;
        powerExceedRecord.mMobileTxBytes = j12;
        powerExceedRecord.mMobileRxPackets = j13;
        powerExceedRecord.mMobileTxPackets = j14;
        powerExceedRecord.mWifiRxBytes = j16;
        powerExceedRecord.mWifiTxBytes = j23;
        powerExceedRecord.mWifiRxPackets = j24;
        powerExceedRecord.mWifiTxPackets = j19;
        if (arrayList.size() > 0) {
            powerExceedRecord.mPartialWakelocks = arrayList;
        }
        if (arrayListNewArrayList2.size() > 0) {
            powerExceedRecord.mSyncStats = arrayListNewArrayList2;
        }
        this.mUidPartialWaklockRecords.put(Integer.valueOf(i6), powerExceedRecord);
        uploadRecord(powerExceedRecord, !policyResult2.mIgnoreOverLimit);
    }

    void notifyUidWakeupsExceedLimit(PowerCheckerPolicy.PolicyResult policyResult, PowerCheckerNotifier.PowerExceedInfo powerExceedInfo, boolean z2) {
        PowerExceedRecord powerExceedRecord;
        int i2 = policyResult.mActionRule;
        PowerCheckerNotifier.AppPowerExceedInfo appPowerExceedInfo = (PowerCheckerNotifier.AppPowerExceedInfo) powerExceedInfo;
        int i3 = appPowerExceedInfo.uid;
        int i4 = appPowerExceedInfo.priority;
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        String str = appPowerExceedInfo.pkg;
        int i5 = appPowerExceedInfo.wakeupTimes;
        int i6 = appPowerExceedInfo.lastWakeupTimes;
        sb.append(str + ":" + sRecordTypeStringMap[5]);
        int i7 = i5 - i6;
        sb2.append("Apk " + str + ":" + i7 + " wakeup alarms\n");
        sb2.append("Stats since unplugged:\n");
        sb2.append("Time on Battery: ");
        PowerCheckerUtils.formatTimeMs(sb2, this.mBatteryStatsTime.whichBatteryRealtime / 1000);
        sb2.append(ShellUtils.COMMAND_LINE_END);
        sb2.append("Apk " + str + ":" + i5 + " wakeup alarms\n");
        if (this.mPkgWakeupTimesRecords.containsKey(str)) {
            this.mPkgWakeupTimesRecords.remove(str);
        }
        PowerExceedRecord powerExceedRecord2 = new PowerExceedRecord(i3, 5, str, PackageUtil.getAppVersion(this.mPackageManager, str), null, i4, i2, sb, sb2, this.mBatteryRealTimeInc);
        if (i2 == 2) {
            powerExceedRecord = powerExceedRecord2;
            powerExceedRecord.mKillResult = z2;
        } else {
            powerExceedRecord = powerExceedRecord2;
        }
        if (i2 == 202) {
            powerExceedRecord.mActiveType = policyResult.mActiveType;
        }
        powerExceedRecord.mWakeupTimes = i7;
        this.mPkgWakeupTimesRecords.put(str, powerExceedRecord);
        uploadRecord(powerExceedRecord, !policyResult.mIgnoreOverLimit);
    }

    void notifyUidWifiScanExceedLimit(PowerCheckerPolicy.PolicyResult policyResult, PowerCheckerNotifier.PowerExceedInfo powerExceedInfo, boolean z2) {
        PowerCheckerPolicy.PolicyResult policyResult2;
        int i2 = policyResult.mActionRule;
        PowerCheckerNotifier.AppPowerExceedInfo appPowerExceedInfo = (PowerCheckerNotifier.AppPowerExceedInfo) powerExceedInfo;
        int i3 = appPowerExceedInfo.uid;
        int i4 = appPowerExceedInfo.priority;
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        if (i3 < 10000) {
            sb.append(i3 + ":" + sRecordTypeStringMap[2]);
        } else {
            sb.append(appPowerExceedInfo.packageName + ":" + sRecordTypeStringMap[2]);
        }
        sb2.append("Full Wifi Lock: ");
        long j2 = appPowerExceedInfo.info.fullWifiLockTime - appPowerExceedInfo.lastInfo.fullWifiLockTime;
        PowerCheckerUtils.formatTimeMs(sb2, j2);
        sb2.append(Constants.SEPARATOR_LEFT_PARENTESIS + PowerCheckerUtils.formatRatioLocked(j2 * 1000, this.mBatteryRealTimeInc) + ")\n");
        sb2.append("Wifi Scan: ");
        long j3 = appPowerExceedInfo.info.wifiScanTime - appPowerExceedInfo.lastInfo.wifiScanTime;
        PowerCheckerUtils.formatTimeMs(sb2, j3);
        sb2.append(Constants.SEPARATOR_LEFT_PARENTESIS + PowerCheckerUtils.formatRatioLocked(j3 * 1000, this.mBatteryRealTimeInc) + ")\n");
        sb2.append("Stats since unplugged:\n");
        sb2.append("Time on Battery: ");
        PowerCheckerUtils.formatTimeMs(sb2, this.mBatteryStatsTime.whichBatteryRealtime / 1000);
        sb2.append(ShellUtils.COMMAND_LINE_END);
        sb2.append("Full Wifi Lock: ");
        PowerCheckerUtils.formatTimeMs(sb2, appPowerExceedInfo.info.fullWifiLockTime);
        sb2.append(Constants.SEPARATOR_LEFT_PARENTESIS + PowerCheckerUtils.formatRatioLocked(appPowerExceedInfo.info.fullWifiLockTime * 1000, this.mBatteryStatsTime.whichBatteryRealtime) + ")\n");
        sb2.append("Wifi Scan: ");
        PowerCheckerUtils.formatTimeMs(sb2, appPowerExceedInfo.info.wifiScanTime);
        sb2.append(Constants.SEPARATOR_LEFT_PARENTESIS + PowerCheckerUtils.formatRatioLocked(appPowerExceedInfo.info.wifiScanTime * 1000, this.mBatteryStatsTime.whichBatteryRealtime) + ")\n");
        if (this.mUidWifiScanRecords.containsKey(Integer.valueOf(i3))) {
            this.mUidWifiScanRecords.remove(Integer.valueOf(i3));
        }
        PowerExceedRecord powerExceedRecord = new PowerExceedRecord(i3, 2, appPowerExceedInfo.packageName, appPowerExceedInfo.packageVersion, null, i4, i2, sb, sb2, this.mBatteryRealTimeInc);
        if (i2 == 2) {
            powerExceedRecord.mKillResult = z2;
        }
        if (i2 == 202) {
            policyResult2 = policyResult;
            powerExceedRecord.mActiveType = policyResult2.mActiveType;
        } else {
            policyResult2 = policyResult;
        }
        powerExceedRecord.mFullWifiLockTime = j2;
        powerExceedRecord.mWifiScanTime = j3;
        this.mUidWifiScanRecords.put(Integer.valueOf(i3), powerExceedRecord);
        uploadRecord(powerExceedRecord, !policyResult2.mIgnoreOverLimit);
    }

    @Override // com.miui.powerkeeper.powerchecker.PowerCheckerNotifier
    protected void onStartSchedulePowerCheckerCycle() {
        Log.i(TAG, "notify start a new schedule power check cycle");
        clearPowerExceedRecords();
    }

    @Override // com.miui.powerkeeper.powerchecker.PowerCheckerNotifier
    protected void onStopPowerCheckerImmediately() {
        Log.i(TAG, "notify stop power check cycle");
        clearPowerExceedRecords();
    }

    void updatePowerExceedRecordAction(int i2, int i3, String str, int i4) {
        switch (i2) {
            case 1:
                if (this.mUidPartialWaklockRecords.containsKey(Integer.valueOf(i3))) {
                    PowerExceedRecord powerExceedRecord = this.mUidPartialWaklockRecords.get(Integer.valueOf(i3));
                    powerExceedRecord.updateAction(i4);
                    uploadRecord(powerExceedRecord, true);
                }
                break;
            case 2:
                if (this.mUidWifiScanRecords.containsKey(Integer.valueOf(i3))) {
                    PowerExceedRecord powerExceedRecord2 = this.mUidWifiScanRecords.get(Integer.valueOf(i3));
                    powerExceedRecord2.updateAction(i4);
                    uploadRecord(powerExceedRecord2, true);
                }
                break;
            case 3:
                if (this.mUidBgGPSTimeRecords.containsKey(Integer.valueOf(i3))) {
                    PowerExceedRecord powerExceedRecord3 = this.mUidBgGPSTimeRecords.get(Integer.valueOf(i3));
                    powerExceedRecord3.updateAction(i4);
                    uploadRecord(powerExceedRecord3, true);
                }
                break;
            case 4:
                if (this.mProcBgCPUTimeRecords.containsKey(str)) {
                    PowerExceedRecord powerExceedRecord4 = this.mProcBgCPUTimeRecords.get(str);
                    powerExceedRecord4.updateAction(i4);
                    uploadRecord(powerExceedRecord4, true);
                }
                if (this.mProcBgCPUTimeRecords.size() > 0) {
                    for (Map.Entry<String, PowerExceedRecord> entry : this.mProcBgCPUTimeRecords.entrySet()) {
                        String packageNameByProcessName = PackageUtil.getPackageNameByProcessName(entry.getKey());
                        if (packageNameByProcessName != null && packageNameByProcessName.equals(str)) {
                            PowerExceedRecord value = entry.getValue();
                            value.updateAction(i4);
                            uploadRecord(value, true);
                        }
                    }
                }
                break;
            case 5:
                if (this.mPkgWakeupTimesRecords.containsKey(str)) {
                    PowerExceedRecord powerExceedRecord5 = this.mPkgWakeupTimesRecords.get(str);
                    powerExceedRecord5.updateAction(i4);
                    uploadRecord(powerExceedRecord5, true);
                }
                break;
            case 6:
                if (this.mUidMobileRadioRecords.containsKey(Integer.valueOf(i3))) {
                    PowerExceedRecord powerExceedRecord6 = this.mUidMobileRadioRecords.get(Integer.valueOf(i3));
                    powerExceedRecord6.updateAction(i4);
                    uploadRecord(powerExceedRecord6, true);
                }
                break;
        }
    }

    private void uploadRecord(PowerExceedRecord powerExceedRecord, boolean z2) {
    }
}
