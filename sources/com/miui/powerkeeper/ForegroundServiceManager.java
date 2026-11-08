package com.miui.powerkeeper;

import android.annotation.NonNull;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.UserHandle;
import android.util.ArraySet;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.os.BackgroundThread;
import com.miui.powerkeeper.event.EventsAggregator;
import com.miui.powerkeeper.provider.SimpleSettingKeys;
import com.miui.powerkeeper.provider.SimpleSettings;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class ForegroundServiceManager extends Handler {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static volatile ForegroundServiceManager f193e;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private final SparseArray<ArraySet<String>> f194a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private final Object f195b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private final boolean f196c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private final Context f197d;

    public ForegroundServiceManager(Context context) {
        super(BackgroundThread.get().getLooper());
        this.f194a = new SparseArray<>();
        this.f195b = new Object();
        this.f196c = Build.IS_DEBUGGABLE;
        this.f197d = context;
        post(new Runnable() { // from class: com.miui.powerkeeper.b
            @Override // java.lang.Runnable
            public final void run() {
                this.f371a.d();
            }
        });
    }

    public static ForegroundServiceManager b(Context context) {
        if (f193e == null) {
            synchronized (ForegroundServiceManager.class) {
                try {
                    if (f193e == null) {
                        f193e = new ForegroundServiceManager(context);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return f193e;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void d() {
        e();
        EventsAggregator eventsAggregator = EventsAggregator.getInstance(this.f197d);
        eventsAggregator.registerCloudUpdateListener(this);
        eventsAggregator.registerPackageAddedListener(this);
        eventsAggregator.registerPackageRemovedListener(this);
    }

    private void e() {
        SparseArray sparseArray = new SparseArray();
        String string = SimpleSettings.Misc.getString(this.f197d, SimpleSettingKeys.KEY_RESTRICT_SERVICE_GROUP);
        Map<String, Integer> packageUidsMap = PowerKeeperManager.getInstance().getPackageUidsMap(0);
        if (string != null && !string.equals("")) {
            for (String str : string.split(",")) {
                String[] strArrSplit = str.split("/");
                if (packageUidsMap.containsKey(strArrSplit[0])) {
                    int iIntValue = packageUidsMap.get(strArrSplit[0]).intValue();
                    if (UserHandle.isApp(iIntValue)) {
                        ArraySet arraySet = (ArraySet) sparseArray.get(UserHandle.getAppId(iIntValue));
                        if (arraySet == null) {
                            arraySet = new ArraySet();
                            sparseArray.put(UserHandle.getAppId(iIntValue), arraySet);
                        }
                        arraySet.add(strArrSplit[1]);
                    }
                }
            }
        }
        synchronized (this.f195b) {
            try {
                this.f194a.clear();
                for (int size = sparseArray.size() - 1; size >= 0; size--) {
                    this.f194a.put(sparseArray.keyAt(size), (ArraySet) sparseArray.valueAt(size));
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public boolean c(int i2, int i3) {
        int appId = UserHandle.getAppId(i2);
        synchronized (this.f195b) {
            try {
                ArraySet<String> arraySet = this.f194a.get(appId);
                if (arraySet != null) {
                    try {
                        for (ActivityManager.RunningServiceInfo runningServiceInfo : ActivityManager.getService().getServices(1000, 0)) {
                            if (runningServiceInfo.uid == i2 && runningServiceInfo.pid == i3 && runningServiceInfo.foreground && !arraySet.contains(runningServiceInfo.service.getClassName())) {
                                if (this.f196c) {
                                    Log.d("ForegroundServiceManger", "isUidFgServiceAllowed: uid=" + i2 + " allowed service=" + runningServiceInfo.service.getClassName());
                                }
                                return true;
                            }
                        }
                        return false;
                    } catch (Exception unused) {
                        Log.d("ForegroundServiceManger", "getServices failed");
                    }
                }
                return true;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // android.os.Handler
    public void handleMessage(@NonNull Message message) {
        int i2 = message.what;
        if (i2 != -17) {
            if (i2 == -2) {
                synchronized (this.f195b) {
                    this.f194a.delete(message.arg2);
                }
                return;
            } else if (i2 != -1) {
                return;
            }
        }
        e();
    }
}
