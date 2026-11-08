package com.miui.powerkeeper.millet.bright;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.Log;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import android.view.KeyEvent;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.c;
import com.miui.powerkeeper.controller.FrozenAppController;
import com.miui.powerkeeper.millet.FreezeBinder;
import com.miui.powerkeeper.millet.MilletConfig;
import com.miui.powerkeeper.millet.MilletHandler;
import com.miui.powerkeeper.millet.MilletPolicy;
import com.miui.powerkeeper.millet.MilletUidObserver;
import com.miui.powerkeeper.millet.MilletUtils;
import com.miui.powerkeeper.utils.PackageUtil;
import com.xiaomi.push.extension.PluginConstants;
import com.xiaomi.push.service.MIPushNotificationHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import miui.process.ForegroundInfo;
import miui.yellowpage.YellowPageContract;

/* JADX INFO: loaded from: classes.dex */
public class MilletBrightHandler extends Handler {
    private static final int BINDER_DELAYED_TIME = 5000;
    private static final String BINDER_REASON = "receiver binder";
    public static final int EVENT_NOT_VIDEO = 100;
    public static final int MSG_ACTIVITY_START_EVENT = 29;
    public static final int MSG_ADD_FROZENLIST = 1;
    public static final int MSG_BACKUP_END = 31;
    public static final int MSG_BATTEY = 16;
    public static final int MSG_BINDER = 6;
    public static final int MSG_BINDER_ST = 5;
    public static final int MSG_BLUETOOTH = 23;
    public static final int MSG_CTS = 21;
    public static final int MSG_FOREGROUND_CHANGE = 30;
    public static final int MSG_FREEZE_AVAILABLE_STATE = 26;
    public static final int MSG_FREEZE_CONDITION = 25;
    public static final int MSG_FREEZE_ENABLE_STATE = 28;
    public static final int MSG_GEO_EVENT = 24;
    public static final int MSG_KEYCODE = 20;
    public static final int MSG_NEW_PROCESS = 15;
    public static final int MSG_ORDER_BROADCAST = 22;
    public static final int MSG_PKG = 8;
    public static final int MSG_PKG_DELAY_THAWED = 12;
    public static final int MSG_PKG_EXISTENT_CHANGED = 27;
    public static final int MSG_QUERY = 4;
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
    private static final String TIMEOUT_REASON = "bnativequery timeout";
    private boolean DEBUG;
    private String TAG;
    private SparseIntArray busyStats;
    private boolean enabled;
    private Set<Integer> failedFrozenList;
    private Set<Integer> frozenList;
    private boolean isAlreadyScreenOn;
    private boolean isAvailable;
    private Context mCtx;
    private int mCurrQueryUid;
    private FrozenAppController mFrzCtrl;
    private SparseLongArray mPendingUidTime;
    private MilletPolicy mPolicy;
    private SparseIntArray mTopUids;
    private Set<Integer> pkgReportList;
    private Set<Integer> queryList;
    private int queryTimeoutCnt;

    public MilletBrightHandler(Looper looper, Context context, FrozenAppController frozenAppController) {
        super(looper);
        this.TAG = "MilletBrightHandler";
        this.DEBUG = Log.isLoggable("MilletBrightHandler", 3) || Build.IS_DEBUGGABLE;
        this.isAlreadyScreenOn = true;
        this.queryTimeoutCnt = 0;
        this.queryList = new HashSet();
        this.frozenList = new HashSet();
        this.pkgReportList = new HashSet();
        this.failedFrozenList = new HashSet();
        this.mPolicy = null;
        this.mCtx = null;
        this.enabled = false;
        this.isAvailable = false;
        this.mFrzCtrl = null;
        this.mCurrQueryUid = -1;
        this.busyStats = new SparseIntArray();
        this.mTopUids = new SparseIntArray();
        this.mPendingUidTime = new SparseLongArray();
        this.mCtx = context;
        this.mFrzCtrl = frozenAppController;
        this.mPolicy = frozenAppController.getMilletPolicy();
    }

