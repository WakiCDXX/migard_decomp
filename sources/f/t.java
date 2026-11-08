package f;

import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.util.Log;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.powerchecker.PowerCheckerService;

/* JADX INFO: compiled from: PowerChecker.java */
/* JADX INFO: loaded from: classes.dex */
public class t extends q {
    public j b(Context context) {
        Log.d("M->PowerChecker", " created.");
        this.f1812b = context;
        Intent intent = new Intent(this.f1812b, (Class<?>) PowerCheckerService.class);
        intent.setAction("android.intent.action.BOOT_COMPLETED");
        this.f1812b.startServiceAsUser(intent, UserHandle.OWNER);
        PowerKeeperManager.getInstance().bindPowerCheckerService();
        this.f1811a = true;
        return this;
    }

    @Override // f.q, f.j
    public void destroy() {
        Log.d("M->PowerChecker", " destroy.");
        if (this.f1811a) {
            PowerKeeperManager.getInstance().unbindPowerCheckerService();
            this.f1811a = false;
        }
    }
}
