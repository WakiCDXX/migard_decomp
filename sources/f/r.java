package f;

import android.content.Context;
import android.util.Log;
import com.miui.powerkeeper.statemachine.NoiseCpuHandler;
import com.miui.powerkeeper.statemachine.PowerStateMachineService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;
import org.json.JSONObject;

/* JADX INFO: compiled from: NoiseCpu.java */
/* JADX INFO: loaded from: classes.dex */
public class r extends q {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private NoiseCpuHandler f1813c;

    @Override // f.q, f.j
    public void a(JSONObject jSONObject) {
        Log.d("M->NoiseCpu", " updateCloud.");
        if (this.f1811a) {
            NoiseCpuHandler.onCloudFunctionUpdated(this.f1812b, jSONObject);
        }
    }

    public j b(Context context) {
        Log.d("M->NoiseCpu", " created.");
        this.f1812b = context;
        if (!e.f.h()) {
            return null;
        }
        this.f1813c = NoiseCpuHandler.init(this.f1812b, PowerStateMachineService.getThread().getLooper());
        this.f1811a = true;
        return this;
    }

    @Override // f.q, f.j
    public void destroy() {
        Log.d("M->NoiseCpu", " destroy.");
        if (this.f1811a) {
            this.f1813c = null;
            this.f1811a = false;
        }
    }

    @Override // f.q, f.j
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        Log.d("M->NoiseCpu", " dump, args = " + Arrays.toString(strArr));
        if (!this.f1811a || this.f1813c == null) {
            printWriter.println("mNoiseCpuHandler disabled");
        } else {
            printWriter.println("mNoiseCpuHandler enabled");
            this.f1813c.dump(fileDescriptor, printWriter, strArr);
        }
    }
}
