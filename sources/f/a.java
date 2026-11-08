package f;

import android.content.Context;
import android.util.Log;
import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.batterylife.BatteryLifeChecker;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;
import org.json.JSONObject;

/* JADX INFO: compiled from: BatteryLife.java */
/* JADX INFO: loaded from: classes.dex */
public class a extends q {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    BatteryLifeChecker f1798c;

    @Override // f.q, f.j
    public void a(JSONObject jSONObject) {
        Log.d("M->BatteryLife", " updateCloud.");
        if (this.f1811a) {
            this.f1798c.onCloudFunctionUpdated(jSONObject);
        }
    }

    public j b(Context context) {
        Log.d("M->BatteryLife", " created.");
        this.f1812b = context;
        this.f1798c = new BatteryLifeChecker(this.f1812b);
        PowerKeeperManager.getInstance().getTimeScheduleManager().e(0, this.f1798c);
        this.f1811a = true;
        return this;
    }

    @Override // f.q, f.j
    public void destroy() {
        Log.d("M->BatteryLife", " destroy.");
        if (this.f1798c == null || !this.f1811a) {
            return;
        }
        PowerKeeperManager.getInstance().getTimeScheduleManager().g(0, this.f1798c);
        this.f1798c.destory();
        this.f1798c = null;
        this.f1811a = false;
    }

    @Override // f.q, f.j
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        BatteryLifeChecker batteryLifeChecker;
        Log.d("M->BatteryLife", " dump, args = " + Arrays.toString(strArr));
        if (!this.f1811a || (batteryLifeChecker = this.f1798c) == null) {
            return;
        }
        batteryLifeChecker.dump(printWriter, strArr);
    }
}
