package com.miui.powerkeeper.powerchecker;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.NetworkStats;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.app.IBatteryStats;
import com.miui.powerkeeper.PowerKeeperInterface;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.TimeSpan;
import com.miui.powerkeeper.cloudcontrol.CloudFunctionConfig;
import com.miui.powerkeeper.logsystem.config.LogType;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.utils.Constant;
import com.miui.powerkeeper.utils.KernelRpmStatsReader;
import com.miui.powerkeeper.utils.NetUtils;
import com.miui.powerkeeper.utils.PackageUtil;
import com.miui.powerkeeper.utils.Utils;
import com.xiaomi.push.crash.CrashDb;
import com.xiaomi.push.extension.PluginConstants;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class NightPowerAbnormalChecker implements TimeSpan.a {
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    private static final int FLAG_CATCH_NETWORK_LOG = 2;
    private static final int FLAG_CATCH_POWER_LOG = 1;
    private static final boolean IS_SUPPORT_NETWROK_STATS = false;
    private static final String TAG = "NightPowerAbnormal";
    private static final String TYPE = "night_power";
    private TrafficInfo mConsumeMibileTotal;
    private Context mContext;
    private boolean mInNight;
    private SubSysAwakeDetector mSubSysAwakeDetector;
    private SparseArray<UidNetworkStats> mUidStats;
    private NetworkStats mAllUidNetworkStats = null;
    private BroadcastReceiver mBroadcastReceiver = null;
    private String mMobileIfaceName = "";

    private final class SubSysAwakeDetector implements PowerKeeperInterface.z {
        private static final int ADSP = 4;
        private static final int APSS = 2;
        private static final int BUGREPORT_TYPE = 108;
        private static final int DEFAULT_CATCH_CNT = 1;
        private static final int DEFAULT_CHECK_CYCLE = 3600000;
        private static final long MAX_MPSS_TRAFFIC_THRESHOLD = 104857600;
        private static final int MAX_RPM_ITEM_NUMS = 30;
        private static final int MPSS = 1;
        private static final int MSG_IN_TIME_SPAN = 5;
        private static final int MSG_NOTICE_KLO_UPLOAD = 4;
        private static final int MSG_OUT_TIME_SPAN = 6;
        private static final int MSG_PARAM_CFG = 1;
        private static final int MSG_SUB_SYS_AWAKE_FOUNDED_INFO = 3;
        private static final int MSG_SUB_SYS_AWAKE_RECOGNITION = 2;
        private static final int OTHERS = 16;
        private static final int RDI_UPLOAD = 1;
        private static final String RDI_VALUE_TAG = "RDIValue";
        private static final int SLPI = 8;
        private Handler mHandler;
        private PowerKeeperInterface.f0 mScreenChangedCallback;
        private final LinkedList<SubSysCheckItem> mRpmItemHistoryStats = new LinkedList<>();
        private final LinkedList<WakeUpSourceEntry> mWakeUpStats = new LinkedList<>();
        private final LinkedList<KernelWakeLockEntry> mKernelWakelockStats = new LinkedList<>();
        private final SubsysCatchLogManage mSubsysCatchLogManage = new SubsysCatchLogManage();
        private final WakeUpSourceCatchLogManage mWakeUpSourceCatchLogManage = new WakeUpSourceCatchLogManage();
        private final KernelWakeLockManager mKernelWakeLockManager = new KernelWakeLockManager();
        private final ReportDataManager mReportDataManager = new ReportDataManager();
        private final Object mObject = new Object();
        private boolean mScreenOn = false;
        private boolean mStatus = false;
        private boolean mIsEnable = false;
        private int mCheckCycle = 3600000;
        private int mCatchSubSysMask = 0;

        private final class KernelWakeLockAttribute {
            public int count = 0;
            public long length = 0;

            public KernelWakeLockAttribute() {
            }
        }

        private final class KernelWakeLockEntry {
            public Map<String, KernelWakeLockAttribute> kernelWakeLockStat;
            public long time;

            private KernelWakeLockEntry() {
                this.kernelWakeLockStat = new HashMap();
            }
        }

        private class KernelWakeLockManager {
            private boolean mIsNeedReportDataInfo;
            private Map<String, KernelWakeLockCfg> mKernelWakeLockCfg;
            private int mRDIDefaultCntThreshold;
            private int mRDIDefaultTimeThreshold;

            private class KernelWakeLockCfg {
                int cnt_threshold;
                int mask;
                int time_threshold;

                public KernelWakeLockCfg(int i2, int i3, int i4) {
                    this.time_threshold = i2;
                    this.cnt_threshold = i3;
                    this.mask = i4;
                }

                public void update(int i2, int i3, int i4) {
                    this.time_threshold = i2;
                    this.cnt_threshold = i3;
                    this.mask = i4;
                }
            }

            public void addParam(String str, int i2, int i3, int i4) {
                if (this.mKernelWakeLockCfg.get(str) == null) {
                    this.mKernelWakeLockCfg.put(str, new KernelWakeLockCfg(i2, i3, i4));
                } else {
                    this.mKernelWakeLockCfg.get(str).update(i2, i3, i4);
                }
                if (SubSysAwakeDetector.RDI_VALUE_TAG.equalsIgnoreCase(str)) {
                    if ((i4 & 1) != 0) {
                        this.mIsNeedReportDataInfo = true;
                    }
                    this.mRDIDefaultCntThreshold = i3;
                    this.mRDIDefaultTimeThreshold = i2;
                }
            }

            public void dealKernelWakeLockChange(KernelWakeLockEntry kernelWakeLockEntry, KernelWakeLockEntry kernelWakeLockEntry2) {
                int i2;
                int i3;
                if (this.mIsNeedReportDataInfo) {
                    for (Map.Entry<String, KernelWakeLockAttribute> entry : kernelWakeLockEntry.kernelWakeLockStat.entrySet()) {
                        if (kernelWakeLockEntry2.kernelWakeLockStat.get(entry.getKey()) != null) {
                            long j2 = entry.getValue().length;
                            int i4 = entry.getValue().count;
                            long j3 = kernelWakeLockEntry2.kernelWakeLockStat.get(entry.getKey()).length;
                            int i5 = i4 - kernelWakeLockEntry2.kernelWakeLockStat.get(entry.getKey()).count;
                            long j4 = j2 - j3;
                            if (i5 >= 0 && j4 >= 0) {
                                KernelWakeLockCfg kernelWakeLockCfg = this.mKernelWakeLockCfg.get(entry.getKey());
                                if (kernelWakeLockCfg == null || (((i2 = kernelWakeLockCfg.cnt_threshold) == 0 || i5 <= i2) && ((i3 = kernelWakeLockCfg.time_threshold) == 0 || j4 <= i3))) {
                                    int i6 = this.mRDIDefaultTimeThreshold;
                                    if ((i6 != 0 && i5 > i6) || (this.mRDIDefaultCntThreshold != 0 && j4 > i6)) {
                                        SubSysAwakeDetector.this.mReportDataManager.dealKernelWakeLockInfo(entry.getKey(), j4, i5);
                                    }
                                } else {
                                    SubSysAwakeDetector.this.mReportDataManager.dealKernelWakeLockInfo(entry.getKey(), j4, i5);
                                }
                            }
                        }
                    }
                }
            }

            public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
                printWriter.println("dump KernelWakeLockManage:");
                printWriter.println(" mIsNeedReportDataInfo " + this.mIsNeedReportDataInfo + " mRDIDefaultCntThreshold " + this.mRDIDefaultCntThreshold + " mRDIDefaultTimeThreshold " + this.mRDIDefaultTimeThreshold);
                for (Map.Entry<String, KernelWakeLockCfg> entry : this.mKernelWakeLockCfg.entrySet()) {
                    printWriter.println(" " + entry.getKey() + " " + entry.getValue().time_threshold + " " + entry.getValue().cnt_threshold + " " + entry.getValue().mask);
                }
            }

            public void reset() {
                this.mKernelWakeLockCfg.clear();
                this.mIsNeedReportDataInfo = false;
                this.mRDIDefaultCntThreshold = 0;
                this.mRDIDefaultTimeThreshold = 0;
            }

            private KernelWakeLockManager() {
                this.mKernelWakeLockCfg = new HashMap();
                this.mIsNeedReportDataInfo = false;
                this.mRDIDefaultCntThreshold = 0;
                this.mRDIDefaultTimeThreshold = 0;
            }
        }

        private class ReportDataManager {
            public static final int TYPE_KERNEL_WAKELOCK = 2;
            public static final int TYPE_SUBSYS = 0;
            public static final int TYPE_WAKEUP = 1;
            private LinkedList<ReportDataEntry> mRDIData;
            private int mSubsysRDIValue;
            private final String[] nameMap;

            private final class ReportDataEntry {
                String tag;
                int type;
                long time = System.currentTimeMillis();
                long time_length = 0;
                int cnt = 0;

                public ReportDataEntry(int i2, String str) {
                    this.tag = str;
                    this.type = i2;
                }
            }

            private final class ReportDataStatisticsAttr {
                int cnt = 1;
                int maxCnt;
                long maxLength;

                public ReportDataStatisticsAttr(int i2, long j2) {
                    this.maxCnt = i2;
                    this.maxLength = j2;
                }

                public void update(int i2, long j2) {
                    this.cnt++;
                    if (i2 > this.maxCnt) {
                        this.maxCnt = i2;
                    }
                    if (j2 > this.maxLength) {
                        this.maxLength = j2;
                    }
                }
            }

            private final class ReportDataStatisticsType {
                Map<String, ReportDataStatisticsAttr> statictis;

                private ReportDataStatisticsType() {
                    this.statictis = new HashMap();
                }
            }

            private void insertRDIData(ReportDataEntry reportDataEntry) {
                this.mRDIData.add(reportDataEntry);
            }

            public void dealKernelWakeLockInfo(String str, long j2, int i2) {
                ReportDataEntry reportDataEntry = new ReportDataEntry(2, str);
                reportDataEntry.cnt = i2;
                reportDataEntry.time_length = j2;
                insertRDIData(reportDataEntry);
            }

            public void dealSubsysData(SubSysCheckItem subSysCheckItem, SubSysCheckItem subSysCheckItem2) {
                if ((this.mSubsysRDIValue & 1) != 0 && subSysCheckItem2.VddMinCnt == subSysCheckItem.VddMinCnt) {
                    int i2 = 2;
                    if (SubSysAwakeDetector.this.isSubSystemAwake(subSysCheckItem, subSysCheckItem2, 2)) {
                        insertRDIData(new ReportDataEntry(0, "APSS"));
                    } else {
                        i2 = 0;
                    }
                    if (SubSysAwakeDetector.this.isSubSystemAwake(subSysCheckItem, subSysCheckItem2, 4)) {
                        i2 |= 4;
                        insertRDIData(new ReportDataEntry(0, "ADSP"));
                    }
                    if (SubSysAwakeDetector.this.isSubSystemAwake(subSysCheckItem, subSysCheckItem2, 1)) {
                        i2 |= 1;
                        insertRDIData(new ReportDataEntry(0, "MPSS"));
                    }
                    if (SubSysAwakeDetector.this.isSubSystemAwake(subSysCheckItem, subSysCheckItem2, 8)) {
                        i2 |= 8;
                        insertRDIData(new ReportDataEntry(0, "SLPI"));
                    }
                    if (i2 == 0) {
                        i2 |= 16;
                        insertRDIData(new ReportDataEntry(0, "OTHERS"));
                    }
                    if (NightPowerAbnormalChecker.DEBUG) {
                        Log.d(NightPowerAbnormalChecker.TAG, " subsys awake info " + i2);
                    }
                }
            }

            public void dealWakeUpInfo(String str, int i2) {
                ReportDataEntry reportDataEntry = new ReportDataEntry(1, str);
                reportDataEntry.cnt = i2;
                insertRDIData(reportDataEntry);
            }

            public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
                printWriter.println("dump ReportDataManage:");
                printWriter.println(" mSubsysRDIValue " + this.mSubsysRDIValue);
                Calendar calendar = Calendar.getInstance();
                for (ReportDataEntry reportDataEntry : this.mRDIData) {
                    StringBuilder sb = new StringBuilder();
                    calendar.setTimeInMillis(reportDataEntry.time);
                    sb.append(String.format("%tm-%td %tH:%tM:%tS.%tL", calendar, calendar, calendar, calendar, calendar, calendar));
                    printWriter.println(" time " + sb.toString() + " tag " + reportDataEntry.tag + " type " + reportDataEntry.type + " time_length " + reportDataEntry.time_length + " cnt " + reportDataEntry.cnt);
                }
            }

            public void reset() {
                this.mSubsysRDIValue = 0;
                this.mRDIData.clear();
            }

            public void upload() {
                if (this.mRDIData.isEmpty()) {
                    return;
                }
                ReportDataStatisticsType[] reportDataStatisticsTypeArr = {new ReportDataStatisticsType(), new ReportDataStatisticsType(), new ReportDataStatisticsType()};
                for (ReportDataEntry reportDataEntry : this.mRDIData) {
                    ReportDataStatisticsAttr reportDataStatisticsAttr = reportDataStatisticsTypeArr[reportDataEntry.type].statictis.get(reportDataEntry.tag);
                    if (reportDataStatisticsAttr == null) {
                        reportDataStatisticsTypeArr[reportDataEntry.type].statictis.put(reportDataEntry.tag, new ReportDataStatisticsAttr(reportDataEntry.cnt, reportDataEntry.time_length));
                    } else {
                        reportDataStatisticsAttr.update(reportDataEntry.cnt, reportDataEntry.time_length);
                    }
                }
                JSONObject jSONObject = new JSONObject();
                for (int i2 = 0; i2 < 3; i2++) {
                    try {
                        if (!reportDataStatisticsTypeArr[i2].statictis.isEmpty()) {
                            JSONArray jSONArray = new JSONArray();
                            for (Map.Entry<String, ReportDataStatisticsAttr> entry : reportDataStatisticsTypeArr[i2].statictis.entrySet()) {
                                JSONObject jSONObject2 = new JSONObject();
                                jSONObject2.put("tag", entry.getKey());
                                jSONObject2.put(CrashDb.DbTables.COL_COUNT, entry.getValue().cnt);
                                if (entry.getValue().maxCnt != 0) {
                                    jSONObject2.put("maxCnt", entry.getValue().maxCnt);
                                }
                                if (0 != entry.getValue().maxLength) {
                                    jSONObject2.put("maxLength", entry.getValue().maxLength);
                                }
                                jSONArray.put(jSONObject2);
                            }
                            jSONObject.put(this.nameMap[i2], jSONArray);
                        }
                    } catch (JSONException e2) {
                        Log.e(NightPowerAbnormalChecker.TAG, " ReportDataManage " + e2);
                    }
                }
            }

            private ReportDataManager() {
                this.nameMap = new String[]{NightStandbyRecord.KEY_SUBSYSCHECK, "wakeup", "kernelwakelock"};
                this.mSubsysRDIValue = 0;
                this.mRDIData = new LinkedList<>();
            }
        }

        private final class SubSysCheckItem {
            public long ADSPXoCnt;
            public long APSSXoCnt;
            public long MPSSXoCnt;
            public long SLPIXoCnt;
            public long VddLowCnt;
            public long VddMinCnt;
            public long mobileBytes;
            public int temp;
            public long time;
            public long totalBytes;
            public long wifiBytes;

            public String toString() {
                StringBuilder sb = new StringBuilder(512);
                sb.append("VddLow ");
                sb.append(this.VddLowCnt);
                sb.append(" VddMin ");
                sb.append(this.VddMinCnt);
                sb.append(" MPSSXo ");
                sb.append(this.MPSSXoCnt);
                sb.append(" APSSXo ");
                sb.append(this.APSSXoCnt);
                sb.append(" ADSPXo ");
                sb.append(this.ADSPXoCnt);
                sb.append(" SLPIXo ");
                sb.append(this.SLPIXoCnt);
                sb.append(" time ");
                sb.append(this.time);
                sb.append(" temp ");
                sb.append(this.temp);
                sb.append(" totalBytes ");
                sb.append(this.totalBytes);
                sb.append(" mobileBytes ");
                sb.append(this.mobileBytes);
                sb.append(" wifiBytes ");
                sb.append(this.wifiBytes);
                return sb.toString();
            }

            private SubSysCheckItem() {
            }
        }

        private final class SubsysCatchLogManage {
            public static final int MODEM = 0;
            public static final int WIFI = 2;
            private int ADSPMask;
            private int APSSMask;
            private int MPSSCnt;
            private int MPSSMask;
            private int SLPIMask;
            private int maxCatchCnt;

            /* JADX INFO: Access modifiers changed from: private */
            public void catchLog(int i2) {
                if ((i2 & 1) != 0) {
                    if (!isExceedMaxCnt(1)) {
                        if ((this.APSSMask & 2) == 0 || !NightPowerAbnormalChecker.DEBUG) {
                            return;
                        }
                        Log.d(NightPowerAbnormalChecker.TAG, " catch subsys wifi log");
                        return;
                    }
                    if (NightPowerAbnormalChecker.DEBUG) {
                        Log.d(NightPowerAbnormalChecker.TAG, " catch subsys MPSS cnt too much " + this.MPSSCnt);
                    }
                }
            }

            private void catchModemLog() {
                long jCurrentTimeMillis = System.currentTimeMillis();
                SystemProperties.set("mcd.extra.params", "sudebug trigger_subsys 0");
                SystemProperties.set("ctl.start", "mcd_init");
                Message messageObtainMessage = SubSysAwakeDetector.this.mHandler.obtainMessage(4);
                messageObtainMessage.obj = Long.valueOf(jCurrentTimeMillis);
                messageObtainMessage.arg1 = 0;
                SubSysAwakeDetector.this.mHandler.sendMessageDelayed(messageObtainMessage, PluginConstants.TTS_REMOVE_BEAN_TIME);
            }

            private boolean isExceedMaxCnt(int i2) {
                int i3;
                if (i2 != 1 || (i3 = this.MPSSCnt) >= this.maxCatchCnt) {
                    return true;
                }
                this.MPSSCnt = i3 + 1;
                return false;
            }

            /* JADX INFO: Access modifiers changed from: private */
            public void reset() {
                this.MPSSCnt = 0;
                this.maxCatchCnt = 1;
                this.MPSSMask = 0;
                this.APSSMask = 0;
                this.ADSPMask = 0;
                this.SLPIMask = 0;
            }

            public String toString() {
                StringBuilder sb = new StringBuilder(512);
                sb.append("MAX_CATCH_CNT ");
                sb.append(this.maxCatchCnt);
                sb.append(" MPSS_MASK ");
                sb.append(this.MPSSMask);
                sb.append(" APSS_MASK ");
                sb.append(this.APSSMask);
                sb.append(" ADSP_MASK ");
                sb.append(this.ADSPMask);
                sb.append(" SLPI_MASK ");
                sb.append(this.SLPIMask);
                sb.append(" MPSSCnt ");
                sb.append(this.MPSSCnt);
                return sb.toString();
            }

            private SubsysCatchLogManage() {
                this.maxCatchCnt = 1;
                this.MPSSCnt = 0;
                this.MPSSMask = 0;
                this.APSSMask = 0;
                this.ADSPMask = 0;
                this.SLPIMask = 0;
            }
        }

        private final class WakeUpSourceCatchLogManage {
            public static final int WLAN_CE_2 = 1;
            private boolean mIsNeedReportDataInfo;
            private int mRDIDefaultThreshold;
            private int maxCatchCnt;
            private Map<String, WakeUpSourceCfg> wakeUpCfg;
            private int wifiCnt;

            private final class WakeUpSourceCfg {
                int mask;
                int threshold;

                public WakeUpSourceCfg(int i2, int i3) {
                    this.threshold = i2;
                    this.mask = i3;
                }

                public void update(int i2, int i3) {
                    this.threshold = i2;
                    this.mask = i3;
                }
            }

            public void addParam(String str, int i2, int i3) {
                if (this.wakeUpCfg.get(str) == null) {
                    this.wakeUpCfg.put(str, new WakeUpSourceCfg(i2, i3));
                } else {
                    this.wakeUpCfg.get(str).update(i2, i3);
                }
                if (SubSysAwakeDetector.RDI_VALUE_TAG.equalsIgnoreCase(str)) {
                    if ((i3 & 1) != 0) {
                        this.mIsNeedReportDataInfo = true;
                    }
                    this.mRDIDefaultThreshold = i2;
                }
            }

            public void dealWakeUpEvent(WakeUpSourceEntry wakeUpSourceEntry, WakeUpSourceEntry wakeUpSourceEntry2) {
                int iIntValue;
                for (Map.Entry<String, Integer> entry : wakeUpSourceEntry.wakeUpStat.entrySet()) {
                    if (entry != null && entry.getKey() != null && entry.getValue() != null && wakeUpSourceEntry2.wakeUpStat.get(entry.getKey()) != null) {
                        if (this.wakeUpCfg.get(entry.getKey()) != null) {
                            int iIntValue2 = entry.getValue().intValue();
                            int iIntValue3 = wakeUpSourceEntry2.wakeUpStat.get(entry.getKey()).intValue();
                            int i2 = this.wakeUpCfg.get(entry.getKey()).threshold;
                            if (NightPowerAbnormalChecker.DEBUG) {
                                Log.d(NightPowerAbnormalChecker.TAG, "tag " + entry.getKey() + " curCnt " + iIntValue2 + " lastCnt " + iIntValue3 + " threadHolds " + i2);
                            }
                            int i3 = iIntValue2 - iIntValue3;
                            if (i3 > i2) {
                                if ((this.wakeUpCfg.get(entry.getKey()).mask & 1) != 0) {
                                    WakeUpSourceCatchLogManage wakeUpSourceCatchLogManage = SubSysAwakeDetector.this.mWakeUpSourceCatchLogManage;
                                    int i4 = wakeUpSourceCatchLogManage.wifiCnt + 1;
                                    wakeUpSourceCatchLogManage.wifiCnt = i4;
                                    if (i4 <= SubSysAwakeDetector.this.mWakeUpSourceCatchLogManage.maxCatchCnt) {
                                        if (NightPowerAbnormalChecker.DEBUG) {
                                            Log.d(NightPowerAbnormalChecker.TAG, " find wifi ce wakeup abnormal");
                                        }
                                        Message messageObtainMessage = SubSysAwakeDetector.this.mHandler.obtainMessage(4);
                                        messageObtainMessage.obj = "WLAN_CE_2";
                                        messageObtainMessage.arg1 = 2;
                                        SubSysAwakeDetector.this.mHandler.sendMessage(messageObtainMessage);
                                    }
                                }
                                if (this.mIsNeedReportDataInfo) {
                                    SubSysAwakeDetector.this.mReportDataManager.dealWakeUpInfo(entry.getKey(), i3);
                                }
                            }
                        } else if (this.mIsNeedReportDataInfo && (iIntValue = entry.getValue().intValue() - wakeUpSourceEntry2.wakeUpStat.get(entry.getKey()).intValue()) > this.mRDIDefaultThreshold) {
                            SubSysAwakeDetector.this.mReportDataManager.dealWakeUpInfo(entry.getKey(), iIntValue);
                        }
                    }
                }
            }

            public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
                printWriter.println("dump wakeUpSourceCatchLogManage:");
                printWriter.println(" maxCatchCnt " + this.maxCatchCnt + " wifiCnt " + this.wifiCnt);
                printWriter.println(" mIsNeedReportDataInfo " + this.mIsNeedReportDataInfo + " mRDIDefaultThreshold " + this.mRDIDefaultThreshold);
                for (Map.Entry<String, WakeUpSourceCfg> entry : this.wakeUpCfg.entrySet()) {
                    printWriter.println(" tag " + entry.getKey() + " threshold " + entry.getValue().threshold + " mask " + entry.getValue().mask);
                }
            }

            public void reset() {
                this.maxCatchCnt = 1;
                this.wifiCnt = 0;
                this.wakeUpCfg.clear();
                this.mIsNeedReportDataInfo = false;
                this.mRDIDefaultThreshold = 0;
            }

            private WakeUpSourceCatchLogManage() {
                this.maxCatchCnt = 1;
                this.mIsNeedReportDataInfo = false;
                this.mRDIDefaultThreshold = 0;
                this.wakeUpCfg = new HashMap();
            }
        }

        private final class WakeUpSourceEntry {
            public long time;
            public Map<String, Integer> wakeUpStat;

            private WakeUpSourceEntry() {
                this.wakeUpStat = new HashMap();
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        public SubSysAwakeDetector(Looper looper) {
            this.mHandler = new Handler(looper) { // from class: com.miui.powerkeeper.powerchecker.NightPowerAbnormalChecker.SubSysAwakeDetector.1
                @Override // android.os.Handler
                public void handleMessage(Message message) {
                    SubSysAwakeDetector.this.onMessageHandler(message);
                }
            };
        }

        private void cfgParam() {
            this.mHandler.obtainMessage(1).sendToTarget();
        }

        private void clearUselessData() {
            this.mSubsysCatchLogManage.reset();
            this.mWakeUpSourceCatchLogManage.reset();
            this.mReportDataManager.reset();
            this.mKernelWakeLockManager.reset();
            this.mRpmItemHistoryStats.clear();
            this.mWakeUpStats.clear();
            this.mKernelWakelockStats.clear();
            this.mCatchSubSysMask = 0;
            this.mStatus = false;
            this.mIsEnable = false;
            this.mCheckCycle = 3600000;
        }

        private KernelWakeLockEntry getCurKernelWakeLockState(IBatteryStats iBatteryStats) {
            KernelWakeLockEntry kernelWakeLockEntry = new KernelWakeLockEntry();
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
                            int i4 = parcelObtain.readInt();
                            long j2 = parcelObtain.readLong();
                            if (i4 > 0 && j2 > 0) {
                                KernelWakeLockAttribute kernelWakeLockAttribute = new KernelWakeLockAttribute();
                                kernelWakeLockAttribute.count = i4;
                                kernelWakeLockAttribute.length = (j2 + 500) / 1000;
                                kernelWakeLockEntry.kernelWakeLockStat.put(string, kernelWakeLockAttribute);
                            }
                        }
                    }
                } catch (Exception e2) {
                    Log.e(NightPowerAbnormalChecker.TAG, e2.getMessage());
                }
                return kernelWakeLockEntry;
            } finally {
                parcelObtain.recycle();
            }
        }

        private SubSysCheckItem getCurRPMStatus() {
            if (KernelRpmStatsReader.isSupportReadSubSystemStat()) {
                try {
                    if (Utils.isCharging(NightPowerAbnormalChecker.this.mContext)) {
                        return null;
                    }
                    KernelRpmStatsReader.readRpmStats();
                    SubSysCheckItem subSysCheckItem = new SubSysCheckItem();
                    subSysCheckItem.VddLowCnt = KernelRpmStatsReader.getSubsysSleepCnt(KernelRpmStatsReader.RPMSubsys.RPM_VDD_LOW);
                    subSysCheckItem.VddMinCnt = KernelRpmStatsReader.getSubsysSleepCnt(KernelRpmStatsReader.RPMSubsys.RPM_VDD_MIN);
                    subSysCheckItem.APSSXoCnt = KernelRpmStatsReader.getSubsysSleepCnt(KernelRpmStatsReader.RPMSubsys.RPM_APSS);
                    subSysCheckItem.MPSSXoCnt = KernelRpmStatsReader.getSubsysSleepCnt(KernelRpmStatsReader.RPMSubsys.RPM_MPSS);
                    subSysCheckItem.ADSPXoCnt = KernelRpmStatsReader.getSubsysSleepCnt(KernelRpmStatsReader.RPMSubsys.RPM_ADSP);
                    subSysCheckItem.SLPIXoCnt = KernelRpmStatsReader.getSubsysSleepCnt(KernelRpmStatsReader.RPMSubsys.RPM_SLPI);
                    subSysCheckItem.temp = Utils.getCurTemp(NightPowerAbnormalChecker.this.mContext);
                    subSysCheckItem.time = System.currentTimeMillis();
                    subSysCheckItem.mobileBytes = TrafficStats.getMobileRxBytes() + TrafficStats.getMobileTxBytes();
                    long totalRxBytes = TrafficStats.getTotalRxBytes() + TrafficStats.getMobileTxPackets();
                    subSysCheckItem.totalBytes = totalRxBytes;
                    subSysCheckItem.wifiBytes = totalRxBytes - subSysCheckItem.mobileBytes;
                    return subSysCheckItem;
                } catch (IOException e2) {
                    Log.e(NightPowerAbnormalChecker.TAG, " getCurRPMStatus" + e2);
                }
            }
            return null;
        }

        private WakeUpSourceEntry getCurWakeUpState(IBatteryStats iBatteryStats) {
            WakeUpSourceEntry wakeUpSourceEntry = new WakeUpSourceEntry();
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
                            long j2 = parcelObtain.readLong();
                            if (string != null && string.length() > 0 && j2 > 0) {
                                wakeUpSourceEntry.wakeUpStat.put(string, Integer.valueOf(i4));
                            }
                        }
                    }
                } catch (Exception e2) {
                    Log.e(NightPowerAbnormalChecker.TAG, e2.getMessage());
                }
                return wakeUpSourceEntry;
            } finally {
                parcelObtain.recycle();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isSubSystemAwake(SubSysCheckItem subSysCheckItem, SubSysCheckItem subSysCheckItem2, int i2) {
            if (i2 == 1) {
                long j2 = subSysCheckItem2.MPSSXoCnt;
                return j2 == subSysCheckItem.MPSSXoCnt && j2 != -1 && subSysCheckItem.totalBytes - subSysCheckItem2.totalBytes < MAX_MPSS_TRAFFIC_THRESHOLD;
            }
            if (i2 == 2) {
                long j3 = subSysCheckItem2.APSSXoCnt;
                return j3 == subSysCheckItem.APSSXoCnt && j3 != -1;
            }
            if (i2 == 4) {
                long j4 = subSysCheckItem2.ADSPXoCnt;
                return j4 == subSysCheckItem.ADSPXoCnt && j4 != -1;
            }
            if (i2 != 8) {
                return false;
            }
            long j5 = subSysCheckItem2.SLPIXoCnt;
            return j5 == subSysCheckItem.SLPIXoCnt && j5 != -1;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void onMessageHandler(Message message) {
            switch (message.what) {
                case 1:
                    boolean z2 = SimpleSettings.Misc.getBoolean(NightPowerAbnormalChecker.this.mContext, Constant.FUNC_NIHGT_SUBSYSAWAKE_CHECK, false);
                    this.mIsEnable = z2;
                    if (!z2) {
                        if (NightPowerAbnormalChecker.DEBUG) {
                            Log.d(NightPowerAbnormalChecker.TAG, "feature is disabled");
                            return;
                        }
                        return;
                    }
                    String functionParameters = CloudFunctionConfig.getFunctionParameters(NightPowerAbnormalChecker.this.mContext, Constant.FUNC_NIHGT_SUBSYSAWAKE_CHECK, null);
                    if (functionParameters != null && !functionParameters.isEmpty()) {
                        if (NightPowerAbnormalChecker.DEBUG) {
                            Log.d(NightPowerAbnormalChecker.TAG, "catchCfg param " + functionParameters);
                        }
                        try {
                            JSONObject jSONObject = new JSONObject(functionParameters);
                            this.mCatchSubSysMask = jSONObject.getInt("subsysMask");
                            this.mReportDataManager.mSubsysRDIValue = jSONObject.getInt(RDI_VALUE_TAG);
                            this.mCheckCycle = jSONObject.getInt("checkCycle");
                            this.mSubsysCatchLogManage.maxCatchCnt = jSONObject.getInt("maxCnt");
                            this.mSubsysCatchLogManage.MPSSMask = jSONObject.getInt("MPSSMask");
                            this.mSubsysCatchLogManage.APSSMask = jSONObject.getInt("APSSMask");
                            this.mSubsysCatchLogManage.SLPIMask = jSONObject.getInt("SLPIMask");
                            this.mSubsysCatchLogManage.ADSPMask = jSONObject.getInt("ADSPMask");
                        } catch (JSONException e2) {
                            Log.e(NightPowerAbnormalChecker.TAG, " cfg param " + e2);
                        }
                        break;
                    } else if (NightPowerAbnormalChecker.DEBUG) {
                        Log.d(NightPowerAbnormalChecker.TAG, "get cloud param null");
                    }
                    String functionParameters2 = CloudFunctionConfig.getFunctionParameters2(NightPowerAbnormalChecker.this.mContext, Constant.FUNC_NIHGT_SUBSYSAWAKE_CHECK, null);
                    if (functionParameters2 != null && !functionParameters2.isEmpty()) {
                        if (NightPowerAbnormalChecker.DEBUG) {
                            Log.d(NightPowerAbnormalChecker.TAG, "wakeUpCfg param " + functionParameters2);
                        }
                        try {
                            JSONObject jSONObject2 = new JSONObject(functionParameters2);
                            this.mWakeUpSourceCatchLogManage.maxCatchCnt = jSONObject2.getInt("maxCnt");
                            JSONArray jSONArray = jSONObject2.getJSONArray("wakeupsource");
                            for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                                this.mWakeUpSourceCatchLogManage.addParam(jSONArray.getJSONObject(i2).getString("tag").trim(), jSONArray.getJSONObject(i2).getInt("threshold"), jSONArray.getJSONObject(i2).getInt("mask"));
                            }
                            JSONArray jSONArray2 = jSONObject2.getJSONArray("kernelwakelock");
                            for (int i3 = 0; i3 < jSONArray2.length(); i3++) {
                                this.mKernelWakeLockManager.addParam(jSONArray2.getJSONObject(i3).getString("tag").trim(), jSONArray2.getJSONObject(i3).getInt("time_threshold"), jSONArray2.getJSONObject(i3).getInt("cnt_threshold"), jSONArray2.getJSONObject(i3).getInt("mask"));
                            }
                        } catch (JSONException e3) {
                            Log.e(NightPowerAbnormalChecker.TAG, " wakeUpCfg param " + e3);
                        }
                        break;
                    } else if (NightPowerAbnormalChecker.DEBUG) {
                        Log.d(NightPowerAbnormalChecker.TAG, "get cloud param2 null");
                    }
                    startSubsysAwakeCheck();
                    return;
                case 2:
                    updateSubSysAwakeRecognition();
                    IBatteryStats batteryStats = BatteryStatsHelper.getBatteryStats();
                    if (batteryStats == null) {
                        return;
                    }
                    updateWakeUpState(batteryStats);
                    updateKernelWakeLockState(batteryStats);
                    return;
                case 3:
                    if (message.arg1 > 0) {
                        this.mSubsysCatchLogManage.catchLog(((Integer) message.obj).intValue());
                        return;
                    }
                    return;
                case 4:
                    Intent intent = new Intent("upload_one_bugreport");
                    intent.setComponent(new ComponentName("com.miui.klo.bugreport", "com.miui.klo.bugreport.service.FeedbackBackgroundService"));
                    Bundle bundle = new Bundle();
                    bundle.putInt("bugreportTypeID", 108);
                    if (NightPowerAbnormalChecker.DEBUG) {
                        Log.d(NightPowerAbnormalChecker.TAG, " MSG_NOTICE_KLO_UPLOAD type is " + message.arg1);
                    }
                    int i4 = message.arg1;
                    if (i4 == 0) {
                        bundle.putLong("timestamp", ((Long) message.obj).longValue());
                        bundle.putString("type", "modem");
                    } else if (2 == i4) {
                        bundle.putString("type", "wifi");
                    }
                    intent.putExtras(bundle);
                    NightPowerAbnormalChecker.this.mContext.startServiceAsUser(intent, UserHandle.CURRENT);
                    return;
                case 5:
                    synchronized (this.mObject) {
                        clearUselessData();
                        cfgParam();
                        break;
                    }
                    return;
                case 6:
                    synchronized (this.mObject) {
                        stopSubsysAwakeCheck();
                        clearUselessData();
                        break;
                    }
                    return;
                default:
                    return;
            }
        }

        private void registerScreenReceiver() {
            if (this.mScreenChangedCallback != null) {
                return;
            }
            this.mScreenChangedCallback = new PowerKeeperInterface.f0(this.mHandler, this);
            PowerKeeperManager.getInstance().registerScreenChangedListener(this.mScreenChangedCallback);
        }

        private void resetOutOfDateInfo() {
            this.mHandler.removeMessages(2);
            this.mRpmItemHistoryStats.clear();
            this.mWakeUpStats.clear();
            this.mKernelWakelockStats.clear();
        }

        private void startSubsysAwakeCheck() {
            if (this.mIsEnable) {
                registerScreenReceiver();
                if (this.mHandler.hasMessages(2)) {
                    this.mHandler.removeMessages(2);
                    this.mHandler.obtainMessage(2).sendToTarget();
                } else {
                    this.mHandler.obtainMessage(2).sendToTarget();
                }
                this.mStatus = true;
            }
        }

        private void stopSubsysAwakeCheck() {
            if (this.mIsEnable) {
                unRegisterScreenReceiver();
                this.mHandler.removeMessages(2);
                this.mStatus = false;
                this.mReportDataManager.upload();
            }
        }

        private void unRegisterScreenReceiver() {
            if (this.mScreenChangedCallback == null) {
                return;
            }
            PowerKeeperManager.getInstance().unregisterScreenChangedListener(this.mScreenChangedCallback);
            this.mScreenChangedCallback.a();
            this.mScreenChangedCallback = null;
        }

        private void updateKernelWakeLockState(IBatteryStats iBatteryStats) {
            KernelWakeLockEntry curKernelWakeLockState = getCurKernelWakeLockState(iBatteryStats);
            if (curKernelWakeLockState != null) {
                if (this.mKernelWakelockStats.isEmpty()) {
                    this.mKernelWakelockStats.addFirst(curKernelWakeLockState);
                    return;
                }
                KernelWakeLockEntry kernelWakeLockEntry = this.mKernelWakelockStats.get(0);
                if (this.mKernelWakelockStats.size() > 30) {
                    this.mKernelWakelockStats.removeLast();
                }
                this.mKernelWakelockStats.addFirst(curKernelWakeLockState);
                if (kernelWakeLockEntry != null) {
                    this.mKernelWakeLockManager.dealKernelWakeLockChange(curKernelWakeLockState, kernelWakeLockEntry);
                }
            }
        }

        private void updateSubSysAwakeRecognition() {
            int i2;
            if (this.mScreenOn) {
                resetOutOfDateInfo();
                Handler handler = this.mHandler;
                handler.sendMessageDelayed(handler.obtainMessage(2), this.mCheckCycle);
                return;
            }
            SubSysCheckItem curRPMStatus = getCurRPMStatus();
            if (curRPMStatus != null) {
                if (NightPowerAbnormalChecker.DEBUG) {
                    Log.d(NightPowerAbnormalChecker.TAG, "cur state is " + curRPMStatus.toString());
                }
                if (this.mRpmItemHistoryStats.size() <= 0) {
                    this.mRpmItemHistoryStats.addFirst(curRPMStatus);
                    Handler handler2 = this.mHandler;
                    handler2.sendMessageDelayed(handler2.obtainMessage(2), this.mCheckCycle);
                    return;
                }
                int i3 = 0;
                SubSysCheckItem subSysCheckItem = this.mRpmItemHistoryStats.get(0);
                if (subSysCheckItem != null && NightPowerAbnormalChecker.DEBUG) {
                    Log.d(NightPowerAbnormalChecker.TAG, "last state is " + subSysCheckItem.toString());
                }
                if (this.mRpmItemHistoryStats.size() >= 30) {
                    this.mRpmItemHistoryStats.removeLast();
                }
                this.mRpmItemHistoryStats.addFirst(curRPMStatus);
                if (subSysCheckItem != null) {
                    this.mReportDataManager.dealSubsysData(curRPMStatus, subSysCheckItem);
                    if (subSysCheckItem.VddMinCnt == curRPMStatus.VddMinCnt) {
                        if ((this.mCatchSubSysMask & 2) == 0 || !isSubSystemAwake(curRPMStatus, subSysCheckItem, 2)) {
                            i2 = 0;
                        } else {
                            i3 = 2;
                            i2 = 1;
                        }
                        if ((this.mCatchSubSysMask & 4) != 0 && isSubSystemAwake(curRPMStatus, subSysCheckItem, 4)) {
                            i3 += 4;
                            i2++;
                        }
                        if ((this.mCatchSubSysMask & 1) != 0 && isSubSystemAwake(curRPMStatus, subSysCheckItem, 1)) {
                            i3++;
                            i2++;
                        }
                        if ((this.mCatchSubSysMask & 8) != 0 && isSubSystemAwake(curRPMStatus, subSysCheckItem, 8)) {
                            i3 += 8;
                            i2++;
                        }
                        Message messageObtainMessage = this.mHandler.obtainMessage(3);
                        messageObtainMessage.obj = Integer.valueOf(i3);
                        messageObtainMessage.arg1 = i2;
                        if (NightPowerAbnormalChecker.DEBUG) {
                            Log.d(NightPowerAbnormalChecker.TAG, "awake founded msg " + Integer.valueOf(i3) + " " + i2);
                        }
                        messageObtainMessage.sendToTarget();
                    }
                }
            }
            Handler handler3 = this.mHandler;
            handler3.sendMessageDelayed(handler3.obtainMessage(2), this.mCheckCycle);
        }

        private void updateWakeUpState(IBatteryStats iBatteryStats) {
            WakeUpSourceEntry curWakeUpState = getCurWakeUpState(iBatteryStats);
            if (curWakeUpState != null) {
                if (this.mWakeUpStats.size() <= 0) {
                    this.mWakeUpStats.addFirst(curWakeUpState);
                    return;
                }
                WakeUpSourceEntry wakeUpSourceEntry = this.mWakeUpStats.get(0);
                if (this.mWakeUpStats.size() > 30) {
                    this.mWakeUpStats.removeLast();
                }
                this.mWakeUpStats.addFirst(curWakeUpState);
                if (wakeUpSourceEntry != null) {
                    this.mWakeUpSourceCatchLogManage.dealWakeUpEvent(curWakeUpState, wakeUpSourceEntry);
                }
            }
        }

        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            synchronized (this.mObject) {
                try {
                    printWriter.println("dump SubSysAwakeDetector:");
                    printWriter.println(" mIsEnable " + this.mIsEnable + " mScreenOn " + this.mScreenOn + " mStatus " + this.mStatus);
                    printWriter.println(" mIsInNight " + NightPowerAbnormalChecker.this.mInNight + " mCheckCycle " + this.mCheckCycle + " mCatchSubSysMask " + this.mCatchSubSysMask);
                    printWriter.println(this.mSubsysCatchLogManage.toString());
                    Iterator<SubSysCheckItem> it = this.mRpmItemHistoryStats.iterator();
                    while (it.hasNext()) {
                        printWriter.println(it.next().toString());
                    }
                    for (WakeUpSourceEntry wakeUpSourceEntry : this.mWakeUpStats) {
                        StringBuilder sb = new StringBuilder();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(wakeUpSourceEntry.time);
                        sb.append(String.format("%tm-%td %tH:%tM:%tS.%tL", calendar, calendar, calendar, calendar, calendar, calendar));
                        printWriter.println(" time " + sb.toString());
                        for (Map.Entry<String, Integer> entry : wakeUpSourceEntry.wakeUpStat.entrySet()) {
                            printWriter.println(" tag " + entry.getKey() + " cnt " + entry.getValue());
                        }
                    }
                    this.mWakeUpSourceCatchLogManage.dump(fileDescriptor, printWriter, strArr);
                    this.mKernelWakeLockManager.dump(fileDescriptor, printWriter, strArr);
                    this.mReportDataManager.dump(fileDescriptor, printWriter, strArr);
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        public void inTimeSpan() {
            this.mHandler.sendEmptyMessage(5);
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.z
        public void onRegistered(boolean z2) {
            synchronized (this.mObject) {
                try {
                    this.mScreenOn = z2;
                    if (NightPowerAbnormalChecker.this.mInNight && this.mStatus) {
                        resetOutOfDateInfo();
                        this.mHandler.removeMessages(2);
                        if (!this.mScreenOn) {
                            this.mHandler.obtainMessage(2).sendToTarget();
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.z
        public void onScreenChanged(boolean z2) {
            synchronized (this.mObject) {
                try {
                    if (this.mScreenOn != z2) {
                        this.mScreenOn = z2;
                        if (NightPowerAbnormalChecker.this.mInNight && this.mStatus) {
                            resetOutOfDateInfo();
                            this.mHandler.removeMessages(2);
                            if (!this.mScreenOn) {
                                this.mHandler.obtainMessage(2).sendToTarget();
                            }
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        public void outTimeSpan() {
            this.mHandler.sendEmptyMessage(6);
        }
    }

    private class TrafficInfo {
        long mRxBytes;
        long mRxPackets;
        long mTxBytes;
        long mTxPackets;

        void add(long j2, long j3, long j4, long j5) {
            this.mRxBytes += j2;
            this.mTxBytes += j3;
            this.mRxPackets += j4;
            this.mTxPackets += j5;
        }

        boolean isExceed(TrafficInfo trafficInfo) {
            long j2 = this.mRxBytes;
            long j3 = trafficInfo.mRxBytes;
            if (j2 > j3 && j3 > 0) {
                return true;
            }
            long j4 = this.mTxBytes;
            long j5 = trafficInfo.mTxBytes;
            if (j4 > j5 && j5 > 0) {
                return true;
            }
            long j6 = this.mRxPackets;
            long j7 = trafficInfo.mRxPackets;
            if (j6 > j7 && j7 > 0) {
                return true;
            }
            long j8 = this.mTxPackets;
            long j9 = trafficInfo.mTxPackets;
            return j8 > j9 && j9 > 0;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            sb.append("{rxBytes=");
            sb.append(this.mRxBytes);
            sb.append(" txBytes=");
            sb.append(this.mTxBytes);
            sb.append(" rxPackets=");
            sb.append(this.mRxPackets);
            sb.append(" txPackets=");
            sb.append(this.mTxPackets);
            sb.append('}');
            return sb.toString();
        }

        private TrafficInfo() {
        }
    }

    private class UidNetworkStats {
        TrafficInfo mMobile;
        TrafficInfo mMobileThreshold;
        String mPkg;
        int mUid;

        UidNetworkStats(int i2, String str) {
            this.mUid = i2;
            this.mPkg = str;
            this.mMobile = new TrafficInfo();
            this.mMobileThreshold = new TrafficInfo();
        }

        boolean isExceedThreshold() {
            TrafficInfo trafficInfo;
            TrafficInfo trafficInfo2 = this.mMobile;
            return (trafficInfo2 == null || (trafficInfo = this.mMobileThreshold) == null || !trafficInfo2.isExceed(trafficInfo)) ? false : true;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(512);
            sb.append("{ uid=");
            sb.append(this.mUid);
            sb.append(" pkg=");
            sb.append(this.mPkg);
            sb.append(" mMobile=");
            sb.append(this.mMobile);
            sb.append(" mMobileThreshold=");
            sb.append(this.mMobileThreshold);
            sb.append('}');
            return sb.toString();
        }
    }

    public NightPowerAbnormalChecker(Context context, Looper looper) {
        this.mContext = context;
        this.mSubSysAwakeDetector = new SubSysAwakeDetector(looper);
    }

    /* JADX WARN: Code duplicated, block: B:43:0x008c  */
    /* JADX WARN: Code duplicated, block: B:46:0x0099  */
    private SparseArray<UidNetworkStats> getMonitoringUid() {
        String functionParameters;
        SparseArray<UidNetworkStats> sparseArray;
        String str;
        int uidByPackageName;
        SparseArray<UidNetworkStats> sparseArray2 = null;
        if (this.mAllUidNetworkStats == null) {
            return null;
        }
        if (!SimpleSettings.Misc.getBoolean(this.mContext, Constant.FUNC_NETWORK_TRAFFIC_CHECK, false) || (functionParameters = CloudFunctionConfig.getFunctionParameters(this.mContext, Constant.FUNC_NETWORK_TRAFFIC_CHECK, null)) == null || functionParameters.isEmpty()) {
            return null;
        }
        try {
            JSONArray jSONArray = new JSONArray(functionParameters);
            int length = jSONArray.length();
            sparseArray = null;
            for (int i2 = 0; i2 < length; i2++) {
                try {
                    Object obj = jSONArray.getJSONObject(i2).get("pkg");
                    if (obj instanceof Number) {
                        uidByPackageName = ((Number) obj).intValue();
                        str = null;
                    } else {
                        String str2 = (String) obj;
                        str = str2;
                        uidByPackageName = PackageUtil.getUidByPackageName(this.mContext, str2);
                    }
                    if (uidByPackageName != -1) {
                        if (sparseArray == null) {
                            sparseArray = new SparseArray<>(length);
                        } else if (sparseArray.get(uidByPackageName) != null) {
                        }
                        sparseArray.put(uidByPackageName, new UidNetworkStats(uidByPackageName, str));
                    }
                } catch (JSONException e2) {
                    e = e2;
                    sparseArray2 = sparseArray;
                    Log.w(TAG, "JSONException ", e);
                    sparseArray = sparseArray2;
                    if (DEBUG) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("getMonitoringUid size=");
                        sb.append(sparseArray != null ? sparseArray.size() : 0);
                        Log.d(TAG, sb.toString());
                    }
                    return sparseArray;
                } catch (Exception e3) {
                    e = e3;
                    sparseArray2 = sparseArray;
                    Log.w(TAG, "Exception", e);
                    sparseArray = sparseArray2;
                    if (DEBUG) {
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("getMonitoringUid size=");
                        sb2.append(sparseArray != null ? sparseArray.size() : 0);
                        Log.d(TAG, sb2.toString());
                    }
                    return sparseArray;
                }
            }
        } catch (JSONException e4) {
            e = e4;
        } catch (Exception e5) {
            e = e5;
        }
        if (DEBUG) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("getMonitoringUid size=");
            sb3.append(sparseArray != null ? sparseArray.size() : 0);
            Log.d(TAG, sb3.toString());
        }
        return sparseArray;
    }

    private boolean isAbnormalNetworkStats(SparseArray<UidNetworkStats> sparseArray) {
        int size = sparseArray.size();
        for (int i2 = 0; i2 < size; i2++) {
            if (sparseArray.valueAt(i2).isExceedThreshold()) {
                return true;
            }
        }
        return false;
    }

    private void networkInTime() {
        if (SimpleSettings.Misc.getBoolean(this.mContext, Constant.FUNC_NETWORK_TRAFFIC_CHECK, false)) {
            if (DEBUG) {
                Log.d(TAG, "inTimeSpan FUNC_NETWORK_TRAFFIC_CHECK enable");
            }
            NetworkStats networkStats = NetUtils.getNetworkStats();
            this.mAllUidNetworkStats = networkStats;
            if (networkStats != null) {
                registerBroadcastReceiver();
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:7:0x0016  */
    private int networkOutTime() {
        int i2;
        SparseArray<UidNetworkStats> monitoringUid = getMonitoringUid();
        this.mUidStats = monitoringUid;
        if (monitoringUid != null) {
            updateNetworkStats(monitoringUid);
            updateThreshold(monitoringUid);
            if (isAbnormalNetworkStats(monitoringUid)) {
                i2 = 2;
            } else {
                i2 = 0;
            }
        } else {
            i2 = 0;
        }
        this.mAllUidNetworkStats = null;
        unregisterBroadcastReceiverIfNeeded();
        return i2;
    }

    private void registerBroadcastReceiver() {
        this.mMobileIfaceName = "";
        BroadcastReceiver broadcastReceiver = this.mBroadcastReceiver;
        if (broadcastReceiver != null) {
            this.mContext.unregisterReceiver(broadcastReceiver);
        }
        this.mBroadcastReceiver = new BroadcastReceiver() { // from class: com.miui.powerkeeper.powerchecker.NightPowerAbnormalChecker.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                if (intent != null && "android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
                    NightPowerAbnormalChecker.this.updateIfaceName();
                }
            }
        };
        this.mContext.registerReceiver(this.mBroadcastReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"), 2);
        updateIfaceName();
    }

    private void unregisterBroadcastReceiverIfNeeded() {
        this.mMobileIfaceName = "";
        BroadcastReceiver broadcastReceiver = this.mBroadcastReceiver;
        if (broadcastReceiver != null) {
            this.mContext.unregisterReceiver(broadcastReceiver);
            this.mBroadcastReceiver = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateIfaceName() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.mContext.getSystemService(LogType.LOG_TYPE_CONNECTIVITY_NAME);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        LinkProperties linkProperties = connectivityManager.getLinkProperties(connectivityManager.getActiveNetwork());
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnectedOrConnecting() || linkProperties == null) {
            if (DEBUG) {
                Log.d(TAG, "updateIfaceName abort for ni=" + activeNetworkInfo + " activeLink=" + linkProperties);
                return;
            }
            return;
        }
        String interfaceName = linkProperties.getInterfaceName();
        if (interfaceName == null || interfaceName.isEmpty()) {
            if (DEBUG) {
                Log.d(TAG, "updateIfaceName abort for ifName=" + interfaceName);
                return;
            }
            return;
        }
        if (activeNetworkInfo.getType() == 0 && !this.mMobileIfaceName.contains(interfaceName)) {
            this.mMobileIfaceName += "," + interfaceName;
        }
        if (DEBUG) {
            Log.d(TAG, "updateIfaceName mMobileIfaceName=" + this.mMobileIfaceName);
        }
    }

    private void updateNetworkStats(SparseArray<UidNetworkStats> sparseArray) {
        NetworkStats networkStats = NetUtils.getNetworkStats();
        if (networkStats == null) {
            return;
        }
        networkStats.subtract(this.mAllUidNetworkStats);
    }

    private void updateThreshold(SparseArray<UidNetworkStats> sparseArray) {
        UidNetworkStats uidNetworkStats;
        String functionParameters2 = CloudFunctionConfig.getFunctionParameters2(this.mContext, Constant.FUNC_NETWORK_TRAFFIC_CHECK, null);
        if (functionParameters2 == null || functionParameters2.isEmpty()) {
            return;
        }
        try {
            JSONArray jSONArray = new JSONArray(functionParameters2);
            int length = jSONArray.length();
            for (int i2 = 0; i2 < length; i2++) {
                JSONObject jSONObject = jSONArray.getJSONObject(i2);
                Object obj = jSONObject.get("pkg");
                int iIntValue = obj instanceof Number ? ((Number) obj).intValue() : PackageUtil.getUidByPackageName(this.mContext, (String) obj);
                if (iIntValue != -1 && (uidNetworkStats = sparseArray.get(iIntValue)) != null) {
                    uidNetworkStats.mMobileThreshold.mRxBytes = jSONObject.getLong("rx");
                    uidNetworkStats.mMobileThreshold.mTxBytes = jSONObject.getLong("tx");
                }
            }
        } catch (JSONException e2) {
            Log.w(TAG, "JSONException ", e2);
        } catch (Exception e3) {
            Log.w(TAG, "Exception", e3);
        }
        if (DEBUG) {
            Log.d(TAG, "updateThreshold");
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("dump NightAbnormal:");
        printWriter.println("\tinNight = " + this.mInNight);
        printWriter.println("nework traffic func enabled = " + SimpleSettings.Misc.getBoolean(this.mContext, Constant.FUNC_NETWORK_TRAFFIC_CHECK, false));
        printWriter.println("nework traffic para1= " + CloudFunctionConfig.getFunctionParameters(this.mContext, Constant.FUNC_NETWORK_TRAFFIC_CHECK, ""));
        printWriter.println("nework traffic para2 = " + CloudFunctionConfig.getFunctionParameters2(this.mContext, Constant.FUNC_NETWORK_TRAFFIC_CHECK, ""));
        printWriter.println("nework traffic mConsumeMibileTotal=" + this.mConsumeMibileTotal);
        printWriter.println("nework traffic mUidStats = " + this.mUidStats);
        this.mSubSysAwakeDetector.dump(fileDescriptor, printWriter, strArr);
    }

    @Override // com.miui.powerkeeper.TimeSpan.a
    public void inTimeSpan() {
        this.mInNight = true;
        this.mSubSysAwakeDetector.inTimeSpan();
    }

    @Override // com.miui.powerkeeper.TimeSpan.a
    public void outTimeSpan() {
        this.mSubSysAwakeDetector.outTimeSpan();
        if (this.mInNight) {
            this.mInNight = false;
        }
    }

    public void destroy() {
    }
}
