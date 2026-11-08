package com.miui.powerkeeper.millet;

import android.app.ActivityManagerNative;
import android.app.AppOpsManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.ArraySet;
import android.util.Log;
import android.util.Slog;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import android.view.KeyEvent;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.controller.BroadcastController;
import com.miui.powerkeeper.controller.FrozenAppController;
import com.miui.powerkeeper.millet.bright.MilletBrightHandler;
import com.miui.powerkeeper.powerchecker.NightStandbyRecord;
import com.miui.powerkeeper.utils.PackageUtil;
import com.xiaomi.push.extension.PluginConstants;
import com.xiaomi.push.service.MIPushNotificationHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import miui.yellowpage.YellowPageContract;

/* JADX INFO: loaded from: classes.dex */
public class MilletHandler extends Handler {
    private static final int BINDER_DELAYED_TIME = 5000;
    private static final String BINDER_REASON = "receiver binder";
    public static boolean DEBUG = false;
    public static final int MSG_ACTIVITY_START_EVENT = 29;
    public static final int MSG_ADD_FROZENLIST = 1;
    public static final int MSG_BACKUPING = 31;
    public static final int MSG_BACKUP_END = 32;
    public static final int MSG_BATTEY = 16;
    public static final int MSG_BINDER = 6;
    public static final int MSG_BINDER_ST = 5;
    public static final int MSG_BLUETOOTH = 23;
    public static final int MSG_DEVICE_IDLE_ENTRY = 25;
    public static final int MSG_GEO_EVENT = 24;
    public static final int MSG_HANDSHAKE = 17;
    public static final int MSG_KEYCODE = 20;
    public static final int MSG_NEW_PROCESS = 15;
    public static final int MSG_ORDER_BROADCAST = 22;
    public static final int MSG_PKG = 8;
    public static final int MSG_PKG_DELAY_THAWED = 12;
    public static final int MSG_QUERYLIST_ADD = 13;
    public static final int MSG_QUERY_TIMEOUT = 14;
    public static final int MSG_REPEAT_TRY = 30;
    public static final int MSG_RESET_DATA = 10;
    public static final int MSG_SCREEN_OFF = 3;
    public static final int MSG_SCREEN_ON = 2;
    public static final int MSG_SET_PKG_DATA = 11;
    public static final int MSG_SIG = 7;
    public static final int MSG_STATE_INFO = 9;
    public static final int MSG_SYNC = 19;
    public static final int MSG_THAW_OTHER = 18;
    public static final int MSG_USER_PRESENT = 33;
    private static String TAG = "MilletHandler";
    private static final String TIMEOUT_REASON = "nativequery timeout";
    private static boolean isAlreadyScreenOn = true;
    private Map<Integer, Set<Integer>> busyPids;
    private SparseIntArray busyStats;
    private CountUidCpuRuntime cpuRuntime;
    private boolean enabled;
    private Set<Integer> failedFrozenList;
    private Set<Integer> frozenList;
    private boolean isInitBroadcast;
    private AppOpsManager mAppOpsManager;
    private boolean mBinderStateRecieved;
    private BroadcastController mBroadcastCtrl;
    private FrozenAppController mCtrl;
    private Context mCtx;
    private int mCurrQueryUid;
    private boolean mHandshake;
    private SparseLongArray mPendingUidTime;
    private MilletPolicy mPolicy;
    private PowerKeeperManager manager;
    private Set<Integer> pkgReportList;
    private PowerManager pm;
    private Set<Integer> queryList;
    private long queryTime;
    private int queryTimeoutCnt;
    private MilletUidObserver uidOb;
    private PowerManager.WakeLock wl;

    static {
        DEBUG = Log.isLoggable("MilletHandler", 3) || Build.IS_DEBUGGABLE;
    }

    public MilletHandler(Looper looper, FrozenAppController frozenAppController, MilletPolicy milletPolicy, PowerKeeperManager powerKeeperManager, Context context) {
        super(looper);
        this.queryTime = 0L;
        this.queryTimeoutCnt = 0;
        this.pkgReportList = new HashSet();
        this.queryList = new HashSet();
        this.frozenList = new HashSet();
        this.failedFrozenList = new HashSet();
        this.mCtrl = null;
        this.mPolicy = null;
        this.wl = null;
        this.pm = null;
        this.mBroadcastCtrl = null;
        this.manager = null;
        this.mCtx = null;
        this.cpuRuntime = null;
        this.enabled = false;
        this.isInitBroadcast = false;
        this.mBinderStateRecieved = false;
        this.mCurrQueryUid = -1;
        this.mHandshake = false;
        this.uidOb = null;
        this.busyStats = new SparseIntArray();
        this.busyPids = new HashMap();
        this.mPendingUidTime = new SparseLongArray();
        this.mAppOpsManager = null;
        this.mCtrl = frozenAppController;
        this.mCtx = context;
        this.cpuRuntime = new CountUidCpuRuntime(context);
        PowerManager powerManager = (PowerManager) context.getSystemService(NightStandbyRecord.KEY_POWER);
        this.pm = powerManager;
        this.wl = powerManager.newWakeLock(1, "binder query");
        this.mPolicy = milletPolicy;
        this.manager = powerKeeperManager;
        this.uidOb = new MilletUidObserver(context, this, powerKeeperManager);
    }

