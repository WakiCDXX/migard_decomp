package f;

import android.content.Context;
import android.util.Log;
import com.miui.powerkeeper.statemachine.PowerStateMachineService;
import com.miui.powerkeeper.statemachine.SocOptimizationHandler;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;
import org.json.JSONObject;

/* JADX INFO: compiled from: SocOptimization.java */
/* JADX INFO: loaded from: classes.dex */
public class a0 extends q {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private SocOptimizationHandler f1799c;

    @Override // f.q, f.j
    public void a(JSONObject jSONObject) {
        Log.d("M->SocOptimization", " updateCloud.");
        if (this.f1811a) {
            SocOptimizationHandler.onCloudFunctionUpdated(this.f1812b, jSONObject);
        }
    }

    public j b(Context context) {
        Log.d("M->SocOptimization", " created.");
        this.f1812b = context;
        if (!e.f.l()) {
            return null;
        }
        this.f1799c = SocOptimizationHandler.getInstance(this.f1812b, PowerStateMachineService.getThread().getLooper());
        this.f1811a = true;
        return this;
    }

    @Override // f.q, f.j
    public void destroy() {
        Log.d("M->SocOptimization", " destroy.");
        if (this.f1811a) {
            this.f1799c = null;
            this.f1811a = false;
        }
    }

    @Override // f.q, f.j
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        Log.d("M->SocOptimization", " dump, args = " + Arrays.toString(strArr));
        if (!this.f1811a || this.f1799c == null) {
            printWriter.println("mSocOptimizationHandler disabled");
        } else {
            printWriter.println("mSocOptimizationHandler enabled");
            this.f1799c.dump(fileDescriptor, printWriter, strArr);
        }
    }
}
