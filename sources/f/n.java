package f;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import com.miui.powerkeeper.feedbackcontrol.ThermalManager;

/* JADX INFO: compiled from: MThermalManager.java */
/* JADX INFO: loaded from: classes.dex */
public class n extends q {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    ThermalManager f1809c;

    public j b(Context context) {
        this.f1812b = context;
        Log.d("M->MThermalManager", "MThermalManager created.");
        return this;
    }

    public ThermalManager c(Handler handler, boolean z2) {
        ThermalManager thermalManagerInit = ThermalManager.init(this.f1812b, handler, z2);
        this.f1809c = thermalManagerInit;
        return thermalManagerInit;
    }

    @Override // f.q, f.j
    public void destroy() {
        Log.d("M->MThermalManager", "MThermalManager destory.");
    }
}
