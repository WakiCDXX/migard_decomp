package f;

import android.content.Context;
import android.util.Log;
import com.miui.powerkeeper.statemachine.ExtremeScenarioAwareHandler;
import com.miui.powerkeeper.statemachine.PowerStateMachineService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;

/* JADX INFO: compiled from: ExtremeScenarioAware.java */
/* JADX INFO: loaded from: classes.dex */
public class i extends q {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private ExtremeScenarioAwareHandler f1807c;

    public j b(Context context) {
        this.f1812b = context;
        if (!e.f.m()) {
            return null;
        }
        this.f1807c = ExtremeScenarioAwareHandler.init(this.f1812b, PowerStateMachineService.getThread().getLooper());
        this.f1811a = true;
        return this;
    }

    @Override // f.q, f.j
    public void destroy() {
        Log.d("M->ESAware", " destroy");
        if (this.f1811a) {
            this.f1807c.destroy();
            this.f1811a = false;
            this.f1807c = null;
        }
    }

    @Override // f.q, f.j
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        Log.d("M->ESAware", " dump, args = " + Arrays.toString(strArr));
        if (!this.f1811a || this.f1807c == null) {
            printWriter.println("ExtremeScenarioAwareHandler disabled");
        } else {
            printWriter.println("ExtremeScenarioAwareHandler enabled");
            this.f1807c.dump(fileDescriptor, printWriter, strArr);
        }
    }
}
