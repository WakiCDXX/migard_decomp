package f;

import android.content.Context;
import android.util.Log;
import com.miui.powerkeeper.statemachine.DisplayStandardVideoHandler;
import com.miui.powerkeeper.statemachine.PowerStateMachineService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;
import org.json.JSONObject;

/* JADX INFO: compiled from: DisplayStandardVideo.java */
/* JADX INFO: loaded from: classes.dex */
public class g extends q {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private DisplayStandardVideoHandler f1805c;

    @Override // f.q, f.j
    public void a(JSONObject jSONObject) {
        Log.d("M->DisplayStandardVideo", " updateCloud.");
        if (this.f1811a) {
            DisplayStandardVideoHandler.onCloudFunctionUpdated(this.f1812b, jSONObject);
        }
    }

    public j b(Context context) {
        Log.d("M->DisplayStandardVideo", " created.");
        this.f1812b = context;
        if (!e.f.f()) {
            return null;
        }
        this.f1805c = DisplayStandardVideoHandler.init(this.f1812b, PowerStateMachineService.getThread().getLooper());
        this.f1811a = true;
        return this;
    }

    @Override // f.q, f.j
    public void destroy() {
        Log.d("M->DisplayStandardVideo", " destroy.");
        if (this.f1811a) {
            this.f1805c = null;
            this.f1811a = false;
        }
    }

    @Override // f.q, f.j
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        Log.d("M->DisplayStandardVideo", " dump, args = " + Arrays.toString(strArr));
        if (!this.f1811a || this.f1805c == null) {
            printWriter.println("mDisplayStandardVideoHandler disabled");
        } else {
            printWriter.println("mDisplayStandardVideoHandler enabled");
            this.f1805c.dump(fileDescriptor, printWriter, strArr);
        }
    }
}
