package f;

import android.content.Context;
import android.util.Log;
import com.miui.powerkeeper.statemachine.CpuDdrHandler;
import com.miui.powerkeeper.statemachine.PowerStateMachineService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;
import org.json.JSONObject;

/* JADX INFO: compiled from: CpuDdr.java */
/* JADX INFO: loaded from: classes.dex */
public class b extends q {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private CpuDdrHandler f1800c;

    @Override // f.q, f.j
    public void a(JSONObject jSONObject) {
        Log.d("M->CpuDdr", " updateCloud.");
        if (this.f1811a) {
            CpuDdrHandler.onCloudFunctionUpdated(this.f1812b, jSONObject);
        }
    }

    public j b(Context context) {
        Log.d("M->CpuDdr", " created.");
        this.f1812b = context;
        if (!e.f.c()) {
            return null;
        }
        this.f1800c = CpuDdrHandler.init(this.f1812b, PowerStateMachineService.getThread().getLooper());
        this.f1811a = true;
        return this;
    }

    @Override // f.q, f.j
    public void destroy() {
        Log.d("M->CpuDdr", " destroy.");
        if (this.f1811a) {
            this.f1800c = null;
            this.f1811a = false;
        }
    }

    @Override // f.q, f.j
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        Log.d("M->CpuDdr", " dump, args = " + Arrays.toString(strArr));
        if (!this.f1811a || this.f1800c == null) {
            printWriter.println("mNoiseCpuHandler disabled");
        } else {
            printWriter.println("mNoiseCpuHandler enabled");
            this.f1800c.dump(fileDescriptor, printWriter, strArr);
        }
    }
}
