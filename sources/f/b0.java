package f;

import android.content.Context;
import android.util.Log;
import com.miui.powerkeeper.statemachine.PowerStateMachineService;
import com.miui.powerkeeper.statemachine.SyncController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;
import org.json.JSONObject;

/* JADX INFO: compiled from: Sync.java */
/* JADX INFO: loaded from: classes.dex */
public class b0 extends q {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private SyncController f1801c;

    @Override // f.q, f.j
    public void a(JSONObject jSONObject) {
        Log.d("M->Sync", " updateCloud.");
        if (this.f1811a) {
            SyncController.onCloudFunctionUpdated(this.f1812b, jSONObject);
        }
    }

    public j b(Context context) {
        Log.d("M->Sync", " created.");
        this.f1812b = context;
        this.f1801c = SyncController.getInstance(context, PowerStateMachineService.getThread().getLooper());
        this.f1811a = true;
        return this;
    }

    public SyncController c() {
        Log.d("M->Sync", " getHandler. handler = " + this.f1801c);
        return this.f1801c;
    }

    @Override // f.q, f.j
    public void destroy() {
        Log.d("M->Sync", " destroy.");
        if (this.f1811a) {
            this.f1801c = null;
            this.f1811a = false;
        }
    }

    @Override // f.q, f.j
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        Log.d("M->Sync", " dump, args = " + Arrays.toString(strArr));
        if (!this.f1811a || this.f1801c == null) {
            printWriter.println("mSyncController disabled");
        } else {
            printWriter.println("mSyncController enabled");
            this.f1801c.dump(fileDescriptor, printWriter, strArr);
        }
    }
}
