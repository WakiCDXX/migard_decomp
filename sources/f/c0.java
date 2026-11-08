package f;

import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.util.Log;
import com.miui.powerkeeper.unionpower.UnionPowerService;
import com.miui.powerkeeper.unionpower.utils.UnionPowerConfig;
import org.json.JSONObject;

/* JADX INFO: compiled from: UnionPower.java */
/* JADX INFO: loaded from: classes.dex */
public class c0 extends q {
    public j b(Context context) {
        Log.d("M->UnionPower", " created.");
        this.f1812b = context;
        if (!UnionPowerConfig.f657b) {
            return null;
        }
        this.f1812b.startServiceAsUser(new Intent(this.f1812b, (Class<?>) UnionPowerService.class), UserHandle.OWNER);
        this.f1811a = true;
        return this;
    }

    @Override // f.q, f.j
    public void destroy() {
        Log.d("M->UnionPower", " destroy.");
        if (this.f1811a) {
            this.f1812b.stopService(new Intent(this.f1812b, (Class<?>) UnionPowerService.class));
        }
    }

    @Override // f.q, f.j
    public void a(JSONObject jSONObject) {
    }
}