    private boolean checkCanFrozen(int i2, String str, Message message) {
        if (!this.enabled || !MilletUtils.isAppUid(i2)) {
            return false;
        }
        if ((str.equals("binder st") && message.getData().getInt("reason") == 1) || FreezeBinder.getInstance().isUidFrozen(i2)) {
            return true;
        }
        if (this.DEBUG) {
            Log.w(this.TAG, " ----- checkCanFrozen uid=" + i2 + " reason=" + str);
        }
        return false;
    }

    private void continueQuery(Set<Integer> set) {
        this.queryList.removeAll(set);
        int i2 = this.mCurrQueryUid;
        if (i2 == -1 || !set.contains(Integer.valueOf(i2))) {
            return;
        }
        removeMessages(14);
        if (this.queryList.size() > 0) {
            Message messageObtainMessage = obtainMessage(4, this.queryList.iterator().next().intValue(), 0);
            messageObtainMessage.obj = "retry query";
            sendMessage(messageObtainMessage);
        }
    }

    private void dealBinder(Message message) {
        int i2 = message.getData().getInt("caller_pid");
        int i3 = message.getData().getInt("uid");
        int i4 = message.getData().getInt("caller_uid");
        int i5 = message.getData().getInt("dst_pid");
        char c2 = message.getData().getChar("tf_oneway");
        int i6 = message.getData().getInt("buffer");
        if (checkCanFrozen(i3, "binder tp=" + i5, message)) {
            if (this.DEBUG) {
                Log.d(this.TAG, " binder event onew=" + c2 + " caller=" + i4 + " target=" + i3 + " callerPid=" + i2);
            }
            if (MilletConfig.isGlobal) {
                if (i6 != 0 && c2 != 'F') {
                    if (i4 == 1000 || i4 == 1047) {
                        return;
                    }
                    if (!MilletUidObserver.getIsSystemApp(i4) && !UserHandle.isCore(i4) && !PowerKeeperManager.getInstance().getProcessObserver().isUidForegroundService(i4) && !PowerKeeperManager.getInstance().getProcessObserver().isUidForeground(i4)) {
                        return;
                    }
                }
            } else if (i6 < 4 && i6 > 0 && c2 == 'T' && i4 != i3 && UserHandle.getUserId(i3) != 1000 && i4 != 1002 && i4 != 1027) {
                return;
            }
            thawedPidsAndDelay(new int[]{i5}, i3, PluginConstants.TTS_REMOVE_BEAN_TIME, "receiver binder c=" + c2 + " b=" + i6 + " call=" + i4);
        }
    }

    private void dealBinderState(Message message) {
        int i2 = message.getData().getInt("uid");
        this.queryTimeoutCnt = 0;
        this.mCurrQueryUid = -1;
        int i3 = message.getData().getInt("reason");
        if (checkCanFrozen(i2, "binder st", message)) {
            if ((i3 == 1 || i3 == 0) && i3 == 0) {
                this.busyStats.delete(i2);
            }
        }
    }

    private void dealGeoEvent(Message message) {
        String string = message.getData().getString("target-package");
        int i2 = message.getData().getInt("transition-event");
        int uidByPackageName = PackageUtil.getUidByPackageName(this.mCtx, string);
        Log.d(this.TAG, "geo pkg = " + string + " uid=" + uidByPackageName + " event=" + i2);
        if (FreezeBinder.getInstance().isUidFrozen(uidByPackageName)) {
            HashSet hashSet = new HashSet();
            hashSet.add(Integer.valueOf(uidByPackageName));
            thawedAndDelay(hashSet, PluginConstants.TTS_REMOVE_BEAN_TIME, "Geo event=" + i2);
        }
    }

    private void dealKeyCode(Message message) {
        int i2 = message.getData().getInt("eventcode");
        if (this.mFrzCtrl == null || !KeyEvent.isMediaSessionKey(i2)) {
            return;
        }
        String str = i2 + "";
        Set<Integer> frozenUidsAll = FreezeBinder.getInstance().getFrozenUidsAll();
        if (frozenUidsAll.size() > 0) {
            thawedAndDelay(frozenUidsAll, 3500L, "bright eventReason =" + str);
        }
    }