    private boolean checkCanFrozen(int i2, String str, Message message) {
        if (!this.enabled || !MilletUtils.isAppUid(i2)) {
            if (!this.enabled) {
                Slog.d(TAG, "checkCanFrozen enabled=" + this.enabled + " screen=" + isAlreadyScreenOn);
            }
            return false;
        }
        if ((str.equals("binder_st") && message.getData().getInt("reason") == 1) || FreezeBinder.getInstance().isUidFrozen(i2) || str.equals("package")) {
            return true;
        }
        if (DEBUG) {
            Slog.w(TAG, "checkCanFrozen uid=" + i2 + " reason=" + str);
        }
        return false;
    }

    private boolean checkSystemAlert(int i2) {
        if (this.mAppOpsManager == null) {
            try {
                this.mAppOpsManager = (AppOpsManager) this.mCtx.getSystemService(AppOpsManager.class);
            } catch (Exception e2) {
                Log.e(TAG, "get AppOpsManager error", e2);
            }
            if (this.mAppOpsManager == null) {
                return false;
            }
        }
        return this.mAppOpsManager.checkOpNoThrow(24, i2, MilletUidObserver.getPkgNameByUid(i2)) == 0;
    }

    private void controlRes(List<Integer> list) {
        if (!isAlreadyScreenOn && this.enabled && this.mCtrl.getChangeFrozenStrategy()) {
            if (!this.isInitBroadcast) {
                this.isInitBroadcast = true;
                if (this.mBroadcastCtrl == null) {
                    this.mBroadcastCtrl = this.manager.getBroadcastController();
                }
                this.mBroadcastCtrl.checkWhetstoneAMProxy(true);
            }
            for (Integer num : list) {
                boolean zIsMessageApp = isMessageApp(num.intValue());
                this.mPendingUidTime.delete(num.intValue());
                this.mCtrl.updateWakelockBlockedUid(num.intValue(), true);
                if (this.mBroadcastCtrl != null && !MilletConfig.getBroadcastCancel()) {
                    this.mBroadcastCtrl.setBroadcastControlStat(num.intValue(), true);
                    if (zIsMessageApp) {
                        this.mBroadcastCtrl.setBroadcastRestrict(num.intValue(), MilletUtils.getRestrictActions());
                    } else {
                        this.mBroadcastCtrl.setBroadcastRestrict(num.intValue(), null);
                    }
                }
                if (!this.pkgReportList.contains(num)) {
                    this.pkgReportList.add(num);
                    this.mPolicy.dealPacket(num.intValue(), true, this);
                }
            }
        }
        if (hasMessages(30) || this.failedFrozenList.size() <= 0) {
            return;
        }
        sendMessageDelayed(obtainMessage(30), PluginConstants.TTS_REMOVE_BEAN_TIME);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void frozen(Set<Integer> set, long j2, String str) {
        if (!this.enabled || MilletUtils.checkHuanji(this.mCtx)) {
            if (DEBUG) {
                Log.d(TAG, "uids =" + set.toString() + " active=" + this.pm.isInteractive() + " enable=" + this.enabled);
                return;
            }
            return;
        }
        if (!this.mCtrl.getChangeFrozenStrategy()) {
            if (this.mCtrl.getFrozenCondition() != 41) {
                if (DEBUG) {
                    Log.d(TAG, "uids =" + set.toString() + " str:" + this.mCtrl.getFrozenCondition());
                    return;
                }
                return;
            }
            Iterator<Integer> it = set.iterator();
            while (it.hasNext()) {
                if (!this.mPolicy.isGameUid(it.next().intValue(), this.mCtx)) {
                    it.remove();
                }
            }
            if (set.size() == 0) {
                return;
            }
        }
        if (isAlreadyScreenOn && str != null && str.contains("frozenMillet")) {
            return;
        }
        if (set.size() == 1) {
            Integer next = set.iterator().next();
            if (this.mPolicy.checkIntervalThawTime(next.intValue())) {
                this.failedFrozenList.add(next);
                return;
            }
        }
        synchronized (this.failedFrozenList) {
            set.addAll(this.failedFrozenList);
            this.failedFrozenList.clear();
        }
        set.removeAll(FreezeBinder.getInstance().getFrozenUidsPower());
        Iterator<Integer> it2 = set.iterator();
        while (it2.hasNext()) {
            Integer next2 = it2.next();
            int iIntValue = next2.intValue();
            if (this.mPolicy.isInWhiteList(iIntValue)) {
                it2.remove();
            } else if (this.mPolicy.isNotAllowFrozenMusic(iIntValue)) {
                synchronized (this.failedFrozenList) {
                    this.failedFrozenList.add(next2);
                }
                it2.remove();
            } else if (this.mPolicy.isAllowFreeze(iIntValue)) {
                boolean zIsInteractive = this.pm.isInteractive();
                boolean isSystemApp = MilletUidObserver.getIsSystemApp(iIntValue);
                if (zIsInteractive && isSystemApp) {
                    Log.d(TAG, "frozen active=" + zIsInteractive + " sys=" + isSystemApp + " uid=" + iIntValue);
                    it2.remove();
                }
            } else if (!PowerKeeperManager.getInstance().isUidForeground(iIntValue) || MilletUidObserver.getIsSystemApp(iIntValue)) {
                it2.remove();
            }
        }
        if (set.size() == 0) {
            if (hasMessages(30) || this.failedFrozenList.size() <= 0) {
                return;
            }
            sendMessageDelayed(obtainMessage(30), PluginConstants.TTS_REMOVE_BEAN_TIME);
            return;
        }
        int[] array = set.stream().mapToInt(new com.miui.powerkeeper.c()).toArray();
        ArrayList arrayList = new ArrayList();
        Set<Integer> frozenUidsAll = FreezeBinder.getInstance().getFrozenUidsAll();
        if (array.length <= 0) {
            if (hasMessages(30) || this.failedFrozenList.size() <= 0) {
                return;
            }
            sendMessageDelayed(obtainMessage(30), PluginConstants.TTS_REMOVE_BEAN_TIME);
            return;
        }
        acquireLock(array.length * 5);
        arrayList.addAll(FreezeBinder.getInstance().freezeUids(array, 0L, str, false));
        this.failedFrozenList.removeAll(arrayList);
        ArrayList arrayList2 = new ArrayList();
        if (arrayList.size() != array.length) {
            for (int i2 : array) {
                if (!arrayList.contains(Integer.valueOf(i2)) && MilletUtils.getPidsForUid(i2, this.mCtx).length > 0) {
                    this.failedFrozenList.add(Integer.valueOf(i2));
                    arrayList2.add(Integer.valueOf(i2));
                }
            }
            if (this.failedFrozenList.size() > 0) {
                synchronized (this.failedFrozenList) {
                    try {
                        if (DEBUG) {
                            Log.d(TAG, "frozen fail result:" + this.failedFrozenList.toString() + " failApp:" + arrayList2.toString());
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        }
        this.frozenList.addAll(arrayList);
        Set<Integer> frozenUidsPower = FreezeBinder.getInstance().getFrozenUidsPower();
        for (Integer num : arrayList) {
            if (frozenUidsPower.contains(num)) {
                if (frozenUidsAll.contains(num)) {
                    Log.d(TAG, "repeat query uid:" + num);
                } else {
                    Message messageObtain = Message.obtain();
                    messageObtain.what = 13;
                    messageObtain.arg1 = num.intValue();
                    sendMessageDelayed(messageObtain, 200L);
                    Log.d(TAG, "frozen query_add uid =" + num + " reason=" + str);
                }
            }
        }
        controlRes(arrayList);
        this.mPolicy.dealWhiteApp(false, set);
        if (DEBUG) {
            Log.d(TAG, "frozen end " + str + " uids: " + Arrays.toString(array));
        }
    }

    private boolean isMessageApp(int i2) {
        String pkgNameByUid = MilletUidObserver.getPkgNameByUid(i2);
        return pkgNameByUid != null && MilletPolicy.MESSAGE_APPS.contains(pkgNameByUid);
    }

    public static boolean isScreenOn() {
        return isAlreadyScreenOn;
    }

    private void registerUid() {
        try {
            ActivityManagerNative.getDefault().registerUidObserver(this.uidOb, 6, 6, this.mCtx.getOpPackageName());
        } catch (Exception e2) {
            Log.e(TAG, "regis exception", e2);
        }
        ServiceManager.addService(PowerMilletService.SERVICE_NAME, new PowerMilletService(this.mCtx, this.mCtrl));
    }

    private void restoreRes(int i2) {
        if (i2 != 2) {
            Log.d(TAG, "removeAll restoreRes event=" + i2);
            byte b2 = hasMessages(2) ? (byte) 2 : hasMessages(3) ? (byte) 1 : (byte) -1;
            removeCallbacksAndMessages(null);
            if (b2 > 0) {
                sendEmptyMessage(b2 != 1 ? 2 : 3);
            }
        }
        ArrayList arrayList = new ArrayList();
        for (Integer num : this.frozenList) {
            int iIntValue = num.intValue();
            if (i2 == 16 && this.mPolicy.isGameUid(iIntValue, this.mCtx)) {
                arrayList.add(num);
            } else {
                this.uidOb.setAppRunInBackGroundOp(iIntValue, false);
                BroadcastController broadcastController = this.mBroadcastCtrl;
                if (broadcastController != null) {
                    broadcastController.setBroadcastControlStat(iIntValue, false);
                }
                this.mCtrl.updateWakelockBlockedUid(iIntValue, false);
            }
        }
        MilletPolicy milletPolicy = this.mPolicy;
        if (milletPolicy != null) {
            milletPolicy.dealWhiteApp(true, this.frozenList);
            this.mPolicy.resetDelayList();
        }
        this.pkgReportList.clear();
        this.frozenList.clear();
        if (i2 == 2) {
            this.frozenList.addAll(FreezeBinder.getInstance().getFrozenUidsPower());
        } else if (i2 == 16) {
            this.frozenList.addAll(arrayList);
        }
        this.queryList.clear();
    }

    private void thawed(Set<Integer> set, long j2, String str) {
        if (set.size() > 0) {
            acquireLock(set.size() * 5);
            this.mPolicy.dealWhiteApp(true, set);
            int[] array = set.stream().mapToInt(new com.miui.powerkeeper.c()).toArray();
            boolean zContains = str.contains("signal");
            List<Integer> arrayList = new ArrayList<>();
            if (!zContains) {
                arrayList = FreezeBinder.getInstance().thawUids(array, str);
            }
            if (arrayList.size() != array.length && !zContains) {
                set.retainAll(arrayList);
            }
            this.frozenList.removeAll(set);
            this.failedFrozenList.removeAll(set);
            for (int i2 : array) {
                this.mPolicy.getThawTime().put(i2, SystemClock.uptimeMillis());
                this.mCtrl.updateWakelockBlockedUid(i2, false);
                if (!zContains) {
                    this.uidOb.setAppRunInBackGroundOp(i2, false);
                }
                BroadcastController broadcastController = this.mBroadcastCtrl;
                if (broadcastController != null) {
                    broadcastController.setBroadcastControlStat(i2, false);
                }
            }
            if (DEBUG) {
                Log.d(TAG, "thawed end " + str + " uids:" + Arrays.toString(array));
            }
        }
    }

    private void thawedAndDelay(final Set<Integer> set, final long j2, final String str) {
        final HashSet hashSet = new HashSet();
        hashSet.addAll(set);
        if (set.size() == 0) {
            return;
        }
        acquireLock(hashSet.size() * 5);
        Iterator<Integer> it = hashSet.iterator();
        while (it.hasNext()) {
            int iIntValue = it.next().intValue();
            if (SystemClock.uptimeMillis() + j2 > this.mPendingUidTime.get(iIntValue)) {
                this.mPendingUidTime.put(iIntValue, SystemClock.uptimeMillis() + j2);
            }
        }
        this.failedFrozenList.removeAll(hashSet);
        Iterator<Integer> it2 = hashSet.iterator();
        while (it2.hasNext()) {
            MilletUtils.setPkgData(it2.next().intValue(), this, (int) j2);
        }
        postDelayed(new Runnable() { // from class: com.miui.powerkeeper.millet.MilletHandler.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    if (MilletHandler.isAlreadyScreenOn && MilletHandler.TIMEOUT_REASON.equals(str) && MilletHandler.this.mCtrl.getBrightCtrl() != null) {
                        MilletBrightHandler milletBrightHandler = (MilletBrightHandler) MilletHandler.this.mCtrl.getBrightCtrl().getHandler();
                        if (milletBrightHandler == null) {
                            return;
                        }
                        Iterator it3 = hashSet.iterator();
                        while (it3.hasNext()) {
                            Message messageObtainMessage = milletBrightHandler.obtainMessage(1, ((Integer) it3.next()).intValue(), 1);
                            messageObtainMessage.obj = MilletHandler.TIMEOUT_REASON;
                            milletBrightHandler.sendMessage(messageObtainMessage);
                        }
                        return;
                    }
                    HashSet hashSet2 = new HashSet();
                    for (Integer num : set) {
                        int iIntValue2 = num.intValue();
                        if (!MilletHandler.isAlreadyScreenOn || (!MilletUtils.isTopApp(iIntValue2) && iIntValue2 != MilletHandler.this.mCtrl.getBrightCtrl().getCurUid())) {
                            if (SystemClock.uptimeMillis() >= MilletHandler.this.mPendingUidTime.get(iIntValue2)) {
                                hashSet2.add(num);
                                MilletHandler.this.mPendingUidTime.delete(iIntValue2);
                            }
                        }
                    }
                    if (hashSet2.size() > 0) {
                        MilletHandler.this.frozen(hashSet2, j2, str);
                    }
                    Iterator it4 = hashSet.iterator();
                    while (it4.hasNext()) {
                        MilletHandler.this.mPolicy.dealPacket(((Integer) it4.next()).intValue(), true, MilletHandler.this);
                    }
                } catch (Exception e2) {
                    Log.e(MilletHandler.TAG, "thawedAndDelay err", e2);
                }
            }
        }, j2);
        thawed(hashSet, 0L, str);
    }

    private void thawedPidsAndDelay(int[] iArr, int i2, long j2, String str) {
        HashSet hashSet = new HashSet();
        hashSet.add(Integer.valueOf(i2));
        thawedAndDelay(hashSet, j2, str);
    }

    private void unRegisterUid() {
        try {
            ActivityManagerNative.getDefault().unregisterUidObserver(this.uidOb);
        } catch (Exception e2) {
            Log.e(TAG, "unRegis exception", e2);
        }
    }

    public void addFailedList(Set<Integer> set) {
        for (Integer num : set) {
            synchronized (this.failedFrozenList) {
                this.failedFrozenList.add(num);
            }
        }
        set.clear();
    }

    public FrozenAppController getFrozenCtrl() {
        return this.mCtrl;
    }

    public MilletUidObserver getUidObserver() {
        return this.uidOb;
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        String str = "handleMessage what = " + message.what;
        int i2 = message.what;
        if (i2 == -27) {
            int i3 = message.arg1;
            if (!MilletUtils.isAppUid(i3)) {
                Log.d(TAG, "disguise err uid = " + i3);
                return;
            }
            if (!isAlreadyScreenOn || this.mCtrl.getBrightCtrl() == null) {
                HashSet hashSet = new HashSet();
                hashSet.add(Integer.valueOf(i3));
                frozen(hashSet, 0L, "disguise");
                return;
            } else {
                MilletBrightHandler milletBrightHandler = (MilletBrightHandler) this.mCtrl.getBrightCtrl().getHandler();
                if (milletBrightHandler != null) {
                    milletBrightHandler.sendMessage(milletBrightHandler.obtainMessage(-27, Integer.valueOf(i3)));
                    return;
                }
                return;
            }
        }
        char c2 = 1;
        if (i2 == 1) {
            int i4 = message.arg1;
            final HashSet hashSet2 = new HashSet();
            if (MilletUtils.isAppUid(i4) && (message.obj instanceof String)) {
                hashSet2.add(Integer.valueOf(message.arg1));
                final String str2 = (String) message.obj;
                int i5 = message.arg2;
                if (i5 != 1) {
                    if (i5 == 0) {
                        thawed(hashSet2, 0L, str2);
                        return;
                    }
                    return;
                } else if (str2 != null && str2.contains("frozenMillet") && "com.android.camera".equals(MilletUidObserver.getPkgNameByUid(message.arg1))) {
                    postDelayed(new Runnable() { // from class: com.miui.powerkeeper.millet.MilletHandler.4
                        @Override // java.lang.Runnable
                        public void run() {
                            MilletHandler.this.frozen(hashSet2, 0L, str2);
                        }
                    }, 35000L);
                    return;
                } else {
                    frozen(hashSet2, 0L, str2);
                    return;
                }
            }
            return;
        }
        if (i2 == 2) {
            if (DEBUG) {
                Log.d(TAG, "MSG_SCREEN_ON " + this.mCtrl.getChangeFrozenStrategy());
            }
            if (isAlreadyScreenOn) {
                return;
            }
            isAlreadyScreenOn = true;
            if (this.enabled) {
                HashSet hashSet3 = new HashSet();
                hashSet3.clear();
                HashSet hashSet4 = new HashSet();
                for (Integer num : FreezeBinder.getInstance().getFrozenUidsPower()) {
                    int iIntValue = num.intValue();
                    if (PowerKeeperManager.getInstance().getProcessObserver().isUidForegroundService(iIntValue) && this.mPolicy.getSpecialBusiness().contains(num)) {
                        hashSet4.add(num);
                    } else if (MilletUtils.isTopApp(iIntValue) || iIntValue == this.mPolicy.getWallpaper() || PowerKeeperManager.getInstance().isUidForeground(iIntValue) || this.mPolicy.isBrightWhite(iIntValue) || ((this.mCtrl.getBrightCtrl() != null && iIntValue == this.mCtrl.getBrightCtrl().getCurUid()) || MilletUtils.isWidget(iIntValue) || MilletUidObserver.getIsSystemApp(iIntValue))) {
                        hashSet3.add(num);
                    }
                }
                if (hashSet3.size() > 0) {
                    thawed(hashSet3, 0L, "screen on");
                }
                if (hashSet4.size() > 0) {
                    thawedAndDelay(hashSet4, PluginConstants.TTS_REMOVE_BEAN_TIME, "screen on");
                }
            }
            restoreRes(2);
            this.uidOb.setScreenState(isAlreadyScreenOn);
            this.mPolicy.checkSleepMode();
            this.mCtrl.notifyEvent(11, null);
            if (this.mCtrl.getBrightCtrl() != null) {
                MilletBrightHandler milletBrightHandler2 = (MilletBrightHandler) this.mCtrl.getBrightCtrl().getHandler();
                if (milletBrightHandler2 != null) {
                    milletBrightHandler2.sendMessage(milletBrightHandler2.obtainMessage(2));
                }
                this.mCtrl.getBrightCtrl().setScreenState(true);
                removeMessages(30);
                if (this.failedFrozenList.size() > 0) {
                    synchronized (this.failedFrozenList) {
                        Log.d(TAG, "ScreenOn failedFrozenList tranf:" + this.failedFrozenList.toString());
                    }
                }
                if (this.failedFrozenList.size() <= 0 || milletBrightHandler2 == null) {
                    return;
                }
                milletBrightHandler2.addFailedList(this.failedFrozenList);
                milletBrightHandler2.sendMessage(milletBrightHandler2.obtainMessage(30));
                return;
            }
            return;
        }
        if (i2 == 3) {
            if (DEBUG) {
                Log.d(TAG, "MSG_SCREEN_OFF " + isAlreadyScreenOn);
            }
            if (isAlreadyScreenOn || message.obj != null) {
                if (this.mCtrl.getBrightCtrl() != null && this.mCtrl.getBrightCtrl().getBackupState()) {
                    sendMessage(obtainMessage(31));
                }
                this.mPolicy.dealFindDeviceOpen(this.mCtx);
                isAlreadyScreenOn = false;
                this.uidOb.setScreenState(false);
                this.mCtrl.notifyEvent(12, null);
                if (this.mCtrl.getBrightCtrl() != null) {
                    MilletBrightHandler milletBrightHandler3 = (MilletBrightHandler) this.mCtrl.getBrightCtrl().getHandler();
                    if (milletBrightHandler3 != null) {
                        milletBrightHandler3.sendMessage(milletBrightHandler3.obtainMessage(3));
                    }
                    this.mCtrl.getBrightCtrl().setScreenState(false);
                    return;
                }
                return;
            }
            return;
        }
        switch (i2) {
            case 5:
                int i6 = message.getData().getInt("uid");
                if (checkCanFrozen(i6, "binder_st", message)) {
                    this.queryTimeoutCnt = 0;
                    this.mCurrQueryUid = -1;
                    this.mBinderStateRecieved = true;
                    int i7 = message.getData().getInt("reason");
                    if (i7 == 0) {
                        if (!isMessageApp(i6)) {
                            this.uidOb.setAppRunInBackGroundOp(i6, true);
                        }
                        this.busyStats.delete(i6);
                        this.busyPids.remove(Integer.valueOf(i6));
                        return;
                    }
                    if (i7 == 1) {
                        Message messageObtainMessage = obtainMessage(1, i6, 1);
                        messageObtainMessage.obj = "check binder";
                        sendMessageDelayed(messageObtainMessage, PluginConstants.TTS_REMOVE_BEAN_TIME);
                        return;
                    }
                    return;
                }
                return;
            case 6:
                message.getData().getInt("caller_pid");
                int i8 = message.getData().getInt("uid");
                int i9 = message.getData().getInt("caller_uid");
                int i10 = message.getData().getInt("dst_pid");
                char c3 = message.getData().getChar("tf_oneway");
                int i11 = message.getData().getInt("buffer");
                if (checkCanFrozen(i8, "binder tp=" + i10, message)) {
                    if (MilletConfig.isGlobal) {
                        if (i11 != 0 && c3 != 'F') {
                            c2 = (i9 == 1000 || i9 == 1047 || !(MilletUidObserver.getIsSystemApp(i9) || UserHandle.isCore(i9) || PowerKeeperManager.getInstance().getProcessObserver().isUidForegroundService(i9))) ? (char) 0 : (char) 2;
                        }
                        if (c2 == 0) {
                            return;
                        }
                    } else if (i11 < 4 && i11 > 0 && c3 == 'T' && i9 != i8 && i9 != 1002 && i9 != 1027) {
                        return;
                    }
                    thawedPidsAndDelay(new int[]{i10}, i8, PluginConstants.TTS_REMOVE_BEAN_TIME, "receiver binder c=" + c3 + " callerUid=" + i9 + " b=" + i11 + " p=" + i10);
                    return;
                }
                return;
            case 7:
                int i12 = message.getData().getInt("uid");
                HashSet hashSet5 = new HashSet();
                hashSet5.add(Integer.valueOf(i12));
                this.mPolicy.removeBluetoothUid(i12);
                thawed(hashSet5, 0L, "signal");
                return;
            case 8:
                int i13 = message.getData().getInt("uid");
                if (checkCanFrozen(i13, "package", message) && this.mPolicy != null && this.pkgReportList.contains(Integer.valueOf(i13))) {
                    HashSet hashSet6 = new HashSet();
                    hashSet6.add(Integer.valueOf(i13));
                    thawedAndDelay(hashSet6, 10000L, "receiver pkg");
                    return;
                }
                return;
            case 9:
                if (this.mPolicy != null) {
                    Log.d(TAG, "show state pkgList:" + this.mPolicy.getReportList().toString());
                    Log.d(TAG, "show state pkgDelayList:" + this.mPolicy.getDelayUids().toString());
                }
                Log.d(TAG, "show state pkgReportList:" + this.pkgReportList.toString());
                Log.d(TAG, "show state frozenList:" + this.frozenList.toString());
                Log.d(TAG, "show state queryList:" + this.queryList.toString());
                Log.d(TAG, "enabled:" + this.enabled + " isAlreadyScreenOn=" + isAlreadyScreenOn + " changeStrategy=" + this.mCtrl.getChangeFrozenStrategy());
                return;
            case 10:
                Log.d(TAG, "reset_data");
                FreezeBinder.getInstance().thawAll("reset data", false);
                restoreRes(10);
                unRegisterUid();
                this.mCtrl.setChangeFrozenStrategy(true, 4, false);
                return;
            case 11:
                this.pkgReportList.add(Integer.valueOf(message.arg1));
                Log.d(TAG, "set_pkg_data uid=" + message.arg1 + " " + message.arg2);
                FrozenAppController.callNativeSet(message.arg1, message.arg2);
                return;
            case 12:
                thawedAndDelay(this.mPolicy.getDelayUids(), this.mPolicy.getDelayUids().size() * 10, "pkgOrBinder timeout");
                this.mPolicy.resetDelayList();
                return;
            case 13:
                boolean zContains = this.frozenList.contains(Integer.valueOf(message.arg1));
                if (!zContains) {
                    zContains = FreezeBinder.getInstance().getFrozenUidsPower().contains(Integer.valueOf(message.arg1));
                }
                if (zContains) {
                    this.mCtrl.callNativeQuery(message.arg1);
                    return;
                }
                return;
            default:
                switch (i2) {
                    case 15:
                        if (this.enabled && this.mCtrl.getChangeFrozenStrategy()) {
                            int i14 = message.arg1;
                            HashSet hashSet7 = new HashSet();
                            hashSet7.add(Integer.valueOf(i14));
                            frozen(hashSet7, 30L, "off new process");
                            return;
                        }
                        return;
                    case 16:
                        int i15 = message.arg1;
                        if (i15 == 1) {
                            HashSet hashSet8 = new HashSet();
                            hashSet8.addAll(FreezeBinder.getInstance().getFrozenUidsPower());
                            Iterator<Integer> it = hashSet8.iterator();
                            while (it.hasNext()) {
                                if (this.mPolicy.isGameUid(it.next().intValue(), this.mCtx)) {
                                    it.remove();
                                }
                            }
                            thawed(hashSet8, 0L, "battery charging");
                            restoreRes(16);
                        }
                        Log.d(TAG, "plugged=" + i15);
                        this.mCtrl.setChangeFrozenStrategy(i15 == 1, 1, false);
                        return;
                    case 17:
                        boolean z2 = message.getData().getChar("ready") == 'T';
                        boolean z3 = this.enabled;
                        if (!z3 && z2) {
                            this.enabled = true;
                            postDelayed(new Runnable() { // from class: com.miui.powerkeeper.millet.MilletHandler.2
                                @Override // java.lang.Runnable
                                public void run() {
                                    MilletHandler.this.mCtrl.setChangeFrozenStrategy(true, 8, true);
                                }
                            }, 3000L);
                            if (this.mHandshake) {
                                return;
                            }
                            this.mHandshake = true;
                            registerUid();
                            this.mPolicy.registerEvent();
                            return;
                        }
                        if (!z3 || z2) {
                            return;
                        }
                        this.enabled = false;
                        this.mCtrl.setChangeFrozenStrategy(false, 8, false);
                        FreezeBinder.getInstance().thawAll("handshake false", false);
                        restoreRes(17);
                        postDelayed(new Runnable() { // from class: com.miui.powerkeeper.millet.MilletHandler.3
                            @Override // java.lang.Runnable
                            public void run() {
                                FreezeBinder.getInstance().registerCallback(new GreezeCallback(MilletHandler.this.mCtx, MilletHandler.this));
                            }
                        }, 60000L);
                        return;
                    case 18:
                        int i16 = message.getData().getInt("uid");
                        message.getData().getInt("pid");
                        int i17 = message.getData().getInt("module");
                        if (DEBUG) {
                            Log.d(TAG, "thaw other module=" + i17 + " uid=" + i16);
                        }
                        this.frozenList.remove(Integer.valueOf(i16));
                        HashSet hashSet9 = new HashSet();
                        hashSet9.add(Integer.valueOf(i16));
                        if (i17 == 1000) {
                            thawedAndDelay(hashSet9, PluginConstants.TTS_REMOVE_BEAN_TIME, "from aurogon");
                            return;
                        }
                        thawedAndDelay(hashSet9, PluginConstants.TTS_REMOVE_BEAN_TIME, "from module " + i17);
                        return;
                    case 19:
                        int[] intArray = message.getData().getIntArray("uids");
                        String string = message.getData().getString("pkg");
                        String string2 = message.getData().getString("reason");
                        int i18 = message.getData().getInt(YellowPageContract.Permission.STATE);
                        int i19 = message.getData().getInt(MIPushNotificationHelper.MSG_BUSI_TYPE_SCENE);
                        if (DEBUG) {
                            Log.d(TAG, "MSG_SYNC:" + string2 + " pkg:" + string + " state=" + i18 + " scene=" + i19);
                        }
                        HashSet hashSet10 = new HashSet();
                        hashSet10.clear();
                        for (int i20 : intArray) {
                            if ((i18 == 1 && this.mPolicy.isAllowSync(i20, string)) || i18 == 0) {
                                hashSet10.add(Integer.valueOf(i20));
                            }
                        }
                        if (hashSet10.size() > 0) {
                            if (i18 == 1) {
                                thawed(hashSet10, 0L, string2);
                                return;
                            } else {
                                if (i18 == 0) {
                                    frozen(hashSet10, 0L, string2);
                                    return;
                                }
                                return;
                            }
                        }
                        return;
                    case 20:
                        int i21 = message.getData().getInt("eventcode");
                        if (isAlreadyScreenOn || !KeyEvent.isMediaSessionKey(i21)) {
                            return;
                        }
                        if (message.what == 20) {
                            str = i21 + "";
                        }
                        Set<Integer> frozenUidsAll = FreezeBinder.getInstance().getFrozenUidsAll();
                        if (frozenUidsAll.size() > 0) {
                            thawedAndDelay(frozenUidsAll, 3500L, "eventReason =" + str);
                            return;
                        }
                        return;
                    default:
                        switch (i2) {
                            case 22:
                                String str3 = "off state = " + message.getData().getInt(YellowPageContract.Permission.STATE) + " pid= " + message.getData().getInt("pid") + " uid= " + message.getData().getInt("uid") + " action = " + message.getData().getString("action");
                                if (DEBUG) {
                                    Log.d(TAG, str3);
                                }
                                if (this.mCtrl.getChangeFrozenStrategy()) {
                                    message.getData().getString("action");
                                    int i22 = message.getData().getInt("uid");
                                    int[] iArr = {message.getData().getInt("pid")};
                                    if (FreezeBinder.getInstance().isUidFrozen(i22)) {
                                        thawedPidsAndDelay(iArr, i22, PluginConstants.TTS_REMOVE_BEAN_TIME, "eventReason =" + str3);
                                        return;
                                    }
                                    return;
                                }
                                return;
                            case 23:
                                HashSet hashSet11 = new HashSet();
                                String string3 = message.getData().getString("thirty_apps_start");
                                if (string3 == null) {
                                    this.mPolicy.dealBluetoothEvent(message.getData());
                                    return;
                                }
                                int uidByPackageName = PackageUtil.getUidByPackageName(this.mCtx, string3);
                                hashSet11.add(Integer.valueOf(uidByPackageName));
                                thawedAndDelay(hashSet11, PluginConstants.TTS_REMOVE_BEAN_TIME, "thirty nearby uid=" + uidByPackageName);
                                return;
                            case 24:
                                String string4 = message.getData().getString("target-package");
                                int i23 = message.getData().getInt("transition-event");
                                int uidByPackageName2 = PackageUtil.getUidByPackageName(this.mCtx, string4);
                                Log.d(TAG, "geo pkg = " + string4 + " uid=" + uidByPackageName2 + " event=" + i23);
                                if (FreezeBinder.getInstance().isUidFrozen(uidByPackageName2)) {
                                    HashSet hashSet12 = new HashSet();
                                    hashSet12.add(Integer.valueOf(uidByPackageName2));
                                    thawedAndDelay(hashSet12, PluginConstants.TTS_REMOVE_BEAN_TIME, "Geo event=" + i23);
                                    return;
                                }
                                return;
                            case 25:
                                ArraySet arraySet = new ArraySet();
                                Iterator<String> it2 = MilletPolicy.MESSAGE_APPS.iterator();
                                while (it2.hasNext()) {
                                    int uidByPackageName3 = PackageUtil.getUidByPackageName(this.mCtx, it2.next());
                                    if (UserHandle.isApp(uidByPackageName3)) {
                                        arraySet.add(Integer.valueOf(uidByPackageName3));
                                    }
                                }
                                thawedAndDelay(arraySet, 10000L, NightStandbyRecord.KEY_IDLE);
                                return;
                            default:
                                switch (i2) {
                                    case 29:
                                        int i24 = message.getData().getInt("uid");
                                        HashSet hashSet13 = new HashSet();
                                        hashSet13.clear();
                                        hashSet13.add(Integer.valueOf(i24));
                                        thawed(hashSet13, 0L, "activity start");
                                        return;
                                    case 30:
                                        removeMessages(30);
                                        if (DEBUG) {
                                            synchronized (this.failedFrozenList) {
                                                Log.d(TAG, " retry failedFrozenList:" + this.failedFrozenList.toString());
                                                break;
                                            }
                                        }
                                        HashSet hashSet14 = new HashSet();
                                        Iterator<Integer> it3 = this.failedFrozenList.iterator();
                                        while (it3.hasNext()) {
                                            try {
                                                Integer next = it3.next();
                                                int iIntValue2 = next.intValue();
                                                if (MilletUtils.getPidsForUid(iIntValue2, this.mCtx).length == 0 || !this.mPolicy.isCycleCheck(iIntValue2)) {
                                                    it3.remove();
                                                } else {
                                                    hashSet14.add(next);
                                                }
                                            } catch (Exception unused) {
                                            }
                                        }
                                        if (hashSet14.size() > 0) {
                                            frozen(hashSet14, 0L, "retry");
                                        }
                                        if (this.failedFrozenList.size() > 0) {
                                            sendMessageDelayed(obtainMessage(30), PluginConstants.TTS_REMOVE_BEAN_TIME);
                                            return;
                                        }
                                        return;
                                    case 31:
                                        this.mCtrl.checkBackuping(this.mCtx, true);
                                        return;
                                    case 32:
                                        int i25 = message.getData().getInt("uid");
                                        HashSet hashSet15 = new HashSet();
                                        hashSet15.clear();
                                        hashSet15.add(Integer.valueOf(i25));
                                        frozen(hashSet15, 0L, "backingE");
                                        return;
                                    case MSG_USER_PRESENT /* 33 */:
                                        Set<Integer> set = MilletUtils.mOnWindowAppList;
                                        if (set != null && set.size() != 0) {
                                            HashSet hashSet16 = new HashSet();
                                            hashSet16.clear();
                                            String str4 = "screen on thaw onWinApps ";
                                            for (Integer num2 : MilletUtils.mOnWindowAppList) {
                                                int iIntValue3 = num2.intValue();
                                                hashSet16.add(num2);
                                                str4 = str4 + iIntValue3 + " ";
                                            }
                                            Log.d(TAG, str4);
                                            if (hashSet16.size() > 0) {
                                                thawed(hashSet16, 0L, "onWin");
                                            }
                                        }
                                        HashSet hashSet17 = new HashSet();
                                        hashSet17.clear();
                                        for (Integer num3 : FreezeBinder.getInstance().getFrozenUidsPower()) {
                                            if (checkSystemAlert(num3.intValue())) {
                                                hashSet17.add(num3);
                                            }
                                        }
                                        if (hashSet17.size() > 0) {
                                            thawedAndDelay(hashSet17, PluginConstants.TTS_REMOVE_BEAN_TIME, "alert");
                                            return;
                                        }
                                        return;
                                    default:
                                        return;
                                }
                        }
                }
        }
    }

    public boolean inFrozenList(Integer num) {
        return this.frozenList.contains(num);
    }

    public boolean isInQueryState(int i2) {
        return this.mCurrQueryUid == i2;
    }

    private void acquireLock(long j2) {
    }
}
