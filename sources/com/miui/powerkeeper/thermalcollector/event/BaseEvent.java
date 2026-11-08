package com.miui.powerkeeper.thermalcollector.event;

import android.content.Context;
import android.util.Base64;
import com.google.gson.Gson;
import com.miui.powerkeeper.PowerKeeperApplication;
import com.miui.powerkeeper.provider.ThermalInfoHelper;
import com.miui.powerkeeper.thermalcollector.CollectorManager;
import com.miui.powerkeeper.thermalcollector.source.CollectorConfig;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import miui.os.Build;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public abstract class BaseEvent implements IEvent {
    protected final Context mContext = PowerKeeperApplication.b();
    protected final Gson mGson = CollectorConfig.sGson;

    private String postProcessCpuData(String str) {
        ArrayList arrayList = new ArrayList();
        if (str == null || str.length() < 3) {
            return arrayList.toString();
        }
        for (String str2 : str.substring(1, str.length() - 1).split(",")) {
            String[] strArrSplit = str2.trim().split("%|:|/");
            if (strArrSplit.length > 2) {
                if (Build.IS_INTERNATIONAL_BUILD) {
                    arrayList.add("{process=" + Base64.encodeToString(strArrSplit[2].getBytes(), 0) + ",cpu=" + strArrSplit[0].trim() + "}");
                } else {
                    arrayList.add("{process=" + strArrSplit[2] + ",cpu=" + strArrSplit[0].trim() + "}");
                }
            }
        }
        return arrayList.toString();
    }

    protected final void commitData(String str, String str2) {
        ThermalInfoHelper.insertThermalInfo(this.mContext, str2, str);
    }

    @Override // com.miui.powerkeeper.thermalcollector.event.IEvent, com.miui.powerkeeper.thermal.resource.IDumpable
    public void dump(PrintWriter printWriter) {
        printWriter.println("Using Event: " + getClass().getName());
    }

    protected final void saveJson(JSONObject jSONObject, JSONObject jSONObject2) {
        Iterator<String> itKeys = jSONObject2.keys();
        while (itKeys.hasNext()) {
            String next = itKeys.next();
            try {
                if (CollectorManager.getDataCollectorVersion().equals("2.0") && "cpu_process".equals(next)) {
                    jSONObject.put(next, postProcessCpuData((String) jSONObject2.get(next)));
                } else {
                    jSONObject.put(next, jSONObject2.get(next));
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    @Override // com.miui.powerkeeper.thermalcollector.event.IEvent
    public void settlement() {
    }
}
