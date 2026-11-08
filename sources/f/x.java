package f;

import android.content.Context;
import android.util.Log;
import com.miui.powerkeeper.statemachine.ResolutionPolicyHandler;
import org.json.JSONObject;

/* JADX INFO: compiled from: ResolutionPolicy.java */
/* JADX INFO: loaded from: classes.dex */
public class x extends q {
    @Override // f.q, f.j
    public void a(JSONObject jSONObject) {
        Log.d("M->ResolutionPolicy", " updateCloud.");
        if (this.f1811a) {
            ResolutionPolicyHandler.onCloudFunctionUpdated(this.f1812b, jSONObject);
        }
    }

    public j b(Context context) {
        Log.d("M->ResolutionPolicy", " created.");
        this.f1812b = context;
        if (!e.f.k()) {
            return null;
        }
        ResolutionPolicyHandler.updateResolutionIfNeeded(null, context);
        this.f1811a = true;
        return this;
    }

    @Override // f.q, f.j
    public void destroy() {
        Log.d("M->ResolutionPolicy", " destroy.");
        if (this.f1811a) {
            this.f1811a = false;
        }
    }
}
