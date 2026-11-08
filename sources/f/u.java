package f;

import android.content.Context;
import android.util.Log;
import com.miui.powerkeeper.statemachine.PowerModeHandler;
import com.miui.powerkeeper.statemachine.PowerStateMachineService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;
import org.json.JSONObject;

/* JADX INFO: compiled from: PowerMode.java */
/* JADX INFO: loaded from: classes.dex */
public class u extends q {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private PowerModeHandler f1815c;

    @Override // f.q, f.j
    public void a(JSONObject jSONObject) {
        Log.d("M->PowerMode", " updateCloud.");
        if (this.f1811a) {
            PowerModeHandler.onCloudFunctionUpdated(this.f1812b, jSONObject);
        }
    }

    public j b(Context context) {
        Log.d("M->PowerMode", " created.");
        this.f1812b = context;
        if (!e.f.i()) {
            return null;
        }
        this.f1815c = PowerModeHandler.init(this.f1812b, PowerStateMachineService.getThread().getLooper());
        this.f1811a = true;
        return this;
    }

    @Override // f.q, f.j
    public void destroy() {
        Log.d("M->PowerMode", " destroy.");
        if (this.f1811a) {
            this.f1815c = null;
            this.f1811a = false;
        }
    }

    @Override // f.q, f.j
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        Log.d("M->PowerMode", " dump, args = " + Arrays.toString(strArr));
        if (!this.f1811a || this.f1815c == null) {
            printWriter.println("mPowerModeHandler disabled");
        } else {
            printWriter.println("mPowerModeHandler enabled");
            this.f1815c.dump(fileDescriptor, printWriter, strArr);
        }
    }
}
