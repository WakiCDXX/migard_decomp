package com.miui.powerkeeper.resource.bigdata.resourcelight;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import com.android.internal.os.BackgroundThread;
import com.miui.powerkeeper.tracker.TrackerConfig;
import com.miui.powerkeeper.tracker.TrackerManager;
import com.miui.powerkeeper.utils.PackageUtil;
import com.xiaomi.onetrack.util.ac;
import com.xiaomi.push.mpcd.Constants;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class WakeupAnalysis {
    private static final String ACTION_SYS_CONTROLL_JOB_SYNC_ALARM = "sys.action.controll_job_sync_alarm";
    private static final String ALARM = "alarm";
    public static final int ALARM_WAKEUP_STANDARD;
    private static ArrayList<ResourceInterface.IAppResourceUploadListener> BigdataListener = null;
    private static final String CLOUD_SYNC_JOB_ALARM_CONTROL = "cloud_SyncJobAlarm_control";
    public static final long CURRENTNOW_STANDARD;
    private static final boolean DEBUG;
    private static final String JOB = "job";
    private static final int JOB_WAKEUP_STANDARD;
    private static final int MSG_GREENIFY_UPDATE_FGBG_RESOURCE = 0;
    private static final String SYNC = "sync";
    public static final int SYNC_WAKEUP_STANDARD;
    private static final String TAG = "WakeupAnalysis";
    private static final String TAG_OPT = "OPT-TF-Power";
    private static Set<UidWakeReason> mListWakeupInfo;
    private int FGBG_TIME;
    private AlarmManager alarmManager;
    private ResourceInterface.IAppResourceUploadListener mAppResourceUploadListener;
    private Context mContext;
    private boolean mEnabled;
    private Handler mHandler;
    private boolean mIsSupportSysControll;
    private Set<UidPacket> mPacketMapAlarm;
    private Set<UidPacket> mPacketMapJob;
    private Set<UidPacket> mPacketMapSync;
    private final BroadcastReceiver mReceiveralarm;
    private List<String> mSupportDeviceList;
    private boolean mSyncCloundEnable;
    private boolean suspend;
    public final Object mLock = new Object();
    private String FGBGALARM_ACTION = "fgbgalarm";

    private class UidHandler extends Handler {
        public UidHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what != 0) {
                return;
            }
            WakeupAnalysis.this.notifyBigdataListener();
        }
    }

    private class UidPacket {
        public long bgcount;
        public long fgcount;
        public boolean foreground;
        public String packageName;
        public String packageVersion;
        public long suspendbgcount;
        public long suspendfgcount;
        public int uid;

        private UidPacket() {
            this.fgcount = 0L;
            this.suspendfgcount = 0L;
            this.bgcount = 0L;
            this.suspendbgcount = 0L;
            this.foreground = false;
        }
    }

    private class UidWakeReason {
        public String WakeReasons;
        public long count;

        private UidWakeReason() {
            this.count = 1L;
        }
    }

    static {
        boolean zIsLoggable = Log.isLoggable("debug.wakeup", 3);
        DEBUG = zIsLoggable;
        CURRENTNOW_STANDARD = zIsLoggable ? 1L : 20L;
        BigdataListener = new ArrayList<>();
        JOB_WAKEUP_STANDARD = zIsLoggable ? 1 : 10;
        ALARM_WAKEUP_STANDARD = zIsLoggable ? 1 : 10;
        SYNC_WAKEUP_STANDARD = zIsLoggable ? 1 : 10;
        mListWakeupInfo = new HashSet();
    }

    public WakeupAnalysis(Context context) {
        this.mContext = null;
        this.mEnabled = false;
        this.FGBG_TIME = DEBUG ? 3600000 : ac.f1522c;
        this.mSupportDeviceList = new ArrayList(Arrays.asList("sheng"));
        this.mIsSupportSysControll = false;
        this.mSyncCloundEnable = false;
        this.mPacketMapAlarm = new HashSet();
        this.mPacketMapJob = new HashSet();
        this.mPacketMapSync = new HashSet();
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.miui.powerkeeper.resource.bigdata.resourcelight.WakeupAnalysis.3
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (WakeupAnalysis.this.mEnabled) {
                    intent.getAction();
                    if (intent.getAction().equals(WakeupAnalysis.this.FGBGALARM_ACTION)) {
                        WakeupAnalysis.this.setTwelveFgbgAlarm();
                        WakeupAnalysis.this.mHandler.sendMessageDelayed(WakeupAnalysis.this.mHandler.obtainMessage(0), 1000L);
                        WakeupAnalysis.this.sendSysControllBroadcast(1);
                    }
                }
            }
        };
        this.mReceiveralarm = broadcastReceiver;
        this.mContext = context;
        ResourceInterface.IAppResourceUploadListener iAppResourceUploadListener = new ResourceInterface.IAppResourceUploadListener() { // from class: com.miui.powerkeeper.resource.bigdata.resourcelight.WakeupAnalysis.1
            @Override // com.miui.powerkeeper.resource.bigdata.resourcelight.ResourceInterface.IAppResourceUploadListener
            public void updateBigData() {
                if (WakeupAnalysis.this.mEnabled) {
                    WakeupAnalysis.this.UploadWakeupData();
                }
            }
        };
        this.mAppResourceUploadListener = iAppResourceUploadListener;
        registerResourceUploadistener(iAppResourceUploadListener);
        this.alarmManager = (AlarmManager) this.mContext.getSystemService("alarm");
        this.mHandler = new UidHandler(BackgroundThread.getHandler().getLooper());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(this.FGBGALARM_ACTION);
        this.mContext.registerReceiver(broadcastReceiver, intentFilter, 2);
        this.mEnabled = true;
        boolean zContains = this.mSupportDeviceList.contains(Build.DEVICE);
        this.mIsSupportSysControll = zContains;
        if (zContains) {
            updateSysControll();
            registerSysControllObserver();
        }
    }

    private void checkStardard(String str, JSONObject jSONObject) {
        long jOptLong = jSONObject.optLong("bgcount", 0L) + jSONObject.optLong("suspendbgcount", 0L);
        if (jOptLong > ALARM_WAKEUP_STANDARD || jOptLong > JOB_WAKEUP_STANDARD || jOptLong > SYNC_WAKEUP_STANDARD) {
            Log.e(TAG_OPT, str + " wakeup is too much pkg = " + jSONObject.optString("packageName"));
        }
    }

    private JSONObject makeJson() {
        try {
            JSONObject jSONObject = new JSONObject();
            JSONArray jSONArray = new JSONArray();
            JSONArray jSONArray2 = new JSONArray();
            JSONArray jSONArray3 = new JSONArray();
            JSONArray jSONArray4 = new JSONArray();
            Iterator<UidPacket> it = this.mPacketMapAlarm.iterator();
            while (it.hasNext()) {
                UidPacket next = it.next();
                Iterator<UidPacket> it2 = it;
                if (!ResourceUploader.getInstance(this.mContext).getPackageVersionChange(next.packageName) && !ResourceUploader.getInstance(this.mContext).getUidVersionChange(next.uid)) {
                    JSONObject jSONObject2 = new JSONObject();
                    jSONObject2.put("packageName", next.packageName);
                    jSONObject2.put("fgcount", next.fgcount);
                    jSONObject2.put("bgcount", next.bgcount);
                    jSONObject2.put("suspendfgcount", next.suspendfgcount);
                    jSONObject2.put("suspendbgcount", next.suspendbgcount);
                    jSONObject2.put("packageVersion", next.packageVersion);
                    jSONArray.put(jSONObject2);
                    checkStardard("alarm", jSONObject2);
                }
                it = it2;
            }
            jSONObject.put("alarm", jSONArray);
            for (UidPacket uidPacket : this.mPacketMapSync) {
                if (!ResourceUploader.getInstance(this.mContext).getPackageVersionChange(uidPacket.packageName) && !ResourceUploader.getInstance(this.mContext).getUidVersionChange(uidPacket.uid)) {
                    JSONObject jSONObject3 = new JSONObject();
                    jSONObject3.put("packageName", uidPacket.packageName);
                    jSONObject3.put("fgcount", uidPacket.fgcount);
                    jSONObject3.put("bgcount", uidPacket.bgcount);
                    jSONObject3.put("suspendfgcount", uidPacket.suspendfgcount);
                    jSONObject3.put("suspendbgcount", uidPacket.suspendbgcount);
                    jSONObject3.put("packageVersion", uidPacket.packageVersion);
                    jSONArray2.put(jSONObject3);
                    checkStardard("sync", jSONObject3);
                    jSONArray3 = jSONArray3;
                    jSONArray4 = jSONArray4;
                }
            }
            JSONArray jSONArray5 = jSONArray3;
            JSONArray jSONArray6 = jSONArray4;
            jSONObject.put("sync", jSONArray2);
            for (UidPacket uidPacket2 : this.mPacketMapJob) {
                if (!ResourceUploader.getInstance(this.mContext).getPackageVersionChange(uidPacket2.packageName) && !ResourceUploader.getInstance(this.mContext).getUidVersionChange(uidPacket2.uid)) {
                    JSONObject jSONObject4 = new JSONObject();
                    jSONObject4.put("packageName", uidPacket2.packageName);
                    jSONObject4.put("fgcount", uidPacket2.fgcount);
                    jSONObject4.put("bgcount", uidPacket2.bgcount);
                    jSONObject4.put("suspendfgcount", uidPacket2.suspendfgcount);
                    jSONObject4.put("suspendbgcount", uidPacket2.suspendbgcount);
                    jSONObject4.put("packageVersion", uidPacket2.packageVersion);
                    JSONArray jSONArray7 = jSONArray5;
                    jSONArray7.put(jSONObject4);
                    checkStardard("job", jSONObject4);
                    jSONArray5 = jSONArray7;
                }
            }
            jSONObject.put("job", jSONArray5);
            for (UidWakeReason uidWakeReason : mListWakeupInfo) {
                JSONObject jSONObject5 = new JSONObject();
                jSONObject5.put("Reasons", uidWakeReason.WakeReasons);
                jSONObject5.put("count", uidWakeReason.count);
                JSONArray jSONArray8 = jSONArray6;
                jSONArray8.put(jSONObject5);
                jSONArray6 = jSONArray8;
            }
            jSONObject.put("Reasons", jSONArray6);
            return jSONObject;
        } catch (Exception e2) {
            Log.e(TAG, "makeJson error", e2);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyBigdataListener() {
        int size = BigdataListener.size();
        for (int i2 = 0; i2 < size; i2++) {
            BigdataListener.get(i2).updateBigData();
        }
    }

    private void registerSysControllObserver() {
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(CLOUD_SYNC_JOB_ALARM_CONTROL), false, new ContentObserver(this.mHandler) { // from class: com.miui.powerkeeper.resource.bigdata.resourcelight.WakeupAnalysis.2
            @Override // android.database.ContentObserver
            public void onChange(boolean z2, Uri uri) {
                Log.d(WakeupAnalysis.TAG, "Set updateSysControll from cloud control uri : " + uri);
                if (uri == null || !uri.equals(Settings.System.getUriFor(WakeupAnalysis.CLOUD_SYNC_JOB_ALARM_CONTROL))) {
                    return;
                }
                WakeupAnalysis.this.updateSysControll();
            }
        }, -2);
    }

    private void resetData() {
        this.mPacketMapAlarm.clear();
        this.mPacketMapJob.clear();
        this.mPacketMapSync.clear();
        mListWakeupInfo.clear();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendSysControllBroadcast(int i2) {
        Intent intent = new Intent(ACTION_SYS_CONTROLL_JOB_SYNC_ALARM);
        intent.putExtra("uid", i2);
        this.mContext.sendBroadcast(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSysControll() {
        String stringForUser = Settings.System.getStringForUser(this.mContext.getContentResolver(), CLOUD_SYNC_JOB_ALARM_CONTROL, -2);
        if (stringForUser == null || stringForUser.equals("1")) {
            sendSysControllBroadcast(1);
            this.mSyncCloundEnable = true;
        } else if (stringForUser.equals("0")) {
            sendSysControllBroadcast(0);
            this.mSyncCloundEnable = false;
        }
        Log.d(TAG, "Set updateSysControll syncEnable:" + stringForUser + " mSyncCloundEnable:" + this.mSyncCloundEnable);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void LogAlarmData(int i2, boolean z2) {
        for (UidPacket uidPacket : this.mPacketMapAlarm) {
            if (i2 == uidPacket.uid) {
                uidPacket.foreground = z2;
                if (z2) {
                    if (this.suspend) {
                        uidPacket.suspendfgcount++;
                    } else {
                        uidPacket.fgcount++;
                    }
                } else if (this.suspend) {
                    uidPacket.suspendbgcount++;
                } else {
                    uidPacket.bgcount++;
                }
                this.suspend = false;
                return;
            }
        }
        Pattern patternCompile = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");
        UidPacket uidPacket2 = new UidPacket();
        String packageNameByUid = PackageUtil.getPackageNameByUid(this.mContext, i2);
        String appVersion = patternCompile.matcher(packageNameByUid).matches() ? null : PackageUtil.getAppVersion(this.mContext.getPackageManager(), packageNameByUid);
        uidPacket2.foreground = z2;
        if (z2) {
            if (this.suspend) {
                uidPacket2.suspendfgcount++;
            } else {
                uidPacket2.fgcount++;
            }
        } else if (this.suspend) {
            uidPacket2.suspendbgcount++;
        } else {
            uidPacket2.bgcount++;
        }
        this.suspend = false;
        uidPacket2.packageName = packageNameByUid;
        uidPacket2.packageVersion = appVersion;
        uidPacket2.uid = i2;
        this.mPacketMapAlarm.add(uidPacket2);
    }

    public void LogDetailsWakeupData(Bundle bundle) {
        int i2 = bundle.getInt("uid");
        String string = bundle.getString("type");
        String string2 = bundle.getString("name");
        boolean uidState = ResourceUploader.getInstance(this.mContext).getUidState(i2);
        if (string.equals("walarm")) {
            LogAlarmData(i2, uidState);
        } else if (string.equals("job")) {
            LognetjobData(i2, uidState, string2);
        } else if (string.equals("sync")) {
            LognetsyncData(i2, uidState, string2);
        }
    }

    public void LogWakeupData(Bundle bundle) {
        String string = bundle.getString("reason");
        if (string == null) {
            return;
        }
        for (UidWakeReason uidWakeReason : mListWakeupInfo) {
            if (string.equals(uidWakeReason.WakeReasons)) {
                uidWakeReason.count++;
                return;
            }
        }
        UidWakeReason uidWakeReason2 = new UidWakeReason();
        uidWakeReason2.WakeReasons = string;
        mListWakeupInfo.add(uidWakeReason2);
        this.suspend = true;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void LognetjobData(int i2, boolean z2, String str) {
        for (UidPacket uidPacket : this.mPacketMapJob) {
            if (i2 == uidPacket.uid && str != null && str.equals(uidPacket.packageName)) {
                uidPacket.foreground = z2;
                if (z2) {
                    if (this.suspend) {
                        uidPacket.suspendfgcount++;
                    } else {
                        uidPacket.fgcount++;
                    }
                } else if (this.suspend) {
                    uidPacket.suspendbgcount++;
                } else {
                    uidPacket.bgcount++;
                }
                this.suspend = false;
                return;
            }
        }
        Pattern patternCompile = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");
        UidPacket uidPacket2 = new UidPacket();
        String packageNameByUid = PackageUtil.getPackageNameByUid(this.mContext, i2);
        String appVersion = patternCompile.matcher(packageNameByUid).matches() ? null : PackageUtil.getAppVersion(this.mContext.getPackageManager(), packageNameByUid);
        uidPacket2.foreground = z2;
        if (z2) {
            if (this.suspend) {
                uidPacket2.suspendfgcount++;
            } else {
                uidPacket2.fgcount++;
            }
        } else if (this.suspend) {
            uidPacket2.suspendbgcount++;
        } else {
            uidPacket2.bgcount++;
        }
        this.suspend = false;
        uidPacket2.packageName = packageNameByUid;
        if (str != null) {
            uidPacket2.packageName = str;
        }
        uidPacket2.packageVersion = appVersion;
        uidPacket2.uid = i2;
        this.mPacketMapJob.add(uidPacket2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void LognetsyncData(int i2, boolean z2, String str) {
        for (UidPacket uidPacket : this.mPacketMapSync) {
            if (i2 == uidPacket.uid && str != null && str.equals(uidPacket.packageName)) {
                uidPacket.foreground = z2;
                if (!z2) {
                    if (this.suspend) {
                        uidPacket.suspendbgcount++;
                    } else {
                        uidPacket.bgcount++;
                    }
                    if (this.mEnabled && this.mIsSupportSysControll && this.mSyncCloundEnable && uidPacket.suspendbgcount + uidPacket.bgcount >= 10) {
                        sendSysControllBroadcast(i2);
                        Log.d(TAG, "LognetsyncData disallow  uid=" + i2 + " detail=" + str);
                    }
                } else if (this.suspend) {
                    uidPacket.suspendfgcount++;
                } else {
                    uidPacket.fgcount++;
                }
                this.suspend = false;
                return;
            }
        }
        Pattern patternCompile = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");
        UidPacket uidPacket2 = new UidPacket();
        String packageNameByUid = PackageUtil.getPackageNameByUid(this.mContext, i2);
        String appVersion = patternCompile.matcher(packageNameByUid).matches() ? null : PackageUtil.getAppVersion(this.mContext.getPackageManager(), packageNameByUid);
        uidPacket2.foreground = z2;
        if (z2) {
            if (this.suspend) {
                uidPacket2.suspendfgcount++;
            } else {
                uidPacket2.fgcount++;
            }
        } else if (this.suspend) {
            uidPacket2.suspendbgcount++;
        } else {
            uidPacket2.bgcount++;
        }
        this.suspend = false;
        uidPacket2.packageName = packageNameByUid;
        if (str != null) {
            uidPacket2.packageName = str;
        }
        uidPacket2.packageVersion = appVersion;
        uidPacket2.uid = i2;
        this.mPacketMapSync.add(uidPacket2);
    }

    public void UploadWakeupData() {
        JSONObject jSONObjectMakeJson = makeJson();
        if (jSONObjectMakeJson == null) {
            Log.d(TAG, "json null error");
        } else {
            TrackerManager.getInstance(this.mContext).track(3, TrackerConfig.EVENT_WAKEUP_REASONS, jSONObjectMakeJson);
            resetData();
        }
    }

    public void cancelTwelveFgbgAlarm() {
        if (this.mContext == null) {
            Log.d(TAG, "cancelTwelveFgbgAlarm context null");
            return;
        }
        this.alarmManager.cancel(PendingIntent.getBroadcast(this.mContext, 0, new Intent(this.FGBGALARM_ACTION), 201326592));
    }

    public void dump(PrintWriter printWriter, String[] strArr) {
        printWriter.println("WakeupAnalysis dump start >>>>>>");
        StringBuilder sb = new StringBuilder();
        sb.append(" walarm: ");
        sb.append(Constants.ENDLINE);
        for (UidPacket uidPacket : this.mPacketMapAlarm) {
            sb.append("packageName ");
            sb.append(uidPacket.packageName);
            sb.append(" fgcount ");
            sb.append(uidPacket.fgcount);
            sb.append(" bgcount ");
            sb.append(uidPacket.bgcount);
            sb.append(" suspendfgcount ");
            sb.append(uidPacket.suspendfgcount);
            sb.append(" suspendbgcount ");
            sb.append(uidPacket.suspendbgcount);
            sb.append(" packageVersion ");
            sb.append(uidPacket.packageVersion);
            sb.append(Constants.ENDLINE);
        }
        sb.append(" job: ");
        sb.append(Constants.ENDLINE);
        for (UidPacket uidPacket2 : this.mPacketMapJob) {
            sb.append("packageName ");
            sb.append(uidPacket2.packageName);
            sb.append(" fgcount ");
            sb.append(uidPacket2.fgcount);
            sb.append(" bgcount ");
            sb.append(uidPacket2.bgcount);
            sb.append(" suspendfgcount ");
            sb.append(uidPacket2.suspendfgcount);
            sb.append(" suspendbgcount ");
            sb.append(uidPacket2.suspendbgcount);
            sb.append(" packageVersion ");
            sb.append(uidPacket2.packageVersion);
            sb.append(Constants.ENDLINE);
        }
        sb.append(" sync: ");
        sb.append(Constants.ENDLINE);
        for (UidPacket uidPacket3 : this.mPacketMapSync) {
            sb.append("packageName ");
            sb.append(uidPacket3.packageName);
            sb.append(" fgcount ");
            sb.append(uidPacket3.fgcount);
            sb.append(" bgcount ");
            sb.append(uidPacket3.bgcount);
            sb.append(" suspendfgcount ");
            sb.append(uidPacket3.suspendfgcount);
            sb.append(" suspendbgcount ");
            sb.append(uidPacket3.suspendbgcount);
            sb.append(" packageVersion ");
            sb.append(uidPacket3.packageVersion);
            sb.append(Constants.ENDLINE);
        }
        sb.append(" wakeupReasons: ");
        sb.append(Constants.ENDLINE);
        for (UidWakeReason uidWakeReason : mListWakeupInfo) {
            sb.append("Reasons ");
            sb.append(uidWakeReason.WakeReasons);
            sb.append(" count ");
            sb.append(uidWakeReason.count);
            sb.append(Constants.ENDLINE);
        }
        printWriter.println(sb.toString());
        printWriter.println();
    }

    public void onDisable() {
        this.mEnabled = false;
        Log.d(TAG, "onDisable");
        this.mHandler.removeCallbacksAndMessages(null);
        cancelTwelveFgbgAlarm();
        sendSysControllBroadcast(0);
    }

    public void onEnable() {
        this.mEnabled = true;
        Log.d(TAG, "onEnable");
    }

    public void postinit() {
        setTwelveFgbgAlarm();
    }

    public void registerResourceUploadistener(ResourceInterface.IAppResourceUploadListener iAppResourceUploadListener) {
        if (iAppResourceUploadListener == null) {
            return;
        }
        synchronized (this.mLock) {
            BigdataListener.add(iAppResourceUploadListener);
        }
    }

    public void setTwelveFgbgAlarm() {
        if (this.mContext == null) {
            Log.d(TAG, "setTwelveFgbgAlarm context null");
            return;
        }
        this.alarmManager.set(2, SystemClock.elapsedRealtime() + ((long) this.FGBG_TIME), PendingIntent.getBroadcast(this.mContext, 0, new Intent(this.FGBGALARM_ACTION), 201326592));
    }
}
