package com.miui.powerkeeper.thermal.listener;

import com.miui.powerkeeper.cloudcontrol.AutoRegister;
import com.miui.powerkeeper.provider.SimpleSettingKeys;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.thermal.ScenarioManager;
import java.io.PrintWriter;
import java.io.StringWriter;
import miui.provider.Weather;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class ForegroundSimpleListener extends ForegroundGroupListener {
    @Override // com.miui.powerkeeper.thermal.listener.ForegroundGroupListener, com.miui.powerkeeper.thermal.listener.ElementListener, com.miui.powerkeeper.cloudcontrol.IUseCloud
    public void updateCloudData() {
        JSONObject jSONObject;
        String string = SimpleSettings.Misc.getString(AutoRegister.getContext(), "thermal_group", "");
        String string2 = SimpleSettings.Misc.getString(AutoRegister.getContext(), SimpleSettingKeys.KEY_OPTIMIZE_GAME_NAMES, "");
        try {
            jSONObject = new JSONObject(string);
        } catch (Exception e2) {
            StringWriter stringWriter = new StringWriter();
            e2.printStackTrace(new PrintWriter(stringWriter));
            ScenarioManager.getHistory().log(stringWriter.toString());
            jSONObject = null;
        }
        if (jSONObject == null) {
            return;
        }
        this.mForegroundMap.clear();
        String strOptString = jSONObject.optString("game", "");
        if (!strOptString.isEmpty()) {
            for (String str : strOptString.split(",")) {
                this.mForegroundMap.put(str, 3);
            }
        }
        String strOptString2 = jSONObject.optString("pubg", "");
        if (!strOptString2.isEmpty()) {
            for (String str2 : strOptString2.split(",")) {
                this.mForegroundMap.put(str2, 6);
            }
        }
        String strOptString3 = jSONObject.optString("game2", "");
        if (!strOptString3.isEmpty()) {
            for (String str3 : strOptString3.split(",")) {
                this.mForegroundMap.put(str3, 10);
            }
        }
        String strOptString4 = jSONObject.optString("yuanshen", "");
        if (!strOptString4.isEmpty()) {
            for (String str4 : strOptString4.split(",")) {
                this.mForegroundMap.put(str4, 12);
            }
        }
        String strOptString5 = jSONObject.optString("camera", "");
        if (!strOptString5.isEmpty()) {
            for (String str5 : strOptString5.split(",")) {
                this.mForegroundMap.put(str5, 7);
            }
        }
        String strOptString6 = jSONObject.optString("evaluation", "");
        if (!strOptString6.isEmpty()) {
            for (String str6 : strOptString6.split(",")) {
                this.mForegroundMap.put(str6, 4);
            }
        }
        String strOptString7 = jSONObject.optString("class0", "");
        if (!strOptString7.isEmpty()) {
            for (String str7 : strOptString7.split(",")) {
                this.mForegroundMap.put(str7, 5);
            }
        }
        String strOptString8 = jSONObject.optString("youtube", "");
        if (!strOptString8.isEmpty()) {
            for (String str8 : strOptString8.split(",")) {
                this.mForegroundMap.put(str8, 8);
            }
        }
        String strOptString9 = jSONObject.optString("arvr", "");
        if (!strOptString9.isEmpty()) {
            for (String str9 : strOptString9.split(",")) {
                this.mForegroundMap.put(str9, 9);
            }
        }
        String strOptString10 = jSONObject.optString("navigation", "");
        if (!strOptString10.isEmpty()) {
            for (String str10 : strOptString10.split(",")) {
                this.mForegroundMap.put(str10, 11);
            }
        }
        String strOptString11 = jSONObject.optString("video", "");
        if (!strOptString11.isEmpty()) {
            for (String str11 : strOptString11.split(",")) {
                this.mForegroundMap.put(str11, 13);
            }
        }
        String strOptString12 = jSONObject.optString("huanji", "");
        if (!strOptString12.isEmpty()) {
            for (String str12 : strOptString12.split(",")) {
                this.mForegroundMap.put(str12, 1);
            }
        }
        String strOptString13 = jSONObject.optString("demo", "");
        if (!strOptString13.isEmpty()) {
            for (String str13 : strOptString13.split(",")) {
                this.mForegroundMap.put(str13, 14);
            }
        }
        String strOptString14 = jSONObject.optString(Weather.AlertInfo.ABNORMAL, "");
        if (!strOptString14.isEmpty()) {
            for (String str14 : strOptString14.split(",")) {
                this.mForegroundMap.put(str14, 2);
            }
        }
        for (String str15 : string2.split(",")) {
            this.mForegroundMap.putIfAbsent(str15, 16);
        }
    }
}
