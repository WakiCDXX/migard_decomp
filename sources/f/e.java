package f;

import android.content.Context;
import android.util.Log;
import com.miui.powerkeeper.statemachine.DisplayCabcHandler;
import com.miui.powerkeeper.statemachine.PowerStateMachineService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;
import org.json.JSONObject;

/* JADX INFO: compiled from: DisplayCabc.java */
/* JADX INFO: loaded from: classes.dex */
public class e extends q {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private DisplayCabcHandler f1803c;

    @Override // f.q, f.j
    public void a(JSONObject jSONObject) {
        Log.d("M->DisplayCabc", " updateCloud.");
        if (this.f1811a) {
            DisplayCabcHandler.onCloudFunctionUpdated(this.f1812b, jSONObject);
        }
    }

    public j b(Context context) {
        Log.d("M->DisplayCabc", " created.");
        this.f1812b = context;
        if (!e.f.d()) {
            return null;
        }
        this.f1803c = DisplayCabcHandler.init(this.f1812b, PowerStateMachineService.getThread().getLooper());
        this.f1811a = true;
        return this;
    }

    public DisplayCabcHandler c() {
        Log.d("M->DisplayCabc", " getHandler. handler = " + this.f1803c);
        return this.f1803c;
    }

    @Override // f.q, f.j
    public void destroy() {
        Log.d("M->DisplayCabc", " destroy.");
        if (this.f1811a) {
            this.f1803c = null;
            this.f1811a = false;
        }
    }

    @Override // f.q, f.j
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        Log.d("M->DisplayCabc", " dump, args = " + Arrays.toString(strArr));
        if (!this.f1811a || this.f1803c == null) {
            printWriter.println("mDisplayCabcHandler disabled");
        } else {
            printWriter.println("mDisplayCabcHandler enabled");
            this.f1803c.dump(fileDescriptor, printWriter, strArr);
        }
    }
}
