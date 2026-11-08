package f;

import android.content.Context;
import android.util.Log;
import com.miui.powerkeeper.statemachine.PowerSaveModeHandler;
import com.miui.powerkeeper.statemachine.PowerStateMachineService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;
import org.json.JSONObject;

/* JADX INFO: compiled from: PowerSave.java */
/* JADX INFO: loaded from: classes.dex */
public class v extends q {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private PowerSaveModeHandler f1816c;

    @Override // f.q, f.j
    public void a(JSONObject jSONObject) {
        Log.d("M->PowerSave", " updateCloud.");
        if (this.f1811a) {
            PowerSaveModeHandler.onCloudFunctionUpdated(this.f1812b, jSONObject);
        }
    }

    public j b(Context context) {
        Log.d("M->PowerSave", " created.");
        this.f1812b = context;
        if (!e.f.j()) {
            return null;
        }
        this.f1816c = PowerSaveModeHandler.init(this.f1812b, PowerStateMachineService.getThread().getLooper());
        this.f1811a = true;
        return this;
    }

    @Override // f.q, f.j
    public void destroy() throws Throwable {
        Log.d("M->PowerSave", " destroy.");
        if (this.f1811a) {
            this.f1811a = false;
            this.f1816c.onDestroy();
            this.f1816c = null;
        }
    }

    @Override // f.q, f.j
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        Log.d("M->PowerSave", " dump, args = " + Arrays.toString(strArr));
        if (!this.f1811a || this.f1816c == null) {
            printWriter.println("mPowerSaveModeHandler disabled");
        } else {
            printWriter.println("mPowerSaveModeHandler enabled");
            this.f1816c.dump(fileDescriptor, printWriter, strArr);
        }
    }
}
