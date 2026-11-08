package com.miui.powerkeeper.logsystem.config;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.miui.powerkeeper.logsystem.utils.LSUtils;
import com.miui.powerkeeper.provider.SimpleSettings;
import com.miui.powerkeeper.utils.Utils;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class ConfigManager {
    public static final int SENCE_ID_UNSUPPORT = -1;
    public static final String TAG = "LS-ConfigManager";
    private static ConfigManager gInstance;
    private Config mCloudConfig;
    private Context mContext;
    private Config mLocalConfig;
    private Config mUserConfig;

    private ConfigManager(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static synchronized ConfigManager getInstance(Context context) {
        try {
            if (gInstance == null) {
                gInstance = new ConfigManager(context);
            }
        } catch (Throwable th) {
            throw th;
        }
        return gInstance;
    }

    public long getClearIntervalMills() {
        return getConfig().getClearIntervalHour() * 3600000;
    }

    public String getCloudConfigString() {
        return new Gson().toJson(this.mCloudConfig);
    }

    Config getConfig() {
        Config config = this.mCloudConfig;
        return config == null ? this.mLocalConfig : config;
    }

    public String getLocalConfig() {
        return new Gson().toJson(this.mLocalConfig);
    }

    public long getMinIntervelMills() {
        return getConfig().getMinIntervalMin() * 60000;
    }

    public Scene getSceneById(int i2) {
        for (Scene scene : getConfig().getScenes()) {
            if (scene.getId() == i2) {
                return scene;
            }
        }
        return null;
    }

    public Scene getSceneByName(String str) {
        for (Scene scene : getConfig().getScenes()) {
            if (scene.getName().equals(str)) {
                return scene;
            }
        }
        return null;
    }

    public float getThreshold() {
        return getConfig().getThreshold();
    }

    public int[] getValidSceneIds() {
        ArrayList<Scene> scenes = getConfig().getScenes();
        int[] iArr = new int[scenes.size()];
        for (int i2 = 0; i2 < scenes.size(); i2++) {
            iArr[i2] = scenes.get(i2).getId();
        }
        return iArr;
    }

    public void initLocalConfig() throws Throwable {
        String jsonFromAssert = Utils.getJsonFromAssert(this.mContext, "log_system_config.json");
        try {
            this.mLocalConfig = (Config) new Gson().fromJson(jsonFromAssert, new TypeToken<Config>() { // from class: com.miui.powerkeeper.logsystem.config.ConfigManager.1
            }.getType());
        } catch (JsonSyntaxException e2) {
            e2.printStackTrace();
        }
        Log.d(TAG, "mLocalConfig = " + jsonFromAssert);
    }

    public boolean isFeatureEnabled() {
        if (LSUtils.isDeviceSupport(getConfig().getDeviceList())) {
            return getConfig().isFucEnabled();
        }
        return false;
    }

    public boolean isLogTypeSupport(int i2) {
        return ((getConfig().getLogType() != 0 ? 1128202399 & getConfig().getLogType() : 1128202399) & i2) == i2;
    }

    public void updateCloudConfig() {
        String string = SimpleSettings.Misc.getString(this.mContext, "log_system_config");
        try {
            if (!LSUtils.isEmptyString(string)) {
                this.mCloudConfig = (Config) new Gson().fromJson(string, new TypeToken<Config>() { // from class: com.miui.powerkeeper.logsystem.config.ConfigManager.2
                }.getType());
            }
        } catch (JsonSyntaxException e2) {
            e2.printStackTrace();
        }
        Log.d(TAG, "mCloudConfig = " + string);
    }
}
