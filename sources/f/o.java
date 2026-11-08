package f;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.miui.powerkeeper.thermal.ThermalService;

/* JADX INFO: compiled from: MThermalService.java */
/* JADX INFO: loaded from: classes.dex */
public class o extends q {
    public j b(Context context) {
        this.f1812b = context;
        Log.d("M->MThermalService", "MThermalService created.");
        return this;
    }

    public void c() {
        this.f1812b.startService(new Intent(this.f1812b, (Class<?>) ThermalService.class));
    }

    @Override // f.q, f.j
    public void destroy() {
        Log.d("M->MThermalService", "MThermalService destory.");
    }
}