    private void dealOrderBroadcast(Message message) {
        String str = "state = " + message.getData().getInt(YellowPageContract.Permission.STATE) + " pid= " + message.getData().getInt("pid") + " uid= " + message.getData().getInt("uid") + " action = " + message.getData().getString("action");
        if (this.DEBUG) {
            Log.d(this.TAG, str);
        }
        String string = message.getData().getString("action");
        if ((string == null || !(string.contains("mipush.RECEIVE_MESSAGE") || string.contains("android.appwidget"))) && this.mFrzCtrl.getChangeFrozenStrategy()) {
            int i2 = message.getData().getInt("uid");
            int[] iArr = {message.getData().getInt("pid")};
            if (FreezeBinder.getInstance().isUidFrozen(i2)) {
                thawedPidsAndDelay(iArr, i2, (isMessageApp(i2) ? 2 : 1) * 5000, "eventReason =" + str);
            }
        }
    }

    private void dealQueryTimeout(Message message) {
        HashSet hashSet = new HashSet();
        hashSet.addAll(this.queryList);
        this.queryTimeoutCnt++;
        Log.d(this.TAG, "nativeQuery timeout!!!! uid=" + message.arg1 + " cnt=" + this.queryTimeoutCnt);
        if (this.isAlreadyScreenOn && this.queryTimeoutCnt <= 3) {
            hashSet.add(Integer.valueOf(message.arg1));
            thawedAndDelay(FreezeBinder.getInstance().getFrozenUidsPower(), ((long) this.queryTimeoutCnt) * PluginConstants.TTS_REMOVE_BEAN_TIME, TIMEOUT_REASON);
        } else if (this.queryTimeoutCnt > 3) {
            this.queryTimeoutCnt = 2;
        }
        this.queryList.clear();
    }

    private void dealSignal(Message message) {
        final int i2 = message.getData().getInt("uid");
        if (checkCanFrozen(i2, "signal", message)) {
            final HashSet hashSet = new HashSet();
            hashSet.add(Integer.valueOf(i2));
            final String str = "receive signal pid=" + message.getData().getInt("pid");
            thawed(hashSet, 0L, str);
            postDelayed(new Runnable() { // from class: com.miui.powerkeeper.millet.bright.MilletBrightHandler.2
                @Override // java.lang.Runnable
                public void run() {
                    if (MilletUtils.getPidsForUid(i2, MilletBrightHandler.this.mCtx).length > 0) {
                        MilletBrightHandler.this.frozen(hashSet, 10L, str);
                    } else {
                        MilletBrightHandler.this.failedFrozenList.remove(Integer.valueOf(i2));
                    }
                }
            }, 3000L);
        }
    }

