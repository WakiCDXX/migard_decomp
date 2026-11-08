package f;

import android.content.Context;
import android.util.Log;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.camera.KillCameraReceiver;

/* JADX INFO: compiled from: KillCamera.java */
/* JADX INFO: loaded from: classes.dex */
public class k extends q {
    public j b(Context context) {
        Log.d("M->KillCamera", " created.");
        this.f1812b = context;
        KillCameraReceiver.register(context, PowerKeeperManager.getHandler());
        this.f1811a = true;
        return this;
    }

    @Override // f.q, f.j
    public void destroy() {
        Log.d("M->KillCamera", " destroy.");
        if (this.f1811a) {
            KillCameraReceiver.unregister(this.f1812b);
            this.f1811a = false;
        }
    }
}
