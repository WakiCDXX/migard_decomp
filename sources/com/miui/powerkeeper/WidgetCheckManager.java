package com.miui.powerkeeper;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseArray;
import com.google.android.collect.Lists;
import com.google.android.collect.Sets;
import com.miui.powerkeeper.utils.PackageUtil;
import java.util.List;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class WidgetCheckManager {

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    private static String f355j = "WidgetCheckManager";

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private final Context f356a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private final PowerKeeperManager f357b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private final Object f358c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private final List<PowerKeeperInterface.h0> f359d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private final PowerKeeperInterface.g0 f360e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    private final c f361f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    private final Handler f362g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    private final SparseArray<Set<Integer>> f363h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    private PowerKeeperInterface.b0 f364i;

    class a implements Runnable {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final /* synthetic */ PowerKeeperInterface.h0 f365a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        final /* synthetic */ int f366b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        final /* synthetic */ Integer[] f367c;

        a(PowerKeeperInterface.h0 h0Var, int i2, Integer[] numArr) {
            this.f365a = h0Var;
            this.f366b = i2;
            this.f367c = numArr;
        }

        @Override // java.lang.Runnable
        public void run() {
            PowerKeeperInterface.c0 c0Var = this.f365a.f271b;
            if (c0Var != null) {
                c0Var.onWidgetChange(this.f366b, this.f367c);
            }
        }
    }

    private class c extends ContentObserver {
        public c(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z2, Uri uri, int i2) {
            synchronized (WidgetCheckManager.this.f358c) {
                try {
                    if (WidgetCheckManager.this.i(i2)) {
                        WidgetCheckManager widgetCheckManager = WidgetCheckManager.this;
                        widgetCheckManager.f(i2, widgetCheckManager.e(i2));
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    public WidgetCheckManager(Context context, PowerKeeperManager powerKeeperManager) {
        Handler handler = new Handler(PowerKeeperManager.getThread().getLooper());
        this.f362g = handler;
        this.f363h = new SparseArray<>();
        this.f364i = new b();
        this.f356a = context;
        this.f357b = powerKeeperManager;
        Object obj = new Object();
        this.f358c = obj;
        this.f359d = Lists.newArrayList();
        synchronized (obj) {
            try {
                PowerKeeperInterface.g0 g0Var = new PowerKeeperInterface.g0(handler, this.f364i);
                this.f360e = g0Var;
                powerKeeperManager.getUserCheckManager().J(g0Var);
                c cVar = new c(handler);
                this.f361f = cVar;
                context.getContentResolverForUser(UserHandle.OWNER).registerContentObserver(Settings.Secure.getUriFor("enabled_widgets"), false, cVar, -1);
                for (int i2 : powerKeeperManager.getUserCheckManager().x()) {
                    i(i2);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void f(int i2, Integer[] numArr) {
        synchronized (this.f359d) {
            try {
                for (PowerKeeperInterface.h0 h0Var : this.f359d) {
                    Handler handler = h0Var.f270a;
                    if (handler == null) {
                        handler = this.f362g;
                    }
                    handler.post(new a(h0Var, i2, numArr));
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean i(int i2) {
        Set<Integer> setNewHashSet = this.f363h.get(i2);
        if (setNewHashSet == null) {
            setNewHashSet = Sets.newHashSet();
            this.f363h.put(i2, setNewHashSet);
        }
        setNewHashSet.clear();
        String stringForUser = Settings.Secure.getStringForUser(this.f356a.getContentResolverForUser(UserHandle.OWNER), "enabled_widgets", i2);
        if (stringForUser != null && !"".equals(stringForUser)) {
            for (String str : stringForUser.split(":")) {
                int uidByPackageName = PackageUtil.getUidByPackageName(this.f356a, str, i2);
                if (uidByPackageName != -1) {
                    setNewHashSet.add(Integer.valueOf(uidByPackageName));
                }
            }
        }
        Log.i(f355j, "updateCurrentWidget, userId is " + i2 + ", widgetUidSet: " + setNewHashSet);
        return true;
    }

    public Integer[] e(int i2) {
        Integer[] numArr;
        synchronized (this.f358c) {
            try {
                Set<Integer> setNewHashSet = this.f363h.get(i2);
                if (setNewHashSet == null) {
                    setNewHashSet = Sets.newHashSet();
                }
                numArr = (Integer[]) setNewHashSet.toArray(new Integer[setNewHashSet.size()]);
            } catch (Throwable th) {
                throw th;
            }
        }
        return numArr;
    }

    public void g(PowerKeeperInterface.h0 h0Var) {
        synchronized (this.f359d) {
            try {
                if (!this.f359d.contains(h0Var)) {
                    this.f359d.add(h0Var);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void h(PowerKeeperInterface.h0 h0Var) {
        synchronized (this.f359d) {
            try {
                if (this.f359d.contains(h0Var)) {
                    this.f359d.remove(h0Var);
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
            synchronized (WidgetCheckManager.this.f358c) {
                if (!z2) {
                    try {
                        Set set = (Set) WidgetCheckManager.this.f363h.get(i2);
                        if (set != null) {
                            set.clear();
                            WidgetCheckManager.this.f363h.delete(i2);
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
        public void onUserRunningStatusChange(int i2, boolean z2) {
            synchronized (WidgetCheckManager.this.f358c) {
                try {
                    if (!z2) {
                        Set set = (Set) WidgetCheckManager.this.f363h.get(i2);
                        if (set != null) {
                            set.clear();
                            WidgetCheckManager.this.f363h.delete(i2);
                        }
                    } else if (WidgetCheckManager.this.i(i2)) {
                        WidgetCheckManager widgetCheckManager = WidgetCheckManager.this;
                        widgetCheckManager.f(i2, widgetCheckManager.e(i2));
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
        public void onUserForegroundStatusChange(int i2, boolean z2) {
        }

        @Override // com.miui.powerkeeper.PowerKeeperInterface.b0
        public void onXSpaceStatusChange(int i2, boolean z2) {
        }
    }
}
