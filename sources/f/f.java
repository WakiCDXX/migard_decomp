package f;

import android.content.Context;
import android.util.Log;
import com.miui.powerkeeper.statemachine.DisplayDfpsHandler;
import com.miui.powerkeeper.statemachine.PowerStateMachineService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;
import org.json.JSONObject;

/* JADX INFO: compiled from: DisplayDfps.java */
/* JADX INFO: loaded from: classes.dex */
public class f extends q {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private DisplayDfpsHandler f1804c;

    @Override // f.q, f.j
    public void a(JSONObject jSONObject) {
        Log.d("M->DisplayDfps", " updateCloud.");
        if (this.f1811a) {
            DisplayDfpsHandler.onCloudFunctionUpdated(this.f1812b, jSONObject);
        }
    }

    public j b(Context context) {
        Log.d("M->DisplayDfps", " created.");
        this.f1812b = context;
        if (!e.f.e()) {
            return null;
        }
        this.f1804c = DisplayDfpsHandler.init(this.f1812b, PowerStateMachineService.getThread().getLooper());
        this.f1811a = true;
        return this;
    }

    public DisplayDfpsHandler c() {
        Log.d("M->DisplayDfps", " getHandler. handler = " + this.f1804c);
        return this.f1804c;
    }

    @Override // f.q, f.j
    public void destroy() {
        Log.d("M->DisplayDfps", " destroy.");
        if (this.f1811a) {
            this.f1804c = null;
            this.f1811a = false;
        }
    }

    @Override // f.q, f.j
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        Log.d("M->DisplayDfps", " dump, args = " + Arrays.toString(strArr));
        if (!this.f1811a || this.f1804c == null) {
            printWriter.println("mDisplayDfpsHandler disabled");
        } else {
            printWriter.println("mDisplayDfpsHandler enabled");
            this.f1804c.dump(fileDescriptor, printWriter, strArr);
        }
    }
}
