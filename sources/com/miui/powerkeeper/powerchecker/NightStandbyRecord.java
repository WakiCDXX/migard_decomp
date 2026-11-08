package com.miui.powerkeeper.powerchecker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.app.IBatteryStats;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.ArrayUtils;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.appcontrol.HideModeStateMachineConfig;
import com.miui.powerkeeper.cloudcontrol.CloudFunctionConfig;
import com.miui.powerkeeper.cloudcontrol.LocalUpdateUtils;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.utils.Constant;
import com.miui.powerkeeper.utils.KernelRpmStatsReader;
import com.miui.powerkeeper.utils.Utils;
import com.xiaomi.channelcollection.RegisterAppChannelInfoCollectionJob;
import com.xiaomi.mipush.sdk.Constants;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class NightStandbyRecord {
    private static final int ADSP = 4;
    private static final int ALLOW_SET_ALARM = 1;
    private static final int APSS = 2;
    private static final int CDSP = 16;
    public static final String KEY_ACTUAL = "ac";
    public static final String KEY_BLUETOOTH = "bt";
    public static final String KEY_CAMERA = "cmr";
    public static final String KEY_CELLSTANDBY = "cs";
    public static final String KEY_COMPUTED = "cp";
    public static final String KEY_COUNT = "count";
    public static final String KEY_FLASHLIGHT = "fls";
    public static final String KEY_FOREGROUND_ACTIVITIES = "fg";
    public static final String KEY_FOREGROUND_ACTIVITIES_COUNT = "fgc";
    public static final String KEY_FULL_WAKELOCK_TIME = "fwlt";
    public static final String KEY_FULL_WIFI_LOCK_TIME = "fwifil";
    public static final String KEY_IDLE = "idle";
    public static final String KEY_MOBILE_RADIO_ACTIVE = "mra";
    public static final String KEY_MOBILE_RADIO_ACTIVE_COUNT = "mrac";
    public static final String KEY_MOBILE_RADIO_ACTIVE_TIME = "mrat";
    public static final String KEY_OVERACCOUNTED = "ov";
    public static final String KEY_PACKAGE = "pkg";
    public static final String KEY_PARTIAL_WAKELOCK_TIME = "pwlt";
    public static final String KEY_PHONECALLS = "pcs";
    public static final String KEY_POWER = "power";
    public static final String KEY_RECORD_FAIL_CLOUD_UPDATE = "errorcloud";
    public static final String KEY_RECORD_FAIL_FIRST_CHARGING = "errorfirst";
    public static final String KEY_RECORD_FAIL_REASON_CHARGING = "errorcharg";
    public static final String KEY_RECORD_SLEEP_MODE_MESSAGE = "sleepmsg";
    public static final String KEY_RECORD_TIME = "rt";
    public static final String KEY_RECORD_VALUE = "record";
    public static final String KEY_RX_MOBILE_NETWORK = "rxmntb";
    public static final String KEY_RX_MOBILE_NETWORK_PACKAGE = "rxmntp";
    public static final String KEY_RX_WIFI_NETWORK = "rwntb";
    public static final String KEY_RX_WIFI_NETWORK_PACKAGE = "rwntp";
    public static final String KEY_SCREEN = "scr";
    public static final String KEY_SENSOR_GPS_TIME = "gps";
    public static final String KEY_SENSOR_GPS_TIME_COUNT = "gpsc";
    public static final String KEY_SLEEP_STATE = "sleepstate";
    public static final String KEY_SUBSYSCHECK = "subsys";
    public static final String KEY_TAG = "tag";
    public static final String KEY_TIME = "time";
    public static final String KEY_TIME_ON_BATTERY = "tob";
    public static final String KEY_TIME_ON_BATTERY_SCREEN_OFF = "toff";
    public static final String KEY_TIME_ON_BATTERY_SCREEN_OFF_UPTIME = "toffup";
    public static final String KEY_TIME_ON_BATTERY_UPTIME = "tobup";
    public static final String KEY_TOTAL_RX_MOBILE_NETWORK = "trxmntb";
    public static final String KEY_TOTAL_RX_MOBILE_NETWORK_PACKAGE = "trxmntp";
    public static final String KEY_TOTAL_RX_WIFI_NETWORK = "twntrb";
    public static final String KEY_TOTAL_RX_WIFI_NETWORK_PACKAGE = "twnrtp";
    public static final String KEY_TOTAL_SYSTEM_CPU_TIME = "syscpu";
    public static final String KEY_TOTAL_TX_MOBILE_NETWORK = "ttxmntb";
    public static final String KEY_TOTAL_TX_MOBILE_NETWORK_PACKAGE = "ttxmntp";
    public static final String KEY_TOTAL_TX_WIFI_NETWORK = "twnttb";
    public static final String KEY_TOTAL_TX_WIFI_NETWORK_PACKAGE = "twnttp";
    public static final String KEY_TOTAL_USER_CPU_TIME = "usercpu";
    public static final String KEY_TX_MOBILE_NETWORK = "txmntb";
    public static final String KEY_TX_MOBILE_NETWORK_PACKAGE = "txmntp";
    public static final String KEY_TX_WIFI_NETWORK = "twntb";
    public static final String KEY_TX_WIFI_NETWORK_PACKAGE = "twntp";
    public static final String KEY_UID = "uid";
    public static final String KEY_UNACCOUNTED = "un";
    public static final String KEY_USAGE_POWER = "pwr";
    public static final String KEY_USER = "user";
    public static final String KEY_WIFI = "wf";
    public static final String KEY_WIFI_SCAN_TIME = "wst";
    public static final int LIMIT_MIN_COUNT = 10;
    public static final int LIMIT_MIN_TIME = 60000;
    private static final int MAX_RPM_ITEM_NUMS = 30;
    private static final int MPSS = 1;
    private static final int MSG_RECORD = 1;
    private static final int MSG_UPDATECLOUDE = 2;
    private static final int NIGHT_MODE_END = 9;
    private static final int RECORD_ABORT = -1;
    private static final int RECORD_CONTINUE = 1;
    private static final int RECORD_START = 0;
    private static final int SLPI = 8;
    private static final String TAG = "NightStandbyRecord";
    private PowerKeeperInterface.d mBatteryChangedCallback;
    private boolean mCharging;
    private int mChargingRecordFail;
    private int[] mCloudConfigureState;
    private int mCloudUpdateRecordFail;
    private Context mContext;
    private int mCurrentRecordStats;
    private int mCurrentSleepMode;
    private int mFirstModeChargingRecordFail;
    private NightStandbyHandler mHandler;
    private BroadcastReceiver mReceiver;
    private boolean mRegisterBroadcast;
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    public static int WHICH = 0;
    private static final String[] MONITER_START = {"-1"};
    private ArrayMap<String, PowerCheckerNotifier.Timer> mKernelWakeupStats = new ArrayMap<>();
    private ArrayMap<String, PowerCheckerNotifier.Timer> mPartialWakelockStats = new ArrayMap<>();
    private ArrayMap<String, PowerCheckerNotifier.Timer> mSensorStats = new ArrayMap<>();
    private ArrayMap<Integer, ArrayMap<String, Integer>> mWakeupAlarmStatsResult = new ArrayMap<>();
    private ArrayMap<String, Integer> mLastWakeupAlarmStats = new ArrayMap<>();
    private ArrayMap<String, PowerCheckerNotifier.Timer> mSyncStats = new ArrayMap<>();
    private ArrayMap<String, PowerCheckerNotifier.Timer> mJobStats = new ArrayMap<>();
    private SparseArray<PowerCheckerNotifier.UidRecordInfo> mPowerCheckerUidStats = new SparseArray<>();
    private SparseArray<Double> mAppPower = new SparseArray<>();
    private final LinkedList<SubSysCheckItem> mRpmItemHistoryStats = new LinkedList<>();
    private ArrayMap<String, PowerCheckerNotifier.Timer> mKernelWakelockStats = new ArrayMap<>();
    private ArrayMap<String, PowerCheckerNotifier.Timer> mKernelWakelockStatsResult = new ArrayMap<>();
    private ArrayMap<String, PowerCheckerNotifier.Timer> mKernelWakeupStatsResult = new ArrayMap<>();
    private ArrayMap<Integer, ArrayMap<String, PowerCheckerNotifier.Timer>> mPartialWakelockStatsResult = new ArrayMap<>();
    private ArrayMap<Integer, ArrayMap<Integer, PowerCheckerNotifier.Timer>> mSensorStatsResult = new ArrayMap<>();
    private ArrayMap<Integer, ArrayMap<String, PowerCheckerNotifier.Timer>> mJobStatsResult = new ArrayMap<>();
    private ArrayMap<Integer, ArrayMap<String, PowerCheckerNotifier.Timer>> mSyncStatsResult = new ArrayMap<>();
    private SparseArray<Double> mAppPowerResult = new SparseArray<>();
    private SparseArray<String> mAppPackageResult = new SparseArray<>();
    private SparseArray<PowerCheckerNotifier.UidRecordInfo> mPowerCheckerUidStatsResult = new SparseArray<>();
    private SparseArray<JSONObject> mSleepModeResult = new SparseArray<>();
    private ArrayList<Integer> mSleepModeBroacast = new ArrayList<>();
    private BatteryUsageStats mLastBatteryUsageStats = new BatteryUsageStats();
    private int mSubSysCheck = -1;
    private BroadcastReceiver mSleepModeReceiver = new BroadcastReceiver() { // from class: com.miui.powerkeeper.powerchecker.NightStandbyRecord.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent == null || !"action_sleep_state_changed".equals(intent.getAction())) {
                return;
            }
            int intExtra = intent.getIntExtra("cur_state", 0);
            int intExtra2 = intent.getIntExtra("pre_state", 0);
            NightStandbyRecord.this.mSleepModeBroacast.add(Integer.valueOf(intExtra));
            NightStandbyRecord.this.mCurrentSleepMode = intExtra;
            if (NightStandbyRecord.this.mCurrentSleepMode == 1 && NightStandbyRecord.this.mCharging) {
                NightStandbyRecord.this.mFirstModeChargingRecordFail = -1;
            }
            NightStandbyRecord.this.sendRecordMessage(intExtra, intExtra2);
            if (NightStandbyRecord.DEBUG) {
                Log.d(NightStandbyRecord.TAG, "receive sleep changed cur_state = " + intExtra + ", pre_state = " + intExtra2);
            }
        }
    };
    private PowerKeeperInterface.m mBatteryStatusReceiver = new PowerKeeperInterface.m() { // from class: com.miui.powerkeeper.powerchecker.NightStandbyRecord.2
        @Override // com.miui.powerkeeper.PowerKeeperInterface.m
        public void onBatteryChanged(int i2, int i3, int i4, int i5, int i6, int i7) {
            NightStandbyRecord nightStandbyRecord = NightStandbyRecord.this;
            nightStandbyRecord.mCharging = nightStandbyRecord.isCharging(i2, i6);
            if (NightStandbyRecord.this.mCharging) {
                NightStandbyRecord.this.mCurrentRecordStats = -1;
                if (NightStandbyRecord.this.mCurrentSleepMode != 0) {
                    NightStandbyRecord.this.mChargingRecordFail = -1;
                }
            }
        }
    };
    private CloudFunctionConfig.ICloudFunctionListener mFunctionListener = new CloudFunctionConfig.ICloudFunctionListener() { // from class: com.miui.powerkeeper.powerchecker.NightStandbyRecord.3
        @Override // com.miui.powerkeeper.cloudcontrol.CloudFunctionConfig.ICloudFunctionListener
        public void onCloudFunctionUpdated() {
            Message messageObtain = Message.obtain(NightStandbyRecord.this.mHandler);
            messageObtain.what = 2;
            NightStandbyRecord.this.mHandler.sendMessage(messageObtain);
        }
    };
    private PowerKeeperManager mPowerKeeperManager = PowerKeeperManager.getInstance();

    private class BatteryUsageStats {
        private long mBatteryLevel;
        private long mBatteryRealTime;
        private long mBatteryScreenOffRealTimeInc;
        private long mBatteryScreenOffUpTimeInc;
        private long mBatteryUpTime;
        private double mBluetoothEstimated;
        private double mCameraEstimated;
        private double mCellStandbyEstimated;
        private double mComputedPower;
        private double mFlashEstimated;
        private double mIdleEstimated;
        private long mMobileRadioActiveTime;
        private long mMobileRxTotalBytes;
        private long mMobileRxTotalPackets;
        private long mMobileTxTotalBytes;
        private long mMobileTxTotalPackets;
        private double mOverCounted;
        private double mPhoneEstimated;
        private double mScreenEstimated;
        private double mTotalAveragePower;
        private double mUnaccounted;
        private double mUserEstimated;
        private double mWifiEstimated;
        private long mWifiRxTotalBytes;
        private long mWifiRxTotalPackets;
        private long mWifiTxTotalBytes;
        private long mWifiTxTotalPackets;

        /* JADX INFO: Access modifiers changed from: private */
        public void reset() {
            this.mBatteryRealTime = 0L;
            this.mBatteryUpTime = 0L;
            this.mMobileRadioActiveTime = 0L;
            this.mMobileRxTotalPackets = 0L;
            this.mMobileTxTotalPackets = 0L;
            this.mWifiRxTotalPackets = 0L;
            this.mWifiTxTotalPackets = 0L;
            this.mMobileRxTotalBytes = 0L;
            this.mMobileTxTotalBytes = 0L;
            this.mWifiRxTotalBytes = 0L;
            this.mWifiTxTotalBytes = 0L;
            this.mBatteryScreenOffRealTimeInc = 0L;
            this.mBatteryScreenOffUpTimeInc = 0L;
            this.mBatteryLevel = 0L;
            this.mComputedPower = 0.0d;
            this.mTotalAveragePower = 0.0d;
            this.mUnaccounted = 0.0d;
            this.mOverCounted = 0.0d;
            this.mScreenEstimated = 0.0d;
            this.mCellStandbyEstimated = 0.0d;
            this.mBluetoothEstimated = 0.0d;
            this.mWifiEstimated = 0.0d;
            this.mIdleEstimated = 0.0d;
            this.mPhoneEstimated = 0.0d;
            this.mCameraEstimated = 0.0d;
            this.mFlashEstimated = 0.0d;
            this.mUserEstimated = 0.0d;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public BatteryUsageStats substract(BatteryUsageStats batteryUsageStats) {
            BatteryUsageStats batteryUsageStats2 = NightStandbyRecord.this.new BatteryUsageStats();
            batteryUsageStats2.mBatteryRealTime = this.mBatteryRealTime - batteryUsageStats.mBatteryRealTime;
            batteryUsageStats2.mBatteryUpTime = this.mBatteryUpTime - batteryUsageStats.mBatteryUpTime;
            batteryUsageStats2.mMobileRadioActiveTime = this.mMobileRadioActiveTime - batteryUsageStats.mMobileRadioActiveTime;
            batteryUsageStats2.mMobileRxTotalPackets = this.mMobileRxTotalPackets - batteryUsageStats.mMobileRxTotalPackets;
            batteryUsageStats2.mMobileTxTotalPackets = this.mMobileTxTotalPackets - batteryUsageStats.mMobileTxTotalPackets;
            batteryUsageStats2.mWifiRxTotalPackets = this.mWifiRxTotalPackets - batteryUsageStats.mWifiRxTotalPackets;
            batteryUsageStats2.mWifiTxTotalPackets = this.mWifiTxTotalPackets - batteryUsageStats.mWifiTxTotalPackets;
            batteryUsageStats2.mMobileRxTotalBytes = this.mMobileRxTotalBytes - batteryUsageStats.mMobileRxTotalBytes;
            batteryUsageStats2.mMobileTxTotalBytes = this.mMobileTxTotalBytes - batteryUsageStats.mMobileTxTotalBytes;
            batteryUsageStats2.mWifiRxTotalBytes = this.mWifiRxTotalBytes - batteryUsageStats.mWifiRxTotalBytes;
            batteryUsageStats2.mWifiTxTotalBytes = this.mWifiTxTotalBytes - batteryUsageStats.mWifiTxTotalBytes;
            batteryUsageStats2.mBatteryScreenOffRealTimeInc = this.mBatteryScreenOffRealTimeInc - batteryUsageStats.mBatteryScreenOffRealTimeInc;
            batteryUsageStats2.mBatteryScreenOffUpTimeInc = this.mBatteryScreenOffUpTimeInc - batteryUsageStats.mBatteryScreenOffUpTimeInc;
            batteryUsageStats2.mComputedPower = this.mComputedPower - batteryUsageStats.mComputedPower;
            batteryUsageStats2.mTotalAveragePower = this.mTotalAveragePower - batteryUsageStats.mTotalAveragePower;
            batteryUsageStats2.mUnaccounted = this.mUnaccounted - batteryUsageStats.mUnaccounted;
            batteryUsageStats2.mOverCounted = this.mOverCounted - batteryUsageStats.mOverCounted;
            batteryUsageStats2.mScreenEstimated = this.mScreenEstimated - batteryUsageStats.mScreenEstimated;
            batteryUsageStats2.mCellStandbyEstimated = this.mCellStandbyEstimated - batteryUsageStats.mCellStandbyEstimated;
            batteryUsageStats2.mBluetoothEstimated = this.mBluetoothEstimated - batteryUsageStats.mBluetoothEstimated;
            batteryUsageStats2.mWifiEstimated = this.mWifiEstimated - batteryUsageStats.mWifiEstimated;
            batteryUsageStats2.mIdleEstimated = this.mIdleEstimated - batteryUsageStats.mIdleEstimated;
            batteryUsageStats2.mPhoneEstimated = this.mPhoneEstimated - batteryUsageStats.mPhoneEstimated;
            batteryUsageStats2.mCameraEstimated = this.mCameraEstimated - batteryUsageStats.mCameraEstimated;
            batteryUsageStats2.mFlashEstimated = this.mFlashEstimated - batteryUsageStats.mFlashEstimated;
            batteryUsageStats2.mUserEstimated = this.mUserEstimated - batteryUsageStats.mUserEstimated;
            batteryUsageStats2.mBatteryLevel = batteryUsageStats.mBatteryLevel - this.mBatteryLevel;
            return batteryUsageStats2;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(1024);
            sb.append("mBatteryRealTime ");
            sb.append(this.mBatteryRealTime);
            sb.append(" mBatteryUpTime ");
            sb.append(this.mBatteryUpTime);
            sb.append(" mMobileRadioActiveTime ");
            sb.append(this.mMobileRadioActiveTime);
            sb.append(" mMobileRxTotalPackets ");
            sb.append(this.mMobileRxTotalPackets);
            sb.append(" mMobileTxTotalPackets ");
            sb.append(this.mMobileTxTotalPackets);
            sb.append(" mWifiRxTotalPackets ");
            sb.append(this.mWifiRxTotalPackets);
            sb.append(" mWifiTxTotalPackets ");
            sb.append(this.mWifiTxTotalPackets);
            sb.append(" mBatteryScreenOffRealTimeInc ");
            sb.append(this.mBatteryScreenOffRealTimeInc);
            sb.append(" mBatteryScreenOffUpTimeInc ");
            sb.append(this.mBatteryScreenOffUpTimeInc);
            sb.append(" mBatteryLevel ");
            sb.append(this.mBatteryLevel);
            sb.append(" mComputedPower ");
            sb.append(this.mComputedPower);
            sb.append(" mTotalAveragePower ");
            sb.append(this.mTotalAveragePower);
            sb.append(" mUnaccounted ");
            sb.append(this.mUnaccounted);
            sb.append(" mOverCounted ");
            sb.append(this.mOverCounted);
            sb.append(" mScreenEstimated ");
            sb.append(this.mScreenEstimated);
            sb.append(" mCellStandbyEstimated ");
            sb.append(this.mCellStandbyEstimated);
            sb.append(" mBluetoothEstimated ");
            sb.append(this.mBluetoothEstimated);
            sb.append(" mWifiEstimated ");
            sb.append(this.mWifiEstimated);
            sb.append(" mIdleEstimated ");
            sb.append(this.mIdleEstimated);
            sb.append(" mPhoneEstimated ");
            sb.append(this.mPhoneEstimated);
            sb.append(" mCameraEstimated ");
            sb.append(this.mCameraEstimated);
            sb.append(" mFlashEstimated ");
            sb.append(this.mFlashEstimated);
            sb.append(" mUserEstimated ");
            sb.append(this.mUserEstimated);
            return sb.toString();
        }

        private BatteryUsageStats() {
            this.mBatteryRealTime = 0L;
            this.mBatteryUpTime = 0L;
            this.mMobileRadioActiveTime = 0L;
            this.mMobileRxTotalPackets = 0L;
            this.mMobileTxTotalPackets = 0L;
            this.mWifiRxTotalPackets = 0L;
            this.mWifiTxTotalPackets = 0L;
            this.mMobileRxTotalBytes = 0L;
            this.mMobileTxTotalBytes = 0L;
            this.mWifiRxTotalBytes = 0L;
            this.mWifiTxTotalBytes = 0L;
            this.mBatteryScreenOffRealTimeInc = 0L;
            this.mBatteryScreenOffUpTimeInc = 0L;
            this.mBatteryLevel = 0L;
            this.mComputedPower = 0.0d;
            this.mTotalAveragePower = 0.0d;
            this.mUnaccounted = 0.0d;
            this.mOverCounted = 0.0d;
            this.mScreenEstimated = 0.0d;
            this.mCellStandbyEstimated = 0.0d;
            this.mBluetoothEstimated = 0.0d;
            this.mWifiEstimated = 0.0d;
            this.mIdleEstimated = 0.0d;
            this.mPhoneEstimated = 0.0d;
            this.mCameraEstimated = 0.0d;
            this.mFlashEstimated = 0.0d;
            this.mUserEstimated = 0.0d;
        }
    }

    private class NightStandbyHandler extends Handler {
        public NightStandbyHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i2 = message.what;
            if (i2 == 1) {
                NightStandbyRecord.this.updateRecordBatteryStats(message.arg1, message.arg2);
            } else {
                if (i2 != 2) {
                    return;
                }
                NightStandbyRecord.this.updateCloudFunction();
            }
        }
    }

    private final class SubSysCheckItem {
        public long ADSPXoCnt;
        public long APSSXoCnt;
        public long CDSPXoCnt;
        public long MPSSXoCnt;
        public long SLPIXoCnt;
        public long VddMinCnt;

        public String toString() {
            StringBuilder sb = new StringBuilder(512);
            sb.append("VddMinCnt ");
            sb.append(this.VddMinCnt);
            sb.append(" MPSSXoCnt ");
            sb.append(this.MPSSXoCnt);
            sb.append(" APSSXoCnt ");
            sb.append(this.APSSXoCnt);
            sb.append(" ADSPXoCnt ");
            sb.append(this.ADSPXoCnt);
            sb.append(" SLPIXoCnt ");
            sb.append(this.SLPIXoCnt);
            sb.append(" CDSPXoCnt ");
            sb.append(this.CDSPXoCnt);
            return sb.toString();
        }

        private SubSysCheckItem() {
        }
    }

    public NightStandbyRecord(Context context) {
        this.mContext = context;
        CloudFunctionConfig.getInstance().registerCloudFunctionConfig(this.mFunctionListener);
        this.mHandler = new NightStandbyHandler(BackgroundThread.getHandler().getLooper());
        updateCloudFunction();
        resetRecord();
    }

    private BroadcastReceiver broadcastReceiver() {
        return new BroadcastReceiver() { // from class: com.miui.powerkeeper.powerchecker.NightStandbyRecord.4
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (NightStandbyRecord.DEBUG) {
                    Log.d(NightStandbyRecord.TAG, "receive: " + action);
                }
                if ("miui.intent.action.powerkeeper_nightstandbyrecord".equals(action)) {
                    NightStandbyRecord.this.resetRecord();
                    NightStandbyRecord.this.setNextAlarm();
                }
            }
        };
    }

    private void cloudUpdateAbortRecord(int[] iArr) {
        if (DEBUG) {
            Log.d(TAG, "The cloud configure update , mCloudConfigureState.length" + this.mCloudConfigureState.length + ", cloudConfigureState.length" + iArr.length);
        }
        this.mCurrentRecordStats = -1;
        if (this.mCurrentSleepMode != 0) {
            this.mCloudUpdateRecordFail = -1;
        }
    }

    private void cloudUpdateChange(int[] iArr) {
        int[] iArr2 = this.mCloudConfigureState;
        if (iArr2 == null || iArr == null) {
            return;
        }
        if (iArr2.length != iArr.length) {
            cloudUpdateAbortRecord(iArr);
            return;
        }
        int i2 = 0;
        while (true) {
            int[] iArr3 = this.mCloudConfigureState;
            if (i2 >= iArr3.length) {
                return;
            }
            if (iArr3[i2] != iArr[i2]) {
                cloudUpdateAbortRecord(iArr);
                return;
            }
            i2++;
        }
    }

    private void computeAlarmStats(UidStatsData uidStatsData, int i2, PowerCheckerNotifier.UidStatsInfo uidStatsInfo) {
        ArrayMap<String, Integer> pkgWakeUpAlarmCount = uidStatsData.getPkgWakeUpAlarmCount();
        if (pkgWakeUpAlarmCount.size() <= 0) {
            return;
        }
        for (Map.Entry<String, Integer> entry : pkgWakeUpAlarmCount.entrySet()) {
            try {
                Integer value = entry.getValue();
                int iIntValue = value.intValue();
                if (iIntValue > 0) {
                    Integer num = this.mLastWakeupAlarmStats.get(i2 + RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT + entry.getKey());
                    this.mLastWakeupAlarmStats.put(i2 + RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT + entry.getKey(), value);
                    if (this.mCurrentRecordStats == 1) {
                        if (num == null) {
                            uidStatsInfo.pkgWakeupStats.put(entry.getKey(), value);
                        } else if (iIntValue - num.intValue() > 0) {
                            uidStatsInfo.pkgWakeupStats.put(entry.getKey(), Integer.valueOf(iIntValue - num.intValue()));
                        }
                    }
                }
            } catch (Exception unused) {
            }
        }
        if (uidStatsInfo.pkgWakeupStats.size() > 0) {
            this.mWakeupAlarmStatsResult.put(Integer.valueOf(i2), uidStatsInfo.pkgWakeupStats);
        }
    }

    private void computeJobStatsResult(UidStatsData uidStatsData, int i2, long j2) {
        List<UidStatsData.Timer> jobTimer = uidStatsData.getJobTimer();
        if (jobTimer.size() <= 0) {
            return;
        }
        ArrayMap<String, PowerCheckerNotifier.Timer> arrayMap = new ArrayMap<>();
        for (UidStatsData.Timer timer : jobTimer) {
            long j3 = timer.totalTime;
            int i3 = timer.count;
            if (j3 > 0) {
                PowerCheckerNotifier.Timer timer2 = this.mJobStats.get(i2 + RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT + timer.name);
                this.mJobStats.put(i2 + RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT + timer.name, new PowerCheckerNotifier.Timer(j3, i3));
                if (this.mCurrentRecordStats == 1) {
                    if (timer2 != null) {
                        computeJobStatsResult(i3 - timer2.count, j3 - timer2.time, arrayMap, timer.name);
                    } else {
                        computeJobStatsResult(i3, j3, arrayMap, timer.name);
                    }
                }
            }
        }
        if (arrayMap.size() > 0) {
            this.mJobStatsResult.put(Integer.valueOf(i2), arrayMap);
        }
    }

    private void computeKernelWakeUpStats(long j2, int i2, String str) {
        if (j2 > 60000 || i2 > 10) {
            this.mKernelWakeupStatsResult.put(str, new PowerCheckerNotifier.Timer(j2, i2));
        }
    }

    private void computeKernelWakelockStats(long j2, int i2, String str) {
        if (j2 > 60000 || i2 > 10) {
            this.mKernelWakelockStatsResult.put(str, new PowerCheckerNotifier.Timer(j2, i2));
        }
    }

    private void computeSensorState(SparseArray<UidStatsData.Timer> sparseArray, PowerCheckerNotifier.UidStatsInfo uidStatsInfo, int i2, long j2) {
        if (sparseArray.size() <= 0) {
            return;
        }
        ArrayMap<Integer, PowerCheckerNotifier.Timer> arrayMap = new ArrayMap<>();
        for (int i3 = 0; i3 < sparseArray.size(); i3++) {
            if (sparseArray.keyAt(i3) == -10000) {
                uidStatsInfo.info.countGPS = sparseArray.valueAt(i3).count;
                uidStatsInfo.info.gpsSensorHoldTime = sparseArray.valueAt(i3).totalTime;
            } else {
                int i4 = sparseArray.valueAt(i3).count;
                long j3 = sparseArray.valueAt(i3).totalTime;
                if (j3 > 0) {
                    PowerCheckerNotifier.Timer timer = this.mSensorStats.get(i2 + RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT + sparseArray.keyAt(i3));
                    this.mSensorStats.put(i2 + RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT + sparseArray.keyAt(i3), new PowerCheckerNotifier.Timer(j3, i4));
                    if (this.mCurrentRecordStats == 1) {
                        if (timer != null) {
                            computeSensorStateResult(i4 - timer.count, j3 - timer.time, arrayMap, sparseArray.keyAt(i3));
                        } else {
                            computeSensorStateResult(i4, j3, arrayMap, sparseArray.keyAt(i3));
                        }
                    }
                }
            }
        }
        if (arrayMap.size() > 0) {
            this.mSensorStatsResult.put(Integer.valueOf(i2), arrayMap);
        }
    }

    private void computeSensorStateResult(int i2, long j2, ArrayMap<Integer, PowerCheckerNotifier.Timer> arrayMap, int i3) {
        if (i2 > 10 || j2 > 60000) {
            arrayMap.put(Integer.valueOf(i3), new PowerCheckerNotifier.Timer(j2, i2));
        }
    }

    private void computeSyncStats(UidStatsData uidStatsData, int i2, long j2) {
        List<UidStatsData.Timer> syncTimer = uidStatsData.getSyncTimer();
        if (syncTimer.size() <= 0) {
            return;
        }
        ArrayMap<String, PowerCheckerNotifier.Timer> arrayMap = new ArrayMap<>();
        for (UidStatsData.Timer timer : syncTimer) {
            long j3 = timer.totalTime;
            int i3 = timer.count;
            if (j3 > 0) {
                PowerCheckerNotifier.Timer timer2 = this.mSyncStats.get(i2 + RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT + timer.name);
                this.mSyncStats.put(i2 + RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT + timer.name, new PowerCheckerNotifier.Timer(j3, i3));
                if (this.mCurrentRecordStats == 1) {
                    if (timer2 != null) {
                        computeSyncStatsResult(i3 - timer2.count, j3 - timer2.time, arrayMap, timer.name);
                    } else {
                        computeSyncStatsResult(i3, j3, arrayMap, timer.name);
                    }
                }
            }
        }
        if (arrayMap.size() > 0) {
            this.mSyncStatsResult.put(Integer.valueOf(i2), arrayMap);
        }
    }

    private void computeSyncStatsResult(int i2, long j2, ArrayMap<String, PowerCheckerNotifier.Timer> arrayMap, String str) {
        if (i2 > 10 || j2 > 60000) {
            arrayMap.put(str, new PowerCheckerNotifier.Timer(j2, i2));
        }
    }

    private void computeUidSource(int i2, PowerCheckerNotifier.UidStatsInfo uidStatsInfo, PowerCheckerNotifier.UidStatsInfo uidStatsInfo2, PowerCheckerNotifier.UidRecordInfo uidRecordInfo) {
        if (this.mCurrentRecordStats != 1) {
            return;
        }
        if (uidRecordInfo != null) {
            PowerCheckerNotifier.BaseInfo baseInfo = uidStatsInfo.info;
            PowerCheckerNotifier.BaseInfo baseInfo2 = uidStatsInfo2.info;
            long j2 = baseInfo2.mobileActiveTime;
            PowerCheckerNotifier.BaseInfo baseInfo3 = uidRecordInfo.info;
            baseInfo.mobileActiveTime = j2 - baseInfo3.mobileActiveTime;
            baseInfo.mobileActiveCount = baseInfo2.mobileActiveCount - baseInfo3.mobileActiveCount;
            baseInfo.mobileRxBytes = baseInfo2.mobileRxBytes - baseInfo3.mobileRxBytes;
            baseInfo.mobileTxBytes = baseInfo2.mobileTxBytes - baseInfo3.mobileTxBytes;
            baseInfo.wifiRxBytes = baseInfo2.wifiRxBytes - baseInfo3.wifiRxBytes;
            baseInfo.wifiTxBytes = baseInfo2.wifiTxBytes - baseInfo3.wifiTxBytes;
            baseInfo.mobileRxPackets = baseInfo2.mobileRxPackets - baseInfo3.mobileRxPackets;
            baseInfo.mobileTxPackets = baseInfo2.mobileTxPackets - baseInfo3.mobileTxPackets;
            baseInfo.wifiRxPackets = baseInfo2.wifiRxPackets - baseInfo3.wifiRxPackets;
            baseInfo.wifiTxPackets = baseInfo2.wifiTxPackets - baseInfo3.wifiTxPackets;
            baseInfo.totalUserCpuTime = baseInfo2.totalUserCpuTime - baseInfo3.totalUserCpuTime;
            baseInfo.totalSystemCpuTime = baseInfo2.totalSystemCpuTime - baseInfo3.totalSystemCpuTime;
            baseInfo.totalFgTimeMs = baseInfo2.totalFgTimeMs - baseInfo3.totalFgTimeMs;
            baseInfo.totalFgTimes = baseInfo2.totalFgTimes - baseInfo3.totalFgTimes;
            baseInfo.wifiScanTime = baseInfo2.wifiScanTime - baseInfo3.wifiScanTime;
            baseInfo.fullWifiLockTime = baseInfo2.fullWifiLockTime - baseInfo3.fullWifiLockTime;
            baseInfo.gpsSensorHoldTime = baseInfo2.gpsSensorHoldTime - baseInfo3.gpsSensorHoldTime;
            baseInfo.totalFull = baseInfo2.totalFull - baseInfo3.totalFull;
            baseInfo.totalPartial = baseInfo2.totalPartial - baseInfo3.totalPartial;
        } else {
            PowerCheckerNotifier.BaseInfo baseInfo4 = uidStatsInfo.info;
            PowerCheckerNotifier.BaseInfo baseInfo5 = uidStatsInfo2.info;
            baseInfo4.mobileActiveTime = baseInfo5.mobileActiveTime;
            baseInfo4.mobileActiveCount = baseInfo5.mobileActiveCount;
            baseInfo4.mobileRxBytes = baseInfo5.mobileRxBytes;
            baseInfo4.mobileTxBytes = baseInfo5.mobileTxBytes;
            baseInfo4.wifiRxBytes = baseInfo5.wifiRxBytes;
            baseInfo4.wifiTxBytes = baseInfo5.wifiTxBytes;
            baseInfo4.mobileRxPackets = baseInfo5.mobileRxPackets;
            baseInfo4.mobileTxPackets = baseInfo5.mobileTxPackets;
            baseInfo4.wifiRxPackets = baseInfo5.wifiRxPackets;
            baseInfo4.wifiTxPackets = baseInfo5.wifiTxPackets;
            baseInfo4.totalUserCpuTime = baseInfo5.totalUserCpuTime;
            baseInfo4.totalSystemCpuTime = baseInfo5.totalSystemCpuTime;
            baseInfo4.totalFgTimeMs = baseInfo5.totalFgTimeMs;
            baseInfo4.totalFgTimes = baseInfo5.totalFgTimes;
            baseInfo4.wifiScanTime = baseInfo5.wifiScanTime;
            baseInfo4.fullWifiLockTime = baseInfo5.fullWifiLockTime;
            baseInfo4.gpsSensorHoldTime = baseInfo5.gpsSensorHoldTime;
            baseInfo4.totalFull = baseInfo5.totalFull;
            baseInfo4.totalPartial = baseInfo5.totalPartial;
        }
        if (uidStatsInfo != null) {
            this.mPowerCheckerUidStatsResult.put(i2, new PowerCheckerNotifier.UidRecordInfo(uidStatsInfo));
        }
    }

    private void computeWakelock(PowerCheckerNotifier.UidStatsInfo uidStatsInfo, UidStatsData uidStatsData, int i2, long j2) {
        List<UidStatsData.WakeLock> wakeLockList = uidStatsData.getWakeLockList();
        if (wakeLockList.size() <= 0) {
            return;
        }
        ArrayMap<String, PowerCheckerNotifier.Timer> arrayMap = new ArrayMap<>();
        for (UidStatsData.WakeLock wakeLock : wakeLockList) {
            PowerCheckerNotifier.BaseInfo baseInfo = uidStatsInfo.info;
            baseInfo.totalFull += wakeLock.wlFullTime;
            long j3 = baseInfo.totalPartial;
            long j4 = wakeLock.wlPartialTime;
            baseInfo.totalPartial = j3 + j4;
            int i3 = wakeLock.wlPartialCount;
            if (j4 > 0) {
                PowerCheckerNotifier.Timer timer = this.mPartialWakelockStats.get(i2 + RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT + wakeLock.wlName);
                this.mPartialWakelockStats.put(i2 + RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT + uidStatsData.getUid(), new PowerCheckerNotifier.Timer(wakeLock.wlPartialTime, i3));
                if (this.mCurrentRecordStats == 1) {
                    if (timer != null) {
                        computeWakelockResult(i3 - timer.count, wakeLock.wlPartialTime - timer.time, arrayMap, wakeLock.wlName);
                    } else {
                        computeWakelockResult(i3, wakeLock.wlPartialTime, arrayMap, wakeLock.wlName);
                    }
                }
            }
        }
        if (arrayMap.size() > 0) {
            this.mPartialWakelockStatsResult.put(Integer.valueOf(i2), arrayMap);
        }
    }

    private void computeWakelockResult(int i2, long j2, ArrayMap<String, PowerCheckerNotifier.Timer> arrayMap, String str) {
        if (i2 > 10 || j2 > 60000) {
            arrayMap.put(str, new PowerCheckerNotifier.Timer(j2, i2));
        }
    }

    private SubSysCheckItem getCurRPMStatus() {
        if (KernelRpmStatsReader.isSupportReadSubSystemStat()) {
            try {
                KernelRpmStatsReader.readRpmStats();
                SubSysCheckItem subSysCheckItem = new SubSysCheckItem();
                subSysCheckItem.VddMinCnt = KernelRpmStatsReader.getSubsysSleepCnt(KernelRpmStatsReader.RPMSubsys.RPM_VDD_MIN);
                subSysCheckItem.APSSXoCnt = KernelRpmStatsReader.getSubsysSleepCnt(KernelRpmStatsReader.RPMSubsys.RPM_APSS);
                subSysCheckItem.MPSSXoCnt = KernelRpmStatsReader.getSubsysSleepCnt(KernelRpmStatsReader.RPMSubsys.RPM_MPSS);
                subSysCheckItem.ADSPXoCnt = KernelRpmStatsReader.getSubsysSleepCnt(KernelRpmStatsReader.RPMSubsys.RPM_ADSP);
                subSysCheckItem.SLPIXoCnt = KernelRpmStatsReader.getSubsysSleepCnt(KernelRpmStatsReader.RPMSubsys.RPM_SLPI);
                subSysCheckItem.CDSPXoCnt = KernelRpmStatsReader.getSubsysSleepCnt(KernelRpmStatsReader.RPMSubsys.RPM_CDSP);
                return subSysCheckItem;
            } catch (IOException e2) {
                Log.e(TAG, " getCurRPMStatus" + e2);
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isCharging(int i2, int i3) {
        if (i2 == 2) {
            return true;
        }
        return i2 == 5 && (i3 & 15) != 0;
    }

    private void recordAppSource(long j2) {
        SparseArray<UidStatsData> uidStats = BatteryStatsHelper.getUidStats();
        for (int i2 = 0; i2 < uidStats.size(); i2++) {
            int iKeyAt = uidStats.keyAt(i2);
            UidStatsData uidStatsDataValueAt = uidStats.valueAt(i2);
            PowerCheckerNotifier.UidStatsInfo uidStatsInfo = new PowerCheckerNotifier.UidStatsInfo(this.mContext, iKeyAt, uidStatsDataValueAt);
            PowerCheckerNotifier.UidStatsInfo uidStatsInfo2 = new PowerCheckerNotifier.UidStatsInfo(this.mContext, iKeyAt, uidStatsDataValueAt);
            UidStatsData.Timer fgActivityTimer = uidStatsDataValueAt.getFgActivityTimer();
            if (fgActivityTimer != null) {
                PowerCheckerNotifier.BaseInfo baseInfo = uidStatsInfo.info;
                baseInfo.totalFgTimeMs = fgActivityTimer.totalTime;
                baseInfo.totalFgTimes = fgActivityTimer.count;
            }
            PowerCheckerNotifier.BaseInfo baseInfo2 = uidStatsInfo.info;
            if (baseInfo2.totalFgTimeMs < 0) {
                baseInfo2.totalFgTimeMs = 0L;
            }
            if (baseInfo2.totalFgTimes < 0) {
                baseInfo2.totalFgTimes = 0;
            }
            computeWakelock(uidStatsInfo, uidStatsDataValueAt, iKeyAt, j2);
            computeSyncStats(uidStatsDataValueAt, iKeyAt, j2);
            computeJobStatsResult(uidStatsDataValueAt, iKeyAt, j2);
            computeSensorState(uidStatsDataValueAt.getSensorTimer(), uidStatsInfo, iKeyAt, j2);
            uidStatsInfo.info.fullWifiLockTime = uidStatsDataValueAt.getFullWifiLockTime();
            uidStatsInfo.info.wifiScanTime = uidStatsDataValueAt.getWifiScanTime();
            try {
                uidStatsInfo.info.totalUserCpuTime = uidStatsDataValueAt.getUserCpuTimeUs() / 1000;
                uidStatsInfo.info.totalSystemCpuTime = uidStatsDataValueAt.getSystemCpuTimeUs() / 1000;
            } catch (Exception e2) {
                Log.e(TAG, "extract total cpu time stats info", e2);
            }
            computeAlarmStats(uidStatsDataValueAt, iKeyAt, uidStatsInfo);
            try {
                uidStatsInfo.info.mobileActiveTime = uidStatsDataValueAt.getRadioActiveTimer().totalTime;
                uidStatsInfo.info.mobileActiveCount = uidStatsDataValueAt.getRadioActiveTimer().count;
                uidStatsInfo.info.mobileRxBytes = uidStatsDataValueAt.getNetworkActivityBytes(0);
                uidStatsInfo.info.mobileTxBytes = uidStatsDataValueAt.getNetworkActivityBytes(1);
                uidStatsInfo.info.wifiRxBytes = uidStatsDataValueAt.getNetworkActivityBytes(2);
                uidStatsInfo.info.wifiTxBytes = uidStatsDataValueAt.getNetworkActivityBytes(3);
                uidStatsInfo.info.mobileRxPackets = uidStatsDataValueAt.getNetworkActivityPackets(0);
                uidStatsInfo.info.mobileTxPackets = uidStatsDataValueAt.getNetworkActivityPackets(1);
                uidStatsInfo.info.wifiRxPackets = uidStatsDataValueAt.getNetworkActivityPackets(2);
                uidStatsInfo.info.wifiTxPackets = uidStatsDataValueAt.getNetworkActivityPackets(3);
            } catch (Exception e3) {
                Log.e(TAG, "extract mobile radio active stats info", e3);
            }
            PowerCheckerNotifier.UidRecordInfo uidRecordInfo = this.mPowerCheckerUidStats.get(iKeyAt);
            this.mPowerCheckerUidStats.put(iKeyAt, new PowerCheckerNotifier.UidRecordInfo(uidStatsInfo));
            computeUidSource(iKeyAt, uidStatsInfo2, uidStatsInfo, uidRecordInfo);
        }
    }

    private BatteryUsageStats recordBatteryStats() {
        IBatteryStats batteryStats = BatteryStatsHelper.getBatteryStats();
        if (batteryStats == null) {
            return null;
        }
        long jUptimeMillis = SystemClock.uptimeMillis() * 1000;
        long jElapsedRealtime = SystemClock.elapsedRealtime() * 1000;
        BatteryUsageStats batteryUsageStats = new BatteryUsageStats();
        try {
            long batteryRealtime = batteryStats.getBatteryRealtime(jElapsedRealtime);
            recordTotalSource(batteryStats, jElapsedRealtime, jUptimeMillis, batteryUsageStats);
            recordKernelWakelock(batteryStats, batteryRealtime);
            recordKernelWakeupReason(batteryStats, batteryRealtime);
            recordBatteryEstimated(batteryStats, batteryUsageStats);
            recordAppSource(batteryRealtime);
            updateSubSysAwakeRecognition();
        } catch (RemoteException e2) {
            Log.e(TAG, e2.getMessage());
        }
        return batteryUsageStats;
    }

    private void recordKernelWakelock(IBatteryStats iBatteryStats, long j2) {
        Parcel parcelObtain = Parcel.obtain();
        try {
            try {
                byte[] kernelWakelockStats = iBatteryStats.getKernelWakelockStats();
                if (kernelWakelockStats != null) {
                    parcelObtain.unmarshall(kernelWakelockStats, 0, kernelWakelockStats.length);
                    parcelObtain.setDataPosition(0);
                    int i2 = parcelObtain.readInt();
                    for (int i3 = 0; i3 < i2; i3++) {
                        String string = parcelObtain.readString();
                        if (string != null && !string.equals("PowerManagerService.WakeLocks") && !string.equals("PowerManagerService.Display") && !string.equals("PowerManagerService.Broadcasts")) {
                            int i4 = parcelObtain.readInt();
                            long j3 = parcelObtain.readLong();
                            if (j3 > 0) {
                                PowerCheckerNotifier.Timer timer = new PowerCheckerNotifier.Timer(j3, i4);
                                PowerCheckerNotifier.Timer timer2 = this.mKernelWakelockStats.get(string);
                                this.mKernelWakelockStats.put(string, timer);
                                if (this.mCurrentRecordStats == 1) {
                                    if (timer2 != null) {
                                        computeKernelWakelockStats(timer.time - timer2.time, timer.count - timer2.count, string);
                                    } else {
                                        computeKernelWakelockStats(timer.time, timer.count, string);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e2) {
                Log.e(TAG, e2.getMessage());
            }
        } finally {
            parcelObtain.recycle();
        }
    }

    private void recordKernelWakeupReason(IBatteryStats iBatteryStats, long j2) {
        Parcel parcelObtain = Parcel.obtain();
        try {
            try {
                byte[] wakeupReasonStats = iBatteryStats.getWakeupReasonStats();
                if (wakeupReasonStats != null) {
                    parcelObtain.unmarshall(wakeupReasonStats, 0, wakeupReasonStats.length);
                    parcelObtain.setDataPosition(0);
                    int i2 = parcelObtain.readInt();
                    for (int i3 = 0; i3 < i2; i3++) {
                        String string = parcelObtain.readString();
                        int i4 = parcelObtain.readInt();
                        long j3 = parcelObtain.readLong();
                        if (j3 > 0) {
                            PowerCheckerNotifier.Timer timer = this.mKernelWakeupStats.get(string);
                            PowerCheckerNotifier.Timer timer2 = new PowerCheckerNotifier.Timer(j3, i4);
                            this.mKernelWakeupStats.put(string, timer2);
                            if (this.mCurrentRecordStats == 1) {
                                if (timer != null) {
                                    computeKernelWakeUpStats(timer2.time - timer.time, timer2.count - timer.count, string);
                                } else {
                                    computeKernelWakeUpStats(timer2.time, timer2.count, string);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e2) {
                Log.e(TAG, e2.getMessage());
            }
        } finally {
            parcelObtain.recycle();
        }
    }

    private BatteryUsageStats recordTotalSource(IBatteryStats iBatteryStats, long j2, long j3, BatteryUsageStats batteryUsageStats) {
        if (iBatteryStats == null) {
            return batteryUsageStats;
        }
        double computedPower = BatteryStatsHelper.getComputedPower();
        double maxDrainedPower = (BatteryStatsHelper.getMaxDrainedPower() + BatteryStatsHelper.getMinDrainedPower()) / 2.0d;
        try {
            batteryUsageStats.mBatteryRealTime = iBatteryStats.computeBatteryRealtime(j2, WHICH) / 1000;
            if (batteryUsageStats.mBatteryRealTime != 0 && j2 != 0) {
                batteryUsageStats.mBatteryUpTime = iBatteryStats.computeBatteryUptime(j2, WHICH) / 1000;
                batteryUsageStats.mBatteryScreenOffRealTimeInc = iBatteryStats.computeBatteryScreenOffRealtime(j2, WHICH) / 1000;
                batteryUsageStats.mBatteryScreenOffUpTimeInc = iBatteryStats.computeBatteryScreenOffUptime(j3, WHICH) / 1000;
                batteryUsageStats.mMobileRadioActiveTime = iBatteryStats.getMobileRadioActiveTime(j2, WHICH) / 1000;
                batteryUsageStats.mMobileRxTotalBytes = iBatteryStats.getNetworkActivityBytes(0, WHICH);
                batteryUsageStats.mMobileTxTotalBytes = iBatteryStats.getNetworkActivityBytes(1, WHICH);
                batteryUsageStats.mWifiRxTotalBytes = iBatteryStats.getNetworkActivityBytes(2, WHICH);
                batteryUsageStats.mWifiTxTotalBytes = iBatteryStats.getNetworkActivityBytes(3, WHICH);
                batteryUsageStats.mMobileRxTotalPackets = iBatteryStats.getNetworkActivityPackets(0, WHICH);
                batteryUsageStats.mMobileTxTotalPackets = iBatteryStats.getNetworkActivityPackets(1, WHICH);
                batteryUsageStats.mWifiRxTotalPackets = iBatteryStats.getNetworkActivityPackets(2, WHICH);
                batteryUsageStats.mWifiTxTotalPackets = iBatteryStats.getNetworkActivityPackets(3, WHICH);
                batteryUsageStats.mComputedPower = computedPower;
                batteryUsageStats.mTotalAveragePower = maxDrainedPower;
                batteryUsageStats.mBatteryLevel = Utils.getCurBatteryLevel(this.mContext);
                return batteryUsageStats;
            }
            return batteryUsageStats;
        } catch (Exception e2) {
            Log.e(TAG, "extract battery stats screen off time", e2);
        }
    }

    private void registerBroadcastandAlarm(int i2) {
        if (i2 == 1) {
            registerAlarm();
        } else {
            unregisterAlarm();
        }
        registerBroadcast();
    }

    private void resetAllResult() {
        this.mKernelWakelockStatsResult.clear();
        this.mKernelWakeupStatsResult.clear();
        this.mPowerCheckerUidStatsResult.clear();
        this.mWakeupAlarmStatsResult.clear();
        this.mPartialWakelockStatsResult.clear();
        this.mSyncStatsResult.clear();
        this.mSensorStatsResult.clear();
        this.mJobStatsResult.clear();
        this.mAppPowerResult.clear();
        this.mAppPackageResult.clear();
        this.mSubSysCheck = -1;
    }

    private void resetAllStats() {
        BatteryUsageStats batteryUsageStats = this.mLastBatteryUsageStats;
        if (batteryUsageStats != null) {
            batteryUsageStats.reset();
        }
        this.mKernelWakeupStats.clear();
        this.mKernelWakelockStats.clear();
        this.mPowerCheckerUidStats.clear();
        this.mLastWakeupAlarmStats.clear();
        this.mPartialWakelockStats.clear();
        this.mSyncStats.clear();
        this.mSensorStats.clear();
        this.mJobStats.clear();
        this.mAppPower.clear();
        this.mRpmItemHistoryStats.clear();
        resetAllResult();
        resetUploadResult();
    }

    private void resetUploadResult() {
        this.mSleepModeResult.clear();
        resetAllResult();
    }

    private JSONObject savePartRecord() {
        this.mCurrentRecordStats = 1;
        BatteryUsageStats batteryUsageStatsRecordBatteryStats = recordBatteryStats();
        if (batteryUsageStatsRecordBatteryStats == null) {
            resetAllResult();
            return null;
        }
        JSONObject json = toJson(batteryUsageStatsRecordBatteryStats.substract(this.mLastBatteryUsageStats));
        resetAllResult();
        this.mLastBatteryUsageStats = batteryUsageStatsRecordBatteryStats;
        return json;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendRecordMessage(int i2, int i3) {
        Message messageObtain = Message.obtain(this.mHandler);
        messageObtain.what = 1;
        messageObtain.arg1 = i2;
        messageObtain.arg2 = i3;
        this.mHandler.sendMessage(messageObtain);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setNextAlarm() {
        if (DEBUG) {
            Log.d(TAG, "setNextAlarm");
        }
        PendingIntent broadcast = PendingIntent.getBroadcast(this.mContext, 0, new Intent("miui.intent.action.powerkeeper_nightstandbyrecord"), 67108864);
        ((AlarmManager) this.mContext.getSystemService("alarm")).setExact(1, getNextAlarmTime(), broadcast);
    }

    private void startFirstRecord() {
        if (this.mCharging) {
            this.mCurrentRecordStats = -1;
        } else {
            this.mCurrentRecordStats = 0;
            this.mLastBatteryUsageStats = recordBatteryStats();
        }
    }

    private JSONObject toAlarmJson(Map.Entry entry) {
        Integer num = (Integer) entry.getValue();
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("tag", entry.getKey());
            jSONObject.put("count", num);
        } catch (JSONException e2) {
            Log.e(TAG, "toJson", e2);
        }
        return jSONObject;
    }

    private JSONObject toAllJson() {
        try {
            JSONArray jSONArray = new JSONArray();
            int i2 = 0;
            while (i2 < this.mCloudConfigureState.length - 1) {
                StringBuilder sb = new StringBuilder();
                sb.append(this.mCloudConfigureState[i2]);
                sb.append(Constants.ACCEPT_TIME_SEPARATOR_SERVER);
                i2++;
                sb.append(this.mCloudConfigureState[i2]);
                jSONArray.put(toSleepModeJson(sb.toString(), this.mSleepModeResult.get(this.mCloudConfigureState[i2])));
            }
            JSONObject jSONObject = new JSONObject();
            if (jSONArray.length() != 0) {
                jSONObject.put(com.xiaomi.onetrack.api.d.L, jSONArray);
            }
            return jSONObject;
        } catch (JSONException e2) {
            Log.e(TAG, "toJson", e2);
            return null;
        }
    }

    private JSONObject toAppSourceJson(Map.Entry entry) {
        PowerCheckerNotifier.Timer timer = (PowerCheckerNotifier.Timer) entry.getValue();
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("tag", entry.getKey());
            jSONObject.put("time", timer.time);
            jSONObject.put("count", timer.count);
        } catch (JSONException e2) {
            Log.e(TAG, "toJson", e2);
        }
        return jSONObject;
    }

    private JSONObject toJson(BatteryUsageStats batteryUsageStats) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(KEY_RECORD_TIME, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())));
            if (batteryUsageStats.mBatteryScreenOffRealTimeInc > 0) {
                jSONObject.put(KEY_TIME_ON_BATTERY_SCREEN_OFF, batteryUsageStats.mBatteryScreenOffRealTimeInc);
            }
            if (batteryUsageStats.mBatteryScreenOffUpTimeInc > 0) {
                jSONObject.put(KEY_TIME_ON_BATTERY_SCREEN_OFF_UPTIME, batteryUsageStats.mBatteryScreenOffUpTimeInc);
            }
            if (batteryUsageStats.mBatteryRealTime > 0) {
                jSONObject.put(KEY_TIME_ON_BATTERY, batteryUsageStats.mBatteryRealTime);
            }
            if (batteryUsageStats.mBatteryUpTime > 0) {
                jSONObject.put(KEY_TIME_ON_BATTERY_UPTIME, batteryUsageStats.mBatteryUpTime);
            }
            if (batteryUsageStats.mBatteryLevel > 0) {
                jSONObject.put(KEY_POWER, batteryUsageStats.mBatteryLevel);
            }
            if (batteryUsageStats.mComputedPower > 0.0d) {
                jSONObject.put(KEY_COMPUTED, PowerCheckerUtils.makemAh(batteryUsageStats.mComputedPower));
            }
            if (batteryUsageStats.mTotalAveragePower > 0.0d) {
                jSONObject.put(KEY_ACTUAL, PowerCheckerUtils.makemAh(batteryUsageStats.mTotalAveragePower));
            }
            if (batteryUsageStats.mUnaccounted > 0.0d) {
                jSONObject.put(KEY_UNACCOUNTED, PowerCheckerUtils.makemAh(batteryUsageStats.mUnaccounted));
            }
            if (batteryUsageStats.mOverCounted > 0.0d) {
                jSONObject.put("ov", PowerCheckerUtils.makemAh(batteryUsageStats.mOverCounted));
            }
            if (batteryUsageStats.mScreenEstimated > 0.0d) {
                jSONObject.put(KEY_SCREEN, PowerCheckerUtils.makemAh(batteryUsageStats.mScreenEstimated));
            }
            if (batteryUsageStats.mCameraEstimated > 0.0d) {
                jSONObject.put(KEY_CAMERA, PowerCheckerUtils.makemAh(batteryUsageStats.mCameraEstimated));
            }
            if (batteryUsageStats.mFlashEstimated > 0.0d) {
                jSONObject.put(KEY_FLASHLIGHT, PowerCheckerUtils.makemAh(batteryUsageStats.mFlashEstimated));
            }
            if (batteryUsageStats.mUserEstimated > 0.0d) {
                jSONObject.put(KEY_USER, PowerCheckerUtils.makemAh(batteryUsageStats.mUserEstimated));
            }
            if (batteryUsageStats.mCellStandbyEstimated > 0.0d) {
                jSONObject.put("cs", PowerCheckerUtils.makemAh(batteryUsageStats.mCellStandbyEstimated));
            }
            if (batteryUsageStats.mPhoneEstimated > 0.0d) {
                jSONObject.put(KEY_PHONECALLS, PowerCheckerUtils.makemAh(batteryUsageStats.mPhoneEstimated));
            }
            if (batteryUsageStats.mWifiEstimated > 0.0d) {
                jSONObject.put(KEY_WIFI, PowerCheckerUtils.makemAh(batteryUsageStats.mWifiEstimated));
            }
            if (batteryUsageStats.mBluetoothEstimated > 0.0d) {
                jSONObject.put(KEY_BLUETOOTH, PowerCheckerUtils.makemAh(batteryUsageStats.mBluetoothEstimated));
            }
            if (batteryUsageStats.mIdleEstimated > 0.0d) {
                jSONObject.put(KEY_IDLE, PowerCheckerUtils.makemAh(batteryUsageStats.mIdleEstimated));
            }
            if (batteryUsageStats.mMobileRadioActiveTime > 0) {
                jSONObject.put(KEY_MOBILE_RADIO_ACTIVE_TIME, batteryUsageStats.mMobileRadioActiveTime);
            }
            if (batteryUsageStats.mMobileRxTotalBytes > 0) {
                jSONObject.put(KEY_TOTAL_RX_MOBILE_NETWORK, batteryUsageStats.mMobileRxTotalBytes);
            }
            if (batteryUsageStats.mMobileTxTotalBytes > 0) {
                jSONObject.put(KEY_TOTAL_TX_MOBILE_NETWORK, batteryUsageStats.mMobileTxTotalBytes);
            }
            if (batteryUsageStats.mMobileRxTotalPackets > 0) {
                jSONObject.put(KEY_TOTAL_RX_MOBILE_NETWORK_PACKAGE, batteryUsageStats.mMobileRxTotalPackets);
            }
            if (batteryUsageStats.mMobileTxTotalPackets > 0) {
                jSONObject.put(KEY_TOTAL_TX_MOBILE_NETWORK_PACKAGE, batteryUsageStats.mMobileTxTotalPackets);
            }
            if (batteryUsageStats.mWifiRxTotalBytes > 0) {
                jSONObject.put(KEY_TOTAL_RX_WIFI_NETWORK, batteryUsageStats.mWifiRxTotalBytes);
            }
            if (batteryUsageStats.mWifiTxTotalBytes > 0) {
                jSONObject.put(KEY_TOTAL_TX_WIFI_NETWORK, batteryUsageStats.mWifiTxTotalBytes);
            }
            if (batteryUsageStats.mWifiRxTotalPackets > 0) {
                jSONObject.put(KEY_TOTAL_RX_WIFI_NETWORK_PACKAGE, batteryUsageStats.mWifiRxTotalPackets);
            }
            if (batteryUsageStats.mWifiTxTotalPackets > 0) {
                jSONObject.put(KEY_TOTAL_TX_WIFI_NETWORK_PACKAGE, batteryUsageStats.mWifiTxTotalPackets);
            }
            ArrayMap<String, PowerCheckerNotifier.Timer> arrayMap = this.mKernelWakelockStatsResult;
            if (arrayMap != null) {
                Iterator<Map.Entry<String, PowerCheckerNotifier.Timer>> it = arrayMap.entrySet().iterator();
                JSONArray jSONArray = new JSONArray();
                while (it.hasNext()) {
                    jSONArray.put(toSourceJson(it.next()));
                }
                if (jSONArray.length() != 0) {
                    jSONObject.put("kwakelock", jSONArray);
                }
            }
            ArrayMap<String, PowerCheckerNotifier.Timer> arrayMap2 = this.mKernelWakeupStatsResult;
            if (arrayMap2 != null) {
                Iterator<Map.Entry<String, PowerCheckerNotifier.Timer>> it2 = arrayMap2.entrySet().iterator();
                JSONArray jSONArray2 = new JSONArray();
                while (it2.hasNext()) {
                    jSONArray2.put(toSourceJson(it2.next()));
                }
                if (jSONArray2.length() != 0) {
                    jSONObject.put("kwakeup", jSONArray2);
                }
            }
            int i2 = this.mSubSysCheck;
            if (i2 >= 0) {
                jSONObject.put(KEY_SUBSYSCHECK, i2);
            }
            JSONArray jSONArray3 = new JSONArray();
            SparseArray<UidStatsData> uidStats = BatteryStatsHelper.getUidStats();
            for (int i3 = 0; i3 < uidStats.size(); i3++) {
                int iKeyAt = uidStats.keyAt(i3);
                if (toUidJson(iKeyAt) != null) {
                    jSONArray3.put(toUidJson(iKeyAt));
                }
            }
            if (jSONArray3.length() != 0) {
                jSONObject.put("romCore", jSONArray3);
            }
        } catch (JSONException e2) {
            Log.e(TAG, e2.getMessage());
        }
        return jSONObject;
    }

    private JSONObject toRecordAllJson() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put(KEY_RECORD_TIME, simpleDateFormat.format(new Date(System.currentTimeMillis())));
            StringBuilder sb = new StringBuilder(1024);
            Iterator<Integer> it = this.mSleepModeBroacast.iterator();
            while (it.hasNext()) {
                sb.append(it.next());
            }
            jSONObject.put(KEY_RECORD_SLEEP_MODE_MESSAGE, sb);
            jSONObject.put(KEY_RECORD_FAIL_REASON_CHARGING, this.mChargingRecordFail);
            jSONObject.put(KEY_RECORD_FAIL_FIRST_CHARGING, this.mFirstModeChargingRecordFail);
            jSONObject.put(KEY_RECORD_FAIL_CLOUD_UPDATE, this.mCloudUpdateRecordFail);
            if (DEBUG) {
                Log.d(TAG, "Alarm toRecordAllJson finishedcloudParam == " + ((Object) sb));
            }
            return jSONObject;
        } catch (JSONException e2) {
            Log.e(TAG, "toJson", e2);
            return null;
        }
    }

    private JSONObject toSleepModeJson(String str, JSONObject jSONObject) {
        JSONObject jSONObject2 = new JSONObject();
        try {
            jSONObject2.put(KEY_SLEEP_STATE, str);
            jSONObject2.put(KEY_RECORD_VALUE, jSONObject);
        } catch (JSONException e2) {
            Log.e(TAG, "toJson", e2);
        }
        return jSONObject2;
    }

    private JSONObject toSourceJson(Map.Entry entry) {
        PowerCheckerNotifier.Timer timer = (PowerCheckerNotifier.Timer) entry.getValue();
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("tag", entry.getKey().toString());
            jSONObject.put("time", timer.time);
            jSONObject.put("count", timer.count);
        } catch (JSONException e2) {
            Log.e(TAG, "toJson", e2);
        }
        return jSONObject;
    }

    private JSONObject toUidJson(int i2) {
        try {
            if (this.mAppPowerResult.get(i2) != null && this.mAppPowerResult.get(i2).doubleValue() > 0.0d) {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("uid", i2);
                jSONObject.put("pkg", this.mAppPackageResult.get(i2));
                jSONObject.put(KEY_USAGE_POWER, PowerCheckerUtils.makemAh(this.mAppPowerResult.get(i2).doubleValue()));
                if (this.mPowerCheckerUidStatsResult.get(i2) == null) {
                    return jSONObject;
                }
                PowerCheckerNotifier.UidRecordInfo uidRecordInfo = this.mPowerCheckerUidStatsResult.get(i2);
                long j2 = uidRecordInfo.info.mobileActiveTime;
                if (j2 > 0) {
                    jSONObject.put(KEY_MOBILE_RADIO_ACTIVE, j2);
                }
                int i3 = uidRecordInfo.info.mobileActiveCount;
                if (i3 > 0) {
                    jSONObject.put(KEY_MOBILE_RADIO_ACTIVE_COUNT, i3);
                }
                long j3 = uidRecordInfo.info.mobileRxBytes;
                if (j3 > 0) {
                    jSONObject.put(KEY_RX_MOBILE_NETWORK, j3);
                }
                long j4 = uidRecordInfo.info.mobileTxBytes;
                if (j4 > 0) {
                    jSONObject.put(KEY_TX_MOBILE_NETWORK, j4);
                }
                long j5 = uidRecordInfo.info.mobileRxPackets;
                if (j5 > 0) {
                    jSONObject.put(KEY_RX_MOBILE_NETWORK_PACKAGE, j5);
                }
                long j6 = uidRecordInfo.info.mobileTxPackets;
                if (j6 > 0) {
                    jSONObject.put(KEY_TX_MOBILE_NETWORK_PACKAGE, j6);
                }
                long j7 = uidRecordInfo.info.wifiRxPackets;
                if (j7 > 0) {
                    jSONObject.put(KEY_RX_WIFI_NETWORK_PACKAGE, j7);
                }
                long j8 = uidRecordInfo.info.wifiTxPackets;
                if (j8 > 0) {
                    jSONObject.put(KEY_TX_WIFI_NETWORK_PACKAGE, j8);
                }
                long j9 = uidRecordInfo.info.wifiRxBytes;
                if (j9 > 0) {
                    jSONObject.put(KEY_RX_WIFI_NETWORK, j9);
                }
                long j10 = uidRecordInfo.info.wifiTxBytes;
                if (j10 > 0) {
                    jSONObject.put(KEY_TX_WIFI_NETWORK, j10);
                }
                long j11 = uidRecordInfo.info.wifiScanTime;
                if (j11 > 0) {
                    jSONObject.put(KEY_WIFI_SCAN_TIME, j11);
                }
                long j12 = uidRecordInfo.info.gpsSensorHoldTime;
                if (j12 > 0) {
                    jSONObject.put("gps", j12);
                }
                int i4 = uidRecordInfo.info.countGPS;
                if (i4 > 0) {
                    jSONObject.put(KEY_SENSOR_GPS_TIME_COUNT, i4);
                }
                long j13 = uidRecordInfo.info.fullWifiLockTime;
                if (j13 > 0) {
                    jSONObject.put(KEY_FULL_WIFI_LOCK_TIME, j13);
                }
                long j14 = uidRecordInfo.info.totalUserCpuTime;
                if (j14 > 0) {
                    jSONObject.put(KEY_TOTAL_USER_CPU_TIME, j14);
                }
                long j15 = uidRecordInfo.info.totalSystemCpuTime;
                if (j15 > 0) {
                    jSONObject.put(KEY_TOTAL_SYSTEM_CPU_TIME, j15);
                }
                long j16 = uidRecordInfo.info.totalFgTimeMs;
                if (j16 > 0) {
                    jSONObject.put(KEY_FOREGROUND_ACTIVITIES, j16);
                }
                int i5 = uidRecordInfo.info.totalFgTimes;
                if (i5 > 0) {
                    jSONObject.put(KEY_FOREGROUND_ACTIVITIES_COUNT, i5);
                }
                long j17 = uidRecordInfo.info.totalPartial;
                if (j17 > 0) {
                    jSONObject.put(KEY_PARTIAL_WAKELOCK_TIME, j17);
                }
                long j18 = uidRecordInfo.info.totalFull;
                if (j18 > 0) {
                    jSONObject.put(KEY_FULL_WAKELOCK_TIME, j18);
                }
                if (this.mSyncStatsResult.get(Integer.valueOf(i2)) != null) {
                    Iterator<Map.Entry<String, PowerCheckerNotifier.Timer>> it = this.mSyncStatsResult.get(Integer.valueOf(i2)).entrySet().iterator();
                    JSONArray jSONArray = new JSONArray();
                    while (it.hasNext()) {
                        jSONArray.put(toAppSourceJson(it.next()));
                    }
                    if (jSONArray.length() != 0) {
                        jSONObject.put(PowerCheckerProcessNotifier.KEY_SYNC, jSONArray);
                    }
                }
                if (this.mJobStatsResult.get(Integer.valueOf(i2)) != null) {
                    Iterator<Map.Entry<String, PowerCheckerNotifier.Timer>> it2 = this.mJobStatsResult.get(Integer.valueOf(i2)).entrySet().iterator();
                    JSONArray jSONArray2 = new JSONArray();
                    while (it2.hasNext()) {
                        jSONArray2.put(toAppSourceJson(it2.next()));
                    }
                    if (jSONArray2.length() != 0) {
                        jSONObject.put("job", jSONArray2);
                    }
                }
                if (this.mPartialWakelockStatsResult.get(Integer.valueOf(i2)) != null) {
                    Iterator<Map.Entry<String, PowerCheckerNotifier.Timer>> it3 = this.mPartialWakelockStatsResult.get(Integer.valueOf(i2)).entrySet().iterator();
                    JSONArray jSONArray3 = new JSONArray();
                    while (it3.hasNext()) {
                        jSONArray3.put(toAppSourceJson(it3.next()));
                    }
                    if (jSONArray3.length() != 0) {
                        jSONObject.put(LocalUpdateUtils.WAKELOCK, jSONArray3);
                    }
                }
                if (this.mSensorStatsResult.get(Integer.valueOf(i2)) != null) {
                    Iterator<Map.Entry<Integer, PowerCheckerNotifier.Timer>> it4 = this.mSensorStatsResult.get(Integer.valueOf(i2)).entrySet().iterator();
                    JSONArray jSONArray4 = new JSONArray();
                    while (it4.hasNext()) {
                        jSONArray4.put(toAppSourceJson(it4.next()));
                    }
                    if (jSONArray4.length() != 0) {
                        jSONObject.put(HideModeStateMachineConfig.STRATEGY_SENSOR, jSONArray4);
                    }
                }
                if (this.mWakeupAlarmStatsResult.get(Integer.valueOf(i2)) != null) {
                    Iterator<Map.Entry<String, Integer>> it5 = this.mWakeupAlarmStatsResult.get(Integer.valueOf(i2)).entrySet().iterator();
                    JSONArray jSONArray5 = new JSONArray();
                    while (it5.hasNext()) {
                        jSONArray5.put(toAlarmJson(it5.next()));
                    }
                    if (jSONArray5.length() != 0) {
                        jSONObject.put("alarm", jSONArray5);
                    }
                }
                return jSONObject;
            }
            return null;
        } catch (JSONException e2) {
            Log.e(TAG, "toJson", e2);
            return null;
        }
    }

    private void unregisterBroadcastandAlarm() {
        unregisterBroadcast();
        unregisterAlarm();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCloudFunction() {
        try {
            int i2 = 0;
            boolean z2 = SimpleSettings.Misc.getBoolean(this.mContext, Constant.FUNC_MONITOR_NIGHT_BATTERY_USAGE_CHECK, false);
            if (z2) {
                String[] functionParameter = CloudFunctionConfig.getFunctionParameter(this.mContext, Constant.FUNC_MONITOR_NIGHT_BATTERY_USAGE_CHECK, MONITER_START);
                int[] iArr = new int[functionParameter.length];
                while (i2 < functionParameter.length) {
                    iArr[i2] = Integer.parseInt(functionParameter[i2].trim());
                    i2++;
                }
                i2 = Integer.parseInt(CloudFunctionConfig.getFunctionParameters2(this.mContext, Constant.FUNC_MONITOR_NIGHT_BATTERY_USAGE_CHECK, "0"));
                registerBroadcastandAlarm(i2);
                cloudUpdateChange(iArr);
                this.mCloudConfigureState = iArr;
            } else {
                unregisterBroadcastandAlarm();
            }
            if (DEBUG) {
                Log.d(TAG, "enable = " + z2 + ", setAlarm = " + i2);
            }
        } catch (Exception e2) {
            Log.e(TAG, "get night mode record parameters failed: ", e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateRecordBatteryStats(int i2, int i3) {
        if (DEBUG) {
            Log.d(TAG, "update record battery stats state = " + i2 + ", prestate = " + i3 + ", mCurrentRecordStats = " + this.mCurrentRecordStats);
        }
        int[] iArr = this.mCloudConfigureState;
        if (iArr.length <= 1) {
            return;
        }
        if (i2 == 0 && i3 != 3) {
            this.mCurrentRecordStats = -1;
        }
        if (iArr[0] == i2) {
            startFirstRecord();
            return;
        }
        if (this.mCurrentRecordStats == -1) {
            resetAllStats();
            return;
        }
        if (ArrayUtils.contains(iArr, i2)) {
            int[] iArr2 = this.mCloudConfigureState;
            boolean z2 = iArr2[iArr2.length - 1] != i2;
            this.mSleepModeResult.put(i2, savePartRecord());
            if (z2) {
                return;
            }
            resetAllStats();
        }
    }

    private void updateSubSysAwakeRecognition() {
        SubSysCheckItem curRPMStatus = getCurRPMStatus();
        if (curRPMStatus == null) {
            return;
        }
        boolean z2 = DEBUG;
        if (z2) {
            Log.d(TAG, "cur state is " + curRPMStatus.toString());
        }
        if (this.mRpmItemHistoryStats.size() <= 0) {
            this.mRpmItemHistoryStats.addFirst(curRPMStatus);
            return;
        }
        int i2 = 0;
        SubSysCheckItem subSysCheckItem = this.mRpmItemHistoryStats.get(0);
        if (subSysCheckItem != null && z2) {
            Log.d(TAG, "last state is " + subSysCheckItem.toString());
        }
        if (subSysCheckItem == null) {
            return;
        }
        if (this.mRpmItemHistoryStats.size() >= 30) {
            this.mRpmItemHistoryStats.removeLast();
        }
        this.mRpmItemHistoryStats.addFirst(curRPMStatus);
        int i3 = 1;
        if (this.mCurrentRecordStats == 1 && subSysCheckItem.VddMinCnt == curRPMStatus.VddMinCnt) {
            if (subSysCheckItem.APSSXoCnt == curRPMStatus.APSSXoCnt) {
                i2 = 2;
            } else {
                i3 = 0;
            }
            if (subSysCheckItem.ADSPXoCnt == curRPMStatus.ADSPXoCnt) {
                i2 += 4;
                i3++;
            }
            if (subSysCheckItem.MPSSXoCnt == curRPMStatus.MPSSXoCnt) {
                i2++;
                i3++;
            }
            if (subSysCheckItem.SLPIXoCnt == curRPMStatus.SLPIXoCnt) {
                i2 += 8;
                i3++;
            }
            if (subSysCheckItem.CDSPXoCnt == curRPMStatus.CDSPXoCnt) {
                i2 += 16;
                i3++;
            }
            this.mSubSysCheck = i2;
            if (z2) {
                Log.d(TAG, "awake founded msg " + Integer.valueOf(i2) + " " + i3 + ", sub sys check = " + this.mSubSysCheck);
            }
        }
    }

    public long getNextAlarmTime() {
        Calendar calendar = Calendar.getInstance();
        int i2 = 33 - calendar.get(11);
        if (DEBUG) {
            Log.d(TAG, "current hour: " + calendar.get(11) + ", add interval: " + i2);
        }
        calendar.add(11, i2);
        calendar.set(12, 0);
        calendar.set(13, 0);
        return calendar.getTimeInMillis();
    }

    public void registerAlarm() {
        if (this.mReceiver != null) {
            return;
        }
        this.mReceiver = broadcastReceiver();
        this.mContext.registerReceiver(this.mReceiver, new IntentFilter("miui.intent.action.powerkeeper_nightstandbyrecord"), 2);
        setNextAlarm();
    }

    void registerBroadcast() {
        if (this.mRegisterBroadcast) {
            return;
        }
        PowerKeeperInterface.d dVar = new PowerKeeperInterface.d(this.mHandler, this.mBatteryStatusReceiver);
        this.mBatteryChangedCallback = dVar;
        this.mPowerKeeperManager.registerBatteryChangedListener(dVar);
        this.mContext.registerReceiver(this.mSleepModeReceiver, new IntentFilter("action_sleep_state_changed"), 2);
        this.mRegisterBroadcast = true;
    }

    public void resetRecord() {
        if (DEBUG) {
            Log.d(TAG, "resetRecord");
        }
        this.mCurrentSleepMode = 0;
        this.mCloudUpdateRecordFail = 0;
        this.mChargingRecordFail = 0;
        this.mFirstModeChargingRecordFail = 0;
        this.mSleepModeBroacast.clear();
    }

    public void unregisterAlarm() {
        BroadcastReceiver broadcastReceiver = this.mReceiver;
        if (broadcastReceiver == null) {
            return;
        }
        this.mContext.unregisterReceiver(broadcastReceiver);
        this.mReceiver = null;
    }

    void unregisterBroadcast() {
        if (this.mRegisterBroadcast) {
            if (DEBUG) {
                Log.d(TAG, "un-register sleep mode");
            }
            this.mContext.unregisterReceiver(this.mSleepModeReceiver);
            this.mPowerKeeperManager.unregisterBatteryChangedListener(this.mBatteryChangedCallback);
            this.mRegisterBroadcast = false;
        }
    }

    private void computeJobStatsResult(int i2, long j2, ArrayMap<String, PowerCheckerNotifier.Timer> arrayMap, String str) {
        if (i2 > 10 || j2 > 60000) {
            arrayMap.put(str, new PowerCheckerNotifier.Timer(j2, i2));
        }
    }

    private void recordBatteryEstimated(IBatteryStats iBatteryStats, BatteryUsageStats batteryUsageStats) {
    }
}
