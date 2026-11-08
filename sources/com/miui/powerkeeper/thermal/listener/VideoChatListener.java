package com.miui.powerkeeper.thermal.listener;

import com.miui.powerkeeper.PowerKeeperManager;
import com.miui.powerkeeper.cloudcontrol.AutoRegister;
import com.miui.powerkeeper.cloudcontrol.CloudFunctionConfig;
import com.miui.powerkeeper.provider.SimpleSettingKeys;
import com.miui.powerkeeper.thermal.resource.DBObserver;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import miui.process.ForegroundInfo;

/* JADX INFO: loaded from: classes.dex */
public class VideoChatListener extends ElementListener {
    private final Set<String> mVideoChats = new HashSet(20);

    @Override // com.miui.powerkeeper.thermal.listener.IElement
    public void elementChanged(int i2, Object obj, int i3, int i4) {
        if (i2 != -9) {
            return;
        }
        modifyCurrentState(this.mVideoChats.contains(((ForegroundInfo) obj).mForegroundPackageName) ? 98 : 99);
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener
    void init() {
        DBObserver.getInstance().registerUser(SimpleSettingKeys.KEY_OPTIMIZE_GAME_NAMES, this);
        PowerKeeperManager.getInstance().getEventsAggregator().registerForForegroundInfo(this.mElementHandler);
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener
    public int initCurrentState() {
        ForegroundInfo foregroundInfo = PowerKeeperManager.getInstance().getEventsAggregator().getForegroundInfo();
        return (foregroundInfo == null || !this.mVideoChats.contains(foregroundInfo.mForegroundPackageName)) ? 99 : 98;
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener, com.miui.powerkeeper.thermal.listener.IListener
    public void uninstall() {
        super.uninstall();
        DBObserver.getInstance().unregisterUser(SimpleSettingKeys.KEY_OPTIMIZE_GAME_NAMES, this);
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener, com.miui.powerkeeper.cloudcontrol.IUseCloud
    public void updateCloudData() {
        this.mVideoChats.addAll(Arrays.asList(CloudFunctionConfig.getFunctionParameters2(AutoRegister.getContext(), SimpleSettingKeys.FUNC_THERMAL_VIDEOCHAT_CONFIG_ENABLE, "").split(",")));
    }
}
