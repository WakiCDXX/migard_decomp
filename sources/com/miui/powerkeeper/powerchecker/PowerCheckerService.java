package com.miui.powerkeeper.powerchecker;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.TrafficStats;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import com.android.internal.app.IBatteryStats;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.batterylife.systeminfo.BSHelper;
import com.miui.powerkeeper.customerpower.CustomerPowerCheck;
import com.miui.powerkeeper.customerpower.CustomerPowerCheckerDatabaseCrud;
import com.miui.powerkeeper.customerpower.CustomerPowerResultBean;
import com.miui.powerkeeper.customerpower.CustomerPowerUtil;
import com.miui.powerkeeper.provider.CustomerPowerCheckerConfigure;
import com.miui.powerkeeper.provider.SimpleSettingKeys;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.utils.KernelRpmStatsReader;
import com.miui.powerkeeper.utils.ShellUtils;
import com.xiaomi.channelcollection.RegisterAppChannelInfoCollectionJob;
import com.xiaomi.push.mpcd.Constants;
import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import miui.process.ForegroundInfo;
import miui.process.ProcessManager;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class PowerCheckerService extends Service {
    private static final long BATTERY_LEVEL_CHANGE_EVENT_DELAY_MS = 10000;
    private static final long BATTERY_LEVEL_CHANGE_TIME_FULL = 600000;
    private static final long CUSTOMER_POWER_CHECKCYCLE_INTERVAL = 7200000;
    private static final int DAY_STORE_ABNORMAL = 7;
    private static final int DAY_STORE_CUSTOMER_ABNORMAL = 168;
    static final int MAX_BATTERY_HISTORY_SIZE = 50;
    private static final int MSG_CHECKER_APP_LIST_CHANGE = 13;
    private static final int MSG_CHECKER_DETECT_PARAM_CHANGE = 12;
    private static final int MSG_CUSTOMER_POWER_RECORD_DELETE_BY_DAY = 11;
    private static final int MSG_POWER_CHECK_AUDIO_STATUS_CHANGE = 7;
    private static final int MSG_POWER_CHECK_BATTERY_CHANGE = 8;
    private static final int MSG_POWER_CHECK_CYCLE_DONE = 2;
    private static final int MSG_POWER_CHECK_CYCLE_START = 1;
    private static final int MSG_POWER_CHECK_FOREGROUND_ACTIVITY_CHANGE = 6;
    private static final int MSG_POWER_CHECK_POWER_CHANGE = 4;
    private static final int MSG_POWER_CHECK_SCREEN_CHANGE = 5;
    private static final int MSG_POWER_CHECK_UPDATE_CONFIG = 3;
    private static final int MSG_POWER_CUSTOMER_CONTROL_CHANGE = 14;
    private static final int MSG_POWER_RECORD_DELETE_BY_DAY = 9;
    private static final int MSG_POWER_RECORD_FOR_CUSTOMER = 10;
    private static final long POWERCHECKER_DATEBASE_CHECK_EVERYDAY = 86400000;
    static final String TAG = "PowerCheckerService";
    private PowerKeeperInterface.j mAppActiveChangedListener;
    private PowerKeeperInterface.d mBatteryChangedCallback;
    private int mBatteryLevel;
    private BatteryUsageAttribute mBatteryUsageAttribute;
    private boolean mBootCompleted;
    private b.a mCloudObserver;
    long mCurrentDataUsageBytesSinceBoot;
    long mCurrentTimeBatteryLevelChanged;
    private CustomerPowerCheckHandler mCustomerPowerCheckHandler;
    private PowerKeeperInterface.f mForegroundActivitiesChangedCallback;
    private HandlerThread mHandlerThread;
    private int mLastCheckBatteryLevel;
    private NightPowerAbnormalChecker mNightPowerAbnormalChecker;
    private NightStandbyRecord mNightStandbyRecord;
    private PowerCheckerBatteryStats mPowerCheckerBatteryStats;
    private PowerCheckerController mPowerCheckerController;
    private PowerCheckerPolicy mPowerCheckerPolicy;
    private PowerCheckerProcessNotifier mPowerCheckerProcessNotifier;
    private PowerCheckerProcessStats mPowerCheckerProcessStats;
    private PowerKeeperManager mPowerKeeperManager;
    private PowerKeeperInterface.e0 mProcessDiedCallback;
    private PowerKeeperInterface.f0 mScreenChangedCallback;
    private volatile boolean mScreenOn;
    private ShutdownPowerChecker mShutdownPowerChecker;
    static final boolean DEBUG = Log.isLoggable("power.checker", 3);
    public static int WHICH = 0;
    private long mLastCustomerCheckRawRealTime = 0;
    private long rawCustomerCheckRealtime = 0;
    private final Object mLock = new Object();
    private long mLastRawRealTime = 0;
    private long mLastBatteryRealTime = 0;
    private long mLastBatteryScreenOffRealTime = 0;
    private long mLastRawUTCTime = 0;
    private long mLastStartClockTime = 0;
    private boolean mPowerCheckerEnabled = false;
    private int mPlatformWakelockContributeLimitRatio = 10;
    private int mDefaultKernelWakelockIntervalLimit = 5000;
    private int mDefaultKernelWakeupIntervalLimit = PowerCheckerCloudConfigure.DEFAULT_KERNEL_WAKEUP_INTERVAL;
    private int mDefaultUidPartialWakelockLimitRatio = 10;
    private int mDefaultUidWifiScanLimitRatio = 10;
    private int mDefaultUidBgGPSTimeLimitRatio = 10;
    private int mDefaultUidBgCPUTimeLimitRatio = 10;
    private int mDefaultAverageWakeupIntervalLimit = PowerCheckerCloudConfigure.DEFAULT_DEFAULT_WAKEUP_ALARM_LIMIT_RATIO;
    private int mDefaultUidMobileRadioActiveLimitRatio = 10;
    private SparseIntArray mUidPartialWakelockLimitRatio = new SparseIntArray();
    private SparseIntArray mUidWifiScanLimitRatio = new SparseIntArray();
    private SparseIntArray mUidBgGPSTimeLimitRatio = new SparseIntArray();
    private SparseIntArray mUidBgCPUTimeLimitRatio = new SparseIntArray();
    private SparseIntArray mUidAverageWakeupIntervalLimit = new SparseIntArray();
    private SparseIntArray mUidMobileRadioActiveLimitRatio = new SparseIntArray();
    private int mPowerCheckCycleInterval = 600000;
    private SparseArray<PowerCheckerNotifier.UidRecordInfo> mPowerCheckerUidStats = new SparseArray<>();
    private ArrayMap<String, PowerCheckerNotifier.Timer> mKernelWakelockStats = new ArrayMap<>();
    private ArrayMap<String, PowerCheckerNotifier.Timer> mKernelWakeupStats = new ArrayMap<>();
    private ArrayMap<String, PowerCheckerNotifier.Timer> mPartialWakelockStats = new ArrayMap<>();
    private ArrayMap<String, PowerCheckerNotifier.Timer> mSyncStats = new ArrayMap<>();
    private ArrayMap<String, PowerCheckerNotifier.CpuTime> mProcCpuTimeStats = new ArrayMap<>();
    private ArrayMap<String, Integer> mPkgWakeupStats = new ArrayMap<>();
    private final LinkedList<PowerCheckerNotifier.BatteryHistoryItem> mBatteryHistoryStats = new LinkedList<>();
    private boolean mPowerCheckerReady = false;
    private boolean mFastCheck = false;
    private int mBatteryStatus = -1;
    private int mBatteryPlugged = -1;
    private boolean mKernelRpmStatsSupported = true;
    private boolean mIsOnlyCheckRpm = true;
    private PowerCheckHandler mHandler;
    private ContentObserver mCheckerObserver = new ContentObserver(this.mHandler) { // from class: com.miui.powerkeeper.powerchecker.PowerCheckerService.1
        @Override // android.database.ContentObserver
        public void onChange(boolean z2) {
            super.onChange(z2);
            Log.d(PowerCheckerService.TAG, "old-cloud on change");
            PowerCheckerService.this.updateCheckerData();
        }
    };
    private PowerKeeperInterface.r mProcessObserver = new PowerKeeperInterface.r() { // from class: com.miui.powerkeeper.powerchecker.PowerCheckerService.5
        @Override // com.miui.powerkeeper.PowerKeeperInterface.r
        public void onForegroundActivitiesChanged(int i2, boolean z2) {
            PowerCheckerService.this.updateForFgUidChangedLocked(i2, z2);
        }
    };
    private PowerKeeperInterface.y mProcessDiedObserver = new PowerKeeperInterface.y() { // from class: com.miui.powerkeeper.powerchecker.PowerCheckerService.6
        @Override // com.miui.powerkeeper.PowerKeeperInterface.y
        public void onProcessDiedListener(int i2, int i3) {
            PowerCheckerService.this.mPowerCheckerController.notifyProcessDied(i2, i3);
        }
    };
    private PowerKeeperInterface.m mBatteryStatusReceiver = new PowerKeeperInterface.m() { // from class: com.miui.powerkeeper.powerchecker.PowerCheckerService.7
        @Override // com.miui.powerkeeper.PowerKeeperInterface.m
        public void onBatteryChanged(int i2, int i3, int i4, int i5, int i6, int i7) {
            Log.d(PowerCheckerService.TAG, "onBatteryChanged, mBatteryLevel = " + PowerCheckerService.this.mBatteryLevel + ", status = " + i2 + ", level = " + i4 + ", plug = " + i6 + ", scale = " + i7);
            synchronized (PowerCheckerService.this.mLock) {
                try {
                    if (i4 != PowerCheckerService.this.mBatteryLevel) {
                        int i8 = i4 < PowerCheckerService.this.mBatteryLevel ? 1 : 0;
                        PowerCheckerNotifier.BatteryHistoryItem batteryHistoryItem = new PowerCheckerNotifier.BatteryHistoryItem(System.currentTimeMillis());
                        batteryHistoryItem.batteryLevel = i4;
                        batteryHistoryItem.batteryStatus = i2;
                        batteryHistoryItem.batteryPlugType = i6;
                        batteryHistoryItem.screenOn = PowerCheckerService.this.mIsOnlyCheckRpm ? PowerCheckerService.this.mPowerKeeperManager.isScreenOn() : PowerCheckerService.this.mScreenOn;
                        PowerCheckerService powerCheckerService = PowerCheckerService.this;
                        powerCheckerService.mCurrentDataUsageBytesSinceBoot = powerCheckerService.getTotalBytes();
                        PowerCheckerService.this.mCurrentTimeBatteryLevelChanged = System.currentTimeMillis();
                        PowerCheckerService.this.mHandler.sendMessageDelayed(Message.obtain(PowerCheckerService.this.mHandler, 8, i8, 0, batteryHistoryItem), 10000L);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
            if (PowerCheckerService.this.mIsOnlyCheckRpm) {
                PowerCheckerService.this.mBatteryLevel = i4;
                return;
            }
            if (i2 != PowerCheckerService.this.mBatteryStatus || i6 != PowerCheckerService.this.mBatteryPlugged) {
                PowerCheckerService powerCheckerService2 = PowerCheckerService.this;
                boolean zIsCharging = powerCheckerService2.isCharging(powerCheckerService2.mBatteryStatus, PowerCheckerService.this.mBatteryPlugged);
                boolean zIsCharging2 = PowerCheckerService.this.isCharging(i2, i6);
                if (PowerCheckerService.this.mBatteryStatus == -1 || zIsCharging != zIsCharging2) {
                    PowerCheckerService.this.updateForPowerChangedLocked(zIsCharging2);
                    if (i4 >= 90 && !zIsCharging2) {
                        PowerCheckerService.this.logd("battery level over 90 and is not charging, then send message ");
                        if (PowerCheckerService.this.mHandler.hasMessages(9)) {
                            PowerCheckerService.this.mHandler.removeMessages(9);
                        }
                        PowerCheckerService.this.mHandler.sendMessageDelayed(Message.obtain(PowerCheckerService.this.mHandler, 9), 600000L);
                    }
                }
            }
            boolean customerPower = PowerCheckerService.this.mPowerCheckerPolicy.getCustomerPower();
            PowerCheckerService.this.logd("CustomerPower check switch is : " + customerPower);
            if (customerPower && i4 != PowerCheckerService.this.mBatteryLevel) {
                PowerCheckerService powerCheckerService3 = PowerCheckerService.this;
                powerCheckerService3.isCharging(powerCheckerService3.mBatteryStatus, PowerCheckerService.this.mBatteryPlugged);
                boolean zIsCharging3 = PowerCheckerService.this.isCharging(i2, i6);
                if (i4 >= 90 && zIsCharging3) {
                    PowerCheckerService.this.rawCustomerCheckRealtime = SystemClock.elapsedRealtime();
                    PowerCheckerService.this.logd("mLastCustomerCheckRawRealTime : " + PowerCheckerService.this.mLastCustomerCheckRawRealTime + ", rawCustomerCheckRealtime: " + PowerCheckerService.this.rawCustomerCheckRealtime + ", CUSTOMER_POWER_CHECKCYCLE_INTERVAL: 7200000");
                    if (PowerCheckerService.this.mLastCustomerCheckRawRealTime == 0 || PowerCheckerService.this.rawCustomerCheckRealtime - PowerCheckerService.this.mLastCustomerCheckRawRealTime >= 7200000) {
                        PowerCheckerService.this.logd("battery level over 90 and is charging, then send message for customer check");
                        if (PowerCheckerService.this.mCustomerPowerCheckHandler.hasMessages(11)) {
                            PowerCheckerService.this.mCustomerPowerCheckHandler.removeMessages(11);
                        }
                        PowerCheckerService.this.mCustomerPowerCheckHandler.sendMessageDelayed(Message.obtain(PowerCheckerService.this.mCustomerPowerCheckHandler, 11), 600000L);
                        PowerCheckerService.this.mCustomerPowerCheckHandler.sendMessage(Message.obtain(PowerCheckerService.this.mCustomerPowerCheckHandler, 10));
                        PowerCheckerService powerCheckerService4 = PowerCheckerService.this;
                        powerCheckerService4.mLastCustomerCheckRawRealTime = powerCheckerService4.rawCustomerCheckRealtime;
                    } else {
                        PowerCheckerService.this.logd("Time interval is too short since last customer check " + (PowerCheckerService.this.rawCustomerCheckRealtime - PowerCheckerService.this.mLastCustomerCheckRawRealTime) + "ms");
                    }
                }
            }
            PowerCheckerService.this.mBatteryStatus = i2;
            PowerCheckerService.this.mBatteryPlugged = i6;
            PowerCheckerService.this.mBatteryLevel = i4;
        }
    };
    private PowerKeeperInterface.z mScreenReceiver = new PowerKeeperInterface.z() { // from class: com.miui.powerkeeper.powerchecker.PowerCheckerService.8
        @Override // com.miui.powerkeeper.PowerKeeperInterface.z
        public void onScreenChanged(boolean z2) {
            synchronized (PowerCheckerService.this.mLock) {
                try {
                    if (PowerCheckerService.this.mScreenOn != PowerCheckerService.this.mPowerKeeperManager.isScreenOn()) {
                        PowerCheckerService.this.updateForScreenChangedLocked();
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.z
        public void onRegistered(boolean z2) {
        }
    };
    private SparseBooleanArray mUidActiveStatus = new SparseBooleanArray();

    class BatteryStatsTime {
        long batteryRealtime;
        long batteryUptime;
        long rawRealtime;
        long rawUptime;
        long whichBatteryRealtime;
        long whichBatteryUptime;

        public BatteryStatsTime(long j2, long j3, long j4, long j5, long j6, long j7) {
            this.rawUptime = j2;
            this.rawRealtime = j3;
            this.batteryUptime = j4;
            this.batteryRealtime = j5;
            this.whichBatteryUptime = j6;
            this.whichBatteryRealtime = j7;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("rawUptime = " + this.rawUptime + " us\n");
            sb.append("rawRealtime = " + this.rawRealtime + " us\n");
            sb.append("batteryUptime = " + this.batteryUptime + " us\n");
            sb.append("batteryRealtime = " + this.batteryRealtime + " us\n");
            sb.append("whichBatteryUptime = " + this.whichBatteryUptime + " us\n");
            sb.append("whichBatteryRealtime = " + this.whichBatteryRealtime + " us\n");
            return sb.toString();
        }
    }

    private class CustomerPowerCheckHandler extends Handler {
        public CustomerPowerCheckHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i2 = message.what;
            if (i2 != 10) {
                if (i2 != 11) {
                    return;
                }
                PowerCheckerService.this.logd("get message to delete customercheck data before 6 months, process delete");
                CustomerPowerCheckerDatabaseCrud.getInstance(PowerCheckerService.this).deleteByDate(CustomerPowerCheckerConfigure.TABLE, 168);
                PowerCheckerService.this.mCustomerPowerCheckHandler.sendMessageDelayed(Message.obtain(PowerCheckerService.this.mCustomerPowerCheckHandler, 11), 86400000L);
                return;
            }
            PowerCheckerService.this.logd("get message to record power batterystats for customer");
            CustomerPowerCheck customerPowerCheck = new CustomerPowerCheck(PowerCheckerService.this);
            if (customerPowerCheck.mBatteryStats != null) {
                customerPowerCheck.initBatteryInfo();
                ArrayList<CustomerPowerResultBean> arrayListCheckPowerConsume = customerPowerCheck.checkPowerConsume();
                int size = arrayListCheckPowerConsume.size();
                if (size > 0) {
                    for (int i3 = 0; i3 < size; i3++) {
                        CustomerPowerResultBean customerPowerResultBean = arrayListCheckPowerConsume.get(i3);
                        int type = customerPowerResultBean.getType();
                        if (type != 100) {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("type", Integer.valueOf(type));
                            contentValues.put(CustomerPowerCheckerConfigure.Columns.TYPE_NAME, CustomerPowerUtil.getTypeName(type));
                            contentValues.put(CustomerPowerCheckerConfigure.Columns.DETAIL_NAME, customerPowerResultBean.getDetailName());
                            contentValues.put("record_time", Long.valueOf(System.currentTimeMillis()));
                            PowerCheckerService.this.logd("type: " + type + ", typeName: " + CustomerPowerUtil.getTypeName(type) + ", detailName: " + customerPowerResultBean.getDetailName());
                            CustomerPowerCheckerDatabaseCrud.getInstance(PowerCheckerService.this).insert(CustomerPowerCheckerConfigure.TABLE, contentValues);
                        }
                    }
                }
            }
        }
    }

    private class MyBinderService extends IPowerChecker.Stub {
        @Override // com.miui.powerkeeper.powerchecker.IPowerChecker
        public void triggerPowerChecker() {
            if (Binder.getCallingUid() != 1000) {
                Slog.d(PowerCheckerService.TAG, "Only system user can trigger PowerChecker feature");
            } else {
                PowerCheckerService.this.triggerPowerCheckerInternal(false);
            }
        }

        private MyBinderService() {
        }
    }

    private class PowerCheckHandler extends Handler {
        public PowerCheckHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i2 = message.what;
            if (i2 == 1) {
                PowerCheckerService.this.startPowerCheckerCycle();
            }
            if (i2 == 3) {
                Log.d(PowerCheckerService.TAG, "MSG_POWER_CHECK_UPDATE_CONFIG");
                PowerCheckerService.this.mPowerCheckerPolicy.updatePolicy();
                PowerCheckerService.this.updateConfig();
                return;
            }
            if (i2 == 8) {
                PowerCheckerService.this.updateBatteryLevelChanged(message.arg1 == 1, (PowerCheckerNotifier.BatteryHistoryItem) message.obj);
                return;
            }
            if (i2 == 9) {
                PowerCheckerService.this.logd("get message to delete data before 7 days, process delete");
                PowerCheckerDatabaseCrud.getInstance(PowerCheckerService.this).deleteByDate(7);
                PowerCheckerService.this.mHandler.sendMessageDelayed(Message.obtain(PowerCheckerService.this.mHandler, 9), 86400000L);
                return;
            }
            switch (i2) {
                case 12:
                    Log.d(PowerCheckerService.TAG, "MSG_CHECKER_DETECT_PARAM_CHANGE");
                    PowerCheckerService.this.mPowerCheckerPolicy.updatePolicy(b.b.f43y, (JSONObject) message.obj);
                    PowerCheckerService.this.updateConfig();
                    break;
                case 13:
                    Log.d(PowerCheckerService.TAG, "MSG_CHECKER_APP_LIST_CHANGE");
                    PowerCheckerService.this.mPowerCheckerPolicy.updatePolicy(b.b.f42x, (JSONObject) message.obj);
                    PowerCheckerService.this.updateConfig();
                    break;
                case 14:
                    Log.d(PowerCheckerService.TAG, "MSG_POWER_CUSTOMER_CONTROL_CHANGE");
                    PowerCheckerService.this.mPowerCheckerPolicy.updatePolicy(b.b.f41w, (JSONObject) message.obj);
                    PowerCheckerService.this.updateConfig();
                    break;
            }
        }
    }

    private void addBatteryHistoryRecord(PowerCheckerNotifier.BatteryHistoryItem batteryHistoryItem) {
        batteryHistoryItem.active = this.mPowerCheckerPolicy.isNetworkActive(caculateNetSpeed()) || this.mPowerKeeperManager.getActiveController().hasActiveApp(31);
        requestKernelRpmStatsUpdate(batteryHistoryItem);
        batteryHistoryItem.chargeCounter = BSHelper.getCurrentBatteryCap();
        addBatteryHistoryStats(batteryHistoryItem);
    }

    private void addBatteryHistoryStats(PowerCheckerNotifier.BatteryHistoryItem batteryHistoryItem) {
        if (this.mBatteryHistoryStats.size() >= 50) {
            this.mBatteryHistoryStats.removeLast();
        }
        this.mBatteryHistoryStats.addFirst(batteryHistoryItem);
    }

    private double caculateNetSpeed() {
        long totalBytes = getTotalBytes();
        double dCurrentTimeMillis = (System.currentTimeMillis() - this.mCurrentTimeBatteryLevelChanged) / 1000;
        double d2 = totalBytes - this.mCurrentDataUsageBytesSinceBoot;
        double d3 = dCurrentTimeMillis > 0.0d ? d2 / dCurrentTimeMillis : 0.0d;
        logd("caculateNetSpeed, netSpeed=" + d3 + " dataused=" + d2 + " timepassed=" + dCurrentTimeMillis);
        return d3;
    }

    private void checkAppActive() {
        SparseBooleanArray sparseBooleanArrayLoadActiveUid = this.mPowerKeeperManager.getActiveController().loadActiveUid(null, 3);
        int size = sparseBooleanArrayLoadActiveUid.size();
        for (int i2 = 0; i2 < size; i2++) {
            updateForAudioChangedLocked(sparseBooleanArrayLoadActiveUid.keyAt(i2), true);
        }
    }

    private void checkKernelRpmStatsSupported() {
        this.mKernelRpmStatsSupported = KernelRpmStatsReader.isSupportReadSubSystemStat();
        logd("checkKernelRpmStatsSupported: mKernelRpmStatsSupported=" + this.mKernelRpmStatsSupported);
    }

    /* JADX WARN: Code duplicated, block: B:272:0x0c2d A[Catch: Exception -> 0x0bdd, TryCatch #14 {Exception -> 0x0bdd, blocks: (B:261:0x0b8b, B:270:0x0c10, B:272:0x0c2d, B:273:0x0c42, B:275:0x0c48, B:277:0x0c5c, B:278:0x0c60, B:280:0x0c66, B:281:0x0c68, B:284:0x0c75, B:286:0x0c81, B:287:0x0c83, B:289:0x0c89, B:290:0x0c8b, B:296:0x0cac, B:298:0x0cbb, B:300:0x0cd7, B:301:0x0cf4), top: B:516:0x0b8b }] */
    /* JADX WARN: Code duplicated, block: B:273:0x0c42 A[Catch: Exception -> 0x0bdd, TryCatch #14 {Exception -> 0x0bdd, blocks: (B:261:0x0b8b, B:270:0x0c10, B:272:0x0c2d, B:273:0x0c42, B:275:0x0c48, B:277:0x0c5c, B:278:0x0c60, B:280:0x0c66, B:281:0x0c68, B:284:0x0c75, B:286:0x0c81, B:287:0x0c83, B:289:0x0c89, B:290:0x0c8b, B:296:0x0cac, B:298:0x0cbb, B:300:0x0cd7, B:301:0x0cf4), top: B:516:0x0b8b }] */
    /* JADX WARN: Code duplicated, block: B:275:0x0c48 A[Catch: Exception -> 0x0bdd, TryCatch #14 {Exception -> 0x0bdd, blocks: (B:261:0x0b8b, B:270:0x0c10, B:272:0x0c2d, B:273:0x0c42, B:275:0x0c48, B:277:0x0c5c, B:278:0x0c60, B:280:0x0c66, B:281:0x0c68, B:284:0x0c75, B:286:0x0c81, B:287:0x0c83, B:289:0x0c89, B:290:0x0c8b, B:296:0x0cac, B:298:0x0cbb, B:300:0x0cd7, B:301:0x0cf4), top: B:516:0x0b8b }] */
    /* JADX WARN: Code duplicated, block: B:277:0x0c5c A[Catch: Exception -> 0x0bdd, TryCatch #14 {Exception -> 0x0bdd, blocks: (B:261:0x0b8b, B:270:0x0c10, B:272:0x0c2d, B:273:0x0c42, B:275:0x0c48, B:277:0x0c5c, B:278:0x0c60, B:280:0x0c66, B:281:0x0c68, B:284:0x0c75, B:286:0x0c81, B:287:0x0c83, B:289:0x0c89, B:290:0x0c8b, B:296:0x0cac, B:298:0x0cbb, B:300:0x0cd7, B:301:0x0cf4), top: B:516:0x0b8b }] */
    /* JADX WARN: Code duplicated, block: B:280:0x0c66 A[Catch: Exception -> 0x0bdd, TryCatch #14 {Exception -> 0x0bdd, blocks: (B:261:0x0b8b, B:270:0x0c10, B:272:0x0c2d, B:273:0x0c42, B:275:0x0c48, B:277:0x0c5c, B:278:0x0c60, B:280:0x0c66, B:281:0x0c68, B:284:0x0c75, B:286:0x0c81, B:287:0x0c83, B:289:0x0c89, B:290:0x0c8b, B:296:0x0cac, B:298:0x0cbb, B:300:0x0cd7, B:301:0x0cf4), top: B:516:0x0b8b }] */
    /* JADX WARN: Code duplicated, block: B:282:0x0c6e  */
    /* JADX WARN: Code duplicated, block: B:284:0x0c75 A[Catch: Exception -> 0x0bdd, TryCatch #14 {Exception -> 0x0bdd, blocks: (B:261:0x0b8b, B:270:0x0c10, B:272:0x0c2d, B:273:0x0c42, B:275:0x0c48, B:277:0x0c5c, B:278:0x0c60, B:280:0x0c66, B:281:0x0c68, B:284:0x0c75, B:286:0x0c81, B:287:0x0c83, B:289:0x0c89, B:290:0x0c8b, B:296:0x0cac, B:298:0x0cbb, B:300:0x0cd7, B:301:0x0cf4), top: B:516:0x0b8b }] */
    /* JADX WARN: Code duplicated, block: B:286:0x0c81 A[Catch: Exception -> 0x0bdd, TryCatch #14 {Exception -> 0x0bdd, blocks: (B:261:0x0b8b, B:270:0x0c10, B:272:0x0c2d, B:273:0x0c42, B:275:0x0c48, B:277:0x0c5c, B:278:0x0c60, B:280:0x0c66, B:281:0x0c68, B:284:0x0c75, B:286:0x0c81, B:287:0x0c83, B:289:0x0c89, B:290:0x0c8b, B:296:0x0cac, B:298:0x0cbb, B:300:0x0cd7, B:301:0x0cf4), top: B:516:0x0b8b }] */
    /* JADX WARN: Code duplicated, block: B:289:0x0c89 A[Catch: Exception -> 0x0bdd, TryCatch #14 {Exception -> 0x0bdd, blocks: (B:261:0x0b8b, B:270:0x0c10, B:272:0x0c2d, B:273:0x0c42, B:275:0x0c48, B:277:0x0c5c, B:278:0x0c60, B:280:0x0c66, B:281:0x0c68, B:284:0x0c75, B:286:0x0c81, B:287:0x0c83, B:289:0x0c89, B:290:0x0c8b, B:296:0x0cac, B:298:0x0cbb, B:300:0x0cd7, B:301:0x0cf4), top: B:516:0x0b8b }] */
    /* JADX WARN: Code duplicated, block: B:298:0x0cbb A[Catch: Exception -> 0x0bdd, TryCatch #14 {Exception -> 0x0bdd, blocks: (B:261:0x0b8b, B:270:0x0c10, B:272:0x0c2d, B:273:0x0c42, B:275:0x0c48, B:277:0x0c5c, B:278:0x0c60, B:280:0x0c66, B:281:0x0c68, B:284:0x0c75, B:286:0x0c81, B:287:0x0c83, B:289:0x0c89, B:290:0x0c8b, B:296:0x0cac, B:298:0x0cbb, B:300:0x0cd7, B:301:0x0cf4), top: B:516:0x0b8b }] */
    /* JADX WARN: Code duplicated, block: B:300:0x0cd7 A[Catch: Exception -> 0x0bdd, TryCatch #14 {Exception -> 0x0bdd, blocks: (B:261:0x0b8b, B:270:0x0c10, B:272:0x0c2d, B:273:0x0c42, B:275:0x0c48, B:277:0x0c5c, B:278:0x0c60, B:280:0x0c66, B:281:0x0c68, B:284:0x0c75, B:286:0x0c81, B:287:0x0c83, B:289:0x0c89, B:290:0x0c8b, B:296:0x0cac, B:298:0x0cbb, B:300:0x0cd7, B:301:0x0cf4), top: B:516:0x0b8b }] */
    /* JADX WARN: Code duplicated, block: B:301:0x0cf4 A[Catch: Exception -> 0x0bdd, TRY_LEAVE, TryCatch #14 {Exception -> 0x0bdd, blocks: (B:261:0x0b8b, B:270:0x0c10, B:272:0x0c2d, B:273:0x0c42, B:275:0x0c48, B:277:0x0c5c, B:278:0x0c60, B:280:0x0c66, B:281:0x0c68, B:284:0x0c75, B:286:0x0c81, B:287:0x0c83, B:289:0x0c89, B:290:0x0c8b, B:296:0x0cac, B:298:0x0cbb, B:300:0x0cd7, B:301:0x0cf4), top: B:516:0x0b8b }] */
    /* JADX WARN: Code duplicated, block: B:323:0x0d87  */
    /* JADX WARN: Code duplicated, block: B:327:0x0dad A[Catch: Exception -> 0x1094, TryCatch #7 {Exception -> 0x1094, blocks: (B:324:0x0d8a, B:325:0x0da7, B:327:0x0dad, B:330:0x0dbc, B:332:0x0dc4, B:334:0x0dcc, B:337:0x0dd5), top: B:504:0x0d8a }] */
    /* JADX WARN: Code duplicated, block: B:332:0x0dc4 A[Catch: Exception -> 0x1094, TryCatch #7 {Exception -> 0x1094, blocks: (B:324:0x0d8a, B:325:0x0da7, B:327:0x0dad, B:330:0x0dbc, B:332:0x0dc4, B:334:0x0dcc, B:337:0x0dd5), top: B:504:0x0d8a }] */
    /* JADX WARN: Code duplicated, block: B:402:0x10c9 A[Catch: Exception -> 0x104c, TryCatch #3 {Exception -> 0x104c, blocks: (B:3:0x0010, B:383:0x1044, B:389:0x1054, B:399:0x109e, B:400:0x10c3, B:402:0x10c9, B:404:0x10e5, B:408:0x1129, B:410:0x1136, B:412:0x1154, B:413:0x117b, B:419:0x11a6, B:424:0x11c3, B:427:0x11dc, B:429:0x11e6, B:431:0x11ec, B:433:0x1208, B:434:0x1223, B:435:0x1230, B:440:0x1244, B:442:0x1252, B:445:0x1268, B:447:0x126e, B:449:0x1274, B:451:0x127a, B:453:0x1287, B:454:0x128e, B:455:0x1295, B:464:0x12cc, B:481:0x12ec, B:479:0x12e6, B:478:0x12e3, B:480:0x12e7, B:482:0x1316), top: B:497:0x0010, inners: #13 }] */
    /* JADX WARN: Code duplicated, block: B:404:0x10e5 A[Catch: Exception -> 0x104c, TryCatch #3 {Exception -> 0x104c, blocks: (B:3:0x0010, B:383:0x1044, B:389:0x1054, B:399:0x109e, B:400:0x10c3, B:402:0x10c9, B:404:0x10e5, B:408:0x1129, B:410:0x1136, B:412:0x1154, B:413:0x117b, B:419:0x11a6, B:424:0x11c3, B:427:0x11dc, B:429:0x11e6, B:431:0x11ec, B:433:0x1208, B:434:0x1223, B:435:0x1230, B:440:0x1244, B:442:0x1252, B:445:0x1268, B:447:0x126e, B:449:0x1274, B:451:0x127a, B:453:0x1287, B:454:0x128e, B:455:0x1295, B:464:0x12cc, B:481:0x12ec, B:479:0x12e6, B:478:0x12e3, B:480:0x12e7, B:482:0x1316), top: B:497:0x0010, inners: #13 }] */
    /* JADX WARN: Code duplicated, block: B:406:0x1125 A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:417:0x119e  */
    /* JADX WARN: Code duplicated, block: B:421:0x11bb  */
    /* JADX WARN: Code duplicated, block: B:546:0x1230 A[ADDED_TO_REGION, REMOVE, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:573:0x0dbc A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:574:0x0dbb A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:575:0x1098 A[ADDED_TO_REGION, REMOVE, SYNTHETIC] */
    /* JADX WARN: Instruction removed from duplicated block: B:272:0x0c2d, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:300:0x0cd7, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:404:0x10e5, please report this as an issue */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r18v10 */
    /* JADX WARN: Type inference failed for: r18v11 */
    /* JADX WARN: Type inference failed for: r18v22 */
    /* JADX WARN: Type inference failed for: r18v24 */
    /* JADX WARN: Type inference failed for: r18v25 */
    /* JADX WARN: Type inference failed for: r18v26 */
    /* JADX WARN: Type inference failed for: r18v27 */
    /* JADX WARN: Type inference failed for: r18v28 */
    /* JADX WARN: Type inference failed for: r18v29 */
    /* JADX WARN: Type inference failed for: r18v3 */
    /* JADX WARN: Type inference failed for: r18v4 */
    /* JADX WARN: Type inference failed for: r18v5 */
    /* JADX WARN: Type inference failed for: r18v6 */
    /* JADX WARN: Type inference failed for: r18v7 */
    /* JADX WARN: Type inference failed for: r18v8 */
    /* JADX WARN: Type inference failed for: r18v9 */
    /* JADX WARN: Type inference failed for: r1v72, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r28v10 */
    /* JADX WARN: Type inference failed for: r28v11 */
    /* JADX WARN: Type inference failed for: r28v12 */
    /* JADX WARN: Type inference failed for: r28v13 */
    /* JADX WARN: Type inference failed for: r28v2 */
    /* JADX WARN: Type inference failed for: r28v22 */
    /* JADX WARN: Type inference failed for: r28v23 */
    /* JADX WARN: Type inference failed for: r28v24 */
    /* JADX WARN: Type inference failed for: r28v25 */
    /* JADX WARN: Type inference failed for: r28v26 */
    /* JADX WARN: Type inference failed for: r28v3 */
    /* JADX WARN: Type inference failed for: r28v4 */
    /* JADX WARN: Type inference failed for: r28v5 */
    /* JADX WARN: Type inference failed for: r28v7 */
    /* JADX WARN: Type inference failed for: r28v8 */
    /* JADX WARN: Type inference failed for: r29v0 */
    /* JADX WARN: Type inference failed for: r2v1, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r2v12 */
    /* JADX WARN: Type inference failed for: r2v2 */
    /* JADX WARN: Type inference failed for: r2v3 */
    /* JADX WARN: Type inference failed for: r2v45 */
    /* JADX WARN: Type inference failed for: r34v0 */
    /* JADX WARN: Type inference failed for: r34v1 */
    /* JADX WARN: Type inference failed for: r34v3 */
    /* JADX WARN: Type inference failed for: r4v108, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r4v208 */
    /* JADX WARN: Type inference failed for: r4v209 */
    /* JADX WARN: Type inference failed for: r4v210 */
    /* JADX WARN: Type inference failed for: r4v211 */
    /* JADX WARN: Type inference failed for: r4v212 */
    /* JADX WARN: Type inference failed for: r4v213 */
    /* JADX WARN: Type inference failed for: r4v214 */
    /* JADX WARN: Type inference failed for: r4v53, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r4v56, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r4v60, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r4v84 */
    /* JADX WARN: Type inference failed for: r4v90 */
    /* JADX WARN: Type inference failed for: r4v92, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r4v93 */
    /* JADX WARN: Type inference failed for: r4v97 */
    /* JADX WARN: Type inference failed for: r4v98 */
    /* JADX WARN: Type inference failed for: r50v0 */
    /* JADX WARN: Type inference failed for: r50v1 */
    /* JADX WARN: Type inference failed for: r50v10 */
    /* JADX WARN: Type inference failed for: r50v14 */
    /* JADX WARN: Type inference failed for: r50v20 */
    /* JADX WARN: Type inference failed for: r50v9 */
    /* JADX WARN: Type inference failed for: r5v59, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r5v65, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r6v123 */
    /* JADX WARN: Type inference failed for: r6v124 */
    /* JADX WARN: Type inference failed for: r6v125 */
    /* JADX WARN: Type inference failed for: r6v49 */
    /* JADX WARN: Type inference failed for: r6v66 */
    /* JADX WARN: Type inference failed for: r6v67 */
    /* JADX WARN: Type inference failed for: r6v68 */
    /* JADX WARN: Type inference failed for: r6v69 */
    /* JADX WARN: Type inference failed for: r6v73, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r6v74, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r7v0, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r7v1 */
    /* JADX WARN: Type inference failed for: r7v106 */
    /* JADX WARN: Type inference failed for: r7v107 */
    /* JADX WARN: Type inference failed for: r7v140 */
    /* JADX WARN: Type inference failed for: r7v141 */
    /* JADX WARN: Type inference failed for: r7v142 */
    /* JADX WARN: Type inference failed for: r7v143 */
    /* JADX WARN: Type inference failed for: r7v15 */
    /* JADX WARN: Type inference failed for: r7v19 */
    /* JADX WARN: Type inference failed for: r7v2 */
    /* JADX WARN: Type inference failed for: r7v20 */
    /* JADX WARN: Type inference failed for: r7v21 */
    /* JADX WARN: Type inference failed for: r7v29, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r7v3, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r7v30 */
    /* JADX WARN: Type inference failed for: r7v31 */
    /* JADX WARN: Type inference failed for: r7v38 */
    /* JADX WARN: Type inference failed for: r7v5 */
    /* JADX WARN: Type inference failed for: r7v55 */
    /* JADX WARN: Type inference failed for: r7v57 */
    /* JADX WARN: Type inference failed for: r7v6 */
    /* JADX WARN: Type inference failed for: r7v61 */
    /* JADX WARN: Type inference failed for: r7v62 */
    /* JADX WARN: Type inference failed for: r7v63 */
    /* JADX WARN: Type inference failed for: r7v64 */
    /* JADX WARN: Type inference failed for: r7v65 */
    /* JADX WARN: Type inference failed for: r7v66 */
    /* JADX WARN: Type inference failed for: r7v67, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r7v68 */
    /* JADX WARN: Type inference failed for: r7v70 */
    /* JADX WARN: Type inference failed for: r7v76 */
    /* JADX WARN: Type inference failed for: r7v84 */
    /* JADX WARN: Type inference failed for: r8v110 */
    /* JADX WARN: Type inference failed for: r8v111 */
    /* JADX WARN: Type inference failed for: r8v112 */
    /* JADX WARN: Type inference failed for: r8v113 */
    /* JADX WARN: Type inference failed for: r8v114 */
    /* JADX WARN: Type inference failed for: r8v18 */
    /* JADX WARN: Type inference failed for: r8v19 */
    /* JADX WARN: Type inference failed for: r8v20 */
    /* JADX WARN: Type inference failed for: r8v21 */
    /* JADX WARN: Type inference failed for: r8v22 */
    /* JADX WARN: Type inference failed for: r8v23 */
    /* JADX WARN: Type inference failed for: r8v24 */
    /* JADX WARN: Type inference failed for: r8v25, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r8v26 */
    /* JADX WARN: Type inference failed for: r8v60, types: [com.miui.powerkeeper.powerchecker.UidStatsData] */
    /* JADX WARN: Type inference failed for: r8v61 */
    /* JADX WARN: Type inference failed for: r8v62 */
    /* JADX WARN: Type inference failed for: r8v63 */
    /* JADX WARN: Type inference failed for: r8v64 */
    /* JADX WARN: Type inference failed for: r8v66, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r8v67 */
    /* JADX WARN: Type inference failed for: r8v68 */
    /* JADX WARN: Type inference failed for: r8v69, types: [com.miui.powerkeeper.powerchecker.PowerCheckerNotifier$CpuTime, java.lang.Object] */
    /* JADX WARN: Type inference failed for: r8v70 */
    /* JADX WARN: Type inference failed for: r8v71 */
    /* JADX WARN: Type inference failed for: r8v72, types: [int] */
    /* JADX WARN: Type inference failed for: r8v74 */
    /* JADX WARN: Type inference failed for: r8v75 */
    /* JADX WARN: Type inference failed for: r8v78 */
    private boolean doPowerCheck(IBatteryStats iBatteryStats) {
        Exception exc;
        ?? r7;
        String str;
        Exception e2;
        long j2;
        long j3;
        int i2;
        String str2;
        long j4;
        String str3;
        long j5;
        String str4;
        ?? r18;
        ?? r19;
        long j6;
        long j7;
        long j8;
        long j9;
        SparseArray<UidStatsData> sparseArray;
        long j10;
        int i3;
        String str5;
        long j11;
        ?? r8;
        PowerCheckerNotifier.UidRecordInfo uidRecordInfo;
        long j12;
        long j13;
        String str6;
        ?? r28;
        PowerCheckerNotifier.UidStatsInfo uidStatsInfo;
        Iterator<Map.Entry<String, PowerCheckerNotifier.CpuTime>> it;
        int i4;
        PowerCheckerService powerCheckerService;
        String str7;
        PowerCheckerNotifier.UidStatsInfo uidStatsInfo2;
        Iterator<Map.Entry<String, Integer>> it2;
        String key;
        int iIntValue;
        Iterator<Map.Entry<String, Integer>> it3;
        PowerCheckerNotifier.UidStatsInfo uidStatsInfo3;
        String str8;
        String key2;
        Iterator<Map.Entry<String, PowerCheckerNotifier.CpuTime>> it4;
        int i5;
        PowerCheckerNotifier.UidStatsInfo uidStatsInfo4;
        String str9;
        PowerCheckerService powerCheckerService2;
        PowerCheckerNotifier.UidRecordInfo uidRecordInfo2;
        String str10;
        ?? r9;
        ?? r10;
        ?? r4;
        ?? r6;
        PowerCheckerService powerCheckerService3;
        PowerCheckerNotifier.UidRecordInfo uidRecordInfo3;
        String str11;
        ?? r11;
        String str12;
        ?? r5;
        ?? r29;
        long j14;
        int i6;
        long j15;
        PowerCheckerNotifier.AppPowerExceedInfo appPowerExceedInfo;
        PowerCheckerNotifier.AppPowerExceedInfo appPowerExceedInfo2;
        ArrayMap<String, Integer> pkgWakeUpAlarmCount;
        String str13;
        long j16;
        boolean z2;
        long j17;
        int i7;
        long j18;
        long j19;
        long j20;
        boolean z3;
        ?? r12 = TAG;
        try {
            long jUptimeMillis = SystemClock.uptimeMillis() * 1000;
            long jElapsedRealtime = SystemClock.elapsedRealtime() * 1000;
            long batteryUptime = iBatteryStats.getBatteryUptime(jUptimeMillis);
            long batteryRealtime = iBatteryStats.getBatteryRealtime(jElapsedRealtime);
            long jComputeBatteryUptime = iBatteryStats.computeBatteryUptime(jUptimeMillis, WHICH);
            long jComputeBatteryRealtime = iBatteryStats.computeBatteryRealtime(jElapsedRealtime, WHICH);
            long jComputeBatteryScreenOffRealtime = iBatteryStats.computeBatteryScreenOffRealtime(jElapsedRealtime, 0);
            ?? r34 = " ms";
            try {
                BatteryStatsTime batteryStatsTime = new BatteryStatsTime(jUptimeMillis, jElapsedRealtime, batteryUptime, batteryRealtime, jComputeBatteryUptime, jComputeBatteryRealtime);
                String string = batteryStatsTime.toString();
                ?? r2 = TAG;
                try {
                    Log.d(r2, string);
                    LogCatcher logCatcher = LogCatcher.getInstance();
                    StringBuilder sb = new StringBuilder();
                    sb.append("rU=");
                    sb.append(jUptimeMillis);
                    sb.append(" rR=");
                    sb.append(jElapsedRealtime);
                    sb.append(" bU=");
                    sb.append(batteryUptime);
                    sb.append(" bR=");
                    sb.append(batteryRealtime);
                    sb.append(" wBU=");
                    sb.append(jComputeBatteryUptime);
                    sb.append(" wBR=");
                    long j21 = jComputeBatteryRealtime;
                    sb.append(j21);
                    sb.append(" wBSOR=");
                    sb.append(jComputeBatteryScreenOffRealtime);
                    logCatcher.debugLog(sb.toString());
                    long jCurrentTimeMillis = System.currentTimeMillis() * 1000;
                    long startClockTime = iBatteryStats.getStartClockTime() * 1000;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("startClockTime = ");
                    sb2.append(startClockTime);
                    sb2.append(" us mLastStartClockTime = ");
                    PowerCheckerService powerCheckerService4 = this;
                    long j22 = batteryRealtime;
                    sb2.append(powerCheckerService4.mLastStartClockTime);
                    sb2.append(" us mLastRawUTCTime = ");
                    sb2.append(powerCheckerService4.mLastRawUTCTime);
                    sb2.append(" us");
                    Log.d(r2, sb2.toString());
                    long j23 = powerCheckerService4.mLastRawUTCTime;
                    if (j23 != 0) {
                        str = " us";
                        try {
                            long j24 = powerCheckerService4.mLastStartClockTime;
                            if (j24 != 0 && startClockTime != j24 && startClockTime > j23) {
                                Log.w((String) r2, "battery stats must have been reset just now");
                                powerCheckerService4.mLastStartClockTime = startClockTime;
                                LogCatcher.getInstance().debugLog("battery stats reset");
                                reset();
                                return false;
                            }
                        } catch (Exception e3) {
                            e2 = e3;
                            exc = e2;
                            r7 = r2;
                            Log.e(r7, exc.getMessage());
                            return true;
                        }
                    } else {
                        str = " us";
                    }
                    powerCheckerService4.mLastStartClockTime = startClockTime;
                    if (j21 < powerCheckerService4.mLastBatteryRealTime) {
                        Log.w((String) r2, "the device must have been unplugged just now");
                        reset();
                        return false;
                    }
                    if (powerCheckerService4.mLastRawRealTime != 0) {
                        Log.d(r2, "mLastBatteryRealTime = " + powerCheckerService4.mLastBatteryRealTime + " us whichBatteryRealtime = " + j21 + str);
                        long j25 = powerCheckerService4.mLastBatteryRealTime;
                        j2 = j21 - j25;
                        j3 = jComputeBatteryScreenOffRealtime - powerCheckerService4.mLastBatteryScreenOffRealTime;
                        if (j21 == j25 || j2 / 1000 < powerCheckerService4.mPowerCheckCycleInterval) {
                            Log.w((String) r2, "Time on battery is not increased since last check");
                            return false;
                        }
                    } else {
                        j2 = 0;
                        j3 = 0;
                    }
                    LogCatcher logCatcher2 = LogCatcher.getInstance();
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("bRTInc=");
                    sb3.append(j2);
                    String str14 = " LRRT=";
                    sb3.append(str14);
                    sb3.append(powerCheckerService4.mLastRawRealTime);
                    sb3.append(" LBRT=");
                    sb3.append(powerCheckerService4.mLastBatteryRealTime);
                    sb3.append("bSORTInc=");
                    sb3.append(j3);
                    sb3.append(" LBSORT=");
                    sb3.append(powerCheckerService4.mLastBatteryScreenOffRealTime);
                    logCatcher2.debugLog(sb3.toString());
                    powerCheckerService4.mPowerCheckerProcessNotifier.updateBatteryStatsTime(batteryStatsTime, j2);
                    ForegroundInfo foregroundInfo = ProcessManager.getForegroundInfo();
                    String str15 = foregroundInfo == null ? null : foregroundInfo.mForegroundPackageName;
                    int i8 = foregroundInfo == null ? -1 : foregroundInfo.mForegroundUid;
                    Log.d(r2, "foreground, uid = " + i8 + ", app = " + str15);
                    long j26 = (((long) powerCheckerService4.mPlatformWakelockContributeLimitRatio) * j2) / 100;
                    long j27 = j2 / ((long) (powerCheckerService4.mDefaultKernelWakelockIntervalLimit * 1000));
                    ArrayList<PowerCheckerUtils.TimerEntry> kernelWakelockStats = PowerCheckerUtils.getKernelWakelockStats(iBatteryStats);
                    if (kernelWakelockStats.size() > 0) {
                        PowerCheckerUtils.sortTimerEntry(kernelWakelockStats);
                        i2 = i8;
                        str2 = str15;
                        str4 = " wBSOR=";
                        long j28 = 0;
                        int i9 = 0;
                        boolean z4 = false;
                        for (PowerCheckerUtils.TimerEntry timerEntry : kernelWakelockStats) {
                            str14 = str14;
                            j21 = j21;
                            String str16 = timerEntry.mName;
                            if (str16 != null) {
                                i7 = i9;
                                if (str16.equals("PowerManagerService.WakeLocks")) {
                                    StringBuilder sb4 = new StringBuilder();
                                    sb4.append("PowerManagerService.WakeLocks ");
                                    j28 = j28;
                                    sb4.append(timerEntry.mTime);
                                    sb4.append("ms");
                                    powerCheckerService4.logd(sb4.toString());
                                }
                                i9 = i7;
                                j28 = j28;
                            } else {
                                i7 = i9;
                            }
                            String str17 = timerEntry.mName;
                            if (str17 == null || !str17.equals("PowerManagerService.Display")) {
                                String str18 = timerEntry.mName;
                                if (str18 == null || !str18.equals("PowerManagerService.Broadcasts")) {
                                    PowerCheckerNotifier.Timer timer = new PowerCheckerNotifier.Timer(timerEntry.mTime, timerEntry.mCount);
                                    PowerCheckerNotifier.Timer timer2 = powerCheckerService4.mKernelWakelockStats.get(timerEntry.mName);
                                    powerCheckerService4.mKernelWakelockStats.put(timerEntry.mName, timer);
                                    if (j2 <= 0 || timer2 == null) {
                                        j18 = j26;
                                        j19 = j27;
                                        i9 = i7;
                                        j28 = j28;
                                    } else {
                                        int i10 = timer.count - timer2.count;
                                        long j29 = j27;
                                        long j30 = timer.time - timer2.time;
                                        if (j30 > j26 / 1000) {
                                            j20 = j30;
                                            try {
                                                int i11 = (int) ((j30 * 1.0d) / (j26 / 1000));
                                                if (powerCheckerService4.mPowerCheckerPolicy.preFiltered(0, 0, i11, timerEntry.mName, null)) {
                                                    Log.d(r2, "  kernel wakelock skip for pre filtered, priority:" + i11);
                                                    z3 = z4;
                                                } else {
                                                    PowerCheckerNotifier.KWLPowerExceedInfo kWLPowerExceedInfo = new PowerCheckerNotifier.KWLPowerExceedInfo(0, i11, 1, kernelWakelockStats, timerEntry, timer2, 0L, 0);
                                                    kWLPowerExceedInfo.screenOffTotalBytes = powerCheckerService4.mPowerCheckerBatteryStats.getNetworkActivityTotalBytes(1);
                                                    kWLPowerExceedInfo.screenOffTotalAudioOnTimeMs = powerCheckerService4.mPowerCheckerBatteryStats.getAudioOnTime(1);
                                                    powerCheckerService4.mPowerCheckerController.dispatchEvent(kWLPowerExceedInfo);
                                                    z3 = true;
                                                }
                                                j19 = j29;
                                                j18 = j26;
                                            } catch (Exception e4) {
                                                e2 = e4;
                                                exc = e2;
                                                r7 = r2;
                                                Log.e(r7, exc.getMessage());
                                                return true;
                                            }
                                        } else {
                                            j20 = j30;
                                            if (i10 > j29) {
                                                j19 = j29;
                                                j18 = j26;
                                                int i12 = (int) ((((double) i10) * 1.0d) / j19);
                                                if (powerCheckerService4.mPowerCheckerPolicy.preFiltered(0, 0, i12, timerEntry.mName, null)) {
                                                    Log.d(r2, "  kernel wakelock skip for pre filtered, priority:" + i12);
                                                } else {
                                                    PowerCheckerNotifier.KWLPowerExceedInfo kWLPowerExceedInfo2 = new PowerCheckerNotifier.KWLPowerExceedInfo(0, i12, 2, kernelWakelockStats, timerEntry, timer2, 0L, 0);
                                                    kWLPowerExceedInfo2.screenOffTotalBytes = powerCheckerService4.mPowerCheckerBatteryStats.getNetworkActivityTotalBytes(1);
                                                    kWLPowerExceedInfo2.screenOffTotalAudioOnTimeMs = powerCheckerService4.mPowerCheckerBatteryStats.getAudioOnTime(1);
                                                    powerCheckerService4.mPowerCheckerController.dispatchEvent(kWLPowerExceedInfo2);
                                                    z3 = true;
                                                }
                                            } else {
                                                j19 = j29;
                                                j18 = j26;
                                            }
                                            z3 = z4;
                                        }
                                        j28 += j20;
                                        i9 = i7 + i10;
                                        z4 = z3;
                                    }
                                    j27 = j19;
                                    j2 = j2;
                                    j26 = j18;
                                } else {
                                    powerCheckerService4.logd("PowerManagerService.Broadcasts " + timerEntry.mTime + "ms");
                                }
                            } else {
                                powerCheckerService4.logd("PowerManagerService.Display " + timerEntry.mTime + "ms");
                            }
                            i9 = i7;
                            j28 = j28;
                        }
                        long j31 = j28;
                        j4 = j2;
                        str3 = str14;
                        j5 = j21;
                        long j32 = j26;
                        int i13 = i9;
                        powerCheckerService4.logd(" totalKernelWakeLockTimeMsInc = " + j31 + " ms totalKernelWakeLockCountInc = " + i13);
                        if (!z4 && j4 > 0 && j31 > j32 / 1000) {
                            int i14 = (int) ((j31 * 1.0d) / (j32 / 1000));
                            if (powerCheckerService4.mPowerCheckerPolicy.preFiltered(0, 0, i14, null, null)) {
                                Log.d(r2, "  total kernel wakelock skip for pre filtered, priority:" + i14);
                            } else {
                                PowerCheckerNotifier.KWLPowerExceedInfo kWLPowerExceedInfo3 = new PowerCheckerNotifier.KWLPowerExceedInfo(0, i14, 1, kernelWakelockStats, null, null, j31, i13);
                                kWLPowerExceedInfo3.screenOffTotalBytes = powerCheckerService4.mPowerCheckerBatteryStats.getNetworkActivityTotalBytes(1);
                                kWLPowerExceedInfo3.screenOffTotalAudioOnTimeMs = powerCheckerService4.mPowerCheckerBatteryStats.getAudioOnTime(1);
                                powerCheckerService4.mPowerCheckerController.dispatchEvent(kWLPowerExceedInfo3);
                            }
                        }
                    } else {
                        i2 = i8;
                        str2 = str15;
                        j4 = j2;
                        str3 = str14;
                        j5 = j21;
                        str4 = " wBSOR=";
                    }
                    long j33 = (j4 * ((long) powerCheckerService4.mPlatformWakelockContributeLimitRatio)) / 100;
                    long j34 = j4 / ((long) (powerCheckerService4.mDefaultKernelWakeupIntervalLimit * 1000));
                    ArrayList<PowerCheckerUtils.TimerEntry> wakeupReasonStats = PowerCheckerUtils.getWakeupReasonStats(iBatteryStats);
                    if (wakeupReasonStats.size() > 0) {
                        try {
                            PowerCheckerUtils.sortTimerEntry(wakeupReasonStats);
                            Iterator<PowerCheckerUtils.TimerEntry> it5 = wakeupReasonStats.iterator();
                            boolean z5 = false;
                            long j35 = 0;
                            int i15 = 0;
                            r2 = r2;
                            while (it5.hasNext()) {
                                PowerCheckerUtils.TimerEntry next = it5.next();
                                Iterator<PowerCheckerUtils.TimerEntry> it6 = it5;
                                ArrayList<PowerCheckerUtils.TimerEntry> arrayList = kernelWakelockStats;
                                r18 = r2;
                                try {
                                    PowerCheckerNotifier.Timer timer3 = new PowerCheckerNotifier.Timer(next.mTime, next.mCount);
                                    PowerCheckerNotifier.Timer timer4 = powerCheckerService4.mKernelWakeupStats.get(next.mName);
                                    powerCheckerService4.mKernelWakeupStats.put(next.mName, timer3);
                                    if (j4 <= 0 || timer4 == null) {
                                        j6 = j34;
                                        j7 = j33;
                                        z5 = z5;
                                        j35 = j35;
                                    } else {
                                        int i16 = timer3.count - timer4.count;
                                        boolean z6 = z5;
                                        long j36 = j35;
                                        long j37 = timer3.time - timer4.time;
                                        if (i16 > j34) {
                                            j8 = j37;
                                            j7 = j33;
                                            int i17 = (int) ((((double) i16) * 1.0d) / j34);
                                            if (powerCheckerService4.mPowerCheckerPolicy.preFiltered(0, 0, i17, next.mName, null)) {
                                                powerCheckerService4.logd("  kernel wakeup skip for pre filtered, priority:" + i17);
                                            } else {
                                                PowerCheckerNotifier.KWLPowerExceedInfo kWLPowerExceedInfo4 = new PowerCheckerNotifier.KWLPowerExceedInfo(0, i17, 4, wakeupReasonStats, next, timer4, 0L, 0);
                                                kWLPowerExceedInfo4.screenOffTotalBytes = powerCheckerService4.mPowerCheckerBatteryStats.getNetworkActivityTotalBytes(1);
                                                kWLPowerExceedInfo4.screenOffTotalAudioOnTimeMs = powerCheckerService4.mPowerCheckerBatteryStats.getAudioOnTime(1);
                                                powerCheckerService4.mPowerCheckerController.dispatchEvent(kWLPowerExceedInfo4);
                                                z6 = true;
                                            }
                                        } else {
                                            j7 = j33;
                                            j8 = j37;
                                            if (j8 > j7 / 1000) {
                                                j9 = j8;
                                                j6 = j34;
                                                int i18 = (int) ((j9 * 1.0d) / (j7 / 1000));
                                                if (powerCheckerService4.mPowerCheckerPolicy.preFiltered(0, 0, i18, next.mName, null)) {
                                                    powerCheckerService4.logd("  kernel wakeup skip for pre filtered, priority:" + i18);
                                                } else {
                                                    PowerCheckerNotifier.KWLPowerExceedInfo kWLPowerExceedInfo5 = new PowerCheckerNotifier.KWLPowerExceedInfo(0, i18, 3, wakeupReasonStats, next, timer4, 0L, 0);
                                                    kWLPowerExceedInfo5.screenOffTotalBytes = powerCheckerService4.mPowerCheckerBatteryStats.getNetworkActivityTotalBytes(1);
                                                    kWLPowerExceedInfo5.screenOffTotalAudioOnTimeMs = powerCheckerService4.mPowerCheckerBatteryStats.getAudioOnTime(1);
                                                    powerCheckerService4.mPowerCheckerController.dispatchEvent(kWLPowerExceedInfo5);
                                                    z6 = true;
                                                }
                                            }
                                            j35 = j36 + j9;
                                            i15 += i16;
                                            z5 = z6;
                                        }
                                        j9 = j8;
                                        j6 = j34;
                                        j35 = j36 + j9;
                                        i15 += i16;
                                        z5 = z6;
                                    }
                                    it5 = it6;
                                    r2 = r18;
                                    kernelWakelockStats = arrayList;
                                    j34 = j6;
                                    j33 = j7;
                                } catch (Exception e5) {
                                    e = e5;
                                    exc = e;
                                    r7 = r18;
                                    Log.e(r7, exc.getMessage());
                                    return true;
                                }
                            }
                            ArrayList<PowerCheckerUtils.TimerEntry> arrayList2 = kernelWakelockStats;
                            r19 = r2;
                            long j38 = j34;
                            boolean z7 = z5;
                            long j39 = j35;
                            powerCheckerService4.logd(" totalKernelWakeupTimeMsInc = " + j39 + " ms totalKernelWakeupCountInc = " + i15);
                            r18 = r19;
                            if (!z7 && j4 > 0 && i15 > j38) {
                                int i19 = (int) ((((double) i15) * 1.0d) / j38);
                                if (powerCheckerService4.mPowerCheckerPolicy.preFiltered(0, 0, i19, null, null)) {
                                    r18 = r19;
                                    r18 = r19;
                                    powerCheckerService4.logd("  total kernel wakeup skip for pre filtered, priority:" + i19);
                                    r18 = r19;
                                } else {
                                    r18 = r19;
                                    r18 = r19;
                                    PowerCheckerNotifier.KWLPowerExceedInfo kWLPowerExceedInfo6 = new PowerCheckerNotifier.KWLPowerExceedInfo(0, i19, 4, arrayList2, null, null, j39, i15);
                                    kWLPowerExceedInfo6.screenOffTotalBytes = powerCheckerService4.mPowerCheckerBatteryStats.getNetworkActivityTotalBytes(1);
                                    kWLPowerExceedInfo6.screenOffTotalAudioOnTimeMs = powerCheckerService4.mPowerCheckerBatteryStats.getAudioOnTime(1);
                                    powerCheckerService4.mPowerCheckerController.dispatchEvent(kWLPowerExceedInfo6);
                                    r18 = r19;
                                }
                            }
                        } catch (Exception e6) {
                            e = e6;
                            r18 = r2;
                            exc = e;
                            r7 = r18;
                        }
                    } else {
                        r18 = r2;
                    }
                    try {
                        r18 = r19;
                        r18 = r19;
                        r18 = r19;
                        powerCheckerService4.mPowerCheckerController.notifyPowerCheckerStart();
                        powerCheckerService4.mPowerCheckerBatteryStats.updateExternalStats();
                        long j40 = jElapsedRealtime;
                        long jComputeBatteryRealtime2 = powerCheckerService4.mPowerCheckerBatteryStats.computeBatteryRealtime(j40, 1);
                        long jComputeDummyRealtime = powerCheckerService4.mPowerCheckerBatteryStats.computeDummyRealtime(j40, 1);
                        LogCatcher.getInstance().debugLog("bRTSC=" + jComputeBatteryRealtime2 + " dRTSC=" + jComputeDummyRealtime);
                        powerCheckerService4.mPowerCheckerProcessNotifier.updatePowerCheckerBatteryStatsTime(jComputeBatteryRealtime2, jComputeDummyRealtime);
                        powerCheckerService4.mPowerCheckerController.updateBackgroundNetworkActiveUids();
                        SparseArray<UidStatsData> uidStats = BatteryStatsHelper.getUidStats();
                        int i20 = 0;
                        r18 = r18;
                        while (i20 < uidStats.size()) {
                            int iKeyAt = uidStats.keyAt(i20);
                            UidStatsData uidStatsDataValueAt = uidStats.valueAt(i20);
                            PowerCheckerNotifier.UidStatsInfo uidStatsInfo5 = new PowerCheckerNotifier.UidStatsInfo(powerCheckerService4, iKeyAt, uidStatsDataValueAt);
                            UidStatsData.Timer fgActivityTimer = uidStatsDataValueAt.getFgActivityTimer();
                            if (fgActivityTimer != null) {
                                PowerCheckerNotifier.BaseInfo baseInfo = uidStatsInfo5.info;
                                baseInfo.totalFgTimeMs = fgActivityTimer.totalTime;
                                baseInfo.totalFgTimes = fgActivityTimer.count;
                            }
                            StringBuilder sb5 = new StringBuilder();
                            sb5.append("uid:");
                            sb5.append(iKeyAt);
                            sb5.append(" batteryRealtime:");
                            long j41 = j22;
                            sb5.append(j41);
                            sb5.append(" ForegroundActivityTime:");
                            sb5.append(uidStatsInfo5.info.totalFgTimeMs);
                            sb5.append("ms(");
                            sb5.append(uidStatsInfo5.info.totalFgTimes);
                            sb5.append(Constants.SEPARATOR_RIGHT_PARENTESIS);
                            powerCheckerService4.logd(sb5.toString());
                            PowerCheckerNotifier.BaseInfo baseInfo2 = uidStatsInfo5.info;
                            if (baseInfo2.totalFgTimeMs < 0) {
                                baseInfo2.totalFgTimeMs = 0L;
                            }
                            if (baseInfo2.totalFgTimes < 0) {
                                baseInfo2.totalFgTimes = 0;
                            }
                            ArrayMap<String, PowerCheckerNotifier.Timer> arrayMap = new ArrayMap<>();
                            List<UidStatsData.WakeLock> wakeLockList = uidStatsDataValueAt.getWakeLockList();
                            int size = wakeLockList.size();
                            String str19 = RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT;
                            if (size > 0) {
                                Iterator<UidStatsData.WakeLock> it7 = wakeLockList.iterator();
                                boolean z8 = false;
                                while (it7.hasNext()) {
                                    UidStatsData.WakeLock next2 = it7.next();
                                    uidStats = uidStats;
                                    it7 = it7;
                                    if ("ProximitySensor".equals(next2.wlName)) {
                                        powerCheckerService4.logd("we will not record entry.getKey() = " + next2.wlName);
                                    } else {
                                        boolean z9 = z8;
                                        long j42 = j41;
                                        long j43 = next2.wlFullTime;
                                        long j44 = j40;
                                        long j45 = next2.wlPartialTime;
                                        int i21 = i20;
                                        long j46 = next2.wlWindowTime;
                                        PowerCheckerNotifier.BaseInfo baseInfo3 = uidStatsInfo5.info;
                                        String str20 = str19;
                                        baseInfo3.totalFull += j43;
                                        baseInfo3.totalPartial += j45;
                                        baseInfo3.totalWindow += j46;
                                        baseInfo3.totalPartialSinceCharged += j45;
                                        int i22 = next2.wlPartialCount;
                                        if (j45 > 0) {
                                            ArrayMap<String, PowerCheckerNotifier.Timer> arrayMap2 = powerCheckerService4.mPartialWakelockStats;
                                            StringBuilder sb6 = new StringBuilder();
                                            sb6.append(iKeyAt);
                                            str13 = str20;
                                            sb6.append(str13);
                                            sb6.append(next2.wlName);
                                            PowerCheckerNotifier.Timer timer5 = arrayMap2.get(sb6.toString());
                                            if (timer5 != null) {
                                                long j47 = j45 - timer5.time;
                                                int i23 = i22 - timer5.count;
                                                arrayMap.put(next2.wlName, timer5);
                                                StringBuilder sb7 = new StringBuilder();
                                                sb7.append("  batteryRealTimeInc = ");
                                                j17 = j45;
                                                j16 = j4;
                                                sb7.append(j16);
                                                sb7.append(" us dltTime = ");
                                                sb7.append(j47);
                                                sb7.append(" ms , dltCount = ");
                                                sb7.append(i23);
                                                powerCheckerService4.logd(sb7.toString());
                                                if (j16 > 0 && powerCheckerService4.mPowerCheckerPolicy.isPwlTimeCountChecked(j16 / 1000, i23, j47)) {
                                                    z2 = true;
                                                }
                                                powerCheckerService4.mPartialWakelockStats.put(iKeyAt + str13 + next2.wlName, new PowerCheckerNotifier.Timer(j17, i22));
                                            } else {
                                                j17 = j45;
                                                j16 = j4;
                                            }
                                            z2 = z9;
                                            powerCheckerService4.mPartialWakelockStats.put(iKeyAt + str13 + next2.wlName, new PowerCheckerNotifier.Timer(j17, i22));
                                        } else {
                                            str13 = str20;
                                            j16 = j4;
                                            z2 = z9;
                                        }
                                        j4 = j16;
                                        str19 = str13;
                                        j41 = j42;
                                        j40 = j44;
                                        z8 = z2;
                                        i20 = i21;
                                    }
                                }
                                sparseArray = uidStats;
                                j10 = j40;
                                i3 = i20;
                                boolean z10 = z8;
                                j22 = j41;
                                str5 = str19;
                                j11 = j4;
                                r8 = z10;
                            } else {
                                sparseArray = uidStats;
                                j10 = j40;
                                i3 = i20;
                                j22 = j41;
                                str5 = RegisterAppChannelInfoCollectionJob.MIPUSH_CHANNEL_OLD_CONCAT;
                                j11 = j4;
                                r8 = 0;
                            }
                            ?? sb8 = new StringBuilder();
                            sb8.append("  totalPartial = ");
                            sb8.append(uidStatsInfo5.info.totalPartial);
                            sb8.append(" ms totalPartialSinceCharged = ");
                            sb8.append(uidStatsInfo5.info.totalPartialSinceCharged);
                            ?? r13 = r34;
                            sb8.append(r13);
                            powerCheckerService4.logd(sb8.toString());
                            ArrayMap<String, PowerCheckerNotifier.Timer> arrayMap3 = new ArrayMap<>();
                            List<UidStatsData.Timer> syncTimer = uidStatsDataValueAt.getSyncTimer();
                            if (syncTimer.size() > 0) {
                                Iterator<UidStatsData.Timer> it8 = syncTimer.iterator();
                                r8 = r8;
                                while (it8.hasNext()) {
                                    UidStatsData.Timer next3 = it8.next();
                                    long j48 = next3.totalTime;
                                    Iterator<UidStatsData.Timer> it9 = it8;
                                    int i24 = next3.count;
                                    if (j48 > 0) {
                                        PowerCheckerNotifier.Timer timer6 = powerCheckerService4.mSyncStats.get(iKeyAt + str5 + next3.name);
                                        if (timer6 != null) {
                                            arrayMap3.put(next3.name, timer6);
                                        }
                                        powerCheckerService4.mSyncStats.put(iKeyAt + str5 + next3.name, new PowerCheckerNotifier.Timer(j48, i24));
                                    }
                                    it8 = it9;
                                    r8 = r8 == true ? 1 : 0;
                                    arrayMap = arrayMap;
                                    j11 = j11;
                                }
                            }
                            j4 = j11;
                            ArrayMap<String, PowerCheckerNotifier.Timer> arrayMap4 = arrayMap;
                            ?? r210 = r8;
                            uidStatsInfo5.info.fullWifiLockTime = uidStatsDataValueAt.getFullWifiLockTime();
                            uidStatsInfo5.info.wifiScanTime = uidStatsDataValueAt.getWifiScanTime();
                            powerCheckerService4.logd("  wifiScanTime = " + uidStatsInfo5.info.wifiScanTime + r13);
                            SparseArray<UidStatsData.Timer> sensorTimer = uidStatsDataValueAt.getSensorTimer();
                            if (sensorTimer.size() > 0) {
                                r8 = 0;
                                while (r8 < sensorTimer.size()) {
                                    UidStatsData.Timer timerValueAt = sensorTimer.valueAt(r8);
                                    if (sensorTimer.keyAt(r8) == -10000) {
                                        PowerCheckerNotifier.BaseInfo baseInfo4 = uidStatsInfo5.info;
                                        baseInfo4.countGPS = timerValueAt.count;
                                        baseInfo4.gpsSensorHoldTime = timerValueAt.totalTime;
                                    }
                                    r8++;
                                }
                            }
                            powerCheckerService4.logd("  gpsSensorHoldTime = " + uidStatsInfo5.info.gpsSensorHoldTime + r13);
                            uidStatsInfo5.info.totalUserCpuTime = uidStatsDataValueAt.getUserCpuTimeUs() / 1000;
                            uidStatsInfo5.info.totalSystemCpuTime = uidStatsDataValueAt.getSystemCpuTimeUs() / 1000;
                            powerCheckerService4.logd("  totalUserCpuTime = " + uidStatsInfo5.info.totalUserCpuTime + " ms totalSystemCpuTime = " + uidStatsInfo5.info.totalSystemCpuTime + r13);
                            List<UidStatsData.Proc> procList = uidStatsDataValueAt.getProcList();
                            if (procList.size() > 0) {
                                Iterator<UidStatsData.Proc> it10 = procList.iterator();
                                r13 = r13;
                                r8 = r8;
                                while (it10.hasNext()) {
                                    UidStatsData.Proc next4 = it10.next();
                                    long j49 = next4.procUserTime;
                                    long j50 = next4.procSystemTime;
                                    ?? cpuTime = r13;
                                    long j51 = next4.procFgTime;
                                    if (j49 > 0 || j50 > 0 || j51 > 0) {
                                        ArrayMap<String, PowerCheckerNotifier.CpuTime> arrayMap5 = uidStatsInfo5.procCpuTimeStats;
                                        String str21 = next4.procName;
                                        cpuTime = new PowerCheckerNotifier.CpuTime(j49, j50, j51);
                                        arrayMap5.put(str21, (PowerCheckerNotifier.CpuTime) cpuTime);
                                    }
                                    it10 = it10;
                                    r13 = cpuTime;
                                    r8 = cpuTime;
                                }
                            }
                            ?? r50 = r13;
                            if (j5 >= ((long) powerCheckerService4.mPowerCheckCycleInterval) * 6 && (pkgWakeUpAlarmCount = uidStatsDataValueAt.getPkgWakeUpAlarmCount()) != null && pkgWakeUpAlarmCount.size() > 0) {
                                for (Map.Entry<String, Integer> entry : pkgWakeUpAlarmCount.entrySet()) {
                                    try {
                                        Integer value = entry.getValue();
                                        int iIntValue2 = value.intValue();
                                        if (iIntValue2 > 0) {
                                            uidStatsInfo5.pkgWakeupStats.put(entry.getKey(), value);
                                            StringBuilder sb9 = new StringBuilder();
                                            r8 = "  pkg:";
                                            sb9.append("  pkg:");
                                            sb9.append(entry.getKey());
                                            sb9.append("  wakeups = ");
                                            sb9.append(iIntValue2);
                                            powerCheckerService4.logd(sb9.toString());
                                        }
                                    } catch (Exception unused) {
                                    }
                                }
                            }
                            try {
                                uidStatsInfo5.info.mobileActiveTime = uidStatsDataValueAt.getRadioActiveTimer().totalTime;
                                uidStatsInfo5.info.mobileActiveCount = uidStatsDataValueAt.getRadioActiveTimer().count;
                                r8 = uidStatsDataValueAt;
                                uidStatsInfo5.info.mobileRxBytes = r8.getNetworkActivityBytes(0);
                                uidStatsInfo5.info.mobileTxBytes = r8.getNetworkActivityBytes(1);
                                uidStatsInfo5.info.wifiRxBytes = r8.getNetworkActivityBytes(2);
                                uidStatsInfo5.info.wifiTxBytes = r8.getNetworkActivityBytes(3);
                                uidStatsInfo5.info.mobileRxPackets = r8.getNetworkActivityPackets(0);
                                uidStatsInfo5.info.mobileTxPackets = r8.getNetworkActivityPackets(1);
                                uidStatsInfo5.info.wifiRxPackets = r8.getNetworkActivityPackets(2);
                                uidStatsInfo5.info.wifiTxPackets = r8.getNetworkActivityPackets(3);
                                uidStatsInfo5.info.mobileBgRxBytes = r8.getNetworkActivityBytes(6);
                                uidStatsInfo5.info.mobileBgTxBytes = r8.getNetworkActivityBytes(7);
                                uidStatsInfo5.info.wifiBgRxBytes = r8.getNetworkActivityBytes(8);
                                uidStatsInfo5.info.wifiBgTxBytes = r8.getNetworkActivityBytes(9);
                                uidStatsInfo5.info.mobileBgRxPackets = r8.getNetworkActivityPackets(6);
                                uidStatsInfo5.info.mobileBgTxPackets = r8.getNetworkActivityPackets(7);
                                uidStatsInfo5.info.wifiBgRxPackets = r8.getNetworkActivityPackets(8);
                                uidStatsInfo5.info.wifiBgTxPackets = r8.getNetworkActivityPackets(9);
                                r8 = r18;
                            } catch (Exception e7) {
                                ?? r14 = r18;
                                Log.e(r14, "extract mobile radio active stats info", e7);
                                r8 = r14;
                            }
                            try {
                                powerCheckerService4.logd("  mobileActiveTime = " + uidStatsInfo5.info.mobileActiveTime + " ms mobileActiveCount = " + uidStatsInfo5.info.mobileActiveCount);
                                if (powerCheckerService4.mPowerCheckerBatteryStats.hasUidStats(iKeyAt)) {
                                    try {
                                        uidStatsInfo5.info.screenOffBytes = powerCheckerService4.mPowerCheckerBatteryStats.getNetworkActivityBytes(iKeyAt, 1);
                                        uidStatsInfo5.info.backgroundBytes = powerCheckerService4.mPowerCheckerBatteryStats.getBackgroundNetworkActivityBytes(iKeyAt, 1);
                                        uidStatsInfo5.info.fgActivityTimeMs = powerCheckerService4.mPowerCheckerBatteryStats.getForegroundActivityTime(iKeyAt, 1);
                                        uidStatsInfo5.info.fgActivityTimes = powerCheckerService4.mPowerCheckerBatteryStats.getForegroundActivityCount(iKeyAt, 1);
                                        uidStatsInfo5.info.screenOffAudioOnTimeMs = powerCheckerService4.mPowerCheckerBatteryStats.getAudioTurnedOnTime(iKeyAt, 1);
                                        uidStatsInfo5.info.screenOffAudioOnTimes = powerCheckerService4.mPowerCheckerBatteryStats.getAudioTurnedOnCount(iKeyAt, 1);
                                        uidStatsInfo5.info.backgroundAudioOnTimeMs = powerCheckerService4.mPowerCheckerBatteryStats.getBackgroundAudioTurnedOnTime(iKeyAt, 1);
                                        uidStatsInfo5.info.backgroundAudioOnTimes = powerCheckerService4.mPowerCheckerBatteryStats.getBackgroundAudioTurnedOnCount(iKeyAt, 1);
                                        uidRecordInfo = powerCheckerService4.mPowerCheckerUidStats.get(iKeyAt);
                                        powerCheckerService4.mPowerCheckerUidStats.put(iKeyAt, new PowerCheckerNotifier.UidRecordInfo(uidStatsInfo5));
                                        j12 = (((long) powerCheckerService4.mUidPartialWakelockLimitRatio.get(iKeyAt, powerCheckerService4.mDefaultUidPartialWakelockLimitRatio)) * j4) / 100;
                                        if (UserHandle.isApp(iKeyAt) && j4 > 0 && uidRecordInfo != null) {
                                            j15 = uidStatsInfo5.info.totalPartialSinceCharged - uidRecordInfo.info.totalPartialSinceCharged;
                                            if (powerCheckerService4.mPowerCheckerPolicy.preFiltered(1, iKeyAt, 0, null, str2)) {
                                                powerCheckerService4.logd("  partial wakelock skip for pre filtered, uid:" + iKeyAt);
                                            } else if (j15 > j12 / 1000) {
                                                appPowerExceedInfo2 = new PowerCheckerNotifier.AppPowerExceedInfo(1, (int) ((j15 * 1.0d) / (j12 / 1000)), uidStatsInfo5, uidRecordInfo);
                                                if (arrayMap4.size() > 0) {
                                                    appPowerExceedInfo2.lastPartialWakelocks = arrayMap4;
                                                }
                                                if (arrayMap3.size() > 0) {
                                                    appPowerExceedInfo2.lastSyncs = arrayMap3;
                                                }
                                                powerCheckerService4.mPowerCheckerController.dispatchEvent(appPowerExceedInfo2);
                                            } else if (r210 == 1) {
                                                appPowerExceedInfo = new PowerCheckerNotifier.AppPowerExceedInfo(1, 0, uidStatsInfo5, uidRecordInfo);
                                                if (arrayMap4.size() > 0) {
                                                    appPowerExceedInfo.lastPartialWakelocks = arrayMap4;
                                                }
                                                if (arrayMap3.size() > 0) {
                                                    appPowerExceedInfo.lastSyncs = arrayMap3;
                                                }
                                                powerCheckerService4.mPowerCheckerController.dispatchEvent(appPowerExceedInfo);
                                            }
                                        }
                                        j13 = (((long) powerCheckerService4.mUidWifiScanLimitRatio.get(iKeyAt, powerCheckerService4.mDefaultUidWifiScanLimitRatio)) * j4) / 100;
                                        if (UserHandle.isApp(iKeyAt) && j4 > 0 && uidRecordInfo != null) {
                                            j14 = uidStatsInfo5.info.wifiScanTime - uidRecordInfo.info.wifiScanTime;
                                            if (j14 > j13 / 1000) {
                                                i6 = (int) ((j14 * 1.0d) / (j13 / 1000));
                                                if (powerCheckerService4.mPowerCheckerPolicy.preFiltered(2, iKeyAt, i6, null, str2)) {
                                                    powerCheckerService4.logd("  wifi scan skip for pre filtered, uid:" + iKeyAt + " processPriority:" + i6);
                                                } else {
                                                    powerCheckerService4.mPowerCheckerController.dispatchEvent(new PowerCheckerNotifier.AppPowerExceedInfo(2, i6, uidStatsInfo5, uidRecordInfo));
                                                }
                                            }
                                        }
                                        long j52 = (((long) powerCheckerService4.mUidBgGPSTimeLimitRatio.get(iKeyAt, powerCheckerService4.mDefaultUidBgGPSTimeLimitRatio)) * j4) / 100;
                                        str6 = " priority:";
                                        if (UserHandle.isApp(iKeyAt) || j4 <= 0 || uidRecordInfo == null) {
                                            r28 = r8;
                                            uidStatsInfo = uidStatsInfo5;
                                        } else {
                                            try {
                                                PowerCheckerNotifier.BaseInfo baseInfo5 = uidStatsInfo5.info;
                                                long j53 = baseInfo5.gpsSensorHoldTime;
                                                PowerCheckerNotifier.BaseInfo baseInfo6 = uidRecordInfo.info;
                                                long j54 = j53 - baseInfo6.gpsSensorHoldTime;
                                                long j55 = baseInfo5.totalFgTimeMs;
                                                r29 = r8;
                                                uidStatsInfo = uidStatsInfo5;
                                                try {
                                                    long j56 = j55 - baseInfo6.totalFgTimeMs;
                                                    r28 = r29;
                                                    if (j54 > j56 && j54 - j56 > j52 / 1000) {
                                                        int i25 = (int) ((j54 * 1.0d) / (j52 / 1000));
                                                        if (powerCheckerService4.mPowerCheckerPolicy.preFiltered(3, iKeyAt, i25, null, str2)) {
                                                            r28 = r29;
                                                            powerCheckerService4.logd("  bg gps skip for pre filtered, uid:" + iKeyAt + " priority:" + i25);
                                                            r28 = r29;
                                                        } else {
                                                            r28 = r29;
                                                            powerCheckerService4.mPowerCheckerController.dispatchEvent(new PowerCheckerNotifier.AppPowerExceedInfo(3, i25, uidStatsInfo, uidRecordInfo));
                                                            r28 = r29;
                                                        }
                                                    }
                                                } catch (Exception e8) {
                                                    e = e8;
                                                    exc = e;
                                                    r7 = r29;
                                                }
                                            } catch (Exception e9) {
                                                e = e9;
                                                r29 = r8;
                                            }
                                        }
                                        try {
                                            r28 = r29;
                                            long j57 = j4 + 500;
                                            long j58 = ((j57 / 1000) * ((long) powerCheckerService4.mUidBgCPUTimeLimitRatio.get(iKeyAt, powerCheckerService4.mDefaultUidBgCPUTimeLimitRatio))) / 100;
                                            it = uidStatsInfo.procCpuTimeStats.entrySet().iterator();
                                            while (it.hasNext()) {
                                                Map.Entry<String, PowerCheckerNotifier.CpuTime> next5 = it.next();
                                                key2 = next5.getKey();
                                                if (key2 != null) {
                                                    if (!key2.equals("*wakelock*") || key2.equals("*wifi*")) {
                                                        it4 = it;
                                                        r28 = r28;
                                                        r50 = r50;
                                                        it = it4;
                                                    } else if (!key2.equals("(Unknown)")) {
                                                        PowerCheckerNotifier.CpuTime value2 = next5.getValue();
                                                        long j59 = j57;
                                                        long j60 = value2.userTime + value2.systemTime;
                                                        long j61 = value2.foregroundTime;
                                                        if (UserHandle.isApp(iKeyAt)) {
                                                            it4 = it;
                                                            try {
                                                                String str22 = str6;
                                                                PowerCheckerNotifier.CpuTime cpuTime2 = powerCheckerService4.mProcCpuTimeStats.get(iKeyAt + str5 + key2);
                                                                powerCheckerService4.mProcCpuTimeStats.put(iKeyAt + str5 + key2, value2);
                                                                if (j4 <= 0 || uidRecordInfo == null || cpuTime2 == null) {
                                                                    powerCheckerService3 = this;
                                                                    i5 = iKeyAt;
                                                                    uidRecordInfo3 = uidRecordInfo;
                                                                    str11 = str5;
                                                                    r11 = r28;
                                                                    str12 = str22;
                                                                    r5 = r50;
                                                                } else {
                                                                    int i26 = iKeyAt;
                                                                    str11 = str5;
                                                                    long j62 = j58;
                                                                    long j63 = cpuTime2.userTime + cpuTime2.systemTime;
                                                                    long j64 = cpuTime2.foregroundTime;
                                                                    PowerCheckerNotifier.UidRecordInfo uidRecordInfo4 = uidRecordInfo;
                                                                    long j65 = uidStatsInfo.info.totalFgTimeMs - uidRecordInfo.info.totalFgTimeMs;
                                                                    long j66 = j60 - j63;
                                                                    long j67 = j61 - j64;
                                                                    if (j65 <= j67) {
                                                                        j65 = j67;
                                                                    }
                                                                    long j68 = (j65 < 0 || j66 <= j65) ? 0L : j66 - j65;
                                                                    ?? sb10 = new StringBuilder();
                                                                    sb10.append("  proc:");
                                                                    sb10.append(key2);
                                                                    sb10.append("  totalFgTimeMs = ");
                                                                    long j69 = j68;
                                                                    sb10.append(uidStatsInfo.info.totalFgTimeMs);
                                                                    sb10.append(" ms  totalTimeMs = ");
                                                                    sb10.append(j60);
                                                                    sb10.append(" ms  fgTimeMs = ");
                                                                    sb10.append(j61);
                                                                    ?? r15 = r50;
                                                                    sb10.append(r15);
                                                                    String string2 = sb10.toString();
                                                                    r6 = r28;
                                                                    try {
                                                                        Log.d(r6, string2);
                                                                        ?? sb11 = new StringBuilder();
                                                                        sb11.append("  proc:");
                                                                        sb11.append(key2);
                                                                        sb11.append("  lastTotalFgTimeMs = ");
                                                                        uidRecordInfo3 = uidRecordInfo4;
                                                                        sb11.append(uidRecordInfo3.info.totalFgTimeMs);
                                                                        sb11.append(" ms  lastTotalTimeMs = ");
                                                                        sb11.append(j63);
                                                                        sb11.append(" ms  lastFgTimeMs = ");
                                                                        sb11.append(j64);
                                                                        sb11.append(" ms  backgroundTimeMsInc = ");
                                                                        sb11.append(j69);
                                                                        sb11.append(" ms  uidCPUTimeLimitMsInc = ");
                                                                        j58 = j62;
                                                                        sb11.append(j58);
                                                                        sb11.append(r15);
                                                                        Log.d(r6, sb11.toString());
                                                                        if (j69 > j58) {
                                                                            powerCheckerService3 = this;
                                                                            int i27 = (int) ((j69 * 1.0d) / j58);
                                                                            if (powerCheckerService3.mPowerCheckerPolicy.preFiltered(4, i26, i27, key2, str2)) {
                                                                                StringBuilder sb12 = new StringBuilder();
                                                                                sb12.append("bg cpu skip for pre filtered, uid:");
                                                                                i5 = i26;
                                                                                sb12.append(i5);
                                                                                str12 = str22;
                                                                                sb12.append(str12);
                                                                                sb12.append(i27);
                                                                                sb12.append(" proc:");
                                                                                sb12.append(key2);
                                                                                Log.d(r6, sb12.toString());
                                                                                r5 = r15;
                                                                                r11 = r6;
                                                                            } else {
                                                                                str12 = str22;
                                                                                i5 = i26;
                                                                                PowerCheckerNotifier.AppPowerExceedInfo appPowerExceedInfo3 = new PowerCheckerNotifier.AppPowerExceedInfo(4, i27, uidStatsInfo, uidRecordInfo3);
                                                                                appPowerExceedInfo3.proc = key2;
                                                                                appPowerExceedInfo3.cpuTime = value2;
                                                                                appPowerExceedInfo3.lastCpuTime = cpuTime2;
                                                                                powerCheckerService3.mPowerCheckerController.dispatchEvent(appPowerExceedInfo3);
                                                                                r5 = r15;
                                                                                r11 = r6;
                                                                            }
                                                                        } else {
                                                                            powerCheckerService3 = this;
                                                                            str12 = str22;
                                                                            i5 = i26;
                                                                            r5 = r15;
                                                                            r11 = r6;
                                                                        }
                                                                    } catch (Exception e10) {
                                                                        e = e10;
                                                                        exc = e;
                                                                        r7 = r6;
                                                                        Log.e(r7, exc.getMessage());
                                                                        return true;
                                                                    }
                                                                }
                                                                uidStatsInfo4 = uidStatsInfo;
                                                                powerCheckerService2 = powerCheckerService3;
                                                                uidRecordInfo2 = uidRecordInfo3;
                                                                str9 = str11;
                                                                r9 = r11;
                                                                str10 = str12;
                                                                r4 = r5;
                                                            } catch (Exception e11) {
                                                                e = e11;
                                                                r6 = r28;
                                                            }
                                                        } else {
                                                            i5 = iKeyAt;
                                                            it4 = it;
                                                            String str23 = str6;
                                                            String str24 = str5;
                                                            ?? r16 = r50;
                                                            PowerCheckerService powerCheckerService5 = powerCheckerService4;
                                                            PowerCheckerNotifier.UidRecordInfo uidRecordInfo5 = uidRecordInfo;
                                                            ?? r17 = r28;
                                                            try {
                                                                ArrayMap<String, PowerCheckerNotifier.CpuTime> arrayMap6 = powerCheckerService5.mProcCpuTimeStats;
                                                                StringBuilder sb13 = new StringBuilder();
                                                                sb13.append(i5);
                                                                uidStatsInfo4 = uidStatsInfo;
                                                                sb13.append(str24);
                                                                sb13.append(key2);
                                                                PowerCheckerNotifier.CpuTime cpuTime3 = arrayMap6.get(sb13.toString());
                                                                try {
                                                                    powerCheckerService5.mProcCpuTimeStats.put(i5 + str24 + key2, value2);
                                                                    if (j4 <= 0 || uidRecordInfo5 == null || cpuTime3 == null) {
                                                                        str9 = str24;
                                                                        powerCheckerService2 = powerCheckerService5;
                                                                        uidRecordInfo2 = uidRecordInfo5;
                                                                        str10 = str23;
                                                                        uidStatsInfo4 = uidStatsInfo4;
                                                                        r9 = r17;
                                                                        r4 = r16;
                                                                    } else {
                                                                        str9 = str24;
                                                                        long j70 = cpuTime3.userTime + cpuTime3.systemTime;
                                                                        long j71 = j60 - j70;
                                                                        double d2 = (j71 * 1.0d) / j58;
                                                                        powerCheckerService2 = this;
                                                                        powerCheckerService2.logd("system proc:" + key2 + "  lastTotalFgTimeMs = " + uidRecordInfo5.info.totalFgTimeMs + " ms  lastTotalTimeMs = " + j70 + " ms  backgroundTimeMsInc = " + j71 + " ms  uidCPUTimeLimitMsInc = " + j58 + r16);
                                                                        if (j71 > j58) {
                                                                            int i28 = (int) d2;
                                                                            if (powerCheckerService2.mPowerCheckerPolicy.preFiltered(4, i5, i28, key2, str2)) {
                                                                                StringBuilder sb14 = new StringBuilder();
                                                                                sb14.append("system bg cpu skip for pre filtered, uid:");
                                                                                sb14.append(i5);
                                                                                str10 = str23;
                                                                                sb14.append(str10);
                                                                                sb14.append(i28);
                                                                                sb14.append(" proc:");
                                                                                sb14.append(key2);
                                                                                ?? r20 = r17;
                                                                                Log.d(r20, sb14.toString());
                                                                                r10 = r20;
                                                                            } else {
                                                                                r9 = r17;
                                                                                str10 = str23;
                                                                                uidStatsInfo4 = uidStatsInfo4;
                                                                                uidRecordInfo2 = uidRecordInfo5;
                                                                                PowerCheckerNotifier.AppPowerExceedInfo appPowerExceedInfo4 = new PowerCheckerNotifier.AppPowerExceedInfo(4, i28, uidStatsInfo4, uidRecordInfo2);
                                                                                appPowerExceedInfo4.proc = key2;
                                                                                appPowerExceedInfo4.cpuTime = value2;
                                                                                appPowerExceedInfo4.lastCpuTime = cpuTime3;
                                                                                powerCheckerService2.mPowerCheckerController.dispatchEvent(appPowerExceedInfo4);
                                                                                r4 = r16;
                                                                            }
                                                                        } else {
                                                                            r10 = r17;
                                                                            str10 = str23;
                                                                        }
                                                                        uidRecordInfo2 = uidRecordInfo5;
                                                                        r4 = r16;
                                                                        r9 = r10;
                                                                    }
                                                                } catch (Exception e12) {
                                                                    e = e12;
                                                                    r12 = r17;
                                                                }
                                                            } catch (Exception e13) {
                                                                e = e13;
                                                                r12 = r17;
                                                            }
                                                        }
                                                        powerCheckerService4 = powerCheckerService2;
                                                        r28 = r9;
                                                        iKeyAt = i5;
                                                        uidStatsInfo = uidStatsInfo4;
                                                        str5 = str9;
                                                        j57 = j59;
                                                        r50 = r4;
                                                        str6 = str10;
                                                        uidRecordInfo = uidRecordInfo2;
                                                        it = it4;
                                                    }
                                                }
                                            }
                                            PowerCheckerNotifier.UidRecordInfo uidRecordInfo6 = uidRecordInfo;
                                            String str25 = str6;
                                            long j72 = j57;
                                            ?? r21 = r28;
                                            ?? r22 = r50;
                                            i4 = iKeyAt;
                                            powerCheckerService = powerCheckerService4;
                                            str7 = str5;
                                            uidStatsInfo2 = uidStatsInfo;
                                            r34 = r22;
                                            long j73 = (j72 / 1000) / ((long) powerCheckerService.mUidAverageWakeupIntervalLimit.get(i4, powerCheckerService.mDefaultAverageWakeupIntervalLimit));
                                            it2 = uidStatsInfo2.pkgWakeupStats.entrySet().iterator();
                                            while (it2.hasNext()) {
                                                Map.Entry<String, Integer> next6 = it2.next();
                                                key = next6.getKey();
                                                iIntValue = next6.getValue().intValue();
                                                if (UserHandle.isApp(i4)) {
                                                    ArrayMap<String, Integer> arrayMap7 = powerCheckerService.mPkgWakeupStats;
                                                    StringBuilder sb15 = new StringBuilder();
                                                    sb15.append(i4);
                                                    str8 = str7;
                                                    sb15.append(str8);
                                                    sb15.append(key);
                                                    Integer num = arrayMap7.get(sb15.toString());
                                                    it3 = it2;
                                                    powerCheckerService.mPkgWakeupStats.put(i4 + str8 + key, new Integer(iIntValue));
                                                    if (j4 > 0 || uidRecordInfo6 == null || num == null) {
                                                        str7 = str8;
                                                    } else {
                                                        int iIntValue3 = iIntValue - num.intValue();
                                                        PowerCheckerNotifier.UidStatsInfo uidStatsInfo6 = uidStatsInfo2;
                                                        if (iIntValue3 > j73) {
                                                            str7 = str8;
                                                            PowerCheckerNotifier.UidRecordInfo uidRecordInfo7 = uidRecordInfo6;
                                                            int i29 = (int) ((((double) iIntValue3) * 1.0d) / j73);
                                                            if (powerCheckerService.mPowerCheckerPolicy.preFiltered(5, i4, i29, key, str2)) {
                                                                powerCheckerService.logd("  wakeup alarm skip for pre filtered, uid:" + i4 + str25 + i29 + " pkg:" + key);
                                                                uidStatsInfo3 = uidStatsInfo6;
                                                                uidRecordInfo6 = uidRecordInfo7;
                                                            } else {
                                                                uidStatsInfo3 = uidStatsInfo6;
                                                                uidRecordInfo6 = uidRecordInfo7;
                                                                PowerCheckerNotifier.AppPowerExceedInfo appPowerExceedInfo5 = new PowerCheckerNotifier.AppPowerExceedInfo(5, i29, uidStatsInfo3, uidRecordInfo6);
                                                                appPowerExceedInfo5.pkg = key;
                                                                appPowerExceedInfo5.wakeupTimes = iIntValue;
                                                                appPowerExceedInfo5.lastWakeupTimes = num.intValue();
                                                                powerCheckerService.mPowerCheckerController.dispatchEvent(appPowerExceedInfo5);
                                                            }
                                                        } else {
                                                            str7 = str8;
                                                            uidStatsInfo3 = uidStatsInfo6;
                                                        }
                                                    }
                                                    uidStatsInfo2 = uidStatsInfo3;
                                                    it2 = it3;
                                                } else {
                                                    it3 = it2;
                                                }
                                                uidStatsInfo3 = uidStatsInfo2;
                                                uidStatsInfo2 = uidStatsInfo3;
                                                it2 = it3;
                                            }
                                            PowerCheckerNotifier.UidStatsInfo uidStatsInfo7 = uidStatsInfo2;
                                            long j74 = (((long) powerCheckerService.mUidMobileRadioActiveLimitRatio.get(i4, powerCheckerService.mDefaultUidMobileRadioActiveLimitRatio)) * j4) / 100;
                                            if (!UserHandle.isApp(i4) && j4 > 0 && uidRecordInfo6 != null) {
                                                PowerCheckerNotifier.BaseInfo baseInfo7 = uidStatsInfo7.info;
                                                long j75 = baseInfo7.totalFgTimeMs;
                                                PowerCheckerNotifier.BaseInfo baseInfo8 = uidRecordInfo6.info;
                                                long j76 = j75 - baseInfo8.totalFgTimeMs;
                                                PowerCheckerNotifier.UidRecordInfo uidRecordInfo8 = uidRecordInfo6;
                                                long j77 = (baseInfo7.mobileActiveTime - baseInfo8.mobileActiveTime) - j76;
                                                if (j77 < 0) {
                                                    j77 = 0;
                                                }
                                                if (powerCheckerService.mPowerCheckerPolicy.isInBackgroundMainly(j4 / 1000, j76) && j77 > j74 / 1000) {
                                                    int i30 = (int) ((j77 * 1.0d) / (j74 / 1000));
                                                    if (powerCheckerService.mPowerCheckerPolicy.preFiltered(6, i4, i30, null, str2)) {
                                                        powerCheckerService.logd("  mobile radio skip for pre filtered, uid:" + i4 + str25 + i30);
                                                    } else {
                                                        powerCheckerService.mPowerCheckerController.dispatchEvent(new PowerCheckerNotifier.AppPowerExceedInfo(6, i30, uidStatsInfo7, uidRecordInfo8));
                                                    }
                                                }
                                            }
                                            i20 = i3 + 1;
                                            powerCheckerService4 = powerCheckerService;
                                            r18 = r21;
                                            uidStats = sparseArray;
                                            j40 = j10;
                                        } catch (Exception e14) {
                                            e = e14;
                                            r12 = r28;
                                        }
                                    } catch (Exception e15) {
                                        exc = e15;
                                        r7 = r8;
                                    }
                                } else {
                                    uidRecordInfo = powerCheckerService4.mPowerCheckerUidStats.get(iKeyAt);
                                    powerCheckerService4.mPowerCheckerUidStats.put(iKeyAt, new PowerCheckerNotifier.UidRecordInfo(uidStatsInfo5));
                                    j12 = (((long) powerCheckerService4.mUidPartialWakelockLimitRatio.get(iKeyAt, powerCheckerService4.mDefaultUidPartialWakelockLimitRatio)) * j4) / 100;
                                    if (UserHandle.isApp(iKeyAt)) {
                                        j15 = uidStatsInfo5.info.totalPartialSinceCharged - uidRecordInfo.info.totalPartialSinceCharged;
                                        if (powerCheckerService4.mPowerCheckerPolicy.preFiltered(1, iKeyAt, 0, null, str2)) {
                                            powerCheckerService4.logd("  partial wakelock skip for pre filtered, uid:" + iKeyAt);
                                        } else if (j15 > j12 / 1000) {
                                            appPowerExceedInfo2 = new PowerCheckerNotifier.AppPowerExceedInfo(1, (int) ((j15 * 1.0d) / (j12 / 1000)), uidStatsInfo5, uidRecordInfo);
                                            if (arrayMap4.size() > 0) {
                                                appPowerExceedInfo2.lastPartialWakelocks = arrayMap4;
                                            }
                                            if (arrayMap3.size() > 0) {
                                                appPowerExceedInfo2.lastSyncs = arrayMap3;
                                            }
                                            powerCheckerService4.mPowerCheckerController.dispatchEvent(appPowerExceedInfo2);
                                        } else if (r210 == 1) {
                                            appPowerExceedInfo = new PowerCheckerNotifier.AppPowerExceedInfo(1, 0, uidStatsInfo5, uidRecordInfo);
                                            if (arrayMap4.size() > 0) {
                                                appPowerExceedInfo.lastPartialWakelocks = arrayMap4;
                                            }
                                            if (arrayMap3.size() > 0) {
                                                appPowerExceedInfo.lastSyncs = arrayMap3;
                                            }
                                            powerCheckerService4.mPowerCheckerController.dispatchEvent(appPowerExceedInfo);
                                        }
                                    }
                                    j13 = (((long) powerCheckerService4.mUidWifiScanLimitRatio.get(iKeyAt, powerCheckerService4.mDefaultUidWifiScanLimitRatio)) * j4) / 100;
                                    if (UserHandle.isApp(iKeyAt)) {
                                        j14 = uidStatsInfo5.info.wifiScanTime - uidRecordInfo.info.wifiScanTime;
                                        if (j14 > j13 / 1000) {
                                            i6 = (int) ((j14 * 1.0d) / (j13 / 1000));
                                            if (powerCheckerService4.mPowerCheckerPolicy.preFiltered(2, iKeyAt, i6, null, str2)) {
                                                powerCheckerService4.logd("  wifi scan skip for pre filtered, uid:" + iKeyAt + " processPriority:" + i6);
                                            } else {
                                                powerCheckerService4.mPowerCheckerController.dispatchEvent(new PowerCheckerNotifier.AppPowerExceedInfo(2, i6, uidStatsInfo5, uidRecordInfo));
                                            }
                                        }
                                    }
                                    long j510 = (((long) powerCheckerService4.mUidBgGPSTimeLimitRatio.get(iKeyAt, powerCheckerService4.mDefaultUidBgGPSTimeLimitRatio)) * j4) / 100;
                                    str6 = " priority:";
                                    if (UserHandle.isApp(iKeyAt)) {
                                        r28 = r8;
                                        uidStatsInfo = uidStatsInfo5;
                                        r28 = r29;
                                        long j511 = j4 + 500;
                                        long j512 = ((j511 / 1000) * ((long) powerCheckerService4.mUidBgCPUTimeLimitRatio.get(iKeyAt, powerCheckerService4.mDefaultUidBgCPUTimeLimitRatio))) / 100;
                                        it = uidStatsInfo.procCpuTimeStats.entrySet().iterator();
                                        while (it.hasNext()) {
                                            Map.Entry<String, PowerCheckerNotifier.CpuTime> next7 = it.next();
                                            key2 = next7.getKey();
                                            if (key2 != null) {
                                                if (key2.equals("*wakelock*")) {
                                                }
                                                it4 = it;
                                                r28 = r28;
                                                r50 = r50;
                                                it = it4;
                                            }
                                        }
                                        PowerCheckerNotifier.UidRecordInfo uidRecordInfo9 = uidRecordInfo;
                                        String str26 = str6;
                                        long j78 = j511;
                                        ?? r23 = r28;
                                        ?? r24 = r50;
                                        i4 = iKeyAt;
                                        powerCheckerService = powerCheckerService4;
                                        str7 = str5;
                                        uidStatsInfo2 = uidStatsInfo;
                                        r34 = r24;
                                        long j79 = (j78 / 1000) / ((long) powerCheckerService.mUidAverageWakeupIntervalLimit.get(i4, powerCheckerService.mDefaultAverageWakeupIntervalLimit));
                                        it2 = uidStatsInfo2.pkgWakeupStats.entrySet().iterator();
                                        while (it2.hasNext()) {
                                            Map.Entry<String, Integer> next8 = it2.next();
                                            key = next8.getKey();
                                            iIntValue = next8.getValue().intValue();
                                            if (UserHandle.isApp(i4)) {
                                                ArrayMap<String, Integer> arrayMap8 = powerCheckerService.mPkgWakeupStats;
                                                StringBuilder sb16 = new StringBuilder();
                                                sb16.append(i4);
                                                str8 = str7;
                                                sb16.append(str8);
                                                sb16.append(key);
                                                Integer num2 = arrayMap8.get(sb16.toString());
                                                it3 = it2;
                                                powerCheckerService.mPkgWakeupStats.put(i4 + str8 + key, new Integer(iIntValue));
                                                if (j4 > 0) {
                                                }
                                                str7 = str8;
                                            } else {
                                                it3 = it2;
                                            }
                                            uidStatsInfo3 = uidStatsInfo2;
                                            uidStatsInfo2 = uidStatsInfo3;
                                            it2 = it3;
                                        }
                                        PowerCheckerNotifier.UidStatsInfo uidStatsInfo8 = uidStatsInfo2;
                                        long j710 = (((long) powerCheckerService.mUidMobileRadioActiveLimitRatio.get(i4, powerCheckerService.mDefaultUidMobileRadioActiveLimitRatio)) * j4) / 100;
                                        if (!UserHandle.isApp(i4)) {
                                        }
                                        i20 = i3 + 1;
                                        powerCheckerService4 = powerCheckerService;
                                        r18 = r23;
                                        uidStats = sparseArray;
                                        j40 = j10;
                                    } else {
                                        r28 = r8;
                                        uidStatsInfo = uidStatsInfo5;
                                        r28 = r29;
                                        long j513 = j4 + 500;
                                        long j514 = ((j513 / 1000) * ((long) powerCheckerService4.mUidBgCPUTimeLimitRatio.get(iKeyAt, powerCheckerService4.mDefaultUidBgCPUTimeLimitRatio))) / 100;
                                        it = uidStatsInfo.procCpuTimeStats.entrySet().iterator();
                                        while (it.hasNext()) {
                                            Map.Entry<String, PowerCheckerNotifier.CpuTime> next9 = it.next();
                                            key2 = next9.getKey();
                                            if (key2 != null) {
                                                if (key2.equals("*wakelock*")) {
                                                }
                                                it4 = it;
                                                r28 = r28;
                                                r50 = r50;
                                                it = it4;
                                            }
                                        }
                                        PowerCheckerNotifier.UidRecordInfo uidRecordInfo10 = uidRecordInfo;
                                        String str27 = str6;
                                        long j711 = j513;
                                        ?? r25 = r28;
                                        ?? r26 = r50;
                                        i4 = iKeyAt;
                                        powerCheckerService = powerCheckerService4;
                                        str7 = str5;
                                        uidStatsInfo2 = uidStatsInfo;
                                        r34 = r26;
                                        long j712 = (j711 / 1000) / ((long) powerCheckerService.mUidAverageWakeupIntervalLimit.get(i4, powerCheckerService.mDefaultAverageWakeupIntervalLimit));
                                        it2 = uidStatsInfo2.pkgWakeupStats.entrySet().iterator();
                                        while (it2.hasNext()) {
                                            Map.Entry<String, Integer> next10 = it2.next();
                                            key = next10.getKey();
                                            iIntValue = next10.getValue().intValue();
                                            if (UserHandle.isApp(i4)) {
                                                ArrayMap<String, Integer> arrayMap9 = powerCheckerService.mPkgWakeupStats;
                                                StringBuilder sb17 = new StringBuilder();
                                                sb17.append(i4);
                                                str8 = str7;
                                                sb17.append(str8);
                                                sb17.append(key);
                                                Integer num3 = arrayMap9.get(sb17.toString());
                                                it3 = it2;
                                                powerCheckerService.mPkgWakeupStats.put(i4 + str8 + key, new Integer(iIntValue));
                                                if (j4 > 0) {
                                                }
                                                str7 = str8;
                                            } else {
                                                it3 = it2;
                                            }
                                            uidStatsInfo3 = uidStatsInfo2;
                                            uidStatsInfo2 = uidStatsInfo3;
                                            it2 = it3;
                                        }
                                        PowerCheckerNotifier.UidStatsInfo uidStatsInfo9 = uidStatsInfo2;
                                        long j713 = (((long) powerCheckerService.mUidMobileRadioActiveLimitRatio.get(i4, powerCheckerService.mDefaultUidMobileRadioActiveLimitRatio)) * j4) / 100;
                                        if (!UserHandle.isApp(i4)) {
                                        }
                                        i20 = i3 + 1;
                                        powerCheckerService4 = powerCheckerService;
                                        r18 = r25;
                                        uidStats = sparseArray;
                                        j40 = j10;
                                    }
                                }
                            } catch (Exception e16) {
                                e = e16;
                                r12 = r8;
                            }
                            Log.e(r7, exc.getMessage());
                            return true;
                        }
                        PowerCheckerService powerCheckerService6 = powerCheckerService4;
                        long j80 = j40;
                        r12 = r18;
                        if (powerCheckerService6.mBatteryHistoryStats.size() >= 2) {
                            PowerCheckerNotifier.BatteryHistoryItem batteryHistoryItem = powerCheckerService6.mBatteryHistoryStats.get(0);
                            PowerCheckerNotifier.BatteryHistoryItem batteryHistoryItem2 = powerCheckerService6.mBatteryHistoryStats.get(1);
                            if (batteryHistoryItem != null && batteryHistoryItem2 != null && batteryHistoryItem.isOnBatteryScreenOffNonActive() && batteryHistoryItem2.isOnBatteryScreenOffNonActive() && batteryHistoryItem.checkSubSystemInfo(batteryHistoryItem2)) {
                                if (powerCheckerService6.mPowerCheckerPolicy.preFiltered(7, 0, 0, null, null)) {
                                    powerCheckerService6.logd("  sub system excessive power skip for pre filtered");
                                } else {
                                    StringBuilder sb18 = new StringBuilder();
                                    try {
                                        InputStreamReader inputStreamReader = new InputStreamReader(Runtime.getRuntime().exec("cat /d/system_stats").getInputStream());
                                        try {
                                            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                                            while (true) {
                                                try {
                                                    String line = bufferedReader.readLine();
                                                    if (line == null) {
                                                        break;
                                                    }
                                                    sb18.append(line + ShellUtils.COMMAND_LINE_END);
                                                } catch (Throwable th) {
                                                    try {
                                                        bufferedReader.close();
                                                        throw th;
                                                    } catch (Throwable th2) {
                                                        th.addSuppressed(th2);
                                                        throw th;
                                                    }
                                                }
                                                try {
                                                    inputStreamReader.close();
                                                    throw th;
                                                } catch (Throwable th3) {
                                                    th.addSuppressed(th3);
                                                    throw th;
                                                }
                                            }
                                            bufferedReader.close();
                                            inputStreamReader.close();
                                        } catch (Throwable th4) {
                                            inputStreamReader.close();
                                            throw th4;
                                        }
                                    } catch (IOException unused2) {
                                        powerCheckerService6.logd("  sub system shell cmd collect logs fail.");
                                    }
                                    PowerCheckerNotifier.SubSystemPowerExceedInfo subSystemPowerExceedInfo = new PowerCheckerNotifier.SubSystemPowerExceedInfo(7, 0, batteryHistoryItem, batteryHistoryItem2, sb18.toString());
                                    subSystemPowerExceedInfo.subType = batteryHistoryItem.getSubSystemAbnormalType(batteryHistoryItem2);
                                    subSystemPowerExceedInfo.screenOffTotalBytes = powerCheckerService6.mPowerCheckerBatteryStats.getNetworkActivityTotalBytes(1);
                                    subSystemPowerExceedInfo.screenOffTotalAudioOnTimeMs = powerCheckerService6.mPowerCheckerBatteryStats.getAudioOnTime(1);
                                    powerCheckerService6.mPowerCheckerController.dispatchEvent(subSystemPowerExceedInfo);
                                }
                            }
                        }
                        powerCheckerService6.mPowerCheckerController.notifyPowerCheckerComplete();
                        powerCheckerService6.mPowerCheckerBatteryStats.resetAllStatsCurrent(i2, powerCheckerService6.mScreenOn);
                        powerCheckerService6.mLastRawRealTime = j80;
                        long j81 = j5;
                        powerCheckerService6.mLastBatteryRealTime = j81;
                        powerCheckerService6.mLastBatteryScreenOffRealTime = jComputeBatteryScreenOffRealtime;
                        LogCatcher.getInstance().debugLog("wBR=" + j81 + " mLBRT=" + powerCheckerService6.mLastBatteryRealTime + " rR=" + j80 + str3 + powerCheckerService6.mLastRawRealTime + str4 + jComputeBatteryScreenOffRealtime + " mLBSORT=" + powerCheckerService6.mLastBatteryScreenOffRealTime);
                        powerCheckerService6.mLastRawUTCTime = jCurrentTimeMillis;
                        powerCheckerService6.mLastCheckBatteryLevel = powerCheckerService6.mBatteryLevel;
                        return true;
                    } catch (Exception e17) {
                        e = e17;
                        r12 = r18;
                    }
                } catch (Exception e18) {
                    e = e18;
                    r12 = r2;
                }
            } catch (Exception e19) {
                e = e19;
                r12 = TAG;
            }
        } catch (Exception e20) {
            e = e20;
        }
        exc = e;
        r7 = r12;
        Log.e(r7, exc.getMessage());
        return true;
    }

    private void dumpBatteryHistoryStats(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("PowerCheckerService dump battery history stats");
        for (int size = this.mBatteryHistoryStats.size() - 1; size >= 0; size--) {
            printWriter.println(this.mBatteryHistoryStats.get(size));
        }
        KernelRpmStatsReader.dump(fileDescriptor, printWriter, strArr);
        printWriter.println();
    }

    private void dumpCustomerPower(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("Customer power dump start...");
        new CustomerPowerCheck(this).outputFinalResult(printWriter, 0);
        printWriter.println("Customer power dump end.");
        printWriter.println();
    }

    private void dumpService(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        synchronized (this.mLock) {
            try {
                printWriter.println("PowerCheckerService dump start >>>>>>");
                printWriter.println("mPowerCheckerEnabled =" + this.mPowerCheckerEnabled);
                printWriter.println("mPowerCheckCycleInterval =" + this.mPowerCheckCycleInterval);
                printWriter.println("  mPlatformWakelockContributeLimitRatio =" + this.mPlatformWakelockContributeLimitRatio + "%");
                StringBuilder sb = new StringBuilder();
                sb.append("  mDefaultKernelWakelockIntervalLimit =");
                sb.append(this.mDefaultKernelWakelockIntervalLimit);
                printWriter.println(sb.toString());
                printWriter.println("  mDefaultKernelWakeupIntervalLimit =" + this.mDefaultKernelWakeupIntervalLimit);
                printWriter.println("  mDefaultUidPartialWakelockLimitRatio =" + this.mDefaultUidPartialWakelockLimitRatio + "%");
                printWriter.println("  mDefaultUidWifiScanLimitRatio =" + this.mDefaultUidWifiScanLimitRatio + "%");
                printWriter.println("  mDefaultUidBgGPSTimeLimitRatio =" + this.mDefaultUidBgGPSTimeLimitRatio + "%");
                printWriter.println("  mDefaultUidBgCPUTimeLimitRatio =" + this.mDefaultUidBgCPUTimeLimitRatio + "%");
                printWriter.println("  mDefaultAverageWakeupIntervalLimit =" + this.mDefaultAverageWakeupIntervalLimit + "ms");
                printWriter.println("  mDefaultUidMobileRadioActiveLimitRatio =" + this.mDefaultUidMobileRadioActiveLimitRatio + "%");
                for (int i2 = 0; i2 < this.mUidPartialWakelockLimitRatio.size(); i2++) {
                    printWriter.println("uid = " + this.mUidPartialWakelockLimitRatio.keyAt(i2) + ", partial wake lock time limit ratio: " + this.mUidPartialWakelockLimitRatio.valueAt(i2) + "%");
                }
                for (int i3 = 0; i3 < this.mUidWifiScanLimitRatio.size(); i3++) {
                    printWriter.println("uid = " + this.mUidWifiScanLimitRatio.keyAt(i3) + ", wifi scan time limit ratio: " + this.mUidWifiScanLimitRatio.valueAt(i3) + "%");
                }
                for (int i4 = 0; i4 < this.mUidBgGPSTimeLimitRatio.size(); i4++) {
                    printWriter.println("uid = " + this.mUidBgGPSTimeLimitRatio.keyAt(i4) + ", hold GPS at background time limit ratio: " + this.mUidBgGPSTimeLimitRatio.valueAt(i4) + "%");
                }
                for (int i5 = 0; i5 < this.mUidBgCPUTimeLimitRatio.size(); i5++) {
                    printWriter.println("uid = " + this.mUidBgCPUTimeLimitRatio.keyAt(i5) + ", consume CPU resource at background time limit ratio: " + this.mUidBgCPUTimeLimitRatio.valueAt(i5) + "%");
                }
                for (int i6 = 0; i6 < this.mUidAverageWakeupIntervalLimit.size(); i6++) {
                    printWriter.println("uid = " + this.mUidAverageWakeupIntervalLimit.keyAt(i6) + ", average wakeup interval min: " + this.mUidAverageWakeupIntervalLimit.valueAt(i6) + "ms");
                }
                for (int i7 = 0; i7 < this.mUidMobileRadioActiveLimitRatio.size(); i7++) {
                    printWriter.println("uid = " + this.mUidMobileRadioActiveLimitRatio.keyAt(i7) + ", mobile radio active time limit ratio: " + this.mUidMobileRadioActiveLimitRatio.valueAt(i7) + "%");
                }
                printWriter.println();
                printWriter.println("mUidActiveStatus = " + this.mUidActiveStatus);
                printWriter.println();
                dumpBatteryHistoryStats(fileDescriptor, printWriter, strArr);
            } catch (Throwable th) {
                throw th;
            }
        }
        this.mPowerCheckerPolicy.dump(fileDescriptor, printWriter, strArr);
        this.mPowerCheckerProcessStats.dump(fileDescriptor, printWriter, strArr);
        this.mPowerCheckerProcessNotifier.dump(fileDescriptor, printWriter, strArr);
        this.mPowerCheckerBatteryStats.dump(fileDescriptor, printWriter, strArr, 0);
        this.mPowerCheckerBatteryStats.dump(fileDescriptor, printWriter, strArr, 1);
        this.mPowerCheckerBatteryStats.dump(fileDescriptor, printWriter, strArr, 2);
        this.mPowerCheckerBatteryStats.dumpLastStatsCurrent(fileDescriptor, printWriter, strArr);
        this.mPowerCheckerController.dump(fileDescriptor, printWriter, strArr);
        printWriter.println();
        this.mNightPowerAbnormalChecker.dump(fileDescriptor, printWriter, strArr);
        printWriter.println();
        printWriter.println("All debug log:");
        printWriter.println(LogCatcher.getInstance().getAllDebugLog());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public long getTotalBytes() {
        return TrafficStats.getTotalTxBytes() + TrafficStats.getTotalRxBytes();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isCharging(int i2, int i3) {
        if (i2 == 2) {
            return true;
        }
        return i2 == 5 && (i3 & 15) != 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logd(String str) {
        if (DEBUG) {
            Log.d(TAG, str);
        }
    }

    private void registerActiveCheck() {
        this.mUidActiveStatus.clear();
        this.mPowerKeeperManager.getActiveController().loadActiveUid(this.mUidActiveStatus, 3);
        this.mAppActiveChangedListener = new PowerKeeperInterface.j() { // from class: com.miui.powerkeeper.powerchecker.PowerCheckerService.9
            @Override // com.miui.powerkeeper.PowerKeeperInterface.j
            public void onAppActiveChange(int i2, boolean z2) {
                StringBuilder sb = new StringBuilder();
                sb.append("uid:");
                sb.append(i2);
                sb.append(z2 ? " change to active" : "change to inactive");
                Log.i(PowerCheckerService.TAG, sb.toString());
                if (z2 != PowerCheckerService.this.mUidActiveStatus.get(i2, false)) {
                    PowerCheckerService.this.mUidActiveStatus.put(i2, z2);
                    PowerCheckerService.this.updateForAudioChangedLocked(i2, z2);
                }
            }
        };
        this.mPowerKeeperManager.getActiveController().registerActiveListener(this.mAppActiveChangedListener, 3);
    }

    private void registerCloudObserver() {
        Log.d(TAG, "register cloud");
        b.a aVar = this.mCloudObserver;
        int i2 = b.b.f43y;
        aVar.t(i2, new b.a.d() { // from class: com.miui.powerkeeper.powerchecker.PowerCheckerService.2
            @Override // b.a.d
            public void onChanged(JSONObject jSONObject) {
                Log.d(PowerCheckerService.TAG, "new-cloud cdp onchange");
                PowerCheckerService.this.updateCheckerData(12, jSONObject);
            }
        });
        b.a aVar2 = this.mCloudObserver;
        int i3 = b.b.f42x;
        aVar2.t(i3, new b.a.d() { // from class: com.miui.powerkeeper.powerchecker.PowerCheckerService.3
            @Override // b.a.d
            public void onChanged(JSONObject jSONObject) {
                Log.d(PowerCheckerService.TAG, "new-cloud cal onchange");
                PowerCheckerService.this.updateCheckerData(13, jSONObject);
            }
        });
        b.a aVar3 = this.mCloudObserver;
        int i4 = b.b.f41w;
        aVar3.t(i4, new b.a.d() { // from class: com.miui.powerkeeper.powerchecker.PowerCheckerService.4
            @Override // b.a.d
            public void onChanged(JSONObject jSONObject) {
                Log.d(PowerCheckerService.TAG, "new-cloud pcc onchange");
                PowerCheckerService.this.updateCheckerData(14, jSONObject);
            }
        });
        boolean zS = this.mCloudObserver.s(i2);
        boolean zRegisterOldCloudObserver = false;
        if (!zS) {
            Log.d(TAG, "cdp new-cloud is not configured");
            zRegisterOldCloudObserver = registerOldCloudObserver(false);
        }
        if (!this.mCloudObserver.s(i3)) {
            Log.d(TAG, "cal new-cloud is not configured");
            zRegisterOldCloudObserver = registerOldCloudObserver(zRegisterOldCloudObserver);
        }
        if (this.mCloudObserver.s(i4)) {
            return;
        }
        Log.d(TAG, "pcc new-cloud is not configured");
        registerOldCloudObserver(zRegisterOldCloudObserver);
    }

    private boolean registerOldCloudObserver(boolean z2) {
        Log.d(TAG, "register old-cloud");
        if (z2) {
            return true;
        }
        getContentResolver().registerContentObserver(SimpleSettings.Misc.getUriFor(SimpleSettingKeys.KEY_ABNORMAL_CONSUME_LAST_DATA_MD5), false, this.mCheckerObserver);
        return true;
    }

    private void requestKernelRpmStatsUpdate(PowerCheckerNotifier.BatteryHistoryItem batteryHistoryItem) {
        if (this.mKernelRpmStatsSupported) {
            try {
                KernelRpmStatsReader.readRpmStats();
                KernelRpmStatsReader.RPMSubsys rPMSubsys = KernelRpmStatsReader.RPMSubsys.RPM_VDD_LOW;
                batteryHistoryItem.rpmVddLowCount = KernelRpmStatsReader.getSubsysSleepCnt(rPMSubsys);
                KernelRpmStatsReader.RPMSubsys rPMSubsys2 = KernelRpmStatsReader.RPMSubsys.RPM_VDD_MIN;
                batteryHistoryItem.rpmVddMinCount = KernelRpmStatsReader.getSubsysSleepCnt(rPMSubsys2);
                batteryHistoryItem.rpmAPSSXOCount = KernelRpmStatsReader.getSubsysSleepCnt(KernelRpmStatsReader.RPMSubsys.RPM_APSS);
                batteryHistoryItem.rpmMPSSXOCount = KernelRpmStatsReader.getSubsysSleepCnt(KernelRpmStatsReader.RPMSubsys.RPM_MPSS);
                batteryHistoryItem.rpmADSPXOCount = KernelRpmStatsReader.getSubsysSleepCnt(KernelRpmStatsReader.RPMSubsys.RPM_ADSP);
                batteryHistoryItem.rpmSLPIXOCount = KernelRpmStatsReader.getSubsysSleepCnt(KernelRpmStatsReader.RPMSubsys.RPM_SLPI);
                batteryHistoryItem.rpmCDSPXOCount = KernelRpmStatsReader.getSubsysSleepCnt(KernelRpmStatsReader.RPMSubsys.RPM_CDSP);
                batteryHistoryItem.rpmVddLowDuration = KernelRpmStatsReader.getSubsysSleepCnt(KernelRpmStatsReader.RPMSubsys.RPM_VDD_LOW_TIME);
                batteryHistoryItem.rpmVddMinDuration = KernelRpmStatsReader.getSubsysSleepCnt(KernelRpmStatsReader.RPMSubsys.RPM_VDD_MIN_TIME);
                batteryHistoryItem.rpmVddLowName = KernelRpmStatsReader.getRpmModeName(rPMSubsys);
                batteryHistoryItem.rpmVddMinName = KernelRpmStatsReader.getRpmModeName(rPMSubsys2);
                batteryHistoryItem.rpmSubsysStats = KernelRpmStatsReader.getRpmSubsysStats();
                logd("Update Rpm Stats: vddLow=" + batteryHistoryItem.rpmVddLowCount + ", vddMin=" + batteryHistoryItem.rpmVddMinCount + ", APSS=" + batteryHistoryItem.rpmAPSSXOCount + ", MPSS=" + batteryHistoryItem.rpmMPSSXOCount + ", ADSP=" + batteryHistoryItem.rpmADSPXOCount + ", SLPI=" + batteryHistoryItem.rpmSLPIXOCount + ", CDSP=" + batteryHistoryItem.rpmCDSPXOCount + ", VddLowDuration=" + batteryHistoryItem.rpmVddLowDuration + ", VddMinDuration=" + batteryHistoryItem.rpmVddLowDuration);
            } catch (IOException unused) {
                logd("Kernel rpm stats not exist or read failed");
            }
        }
    }

    private void reset() {
        Log.d(TAG, "reset");
        synchronized (this.mLock) {
            this.mLastRawRealTime = 0L;
            this.mLastBatteryRealTime = 0L;
            this.mLastBatteryScreenOffRealTime = 0L;
            this.mLastRawUTCTime = 0L;
            this.mPowerCheckerUidStats.clear();
            this.mKernelWakelockStats.clear();
            this.mKernelWakeupStats.clear();
            this.mPartialWakelockStats.clear();
            this.mSyncStats.clear();
            this.mProcCpuTimeStats.clear();
            this.mPkgWakeupStats.clear();
            this.mLastCheckBatteryLevel = -1;
        }
    }

    private void resetWakelock() {
        PowerCheckerNotifier.BaseInfo baseInfo;
        synchronized (this.mLock) {
            try {
                if (this.mScreenOn) {
                    return;
                }
                for (int i2 = 0; i2 < this.mPowerCheckerUidStats.size(); i2++) {
                    PowerCheckerNotifier.UidRecordInfo uidRecordInfoValueAt = this.mPowerCheckerUidStats.valueAt(i2);
                    if (uidRecordInfoValueAt != null && (baseInfo = uidRecordInfoValueAt.info) != null) {
                        baseInfo.totalPartial = 0L;
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startPowerCheckerCycle() {
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        Log.d(TAG, "power check cycle start...");
        IBatteryStats batteryStats = BatteryStatsHelper.getBatteryStats();
        synchronized (this.mLock) {
            if (batteryStats != null) {
                try {
                    if (doPowerCheck(batteryStats)) {
                        this.mHandler.sendEmptyMessage(2);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
            this.mPowerCheckerReady = false;
        }
        Log.d(TAG, "power check cycle done, consume time " + (SystemClock.elapsedRealtime() - jElapsedRealtime) + "ms");
    }

    private void unregisterActiveCheck() {
        if (this.mAppActiveChangedListener != null) {
            this.mPowerKeeperManager.getActiveController().unregisterActiveListener(this.mAppActiveChangedListener, 3);
            this.mAppActiveChangedListener = null;
        }
        this.mUidActiveStatus.clear();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateBatteryLevelChanged(boolean z2, PowerCheckerNotifier.BatteryHistoryItem batteryHistoryItem) {
        synchronized (this.mLock) {
            if (z2) {
                try {
                    this.mPowerCheckerReady = true;
                } catch (Throwable th) {
                    throw th;
                }
            }
            addBatteryHistoryRecord(batteryHistoryItem);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCheckerData(int i2, JSONObject jSONObject) {
        PowerCheckHandler powerCheckHandler = this.mHandler;
        powerCheckHandler.sendMessage(powerCheckHandler.obtainMessage(i2, jSONObject));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateConfig() {
        Log.d(TAG, "updateConfig after updateCloud");
        reset();
        this.mPowerCheckCycleInterval = this.mPowerCheckerPolicy.getMinCheckTimeInterval();
        boolean z2 = false;
        this.mPlatformWakelockContributeLimitRatio = this.mPowerCheckerPolicy.getDefaultLimitRatio(0);
        this.mDefaultKernelWakelockIntervalLimit = this.mPowerCheckerPolicy.getKernelWakelockInterval();
        this.mDefaultKernelWakeupIntervalLimit = this.mPowerCheckerPolicy.getKernelWakeupInterval();
        this.mDefaultUidPartialWakelockLimitRatio = this.mPowerCheckerPolicy.getDefaultLimitRatio(1);
        this.mDefaultUidWifiScanLimitRatio = this.mPowerCheckerPolicy.getDefaultLimitRatio(2);
        this.mDefaultUidBgGPSTimeLimitRatio = this.mPowerCheckerPolicy.getDefaultLimitRatio(3);
        this.mDefaultUidBgCPUTimeLimitRatio = this.mPowerCheckerPolicy.getDefaultLimitRatio(4);
        this.mDefaultAverageWakeupIntervalLimit = this.mPowerCheckerPolicy.getDefaultLimitRatio(5);
        this.mDefaultUidMobileRadioActiveLimitRatio = this.mPowerCheckerPolicy.getDefaultLimitRatio(6);
        boolean zIsEnable = this.mPowerCheckerPolicy.isEnable();
        Log.d(TAG, "feature config: " + zIsEnable);
        if (zIsEnable) {
            enable();
        } else {
            disable();
        }
        if (this.mPowerCheckerEnabled && !isCharging(this.mBatteryStatus, this.mBatteryPlugged)) {
            z2 = true;
        }
        LogCatcher.getInstance().debugLog("enable=" + zIsEnable + " enableStats=" + z2);
        this.mPowerCheckerBatteryStats.setEnabledState(z2, true);
        if (z2) {
            checkAppActive();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateForAudioChangedLocked(int i2, boolean z2) {
        this.mPowerCheckerBatteryStats.notifyAudioStatusChanged(i2, z2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateForFgUidChangedLocked(int i2, boolean z2) {
        logd("updateForFgUidChangedLocked, mScreenOn = " + this.mScreenOn + " uid = " + i2 + " foreground = " + z2);
        LogCatcher logCatcher = LogCatcher.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append("uid=");
        sb.append(i2);
        sb.append(" foreground=");
        sb.append(z2);
        logCatcher.debugLog(sb.toString());
        this.mPowerCheckerBatteryStats.noteForegroundActivitiesChanged(i2, z2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateForPowerChangedLocked(boolean z2) {
        if (!z2) {
            reset();
        }
        boolean z3 = this.mPowerCheckerEnabled && !z2;
        LogCatcher.getInstance().debugLog("enableStats=" + z3);
        this.mPowerCheckerBatteryStats.setEnabledState(z3, true);
        if (z3) {
            checkAppActive();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateForScreenChangedLocked() {
        this.mScreenOn = this.mPowerKeeperManager.isScreenOn();
        logd("updateForScreenChangedLocked, mScreenOn = " + this.mScreenOn);
        if (!this.mScreenOn) {
            resetWakelock();
        }
        this.mPowerCheckerBatteryStats.setBatteryState(!this.mScreenOn);
        ForegroundInfo foregroundInfo = ProcessManager.getForegroundInfo();
        String str = foregroundInfo == null ? null : foregroundInfo.mForegroundPackageName;
        int i2 = foregroundInfo == null ? -1 : foregroundInfo.mForegroundUid;
        logd("foreground, uid = " + i2 + ", app = " + str);
        LogCatcher.getInstance().debugLog("mScreenOn=" + this.mScreenOn + " uid=" + i2 + " app=" + str);
        if (i2 >= 0) {
            this.mPowerCheckerBatteryStats.noteForegroundActivitiesChanged(i2, this.mScreenOn);
        }
    }

    public void disable() {
        synchronized (this.mLock) {
            try {
                if (!this.mPowerCheckerEnabled) {
                    logd("still disabled");
                    return;
                }
                Log.d(TAG, "disable");
                this.mPowerCheckerEnabled = false;
                this.mPowerKeeperManager.unregisterForegroundActivitiesChangedListener(this.mForegroundActivitiesChangedCallback);
                this.mForegroundActivitiesChangedCallback.a();
                this.mForegroundActivitiesChangedCallback = null;
                this.mPowerKeeperManager.unregisterProcessDiedListener(this.mProcessDiedCallback);
                this.mProcessDiedCallback.a();
                this.mProcessDiedCallback = null;
                this.mIsOnlyCheckRpm = true;
                this.mPowerKeeperManager.unregisterScreenChangedListener(this.mScreenChangedCallback);
                this.mScreenChangedCallback.a();
                this.mScreenChangedCallback = null;
                unregisterActiveCheck();
                this.mPowerCheckerProcessNotifier.notifyStopPowerCheckerImmediately();
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // android.app.Service
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (UserHandle.myUserId() != 0) {
            return;
        }
        boolean customerPower = this.mPowerCheckerPolicy.getCustomerPower();
        boolean z2 = false;
        for (int i2 = 0; i2 < strArr.length; i2++) {
            String str = strArr[i2];
            if (str.equalsIgnoreCase("json") && i2 < strArr.length - 1) {
                return;
            }
            if (str.equalsIgnoreCase("battery_usage")) {
                JSONObject jSONObjectDumpBatteryUsageAttributeJson = this.mBatteryUsageAttribute.dumpBatteryUsageAttributeJson(getBatteryStatsData());
                if (jSONObjectDumpBatteryUsageAttributeJson.names() != null) {
                    printWriter.println(jSONObjectDumpBatteryUsageAttributeJson.toString());
                    return;
                }
                return;
            }
            if (str.equalsIgnoreCase("genklofile") && i2 < strArr.length - 1) {
                return;
            }
            if (str.equalsIgnoreCase("fastcheck")) {
                z2 = true;
            }
            if (str.equalsIgnoreCase("cloud")) {
                b.a.p().j(printWriter, strArr);
                return;
            }
        }
        if (z2) {
            synchronized (this.mLock) {
                this.mPowerCheckCycleInterval = 180000;
                this.mFastCheck = true;
                this.mShutdownPowerChecker.setFastCheck(true);
            }
            return;
        }
        printWriter.println("CustomerPower dump switch is " + customerPower);
        if (customerPower) {
            dumpCustomerPower(fileDescriptor, printWriter, strArr);
        }
        dumpService(fileDescriptor, printWriter, strArr);
        b.a.p().j(printWriter, strArr);
    }

    public void enable() {
        synchronized (this.mLock) {
            try {
                if (this.mPowerCheckerEnabled) {
                    logd("already enabled");
                    return;
                }
                Log.d(TAG, "enable");
                this.mPowerCheckerEnabled = true;
                PowerKeeperInterface.f fVar = new PowerKeeperInterface.f(this.mHandler, this.mProcessObserver);
                this.mForegroundActivitiesChangedCallback = fVar;
                this.mPowerKeeperManager.registerForegroundActivitiesChangedListener(fVar);
                PowerKeeperInterface.e0 e0Var = new PowerKeeperInterface.e0(this.mHandler, this.mProcessDiedObserver);
                this.mProcessDiedCallback = e0Var;
                this.mPowerKeeperManager.registerProcessDiedListener(e0Var);
                Intent intentRegisterReceiver = registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"), 4);
                if (intentRegisterReceiver != null) {
                    this.mBatteryStatus = intentRegisterReceiver.getIntExtra("status", -1);
                    this.mBatteryPlugged = intentRegisterReceiver.getIntExtra("plugged", -1);
                    this.mBatteryLevel = intentRegisterReceiver.getIntExtra("level", -1);
                } else {
                    this.mBatteryStatus = -1;
                    this.mBatteryPlugged = -1;
                    this.mBatteryLevel = -1;
                }
                this.mIsOnlyCheckRpm = false;
                if (this.mBatteryChangedCallback == null) {
                    PowerKeeperInterface.d dVar = new PowerKeeperInterface.d(this.mHandler, this.mBatteryStatusReceiver);
                    this.mBatteryChangedCallback = dVar;
                    this.mPowerKeeperManager.registerBatteryChangedListener(dVar);
                }
                this.mScreenOn = this.mPowerKeeperManager.isScreenOn();
                PowerKeeperInterface.f0 f0Var = new PowerKeeperInterface.f0(this.mHandler, this.mScreenReceiver);
                this.mScreenChangedCallback = f0Var;
                this.mPowerKeeperManager.registerScreenChangedListener(f0Var);
                registerActiveCheck();
                this.mPowerCheckerProcessNotifier.notifyStartSchedulePowerCheckerCycle();
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    IBatteryStats getBatteryStatsData() {
        IBatteryStats batteryStats;
        synchronized (this.mLock) {
            batteryStats = BatteryStatsHelper.getBatteryStats();
        }
        return batteryStats;
    }

    public void handleBootCompleted() {
        if (this.mBootCompleted) {
            return;
        }
        checkKernelRpmStatsSupported();
        PowerCheckHandler powerCheckHandler = this.mHandler;
        powerCheckHandler.sendMessageDelayed(Message.obtain(powerCheckHandler, 9), 86400000L);
        CustomerPowerCheckHandler customerPowerCheckHandler = this.mCustomerPowerCheckHandler;
        customerPowerCheckHandler.sendMessageDelayed(Message.obtain(customerPowerCheckHandler, 11), 86400000L);
        this.mHandler.post(new Runnable() { // from class: com.miui.powerkeeper.powerchecker.PowerCheckerService.10
            @Override // java.lang.Runnable
            public void run() {
                PowerCheckerService.this.mPowerCheckerBatteryStats.setBatteryState(!PowerCheckerService.this.mPowerKeeperManager.isScreenOn());
                PowerCheckerService.this.mPowerCheckerBatteryStats.setDummyState(true);
                Slog.d(PowerCheckerService.TAG, "enable slog the cpu usage");
                SystemProperties.set("events.cpu", "true");
            }
        });
        this.mHandler.sendEmptyMessage(3);
        this.mBootCompleted = true;
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        if (UserHandle.myUserId() != 0) {
            return null;
        }
        return new MyBinderService().asBinder();
    }

    @Override // android.app.Service
    public void onCreate() {
        if (UserHandle.myUserId() != 0) {
            stopSelf();
            return;
        }
        this.mPowerKeeperManager = PowerKeeperManager.getInstance();
        this.mBatteryUsageAttribute = new BatteryUsageAttribute(this, this.mLock);
        HandlerThread handlerThread = new HandlerThread(TAG);
        this.mHandlerThread = handlerThread;
        handlerThread.start();
        this.mHandler = new PowerCheckHandler(this.mHandlerThread.getLooper());
        this.mCustomerPowerCheckHandler = new CustomerPowerCheckHandler(this.mHandlerThread.getLooper());
        this.mPowerCheckerBatteryStats = new PowerCheckerBatteryStats(this);
        this.mPowerCheckerProcessStats = new PowerCheckerProcessStats(this);
        this.mPowerCheckerProcessNotifier = new PowerCheckerProcessNotifier(this);
        this.mPowerCheckerPolicy = new PowerCheckerPolicy(this, this.mPowerCheckerProcessStats, this.mPowerCheckerProcessNotifier, this.mPowerCheckerBatteryStats);
        this.mPowerCheckerController = new PowerCheckerController(this, this.mHandlerThread.getLooper(), this.mPowerCheckerPolicy, this.mPowerCheckerProcessStats, this.mPowerCheckerProcessNotifier);
        this.mShutdownPowerChecker = new ShutdownPowerChecker(this, this.mHandlerThread.getLooper());
        this.mCloudObserver = b.a.p();
        registerCloudObserver();
        this.mNightPowerAbnormalChecker = new NightPowerAbnormalChecker(this, this.mHandlerThread.getLooper());
        this.mPowerKeeperManager.getTimeScheduleManager().e(0, this.mNightPowerAbnormalChecker);
        this.mNightStandbyRecord = new NightStandbyRecord(this);
        if (this.mBatteryChangedCallback == null) {
            PowerKeeperInterface.d dVar = new PowerKeeperInterface.d(this.mHandler, this.mBatteryStatusReceiver);
            this.mBatteryChangedCallback = dVar;
            this.mPowerKeeperManager.registerBatteryChangedListener(dVar);
        }
    }

    @Override // android.app.Service
    public void onDestroy() {
        if (UserHandle.myUserId() != 0) {
            super.onDestroy();
            return;
        }
        this.mHandlerThread.quitSafely();
        this.mPowerKeeperManager.getTimeScheduleManager().g(0, this.mNightPowerAbnormalChecker);
        NightPowerAbnormalChecker nightPowerAbnormalChecker = this.mNightPowerAbnormalChecker;
        if (nightPowerAbnormalChecker != null) {
            nightPowerAbnormalChecker.destroy();
        }
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i2, int i3) {
        if (UserHandle.myUserId() != 0) {
            stopSelf();
            return 1;
        }
        if (intent != null && intent.getAction() != null) {
            Slog.d(TAG, "onStartCommand: " + intent.toString());
            if (intent.getAction().equalsIgnoreCase("android.intent.action.BOOT_COMPLETED")) {
                handleBootCompleted();
                this.mShutdownPowerChecker.sendEmptyMessage(2);
            }
        } else if (SystemProperties.get("sys.boot_completed").equals("1")) {
            handleBootCompleted();
        }
        return 1;
    }

    void triggerPowerCheckerInternal(boolean z2) {
        synchronized (this.mLock) {
            try {
                if (this.mPowerCheckerEnabled && this.mHandler.hasMessages(1)) {
                    this.mHandler.removeMessages(1);
                }
                if (z2) {
                    this.mHandler.sendEmptyMessage(1);
                } else if (this.mPowerCheckerEnabled && (this.mPowerCheckerReady || this.mFastCheck)) {
                    if (this.mLastRawRealTime != 0 && ((SystemClock.elapsedRealtime() * 1000) - this.mLastRawRealTime) / 1000 < this.mPowerCheckCycleInterval) {
                        if (DEBUG) {
                            Log.w(TAG, "Time interval is too short since last check");
                        }
                        return;
                    }
                    this.mHandler.sendEmptyMessage(1);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCheckerData() {
        this.mHandler.sendEmptyMessage(3);
    }
}
