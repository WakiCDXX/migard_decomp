package com.miui.powerkeeper;

import android.app.ActivityManagerNative;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseBooleanArray;
import com.google.android.collect.Lists;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.statemachine.SleepModeControllerNew;
import com.xiaomi.channel.commonutils.android.DeviceInfo;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class UserCheckManager {

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    private static final String f289l = "UserCheckManager";

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    private static final boolean f290m = Build.IS_DEBUGGABLE;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    private static UserCheckManager f291n;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private final Context f292a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private final Handler f293b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private final Object f294c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private final f f295d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private SparseBooleanArray f296e = new SparseBooleanArray();

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private SparseBooleanArray f297f = new SparseBooleanArray();

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private int f298g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private int f299h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private int f300i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private List<PowerKeeperInterface.g0> f301j;

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private final BroadcastReceiver f302k;

    class a implements Runnable {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final /* synthetic */ PowerKeeperInterface.g0 f303a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        final /* synthetic */ int f304b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        final /* synthetic */ boolean f305c;

        a(PowerKeeperInterface.g0 g0Var, int i2, boolean z2) {
            this.f303a = g0Var;
            this.f304b = i2;
            this.f305c = z2;
        }

        @Override // java.lang.Runnable
        public void run() {
            PowerKeeperInterface.b0 b0Var = this.f303a.f267b;
            if (b0Var != null) {
                b0Var.onUserEnableStatusChange(this.f304b, this.f305c);
            }
        }
    }

    class b implements Runnable {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final /* synthetic */ PowerKeeperInterface.g0 f307a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        final /* synthetic */ int f308b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        final /* synthetic */ boolean f309c;

        b(PowerKeeperInterface.g0 g0Var, int i2, boolean z2) {
            this.f307a = g0Var;
            this.f308b = i2;
            this.f309c = z2;
        }

        @Override // java.lang.Runnable
        public void run() {
            PowerKeeperInterface.b0 b0Var = this.f307a.f267b;
            if (b0Var != null) {
                b0Var.onUserRunningStatusChange(this.f308b, this.f309c);
            }
        }
    }

    class c implements Runnable {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final /* synthetic */ PowerKeeperInterface.g0 f311a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        final /* synthetic */ int f312b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        final /* synthetic */ boolean f313c;

        c(PowerKeeperInterface.g0 g0Var, int i2, boolean z2) {
            this.f311a = g0Var;
            this.f312b = i2;
            this.f313c = z2;
        }

        @Override // java.lang.Runnable
        public void run() {
            PowerKeeperInterface.b0 b0Var = this.f311a.f267b;
            if (b0Var != null) {
                b0Var.onUserForegroundStatusChange(this.f312b, this.f313c);
            }
        }
    }

    class d implements Runnable {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final /* synthetic */ PowerKeeperInterface.g0 f315a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        final /* synthetic */ int f316b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        final /* synthetic */ boolean f317c;

        d(PowerKeeperInterface.g0 g0Var, int i2, boolean z2) {
            this.f315a = g0Var;
            this.f316b = i2;
            this.f317c = z2;
        }

        @Override // java.lang.Runnable
        public void run() {
            PowerKeeperInterface.b0 b0Var = this.f315a.f267b;
            if (b0Var != null) {
                b0Var.onXSpaceStatusChange(this.f316b, this.f317c);
            }
        }
    }

    class e extends BroadcastReceiver {
        e() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }
            synchronized (UserCheckManager.this.f294c) {
                try {
                    if (action.equals("android.intent.action.USER_SWITCHED")) {
                        int intExtra = intent.getIntExtra("android.intent.extra.user_handle", -1);
                        if (intExtra != -1 && intExtra != 999) {
                            if (UserCheckManager.f290m) {
                                Log.d(UserCheckManager.f289l, "action is " + action + ", user handle is " + intExtra);
                            }
                            if (UserCheckManager.this.f297f.indexOfKey(intExtra) < 0) {
                                UserCheckManager.this.f297f.put(intExtra, true);
                                UserCheckManager.this.H(intExtra, true);
                            }
                            if (UserCheckManager.this.f298g != intExtra) {
                                UserCheckManager.this.G(intExtra, true);
                                UserCheckManager.this.f298g = intExtra;
                            }
                        }
                        return;
                    }
                    if (action.equals("android.intent.action.USER_STARTING")) {
                        int intExtra2 = intent.getIntExtra("android.intent.extra.user_handle", -1);
                        if (intExtra2 != -1 && intExtra2 != 999) {
                            if (UserCheckManager.f290m) {
                                Log.d(UserCheckManager.f289l, "action is " + action + ", user handle is " + intExtra2);
                            }
                            if (UserCheckManager.this.f297f.indexOfKey(intExtra2) < 0) {
                                UserCheckManager.this.f297f.put(intExtra2, true);
                                UserCheckManager.this.H(intExtra2, true);
                            }
                        }
                        return;
                    }
                    if (action.equals("android.intent.action.USER_STOPPED")) {
                        int intExtra3 = intent.getIntExtra("android.intent.extra.user_handle", -1);
                        if (intExtra3 != -1 && intExtra3 != 999) {
                            if (UserCheckManager.f290m) {
                                Log.d(UserCheckManager.f289l, "action is " + action + ", user handle is " + intExtra3);
                            }
                            if (UserCheckManager.this.f297f.indexOfKey(intExtra3) >= 0) {
                                UserCheckManager.this.f297f.delete(intExtra3);
                                UserCheckManager.this.H(intExtra3, false);
                            }
                        }
                        return;
                    }
                    if (action.equals("android.intent.action.USER_ADDED")) {
                        int intExtra4 = intent.getIntExtra("android.intent.extra.user_handle", -1);
                        if (intExtra4 != -1 && intExtra4 != 999) {
                            if (UserCheckManager.f290m) {
                                Log.d(UserCheckManager.f289l, "action is " + action + ", user handle is " + intExtra4);
                            }
                        }
                        return;
                    }
                    if (action.equals("android.intent.action.USER_REMOVED")) {
                        int intExtra5 = intent.getIntExtra("android.intent.extra.user_handle", -1);
                        if (intExtra5 != -1 && intExtra5 != 999) {
                            if (UserCheckManager.f290m) {
                                Log.d(UserCheckManager.f289l, "action is " + action + ", user handle is " + intExtra5);
                            }
                            if (UserCheckManager.this.f297f.indexOfKey(intExtra5) >= 0) {
                                UserCheckManager.this.f297f.delete(intExtra5);
                                UserCheckManager.this.H(intExtra5, false);
                            }
                            if (UserCheckManager.this.f296e.indexOfKey(intExtra5) >= 0) {
                                UserCheckManager.this.f296e.delete(intExtra5);
                                UserCheckManager.this.F(intExtra5, false);
                            }
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    private class f extends ContentObserver {
        public f(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z2, Uri uri) {
            if (uri == null) {
                return;
            }
            if (UserCheckManager.f290m) {
                Log.d(UserCheckManager.f289l, uri.toSafeString());
            }
            synchronized (UserCheckManager.this.f294c) {
                try {
                    if (uri.equals(Settings.Secure.getUriFor("second_user_id"))) {
                        boolean zB = UserCheckManager.this.B();
                        int iY = zB ? UserCheckManager.this.y() : -10000;
                        if (zB && UserCheckManager.this.f297f.indexOfKey(iY) >= 0) {
                            UserCheckManager.this.f297f.delete(iY);
                            UserCheckManager.this.H(iY, false);
                        }
                        if (zB && UserCheckManager.this.f299h != iY) {
                            if (UserCheckManager.this.f299h != -10000) {
                                if (UserCheckManager.this.f297f.indexOfKey(UserCheckManager.this.f299h) >= 0) {
                                    UserCheckManager.this.f297f.delete(UserCheckManager.this.f299h);
                                    UserCheckManager userCheckManager = UserCheckManager.this;
                                    userCheckManager.H(userCheckManager.f299h, false);
                                }
                                UserCheckManager.this.f296e.delete(UserCheckManager.this.f299h);
                                UserCheckManager userCheckManager2 = UserCheckManager.this;
                                userCheckManager2.F(userCheckManager2.f299h, false);
                            }
                            UserCheckManager.this.f299h = iY;
                            UserCheckManager.this.f296e.put(UserCheckManager.this.f299h, true);
                            UserCheckManager userCheckManager3 = UserCheckManager.this;
                            userCheckManager3.F(userCheckManager3.f299h, true);
                        } else if (!zB && UserCheckManager.this.f299h != iY) {
                            if (UserCheckManager.this.f297f.indexOfKey(UserCheckManager.this.f299h) >= 0) {
                                UserCheckManager.this.f297f.delete(UserCheckManager.this.f299h);
                                UserCheckManager userCheckManager4 = UserCheckManager.this;
                                userCheckManager4.H(userCheckManager4.f299h, false);
                            }
                            UserCheckManager.this.f296e.delete(UserCheckManager.this.f299h);
                            UserCheckManager userCheckManager5 = UserCheckManager.this;
                            userCheckManager5.F(userCheckManager5.f299h, false);
                            UserCheckManager.this.f299h = iY;
                        }
                    } else if (uri.equals(Settings.Secure.getUriFor("xspace_enabled"))) {
                        boolean zD = UserCheckManager.this.D();
                        int iZ = zD ? UserCheckManager.this.z() : -10000;
                        if (zD && UserCheckManager.this.f300i != iZ) {
                            if (UserCheckManager.this.f300i != -10000) {
                                UserCheckManager userCheckManager6 = UserCheckManager.this;
                                userCheckManager6.I(userCheckManager6.f300i, false);
                            }
                            UserCheckManager.this.f300i = iZ;
                            UserCheckManager userCheckManager7 = UserCheckManager.this;
                            userCheckManager7.I(userCheckManager7.f300i, true);
                        } else if (!zD && UserCheckManager.this.f300i != iZ) {
                            UserCheckManager userCheckManager8 = UserCheckManager.this;
                            userCheckManager8.I(userCheckManager8.f300i, false);
                            UserCheckManager.this.f300i = iZ;
                        }
                    } else if (uri.equals(Settings.Secure.getUriFor("key_open_earthquake_warning"))) {
                        int iS = UserCheckManager.this.s();
                        SleepModeControllerNew sleepModeControllerNew = SleepModeControllerNew.getInstance();
                        if (sleepModeControllerNew == null) {
                            return;
                        }
                        if (iS == 1) {
                            if (!SleepModeControllerNew.sUserCloseSleepModeByUIButton && !sleepModeControllerNew.getSleepModeForEarthquakeWarnFlag()) {
                                SleepModeControllerNew.setSleepModeSwitch(false, 3);
                            }
                        } else if (iS == 0 && !SleepModeControllerNew.sUserCloseSleepModeByUIButton && !sleepModeControllerNew.getSleepModeForEarthquakeWarnFlag()) {
                            SleepModeControllerNew.setSleepModeSwitch(true, 3);
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    private UserCheckManager(Context context) {
        e eVar = new e();
        this.f302k = eVar;
        this.f292a = context;
        Object obj = new Object();
        this.f294c = obj;
        this.f301j = Lists.newArrayList();
        Handler handler = new Handler(PowerKeeperManager.getThread().getLooper());
        this.f293b = handler;
        synchronized (obj) {
            f fVar = new f(handler);
            this.f295d = fVar;
            ContentResolver contentResolverForUser = context.getContentResolverForUser(UserHandle.OWNER);
            contentResolverForUser.registerContentObserver(Settings.Secure.getUriFor("second_user_id"), false, fVar, -1);
            contentResolverForUser.registerContentObserver(Settings.Secure.getUriFor("xspace_enabled"), false, fVar, -1);
            contentResolverForUser.registerContentObserver(Settings.Secure.getUriFor("key_open_earthquake_warning"), false, fVar, -1);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.USER_SWITCHED");
            intentFilter.addAction("android.intent.action.USER_STARTING");
            intentFilter.addAction("android.intent.action.USER_STOPPED");
            intentFilter.addAction("android.intent.action.USER_ADDED");
            intentFilter.addAction("android.intent.action.USER_REMOVED");
            context.registerReceiverAsUser(eVar, UserHandle.ALL, intentFilter, null, handler, 2);
            L();
        }
    }

    private boolean A() {
        return C(w());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean B() {
        return -10000 != Settings.Secure.getIntForUser(this.f292a.getContentResolverForUser(UserHandle.OWNER), "second_user_id", -10000, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void F(int i2, boolean z2) {
        synchronized (this.f301j) {
            try {
                for (PowerKeeperInterface.g0 g0Var : this.f301j) {
                    Handler handler = g0Var.f266a;
                    if (handler == null) {
                        handler = this.f293b;
                    }
                    handler.post(new a(g0Var, i2, z2));
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        EventsAggregator.getInstance(this.f292a).notifyUserStatusListeners(i2, z2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void G(int i2, boolean z2) {
        synchronized (this.f301j) {
            try {
                for (PowerKeeperInterface.g0 g0Var : this.f301j) {
                    Handler handler = g0Var.f266a;
                    if (handler == null) {
                        handler = this.f293b;
                    }
                    handler.post(new c(g0Var, i2, z2));
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void H(int i2, boolean z2) {
        synchronized (this.f301j) {
            try {
                for (PowerKeeperInterface.g0 g0Var : this.f301j) {
                    Handler handler = g0Var.f266a;
                    if (handler == null) {
                        handler = this.f293b;
                    }
                    handler.post(new b(g0Var, i2, z2));
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        EventsAggregator.getInstance(this.f292a).notifyUserStatusListeners(i2, z2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void I(int i2, boolean z2) {
        synchronized (this.f301j) {
            try {
                for (PowerKeeperInterface.g0 g0Var : this.f301j) {
                    Handler handler = g0Var.f266a;
                    if (handler == null) {
                        handler = this.f293b;
                    }
                    handler.post(new d(g0Var, i2, z2));
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        EventsAggregator.getInstance(this.f292a).notifyUserStatusListeners(i2, z2);
    }

    private void L() {
        for (int i2 : t()) {
            this.f296e.put(i2, true);
        }
        for (int i3 : x()) {
            this.f297f.put(i3, true);
        }
        this.f298g = u();
        if (B()) {
            this.f299h = y();
        } else {
            this.f299h = -10000;
        }
        if (D()) {
            this.f300i = z();
        } else {
            this.f300i = -10000;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int s() {
        return Settings.Secure.getIntForUser(this.f292a.getContentResolverForUser(UserHandle.OWNER), "key_open_earthquake_warning", 0, 0);
    }

    public static synchronized UserCheckManager v(Context context) {
        try {
            if (f291n == null) {
                f291n = new UserCheckManager(context.getApplicationContext());
            }
        } catch (Throwable th) {
            throw th;
        }
        return f291n;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int y() {
        return Settings.Secure.getIntForUser(this.f292a.getContentResolverForUser(UserHandle.OWNER), "second_user_id", -10000, 0);
    }

    public boolean C(int i2) {
        return UserManager.get(this.f292a).isUserRunning(new UserHandle(i2));
    }

    public boolean D() {
        return MiuiSettings.Secure.getBoolean(this.f292a.getContentResolverForUser(UserHandle.OWNER), "xspace_enabled", false);
    }

    public boolean E() {
        return A() && D();
    }

    public void J(PowerKeeperInterface.g0 g0Var) {
        synchronized (this.f301j) {
            try {
                if (!this.f301j.contains(g0Var)) {
                    this.f301j.add(g0Var);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void K(PowerKeeperInterface.g0 g0Var) {
        synchronized (this.f301j) {
            try {
                if (this.f301j.contains(g0Var)) {
                    this.f301j.remove(g0Var);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public int[] t() {
        boolean zB = B();
        int[] iArr = zB ? new int[2] : new int[1];
        iArr[0] = w();
        if (zB) {
            iArr[1] = y();
        }
        return iArr;
    }

    public int u() {
        try {
            return ActivityManagerNative.getDefault().getCurrentUser().id;
        } catch (RemoteException e2) {
            Log.e(f289l, "getForgroundUser", e2);
            return 0;
        }
    }

    public int w() {
        return 0;
    }

    public int[] x() {
        boolean zC = B() ? C(y()) : false;
        int[] iArr = zC ? new int[2] : new int[1];
        iArr[0] = w();
        if (zC) {
            iArr[1] = y();
        }
        return iArr;
    }

    public int z() {
        if (D()) {
            return DeviceInfo.DUAL_SPACE_ID;
        }
        return -10000;
    }
}
