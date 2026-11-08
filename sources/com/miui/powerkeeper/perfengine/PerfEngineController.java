package com.miui.powerkeeper.perfengine;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import com.miui.powerkeeper.utils.PowerLog;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miui.process.ForegroundInfo;
import miui.process.IForegroundInfoListener;
import miui.process.ProcessManager;

/* JADX INFO: loaded from: classes.dex */
public class PerfEngineController {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private static PerfEngineController f471c;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private List<c> f472a = new ArrayList();

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private IForegroundInfoListener.Stub f473b;

    private PerfEngineController(Context context) {
        IForegroundInfoListener.Stub stub = new IForegroundInfoListener.Stub() { // from class: com.miui.powerkeeper.perfengine.PerfEngineController.1
            public void onForegroundInfoChanged(ForegroundInfo foregroundInfo) throws RemoteException {
                Log.d("PerfEngineController", foregroundInfo.toString());
                synchronized (PerfEngineController.this.f472a) {
                    try {
                        Iterator it = PerfEngineController.this.f472a.iterator();
                        while (it.hasNext()) {
                            ((c) it.next()).a(foregroundInfo);
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        };
        this.f473b = stub;
        ProcessManager.registerForegroundInfoListener(stub);
        if (PowerLog.D) {
            Log.d("PerfEngineController", "Created");
        }
    }

    public static PerfEngineController b(Context context) {
        if (f471c == null) {
            synchronized (PerfEngineController.class) {
                try {
                    if (f471c == null) {
                        f471c = new PerfEngineController(context);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return f471c;
    }

    public void c(c cVar) {
        synchronized (this.f472a) {
            this.f472a.add(cVar);
        }
    }
}
