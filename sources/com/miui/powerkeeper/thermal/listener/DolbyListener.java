package com.miui.powerkeeper.thermal.listener;

import android.annotation.Nullable;
import android.database.ContentObserver;
import android.net.Uri;
import android.provider.Settings;
import com.miui.powerkeeper.cloudcontrol.AutoRegister;
import com.miui.powerkeeper.thermal.resource.ScenarioConfig;

/* JADX INFO: loaded from: classes.dex */
public class DolbyListener extends ElementListener {
    private static final int DOLBY_VISION_ON = 1;
    private final ContentObserver mContentObserver = new ContentObserver(this.mElementHandler) { // from class: com.miui.powerkeeper.thermal.listener.DolbyListener.1
        @Override // android.database.ContentObserver
        public void onChange(boolean z2, @Nullable Uri uri) {
            if (uri == null || uri.getLastPathSegment() == null || uri.getLastPathSegment().equals(ScenarioConfig.BRIGHTNESS_THROTTLER_STATUS)) {
                DolbyListener.this.modifyCurrentState(DolbyListener.this.mapToState(Settings.System.getIntForUser(AutoRegister.getContext().getContentResolver(), ScenarioConfig.BRIGHTNESS_THROTTLER_STATUS, 0, -2)));
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public int mapToState(int i2) {
        return i2 == 1 ? 98 : 99;
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener
    void init() {
        AutoRegister.getContext().getContentResolver().registerContentObserver(Settings.System.getUriFor(ScenarioConfig.BRIGHTNESS_THROTTLER_STATUS), false, this.mContentObserver, -1);
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener
    public int initCurrentState() {
        int iMapToState = mapToState(Settings.System.getIntForUser(AutoRegister.getContext().getContentResolver(), ScenarioConfig.BRIGHTNESS_THROTTLER_STATUS, 0, -2));
        this.mCurrentState = iMapToState;
        return iMapToState;
    }

    @Override // com.miui.powerkeeper.thermal.listener.ElementListener, com.miui.powerkeeper.thermal.listener.IListener
    public void uninstall() {
        super.uninstall();
        AutoRegister.getContext().getContentResolver().unregisterContentObserver(this.mContentObserver);
    }

    @Override // com.miui.powerkeeper.thermal.listener.IElement
    public void elementChanged(int i2, Object obj, int i3, int i4) {
    }
}
