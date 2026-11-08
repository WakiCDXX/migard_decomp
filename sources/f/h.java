package f;

import android.content.Context;
import android.os.SystemProperties;
import android.util.Log;
import com.miui.powerkeeper.statemachine.DynamicTurboPowerHandler;
import com.miui.powerkeeper.statemachine.PowerStateMachineService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;
import org.json.JSONObject;

/* JADX INFO: compiled from: DynamicTurboPower.java */
/* JADX INFO: loaded from: classes.dex */
public class h extends q {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private DynamicTurboPowerHandler f1806c;

    @Override // f.q, f.j
    public void a(JSONObject jSONObject) {
        Log.d("M->DynamicTurboPower", " updateCloud.");
        if (this.f1811a) {
            DynamicTurboPowerHandler.onCloudFunctionUpdated(this.f1812b, jSONObject);
        }
    }

    public j b(Context context) {
        boolean z2 = false;
        try {
            JSONObject jSONObjectL = b.a.p().l(b.b.f35q);
            if (jSONObjectL != null && jSONObjectL.optBoolean("fucSwitch", false)) {
                z2 = true;
            }
            Log.i("M->DynamicTurboPower", "create mIsFeatureOn = " + z2);
        } catch (Exception e2) {
            Log.e("M->DynamicTurboPower", "create error", e2);
        }
        Log.d("M->DynamicTurboPower", " created ModulesUtils.isSupportDeviceForDynamicTurboPower() is " + e.f.g());
        this.f1812b = context;
        if ((!z2 && !e.f.g()) || !SystemProperties.get("ro.miui.region", "unknown").equalsIgnoreCase("cn")) {
            return null;
        }
        this.f1806c = DynamicTurboPowerHandler.init(this.f1812b, PowerStateMachineService.getThread().getLooper());
        this.f1811a = true;
        return this;
    }

    @Override // f.q, f.j
    public void destroy() {
        Log.d("M->DynamicTurboPower", " destroy.");
        if (this.f1811a) {
            this.f1806c = null;
            this.f1811a = false;
        }
    }

    @Override // f.q, f.j
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        Log.d("M->DynamicTurboPower", " dump, args = " + Arrays.toString(strArr));
        if (!this.f1811a || this.f1806c == null) {
            printWriter.println("mDynamicTurboPower disabled");
        } else {
            printWriter.println("mDynamicTurboPower enabled");
            this.f1806c.dump(fileDescriptor, printWriter, strArr);
        }
    }
}
