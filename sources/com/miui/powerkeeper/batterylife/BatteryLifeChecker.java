package com.miui.powerkeeper.batterylife;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import com.android.id.IdentifierManager;
import com.android.internal.os.BackgroundThread;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.TimeSpan;
import com.miui.powerkeeper.batterylife.data.AppAudioData;
import com.miui.powerkeeper.batterylife.data.AppCpuinfoData;
import com.miui.powerkeeper.batterylife.data.AppScreenPowerData;
import com.miui.powerkeeper.batterylife.data.AppTransactionData;
import com.miui.powerkeeper.batterylife.data.BatteryLife;
import com.miui.powerkeeper.batterylife.data.CpuTimeStatsData;
import com.miui.powerkeeper.batterylife.data.MyBatterySipper;
import com.miui.powerkeeper.batterylife.data.SsruSystemData;
import com.miui.powerkeeper.batterylife.data.TimeInStateData;
import com.miui.powerkeeper.batterylife.monitor.ResourceMonitor;
import com.miui.powerkeeper.batterylife.systeminfo.BSHelper;
import com.miui.powerkeeper.batterylife.systeminfo.CpuStatus;
import com.miui.powerkeeper.cloudcontrol.CloudFunctionConfig;
import com.miui.powerkeeper.logsystem.utils.GsonUtils;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.tracker.TrackerConfig;
import com.miui.powerkeeper.tracker.TrackerManager;
import com.miui.powerkeeper.utils.SharedPrefUtil;
import com.miui.powerkeeper.utils.ShellUtils;
import com.miui.powerkeeper.utils.Utils;
import com.xiaomi.push.service.PushServiceConstants;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import miui.os.Build;
import miui.util.MiuiMultiDisplayTypeInfo;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class BatteryLifeChecker implements TimeSpan.a, PowerKeeperInterface.z, PowerKeeperInterface.m {
    public static final int BATTERY_PLUGGED_AC = 1;
    public static final int BATTERY_PLUGGED_DOCK = 8;
    public static final int BATTERY_PLUGGED_NONE = 0;
    public static final int BATTERY_PLUGGED_USB = 2;
    public static final int BATTERY_PLUGGED_WIRELESS = 4;
    public static final String FLAG_NIGHTCURRENT = "flag_nightcurrent";
    private static final int MIN_ACCOUNT_INTERVAL = 36000000;
    private static final int MSG_BL_STATUS_CHANGED = 4;
    private static final int MSG_CLOUD_UPDATE = 0;
    private static final int MSG_IN_TIME_SPAN = 1;
    private static final int MSG_OUT_TIME_SPAN = 2;
    private static final int MSG_REGISTER_CLOUD_OBSERVER = 3;
    private static final String TAG = "BL-BatteryLifeChecker";
    public static BatteryLifeChecker sInstance;
    private PowerKeeperInterface.d mBatteryChangedCallback;
    private BatteryHistoryManager mBatteryHistoryManager;
    private Context mContext;
    private ProcCpuTimeInStateManager mCpuTimeInStateManager;
    private Handler mHandler;
    private boolean mIsAlarmEnable;
    private boolean mIsEnabled;
    private boolean mIsJobEnable;
    private boolean mIsSsruTransactionEnable;
    private BatteryLifePolicy mPolicy;
    private ProcAlarmCountManager mProcAlarmCountManager;
    private ProcAudioPowerManager mProcAudioPowerManager;
    private ProcCpuinfoManager mProcCpuinfoManager;
    private ProcJobSchedulerCountManager mProcJobSchedulerCountManager;
    private ProcScreenPowerManager mProcScreenPowerManager;
    private ProcSsruTransactionManager mProcSsruTransactionManager;
    private ResourceMonitor mResourceMonitor;
    private PowerKeeperInterface.f0 mScreenChangedCallback;
    private boolean mWhetstoneInterfaceAvailable = true;

    public BatteryLifeChecker(Context context) {
        this.mResourceMonitor = null;
        Log.d(TAG, "BatteryLifeChecker start.");
        this.mContext = context;
        Handler handler = new Handler(BackgroundThread.getHandler().getLooper()) { // from class: com.miui.powerkeeper.batterylife.BatteryLifeChecker.1
            @Override // android.os.Handler
            public void handleMessage(Message message) throws Throwable {
                BatteryLifeChecker.this.onMessage(message);
            }
        };
        this.mHandler = handler;
        handler.sendEmptyMessage(3);
        this.mResourceMonitor = new ResourceMonitor();
        Settings.System.putInt(this.mContext.getContentResolver(), FLAG_NIGHTCURRENT, 1);
    }

    private void batteryHistoryManagerDisableIfNeeded() {
        if (this.mBatteryHistoryManager != null) {
            this.mBatteryHistoryManager = null;
        }
    }

    private void batteryHistoryManagerEnableIfNeeded() {
        if (this.mIsEnabled && this.mBatteryHistoryManager == null) {
            this.mBatteryHistoryManager = new BatteryHistoryManager(this.mContext);
        }
    }

    private void checkNightCurrent() {
        BatteryData data = this.mPolicy.getData();
        int i2 = 8;
        if (data.mEstimateBatteryLifeDQ.size() <= 0) {
            Log.d(TAG, " checkNightCurrent, data is null");
            Settings.System.putInt(this.mContext.getContentResolver(), FLAG_NIGHTCURRENT, 8);
            return;
        }
        Iterator<BatteryLife.Data> it = data.mEstimateBatteryLifeDQ.getLast().dataList.iterator();
        boolean z2 = false;
        while (it.hasNext()) {
            for (BatteryLife.MatchingData matchingData : it.next().matchingDatas) {
                long j2 = matchingData.duration;
                if (j2 > 0) {
                    double d2 = matchingData.consumption;
                    if (d2 <= 0.0d) {
                        continue;
                    } else {
                        if ((d2 * 3600.0d) / j2 > 20.0d) {
                            i2 = 4;
                            z2 = true;
                            break;
                        }
                        i2 = 2;
                    }
                }
            }
            if (z2) {
                break;
            }
        }
        Log.d(TAG, "curFlagOfNightCurrent is " + i2);
        Settings.System.putInt(this.mContext.getContentResolver(), FLAG_NIGHTCURRENT, i2);
    }

    private void disable() {
        Log.d(TAG, " disable()");
        setInstance(null);
        unRegisterReceiver();
        this.mPolicy = null;
        procCpuDisableIfNeeded();
        procScreenDisableIfNeeed();
        procAlarmDisableIfNeed();
        procJobDisableIfNeed();
        procSsruTransactionDisableIfNeed();
        procAudioDisableIfNeeed();
        procCpuInfoDisableIfNeeed();
        batteryHistoryManagerDisableIfNeeded();
    }

    private void doInit() {
        Log.d(TAG, " doInit()");
        setInstance(this);
        this.mPolicy = new BatteryLifePolicy(this.mContext);
        registerReceiver();
        procCpuEnableIfNeeded();
        procScreenEnableIfNeeed();
        procAlarmEnableIfNeed();
        procJobEnableIfNeed();
        procAudioEnableIfNeeded();
        procCpuInfoEnableIfNeeed();
        batteryHistoryManagerEnableIfNeeded();
    }

    public static synchronized BatteryLifeChecker getInstance() {
        BatteryLifeChecker batteryLifeChecker;
        synchronized (BatteryLifeChecker.class) {
            batteryLifeChecker = sInstance;
        }
        return batteryLifeChecker;
        return batteryLifeChecker;
    }

    private long getScreenOnDuration() {
        BatteryLifePolicy batteryLifePolicy = this.mPolicy;
        if (batteryLifePolicy == null) {
            return 0L;
        }
        BatteryData data = batteryLifePolicy.getData();
        if (data.mEstimateBatteryLifeDQ.size() <= 0) {
            return 0L;
        }
        for (BatteryLife.Data data2 : data.mEstimateBatteryLifeDQ.getLast().dataList) {
            if (data2.powerMode == 100) {
                return data2.duration[0];
            }
        }
        return 0L;
    }

    /* JADX WARN: Code duplicated, block: B:10:0x0033  */
    private SparseArray<Map<String, Object>> getTimeInState() {
        TimeInStateData timeInStateData;
        TimeInStateData timeInStateData2;
        SparseArray<Map<String, Object>> sparseArray = new SparseArray<>();
        TimeInStateData timeInStateData3 = new TimeInStateData();
        timeInStateData3.time = System.currentTimeMillis();
        timeInStateData3.mTimeInStateMap = CpuStatus.getTimeInStateMap();
        TimeInStateData lastTimeInState = this.mPolicy.getLastTimeInState();
        String[] cpuCluster = CpuStatus.getCpuCluster();
        if (cpuCluster == null) {
            return sparseArray;
        }
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        while (i3 < cpuCluster.length) {
            if (TextUtils.isEmpty(cpuCluster[i3])) {
                timeInStateData = timeInStateData3;
                timeInStateData2 = lastTimeInState;
            } else {
                ArrayMap arrayMap = new ArrayMap();
                arrayMap.put(TrackData.CPU_TIME_IN_STATE_CLUSTER, "cluster" + i3);
                arrayMap.put(TrackData.CPU_TIME_IN_STATE_DURATION, Long.valueOf(timeInStateData3.time - lastTimeInState.time));
                String str = lastTimeInState.mTimeInStateMap.get(cpuCluster[i3]);
                String[] strArrSplit = str != null ? str.split(ShellUtils.COMMAND_LINE_END) : null;
                String str2 = timeInStateData3.mTimeInStateMap.get(cpuCluster[i3]);
                String[] strArrSplit2 = str2 != null ? str2.split(ShellUtils.COMMAND_LINE_END) : null;
                if (strArrSplit2 == null || strArrSplit == null || strArrSplit2.length != strArrSplit.length) {
                    timeInStateData = timeInStateData3;
                    timeInStateData2 = lastTimeInState;
                } else {
                    ArrayList arrayList = new ArrayList();
                    long j2 = 0;
                    int i5 = i2;
                    while (i5 < strArrSplit2.length) {
                        ArrayMap arrayMap2 = new ArrayMap();
                        TimeInStateData timeInStateData4 = timeInStateData3;
                        arrayMap2.put(TrackData.CPU_FREQ_TIME_IN_STATE_FREQ, strArrSplit2[i5].split("\\s+")[i2].trim());
                        long jNumberStringSubtract = BLUtils.numberStringSubtract(strArrSplit2[i5].split("\\s+")[1], strArrSplit[i5].split("\\s+")[1]);
                        arrayMap2.put("time", Long.valueOf(jNumberStringSubtract));
                        arrayList.add(arrayMap2);
                        j2 += jNumberStringSubtract;
                        i5++;
                        timeInStateData3 = timeInStateData4;
                        lastTimeInState = lastTimeInState;
                        i2 = 0;
                    }
                    timeInStateData = timeInStateData3;
                    timeInStateData2 = lastTimeInState;
                    arrayMap.put(TrackData.CPU_FREQ_TIME_IN_STATE, arrayList);
                    arrayMap.put(TrackData.CPU_FREQ_TIME_IN_STATE_TIME_TOTAL, Long.valueOf(j2));
                    sparseArray.append(i4, arrayMap);
                    i4++;
                }
            }
            i3++;
            timeInStateData3 = timeInStateData;
            lastTimeInState = timeInStateData2;
            i2 = 0;
        }
        return sparseArray;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initAndSaveProcCpuConfig(JSONObject jSONObject) {
        Log.d(TAG, "initAndSaveProcCpuConfig");
        if (jSONObject == null) {
            procCpuDisableIfNeeded();
            return;
        }
        String string = jSONObject.toString();
        boolean z2 = !string.equals(SimpleSettings.Misc.getString(this.mContext, BLUtils.CLOUD_PROC_CPU_TIME_IN_STATE, ""));
        if (z2) {
            SimpleSettings.Misc.putString(this.mContext, BLUtils.CLOUD_PROC_CPU_TIME_IN_STATE, string);
        }
        if (string.length() <= 0) {
            procCpuDisableIfNeeded();
            procScreenDisableIfNeeed();
            procCpuInfoDisableIfNeeed();
        } else {
            procCpuEnableIfNeeded();
            procScreenEnableIfNeeed();
            procCpuInfoEnableIfNeeed();
            if (z2) {
                updateProcSupportPkgs(jSONObject);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onMessage(Message message) throws Throwable {
        BatteryLifePolicy batteryLifePolicy;
        BatteryLifePolicy batteryLifePolicy2;
        int i2 = message.what;
        if (i2 == 1) {
            BatteryLifePolicy batteryLifePolicy3 = this.mPolicy;
            if (batteryLifePolicy3 != null) {
                batteryLifePolicy3.intoNight(true);
            }
            ProcCpuinfoManager procCpuinfoManager = this.mProcCpuinfoManager;
            if (procCpuinfoManager != null) {
                procCpuinfoManager.checkFile();
                return;
            }
            return;
        }
        if (i2 == 2) {
            BatteryLifePolicy batteryLifePolicy4 = this.mPolicy;
            if (batteryLifePolicy4 == null) {
                uploadData(true);
                Settings.System.putInt(this.mContext.getContentResolver(), FLAG_NIGHTCURRENT, 8);
                return;
            }
            boolean z2 = !batteryLifePolicy4.accounting();
            uploadData(z2);
            this.mPolicy.intoNight(false);
            if (z2) {
                Settings.System.putInt(this.mContext.getContentResolver(), FLAG_NIGHTCURRENT, 8);
                return;
            } else {
                checkNightCurrent();
                return;
            }
        }
        if (i2 == 3) {
            registerCloudObserver();
            return;
        }
        if (i2 == 4) {
            Log.d(TAG, " MSG_BL_STATUS_CHANGED. mIsEnabled = " + this.mIsEnabled);
            boolean z3 = this.mIsEnabled;
            if (z3 && this.mPolicy == null) {
                doInit();
            } else if (!z3 && this.mPolicy != null) {
                disable();
            }
            procAlarmEnableIfNeed();
            procJobEnableIfNeed();
            procSsruTransactionEnableIfNeed();
            return;
        }
        if (i2 == -22) {
            int i3 = message.arg1;
            if (i3 == 5 || (batteryLifePolicy2 = this.mPolicy) == null) {
                return;
            }
            batteryLifePolicy2.onPowerModeChanged(i3);
            return;
        }
        if (i2 == -10) {
            int i4 = message.arg1;
            if (message.arg2 != 1 || (batteryLifePolicy = this.mPolicy) == null) {
                return;
            }
            batteryLifePolicy.onForegroundChanged(i4);
        }
    }

    private void printHelp(PrintWriter printWriter) {
        printWriter.println("HELP: dumpsys activity service com.miui.powerkeeper/.PowerKeeperBackgroundService -bm 0 <args> ");
        printWriter.println("args:");
        printWriter.println("    -cloud: 云控配置信息");
        printWriter.println("    -a: Force upload data by sending a fake TIME message.");
        printWriter.println("    -m: dump RM related info.");
        printWriter.println("    -fe/-fd: Force enable/disable battery life.");
        printWriter.println("    -r: 读取系统缓存序列化文件内容");
        printWriter.println("    -cpu: CPU time_in_state info");
        printWriter.println("    --cpu: Process CPU time_in_state info");
        printWriter.println("    --screen: Process screen battery dry info");
        printWriter.println("");
        printWriter.println("    -h, --help: Show this help.");
    }

    private void procAlarmDisableIfNeed() {
        if (this.mProcAlarmCountManager != null) {
            Log.d(TAG, " procAlarmDisableIfNeed disable");
            this.mProcAlarmCountManager.destroy();
            this.mProcAlarmCountManager = null;
        }
    }

    private void procAlarmEnableIfNeed() {
        if (!this.mIsEnabled || !this.mIsAlarmEnable) {
            procAlarmDisableIfNeed();
        } else if (this.mProcAlarmCountManager == null) {
            this.mProcAlarmCountManager = new ProcAlarmCountManager(this.mContext);
        }
    }

    private void procAudioDisableIfNeeed() {
        ProcAudioPowerManager procAudioPowerManager = this.mProcAudioPowerManager;
        if (procAudioPowerManager != null) {
            procAudioPowerManager.destroy();
            this.mProcAudioPowerManager = null;
        }
    }

    private void procAudioEnableIfNeeded() {
        if (this.mIsEnabled && this.mProcAudioPowerManager == null) {
            this.mProcAudioPowerManager = new ProcAudioPowerManager(this.mContext);
        }
    }

    private void procCpuDisableIfNeeded() {
        ProcCpuTimeInStateManager procCpuTimeInStateManager = this.mCpuTimeInStateManager;
        if (procCpuTimeInStateManager != null) {
            procCpuTimeInStateManager.distroy();
            this.mCpuTimeInStateManager = null;
        }
    }

    private void procCpuEnableIfNeeded() {
        if (this.mIsEnabled && this.mWhetstoneInterfaceAvailable) {
            ProcCpuTimeInStateManager procCpuTimeInStateManager = this.mCpuTimeInStateManager;
            if (procCpuTimeInStateManager == null || procCpuTimeInStateManager.getSupportPkgs() == null) {
                ProcCpuTimeInStateManager procCpuTimeInStateManager2 = new ProcCpuTimeInStateManager(this.mContext);
                this.mCpuTimeInStateManager = procCpuTimeInStateManager2;
                if (procCpuTimeInStateManager2.isInterfaceEnabled()) {
                    return;
                }
                procCpuDisableIfNeeded();
                this.mWhetstoneInterfaceAvailable = false;
            }
        }
    }

    private void procCpuInfoDisableIfNeeed() {
        ProcCpuinfoManager procCpuinfoManager = this.mProcCpuinfoManager;
        if (procCpuinfoManager != null) {
            procCpuinfoManager.destroy();
            this.mProcCpuinfoManager = null;
        }
    }

    private void procCpuInfoEnableIfNeeed() {
        String str = Build.DEVICE;
        if ((str.equals("haotian") || str.equals("dada")) && this.mIsEnabled && this.mProcCpuinfoManager == null) {
            this.mProcCpuinfoManager = new ProcCpuinfoManager(this.mContext);
        }
    }

    private void procJobDisableIfNeed() {
        ProcJobSchedulerCountManager procJobSchedulerCountManager = this.mProcJobSchedulerCountManager;
        if (procJobSchedulerCountManager != null) {
            procJobSchedulerCountManager.destroy();
            this.mProcJobSchedulerCountManager = null;
            Log.d(TAG, " procJobDisableIfNeed disable");
        }
    }

    private void procJobEnableIfNeed() {
        if (!this.mIsJobEnable || !this.mIsEnabled) {
            procJobDisableIfNeed();
        } else if (this.mProcJobSchedulerCountManager == null) {
            this.mProcJobSchedulerCountManager = new ProcJobSchedulerCountManager(this.mContext);
        }
    }

    private void procScreenDisableIfNeeed() {
        ProcScreenPowerManager procScreenPowerManager = this.mProcScreenPowerManager;
        if (procScreenPowerManager != null) {
            procScreenPowerManager.destroy();
            this.mProcScreenPowerManager = null;
        }
    }

    private void procScreenEnableIfNeeed() {
        if (this.mIsEnabled && this.mProcScreenPowerManager == null) {
            this.mProcScreenPowerManager = new ProcScreenPowerManager(this.mContext);
        }
    }

    private void procSsruTransactionDisableIfNeed() {
        ProcSsruTransactionManager procSsruTransactionManager = this.mProcSsruTransactionManager;
        if (procSsruTransactionManager != null) {
            procSsruTransactionManager.destroy();
            this.mProcSsruTransactionManager = null;
            Log.d(TAG, " procSsruTransactionDisableIfNeed disable");
        }
    }

    private void procSsruTransactionEnableIfNeed() {
        if (!this.mIsEnabled || !this.mIsSsruTransactionEnable) {
            procSsruTransactionDisableIfNeed();
        } else if (this.mProcSsruTransactionManager == null) {
            this.mProcSsruTransactionManager = new ProcSsruTransactionManager(this.mContext);
        }
    }

    private void registerCloudObserver() {
        Log.d(TAG, "registerCloudObserver start.");
        b.a aVarP = b.a.p();
        int i2 = b.b.f20b;
        JSONObject jSONObjectL = aVarP.l(i2);
        if (jSONObjectL != null) {
            this.mIsEnabled = jSONObjectL.optBoolean("enabled", false);
            this.mIsAlarmEnable = jSONObjectL.optBoolean("enabled_alarm", false);
            this.mIsJobEnable = jSONObjectL.optBoolean("enabled_job", false);
            this.mIsSsruTransactionEnable = jSONObjectL.optBoolean("enabled_ssru", false);
        }
        b.a aVarP2 = b.a.p();
        int i3 = b.b.f22d;
        initAndSaveProcCpuConfig(aVarP2.l(i3));
        if (b.a.p().s(i2)) {
            Log.d(TAG, "Init, Use the cloud config, mIsEnabled=" + this.mIsEnabled);
            this.mHandler.sendEmptyMessage(4);
        } else {
            if (SimpleSettings.Misc.contains(this.mContext, BLUtils.CLOUD_BATTERY_LIFE)) {
                Log.d(TAG, "battery_life_do_log =" + SimpleSettings.Misc.getBoolean(this.mContext, BLUtils.CLOUD_BATTERY_LIFE, false));
            } else {
                Log.d(TAG, "local configuration don't contains battery_life_do_log");
            }
            this.mIsEnabled = SimpleSettings.Misc.getBoolean(this.mContext, BLUtils.CLOUD_BATTERY_LIFE, false);
            this.mIsAlarmEnable = SimpleSettings.Misc.getBoolean(this.mContext, BLUtils.CLOUD_ALARM_LIFE, false);
            this.mIsJobEnable = SimpleSettings.Misc.getBoolean(this.mContext, BLUtils.CLOUD_JOB_LIFE, false);
            this.mIsSsruTransactionEnable = SimpleSettings.Misc.getBoolean(this.mContext, BLUtils.CLOUD_SSRU_LIFE, false);
            if (this.mIsEnabled) {
                this.mHandler.sendEmptyMessage(4);
            }
            Log.d(TAG, "Init, Use the local config, mIsEnabled=" + this.mIsEnabled + " mIsAlarmEnable = " + this.mIsAlarmEnable + " mIsJobEnable = " + this.mIsJobEnable + " mIsSsruTransactionEnable = " + this.mIsSsruTransactionEnable);
        }
        if (b.a.p().s(i3)) {
            Log.d(TAG, "Init, proc_cpu_time_in_state Use the cloud config");
        } else if (SimpleSettings.Misc.contains(this.mContext, BLUtils.CLOUD_PROC_CPU_TIME_IN_STATE)) {
            String string = SimpleSettings.Misc.getString(this.mContext, BLUtils.CLOUD_PROC_CPU_TIME_IN_STATE, "");
            Log.d(TAG, "proc_cpu_time_in_state =" + string);
            try {
                initAndSaveProcCpuConfig(new JSONObject(string));
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
        } else {
            Log.d(TAG, "local configuration don't contains proc_cpu_time_in_state");
        }
        b.a.p().t(b.b.f20b, new b.a.d() { // from class: com.miui.powerkeeper.batterylife.BatteryLifeChecker.2
            @Override // b.a.d
            public void onChanged(JSONObject jSONObject) {
                Log.d(BatteryLifeChecker.TAG, "Cloud-Observer onChange - BL");
                BatteryLifeChecker.this.mIsEnabled = false;
                if (jSONObject != null) {
                    BatteryLifeChecker.this.mIsEnabled = jSONObject.optBoolean("enabled");
                    BatteryLifeChecker.this.mIsAlarmEnable = jSONObject.optBoolean("enabled_alarm", false);
                    BatteryLifeChecker.this.mIsJobEnable = jSONObject.optBoolean("enabled_job", false);
                    BatteryLifeChecker.this.mIsSsruTransactionEnable = jSONObject.optBoolean("enabled_ssru", false);
                    Log.d(BatteryLifeChecker.TAG, "Cloud-Observer onChanged mIsEnabled=" + BatteryLifeChecker.this.mIsEnabled + ", cloudData:" + jSONObject);
                    BLUtils.LOCAL_LOG.log("Use the cloud config, mIsEnabled=" + BatteryLifeChecker.this.mIsEnabled + ", json=" + jSONObject);
                } else if (!b.a.p().s(b.b.f20b)) {
                    BatteryLifeChecker batteryLifeChecker = BatteryLifeChecker.this;
                    batteryLifeChecker.mIsEnabled = SimpleSettings.Misc.getBoolean(batteryLifeChecker.mContext, BLUtils.CLOUD_BATTERY_LIFE, false);
                    BatteryLifeChecker batteryLifeChecker2 = BatteryLifeChecker.this;
                    batteryLifeChecker2.mIsAlarmEnable = SimpleSettings.Misc.getBoolean(batteryLifeChecker2.mContext, BLUtils.CLOUD_ALARM_LIFE, false);
                    BatteryLifeChecker batteryLifeChecker3 = BatteryLifeChecker.this;
                    batteryLifeChecker3.mIsJobEnable = SimpleSettings.Misc.getBoolean(batteryLifeChecker3.mContext, BLUtils.CLOUD_JOB_LIFE, false);
                    BatteryLifeChecker batteryLifeChecker4 = BatteryLifeChecker.this;
                    batteryLifeChecker4.mIsSsruTransactionEnable = SimpleSettings.Misc.getBoolean(batteryLifeChecker4.mContext, BLUtils.CLOUD_SSRU_LIFE, false);
                    Log.d(BatteryLifeChecker.TAG, "Cloud-Observer cloud config is deleted, choose the local config. mIsEnabled=" + BatteryLifeChecker.this.mIsEnabled + " mIsAlarmEnable = " + BatteryLifeChecker.this.mIsAlarmEnable + " mIsJobEnable = " + BatteryLifeChecker.this.mIsJobEnable + " mIsSsruTransactionEnable = " + BatteryLifeChecker.this.mIsSsruTransactionEnable);
                    BLUtils.LOCAL_LOG.log("Cloud config is deleted, use the local config, mIsEnabled=" + BatteryLifeChecker.this.mIsEnabled + " mIsAlarmEnable = " + BatteryLifeChecker.this.mIsAlarmEnable + " mIsJobEnable = " + BatteryLifeChecker.this.mIsJobEnable + " mIsSsruTransactionEnable = " + BatteryLifeChecker.this.mIsSsruTransactionEnable);
                }
                BatteryLifeChecker.this.mHandler.sendEmptyMessage(4);
            }
        });
        b.a.p().t(b.b.f22d, new b.a.d() { // from class: com.miui.powerkeeper.batterylife.BatteryLifeChecker.3
            @Override // b.a.d
            public void onChanged(JSONObject jSONObject) {
                Log.d(BatteryLifeChecker.TAG, "Cloud-Observer onChange - TIME_IN_STATE object = " + jSONObject);
                BatteryLifeChecker.this.initAndSaveProcCpuConfig(jSONObject);
            }
        });
    }

    private void registerReceiver() {
        if (this.mScreenChangedCallback != null) {
            return;
        }
        this.mScreenChangedCallback = new PowerKeeperInterface.f0(this.mHandler, this);
        PowerKeeperManager.getInstance().registerScreenChangedListener(this.mScreenChangedCallback);
        if (this.mBatteryChangedCallback != null) {
            return;
        }
        this.mBatteryChangedCallback = new PowerKeeperInterface.d(this.mHandler, this);
        PowerKeeperManager.getInstance().registerBatteryChangedListener(this.mBatteryChangedCallback);
        PowerKeeperManager.getInstance().getEventsAggregator().registerPowerModeListener(this.mHandler);
        PowerKeeperManager.getInstance().getEventsAggregator().registerForegroundActivityChangeListener(this.mHandler);
    }

    private static synchronized void setInstance(BatteryLifeChecker batteryLifeChecker) {
        synchronized (BatteryLifeChecker.class) {
            sInstance = batteryLifeChecker;
        }
    }

    private void unRegisterReceiver() {
        if (this.mScreenChangedCallback != null) {
            PowerKeeperManager.getInstance().unregisterScreenChangedListener(this.mScreenChangedCallback);
            this.mScreenChangedCallback.a();
            this.mScreenChangedCallback = null;
        }
        if (this.mBatteryChangedCallback != null) {
            PowerKeeperManager.getInstance().unregisterBatteryChangedListener(this.mBatteryChangedCallback);
            this.mBatteryChangedCallback.a();
            this.mBatteryChangedCallback = null;
        }
        PowerKeeperManager.getInstance().getEventsAggregator().unregisterPowerModeListener(this.mHandler);
        PowerKeeperManager.getInstance().getEventsAggregator().unregisterForegroundActivityChangeListener(this.mHandler);
    }

    private void updateProcSupportPkgs(JSONObject jSONObject) {
        Log.d(TAG, "updateProcSupportPkgs");
        if (jSONObject.has("pkgList_all")) {
            String strOptString = jSONObject.optString("pkgList_all");
            Log.d(TAG, "pkgList1:" + strOptString);
            if (PushServiceConstants.EXTENSION_ELE_NAME_ALL.equalsIgnoreCase(strOptString)) {
                ProcCpuTimeInStateManager procCpuTimeInStateManager = this.mCpuTimeInStateManager;
                if (procCpuTimeInStateManager != null) {
                    procCpuTimeInStateManager.setSupportPkgs(null, true);
                }
                ProcScreenPowerManager procScreenPowerManager = this.mProcScreenPowerManager;
                if (procScreenPowerManager != null) {
                    procScreenPowerManager.setSupportPkgs(null, true);
                }
            }
        } else {
            String strOptString2 = Build.IS_INTERNATIONAL_BUILD ? jSONObject.optString("pkgList_intl") : jSONObject.optString("pkgList");
            Log.d(TAG, "pkgList1:" + strOptString2);
            if (strOptString2.length() > 0) {
                String[] strArrSplit = "none".equalsIgnoreCase(strOptString2) ? null : strOptString2.split(";");
                ProcCpuTimeInStateManager procCpuTimeInStateManager2 = this.mCpuTimeInStateManager;
                if (procCpuTimeInStateManager2 != null) {
                    procCpuTimeInStateManager2.setSupportPkgs(strArrSplit, false);
                }
                ProcScreenPowerManager procScreenPowerManager2 = this.mProcScreenPowerManager;
                if (procScreenPowerManager2 != null) {
                    procScreenPowerManager2.setSupportPkgs(strArrSplit, false);
                }
            }
        }
        String strOptString3 = Build.IS_INTERNATIONAL_BUILD ? jSONObject.optString("pkgList_intl") : jSONObject.optString("pkgList");
        Log.d(TAG, "pkgList2:" + strOptString3);
        if (strOptString3.length() > 0) {
            String[] strArrSplit2 = "none".equalsIgnoreCase(strOptString3) ? null : strOptString3.split(";");
            ProcCpuinfoManager procCpuinfoManager = this.mProcCpuinfoManager;
            if (procCpuinfoManager != null) {
                procCpuinfoManager.setSupportPkgs(strArrSplit2);
            }
        }
    }

    private void uploadBatteryHistory() {
        if (this.mBatteryHistoryManager != null) {
            HashMap map = new HashMap();
            Map<String, Object> mapDoUploadBatteryStats = this.mBatteryHistoryManager.doUploadBatteryStats();
            map.put(TrackData.BATTERY_LIFE_VERSION, Float.valueOf(5.8f));
            map.put("battery_history", GsonUtils.getGson().toJson(mapDoUploadBatteryStats));
            map.put(TrackData.BATTERY_LIFE_SCREEN_ON_DURATION, Long.valueOf(getScreenOnDuration()));
            BatteryLifePolicy batteryLifePolicy = this.mPolicy;
            if (batteryLifePolicy != null) {
                map.put(TrackData.BATTERY_LIFE_BATTERY_CONSUMPTION, Double.valueOf(batteryLifePolicy.getTotalBatteryConsumption()));
                map.put(TrackData.BATTERY_LIFE_CHARGING_DURATION, Long.valueOf(this.mPolicy.getTotalChargingDuration() / 1000));
                map.put(TrackData.BATTERY_LIFE_ON_BATTERY_DURATION, Long.valueOf(this.mPolicy.getTotalOnBatteryDuaration() / 1000));
            } else {
                map.put(TrackData.BATTERY_LIFE_BATTERY_CONSUMPTION, 0);
                map.put(TrackData.BATTERY_LIFE_CHARGING_DURATION, 0);
                map.put(TrackData.BATTERY_LIFE_ON_BATTERY_DURATION, 0);
            }
            TrackerManager.getInstance(this.mContext).track(0, "battery_history", map);
            Log.d(TAG, "uploadBatteryHistory success.");
        }
    }

    /* JADX WARN: Code duplicated, block: B:39:0x027f  */
    /* JADX WARN: Code duplicated, block: B:41:0x029c  */
    /* JADX WARN: Code duplicated, block: B:42:0x02aa  */
    /* JADX WARN: Code duplicated, block: B:66:0x0313  */
    private void uploadBatteryLife() {
        int i2;
        ArrayList arrayList;
        ArrayMap arrayMap;
        ResourceMonitor resourceMonitor;
        ArrayList<Map<String, Object>> resourceData;
        ArrayList<Map<String, Object>> resourceData2;
        ArrayList<Map<String, Object>> resourceData3;
        int i3;
        BatteryLifePolicy batteryLifePolicy = this.mPolicy;
        if (batteryLifePolicy == null) {
            return;
        }
        BatteryData data = batteryLifePolicy.getData();
        if (data.mEstimateBatteryLifeDQ.size() <= 0) {
            Log.d(TAG, " uploadBatteryLife failed, data is null");
            return;
        }
        ArrayList arrayList2 = new ArrayList();
        for (MyBatterySipper myBatterySipper : this.mPolicy.getTopAppsManager().getUsageList()) {
            ArrayMap arrayMap2 = new ArrayMap();
            if (myBatterySipper.getForegroundTime() > 0 && myBatterySipper.getTotalPower() > 0.0d) {
                arrayMap2.put("package_name", BLUtils.getPackageForUid(this.mContext, myBatterySipper.getUid()));
                arrayMap2.put("app_name", BLUtils.getAppNameByUid(this.mContext, myBatterySipper.getUid()));
                arrayMap2.put(TrackData.APP_FG_TIME, Long.valueOf(myBatterySipper.getForegroundTime()));
                arrayMap2.put(TrackData.APP_TOTAL_FG_TIME, Long.valueOf(myBatterySipper.getTotalForegroundTime()));
                arrayMap2.put(TrackData.APP_FG_TOTAL_POWER, String.format(Locale.getDefault(), "%.2f", Double.valueOf(myBatterySipper.getTotalPower())));
                arrayList2.add(arrayMap2);
            }
        }
        BatteryLife.FluctuatingState fluctuatingState = this.mPolicy.getFluctuatingState();
        SparseArray sparseArray = new SparseArray();
        for (int i4 = 0; i4 < 6; i4++) {
            sparseArray.append(i4, null);
        }
        sparseArray.append(100, null);
        String deviceId = BSHelper.getDeviceId();
        for (BatteryLife.Data data2 : data.mEstimateBatteryLifeDQ.getLast().dataList) {
            Map arrayMap3 = (Map) sparseArray.get(data2.powerMode);
            if (arrayMap3 == null) {
                arrayMap3 = new ArrayMap();
                sparseArray.append(data2.powerMode, arrayMap3);
            }
            arrayMap3.put(TrackData.BATTERY_LIFE_SCREEN_ON, Integer.valueOf(data2.life[0]));
            arrayMap3.put(TrackData.BATTERY_LIFE_SCREEN_OFF, Integer.valueOf(data2.life[1]));
            arrayMap3.put(TrackData.BATTERY_LIFE_SCREEN_OFF_AT_NIGHT, Integer.valueOf(data2.life[3]));
            arrayMap3.put(TrackData.BATTERY_LIFE_COMP, Integer.valueOf(data2.life[4]));
            arrayMap3.put("battery_life_screen_on_d", Integer.valueOf(data2.lifeD[0]));
            arrayMap3.put("battery_life_screen_off_d", Integer.valueOf(data2.lifeD[1]));
            arrayMap3.put("battery_life_screen_off_at_night_d", Integer.valueOf(data2.lifeD[3]));
            arrayMap3.put("battery_life_comprehensive_d", Integer.valueOf(data2.lifeD[4]));
            arrayMap3.put(TrackData.BATTERY_LIFE_SCREEN_ON_DURATION, Long.valueOf(data2.duration[0]));
            arrayMap3.put(TrackData.BATTERY_LIFE_SCREEN_OFF_DURATION, Long.valueOf(data2.duration[1]));
            arrayMap3.put(TrackData.BATTERY_LIFE_SCREEN_OFF_AT_NIGHT_DURATION, Long.valueOf(data2.duration[3]));
            arrayMap3.put(TrackData.BATTERY_LIFE_SCREEN_ON_TOTAL_DURATION, Long.valueOf(data2.totalDuration[0]));
            arrayMap3.put(TrackData.BATTERY_LIFE_SCREEN_OFF_TOTAL_DURATION, Long.valueOf(data2.totalDuration[1]));
            arrayMap3.put(TrackData.BATTERY_LIFE_SCREEN_OFF_AT_NIGHT_TOTAL_DURATION, Long.valueOf(data2.totalDuration[3]));
            arrayMap3.put(TrackData.BATTERY_LIFE_SCREEN_ON_DRY, Double.valueOf(data2.batteryDry[0]));
            arrayMap3.put(TrackData.BATTERY_LIFE_SCREEN_OFF_DRY, Double.valueOf(data2.batteryDry[1]));
            arrayMap3.put(TrackData.BATTERY_LIFE_SCREEN_OFF_AT_NIGHT_DRY, Double.valueOf(data2.batteryDry[3]));
            arrayMap3.put(TrackData.BATTERY_LIFE_BATTERY_CONSUMPTION, Double.valueOf(this.mPolicy.getTotalBatteryConsumption()));
            arrayMap3.put(TrackData.BATTERY_LIFE_CHARGING_DURATION, Long.valueOf(this.mPolicy.getTotalChargingDuration() / 1000));
            arrayMap3.put(TrackData.BATTERY_LIFE_ON_BATTERY_DURATION, Long.valueOf(this.mPolicy.getTotalOnBatteryDuaration() / 1000));
            arrayMap3.put("power_mode", Integer.valueOf(data2.powerMode));
            arrayMap3.put(TrackData.BATTERY_CAPACITY, Integer.valueOf((int) BSHelper.getDesignedBatteryCapacity(this.mContext)));
            arrayMap3.put(TrackData.BATTERY_CHARGE_FULL, Integer.valueOf((int) BSHelper.getTotalBatteryCapacity(this.mContext)));
            if (BLUtils.isFlagSupport(2)) {
                arrayMap3.put(TrackData.BATTERY_LIFE_FLUCTUATING_STATE_COMP, Float.valueOf(fluctuatingState.fluctuatingStateComp[data2.powerMode]));
                arrayMap3.put(TrackData.BATTERY_LIFE_FLUCTUATING_STATE_OFF, Float.valueOf(fluctuatingState.fluctuatingStateOff[data2.powerMode]));
                arrayMap3.put(TrackData.BATTERY_LIFE_FLUCTUATING_STATE_ON, Float.valueOf(fluctuatingState.fluctuatingStateOn[data2.powerMode]));
            }
            int i5 = data2.powerMode;
            if (i5 != 2) {
                i2 = 100;
                if (i5 == 100) {
                }
                if (data2.powerMode == i2) {
                    arrayMap3.put(TrackData.CAPACITY_ON_SAVE_BATTERY_FIRST, Integer.valueOf(this.mPolicy.getCapacityOnSaveBattery()));
                    if (Settings.System.getInt(this.mContext.getContentResolver(), "is_smart_fps", 0) == 0) {
                        i3 = Settings.Secure.getInt(this.mContext.getContentResolver(), "user_refresh_rate", -1);
                    } else {
                        i3 = 0;
                    }
                    arrayMap3.put(TrackData.REFRESH_RATE, Integer.valueOf(i3));
                }
                if (data2.powerMode == 100 && (resourceMonitor = this.mResourceMonitor) != null) {
                    resourceData = resourceMonitor.getResourceData(1);
                    if (resourceData != null && resourceData.size() > 0) {
                        arrayMap3.put(TrackData.SS_TRACK_NAME, resourceData);
                    }
                    resourceData2 = this.mResourceMonitor.getResourceData(2);
                    if (resourceData2 != null && resourceData2.size() > 0) {
                        arrayMap3.put(TrackData.DI_TRACK_NAME, resourceData2);
                    }
                    resourceData3 = this.mResourceMonitor.getResourceData(4);
                    if (resourceData3 != null && resourceData3.size() > 0) {
                        arrayMap3.put(TrackData.SM_TRACK_NAME, resourceData3);
                    }
                }
                arrayMap3.put(TrackData.BATTERY_LIFE_VERSION, Float.valueOf(5.8f));
                arrayList = new ArrayList();
                for (BatteryLife.MatchingData matchingData : data2.matchingDatas) {
                    arrayMap = new ArrayMap();
                    if (matchingData.duration > 0 && matchingData.consumption > 0.0d) {
                        arrayMap.put(TrackData.MATCHING_DATA_TYPE, Integer.valueOf(matchingData.type));
                        arrayMap.put(TrackData.MATCHING_DATA_DURATION, Long.valueOf(matchingData.duration));
                        arrayMap.put(TrackData.MATCHING_DATA_CONSUMPTION, Double.valueOf(matchingData.consumption));
                        arrayMap.put(TrackData.MATCHING_DATA_STARTTIME, Long.valueOf(matchingData.startTime));
                        arrayMap.put(TrackData.MATCHING_DATA_ENDTIME, Long.valueOf(matchingData.endTime));
                        arrayMap.put(TrackData.MATCHING_DATA_STARTCAP, Double.valueOf(matchingData.startCap));
                        arrayMap.put(TrackData.MATCHING_DATA_ENDCAP, Double.valueOf(matchingData.endCap));
                        arrayMap.put(TrackData.MATCHING_DATA_STARTCAPLEVEL, Integer.valueOf(matchingData.startCapLevel));
                        arrayMap.put(TrackData.MATCHING_DATA_ENDCAPLEVEL, Integer.valueOf(matchingData.endCapLevel));
                        arrayMap.put(TrackData.MATCHING_DATA_STARTVOLTAGE, Long.valueOf(matchingData.startVoltage));
                        arrayMap.put(TrackData.MATCHING_DATA_ENDVOLTAGE, Long.valueOf(matchingData.endVoltage));
                        arrayList.add(arrayMap);
                    }
                }
                arrayMap3.put(TrackData.MATCHING_DATA, arrayList);
                arrayMap3.put(TrackData.SPEED_MODE_STATE, Integer.valueOf(Settings.System.getInt(this.mContext.getContentResolver(), "speed_mode", 0)));
                arrayMap3.put(TrackData.DEVICE_ID, deviceId);
            } else {
                i2 = 100;
            }
            arrayMap3.put(TrackData.TOP_APPS, arrayList2);
            if (data2.powerMode == i2) {
                arrayMap3.put(TrackData.CAPACITY_ON_SAVE_BATTERY_FIRST, Integer.valueOf(this.mPolicy.getCapacityOnSaveBattery()));
                if (Settings.System.getInt(this.mContext.getContentResolver(), "is_smart_fps", 0) == 0) {
                    i3 = Settings.Secure.getInt(this.mContext.getContentResolver(), "user_refresh_rate", -1);
                } else {
                    i3 = 0;
                }
                arrayMap3.put(TrackData.REFRESH_RATE, Integer.valueOf(i3));
            }
            if (data2.powerMode == 100) {
                resourceData = resourceMonitor.getResourceData(1);
                if (resourceData != null) {
                    arrayMap3.put(TrackData.SS_TRACK_NAME, resourceData);
                }
                resourceData2 = this.mResourceMonitor.getResourceData(2);
                if (resourceData2 != null) {
                    arrayMap3.put(TrackData.DI_TRACK_NAME, resourceData2);
                }
                resourceData3 = this.mResourceMonitor.getResourceData(4);
                if (resourceData3 != null) {
                    arrayMap3.put(TrackData.SM_TRACK_NAME, resourceData3);
                }
            }
            arrayMap3.put(TrackData.BATTERY_LIFE_VERSION, Float.valueOf(5.8f));
            arrayList = new ArrayList();
            while (r7.hasNext()) {
                arrayMap = new ArrayMap();
                if (matchingData.duration > 0) {
                    arrayMap.put(TrackData.MATCHING_DATA_TYPE, Integer.valueOf(matchingData.type));
                    arrayMap.put(TrackData.MATCHING_DATA_DURATION, Long.valueOf(matchingData.duration));
                    arrayMap.put(TrackData.MATCHING_DATA_CONSUMPTION, Double.valueOf(matchingData.consumption));
                    arrayMap.put(TrackData.MATCHING_DATA_STARTTIME, Long.valueOf(matchingData.startTime));
                    arrayMap.put(TrackData.MATCHING_DATA_ENDTIME, Long.valueOf(matchingData.endTime));
                    arrayMap.put(TrackData.MATCHING_DATA_STARTCAP, Double.valueOf(matchingData.startCap));
                    arrayMap.put(TrackData.MATCHING_DATA_ENDCAP, Double.valueOf(matchingData.endCap));
                    arrayMap.put(TrackData.MATCHING_DATA_STARTCAPLEVEL, Integer.valueOf(matchingData.startCapLevel));
                    arrayMap.put(TrackData.MATCHING_DATA_ENDCAPLEVEL, Integer.valueOf(matchingData.endCapLevel));
                    arrayMap.put(TrackData.MATCHING_DATA_STARTVOLTAGE, Long.valueOf(matchingData.startVoltage));
                    arrayMap.put(TrackData.MATCHING_DATA_ENDVOLTAGE, Long.valueOf(matchingData.endVoltage));
                    arrayList.add(arrayMap);
                }
            }
            arrayMap3.put(TrackData.MATCHING_DATA, arrayList);
            arrayMap3.put(TrackData.SPEED_MODE_STATE, Integer.valueOf(Settings.System.getInt(this.mContext.getContentResolver(), "speed_mode", 0)));
            arrayMap3.put(TrackData.DEVICE_ID, deviceId);
        }
        if (BLUtils.isFlagSupport(2)) {
            for (BatteryLife.Data data3 : this.mPolicy.getHistoricalAverage().dataList) {
                Map arrayMap4 = (Map) sparseArray.get(data3.powerMode);
                if (arrayMap4 == null) {
                    arrayMap4 = new ArrayMap();
                    sparseArray.append(data3.powerMode, arrayMap4);
                    arrayMap4.put("power_mode", Integer.valueOf(data3.powerMode));
                }
                arrayMap4.put(TrackData.HISTORICAL_AVG_SCREEN_ON, Integer.valueOf(data3.life[0]));
                arrayMap4.put(TrackData.HISTORICAL_AVG_SCREEN_OFF, Integer.valueOf(data3.life[1]));
                arrayMap4.put(TrackData.HISTORICAL_AVG_COMP, Integer.valueOf(data3.life[4]));
            }
        }
        for (int i6 = 0; i6 < sparseArray.size(); i6++) {
            if (sparseArray.valueAt(i6) != null) {
                TrackerManager.getInstance(this.mContext).track(0, "battery_life", (Map<String, Object>) sparseArray.valueAt(i6));
            }
        }
        ResourceMonitor resourceMonitor2 = this.mResourceMonitor;
        if (resourceMonitor2 != null) {
            resourceMonitor2.resetAllData();
        }
        BLUtils.LOCAL_LOG.log("Uploade the battery life data Success.");
        Log.d(TAG, "uploadBatteryLife success.");
    }

    private void uploadCpuFreq() {
        if (this.mPolicy == null) {
            return;
        }
        SparseArray<Map<String, Object>> timeInState = getTimeInState();
        if (timeInState.size() == 0) {
            Log.d(TAG, " uploadCpuFreq failed, data is null");
            return;
        }
        for (int i2 = 0; i2 < timeInState.size(); i2++) {
            Map<String, Object> mapValueAt = timeInState.valueAt(i2);
            mapValueAt.put(TrackData.BATTERY_LIFE_VERSION, Float.valueOf(5.8f));
            mapValueAt.put(TrackData.BATTERY_LIFE_SCREEN_ON_DURATION, Long.valueOf(getScreenOnDuration()));
            mapValueAt.put(TrackData.BATTERY_LIFE_BATTERY_CONSUMPTION, Double.valueOf(this.mPolicy.getTotalBatteryConsumption()));
            mapValueAt.put(TrackData.BATTERY_LIFE_CHARGING_DURATION, Long.valueOf(this.mPolicy.getTotalChargingDuration() / 1000));
            mapValueAt.put(TrackData.BATTERY_LIFE_ON_BATTERY_DURATION, Long.valueOf(this.mPolicy.getTotalOnBatteryDuaration() / 1000));
            TrackerManager.getInstance(this.mContext).track(0, "cpu_freq_state", mapValueAt);
        }
        Log.d(TAG, "uploadCpuFreq success.");
        this.mPolicy.resetTimeInState();
    }

    private void uploadData(boolean z2) throws Throwable {
        if (!Utils.isUserExperienceAndPrivacyAllowed(this.mContext)) {
            Log.d(TAG, "uploadData, Privacy forbid.");
            return;
        }
        Log.d(TAG, "Do uploadData.");
        if (this.mPolicy == null || !this.mIsEnabled) {
            Log.d(TAG, "uploadData, BatteryLifePolicy is not enabled.");
            return;
        }
        if (!z2) {
            uploadBatteryLife();
        }
        uploadCpuFreq();
        SharedPrefUtil.getInstance(this.mContext, "battery_life").save("in_time_span_time", System.currentTimeMillis());
        ProcCpuTimeInStateManager procCpuTimeInStateManager = this.mCpuTimeInStateManager;
        if (procCpuTimeInStateManager == null || !procCpuTimeInStateManager.isInterfaceEnabled()) {
            Log.d(TAG, "uploadData, ProcCpuTimeInState is not enabled.");
        } else {
            uploadProcCpuTimes();
            uploadProcCpuGpuTimes();
        }
        if (this.mProcScreenPowerManager != null) {
            uploadProcScreenPower();
        }
        if (this.mProcAlarmCountManager != null || this.mProcJobSchedulerCountManager != null || this.mProcSsruTransactionManager != null) {
            uploadSsru();
        }
        if (this.mProcAudioPowerManager != null) {
            uploadProcAudioPower();
        }
        if (this.mProcCpuinfoManager != null) {
            uploadProcCpuinfo();
        }
        if (this.mBatteryHistoryManager != null) {
            uploadBatteryHistory();
        }
    }

    private void uploadProcAudioPower() {
        if (this.mProcAudioPowerManager != null) {
            HashMap map = new HashMap();
            List<AppAudioData> listDoUploadAppAudioData = this.mProcAudioPowerManager.doUploadAppAudioData();
            map.put(TrackData.BATTERY_LIFE_VERSION, Float.valueOf(5.8f));
            map.put(TrackData.PROC_AUDIO_STATS_APPS, GsonUtils.getGson().toJson(listDoUploadAppAudioData));
            map.put(TrackData.BATTERY_LIFE_SCREEN_ON_DURATION, Long.valueOf(getScreenOnDuration()));
            BatteryLifePolicy batteryLifePolicy = this.mPolicy;
            if (batteryLifePolicy != null) {
                map.put(TrackData.BATTERY_LIFE_BATTERY_CONSUMPTION, Double.valueOf(batteryLifePolicy.getTotalBatteryConsumption()));
                map.put(TrackData.BATTERY_LIFE_CHARGING_DURATION, Long.valueOf(this.mPolicy.getTotalChargingDuration() / 1000));
                map.put(TrackData.BATTERY_LIFE_ON_BATTERY_DURATION, Long.valueOf(this.mPolicy.getTotalOnBatteryDuaration() / 1000));
            } else {
                map.put(TrackData.BATTERY_LIFE_BATTERY_CONSUMPTION, 0);
                map.put(TrackData.BATTERY_LIFE_CHARGING_DURATION, 0);
                map.put(TrackData.BATTERY_LIFE_ON_BATTERY_DURATION, 0);
            }
            TrackerManager.getInstance(this.mContext).track(0, TrackerConfig.EVENT_PROC_AUDIO_TIME_STATS, map);
            this.mProcAudioPowerManager.resetAllData();
            Log.d(TAG, "uploadProcAudio success.");
        }
    }

    private void uploadProcCpuGpuTimes() throws Throwable {
        if (this.mCpuTimeInStateManager == null) {
            return;
        }
        String[] availableGpuFreqs = CpuStatus.getAvailableGpuFreqs();
        HashMap map = new HashMap();
        List<CpuTimeStatsData> procCpuGpuTimes = this.mCpuTimeInStateManager.getProcCpuGpuTimes(availableGpuFreqs);
        ArrayList<Object> deviceGpuTimes = this.mCpuTimeInStateManager.getDeviceGpuTimes(availableGpuFreqs);
        map.put(TrackData.BATTERY_LIFE_VERSION, Float.valueOf(5.8f));
        map.put(TrackData.PROC_STATS_APPS, GsonUtils.getGson().toJson(procCpuGpuTimes));
        map.put(TrackData.DEVICE_GPU_STATS, deviceGpuTimes);
        map.put(TrackData.BATTERY_LIFE_SCREEN_ON_DURATION, Long.valueOf(getScreenOnDuration()));
        BatteryLifePolicy batteryLifePolicy = this.mPolicy;
        if (batteryLifePolicy != null) {
            map.put(TrackData.BATTERY_LIFE_BATTERY_CONSUMPTION, Double.valueOf(batteryLifePolicy.getTotalBatteryConsumption()));
            map.put(TrackData.BATTERY_LIFE_CHARGING_DURATION, Long.valueOf(this.mPolicy.getTotalChargingDuration() / 1000));
            map.put(TrackData.BATTERY_LIFE_ON_BATTERY_DURATION, Long.valueOf(this.mPolicy.getTotalOnBatteryDuaration() / 1000));
        } else {
            map.put(TrackData.BATTERY_LIFE_BATTERY_CONSUMPTION, 0);
            map.put(TrackData.BATTERY_LIFE_CHARGING_DURATION, 0);
            map.put(TrackData.BATTERY_LIFE_ON_BATTERY_DURATION, 0);
        }
        TrackerManager.getInstance(this.mContext).track(0, TrackerConfig.EVENT_PROC_CPU_TIME_STATS, map);
        this.mCpuTimeInStateManager.resetAllData();
    }

    private void uploadProcCpuTimes() {
        ProcCpuTimeInStateManager procCpuTimeInStateManager = this.mCpuTimeInStateManager;
        if (procCpuTimeInStateManager == null) {
            return;
        }
        SparseArray<Map<String, Object>> procCpuTimes = procCpuTimeInStateManager.getProcCpuTimes();
        if (procCpuTimes.size() == 0) {
            Log.d(TAG, " uploadProcCpuTimes failed, data is null");
            return;
        }
        for (int i2 = 0; i2 < procCpuTimes.size(); i2++) {
            TrackerManager.getInstance(this.mContext).track(0, "app_cpu_time_in_state", procCpuTimes.valueAt(i2));
        }
    }

    private void uploadProcCpuinfo() {
        if (this.mProcCpuinfoManager != null) {
            HashMap map = new HashMap();
            List<AppCpuinfoData> listDoUploadAppCpuinfoData = this.mProcCpuinfoManager.doUploadAppCpuinfoData();
            map.put(TrackData.BATTERY_LIFE_VERSION, Float.valueOf(5.8f));
            map.put(TrackData.PROC_CPUINFO_APPS, GsonUtils.getGson().toJson(listDoUploadAppCpuinfoData));
            map.put(TrackData.BATTERY_LIFE_SCREEN_ON_DURATION, Long.valueOf(getScreenOnDuration()));
            BatteryLifePolicy batteryLifePolicy = this.mPolicy;
            if (batteryLifePolicy != null) {
                map.put(TrackData.BATTERY_LIFE_BATTERY_CONSUMPTION, Double.valueOf(batteryLifePolicy.getTotalBatteryConsumption()));
                map.put(TrackData.BATTERY_LIFE_CHARGING_DURATION, Long.valueOf(this.mPolicy.getTotalChargingDuration() / 1000));
                map.put(TrackData.BATTERY_LIFE_ON_BATTERY_DURATION, Long.valueOf(this.mPolicy.getTotalOnBatteryDuaration() / 1000));
            } else {
                map.put(TrackData.BATTERY_LIFE_BATTERY_CONSUMPTION, 0);
                map.put(TrackData.BATTERY_LIFE_CHARGING_DURATION, 0);
                map.put(TrackData.BATTERY_LIFE_ON_BATTERY_DURATION, 0);
            }
            TrackerManager.getInstance(this.mContext).track(0, TrackerConfig.EVENT_PROC_CPUINFO, map);
            this.mProcCpuinfoManager.resetAllData();
            Log.d(TAG, "uploadProcCpuinfo success.");
        }
    }

    private void uploadProcScreenPower() {
        if (this.mProcScreenPowerManager != null) {
            HashMap map = new HashMap();
            List<AppScreenPowerData> listDoUploadAppScreenPower = this.mProcScreenPowerManager.doUploadAppScreenPower();
            map.put(TrackData.BATTERY_LIFE_VERSION, Float.valueOf(5.8f));
            map.put(TrackData.PROC_SCREEN_STATS_APPS, GsonUtils.getGson().toJson(listDoUploadAppScreenPower));
            map.put(TrackData.BATTERY_LIFE_SCREEN_ON_DURATION, Long.valueOf(getScreenOnDuration()));
            BatteryLifePolicy batteryLifePolicy = this.mPolicy;
            if (batteryLifePolicy != null) {
                map.put(TrackData.BATTERY_LIFE_BATTERY_CONSUMPTION, Double.valueOf(batteryLifePolicy.getTotalBatteryConsumption()));
                map.put(TrackData.BATTERY_LIFE_CHARGING_DURATION, Long.valueOf(this.mPolicy.getTotalChargingDuration() / 1000));
                map.put(TrackData.BATTERY_LIFE_ON_BATTERY_DURATION, Long.valueOf(this.mPolicy.getTotalOnBatteryDuaration() / 1000));
            } else {
                map.put(TrackData.BATTERY_LIFE_BATTERY_CONSUMPTION, 0);
                map.put(TrackData.BATTERY_LIFE_CHARGING_DURATION, 0);
                map.put(TrackData.BATTERY_LIFE_ON_BATTERY_DURATION, 0);
            }
            TrackerManager.getInstance(this.mContext).track(0, TrackerConfig.EVENT_PROC_SCREEN_TIME_STATS, map);
            this.mProcScreenPowerManager.resetAllData();
        }
    }

    private void uploadSsru() {
        ArrayMap arrayMap = new ArrayMap();
        ProcAlarmCountManager procAlarmCountManager = this.mProcAlarmCountManager;
        if (procAlarmCountManager != null) {
            arrayMap.put(TrackData.PROC_ALARM_COUNT, GsonUtils.getGson().toJson(procAlarmCountManager.sortDataToList()));
        }
        ProcJobSchedulerCountManager procJobSchedulerCountManager = this.mProcJobSchedulerCountManager;
        if (procJobSchedulerCountManager != null) {
            arrayMap.put(TrackData.PROC_JOBSCHEDULER_COUNT, GsonUtils.getGson().toJson(procJobSchedulerCountManager.sortDataToList()));
        }
        ProcSsruTransactionManager procSsruTransactionManager = this.mProcSsruTransactionManager;
        if (procSsruTransactionManager != null) {
            arrayMap.put(TrackData.PROC_SSRU_TRANSACTION_COUNT, GsonUtils.getGson().toJson(procSsruTransactionManager.getTransactionData()));
            arrayMap.put(TrackData.PROC_SSRU_SYSTEM_COUNT, GsonUtils.getGson().toJson(this.mProcSsruTransactionManager.getSystemData()));
        }
        arrayMap.put(TrackData.BATTERY_LIFE_VERSION, Float.valueOf(5.8f));
        arrayMap.put(TrackData.BATTERY_LIFE_SCREEN_ON_DURATION, Long.valueOf(getScreenOnDuration()));
        BatteryLifePolicy batteryLifePolicy = this.mPolicy;
        if (batteryLifePolicy != null) {
            arrayMap.put(TrackData.BATTERY_LIFE_BATTERY_CONSUMPTION, Double.valueOf(batteryLifePolicy.getTotalBatteryConsumption()));
            arrayMap.put(TrackData.BATTERY_LIFE_CHARGING_DURATION, Long.valueOf(this.mPolicy.getTotalChargingDuration() / 1000));
            arrayMap.put(TrackData.BATTERY_LIFE_ON_BATTERY_DURATION, Long.valueOf(this.mPolicy.getTotalOnBatteryDuaration() / 1000));
        } else {
            arrayMap.put(TrackData.BATTERY_LIFE_BATTERY_CONSUMPTION, 0);
            arrayMap.put(TrackData.BATTERY_LIFE_CHARGING_DURATION, 0);
            arrayMap.put(TrackData.BATTERY_LIFE_ON_BATTERY_DURATION, 0);
        }
        TrackerManager.getInstance(this.mContext).track(0, TrackerConfig.EVENT_SSRU, arrayMap);
        ProcAlarmCountManager procAlarmCountManager2 = this.mProcAlarmCountManager;
        if (procAlarmCountManager2 != null) {
            procAlarmCountManager2.resetAllData();
        }
        ProcJobSchedulerCountManager procJobSchedulerCountManager2 = this.mProcJobSchedulerCountManager;
        if (procJobSchedulerCountManager2 != null) {
            procJobSchedulerCountManager2.resetAllData();
        }
        ProcSsruTransactionManager procSsruTransactionManager2 = this.mProcSsruTransactionManager;
        if (procSsruTransactionManager2 != null) {
            procSsruTransactionManager2.resetAllData();
        }
    }

    public void destory() {
        Log.d(TAG, " destory.");
        disable();
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:68:0x0135  */
    public void dump(PrintWriter printWriter, String[] strArr) {
        byte b2 = 4;
        if (strArr != null && strArr.length > 0) {
            if (strArr[0].equals("-fe")) {
                this.mIsEnabled = true;
                this.mHandler.sendEmptyMessage(4);
                return;
            } else if (strArr[0].equals("-fd")) {
                this.mIsEnabled = false;
                this.mHandler.sendEmptyMessage(4);
                return;
            }
        }
        printWriter.println("Related Information about Battery Life--------------");
        printWriter.println();
        printWriter.println("Battery Life version:5.8");
        if (Build.IS_TABLET && !MiuiMultiDisplayTypeInfo.isFoldDevice()) {
            String oaid = IdentifierManager.getOAID(this.mContext);
            if (TextUtils.isEmpty(oaid)) {
                printWriter.println("oaid is null");
            } else {
                printWriter.println("oaid:" + oaid);
            }
        }
        if (!this.mIsEnabled) {
            if (strArr != null && strArr.length > 0 && strArr[0].equals("-cloud")) {
                printWriter.println("feature is enabled: " + SimpleSettings.Misc.getBoolean(this.mContext, BLUtils.CLOUD_BATTERY_LIFE, false));
                printWriter.println("config devices: " + CloudFunctionConfig.getFunctionParameters(this.mContext, BLUtils.CLOUD_BATTERY_LIFE, "").trim());
            }
            printWriter.println("dump, Feature is not enabled.");
            return;
        }
        if (strArr != null && strArr.length > 0) {
            String str = strArr[0];
            switch (str.hashCode()) {
                case 1492:
                    if (!str.equals("-a")) {
                        b2 = -1;
                    } else {
                        b2 = 1;
                    }
                    break;
                case 1497:
                    if (!str.equals("-f")) {
                        b2 = -1;
                    } else {
                        b2 = 2;
                    }
                    break;
                case 1499:
                    if (!str.equals("-h")) {
                        b2 = -1;
                    } else {
                        b2 = 3;
                    }
                    break;
                case 1504:
                    if (!str.equals("-m")) {
                        b2 = -1;
                    } else {
                        b2 = 6;
                    }
                    break;
                case 1509:
                    if (!str.equals("-r")) {
                        b2 = -1;
                    } else {
                        b2 = 7;
                    }
                    break;
                case 1439323:
                    if (!str.equals("-cpu")) {
                        b2 = -1;
                    } else {
                        b2 = 5;
                    }
                    break;
                case 42997768:
                    if (!str.equals("--cpu")) {
                        b2 = -1;
                    } else {
                        b2 = 8;
                    }
                    break;
                case 1333069025:
                    if (!str.equals("--help")) {
                        b2 = -1;
                    }
                    break;
                case 1383068200:
                    if (!str.equals("-cloud")) {
                        b2 = -1;
                    } else {
                        b2 = 0;
                    }
                    break;
                case 1492323852:
                    if (!str.equals("--screen")) {
                        b2 = -1;
                    } else {
                        b2 = 9;
                    }
                    break;
                default:
                    b2 = -1;
                    break;
            }
            switch (b2) {
                case 0:
                    printWriter.println("Cloud battery life is enabled: " + SimpleSettings.Misc.getBoolean(this.mContext, BLUtils.CLOUD_BATTERY_LIFE, false));
                    printWriter.println("feature is enabled: " + this.mIsEnabled);
                    printWriter.println("support devices: " + CloudFunctionConfig.getFunctionParameters(this.mContext, BLUtils.CLOUD_BATTERY_LIFE, "").trim());
                    try {
                        JSONObject jSONObject = new JSONObject(SimpleSettings.Misc.getString(this.mContext, BLUtils.CLOUD_PROC_CPU_TIME_IN_STATE, ""));
                        if (jSONObject.has("pkgList_all")) {
                            String strOptString = jSONObject.optString("pkgList_all");
                            if (strOptString.length() > 0 && PushServiceConstants.EXTENSION_ELE_NAME_ALL.equalsIgnoreCase(strOptString)) {
                                printWriter.println("support apps: " + strOptString);
                            }
                        } else if (Build.IS_INTERNATIONAL_BUILD) {
                            printWriter.println("support apps: " + jSONObject.getString("pkgList_intl"));
                        } else {
                            printWriter.println("support apps: " + jSONObject.getString("pkgList"));
                        }
                    } catch (JSONException e2) {
                        e2.printStackTrace();
                        return;
                    }
                    break;
                case 1:
                    this.mHandler.sendEmptyMessage(2);
                    break;
                case 2:
                    printWriter.println("make exception to restart.");
                    printWriter.println("make exception to restart." + new int[]{1, 2}[5]);
                    break;
                case 3:
                case 4:
                    printHelp(printWriter);
                    break;
                case 5:
                    printWriter.println("cpuFreq: \n" + GsonUtils.getGson().toJson(getTimeInState()));
                    break;
                case 6:
                    ResourceMonitor resourceMonitor = this.mResourceMonitor;
                    if (resourceMonitor != null) {
                        resourceMonitor.dump(printWriter, (String[]) Arrays.copyOfRange(strArr, 1, strArr.length));
                    }
                    break;
                case 7:
                    BatteryLifePolicy batteryLifePolicy = this.mPolicy;
                    if (batteryLifePolicy == null) {
                        printWriter.println("Don't support!");
                    } else {
                        BatteryData batteryData = (BatteryData) BLUtils.readFile(batteryLifePolicy.getFilePath());
                        if (batteryData != null) {
                            printWriter.println(batteryData.toString());
                        }
                    }
                    break;
                case 8:
                    ProcCpuTimeInStateManager procCpuTimeInStateManager = this.mCpuTimeInStateManager;
                    if (procCpuTimeInStateManager != null) {
                        procCpuTimeInStateManager.dump(printWriter);
                    }
                    break;
                case 9:
                    ProcScreenPowerManager procScreenPowerManager = this.mProcScreenPowerManager;
                    if (procScreenPowerManager != null) {
                        procScreenPowerManager.dump(printWriter);
                    }
                    break;
            }
            return;
        }
        BatteryLifePolicy batteryLifePolicy2 = this.mPolicy;
        if (batteryLifePolicy2 != null) {
            printWriter.print(batteryLifePolicy2.dump());
        }
        ProcAudioPowerManager procAudioPowerManager = this.mProcAudioPowerManager;
        if (procAudioPowerManager != null) {
            printWriter.println(procAudioPowerManager.dump());
        }
        ProcCpuinfoManager procCpuinfoManager = this.mProcCpuinfoManager;
        if (procCpuinfoManager != null) {
            printWriter.print(procCpuinfoManager.dump());
        }
        printWriter.println("mIsJobEnable = " + this.mIsJobEnable + " mIsAlarmEnable = " + this.mIsAlarmEnable);
        if (this.mProcAlarmCountManager != null) {
            printWriter.println("---- Alarm Data----");
            printWriter.println(this.mProcAlarmCountManager.dump());
        }
        ProcJobSchedulerCountManager procJobSchedulerCountManager = this.mProcJobSchedulerCountManager;
        if (procJobSchedulerCountManager != null) {
            printWriter.print(procJobSchedulerCountManager.dump());
        }
        if (this.mProcSsruTransactionManager != null) {
            printWriter.println("----Ssru transactions Data----");
            printWriter.print(this.mProcSsruTransactionManager.dump());
            List<AppTransactionData> transactionData = this.mProcSsruTransactionManager.getTransactionData();
            SsruSystemData systemData = this.mProcSsruTransactionManager.getSystemData();
            Iterator<AppTransactionData> it = transactionData.iterator();
            while (it.hasNext()) {
                printWriter.println(it.next().toString());
            }
            if (systemData != null) {
                printWriter.println("systemData:");
                printWriter.println(systemData.toString());
            } else {
                printWriter.println("systemData: null");
            }
        }
        if (this.mResourceMonitor != null) {
            printWriter.println("----RM start----");
            this.mResourceMonitor.dump(printWriter, strArr);
            printWriter.println("----RM end----");
        }
        if (this.mBatteryHistoryManager != null) {
            printWriter.println("----Battery History----");
            printWriter.println(this.mBatteryHistoryManager.refreshStats());
        }
    }

    public BatteryLifePolicy getPolicy() {
        return this.mPolicy;
    }

    public ProcAudioPowerManager getProcAudioPowerManager() {
        return this.mProcAudioPowerManager;
    }

    @Override // com.miui.powerkeeper.TimeSpan.a
    public void inTimeSpan() {
        Log.d(TAG, " inTimeSpan. ");
        this.mHandler.sendEmptyMessage(1);
    }

    /* JADX WARN: Code duplicated, block: B:10:0x0012  */
    @Override // com.miui.powerkeeper.PowerKeeperInterface.m
    public void onBatteryChanged(int i2, int i3, int i4, int i5, int i6, int i7) {
        boolean z2;
        if (!this.mIsEnabled) {
            Log.d(TAG, "onBatteryChanged, Feature is not enabled.");
            return;
        }
        if (i6 == 0) {
            z2 = i2 != 1;
        }
        BatteryLifePolicy batteryLifePolicy = this.mPolicy;
        if (batteryLifePolicy != null) {
            batteryLifePolicy.onBatteryChanged(z2, i4);
        }
        ProcScreenPowerManager procScreenPowerManager = this.mProcScreenPowerManager;
        if (procScreenPowerManager != null) {
            procScreenPowerManager.onBatteryChanged(z2);
        }
        ProcAudioPowerManager procAudioPowerManager = this.mProcAudioPowerManager;
        if (procAudioPowerManager != null) {
            procAudioPowerManager.onBatteryChanged(z2);
        }
        ProcCpuinfoManager procCpuinfoManager = this.mProcCpuinfoManager;
        if (procCpuinfoManager != null) {
            procCpuinfoManager.onBatteryChanged(z2);
        }
    }

    public void onCloudFunctionUpdated(JSONObject jSONObject) {
        String strOptString;
        String strOptString2;
        Log.d(TAG, "onCloudFunctionUpdated, mIsEnabled=" + this.mIsEnabled);
        if (jSONObject.has(BLUtils.CLOUD_BATTERY_LIFE)) {
            String strOptString3 = jSONObject.optString(BLUtils.CLOUD_BATTERY_LIFE);
            String strOptString4 = "";
            if (jSONObject.has(BLUtils.CLOUD_RESOURCE_BATTERY_LIFE)) {
                try {
                    JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject(BLUtils.CLOUD_RESOURCE_BATTERY_LIFE);
                    strOptString = jSONObjectOptJSONObject.optString(BLUtils.CLOUD_ALARM_LIFE, "false");
                    try {
                        strOptString2 = jSONObjectOptJSONObject.optString(BLUtils.CLOUD_JOB_LIFE, "false");
                    } catch (Exception e2) {
                        e = e2;
                        Log.e(TAG, "onCloudFunctionUpdated" + e);
                        strOptString2 = "";
                    }
                } catch (Exception e3) {
                    e = e3;
                    strOptString = "";
                }
            } else {
                strOptString2 = "";
                strOptString = strOptString2;
            }
            if (jSONObject.has(BLUtils.CLOUD_SSRU_LIFE)) {
                try {
                    strOptString4 = jSONObject.optString(BLUtils.CLOUD_SSRU_LIFE, "false");
                } catch (Exception e4) {
                    Log.e(TAG, "onCloudFunctionUpdated" + e4);
                }
            }
            Log.d(TAG, "battery_life_do_log is " + strOptString3 + " featureValueAlarm= " + strOptString + " featureValueJob= " + strOptString2 + " featureValueSsru= " + strOptString4);
            if (TextUtils.isEmpty(strOptString3)) {
                Log.d(TAG, "battery_life_do_log is empty");
            } else if ("true".equalsIgnoreCase(strOptString3) || "false".equalsIgnoreCase(strOptString3)) {
                boolean zEqualsIgnoreCase = "true".equalsIgnoreCase(strOptString3);
                Log.d(TAG, "config = " + zEqualsIgnoreCase);
                SimpleSettings.Misc.putBoolean(this.mContext, BLUtils.CLOUD_BATTERY_LIFE, zEqualsIgnoreCase);
                SimpleSettings.Misc.putBoolean(this.mContext, BLUtils.CLOUD_ALARM_LIFE, strOptString.equalsIgnoreCase("true"));
                SimpleSettings.Misc.putBoolean(this.mContext, BLUtils.CLOUD_JOB_LIFE, strOptString2.equalsIgnoreCase("true"));
                SimpleSettings.Misc.putBoolean(this.mContext, BLUtils.CLOUD_SSRU_LIFE, strOptString4.equalsIgnoreCase("true"));
                if (!b.a.p().s(b.b.f20b)) {
                    Log.d(TAG, "Cloud is not configured, use the local configure.");
                    this.mIsEnabled = zEqualsIgnoreCase;
                    this.mIsAlarmEnable = strOptString.equalsIgnoreCase("true");
                    this.mIsJobEnable = strOptString2.equalsIgnoreCase("true");
                    this.mIsSsruTransactionEnable = strOptString4.equalsIgnoreCase("true");
                    this.mHandler.sendEmptyMessage(4);
                    BLUtils.LOCAL_LOG.log("Use the local config, mIsEnabled=" + this.mIsEnabled + " mIsAlarmEnable = " + this.mIsAlarmEnable + " mIsJobEnable = " + this.mIsJobEnable + " mIsSsruTransactionEnable=" + this.mIsSsruTransactionEnable);
                }
            } else {
                Log.d(TAG, "this is old cloud config, battery_life_do_log is " + strOptString3);
            }
        } else {
            Log.d(TAG, "don't have battery_life_do_log");
        }
        if (!jSONObject.has(BLUtils.CLOUD_PROC_CPU_TIME_IN_STATE)) {
            Log.d(TAG, "don't have proc_cpu_time_in_state");
            return;
        }
        String strOptString5 = jSONObject.optString(BLUtils.CLOUD_PROC_CPU_TIME_IN_STATE);
        Log.d(TAG, "featureValue is " + strOptString5);
        if (TextUtils.isEmpty(strOptString5)) {
            Log.d(TAG, "proc_cpu_time_in_state is empty");
            return;
        }
        try {
            JSONObject jSONObject2 = new JSONObject(strOptString5);
            if (jSONObject2.keySet().contains("fucSwitch")) {
                Log.d(TAG, "this is old cloud");
            } else {
                Log.d(TAG, "this is local config");
                initAndSaveProcCpuConfig(jSONObject2);
            }
        } catch (JSONException e5) {
            e5.printStackTrace();
        }
    }

    @Override // com.miui.powerkeeper.PowerKeeperInterface.z
    public void onScreenChanged(boolean z2) {
        if (!this.mIsEnabled) {
            Log.d(TAG, "onScreenChanged, Feature is not enabled.");
            return;
        }
        Log.d(TAG, " onScreenChanged. screenOn = " + z2);
        BatteryLifePolicy batteryLifePolicy = this.mPolicy;
        if (batteryLifePolicy != null) {
            batteryLifePolicy.onScreenChanged(z2);
        }
    }

    @Override // com.miui.powerkeeper.TimeSpan.a
    public void outTimeSpan() {
        Log.d(TAG, " outTimeSpan.");
        if (System.currentTimeMillis() - SharedPrefUtil.getInstance(this.mContext, "battery_life").getLongValue("in_time_span_time", 0L) < 36000000) {
            Log.d(TAG, " outTimeSpan. interval is too short, maybe PowerKeeper restart.");
        } else {
            this.mHandler.sendEmptyMessage(2);
        }
    }

    @Override // com.miui.powerkeeper.PowerKeeperInterface.z
    public void onRegistered(boolean z2) {
    }
}
