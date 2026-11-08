package com.xiaomi.push.extension;

import android.content.Context;
import com.xiaomi.channel.commonutils.android.AppInfoUtils;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class AppInfoAgentImpl implements AppInfoUtils.AppInfoAgent {
    @Override // com.xiaomi.channel.commonutils.android.AppInfoUtils.AppInfoAgent
    public Map<String, String> divideAppStatus(Context context, String str) {
        return AppRunningUtils.divideAppStatus(context, str);
    }

    @Override // com.xiaomi.channel.commonutils.android.AppInfoUtils.AppInfoAgent
    public boolean isAppRunning(Context context, String str) {
        return AppRunningUtils.isAppRunning(context, str);
    }

    @Override // com.xiaomi.channel.commonutils.android.AppInfoUtils.AppInfoAgent
    public boolean isApplicationForeground(Context context, String str) {
        return AppRunningUtils.isApplicationForeground(context, str);
    }
}