    private void freezeLater(int[] iArr, List<Integer> list, String str, Set<Integer> set) {
        ArrayList arrayList = new ArrayList();
        if (list.size() != iArr.length) {
            for (int i2 : iArr) {
                if (!list.contains(Integer.valueOf(i2)) && MilletUtils.getPidsForUid(i2, this.mCtx).length > 0) {
                    this.failedFrozenList.add(Integer.valueOf(i2));
                    arrayList.add(Integer.valueOf(i2));
                }
            }
        }
        if (this.failedFrozenList.size() > 0) {
            synchronized (this.failedFrozenList) {
                try {
                    if (this.DEBUG) {
                        Log.d(this.TAG, "frozen fail result:" + this.failedFrozenList.toString() + " freeze fail:" + arrayList.toString());
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        if (!hasMessages(30) && this.failedFrozenList.size() > 0) {
            sendMessageDelayed(obtainMessage(30), PluginConstants.TTS_REMOVE_BEAN_TIME);
        }
        this.frozenList.addAll(list);
        FrozenAppController frozenAppController = this.mFrzCtrl;
        if (frozenAppController != null && frozenAppController.getBrightCtrl() != null && BrightController.REASON_BRIGHT_MILLET_END.equals(str)) {
            this.mFrzCtrl.getBrightCtrl().notifyFreezeEvent(true);
        }
        for (Integer num : list) {
            this.mPendingUidTime.delete(num.intValue());
            FrozenAppController frozenAppController2 = this.mFrzCtrl;
            if (frozenAppController2 != null && frozenAppController2.getBrightCtrl() != null && this.mFrzCtrl.getBrightCtrl().getScenario() != null) {
                this.mFrzCtrl.getBrightCtrl().getScenario().controlScenaroApp(num.intValue());
            }
            if (set.contains(num)) {
                Log.d(this.TAG, "repeat query uid:" + num);
            } else {
                Message messageObtain = Message.obtain();
                messageObtain.what = 13;
                messageObtain.arg1 = num.intValue();
                sendMessageDelayed(messageObtain, 200L);
                Log.d(this.TAG, "frozen query_add uid =" + num + " reason=" + str);
            }
            if (!this.pkgReportList.contains(num)) {
                this.pkgReportList.add(num);
                this.mPolicy.dealPacket(num.intValue(), true, this);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void frozen(Set<Integer> set, long j2, String str) {
        try {
            if (!this.enabled) {
                if (this.DEBUG) {
                    Log.d(this.TAG, "frozen return enabled=" + this.enabled + " avai=" + this.isAvailable + " uids=" + set.toString());
                }
                return;
            }
            if (getFrozenCtrl().getBrightCtrl() != null && (MilletUtils.checkHuanji(this.mCtx) || getFrozenCtrl().getBrightCtrl().getBackupState())) {
                Log.d(this.TAG, "frozen backuping");
                return;
            }
            if (!this.mFrzCtrl.getChangeFrozenStrategy()) {
                if (this.mFrzCtrl.getFrozenCondition() != 41) {
                    if (this.DEBUG) {
                        Log.d(this.TAG, "uids =" + set.toString() + " str:" + this.mFrzCtrl.getFrozenCondition());
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
            Iterator<Integer> it2 = set.iterator();
            if (set.size() == 0) {
                Log.d(this.TAG, "frozen uids=0");
                return;
            }
            while (it2.hasNext()) {
                Integer next2 = it2.next();
                int iIntValue = next2.intValue();
                MilletPolicy milletPolicy = this.mPolicy;
                if (milletPolicy == null || !milletPolicy.isBrightWhite(iIntValue)) {
                    boolean z2 = false;
                    if (MilletUtils.isTopApp(iIntValue) || ((getFrozenCtrl().getBrightCtrl() != null && iIntValue == getFrozenCtrl().getBrightCtrl().getCurUid()) || (getFrozenCtrl().getBrightCtrl() != null && getFrozenCtrl().getBrightCtrl().getScenario() != null && !getFrozenCtrl().getBrightCtrl().getScenario().canFreezePkg(null, iIntValue)))) {
                        it2.remove();
                        String pkgNameByUid = MilletUidObserver.getPkgNameByUid(iIntValue);
                        if (pkgNameByUid != null && ("com.miui.weather2".equals(pkgNameByUid) || "com.mfashiongallery.emag".equals(pkgNameByUid))) {
                            z2 = true;
                        }
                        if (!MilletUtils.isTopApp(iIntValue) && MilletUtils.isAppUid(iIntValue) && !z2 && !MilletUidObserver.getIsSystemApp(iIntValue)) {
                            MilletPolicy milletPolicy2 = this.mPolicy;
                            if (milletPolicy2 == null || !milletPolicy2.isInWhiteList(iIntValue)) {
                                this.failedFrozenList.add(next2);
                            }
                        }
                        z2 = true;
                    }
                    if (this.mPendingUidTime.get(iIntValue) > 1000) {
                        if (SystemClock.uptimeMillis() > this.mPendingUidTime.get(iIntValue)) {
                            removeMessages(iIntValue);
                            this.mPendingUidTime.delete(iIntValue);
                        } else if (!z2) {
                            it2.remove();
                            if (this.DEBUG) {
                                Log.d(this.TAG, "frozen remove uid =" + iIntValue + " exist pending now=" + SystemClock.uptimeMillis() + " time =" + this.mPendingUidTime.get(iIntValue));
                            }
                        }
                    }
                } else {
                    it2.remove();
                }
            }
            int[] array = set.stream().mapToInt(new c()).toArray();
            ArrayList arrayList = new ArrayList();
            if (!this.enabled) {
                Log.d(this.TAG, "enabled return");
                return;
            }
            Set<Integer> frozenUidsAll = FreezeBinder.getInstance().getFrozenUidsAll();
            if (array.length <= 0) {
                if (!hasMessages(30) && this.failedFrozenList.size() > 0 && this.isAlreadyScreenOn) {
                    sendMessageDelayed(obtainMessage(30), PluginConstants.TTS_REMOVE_BEAN_TIME);
                }
                if (!this.isAlreadyScreenOn) {
                    sendFailList();
                }
                return;
            }
            arrayList.addAll(FreezeBinder.getInstance().freezeUids(array, 0L, str, false));
            this.failedFrozenList.removeAll(arrayList);
            if (!this.isAlreadyScreenOn) {
                sendFailList();
            }
            if (arrayList.size() > 0 && this.DEBUG) {
                Log.d(this.TAG, "frozen uids:" + arrayList.toString() + " reason = " + str);
            }
            freezeLater(array, arrayList, str, frozenUidsAll);
        } catch (Throwable th) {
            throw th;
        }
    }

    private boolean isMessageApp(int i2) {
        String pkgNameByUid = MilletUidObserver.getPkgNameByUid(i2);
        return pkgNameByUid != null && MilletPolicy.MESSAGE_APPS.contains(pkgNameByUid);
    }

    private void restoreRes() {
        removeCallbacksAndMessages(null);
        this.mFrzCtrl.getBrightCtrl().restoreState();
        this.frozenList.clear();
        this.mFrzCtrl.setChangeFrozenStrategy(true, 4, false);
        this.pkgReportList.clear();
        this.queryList.clear();
        this.mPendingUidTime.clear();
        Log.d(this.TAG, "restoreRes mPending delete all");
    }

    private void restoreThawPolicy(int[] iArr) {
        if (this.mFrzCtrl.getBrightCtrl().getScenario() == null) {
            return;
        }
        for (int i2 : iArr) {
            this.mPolicy.getThawTime().put(i2, SystemClock.uptimeMillis());
            if (i2 > 0) {
                this.mFrzCtrl.getBrightCtrl().getScenario().restoreCtrl(i2);
            } else if (i2 == -1) {
                this.mFrzCtrl.getBrightCtrl().getScenario().restoreCtrl();
            }
        }
    }

    private void sendFailList() {
        MilletHandler milletHandler = (MilletHandler) this.mFrzCtrl.getScreenOffHandler();
        milletHandler.addFailedList(this.failedFrozenList);
        milletHandler.sendMessage(milletHandler.obtainMessage(30));
    }

    private synchronized void thawed(Set<Integer> set, long j2, String str) {
        try {
            if (set.size() == 0) {
                return;
            }
            int[] array = set.stream().mapToInt(new c()).toArray();
            continueQuery(set);
            new ArrayList();
            if (str != null && !str.contains("module")) {
                List<Integer> listThawUids = FreezeBinder.getInstance().thawUids(array, str);
                if (listThawUids.size() != array.length && !str.contains("signal")) {
                    set.retainAll(listThawUids);
                }
            }
            this.frozenList.removeAll(set);
            restoreThawPolicy(array);
            if (this.DEBUG) {
                Log.d(this.TAG, "thawed end " + str + " " + j2 + " uids:" + Arrays.toString(array));
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    private void thawedAndDelay(Set<Integer> set, final long j2, final String str) {
        Set<Integer> hashSet = new HashSet<>();
        hashSet.addAll(set);
        if (set.size() == 0) {
            return;
        }
        Iterator<Integer> it = hashSet.iterator();
        while (it.hasNext()) {
            int iIntValue = it.next().intValue();
            if (SystemClock.uptimeMillis() + j2 > this.mPendingUidTime.get(iIntValue)) {
                removeMessages(iIntValue);
                this.mPendingUidTime.put(iIntValue, SystemClock.uptimeMillis() + j2);
                if (this.DEBUG) {
                    Log.d(this.TAG, "thawedAndDelay uid=" + iIntValue + " d=" + j2);
                }
            }
        }
        this.failedFrozenList.removeAll(hashSet);
        for (final Integer num : hashSet) {
            if (!MilletUtils.isTopApp(num.intValue()) && !PowerKeeperManager.getInstance().isUidForeground(num.intValue()) && this.mFrzCtrl.getBrightCtrl().getCurUid() != num.intValue()) {
                MilletUtils.setPkgData(num.intValue(), this, (int) j2);
            }
            MilletUtils.setPkgData(num.intValue(), this, (int) j2);
            postDelayed(new Runnable() { // from class: com.miui.powerkeeper.millet.bright.MilletBrightHandler.1
                @Override // java.lang.Runnable
                public void run() {
                    MilletBrightHandler.this.triggerDelay(num.intValue(), j2, str);
                }
            }, num, j2);
        }
        thawed(hashSet, 0L, str);
    }

    private void thawedPidsAndDelay(int[] iArr, int i2, long j2, String str) {
        HashSet hashSet = new HashSet();
        hashSet.add(Integer.valueOf(i2));
        thawedAndDelay(hashSet, j2, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void triggerDelay(int i2, long j2, String str) {
        try {
            if (this.isAlreadyScreenOn || !TIMEOUT_REASON.equals(str)) {
                HashSet hashSet = new HashSet();
                if (SystemClock.uptimeMillis() >= this.mPendingUidTime.get(i2)) {
                    hashSet.add(Integer.valueOf(i2));
                    this.mPendingUidTime.delete(i2);
                    frozen(hashSet, j2, str);
                }
                this.mPolicy.dealPacket(i2, true, this);
            }
        } catch (Exception e2) {
            Log.e(this.TAG, "thawedAndDelay err uid= " + i2, e2);
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
        return this.mFrzCtrl;
    }

    public MilletPolicy getMilletPolicy() {
        return this.mPolicy;
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i2 = message.what;
        if (i2 == -27) {
            int i3 = message.arg1;
            if (!MilletUtils.isAppUid(i3)) {
                Log.d(this.TAG, "disguise err uid = " + i3);
                return;
            }
            HashSet hashSet = new HashSet();
            hashSet.add(Integer.valueOf(i3));
            frozen(hashSet, 0L, "b disguise");
            return;
        }
        if (i2 == -22) {
            this.mFrzCtrl.getBrightCtrl().setPowerMode(message.arg1);
            return;
        }
        if (i2 == -9) {
            this.mFrzCtrl.getBrightCtrl().notifyForegroundChange(((ForegroundInfo) message.obj).mForegroundUid, ((ForegroundInfo) message.obj).mForegroundPackageName);
            return;
        }
        if (i2 == 13) {
            boolean zContains = this.frozenList.contains(Integer.valueOf(message.arg1));
            if (!zContains) {
                zContains = FreezeBinder.getInstance().getFrozenUidsPower().contains(Integer.valueOf(message.arg1));
            }
            if (zContains) {
                int i4 = message.arg1;
                this.mCurrQueryUid = i4;
                if (PowerKeeperManager.JNI_LOADED) {
                    FrozenAppController.nativeQuery(i4);
                    return;
                }
                return;
            }
            return;
        }
        if (i2 == 26) {
            synchronized (this) {
                this.isAvailable = message.arg1 == 1;
            }
            return;
        }
        if (i2 == 1) {
            if (MilletUtils.isAppUid(message.arg1)) {
                Object obj = message.obj;
                if (obj instanceof String) {
                    String str = (String) obj;
                    HashSet hashSet2 = new HashSet();
                    hashSet2.add(Integer.valueOf(message.arg1));
                    int i5 = message.arg2;
                    if (i5 == 1) {
                        frozen(hashSet2, 0L, str);
                        return;
                    } else {
                        if (i5 == 0) {
                            thawed(hashSet2, 0L, str);
                            return;
                        }
                        return;
                    }
                }
                return;
            }
            return;
        }
        if (i2 == 2) {
            this.isAlreadyScreenOn = true;
            return;
        }
        if (i2 == 3) {
            if (this.isAlreadyScreenOn) {
                if (this.failedFrozenList.size() > 0) {
                    synchronized (this.failedFrozenList) {
                        Log.d(this.TAG, " ti=" + this.failedFrozenList.toString());
                    }
                }
                if (this.failedFrozenList.size() > 0) {
                    removeMessages(30);
                    sendFailList();
                }
            }
            this.isAlreadyScreenOn = false;
            return;
        }
        if (i2 == 5) {
            dealBinderState(message);
            return;
        }
        if (i2 == 6) {
            dealBinder(message);
            return;
        }
        if (i2 == 7) {
            this.mPolicy.removeBluetoothUid(message.getData().getInt("uid"));
            return;
        }
        if (i2 == 8) {
            int i6 = message.getData().getInt("uid");
            if (this.mPolicy == null || !this.pkgReportList.contains(Integer.valueOf(i6))) {
                return;
            }
            HashSet hashSet3 = new HashSet();
            hashSet3.add(Integer.valueOf(i6));
            thawedAndDelay(hashSet3, 10000L, "receiver pkg");
            return;
        }
        if (i2 == 10) {
            Log.d(this.TAG, "reset_data");
            int[] frozenUidsPowerArray = FreezeBinder.getInstance().getFrozenUidsPowerArray();
            if (frozenUidsPowerArray.length > 0) {
                restoreThawPolicy(frozenUidsPowerArray);
            }
            FreezeBinder.getInstance().thawAll("reset data", true);
            restoreRes();
            return;
        }
        if (i2 == 11) {
            this.pkgReportList.add(Integer.valueOf(message.arg1));
            if (this.DEBUG) {
                Log.d(this.TAG, "set_pkg_data uid=" + message.arg1 + " " + message.arg2);
            }
            FrozenAppController.callNativeSet(message.arg1, message.arg2);
            return;
        }
        switch (i2) {
            case 18:
                int i7 = message.getData().getInt("uid");
                message.getData().getInt("pid");
                int i8 = message.getData().getInt("module");
                this.frozenList.remove(Integer.valueOf(i7));
                HashSet hashSet4 = new HashSet();
                hashSet4.add(Integer.valueOf(i7));
                if (i7 == getFrozenCtrl().getBrightCtrl().getCurUid()) {
                    thawed(hashSet4, 0L, "from system");
                    return;
                }
                if (i8 == 1000) {
                    thawedAndDelay(hashSet4, (isMessageApp(i7) ? 2 : 1) * 5000, "from system");
                    return;
                }
                thawedAndDelay(hashSet4, PluginConstants.TTS_REMOVE_BEAN_TIME, "from module " + i8);
                return;
            case 19:
                int[] intArray = message.getData().getIntArray("uids");
                String string = message.getData().getString("pkg");
                String string2 = message.getData().getString("reason");
                int i9 = message.getData().getInt(YellowPageContract.Permission.STATE);
                int i10 = message.getData().getInt(MIPushNotificationHelper.MSG_BUSI_TYPE_SCENE);
                if (this.DEBUG) {
                    Log.d(this.TAG, "MSG_SYNC:" + string2 + " pkg:" + string + " state=" + i9 + " scene=" + i10);
                }
                HashSet hashSet5 = new HashSet();
                for (int i11 : intArray) {
                    if ((i9 == 1 && this.mPolicy.isAllowSync(i11, string)) || i9 == 0) {
                        hashSet5.add(Integer.valueOf(i11));
                    }
                }
                if (hashSet5.size() > 0) {
                    if (i9 == 1) {
                        thawed(hashSet5, 0L, string2);
                        return;
                    } else {
                        if (i9 == 0) {
                            frozen(hashSet5, 0L, string2);
                            return;
                        }
                        return;
                    }
                }
                return;
            case 20:
                dealKeyCode(message);
                return;
            default:
                switch (i2) {
                    case 22:
                        dealOrderBroadcast(message);
                        return;
                    case 23:
                        String string3 = message.getData().getString("thirty_apps_start");
                        if (string3 == null) {
                            this.mPolicy.dealBluetoothEvent(message.getData());
                            return;
                        }
                        int uidByPackageName = PackageUtil.getUidByPackageName(this.mCtx, string3);
                        HashSet hashSet6 = new HashSet();
                        hashSet6.add(Integer.valueOf(uidByPackageName));
                        thawedAndDelay(hashSet6, PluginConstants.TTS_REMOVE_BEAN_TIME, "thirty nearby uid=" + uidByPackageName);
                        return;
                    case 24:
                        dealGeoEvent(message);
                        return;
                    default:
                        switch (i2) {
                            case 28:
                                boolean z2 = message.arg1 == 1;
                                this.enabled = z2;
                                if (z2) {
                                    return;
                                }
                                int[] frozenUidsPowerArray2 = FreezeBinder.getInstance().getFrozenUidsPowerArray();
                                if (frozenUidsPowerArray2.length > 0) {
                                    restoreThawPolicy(frozenUidsPowerArray2);
                                    return;
                                }
                                return;
                            case 29:
                                int i12 = message.getData().getInt("uid");
                                HashSet hashSet7 = new HashSet();
                                hashSet7.clear();
                                hashSet7.add(Integer.valueOf(i12));
                                thawed(hashSet7, 0L, "activity start");
                                return;
                            case 30:
                                removeMessages(30);
                                if (this.DEBUG) {
                                    synchronized (this.failedFrozenList) {
                                        Log.d(this.TAG, " retry failedFrozenList:" + this.failedFrozenList.toString());
                                        break;
                                    }
                                }
                                HashSet hashSet8 = new HashSet();
                                synchronized (this) {
                                    try {
                                        Iterator<Integer> it = this.failedFrozenList.iterator();
                                        while (it.hasNext()) {
                                            Integer next = it.next();
                                            int iIntValue = next.intValue();
                                            if (MilletUtils.getPidsForUid(iIntValue, this.mCtx).length == 0 || !this.mPolicy.isCycleCheck(iIntValue) || (getFrozenCtrl().getBrightCtrl() != null && iIntValue == getFrozenCtrl().getBrightCtrl().getCurUid())) {
                                                it.remove();
                                            } else {
                                                hashSet8.add(next);
                                            }
                                        }
                                    } catch (Throwable th) {
                                        throw th;
                                    }
                                    break;
                                }
                                if (hashSet8.size() == 0) {
                                    return;
                                }
                                frozen(hashSet8, 0L, "retry");
                                if (!this.isAlreadyScreenOn || this.failedFrozenList.size() <= 0) {
                                    return;
                                }
                                sendMessageDelayed(obtainMessage(30), PluginConstants.TTS_REMOVE_BEAN_TIME);
                                return;
                            case 31:
                                int i13 = message.getData().getInt("uid");
                                HashSet hashSet9 = new HashSet();
                                hashSet9.add(Integer.valueOf(i13));
                                frozen(hashSet9, 0L, "backingE");
                                return;
                            default:
                                return;
                        }
                }
        }
    }

    public boolean isInQueryState(int i2) {
        return this.mCurrQueryUid == i2;
    }

    public boolean isScreenOn() {
        return this.isAlreadyScreenOn;
    }
}
