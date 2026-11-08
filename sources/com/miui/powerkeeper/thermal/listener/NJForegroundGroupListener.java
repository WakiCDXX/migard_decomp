package com.miui.powerkeeper.thermal.listener;

import com.miui.powerkeeper.cloudcontrol.AutoRegister;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.thermal.ScenarioManager;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import miui.process.ForegroundInfo;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class NJForegroundGroupListener extends ForegroundGroupListener {
    public static final int ELEMENT_FOREGROUND_DOUYIN = 18;
    public static final int ELEMENT_FOREGROUND_UPDATE = 19;
    private final Map<String, Integer> mAppendForegroundMap = new HashMap();

    @Override // com.miui.powerkeeper.thermal.listener.ForegroundGroupListener, com.miui.powerkeeper.thermal.listener.IElement
    public void elementChanged(int i2, Object obj, int i3, int i4) {
        if (i2 != -9) {
            return;
        }
        String str = ((ForegroundInfo) obj).mForegroundPackageName;
        if (this.mAppendForegroundMap.containsKey(str)) {
            modifyCurrentState(this.mAppendForegroundMap.getOrDefault(str, 97).intValue());
        } else {
            onForegroundChanged(str);
        }
    }

    @Override // com.miui.powerkeeper.thermal.listener.ForegroundGroupListener, com.miui.powerkeeper.thermal.listener.ElementListener, com.miui.powerkeeper.cloudcontrol.IUseCloud
    public void updateCloudData() {
        JSONObject jSONObject;
        super.updateCloudData();
        try {
            jSONObject = new JSONObject(SimpleSettings.Misc.getString(AutoRegister.getContext(), "thermal_group", ""));
        } catch (Exception e2) {
            StringWriter stringWriter = new StringWriter();
            e2.printStackTrace(new PrintWriter(stringWriter));
            ScenarioManager.getHistory().log(stringWriter.toString());
            jSONObject = null;
        }
        if (jSONObject == null) {
            return;
        }
        this.mAppendForegroundMap.clear();
        String strOptString = jSONObject.optString("douyin", "");
        if (!strOptString.isEmpty()) {
            for (String str : strOptString.split(",")) {
                this.mAppendForegroundMap.put(str, 18);
            }
        }
        String strOptString2 = jSONObject.optString("update", "");
        if (strOptString2.isEmpty()) {
            return;
        }
        for (String str2 : strOptString2.split(",")) {
            this.mAppendForegroundMap.put(str2, 19);
        }
    }
}
