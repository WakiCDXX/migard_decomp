package com.miui.powerkeeper;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import com.google.android.collect.Lists;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.utils.PackageUtil;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class IMECheckManager {

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    private static String f206l = "IMECheckManager";

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private final Context f207a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private final PowerKeeperManager f208b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private final Object f209c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private final List<PowerKeeperInterface.w> f210d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private final PowerKeeperInterface.g0 f211e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private final Map<String, InputMethodInfo> f212f = new ArrayMap();

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private final c f213g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private final Handler f214h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private SparseArray<String> f215i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private SparseIntArray f216j;

    /* JADX INFO: renamed from: k, reason: collision with root package name */
    private PowerKeeperInterface.b0 f217k;

    class a implements Runnable {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final /* synthetic */ PowerKeeperInterface.w f218a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        final /* synthetic */ int f219b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        final /* synthetic */ int f220c;

        a(PowerKeeperInterface.w wVar, int i2, int i3) {
            this.f218a = wVar;
            this.f219b = i2;
            this.f220c = i3;
        }

        @Override // java.lang.Runnable
        public void run() {
            PowerKeeperInterface.v vVar = this.f218a.f273b;
            if (vVar != null) {
                vVar.onIMEChange(this.f219b, this.f220c);
            }
        }
    }

    private class c extends ContentObserver {
        public c(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z2, Uri uri, int i2) {
            synchronized (IMECheckManager.this.f209c) {
                try {
                    String strI = IMECheckManager.this.i(i2);
                    Log.i(IMECheckManager.f206l, "InputMethodObserver, userId is " + i2 + ", ime = " + strI);
                    if (strI != null && !strI.isEmpty()) {
                        String str = (String) IMECheckManager.this.f215i.get(i2, null);
                        if (str == null || !strI.equalsIgnoreCase(str)) {
                            if (IMECheckManager.this.n(i2)) {
                                IMECheckManager iMECheckManager = IMECheckManager.this;
                                iMECheckManager.j(i2, iMECheckManager.f216j.get(i2, -1));
                            }
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    public IMECheckManager(Context context, PowerKeeperManager powerKeeperManager) {
        Handler handler = new Handler(PowerKeeperManager.getThread().getLooper());
        this.f214h = handler;
        this.f215i = new SparseArray<>();
        this.f216j = new SparseIntArray();
        this.f217k = new b();
        this.f207a = context;
        this.f208b = powerKeeperManager;
        Object obj = new Object();
        this.f209c = obj;
        this.f210d = Lists.newArrayList();
        synchronized (obj) {
            PowerKeeperInterface.g0 g0Var = new PowerKeeperInterface.g0(handler, this.f217k);
            this.f211e = g0Var;
            powerKeeperManager.getUserCheckManager().J(g0Var);
            c cVar = new c(handler);
            this.f213g = cVar;
            context.getContentResolverForUser(UserHandle.OWNER).registerContentObserver(Settings.Secure.getUriFor("default_input_method"), false, cVar, -1);
            n(powerKeeperManager.getUserCheckManager().u());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String i(int i2) {
        String stringForUser = Settings.Secure.getStringForUser(this.f207a.getContentResolverForUser(UserHandle.OWNER), "default_input_method", i2);
        Log.i(f206l, "getSelectedInputMethod, ime is " + stringForUser);
        return stringForUser;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void j(int i2, int i3) {
        EventsAggregator.getInstance(this.f207a).notifyImeChangedListeners(i2, i3);
        synchronized (this.f210d) {
            try {
                for (PowerKeeperInterface.w wVar : this.f210d) {
                    Handler handler = wVar.f272a;
                    if (handler == null) {
                        handler = this.f214h;
                    }
                    handler.post(new a(wVar, i2, i3));
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private void k() {
        this.f212f.clear();
        for (InputMethodInfo inputMethodInfo : ((InputMethodManager) this.f207a.getSystemService("input_method")).getInputMethodList()) {
            this.f212f.put(inputMethodInfo.getId(), inputMethodInfo);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean n(int i2) {
        k();
        String strI = i(i2);
        InputMethodInfo inputMethodInfo = this.f212f.get(strI);
        if (inputMethodInfo == null) {
            Log.e(f206l, "Unknown IME: " + strI);
            return false;
        }
        this.f215i.put(i2, strI);
        String packageName = inputMethodInfo.getPackageName();
        int uidByPackageName = PackageUtil.getUidByPackageName(this.f207a, packageName, i2);
        this.f216j.put(i2, uidByPackageName);
        Log.i(f206l, "updateCurrentIME, userId is " + i2 + ", ime is " + strI + ", pkg is " + packageName + ", uid is " + uidByPackageName);
        return true;
    }

    public int h(int i2) {
        int i3;
        synchronized (this.f209c) {
            i3 = this.f216j.get(i2, -1);
        }
        return i3;
    }

    public void l(PowerKeeperInterface.w wVar) {
        synchronized (this.f210d) {
            try {
                if (!this.f210d.contains(wVar)) {
                    this.f210d.add(wVar);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void m(PowerKeeperInterface.w wVar) {
        synchronized (this.f210d) {
            try {
                if (this.f210d.contains(wVar)) {
                    this.f210d.remove(wVar);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    class b implements PowerKeeperInterface.b0 {
        b() {
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
        public void onUserEnableStatusChange(int i2, boolean z2) {
            synchronized (IMECheckManager.this.f209c) {
                if (!z2) {
                    try {
                        IMECheckManager.this.f215i.delete(i2);
                        IMECheckManager.this.f216j.delete(i2);
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
        public void onUserForegroundStatusChange(int i2, boolean z2) {
            synchronized (IMECheckManager.this.f209c) {
                try {
                    Log.i(IMECheckManager.f206l, "onUserForegroundStatusChange, userId is " + i2);
                    if (z2) {
                        String strI = IMECheckManager.this.i(i2);
                        Log.i(IMECheckManager.f206l, "onUserForegroundStatusChange, ime = " + strI);
                        String str = (String) IMECheckManager.this.f215i.get(i2, null);
                        if (str != null && strI.equalsIgnoreCase(str)) {
                            return;
                        }
                        if (IMECheckManager.this.n(i2)) {
                            IMECheckManager iMECheckManager = IMECheckManager.this;
                            iMECheckManager.j(i2, iMECheckManager.f216j.get(i2, -1));
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
        public void onUserRunningStatusChange(int i2, boolean z2) {
            synchronized (IMECheckManager.this.f209c) {
                if (!z2) {
                    try {
                        IMECheckManager.this.f215i.delete(i2);
                        IMECheckManager.this.f216j.delete(i2);
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
        public void onXSpaceStatusChange(int i2, boolean z2) {
        }
    }
}
