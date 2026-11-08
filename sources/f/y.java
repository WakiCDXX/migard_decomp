package f;

import android.content.Context;
import android.util.Log;
import com.miui.powerkeeper.statemachine.SatelliteTelephonyController;
import miui.telephony.TelephonyManagerEx;
import org.json.JSONObject;

/* JADX INFO: compiled from: SatelliteTelephony.java */
/* JADX INFO: loaded from: classes.dex */
public class y extends q {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private SatelliteTelephonyController f1819c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    private Context f1820d;

    @Override // f.q, f.j
    public void a(JSONObject jSONObject) {
        Log.d("M->SatelliteTelephony", " updateCloud.");
        if (this.f1811a) {
            SatelliteTelephonyController.onCloudFunctionUpdated(this.f1820d, jSONObject);
        }
    }

    public j b(Context context) {
        this.f1820d = context;
        if (!TelephonyManagerEx.getDefault().isSupportSatelliteByDevice()) {
            return null;
        }
        this.f1819c = SatelliteTelephonyController.getInstance();
        this.f1811a = true;
        return this;
    }

    @Override // f.q, f.j
    public void destroy() {
        Log.d("M->SatelliteTelephony", " destroy.");
        if (this.f1811a) {
            this.f1819c = null;
            this.f1811a = false;
        }
    }
}
