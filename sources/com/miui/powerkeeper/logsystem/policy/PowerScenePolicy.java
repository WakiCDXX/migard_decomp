package com.miui.powerkeeper.logsystem.policy;

import android.content.Context;
import android.util.Log;
import com.miui.powerkeeper.logsystem.config.ConfigManager;
import com.miui.powerkeeper.logsystem.config.Scene;
import com.miui.powerkeeper.logsystem.utils.LSUtils;

/* JADX INFO: loaded from: classes.dex */
public class PowerScenePolicy implements IWork {
    private static final String TAG = "LS-PowerScenePolicy";
    private Context mContext;
    ForegroundStatusManager mManager;

    PowerScenePolicy(Context context) {
        this.mContext = context;
        this.mManager = new ForegroundStatusManager(this.mContext);
    }

    @Override // com.miui.powerkeeper.logsystem.policy.IWork
    public void destroy() {
        this.mManager.destroy();
    }

    Scene getCurrentScene() {
        String foregroundPkgName = this.mManager.getForegroundPkgName();
        if (LSUtils.isEmptyString(foregroundPkgName)) {
            return null;
        }
        Log.d(TAG, "Current Scene pkgName = " + foregroundPkgName);
        return ConfigManager.getInstance(this.mContext).getSceneByName(foregroundPkgName);
    }

    @Override // com.miui.powerkeeper.logsystem.policy.IWork
    public void startWorking() {
        this.mManager.startWorking();
    }

    @Override // com.miui.powerkeeper.logsystem.policy.IWork
    public void stopWorking() {
        this.mManager.stopWorking();
    }
}
