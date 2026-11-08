package f;

import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.util.Log;
import com.miui.powerkeeper.logsystem.LogSystemService;

/* JADX INFO: compiled from: LogSystem.java */
/* JADX INFO: loaded from: classes.dex */
public class l extends q {
    public j b(Context context) {
        this.f1812b = context;
        Log.d("M->LogSystem", "LogSystem created.");
        if (!e.f.a()) {
            return null;
        }
        this.f1812b.startServiceAsUser(new Intent(this.f1812b, (Class<?>) LogSystemService.class), UserHandle.OWNER);
        this.f1811a = true;
        return this;
    }

    @Override // f.q, f.j
    public void destroy() {
        Log.d("M->LogSystem", "LogSystem destory.");
        if (this.f1811a) {
            this.f1812b.stopService(new Intent(this.f1812b, (Class<?>) LogSystemService.class));
            this.f1811a = false;
        }
    }
}
